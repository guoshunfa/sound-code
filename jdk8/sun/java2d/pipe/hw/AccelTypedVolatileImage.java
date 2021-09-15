package sun.java2d.pipe.hw;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.ImageCapabilities;
import sun.awt.image.SunVolatileImage;

public class AccelTypedVolatileImage extends SunVolatileImage {
   public AccelTypedVolatileImage(GraphicsConfiguration var1, int var2, int var3, int var4, int var5) {
      super((Component)null, var1, var2, var3, (Object)null, var4, (ImageCapabilities)null, var5);
   }

   public Graphics2D createGraphics() {
      if (this.getForcedAccelSurfaceType() == 3) {
         throw new UnsupportedOperationException("Can't render to a non-RT Texture");
      } else {
         return super.createGraphics();
      }
   }
}
