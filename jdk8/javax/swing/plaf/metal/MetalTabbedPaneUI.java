package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class MetalTabbedPaneUI extends BasicTabbedPaneUI {
   protected int minTabWidth = 40;
   private Color unselectedBackground;
   protected Color tabAreaBackground;
   protected Color selectColor;
   protected Color selectHighlight;
   private boolean tabsOpaque = true;
   private boolean ocean;
   private Color oceanSelectedBorderColor;

   public static ComponentUI createUI(JComponent var0) {
      return new MetalTabbedPaneUI();
   }

   protected LayoutManager createLayoutManager() {
      return (LayoutManager)(this.tabPane.getTabLayoutPolicy() == 1 ? super.createLayoutManager() : new MetalTabbedPaneUI.TabbedPaneLayout());
   }

   protected void installDefaults() {
      super.installDefaults();
      this.tabAreaBackground = UIManager.getColor("TabbedPane.tabAreaBackground");
      this.selectColor = UIManager.getColor("TabbedPane.selected");
      this.selectHighlight = UIManager.getColor("TabbedPane.selectHighlight");
      this.tabsOpaque = UIManager.getBoolean("TabbedPane.tabsOpaque");
      this.unselectedBackground = UIManager.getColor("TabbedPane.unselectedBackground");
      this.ocean = MetalLookAndFeel.usingOcean();
      if (this.ocean) {
         this.oceanSelectedBorderColor = UIManager.getColor("TabbedPane.borderHightlightColor");
      }

   }

   protected void paintTabBorder(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      int var9 = var5 + (var7 - 1);
      int var10 = var4 + (var6 - 1);
      switch(var2) {
      case 1:
      default:
         this.paintTopTabBorder(var3, var1, var4, var5, var6, var7, var9, var10, var8);
         break;
      case 2:
         this.paintLeftTabBorder(var3, var1, var4, var5, var6, var7, var9, var10, var8);
         break;
      case 3:
         this.paintBottomTabBorder(var3, var1, var4, var5, var6, var7, var9, var10, var8);
         break;
      case 4:
         this.paintRightTabBorder(var3, var1, var4, var5, var6, var7, var9, var10, var8);
      }

   }

   protected void paintTopTabBorder(int var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
      int var10 = this.getRunForTab(this.tabPane.getTabCount(), var1);
      int var11 = this.lastTabInRun(this.tabPane.getTabCount(), var10);
      int var12 = this.tabRuns[var10];
      boolean var13 = MetalUtils.isLeftToRight(this.tabPane);
      int var14 = this.tabPane.getSelectedIndex();
      int var15 = var6 - 1;
      int var16 = var5 - 1;
      if (this.shouldFillGap(var10, var1, var3, var4)) {
         var2.translate(var3, var4);
         if (var13) {
            var2.setColor(this.getColorForGap(var10, var3, var4 + 1));
            var2.fillRect(1, 0, 5, 3);
            var2.fillRect(1, 3, 2, 2);
         } else {
            var2.setColor(this.getColorForGap(var10, var3 + var5 - 1, var4 + 1));
            var2.fillRect(var16 - 5, 0, 5, 3);
            var2.fillRect(var16 - 2, 3, 2, 2);
         }

         var2.translate(-var3, -var4);
      }

      var2.translate(var3, var4);
      if (this.ocean && var9) {
         var2.setColor(this.oceanSelectedBorderColor);
      } else {
         var2.setColor(this.darkShadow);
      }

      if (var13) {
         var2.drawLine(1, 5, 6, 0);
         var2.drawLine(6, 0, var16, 0);
         if (var1 == var11) {
            var2.drawLine(var16, 1, var16, var15);
         }

         if (this.ocean && var1 - 1 == var14 && var10 == this.getRunForTab(this.tabPane.getTabCount(), var14)) {
            var2.setColor(this.oceanSelectedBorderColor);
         }

         if (var1 != this.tabRuns[this.runCount - 1]) {
            if (this.ocean && var9) {
               var2.drawLine(0, 6, 0, var15);
               var2.setColor(this.darkShadow);
               var2.drawLine(0, 0, 0, 5);
            } else {
               var2.drawLine(0, 0, 0, var15);
            }
         } else {
            var2.drawLine(0, 6, 0, var15);
         }
      } else {
         var2.drawLine(var16 - 1, 5, var16 - 6, 0);
         var2.drawLine(var16 - 6, 0, 0, 0);
         if (var1 == var11) {
            var2.drawLine(0, 1, 0, var15);
         }

         if (this.ocean && var1 - 1 == var14 && var10 == this.getRunForTab(this.tabPane.getTabCount(), var14)) {
            var2.setColor(this.oceanSelectedBorderColor);
            var2.drawLine(var16, 0, var16, var15);
         } else if (this.ocean && var9) {
            var2.drawLine(var16, 6, var16, var15);
            if (var1 != 0) {
               var2.setColor(this.darkShadow);
               var2.drawLine(var16, 0, var16, 5);
            }
         } else if (var1 != this.tabRuns[this.runCount - 1]) {
            var2.drawLine(var16, 0, var16, var15);
         } else {
            var2.drawLine(var16, 6, var16, var15);
         }
      }

      var2.setColor(var9 ? this.selectHighlight : this.highlight);
      if (var13) {
         var2.drawLine(1, 6, 6, 1);
         var2.drawLine(6, 1, var1 == var11 ? var16 - 1 : var16, 1);
         var2.drawLine(1, 6, 1, var15);
         if (var1 == var12 && var1 != this.tabRuns[this.runCount - 1]) {
            if (this.tabPane.getSelectedIndex() == this.tabRuns[var10 + 1]) {
               var2.setColor(this.selectHighlight);
            } else {
               var2.setColor(this.highlight);
            }

            var2.drawLine(1, 0, 1, 4);
         }
      } else {
         var2.drawLine(var16 - 1, 6, var16 - 6, 1);
         var2.drawLine(var16 - 6, 1, 1, 1);
         if (var1 == var11) {
            var2.drawLine(1, 1, 1, var15);
         } else {
            var2.drawLine(0, 1, 0, var15);
         }
      }

      var2.translate(-var3, -var4);
   }

   protected boolean shouldFillGap(int var1, int var2, int var3, int var4) {
      boolean var5 = false;
      if (!this.tabsOpaque) {
         return false;
      } else {
         if (var1 == this.runCount - 2) {
            Rectangle var6 = this.getTabBounds(this.tabPane, this.tabPane.getTabCount() - 1);
            Rectangle var7 = this.getTabBounds(this.tabPane, var2);
            int var8;
            if (MetalUtils.isLeftToRight(this.tabPane)) {
               var8 = var6.x + var6.width - 1;
               if (var8 > var7.x + 2) {
                  return true;
               }
            } else {
               var8 = var6.x;
               int var9 = var7.x + var7.width - 1;
               if (var8 < var9 - 2) {
                  return true;
               }
            }
         } else {
            var5 = var1 != this.runCount - 1;
         }

         return var5;
      }
   }

   protected Color getColorForGap(int var1, int var2, int var3) {
      int var5 = this.tabPane.getSelectedIndex();
      int var6 = this.tabRuns[var1 + 1];
      int var7 = this.lastTabInRun(this.tabPane.getTabCount(), var1 + 1);
      boolean var8 = true;

      for(int var9 = var6; var9 <= var7; ++var9) {
         Rectangle var10 = this.getTabBounds(this.tabPane, var9);
         int var11 = var10.x;
         int var12 = var10.x + var10.width - 1;
         if (MetalUtils.isLeftToRight(this.tabPane)) {
            if (var11 <= var2 && var12 - 4 > var2) {
               return var5 == var9 ? this.selectColor : this.getUnselectedBackgroundAt(var9);
            }
         } else if (var11 + 4 < var2 && var12 >= var2) {
            return var5 == var9 ? this.selectColor : this.getUnselectedBackgroundAt(var9);
         }
      }

      return this.tabPane.getBackground();
   }

   protected void paintLeftTabBorder(int var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
      int var10 = this.tabPane.getTabCount();
      int var11 = this.getRunForTab(var10, var1);
      int var12 = this.lastTabInRun(var10, var11);
      int var13 = this.tabRuns[var11];
      var2.translate(var3, var4);
      int var14 = var6 - 1;
      int var15 = var5 - 1;
      if (var1 != var13 && this.tabsOpaque) {
         var2.setColor(this.tabPane.getSelectedIndex() == var1 - 1 ? this.selectColor : this.getUnselectedBackgroundAt(var1 - 1));
         var2.fillRect(2, 0, 4, 3);
         var2.drawLine(2, 3, 2, 3);
      }

      if (this.ocean) {
         var2.setColor((Color)(var9 ? this.selectHighlight : MetalLookAndFeel.getWhite()));
      } else {
         var2.setColor(var9 ? this.selectHighlight : this.highlight);
      }

      var2.drawLine(1, 6, 6, 1);
      var2.drawLine(1, 6, 1, var14);
      var2.drawLine(6, 1, var15, 1);
      if (var1 != var13) {
         if (this.tabPane.getSelectedIndex() == var1 - 1) {
            var2.setColor(this.selectHighlight);
         } else {
            var2.setColor((Color)(this.ocean ? MetalLookAndFeel.getWhite() : this.highlight));
         }

         var2.drawLine(1, 0, 1, 4);
      }

      if (this.ocean) {
         if (var9) {
            var2.setColor(this.oceanSelectedBorderColor);
         } else {
            var2.setColor(this.darkShadow);
         }
      } else {
         var2.setColor(this.darkShadow);
      }

      var2.drawLine(1, 5, 6, 0);
      var2.drawLine(6, 0, var15, 0);
      if (var1 == var12) {
         var2.drawLine(0, var14, var15, var14);
      }

      if (this.ocean) {
         if (this.tabPane.getSelectedIndex() == var1 - 1) {
            var2.drawLine(0, 5, 0, var14);
            var2.setColor(this.oceanSelectedBorderColor);
            var2.drawLine(0, 0, 0, 5);
         } else if (var9) {
            var2.drawLine(0, 6, 0, var14);
            if (var1 != 0) {
               var2.setColor(this.darkShadow);
               var2.drawLine(0, 0, 0, 5);
            }
         } else if (var1 != var13) {
            var2.drawLine(0, 0, 0, var14);
         } else {
            var2.drawLine(0, 6, 0, var14);
         }
      } else if (var1 != var13) {
         var2.drawLine(0, 0, 0, var14);
      } else {
         var2.drawLine(0, 6, 0, var14);
      }

      var2.translate(-var3, -var4);
   }

   protected void paintBottomTabBorder(int var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
      int var10 = this.tabPane.getTabCount();
      int var11 = this.getRunForTab(var10, var1);
      int var12 = this.lastTabInRun(var10, var11);
      int var13 = this.tabRuns[var11];
      boolean var14 = MetalUtils.isLeftToRight(this.tabPane);
      int var15 = var6 - 1;
      int var16 = var5 - 1;
      if (this.shouldFillGap(var11, var1, var3, var4)) {
         var2.translate(var3, var4);
         if (var14) {
            var2.setColor(this.getColorForGap(var11, var3, var4));
            var2.fillRect(1, var15 - 4, 3, 5);
            var2.fillRect(4, var15 - 1, 2, 2);
         } else {
            var2.setColor(this.getColorForGap(var11, var3 + var5 - 1, var4));
            var2.fillRect(var16 - 3, var15 - 3, 3, 4);
            var2.fillRect(var16 - 5, var15 - 1, 2, 2);
            var2.drawLine(var16 - 1, var15 - 4, var16 - 1, var15 - 4);
         }

         var2.translate(-var3, -var4);
      }

      var2.translate(var3, var4);
      if (this.ocean && var9) {
         var2.setColor(this.oceanSelectedBorderColor);
      } else {
         var2.setColor(this.darkShadow);
      }

      if (var14) {
         var2.drawLine(1, var15 - 5, 6, var15);
         var2.drawLine(6, var15, var16, var15);
         if (var1 == var12) {
            var2.drawLine(var16, 0, var16, var15);
         }

         if (this.ocean && var9) {
            var2.drawLine(0, 0, 0, var15 - 6);
            if (var11 == 0 && var1 != 0 || var11 > 0 && var1 != this.tabRuns[var11 - 1]) {
               var2.setColor(this.darkShadow);
               var2.drawLine(0, var15 - 5, 0, var15);
            }
         } else {
            if (this.ocean && var1 == this.tabPane.getSelectedIndex() + 1) {
               var2.setColor(this.oceanSelectedBorderColor);
            }

            if (var1 != this.tabRuns[this.runCount - 1]) {
               var2.drawLine(0, 0, 0, var15);
            } else {
               var2.drawLine(0, 0, 0, var15 - 6);
            }
         }
      } else {
         var2.drawLine(var16 - 1, var15 - 5, var16 - 6, var15);
         var2.drawLine(var16 - 6, var15, 0, var15);
         if (var1 == var12) {
            var2.drawLine(0, 0, 0, var15);
         }

         if (this.ocean && var1 == this.tabPane.getSelectedIndex() + 1) {
            var2.setColor(this.oceanSelectedBorderColor);
            var2.drawLine(var16, 0, var16, var15);
         } else if (this.ocean && var9) {
            var2.drawLine(var16, 0, var16, var15 - 6);
            if (var1 != var13) {
               var2.setColor(this.darkShadow);
               var2.drawLine(var16, var15 - 5, var16, var15);
            }
         } else if (var1 != this.tabRuns[this.runCount - 1]) {
            var2.drawLine(var16, 0, var16, var15);
         } else {
            var2.drawLine(var16, 0, var16, var15 - 6);
         }
      }

      var2.setColor(var9 ? this.selectHighlight : this.highlight);
      if (var14) {
         var2.drawLine(1, var15 - 6, 6, var15 - 1);
         var2.drawLine(1, 0, 1, var15 - 6);
         if (var1 == var13 && var1 != this.tabRuns[this.runCount - 1]) {
            if (this.tabPane.getSelectedIndex() == this.tabRuns[var11 + 1]) {
               var2.setColor(this.selectHighlight);
            } else {
               var2.setColor(this.highlight);
            }

            var2.drawLine(1, var15 - 4, 1, var15);
         }
      } else if (var1 == var12) {
         var2.drawLine(1, 0, 1, var15 - 1);
      } else {
         var2.drawLine(0, 0, 0, var15 - 1);
      }

      var2.translate(-var3, -var4);
   }

   protected void paintRightTabBorder(int var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
      int var10 = this.tabPane.getTabCount();
      int var11 = this.getRunForTab(var10, var1);
      int var12 = this.lastTabInRun(var10, var11);
      int var13 = this.tabRuns[var11];
      var2.translate(var3, var4);
      int var14 = var6 - 1;
      int var15 = var5 - 1;
      if (var1 != var13 && this.tabsOpaque) {
         var2.setColor(this.tabPane.getSelectedIndex() == var1 - 1 ? this.selectColor : this.getUnselectedBackgroundAt(var1 - 1));
         var2.fillRect(var15 - 5, 0, 5, 3);
         var2.fillRect(var15 - 2, 3, 2, 2);
      }

      var2.setColor(var9 ? this.selectHighlight : this.highlight);
      var2.drawLine(var15 - 6, 1, var15 - 1, 6);
      var2.drawLine(0, 1, var15 - 6, 1);
      if (!var9) {
         var2.drawLine(0, 1, 0, var14);
      }

      if (this.ocean && var9) {
         var2.setColor(this.oceanSelectedBorderColor);
      } else {
         var2.setColor(this.darkShadow);
      }

      if (var1 == var12) {
         var2.drawLine(0, var14, var15, var14);
      }

      if (this.ocean && this.tabPane.getSelectedIndex() == var1 - 1) {
         var2.setColor(this.oceanSelectedBorderColor);
      }

      var2.drawLine(var15 - 6, 0, var15, 6);
      var2.drawLine(0, 0, var15 - 6, 0);
      if (this.ocean && var9) {
         var2.drawLine(var15, 6, var15, var14);
         if (var1 != var13) {
            var2.setColor(this.darkShadow);
            var2.drawLine(var15, 0, var15, 5);
         }
      } else if (this.ocean && this.tabPane.getSelectedIndex() == var1 - 1) {
         var2.setColor(this.oceanSelectedBorderColor);
         var2.drawLine(var15, 0, var15, 6);
         var2.setColor(this.darkShadow);
         var2.drawLine(var15, 6, var15, var14);
      } else if (var1 != var13) {
         var2.drawLine(var15, 0, var15, var14);
      } else {
         var2.drawLine(var15, 6, var15, var14);
      }

      var2.translate(-var3, -var4);
   }

   public void update(Graphics var1, JComponent var2) {
      if (var2.isOpaque()) {
         var1.setColor(this.tabAreaBackground);
         var1.fillRect(0, 0, var2.getWidth(), var2.getHeight());
      }

      this.paint(var1, var2);
   }

   protected void paintTabBackground(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      int var9 = var7 / 2;
      if (var8) {
         var1.setColor(this.selectColor);
      } else {
         var1.setColor(this.getUnselectedBackgroundAt(var3));
      }

      if (MetalUtils.isLeftToRight(this.tabPane)) {
         switch(var2) {
         case 1:
         default:
            var1.fillRect(var4 + 4, var5 + 2, var6 - 1 - 3, var7 - 1 - 1);
            var1.fillRect(var4 + 2, var5 + 5, 2, var7 - 5);
            break;
         case 2:
            var1.fillRect(var4 + 5, var5 + 1, var6 - 5, var7 - 1);
            var1.fillRect(var4 + 2, var5 + 4, 3, var7 - 4);
            break;
         case 3:
            var1.fillRect(var4 + 2, var5, var6 - 2, var7 - 4);
            var1.fillRect(var4 + 5, var5 + (var7 - 1) - 3, var6 - 5, 3);
            break;
         case 4:
            var1.fillRect(var4, var5 + 2, var6 - 4, var7 - 2);
            var1.fillRect(var4 + (var6 - 1) - 3, var5 + 5, 3, var7 - 5);
         }
      } else {
         switch(var2) {
         case 1:
         default:
            var1.fillRect(var4, var5 + 2, var6 - 1 - 3, var7 - 1 - 1);
            var1.fillRect(var4 + (var6 - 1) - 3, var5 + 5, 3, var7 - 3);
            break;
         case 2:
            var1.fillRect(var4 + 5, var5 + 1, var6 - 5, var7 - 1);
            var1.fillRect(var4 + 2, var5 + 4, 3, var7 - 4);
            break;
         case 3:
            var1.fillRect(var4, var5, var6 - 5, var7 - 1);
            var1.fillRect(var4 + (var6 - 1) - 4, var5, 4, var7 - 5);
            var1.fillRect(var4 + (var6 - 1) - 4, var5 + (var7 - 1) - 4, 2, 2);
            break;
         case 4:
            var1.fillRect(var4 + 1, var5 + 1, var6 - 5, var7 - 1);
            var1.fillRect(var4 + (var6 - 1) - 3, var5 + 5, 3, var7 - 5);
         }
      }

   }

   protected int getTabLabelShiftX(int var1, int var2, boolean var3) {
      return 0;
   }

   protected int getTabLabelShiftY(int var1, int var2, boolean var3) {
      return 0;
   }

   protected int getBaselineOffset() {
      return 0;
   }

   public void paint(Graphics var1, JComponent var2) {
      int var3 = this.tabPane.getTabPlacement();
      Insets var4 = var2.getInsets();
      Dimension var5 = var2.getSize();
      if (this.tabPane.isOpaque()) {
         Color var6 = var2.getBackground();
         if (var6 instanceof UIResource && this.tabAreaBackground != null) {
            var1.setColor(this.tabAreaBackground);
         } else {
            var1.setColor(var6);
         }

         switch(var3) {
         case 1:
         default:
            var1.fillRect(var4.left, var4.top, var5.width - var4.right - var4.left, this.calculateTabAreaHeight(var3, this.runCount, this.maxTabHeight));
            this.paintHighlightBelowTab();
            break;
         case 2:
            var1.fillRect(var4.left, var4.top, this.calculateTabAreaWidth(var3, this.runCount, this.maxTabWidth), var5.height - var4.bottom - var4.top);
            break;
         case 3:
            int var7 = this.calculateTabAreaHeight(var3, this.runCount, this.maxTabHeight);
            var1.fillRect(var4.left, var5.height - var4.bottom - var7, var5.width - var4.left - var4.right, var7);
            break;
         case 4:
            int var8 = this.calculateTabAreaWidth(var3, this.runCount, this.maxTabWidth);
            var1.fillRect(var5.width - var4.right - var8, var4.top, var8, var5.height - var4.top - var4.bottom);
         }
      }

      super.paint(var1, var2);
   }

   protected void paintHighlightBelowTab() {
   }

   protected void paintFocusIndicator(Graphics var1, int var2, Rectangle[] var3, int var4, Rectangle var5, Rectangle var6, boolean var7) {
      if (this.tabPane.hasFocus() && var7) {
         Rectangle var8 = var3[var4];
         boolean var9 = this.isLastInRun(var4);
         var1.setColor(this.focus);
         var1.translate(var8.x, var8.y);
         int var10 = var8.width - 1;
         int var11 = var8.height - 1;
         boolean var12 = MetalUtils.isLeftToRight(this.tabPane);
         switch(var2) {
         case 1:
         default:
            if (var12) {
               var1.drawLine(2, 6, 6, 2);
               var1.drawLine(2, 6, 2, var11 - 1);
               var1.drawLine(6, 2, var10, 2);
               var1.drawLine(var10, 2, var10, var11 - 1);
               var1.drawLine(2, var11 - 1, var10, var11 - 1);
            } else {
               var1.drawLine(var10 - 2, 6, var10 - 6, 2);
               var1.drawLine(var10 - 2, 6, var10 - 2, var11 - 1);
               if (var9) {
                  var1.drawLine(var10 - 6, 2, 2, 2);
                  var1.drawLine(2, 2, 2, var11 - 1);
                  var1.drawLine(var10 - 2, var11 - 1, 2, var11 - 1);
               } else {
                  var1.drawLine(var10 - 6, 2, 1, 2);
                  var1.drawLine(1, 2, 1, var11 - 1);
                  var1.drawLine(var10 - 2, var11 - 1, 1, var11 - 1);
               }
            }
            break;
         case 2:
            var1.drawLine(2, 6, 6, 2);
            var1.drawLine(2, 6, 2, var11 - 1);
            var1.drawLine(6, 2, var10, 2);
            var1.drawLine(var10, 2, var10, var11 - 1);
            var1.drawLine(2, var11 - 1, var10, var11 - 1);
            break;
         case 3:
            if (var12) {
               var1.drawLine(2, var11 - 6, 6, var11 - 2);
               var1.drawLine(6, var11 - 2, var10, var11 - 2);
               var1.drawLine(2, 0, 2, var11 - 6);
               var1.drawLine(2, 0, var10, 0);
               var1.drawLine(var10, 0, var10, var11 - 2);
            } else {
               var1.drawLine(var10 - 2, var11 - 6, var10 - 6, var11 - 2);
               var1.drawLine(var10 - 2, 0, var10 - 2, var11 - 6);
               if (var9) {
                  var1.drawLine(2, var11 - 2, var10 - 6, var11 - 2);
                  var1.drawLine(2, 0, var10 - 2, 0);
                  var1.drawLine(2, 0, 2, var11 - 2);
               } else {
                  var1.drawLine(1, var11 - 2, var10 - 6, var11 - 2);
                  var1.drawLine(1, 0, var10 - 2, 0);
                  var1.drawLine(1, 0, 1, var11 - 2);
               }
            }
            break;
         case 4:
            var1.drawLine(var10 - 6, 2, var10 - 2, 6);
            var1.drawLine(1, 2, var10 - 6, 2);
            var1.drawLine(var10 - 2, 6, var10 - 2, var11);
            var1.drawLine(1, 2, 1, var11);
            var1.drawLine(1, var11, var10 - 2, var11);
         }

         var1.translate(-var8.x, -var8.y);
      }

   }

   protected void paintContentBorderTopEdge(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      boolean var8 = MetalUtils.isLeftToRight(this.tabPane);
      int var9 = var4 + var6 - 1;
      Rectangle var10 = var3 < 0 ? null : this.getTabBounds(var3, this.calcRect);
      if (this.ocean) {
         var1.setColor(this.oceanSelectedBorderColor);
      } else {
         var1.setColor(this.selectHighlight);
      }

      if (var2 == 1 && var3 >= 0 && var10.y + var10.height + 1 >= var5 && var10.x >= var4 && var10.x <= var4 + var6) {
         boolean var11 = this.isLastInRun(var3);
         if (!var8 && !var11) {
            var1.drawLine(var4, var5, var10.x, var5);
         } else {
            var1.drawLine(var4, var5, var10.x + 1, var5);
         }

         if (var10.x + var10.width < var9 - 1) {
            if (var8 && !var11) {
               var1.drawLine(var10.x + var10.width, var5, var9 - 1, var5);
            } else {
               var1.drawLine(var10.x + var10.width - 1, var5, var9 - 1, var5);
            }
         } else {
            var1.setColor(this.shadow);
            var1.drawLine(var4 + var6 - 2, var5, var4 + var6 - 2, var5);
         }

         if (this.ocean) {
            var1.setColor(MetalLookAndFeel.getWhite());
            if (!var8 && !var11) {
               var1.drawLine(var4, var5 + 1, var10.x, var5 + 1);
            } else {
               var1.drawLine(var4, var5 + 1, var10.x + 1, var5 + 1);
            }

            if (var10.x + var10.width < var9 - 1) {
               if (var8 && !var11) {
                  var1.drawLine(var10.x + var10.width, var5 + 1, var9 - 1, var5 + 1);
               } else {
                  var1.drawLine(var10.x + var10.width - 1, var5 + 1, var9 - 1, var5 + 1);
               }
            } else {
               var1.setColor(this.shadow);
               var1.drawLine(var4 + var6 - 2, var5 + 1, var4 + var6 - 2, var5 + 1);
            }
         }
      } else {
         var1.drawLine(var4, var5, var4 + var6 - 2, var5);
         if (this.ocean && var2 == 1) {
            var1.setColor(MetalLookAndFeel.getWhite());
            var1.drawLine(var4, var5 + 1, var4 + var6 - 2, var5 + 1);
         }
      }

   }

   protected void paintContentBorderBottomEdge(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      boolean var8 = MetalUtils.isLeftToRight(this.tabPane);
      int var9 = var5 + var7 - 1;
      int var10 = var4 + var6 - 1;
      Rectangle var11 = var3 < 0 ? null : this.getTabBounds(var3, this.calcRect);
      var1.setColor(this.darkShadow);
      if (var2 == 3 && var3 >= 0 && var11.y - 1 <= var7 && var11.x >= var4 && var11.x <= var4 + var6) {
         boolean var12 = this.isLastInRun(var3);
         if (this.ocean) {
            var1.setColor(this.oceanSelectedBorderColor);
         }

         if (!var8 && !var12) {
            var1.drawLine(var4, var9, var11.x - 1, var9);
         } else {
            var1.drawLine(var4, var9, var11.x, var9);
         }

         if (var11.x + var11.width < var4 + var6 - 2) {
            if (var8 && !var12) {
               var1.drawLine(var11.x + var11.width, var9, var10, var9);
            } else {
               var1.drawLine(var11.x + var11.width - 1, var9, var10, var9);
            }
         }
      } else {
         if (this.ocean && var2 == 3) {
            var1.setColor(this.oceanSelectedBorderColor);
         }

         var1.drawLine(var4, var5 + var7 - 1, var4 + var6 - 1, var5 + var7 - 1);
      }

   }

   protected void paintContentBorderLeftEdge(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      Rectangle var8 = var3 < 0 ? null : this.getTabBounds(var3, this.calcRect);
      if (this.ocean) {
         var1.setColor(this.oceanSelectedBorderColor);
      } else {
         var1.setColor(this.selectHighlight);
      }

      if (var2 == 2 && var3 >= 0 && var8.x + var8.width + 1 >= var4 && var8.y >= var5 && var8.y <= var5 + var7) {
         var1.drawLine(var4, var5, var4, var8.y + 1);
         if (var8.y + var8.height < var5 + var7 - 2) {
            var1.drawLine(var4, var8.y + var8.height + 1, var4, var5 + var7 + 2);
         }

         if (this.ocean) {
            var1.setColor(MetalLookAndFeel.getWhite());
            var1.drawLine(var4 + 1, var5 + 1, var4 + 1, var8.y + 1);
            if (var8.y + var8.height < var5 + var7 - 2) {
               var1.drawLine(var4 + 1, var8.y + var8.height + 1, var4 + 1, var5 + var7 + 2);
            }
         }
      } else {
         var1.drawLine(var4, var5 + 1, var4, var5 + var7 - 2);
         if (this.ocean && var2 == 2) {
            var1.setColor(MetalLookAndFeel.getWhite());
            var1.drawLine(var4 + 1, var5, var4 + 1, var5 + var7 - 2);
         }
      }

   }

   protected void paintContentBorderRightEdge(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      Rectangle var8 = var3 < 0 ? null : this.getTabBounds(var3, this.calcRect);
      var1.setColor(this.darkShadow);
      if (var2 == 4 && var3 >= 0 && var8.x - 1 <= var6 && var8.y >= var5 && var8.y <= var5 + var7) {
         if (this.ocean) {
            var1.setColor(this.oceanSelectedBorderColor);
         }

         var1.drawLine(var4 + var6 - 1, var5, var4 + var6 - 1, var8.y);
         if (var8.y + var8.height < var5 + var7 - 2) {
            var1.drawLine(var4 + var6 - 1, var8.y + var8.height, var4 + var6 - 1, var5 + var7 - 2);
         }
      } else {
         if (this.ocean && var2 == 4) {
            var1.setColor(this.oceanSelectedBorderColor);
         }

         var1.drawLine(var4 + var6 - 1, var5, var4 + var6 - 1, var5 + var7 - 1);
      }

   }

   protected int calculateMaxTabHeight(int var1) {
      FontMetrics var2 = this.getFontMetrics();
      int var3 = var2.getHeight();
      boolean var4 = false;

      for(int var5 = 0; var5 < this.tabPane.getTabCount(); ++var5) {
         Icon var6 = this.tabPane.getIconAt(var5);
         if (var6 != null && var6.getIconHeight() > var3) {
            var4 = true;
            break;
         }
      }

      return super.calculateMaxTabHeight(var1) - (var4 ? this.tabInsets.top + this.tabInsets.bottom : 0);
   }

   protected int getTabRunOverlay(int var1) {
      if (var1 != 2 && var1 != 4) {
         return 0;
      } else {
         int var2 = this.calculateMaxTabHeight(var1);
         return var2 / 2;
      }
   }

   protected boolean shouldRotateTabRuns(int var1, int var2) {
      return false;
   }

   protected boolean shouldPadTabRun(int var1, int var2) {
      return this.runCount > 1 && var2 < this.runCount - 1;
   }

   private boolean isLastInRun(int var1) {
      int var2 = this.getRunForTab(this.tabPane.getTabCount(), var1);
      int var3 = this.lastTabInRun(this.tabPane.getTabCount(), var2);
      return var1 == var3;
   }

   private Color getUnselectedBackgroundAt(int var1) {
      Color var2 = this.tabPane.getBackgroundAt(var1);
      return var2 instanceof UIResource && this.unselectedBackground != null ? this.unselectedBackground : var2;
   }

   int getRolloverTabIndex() {
      return this.getRolloverTab();
   }

   public class TabbedPaneLayout extends BasicTabbedPaneUI.TabbedPaneLayout {
      public TabbedPaneLayout() {
         MetalTabbedPaneUI.this.getClass();
         super();
      }

      protected void normalizeTabRuns(int var1, int var2, int var3, int var4) {
         if (var1 == 1 || var1 == 3) {
            super.normalizeTabRuns(var1, var2, var3, var4);
         }

      }

      protected void rotateTabRuns(int var1, int var2) {
      }

      protected void padSelectedTab(int var1, int var2) {
      }
   }
}
