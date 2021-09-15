package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

public class MetalSliderUI extends BasicSliderUI {
   protected final int TICK_BUFFER = 4;
   protected boolean filledSlider = false;
   protected static Color thumbColor;
   protected static Color highlightColor;
   protected static Color darkShadowColor;
   protected static int trackWidth;
   protected static int tickLength;
   private int safeLength;
   protected static Icon horizThumbIcon;
   protected static Icon vertThumbIcon;
   private static Icon SAFE_HORIZ_THUMB_ICON;
   private static Icon SAFE_VERT_THUMB_ICON;
   protected final String SLIDER_FILL = "JSlider.isFilled";

   public static ComponentUI createUI(JComponent var0) {
      return new MetalSliderUI();
   }

   public MetalSliderUI() {
      super((JSlider)null);
   }

   private static Icon getHorizThumbIcon() {
      return System.getSecurityManager() != null ? SAFE_HORIZ_THUMB_ICON : horizThumbIcon;
   }

   private static Icon getVertThumbIcon() {
      return System.getSecurityManager() != null ? SAFE_VERT_THUMB_ICON : vertThumbIcon;
   }

   public void installUI(JComponent var1) {
      trackWidth = (Integer)UIManager.get("Slider.trackWidth");
      tickLength = this.safeLength = (Integer)UIManager.get("Slider.majorTickLength");
      horizThumbIcon = SAFE_HORIZ_THUMB_ICON = UIManager.getIcon("Slider.horizontalThumbIcon");
      vertThumbIcon = SAFE_VERT_THUMB_ICON = UIManager.getIcon("Slider.verticalThumbIcon");
      super.installUI(var1);
      thumbColor = UIManager.getColor("Slider.thumb");
      highlightColor = UIManager.getColor("Slider.highlight");
      darkShadowColor = UIManager.getColor("Slider.darkShadow");
      this.scrollListener.setScrollByBlock(false);
      this.prepareFilledSliderField();
   }

   protected PropertyChangeListener createPropertyChangeListener(JSlider var1) {
      return new MetalSliderUI.MetalPropertyListener();
   }

   private void prepareFilledSliderField() {
      this.filledSlider = MetalLookAndFeel.usingOcean();
      Object var1 = this.slider.getClientProperty("JSlider.isFilled");
      if (var1 != null) {
         this.filledSlider = (Boolean)var1;
      }

   }

   public void paintThumb(Graphics var1) {
      Rectangle var2 = this.thumbRect;
      var1.translate(var2.x, var2.y);
      if (this.slider.getOrientation() == 0) {
         getHorizThumbIcon().paintIcon(this.slider, var1, 0, 0);
      } else {
         getVertThumbIcon().paintIcon(this.slider, var1, 0, 0);
      }

      var1.translate(-var2.x, -var2.y);
   }

   private Rectangle getPaintTrackRect() {
      int var1 = 0;
      int var3 = 0;
      int var2;
      int var4;
      if (this.slider.getOrientation() == 0) {
         var4 = this.trackRect.height - 1 - this.getThumbOverhang();
         var3 = var4 - (this.getTrackWidth() - 1);
         var2 = this.trackRect.width - 1;
      } else {
         if (MetalUtils.isLeftToRight(this.slider)) {
            var1 = this.trackRect.width - this.getThumbOverhang() - this.getTrackWidth();
            var2 = this.trackRect.width - this.getThumbOverhang() - 1;
         } else {
            var1 = this.getThumbOverhang();
            var2 = this.getThumbOverhang() + this.getTrackWidth() - 1;
         }

         var4 = this.trackRect.height - 1;
      }

      return new Rectangle(this.trackRect.x + var1, this.trackRect.y + var3, var2 - var1, var4 - var3);
   }

