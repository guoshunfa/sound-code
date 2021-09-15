package sun.awt.image;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class BufferedImageGraphicsConfig extends GraphicsConfiguration {
   private static final int numconfigs = 12;
   private static BufferedImageGraphicsConfig[] configs = new BufferedImageGraphicsConfig[12];
   GraphicsDevice gd;
   ColorModel model;
   Raster raster;
   int width;
   int height;

   public static BufferedImageGraphicsConfig getConfig(BufferedImage var0) {
      int var2 = var0.getType();
      BufferedImageGraphicsConfig var1;
      if (var2 > 0 && var2 < 12) {
         var1 = configs[var2];
         if (var1 != null) {
            return var1;
         }
      }

      var1 = new BufferedImageGraphicsConfig(var0, (Component)null);
      if (var2 > 0 && var2 < 12) {
         configs[var2] = var1;
      }

      return var1;
   }

   public BufferedImageGraphicsConfig(BufferedImage var1, Component var2) {
      if (var2 == null) {
         this.gd = new BufferedImageDevice(this);
      } else {
         Graphics2D var3 = (Graphics2D)var2.getGraphics();
         this.gd = var3.getDeviceConfiguration().getDevice();
      }

      this.model = var1.getColorModel();
      this.raster = var1.getRaster().createCompatibleWritableRaster(1, 1);
      this.width = var1.getWidth();
      this.height = var1.getHeight();
   }

   public GraphicsDevice getDevice() {
      return this.gd;
   }

   public BufferedImage createCompatibleImage(int var1, int var2) {
      WritableRaster var3 = this.raster.createCompatibleWritableRaster(var1, var2);
      return new BufferedImage(this.model, var3, this.model.isAlphaPremultiplied(), (Hashtable)null);
   }

   public ColorModel getColorModel() {
      return this.model;
   }

   public ColorModel getColorModel(int var1) {
      if (this.model.getTransparency() == var1) {
         return this.model;
      } else {
         switch(var1) {
         case 1:
            return new DirectColorModel(24, 16711680, 65280, 255);
         case 2:
            return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
         case 3:
            return ColorModel.getRGBdefault();
         default:
            return null;
         }
      }
   }

   public AffineTransform getDefaultTransform() {
      return new AffineTransform();
   }

   public AffineTransform getNormalizingTransform() {
      return new AffineTransform();
   }

   public Rectangle getBounds() {
      return new Rectangle(0, 0, this.width, this.height);
   }
}
