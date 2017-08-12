package me.kenzierocks.hardvox.operation;

import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.region.Region;
import me.kenzierocks.hardvox.region.chunker.RegionChunk.PositionIterator;
import me.kenzierocks.hardvox.session.HVSession;
import me.kenzierocks.hardvox.vector.MutableVectorMap;
import me.kenzierocks.hardvox.vector.VectorMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * A phase of a region change. Each operation handles specific block placement
 * actions.
 */
public interface Operation {

    static boolean isHit(int x, int y, int z, MutableVectorMap<IBlockState> hitMap) {
        return hitMap.get(x, y, z).isPresent();
    }
    
    default boolean isEnabled(HVSession session) {
        return true;
    }

    default void resetForNextRun() {
    }

    default void finish() {
    }

    String getName();

    int performOperation(Region region, PositionIterator chunk, World world, Chunk c, VectorMap<BlockData> blockMap, MutableVectorMap<IBlockState> hitStore);

}
