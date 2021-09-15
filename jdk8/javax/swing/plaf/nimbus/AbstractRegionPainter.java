package javax.swing.plaf.nimbus;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.awt.print.PrinterGraphics;
import java.lang.reflect.Method;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.Painter;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import sun.reflect.misc.MethodUtil;

public abstract class AbstractRegionPainter implements Painter<JComponent> {
   private AbstractRegionPainter.PaintContext ctx;
   private float f;
   private float leftWidth;
   private float topHeight;
   private float centerWidth;
   private float centerHeight;
   private float rightWidth;
   private float bottomHeight;
   private float leftScale;
   private float topScale;
   private float centerHScale;
   private float centerVScale;
   private float rightScale;
   private float bottomScale;

   protected AbstractRegionPainter() {
   }

   public final void paint(Graphics2D var1, JComponent var2, int var3, int var4) {
      if (var3 > 0 && var4 > 0) {
         Object[] var5 = this.getExtendedCacheKeys(var2);
         this.ctx = this.getPaintContext();
         AbstractRegionPainter.PaintContext.CacheMode var6 = this.ctx == null ? AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING : this.ctx.cacheMode;
         if (var6 != AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING && ImageCache.getInstance().isImageCachable(var3, var4) && !(var1 instanceof PrinterGraphics)) {
            if (var6 == AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES) {
               this.paintWithFixedSizeCaching(var1, var2, var3, var4, var5);
            } else {
               this.paintWith9SquareCaching(var1, this.ctx, var2, var3, var4, var5);
            }
         } else {
            this.paint0(var1, var2, var3, var4, var5);
         }

      }
   }

   protected Object[] getExtendedCacheKeys(JComponent var1) {
      return null;
   }

   protected abstract AbstractRegionPainter.PaintContext getPaintContext();

   protected void configureGraphics(Graphics2D var1) {
      var1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
   }

