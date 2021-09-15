package sun.security.provider.certpath;

import java.io.IOException;
import java.net.URI;
import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Cache;

public abstract class CertStoreHelper {
   private static final int NUM_TYPES = 2;
   private static final Map<String, String> classMap = new HashMap(2);
   private static Cache<String, CertStoreHelper> cache;

   public static CertStoreHelper getInstance(final String var0) throws NoSuchAlgorithmException {
      CertStoreHelper var1 = (CertStoreHelper)cache.get(var0);
      if (var1 != null) {
         return var1;
      } else {
         final String var2 = (String)classMap.get(var0);
         if (var2 == null) {
            throw new NoSuchAlgorithmException(var0 + " not available");
         } else {
            try {
               var1 = (CertStoreHelper)AccessController.doPrivileged(new PrivilegedExceptionAction<CertStoreHelper>() {
                  public CertStoreHelper run() throws ClassNotFoundException {
                     try {
                        Class var1 = Class.forName(var2, true, (ClassLoader)null);
                        CertStoreHelper var2x = (CertStoreHelper)var1.newInstance();
                        CertStoreHelper.cache.put(var0, var2x);
                        return var2x;
                     } catch (IllegalAccessException | InstantiationException var3) {
                        throw new AssertionError(var3);
                     }
                  }
               });
               return var1;
            } catch (PrivilegedActionException var4) {
               throw new NoSuchAlgorithmException(var0 + " not available", var4.getException());
            }
         }
      }
   }

   static boolean isCausedByNetworkIssue(String var0, CertStoreException var1) {
      byte var3 = -1;
      switch(var0.hashCode()) {
      case 84300:
         if (var0.equals("URI")) {
            var3 = 2;
         }
         break;
      case 2331559:
         if (var0.equals("LDAP")) {
            var3 = 0;
         }
         break;
      case 133315663:
         if (var0.equals("SSLServer")) {
            var3 = 1;
         }
      }

      switch(var3) {
      case 0:
      case 1:
         try {
            CertStoreHelper var6 = getInstance(var0);
            return var6.isCausedByNetworkIssue(var1);
         } catch (NoSuchAlgorithmException var5) {
            return false;
         }
      case 2:
         Throwable var4 = var1.getCause();
         return var4 != null && var4 instanceof IOException;
      default:
         return false;
      }
   }

   public abstract CertStore getCertStore(URI var1) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;

   public abstract X509CertSelector wrap(X509CertSelector var1, X500Principal var2, String var3) throws IOException;

   public abstract X509CRLSelector wrap(X509CRLSelector var1, Collection<X500Principal> var2, String var3) throws IOException;

   public abstract boolean isCausedByNetworkIssue(CertStoreException var1);

   static {
      classMap.put("LDAP", "sun.security.provider.certpath.ldap.LDAPCertStoreHelper");
      classMap.put("SSLServer", "sun.security.provider.certpath.ssl.SSLServerCertStoreHelper");
      cache = Cache.newSoftMemoryCache(2);
   }
}
