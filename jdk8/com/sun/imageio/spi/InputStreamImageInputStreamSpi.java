package com.sun.imageio.spi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.FileCacheImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

public class InputStreamImageInputStreamSpi extends ImageInputStreamSpi {
   private static final String vendorName = "Oracle Corporation";
   private static final String version = "1.0";
   private static final Class inputClass = InputStream.class;

   public InputStreamImageInputStreamSpi() {
      super("Oracle Corporation", "1.0", inputClass);
   }

   public String getDescription(Locale var1) {
      return "Service provider that instantiates a FileCacheImageInputStream or MemoryCacheImageInputStream from an InputStream";
   }

   public boolean canUseCacheFile() {
      return true;
   }

   public boolean needsCacheFile() {
      return false;
   }

   public ImageInputStream createInputStreamInstance(Object var1, boolean var2, File var3) throws IOException {
      if (var1 instanceof InputStream) {
         InputStream var4 = (InputStream)var1;
         return (ImageInputStream)(var2 ? new FileCacheImageInputStream(var4, var3) : new MemoryCacheImageInputStream(var4));
      } else {
         throw new IllegalArgumentException();
      }
   }
}
