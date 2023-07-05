package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.util.TimerUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoGgModule extends Module {

    private final Value<Boolean> clean = new Value<>("Clean", true);
    private final Value<Boolean> greenText = new Value<>("GreenText", false);
    private final Value<Boolean> discord = new Value<>("Discord", false);
    private final Value<Client> client = new Value<>("Mode", Client.TROLLGOD);
    private final Value<Integer> delay = new Value<Integer>("Delay", 1000, 0, 10000);

    private final TimerUtil timer = new TimerUtil();

    public AutoGgModule() {
        super("AutoGG", Category.MISC);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat && !isNull()) {
            SPacketChat packet = (SPacketChat) event.getPacket();
            String message = packet.getChatComponent().getUnformattedText();
            if (!message.contains("<") && message.contains("by " + mc.player.getName())) {
                if (timer.hasReached(delay.getValue())) {
                    if (client.getValue() == Client.ZAZA) {
                        mc.player.sendChatMessage("I spent $80 on an eighth! It is straight ZAZA!!!");
                    } else {
                        String clean = ((greenText.getValue() ? ">" : "") + "Good fight!");
                        mc.player.sendChatMessage(clean + (this.clean.getValue() ? "" : (" " + client.getValue().getName()) + " Still Hated and Highly Duplicated") + (discord.getValue() ? "  https://discord.gg/cFzDjYUkbp" : ""));
                    }
                    timer.reset();
                }
            }
        }
    }

    public enum Client {
        KAMI_BLUE("Kami Blue"),
        TROLLGOD("TrollGod.CC"),
        ZAZA("");

        private final String name;

        Client(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}
