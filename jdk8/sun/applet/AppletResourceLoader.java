package sun.applet;

import java.awt.Image;
import java.net.URL;
import sun.misc.Ref;

public class AppletResourceLoader {
   public static Image getImage(URL var0) {
      return AppletViewer.getCachedImage(var0);
   }

   public static Ref getImageRef(URL var0) {
      return AppletViewer.getCachedImageRef(var0);
   }

   public static void flushImages() {
      AppletViewer.flushImageCache();
   }
}
