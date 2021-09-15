package com.sun.imageio.plugins.gif;

import java.util.Locale;
import javax.imageio.ImageWriteParam;

class GIFImageWriteParam extends ImageWriteParam {
   GIFImageWriteParam(Locale var1) {
      super(var1);
      this.canWriteCompressed = true;
      this.canWriteProgressive = true;
      this.compressionTypes = new String[]{"LZW", "lzw"};
      this.compressionType = this.compressionTypes[0];
   }

   public void setCompressionMode(int var1) {
      if (var1 == 0) {
         throw new UnsupportedOperationException("MODE_DISABLED is not supported.");
      } else {
         super.setCompressionMode(var1);
      }
   }
}
