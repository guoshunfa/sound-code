package com.sun.imageio.spi;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

public class RAFImageInputStreamSpi extends ImageInputStreamSpi {
   private static final String vendorName = "Oracle Corporation";
   private static final String version = "1.0";
   private static final Class inputClass = RandomAccessFile.class;

   public RAFImageInputStreamSpi() {
      super("Oracle Corporation", "1.0", inputClass);
   }

   public String getDescription(Locale var1) {
      return "Service provider that instantiates a FileImageInputStream from a RandomAccessFile";
   }

   public ImageInputStream createInputStreamInstance(Object var1, boolean var2, File var3) {
      if (var1 instanceof RandomAccessFile) {
         try {
            return new FileImageInputStream((RandomAccessFile)var1);
         } catch (Exception var5) {
            return null;
         }
      } else {
         throw new IllegalArgumentException("input not a RandomAccessFile!");
      }
   }
}
