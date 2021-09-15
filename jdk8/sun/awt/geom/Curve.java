package sun.awt.geom;

import java.awt.geom.IllegalPathStateException;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

public abstract class Curve {
   public static final int INCREASING = 1;
   public static final int DECREASING = -1;
   protected int direction;
   public static final int RECT_INTERSECTS = Integer.MIN_VALUE;
   public static final double TMIN = 0.001D;

   public static void insertMove(Vector var0, double var1, double var3) {
      var0.add(new Order0(var1, var3));
   }

   public static void insertLine(Vector var0, double var1, double var3, double var5, double var7) {
      if (var3 < var7) {
         var0.add(new Order1(var1, var3, var5, var7, 1));
      } else if (var3 > var7) {
         var0.add(new Order1(var5, var7, var1, var3, -1));
      }

   }

   public static void insertQuad(Vector var0, double var1, double var3, double[] var5) {
      double var6 = var5[3];
      if (var3 > var6) {
         Order2.insert(var0, var5, var5[2], var6, var5[0], var5[1], var1, var3, -1);
      } else {
         if (var3 == var6 && var3 == var5[1]) {
            return;
         }

         Order2.insert(var0, var5, var1, var3, var5[0], var5[1], var5[2], var6, 1);
      }

   }

   public static void insertCubic(Vector var0, double var1, double var3, double[] var5) {
      double var6 = var5[5];
      if (var3 > var6) {
         Order3.insert(var0, var5, var5[4], var6, var5[2], var5[3], var5[0], var5[1], var1, var3, -1);
      } else {
         if (var3 == var6 && var3 == var5[1] && var3 == var5[3]) {
            return;
         }

         Order3.insert(var0, var5, var1, var3, var5[0], var5[1], var5[2], var5[3], var5[4], var6, 1);
      }

   }

   public static int pointCrossingsForPath(PathIterator var0, double var1, double var3) {
      if (var0.isDone()) {
         return 0;
      } else {
         double[] var5 = new double[6];
         if (var0.currentSegment(var5) != 0) {
            throw new IllegalPathStateException("missing initial moveto in path definition");
         } else {
            var0.next();
            double var6 = var5[0];
            double var8 = var5[1];
            double var10 = var6;
            double var12 = var8;

            int var18;
            for(var18 = 0; !var0.isDone(); var0.next()) {
               double var16;
               double var14;
               switch(var0.currentSegment(var5)) {
               case 0:
                  if (var12 != var8) {
                     var18 += pointCrossingsForLine(var1, var3, var10, var12, var6, var8);
                  }

                  var6 = var10 = var5[0];
                  var8 = var12 = var5[1];
                  break;
               case 1:
                  var14 = var5[0];
                  var16 = var5[1];
                  var18 += pointCrossingsForLine(var1, var3, var10, var12, var14, var16);
                  var10 = var14;
                  var12 = var16;
                  break;
               case 2:
                  var14 = var5[2];
                  var16 = var5[3];
                  var18 += pointCrossingsForQuad(var1, var3, var10, var12, var5[0], var5[1], var14, var16, 0);
                  var10 = var14;
                  var12 = var16;
                  break;
               case 3:
                  var14 = var5[4];
                  var16 = var5[5];
                  var18 += pointCrossingsForCubic(var1, var3, var10, var12, var5[0], var5[1], var5[2], var5[3], var14, var16, 0);
                  var10 = var14;
                  var12 = var16;
                  break;
               case 4:
                  if (var12 != var8) {
                     var18 += pointCrossingsForLine(var1, var3, var10, var12, var6, var8);
                  }

                  var10 = var6;
                  var12 = var8;
               }
            }

            if (var12 != var8) {
               var18 += pointCrossingsForLine(var1, var3, var10, var12, var6, var8);
            }

            return var18;
         }
      }
   }

   public static int pointCrossingsForLine(double var0, double var2, double var4, double var6, double var8, double var10) {
      if (var2 < var6 && var2 < var10) {
         return 0;
      } else if (var2 >= var6 && var2 >= var10) {
         return 0;
      } else if (var0 >= var4 && var0 >= var8) {
         return 0;
      } else if (var0 < var4 && var0 < var8) {
         return var6 < var10 ? 1 : -1;
      } else {
         double var12 = var4 + (var2 - var6) * (var8 - var4) / (var10 - var6);
         if (var0 >= var12) {
            return 0;
         } else {
            return var6 < var10 ? 1 : -1;
         }
      }
   }

