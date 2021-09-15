package sun.nio.fs;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;

class UnixUriUtils {
   private static final long L_DIGIT = lowMask('0', '9');
   private static final long H_DIGIT = 0L;
   private static final long L_UPALPHA = 0L;
   private static final long H_UPALPHA = highMask('A', 'Z');
   private static final long L_LOWALPHA = 0L;
   private static final long H_LOWALPHA = highMask('a', 'z');
   private static final long L_ALPHA = 0L;
   private static final long H_ALPHA;
   private static final long L_ALPHANUM;
   private static final long H_ALPHANUM;
   private static final long L_MARK;
   private static final long H_MARK;
   private static final long L_UNRESERVED;
   private static final long H_UNRESERVED;
   private static final long L_PCHAR;
   private static final long H_PCHAR;
   private static final long L_PATH;
   private static final long H_PATH;
   private static final char[] hexDigits;

   private UnixUriUtils() {
   }

   static Path fromUri(UnixFileSystem var0, URI var1) {
      if (!var1.isAbsolute()) {
         throw new IllegalArgumentException("URI is not absolute");
      } else if (var1.isOpaque()) {
         throw new IllegalArgumentException("URI is not hierarchical");
      } else {
         String var2 = var1.getScheme();
         if (var2 != null && var2.equalsIgnoreCase("file")) {
            if (var1.getAuthority() != null) {
               throw new IllegalArgumentException("URI has an authority component");
            } else if (var1.getFragment() != null) {
               throw new IllegalArgumentException("URI has a fragment component");
            } else if (var1.getQuery() != null) {
               throw new IllegalArgumentException("URI has a query component");
            } else if (!var1.toString().startsWith("file:///")) {
               return (new File(var1)).toPath();
            } else {
               String var3 = var1.getRawPath();
               int var4 = var3.length();
               if (var4 == 0) {
                  throw new IllegalArgumentException("URI path component is empty");
               } else {
                  if (var3.endsWith("/") && var4 > 1) {
                     --var4;
                  }

                  byte[] var5 = new byte[var4];
                  int var6 = 0;

                  byte var9;
                  for(int var7 = 0; var7 < var4; var5[var6++] = var9) {
                     char var8 = var3.charAt(var7++);
                     if (var8 == '%') {
                        assert var7 + 2 <= var4;

                        char var10 = var3.charAt(var7++);
                        char var11 = var3.charAt(var7++);
                        var9 = (byte)(decode(var10) << 4 | decode(var11));
                        if (var9 == 0) {
                           throw new IllegalArgumentException("Nul character not allowed");
                        }
                     } else {
                        assert var8 < 128;

                        var9 = (byte)var8;
                     }
                  }

                  if (var6 != var5.length) {
                     var5 = Arrays.copyOf(var5, var6);
                  }

                  return new UnixPath(var0, var5);
               }
            }
         } else {
            throw new IllegalArgumentException("URI scheme is not \"file\"");
         }
      }
   }

   static URI toUri(UnixPath var0) {
      byte[] var1 = var0.toAbsolutePath().asByteArray();
      StringBuilder var2 = new StringBuilder("file:///");

      assert var1[0] == 47;

      for(int var3 = 1; var3 < var1.length; ++var3) {
         char var4 = (char)(var1[var3] & 255);
         if (match(var4, L_PATH, H_PATH)) {
            var2.append(var4);
         } else {
            var2.append('%');
            var2.append(hexDigits[var4 >> 4 & 15]);
            var2.append(hexDigits[var4 & 15]);
         }
      }

      if (var2.charAt(var2.length() - 1) != '/') {
         try {
            if (UnixFileAttributes.get(var0, true).isDirectory()) {
               var2.append('/');
            }
         } catch (UnixException var6) {
         }
      }

      try {
         return new URI(var2.toString());
      } catch (URISyntaxException var5) {
         throw new AssertionError(var5);
      }
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

   private static long lowMask(char var0, char var1) {
      long var2 = 0L;
      int var4 = Math.max(Math.min(var0, 63), 0);
      int var5 = Math.max(Math.min(var1, 63), 0);

      for(int var6 = var4; var6 <= var5; ++var6) {
         var2 |= 1L << var6;
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

   private static boolean match(char var0, long var1, long var3) {
      if (var0 < '@') {
         return (1L << var0 & var1) != 0L;
      } else if (var0 < 128) {
         return (1L << var0 - 64 & var3) != 0L;
      } else {
         return false;
      }
   }

   private static int decode(char var0) {
      if (var0 >= '0' && var0 <= '9') {
         return var0 - 48;
      } else if (var0 >= 'a' && var0 <= 'f') {
         return var0 - 97 + 10;
      } else if (var0 >= 'A' && var0 <= 'F') {
         return var0 - 65 + 10;
      } else {
         throw new AssertionError();
      }
   }

   static {
      H_ALPHA = H_LOWALPHA | H_UPALPHA;
      L_ALPHANUM = L_DIGIT | 0L;
      H_ALPHANUM = 0L | H_ALPHA;
      L_MARK = lowMask("-_.!~*'()");
      H_MARK = highMask("-_.!~*'()");
      L_UNRESERVED = L_ALPHANUM | L_MARK;
      H_UNRESERVED = H_ALPHANUM | H_MARK;
      L_PCHAR = L_UNRESERVED | lowMask(":@&=+$,");
      H_PCHAR = H_UNRESERVED | highMask(":@&=+$,");
      L_PATH = L_PCHAR | lowMask(";/");
      H_PATH = H_PCHAR | highMask(";/");
      hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
   }
}
