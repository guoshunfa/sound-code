package sun.text.normalizer;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.MissingResourceException;

public final class ICUData {
   private static InputStream getStream(final Class<ICUData> var0, final String var1, boolean var2) {
      InputStream var3 = null;
      if (System.getSecurityManager() != null) {
         var3 = (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
            public InputStream run() {
               return var0.getResourceAsStream(var1);
            }
         });
      } else {
         var3 = var0.getResourceAsStream(var1);
      }

      if (var3 == null && var2) {
         throw new MissingResourceException("could not locate data", var0.getPackage().getName(), var1);
      } else {
         return var3;
      }
   }

   public static InputStream getStream(String var0) {
      return getStream(ICUData.class, var0, false);
   }

   public static InputStream getRequiredStream(String var0) {
      return getStream(ICUData.class, var0, true);
   }
}
