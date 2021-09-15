package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarSeparatorUI;

public class WindowsToolBarSeparatorUI extends BasicToolBarSeparatorUI {
   public static ComponentUI createUI(JComponent var0) {
      return new WindowsToolBarSeparatorUI();
   }

   public Dimension getPreferredSize(JComponent var1) {
      Dimension var2 = ((JToolBar.Separator)var1).getSeparatorSize();
      if (var2 != null) {
         var2 = var2.getSize();
      } else {
         var2 = new Dimension(6, 6);
         XPStyle var3 = XPStyle.getXP();
         if (var3 != null) {
            boolean var4 = ((JSeparator)var1).getOrientation() == 1;
            TMSchema.Part var5 = var4 ? TMSchema.Part.TP_SEPARATOR : TMSchema.Part.TP_SEPARATORVERT;
            XPStyle.Skin var6 = var3.getSkin(var1, var5);
            var2.width = var6.getWidth();
            var2.height = var6.getHeight();
         }

         if (((JSeparator)var1).getOrientation() == 1) {
            var2.height = 0;
         } else {
            var2.width = 0;
         }
      }

      return var2;
   }

   public Dimension getMaximumSize(JComponent var1) {
      Dimension var2 = this.getPreferredSize(var1);
      return ((JSeparator)var1).getOrientation() == 1 ? new Dimension(var2.width, 32767) : new Dimension(32767, var2.height);
   }

   public void paint(Graphics var1, JComponent var2) {
      boolean var3 = ((JSeparator)var2).getOrientation() == 1;
      Dimension var4 = var2.getSize();
      XPStyle var5 = XPStyle.getXP();
      int var10;
      if (var5 != null) {
         TMSchema.Part var6 = var3 ? TMSchema.Part.TP_SEPARATOR : TMSchema.Part.TP_SEPARATORVERT;
         XPStyle.Skin var7 = var5.getSkin(var2, var6);
         int var8 = var3 ? (var4.width - var7.getWidth()) / 2 : 0;
         int var9 = var3 ? 0 : (var4.height - var7.getHeight()) / 2;
         var10 = var3 ? var7.getWidth() : var4.width;
         int var11 = var3 ? var4.height : var7.getHeight();
         var7.paintSkin(var1, var8, var9, var10, var11, (TMSchema.State)null);
      } else {
         Color var12 = var1.getColor();
         UIDefaults var13 = UIManager.getLookAndFeelDefaults();
         Color var14 = var13.getColor("ToolBar.shadow");
         Color var15 = var13.getColor("ToolBar.highlight");
         if (var3) {
            var10 = var4.width / 2 - 1;
            var1.setColor(var14);
            var1.drawLine(var10, 2, var10, var4.height - 2);
            var1.setColor(var15);
            var1.drawLine(var10 + 1, 2, var10 + 1, var4.height - 2);
         } else {
            var10 = var4.height / 2 - 1;
            var1.setColor(var14);
            var1.drawLine(2, var10, var4.width - 2, var10);
            var1.setColor(var15);
            var1.drawLine(2, var10 + 1, var4.width - 2, var10 + 1);
         }

         var1.setColor(var12);
      }

   }
}
