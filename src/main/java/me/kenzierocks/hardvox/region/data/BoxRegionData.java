package me.kenzierocks.hardvox.region.data;

import com.flowpowered.math.vector.Vector3i;

public final class BoxRegionData implements RegionData {

    public final Vector3i pos1;
    public final Vector3i pos2;

    public BoxRegionData(Vector3i pos1, Vector3i pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    @Override
    public RegionType getType() {
        return RegionType.BOX;
    }

}
