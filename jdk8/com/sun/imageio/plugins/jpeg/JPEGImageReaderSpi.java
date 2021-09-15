package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class JPEGImageReaderSpi extends ImageReaderSpi {
   private static String[] writerSpiNames = new String[]{"com.sun.imageio.plugins.jpeg.JPEGImageWriterSpi"};

   public JPEGImageReaderSpi() {
      super("Oracle Corporation", "0.5", JPEG.names, JPEG.suffixes, JPEG.MIMETypes, "com.sun.imageio.plugins.jpeg.JPEGImageReader", new Class[]{ImageInputStream.class}, writerSpiNames, true, "javax_imageio_jpeg_stream_1.0", "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat", (String[])null, (String[])null, true, "javax_imageio_jpeg_image_1.0", "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat", (String[])null, (String[])null);
   }

   public String getDescription(Locale var1) {
      return "Standard JPEG Image Reader";
   }

   public boolean canDecodeInput(Object var1) throws IOException {
      if (!(var1 instanceof ImageInputStream)) {
         return false;
      } else {
         ImageInputStream var2 = (ImageInputStream)var1;
         var2.mark();
         int var3 = var2.read();
         int var4 = var2.read();
         var2.reset();
         return var3 == 255 && var4 == 216;
      }
   }

   public ImageReader createReaderInstance(Object var1) throws IIOException {
      return new JPEGImageReader(this);
   }
}
