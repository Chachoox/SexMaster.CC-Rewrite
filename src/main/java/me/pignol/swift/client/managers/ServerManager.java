package me.pignol.swift.client.managers;

import me.pignol.swift.api.util.TimerUtil;
import me.pignol.swift.client.event.events.DisconnectEvent;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.other.ManageModule;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerManager {

    private static final ServerManager INSTANCE = new ServerManager();

    private final TimerUtil timer = new TimerUtil();

    private float ticksPerSecond = 20.0F;
    private long lastUpdate = -1;

    public static ServerManager getInstance() {
        return INSTANCE;
    }

    public void load() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onDisconnect(DisconnectEvent event) {
        reset();
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        timer.reset();
        if (event.getPacket() instanceof SPacketTimeUpdate) {
            long currentTime = System.currentTimeMillis();

            if (lastUpdate == -1) {
                lastUpdate = currentTime;
                return;
            }

            long timeDiff = currentTime - lastUpdate;

            float tickTime = timeDiff / 20.0F;
            if (tickTime == 0) {
                tickTime = 50;
            }

            float tps = 1000 / tickTime;
            if (tps > 20.0F) {
                tps = 20.0F;
            }

            this.ticksPerSecond = tps;
            lastUpdate = currentTime;
        }
    }

    public TimerUtil getTimer() {
        return timer;
    }

    public boolean isServerNotResponding() {
        return timer.hasReached(ManageModule.INSTANCE.serverNotResponding.getValue());
    }

    public float getTPS() {
        return ticksPerSecond;
    }

    public void reset() {
        this.ticksPerSecond = 20.0F;
    }

}
