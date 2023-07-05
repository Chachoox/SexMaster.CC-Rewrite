package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.mixins.AccessorSPacketChat;
import me.pignol.swift.api.util.TextUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChatTimestampsModule extends Module {

    private final Value<TextUtil.Color> bracketColor = new Value<>("BracketColor", TextUtil.Color.WHITE);
    private final Value<TextUtil.Color> timeColor = new Value<>("TimeColor", TextUtil.Color.WHITE);

    private final Value<Boolean> system = new Value<>("SystemMsgs", true);

    public ChatTimestampsModule() {
        super("ChatTimestamps", Category.MISC);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat) {
            SPacketChat packet = (SPacketChat) event.getPacket();
            if (packet.isSystem() && !system.getValue()) return;
            AccessorSPacketChat chat = (AccessorSPacketChat) event.getPacket();

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("H:mm");
            String timeStamp = simpleDateFormat.format(calendar.getTime());
            String time = TextUtil.coloredString("<", bracketColor.getValue()) + TextUtil.coloredString(timeStamp, timeColor.getValue()) + TextUtil.coloredString(">", bracketColor.getValue());
            chat.setTextComponent(new TextComponentString(time + " " + packet.getChatComponent().getFormattedText()));
        }
    }

}
