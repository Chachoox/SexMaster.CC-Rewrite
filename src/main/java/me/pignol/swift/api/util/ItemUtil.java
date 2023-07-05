package me.pignol.swift.api.util;

import me.pignol.swift.api.mixins.AccessorPlayerControllerMP;
import net.minecraft.client.Minecraft;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class ItemUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float getDamageInPercent(ItemStack stack) {
        float green = ((float)stack.getMaxDamage() - (float)stack.getItemDamage()) / (float)stack.getMaxDamage();
        float red = 1.0f - green;
        return 100 - (int)(red * 100.0f);
    }

    public static boolean hasDurability(Item item) {
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }

    public static void switchToSlot(int slot, boolean silent) {
        if (slot >= 0 && slot <= 8) { //only valid slots
            if (!silent) {
                mc.player.inventory.currentItem = slot;
                syncHeldItem();
            } else {
                mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
            }
        }
    }

    public static int getSlotHotbar(Class clazz) {
        int slot = -1;
        if (mc.player == null) {
            return slot;
        }

        for (int i = 8; i >= 0; --i) {
            if (mc.player.inventory.getStackInSlot(i).getItem().getClass() == clazz) {
                slot = i;
                break;
            }
        }

        return slot;
    }

    public static int getSlotHotbar(Item item) {
        int slot = -1;
        if (mc.player == null) {
            return slot;
        }

        for (int i = 8; i >= 0; --i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                slot = i;
                break;
            }
        }

        return slot;
    }

    public static int getSlot(Item item) {
        int slot = -1;
        if (mc.player == null) {
            return slot;
        }

        for (int i = 44; i >= 0; --i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                if (i < 9) {
                    i += 36;
                }
                slot = i;
                break;
            }
        }

        return slot;
    }

    public static int getItemCount(Item item) {
        if (mc.player == null) {
            return 0;
        }

        int count = 0;

        for (int i = 0; i < 45; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                count++;
            }
        }

        return count;
    }

    public static void syncHeldItem() {
        ((AccessorPlayerControllerMP) mc.playerController).invokeSyncCurrentPlayItem();
    }

}
