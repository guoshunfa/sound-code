package com.sun.security.sasl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.security.sasl.SaslException;

abstract class CramMD5Base {
   protected boolean completed = false;
   protected boolean aborted = false;
   protected byte[] pw;
   private static final int MD5_BLOCKSIZE = 64;
   private static final String SASL_LOGGER_NAME = "javax.security.sasl";
   protected static Logger logger;

   protected CramMD5Base() {
      initLogger();
   }

   public String getMechanismName() {
      return "CRAM-MD5";
   }

   public boolean isComplete() {
      return this.completed;
   }

   public byte[] unwrap(byte[] var1, int var2, int var3) throws SaslException {
      if (this.completed) {
         throw new IllegalStateException("CRAM-MD5 supports neither integrity nor privacy");
      } else {
         throw new IllegalStateException("CRAM-MD5 authentication not completed");
      }
   }

   public byte[] wrap(byte[] var1, int var2, int var3) throws SaslException {
      if (this.completed) {
         throw new IllegalStateException("CRAM-MD5 supports neither integrity nor privacy");
      } else {
         throw new IllegalStateException("CRAM-MD5 authentication not completed");
      }
   }

   public Object getNegotiatedProperty(String var1) {
      if (this.completed) {
         return var1.equals("javax.security.sasl.qop") ? "auth" : null;
      } else {
         throw new IllegalStateException("CRAM-MD5 authentication not completed");
      }
   }

   public void dispose() throws SaslException {
      this.clearPassword();
   }

   protected void clearPassword() {
      if (this.pw != null) {
         for(int var1 = 0; var1 < this.pw.length; ++var1) {
            this.pw[var1] = 0;
         }

         this.pw = null;
      }

   }

   protected void finalize() {
      this.clearPassword();
   }

   static final String HMAC_MD5(byte[] var0, byte[] var1) throws NoSuchAlgorithmException {
      MessageDigest var2 = MessageDigest.getInstance("MD5");
      if (var0.length > 64) {
         var0 = var2.digest(var0);
      }

      byte[] var3 = new byte[64];
      byte[] var4 = new byte[64];

      int var6;
      for(var6 = 0; var6 < var0.length; ++var6) {
         var3[var6] = var0[var6];
         var4[var6] = var0[var6];
      }

      for(var6 = 0; var6 < 64; ++var6) {
         var3[var6] = (byte)(var3[var6] ^ 54);
         var4[var6] = (byte)(var4[var6] ^ 92);
      }

      var2.update(var3);
      var2.update(var1);
      byte[] var5 = var2.digest();
      var2.update(var4);
      var2.update(var5);
      var5 = var2.digest();
      StringBuffer var7 = new StringBuffer();

      for(var6 = 0; var6 < var5.length; ++var6) {
         if ((var5[var6] & 255) < 16) {
            var7.append("0" + Integer.toHexString(var5[var6] & 255));
         } else {
            var7.append(Integer.toHexString(var5[var6] & 255));
         }
      }

      Arrays.fill((byte[])var3, (byte)0);
      Arrays.fill((byte[])var4, (byte)0);
      Object var8 = null;
      Object var9 = null;
      return var7.toString();
   }

   private static synchronized void initLogger() {
      if (logger == null) {
         logger = Logger.getLogger("javax.security.sasl");
      }

   }
}
