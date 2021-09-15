package sun.corba;

import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

public final class Bridge {
   private static final Class[] NO_ARGS = new Class[0];
   private static final Permission getBridgePermission = new BridgePermission("getBridge");
   private static Bridge bridge = null;
   private final Method latestUserDefinedLoaderMethod = this.getLatestUserDefinedLoaderMethod();
   private final Unsafe unsafe = this.getUnsafe();
   private final ReflectionFactory reflectionFactory = (ReflectionFactory)AccessController.doPrivileged((PrivilegedAction)(new ReflectionFactory.GetReflectionFactoryAction()));
   public static final long INVALID_FIELD_OFFSET = -1L;

   private Method getLatestUserDefinedLoaderMethod() {
      return (Method)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            Method var1 = null;

            try {
               Class var2 = ObjectInputStream.class;
               var1 = var2.getDeclaredMethod("latestUserDefinedLoader", Bridge.NO_ARGS);
               var1.setAccessible(true);
               return var1;
            } catch (NoSuchMethodException var4) {
               Error var3 = new Error("java.io.ObjectInputStream latestUserDefinedLoader " + var4);
               var3.initCause(var4);
               throw var3;
            }
         }
      });
   }

   private Unsafe getUnsafe() {
      Field var1 = (Field)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            Field var1 = null;

            try {
               Class var2 = Unsafe.class;
               var1 = var2.getDeclaredField("theUnsafe");
               var1.setAccessible(true);
               return var1;
            } catch (NoSuchFieldException var4) {
               Error var3 = new Error("Could not access Unsafe");
               var3.initCause(var4);
               throw var3;
            }
         }
      });
      Unsafe var2 = null;

      try {
         var2 = (Unsafe)((Unsafe)var1.get((Object)null));
         return var2;
      } catch (Throwable var5) {
         Error var4 = new Error("Could not access Unsafe");
         var4.initCause(var5);
         throw var4;
      }
   }

   private Bridge() {
   }

   public static final synchronized Bridge get() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(getBridgePermission);
      }

      if (bridge == null) {
         bridge = new Bridge();
      }

      return bridge;
   }

   public final ClassLoader getLatestUserDefinedLoader() {
      Error var2;
      try {
         return (ClassLoader)this.latestUserDefinedLoaderMethod.invoke((Object)null, (Object[])NO_ARGS);
      } catch (InvocationTargetException var3) {
         var2 = new Error("sun.corba.Bridge.latestUserDefinedLoader: " + var3);
         var2.initCause(var3);
         throw var2;
      } catch (IllegalAccessException var4) {
         var2 = new Error("sun.corba.Bridge.latestUserDefinedLoader: " + var4);
         var2.initCause(var4);
         throw var2;
      }
   }

   public final int getInt(Object var1, long var2) {
      return this.unsafe.getInt(var1, var2);
   }

   public final void putInt(Object var1, long var2, int var4) {
      this.unsafe.putInt(var1, var2, var4);
   }

   public final Object getObject(Object var1, long var2) {
      return this.unsafe.getObject(var1, var2);
   }

   public final void putObject(Object var1, long var2, Object var4) {
      this.unsafe.putObject(var1, var2, var4);
   }

   public final boolean getBoolean(Object var1, long var2) {
      return this.unsafe.getBoolean(var1, var2);
   }

   public final void putBoolean(Object var1, long var2, boolean var4) {
      this.unsafe.putBoolean(var1, var2, var4);
   }

   public final byte getByte(Object var1, long var2) {
      return this.unsafe.getByte(var1, var2);
   }

   public final void putByte(Object var1, long var2, byte var4) {
      this.unsafe.putByte(var1, var2, var4);
   }

   public final short getShort(Object var1, long var2) {
      return this.unsafe.getShort(var1, var2);
   }

   public final void putShort(Object var1, long var2, short var4) {
      this.unsafe.putShort(var1, var2, var4);
   }

   public final char getChar(Object var1, long var2) {
      return this.unsafe.getChar(var1, var2);
   }

   public final void putChar(Object var1, long var2, char var4) {
      this.unsafe.putChar(var1, var2, var4);
   }

   public final long getLong(Object var1, long var2) {
      return this.unsafe.getLong(var1, var2);
   }

   public final void putLong(Object var1, long var2, long var4) {
      this.unsafe.putLong(var1, var2, var4);
   }

   public final float getFloat(Object var1, long var2) {
      return this.unsafe.getFloat(var1, var2);
   }

   public final void putFloat(Object var1, long var2, float var4) {
      this.unsafe.putFloat(var1, var2, var4);
   }

   public final double getDouble(Object var1, long var2) {
      return this.unsafe.getDouble(var1, var2);
   }

   public final void putDouble(Object var1, long var2, double var4) {
      this.unsafe.putDouble(var1, var2, var4);
   }

   public final long objectFieldOffset(Field var1) {
      return this.unsafe.objectFieldOffset(var1);
   }

   public final void throwException(Throwable var1) {
      this.unsafe.throwException(var1);
   }

   public final Constructor newConstructorForSerialization(Class var1, Constructor var2) {
      return this.reflectionFactory.newConstructorForSerialization(var1, var2);
   }
}
