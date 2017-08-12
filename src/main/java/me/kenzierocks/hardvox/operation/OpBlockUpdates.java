package me.kenzierocks.hardvox.operation;

import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.region.Region;
import me.kenzierocks.hardvox.region.chunker.RegionChunk.PositionIterator;
import me.kenzierocks.hardvox.session.HVSession;
import me.kenzierocks.hardvox.session.SessionFlag;
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
    public boolean isEnabled(HVSession session) {
        return session.flags.contains(SessionFlag.BLOCK_UPDATES);
    }

    @Override
    public String getName() {
        return "Trigger Block Updates";
    }

    @Override
    public int performOperation(Region region, PositionIterator chunk, World world, Chunk c, VectorMap<BlockData> blockMap,
            MutableVectorMap<IBlockState> hitMap) {
        return chunk.forEachRemaining((x, y, z) -> {
            if (!Operation.isHit(x, y, z, hitMap)) {
                return 0;
            }

            BlockPos pos = new BlockPos(x, y, z);

            IBlockState state = c.getBlockState(pos);
            world.markAndNotifyBlock(pos, c, state, state, 1);
            return 1;
        });
    }

}
