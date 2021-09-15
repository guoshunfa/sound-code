package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.Serializable;
import java.util.Arrays;
import sun.awt.geom.Curve;

public abstract class CubicCurve2D implements Shape, Cloneable {
   protected CubicCurve2D() {
   }

   public abstract double getX1();

   public abstract double getY1();

   public abstract Point2D getP1();

   public abstract double getCtrlX1();

   public abstract double getCtrlY1();

   public abstract Point2D getCtrlP1();

   public abstract double getCtrlX2();

   public abstract double getCtrlY2();

   public abstract Point2D getCtrlP2();

   public abstract double getX2();

   public abstract double getY2();

   public abstract Point2D getP2();

   public abstract void setCurve(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15);

   public void setCurve(double[] var1, int var2) {
      this.setCurve(var1[var2 + 0], var1[var2 + 1], var1[var2 + 2], var1[var2 + 3], var1[var2 + 4], var1[var2 + 5], var1[var2 + 6], var1[var2 + 7]);
   }

   public void setCurve(Point2D var1, Point2D var2, Point2D var3, Point2D var4) {
      this.setCurve(var1.getX(), var1.getY(), var2.getX(), var2.getY(), var3.getX(), var3.getY(), var4.getX(), var4.getY());
   }

   public void setCurve(Point2D[] var1, int var2) {
      this.setCurve(var1[var2 + 0].getX(), var1[var2 + 0].getY(), var1[var2 + 1].getX(), var1[var2 + 1].getY(), var1[var2 + 2].getX(), var1[var2 + 2].getY(), var1[var2 + 3].getX(), var1[var2 + 3].getY());
   }

   public void setCurve(CubicCurve2D var1) {
      this.setCurve(var1.getX1(), var1.getY1(), var1.getCtrlX1(), var1.getCtrlY1(), var1.getCtrlX2(), var1.getCtrlY2(), var1.getX2(), var1.getY2());
   }

   public static double getFlatnessSq(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14) {
      return Math.max(Line2D.ptSegDistSq(var0, var2, var12, var14, var4, var6), Line2D.ptSegDistSq(var0, var2, var12, var14, var8, var10));
   }

   public static double getFlatness(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14) {
      return Math.sqrt(getFlatnessSq(var0, var2, var4, var6, var8, var10, var12, var14));
   }

   public static double getFlatnessSq(double[] var0, int var1) {
      return getFlatnessSq(var0[var1 + 0], var0[var1 + 1], var0[var1 + 2], var0[var1 + 3], var0[var1 + 4], var0[var1 + 5], var0[var1 + 6], var0[var1 + 7]);
   }

   public static double getFlatness(double[] var0, int var1) {
      return getFlatness(var0[var1 + 0], var0[var1 + 1], var0[var1 + 2], var0[var1 + 3], var0[var1 + 4], var0[var1 + 5], var0[var1 + 6], var0[var1 + 7]);
   }

   public double getFlatnessSq() {
      return getFlatnessSq(this.getX1(), this.getY1(), this.getCtrlX1(), this.getCtrlY1(), this.getCtrlX2(), this.getCtrlY2(), this.getX2(), this.getY2());
   }

   public double getFlatness() {
      return getFlatness(this.getX1(), this.getY1(), this.getCtrlX1(), this.getCtrlY1(), this.getCtrlX2(), this.getCtrlY2(), this.getX2(), this.getY2());
   }

   public void subdivide(CubicCurve2D var1, CubicCurve2D var2) {
      subdivide(this, var1, var2);
   }

   public static void subdivide(CubicCurve2D var0, CubicCurve2D var1, CubicCurve2D var2) {
      double var3 = var0.getX1();
      double var5 = var0.getY1();
      double var7 = var0.getCtrlX1();
      double var9 = var0.getCtrlY1();
      double var11 = var0.getCtrlX2();
      double var13 = var0.getCtrlY2();
      double var15 = var0.getX2();
      double var17 = var0.getY2();
      double var19 = (var7 + var11) / 2.0D;
      double var21 = (var9 + var13) / 2.0D;
      var7 = (var3 + var7) / 2.0D;
      var9 = (var5 + var9) / 2.0D;
      var11 = (var15 + var11) / 2.0D;
      var13 = (var17 + var13) / 2.0D;
      double var23 = (var7 + var19) / 2.0D;
      double var25 = (var9 + var21) / 2.0D;
      double var27 = (var11 + var19) / 2.0D;
      double var29 = (var13 + var21) / 2.0D;
      var19 = (var23 + var27) / 2.0D;
      var21 = (var25 + var29) / 2.0D;
      if (var1 != null) {
         var1.setCurve(var3, var5, var7, var9, var23, var25, var19, var21);
      }

      if (var2 != null) {
         var2.setCurve(var19, var21, var27, var29, var11, var13, var15, var17);
      }

   }

