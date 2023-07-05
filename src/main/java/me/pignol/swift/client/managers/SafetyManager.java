package me.pignol.swift.client.managers;

import me.pignol.swift.api.interfaces.Globals;
import me.pignol.swift.api.util.runnables.SafetyRunnable;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.other.ManageModule;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SafetyManager implements Globals {

    private static final SafetyManager INSTANCE = new SafetyManager();

    public static SafetyManager getInstance() {
        return INSTANCE;
    }

    private final Executor executor = Executors.newSingleThreadExecutor();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onUpdate(UpdateEvent event) {
        if (!ManageModule.INSTANCE.safety.getValue()) {
            safe.set(true);
        }
        if (isNull()) {
            return;
        }
        executor.execute(new SafetyRunnable(this, mc.world.loadedEntityList, ManageModule.INSTANCE.maxDamage.getValue(), ManageModule.INSTANCE.crystalRange.getValue()));
    }

    private final AtomicBoolean safe = new AtomicBoolean();

    public void setSafe(boolean safe) {
        this.safe.set(safe);
    }

    public boolean isSafe() {
        return safe.get();
    }

}