   public void paintTrack(Graphics var1) {
      if (MetalLookAndFeel.usingOcean()) {
         this.oceanPaintTrack(var1);
      } else {
         if (!this.slider.isEnabled()) {
            MetalLookAndFeel.getControlShadow();
         } else {
            this.slider.getForeground();
         }

         boolean var3 = MetalUtils.isLeftToRight(this.slider);
         var1.translate(this.trackRect.x, this.trackRect.y);
         int var4 = 0;
         int var5 = 0;
         int var6;
         int var7;
         if (this.slider.getOrientation() == 0) {
            var7 = this.trackRect.height - 1 - this.getThumbOverhang();
            var5 = var7 - (this.getTrackWidth() - 1);
            var6 = this.trackRect.width - 1;
         } else {
            if (var3) {
               var4 = this.trackRect.width - this.getThumbOverhang() - this.getTrackWidth();
               var6 = this.trackRect.width - this.getThumbOverhang() - 1;
            } else {
               var4 = this.getThumbOverhang();
               var6 = this.getThumbOverhang() + this.getTrackWidth() - 1;
            }

            var7 = this.trackRect.height - 1;
         }

         if (this.slider.isEnabled()) {
            var1.setColor(MetalLookAndFeel.getControlDarkShadow());
            var1.drawRect(var4, var5, var6 - var4 - 1, var7 - var5 - 1);
            var1.setColor(MetalLookAndFeel.getControlHighlight());
            var1.drawLine(var4 + 1, var7, var6, var7);
            var1.drawLine(var6, var5 + 1, var6, var7);
            var1.setColor(MetalLookAndFeel.getControlShadow());
            var1.drawLine(var4 + 1, var5 + 1, var6 - 2, var5 + 1);
            var1.drawLine(var4 + 1, var5 + 1, var4 + 1, var7 - 2);
         } else {
            var1.setColor(MetalLookAndFeel.getControlShadow());
            var1.drawRect(var4, var5, var6 - var4 - 1, var7 - var5 - 1);
         }

         if (this.filledSlider) {
            int var8;
            int var9;
            int var10;
            int var11;
            int var12;
            if (this.slider.getOrientation() == 0) {
               var8 = this.thumbRect.x + this.thumbRect.width / 2;
               var8 -= this.trackRect.x;
               var9 = !this.slider.isEnabled() ? var5 : var5 + 1;
               var11 = !this.slider.isEnabled() ? var7 - 1 : var7 - 2;
               if (!this.drawInverted()) {
                  var10 = !this.slider.isEnabled() ? var4 : var4 + 1;
                  var12 = var8;
               } else {
                  var10 = var8;
                  var12 = !this.slider.isEnabled() ? var6 - 1 : var6 - 2;
               }
            } else {
               var8 = this.thumbRect.y + this.thumbRect.height / 2;
               var8 -= this.trackRect.y;
               var10 = !this.slider.isEnabled() ? var4 : var4 + 1;
               var12 = !this.slider.isEnabled() ? var6 - 1 : var6 - 2;
               if (!this.drawInverted()) {
                  var9 = var8;
                  var11 = !this.slider.isEnabled() ? var7 - 1 : var7 - 2;
               } else {
                  var9 = !this.slider.isEnabled() ? var5 : var5 + 1;
                  var11 = var8;
               }
            }

            if (this.slider.isEnabled()) {
               var1.setColor(this.slider.getBackground());
               var1.drawLine(var10, var9, var12, var9);
               var1.drawLine(var10, var9, var10, var11);
               var1.setColor(MetalLookAndFeel.getControlShadow());
               var1.fillRect(var10 + 1, var9 + 1, var12 - var10, var11 - var9);
            } else {
               var1.setColor(MetalLookAndFeel.getControlShadow());
               var1.fillRect(var10, var9, var12 - var10, var11 - var9);
            }
         }

         var1.translate(-this.trackRect.x, -this.trackRect.y);
      }
   }