   public static void subdivide(double[] var0, int var1, double[] var2, int var3, double[] var4, int var5) {
      double var6 = var0[var1 + 0];
      double var8 = var0[var1 + 1];
      double var10 = var0[var1 + 2];
      double var12 = var0[var1 + 3];
      double var14 = var0[var1 + 4];
      double var16 = var0[var1 + 5];
      double var18 = var0[var1 + 6];
      double var20 = var0[var1 + 7];
      if (var2 != null) {
         var2[var3 + 0] = var6;
         var2[var3 + 1] = var8;
      }

      if (var4 != null) {
         var4[var5 + 6] = var18;
         var4[var5 + 7] = var20;
      }

      var6 = (var6 + var10) / 2.0D;
      var8 = (var8 + var12) / 2.0D;
      var18 = (var18 + var14) / 2.0D;
      var20 = (var20 + var16) / 2.0D;
      double var22 = (var10 + var14) / 2.0D;
      double var24 = (var12 + var16) / 2.0D;
      var10 = (var6 + var22) / 2.0D;
      var12 = (var8 + var24) / 2.0D;
      var14 = (var18 + var22) / 2.0D;
      var16 = (var20 + var24) / 2.0D;
      var22 = (var10 + var14) / 2.0D;
      var24 = (var12 + var16) / 2.0D;
      if (var2 != null) {
         var2[var3 + 2] = var6;
         var2[var3 + 3] = var8;
         var2[var3 + 4] = var10;
         var2[var3 + 5] = var12;
         var2[var3 + 6] = var22;
         var2[var3 + 7] = var24;
      }

      if (var4 != null) {
         var4[var5 + 0] = var22;
         var4[var5 + 1] = var24;
         var4[var5 + 2] = var14;
         var4[var5 + 3] = var16;
         var4[var5 + 4] = var18;
         var4[var5 + 5] = var20;
      }

   }

   public static int solveCubic(double[] var0) {
      return solveCubic(var0, var0);
   }

   public static int solveCubic(double[] var0, double[] var1) {
      double var2 = var0[3];
      if (var2 == 0.0D) {
         return QuadCurve2D.solveQuadratic(var0, var1);
      } else {
         double var4 = var0[2] / var2;
         double var6 = var0[1] / var2;
         double var8 = var0[0] / var2;
         double var10 = var4 * var4;
         double var12 = 0.3333333333333333D * (-0.3333333333333333D * var10 + var6);
         double var14 = 0.5D * (0.07407407407407407D * var4 * var10 - 0.3333333333333333D * var4 * var6 + var8);
         double var16 = var12 * var12 * var12;
         double var18 = var14 * var14 + var16;
         double var20 = 0.3333333333333333D * var4;
         int var22;
         double var23;
         double var25;
         if (var18 < 0.0D) {
            var23 = 0.3333333333333333D * Math.acos(-var14 / Math.sqrt(-var16));
            var25 = 2.0D * Math.sqrt(-var12);
            if (var1 == var0) {
               var0 = Arrays.copyOf((double[])var0, 4);
            }

            var1[0] = var25 * Math.cos(var23);
            var1[1] = -var25 * Math.cos(var23 + 1.0471975511965976D);
            var1[2] = -var25 * Math.cos(var23 - 1.0471975511965976D);
            var22 = 3;

            for(int var27 = 0; var27 < var22; ++var27) {
               var1[var27] -= var20;
            }
         } else {
            var23 = Math.sqrt(var18);
            var25 = Math.cbrt(var23 - var14);
            double var33 = -Math.cbrt(var23 + var14);
            double var29 = var25 + var33;
            var22 = 1;
            double var31 = 1.2E9D * Math.ulp(Math.abs(var29) + Math.abs(var20));
            if (iszero(var18, var31) || within(var25, var33, var31)) {
               if (var1 == var0) {
                  var0 = Arrays.copyOf((double[])var0, 4);
               }

               var1[1] = -(var29 / 2.0D) - var20;
               var22 = 2;
            }

            var1[0] = var29 - var20;
         }

         if (var22 > 1) {
            var22 = fixRoots(var0, var1, var22);
         }

         if (var22 > 2 && (var1[2] == var1[1] || var1[2] == var1[0])) {
            --var22;
         }

         if (var22 > 1 && var1[1] == var1[0]) {
            --var22;
            var1[1] = var1[var22];
         }

         return var22;
      }
   }

