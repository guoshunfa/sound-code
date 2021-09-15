package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import apple.laf.JRSUIStateFactory;
import apple.laf.JRSUIUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

public class AquaSliderUI extends BasicSliderUI implements AquaUtilControlSize.Sizeable {
   protected static final AquaUtils.RecyclableSingleton<AquaUtilControlSize.SizeDescriptor> roundThumbDescriptor = new AquaUtils.RecyclableSingleton<AquaUtilControlSize.SizeDescriptor>() {
      protected AquaUtilControlSize.SizeDescriptor getInstance() {
         return new AquaUtilControlSize.SizeDescriptor(new AquaUtilControlSize.SizeVariant(25, 25)) {
            public AquaUtilControlSize.SizeVariant deriveSmall(AquaUtilControlSize.SizeVariant var1) {
               return super.deriveSmall(var1.alterMinSize(-2, -2));
            }

            public AquaUtilControlSize.SizeVariant deriveMini(AquaUtilControlSize.SizeVariant var1) {
               return super.deriveMini(var1.alterMinSize(-2, -2));
            }
         };
      }
   };
   protected static final AquaUtils.RecyclableSingleton<AquaUtilControlSize.SizeDescriptor> pointingThumbDescriptor = new AquaUtils.RecyclableSingleton<AquaUtilControlSize.SizeDescriptor>() {
      protected AquaUtilControlSize.SizeDescriptor getInstance() {
         return new AquaUtilControlSize.SizeDescriptor(new AquaUtilControlSize.SizeVariant(23, 26)) {
            public AquaUtilControlSize.SizeVariant deriveSmall(AquaUtilControlSize.SizeVariant var1) {
               return super.deriveSmall(var1.alterMinSize(-2, -2));
            }

            public AquaUtilControlSize.SizeVariant deriveMini(AquaUtilControlSize.SizeVariant var1) {
               return super.deriveMini(var1.alterMinSize(-2, -2));
            }
         };
      }
   };
   static final AquaPainter<JRSUIState> trackPainter = AquaPainter.create(JRSUIStateFactory.getSliderTrack(), new JRSUIUtils.NineSliceMetricsProvider() {
      public AquaImageFactory.NineSliceMetrics getNineSliceMetricsForState(JRSUIState var1) {
         return var1.is(JRSUIConstants.Orientation.VERTICAL) ? new AquaImageFactory.NineSliceMetrics(5, 7, 0, 0, 3, 3, true, false, true) : new AquaImageFactory.NineSliceMetrics(7, 5, 3, 3, 0, 0, true, true, false);
      }
   });
   final AquaPainter<JRSUIState> thumbPainter = AquaPainter.create(JRSUIStateFactory.getSliderThumb());
   protected Color tickColor;
   protected Color disabledTickColor;
   protected transient boolean fIsDragging = false;
   static final int kTickWidth = 3;
   static final int kTickLength = 8;

   public static ComponentUI createUI(JComponent var0) {
      return new AquaSliderUI((JSlider)var0);
   }

   public AquaSliderUI(JSlider var1) {
      super(var1);
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      LookAndFeel.installProperty(this.slider, "opaque", Boolean.FALSE);
      this.tickColor = UIManager.getColor("Slider.tickColor");
   }

   protected BasicSliderUI.TrackListener createTrackListener(JSlider var1) {
      return new AquaSliderUI.TrackListener();
   }

   protected void installListeners(JSlider var1) {
      super.installListeners(var1);
      AquaFocusHandler.install(var1);
      AquaUtilControlSize.addSizePropertyListener(var1);
   }

   protected void uninstallListeners(JSlider var1) {
      AquaUtilControlSize.removeSizePropertyListener(var1);
      AquaFocusHandler.uninstall(var1);
      super.uninstallListeners(var1);
   }

   public void applySizeFor(JComponent var1, JRSUIConstants.Size var2) {
      this.thumbPainter.state.set(var2);
      trackPainter.state.set(var2);
   }

