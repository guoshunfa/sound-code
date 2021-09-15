package sun.net.www.protocol.http;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;
import sun.net.NetProperties;
import sun.net.www.HeaderParser;

class DigestAuthentication extends AuthenticationInfo {
   private static final long serialVersionUID = 100L;
   private String authMethod;
   private static final String compatPropName = "http.auth.digest.quoteParameters";
   private static final boolean delimCompatFlag;
   DigestAuthentication.Parameters params;
   private static final char[] charArray;
   private static final String[] zeroPad;

   public DigestAuthentication(boolean var1, URL var2, String var3, String var4, PasswordAuthentication var5, DigestAuthentication.Parameters var6) {
      super((char)(var1 ? 'p' : 's'), AuthScheme.DIGEST, var2, var3);
      this.authMethod = var4;
      this.pw = var5;
      this.params = var6;
   }

   public DigestAuthentication(boolean var1, String var2, int var3, String var4, String var5, PasswordAuthentication var6, DigestAuthentication.Parameters var7) {
      super((char)(var1 ? 'p' : 's'), AuthScheme.DIGEST, var2, var3, var4);
      this.authMethod = var5;
      this.pw = var6;
      this.params = var7;
   }

   public boolean supportsPreemptiveAuthorization() {
      return true;
   }

   public String getHeaderValue(URL var1, String var2) {
      return this.getHeaderValueImpl(var1.getFile(), var2);
   }

   String getHeaderValue(String var1, String var2) {
      return this.getHeaderValueImpl(var1, var2);
   }