   private static int fixRoots(double[] var0, double[] var1, int var2) {
      double[] var3 = new double[]{var0[1], 2.0D * var0[2], 3.0D * var0[3]};
      int var4 = QuadCurve2D.solveQuadratic(var3, var3);
      if (var4 == 2 && var3[0] == var3[1]) {
         --var4;
      }

      double var5;
      if (var4 == 2 && var3[0] > var3[1]) {
         var5 = var3[0];
         var3[0] = var3[1];
         var3[1] = var5;
      }

      double var7;
      double var9;
      double var11;
      double var13;
      double var15;
      if (var2 == 3) {
         var5 = getRootUpperBound(var0);
         var7 = -var5;
         Arrays.sort((double[])var1, 0, var2);
         if (var4 == 2) {
            var1[0] = refineRootWithHint(var0, var7, var3[0], var1[0]);
            var1[1] = refineRootWithHint(var0, var3[0], var3[1], var1[1]);
            var1[2] = refineRootWithHint(var0, var3[1], var5, var1[2]);
            return 3;
         }

         if (var4 == 1) {
            var9 = var0[3];
            var11 = -var9;
            var13 = var3[0];
            var15 = solveEqn(var0, 3, var13);
            if (oppositeSigns(var11, var15)) {
               var1[0] = bisectRootWithHint(var0, var7, var13, var1[0]);
            } else if (oppositeSigns(var15, var9)) {
               var1[0] = bisectRootWithHint(var0, var13, var5, var1[2]);
            } else {
               var1[0] = var13;
            }
         } else if (var4 == 0) {
            var1[0] = bisectRootWithHint(var0, var7, var5, var1[1]);
         }
      } else if (var2 == 2 && var4 == 2) {
         var5 = var1[0];
         var7 = var1[1];
         var9 = var3[0];
         var11 = var3[1];
         var13 = Math.abs(var9 - var5) > Math.abs(var11 - var5) ? var9 : var11;
         var15 = solveEqn(var0, 3, var13);
         if (iszero(var15, 1.0E7D * Math.ulp(var13))) {
            double var17 = solveEqn(var0, 3, var7);
            var1[1] = Math.abs(var17) < Math.abs(var15) ? var7 : var13;
            return 2;
         }
      }

      return 1;
   }

   private static double refineRootWithHint(double[] var0, double var1, double var3, double var5) {
      if (!inInterval(var5, var1, var3)) {
         return var5;
      } else {
         double[] var7 = new double[]{var0[1], 2.0D * var0[2], 3.0D * var0[3]};
         double var8 = var5;

         for(int var10 = 0; var10 < 3; ++var10) {
            double var11 = solveEqn(var7, 2, var5);
            double var13 = solveEqn(var0, 3, var5);
            double var15 = -(var13 / var11);
            double var17 = var5 + var15;
            if (var11 == 0.0D || var13 == 0.0D || var5 == var17) {
               break;
            }

            var5 = var17;
         }

         return within(var5, var8, 1000.0D * Math.ulp(var8)) && inInterval(var5, var1, var3) ? var5 : var8;
      }
   }

   private static double bisectRootWithHint(double[] var0, double var1, double var3, double var5) {
      double var7 = Math.min(Math.abs(var5 - var1) / 64.0D, 0.0625D);
      double var9 = Math.min(Math.abs(var5 - var3) / 64.0D, 0.0625D);
      double var11 = var5 - var7;
      double var13 = var5 + var9;
      double var15 = solveEqn(var0, 3, var11);

      double var17;
      for(var17 = solveEqn(var0, 3, var13); oppositeSigns(var15, var17); var17 = solveEqn(var0, 3, var13)) {
         if (var11 >= var13) {
            return var11;
         }

         var1 = var11;
         var3 = var13;
         var7 /= 64.0D;
         var9 /= 64.0D;
         var11 = var5 - var7;
         var13 = var5 + var9;
         var15 = solveEqn(var0, 3, var11);
      }

      if (var15 == 0.0D) {
         return var11;
      } else if (var17 == 0.0D) {
         return var13;
      } else {
         return bisectRoot(var0, var1, var3);
      }
   }

