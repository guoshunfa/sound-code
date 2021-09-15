package java.nio.file.attribute;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class PosixFilePermissions {
   private PosixFilePermissions() {
   }

   private static void writeBits(StringBuilder var0, boolean var1, boolean var2, boolean var3) {
      if (var1) {
         var0.append('r');
      } else {
         var0.append('-');
      }

      if (var2) {
         var0.append('w');
      } else {
         var0.append('-');
      }

      if (var3) {
         var0.append('x');
      } else {
         var0.append('-');
      }

   }

   public static String toString(Set<PosixFilePermission> var0) {
      StringBuilder var1 = new StringBuilder(9);
      writeBits(var1, var0.contains(PosixFilePermission.OWNER_READ), var0.contains(PosixFilePermission.OWNER_WRITE), var0.contains(PosixFilePermission.OWNER_EXECUTE));
      writeBits(var1, var0.contains(PosixFilePermission.GROUP_READ), var0.contains(PosixFilePermission.GROUP_WRITE), var0.contains(PosixFilePermission.GROUP_EXECUTE));
      writeBits(var1, var0.contains(PosixFilePermission.OTHERS_READ), var0.contains(PosixFilePermission.OTHERS_WRITE), var0.contains(PosixFilePermission.OTHERS_EXECUTE));
      return var1.toString();
   }

   private static boolean isSet(char var0, char var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 == '-') {
         return false;
      } else {
         throw new IllegalArgumentException("Invalid mode");
      }
   }

   private static boolean isR(char var0) {
      return isSet(var0, 'r');
   }

   private static boolean isW(char var0) {
      return isSet(var0, 'w');
   }

   private static boolean isX(char var0) {
      return isSet(var0, 'x');
   }

   public static Set<PosixFilePermission> fromString(String var0) {
      if (var0.length() != 9) {
         throw new IllegalArgumentException("Invalid mode");
      } else {
         EnumSet var1 = EnumSet.noneOf(PosixFilePermission.class);
         if (isR(var0.charAt(0))) {
            var1.add(PosixFilePermission.OWNER_READ);
         }

         if (isW(var0.charAt(1))) {
            var1.add(PosixFilePermission.OWNER_WRITE);
         }

         if (isX(var0.charAt(2))) {
            var1.add(PosixFilePermission.OWNER_EXECUTE);
         }

         if (isR(var0.charAt(3))) {
            var1.add(PosixFilePermission.GROUP_READ);
         }

         if (isW(var0.charAt(4))) {
            var1.add(PosixFilePermission.GROUP_WRITE);
         }

         if (isX(var0.charAt(5))) {
            var1.add(PosixFilePermission.GROUP_EXECUTE);
         }

         if (isR(var0.charAt(6))) {
            var1.add(PosixFilePermission.OTHERS_READ);
         }

         if (isW(var0.charAt(7))) {
            var1.add(PosixFilePermission.OTHERS_WRITE);
         }

         if (isX(var0.charAt(8))) {
            var1.add(PosixFilePermission.OTHERS_EXECUTE);
         }

         return var1;
      }
   }

   public static FileAttribute<Set<PosixFilePermission>> asFileAttribute(Set<PosixFilePermission> var0) {
      final HashSet var3 = new HashSet(var0);
      Iterator var1 = var3.iterator();

      PosixFilePermission var2;
      do {
         if (!var1.hasNext()) {
            return new FileAttribute<Set<PosixFilePermission>>() {
               public String name() {
                  return "posix:permissions";
               }

               public Set<PosixFilePermission> value() {
                  return Collections.unmodifiableSet(var3);
               }
            };
         }

         var2 = (PosixFilePermission)var1.next();
      } while(var2 != null);

      throw new NullPointerException();
   }
}
