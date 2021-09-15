package java.awt;

import java.awt.image.ColorModel;
import java.awt.image.Raster;

public interface PaintContext {
   void dispose();

   ColorModel getColorModel();

   Raster getRaster(int var1, int var2, int var3, int var4);
}
