package me.kenzierocks.hardvox.operation;

import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.region.chunker.RegionChunk.PositionIterator;
import me.kenzierocks.hardvox.vector.MutableVectorMap;
import me.kenzierocks.hardvox.vector.VectorMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * Runs block updates on each set block, as recorded in hitMap.
 */
public class OpBlockUpdates implements Operation {

    @Override
    public String getName() {
        return "Trigger Block Updates";
    }

    @Override
    public int performOperation(PositionIterator chunk, World world, Chunk c, VectorMap<BlockData> blockMap, MutableVectorMap<Boolean> hitMap) {
        return chunk.forEachRemaining((x, y, z) -> {
            if (!hitMap.get(x, y, z).orElse(false)) {
                return 0;
            }

            BlockPos pos = new BlockPos(x, y, z);

            IBlockState state = c.getBlockState(pos);
            world.markAndNotifyBlock(pos, c, state, state, 1);
            return 1;
        });
    }

}
