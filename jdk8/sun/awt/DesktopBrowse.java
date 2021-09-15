package sun.awt;

import java.net.URL;

public abstract class DesktopBrowse {
   private static volatile DesktopBrowse mInstance;

   public static void setInstance(DesktopBrowse var0) {
      if (mInstance != null) {
         throw new IllegalStateException("DesktopBrowse instance has already been set.");
      } else {
         mInstance = var0;
      }
   }

   public static DesktopBrowse getInstance() {
      return mInstance;
   }

   public abstract void browse(URL var1);
}
