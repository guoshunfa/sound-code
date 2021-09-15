package java.awt.geom;

import java.util.NoSuchElementException;

class QuadIterator implements PathIterator {
   QuadCurve2D quad;
   AffineTransform affine;
   int index;

   QuadIterator(QuadCurve2D var1, AffineTransform var2) {
      this.quad = var1;
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
         throw new NoSuchElementException("quad iterator iterator out of bounds");
      } else {
         byte var2;
         if (this.index == 0) {
            var1[0] = (float)this.quad.getX1();
            var1[1] = (float)this.quad.getY1();
            var2 = 0;
         } else {
            var1[0] = (float)this.quad.getCtrlX();
            var1[1] = (float)this.quad.getCtrlY();
            var1[2] = (float)this.quad.getX2();
            var1[3] = (float)this.quad.getY2();
            var2 = 2;
         }

         if (this.affine != null) {
            this.affine.transform((float[])var1, 0, (float[])var1, 0, this.index == 0 ? 1 : 2);
         }

         return var2;
      }
   }

   public int currentSegment(double[] var1) {
      if (this.isDone()) {
         throw new NoSuchElementException("quad iterator iterator out of bounds");
      } else {
         byte var2;
         if (this.index == 0) {
            var1[0] = this.quad.getX1();
            var1[1] = this.quad.getY1();
            var2 = 0;
         } else {
            var1[0] = this.quad.getCtrlX();
            var1[1] = this.quad.getCtrlY();
            var1[2] = this.quad.getX2();
            var1[3] = this.quad.getY2();
            var2 = 2;
         }

         if (this.affine != null) {
            this.affine.transform((double[])var1, 0, (double[])var1, 0, this.index == 0 ? 1 : 2);
         }

         return var2;
      }
   }
}
