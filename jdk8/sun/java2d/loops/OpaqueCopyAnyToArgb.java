package sun.java2d.loops;

import java.awt.Composite;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import sun.awt.image.IntegerComponentRaster;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.SpanIterator;

class OpaqueCopyAnyToArgb extends Blit {
   OpaqueCopyAnyToArgb() {
      super(SurfaceType.Any, CompositeType.SrcNoEa, SurfaceType.IntArgb);
   }

   public void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      Raster var11 = var1.getRaster(var5, var6, var9, var10);
      ColorModel var12 = var1.getColorModel();
      Raster var13 = var2.getRaster(var7, var8, var9, var10);
      IntegerComponentRaster var14 = (IntegerComponentRaster)var13;
      int[] var15 = var14.getDataStorage();
      Region var16 = CustomComponent.getRegionOfInterest(var1, var2, var4, var5, var6, var7, var8, var9, var10);
      SpanIterator var17 = var16.getSpanIterator();
      Object var18 = null;
      int var19 = var14.getScanlineStride();
      var5 -= var7;
      var6 -= var8;
      int[] var20 = new int[4];

      while(var17.nextSpan(var20)) {
         int var21 = var14.getDataOffset(0) + var20[1] * var19 + var20[0];

         for(int var22 = var20[1]; var22 < var20[3]; ++var22) {
            int var23 = var21;

            for(int var24 = var20[0]; var24 < var20[2]; ++var24) {
               var18 = var11.getDataElements(var24 + var5, var22 + var6, var18);
               var15[var23++] = var12.getRGB(var18);
            }

            var21 += var19;
         }
      }

      var14.markDirty();
   }
}
