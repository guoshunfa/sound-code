package java.awt;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public interface CompositeContext {
   void dispose();

   void compose(Raster var1, Raster var2, WritableRaster var3);
}