   public static int pointCrossingsForQuad(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, int var16) {
      if (var2 < var6 && var2 < var10 && var2 < var14) {
         return 0;
      } else if (var2 >= var6 && var2 >= var10 && var2 >= var14) {
         return 0;
      } else if (var0 >= var4 && var0 >= var8 && var0 >= var12) {
         return 0;
      } else if (var0 < var4 && var0 < var8 && var0 < var12) {
         if (var2 >= var6) {
            if (var2 < var14) {
               return 1;
            }
         } else if (var2 >= var14) {
            return -1;
         }

         return 0;
      } else if (var16 > 52) {
         return pointCrossingsForLine(var0, var2, var4, var6, var12, var14);
      } else {
         double var17 = (var4 + var8) / 2.0D;
         double var19 = (var6 + var10) / 2.0D;
         double var21 = (var8 + var12) / 2.0D;
         double var23 = (var10 + var14) / 2.0D;
         var8 = (var17 + var21) / 2.0D;
         var10 = (var19 + var23) / 2.0D;
         return !Double.isNaN(var8) && !Double.isNaN(var10) ? pointCrossingsForQuad(var0, var2, var4, var6, var17, var19, var8, var10, var16 + 1) + pointCrossingsForQuad(var0, var2, var8, var10, var21, var23, var12, var14, var16 + 1) : 0;
      }
   }

   public static int pointCrossingsForCubic(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, int var20) {
      if (var2 < var6 && var2 < var10 && var2 < var14 && var2 < var18) {
         return 0;
      } else if (var2 >= var6 && var2 >= var10 && var2 >= var14 && var2 >= var18) {
         return 0;
      } else if (var0 >= var4 && var0 >= var8 && var0 >= var12 && var0 >= var16) {
         return 0;
      } else if (var0 < var4 && var0 < var8 && var0 < var12 && var0 < var16) {
         if (var2 >= var6) {
            if (var2 < var18) {
               return 1;
            }
         } else if (var2 >= var18) {
            return -1;
         }

         return 0;
      } else if (var20 > 52) {
         return pointCrossingsForLine(var0, var2, var4, var6, var16, var18);
      } else {
         double var21 = (var8 + var12) / 2.0D;
         double var23 = (var10 + var14) / 2.0D;
         var8 = (var4 + var8) / 2.0D;
         var10 = (var6 + var10) / 2.0D;
         var12 = (var12 + var16) / 2.0D;
         var14 = (var14 + var18) / 2.0D;
         double var25 = (var8 + var21) / 2.0D;
         double var27 = (var10 + var23) / 2.0D;
         double var29 = (var21 + var12) / 2.0D;
         double var31 = (var23 + var14) / 2.0D;
         var21 = (var25 + var29) / 2.0D;
         var23 = (var27 + var31) / 2.0D;
         return !Double.isNaN(var21) && !Double.isNaN(var23) ? pointCrossingsForCubic(var0, var2, var4, var6, var8, var10, var25, var27, var21, var23, var20 + 1) + pointCrossingsForCubic(var0, var2, var21, var23, var29, var31, var12, var14, var16, var18, var20 + 1) : 0;
      }
   }

