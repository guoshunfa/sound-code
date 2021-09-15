package com.sun.imageio.plugins.png;

import java.util.Locale;
import javax.imageio.ImageWriteParam;

class PNGImageWriteParam extends ImageWriteParam {
   public PNGImageWriteParam(Locale var1) {
      this.canWriteProgressive = true;
      this.locale = var1;
   }
}
