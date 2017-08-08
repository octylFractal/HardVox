package me.kenzierocks.hardvox.vector;

import java.util.Optional;

/**
 * An overlay on top of another map. Unless a specific element has been set or
 * removed in this map, delegates to the other map.
 */
public class OverlayVectorMap<V> implements MutableVectorMap<V> {

    public static <V> OverlayVectorMap<V> create(VectorMap<V> delegate) {
        return new OverlayVectorMap<>(delegate);
    }

    private final OptimizedVectorMap<Optional<V>> overlay = OptimizedVectorMap.create();
    private final VectorMap<V> delegate;

    private OverlayVectorMap(VectorMap<V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<V> get(int x, int y, int z) {
        return overlay.get(x, y, z).orElseGet(() -> delegate.get(x, y, z));
    }

    @Override
    public void put(int x, int y, int z, V data) {
        overlay.put(x, y, z, Optional.of(data));
    }

    @Override
    public void delete(int x, int y, int z) {
        overlay.put(x, y, z, Optional.empty());
    }

}
