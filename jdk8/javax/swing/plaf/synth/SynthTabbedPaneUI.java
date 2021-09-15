package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class SynthTabbedPaneUI extends BasicTabbedPaneUI implements PropertyChangeListener, SynthUI {
   private int tabOverlap = 0;
   private boolean extendTabsToBase = false;
   private SynthContext tabAreaContext;
   private SynthContext tabContext;
   private SynthContext tabContentContext;
   private SynthStyle style;
   private SynthStyle tabStyle;
   private SynthStyle tabAreaStyle;
   private SynthStyle tabContentStyle;
   private Rectangle textRect = new Rectangle();
   private Rectangle iconRect = new Rectangle();
   private Rectangle tabAreaBounds = new Rectangle();
   private boolean tabAreaStatesMatchSelectedTab = false;
   private boolean nudgeSelectedLabel = true;
   private boolean selectedTabIsPressed = false;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthTabbedPaneUI();
   }

   private boolean scrollableTabLayoutEnabled() {
      return this.tabPane.getTabLayoutPolicy() == 1;
   }

   protected void installDefaults() {
      this.updateStyle(this.tabPane);
   }

   private void updateStyle(JTabbedPane var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3) {
         this.tabRunOverlay = this.style.getInt(var2, "TabbedPane.tabRunOverlay", 0);
         this.tabOverlap = this.style.getInt(var2, "TabbedPane.tabOverlap", 0);
         this.extendTabsToBase = this.style.getBoolean(var2, "TabbedPane.extendTabsToBase", false);
         this.textIconGap = this.style.getInt(var2, "TabbedPane.textIconGap", 0);
         this.selectedTabPadInsets = (Insets)this.style.get(var2, "TabbedPane.selectedTabPadInsets");
         if (this.selectedTabPadInsets == null) {
            this.selectedTabPadInsets = new Insets(0, 0, 0, 0);
         }

         this.tabAreaStatesMatchSelectedTab = this.style.getBoolean(var2, "TabbedPane.tabAreaStatesMatchSelectedTab", false);
         this.nudgeSelectedLabel = this.style.getBoolean(var2, "TabbedPane.nudgeSelectedLabel", true);
         if (var3 != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
         }
      }

      var2.dispose();
      if (this.tabContext != null) {
         this.tabContext.dispose();
      }

      this.tabContext = this.getContext(var1, Region.TABBED_PANE_TAB, 1);
      this.tabStyle = SynthLookAndFeel.updateStyle(this.tabContext, this);
      this.tabInsets = this.tabStyle.getInsets(this.tabContext, (Insets)null);
      if (this.tabAreaContext != null) {
         this.tabAreaContext.dispose();
      }

      this.tabAreaContext = this.getContext(var1, Region.TABBED_PANE_TAB_AREA, 1);
      this.tabAreaStyle = SynthLookAndFeel.updateStyle(this.tabAreaContext, this);
      this.tabAreaInsets = this.tabAreaStyle.getInsets(this.tabAreaContext, (Insets)null);
      if (this.tabContentContext != null) {
         this.tabContentContext.dispose();
      }

      this.tabContentContext = this.getContext(var1, Region.TABBED_PANE_CONTENT, 1);
      this.tabContentStyle = SynthLookAndFeel.updateStyle(this.tabContentContext, this);
      this.contentBorderInsets = this.tabContentStyle.getInsets(this.tabContentContext, (Insets)null);
   }

   protected void installListeners() {
      super.installListeners();
      this.tabPane.addPropertyChangeListener(this);
   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      this.tabPane.removePropertyChangeListener(this);
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this.tabPane, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
      this.tabStyle.uninstallDefaults(this.tabContext);
      this.tabContext.dispose();
      this.tabContext = null;
      this.tabStyle = null;
      this.tabAreaStyle.uninstallDefaults(this.tabAreaContext);
      this.tabAreaContext.dispose();
      this.tabAreaContext = null;
      this.tabAreaStyle = null;
      this.tabContentStyle.uninstallDefaults(this.tabContentContext);
      this.tabContentContext.dispose();
      this.tabContentContext = null;
      this.tabContentStyle = null;
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, SynthLookAndFeel.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   private SynthContext getContext(JComponent var1, Region var2, int var3) {
      SynthStyle var4 = null;
      if (var2 == Region.TABBED_PANE_TAB) {
         var4 = this.tabStyle;
      } else if (var2 == Region.TABBED_PANE_TAB_AREA) {
         var4 = this.tabAreaStyle;
      } else if (var2 == Region.TABBED_PANE_CONTENT) {
         var4 = this.tabContentStyle;
      }

      return SynthContext.getContext(var1, var2, var4, var3);
   }

   protected JButton createScrollButton(int var1) {
      if (UIManager.getBoolean("TabbedPane.useBasicArrows")) {
         JButton var2 = super.createScrollButton(var1);
         var2.setBorder(BorderFactory.createEmptyBorder());
         return var2;
      } else {
         return new SynthTabbedPaneUI.SynthScrollableTabButton(var1);
      }
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle(this.tabPane);
      }

   }

   protected MouseListener createMouseListener() {
      final MouseListener var1 = super.createMouseListener();
      final MouseMotionListener var2 = (MouseMotionListener)var1;
      return new MouseListener() {
         public void mouseClicked(MouseEvent var1x) {
            var1.mouseClicked(var1x);
         }

         public void mouseEntered(MouseEvent var1x) {
            var1.mouseEntered(var1x);
         }

         public void mouseExited(MouseEvent var1x) {
            var1.mouseExited(var1x);
         }

         public void mousePressed(MouseEvent var1x) {
            if (SynthTabbedPaneUI.this.tabPane.isEnabled()) {
               int var2x = SynthTabbedPaneUI.this.tabForCoordinate(SynthTabbedPaneUI.this.tabPane, var1x.getX(), var1x.getY());
               if (var2x >= 0 && SynthTabbedPaneUI.this.tabPane.isEnabledAt(var2x) && var2x == SynthTabbedPaneUI.this.tabPane.getSelectedIndex()) {
                  SynthTabbedPaneUI.this.selectedTabIsPressed = true;
                  SynthTabbedPaneUI.this.tabPane.repaint();
               }

               var1.mousePressed(var1x);
            }
         }

         public void mouseReleased(MouseEvent var1x) {
            if (SynthTabbedPaneUI.this.selectedTabIsPressed) {
               SynthTabbedPaneUI.this.selectedTabIsPressed = false;
               SynthTabbedPaneUI.this.tabPane.repaint();
            }

            var1.mouseReleased(var1x);
            var2.mouseMoved(var1x);
         }
      };
   }

   protected int getTabLabelShiftX(int var1, int var2, boolean var3) {
      return this.nudgeSelectedLabel ? super.getTabLabelShiftX(var1, var2, var3) : 0;
   }

   protected int getTabLabelShiftY(int var1, int var2, boolean var3) {
      return this.nudgeSelectedLabel ? super.getTabLabelShiftY(var1, var2, var3) : 0;
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintTabbedPaneBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      this.paint(var3, var1);
      var3.dispose();
   }

   protected int getBaseline(int var1) {
      if (this.tabPane.getTabComponentAt(var1) == null && this.getTextViewForTab(var1) == null) {
         String var2 = this.tabPane.getTitleAt(var1);
         Font var3 = this.tabContext.getStyle().getFont(this.tabContext);
         FontMetrics var4 = this.getFontMetrics(var3);
         Icon var5 = this.getIconForTab(var1);
         this.textRect.setBounds(0, 0, 0, 0);
         this.iconRect.setBounds(0, 0, 0, 0);
         this.calcRect.setBounds(0, 0, 32767, this.maxTabHeight);
         this.tabContext.getStyle().getGraphicsUtils(this.tabContext).layoutText(this.tabContext, var4, var2, var5, 0, 0, 10, 0, this.calcRect, this.iconRect, this.textRect, this.textIconGap);
         return this.textRect.y + var4.getAscent() + this.getBaselineOffset();
      } else {
         return super.getBaseline(var1);
      }
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintTabbedPaneBorder(var1, var2, var3, var4, var5, var6);
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      int var3 = this.tabPane.getSelectedIndex();
      int var4 = this.tabPane.getTabPlacement();
      this.ensureCurrentLayout();
      if (!this.scrollableTabLayoutEnabled()) {
         Insets var5 = this.tabPane.getInsets();
         int var6 = var5.left;
         int var7 = var5.top;
         int var8 = this.tabPane.getWidth() - var5.left - var5.right;
         int var9 = this.tabPane.getHeight() - var5.top - var5.bottom;
         int var10;
         switch(var4) {
         case 1:
         default:
            var9 = this.calculateTabAreaHeight(var4, this.runCount, this.maxTabHeight);
            break;
         case 2:
            var8 = this.calculateTabAreaWidth(var4, this.runCount, this.maxTabWidth);
            break;
         case 3:
            var10 = this.calculateTabAreaHeight(var4, this.runCount, this.maxTabHeight);
            var7 = var7 + var9 - var10;
            var9 = var10;
            break;
         case 4:
            var10 = this.calculateTabAreaWidth(var4, this.runCount, this.maxTabWidth);
            var6 = var6 + var8 - var10;
            var8 = var10;
         }

         this.tabAreaBounds.setBounds(var6, var7, var8, var9);
         if (var2.getClipBounds().intersects(this.tabAreaBounds)) {
            this.paintTabArea(this.tabAreaContext, var2, var4, var3, this.tabAreaBounds);
         }
      }

      this.paintContentBorder(this.tabContentContext, var2, var4, var3);
   }

   protected void paintTabArea(Graphics var1, int var2, int var3) {
      Insets var4 = this.tabPane.getInsets();
      int var5 = var4.left;
      int var6 = var4.top;
      int var7 = this.tabPane.getWidth() - var4.left - var4.right;
      int var8 = this.tabPane.getHeight() - var4.top - var4.bottom;
      this.paintTabArea(this.tabAreaContext, var1, var2, var3, new Rectangle(var5, var6, var7, var8));
   }

   private void paintTabArea(SynthContext var1, Graphics var2, int var3, int var4, Rectangle var5) {
      Rectangle var6 = var2.getClipBounds();
      if (this.tabAreaStatesMatchSelectedTab && var4 >= 0) {
         this.updateTabContext(var4, true, this.selectedTabIsPressed, this.getRolloverTab() == var4, this.getFocusIndex() == var4);
         var1.setComponentState(this.tabContext.getComponentState());
      } else {
         var1.setComponentState(1);
      }

      SynthLookAndFeel.updateSubregion(var1, var2, var5);
      var1.getPainter().paintTabbedPaneTabAreaBackground(var1, var2, var5.x, var5.y, var5.width, var5.height, var3);
      var1.getPainter().paintTabbedPaneTabAreaBorder(var1, var2, var5.x, var5.y, var5.width, var5.height, var3);
      int var7 = this.tabPane.getTabCount();
      this.iconRect.setBounds(0, 0, 0, 0);
      this.textRect.setBounds(0, 0, 0, 0);

      for(int var8 = this.runCount - 1; var8 >= 0; --var8) {
         int var9 = this.tabRuns[var8];
         int var10 = this.tabRuns[var8 == this.runCount - 1 ? 0 : var8 + 1];
         int var11 = var10 != 0 ? var10 - 1 : var7 - 1;

         for(int var12 = var9; var12 <= var11; ++var12) {
            if (this.rects[var12].intersects(var6) && var4 != var12) {
               this.paintTab(this.tabContext, var2, var3, this.rects, var12, this.iconRect, this.textRect);
            }
         }
      }

      if (var4 >= 0 && this.rects[var4].intersects(var6)) {
         this.paintTab(this.tabContext, var2, var3, this.rects, var4, this.iconRect, this.textRect);
      }

   }

   protected void setRolloverTab(int var1) {
      int var2 = this.getRolloverTab();
      super.setRolloverTab(var1);
      Rectangle var3 = null;
      if (var2 != var1 && this.tabAreaStatesMatchSelectedTab) {
         this.tabPane.repaint();
      } else {
         if (var2 >= 0 && var2 < this.tabPane.getTabCount()) {
            var3 = this.getTabBounds(this.tabPane, var2);
            if (var3 != null) {
               this.tabPane.repaint(var3);
            }
         }

         if (var1 >= 0) {
            var3 = this.getTabBounds(this.tabPane, var1);
            if (var3 != null) {
               this.tabPane.repaint(var3);
            }
         }
      }

   }

   private void paintTab(SynthContext var1, Graphics var2, int var3, Rectangle[] var4, int var5, Rectangle var6, Rectangle var7) {
      Rectangle var8 = var4[var5];
      int var9 = this.tabPane.getSelectedIndex();
      boolean var10 = var9 == var5;
      this.updateTabContext(var5, var10, var10 && this.selectedTabIsPressed, this.getRolloverTab() == var5, this.getFocusIndex() == var5);
      SynthLookAndFeel.updateSubregion(var1, var2, var8);
      int var11 = var8.x;
      int var12 = var8.y;
      int var13 = var8.height;
      int var14 = var8.width;
      int var15 = this.tabPane.getTabPlacement();
      if (this.extendTabsToBase && this.runCount > 1 && var9 >= 0) {
         Rectangle var16 = var4[var9];
         switch(var15) {
         case 1:
            int var17 = var16.y + var16.height;
            var13 = var17 - var8.y;
            break;
         case 2:
            int var18 = var16.x + var16.width;
            var14 = var18 - var8.x;
            break;
         case 3:
            int var19 = var16.y;
            var13 = var8.y + var8.height - var19;
            var12 = var19;
            break;
         case 4:
            int var20 = var16.x;
            var14 = var8.x + var8.width - var20;
            var11 = var20;
         }
      }

      this.tabContext.getPainter().paintTabbedPaneTabBackground(this.tabContext, var2, var11, var12, var14, var13, var5, var15);
      this.tabContext.getPainter().paintTabbedPaneTabBorder(this.tabContext, var2, var11, var12, var14, var13, var5, var15);
      if (this.tabPane.getTabComponentAt(var5) == null) {
         String var21 = this.tabPane.getTitleAt(var5);
         Font var22 = var1.getStyle().getFont(var1);
         FontMetrics var23 = SwingUtilities2.getFontMetrics(this.tabPane, var2, var22);
         Icon var24 = this.getIconForTab(var5);
         this.layoutLabel(var1, var3, var23, var5, var21, var24, var8, var6, var7, var10);
         this.paintText(var1, var2, var3, var22, var23, var5, var21, var7, var10);
         this.paintIcon(var2, var3, var5, var24, var6, var10);
      }

   }

   private void layoutLabel(SynthContext var1, int var2, FontMetrics var3, int var4, String var5, Icon var6, Rectangle var7, Rectangle var8, Rectangle var9, boolean var10) {
      View var11 = this.getTextViewForTab(var4);
      if (var11 != null) {
         this.tabPane.putClientProperty("html", var11);
      }

      var9.x = var9.y = var8.x = var8.y = 0;
      var1.getStyle().getGraphicsUtils(var1).layoutText(var1, var3, var5, var6, 0, 0, 10, 0, var7, var8, var9, this.textIconGap);
      this.tabPane.putClientProperty("html", (Object)null);
      int var12 = this.getTabLabelShiftX(var2, var4, var10);
      int var13 = this.getTabLabelShiftY(var2, var4, var10);
      var8.x += var12;
      var8.y += var13;
      var9.x += var12;
      var9.y += var13;
   }

   private void paintText(SynthContext var1, Graphics var2, int var3, Font var4, FontMetrics var5, int var6, String var7, Rectangle var8, boolean var9) {
      var2.setFont(var4);
      View var10 = this.getTextViewForTab(var6);
      if (var10 != null) {
         var10.paint(var2, var8);
      } else {
         int var11 = this.tabPane.getDisplayedMnemonicIndexAt(var6);
         var2.setColor(var1.getStyle().getColor(var1, ColorType.TEXT_FOREGROUND));
         var1.getStyle().getGraphicsUtils(var1).paintText(var1, var2, var7, var8, var11);
      }

   }

   private void paintContentBorder(SynthContext var1, Graphics var2, int var3, int var4) {
      int var5 = this.tabPane.getWidth();
      int var6 = this.tabPane.getHeight();
      Insets var7 = this.tabPane.getInsets();
      int var8 = var7.left;
      int var9 = var7.top;
      int var10 = var5 - var7.right - var7.left;
      int var11 = var6 - var7.top - var7.bottom;
      switch(var3) {
      case 1:
      default:
         var9 += this.calculateTabAreaHeight(var3, this.runCount, this.maxTabHeight);
         var11 -= var9 - var7.top;
         break;
      case 2:
         var8 += this.calculateTabAreaWidth(var3, this.runCount, this.maxTabWidth);
         var10 -= var8 - var7.left;
         break;
      case 3:
         var11 -= this.calculateTabAreaHeight(var3, this.runCount, this.maxTabHeight);
         break;
      case 4:
         var10 -= this.calculateTabAreaWidth(var3, this.runCount, this.maxTabWidth);
      }

      SynthLookAndFeel.updateSubregion(var1, var2, new Rectangle(var8, var9, var10, var11));
      var1.getPainter().paintTabbedPaneContentBackground(var1, var2, var8, var9, var10, var11);
      var1.getPainter().paintTabbedPaneContentBorder(var1, var2, var8, var9, var10, var11);
   }

   private void ensureCurrentLayout() {
      if (!this.tabPane.isValid()) {
         this.tabPane.validate();
      }

      if (!this.tabPane.isValid()) {
         BasicTabbedPaneUI.TabbedPaneLayout var1 = (BasicTabbedPaneUI.TabbedPaneLayout)this.tabPane.getLayout();
         var1.calculateLayoutInfo();
      }

   }

   protected int calculateMaxTabHeight(int var1) {
      FontMetrics var2 = this.getFontMetrics(this.tabContext.getStyle().getFont(this.tabContext));
      int var3 = this.tabPane.getTabCount();
      int var4 = 0;
      int var5 = var2.getHeight();

      for(int var6 = 0; var6 < var3; ++var6) {
         var4 = Math.max(this.calculateTabHeight(var1, var6, var5), var4);
      }

      return var4;
   }

   protected int calculateTabWidth(int var1, int var2, FontMetrics var3) {
      Icon var4 = this.getIconForTab(var2);
      Insets var5 = this.getTabInsets(var1, var2);
      int var6 = var5.left + var5.right;
      Component var7 = this.tabPane.getTabComponentAt(var2);
      if (var7 != null) {
         var6 += var7.getPreferredSize().width;
      } else {
         if (var4 != null) {
            var6 += var4.getIconWidth() + this.textIconGap;
         }

         View var8 = this.getTextViewForTab(var2);
         if (var8 != null) {
            var6 += (int)var8.getPreferredSpan(0);
         } else {
            String var9 = this.tabPane.getTitleAt(var2);
            var6 += this.tabContext.getStyle().getGraphicsUtils(this.tabContext).computeStringWidth(this.tabContext, var3.getFont(), var3, var9);
         }
      }

      return var6;
   }

   protected int calculateMaxTabWidth(int var1) {
      FontMetrics var2 = this.getFontMetrics(this.tabContext.getStyle().getFont(this.tabContext));
      int var3 = this.tabPane.getTabCount();
      int var4 = 0;

      for(int var5 = 0; var5 < var3; ++var5) {
         var4 = Math.max(this.calculateTabWidth(var1, var5, var2), var4);
      }

      return var4;
   }

   protected Insets getTabInsets(int var1, int var2) {
      this.updateTabContext(var2, false, false, false, this.getFocusIndex() == var2);
      return this.tabInsets;
   }

   protected FontMetrics getFontMetrics() {
      return this.getFontMetrics(this.tabContext.getStyle().getFont(this.tabContext));
   }

   private FontMetrics getFontMetrics(Font var1) {
      return this.tabPane.getFontMetrics(var1);
   }

   private void updateTabContext(int var1, boolean var2, boolean var3, boolean var4, boolean var5) {
      byte var6 = 0;
      int var7;
      if (this.tabPane.isEnabled() && this.tabPane.isEnabledAt(var1)) {
         if (var2) {
            var7 = var6 | 513;
            if (var4 && UIManager.getBoolean("TabbedPane.isTabRollover")) {
               var7 |= 2;
            }
         } else if (var4) {
            var7 = var6 | 3;
         } else {
            var7 = SynthLookAndFeel.getComponentState(this.tabPane);
            var7 &= -257;
         }
      } else {
         var7 = var6 | 8;
         if (var2) {
            var7 |= 512;
         }
      }

      if (var5 && this.tabPane.hasFocus()) {
         var7 |= 256;
      }

      if (var3) {
         var7 |= 4;
      }

      this.tabContext.setComponentState(var7);
   }

   protected LayoutManager createLayoutManager() {
      return (LayoutManager)(this.tabPane.getTabLayoutPolicy() == 1 ? super.createLayoutManager() : new BasicTabbedPaneUI.TabbedPaneLayout() {
         public void calculateLayoutInfo() {
            super.calculateLayoutInfo();
            if (SynthTabbedPaneUI.this.tabOverlap != 0) {
               int var1 = SynthTabbedPaneUI.this.tabPane.getTabCount();
               boolean var2 = SynthTabbedPaneUI.this.tabPane.getComponentOrientation().isLeftToRight();

               for(int var3 = SynthTabbedPaneUI.this.runCount - 1; var3 >= 0; --var3) {
                  int var4 = SynthTabbedPaneUI.this.tabRuns[var3];
                  int var5 = SynthTabbedPaneUI.this.tabRuns[var3 == SynthTabbedPaneUI.this.runCount - 1 ? 0 : var3 + 1];
                  int var6 = var5 != 0 ? var5 - 1 : var1 - 1;

                  for(int var7 = var4 + 1; var7 <= var6; ++var7) {
                     int var8 = 0;
                     int var9 = 0;
                     switch(SynthTabbedPaneUI.this.tabPane.getTabPlacement()) {
                     case 1:
                     case 3:
                        var8 = var2 ? SynthTabbedPaneUI.this.tabOverlap : -SynthTabbedPaneUI.this.tabOverlap;
                        break;
                     case 2:
                     case 4:
                        var9 = SynthTabbedPaneUI.this.tabOverlap;
                     }

                     Rectangle var10000 = SynthTabbedPaneUI.this.rects[var7];
                     var10000.x += var8;
                     var10000 = SynthTabbedPaneUI.this.rects[var7];
                     var10000.y += var9;
                     var10000 = SynthTabbedPaneUI.this.rects[var7];
                     var10000.width += Math.abs(var8);
                     var10000 = SynthTabbedPaneUI.this.rects[var7];
                     var10000.height += Math.abs(var9);
                  }
               }
            }

         }
      });
   }

   private class SynthScrollableTabButton extends SynthArrowButton implements UIResource {
      public SynthScrollableTabButton(int var2) {
         super(var2);
         this.setName("TabbedPane.button");
      }
   }
}
