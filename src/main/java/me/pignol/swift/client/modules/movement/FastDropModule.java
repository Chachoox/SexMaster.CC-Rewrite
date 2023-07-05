package me.pignol.swift.client.modules.movement;

import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastDropModule extends Module {

    //private final Value<Integer> height = new Value<>("Height", 5, 1, 50);

    public FastDropModule() {
        super("FastDrop", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (mc.player.onGround && !(mc.player.isInLava() || mc.player.isInWater())) {
            mc.player.motionY--;
        }
    }

}
