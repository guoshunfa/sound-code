package com.sun.security.sasl.digest;

import com.sun.security.sasl.util.AbstractSaslImpl;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslException;

abstract class DigestMD5Base extends AbstractSaslImpl {
   private static final String DI_CLASS_NAME = DigestMD5Base.DigestIntegrity.class.getName();
   private static final String DP_CLASS_NAME = DigestMD5Base.DigestPrivacy.class.getName();
   protected static final int MAX_CHALLENGE_LENGTH = 2048;
   protected static final int MAX_RESPONSE_LENGTH = 4096;
   protected static final int DEFAULT_MAXBUF = 65536;
   protected static final int DES3 = 0;
   protected static final int RC4 = 1;
   protected static final int DES = 2;
   protected static final int RC4_56 = 3;
   protected static final int RC4_40 = 4;
   protected static final String[] CIPHER_TOKENS = new String[]{"3des", "rc4", "des", "rc4-56", "rc4-40"};
   private static final String[] JCE_CIPHER_NAME = new String[]{"DESede/CBC/NoPadding", "RC4", "DES/CBC/NoPadding"};
   protected static final byte DES_3_STRENGTH = 4;
   protected static final byte RC4_STRENGTH = 4;
   protected static final byte DES_STRENGTH = 2;
   protected static final byte RC4_56_STRENGTH = 2;
   protected static final byte RC4_40_STRENGTH = 1;
   protected static final byte UNSET = 0;
   protected static final byte[] CIPHER_MASKS = new byte[]{4, 4, 2, 2, 1};
   private static final String SECURITY_LAYER_MARKER = ":00000000000000000000000000000000";
   protected static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
   protected int step;
   protected CallbackHandler cbh;
   protected SecurityCtx secCtx;
   protected byte[] H_A1;
   protected byte[] nonce;
   protected String negotiatedStrength;
   protected String negotiatedCipher;
   protected String negotiatedQop;
   protected String negotiatedRealm;
   protected boolean useUTF8 = false;
   protected String encoding = "8859_1";
   protected String digestUri;
   protected String authzid;
   private static final char[] pem_array = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
   private static final int RAW_NONCE_SIZE = 30;
   private static final int ENCODED_NONCE_SIZE = 40;
   private static final BigInteger MASK = new BigInteger("7f", 16);

   protected DigestMD5Base(Map<String, ?> var1, String var2, int var3, String var4, CallbackHandler var5) throws SaslException {
      super(var1, var2);
      this.step = var3;
      this.digestUri = var4;
      this.cbh = var5;
   }

   public String getMechanismName() {
      return "DIGEST-MD5";
   }

   public byte[] unwrap(byte[] var1, int var2, int var3) throws SaslException {
      if (!this.completed) {
         throw new IllegalStateException("DIGEST-MD5 authentication not completed");
      } else if (this.secCtx == null) {
         throw new IllegalStateException("Neither integrity nor privacy was negotiated");
      } else {
         return this.secCtx.unwrap(var1, var2, var3);
      }
   }

   public byte[] wrap(byte[] var1, int var2, int var3) throws SaslException {
      if (!this.completed) {
         throw new IllegalStateException("DIGEST-MD5 authentication not completed");
      } else if (this.secCtx == null) {
         throw new IllegalStateException("Neither integrity nor privacy was negotiated");
      } else {
         return this.secCtx.wrap(var1, var2, var3);
      }
   }

   public void dispose() throws SaslException {
      if (this.secCtx != null) {
         this.secCtx = null;
      }

   }

   public Object getNegotiatedProperty(String var1) {
      if (this.completed) {
         if (var1.equals("javax.security.sasl.strength")) {
            return this.negotiatedStrength;
         } else {
            return var1.equals("javax.security.sasl.bound.server.name") ? this.digestUri.substring(this.digestUri.indexOf(47) + 1) : super.getNegotiatedProperty(var1);
         }
      } else {
         throw new IllegalStateException("DIGEST-MD5 authentication not completed");
      }
   }

   protected static final byte[] generateNonce() {
      Random var0 = new Random();
      byte[] var1 = new byte[30];
      var0.nextBytes(var1);
      byte[] var2 = new byte[40];
      int var6 = 0;

      for(int var7 = 0; var7 < var1.length; var7 += 3) {
         byte var3 = var1[var7];
         byte var4 = var1[var7 + 1];
         byte var5 = var1[var7 + 2];
         var2[var6++] = (byte)pem_array[var3 >>> 2 & 63];
         var2[var6++] = (byte)pem_array[(var3 << 4 & 48) + (var4 >>> 4 & 15)];
         var2[var6++] = (byte)pem_array[(var4 << 2 & 60) + (var5 >>> 6 & 3)];
         var2[var6++] = (byte)pem_array[var5 & 63];
      }

      return var2;
   }

