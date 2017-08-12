package me.kenzierocks.hardvox.region;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

public interface Region {

    /**
     * The point farthest from the center with all negative relative
     * coordinates.
     */
    Vector3i getMinimum();

    /**
     * The point farthest from the center with all positive relative
     * coordinates.
     */
    Vector3i getMaximum();

    /**
     * The point in the very center. May be non-integer.
     */
    Vector3d getCenter();

    /**
     * The number of blocks in the region.
     */
    int getArea();

    /**
     * Width, Height, Length.
     */
    Vector3i getDimensions();

    /**
     * Re-sizes the region to contain the given point.
     */
    void ensureContains(Vector3i vector);

    /**
     * Re-sizes the region to exclude the given point.
     */
    void ensureExcludes(Vector3i vector);

    /**
     * Checks if the region contains the given point.
     */
    boolean contains(Vector3i vector);

    /**
     * Shifts the region by {@code vector}.
     */
    void shift(Vector3i vector);
    
    /**
     * Creates a copy of this region.
     */
    Region copy();

}
