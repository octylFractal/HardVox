package me.kenzierocks.hardvox.vector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface CDSCodec<D> {

    D read(DataInputStream stream) throws IOException;

    void save(DataOutputStream stream, D data) throws IOException;

}
