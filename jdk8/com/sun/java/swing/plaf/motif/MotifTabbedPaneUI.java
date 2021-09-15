package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class MotifTabbedPaneUI extends BasicTabbedPaneUI {
   protected Color unselectedTabBackground;
   protected Color unselectedTabForeground;
   protected Color unselectedTabShadow;
   protected Color unselectedTabHighlight;

   public static ComponentUI createUI(JComponent var0) {
      return new MotifTabbedPaneUI();
   }

   protected void installDefaults() {
      super.installDefaults();
      this.unselectedTabBackground = UIManager.getColor("TabbedPane.unselectedTabBackground");
      this.unselectedTabForeground = UIManager.getColor("TabbedPane.unselectedTabForeground");
      this.unselectedTabShadow = UIManager.getColor("TabbedPane.unselectedTabShadow");
      this.unselectedTabHighlight = UIManager.getColor("TabbedPane.unselectedTabHighlight");
   }

   protected void uninstallDefaults() {
      super.uninstallDefaults();
      this.unselectedTabBackground = null;
      this.unselectedTabForeground = null;
      this.unselectedTabShadow = null;
      this.unselectedTabHighlight = null;
   }

   protected void paintContentBorderTopEdge(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      Rectangle var8 = var3 < 0 ? null : this.getTabBounds(var3, this.calcRect);
      var1.setColor(this.lightHighlight);
      if (var2 == 1 && var3 >= 0 && var8.x >= var4 && var8.x <= var4 + var6) {
         var1.drawLine(var4, var5, var8.x - 1, var5);
         if (var8.x + var8.width < var4 + var6 - 2) {
            var1.drawLine(var8.x + var8.width, var5, var4 + var6 - 2, var5);
         }
      } else {
         var1.drawLine(var4, var5, var4 + var6 - 2, var5);
      }

   }

   protected void paintContentBorderBottomEdge(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      Rectangle var8 = var3 < 0 ? null : this.getTabBounds(var3, this.calcRect);
      var1.setColor(this.shadow);
      if (var2 == 3 && var3 >= 0 && var8.x >= var4 && var8.x <= var4 + var6) {
         var1.drawLine(var4 + 1, var5 + var7 - 1, var8.x - 1, var5 + var7 - 1);
         if (var8.x + var8.width < var4 + var6 - 2) {
            var1.drawLine(var8.x + var8.width, var5 + var7 - 1, var4 + var6 - 2, var5 + var7 - 1);
         }
      } else {
         var1.drawLine(var4 + 1, var5 + var7 - 1, var4 + var6 - 1, var5 + var7 - 1);
      }

   }

   protected void paintContentBorderRightEdge(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      Rectangle var8 = var3 < 0 ? null : this.getTabBounds(var3, this.calcRect);
      var1.setColor(this.shadow);
      if (var2 == 4 && var3 >= 0 && var8.y >= var5 && var8.y <= var5 + var7) {
         var1.drawLine(var4 + var6 - 1, var5 + 1, var4 + var6 - 1, var8.y - 1);
         if (var8.y + var8.height < var5 + var7 - 2) {
            var1.drawLine(var4 + var6 - 1, var8.y + var8.height, var4 + var6 - 1, var5 + var7 - 2);
         }
      } else {
         var1.drawLine(var4 + var6 - 1, var5 + 1, var4 + var6 - 1, var5 + var7 - 1);
      }

   }

   protected void paintTabBackground(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      var1.setColor(var8 ? this.tabPane.getBackgroundAt(var3) : this.unselectedTabBackground);
      switch(var2) {
      case 1:
      default:
         var1.fillRect(var4 + 1, var5 + 3, var6 - 2, var7 - 3);
         var1.drawLine(var4 + 2, var5 + 2, var4 + var6 - 3, var5 + 2);
         var1.drawLine(var4 + 3, var5 + 1, var4 + var6 - 4, var5 + 1);
         break;
      case 2:
         var1.fillRect(var4 + 1, var5 + 1, var6 - 1, var7 - 2);
         break;
      case 3:
         var1.fillRect(var4 + 1, var5, var6 - 2, var7 - 3);
         var1.drawLine(var4 + 2, var5 + var7 - 3, var4 + var6 - 3, var5 + var7 - 3);
         var1.drawLine(var4 + 3, var5 + var7 - 2, var4 + var6 - 4, var5 + var7 - 2);
         break;
      case 4:
         var1.fillRect(var4, var5 + 1, var6 - 1, var7 - 2);
      }

   }

   protected void paintTabBorder(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      var1.setColor(var8 ? this.lightHighlight : this.unselectedTabHighlight);
      switch(var2) {
      case 1:
      default:
         var1.drawLine(var4, var5 + 2, var4, var5 + var7 - 1);
         var1.drawLine(var4 + 1, var5 + 1, var4 + 1, var5 + 2);
         var1.drawLine(var4 + 2, var5, var4 + 2, var5 + 1);
         var1.drawLine(var4 + 3, var5, var4 + var6 - 4, var5);
         var1.setColor(var8 ? this.shadow : this.unselectedTabShadow);
         var1.drawLine(var4 + var6 - 3, var5, var4 + var6 - 3, var5 + 1);
         var1.drawLine(var4 + var6 - 2, var5 + 1, var4 + var6 - 2, var5 + 2);
         var1.drawLine(var4 + var6 - 1, var5 + 2, var4 + var6 - 1, var5 + var7 - 1);
         break;
      case 2:
         var1.drawLine(var4, var5 + 2, var4, var5 + var7 - 3);
         var1.drawLine(var4 + 1, var5 + 1, var4 + 1, var5 + 2);
         var1.drawLine(var4 + 2, var5, var4 + 2, var5 + 1);
         var1.drawLine(var4 + 3, var5, var4 + var6 - 1, var5);
         var1.setColor(var8 ? this.shadow : this.unselectedTabShadow);
         var1.drawLine(var4 + 1, var5 + var7 - 3, var4 + 1, var5 + var7 - 2);
         var1.drawLine(var4 + 2, var5 + var7 - 2, var4 + 2, var5 + var7 - 1);
         var1.drawLine(var4 + 3, var5 + var7 - 1, var4 + var6 - 1, var5 + var7 - 1);
         break;
      case 3:
         var1.drawLine(var4, var5, var4, var5 + var7 - 3);
         var1.drawLine(var4 + 1, var5 + var7 - 3, var4 + 1, var5 + var7 - 2);
         var1.drawLine(var4 + 2, var5 + var7 - 2, var4 + 2, var5 + var7 - 1);
         var1.setColor(var8 ? this.shadow : this.unselectedTabShadow);
         var1.drawLine(var4 + 3, var5 + var7 - 1, var4 + var6 - 4, var5 + var7 - 1);
         var1.drawLine(var4 + var6 - 3, var5 + var7 - 2, var4 + var6 - 3, var5 + var7 - 1);
         var1.drawLine(var4 + var6 - 2, var5 + var7 - 3, var4 + var6 - 2, var5 + var7 - 2);
         var1.drawLine(var4 + var6 - 1, var5, var4 + var6 - 1, var5 + var7 - 3);
         break;
      case 4:
         var1.drawLine(var4, var5, var4 + var6 - 3, var5);
         var1.setColor(var8 ? this.shadow : this.unselectedTabShadow);
         var1.drawLine(var4 + var6 - 3, var5, var4 + var6 - 3, var5 + 1);
         var1.drawLine(var4 + var6 - 2, var5 + 1, var4 + var6 - 2, var5 + 2);
         var1.drawLine(var4 + var6 - 1, var5 + 2, var4 + var6 - 1, var5 + var7 - 3);
         var1.drawLine(var4 + var6 - 2, var5 + var7 - 3, var4 + var6 - 2, var5 + var7 - 2);
         var1.drawLine(var4 + var6 - 3, var5 + var7 - 2, var4 + var6 - 3, var5 + var7 - 1);
         var1.drawLine(var4, var5 + var7 - 1, var4 + var6 - 3, var5 + var7 - 1);
      }

   }

   protected void paintFocusIndicator(Graphics var1, int var2, Rectangle[] var3, int var4, Rectangle var5, Rectangle var6, boolean var7) {
      Rectangle var8 = var3[var4];
      if (this.tabPane.hasFocus() && var7) {
         var1.setColor(this.focus);
         int var9;
         int var10;
         int var11;
         int var12;
         switch(var2) {
         case 1:
         default:
            var9 = var8.x + 3;
            var10 = var8.y + 3;
            var11 = var8.width - 7;
            var12 = var8.height - 6;
            break;
         case 2:
            var9 = var8.x + 3;
            var10 = var8.y + 3;
            var11 = var8.width - 6;
            var12 = var8.height - 7;
            break;
         case 3:
            var9 = var8.x + 3;
            var10 = var8.y + 2;
            var11 = var8.width - 7;
            var12 = var8.height - 6;
            break;
         case 4:
            var9 = var8.x + 2;
            var10 = var8.y + 3;
            var11 = var8.width - 6;
            var12 = var8.height - 7;
         }

         var1.drawRect(var9, var10, var11, var12);
      }

   }

   protected int getTabRunIndent(int var1, int var2) {
      return var2 * 3;
   }

   protected int getTabRunOverlay(int var1) {
      this.tabRunOverlay = var1 != 2 && var1 != 4 ? (int)Math.round((double)((float)this.maxTabHeight) * 0.22D) : (int)Math.round((double)((float)this.maxTabWidth) * 0.1D);
      switch(var1) {
      case 1:
         if (this.tabRunOverlay > this.tabInsets.bottom - 2) {
            this.tabRunOverlay = this.tabInsets.bottom - 2;
         }
         break;
      case 2:
         if (this.tabRunOverlay > this.tabInsets.right - 2) {
            this.tabRunOverlay = this.tabInsets.right - 2;
         }
         break;
      case 3:
         if (this.tabRunOverlay > this.tabInsets.top - 2) {
            this.tabRunOverlay = this.tabInsets.top - 2;
         }
         break;
      case 4:
         if (this.tabRunOverlay > this.tabInsets.left - 2) {
            this.tabRunOverlay = this.tabInsets.left - 2;
         }
      }

      return this.tabRunOverlay;
   }
}
