package me.pignol.swift.client.modules.other;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

public class ColorsModule extends Module {

    public static ColorsModule INSTANCE = new ColorsModule();

    private final Value<Integer> red = new Value<>("Red", 255, 0, 255);
    private final Value<Integer> green = new Value<>("Green", 255, 0, 255);
    private final Value<Integer> blue = new Value<>("Blue", 255, 0, 255);

    private final Value<Integer> friendRed = new Value<>("FriendRed", 0, 0, 255);
    private final Value<Integer> friendGreen = new Value<>("FriendGreen", 255, 0, 255);
    private final Value<Integer> friendBlue = new Value<>("FriendBlue", 255, 0, 255);

    public final Value<Integer> speed = new Value<>("Speed", 10000, 1000, 20000);
    public final Value<Integer> saturation = new Value<>("Saturation", 255, 0, 255);
    public final Value<Integer> brightness = new Value<>("Brightness", 255, 0, 255);
    public final Value<Integer> factor = new Value<>("Factor", 100, 1, 250);

    public ColorsModule() {
        super("Colors", Category.OTHER, true);
        setDrawn(false);
    }

    private static Color color = new Color(-1);
    private static Color friendColor = new Color(0x00FFFF);

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            color = new Color(red.getValue(), green.getValue(), blue.getValue());
            friendColor = new Color(friendRed.getValue(), friendGreen.getValue(), friendBlue.getValue());
        }
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        setEnabled(true);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public Color getColor() {
        return color;
    }

    public Color getFriendColor() {
        return friendColor;
    }

}
