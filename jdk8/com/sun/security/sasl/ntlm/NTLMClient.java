package com.sun.security.sasl.ntlm;

import com.sun.security.ntlm.Client;
import com.sun.security.ntlm.NTLMException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Random;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

final class NTLMClient implements SaslClient {
   private static final String NTLM_VERSION = "com.sun.security.sasl.ntlm.version";
   private static final String NTLM_RANDOM = "com.sun.security.sasl.ntlm.random";
   private static final String NTLM_DOMAIN = "com.sun.security.sasl.ntlm.domain";
   private static final String NTLM_HOSTNAME = "com.sun.security.sasl.ntlm.hostname";
   private final Client client;
   private final String mech;
   private final Random random;
   private int step = 0;

   NTLMClient(String var1, String var2, String var3, String var4, Map<String, ?> var5, CallbackHandler var6) throws SaslException {
      this.mech = var1;
      String var7 = null;
      Random var8 = null;
      String var9 = null;
      if (var5 != null) {
         String var10 = (String)var5.get("javax.security.sasl.qop");
         if (var10 != null && !var10.equals("auth")) {
            throw new SaslException("NTLM only support auth");
         }

         var7 = (String)var5.get("com.sun.security.sasl.ntlm.version");
         var8 = (Random)var5.get("com.sun.security.sasl.ntlm.random");
         var9 = (String)var5.get("com.sun.security.sasl.ntlm.hostname");
      }

      this.random = var8 != null ? var8 : new Random();
      if (var7 == null) {
         var7 = System.getProperty("ntlm.version");
      }

      RealmCallback var19 = var4 != null && !var4.isEmpty() ? new RealmCallback("Realm: ", var4) : new RealmCallback("Realm: ");
      NameCallback var11 = var2 != null && !var2.isEmpty() ? new NameCallback("User name: ", var2) : new NameCallback("User name: ");
      PasswordCallback var12 = new PasswordCallback("Password: ", false);

      try {
         var6.handle(new Callback[]{var19, var11, var12});
      } catch (UnsupportedCallbackException var17) {
         throw new SaslException("NTLM: Cannot perform callback to acquire realm, username or password", var17);
      } catch (IOException var18) {
         throw new SaslException("NTLM: Error acquiring realm, username or password", var18);
      }

      if (var9 == null) {
         try {
            var9 = InetAddress.getLocalHost().getCanonicalHostName();
         } catch (UnknownHostException var16) {
            var9 = "localhost";
         }
      }

      try {
         String var13 = var11.getName();
         if (var13 == null) {
            var13 = var2;
         }

         String var14 = var19.getText();
         if (var14 == null) {
            var14 = var4;
         }

         this.client = new Client(var7, var9, var13, var14, var12.getPassword());
      } catch (NTLMException var15) {
         throw new SaslException("NTLM: client creation failure", var15);
      }
   }

   public String getMechanismName() {
      return this.mech;
   }

   public boolean isComplete() {
      return this.step >= 2;
   }

   public byte[] unwrap(byte[] var1, int var2, int var3) throws SaslException {
      throw new IllegalStateException("Not supported.");
   }

   public byte[] wrap(byte[] var1, int var2, int var3) throws SaslException {
      throw new IllegalStateException("Not supported.");
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
         case 11529379:
            if (var1.equals("com.sun.security.sasl.ntlm.domain")) {
               var3 = 1;
            }
         }

         switch(var3) {
         case 0:
            return "auth";
         case 1:
            return this.client.getDomain();
         default:
            return null;
         }
      }
   }

   public void dispose() throws SaslException {
      this.client.dispose();
   }

   public boolean hasInitialResponse() {
      return true;
   }

   public byte[] evaluateChallenge(byte[] var1) throws SaslException {
      ++this.step;
      if (this.step == 1) {
         return this.client.type1();
      } else {
         try {
            byte[] var2 = new byte[8];
            this.random.nextBytes(var2);
            return this.client.type3(var1, var2);
         } catch (NTLMException var3) {
            throw new SaslException("Type3 creation failed", var3);
         }
      }
   }
}
