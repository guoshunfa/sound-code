package sun.java2d.cmm;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public interface ColorTransform {
   int Any = -1;
   int In = 1;
   int Out = 2;
   int Gamut = 3;
   int Simulation = 4;

   int getNumInComponents();

   int getNumOutComponents();

   void colorConvert(BufferedImage var1, BufferedImage var2);

   void colorConvert(Raster var1, WritableRaster var2, float[] var3, float[] var4, float[] var5, float[] var6);

   void colorConvert(Raster var1, WritableRaster var2);

   short[] colorConvert(short[] var1, short[] var2);

   byte[] colorConvert(byte[] var1, byte[] var2);
}
