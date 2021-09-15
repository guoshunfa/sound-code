package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.Serializable;

public abstract class QuadCurve2D implements Shape, Cloneable {
   private static final int BELOW = -2;
   private static final int LOWEDGE = -1;
   private static final int INSIDE = 0;
   private static final int HIGHEDGE = 1;
   private static final int ABOVE = 2;

   protected QuadCurve2D() {
   }

   public abstract double getX1();

   public abstract double getY1();

   public abstract Point2D getP1();

   public abstract double getCtrlX();

   public abstract double getCtrlY();

   public abstract Point2D getCtrlPt();

   public abstract double getX2();

   public abstract double getY2();

   public abstract Point2D getP2();

   public abstract void setCurve(double var1, double var3, double var5, double var7, double var9, double var11);

   public void setCurve(double[] var1, int var2) {
      this.setCurve(var1[var2 + 0], var1[var2 + 1], var1[var2 + 2], var1[var2 + 3], var1[var2 + 4], var1[var2 + 5]);
   }

   public void setCurve(Point2D var1, Point2D var2, Point2D var3) {
      this.setCurve(var1.getX(), var1.getY(), var2.getX(), var2.getY(), var3.getX(), var3.getY());
   }

   public void setCurve(Point2D[] var1, int var2) {
      this.setCurve(var1[var2 + 0].getX(), var1[var2 + 0].getY(), var1[var2 + 1].getX(), var1[var2 + 1].getY(), var1[var2 + 2].getX(), var1[var2 + 2].getY());
   }

   public void setCurve(QuadCurve2D var1) {
      this.setCurve(var1.getX1(), var1.getY1(), var1.getCtrlX(), var1.getCtrlY(), var1.getX2(), var1.getY2());
   }

   public static double getFlatnessSq(double var0, double var2, double var4, double var6, double var8, double var10) {
      return Line2D.ptSegDistSq(var0, var2, var8, var10, var4, var6);
   }

   public static double getFlatness(double var0, double var2, double var4, double var6, double var8, double var10) {
      return Line2D.ptSegDist(var0, var2, var8, var10, var4, var6);
   }

   public static double getFlatnessSq(double[] var0, int var1) {
      return Line2D.ptSegDistSq(var0[var1 + 0], var0[var1 + 1], var0[var1 + 4], var0[var1 + 5], var0[var1 + 2], var0[var1 + 3]);
   }

   public static double getFlatness(double[] var0, int var1) {
      return Line2D.ptSegDist(var0[var1 + 0], var0[var1 + 1], var0[var1 + 4], var0[var1 + 5], var0[var1 + 2], var0[var1 + 3]);
   }

   public double getFlatnessSq() {
      return Line2D.ptSegDistSq(this.getX1(), this.getY1(), this.getX2(), this.getY2(), this.getCtrlX(), this.getCtrlY());
   }

   public double getFlatness() {
      return Line2D.ptSegDist(this.getX1(), this.getY1(), this.getX2(), this.getY2(), this.getCtrlX(), this.getCtrlY());
   }

   public void subdivide(QuadCurve2D var1, QuadCurve2D var2) {
      subdivide(this, var1, var2);
   }

   public static void subdivide(QuadCurve2D var0, QuadCurve2D var1, QuadCurve2D var2) {
      double var3 = var0.getX1();
      double var5 = var0.getY1();
      double var7 = var0.getCtrlX();
      double var9 = var0.getCtrlY();
      double var11 = var0.getX2();
      double var13 = var0.getY2();
      double var15 = (var3 + var7) / 2.0D;
      double var17 = (var5 + var9) / 2.0D;
      double var19 = (var11 + var7) / 2.0D;
      double var21 = (var13 + var9) / 2.0D;
      var7 = (var15 + var19) / 2.0D;
      var9 = (var17 + var21) / 2.0D;
      if (var1 != null) {
         var1.setCurve(var3, var5, var15, var17, var7, var9);
      }

      if (var2 != null) {
         var2.setCurve(var7, var9, var19, var21, var11, var13);
      }

   }