   protected abstract void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5);

   protected final float decodeX(float var1) {
      if (var1 >= 0.0F && var1 <= 1.0F) {
         return var1 * this.leftWidth;
      } else if (var1 > 1.0F && var1 < 2.0F) {
         return (var1 - 1.0F) * this.centerWidth + this.leftWidth;
      } else if (var1 >= 2.0F && var1 <= 3.0F) {
         return (var1 - 2.0F) * this.rightWidth + this.leftWidth + this.centerWidth;
      } else {
         throw new IllegalArgumentException("Invalid x");
      }
   }

   protected final float decodeY(float var1) {
      if (var1 >= 0.0F && var1 <= 1.0F) {
         return var1 * this.topHeight;
      } else if (var1 > 1.0F && var1 < 2.0F) {
         return (var1 - 1.0F) * this.centerHeight + this.topHeight;
      } else if (var1 >= 2.0F && var1 <= 3.0F) {
         return (var1 - 2.0F) * this.bottomHeight + this.topHeight + this.centerHeight;
      } else {
         throw new IllegalArgumentException("Invalid y");
      }
   }

   protected final float decodeAnchorX(float var1, float var2) {
      if (var1 >= 0.0F && var1 <= 1.0F) {
         return this.decodeX(var1) + var2 * this.leftScale;
      } else if (var1 > 1.0F && var1 < 2.0F) {
         return this.decodeX(var1) + var2 * this.centerHScale;
      } else if (var1 >= 2.0F && var1 <= 3.0F) {
         return this.decodeX(var1) + var2 * this.rightScale;
      } else {
         throw new IllegalArgumentException("Invalid x");
      }
   }

   protected final float decodeAnchorY(float var1, float var2) {
      if (var1 >= 0.0F && var1 <= 1.0F) {
         return this.decodeY(var1) + var2 * this.topScale;
      } else if (var1 > 1.0F && var1 < 2.0F) {
         return this.decodeY(var1) + var2 * this.centerVScale;
      } else if (var1 >= 2.0F && var1 <= 3.0F) {
         return this.decodeY(var1) + var2 * this.bottomScale;
      } else {
         throw new IllegalArgumentException("Invalid y");
      }
   }

   protected final Color decodeColor(String var1, float var2, float var3, float var4, int var5) {
      if (UIManager.getLookAndFeel() instanceof NimbusLookAndFeel) {
         NimbusLookAndFeel var6 = (NimbusLookAndFeel)UIManager.getLookAndFeel();
         return var6.getDerivedColor(var1, var2, var3, var4, var5, true);
      } else {
         return Color.getHSBColor(var2, var3, var4);
      }
   }

   protected final Color decodeColor(Color var1, Color var2, float var3) {
      return new Color(NimbusLookAndFeel.deriveARGB(var1, var2, var3));
   }

   protected final LinearGradientPaint decodeGradient(float var1, float var2, float var3, float var4, float[] var5, Color[] var6) {
      if (var1 == var3 && var2 == var4) {
         var4 += 1.0E-5F;
      }

      return new LinearGradientPaint(var1, var2, var3, var4, var5, var6);
   }

   protected final RadialGradientPaint decodeRadialGradient(float var1, float var2, float var3, float[] var4, Color[] var5) {
      if (var3 == 0.0F) {
         var3 = 1.0E-5F;
      }

      return new RadialGradientPaint(var1, var2, var3, var4, var5);
   }

   protected final Color getComponentColor(JComponent var1, String var2, Color var3, float var4, float var5, int var6) {
      Color var7 = null;
      if (var1 != null) {
         if ("background".equals(var2)) {
            var7 = var1.getBackground();
         } else if ("foreground".equals(var2)) {
            var7 = var1.getForeground();
         } else if (var1 instanceof JList && "selectionForeground".equals(var2)) {
            var7 = ((JList)var1).getSelectionForeground();
         } else if (var1 instanceof JList && "selectionBackground".equals(var2)) {
            var7 = ((JList)var1).getSelectionBackground();
         } else if (var1 instanceof JTable && "selectionForeground".equals(var2)) {
            var7 = ((JTable)var1).getSelectionForeground();
         } else if (var1 instanceof JTable && "selectionBackground".equals(var2)) {
            var7 = ((JTable)var1).getSelectionBackground();
         } else {
            String var8 = "get" + Character.toUpperCase(var2.charAt(0)) + var2.substring(1);

            try {
               Method var9 = MethodUtil.getMethod(var1.getClass(), var8, (Class[])null);
               var7 = (Color)MethodUtil.invoke(var9, var1, (Object[])null);
            } catch (Exception var10) {
            }

            if (var7 == null) {
               Object var12 = var1.getClientProperty(var2);
               if (var12 instanceof Color) {
                  var7 = (Color)var12;
               }
            }
         }
      }

      if (var7 != null && !(var7 instanceof UIResource)) {
         if (var4 == 0.0F && var5 == 0.0F && var6 == 0) {
            return var7;
         } else {
            float[] var11 = Color.RGBtoHSB(var7.getRed(), var7.getGreen(), var7.getBlue(), (float[])null);
            var11[1] = this.clamp(var11[1] + var4);
            var11[2] = this.clamp(var11[2] + var5);
            int var13 = this.clamp(var7.getAlpha() + var6);
            return new Color(Color.HSBtoRGB(var11[0], var11[1], var11[2]) & 16777215 | var13 << 24);
         }
      } else {
         return var3;
      }
   }

   private void prepare(float var1, float var2) {
      if (this.ctx != null && this.ctx.canvasSize != null) {
         Number var3 = (Number)UIManager.get("scale");
         this.f = var3 == null ? 1.0F : var3.floatValue();
         if (this.ctx.inverted) {
            this.centerWidth = (this.ctx.b - this.ctx.a) * this.f;
            float var4 = var1 - this.centerWidth;
            this.leftWidth = var4 * this.ctx.aPercent;
            this.rightWidth = var4 * this.ctx.bPercent;
            this.centerHeight = (this.ctx.d - this.ctx.c) * this.f;
            var4 = var2 - this.centerHeight;
            this.topHeight = var4 * this.ctx.cPercent;
            this.bottomHeight = var4 * this.ctx.dPercent;
         } else {
            this.leftWidth = this.ctx.a * this.f;
            this.rightWidth = (float)(this.ctx.canvasSize.getWidth() - (double)this.ctx.b) * this.f;
            this.centerWidth = var1 - this.leftWidth - this.rightWidth;
            this.topHeight = this.ctx.c * this.f;
            this.bottomHeight = (float)(this.ctx.canvasSize.getHeight() - (double)this.ctx.d) * this.f;
            this.centerHeight = var2 - this.topHeight - this.bottomHeight;
         }

         this.leftScale = this.ctx.a == 0.0F ? 0.0F : this.leftWidth / this.ctx.a;
         this.centerHScale = this.ctx.b - this.ctx.a == 0.0F ? 0.0F : this.centerWidth / (this.ctx.b - this.ctx.a);
         this.rightScale = (float)this.ctx.canvasSize.width - this.ctx.b == 0.0F ? 0.0F : this.rightWidth / ((float)this.ctx.canvasSize.width - this.ctx.b);
         this.topScale = this.ctx.c == 0.0F ? 0.0F : this.topHeight / this.ctx.c;
         this.centerVScale = this.ctx.d - this.ctx.c == 0.0F ? 0.0F : this.centerHeight / (this.ctx.d - this.ctx.c);
         this.bottomScale = (float)this.ctx.canvasSize.height - this.ctx.d == 0.0F ? 0.0F : this.bottomHeight / ((float)this.ctx.canvasSize.height - this.ctx.d);
      } else {
         this.f = 1.0F;
         this.leftWidth = this.centerWidth = this.rightWidth = 0.0F;
         this.topHeight = this.centerHeight = this.bottomHeight = 0.0F;
         this.leftScale = this.centerHScale = this.rightScale = 0.0F;
         this.topScale = this.centerVScale = this.bottomScale = 0.0F;
      }
   }

   private void paintWith9SquareCaching(Graphics2D var1, AbstractRegionPainter.PaintContext var2, JComponent var3, int var4, int var5, Object[] var6) {
      Dimension var7 = var2.canvasSize;
      Insets var8 = var2.stretchingInsets;
      if ((double)var4 <= (double)var7.width * var2.maxHorizontalScaleFactor && (double)var5 <= (double)var7.height * var2.maxVerticalScaleFactor) {
         VolatileImage var9 = this.getImage(var1.getDeviceConfiguration(), var3, var7.width, var7.height, var6);
         if (var9 != null) {
            Insets var10;
            if (var2.inverted) {
               int var11 = (var4 - (var7.width - (var8.left + var8.right))) / 2;
               int var12 = (var5 - (var7.height - (var8.top + var8.bottom))) / 2;
               var10 = new Insets(var12, var11, var12, var11);
            } else {
               var10 = var8;
            }

            Object var13 = var1.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
            var1.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            ImageScalingHelper.paint(var1, 0, 0, var4, var5, var9, var8, var10, ImageScalingHelper.PaintType.PAINT9_STRETCH, 512);
            var1.setRenderingHint(RenderingHints.KEY_INTERPOLATION, var13 != null ? var13 : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
         } else {
            this.paint0(var1, var3, var4, var5, var6);
         }
      } else {
         this.paint0(var1, var3, var4, var5, var6);
      }

   }

   private void paintWithFixedSizeCaching(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      VolatileImage var6 = this.getImage(var1.getDeviceConfiguration(), var2, var3, var4, var5);
      if (var6 != null) {
         var1.drawImage(var6, 0, 0, (ImageObserver)null);
      } else {
         this.paint0(var1, var2, var3, var4, var5);
      }

   }

   private VolatileImage getImage(GraphicsConfiguration var1, JComponent var2, int var3, int var4, Object[] var5) {
      ImageCache var6 = ImageCache.getInstance();
      VolatileImage var7 = (VolatileImage)var6.getImage(var1, var3, var4, this, var5);
      int var8 = 0;

      do {
         int var9 = 2;
         if (var7 != null) {
            var9 = var7.validate(var1);
         }

         if (var9 == 2 || var9 == 1) {
            if (var7 == null || var7.getWidth() != var3 || var7.getHeight() != var4 || var9 == 2) {
               if (var7 != null) {
                  var7.flush();
                  var7 = null;
               }

               var7 = var1.createCompatibleVolatileImage(var3, var4, 3);
               var6.setImage(var7, var1, var3, var4, this, var5);
            }

            Graphics2D var10 = var7.createGraphics();
            var10.setComposite(AlphaComposite.Clear);
            var10.fillRect(0, 0, var3, var4);
            var10.setComposite(AlphaComposite.SrcOver);
            this.configureGraphics(var10);
            this.paint0(var10, var2, var3, var4, var5);
            var10.dispose();
         }
      } while(var7.contentsLost() && var8++ < 3);

      return var8 == 3 ? null : var7;
   }

   private void paint0(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.prepare((float)var3, (float)var4);
      var1 = (Graphics2D)var1.create();
      this.configureGraphics(var1);
      this.doPaint(var1, var2, var3, var4, var5);
      var1.dispose();
   }

   private float clamp(float var1) {
      if (var1 < 0.0F) {
         var1 = 0.0F;
      } else if (var1 > 1.0F) {
         var1 = 1.0F;
      }

      return var1;
   }

   private int clamp(int var1) {
      if (var1 < 0) {
         var1 = 0;
      } else if (var1 > 255) {
         var1 = 255;
      }

      return var1;
   }

   protected static class PaintContext {
      private static Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
      private Insets stretchingInsets;
      private Dimension canvasSize;
      private boolean inverted;
      private AbstractRegionPainter.PaintContext.CacheMode cacheMode;
      private double maxHorizontalScaleFactor;
      private double maxVerticalScaleFactor;
      private float a;
      private float b;
      private float c;
      private float d;
      private float aPercent;
      private float bPercent;
      private float cPercent;
      private float dPercent;

      public PaintContext(Insets var1, Dimension var2, boolean var3) {
         this(var1, var2, var3, (AbstractRegionPainter.PaintContext.CacheMode)null, 1.0D, 1.0D);
      }

      public PaintContext(Insets var1, Dimension var2, boolean var3, AbstractRegionPainter.PaintContext.CacheMode var4, double var5, double var7) {
         if (var5 >= 1.0D && var5 >= 1.0D) {
            this.stretchingInsets = var1 == null ? EMPTY_INSETS : var1;
            this.canvasSize = var2;
            this.inverted = var3;
            this.cacheMode = var4 == null ? AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING : var4;
            this.maxHorizontalScaleFactor = var5;
            this.maxVerticalScaleFactor = var7;
            if (var2 != null) {
               this.a = (float)this.stretchingInsets.left;
               this.b = (float)(var2.width - this.stretchingInsets.right);
               this.c = (float)this.stretchingInsets.top;
               this.d = (float)(var2.height - this.stretchingInsets.bottom);
               this.canvasSize = var2;
               this.inverted = var3;
               if (var3) {
                  float var9 = (float)var2.width - (this.b - this.a);
                  this.aPercent = var9 > 0.0F ? this.a / var9 : 0.0F;
                  this.bPercent = var9 > 0.0F ? this.b / var9 : 0.0F;
                  var9 = (float)var2.height - (this.d - this.c);
                  this.cPercent = var9 > 0.0F ? this.c / var9 : 0.0F;
                  this.dPercent = var9 > 0.0F ? this.d / var9 : 0.0F;
               }
            }

         } else {
            throw new IllegalArgumentException("Both maxH and maxV must be >= 1");
         }
      }

      protected static enum CacheMode {
         NO_CACHING,
         FIXED_SIZES,
         NINE_SQUARE_SCALE;
      }
   }
}