   private static double bisectRoot(double[] var0, double var1, double var3) {
      double var5 = solveEqn(var0, 3, var1);

      double var7;
      for(var7 = var1 + (var3 - var1) / 2.0D; var7 != var1 && var7 != var3; var7 = var1 + (var3 - var1) / 2.0D) {
         double var9 = solveEqn(var0, 3, var7);
         if (var9 == 0.0D) {
            return var7;
         }

         if (oppositeSigns(var5, var9)) {
            var3 = var7;
         } else {
            var5 = var9;
            var1 = var7;
         }
      }

      return var7;
   }

   private static boolean inInterval(double var0, double var2, double var4) {
      return var2 <= var0 && var0 <= var4;
   }

   private static boolean within(double var0, double var2, double var4) {
      double var6 = var2 - var0;
      return var6 <= var4 && var6 >= -var4;
   }

   private static boolean iszero(double var0, double var2) {
      return within(var0, 0.0D, var2);
   }

   private static boolean oppositeSigns(double var0, double var2) {
      return var0 < 0.0D && var2 > 0.0D || var0 > 0.0D && var2 < 0.0D;
   }

   private static double solveEqn(double[] var0, int var1, double var2) {
      double var4 = var0[var1];

      while(true) {
         --var1;
         if (var1 < 0) {
            return var4;
         }

         var4 = var4 * var2 + var0[var1];
      }
   }

   private static double getRootUpperBound(double[] var0) {
      double var1 = var0[3];
      double var3 = var0[2];
      double var5 = var0[1];
      double var7 = var0[0];
      double var9 = 1.0D + Math.max(Math.max(Math.abs(var3), Math.abs(var5)), Math.abs(var7)) / Math.abs(var1);
      var9 += Math.ulp(var9) + 1.0D;
      return var9;
   }

   public boolean contains(double var1, double var3) {
      if (var1 * 0.0D + var3 * 0.0D != 0.0D) {
         return false;
      } else {
         double var5 = this.getX1();
         double var7 = this.getY1();
         double var9 = this.getX2();
         double var11 = this.getY2();
         int var13 = Curve.pointCrossingsForLine(var1, var3, var5, var7, var9, var11) + Curve.pointCrossingsForCubic(var1, var3, var5, var7, this.getCtrlX1(), this.getCtrlY1(), this.getCtrlX2(), this.getCtrlY2(), var9, var11, 0);
         return (var13 & 1) == 1;
      }
   }

   public boolean contains(Point2D var1) {
      return this.contains(var1.getX(), var1.getY());
   }

   public boolean intersects(double var1, double var3, double var5, double var7) {
      if (var5 > 0.0D && var7 > 0.0D) {
         int var9 = this.rectCrossings(var1, var3, var5, var7);
         return var9 != 0;
      } else {
         return false;
      }
   }

