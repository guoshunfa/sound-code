package sun.net.www.content.image;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.net.ContentHandler;
import java.net.URLConnection;
import sun.awt.image.URLImageSource;

public class jpeg extends ContentHandler {
   public Object getContent(URLConnection var1) throws IOException {
      return new URLImageSource(var1);
   }

   public Object getContent(URLConnection var1, Class[] var2) throws IOException {
      Class[] var3 = var2;

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var3[var4].isAssignableFrom(URLImageSource.class)) {
            return new URLImageSource(var1);
         }

         if (var3[var4].isAssignableFrom(Image.class)) {
            Toolkit var5 = Toolkit.getDefaultToolkit();
            return var5.createImage((ImageProducer)(new URLImageSource(var1)));
         }
      }

      return null;
   }
}
