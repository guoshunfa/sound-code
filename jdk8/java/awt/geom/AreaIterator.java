package java.awt.geom;

import java.util.NoSuchElementException;
import java.util.Vector;
import sun.awt.geom.Curve;

class AreaIterator implements PathIterator {
   private AffineTransform transform;
   private Vector curves;
   private int index;
   private Curve prevcurve;
   private Curve thiscurve;

   public AreaIterator(Vector var1, AffineTransform var2) {
      this.curves = var1;
      this.transform = var2;
      if (var1.size() >= 1) {
         this.thiscurve = (Curve)var1.get(0);
      }

   }

   public int getWindingRule() {
      return 1;
   }

   public boolean isDone() {
      return this.prevcurve == null && this.thiscurve == null;
   }

   public void next() {
      if (this.prevcurve != null) {
         this.prevcurve = null;
      } else {
         this.prevcurve = this.thiscurve;
         ++this.index;
         if (this.index < this.curves.size()) {
            this.thiscurve = (Curve)this.curves.get(this.index);
            if (this.thiscurve.getOrder() != 0 && this.prevcurve.getX1() == this.thiscurve.getX0() && this.prevcurve.getY1() == this.thiscurve.getY0()) {
               this.prevcurve = null;
            }
         } else {
            this.thiscurve = null;
         }
      }

   }

   public int currentSegment(float[] var1) {
      double[] var2 = new double[6];
      int var3 = this.currentSegment(var2);
      int var4 = var3 == 4 ? 0 : (var3 == 2 ? 2 : (var3 == 3 ? 3 : 1));

      for(int var5 = 0; var5 < var4 * 2; ++var5) {
         var1[var5] = (float)var2[var5];
      }

      return var3;
   }

   public int currentSegment(double[] var1) {
      int var2;
      int var3;
      if (this.prevcurve != null) {
         if (this.thiscurve == null || this.thiscurve.getOrder() == 0) {
            return 4;
         }

         var1[0] = this.thiscurve.getX0();
         var1[1] = this.thiscurve.getY0();
         var2 = 1;
         var3 = 1;
      } else {
         if (this.thiscurve == null) {
            throw new NoSuchElementException("area iterator out of bounds");
         }

         var2 = this.thiscurve.getSegment(var1);
         var3 = this.thiscurve.getOrder();
         if (var3 == 0) {
            var3 = 1;
         }
      }

      if (this.transform != null) {
         this.transform.transform((double[])var1, 0, (double[])var1, 0, var3);
      }

      return var2;
   }
}
