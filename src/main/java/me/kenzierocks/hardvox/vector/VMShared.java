package me.kenzierocks.hardvox.vector;

class VMShared {

    static final class Vec {

        final int keyX;
        final int keyY;
        final int keyZ;

        Vec(int keyX, int keyY, int keyZ) {
            this.keyX = keyX;
            this.keyY = keyY;
            this.keyZ = keyZ;
        }

        @Override
        public int hashCode() {
            return (keyX * 1301081) ^ (keyY * 15487309) ^ (keyZ * 746773);
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
            return v.keyX == keyX && v.keyY == keyY && v.keyZ == keyZ;
        }

    }

    static int hash(int x, int y, int z) {
        return (x * 1301081) ^ (y * 15487309) ^ (z * 746773);
    }
}
