package me.pignol.swift.client.blowbui.glowclient.clickgui.utils;

import me.pignol.swift.client.blowbui.glowclient.utils.render.Colors;
import me.pignol.swift.client.modules.other.ClickGuiModule;

import java.awt.*;

public class ColorUtils {
   public static final int BUTTON_OFF_OFF = Colors.toRGBA(150, 150, 150, 250);
   public static final int BUTTON_ON_OFF = Colors.toRGBA(150, 150, 150, 250);
   public static final int BUTTON_OFF_ON;
   public static final int BUTTON_ON_ON;
   public static final int WINDOW_ON;
   public static final int WINDOW_OFF;

   public static int getColorForGuiEntry(int type, boolean hovered, boolean state) {
      int red = ClickGuiModule.INSTANCE.red.getValue();
      int green = ClickGuiModule.INSTANCE.green.getValue();
      int blue = ClickGuiModule.INSTANCE.blue.getValue();
      int BUTTON2_OFF = Colors.toRGBA(0, 0, 0, 0);
      int BUTTON2_OFF_HOV = Colors.toRGBA(150, 150, 150, 50);
      int BUTTON2_ON = Colors.toRGBA(red, green, blue, 150);
      int BUTTON2_ON_HOV = Colors.toRGBA(red, green, blue, 175);
      switch (type) {
         case 0:
            if (hovered) {
               if (state) {
                  return BUTTON_ON_ON;
               }

               return BUTTON_ON_OFF;
            } else {
               if (state) {
                  return BUTTON_OFF_ON;
               }

               return BUTTON_OFF_OFF;
            }
         case 1:
            if (hovered) {
               return WINDOW_ON;
            }

            return WINDOW_OFF;
         case 2:
            if (hovered) {
               return BUTTON2_ON_HOV;
            }

            return BUTTON2_ON;
         case 3:
            if (!hovered) {
               if (state) {
                  return BUTTON2_ON;
               }

               return BUTTON2_OFF;
            } else {
               if (state) {
                  return BUTTON2_ON_HOV;
               }

               return BUTTON2_OFF_HOV;
            }
         default:
            throw new IllegalStateException("Invalid type: " + type);
      }
   }

   static {
      BUTTON_OFF_ON = Colors.WHITE;
      BUTTON_ON_ON = Colors.WHITE;
      WINDOW_ON = (new Color(255, 255, 255)).getRGB();
      WINDOW_OFF = (new Color(183, 183, 183)).getRGB();
   }
}
