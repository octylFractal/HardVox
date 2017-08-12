package me.kenzierocks.hardvox.vector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;

public class ChunkPositionSet {

    public enum Codec implements CDSCodec<ChunkPositionSet> {
        INSTANCE;

        @Override
        public ChunkPositionSet read(DataInputStream stream) throws IOException {
            long[] data = new long[stream.readShort()];
            for (int i = 0; i < data.length; i++) {
                data[i] = stream.readLong();
            }
            return new ChunkPositionSet(BitSet.valueOf(data));
        }

        @Override
        public void save(DataOutputStream stream, ChunkPositionSet data) throws IOException {
            long[] dataA = data.set.toLongArray();
            stream.writeShort(dataA.length);
            for (int i = 0; i < dataA.length; i++) {
                stream.writeLong(dataA[i]);
            }
        }
    }

    public static ChunkPositionSet create() {
        return new ChunkPositionSet();
    }

    private final BitSet set;

    private ChunkPositionSet() {
        this(new BitSet(Masker.CHUNK.size()));
    }

    private ChunkPositionSet(BitSet set) {
        this.set = set;
    }

    public boolean contains(int x, int y, int z) {
        return set.get(Masker.CHUNK.index(x, y, z));
    }

    public void add(int x, int y, int z) {
        set.set(Masker.CHUNK.index(x, y, z));
    }

    public void remove(int x, int y, int z) {
        set.set(Masker.CHUNK.index(x, y, z), false);
    }

}
