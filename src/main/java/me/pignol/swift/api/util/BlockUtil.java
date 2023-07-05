package me.pignol.swift.api.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.pignol.swift.client.managers.RotationManager;
import me.pignol.swift.client.modules.other.ManageModule;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BlockUtil {

    private final static Minecraft mc = Minecraft.getMinecraft();

    public static final Vec3d[] antiDropOffsetList = {
            new Vec3d(0, -2, 0),
    };

    public static final Vec3d[] platformOffsetList = {
            new Vec3d(0, -1, 0),
            new Vec3d(0, -1, -1),
            new Vec3d(0, -1, 1),
            new Vec3d(-1, -1, 0),
            new Vec3d(1, -1, 0)
    };

    public static final Vec3d[] legOffsetList = {
            new Vec3d(-1, 0, 0),
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(0, 0, 1)
    };

    public static final Vec3d[] offsetList = {
            new Vec3d(1, 1, 0),
            new Vec3d(-1, 1, 0),
            new Vec3d(0, 1, 1),
            new Vec3d(0, 1, -1),
            new Vec3d(0, 2, 0)
    };

    public static final Vec3d[] antiStepOffsetList = {
            new Vec3d(-1, 2, 0),
            new Vec3d(1, 2, 0),
            new Vec3d(0, 2, 1),
            new Vec3d(0, 2, -1),
    };

    public static final Vec3d[] antiScaffoldOffsetList = {
            new Vec3d(0, 3, 0)
    };

    public static final Vec3d[] OFFSETS_VEC = {
            new Vec3d(1, 0, 0),
            new Vec3d(-1, 0, 0),
            new Vec3d(0, 0, 1),
            new Vec3d(0, 0, -1),
            new Vec3d(0, -1, 0)
    };


    public static final BlockPos[] OFFSETS = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(0, -1, 0)
    };

    public static BlockPos.MutableBlockPos offsetMutable(BlockPos.MutableBlockPos pos, EnumFacing facing) {
        return pos.setPos(pos.getX() + facing.getXOffset(), pos.getY() + facing.getYOffset(), pos.getZ() + facing.getZOffset());
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        List<EnumFacing> facings = new ArrayList<>();
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            IBlockState state = mc.world.getBlockState(neighbour);
            if (state.getBlock().canCollideCheck(state, false)) {
                if (!state.getMaterial().isReplaceable()) {
                    facings.add(side);
                }
            }
        }
        return facings;
    }

    public static boolean canPlaceCrystal(final BlockPos.MutableBlockPos pos, boolean second, boolean raytrace) {
        final Chunk chunk = mc.world.getChunk(pos);
        final Block block = chunk.getBlockState(pos).getBlock();
        if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN) {
            return false;
        }

        int originX = pos.getX();
        int originY = pos.getY();
        int originZ = pos.getZ();

        pos.setPos(originX, originY + 1, originZ);
        if (chunk.getBlockState(pos).getBlock() != Blocks.AIR || chunk.getBlockState(pos.setPos(originX, originY + 2, originZ)).getBlock() != Blocks.AIR) {
            return false;
        }

        pos.setPos(originX, originY + 1, originZ);
        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + (second ? 2 : 1), pos.getZ() + 1);
        final int entityListLength = chunk.getEntityLists().length - 1;
        final int i = MathHelper.clamp(MathHelper.floor((bb.minY - World.MAX_ENTITY_RADIUS) / 16.0D), 0, entityListLength);
        final int j = MathHelper.clamp(MathHelper.floor((bb.maxY + World.MAX_ENTITY_RADIUS) / 16.0D), 0, entityListLength);
        for (int k = i; k <= j; ++k) {
            for (Entity t : chunk.getEntityLists()[k].getByClass(Entity.class)) {
                if (t.getEntityBoundingBox().intersects(bb) && !(t instanceof EntityEnderCrystal)) {
                    return false;
                }
            }
        }
        pos.setPos(originX, originY, originZ);
        return true;
    }

    public static boolean canPlaceCrystal(final BlockPos pos, boolean second, boolean raytrace) {
        final Chunk chunk = mc.world.getChunk(pos);
        final Block block = chunk.getBlockState(pos).getBlock();
        if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN) {
            return false;
        }

        int originX = pos.getX();
        int originY = pos.getY();
        int originZ = pos.getZ();

        if (chunk.getBlockState(originX, originY + 1, originZ).getBlock() != Blocks.AIR || chunk.getBlockState(originX, originY + 2, originZ).getBlock() != Blocks.AIR) {
            return false;
        }

        if (raytrace && !RaytraceUtil.raytracePlaceCheck(mc.player, pos)) {
            return false;
        }

        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + (second ? 2 : 1), pos.getZ() + 1);
        final int entityListLength = chunk.getEntityLists().length - 1;
        final int start = MathHelper.clamp(MathHelper.floor((bb.minY - World.MAX_ENTITY_RADIUS) / 16.0D), 0, entityListLength);
        final int end = MathHelper.clamp(MathHelper.floor((bb.maxY + World.MAX_ENTITY_RADIUS) / 16.0D), 0, entityListLength);
        for (int k = start; k <= end; ++k) {
            for (Entity entity : chunk.getEntityLists()[k].getByClass(Entity.class)) {
                if (!entity.isDead && !(entity instanceof EntityEnderCrystal)) {
                    if (entity.getEntityBoundingBox().intersects(bb)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static ObjectArrayList<BlockPos> getSphere(final float radius, final float yRadius) {
        final ObjectArrayList<BlockPos> sphere = new ObjectArrayList<>();
        final int posX = (int) mc.player.posX;
        final int posY = (int) mc.player.posY;
        final int posZ = (int) mc.player.posZ;
        for (int x = posX - (int) radius; x <= posX + radius; ++x) {
            for (int z = posZ - (int) radius; z <= posZ + radius; ++z) {
                for (int y = posY - (int) yRadius; y < posY + yRadius; ++y) {
                    if ((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y) < radius * radius) {
                        sphere.add(new BlockPos(x, y, z));
                    }
                }
            }
        }
        return sphere;
    }

    public static int isPositionPlaceable(BlockPos pos) {
        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return 0;
        }

        AxisAlignedBB bbPos = new AxisAlignedBB(pos);
        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, bbPos)) {
            if (entity.getRenderBoundingBox().intersects(bbPos)) {
                if (entity instanceof EntityEnderCrystal) {
                    if (entity.ticksExisted > 15)
                        return 0;

                    return 1;
                }

                if (entity instanceof EntityPlayer) {
                    return 4;
                }
            }
        }

        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            IBlockState state = mc.world.getBlockState(neighbour);
            if (state.getBlock().canCollideCheck(state, false)) {
                if (!state.getMaterial().isReplaceable() && canBeClicked(neighbour)) {
                    return 3;
                }
            }
        }

        return 2;
    }

    public static List<Vec3d> getBlockBlocks(Entity entity) {
        List<Vec3d> vec3ds = new ArrayList<>();
        AxisAlignedBB bb = entity.getEntityBoundingBox();
        double y = entity.posY;
        double minX = Math.floor(bb.minX);
        double minZ = Math.floor(bb.minZ);
        double maxX = Math.floor(bb.maxX);
        double maxZ = Math.floor(bb.maxZ);
        if (minX != maxX) {
            vec3ds.add(new Vec3d(minX, y, minZ));
            vec3ds.add(new Vec3d(maxX, y, minZ));
            if (minZ != maxZ) {
                vec3ds.add(new Vec3d(minX, y, maxZ));
                vec3ds.add(new Vec3d(maxX, y, maxZ));
                return vec3ds;
            }
        } else if (minZ != maxZ) {
            vec3ds.add(new Vec3d(minX, y, minZ));
            vec3ds.add(new Vec3d(minX, y, maxZ));
            return vec3ds;
        }
        vec3ds.add(entity.getPositionVector());
        return vec3ds;
    }

    public static List<Vec3d> targets(Vec3d vec3d, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean needsHelping) {
        List<Vec3d> placeTargets = new ArrayList<>();
        if (antiDrop) {
            Collections.addAll(placeTargets, convertVec3ds(vec3d, antiDropOffsetList));
        }

        if (platform) {
            Collections.addAll(placeTargets, convertVec3ds(vec3d, platformOffsetList));
        }

        if (legs) {
            Collections.addAll(placeTargets, convertVec3ds(vec3d, legOffsetList));
        }

        Collections.addAll(placeTargets, convertVec3ds(vec3d, offsetList));

        if (antiStep) {
            Collections.addAll(placeTargets, convertVec3ds(vec3d, antiStepOffsetList));
        } else if (needsHelping) {
            List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2, false);
            vec3ds.sort(Comparator.comparing(pos -> mc.player.getDistanceSq(vec3d.add(pos.x, 0, 0).x, vec3d.add(0, pos.y, 0).y, vec3d.add(0, 0, pos.z).z)));
            for (Vec3d vector : vec3ds) {
                BlockPos position = new BlockPos(vec3d).add(vector.x, vector.y, vector.z);
                switch (BlockUtil.isPositionPlaceable(position)) {
                    case 0:
                        break;
                    case 1:
                    case 2:
                        continue;
                    case 3:
                        placeTargets.add(vec3d.add(vector));
                        break;
                }
                break;
            }
        }

        if (antiScaffold){
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
        }

        return placeTargets;
    }

    public static boolean isTrapped(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean needsHelping) {
        return targets(player.getPositionVector(), antiScaffold, antiStep, legs, platform, antiDrop, needsHelping).size() == 0;
    }

    public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
        Vec3d[] output = new Vec3d[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = vec3d.add(input[i]);
        }
        return output;
    }

    public static boolean placeBlock(BlockPos pos) {
        for (EnumFacing side : EnumFacing.VALUES) {
            BlockPos neighbor = pos.offset(side);
            IBlockState neighborState = mc.world.getBlockState(neighbor);
            if (neighborState.getBlock().canCollideCheck(neighborState, false)) {
                boolean sneak = !mc.player.isSneaking() && neighborState.getBlock().onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, EnumHand.MAIN_HAND, side, 0.5f, 0.5f, 0.5f);
                if (sneak) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }

                if (ManageModule.INSTANCE.rotate.getValue()) {
                    float[] angles = RotationUtil.getRotations(neighbor, side.getOpposite());
                    RotationManager.getInstance().setPlayerRotations(angles[0], angles[1]);
                }

                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(neighbor, side.getOpposite(), EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));

                if (sneak) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }
                return sneak;
            }
        }
        return false;
    }

    public static List<Vec3d> getOffsetList(int y, boolean floor) {
        List<Vec3d> offsets = new ArrayList<>(floor ? 5 : 4);
        offsets.add(new Vec3d(-1, y, 0));
        offsets.add(new Vec3d(1, y, 0));
        offsets.add(new Vec3d(0, y, -1));
        offsets.add(new Vec3d(0, y, 1));
        if (floor) {
            offsets.add(new Vec3d(0, y - 1, 0));
        }
        return offsets;
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        Vec3d[] offsets = new Vec3d[floor ? 5 : 4];
        offsets[0] = new Vec3d(-1, y, 0);
        offsets[1] = new Vec3d(1, y, 0);
        offsets[2] = new Vec3d(0, y, -1);
        offsets[3] = new Vec3d(0, y, 1);
        if (floor) {
            offsets[4] = new Vec3d(0, y - 1, 0);
        }
        return offsets;
    }

    public static boolean isSafe(EntityPlayer player, int height, boolean floor) {
        final BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
        for (Vec3d vector : getOffsets(height, floor)) {
            final BlockPos targetPos = pos.add(vector.x, vector.y, vector.z);
            IBlockState state = mc.world.getBlockState(targetPos);
            if (state.getMaterial().isReplaceable()) {
                return false;
            }
        }
        return true;
    }

    public static Vec3d[] getUnsafeBlocksArray(Vec3d pos, int height, boolean floor) {
        List<Vec3d> unsafe = getUnsafeBlocksFromVec3d(pos, height, floor);
        Vec3d[] vec3ds = new Vec3d[unsafe.size()];
        return unsafe.toArray(vec3ds);
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
        final BlockPos blockPos = new BlockPos(pos);
        final List<Vec3d> vec3ds = new ArrayList<>();
        for (Vec3d vector : getOffsets(height, floor)) {
            BlockPos targetPos = blockPos.add(vector.x, vector.y, vector.z);
            IBlockState state = mc.world.getBlockState(targetPos);
            if (state.getMaterial().isReplaceable()) {
                vec3ds.add(vector);
            }
        }
        return vec3ds;
    }

    public static boolean canBeClicked(BlockPos pos) {
        IBlockState state = mc.world.getBlockState(pos);
        return state.getBlock().canCollideCheck(state, false);
    }

    public static Vec3d[] getHelpingBlocks(Vec3d vec3d) {
        return new Vec3d[] {
                new Vec3d(vec3d.x, vec3d.y - 1, vec3d.z),
                new Vec3d(vec3d.x != 0 ? vec3d.x * 2 : vec3d.x, vec3d.y, vec3d.x != 0 ? vec3d.z : vec3d.z * 2),
                new Vec3d(vec3d.x == 0 ? vec3d.x + 1 : vec3d.x, vec3d.y, vec3d.x == 0 ? vec3d.z : vec3d.z + 1),
                new Vec3d(vec3d.x == 0 ? vec3d.x - 1 : vec3d.x, vec3d.y, vec3d.x == 0 ? vec3d.z : vec3d.z - 1),
                new Vec3d(vec3d.x, vec3d.y + 1, vec3d.z)
        };
    }



}
