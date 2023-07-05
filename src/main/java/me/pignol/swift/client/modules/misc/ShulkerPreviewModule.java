package me.pignol.swift.client.modules.misc;

import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class ShulkerPreviewModule extends Module {

    public static ShulkerPreviewModule INSTANCE = new ShulkerPreviewModule();

    public ShulkerPreviewModule() {
        super("ShulkerPreview", Category.MISC, false, false);
    }

}
