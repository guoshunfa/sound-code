package java.awt.geom;

import java.util.NoSuchElementException;

class LineIterator implements PathIterator {
   Line2D line;
   AffineTransform affine;
   int index;

   LineIterator(Line2D var1, AffineTransform var2) {
      this.line = var1;
      this.affine = var2;
   }

   public int getWindingRule() {
      return 1;
   }

   public boolean isDone() {
      return this.index > 1;
   }

   public void next() {
      ++this.index;
   }

   public int currentSegment(float[] var1) {
      if (this.isDone()) {
         throw new NoSuchElementException("line iterator out of bounds");
      } else {
         byte var2;
         if (this.index == 0) {
            var1[0] = (float)this.line.getX1();
            var1[1] = (float)this.line.getY1();
            var2 = 0;
         } else {
            var1[0] = (float)this.line.getX2();
            var1[1] = (float)this.line.getY2();
            var2 = 1;
         }

         if (this.affine != null) {
            this.affine.transform((float[])var1, 0, (float[])var1, 0, 1);
         }

         return var2;
      }
   }

   public int currentSegment(double[] var1) {
      if (this.isDone()) {
         throw new NoSuchElementException("line iterator out of bounds");
      } else {
         byte var2;
         if (this.index == 0) {
            var1[0] = this.line.getX1();
            var1[1] = this.line.getY1();
            var2 = 0;
         } else {
            var1[0] = this.line.getX2();
            var1[1] = this.line.getY2();
            var2 = 1;
         }

         if (this.affine != null) {
            this.affine.transform((double[])var1, 0, (double[])var1, 0, 1);
         }

         return var2;
      }
   }
}