   protected static void writeQuotedStringValue(ByteArrayOutputStream var0, byte[] var1) {
      int var2 = var1.length;

      for(int var4 = 0; var4 < var2; ++var4) {
         byte var3 = var1[var4];
         if (needEscape((char)var3)) {
            var0.write(92);
         }

         var0.write(var3);
      }

   }

   private static boolean needEscape(String var0) {
      int var1 = var0.length();

      for(int var2 = 0; var2 < var1; ++var2) {
         if (needEscape(var0.charAt(var2))) {
            return true;
         }
      }

      return false;
   }

   private static boolean needEscape(char var0) {
      return var0 == '"' || var0 == '\\' || var0 == 127 || var0 >= 0 && var0 <= 31 && var0 != '\r' && var0 != '\t' && var0 != '\n';
   }

   protected static String quotedStringValue(String var0) {
      if (needEscape(var0)) {
         int var1 = var0.length();
         char[] var2 = new char[var1 + var1];
         int var3 = 0;

         for(int var5 = 0; var5 < var1; ++var5) {
            char var4 = var0.charAt(var5);
            if (needEscape(var4)) {
               var2[var3++] = '\\';
            }

            var2[var3++] = var4;
         }

         return new String(var2, 0, var3);
      } else {
         return var0;
      }
   }

   protected byte[] binaryToHex(byte[] var1) throws UnsupportedEncodingException {
      StringBuffer var2 = new StringBuffer();

      for(int var3 = 0; var3 < var1.length; ++var3) {
         if ((var1[var3] & 255) < 16) {
            var2.append("0" + Integer.toHexString(var1[var3] & 255));
         } else {
            var2.append(Integer.toHexString(var1[var3] & 255));
         }
      }

      return var2.toString().getBytes(this.encoding);
   }

