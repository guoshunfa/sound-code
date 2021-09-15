package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpDefinitions;

public class SnmpTools implements SnmpDefinitions {
   public static String binary2ascii(byte[] var0, int var1) {
      if (var0 == null) {
         return null;
      } else {
         int var2 = var1 * 2 + 2;
         byte[] var3 = new byte[var2];
         var3[0] = 48;
         var3[1] = 120;

         for(int var4 = 0; var4 < var1; ++var4) {
            int var5 = var4 * 2;
            int var6 = var0[var4] & 240;
            var6 >>= 4;
            if (var6 < 10) {
               var3[var5 + 2] = (byte)(48 + var6);
            } else {
               var3[var5 + 2] = (byte)(65 + (var6 - 10));
            }

            var6 = var0[var4] & 15;
            if (var6 < 10) {
               var3[var5 + 1 + 2] = (byte)(48 + var6);
            } else {
               var3[var5 + 1 + 2] = (byte)(65 + (var6 - 10));
            }
         }

         return new String(var3);
      }
   }

   public static String binary2ascii(byte[] var0) {
      return binary2ascii(var0, var0.length);
   }

   public static byte[] ascii2binary(String var0) {
      if (var0 == null) {
         return null;
      } else {
         String var1 = var0.substring(2);
         int var2 = var1.length();
         byte[] var3 = new byte[var2 / 2];
         byte[] var4 = var1.getBytes();

         for(int var5 = 0; var5 < var2 / 2; ++var5) {
            int var6 = var5 * 2;
            boolean var7 = false;
            byte var8;
            if (var4[var6] >= 48 && var4[var6] <= 57) {
               var8 = (byte)(var4[var6] - 48 << 4);
            } else if (var4[var6] >= 97 && var4[var6] <= 102) {
               var8 = (byte)(var4[var6] - 97 + 10 << 4);
            } else {
               if (var4[var6] < 65 || var4[var6] > 70) {
                  throw new Error("BAD format :" + var0);
               }

               var8 = (byte)(var4[var6] - 65 + 10 << 4);
            }

            if (var4[var6 + 1] >= 48 && var4[var6 + 1] <= 57) {
               var8 = (byte)(var8 + (var4[var6 + 1] - 48));
            } else if (var4[var6 + 1] >= 97 && var4[var6 + 1] <= 102) {
               var8 = (byte)(var8 + var4[var6 + 1] - 97 + 10);
            } else {
               if (var4[var6 + 1] < 65 || var4[var6 + 1] > 70) {
                  throw new Error("BAD format :" + var0);
               }

               var8 = (byte)(var8 + var4[var6 + 1] - 65 + 10);
            }

            var3[var5] = var8;
         }

         return var3;
      }
   }
}
