package me.pignol.swift.client.managers;

import me.pignol.swift.api.util.TimerUtil;
import me.pignol.swift.client.event.events.PacketEvent;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SwitchManager {

    private static final SwitchManager INSTANCE = new SwitchManager();

    public static SwitchManager getInstance() {
        return INSTANCE;
    }

    private final TimerUtil switchTimer = new TimerUtil();

    public void load() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketHeldItemChange) {
            switchTimer.reset();
        }
    }

    public long getMsPassed() {
        return switchTimer.getTimePassed();
    }

}
