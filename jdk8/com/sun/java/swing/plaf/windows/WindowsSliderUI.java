package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

public class WindowsSliderUI extends BasicSliderUI {
   private boolean rollover = false;
   private boolean pressed = false;

   public WindowsSliderUI(JSlider var1) {
      super(var1);
   }

   public static ComponentUI createUI(JComponent var0) {
      return new WindowsSliderUI((JSlider)var0);
   }

   protected BasicSliderUI.TrackListener createTrackListener(JSlider var1) {
      return new WindowsSliderUI.WindowsTrackListener();
   }

   public void paintTrack(Graphics var1) {
      XPStyle var2 = XPStyle.getXP();
      if (var2 != null) {
         boolean var3 = this.slider.getOrientation() == 1;
         TMSchema.Part var4 = var3 ? TMSchema.Part.TKP_TRACKVERT : TMSchema.Part.TKP_TRACK;
         XPStyle.Skin var5 = var2.getSkin(this.slider, var4);
         int var6;
         if (var3) {
            var6 = (this.trackRect.width - var5.getWidth()) / 2;
            var5.paintSkin(var1, this.trackRect.x + var6, this.trackRect.y, var5.getWidth(), this.trackRect.height, (TMSchema.State)null);
         } else {
            var6 = (this.trackRect.height - var5.getHeight()) / 2;
            var5.paintSkin(var1, this.trackRect.x, this.trackRect.y + var6, this.trackRect.width, var5.getHeight(), (TMSchema.State)null);
         }
      } else {
         super.paintTrack(var1);
      }

   }

   protected void paintMinorTickForHorizSlider(Graphics var1, Rectangle var2, int var3) {
      XPStyle var4 = XPStyle.getXP();
      if (var4 != null) {
         var1.setColor(var4.getColor(this.slider, TMSchema.Part.TKP_TICS, (TMSchema.State)null, TMSchema.Prop.COLOR, Color.black));
      }

      super.paintMinorTickForHorizSlider(var1, var2, var3);
   }

   protected void paintMajorTickForHorizSlider(Graphics var1, Rectangle var2, int var3) {
      XPStyle var4 = XPStyle.getXP();
      if (var4 != null) {
         var1.setColor(var4.getColor(this.slider, TMSchema.Part.TKP_TICS, (TMSchema.State)null, TMSchema.Prop.COLOR, Color.black));
      }

      super.paintMajorTickForHorizSlider(var1, var2, var3);
   }

   protected void paintMinorTickForVertSlider(Graphics var1, Rectangle var2, int var3) {
      XPStyle var4 = XPStyle.getXP();
      if (var4 != null) {
         var1.setColor(var4.getColor(this.slider, TMSchema.Part.TKP_TICSVERT, (TMSchema.State)null, TMSchema.Prop.COLOR, Color.black));
      }

      super.paintMinorTickForVertSlider(var1, var2, var3);
   }

   protected void paintMajorTickForVertSlider(Graphics var1, Rectangle var2, int var3) {
      XPStyle var4 = XPStyle.getXP();
      if (var4 != null) {
         var1.setColor(var4.getColor(this.slider, TMSchema.Part.TKP_TICSVERT, (TMSchema.State)null, TMSchema.Prop.COLOR, Color.black));
      }

      super.paintMajorTickForVertSlider(var1, var2, var3);
   }

   public void paintThumb(Graphics var1) {
      XPStyle var2 = XPStyle.getXP();
      if (var2 != null) {
         TMSchema.Part var3 = this.getXPThumbPart();
         TMSchema.State var4 = TMSchema.State.NORMAL;
         if (this.slider.hasFocus()) {
            var4 = TMSchema.State.FOCUSED;
         }

         if (this.rollover) {
            var4 = TMSchema.State.HOT;
         }

         if (this.pressed) {
            var4 = TMSchema.State.PRESSED;
         }

         if (!this.slider.isEnabled()) {
            var4 = TMSchema.State.DISABLED;
         }

         var2.getSkin(this.slider, var3).paintSkin(var1, this.thumbRect.x, this.thumbRect.y, var4);
      } else {
         super.paintThumb(var1);
      }

   }

   protected Dimension getThumbSize() {
      XPStyle var1 = XPStyle.getXP();
      if (var1 != null) {
         Dimension var2 = new Dimension();
         XPStyle.Skin var3 = var1.getSkin(this.slider, this.getXPThumbPart());
         var2.width = var3.getWidth();
         var2.height = var3.getHeight();
         return var2;
      } else {
         return super.getThumbSize();
      }
   }

   private TMSchema.Part getXPThumbPart() {
      boolean var2 = this.slider.getOrientation() == 1;
      boolean var3 = this.slider.getComponentOrientation().isLeftToRight();
      Boolean var4 = (Boolean)this.slider.getClientProperty("Slider.paintThumbArrowShape");
      TMSchema.Part var1;
      if ((this.slider.getPaintTicks() || var4 != null) && var4 != Boolean.FALSE) {
         var1 = var2 ? (var3 ? TMSchema.Part.TKP_THUMBRIGHT : TMSchema.Part.TKP_THUMBLEFT) : TMSchema.Part.TKP_THUMBBOTTOM;
      } else {
         var1 = var2 ? TMSchema.Part.TKP_THUMBVERT : TMSchema.Part.TKP_THUMB;
      }

      return var1;
   }

   private class WindowsTrackListener extends BasicSliderUI.TrackListener {
      private WindowsTrackListener() {
         super();
      }

      public void mouseMoved(MouseEvent var1) {
         this.updateRollover(WindowsSliderUI.this.thumbRect.contains(var1.getX(), var1.getY()));
         super.mouseMoved(var1);
      }

      public void mouseEntered(MouseEvent var1) {
         this.updateRollover(WindowsSliderUI.this.thumbRect.contains(var1.getX(), var1.getY()));
         super.mouseEntered(var1);
      }

      public void mouseExited(MouseEvent var1) {
         this.updateRollover(false);
         super.mouseExited(var1);
      }

      public void mousePressed(MouseEvent var1) {
         this.updatePressed(WindowsSliderUI.this.thumbRect.contains(var1.getX(), var1.getY()));
         super.mousePressed(var1);
      }

      public void mouseReleased(MouseEvent var1) {
         this.updatePressed(false);
         super.mouseReleased(var1);
      }

      public void updatePressed(boolean var1) {
         if (WindowsSliderUI.this.slider.isEnabled()) {
            if (WindowsSliderUI.this.pressed != var1) {
               WindowsSliderUI.this.pressed = var1;
               WindowsSliderUI.this.slider.repaint(WindowsSliderUI.this.thumbRect);
            }

         }
      }

      public void updateRollover(boolean var1) {
         if (WindowsSliderUI.this.slider.isEnabled()) {
            if (WindowsSliderUI.this.rollover != var1) {
               WindowsSliderUI.this.rollover = var1;
               WindowsSliderUI.this.slider.repaint(WindowsSliderUI.this.thumbRect);
            }

         }
      }

      // $FF: synthetic method
      WindowsTrackListener(Object var2) {
         this();
      }
   }
}
