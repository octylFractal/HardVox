package me.kenzierocks.hardvox.block;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

public class BlockData {
    
    public static final BlockData AIR = new BlockData(Blocks.AIR.getDefaultState(), null);
    
    public static BlockData get(IBlockState basicState) {
        return get(basicState, null);
    }
    
    public static BlockData get(IBlockState basicState, @Nullable NBTTagCompound nbt) {
        if (basicState.getBlock() == Blocks.AIR) {
            return AIR;
        }
        return new BlockData(basicState, nbt);
    }

    /**
     * The state for the block data.
     */
    public final IBlockState basicState;
    /**
     * NBT data, if present.
     */
    @Nullable
    public final NBTTagCompound nbt;

    private BlockData(IBlockState basicState, @Nullable NBTTagCompound nbt) {
        this.basicState = basicState;
        this.nbt = nbt;
    }
    
    public boolean isTileEntity() {
        return nbt != null;
    }

}
