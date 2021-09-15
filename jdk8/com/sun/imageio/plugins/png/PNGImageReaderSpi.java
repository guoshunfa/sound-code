package com.sun.imageio.plugins.png;

import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class PNGImageReaderSpi extends ImageReaderSpi {
   private static final String vendorName = "Oracle Corporation";
   private static final String version = "1.0";
   private static final String[] names = new String[]{"png", "PNG"};
   private static final String[] suffixes = new String[]{"png"};
   private static final String[] MIMETypes = new String[]{"image/png", "image/x-png"};
   private static final String readerClassName = "com.sun.imageio.plugins.png.PNGImageReader";
   private static final String[] writerSpiNames = new String[]{"com.sun.imageio.plugins.png.PNGImageWriterSpi"};

   public PNGImageReaderSpi() {
      super("Oracle Corporation", "1.0", names, suffixes, MIMETypes, "com.sun.imageio.plugins.png.PNGImageReader", new Class[]{ImageInputStream.class}, writerSpiNames, false, (String)null, (String)null, (String[])null, (String[])null, true, "javax_imageio_png_1.0", "com.sun.imageio.plugins.png.PNGMetadataFormat", (String[])null, (String[])null);
   }

   public String getDescription(Locale var1) {
      return "Standard PNG image reader";
   }

   public boolean canDecodeInput(Object var1) throws IOException {
      if (!(var1 instanceof ImageInputStream)) {
         return false;
      } else {
         ImageInputStream var2 = (ImageInputStream)var1;
         byte[] var3 = new byte[8];
         var2.mark();
         var2.readFully(var3);
         var2.reset();
         return var3[0] == -119 && var3[1] == 80 && var3[2] == 78 && var3[3] == 71 && var3[4] == 13 && var3[5] == 10 && var3[6] == 26 && var3[7] == 10;
      }
   }

   public ImageReader createReaderInstance(Object var1) {
      return new PNGImageReader(this);
   }
}
