package sun.net.www.protocol.http.ntlm;

import java.net.URL;

public abstract class NTLMAuthenticationCallback {
   private static volatile NTLMAuthenticationCallback callback = new NTLMAuthenticationCallback.DefaultNTLMAuthenticationCallback();

   public static void setNTLMAuthenticationCallback(NTLMAuthenticationCallback var0) {
      callback = var0;
   }

   public static NTLMAuthenticationCallback getNTLMAuthenticationCallback() {
      return callback;
   }

   public abstract boolean isTrustedSite(URL var1);

   static class DefaultNTLMAuthenticationCallback extends NTLMAuthenticationCallback {
      public boolean isTrustedSite(URL var1) {
         return true;
      }
   }
}
