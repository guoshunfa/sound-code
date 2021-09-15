package sun.security.provider;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.util.LinkedHashMap;
import sun.security.action.PutAllAction;
import sun.security.rsa.SunRsaSignEntries;

public final class VerificationProvider extends Provider {
   private static final long serialVersionUID = 7482667077568930381L;
   private static final boolean ACTIVE;

   public VerificationProvider() {
      super("SunJarVerification", 1.8D, "Jar Verification Provider");
      if (ACTIVE) {
         if (System.getSecurityManager() == null) {
            SunEntries.putEntries(this);
            SunRsaSignEntries.putEntries(this);
         } else {
            LinkedHashMap var1 = new LinkedHashMap();
            SunEntries.putEntries(var1);
            SunRsaSignEntries.putEntries(var1);
            AccessController.doPrivileged((PrivilegedAction)(new PutAllAction(this, var1)));
         }

      }
   }

   static {
      boolean var0;
      try {
         Class.forName("sun.security.provider.Sun");
         Class.forName("sun.security.rsa.SunRsaSign");
         var0 = false;
      } catch (ClassNotFoundException var2) {
         var0 = true;
      }

      ACTIVE = var0;
   }
}
