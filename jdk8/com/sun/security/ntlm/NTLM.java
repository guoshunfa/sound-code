package com.sun.security.ntlm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Locale;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.HexDumpEncoder;
import sun.security.provider.MD4;

class NTLM {
   private final SecretKeyFactory fac;
   private final Cipher cipher;
   private final MessageDigest md4;
   private final Mac hmac;
   private final MessageDigest md5;
   private static final boolean DEBUG = System.getProperty("ntlm.debug") != null;
   final Version v;
   final boolean writeLM;
   final boolean writeNTLM;

   protected NTLM(String var1) throws NTLMException {
      if (var1 == null) {
         var1 = "LMv2/NTLMv2";
      }

      byte var3 = -1;
      switch(var1.hashCode()) {
      case -1981975933:
         if (var1.equals("NTLMv2")) {
            var3 = 5;
         }
         break;
      case -1885054699:
         if (var1.equals("LMv2/NTLMv2")) {
            var3 = 6;
         }
         break;
      case 2433:
         if (var1.equals("LM")) {
            var3 = 0;
         }
         break;
      case 2341821:
         if (var1.equals("LMv2")) {
            var3 = 4;
         }
         break;
      case 2406855:
         if (var1.equals("NTLM")) {
            var3 = 1;
         }
         break;
      case 74612555:
         if (var1.equals("NTLM2")) {
            var3 = 3;
         }
         break;
      case 981059989:
         if (var1.equals("LM/NTLM")) {
            var3 = 2;
         }
      }

      switch(var3) {
      case 0:
         this.v = Version.NTLM;
         this.writeLM = true;
         this.writeNTLM = false;
         break;
      case 1:
         this.v = Version.NTLM;
         this.writeLM = false;
         this.writeNTLM = true;
         break;
      case 2:
         this.v = Version.NTLM;
         this.writeLM = this.writeNTLM = true;
         break;
      case 3:
         this.v = Version.NTLM2;
         this.writeLM = this.writeNTLM = true;
         break;
      case 4:
         this.v = Version.NTLMv2;
         this.writeLM = true;
         this.writeNTLM = false;
         break;
      case 5:
         this.v = Version.NTLMv2;
         this.writeLM = false;
         this.writeNTLM = true;
         break;
      case 6:
         this.v = Version.NTLMv2;
         this.writeLM = this.writeNTLM = true;
         break;
      default:
         throw new NTLMException(5, "Unknown version " + var1);
      }

      try {
         this.fac = SecretKeyFactory.getInstance("DES");
         this.cipher = Cipher.getInstance("DES/ECB/NoPadding");
         this.md4 = MD4.getInstance();
         this.hmac = Mac.getInstance("HmacMD5");
         this.md5 = MessageDigest.getInstance("MD5");
      } catch (NoSuchPaddingException var4) {
         throw new AssertionError();
      } catch (NoSuchAlgorithmException var5) {
         throw new AssertionError();
      }
   }

   public void debug(String var1, Object... var2) {
      if (DEBUG) {
         System.out.printf(var1, var2);
      }

   }

   public void debug(byte[] var1) {
      if (DEBUG) {
         try {
            (new HexDumpEncoder()).encodeBuffer(var1, System.out);
         } catch (IOException var3) {
         }
      }

   }

   byte[] makeDesKey(byte[] var1, int var2) {
      int[] var3 = new int[var1.length];

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var3[var4] = var1[var4] < 0 ? var1[var4] + 256 : var1[var4];
      }

