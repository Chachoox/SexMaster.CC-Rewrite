package me.pignol.swift.client.modules.combat;

import me.pignol.swift.api.util.EntityUtil;
import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.util.TimerUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.SafetyManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OffhandModule extends Module {

    private final Value<Boolean> totem = new Value<>("Totem", true);
    private final Value<Boolean> swordGapple = new Value<>("SwordGapple", true);
    private final Value<Float> crystalHealth = new Value<>("CrystalHealth", 16.0F, 0.0F, 36.0F, v -> !totem.getValue());
    private final Value<Float> gappleHealth = new Value<>("GappleHealth", 16.0F, 0.0F, 36.0F, v -> swordGapple.getValue());
    private final Value<Integer> delay = new Value<>("Delay", 250, 0, 2000);

    private final TimerUtil timer = new TimerUtil();

    private boolean gappling;

    public OffhandModule() {
        super("Offhand", Category.COMBAT);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (isNull()) {
            return;
        }

        gappling = mc.gameSettings.keyBindUseItem.isKeyDown() && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword;
        Item item = getItem(gappling, SafetyManager.getInstance().isSafe());
        if (mc.player.getHeldItemOffhand().getItem() != item) {
            if (item != Items.TOTEM_OF_UNDYING && !timer.hasReached(delay.getValue())) { //Bypass delay if its a totem so we dont fucking die man
                return;
            }
            int slot = ItemUtil.getSlot(item);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.updateController();
            timer.reset();
        }
    }

    @Override
    public String getDisplayName() {
        if (totem.getValue()) {
            return "AutoTotem";
        }
        return gappling ? "OffhandGapple" : "OffhandCrystal";
    }

    public Item getItem(boolean gappling, boolean safe) {
        if (EntityUtil.getHealth(mc.player) > (gappling ? gappleHealth.getValue() : crystalHealth.getValue()) && safe) {
            return gappling ? Items.GOLDEN_APPLE : Items.END_CRYSTAL;
        }
        return Items.TOTEM_OF_UNDYING;
    }


}
