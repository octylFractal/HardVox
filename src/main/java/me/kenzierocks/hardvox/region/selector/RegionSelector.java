package me.kenzierocks.hardvox.region.selector;

import java.util.Optional;

import com.flowpowered.math.vector.Vector3i;

import me.kenzierocks.hardvox.region.Region;
import me.kenzierocks.hardvox.region.data.RegionData;
import net.minecraft.util.EnumFacing;

public interface RegionSelector<REGION extends Region, DATA extends RegionData> {

    String selectPrimary(Vector3i selected);

    String selectSecondary(Vector3i selected);

    String shiftRegion(EnumFacing direction, int amount);

    // also contract region, if amount < 0
    String expandRegion(EnumFacing direction, int amount);

    String expandRegionVertically();

    boolean isRegionDefined();

    Optional<REGION> getRegion();

    DATA getSelectorInformation();

}
