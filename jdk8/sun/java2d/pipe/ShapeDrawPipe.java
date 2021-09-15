package sun.java2d.pipe;

import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public interface ShapeDrawPipe {
   void draw(SunGraphics2D var1, Shape var2);

   void fill(SunGraphics2D var1, Shape var2);
}