   public static int rectCrossingsForPath(PathIterator var0, double var1, double var3, double var5, double var7) {
      if (var5 > var1 && var7 > var3) {
         if (var0.isDone()) {
            return 0;
         } else {
            double[] var9 = new double[6];
            if (var0.currentSegment(var9) != 0) {
               throw new IllegalPathStateException("missing initial moveto in path definition");
            } else {
               var0.next();
               double var14;
               double var10 = var14 = var9[0];
               double var16;
               double var12 = var16 = var9[1];

               int var22;
               for(var22 = 0; var22 != Integer.MIN_VALUE && !var0.isDone(); var0.next()) {
                  double var18;
                  double var20;
                  switch(var0.currentSegment(var9)) {
                  case 0:
                     if (var10 != var14 || var12 != var16) {
                        var22 = rectCrossingsForLine(var22, var1, var3, var5, var7, var10, var12, var14, var16);
                     }

                     var14 = var10 = var9[0];
                     var16 = var12 = var9[1];
                     break;
                  case 1:
                     var18 = var9[0];
                     var20 = var9[1];
                     var22 = rectCrossingsForLine(var22, var1, var3, var5, var7, var10, var12, var18, var20);
                     var10 = var18;
                     var12 = var20;
                     break;
                  case 2:
                     var18 = var9[2];
                     var20 = var9[3];
                     var22 = rectCrossingsForQuad(var22, var1, var3, var5, var7, var10, var12, var9[0], var9[1], var18, var20, 0);
                     var10 = var18;
                     var12 = var20;
                     break;
                  case 3:
                     var18 = var9[4];
                     var20 = var9[5];
                     var22 = rectCrossingsForCubic(var22, var1, var3, var5, var7, var10, var12, var9[0], var9[1], var9[2], var9[3], var18, var20, 0);
                     var10 = var18;
                     var12 = var20;
                     break;
                  case 4:
                     if (var10 != var14 || var12 != var16) {
                        var22 = rectCrossingsForLine(var22, var1, var3, var5, var7, var10, var12, var14, var16);
                     }

                     var10 = var14;
                     var12 = var16;
                  }
               }

               if (var22 != Integer.MIN_VALUE && (var10 != var14 || var12 != var16)) {
                  var22 = rectCrossingsForLine(var22, var1, var3, var5, var7, var10, var12, var14, var16);
               }

               return var22;
            }
         }
      } else {
         return 0;
      }
   }

   public static int rectCrossingsForLine(int var0, double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15) {
      if (var11 >= var7 && var15 >= var7) {
         return var0;
      } else if (var11 <= var3 && var15 <= var3) {
         return var0;
      } else if (var9 <= var1 && var13 <= var1) {
         return var0;
      } else if (var9 >= var5 && var13 >= var5) {
         if (var11 < var15) {
            if (var11 <= var3) {
               ++var0;
            }

            if (var15 >= var7) {
               ++var0;
            }
         } else if (var15 < var11) {
            if (var15 <= var3) {
               --var0;
            }

            if (var11 >= var7) {
               --var0;
            }
         }

         return var0;
      } else if (var9 > var1 && var9 < var5 && var11 > var3 && var11 < var7 || var13 > var1 && var13 < var5 && var15 > var3 && var15 < var7) {
         return Integer.MIN_VALUE;
      } else {
         double var17 = var9;
         if (var11 < var3) {
            var17 = var9 + (var3 - var11) * (var13 - var9) / (var15 - var11);
         } else if (var11 > var7) {
            var17 = var9 + (var7 - var11) * (var13 - var9) / (var15 - var11);
         }

         double var19 = var13;
         if (var15 < var3) {
            var19 = var13 + (var3 - var15) * (var9 - var13) / (var11 - var15);
         } else if (var15 > var7) {
            var19 = var13 + (var7 - var15) * (var9 - var13) / (var11 - var15);
         }

         if (var17 <= var1 && var19 <= var1) {
            return var0;
         } else if (var17 >= var5 && var19 >= var5) {
            if (var11 < var15) {
               if (var11 <= var3) {
                  ++var0;
               }

               if (var15 >= var7) {
                  ++var0;
               }
            } else if (var15 < var11) {
               if (var15 <= var3) {
                  --var0;
               }

               if (var11 >= var7) {
                  --var0;
               }
            }

            return var0;
         } else {
            return Integer.MIN_VALUE;
         }
      }
   }

