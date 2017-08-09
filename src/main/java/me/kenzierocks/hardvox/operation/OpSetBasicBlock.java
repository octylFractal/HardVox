package me.kenzierocks.hardvox.operation;

import java.util.Objects;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.region.chunker.RegionChunk.PositionIterator;
import me.kenzierocks.hardvox.vector.MutableVectorMap;
import me.kenzierocks.hardvox.vector.VectorMap;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class OpSetBasicBlock implements Operation {

    // expect a quarter of all blocks
    private static final IntSet basicBlocks = new IntOpenHashSet(Block.REGISTRY.getKeys().size() / 4);
    // expect a tenth of all blocks
    private static final IntSet nonBasicBlocks = new IntOpenHashSet(Block.REGISTRY.getKeys().size() / 10);

    @Override
    public String getName() {
        return "Set Basic Blocks";
    }

    @Override
    public int performOperation(PositionIterator chunk, World world, Chunk c, VectorMap<BlockData> blockMap, MutableVectorMap<Boolean> hitMap) {
        return chunk.forEachRemaining((x, y, z) -> {
            if (hitMap.get(x, y, z).orElse(false)) {
                return 0;
            }

            BlockPos pos = new BlockPos(x, y, z);
            BlockData block = blockMap.get(x, y, z).orElse(BlockData.AIR);
            IBlockState oldState = c.getBlockState(pos);
            if (oldState.equals(block.basicState)) {
                // we don't deal with nbt, return!
                return 0;
            }
            if (!isBasicBlock(world, pos, block.basicState)) {
                // not ours either!
                return 0;
            }
            hitMap.put(x, y, z, Boolean.TRUE);

            if (c.setBlockState(pos, block.basicState) == null) {
                return 0;
            }
            // just send it to the client, updates come at the end
            world.markAndNotifyBlock(pos, c, oldState, block.basicState, 2 | 16);
            return 1;
        });
    }

    private boolean isBasicBlock(IBlockAccess w, BlockPos p, IBlockState s) {
        int stateId = Block.getStateId(s);
        if (basicBlocks.contains(stateId)) {
            return true;
        }
        if (nonBasicBlocks.contains(stateId)) {
            return false;
        }
        boolean result = !s.canProvidePower() && !s.hasComparatorInputOverride()
                && s.getProperties().entrySet().stream().allMatch(e -> allowedProperty(e.getKey()));

        if (result) {
            basicBlocks.add(stateId);
        } else {
            nonBasicBlocks.add(stateId);
        }
        return result;
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
