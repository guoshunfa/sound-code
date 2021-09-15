package com.sun.jndi.ldap.sasl;

import com.sun.jndi.ldap.Connection;
import com.sun.jndi.ldap.LdapClient;
import com.sun.jndi.ldap.LdapResult;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public final class LdapSasl {
   private static final String SASL_CALLBACK = "java.naming.security.sasl.callback";
   private static final String SASL_AUTHZ_ID = "java.naming.security.sasl.authorizationId";
   private static final String SASL_REALM = "java.naming.security.sasl.realm";
   private static final int LDAP_SUCCESS = 0;
   private static final int LDAP_SASL_BIND_IN_PROGRESS = 14;
   private static final byte[] NO_BYTES = new byte[0];

   private LdapSasl() {
   }

   public static LdapResult saslBind(LdapClient var0, Connection var1, String var2, String var3, Object var4, String var5, Hashtable<?, ?> var6, Control[] var7) throws IOException, NamingException {
      SaslClient var8 = null;
      boolean var9 = false;
      Object var10 = var6 != null ? (CallbackHandler)var6.get("java.naming.security.sasl.callback") : null;
      if (var10 == null) {
         var10 = new DefaultCallbackHandler(var3, var4, (String)var6.get("java.naming.security.sasl.realm"));
         var9 = true;
      }

      String var11 = var6 != null ? (String)var6.get("java.naming.security.sasl.authorizationId") : null;
      String[] var12 = getSaslMechanismNames(var5);

      LdapResult var25;
      try {
         var8 = Sasl.createSaslClient(var12, var11, "ldap", var2, var6, (CallbackHandler)var10);
         if (var8 == null) {
            throw new AuthenticationNotSupportedException(var5);
         }

         String var24 = var8.getMechanismName();
         byte[] var15 = var8.hasInitialResponse() ? var8.evaluateChallenge(NO_BYTES) : null;

         LdapResult var13;
         for(var13 = var0.ldapBind((String)null, var15, var7, var24, true); !var8.isComplete() && (var13.status == 14 || var13.status == 0); var13 = var0.ldapBind((String)null, var15, var7, var24, true)) {
            var15 = var8.evaluateChallenge(var13.serverCreds != null ? var13.serverCreds : NO_BYTES);
            if (var13.status == 0) {
               if (var15 != null) {
                  throw new AuthenticationException("SASL client generated response after success");
               }
               break;
            }
         }

         if (var13.status == 0) {
            if (!var8.isComplete()) {
               throw new AuthenticationException("SASL authentication not complete despite server claims");
            }

            String var16 = (String)var8.getNegotiatedProperty("javax.security.sasl.qop");
            if (var16 == null || !var16.equalsIgnoreCase("auth-int") && !var16.equalsIgnoreCase("auth-conf")) {
               var8.dispose();
            } else {
               SaslInputStream var17 = new SaslInputStream(var8, var1.inStream);
               SaslOutputStream var18 = new SaslOutputStream(var8, var1.outStream);
               var1.replaceStreams(var17, var18);
            }
         }

         var25 = var13;
      } catch (SaslException var22) {
         AuthenticationException var14 = new AuthenticationException(var5);
         var14.setRootCause(var22);
         throw var14;
      } finally {
         if (var9) {
            ((DefaultCallbackHandler)var10).clearPassword();
         }

      }

      return var25;
   }

   private static String[] getSaslMechanismNames(String var0) {
      StringTokenizer var1 = new StringTokenizer(var0);
      Vector var2 = new Vector(10);

      while(var1.hasMoreTokens()) {
         var2.addElement(var1.nextToken());
      }

      String[] var3 = new String[var2.size()];

      for(int var4 = 0; var4 < var2.size(); ++var4) {
         var3[var4] = (String)var2.elementAt(var4);
      }

      return var3;
   }
}