   public void paint(Graphics var1, JComponent var2) {
      this.recalculateIfInsetsChanged();
      Rectangle var3 = var1.getClipBounds();
      JRSUIConstants.Orientation var4 = this.slider.getOrientation() == 0 ? JRSUIConstants.Orientation.HORIZONTAL : JRSUIConstants.Orientation.VERTICAL;
      JRSUIConstants.State var5 = this.getState();
      if (this.slider.getPaintTrack()) {
         boolean var6 = var3.intersects(this.trackRect);
         if (!var6) {
            this.calculateGeometry();
         }

         if (var6 || var3.intersects(this.thumbRect)) {
            this.paintTrack(var1, var2, var4, var5);
         }
      }

      if (this.slider.getPaintTicks() && var3.intersects(this.tickRect)) {
         this.paintTicks(var1);
      }

      if (this.slider.getPaintLabels() && var3.intersects(this.labelRect)) {
         this.paintLabels(var1);
      }

      if (var3.intersects(this.thumbRect)) {
         this.paintThumb(var1, var2, var4, var5);
      }

   }

   public void paintTrack(Graphics var1, JComponent var2, JRSUIConstants.Orientation var3, JRSUIConstants.State var4) {
      trackPainter.state.set(var3);
      trackPainter.state.set(var4);
      trackPainter.paint(var1, var2, this.trackRect.x, this.trackRect.y, this.trackRect.width, this.trackRect.height);
   }

   public void paintThumb(Graphics var1, JComponent var2, JRSUIConstants.Orientation var3, JRSUIConstants.State var4) {
      this.thumbPainter.state.set(var3);
      this.thumbPainter.state.set(var4);
      this.thumbPainter.state.set(this.slider.hasFocus() ? JRSUIConstants.Focused.YES : JRSUIConstants.Focused.NO);
      this.thumbPainter.state.set(this.getDirection(var3));
      this.thumbPainter.paint(var1, var2, this.thumbRect.x, this.thumbRect.y, this.thumbRect.width, this.thumbRect.height);
   }

   JRSUIConstants.Direction getDirection(JRSUIConstants.Orientation var1) {
      if (this.shouldUseArrowThumb()) {
         return var1 == JRSUIConstants.Orientation.HORIZONTAL ? JRSUIConstants.Direction.DOWN : JRSUIConstants.Direction.RIGHT;
      } else {
         return JRSUIConstants.Direction.NONE;
      }
   }

   JRSUIConstants.State getState() {
      if (!this.slider.isEnabled()) {
         return JRSUIConstants.State.DISABLED;
      } else if (this.fIsDragging) {
         return JRSUIConstants.State.PRESSED;
      } else {
         return !AquaFocusHandler.isActive(this.slider) ? JRSUIConstants.State.INACTIVE : JRSUIConstants.State.ACTIVE;
      }
   }

   public void paintTicks(Graphics var1) {
      if (this.slider.isEnabled()) {
         var1.setColor(this.tickColor);
      } else {
         if (this.disabledTickColor == null) {
            this.disabledTickColor = new Color(this.tickColor.getRed(), this.tickColor.getGreen(), this.tickColor.getBlue(), this.tickColor.getAlpha() / 2);
         }

         var1.setColor(this.disabledTickColor);
      }

      super.paintTicks(var1);
   }

   protected void calculateThumbLocation() {
      super.calculateThumbLocation();
      if (this.shouldUseArrowThumb()) {
         boolean var1 = this.slider.getOrientation() == 0;
         JRSUIConstants.Size var2 = AquaUtilControlSize.getUserSizeFrom(this.slider);
         Rectangle var10000;
         if (var2 == JRSUIConstants.Size.REGULAR) {
            if (var1) {
               var10000 = this.thumbRect;
               var10000.y += 3;
            } else {
               var10000 = this.thumbRect;
               var10000.x += 2;
            }

            return;
         }

         if (var2 == JRSUIConstants.Size.SMALL) {
            if (var1) {
               var10000 = this.thumbRect;
               var10000.y += 2;
            } else {
               var10000 = this.thumbRect;
               var10000.x += 2;
            }

            return;
         }

         if (var2 == JRSUIConstants.Size.MINI) {
            if (var1) {
               ++this.thumbRect.y;
            }

            return;
         }
      }

   }

   protected void calculateThumbSize() {
      AquaUtilControlSize.SizeDescriptor var1 = this.shouldUseArrowThumb() ? (AquaUtilControlSize.SizeDescriptor)pointingThumbDescriptor.get() : (AquaUtilControlSize.SizeDescriptor)roundThumbDescriptor.get();
      AquaUtilControlSize.SizeVariant var2 = var1.get((JComponent)this.slider);
      if (this.slider.getOrientation() == 0) {
         this.thumbRect.setSize(var2.w, var2.h);
      } else {
         this.thumbRect.setSize(var2.h, var2.w);
      }

   }

