package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Dictionary;
import java.util.Enumeration;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;
import sun.swing.SwingUtilities2;

public class SynthSliderUI extends BasicSliderUI implements PropertyChangeListener, SynthUI {
   private Rectangle valueRect = new Rectangle();
   private boolean paintValue;
   private Dimension lastSize;
   private int trackHeight;
   private int trackBorder;
   private int thumbWidth;
   private int thumbHeight;
   private SynthStyle style;
   private SynthStyle sliderTrackStyle;
   private SynthStyle sliderThumbStyle;
   private transient boolean thumbActive;
   private transient boolean thumbPressed;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthSliderUI((JSlider)var0);
   }

   protected SynthSliderUI(JSlider var1) {
      super(var1);
   }

   protected void installDefaults(JSlider var1) {
      this.updateStyle(var1);
   }

   protected void uninstallDefaults(JSlider var1) {
      SynthContext var2 = this.getContext(var1, 1);
      this.style.uninstallDefaults(var2);
      var2.dispose();
      this.style = null;
      var2 = this.getContext(var1, Region.SLIDER_TRACK, 1);
      this.sliderTrackStyle.uninstallDefaults(var2);
      var2.dispose();
      this.sliderTrackStyle = null;
      var2 = this.getContext(var1, Region.SLIDER_THUMB, 1);
      this.sliderThumbStyle.uninstallDefaults(var2);
      var2.dispose();
      this.sliderThumbStyle = null;
   }

   protected void installListeners(JSlider var1) {
      super.installListeners(var1);
      var1.addPropertyChangeListener(this);
   }

   protected void uninstallListeners(JSlider var1) {
      var1.removePropertyChangeListener(this);
      super.uninstallListeners(var1);
   }

   private void updateStyle(JSlider var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3) {
         this.thumbWidth = this.style.getInt(var2, "Slider.thumbWidth", 30);
         this.thumbHeight = this.style.getInt(var2, "Slider.thumbHeight", 14);
         String var4 = (String)this.slider.getClientProperty("JComponent.sizeVariant");
         if (var4 != null) {
            if ("large".equals(var4)) {
               this.thumbWidth = (int)((double)this.thumbWidth * 1.15D);
               this.thumbHeight = (int)((double)this.thumbHeight * 1.15D);
            } else if ("small".equals(var4)) {
               this.thumbWidth = (int)((double)this.thumbWidth * 0.857D);
               this.thumbHeight = (int)((double)this.thumbHeight * 0.857D);
            } else if ("mini".equals(var4)) {
               this.thumbWidth = (int)((double)this.thumbWidth * 0.784D);
               this.thumbHeight = (int)((double)this.thumbHeight * 0.784D);
            }
         }

         this.trackBorder = this.style.getInt(var2, "Slider.trackBorder", 1);
         this.trackHeight = this.thumbHeight + this.trackBorder * 2;
         this.paintValue = this.style.getBoolean(var2, "Slider.paintValue", true);
         if (var3 != null) {
            this.uninstallKeyboardActions(var1);
            this.installKeyboardActions(var1);
         }
      }

      var2.dispose();
      var2 = this.getContext(var1, Region.SLIDER_TRACK, 1);
      this.sliderTrackStyle = SynthLookAndFeel.updateStyle(var2, this);
      var2.dispose();
      var2 = this.getContext(var1, Region.SLIDER_THUMB, 1);
      this.sliderThumbStyle = SynthLookAndFeel.updateStyle(var2, this);
      var2.dispose();
   }

   protected BasicSliderUI.TrackListener createTrackListener(JSlider var1) {
      return new SynthSliderUI.SynthTrackListener();
   }

   private void updateThumbState(int var1, int var2) {
      this.setThumbActive(this.thumbRect.contains(var1, var2));
   }

   private void updateThumbState(int var1, int var2, boolean var3) {
      this.updateThumbState(var1, var2);
      this.setThumbPressed(var3);
   }

   private void setThumbActive(boolean var1) {
      if (this.thumbActive != var1) {
         this.thumbActive = var1;
         this.slider.repaint(this.thumbRect);
      }

   }

   private void setThumbPressed(boolean var1) {
      if (this.thumbPressed != var1) {
         this.thumbPressed = var1;
         this.slider.repaint(this.thumbRect);
      }

   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException("Component must be non-null");
      } else if (var2 >= 0 && var3 >= 0) {
         if (this.slider.getPaintLabels() && this.labelsHaveSameBaselines()) {
            Insets var4 = new Insets(0, 0, 0, 0);
            SynthContext var5 = this.getContext(this.slider, Region.SLIDER_TRACK);
            this.style.getInsets(var5, var4);
            var5.dispose();
            int var7;
            int var8;
            int var10;
            int var17;
            if (this.slider.getOrientation() == 0) {
               int var15 = 0;
               if (this.paintValue) {
                  SynthContext var16 = this.getContext(this.slider);
                  var15 = var16.getStyle().getGraphicsUtils(var16).getMaximumCharHeight(var16);
                  var16.dispose();
               }

               var7 = 0;
               if (this.slider.getPaintTicks()) {
                  var7 = this.getTickLength();
               }

               var8 = this.getHeightOfTallestLabel();
               var17 = var15 + this.trackHeight + var4.top + var4.bottom + var7 + var8 + 4;
               var10 = var3 / 2 - var17 / 2;
               var10 += var15 + 2;
               var10 += this.trackHeight + var4.top + var4.bottom;
               var10 += var7 + 2;
               JComponent var18 = (JComponent)this.slider.getLabelTable().elements().nextElement();
               Dimension var19 = var18.getPreferredSize();
               return var10 + var18.getBaseline(var19.width, var19.height);
            }

            Integer var6 = this.slider.getInverted() ? this.getLowestValue() : this.getHighestValue();
            if (var6 != null) {
               var7 = this.insetCache.top;
               var8 = 0;
               if (this.paintValue) {
                  SynthContext var9 = this.getContext(this.slider);
                  var8 = var9.getStyle().getGraphicsUtils(var9).getMaximumCharHeight(var9);
                  var9.dispose();
               }

               var17 = var3 - this.insetCache.top - this.insetCache.bottom;
               var10 = var7 + var8;
               int var11 = var17 - var8;
               int var12 = this.yPositionForValue(var6, var10, var11);
               JComponent var13 = (JComponent)this.slider.getLabelTable().get(var6);
               Dimension var14 = var13.getPreferredSize();
               return var12 - var14.height / 2 + var13.getBaseline(var14.width, var14.height);
            }
         }

         return -1;
      } else {
         throw new IllegalArgumentException("Width and height must be >= 0");
      }
   }

   public Dimension getPreferredSize(JComponent var1) {
      this.recalculateIfInsetsChanged();
      Dimension var2 = new Dimension(this.contentRect.width, this.contentRect.height);
      if (this.slider.getOrientation() == 1) {
         var2.height = 200;
      } else {
         var2.width = 200;
      }

      Insets var3 = this.slider.getInsets();
      var2.width += var3.left + var3.right;
      var2.height += var3.top + var3.bottom;
      return var2;
   }

   public Dimension getMinimumSize(JComponent var1) {
      this.recalculateIfInsetsChanged();
      Dimension var2 = new Dimension(this.contentRect.width, this.contentRect.height);
      if (this.slider.getOrientation() == 1) {
         var2.height = this.thumbRect.height + this.insetCache.top + this.insetCache.bottom;
      } else {
         var2.width = this.thumbRect.width + this.insetCache.left + this.insetCache.right;
      }

      return var2;
   }

   protected void calculateGeometry() {
      this.calculateThumbSize();
      this.layout();
      this.calculateThumbLocation();
   }

   protected void layout() {
      SynthContext var1 = this.getContext(this.slider);
      SynthGraphicsUtils var2 = this.style.getGraphicsUtils(var1);
      Insets var3 = new Insets(0, 0, 0, 0);
      SynthContext var4 = this.getContext(this.slider, Region.SLIDER_TRACK);
      this.style.getInsets(var4, var3);
      var4.dispose();
      int var7;
      int var8;
      int var9;
      int var14;
      if (this.slider.getOrientation() == 0) {
         this.valueRect.height = 0;
         if (this.paintValue) {
            this.valueRect.height = var2.getMaximumCharHeight(var1);
         }

         this.trackRect.height = this.trackHeight;
         this.tickRect.height = 0;
         if (this.slider.getPaintTicks()) {
            this.tickRect.height = this.getTickLength();
         }

         this.labelRect.height = 0;
         if (this.slider.getPaintLabels()) {
            this.labelRect.height = this.getHeightOfTallestLabel();
         }

         this.contentRect.height = this.valueRect.height + this.trackRect.height + var3.top + var3.bottom + this.tickRect.height + this.labelRect.height + 4;
         this.contentRect.width = this.slider.getWidth() - this.insetCache.left - this.insetCache.right;
         int var5 = 0;
         if (this.slider.getPaintLabels()) {
            this.trackRect.x = this.insetCache.left;
            this.trackRect.width = this.contentRect.width;
            Dictionary var6 = this.slider.getLabelTable();
            if (var6 != null) {
               var7 = this.slider.getMinimum();
               var8 = this.slider.getMaximum();
               var9 = Integer.MAX_VALUE;
               int var10 = Integer.MIN_VALUE;
               Enumeration var11 = var6.keys();

               while(var11.hasMoreElements()) {
                  int var12 = (Integer)var11.nextElement();
                  if (var12 >= var7 && var12 < var9) {
                     var9 = var12;
                  }

                  if (var12 <= var8 && var12 > var10) {
                     var10 = var12;
                  }
               }

               var5 = this.getPadForLabel(var9);
               var5 = Math.max(var5, this.getPadForLabel(var10));
            }
         }

         this.valueRect.x = this.trackRect.x = this.tickRect.x = this.labelRect.x = this.insetCache.left + var5;
         this.valueRect.width = this.trackRect.width = this.tickRect.width = this.labelRect.width = this.contentRect.width - var5 * 2;
         var14 = this.slider.getHeight() / 2 - this.contentRect.height / 2;
         this.valueRect.y = var14;
         var14 += this.valueRect.height + 2;
         this.trackRect.y = var14 + var3.top;
         var14 += this.trackRect.height + var3.top + var3.bottom;
         this.tickRect.y = var14;
         var14 += this.tickRect.height + 2;
         this.labelRect.y = var14;
         int var10000 = var14 + this.labelRect.height;
      } else {
         this.trackRect.width = this.trackHeight;
         this.tickRect.width = 0;
         if (this.slider.getPaintTicks()) {
            this.tickRect.width = this.getTickLength();
         }

         this.labelRect.width = 0;
         if (this.slider.getPaintLabels()) {
            this.labelRect.width = this.getWidthOfWidestLabel();
         }

         this.valueRect.y = this.insetCache.top;
         this.valueRect.height = 0;
         if (this.paintValue) {
            this.valueRect.height = var2.getMaximumCharHeight(var1);
         }

         FontMetrics var13 = this.slider.getFontMetrics(this.slider.getFont());
         this.valueRect.width = Math.max(var2.computeStringWidth(var1, this.slider.getFont(), var13, "" + this.slider.getMaximum()), var2.computeStringWidth(var1, this.slider.getFont(), var13, "" + this.slider.getMinimum()));
         var14 = this.valueRect.width / 2;
         var7 = var3.left + this.trackRect.width / 2;
         var8 = this.trackRect.width / 2 + var3.right + this.tickRect.width + this.labelRect.width;
         this.contentRect.width = Math.max(var7, var14) + Math.max(var8, var14) + 2 + this.insetCache.left + this.insetCache.right;
         this.contentRect.height = this.slider.getHeight() - this.insetCache.top - this.insetCache.bottom;
         this.trackRect.y = this.tickRect.y = this.labelRect.y = this.valueRect.y + this.valueRect.height;
         this.trackRect.height = this.tickRect.height = this.labelRect.height = this.contentRect.height - this.valueRect.height;
         var9 = this.slider.getWidth() / 2 - this.contentRect.width / 2;
         if (SynthLookAndFeel.isLeftToRight(this.slider)) {
            if (var14 > var7) {
               var9 += var14 - var7;
            }

            this.trackRect.x = var9 + var3.left;
            var9 += var3.left + this.trackRect.width + var3.right;
            this.tickRect.x = var9;
            this.labelRect.x = var9 + this.tickRect.width + 2;
         } else {
            if (var14 > var8) {
               var9 += var14 - var8;
            }

            this.labelRect.x = var9;
            var9 += this.labelRect.width + 2;
            this.tickRect.x = var9;
            this.trackRect.x = var9 + this.tickRect.width + var3.left;
         }
      }

      var1.dispose();
      this.lastSize = this.slider.getSize();
   }

   private int getPadForLabel(int var1) {
      int var2 = 0;
      JComponent var3 = (JComponent)this.slider.getLabelTable().get(var1);
      if (var3 != null) {
         int var4 = this.xPositionForValue(var1);
         int var5 = var3.getPreferredSize().width / 2;
         if (var4 - var5 < this.insetCache.left) {
            var2 = Math.max(var2, this.insetCache.left - (var4 - var5));
         }

         if (var4 + var5 > this.slider.getWidth() - this.insetCache.right) {
            var2 = Math.max(var2, var4 + var5 - (this.slider.getWidth() - this.insetCache.right));
         }
      }

      return var2;
   }

   protected void calculateThumbLocation() {
      super.calculateThumbLocation();
      Rectangle var10000;
      if (this.slider.getOrientation() == 0) {
         var10000 = this.thumbRect;
         var10000.y += this.trackBorder;
      } else {
         var10000 = this.thumbRect;
         var10000.x += this.trackBorder;
      }

      Point var1 = this.slider.getMousePosition();
      if (var1 != null) {
         this.updateThumbState(var1.x, var1.y);
      }

   }

   public void setThumbLocation(int var1, int var2) {
      super.setThumbLocation(var1, var2);
      this.slider.repaint(this.valueRect.x, this.valueRect.y, this.valueRect.width, this.valueRect.height);
      this.setThumbActive(false);
   }

   protected int xPositionForValue(int var1) {
      int var2 = this.slider.getMinimum();
      int var3 = this.slider.getMaximum();
      int var4 = this.trackRect.x + this.thumbRect.width / 2 + this.trackBorder;
      int var5 = this.trackRect.x + this.trackRect.width - this.thumbRect.width / 2 - this.trackBorder;
      int var6 = var5 - var4;
      double var7 = (double)var3 - (double)var2;
      double var9 = (double)var6 / var7;
      int var11;
      if (!this.drawInverted()) {
         var11 = (int)((long)var4 + Math.round(var9 * ((double)var1 - (double)var2)));
      } else {
         var11 = (int)((long)var5 - Math.round(var9 * ((double)var1 - (double)var2)));
      }

      var11 = Math.max(var4, var11);
      var11 = Math.min(var5, var11);
      return var11;
   }

   protected int yPositionForValue(int var1, int var2, int var3) {
      int var4 = this.slider.getMinimum();
      int var5 = this.slider.getMaximum();
      int var6 = var2 + this.thumbRect.height / 2 + this.trackBorder;
      int var7 = var2 + var3 - this.thumbRect.height / 2 - this.trackBorder;
      int var8 = var7 - var6;
      double var9 = (double)var5 - (double)var4;
      double var11 = (double)var8 / var9;
      int var13;
      if (!this.drawInverted()) {
         var13 = (int)((long)var6 + Math.round(var11 * ((double)var5 - (double)var1)));
      } else {
         var13 = (int)((long)var6 + Math.round(var11 * ((double)var1 - (double)var4)));
      }

      var13 = Math.max(var6, var13);
      var13 = Math.min(var7, var13);
      return var13;
   }

   public int valueForYPosition(int var1) {
      int var3 = this.slider.getMinimum();
      int var4 = this.slider.getMaximum();
      int var5 = this.trackRect.y + this.thumbRect.height / 2 + this.trackBorder;
      int var6 = this.trackRect.y + this.trackRect.height - this.thumbRect.height / 2 - this.trackBorder;
      int var7 = var6 - var5;
      int var2;
      if (var1 <= var5) {
         var2 = this.drawInverted() ? var3 : var4;
      } else if (var1 >= var6) {
         var2 = this.drawInverted() ? var4 : var3;
      } else {
         int var8 = var1 - var5;
         double var9 = (double)var4 - (double)var3;
         double var11 = var9 / (double)var7;
         int var13 = (int)Math.round((double)var8 * var11);
         var2 = this.drawInverted() ? var3 + var13 : var4 - var13;
      }

      return var2;
   }

   public int valueForXPosition(int var1) {
      int var3 = this.slider.getMinimum();
      int var4 = this.slider.getMaximum();
      int var5 = this.trackRect.x + this.thumbRect.width / 2 + this.trackBorder;
      int var6 = this.trackRect.x + this.trackRect.width - this.thumbRect.width / 2 - this.trackBorder;
      int var7 = var6 - var5;
      int var2;
      if (var1 <= var5) {
         var2 = this.drawInverted() ? var4 : var3;
      } else if (var1 >= var6) {
         var2 = this.drawInverted() ? var3 : var4;
      } else {
         int var8 = var1 - var5;
         double var9 = (double)var4 - (double)var3;
         double var11 = var9 / (double)var7;
         int var13 = (int)Math.round((double)var8 * var11);
         var2 = this.drawInverted() ? var4 - var13 : var3 + var13;
      }

      return var2;
   }

   protected Dimension getThumbSize() {
      Dimension var1 = new Dimension();
      if (this.slider.getOrientation() == 1) {
         var1.width = this.thumbHeight;
         var1.height = this.thumbWidth;
      } else {
         var1.width = this.thumbWidth;
         var1.height = this.thumbHeight;
      }

      return var1;
   }

   protected void recalculateIfInsetsChanged() {
      SynthContext var1 = this.getContext(this.slider);
      Insets var2 = this.style.getInsets(var1, (Insets)null);
      Insets var3 = this.slider.getInsets();
      var2.left += var3.left;
      var2.right += var3.right;
      var2.top += var3.top;
      var2.bottom += var3.bottom;
      if (!var2.equals(this.insetCache)) {
         this.insetCache = var2;
         this.calculateGeometry();
      }

      var1.dispose();
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, SynthLookAndFeel.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   private SynthContext getContext(JComponent var1, Region var2) {
      return this.getContext(var1, var2, this.getComponentState(var1, var2));
   }

   private SynthContext getContext(JComponent var1, Region var2, int var3) {
      SynthStyle var4 = null;
      if (var2 == Region.SLIDER_TRACK) {
         var4 = this.sliderTrackStyle;
      } else if (var2 == Region.SLIDER_THUMB) {
         var4 = this.sliderThumbStyle;
      }

      return SynthContext.getContext(var1, var2, var4, var3);
   }

   private int getComponentState(JComponent var1, Region var2) {
      if (var2 == Region.SLIDER_THUMB && this.thumbActive && var1.isEnabled()) {
         int var3 = this.thumbPressed ? 4 : 2;
         if (var1.isFocusOwner()) {
            var3 |= 256;
         }

         return var3;
      } else {
         return SynthLookAndFeel.getComponentState(var1);
      }
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintSliderBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight(), this.slider.getOrientation());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      this.recalculateIfInsetsChanged();
      this.recalculateIfOrientationChanged();
      Rectangle var3 = var2.getClipBounds();
      if (this.lastSize == null || !this.lastSize.equals(this.slider.getSize())) {
         this.calculateGeometry();
      }

      if (this.paintValue) {
         FontMetrics var4 = SwingUtilities2.getFontMetrics(this.slider, (Graphics)var2);
         int var5 = var1.getStyle().getGraphicsUtils(var1).computeStringWidth(var1, var2.getFont(), var4, "" + this.slider.getValue());
         this.valueRect.x = this.thumbRect.x + (this.thumbRect.width - var5) / 2;
         if (this.slider.getOrientation() == 0) {
            if (this.valueRect.x + var5 > this.insetCache.left + this.contentRect.width) {
               this.valueRect.x = this.insetCache.left + this.contentRect.width - var5;
            }

            this.valueRect.x = Math.max(this.valueRect.x, 0);
         }

         var2.setColor(var1.getStyle().getColor(var1, ColorType.TEXT_FOREGROUND));
         var1.getStyle().getGraphicsUtils(var1).paintText(var1, var2, "" + this.slider.getValue(), this.valueRect.x, this.valueRect.y, -1);
      }

      SynthContext var6;
      if (this.slider.getPaintTrack() && var3.intersects(this.trackRect)) {
         var6 = this.getContext(this.slider, Region.SLIDER_TRACK);
         this.paintTrack(var6, var2, this.trackRect);
         var6.dispose();
      }

      if (var3.intersects(this.thumbRect)) {
         var6 = this.getContext(this.slider, Region.SLIDER_THUMB);
         this.paintThumb(var6, var2, this.thumbRect);
         var6.dispose();
      }

      if (this.slider.getPaintTicks() && var3.intersects(this.tickRect)) {
         this.paintTicks(var2);
      }

      if (this.slider.getPaintLabels() && var3.intersects(this.labelRect)) {
         this.paintLabels(var2);
      }

   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintSliderBorder(var1, var2, var3, var4, var5, var6, this.slider.getOrientation());
   }

   protected void paintThumb(SynthContext var1, Graphics var2, Rectangle var3) {
      int var4 = this.slider.getOrientation();
      SynthLookAndFeel.updateSubregion(var1, var2, var3);
      var1.getPainter().paintSliderThumbBackground(var1, var2, var3.x, var3.y, var3.width, var3.height, var4);
      var1.getPainter().paintSliderThumbBorder(var1, var2, var3.x, var3.y, var3.width, var3.height, var4);
   }

   protected void paintTrack(SynthContext var1, Graphics var2, Rectangle var3) {
      int var4 = this.slider.getOrientation();
      SynthLookAndFeel.updateSubregion(var1, var2, var3);
      var1.getPainter().paintSliderTrackBackground(var1, var2, var3.x, var3.y, var3.width, var3.height, var4);
      var1.getPainter().paintSliderTrackBorder(var1, var2, var3.x, var3.y, var3.width, var3.height, var4);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JSlider)var1.getSource());
      }

   }

   private class SynthTrackListener extends BasicSliderUI.TrackListener {
      private SynthTrackListener() {
         super();
      }

      public void mouseExited(MouseEvent var1) {
         SynthSliderUI.this.setThumbActive(false);
      }

      public void mousePressed(MouseEvent var1) {
         super.mousePressed(var1);
         SynthSliderUI.this.setThumbPressed(SynthSliderUI.this.thumbRect.contains(var1.getX(), var1.getY()));
      }

      public void mouseReleased(MouseEvent var1) {
         super.mouseReleased(var1);
         SynthSliderUI.this.updateThumbState(var1.getX(), var1.getY(), false);
      }

      public void mouseDragged(MouseEvent var1) {
         if (SynthSliderUI.this.slider.isEnabled()) {
            this.currentMouseX = var1.getX();
            this.currentMouseY = var1.getY();
            if (SynthSliderUI.this.isDragging()) {
               SynthSliderUI.this.slider.setValueIsAdjusting(true);
               int var2;
               switch(SynthSliderUI.this.slider.getOrientation()) {
               case 0:
                  int var8 = SynthSliderUI.this.thumbRect.width / 2;
                  int var9 = var1.getX() - this.offset;
                  int var10 = SynthSliderUI.this.trackRect.x + var8 + SynthSliderUI.this.trackBorder;
                  int var11 = SynthSliderUI.this.trackRect.x + SynthSliderUI.this.trackRect.width - var8 - SynthSliderUI.this.trackBorder;
                  int var12 = SynthSliderUI.this.xPositionForValue(SynthSliderUI.this.slider.getMaximum() - SynthSliderUI.this.slider.getExtent());
                  if (SynthSliderUI.this.drawInverted()) {
                     var10 = var12;
                  } else {
                     var11 = var12;
                  }

                  var9 = Math.max(var9, var10 - var8);
                  var9 = Math.min(var9, var11 - var8);
                  SynthSliderUI.this.setThumbLocation(var9, SynthSliderUI.this.thumbRect.y);
                  var2 = var9 + var8;
                  SynthSliderUI.this.slider.setValue(SynthSliderUI.this.valueForXPosition(var2));
                  break;
               case 1:
                  int var3 = SynthSliderUI.this.thumbRect.height / 2;
                  int var4 = var1.getY() - this.offset;
                  int var5 = SynthSliderUI.this.trackRect.y;
                  int var6 = SynthSliderUI.this.trackRect.y + SynthSliderUI.this.trackRect.height - var3 - SynthSliderUI.this.trackBorder;
                  int var7 = SynthSliderUI.this.yPositionForValue(SynthSliderUI.this.slider.getMaximum() - SynthSliderUI.this.slider.getExtent());
                  if (SynthSliderUI.this.drawInverted()) {
                     var6 = var7;
                     var5 += var3;
                  } else {
                     var5 = var7;
                  }

                  var4 = Math.max(var4, var5 - var3);
                  var4 = Math.min(var4, var6 - var3);
                  SynthSliderUI.this.setThumbLocation(SynthSliderUI.this.thumbRect.x, var4);
                  var2 = var4 + var3;
                  SynthSliderUI.this.slider.setValue(SynthSliderUI.this.valueForYPosition(var2));
                  break;
               default:
                  return;
               }

               if (SynthSliderUI.this.slider.getValueIsAdjusting()) {
                  SynthSliderUI.this.setThumbActive(true);
               }

            }
         }
      }

      public void mouseMoved(MouseEvent var1) {
         SynthSliderUI.this.updateThumbState(var1.getX(), var1.getY());
      }

      // $FF: synthetic method
      SynthTrackListener(Object var2) {
         this();
      }
   }
}
