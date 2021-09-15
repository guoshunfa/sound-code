package com.sun.security.sasl.util;

import java.util.Map;

public final class PolicyUtils {
   public static final int NOPLAINTEXT = 1;
   public static final int NOACTIVE = 2;
   public static final int NODICTIONARY = 4;
   public static final int FORWARD_SECRECY = 8;
   public static final int NOANONYMOUS = 16;
   public static final int PASS_CREDENTIALS = 512;

   private PolicyUtils() {
   }

   public static boolean checkPolicy(int var0, Map<String, ?> var1) {
      if (var1 == null) {
         return true;
      } else if ("true".equalsIgnoreCase((String)var1.get("javax.security.sasl.policy.noplaintext")) && (var0 & 1) == 0) {
         return false;
      } else if ("true".equalsIgnoreCase((String)var1.get("javax.security.sasl.policy.noactive")) && (var0 & 2) == 0) {
         return false;
      } else if ("true".equalsIgnoreCase((String)var1.get("javax.security.sasl.policy.nodictionary")) && (var0 & 4) == 0) {
         return false;
      } else if ("true".equalsIgnoreCase((String)var1.get("javax.security.sasl.policy.noanonymous")) && (var0 & 16) == 0) {
         return false;
      } else if ("true".equalsIgnoreCase((String)var1.get("javax.security.sasl.policy.forward")) && (var0 & 8) == 0) {
         return false;
      } else {
         return !"true".equalsIgnoreCase((String)var1.get("javax.security.sasl.policy.credentials")) || (var0 & 512) != 0;
      }
   }

   public static String[] filterMechs(String[] var0, int[] var1, Map<String, ?> var2) {
      if (var2 == null) {
         return (String[])var0.clone();
      } else {
         boolean[] var3 = new boolean[var0.length];
         int var4 = 0;

         for(int var5 = 0; var5 < var0.length; ++var5) {
            if (var3[var5] = checkPolicy(var1[var5], var2)) {
               ++var4;
            }
         }

         String[] var8 = new String[var4];
         int var6 = 0;

         for(int var7 = 0; var6 < var0.length; ++var6) {
            if (var3[var6]) {
               var8[var7++] = var0[var6];
            }
         }

         return var8;
      }
   }
}
