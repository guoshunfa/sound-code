package sun.security.jgss.wrapper;

import javax.security.auth.kerberos.ServicePermission;
import org.ietf.jgss.GSSException;

class Krb5Util {
   static String getTGSName(GSSNameElement var0) throws GSSException {
      String var1 = var0.getKrbName();
      int var2 = var1.indexOf("@");
      String var3 = var1.substring(var2 + 1);
      StringBuffer var4 = new StringBuffer("krbtgt/");
      var4.append(var3).append('@').append(var3);
      return var4.toString();
   }

   static void checkServicePermission(String var0, String var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         SunNativeProvider.debug("Checking ServicePermission(" + var0 + ", " + var1 + ")");
         ServicePermission var3 = new ServicePermission(var0, var1);
         var2.checkPermission(var3);
      }

   }
}
