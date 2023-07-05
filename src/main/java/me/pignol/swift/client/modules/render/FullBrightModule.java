package me.pignol.swift.client.modules.render;

import me.pignol.swift.client.event.Stage;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FullBrightModule extends Module {

    private static final PotionEffect NIGHT_VISION = new PotionEffect(MobEffects.NIGHT_VISION, 99999);

    public FullBrightModule() {
        super("Fullbright", Category.RENDER);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (event.getStage() == Stage.POST && mc.player != null) {
            mc.player.addPotionEffect(NIGHT_VISION);
        }
    }

}
