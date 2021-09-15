package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Dictionary;
import java.util.Enumeration;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoundedRangeModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.SliderUI;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicSliderUI extends SliderUI {
   private static final BasicSliderUI.Actions SHARED_ACTION = new BasicSliderUI.Actions();
   public static final int POSITIVE_SCROLL = 1;
   public static final int NEGATIVE_SCROLL = -1;
   public static final int MIN_SCROLL = -2;
   public static final int MAX_SCROLL = 2;
   protected Timer scrollTimer;
   protected JSlider slider;
   protected Insets focusInsets = null;
   protected Insets insetCache = null;
   protected boolean leftToRightCache = true;
   protected Rectangle focusRect = null;
   protected Rectangle contentRect = null;
   protected Rectangle labelRect = null;
   protected Rectangle tickRect = null;
   protected Rectangle trackRect = null;
   protected Rectangle thumbRect = null;
   protected int trackBuffer = 0;
   private transient boolean isDragging;
   protected BasicSliderUI.TrackListener trackListener;
   protected ChangeListener changeListener;
   protected ComponentListener componentListener;
   protected FocusListener focusListener;
   protected BasicSliderUI.ScrollListener scrollListener;
   protected PropertyChangeListener propertyChangeListener;
   private BasicSliderUI.Handler handler;
   private int lastValue;
   private Color shadowColor;
   private Color highlightColor;
   private Color focusColor;
   private boolean checkedLabelBaselines;
   private boolean sameLabelBaselines;
   private static Rectangle unionRect = new Rectangle();

   protected Color getShadowColor() {
      return this.shadowColor;
   }

   protected Color getHighlightColor() {
      return this.highlightColor;
   }

   protected Color getFocusColor() {
      return this.focusColor;
   }

   protected boolean isDragging() {
      return this.isDragging;
   }

   public static ComponentUI createUI(JComponent var0) {
      return new BasicSliderUI((JSlider)var0);
   }

   public BasicSliderUI(JSlider var1) {
   }

   public void installUI(JComponent var1) {
      this.slider = (JSlider)var1;
      this.checkedLabelBaselines = false;
      this.slider.setEnabled(this.slider.isEnabled());
      LookAndFeel.installProperty(this.slider, "opaque", Boolean.TRUE);
      this.isDragging = false;
      this.trackListener = this.createTrackListener(this.slider);
      this.changeListener = this.createChangeListener(this.slider);
      this.componentListener = this.createComponentListener(this.slider);
      this.focusListener = this.createFocusListener(this.slider);
      this.scrollListener = this.createScrollListener(this.slider);
      this.propertyChangeListener = this.createPropertyChangeListener(this.slider);
      this.installDefaults(this.slider);
      this.installListeners(this.slider);
      this.installKeyboardActions(this.slider);
      this.scrollTimer = new Timer(100, this.scrollListener);
      this.scrollTimer.setInitialDelay(300);
      this.insetCache = this.slider.getInsets();
      this.leftToRightCache = BasicGraphicsUtils.isLeftToRight(this.slider);
      this.focusRect = new Rectangle();
      this.contentRect = new Rectangle();
      this.labelRect = new Rectangle();
      this.tickRect = new Rectangle();
      this.trackRect = new Rectangle();
      this.thumbRect = new Rectangle();
      this.lastValue = this.slider.getValue();
      this.calculateGeometry();
   }

   public void uninstallUI(JComponent var1) {
      if (var1 != this.slider) {
         throw new IllegalComponentStateException(this + " was asked to deinstall() " + var1 + " when it only knows about " + this.slider + ".");
      } else {
         this.scrollTimer.stop();
         this.scrollTimer = null;
         this.uninstallDefaults(this.slider);
         this.uninstallListeners(this.slider);
         this.uninstallKeyboardActions(this.slider);
         this.insetCache = null;
         this.leftToRightCache = true;
         this.focusRect = null;
         this.contentRect = null;
         this.labelRect = null;
         this.tickRect = null;
         this.trackRect = null;
         this.thumbRect = null;
         this.trackListener = null;
         this.changeListener = null;
         this.componentListener = null;
         this.focusListener = null;
         this.scrollListener = null;
         this.propertyChangeListener = null;
         this.slider = null;
      }
   }

   protected void installDefaults(JSlider var1) {
      LookAndFeel.installBorder(var1, "Slider.border");
      LookAndFeel.installColorsAndFont(var1, "Slider.background", "Slider.foreground", "Slider.font");
      this.highlightColor = UIManager.getColor("Slider.highlight");
      this.shadowColor = UIManager.getColor("Slider.shadow");
      this.focusColor = UIManager.getColor("Slider.focus");
      this.focusInsets = (Insets)UIManager.get("Slider.focusInsets");
      if (this.focusInsets == null) {
         this.focusInsets = new InsetsUIResource(2, 2, 2, 2);
      }

   }

   protected void uninstallDefaults(JSlider var1) {
      LookAndFeel.uninstallBorder(var1);
      this.focusInsets = null;
   }

   protected BasicSliderUI.TrackListener createTrackListener(JSlider var1) {
      return new BasicSliderUI.TrackListener();
   }

   protected ChangeListener createChangeListener(JSlider var1) {
      return this.getHandler();
   }

   protected ComponentListener createComponentListener(JSlider var1) {
      return this.getHandler();
   }

   protected FocusListener createFocusListener(JSlider var1) {
      return this.getHandler();
   }

   protected BasicSliderUI.ScrollListener createScrollListener(JSlider var1) {
      return new BasicSliderUI.ScrollListener();
   }

   protected PropertyChangeListener createPropertyChangeListener(JSlider var1) {
      return this.getHandler();
   }

   private BasicSliderUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicSliderUI.Handler();
      }

      return this.handler;
   }

   protected void installListeners(JSlider var1) {
      var1.addMouseListener(this.trackListener);
      var1.addMouseMotionListener(this.trackListener);
      var1.addFocusListener(this.focusListener);
      var1.addComponentListener(this.componentListener);
      var1.addPropertyChangeListener(this.propertyChangeListener);
      var1.getModel().addChangeListener(this.changeListener);
   }

   protected void uninstallListeners(JSlider var1) {
      var1.removeMouseListener(this.trackListener);
      var1.removeMouseMotionListener(this.trackListener);
      var1.removeFocusListener(this.focusListener);
      var1.removeComponentListener(this.componentListener);
      var1.removePropertyChangeListener(this.propertyChangeListener);
      var1.getModel().removeChangeListener(this.changeListener);
      this.handler = null;
   }

   protected void installKeyboardActions(JSlider var1) {
      InputMap var2 = this.getInputMap(0, var1);
      SwingUtilities.replaceUIInputMap(var1, 0, var2);
      LazyActionMap.installLazyActionMap(var1, BasicSliderUI.class, "Slider.actionMap");
   }

   InputMap getInputMap(int var1, JSlider var2) {
      if (var1 == 0) {
         InputMap var3 = (InputMap)DefaultLookup.get(var2, this, "Slider.focusInputMap");
         InputMap var4;
         if (!var2.getComponentOrientation().isLeftToRight() && (var4 = (InputMap)DefaultLookup.get(var2, this, "Slider.focusInputMap.RightToLeft")) != null) {
            var4.setParent(var3);
            return var4;
         } else {
            return var3;
         }
      } else {
         return null;
      }
   }

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicSliderUI.Actions("positiveUnitIncrement"));
      var0.put(new BasicSliderUI.Actions("positiveBlockIncrement"));
      var0.put(new BasicSliderUI.Actions("negativeUnitIncrement"));
      var0.put(new BasicSliderUI.Actions("negativeBlockIncrement"));
      var0.put(new BasicSliderUI.Actions("minScroll"));
      var0.put(new BasicSliderUI.Actions("maxScroll"));
   }

   protected void uninstallKeyboardActions(JSlider var1) {
      SwingUtilities.replaceUIActionMap(var1, (ActionMap)null);
      SwingUtilities.replaceUIInputMap(var1, 0, (InputMap)null);
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      super.getBaseline(var1, var2, var3);
      if (this.slider.getPaintLabels() && this.labelsHaveSameBaselines()) {
         FontMetrics var4 = this.slider.getFontMetrics(this.slider.getFont());
         Insets var5 = this.slider.getInsets();
         Dimension var6 = this.getThumbSize();
         int var9;
         int var10;
         int var11;
         int var13;
         int var14;
         if (this.slider.getOrientation() == 0) {
            int var16 = this.getTickLength();
            int var17 = var3 - var5.top - var5.bottom - this.focusInsets.top - this.focusInsets.bottom;
            var9 = var6.height;
            var10 = var9;
            if (this.slider.getPaintTicks()) {
               var10 = var9 + var16;
            }

            var10 += this.getHeightOfTallestLabel();
            var11 = var5.top + this.focusInsets.top + (var17 - var10 - 1) / 2;
            var13 = var11 + var9;
            var14 = var16;
            if (!this.slider.getPaintTicks()) {
               var14 = 0;
            }

            int var15 = var13 + var14;
            return var15 + var4.getAscent();
         }

         boolean var7 = this.slider.getInverted();
         Integer var8 = var7 ? this.getLowestValue() : this.getHighestValue();
         if (var8 != null) {
            var9 = var6.height;
            var10 = Math.max(var4.getHeight() / 2, var9 / 2);
            var11 = this.focusInsets.top + var5.top;
            int var12 = var11 + var10;
            var13 = var3 - this.focusInsets.top - this.focusInsets.bottom - var5.top - var5.bottom - var10 - var10;
            var14 = this.yPositionForValue(var8, var12, var13);
            return var14 - var4.getHeight() / 2 + var4.getAscent();
         }
      }

      return 0;
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent var1) {
      super.getBaselineResizeBehavior(var1);
      return Component.BaselineResizeBehavior.OTHER;
   }

   protected boolean labelsHaveSameBaselines() {
      if (!this.checkedLabelBaselines) {
         this.checkedLabelBaselines = true;
         Dictionary var1 = this.slider.getLabelTable();
         if (var1 != null) {
            this.sameLabelBaselines = true;
            Enumeration var2 = var1.elements();
            int var3 = -1;

            while(var2.hasMoreElements()) {
               JComponent var4 = (JComponent)var2.nextElement();
               Dimension var5 = var4.getPreferredSize();
               int var6 = var4.getBaseline(var5.width, var5.height);
               if (var6 < 0) {
                  this.sameLabelBaselines = false;
                  break;
               }

               if (var3 == -1) {
                  var3 = var6;
               } else if (var3 != var6) {
                  this.sameLabelBaselines = false;
                  break;
               }
            }
         } else {
            this.sameLabelBaselines = false;
         }
      }

      return this.sameLabelBaselines;
   }

   public Dimension getPreferredHorizontalSize() {
      Dimension var1 = (Dimension)DefaultLookup.get(this.slider, this, "Slider.horizontalSize");
      if (var1 == null) {
         var1 = new Dimension(200, 21);
      }

      return var1;
   }

   public Dimension getPreferredVerticalSize() {
      Dimension var1 = (Dimension)DefaultLookup.get(this.slider, this, "Slider.verticalSize");
      if (var1 == null) {
         var1 = new Dimension(21, 200);
      }

      return var1;
   }

   public Dimension getMinimumHorizontalSize() {
      Dimension var1 = (Dimension)DefaultLookup.get(this.slider, this, "Slider.minimumHorizontalSize");
      if (var1 == null) {
         var1 = new Dimension(36, 21);
      }

      return var1;
   }

   public Dimension getMinimumVerticalSize() {
      Dimension var1 = (Dimension)DefaultLookup.get(this.slider, this, "Slider.minimumVerticalSize");
      if (var1 == null) {
         var1 = new Dimension(21, 36);
      }

      return var1;
   }

   public Dimension getPreferredSize(JComponent var1) {
      this.recalculateIfInsetsChanged();
      Dimension var2;
      if (this.slider.getOrientation() == 1) {
         var2 = new Dimension(this.getPreferredVerticalSize());
         var2.width = this.insetCache.left + this.insetCache.right;
         var2.width += this.focusInsets.left + this.focusInsets.right;
         var2.width += this.trackRect.width + this.tickRect.width + this.labelRect.width;
      } else {
         var2 = new Dimension(this.getPreferredHorizontalSize());
         var2.height = this.insetCache.top + this.insetCache.bottom;
         var2.height += this.focusInsets.top + this.focusInsets.bottom;
         var2.height += this.trackRect.height + this.tickRect.height + this.labelRect.height;
      }

      return var2;
   }

   public Dimension getMinimumSize(JComponent var1) {
      this.recalculateIfInsetsChanged();
      Dimension var2;
      if (this.slider.getOrientation() == 1) {
         var2 = new Dimension(this.getMinimumVerticalSize());
         var2.width = this.insetCache.left + this.insetCache.right;
         var2.width += this.focusInsets.left + this.focusInsets.right;
         var2.width += this.trackRect.width + this.tickRect.width + this.labelRect.width;
      } else {
         var2 = new Dimension(this.getMinimumHorizontalSize());
         var2.height = this.insetCache.top + this.insetCache.bottom;
         var2.height += this.focusInsets.top + this.focusInsets.bottom;
         var2.height += this.trackRect.height + this.tickRect.height + this.labelRect.height;
      }

      return var2;
   }

   public Dimension getMaximumSize(JComponent var1) {
      Dimension var2 = this.getPreferredSize(var1);
      if (this.slider.getOrientation() == 1) {
         var2.height = 32767;
      } else {
         var2.width = 32767;
      }

      return var2;
   }

   protected void calculateGeometry() {
      this.calculateFocusRect();
      this.calculateContentRect();
      this.calculateThumbSize();
      this.calculateTrackBuffer();
      this.calculateTrackRect();
      this.calculateTickRect();
      this.calculateLabelRect();
      this.calculateThumbLocation();
   }

   protected void calculateFocusRect() {
      this.focusRect.x = this.insetCache.left;
      this.focusRect.y = this.insetCache.top;
      this.focusRect.width = this.slider.getWidth() - (this.insetCache.left + this.insetCache.right);
      this.focusRect.height = this.slider.getHeight() - (this.insetCache.top + this.insetCache.bottom);
   }

   protected void calculateThumbSize() {
      Dimension var1 = this.getThumbSize();
      this.thumbRect.setSize(var1.width, var1.height);
   }

   protected void calculateContentRect() {
      this.contentRect.x = this.focusRect.x + this.focusInsets.left;
      this.contentRect.y = this.focusRect.y + this.focusInsets.top;
      this.contentRect.width = this.focusRect.width - (this.focusInsets.left + this.focusInsets.right);
      this.contentRect.height = this.focusRect.height - (this.focusInsets.top + this.focusInsets.bottom);
   }

   private int getTickSpacing() {
      int var1 = this.slider.getMajorTickSpacing();
      int var2 = this.slider.getMinorTickSpacing();
      int var3;
      if (var2 > 0) {
         var3 = var2;
      } else if (var1 > 0) {
         var3 = var1;
      } else {
         var3 = 0;
      }

      return var3;
   }

   protected void calculateThumbLocation() {
      int var1;
      if (this.slider.getSnapToTicks()) {
         var1 = this.slider.getValue();
         int var2 = var1;
         int var3 = this.getTickSpacing();
         if (var3 != 0) {
            if ((var1 - this.slider.getMinimum()) % var3 != 0) {
               float var4 = (float)(var1 - this.slider.getMinimum()) / (float)var3;
               int var5 = Math.round(var4);
               if ((double)(var4 - (float)((int)var4)) == 0.5D && var1 < this.lastValue) {
                  --var5;
               }

               var2 = this.slider.getMinimum() + var5 * var3;
            }

            if (var2 != var1) {
               this.slider.setValue(var2);
            }
         }
      }

      if (this.slider.getOrientation() == 0) {
         var1 = this.xPositionForValue(this.slider.getValue());
         this.thumbRect.x = var1 - this.thumbRect.width / 2;
         this.thumbRect.y = this.trackRect.y;
      } else {
         var1 = this.yPositionForValue(this.slider.getValue());
         this.thumbRect.x = this.trackRect.x;
         this.thumbRect.y = var1 - this.thumbRect.height / 2;
      }

   }

   protected void calculateTrackBuffer() {
      if (this.slider.getPaintLabels() && this.slider.getLabelTable() != null) {
         Component var1 = this.getHighestValueLabel();
         Component var2 = this.getLowestValueLabel();
         if (this.slider.getOrientation() == 0) {
            this.trackBuffer = Math.max(var1.getBounds().width, var2.getBounds().width) / 2;
            this.trackBuffer = Math.max(this.trackBuffer, this.thumbRect.width / 2);
         } else {
            this.trackBuffer = Math.max(var1.getBounds().height, var2.getBounds().height) / 2;
            this.trackBuffer = Math.max(this.trackBuffer, this.thumbRect.height / 2);
         }
      } else if (this.slider.getOrientation() == 0) {
         this.trackBuffer = this.thumbRect.width / 2;
      } else {
         this.trackBuffer = this.thumbRect.height / 2;
      }

   }

   protected void calculateTrackRect() {
      int var1;
      if (this.slider.getOrientation() == 0) {
         var1 = this.thumbRect.height;
         if (this.slider.getPaintTicks()) {
            var1 += this.getTickLength();
         }

         if (this.slider.getPaintLabels()) {
            var1 += this.getHeightOfTallestLabel();
         }

         this.trackRect.x = this.contentRect.x + this.trackBuffer;
         this.trackRect.y = this.contentRect.y + (this.contentRect.height - var1 - 1) / 2;
         this.trackRect.width = this.contentRect.width - this.trackBuffer * 2;
         this.trackRect.height = this.thumbRect.height;
      } else {
         var1 = this.thumbRect.width;
         if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
            if (this.slider.getPaintTicks()) {
               var1 += this.getTickLength();
            }

            if (this.slider.getPaintLabels()) {
               var1 += this.getWidthOfWidestLabel();
            }
         } else {
            if (this.slider.getPaintTicks()) {
               var1 -= this.getTickLength();
            }

            if (this.slider.getPaintLabels()) {
               var1 -= this.getWidthOfWidestLabel();
            }
         }

         this.trackRect.x = this.contentRect.x + (this.contentRect.width - var1 - 1) / 2;
         this.trackRect.y = this.contentRect.y + this.trackBuffer;
         this.trackRect.width = this.thumbRect.width;
         this.trackRect.height = this.contentRect.height - this.trackBuffer * 2;
      }

   }

   protected int getTickLength() {
      return 8;
   }

   protected void calculateTickRect() {
      if (this.slider.getOrientation() == 0) {
         this.tickRect.x = this.trackRect.x;
         this.tickRect.y = this.trackRect.y + this.trackRect.height;
         this.tickRect.width = this.trackRect.width;
         this.tickRect.height = this.slider.getPaintTicks() ? this.getTickLength() : 0;
      } else {
         this.tickRect.width = this.slider.getPaintTicks() ? this.getTickLength() : 0;
         if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
            this.tickRect.x = this.trackRect.x + this.trackRect.width;
         } else {
            this.tickRect.x = this.trackRect.x - this.tickRect.width;
         }

         this.tickRect.y = this.trackRect.y;
         this.tickRect.height = this.trackRect.height;
      }

   }

   protected void calculateLabelRect() {
      if (this.slider.getPaintLabels()) {
         if (this.slider.getOrientation() == 0) {
            this.labelRect.x = this.tickRect.x - this.trackBuffer;
            this.labelRect.y = this.tickRect.y + this.tickRect.height;
            this.labelRect.width = this.tickRect.width + this.trackBuffer * 2;
            this.labelRect.height = this.getHeightOfTallestLabel();
         } else {
            if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
               this.labelRect.x = this.tickRect.x + this.tickRect.width;
               this.labelRect.width = this.getWidthOfWidestLabel();
            } else {
               this.labelRect.width = this.getWidthOfWidestLabel();
               this.labelRect.x = this.tickRect.x - this.labelRect.width;
            }

            this.labelRect.y = this.tickRect.y - this.trackBuffer;
            this.labelRect.height = this.tickRect.height + this.trackBuffer * 2;
         }
      } else if (this.slider.getOrientation() == 0) {
         this.labelRect.x = this.tickRect.x;
         this.labelRect.y = this.tickRect.y + this.tickRect.height;
         this.labelRect.width = this.tickRect.width;
         this.labelRect.height = 0;
      } else {
         if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
            this.labelRect.x = this.tickRect.x + this.tickRect.width;
         } else {
            this.labelRect.x = this.tickRect.x;
         }

         this.labelRect.y = this.tickRect.y;
         this.labelRect.width = 0;
         this.labelRect.height = this.tickRect.height;
      }

   }

   protected Dimension getThumbSize() {
      Dimension var1 = new Dimension();
      if (this.slider.getOrientation() == 1) {
         var1.width = 20;
         var1.height = 11;
      } else {
         var1.width = 11;
         var1.height = 20;
      }

      return var1;
   }

   protected int getWidthOfWidestLabel() {
      Dictionary var1 = this.slider.getLabelTable();
      int var2 = 0;
      JComponent var4;
      if (var1 != null) {
         for(Enumeration var3 = var1.keys(); var3.hasMoreElements(); var2 = Math.max(var4.getPreferredSize().width, var2)) {
            var4 = (JComponent)var1.get(var3.nextElement());
         }
      }

      return var2;
   }

   protected int getHeightOfTallestLabel() {
      Dictionary var1 = this.slider.getLabelTable();
      int var2 = 0;
      JComponent var4;
      if (var1 != null) {
         for(Enumeration var3 = var1.keys(); var3.hasMoreElements(); var2 = Math.max(var4.getPreferredSize().height, var2)) {
            var4 = (JComponent)var1.get(var3.nextElement());
         }
      }

      return var2;
   }

   protected int getWidthOfHighValueLabel() {
      Component var1 = this.getHighestValueLabel();
      int var2 = 0;
      if (var1 != null) {
         var2 = var1.getPreferredSize().width;
      }

      return var2;
   }

   protected int getWidthOfLowValueLabel() {
      Component var1 = this.getLowestValueLabel();
      int var2 = 0;
      if (var1 != null) {
         var2 = var1.getPreferredSize().width;
      }

      return var2;
   }

   protected int getHeightOfHighValueLabel() {
      Component var1 = this.getHighestValueLabel();
      int var2 = 0;
      if (var1 != null) {
         var2 = var1.getPreferredSize().height;
      }

      return var2;
   }

   protected int getHeightOfLowValueLabel() {
      Component var1 = this.getLowestValueLabel();
      int var2 = 0;
      if (var1 != null) {
         var2 = var1.getPreferredSize().height;
      }

      return var2;
   }

   protected boolean drawInverted() {
      if (this.slider.getOrientation() == 0) {
         if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
            return this.slider.getInverted();
         } else {
            return !this.slider.getInverted();
         }
      } else {
         return this.slider.getInverted();
      }
   }

   protected Integer getHighestValue() {
      Dictionary var1 = this.slider.getLabelTable();
      if (var1 == null) {
         return null;
      } else {
         Enumeration var2 = var1.keys();
         Integer var3 = null;

         while(true) {
            Integer var4;
            do {
               if (!var2.hasMoreElements()) {
                  return var3;
               }

               var4 = (Integer)var2.nextElement();
            } while(var3 != null && var4 <= var3);

            var3 = var4;
         }
      }
   }

   protected Integer getLowestValue() {
      Dictionary var1 = this.slider.getLabelTable();
      if (var1 == null) {
         return null;
      } else {
         Enumeration var2 = var1.keys();
         Integer var3 = null;

         while(true) {
            Integer var4;
            do {
               if (!var2.hasMoreElements()) {
                  return var3;
               }

               var4 = (Integer)var2.nextElement();
            } while(var3 != null && var4 >= var3);

            var3 = var4;
         }
      }
   }

   protected Component getLowestValueLabel() {
      Integer var1 = this.getLowestValue();
      return var1 != null ? (Component)this.slider.getLabelTable().get(var1) : null;
   }

   protected Component getHighestValueLabel() {
      Integer var1 = this.getHighestValue();
      return var1 != null ? (Component)this.slider.getLabelTable().get(var1) : null;
   }

   public void paint(Graphics var1, JComponent var2) {
      this.recalculateIfInsetsChanged();
      this.recalculateIfOrientationChanged();
      Rectangle var3 = var1.getClipBounds();
      if (!var3.intersects(this.trackRect) && this.slider.getPaintTrack()) {
         this.calculateGeometry();
      }

      if (this.slider.getPaintTrack() && var3.intersects(this.trackRect)) {
         this.paintTrack(var1);
      }

      if (this.slider.getPaintTicks() && var3.intersects(this.tickRect)) {
         this.paintTicks(var1);
      }

      if (this.slider.getPaintLabels() && var3.intersects(this.labelRect)) {
         this.paintLabels(var1);
      }

      if (this.slider.hasFocus() && var3.intersects(this.focusRect)) {
         this.paintFocus(var1);
      }

      if (var3.intersects(this.thumbRect)) {
         this.paintThumb(var1);
      }

   }

   protected void recalculateIfInsetsChanged() {
      Insets var1 = this.slider.getInsets();
      if (!var1.equals(this.insetCache)) {
         this.insetCache = var1;
         this.calculateGeometry();
      }

   }

   protected void recalculateIfOrientationChanged() {
      boolean var1 = BasicGraphicsUtils.isLeftToRight(this.slider);
      if (var1 != this.leftToRightCache) {
         this.leftToRightCache = var1;
         this.calculateGeometry();
      }

   }

   public void paintFocus(Graphics var1) {
      var1.setColor(this.getFocusColor());
      BasicGraphicsUtils.drawDashedRect(var1, this.focusRect.x, this.focusRect.y, this.focusRect.width, this.focusRect.height);
   }

   public void paintTrack(Graphics var1) {
      Rectangle var2 = this.trackRect;
      int var3;
      int var4;
      if (this.slider.getOrientation() == 0) {
         var3 = var2.height / 2 - 2;
         var4 = var2.width;
         var1.translate(var2.x, var2.y + var3);
         var1.setColor(this.getShadowColor());
         var1.drawLine(0, 0, var4 - 1, 0);
         var1.drawLine(0, 1, 0, 2);
         var1.setColor(this.getHighlightColor());
         var1.drawLine(0, 3, var4, 3);
         var1.drawLine(var4, 0, var4, 3);
         var1.setColor(Color.black);
         var1.drawLine(1, 1, var4 - 2, 1);
         var1.translate(-var2.x, -(var2.y + var3));
      } else {
         var3 = var2.width / 2 - 2;
         var4 = var2.height;
         var1.translate(var2.x + var3, var2.y);
         var1.setColor(this.getShadowColor());
         var1.drawLine(0, 0, 0, var4 - 1);
         var1.drawLine(1, 0, 2, 0);
         var1.setColor(this.getHighlightColor());
         var1.drawLine(3, 0, 3, var4);
         var1.drawLine(0, var4, 3, var4);
         var1.setColor(Color.black);
         var1.drawLine(1, 1, 1, var4 - 2);
         var1.translate(-(var2.x + var3), -var2.y);
      }

   }

   public void paintTicks(Graphics var1) {
      Rectangle var2 = this.tickRect;
      var1.setColor(DefaultLookup.getColor(this.slider, this, "Slider.tickColor", Color.black));
      int var3;
      int var4;
      if (this.slider.getOrientation() == 0) {
         var1.translate(0, var2.y);
         if (this.slider.getMinorTickSpacing() > 0) {
            for(var3 = this.slider.getMinimum(); var3 <= this.slider.getMaximum(); var3 += this.slider.getMinorTickSpacing()) {
               var4 = this.xPositionForValue(var3);
               this.paintMinorTickForHorizSlider(var1, var2, var4);
               if (Integer.MAX_VALUE - this.slider.getMinorTickSpacing() < var3) {
                  break;
               }
            }
         }

         if (this.slider.getMajorTickSpacing() > 0) {
            for(var3 = this.slider.getMinimum(); var3 <= this.slider.getMaximum(); var3 += this.slider.getMajorTickSpacing()) {
               var4 = this.xPositionForValue(var3);
               this.paintMajorTickForHorizSlider(var1, var2, var4);
               if (Integer.MAX_VALUE - this.slider.getMajorTickSpacing() < var3) {
                  break;
               }
            }
         }

         var1.translate(0, -var2.y);
      } else {
         var1.translate(var2.x, 0);
         if (this.slider.getMinorTickSpacing() > 0) {
            var3 = 0;
            if (!BasicGraphicsUtils.isLeftToRight(this.slider)) {
               var3 = var2.width - var2.width / 2;
               var1.translate(var3, 0);
            }

            for(var4 = this.slider.getMinimum(); var4 <= this.slider.getMaximum(); var4 += this.slider.getMinorTickSpacing()) {
               int var5 = this.yPositionForValue(var4);
               this.paintMinorTickForVertSlider(var1, var2, var5);
               if (Integer.MAX_VALUE - this.slider.getMinorTickSpacing() < var4) {
                  break;
               }
            }

            if (!BasicGraphicsUtils.isLeftToRight(this.slider)) {
               var1.translate(-var3, 0);
            }
         }

         if (this.slider.getMajorTickSpacing() > 0) {
            if (!BasicGraphicsUtils.isLeftToRight(this.slider)) {
               var1.translate(2, 0);
            }

            for(var3 = this.slider.getMinimum(); var3 <= this.slider.getMaximum(); var3 += this.slider.getMajorTickSpacing()) {
               var4 = this.yPositionForValue(var3);
               this.paintMajorTickForVertSlider(var1, var2, var4);
               if (Integer.MAX_VALUE - this.slider.getMajorTickSpacing() < var3) {
                  break;
               }
            }

            if (!BasicGraphicsUtils.isLeftToRight(this.slider)) {
               var1.translate(-2, 0);
            }
         }

         var1.translate(-var2.x, 0);
      }

   }

   protected void paintMinorTickForHorizSlider(Graphics var1, Rectangle var2, int var3) {
      var1.drawLine(var3, 0, var3, var2.height / 2 - 1);
   }

   protected void paintMajorTickForHorizSlider(Graphics var1, Rectangle var2, int var3) {
      var1.drawLine(var3, 0, var3, var2.height - 2);
   }

   protected void paintMinorTickForVertSlider(Graphics var1, Rectangle var2, int var3) {
      var1.drawLine(0, var3, var2.width / 2 - 1, var3);
   }

   protected void paintMajorTickForVertSlider(Graphics var1, Rectangle var2, int var3) {
      var1.drawLine(0, var3, var2.width - 2, var3);
   }

   public void paintLabels(Graphics var1) {
      Rectangle var2 = this.labelRect;
      Dictionary var3 = this.slider.getLabelTable();
      if (var3 != null) {
         Enumeration var4 = var3.keys();
         int var5 = this.slider.getMinimum();
         int var6 = this.slider.getMaximum();
         boolean var7 = this.slider.isEnabled();

         while(var4.hasMoreElements()) {
            Integer var8 = (Integer)var4.nextElement();
            int var9 = var8;
            if (var9 >= var5 && var9 <= var6) {
               JComponent var10 = (JComponent)var3.get(var8);
               var10.setEnabled(var7);
               if (var10 instanceof JLabel) {
                  Icon var11 = var10.isEnabled() ? ((JLabel)var10).getIcon() : ((JLabel)var10).getDisabledIcon();
                  if (var11 instanceof ImageIcon) {
                     Toolkit.getDefaultToolkit().checkImage(((ImageIcon)var11).getImage(), -1, -1, this.slider);
                  }
               }

               if (this.slider.getOrientation() == 0) {
                  var1.translate(0, var2.y);
                  this.paintHorizontalLabel(var1, var9, var10);
                  var1.translate(0, -var2.y);
               } else {
                  int var12 = 0;
                  if (!BasicGraphicsUtils.isLeftToRight(this.slider)) {
                     var12 = var2.width - var10.getPreferredSize().width;
                  }

                  var1.translate(var2.x + var12, 0);
                  this.paintVerticalLabel(var1, var9, var10);
                  var1.translate(-var2.x - var12, 0);
               }
            }
         }
      }

   }

   protected void paintHorizontalLabel(Graphics var1, int var2, Component var3) {
      int var4 = this.xPositionForValue(var2);
      int var5 = var4 - var3.getPreferredSize().width / 2;
      var1.translate(var5, 0);
      var3.paint(var1);
      var1.translate(-var5, 0);
   }

   protected void paintVerticalLabel(Graphics var1, int var2, Component var3) {
      int var4 = this.yPositionForValue(var2);
      int var5 = var4 - var3.getPreferredSize().height / 2;
      var1.translate(0, var5);
      var3.paint(var1);
      var1.translate(0, -var5);
   }

   public void paintThumb(Graphics var1) {
      Rectangle var2 = this.thumbRect;
      int var3 = var2.width;
      int var4 = var2.height;
      var1.translate(var2.x, var2.y);
      if (this.slider.isEnabled()) {
         var1.setColor(this.slider.getBackground());
      } else {
         var1.setColor(this.slider.getBackground().darker());
      }

      Boolean var5 = (Boolean)this.slider.getClientProperty("Slider.paintThumbArrowShape");
      if ((this.slider.getPaintTicks() || var5 != null) && var5 != Boolean.FALSE) {
         int var6;
         Polygon var7;
         if (this.slider.getOrientation() == 0) {
            var6 = var3 / 2;
            var1.fillRect(1, 1, var3 - 3, var4 - 1 - var6);
            var7 = new Polygon();
            var7.addPoint(1, var4 - var6);
            var7.addPoint(var6 - 1, var4 - 1);
            var7.addPoint(var3 - 2, var4 - 1 - var6);
            var1.fillPolygon(var7);
            var1.setColor(this.highlightColor);
            var1.drawLine(0, 0, var3 - 2, 0);
            var1.drawLine(0, 1, 0, var4 - 1 - var6);
            var1.drawLine(0, var4 - var6, var6 - 1, var4 - 1);
            var1.setColor(Color.black);
            var1.drawLine(var3 - 1, 0, var3 - 1, var4 - 2 - var6);
            var1.drawLine(var3 - 1, var4 - 1 - var6, var3 - 1 - var6, var4 - 1);
            var1.setColor(this.shadowColor);
            var1.drawLine(var3 - 2, 1, var3 - 2, var4 - 2 - var6);
            var1.drawLine(var3 - 2, var4 - 1 - var6, var3 - 1 - var6, var4 - 2);
         } else {
            var6 = var4 / 2;
            if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
               var1.fillRect(1, 1, var3 - 1 - var6, var4 - 3);
               var7 = new Polygon();
               var7.addPoint(var3 - var6 - 1, 0);
               var7.addPoint(var3 - 1, var6);
               var7.addPoint(var3 - 1 - var6, var4 - 2);
               var1.fillPolygon(var7);
               var1.setColor(this.highlightColor);
               var1.drawLine(0, 0, 0, var4 - 2);
               var1.drawLine(1, 0, var3 - 1 - var6, 0);
               var1.drawLine(var3 - var6 - 1, 0, var3 - 1, var6);
               var1.setColor(Color.black);
               var1.drawLine(0, var4 - 1, var3 - 2 - var6, var4 - 1);
               var1.drawLine(var3 - 1 - var6, var4 - 1, var3 - 1, var4 - 1 - var6);
               var1.setColor(this.shadowColor);
               var1.drawLine(1, var4 - 2, var3 - 2 - var6, var4 - 2);
               var1.drawLine(var3 - 1 - var6, var4 - 2, var3 - 2, var4 - var6 - 1);
            } else {
               var1.fillRect(5, 1, var3 - 1 - var6, var4 - 3);
               var7 = new Polygon();
               var7.addPoint(var6, 0);
               var7.addPoint(0, var6);
               var7.addPoint(var6, var4 - 2);
               var1.fillPolygon(var7);
               var1.setColor(this.highlightColor);
               var1.drawLine(var6 - 1, 0, var3 - 2, 0);
               var1.drawLine(0, var6, var6, 0);
               var1.setColor(Color.black);
               var1.drawLine(0, var4 - 1 - var6, var6, var4 - 1);
               var1.drawLine(var6, var4 - 1, var3 - 1, var4 - 1);
               var1.setColor(this.shadowColor);
               var1.drawLine(var6, var4 - 2, var3 - 2, var4 - 2);
               var1.drawLine(var3 - 1, 1, var3 - 1, var4 - 2);
            }
         }
      } else {
         var1.fillRect(0, 0, var3, var4);
         var1.setColor(Color.black);
         var1.drawLine(0, var4 - 1, var3 - 1, var4 - 1);
         var1.drawLine(var3 - 1, 0, var3 - 1, var4 - 1);
         var1.setColor(this.highlightColor);
         var1.drawLine(0, 0, 0, var4 - 2);
         var1.drawLine(1, 0, var3 - 2, 0);
         var1.setColor(this.shadowColor);
         var1.drawLine(1, var4 - 2, var3 - 2, var4 - 2);
         var1.drawLine(var3 - 2, 1, var3 - 2, var4 - 3);
      }

      var1.translate(-var2.x, -var2.y);
   }

   public void setThumbLocation(int var1, int var2) {
      unionRect.setBounds(this.thumbRect);
      this.thumbRect.setLocation(var1, var2);
      SwingUtilities.computeUnion(this.thumbRect.x, this.thumbRect.y, this.thumbRect.width, this.thumbRect.height, unionRect);
      this.slider.repaint(unionRect.x, unionRect.y, unionRect.width, unionRect.height);
   }

   public void scrollByBlock(int var1) {
      synchronized(this.slider) {
         int var3 = (this.slider.getMaximum() - this.slider.getMinimum()) / 10;
         if (var3 == 0) {
            var3 = 1;
         }

         int var4;
         if (this.slider.getSnapToTicks()) {
            var4 = this.getTickSpacing();
            if (var3 < var4) {
               var3 = var4;
            }
         }

         var4 = var3 * (var1 > 0 ? 1 : -1);
         this.slider.setValue(this.slider.getValue() + var4);
      }
   }

   public void scrollByUnit(int var1) {
      synchronized(this.slider) {
         int var3 = var1 > 0 ? 1 : -1;
         if (this.slider.getSnapToTicks()) {
            var3 *= this.getTickSpacing();
         }

         this.slider.setValue(this.slider.getValue() + var3);
      }
   }

   protected void scrollDueToClickInTrack(int var1) {
      this.scrollByBlock(var1);
   }

   protected int xPositionForValue(int var1) {
      int var2 = this.slider.getMinimum();
      int var3 = this.slider.getMaximum();
      int var4 = this.trackRect.width;
      double var5 = (double)var3 - (double)var2;
      double var7 = (double)var4 / var5;
      int var9 = this.trackRect.x;
      int var10 = this.trackRect.x + (this.trackRect.width - 1);
      int var11;
      if (!this.drawInverted()) {
         var11 = (int)((long)var9 + Math.round(var7 * ((double)var1 - (double)var2)));
      } else {
         var11 = (int)((long)var10 - Math.round(var7 * ((double)var1 - (double)var2)));
      }

      var11 = Math.max(var9, var11);
      var11 = Math.min(var10, var11);
      return var11;
   }

   protected int yPositionForValue(int var1) {
      return this.yPositionForValue(var1, this.trackRect.y, this.trackRect.height);
   }

   protected int yPositionForValue(int var1, int var2, int var3) {
      int var4 = this.slider.getMinimum();
      int var5 = this.slider.getMaximum();
      double var6 = (double)var5 - (double)var4;
      double var8 = (double)var3 / var6;
      int var10 = var2 + (var3 - 1);
      int var11;
      if (!this.drawInverted()) {
         var11 = (int)((long)var2 + Math.round(var8 * ((double)var5 - (double)var1)));
      } else {
         var11 = (int)((long)var2 + Math.round(var8 * ((double)var1 - (double)var4)));
      }

      var11 = Math.max(var2, var11);
      var11 = Math.min(var10, var11);
      return var11;
   }

   public int valueForYPosition(int var1) {
      int var3 = this.slider.getMinimum();
      int var4 = this.slider.getMaximum();
      int var5 = this.trackRect.height;
      int var6 = this.trackRect.y;
      int var7 = this.trackRect.y + (this.trackRect.height - 1);
      int var2;
      if (var1 <= var6) {
         var2 = this.drawInverted() ? var3 : var4;
      } else if (var1 >= var7) {
         var2 = this.drawInverted() ? var4 : var3;
      } else {
         int var8 = var1 - var6;
         double var9 = (double)var4 - (double)var3;
         double var11 = var9 / (double)var5;
         int var13 = (int)Math.round((double)var8 * var11);
         var2 = this.drawInverted() ? var3 + var13 : var4 - var13;
      }

      return var2;
   }

   public int valueForXPosition(int var1) {
      int var3 = this.slider.getMinimum();
      int var4 = this.slider.getMaximum();
      int var5 = this.trackRect.width;
      int var6 = this.trackRect.x;
      int var7 = this.trackRect.x + (this.trackRect.width - 1);
      int var2;
      if (var1 <= var6) {
         var2 = this.drawInverted() ? var4 : var3;
      } else if (var1 >= var7) {
         var2 = this.drawInverted() ? var3 : var4;
      } else {
         int var8 = var1 - var6;
         double var9 = (double)var4 - (double)var3;
         double var11 = var9 / (double)var5;
         int var13 = (int)Math.round((double)var8 * var11);
         var2 = this.drawInverted() ? var4 - var13 : var3 + var13;
      }

      return var2;
   }

   private static class Actions extends UIAction {
      public static final String POSITIVE_UNIT_INCREMENT = "positiveUnitIncrement";
      public static final String POSITIVE_BLOCK_INCREMENT = "positiveBlockIncrement";
      public static final String NEGATIVE_UNIT_INCREMENT = "negativeUnitIncrement";
      public static final String NEGATIVE_BLOCK_INCREMENT = "negativeBlockIncrement";
      public static final String MIN_SCROLL_INCREMENT = "minScroll";
      public static final String MAX_SCROLL_INCREMENT = "maxScroll";

      Actions() {
         super((String)null);
      }

      public Actions(String var1) {
         super(var1);
      }

      public void actionPerformed(ActionEvent var1) {
         JSlider var2 = (JSlider)var1.getSource();
         BasicSliderUI var3 = (BasicSliderUI)BasicLookAndFeel.getUIOfType(var2.getUI(), BasicSliderUI.class);
         String var4 = this.getName();
         if (var3 != null) {
            if ("positiveUnitIncrement" == var4) {
               this.scroll(var2, var3, 1, false);
            } else if ("negativeUnitIncrement" == var4) {
               this.scroll(var2, var3, -1, false);
            } else if ("positiveBlockIncrement" == var4) {
               this.scroll(var2, var3, 1, true);
            } else if ("negativeBlockIncrement" == var4) {
               this.scroll(var2, var3, -1, true);
            } else if ("minScroll" == var4) {
               this.scroll(var2, var3, -2, false);
            } else if ("maxScroll" == var4) {
               this.scroll(var2, var3, 2, false);
            }

         }
      }

      private void scroll(JSlider var1, BasicSliderUI var2, int var3, boolean var4) {
         boolean var5 = var1.getInverted();
         if (var3 != -1 && var3 != 1) {
            if (var5) {
               var3 = var3 == -2 ? 2 : -2;
            }

            var1.setValue(var3 == -2 ? var1.getMinimum() : var1.getMaximum());
         } else {
            if (var5) {
               var3 = var3 == 1 ? -1 : 1;
            }

            if (var4) {
               var2.scrollByBlock(var3);
            } else {
               var2.scrollByUnit(var3);
            }
         }

      }
   }

   static class SharedActionScroller extends AbstractAction {
      int dir;
      boolean block;

      public SharedActionScroller(int var1, boolean var2) {
         this.dir = var1;
         this.block = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         JSlider var2 = (JSlider)var1.getSource();
         BasicSliderUI var3 = (BasicSliderUI)BasicLookAndFeel.getUIOfType(var2.getUI(), BasicSliderUI.class);
         if (var3 != null) {
            BasicSliderUI.SHARED_ACTION.scroll(var2, var3, this.dir, this.block);
         }
      }
   }

   public class ActionScroller extends AbstractAction {
      int dir;
      boolean block;
      JSlider slider;

      public ActionScroller(JSlider var2, int var3, boolean var4) {
         this.dir = var3;
         this.block = var4;
         this.slider = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         BasicSliderUI.SHARED_ACTION.scroll(this.slider, BasicSliderUI.this, this.dir, this.block);
      }

      public boolean isEnabled() {
         boolean var1 = true;
         if (this.slider != null) {
            var1 = this.slider.isEnabled();
         }

         return var1;
      }
   }

   public class FocusHandler implements FocusListener {
      public void focusGained(FocusEvent var1) {
         BasicSliderUI.this.getHandler().focusGained(var1);
      }

      public void focusLost(FocusEvent var1) {
         BasicSliderUI.this.getHandler().focusLost(var1);
      }
   }

   public class ComponentHandler extends ComponentAdapter {
      public void componentResized(ComponentEvent var1) {
         BasicSliderUI.this.getHandler().componentResized(var1);
      }
   }

   public class ScrollListener implements ActionListener {
      int direction = 1;
      boolean useBlockIncrement;

      public ScrollListener() {
         this.direction = 1;
         this.useBlockIncrement = true;
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
            BasicSliderUI.this.scrollByBlock(this.direction);
         } else {
            BasicSliderUI.this.scrollByUnit(this.direction);
         }

         if (!BasicSliderUI.this.trackListener.shouldScroll(this.direction)) {
            ((Timer)var1.getSource()).stop();
         }

      }
   }

   public class TrackListener extends MouseInputAdapter {
      protected transient int offset;
      protected transient int currentMouseX;
      protected transient int currentMouseY;

      public void mouseReleased(MouseEvent var1) {
         if (BasicSliderUI.this.slider.isEnabled()) {
            this.offset = 0;
            BasicSliderUI.this.scrollTimer.stop();
            BasicSliderUI.this.isDragging = false;
            BasicSliderUI.this.slider.setValueIsAdjusting(false);
            BasicSliderUI.this.slider.repaint();
         }
      }

      public void mousePressed(MouseEvent var1) {
         if (BasicSliderUI.this.slider.isEnabled()) {
            BasicSliderUI.this.calculateGeometry();
            this.currentMouseX = var1.getX();
            this.currentMouseY = var1.getY();
            if (BasicSliderUI.this.slider.isRequestFocusEnabled()) {
               BasicSliderUI.this.slider.requestFocus();
            }

            if (BasicSliderUI.this.thumbRect.contains(this.currentMouseX, this.currentMouseY)) {
               if (!UIManager.getBoolean("Slider.onlyLeftMouseButtonDrag") || SwingUtilities.isLeftMouseButton(var1)) {
                  switch(BasicSliderUI.this.slider.getOrientation()) {
                  case 0:
                     this.offset = this.currentMouseX - BasicSliderUI.this.thumbRect.x;
                     break;
                  case 1:
                     this.offset = this.currentMouseY - BasicSliderUI.this.thumbRect.y;
                  }

                  BasicSliderUI.this.isDragging = true;
               }
            } else if (SwingUtilities.isLeftMouseButton(var1)) {
               BasicSliderUI.this.isDragging = false;
               BasicSliderUI.this.slider.setValueIsAdjusting(true);
               Dimension var2 = BasicSliderUI.this.slider.getSize();
               int var3 = 1;
               int var4;
               switch(BasicSliderUI.this.slider.getOrientation()) {
               case 0:
                  if (BasicSliderUI.this.thumbRect.isEmpty()) {
                     var4 = var2.width / 2;
                     if (!BasicSliderUI.this.drawInverted()) {
                        var3 = this.currentMouseX < var4 ? -1 : 1;
                     } else {
                        var3 = this.currentMouseX < var4 ? 1 : -1;
                     }
                  } else {
                     var4 = BasicSliderUI.this.thumbRect.x;
                     if (!BasicSliderUI.this.drawInverted()) {
                        var3 = this.currentMouseX < var4 ? -1 : 1;
                     } else {
                        var3 = this.currentMouseX < var4 ? 1 : -1;
                     }
                  }
                  break;
               case 1:
                  if (BasicSliderUI.this.thumbRect.isEmpty()) {
                     var4 = var2.height / 2;
                     if (!BasicSliderUI.this.drawInverted()) {
                        var3 = this.currentMouseY < var4 ? 1 : -1;
                     } else {
                        var3 = this.currentMouseY < var4 ? -1 : 1;
                     }
                  } else {
                     var4 = BasicSliderUI.this.thumbRect.y;
                     if (!BasicSliderUI.this.drawInverted()) {
                        var3 = this.currentMouseY < var4 ? 1 : -1;
                     } else {
                        var3 = this.currentMouseY < var4 ? -1 : 1;
                     }
                  }
               }

               if (this.shouldScroll(var3)) {
                  BasicSliderUI.this.scrollDueToClickInTrack(var3);
               }

               if (this.shouldScroll(var3)) {
                  BasicSliderUI.this.scrollTimer.stop();
                  BasicSliderUI.this.scrollListener.setDirection(var3);
                  BasicSliderUI.this.scrollTimer.start();
               }

            }
         }
      }

      public boolean shouldScroll(int var1) {
         Rectangle var2 = BasicSliderUI.this.thumbRect;
         if (BasicSliderUI.this.slider.getOrientation() == 1) {
            label44: {
               label43: {
                  if (BasicSliderUI.this.drawInverted()) {
                     if (var1 >= 0) {
                        break label43;
                     }
                  } else if (var1 <= 0) {
                     break label43;
                  }

                  if (var2.y <= this.currentMouseY) {
                     return false;
                  }
                  break label44;
               }

               if (var2.y + var2.height >= this.currentMouseY) {
                  return false;
               }
            }
         } else {
            label51: {
               label50: {
                  if (BasicSliderUI.this.drawInverted()) {
                     if (var1 >= 0) {
                        break label50;
                     }
                  } else if (var1 <= 0) {
                     break label50;
                  }

                  if (var2.x + var2.width >= this.currentMouseX) {
                     return false;
                  }
                  break label51;
               }

               if (var2.x <= this.currentMouseX) {
                  return false;
               }
            }
         }

         if (var1 > 0 && BasicSliderUI.this.slider.getValue() + BasicSliderUI.this.slider.getExtent() >= BasicSliderUI.this.slider.getMaximum()) {
            return false;
         } else {
            return var1 >= 0 || BasicSliderUI.this.slider.getValue() > BasicSliderUI.this.slider.getMinimum();
         }
      }

      public void mouseDragged(MouseEvent var1) {
         if (BasicSliderUI.this.slider.isEnabled()) {
            this.currentMouseX = var1.getX();
            this.currentMouseY = var1.getY();
            if (BasicSliderUI.this.isDragging) {
               BasicSliderUI.this.slider.setValueIsAdjusting(true);
               int var2;
               switch(BasicSliderUI.this.slider.getOrientation()) {
               case 0:
                  int var8 = BasicSliderUI.this.thumbRect.width / 2;
                  int var9 = var1.getX() - this.offset;
                  int var10 = BasicSliderUI.this.trackRect.x;
                  int var11 = BasicSliderUI.this.trackRect.x + (BasicSliderUI.this.trackRect.width - 1);
                  int var12 = BasicSliderUI.this.xPositionForValue(BasicSliderUI.this.slider.getMaximum() - BasicSliderUI.this.slider.getExtent());
                  if (BasicSliderUI.this.drawInverted()) {
                     var10 = var12;
                  } else {
                     var11 = var12;
                  }

                  var9 = Math.max(var9, var10 - var8);
                  var9 = Math.min(var9, var11 - var8);
                  BasicSliderUI.this.setThumbLocation(var9, BasicSliderUI.this.thumbRect.y);
                  var2 = var9 + var8;
                  BasicSliderUI.this.slider.setValue(BasicSliderUI.this.valueForXPosition(var2));
                  break;
               case 1:
                  int var3 = BasicSliderUI.this.thumbRect.height / 2;
                  int var4 = var1.getY() - this.offset;
                  int var5 = BasicSliderUI.this.trackRect.y;
                  int var6 = BasicSliderUI.this.trackRect.y + (BasicSliderUI.this.trackRect.height - 1);
                  int var7 = BasicSliderUI.this.yPositionForValue(BasicSliderUI.this.slider.getMaximum() - BasicSliderUI.this.slider.getExtent());
                  if (BasicSliderUI.this.drawInverted()) {
                     var6 = var7;
                  } else {
                     var5 = var7;
                  }

                  var4 = Math.max(var4, var5 - var3);
                  var4 = Math.min(var4, var6 - var3);
                  BasicSliderUI.this.setThumbLocation(BasicSliderUI.this.thumbRect.x, var4);
                  var2 = var4 + var3;
                  BasicSliderUI.this.slider.setValue(BasicSliderUI.this.valueForYPosition(var2));
               }

            }
         }
      }

      public void mouseMoved(MouseEvent var1) {
      }
   }

   public class ChangeHandler implements ChangeListener {
      public void stateChanged(ChangeEvent var1) {
         BasicSliderUI.this.getHandler().stateChanged(var1);
      }
   }

   private class Handler implements ChangeListener, ComponentListener, FocusListener, PropertyChangeListener {
      private Handler() {
      }

      public void stateChanged(ChangeEvent var1) {
         if (!BasicSliderUI.this.isDragging) {
            BasicSliderUI.this.calculateThumbLocation();
            BasicSliderUI.this.slider.repaint();
         }

         BasicSliderUI.this.lastValue = BasicSliderUI.this.slider.getValue();
      }

      public void componentHidden(ComponentEvent var1) {
      }

      public void componentMoved(ComponentEvent var1) {
      }

      public void componentResized(ComponentEvent var1) {
         BasicSliderUI.this.calculateGeometry();
         BasicSliderUI.this.slider.repaint();
      }

      public void componentShown(ComponentEvent var1) {
      }

      public void focusGained(FocusEvent var1) {
         BasicSliderUI.this.slider.repaint();
      }

      public void focusLost(FocusEvent var1) {
         BasicSliderUI.this.slider.repaint();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2 != "orientation" && var2 != "inverted" && var2 != "labelTable" && var2 != "majorTickSpacing" && var2 != "minorTickSpacing" && var2 != "paintTicks" && var2 != "paintTrack" && var2 != "font" && var2 != "paintLabels" && var2 != "Slider.paintThumbArrowShape") {
            if (var2 == "componentOrientation") {
               BasicSliderUI.this.calculateGeometry();
               BasicSliderUI.this.slider.repaint();
               InputMap var3 = BasicSliderUI.this.getInputMap(0, BasicSliderUI.this.slider);
               SwingUtilities.replaceUIInputMap(BasicSliderUI.this.slider, 0, var3);
            } else if (var2 == "model") {
               ((BoundedRangeModel)var1.getOldValue()).removeChangeListener(BasicSliderUI.this.changeListener);
               ((BoundedRangeModel)var1.getNewValue()).addChangeListener(BasicSliderUI.this.changeListener);
               BasicSliderUI.this.calculateThumbLocation();
               BasicSliderUI.this.slider.repaint();
            }
         } else {
            BasicSliderUI.this.checkedLabelBaselines = false;
            BasicSliderUI.this.calculateGeometry();
            BasicSliderUI.this.slider.repaint();
         }

      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }

   public class PropertyChangeHandler implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         BasicSliderUI.this.getHandler().propertyChange(var1);
      }
   }
}
