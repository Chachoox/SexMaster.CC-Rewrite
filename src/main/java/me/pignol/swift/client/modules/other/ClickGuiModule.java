package me.pignol.swift.client.modules.other;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.blowbui.glowclient.clickgui.ClickGUI;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class ClickGuiModule extends Module {

    public static ClickGuiModule INSTANCE = new ClickGuiModule();

    public final Value<Integer> red = new Value<>("Red", 255, 0, 255);
    public final Value<Integer> green = new Value<>("Green", 255, 0, 255);
    public final Value<Integer> blue = new Value<>("Blue", 255, 0, 255);
    public final Value<Integer> alpha = new Value<>("Alpha", 80, 0, 255);
    public final Value<Integer> categoryAlpha = new Value<>("CategoryAlpha", 80, 0, 255);
    public final Value<Integer> backgroundRed = new Value<>("BackgroundRed", 0, 0, 255);
    public final Value<Integer> backgroundGreen = new Value<>("BackgroundGreen", 0, 0, 255);
    public final Value<Integer> backgroundBlue = new Value<>("BackgroundBlue", 0, 0, 255);
    public final Value<Integer> backgroundAlpha = new Value<>("BackgroundAlpha", 80, 0, 255);
    public final Value<Boolean> customFont = new Value<>("CustomFont", true);

    public ClickGuiModule() {
        super("ClickGUI", Category.OTHER, false, false);
        setDrawn(false);
    }

    @Override
    public void onEnable() {
        if (mc.world == null) {
            setEnabled(false);
            return;
        }

        mc.displayGuiScreen(ClickGUI.getInstance());
    }

}
