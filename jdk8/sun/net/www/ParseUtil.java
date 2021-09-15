package sun.net.www;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.BitSet;
import sun.nio.cs.ThreadLocalCoders;

public class ParseUtil {
   static BitSet encodedInPath = new BitSet(256);
   private static final char[] hexDigits;
   private static final long L_DIGIT;
   private static final long H_DIGIT = 0L;
   private static final long L_HEX;
   private static final long H_HEX;
   private static final long L_UPALPHA = 0L;
   private static final long H_UPALPHA;
   private static final long L_LOWALPHA = 0L;
   private static final long H_LOWALPHA;
   private static final long L_ALPHA = 0L;
   private static final long H_ALPHA;
   private static final long L_ALPHANUM;
   private static final long H_ALPHANUM;
   private static final long L_MARK;
   private static final long H_MARK;
   private static final long L_UNRESERVED;
   private static final long H_UNRESERVED;
   private static final long L_RESERVED;
   private static final long H_RESERVED;
   private static final long L_ESCAPED = 1L;
   private static final long H_ESCAPED = 0L;
   private static final long L_DASH;
   private static final long H_DASH;
   private static final long L_URIC;
   private static final long H_URIC;
   private static final long L_PCHAR;
   private static final long H_PCHAR;
   private static final long L_PATH;
   private static final long H_PATH;
   private static final long L_USERINFO;
   private static final long H_USERINFO;
   private static final long L_REG_NAME;
   private static final long H_REG_NAME;
   private static final long L_SERVER;
   private static final long H_SERVER;

   public static String encodePath(String var0) {
      return encodePath(var0, true);
   }

   public static String encodePath(String var0, boolean var1) {
      char[] var2 = new char[var0.length() * 2 + 16];
      int var3 = 0;
      char[] var4 = var0.toCharArray();
      int var5 = var0.length();

      for(int var6 = 0; var6 < var5; ++var6) {
         char var7 = var4[var6];
         if (!var1 && var7 == '/' || var1 && var7 == File.separatorChar) {
            var2[var3++] = '/';
         } else if (var7 > 127) {
            if (var7 > 2047) {
               var3 = escape(var2, (char)(224 | var7 >> 12 & 15), var3);
               var3 = escape(var2, (char)(128 | var7 >> 6 & 63), var3);
               var3 = escape(var2, (char)(128 | var7 >> 0 & 63), var3);
            } else {
               var3 = escape(var2, (char)(192 | var7 >> 6 & 31), var3);
               var3 = escape(var2, (char)(128 | var7 >> 0 & 63), var3);
            }
         } else if (var7 >= 'a' && var7 <= 'z' || var7 >= 'A' && var7 <= 'Z' || var7 >= '0' && var7 <= '9') {
            var2[var3++] = var7;
         } else if (encodedInPath.get(var7)) {
            var3 = escape(var2, var7, var3);
         } else {
            var2[var3++] = var7;
         }

         if (var3 + 9 > var2.length) {
            int var8 = var2.length * 2 + 16;
            if (var8 < 0) {
               var8 = Integer.MAX_VALUE;
            }

            char[] var9 = new char[var8];
            System.arraycopy(var2, 0, var9, 0, var3);
            var2 = var9;
         }
      }

      return new String(var2, 0, var3);
   }

   private static int escape(char[] var0, char var1, int var2) {
      var0[var2++] = '%';
      var0[var2++] = Character.forDigit(var1 >> 4 & 15, 16);
      var0[var2++] = Character.forDigit(var1 & 15, 16);
      return var2;
   }

   private static byte unescape(String var0, int var1) {
      return (byte)Integer.parseInt(var0.substring(var1 + 1, var1 + 3), 16);
   }

