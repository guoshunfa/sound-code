package sun.nio.ch;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

class Reflect {
   private Reflect() {
   }

   private static void setAccessible(final AccessibleObject var0) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            var0.setAccessible(true);
            return null;
         }
      });
   }

   static Constructor<?> lookupConstructor(String var0, Class<?>[] var1) {
      try {
         Class var2 = Class.forName(var0);
         Constructor var3 = var2.getDeclaredConstructor(var1);
         setAccessible(var3);
         return var3;
      } catch (NoSuchMethodException | ClassNotFoundException var4) {
         throw new Reflect.ReflectionError(var4);
      }
   }

   static Object invoke(Constructor<?> var0, Object[] var1) {
      try {
         return var0.newInstance(var1);
      } catch (IllegalAccessException | InvocationTargetException | InstantiationException var3) {
         throw new Reflect.ReflectionError(var3);
      }
   }

   static Method lookupMethod(String var0, String var1, Class... var2) {
      try {
         Class var3 = Class.forName(var0);
         Method var4 = var3.getDeclaredMethod(var1, var2);
         setAccessible(var4);
         return var4;
      } catch (NoSuchMethodException | ClassNotFoundException var5) {
         throw new Reflect.ReflectionError(var5);
      }
   }

   static Object invoke(Method var0, Object var1, Object[] var2) {
      try {
         return var0.invoke(var1, var2);
      } catch (InvocationTargetException | IllegalAccessException var4) {
         throw new Reflect.ReflectionError(var4);
      }
   }

   static Object invokeIO(Method var0, Object var1, Object[] var2) throws IOException {
      try {
         return var0.invoke(var1, var2);
      } catch (IllegalAccessException var4) {
         throw new Reflect.ReflectionError(var4);
      } catch (InvocationTargetException var5) {
         if (IOException.class.isInstance(var5.getCause())) {
            throw (IOException)var5.getCause();
         } else {
            throw new Reflect.ReflectionError(var5);
         }
      }
   }

   static Field lookupField(String var0, String var1) {
      try {
         Class var2 = Class.forName(var0);
         Field var3 = var2.getDeclaredField(var1);
         setAccessible(var3);
         return var3;
      } catch (NoSuchFieldException | ClassNotFoundException var4) {
         throw new Reflect.ReflectionError(var4);
      }
   }

   static Object get(Object var0, Field var1) {
      try {
         return var1.get(var0);
      } catch (IllegalAccessException var3) {
         throw new Reflect.ReflectionError(var3);
      }
   }

   static Object get(Field var0) {
      return get((Object)null, var0);
   }

   static void set(Object var0, Field var1, Object var2) {
      try {
         var1.set(var0, var2);
      } catch (IllegalAccessException var4) {
         throw new Reflect.ReflectionError(var4);
      }
   }

   static void setInt(Object var0, Field var1, int var2) {
      try {
         var1.setInt(var0, var2);
      } catch (IllegalAccessException var4) {
         throw new Reflect.ReflectionError(var4);
      }
   }

   static void setBoolean(Object var0, Field var1, boolean var2) {
      try {
         var1.setBoolean(var0, var2);
      } catch (IllegalAccessException var4) {
         throw new Reflect.ReflectionError(var4);
      }
   }

   private static class ReflectionError extends Error {
      private static final long serialVersionUID = -8659519328078164097L;

      ReflectionError(Throwable var1) {
         super(var1);
      }
   }
}
