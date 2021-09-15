package com.sun.imageio.plugins.gif;

import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class GIFImageReaderSpi extends ImageReaderSpi {
   private static final String vendorName = "Oracle Corporation";
   private static final String version = "1.0";
   private static final String[] names = new String[]{"gif", "GIF"};
   private static final String[] suffixes = new String[]{"gif"};
   private static final String[] MIMETypes = new String[]{"image/gif"};
   private static final String readerClassName = "com.sun.imageio.plugins.gif.GIFImageReader";
   private static final String[] writerSpiNames = new String[]{"com.sun.imageio.plugins.gif.GIFImageWriterSpi"};

   public GIFImageReaderSpi() {
      super("Oracle Corporation", "1.0", names, suffixes, MIMETypes, "com.sun.imageio.plugins.gif.GIFImageReader", new Class[]{ImageInputStream.class}, writerSpiNames, true, "javax_imageio_gif_stream_1.0", "com.sun.imageio.plugins.gif.GIFStreamMetadataFormat", (String[])null, (String[])null, true, "javax_imageio_gif_image_1.0", "com.sun.imageio.plugins.gif.GIFImageMetadataFormat", (String[])null, (String[])null);
   }

   public String getDescription(Locale var1) {
      return "Standard GIF image reader";
   }

   public boolean canDecodeInput(Object var1) throws IOException {
      if (!(var1 instanceof ImageInputStream)) {
         return false;
      } else {
         ImageInputStream var2 = (ImageInputStream)var1;
         byte[] var3 = new byte[6];
         var2.mark();
         var2.readFully(var3);
         var2.reset();
         return var3[0] == 71 && var3[1] == 73 && var3[2] == 70 && var3[3] == 56 && (var3[4] == 55 || var3[4] == 57) && var3[5] == 97;
      }
   }

   public ImageReader createReaderInstance(Object var1) {
      return new GIFImageReader(this);
   }
}
