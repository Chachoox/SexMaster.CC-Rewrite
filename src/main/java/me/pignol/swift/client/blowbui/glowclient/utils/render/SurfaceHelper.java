package me.pignol.swift.client.blowbui.glowclient.utils.render;

import me.pignol.swift.api.util.render.font.CFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

public class SurfaceHelper {

   public static double getStringWidth(CFontRenderer fontRenderer, String text) {
      return fontRenderer == null ? (double)Minecraft.getMinecraft().fontRenderer.getStringWidth(text) : (double)fontRenderer.getStringWidth(text);
   }

   public static void drawRect(int x, int y, int w, int h, int color) {
      GL11.glLineWidth(1.0F);
      Gui.drawRect(x, y, x + w, y + h, color);
   }


}
