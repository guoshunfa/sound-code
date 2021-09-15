package sun.nio.fs;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

class Reflect {
   private Reflect() {
   }

   private static void setAccessible(final AccessibleObject var0) {
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            var0.setAccessible(true);
            return null;
         }
      });
   }

   static Field lookupField(String var0, String var1) {
      try {
         Class var2 = Class.forName(var0);
         Field var3 = var2.getDeclaredField(var1);
         setAccessible(var3);
         return var3;
      } catch (ClassNotFoundException var4) {
         throw new AssertionError(var4);
      } catch (NoSuchFieldException var5) {
         throw new AssertionError(var5);
      }
   }
}
