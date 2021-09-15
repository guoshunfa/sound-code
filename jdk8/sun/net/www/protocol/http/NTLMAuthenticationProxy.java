package sun.net.www.protocol.http;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.PasswordAuthentication;
import java.net.URL;
import sun.util.logging.PlatformLogger;

class NTLMAuthenticationProxy {
   private static Method supportsTA;
   private static Method isTrustedSite;
   private static final String clazzStr = "sun.net.www.protocol.http.ntlm.NTLMAuthentication";
   private static final String supportsTAStr = "supportsTransparentAuth";
   private static final String isTrustedSiteStr = "isTrustedSite";
   static final NTLMAuthenticationProxy proxy = tryLoadNTLMAuthentication();
   static final boolean supported;
   static final boolean supportsTransparentAuth;
   private final Constructor<? extends AuthenticationInfo> threeArgCtr;
   private final Constructor<? extends AuthenticationInfo> fiveArgCtr;

   private NTLMAuthenticationProxy(Constructor<? extends AuthenticationInfo> var1, Constructor<? extends AuthenticationInfo> var2) {
      this.threeArgCtr = var1;
      this.fiveArgCtr = var2;
   }

   AuthenticationInfo create(boolean var1, URL var2, PasswordAuthentication var3) {
      try {
         return (AuthenticationInfo)this.threeArgCtr.newInstance(var1, var2, var3);
      } catch (ReflectiveOperationException var5) {
         finest(var5);
         return null;
      }
   }

   AuthenticationInfo create(boolean var1, String var2, int var3, PasswordAuthentication var4) {
      try {
         return (AuthenticationInfo)this.fiveArgCtr.newInstance(var1, var2, var3, var4);
      } catch (ReflectiveOperationException var6) {
         finest(var6);
         return null;
      }
   }

   private static boolean supportsTransparentAuth() {
      try {
         return (Boolean)supportsTA.invoke((Object)null);
      } catch (ReflectiveOperationException var1) {
         finest(var1);
         return false;
      }
   }

   public static boolean isTrustedSite(URL var0) {
      try {
         return (Boolean)isTrustedSite.invoke((Object)null, var0);
      } catch (ReflectiveOperationException var2) {
         finest(var2);
         return false;
      }
   }

   private static NTLMAuthenticationProxy tryLoadNTLMAuthentication() {
      try {
         Class var0 = Class.forName("sun.net.www.protocol.http.ntlm.NTLMAuthentication", true, (ClassLoader)null);
         if (var0 != null) {
            Constructor var1 = var0.getConstructor(Boolean.TYPE, URL.class, PasswordAuthentication.class);
            Constructor var2 = var0.getConstructor(Boolean.TYPE, String.class, Integer.TYPE, PasswordAuthentication.class);
            supportsTA = var0.getDeclaredMethod("supportsTransparentAuth");
            isTrustedSite = var0.getDeclaredMethod("isTrustedSite", URL.class);
            return new NTLMAuthenticationProxy(var1, var2);
         }
      } catch (ClassNotFoundException var4) {
         finest(var4);
      } catch (ReflectiveOperationException var5) {
         throw new AssertionError(var5);
      }

      return null;
   }

   static void finest(Exception var0) {
      PlatformLogger var1 = HttpURLConnection.getHttpLogger();
      if (var1.isLoggable(PlatformLogger.Level.FINEST)) {
         var1.finest("NTLMAuthenticationProxy: " + var0);
      }

   }

   static {
      supported = proxy != null;
      supportsTransparentAuth = supported ? supportsTransparentAuth() : false;
   }
}
