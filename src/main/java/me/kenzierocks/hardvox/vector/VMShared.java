package me.kenzierocks.hardvox.vector;

import java.util.Objects;

import it.unimi.dsi.fastutil.Hash.Strategy;

class VMShared {

    static final class Vec {

        final int x;
        final int y;
        final int z;

        Vec(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public int hashCode() {
            return hash(x, y, z);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Vec)) {
                return false;
            }
            Vec v = (Vec) obj;
            return v.x == x && v.y == y && v.z == z;
        }

    }

    static final Strategy<Vec> VEC_HASH_STRATEGY = new Strategy<VMShared.Vec>() {

        @Override
        public int hashCode(Vec o) {
            return o.hashCode();
        }

        @Override
        public boolean equals(Vec a, Vec b) {
            return Objects.equals(a, b);
        }
    };

    static int hash(int x, int y, int z) {
        return (x * 1301081) ^ (y * 15487309) ^ (z * 746773);
    }
}
