package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

public class MotifIconFactory implements Serializable {
   private static Icon checkBoxIcon;
   private static Icon radioButtonIcon;
   private static Icon menuItemCheckIcon;
   private static Icon menuItemArrowIcon;
   private static Icon menuArrowIcon;

   public static Icon getMenuItemCheckIcon() {
      return null;
   }

   public static Icon getMenuItemArrowIcon() {
      if (menuItemArrowIcon == null) {
         menuItemArrowIcon = new MotifIconFactory.MenuItemArrowIcon();
      }

      return menuItemArrowIcon;
   }

   public static Icon getMenuArrowIcon() {
      if (menuArrowIcon == null) {
         menuArrowIcon = new MotifIconFactory.MenuArrowIcon();
      }

      return menuArrowIcon;
   }

   public static Icon getCheckBoxIcon() {
      if (checkBoxIcon == null) {
         checkBoxIcon = new MotifIconFactory.CheckBoxIcon();
      }

      return checkBoxIcon;
   }

   public static Icon getRadioButtonIcon() {
      if (radioButtonIcon == null) {
         radioButtonIcon = new MotifIconFactory.RadioButtonIcon();
      }

      return radioButtonIcon;
   }

   private static class MenuArrowIcon implements Icon, UIResource, Serializable {
      private Color focus;
      private Color shadow;
      private Color highlight;

      private MenuArrowIcon() {
         this.focus = UIManager.getColor("windowBorder");
         this.shadow = UIManager.getColor("controlShadow");
         this.highlight = UIManager.getColor("controlHighlight");
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         AbstractButton var5 = (AbstractButton)var1;
         ButtonModel var6 = var5.getModel();
         int var7 = this.getIconWidth();
         int var8 = this.getIconHeight();
         Color var9 = var2.getColor();
         if (var6.isSelected()) {
            if (MotifGraphicsUtils.isLeftToRight(var1)) {
               var2.setColor(this.shadow);
               var2.fillRect(var3 + 1, var4 + 1, 2, var8);
               var2.drawLine(var3 + 4, var4 + 2, var3 + 4, var4 + 2);
               var2.drawLine(var3 + 6, var4 + 3, var3 + 6, var4 + 3);
               var2.drawLine(var3 + 8, var4 + 4, var3 + 8, var4 + 5);
               var2.setColor(this.focus);
               var2.fillRect(var3 + 2, var4 + 2, 2, var8 - 2);
               var2.fillRect(var3 + 4, var4 + 3, 2, var8 - 4);
               var2.fillRect(var3 + 6, var4 + 4, 2, var8 - 6);
               var2.setColor(this.highlight);
               var2.drawLine(var3 + 2, var4 + var8, var3 + 2, var4 + var8);
               var2.drawLine(var3 + 4, var4 + var8 - 1, var3 + 4, var4 + var8 - 1);
               var2.drawLine(var3 + 6, var4 + var8 - 2, var3 + 6, var4 + var8 - 2);
               var2.drawLine(var3 + 8, var4 + var8 - 4, var3 + 8, var4 + var8 - 3);
            } else {
               var2.setColor(this.highlight);
               var2.fillRect(var3 + 7, var4 + 1, 2, 10);
               var2.drawLine(var3 + 5, var4 + 9, var3 + 5, var4 + 9);
               var2.drawLine(var3 + 3, var4 + 8, var3 + 3, var4 + 8);
               var2.drawLine(var3 + 1, var4 + 6, var3 + 1, var4 + 7);
               var2.setColor(this.focus);
               var2.fillRect(var3 + 6, var4 + 2, 2, 8);
               var2.fillRect(var3 + 4, var4 + 3, 2, 6);
               var2.fillRect(var3 + 2, var4 + 4, 2, 4);
               var2.setColor(this.shadow);
               var2.drawLine(var3 + 1, var4 + 4, var3 + 1, var4 + 5);
               var2.drawLine(var3 + 3, var4 + 3, var3 + 3, var4 + 3);
               var2.drawLine(var3 + 5, var4 + 2, var3 + 5, var4 + 2);
               var2.drawLine(var3 + 7, var4 + 1, var3 + 7, var4 + 1);
            }
         } else if (MotifGraphicsUtils.isLeftToRight(var1)) {
            var2.setColor(this.highlight);
            var2.drawLine(var3 + 1, var4 + 1, var3 + 1, var4 + var8);
            var2.drawLine(var3 + 2, var4 + 1, var3 + 2, var4 + var8 - 2);
            var2.fillRect(var3 + 3, var4 + 2, 2, 2);
            var2.fillRect(var3 + 5, var4 + 3, 2, 2);
            var2.fillRect(var3 + 7, var4 + 4, 2, 2);
            var2.setColor(this.shadow);
            var2.drawLine(var3 + 2, var4 + var8 - 1, var3 + 2, var4 + var8);
            var2.fillRect(var3 + 3, var4 + var8 - 2, 2, 2);
            var2.fillRect(var3 + 5, var4 + var8 - 3, 2, 2);
            var2.fillRect(var3 + 7, var4 + var8 - 4, 2, 2);
            var2.setColor(var9);
         } else {
            var2.setColor(this.highlight);
            var2.fillRect(var3 + 1, var4 + 4, 2, 2);
            var2.fillRect(var3 + 3, var4 + 3, 2, 2);
            var2.fillRect(var3 + 5, var4 + 2, 2, 2);
            var2.drawLine(var3 + 7, var4 + 1, var3 + 7, var4 + 2);
            var2.setColor(this.shadow);
            var2.fillRect(var3 + 1, var4 + var8 - 4, 2, 2);
            var2.fillRect(var3 + 3, var4 + var8 - 3, 2, 2);
            var2.fillRect(var3 + 5, var4 + var8 - 2, 2, 2);
            var2.drawLine(var3 + 7, var4 + 3, var3 + 7, var4 + var8);
            var2.drawLine(var3 + 8, var4 + 1, var3 + 8, var4 + var8);
            var2.setColor(var9);
         }

      }

