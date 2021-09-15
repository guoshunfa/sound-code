package com.sun.security.sasl.digest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.RealmChoiceCallback;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

final class DigestMD5Client extends DigestMD5Base implements SaslClient {
   private static final String MY_CLASS_NAME = DigestMD5Client.class.getName();
   private static final String CIPHER_PROPERTY = "com.sun.security.sasl.digest.cipher";
   private static final String[] DIRECTIVE_KEY = new String[]{"realm", "qop", "algorithm", "nonce", "maxbuf", "charset", "cipher", "rspauth", "stale"};
   private static final int REALM = 0;
   private static final int QOP = 1;
   private static final int ALGORITHM = 2;
   private static final int NONCE = 3;
   private static final int MAXBUF = 4;
   private static final int CHARSET = 5;
   private static final int CIPHER = 6;
   private static final int RESPONSE_AUTH = 7;
   private static final int STALE = 8;
   private int nonceCount;
   private String specifiedCipher;
   private byte[] cnonce;
   private String username;
   private char[] passwd;
   private byte[] authzidBytes;

   DigestMD5Client(String var1, String var2, String var3, Map<String, ?> var4, CallbackHandler var5) throws SaslException {
      super(var4, MY_CLASS_NAME, 2, var2 + "/" + var3, var5);
      if (var1 != null) {
         this.authzid = var1;

         try {
            this.authzidBytes = var1.getBytes("UTF8");
         } catch (UnsupportedEncodingException var7) {
            throw new SaslException("DIGEST-MD5: Error encoding authzid value into UTF-8", var7);
         }
      }

      if (var4 != null) {
         this.specifiedCipher = (String)var4.get("com.sun.security.sasl.digest.cipher");
         logger.log(Level.FINE, (String)"DIGEST60:Explicitly specified cipher: {0}", (Object)this.specifiedCipher);
      }

   }

   public boolean hasInitialResponse() {
      return false;
   }

   public byte[] evaluateChallenge(byte[] var1) throws SaslException {
      if (var1.length > 2048) {
         throw new SaslException("DIGEST-MD5: Invalid digest-challenge length. Got:  " + var1.length + " Expected < " + 2048);
      } else {
         byte[][] var2;
         switch(this.step) {
         case 2:
            ArrayList var3 = new ArrayList(3);
            var2 = parseDirectives(var1, DIRECTIVE_KEY, var3, 0);

            try {
               this.processChallenge(var2, var3);
               this.checkQopSupport(var2[1], var2[6]);
               ++this.step;
               return this.generateClientResponse(var2[5]);
            } catch (SaslException var9) {
               this.step = 0;
               this.clearPassword();
               throw var9;
            } catch (IOException var10) {
               this.step = 0;
               this.clearPassword();
               throw new SaslException("DIGEST-MD5: Error generating digest response-value", var10);
            }
         case 3:
            Object var4;
            try {
               var2 = parseDirectives(var1, DIRECTIVE_KEY, (List)null, 0);
               this.validateResponseValue(var2[7]);
               if (this.integrity && this.privacy) {
                  this.secCtx = new DigestMD5Base.DigestPrivacy(true);
               } else if (this.integrity) {
                  this.secCtx = new DigestMD5Base.DigestIntegrity(true);
               }

               var4 = null;
            } finally {
               this.clearPassword();
               this.step = 0;
               this.completed = true;
            }

            return (byte[])var4;
         default:
            throw new SaslException("DIGEST-MD5: Client at illegal state");
         }
      }
   }

