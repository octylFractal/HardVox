package me.kenzierocks.hardvox.operation;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.region.Region;
import me.kenzierocks.hardvox.region.chunker.RegionChunk.PositionIterator;
import me.kenzierocks.hardvox.session.HVSession;
import me.kenzierocks.hardvox.session.SessionFlag;
import me.kenzierocks.hardvox.vector.ChunkDataStore;
import me.kenzierocks.hardvox.vector.ChunkPositionSet;
import me.kenzierocks.hardvox.vector.MutableVectorMap;
import me.kenzierocks.hardvox.vector.OptimizedVectorMap;
import me.kenzierocks.hardvox.vector.VectorMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * Runs block updates on each set block, as recorded in hitMap.
 */
public class OpLightUpdates implements Operation {

    private static final int CHUNK_SIZE = 256 * 16 * 16;

    private final HVSession session;
    private final TaskManager taskManager;

    public OpLightUpdates(HVSession session) {
        this.session = session;
        this.taskManager = session.taskManager;
    }

    @Override
    public boolean isEnabled(HVSession session) {
        return session.flags.contains(SessionFlag.LIGHT_UPDATES);
    }

    @Override
    public String getName() {
        return "Trigger Light Updates";
    }

    private Set<ChunkPos> taskedChunks = new HashSet<>();
    private ChunkDataStore<ChunkPositionSet> enlightenedBlocks;
    private MutableVectorMap<Boolean> enlightenedChunks = OptimizedVectorMap.create();

    @Override
    public void resetForNextRun() {
        enlightenedBlocks = new ChunkDataStore<>(ChunkPositionSet.Codec.INSTANCE);
    }

    @Override
    public void finish() {
        if (enlightenedBlocks != null) {
            enlightenedBlocks.close();
        }
        enlightenedChunks.clear();
        taskedChunks.clear();
    }

    @Override
    public int performOperation(Region region, PositionIterator chunk, World world, Chunk c, VectorMap<BlockData> blockMap,
            MutableVectorMap<IBlockState> hitMap) {
        if (taskedChunks.add(c.getPos())) {
            taskManager.submit(new ChunkRelightTask(session, world, c.getPos()));
            return 1;
        }
        return 0;
        // return immediateLightCheck(region, chunk, world, c, hitMap);
    }

    private int immediateLightCheck(Region region, PositionIterator chunk, World world, Chunk c, MutableVectorMap<IBlockState> hitMap) {
        int yMax = region.getMaximum().getY();
        // reset precipitation map
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                BlockPos pos = new BlockPos((c.x << 4) + i, yMax, (c.z << 4) + j);
                c.setBlockState(pos, c.getBlockState(pos));
            }
        }
        // consume iterator
        int ops = chunk.forEachRemaining((x, y, z) -> {
            if (!hitMap.get(x, y, z).isPresent()) {
                return 0;
            }
            int opsIn = 0;
            for (int i = -16; i <= 16; i++) {
                for (int j = -16; j <= 16; j++) {
                    for (int k = -16; k <= 16; k++) {
                        opsIn += enlightenBlocks(x + i, y + j, z + k, world) ? 1 : 0;
                    }
                }
            }
            return opsIn;
        });

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                ops += relightSky(region, c, i, j) ? CHUNK_SIZE : 0;
            }
        }

        return CHUNK_SIZE + ops;
    }

    private boolean enlightenBlocks(int i, int j, int k, World world) {
        enlightenedBlocks.preload(i >> 4, k >> 4);
        Optional<ChunkPositionSet> chunkMapOpt = enlightenedBlocks.get(i >> 4, k >> 4);
        if (chunkMapOpt.map(m -> m.contains(i & 15, j, k & 15)).orElse(false)) {
            return false;
        }
        ChunkPositionSet chunkMap = chunkMapOpt.orElseGet(ChunkPositionSet::create);
        if (!chunkMapOpt.isPresent()) {
            enlightenedBlocks.put(i >> 4, k >> 4, chunkMap);
        }
        chunkMap.add(i & 15, j, k & 15);
        world.checkLightFor(EnumSkyBlock.BLOCK, new BlockPos(i, j, k));
        return true;
    }

    private boolean relightSky(Region region, Chunk c, int i, int j) {
        int cx = c.x + i;
        int cz = c.z + j;
        if (enlightenedChunks.get(cx, 0, cz).isPresent()) {
            return false;
        }
        enlightenedChunks.put(cx, 0, cz, true);
        Chunk cc = c.getWorld().getChunkFromChunkCoords(cx, cz);
        for (int bx = 0; bx < 1; bx++) {
            for (int bz = 0; bz < 1; bz++) {
                // call it for the top block, the method does the check for all
                // blocks below
                cc.relightBlock(bx * 16, 255, bz * 16);
            }
        }
        return true;
    }

}
