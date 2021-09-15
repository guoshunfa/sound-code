package com.apple.laf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class AquaTabbedPaneCopyFromBasicUI extends TabbedPaneUI implements SwingConstants {
   protected JTabbedPane tabPane;
   protected Color highlight;
   protected Color lightHighlight;
   protected Color shadow;
   protected Color darkShadow;
   protected Color focus;
   private Color selectedColor;
   protected int textIconGap;
   protected int tabRunOverlay;
   protected Insets tabInsets;
   protected Insets selectedTabPadInsets;
   protected Insets tabAreaInsets;
   protected Insets contentBorderInsets;
   private boolean tabsOverlapBorder;
   private boolean tabsOpaque = true;
   private boolean contentOpaque = true;
   /** @deprecated */
   @Deprecated
   protected KeyStroke upKey;
   /** @deprecated */
   @Deprecated
   protected KeyStroke downKey;
   /** @deprecated */
   @Deprecated
   protected KeyStroke leftKey;
   /** @deprecated */
   @Deprecated
   protected KeyStroke rightKey;
   protected int[] tabRuns = new int[10];
   protected int runCount = 0;
   protected int selectedRun = -1;
   protected Rectangle[] rects = new Rectangle[0];
   protected int maxTabHeight;
   protected int maxTabWidth;
   protected ChangeListener tabChangeListener;
   protected PropertyChangeListener propertyChangeListener;
   protected MouseListener mouseListener;
   protected FocusListener focusListener;
   private final Insets currentPadInsets = new Insets(0, 0, 0, 0);
   private final Insets currentTabAreaInsets = new Insets(0, 0, 0, 0);
   private Component visibleComponent;
   private Vector<View> htmlViews;
   private Hashtable<Integer, Integer> mnemonicToIndexMap;
   private InputMap mnemonicInputMap;
   private AquaTabbedPaneCopyFromBasicUI.ScrollableTabSupport tabScroller;
   private AquaTabbedPaneCopyFromBasicUI.TabContainer tabContainer;
   protected transient Rectangle calcRect = new Rectangle(0, 0, 0, 0);
   private int focusIndex;
   private AquaTabbedPaneCopyFromBasicUI.Handler handler;
   private int rolloverTabIndex;
   private boolean isRunsDirty;
   private boolean calculatedBaseline;
   private int baseline;
   private static int[] xCropLen = new int[]{1, 1, 0, 0, 1, 1, 2, 2};
   private static int[] yCropLen = new int[]{0, 3, 3, 6, 6, 9, 9, 12};
   private static final int CROP_SEGMENT = 12;

   public static ComponentUI createUI(JComponent var0) {
      return new AquaTabbedPaneCopyFromBasicUI();
   }

   protected Component getTabComponentAt(int var1) {
      return this.tabPane.getTabComponentAt(var1);
   }

   static void loadActionMap(AquaTabbedPaneCopyFromBasicUI.LazyActionMap var0) {
      var0.put(new AquaTabbedPaneCopyFromBasicUI.Actions("navigateNext"));
      var0.put(new AquaTabbedPaneCopyFromBasicUI.Actions("navigatePrevious"));
      var0.put(new AquaTabbedPaneCopyFromBasicUI.Actions("navigateRight"));
      var0.put(new AquaTabbedPaneCopyFromBasicUI.Actions("navigateLeft"));
      var0.put(new AquaTabbedPaneCopyFromBasicUI.Actions("navigateUp"));
      var0.put(new AquaTabbedPaneCopyFromBasicUI.Actions("navigateDown"));
      var0.put(new AquaTabbedPaneCopyFromBasicUI.Actions("navigatePageUp"));
      var0.put(new AquaTabbedPaneCopyFromBasicUI.Actions("navigatePageDown"));
      var0.put(new AquaTabbedPaneCopyFromBasicUI.Actions("requestFocus"));
      var0.put(new AquaTabbedPaneCopyFromBasicUI.Actions("requestFocusForVisibleComponent"));
      var0.put(new AquaTabbedPaneCopyFromBasicUI.Actions("setSelectedIndex"));
      var0.put(new AquaTabbedPaneCopyFromBasicUI.Actions("selectTabWithFocus"));
      var0.put(new AquaTabbedPaneCopyFromBasicUI.Actions("scrollTabsForwardAction"));
      var0.put(new AquaTabbedPaneCopyFromBasicUI.Actions("scrollTabsBackwardAction"));
   }

   public void installUI(JComponent var1) {
      this.tabPane = (JTabbedPane)var1;
      this.calculatedBaseline = false;
      this.rolloverTabIndex = -1;
      this.focusIndex = -1;
      var1.setLayout(this.createLayoutManager());
      this.installComponents();
      this.installDefaults();
      this.installListeners();
      this.installKeyboardActions();
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallKeyboardActions();
      this.uninstallListeners();
      this.uninstallDefaults();
      this.uninstallComponents();
      var1.setLayout((LayoutManager)null);
      this.tabPane = null;
   }

   protected LayoutManager createLayoutManager() {
      return (LayoutManager)(this.tabPane.getTabLayoutPolicy() == 1 ? new AquaTabbedPaneCopyFromBasicUI.TabbedPaneScrollLayout() : new AquaTabbedPaneCopyFromBasicUI.TabbedPaneLayout());
   }

   boolean scrollableTabLayoutEnabled() {
      return this.tabPane.getLayout() instanceof AquaTabbedPaneCopyFromBasicUI.TabbedPaneScrollLayout;
   }

   protected void installComponents() {
      if (this.scrollableTabLayoutEnabled() && this.tabScroller == null) {
         this.tabScroller = new AquaTabbedPaneCopyFromBasicUI.ScrollableTabSupport(this.tabPane.getTabPlacement());
         this.tabPane.add(this.tabScroller.viewport);
      }

      this.installTabContainer();
   }

   private void installTabContainer() {
      for(int var1 = 0; var1 < this.tabPane.getTabCount(); ++var1) {
         Component var2 = this.tabPane.getTabComponentAt(var1);
         if (var2 != null) {
            if (this.tabContainer == null) {
               this.tabContainer = new AquaTabbedPaneCopyFromBasicUI.TabContainer();
            }

            this.tabContainer.add(var2);
         }
      }

      if (this.tabContainer != null) {
         if (this.scrollableTabLayoutEnabled()) {
            this.tabScroller.tabPanel.add(this.tabContainer);
         } else {
            this.tabPane.add(this.tabContainer);
         }

      }
   }

   protected JButton createScrollButton(int var1) {
      if (var1 != 5 && var1 != 1 && var1 != 3 && var1 != 7) {
         throw new IllegalArgumentException("Direction must be one of: SOUTH, NORTH, EAST or WEST");
      } else {
         return new AquaTabbedPaneCopyFromBasicUI.ScrollableTabButton(var1);
      }
   }

   protected void uninstallComponents() {
      this.uninstallTabContainer();
      if (this.scrollableTabLayoutEnabled()) {
         this.tabPane.remove(this.tabScroller.viewport);
         this.tabPane.remove(this.tabScroller.scrollForwardButton);
         this.tabPane.remove(this.tabScroller.scrollBackwardButton);
         this.tabScroller = null;
      }

   }

   private void uninstallTabContainer() {
      if (this.tabContainer != null) {
         this.tabContainer.notifyTabbedPane = false;
         this.tabContainer.removeAll();
         if (this.scrollableTabLayoutEnabled()) {
            this.tabContainer.remove(this.tabScroller.croppedEdge);
            this.tabScroller.tabPanel.remove(this.tabContainer);
         } else {
            this.tabPane.remove(this.tabContainer);
         }

         this.tabContainer = null;
      }
   }

   protected void installDefaults() {
      LookAndFeel.installColorsAndFont(this.tabPane, "TabbedPane.background", "TabbedPane.foreground", "TabbedPane.font");
      this.highlight = UIManager.getColor("TabbedPane.light");
      this.lightHighlight = UIManager.getColor("TabbedPane.highlight");
      this.shadow = UIManager.getColor("TabbedPane.shadow");
      this.darkShadow = UIManager.getColor("TabbedPane.darkShadow");
      this.focus = UIManager.getColor("TabbedPane.focus");
      this.selectedColor = UIManager.getColor("TabbedPane.selected");
      this.textIconGap = UIManager.getInt("TabbedPane.textIconGap");
      this.tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
      this.selectedTabPadInsets = UIManager.getInsets("TabbedPane.selectedTabPadInsets");
      this.tabAreaInsets = UIManager.getInsets("TabbedPane.tabAreaInsets");
      this.tabsOverlapBorder = UIManager.getBoolean("TabbedPane.tabsOverlapBorder");
      this.contentBorderInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
      this.tabRunOverlay = UIManager.getInt("TabbedPane.tabRunOverlay");
      this.tabsOpaque = UIManager.getBoolean("TabbedPane.tabsOpaque");
      this.contentOpaque = UIManager.getBoolean("TabbedPane.contentOpaque");
      Object var1 = UIManager.get("TabbedPane.opaque");
      if (var1 == null) {
         var1 = Boolean.FALSE;
      }

      LookAndFeel.installProperty(this.tabPane, "opaque", var1);
   }

   protected void uninstallDefaults() {
      this.highlight = null;
      this.lightHighlight = null;
      this.shadow = null;
      this.darkShadow = null;
      this.focus = null;
      this.tabInsets = null;
      this.selectedTabPadInsets = null;
      this.tabAreaInsets = null;
      this.contentBorderInsets = null;
   }

   protected void installListeners() {
      if ((this.propertyChangeListener = this.createPropertyChangeListener()) != null) {
         this.tabPane.addPropertyChangeListener(this.propertyChangeListener);
      }

      if ((this.tabChangeListener = this.createChangeListener()) != null) {
         this.tabPane.addChangeListener(this.tabChangeListener);
      }

      if ((this.mouseListener = this.createMouseListener()) != null) {
         this.tabPane.addMouseListener(this.mouseListener);
      }

      this.tabPane.addMouseMotionListener(this.getHandler());
      if ((this.focusListener = this.createFocusListener()) != null) {
         this.tabPane.addFocusListener(this.focusListener);
      }

      this.tabPane.addContainerListener(this.getHandler());
      if (this.tabPane.getTabCount() > 0) {
         this.htmlViews = this.createHTMLVector();
      }

   }

   protected void uninstallListeners() {
      if (this.mouseListener != null) {
         this.tabPane.removeMouseListener(this.mouseListener);
         this.mouseListener = null;
      }

      this.tabPane.removeMouseMotionListener(this.getHandler());
      if (this.focusListener != null) {
         this.tabPane.removeFocusListener(this.focusListener);
         this.focusListener = null;
      }

      this.tabPane.removeContainerListener(this.getHandler());
      if (this.htmlViews != null) {
         this.htmlViews.removeAllElements();
         this.htmlViews = null;
      }

      if (this.tabChangeListener != null) {
         this.tabPane.removeChangeListener(this.tabChangeListener);
         this.tabChangeListener = null;
      }

      if (this.propertyChangeListener != null) {
         this.tabPane.removePropertyChangeListener(this.propertyChangeListener);
         this.propertyChangeListener = null;
      }

      this.handler = null;
   }

   protected MouseListener createMouseListener() {
      return this.getHandler();
   }

   protected FocusListener createFocusListener() {
      return this.getHandler();
   }

   protected ChangeListener createChangeListener() {
      return this.getHandler();
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return this.getHandler();
   }

   private AquaTabbedPaneCopyFromBasicUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new AquaTabbedPaneCopyFromBasicUI.Handler();
      }

      return this.handler;
   }

   protected void installKeyboardActions() {
      InputMap var1 = this.getInputMap(1);
      SwingUtilities.replaceUIInputMap(this.tabPane, 1, var1);
      var1 = this.getInputMap(0);
      SwingUtilities.replaceUIInputMap(this.tabPane, 0, var1);
      AquaTabbedPaneCopyFromBasicUI.LazyActionMap.installLazyActionMap(this.tabPane, AquaTabbedPaneCopyFromBasicUI.class, "TabbedPane.actionMap");
      this.updateMnemonics();
   }

   InputMap getInputMap(int var1) {
      if (var1 == 1) {
         return (InputMap)DefaultLookup.get(this.tabPane, this, "TabbedPane.ancestorInputMap");
      } else {
         return var1 == 0 ? (InputMap)DefaultLookup.get(this.tabPane, this, "TabbedPane.focusInputMap") : null;
      }
   }

   protected void uninstallKeyboardActions() {
      SwingUtilities.replaceUIActionMap(this.tabPane, (ActionMap)null);
      SwingUtilities.replaceUIInputMap(this.tabPane, 1, (InputMap)null);
      SwingUtilities.replaceUIInputMap(this.tabPane, 0, (InputMap)null);
      SwingUtilities.replaceUIInputMap(this.tabPane, 2, (InputMap)null);
      this.mnemonicToIndexMap = null;
      this.mnemonicInputMap = null;
   }

   private void updateMnemonics() {
      this.resetMnemonics();

      for(int var1 = this.tabPane.getTabCount() - 1; var1 >= 0; --var1) {
         int var2 = this.tabPane.getMnemonicAt(var1);
         if (var2 > 0) {
            this.addMnemonic(var1, var2);
         }
      }

   }

   private void resetMnemonics() {
      if (this.mnemonicToIndexMap != null) {
         this.mnemonicToIndexMap.clear();
         this.mnemonicInputMap.clear();
      }

   }

   private void addMnemonic(int var1, int var2) {
      if (this.mnemonicToIndexMap == null) {
         this.initMnemonics();
      }

      this.mnemonicInputMap.put(KeyStroke.getKeyStroke(var2, 10), "setSelectedIndex");
      this.mnemonicToIndexMap.put(new Integer(var2), new Integer(var1));
   }

   private void initMnemonics() {
      this.mnemonicToIndexMap = new Hashtable();
      this.mnemonicInputMap = new ComponentInputMapUIResource(this.tabPane);
      this.mnemonicInputMap.setParent(SwingUtilities.getUIInputMap(this.tabPane, 2));
      SwingUtilities.replaceUIInputMap(this.tabPane, 2, this.mnemonicInputMap);
   }

   private void setRolloverTab(int var1, int var2) {
      this.setRolloverTab(this.tabForCoordinate(this.tabPane, var1, var2, false));
   }

   protected void setRolloverTab(int var1) {
      this.rolloverTabIndex = var1;
   }

   protected int getRolloverTab() {
      return this.rolloverTabIndex;
   }

   public Dimension getMinimumSize(JComponent var1) {
      return null;
   }

   public Dimension getMaximumSize(JComponent var1) {
      return null;
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      super.getBaseline(var1, var2, var3);
      int var4 = this.calculateBaselineIfNecessary();
      if (var4 != -1) {
         int var5 = this.tabPane.getTabPlacement();
         Insets var6 = this.tabPane.getInsets();
         Insets var7 = this.getTabAreaInsets(var5);
         switch(var5) {
         case 1:
            var4 += var6.top + var7.top;
            return var4;
         case 2:
         case 4:
            var4 += var6.top + var7.top;
            return var4;
         case 3:
            var4 += var3 - var6.bottom - var7.bottom - this.maxTabHeight;
            return var4;
         }
      }

      return -1;
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent var1) {
      super.getBaselineResizeBehavior(var1);
      switch(this.tabPane.getTabPlacement()) {
      case 1:
      case 2:
      case 4:
         return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
      case 3:
         return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
      default:
         return Component.BaselineResizeBehavior.OTHER;
      }
   }

   protected int getBaseline(int var1) {
      if (this.tabPane.getTabComponentAt(var1) != null) {
         int var8 = this.getBaselineOffset();
         if (var8 != 0) {
            return -1;
         } else {
            Component var9 = this.tabPane.getTabComponentAt(var1);
            Dimension var10 = var9.getPreferredSize();
            Insets var5 = this.getTabInsets(this.tabPane.getTabPlacement(), var1);
            int var6 = this.maxTabHeight - var5.top - var5.bottom;
            return var9.getBaseline(var10.width, var10.height) + (var6 - var10.height) / 2 + var5.top;
         }
      } else {
         View var2 = this.getTextViewForTab(var1);
         int var3;
         int var4;
         if (var2 != null) {
            var3 = (int)var2.getPreferredSpan(1);
            var4 = BasicHTML.getHTMLBaseline(var2, (int)var2.getPreferredSpan(0), var3);
            return var4 >= 0 ? this.maxTabHeight / 2 - var3 / 2 + var4 + this.getBaselineOffset() : -1;
         } else {
            FontMetrics var7 = this.getFontMetrics();
            var3 = var7.getHeight();
            var4 = var7.getAscent();
            return this.maxTabHeight / 2 - var3 / 2 + var4 + this.getBaselineOffset();
         }
      }
   }

   protected int getBaselineOffset() {
      switch(this.tabPane.getTabPlacement()) {
      case 1:
         if (this.tabPane.getTabCount() > 1) {
            return 1;
         }

         return -1;
      case 3:
         if (this.tabPane.getTabCount() > 1) {
            return -1;
         }

         return 1;
      default:
         return this.maxTabHeight % 2;
      }
   }

   private int calculateBaselineIfNecessary() {
      if (!this.calculatedBaseline) {
         this.calculatedBaseline = true;
         this.baseline = -1;
         if (this.tabPane.getTabCount() > 0) {
            this.calculateBaseline();
         }
      }

      return this.baseline;
   }

   private void calculateBaseline() {
      int var1 = this.tabPane.getTabCount();
      int var2 = this.tabPane.getTabPlacement();
      this.maxTabHeight = this.calculateMaxTabHeight(var2);
      this.baseline = this.getBaseline(0);
      if (this.isHorizontalTabPlacement()) {
         for(int var3 = 1; var3 < var1; ++var3) {
            if (this.getBaseline(var3) != this.baseline) {
               this.baseline = -1;
               break;
            }
         }
      } else {
         FontMetrics var8 = this.getFontMetrics();
         int var4 = var8.getHeight();
         int var5 = this.calculateTabHeight(var2, 0, var4);

         for(int var6 = 1; var6 < var1; ++var6) {
            int var7 = this.calculateTabHeight(var2, var6, var4);
            if (var5 != var7) {
               this.baseline = -1;
               break;
            }
         }
      }

   }

   public void paint(Graphics var1, JComponent var2) {
      int var3 = this.tabPane.getSelectedIndex();
      int var4 = this.tabPane.getTabPlacement();
      this.ensureCurrentLayout();
      if (this.tabsOverlapBorder) {
         this.paintContentBorder(var1, var4, var3);
      }

      if (!this.scrollableTabLayoutEnabled()) {
         this.paintTabArea(var1, var4, var3);
      }

      if (!this.tabsOverlapBorder) {
         this.paintContentBorder(var1, var4, var3);
      }

   }

   protected void paintTabArea(Graphics var1, int var2, int var3) {
      int var4 = this.tabPane.getTabCount();
      Rectangle var5 = new Rectangle();
      Rectangle var6 = new Rectangle();
      Rectangle var7 = var1.getClipBounds();

      for(int var8 = this.runCount - 1; var8 >= 0; --var8) {
         int var9 = this.tabRuns[var8];
         int var10 = this.tabRuns[var8 == this.runCount - 1 ? 0 : var8 + 1];
         int var11 = var10 != 0 ? var10 - 1 : var4 - 1;

         for(int var12 = var9; var12 <= var11; ++var12) {
            if (var12 != var3 && this.rects[var12].intersects(var7)) {
               this.paintTab(var1, var2, this.rects, var12, var5, var6);
            }
         }
      }

      if (var3 >= 0 && this.rects[var3].intersects(var7)) {
         this.paintTab(var1, var2, this.rects, var3, var5, var6);
      }

   }

   protected void paintTab(Graphics var1, int var2, Rectangle[] var3, int var4, Rectangle var5, Rectangle var6) {
      Rectangle var7 = var3[var4];
      int var8 = this.tabPane.getSelectedIndex();
      boolean var9 = var8 == var4;
      if (this.tabsOpaque || this.tabPane.isOpaque()) {
         this.paintTabBackground(var1, var2, var4, var7.x, var7.y, var7.width, var7.height, var9);
      }

      this.paintTabBorder(var1, var2, var4, var7.x, var7.y, var7.width, var7.height, var9);
      String var10 = this.tabPane.getTitleAt(var4);
      Font var11 = this.tabPane.getFont();
      FontMetrics var12 = SwingUtilities2.getFontMetrics(this.tabPane, var1, var11);
      Icon var13 = this.getIconForTab(var4);
      this.layoutLabel(var2, var12, var4, var10, var13, var7, var5, var6, var9);
      if (this.tabPane.getTabComponentAt(var4) == null) {
         String var14 = var10;
         if (this.scrollableTabLayoutEnabled() && this.tabScroller.croppedEdge.isParamsSet() && this.tabScroller.croppedEdge.getTabIndex() == var4 && this.isHorizontalTabPlacement()) {
            int var15 = this.tabScroller.croppedEdge.getCropline() - (var6.x - var7.x) - this.tabScroller.croppedEdge.getCroppedSideWidth();
            var14 = SwingUtilities2.clipStringIfNecessary((JComponent)null, var12, var10, var15);
         }

         this.paintText(var1, var2, var11, var12, var4, var14, var6, var9);
         this.paintIcon(var1, var2, var4, var13, var5, var9);
      }

      this.paintFocusIndicator(var1, var2, var3, var4, var5, var6, var9);
   }

   private boolean isHorizontalTabPlacement() {
      return this.tabPane.getTabPlacement() == 1 || this.tabPane.getTabPlacement() == 3;
   }

   private static Polygon createCroppedTabShape(int var0, Rectangle var1, int var2) {
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      int var14;
      int var15;
      int var16;
      int var17;
      switch(var0) {
      case 1:
      case 3:
      default:
         var14 = var1.height;
         var15 = var1.y;
         var16 = var1.y + var1.height;
         var17 = var1.x + var1.width;
         break;
      case 2:
      case 4:
         var14 = var1.width;
         var15 = var1.x;
         var16 = var1.x + var1.width;
         var17 = var1.y + var1.height;
      }

      int var7 = var14 / 12;
      if (var14 % 12 > 0) {
         ++var7;
      }

      int var8 = 2 + var7 * 8;
      int[] var9 = new int[var8];
      int[] var10 = new int[var8];
      byte var11 = 0;
      var9[var11] = var17;
      int var18 = var11 + 1;
      var10[var11] = var16;
      var9[var18] = var17;
      var10[var18++] = var15;

      for(int var12 = 0; var12 < var7; ++var12) {
         for(int var13 = 0; var13 < xCropLen.length; ++var13) {
            var9[var18] = var2 - xCropLen[var13];
            var10[var18] = var15 + var12 * 12 + yCropLen[var13];
            if (var10[var18] >= var16) {
               var10[var18] = var16;
               ++var18;
               break;
            }

            ++var18;
         }
      }

      if (var0 != 1 && var0 != 3) {
         return new Polygon(var10, var9, var18);
      } else {
         return new Polygon(var9, var10, var18);
      }
   }

   private void paintCroppedTabEdge(Graphics var1) {
      int var2 = this.tabScroller.croppedEdge.getTabIndex();
      int var3 = this.tabScroller.croppedEdge.getCropline();
      int var4;
      int var5;
      int var7;
      switch(this.tabPane.getTabPlacement()) {
      case 1:
      case 3:
      default:
         var4 = var3;
         var5 = this.rects[var2].y;
         var7 = var5;
         var1.setColor(this.shadow);

         while(var7 <= var5 + this.rects[var2].height) {
            for(int var8 = 0; var8 < xCropLen.length; var8 += 2) {
               var1.drawLine(var4 - xCropLen[var8], var7 + yCropLen[var8], var4 - xCropLen[var8 + 1], var7 + yCropLen[var8 + 1] - 1);
            }

            var7 += 12;
         }

         return;
      case 2:
      case 4:
         var4 = this.rects[var2].x;
         var5 = var3;
         int var6 = var4;
         var1.setColor(this.shadow);

         while(var6 <= var4 + this.rects[var2].width) {
            for(var7 = 0; var7 < xCropLen.length; var7 += 2) {
               var1.drawLine(var6 + yCropLen[var7], var5 - xCropLen[var7], var6 + yCropLen[var7 + 1] - 1, var5 - xCropLen[var7 + 1]);
            }

            var6 += 12;
         }

      }
   }

   protected void layoutLabel(int var1, FontMetrics var2, int var3, String var4, Icon var5, Rectangle var6, Rectangle var7, Rectangle var8, boolean var9) {
      var8.x = var8.y = var7.x = var7.y = 0;
      View var10 = this.getTextViewForTab(var3);
      if (var10 != null) {
         this.tabPane.putClientProperty("html", var10);
      }

      SwingUtilities.layoutCompoundLabel(this.tabPane, var2, var4, var5, 0, 0, 0, 11, var6, var7, var8, this.textIconGap);
      this.tabPane.putClientProperty("html", (Object)null);
      int var11 = this.getTabLabelShiftX(var1, var3, var9);
      int var12 = this.getTabLabelShiftY(var1, var3, var9);
      var7.x += var11;
      var7.y += var12;
      var8.x += var11;
      var8.y += var12;
   }

   protected void paintIcon(Graphics var1, int var2, int var3, Icon var4, Rectangle var5, boolean var6) {
      if (var4 != null) {
         var4.paintIcon(this.tabPane, var1, var5.x, var5.y);
      }

   }

   protected void paintText(Graphics var1, int var2, Font var3, FontMetrics var4, int var5, String var6, Rectangle var7, boolean var8) {
      var1.setFont(var3);
      View var9 = this.getTextViewForTab(var5);
      if (var9 != null) {
         var9.paint(var1, var7);
      } else {
         int var10 = this.tabPane.getDisplayedMnemonicIndexAt(var5);
         if (this.tabPane.isEnabled() && this.tabPane.isEnabledAt(var5)) {
            Color var11 = this.tabPane.getForegroundAt(var5);
            if (var8 && var11 instanceof UIResource) {
               Color var12 = UIManager.getColor("TabbedPane.selectedForeground");
               if (var12 != null) {
                  var11 = var12;
               }
            }

            var1.setColor(var11);
            SwingUtilities2.drawStringUnderlineCharAt(this.tabPane, var1, var6, var10, var7.x, var7.y + var4.getAscent());
         } else {
            var1.setColor(this.tabPane.getBackgroundAt(var5).brighter());
            SwingUtilities2.drawStringUnderlineCharAt(this.tabPane, var1, var6, var10, var7.x, var7.y + var4.getAscent());
            var1.setColor(this.tabPane.getBackgroundAt(var5).darker());
            SwingUtilities2.drawStringUnderlineCharAt(this.tabPane, var1, var6, var10, var7.x - 1, var7.y + var4.getAscent() - 1);
         }
      }

   }

   protected int getTabLabelShiftX(int var1, int var2, boolean var3) {
      Rectangle var4 = this.rects[var2];
      boolean var5 = false;
      int var6;
      switch(var1) {
      case 1:
      case 3:
      default:
         var6 = var4.width % 2;
         break;
      case 2:
         var6 = var3 ? -1 : 1;
         break;
      case 4:
         var6 = var3 ? 1 : -1;
      }

      return var6;
   }

   protected int getTabLabelShiftY(int var1, int var2, boolean var3) {
      Rectangle var4 = this.rects[var2];
      boolean var5 = false;
      int var6;
      switch(var1) {
      case 1:
      default:
         var6 = var3 ? -1 : 1;
         break;
      case 2:
      case 4:
         var6 = var4.height % 2;
         break;
      case 3:
         var6 = var3 ? 1 : -1;
      }

      return var6;
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
            var11 = var8.width - 6;
            var12 = var8.height - 5;
            break;
         case 2:
            var9 = var8.x + 3;
            var10 = var8.y + 3;
            var11 = var8.width - 5;
            var12 = var8.height - 6;
            break;
         case 3:
            var9 = var8.x + 3;
            var10 = var8.y + 2;
            var11 = var8.width - 6;
            var12 = var8.height - 5;
            break;
         case 4:
            var9 = var8.x + 2;
            var10 = var8.y + 3;
            var11 = var8.width - 5;
            var12 = var8.height - 6;
         }

         BasicGraphicsUtils.drawDashedRect(var1, var9, var10, var11, var12);
      }

   }

   protected void paintTabBorder(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      var1.setColor(this.lightHighlight);
      switch(var2) {
      case 1:
      default:
         var1.drawLine(var4, var5 + 2, var4, var5 + var7 - 1);
         var1.drawLine(var4 + 1, var5 + 1, var4 + 1, var5 + 1);
         var1.drawLine(var4 + 2, var5, var4 + var6 - 3, var5);
         var1.setColor(this.shadow);
         var1.drawLine(var4 + var6 - 2, var5 + 2, var4 + var6 - 2, var5 + var7 - 1);
         var1.setColor(this.darkShadow);
         var1.drawLine(var4 + var6 - 1, var5 + 2, var4 + var6 - 1, var5 + var7 - 1);
         var1.drawLine(var4 + var6 - 2, var5 + 1, var4 + var6 - 2, var5 + 1);
         break;
      case 2:
         var1.drawLine(var4 + 1, var5 + var7 - 2, var4 + 1, var5 + var7 - 2);
         var1.drawLine(var4, var5 + 2, var4, var5 + var7 - 3);
         var1.drawLine(var4 + 1, var5 + 1, var4 + 1, var5 + 1);
         var1.drawLine(var4 + 2, var5, var4 + var6 - 1, var5);
         var1.setColor(this.shadow);
         var1.drawLine(var4 + 2, var5 + var7 - 2, var4 + var6 - 1, var5 + var7 - 2);
         var1.setColor(this.darkShadow);
         var1.drawLine(var4 + 2, var5 + var7 - 1, var4 + var6 - 1, var5 + var7 - 1);
         break;
      case 3:
         var1.drawLine(var4, var5, var4, var5 + var7 - 3);
         var1.drawLine(var4 + 1, var5 + var7 - 2, var4 + 1, var5 + var7 - 2);
         var1.setColor(this.shadow);
         var1.drawLine(var4 + 2, var5 + var7 - 2, var4 + var6 - 3, var5 + var7 - 2);
         var1.drawLine(var4 + var6 - 2, var5, var4 + var6 - 2, var5 + var7 - 3);
         var1.setColor(this.darkShadow);
         var1.drawLine(var4 + 2, var5 + var7 - 1, var4 + var6 - 3, var5 + var7 - 1);
         var1.drawLine(var4 + var6 - 2, var5 + var7 - 2, var4 + var6 - 2, var5 + var7 - 2);
         var1.drawLine(var4 + var6 - 1, var5, var4 + var6 - 1, var5 + var7 - 3);
         break;
      case 4:
         var1.drawLine(var4, var5, var4 + var6 - 3, var5);
         var1.setColor(this.shadow);
         var1.drawLine(var4, var5 + var7 - 2, var4 + var6 - 3, var5 + var7 - 2);
         var1.drawLine(var4 + var6 - 2, var5 + 2, var4 + var6 - 2, var5 + var7 - 3);
         var1.setColor(this.darkShadow);
         var1.drawLine(var4 + var6 - 2, var5 + 1, var4 + var6 - 2, var5 + 1);
         var1.drawLine(var4 + var6 - 2, var5 + var7 - 2, var4 + var6 - 2, var5 + var7 - 2);
         var1.drawLine(var4 + var6 - 1, var5 + 2, var4 + var6 - 1, var5 + var7 - 3);
         var1.drawLine(var4, var5 + var7 - 1, var4 + var6 - 3, var5 + var7 - 1);
      }

   }

   protected void paintTabBackground(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      var1.setColor(var8 && this.selectedColor != null ? this.selectedColor : this.tabPane.getBackgroundAt(var3));
      switch(var2) {
      case 1:
      default:
         var1.fillRect(var4 + 1, var5 + 1, var6 - 3, var7 - 1);
         break;
      case 2:
         var1.fillRect(var4 + 1, var5 + 1, var6 - 1, var7 - 3);
         break;
      case 3:
         var1.fillRect(var4 + 1, var5, var6 - 3, var7 - 1);
         break;
      case 4:
         var1.fillRect(var4, var5 + 1, var6 - 2, var7 - 3);
      }

   }

   protected void paintContentBorder(Graphics var1, int var2, int var3) {
      int var4 = this.tabPane.getWidth();
      int var5 = this.tabPane.getHeight();
      Insets var6 = this.tabPane.getInsets();
      Insets var7 = this.getTabAreaInsets(var2);
      int var8 = var6.left;
      int var9 = var6.top;
      int var10 = var4 - var6.right - var6.left;
      int var11 = var5 - var6.top - var6.bottom;
      switch(var2) {
      case 1:
      default:
         var9 += this.calculateTabAreaHeight(var2, this.runCount, this.maxTabHeight);
         if (this.tabsOverlapBorder) {
            var9 -= var7.bottom;
         }

         var11 -= var9 - var6.top;
         break;
      case 2:
         var8 += this.calculateTabAreaWidth(var2, this.runCount, this.maxTabWidth);
         if (this.tabsOverlapBorder) {
            var8 -= var7.right;
         }

         var10 -= var8 - var6.left;
         break;
      case 3:
         var11 -= this.calculateTabAreaHeight(var2, this.runCount, this.maxTabHeight);
         if (this.tabsOverlapBorder) {
            var11 += var7.top;
         }
         break;
      case 4:
         var10 -= this.calculateTabAreaWidth(var2, this.runCount, this.maxTabWidth);
         if (this.tabsOverlapBorder) {
            var10 += var7.left;
         }
      }

      if (this.tabPane.getTabCount() > 0 && (this.contentOpaque || this.tabPane.isOpaque())) {
         Color var12 = UIManager.getColor("TabbedPane.contentAreaColor");
         if (var12 != null) {
            var1.setColor(var12);
         } else if (this.selectedColor != null && var3 != -1) {
            var1.setColor(this.selectedColor);
         } else {
            var1.setColor(this.tabPane.getBackground());
         }

         var1.fillRect(var8, var9, var10, var11);
      }

      this.paintContentBorderTopEdge(var1, var2, var3, var8, var9, var10, var11);
      this.paintContentBorderLeftEdge(var1, var2, var3, var8, var9, var10, var11);
      this.paintContentBorderBottomEdge(var1, var2, var3, var8, var9, var10, var11);
      this.paintContentBorderRightEdge(var1, var2, var3, var8, var9, var10, var11);
   }

   protected void paintContentBorderTopEdge(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      Rectangle var8 = var3 < 0 ? null : this.getTabBounds(var3, this.calcRect);
      var1.setColor(this.lightHighlight);
      if (var2 == 1 && var3 >= 0 && var8.y + var8.height + 1 >= var5 && var8.x >= var4 && var8.x <= var4 + var6) {
         var1.drawLine(var4, var5, var8.x - 1, var5);
         if (var8.x + var8.width < var4 + var6 - 2) {
            var1.drawLine(var8.x + var8.width, var5, var4 + var6 - 2, var5);
         } else {
            var1.setColor(this.shadow);
            var1.drawLine(var4 + var6 - 2, var5, var4 + var6 - 2, var5);
         }
      } else {
         var1.drawLine(var4, var5, var4 + var6 - 2, var5);
      }

   }

   protected void paintContentBorderLeftEdge(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      Rectangle var8 = var3 < 0 ? null : this.getTabBounds(var3, this.calcRect);
      var1.setColor(this.lightHighlight);
      if (var2 == 2 && var3 >= 0 && var8.x + var8.width + 1 >= var4 && var8.y >= var5 && var8.y <= var5 + var7) {
         var1.drawLine(var4, var5, var4, var8.y - 1);
         if (var8.y + var8.height < var5 + var7 - 2) {
            var1.drawLine(var4, var8.y + var8.height, var4, var5 + var7 - 2);
         }
      } else {
         var1.drawLine(var4, var5, var4, var5 + var7 - 2);
      }

   }

   protected void paintContentBorderBottomEdge(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      Rectangle var8 = var3 < 0 ? null : this.getTabBounds(var3, this.calcRect);
      var1.setColor(this.shadow);
      if (var2 == 3 && var3 >= 0 && var8.y - 1 <= var7 && var8.x >= var4 && var8.x <= var4 + var6) {
         var1.drawLine(var4 + 1, var5 + var7 - 2, var8.x - 1, var5 + var7 - 2);
         var1.setColor(this.darkShadow);
         var1.drawLine(var4, var5 + var7 - 1, var8.x - 1, var5 + var7 - 1);
         if (var8.x + var8.width < var4 + var6 - 2) {
            var1.setColor(this.shadow);
            var1.drawLine(var8.x + var8.width, var5 + var7 - 2, var4 + var6 - 2, var5 + var7 - 2);
            var1.setColor(this.darkShadow);
            var1.drawLine(var8.x + var8.width, var5 + var7 - 1, var4 + var6 - 1, var5 + var7 - 1);
         }
      } else {
         var1.drawLine(var4 + 1, var5 + var7 - 2, var4 + var6 - 2, var5 + var7 - 2);
         var1.setColor(this.darkShadow);
         var1.drawLine(var4, var5 + var7 - 1, var4 + var6 - 1, var5 + var7 - 1);
      }

   }

   protected void paintContentBorderRightEdge(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      Rectangle var8 = var3 < 0 ? null : this.getTabBounds(var3, this.calcRect);
      var1.setColor(this.shadow);
      if (var2 == 4 && var3 >= 0 && var8.x - 1 <= var6 && var8.y >= var5 && var8.y <= var5 + var7) {
         var1.drawLine(var4 + var6 - 2, var5 + 1, var4 + var6 - 2, var8.y - 1);
         var1.setColor(this.darkShadow);
         var1.drawLine(var4 + var6 - 1, var5, var4 + var6 - 1, var8.y - 1);
         if (var8.y + var8.height < var5 + var7 - 2) {
            var1.setColor(this.shadow);
            var1.drawLine(var4 + var6 - 2, var8.y + var8.height, var4 + var6 - 2, var5 + var7 - 2);
            var1.setColor(this.darkShadow);
            var1.drawLine(var4 + var6 - 1, var8.y + var8.height, var4 + var6 - 1, var5 + var7 - 2);
         }
      } else {
         var1.drawLine(var4 + var6 - 2, var5 + 1, var4 + var6 - 2, var5 + var7 - 3);
         var1.setColor(this.darkShadow);
         var1.drawLine(var4 + var6 - 1, var5, var4 + var6 - 1, var5 + var7 - 1);
      }

   }

   protected void ensureCurrentLayout() {
      if (!this.tabPane.isValid()) {
         this.tabPane.validate();
      }

      if (!this.tabPane.isValid()) {
         AquaTabbedPaneCopyFromBasicUI.TabbedPaneLayout var1 = (AquaTabbedPaneCopyFromBasicUI.TabbedPaneLayout)this.tabPane.getLayout();
         var1.calculateLayoutInfo();
      }

   }

   public Rectangle getTabBounds(JTabbedPane var1, int var2) {
      this.ensureCurrentLayout();
      Rectangle var3 = new Rectangle();
      return this.getTabBounds(var2, var3);
   }

   public int getTabRunCount(JTabbedPane var1) {
      this.ensureCurrentLayout();
      return this.runCount;
   }

   public int tabForCoordinate(JTabbedPane var1, int var2, int var3) {
      return this.tabForCoordinate(var1, var2, var3, true);
   }

   private int tabForCoordinate(JTabbedPane var1, int var2, int var3, boolean var4) {
      if (var4) {
         this.ensureCurrentLayout();
      }

      if (this.isRunsDirty) {
         return -1;
      } else {
         Point var5 = new Point(var2, var3);
         if (this.scrollableTabLayoutEnabled()) {
            this.translatePointToTabPanel(var2, var3, var5);
            Rectangle var6 = this.tabScroller.viewport.getViewRect();
            if (!var6.contains(var5)) {
               return -1;
            }
         }

         int var8 = this.tabPane.getTabCount();

         for(int var7 = 0; var7 < var8; ++var7) {
            if (this.rects[var7].contains(var5.x, var5.y)) {
               return var7;
            }
         }

         return -1;
      }
   }

   protected Rectangle getTabBounds(int var1, Rectangle var2) {
      var2.width = this.rects[var1].width;
      var2.height = this.rects[var1].height;
      if (this.scrollableTabLayoutEnabled()) {
         Point var3 = this.tabScroller.viewport.getLocation();
         Point var4 = this.tabScroller.viewport.getViewPosition();
         var2.x = this.rects[var1].x + var3.x - var4.x;
         var2.y = this.rects[var1].y + var3.y - var4.y;
      } else {
         var2.x = this.rects[var1].x;
         var2.y = this.rects[var1].y;
      }

      return var2;
   }

   private int getClosestTab(int var1, int var2) {
      int var3 = 0;
      int var4 = Math.min(this.rects.length, this.tabPane.getTabCount());
      int var5 = var4;
      int var6 = this.tabPane.getTabPlacement();
      boolean var7 = var6 == 1 || var6 == 3;
      int var8 = var7 ? var1 : var2;

      while(var3 != var5) {
         int var9 = (var5 + var3) / 2;
         int var10;
         int var11;
         if (var7) {
            var10 = this.rects[var9].x;
            var11 = var10 + this.rects[var9].width;
         } else {
            var10 = this.rects[var9].y;
            var11 = var10 + this.rects[var9].height;
         }

         if (var8 < var10) {
            var5 = var9;
            if (var3 == var9) {
               return Math.max(0, var9 - 1);
            }
         } else {
            if (var8 < var11) {
               return var9;
            }

            var3 = var9;
            if (var5 - var9 <= 1) {
               return Math.max(var9 + 1, var4 - 1);
            }
         }
      }

      return var3;
   }

   private Point translatePointToTabPanel(int var1, int var2, Point var3) {
      Point var4 = this.tabScroller.viewport.getLocation();
      Point var5 = this.tabScroller.viewport.getViewPosition();
      var3.x = var1 - var4.x + var5.x;
      var3.y = var2 - var4.y + var5.y;
      return var3;
   }

   protected Component getVisibleComponent() {
      return this.visibleComponent;
   }

   protected void setVisibleComponent(Component var1) {
      if (this.visibleComponent != null && this.visibleComponent != var1 && this.visibleComponent.getParent() == this.tabPane && this.visibleComponent.isVisible()) {
         this.visibleComponent.setVisible(false);
      }

      if (var1 != null && !var1.isVisible()) {
         var1.setVisible(true);
      }

      this.visibleComponent = var1;
   }

   protected void assureRectsCreated(int var1) {
      int var2 = this.rects.length;
      if (var1 != var2) {
         Rectangle[] var3 = new Rectangle[var1];
         System.arraycopy(this.rects, 0, var3, 0, Math.min(var2, var1));
         this.rects = var3;

         for(int var4 = var2; var4 < var1; ++var4) {
            this.rects[var4] = new Rectangle();
         }
      }

   }

   protected void expandTabRunsArray() {
      int var1 = this.tabRuns.length;
      int[] var2 = new int[var1 + 10];
      System.arraycopy(this.tabRuns, 0, var2, 0, this.runCount);
      this.tabRuns = var2;
   }

   protected int getRunForTab(int var1, int var2) {
      for(int var3 = 0; var3 < this.runCount; ++var3) {
         int var4 = this.tabRuns[var3];
         int var5 = this.lastTabInRun(var1, var3);
         if (var2 >= var4 && var2 <= var5) {
            return var3;
         }
      }

      return 0;
   }

   protected int lastTabInRun(int var1, int var2) {
      if (this.runCount == 1) {
         return var1 - 1;
      } else {
         int var3 = var2 == this.runCount - 1 ? 0 : var2 + 1;
         return this.tabRuns[var3] == 0 ? var1 - 1 : this.tabRuns[var3] - 1;
      }
   }

   protected int getTabRunOverlay(int var1) {
      return this.tabRunOverlay;
   }

   protected int getTabRunIndent(int var1, int var2) {
      return 0;
   }

   protected boolean shouldPadTabRun(int var1, int var2) {
      return this.runCount > 1;
   }

   protected boolean shouldRotateTabRuns(int var1) {
      return true;
   }

   protected Icon getIconForTab(int var1) {
      return this.tabPane.isEnabled() && this.tabPane.isEnabledAt(var1) ? this.tabPane.getIconAt(var1) : this.tabPane.getDisabledIconAt(var1);
   }

   protected View getTextViewForTab(int var1) {
      return this.htmlViews != null ? (View)this.htmlViews.elementAt(var1) : null;
   }

   protected int calculateTabHeight(int var1, int var2, int var3) {
      byte var4 = 0;
      Component var5 = this.tabPane.getTabComponentAt(var2);
      int var8;
      if (var5 != null) {
         var8 = var5.getPreferredSize().height;
      } else {
         View var6 = this.getTextViewForTab(var2);
         if (var6 != null) {
            var8 = var4 + (int)var6.getPreferredSpan(1);
         } else {
            var8 = var4 + var3;
         }

         Icon var7 = this.getIconForTab(var2);
         if (var7 != null) {
            var8 = Math.max(var8, var7.getIconHeight());
         }
      }

      Insets var9 = this.getTabInsets(var1, var2);
      var8 += var9.top + var9.bottom + 2;
      return var8;
   }

   protected int calculateMaxTabHeight(int var1) {
      FontMetrics var2 = this.getFontMetrics();
      int var3 = this.tabPane.getTabCount();
      int var4 = 0;
      int var5 = var2.getHeight();

      for(int var6 = 0; var6 < var3; ++var6) {
         var4 = Math.max(this.calculateTabHeight(var1, var6, var5), var4);
      }

      return var4;
   }

   protected int calculateTabWidth(int var1, int var2, FontMetrics var3) {
      Insets var4 = this.getTabInsets(var1, var2);
      int var5 = var4.left + var4.right + 3;
      Component var6 = this.tabPane.getTabComponentAt(var2);
      if (var6 != null) {
         var5 += var6.getPreferredSize().width;
      } else {
         Icon var7 = this.getIconForTab(var2);
         if (var7 != null) {
            var5 += var7.getIconWidth() + this.textIconGap;
         }

         View var8 = this.getTextViewForTab(var2);
         if (var8 != null) {
            var5 += (int)var8.getPreferredSpan(0);
         } else {
            String var9 = this.tabPane.getTitleAt(var2);
            var5 += SwingUtilities2.stringWidth(this.tabPane, var3, var9);
         }
      }

      return var5;
   }

   protected int calculateMaxTabWidth(int var1) {
      FontMetrics var2 = this.getFontMetrics();
      int var3 = this.tabPane.getTabCount();
      int var4 = 0;

      for(int var5 = 0; var5 < var3; ++var5) {
         var4 = Math.max(this.calculateTabWidth(var1, var5, var2), var4);
      }

      return var4;
   }

   protected int calculateTabAreaHeight(int var1, int var2, int var3) {
      Insets var4 = this.getTabAreaInsets(var1);
      int var5 = this.getTabRunOverlay(var1);
      return var2 > 0 ? var2 * (var3 - var5) + var5 + var4.top + var4.bottom : 0;
   }

   protected int calculateTabAreaWidth(int var1, int var2, int var3) {
      Insets var4 = this.getTabAreaInsets(var1);
      int var5 = this.getTabRunOverlay(var1);
      return var2 > 0 ? var2 * (var3 - var5) + var5 + var4.left + var4.right : 0;
   }

   protected Insets getTabInsets(int var1, int var2) {
      return this.tabInsets;
   }

   protected Insets getSelectedTabPadInsets(int var1) {
      rotateInsets(this.selectedTabPadInsets, this.currentPadInsets, var1);
      return this.currentPadInsets;
   }

   protected Insets getTabAreaInsets(int var1) {
      rotateInsets(this.tabAreaInsets, this.currentTabAreaInsets, var1);
      return this.currentTabAreaInsets;
   }

   protected Insets getContentBorderInsets(int var1) {
      return this.contentBorderInsets;
   }

   protected FontMetrics getFontMetrics() {
      Font var1 = this.tabPane.getFont();
      return this.tabPane.getFontMetrics(var1);
   }

   protected void navigateSelectedTab(int var1) {
      int var2 = this.tabPane.getTabPlacement();
      int var3 = DefaultLookup.getBoolean(this.tabPane, this, "TabbedPane.selectionFollowsFocus", true) ? this.tabPane.getSelectedIndex() : this.getFocusIndex();
      int var4 = this.tabPane.getTabCount();
      boolean var5 = AquaUtils.isLeftToRight(this.tabPane);
      if (var4 > 0) {
         int var6;
         switch(var2) {
         case 1:
         case 3:
         default:
            switch(var1) {
            case 1:
               var6 = this.getTabRunOffset(var2, var4, var3, false);
               this.selectAdjacentRunTab(var2, var3, var6);
               return;
            case 2:
            case 4:
            case 6:
            case 8:
            case 9:
            case 10:
            case 11:
            default:
               return;
            case 3:
               if (var5) {
                  this.selectNextTabInRun(var3);
               } else {
                  this.selectPreviousTabInRun(var3);
               }

               return;
            case 5:
               var6 = this.getTabRunOffset(var2, var4, var3, true);
               this.selectAdjacentRunTab(var2, var3, var6);
               return;
            case 7:
               if (var5) {
                  this.selectPreviousTabInRun(var3);
               } else {
                  this.selectNextTabInRun(var3);
               }

               return;
            case 12:
               this.selectNextTab(var3);
               return;
            case 13:
               this.selectPreviousTab(var3);
               return;
            }
         case 2:
         case 4:
            switch(var1) {
            case 1:
               this.selectPreviousTabInRun(var3);
            case 2:
            case 4:
            case 6:
            case 8:
            case 9:
            case 10:
            case 11:
            default:
               break;
            case 3:
               var6 = this.getTabRunOffset(var2, var4, var3, true);
               this.selectAdjacentRunTab(var2, var3, var6);
               break;
            case 5:
               this.selectNextTabInRun(var3);
               break;
            case 7:
               var6 = this.getTabRunOffset(var2, var4, var3, false);
               this.selectAdjacentRunTab(var2, var3, var6);
               break;
            case 12:
               this.selectNextTab(var3);
               break;
            case 13:
               this.selectPreviousTab(var3);
            }
         }

      }
   }

   protected void selectNextTabInRun(int var1) {
      int var2 = this.tabPane.getTabCount();

      int var3;
      for(var3 = this.getNextTabIndexInRun(var2, var1); var3 != var1 && !this.tabPane.isEnabledAt(var3); var3 = this.getNextTabIndexInRun(var2, var3)) {
      }

      this.navigateTo(var3);
   }

   protected void selectPreviousTabInRun(int var1) {
      int var2 = this.tabPane.getTabCount();

      int var3;
      for(var3 = this.getPreviousTabIndexInRun(var2, var1); var3 != var1 && !this.tabPane.isEnabledAt(var3); var3 = this.getPreviousTabIndexInRun(var2, var3)) {
      }

      this.navigateTo(var3);
   }

   protected void selectNextTab(int var1) {
      int var2;
      for(var2 = this.getNextTabIndex(var1); var2 != var1 && !this.tabPane.isEnabledAt(var2); var2 = this.getNextTabIndex(var2)) {
      }

      this.navigateTo(var2);
   }

   protected void selectPreviousTab(int var1) {
      int var2;
      for(var2 = this.getPreviousTabIndex(var1); var2 != var1 && !this.tabPane.isEnabledAt(var2); var2 = this.getPreviousTabIndex(var2)) {
      }

      this.navigateTo(var2);
   }

   protected void selectAdjacentRunTab(int var1, int var2, int var3) {
      if (this.runCount >= 2) {
         Rectangle var5 = this.rects[var2];
         int var4;
         switch(var1) {
         case 1:
         case 3:
         default:
            var4 = this.tabForCoordinate(this.tabPane, var5.x + var5.width / 2, var5.y + var5.height / 2 + var3);
            break;
         case 2:
         case 4:
            var4 = this.tabForCoordinate(this.tabPane, var5.x + var5.width / 2 + var3, var5.y + var5.height / 2);
         }

         if (var4 != -1) {
            while(!this.tabPane.isEnabledAt(var4) && var4 != var2) {
               var4 = this.getNextTabIndex(var4);
            }

            this.navigateTo(var4);
         }

      }
   }

   private void navigateTo(int var1) {
      if (DefaultLookup.getBoolean(this.tabPane, this, "TabbedPane.selectionFollowsFocus", true)) {
         this.tabPane.setSelectedIndex(var1);
      } else {
         this.setFocusIndex(var1, true);
      }

   }

   void setFocusIndex(int var1, boolean var2) {
      if (var2 && !this.isRunsDirty) {
         this.repaintTab(this.focusIndex);
         this.focusIndex = var1;
         this.repaintTab(this.focusIndex);
      } else {
         this.focusIndex = var1;
      }

   }

   private void repaintTab(int var1) {
      if (!this.isRunsDirty && var1 >= 0 && var1 < this.tabPane.getTabCount()) {
         Rectangle var2 = this.getTabBounds(this.tabPane, var1);
         if (var2 != null) {
            this.tabPane.repaint(var2);
         }
      }

   }

   private void validateFocusIndex() {
      if (this.focusIndex >= this.tabPane.getTabCount()) {
         this.setFocusIndex(this.tabPane.getSelectedIndex(), false);
      }

   }

   protected int getFocusIndex() {
      return this.focusIndex;
   }

   protected int getTabRunOffset(int var1, int var2, int var3, boolean var4) {
      int var5 = this.getRunForTab(var2, var3);
      int var6;
      switch(var1) {
      case 1:
      default:
         if (var5 == 0) {
            var6 = var4 ? -(this.calculateTabAreaHeight(var1, this.runCount, this.maxTabHeight) - this.maxTabHeight) : -this.maxTabHeight;
         } else if (var5 == this.runCount - 1) {
            var6 = var4 ? this.maxTabHeight : this.calculateTabAreaHeight(var1, this.runCount, this.maxTabHeight) - this.maxTabHeight;
         } else {
            var6 = var4 ? this.maxTabHeight : -this.maxTabHeight;
         }
         break;
      case 2:
         if (var5 == 0) {
            var6 = var4 ? -(this.calculateTabAreaWidth(var1, this.runCount, this.maxTabWidth) - this.maxTabWidth) : -this.maxTabWidth;
         } else if (var5 == this.runCount - 1) {
            var6 = var4 ? this.maxTabWidth : this.calculateTabAreaWidth(var1, this.runCount, this.maxTabWidth) - this.maxTabWidth;
         } else {
            var6 = var4 ? this.maxTabWidth : -this.maxTabWidth;
         }
         break;
      case 3:
         if (var5 == 0) {
            var6 = var4 ? this.maxTabHeight : this.calculateTabAreaHeight(var1, this.runCount, this.maxTabHeight) - this.maxTabHeight;
         } else if (var5 == this.runCount - 1) {
            var6 = var4 ? -(this.calculateTabAreaHeight(var1, this.runCount, this.maxTabHeight) - this.maxTabHeight) : -this.maxTabHeight;
         } else {
            var6 = var4 ? this.maxTabHeight : -this.maxTabHeight;
         }
         break;
      case 4:
         if (var5 == 0) {
            var6 = var4 ? this.maxTabWidth : this.calculateTabAreaWidth(var1, this.runCount, this.maxTabWidth) - this.maxTabWidth;
         } else if (var5 == this.runCount - 1) {
            var6 = var4 ? -(this.calculateTabAreaWidth(var1, this.runCount, this.maxTabWidth) - this.maxTabWidth) : -this.maxTabWidth;
         } else {
            var6 = var4 ? this.maxTabWidth : -this.maxTabWidth;
         }
      }

      return var6;
   }

   protected int getPreviousTabIndex(int var1) {
      int var2 = var1 - 1 >= 0 ? var1 - 1 : this.tabPane.getTabCount() - 1;
      return var2 >= 0 ? var2 : 0;
   }

   protected int getNextTabIndex(int var1) {
      return (var1 + 1) % this.tabPane.getTabCount();
   }

   protected int getNextTabIndexInRun(int var1, int var2) {
      if (this.runCount < 2) {
         return this.getNextTabIndex(var2);
      } else {
         int var3 = this.getRunForTab(var1, var2);
         int var4 = this.getNextTabIndex(var2);
         return var4 == this.tabRuns[this.getNextTabRun(var3)] ? this.tabRuns[var3] : var4;
      }
   }

   protected int getPreviousTabIndexInRun(int var1, int var2) {
      if (this.runCount < 2) {
         return this.getPreviousTabIndex(var2);
      } else {
         int var3 = this.getRunForTab(var1, var2);
         if (var2 == this.tabRuns[var3]) {
            int var4 = this.tabRuns[this.getNextTabRun(var3)] - 1;
            return var4 != -1 ? var4 : var1 - 1;
         } else {
            return this.getPreviousTabIndex(var2);
         }
      }
   }

   protected int getPreviousTabRun(int var1) {
      int var2 = var1 - 1 >= 0 ? var1 - 1 : this.runCount - 1;
      return var2 >= 0 ? var2 : 0;
   }

   protected int getNextTabRun(int var1) {
      return (var1 + 1) % this.runCount;
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
         var1.top = var0.left;
         var1.left = var0.bottom;
         var1.bottom = var0.right;
         var1.right = var0.top;
      }

   }

   boolean requestFocusForVisibleComponent() {
      return SwingUtilities2.tabbedPaneChangeFocusTo(this.getVisibleComponent());
   }

   private Vector<View> createHTMLVector() {
      Vector var1 = new Vector();
      int var2 = this.tabPane.getTabCount();
      if (var2 > 0) {
         for(int var3 = 0; var3 < var2; ++var3) {
            String var4 = this.tabPane.getTitleAt(var3);
            if (BasicHTML.isHTMLString(var4)) {
               var1.addElement(BasicHTML.createHTMLView(this.tabPane, var4));
            } else {
               var1.addElement((Object)null);
            }
         }
      }

      return var1;
   }

   static class LazyActionMap extends ActionMapUIResource {
      private transient Object _loader;

      static void installLazyActionMap(JComponent var0, Class<AquaTabbedPaneCopyFromBasicUI> var1, String var2) {
         Object var3 = (ActionMap)UIManager.get(var2);
         if (var3 == null) {
            var3 = new AquaTabbedPaneCopyFromBasicUI.LazyActionMap(var1);
            UIManager.getLookAndFeelDefaults().put(var2, var3);
         }

         SwingUtilities.replaceUIActionMap(var0, (ActionMap)var3);
      }

      static ActionMap getActionMap(Class<AquaTabbedPaneCopyFromBasicUI> var0, String var1) {
         Object var2 = (ActionMap)UIManager.get(var1);
         if (var2 == null) {
            var2 = new AquaTabbedPaneCopyFromBasicUI.LazyActionMap(var0);
            UIManager.getLookAndFeelDefaults().put(var1, var2);
         }

         return (ActionMap)var2;
      }

      private LazyActionMap(Class<AquaTabbedPaneCopyFromBasicUI> var1) {
         this._loader = var1;
      }

      public void put(Action var1) {
         this.put(var1.getValue("Name"), var1);
      }

      public void put(Object var1, Action var2) {
         this.loadIfNecessary();
         super.put(var1, var2);
      }

      public Action get(Object var1) {
         this.loadIfNecessary();
         return super.get(var1);
      }

      public void remove(Object var1) {
         this.loadIfNecessary();
         super.remove(var1);
      }

      public void clear() {
         this.loadIfNecessary();
         super.clear();
      }

      public Object[] keys() {
         this.loadIfNecessary();
         return super.keys();
      }

      public int size() {
         this.loadIfNecessary();
         return super.size();
      }

      public Object[] allKeys() {
         this.loadIfNecessary();
         return super.allKeys();
      }

      public void setParent(ActionMap var1) {
         this.loadIfNecessary();
         super.setParent(var1);
      }

      private void loadIfNecessary() {
         if (this._loader != null) {
            Object var1 = this._loader;
            this._loader = null;
            Class var2 = (Class)var1;

            try {
               Method var3 = var2.getDeclaredMethod("loadActionMap", AquaTabbedPaneCopyFromBasicUI.LazyActionMap.class);
               var3.invoke(var2, this);
            } catch (NoSuchMethodException var4) {
               assert false : "LazyActionMap unable to load actions " + var2;
            } catch (IllegalAccessException var5) {
               assert false : "LazyActionMap unable to load actions " + var5;
            } catch (InvocationTargetException var6) {
               assert false : "LazyActionMap unable to load actions " + var6;
            } catch (IllegalArgumentException var7) {
               assert false : "LazyActionMap unable to load actions " + var7;
            }
         }

      }
   }

   private class CroppedEdge extends JPanel implements UIResource {
      private Shape shape;
      private int tabIndex;
      private int cropline;
      private int cropx;
      private int cropy;

      public CroppedEdge() {
         this.setOpaque(false);
      }

      public void setParams(int var1, int var2, int var3, int var4) {
         this.tabIndex = var1;
         this.cropline = var2;
         this.cropx = var3;
         this.cropy = var4;
         Rectangle var5 = AquaTabbedPaneCopyFromBasicUI.this.rects[var1];
         this.setBounds(var5);
         this.shape = AquaTabbedPaneCopyFromBasicUI.createCroppedTabShape(AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabPlacement(), var5, var2);
         if (this.getParent() == null && AquaTabbedPaneCopyFromBasicUI.this.tabContainer != null) {
            AquaTabbedPaneCopyFromBasicUI.this.tabContainer.add(this, 0);
         }

      }

      public void resetParams() {
         this.shape = null;
         if (this.getParent() == AquaTabbedPaneCopyFromBasicUI.this.tabContainer && AquaTabbedPaneCopyFromBasicUI.this.tabContainer != null) {
            AquaTabbedPaneCopyFromBasicUI.this.tabContainer.remove(this);
         }

      }

      public boolean isParamsSet() {
         return this.shape != null;
      }

      public int getTabIndex() {
         return this.tabIndex;
      }

      public int getCropline() {
         return this.cropline;
      }

      public int getCroppedSideWidth() {
         return 3;
      }

      private Color getBgColor() {
         Container var1 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getParent();
         if (var1 != null) {
            Color var2 = var1.getBackground();
            if (var2 != null) {
               return var2;
            }
         }

         return UIManager.getColor("control");
      }

      protected void paintComponent(Graphics var1) {
         super.paintComponent(var1);
         if (this.isParamsSet() && var1 instanceof Graphics2D) {
            Graphics2D var2 = (Graphics2D)var1;
            var2.clipRect(0, 0, this.getWidth(), this.getHeight());
            var2.setColor(this.getBgColor());
            var2.translate(this.cropx, this.cropy);
            var2.fill(this.shape);
            AquaTabbedPaneCopyFromBasicUI.this.paintCroppedTabEdge(var1);
            var2.translate(-this.cropx, -this.cropy);
         }

      }
   }

   private class TabContainer extends JPanel implements UIResource {
      private boolean notifyTabbedPane = true;

      public TabContainer() {
         super((LayoutManager)null);
         this.setOpaque(false);
      }

      public void remove(Component var1) {
         int var2 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.indexOfTabComponent(var1);
         super.remove(var1);
         if (this.notifyTabbedPane && var2 != -1) {
            AquaTabbedPaneCopyFromBasicUI.this.tabPane.setTabComponentAt(var2, (Component)null);
         }

      }

      private void removeUnusedTabComponents() {
         Component[] var1 = this.getComponents();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Component var4 = var1[var3];
            if (!(var4 instanceof UIResource)) {
               int var5 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.indexOfTabComponent(var4);
               if (var5 == -1) {
                  super.remove(var4);
               }
            }
         }

      }

      public boolean isOptimizedDrawingEnabled() {
         return AquaTabbedPaneCopyFromBasicUI.this.tabScroller != null && !AquaTabbedPaneCopyFromBasicUI.this.tabScroller.croppedEdge.isParamsSet();
      }

      public void doLayout() {
         if (AquaTabbedPaneCopyFromBasicUI.this.scrollableTabLayoutEnabled()) {
            AquaTabbedPaneCopyFromBasicUI.this.tabScroller.tabPanel.repaint();
            AquaTabbedPaneCopyFromBasicUI.this.tabScroller.updateView();
         } else {
            AquaTabbedPaneCopyFromBasicUI.this.tabPane.repaint(this.getBounds());
         }

      }
   }

   public class FocusHandler extends FocusAdapter {
      public void focusGained(FocusEvent var1) {
         AquaTabbedPaneCopyFromBasicUI.this.getHandler().focusGained(var1);
      }

      public void focusLost(FocusEvent var1) {
         AquaTabbedPaneCopyFromBasicUI.this.getHandler().focusLost(var1);
      }
   }

   public class MouseHandler extends MouseAdapter {
      public void mousePressed(MouseEvent var1) {
         AquaTabbedPaneCopyFromBasicUI.this.getHandler().mousePressed(var1);
      }
   }

   public class TabSelectionHandler implements ChangeListener {
      public void stateChanged(ChangeEvent var1) {
         AquaTabbedPaneCopyFromBasicUI.this.getHandler().stateChanged(var1);
      }
   }

   public class PropertyChangeHandler implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         AquaTabbedPaneCopyFromBasicUI.this.getHandler().propertyChange(var1);
      }
   }

   private class Handler implements ChangeListener, ContainerListener, FocusListener, MouseListener, MouseMotionListener, PropertyChangeListener {
      private Handler() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         JTabbedPane var2 = (JTabbedPane)var1.getSource();
         String var3 = var1.getPropertyName();
         boolean var4 = AquaTabbedPaneCopyFromBasicUI.this.scrollableTabLayoutEnabled();
         if (var3 == "mnemonicAt") {
            AquaTabbedPaneCopyFromBasicUI.this.updateMnemonics();
            var2.repaint();
         } else if (var3 == "displayedMnemonicIndexAt") {
            var2.repaint();
         } else if (var3 == "indexForTitle") {
            AquaTabbedPaneCopyFromBasicUI.this.calculatedBaseline = false;
            this.updateHtmlViews((Integer)var1.getNewValue());
         } else if (var3 == "tabLayoutPolicy") {
            AquaTabbedPaneCopyFromBasicUI.this.uninstallUI(var2);
            AquaTabbedPaneCopyFromBasicUI.this.installUI(var2);
            AquaTabbedPaneCopyFromBasicUI.this.calculatedBaseline = false;
         } else if (var3 == "tabPlacement") {
            if (AquaTabbedPaneCopyFromBasicUI.this.scrollableTabLayoutEnabled()) {
               AquaTabbedPaneCopyFromBasicUI.this.tabScroller.createButtons();
            }

            AquaTabbedPaneCopyFromBasicUI.this.calculatedBaseline = false;
         } else if (var3 == "opaque" && var4) {
            boolean var8 = (Boolean)var1.getNewValue();
            AquaTabbedPaneCopyFromBasicUI.this.tabScroller.tabPanel.setOpaque(var8);
            AquaTabbedPaneCopyFromBasicUI.this.tabScroller.viewport.setOpaque(var8);
         } else if (var3 == "background" && var4) {
            Color var7 = (Color)var1.getNewValue();
            AquaTabbedPaneCopyFromBasicUI.this.tabScroller.tabPanel.setBackground(var7);
            AquaTabbedPaneCopyFromBasicUI.this.tabScroller.viewport.setBackground(var7);
            Color var6 = AquaTabbedPaneCopyFromBasicUI.this.selectedColor == null ? var7 : AquaTabbedPaneCopyFromBasicUI.this.selectedColor;
            AquaTabbedPaneCopyFromBasicUI.this.tabScroller.scrollForwardButton.setBackground(var6);
            AquaTabbedPaneCopyFromBasicUI.this.tabScroller.scrollBackwardButton.setBackground(var6);
         } else if (var3 == "indexForTabComponent") {
            if (AquaTabbedPaneCopyFromBasicUI.this.tabContainer != null) {
               AquaTabbedPaneCopyFromBasicUI.this.tabContainer.removeUnusedTabComponents();
            }

            Component var5 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabComponentAt((Integer)var1.getNewValue());
            if (var5 != null) {
               if (AquaTabbedPaneCopyFromBasicUI.this.tabContainer == null) {
                  AquaTabbedPaneCopyFromBasicUI.this.installTabContainer();
               } else {
                  AquaTabbedPaneCopyFromBasicUI.this.tabContainer.add(var5);
               }
            }

            AquaTabbedPaneCopyFromBasicUI.this.tabPane.revalidate();
            AquaTabbedPaneCopyFromBasicUI.this.tabPane.repaint();
            AquaTabbedPaneCopyFromBasicUI.this.calculatedBaseline = false;
         } else if (var3 == "indexForNullComponent") {
            AquaTabbedPaneCopyFromBasicUI.this.isRunsDirty = true;
            this.updateHtmlViews((Integer)var1.getNewValue());
         } else if (var3 == "font") {
            AquaTabbedPaneCopyFromBasicUI.this.calculatedBaseline = false;
         }

      }

      public void stateChanged(ChangeEvent var1) {
         JTabbedPane var2 = (JTabbedPane)var1.getSource();
         var2.revalidate();
         var2.repaint();
         AquaTabbedPaneCopyFromBasicUI.this.setFocusIndex(var2.getSelectedIndex(), false);
         if (AquaTabbedPaneCopyFromBasicUI.this.scrollableTabLayoutEnabled()) {
            int var3 = var2.getSelectedIndex();
            if (var3 < AquaTabbedPaneCopyFromBasicUI.this.rects.length && var3 != -1) {
               AquaTabbedPaneCopyFromBasicUI.this.tabScroller.tabPanel.scrollRectToVisible((Rectangle)AquaTabbedPaneCopyFromBasicUI.this.rects[var3].clone());
            }
         }

      }

      public void mouseClicked(MouseEvent var1) {
      }

      public void mouseReleased(MouseEvent var1) {
      }

      public void mouseEntered(MouseEvent var1) {
         AquaTabbedPaneCopyFromBasicUI.this.setRolloverTab(var1.getX(), var1.getY());
      }

      public void mouseExited(MouseEvent var1) {
         AquaTabbedPaneCopyFromBasicUI.this.setRolloverTab(-1);
      }

      public void mousePressed(MouseEvent var1) {
         if (AquaTabbedPaneCopyFromBasicUI.this.tabPane.isEnabled()) {
            int var2 = AquaTabbedPaneCopyFromBasicUI.this.tabForCoordinate(AquaTabbedPaneCopyFromBasicUI.this.tabPane, var1.getX(), var1.getY());
            if (var2 >= 0 && AquaTabbedPaneCopyFromBasicUI.this.tabPane.isEnabledAt(var2)) {
               if (var2 != AquaTabbedPaneCopyFromBasicUI.this.tabPane.getSelectedIndex()) {
                  AquaTabbedPaneCopyFromBasicUI.this.tabPane.setSelectedIndex(var2);
               } else if (AquaTabbedPaneCopyFromBasicUI.this.tabPane.isRequestFocusEnabled()) {
                  AquaTabbedPaneCopyFromBasicUI.this.tabPane.requestFocus();
               }
            }

         }
      }

      public void mouseDragged(MouseEvent var1) {
      }

      public void mouseMoved(MouseEvent var1) {
         AquaTabbedPaneCopyFromBasicUI.this.setRolloverTab(var1.getX(), var1.getY());
      }

      public void focusGained(FocusEvent var1) {
         AquaTabbedPaneCopyFromBasicUI.this.setFocusIndex(AquaTabbedPaneCopyFromBasicUI.this.tabPane.getSelectedIndex(), true);
      }

      public void focusLost(FocusEvent var1) {
         AquaTabbedPaneCopyFromBasicUI.this.repaintTab(AquaTabbedPaneCopyFromBasicUI.this.focusIndex);
      }

      public void componentAdded(ContainerEvent var1) {
         JTabbedPane var2 = (JTabbedPane)var1.getContainer();
         Component var3 = var1.getChild();
         if (!(var3 instanceof UIResource)) {
            AquaTabbedPaneCopyFromBasicUI.this.isRunsDirty = true;
            this.updateHtmlViews(var2.indexOfComponent(var3));
         }
      }

      private void updateHtmlViews(int var1) {
         String var2 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTitleAt(var1);
         boolean var3 = BasicHTML.isHTMLString(var2);
         if (var3) {
            if (AquaTabbedPaneCopyFromBasicUI.this.htmlViews == null) {
               AquaTabbedPaneCopyFromBasicUI.this.htmlViews = AquaTabbedPaneCopyFromBasicUI.this.createHTMLVector();
            } else {
               View var4 = BasicHTML.createHTMLView(AquaTabbedPaneCopyFromBasicUI.this.tabPane, var2);
               AquaTabbedPaneCopyFromBasicUI.this.htmlViews.insertElementAt(var4, var1);
            }
         } else if (AquaTabbedPaneCopyFromBasicUI.this.htmlViews != null) {
            AquaTabbedPaneCopyFromBasicUI.this.htmlViews.insertElementAt((Object)null, var1);
         }

         AquaTabbedPaneCopyFromBasicUI.this.updateMnemonics();
      }

      public void componentRemoved(ContainerEvent var1) {
         JTabbedPane var2 = (JTabbedPane)var1.getContainer();
         Component var3 = var1.getChild();
         if (!(var3 instanceof UIResource)) {
            Integer var4 = (Integer)var2.getClientProperty("__index_to_remove__");
            if (var4 != null) {
               int var5 = var4;
               if (AquaTabbedPaneCopyFromBasicUI.this.htmlViews != null && AquaTabbedPaneCopyFromBasicUI.this.htmlViews.size() > var5) {
                  AquaTabbedPaneCopyFromBasicUI.this.htmlViews.removeElementAt(var5);
               }

               var2.putClientProperty("__index_to_remove__", (Object)null);
            }

            AquaTabbedPaneCopyFromBasicUI.this.isRunsDirty = true;
            AquaTabbedPaneCopyFromBasicUI.this.updateMnemonics();
            AquaTabbedPaneCopyFromBasicUI.this.validateFocusIndex();
         }
      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }

   private class ScrollableTabButton extends BasicArrowButton implements UIResource, SwingConstants {
      public ScrollableTabButton(int var2) {
         super(var2, UIManager.getColor("TabbedPane.selected"), UIManager.getColor("TabbedPane.shadow"), UIManager.getColor("TabbedPane.darkShadow"), UIManager.getColor("TabbedPane.highlight"));
      }
   }

   private class ScrollableTabPanel extends JPanel implements UIResource {
      public ScrollableTabPanel() {
         super((LayoutManager)null);
         this.setOpaque(AquaTabbedPaneCopyFromBasicUI.this.tabPane.isOpaque());
         Color var2 = UIManager.getColor("TabbedPane.tabAreaBackground");
         if (var2 == null) {
            var2 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getBackground();
         }

         this.setBackground(var2);
      }

      public void paintComponent(Graphics var1) {
         super.paintComponent(var1);
         AquaTabbedPaneCopyFromBasicUI.this.paintTabArea(var1, AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabPlacement(), AquaTabbedPaneCopyFromBasicUI.this.tabPane.getSelectedIndex());
         if (AquaTabbedPaneCopyFromBasicUI.this.tabScroller.croppedEdge.isParamsSet() && AquaTabbedPaneCopyFromBasicUI.this.tabContainer == null) {
            Rectangle var2 = AquaTabbedPaneCopyFromBasicUI.this.rects[AquaTabbedPaneCopyFromBasicUI.this.tabScroller.croppedEdge.getTabIndex()];
            var1.translate(var2.x, var2.y);
            AquaTabbedPaneCopyFromBasicUI.this.tabScroller.croppedEdge.paintComponent(var1);
            var1.translate(-var2.x, -var2.y);
         }

      }

      public void doLayout() {
         if (this.getComponentCount() > 0) {
            Component var1 = this.getComponent(0);
            var1.setBounds(0, 0, this.getWidth(), this.getHeight());
         }

      }
   }

   private class ScrollableTabViewport extends JViewport implements UIResource {
      public ScrollableTabViewport() {
         this.setName("TabbedPane.scrollableViewport");
         this.setScrollMode(0);
         this.setOpaque(AquaTabbedPaneCopyFromBasicUI.this.tabPane.isOpaque());
         Color var2 = UIManager.getColor("TabbedPane.tabAreaBackground");
         if (var2 == null) {
            var2 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getBackground();
         }

         this.setBackground(var2);
      }
   }

   private class ScrollableTabSupport implements ActionListener, ChangeListener {
      public AquaTabbedPaneCopyFromBasicUI.ScrollableTabViewport viewport = AquaTabbedPaneCopyFromBasicUI.this.new ScrollableTabViewport();
      public AquaTabbedPaneCopyFromBasicUI.ScrollableTabPanel tabPanel = AquaTabbedPaneCopyFromBasicUI.this.new ScrollableTabPanel();
      public JButton scrollForwardButton;
      public JButton scrollBackwardButton;
      public AquaTabbedPaneCopyFromBasicUI.CroppedEdge croppedEdge;
      public int leadingTabIndex;
      private final Point tabViewPosition = new Point(0, 0);

      ScrollableTabSupport(int var2) {
         this.viewport.setView(this.tabPanel);
         this.viewport.addChangeListener(this);
         this.croppedEdge = AquaTabbedPaneCopyFromBasicUI.this.new CroppedEdge();
         this.createButtons();
      }

      void createButtons() {
         if (this.scrollForwardButton != null) {
            AquaTabbedPaneCopyFromBasicUI.this.tabPane.remove(this.scrollForwardButton);
            this.scrollForwardButton.removeActionListener(this);
            AquaTabbedPaneCopyFromBasicUI.this.tabPane.remove(this.scrollBackwardButton);
            this.scrollBackwardButton.removeActionListener(this);
         }

         int var1 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabPlacement();
         if (var1 != 1 && var1 != 3) {
            this.scrollForwardButton = AquaTabbedPaneCopyFromBasicUI.this.createScrollButton(5);
            this.scrollBackwardButton = AquaTabbedPaneCopyFromBasicUI.this.createScrollButton(1);
         } else {
            this.scrollForwardButton = AquaTabbedPaneCopyFromBasicUI.this.createScrollButton(3);
            this.scrollBackwardButton = AquaTabbedPaneCopyFromBasicUI.this.createScrollButton(7);
         }

         this.scrollForwardButton.addActionListener(this);
         this.scrollBackwardButton.addActionListener(this);
         AquaTabbedPaneCopyFromBasicUI.this.tabPane.add(this.scrollForwardButton);
         AquaTabbedPaneCopyFromBasicUI.this.tabPane.add(this.scrollBackwardButton);
      }

      public void scrollForward(int var1) {
         Dimension var2 = this.viewport.getViewSize();
         Rectangle var3 = this.viewport.getViewRect();
         if (var1 != 1 && var1 != 3) {
            if (var3.height >= var2.height - var3.y) {
               return;
            }
         } else if (var3.width >= var2.width - var3.x) {
            return;
         }

         this.setLeadingTabIndex(var1, this.leadingTabIndex + 1);
      }

      public void scrollBackward(int var1) {
         if (this.leadingTabIndex != 0) {
            this.setLeadingTabIndex(var1, this.leadingTabIndex - 1);
         }
      }

      public void setLeadingTabIndex(int var1, int var2) {
         this.leadingTabIndex = var2;
         Dimension var3 = this.viewport.getViewSize();
         Rectangle var4 = this.viewport.getViewRect();
         Dimension var5;
         switch(var1) {
         case 1:
         case 3:
            this.tabViewPosition.x = this.leadingTabIndex == 0 ? 0 : AquaTabbedPaneCopyFromBasicUI.this.rects[this.leadingTabIndex].x;
            if (var3.width - this.tabViewPosition.x < var4.width) {
               var5 = new Dimension(var3.width - this.tabViewPosition.x, var4.height);
               this.viewport.setExtentSize(var5);
            }
            break;
         case 2:
         case 4:
            this.tabViewPosition.y = this.leadingTabIndex == 0 ? 0 : AquaTabbedPaneCopyFromBasicUI.this.rects[this.leadingTabIndex].y;
            if (var3.height - this.tabViewPosition.y < var4.height) {
               var5 = new Dimension(var4.width, var3.height - this.tabViewPosition.y);
               this.viewport.setExtentSize(var5);
            }
         }

         this.viewport.setViewPosition(this.tabViewPosition);
      }

      public void stateChanged(ChangeEvent var1) {
         this.updateView();
      }

      private void updateView() {
         int var1 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabPlacement();
         int var2 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabCount();
         Rectangle var3 = this.viewport.getBounds();
         Dimension var4 = this.viewport.getViewSize();
         Rectangle var5 = this.viewport.getViewRect();
         this.leadingTabIndex = AquaTabbedPaneCopyFromBasicUI.this.getClosestTab(var5.x, var5.y);
         if (this.leadingTabIndex + 1 < var2) {
            switch(var1) {
            case 1:
            case 3:
               if (AquaTabbedPaneCopyFromBasicUI.this.rects[this.leadingTabIndex].x < var5.x) {
                  ++this.leadingTabIndex;
               }
               break;
            case 2:
            case 4:
               if (AquaTabbedPaneCopyFromBasicUI.this.rects[this.leadingTabIndex].y < var5.y) {
                  ++this.leadingTabIndex;
               }
            }
         }

         Insets var6 = AquaTabbedPaneCopyFromBasicUI.this.getContentBorderInsets(var1);
         switch(var1) {
         case 1:
         default:
            AquaTabbedPaneCopyFromBasicUI.this.tabPane.repaint(var3.x, var3.y + var3.height, var3.width, var6.top);
            this.scrollBackwardButton.setEnabled(var5.x > 0 && this.leadingTabIndex > 0);
            this.scrollForwardButton.setEnabled(this.leadingTabIndex < var2 - 1 && var4.width - var5.x > var5.width);
            break;
         case 2:
            AquaTabbedPaneCopyFromBasicUI.this.tabPane.repaint(var3.x + var3.width, var3.y, var6.left, var3.height);
            this.scrollBackwardButton.setEnabled(var5.y > 0 && this.leadingTabIndex > 0);
            this.scrollForwardButton.setEnabled(this.leadingTabIndex < var2 - 1 && var4.height - var5.y > var5.height);
            break;
         case 3:
            AquaTabbedPaneCopyFromBasicUI.this.tabPane.repaint(var3.x, var3.y - var6.bottom, var3.width, var6.bottom);
            this.scrollBackwardButton.setEnabled(var5.x > 0 && this.leadingTabIndex > 0);
            this.scrollForwardButton.setEnabled(this.leadingTabIndex < var2 - 1 && var4.width - var5.x > var5.width);
            break;
         case 4:
            AquaTabbedPaneCopyFromBasicUI.this.tabPane.repaint(var3.x - var6.right, var3.y, var6.right, var3.height);
            this.scrollBackwardButton.setEnabled(var5.y > 0 && this.leadingTabIndex > 0);
            this.scrollForwardButton.setEnabled(this.leadingTabIndex < var2 - 1 && var4.height - var5.y > var5.height);
         }

      }

      public void actionPerformed(ActionEvent var1) {
         ActionMap var2 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getActionMap();
         if (var2 != null) {
            String var3;
            if (var1.getSource() == this.scrollForwardButton) {
               var3 = "scrollTabsForwardAction";
            } else {
               var3 = "scrollTabsBackwardAction";
            }

            Action var4 = var2.get(var3);
            if (var4 != null && var4.isEnabled()) {
               var4.actionPerformed(new ActionEvent(AquaTabbedPaneCopyFromBasicUI.this.tabPane, 1001, (String)null, var1.getWhen(), var1.getModifiers()));
            }
         }

      }

      public String toString() {
         return new String("viewport.viewSize=" + this.viewport.getViewSize() + "\nviewport.viewRectangle=" + this.viewport.getViewRect() + "\nleadingTabIndex=" + this.leadingTabIndex + "\ntabViewPosition=" + this.tabViewPosition);
      }
   }

   class TabbedPaneScrollLayout extends AquaTabbedPaneCopyFromBasicUI.TabbedPaneLayout {
      TabbedPaneScrollLayout() {
         super();
      }

      protected int preferredTabAreaHeight(int var1, int var2) {
         return AquaTabbedPaneCopyFromBasicUI.this.calculateMaxTabHeight(var1);
      }

      protected int preferredTabAreaWidth(int var1, int var2) {
         return AquaTabbedPaneCopyFromBasicUI.this.calculateMaxTabWidth(var1);
      }

      public void layoutContainer(Container var1) {
         AquaTabbedPaneCopyFromBasicUI.this.setRolloverTab(-1);
         int var2 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabPlacement();
         int var3 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabCount();
         Insets var4 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getInsets();
         int var5 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getSelectedIndex();
         Component var6 = AquaTabbedPaneCopyFromBasicUI.this.getVisibleComponent();
         this.calculateLayoutInfo();
         Component var7 = null;
         if (var5 < 0) {
            if (var6 != null) {
               AquaTabbedPaneCopyFromBasicUI.this.setVisibleComponent((Component)null);
            }
         } else {
            var7 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getComponentAt(var5);
         }

         if (AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabCount() == 0) {
            AquaTabbedPaneCopyFromBasicUI.this.tabScroller.croppedEdge.resetParams();
            AquaTabbedPaneCopyFromBasicUI.this.tabScroller.scrollForwardButton.setVisible(false);
            AquaTabbedPaneCopyFromBasicUI.this.tabScroller.scrollBackwardButton.setVisible(false);
         } else {
            boolean var8 = false;
            if (var7 != null) {
               if (var7 != var6 && var6 != null && SwingUtilities.findFocusOwner(var6) != null) {
                  var8 = true;
               }

               AquaTabbedPaneCopyFromBasicUI.this.setVisibleComponent(var7);
            }

            Insets var17 = AquaTabbedPaneCopyFromBasicUI.this.getContentBorderInsets(var2);
            Rectangle var18 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getBounds();
            int var19 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getComponentCount();
            if (var19 > 0) {
               int var9;
               int var10;
               int var11;
               int var12;
               int var13;
               int var14;
               int var15;
               int var16;
               switch(var2) {
               case 1:
               default:
                  var11 = var18.width - var4.left - var4.right;
                  var12 = AquaTabbedPaneCopyFromBasicUI.this.calculateTabAreaHeight(var2, AquaTabbedPaneCopyFromBasicUI.this.runCount, AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight);
                  var9 = var4.left;
                  var10 = var4.top;
                  var13 = var9 + var17.left;
                  var14 = var10 + var12 + var17.top;
                  var15 = var18.width - var4.left - var4.right - var17.left - var17.right;
                  var16 = var18.height - var4.top - var4.bottom - var12 - var17.top - var17.bottom;
                  break;
               case 2:
                  var11 = AquaTabbedPaneCopyFromBasicUI.this.calculateTabAreaWidth(var2, AquaTabbedPaneCopyFromBasicUI.this.runCount, AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth);
                  var12 = var18.height - var4.top - var4.bottom;
                  var9 = var4.left;
                  var10 = var4.top;
                  var13 = var9 + var11 + var17.left;
                  var14 = var10 + var17.top;
                  var15 = var18.width - var4.left - var4.right - var11 - var17.left - var17.right;
                  var16 = var18.height - var4.top - var4.bottom - var17.top - var17.bottom;
                  break;
               case 3:
                  var11 = var18.width - var4.left - var4.right;
                  var12 = AquaTabbedPaneCopyFromBasicUI.this.calculateTabAreaHeight(var2, AquaTabbedPaneCopyFromBasicUI.this.runCount, AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight);
                  var9 = var4.left;
                  var10 = var18.height - var4.bottom - var12;
                  var13 = var4.left + var17.left;
                  var14 = var4.top + var17.top;
                  var15 = var18.width - var4.left - var4.right - var17.left - var17.right;
                  var16 = var18.height - var4.top - var4.bottom - var12 - var17.top - var17.bottom;
                  break;
               case 4:
                  var11 = AquaTabbedPaneCopyFromBasicUI.this.calculateTabAreaWidth(var2, AquaTabbedPaneCopyFromBasicUI.this.runCount, AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth);
                  var12 = var18.height - var4.top - var4.bottom;
                  var9 = var18.width - var4.right - var11;
                  var10 = var4.top;
                  var13 = var4.left + var17.left;
                  var14 = var4.top + var17.top;
                  var15 = var18.width - var4.left - var4.right - var11 - var17.left - var17.right;
                  var16 = var18.height - var4.top - var4.bottom - var17.top - var17.bottom;
               }

               for(int var20 = 0; var20 < var19; ++var20) {
                  Component var21 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getComponent(var20);
                  int var24;
                  int var25;
                  int var27;
                  if (AquaTabbedPaneCopyFromBasicUI.this.tabScroller != null && var21 == AquaTabbedPaneCopyFromBasicUI.this.tabScroller.viewport) {
                     JViewport var22 = (JViewport)var21;
                     Rectangle var31 = var22.getViewRect();
                     var24 = var11;
                     var25 = var12;
                     Dimension var32 = AquaTabbedPaneCopyFromBasicUI.this.tabScroller.scrollForwardButton.getPreferredSize();
                     switch(var2) {
                     case 1:
                     case 3:
                     default:
                        int var33 = AquaTabbedPaneCopyFromBasicUI.this.rects[var3 - 1].x + AquaTabbedPaneCopyFromBasicUI.this.rects[var3 - 1].width;
                        if (var33 > var11) {
                           var24 = var11 > 2 * var32.width ? var11 - 2 * var32.width : 0;
                           if (var33 - var31.x <= var24) {
                              var24 = var33 - var31.x;
                           }
                        }
                        break;
                     case 2:
                     case 4:
                        var27 = AquaTabbedPaneCopyFromBasicUI.this.rects[var3 - 1].y + AquaTabbedPaneCopyFromBasicUI.this.rects[var3 - 1].height;
                        if (var27 > var12) {
                           var25 = var12 > 2 * var32.height ? var12 - 2 * var32.height : 0;
                           if (var27 - var31.y <= var25) {
                              var25 = var27 - var31.y;
                           }
                        }
                     }

                     var21.setBounds(var9, var10, var24, var25);
                  } else if (AquaTabbedPaneCopyFromBasicUI.this.tabScroller == null || var21 != AquaTabbedPaneCopyFromBasicUI.this.tabScroller.scrollForwardButton && var21 != AquaTabbedPaneCopyFromBasicUI.this.tabScroller.scrollBackwardButton) {
                     var21.setBounds(var13, var14, var15, var16);
                  } else {
                     Dimension var23 = var21.getPreferredSize();
                     var24 = 0;
                     var25 = 0;
                     int var26 = var23.width;
                     var27 = var23.height;
                     boolean var28 = false;
                     switch(var2) {
                     case 1:
                     case 3:
                     default:
                        int var30 = AquaTabbedPaneCopyFromBasicUI.this.rects[var3 - 1].x + AquaTabbedPaneCopyFromBasicUI.this.rects[var3 - 1].width;
                        if (var30 > var11) {
                           var28 = true;
                           var24 = var21 == AquaTabbedPaneCopyFromBasicUI.this.tabScroller.scrollForwardButton ? var18.width - var4.left - var23.width : var18.width - var4.left - 2 * var23.width;
                           var25 = var2 == 1 ? var10 + var12 - var23.height : var10;
                        }
                        break;
                     case 2:
                     case 4:
                        int var29 = AquaTabbedPaneCopyFromBasicUI.this.rects[var3 - 1].y + AquaTabbedPaneCopyFromBasicUI.this.rects[var3 - 1].height;
                        if (var29 > var12) {
                           var28 = true;
                           var24 = var2 == 2 ? var9 + var11 - var23.width : var9;
                           var25 = var21 == AquaTabbedPaneCopyFromBasicUI.this.tabScroller.scrollForwardButton ? var18.height - var4.bottom - var23.height : var18.height - var4.bottom - 2 * var23.height;
                        }
                     }

                     var21.setVisible(var28);
                     if (var28) {
                        var21.setBounds(var24, var25, var26, var27);
                     }
                  }
               }

               super.layoutTabComponents();
               this.layoutCroppedEdge();
               if (var8 && !AquaTabbedPaneCopyFromBasicUI.this.requestFocusForVisibleComponent()) {
                  AquaTabbedPaneCopyFromBasicUI.this.tabPane.requestFocus();
               }
            }

         }
      }

      private void layoutCroppedEdge() {
         AquaTabbedPaneCopyFromBasicUI.this.tabScroller.croppedEdge.resetParams();
         Rectangle var1 = AquaTabbedPaneCopyFromBasicUI.this.tabScroller.viewport.getViewRect();

         for(int var3 = 0; var3 < AquaTabbedPaneCopyFromBasicUI.this.rects.length; ++var3) {
            Rectangle var4 = AquaTabbedPaneCopyFromBasicUI.this.rects[var3];
            int var2;
            switch(AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabPlacement()) {
            case 1:
            case 3:
            default:
               var2 = var1.x + var1.width;
               if (var4.x < var2 - 1 && var4.x + var4.width > var2) {
                  int var10002 = var2 - var4.x - 1;
                  AquaTabbedPaneCopyFromBasicUI.this.tabScroller.croppedEdge.setParams(var3, var10002, 0, -AquaTabbedPaneCopyFromBasicUI.this.currentTabAreaInsets.top);
               }
               break;
            case 2:
            case 4:
               var2 = var1.y + var1.height;
               if (var4.y < var2 && var4.y + var4.height > var2) {
                  AquaTabbedPaneCopyFromBasicUI.this.tabScroller.croppedEdge.setParams(var3, var2 - var4.y - 1, -AquaTabbedPaneCopyFromBasicUI.this.currentTabAreaInsets.left, 0);
               }
            }
         }

      }

      protected void calculateTabRects(int var1, int var2) {
         FontMetrics var3 = AquaTabbedPaneCopyFromBasicUI.this.getFontMetrics();
         Dimension var4 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getSize();
         Insets var5 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getInsets();
         Insets var6 = AquaTabbedPaneCopyFromBasicUI.this.getTabAreaInsets(var1);
         int var7 = var3.getHeight();
         int var8 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getSelectedIndex();
         boolean var10 = var1 == 2 || var1 == 4;
         boolean var11 = AquaUtils.isLeftToRight(AquaTabbedPaneCopyFromBasicUI.this.tabPane);
         int var12 = var6.left;
         int var13 = var6.top;
         int var14 = 0;
         int var15 = 0;
         switch(var1) {
         case 1:
         case 3:
         default:
            AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight = AquaTabbedPaneCopyFromBasicUI.this.calculateMaxTabHeight(var1);
            break;
         case 2:
         case 4:
            AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth = AquaTabbedPaneCopyFromBasicUI.this.calculateMaxTabWidth(var1);
         }

         AquaTabbedPaneCopyFromBasicUI.this.runCount = 0;
         AquaTabbedPaneCopyFromBasicUI.this.selectedRun = -1;
         if (var2 != 0) {
            AquaTabbedPaneCopyFromBasicUI.this.selectedRun = 0;
            AquaTabbedPaneCopyFromBasicUI.this.runCount = 1;

            int var9;
            for(var9 = 0; var9 < var2; ++var9) {
               Rectangle var16 = AquaTabbedPaneCopyFromBasicUI.this.rects[var9];
               if (!var10) {
                  if (var9 > 0) {
                     var16.x = AquaTabbedPaneCopyFromBasicUI.this.rects[var9 - 1].x + AquaTabbedPaneCopyFromBasicUI.this.rects[var9 - 1].width;
                  } else {
                     AquaTabbedPaneCopyFromBasicUI.this.tabRuns[0] = 0;
                     AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth = 0;
                     var15 += AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight;
                     var16.x = var12;
                  }

                  var16.width = AquaTabbedPaneCopyFromBasicUI.this.calculateTabWidth(var1, var9, var3);
                  var14 = var16.x + var16.width;
                  AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth = Math.max(AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth, var16.width);
                  var16.y = var13;
                  var16.height = AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight;
               } else {
                  if (var9 > 0) {
                     var16.y = AquaTabbedPaneCopyFromBasicUI.this.rects[var9 - 1].y + AquaTabbedPaneCopyFromBasicUI.this.rects[var9 - 1].height;
                  } else {
                     AquaTabbedPaneCopyFromBasicUI.this.tabRuns[0] = 0;
                     AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight = 0;
                     var14 = AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth;
                     var16.y = var13;
                  }

                  var16.height = AquaTabbedPaneCopyFromBasicUI.this.calculateTabHeight(var1, var9, var7);
                  var15 = var16.y + var16.height;
                  AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight = Math.max(AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight, var16.height);
                  var16.x = var12;
                  var16.width = AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth;
               }
            }

            if (AquaTabbedPaneCopyFromBasicUI.this.tabsOverlapBorder) {
               this.padSelectedTab(var1, var8);
            }

            if (!var11 && !var10) {
               int var17 = var4.width - (var5.right + var6.right);

               for(var9 = 0; var9 < var2; ++var9) {
                  AquaTabbedPaneCopyFromBasicUI.this.rects[var9].x = var17 - AquaTabbedPaneCopyFromBasicUI.this.rects[var9].x - AquaTabbedPaneCopyFromBasicUI.this.rects[var9].width;
               }
            }

            AquaTabbedPaneCopyFromBasicUI.this.tabScroller.tabPanel.setPreferredSize(new Dimension(var14, var15));
         }
      }
   }

   public class TabbedPaneLayout implements LayoutManager {
      protected Container getTabContainer() {
         return AquaTabbedPaneCopyFromBasicUI.this.tabContainer;
      }

      public void addLayoutComponent(String var1, Component var2) {
      }

      public void removeLayoutComponent(Component var1) {
      }

      public Dimension preferredLayoutSize(Container var1) {
         return this.calculateSize(false);
      }

      public Dimension minimumLayoutSize(Container var1) {
         return this.calculateSize(true);
      }

      protected Dimension calculateSize(boolean var1) {
         int var2 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabPlacement();
         Insets var3 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getInsets();
         Insets var4 = AquaTabbedPaneCopyFromBasicUI.this.getContentBorderInsets(var2);
         Insets var5 = AquaTabbedPaneCopyFromBasicUI.this.getTabAreaInsets(var2);
         Dimension var6 = new Dimension(0, 0);
         byte var7 = 0;
         byte var8 = 0;
         int var9 = 0;
         int var10 = 0;

         int var11;
         for(var11 = 0; var11 < AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabCount(); ++var11) {
            Component var12 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getComponentAt(var11);
            if (var12 != null) {
               Dimension var13 = var1 ? var12.getMinimumSize() : var12.getPreferredSize();
               if (var13 != null) {
                  var10 = Math.max(var13.height, var10);
                  var9 = Math.max(var13.width, var9);
               }
            }
         }

         int var15 = var8 + var9;
         int var14 = var7 + var10;
         boolean var16 = false;
         switch(var2) {
         case 1:
         case 3:
         default:
            var15 = Math.max(var15, AquaTabbedPaneCopyFromBasicUI.this.calculateMaxTabWidth(var2));
            var11 = this.preferredTabAreaHeight(var2, var15 - var5.left - var5.right);
            var14 += var11;
            break;
         case 2:
         case 4:
            var14 = Math.max(var14, AquaTabbedPaneCopyFromBasicUI.this.calculateMaxTabHeight(var2));
            var11 = this.preferredTabAreaWidth(var2, var14 - var5.top - var5.bottom);
            var15 += var11;
         }

         return new Dimension(var15 + var3.left + var3.right + var4.left + var4.right, var14 + var3.bottom + var3.top + var4.top + var4.bottom);
      }

      protected int preferredTabAreaHeight(int var1, int var2) {
         FontMetrics var3 = AquaTabbedPaneCopyFromBasicUI.this.getFontMetrics();
         int var4 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabCount();
         int var5 = 0;
         if (var4 > 0) {
            int var6 = 1;
            int var7 = 0;
            int var8 = AquaTabbedPaneCopyFromBasicUI.this.calculateMaxTabHeight(var1);

            for(int var9 = 0; var9 < var4; ++var9) {
               int var10 = AquaTabbedPaneCopyFromBasicUI.this.calculateTabWidth(var1, var9, var3);
               if (var7 != 0 && var7 + var10 > var2) {
                  ++var6;
                  var7 = 0;
               }

               var7 += var10;
            }

            var5 = AquaTabbedPaneCopyFromBasicUI.this.calculateTabAreaHeight(var1, var6, var8);
         }

         return var5;
      }

      protected int preferredTabAreaWidth(int var1, int var2) {
         FontMetrics var3 = AquaTabbedPaneCopyFromBasicUI.this.getFontMetrics();
         int var4 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabCount();
         int var5 = 0;
         if (var4 > 0) {
            int var6 = 1;
            int var7 = 0;
            int var8 = var3.getHeight();
            AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth = AquaTabbedPaneCopyFromBasicUI.this.calculateMaxTabWidth(var1);

            for(int var9 = 0; var9 < var4; ++var9) {
               int var10 = AquaTabbedPaneCopyFromBasicUI.this.calculateTabHeight(var1, var9, var8);
               if (var7 != 0 && var7 + var10 > var2) {
                  ++var6;
                  var7 = 0;
               }

               var7 += var10;
            }

            var5 = AquaTabbedPaneCopyFromBasicUI.this.calculateTabAreaWidth(var1, var6, AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth);
         }

         return var5;
      }

      public void layoutContainer(Container var1) {
         AquaTabbedPaneCopyFromBasicUI.this.setRolloverTab(-1);
         int var2 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabPlacement();
         Insets var3 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getInsets();
         int var4 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getSelectedIndex();
         Component var5 = AquaTabbedPaneCopyFromBasicUI.this.getVisibleComponent();
         this.calculateLayoutInfo();
         Component var6 = null;
         if (var4 < 0) {
            if (var5 != null) {
               AquaTabbedPaneCopyFromBasicUI.this.setVisibleComponent((Component)null);
            }
         } else {
            var6 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getComponentAt(var4);
         }

         int var11 = 0;
         int var12 = 0;
         Insets var13 = AquaTabbedPaneCopyFromBasicUI.this.getContentBorderInsets(var2);
         boolean var14 = false;
         if (var6 != null) {
            if (var6 != var5 && var5 != null && SwingUtilities.findFocusOwner(var5) != null) {
               var14 = true;
            }

            AquaTabbedPaneCopyFromBasicUI.this.setVisibleComponent(var6);
         }

         Rectangle var15 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getBounds();
         int var16 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getComponentCount();
         if (var16 > 0) {
            int var7;
            int var8;
            switch(var2) {
            case 1:
            default:
               var12 = AquaTabbedPaneCopyFromBasicUI.this.calculateTabAreaHeight(var2, AquaTabbedPaneCopyFromBasicUI.this.runCount, AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight);
               var7 = var3.left + var13.left;
               var8 = var3.top + var12 + var13.top;
               break;
            case 2:
               var11 = AquaTabbedPaneCopyFromBasicUI.this.calculateTabAreaWidth(var2, AquaTabbedPaneCopyFromBasicUI.this.runCount, AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth);
               var7 = var3.left + var11 + var13.left;
               var8 = var3.top + var13.top;
               break;
            case 3:
               var12 = AquaTabbedPaneCopyFromBasicUI.this.calculateTabAreaHeight(var2, AquaTabbedPaneCopyFromBasicUI.this.runCount, AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight);
               var7 = var3.left + var13.left;
               var8 = var3.top + var13.top;
               break;
            case 4:
               var11 = AquaTabbedPaneCopyFromBasicUI.this.calculateTabAreaWidth(var2, AquaTabbedPaneCopyFromBasicUI.this.runCount, AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth);
               var7 = var3.left + var13.left;
               var8 = var3.top + var13.top;
            }

            int var9 = var15.width - var11 - var3.left - var3.right - var13.left - var13.right;
            int var10 = var15.height - var12 - var3.top - var3.bottom - var13.top - var13.bottom;

            for(int var17 = 0; var17 < var16; ++var17) {
               Component var18 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getComponent(var17);
               if (var18 == AquaTabbedPaneCopyFromBasicUI.this.tabContainer) {
                  int var19 = var11 == 0 ? var15.width : var11 + var3.left + var3.right + var13.left + var13.right;
                  int var20 = var12 == 0 ? var15.height : var12 + var3.top + var3.bottom + var13.top + var13.bottom;
                  int var21 = 0;
                  int var22 = 0;
                  if (var2 == 3) {
                     var22 = var15.height - var20;
                  } else if (var2 == 4) {
                     var21 = var15.width - var19;
                  }

                  var18.setBounds(var21, var22, var19, var20);
               } else {
                  var18.setBounds(var7, var8, var9, var10);
               }
            }
         }

         this.layoutTabComponents();
         if (var14 && !AquaTabbedPaneCopyFromBasicUI.this.requestFocusForVisibleComponent()) {
            AquaTabbedPaneCopyFromBasicUI.this.tabPane.requestFocus();
         }

      }

      public void calculateLayoutInfo() {
         int var1 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabCount();
         AquaTabbedPaneCopyFromBasicUI.this.assureRectsCreated(var1);
         this.calculateTabRects(AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabPlacement(), var1);
         AquaTabbedPaneCopyFromBasicUI.this.isRunsDirty = false;
      }

      protected void layoutTabComponents() {
         if (AquaTabbedPaneCopyFromBasicUI.this.tabContainer != null) {
            Rectangle var1 = new Rectangle();
            Point var2 = new Point(-AquaTabbedPaneCopyFromBasicUI.this.tabContainer.getX(), -AquaTabbedPaneCopyFromBasicUI.this.tabContainer.getY());
            if (AquaTabbedPaneCopyFromBasicUI.this.scrollableTabLayoutEnabled()) {
               AquaTabbedPaneCopyFromBasicUI.this.translatePointToTabPanel(0, 0, var2);
            }

            for(int var3 = 0; var3 < AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabCount(); ++var3) {
               Component var4 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabComponentAt(var3);
               if (var4 != null) {
                  AquaTabbedPaneCopyFromBasicUI.this.getTabBounds(var3, var1);
                  Dimension var5 = var4.getPreferredSize();
                  Insets var6 = AquaTabbedPaneCopyFromBasicUI.this.getTabInsets(AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabPlacement(), var3);
                  int var7 = var1.x + var6.left + var2.x;
                  int var8 = var1.y + var6.top + var2.y;
                  int var9 = var1.width - var6.left - var6.right;
                  int var10 = var1.height - var6.top - var6.bottom;
                  int var11 = var7 + (var9 - var5.width) / 2;
                  int var12 = var8 + (var10 - var5.height) / 2;
                  int var13 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getTabPlacement();
                  boolean var14 = var3 == AquaTabbedPaneCopyFromBasicUI.this.tabPane.getSelectedIndex();
                  var4.setBounds(var11 + AquaTabbedPaneCopyFromBasicUI.this.getTabLabelShiftX(var13, var3, var14), var12 + AquaTabbedPaneCopyFromBasicUI.this.getTabLabelShiftY(var13, var3, var14), var5.width, var5.height);
               }
            }

         }
      }

      protected void calculateTabRects(int var1, int var2) {
         FontMetrics var3 = AquaTabbedPaneCopyFromBasicUI.this.getFontMetrics();
         Dimension var4 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getSize();
         Insets var5 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getInsets();
         Insets var6 = AquaTabbedPaneCopyFromBasicUI.this.getTabAreaInsets(var1);
         int var7 = var3.getHeight();
         int var8 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getSelectedIndex();
         boolean var15 = var1 == 2 || var1 == 4;
         boolean var16 = AquaUtils.isLeftToRight(AquaTabbedPaneCopyFromBasicUI.this.tabPane);
         int var12;
         int var13;
         int var14;
         switch(var1) {
         case 1:
         default:
            AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight = AquaTabbedPaneCopyFromBasicUI.this.calculateMaxTabHeight(var1);
            var12 = var5.left + var6.left;
            var13 = var5.top + var6.top;
            var14 = var4.width - (var5.right + var6.right);
            break;
         case 2:
            AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth = AquaTabbedPaneCopyFromBasicUI.this.calculateMaxTabWidth(var1);
            var12 = var5.left + var6.left;
            var13 = var5.top + var6.top;
            var14 = var4.height - (var5.bottom + var6.bottom);
            break;
         case 3:
            AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight = AquaTabbedPaneCopyFromBasicUI.this.calculateMaxTabHeight(var1);
            var12 = var5.left + var6.left;
            var13 = var4.height - var5.bottom - var6.bottom - AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight;
            var14 = var4.width - (var5.right + var6.right);
            break;
         case 4:
            AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth = AquaTabbedPaneCopyFromBasicUI.this.calculateMaxTabWidth(var1);
            var12 = var4.width - var5.right - var6.right - AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth;
            var13 = var5.top + var6.top;
            var14 = var4.height - (var5.bottom + var6.bottom);
         }

         int var9 = AquaTabbedPaneCopyFromBasicUI.this.getTabRunOverlay(var1);
         AquaTabbedPaneCopyFromBasicUI.this.runCount = 0;
         AquaTabbedPaneCopyFromBasicUI.this.selectedRun = -1;
         if (var2 != 0) {
            int var10;
            Rectangle var17;
            for(var10 = 0; var10 < var2; ++var10) {
               var17 = AquaTabbedPaneCopyFromBasicUI.this.rects[var10];
               if (!var15) {
                  if (var10 > 0) {
                     var17.x = AquaTabbedPaneCopyFromBasicUI.this.rects[var10 - 1].x + AquaTabbedPaneCopyFromBasicUI.this.rects[var10 - 1].width;
                  } else {
                     AquaTabbedPaneCopyFromBasicUI.this.tabRuns[0] = 0;
                     AquaTabbedPaneCopyFromBasicUI.this.runCount = 1;
                     AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth = 0;
                     var17.x = var12;
                  }

                  var17.width = AquaTabbedPaneCopyFromBasicUI.this.calculateTabWidth(var1, var10, var3);
                  AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth = Math.max(AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth, var17.width);
                  if (var17.x != 2 + var5.left && var17.x + var17.width > var14) {
                     if (AquaTabbedPaneCopyFromBasicUI.this.runCount > AquaTabbedPaneCopyFromBasicUI.this.tabRuns.length - 1) {
                        AquaTabbedPaneCopyFromBasicUI.this.expandTabRunsArray();
                     }

                     AquaTabbedPaneCopyFromBasicUI.this.tabRuns[AquaTabbedPaneCopyFromBasicUI.this.runCount] = var10;
                     ++AquaTabbedPaneCopyFromBasicUI.this.runCount;
                     var17.x = var12;
                  }

                  var17.y = var13;
                  var17.height = AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight;
               } else {
                  if (var10 > 0) {
                     var17.y = AquaTabbedPaneCopyFromBasicUI.this.rects[var10 - 1].y + AquaTabbedPaneCopyFromBasicUI.this.rects[var10 - 1].height;
                  } else {
                     AquaTabbedPaneCopyFromBasicUI.this.tabRuns[0] = 0;
                     AquaTabbedPaneCopyFromBasicUI.this.runCount = 1;
                     AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight = 0;
                     var17.y = var13;
                  }

                  var17.height = AquaTabbedPaneCopyFromBasicUI.this.calculateTabHeight(var1, var10, var7);
                  AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight = Math.max(AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight, var17.height);
                  if (var17.y != 2 + var5.top && var17.y + var17.height > var14) {
                     if (AquaTabbedPaneCopyFromBasicUI.this.runCount > AquaTabbedPaneCopyFromBasicUI.this.tabRuns.length - 1) {
                        AquaTabbedPaneCopyFromBasicUI.this.expandTabRunsArray();
                     }

                     AquaTabbedPaneCopyFromBasicUI.this.tabRuns[AquaTabbedPaneCopyFromBasicUI.this.runCount] = var10;
                     ++AquaTabbedPaneCopyFromBasicUI.this.runCount;
                     var17.y = var13;
                  }

                  var17.x = var12;
                  var17.width = AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth;
               }

               if (var10 == var8) {
                  AquaTabbedPaneCopyFromBasicUI.this.selectedRun = AquaTabbedPaneCopyFromBasicUI.this.runCount - 1;
               }
            }

            if (AquaTabbedPaneCopyFromBasicUI.this.runCount > 1) {
               this.normalizeTabRuns(var1, var2, var15 ? var13 : var12, var14);
               AquaTabbedPaneCopyFromBasicUI.this.selectedRun = AquaTabbedPaneCopyFromBasicUI.this.getRunForTab(var2, var8);
               if (AquaTabbedPaneCopyFromBasicUI.this.shouldRotateTabRuns(var1)) {
                  this.rotateTabRuns(var1, AquaTabbedPaneCopyFromBasicUI.this.selectedRun);
               }
            }

            int var18;
            for(var10 = AquaTabbedPaneCopyFromBasicUI.this.runCount - 1; var10 >= 0; --var10) {
               var18 = AquaTabbedPaneCopyFromBasicUI.this.tabRuns[var10];
               int var19 = AquaTabbedPaneCopyFromBasicUI.this.tabRuns[var10 == AquaTabbedPaneCopyFromBasicUI.this.runCount - 1 ? 0 : var10 + 1];
               int var20 = var19 != 0 ? var19 - 1 : var2 - 1;
               int var11;
               if (!var15) {
                  for(var11 = var18; var11 <= var20; ++var11) {
                     var17 = AquaTabbedPaneCopyFromBasicUI.this.rects[var11];
                     var17.y = var13;
                     var17.x += AquaTabbedPaneCopyFromBasicUI.this.getTabRunIndent(var1, var10);
                  }

                  if (AquaTabbedPaneCopyFromBasicUI.this.shouldPadTabRun(var1, var10)) {
                     this.padTabRun(var1, var18, var20, var14);
                  }

                  if (var1 == 3) {
                     var13 -= AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight - var9;
                  } else {
                     var13 += AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight - var9;
                  }
               } else {
                  for(var11 = var18; var11 <= var20; ++var11) {
                     var17 = AquaTabbedPaneCopyFromBasicUI.this.rects[var11];
                     var17.x = var12;
                     var17.y += AquaTabbedPaneCopyFromBasicUI.this.getTabRunIndent(var1, var10);
                  }

                  if (AquaTabbedPaneCopyFromBasicUI.this.shouldPadTabRun(var1, var10)) {
                     this.padTabRun(var1, var18, var20, var14);
                  }

                  if (var1 == 4) {
                     var12 -= AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth - var9;
                  } else {
                     var12 += AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth - var9;
                  }
               }
            }

            this.padSelectedTab(var1, var8);
            if (!var16 && !var15) {
               var18 = var4.width - (var5.right + var6.right);

               for(var10 = 0; var10 < var2; ++var10) {
                  AquaTabbedPaneCopyFromBasicUI.this.rects[var10].x = var18 - AquaTabbedPaneCopyFromBasicUI.this.rects[var10].x - AquaTabbedPaneCopyFromBasicUI.this.rects[var10].width;
               }
            }

         }
      }

      protected void rotateTabRuns(int var1, int var2) {
         for(int var3 = 0; var3 < var2; ++var3) {
            int var4 = AquaTabbedPaneCopyFromBasicUI.this.tabRuns[0];

            for(int var5 = 1; var5 < AquaTabbedPaneCopyFromBasicUI.this.runCount; ++var5) {
               AquaTabbedPaneCopyFromBasicUI.this.tabRuns[var5 - 1] = AquaTabbedPaneCopyFromBasicUI.this.tabRuns[var5];
            }

            AquaTabbedPaneCopyFromBasicUI.this.tabRuns[AquaTabbedPaneCopyFromBasicUI.this.runCount - 1] = var4;
         }

      }

      protected void normalizeTabRuns(int var1, int var2, int var3, int var4) {
         boolean var5 = var1 == 2 || var1 == 4;
         int var6 = AquaTabbedPaneCopyFromBasicUI.this.runCount - 1;
         boolean var7 = true;
         double var8 = 1.25D;

         while(var7) {
            int var10 = AquaTabbedPaneCopyFromBasicUI.this.lastTabInRun(var2, var6);
            int var11 = AquaTabbedPaneCopyFromBasicUI.this.lastTabInRun(var2, var6 - 1);
            int var12;
            int var13;
            if (!var5) {
               var12 = AquaTabbedPaneCopyFromBasicUI.this.rects[var10].x + AquaTabbedPaneCopyFromBasicUI.this.rects[var10].width;
               var13 = (int)((double)AquaTabbedPaneCopyFromBasicUI.this.maxTabWidth * var8);
            } else {
               var12 = AquaTabbedPaneCopyFromBasicUI.this.rects[var10].y + AquaTabbedPaneCopyFromBasicUI.this.rects[var10].height;
               var13 = (int)((double)AquaTabbedPaneCopyFromBasicUI.this.maxTabHeight * var8 * 2.0D);
            }

            if (var4 - var12 > var13) {
               AquaTabbedPaneCopyFromBasicUI.this.tabRuns[var6] = var11;
               if (!var5) {
                  AquaTabbedPaneCopyFromBasicUI.this.rects[var11].x = var3;
               } else {
                  AquaTabbedPaneCopyFromBasicUI.this.rects[var11].y = var3;
               }

               for(int var14 = var11 + 1; var14 <= var10; ++var14) {
                  if (!var5) {
                     AquaTabbedPaneCopyFromBasicUI.this.rects[var14].x = AquaTabbedPaneCopyFromBasicUI.this.rects[var14 - 1].x + AquaTabbedPaneCopyFromBasicUI.this.rects[var14 - 1].width;
                  } else {
                     AquaTabbedPaneCopyFromBasicUI.this.rects[var14].y = AquaTabbedPaneCopyFromBasicUI.this.rects[var14 - 1].y + AquaTabbedPaneCopyFromBasicUI.this.rects[var14 - 1].height;
                  }
               }
            } else if (var6 == AquaTabbedPaneCopyFromBasicUI.this.runCount - 1) {
               var7 = false;
            }

            if (var6 - 1 > 0) {
               --var6;
            } else {
               var6 = AquaTabbedPaneCopyFromBasicUI.this.runCount - 1;
               var8 += 0.25D;
            }
         }

      }

      protected void padTabRun(int var1, int var2, int var3, int var4) {
         Rectangle var5 = AquaTabbedPaneCopyFromBasicUI.this.rects[var3];
         int var6;
         int var7;
         float var8;
         int var9;
         Rectangle var10;
         if (var1 != 1 && var1 != 3) {
            var6 = var5.y + var5.height - AquaTabbedPaneCopyFromBasicUI.this.rects[var2].y;
            var7 = var4 - (var5.y + var5.height);
            var8 = (float)var7 / (float)var6;

            for(var9 = var2; var9 <= var3; ++var9) {
               var10 = AquaTabbedPaneCopyFromBasicUI.this.rects[var9];
               if (var9 > var2) {
                  var10.y = AquaTabbedPaneCopyFromBasicUI.this.rects[var9 - 1].y + AquaTabbedPaneCopyFromBasicUI.this.rects[var9 - 1].height;
               }

               var10.height += Math.round((float)var10.height * var8);
            }

            var5.height = var4 - var5.y;
         } else {
            var6 = var5.x + var5.width - AquaTabbedPaneCopyFromBasicUI.this.rects[var2].x;
            var7 = var4 - (var5.x + var5.width);
            var8 = (float)var7 / (float)var6;

            for(var9 = var2; var9 <= var3; ++var9) {
               var10 = AquaTabbedPaneCopyFromBasicUI.this.rects[var9];
               if (var9 > var2) {
                  var10.x = AquaTabbedPaneCopyFromBasicUI.this.rects[var9 - 1].x + AquaTabbedPaneCopyFromBasicUI.this.rects[var9 - 1].width;
               }

               var10.width += Math.round((float)var10.width * var8);
            }

            var5.width = var4 - var5.x;
         }

      }

      protected void padSelectedTab(int var1, int var2) {
         if (var2 >= 0) {
            Rectangle var3 = AquaTabbedPaneCopyFromBasicUI.this.rects[var2];
            Insets var4 = AquaTabbedPaneCopyFromBasicUI.this.getSelectedTabPadInsets(var1);
            var3.x -= var4.left;
            var3.width += var4.left + var4.right;
            var3.y -= var4.top;
            var3.height += var4.top + var4.bottom;
            if (!AquaTabbedPaneCopyFromBasicUI.this.scrollableTabLayoutEnabled()) {
               Dimension var5 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getSize();
               Insets var6 = AquaTabbedPaneCopyFromBasicUI.this.tabPane.getInsets();
               int var7;
               int var8;
               if (var1 != 2 && var1 != 4) {
                  var7 = var6.left - var3.x;
                  if (var7 > 0) {
                     var3.x += var7;
                     var3.width -= var7;
                  }

                  var8 = var3.x + var3.width + var6.right - var5.width;
                  if (var8 > 0) {
                     var3.width -= var8;
                  }
               } else {
                  var7 = var6.top - var3.y;
                  if (var7 > 0) {
                     var3.y += var7;
                     var3.height -= var7;
                  }

                  var8 = var3.y + var3.height + var6.bottom - var5.height;
                  if (var8 > 0) {
                     var3.height -= var8;
                  }
               }
            }
         }

      }
   }

   private static class Actions extends UIAction {
      static final String NEXT = "navigateNext";
      static final String PREVIOUS = "navigatePrevious";
      static final String RIGHT = "navigateRight";
      static final String LEFT = "navigateLeft";
      static final String UP = "navigateUp";
      static final String DOWN = "navigateDown";
      static final String PAGE_UP = "navigatePageUp";
      static final String PAGE_DOWN = "navigatePageDown";
      static final String REQUEST_FOCUS = "requestFocus";
      static final String REQUEST_FOCUS_FOR_VISIBLE = "requestFocusForVisibleComponent";
      static final String SET_SELECTED = "setSelectedIndex";
      static final String SELECT_FOCUSED = "selectTabWithFocus";
      static final String SCROLL_FORWARD = "scrollTabsForwardAction";
      static final String SCROLL_BACKWARD = "scrollTabsBackwardAction";

      Actions(String var1) {
         super(var1);
      }

      static Object getUIOfType(ComponentUI var0, Class<AquaTabbedPaneCopyFromBasicUI> var1) {
         return var1.isInstance(var0) ? var0 : null;
      }

      public void actionPerformed(ActionEvent var1) {
         String var2 = this.getName();
         JTabbedPane var3 = (JTabbedPane)var1.getSource();
         AquaTabbedPaneCopyFromBasicUI var4 = (AquaTabbedPaneCopyFromBasicUI)getUIOfType(var3.getUI(), AquaTabbedPaneCopyFromBasicUI.class);
         if (var4 != null) {
            if (var2 == "navigateNext") {
               var4.navigateSelectedTab(12);
            } else if (var2 == "navigatePrevious") {
               var4.navigateSelectedTab(13);
            } else if (var2 == "navigateRight") {
               var4.navigateSelectedTab(3);
            } else if (var2 == "navigateLeft") {
               var4.navigateSelectedTab(7);
            } else if (var2 == "navigateUp") {
               var4.navigateSelectedTab(1);
            } else if (var2 == "navigateDown") {
               var4.navigateSelectedTab(5);
            } else {
               int var5;
               if (var2 == "navigatePageUp") {
                  var5 = var3.getTabPlacement();
                  if (var5 != 1 && var5 != 3) {
                     var4.navigateSelectedTab(1);
                  } else {
                     var4.navigateSelectedTab(7);
                  }
               } else if (var2 == "navigatePageDown") {
                  var5 = var3.getTabPlacement();
                  if (var5 != 1 && var5 != 3) {
                     var4.navigateSelectedTab(5);
                  } else {
                     var4.navigateSelectedTab(3);
                  }
               } else if (var2 == "requestFocus") {
                  var3.requestFocus();
               } else if (var2 == "requestFocusForVisibleComponent") {
                  var4.requestFocusForVisibleComponent();
               } else if (var2 == "setSelectedIndex") {
                  String var8 = var1.getActionCommand();
                  if (var8 != null && var8.length() > 0) {
                     int var6 = var1.getActionCommand().charAt(0);
                     if (var6 >= 97 && var6 <= 122) {
                        var6 -= 32;
                     }

                     Integer var7 = (Integer)var4.mnemonicToIndexMap.get(new Integer(var6));
                     if (var7 != null && var3.isEnabledAt(var7)) {
                        var3.setSelectedIndex(var7);
                     }
                  }
               } else if (var2 == "selectTabWithFocus") {
                  var5 = var4.getFocusIndex();
                  if (var5 != -1) {
                     var3.setSelectedIndex(var5);
                  }
               } else if (var2 == "scrollTabsForwardAction") {
                  if (var4.scrollableTabLayoutEnabled()) {
                     var4.tabScroller.scrollForward(var3.getTabPlacement());
                  }
               } else if (var2 == "scrollTabsBackwardAction" && var4.scrollableTabLayoutEnabled()) {
                  var4.tabScroller.scrollBackward(var3.getTabPlacement());
               }
            }

         }
      }
   }
}
