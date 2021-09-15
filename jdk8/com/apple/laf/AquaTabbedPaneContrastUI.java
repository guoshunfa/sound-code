package com.apple.laf;

import apple.laf.JRSUIConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class AquaTabbedPaneContrastUI extends AquaTabbedPaneUI {
   public static ComponentUI createUI(JComponent var0) {
      return new AquaTabbedPaneContrastUI();
   }

   protected void paintTitle(Graphics2D var1, Font var2, FontMetrics var3, Rectangle var4, int var5, String var6) {
      View var7 = this.getTextViewForTab(var5);
      if (var7 != null) {
         var7.paint(var1, var4);
      } else if (var6 != null) {
         Color var8 = this.tabPane.getForegroundAt(var5);
         if (var8 instanceof UIResource) {
            var1.setColor(getNonSelectedTabTitleColor());
            if (this.tabPane.getSelectedIndex() == var5) {
               boolean var9 = this.isPressedAt(var5);
               boolean var10 = this.tabPane.isEnabled() && this.tabPane.isEnabledAt(var5);
               Color var11 = getSelectedTabTitleColor(var10, var9);
               Color var12 = getSelectedTabTitleShadowColor(var10);
               AquaUtils.paintDropShadowText(var1, this.tabPane, var2, var3, var4.x, var4.y, 0, 1, var11, var12, var6);
               return;
            }
         } else {
            var1.setColor(var8);
         }

         var1.setFont(var2);
         SwingUtilities2.drawString(this.tabPane, var1, (String)var6, var4.x, var4.y + var3.getAscent());
      }
   }

   protected static Color getSelectedTabTitleColor(boolean var0, boolean var1) {
      if (var0 && var1) {
         return UIManager.getColor("TabbedPane.selectedTabTitlePressedColor");
      } else {
         return !var0 ? UIManager.getColor("TabbedPane.selectedTabTitleDisabledColor") : UIManager.getColor("TabbedPane.selectedTabTitleNormalColor");
      }
   }

   protected static Color getSelectedTabTitleShadowColor(boolean var0) {
      return var0 ? UIManager.getColor("TabbedPane.selectedTabTitleShadowNormalColor") : UIManager.getColor("TabbedPane.selectedTabTitleShadowDisabledColor");
   }

   protected static Color getNonSelectedTabTitleColor() {
      return UIManager.getColor("TabbedPane.nonSelectedTabTitleNormalColor");
   }

   protected boolean isPressedAt(int var1) {
      return ((AquaTabbedPaneUI.MouseHandler)this.mouseListener).trackingTab == var1;
   }

   protected boolean shouldRepaintSelectedTabOnMouseDown() {
      return true;
   }

   protected JRSUIConstants.State getState(int var1, boolean var2, boolean var3) {
      if (!var2) {
         return JRSUIConstants.State.INACTIVE;
      } else if (!this.tabPane.isEnabled()) {
         return JRSUIConstants.State.DISABLED;
      } else {
         return this.pressedTab == var1 ? JRSUIConstants.State.PRESSED : JRSUIConstants.State.ACTIVE;
      }
   }

   protected JRSUIConstants.SegmentTrailingSeparator getSegmentTrailingSeparator(int var1, int var2, boolean var3) {
      return this.isTabBeforeSelectedTab(var1, var2, var3) ? JRSUIConstants.SegmentTrailingSeparator.NO : JRSUIConstants.SegmentTrailingSeparator.YES;
   }

   protected JRSUIConstants.SegmentLeadingSeparator getSegmentLeadingSeparator(int var1, int var2, boolean var3) {
      return var1 == var2 ? JRSUIConstants.SegmentLeadingSeparator.YES : JRSUIConstants.SegmentLeadingSeparator.NO;
   }
}
