package com.sun.imageio.plugins.wbmp;

import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.SampleModel;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageOutputStream;

public class WBMPImageWriterSpi extends ImageWriterSpi {
   private static String[] readerSpiNames = new String[]{"com.sun.imageio.plugins.wbmp.WBMPImageReaderSpi"};
   private static String[] formatNames = new String[]{"wbmp", "WBMP"};
   private static String[] entensions = new String[]{"wbmp"};
   private static String[] mimeType = new String[]{"image/vnd.wap.wbmp"};
   private boolean registered = false;

   public WBMPImageWriterSpi() {
      super("Oracle Corporation", "1.0", formatNames, entensions, mimeType, "com.sun.imageio.plugins.wbmp.WBMPImageWriter", new Class[]{ImageOutputStream.class}, readerSpiNames, true, (String)null, (String)null, (String[])null, (String[])null, true, (String)null, (String)null, (String[])null, (String[])null);
   }

   public String getDescription(Locale var1) {
      return "Standard WBMP Image Writer";
   }

   public void onRegistration(ServiceRegistry var1, Class<?> var2) {
      if (!this.registered) {
         this.registered = true;
      }
   }

   public boolean canEncodeImage(ImageTypeSpecifier var1) {
      SampleModel var2 = var1.getSampleModel();
      if (!(var2 instanceof MultiPixelPackedSampleModel)) {
         return false;
      } else {
         return var2.getSampleSize(0) == 1;
      }
   }

   public ImageWriter createWriterInstance(Object var1) throws IIOException {
      return new WBMPImageWriter(this);
   }
}