   public static String decode(String var0) {
      int var1 = var0.length();
      if (var1 != 0 && var0.indexOf(37) >= 0) {
         StringBuilder var2 = new StringBuilder(var1);
         ByteBuffer var3 = ByteBuffer.allocate(var1);
         CharBuffer var4 = CharBuffer.allocate(var1);
         CharsetDecoder var5 = ThreadLocalCoders.decoderFor("UTF-8").onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
         char var6 = var0.charAt(0);
         int var7 = 0;

         while(var7 < var1) {
            assert var6 == var0.charAt(var7);

            if (var6 != '%') {
               var2.append(var6);
               ++var7;
               if (var7 >= var1) {
                  break;
               }

               var6 = var0.charAt(var7);
            } else {
               var3.clear();

               do {
                  assert var1 - var7 >= 2;

                  try {
                     var3.put(unescape(var0, var7));
                  } catch (NumberFormatException var10) {
                     throw new IllegalArgumentException();
                  }

                  var7 += 3;
                  if (var7 >= var1) {
                     break;
                  }

                  var6 = var0.charAt(var7);
               } while(var6 == '%');

               var3.flip();
               var4.clear();
               var5.reset();
               CoderResult var9 = var5.decode(var3, var4, true);
               if (var9.isError()) {
                  throw new IllegalArgumentException("Error decoding percent encoded characters");
               }

               var9 = var5.flush(var4);
               if (var9.isError()) {
                  throw new IllegalArgumentException("Error decoding percent encoded characters");
               }

               var2.append(var4.flip().toString());
            }
         }

         return var2.toString();
      } else {
         return var0;
      }
   }

   public String canonizeString(String var1) {
      boolean var2 = false;
      int var3 = var1.length();

      int var4;
      while((var4 = var1.indexOf("/../")) >= 0) {
         if ((var3 = var1.lastIndexOf(47, var4 - 1)) >= 0) {
            var1 = var1.substring(0, var3) + var1.substring(var4 + 3);
         } else {
            var1 = var1.substring(var4 + 3);
         }
      }

      while((var4 = var1.indexOf("/./")) >= 0) {
         var1 = var1.substring(0, var4) + var1.substring(var4 + 2);
      }

      while(var1.endsWith("/..")) {
         var4 = var1.indexOf("/..");
         if ((var3 = var1.lastIndexOf(47, var4 - 1)) >= 0) {
            var1 = var1.substring(0, var3 + 1);
         } else {
            var1 = var1.substring(0, var4);
         }
      }

      if (var1.endsWith("/.")) {
         var1 = var1.substring(0, var1.length() - 1);
      }

      return var1;
   }

   public static URL fileToEncodedURL(File var0) throws MalformedURLException {
      String var1 = var0.getAbsolutePath();
      var1 = encodePath(var1);
      if (!var1.startsWith("/")) {
         var1 = "/" + var1;
      }

      if (!var1.endsWith("/") && var0.isDirectory()) {
         var1 = var1 + "/";
      }

      return new URL("file", "", var1);
   }

   public static URI toURI(URL var0) {
      String var1 = var0.getProtocol();
      String var2 = var0.getAuthority();
      String var3 = var0.getPath();
      String var4 = var0.getQuery();
      String var5 = var0.getRef();
      if (var3 != null && !var3.startsWith("/")) {
         var3 = "/" + var3;
      }

      if (var2 != null && var2.endsWith(":-1")) {
         var2 = var2.substring(0, var2.length() - 3);
      }

      URI var6;
      try {
         var6 = createURI(var1, var2, var3, var4, var5);
      } catch (URISyntaxException var8) {
         var6 = null;
      }

      return var6;
   }

   private static URI createURI(String var0, String var1, String var2, String var3, String var4) throws URISyntaxException {
      String var5 = toString(var0, (String)null, var1, (String)null, (String)null, -1, var2, var3, var4);
      checkPath(var5, var0, var2);
      return new URI(var5);
   }

   private static String toString(String var0, String var1, String var2, String var3, String var4, int var5, String var6, String var7, String var8) {
      StringBuffer var9 = new StringBuffer();
      if (var0 != null) {
         var9.append(var0);
         var9.append(':');
      }

      appendSchemeSpecificPart(var9, var1, var2, var3, var4, var5, var6, var7);
      appendFragment(var9, var8);
      return var9.toString();
   }

   private static void appendSchemeSpecificPart(StringBuffer var0, String var1, String var2, String var3, String var4, int var5, String var6, String var7) {
      if (var1 != null) {
         if (var1.startsWith("//[")) {
            int var8 = var1.indexOf("]");
            if (var8 != -1 && var1.indexOf(":") != -1) {
               String var9;
               String var10;
               if (var8 == var1.length()) {
                  var10 = var1;
                  var9 = "";
               } else {
                  var10 = var1.substring(0, var8 + 1);
                  var9 = var1.substring(var8 + 1);
               }

               var0.append(var10);
               var0.append(quote(var9, L_URIC, H_URIC));
            }
         } else {
            var0.append(quote(var1, L_URIC, H_URIC));
         }
      } else {
         appendAuthority(var0, var2, var3, var4, var5);
         if (var6 != null) {
            var0.append(quote(var6, L_PATH, H_PATH));
         }

         if (var7 != null) {
            var0.append('?');
            var0.append(quote(var7, L_URIC, H_URIC));
         }
      }

   }

