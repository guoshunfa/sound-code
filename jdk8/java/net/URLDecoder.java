package java.net;

import java.io.UnsupportedEncodingException;

public class URLDecoder {
   static String dfltEncName;

   /** @deprecated */
   @Deprecated
   public static String decode(String var0) {
      String var1 = null;

      try {
         var1 = decode(var0, dfltEncName);
      } catch (UnsupportedEncodingException var3) {
      }

      return var1;
   }

   public static String decode(String var0, String var1) throws UnsupportedEncodingException {
      boolean var2 = false;
      int var3 = var0.length();
      StringBuffer var4 = new StringBuffer(var3 > 500 ? var3 / 2 : var3);
      int var5 = 0;
      if (var1.length() == 0) {
         throw new UnsupportedEncodingException("URLDecoder: empty string enc parameter");
      } else {
         byte[] var7 = null;

         while(true) {
            while(var5 < var3) {
               char var6 = var0.charAt(var5);
               switch(var6) {
               case '%':
                  try {
                     if (var7 == null) {
                        var7 = new byte[(var3 - var5) / 3];
                     }

                     int var8 = 0;

                     while(var5 + 2 < var3 && var6 == '%') {
                        int var9 = Integer.parseInt(var0.substring(var5 + 1, var5 + 3), 16);
                        if (var9 < 0) {
                           throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - negative value");
                        }

                        var7[var8++] = (byte)var9;
                        var5 += 3;
                        if (var5 < var3) {
                           var6 = var0.charAt(var5);
                        }
                     }

                     if (var5 < var3 && var6 == '%') {
                        throw new IllegalArgumentException("URLDecoder: Incomplete trailing escape (%) pattern");
                     }

                     var4.append(new String(var7, 0, var8, var1));
                  } catch (NumberFormatException var10) {
                     throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - " + var10.getMessage());
                  }

                  var2 = true;
                  break;
               case '+':
                  var4.append(' ');
                  ++var5;
                  var2 = true;
                  break;
               default:
                  var4.append(var6);
                  ++var5;
               }
            }

            return var2 ? var4.toString() : var0;
         }
      }
   }

   static {
      dfltEncName = URLEncoder.dfltEncName;
   }
}
