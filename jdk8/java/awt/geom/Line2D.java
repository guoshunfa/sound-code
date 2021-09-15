package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.Serializable;

public abstract class Line2D implements Shape, Cloneable {
   protected Line2D() {
   }

   public abstract double getX1();

   public abstract double getY1();

   public abstract Point2D getP1();

   public abstract double getX2();

   public abstract double getY2();

   public abstract Point2D getP2();

   public abstract void setLine(double var1, double var3, double var5, double var7);

   public void setLine(Point2D var1, Point2D var2) {
      this.setLine(var1.getX(), var1.getY(), var2.getX(), var2.getY());
   }

   public void setLine(Line2D var1) {
      this.setLine(var1.getX1(), var1.getY1(), var1.getX2(), var1.getY2());
   }

   public static int relativeCCW(double var0, double var2, double var4, double var6, double var8, double var10) {
      var4 -= var0;
      var6 -= var2;
      var8 -= var0;
      var10 -= var2;
      double var12 = var8 * var6 - var10 * var4;
      if (var12 == 0.0D) {
         var12 = var8 * var4 + var10 * var6;
         if (var12 > 0.0D) {
            var8 -= var4;
            var10 -= var6;
            var12 = var8 * var4 + var10 * var6;
            if (var12 < 0.0D) {
               var12 = 0.0D;
            }
         }
      }

      return var12 < 0.0D ? -1 : (var12 > 0.0D ? 1 : 0);
   }

   public int relativeCCW(double var1, double var3) {
      return relativeCCW(this.getX1(), this.getY1(), this.getX2(), this.getY2(), var1, var3);
   }

   public int relativeCCW(Point2D var1) {
      return relativeCCW(this.getX1(), this.getY1(), this.getX2(), this.getY2(), var1.getX(), var1.getY());
   }

   public static boolean linesIntersect(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14) {
      return relativeCCW(var0, var2, var4, var6, var8, var10) * relativeCCW(var0, var2, var4, var6, var12, var14) <= 0 && relativeCCW(var8, var10, var12, var14, var0, var2) * relativeCCW(var8, var10, var12, var14, var4, var6) <= 0;
   }

   public boolean intersectsLine(double var1, double var3, double var5, double var7) {
      return linesIntersect(var1, var3, var5, var7, this.getX1(), this.getY1(), this.getX2(), this.getY2());
   }

   public boolean intersectsLine(Line2D var1) {
      return linesIntersect(var1.getX1(), var1.getY1(), var1.getX2(), var1.getY2(), this.getX1(), this.getY1(), this.getX2(), this.getY2());
   }

   public static double ptSegDistSq(double var0, double var2, double var4, double var6, double var8, double var10) {
      var4 -= var0;
      var6 -= var2;
      var8 -= var0;
      var10 -= var2;
      double var12 = var8 * var4 + var10 * var6;
      double var14;
      if (var12 <= 0.0D) {
         var14 = 0.0D;
      } else {
         var8 = var4 - var8;
         var10 = var6 - var10;
         var12 = var8 * var4 + var10 * var6;
         if (var12 <= 0.0D) {
            var14 = 0.0D;
         } else {
            var14 = var12 * var12 / (var4 * var4 + var6 * var6);
         }
      }

      double var16 = var8 * var8 + var10 * var10 - var14;
      if (var16 < 0.0D) {
         var16 = 0.0D;
      }

      return var16;
   }

   public static double ptSegDist(double var0, double var2, double var4, double var6, double var8, double var10) {
      return Math.sqrt(ptSegDistSq(var0, var2, var4, var6, var8, var10));
   }

   public double ptSegDistSq(double var1, double var3) {
      return ptSegDistSq(this.getX1(), this.getY1(), this.getX2(), this.getY2(), var1, var3);
   }

   public double ptSegDistSq(Point2D var1) {
      return ptSegDistSq(this.getX1(), this.getY1(), this.getX2(), this.getY2(), var1.getX(), var1.getY());
   }

   public double ptSegDist(double var1, double var3) {
      return ptSegDist(this.getX1(), this.getY1(), this.getX2(), this.getY2(), var1, var3);
   }

   public double ptSegDist(Point2D var1) {
      return ptSegDist(this.getX1(), this.getY1(), this.getX2(), this.getY2(), var1.getX(), var1.getY());
   }

   public static double ptLineDistSq(double var0, double var2, double var4, double var6, double var8, double var10) {
      var4 -= var0;
      var6 -= var2;
      var8 -= var0;
      var10 -= var2;
      double var12 = var8 * var4 + var10 * var6;
      double var14 = var12 * var12 / (var4 * var4 + var6 * var6);
      double var16 = var8 * var8 + var10 * var10 - var14;
      if (var16 < 0.0D) {
         var16 = 0.0D;
      }

      return var16;
   }

