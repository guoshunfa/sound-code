package sun.nio.fs;

import java.nio.charset.Charset;
import java.nio.file.LinkOption;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;
import sun.security.action.GetPropertyAction;

class Util {
   private static final Charset jnuEncoding = Charset.forName((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.jnu.encoding"))));

   private Util() {
   }

   static Charset jnuEncoding() {
      return jnuEncoding;
   }

   static byte[] toBytes(String var0) {
      return var0.getBytes(jnuEncoding);
   }

   static String toString(byte[] var0) {
      return new String(var0, jnuEncoding);
   }

   static String[] split(String var0, char var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < var0.length(); ++var3) {
         if (var0.charAt(var3) == var1) {
            ++var2;
         }
      }

      String[] var7 = new String[var2 + 1];
      int var4 = 0;
      int var5 = 0;

      for(int var6 = 0; var6 < var0.length(); ++var6) {
         if (var0.charAt(var6) == var1) {
            var7[var4++] = var0.substring(var5, var6);
            var5 = var6 + 1;
         }
      }

      var7[var4] = var0.substring(var5, var0.length());
      return var7;
   }

   @SafeVarargs
   static <E> Set<E> newSet(E... var0) {
      HashSet var1 = new HashSet();
      Object[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object var5 = var2[var4];
         var1.add(var5);
      }

      return var1;
   }

   @SafeVarargs
   static <E> Set<E> newSet(Set<E> var0, E... var1) {
      HashSet var2 = new HashSet(var0);
      Object[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Object var6 = var3[var5];
         var2.add(var6);
      }

      return var2;
   }

   static boolean followLinks(LinkOption... var0) {
      boolean var1 = true;
      LinkOption[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         LinkOption var5 = var2[var4];
         if (var5 != LinkOption.NOFOLLOW_LINKS) {
            if (var5 == null) {
               throw new NullPointerException();
            }

            throw new AssertionError("Should not get here");
         }

         var1 = false;
      }

      return var1;
   }
}
