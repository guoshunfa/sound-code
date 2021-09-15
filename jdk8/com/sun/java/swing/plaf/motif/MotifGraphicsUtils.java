package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class MotifGraphicsUtils implements SwingConstants {
   private static final String MAX_ACC_WIDTH = "maxAccWidth";

   static void drawPoint(Graphics var0, int var1, int var2) {
      var0.drawLine(var1, var2, var1, var2);
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

   public static void drawStringInRect(Graphics var0, String var1, int var2, int var3, int var4, int var5, int var6) {
      drawStringInRect((JComponent)null, var0, var1, var2, var3, var4, var5, var6);
   }

   static void drawStringInRect(JComponent var0, Graphics var1, String var2, int var3, int var4, int var5, int var6, int var7) {
      if (var1.getFont() != null) {
         FontMetrics var8 = SwingUtilities2.getFontMetrics(var0, var1);
         if (var8 != null) {
            int var9;
            int var10;
            if (var7 == 0) {
               var9 = SwingUtilities2.stringWidth(var0, var8, var2);
               if (var9 > var5) {
                  var9 = var5;
               }

               var10 = var3 + (var5 - var9) / 2;
            } else if (var7 == 4) {
               var9 = SwingUtilities2.stringWidth(var0, var8, var2);
               if (var9 > var5) {
                  var9 = var5;
               }

               var10 = var3 + var5 - var9;
            } else {
               var10 = var3;
            }

            int var12 = (var6 - var8.getAscent() - var8.getDescent()) / 2;
            if (var12 < 0) {
               var12 = 0;
            }

            int var11 = var4 + var6 - var12 - var8.getDescent();
            SwingUtilities2.drawString(var0, var1, var2, var10, var11);
         }
      }
   }

   public static void paintMenuItem(Graphics var0, JComponent var1, Icon var2, Icon var3, Color var4, Color var5, int var6) {
      JMenuItem var7 = (JMenuItem)var1;
      ButtonModel var8 = var7.getModel();
      Dimension var9 = var7.getSize();
      Insets var10 = var1.getInsets();
      Rectangle var11 = new Rectangle(var9);
      var11.x += var10.left;
      var11.y += var10.top;
      var11.width -= var10.right + var11.x;
      var11.height -= var10.bottom + var11.y;
      Rectangle var12 = new Rectangle();
      Rectangle var13 = new Rectangle();
      Rectangle var14 = new Rectangle();
      Rectangle var15 = new Rectangle();
      Rectangle var16 = new Rectangle();
      Font var17 = var0.getFont();
      Font var18 = var1.getFont();
      var0.setFont(var18);
      FontMetrics var19 = SwingUtilities2.getFontMetrics(var1, var0, var18);
      FontMetrics var20 = SwingUtilities2.getFontMetrics(var1, var0, UIManager.getFont("MenuItem.acceleratorFont"));
      if (var1.isOpaque()) {
         if (!var8.isArmed() && (!(var1 instanceof JMenu) || !var8.isSelected())) {
            var0.setColor(var1.getBackground());
         } else {
            var0.setColor(var4);
         }

         var0.fillRect(0, 0, var9.width, var9.height);
      }

      KeyStroke var21 = var7.getAccelerator();
      String var22 = "";
      if (var21 != null) {
         int var23 = var21.getModifiers();
         if (var23 > 0) {
            var22 = KeyEvent.getKeyModifiersText(var23);
            var22 = var22 + "+";
         }

         var22 = var22 + KeyEvent.getKeyText(var21.getKeyCode());
      }

      String var30 = layoutMenuItem(var1, var19, var7.getText(), var20, var22, var7.getIcon(), var2, var3, var7.getVerticalAlignment(), var7.getHorizontalAlignment(), var7.getVerticalTextPosition(), var7.getHorizontalTextPosition(), var11, var12, var13, var14, var15, var16, var7.getText() == null ? 0 : var6, var6);
      Color var24 = var0.getColor();
      if (var2 != null) {
         if (var8.isArmed() || var1 instanceof JMenu && var8.isSelected()) {
            var0.setColor(var5);
         }

         var2.paintIcon(var1, var0, var15.x, var15.y);
         var0.setColor(var24);
      }

      if (var7.getIcon() != null) {
         Icon var25;
         if (!var8.isEnabled()) {
            var25 = var7.getDisabledIcon();
         } else if (var8.isPressed() && var8.isArmed()) {
            var25 = var7.getPressedIcon();
            if (var25 == null) {
               var25 = var7.getIcon();
            }
         } else {
            var25 = var7.getIcon();
         }

         if (var25 != null) {
            var25.paintIcon(var1, var0, var12.x, var12.y);
         }
      }

      if (var30 != null && !var30.equals("")) {
         View var32 = (View)var1.getClientProperty("html");
         if (var32 != null) {
            var32.paint(var0, var13);
         } else {
            int var26 = var7.getDisplayedMnemonicIndex();
            if (!var8.isEnabled()) {
               var0.setColor(var7.getBackground().brighter());
               SwingUtilities2.drawStringUnderlineCharAt(var7, var0, var30, var26, var13.x, var13.y + var20.getAscent());
               var0.setColor(var7.getBackground().darker());
               SwingUtilities2.drawStringUnderlineCharAt(var7, var0, var30, var26, var13.x - 1, var13.y + var20.getAscent() - 1);
            } else {
               if (!var8.isArmed() && (!(var1 instanceof JMenu) || !var8.isSelected())) {
                  var0.setColor(var7.getForeground());
               } else {
                  var0.setColor(var5);
               }

               SwingUtilities2.drawStringUnderlineCharAt(var7, var0, var30, var26, var13.x, var13.y + var19.getAscent());
            }
         }
      }

      if (var22 != null && !var22.equals("")) {
         int var33 = 0;
         Container var31 = var7.getParent();
         if (var31 != null && var31 instanceof JComponent) {
            JComponent var27 = (JComponent)var31;
            Integer var28 = (Integer)var27.getClientProperty("maxAccWidth");
            int var29 = var28 != null ? var28 : var14.width;
            var33 = var29 - var14.width;
         }

         var0.setFont(UIManager.getFont("MenuItem.acceleratorFont"));
         if (!var8.isEnabled()) {
            var0.setColor(var7.getBackground().brighter());
            SwingUtilities2.drawString(var1, var0, var22, var14.x - var33, var14.y + var19.getAscent());
            var0.setColor(var7.getBackground().darker());
            SwingUtilities2.drawString(var1, var0, var22, var14.x - var33 - 1, var14.y + var19.getAscent() - 1);
         } else {
            if (!var8.isArmed() && (!(var1 instanceof JMenu) || !var8.isSelected())) {
               var0.setColor(var7.getForeground());
            } else {
               var0.setColor(var5);
            }

            SwingUtilities2.drawString(var1, var0, var22, var14.x - var33, var14.y + var20.getAscent());
         }
      }

      if (var3 != null) {
         if (var8.isArmed() || var1 instanceof JMenu && var8.isSelected()) {
            var0.setColor(var5);
         }

         if (!(var7.getParent() instanceof JMenuBar)) {
            var3.paintIcon(var1, var0, var16.x, var16.y);
         }
      }

      var0.setColor(var24);
      var0.setFont(var17);
   }

   private static String layoutMenuItem(JComponent var0, FontMetrics var1, String var2, FontMetrics var3, String var4, Icon var5, Icon var6, Icon var7, int var8, int var9, int var10, int var11, Rectangle var12, Rectangle var13, Rectangle var14, Rectangle var15, Rectangle var16, Rectangle var17, int var18, int var19) {
      SwingUtilities.layoutCompoundLabel(var0, var1, var2, var5, var8, var9, var10, var11, var12, var13, var14, var18);
      if (var4 != null && !var4.equals("")) {
         var15.width = SwingUtilities2.stringWidth(var0, var3, var4);
         var15.height = var3.getHeight();
      } else {
         var15.width = var15.height = 0;
         var4 = "";
      }

      if (var6 != null) {
         var16.width = var6.getIconWidth();
         var16.height = var6.getIconHeight();
      } else {
         var16.width = var16.height = 0;
      }

      if (var7 != null) {
         var17.width = var7.getIconWidth();
         var17.height = var7.getIconHeight();
      } else {
         var17.width = var17.height = 0;
      }

      Rectangle var20 = var13.union(var14);
      if (isLeftToRight(var0)) {
         var14.x += var16.width + var19;
         var13.x += var16.width + var19;
         var15.x = var12.x + var12.width - var17.width - var19 - var15.width;
         var16.x = var12.x;
         var17.x = var12.x + var12.width - var19 - var17.width;
      } else {
         var14.x -= var16.width + var19;
         var13.x -= var16.width + var19;
         var15.x = var12.x + var17.width + var19;
         var16.x = var12.x + var12.width - var16.width;
         var17.x = var12.x + var19;
      }

      var15.y = var20.y + var20.height / 2 - var15.height / 2;
      var17.y = var20.y + var20.height / 2 - var17.height / 2;
      var16.y = var20.y + var20.height / 2 - var16.height / 2;
      return var2;
   }

   private static void drawMenuBezel(Graphics var0, Color var1, int var2, int var3, int var4, int var5) {
      var0.setColor(var1);
      var0.fillRect(var2, var3, var4, var5);
      var0.setColor(var1.brighter().brighter());
      var0.drawLine(var2 + 1, var3 + var5 - 1, var2 + var4 - 1, var3 + var5 - 1);
      var0.drawLine(var2 + var4 - 1, var3 + var5 - 2, var2 + var4 - 1, var3 + 1);
      var0.setColor(var1.darker().darker());
      var0.drawLine(var2, var3, var2 + var4 - 2, var3);
      var0.drawLine(var2, var3 + 1, var2, var3 + var5 - 2);
   }

   static boolean isLeftToRight(Component var0) {
      return var0.getComponentOrientation().isLeftToRight();
   }
}
