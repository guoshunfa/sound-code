package com.sun.imageio.plugins.common;

import java.io.IOException;
import java.io.InputStream;
import javax.imageio.stream.ImageInputStream;

public class InputStreamAdapter extends InputStream {
   ImageInputStream stream;

   public InputStreamAdapter(ImageInputStream var1) {
      this.stream = var1;
   }

   public int read() throws IOException {
      return this.stream.read();
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      return this.stream.read(var1, var2, var3);
   }
}
