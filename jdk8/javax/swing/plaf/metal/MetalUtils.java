package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import sun.swing.CachedPainter;
import sun.swing.ImageIconUIResource;

class MetalUtils {
   static void drawFlush3DBorder(Graphics var0, Rectangle var1) {
      drawFlush3DBorder(var0, var1.x, var1.y, var1.width, var1.height);
   }

   static void drawFlush3DBorder(Graphics var0, int var1, int var2, int var3, int var4) {
      var0.translate(var1, var2);
      var0.setColor(MetalLookAndFeel.getControlDarkShadow());
      var0.drawRect(0, 0, var3 - 2, var4 - 2);
      var0.setColor(MetalLookAndFeel.getControlHighlight());
      var0.drawRect(1, 1, var3 - 2, var4 - 2);
      var0.setColor(MetalLookAndFeel.getControl());
      var0.drawLine(0, var4 - 1, 1, var4 - 2);
      var0.drawLine(var3 - 1, 0, var3 - 2, 1);
      var0.translate(-var1, -var2);
   }

   static void drawPressed3DBorder(Graphics var0, Rectangle var1) {
      drawPressed3DBorder(var0, var1.x, var1.y, var1.width, var1.height);
   }

   static void drawDisabledBorder(Graphics var0, int var1, int var2, int var3, int var4) {
      var0.translate(var1, var2);
      var0.setColor(MetalLookAndFeel.getControlShadow());
      var0.drawRect(0, 0, var3 - 1, var4 - 1);
      var0.translate(-var1, -var2);
   }

   static void drawPressed3DBorder(Graphics var0, int var1, int var2, int var3, int var4) {
      var0.translate(var1, var2);
      drawFlush3DBorder(var0, 0, 0, var3, var4);
      var0.setColor(MetalLookAndFeel.getControlShadow());
      var0.drawLine(1, 1, 1, var4 - 2);
      var0.drawLine(1, 1, var3 - 2, 1);
      var0.translate(-var1, -var2);
   }

   static void drawDark3DBorder(Graphics var0, Rectangle var1) {
      drawDark3DBorder(var0, var1.x, var1.y, var1.width, var1.height);
   }

   static void drawDark3DBorder(Graphics var0, int var1, int var2, int var3, int var4) {
      var0.translate(var1, var2);
      drawFlush3DBorder(var0, 0, 0, var3, var4);
      var0.setColor(MetalLookAndFeel.getControl());
      var0.drawLine(1, 1, 1, var4 - 2);
      var0.drawLine(1, 1, var3 - 2, 1);
      var0.setColor(MetalLookAndFeel.getControlShadow());
      var0.drawLine(1, var4 - 2, 1, var4 - 2);
      var0.drawLine(var3 - 2, 1, var3 - 2, 1);
      var0.translate(-var1, -var2);
   }

   static void drawButtonBorder(Graphics var0, int var1, int var2, int var3, int var4, boolean var5) {
      if (var5) {
         drawActiveButtonBorder(var0, var1, var2, var3, var4);
      } else {
         drawFlush3DBorder(var0, var1, var2, var3, var4);
      }

   }

   static void drawActiveButtonBorder(Graphics var0, int var1, int var2, int var3, int var4) {
      drawFlush3DBorder(var0, var1, var2, var3, var4);
      var0.setColor(MetalLookAndFeel.getPrimaryControl());
      var0.drawLine(var1 + 1, var2 + 1, var1 + 1, var4 - 3);
      var0.drawLine(var1 + 1, var2 + 1, var3 - 3, var1 + 1);
      var0.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      var0.drawLine(var1 + 2, var4 - 2, var3 - 2, var4 - 2);
      var0.drawLine(var3 - 2, var2 + 2, var3 - 2, var4 - 2);
   }

   static void drawDefaultButtonBorder(Graphics var0, int var1, int var2, int var3, int var4, boolean var5) {
      drawButtonBorder(var0, var1 + 1, var2 + 1, var3 - 1, var4 - 1, var5);
      var0.translate(var1, var2);
      var0.setColor(MetalLookAndFeel.getControlDarkShadow());
      var0.drawRect(0, 0, var3 - 3, var4 - 3);
      var0.drawLine(var3 - 2, 0, var3 - 2, 0);
      var0.drawLine(0, var4 - 2, 0, var4 - 2);
      var0.translate(-var1, -var2);
   }

