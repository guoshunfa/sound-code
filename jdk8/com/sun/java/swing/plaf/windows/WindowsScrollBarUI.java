package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class WindowsScrollBarUI extends BasicScrollBarUI {
   private WindowsScrollBarUI.Grid thumbGrid;
   private WindowsScrollBarUI.Grid highlightGrid;
   private Dimension horizontalThumbSize;
   private Dimension verticalThumbSize;

   public static ComponentUI createUI(JComponent var0) {
      return new WindowsScrollBarUI();
   }

   protected void installDefaults() {
      super.installDefaults();
      XPStyle var1 = XPStyle.getXP();
      if (var1 != null) {
         this.scrollbar.setBorder((Border)null);
         this.horizontalThumbSize = getSize(this.scrollbar, var1, TMSchema.Part.SBP_THUMBBTNHORZ);
         this.verticalThumbSize = getSize(this.scrollbar, var1, TMSchema.Part.SBP_THUMBBTNVERT);
      } else {
         this.horizontalThumbSize = null;
         this.verticalThumbSize = null;
      }

   }

   private static Dimension getSize(Component var0, XPStyle var1, TMSchema.Part var2) {
      XPStyle.Skin var3 = var1.getSkin(var0, var2);
      return new Dimension(var3.getWidth(), var3.getHeight());
   }

   protected Dimension getMinimumThumbSize() {
      if (this.horizontalThumbSize != null && this.verticalThumbSize != null) {
         return 0 == this.scrollbar.getOrientation() ? this.horizontalThumbSize : this.verticalThumbSize;
      } else {
         return super.getMinimumThumbSize();
      }
   }

   public void uninstallUI(JComponent var1) {
      super.uninstallUI(var1);
      this.thumbGrid = this.highlightGrid = null;
   }

   protected void configureScrollBarColors() {
      super.configureScrollBarColors();
      Color var1 = UIManager.getColor("ScrollBar.trackForeground");
      if (var1 != null && this.trackColor != null) {
         this.thumbGrid = WindowsScrollBarUI.Grid.getGrid(var1, this.trackColor);
      }

      var1 = UIManager.getColor("ScrollBar.trackHighlightForeground");
      if (var1 != null && this.trackHighlightColor != null) {
         this.highlightGrid = WindowsScrollBarUI.Grid.getGrid(var1, this.trackHighlightColor);
      }

   }

   protected JButton createDecreaseButton(int var1) {
      return new WindowsScrollBarUI.WindowsArrowButton(var1, UIManager.getColor("ScrollBar.thumb"), UIManager.getColor("ScrollBar.thumbShadow"), UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight"));
   }

   protected JButton createIncreaseButton(int var1) {
      return new WindowsScrollBarUI.WindowsArrowButton(var1, UIManager.getColor("ScrollBar.thumb"), UIManager.getColor("ScrollBar.thumbShadow"), UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight"));
   }

   protected BasicScrollBarUI.ArrowButtonListener createArrowButtonListener() {
      return XPStyle.isVista() ? new BasicScrollBarUI.ArrowButtonListener() {
         public void mouseEntered(MouseEvent var1) {
            this.repaint();
            super.mouseEntered(var1);
         }

         public void mouseExited(MouseEvent var1) {
            this.repaint();
            super.mouseExited(var1);
         }

         private void repaint() {
            WindowsScrollBarUI.this.scrollbar.repaint();
         }
      } : super.createArrowButtonListener();
   }

   protected void paintTrack(Graphics var1, JComponent var2, Rectangle var3) {
      boolean var4 = this.scrollbar.getOrientation() == 1;
      XPStyle var5 = XPStyle.getXP();
      if (var5 != null) {
         JScrollBar var6 = (JScrollBar)var2;
         TMSchema.State var7 = TMSchema.State.NORMAL;
         if (!var6.isEnabled()) {
            var7 = TMSchema.State.DISABLED;
         }

         TMSchema.Part var8 = var4 ? TMSchema.Part.SBP_LOWERTRACKVERT : TMSchema.Part.SBP_LOWERTRACKHORZ;
         var5.getSkin(var6, var8).paintSkin(var1, var3, var7);
      } else if (this.thumbGrid == null) {
         super.paintTrack(var1, var2, var3);
      } else {
         this.thumbGrid.paint(var1, var3.x, var3.y, var3.width, var3.height);
         if (this.trackHighlight == 1) {
            this.paintDecreaseHighlight(var1);
         } else if (this.trackHighlight == 2) {
            this.paintIncreaseHighlight(var1);
         }
      }

   }

   protected void paintThumb(Graphics var1, JComponent var2, Rectangle var3) {
      boolean var4 = this.scrollbar.getOrientation() == 1;
      XPStyle var5 = XPStyle.getXP();
      if (var5 != null) {
         JScrollBar var6 = (JScrollBar)var2;
         TMSchema.State var7 = TMSchema.State.NORMAL;
         if (!var6.isEnabled()) {
            var7 = TMSchema.State.DISABLED;
         } else if (this.isDragging) {
            var7 = TMSchema.State.PRESSED;
         } else if (this.isThumbRollover()) {
            var7 = TMSchema.State.HOT;
         } else if (XPStyle.isVista() && (this.incrButton != null && this.incrButton.getModel().isRollover() || this.decrButton != null && this.decrButton.getModel().isRollover())) {
            var7 = TMSchema.State.HOVER;
         }

         TMSchema.Part var8 = var4 ? TMSchema.Part.SBP_THUMBBTNVERT : TMSchema.Part.SBP_THUMBBTNHORZ;
         var5.getSkin(var6, var8).paintSkin(var1, var3, var7);
         TMSchema.Part var9 = var4 ? TMSchema.Part.SBP_GRIPPERVERT : TMSchema.Part.SBP_GRIPPERHORZ;
         XPStyle.Skin var10 = var5.getSkin(var6, var9);
         Insets var11 = var5.getMargin(var2, var8, (TMSchema.State)null, TMSchema.Prop.CONTENTMARGINS);
         if (var11 == null || var4 && var3.height - var11.top - var11.bottom >= var10.getHeight() || !var4 && var3.width - var11.left - var11.right >= var10.getWidth()) {
            var10.paintSkin(var1, var3.x + (var3.width - var10.getWidth()) / 2, var3.y + (var3.height - var10.getHeight()) / 2, var10.getWidth(), var10.getHeight(), var7);
         }
      } else {
         super.paintThumb(var1, var2, var3);
      }

   }

   protected void paintDecreaseHighlight(Graphics var1) {
      if (this.highlightGrid == null) {
         super.paintDecreaseHighlight(var1);
      } else {
         Insets var2 = this.scrollbar.getInsets();
         Rectangle var3 = this.getThumbBounds();
         int var4;
         int var5;
         int var6;
         int var7;
         if (this.scrollbar.getOrientation() == 1) {
            var4 = var2.left;
            var5 = this.decrButton.getY() + this.decrButton.getHeight();
            var6 = this.scrollbar.getWidth() - (var2.left + var2.right);
            var7 = var3.y - var5;
         } else {
            var4 = this.decrButton.getX() + this.decrButton.getHeight();
            var5 = var2.top;
            var6 = var3.x - var4;
            var7 = this.scrollbar.getHeight() - (var2.top + var2.bottom);
         }

         this.highlightGrid.paint(var1, var4, var5, var6, var7);
      }

   }

   protected void paintIncreaseHighlight(Graphics var1) {
      if (this.highlightGrid == null) {
         super.paintDecreaseHighlight(var1);
      } else {
         Insets var2 = this.scrollbar.getInsets();
         Rectangle var3 = this.getThumbBounds();
         int var4;
         int var5;
         int var6;
         int var7;
         if (this.scrollbar.getOrientation() == 1) {
            var4 = var2.left;
            var5 = var3.y + var3.height;
            var6 = this.scrollbar.getWidth() - (var2.left + var2.right);
            var7 = this.incrButton.getY() - var5;
         } else {
            var4 = var3.x + var3.width;
            var5 = var2.top;
            var6 = this.incrButton.getX() - var4;
            var7 = this.scrollbar.getHeight() - (var2.top + var2.bottom);
         }

         this.highlightGrid.paint(var1, var4, var5, var6, var7);
      }

   }

   protected void setThumbRollover(boolean var1) {
      boolean var2 = this.isThumbRollover();
      super.setThumbRollover(var1);
      if (XPStyle.isVista() && var1 != var2) {
         this.scrollbar.repaint();
      }

   }

   private static class Grid {
      private static final int BUFFER_SIZE = 64;
      private static HashMap<String, WeakReference<WindowsScrollBarUI.Grid>> map = new HashMap();
      private BufferedImage image;

      public static WindowsScrollBarUI.Grid getGrid(Color var0, Color var1) {
         String var2 = var0.getRGB() + " " + var1.getRGB();
         WeakReference var3 = (WeakReference)map.get(var2);
         WindowsScrollBarUI.Grid var4 = var3 == null ? null : (WindowsScrollBarUI.Grid)var3.get();
         if (var4 == null) {
            var4 = new WindowsScrollBarUI.Grid(var0, var1);
            map.put(var2, new WeakReference(var4));
         }

         return var4;
      }

      public Grid(Color var1, Color var2) {
         int[] var3 = new int[]{var1.getRGB(), var2.getRGB()};
         IndexColorModel var4 = new IndexColorModel(8, 2, var3, 0, false, -1, 0);
         this.image = new BufferedImage(64, 64, 13, var4);
         Graphics var5 = this.image.getGraphics();

         try {
            var5.setClip(0, 0, 64, 64);
            this.paintGrid(var5, var1, var2);
         } finally {
            var5.dispose();
         }

      }

      public void paint(Graphics var1, int var2, int var3, int var4, int var5) {
         Rectangle var6 = var1.getClipBounds();
         int var7 = Math.max(var2, var6.x);
         int var8 = Math.max(var3, var6.y);
         int var9 = Math.min(var6.x + var6.width, var2 + var4);
         int var10 = Math.min(var6.y + var6.height, var3 + var5);
         if (var9 > var7 && var10 > var8) {
            int var11 = (var7 - var2) % 2;

            for(int var12 = var7; var12 < var9; var12 += 64) {
               int var13 = (var8 - var3) % 2;
               int var14 = Math.min(64 - var11, var9 - var12);

               for(int var15 = var8; var15 < var10; var15 += 64) {
                  int var16 = Math.min(64 - var13, var10 - var15);
                  var1.drawImage(this.image, var12, var15, var12 + var14, var15 + var16, var11, var13, var11 + var14, var13 + var16, (ImageObserver)null);
                  if (var13 != 0) {
                     var15 -= var13;
                     var13 = 0;
                  }
               }

               if (var11 != 0) {
                  var12 -= var11;
                  var11 = 0;
               }
            }

         }
      }

      private void paintGrid(Graphics var1, Color var2, Color var3) {
         Rectangle var4 = var1.getClipBounds();
         var1.setColor(var3);
         var1.fillRect(var4.x, var4.y, var4.width, var4.height);
         var1.setColor(var2);
         var1.translate(var4.x, var4.y);
         int var5 = var4.width;
         int var6 = var4.height;
         int var7 = var4.x % 2;

         int var8;
         for(var8 = var5 - var6; var7 < var8; var7 += 2) {
            var1.drawLine(var7, 0, var7 + var6, var6);
         }

         for(var8 = var5; var7 < var8; var7 += 2) {
            var1.drawLine(var7, 0, var5, var5 - var7);
         }

         var8 = var4.x % 2 == 0 ? 2 : 1;

         int var9;
         for(var9 = var6 - var5; var8 < var9; var8 += 2) {
            var1.drawLine(0, var8, var5, var8 + var5);
         }

         for(var9 = var6; var8 < var9; var8 += 2) {
            var1.drawLine(0, var8, var6 - var8, var6);
         }

         var1.translate(-var4.x, -var4.y);
      }
   }

   private class WindowsArrowButton extends BasicArrowButton {
      public WindowsArrowButton(int var2, Color var3, Color var4, Color var5, Color var6) {
         super(var2, var3, var4, var5, var6);
      }

      public WindowsArrowButton(int var2) {
         super(var2);
      }

      public void paint(Graphics var1) {
         XPStyle var2 = XPStyle.getXP();
         if (var2 != null) {
            ButtonModel var3 = this.getModel();
            XPStyle.Skin var4 = var2.getSkin(this, TMSchema.Part.SBP_ARROWBTN);
            TMSchema.State var5 = null;
            boolean var6 = XPStyle.isVista() && (WindowsScrollBarUI.this.isThumbRollover() || this == WindowsScrollBarUI.this.incrButton && WindowsScrollBarUI.this.decrButton.getModel().isRollover() || this == WindowsScrollBarUI.this.decrButton && WindowsScrollBarUI.this.incrButton.getModel().isRollover());
            if (var3.isArmed() && var3.isPressed()) {
               switch(this.direction) {
               case 1:
                  var5 = TMSchema.State.UPPRESSED;
               case 2:
               case 4:
               case 6:
               default:
                  break;
               case 3:
                  var5 = TMSchema.State.RIGHTPRESSED;
                  break;
               case 5:
                  var5 = TMSchema.State.DOWNPRESSED;
                  break;
               case 7:
                  var5 = TMSchema.State.LEFTPRESSED;
               }
            } else if (!var3.isEnabled()) {
               switch(this.direction) {
               case 1:
                  var5 = TMSchema.State.UPDISABLED;
               case 2:
               case 4:
               case 6:
               default:
                  break;
               case 3:
                  var5 = TMSchema.State.RIGHTDISABLED;
                  break;
               case 5:
                  var5 = TMSchema.State.DOWNDISABLED;
                  break;
               case 7:
                  var5 = TMSchema.State.LEFTDISABLED;
               }
            } else if (!var3.isRollover() && !var3.isPressed()) {
               if (var6) {
                  switch(this.direction) {
                  case 1:
                     var5 = TMSchema.State.UPHOVER;
                  case 2:
                  case 4:
                  case 6:
                  default:
                     break;
                  case 3:
                     var5 = TMSchema.State.RIGHTHOVER;
                     break;
                  case 5:
                     var5 = TMSchema.State.DOWNHOVER;
                     break;
                  case 7:
                     var5 = TMSchema.State.LEFTHOVER;
                  }
               } else {
                  switch(this.direction) {
                  case 1:
                     var5 = TMSchema.State.UPNORMAL;
                  case 2:
                  case 4:
                  case 6:
                  default:
                     break;
                  case 3:
                     var5 = TMSchema.State.RIGHTNORMAL;
                     break;
                  case 5:
                     var5 = TMSchema.State.DOWNNORMAL;
                     break;
                  case 7:
                     var5 = TMSchema.State.LEFTNORMAL;
                  }
               }
            } else {
               switch(this.direction) {
               case 1:
                  var5 = TMSchema.State.UPHOT;
               case 2:
               case 4:
               case 6:
               default:
                  break;
               case 3:
                  var5 = TMSchema.State.RIGHTHOT;
                  break;
               case 5:
                  var5 = TMSchema.State.DOWNHOT;
                  break;
               case 7:
                  var5 = TMSchema.State.LEFTHOT;
               }
            }

            var4.paintSkin(var1, 0, 0, this.getWidth(), this.getHeight(), var5);
         } else {
            super.paint(var1);
         }

      }

      public Dimension getPreferredSize() {
         int var1 = 16;
         if (WindowsScrollBarUI.this.scrollbar != null) {
            switch(WindowsScrollBarUI.this.scrollbar.getOrientation()) {
            case 0:
               var1 = WindowsScrollBarUI.this.scrollbar.getHeight();
               break;
            case 1:
               var1 = WindowsScrollBarUI.this.scrollbar.getWidth();
            }

            var1 = Math.max(var1, 5);
         }

         return new Dimension(var1, var1);
      }
   }
}
