package sun.security.krb5;

import javax.security.auth.kerberos.KeyTab;
import sun.misc.Unsafe;

public class KerberosSecrets {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static JavaxSecurityAuthKerberosAccess javaxSecurityAuthKerberosAccess;

   public static void setJavaxSecurityAuthKerberosAccess(JavaxSecurityAuthKerberosAccess var0) {
      javaxSecurityAuthKerberosAccess = var0;
   }

   public static JavaxSecurityAuthKerberosAccess getJavaxSecurityAuthKerberosAccess() {
      if (javaxSecurityAuthKerberosAccess == null) {
         unsafe.ensureClassInitialized(KeyTab.class);
      }

      return javaxSecurityAuthKerberosAccess;
   }
}
