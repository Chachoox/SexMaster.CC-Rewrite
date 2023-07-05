package me.pignol.swift.client.modules.movement;

import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class StepModule extends Module {

    public StepModule() {
        super("Step", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if (isNull()) return;

        mc.player.stepHeight = 2F;
    }

    @Override
    public void onDisable() {
        if (isNull()) return;
        mc.player.stepHeight = 0.6F;
    }

}
