package java.awt.geom;

import java.util.NoSuchElementException;

class RectIterator implements PathIterator {
   double x;
   double y;
   double w;
   double h;
   AffineTransform affine;
   int index;

   RectIterator(Rectangle2D var1, AffineTransform var2) {
      this.x = var1.getX();
      this.y = var1.getY();
      this.w = var1.getWidth();
      this.h = var1.getHeight();
      this.affine = var2;
      if (this.w < 0.0D || this.h < 0.0D) {
         this.index = 6;
      }

   }

   public int getWindingRule() {
      return 1;
   }

   public boolean isDone() {
      return this.index > 5;
   }

   public void next() {
      ++this.index;
   }

   public int currentSegment(float[] var1) {
      if (this.isDone()) {
         throw new NoSuchElementException("rect iterator out of bounds");
      } else if (this.index == 5) {
         return 4;
      } else {
         var1[0] = (float)this.x;
         var1[1] = (float)this.y;
         if (this.index == 1 || this.index == 2) {
            var1[0] += (float)this.w;
         }

         if (this.index == 2 || this.index == 3) {
            var1[1] += (float)this.h;
         }

         if (this.affine != null) {
            this.affine.transform((float[])var1, 0, (float[])var1, 0, 1);
         }

         return this.index == 0 ? 0 : 1;
      }
   }

   public int currentSegment(double[] var1) {
      if (this.isDone()) {
         throw new NoSuchElementException("rect iterator out of bounds");
      } else if (this.index == 5) {
         return 4;
      } else {
         var1[0] = this.x;
         var1[1] = this.y;
         if (this.index == 1 || this.index == 2) {
            var1[0] += this.w;
         }

         if (this.index == 2 || this.index == 3) {
            var1[1] += this.h;
         }

         if (this.affine != null) {
            this.affine.transform((double[])var1, 0, (double[])var1, 0, 1);
         }

         return this.index == 0 ? 0 : 1;
      }
   }
}
