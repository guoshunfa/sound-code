package sun.font;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public final class DelegatingShape implements Shape {
   Shape delegate;

   public DelegatingShape(Shape var1) {
      this.delegate = var1;
   }

   public Rectangle getBounds() {
      return this.delegate.getBounds();
   }

   public Rectangle2D getBounds2D() {
      return this.delegate.getBounds2D();
   }

   public boolean contains(double var1, double var3) {
      return this.delegate.contains(var1, var3);
   }

   public boolean contains(Point2D var1) {
      return this.delegate.contains(var1);
   }

   public boolean intersects(double var1, double var3, double var5, double var7) {
      return this.delegate.intersects(var1, var3, var5, var7);
   }

   public boolean intersects(Rectangle2D var1) {
      return this.delegate.intersects(var1);
   }

   public boolean contains(double var1, double var3, double var5, double var7) {
      return this.delegate.contains(var1, var3, var5, var7);
   }

   public boolean contains(Rectangle2D var1) {
      return this.delegate.contains(var1);
   }

   public PathIterator getPathIterator(AffineTransform var1) {
      return this.delegate.getPathIterator(var1);
   }

   public PathIterator getPathIterator(AffineTransform var1, double var2) {
      return this.delegate.getPathIterator(var1, var2);
   }
}
