package me.pignol.swift.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.util.BlockUtil;
import me.pignol.swift.api.util.ChatUtil;
import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.util.TimerUtil;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.FriendManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.other.ColorsModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

public class AutoTrapModule extends Module {

    private final Value<Integer> delay = new Value<>("Delay/Place", 50, 0, 250, "delay pearl");
    private final Value<Integer> blocksPerPlace = new Value<>("Block/Place", 8, 1, 30, "Blockssinplace");
    private final Value<Double> targetRange = new Value<>("TargetRange", 10.0, 0.0, 20.0, "Targetsing");
    private final Value<Double> range = new Value<>("PlaceRange", 6.0, 0.0, 10.0, "Placing");
    private final Value<Boolean> antiScaffold = new Value<>("AntiScaffold", false, " Place one more block on top of them");
    private final Value<Boolean> antiStep = new Value<>("AntiStep", false, "place blocks around them and stuff");
    private final Value<Boolean> legs = new Value<>("Legs", false, "Aways place on leg");
    private final Value<Boolean> smartLegs = new Value<>("SmartLegs", false, "Doesnt place on legs if dont need ");
    private final Value<Boolean> platform = new Value<>("Platform", false, "I hate dez settings");
    private final Value<Boolean> antiDrop = new Value<>("AntiDrop", false, "Dumb ass shit");
    private final Value<Boolean> entityCheck = new Value<>("NoBlock", true, "Try place block on entity");
    private final Value<Integer> retryer = new Value<>("Retries", 4, 1, 15, "How many times try blck on entity");
    private final Value<Boolean> render = new Value<>("Render", true, "Renderings or no");
    private final Value<Integer> alpha = new Value<>("Alpha", 50, 0, 255, "ALpha for renderings some stuff");

    private List<Vec3d> placeTargets = new ArrayList<>();
    private boolean didPlace = false;
    public EntityPlayer target;
    private int placements = 0;
    private int obbySlot;
    private boolean didSwitch;
    public static boolean isPlacing = false;

    private final Map<BlockPos, Integer> retries = new HashMap<>();
    private final TimerUtil retryTimer = new TimerUtil();
    private final TimerUtil timer = new TimerUtil();

    public AutoTrapModule() {
        super("AutoTrap", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        if (isNull()) return;
        retries.clear();
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (render.getValue() && target != null && placeTargets.size() != 0) {
            for (Vec3d vec : placeTargets) {
                BlockPos pos = new BlockPos(vec);
                if (BlockUtil.isPositionPlaceable(pos) != 3)
                    continue;
                RenderUtil.drawBox(pos, ColorsModule.INSTANCE.getColor(), true, alpha.getValue());
            }
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateEvent event) {
        if (check()) {
            return;
        }

        obbySlot = ItemUtil.getSlotHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (obbySlot == -1) {
            ChatUtil.printString(ChatFormatting.RED + "[AutoTrap] No obsidian.", -554);
            setEnabled(false);
            return;
        }

        EnumHand hand = mc.player.isHandActive() ? mc.player.getActiveHand() : null;
        int lastSlot = mc.player.inventory.currentItem;

        doTrap();

        if (didSwitch) {
            mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
            if (hand != null)
                mc.player.setActiveHand(hand);
        }

        if (didPlace) {
            timer.reset();
        }
    }

    private void doTrap() {
        boolean needsHelpingBlock = BlockUtil.isPositionPlaceable(new BlockPos(target.getPositionVector().add(0, 2, 0))) == 2;
        boolean needsLegs = false;

        if (smartLegs.getValue()) {
            for (Vec3d offset : BlockUtil.getUnsafeBlocksFromVec3d(target.getPositionVector(), 1, false)) {
                if (offset == null)
                    continue;
                BlockPos pos = new BlockPos(target.getPositionVector().add(offset));
                if (BlockUtil.isPositionPlaceable(pos) != 3) {
                    needsLegs = true;
                    break;
                }
            }
        }

        List<Vec3d> placeTargets = BlockUtil.targets(target.getPositionVector(), antiScaffold.getValue(), antiStep.getValue(), smartLegs.getValue() ? needsLegs : legs.getValue(), platform.getValue(), antiDrop.getValue(), needsHelpingBlock);
        placeList(placeTargets);
        this.placeTargets = placeTargets;
    }

    private void placeList(List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));

        for (Vec3d vec3d : list) {
            BlockPos position = new BlockPos(vec3d);
            int placeability = BlockUtil.isPositionPlaceable(position);
            if (entityCheck.getValue() && placeability == 1 && (retries.get(position) == null || retries.get(position) < retryer.getValue())) {
                placeBlock(position);
                retries.put(position, (retries.get(position) == null ? 1 : (retries.get(position) + 1)));
                retryTimer.reset();
                continue;
            }

            if (placeability == 3) {
                placeBlock(position);
            }
        }
    }

    private boolean check() {
        isPlacing = false;
        didPlace = false;
        didSwitch = false;
        placements = 0;

        if (retryTimer.hasReached(2000)) {
            retries.clear();
            retryTimer.reset();
        }

        target = getClosestPlayer(targetRange.getValue(), true);

        return target == null || !timer.hasReached(delay.getValue());
    }

    public EntityPlayer getClosestPlayer(double range, boolean untrapped) {
        double maxDistance = 999.0D;
        EntityPlayer target = null;

        range *= range;

        for (EntityPlayer player : mc.world.playerEntities) {
            if (player != mc.player) {
                final double distance = player.getDistanceSq(mc.player);
                if (range > distance && player.getHealth() > 0 && !player.isDead && !FriendManager.getInstance().isFriend(player.getName())) {
                    if (untrapped && !BlockUtil.isTrapped(player, antiScaffold.getValue(), antiStep.getValue(), legs.getValue(), platform.getValue(), antiDrop.getValue(), false)) {
                        if (distance < maxDistance) {
                            maxDistance = distance;
                            target = player;
                        }
                    }
                }
            }
        }

        if (untrapped && target == null) {
            return getClosestPlayer(range, false);
        }

        return target;
    }

    private void placeBlock(BlockPos pos) {
        if (placements < blocksPerPlace.getValue() && mc.player.getDistanceSq(pos) <= (range.getValue() * range.getValue())) {
            if (!didSwitch) {
                mc.getConnection().sendPacket(new CPacketHeldItemChange(obbySlot));
                didSwitch = true;
            }
            isPlacing = true;
            BlockUtil.placeBlock(pos);
            didPlace = true;
            placements++;
        }
    }


}