   protected boolean shouldUseArrowThumb() {
      if (!this.slider.getPaintTicks() && !this.slider.getPaintLabels()) {
         Object var1 = this.slider.getClientProperty("Slider.paintThumbArrowShape");
         return var1 != null && var1 instanceof Boolean ? (Boolean)var1 : false;
      } else {
         return true;
      }
   }

   protected void calculateTickRect() {
      int var1 = this.slider.getPaintTicks() ? this.getTickLength() : 0;
      if (this.slider.getOrientation() == 0) {
         this.tickRect.height = var1;
         this.tickRect.x = this.trackRect.x + this.trackBuffer;
         this.tickRect.y = this.trackRect.y + this.trackRect.height - this.tickRect.height / 2;
         this.tickRect.width = this.trackRect.width - this.trackBuffer * 2;
      } else {
         this.tickRect.width = var1;
         this.tickRect.x = this.trackRect.x + this.trackRect.width - this.tickRect.width / 2;
         this.tickRect.y = this.trackRect.y + this.trackBuffer;
         this.tickRect.height = this.trackRect.height - this.trackBuffer * 2;
      }

   }

   public Dimension getPreferredHorizontalSize() {
      return new Dimension(190, 21);
   }

   public Dimension getPreferredVerticalSize() {
      return new Dimension(21, 190);
   }

   protected ChangeListener createChangeListener(JSlider var1) {
      return new ChangeListener() {
         public void stateChanged(ChangeEvent var1) {
            if (!AquaSliderUI.this.fIsDragging) {
               AquaSliderUI.this.calculateThumbLocation();
               AquaSliderUI.this.slider.repaint();
            }
         }
      };
   }

   int getScale() {
      if (!this.slider.getSnapToTicks()) {
         return 1;
      } else {
         int var1 = this.slider.getMinorTickSpacing();
         if (var1 < 1) {
            var1 = this.slider.getMajorTickSpacing();
         }

         return var1 < 1 ? 1 : var1;
      }
   }

   class TrackListener extends BasicSliderUI.TrackListener {
      protected transient int offset;
      protected transient int currentMouseX = -1;
      protected transient int currentMouseY = -1;

      TrackListener() {
         super();
      }

      public void mouseReleased(MouseEvent var1) {
         if (AquaSliderUI.this.slider.isEnabled()) {
            this.currentMouseX = -1;
            this.currentMouseY = -1;
            this.offset = 0;
            AquaSliderUI.this.scrollTimer.stop();
            if (AquaSliderUI.this.slider.getSnapToTicks()) {
               AquaSliderUI.this.fIsDragging = false;
               AquaSliderUI.this.slider.setValueIsAdjusting(false);
            } else {
               AquaSliderUI.this.slider.setValueIsAdjusting(false);
               AquaSliderUI.this.fIsDragging = false;
            }

            AquaSliderUI.this.slider.repaint();
         }
      }

      public void mousePressed(MouseEvent var1) {
         if (AquaSliderUI.this.slider.isEnabled()) {
            AquaSliderUI.this.calculateGeometry();
            boolean var2 = this.currentMouseX == -1 && this.currentMouseY == -1;
            this.currentMouseX = var1.getX();
            this.currentMouseY = var1.getY();
            if (AquaSliderUI.this.slider.isRequestFocusEnabled()) {
               AquaSliderUI.this.slider.requestFocus();
            }

            boolean var3 = AquaSliderUI.this.thumbRect.contains(this.currentMouseX, this.currentMouseY);
            if (!var2 || !var3) {
               AquaSliderUI.this.slider.setValueIsAdjusting(true);
               switch(AquaSliderUI.this.slider.getOrientation()) {
               case 0:
                  AquaSliderUI.this.slider.setValue(AquaSliderUI.this.valueForXPosition(this.currentMouseX));
                  break;
               case 1:
                  AquaSliderUI.this.slider.setValue(AquaSliderUI.this.valueForYPosition(this.currentMouseY));
               }

               AquaSliderUI.this.slider.setValueIsAdjusting(false);
               var3 = true;
            }

            if (var3) {
               switch(AquaSliderUI.this.slider.getOrientation()) {
               case 0:
                  this.offset = this.currentMouseX - AquaSliderUI.this.thumbRect.x;
                  break;
               case 1:
                  this.offset = this.currentMouseY - AquaSliderUI.this.thumbRect.y;
               }

               AquaSliderUI.this.fIsDragging = true;
            } else {
               AquaSliderUI.this.fIsDragging = false;
            }
         }
      }

