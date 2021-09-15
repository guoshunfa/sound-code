package sun.awt.image;

import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import sun.java2d.SurfaceData;

public class WritableRasterNative extends WritableRaster {
   public static WritableRasterNative createNativeRaster(SampleModel var0, DataBuffer var1) {
      return new WritableRasterNative(var0, var1);
   }

   protected WritableRasterNative(SampleModel var1, DataBuffer var2) {
      super(var1, var2, new Point(0, 0));
   }

   public static WritableRasterNative createNativeRaster(ColorModel var0, SurfaceData var1, int var2, int var3) {
      Object var4 = null;
      boolean var5 = false;
      int[] var8;
      DirectColorModel var9;
      byte var10;
      switch(var0.getPixelSize()) {
      case 8:
      case 12:
         if (var0.getPixelSize() == 8) {
            var10 = 0;
         } else {
            var10 = 1;
         }

         int[] var7 = new int[]{0};
         var4 = new PixelInterleavedSampleModel(var10, var2, var3, 1, var2, var7);
         break;
      case 15:
      case 16:
         var10 = 1;
         var8 = new int[3];
         var9 = (DirectColorModel)var0;
         var8[0] = var9.getRedMask();
         var8[1] = var9.getGreenMask();
         var8[2] = var9.getBlueMask();
         var4 = new SinglePixelPackedSampleModel(var10, var2, var3, var2, var8);
         break;
      case 24:
      case 32:
         var10 = 3;
         var8 = new int[3];
         var9 = (DirectColorModel)var0;
         var8[0] = var9.getRedMask();
         var8[1] = var9.getGreenMask();
         var8[2] = var9.getBlueMask();
         var4 = new SinglePixelPackedSampleModel(var10, var2, var3, var2, var8);
         break;
      default:
         throw new InternalError("Unsupported depth " + var0.getPixelSize());
      }

      DataBufferNative var11 = new DataBufferNative(var1, var10, var2, var3);
      return new WritableRasterNative((SampleModel)var4, var11);
   }
}
