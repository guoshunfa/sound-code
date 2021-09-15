package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIControl;
import apple.laf.JRSUIState;
import apple.laf.JRSUIUtils;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import sun.awt.image.ImageCache;
import sun.awt.image.MultiResolutionCachedImage;
import sun.awt.image.SunWritableRaster;
import sun.java2d.SunGraphics2D;
import sun.print.PeekGraphics;
import sun.print.ProxyGraphics2D;

abstract class AquaPainter<T extends JRSUIState> {
   final Rectangle boundsRect = new Rectangle();
   final JRSUIControl control;
   T state;

   static <T extends JRSUIState> AquaPainter<T> create(T var0) {
      return new AquaPainter.AquaSingleImagePainter(var0);
   }

   static <T extends JRSUIState> AquaPainter<T> create(T var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      return create(var0, var1, var2, var3, var4, var5, var6, true);
   }

   static <T extends JRSUIState> AquaPainter<T> create(T var0, int var1, int var2, int var3, int var4, int var5, int var6, boolean var7) {
      return create(var0, var1, var2, var3, var4, var5, var6, var7, true, true);
   }

   static <T extends JRSUIState> AquaPainter<T> create(T var0, final int var1, final int var2, final int var3, final int var4, final int var5, final int var6, final boolean var7, final boolean var8, final boolean var9) {
      return create(var0, new JRSUIUtils.NineSliceMetricsProvider() {
         public AquaImageFactory.NineSliceMetrics getNineSliceMetricsForState(JRSUIState var1x) {
            return new AquaImageFactory.NineSliceMetrics(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         }
      });
   }

   static <T extends JRSUIState> AquaPainter<T> create(T var0, JRSUIUtils.NineSliceMetricsProvider var1) {
      return new AquaPainter.AquaNineSlicingImagePainter(var0, var1);
   }

   abstract void paint(Graphics2D var1, T var2);

   AquaPainter(JRSUIControl var1, T var2) {
      this.control = var1;
      this.state = var2;
   }

   final JRSUIControl getControl() {
      this.control.set(this.state = this.state.derive());
      return this.control;
   }

   final void paint(Graphics var1, Component var2, int var3, int var4, int var5, int var6) {
      this.boundsRect.setBounds(var3, var4, var5, var6);
      JRSUIState var7 = this.state.derive();
      Graphics2D var8 = this.getGraphics2D(var1);
      if (var8 != null) {
         this.paint(var8, var7);
      }

      this.state = var7;
   }

   private Graphics2D getGraphics2D(Graphics var1) {
      try {
         return (SunGraphics2D)var1;
      } catch (Exception var5) {
         if (var1 instanceof PeekGraphics) {
            var1.fillRect(this.boundsRect.x, this.boundsRect.y, this.boundsRect.width, this.boundsRect.height);
         } else if (var1 instanceof ProxyGraphics2D) {
            ProxyGraphics2D var3 = (ProxyGraphics2D)var1;
            Graphics2D var4 = var3.getDelegate();
            if (var4 instanceof SunGraphics2D) {
               return var4;
            }
         } else if (var1 instanceof Graphics2D) {
            return (Graphics2D)var1;
         }

         return null;
      }
   }

   private static class RecyclableJRSUISlicedImageControl extends AquaImageFactory.RecyclableSlicedImageControl {
      private final JRSUIControl control;
      private final JRSUIState state;

      RecyclableJRSUISlicedImageControl(JRSUIControl var1, JRSUIState var2, AquaImageFactory.NineSliceMetrics var3) {
         super(var3);
         this.control = var1;
         this.state = var2;
      }

      protected Image createTemplateImage(int var1, int var2) {
         BufferedImage var3 = new BufferedImage(this.metrics.minW, this.metrics.minH, 3);
         WritableRaster var4 = var3.getRaster();
         DataBufferInt var5 = (DataBufferInt)var4.getDataBuffer();
         this.control.set(this.state);
         this.control.paint(SunWritableRaster.stealData((DataBufferInt)var5, 0), this.metrics.minW, this.metrics.minH, 0.0D, 0.0D, (double)this.metrics.minW, (double)this.metrics.minH);
         SunWritableRaster.markDirty((DataBuffer)var5);
         return var3;
      }
   }

   private static class AquaPixelsKey implements ImageCache.PixelsKey {
      private final int pixelCount;
      private final int hash;
      private final GraphicsConfiguration config;
      private final int w;
      private final int h;
      private final Rectangle bounds;
      private final JRSUIState state;

      AquaPixelsKey(GraphicsConfiguration var1, int var2, int var3, Rectangle var4, JRSUIState var5) {
         this.pixelCount = var2 * var3;
         this.config = var1;
         this.w = var2;
         this.h = var3;
         this.bounds = var4;
         this.state = var5;
         this.hash = this.hash();
      }

      public int getPixelCount() {
         return this.pixelCount;
      }

      private int hash() {
         int var1 = this.config != null ? this.config.hashCode() : 0;
         var1 = 31 * var1 + this.w;
         var1 = 31 * var1 + this.h;
         var1 = 31 * var1 + this.bounds.hashCode();
         var1 = 31 * var1 + this.state.hashCode();
         return var1;
      }

      public int hashCode() {
         return this.hash;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof AquaPainter.AquaPixelsKey)) {
            return false;
         } else {
            AquaPainter.AquaPixelsKey var2 = (AquaPainter.AquaPixelsKey)var1;
            return this.config == var2.config && this.w == var2.w && this.h == var2.h && this.bounds.equals(var2.bounds) && this.state.equals(var2.state);
         }
      }
   }

   private static final class AquaSingleImagePainter<T extends JRSUIState> extends AquaPainter<T> {
      AquaSingleImagePainter(T var1) {
         super(new JRSUIControl(false), var1);
      }

      void paint(Graphics2D var1, T var2) {
         paintFromSingleCachedImage(var1, this.control, var2, this.boundsRect);
      }

      static void paintFromSingleCachedImage(Graphics2D var0, JRSUIControl var1, JRSUIState var2, Rectangle var3) {
         if (var3.width > 0 && var3.height > 0) {
            byte var4 = 0;
            if (var2.is(JRSUIConstants.Focused.YES)) {
               var4 = 4;
            }

            int var5 = var3.x - var4;
            int var6 = var3.y - var4;
            int var7 = var3.width + (var4 << 1);
            int var8 = var3.height + (var4 << 1);
            GraphicsConfiguration var9 = var0.getDeviceConfiguration();
            ImageCache var10 = ImageCache.getInstance();
            AquaPainter.AquaPixelsKey var11 = new AquaPainter.AquaPixelsKey(var9, var7, var8, var3, var2);
            Object var12 = var10.getImage(var11);
            if (var12 == null) {
               var12 = new MultiResolutionCachedImage(var7, var8, (var5x, var6x) -> {
                  return createImage(var5, var6, var5x, var6x, var3, var1, var2);
               });
               if (!var2.is(JRSUIConstants.Animating.YES)) {
                  var10.setImage(var11, (Image)var12);
               }
            }

            var0.drawImage((Image)var12, var5, var6, var7, var8, (ImageObserver)null);
         }
      }

      private static Image createImage(int var0, int var1, int var2, int var3, Rectangle var4, JRSUIControl var5, JRSUIState var6) {
         BufferedImage var7 = new BufferedImage(var2, var3, 3);
         WritableRaster var8 = var7.getRaster();
         DataBufferInt var9 = (DataBufferInt)var8.getDataBuffer();
         var5.set(var6);
         var5.paint(SunWritableRaster.stealData((DataBufferInt)var9, 0), var2, var3, (double)(var4.x - var0), (double)(var4.y - var1), (double)var4.width, (double)var4.height);
         SunWritableRaster.markDirty((DataBuffer)var9);
         return var7;
      }
   }

   private static class AquaNineSlicingImagePainter<T extends JRSUIState> extends AquaPainter<T> {
      private final HashMap<T, AquaPainter.RecyclableJRSUISlicedImageControl> slicedControlImages;
      private final JRSUIUtils.NineSliceMetricsProvider metricsProvider;

      AquaNineSlicingImagePainter(T var1) {
         this(var1, (JRSUIUtils.NineSliceMetricsProvider)null);
      }

      AquaNineSlicingImagePainter(T var1, JRSUIUtils.NineSliceMetricsProvider var2) {
         super(new JRSUIControl(false), var1);
         this.metricsProvider = var2;
         this.slicedControlImages = new HashMap();
      }

      void paint(Graphics2D var1, T var2) {
         if (this.metricsProvider == null) {
            AquaPainter.AquaSingleImagePainter.paintFromSingleCachedImage(var1, this.control, var2, this.boundsRect);
         } else {
            AquaPainter.RecyclableJRSUISlicedImageControl var3 = (AquaPainter.RecyclableJRSUISlicedImageControl)this.slicedControlImages.get(var2);
            if (var3 == null) {
               AquaImageFactory.NineSliceMetrics var4 = this.metricsProvider.getNineSliceMetricsForState(var2);
               if (var4 == null) {
                  AquaPainter.AquaSingleImagePainter.paintFromSingleCachedImage(var1, this.control, var2, this.boundsRect);
                  return;
               }

               var3 = new AquaPainter.RecyclableJRSUISlicedImageControl(this.control, var2, var4);
               this.slicedControlImages.put(var2, var3);
            }

            AquaImageFactory.SlicedImageControl var5 = (AquaImageFactory.SlicedImageControl)var3.get();
            var5.paint(var1, this.boundsRect.x, this.boundsRect.y, this.boundsRect.width, this.boundsRect.height);
         }
      }
   }
}
