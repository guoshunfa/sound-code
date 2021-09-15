package sun.print;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;

public class PrinterGraphicsConfig extends GraphicsConfiguration {
   static ColorModel theModel;
   GraphicsDevice gd;
   int pageWidth;
   int pageHeight;
   AffineTransform deviceTransform;

   public PrinterGraphicsConfig(String var1, AffineTransform var2, int var3, int var4) {
      this.pageWidth = var3;
      this.pageHeight = var4;
      this.deviceTransform = var2;
      this.gd = new PrinterGraphicsDevice(this, var1);
   }

   public GraphicsDevice getDevice() {
      return this.gd;
   }

   public ColorModel getColorModel() {
      if (theModel == null) {
         BufferedImage var1 = new BufferedImage(1, 1, 5);
         theModel = var1.getColorModel();
      }

      return theModel;
   }

   public ColorModel getColorModel(int var1) {
      switch(var1) {
      case 1:
         return this.getColorModel();
      case 2:
         return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
      case 3:
         return ColorModel.getRGBdefault();
      default:
         return null;
      }
   }

   public AffineTransform getDefaultTransform() {
      return new AffineTransform(this.deviceTransform);
   }

   public AffineTransform getNormalizingTransform() {
      return new AffineTransform();
   }

   public Rectangle getBounds() {
      return new Rectangle(0, 0, this.pageWidth, this.pageHeight);
   }
}
