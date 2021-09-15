package com.sun.imageio.spi;

import java.io.File;
import java.util.Locale;
import javax.imageio.spi.ImageOutputStreamSpi;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class FileImageOutputStreamSpi extends ImageOutputStreamSpi {
   private static final String vendorName = "Oracle Corporation";
   private static final String version = "1.0";
   private static final Class outputClass = File.class;

   public FileImageOutputStreamSpi() {
      super("Oracle Corporation", "1.0", outputClass);
   }

   public String getDescription(Locale var1) {
      return "Service provider that instantiates a FileImageOutputStream from a File";
   }

   public ImageOutputStream createOutputStreamInstance(Object var1, boolean var2, File var3) {
      if (var1 instanceof File) {
         try {
            return new FileImageOutputStream((File)var1);
         } catch (Exception var5) {
            var5.printStackTrace();
            return null;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }
}
