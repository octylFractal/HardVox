package me.kenzierocks.hardvox.operation;

import java.util.Objects;

import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.region.chunker.RegionChunk.PositionIterator;
import me.kenzierocks.hardvox.vector.MutableVectorMap;
import me.kenzierocks.hardvox.vector.VectorMap;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class OpSetBasicBlock implements Operation {

    @Override
    public int performOperation(PositionIterator chunk, World world, VectorMap<BlockData> blockMap, MutableVectorMap<Boolean> hitMap) {
        return chunk.forEachRemaining((x, y, z) -> {
            if (hitMap.get(x, y, z).orElse(false)) {
                return 0;
            }

            BlockPos pos = new BlockPos(x, y, z);
            BlockData block = blockMap.get(x, y, z).orElse(BlockData.AIR);
            IBlockState oldState = world.getBlockState(pos);
            if (oldState.equals(block.basicState)) {
                // we don't deal with nbt, return!
                return 0;
            }
            if (!isBasicBlock(world, pos, block.basicState)) {
                // not ours either!
                return 0;
            }
            hitMap.put(x, y, z, Boolean.TRUE);

            // just send it to the client, updates come at the end
            SpeedTricks.LightTrick lightTrick = SpeedTricks.lightTrick(oldState, block.basicState);
            if (!world.setBlockState(pos, lightTrick, 2)) {
                return 0;
            }
            lightTrick.turnOff();
            return 1;
        });
    }

    private boolean isBasicBlock(IBlockAccess w, BlockPos p, IBlockState s) {
        return !s.canProvidePower() && !s.hasComparatorInputOverride()
                && s.getProperties().entrySet().stream().allMatch(e -> allowedProperty(e.getKey()));
    }

    // primarily dis-allows directional properties
    private boolean allowedProperty(IProperty<?> key) {
        Class<?> vc = key.getValueClass();
        if (EnumFacing.class.isAssignableFrom(vc)) {
            return false;
        }
        if (Objects.equals(key.getName(), "facing")) {
            return false;
        }
        if (Objects.equals(key.getName(), "direction")) {
            return false;
        }
        return true;
    }

}
