package me.kenzierocks.hardvox.operation;

import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.region.chunker.RegionChunk.PositionIterator;
import me.kenzierocks.hardvox.vector.MutableVectorMap;
import net.minecraft.world.World;

/**
 * A phase of a region change. Each operation handles specific block placement
 * actions.
 */
public interface Operation {

    int performOperation(PositionIterator chunk, World world, MutableVectorMap<BlockData> blockMap);

}