   public static void subdivide(double[] var0, int var1, double[] var2, int var3, double[] var4, int var5) {
      double var6 = var0[var1 + 0];
      double var8 = var0[var1 + 1];
      double var10 = var0[var1 + 2];
      double var12 = var0[var1 + 3];
      double var14 = var0[var1 + 4];
      double var16 = var0[var1 + 5];
      if (var2 != null) {
         var2[var3 + 0] = var6;
         var2[var3 + 1] = var8;
      }

      if (var4 != null) {
         var4[var5 + 4] = var14;
         var4[var5 + 5] = var16;
      }

      var6 = (var6 + var10) / 2.0D;
      var8 = (var8 + var12) / 2.0D;
      var14 = (var14 + var10) / 2.0D;
      var16 = (var16 + var12) / 2.0D;
      var10 = (var6 + var14) / 2.0D;
      var12 = (var8 + var16) / 2.0D;
      if (var2 != null) {
         var2[var3 + 2] = var6;
         var2[var3 + 3] = var8;
         var2[var3 + 4] = var10;
         var2[var3 + 5] = var12;
      }

      if (var4 != null) {
         var4[var5 + 0] = var10;
         var4[var5 + 1] = var12;
         var4[var5 + 2] = var14;
         var4[var5 + 3] = var16;
      }

   }

   public static int solveQuadratic(double[] var0) {
      return solveQuadratic(var0, var0);
   }

   public static int solveQuadratic(double[] var0, double[] var1) {
      double var2 = var0[2];
      double var4 = var0[1];
      double var6 = var0[0];
      byte var8 = 0;
      int var13;
      if (var2 == 0.0D) {
         if (var4 == 0.0D) {
            return -1;
         }

         var13 = var8 + 1;
         var1[var8] = -var6 / var4;
      } else {
         double var9 = var4 * var4 - 4.0D * var2 * var6;
         if (var9 < 0.0D) {
            return 0;
         }

         var9 = Math.sqrt(var9);
         if (var4 < 0.0D) {
            var9 = -var9;
         }

         double var11 = (var4 + var9) / -2.0D;
         var13 = var8 + 1;
         var1[var8] = var11 / var2;
         if (var11 != 0.0D) {
            var1[var13++] = var6 / var11;
         }
      }

      return var13;
   }

   public boolean contains(double var1, double var3) {
      double var5 = this.getX1();
      double var7 = this.getY1();
      double var9 = this.getCtrlX();
      double var11 = this.getCtrlY();
      double var13 = this.getX2();
      double var15 = this.getY2();
      double var17 = var5 - 2.0D * var9 + var13;
      double var19 = var7 - 2.0D * var11 + var15;
      double var21 = var1 - var5;
      double var23 = var3 - var7;
      double var25 = var13 - var5;
      double var27 = var15 - var7;
      double var29 = (var21 * var19 - var23 * var17) / (var25 * var19 - var27 * var17);
      if (var29 >= 0.0D && var29 <= 1.0D && var29 == var29) {
         double var31 = var17 * var29 * var29 + 2.0D * (var9 - var5) * var29 + var5;
         double var33 = var19 * var29 * var29 + 2.0D * (var11 - var7) * var29 + var7;
         double var35 = var25 * var29 + var5;
         double var37 = var27 * var29 + var7;
         return var1 >= var31 && var1 < var35 || var1 >= var35 && var1 < var31 || var3 >= var33 && var3 < var37 || var3 >= var37 && var3 < var33;
      } else {
         return false;
      }
   }

   public boolean contains(Point2D var1) {
      return this.contains(var1.getX(), var1.getY());
   }

   private static void fillEqn(double[] var0, double var1, double var3, double var5, double var7) {
      var0[0] = var3 - var1;
      var0[1] = var5 + var5 - var3 - var3;
      var0[2] = var3 - var5 - var5 + var7;
   }

   private static int evalQuadratic(double[] var0, int var1, boolean var2, boolean var3, double[] var4, double var5, double var7, double var9) {
      int var11 = 0;

      for(int var12 = 0; var12 < var1; ++var12) {
         double var13 = var0[var12];
         if (var2) {
            if (var13 < 0.0D) {
               continue;
            }
         } else if (var13 <= 0.0D) {
            continue;
         }

         if (var3) {
            if (var13 > 1.0D) {
               continue;
            }
         } else if (var13 >= 1.0D) {
            continue;
         }

         if (var4 == null || var4[1] + 2.0D * var4[2] * var13 != 0.0D) {
            double var15 = 1.0D - var13;
            var0[var11++] = var5 * var15 * var15 + 2.0D * var7 * var13 * var15 + var9 * var13 * var13;
         }
      }

      return var11;
   }

