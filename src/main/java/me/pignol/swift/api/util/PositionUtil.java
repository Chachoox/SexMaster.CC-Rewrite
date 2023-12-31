package me.pignol.swift.api.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class PositionUtil {

    public static Vec3d getEyesPos(Entity entity) {
        return new Vec3d(entity.posX, getEyeHeight(entity), entity.posZ);
    }

    public static double getEyeHeight(Entity entity) {
        return entity.posY + entity.getEyeHeight();
    }

}
