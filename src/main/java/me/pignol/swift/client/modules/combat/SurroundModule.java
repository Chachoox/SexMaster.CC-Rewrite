package me.pignol.swift.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.mixins.AccessorCPacketUseEntity;
import me.pignol.swift.api.util.*;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SurroundModule extends Module {

    private final Value<Integer> delay = new Value<>("Delay", 0, 0, 300);
    private final Value<Integer> blocksPerTick = new Value<>("Blocks/Tick", 10, 1, 20);
    private final Value<Boolean> center = new Value<>("Center", false);
    private final Value<Boolean> attack = new Value<>("Attack", false);
    private final Value<Boolean> floor = new Value<>("Floor", false);
    private final Value<Integer> maxDamage = new Value<>("MaxDamage", 6, 0, 36);
    private final Value<Integer> attackDelay = new Value<>("AttackDelay", 100, 0, 1000);
    private final Value<Integer> extender = new Value<>("Extend", 2, 0, 4);
    private final Value<Integer> retries = new Value<>("Retries", 4, 0, 15);
    private final Value<Boolean> retryPlayers = new Value<>("RetryPlayers", false);

    private final Map<BlockPos, Integer> retryMap = new HashMap<>();
    private final Set<Vec3d> extendingBlocks = new HashSet<>();
    private final TimerUtil timer = new TimerUtil();
    private final TimerUtil retryTimer = new TimerUtil();
    private final TimerUtil attackTimer = new TimerUtil();

    public static boolean isPlacing;

    private boolean didSwitch;
    private boolean onEchest;
    private boolean didPlace;
    private int placements, extenders, obbySlot;
    private double posY;

    public SurroundModule() {
        super("Surround", Category.COMBAT);
    }

    public static final BlockPos[] DOUBLEOFFSETS = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
    };

    @Override
    public void onDisable() {
        obbySlot = -1;
        onEchest = false;
        didPlace = false;
        placements = 0;
        didSwitch = false;
        isPlacing = false;
    }

    @Override
    public void onEnable() {
        if (isNull()) {
            setEnabled(false);
            return;
        }
        obbySlot = -1;
        if (center.getValue()) {
            BlockPos centerPos = mc.player.getPosition();
            Vec3d playerPos = mc.player.getPositionVector();
            double y = centerPos.getY();
            double x = centerPos.getX();
            double z = centerPos.getZ();

            final Vec3d plusPlus = new Vec3d(x + 0.5, y, z + 0.5);
            final Vec3d plusMinus = new Vec3d(x + 0.5, y, z - 0.5);
            final Vec3d minusMinus = new Vec3d(x - 0.5, y, z - 0.5);
            final Vec3d minusPlus = new Vec3d(x - 0.5, y, z + 0.5);

            //amazing
            if (playerPos.distanceTo(plusPlus) < playerPos.distanceTo(plusMinus) && playerPos.distanceTo(plusPlus) < playerPos.distanceTo(minusMinus) && playerPos.distanceTo(plusPlus) < playerPos.distanceTo(minusPlus)) {
                x = centerPos.getX() + 0.5;
                z = centerPos.getZ() + 0.5;
                mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
                mc.player.setPosition(x, y, z);
            }
            if (playerPos.distanceTo(plusMinus) < playerPos.distanceTo(plusPlus) && playerPos.distanceTo(plusMinus) < playerPos.distanceTo(minusMinus) && playerPos.distanceTo(plusMinus) < playerPos.distanceTo(minusPlus)) {
                x = centerPos.getX() + 0.5;
                z = centerPos.getZ() - 0.5;
                mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
                mc.player.setPosition(x, y, z);
            }
            if (playerPos.distanceTo(minusMinus) < playerPos.distanceTo(plusPlus) && playerPos.distanceTo(minusMinus) < playerPos.distanceTo(plusMinus) && playerPos.distanceTo(minusMinus) < playerPos.distanceTo(minusPlus)) {
                x = centerPos.getX() - 0.5;
                z = centerPos.getZ() - 0.5;
                mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
                mc.player.setPosition(x, y, z);
            }
            if (playerPos.distanceTo(minusPlus) < playerPos.distanceTo(plusPlus) && playerPos.distanceTo(minusPlus) < playerPos.distanceTo(plusMinus) && playerPos.distanceTo(minusPlus) < playerPos.distanceTo(minusMinus)) {
                x = centerPos.getX() - 0.5;
                z = centerPos.getZ() + 0.5;
                mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
                mc.player.setPosition(x, y, z);
            }
        }

        posY = mc.player.posY;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject && attack.getValue()) {
            final SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 51) {
                BlockPos crystalPos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
                BlockPos playerPos = new BlockPos(mc.player.getPositionVector());
                if (BlockUtil.isSafe(mc.player, 0, true)) {
                    for (BlockPos pos : DOUBLEOFFSETS) {
                        pos = playerPos.add(pos);
                        if (crystalPos.equals(pos)) {
                            AccessorCPacketUseEntity accessor = (AccessorCPacketUseEntity) new CPacketUseEntity();
                            accessor.setEntityId(packet.getEntityID());
                            accessor.setAction(CPacketUseEntity.Action.ATTACK);
                            mc.getConnection().sendPacket((Packet<?>) accessor);
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                            attackTimer.reset();
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (check()) {
            return;
        }

        EnumHand hand = mc.player.isHandActive() ? mc.player.getActiveHand() : null;
        int lastSlot = mc.player.inventory.currentItem;

        Vec3d[] unsafeBlocks = BlockUtil.getUnsafeBlocksArray(mc.player.getPositionVector(), onEchest ? 1 : 0, floor.getValue());
        if (unsafeBlocks.length != 0) {
            placeBlocks(mc.player.getPositionVector(), unsafeBlocks, true, false, false);
        }

        processExtendingBlocks();

        if (attack.getValue()) {
            doAttack();
        }

        if (didSwitch) {
            mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
            if (hand != null)
                mc.player.setActiveHand(hand);
        }

        if (didPlace) {
            timer.reset();
        }
    }

    private void doAttack() {
        if (attackTimer.hasReached(attackDelay.getValue())) {
            BlockPos playerPos = new BlockPos(mc.player.getPositionVector());
            for (BlockPos pos : DOUBLEOFFSETS) {
                pos = playerPos.add(pos);
                if (attack.getValue()) {
                    for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                        if (entity instanceof EntityEnderCrystal) {
                            final float damage = EntityUtil.calculate(entity.posX, entity.posY, entity.posY, mc.player);
                            if (damage < maxDamage.getValue() && damage + 4 < EntityUtil.getHealth(mc.player)) {
                                mc.getConnection().sendPacket(new CPacketUseEntity(entity));
                                mc.player.swingArm(EnumHand.MAIN_HAND);
                                attackTimer.reset();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void processExtendingBlocks() {
        if (extendingBlocks.size() == 2 && extenders < extender.getValue()) {
            Vec3d[] array = new Vec3d[2];
            int i = 0;
            for (Vec3d vec3d : extendingBlocks) {
                array[i] = vec3d;
                i++;
            }
            int placementsBefore = placements;
            if (areClose(array) != null) {
                placeBlocks(areClose(array), BlockUtil.getUnsafeBlocksArray(areClose(array), 0, floor.getValue()), true, false, true);
            }

            if (placementsBefore < placements) {
                extendingBlocks.clear();
            }
        } else if (extendingBlocks.size() > 2 || !(extenders < extender.getValue())) {
            extendingBlocks.clear();
        }
    }

    private Vec3d areClose(Vec3d[] vec3ds) {
        int matches = 0;
        for (Vec3d vec3d : vec3ds) {
            for (Vec3d pos : BlockUtil.getUnsafeBlocksArray(mc.player.getPositionVector(), 0, floor.getValue())) {
                if (vec3d.equals(pos)) {
                    matches++;
                }
            }
        }
        if (matches >= 2) {
            return mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1]));
        }
        return null;
    }

    private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean isExtending) {
        int helpings = 0;
        if (pos == null) return false;
        boolean gotHelp;
        for (Vec3d vec3d : vec3ds) {
            if (vec3d == null) {
                continue;
            }
            gotHelp = true;
            helpings++;
            if (isHelping && helpings > 1) {
                return false;
            }
            BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
            switch (BlockUtil.isPositionPlaceable(position)) {
                case -1:
                    continue;
                case 1:
                    if (retryMap.get(position) == null || retryMap.get(position) < retries.getValue()) {
                        placeBlock(position);
                        retryMap.put(position, (retryMap.get(position) == null ? 1 : (retryMap.get(position) + 1)));
                        retryTimer.reset();
                        continue;
                    }
                case 4:
                    if (retryPlayers.getValue() && (retryMap.get(position) == null || retryMap.get(position) < retries.getValue())) {
                        placeBlock(position);
                        retryMap.put(position, (retryMap.get(position) == null ? 1 : (retryMap.get(position) + 1)));
                        retryTimer.reset();
                    }
                    if (extender.getValue() > 0 && !isExtending && extenders < extender.getValue()) {
                        placeBlocks(mc.player.getPositionVector().add(vec3d), BlockUtil.getUnsafeBlocksArray(mc.player.getPositionVector().add(vec3d), 0, floor.getValue()), hasHelpingBlocks, false, true);
                        extendingBlocks.add(vec3d);
                        extenders++;
                    }
                    continue;
                case 2:
                    if (hasHelpingBlocks) {
                        gotHelp = placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
                    } else {
                        continue;
                    }
                case 3:
                    if (gotHelp) {
                        placeBlock(position);
                    }
                    if (isHelping) {
                        return true;
                    }
            }
        }
        return false;
    }

    private boolean check() {
        if (isNull()) {
            return true;
        }

        isPlacing = false;

        if (mc.player.posY > posY) {
            setEnabled(false);
            return true;
        }

        obbySlot = ItemUtil.getSlotHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (obbySlot == -1) {
            obbySlot = ItemUtil.getSlotHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
            if (obbySlot == -1) {
                ChatUtil.printString(ChatFormatting.RED + "[Surround] No obsidian.", -554);
                setEnabled(false);
                return true;
            }
        }

        onEchest = mc.player.onGround && (mc.player.posY - (int) mc.player.posY != 0);
        didSwitch = false;
        didPlace = false;
        extenders = 1;
        placements = 0;

        if (retryTimer.hasReached(500)) {
            retryMap.clear();
            retryTimer.reset();
        }

        return !timer.hasReached(delay.getValue());
    }

    private void placeBlock(BlockPos pos) {
        if (placements < blocksPerTick.getValue()) {
            isPlacing = true;
            if (!didSwitch) {
                mc.getConnection().sendPacket(new CPacketHeldItemChange(obbySlot));
                didSwitch = true;
            }
            BlockUtil.placeBlock(pos);
            didPlace = true;
            placements++;
        }
    }

}
