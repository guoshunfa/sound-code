package java.awt.geom;

import java.util.NoSuchElementException;

class ArcIterator implements PathIterator {
   double x;
   double y;
   double w;
   double h;
   double angStRad;
   double increment;
   double cv;
   AffineTransform affine;
   int index;
   int arcSegs;
   int lineSegs;

   ArcIterator(Arc2D var1, AffineTransform var2) {
      this.w = var1.getWidth() / 2.0D;
      this.h = var1.getHeight() / 2.0D;
      this.x = var1.getX() + this.w;
      this.y = var1.getY() + this.h;
      this.angStRad = -Math.toRadians(var1.getAngleStart());
      this.affine = var2;
      double var3 = -var1.getAngleExtent();
      if (var3 < 360.0D && var3 > -360.0D) {
         this.arcSegs = (int)Math.ceil(Math.abs(var3) / 90.0D);
         this.increment = Math.toRadians(var3 / (double)this.arcSegs);
         this.cv = btan(this.increment);
         if (this.cv == 0.0D) {
            this.arcSegs = 0;
         }
      } else {
         this.arcSegs = 4;
         this.increment = 1.5707963267948966D;
         this.cv = 0.5522847498307933D;
         if (var3 < 0.0D) {
            this.increment = -this.increment;
            this.cv = -this.cv;
         }
      }

      switch(var1.getArcType()) {
      case 0:
         this.lineSegs = 0;
         break;
      case 1:
         this.lineSegs = 1;
         break;
      case 2:
         this.lineSegs = 2;
      }

      if (this.w < 0.0D || this.h < 0.0D) {
         this.arcSegs = this.lineSegs = -1;
      }

   }

   public int getWindingRule() {
      return 1;
   }

   public boolean isDone() {
      return this.index > this.arcSegs + this.lineSegs;
   }

   public void next() {
      ++this.index;
   }

   private static double btan(double var0) {
      var0 /= 2.0D;
      return 1.3333333333333333D * Math.sin(var0) / (1.0D + Math.cos(var0));
   }

   public int currentSegment(float[] var1) {
      if (this.isDone()) {
         throw new NoSuchElementException("arc iterator out of bounds");
      } else {
         double var2 = this.angStRad;
         if (this.index == 0) {
            var1[0] = (float)(this.x + Math.cos(var2) * this.w);
            var1[1] = (float)(this.y + Math.sin(var2) * this.h);
            if (this.affine != null) {
               this.affine.transform((float[])var1, 0, (float[])var1, 0, 1);
            }

            return 0;
         } else if (this.index > this.arcSegs) {
            if (this.index == this.arcSegs + this.lineSegs) {
               return 4;
            } else {
               var1[0] = (float)this.x;
               var1[1] = (float)this.y;
               if (this.affine != null) {
                  this.affine.transform((float[])var1, 0, (float[])var1, 0, 1);
               }

               return 1;
            }
         } else {
            var2 += this.increment * (double)(this.index - 1);
            double var4 = Math.cos(var2);
            double var6 = Math.sin(var2);
            var1[0] = (float)(this.x + (var4 - this.cv * var6) * this.w);
            var1[1] = (float)(this.y + (var6 + this.cv * var4) * this.h);
            var2 += this.increment;
            var4 = Math.cos(var2);
            var6 = Math.sin(var2);
            var1[2] = (float)(this.x + (var4 + this.cv * var6) * this.w);
            var1[3] = (float)(this.y + (var6 - this.cv * var4) * this.h);
            var1[4] = (float)(this.x + var4 * this.w);
            var1[5] = (float)(this.y + var6 * this.h);
            if (this.affine != null) {
               this.affine.transform((float[])var1, 0, (float[])var1, 0, 3);
            }

            return 3;
         }
      }
   }

   public int currentSegment(double[] var1) {
      if (this.isDone()) {
         throw new NoSuchElementException("arc iterator out of bounds");
      } else {
         double var2 = this.angStRad;
         if (this.index == 0) {
            var1[0] = this.x + Math.cos(var2) * this.w;
            var1[1] = this.y + Math.sin(var2) * this.h;
            if (this.affine != null) {
               this.affine.transform((double[])var1, 0, (double[])var1, 0, 1);
            }

            return 0;
         } else if (this.index > this.arcSegs) {
            if (this.index == this.arcSegs + this.lineSegs) {
               return 4;
            } else {
               var1[0] = this.x;
               var1[1] = this.y;
               if (this.affine != null) {
                  this.affine.transform((double[])var1, 0, (double[])var1, 0, 1);
               }

               return 1;
            }
         } else {
            var2 += this.increment * (double)(this.index - 1);
            double var4 = Math.cos(var2);
            double var6 = Math.sin(var2);
            var1[0] = this.x + (var4 - this.cv * var6) * this.w;
            var1[1] = this.y + (var6 + this.cv * var4) * this.h;
            var2 += this.increment;
            var4 = Math.cos(var2);
            var6 = Math.sin(var2);
            var1[2] = this.x + (var4 + this.cv * var6) * this.w;
            var1[3] = this.y + (var6 - this.cv * var4) * this.h;
            var1[4] = this.x + var4 * this.w;
            var1[5] = this.y + var6 * this.h;
            if (this.affine != null) {
               this.affine.transform((double[])var1, 0, (double[])var1, 0, 3);
            }

            return 3;
         }
      }
   }
}