   private static int getTag(double var0, double var2, double var4) {
      if (var0 <= var2) {
         return var0 < var2 ? -2 : -1;
      } else if (var0 >= var4) {
         return var0 > var4 ? 2 : 1;
      } else {
         return 0;
      }
   }

   private static boolean inwards(int var0, int var1, int var2) {
      switch(var0) {
      case -2:
      case 2:
      default:
         return false;
      case -1:
         return var1 >= 0 || var2 >= 0;
      case 0:
         return true;
      case 1:
         return var1 <= 0 || var2 <= 0;
      }
   }

   public boolean intersects(double var1, double var3, double var5, double var7) {
      if (var5 > 0.0D && var7 > 0.0D) {
         double var9 = this.getX1();
         double var11 = this.getY1();
         int var13 = getTag(var9, var1, var1 + var5);
         int var14 = getTag(var11, var3, var3 + var7);
         if (var13 == 0 && var14 == 0) {
            return true;
         } else {
            double var15 = this.getX2();
            double var17 = this.getY2();
            int var19 = getTag(var15, var1, var1 + var5);
            int var20 = getTag(var17, var3, var3 + var7);
            if (var19 == 0 && var20 == 0) {
               return true;
            } else {
               double var21 = this.getCtrlX();
               double var23 = this.getCtrlY();
               int var25 = getTag(var21, var1, var1 + var5);
               int var26 = getTag(var23, var3, var3 + var7);
               if (var13 < 0 && var19 < 0 && var25 < 0) {
                  return false;
               } else if (var14 < 0 && var20 < 0 && var26 < 0) {
                  return false;
               } else if (var13 > 0 && var19 > 0 && var25 > 0) {
                  return false;
               } else if (var14 > 0 && var20 > 0 && var26 > 0) {
                  return false;
               } else if (inwards(var13, var19, var25) && inwards(var14, var20, var26)) {
                  return true;
               } else if (inwards(var19, var13, var25) && inwards(var20, var14, var26)) {
                  return true;
               } else {
                  boolean var27 = var13 * var19 <= 0;
                  boolean var28 = var14 * var20 <= 0;
                  if (var13 == 0 && var19 == 0 && var28) {
                     return true;
                  } else if (var14 == 0 && var20 == 0 && var27) {
                     return true;
                  } else {
                     double[] var29 = new double[3];
                     double[] var30 = new double[3];
                     if (!var28) {
                        fillEqn(var29, var14 < 0 ? var3 : var3 + var7, var11, var23, var17);
                        return solveQuadratic(var29, var30) == 2 && evalQuadratic(var30, 2, true, true, (double[])null, var9, var21, var15) == 2 && getTag(var30[0], var1, var1 + var5) * getTag(var30[1], var1, var1 + var5) <= 0;
                     } else if (!var27) {
                        fillEqn(var29, var13 < 0 ? var1 : var1 + var5, var9, var21, var15);
                        return solveQuadratic(var29, var30) == 2 && evalQuadratic(var30, 2, true, true, (double[])null, var11, var23, var17) == 2 && getTag(var30[0], var3, var3 + var7) * getTag(var30[1], var3, var3 + var7) <= 0;
                     } else {
                        double var31 = var15 - var9;
                        double var33 = var17 - var11;
                        double var35 = var17 * var9 - var15 * var11;
                        int var37;
                        if (var14 == 0) {
                           var37 = var13;
                        } else {
                           var37 = getTag((var35 + var31 * (var14 < 0 ? var3 : var3 + var7)) / var33, var1, var1 + var5);
                        }

                        int var38;
                        if (var20 == 0) {
                           var38 = var19;
                        } else {
                           var38 = getTag((var35 + var31 * (var20 < 0 ? var3 : var3 + var7)) / var33, var1, var1 + var5);
                        }

                        if (var37 * var38 <= 0) {
                           return true;
                        } else {
                           var37 = var37 * var13 <= 0 ? var14 : var20;
                           fillEqn(var29, var38 < 0 ? var1 : var1 + var5, var9, var21, var15);
                           int var39 = solveQuadratic(var29, var30);
                           evalQuadratic(var30, var39, true, true, (double[])null, var11, var23, var17);
                           var38 = getTag(var30[0], var3, var3 + var7);
                           return var37 * var38 <= 0;
                        }
                     }
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   public boolean intersects(Rectangle2D var1) {
      return this.intersects(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
   }

   public boolean contains(double var1, double var3, double var5, double var7) {
      if (var5 > 0.0D && var7 > 0.0D) {
         return this.contains(var1, var3) && this.contains(var1 + var5, var3) && this.contains(var1 + var5, var3 + var7) && this.contains(var1, var3 + var7);
      } else {
         return false;
      }
   }

   public boolean contains(Rectangle2D var1) {
      return this.contains(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
   }

   public Rectangle getBounds() {
      return this.getBounds2D().getBounds();
   }

   public PathIterator getPathIterator(AffineTransform var1) {
      return new QuadIterator(this, var1);
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

   public static class Double extends QuadCurve2D implements Serializable {
      public double x1;
      public double y1;
      public double ctrlx;
      public double ctrly;
      public double x2;
      public double y2;
      private static final long serialVersionUID = 4217149928428559721L;

      public Double() {
      }

      public Double(double var1, double var3, double var5, double var7, double var9, double var11) {
         this.setCurve(var1, var3, var5, var7, var9, var11);
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

      public double getCtrlX() {
         return this.ctrlx;
      }

      public double getCtrlY() {
         return this.ctrly;
      }

      public Point2D getCtrlPt() {
         return new Point2D.Double(this.ctrlx, this.ctrly);
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

      public void setCurve(double var1, double var3, double var5, double var7, double var9, double var11) {
         this.x1 = var1;
         this.y1 = var3;
         this.ctrlx = var5;
         this.ctrly = var7;
         this.x2 = var9;
         this.y2 = var11;
      }

      public Rectangle2D getBounds2D() {
         double var1 = Math.min(Math.min(this.x1, this.x2), this.ctrlx);
         double var3 = Math.min(Math.min(this.y1, this.y2), this.ctrly);
         double var5 = Math.max(Math.max(this.x1, this.x2), this.ctrlx);
         double var7 = Math.max(Math.max(this.y1, this.y2), this.ctrly);
         return new Rectangle2D.Double(var1, var3, var5 - var1, var7 - var3);
      }
   }

   public static class Float extends QuadCurve2D implements Serializable {
      public float x1;
      public float y1;
      public float ctrlx;
      public float ctrly;
      public float x2;
      public float y2;
      private static final long serialVersionUID = -8511188402130719609L;

      public Float() {
      }

      public Float(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.setCurve(var1, var2, var3, var4, var5, var6);
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

      public double getCtrlX() {
         return (double)this.ctrlx;
      }

      public double getCtrlY() {
         return (double)this.ctrly;
      }

      public Point2D getCtrlPt() {
         return new Point2D.Float(this.ctrlx, this.ctrly);
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

      public void setCurve(double var1, double var3, double var5, double var7, double var9, double var11) {
         this.x1 = (float)var1;
         this.y1 = (float)var3;
         this.ctrlx = (float)var5;
         this.ctrly = (float)var7;
         this.x2 = (float)var9;
         this.y2 = (float)var11;
      }

      public void setCurve(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.x1 = var1;
         this.y1 = var2;
         this.ctrlx = var3;
         this.ctrly = var4;
         this.x2 = var5;
         this.y2 = var6;
      }

      public Rectangle2D getBounds2D() {
         float var1 = Math.min(Math.min(this.x1, this.x2), this.ctrlx);
         float var2 = Math.min(Math.min(this.y1, this.y2), this.ctrly);
         float var3 = Math.max(Math.max(this.x1, this.x2), this.ctrlx);
         float var4 = Math.max(Math.max(this.y1, this.y2), this.ctrly);
         return new Rectangle2D.Float(var1, var2, var3 - var1, var4 - var2);
      }
   }
}
