package com.sun.imageio.plugins.bmp;

import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageOutputStream;

public class BMPImageWriterSpi extends ImageWriterSpi {
   private static String[] readerSpiNames = new String[]{"com.sun.imageio.plugins.bmp.BMPImageReaderSpi"};
   private static String[] formatNames = new String[]{"bmp", "BMP"};
   private static String[] entensions = new String[]{"bmp"};
   private static String[] mimeType = new String[]{"image/bmp"};
   private boolean registered = false;

   public BMPImageWriterSpi() {
      super("Oracle Corporation", "1.0", formatNames, entensions, mimeType, "com.sun.imageio.plugins.bmp.BMPImageWriter", new Class[]{ImageOutputStream.class}, readerSpiNames, false, (String)null, (String)null, (String[])null, (String[])null, true, "javax_imageio_bmp_1.0", "com.sun.imageio.plugins.bmp.BMPMetadataFormat", (String[])null, (String[])null);
   }

   public String getDescription(Locale var1) {
      return "Standard BMP Image Writer";
   }

   public void onRegistration(ServiceRegistry var1, Class<?> var2) {
      if (!this.registered) {
         this.registered = true;
      }
   }

   public boolean canEncodeImage(ImageTypeSpecifier var1) {
      int var2 = var1.getSampleModel().getDataType();
      if (var2 >= 0 && var2 <= 3) {
         SampleModel var3 = var1.getSampleModel();
         int var4 = var3.getNumBands();
         if (var4 != 1 && var4 != 3) {
            return false;
         } else if (var4 == 1 && var2 != 0) {
            return false;
         } else {
            return var2 <= 0 || var3 instanceof SinglePixelPackedSampleModel;
         }
      } else {
         return false;
      }
   }

   public ImageWriter createWriterInstance(Object var1) throws IIOException {
      return new BMPImageWriter(this);
   }
}