   static void drawDefaultButtonPressedBorder(Graphics var0, int var1, int var2, int var3, int var4) {
      drawPressed3DBorder(var0, var1 + 1, var2 + 1, var3 - 1, var4 - 1);
      var0.translate(var1, var2);
      var0.setColor(MetalLookAndFeel.getControlDarkShadow());
      var0.drawRect(0, 0, var3 - 3, var4 - 3);
      var0.drawLine(var3 - 2, 0, var3 - 2, 0);
      var0.drawLine(0, var4 - 2, 0, var4 - 2);
      var0.setColor(MetalLookAndFeel.getControl());
      var0.drawLine(var3 - 1, 0, var3 - 1, 0);
      var0.drawLine(0, var4 - 1, 0, var4 - 1);
      var0.translate(-var1, -var2);
   }

   static boolean isLeftToRight(Component var0) {
      return var0.getComponentOrientation().isLeftToRight();
   }

   static int getInt(Object var0, int var1) {
      Object var2 = UIManager.get(var0);
      if (var2 instanceof Integer) {
         return (Integer)var2;
      } else {
         if (var2 instanceof String) {
            try {
               return Integer.parseInt((String)var2);
            } catch (NumberFormatException var4) {
            }
         }

         return var1;
      }
   }

   static boolean drawGradient(Component var0, Graphics var1, String var2, int var3, int var4, int var5, int var6, boolean var7) {
      List var8 = (List)UIManager.get(var2);
      if (var8 != null && var1 instanceof Graphics2D) {
         if (var5 > 0 && var6 > 0) {
            MetalUtils.GradientPainter.INSTANCE.paint(var0, (Graphics2D)var1, var8, var3, var4, var5, var6, var7);
            return true;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   static boolean isToolBarButton(JComponent var0) {
      return var0.getParent() instanceof JToolBar;
   }

   static Icon getOceanToolBarIcon(Image var0) {
      FilteredImageSource var1 = new FilteredImageSource(var0.getSource(), new MetalUtils.OceanToolBarImageFilter());
      return new ImageIconUIResource(Toolkit.getDefaultToolkit().createImage((ImageProducer)var1));
   }

   static Icon getOceanDisabledButtonIcon(Image var0) {
      Object[] var1 = (Object[])((Object[])UIManager.get("Button.disabledGrayRange"));
      int var2 = 180;
      int var3 = 215;
      if (var1 != null) {
         var2 = (Integer)var1[0];
         var3 = (Integer)var1[1];
      }

      FilteredImageSource var4 = new FilteredImageSource(var0.getSource(), new MetalUtils.OceanDisabledButtonImageFilter(var2, var3));
      return new ImageIconUIResource(Toolkit.getDefaultToolkit().createImage((ImageProducer)var4));
   }

   private static class OceanToolBarImageFilter extends RGBImageFilter {
      OceanToolBarImageFilter() {
         this.canFilterIndexColorModel = true;
      }

      public int filterRGB(int var1, int var2, int var3) {
         int var4 = var3 >> 16 & 255;
         int var5 = var3 >> 8 & 255;
         int var6 = var3 & 255;
         int var7 = Math.max(Math.max(var4, var5), var6);
         return var3 & -16777216 | var7 << 16 | var7 << 8 | var7 << 0;
      }
   }

   private static class OceanDisabledButtonImageFilter extends RGBImageFilter {
      private float min;
      private float factor;

      OceanDisabledButtonImageFilter(int var1, int var2) {
         this.canFilterIndexColorModel = true;
         this.min = (float)var1;
         this.factor = (float)(var2 - var1) / 255.0F;
      }

      public int filterRGB(int var1, int var2, int var3) {
         int var4 = Math.min(255, (int)((0.2125F * (float)(var3 >> 16 & 255) + 0.7154F * (float)(var3 >> 8 & 255) + 0.0721F * (float)(var3 & 255) + 0.5F) * this.factor + this.min));
         return var3 & -16777216 | var4 << 16 | var4 << 8 | var4 << 0;
      }
   }

   private static class GradientPainter extends CachedPainter {
      public static final MetalUtils.GradientPainter INSTANCE = new MetalUtils.GradientPainter(8);
      private static final int IMAGE_SIZE = 64;
      private int w;
      private int h;

      GradientPainter(int var1) {
         super(var1);
      }

      public void paint(Component var1, Graphics2D var2, List var3, int var4, int var5, int var6, int var7, boolean var8) {
         int var9;
         int var10;
         if (var8) {
            var9 = 64;
            var10 = var7;
         } else {
            var9 = var6;
            var10 = 64;
         }

         synchronized(var1.getTreeLock()) {
            this.w = var6;
            this.h = var7;
            this.paint(var1, var2, var4, var5, var9, var10, new Object[]{var3, var8});
         }
      }

      protected void paintToImage(Component var1, Image var2, Graphics var3, int var4, int var5, Object[] var6) {
         Graphics2D var7 = (Graphics2D)var3;
         List var8 = (List)var6[0];
         boolean var9 = (Boolean)var6[1];
         if (var9) {
            this.drawVerticalGradient(var7, ((Number)var8.get(0)).floatValue(), ((Number)var8.get(1)).floatValue(), (Color)var8.get(2), (Color)var8.get(3), (Color)var8.get(4), var4, var5);
         } else {
            this.drawHorizontalGradient(var7, ((Number)var8.get(0)).floatValue(), ((Number)var8.get(1)).floatValue(), (Color)var8.get(2), (Color)var8.get(3), (Color)var8.get(4), var4, var5);
         }

      }

      protected void paintImage(Component var1, Graphics var2, int var3, int var4, int var5, int var6, Image var7, Object[] var8) {
         boolean var9 = (Boolean)var8[1];
         var2.translate(var3, var4);
         int var10;
         int var11;
         if (var9) {
            for(var10 = 0; var10 < this.w; var10 += 64) {
               var11 = Math.min(64, this.w - var10);
               var2.drawImage(var7, var10, 0, var10 + var11, this.h, 0, 0, var11, this.h, (ImageObserver)null);
            }
         } else {
            for(var10 = 0; var10 < this.h; var10 += 64) {
               var11 = Math.min(64, this.h - var10);
               var2.drawImage(var7, 0, var10, this.w, var10 + var11, 0, 0, this.w, var11, (ImageObserver)null);
            }
         }

         var2.translate(-var3, -var4);
      }

      private void drawVerticalGradient(Graphics2D var1, float var2, float var3, Color var4, Color var5, Color var6, int var7, int var8) {
         int var9 = (int)(var2 * (float)var8);
         int var10 = (int)(var3 * (float)var8);
         if (var9 > 0) {
            var1.setPaint(this.getGradient(0.0F, 0.0F, var4, 0.0F, (float)var9, var5));
            var1.fillRect(0, 0, var7, var9);
         }

         if (var10 > 0) {
            var1.setColor(var5);
            var1.fillRect(0, var9, var7, var10);
         }

         if (var9 > 0) {
            var1.setPaint(this.getGradient(0.0F, (float)var9 + (float)var10, var5, 0.0F, (float)var9 * 2.0F + (float)var10, var4));
            var1.fillRect(0, var9 + var10, var7, var9);
         }

         if (var8 - var9 * 2 - var10 > 0) {
            var1.setPaint(this.getGradient(0.0F, (float)var9 * 2.0F + (float)var10, var4, 0.0F, (float)var8, var6));
            var1.fillRect(0, var9 * 2 + var10, var7, var8 - var9 * 2 - var10);
         }

      }

      private void drawHorizontalGradient(Graphics2D var1, float var2, float var3, Color var4, Color var5, Color var6, int var7, int var8) {
         int var9 = (int)(var2 * (float)var7);
         int var10 = (int)(var3 * (float)var7);
         if (var9 > 0) {
            var1.setPaint(this.getGradient(0.0F, 0.0F, var4, (float)var9, 0.0F, var5));
            var1.fillRect(0, 0, var9, var8);
         }

         if (var10 > 0) {
            var1.setColor(var5);
            var1.fillRect(var9, 0, var10, var8);
         }

         if (var9 > 0) {
            var1.setPaint(this.getGradient((float)var9 + (float)var10, 0.0F, var5, (float)var9 * 2.0F + (float)var10, 0.0F, var4));
            var1.fillRect(var9 + var10, 0, var9, var8);
         }

         if (var7 - var9 * 2 - var10 > 0) {
            var1.setPaint(this.getGradient((float)var9 * 2.0F + (float)var10, 0.0F, var4, (float)var7, 0.0F, var6));
            var1.fillRect(var9 * 2 + var10, 0, var7 - var9 * 2 - var10, var8);
         }

      }

      private GradientPaint getGradient(float var1, float var2, Color var3, float var4, float var5, Color var6) {
         return new GradientPaint(var1, var2, var3, var4, var5, var6, true);
      }
   }
}
