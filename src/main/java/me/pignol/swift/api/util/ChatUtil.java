package me.pignol.swift.api.util;

import me.pignol.swift.client.modules.other.ManageModule;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class ChatUtil {

    private static final Minecraft MC = Minecraft.getMinecraft();

    private static String getPrefix() {
        return (TextUtil.coloredString("[", ManageModule.INSTANCE.bracketColor.getValue()) + TextUtil.coloredString("Swift", ManageModule.INSTANCE.nameColor.getValue()) + TextUtil.coloredString("]", ManageModule.INSTANCE.bracketColor.getValue())) + " ";
    }

    public static void printString(String string) {
        if (MC.player == null) return;
        MC.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(getPrefix() + string));
    }

    public static void printString(String string, int id) {
        if (MC.player == null) return;
        MC.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(getPrefix() + string), id);
    }


}
