package me.kenzierocks.hardvox.vector;

import static com.google.common.base.Preconditions.checkState;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.vector.VMShared.Vec;

/**
 * Optimized map for storing vector -&gt; {@link BlockData} mappings
 */
public class OptimizedVectorMap<V> implements MutableVectorMap<V>, SerializableVectorMap<V> {

    private static final int DEFAULT_SIZE = 10;

    public static <V> OptimizedVectorMap<V> create() {
        return create(DEFAULT_SIZE);
    }

    public static <V> OptimizedVectorMap<V> create(int size) {
        return new OptimizedVectorMap<>(size);
    }

    private static final class OptiIterator<V> implements EntryIterator<V> {

        private final Iterator<Map.Entry<Vec, V>> delegate;
        private Vec key;
        private V value;

        OptiIterator(Iterator<Entry<Vec, V>> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public void next() {
            Map.Entry<Vec, V> n = delegate.next();
            key = n.getKey();
            value = n.getValue();
        }

        @Override
        public int getX() {
            checkState(value != null);
            return key.x;
        }

        @Override
        public int getY() {
            checkState(value != null);
            return key.y;
        }

        @Override
        public int getZ() {
            checkState(value != null);
            return key.z;
        }

        @Override
        public V getValue() {
            checkState(value != null);
            return value;
        }

    }

    private final Object2ObjectOpenCustomHashMap<Vec, V> delegate;

    private OptimizedVectorMap(int initialSize) {
        delegate = new Object2ObjectOpenCustomHashMap<>(initialSize, VMShared.VEC_HASH_STRATEGY);
    }

    @Override
    public void put(int x, int y, int z, V data) {
        delegate.put(new Vec(x, y, z), data);
    }

    @Override
    public void delete(int x, int y, int z) {
        delegate.remove(new Vec(x, y, z));
    }

    @Override
    public Optional<V> get(int x, int y, int z) {
        return Optional.ofNullable(delegate.get(new Vec(x, y, z)));
    }

    @Override
    public EntryIterator<V> iterateEntries() {
        return new OptiIterator<>(delegate.entrySet().iterator());
    }

    @Override
    public int getSize() {
        return delegate.size();
    }

    @Override
    public void clear() {
        delegate.clear();
    }

}
