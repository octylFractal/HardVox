package me.kenzierocks.hardvox.vector;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class VecBridge {

    public static Vector3i toChunk(Vector3i v) {
        // NOT EQUAL TO DIVIDING BY 16!
        return new Vector3i(v.getX() >> 4, v.getY() >> 4, v.getZ() >> 4);
    }

    public static Vector3d toFlow(Vec3d mc) {
        return new Vector3d(mc.x, mc.y, mc.z);
    }

    public static Vector3i toFlow(Vec3i mc) {
        return new Vector3i(mc.getX(), mc.getY(), mc.getZ());
    }

    public static EnumFacing facingFromLook(Entity e) {
        // i know it looks backwards just trust me
        if (e.rotationPitch >= 45) {
            // looking down
            return EnumFacing.DOWN;
        } else if (e.rotationPitch <= -45) {
            // looking up
            return EnumFacing.UP;
        }
        return e.getHorizontalFacing();
    }

    /**
     * Find out which vector is more towards the given direction relative to the
     * other.
     */
    public static Vector3i vecForDirection(EnumFacing direction, Vector3i pos1, Vector3i pos2) {
        int pos1ToPos2 = pos1.distanceSquared(pos2);
        if (pos1ToPos2 == 0) {
            // same vector!
            return pos1;
        }
        int moved1ToPos2 = pos1.add(toFlow(direction.getDirectionVec())).distanceSquared(pos2);
        if (pos1ToPos2 >= moved1ToPos2) {
            // it moved closer to pos2, therefore pos2 is more
            return pos2;
        } else {
            // it moved away from pos2, therefore pos1 is more
            return pos1;
        }
    }

    public static int getAxisValue(EnumFacing.Axis axis, Vector3i vector) {
        switch (axis) {
            case X:
                return vector.getX();
            case Y:
                return vector.getY();
            case Z:
                return vector.getZ();
            default:
                throw new AssertionError("the hell is the " + axis + " axis? I exist in the 3d plane alone!");
        }
    }

}
