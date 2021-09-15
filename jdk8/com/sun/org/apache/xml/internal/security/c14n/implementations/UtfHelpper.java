package com.sun.org.apache.xml.internal.security.c14n.implementations;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class UtfHelpper {
   static final void writeByte(String var0, OutputStream var1, Map<String, byte[]> var2) throws IOException {
      byte[] var3 = (byte[])var2.get(var0);
      if (var3 == null) {
         var3 = getStringInUtf8(var0);
         var2.put(var0, var3);
      }

      var1.write(var3);
   }

   static final void writeCharToUtf8(char var0, OutputStream var1) throws IOException {
      if (var0 < 128) {
         var1.write(var0);
      } else if ((var0 < '\ud800' || var0 > '\udbff') && (var0 < '\udc00' || var0 > '\udfff')) {
         byte var2;
         int var3;
         char var4;
         if (var0 > 2047) {
            var4 = (char)(var0 >>> 12);
            var3 = 224;
            if (var4 > 0) {
               var3 |= var4 & 15;
            }

            var1.write(var3);
            var3 = 128;
            var2 = 63;
         } else {
            var3 = 192;
            var2 = 31;
         }

         var4 = (char)(var0 >>> 6);
         if (var4 > 0) {
            var3 |= var4 & var2;
         }

         var1.write(var3);
         var1.write(128 | var0 & 63);
      } else {
         var1.write(63);
      }
   }

   static final void writeStringToUtf8(String var0, OutputStream var1) throws IOException {
      int var2 = var0.length();
      int var3 = 0;

      while(true) {
         while(var3 < var2) {
            char var4 = var0.charAt(var3++);
            if (var4 < 128) {
               var1.write(var4);
            } else if ((var4 < '\ud800' || var4 > '\udbff') && (var4 < '\udc00' || var4 > '\udfff')) {
               char var5;
               byte var6;
               int var7;
               if (var4 > 2047) {
                  var5 = (char)(var4 >>> 12);
                  var7 = 224;
                  if (var5 > 0) {
                     var7 |= var5 & 15;
                  }

                  var1.write(var7);
                  var7 = 128;
                  var6 = 63;
               } else {
                  var7 = 192;
                  var6 = 31;
               }

               var5 = (char)(var4 >>> 6);
               if (var5 > 0) {
                  var7 |= var5 & var6;
               }

               var1.write(var7);
               var1.write(128 | var4 & 63);
            } else {
               var1.write(63);
            }
         }

         return;
      }
   }

   public static final byte[] getStringInUtf8(String var0) {
      int var1 = var0.length();
      boolean var2 = false;
      byte[] var3 = new byte[var1];
      int var4 = 0;
      int var5 = 0;

      while(true) {
         byte[] var7;
         while(var4 < var1) {
            char var6 = var0.charAt(var4++);
            if (var6 < 128) {
               var3[var5++] = (byte)var6;
            } else if (var6 >= '\ud800' && var6 <= '\udbff' || var6 >= '\udc00' && var6 <= '\udfff') {
               var3[var5++] = 63;
            } else {
               if (!var2) {
                  var7 = new byte[3 * var1];
                  System.arraycopy(var3, 0, var7, 0, var5);
                  var3 = var7;
                  var2 = true;
               }

               byte var8;
               byte var9;
               char var10;
               if (var6 > 2047) {
                  var10 = (char)(var6 >>> 12);
                  var9 = -32;
                  if (var10 > 0) {
                     var9 = (byte)(var9 | var10 & 15);
                  }

                  var3[var5++] = var9;
                  var9 = -128;
                  var8 = 63;
               } else {
                  var9 = -64;
                  var8 = 31;
               }

               var10 = (char)(var6 >>> 6);
               if (var10 > 0) {
                  var9 = (byte)(var9 | var10 & var8);
               }

               var3[var5++] = var9;
               var3[var5++] = (byte)(128 | var6 & 63);
            }
         }

         if (var2) {
            var7 = new byte[var5];
            System.arraycopy(var3, 0, var7, 0, var5);
            var3 = var7;
         }

         return var3;
      }
   }
}
