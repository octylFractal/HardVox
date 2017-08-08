package me.kenzierocks.hardvox.vector.codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.kenzierocks.hardvox.vector.CDSCodec;
import me.kenzierocks.hardvox.vector.OptimizedVectorMap;
import me.kenzierocks.hardvox.vector.SerializableVectorMap.EntryIterator;

public class OptimizedVectorMapCodec<V> implements CDSCodec<OptimizedVectorMap<V>> {

    private final CDSCodec<V> valueCodec;

    public OptimizedVectorMapCodec(CDSCodec<V> valueCodec) {
        this.valueCodec = valueCodec;
    }

    @Override
    public OptimizedVectorMap<V> read(DataInputStream stream) throws IOException {
        int size = stream.readInt();
        OptimizedVectorMap<V> ret = OptimizedVectorMap.create(size);
        for (int i = 0; i < size; i++) {
            int x = stream.readInt();
            int y = stream.readInt();
            int z = stream.readInt();
            V value = valueCodec.read(stream);
            ret.put(x, y, z, value);
        }
        return ret;
    }

    @Override
    public void save(DataOutputStream stream, OptimizedVectorMap<V> data) throws IOException {
        stream.writeInt(data.getSize());
        EntryIterator<V> iter = data.iterateEntries();
        while (iter.hasNext()) {
            iter.next();

            stream.writeInt(iter.getX());
            stream.writeInt(iter.getY());
            stream.writeInt(iter.getZ());
            valueCodec.save(stream, iter.getValue());
        }
    }

}
