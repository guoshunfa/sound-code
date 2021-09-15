package com.sun.xml.internal.org.jvnet.mimepull;

final class ASCIIUtility {
   private ASCIIUtility() {
   }

   public static int parseInt(byte[] b, int start, int end, int radix) throws NumberFormatException {
      if (b == null) {
         throw new NumberFormatException("null");
      } else {
         int result = 0;
         boolean negative = false;
         int i = start;
         if (end > start) {
            int limit;
            if (b[start] == 45) {
               negative = true;
               limit = Integer.MIN_VALUE;
               i = start + 1;
            } else {
               limit = -2147483647;
            }

            int multmin = limit / radix;
            int digit;
            if (i < end) {
               digit = Character.digit((char)b[i++], radix);
               if (digit < 0) {
                  throw new NumberFormatException("illegal number: " + toString(b, start, end));
               }

               result = -digit;
            }

            while(i < end) {
               digit = Character.digit((char)b[i++], radix);
               if (digit < 0) {
                  throw new NumberFormatException("illegal number");
               }

               if (result < multmin) {
                  throw new NumberFormatException("illegal number");
               }

               result *= radix;
               if (result < limit + digit) {
                  throw new NumberFormatException("illegal number");
               }

               result -= digit;
            }

            if (negative) {
               if (i > start + 1) {
                  return result;
               } else {
                  throw new NumberFormatException("illegal number");
               }
            } else {
               return -result;
            }
         } else {
            throw new NumberFormatException("illegal number");
         }
      }
   }

   public static String toString(byte[] b, int start, int end) {
      int size = end - start;
      char[] theChars = new char[size];
      int i = 0;

      for(int var6 = start; i < size; theChars[i++] = (char)(b[var6++] & 255)) {
      }

      return new String(theChars);
   }
}
