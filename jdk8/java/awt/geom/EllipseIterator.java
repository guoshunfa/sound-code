package java.awt.geom;

import java.util.NoSuchElementException;

class EllipseIterator implements PathIterator {
   double x;
   double y;
   double w;
   double h;
   AffineTransform affine;
   int index;
   public static final double CtrlVal = 0.5522847498307933D;
   private static final double pcv = 0.7761423749153966D;
   private static final double ncv = 0.22385762508460333D;
   private static double[][] ctrlpts = new double[][]{{1.0D, 0.7761423749153966D, 0.7761423749153966D, 1.0D, 0.5D, 1.0D}, {0.22385762508460333D, 1.0D, 0.0D, 0.7761423749153966D, 0.0D, 0.5D}, {0.0D, 0.22385762508460333D, 0.22385762508460333D, 0.0D, 0.5D, 0.0D}, {0.7761423749153966D, 0.0D, 1.0D, 0.22385762508460333D, 1.0D, 0.5D}};

   EllipseIterator(Ellipse2D var1, AffineTransform var2) {
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
         throw new NoSuchElementException("ellipse iterator out of bounds");
      } else if (this.index == 5) {
         return 4;
      } else {
         double[] var2;
         if (this.index == 0) {
            var2 = ctrlpts[3];
            var1[0] = (float)(this.x + var2[4] * this.w);
            var1[1] = (float)(this.y + var2[5] * this.h);
            if (this.affine != null) {
               this.affine.transform((float[])var1, 0, (float[])var1, 0, 1);
            }

            return 0;
         } else {
            var2 = ctrlpts[this.index - 1];
            var1[0] = (float)(this.x + var2[0] * this.w);
            var1[1] = (float)(this.y + var2[1] * this.h);
            var1[2] = (float)(this.x + var2[2] * this.w);
            var1[3] = (float)(this.y + var2[3] * this.h);
            var1[4] = (float)(this.x + var2[4] * this.w);
            var1[5] = (float)(this.y + var2[5] * this.h);
            if (this.affine != null) {
               this.affine.transform((float[])var1, 0, (float[])var1, 0, 3);
            }

            return 3;
         }
      }
   }

   public int currentSegment(double[] var1) {
      if (this.isDone()) {
         throw new NoSuchElementException("ellipse iterator out of bounds");
      } else if (this.index == 5) {
         return 4;
      } else {
         double[] var2;
         if (this.index == 0) {
            var2 = ctrlpts[3];
            var1[0] = this.x + var2[4] * this.w;
            var1[1] = this.y + var2[5] * this.h;
            if (this.affine != null) {
               this.affine.transform((double[])var1, 0, (double[])var1, 0, 1);
            }

            return 0;
         } else {
            var2 = ctrlpts[this.index - 1];
            var1[0] = this.x + var2[0] * this.w;
            var1[1] = this.y + var2[1] * this.h;
            var1[2] = this.x + var2[2] * this.w;
            var1[3] = this.y + var2[3] * this.h;
            var1[4] = this.x + var2[4] * this.w;
            var1[5] = this.y + var2[5] * this.h;
            if (this.affine != null) {
               this.affine.transform((double[])var1, 0, (double[])var1, 0, 3);
            }

            return 3;
         }
      }
   }
}
