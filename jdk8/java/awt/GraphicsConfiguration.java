package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import sun.awt.image.SunVolatileImage;

public abstract class GraphicsConfiguration {
   private static BufferCapabilities defaultBufferCaps;
   private static ImageCapabilities defaultImageCaps;

   protected GraphicsConfiguration() {
   }

   public abstract GraphicsDevice getDevice();

   public BufferedImage createCompatibleImage(int var1, int var2) {
      ColorModel var3 = this.getColorModel();
      WritableRaster var4 = var3.createCompatibleWritableRaster(var1, var2);
      return new BufferedImage(var3, var4, var3.isAlphaPremultiplied(), (Hashtable)null);
   }

   public BufferedImage createCompatibleImage(int var1, int var2, int var3) {
      if (this.getColorModel().getTransparency() == var3) {
         return this.createCompatibleImage(var1, var2);
      } else {
         ColorModel var4 = this.getColorModel(var3);
         if (var4 == null) {
            throw new IllegalArgumentException("Unknown transparency: " + var3);
         } else {
            WritableRaster var5 = var4.createCompatibleWritableRaster(var1, var2);
            return new BufferedImage(var4, var5, var4.isAlphaPremultiplied(), (Hashtable)null);
         }
      }
   }

   public VolatileImage createCompatibleVolatileImage(int var1, int var2) {
      VolatileImage var3 = null;

      try {
         var3 = this.createCompatibleVolatileImage(var1, var2, (ImageCapabilities)null, 1);
      } catch (AWTException var5) {
         assert false;
      }

      return var3;
   }

   public VolatileImage createCompatibleVolatileImage(int var1, int var2, int var3) {
      VolatileImage var4 = null;

      try {
         var4 = this.createCompatibleVolatileImage(var1, var2, (ImageCapabilities)null, var3);
      } catch (AWTException var6) {
         assert false;
      }

      return var4;
   }

   public VolatileImage createCompatibleVolatileImage(int var1, int var2, ImageCapabilities var3) throws AWTException {
      return this.createCompatibleVolatileImage(var1, var2, var3, 1);
   }

   public VolatileImage createCompatibleVolatileImage(int var1, int var2, ImageCapabilities var3, int var4) throws AWTException {
      SunVolatileImage var5 = new SunVolatileImage(this, var1, var2, var4, var3);
      if (var3 != null && var3.isAccelerated() && !var5.getCapabilities().isAccelerated()) {
         throw new AWTException("Supplied image capabilities could not be met by this graphics configuration.");
      } else {
         return var5;
      }
   }

   public abstract ColorModel getColorModel();

   public abstract ColorModel getColorModel(int var1);

   public abstract AffineTransform getDefaultTransform();

   public abstract AffineTransform getNormalizingTransform();

   public abstract Rectangle getBounds();

   public BufferCapabilities getBufferCapabilities() {
      if (defaultBufferCaps == null) {
         defaultBufferCaps = new GraphicsConfiguration.DefaultBufferCapabilities(this.getImageCapabilities());
      }

      return defaultBufferCaps;
   }

   public ImageCapabilities getImageCapabilities() {
      if (defaultImageCaps == null) {
         defaultImageCaps = new ImageCapabilities(false);
      }

      return defaultImageCaps;
   }

   public boolean isTranslucencyCapable() {
      return false;
   }

   private static class DefaultBufferCapabilities extends BufferCapabilities {
      public DefaultBufferCapabilities(ImageCapabilities var1) {
         super(var1, var1, (BufferCapabilities.FlipContents)null);
      }
   }
}
