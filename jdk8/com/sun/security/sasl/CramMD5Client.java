package com.sun.security.sasl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

final class CramMD5Client extends CramMD5Base implements SaslClient {
   private String username;

   CramMD5Client(String var1, byte[] var2) throws SaslException {
      if (var1 != null && var2 != null) {
         this.username = var1;
         this.pw = var2;
      } else {
         throw new SaslException("CRAM-MD5: authentication ID and password must be specified");
      }
   }

   public boolean hasInitialResponse() {
      return false;
   }

   public byte[] evaluateChallenge(byte[] var1) throws SaslException {
      if (this.completed) {
         throw new IllegalStateException("CRAM-MD5 authentication already completed");
      } else if (this.aborted) {
         throw new IllegalStateException("CRAM-MD5 authentication previously aborted due to error");
      } else {
         try {
            if (logger.isLoggable(Level.FINE)) {
               logger.log(Level.FINE, (String)"CRAMCLNT01:Received challenge: {0}", (Object)(new String(var1, "UTF8")));
            }

            String var2 = HMAC_MD5(this.pw, var1);
            this.clearPassword();
            String var3 = this.username + " " + var2;
            logger.log(Level.FINE, (String)"CRAMCLNT02:Sending response: {0}", (Object)var3);
            this.completed = true;
            return var3.getBytes("UTF8");
         } catch (NoSuchAlgorithmException var4) {
            this.aborted = true;
            throw new SaslException("MD5 algorithm not available on platform", var4);
         } catch (UnsupportedEncodingException var5) {
            this.aborted = true;
            throw new SaslException("UTF8 not available on platform", var5);
         }
      }
   }
}