   private void oceanPaintTrack(Graphics var1) {
      boolean var2 = MetalUtils.isLeftToRight(this.slider);
      boolean var3 = this.drawInverted();
      Color var4 = (Color)UIManager.get("Slider.altTrackColor");
      Rectangle var5 = this.getPaintTrackRect();
      var1.translate(var5.x, var5.y);
      int var6 = var5.width;
      int var7 = var5.height;
      int var8;
      int var9;
      int var10;
      if (this.slider.getOrientation() == 0) {
         var8 = this.thumbRect.x + this.thumbRect.width / 2 - var5.x;
         if (this.slider.isEnabled()) {
            if (var8 > 0) {
               var1.setColor(var3 ? MetalLookAndFeel.getControlDarkShadow() : MetalLookAndFeel.getPrimaryControlDarkShadow());
               var1.drawRect(0, 0, var8 - 1, var7 - 1);
            }

            if (var8 < var6) {
               var1.setColor(var3 ? MetalLookAndFeel.getPrimaryControlDarkShadow() : MetalLookAndFeel.getControlDarkShadow());
               var1.drawRect(var8, 0, var6 - var8 - 1, var7 - 1);
            }

            if (this.filledSlider) {
               var1.setColor(MetalLookAndFeel.getPrimaryControlShadow());
               if (var3) {
                  var9 = var8;
                  var10 = var6 - 2;
                  var1.drawLine(1, 1, var8, 1);
               } else {
                  var9 = 1;
                  var10 = var8;
                  var1.drawLine(var8, 1, var6 - 1, 1);
               }

               if (var7 == 6) {
                  var1.setColor(MetalLookAndFeel.getWhite());
                  var1.drawLine(var9, 1, var10, 1);
                  var1.setColor(var4);
                  var1.drawLine(var9, 2, var10, 2);
                  var1.setColor(MetalLookAndFeel.getControlShadow());
                  var1.drawLine(var9, 3, var10, 3);
                  var1.setColor(MetalLookAndFeel.getPrimaryControlShadow());
                  var1.drawLine(var9, 4, var10, 4);
               }
            }
         } else {
            var1.setColor(MetalLookAndFeel.getControlShadow());
            if (var8 > 0) {
               if (!var3 && this.filledSlider) {
                  var1.fillRect(0, 0, var8 - 1, var7 - 1);
               } else {
                  var1.drawRect(0, 0, var8 - 1, var7 - 1);
               }
            }

            if (var8 < var6) {
               if (var3 && this.filledSlider) {
                  var1.fillRect(var8, 0, var6 - var8 - 1, var7 - 1);
               } else {
                  var1.drawRect(var8, 0, var6 - var8 - 1, var7 - 1);
               }
            }
         }
      } else {
         var8 = this.thumbRect.y + this.thumbRect.height / 2 - var5.y;
         if (this.slider.isEnabled()) {
            if (var8 > 0) {
               var1.setColor(var3 ? MetalLookAndFeel.getPrimaryControlDarkShadow() : MetalLookAndFeel.getControlDarkShadow());
               var1.drawRect(0, 0, var6 - 1, var8 - 1);
            }

            if (var8 < var7) {
               var1.setColor(var3 ? MetalLookAndFeel.getControlDarkShadow() : MetalLookAndFeel.getPrimaryControlDarkShadow());
               var1.drawRect(0, var8, var6 - 1, var7 - var8 - 1);
            }

            if (this.filledSlider) {
               var1.setColor(MetalLookAndFeel.getPrimaryControlShadow());
               if (this.drawInverted()) {
                  var9 = 1;
                  var10 = var8;
                  if (var2) {
                     var1.drawLine(1, var8, 1, var7 - 1);
                  } else {
                     var1.drawLine(var6 - 2, var8, var6 - 2, var7 - 1);
                  }
               } else {
                  var9 = var8;
                  var10 = var7 - 2;
                  if (var2) {
                     var1.drawLine(1, 1, 1, var8);
                  } else {
                     var1.drawLine(var6 - 2, 1, var6 - 2, var8);
                  }
               }

               if (var6 == 6) {
                  var1.setColor(var2 ? MetalLookAndFeel.getWhite() : MetalLookAndFeel.getPrimaryControlShadow());
                  var1.drawLine(1, var9, 1, var10);
                  var1.setColor((Color)(var2 ? var4 : MetalLookAndFeel.getControlShadow()));
                  var1.drawLine(2, var9, 2, var10);
                  var1.setColor((Color)(var2 ? MetalLookAndFeel.getControlShadow() : var4));
                  var1.drawLine(3, var9, 3, var10);
                  var1.setColor(var2 ? MetalLookAndFeel.getPrimaryControlShadow() : MetalLookAndFeel.getWhite());
                  var1.drawLine(4, var9, 4, var10);
               }
            }
         } else {
            var1.setColor(MetalLookAndFeel.getControlShadow());
            if (var8 > 0) {
               if (var3 && this.filledSlider) {
                  var1.fillRect(0, 0, var6 - 1, var8 - 1);
               } else {
                  var1.drawRect(0, 0, var6 - 1, var8 - 1);
               }
            }

            if (var8 < var7) {
               if (!var3 && this.filledSlider) {
                  var1.fillRect(0, var8, var6 - 1, var7 - var8 - 1);
               } else {
                  var1.drawRect(0, var8, var6 - 1, var7 - var8 - 1);
               }
            }
         }
      }

      var1.translate(-var5.x, -var5.y);
   }

