package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Enumeration;
import java.util.Vector;
import sun.awt.geom.AreaOp;
import sun.awt.geom.Crossings;
import sun.awt.geom.Curve;

public class Area implements Shape, Cloneable {
   private static Vector EmptyCurves = new Vector();
   private Vector curves;
   private Rectangle2D cachedBounds;

   public Area() {
      this.curves = EmptyCurves;
   }

   public Area(Shape var1) {
      if (var1 instanceof Area) {
         this.curves = ((Area)var1).curves;
      } else {
         this.curves = pathToCurves(var1.getPathIterator((AffineTransform)null));
      }

   }

   private static Vector pathToCurves(PathIterator var0) {
      Vector var1 = new Vector();
      int var2 = var0.getWindingRule();
      double[] var3 = new double[23];
      double var4 = 0.0D;
      double var6 = 0.0D;
      double var8 = 0.0D;

      double var10;
      for(var10 = 0.0D; !var0.isDone(); var0.next()) {
         double var12;
         double var14;
         switch(var0.currentSegment(var3)) {
         case 0:
            Curve.insertLine(var1, var8, var10, var4, var6);
            var8 = var4 = var3[0];
            var10 = var6 = var3[1];
            Curve.insertMove(var1, var4, var6);
            break;
         case 1:
            var12 = var3[0];
            var14 = var3[1];
            Curve.insertLine(var1, var8, var10, var12, var14);
            var8 = var12;
            var10 = var14;
            break;
         case 2:
            var12 = var3[2];
            var14 = var3[3];
            Curve.insertQuad(var1, var8, var10, var3);
            var8 = var12;
            var10 = var14;
            break;
         case 3:
            var12 = var3[4];
            var14 = var3[5];
            Curve.insertCubic(var1, var8, var10, var3);
            var8 = var12;
            var10 = var14;
            break;
         case 4:
            Curve.insertLine(var1, var8, var10, var4, var6);
            var8 = var4;
            var10 = var6;
         }
      }

      Curve.insertLine(var1, var8, var10, var4, var6);
      Object var16;
      if (var2 == 0) {
         var16 = new AreaOp.EOWindOp();
      } else {
         var16 = new AreaOp.NZWindOp();
      }

      return ((AreaOp)var16).calculate(var1, EmptyCurves);
   }

   public void add(Area var1) {
      this.curves = (new AreaOp.AddOp()).calculate(this.curves, var1.curves);
      this.invalidateBounds();
   }

   public void subtract(Area var1) {
      this.curves = (new AreaOp.SubOp()).calculate(this.curves, var1.curves);
      this.invalidateBounds();
   }

   public void intersect(Area var1) {
      this.curves = (new AreaOp.IntOp()).calculate(this.curves, var1.curves);
      this.invalidateBounds();
   }

   public void exclusiveOr(Area var1) {
      this.curves = (new AreaOp.XorOp()).calculate(this.curves, var1.curves);
      this.invalidateBounds();
   }

   public void reset() {
      this.curves = new Vector();
      this.invalidateBounds();
   }

   public boolean isEmpty() {
      return this.curves.size() == 0;
   }

   public boolean isPolygonal() {
      Enumeration var1 = this.curves.elements();

      do {
         if (!var1.hasMoreElements()) {
            return true;
         }
      } while(((Curve)var1.nextElement()).getOrder() <= 1);

      return false;
   }

