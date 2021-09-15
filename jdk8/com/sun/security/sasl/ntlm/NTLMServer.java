package com.sun.security.sasl.ntlm;

import com.sun.security.ntlm.NTLMException;
import com.sun.security.ntlm.Server;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

final class NTLMServer implements SaslServer {
   private static final String NTLM_VERSION = "com.sun.security.sasl.ntlm.version";
   private static final String NTLM_DOMAIN = "com.sun.security.sasl.ntlm.domain";
   private static final String NTLM_HOSTNAME = "com.sun.security.sasl.ntlm.hostname";
   private static final String NTLM_RANDOM = "com.sun.security.sasl.ntlm.random";
   private final Random random;
   private final Server server;
   private byte[] nonce;
   private int step = 0;
   private String authzId;
   private final String mech;
   private String hostname;
   private String target;

   NTLMServer(String var1, String var2, String var3, Map<String, ?> var4, final CallbackHandler var5) throws SaslException {
      this.mech = var1;
      String var6 = null;
      String var7 = null;
      Random var8 = null;
      if (var4 != null) {
         var7 = (String)var4.get("com.sun.security.sasl.ntlm.domain");
         var6 = (String)var4.get("com.sun.security.sasl.ntlm.version");
         var8 = (Random)var4.get("com.sun.security.sasl.ntlm.random");
      }

      this.random = var8 != null ? var8 : new Random();
      if (var6 == null) {
         var6 = System.getProperty("ntlm.version");
      }

      if (var7 == null) {
         var7 = var3;
      }

      if (var7 == null) {
         throw new SaslException("Domain must be provided as the serverName argument or in props");
      } else {
         try {
            this.server = new Server(var6, var7) {
               public char[] getPassword(String var1, String var2) {
                  try {
                     RealmCallback var3 = var1 != null && !var1.isEmpty() ? new RealmCallback("Domain: ", var1) : new RealmCallback("Domain: ");
                     NameCallback var4 = new NameCallback("Name: ", var2);
                     PasswordCallback var5x = new PasswordCallback("Password: ", false);
                     var5.handle(new Callback[]{var3, var4, var5x});
                     char[] var6 = var5x.getPassword();
                     var5x.clearPassword();
                     return var6;
                  } catch (IOException var7) {
                     return null;
                  } catch (UnsupportedCallbackException var8) {
                     return null;
                  }
               }
            };
         } catch (NTLMException var10) {
            throw new SaslException("NTLM: server creation failure", var10);
         }

         this.nonce = new byte[8];
      }
   }

   public String getMechanismName() {
      return this.mech;
   }

   public byte[] evaluateResponse(byte[] var1) throws SaslException {
      try {
         ++this.step;
         if (this.step == 1) {
            this.random.nextBytes(this.nonce);
            return this.server.type2(var1, this.nonce);
         } else {
            String[] var2 = this.server.verify(var1, this.nonce);
            this.authzId = var2[0];
            this.hostname = var2[1];
            this.target = var2[2];
            return null;
         }
      } catch (NTLMException var3) {
         throw new SaslException("NTLM: generate response failure", var3);
      }
   }

   public boolean isComplete() {
      return this.step >= 2;
   }

   public String getAuthorizationID() {
      if (!this.isComplete()) {
         throw new IllegalStateException("authentication not complete");
      } else {
         return this.authzId;
      }
   }

   public byte[] unwrap(byte[] var1, int var2, int var3) throws SaslException {
      throw new IllegalStateException("Not supported yet.");
   }

   public byte[] wrap(byte[] var1, int var2, int var3) throws SaslException {
      throw new IllegalStateException("Not supported yet.");
   }

   public Object getNegotiatedProperty(String var1) {
      if (!this.isComplete()) {
         throw new IllegalStateException("authentication not complete");
      } else {
         byte var3 = -1;
         switch(var1.hashCode()) {
         case -1548608927:
            if (var1.equals("javax.security.sasl.qop")) {
               var3 = 0;
            }
            break;
         case 183461877:
            if (var1.equals("javax.security.sasl.bound.server.name")) {
               var3 = 1;
            }
            break;
         case 1060567122:
            if (var1.equals("com.sun.security.sasl.ntlm.hostname")) {
               var3 = 2;
            }
         }

         switch(var3) {
         case 0:
            return "auth";
         case 1:
            return this.target;
         case 2:
            return this.hostname;
         default:
            return null;
         }
      }
   }

   public void dispose() throws SaslException {
   }
}
