package com.sun.java.swing.plaf.gtk;

import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.util.StringTokenizer;
import javax.swing.plaf.FontUIResource;
import sun.font.FontUtilities;

class PangoFonts {
   public static final String CHARS_DIGITS = "0123456789";
   private static double fontScale = 1.0D;

   static Font lookupFont(String var0) {
      String var1 = "";
      int var2 = 0;
      int var3 = 10;
      StringTokenizer var4 = new StringTokenizer(var0);

      while(var4.hasMoreTokens()) {
         String var5 = var4.nextToken();
         if (var5.equalsIgnoreCase("italic")) {
            var2 |= 2;
         } else if (var5.equalsIgnoreCase("bold")) {
            var2 |= 1;
         } else if ("0123456789".indexOf(var5.charAt(0)) != -1) {
            try {
               var3 = Integer.parseInt(var5);
            } catch (NumberFormatException var12) {
            }
         } else {
            if (var1.length() > 0) {
               var1 = var1 + " ";
            }

            var1 = var1 + var5;
         }
      }

      double var13 = (double)var3;
      boolean var7 = true;
      Object var8 = Toolkit.getDefaultToolkit().getDesktopProperty("gnome.Xft/DPI");
      if (var8 instanceof Integer) {
         int var14 = (Integer)var8 / 1024;
         if (var14 == -1) {
            var14 = 96;
         }

         if (var14 < 50) {
            var14 = 50;
         }

         var13 = (double)(var14 * var3) / 72.0D;
      } else {
         var13 = (double)var3 * fontScale;
      }

      var3 = (int)(var13 + 0.5D);
      if (var3 < 1) {
         var3 = 1;
      }

      String var9 = var1.toLowerCase();
      Font var10;
      if (FontUtilities.mapFcName(var9) != null) {
         FontUIResource var15 = FontUtilities.getFontConfigFUIR(var9, var2, var3);
         var10 = var15.deriveFont(var2, (float)var13);
         return new FontUIResource(var10);
      } else {
         var10 = new Font(var1, var2, var3);
         var10 = var10.deriveFont(var2, (float)var13);
         FontUIResource var11 = new FontUIResource(var10);
         return FontUtilities.getCompositeFontUIResource(var11);
      }
   }

   static int getFontSize(String var0) {
      int var1 = 10;
      StringTokenizer var2 = new StringTokenizer(var0);

      while(var2.hasMoreTokens()) {
         String var3 = var2.nextToken();
         if ("0123456789".indexOf(var3.charAt(0)) != -1) {
            try {
               var1 = Integer.parseInt(var3);
            } catch (NumberFormatException var5) {
            }
         }
      }

      return var1;
   }

   static {
      GraphicsEnvironment var0 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      if (!GraphicsEnvironment.isHeadless()) {
         GraphicsConfiguration var1 = var0.getDefaultScreenDevice().getDefaultConfiguration();
         AffineTransform var2 = var1.getNormalizingTransform();
         fontScale = var2.getScaleY();
      }

   }
}
