package com.sun.security.sasl;

import com.sun.security.sasl.util.PolicyUtils;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;

public final class ServerFactoryImpl implements SaslServerFactory {
   private static final String[] myMechs = new String[]{"CRAM-MD5"};
   private static final int[] mechPolicies = new int[]{17};
   private static final int CRAMMD5 = 0;

   public SaslServer createSaslServer(String var1, String var2, String var3, Map<String, ?> var4, CallbackHandler var5) throws SaslException {
      if (var1.equals(myMechs[0]) && PolicyUtils.checkPolicy(mechPolicies[0], var4)) {
         if (var5 == null) {
            throw new SaslException("Callback handler with support for AuthorizeCallback required");
         } else {
            return new CramMD5Server(var2, var3, var4, var5);
         }
      } else {
         return null;
      }
   }

   public String[] getMechanismNames(Map<String, ?> var1) {
      return PolicyUtils.filterMechs(myMechs, mechPolicies, var1);
   }
}
