package me.kenzierocks.hardvox.region.chunker;

import me.kenzierocks.hardvox.region.BoxRegion;
import me.kenzierocks.hardvox.region.Region;

public final class RegionChunkers {

    public static RegionChunker<BoxRegion> box() {
        return new BoxRegionChunker();
    }

    public static <R extends Region> RegionChunker<R> forRegion(R r) {
        if (r instanceof BoxRegion) {
            @SuppressWarnings("unchecked")
            RegionChunker<R> box = (RegionChunker<R>) box();
            return box;
        }
        throw new IllegalArgumentException("Unknown region class " + r.getClass().getName());
    }

    private RegionChunkers() {
        throw new AssertionError();
    }

}
