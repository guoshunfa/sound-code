package com.sun.imageio.plugins.wbmp;

import com.sun.imageio.plugins.common.ReaderUtil;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

public class WBMPImageReaderSpi extends ImageReaderSpi {
   private static final int MAX_WBMP_WIDTH = 1024;
   private static final int MAX_WBMP_HEIGHT = 768;
   private static String[] writerSpiNames = new String[]{"com.sun.imageio.plugins.wbmp.WBMPImageWriterSpi"};
   private static String[] formatNames = new String[]{"wbmp", "WBMP"};
   private static String[] entensions = new String[]{"wbmp"};
   private static String[] mimeType = new String[]{"image/vnd.wap.wbmp"};
   private boolean registered = false;

   public WBMPImageReaderSpi() {
      super("Oracle Corporation", "1.0", formatNames, entensions, mimeType, "com.sun.imageio.plugins.wbmp.WBMPImageReader", new Class[]{ImageInputStream.class}, writerSpiNames, true, (String)null, (String)null, (String[])null, (String[])null, true, "javax_imageio_wbmp_1.0", "com.sun.imageio.plugins.wbmp.WBMPMetadataFormat", (String[])null, (String[])null);
   }

   public void onRegistration(ServiceRegistry var1, Class<?> var2) {
      if (!this.registered) {
         this.registered = true;
      }
   }

   public String getDescription(Locale var1) {
      return "Standard WBMP Image Reader";
   }

   public boolean canDecodeInput(Object var1) throws IOException {
      if (!(var1 instanceof ImageInputStream)) {
         return false;
      } else {
         ImageInputStream var2 = (ImageInputStream)var1;
         var2.mark();

         try {
            byte var3 = var2.readByte();
            byte var4 = var2.readByte();
            if (var3 == 0 && var4 == 0) {
               int var15 = ReaderUtil.readMultiByteInteger(var2);
               int var6 = ReaderUtil.readMultiByteInteger(var2);
               if (var15 > 0 && var6 > 0) {
                  long var16 = var2.length();
                  if (var16 != -1L) {
                     var16 -= var2.getStreamPosition();
                     long var17 = (long)(var15 / 8 + (var15 % 8 == 0 ? 0 : 1));
                     boolean var11 = var16 == var17 * (long)var6;
                     return var11;
                  } else {
                     boolean var9 = var15 < 1024 && var6 < 768;
                     return var9;
                  }
               } else {
                  boolean var7 = false;
                  return var7;
               }
            } else {
               boolean var5 = false;
               return var5;
            }
         } finally {
            var2.reset();
         }
      }
   }

   public ImageReader createReaderInstance(Object var1) throws IIOException {
      return new WBMPImageReader(this);
   }
}
