package com.sun.imageio.plugins.jpeg;

import java.awt.image.SampleModel;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class JPEGImageWriterSpi extends ImageWriterSpi {
   private static String[] readerSpiNames = new String[]{"com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi"};

   public JPEGImageWriterSpi() {
      super("Oracle Corporation", "0.5", JPEG.names, JPEG.suffixes, JPEG.MIMETypes, "com.sun.imageio.plugins.jpeg.JPEGImageWriter", new Class[]{ImageOutputStream.class}, readerSpiNames, true, "javax_imageio_jpeg_stream_1.0", "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat", (String[])null, (String[])null, true, "javax_imageio_jpeg_image_1.0", "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat", (String[])null, (String[])null);
   }

   public String getDescription(Locale var1) {
      return "Standard JPEG Image Writer";
   }

   public boolean isFormatLossless() {
      return false;
   }

   public boolean canEncodeImage(ImageTypeSpecifier var1) {
      SampleModel var2 = var1.getSampleModel();
      int[] var3 = var2.getSampleSize();
      int var4 = var3[0];

      for(int var5 = 1; var5 < var3.length; ++var5) {
         if (var3[var5] > var4) {
            var4 = var3[var5];
         }
      }

      if (var4 >= 1 && var4 <= 8) {
         return true;
      } else {
         return false;
      }
   }

   public ImageWriter createWriterInstance(Object var1) throws IIOException {
      return new JPEGImageWriter(this);
   }
}
