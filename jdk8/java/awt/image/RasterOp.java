package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface RasterOp {
   WritableRaster filter(Raster var1, WritableRaster var2);

   Rectangle2D getBounds2D(Raster var1);

   WritableRaster createCompatibleDestRaster(Raster var1);

   Point2D getPoint2D(Point2D var1, Point2D var2);

   RenderingHints getRenderingHints();
}
