package com.sun.security.sasl.gsskerb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.logging.Level;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;

final class GssKrb5Server extends GssKrb5Base implements SaslServer {
   private static final String MY_CLASS_NAME = GssKrb5Server.class.getName();
   private int handshakeStage = 0;
   private String peer;
   private String me;
   private String authzid;
   private CallbackHandler cbh;
   private final String protocolSaved;

   GssKrb5Server(String var1, String var2, Map<String, ?> var3, CallbackHandler var4) throws SaslException {
      super(var3, MY_CLASS_NAME);
      this.cbh = var4;
      String var5;
      if (var2 == null) {
         this.protocolSaved = var1;
         var5 = null;
      } else {
         this.protocolSaved = null;
         var5 = var1 + "@" + var2;
      }

      logger.log(Level.FINE, (String)"KRB5SRV01:Using service name: {0}", (Object)var5);

      try {
         GSSManager var6 = GSSManager.getInstance();
         GSSName var7 = var5 == null ? null : var6.createName(var5, GSSName.NT_HOSTBASED_SERVICE, KRB5_OID);
         GSSCredential var8 = var6.createCredential(var7, Integer.MAX_VALUE, (Oid)KRB5_OID, 2);
         this.secCtx = var6.createContext(var8);
         if ((this.allQop & 2) != 0) {
            this.secCtx.requestInteg(true);
         }

         if ((this.allQop & 4) != 0) {
            this.secCtx.requestConf(true);
         }
      } catch (GSSException var9) {
         throw new SaslException("Failure to initialize security context", var9);
      }

      logger.log(Level.FINE, "KRB5SRV02:Initialization complete");
   }

   public byte[] evaluateResponse(byte[] var1) throws SaslException {
      if (this.completed) {
         throw new SaslException("SASL authentication already complete");
      } else {
         if (logger.isLoggable(Level.FINER)) {
            traceOutput(MY_CLASS_NAME, "evaluateResponse", "KRB5SRV03:Response [raw]:", var1);
         }

         switch(this.handshakeStage) {
         case 1:
            return this.doHandshake1(var1);
         case 2:
            return this.doHandshake2(var1);
         default:
            try {
               byte[] var2 = this.secCtx.acceptSecContext(var1, 0, var1.length);
               if (logger.isLoggable(Level.FINER)) {
                  traceOutput(MY_CLASS_NAME, "evaluateResponse", "KRB5SRV04:Challenge: [after acceptSecCtx]", var2);
               }

               if (this.secCtx.isEstablished()) {
                  this.handshakeStage = 1;
                  this.peer = this.secCtx.getSrcName().toString();
                  this.me = this.secCtx.getTargName().toString();
                  logger.log(Level.FINE, "KRB5SRV05:Peer name is : {0}, my name is : {1}", new Object[]{this.peer, this.me});
                  if (this.protocolSaved != null && !this.protocolSaved.equalsIgnoreCase(this.me.split("[/@]")[0])) {
                     throw new SaslException("GSS context targ name protocol error: " + this.me);
                  }

                  if (var2 == null) {
                     return this.doHandshake1(EMPTY);
                  }
               }

               return var2;
            } catch (GSSException var3) {
               throw new SaslException("GSS initiate failed", var3);
            }
         }
      }
   }

   private byte[] doHandshake1(byte[] var1) throws SaslException {
      try {
         if (var1 != null && var1.length > 0) {
            throw new SaslException("Handshake expecting no response data from server");
         } else {
            byte[] var2 = new byte[4];
            var2[0] = this.allQop;
            intToNetworkByteOrder(this.recvMaxBufSize, var2, 1, 3);
            if (logger.isLoggable(Level.FINE)) {
               logger.log(Level.FINE, "KRB5SRV06:Supported protections: {0}; recv max buf size: {1}", new Object[]{new Byte(this.allQop), new Integer(this.recvMaxBufSize)});
            }

            this.handshakeStage = 2;
            if (logger.isLoggable(Level.FINER)) {
               traceOutput(MY_CLASS_NAME, "doHandshake1", "KRB5SRV07:Challenge [raw]", var2);
            }

            byte[] var3 = this.secCtx.wrap(var2, 0, var2.length, new MessageProp(0, false));
            if (logger.isLoggable(Level.FINER)) {
               traceOutput(MY_CLASS_NAME, "doHandshake1", "KRB5SRV08:Challenge [after wrap]", var3);
            }

            return var3;
         }
      } catch (GSSException var4) {
         throw new SaslException("Problem wrapping handshake1", var4);
      }
   }

   private byte[] doHandshake2(byte[] var1) throws SaslException {
      try {
         byte[] var2 = this.secCtx.unwrap(var1, 0, var1.length, new MessageProp(0, false));
         if (logger.isLoggable(Level.FINER)) {
            traceOutput(MY_CLASS_NAME, "doHandshake2", "KRB5SRV09:Response [after unwrap]", var2);
         }

         byte var3 = var2[0];
         if ((var3 & this.allQop) == 0) {
            throw new SaslException("Client selected unsupported protection: " + var3);
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
               logger.log(Level.FINE, "KRB5SRV10:Selected protection: {0}; privacy: {1}; integrity: {2}", new Object[]{new Byte(var3), this.privacy, this.integrity});
               logger.log(Level.FINE, "KRB5SRV11:Client max recv size: {0}; server max send size: {1}; rawSendSize: {2}", new Object[]{new Integer(var4), new Integer(this.sendMaxBufSize), new Integer(this.rawSendSize)});
            }

            if (var2.length > 4) {
               try {
                  this.authzid = new String(var2, 4, var2.length - 4, "UTF-8");
               } catch (UnsupportedEncodingException var6) {
                  throw new SaslException("Cannot decode authzid", var6);
               }
            } else {
               this.authzid = this.peer;
            }

            logger.log(Level.FINE, (String)"KRB5SRV12:Authzid: {0}", (Object)this.authzid);
            AuthorizeCallback var5 = new AuthorizeCallback(this.peer, this.authzid);
            this.cbh.handle(new Callback[]{var5});
            if (var5.isAuthorized()) {
               this.authzid = var5.getAuthorizedID();
               this.completed = true;
               return null;
            } else {
               throw new SaslException(this.peer + " is not authorized to connect as " + this.authzid);
            }
         }
      } catch (GSSException var7) {
         throw new SaslException("Final handshake step failed", var7);
      } catch (IOException var8) {
         throw new SaslException("Problem with callback handler", var8);
      } catch (UnsupportedCallbackException var9) {
         throw new SaslException("Problem with callback handler", var9);
      }
   }

   public String getAuthorizationID() {
      if (this.completed) {
         return this.authzid;
      } else {
         throw new IllegalStateException("Authentication incomplete");
      }
   }

   public Object getNegotiatedProperty(String var1) {
      if (!this.completed) {
         throw new IllegalStateException("Authentication incomplete");
      } else {
         byte var4 = -1;
         switch(var1.hashCode()) {
         case 183461877:
            if (var1.equals("javax.security.sasl.bound.server.name")) {
               var4 = 0;
            }
         default:
            Object var2;
            switch(var4) {
            case 0:
               try {
                  var2 = this.me.split("[/@]")[1];
               } catch (Exception var6) {
                  var2 = null;
               }
               break;
            default:
               var2 = super.getNegotiatedProperty(var1);
            }

            return var2;
         }
      }
   }
}
