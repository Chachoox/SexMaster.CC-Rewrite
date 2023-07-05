package me.pignol.swift;

import me.pignol.swift.api.util.Webhook;
import me.pignol.swift.client.blowbui.glowclient.clickgui.ClickGUI;
import me.pignol.swift.client.managers.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.File;

@Mod(modid = Swift.MOD_ID, name = Swift.MOD_NAME, version = Swift.VERSION)
public class Swift {
    public static final Logger LOGGER = LogManager.getLogger("Swift");
    public static final String MOD_ID = "swift";
    public static final String MOD_NAME = "Swift";
    public static final String VERSION = "0.7.1";
    public static final String NAME_VERSION = MOD_NAME + " " + VERSION;

    @Mod.Instance(MOD_ID)
    private static Swift INSTANCE;

    private final File file = new File(Minecraft.getMinecraft().gameDir, "Swift");
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (!file.exists()) {
            file.mkdirs();
        }
        ModuleManager.getInstance().load(new File(file, "Modules"));
        CommandManager.getInstance().load(new File(file, "Prefix.json"));
        FriendManager.getInstance().load(new File(file, "Friends.json"));
        EventManager.getInstance().load();
        ServerManager.getInstance().load();
        SwitchManager.getInstance().load();
        HoleManager.getInstance().load();
        Display.setTitle(Swift.MOD_NAME.toLowerCase() + " " + VERSION);
        new ClickGUI();
        ClickGUI.getInstance().initWindows();
    }

    public void unload() {
        ModuleManager.getInstance().unload();
        CommandManager.getInstance().unload();
        FriendManager.getInstance().unload();
    }

    public static Swift getInstance() {
        return INSTANCE;
    }

}
