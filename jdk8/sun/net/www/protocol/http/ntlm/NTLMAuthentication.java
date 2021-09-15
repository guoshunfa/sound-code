package sun.net.www.protocol.http.ntlm;

import com.sun.security.ntlm.Client;
import com.sun.security.ntlm.NTLMException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.util.Base64;
import java.util.Random;
import sun.net.www.HeaderParser;
import sun.net.www.protocol.http.AuthScheme;
import sun.net.www.protocol.http.AuthenticationInfo;
import sun.net.www.protocol.http.HttpURLConnection;
import sun.security.action.GetPropertyAction;

public class NTLMAuthentication extends AuthenticationInfo {
   private static final long serialVersionUID = 170L;
   private static final NTLMAuthenticationCallback NTLMAuthCallback = NTLMAuthenticationCallback.getNTLMAuthenticationCallback();
   private String hostname;
   private static String defaultDomain = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("http.auth.ntlm.domain", "")));
   private static final boolean ntlmCache;
   PasswordAuthentication pw;
   Client client;

   public static boolean supportsTransparentAuth() {
      return false;
   }

   public static boolean isTrustedSite(URL var0) {
      return NTLMAuthCallback.isTrustedSite(var0);
   }

   private void init0() {
      this.hostname = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            String var1;
            try {
               var1 = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException var3) {
               var1 = "localhost";
            }

            return var1;
         }
      });
   }

   public NTLMAuthentication(boolean var1, URL var2, PasswordAuthentication var3) {
      super((char)(var1 ? 'p' : 's'), AuthScheme.NTLM, var2, "");
      this.init(var3);
   }

   private void init(PasswordAuthentication var1) {
      this.pw = var1;
      String var5 = var1.getUserName();
      int var6 = var5.indexOf(92);
      String var2;
      String var3;
      if (var6 == -1) {
         var2 = var5;
         var3 = defaultDomain;
      } else {
         var3 = var5.substring(0, var6).toUpperCase();
         var2 = var5.substring(var6 + 1);
      }

      char[] var4 = var1.getPassword();
      this.init0();

      try {
         this.client = new Client(System.getProperty("ntlm.version"), this.hostname, var2, var3, var4);
      } catch (NTLMException var10) {
         try {
            this.client = new Client((String)null, this.hostname, var2, var3, var4);
         } catch (NTLMException var9) {
            throw new AssertionError("Really?");
         }
      }

   }

   public NTLMAuthentication(boolean var1, String var2, int var3, PasswordAuthentication var4) {
      super((char)(var1 ? 'p' : 's'), AuthScheme.NTLM, var2, var3, "");
      this.init(var4);
   }

   protected boolean useAuthCache() {
      return ntlmCache && super.useAuthCache();
   }

   public boolean supportsPreemptiveAuthorization() {
      return false;
   }

   public String getHeaderValue(URL var1, String var2) {
      throw new RuntimeException("getHeaderValue not supported");
   }

   public boolean isAuthorizationStale(String var1) {
      return false;
   }

   public synchronized boolean setHeaders(HttpURLConnection var1, HeaderParser var2, String var3) {
      try {
         String var4;
         if (var3.length() < 6) {
            var4 = this.buildType1Msg();
         } else {
            String var5 = var3.substring(5);
            var4 = this.buildType3Msg(var5);
         }

         var1.setAuthenticationProperty(this.getHeaderName(), var4);
         return true;
      } catch (IOException var6) {
         return false;
      } catch (GeneralSecurityException var7) {
         return false;
      }
   }

   private String buildType1Msg() {
      byte[] var1 = this.client.type1();
      String var2 = "NTLM " + Base64.getEncoder().encodeToString(var1);
      return var2;
   }

   private String buildType3Msg(String var1) throws GeneralSecurityException, IOException {
      byte[] var2 = Base64.getDecoder().decode(var1);
      byte[] var3 = new byte[8];
      (new Random()).nextBytes(var3);
      byte[] var4 = this.client.type3(var2, var3);
      String var5 = "NTLM " + Base64.getEncoder().encodeToString(var4);
      return var5;
   }

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("jdk.ntlm.cache", "true")));
      ntlmCache = Boolean.parseBoolean(var0);
   }
}
