package me.kenzierocks.hardvox.vector;

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
            return (x * 1301081) ^ (y * 15487309) ^ (z * 746773);
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

    static int hash(int x, int y, int z) {
        return (x * 1301081) ^ (y * 15487309) ^ (z * 746773);
    }
}
