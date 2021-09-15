package java.awt.geom;

import java.util.NoSuchElementException;

class RoundRectIterator implements PathIterator {
   double x;
   double y;
   double w;
   double h;
   double aw;
   double ah;
   AffineTransform affine;
   int index;
   private static final double angle = 0.7853981633974483D;
   private static final double a = 1.0D - Math.cos(0.7853981633974483D);
   private static final double b = Math.tan(0.7853981633974483D);
   private static final double c;
   private static final double cv;
   private static final double acv;
   private static double[][] ctrlpts;
   private static int[] types;

   RoundRectIterator(RoundRectangle2D var1, AffineTransform var2) {
      this.x = var1.getX();
      this.y = var1.getY();
      this.w = var1.getWidth();
      this.h = var1.getHeight();
      this.aw = Math.min(this.w, Math.abs(var1.getArcWidth()));
      this.ah = Math.min(this.h, Math.abs(var1.getArcHeight()));
      this.affine = var2;
      if (this.aw < 0.0D || this.ah < 0.0D) {
         this.index = ctrlpts.length;
      }

   }

   public int getWindingRule() {
      return 1;
   }

   public boolean isDone() {
      return this.index >= ctrlpts.length;
   }

   public void next() {
      ++this.index;
   }

   public int currentSegment(float[] var1) {
      if (this.isDone()) {
         throw new NoSuchElementException("roundrect iterator out of bounds");
      } else {
         double[] var2 = ctrlpts[this.index];
         int var3 = 0;

         for(int var4 = 0; var4 < var2.length; var4 += 4) {
            var1[var3++] = (float)(this.x + var2[var4 + 0] * this.w + var2[var4 + 1] * this.aw);
            var1[var3++] = (float)(this.y + var2[var4 + 2] * this.h + var2[var4 + 3] * this.ah);
         }

         if (this.affine != null) {
            this.affine.transform((float[])var1, 0, (float[])var1, 0, var3 / 2);
         }

         return types[this.index];
      }
   }

   public int currentSegment(double[] var1) {
      if (this.isDone()) {
         throw new NoSuchElementException("roundrect iterator out of bounds");
      } else {
         double[] var2 = ctrlpts[this.index];
         int var3 = 0;

         for(int var4 = 0; var4 < var2.length; var4 += 4) {
            var1[var3++] = this.x + var2[var4 + 0] * this.w + var2[var4 + 1] * this.aw;
            var1[var3++] = this.y + var2[var4 + 2] * this.h + var2[var4 + 3] * this.ah;
         }

         if (this.affine != null) {
            this.affine.transform((double[])var1, 0, (double[])var1, 0, var3 / 2);
         }

         return types[this.index];
      }
   }

   static {
      c = Math.sqrt(1.0D + b * b) - 1.0D + a;
      cv = 1.3333333333333333D * a * b / c;
      acv = (1.0D - cv) / 2.0D;
      ctrlpts = new double[][]{{0.0D, 0.0D, 0.0D, 0.5D}, {0.0D, 0.0D, 1.0D, -0.5D}, {0.0D, 0.0D, 1.0D, -acv, 0.0D, acv, 1.0D, 0.0D, 0.0D, 0.5D, 1.0D, 0.0D}, {1.0D, -0.5D, 1.0D, 0.0D}, {1.0D, -acv, 1.0D, 0.0D, 1.0D, 0.0D, 1.0D, -acv, 1.0D, 0.0D, 1.0D, -0.5D}, {1.0D, 0.0D, 0.0D, 0.5D}, {1.0D, 0.0D, 0.0D, acv, 1.0D, -acv, 0.0D, 0.0D, 1.0D, -0.5D, 0.0D, 0.0D}, {0.0D, 0.5D, 0.0D, 0.0D}, {0.0D, acv, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, acv, 0.0D, 0.0D, 0.0D, 0.5D}, new double[0]};
      types = new int[]{0, 1, 3, 1, 3, 1, 3, 1, 3, 4};
   }
}
