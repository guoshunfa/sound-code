package com.apple.laf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.plaf.UIResource;

public class AquaNativeResources {
   static final AquaUtils.RecyclableSingleton<Color> sBackgroundColor;

   private static native long getWindowBackgroundColor();

   public static Color getWindowBackgroundColorUIResource() {
      return (Color)sBackgroundColor.get();
   }

   static BufferedImage getRadioButtonSizerImage() {
      BufferedImage var0 = new BufferedImage(20, 20, 3);
      Graphics var1 = var0.getGraphics();
      var1.setColor(Color.pink);
      var1.fillRect(0, 0, 20, 20);
      var1.dispose();
      return var0;
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("osxui");
            return null;
         }
      });
      sBackgroundColor = new AquaUtils.RecyclableSingleton<Color>() {
         protected Color getInstance() {
            long var1 = AquaNativeResources.getWindowBackgroundColor();
            return new AquaNativeResources.CColorPaintUIResource(var1, 238, 238, 238, 255);
         }
      };
   }

   static class CColorPaintUIResource extends Color implements UIResource {
      public CColorPaintUIResource(long var1, int var3, int var4, int var5, int var6) {
         super(var3, var4, var5, var6);
      }
   }
}
