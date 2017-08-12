package me.kenzierocks.hardvox.region.chunker;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;

import me.kenzierocks.hardvox.region.Region;
import me.kenzierocks.hardvox.vector.VecBridge;

public abstract class BaseRegionChunker<REGION extends Region> implements RegionChunker<REGION> {

    private static final class BRC implements RegionChunk {

        public static BRC fromStream(int x, int z, Stream<Vector3i> vectors) {
            return new BRC(x, z, vectors.collect(toImmutableList()));
        }

        private final int x;
        private final int z;
        private final List<Vector3i> vectors;

        private BRC(int x, int z, Iterable<Vector3i> vectors) {
            this.x = x;
            this.z = z;
            this.vectors = ImmutableList.copyOf(vectors);
        }

        @Override
        public PositionIterator iterator() {
            return new PositionIterator() {

                private Iterator<Vector3i> iterator = vectors.iterator();

                @Override
                public int next(PositionConsumer nextConsumer) {
                    Vector3i next = iterator.next();
                    return nextConsumer.accept(next.getX(), next.getY(), next.getZ());
                }

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }
            };
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getZ() {
            return z;
        }

    }

    private static final class BoundBaseRC<REGION extends Region> implements BoundRegionChunker<REGION> {

        private final REGION region;
        private final RCCreator creator;

        BoundBaseRC(REGION region, RCCreator creator) {
            this.region = region;
            this.creator = creator;
        }

        @Override
        public RegionChunk getChunk(int x, int y, int z) {
            return creator.chunk(x, z);
        }

        @Override
        public REGION getRegion() {
            return region;
        }

    }

    private interface RCCreator {

        RegionChunk chunk(int x, int z);
    }

    private static IntStream chunkAxis(int chunk, int min, int max) {
        return IntStream.range(chunk * 16, (chunk + 1) * 16).filter(i -> min <= i && i <= max);
    }

    private RCCreator getRCCreator(REGION region) {
        int minX = region.getMinimum().getX();
        int maxX = region.getMaximum().getX();
        int minY = region.getMinimum().getY();
        int maxY = region.getMaximum().getY();
        int minZ = region.getMinimum().getZ();
        int maxZ = region.getMaximum().getZ();
        int yDiff = maxY - minY + 1;
        return (cx, cz) -> BRC.fromStream(cx, cz,
                chunkAxis(cx, minX, maxX).mapToObj(x -> {
                    return chunkAxis(cz, minZ, maxZ).mapToObj(z -> {
                        return IntStream.iterate(maxY, y -> y - 1)
                                .limit(yDiff)
                                .mapToObj(y -> {
                                    if (isInWorld(x, y, z) && isInRegion(region, x, y, z)) {
                                        return new Vector3i(x, y, z);
                                    }
                                    return null;
                                });
                    }).flatMap(Function.identity());
                }).flatMap(Function.identity()).filter(Objects::nonNull));
    }

    private boolean isInWorld(int x, int y, int z) {
        return 0 <= y && y <= 255;
    }

    @Override
    public Stream<RegionChunk> getChunks(REGION region) {
        Vector3i min = VecBridge.toChunk(region.getMinimum());
        Vector3i max = VecBridge.toChunk(region.getMaximum());
        RCCreator regionChunk = getRCCreator(region);

        return IntStream.rangeClosed(min.getX(), max.getX())
                .mapToObj(x -> {
                    return IntStream.rangeClosed(min.getZ(), max.getZ())
                            .mapToObj(z -> {
                                return regionChunk.chunk(x, z);
                            });
                }).flatMap(Function.identity());
    }

    @Override
    public BoundRegionChunker<REGION> bind(REGION region) {
        return new BoundBaseRC<>(region, getRCCreator(region));
    }

    /**
     * @implNote The position is guaranteed to be with the bounds of the
     *           region's min and max, so you do not need to check that.
     */
    protected abstract boolean isInRegion(REGION region, int x, int y, int z);

}