   public boolean isAuthorizationStale(String var1) {
      HeaderParser var2 = new HeaderParser(var1);
      String var3 = var2.findValue("stale");
      if (var3 != null && var3.equals("true")) {
         String var4 = var2.findValue("nonce");
         if (var4 != null && !"".equals(var4)) {
            this.params.setNonce(var4);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean setHeaders(HttpURLConnection var1, HeaderParser var2, String var3) {
      this.params.setNonce(var2.findValue("nonce"));
      this.params.setOpaque(var2.findValue("opaque"));
      this.params.setQop(var2.findValue("qop"));
      String var4 = "";
      String var5;
      if (this.type == 'p' && var1.tunnelState() == HttpURLConnection.TunnelState.SETUP) {
         var4 = HttpURLConnection.connectRequestURI(var1.getURL());
         var5 = HttpURLConnection.HTTP_CONNECT;
      } else {
         try {
            var4 = var1.getRequestURI();
         } catch (IOException var8) {
         }

         var5 = var1.getMethod();
      }

      if (this.params.nonce != null && this.authMethod != null && this.pw != null && this.realm != null) {
         if (this.authMethod.length() >= 1) {
            this.authMethod = Character.toUpperCase(this.authMethod.charAt(0)) + this.authMethod.substring(1).toLowerCase();
         }

         String var6 = var2.findValue("algorithm");
         if (var6 == null || "".equals(var6)) {
            var6 = "MD5";
         }

         this.params.setAlgorithm(var6);
         if (this.params.authQop()) {
            this.params.setNewCnonce();
         }

         String var7 = this.getHeaderValueImpl(var4, var5);
         if (var7 != null) {
            var1.setAuthenticationProperty(this.getHeaderName(), var7);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private String getHeaderValueImpl(String var1, String var2) {
      char[] var4 = this.pw.getPassword();
      boolean var5 = this.params.authQop();
      String var6 = this.params.getOpaque();
      String var7 = this.params.getCnonce();
      String var8 = this.params.getNonce();
      String var9 = this.params.getAlgorithm();
      this.params.incrementNC();
      int var10 = this.params.getNCCount();
      String var11 = null;
      if (var10 != -1) {
         var11 = Integer.toHexString(var10).toLowerCase();
         int var12 = var11.length();
         if (var12 < 8) {
            var11 = zeroPad[var12] + var11;
         }
      }

      String var3;
      try {
         var3 = this.computeDigest(true, this.pw.getUserName(), var4, this.realm, var2, var1, var8, var7, var11);
      } catch (NoSuchAlgorithmException var16) {
         return null;
      }

      String var17 = "\"";
      if (var5) {
         var17 = "\", nc=" + var11;
      }

      String var13;
      String var14;
      if (delimCompatFlag) {
         var13 = ", algorithm=\"" + var9 + "\"";
         var14 = ", qop=\"auth\"";
      } else {
         var13 = ", algorithm=" + var9;
         var14 = ", qop=auth";
      }

      String var15 = this.authMethod + " username=\"" + this.pw.getUserName() + "\", realm=\"" + this.realm + "\", nonce=\"" + var8 + var17 + ", uri=\"" + var1 + "\", response=\"" + var3 + "\"" + var13;
      if (var6 != null) {
         var15 = var15 + ", opaque=\"" + var6 + "\"";
      }

      if (var7 != null) {
         var15 = var15 + ", cnonce=\"" + var7 + "\"";
      }

      if (var5) {
         var15 = var15 + var14;
      }

      return var15;
   }

   public void checkResponse(String var1, String var2, URL var3) throws IOException {
      this.checkResponse(var1, var2, var3.getFile());
   }

   public void checkResponse(String var1, String var2, String var3) throws IOException {
      char[] var4 = this.pw.getPassword();
      String var5 = this.pw.getUserName();
      boolean var6 = this.params.authQop();
      String var7 = this.params.getOpaque();
      String var8 = this.params.cnonce;
      String var9 = this.params.getNonce();
      String var10 = this.params.getAlgorithm();
      int var11 = this.params.getNCCount();
      String var12 = null;
      if (var1 == null) {
         throw new ProtocolException("No authentication information in response");
      } else {
         if (var11 != -1) {
            var12 = Integer.toHexString(var11).toUpperCase();
            int var13 = var12.length();
            if (var13 < 8) {
               var12 = zeroPad[var13] + var12;
            }
         }

         try {
            String var18 = this.computeDigest(false, var5, var4, this.realm, var2, var3, var9, var8, var12);
            HeaderParser var14 = new HeaderParser(var1);
            String var15 = var14.findValue("rspauth");
            if (var15 == null) {
               throw new ProtocolException("No digest in response");
            } else if (!var15.equals(var18)) {
               throw new ProtocolException("Response digest invalid");
            } else {
               String var16 = var14.findValue("nextnonce");
               if (var16 != null && !"".equals(var16)) {
                  this.params.setNonce(var16);
               }

            }
         } catch (NoSuchAlgorithmException var17) {
            throw new ProtocolException("Unsupported algorithm in response");
         }
      }
   }

   private String computeDigest(boolean var1, String var2, char[] var3, String var4, String var5, String var6, String var7, String var8, String var9) throws NoSuchAlgorithmException {
      String var12 = this.params.getAlgorithm();
      boolean var13 = var12.equalsIgnoreCase("MD5-sess");
      MessageDigest var14 = MessageDigest.getInstance(var13 ? "MD5" : var12);
      String var10;
      String var11;
      String var15;
      String var16;
      if (var13) {
         if ((var11 = this.params.getCachedHA1()) == null) {
            var15 = var2 + ":" + var4 + ":";
            var16 = this.encode(var15, var3, var14);
            var10 = var16 + ":" + var7 + ":" + var8;
            var11 = this.encode(var10, (char[])null, var14);
            this.params.setCachedHA1(var11);
         }
      } else {
         var10 = var2 + ":" + var4 + ":";
         var11 = this.encode(var10, var3, var14);
      }

      if (var1) {
         var15 = var5 + ":" + var6;
      } else {
         var15 = ":" + var6;
      }

      var16 = this.encode(var15, (char[])null, var14);
      String var17;
      if (this.params.authQop()) {
         var17 = var11 + ":" + var7 + ":" + var9 + ":" + var8 + ":auth:" + var16;
      } else {
         var17 = var11 + ":" + var7 + ":" + var16;
      }

      String var18 = this.encode(var17, (char[])null, var14);
      return var18;
   }

   private String encode(String var1, char[] var2, MessageDigest var3) {
      try {
         var3.update(var1.getBytes("ISO-8859-1"));
      } catch (UnsupportedEncodingException var8) {
         assert false;
      }

      byte[] var4;
      if (var2 != null) {
         var4 = new byte[var2.length];

         for(int var5 = 0; var5 < var2.length; ++var5) {
            var4[var5] = (byte)var2[var5];
         }

         var3.update(var4);
         Arrays.fill((byte[])var4, (byte)0);
      }

      var4 = var3.digest();
      StringBuffer var9 = new StringBuffer(var4.length * 2);

      for(int var6 = 0; var6 < var4.length; ++var6) {
         int var7 = var4[var6] >>> 4 & 15;
         var9.append(charArray[var7]);
         var7 = var4[var6] & 15;
         var9.append(charArray[var7]);
      }

      return var9.toString();
   }

   static {
      Boolean var0 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            return NetProperties.getBoolean("http.auth.digest.quoteParameters");
         }
      });
      delimCompatFlag = var0 == null ? false : var0;
      charArray = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
      zeroPad = new String[]{"00000000", "0000000", "000000", "00000", "0000", "000", "00", "0"};
   }

   static class Parameters implements Serializable {
      private static final long serialVersionUID = -3584543755194526252L;
      private boolean serverQop = false;
      private String opaque = null;
      private String cnonce;
      private String nonce = null;
      private String algorithm = null;
      private int NCcount = 0;
      private String cachedHA1 = null;
      private boolean redoCachedHA1 = true;
      private static final int cnonceRepeat = 5;
      private static final int cnoncelen = 40;
      private static Random random = new Random();
      int cnonce_count = 0;

      Parameters() {
         this.setNewCnonce();
      }

      boolean authQop() {
         return this.serverQop;
      }

      synchronized void incrementNC() {
         ++this.NCcount;
      }

      synchronized int getNCCount() {
         return this.NCcount;
      }

      synchronized String getCnonce() {
         if (this.cnonce_count >= 5) {
            this.setNewCnonce();
         }

         ++this.cnonce_count;
         return this.cnonce;
      }

      synchronized void setNewCnonce() {
         byte[] var1 = new byte[20];
         char[] var2 = new char[40];
         random.nextBytes(var1);

         for(int var3 = 0; var3 < 20; ++var3) {
            int var4 = var1[var3] + 128;
            var2[var3 * 2] = (char)(65 + var4 / 16);
            var2[var3 * 2 + 1] = (char)(65 + var4 % 16);
         }

         this.cnonce = new String(var2, 0, 40);
         this.cnonce_count = 0;
         this.redoCachedHA1 = true;
      }

      synchronized void setQop(String var1) {
         if (var1 != null) {
            StringTokenizer var2 = new StringTokenizer(var1, " ");

            while(var2.hasMoreTokens()) {
               if (var2.nextToken().equalsIgnoreCase("auth")) {
                  this.serverQop = true;
                  return;
               }
            }
         }

         this.serverQop = false;
      }

      synchronized String getOpaque() {
         return this.opaque;
      }

      synchronized void setOpaque(String var1) {
         this.opaque = var1;
      }

      synchronized String getNonce() {
         return this.nonce;
      }

      synchronized void setNonce(String var1) {
         if (!var1.equals(this.nonce)) {
            this.nonce = var1;
            this.NCcount = 0;
            this.redoCachedHA1 = true;
         }

      }

      synchronized String getCachedHA1() {
         return this.redoCachedHA1 ? null : this.cachedHA1;
      }

      synchronized void setCachedHA1(String var1) {
         this.cachedHA1 = var1;
         this.redoCachedHA1 = false;
      }

      synchronized String getAlgorithm() {
         return this.algorithm;
      }

      synchronized void setAlgorithm(String var1) {
         this.algorithm = var1;
      }
   }
}
