package java.awt.geom;

import java.util.NoSuchElementException;

class CubicIterator implements PathIterator {
   CubicCurve2D cubic;
   AffineTransform affine;
   int index;

   CubicIterator(CubicCurve2D var1, AffineTransform var2) {
      this.cubic = var1;
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
         throw new NoSuchElementException("cubic iterator iterator out of bounds");
      } else {
         byte var2;
         if (this.index == 0) {
            var1[0] = (float)this.cubic.getX1();
            var1[1] = (float)this.cubic.getY1();
            var2 = 0;
         } else {
            var1[0] = (float)this.cubic.getCtrlX1();
            var1[1] = (float)this.cubic.getCtrlY1();
            var1[2] = (float)this.cubic.getCtrlX2();
            var1[3] = (float)this.cubic.getCtrlY2();
            var1[4] = (float)this.cubic.getX2();
            var1[5] = (float)this.cubic.getY2();
            var2 = 3;
         }

         if (this.affine != null) {
            this.affine.transform((float[])var1, 0, (float[])var1, 0, this.index == 0 ? 1 : 3);
         }

         return var2;
      }
   }

   public int currentSegment(double[] var1) {
      if (this.isDone()) {
         throw new NoSuchElementException("cubic iterator iterator out of bounds");
      } else {
         byte var2;
         if (this.index == 0) {
            var1[0] = this.cubic.getX1();
            var1[1] = this.cubic.getY1();
            var2 = 0;
         } else {
            var1[0] = this.cubic.getCtrlX1();
            var1[1] = this.cubic.getCtrlY1();
            var1[2] = this.cubic.getCtrlX2();
            var1[3] = this.cubic.getCtrlY2();
            var1[4] = this.cubic.getX2();
            var1[5] = this.cubic.getY2();
            var2 = 3;
         }

         if (this.affine != null) {
            this.affine.transform((double[])var1, 0, (double[])var1, 0, this.index == 0 ? 1 : 3);
         }

         return var2;
      }
   }
}
