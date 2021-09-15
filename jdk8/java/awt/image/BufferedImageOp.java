package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface BufferedImageOp {
   BufferedImage filter(BufferedImage var1, BufferedImage var2);

   Rectangle2D getBounds2D(BufferedImage var1);

   BufferedImage createCompatibleDestImage(BufferedImage var1, ColorModel var2);

   Point2D getPoint2D(Point2D var1, Point2D var2);

   RenderingHints getRenderingHints();
}