      public int getIconWidth() {
         return 10;
      }

      public int getIconHeight() {
         return 10;
      }

      // $FF: synthetic method
      MenuArrowIcon(Object var1) {
         this();
      }
   }

   private static class MenuItemArrowIcon implements Icon, UIResource, Serializable {
      private MenuItemArrowIcon() {
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      }

      public int getIconWidth() {
         return 0;
      }

      public int getIconHeight() {
         return 0;
      }

      // $FF: synthetic method
      MenuItemArrowIcon(Object var1) {
         this();
      }
   }

   private static class MenuItemCheckIcon implements Icon, UIResource, Serializable {
      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      }

      public int getIconWidth() {
         return 0;
      }

      public int getIconHeight() {
         return 0;
      }
   }

   private static class RadioButtonIcon implements Icon, UIResource, Serializable {
      private Color dot;
      private Color highlight;
      private Color shadow;

      private RadioButtonIcon() {
         this.dot = UIManager.getColor("activeCaptionBorder");
         this.highlight = UIManager.getColor("controlHighlight");
         this.shadow = UIManager.getColor("controlShadow");
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         AbstractButton var5 = (AbstractButton)var1;
         ButtonModel var6 = var5.getModel();
         int var7 = this.getIconWidth();
         int var8 = this.getIconHeight();
         boolean var9 = var6.isPressed();
         boolean var10 = var6.isArmed();
         boolean var11 = var6.isEnabled();
         boolean var12 = var6.isSelected();
         boolean var13 = var9 && !var10 && var12 || var9 && var10 && !var12 || !var9 && var10 && var12 || !var9 && !var10 && var12;
         if (var13) {
            var2.setColor(this.shadow);
            var2.drawLine(var3 + 5, var4 + 0, var3 + 8, var4 + 0);
            var2.drawLine(var3 + 3, var4 + 1, var3 + 4, var4 + 1);
            var2.drawLine(var3 + 9, var4 + 1, var3 + 9, var4 + 1);
            var2.drawLine(var3 + 2, var4 + 2, var3 + 2, var4 + 2);
            var2.drawLine(var3 + 1, var4 + 3, var3 + 1, var4 + 3);
            var2.drawLine(var3, var4 + 4, var3, var4 + 9);
            var2.drawLine(var3 + 1, var4 + 10, var3 + 1, var4 + 10);
            var2.drawLine(var3 + 2, var4 + 11, var3 + 2, var4 + 11);
            var2.setColor(this.highlight);
            var2.drawLine(var3 + 3, var4 + 12, var3 + 4, var4 + 12);
            var2.drawLine(var3 + 5, var4 + 13, var3 + 8, var4 + 13);
            var2.drawLine(var3 + 9, var4 + 12, var3 + 10, var4 + 12);
            var2.drawLine(var3 + 11, var4 + 11, var3 + 11, var4 + 11);
            var2.drawLine(var3 + 12, var4 + 10, var3 + 12, var4 + 10);
            var2.drawLine(var3 + 13, var4 + 9, var3 + 13, var4 + 4);
            var2.drawLine(var3 + 12, var4 + 3, var3 + 12, var4 + 3);
            var2.drawLine(var3 + 11, var4 + 2, var3 + 11, var4 + 2);
            var2.drawLine(var3 + 10, var4 + 1, var3 + 10, var4 + 1);
            var2.setColor(this.dot);
            var2.fillRect(var3 + 4, var4 + 5, 6, 4);
            var2.drawLine(var3 + 5, var4 + 4, var3 + 8, var4 + 4);
            var2.drawLine(var3 + 5, var4 + 9, var3 + 8, var4 + 9);
         } else {
            var2.setColor(this.highlight);
            var2.drawLine(var3 + 5, var4 + 0, var3 + 8, var4 + 0);
            var2.drawLine(var3 + 3, var4 + 1, var3 + 4, var4 + 1);
            var2.drawLine(var3 + 9, var4 + 1, var3 + 9, var4 + 1);
            var2.drawLine(var3 + 2, var4 + 2, var3 + 2, var4 + 2);
            var2.drawLine(var3 + 1, var4 + 3, var3 + 1, var4 + 3);
            var2.drawLine(var3, var4 + 4, var3, var4 + 9);
            var2.drawLine(var3 + 1, var4 + 10, var3 + 1, var4 + 10);
            var2.drawLine(var3 + 2, var4 + 11, var3 + 2, var4 + 11);
            var2.setColor(this.shadow);
            var2.drawLine(var3 + 3, var4 + 12, var3 + 4, var4 + 12);
            var2.drawLine(var3 + 5, var4 + 13, var3 + 8, var4 + 13);
            var2.drawLine(var3 + 9, var4 + 12, var3 + 10, var4 + 12);
            var2.drawLine(var3 + 11, var4 + 11, var3 + 11, var4 + 11);
            var2.drawLine(var3 + 12, var4 + 10, var3 + 12, var4 + 10);
            var2.drawLine(var3 + 13, var4 + 9, var3 + 13, var4 + 4);
            var2.drawLine(var3 + 12, var4 + 3, var3 + 12, var4 + 3);
            var2.drawLine(var3 + 11, var4 + 2, var3 + 11, var4 + 2);
            var2.drawLine(var3 + 10, var4 + 1, var3 + 10, var4 + 1);
         }

      }

      public int getIconWidth() {
         return 14;
      }

      public int getIconHeight() {
         return 14;
      }

      // $FF: synthetic method
      RadioButtonIcon(Object var1) {
         this();
      }
   }

   private static class CheckBoxIcon implements Icon, UIResource, Serializable {
      static final int csize = 13;
      private Color control;
      private Color foreground;
      private Color shadow;
      private Color highlight;
      private Color lightShadow;

      private CheckBoxIcon() {
         this.control = UIManager.getColor("control");
         this.foreground = UIManager.getColor("CheckBox.foreground");
         this.shadow = UIManager.getColor("controlShadow");
         this.highlight = UIManager.getColor("controlHighlight");
         this.lightShadow = UIManager.getColor("controlLightShadow");
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         AbstractButton var5 = (AbstractButton)var1;
         ButtonModel var6 = var5.getModel();
         boolean var7 = false;
         if (var5 instanceof JCheckBox) {
            var7 = ((JCheckBox)var5).isBorderPaintedFlat();
         }

         boolean var8 = var6.isPressed();
         boolean var9 = var6.isArmed();
         boolean var10 = var6.isEnabled();
         boolean var11 = var6.isSelected();
         boolean var12 = var8 && !var9 && var11 || var8 && var9 && !var11;
         boolean var13 = var8 && !var9 && !var11 || var8 && var9 && var11;
         boolean var14 = !var8 && var9 && var11 || !var8 && !var9 && var11;
         if (var7) {
            var2.setColor(this.shadow);
            var2.drawRect(var3 + 2, var4, 12, 12);
            if (var13 || var12) {
               var2.setColor(this.control);
               var2.fillRect(var3 + 3, var4 + 1, 11, 11);
            }
         }

         if (var12) {
            this.drawCheckBezel(var2, var3, var4, 13, true, false, false, var7);
         } else if (var13) {
            this.drawCheckBezel(var2, var3, var4, 13, true, true, false, var7);
         } else if (var14) {
            this.drawCheckBezel(var2, var3, var4, 13, false, false, true, var7);
         } else if (!var7) {
            this.drawCheckBezelOut(var2, var3, var4, 13);
         }

      }

      public int getIconWidth() {
         return 13;
      }

      public int getIconHeight() {
         return 13;
      }

      public void drawCheckBezelOut(Graphics var1, int var2, int var3, int var4) {
         Color var5 = UIManager.getColor("controlShadow");
         Color var8 = var1.getColor();
         var1.translate(var2, var3);
         var1.setColor(this.highlight);
         var1.drawLine(0, 0, 0, var4 - 1);
         var1.drawLine(1, 0, var4 - 1, 0);
         var1.setColor(this.shadow);
         var1.drawLine(1, var4 - 1, var4 - 1, var4 - 1);
         var1.drawLine(var4 - 1, var4 - 1, var4 - 1, 1);
         var1.translate(-var2, -var3);
         var1.setColor(var8);
      }

      public void drawCheckBezel(Graphics var1, int var2, int var3, int var4, boolean var5, boolean var6, boolean var7, boolean var8) {
         Color var9 = var1.getColor();
         var1.translate(var2, var3);
         if (!var8) {
            if (var6) {
               var1.setColor(this.control);
               var1.fillRect(1, 1, var4 - 2, var4 - 2);
               var1.setColor(this.shadow);
            } else {
               var1.setColor(this.lightShadow);
               var1.fillRect(0, 0, var4, var4);
               var1.setColor(this.highlight);
            }

            var1.drawLine(1, var4 - 1, var4 - 2, var4 - 1);
            if (var5) {
               var1.drawLine(2, var4 - 2, var4 - 3, var4 - 2);
               var1.drawLine(var4 - 2, 2, var4 - 2, var4 - 1);
               if (var6) {
                  var1.setColor(this.highlight);
               } else {
                  var1.setColor(this.shadow);
               }

               var1.drawLine(1, 2, 1, var4 - 2);
               var1.drawLine(1, 1, var4 - 3, 1);
               if (var6) {
                  var1.setColor(this.shadow);
               } else {
                  var1.setColor(this.highlight);
               }
            }

            var1.drawLine(var4 - 1, 1, var4 - 1, var4 - 1);
            if (var6) {
               var1.setColor(this.highlight);
            } else {
               var1.setColor(this.shadow);
            }

            var1.drawLine(0, 1, 0, var4 - 1);
            var1.drawLine(0, 0, var4 - 1, 0);
         }

         if (var7) {
            var1.setColor(this.foreground);
            var1.drawLine(var4 - 2, 1, var4 - 2, 2);
            var1.drawLine(var4 - 3, 2, var4 - 3, 3);
            var1.drawLine(var4 - 4, 3, var4 - 4, 4);
            var1.drawLine(var4 - 5, 4, var4 - 5, 6);
            var1.drawLine(var4 - 6, 5, var4 - 6, 8);
            var1.drawLine(var4 - 7, 6, var4 - 7, 10);
            var1.drawLine(var4 - 8, 7, var4 - 8, 10);
            var1.drawLine(var4 - 9, 6, var4 - 9, 9);
            var1.drawLine(var4 - 10, 5, var4 - 10, 8);
            var1.drawLine(var4 - 11, 5, var4 - 11, 7);
            var1.drawLine(var4 - 12, 6, var4 - 12, 6);
         }

         var1.translate(-var2, -var3);
         var1.setColor(var9);
      }

      // $FF: synthetic method
      CheckBoxIcon(Object var1) {
         this();
      }
   }
}
