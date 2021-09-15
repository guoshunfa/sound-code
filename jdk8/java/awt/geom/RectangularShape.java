package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.beans.Transient;

public abstract class RectangularShape implements Shape, Cloneable {
   protected RectangularShape() {
   }

   public abstract double getX();

   public abstract double getY();

   public abstract double getWidth();

   public abstract double getHeight();

   public double getMinX() {
      return this.getX();
   }

   public double getMinY() {
      return this.getY();
   }

   public double getMaxX() {
      return this.getX() + this.getWidth();
   }

   public double getMaxY() {
      return this.getY() + this.getHeight();
   }

   public double getCenterX() {
      return this.getX() + this.getWidth() / 2.0D;
   }

   public double getCenterY() {
      return this.getY() + this.getHeight() / 2.0D;
   }

   @Transient
   public Rectangle2D getFrame() {
      return new Rectangle2D.Double(this.getX(), this.getY(), this.getWidth(), this.getHeight());
   }

   public abstract boolean isEmpty();

   public abstract void setFrame(double var1, double var3, double var5, double var7);

   public void setFrame(Point2D var1, Dimension2D var2) {
      this.setFrame(var1.getX(), var1.getY(), var2.getWidth(), var2.getHeight());
   }

   public void setFrame(Rectangle2D var1) {
      this.setFrame(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
   }

   public void setFrameFromDiagonal(double var1, double var3, double var5, double var7) {
      double var9;
      if (var5 < var1) {
         var9 = var1;
         var1 = var5;
         var5 = var9;
      }

      if (var7 < var3) {
         var9 = var3;
         var3 = var7;
         var7 = var9;
      }

      this.setFrame(var1, var3, var5 - var1, var7 - var3);
   }

   public void setFrameFromDiagonal(Point2D var1, Point2D var2) {
      this.setFrameFromDiagonal(var1.getX(), var1.getY(), var2.getX(), var2.getY());
   }

   public void setFrameFromCenter(double var1, double var3, double var5, double var7) {
      double var9 = Math.abs(var5 - var1);
      double var11 = Math.abs(var7 - var3);
      this.setFrame(var1 - var9, var3 - var11, var9 * 2.0D, var11 * 2.0D);
   }

   public void setFrameFromCenter(Point2D var1, Point2D var2) {
      this.setFrameFromCenter(var1.getX(), var1.getY(), var2.getX(), var2.getY());
   }

   public boolean contains(Point2D var1) {
      return this.contains(var1.getX(), var1.getY());
   }

   public boolean intersects(Rectangle2D var1) {
      return this.intersects(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
   }

   public boolean contains(Rectangle2D var1) {
      return this.contains(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
   }

   public Rectangle getBounds() {
      double var1 = this.getWidth();
      double var3 = this.getHeight();
      if (var1 >= 0.0D && var3 >= 0.0D) {
         double var5 = this.getX();
         double var7 = this.getY();
         double var9 = Math.floor(var5);
         double var11 = Math.floor(var7);
         double var13 = Math.ceil(var5 + var1);
         double var15 = Math.ceil(var7 + var3);
         return new Rectangle((int)var9, (int)var11, (int)(var13 - var9), (int)(var15 - var11));
      } else {
         return new Rectangle();
      }
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
}