   public static double ptLineDist(double var0, double var2, double var4, double var6, double var8, double var10) {
      return Math.sqrt(ptLineDistSq(var0, var2, var4, var6, var8, var10));
   }

   public double ptLineDistSq(double var1, double var3) {
      return ptLineDistSq(this.getX1(), this.getY1(), this.getX2(), this.getY2(), var1, var3);
   }

   public double ptLineDistSq(Point2D var1) {
      return ptLineDistSq(this.getX1(), this.getY1(), this.getX2(), this.getY2(), var1.getX(), var1.getY());
   }

   public double ptLineDist(double var1, double var3) {
      return ptLineDist(this.getX1(), this.getY1(), this.getX2(), this.getY2(), var1, var3);
   }

   public double ptLineDist(Point2D var1) {
      return ptLineDist(this.getX1(), this.getY1(), this.getX2(), this.getY2(), var1.getX(), var1.getY());
   }

   public boolean contains(double var1, double var3) {
      return false;
   }

   public boolean contains(Point2D var1) {
      return false;
   }

   public boolean intersects(double var1, double var3, double var5, double var7) {
      return this.intersects(new Rectangle2D.Double(var1, var3, var5, var7));
   }

   public boolean intersects(Rectangle2D var1) {
      return var1.intersectsLine(this.getX1(), this.getY1(), this.getX2(), this.getY2());
   }

   public boolean contains(double var1, double var3, double var5, double var7) {
      return false;
   }

   public boolean contains(Rectangle2D var1) {
      return false;
   }

   public Rectangle getBounds() {
      return this.getBounds2D().getBounds();
   }

   public PathIterator getPathIterator(AffineTransform var1) {
      return new LineIterator(this, var1);
   }

   public PathIterator getPathIterator(AffineTransform var1, double var2) {
      return new LineIterator(this, var1);
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   public static class Double extends Line2D implements Serializable {
      public double x1;
      public double y1;
      public double x2;
      public double y2;
      private static final long serialVersionUID = 7979627399746467499L;

      public Double() {
      }

      public Double(double var1, double var3, double var5, double var7) {
         this.setLine(var1, var3, var5, var7);
      }

      public Double(Point2D var1, Point2D var2) {
         this.setLine(var1, var2);
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

      public double getX2() {
         return this.x2;
      }

      public double getY2() {
         return this.y2;
      }

      public Point2D getP2() {
         return new Point2D.Double(this.x2, this.y2);
      }

      public void setLine(double var1, double var3, double var5, double var7) {
         this.x1 = var1;
         this.y1 = var3;
         this.x2 = var5;
         this.y2 = var7;
      }

      public Rectangle2D getBounds2D() {
         double var1;
         double var5;
         if (this.x1 < this.x2) {
            var1 = this.x1;
            var5 = this.x2 - this.x1;
         } else {
            var1 = this.x2;
            var5 = this.x1 - this.x2;
         }

         double var3;
         double var7;
         if (this.y1 < this.y2) {
            var3 = this.y1;
            var7 = this.y2 - this.y1;
         } else {
            var3 = this.y2;
            var7 = this.y1 - this.y2;
         }

         return new Rectangle2D.Double(var1, var3, var5, var7);
      }
   }

   public static class Float extends Line2D implements Serializable {
      public float x1;
      public float y1;
      public float x2;
      public float y2;
      private static final long serialVersionUID = 6161772511649436349L;

      public Float() {
      }

      public Float(float var1, float var2, float var3, float var4) {
         this.setLine(var1, var2, var3, var4);
      }

      public Float(Point2D var1, Point2D var2) {
         this.setLine(var1, var2);
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

      public double getX2() {
         return (double)this.x2;
      }

      public double getY2() {
         return (double)this.y2;
      }

      public Point2D getP2() {
         return new Point2D.Float(this.x2, this.y2);
      }

      public void setLine(double var1, double var3, double var5, double var7) {
         this.x1 = (float)var1;
         this.y1 = (float)var3;
         this.x2 = (float)var5;
         this.y2 = (float)var7;
      }

      public void setLine(float var1, float var2, float var3, float var4) {
         this.x1 = var1;
         this.y1 = var2;
         this.x2 = var3;
         this.y2 = var4;
      }

      public Rectangle2D getBounds2D() {
         float var1;
         float var3;
         if (this.x1 < this.x2) {
            var1 = this.x1;
            var3 = this.x2 - this.x1;
         } else {
            var1 = this.x2;
            var3 = this.x1 - this.x2;
         }

         float var2;
         float var4;
         if (this.y1 < this.y2) {
            var2 = this.y1;
            var4 = this.y2 - this.y1;
         } else {
            var2 = this.y2;
            var4 = this.y1 - this.y2;
         }

         return new Rectangle2D.Float(var1, var2, var3, var4);
      }
   }
}
