package me.pignol.swift.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.util.*;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InstantFloorModule extends Module {

    private final Value<Integer> delay = new Value<>("Delay", 100, 0, 1000);

    private final TimerUtil timer = new TimerUtil();

    private boolean didSwitch, didPlace;
    private int obbySlot;

    public InstantFloorModule() {
        super("InstantFloor", Category.COMBAT);
    }

    public static final BlockPos[] OFFSETS = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1)
    };

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (isNull()) {
            return;
        }

        if (!timer.hasReached(delay.getValue())) return;

        didPlace = false;
        didSwitch = false;

        int lastSlot = mc.player.inventory.currentItem;
        obbySlot = ItemUtil.getSlotHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (obbySlot == -1) {
            ChatUtil.printString(ChatFormatting.RED + "[InstantFloor] No obsidian.", -554);
            setEnabled(false);
            return;
        }

        EntityPlayer target = EntityUtil.getClosestPlayer(6F);
        if (target == null) {
            return;
        }

        BlockPos pos = new BlockPos(target.getPositionVector()).down();
        if (BlockUtil.isPositionPlaceable(pos) == 3) {

        }

        if (didSwitch) {
            mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
        }

        if (didPlace) {
            timer.reset();
        }
    }

    public void placeBlock(BlockPos pos) {
        if (!didSwitch) {
            mc.getConnection().sendPacket(new CPacketHeldItemChange(obbySlot));
            didSwitch = true;
        }
        didPlace = true;
        BlockUtil.placeBlock(pos);
    }

}
