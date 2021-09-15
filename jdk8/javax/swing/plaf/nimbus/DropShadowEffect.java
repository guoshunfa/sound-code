package javax.swing.plaf.nimbus;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

class DropShadowEffect extends ShadowEffect {
   Effect.EffectType getEffectType() {
      return Effect.EffectType.UNDER;
   }

   BufferedImage applyEffect(BufferedImage var1, BufferedImage var2, int var3, int var4) {
      if (var1 != null && var1.getType() == 2) {
         if (var2 != null && var2.getType() != 2) {
            throw new IllegalArgumentException("Effect only works with destination images of type BufferedImage.TYPE_INT_ARGB.");
         } else {
            double var5 = Math.toRadians((double)(this.angle - 90));
            int var7 = (int)(Math.sin(var5) * (double)this.distance);
            int var8 = (int)(Math.cos(var5) * (double)this.distance);
            int var9 = var7 + this.size;
            int var10 = var7 + this.size;
            int var11 = var3 + var7 + this.size + this.size;
            int var12 = var4 + var7 + this.size;
            int[] var13 = getArrayCache().getTmpIntArray(var3);
            byte[] var14 = getArrayCache().getTmpByteArray1(var11 * var12);
            Arrays.fill((byte[])var14, (byte)0);
            byte[] var15 = getArrayCache().getTmpByteArray2(var11 * var12);
            WritableRaster var16 = var1.getRaster();

            int var19;
            int var20;
            int var21;
            for(int var17 = 0; var17 < var4; ++var17) {
               int var18 = var17 + var10;
               var19 = var18 * var11;
               var16.getDataElements(0, var17, var3, 1, var13);

               for(var20 = 0; var20 < var3; ++var20) {
                  var21 = var20 + var9;
                  var14[var19 + var21] = (byte)((var13[var20] & -16777216) >>> 24);
               }
            }

            float[] var28 = EffectUtils.createGaussianKernel(this.size);
            EffectUtils.blur(var14, var15, var11, var12, var28, this.size);
            EffectUtils.blur(var15, var14, var12, var11, var28, this.size);
            float var29 = Math.min(1.0F / (1.0F - 0.01F * (float)this.spread), 255.0F);

            for(var19 = 0; var19 < var14.length; ++var19) {
               var20 = (int)((float)(var14[var19] & 255) * var29);
               var14[var19] = var20 > 255 ? -1 : (byte)var20;
            }

            if (var2 == null) {
               var2 = new BufferedImage(var3, var4, 2);
            }

            WritableRaster var30 = var2.getRaster();
            var20 = this.color.getRed();
            var21 = this.color.getGreen();
            int var22 = this.color.getBlue();

            for(int var23 = 0; var23 < var4; ++var23) {
               int var24 = var23 + var10;
               int var25 = (var24 - var8) * var11;

               for(int var26 = 0; var26 < var3; ++var26) {
                  int var27 = var26 + var9;
                  var13[var26] = var14[var25 + (var27 - var7)] << 24 | var20 << 16 | var21 << 8 | var22;
               }

               var30.setDataElements(0, var23, var3, 1, var13);
            }

            return var2;
         }
      } else {
         throw new IllegalArgumentException("Effect only works with source images of type BufferedImage.TYPE_INT_ARGB.");
      }
   }
}
