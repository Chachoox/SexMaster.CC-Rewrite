package me.pignol.swift.api.util;

import me.pignol.swift.api.interfaces.Globals;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class InterpolationUtil implements Globals {

    private static final MutableAABB MUTABLE_AABB = new MutableAABB(0, 0, 0, 0, 0, 0);

    public static Vec3d interpolatedEyePos() {
        return mc.player.getPositionEyes(mc.getRenderPartialTicks());
    }

    public static Vec3d interpolatedEyeVec() {
        return mc.player.getLook(mc.getRenderPartialTicks());
    }

    public static MutableAABB interpolatePos(BlockPos pos, float height) {
        return MUTABLE_AABB.setAABB(pos.getX() - mc.getRenderManager().viewerPosX, pos.getY() - mc.getRenderManager().viewerPosY, pos.getZ() - mc.getRenderManager().viewerPosZ, pos.getX() - mc.getRenderManager().viewerPosX + 1, pos.getY() - mc.getRenderManager().viewerPosY + height, pos.getZ() - mc.getRenderManager().viewerPosZ + 1);
    }

    public static MutableAABB interpolateAxis(AxisAlignedBB bb) {
        return MUTABLE_AABB.setAABB(bb.minX - mc.getRenderManager().viewerPosX, bb.minY - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ, bb.maxX - mc.getRenderManager().viewerPosX, bb.maxY - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ);
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time,
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time,
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time
        );
    }

}
