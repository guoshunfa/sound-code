package sun.java2d.loops;

import java.awt.image.ColorModel;

abstract class XorPixelWriter extends PixelWriter {
   protected ColorModel dstCM;

   public void writePixel(int var1, int var2) {
      Object var3 = this.dstRast.getDataElements(var1, var2, (Object)null);
      this.xorPixel(var3);
      this.dstRast.setDataElements(var1, var2, var3);
   }

   protected abstract void xorPixel(Object var1);

   public static class DoubleData extends XorPixelWriter {
      long[] xorData;

      DoubleData(Object var1, Object var2) {
         double[] var3 = (double[])((double[])var1);
         double[] var4 = (double[])((double[])var2);
         this.xorData = new long[var3.length];

         for(int var5 = 0; var5 < var3.length; ++var5) {
            this.xorData[var5] = Double.doubleToLongBits(var3[var5]) ^ Double.doubleToLongBits(var4[var5]);
         }

      }

      protected void xorPixel(Object var1) {
         double[] var2 = (double[])((double[])var1);

         for(int var3 = 0; var3 < var2.length; ++var3) {
            long var4 = Double.doubleToLongBits(var2[var3]) ^ this.xorData[var3];
            var2[var3] = Double.longBitsToDouble(var4);
         }

      }
   }

   public static class FloatData extends XorPixelWriter {
      int[] xorData;

      FloatData(Object var1, Object var2) {
         float[] var3 = (float[])((float[])var1);
         float[] var4 = (float[])((float[])var2);
         this.xorData = new int[var3.length];

         for(int var5 = 0; var5 < var3.length; ++var5) {
            this.xorData[var5] = Float.floatToIntBits(var3[var5]) ^ Float.floatToIntBits(var4[var5]);
         }

      }

      protected void xorPixel(Object var1) {
         float[] var2 = (float[])((float[])var1);

         for(int var3 = 0; var3 < var2.length; ++var3) {
            int var4 = Float.floatToIntBits(var2[var3]) ^ this.xorData[var3];
            var2[var3] = Float.intBitsToFloat(var4);
         }

      }
   }

   public static class IntData extends XorPixelWriter {
      int[] xorData;

      IntData(Object var1, Object var2) {
         this.xorData = (int[])((int[])var1);
         this.xorPixel(var2);
         this.xorData = (int[])((int[])var2);
      }

      protected void xorPixel(Object var1) {
         int[] var2 = (int[])((int[])var1);

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3] ^= this.xorData[var3];
         }

      }
   }

   public static class ShortData extends XorPixelWriter {
      short[] xorData;

      ShortData(Object var1, Object var2) {
         this.xorData = (short[])((short[])var1);
         this.xorPixel(var2);
         this.xorData = (short[])((short[])var2);
      }

      protected void xorPixel(Object var1) {
         short[] var2 = (short[])((short[])var1);

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3] ^= this.xorData[var3];
         }

      }
   }

   public static class ByteData extends XorPixelWriter {
      byte[] xorData;

      ByteData(Object var1, Object var2) {
         this.xorData = (byte[])((byte[])var1);
         this.xorPixel(var2);
         this.xorData = (byte[])((byte[])var2);
      }

      protected void xorPixel(Object var1) {
         byte[] var2 = (byte[])((byte[])var1);

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3] ^= this.xorData[var3];
         }

      }
   }
}
