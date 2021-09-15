package com.sun.imageio.plugins.png;

import com.sun.imageio.plugins.common.InputStreamAdapter;
import com.sun.imageio.plugins.common.SubImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import javax.imageio.stream.ImageInputStream;

class PNGImageDataEnumeration implements Enumeration<InputStream> {
   boolean firstTime = true;
   ImageInputStream stream;
   int length;

   public PNGImageDataEnumeration(ImageInputStream var1) throws IOException {
      this.stream = var1;
      this.length = var1.readInt();
      int var2 = var1.readInt();
   }

   public InputStream nextElement() {
      try {
         this.firstTime = false;
         SubImageInputStream var1 = new SubImageInputStream(this.stream, this.length);
         return new InputStreamAdapter(var1);
      } catch (IOException var2) {
         return null;
      }
   }

   public boolean hasMoreElements() {
      if (this.firstTime) {
         return true;
      } else {
         try {
            int var1 = this.stream.readInt();
            this.length = this.stream.readInt();
            int var2 = this.stream.readInt();
            return var2 == 1229209940;
         } catch (IOException var3) {
            return false;
         }
      }
   }
}
