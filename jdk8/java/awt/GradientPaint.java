package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.beans.ConstructorProperties;

public class GradientPaint implements Paint {
   Point2D.Float p1;
   Point2D.Float p2;
   Color color1;
   Color color2;
   boolean cyclic;

   public GradientPaint(float var1, float var2, Color var3, float var4, float var5, Color var6) {
      if (var3 != null && var6 != null) {
         this.p1 = new Point2D.Float(var1, var2);
         this.p2 = new Point2D.Float(var4, var5);
         this.color1 = var3;
         this.color2 = var6;
      } else {
         throw new NullPointerException("Colors cannot be null");
      }
   }

   public GradientPaint(Point2D var1, Color var2, Point2D var3, Color var4) {
      if (var2 != null && var4 != null && var1 != null && var3 != null) {
         this.p1 = new Point2D.Float((float)var1.getX(), (float)var1.getY());
         this.p2 = new Point2D.Float((float)var3.getX(), (float)var3.getY());
         this.color1 = var2;
         this.color2 = var4;
      } else {
         throw new NullPointerException("Colors and points should be non-null");
      }
   }

   public GradientPaint(float var1, float var2, Color var3, float var4, float var5, Color var6, boolean var7) {
      this(var1, var2, var3, var4, var5, var6);
      this.cyclic = var7;
   }

   @ConstructorProperties({"point1", "color1", "point2", "color2", "cyclic"})
   public GradientPaint(Point2D var1, Color var2, Point2D var3, Color var4, boolean var5) {
      this(var1, var2, var3, var4);
      this.cyclic = var5;
   }

   public Point2D getPoint1() {
      return new Point2D.Float(this.p1.x, this.p1.y);
   }

   public Color getColor1() {
      return this.color1;
   }

   public Point2D getPoint2() {
      return new Point2D.Float(this.p2.x, this.p2.y);
   }

   public Color getColor2() {
      return this.color2;
   }

   public boolean isCyclic() {
      return this.cyclic;
   }

   public PaintContext createContext(ColorModel var1, Rectangle var2, Rectangle2D var3, AffineTransform var4, RenderingHints var5) {
      return new GradientPaintContext(var1, this.p1, this.p2, var4, this.color1, this.color2, this.cyclic);
   }

   public int getTransparency() {
      int var1 = this.color1.getAlpha();
      int var2 = this.color2.getAlpha();
      return (var1 & var2) == 255 ? 1 : 3;
   }
}