   public static int rectCrossingsForQuad(int var0, double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19, int var21) {
      if (var11 >= var7 && var15 >= var7 && var19 >= var7) {
         return var0;
      } else if (var11 <= var3 && var15 <= var3 && var19 <= var3) {
         return var0;
      } else if (var9 <= var1 && var13 <= var1 && var17 <= var1) {
         return var0;
      } else if (var9 >= var5 && var13 >= var5 && var17 >= var5) {
         if (var11 < var19) {
            if (var11 <= var3 && var19 > var3) {
               ++var0;
            }

            if (var11 < var7 && var19 >= var7) {
               ++var0;
            }
         } else if (var19 < var11) {
            if (var19 <= var3 && var11 > var3) {
               --var0;
            }

            if (var19 < var7 && var11 >= var7) {
               --var0;
            }
         }

         return var0;
      } else if (var9 < var5 && var9 > var1 && var11 < var7 && var11 > var3 || var17 < var5 && var17 > var1 && var19 < var7 && var19 > var3) {
         return Integer.MIN_VALUE;
      } else if (var21 > 52) {
         return rectCrossingsForLine(var0, var1, var3, var5, var7, var9, var11, var17, var19);
      } else {
         double var22 = (var9 + var13) / 2.0D;
         double var24 = (var11 + var15) / 2.0D;
         double var26 = (var13 + var17) / 2.0D;
         double var28 = (var15 + var19) / 2.0D;
         var13 = (var22 + var26) / 2.0D;
         var15 = (var24 + var28) / 2.0D;
         if (!Double.isNaN(var13) && !Double.isNaN(var15)) {
            var0 = rectCrossingsForQuad(var0, var1, var3, var5, var7, var9, var11, var22, var24, var13, var15, var21 + 1);
            if (var0 != Integer.MIN_VALUE) {
               var0 = rectCrossingsForQuad(var0, var1, var3, var5, var7, var13, var15, var26, var28, var17, var19, var21 + 1);
            }

            return var0;
         } else {
            return 0;
         }
      }
   }

   public static int rectCrossingsForCubic(int var0, double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19, double var21, double var23, int var25) {
      if (var11 >= var7 && var15 >= var7 && var19 >= var7 && var23 >= var7) {
         return var0;
      } else if (var11 <= var3 && var15 <= var3 && var19 <= var3 && var23 <= var3) {
         return var0;
      } else if (var9 <= var1 && var13 <= var1 && var17 <= var1 && var21 <= var1) {
         return var0;
      } else if (var9 >= var5 && var13 >= var5 && var17 >= var5 && var21 >= var5) {
         if (var11 < var23) {
            if (var11 <= var3 && var23 > var3) {
               ++var0;
            }

            if (var11 < var7 && var23 >= var7) {
               ++var0;
            }
         } else if (var23 < var11) {
            if (var23 <= var3 && var11 > var3) {
               --var0;
            }

            if (var23 < var7 && var11 >= var7) {
               --var0;
            }
         }

         return var0;
      } else if (var9 > var1 && var9 < var5 && var11 > var3 && var11 < var7 || var21 > var1 && var21 < var5 && var23 > var3 && var23 < var7) {
         return Integer.MIN_VALUE;
      } else if (var25 > 52) {
         return rectCrossingsForLine(var0, var1, var3, var5, var7, var9, var11, var21, var23);
      } else {
         double var26 = (var13 + var17) / 2.0D;
         double var28 = (var15 + var19) / 2.0D;
         var13 = (var9 + var13) / 2.0D;
         var15 = (var11 + var15) / 2.0D;
         var17 = (var17 + var21) / 2.0D;
         var19 = (var19 + var23) / 2.0D;
         double var30 = (var13 + var26) / 2.0D;
         double var32 = (var15 + var28) / 2.0D;
         double var34 = (var26 + var17) / 2.0D;
         double var36 = (var28 + var19) / 2.0D;
         var26 = (var30 + var34) / 2.0D;
         var28 = (var32 + var36) / 2.0D;
         if (!Double.isNaN(var26) && !Double.isNaN(var28)) {
            var0 = rectCrossingsForCubic(var0, var1, var3, var5, var7, var9, var11, var13, var15, var30, var32, var26, var28, var25 + 1);
            if (var0 != Integer.MIN_VALUE) {
               var0 = rectCrossingsForCubic(var0, var1, var3, var5, var7, var26, var28, var34, var36, var17, var19, var21, var23, var25 + 1);
            }

            return var0;
         } else {
            return 0;
         }
      }
   }

   public Curve(int var1) {
      this.direction = var1;
   }

   public final int getDirection() {
      return this.direction;
   }

   public final Curve getWithDirection(int var1) {
      return this.direction == var1 ? this : this.getReversedCurve();
   }

   public static double round(double var0) {
      return var0;
   }

   public static int orderof(double var0, double var2) {
      if (var0 < var2) {
         return -1;
      } else {
         return var0 > var2 ? 1 : 0;
      }
   }

   public static long signeddiffbits(double var0, double var2) {
      return Double.doubleToLongBits(var0) - Double.doubleToLongBits(var2);
   }