   private void processChallenge(byte[][] var1, List<byte[]> var2) throws SaslException, UnsupportedEncodingException {
      if (var1[5] != null) {
         if (!"utf-8".equals(new String(var1[5], this.encoding))) {
            throw new SaslException("DIGEST-MD5: digest-challenge format violation. Unrecognised charset value: " + new String(var1[5]));
         }

         this.encoding = "UTF8";
         this.useUTF8 = true;
      }

      if (var1[2] == null) {
         throw new SaslException("DIGEST-MD5: Digest-challenge format violation: algorithm directive missing");
      } else if (!"md5-sess".equals(new String(var1[2], this.encoding))) {
         throw new SaslException("DIGEST-MD5: Digest-challenge format violation. Invalid value for 'algorithm' directive: " + var1[2]);
      } else if (var1[3] == null) {
         throw new SaslException("DIGEST-MD5: Digest-challenge format violation: nonce directive missing");
      } else {
         this.nonce = var1[3];

         try {
            String[] var3 = null;
            if (var1[0] != null) {
               if (var2 != null && var2.size() > 1) {
                  var3 = new String[var2.size()];

                  for(int var4 = 0; var4 < var3.length; ++var4) {
                     var3[var4] = new String((byte[])var2.get(var4), this.encoding);
                  }
               } else {
                  this.negotiatedRealm = new String(var1[0], this.encoding);
               }
            }

            NameCallback var12 = this.authzid == null ? new NameCallback("DIGEST-MD5 authentication ID: ") : new NameCallback("DIGEST-MD5 authentication ID: ", this.authzid);
            PasswordCallback var5 = new PasswordCallback("DIGEST-MD5 password: ", false);
            if (var3 == null) {
               RealmCallback var13 = this.negotiatedRealm == null ? new RealmCallback("DIGEST-MD5 realm: ") : new RealmCallback("DIGEST-MD5 realm: ", this.negotiatedRealm);
               this.cbh.handle(new Callback[]{var13, var12, var5});
               this.negotiatedRealm = var13.getText();
               if (this.negotiatedRealm == null) {
                  this.negotiatedRealm = "";
               }
            } else {
               RealmChoiceCallback var6 = new RealmChoiceCallback("DIGEST-MD5 realm: ", var3, 0, false);
               this.cbh.handle(new Callback[]{var6, var12, var5});
               int[] var7 = var6.getSelectedIndexes();
               if (var7 == null || var7[0] < 0 || var7[0] >= var3.length) {
                  throw new SaslException("DIGEST-MD5: Invalid realm chosen");
               }

               this.negotiatedRealm = var3[var7[0]];
            }

            this.passwd = var5.getPassword();
            var5.clearPassword();
            this.username = var12.getName();
         } catch (SaslException var8) {
            throw var8;
         } catch (UnsupportedCallbackException var9) {
            throw new SaslException("DIGEST-MD5: Cannot perform callback to acquire realm, authentication ID or password", var9);
         } catch (IOException var10) {
            throw new SaslException("DIGEST-MD5: Error acquiring realm, authentication ID or password", var10);
         }

         if (this.username != null && this.passwd != null) {
            int var11 = var1[4] == null ? 65536 : Integer.parseInt(new String(var1[4], this.encoding));
            this.sendMaxBufSize = this.sendMaxBufSize == 0 ? var11 : Math.min(this.sendMaxBufSize, var11);
         } else {
            throw new SaslException("DIGEST-MD5: authentication ID and password must be specified");
         }
      }
   }

   private void checkQopSupport(byte[] var1, byte[] var2) throws IOException {
      String var3;
      if (var1 == null) {
         var3 = "auth";
      } else {
         var3 = new String(var1, this.encoding);
      }

      String[] var4 = new String[3];
      byte[] var5 = parseQop(var3, var4, true);
      byte var6 = combineMasks(var5);
      switch(findPreferredMask(var6, this.qop)) {
      case 0:
         throw new SaslException("DIGEST-MD5: No common protection layer between client and server");
      case 1:
         this.negotiatedQop = "auth";
         break;
      case 2:
         this.negotiatedQop = "auth-int";
         this.integrity = true;
         this.rawSendSize = this.sendMaxBufSize - 16;
      case 3:
      default:
         break;
      case 4:
         this.negotiatedQop = "auth-conf";
         this.privacy = this.integrity = true;
         this.rawSendSize = this.sendMaxBufSize - 26;
         this.checkStrengthSupport(var2);
      }

      if (logger.isLoggable(Level.FINE)) {
         logger.log(Level.FINE, (String)"DIGEST61:Raw send size: {0}", (Object)(new Integer(this.rawSendSize)));
      }

   }

   private void checkStrengthSupport(byte[] var1) throws IOException {
      if (var1 == null) {
         throw new SaslException("DIGEST-MD5: server did not specify cipher to use for 'auth-conf'");
      } else {
         String var2 = new String(var1, this.encoding);
         StringTokenizer var3 = new StringTokenizer(var2, ", \t\n");
         int var4 = var3.countTokens();
         String var5 = null;
         byte[] var6 = new byte[]{0, 0, 0, 0, 0};
         String[] var7 = new String[var6.length];

         for(int var8 = 0; var8 < var4; ++var8) {
            var5 = var3.nextToken();

            for(int var9 = 0; var9 < CIPHER_TOKENS.length; ++var9) {
               if (var5.equals(CIPHER_TOKENS[var9])) {
                  var6[var9] |= CIPHER_MASKS[var9];
                  var7[var9] = var5;
                  logger.log(Level.FINE, (String)"DIGEST62:Server supports {0}", (Object)var5);
               }
            }
         }

         byte[] var11 = getPlatformCiphers();
         byte var12 = 0;

         for(int var10 = 0; var10 < var6.length; ++var10) {
            var6[var10] &= var11[var10];
            var12 |= var6[var10];
         }

         if (var12 == 0) {
            throw new SaslException("DIGEST-MD5: Client supports none of these cipher suites: " + var2);
         } else {
            this.negotiatedCipher = this.findCipherAndStrength(var6, var7);
            if (this.negotiatedCipher == null) {
               throw new SaslException("DIGEST-MD5: Unable to negotiate a strength level for 'auth-conf'");
            } else {
               logger.log(Level.FINE, (String)"DIGEST63:Cipher suite: {0}", (Object)this.negotiatedCipher);
            }
         }
      }
   }

