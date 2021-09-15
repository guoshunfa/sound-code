package com.sun.imageio.plugins.common;

import java.awt.color.ColorSpace;

public class BogusColorSpace extends ColorSpace {
   private static int getType(int var0) {
      if (var0 < 1) {
         throw new IllegalArgumentException("numComponents < 1!");
      } else {
         int var1;
         switch(var0) {
         case 1:
            var1 = 6;
            break;
         default:
            var1 = var0 + 10;
         }

         return var1;
      }
   }

   public BogusColorSpace(int var1) {
      super(getType(var1), var1);
   }

   public float[] toRGB(float[] var1) {
      if (var1.length < this.getNumComponents()) {
         throw new ArrayIndexOutOfBoundsException("colorvalue.length < getNumComponents()");
      } else {
         float[] var2 = new float[3];
         System.arraycopy(var1, 0, var2, 0, Math.min(3, this.getNumComponents()));
         return var1;
      }
   }

   public float[] fromRGB(float[] var1) {
      if (var1.length < 3) {
         throw new ArrayIndexOutOfBoundsException("rgbvalue.length < 3");
      } else {
         float[] var2 = new float[this.getNumComponents()];
         System.arraycopy(var1, 0, var2, 0, Math.min(3, var2.length));
         return var1;
      }
   }

   public float[] toCIEXYZ(float[] var1) {
      if (var1.length < this.getNumComponents()) {
         throw new ArrayIndexOutOfBoundsException("colorvalue.length < getNumComponents()");
      } else {
         float[] var2 = new float[3];
         System.arraycopy(var1, 0, var2, 0, Math.min(3, this.getNumComponents()));
         return var1;
      }
   }

   public float[] fromCIEXYZ(float[] var1) {
      if (var1.length < 3) {
         throw new ArrayIndexOutOfBoundsException("xyzvalue.length < 3");
      } else {
         float[] var2 = new float[this.getNumComponents()];
         System.arraycopy(var1, 0, var2, 0, Math.min(3, var2.length));
         return var1;
      }
   }
}