   public static long diffbits(double var0, double var2) {
      return Math.abs(Double.doubleToLongBits(var0) - Double.doubleToLongBits(var2));
   }

   public static double prev(double var0) {
      return Double.longBitsToDouble(Double.doubleToLongBits(var0) - 1L);
   }

   public static double next(double var0) {
      return Double.longBitsToDouble(Double.doubleToLongBits(var0) + 1L);
   }

   public String toString() {
      return "Curve[" + this.getOrder() + ", " + "(" + round(this.getX0()) + ", " + round(this.getY0()) + "), " + this.controlPointString() + "(" + round(this.getX1()) + ", " + round(this.getY1()) + "), " + (this.direction == 1 ? "D" : "U") + "]";
   }

   public String controlPointString() {
      return "";
   }

   public abstract int getOrder();

   public abstract double getXTop();

   public abstract double getYTop();

   public abstract double getXBot();

   public abstract double getYBot();

   public abstract double getXMin();

   public abstract double getXMax();

   public abstract double getX0();

   public abstract double getY0();

   public abstract double getX1();

   public abstract double getY1();

   public abstract double XforY(double var1);

   public abstract double TforY(double var1);

   public abstract double XforT(double var1);

   public abstract double YforT(double var1);

   public abstract double dXforT(double var1, int var3);

   public abstract double dYforT(double var1, int var3);

   public abstract double nextVertical(double var1, double var3);

   public int crossingsFor(double var1, double var3) {
      return var3 < this.getYTop() || var3 >= this.getYBot() || var1 >= this.getXMax() || var1 >= this.getXMin() && var1 >= this.XforY(var3) ? 0 : 1;
   }

   public boolean accumulateCrossings(Crossings var1) {
      double var2 = var1.getXHi();
      if (this.getXMin() >= var2) {
         return false;
      } else {
         double var4 = var1.getXLo();
         double var6 = var1.getYLo();
         double var8 = var1.getYHi();
         double var10 = this.getYTop();
         double var12 = this.getYBot();
         double var14;
         double var16;
         if (var10 < var6) {
            if (var12 <= var6) {
               return false;
            }

            var16 = var6;
            var14 = this.TforY(var6);
         } else {
            if (var10 >= var8) {
               return false;
            }

            var16 = var10;
            var14 = 0.0D;
         }

         double var18;
         double var20;
         if (var12 > var8) {
            var20 = var8;
            var18 = this.TforY(var8);
         } else {
            var20 = var12;
            var18 = 1.0D;
         }

         boolean var22 = false;
         boolean var23 = false;

         while(true) {
            double var24 = this.XforT(var14);
            if (var24 < var2) {
               if (var23 || var24 > var4) {
                  return true;
               }

               var22 = true;
            } else {
               if (var22) {
                  return true;
               }

               var23 = true;
            }

            if (var14 >= var18) {
               if (var22) {
                  var1.record(var16, var20, this.direction);
               }

               return false;
            }

            var14 = this.nextVertical(var14, var18);
         }
      }
   }

   public abstract void enlarge(Rectangle2D var1);

   public Curve getSubCurve(double var1, double var3) {
      return this.getSubCurve(var1, var3, this.direction);
   }

   public abstract Curve getReversedCurve();

   public abstract Curve getSubCurve(double var1, double var3, int var5);

