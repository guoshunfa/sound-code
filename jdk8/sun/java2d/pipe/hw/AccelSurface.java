package sun.java2d.pipe.hw;

import java.awt.Rectangle;
import sun.java2d.Surface;

public interface AccelSurface extends BufferedContextProvider, Surface {
   int UNDEFINED = 0;
   int WINDOW = 1;
   int RT_PLAIN = 2;
   int TEXTURE = 3;
   int FLIP_BACKBUFFER = 4;
   int RT_TEXTURE = 5;

   int getType();

   long getNativeOps();

   long getNativeResource(int var1);

   void markDirty();

   boolean isValid();

   boolean isSurfaceLost();

   Rectangle getBounds();

   Rectangle getNativeBounds();
}
