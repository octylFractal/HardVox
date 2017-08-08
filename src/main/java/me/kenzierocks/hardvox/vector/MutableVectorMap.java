package me.kenzierocks.hardvox.vector;

import com.flowpowered.math.vector.Vector3i;

public interface MutableVectorMap<V> extends VectorMap<V> {

    default void put(Vector3i vector, V data) {
        put(vector.getX(), vector.getY(), vector.getZ(), data);
    }

    void put(int x, int y, int z, V data);

    default void delete(Vector3i vector) {
        delete(vector.getX(), vector.getY(), vector.getZ());
    }

    void delete(int x, int y, int z);

}