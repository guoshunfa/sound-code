package com.sun.media.sound;

public final class ModelStandardTransform implements ModelTransform {
   public static final boolean DIRECTION_MIN2MAX = false;
   public static final boolean DIRECTION_MAX2MIN = true;
   public static final boolean POLARITY_UNIPOLAR = false;
   public static final boolean POLARITY_BIPOLAR = true;
   public static final int TRANSFORM_LINEAR = 0;
   public static final int TRANSFORM_CONCAVE = 1;
   public static final int TRANSFORM_CONVEX = 2;
   public static final int TRANSFORM_SWITCH = 3;
   public static final int TRANSFORM_ABSOLUTE = 4;
   private boolean direction = false;
   private boolean polarity = false;
   private int transform = 0;

   public ModelStandardTransform() {
   }

   public ModelStandardTransform(boolean var1) {
      this.direction = var1;
   }

   public ModelStandardTransform(boolean var1, boolean var2) {
      this.direction = var1;
      this.polarity = var2;
   }

   public ModelStandardTransform(boolean var1, boolean var2, int var3) {
      this.direction = var1;
      this.polarity = var2;
      this.transform = var3;
   }

   public double transform(double var1) {
      if (this.direction) {
         var1 = 1.0D - var1;
      }

      if (this.polarity) {
         var1 = var1 * 2.0D - 1.0D;
      }

      double var3;
      double var5;
      switch(this.transform) {
      case 1:
         var3 = Math.signum(var1);
         var5 = Math.abs(var1);
         var5 = -(0.4166666666666667D / Math.log(10.0D)) * Math.log(1.0D - var5);
         if (var5 < 0.0D) {
            var5 = 0.0D;
         } else if (var5 > 1.0D) {
            var5 = 1.0D;
         }

         return var3 * var5;
      case 2:
         var3 = Math.signum(var1);
         var5 = Math.abs(var1);
         var5 = 1.0D + 0.4166666666666667D / Math.log(10.0D) * Math.log(var5);
         if (var5 < 0.0D) {
            var5 = 0.0D;
         } else if (var5 > 1.0D) {
            var5 = 1.0D;
         }

         return var3 * var5;
      case 3:
         if (this.polarity) {
            return var1 > 0.0D ? 1.0D : -1.0D;
         }

         return var1 > 0.5D ? 1.0D : 0.0D;
      case 4:
         return Math.abs(var1);
      default:
         return var1;
      }
   }

   public boolean getDirection() {
      return this.direction;
   }

   public void setDirection(boolean var1) {
      this.direction = var1;
   }

   public boolean getPolarity() {
      return this.polarity;
   }

   public void setPolarity(boolean var1) {
      this.polarity = var1;
   }

   public int getTransform() {
      return this.transform;
   }

   public void setTransform(int var1) {
      this.transform = var1;
   }
}
