package com.sun.security.sasl.ntlm;

import com.sun.security.sasl.util.PolicyUtils;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;

public final class FactoryImpl implements SaslClientFactory, SaslServerFactory {
   private static final String[] myMechs = new String[]{"NTLM"};
   private static final int[] mechPolicies = new int[]{17};

   public SaslClient createSaslClient(String[] var1, String var2, String var3, String var4, Map<String, ?> var5, CallbackHandler var6) throws SaslException {
      for(int var7 = 0; var7 < var1.length; ++var7) {
         if (var1[var7].equals("NTLM") && PolicyUtils.checkPolicy(mechPolicies[0], var5)) {
            if (var6 == null) {
               throw new SaslException("Callback handler with support for RealmCallback, NameCallback, and PasswordCallback required");
            }

            return new NTLMClient(var1[var7], var2, var3, var4, var5, var6);
         }
      }

      return null;
   }

   public SaslServer createSaslServer(String var1, String var2, String var3, Map<String, ?> var4, CallbackHandler var5) throws SaslException {
      if (var1.equals("NTLM") && PolicyUtils.checkPolicy(mechPolicies[0], var4)) {
         if (var4 != null) {
            String var6 = (String)var4.get("javax.security.sasl.qop");
            if (var6 != null && !var6.equals("auth")) {
               throw new SaslException("NTLM only support auth");
            }
         }

         if (var5 == null) {
            throw new SaslException("Callback handler with support for RealmCallback, NameCallback, and PasswordCallback required");
         } else {
            return new NTLMServer(var1, var2, var3, var4, var5);
         }
      } else {
         return null;
      }
   }

   public String[] getMechanismNames(Map<String, ?> var1) {
      return PolicyUtils.filterMechs(myMechs, mechPolicies, var1);
   }
}
