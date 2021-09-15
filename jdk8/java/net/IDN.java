package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.ParseException;
import sun.net.idn.Punycode;
import sun.net.idn.StringPrep;
import sun.text.normalizer.UCharacterIterator;

public final class IDN {
   public static final int ALLOW_UNASSIGNED = 1;
   public static final int USE_STD3_ASCII_RULES = 2;
   private static final String ACE_PREFIX = "xn--";
   private static final int ACE_PREFIX_LENGTH = "xn--".length();
   private static final int MAX_LABEL_LENGTH = 63;
   private static StringPrep namePrep = null;

   public static String toASCII(String var0, int var1) {
      int var2 = 0;
      boolean var3 = false;
      StringBuffer var4 = new StringBuffer();
      if (isRootLabel(var0)) {
         return ".";
      } else {
         int var5;
         for(; var2 < var0.length(); var2 = var5 + 1) {
            var5 = searchDots(var0, var2);
            var4.append(toASCIIInternal(var0.substring(var2, var5), var1));
            if (var5 != var0.length()) {
               var4.append('.');
            }
         }

         return var4.toString();
      }
   }

   public static String toASCII(String var0) {
      return toASCII(var0, 0);
   }

   public static String toUnicode(String var0, int var1) {
      int var2 = 0;
      boolean var3 = false;
      StringBuffer var4 = new StringBuffer();
      if (isRootLabel(var0)) {
         return ".";
      } else {
         int var5;
         for(; var2 < var0.length(); var2 = var5 + 1) {
            var5 = searchDots(var0, var2);
            var4.append(toUnicodeInternal(var0.substring(var2, var5), var1));
            if (var5 != var0.length()) {
               var4.append('.');
            }
         }

         return var4.toString();
      }
   }

   public static String toUnicode(String var0) {
      return toUnicode(var0, 0);
   }

   private IDN() {
   }

   private static String toASCIIInternal(String var0, int var1) {
      boolean var2 = isAllASCII(var0);
      StringBuffer var3;
      if (!var2) {
         UCharacterIterator var4 = UCharacterIterator.getInstance(var0);

         try {
            var3 = namePrep.prepare(var4, var1);
         } catch (ParseException var8) {
            throw new IllegalArgumentException(var8);
         }
      } else {
         var3 = new StringBuffer(var0);
      }

      if (var3.length() == 0) {
         throw new IllegalArgumentException("Empty label is not a legal name");
      } else {
         boolean var9 = (var1 & 2) != 0;
         if (var9) {
            int var5 = 0;

            while(true) {
               if (var5 >= var3.length()) {
                  if (var3.charAt(0) == '-' || var3.charAt(var3.length() - 1) == '-') {
                     throw new IllegalArgumentException("Has leading or trailing hyphen");
                  }
                  break;
               }

               char var6 = var3.charAt(var5);
               if (isNonLDHAsciiCodePoint(var6)) {
                  throw new IllegalArgumentException("Contains non-LDH ASCII characters");
               }

               ++var5;
            }
         }

         if (!var2 && !isAllASCII(var3.toString())) {
            if (startsWithACEPrefix(var3)) {
               throw new IllegalArgumentException("The input starts with the ACE Prefix");
            }

            try {
               var3 = Punycode.encode(var3, (boolean[])null);
            } catch (ParseException var7) {
               throw new IllegalArgumentException(var7);
            }

            var3 = toASCIILower(var3);
            var3.insert(0, (String)"xn--");
         }

         if (var3.length() > 63) {
            throw new IllegalArgumentException("The label in the input is too long");
         } else {
            return var3.toString();
         }
      }
   }

   private static String toUnicodeInternal(String var0, int var1) {
      Object var2 = null;
      boolean var4 = isAllASCII(var0);
      StringBuffer var3;
      if (!var4) {
         try {
            UCharacterIterator var5 = UCharacterIterator.getInstance(var0);
            var3 = namePrep.prepare(var5, var1);
         } catch (Exception var9) {
            return var0;
         }
      } else {
         var3 = new StringBuffer(var0);
      }

      if (startsWithACEPrefix(var3)) {
         String var10 = var3.substring(ACE_PREFIX_LENGTH, var3.length());

         try {
            StringBuffer var6 = Punycode.decode(new StringBuffer(var10), (boolean[])null);
            String var7 = toASCII(var6.toString(), var1);
            if (var7.equalsIgnoreCase(var3.toString())) {
               return var6.toString();
            }
         } catch (Exception var8) {
         }
      }

      return var0;
   }

   private static boolean isNonLDHAsciiCodePoint(int var0) {
      return 0 <= var0 && var0 <= 44 || 46 <= var0 && var0 <= 47 || 58 <= var0 && var0 <= 64 || 91 <= var0 && var0 <= 96 || 123 <= var0 && var0 <= 127;
   }

   private static int searchDots(String var0, int var1) {
      int var2;
      for(var2 = var1; var2 < var0.length() && !isLabelSeparator(var0.charAt(var2)); ++var2) {
      }

      return var2;
   }

   private static boolean isRootLabel(String var0) {
      return var0.length() == 1 && isLabelSeparator(var0.charAt(0));
   }

   private static boolean isLabelSeparator(char var0) {
      return var0 == '.' || var0 == 12290 || var0 == '．' || var0 == '｡';
   }

   private static boolean isAllASCII(String var0) {
      boolean var1 = true;

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         char var3 = var0.charAt(var2);
         if (var3 > 127) {
            var1 = false;
            break;
         }
      }

      return var1;
   }

   private static boolean startsWithACEPrefix(StringBuffer var0) {
      boolean var1 = true;
      if (var0.length() < ACE_PREFIX_LENGTH) {
         return false;
      } else {
         for(int var2 = 0; var2 < ACE_PREFIX_LENGTH; ++var2) {
            if (toASCIILower(var0.charAt(var2)) != "xn--".charAt(var2)) {
               var1 = false;
            }
         }

         return var1;
      }
   }

   private static char toASCIILower(char var0) {
      return 'A' <= var0 && var0 <= 'Z' ? (char)(var0 + 97 - 65) : var0;
   }

   private static StringBuffer toASCIILower(StringBuffer var0) {
      StringBuffer var1 = new StringBuffer();

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         var1.append(toASCIILower(var0.charAt(var2)));
      }

      return var1;
   }

   static {
      InputStream var0 = null;

      try {
         if (System.getSecurityManager() != null) {
            var0 = (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
               public InputStream run() {
                  return StringPrep.class.getResourceAsStream("uidna.spp");
               }
            });
         } else {
            var0 = StringPrep.class.getResourceAsStream("uidna.spp");
         }

         namePrep = new StringPrep(var0);
         var0.close();
      } catch (IOException var2) {
         assert false;
      }

   }
}
