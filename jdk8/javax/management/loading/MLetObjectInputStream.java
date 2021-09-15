package javax.management.loading;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;

class MLetObjectInputStream extends ObjectInputStream {
   private MLet loader;

   public MLetObjectInputStream(InputStream var1, MLet var2) throws IOException, StreamCorruptedException {
      super(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("Illegal null argument to MLetObjectInputStream");
      } else {
         this.loader = var2;
      }
   }

   private Class<?> primitiveType(char var1) {
      switch(var1) {
      case 'B':
         return Byte.TYPE;
      case 'C':
         return Character.TYPE;
      case 'D':
         return Double.TYPE;
      case 'E':
      case 'G':
      case 'H':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      default:
         return null;
      case 'F':
         return Float.TYPE;
      case 'I':
         return Integer.TYPE;
      case 'J':
         return Long.TYPE;
      case 'S':
         return Short.TYPE;
      case 'Z':
         return Boolean.TYPE;
      }
   }

   protected Class<?> resolveClass(ObjectStreamClass var1) throws IOException, ClassNotFoundException {
      String var2 = var1.getName();
      if (!var2.startsWith("[")) {
         return this.loader.loadClass(var2);
      } else {
         int var3;
         for(var3 = 1; var2.charAt(var3) == '['; ++var3) {
         }

         Class var4;
         if (var2.charAt(var3) == 'L') {
            var4 = this.loader.loadClass(var2.substring(var3 + 1, var2.length() - 1));
         } else {
            if (var2.length() != var3 + 1) {
               throw new ClassNotFoundException(var2);
            }

            var4 = this.primitiveType(var2.charAt(var3));
         }

         int[] var5 = new int[var3];

         for(int var6 = 0; var6 < var3; ++var6) {
            var5[var6] = 0;
         }

         return Array.newInstance(var4, var5).getClass();
      }
   }

   public ClassLoader getClassLoader() {
      return this.loader;
   }
}
