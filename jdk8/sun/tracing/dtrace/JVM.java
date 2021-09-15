package sun.tracing.dtrace;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

class JVM {
   static long activate(String var0, DTraceProvider[] var1) {
      return activate0(var0, var1);
   }

   static void dispose(long var0) {
      dispose0(var0);
   }

   static boolean isEnabled(Method var0) {
      return isEnabled0(var0);
   }

   static boolean isSupported() {
      return isSupported0();
   }

   static Class<?> defineClass(ClassLoader var0, String var1, byte[] var2, int var3, int var4) {
      return defineClass0(var0, var1, var2, var3, var4);
   }

   private static native long activate0(String var0, DTraceProvider[] var1);

   private static native void dispose0(long var0);

   private static native boolean isEnabled0(Method var0);

   private static native boolean isSupported0();

   private static native Class<?> defineClass0(ClassLoader var0, String var1, byte[] var2, int var3, int var4);

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("jsdt");
            return null;
         }
      });
   }
}
