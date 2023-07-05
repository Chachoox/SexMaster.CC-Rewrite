package me.pignol.swift.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import me.pignol.swift.api.util.*;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.HoleManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;
import java.util.stream.Collectors;

public class HoleFillerModule extends Module {

    private final Value<Float> enemyRange = new Value<>("EnemyRange", 8F, 1F, 10F);
    private final Value<Float> enemyHoleRange = new Value<>("EnemyHoleRange", 3F, 1F, 10F);
    private final Value<Float> holeRange = new Value<>("HoleRange", 5F, 1F, 6F);
    private final Value<Integer> blocksPerTick = new Value<>("Block/Tick", 10, 1, 20);
    private final Value<Integer> delay = new Value<>("Delay", 100, 0, 500);
    private final Value<Boolean> rayTrace = new Value<>("Raytrace", true);
    private final Value<Boolean> autoDisable = new Value<>("AutoDisable", true);
    private final Value<Boolean> holeCheck = new Value<>("HoleCheck", true);

    private final TimerUtil timer = new TimerUtil();

    private boolean placed;
    private int placeAmount;
    private int obbySlot;

    public HoleFillerModule() {
        super("HoleFiller", Category.COMBAT);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (isNull()) return;

        if (!timer.hasReached(delay.getValue())) {
            return;
        }

        placeAmount = 0;
        placed = false;
        final EntityPlayer target = EntityUtil.getClosestPlayer(enemyRange.getValue());
        if (target == null) {
            if (autoDisable.getValue()) {
                setEnabled(false);
            }
            return;
        }

        ReferenceArrayList<Pair<BlockPos, Boolean>> holes = HoleManager.getInstance().getHoles().stream().sorted(Comparator.comparing(pos -> target.getDistanceSq(pos.getKey()))).collect(Collectors.toCollection(ReferenceArrayList::new));

        if (holes.size() == 0) {
            if (autoDisable.getValue()) {
                setEnabled(false);
            }
            return;
        }

        obbySlot = ItemUtil.getSlotHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (obbySlot == -1) {
            if (autoDisable.getValue()) {
                ChatUtil.printString(ChatFormatting.RED + "[Surround] No obsidian.", -554);
                setEnabled(false);
            }
            return;
        }

        if (holeCheck.getValue()) {
            if (!BlockUtil.isSafe(mc.player, 0, true) && BlockUtil.isSafe(target, 0, true)) {
                return;
            }
        }

        int lastSlot = mc.player.inventory.currentItem;
        EnumHand hand = mc.player.isHandActive() ? mc.player.getActiveHand() : null;
        boolean switched = false;

        for (Pair<BlockPos, Boolean> pair : holes) {
            if (pair == null) continue;
            BlockPos pos = pair.getKey();
            if (pos == null) continue;
            if (mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ()) < holeRange.getValue() && target.getDistance(pos.getX(), pos.getY(), pos.getZ()) < enemyHoleRange.getValue()) {
                if (BlockUtil.isPositionPlaceable(pos) != 3) continue;
                if (!switched) {
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(obbySlot));
                    switched = true;
                }
                placeBlock(pos);
            }
        }

        if (switched) {
            mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
            if (hand != null)
                mc.player.setActiveHand(hand);
        }

        if (autoDisable.getValue()) {
            setEnabled(false);
        }

        if (placed) {
            timer.reset();
        }
    }

    private void placeBlock(BlockPos pos) {
        if (blocksPerTick.getValue() > placeAmount) {
            BlockUtil.placeBlock(pos);
            placeAmount++;
            placed = true;
        }
    }


}
