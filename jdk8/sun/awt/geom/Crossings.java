package sun.awt.geom;

import java.awt.geom.PathIterator;
import java.util.Enumeration;
import java.util.Vector;

public abstract class Crossings {
   public static final boolean debug = false;
   int limit = 0;
   double[] yranges = new double[10];
   double xlo;
   double ylo;
   double xhi;
   double yhi;
   private Vector tmp = new Vector();

   public Crossings(double var1, double var3, double var5, double var7) {
      this.xlo = var1;
      this.ylo = var3;
      this.xhi = var5;
      this.yhi = var7;
   }

   public final double getXLo() {
      return this.xlo;
   }

   public final double getYLo() {
      return this.ylo;
   }

   public final double getXHi() {
      return this.xhi;
   }

   public final double getYHi() {
      return this.yhi;
   }

   public abstract void record(double var1, double var3, int var5);

   public void print() {
      System.out.println("Crossings [");
      System.out.println("  bounds = [" + this.ylo + ", " + this.yhi + "]");

      for(int var1 = 0; var1 < this.limit; var1 += 2) {
         System.out.println("  [" + this.yranges[var1] + ", " + this.yranges[var1 + 1] + "]");
      }

      System.out.println("]");
   }

   public final boolean isEmpty() {
      return this.limit == 0;
   }

   public abstract boolean covers(double var1, double var3);

   public static Crossings findCrossings(Vector var0, double var1, double var3, double var5, double var7) {
      Crossings.EvenOdd var9 = new Crossings.EvenOdd(var1, var3, var5, var7);
      Enumeration var10 = var0.elements();

      Curve var11;
      do {
         if (!var10.hasMoreElements()) {
            return var9;
         }

         var11 = (Curve)var10.nextElement();
      } while(!var11.accumulateCrossings(var9));

      return null;
   }

   public static Crossings findCrossings(PathIterator var0, double var1, double var3, double var5, double var7) {
      Object var9;
      if (var0.getWindingRule() == 0) {
         var9 = new Crossings.EvenOdd(var1, var3, var5, var7);
      } else {
         var9 = new Crossings.NonZero(var1, var3, var5, var7);
      }

      double[] var10 = new double[23];
      double var11 = 0.0D;
      double var13 = 0.0D;
      double var15 = 0.0D;

      double var17;
      for(var17 = 0.0D; !var0.isDone(); var0.next()) {
         int var23 = var0.currentSegment(var10);
         double var19;
         double var21;
         switch(var23) {
         case 0:
            if (var13 != var17 && ((Crossings)var9).accumulateLine(var15, var17, var11, var13)) {
               return null;
            }

            var11 = var15 = var10[0];
            var13 = var17 = var10[1];
            break;
         case 1:
            var19 = var10[0];
            var21 = var10[1];
            if (((Crossings)var9).accumulateLine(var15, var17, var19, var21)) {
               return null;
            }

            var15 = var19;
            var17 = var21;
            break;
         case 2:
            var19 = var10[2];
            var21 = var10[3];
            if (((Crossings)var9).accumulateQuad(var15, var17, var10)) {
               return null;
            }

            var15 = var19;
            var17 = var21;
            break;
         case 3:
            var19 = var10[4];
            var21 = var10[5];
            if (((Crossings)var9).accumulateCubic(var15, var17, var10)) {
               return null;
            }

            var15 = var19;
            var17 = var21;
            break;
         case 4:
            if (var13 != var17 && ((Crossings)var9).accumulateLine(var15, var17, var11, var13)) {
               return null;
            }

            var15 = var11;
            var17 = var13;
         }
      }

      if (var13 != var17 && ((Crossings)var9).accumulateLine(var15, var17, var11, var13)) {
         return null;
      } else {
         return (Crossings)var9;
      }
   }

   public boolean accumulateLine(double var1, double var3, double var5, double var7) {
      return var3 <= var7 ? this.accumulateLine(var1, var3, var5, var7, 1) : this.accumulateLine(var5, var7, var1, var3, -1);
   }

