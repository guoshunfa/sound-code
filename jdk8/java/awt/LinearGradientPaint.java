package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.beans.ConstructorProperties;

public final class LinearGradientPaint extends MultipleGradientPaint {
   private final Point2D start;
   private final Point2D end;

   public LinearGradientPaint(float var1, float var2, float var3, float var4, float[] var5, Color[] var6) {
      this(new Point2D.Float(var1, var2), new Point2D.Float(var3, var4), var5, var6, MultipleGradientPaint.CycleMethod.NO_CYCLE);
   }

   public LinearGradientPaint(float var1, float var2, float var3, float var4, float[] var5, Color[] var6, MultipleGradientPaint.CycleMethod var7) {
      this(new Point2D.Float(var1, var2), new Point2D.Float(var3, var4), var5, var6, var7);
   }

   public LinearGradientPaint(Point2D var1, Point2D var2, float[] var3, Color[] var4) {
      this(var1, var2, var3, var4, MultipleGradientPaint.CycleMethod.NO_CYCLE);
   }

   public LinearGradientPaint(Point2D var1, Point2D var2, float[] var3, Color[] var4, MultipleGradientPaint.CycleMethod var5) {
      this(var1, var2, var3, var4, var5, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform());
   }

   @ConstructorProperties({"startPoint", "endPoint", "fractions", "colors", "cycleMethod", "colorSpace", "transform"})
   public LinearGradientPaint(Point2D var1, Point2D var2, float[] var3, Color[] var4, MultipleGradientPaint.CycleMethod var5, MultipleGradientPaint.ColorSpaceType var6, AffineTransform var7) {
      super(var3, var4, var5, var6, var7);
      if (var1 != null && var2 != null) {
         if (var1.equals(var2)) {
            throw new IllegalArgumentException("Start point cannot equalendpoint");
         } else {
            this.start = new Point2D.Double(var1.getX(), var1.getY());
            this.end = new Point2D.Double(var2.getX(), var2.getY());
         }
      } else {
         throw new NullPointerException("Start and end points must benon-null");
      }
   }

   public PaintContext createContext(ColorModel var1, Rectangle var2, Rectangle2D var3, AffineTransform var4, RenderingHints var5) {
      var4 = new AffineTransform(var4);
      var4.concatenate(this.gradientTransform);
      if (this.fractions.length == 2 && this.cycleMethod != MultipleGradientPaint.CycleMethod.REPEAT && this.colorSpace == MultipleGradientPaint.ColorSpaceType.SRGB) {
         boolean var6 = this.cycleMethod != MultipleGradientPaint.CycleMethod.NO_CYCLE;
         return new GradientPaintContext(var1, this.start, this.end, var4, this.colors[0], this.colors[1], var6);
      } else {
         return new LinearGradientPaintContext(this, var1, var2, var3, var4, var5, this.start, this.end, this.fractions, this.colors, this.cycleMethod, this.colorSpace);
      }
   }

   public Point2D getStartPoint() {
      return new Point2D.Double(this.start.getX(), this.start.getY());
   }

   public Point2D getEndPoint() {
      return new Point2D.Double(this.end.getX(), this.end.getY());
   }
}
