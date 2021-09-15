package com.sun.security.sasl;

import java.io.UnsupportedEncodingException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

final class ExternalClient implements SaslClient {
   private byte[] username;
   private boolean completed = false;

   ExternalClient(String var1) throws SaslException {
      if (var1 != null) {
         try {
            this.username = var1.getBytes("UTF8");
         } catch (UnsupportedEncodingException var3) {
            throw new SaslException("Cannot convert " + var1 + " into UTF-8", var3);
         }
      } else {
         this.username = new byte[0];
      }

   }

   public String getMechanismName() {
      return "EXTERNAL";
   }

   public boolean hasInitialResponse() {
      return true;
   }

   public void dispose() throws SaslException {
   }

   public byte[] evaluateChallenge(byte[] var1) throws SaslException {
      if (this.completed) {
         throw new IllegalStateException("EXTERNAL authentication already completed");
      } else {
         this.completed = true;
         return this.username;
      }
   }

   public boolean isComplete() {
      return this.completed;
   }

   public byte[] unwrap(byte[] var1, int var2, int var3) throws SaslException {
      if (this.completed) {
         throw new SaslException("EXTERNAL has no supported QOP");
      } else {
         throw new IllegalStateException("EXTERNAL authentication Not completed");
      }
   }

   public byte[] wrap(byte[] var1, int var2, int var3) throws SaslException {
      if (this.completed) {
         throw new SaslException("EXTERNAL has no supported QOP");
      } else {
         throw new IllegalStateException("EXTERNAL authentication not completed");
      }
   }

   public Object getNegotiatedProperty(String var1) {
      if (this.completed) {
         return null;
      } else {
         throw new IllegalStateException("EXTERNAL authentication not completed");
      }
   }
}
