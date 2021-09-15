package java.awt.font;

import java.awt.geom.Point2D;

public abstract class LayoutPath {
   public abstract boolean pointToPath(Point2D var1, Point2D var2);

   public abstract void pathToPoint(Point2D var1, boolean var2, Point2D var3);
}
