package java.awt.image;

import java.util.Hashtable;

public interface ImageConsumer {
   int RANDOMPIXELORDER = 1;
   int TOPDOWNLEFTRIGHT = 2;
   int COMPLETESCANLINES = 4;
   int SINGLEPASS = 8;
   int SINGLEFRAME = 16;
   int IMAGEERROR = 1;
   int SINGLEFRAMEDONE = 2;
   int STATICIMAGEDONE = 3;
   int IMAGEABORTED = 4;

   void setDimensions(int var1, int var2);

   void setProperties(Hashtable<?, ?> var1);

   void setColorModel(ColorModel var1);

   void setHints(int var1);

   void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, byte[] var6, int var7, int var8);

   void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, int[] var6, int var7, int var8);

   void imageComplete(int var1);
}
