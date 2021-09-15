package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Arrays;
import sun.awt.geom.Crossings;

public class Polygon implements Shape, Serializable {
   public int npoints;
   public int[] xpoints;
   public int[] ypoints;
   protected Rectangle bounds;
   private static final long serialVersionUID = -6460061437900069969L;
   private static final int MIN_LENGTH = 4;

   public Polygon() {
      this.xpoints = new int[4];
      this.ypoints = new int[4];
   }

   public Polygon(int[] var1, int[] var2, int var3) {
      if (var3 <= var1.length && var3 <= var2.length) {
         if (var3 < 0) {
            throw new NegativeArraySizeException("npoints < 0");
         } else {
            this.npoints = var3;
            this.xpoints = Arrays.copyOf(var1, var3);
            this.ypoints = Arrays.copyOf(var2, var3);
         }
      } else {
         throw new IndexOutOfBoundsException("npoints > xpoints.length || npoints > ypoints.length");
      }
   }

   public void reset() {
      this.npoints = 0;
      this.bounds = null;
   }

   public void invalidate() {
      this.bounds = null;
   }

   public void translate(int var1, int var2) {
      for(int var3 = 0; var3 < this.npoints; ++var3) {
         int[] var10000 = this.xpoints;
         var10000[var3] += var1;
         var10000 = this.ypoints;
         var10000[var3] += var2;
      }

      if (this.bounds != null) {
         this.bounds.translate(var1, var2);
      }

   }

   void calculateBounds(int[] var1, int[] var2, int var3) {
      int var4 = Integer.MAX_VALUE;
      int var5 = Integer.MAX_VALUE;
      int var6 = Integer.MIN_VALUE;
      int var7 = Integer.MIN_VALUE;

      for(int var8 = 0; var8 < var3; ++var8) {
         int var9 = var1[var8];
         var4 = Math.min(var4, var9);
         var6 = Math.max(var6, var9);
         int var10 = var2[var8];
         var5 = Math.min(var5, var10);
         var7 = Math.max(var7, var10);
      }

      this.bounds = new Rectangle(var4, var5, var6 - var4, var7 - var5);
   }

   void updateBounds(int var1, int var2) {
      if (var1 < this.bounds.x) {
         this.bounds.width += this.bounds.x - var1;
         this.bounds.x = var1;
      } else {
         this.bounds.width = Math.max(this.bounds.width, var1 - this.bounds.x);
      }

      if (var2 < this.bounds.y) {
         this.bounds.height += this.bounds.y - var2;
         this.bounds.y = var2;
      } else {
         this.bounds.height = Math.max(this.bounds.height, var2 - this.bounds.y);
      }

   }

   public void addPoint(int var1, int var2) {
      if (this.npoints >= this.xpoints.length || this.npoints >= this.ypoints.length) {
         int var3 = this.npoints * 2;
         if (var3 < 4) {
            var3 = 4;
         } else if ((var3 & var3 - 1) != 0) {
            var3 = Integer.highestOneBit(var3);
         }

         this.xpoints = Arrays.copyOf(this.xpoints, var3);
         this.ypoints = Arrays.copyOf(this.ypoints, var3);
      }

      this.xpoints[this.npoints] = var1;
      this.ypoints[this.npoints] = var2;
      ++this.npoints;
      if (this.bounds != null) {
         this.updateBounds(var1, var2);
      }

   }

   public Rectangle getBounds() {
      return this.getBoundingBox();
   }

   /** @deprecated */
   @Deprecated
   public Rectangle getBoundingBox() {
      if (this.npoints == 0) {
         return new Rectangle();
      } else {
         if (this.bounds == null) {
            this.calculateBounds(this.xpoints, this.ypoints, this.npoints);
         }

         return this.bounds.getBounds();
      }
   }

   public boolean contains(Point var1) {
      return this.contains(var1.x, var1.y);
   }

   public boolean contains(int var1, int var2) {
      return this.contains((double)var1, (double)var2);
   }

   /** @deprecated */
   @Deprecated
   public boolean inside(int var1, int var2) {
      return this.contains((double)var1, (double)var2);
   }

   public Rectangle2D getBounds2D() {
      return this.getBounds();
   }