   public void paintFocus(Graphics var1) {
   }

   protected Dimension getThumbSize() {
      Dimension var1 = new Dimension();
      if (this.slider.getOrientation() == 1) {
         var1.width = getVertThumbIcon().getIconWidth();
         var1.height = getVertThumbIcon().getIconHeight();
      } else {
         var1.width = getHorizThumbIcon().getIconWidth();
         var1.height = getHorizThumbIcon().getIconHeight();
      }

      return var1;
   }

   public int getTickLength() {
      return this.slider.getOrientation() == 0 ? this.safeLength + 4 + 1 : this.safeLength + 4 + 3;
   }

   protected int getTrackWidth() {
      return this.slider.getOrientation() == 0 ? (int)(0.4375D * (double)this.thumbRect.height) : (int)(0.4375D * (double)this.thumbRect.width);
   }

   protected int getTrackLength() {
      return this.slider.getOrientation() == 0 ? this.trackRect.width : this.trackRect.height;
   }

   protected int getThumbOverhang() {
      return (int)(this.getThumbSize().getHeight() - (double)this.getTrackWidth()) / 2;
   }

   protected void scrollDueToClickInTrack(int var1) {
      this.scrollByUnit(var1);
   }

   protected void paintMinorTickForHorizSlider(Graphics var1, Rectangle var2, int var3) {
      var1.setColor((Color)(this.slider.isEnabled() ? this.slider.getForeground() : MetalLookAndFeel.getControlShadow()));
      var1.drawLine(var3, 4, var3, 4 + this.safeLength / 2);
   }

   protected void paintMajorTickForHorizSlider(Graphics var1, Rectangle var2, int var3) {
      var1.setColor((Color)(this.slider.isEnabled() ? this.slider.getForeground() : MetalLookAndFeel.getControlShadow()));
      var1.drawLine(var3, 4, var3, 4 + (this.safeLength - 1));
   }

   protected void paintMinorTickForVertSlider(Graphics var1, Rectangle var2, int var3) {
      var1.setColor((Color)(this.slider.isEnabled() ? this.slider.getForeground() : MetalLookAndFeel.getControlShadow()));
      if (MetalUtils.isLeftToRight(this.slider)) {
         var1.drawLine(4, var3, 4 + this.safeLength / 2, var3);
      } else {
         var1.drawLine(0, var3, this.safeLength / 2, var3);
      }

   }

   protected void paintMajorTickForVertSlider(Graphics var1, Rectangle var2, int var3) {
      var1.setColor((Color)(this.slider.isEnabled() ? this.slider.getForeground() : MetalLookAndFeel.getControlShadow()));
      if (MetalUtils.isLeftToRight(this.slider)) {
         var1.drawLine(4, var3, 4 + this.safeLength, var3);
      } else {
         var1.drawLine(0, var3, this.safeLength, var3);
      }

   }

   protected class MetalPropertyListener extends BasicSliderUI.PropertyChangeHandler {
      protected MetalPropertyListener() {
         super();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         super.propertyChange(var1);
         if (var1.getPropertyName().equals("JSlider.isFilled")) {
            MetalSliderUI.this.prepareFilledSliderField();
         }

      }
   }
}
