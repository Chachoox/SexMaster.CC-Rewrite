package me.pignol.swift.api.util.runnables;

import me.pignol.swift.api.interfaces.Globals;
import me.pignol.swift.api.util.DamageUtil;
import me.pignol.swift.api.util.EntityUtil;
import me.pignol.swift.client.managers.SafetyManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;

import java.util.List;

public class SafetyRunnable implements Globals, Runnable {

    private final SafetyManager manager;
    private final List<Entity> crystals;

    private final float maxDamage;
    private final float range;

    public SafetyRunnable(SafetyManager manager, List<Entity> crystals, float maxDamage, float range) {
        this.manager = manager;
        this.crystals = crystals;

        this.maxDamage = maxDamage;
        this.range = range;
    }

    @Override
    public void run() {
        float sqRange = range * range;
        for (Entity entity : crystals) {
            if (entity instanceof EntityEnderCrystal && !entity.isDead) {
                if (mc.player.getDistanceSq(entity) < sqRange) {
                    float damage = DamageUtil.calculate(entity.posX, entity.posY, entity.posZ, mc.player);
                    if (damage > maxDamage || damage > EntityUtil.getHealth(mc.player) + 2.0) {
                        manager.setSafe(false);
                        return;
                    }
                }
            }
        }
        manager.setSafe(true);
    }

}
