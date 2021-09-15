package com.sun.jndi.toolkit.url;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;

public final class UrlUtil {
   private UrlUtil() {
   }

   public static final String decode(String var0) throws MalformedURLException {
      try {
         return decode(var0, "8859_1");
      } catch (UnsupportedEncodingException var2) {
         throw new MalformedURLException("ISO-Latin-1 decoder unavailable");
      }
   }

   public static final String decode(String var0, String var1) throws MalformedURLException, UnsupportedEncodingException {
      try {
         return URLDecoder.decode(var0, var1);
      } catch (IllegalArgumentException var4) {
         MalformedURLException var3 = new MalformedURLException("Invalid URI encoding: " + var0);
         var3.initCause(var4);
         throw var3;
      }
   }

   public static final String encode(String var0, String var1) throws UnsupportedEncodingException {
      byte[] var2 = var0.getBytes(var1);
      int var3 = var2.length;
      char[] var5 = new char[3 * var3];
      int var6 = 0;

      for(int var7 = 0; var7 < var3; ++var7) {
         if ((var2[var7] < 97 || var2[var7] > 122) && (var2[var7] < 65 || var2[var7] > 90) && (var2[var7] < 48 || var2[var7] > 57) && "=,+;.'-@&/$_()!~*:".indexOf(var2[var7]) < 0) {
            var5[var6++] = '%';
            var5[var6++] = Character.forDigit(15 & var2[var7] >>> 4, 16);
            var5[var6++] = Character.forDigit(15 & var2[var7], 16);
         } else {
            var5[var6++] = (char)var2[var7];
         }
      }

      return new String(var5, 0, var6);
   }
}
