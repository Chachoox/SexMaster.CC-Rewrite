package me.pignol.swift.api.util;

import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ColorUtil {

    public static void glColor(final int hex) {
        GL11.glColor4f(hex >> 16 & 0xFF, hex >> 8 & 0xFF, hex & 0xFF, hex >> 24 & 0xFF);
    }

    public static int getRainbow(int speed, int offset, float s) {
        float hue = (System.currentTimeMillis() + offset) % speed;
        return (Color.getHSBColor(hue / speed, s, 1f).getRGB());
    }



}
