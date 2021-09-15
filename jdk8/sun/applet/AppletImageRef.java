package sun.applet;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.net.URL;
import sun.awt.image.URLImageSource;
import sun.misc.Ref;

class AppletImageRef extends Ref {
   URL url;

   AppletImageRef(URL var1) {
      this.url = var1;
   }

   public void flush() {
      super.flush();
   }

   public Object reconstitute() {
      Image var1 = Toolkit.getDefaultToolkit().createImage((ImageProducer)(new URLImageSource(this.url)));
      return var1;
   }
}
