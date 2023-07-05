package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.util.TimerUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;
import java.util.Queue;

public class PingSpoofModule extends Module {

    private final Value<Integer> ping = new Value<>("Ping", 0, 0, 1000);

    private final Queue<CPacketKeepAlive> packetQueue = new LinkedList<>();

    private TimerUtil timer = new TimerUtil();

    public PingSpoofModule() {
        super("PingSpoof", Category.MISC);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketKeepAlive) {
            packetQueue.add((CPacketKeepAlive) event.getPacket());
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (isNull()) {
            return;
        }
        if (timer.hasReached(ping.getValue())) {
            CPacketKeepAlive packet = packetQueue.poll();
            if (packet != null) {
                mc.getConnection().sendPacket(packet);
            }
            timer.reset();
        }
    }

}
