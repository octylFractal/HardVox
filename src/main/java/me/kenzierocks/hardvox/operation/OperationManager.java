package me.kenzierocks.hardvox.operation;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;

import me.kenzierocks.hardvox.HardVoxConfig;
import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.region.chunker.RegionChunk;
import me.kenzierocks.hardvox.region.chunker.RegionChunk.PositionConsumer;
import me.kenzierocks.hardvox.region.chunker.RegionChunk.PositionIterator;
import me.kenzierocks.hardvox.region.chunker.RegionChunker.BoundRegionChunker;
import me.kenzierocks.hardvox.vector.MutableVectorMap;
import me.kenzierocks.hardvox.vector.OverlayVectorMap;
import me.kenzierocks.hardvox.vector.VectorMap;
import net.minecraft.world.World;

public class OperationManager {

    private final List<Operation> operations;

    private OMTask runningOp = null;

    public OperationManager() {
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
            runningOp.tick();
            if (runningOp.done) {
                runningOp = null;
            }
        }
    }

    private static final class OMTask {

        private final OperationManager manager;
        private final CompletableFuture<Integer> future = new CompletableFuture<>();
        // relies on the fact that ImmutableSet returns the same iteration order
        private final Set<Vector3i> vectors;
        private final BoundRegionChunker regionChunker;
        private final World world;
        private final MutableVectorMap<BlockData> blockMap;

        private final int savedMaxOps = HardVoxConfig.operations.operationsPerTick;

        private int totalOperations;
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
            this.blockMap = OverlayVectorMap.create(blockMap);
        }

        public void tick() {
            opsThisRound = 0;
            try {
                while (!runOneOp()) {
                    // loop until done...
                }
            } catch (Exception e) {
                future.completeExceptionally(e);
                done = true;
                return;
            }
            if (done) {
                future.complete(totalOperations);
            }
        }

        private boolean runOneOp() {
            if (operationIndex >= manager.operations.size()) {
                done = true;
                return true;
            }
            Operation o = manager.operations.get(operationIndex);
            if (chunkLocations == null) {
                chunkLocations = vectors.iterator();
            }
            // iterate over all chunks
            while (opsThisRound < savedMaxOps) {
                if (blockLocations == null) {
                    if (!chunkLocations.hasNext()) {
                        // loop again to fill the entire tick
                        operationIndex++;
                        chunkLocations = null;
                        return false;
                    }
                    RegionChunk chunk = regionChunker.getChunk(chunkLocations.next());
                    blockLocations = new OMRegionChunkIterator(chunk.iterator(), savedMaxOps);
                }
                blockLocations.currentOps = opsThisRound;

                // should drain blockLocations
                totalOperations += o.performOperation(blockLocations, world, blockMap);

                // update ops
                opsThisRound = blockLocations.currentOps;

                if (!blockLocations.delegate.hasNext()) {
                    blockLocations = null;
                }
            }
            return true;
        }

        private static final class OMRegionChunkIterator implements PositionIterator {

            private final PositionIterator delegate;
            private final int maxOps;
            private int currentOps;

            public OMRegionChunkIterator(PositionIterator delegate, int maxOps) {
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