   public boolean accumulateLine(double var1, double var3, double var5, double var7, int var9) {
      if (this.yhi > var3 && this.ylo < var7) {
         if (var1 >= this.xhi && var5 >= this.xhi) {
            return false;
         } else if (var3 != var7) {
            double var18 = var5 - var1;
            double var20 = var7 - var3;
            double var10;
            double var12;
            if (var3 < this.ylo) {
               var10 = var1 + (this.ylo - var3) * var18 / var20;
               var12 = this.ylo;
            } else {
               var10 = var1;
               var12 = var3;
            }

            double var16;
            double var14;
            if (this.yhi < var7) {
               var14 = var1 + (this.yhi - var3) * var18 / var20;
               var16 = this.yhi;
            } else {
               var14 = var5;
               var16 = var7;
            }

            if (var10 >= this.xhi && var14 >= this.xhi) {
               return false;
            } else if (var10 <= this.xlo && var14 <= this.xlo) {
               this.record(var12, var16, var9);
               return false;
            } else {
               return true;
            }
         } else {
            return var1 >= this.xlo || var5 >= this.xlo;
         }
      } else {
         return false;
      }
   }

   public boolean accumulateQuad(double var1, double var3, double[] var5) {
      if (var3 < this.ylo && var5[1] < this.ylo && var5[3] < this.ylo) {
         return false;
      } else if (var3 > this.yhi && var5[1] > this.yhi && var5[3] > this.yhi) {
         return false;
      } else if (var1 > this.xhi && var5[0] > this.xhi && var5[2] > this.xhi) {
         return false;
      } else if (var1 < this.xlo && var5[0] < this.xlo && var5[2] < this.xlo) {
         if (var3 < var5[3]) {
            this.record(Math.max(var3, this.ylo), Math.min(var5[3], this.yhi), 1);
         } else if (var3 > var5[3]) {
            this.record(Math.max(var5[3], this.ylo), Math.min(var3, this.yhi), -1);
         }

         return false;
      } else {
         Curve.insertQuad(this.tmp, var1, var3, var5);
         Enumeration var6 = this.tmp.elements();

         Curve var7;
         do {
            if (!var6.hasMoreElements()) {
               this.tmp.clear();
               return false;
            }

            var7 = (Curve)var6.nextElement();
         } while(!var7.accumulateCrossings(this));

         return true;
      }
   }

   public boolean accumulateCubic(double var1, double var3, double[] var5) {
      if (var3 < this.ylo && var5[1] < this.ylo && var5[3] < this.ylo && var5[5] < this.ylo) {
         return false;
      } else if (var3 > this.yhi && var5[1] > this.yhi && var5[3] > this.yhi && var5[5] > this.yhi) {
         return false;
      } else if (var1 > this.xhi && var5[0] > this.xhi && var5[2] > this.xhi && var5[4] > this.xhi) {
         return false;
      } else if (var1 < this.xlo && var5[0] < this.xlo && var5[2] < this.xlo && var5[4] < this.xlo) {
         if (var3 <= var5[5]) {
            this.record(Math.max(var3, this.ylo), Math.min(var5[5], this.yhi), 1);
         } else {
            this.record(Math.max(var5[5], this.ylo), Math.min(var3, this.yhi), -1);
         }

         return false;
      } else {
         Curve.insertCubic(this.tmp, var1, var3, var5);
         Enumeration var6 = this.tmp.elements();

         Curve var7;
         do {
            if (!var6.hasMoreElements()) {
               this.tmp.clear();
               return false;
            }

            var7 = (Curve)var6.nextElement();
         } while(!var7.accumulateCrossings(this));

         return true;
      }
   }

   public static final class NonZero extends Crossings {
      private int[] crosscounts;

      public NonZero(double var1, double var3, double var5, double var7) {
         super(var1, var3, var5, var7);
         this.crosscounts = new int[this.yranges.length / 2];
      }

      public final boolean covers(double var1, double var3) {
         int var5 = 0;

         while(var5 < this.limit) {
            double var6 = this.yranges[var5++];
            double var8 = this.yranges[var5++];
            if (var1 < var8) {
               if (var1 < var6) {
                  return false;
               }

               if (var3 <= var8) {
                  return true;
               }

               var1 = var8;
            }
         }

         return var1 >= var3;
      }

      public void remove(int var1) {
         this.limit -= 2;
         int var2 = this.limit - var1;
         if (var2 > 0) {
            System.arraycopy(this.yranges, var1 + 2, this.yranges, var1, var2);
            System.arraycopy(this.crosscounts, var1 / 2 + 1, this.crosscounts, var1 / 2, var2 / 2);
         }

      }

