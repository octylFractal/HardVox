package me.kenzierocks.hardvox.vector.codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.kenzierocks.hardvox.vector.CDSCodec;

public enum BooleanCodec implements CDSCodec<Boolean> {
    INSTANCE;

    @Override
    public Boolean read(DataInputStream stream) throws IOException {
        return stream.readBoolean();
    }

    @Override
    public void save(DataOutputStream stream, Boolean data) throws IOException {
        stream.writeBoolean(data);
    }

}
