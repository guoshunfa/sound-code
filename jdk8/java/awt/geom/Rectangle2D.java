package java.awt.geom;

import java.io.Serializable;

public abstract class Rectangle2D extends RectangularShape {
   public static final int OUT_LEFT = 1;
   public static final int OUT_TOP = 2;
   public static final int OUT_RIGHT = 4;
   public static final int OUT_BOTTOM = 8;

   protected Rectangle2D() {
   }

   public abstract void setRect(double var1, double var3, double var5, double var7);

   public void setRect(Rectangle2D var1) {
      this.setRect(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
   }

   public boolean intersectsLine(double var1, double var3, double var5, double var7) {
      int var10;
      if ((var10 = this.outcode(var5, var7)) == 0) {
         return true;
      } else {
         int var9;
         while((var9 = this.outcode(var1, var3)) != 0) {
            if ((var9 & var10) != 0) {
               return false;
            }

            double var11;
            if ((var9 & 5) != 0) {
               var11 = this.getX();
               if ((var9 & 4) != 0) {
                  var11 += this.getWidth();
               }

               var3 += (var11 - var1) * (var7 - var3) / (var5 - var1);
               var1 = var11;
            } else {
               var11 = this.getY();
               if ((var9 & 8) != 0) {
                  var11 += this.getHeight();
               }

               var1 += (var11 - var3) * (var5 - var1) / (var7 - var3);
               var3 = var11;
            }
         }

         return true;
      }
   }

   public boolean intersectsLine(Line2D var1) {
      return this.intersectsLine(var1.getX1(), var1.getY1(), var1.getX2(), var1.getY2());
   }

   public abstract int outcode(double var1, double var3);

   public int outcode(Point2D var1) {
      return this.outcode(var1.getX(), var1.getY());
   }

   public void setFrame(double var1, double var3, double var5, double var7) {
      this.setRect(var1, var3, var5, var7);
   }

   public Rectangle2D getBounds2D() {
      return (Rectangle2D)this.clone();
   }

   public boolean contains(double var1, double var3) {
      double var5 = this.getX();
      double var7 = this.getY();
      return var1 >= var5 && var3 >= var7 && var1 < var5 + this.getWidth() && var3 < var7 + this.getHeight();
   }

   public boolean intersects(double var1, double var3, double var5, double var7) {
      if (!this.isEmpty() && var5 > 0.0D && var7 > 0.0D) {
         double var9 = this.getX();
         double var11 = this.getY();
         return var1 + var5 > var9 && var3 + var7 > var11 && var1 < var9 + this.getWidth() && var3 < var11 + this.getHeight();
      } else {
         return false;
      }
   }

   public boolean contains(double var1, double var3, double var5, double var7) {
      if (!this.isEmpty() && var5 > 0.0D && var7 > 0.0D) {
         double var9 = this.getX();
         double var11 = this.getY();
         return var1 >= var9 && var3 >= var11 && var1 + var5 <= var9 + this.getWidth() && var3 + var7 <= var11 + this.getHeight();
      } else {
         return false;
      }
   }

   public abstract Rectangle2D createIntersection(Rectangle2D var1);

   public static void intersect(Rectangle2D var0, Rectangle2D var1, Rectangle2D var2) {
      double var3 = Math.max(var0.getMinX(), var1.getMinX());
      double var5 = Math.max(var0.getMinY(), var1.getMinY());
      double var7 = Math.min(var0.getMaxX(), var1.getMaxX());
      double var9 = Math.min(var0.getMaxY(), var1.getMaxY());
      var2.setFrame(var3, var5, var7 - var3, var9 - var5);
   }

   public abstract Rectangle2D createUnion(Rectangle2D var1);

   public static void union(Rectangle2D var0, Rectangle2D var1, Rectangle2D var2) {
      double var3 = Math.min(var0.getMinX(), var1.getMinX());
      double var5 = Math.min(var0.getMinY(), var1.getMinY());
      double var7 = Math.max(var0.getMaxX(), var1.getMaxX());
      double var9 = Math.max(var0.getMaxY(), var1.getMaxY());
      var2.setFrameFromDiagonal(var3, var5, var7, var9);
   }

   public void add(double var1, double var3) {
      double var5 = Math.min(this.getMinX(), var1);
      double var7 = Math.max(this.getMaxX(), var1);
      double var9 = Math.min(this.getMinY(), var3);
      double var11 = Math.max(this.getMaxY(), var3);
      this.setRect(var5, var9, var7 - var5, var11 - var9);
   }

   public void add(Point2D var1) {
      this.add(var1.getX(), var1.getY());
   }

   public void add(Rectangle2D var1) {
      double var2 = Math.min(this.getMinX(), var1.getMinX());
      double var4 = Math.max(this.getMaxX(), var1.getMaxX());
      double var6 = Math.min(this.getMinY(), var1.getMinY());
      double var8 = Math.max(this.getMaxY(), var1.getMaxY());
      this.setRect(var2, var6, var4 - var2, var8 - var6);
   }

   public PathIterator getPathIterator(AffineTransform var1) {
      return new RectIterator(this, var1);
   }

   public PathIterator getPathIterator(AffineTransform var1, double var2) {
      return new RectIterator(this, var1);
   }

   public int hashCode() {
      long var1 = java.lang.Double.doubleToLongBits(this.getX());
      var1 += java.lang.Double.doubleToLongBits(this.getY()) * 37L;
      var1 += java.lang.Double.doubleToLongBits(this.getWidth()) * 43L;
      var1 += java.lang.Double.doubleToLongBits(this.getHeight()) * 47L;
      return (int)var1 ^ (int)(var1 >> 32);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Rectangle2D)) {
         return false;
      } else {
         Rectangle2D var2 = (Rectangle2D)var1;
         return this.getX() == var2.getX() && this.getY() == var2.getY() && this.getWidth() == var2.getWidth() && this.getHeight() == var2.getHeight();
      }
   }

   public static class Double extends Rectangle2D implements Serializable {
      public double x;
      public double y;
      public double width;
      public double height;
      private static final long serialVersionUID = 7771313791441850493L;

      public Double() {
      }

      public Double(double var1, double var3, double var5, double var7) {
         this.setRect(var1, var3, var5, var7);
      }

      public double getX() {
         return this.x;
      }

      public double getY() {
         return this.y;
      }

      public double getWidth() {
         return this.width;
      }

      public double getHeight() {
         return this.height;
      }

      public boolean isEmpty() {
         return this.width <= 0.0D || this.height <= 0.0D;
      }

      public void setRect(double var1, double var3, double var5, double var7) {
         this.x = var1;
         this.y = var3;
         this.width = var5;
         this.height = var7;
      }

      public void setRect(Rectangle2D var1) {
         this.x = var1.getX();
         this.y = var1.getY();
         this.width = var1.getWidth();
         this.height = var1.getHeight();
      }

      public int outcode(double var1, double var3) {
         int var5 = 0;
         if (this.width <= 0.0D) {
            var5 |= 5;
         } else if (var1 < this.x) {
            var5 |= 1;
         } else if (var1 > this.x + this.width) {
            var5 |= 4;
         }

         if (this.height <= 0.0D) {
            var5 |= 10;
         } else if (var3 < this.y) {
            var5 |= 2;
         } else if (var3 > this.y + this.height) {
            var5 |= 8;
         }

         return var5;
      }

      public Rectangle2D getBounds2D() {
         return new Rectangle2D.Double(this.x, this.y, this.width, this.height);
      }

      public Rectangle2D createIntersection(Rectangle2D var1) {
         Rectangle2D.Double var2 = new Rectangle2D.Double();
         Rectangle2D.intersect(this, var1, var2);
         return var2;
      }

      public Rectangle2D createUnion(Rectangle2D var1) {
         Rectangle2D.Double var2 = new Rectangle2D.Double();
         Rectangle2D.union(this, var1, var2);
         return var2;
      }

      public String toString() {
         return this.getClass().getName() + "[x=" + this.x + ",y=" + this.y + ",w=" + this.width + ",h=" + this.height + "]";
      }
   }

   public static class Float extends Rectangle2D implements Serializable {
      public float x;
      public float y;
      public float width;
      public float height;
      private static final long serialVersionUID = 3798716824173675777L;

      public Float() {
      }

      public Float(float var1, float var2, float var3, float var4) {
         this.setRect(var1, var2, var3, var4);
      }

      public double getX() {
         return (double)this.x;
      }

      public double getY() {
         return (double)this.y;
      }

      public double getWidth() {
         return (double)this.width;
      }

      public double getHeight() {
         return (double)this.height;
      }

      public boolean isEmpty() {
         return this.width <= 0.0F || this.height <= 0.0F;
      }

      public void setRect(float var1, float var2, float var3, float var4) {
         this.x = var1;
         this.y = var2;
         this.width = var3;
         this.height = var4;
      }

      public void setRect(double var1, double var3, double var5, double var7) {
         this.x = (float)var1;
         this.y = (float)var3;
         this.width = (float)var5;
         this.height = (float)var7;
      }

      public void setRect(Rectangle2D var1) {
         this.x = (float)var1.getX();
         this.y = (float)var1.getY();
         this.width = (float)var1.getWidth();
         this.height = (float)var1.getHeight();
      }

      public int outcode(double var1, double var3) {
         int var5 = 0;
         if (this.width <= 0.0F) {
            var5 |= 5;
         } else if (var1 < (double)this.x) {
            var5 |= 1;
         } else if (var1 > (double)this.x + (double)this.width) {
            var5 |= 4;
         }

         if (this.height <= 0.0F) {
            var5 |= 10;
         } else if (var3 < (double)this.y) {
            var5 |= 2;
         } else if (var3 > (double)this.y + (double)this.height) {
            var5 |= 8;
         }

         return var5;
      }

      public Rectangle2D getBounds2D() {
         return new Rectangle2D.Float(this.x, this.y, this.width, this.height);
      }

      public Rectangle2D createIntersection(Rectangle2D var1) {
         Object var2;
         if (var1 instanceof Rectangle2D.Float) {
            var2 = new Rectangle2D.Float();
         } else {
            var2 = new Rectangle2D.Double();
         }

         Rectangle2D.intersect(this, var1, (Rectangle2D)var2);
         return (Rectangle2D)var2;
      }

      public Rectangle2D createUnion(Rectangle2D var1) {
         Object var2;
         if (var1 instanceof Rectangle2D.Float) {
            var2 = new Rectangle2D.Float();
         } else {
            var2 = new Rectangle2D.Double();
         }

         Rectangle2D.union(this, var1, (Rectangle2D)var2);
         return (Rectangle2D)var2;
      }

      public String toString() {
         return this.getClass().getName() + "[x=" + this.x + ",y=" + this.y + ",w=" + this.width + ",h=" + this.height + "]";
      }
   }
}