      public void insert(int var1, double var2, double var4, int var6) {
         int var7 = this.limit - var1;
         double[] var8 = this.yranges;
         int[] var9 = this.crosscounts;
         if (this.limit >= this.yranges.length) {
            this.yranges = new double[this.limit + 10];
            System.arraycopy(var8, 0, this.yranges, 0, var1);
            this.crosscounts = new int[(this.limit + 10) / 2];
            System.arraycopy(var9, 0, this.crosscounts, 0, var1 / 2);
         }

         if (var7 > 0) {
            System.arraycopy(var8, var1, this.yranges, var1 + 2, var7);
            System.arraycopy(var9, var1 / 2, this.crosscounts, var1 / 2 + 1, var7 / 2);
         }

         this.yranges[var1 + 0] = var2;
         this.yranges[var1 + 1] = var4;
         this.crosscounts[var1 / 2] = var6;
         this.limit += 2;
      }

      public void record(double var1, double var3, int var5) {
         if (var1 < var3) {
            int var6;
            for(var6 = 0; var6 < this.limit && var1 > this.yranges[var6 + 1]; var6 += 2) {
            }

            if (var6 < this.limit) {
               int var7 = this.crosscounts[var6 / 2];
               double var8 = this.yranges[var6 + 0];
               double var10 = this.yranges[var6 + 1];
               if (var10 == var1 && var7 == var5) {
                  if (var6 + 2 == this.limit) {
                     this.yranges[var6 + 1] = var3;
                     return;
                  }

                  this.remove(var6);
                  var1 = var8;
                  var7 = this.crosscounts[var6 / 2];
                  var8 = this.yranges[var6 + 0];
                  var10 = this.yranges[var6 + 1];
               }

               if (var3 < var8) {
                  this.insert(var6, var1, var3, var5);
                  return;
               }

               if (var3 == var8 && var7 == var5) {
                  this.yranges[var6] = var1;
                  return;
               }

               if (var1 < var8) {
                  this.insert(var6, var1, var8, var5);
                  var6 += 2;
                  var1 = var8;
               } else if (var8 < var1) {
                  this.insert(var6, var8, var1, var7);
                  var6 += 2;
               }

               int var12 = var7 + var5;
               double var13 = Math.min(var3, var10);
               if (var12 == 0) {
                  this.remove(var6);
               } else {
                  this.crosscounts[var6 / 2] = var12;
                  this.yranges[var6++] = var1;
                  this.yranges[var6++] = var13;
               }

               var1 = var13;
               if (var13 < var10) {
                  this.insert(var6, var13, var10, var7);
               }
            }

            if (var1 < var3) {
               this.insert(var6, var1, var3, var5);
            }

         }
      }
   }

   public static final class EvenOdd extends Crossings {
      public EvenOdd(double var1, double var3, double var5, double var7) {
         super(var1, var3, var5, var7);
      }

      public final boolean covers(double var1, double var3) {
         return this.limit == 2 && this.yranges[0] <= var1 && this.yranges[1] >= var3;
      }

      public void record(double var1, double var3, int var5) {
         if (var1 < var3) {
            int var6;
            for(var6 = 0; var6 < this.limit && var1 > this.yranges[var6 + 1]; var6 += 2) {
            }

            int var7 = var6;

            while(var6 < this.limit) {
               double var8 = this.yranges[var6++];
               double var10 = this.yranges[var6++];
               if (var3 < var8) {
                  this.yranges[var7++] = var1;
                  this.yranges[var7++] = var3;
                  var1 = var8;
                  var3 = var10;
               } else {
                  double var12;
                  double var14;
                  if (var1 < var8) {
                     var12 = var1;
                     var14 = var8;
                  } else {
                     var12 = var8;
                     var14 = var1;
                  }

                  double var16;
                  double var18;
                  if (var3 < var10) {
                     var16 = var3;
                     var18 = var10;
                  } else {
                     var16 = var10;
                     var18 = var3;
                  }

                  if (var14 == var16) {
                     var1 = var12;
                     var3 = var18;
                  } else {
                     if (var14 > var16) {
                        var1 = var16;
                        var16 = var14;
                        var14 = var1;
                     }

                     if (var12 != var14) {
                        this.yranges[var7++] = var12;
                        this.yranges[var7++] = var14;
                     }

                     var1 = var16;
                     var3 = var18;
                  }

                  if (var1 >= var3) {
                     break;
                  }
               }
            }

            if (var7 < var6 && var6 < this.limit) {
               System.arraycopy(this.yranges, var6, this.yranges, var7, this.limit - var6);
            }

            var7 += this.limit - var6;
            if (var1 < var3) {
               if (var7 >= this.yranges.length) {
                  double[] var20 = new double[var7 + 10];
                  System.arraycopy(this.yranges, 0, var20, 0, var7);
                  this.yranges = var20;
               }

               this.yranges[var7++] = var1;
               this.yranges[var7++] = var3;
            }

            this.limit = var7;
         }
      }
   }
}
