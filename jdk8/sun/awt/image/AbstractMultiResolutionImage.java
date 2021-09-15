package sun.awt.image;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

public abstract class AbstractMultiResolutionImage extends Image implements MultiResolutionImage {
   public int getWidth(ImageObserver var1) {
      return this.getBaseImage().getWidth((ImageObserver)null);
   }

   public int getHeight(ImageObserver var1) {
      return this.getBaseImage().getHeight((ImageObserver)null);
   }

   public ImageProducer getSource() {
      return this.getBaseImage().getSource();
   }

   public Graphics getGraphics() {
      return this.getBaseImage().getGraphics();
   }

   public Object getProperty(String var1, ImageObserver var2) {
      return this.getBaseImage().getProperty(var1, var2);
   }

   protected abstract Image getBaseImage();
}
