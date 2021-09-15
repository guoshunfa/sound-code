package com.sun.security.sasl;

import java.io.UnsupportedEncodingException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

final class PlainClient implements SaslClient {
   private boolean completed = false;
   private byte[] pw;
   private String authorizationID;
   private String authenticationID;
   private static byte SEP = 0;

   PlainClient(String var1, String var2, byte[] var3) throws SaslException {
      if (var2 != null && var3 != null) {
         this.authorizationID = var1;
         this.authenticationID = var2;
         this.pw = var3;
      } else {
         throw new SaslException("PLAIN: authorization ID and password must be specified");
      }
   }

   public String getMechanismName() {
      return "PLAIN";
   }

   public boolean hasInitialResponse() {
      return true;
   }

   public void dispose() throws SaslException {
      this.clearPassword();
   }

   public byte[] evaluateChallenge(byte[] var1) throws SaslException {
      if (this.completed) {
         throw new IllegalStateException("PLAIN authentication already completed");
      } else {
         this.completed = true;

         try {
            byte[] var2 = this.authorizationID != null ? this.authorizationID.getBytes("UTF8") : null;
            byte[] var3 = this.authenticationID.getBytes("UTF8");
            byte[] var4 = new byte[this.pw.length + var3.length + 2 + (var2 == null ? 0 : var2.length)];
            int var5 = 0;
            if (var2 != null) {
               System.arraycopy(var2, 0, var4, 0, var2.length);
               var5 = var2.length;
            }

            var4[var5++] = SEP;
            System.arraycopy(var3, 0, var4, var5, var3.length);
            var5 += var3.length;
            var4[var5++] = SEP;
            System.arraycopy(this.pw, 0, var4, var5, this.pw.length);
            this.clearPassword();
            return var4;
         } catch (UnsupportedEncodingException var6) {
            throw new SaslException("Cannot get UTF-8 encoding of ids", var6);
         }
      }
   }

   public boolean isComplete() {
      return this.completed;
   }

   public byte[] unwrap(byte[] var1, int var2, int var3) throws SaslException {
      if (this.completed) {
         throw new SaslException("PLAIN supports neither integrity nor privacy");
      } else {
         throw new IllegalStateException("PLAIN authentication not completed");
      }
   }

   public byte[] wrap(byte[] var1, int var2, int var3) throws SaslException {
      if (this.completed) {
         throw new SaslException("PLAIN supports neither integrity nor privacy");
      } else {
         throw new IllegalStateException("PLAIN authentication not completed");
      }
   }

   public Object getNegotiatedProperty(String var1) {
      if (this.completed) {
         return var1.equals("javax.security.sasl.qop") ? "auth" : null;
      } else {
         throw new IllegalStateException("PLAIN authentication not completed");
      }
   }

   private void clearPassword() {
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
}