   public boolean isRectangular() {
      int var1 = this.curves.size();
      if (var1 == 0) {
         return true;
      } else if (var1 > 3) {
         return false;
      } else {
         Curve var2 = (Curve)this.curves.get(1);
         Curve var3 = (Curve)this.curves.get(2);
         if (var2.getOrder() == 1 && var3.getOrder() == 1) {
            if (var2.getXTop() == var2.getXBot() && var3.getXTop() == var3.getXBot()) {
               return var2.getYTop() == var3.getYTop() && var2.getYBot() == var3.getYBot();
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public boolean isSingular() {
      if (this.curves.size() < 3) {
         return true;
      } else {
         Enumeration var1 = this.curves.elements();
         var1.nextElement();

         do {
            if (!var1.hasMoreElements()) {
               return true;
            }
         } while(((Curve)var1.nextElement()).getOrder() != 0);

         return false;
      }
   }

   private void invalidateBounds() {
      this.cachedBounds = null;
   }

   private Rectangle2D getCachedBounds() {
      if (this.cachedBounds != null) {
         return this.cachedBounds;
      } else {
         Rectangle2D.Double var1 = new Rectangle2D.Double();
         if (this.curves.size() > 0) {
            Curve var2 = (Curve)this.curves.get(0);
            var1.setRect(var2.getX0(), var2.getY0(), 0.0D, 0.0D);

            for(int var3 = 1; var3 < this.curves.size(); ++var3) {
               ((Curve)this.curves.get(var3)).enlarge(var1);
            }
         }

         return this.cachedBounds = var1;
      }
   }

   public Rectangle2D getBounds2D() {
      return this.getCachedBounds().getBounds2D();
   }

   public Rectangle getBounds() {
      return this.getCachedBounds().getBounds();
   }

   public Object clone() {
      return new Area(this);
   }

   public boolean equals(Area var1) {
      if (var1 == this) {
         return true;
      } else if (var1 == null) {
         return false;
      } else {
         Vector var2 = (new AreaOp.XorOp()).calculate(this.curves, var1.curves);
         return var2.isEmpty();
      }
   }

   public void transform(AffineTransform var1) {
      if (var1 == null) {
         throw new NullPointerException("transform must not be null");
      } else {
         this.curves = pathToCurves(this.getPathIterator(var1));
         this.invalidateBounds();
      }
   }

   public Area createTransformedArea(AffineTransform var1) {
      Area var2 = new Area(this);
      var2.transform(var1);
      return var2;
   }

   public boolean contains(double var1, double var3) {
      if (!this.getCachedBounds().contains(var1, var3)) {
         return false;
      } else {
         Enumeration var5 = this.curves.elements();

         int var6;
         Curve var7;
         for(var6 = 0; var5.hasMoreElements(); var6 += var7.crossingsFor(var1, var3)) {
            var7 = (Curve)var5.nextElement();
         }

         return (var6 & 1) == 1;
      }
   }

   public boolean contains(Point2D var1) {
      return this.contains(var1.getX(), var1.getY());
   }

   public boolean contains(double var1, double var3, double var5, double var7) {
      if (var5 >= 0.0D && var7 >= 0.0D) {
         if (!this.getCachedBounds().contains(var1, var3, var5, var7)) {
            return false;
         } else {
            Crossings var9 = Crossings.findCrossings(this.curves, var1, var3, var1 + var5, var3 + var7);
            return var9 != null && var9.covers(var3, var3 + var7);
         }
      } else {
         return false;
      }
   }

   public boolean contains(Rectangle2D var1) {
      return this.contains(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
   }

   public boolean intersects(double var1, double var3, double var5, double var7) {
      if (var5 >= 0.0D && var7 >= 0.0D) {
         if (!this.getCachedBounds().intersects(var1, var3, var5, var7)) {
            return false;
         } else {
            Crossings var9 = Crossings.findCrossings(this.curves, var1, var3, var1 + var5, var3 + var7);
            return var9 == null || !var9.isEmpty();
         }
      } else {
         return false;
      }
   }

   public boolean intersects(Rectangle2D var1) {
      return this.intersects(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
   }

   public PathIterator getPathIterator(AffineTransform var1) {
      return new AreaIterator(this.curves, var1);
   }

   public PathIterator getPathIterator(AffineTransform var1, double var2) {
      return new FlatteningPathIterator(this.getPathIterator(var1), var2);
   }
}
