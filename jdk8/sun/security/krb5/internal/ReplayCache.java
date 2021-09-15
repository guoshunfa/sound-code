package sun.security.krb5.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.internal.rcache.AuthTimeWithHash;
import sun.security.krb5.internal.rcache.DflCache;
import sun.security.krb5.internal.rcache.MemoryCache;

public abstract class ReplayCache {
   public static ReplayCache getInstance(String var0) {
      if (var0 == null) {
         return new MemoryCache();
      } else if (!var0.equals("dfl") && !var0.startsWith("dfl:")) {
         if (var0.equals("none")) {
            return new ReplayCache() {
               public void checkAndStore(KerberosTime var1, AuthTimeWithHash var2) throws KrbApErrException {
               }
            };
         } else {
            throw new IllegalArgumentException("Unknown type: " + var0);
         }
      } else {
         return new DflCache(var0);
      }
   }

   public static ReplayCache getInstance() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.security.krb5.rcache")));
      return getInstance(var0);
   }

   public abstract void checkAndStore(KerberosTime var1, AuthTimeWithHash var2) throws KrbApErrException;
}
