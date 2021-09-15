package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import apple.laf.JRSUIStateFactory;
import apple.laf.JRSUIUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class AquaTabbedPaneUI extends AquaTabbedPaneCopyFromBasicUI {
   private static final int kSmallTabHeight = 20;
   private static final int kLargeTabHeight = 23;
   private static final int kMaxIconSize = 16;
   private static final double kNinetyDegrees = 1.5707963267948966D;
   protected final Insets currentContentDrawingInsets = new Insets(0, 0, 0, 0);
   protected final Insets currentContentBorderInsets = new Insets(0, 0, 0, 0);
   protected final Insets contentDrawingInsets = new Insets(0, 0, 0, 0);
   protected int pressedTab = -3;
   protected boolean popupSelectionChanged;
   protected Boolean isDefaultFocusReceiver = null;
   protected boolean hasAvoidedFirstFocus = false;
   protected final AquaTabbedPaneTabState visibleTabState = new AquaTabbedPaneTabState(this);
   protected final AquaPainter<JRSUIState> painter = AquaPainter.create(JRSUIStateFactory.getTab());
   final Rectangle fContentRect = new Rectangle();
   final Rectangle fIconRect = new Rectangle();
   final Rectangle fTextRect = new Rectangle();
   static AquaTabbedPaneUI.AlterRects[] alterRects = new AquaTabbedPaneUI.AlterRects[5];
   private static final int TAB_BORDER_INSET = 9;

   public static ComponentUI createUI(JComponent var0) {
      return new AquaTabbedPaneUI();
   }

   protected void installListeners() {
      super.installListeners();
      if (this.mouseListener != null) {
         this.tabPane.addMouseMotionListener((MouseMotionListener)this.mouseListener);
      }

   }

   protected void installDefaults() {
      super.installDefaults();
      if (this.tabPane.getFont() instanceof UIResource) {
         Boolean var1 = (Boolean)UIManager.get("TabbedPane.useSmallLayout");
         if (var1 != null && var1 == Boolean.TRUE) {
            this.tabPane.setFont(UIManager.getFont("TabbedPane.smallFont"));
            this.painter.state.set(JRSUIConstants.Size.SMALL);
         }
      }

      this.contentDrawingInsets.set(0, 11, 13, 10);
      this.tabPane.setOpaque(false);
   }

   protected void assureRectsCreated(int var1) {
      this.visibleTabState.init(var1);
      super.assureRectsCreated(var1);
   }

   protected void uninstallDefaults() {
      this.contentDrawingInsets.set(0, 0, 0, 0);
   }

   protected MouseListener createMouseListener() {
      return new AquaTabbedPaneUI.MouseHandler();
   }

   protected FocusListener createFocusListener() {
      return new AquaTabbedPaneUI.FocusHandler();
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return new AquaTabbedPaneUI.TabbedPanePropertyChangeHandler();
   }

   protected LayoutManager createLayoutManager() {
      return new AquaTabbedPaneUI.AquaTruncatingTabbedPaneLayout();
   }

   protected boolean shouldRepaintSelectedTabOnMouseDown() {
      return false;
   }

   public void paint(Graphics var1, JComponent var2) {
      this.painter.state.set(this.getDirection());
      int var3 = this.tabPane.getTabPlacement();
      int var4 = this.tabPane.getSelectedIndex();
      this.paintContentBorder(var1, var3, var4);
      this.ensureCurrentLayout();
      Rectangle var5 = var1.getClipBounds();
      boolean var6 = this.tabPane.isEnabled();
      boolean var7 = AquaFocusHandler.isActive(this.tabPane);
      boolean var8 = this.tabPane.getComponentOrientation().isLeftToRight() || var3 == 2 || var3 == 4;
      if (this.visibleTabState.needsScrollTabs()) {
         this.paintScrollingTabs(var1, var5, var3, var4, var6, var7, var8);
      } else {
         this.paintAllTabs(var1, var5, var3, var4, var6, var7, var8);
      }
   }

   protected void paintAllTabs(Graphics var1, Rectangle var2, int var3, int var4, boolean var5, boolean var6, boolean var7) {
      boolean var8 = false;

      for(int var9 = 0; var9 < this.rects.length; ++var9) {
         if (var9 == var4) {
            var8 = true;
         } else if (this.rects[var9].intersects(var2)) {
            this.paintTabNormal(var1, var3, var9, var5, var6, var7);
         }
      }

      if (var8 && this.rects[var4].intersects(var2)) {
         this.paintTabNormal(var1, var3, var4, var5, var6, var7);
      }

   }

   protected void paintScrollingTabs(Graphics var1, Rectangle var2, int var3, int var4, boolean var5, boolean var6, boolean var7) {
      for(int var8 = 0; var8 < this.visibleTabState.getTotal(); ++var8) {
         int var9 = this.visibleTabState.getIndex(var8);
         if (var9 != var4 && this.rects[var9].intersects(var2)) {
            this.paintTabNormal(var1, var3, var9, var5, var6, var7);
         }
      }

      Rectangle var10 = this.visibleTabState.getLeftScrollTabRect();
      if (this.visibleTabState.needsLeftScrollTab() && var10.intersects(var2)) {
         this.paintTabNormalFromRect(var1, var3, var10, -2, this.fIconRect, this.fTextRect, this.visibleTabState.needsLeftScrollTab(), var6, var7);
      }

      Rectangle var11 = this.visibleTabState.getRightScrollTabRect();
      if (this.visibleTabState.needsRightScrollTab() && var11.intersects(var2)) {
         this.paintTabNormalFromRect(var1, var3, var11, -1, this.fIconRect, this.fTextRect, this.visibleTabState.needsRightScrollTab(), var6, var7);
      }

      if (var4 >= 0) {
         this.paintTabNormal(var1, var3, var4, var5, var6, var7);
      }

   }

   private static boolean isScrollTabIndex(int var0) {
      return var0 == -1 || var0 == -2;
   }

   protected static void transposeRect(Rectangle var0) {
      int var1 = var0.y;
      var0.y = var0.x;
      var0.x = var1;
      var1 = var0.width;
      var0.width = var0.height;
      var0.height = var1;
   }

   protected int getTabLabelShiftX(int var1, int var2, boolean var3) {
      Rectangle var4 = var2 >= 0 ? this.rects[var2] : this.visibleTabState.getRightScrollTabRect();
      boolean var5 = false;
      int var6;
      switch(var1) {
      case 1:
      case 3:
      default:
         var6 = var4.width % 2;
         break;
      case 2:
      case 4:
         var6 = var4.height % 2;
      }

      return var6;
   }

   protected int getTabLabelShiftY(int var1, int var2, boolean var3) {
      switch(var1) {
      case 1:
      default:
         return 0;
      case 2:
      case 3:
      case 4:
         return -1;
      }
   }

   protected Icon getIconForScrollTab(int var1, int var2, boolean var3) {
      boolean var4 = !AquaUtils.isLeftToRight(this.tabPane);
      if (var1 == 4) {
         var4 = false;
      }

      if (var1 == 2) {
         var4 = true;
      }

      int var5 = var2 == -1 ? 3 : 7;
      if (var4) {
         if (var5 == 3) {
            var5 = 7;
         } else if (var5 == 7) {
            var5 = 3;
         }
      }

      if (var3) {
         return AquaImageFactory.getArrowIconForDirection(var5);
      } else {
         Image var6 = AquaImageFactory.getArrowImageForDirection(var5);
         return new ImageIcon(AquaUtils.generateDisabledImage(var6));
      }
   }

   protected void paintContents(Graphics var1, int var2, int var3, Rectangle var4, Rectangle var5, Rectangle var6, boolean var7) {
      Shape var8 = var1.getClip();
      var1.clipRect(this.fContentRect.x, this.fContentRect.y, this.fContentRect.width, this.fContentRect.height);
      Component var9;
      String var10;
      Icon var11;
      if (isScrollTabIndex(var3)) {
         var9 = null;
         var10 = null;
         var11 = this.getIconForScrollTab(var2, var3, true);
      } else {
         var9 = this.getTabComponentAt(var3);
         if (var9 == null) {
            var10 = this.tabPane.getTitleAt(var3);
            var11 = this.getIconForTab(var3);
         } else {
            var10 = null;
            var11 = null;
         }
      }

      boolean var12 = var2 == 4 || var2 == 2;
      if (var12) {
         transposeRect(this.fContentRect);
      }

      Font var13 = this.tabPane.getFont();
      FontMetrics var14 = var1.getFontMetrics(var13);
      this.layoutLabel(var2, var14, var3 < 0 ? 0 : var3, var10, var11, this.fContentRect, var5, var6, false);
      if (var12) {
         transposeRect(this.fContentRect);
         transposeRect(var5);
         transposeRect(var6);
      }

      if (!(var1 instanceof Graphics2D)) {
         var1.setClip(var8);
      } else {
         Graphics2D var15 = (Graphics2D)var1;
         AffineTransform var16 = null;
         if (var12) {
            var16 = var15.getTransform();
            this.rotateGraphics(var15, var4, var6, var5, var2);
         }

         if (var9 == null && var3 >= 0) {
            this.paintTitle(var15, var13, var14, var6, var3, var10);
         }

         if (var11 != null) {
            this.paintIcon(var1, var2, var3, var11, var5, var7);
         }

         if (var16 != null) {
            var15.setTransform(var16);
         }

         var1.setClip(var8);
      }
   }

   protected void paintTitle(Graphics2D var1, Font var2, FontMetrics var3, Rectangle var4, int var5, String var6) {
      View var7 = this.getTextViewForTab(var5);
      if (var7 != null) {
         var7.paint(var1, var4);
      } else if (var6 != null) {
         Color var8 = this.tabPane.getForegroundAt(var5);
         if (var8 instanceof UIResource) {
            if (this.tabPane.isEnabledAt(var5)) {
               var1.setColor(Color.black);
            } else {
               var1.setColor(Color.gray);
            }
         } else {
            var1.setColor(var8);
         }

         var1.setFont(var2);
         SwingUtilities2.drawString(this.tabPane, var1, (String)var6, var4.x, var4.y + var3.getAscent());
      }
   }

   protected void rotateGraphics(Graphics2D var1, Rectangle var2, Rectangle var3, Rectangle var4, int var5) {
      boolean var6 = false;
      boolean var7 = false;
      boolean var8 = false;
      boolean var9 = false;
      double var10 = var5 == 2 ? -1.5707963267948966D : 1.5707963267948966D;
      var1.transform(AffineTransform.getRotateInstance(var10, (double)var2.x, (double)var2.y));
      int var13;
      int var14;
      int var15;
      int var16;
      if (var5 == 2) {
         var1.translate(-var2.height - 1, 1);
         var14 = var3.x - var2.x;
         var13 = var2.height + var2.y - (var3.y + var3.height);
         var16 = var4.x - var2.x;
         var15 = var2.height + var2.y - (var4.y + var4.height);
      } else {
         var1.translate(0, -var2.width - 1);
         var13 = var3.y - var2.y;
         var14 = var2.x + var2.width - (var3.x + var3.width);
         var15 = var4.y - var2.y;
         var16 = var2.x + var2.width - (var4.x + var4.width);
      }

      var3.x = var2.x + var13;
      var3.y = var2.y + var14;
      int var12 = var3.height;
      var3.height = var3.width;
      var3.width = var12;
      var4.x = var2.x + var15;
      var4.y = var2.y + var16;
      var12 = var4.height;
      var4.height = var4.width;
      var4.width = var12;
   }

   protected void paintTabNormal(Graphics var1, int var2, int var3, boolean var4, boolean var5, boolean var6) {
      this.paintTabNormalFromRect(var1, var2, this.rects[var3], var3, this.fIconRect, this.fTextRect, var4, var5, var6);
   }

   protected void paintTabNormalFromRect(Graphics var1, int var2, Rectangle var3, int var4, Rectangle var5, Rectangle var6, boolean var7, boolean var8, boolean var9) {
      int var10 = this.tabPane.getSelectedIndex();
      boolean var11 = var10 == var4;
      this.paintCUITab(var1, var2, var3, var11, var8, var9, var4);
      var6.setBounds(var3);
      this.fContentRect.setBounds(var3);
      this.paintContents(var1, var2, var4, var3, var5, var6, var11);
   }

   protected void paintCUITab(Graphics var1, int var2, Rectangle var3, boolean var4, boolean var5, boolean var6, int var7) {
      int var8 = this.tabPane.getTabCount();
      boolean var9 = this.visibleTabState.needsLeftScrollTab();
      boolean var10 = this.visibleTabState.needsRightScrollTab();
      boolean var11 = var7 == 0;
      boolean var12 = var7 == var8 - 1;
      if (var9 || var10) {
         if (var7 == -1) {
            var11 = false;
            var12 = true;
         } else if (var7 == -2) {
            var11 = true;
            var12 = false;
         } else {
            if (var9) {
               var11 = false;
            }

            if (var10) {
               var12 = false;
            }
         }
      }

      if (var2 == 2 || var2 == 4) {
         boolean var13 = var12;
         var12 = var11;
         var11 = var13;
      }

      JRSUIConstants.State var17 = this.getState(var7, var5, var4);
      this.painter.state.set(var17);
      this.painter.state.set(!var4 && (var17 != JRSUIConstants.State.INACTIVE || !var5) ? JRSUIConstants.BooleanValue.NO : JRSUIConstants.BooleanValue.YES);
      this.painter.state.set(getSegmentPosition(var11, var12, var6));
      int var14 = this.tabPane.getSelectedIndex();
      this.painter.state.set(this.getSegmentTrailingSeparator(var7, var14, var6));
      this.painter.state.set(this.getSegmentLeadingSeparator(var7, var14, var6));
      this.painter.state.set(this.tabPane.hasFocus() && var4 ? JRSUIConstants.Focused.YES : JRSUIConstants.Focused.NO);
      this.painter.paint(var1, this.tabPane, var3.x, var3.y, var3.width, var3.height);
      if (!isScrollTabIndex(var7)) {
         Color var15 = this.tabPane.getBackgroundAt(var7);
         if (var15 != null && !(var15 instanceof UIResource)) {
            if (!var6 && (var2 == 1 || var2 == 3)) {
               boolean var16 = var12;
               var12 = var11;
               var11 = var16;
            }

            this.fillTabWithBackground(var1, var3, var2, var11, var12, var15);
         }
      }
   }

   protected JRSUIConstants.Direction getDirection() {
      switch(this.tabPane.getTabPlacement()) {
      case 2:
         return JRSUIConstants.Direction.WEST;
      case 3:
         return JRSUIConstants.Direction.SOUTH;
      case 4:
         return JRSUIConstants.Direction.EAST;
      default:
         return JRSUIConstants.Direction.NORTH;
      }
   }

   protected static JRSUIConstants.SegmentPosition getSegmentPosition(boolean var0, boolean var1, boolean var2) {
      if (var0 && var1) {
         return JRSUIConstants.SegmentPosition.ONLY;
      } else if (var0) {
         return var2 ? JRSUIConstants.SegmentPosition.FIRST : JRSUIConstants.SegmentPosition.LAST;
      } else if (var1) {
         return var2 ? JRSUIConstants.SegmentPosition.LAST : JRSUIConstants.SegmentPosition.FIRST;
      } else {
         return JRSUIConstants.SegmentPosition.MIDDLE;
      }
   }

   protected JRSUIConstants.SegmentTrailingSeparator getSegmentTrailingSeparator(int var1, int var2, boolean var3) {
      return JRSUIConstants.SegmentTrailingSeparator.YES;
   }

   protected JRSUIConstants.SegmentLeadingSeparator getSegmentLeadingSeparator(int var1, int var2, boolean var3) {
      return JRSUIConstants.SegmentLeadingSeparator.NO;
   }

   protected boolean isTabBeforeSelectedTab(int var1, int var2, boolean var3) {
      if (var1 == -2 && this.visibleTabState.getIndex(0) == var2) {
         return true;
      } else {
         int var4 = var3 ? var2 - 1 : var2 + 1;
         return var1 == var4;
      }
   }

   protected JRSUIConstants.State getState(int var1, boolean var2, boolean var3) {
      if (!var2) {
         return JRSUIConstants.State.INACTIVE;
      } else if (!this.tabPane.isEnabled()) {
         return JRSUIConstants.State.DISABLED;
      } else {
         if (JRSUIUtils.TabbedPane.useLegacyTabs()) {
            if (var3) {
               return JRSUIConstants.State.PRESSED;
            }

            if (this.pressedTab == var1) {
               return JRSUIConstants.State.INACTIVE;
            }
         } else {
            if (var3) {
               return JRSUIConstants.State.ACTIVE;
            }

            if (this.pressedTab == var1) {
               return JRSUIConstants.State.PRESSED;
            }
         }

         return JRSUIConstants.State.ACTIVE;
      }
   }

   protected static AquaTabbedPaneUI.AlterRects getAlterationFor(int var0) {
      if (alterRects[var0] != null) {
         return alterRects[var0];
      } else {
         switch(var0) {
         case 1:
         default:
            return alterRects[1] = (new AquaTabbedPaneUI.AlterRects(0, 2, 0, -4)).start(3, 0, -3, 0).end(0, 0, -3, 0);
         case 2:
            return alterRects[2] = (new AquaTabbedPaneUI.AlterRects(2, 0, -4, 1)).start(0, 0, 0, -4).end(0, 3, 0, -3);
         case 3:
            return alterRects[3] = (new AquaTabbedPaneUI.AlterRects(0, 1, 0, -4)).start(3, 0, -3, 0).end(0, 0, -3, 0);
         case 4:
            return alterRects[4] = (new AquaTabbedPaneUI.AlterRects(1, 0, -4, 1)).start(0, 0, 0, -4).end(0, 3, 0, -3);
         }
      }
   }

   protected void fillTabWithBackground(Graphics var1, Rectangle var2, int var3, boolean var4, boolean var5, Color var6) {
      Rectangle var7 = new Rectangle(var2);
      AquaTabbedPaneUI.AlterRects var8 = getAlterationFor(var3);
      AquaTabbedPaneUI.AlterRects.alter(var7, var8.standard);
      if (var4) {
         AquaTabbedPaneUI.AlterRects.alter(var7, var8.first);
      }

      if (var5) {
         AquaTabbedPaneUI.AlterRects.alter(var7, var8.last);
      }

      var1.setColor(new Color(var6.getRed(), var6.getGreen(), var6.getBlue(), (int)((double)var6.getAlpha() * 0.25D)));
      var1.fillRoundRect(var7.x, var7.y, var7.width, var7.height, 3, 1);
   }

   protected Insets getContentBorderInsets(int var1) {
      Insets var2 = this.getContentDrawingInsets(var1);
      rotateInsets(this.contentBorderInsets, this.currentContentBorderInsets, var1);
      Insets var10000 = this.currentContentBorderInsets;
      var10000.left += var2.left;
      var10000 = this.currentContentBorderInsets;
      var10000.right += var2.right;
      var10000 = this.currentContentBorderInsets;
      var10000.top += var2.top;
      var10000 = this.currentContentBorderInsets;
      var10000.bottom += var2.bottom;
      return this.currentContentBorderInsets;
   }

   protected static void rotateInsets(Insets var0, Insets var1, int var2) {
      switch(var2) {
      case 1:
      default:
         var1.top = var0.top;
         var1.left = var0.left;
         var1.bottom = var0.bottom;
         var1.right = var0.right;
         break;
      case 2:
         var1.top = var0.left;
         var1.left = var0.top;
         var1.bottom = var0.right;
         var1.right = var0.bottom;
         break;
      case 3:
         var1.top = var0.bottom;
         var1.left = var0.left;
         var1.bottom = var0.top;
         var1.right = var0.right;
         break;
      case 4:
         var1.top = var0.right;
         var1.left = var0.bottom;
         var1.bottom = var0.left;
         var1.right = var0.top;
      }

   }

   protected Insets getContentDrawingInsets(int var1) {
      rotateInsets(this.contentDrawingInsets, this.currentContentDrawingInsets, var1);
      return this.currentContentDrawingInsets;
   }

   protected Icon getIconForTab(int var1) {
      final Icon var2 = super.getIconForTab(var1);
      if (var2 == null) {
         return null;
      } else {
         int var3 = var2.getIconHeight();
         if (var3 <= 16) {
            return var2;
         } else {
            float var4 = 16.0F / (float)var3;
            int var5 = var2.getIconWidth();
            return new AquaIcon.CachingScalingIcon((int)((float)var5 * var4), 16) {
               Image createImage() {
                  return AquaIcon.getImageForIcon(var2);
               }
            };
         }
      }
   }

   protected void paintContentBorder(Graphics var1, int var2, int var3) {
      int var4 = this.tabPane.getWidth();
      int var5 = this.tabPane.getHeight();
      Insets var6 = this.tabPane.getInsets();
      int var7 = var6.left;
      int var8 = var6.top;
      int var9 = var4 - var6.right - var6.left;
      int var10 = var5 - var6.top - var6.bottom;
      switch(var2) {
      case 1:
         var8 += 9;
         var10 -= 9;
         break;
      case 2:
         var7 += 9;
         var9 -= 9;
         break;
      case 3:
         var10 -= 9;
         break;
      case 4:
         var9 -= 9;
      }

      if (this.tabPane.isOpaque()) {
         var1.setColor(this.tabPane.getBackground());
         var1.fillRect(0, 0, var4, var5);
      }

      AquaGroupBorder.getTabbedPaneGroupBorder().paintBorder(this.tabPane, var1, var7, var8, var9, var10);
   }

   protected void repaintContentBorderEdge() {
      int var1 = this.tabPane.getWidth();
      int var2 = this.tabPane.getHeight();
      Insets var3 = this.tabPane.getInsets();
      int var4 = this.tabPane.getTabPlacement();
      Insets var5 = this.getContentBorderInsets(var4);
      int var6 = var3.left;
      int var7 = var3.top;
      int var8 = var1 - var3.right - var3.left;
      int var9 = var2 - var3.top - var3.bottom;
      switch(var4) {
      case 1:
      default:
         var7 += this.calculateTabAreaHeight(var4, this.runCount, this.maxTabHeight);
         var9 = var5.top;
         break;
      case 2:
         var6 += this.calculateTabAreaWidth(var4, this.runCount, this.maxTabWidth);
         var8 = var5.left;
         break;
      case 3:
         var9 = var5.bottom;
         break;
      case 4:
         var8 = var5.right;
      }

      this.tabPane.repaint(var6, var7, var8, var9);
   }

   public boolean isTabVisible(int var1) {
      if (var1 != -1 && var1 != -2) {
         for(int var2 = 0; var2 < this.visibleTabState.getTotal(); ++var2) {
            if (this.visibleTabState.getIndex(var2) == var1) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public Rectangle getTabBounds(JTabbedPane var1, int var2) {
      return !this.visibleTabState.needsScrollTabs() || !this.visibleTabState.isBefore(var2) && !this.visibleTabState.isAfter(var2) ? super.getTabBounds(var1, var2) : null;
   }

   public int tabForCoordinate(JTabbedPane var1, int var2, int var3) {
      this.ensureCurrentLayout();
      Point var4 = new Point(var2, var3);
      int var5;
      int var6;
      if (this.visibleTabState.needsScrollTabs()) {
         for(var5 = 0; var5 < this.visibleTabState.getTotal(); ++var5) {
            var6 = this.visibleTabState.getIndex(var5);
            if (this.rects[var6].contains(var4.x, var4.y)) {
               return var6;
            }
         }

         if (this.visibleTabState.getRightScrollTabRect().contains(var4.x, var4.y)) {
            return -1;
         }
      } else {
         var5 = this.tabPane.getTabCount();

         for(var6 = 0; var6 < var5; ++var6) {
            if (this.rects[var6].contains(var4.x, var4.y)) {
               return var6;
            }
         }
      }

      return -1;
   }

   protected Insets getTabInsets(int var1, int var2) {
      switch(var1) {
      case 2:
         return UIManager.getInsets("TabbedPane.leftTabInsets");
      case 4:
         return UIManager.getInsets("TabbedPane.rightTabInsets");
      default:
         return this.tabInsets;
      }
   }

   protected int calculateTabHeight(int var1, int var2, int var3) {
      int var4 = super.calculateTabHeight(var1, var2, var3);
      return var4 <= 20 ? 20 : 23;
   }

   protected boolean shouldRotateTabRuns(int var1) {
      return false;
   }

   protected ChangeListener createChangeListener() {
      return new ChangeListener() {
         public void stateChanged(ChangeEvent var1) {
            if (!AquaTabbedPaneUI.this.isTabVisible(AquaTabbedPaneUI.this.tabPane.getSelectedIndex())) {
               AquaTabbedPaneUI.this.popupSelectionChanged = true;
            }

            AquaTabbedPaneUI.this.tabPane.revalidate();
            AquaTabbedPaneUI.this.tabPane.repaint();
         }
      };
   }

   protected class AquaTruncatingTabbedPaneLayout extends AquaTabbedPaneCopyFromBasicUI.TabbedPaneLayout {
      protected AquaTruncatingTabbedPaneLayout() {
         super();
      }

      protected int preferredTabAreaWidth(int var1, int var2) {
         return var1 != 2 && var1 != 4 ? super.preferredTabAreaWidth(var1, var2) : super.preferredTabAreaHeight(var1, var2);
      }

      protected int preferredTabAreaHeight(int var1, int var2) {
         return var1 != 2 && var1 != 4 ? super.preferredTabAreaHeight(var1, var2) : super.preferredTabAreaWidth(var1, var2);
      }

      protected void calculateTabRects(int var1, int var2) {
         if (var2 > 0) {
            this.superCalculateTabRects(var1, var2);
            if (AquaTabbedPaneUI.this.rects.length > 0) {
               AquaTabbedPaneUI.this.visibleTabState.alignRectsRunFor(AquaTabbedPaneUI.this.rects, AquaTabbedPaneUI.this.tabPane.getSize(), var1, AquaUtils.isLeftToRight(AquaTabbedPaneUI.this.tabPane));
            }
         }
      }

      protected void padTabRun(int var1, int var2, int var3, int var4) {
         if (var1 != 1 && var1 != 3) {
            Rectangle var5 = AquaTabbedPaneUI.this.rects[var3];
            int var6 = var5.y + var5.height - AquaTabbedPaneUI.this.rects[var2].y;
            int var7 = var4 - (var5.y + var5.height);
            float var8 = (float)var7 / (float)var6;

            for(int var9 = var2; var9 <= var3; ++var9) {
               Rectangle var10 = AquaTabbedPaneUI.this.rects[var9];
               if (var9 > var2) {
                  var10.y = AquaTabbedPaneUI.this.rects[var9 - 1].y + AquaTabbedPaneUI.this.rects[var9 - 1].height;
               }

               var10.height += Math.round((float)var10.height * var8);
            }

            var5.height = var4 - var5.y;
         } else {
            super.padTabRun(var1, var2, var3, var4);
         }
      }

      protected synchronized void superCalculateTabRects(int var1, int var2) {
         Dimension var3 = AquaTabbedPaneUI.this.tabPane.getSize();
         Insets var4 = AquaTabbedPaneUI.this.tabPane.getInsets();
         Insets var5 = AquaTabbedPaneUI.this.getTabAreaInsets(var1);
         int var6;
         int var7;
         int var8;
         switch(var1) {
         case 1:
         default:
            AquaTabbedPaneUI.this.maxTabHeight = AquaTabbedPaneUI.this.calculateMaxTabHeight(var1);
            var7 = var4.left + var5.left;
            var8 = var4.top + var5.top;
            var6 = var3.width - (var4.right + var5.right);
            break;
         case 2:
            AquaTabbedPaneUI.this.maxTabWidth = AquaTabbedPaneUI.this.calculateMaxTabHeight(var1);
            var7 = var4.left + var5.left;
            var8 = var4.top + var5.top;
            var6 = var3.height - (var4.bottom + var5.bottom);
            break;
         case 3:
            AquaTabbedPaneUI.this.maxTabHeight = AquaTabbedPaneUI.this.calculateMaxTabHeight(var1);
            var7 = var4.left + var5.left;
            var8 = var3.height - var4.bottom - var5.bottom - AquaTabbedPaneUI.this.maxTabHeight;
            var6 = var3.width - (var4.right + var5.right);
            break;
         case 4:
            AquaTabbedPaneUI.this.maxTabWidth = AquaTabbedPaneUI.this.calculateMaxTabHeight(var1);
            var7 = var3.width - var4.right - var5.right - AquaTabbedPaneUI.this.maxTabWidth - 1;
            var8 = var4.top + var5.top;
            var6 = var3.height - (var4.bottom + var5.bottom);
         }

         AquaTabbedPaneUI.this.tabRunOverlay = AquaTabbedPaneUI.this.getTabRunOverlay(var1);
         AquaTabbedPaneUI.this.runCount = 0;
         AquaTabbedPaneUI.this.selectedRun = 0;
         if (var2 != 0) {
            FontMetrics var9 = AquaTabbedPaneUI.this.getFontMetrics();
            boolean var10 = var1 == 2 || var1 == 4;
            int var11 = AquaTabbedPaneUI.this.tabPane.getSelectedIndex();
            AquaTabbedPaneUI.this.visibleTabState.setNeedsScrollers(false);

            int var12;
            for(var12 = 0; var12 < var2; ++var12) {
               Rectangle var13 = AquaTabbedPaneUI.this.rects[var12];
               if (var10) {
                  this.calculateVerticalTabRunRect(var13, var9, var1, var6, var12, var7, var8);
                  if (var13.y + var13.height > var6) {
                     AquaTabbedPaneUI.this.visibleTabState.setNeedsScrollers(true);
                  }
               } else {
                  this.calculateHorizontalTabRunRect(var13, var9, var1, var6, var12, var7, var8);
                  if (var13.x + var13.width > var6) {
                     AquaTabbedPaneUI.this.visibleTabState.setNeedsScrollers(true);
                  }
               }
            }

            AquaTabbedPaneUI.this.visibleTabState.relayoutForScrolling(AquaTabbedPaneUI.this.rects, var7, var8, var6, var11, var10, var2, AquaUtils.isLeftToRight(AquaTabbedPaneUI.this.tabPane));
            if (!AquaUtils.isLeftToRight(AquaTabbedPaneUI.this.tabPane) && !var10) {
               var12 = var3.width - (var4.right + var5.right);

               for(int var14 = 0; var14 < var2; ++var14) {
                  AquaTabbedPaneUI.this.rects[var14].x = var12 - AquaTabbedPaneUI.this.rects[var14].x - AquaTabbedPaneUI.this.rects[var14].width;
               }
            }

         }
      }

      private void calculateHorizontalTabRunRect(Rectangle var1, FontMetrics var2, int var3, int var4, int var5, int var6, int var7) {
         if (var5 > 0) {
            var1.x = AquaTabbedPaneUI.this.rects[var5 - 1].x + AquaTabbedPaneUI.this.rects[var5 - 1].width;
         } else {
            AquaTabbedPaneUI.this.tabRuns[0] = 0;
            AquaTabbedPaneUI.this.runCount = 1;
            AquaTabbedPaneUI.this.maxTabWidth = 0;
            var1.x = var6;
         }

         var1.width = AquaTabbedPaneUI.this.calculateTabWidth(var3, var5, var2);
         AquaTabbedPaneUI.this.maxTabWidth = Math.max(AquaTabbedPaneUI.this.maxTabWidth, var1.width);
         var1.y = var7;
         var1.height = AquaTabbedPaneUI.this.maxTabHeight;
      }

      private void calculateVerticalTabRunRect(Rectangle var1, FontMetrics var2, int var3, int var4, int var5, int var6, int var7) {
         if (var5 > 0) {
            var1.y = AquaTabbedPaneUI.this.rects[var5 - 1].y + AquaTabbedPaneUI.this.rects[var5 - 1].height;
         } else {
            AquaTabbedPaneUI.this.tabRuns[0] = 0;
            AquaTabbedPaneUI.this.runCount = 1;
            AquaTabbedPaneUI.this.maxTabHeight = 0;
            var1.y = var7;
         }

         var1.height = AquaTabbedPaneUI.this.calculateTabWidth(var3, var5, var2);
         AquaTabbedPaneUI.this.maxTabHeight = Math.max(AquaTabbedPaneUI.this.maxTabHeight, var1.height);
         var1.x = var6;
         var1.width = AquaTabbedPaneUI.this.maxTabWidth;
      }

      protected void layoutTabComponents() {
         Container var1 = this.getTabContainer();
         if (var1 != null) {
            int var2 = AquaTabbedPaneUI.this.tabPane.getTabPlacement();
            Rectangle var3 = new Rectangle();
            Point var4 = new Point(-var1.getX(), -var1.getY());

            for(int var5 = 0; var5 < AquaTabbedPaneUI.this.tabPane.getTabCount(); ++var5) {
               Component var6 = AquaTabbedPaneUI.this.getTabComponentAt(var5);
               if (var6 != null) {
                  AquaTabbedPaneUI.this.getTabBounds(var5, var3);
                  Insets var7 = AquaTabbedPaneUI.this.getTabInsets(AquaTabbedPaneUI.this.tabPane.getTabPlacement(), var5);
                  boolean var8 = var5 == AquaTabbedPaneUI.this.tabPane.getSelectedIndex();
                  if (var2 != 1 && var2 != 3) {
                     var3.x += var7.top + var4.x + AquaTabbedPaneUI.this.getTabLabelShiftY(var2, var5, var8) + (var2 == 2 ? 2 : 1);
                     var3.y += var7.left + var4.y + AquaTabbedPaneUI.this.getTabLabelShiftX(var2, var5, var8);
                     var3.width -= var7.top + var7.bottom - 1;
                     var3.height -= var7.left + var7.right;
                  } else {
                     var3.x += var7.left + var4.x + AquaTabbedPaneUI.this.getTabLabelShiftX(var2, var5, var8);
                     var3.y += var7.top + var4.y + AquaTabbedPaneUI.this.getTabLabelShiftY(var2, var5, var8) + 1;
                     var3.width -= var7.left + var7.right;
                     var3.height -= var7.top + var7.bottom - 1;
                  }

                  var6.setBounds(var3);
               }
            }

         }
      }
   }

   public class MouseHandler extends MouseInputAdapter implements ActionListener {
      protected int trackingTab = -3;
      protected Timer popupTimer = new Timer(500, this);

      public MouseHandler() {
         this.popupTimer.setRepeats(false);
      }

      public void mousePressed(MouseEvent var1) {
         JTabbedPane var2 = (JTabbedPane)var1.getSource();
         if (!var2.isEnabled()) {
            this.trackingTab = -3;
         } else {
            Point var3 = var1.getPoint();
            this.trackingTab = this.getCurrentTab(var2, var3);
            if (this.trackingTab == -3 || !AquaTabbedPaneUI.this.shouldRepaintSelectedTabOnMouseDown() && this.trackingTab == var2.getSelectedIndex()) {
               this.trackingTab = -3;
            } else {
               if (this.trackingTab < 0 && this.trackingTab > -3) {
                  this.popupTimer.start();
               }

               AquaTabbedPaneUI.this.pressedTab = this.trackingTab;
               this.repaint(var2, AquaTabbedPaneUI.this.pressedTab);
            }
         }
      }

      public void mouseDragged(MouseEvent var1) {
         if (this.trackingTab >= -2) {
            JTabbedPane var2 = (JTabbedPane)var1.getSource();
            int var3 = this.getCurrentTab(var2, var1.getPoint());
            if (var3 != this.trackingTab) {
               AquaTabbedPaneUI.this.pressedTab = -3;
            } else {
               AquaTabbedPaneUI.this.pressedTab = this.trackingTab;
            }

            if (this.trackingTab < 0 && this.trackingTab > -3) {
               this.popupTimer.start();
            }

            this.repaint(var2, this.trackingTab);
         }
      }

      public void mouseReleased(MouseEvent var1) {
         if (this.trackingTab >= -2) {
            this.popupTimer.stop();
            JTabbedPane var2 = (JTabbedPane)var1.getSource();
            Point var3 = var1.getPoint();
            int var4 = this.getCurrentTab(var2, var3);
            if (this.trackingTab == -1 && var4 == -1) {
               var2.setSelectedIndex(var2.getSelectedIndex() + 1);
            }

            if (this.trackingTab == -2 && var4 == -2) {
               var2.setSelectedIndex(var2.getSelectedIndex() - 1);
            }

            if (this.trackingTab >= 0 && var4 == this.trackingTab) {
               var2.setSelectedIndex(this.trackingTab);
            }

            this.repaint(var2, this.trackingTab);
            AquaTabbedPaneUI.this.pressedTab = -3;
            this.trackingTab = -3;
         }
      }

      public void actionPerformed(ActionEvent var1) {
         if (this.trackingTab == AquaTabbedPaneUI.this.pressedTab) {
            if (this.trackingTab == -1) {
               this.showFullPopup(false);
               this.trackingTab = -3;
            }

            if (this.trackingTab == -2) {
               this.showFullPopup(true);
               this.trackingTab = -3;
            }

         }
      }

      int getCurrentTab(JTabbedPane var1, Point var2) {
         int var3 = AquaTabbedPaneUI.this.tabForCoordinate(var1, var2.x, var2.y);
         if (var3 >= 0 && var1.isEnabledAt(var3)) {
            return var3;
         } else if (AquaTabbedPaneUI.this.visibleTabState.needsLeftScrollTab() && AquaTabbedPaneUI.this.visibleTabState.getLeftScrollTabRect().contains(var2)) {
            return -2;
         } else {
            return AquaTabbedPaneUI.this.visibleTabState.needsRightScrollTab() && AquaTabbedPaneUI.this.visibleTabState.getRightScrollTabRect().contains(var2) ? -1 : -3;
         }
      }

      void repaint(JTabbedPane var1, int var2) {
         switch(var2) {
         case -2:
            var1.repaint(AquaTabbedPaneUI.this.visibleTabState.getLeftScrollTabRect());
            return;
         case -1:
            var1.repaint(AquaTabbedPaneUI.this.visibleTabState.getRightScrollTabRect());
            return;
         default:
            if (this.trackingTab >= 0) {
               var1.repaint(AquaTabbedPaneUI.this.rects[this.trackingTab]);
            }

         }
      }

      void showFullPopup(boolean var1) {
         JPopupMenu var2 = new JPopupMenu();

         for(int var3 = 0; var3 < AquaTabbedPaneUI.this.tabPane.getTabCount(); ++var3) {
            if (var1) {
               if (!AquaTabbedPaneUI.this.visibleTabState.isBefore(var3)) {
                  continue;
               }
            } else if (!AquaTabbedPaneUI.this.visibleTabState.isAfter(var3)) {
               continue;
            }

            var2.add(this.createMenuItem(var3));
         }

         Rectangle var5;
         if (var1) {
            var5 = AquaTabbedPaneUI.this.visibleTabState.getLeftScrollTabRect();
            Dimension var4 = var2.getPreferredSize();
            var2.show(AquaTabbedPaneUI.this.tabPane, var5.x - var4.width, var5.y + 7);
         } else {
            var5 = AquaTabbedPaneUI.this.visibleTabState.getRightScrollTabRect();
            var2.show(AquaTabbedPaneUI.this.tabPane, var5.x + var5.width, var5.y + 7);
         }

         var2.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuCanceled(PopupMenuEvent var1) {
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent var1) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent var1) {
               AquaTabbedPaneUI.this.pressedTab = -3;
               AquaTabbedPaneUI.this.tabPane.repaint(AquaTabbedPaneUI.this.visibleTabState.getLeftScrollTabRect());
               AquaTabbedPaneUI.this.tabPane.repaint(AquaTabbedPaneUI.this.visibleTabState.getRightScrollTabRect());
            }
         });
      }

      JMenuItem createMenuItem(final int var1) {
         final Component var2 = AquaTabbedPaneUI.this.getTabComponentAt(var1);
         JMenuItem var3;
         if (var2 == null) {
            var3 = new JMenuItem(AquaTabbedPaneUI.this.tabPane.getTitleAt(var1), AquaTabbedPaneUI.this.tabPane.getIconAt(var1));
         } else {
            var3 = new JMenuItem() {
               public void paintComponent(Graphics var1) {
                  super.paintComponent(var1);
                  Dimension var2x = var2.getSize();
                  var2.setSize(this.getSize());
                  var2.validate();
                  var2.paint(var1);
                  var2.setSize(var2x);
               }

               public Dimension getPreferredSize() {
                  return var2.getPreferredSize();
               }
            };
         }

         Color var4 = AquaTabbedPaneUI.this.tabPane.getBackgroundAt(var1);
         if (!(var4 instanceof UIResource)) {
            var3.setBackground(var4);
         }

         var3.setForeground(AquaTabbedPaneUI.this.tabPane.getForegroundAt(var1));
         if (!AquaTabbedPaneUI.this.tabPane.isEnabledAt(var1)) {
            var3.setEnabled(false);
         }

         var3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent var1x) {
               boolean var2 = AquaTabbedPaneUI.this.isTabVisible(var1);
               AquaTabbedPaneUI.this.tabPane.setSelectedIndex(var1);
               if (!var2) {
                  AquaTabbedPaneUI.this.popupSelectionChanged = true;
                  AquaTabbedPaneUI.this.tabPane.invalidate();
                  AquaTabbedPaneUI.this.tabPane.repaint();
               }

            }
         });
         return var3;
      }
   }

   protected class FocusHandler extends FocusAdapter {
      Rectangle sWorkingRect = new Rectangle();

      public void focusGained(FocusEvent var1) {
         if (this.isDefaultFocusReceiver(AquaTabbedPaneUI.this.tabPane) && !AquaTabbedPaneUI.this.hasAvoidedFirstFocus) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
            AquaTabbedPaneUI.this.hasAvoidedFirstFocus = true;
         }

         this.adjustPaintingRectForFocusRing(var1);
      }

      public void focusLost(FocusEvent var1) {
         this.adjustPaintingRectForFocusRing(var1);
      }

      void adjustPaintingRectForFocusRing(FocusEvent var1) {
         JTabbedPane var2 = (JTabbedPane)var1.getSource();
         int var3 = var2.getTabCount();
         int var4 = var2.getSelectedIndex();
         if (var4 != -1 && var3 > 0 && var3 == AquaTabbedPaneUI.this.rects.length) {
            this.sWorkingRect.setBounds(AquaTabbedPaneUI.this.rects[var4]);
            this.sWorkingRect.grow(4, 4);
            var2.repaint(this.sWorkingRect);
         }

      }

      boolean isDefaultFocusReceiver(JComponent var1) {
         if (AquaTabbedPaneUI.this.isDefaultFocusReceiver == null) {
            Component var2 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalPolicy().getDefaultComponent(this.getTopLevelFocusCycleRootAncestor(var1));
            AquaTabbedPaneUI.this.isDefaultFocusReceiver = new Boolean(var2 != null && var2.equals(var1));
         }

         return AquaTabbedPaneUI.this.isDefaultFocusReceiver;
      }

      Container getTopLevelFocusCycleRootAncestor(Container var1) {
         Container var2;
         while((var2 = var1.getFocusCycleRootAncestor()) != null) {
            var1 = var2;
         }

         return var1;
      }
   }

   protected class TabbedPanePropertyChangeHandler extends AquaTabbedPaneCopyFromBasicUI.PropertyChangeHandler {
      protected TabbedPanePropertyChangeHandler() {
         super();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (!"Frame.active".equals(var2)) {
            super.propertyChange(var1);
         } else {
            JTabbedPane var3 = (JTabbedPane)var1.getSource();
            var3.repaint();
            int var4 = AquaTabbedPaneUI.this.tabPane.getSelectedIndex();
            Rectangle[] var5 = AquaTabbedPaneUI.this.rects;
            if (var4 >= 0 && var4 < var5.length) {
               var3.repaint(var5[var4]);
            }

            AquaTabbedPaneUI.this.repaintContentBorderEdge();
         }
      }
   }

   static class AlterRects {
      Rectangle standard;
      Rectangle first;
      Rectangle last;

      AlterRects(int var1, int var2, int var3, int var4) {
         this.standard = new Rectangle(var1, var2, var3, var4);
      }

      AquaTabbedPaneUI.AlterRects start(int var1, int var2, int var3, int var4) {
         this.first = new Rectangle(var1, var2, var3, var4);
         return this;
      }

      AquaTabbedPaneUI.AlterRects end(int var1, int var2, int var3, int var4) {
         this.last = new Rectangle(var1, var2, var3, var4);
         return this;
      }

      static Rectangle alter(Rectangle var0, Rectangle var1) {
         var0.x += var1.x;
         var0.y += var1.y;
         var0.width += var1.width;
         var0.height += var1.height;
         return var0;
      }
   }
}
