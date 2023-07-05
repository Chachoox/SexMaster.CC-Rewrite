package me.pignol.swift.api.util;

import me.pignol.swift.api.interfaces.Globals;
import me.pignol.swift.api.mixins.AccessorBlock;
import me.pignol.swift.client.managers.FriendManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

import java.util.List;

public class EntityUtil implements Globals {

    private static final DamageSource DAMAGE_SOURCE = DamageSource.causeExplosionDamage(new Explosion(mc.world, mc.player, 0, 0, 0, 6.0F, false, true));

    public static EntityPlayer getClosestPlayer(double range) {
        return getClosestPlayer(range, false, mc.world.playerEntities);
    }

    public static EntityPlayer getClosestPlayer(double range, boolean unsafe) {
        return getClosestPlayer(range, unsafe, mc.world.playerEntities);
    }

    public static EntityPlayer getClosestPlayer(double x, double y, double z, double range, boolean unsafe, List<EntityPlayer> players) {
        double maxDistance = 999.0D;
        EntityPlayer target = null;
        range *= range;
        for (EntityPlayer player : players) {
            if (player != mc.player) {
                final double distance = player.getDistanceSq(x, y, z);
                if (range > distance && !isDead(player) && !FriendManager.getInstance().isFriend(player.getName())) {
                    if (unsafe && BlockUtil.isSafe(player, 0, false)) {
                        continue;
                    }
                    if (distance < maxDistance) {
                        maxDistance = distance;
                        target = player;
                    }
                }
            }
        }

        if (unsafe && target == null) {
            return getClosestPlayer(range, false);
        }
        return target;
    }

    public static EntityPlayer getClosestPlayer(double range, boolean unsafe, List<EntityPlayer> players) {
        double maxDistance = 999.0D;
        EntityPlayer target = null;
        range *= range;
        for (EntityPlayer player : players) {
            if (player != mc.player) {
                final double distance = player.getDistanceSq(mc.player);
                if (range > distance && !isDead(player) && !FriendManager.getInstance().isFriend(player.getName())) {
                    if (unsafe && BlockUtil.isSafe(player, 0, false)) {
                        continue;
                    }
                    if (distance < maxDistance) {
                        maxDistance = distance;
                        target = player;
                    }
                }
            }
        }

        if (unsafe && target == null) {
            return getClosestPlayer(range, false);
        }
        return target;
    }

    public static boolean isDead(Entity entity) {
        return entity.isDead || entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getHealth() <= 0.0F;
    }

    public static int getPing(EntityPlayer player) {
        if (mc.player.connection != null) {
            final NetworkPlayerInfo info = mc.player.connection.getPlayerInfo(player.getUniqueID());
            if (info != null) { //dont listen to intellij this can actually be null
                return info.getResponseTime();
            }
        }
        return -1;
    }

    public static boolean isPlayerValid(EntityPlayer player, double range) {
        return player != null && player != mc.player && mc.player.getDistanceSq(player) < range * range && !isDead(player) && !FriendManager.getInstance().isFriend(player.getName());
    }

    public static float getHealth(EntityLivingBase entity) {
        return entity.getHealth() + entity.getAbsorptionAmount();
    }

    public static float calculate(double x, double y, double z, EntityLivingBase base) {
        return calculate(x, y, z, base, false);
    }

