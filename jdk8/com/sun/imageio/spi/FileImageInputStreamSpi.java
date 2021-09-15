package com.sun.imageio.spi;

import java.io.File;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

public class FileImageInputStreamSpi extends ImageInputStreamSpi {
   private static final String vendorName = "Oracle Corporation";
   private static final String version = "1.0";
   private static final Class inputClass = File.class;

   public FileImageInputStreamSpi() {
      super("Oracle Corporation", "1.0", inputClass);
   }

   public String getDescription(Locale var1) {
      return "Service provider that instantiates a FileImageInputStream from a File";
   }

   public ImageInputStream createInputStreamInstance(Object var1, boolean var2, File var3) {
      if (var1 instanceof File) {
         try {
            return new FileImageInputStream((File)var1);
         } catch (Exception var5) {
            return null;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }
}
