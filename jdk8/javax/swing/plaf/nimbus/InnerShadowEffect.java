package javax.swing.plaf.nimbus;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

class InnerShadowEffect extends ShadowEffect {
   Effect.EffectType getEffectType() {
      return Effect.EffectType.OVER;
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
            Arrays.fill(var14, (byte)-1);
            byte[] var15 = getArrayCache().getTmpByteArray2(var11 * var12);
            byte[] var16 = getArrayCache().getTmpByteArray3(var11 * var12);
            WritableRaster var17 = var1.getRaster();

            int var20;
            int var21;
            int var22;
            for(int var18 = 0; var18 < var4; ++var18) {
               int var19 = var18 + var10;
               var20 = var19 * var11;
               var17.getDataElements(0, var18, var3, 1, var13);

               for(var21 = 0; var21 < var3; ++var21) {
                  var22 = var21 + var9;
                  var14[var20 + var22] = (byte)(255 - ((var13[var21] & -16777216) >>> 24) & 255);
               }
            }

            float[] var33 = EffectUtils.createGaussianKernel(this.size * 2);
            EffectUtils.blur(var14, var16, var11, var12, var33, this.size * 2);
            EffectUtils.blur(var16, var15, var12, var11, var33, this.size * 2);
            float var34 = Math.min(1.0F / (1.0F - 0.01F * (float)this.spread), 255.0F);

            for(var20 = 0; var20 < var15.length; ++var20) {
               var21 = (int)((float)(var15[var20] & 255) * var34);
               var15[var20] = var21 > 255 ? -1 : (byte)var21;
            }

            if (var2 == null) {
               var2 = new BufferedImage(var3, var4, 2);
            }

            WritableRaster var35 = var2.getRaster();
            var21 = this.color.getRed();
            var22 = this.color.getGreen();
            int var23 = this.color.getBlue();

            for(int var24 = 0; var24 < var4; ++var24) {
               int var25 = var24 + var10;
               int var26 = var25 * var11;
               int var27 = (var25 - var8) * var11;

               for(int var28 = 0; var28 < var3; ++var28) {
                  int var29 = var28 + var9;
                  int var30 = 255 - (var14[var26 + var29] & 255);
                  int var31 = var15[var27 + (var29 - var7)] & 255;
                  int var32 = Math.min(var30, var31);
                  var13[var28] = ((byte)var32 & 255) << 24 | var21 << 16 | var22 << 8 | var23;
               }

               var35.setDataElements(0, var24, var3, 1, var13);
            }

            return var2;
         }
      } else {
         throw new IllegalArgumentException("Effect only works with source images of type BufferedImage.TYPE_INT_ARGB.");
      }
   }
}
