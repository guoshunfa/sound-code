package com.sun.imageio.spi;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import javax.imageio.spi.ImageOutputStreamSpi;
import javax.imageio.stream.FileCacheImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public class OutputStreamImageOutputStreamSpi extends ImageOutputStreamSpi {
   private static final String vendorName = "Oracle Corporation";
   private static final String version = "1.0";
   private static final Class outputClass = OutputStream.class;

   public OutputStreamImageOutputStreamSpi() {
      super("Oracle Corporation", "1.0", outputClass);
   }

   public String getDescription(Locale var1) {
      return "Service provider that instantiates an OutputStreamImageOutputStream from an OutputStream";
   }

   public boolean canUseCacheFile() {
      return true;
   }

   public boolean needsCacheFile() {
      return false;
   }

   public ImageOutputStream createOutputStreamInstance(Object var1, boolean var2, File var3) throws IOException {
      if (var1 instanceof OutputStream) {
         OutputStream var4 = (OutputStream)var1;
         return (ImageOutputStream)(var2 ? new FileCacheImageOutputStream(var4, var3) : new MemoryCacheImageOutputStream(var4));
      } else {
         throw new IllegalArgumentException();
      }
   }
}
