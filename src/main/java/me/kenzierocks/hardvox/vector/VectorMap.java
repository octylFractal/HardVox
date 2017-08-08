package me.kenzierocks.hardvox.vector;

import java.util.Optional;

import com.flowpowered.math.vector.Vector3i;

public interface VectorMap<V> {

    default Optional<V> get(Vector3i vector) {
        return get(vector.getX(), vector.getY(), vector.getZ());
    }

    Optional<V> get(int x, int y, int z);

}