      public boolean shouldScroll(int var1) {
         Rectangle var2 = AquaSliderUI.this.thumbRect;
         if (AquaSliderUI.this.slider.getOrientation() == 1) {
            label44: {
               label43: {
                  if (AquaSliderUI.this.drawInverted()) {
                     if (var1 >= 0) {
                        break label43;
                     }
                  } else if (var1 <= 0) {
                     break label43;
                  }

                  if (var2.y + var2.height <= this.currentMouseY) {
                     return false;
                  }
                  break label44;
               }

               if (var2.y >= this.currentMouseY) {
                  return false;
               }
            }
         } else {
            label51: {
               label50: {
                  if (AquaSliderUI.this.drawInverted()) {
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

         if (var1 > 0 && AquaSliderUI.this.slider.getValue() + AquaSliderUI.this.slider.getExtent() >= AquaSliderUI.this.slider.getMaximum()) {
            return false;
         } else {
            return var1 >= 0 || AquaSliderUI.this.slider.getValue() > AquaSliderUI.this.slider.getMinimum();
         }
      }

      public void mouseDragged(MouseEvent var1) {
         boolean var2 = false;
         if (AquaSliderUI.this.slider.isEnabled()) {
            this.currentMouseX = var1.getX();
            this.currentMouseY = var1.getY();
            if (AquaSliderUI.this.fIsDragging) {
               AquaSliderUI.this.slider.setValueIsAdjusting(true);
               int var13;
               switch(AquaSliderUI.this.slider.getOrientation()) {
               case 0:
                  int var8 = AquaSliderUI.this.thumbRect.width / 2;
                  int var9 = var1.getX() - this.offset;
                  int var10 = AquaSliderUI.this.trackRect.x;
                  int var11 = AquaSliderUI.this.trackRect.x + (AquaSliderUI.this.trackRect.width - 1);
                  int var12 = AquaSliderUI.this.xPositionForValue(AquaSliderUI.this.slider.getMaximum() - AquaSliderUI.this.slider.getExtent());
                  if (AquaSliderUI.this.drawInverted()) {
                     var10 = var12;
                  } else {
                     var11 = var12;
                  }

                  var9 = Math.max(var9, var10 - var8);
                  var9 = Math.min(var9, var11 - var8);
                  AquaSliderUI.this.setThumbLocation(var9, AquaSliderUI.this.thumbRect.y);
                  var13 = var9 + var8;
                  AquaSliderUI.this.slider.setValue(AquaSliderUI.this.valueForXPosition(var13));
                  break;
               case 1:
                  int var3 = AquaSliderUI.this.thumbRect.height / 2;
                  int var4 = var1.getY() - this.offset;
                  int var5 = AquaSliderUI.this.trackRect.y;
                  int var6 = AquaSliderUI.this.trackRect.y + (AquaSliderUI.this.trackRect.height - 1);
                  int var7 = AquaSliderUI.this.yPositionForValue(AquaSliderUI.this.slider.getMaximum() - AquaSliderUI.this.slider.getExtent());
                  if (AquaSliderUI.this.drawInverted()) {
                     var6 = var7;
                  } else {
                     var5 = var7;
                  }

                  var4 = Math.max(var4, var5 - var3);
                  var4 = Math.min(var4, var6 - var3);
                  AquaSliderUI.this.setThumbLocation(AquaSliderUI.this.thumbRect.x, var4);
                  var13 = var4 + var3;
                  AquaSliderUI.this.slider.setValue(AquaSliderUI.this.valueForYPosition(var13));
                  break;
               default:
                  return;
               }

               if (AquaSliderUI.this.slider.getSnapToTicks()) {
                  AquaSliderUI.this.calculateThumbLocation();
                  AquaSliderUI.this.setThumbLocation(AquaSliderUI.this.thumbRect.x, AquaSliderUI.this.thumbRect.y);
               }

            }
         }
      }

      public void mouseMoved(MouseEvent var1) {
      }
   }
}