   public int compareTo(Curve var1, double[] var2) {
      double var3 = var2[0];
      double var5 = var2[1];
      var5 = Math.min(Math.min(var5, this.getYBot()), var1.getYBot());
      if (var5 <= var2[0]) {
         System.err.println("this == " + this);
         System.err.println("that == " + var1);
         System.out.println("target range = " + var2[0] + "=>" + var2[1]);
         throw new InternalError("backstepping from " + var2[0] + " to " + var5);
      } else {
         var2[1] = var5;
         if (this.getXMax() <= var1.getXMin()) {
            return this.getXMin() == var1.getXMax() ? 0 : -1;
         } else if (this.getXMin() >= var1.getXMax()) {
            return 1;
         } else {
            double var7 = this.TforY(var3);
            double var9 = this.YforT(var7);
            if (var9 < var3) {
               var7 = this.refineTforY(var7, var9, var3);
               var9 = this.YforT(var7);
            }

            double var11 = this.TforY(var5);
            if (this.YforT(var11) < var3) {
               var11 = this.refineTforY(var11, this.YforT(var11), var3);
            }

            double var13 = var1.TforY(var3);
            double var15 = var1.YforT(var13);
            if (var15 < var3) {
               var13 = var1.refineTforY(var13, var15, var3);
               var15 = var1.YforT(var13);
            }

            double var17 = var1.TforY(var5);
            if (var1.YforT(var17) < var3) {
               var17 = var1.refineTforY(var17, var1.YforT(var17), var3);
            }

            double var19 = this.XforT(var7);
            double var21 = var1.XforT(var13);
            double var23 = Math.max(Math.abs(var3), Math.abs(var5));
            double var25 = Math.max(var23 * 1.0E-14D, 1.0E-300D);
            double var33;
            double var27;
            double var29;
            double var31;
            if (this.fairlyClose(var19, var21)) {
               var27 = var25;
               var29 = Math.min(var25 * 1.0E13D, (var5 - var3) * 0.1D);

               label101:
               for(var31 = var3 + var25; var31 <= var5; var31 += var27) {
                  if (!this.fairlyClose(this.XforY(var31), var1.XforY(var31))) {
                     var31 -= var27;

                     while(true) {
                        var27 /= 2.0D;
                        var33 = var31 + var27;
                        if (var33 <= var31) {
                           break label101;
                        }

                        if (this.fairlyClose(this.XforY(var33), var1.XforY(var33))) {
                           var31 = var33;
                        }
                     }
                  }

                  if ((var27 *= 2.0D) > var29) {
                     var27 = var29;
                  }
               }

               if (var31 > var3) {
                  if (var31 < var5) {
                     var2[1] = var31;
                  }

                  return 0;
               }
            }

            if (var25 <= 0.0D) {
               System.out.println("ymin = " + var25);
            }

            while(var7 < var11 && var13 < var17) {
               var27 = this.nextVertical(var7, var11);
               var29 = this.XforT(var27);
               var31 = this.YforT(var27);
               var33 = var1.nextVertical(var13, var17);
               double var35 = var1.XforT(var33);
               double var37 = var1.YforT(var33);

               try {
                  if (this.findIntersect(var1, var2, var25, 0, 0, var7, var19, var9, var27, var29, var31, var13, var21, var15, var33, var35, var37)) {
                     break;
                  }
               } catch (Throwable var40) {
                  System.err.println("Error: " + var40);
                  System.err.println("y range was " + var2[0] + "=>" + var2[1]);
                  System.err.println("s y range is " + var9 + "=>" + var31);
                  System.err.println("t y range is " + var15 + "=>" + var37);
                  System.err.println("ymin is " + var25);
                  return 0;
               }

               if (var31 < var37) {
                  if (var31 > var2[0]) {
                     if (var31 < var2[1]) {
                        var2[1] = var31;
                     }
                     break;
                  }

                  var7 = var27;
                  var19 = var29;
                  var9 = var31;
               } else {
                  if (var37 > var2[0]) {
                     if (var37 < var2[1]) {
                        var2[1] = var37;
                     }
                     break;
                  }

                  var13 = var33;
                  var21 = var35;
                  var15 = var37;
               }
            }

            var27 = (var2[0] + var2[1]) / 2.0D;
            return orderof(this.XforY(var27), var1.XforY(var27));
         }
      }
   }

