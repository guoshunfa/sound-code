package sun.java2d.loops;

import java.awt.Composite;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.SpanIterator;

class XorCopyArgbToAny extends Blit {
   XorCopyArgbToAny() {
      super(SurfaceType.IntArgb, CompositeType.Xor, SurfaceType.Any);
   }

   public void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      Raster var11 = var1.getRaster(var5, var6, var9, var10);
      IntegerComponentRaster var12 = (IntegerComponentRaster)var11;
      int[] var13 = var12.getDataStorage();
      WritableRaster var14 = (WritableRaster)var2.getRaster(var7, var8, var9, var10);
      ColorModel var15 = var2.getColorModel();
      Region var16 = CustomComponent.getRegionOfInterest(var1, var2, var4, var5, var6, var7, var8, var9, var10);
      SpanIterator var17 = var16.getSpanIterator();
      int var18 = ((XORComposite)var3).getXorColor().getRGB();
      Object var19 = var15.getDataElements(var18, (Object)null);
      Object var20 = null;
      Object var21 = null;
      int var22 = var12.getScanlineStride();
      var5 -= var7;
      var6 -= var8;
      int[] var23 = new int[4];

      while(var17.nextSpan(var23)) {
         int var24 = var12.getDataOffset(0) + (var6 + var23[1]) * var22 + var5 + var23[0];

         for(int var25 = var23[1]; var25 < var23[3]; ++var25) {
            int var26 = var24;

            for(int var27 = var23[0]; var27 < var23[2]; ++var27) {
               var20 = var15.getDataElements(var13[var26++], var20);
               var21 = var14.getDataElements(var27, var25, var21);
               label64:
               switch(var15.getTransferType()) {
               case 0:
                  byte[] var28 = (byte[])((byte[])var20);
                  byte[] var29 = (byte[])((byte[])var21);
                  byte[] var30 = (byte[])((byte[])var19);
                  int var46 = 0;

                  while(true) {
                     if (var46 >= var29.length) {
                        break label64;
                     }

                     var29[var46] = (byte)(var29[var46] ^ var28[var46] ^ var30[var46]);
                     ++var46;
                  }
               case 1:
               case 2:
                  short[] var31 = (short[])((short[])var20);
                  short[] var32 = (short[])((short[])var21);
                  short[] var33 = (short[])((short[])var19);
                  int var47 = 0;

                  while(true) {
                     if (var47 >= var32.length) {
                        break label64;
                     }

                     var32[var47] = (short)(var32[var47] ^ var31[var47] ^ var33[var47]);
                     ++var47;
                  }
               case 3:
                  int[] var34 = (int[])((int[])var20);
                  int[] var35 = (int[])((int[])var21);
                  int[] var36 = (int[])((int[])var19);
                  int var48 = 0;

                  while(true) {
                     if (var48 >= var35.length) {
                        break label64;
                     }

                     var35[var48] ^= var34[var48] ^ var36[var48];
                     ++var48;
                  }
               case 4:
                  float[] var37 = (float[])((float[])var20);
                  float[] var38 = (float[])((float[])var21);
                  float[] var39 = (float[])((float[])var19);
                  int var49 = 0;

                  while(true) {
                     if (var49 >= var38.length) {
                        break label64;
                     }

                     int var50 = Float.floatToIntBits(var38[var49]) ^ Float.floatToIntBits(var37[var49]) ^ Float.floatToIntBits(var39[var49]);
                     var38[var49] = Float.intBitsToFloat(var50);
                     ++var49;
                  }
               case 5:
                  double[] var40 = (double[])((double[])var20);
                  double[] var41 = (double[])((double[])var21);
                  double[] var42 = (double[])((double[])var19);
                  int var43 = 0;

                  while(true) {
                     if (var43 >= var41.length) {
                        break label64;
                     }

                     long var44 = Double.doubleToLongBits(var41[var43]) ^ Double.doubleToLongBits(var40[var43]) ^ Double.doubleToLongBits(var42[var43]);
                     var41[var43] = Double.longBitsToDouble(var44);
                     ++var43;
                  }
               default:
                  throw new InternalError("Unsupported XOR pixel type");
               }

               var14.setDataElements(var27, var25, var21);
            }

            var24 += var22;
         }
      }

   }
}
