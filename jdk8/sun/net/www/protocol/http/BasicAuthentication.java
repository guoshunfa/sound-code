package sun.net.www.protocol.http;

import java.io.UnsupportedEncodingException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import sun.net.www.HeaderParser;

class BasicAuthentication extends AuthenticationInfo {
   private static final long serialVersionUID = 100L;
   String auth;

   public BasicAuthentication(boolean var1, String var2, int var3, String var4, PasswordAuthentication var5) {
      super((char)(var1 ? 'p' : 's'), AuthScheme.BASIC, var2, var3, var4);
      String var6 = var5.getUserName() + ":";
      byte[] var7 = null;

      try {
         var7 = var6.getBytes("ISO-8859-1");
      } catch (UnsupportedEncodingException var11) {
         assert false;
      }

      char[] var8 = var5.getPassword();
      byte[] var9 = new byte[var8.length];

      for(int var10 = 0; var10 < var8.length; ++var10) {
         var9[var10] = (byte)var8[var10];
      }

      byte[] var12 = new byte[var7.length + var9.length];
      System.arraycopy(var7, 0, var12, 0, var7.length);
      System.arraycopy(var9, 0, var12, var7.length, var9.length);
      this.auth = "Basic " + Base64.getEncoder().encodeToString(var12);
      this.pw = var5;
   }

   public BasicAuthentication(boolean var1, String var2, int var3, String var4, String var5) {
      super((char)(var1 ? 'p' : 's'), AuthScheme.BASIC, var2, var3, var4);
      this.auth = "Basic " + var5;
   }

   public BasicAuthentication(boolean var1, URL var2, String var3, PasswordAuthentication var4) {
      super((char)(var1 ? 'p' : 's'), AuthScheme.BASIC, var2, var3);
      String var5 = var4.getUserName() + ":";
      byte[] var6 = null;

      try {
         var6 = var5.getBytes("ISO-8859-1");
      } catch (UnsupportedEncodingException var10) {
         assert false;
      }

      char[] var7 = var4.getPassword();
      byte[] var8 = new byte[var7.length];

      for(int var9 = 0; var9 < var7.length; ++var9) {
         var8[var9] = (byte)var7[var9];
      }

      byte[] var11 = new byte[var6.length + var8.length];
      System.arraycopy(var6, 0, var11, 0, var6.length);
      System.arraycopy(var8, 0, var11, var6.length, var8.length);
      this.auth = "Basic " + Base64.getEncoder().encodeToString(var11);
      this.pw = var4;
   }

   public BasicAuthentication(boolean var1, URL var2, String var3, String var4) {
      super((char)(var1 ? 'p' : 's'), AuthScheme.BASIC, var2, var3);
      this.auth = "Basic " + var4;
   }

   public boolean supportsPreemptiveAuthorization() {
      return true;
   }

   public boolean setHeaders(HttpURLConnection var1, HeaderParser var2, String var3) {
      var1.setAuthenticationProperty(this.getHeaderName(), this.getHeaderValue((URL)null, (String)null));
      return true;
   }

   public String getHeaderValue(URL var1, String var2) {
      return this.auth;
   }

   public boolean isAuthorizationStale(String var1) {
      return false;
   }

   static String getRootPath(String var0, String var1) {
      int var2 = 0;

      try {
         var0 = (new URI(var0)).normalize().getPath();
         var1 = (new URI(var1)).normalize().getPath();
      } catch (URISyntaxException var5) {
      }

      while(var2 < var1.length()) {
         int var3 = var1.indexOf(47, var2 + 1);
         if (var3 == -1 || !var1.regionMatches(0, var0, 0, var3 + 1)) {
            return var1.substring(0, var2 + 1);
         }

         var2 = var3;
      }

      return var0;
   }
}
