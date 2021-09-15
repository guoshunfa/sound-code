package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import sun.swing.SwingUtilities2;

public class BasicGraphicsUtils {
   private static final Insets GROOVE_INSETS = new Insets(2, 2, 2, 2);
   private static final Insets ETCHED_INSETS = new Insets(2, 2, 2, 2);

   public static void drawEtchedRect(Graphics var0, int var1, int var2, int var3, int var4, Color var5, Color var6, Color var7, Color var8) {
      Color var9 = var0.getColor();
      var0.translate(var1, var2);
      var0.setColor(var5);
      var0.drawLine(0, 0, var3 - 1, 0);
      var0.drawLine(0, 1, 0, var4 - 2);
      var0.setColor(var6);
      var0.drawLine(1, 1, var3 - 3, 1);
      var0.drawLine(1, 2, 1, var4 - 3);
      var0.setColor(var8);
      var0.drawLine(var3 - 1, 0, var3 - 1, var4 - 1);
      var0.drawLine(0, var4 - 1, var3 - 1, var4 - 1);
      var0.setColor(var7);
      var0.drawLine(var3 - 2, 1, var3 - 2, var4 - 3);
      var0.drawLine(1, var4 - 2, var3 - 2, var4 - 2);
      var0.translate(-var1, -var2);
      var0.setColor(var9);
   }

   public static Insets getEtchedInsets() {
      return ETCHED_INSETS;
   }

   public static void drawGroove(Graphics var0, int var1, int var2, int var3, int var4, Color var5, Color var6) {
      Color var7 = var0.getColor();
      var0.translate(var1, var2);
      var0.setColor(var5);
      var0.drawRect(0, 0, var3 - 2, var4 - 2);
      var0.setColor(var6);
      var0.drawLine(1, var4 - 3, 1, 1);
      var0.drawLine(1, 1, var3 - 3, 1);
      var0.drawLine(0, var4 - 1, var3 - 1, var4 - 1);
      var0.drawLine(var3 - 1, var4 - 1, var3 - 1, 0);
      var0.translate(-var1, -var2);
      var0.setColor(var7);
   }

   public static Insets getGrooveInsets() {
      return GROOVE_INSETS;
   }

   public static void drawBezel(Graphics var0, int var1, int var2, int var3, int var4, boolean var5, boolean var6, Color var7, Color var8, Color var9, Color var10) {
      Color var11 = var0.getColor();
      var0.translate(var1, var2);
      if (var5 && var6) {
         var0.setColor(var8);
         var0.drawRect(0, 0, var3 - 1, var4 - 1);
         var0.setColor(var7);
         var0.drawRect(1, 1, var3 - 3, var4 - 3);
      } else if (var5) {
         drawLoweredBezel(var0, var1, var2, var3, var4, var7, var8, var9, var10);
      } else if (var6) {
         var0.setColor(var8);
         var0.drawRect(0, 0, var3 - 1, var4 - 1);
         var0.setColor(var10);
         var0.drawLine(1, 1, 1, var4 - 3);
         var0.drawLine(2, 1, var3 - 3, 1);
         var0.setColor(var9);
         var0.drawLine(2, 2, 2, var4 - 4);
         var0.drawLine(3, 2, var3 - 4, 2);
         var0.setColor(var7);
         var0.drawLine(2, var4 - 3, var3 - 3, var4 - 3);
         var0.drawLine(var3 - 3, 2, var3 - 3, var4 - 4);
         var0.setColor(var8);
         var0.drawLine(1, var4 - 2, var3 - 2, var4 - 2);
         var0.drawLine(var3 - 2, var4 - 2, var3 - 2, 1);
      } else {
         var0.setColor(var10);
         var0.drawLine(0, 0, 0, var4 - 1);
         var0.drawLine(1, 0, var3 - 2, 0);
         var0.setColor(var9);
         var0.drawLine(1, 1, 1, var4 - 3);
         var0.drawLine(2, 1, var3 - 3, 1);
         var0.setColor(var7);
         var0.drawLine(1, var4 - 2, var3 - 2, var4 - 2);
         var0.drawLine(var3 - 2, 1, var3 - 2, var4 - 3);
         var0.setColor(var8);
         var0.drawLine(0, var4 - 1, var3 - 1, var4 - 1);
         var0.drawLine(var3 - 1, var4 - 1, var3 - 1, 0);
      }

      var0.translate(-var1, -var2);
      var0.setColor(var11);
   }

