package sun.security.krb5.internal.ccache;

import java.io.IOException;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.LoginOptions;

public abstract class CredentialsCache {
   static CredentialsCache singleton = null;
   static String cacheName;
   private static boolean DEBUG;

   public static CredentialsCache getInstance(PrincipalName var0) {
      return FileCredentialsCache.acquireInstance(var0, (String)null);
   }

   public static CredentialsCache getInstance(String var0) {
      return var0.length() >= 5 && var0.substring(0, 5).equalsIgnoreCase("FILE:") ? FileCredentialsCache.acquireInstance((PrincipalName)null, var0.substring(5)) : FileCredentialsCache.acquireInstance((PrincipalName)null, var0);
   }

   public static CredentialsCache getInstance(PrincipalName var0, String var1) {
      return var1 != null && var1.length() >= 5 && var1.regionMatches(true, 0, "FILE:", 0, 5) ? FileCredentialsCache.acquireInstance(var0, var1.substring(5)) : FileCredentialsCache.acquireInstance(var0, var1);
   }

   public static CredentialsCache getInstance() {
      return FileCredentialsCache.acquireInstance();
   }

   public static CredentialsCache create(PrincipalName var0, String var1) {
      if (var1 == null) {
         throw new RuntimeException("cache name error");
      } else if (var1.length() >= 5 && var1.regionMatches(true, 0, "FILE:", 0, 5)) {
         var1 = var1.substring(5);
         return FileCredentialsCache.New(var0, var1);
      } else {
         return FileCredentialsCache.New(var0, var1);
      }
   }

   public static CredentialsCache create(PrincipalName var0) {
      return FileCredentialsCache.New(var0);
   }

   public static String cacheName() {
      return cacheName;
   }

   public abstract PrincipalName getPrimaryPrincipal();

   public abstract void update(Credentials var1);

   public abstract void save() throws IOException, KrbException;

   public abstract Credentials[] getCredsList();

   public abstract Credentials getDefaultCreds();

   public abstract Credentials getCreds(PrincipalName var1);

   public abstract Credentials getCreds(LoginOptions var1, PrincipalName var2);

   static {
      DEBUG = Krb5.DEBUG;
   }
}