   private static void appendAuthority(StringBuffer var0, String var1, String var2, String var3, int var4) {
      if (var3 != null) {
         var0.append("//");
         if (var2 != null) {
            var0.append(quote(var2, L_USERINFO, H_USERINFO));
            var0.append('@');
         }

         boolean var5 = var3.indexOf(58) >= 0 && !var3.startsWith("[") && !var3.endsWith("]");
         if (var5) {
            var0.append('[');
         }

         var0.append(var3);
         if (var5) {
            var0.append(']');
         }

         if (var4 != -1) {
            var0.append(':');
            var0.append(var4);
         }
      } else if (var1 != null) {
         var0.append("//");
         if (var1.startsWith("[")) {
            int var8 = var1.indexOf("]");
            if (var8 != -1 && var1.indexOf(":") != -1) {
               String var6;
               String var7;
               if (var8 == var1.length()) {
                  var7 = var1;
                  var6 = "";
               } else {
                  var7 = var1.substring(0, var8 + 1);
                  var6 = var1.substring(var8 + 1);
               }

               var0.append(var7);
               var0.append(quote(var6, L_REG_NAME | L_SERVER, H_REG_NAME | H_SERVER));
            }
         } else {
            var0.append(quote(var1, L_REG_NAME | L_SERVER, H_REG_NAME | H_SERVER));
         }
      }

   }

   private static void appendFragment(StringBuffer var0, String var1) {
      if (var1 != null) {
         var0.append('#');
         var0.append(quote(var1, L_URIC, H_URIC));
      }

   }

   private static String quote(String var0, long var1, long var3) {
      int var5 = var0.length();
      StringBuffer var6 = null;
      boolean var7 = (var1 & 1L) != 0L;

      for(int var8 = 0; var8 < var0.length(); ++var8) {
         char var9 = var0.charAt(var8);
         if (var9 < 128) {
            if (!match(var9, var1, var3) && !isEscaped(var0, var8)) {
               if (var6 == null) {
                  var6 = new StringBuffer();
                  var6.append(var0.substring(0, var8));
               }

               appendEscape(var6, (byte)var9);
            } else if (var6 != null) {
               var6.append(var9);
            }
         } else if (var7 && (Character.isSpaceChar(var9) || Character.isISOControl(var9))) {
            if (var6 == null) {
               var6 = new StringBuffer();
               var6.append(var0.substring(0, var8));
            }

            appendEncoded(var6, var9);
         } else if (var6 != null) {
            var6.append(var9);
         }
      }

      return var6 == null ? var0 : var6.toString();
   }

   private static boolean isEscaped(String var0, int var1) {
      if (var0 != null && var0.length() > var1 + 2) {
         return var0.charAt(var1) == '%' && match(var0.charAt(var1 + 1), L_HEX, H_HEX) && match(var0.charAt(var1 + 2), L_HEX, H_HEX);
      } else {
         return false;
      }
   }

   private static void appendEncoded(StringBuffer var0, char var1) {
      ByteBuffer var2 = null;

      try {
         var2 = ThreadLocalCoders.encoderFor("UTF-8").encode(CharBuffer.wrap((CharSequence)("" + var1)));
      } catch (CharacterCodingException var4) {
         assert false;
      }

      while(var2.hasRemaining()) {
         int var3 = var2.get() & 255;
         if (var3 >= 128) {
            appendEscape(var0, (byte)var3);
         } else {
            var0.append((char)var3);
         }
      }

   }

   private static void appendEscape(StringBuffer var0, byte var1) {
      var0.append('%');
      var0.append(hexDigits[var1 >> 4 & 15]);
      var0.append(hexDigits[var1 >> 0 & 15]);
   }

   private static boolean match(char var0, long var1, long var3) {
      if (var0 < '@') {
         return (1L << var0 & var1) != 0L;
      } else if (var0 < 128) {
         return (1L << var0 - 64 & var3) != 0L;
      } else {
         return false;
      }
   }

