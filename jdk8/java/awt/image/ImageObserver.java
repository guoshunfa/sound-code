package java.awt.image;

import java.awt.Image;

public interface ImageObserver {
   int WIDTH = 1;
   int HEIGHT = 2;
   int PROPERTIES = 4;
   int SOMEBITS = 8;
   int FRAMEBITS = 16;
   int ALLBITS = 32;
   int ERROR = 64;
   int ABORT = 128;

   boolean imageUpdate(Image var1, int var2, int var3, int var4, int var5, int var6);
}