   private String findCipherAndStrength(byte[] var1, String[] var2) {
      for(int var4 = 0; var4 < this.strength.length; ++var4) {
         byte var3;
         if ((var3 = this.strength[var4]) != 0) {
            for(int var5 = 0; var5 < var1.length; ++var5) {
               if (var3 == var1[var5] && (this.specifiedCipher == null || this.specifiedCipher.equals(var2[var5]))) {
                  switch(var3) {
                  case 1:
                     this.negotiatedStrength = "low";
                     break;
                  case 2:
                     this.negotiatedStrength = "medium";
                  case 3:
                  default:
                     break;
                  case 4:
                     this.negotiatedStrength = "high";
                  }

                  return var2[var5];
               }
            }
         }
      }

      return null;
   }

   private byte[] generateClientResponse(byte[] var1) throws IOException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      if (this.useUTF8) {
         var2.write("charset=".getBytes(this.encoding));
         var2.write(var1);
         var2.write(44);
      }

      var2.write(("username=\"" + quotedStringValue(this.username) + "\",").getBytes(this.encoding));
      if (this.negotiatedRealm.length() > 0) {
         var2.write(("realm=\"" + quotedStringValue(this.negotiatedRealm) + "\",").getBytes(this.encoding));
      }

      var2.write("nonce=\"".getBytes(this.encoding));
      writeQuotedStringValue(var2, this.nonce);
      var2.write(34);
      var2.write(44);
      this.nonceCount = getNonceCount(this.nonce);
      var2.write(("nc=" + nonceCountToHex(this.nonceCount) + ",").getBytes(this.encoding));
      this.cnonce = generateNonce();
      var2.write("cnonce=\"".getBytes(this.encoding));
      writeQuotedStringValue(var2, this.cnonce);
      var2.write("\",".getBytes(this.encoding));
      var2.write(("digest-uri=\"" + this.digestUri + "\",").getBytes(this.encoding));
      var2.write("maxbuf=".getBytes(this.encoding));
      var2.write(String.valueOf(this.recvMaxBufSize).getBytes(this.encoding));
      var2.write(44);

      try {
         var2.write("response=".getBytes(this.encoding));
         var2.write(this.generateResponseValue("AUTHENTICATE", this.digestUri, this.negotiatedQop, this.username, this.negotiatedRealm, this.passwd, this.nonce, this.cnonce, this.nonceCount, this.authzidBytes));
         var2.write(44);
      } catch (Exception var4) {
         throw new SaslException("DIGEST-MD5: Error generating response value", var4);
      }

      var2.write(("qop=" + this.negotiatedQop).getBytes(this.encoding));
      if (this.negotiatedCipher != null) {
         var2.write((",cipher=\"" + this.negotiatedCipher + "\"").getBytes(this.encoding));
      }

      if (this.authzidBytes != null) {
         var2.write(",authzid=\"".getBytes(this.encoding));
         writeQuotedStringValue(var2, this.authzidBytes);
         var2.write("\"".getBytes(this.encoding));
      }

      if (var2.size() > 4096) {
         throw new SaslException("DIGEST-MD5: digest-response size too large. Length: " + var2.size());
      } else {
         return var2.toByteArray();
      }
   }

   private void validateResponseValue(byte[] var1) throws SaslException {
      if (var1 == null) {
         throw new SaslException("DIGEST-MD5: Authenication failed. Expecting 'rspauth' authentication success message");
      } else {
         try {
            byte[] var2 = this.generateResponseValue("", this.digestUri, this.negotiatedQop, this.username, this.negotiatedRealm, this.passwd, this.nonce, this.cnonce, this.nonceCount, this.authzidBytes);
            if (!Arrays.equals(var2, var1)) {
               throw new SaslException("Server's rspauth value does not match what client expects");
            }
         } catch (NoSuchAlgorithmException var3) {
            throw new SaslException("Problem generating response value for verification", var3);
         } catch (IOException var4) {
            throw new SaslException("Problem generating response value for verification", var4);
         }
      }
   }

   private static int getNonceCount(byte[] var0) {
      return 1;
   }

   private void clearPassword() {
      if (this.passwd != null) {
         for(int var1 = 0; var1 < this.passwd.length; ++var1) {
            this.passwd[var1] = 0;
         }

         this.passwd = null;
      }

   }
}
