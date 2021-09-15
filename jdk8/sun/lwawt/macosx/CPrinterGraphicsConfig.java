package sun.lwawt.macosx;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.VolatileImage;
import java.awt.print.PageFormat;

public class CPrinterGraphicsConfig extends GraphicsConfiguration {
   GraphicsDevice gd = new CPrinterDevice(this);
   PageFormat pf;

   public static CPrinterGraphicsConfig getConfig(PageFormat var0) {
      return new CPrinterGraphicsConfig(var0);
   }

   public CPrinterGraphicsConfig(PageFormat var1) {
      this.pf = var1;
   }

   public PageFormat getPageFormat() {
      return this.pf;
   }

   public GraphicsDevice getDevice() {
      return this.gd;
   }

   public BufferedImage createCompatibleImage(int var1, int var2) {
      return this.createCompatibleImage(var1, var2, 1);
   }

   public VolatileImage createCompatibleVolatileImage(int var1, int var2) {
      return this.createCompatibleVolatileImage(var1, var2, 1);
   }

   public VolatileImage createCompatibleVolatileImage(int var1, int var2, int var3) {
      return null;
   }

   public BufferedImage createCompatibleImage(int var1, int var2, int var3) {
      return null;
   }

   public ColorModel getColorModel() {
      return this.getColorModel(1);
   }

   public ColorModel getColorModel(int var1) {
      return ColorModel.getRGBdefault();
   }

   public AffineTransform getDefaultTransform() {
      return new AffineTransform();
   }

   public AffineTransform getNormalizingTransform() {
      return new AffineTransform();
   }

   public Rectangle getBounds() {
      return new Rectangle(0, 0, (int)this.pf.getWidth(), (int)this.pf.getHeight());
   }
}
