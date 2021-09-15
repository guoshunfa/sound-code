package com.sun.security.sasl;

import com.sun.security.sasl.util.PolicyUtils;
import java.io.IOException;
import java.util.Map;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;

public final class ClientFactoryImpl implements SaslClientFactory {
   private static final String[] myMechs = new String[]{"EXTERNAL", "CRAM-MD5", "PLAIN"};
   private static final int[] mechPolicies = new int[]{7, 17, 16};
   private static final int EXTERNAL = 0;
   private static final int CRAMMD5 = 1;
   private static final int PLAIN = 2;

   public SaslClient createSaslClient(String[] var1, String var2, String var3, String var4, Map<String, ?> var5, CallbackHandler var6) throws SaslException {
      for(int var7 = 0; var7 < var1.length; ++var7) {
         if (var1[var7].equals(myMechs[0]) && PolicyUtils.checkPolicy(mechPolicies[0], var5)) {
            return new ExternalClient(var2);
         }

         Object[] var8;
         if (var1[var7].equals(myMechs[1]) && PolicyUtils.checkPolicy(mechPolicies[1], var5)) {
            var8 = this.getUserInfo("CRAM-MD5", var2, var6);
            return new CramMD5Client((String)var8[0], (byte[])((byte[])var8[1]));
         }

         if (var1[var7].equals(myMechs[2]) && PolicyUtils.checkPolicy(mechPolicies[2], var5)) {
            var8 = this.getUserInfo("PLAIN", var2, var6);
            return new PlainClient(var2, (String)var8[0], (byte[])((byte[])var8[1]));
         }
      }

      return null;
   }

   public String[] getMechanismNames(Map<String, ?> var1) {
      return PolicyUtils.filterMechs(myMechs, mechPolicies, var1);
   }

   private Object[] getUserInfo(String var1, String var2, CallbackHandler var3) throws SaslException {
      if (var3 == null) {
         throw new SaslException("Callback handler to get username/password required");
      } else {
         try {
            String var4 = var1 + " authentication id: ";
            String var5 = var1 + " password: ";
            NameCallback var6 = var2 == null ? new NameCallback(var4) : new NameCallback(var4, var2);
            PasswordCallback var7 = new PasswordCallback(var5, false);
            var3.handle(new Callback[]{var6, var7});
            char[] var8 = var7.getPassword();
            byte[] var9;
            if (var8 != null) {
               var9 = (new String(var8)).getBytes("UTF8");
               var7.clearPassword();
            } else {
               var9 = null;
            }

            String var10 = var6.getName();
            return new Object[]{var10, var9};
         } catch (IOException var11) {
            throw new SaslException("Cannot get password", var11);
         } catch (UnsupportedCallbackException var12) {
            throw new SaslException("Cannot get userid/password", var12);
         }
      }
   }
}
