package com.apple.laf;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicToolBarUI;

public class AquaToolBarUI extends BasicToolBarUI implements SwingConstants {
   private static AquaUtils.RecyclableSingleton<AquaToolBarUI.ToolBarBorder> toolBarBorder = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaToolBarUI.ToolBarBorder.class);

   public static Border getToolBarBorder() {
      return (Border)toolBarBorder.get();
   }

   public static ComponentUI createUI(JComponent var0) {
      return new AquaToolBarUI();
   }

   protected void setBorderToNonRollover(Component var1) {
   }

   protected void setBorderToNormal(Component var1) {
   }

   protected void setBorderToRollover(Component var1) {
   }

   protected RootPaneContainer createFloatingWindow(JToolBar var1) {
      RootPaneContainer var2 = super.createFloatingWindow(var1);
      var2.getRootPane().putClientProperty("Window.style", "small");
      return var2;
   }

   public final void update(Graphics var1, JComponent var2) {
      if (var2.isOpaque()) {
         AquaUtils.fillRect(var1, var2);
      }

      this.paint(var1, var2);
   }

   static class ToolBarBorder extends AbstractBorder implements UIResource, SwingConstants {
      protected void fillHandle(Graphics var1, int var2, int var3, int var4, int var5, boolean var6) {
         var1.setColor(UIManager.getColor("ToolBar.borderHandleColor"));
         int var7;
         if (var6) {
            var7 = var5 - var3 - 2;
            var1.fillRect(var2 + 2, var3 + 1, 1, var7);
            var1.fillRect(var2 + 5, var3 + 1, 1, var7);
         } else {
            var7 = var4 - var2 - 2;
            var1.fillRect(var2 + 1, var3 + 2, var7, 1);
            var1.fillRect(var2 + 1, var3 + 5, var7, 1);
         }

      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         var2.translate(var3, var4);
         if (var1.isOpaque()) {
            AquaUtils.fillRect(var2, var1, var1.getBackground(), 0, 0, var5 - 1, var6 - 1);
         }

         Color var7 = var2.getColor();
         JToolBar var8 = (JToolBar)var1;
         ComponentOrientation var9 = var8.getComponentOrientation();
         boolean var10 = var8.getOrientation() == 0;
         if (var8.isFloatable()) {
            if (var10) {
               if (var9.isLeftToRight()) {
                  this.fillHandle(var2, 2, 2, 10, var6 - 2, true);
               } else {
                  this.fillHandle(var2, var5 - 10, 2, var5 - 2, var6 - 2, true);
               }
            } else {
               this.fillHandle(var2, 2, 2, var5 - 2, 10, false);
            }
         }

         var2.setColor(var7);
         var2.translate(-var3, -var4);
      }

      public Insets getBorderInsets(Component var1) {
         Insets var2 = new Insets(5, 5, 5, 5);
         return this.getBorderInsets(var1, var2);
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         var2.left = 4;
         var2.right = 4;
         var2.top = 2;
         var2.bottom = 2;
         if (((JToolBar)var1).isFloatable()) {
            if (((JToolBar)var1).getOrientation() == 0) {
               var2.left = 12;
            } else {
               var2.top = 12;
            }
         }

         Insets var3 = ((JToolBar)var1).getMargin();
         if (var3 != null) {
            var2.left += var3.left;
            var2.top += var3.top;
            var2.right += var3.right;
            var2.bottom += var3.bottom;
         }

         return var2;
      }

      public boolean isBorderOpaque() {
         return true;
      }
   }
}
