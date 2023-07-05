package me.pignol.swift.client.modules.other;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.managers.FontManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class FontModule extends Module {

    public static FontModule INSTANCE = new FontModule();

    public final Value<Boolean> syncChat = new Value<>("SyncChat", true);

    public FontModule() {
        super("Font", Category.OTHER, false, false);
    }

    @Override
    public void onDisable() {
        FontManager.getInstance().setCustom(false);
        HudModule.INSTANCE.sortModules();
    }

    @Override
    public void onEnable() {
        FontManager.getInstance().setCustom(true);
        HudModule.INSTANCE.sortModules();
    }

}