   public static void drawLoweredBezel(Graphics var0, int var1, int var2, int var3, int var4, Color var5, Color var6, Color var7, Color var8) {
      var0.setColor(var6);
      var0.drawLine(0, 0, 0, var4 - 1);
      var0.drawLine(1, 0, var3 - 2, 0);
      var0.setColor(var5);
      var0.drawLine(1, 1, 1, var4 - 2);
      var0.drawLine(1, 1, var3 - 3, 1);
      var0.setColor(var8);
      var0.drawLine(0, var4 - 1, var3 - 1, var4 - 1);
      var0.drawLine(var3 - 1, var4 - 1, var3 - 1, 0);
      var0.setColor(var7);
      var0.drawLine(1, var4 - 2, var3 - 2, var4 - 2);
      var0.drawLine(var3 - 2, var4 - 2, var3 - 2, 1);
   }

   public static void drawString(Graphics var0, String var1, int var2, int var3, int var4) {
      int var5 = -1;
      if (var2 != 0) {
         char var6 = Character.toUpperCase((char)var2);
         char var7 = Character.toLowerCase((char)var2);
         int var8 = var1.indexOf(var6);
         int var9 = var1.indexOf(var7);
         if (var8 == -1) {
            var5 = var9;
         } else if (var9 == -1) {
            var5 = var8;
         } else {
            var5 = var9 < var8 ? var9 : var8;
         }
      }

      drawStringUnderlineCharAt(var0, var1, var5, var3, var4);
   }

   public static void drawStringUnderlineCharAt(Graphics var0, String var1, int var2, int var3, int var4) {
      SwingUtilities2.drawStringUnderlineCharAt((JComponent)null, var0, var1, var2, var3, var4);
   }

   public static void drawDashedRect(Graphics var0, int var1, int var2, int var3, int var4) {
      for(int var5 = var1; var5 < var1 + var3; var5 += 2) {
         var0.fillRect(var5, var2, 1, 1);
         var0.fillRect(var5, var2 + var4 - 1, 1, 1);
      }

      for(int var6 = var2; var6 < var2 + var4; var6 += 2) {
         var0.fillRect(var1, var6, 1, 1);
         var0.fillRect(var1 + var3 - 1, var6, 1, 1);
      }

   }

   public static Dimension getPreferredButtonSize(AbstractButton var0, int var1) {
      if (var0.getComponentCount() > 0) {
         return null;
      } else {
         Icon var2 = var0.getIcon();
         String var3 = var0.getText();
         Font var4 = var0.getFont();
         FontMetrics var5 = var0.getFontMetrics(var4);
         Rectangle var6 = new Rectangle();
         Rectangle var7 = new Rectangle();
         Rectangle var8 = new Rectangle(32767, 32767);
         SwingUtilities.layoutCompoundLabel(var0, var5, var3, var2, var0.getVerticalAlignment(), var0.getHorizontalAlignment(), var0.getVerticalTextPosition(), var0.getHorizontalTextPosition(), var8, var6, var7, var3 == null ? 0 : var1);
         Rectangle var9 = var6.union(var7);
         Insets var10 = var0.getInsets();
         var9.width += var10.left + var10.right;
         var9.height += var10.top + var10.bottom;
         return var9.getSize();
      }
   }

   static boolean isLeftToRight(Component var0) {
      return var0.getComponentOrientation().isLeftToRight();
   }

   static boolean isMenuShortcutKeyDown(InputEvent var0) {
      return (var0.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;
   }
}
