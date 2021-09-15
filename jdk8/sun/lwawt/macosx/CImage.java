package sun.lwawt.macosx;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import sun.awt.image.MultiResolutionCachedImage;
import sun.awt.image.MultiResolutionImage;
import sun.awt.image.SunWritableRaster;

public class CImage extends CFRetainedResource {
   static CImage.Creator creator = new CImage.Creator();

   private static native long nativeCreateNSImageFromArray(int[] var0, int var1, int var2);

   private static native long nativeCreateNSImageFromBytes(byte[] var0);

   private static native long nativeCreateNSImageFromArrays(int[][] var0, int[] var1, int[] var2);

   private static native long nativeCreateNSImageFromFileContents(String var0);

   private static native long nativeCreateNSImageOfFileFromLaunchServices(String var0);

   private static native long nativeCreateNSImageFromImageName(String var0);

   private static native long nativeCreateNSImageFromIconSelector(int var0);

   private static native byte[] nativeGetPlatformImageBytes(int[] var0, int var1, int var2);

   private static native void nativeCopyNSImageIntoArray(long var0, int[] var2, int var3, int var4, int var5, int var6);

   private static native Dimension2D nativeGetNSImageSize(long var0);

   private static native void nativeSetNSImageSize(long var0, double var2, double var4);

   private static native void nativeResizeNSImageRepresentations(long var0, double var2, double var4);

   private static native Dimension2D[] nativeGetNSImageRepresentationSizes(long var0, double var2, double var4);

   public static CImage.Creator getCreator() {
      return creator;
   }

   CImage(long var1) {
      super(var1, true);
   }

   private Image toImage() {
      if (this.ptr == 0L) {
         return null;
      } else {
         AtomicReference var1 = new AtomicReference();
         this.execute((var1x) -> {
            var1.set(nativeGetNSImageSize(var1x));
         });
         Dimension2D var2 = (Dimension2D)var1.get();
         if (var2 == null) {
            return null;
         } else {
            int var3 = (int)var2.getWidth();
            int var4 = (int)var2.getHeight();
            AtomicReference var5 = new AtomicReference();
            this.execute((var2x) -> {
               var5.set(nativeGetNSImageRepresentationSizes(var2x, var2.getWidth(), var2.getHeight()));
            });
            Dimension2D[] var6 = (Dimension2D[])var5.get();
            return var6 != null && var6.length >= 2 ? new MultiResolutionCachedImage(var3, var4, var6, (var3x, var4x) -> {
               return this.toImage(var3, var4, var3x, var4x);
            }) : new MultiResolutionCachedImage(var3, var4, (var3x, var4x) -> {
               return this.toImage(var3, var4, var3x, var4x);
            });
         }
      }
   }

   private BufferedImage toImage(int var1, int var2, int var3, int var4) {
      BufferedImage var5 = new BufferedImage(var3, var4, 3);
      DataBufferInt var6 = (DataBufferInt)var5.getRaster().getDataBuffer();
      int[] var7 = SunWritableRaster.stealData((DataBufferInt)var6, 0);
      this.execute((var5x) -> {
         nativeCopyNSImageIntoArray(var5x, var7, var1, var2, var3, var4);
      });
      SunWritableRaster.markDirty((DataBuffer)var6);
      return var5;
   }

   CImage resize(double var1, double var3) {
      this.execute((var4) -> {
         nativeSetNSImageSize(var4, var1, var3);
      });
      return this;
   }

   void resizeRepresentations(double var1, double var3) {
      this.execute((var4) -> {
         nativeResizeNSImageRepresentations(var4, var1, var3);
      });
   }

   public static class Creator {
      Creator() {
      }

      public Image createImageUsingNativeSize(long var1) {
         if (var1 == 0L) {
            return null;
         } else {
            Dimension2D var3 = CImage.nativeGetNSImageSize(var1);
            return this.createImage(var1, var3.getWidth(), var3.getHeight());
         }
      }

      Image createImage(long var1, double var3, double var5) {
         if (var1 == 0L) {
            throw new Error("Unable to instantiate CImage with null native image reference.");
         } else {
            return this.createImageWithSize(var1, var3, var5);
         }
      }

      public Image createImageWithSize(long var1, double var3, double var5) {
         CImage var7 = new CImage(var1);
         var7.resize(var3, var5);
         return var7.toImage();
      }

      public Image createImageOfFile(String var1, int var2, int var3) {
         return this.createImage(CImage.nativeCreateNSImageOfFileFromLaunchServices(var1), (double)var2, (double)var3);
      }

