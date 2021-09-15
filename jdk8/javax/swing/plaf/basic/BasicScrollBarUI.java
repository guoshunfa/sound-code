package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ActionMap;
import javax.swing.BoundedRangeModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicScrollBarUI extends ScrollBarUI implements LayoutManager, SwingConstants {
   private static final int POSITIVE_SCROLL = 1;
   private static final int NEGATIVE_SCROLL = -1;
   private static final int MIN_SCROLL = 2;
   private static final int MAX_SCROLL = 3;
   protected Dimension minimumThumbSize;
   protected Dimension maximumThumbSize;
   protected Color thumbHighlightColor;
   protected Color thumbLightShadowColor;
   protected Color thumbDarkShadowColor;
   protected Color thumbColor;
   protected Color trackColor;
   protected Color trackHighlightColor;
   protected JScrollBar scrollbar;
   protected JButton incrButton;
   protected JButton decrButton;
   protected boolean isDragging;
   protected BasicScrollBarUI.TrackListener trackListener;
   protected BasicScrollBarUI.ArrowButtonListener buttonListener;
   protected BasicScrollBarUI.ModelListener modelListener;
   protected Rectangle thumbRect;
   protected Rectangle trackRect;
   protected int trackHighlight;
   protected static final int NO_HIGHLIGHT = 0;
   protected static final int DECREASE_HIGHLIGHT = 1;
   protected static final int INCREASE_HIGHLIGHT = 2;
   protected BasicScrollBarUI.ScrollListener scrollListener;
   protected PropertyChangeListener propertyChangeListener;
   protected Timer scrollTimer;
   private static final int scrollSpeedThrottle = 60;
   private boolean supportsAbsolutePositioning;
   protected int scrollBarWidth;
   private BasicScrollBarUI.Handler handler;
   private boolean thumbActive;
   private boolean useCachedValue = false;
   private int scrollBarValue;
   protected int incrGap;
   protected int decrGap;

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicScrollBarUI.Actions("positiveUnitIncrement"));
      var0.put(new BasicScrollBarUI.Actions("positiveBlockIncrement"));
      var0.put(new BasicScrollBarUI.Actions("negativeUnitIncrement"));
      var0.put(new BasicScrollBarUI.Actions("negativeBlockIncrement"));
      var0.put(new BasicScrollBarUI.Actions("minScroll"));
      var0.put(new BasicScrollBarUI.Actions("maxScroll"));
   }

   public static ComponentUI createUI(JComponent var0) {
      return new BasicScrollBarUI();
   }

   protected void configureScrollBarColors() {
      LookAndFeel.installColors(this.scrollbar, "ScrollBar.background", "ScrollBar.foreground");
      this.thumbHighlightColor = UIManager.getColor("ScrollBar.thumbHighlight");
      this.thumbLightShadowColor = UIManager.getColor("ScrollBar.thumbShadow");
      this.thumbDarkShadowColor = UIManager.getColor("ScrollBar.thumbDarkShadow");
      this.thumbColor = UIManager.getColor("ScrollBar.thumb");
      this.trackColor = UIManager.getColor("ScrollBar.track");
      this.trackHighlightColor = UIManager.getColor("ScrollBar.trackHighlight");
   }

   public void installUI(JComponent var1) {
      this.scrollbar = (JScrollBar)var1;
      this.thumbRect = new Rectangle(0, 0, 0, 0);
      this.trackRect = new Rectangle(0, 0, 0, 0);
      this.installDefaults();
      this.installComponents();
      this.installListeners();
      this.installKeyboardActions();
   }

   public void uninstallUI(JComponent var1) {
      this.scrollbar = (JScrollBar)var1;
      this.uninstallListeners();
      this.uninstallDefaults();
      this.uninstallComponents();
      this.uninstallKeyboardActions();
      this.thumbRect = null;
      this.scrollbar = null;
      this.incrButton = null;
      this.decrButton = null;
   }

   protected void installDefaults() {
      this.scrollBarWidth = UIManager.getInt("ScrollBar.width");
      if (this.scrollBarWidth <= 0) {
         this.scrollBarWidth = 16;
      }

      this.minimumThumbSize = (Dimension)UIManager.get("ScrollBar.minimumThumbSize");
      this.maximumThumbSize = (Dimension)UIManager.get("ScrollBar.maximumThumbSize");
      Boolean var1 = (Boolean)UIManager.get("ScrollBar.allowsAbsolutePositioning");
      this.supportsAbsolutePositioning = var1 != null ? var1 : false;
      this.trackHighlight = 0;
      if (this.scrollbar.getLayout() == null || this.scrollbar.getLayout() instanceof UIResource) {
         this.scrollbar.setLayout(this);
      }

      this.configureScrollBarColors();
      LookAndFeel.installBorder(this.scrollbar, "ScrollBar.border");
      LookAndFeel.installProperty(this.scrollbar, "opaque", Boolean.TRUE);
      this.scrollBarValue = this.scrollbar.getValue();
      this.incrGap = UIManager.getInt("ScrollBar.incrementButtonGap");
      this.decrGap = UIManager.getInt("ScrollBar.decrementButtonGap");
      String var2 = (String)this.scrollbar.getClientProperty("JComponent.sizeVariant");
      if (var2 != null) {
         if ("large".equals(var2)) {
            this.scrollBarWidth = (int)((double)this.scrollBarWidth * 1.15D);
            this.incrGap = (int)((double)this.incrGap * 1.15D);
            this.decrGap = (int)((double)this.decrGap * 1.15D);
         } else if ("small".equals(var2)) {
            this.scrollBarWidth = (int)((double)this.scrollBarWidth * 0.857D);
            this.incrGap = (int)((double)this.incrGap * 0.857D);
            this.decrGap = (int)((double)this.decrGap * 0.714D);
         } else if ("mini".equals(var2)) {
            this.scrollBarWidth = (int)((double)this.scrollBarWidth * 0.714D);
            this.incrGap = (int)((double)this.incrGap * 0.714D);
            this.decrGap = (int)((double)this.decrGap * 0.714D);
         }
      }

   }

   protected void installComponents() {
      switch(this.scrollbar.getOrientation()) {
      case 0:
         if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
            this.incrButton = this.createIncreaseButton(3);
            this.decrButton = this.createDecreaseButton(7);
         } else {
            this.incrButton = this.createIncreaseButton(7);
            this.decrButton = this.createDecreaseButton(3);
         }
         break;
      case 1:
         this.incrButton = this.createIncreaseButton(5);
         this.decrButton = this.createDecreaseButton(1);
      }

      this.scrollbar.add(this.incrButton);
      this.scrollbar.add(this.decrButton);
      this.scrollbar.setEnabled(this.scrollbar.isEnabled());
   }

   protected void uninstallComponents() {
      this.scrollbar.remove(this.incrButton);
      this.scrollbar.remove(this.decrButton);
   }

   protected void installListeners() {
      this.trackListener = this.createTrackListener();
      this.buttonListener = this.createArrowButtonListener();
      this.modelListener = this.createModelListener();
      this.propertyChangeListener = this.createPropertyChangeListener();
      this.scrollbar.addMouseListener(this.trackListener);
      this.scrollbar.addMouseMotionListener(this.trackListener);
      this.scrollbar.getModel().addChangeListener(this.modelListener);
      this.scrollbar.addPropertyChangeListener(this.propertyChangeListener);
      this.scrollbar.addFocusListener(this.getHandler());
      if (this.incrButton != null) {
         this.incrButton.addMouseListener(this.buttonListener);
      }

      if (this.decrButton != null) {
         this.decrButton.addMouseListener(this.buttonListener);
      }

      this.scrollListener = this.createScrollListener();
      this.scrollTimer = new Timer(60, this.scrollListener);
      this.scrollTimer.setInitialDelay(300);
   }

   protected void installKeyboardActions() {
      LazyActionMap.installLazyActionMap(this.scrollbar, BasicScrollBarUI.class, "ScrollBar.actionMap");
      InputMap var1 = this.getInputMap(0);
      SwingUtilities.replaceUIInputMap(this.scrollbar, 0, var1);
      var1 = this.getInputMap(1);
      SwingUtilities.replaceUIInputMap(this.scrollbar, 1, var1);
   }

   protected void uninstallKeyboardActions() {
      SwingUtilities.replaceUIInputMap(this.scrollbar, 0, (InputMap)null);
      SwingUtilities.replaceUIActionMap(this.scrollbar, (ActionMap)null);
   }

   private InputMap getInputMap(int var1) {
      InputMap var2;
      InputMap var3;
      if (var1 == 0) {
         var2 = (InputMap)DefaultLookup.get(this.scrollbar, this, "ScrollBar.focusInputMap");
         if (!this.scrollbar.getComponentOrientation().isLeftToRight() && (var3 = (InputMap)DefaultLookup.get(this.scrollbar, this, "ScrollBar.focusInputMap.RightToLeft")) != null) {
            var3.setParent(var2);
            return var3;
         } else {
            return var2;
         }
      } else if (var1 == 1) {
         var2 = (InputMap)DefaultLookup.get(this.scrollbar, this, "ScrollBar.ancestorInputMap");
         if (!this.scrollbar.getComponentOrientation().isLeftToRight() && (var3 = (InputMap)DefaultLookup.get(this.scrollbar, this, "ScrollBar.ancestorInputMap.RightToLeft")) != null) {
            var3.setParent(var2);
            return var3;
         } else {
            return var2;
         }
      } else {
         return null;
      }
   }

   protected void uninstallListeners() {
      this.scrollTimer.stop();
      this.scrollTimer = null;
      if (this.decrButton != null) {
         this.decrButton.removeMouseListener(this.buttonListener);
      }

      if (this.incrButton != null) {
         this.incrButton.removeMouseListener(this.buttonListener);
      }

      this.scrollbar.getModel().removeChangeListener(this.modelListener);
      this.scrollbar.removeMouseListener(this.trackListener);
      this.scrollbar.removeMouseMotionListener(this.trackListener);
      this.scrollbar.removePropertyChangeListener(this.propertyChangeListener);
      this.scrollbar.removeFocusListener(this.getHandler());
      this.handler = null;
   }

   protected void uninstallDefaults() {
      LookAndFeel.uninstallBorder(this.scrollbar);
      if (this.scrollbar.getLayout() == this) {
         this.scrollbar.setLayout((LayoutManager)null);
      }

   }

   private BasicScrollBarUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicScrollBarUI.Handler();
      }

      return this.handler;
   }

   protected BasicScrollBarUI.TrackListener createTrackListener() {
      return new BasicScrollBarUI.TrackListener();
   }

   protected BasicScrollBarUI.ArrowButtonListener createArrowButtonListener() {
      return new BasicScrollBarUI.ArrowButtonListener();
   }

   protected BasicScrollBarUI.ModelListener createModelListener() {
      return new BasicScrollBarUI.ModelListener();
   }

   protected BasicScrollBarUI.ScrollListener createScrollListener() {
      return new BasicScrollBarUI.ScrollListener();
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return this.getHandler();
   }

   private void updateThumbState(int var1, int var2) {
      Rectangle var3 = this.getThumbBounds();
      this.setThumbRollover(var3.contains(var1, var2));
   }

   protected void setThumbRollover(boolean var1) {
      if (this.thumbActive != var1) {
         this.thumbActive = var1;
         this.scrollbar.repaint(this.getThumbBounds());
      }

   }

   public boolean isThumbRollover() {
      return this.thumbActive;
   }

   public void paint(Graphics var1, JComponent var2) {
      this.paintTrack(var1, var2, this.getTrackBounds());
      Rectangle var3 = this.getThumbBounds();
      if (var3.intersects(var1.getClipBounds())) {
         this.paintThumb(var1, var2, var3);
      }

   }

   public Dimension getPreferredSize(JComponent var1) {
      return this.scrollbar.getOrientation() == 1 ? new Dimension(this.scrollBarWidth, 48) : new Dimension(48, this.scrollBarWidth);
   }

   public Dimension getMaximumSize(JComponent var1) {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   protected JButton createDecreaseButton(int var1) {
      return new BasicArrowButton(var1, UIManager.getColor("ScrollBar.thumb"), UIManager.getColor("ScrollBar.thumbShadow"), UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight"));
   }

   protected JButton createIncreaseButton(int var1) {
      return new BasicArrowButton(var1, UIManager.getColor("ScrollBar.thumb"), UIManager.getColor("ScrollBar.thumbShadow"), UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight"));
   }

   protected void paintDecreaseHighlight(Graphics var1) {
      Insets var2 = this.scrollbar.getInsets();
      Rectangle var3 = this.getThumbBounds();
      var1.setColor(this.trackHighlightColor);
      int var4;
      int var5;
      int var6;
      int var7;
      if (this.scrollbar.getOrientation() == 1) {
         var4 = var2.left;
         var5 = this.trackRect.y;
         var6 = this.scrollbar.getWidth() - (var2.left + var2.right);
         var7 = var3.y - var5;
         var1.fillRect(var4, var5, var6, var7);
      } else {
         if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
            var4 = this.trackRect.x;
            var5 = var3.x - var4;
         } else {
            var4 = var3.x + var3.width;
            var5 = this.trackRect.x + this.trackRect.width - var4;
         }

         var6 = var2.top;
         var7 = this.scrollbar.getHeight() - (var2.top + var2.bottom);
         var1.fillRect(var4, var6, var5, var7);
      }

   }

   protected void paintIncreaseHighlight(Graphics var1) {
      Insets var2 = this.scrollbar.getInsets();
      Rectangle var3 = this.getThumbBounds();
      var1.setColor(this.trackHighlightColor);
      int var4;
      int var5;
      int var6;
      int var7;
      if (this.scrollbar.getOrientation() == 1) {
         var4 = var2.left;
         var5 = var3.y + var3.height;
         var6 = this.scrollbar.getWidth() - (var2.left + var2.right);
         var7 = this.trackRect.y + this.trackRect.height - var5;
         var1.fillRect(var4, var5, var6, var7);
      } else {
         if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
            var4 = var3.x + var3.width;
            var5 = this.trackRect.x + this.trackRect.width - var4;
         } else {
            var4 = this.trackRect.x;
            var5 = var3.x - var4;
         }

         var6 = var2.top;
         var7 = this.scrollbar.getHeight() - (var2.top + var2.bottom);
         var1.fillRect(var4, var6, var5, var7);
      }

   }

   protected void paintTrack(Graphics var1, JComponent var2, Rectangle var3) {
      var1.setColor(this.trackColor);
      var1.fillRect(var3.x, var3.y, var3.width, var3.height);
      if (this.trackHighlight == 1) {
         this.paintDecreaseHighlight(var1);
      } else if (this.trackHighlight == 2) {
         this.paintIncreaseHighlight(var1);
      }

   }

   protected void paintThumb(Graphics var1, JComponent var2, Rectangle var3) {
      if (!var3.isEmpty() && this.scrollbar.isEnabled()) {
         int var4 = var3.width;
         int var5 = var3.height;
         var1.translate(var3.x, var3.y);
         var1.setColor(this.thumbDarkShadowColor);
         SwingUtilities2.drawRect(var1, 0, 0, var4 - 1, var5 - 1);
         var1.setColor(this.thumbColor);
         var1.fillRect(0, 0, var4 - 1, var5 - 1);
         var1.setColor(this.thumbHighlightColor);
         SwingUtilities2.drawVLine(var1, 1, 1, var5 - 2);
         SwingUtilities2.drawHLine(var1, 2, var4 - 3, 1);
         var1.setColor(this.thumbLightShadowColor);
         SwingUtilities2.drawHLine(var1, 2, var4 - 2, var5 - 2);
         SwingUtilities2.drawVLine(var1, var4 - 2, 1, var5 - 3);
         var1.translate(-var3.x, -var3.y);
      }
   }

   protected Dimension getMinimumThumbSize() {
      return this.minimumThumbSize;
   }

   protected Dimension getMaximumThumbSize() {
      return this.maximumThumbSize;
   }

   public void addLayoutComponent(String var1, Component var2) {
   }

   public void removeLayoutComponent(Component var1) {
   }

   public Dimension preferredLayoutSize(Container var1) {
      return this.getPreferredSize((JComponent)var1);
   }

   public Dimension minimumLayoutSize(Container var1) {
      return this.getMinimumSize((JComponent)var1);
   }

   private int getValue(JScrollBar var1) {
      return this.useCachedValue ? this.scrollBarValue : var1.getValue();
   }

   protected void layoutVScrollbar(JScrollBar var1) {
      Dimension var2 = var1.getSize();
      Insets var3 = var1.getInsets();
      int var4 = var2.width - (var3.left + var3.right);
      int var5 = var3.left;
      boolean var6 = DefaultLookup.getBoolean(this.scrollbar, this, "ScrollBar.squareButtons", false);
      int var7 = var6 ? var4 : this.decrButton.getPreferredSize().height;
      int var8 = var3.top;
      int var9 = var6 ? var4 : this.incrButton.getPreferredSize().height;
      int var10 = var2.height - (var3.bottom + var9);
      int var11 = var3.top + var3.bottom;
      int var12 = var7 + var9;
      int var13 = this.decrGap + this.incrGap;
      float var14 = (float)(var2.height - (var11 + var12) - var13);
      float var15 = (float)var1.getMinimum();
      float var16 = (float)var1.getVisibleAmount();
      float var17 = (float)var1.getMaximum() - var15;
      float var18 = (float)this.getValue(var1);
      int var19 = var17 <= 0.0F ? this.getMaximumThumbSize().height : (int)(var14 * (var16 / var17));
      var19 = Math.max(var19, this.getMinimumThumbSize().height);
      var19 = Math.min(var19, this.getMaximumThumbSize().height);
      int var20 = var10 - this.incrGap - var19;
      if (var18 < (float)(var1.getMaximum() - var1.getVisibleAmount())) {
         float var21 = var14 - (float)var19;
         var20 = (int)(0.5F + var21 * ((var18 - var15) / (var17 - var16)));
         var20 += var8 + var7 + this.decrGap;
      }

      int var24 = var2.height - var11;
      if (var24 < var12) {
         var9 = var7 = var24 / 2;
         var10 = var2.height - (var3.bottom + var9);
      }

      this.decrButton.setBounds(var5, var8, var4, var7);
      this.incrButton.setBounds(var5, var10, var4, var9);
      int var22 = var8 + var7 + this.decrGap;
      int var23 = var10 - this.incrGap - var22;
      this.trackRect.setBounds(var5, var22, var4, var23);
      if (var19 >= (int)var14) {
         if (UIManager.getBoolean("ScrollBar.alwaysShowThumb")) {
            this.setThumbBounds(var5, var22, var4, var23);
         } else {
            this.setThumbBounds(0, 0, 0, 0);
         }
      } else {
         if (var20 + var19 > var10 - this.incrGap) {
            var20 = var10 - this.incrGap - var19;
         }

         if (var20 < var8 + var7 + this.decrGap) {
            var20 = var8 + var7 + this.decrGap + 1;
         }

         this.setThumbBounds(var5, var20, var4, var19);
      }

   }

   protected void layoutHScrollbar(JScrollBar var1) {
      Dimension var2 = var1.getSize();
      Insets var3 = var1.getInsets();
      int var4 = var2.height - (var3.top + var3.bottom);
      int var5 = var3.top;
      boolean var6 = var1.getComponentOrientation().isLeftToRight();
      boolean var7 = DefaultLookup.getBoolean(this.scrollbar, this, "ScrollBar.squareButtons", false);
      int var8 = var7 ? var4 : this.decrButton.getPreferredSize().width;
      int var9 = var7 ? var4 : this.incrButton.getPreferredSize().width;
      int var10;
      if (!var6) {
         var10 = var8;
         var8 = var9;
         var9 = var10;
      }

      var10 = var3.left;
      int var11 = var2.width - (var3.right + var9);
      int var12 = var6 ? this.decrGap : this.incrGap;
      int var13 = var6 ? this.incrGap : this.decrGap;
      int var14 = var3.left + var3.right;
      int var15 = var8 + var9;
      float var16 = (float)(var2.width - (var14 + var15) - (var12 + var13));
      float var17 = (float)var1.getMinimum();
      float var18 = (float)var1.getMaximum();
      float var19 = (float)var1.getVisibleAmount();
      float var20 = var18 - var17;
      float var21 = (float)this.getValue(var1);
      int var22 = var20 <= 0.0F ? this.getMaximumThumbSize().width : (int)(var16 * (var19 / var20));
      var22 = Math.max(var22, this.getMinimumThumbSize().width);
      var22 = Math.min(var22, this.getMaximumThumbSize().width);
      int var23 = var6 ? var11 - var13 - var22 : var10 + var8 + var12;
      if (var21 < var18 - (float)var1.getVisibleAmount()) {
         float var24 = var16 - (float)var22;
         if (var6) {
            var23 = (int)(0.5F + var24 * ((var21 - var17) / (var20 - var19)));
         } else {
            var23 = (int)(0.5F + var24 * ((var18 - var19 - var21) / (var20 - var19)));
         }

         var23 += var10 + var8 + var12;
      }

      int var27 = var2.width - var14;
      if (var27 < var15) {
         var9 = var8 = var27 / 2;
         var11 = var2.width - (var3.right + var9 + var13);
      }

      (var6 ? this.decrButton : this.incrButton).setBounds(var10, var5, var8, var4);
      (var6 ? this.incrButton : this.decrButton).setBounds(var11, var5, var9, var4);
      int var25 = var10 + var8 + var12;
      int var26 = var11 - var13 - var25;
      this.trackRect.setBounds(var25, var5, var26, var4);
      if (var22 >= (int)var16) {
         if (UIManager.getBoolean("ScrollBar.alwaysShowThumb")) {
            this.setThumbBounds(var25, var5, var26, var4);
         } else {
            this.setThumbBounds(0, 0, 0, 0);
         }
      } else {
         if (var23 + var22 > var11 - var13) {
            var23 = var11 - var13 - var22;
         }

         if (var23 < var10 + var8 + var12) {
            var23 = var10 + var8 + var12 + 1;
         }

         this.setThumbBounds(var23, var5, var22, var4);
      }

   }

   public void layoutContainer(Container var1) {
      if (!this.isDragging) {
         JScrollBar var2 = (JScrollBar)var1;
         switch(var2.getOrientation()) {
         case 0:
            this.layoutHScrollbar(var2);
            break;
         case 1:
            this.layoutVScrollbar(var2);
         }

      }
   }

   protected void setThumbBounds(int var1, int var2, int var3, int var4) {
      if (this.thumbRect.x != var1 || this.thumbRect.y != var2 || this.thumbRect.width != var3 || this.thumbRect.height != var4) {
         int var5 = Math.min(var1, this.thumbRect.x);
         int var6 = Math.min(var2, this.thumbRect.y);
         int var7 = Math.max(var1 + var3, this.thumbRect.x + this.thumbRect.width);
         int var8 = Math.max(var2 + var4, this.thumbRect.y + this.thumbRect.height);
         this.thumbRect.setBounds(var1, var2, var3, var4);
         this.scrollbar.repaint(var5, var6, var7 - var5, var8 - var6);
         this.setThumbRollover(false);
      }
   }

   protected Rectangle getThumbBounds() {
      return this.thumbRect;
   }

   protected Rectangle getTrackBounds() {
      return this.trackRect;
   }

   static void scrollByBlock(JScrollBar var0, int var1) {
      int var2 = var0.getValue();
      int var3 = var0.getBlockIncrement(var1);
      int var4 = var3 * (var1 > 0 ? 1 : -1);
      int var5 = var2 + var4;
      if (var4 > 0 && var5 < var2) {
         var5 = var0.getMaximum();
      } else if (var4 < 0 && var5 > var2) {
         var5 = var0.getMinimum();
      }

      var0.setValue(var5);
   }

   protected void scrollByBlock(int var1) {
      scrollByBlock(this.scrollbar, var1);
      this.trackHighlight = var1 > 0 ? 2 : 1;
      Rectangle var2 = this.getTrackBounds();
      this.scrollbar.repaint(var2.x, var2.y, var2.width, var2.height);
   }

   static void scrollByUnits(JScrollBar var0, int var1, int var2, boolean var3) {
      int var5 = -1;
      if (var3) {
         if (var1 < 0) {
            var5 = var0.getValue() - var0.getBlockIncrement(var1);
         } else {
            var5 = var0.getValue() + var0.getBlockIncrement(var1);
         }
      }

      for(int var6 = 0; var6 < var2; ++var6) {
         int var4;
         if (var1 > 0) {
            var4 = var0.getUnitIncrement(var1);
         } else {
            var4 = -var0.getUnitIncrement(var1);
         }

         int var7 = var0.getValue();
         int var8 = var7 + var4;
         if (var4 > 0 && var8 < var7) {
            var8 = var0.getMaximum();
         } else if (var4 < 0 && var8 > var7) {
            var8 = var0.getMinimum();
         }

         if (var7 == var8) {
            break;
         }

         if (var3 && var6 > 0) {
            assert var5 != -1;

            if (var1 < 0 && var8 < var5 || var1 > 0 && var8 > var5) {
               break;
            }
         }

         var0.setValue(var8);
      }

   }

   protected void scrollByUnit(int var1) {
      scrollByUnits(this.scrollbar, var1, 1, false);
   }

   public boolean getSupportsAbsolutePositioning() {
      return this.supportsAbsolutePositioning;
   }

   private boolean isMouseLeftOfThumb() {
      return this.trackListener.currentMouseX < this.getThumbBounds().x;
   }

   private boolean isMouseRightOfThumb() {
      Rectangle var1 = this.getThumbBounds();
      return this.trackListener.currentMouseX > var1.x + var1.width;
   }

   private boolean isMouseBeforeThumb() {
      return this.scrollbar.getComponentOrientation().isLeftToRight() ? this.isMouseLeftOfThumb() : this.isMouseRightOfThumb();
   }

   private boolean isMouseAfterThumb() {
      return this.scrollbar.getComponentOrientation().isLeftToRight() ? this.isMouseRightOfThumb() : this.isMouseLeftOfThumb();
   }

   private void updateButtonDirections() {
      int var1 = this.scrollbar.getOrientation();
      if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
         if (this.incrButton instanceof BasicArrowButton) {
            ((BasicArrowButton)this.incrButton).setDirection(var1 == 0 ? 3 : 5);
         }

         if (this.decrButton instanceof BasicArrowButton) {
            ((BasicArrowButton)this.decrButton).setDirection(var1 == 0 ? 7 : 1);
         }
      } else {
         if (this.incrButton instanceof BasicArrowButton) {
            ((BasicArrowButton)this.incrButton).setDirection(var1 == 0 ? 7 : 5);
         }

         if (this.decrButton instanceof BasicArrowButton) {
            ((BasicArrowButton)this.decrButton).setDirection(var1 == 0 ? 3 : 1);
         }
      }

   }

   private class Handler implements FocusListener, PropertyChangeListener {
      private Handler() {
      }

      public void focusGained(FocusEvent var1) {
         BasicScrollBarUI.this.scrollbar.repaint();
      }

      public void focusLost(FocusEvent var1) {
         BasicScrollBarUI.this.scrollbar.repaint();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if ("model" == var2) {
            BoundedRangeModel var3 = (BoundedRangeModel)var1.getOldValue();
            BoundedRangeModel var4 = (BoundedRangeModel)var1.getNewValue();
            var3.removeChangeListener(BasicScrollBarUI.this.modelListener);
            var4.addChangeListener(BasicScrollBarUI.this.modelListener);
            BasicScrollBarUI.this.scrollBarValue = BasicScrollBarUI.this.scrollbar.getValue();
            BasicScrollBarUI.this.scrollbar.repaint();
            BasicScrollBarUI.this.scrollbar.revalidate();
         } else if ("orientation" == var2) {
            BasicScrollBarUI.this.updateButtonDirections();
         } else if ("componentOrientation" == var2) {
            BasicScrollBarUI.this.updateButtonDirections();
            InputMap var5 = BasicScrollBarUI.this.getInputMap(0);
            SwingUtilities.replaceUIInputMap(BasicScrollBarUI.this.scrollbar, 0, var5);
         }

      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }

   private static class Actions extends UIAction {
      private static final String POSITIVE_UNIT_INCREMENT = "positiveUnitIncrement";
      private static final String POSITIVE_BLOCK_INCREMENT = "positiveBlockIncrement";
      private static final String NEGATIVE_UNIT_INCREMENT = "negativeUnitIncrement";
      private static final String NEGATIVE_BLOCK_INCREMENT = "negativeBlockIncrement";
      private static final String MIN_SCROLL = "minScroll";
      private static final String MAX_SCROLL = "maxScroll";

      Actions(String var1) {
         super(var1);
      }

      public void actionPerformed(ActionEvent var1) {
         JScrollBar var2 = (JScrollBar)var1.getSource();
         String var3 = this.getName();
         if (var3 == "positiveUnitIncrement") {
            this.scroll(var2, 1, false);
         } else if (var3 == "positiveBlockIncrement") {
            this.scroll(var2, 1, true);
         } else if (var3 == "negativeUnitIncrement") {
            this.scroll(var2, -1, false);
         } else if (var3 == "negativeBlockIncrement") {
            this.scroll(var2, -1, true);
         } else if (var3 == "minScroll") {
            this.scroll(var2, 2, true);
         } else if (var3 == "maxScroll") {
            this.scroll(var2, 3, true);
         }

      }

      private void scroll(JScrollBar var1, int var2, boolean var3) {
         if (var2 != -1 && var2 != 1) {
            if (var2 == 2) {
               var1.setValue(var1.getMinimum());
            } else if (var2 == 3) {
               var1.setValue(var1.getMaximum());
            }
         } else {
            int var4;
            if (var3) {
               if (var2 == -1) {
                  var4 = -1 * var1.getBlockIncrement(-1);
               } else {
                  var4 = var1.getBlockIncrement(1);
               }
            } else if (var2 == -1) {
               var4 = -1 * var1.getUnitIncrement(-1);
            } else {
               var4 = var1.getUnitIncrement(1);
            }

            var1.setValue(var1.getValue() + var4);
         }

      }
   }

   public class PropertyChangeHandler implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         BasicScrollBarUI.this.getHandler().propertyChange(var1);
      }
   }

   protected class ScrollListener implements ActionListener {
      int direction = 1;
      boolean useBlockIncrement;

      public ScrollListener() {
         this.direction = 1;
         this.useBlockIncrement = false;
      }

      public ScrollListener(int var2, boolean var3) {
         this.direction = var2;
         this.useBlockIncrement = var3;
      }

      public void setDirection(int var1) {
         this.direction = var1;
      }

      public void setScrollByBlock(boolean var1) {
         this.useBlockIncrement = var1;
      }

      public void actionPerformed(ActionEvent var1) {
         if (this.useBlockIncrement) {
            BasicScrollBarUI.this.scrollByBlock(this.direction);
            if (BasicScrollBarUI.this.scrollbar.getOrientation() == 1) {
               if (this.direction > 0) {
                  if (BasicScrollBarUI.this.getThumbBounds().y + BasicScrollBarUI.this.getThumbBounds().height >= BasicScrollBarUI.this.trackListener.currentMouseY) {
                     ((Timer)var1.getSource()).stop();
                  }
               } else if (BasicScrollBarUI.this.getThumbBounds().y <= BasicScrollBarUI.this.trackListener.currentMouseY) {
                  ((Timer)var1.getSource()).stop();
               }
            } else if (this.direction > 0 && !BasicScrollBarUI.this.isMouseAfterThumb() || this.direction < 0 && !BasicScrollBarUI.this.isMouseBeforeThumb()) {
               ((Timer)var1.getSource()).stop();
            }
         } else {
            BasicScrollBarUI.this.scrollByUnit(this.direction);
         }

         if (this.direction > 0 && BasicScrollBarUI.this.scrollbar.getValue() + BasicScrollBarUI.this.scrollbar.getVisibleAmount() >= BasicScrollBarUI.this.scrollbar.getMaximum()) {
            ((Timer)var1.getSource()).stop();
         } else if (this.direction < 0 && BasicScrollBarUI.this.scrollbar.getValue() <= BasicScrollBarUI.this.scrollbar.getMinimum()) {
            ((Timer)var1.getSource()).stop();
         }

      }
   }

   protected class ArrowButtonListener extends MouseAdapter {
      boolean handledEvent;

      public void mousePressed(MouseEvent var1) {
         if (BasicScrollBarUI.this.scrollbar.isEnabled()) {
            if (SwingUtilities.isLeftMouseButton(var1)) {
               int var2 = var1.getSource() == BasicScrollBarUI.this.incrButton ? 1 : -1;
               BasicScrollBarUI.this.scrollByUnit(var2);
               BasicScrollBarUI.this.scrollTimer.stop();
               BasicScrollBarUI.this.scrollListener.setDirection(var2);
               BasicScrollBarUI.this.scrollListener.setScrollByBlock(false);
               BasicScrollBarUI.this.scrollTimer.start();
               this.handledEvent = true;
               if (!BasicScrollBarUI.this.scrollbar.hasFocus() && BasicScrollBarUI.this.scrollbar.isRequestFocusEnabled()) {
                  BasicScrollBarUI.this.scrollbar.requestFocus();
               }

            }
         }
      }

      public void mouseReleased(MouseEvent var1) {
         BasicScrollBarUI.this.scrollTimer.stop();
         this.handledEvent = false;
         BasicScrollBarUI.this.scrollbar.setValueIsAdjusting(false);
      }
   }

   protected class TrackListener extends MouseAdapter implements MouseMotionListener {
      protected transient int offset;
      protected transient int currentMouseX;
      protected transient int currentMouseY;
      private transient int direction = 1;

      public void mouseReleased(MouseEvent var1) {
         if (BasicScrollBarUI.this.isDragging) {
            BasicScrollBarUI.this.updateThumbState(var1.getX(), var1.getY());
         }

         if (!SwingUtilities.isRightMouseButton(var1) && (BasicScrollBarUI.this.getSupportsAbsolutePositioning() || !SwingUtilities.isMiddleMouseButton(var1))) {
            if (BasicScrollBarUI.this.scrollbar.isEnabled()) {
               Rectangle var2 = BasicScrollBarUI.this.getTrackBounds();
               BasicScrollBarUI.this.scrollbar.repaint(var2.x, var2.y, var2.width, var2.height);
               BasicScrollBarUI.this.trackHighlight = 0;
               BasicScrollBarUI.this.isDragging = false;
               this.offset = 0;
               BasicScrollBarUI.this.scrollTimer.stop();
               BasicScrollBarUI.this.useCachedValue = true;
               BasicScrollBarUI.this.scrollbar.setValueIsAdjusting(false);
            }
         }
      }

      public void mousePressed(MouseEvent var1) {
         if (!SwingUtilities.isRightMouseButton(var1) && (BasicScrollBarUI.this.getSupportsAbsolutePositioning() || !SwingUtilities.isMiddleMouseButton(var1))) {
            if (BasicScrollBarUI.this.scrollbar.isEnabled()) {
               if (!BasicScrollBarUI.this.scrollbar.hasFocus() && BasicScrollBarUI.this.scrollbar.isRequestFocusEnabled()) {
                  BasicScrollBarUI.this.scrollbar.requestFocus();
               }

               BasicScrollBarUI.this.useCachedValue = true;
               BasicScrollBarUI.this.scrollbar.setValueIsAdjusting(true);
               this.currentMouseX = var1.getX();
               this.currentMouseY = var1.getY();
               if (BasicScrollBarUI.this.getThumbBounds().contains(this.currentMouseX, this.currentMouseY)) {
                  switch(BasicScrollBarUI.this.scrollbar.getOrientation()) {
                  case 0:
                     this.offset = this.currentMouseX - BasicScrollBarUI.this.getThumbBounds().x;
                     break;
                  case 1:
                     this.offset = this.currentMouseY - BasicScrollBarUI.this.getThumbBounds().y;
                  }

                  BasicScrollBarUI.this.isDragging = true;
               } else if (BasicScrollBarUI.this.getSupportsAbsolutePositioning() && SwingUtilities.isMiddleMouseButton(var1)) {
                  switch(BasicScrollBarUI.this.scrollbar.getOrientation()) {
                  case 0:
                     this.offset = BasicScrollBarUI.this.getThumbBounds().width / 2;
                     break;
                  case 1:
                     this.offset = BasicScrollBarUI.this.getThumbBounds().height / 2;
                  }

                  BasicScrollBarUI.this.isDragging = true;
                  this.setValueFrom(var1);
               } else {
                  BasicScrollBarUI.this.isDragging = false;
                  Dimension var2 = BasicScrollBarUI.this.scrollbar.getSize();
                  this.direction = 1;
                  int var3;
                  switch(BasicScrollBarUI.this.scrollbar.getOrientation()) {
                  case 0:
                     if (BasicScrollBarUI.this.getThumbBounds().isEmpty()) {
                        var3 = var2.width / 2;
                        this.direction = this.currentMouseX < var3 ? -1 : 1;
                     } else {
                        var3 = BasicScrollBarUI.this.getThumbBounds().x;
                        this.direction = this.currentMouseX < var3 ? -1 : 1;
                     }

                     if (!BasicScrollBarUI.this.scrollbar.getComponentOrientation().isLeftToRight()) {
                        this.direction = -this.direction;
                     }
                     break;
                  case 1:
                     if (BasicScrollBarUI.this.getThumbBounds().isEmpty()) {
                        var3 = var2.height / 2;
                        this.direction = this.currentMouseY < var3 ? -1 : 1;
                     } else {
                        var3 = BasicScrollBarUI.this.getThumbBounds().y;
                        this.direction = this.currentMouseY < var3 ? -1 : 1;
                     }
                  }

                  BasicScrollBarUI.this.scrollByBlock(this.direction);
                  BasicScrollBarUI.this.scrollTimer.stop();
                  BasicScrollBarUI.this.scrollListener.setDirection(this.direction);
                  BasicScrollBarUI.this.scrollListener.setScrollByBlock(true);
                  this.startScrollTimerIfNecessary();
               }
            }
         }
      }

      public void mouseDragged(MouseEvent var1) {
         if (!SwingUtilities.isRightMouseButton(var1) && (BasicScrollBarUI.this.getSupportsAbsolutePositioning() || !SwingUtilities.isMiddleMouseButton(var1))) {
            if (BasicScrollBarUI.this.scrollbar.isEnabled() && !BasicScrollBarUI.this.getThumbBounds().isEmpty()) {
               if (BasicScrollBarUI.this.isDragging) {
                  this.setValueFrom(var1);
               } else {
                  this.currentMouseX = var1.getX();
                  this.currentMouseY = var1.getY();
                  BasicScrollBarUI.this.updateThumbState(this.currentMouseX, this.currentMouseY);
                  this.startScrollTimerIfNecessary();
               }

            }
         }
      }

      private void setValueFrom(MouseEvent var1) {
         boolean var2 = BasicScrollBarUI.this.isThumbRollover();
         BoundedRangeModel var3 = BasicScrollBarUI.this.scrollbar.getModel();
         Rectangle var4 = BasicScrollBarUI.this.getThumbBounds();
         float var5;
         int var6;
         int var7;
         int var8;
         if (BasicScrollBarUI.this.scrollbar.getOrientation() == 1) {
            var6 = BasicScrollBarUI.this.trackRect.y;
            var7 = BasicScrollBarUI.this.trackRect.y + BasicScrollBarUI.this.trackRect.height - var4.height;
            var8 = Math.min(var7, Math.max(var6, var1.getY() - this.offset));
            BasicScrollBarUI.this.setThumbBounds(var4.x, var8, var4.width, var4.height);
            var5 = (float)BasicScrollBarUI.this.getTrackBounds().height;
         } else {
            var6 = BasicScrollBarUI.this.trackRect.x;
            var7 = BasicScrollBarUI.this.trackRect.x + BasicScrollBarUI.this.trackRect.width - var4.width;
            var8 = Math.min(var7, Math.max(var6, var1.getX() - this.offset));
            BasicScrollBarUI.this.setThumbBounds(var8, var4.y, var4.width, var4.height);
            var5 = (float)BasicScrollBarUI.this.getTrackBounds().width;
         }

         if (var8 == var7) {
            if (BasicScrollBarUI.this.scrollbar.getOrientation() != 1 && !BasicScrollBarUI.this.scrollbar.getComponentOrientation().isLeftToRight()) {
               BasicScrollBarUI.this.scrollbar.setValue(var3.getMinimum());
            } else {
               BasicScrollBarUI.this.scrollbar.setValue(var3.getMaximum() - var3.getExtent());
            }
         } else {
            float var9 = (float)(var3.getMaximum() - var3.getExtent());
            float var10 = var9 - (float)var3.getMinimum();
            float var11 = (float)(var8 - var6);
            float var12 = (float)(var7 - var6);
            int var13;
            if (BasicScrollBarUI.this.scrollbar.getOrientation() != 1 && !BasicScrollBarUI.this.scrollbar.getComponentOrientation().isLeftToRight()) {
               var13 = (int)(0.5D + (double)((float)(var7 - var8) / var12 * var10));
            } else {
               var13 = (int)(0.5D + (double)(var11 / var12 * var10));
            }

            BasicScrollBarUI.this.useCachedValue = true;
            BasicScrollBarUI.this.scrollBarValue = var13 + var3.getMinimum();
            BasicScrollBarUI.this.scrollbar.setValue(this.adjustValueIfNecessary(BasicScrollBarUI.this.scrollBarValue));
         }

         BasicScrollBarUI.this.setThumbRollover(var2);
      }

      private int adjustValueIfNecessary(int var1) {
         if (BasicScrollBarUI.this.scrollbar.getParent() instanceof JScrollPane) {
            JScrollPane var2 = (JScrollPane)BasicScrollBarUI.this.scrollbar.getParent();
            JViewport var3 = var2.getViewport();
            Component var4 = var3.getView();
            if (var4 instanceof JList) {
               JList var5 = (JList)var4;
               if (DefaultLookup.getBoolean(var5, var5.getUI(), "List.lockToPositionOnScroll", false)) {
                  int var6 = var1;
                  int var7 = var5.getLayoutOrientation();
                  int var8 = BasicScrollBarUI.this.scrollbar.getOrientation();
                  int var9;
                  Rectangle var10;
                  if (var8 == 1 && var7 == 0) {
                     var9 = var5.locationToIndex(new Point(0, var1));
                     var10 = var5.getCellBounds(var9, var9);
                     if (var10 != null) {
                        var6 = var10.y;
                     }
                  }

                  if (var8 == 0 && (var7 == 1 || var7 == 2)) {
                     if (var2.getComponentOrientation().isLeftToRight()) {
                        var9 = var5.locationToIndex(new Point(var1, 0));
                        var10 = var5.getCellBounds(var9, var9);
                        if (var10 != null) {
                           var6 = var10.x;
                        }
                     } else {
                        Point var13 = new Point(var1, 0);
                        int var14 = var3.getExtentSize().width;
                        var13.x += var14 - 1;
                        int var11 = var5.locationToIndex(var13);
                        Rectangle var12 = var5.getCellBounds(var11, var11);
                        if (var12 != null) {
                           var6 = var12.x + var12.width - var14;
                        }
                     }
                  }

                  var1 = var6;
               }
            }
         }

         return var1;
      }

      private void startScrollTimerIfNecessary() {
         if (!BasicScrollBarUI.this.scrollTimer.isRunning()) {
            Rectangle var1 = BasicScrollBarUI.this.getThumbBounds();
            switch(BasicScrollBarUI.this.scrollbar.getOrientation()) {
            case 0:
               if (this.direction > 0 && BasicScrollBarUI.this.isMouseAfterThumb() || this.direction < 0 && BasicScrollBarUI.this.isMouseBeforeThumb()) {
                  BasicScrollBarUI.this.scrollTimer.start();
               }
               break;
            case 1:
               if (this.direction > 0) {
                  if (var1.y + var1.height < BasicScrollBarUI.this.trackListener.currentMouseY) {
                     BasicScrollBarUI.this.scrollTimer.start();
                  }
               } else if (var1.y > BasicScrollBarUI.this.trackListener.currentMouseY) {
                  BasicScrollBarUI.this.scrollTimer.start();
               }
            }

         }
      }

      public void mouseMoved(MouseEvent var1) {
         if (!BasicScrollBarUI.this.isDragging) {
            BasicScrollBarUI.this.updateThumbState(var1.getX(), var1.getY());
         }

      }

      public void mouseExited(MouseEvent var1) {
         if (!BasicScrollBarUI.this.isDragging) {
            BasicScrollBarUI.this.setThumbRollover(false);
         }

      }
   }

   protected class ModelListener implements ChangeListener {
      public void stateChanged(ChangeEvent var1) {
         if (!BasicScrollBarUI.this.useCachedValue) {
            BasicScrollBarUI.this.scrollBarValue = BasicScrollBarUI.this.scrollbar.getValue();
         }

         BasicScrollBarUI.this.layoutContainer(BasicScrollBarUI.this.scrollbar);
         BasicScrollBarUI.this.useCachedValue = false;
      }
   }
}
