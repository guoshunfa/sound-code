package com.sun.imageio.stream;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

public class StreamFinalizer {
   private ImageInputStream stream;

   public StreamFinalizer(ImageInputStream var1) {
      this.stream = var1;
   }

   protected void finalize() throws Throwable {
      try {
         this.stream.close();
      } catch (IOException var5) {
      } finally {
         this.stream = null;
         super.finalize();
      }

   }
}
