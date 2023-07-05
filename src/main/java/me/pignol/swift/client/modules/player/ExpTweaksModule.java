package me.pignol.swift.client.modules.player;

import me.pignol.swift.api.mixins.AccessorMinecraft;
import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.init.Items;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;


public class ExpTweaksModule extends Module {

    public ExpTweaksModule() {
        super("XPTweaks", Category.PLAYER);
    }

    private final Value<Boolean> middleclick = new Value<>("MiddleClick", true, "Throws Exp when you middle click");
    private final Value<Integer> stopMend = new Value<>("StopPercentage",80,0,100, "Percentage to stop throwing Exp");
    private final Value<Boolean> feet = new Value<>("FeetXP", true, "Throws Exp at -90 pitch");
    private final Value<Boolean> fast = new Value<>("FastXP", true, "Speeds up Exp placing");

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        boolean isHolding = mc.player.getHeldItemMainhand().getItem().getClass() == ItemExpBottle.class;

        if (isHolding && fast.getValue()) {
            ((AccessorMinecraft) mc).setDelay(0);
        }
        if (!isAnyPieceLow()) return;

        //this shit wouldnt have worked unless u had a exp bottle in hand
        if (middleclick.getValue()) {
            if (Mouse.isButtonDown(2)) {
                onClick();
            }
        }
    }

    private boolean isAnyPieceLow() {
        boolean wasAnyLower = false;
        for (int i = 0; i < 4; ++i) {
            int dura = (int) ItemUtil.getDamageInPercent(mc.player.inventory.armorInventory.get(i));
            if (dura < stopMend.getValue()) {
                wasAnyLower = true;
                break;
            }
        }

        return wasAnyLower;
    }

    private void onClick() {
        int xpSlot = ItemUtil.getSlotHotbar(Items.EXPERIENCE_BOTTLE);
        if (xpSlot != -1) {

            int oldslot = mc.player.inventory.currentItem;
            EnumHand hand = mc.player.isHandActive() ? mc.player.getActiveHand() : null;

            mc.getConnection().sendPacket(new CPacketHeldItemChange(xpSlot));

            mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);

            mc.getConnection().sendPacket(new CPacketHeldItemChange(oldslot));

            if (hand != null) {
                mc.player.setActiveHand(hand);
            }

        }
    }
}
