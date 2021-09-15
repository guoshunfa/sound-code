package sun.awt.image;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

public class ByteArrayImageSource extends InputStreamImageSource {
   byte[] imagedata;
   int imageoffset;
   int imagelength;

   public ByteArrayImageSource(byte[] var1) {
      this(var1, 0, var1.length);
   }

   public ByteArrayImageSource(byte[] var1, int var2, int var3) {
      this.imagedata = var1;
      this.imageoffset = var2;
      this.imagelength = var3;
   }

   final boolean checkSecurity(Object var1, boolean var2) {
      return true;
   }

   protected ImageDecoder getDecoder() {
      BufferedInputStream var1 = new BufferedInputStream(new ByteArrayInputStream(this.imagedata, this.imageoffset, this.imagelength));
      return this.getDecoder(var1);
   }
}