   private static void checkPath(String var0, String var1, String var2) throws URISyntaxException {
      if (var1 != null && var2 != null && var2.length() > 0 && var2.charAt(0) != '/') {
         throw new URISyntaxException(var0, "Relative path in absolute URI");
      }
   }

   private static long lowMask(char var0, char var1) {
      long var2 = 0L;
      int var4 = Math.max(Math.min(var0, 63), 0);
      int var5 = Math.max(Math.min(var1, 63), 0);

      for(int var6 = var4; var6 <= var5; ++var6) {
         var2 |= 1L << var6;
      }

      return var2;
   }

   private static long lowMask(String var0) {
      int var1 = var0.length();
      long var2 = 0L;

      for(int var4 = 0; var4 < var1; ++var4) {
         char var5 = var0.charAt(var4);
         if (var5 < '@') {
            var2 |= 1L << var5;
         }
      }

      return var2;
   }

   private static long highMask(char var0, char var1) {
      long var2 = 0L;
      int var4 = Math.max(Math.min(var0, 127), 64) - 64;
      int var5 = Math.max(Math.min(var1, 127), 64) - 64;

      for(int var6 = var4; var6 <= var5; ++var6) {
         var2 |= 1L << var6;
      }

      return var2;
   }

   private static long highMask(String var0) {
      int var1 = var0.length();
      long var2 = 0L;

      for(int var4 = 0; var4 < var1; ++var4) {
         char var5 = var0.charAt(var4);
         if (var5 >= '@' && var5 < 128) {
            var2 |= 1L << var5 - 64;
         }
      }

      return var2;
   }

   static {
      encodedInPath.set(61);
      encodedInPath.set(59);
      encodedInPath.set(63);
      encodedInPath.set(47);
      encodedInPath.set(35);
      encodedInPath.set(32);
      encodedInPath.set(60);
      encodedInPath.set(62);
      encodedInPath.set(37);
      encodedInPath.set(34);
      encodedInPath.set(123);
      encodedInPath.set(125);
      encodedInPath.set(124);
      encodedInPath.set(92);
      encodedInPath.set(94);
      encodedInPath.set(91);
      encodedInPath.set(93);
      encodedInPath.set(96);

      for(int var0 = 0; var0 < 32; ++var0) {
         encodedInPath.set(var0);
      }

      encodedInPath.set(127);
      hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
      L_DIGIT = lowMask('0', '9');
      L_HEX = L_DIGIT;
      H_HEX = highMask('A', 'F') | highMask('a', 'f');
      H_UPALPHA = highMask('A', 'Z');
      H_LOWALPHA = highMask('a', 'z');
      H_ALPHA = H_LOWALPHA | H_UPALPHA;
      L_ALPHANUM = L_DIGIT | 0L;
      H_ALPHANUM = 0L | H_ALPHA;
      L_MARK = lowMask("-_.!~*'()");
      H_MARK = highMask("-_.!~*'()");
      L_UNRESERVED = L_ALPHANUM | L_MARK;
      H_UNRESERVED = H_ALPHANUM | H_MARK;
      L_RESERVED = lowMask(";/?:@&=+$,[]");
      H_RESERVED = highMask(";/?:@&=+$,[]");
      L_DASH = lowMask("-");
      H_DASH = highMask("-");
      L_URIC = L_RESERVED | L_UNRESERVED | 1L;
      H_URIC = H_RESERVED | H_UNRESERVED | 0L;
      L_PCHAR = L_UNRESERVED | 1L | lowMask(":@&=+$,");
      H_PCHAR = H_UNRESERVED | 0L | highMask(":@&=+$,");
      L_PATH = L_PCHAR | lowMask(";/");
      H_PATH = H_PCHAR | highMask(";/");
      L_USERINFO = L_UNRESERVED | 1L | lowMask(";:&=+$,");
      H_USERINFO = H_UNRESERVED | 0L | highMask(";:&=+$,");
      L_REG_NAME = L_UNRESERVED | 1L | lowMask("$,;:@&=+");
      H_REG_NAME = H_UNRESERVED | 0L | highMask("$,;:@&=+");
      L_SERVER = L_USERINFO | L_ALPHANUM | L_DASH | lowMask(".:@[]");
      H_SERVER = H_USERINFO | H_ALPHANUM | H_DASH | highMask(".:@[]");
   }
}
