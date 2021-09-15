package com.sun.security.sasl.gsskerb;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.MessageProp;

final class GssKrb5Client extends GssKrb5Base implements SaslClient {
   private static final String MY_CLASS_NAME = GssKrb5Client.class.getName();
   private boolean finalHandshake = false;
   private boolean mutual = false;
   private byte[] authzID;

   GssKrb5Client(String var1, String var2, String var3, Map<String, ?> var4, CallbackHandler var5) throws SaslException {
      super(var4, MY_CLASS_NAME);
      String var6 = var2 + "@" + var3;
      logger.log(Level.FINE, (String)"KRB5CLNT01:Requesting service name: {0}", (Object)var6);

      try {
         GSSManager var7 = GSSManager.getInstance();
         GSSName var8 = var7.createName(var6, GSSName.NT_HOSTBASED_SERVICE, KRB5_OID);
         GSSCredential var9 = null;
         if (var4 != null) {
            Object var10 = var4.get("javax.security.sasl.credentials");
            if (var10 != null && var10 instanceof GSSCredential) {
               var9 = (GSSCredential)var10;
               logger.log(Level.FINE, "KRB5CLNT01:Using the credentials supplied in javax.security.sasl.credentials");
            }
         }

         this.secCtx = var7.createContext(var8, KRB5_OID, var9, Integer.MAX_VALUE);
         if (var9 != null) {
            this.secCtx.requestCredDeleg(true);
         }

         if (var4 != null) {
            String var13 = (String)var4.get("javax.security.sasl.server.authentication");
            if (var13 != null) {
               this.mutual = "true".equalsIgnoreCase(var13);
            }
         }

         this.secCtx.requestMutualAuth(this.mutual);
         this.secCtx.requestConf(true);
         this.secCtx.requestInteg(true);
      } catch (GSSException var12) {
         throw new SaslException("Failure to initialize security context", var12);
      }

      if (var1 != null && var1.length() > 0) {
         try {
            this.authzID = var1.getBytes("UTF8");
         } catch (IOException var11) {
            throw new SaslException("Cannot encode authorization ID", var11);
         }
      }

   }

   public boolean hasInitialResponse() {
      return true;
   }

   public byte[] evaluateChallenge(byte[] var1) throws SaslException {
      if (this.completed) {
         throw new IllegalStateException("GSSAPI authentication already complete");
      } else if (this.finalHandshake) {
         return this.doFinalHandshake(var1);
      } else {
         try {
            byte[] var2 = this.secCtx.initSecContext(var1, 0, var1.length);
            if (logger.isLoggable(Level.FINER)) {
               traceOutput(MY_CLASS_NAME, "evaluteChallenge", "KRB5CLNT02:Challenge: [raw]", var1);
               traceOutput(MY_CLASS_NAME, "evaluateChallenge", "KRB5CLNT03:Response: [after initSecCtx]", var2);
            }

            if (this.secCtx.isEstablished()) {
               this.finalHandshake = true;
               if (var2 == null) {
                  return EMPTY;
               }
            }

            return var2;
         } catch (GSSException var3) {
            throw new SaslException("GSS initiate failed", var3);
         }
      }
   }

   private byte[] doFinalHandshake(byte[] var1) throws SaslException {
      try {
         if (logger.isLoggable(Level.FINER)) {
            traceOutput(MY_CLASS_NAME, "doFinalHandshake", "KRB5CLNT04:Challenge [raw]:", var1);
         }

         if (var1.length == 0) {
            return EMPTY;
         } else {
            byte[] var2 = this.secCtx.unwrap(var1, 0, var1.length, new MessageProp(0, false));
            if (logger.isLoggable(Level.FINE)) {
               if (logger.isLoggable(Level.FINER)) {
                  traceOutput(MY_CLASS_NAME, "doFinalHandshake", "KRB5CLNT05:Challenge [unwrapped]:", var2);
               }

               logger.log(Level.FINE, (String)"KRB5CLNT06:Server protections: {0}", (Object)(new Byte(var2[0])));
            }

            byte var3 = findPreferredMask(var2[0], this.qop);
            if (var3 == 0) {
               throw new SaslException("No common protection layer between client and server");
            } else {
               if ((var3 & 4) != 0) {
                  this.privacy = true;
                  this.integrity = true;
               } else if ((var3 & 2) != 0) {
                  this.integrity = true;
               }

               int var4 = networkByteOrderToInt(var2, 1, 3);
               this.sendMaxBufSize = this.sendMaxBufSize == 0 ? var4 : Math.min(this.sendMaxBufSize, var4);
               this.rawSendSize = this.secCtx.getWrapSizeLimit(0, this.privacy, this.sendMaxBufSize);
               if (logger.isLoggable(Level.FINE)) {
                  logger.log(Level.FINE, "KRB5CLNT07:Client max recv size: {0}; server max recv size: {1}; rawSendSize: {2}", new Object[]{new Integer(this.recvMaxBufSize), new Integer(var4), new Integer(this.rawSendSize)});
               }

               int var5 = 4;
               if (this.authzID != null) {
                  var5 += this.authzID.length;
               }

               byte[] var6 = new byte[var5];
               var6[0] = var3;
               if (logger.isLoggable(Level.FINE)) {
                  logger.log(Level.FINE, "KRB5CLNT08:Selected protection: {0}; privacy: {1}; integrity: {2}", new Object[]{new Byte(var3), this.privacy, this.integrity});
               }

               intToNetworkByteOrder(this.recvMaxBufSize, var6, 1, 3);
               if (this.authzID != null) {
                  System.arraycopy(this.authzID, 0, var6, 4, this.authzID.length);
                  logger.log(Level.FINE, (String)"KRB5CLNT09:Authzid: {0}", (Object)this.authzID);
               }

               if (logger.isLoggable(Level.FINER)) {
                  traceOutput(MY_CLASS_NAME, "doFinalHandshake", "KRB5CLNT10:Response [raw]", var6);
               }

               var2 = this.secCtx.wrap(var6, 0, var6.length, new MessageProp(0, false));
               if (logger.isLoggable(Level.FINER)) {
                  traceOutput(MY_CLASS_NAME, "doFinalHandshake", "KRB5CLNT11:Response [after wrap]", var2);
               }

               this.completed = true;
               return var2;
            }
         }
      } catch (GSSException var7) {
         throw new SaslException("Final handshake failed", var7);
      }
   }
}
