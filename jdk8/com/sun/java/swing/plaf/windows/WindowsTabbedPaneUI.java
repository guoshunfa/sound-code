package com.sun.java.swing.plaf.windows;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class WindowsTabbedPaneUI extends BasicTabbedPaneUI {
   private static Set<KeyStroke> managingFocusForwardTraversalKeys;
   private static Set<KeyStroke> managingFocusBackwardTraversalKeys;
   private boolean contentOpaque = true;

   protected void installDefaults() {
      super.installDefaults();
      this.contentOpaque = UIManager.getBoolean("TabbedPane.contentOpaque");
      if (managingFocusForwardTraversalKeys == null) {
         managingFocusForwardTraversalKeys = new HashSet();
         managingFocusForwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 0));
      }

      this.tabPane.setFocusTraversalKeys(0, managingFocusForwardTraversalKeys);
      if (managingFocusBackwardTraversalKeys == null) {
         managingFocusBackwardTraversalKeys = new HashSet();
         managingFocusBackwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 1));
      }

      this.tabPane.setFocusTraversalKeys(1, managingFocusBackwardTraversalKeys);
   }

   protected void uninstallDefaults() {
      this.tabPane.setFocusTraversalKeys(0, (Set)null);
      this.tabPane.setFocusTraversalKeys(1, (Set)null);
      super.uninstallDefaults();
   }

   public static ComponentUI createUI(JComponent var0) {
      return new WindowsTabbedPaneUI();
   }

   protected void setRolloverTab(int var1) {
      if (XPStyle.getXP() != null) {
         int var2 = this.getRolloverTab();
         super.setRolloverTab(var1);
         Rectangle var3 = null;
         Rectangle var4 = null;
         if (var2 >= 0 && var2 < this.tabPane.getTabCount()) {
            var3 = this.getTabBounds(this.tabPane, var2);
         }

         if (var1 >= 0) {
            var4 = this.getTabBounds(this.tabPane, var1);
         }

         if (var3 != null) {
            if (var4 != null) {
               this.tabPane.repaint(var3.union(var4));
            } else {
               this.tabPane.repaint(var3);
            }
         } else if (var4 != null) {
            this.tabPane.repaint(var4);
         }
      }

   }

   protected void paintContentBorder(Graphics var1, int var2, int var3) {
      XPStyle var4 = XPStyle.getXP();
      if (var4 != null && (this.contentOpaque || this.tabPane.isOpaque())) {
         XPStyle.Skin var5 = var4.getSkin(this.tabPane, TMSchema.Part.TABP_PANE);
         if (var5 != null) {
            Insets var6 = this.tabPane.getInsets();
            Insets var7 = UIManager.getInsets("TabbedPane.tabAreaInsets");
            int var8 = var6.left;
            int var9 = var6.top;
            int var10 = this.tabPane.getWidth() - var6.right - var6.left;
            int var11 = this.tabPane.getHeight() - var6.top - var6.bottom;
            int var12;
            if (var2 != 2 && var2 != 4) {
               var12 = this.calculateTabAreaHeight(var2, this.runCount, this.maxTabHeight);
               if (var2 == 1) {
                  var9 += var12 - var7.bottom;
               }

               var11 -= var12 - var7.bottom;
            } else {
               var12 = this.calculateTabAreaWidth(var2, this.runCount, this.maxTabWidth);
               if (var2 == 2) {
                  var8 += var12 - var7.bottom;
               }

               var10 -= var12 - var7.bottom;
            }

            this.paintRotatedSkin(var1, var5, var2, var8, var9, var10, var11, (TMSchema.State)null);
            return;
         }
      }

      super.paintContentBorder(var1, var2, var3);
   }

   protected void paintTabBackground(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      if (XPStyle.getXP() == null) {
         super.paintTabBackground(var1, var2, var3, var4, var5, var6, var7, var8);
      }

   }

   protected void paintTabBorder(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      XPStyle var9 = XPStyle.getXP();
      if (var9 != null) {
         int var11 = this.tabPane.getTabCount();
         int var12 = this.getRunForTab(var11, var3);
         TMSchema.Part var10;
         if (this.tabRuns[var12] == var3) {
            var10 = TMSchema.Part.TABP_TABITEMLEFTEDGE;
         } else if (var11 > 1 && this.lastTabInRun(var11, var12) == var3) {
            var10 = TMSchema.Part.TABP_TABITEMRIGHTEDGE;
            if (var8) {
               if (var2 != 1 && var2 != 3) {
                  ++var7;
               } else {
                  ++var6;
               }
            }
         } else {
            var10 = TMSchema.Part.TABP_TABITEM;
         }

         TMSchema.State var13 = TMSchema.State.NORMAL;
         if (var8) {
            var13 = TMSchema.State.SELECTED;
         } else if (var3 == this.getRolloverTab()) {
            var13 = TMSchema.State.HOT;
         }

         this.paintRotatedSkin(var1, var9.getSkin(this.tabPane, var10), var2, var4, var5, var6, var7, var13);
      } else {
         super.paintTabBorder(var1, var2, var3, var4, var5, var6, var7, var8);
      }

   }

   private void paintRotatedSkin(Graphics var1, XPStyle.Skin var2, int var3, int var4, int var5, int var6, int var7, TMSchema.State var8) {
      Graphics2D var9 = (Graphics2D)var1.create();
      var9.translate(var4, var5);
      switch(var3) {
      case 1:
      default:
         var2.paintSkin(var9, 0, 0, var6, var7, var8);
         break;
      case 2:
         var9.scale(-1.0D, 1.0D);
         var9.rotate(Math.toRadians(90.0D));
         var2.paintSkin(var9, 0, 0, var7, var6, var8);
         break;
      case 3:
         var9.translate(0, var7);
         var9.scale(-1.0D, 1.0D);
         var9.rotate(Math.toRadians(180.0D));
         var2.paintSkin(var9, 0, 0, var6, var7, var8);
         break;
      case 4:
         var9.translate(var6, 0);
         var9.rotate(Math.toRadians(90.0D));
         var2.paintSkin(var9, 0, 0, var7, var6, var8);
      }

      var9.dispose();
   }
}