    public static float calculate(double x, double y, double z, EntityLivingBase base, boolean ignoreTerrain) {
        double distance = base.getDistance(x, y, z) / 12.0D;
        if (distance > 1.0D) {
            return 0.0F;
        } else {
            final double density = getBlockDensity(new Vec3d(x, y, z), base.getEntityBoundingBox(), ignoreTerrain);
            final double densityDistance = distance = (1.0D - distance) * density;
            float damage = CombatRules.getDamageAfterAbsorb(
                    getDifficultyMultiplier((float) ((densityDistance * densityDistance + distance) / 2.0D * 7.0D * 12.0D + 1.0D)),
                    base.getTotalArmorValue(),
                    (float) base.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            final int modifierDamage = EnchantmentHelper.getEnchantmentModifierDamage(base.getArmorInventoryList(), DAMAGE_SOURCE);
            if (modifierDamage > 0) {
                damage = CombatRules.getDamageAfterMagicAbsorb(damage, modifierDamage);
            }

            final PotionEffect resistance = base.getActivePotionEffect(MobEffects.RESISTANCE);
            if (resistance != null) {
                damage = damage * (25 - (resistance.getAmplifier() + 1) * 5) / 25.0F;
            }

            if (damage <= 0)
                return 0.0F;

            return damage;
        }
    }

    public static float getBlockDensity(Vec3d vec, AxisAlignedBB bb, boolean ignoreTerrain) {
        double d0 = 1.0D / ((bb.maxX - bb.minX) * 2.0D + 1.0D);
        double d1 = 1.0D / ((bb.maxY - bb.minY) * 2.0D + 1.0D);
        double d2 = 1.0D / ((bb.maxZ - bb.minZ) * 2.0D + 1.0D);
        double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;

        if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D) {
            int j2 = 0;
            int k2 = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float) ((double) f + d0)) {
                for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) ((double) f1 + d1)) {
                    for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) ((double) f2 + d2)) {
                        double d5 = bb.minX + (bb.maxX - bb.minX) * (double) f;
                        double d6 = bb.minY + (bb.maxY - bb.minY) * (double) f1;
                        double d7 = bb.minZ + (bb.maxZ - bb.minZ) * (double) f2;

                        if (rayTraceBlocks(new Vec3d(d5 + d3, d6, d7 + d4), vec, false, ignoreTerrain) == null) {
                            ++j2;
                        }

                        ++k2;
                    }
                }
            }

            return (float) j2 / (float) k2;
        } else {
            return 0.0F;
        }
    }

    public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean ignoreBlockWithoutBoundingBox, boolean ignoreTerrain) {
        int startPosX = MathHelper.floor(vec32.x);
        int startPosY = MathHelper.floor(vec32.y);
        int startPosZ = MathHelper.floor(vec32.z);
        int endPosX = MathHelper.floor(vec31.x);
        int endPosY = MathHelper.floor(vec31.y);
        int endPosZ = MathHelper.floor(vec31.z);
        BlockPos.MutableBlockPos blockpos = new BlockPos.MutableBlockPos(endPosX, endPosY, endPosZ);
        IBlockState iblockstate = mc.world.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(mc.world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, false)) {
            if (!ignoreTerrain || ((AccessorBlock) block).getBlockResistance() > 599) {
                return iblockstate.collisionRayTrace(mc.world, blockpos, vec31, vec32);
            }
        }

        int iterations = 200;

        while (iterations-- >= 0) {
            if (endPosX == startPosX && endPosY == startPosY && endPosZ == startPosZ) {
                return null;
            }

            boolean flag2 = true;
            boolean flag = true;
            boolean flag1 = true;
            double d0 = 999.0D;
            double d1 = 999.0D;
            double d2 = 999.0D;

            if (startPosX > endPosX) {
                d0 = (double) endPosX + 1.0D;
            } else if (startPosX < endPosX) {
                d0 = (double) endPosX + 0.0D;
            } else {
                flag2 = false;
            }

            if (startPosY > endPosY) {
                d1 = (double) endPosY + 1.0D;
            } else if (startPosY < endPosY) {
                d1 = (double) endPosY + 0.0D;
            } else {
                flag = false;
            }

            if (startPosZ > endPosZ) {
                d2 = (double) endPosZ + 1.0D;
            } else if (startPosZ < endPosZ) {
                d2 = (double) endPosZ + 0.0D;
            } else {
                flag1 = false;
            }

            double d3 = 999.0D;
            double d4 = 999.0D;
            double d5 = 999.0D;
            double d6 = vec32.x - vec31.x;
            double d7 = vec32.y - vec31.y;
            double d8 = vec32.z - vec31.z;

            if (flag2) {
                d3 = (d0 - vec31.x) / d6;
            }

            if (flag) {
                d4 = (d1 - vec31.y) / d7;
            }

            if (flag1) {
                d5 = (d2 - vec31.z) / d8;
            }

            if (d3 == -0.0D) {
                d3 = -1.0E-4D;
            }

            if (d4 == -0.0D) {
                d4 = -1.0E-4D;
            }

            if (d5 == -0.0D) {
                d5 = -1.0E-4D;
            }

            EnumFacing enumfacing;

            if (d3 < d4 && d3 < d5) {
                enumfacing = startPosX > endPosX ? EnumFacing.WEST : EnumFacing.EAST;
                //vec31.setPos(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
                vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
            } else if (d4 < d5) {
                enumfacing = startPosY > endPosY ? EnumFacing.DOWN : EnumFacing.UP;
                // vec31.setPos(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
                vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);

            } else {
                enumfacing = startPosZ > endPosZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
                vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
            }

            endPosX = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
            endPosY = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
            endPosZ = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
            blockpos.setPos(endPosX, endPosY, endPosZ);
            final IBlockState iblockstate1 = mc.world.getBlockState(blockpos);
            final Block block1 = iblockstate1.getBlock();

            if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(mc.world, blockpos) != Block.NULL_AABB) {
                if (block1.canCollideCheck(iblockstate1, false) && (!ignoreTerrain || ((AccessorBlock) block).getBlockResistance() > 599)) {
                    return iblockstate1.collisionRayTrace(mc.world, blockpos, vec31, vec32);
                }

            }
        }
        return null;
    }

    public static float getDifficultyMultiplier(float distance) {
        switch (mc.world.getDifficulty()) {
            case PEACEFUL:
                return 0.0F;
            case EASY:
                return Math.min(distance / 2.0F + 1.0F, distance);
            case HARD:
                return distance * 3.0F / 2.0F;
        }
        return distance;
    }

}