   protected byte[] stringToByte_8859_1(String var1) throws SaslException {
      char[] var2 = var1.toCharArray();

      try {
         if (this.useUTF8) {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               if (var2[var3] > 255) {
                  return var1.getBytes("UTF8");
               }
            }
         }

         return var1.getBytes("8859_1");
      } catch (UnsupportedEncodingException var4) {
         throw new SaslException("cannot encode string in UTF8 or 8859-1 (Latin-1)", var4);
      }
   }

   protected static byte[] getPlatformCiphers() {
      byte[] var0 = new byte[CIPHER_TOKENS.length];

      for(int var1 = 0; var1 < JCE_CIPHER_NAME.length; ++var1) {
         try {
            Cipher.getInstance(JCE_CIPHER_NAME[var1]);
            logger.log(Level.FINE, (String)"DIGEST01:Platform supports {0}", (Object)JCE_CIPHER_NAME[var1]);
            var0[var1] |= CIPHER_MASKS[var1];
         } catch (NoSuchAlgorithmException var3) {
         } catch (NoSuchPaddingException var4) {
         }
      }

      if (var0[1] != 0) {
         var0[3] |= CIPHER_MASKS[3];
         var0[4] |= CIPHER_MASKS[4];
      }

      return var0;
   }

   protected byte[] generateResponseValue(String var1, String var2, String var3, String var4, String var5, char[] var6, byte[] var7, byte[] var8, int var9, byte[] var10) throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException {
      MessageDigest var11 = MessageDigest.getInstance("MD5");
      ByteArrayOutputStream var14 = new ByteArrayOutputStream();
      var14.write((var1 + ":" + var2).getBytes(this.encoding));
      if (var3.equals("auth-conf") || var3.equals("auth-int")) {
         logger.log(Level.FINE, (String)"DIGEST04:QOP: {0}", (Object)var3);
         var14.write(":00000000000000000000000000000000".getBytes(this.encoding));
      }

      if (logger.isLoggable(Level.FINE)) {
         logger.log(Level.FINE, (String)"DIGEST05:A2: {0}", (Object)var14.toString());
      }

      var11.update(var14.toByteArray());
      byte[] var18 = var11.digest();
      byte[] var13 = this.binaryToHex(var18);
      if (logger.isLoggable(Level.FINE)) {
         logger.log(Level.FINE, (String)"DIGEST06:HEX(H(A2)): {0}", (Object)(new String(var13)));
      }

      ByteArrayOutputStream var15 = new ByteArrayOutputStream();
      var15.write(this.stringToByte_8859_1(var4));
      var15.write(58);
      var15.write(this.stringToByte_8859_1(var5));
      var15.write(58);
      var15.write(this.stringToByte_8859_1(new String(var6)));
      var11.update(var15.toByteArray());
      var18 = var11.digest();
      if (logger.isLoggable(Level.FINE)) {
         logger.log(Level.FINE, "DIGEST07:H({0}) = {1}", new Object[]{var15.toString(), new String(this.binaryToHex(var18))});
      }

      ByteArrayOutputStream var16 = new ByteArrayOutputStream();
      var16.write(var18);
      var16.write(58);
      var16.write(var7);
      var16.write(58);
      var16.write(var8);
      if (var10 != null) {
         var16.write(58);
         var16.write(var10);
      }

      var11.update(var16.toByteArray());
      var18 = var11.digest();
      this.H_A1 = var18;
      byte[] var12 = this.binaryToHex(var18);
      if (logger.isLoggable(Level.FINE)) {
         logger.log(Level.FINE, (String)"DIGEST08:H(A1) = {0}", (Object)(new String(var12)));
      }

      ByteArrayOutputStream var17 = new ByteArrayOutputStream();
      var17.write(var12);
      var17.write(58);
      var17.write(var7);
      var17.write(58);
      var17.write(nonceCountToHex(var9).getBytes(this.encoding));
      var17.write(58);
      var17.write(var8);
      var17.write(58);
      var17.write(var3.getBytes(this.encoding));
      var17.write(58);
      var17.write(var13);
      if (logger.isLoggable(Level.FINE)) {
         logger.log(Level.FINE, (String)"DIGEST09:KD: {0}", (Object)var17.toString());
      }

      var11.update(var17.toByteArray());
      var18 = var11.digest();
      byte[] var19 = this.binaryToHex(var18);
      if (logger.isLoggable(Level.FINE)) {
         logger.log(Level.FINE, (String)"DIGEST10:response-value: {0}", (Object)(new String(var19)));
      }

      return var19;
   }

   protected static String nonceCountToHex(int var0) {
      String var1 = Integer.toHexString(var0);
      StringBuffer var2 = new StringBuffer();
      if (var1.length() < 8) {
         for(int var3 = 0; var3 < 8 - var1.length(); ++var3) {
            var2.append("0");
         }
      }

      return var2.toString() + var1;
   }

   protected static byte[][] parseDirectives(byte[] var0, String[] var1, List<byte[]> var2, int var3) throws SaslException {
      byte[][] var4 = new byte[var1.length][];
      ByteArrayOutputStream var5 = new ByteArrayOutputStream(10);
      ByteArrayOutputStream var6 = new ByteArrayOutputStream(10);
      boolean var7 = true;
      boolean var8 = false;
      boolean var9 = false;
      int var11 = skipLws(var0, 0);

      while(true) {
         while(var11 < var0.length) {
            byte var10 = var0[var11];
            if (var7) {
               if (var10 == 44) {
                  if (var5.size() != 0) {
                     throw new SaslException("Directive key contains a ',':" + var5);
                  }

                  var11 = skipLws(var0, var11 + 1);
               } else if (var10 == 61) {
                  if (var5.size() == 0) {
                     throw new SaslException("Empty directive key");
                  }

                  var7 = false;
                  var11 = skipLws(var0, var11 + 1);
                  if (var11 >= var0.length) {
                     throw new SaslException("Valueless directive found: " + var5.toString());
                  }

                  if (var0[var11] == 34) {
                     var8 = true;
                     ++var11;
                  }
               } else if (isLws(var10)) {
                  var11 = skipLws(var0, var11 + 1);
                  if (var11 >= var0.length) {
                     throw new SaslException("'=' expected after key: " + var5.toString());
                  }

                  if (var0[var11] != 61) {
                     throw new SaslException("'=' expected after key: " + var5.toString());
                  }
               } else {
                  var5.write(var10);
                  ++var11;
               }
            } else if (var8) {
               if (var10 == 92) {
                  ++var11;
                  if (var11 >= var0.length) {
                     throw new SaslException("Unmatched quote found for directive: " + var5.toString() + " with value: " + var6.toString());
                  }

                  var6.write(var0[var11]);
                  ++var11;
               } else if (var10 == 34) {
                  ++var11;
                  var8 = false;
                  var9 = true;
               } else {
                  var6.write(var10);
                  ++var11;
               }
            } else if (!isLws(var10) && var10 != 44) {
               if (var9) {
                  throw new SaslException("Expecting comma or linear whitespace after quoted string: \"" + var6.toString() + "\"");
               }

               var6.write(var10);
               ++var11;
            } else {
               extractDirective(var5.toString(), var6.toByteArray(), var1, var4, var2, var3);
               var5.reset();
               var6.reset();
               var7 = true;
               var9 = false;
               var8 = false;
               var11 = skipLws(var0, var11 + 1);
            }
         }

         if (var8) {
            throw new SaslException("Unmatched quote found for directive: " + var5.toString() + " with value: " + var6.toString());
         }

         if (var5.size() > 0) {
            extractDirective(var5.toString(), var6.toByteArray(), var1, var4, var2, var3);
         }

         return var4;
      }
   }

   private static boolean isLws(byte var0) {
      switch(var0) {
      case 9:
      case 10:
      case 13:
      case 32:
         return true;
      default:
         return false;
      }
   }

   private static int skipLws(byte[] var0, int var1) {
      int var2;
      for(var2 = var1; var2 < var0.length; ++var2) {
         if (!isLws(var0[var2])) {
            return var2;
         }
      }

      return var2;
   }

   private static void extractDirective(String var0, byte[] var1, String[] var2, byte[][] var3, List<byte[]> var4, int var5) throws SaslException {
      int var6 = 0;

      while(true) {
         if (var6 < var2.length) {
            if (!var0.equalsIgnoreCase(var2[var6])) {
               ++var6;
               continue;
            }

            if (var3[var6] == null) {
               var3[var6] = var1;
               if (logger.isLoggable(Level.FINE)) {
                  logger.log(Level.FINE, "DIGEST11:Directive {0} = {1}", new Object[]{var2[var6], new String(var3[var6])});
               }
            } else {
               if (var4 == null || var6 != var5) {
                  throw new SaslException("DIGEST-MD5: peer sent more than one " + var0 + " directive: " + new String(var1));
               }

               if (var4.isEmpty()) {
                  var4.add(var3[var6]);
               }

               var4.add(var1);
            }
         }

         return;
      }
   }

   private static void setParityBit(byte[] var0) {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         int var2 = var0[var1] & 254;
         var2 |= Integer.bitCount(var2) & 1 ^ 1;
         var0[var1] = (byte)var2;
      }

   }

   private static byte[] addDesParity(byte[] var0, int var1, int var2) {
      if (var2 != 7) {
         throw new IllegalArgumentException("Invalid length of DES Key Value:" + var2);
      } else {
         byte[] var3 = new byte[7];
         System.arraycopy(var0, var1, var3, 0, var2);
         byte[] var4 = new byte[8];
         BigInteger var5 = new BigInteger(var3);

         for(int var6 = var4.length - 1; var6 >= 0; --var6) {
            var4[var6] = var5.and(MASK).toByteArray()[0];
            var4[var6] = (byte)(var4[var6] << 1);
            var5 = var5.shiftRight(7);
         }

         setParityBit(var4);
         return var4;
      }
   }

   private static SecretKey makeDesKeys(byte[] var0, String var1) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
      byte[] var2 = addDesParity(var0, 0, 7);
      Object var3 = null;
      SecretKeyFactory var4 = SecretKeyFactory.getInstance(var1);
      byte var6 = -1;
      switch(var1.hashCode()) {
      case -1335250348:
         if (var1.equals("desede")) {
            var6 = 1;
         }
         break;
      case 99346:
         if (var1.equals("des")) {
            var6 = 0;
         }
      }

      switch(var6) {
      case 0:
         var3 = new DESKeySpec(var2, 0);
         if (logger.isLoggable(Level.FINEST)) {
            traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST42:DES key input: ", var0);
            traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST43:DES key parity-adjusted: ", var2);
            traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST44:DES key material: ", ((DESKeySpec)var3).getKey());
            logger.log(Level.FINEST, (String)"DIGEST45: is parity-adjusted? {0}", (Object)DESKeySpec.isParityAdjusted(var2, 0));
         }
         break;
      case 1:
         byte[] var7 = addDesParity(var0, 7, 7);
         byte[] var8 = new byte[var2.length * 2 + var7.length];
         System.arraycopy(var2, 0, var8, 0, var2.length);
         System.arraycopy(var7, 0, var8, var2.length, var7.length);
         System.arraycopy(var2, 0, var8, var2.length + var7.length, var2.length);
         var3 = new DESedeKeySpec(var8, 0);
         if (logger.isLoggable(Level.FINEST)) {
            traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST46:3DES key input: ", var0);
            traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST47:3DES key ede: ", var8);
            traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST48:3DES key material: ", ((DESedeKeySpec)var3).getKey());
            logger.log(Level.FINEST, (String)"DIGEST49: is parity-adjusted? ", (Object)DESedeKeySpec.isParityAdjusted(var8, 0));
         }
         break;
      default:
         throw new IllegalArgumentException("Invalid DES strength:" + var1);
      }

      return var4.generateSecret((KeySpec)var3);
   }

   final class DigestPrivacy extends DigestMD5Base.DigestIntegrity implements SecurityCtx {
      private static final String CLIENT_CONF_MAGIC = "Digest H(A1) to client-to-server sealing key magic constant";
      private static final String SVR_CONF_MAGIC = "Digest H(A1) to server-to-client sealing key magic constant";
      private Cipher encCipher;
      private Cipher decCipher;

      DigestPrivacy(boolean var2) throws SaslException {
         super(var2);

         try {
            this.generatePrivacyKeyPair(var2);
         } catch (SaslException var4) {
            throw var4;
         } catch (UnsupportedEncodingException var5) {
            throw new SaslException("DIGEST-MD5: Error encoding string value into UTF-8", var5);
         } catch (IOException var6) {
            throw new SaslException("DIGEST-MD5: Error accessing buffers required to generate cipher keys", var6);
         } catch (NoSuchAlgorithmException var7) {
            throw new SaslException("DIGEST-MD5: Error creating instance of required cipher or digest", var7);
         }
      }

      private void generatePrivacyKeyPair(boolean var1) throws IOException, UnsupportedEncodingException, NoSuchAlgorithmException, SaslException {
         byte[] var2 = "Digest H(A1) to client-to-server sealing key magic constant".getBytes(DigestMD5Base.this.encoding);
         byte[] var3 = "Digest H(A1) to server-to-client sealing key magic constant".getBytes(DigestMD5Base.this.encoding);
         MessageDigest var4 = MessageDigest.getInstance("MD5");
         byte var5;
         if (DigestMD5Base.this.negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[4])) {
            var5 = 5;
         } else if (DigestMD5Base.this.negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[3])) {
            var5 = 7;
         } else {
            var5 = 16;
         }

         byte[] var6 = new byte[var5 + var2.length];
         System.arraycopy(DigestMD5Base.this.H_A1, 0, var6, 0, var5);
         System.arraycopy(var2, 0, var6, var5, var2.length);
         var4.update(var6);
         byte[] var7 = var4.digest();
         System.arraycopy(var3, 0, var6, var5, var3.length);
         var4.update(var6);
         byte[] var8 = var4.digest();
         if (DigestMD5Base.logger.isLoggable(Level.FINER)) {
            DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST24:Kcc: ", var7);
            DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST25:Kcs: ", var8);
         }

         byte[] var9;
         byte[] var10;
         if (var1) {
            var9 = var7;
            var10 = var8;
         } else {
            var9 = var8;
            var10 = var7;
         }

         try {
            if (DigestMD5Base.this.negotiatedCipher.indexOf(DigestMD5Base.CIPHER_TOKENS[1]) > -1) {
               this.encCipher = Cipher.getInstance("RC4");
               this.decCipher = Cipher.getInstance("RC4");
               SecretKeySpec var11 = new SecretKeySpec(var9, "RC4");
               SecretKeySpec var12 = new SecretKeySpec(var10, "RC4");
               this.encCipher.init(1, var11);
               this.decCipher.init(2, var12);
            } else if (DigestMD5Base.this.negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[2]) || DigestMD5Base.this.negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[0])) {
               String var13;
               String var14;
               if (DigestMD5Base.this.negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[2])) {
                  var13 = "DES/CBC/NoPadding";
                  var14 = "des";
               } else {
                  var13 = "DESede/CBC/NoPadding";
                  var14 = "desede";
               }

               this.encCipher = Cipher.getInstance(var13);
               this.decCipher = Cipher.getInstance(var13);
               SecretKey var21 = DigestMD5Base.makeDesKeys(var9, var14);
               SecretKey var22 = DigestMD5Base.makeDesKeys(var10, var14);
               IvParameterSpec var15 = new IvParameterSpec(var9, 8, 8);
               IvParameterSpec var16 = new IvParameterSpec(var10, 8, 8);
               this.encCipher.init(1, var21, var15);
               this.decCipher.init(2, var22, var16);
               if (DigestMD5Base.logger.isLoggable(Level.FINER)) {
                  DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST26:" + DigestMD5Base.this.negotiatedCipher + " IVcc: ", var15.getIV());
                  DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST27:" + DigestMD5Base.this.negotiatedCipher + " IVcs: ", var16.getIV());
                  DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST28:" + DigestMD5Base.this.negotiatedCipher + " encryption key: ", var21.getEncoded());
                  DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST29:" + DigestMD5Base.this.negotiatedCipher + " decryption key: ", var22.getEncoded());
               }
            }

         } catch (InvalidKeySpecException var17) {
            throw new SaslException("DIGEST-MD5: Unsupported key specification used.", var17);
         } catch (InvalidAlgorithmParameterException var18) {
            throw new SaslException("DIGEST-MD5: Invalid cipher algorithem parameter used to create cipher instance", var18);
         } catch (NoSuchPaddingException var19) {
            throw new SaslException("DIGEST-MD5: Unsupported padding used for chosen cipher", var19);
         } catch (InvalidKeyException var20) {
            throw new SaslException("DIGEST-MD5: Invalid data used to initialize keys", var20);
         }
      }

      public byte[] wrap(byte[] var1, int var2, int var3) throws SaslException {
         if (var3 == 0) {
            return DigestMD5Base.EMPTY_BYTE_ARRAY;
         } else {
            this.incrementSeqNum();
            byte[] var4 = this.getHMAC(this.myKi, this.sequenceNum, var1, var2, var3);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
               DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "wrap", "DIGEST30:Outgoing: ", var1, var2, var3);
               DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "wrap", "seqNum: ", this.sequenceNum);
               DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "wrap", "MAC: ", var4);
            }

            int var5 = this.encCipher.getBlockSize();
            byte[] var6;
            if (var5 > 1) {
               int var7 = var5 - (var3 + 10) % var5;
               var6 = new byte[var7];

               for(int var8 = 0; var8 < var7; ++var8) {
                  var6[var8] = (byte)var7;
               }
            } else {
               var6 = DigestMD5Base.EMPTY_BYTE_ARRAY;
            }

            byte[] var11 = new byte[var3 + var6.length + 10];
            System.arraycopy(var1, var2, var11, 0, var3);
            System.arraycopy(var6, 0, var11, var3, var6.length);
            System.arraycopy(var4, 0, var11, var3 + var6.length, 10);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
               DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "wrap", "DIGEST31:{msg, pad, KicMAC}: ", var11);
            }

            byte[] var12;
            try {
               var12 = this.encCipher.update(var11);
               if (var12 == null) {
                  throw new IllegalBlockSizeException("" + var11.length);
               }
            } catch (IllegalBlockSizeException var10) {
               throw new SaslException("DIGEST-MD5: Invalid block size for cipher", var10);
            }

            byte[] var9 = new byte[var12.length + 2 + 4];
            System.arraycopy(var12, 0, var9, 0, var12.length);
            System.arraycopy(this.messageType, 0, var9, var12.length, 2);
            System.arraycopy(this.sequenceNum, 0, var9, var12.length + 2, 4);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
               DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "wrap", "DIGEST32:Wrapped: ", var9);
            }

            return var9;
         }
      }

      public byte[] unwrap(byte[] var1, int var2, int var3) throws SaslException {
         if (var3 == 0) {
            return DigestMD5Base.EMPTY_BYTE_ARRAY;
         } else {
            byte[] var4 = new byte[var3 - 6];
            byte[] var5 = new byte[2];
            byte[] var6 = new byte[4];
            System.arraycopy(var1, var2, var4, 0, var4.length);
            System.arraycopy(var1, var2 + var4.length, var5, 0, 2);
            System.arraycopy(var1, var2 + var4.length + 2, var6, 0, 4);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
               DigestMD5Base.logger.log(Level.FINEST, (String)"DIGEST33:Expecting sequence num: {0}", (Object)(new Integer(this.peerSeqNum)));
               DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST34:incoming: ", var4);
            }

            byte[] var7;
            try {
               var7 = this.decCipher.update(var4);
               if (var7 == null) {
                  throw new IllegalBlockSizeException("" + var4.length);
               }
            } catch (IllegalBlockSizeException var14) {
               throw new SaslException("DIGEST-MD5: Illegal block sizes used with chosen cipher", var14);
            }

            byte[] var8 = new byte[var7.length - 10];
            byte[] var9 = new byte[10];
            System.arraycopy(var7, 0, var8, 0, var8.length);
            System.arraycopy(var7, var8.length, var9, 0, 10);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
               DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST35:Unwrapped (w/padding): ", var8);
               DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST36:MAC: ", var9);
               DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST37:messageType: ", var5);
               DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST38:sequenceNum: ", var6);
            }

            int var10 = var8.length;
            int var11 = this.decCipher.getBlockSize();
            if (var11 > 1) {
               var10 -= var8[var8.length - 1];
               if (var10 < 0) {
                  if (DigestMD5Base.logger.isLoggable(Level.INFO)) {
                     DigestMD5Base.logger.log(Level.INFO, (String)"DIGEST39:Incorrect padding: {0}", (Object)(new Byte(var8[var8.length - 1])));
                  }

                  return DigestMD5Base.EMPTY_BYTE_ARRAY;
               }
            }

            byte[] var12 = this.getHMAC(this.peerKi, var6, var8, 0, var10);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
               DigestMD5Base.traceOutput(DigestMD5Base.DP_CLASS_NAME, "unwrap", "DIGEST40:KisMAC: ", var12);
            }

            if (!Arrays.equals(var9, var12)) {
               DigestMD5Base.logger.log(Level.INFO, "DIGEST41:Unmatched MACs");
               return DigestMD5Base.EMPTY_BYTE_ARRAY;
            } else if (this.peerSeqNum != DigestMD5Base.networkByteOrderToInt(var6, 0, 4)) {
               throw new SaslException("DIGEST-MD5: Out of order sequencing of messages from server. Got: " + DigestMD5Base.networkByteOrderToInt(var6, 0, 4) + " Expected: " + this.peerSeqNum);
            } else if (!Arrays.equals(this.messageType, var5)) {
               throw new SaslException("DIGEST-MD5: invalid message type: " + DigestMD5Base.networkByteOrderToInt(var5, 0, 2));
            } else {
               ++this.peerSeqNum;
               if (var10 == var8.length) {
                  return var8;
               } else {
                  byte[] var13 = new byte[var10];
                  System.arraycopy(var8, 0, var13, 0, var10);
                  return var13;
               }
            }
         }
      }
   }

   class DigestIntegrity implements SecurityCtx {
      private static final String CLIENT_INT_MAGIC = "Digest session key to client-to-server signing key magic constant";
      private static final String SVR_INT_MAGIC = "Digest session key to server-to-client signing key magic constant";
      protected byte[] myKi;
      protected byte[] peerKi;
      protected int mySeqNum = 0;
      protected int peerSeqNum = 0;
      protected final byte[] messageType = new byte[2];
      protected final byte[] sequenceNum = new byte[4];

      DigestIntegrity(boolean var2) throws SaslException {
         try {
            this.generateIntegrityKeyPair(var2);
         } catch (UnsupportedEncodingException var4) {
            throw new SaslException("DIGEST-MD5: Error encoding strings into UTF-8", var4);
         } catch (IOException var5) {
            throw new SaslException("DIGEST-MD5: Error accessing buffers required to create integrity key pairs", var5);
         } catch (NoSuchAlgorithmException var6) {
            throw new SaslException("DIGEST-MD5: Unsupported digest algorithm used to create integrity key pairs", var6);
         }

         DigestMD5Base.intToNetworkByteOrder(1, this.messageType, 0, 2);
      }

      private void generateIntegrityKeyPair(boolean var1) throws UnsupportedEncodingException, IOException, NoSuchAlgorithmException {
         byte[] var2 = "Digest session key to client-to-server signing key magic constant".getBytes(DigestMD5Base.this.encoding);
         byte[] var3 = "Digest session key to server-to-client signing key magic constant".getBytes(DigestMD5Base.this.encoding);
         MessageDigest var4 = MessageDigest.getInstance("MD5");
         byte[] var5 = new byte[DigestMD5Base.this.H_A1.length + var2.length];
         System.arraycopy(DigestMD5Base.this.H_A1, 0, var5, 0, DigestMD5Base.this.H_A1.length);
         System.arraycopy(var2, 0, var5, DigestMD5Base.this.H_A1.length, var2.length);
         var4.update(var5);
         byte[] var6 = var4.digest();
         System.arraycopy(var3, 0, var5, DigestMD5Base.this.H_A1.length, var3.length);
         var4.update(var5);
         byte[] var7 = var4.digest();
         if (DigestMD5Base.logger.isLoggable(Level.FINER)) {
            DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "generateIntegrityKeyPair", "DIGEST12:Kic: ", var6);
            DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "generateIntegrityKeyPair", "DIGEST13:Kis: ", var7);
         }

         if (var1) {
            this.myKi = var6;
            this.peerKi = var7;
         } else {
            this.myKi = var7;
            this.peerKi = var6;
         }

      }

      public byte[] wrap(byte[] var1, int var2, int var3) throws SaslException {
         if (var3 == 0) {
            return DigestMD5Base.EMPTY_BYTE_ARRAY;
         } else {
            byte[] var4 = new byte[var3 + 10 + 2 + 4];
            System.arraycopy(var1, var2, var4, 0, var3);
            this.incrementSeqNum();
            byte[] var5 = this.getHMAC(this.myKi, this.sequenceNum, var1, var2, var3);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
               DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "wrap", "DIGEST14:outgoing: ", var1, var2, var3);
               DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "wrap", "DIGEST15:seqNum: ", this.sequenceNum);
               DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "wrap", "DIGEST16:MAC: ", var5);
            }

            System.arraycopy(var5, 0, var4, var3, 10);
            System.arraycopy(this.messageType, 0, var4, var3 + 10, 2);
            System.arraycopy(this.sequenceNum, 0, var4, var3 + 12, 4);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
               DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "wrap", "DIGEST17:wrapped: ", var4);
            }

            return var4;
         }
      }

      public byte[] unwrap(byte[] var1, int var2, int var3) throws SaslException {
         if (var3 == 0) {
            return DigestMD5Base.EMPTY_BYTE_ARRAY;
         } else {
            byte[] var4 = new byte[10];
            byte[] var5 = new byte[var3 - 16];
            byte[] var6 = new byte[2];
            byte[] var7 = new byte[4];
            System.arraycopy(var1, var2, var5, 0, var5.length);
            System.arraycopy(var1, var2 + var5.length, var4, 0, 10);
            System.arraycopy(var1, var2 + var5.length + 10, var6, 0, 2);
            System.arraycopy(var1, var2 + var5.length + 12, var7, 0, 4);
            byte[] var8 = this.getHMAC(this.peerKi, var7, var5, 0, var5.length);
            if (DigestMD5Base.logger.isLoggable(Level.FINEST)) {
               DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "unwrap", "DIGEST18:incoming: ", var5);
               DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "unwrap", "DIGEST19:MAC: ", var4);
               DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "unwrap", "DIGEST20:messageType: ", var6);
               DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "unwrap", "DIGEST21:sequenceNum: ", var7);
               DigestMD5Base.traceOutput(DigestMD5Base.DI_CLASS_NAME, "unwrap", "DIGEST22:expectedMAC: ", var8);
            }

            if (!Arrays.equals(var4, var8)) {
               DigestMD5Base.logger.log(Level.INFO, "DIGEST23:Unmatched MACs");
               return DigestMD5Base.EMPTY_BYTE_ARRAY;
            } else if (this.peerSeqNum != DigestMD5Base.networkByteOrderToInt(var7, 0, 4)) {
               throw new SaslException("DIGEST-MD5: Out of order sequencing of messages from server. Got: " + DigestMD5Base.networkByteOrderToInt(var7, 0, 4) + " Expected: " + this.peerSeqNum);
            } else if (!Arrays.equals(this.messageType, var6)) {
               throw new SaslException("DIGEST-MD5: invalid message type: " + DigestMD5Base.networkByteOrderToInt(var6, 0, 2));
            } else {
               ++this.peerSeqNum;
               return var5;
            }
         }
      }

      protected byte[] getHMAC(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) throws SaslException {
         byte[] var6 = new byte[4 + var5];
         System.arraycopy(var2, 0, var6, 0, 4);
         System.arraycopy(var3, var4, var6, 4, var5);

         try {
            SecretKeySpec var7 = new SecretKeySpec(var1, "HmacMD5");
            Mac var8 = Mac.getInstance("HmacMD5");
            var8.init(var7);
            var8.update(var6);
            byte[] var9 = var8.doFinal();
            byte[] var10 = new byte[10];
            System.arraycopy(var9, 0, var10, 0, 10);
            return var10;
         } catch (InvalidKeyException var11) {
            throw new SaslException("DIGEST-MD5: Invalid bytes used for key of HMAC-MD5 hash.", var11);
         } catch (NoSuchAlgorithmException var12) {
            throw new SaslException("DIGEST-MD5: Error creating instance of MD5 digest algorithm", var12);
         }
      }

      protected void incrementSeqNum() {
         DigestMD5Base.intToNetworkByteOrder(this.mySeqNum++, this.sequenceNum, 0, 4);
      }
   }
}
