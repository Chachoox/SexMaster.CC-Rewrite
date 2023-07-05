package me.pignol.swift.client.managers;

import me.pignol.swift.api.util.render.font.CFontRenderer;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class FontManager {

    private static final FontManager INSTANCE = new FontManager();

    public static FontManager getInstance() {
        return INSTANCE;
    }


    private boolean isCustom;

    private final CFontRenderer renderer = new CFontRenderer(new Font("Verdana", 0, 18), true, true);

    public void drawStringWithShadow(String text, float x, float y, int color) {
        if (isCustom) {
            renderer.drawStringWithShadow(text, x, y, color);
            return;
        }
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    public void drawString(String text, float x, float y, int color, boolean shadow) {
        if (isCustom) {
            renderer.drawString(text, x, y, color, shadow);
            return;
        }
        Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, color, shadow);
    }

    public int getHeight() {
        if (isCustom) {
            return renderer.getHeight();
        }
        return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    }

    public int getStringWidth(String text) {
        if (isCustom) {
            return renderer.getStringWidth(text);
        }
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    public CFontRenderer getCFontRenderer() {
        return this.renderer;
    }

}
