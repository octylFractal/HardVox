package me.kenzierocks.hardvox.vector.codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.kenzierocks.hardvox.vector.CDSCodec;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public enum IBlockStateCodec implements CDSCodec<IBlockState> {
    INSTANCE;

    @Override
    public IBlockState read(DataInputStream stream) throws IOException {
        return Block.getStateById(stream.readInt());
    }

    @Override
    public void save(DataOutputStream stream, IBlockState data) throws IOException {
        stream.writeInt(Block.getStateId(data));
    }

}