   public boolean findIntersect(Curve var1, double[] var2, double var3, int var5, int var6, double var7, double var9, double var11, double var13, double var15, double var17, double var19, double var21, double var23, double var25, double var27, double var29) {
      if (var11 <= var29 && var23 <= var17) {
         if (Math.min(var9, var15) <= Math.max(var21, var27) && Math.max(var9, var15) >= Math.min(var21, var27)) {
            double var31;
            double var33;
            double var35;
            double var37;
            double var39;
            double var41;
            if (var13 - var7 > 0.001D) {
               var31 = (var7 + var13) / 2.0D;
               var33 = this.XforT(var31);
               var35 = this.YforT(var31);
               if (var31 == var7 || var31 == var13) {
                  System.out.println("s0 = " + var7);
                  System.out.println("s1 = " + var13);
                  throw new InternalError("no s progress!");
               }

               if (var25 - var19 > 0.001D) {
                  var37 = (var19 + var25) / 2.0D;
                  var39 = var1.XforT(var37);
                  var41 = var1.YforT(var37);
                  if (var37 == var19 || var37 == var25) {
                     System.out.println("t0 = " + var19);
                     System.out.println("t1 = " + var25);
                     throw new InternalError("no t progress!");
                  }

                  if (var35 >= var23 && var41 >= var11 && this.findIntersect(var1, var2, var3, var5 + 1, var6 + 1, var7, var9, var11, var31, var33, var35, var19, var21, var23, var37, var39, var41)) {
                     return true;
                  }

                  if (var35 >= var41 && this.findIntersect(var1, var2, var3, var5 + 1, var6 + 1, var7, var9, var11, var31, var33, var35, var37, var39, var41, var25, var27, var29)) {
                     return true;
                  }

                  if (var41 >= var35 && this.findIntersect(var1, var2, var3, var5 + 1, var6 + 1, var31, var33, var35, var13, var15, var17, var19, var21, var23, var37, var39, var41)) {
                     return true;
                  }

                  if (var17 >= var41 && var29 >= var35 && this.findIntersect(var1, var2, var3, var5 + 1, var6 + 1, var31, var33, var35, var13, var15, var17, var37, var39, var41, var25, var27, var29)) {
                     return true;
                  }
               } else {
                  if (var35 >= var23 && this.findIntersect(var1, var2, var3, var5 + 1, var6, var7, var9, var11, var31, var33, var35, var19, var21, var23, var25, var27, var29)) {
                     return true;
                  }

                  if (var29 >= var35 && this.findIntersect(var1, var2, var3, var5 + 1, var6, var31, var33, var35, var13, var15, var17, var19, var21, var23, var25, var27, var29)) {
                     return true;
                  }
               }
            } else if (var25 - var19 > 0.001D) {
               var31 = (var19 + var25) / 2.0D;
               var33 = var1.XforT(var31);
               var35 = var1.YforT(var31);
               if (var31 == var19 || var31 == var25) {
                  System.out.println("t0 = " + var19);
                  System.out.println("t1 = " + var25);
                  throw new InternalError("no t progress!");
               }

               if (var35 >= var11 && this.findIntersect(var1, var2, var3, var5, var6 + 1, var7, var9, var11, var13, var15, var17, var19, var21, var23, var31, var33, var35)) {
                  return true;
               }

               if (var17 >= var35 && this.findIntersect(var1, var2, var3, var5, var6 + 1, var7, var9, var11, var13, var15, var17, var31, var33, var35, var25, var27, var29)) {
                  return true;
               }
            } else {
               var31 = var15 - var9;
               var33 = var17 - var11;
               var35 = var27 - var21;
               var37 = var29 - var23;
               var39 = var21 - var9;
               var41 = var23 - var11;
               double var43 = var35 * var33 - var37 * var31;
               if (var43 != 0.0D) {
                  double var45 = 1.0D / var43;
                  double var47 = (var35 * var41 - var37 * var39) * var45;
                  double var49 = (var31 * var41 - var33 * var39) * var45;
                  if (var47 >= 0.0D && var47 <= 1.0D && var49 >= 0.0D && var49 <= 1.0D) {
                     var47 = var7 + var47 * (var13 - var7);
                     var49 = var19 + var49 * (var25 - var19);
                     if (var47 < 0.0D || var47 > 1.0D || var49 < 0.0D || var49 > 1.0D) {
                        System.out.println("Uh oh!");
                     }

                     double var51 = (this.YforT(var47) + var1.YforT(var49)) / 2.0D;
                     if (var51 <= var2[1] && var51 > var2[0]) {
                        var2[1] = var51;
                        return true;
                     }
                  }
               }
            }

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public double refineTforY(double var1, double var3, double var5) {
      double var7 = 1.0D;

      while(true) {
         double var9 = (var1 + var7) / 2.0D;
         if (var9 == var1 || var9 == var7) {
            return var7;
         }

         double var11 = this.YforT(var9);
         if (var11 < var5) {
            var1 = var9;
         } else {
            if (var11 <= var5) {
               return var7;
            }

            var7 = var9;
         }
      }
   }

   public boolean fairlyClose(double var1, double var3) {
      return Math.abs(var1 - var3) < Math.max(Math.abs(var1), Math.abs(var3)) * 1.0E-10D;
   }

   public abstract int getSegment(double[] var1);
}
