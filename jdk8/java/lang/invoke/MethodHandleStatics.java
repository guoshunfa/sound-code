package java.lang.invoke;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

class MethodHandleStatics {
   static final Unsafe UNSAFE = Unsafe.getUnsafe();
   static final boolean DEBUG_METHOD_HANDLE_NAMES;
   static final boolean DUMP_CLASS_FILES;
   static final boolean TRACE_INTERPRETER;
   static final boolean TRACE_METHOD_LINKAGE;
   static final int COMPILE_THRESHOLD;
   static final int DONT_INLINE_THRESHOLD;
   static final int PROFILE_LEVEL;
   static final boolean PROFILE_GWT;
   static final int CUSTOMIZE_THRESHOLD;

   private MethodHandleStatics() {
   }

   static boolean debugEnabled() {
      return DEBUG_METHOD_HANDLE_NAMES | DUMP_CLASS_FILES | TRACE_INTERPRETER | TRACE_METHOD_LINKAGE;
   }

   static String getNameString(MethodHandle var0, MethodType var1) {
      if (var1 == null) {
         var1 = var0.type();
      }

      MemberName var2 = null;
      if (var0 != null) {
         var2 = var0.internalMemberName();
      }

      return var2 == null ? "invoke" + var1 : var2.getName() + var1;
   }

   static String getNameString(MethodHandle var0, MethodHandle var1) {
      return getNameString(var0, var1 == null ? (MethodType)null : var1.type());
   }

   static String getNameString(MethodHandle var0) {
      return getNameString(var0, (MethodType)null);
   }

   static String addTypeString(Object var0, MethodHandle var1) {
      String var2 = String.valueOf(var0);
      if (var1 == null) {
         return var2;
      } else {
         int var3 = var2.indexOf(40);
         if (var3 >= 0) {
            var2 = var2.substring(0, var3);
         }

         return var2 + var1.type();
      }
   }

   static InternalError newInternalError(String var0) {
      return new InternalError(var0);
   }

   static InternalError newInternalError(String var0, Throwable var1) {
      return new InternalError(var0, var1);
   }

   static InternalError newInternalError(Throwable var0) {
      return new InternalError(var0);
   }

   static RuntimeException newIllegalStateException(String var0) {
      return new IllegalStateException(var0);
   }

   static RuntimeException newIllegalStateException(String var0, Object var1) {
      return new IllegalStateException(message(var0, var1));
   }

   static RuntimeException newIllegalArgumentException(String var0) {
      return new IllegalArgumentException(var0);
   }

   static RuntimeException newIllegalArgumentException(String var0, Object var1) {
      return new IllegalArgumentException(message(var0, var1));
   }

   static RuntimeException newIllegalArgumentException(String var0, Object var1, Object var2) {
      return new IllegalArgumentException(message(var0, var1, var2));
   }

   static Error uncaughtException(Throwable var0) {
      if (var0 instanceof Error) {
         throw (Error)var0;
      } else if (var0 instanceof RuntimeException) {
         throw (RuntimeException)var0;
      } else {
         throw newInternalError("uncaught exception", var0);
      }
   }

   static Error NYI() {
      throw new AssertionError("NYI");
   }

   private static String message(String var0, Object var1) {
      if (var1 != null) {
         var0 = var0 + ": " + var1;
      }

      return var0;
   }

   private static String message(String var0, Object var1, Object var2) {
      if (var1 != null || var2 != null) {
         var0 = var0 + ": " + var1 + ", " + var2;
      }

      return var0;
   }

   static {
      final Object[] var0 = new Object[9];
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            var0[0] = Boolean.getBoolean("java.lang.invoke.MethodHandle.DEBUG_NAMES");
            var0[1] = Boolean.getBoolean("java.lang.invoke.MethodHandle.DUMP_CLASS_FILES");
            var0[2] = Boolean.getBoolean("java.lang.invoke.MethodHandle.TRACE_INTERPRETER");
            var0[3] = Boolean.getBoolean("java.lang.invoke.MethodHandle.TRACE_METHOD_LINKAGE");
            var0[4] = Integer.getInteger("java.lang.invoke.MethodHandle.COMPILE_THRESHOLD", 0);
            var0[5] = Integer.getInteger("java.lang.invoke.MethodHandle.DONT_INLINE_THRESHOLD", 30);
            var0[6] = Integer.getInteger("java.lang.invoke.MethodHandle.PROFILE_LEVEL", 0);
            var0[7] = Boolean.parseBoolean(System.getProperty("java.lang.invoke.MethodHandle.PROFILE_GWT", "true"));
            var0[8] = Integer.getInteger("java.lang.invoke.MethodHandle.CUSTOMIZE_THRESHOLD", 127);
            return null;
         }
      });
      DEBUG_METHOD_HANDLE_NAMES = (Boolean)var0[0];
      DUMP_CLASS_FILES = (Boolean)var0[1];
      TRACE_INTERPRETER = (Boolean)var0[2];
      TRACE_METHOD_LINKAGE = (Boolean)var0[3];
      COMPILE_THRESHOLD = (Integer)var0[4];
      DONT_INLINE_THRESHOLD = (Integer)var0[5];
      PROFILE_LEVEL = (Integer)var0[6];
      PROFILE_GWT = (Boolean)var0[7];
      CUSTOMIZE_THRESHOLD = (Integer)var0[8];
      if (CUSTOMIZE_THRESHOLD < -1 || CUSTOMIZE_THRESHOLD > 127) {
         throw newInternalError("CUSTOMIZE_THRESHOLD should be in [-1...127] range");
      }
   }
}
