package me.kenzierocks.hardvox.region.chunker;

import java.util.stream.Stream;

import com.flowpowered.math.vector.Vector3i;

import me.kenzierocks.hardvox.region.Region;

public interface RegionChunker<REGION extends Region> {

    Stream<RegionChunk> getChunks(REGION region);

    BoundRegionChunker<REGION> bind(REGION region);

    interface BoundRegionChunker<REGION extends Region> {

        default RegionChunk getChunk(Vector3i position) {
            return getChunk(position.getX(), position.getY(), position.getZ());
        }

        RegionChunk getChunk(int x, int y, int z);

        REGION getRegion();

    }

}