   public boolean intersects(Rectangle2D var1) {
      return this.intersects(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
   }

   public boolean contains(double var1, double var3, double var5, double var7) {
      if (var5 > 0.0D && var7 > 0.0D) {
         int var9 = this.rectCrossings(var1, var3, var5, var7);
         return var9 != 0 && var9 != Integer.MIN_VALUE;
      } else {
         return false;
      }
   }

   private int rectCrossings(double var1, double var3, double var5, double var7) {
      int var9 = 0;
      if (this.getX1() != this.getX2() || this.getY1() != this.getY2()) {
         var9 = Curve.rectCrossingsForLine(var9, var1, var3, var1 + var5, var3 + var7, this.getX1(), this.getY1(), this.getX2(), this.getY2());
         if (var9 == Integer.MIN_VALUE) {
            return var9;
         }
      }

      return Curve.rectCrossingsForCubic(var9, var1, var3, var1 + var5, var3 + var7, this.getX2(), this.getY2(), this.getCtrlX2(), this.getCtrlY2(), this.getCtrlX1(), this.getCtrlY1(), this.getX1(), this.getY1(), 0);
   }

   public boolean contains(Rectangle2D var1) {
      return this.contains(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
   }

   public Rectangle getBounds() {
      return this.getBounds2D().getBounds();
   }

   public PathIterator getPathIterator(AffineTransform var1) {
      return new CubicIterator(this, var1);
   }

   public PathIterator getPathIterator(AffineTransform var1, double var2) {
      return new FlatteningPathIterator(this.getPathIterator(var1), var2);
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   public static class Double extends CubicCurve2D implements Serializable {
      public double x1;
      public double y1;
      public double ctrlx1;
      public double ctrly1;
      public double ctrlx2;
      public double ctrly2;
      public double x2;
      public double y2;
      private static final long serialVersionUID = -4202960122839707295L;

      public Double() {
      }

      public Double(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15) {
         this.setCurve(var1, var3, var5, var7, var9, var11, var13, var15);
      }

      public double getX1() {
         return this.x1;
      }

      public double getY1() {
         return this.y1;
      }

      public Point2D getP1() {
         return new Point2D.Double(this.x1, this.y1);
      }

      public double getCtrlX1() {
         return this.ctrlx1;
      }

      public double getCtrlY1() {
         return this.ctrly1;
      }

      public Point2D getCtrlP1() {
         return new Point2D.Double(this.ctrlx1, this.ctrly1);
      }

      public double getCtrlX2() {
         return this.ctrlx2;
      }

      public double getCtrlY2() {
         return this.ctrly2;
      }

      public Point2D getCtrlP2() {
         return new Point2D.Double(this.ctrlx2, this.ctrly2);
      }

      public double getX2() {
         return this.x2;
      }

      public double getY2() {
         return this.y2;
      }

      public Point2D getP2() {
         return new Point2D.Double(this.x2, this.y2);
      }

      public void setCurve(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15) {
         this.x1 = var1;
         this.y1 = var3;
         this.ctrlx1 = var5;
         this.ctrly1 = var7;
         this.ctrlx2 = var9;
         this.ctrly2 = var11;
         this.x2 = var13;
         this.y2 = var15;
      }

      public Rectangle2D getBounds2D() {
         double var1 = Math.min(Math.min(this.x1, this.x2), Math.min(this.ctrlx1, this.ctrlx2));
         double var3 = Math.min(Math.min(this.y1, this.y2), Math.min(this.ctrly1, this.ctrly2));
         double var5 = Math.max(Math.max(this.x1, this.x2), Math.max(this.ctrlx1, this.ctrlx2));
         double var7 = Math.max(Math.max(this.y1, this.y2), Math.max(this.ctrly1, this.ctrly2));
         return new Rectangle2D.Double(var1, var3, var5 - var1, var7 - var3);
      }
   }

   public static class Float extends CubicCurve2D implements Serializable {
      public float x1;
      public float y1;
      public float ctrlx1;
      public float ctrly1;
      public float ctrlx2;
      public float ctrly2;
      public float x2;
      public float y2;
      private static final long serialVersionUID = -1272015596714244385L;

      public Float() {
      }

      public Float(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         this.setCurve(var1, var2, var3, var4, var5, var6, var7, var8);
      }

      public double getX1() {
         return (double)this.x1;
      }

      public double getY1() {
         return (double)this.y1;
      }

      public Point2D getP1() {
         return new Point2D.Float(this.x1, this.y1);
      }

      public double getCtrlX1() {
         return (double)this.ctrlx1;
      }

      public double getCtrlY1() {
         return (double)this.ctrly1;
      }

      public Point2D getCtrlP1() {
         return new Point2D.Float(this.ctrlx1, this.ctrly1);
      }

      public double getCtrlX2() {
         return (double)this.ctrlx2;
      }

      public double getCtrlY2() {
         return (double)this.ctrly2;
      }

      public Point2D getCtrlP2() {
         return new Point2D.Float(this.ctrlx2, this.ctrly2);
      }

      public double getX2() {
         return (double)this.x2;
      }

      public double getY2() {
         return (double)this.y2;
      }

      public Point2D getP2() {
         return new Point2D.Float(this.x2, this.y2);
      }

      public void setCurve(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15) {
         this.x1 = (float)var1;
         this.y1 = (float)var3;
         this.ctrlx1 = (float)var5;
         this.ctrly1 = (float)var7;
         this.ctrlx2 = (float)var9;
         this.ctrly2 = (float)var11;
         this.x2 = (float)var13;
         this.y2 = (float)var15;
      }

      public void setCurve(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         this.x1 = var1;
         this.y1 = var2;
         this.ctrlx1 = var3;
         this.ctrly1 = var4;
         this.ctrlx2 = var5;
         this.ctrly2 = var6;
         this.x2 = var7;
         this.y2 = var8;
      }

      public Rectangle2D getBounds2D() {
         float var1 = Math.min(Math.min(this.x1, this.x2), Math.min(this.ctrlx1, this.ctrlx2));
         float var2 = Math.min(Math.min(this.y1, this.y2), Math.min(this.ctrly1, this.ctrly2));
         float var3 = Math.max(Math.max(this.x1, this.x2), Math.max(this.ctrlx1, this.ctrlx2));
         float var4 = Math.max(Math.max(this.y1, this.y2), Math.max(this.ctrly1, this.ctrly2));
         return new Rectangle2D.Float(var1, var2, var3 - var1, var4 - var2);
      }
   }
}
