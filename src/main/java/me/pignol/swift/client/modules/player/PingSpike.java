package me.pignol.swift.client.modules.player;

import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class PingSpike extends Module {

    public PingSpike() {
        super("KeyPearl", Category.PLAYER);
    }

    private boolean clicked = false;

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (Mouse.isButtonDown(2)) {
            if (!this.clicked && mc.currentScreen == null) {
                this.onClick();
            }
            this.clicked = true;
        } else {
            this.clicked = false;
        }
    }

    private void onClick() {
        boolean offhand;
        int pearlSlot = ItemUtil.getSlotHotbar(Items.ENDER_PEARL);
        boolean bl = offhand = mc.player.getHeldItemOffhand().getItem() == Items.ENDER_PEARL;
        if (pearlSlot != -1 || offhand) {
            int oldslot = mc.player.inventory.currentItem;
            if (!offhand) {
                mc.getConnection().sendPacket(new CPacketHeldItemChange(pearlSlot));
            }
            mc.playerController.processRightClick(mc.player, mc.world, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if (!offhand) {
                mc.getConnection().sendPacket(new CPacketHeldItemChange(oldslot));
            }
        }
    }

}