      public Image createImageFromFile(String var1, double var2, double var4) {
         long var6 = CImage.nativeCreateNSImageFromFileContents(var1);
         CImage.nativeSetNSImageSize(var6, var2, var4);
         return this.createImage(var6, var2, var4);
      }

      public Image createSystemImageFromSelector(String var1, int var2, int var3) {
         return this.createImage(CImage.nativeCreateNSImageFromIconSelector(getSelectorAsInt(var1)), (double)var2, (double)var3);
      }

      public Image createImageFromName(String var1, int var2, int var3) {
         return this.createImage(CImage.nativeCreateNSImageFromImageName(var1), (double)var2, (double)var3);
      }

      public Image createImageFromName(String var1) {
         return this.createImageUsingNativeSize(CImage.nativeCreateNSImageFromImageName(var1));
      }

      private static int[] imageToArray(Image var0, boolean var1) {
         if (var0 == null) {
            return null;
         } else {
            if (var1 && !(var0 instanceof BufferedImage)) {
               MediaTracker var2 = new MediaTracker(new Label());
               var2.addImage(var0, 0);

               try {
                  var2.waitForID(0);
               } catch (InterruptedException var6) {
                  return null;
               }

               if (var2.isErrorID(0)) {
                  return null;
               }
            }

            int var7 = var0.getWidth((ImageObserver)null);
            int var3 = var0.getHeight((ImageObserver)null);
            if (var7 >= 0 && var3 >= 0) {
               BufferedImage var4 = new BufferedImage(var7, var3, 3);
               Graphics2D var5 = var4.createGraphics();
               var5.setComposite(AlphaComposite.Src);
               var5.drawImage(var0, 0, 0, (ImageObserver)null);
               var5.dispose();
               return ((DataBufferInt)var4.getRaster().getDataBuffer()).getData();
            } else {
               return null;
            }
         }
      }

      public byte[] getPlatformImageBytes(Image var1) {
         int[] var2 = imageToArray(var1, false);
         return var2 == null ? null : CImage.nativeGetPlatformImageBytes(var2, var1.getWidth((ImageObserver)null), var1.getHeight((ImageObserver)null));
      }

      public Image createImageFromPlatformImageBytes(byte[] var1) {
         return this.createImageUsingNativeSize(CImage.nativeCreateNSImageFromBytes(var1));
      }

      public CImage createFromImage(Image var1) {
         return this.createFromImage(var1, true);
      }

      public CImage createFromImageImmediately(Image var1) {
         return this.createFromImage(var1, false);
      }

      private CImage createFromImage(Image var1, boolean var2) {
         if (var1 instanceof MultiResolutionImage) {
            List var4 = ((MultiResolutionImage)var1).getResolutionVariants();
            return this.createFromImages(var4, var2);
         } else {
            int[] var3 = imageToArray(var1, var2);
            return var3 == null ? null : new CImage(CImage.nativeCreateNSImageFromArray(var3, var1.getWidth((ImageObserver)null), var1.getHeight((ImageObserver)null)));
         }
      }

      public CImage createFromImages(List<Image> var1) {
         return this.createFromImages(var1, true);
      }

      private CImage createFromImages(List<Image> var1, boolean var2) {
         if (var1 != null && !var1.isEmpty()) {
            int var3 = var1.size();
            int[][] var4 = new int[var3][];
            int[] var5 = new int[var3];
            int[] var6 = new int[var3];
            var3 = 0;
            Iterator var7 = var1.iterator();

            while(var7.hasNext()) {
               Image var8 = (Image)var7.next();
               var4[var3] = imageToArray(var8, var2);
               if (var4[var3] != null) {
                  var5[var3] = var8.getWidth((ImageObserver)null);
                  var6[var3] = var8.getHeight((ImageObserver)null);
                  ++var3;
               }
            }

            if (var3 == 0) {
               return null;
            } else {
               return new CImage(CImage.nativeCreateNSImageFromArrays((int[][])Arrays.copyOf((Object[])var4, var3), Arrays.copyOf(var5, var3), Arrays.copyOf(var6, var3)));
            }
         } else {
            return null;
         }
      }

      static int getSelectorAsInt(String var0) {
         byte[] var1 = var0.getBytes();
         int var2 = Math.min(var1.length, 4);
         int var3 = 0;

         for(int var4 = 0; var4 < var2; ++var4) {
            if (var4 > 0) {
               var3 <<= 8;
            }

            var3 |= var1[var4] & 255;
         }

         return var3;
      }
   }
}
