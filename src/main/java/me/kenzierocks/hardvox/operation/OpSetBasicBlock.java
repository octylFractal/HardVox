package me.kenzierocks.hardvox.operation;

import java.util.Objects;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.region.Region;
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
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

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
    public int performOperation(Region region, PositionIterator chunk, World world, Chunk c, VectorMap<BlockData> blockMap,
            MutableVectorMap<IBlockState> hitMap) {
        return chunk.forEachRemaining((x, y, z) -> {
            if (hitMap.get(x, y, z).isPresent()) {
                return 0;
            }

            ExtendedBlockStorage ebs = c.getBlockStorageArray()[y >> 4];
            if (ebs == Chunk.NULL_BLOCK_STORAGE) {
                ebs = new ExtendedBlockStorage(y >> 4 << 4, world.provider.hasSkyLight());
                c.getBlockStorageArray()[y >> 4] = ebs;
            }
            int ex = x & 15;
            int ey = y & 15;
            int ez = z & 15;

            BlockData block = blockMap.get(x, y, z).orElse(BlockData.AIR);
            IBlockState oldState = ebs.get(ex, ey, ez);
            if (oldState.equals(block.basicState)) {
                // we don't deal with nbt, return!
                return 0;
            }
            BlockPos pos = new BlockPos(x, y, z);
            if (!isBasicBlock(world, pos, block.basicState)) {
                // not ours either!
                return 0;
            }
            hitMap.put(x, y, z, oldState);

            // set it in the EBS arrays to avoid many unwanted slowdowns
            // we'll handle all of them in another op
            ebs.set(ex, ey, ez, block.basicState);
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
        boolean result = !s.getBlock().hasTileEntity(s) && !s.canProvidePower() && !s.hasComparatorInputOverride()
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
