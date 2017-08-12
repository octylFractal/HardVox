package me.kenzierocks.hardvox.operation;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
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
import me.kenzierocks.hardvox.vector.codec.IBlockStateCodec;
import me.kenzierocks.hardvox.vector.codec.OptimizedVectorMapCodec;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

final class OMTask extends BaseTask<Integer> {

    private static final OptimizedVectorMapCodec<IBlockState> CODEC = new OptimizedVectorMapCodec<>(IBlockStateCodec.INSTANCE);

    private final HVSession session;
    private final List<Operation> operations;
    // relies on the fact that ImmutableSet returns the same iteration order
    private final Set<Vector3i> vectors;
    private final BoundRegionChunker<?> regionChunker;
    private final World world;
    private final VectorMap<BlockData> blockMap;
    private final ChunkDataStore<OptimizedVectorMap<IBlockState>> blockHitChunkData;

    private int opsInStage;
    private int totalOperations;
    private int lastOperations;
    private int operationIndex = 0;
    private Iterator<Vector3i> chunkLocations;
    private OMTask.OMRegionChunkIterator blockLocations;
    private Chunk currentChunk;
    private int opsThisRound = 0;

    public OMTask(HVSession session, List<Operation> operations, Stream<Vector3i> chunkLocations, BoundRegionChunker<?> regionChunker, World world,
            VectorMap<BlockData> blockMap) {
        this.session = session;
        this.operations = ImmutableList.copyOf(operations);
        this.vectors = chunkLocations.collect(toImmutableSet());
        this.regionChunker = regionChunker;
        this.world = world;
        this.blockMap = blockMap;
        this.blockHitChunkData = new ChunkDataStore<>(CODEC);
    }

    @Override
    public void cancel() {
        try {
            blockHitChunkData.close();
        } finally {
            super.cancel();
        }
    }

    @Override
    protected void onTick() throws Exception {
        opsThisRound = 0;
        while (!runOneOp()) {
            // loop until done...
        }
    }

    @Override
    protected void onFirstTick() {
        session.sendMessage(Texts.hardVoxMessage("Starting operation on region of size " + regionChunker.getRegion().getArea() + "."));
    }

    private boolean runOneOp() {
        if (operationIndex >= operations.size()) {
            complete(totalOperations);
            return true;
        }
        Operation o = operations.get(operationIndex);
        o.resetForNextRun();
        if (chunkLocations == null) {
            chunkLocations = vectors.iterator();
        }
        // iterate over all chunks
        int maxOps = blockLocations == null ? HardVoxConfig.operations.operationsPerTick : blockLocations.maxOps;
        while (opsThisRound < maxOps) {
            if (blockLocations == null) {
                maxOps = HardVoxConfig.operations.operationsPerTick;
                if (!chunkLocations.hasNext()) {
                    // loop again to fill the entire tick
                    sendStageMessage(o);
                    o.finish();
                    operationIndex++;
                    chunkLocations = null;
                    return false;
                }
                RegionChunk chunk = regionChunker.getChunk(chunkLocations.next());
                blockLocations = new OMRegionChunkIterator(chunk.getX(), chunk.getZ(), chunk.iterator(), maxOps);
                currentChunk = world.getChunkFromChunkCoords(chunk.getX(), chunk.getZ());
            }
            blockLocations.currentOps = opsThisRound;

            int cx = blockLocations.x;
            int cz = blockLocations.z;

            // pre-load map
            blockHitChunkData.preload(cx, cz);
            Optional<OptimizedVectorMap<IBlockState>> mapOpt = blockHitChunkData.get(cx, cz);
            OptimizedVectorMap<IBlockState> blockHits = mapOpt
                    .orElseGet(OptimizedVectorMap::create);

            int opsFromRound = o.performOperation(regionChunker.getRegion(), blockLocations, world, currentChunk, blockMap, blockHits);
            opsInStage += opsFromRound;
            totalOperations += opsFromRound;
            // drain the remainder of blockLocations
            // in case the op didn't use all of it
            blockLocations.forEachRemaining((x, y, z) -> 0);

            sendOpsMessage();

            if (!mapOpt.isPresent()) {
                // put back map if needed
                blockHitChunkData.put(cx, cz, blockHits);
            }

            // update ops
            opsThisRound = blockLocations.currentOps;

            if (!blockLocations.delegate.hasNext()) {
                blockLocations = null;
            }
        }
        return true;
    }

    private void sendStageMessage(Operation o) {
        if (HardVoxConfig.operations.printStages) {
            int ops = opsInStage;
            opsInStage = 0;
            String stageName = o.getName();
            String progress = (operationIndex + 1) + "/" + operations.size();
            String msg = "Completed stage " + progress + ": " + stageName + " (" + ops + " operation(s)).";
            session.sendMessage(Texts.hardVoxMessage(msg));
        }
    }

    private void sendOpsMessage() {
        int opsPerMessage = HardVoxConfig.operations.operationsPerMessage;
        if (opsPerMessage != 0 && totalOperations - lastOperations > opsPerMessage) {
            // round down
            int totalOpsPM = totalOperations - (totalOperations % opsPerMessage);
            session.sendMessage(Texts.hardVoxMessage(totalOpsPM + " operations(s) performed so far..."));
            lastOperations = totalOpsPM;
        }
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