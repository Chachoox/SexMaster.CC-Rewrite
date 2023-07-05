package me.pignol.swift.client.blowbui.glowclient.clickgui;

import me.pignol.swift.api.util.render.font.CFontRenderer;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.other.ClickGuiModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;

public class ClickGUI extends GuiScreen {

   public static CFontRenderer fontRenderer = new CFontRenderer(new Font("Verdana", Font.PLAIN, 18), true, true);

   private static ClickGUI INSTANCE;

   private final Minecraft mc;

   private Window[] windows;

   public ClickGUI() {
      INSTANCE = this;
      this.mc = Minecraft.getMinecraft();
   }

   @Override
   public void drawBackground(int tint) {
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void drawScreen(int x, int y, float ticks) {
      for (Window window : windows) {
         window.draw(x, y);
      }
   }

   public void mouseClicked(int x, int y, int b) throws IOException {
      for (Window window : windows) {
         window.processMouseClick(x, y, b);
      }
      super.mouseClicked(x, y, b);
   }

   public void mouseReleased(int x, int y, int state) {
      for (Window window : windows) {
         window.processMouseRelease(x, y, state);
      }
      super.mouseReleased(x, y, state);
   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int dWheel = MathHelper.clamp(Mouse.getEventDWheel(), -1, 1);
      if (dWheel != 0) {
         dWheel *= -1;
         int x = Mouse.getEventX() * this.width / mc.displayWidth;
         int y = this.height - Mouse.getEventY() * this.height / mc.displayHeight - 1;
         for (Window window : windows) {
            window.handleScroll(dWheel, x, y);
         }
      }
   }

   protected void keyTyped(char eventChar, int eventKey) {
      for (Window window : windows) {
         window.processKeyPress(eventChar, eventKey);
      }

      if (eventKey == 1) {
         mc.displayGuiScreen(null);
         if (mc.currentScreen == null) {
            mc.setIngameFocus();
         }
      }
   }

   @Override
   public void onGuiClosed() {
      ClickGuiModule.INSTANCE.setEnabled(false);
      super.onGuiClosed();
   }

   public void setWindows(Window... windows) {
      this.windows = windows;
   }

   public void initWindows() {
      windows = new Window[Category.values().length];
      Category[] values = Category.values();

      int xOffset = 2;

      for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
         Category category = values[i];
         String name = category.name();
         windows[i] = new Window(xOffset, 2, name.charAt(0) + name.toLowerCase().replaceFirst(Character.toString(name.charAt(0)).toLowerCase(), ""), category);
         xOffset += 110;
      }

      for (Window window : windows) {
         window.init(window.getCategory());
      }
   }

   public static ClickGUI getInstance() {
      return INSTANCE;
   }
}
