package me.kenzierocks.hardvox.vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import me.kenzierocks.hardvox.block.BlockData;
import me.kenzierocks.hardvox.vector.VMShared.Vec;

/**
 * Optimized map for storing vector -&gt; {@link BlockData} mappings
 */
public class OptimizedVectorMap<V> implements MutableVectorMap<V> {

    private static final int DEFAULT_SIZE = 10;

    public static <V> OptimizedVectorMap<V> create() {
        return create(DEFAULT_SIZE);
    }

    public static <V> OptimizedVectorMap<V> create(int size) {
        return new OptimizedVectorMap<>(size);
    }

    private final Map<Vec, V> delegate;

    private OptimizedVectorMap(int initialSize) {
        delegate = new HashMap<>(initialSize);
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

}
