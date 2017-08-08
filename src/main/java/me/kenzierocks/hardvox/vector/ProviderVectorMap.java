package me.kenzierocks.hardvox.vector;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Always returns the same block.
 */
public class ProviderVectorMap<V> implements VectorMap<V> {

    public static <V> ProviderVectorMap<V> single(V singleBlock) {
        return from(() -> singleBlock);
    }

    public static <V> ProviderVectorMap<V> from(Supplier<V> supplier) {
        return new ProviderVectorMap<>(supplier);
    }

    private final Supplier<V> blockData;

    private ProviderVectorMap(Supplier<V> blockData) {
        this.blockData = blockData;
    }

    @Override
    public Optional<V> get(int x, int y, int z) {
        return Optional.of(blockData.get());
    }

}
