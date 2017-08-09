package me.kenzierocks.hardvox.operation;

import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.region.chunker.RegionChunk.PositionIterator;
import me.kenzierocks.hardvox.vector.MutableVectorMap;
import me.kenzierocks.hardvox.vector.VectorMap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * A phase of a region change. Each operation handles specific block placement
 * actions.
 */
public interface Operation {

    String getName();

    int performOperation(PositionIterator chunk, World world, Chunk c, VectorMap<BlockData> blockMap, MutableVectorMap<Boolean> hitStore);

}
