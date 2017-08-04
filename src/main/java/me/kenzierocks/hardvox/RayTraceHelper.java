package me.kenzierocks.hardvox;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class RayTraceHelper {

    public static RayTraceResult rayTraceEntity(Entity e, double blockReachDistance) {
        Vec3d eyes = e.getPositionEyes(1);
        Vec3d look = e.getLook(1);
        Vec3d end = eyes.addVector(look.x * blockReachDistance, look.y * blockReachDistance, look.z * blockReachDistance);
        return e.world.rayTraceBlocks(eyes, end, false, false, true);
    }
}
