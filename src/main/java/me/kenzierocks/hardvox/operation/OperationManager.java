package me.kenzierocks.hardvox.operation;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;

import me.kenzierocks.hardvox.HardVoxConfig;
import me.kenzierocks.hardvox.Texts;
import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.region.chunker.RegionChunk;
import me.kenzierocks.hardvox.region.chunker.RegionChunk.PositionConsumer;
import me.kenzierocks.hardvox.region.chunker.RegionChunk.PositionIterator;
import me.kenzierocks.hardvox.region.chunker.RegionChunker.BoundRegionChunker;
import me.kenzierocks.hardvox.session.HVSession;
import me.kenzierocks.hardvox.vector.ChunkDataStore;
import me.kenzierocks.hardvox.vector.OptimizedVectorMap;
import me.kenzierocks.hardvox.vector.VectorMap;
import me.kenzierocks.hardvox.vector.codec.BooleanCodec;
import me.kenzierocks.hardvox.vector.codec.OptimizedVectorMapCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OperationManager {

    private static final OptimizedVectorMapCodec<Boolean> CODEC = new OptimizedVectorMapCodec<>(BooleanCodec.INSTANCE);

    private final HVSession parent;
    private final List<Operation> operations;

    private OMTask runningOp = null;

    public OperationManager(HVSession parent) {
        this.parent = parent;
        ImmutableList.Builder<Operation> ops = ImmutableList.builder();

        ops.add(new OpSetBasicBlock());

        operations = ops.build();
    }

    /*
     * TODO Optimize using chunk-dependencies
     * 
     * We know that each operation operates on a single chunk at a time. We also
     * know that each operation only depends on the nearby chunks. There should
     * be a way to optimize the operations to run on the closest chunks FIRST.
     * Instead of iterating over all chunks for every operation.
     * 
     * Imagine you have C1|C2|C3|C4, instead of running operations O1,O2,O3 like
     * this:
     * 
     * <pre> O1{C1,C2,C3,C4},O2{C1,C2,C3,C4},O3{C1,C2,C3,C4},O4{C1,C2,C3,C4}
     * </pre>
     * 
     * We can do it like this:
     * 
     * <pre>O1{C1,C2},O2{C1},O1{C3},O2{C2},O3{C1}...</pre>
     * 
     * Notice how we operate on the closest chunks first, ensuring that all
     * blocks that could touch are already placed.
     */

    public CompletableFuture<Integer> performOperations(Stream<Vector3i> chunkLocations, BoundRegionChunker regionChunker, World world,
            VectorMap<BlockData> blockMap) {
        if (isRunningOperation()) {
            throw new IllegalStateException("Already working!");
        }
        runningOp = new OMTask(this, chunkLocations, regionChunker, world, blockMap);
        return runningOp.future;
    }

    public boolean isRunningOperation() {
        return runningOp != null;
    }

    /**
     * Runs the current tasks in this manager. Must be called on the MC main
     * thread.
     */
    public void runTasks() {
        if (runningOp != null) {
            if (runningOp.done) {
                runningOp = null;
            } else {
                runningOp.tick();
            }
        }
    }

    public void cancelTasks() {
        if (runningOp != null) {
            runningOp.stop();
            runningOp = null;
        }
    }

    private static final class OMTask {

        private final OperationManager manager;
        private final CompletableFuture<Integer> future = new CompletableFuture<>();
        // relies on the fact that ImmutableSet returns the same iteration order
        private final Set<Vector3i> vectors;
        private final BoundRegionChunker regionChunker;
        private final World world;
        private final VectorMap<BlockData> blockMap;
        private final ChunkDataStore<OptimizedVectorMap<Boolean>> blockHitChunkData;

        private int totalOperations;
        private int lastOperations;
        private int operationIndex = 0;
        private Iterator<Vector3i> chunkLocations;
        private OMRegionChunkIterator blockLocations;
        private int opsThisRound = 0;

        private boolean done;

        public OMTask(OperationManager manager, Stream<Vector3i> chunkLocations, BoundRegionChunker regionChunker, World world, VectorMap<BlockData> blockMap) {
            this.manager = manager;
            this.vectors = chunkLocations.collect(toImmutableSet());
            this.regionChunker = regionChunker;
            this.world = world;
            this.blockMap = blockMap;
            this.blockHitChunkData = new ChunkDataStore<>(CODEC);
        }

        void stop() {
            done = true;
            blockHitChunkData.close();
        }

        public void tick() {
            opsThisRound = 0;
            try {
                while (!runOneOp()) {
                    // loop until done...
                }
            } catch (Exception e) {
                future.completeExceptionally(e);
                stop();
                return;
            }
            if (done) {
                future.complete(totalOperations);
            }
        }

        private boolean runOneOp() {
            if (operationIndex >= manager.operations.size()) {
                stop();
                return true;
            }
            Operation o = manager.operations.get(operationIndex);
            if (chunkLocations == null) {
                chunkLocations = vectors.iterator();
            }
            // iterate over all chunks
            int maxOps = HardVoxConfig.operations.operationsPerTick;
            while (opsThisRound < maxOps) {
                if (blockLocations == null) {
                    if (!chunkLocations.hasNext()) {
                        // loop again to fill the entire tick
                        operationIndex++;
                        chunkLocations = null;
                        return false;
                    }
                    RegionChunk chunk = regionChunker.getChunk(chunkLocations.next());
                    blockLocations = new OMRegionChunkIterator(chunk.getX(), chunk.getZ(), chunk.iterator(), maxOps);
                }
                blockLocations.currentOps = opsThisRound;

                // pre-load chunk
                BlockPos pos = new BlockPos(blockLocations.x, 0, blockLocations.z);
                world.getChunkFromBlockCoords(pos);

                // pre-load map
                blockHitChunkData.preload(pos.getX(), pos.getZ());
                Optional<OptimizedVectorMap<Boolean>> mapOpt = blockHitChunkData.get(pos.getX(), pos.getZ());
                OptimizedVectorMap<Boolean> blockHits = mapOpt
                        .orElseGet(OptimizedVectorMap::create);

                // should drain blockLocations
                totalOperations += o.performOperation(blockLocations, world, blockMap, blockHits);

                int opsPerMessage = HardVoxConfig.operations.operationsPerMessage;
                if (opsPerMessage != 0 && totalOperations - lastOperations > opsPerMessage) {
                    // round down
                    int totalOpsPM = totalOperations - (totalOperations % opsPerMessage);
                    manager.parent.owner.sendMessage(Texts.hardVoxMessage(totalOpsPM + " operations(s) performed so far..."));
                    lastOperations = totalOpsPM;
                }

                if (!mapOpt.isPresent()) {
                    // put back map if needed
                    blockHitChunkData.put(pos.getX(), pos.getZ(), blockHits);
                }

                // update ops
                opsThisRound = blockLocations.currentOps;

                if (!blockLocations.delegate.hasNext()) {
                    blockLocations = null;
                }
            }
            return true;
        }

        private static final class OMRegionChunkIterator implements PositionIterator {

            private final int x;
            private final int z;
            private final PositionIterator delegate;
            private final int maxOps;
            private int currentOps;

            public OMRegionChunkIterator(int x, int z, PositionIterator delegate, int maxOps) {
                this.x = x;
                this.z = z;
                this.delegate = delegate;
                this.maxOps = maxOps;
            }

            @Override
            public boolean hasNext() {
                return currentOps < maxOps && delegate.hasNext();
            }

            @Override
            public int next(PositionConsumer nextConsumer) {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                currentOps++;
                return delegate.next(nextConsumer);
            }

        }
    }

}
