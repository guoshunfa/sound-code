package com.sun.imageio.plugins.bmp;

import java.io.IOException;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

public class BMPImageReaderSpi extends ImageReaderSpi {
   private static String[] writerSpiNames = new String[]{"com.sun.imageio.plugins.bmp.BMPImageWriterSpi"};
   private static String[] formatNames = new String[]{"bmp", "BMP"};
   private static String[] entensions = new String[]{"bmp"};
   private static String[] mimeType = new String[]{"image/bmp"};
   private boolean registered = false;

   public BMPImageReaderSpi() {
      super("Oracle Corporation", "1.0", formatNames, entensions, mimeType, "com.sun.imageio.plugins.bmp.BMPImageReader", new Class[]{ImageInputStream.class}, writerSpiNames, false, (String)null, (String)null, (String[])null, (String[])null, true, "javax_imageio_bmp_1.0", "com.sun.imageio.plugins.bmp.BMPMetadataFormat", (String[])null, (String[])null);
   }

   public void onRegistration(ServiceRegistry var1, Class<?> var2) {
      if (!this.registered) {
         this.registered = true;
      }
   }

   public String getDescription(Locale var1) {
      return "Standard BMP Image Reader";
   }

   public boolean canDecodeInput(Object var1) throws IOException {
      if (!(var1 instanceof ImageInputStream)) {
         return false;
      } else {
         ImageInputStream var2 = (ImageInputStream)var1;
         byte[] var3 = new byte[2];
         var2.mark();
         var2.readFully(var3);
         var2.reset();
         return var3[0] == 66 && var3[1] == 77;
      }
   }

   public ImageReader createReaderInstance(Object var1) throws IIOException {
      return new BMPImageReader(this);
   }
}
