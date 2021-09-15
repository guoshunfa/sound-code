package com.sun.xml.internal.messaging.saaj.util;

public class ParseUtil {
   private static char unescape(String s, int i) {
      return (char)Integer.parseInt(s.substring(i + 1, i + 3), 16);
   }

   public static String decode(String s) {
      StringBuffer sb = new StringBuffer();

      char c;
      for(int i = 0; i < s.length(); sb.append(c)) {
         c = s.charAt(i);
         if (c != '%') {
            ++i;
         } else {
            try {
               c = unescape(s, i);
               i += 3;
               if ((c & 128) != 0) {
                  char c2;
                  switch(c >> 4) {
                  case 12:
                  case 13:
                     c2 = unescape(s, i);
                     i += 3;
                     c = (char)((c & 31) << 6 | c2 & 63);
                     break;
                  case 14:
                     c2 = unescape(s, i);
                     i += 3;
                     char c3 = unescape(s, i);
                     i += 3;
                     c = (char)((c & 15) << 12 | (c2 & 63) << 6 | c3 & 63);
                     break;
                  default:
                     throw new IllegalArgumentException();
                  }
               }
            } catch (NumberFormatException var7) {
               throw new IllegalArgumentException();
            }
         }
      }

      return sb.toString();
   }
}