      byte[] var5 = new byte[]{(byte)var3[var2 + 0], (byte)(var3[var2 + 0] << 7 & 255 | var3[var2 + 1] >> 1), (byte)(var3[var2 + 1] << 6 & 255 | var3[var2 + 2] >> 2), (byte)(var3[var2 + 2] << 5 & 255 | var3[var2 + 3] >> 3), (byte)(var3[var2 + 3] << 4 & 255 | var3[var2 + 4] >> 4), (byte)(var3[var2 + 4] << 3 & 255 | var3[var2 + 5] >> 5), (byte)(var3[var2 + 5] << 2 & 255 | var3[var2 + 6] >> 6), (byte)(var3[var2 + 6] << 1 & 255)};
      return var5;
   }

   byte[] calcLMHash(byte[] var1) {
      byte[] var2 = new byte[]{75, 71, 83, 33, 64, 35, 36, 37};
      byte[] var3 = new byte[14];
      int var4 = var1.length;
      if (var4 > 14) {
         var4 = 14;
      }

      System.arraycopy(var1, 0, var3, 0, var4);

      try {
         DESKeySpec var5 = new DESKeySpec(this.makeDesKey(var3, 0));
         DESKeySpec var6 = new DESKeySpec(this.makeDesKey(var3, 7));
         SecretKey var7 = this.fac.generateSecret(var5);
         SecretKey var8 = this.fac.generateSecret(var6);
         this.cipher.init(1, var7);
         byte[] var9 = this.cipher.doFinal(var2, 0, 8);
         this.cipher.init(1, var8);
         byte[] var10 = this.cipher.doFinal(var2, 0, 8);
         byte[] var11 = new byte[21];
         System.arraycopy(var9, 0, var11, 0, 8);
         System.arraycopy(var10, 0, var11, 8, 8);
         return var11;
      } catch (InvalidKeyException var12) {
         assert false;
      } catch (InvalidKeySpecException var13) {
         assert false;
      } catch (IllegalBlockSizeException var14) {
         assert false;
      } catch (BadPaddingException var15) {
         assert false;
      }

      return null;
   }

   byte[] calcNTHash(byte[] var1) {
      byte[] var2 = this.md4.digest(var1);
      byte[] var3 = new byte[21];
      System.arraycopy(var2, 0, var3, 0, 16);
      return var3;
   }

   byte[] calcResponse(byte[] var1, byte[] var2) {
      try {
         assert var1.length == 21;

         DESKeySpec var3 = new DESKeySpec(this.makeDesKey(var1, 0));
         DESKeySpec var4 = new DESKeySpec(this.makeDesKey(var1, 7));
         DESKeySpec var5 = new DESKeySpec(this.makeDesKey(var1, 14));
         SecretKey var6 = this.fac.generateSecret(var3);
         SecretKey var7 = this.fac.generateSecret(var4);
         SecretKey var8 = this.fac.generateSecret(var5);
         this.cipher.init(1, var6);
         byte[] var9 = this.cipher.doFinal(var2, 0, 8);
         this.cipher.init(1, var7);
         byte[] var10 = this.cipher.doFinal(var2, 0, 8);
         this.cipher.init(1, var8);
         byte[] var11 = this.cipher.doFinal(var2, 0, 8);
         byte[] var12 = new byte[24];
         System.arraycopy(var9, 0, var12, 0, 8);
         System.arraycopy(var10, 0, var12, 8, 8);
         System.arraycopy(var11, 0, var12, 16, 8);
         return var12;
      } catch (IllegalBlockSizeException var13) {
         assert false;
      } catch (BadPaddingException var14) {
         assert false;
      } catch (InvalidKeySpecException var15) {
         assert false;
      } catch (InvalidKeyException var16) {
         assert false;
      }

      return null;
   }

   byte[] hmacMD5(byte[] var1, byte[] var2) {
      try {
         SecretKeySpec var3 = new SecretKeySpec(Arrays.copyOf((byte[])var1, 16), "HmacMD5");
         this.hmac.init(var3);
         return this.hmac.doFinal(var2);
      } catch (InvalidKeyException var4) {
         assert false;
      } catch (RuntimeException var5) {
         assert false;
      }

      return null;
   }

   byte[] calcV2(byte[] var1, String var2, byte[] var3, byte[] var4) {
      try {
         byte[] var5 = this.hmacMD5(var1, var2.getBytes("UnicodeLittleUnmarked"));
         byte[] var6 = new byte[var3.length + 8];
         System.arraycopy(var4, 0, var6, 0, 8);
         System.arraycopy(var3, 0, var6, 8, var3.length);
         byte[] var7 = new byte[16 + var3.length];
         System.arraycopy(this.hmacMD5(var5, var6), 0, var7, 0, 16);
         System.arraycopy(var3, 0, var7, 16, var3.length);
         return var7;
      } catch (UnsupportedEncodingException var8) {
         assert false;

         return null;
      }
   }

   static byte[] ntlm2LM(byte[] var0) {
      return Arrays.copyOf((byte[])var0, 24);
   }

   byte[] ntlm2NTLM(byte[] var1, byte[] var2, byte[] var3) {
      byte[] var4 = Arrays.copyOf((byte[])var3, 16);
      System.arraycopy(var2, 0, var4, 8, 8);
      byte[] var5 = Arrays.copyOf((byte[])this.md5.digest(var4), 8);
      return this.calcResponse(var1, var5);
   }

   static byte[] getP1(char[] var0) {
      try {
         return (new String(var0)).toUpperCase(Locale.ENGLISH).getBytes("ISO8859_1");
      } catch (UnsupportedEncodingException var2) {
         return null;
      }
   }

   static byte[] getP2(char[] var0) {
      try {
         return (new String(var0)).getBytes("UnicodeLittleUnmarked");
      } catch (UnsupportedEncodingException var2) {
         return null;
      }
   }

   static class Writer {
      private byte[] internal;
      private int current;

      Writer(int var1, int var2) {
         assert var2 < 256;

         this.internal = new byte[256];
         this.current = var2;
         System.arraycopy(new byte[]{78, 84, 76, 77, 83, 83, 80, 0, (byte)var1}, 0, this.internal, 0, 9);
      }

      void writeShort(int var1, int var2) {
         this.internal[var1] = (byte)var2;
         this.internal[var1 + 1] = (byte)(var2 >> 8);
      }

      void writeInt(int var1, int var2) {
         this.internal[var1] = (byte)var2;
         this.internal[var1 + 1] = (byte)(var2 >> 8);
         this.internal[var1 + 2] = (byte)(var2 >> 16);
         this.internal[var1 + 3] = (byte)(var2 >> 24);
      }

      void writeBytes(int var1, byte[] var2) {
         System.arraycopy(var2, 0, this.internal, var1, var2.length);
      }

      void writeSecurityBuffer(int var1, byte[] var2) {
         if (var2 == null) {
            this.writeShort(var1 + 4, this.current);
         } else {
            int var3 = var2.length;
            if (this.current + var3 > this.internal.length) {
               this.internal = Arrays.copyOf(this.internal, this.current + var3 + 256);
            }

            this.writeShort(var1, var3);
            this.writeShort(var1 + 2, var3);
            this.writeShort(var1 + 4, this.current);
            System.arraycopy(var2, 0, this.internal, this.current, var3);
            this.current += var3;
         }

      }

      void writeSecurityBuffer(int var1, String var2, boolean var3) {
         try {
            this.writeSecurityBuffer(var1, var2 == null ? null : var2.getBytes(var3 ? "UnicodeLittleUnmarked" : "ISO8859_1"));
         } catch (UnsupportedEncodingException var5) {
            assert false;
         }

      }

      byte[] getBytes() {
         return Arrays.copyOf(this.internal, this.current);
      }
   }

   static class Reader {
      private final byte[] internal;

      Reader(byte[] var1) {
         this.internal = var1;
      }

      int readInt(int var1) throws NTLMException {
         try {
            return (this.internal[var1] & 255) + ((this.internal[var1 + 1] & 255) << 8) + ((this.internal[var1 + 2] & 255) << 16) + ((this.internal[var1 + 3] & 255) << 24);
         } catch (ArrayIndexOutOfBoundsException var3) {
            throw new NTLMException(1, "Input message incorrect size");
         }
      }

      int readShort(int var1) throws NTLMException {
         try {
            return (this.internal[var1] & 255) + (this.internal[var1 + 1] & '\uff00');
         } catch (ArrayIndexOutOfBoundsException var3) {
            throw new NTLMException(1, "Input message incorrect size");
         }
      }

      byte[] readBytes(int var1, int var2) throws NTLMException {
         try {
            return Arrays.copyOfRange(this.internal, var1, var1 + var2);
         } catch (ArrayIndexOutOfBoundsException var4) {
            throw new NTLMException(1, "Input message incorrect size");
         }
      }

      byte[] readSecurityBuffer(int var1) throws NTLMException {
         int var2 = this.readInt(var1 + 4);
         if (var2 == 0) {
            return null;
         } else {
            try {
               return Arrays.copyOfRange(this.internal, var2, var2 + this.readShort(var1));
            } catch (ArrayIndexOutOfBoundsException var4) {
               throw new NTLMException(1, "Input message incorrect size");
            }
         }
      }

      String readSecurityBuffer(int var1, boolean var2) throws NTLMException {
         byte[] var3 = this.readSecurityBuffer(var1);

         try {
            return var3 == null ? null : new String(var3, var2 ? "UnicodeLittleUnmarked" : "ISO8859_1");
         } catch (UnsupportedEncodingException var5) {
            throw new NTLMException(1, "Invalid input encoding");
         }
      }
   }
}