   public boolean contains(double var1, double var3) {
      if (this.npoints > 2 && this.getBoundingBox().contains(var1, var3)) {
         int var5 = 0;
         int var6 = this.xpoints[this.npoints - 1];
         int var7 = this.ypoints[this.npoints - 1];

         for(int var10 = 0; var10 < this.npoints; ++var10) {
            int var8 = this.xpoints[var10];
            int var9 = this.ypoints[var10];
            if (var9 != var7) {
               label78: {
                  int var11;
                  if (var8 < var6) {
                     if (var1 >= (double)var6) {
                        break label78;
                     }

                     var11 = var8;
                  } else {
                     if (var1 >= (double)var8) {
                        break label78;
                     }

                     var11 = var6;
                  }

                  double var12;
                  double var14;
                  if (var9 < var7) {
                     if (var3 < (double)var9 || var3 >= (double)var7) {
                        break label78;
                     }

                     if (var1 < (double)var11) {
                        ++var5;
                        break label78;
                     }

                     var12 = var1 - (double)var8;
                     var14 = var3 - (double)var9;
                  } else {
                     if (var3 < (double)var7 || var3 >= (double)var9) {
                        break label78;
                     }

                     if (var1 < (double)var11) {
                        ++var5;
                        break label78;
                     }

                     var12 = var1 - (double)var6;
                     var14 = var3 - (double)var7;
                  }

                  if (var12 < var14 / (double)(var7 - var9) * (double)(var6 - var8)) {
                     ++var5;
                  }
               }
            }

            var6 = var8;
            var7 = var9;
         }

         return (var5 & 1) != 0;
      } else {
         return false;
      }
   }

   private Crossings getCrossings(double var1, double var3, double var5, double var7) {
      Crossings.EvenOdd var9 = new Crossings.EvenOdd(var1, var3, var5, var7);
      int var10 = this.xpoints[this.npoints - 1];
      int var11 = this.ypoints[this.npoints - 1];

      for(int var14 = 0; var14 < this.npoints; ++var14) {
         int var12 = this.xpoints[var14];
         int var13 = this.ypoints[var14];
         if (var9.accumulateLine((double)var10, (double)var11, (double)var12, (double)var13)) {
            return null;
         }

         var10 = var12;
         var11 = var13;
      }

      return var9;
   }

   public boolean contains(Point2D var1) {
      return this.contains(var1.getX(), var1.getY());
   }

   public boolean intersects(double var1, double var3, double var5, double var7) {
      if (this.npoints > 0 && this.getBoundingBox().intersects(var1, var3, var5, var7)) {
         Crossings var9 = this.getCrossings(var1, var3, var1 + var5, var3 + var7);
         return var9 == null || !var9.isEmpty();
      } else {
         return false;
      }
   }

   public boolean intersects(Rectangle2D var1) {
      return this.intersects(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
   }

   public boolean contains(double var1, double var3, double var5, double var7) {
      if (this.npoints > 0 && this.getBoundingBox().intersects(var1, var3, var5, var7)) {
         Crossings var9 = this.getCrossings(var1, var3, var1 + var5, var3 + var7);
         return var9 != null && var9.covers(var3, var3 + var7);
      } else {
         return false;
      }
   }

   public boolean contains(Rectangle2D var1) {
      return this.contains(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
   }

   public PathIterator getPathIterator(AffineTransform var1) {
      return new Polygon.PolygonPathIterator(this, var1);
   }

   public PathIterator getPathIterator(AffineTransform var1, double var2) {
      return this.getPathIterator(var1);
   }

   class PolygonPathIterator implements PathIterator {
      Polygon poly;
      AffineTransform transform;
      int index;

      public PolygonPathIterator(Polygon var2, AffineTransform var3) {
         this.poly = var2;
         this.transform = var3;
         if (var2.npoints == 0) {
            this.index = 1;
         }

      }

      public int getWindingRule() {
         return 0;
      }

      public boolean isDone() {
         return this.index > this.poly.npoints;
      }

      public void next() {
         ++this.index;
      }

      public int currentSegment(float[] var1) {
         if (this.index >= this.poly.npoints) {
            return 4;
         } else {
            var1[0] = (float)this.poly.xpoints[this.index];
            var1[1] = (float)this.poly.ypoints[this.index];
            if (this.transform != null) {
               this.transform.transform((float[])var1, 0, (float[])var1, 0, 1);
            }

            return this.index == 0 ? 0 : 1;
         }
      }

      public int currentSegment(double[] var1) {
         if (this.index >= this.poly.npoints) {
            return 4;
         } else {
            var1[0] = (double)this.poly.xpoints[this.index];
            var1[1] = (double)this.poly.ypoints[this.index];
            if (this.transform != null) {
               this.transform.transform((double[])var1, 0, (double[])var1, 0, 1);
            }

            return this.index == 0 ? 0 : 1;
         }
      }
   }
}
