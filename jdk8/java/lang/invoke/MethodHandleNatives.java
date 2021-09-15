package java.lang.invoke;

import java.lang.reflect.Field;
import java.util.Arrays;

class MethodHandleNatives {
   static final boolean COUNT_GWT;

   private MethodHandleNatives() {
   }

   static native void init(MemberName var0, Object var1);

   static native void expand(MemberName var0);

   static native MemberName resolve(MemberName var0, Class<?> var1) throws LinkageError, ClassNotFoundException;

   static native int getMembers(Class<?> var0, String var1, String var2, int var3, Class<?> var4, int var5, MemberName[] var6);

   static native long objectFieldOffset(MemberName var0);

   static native long staticFieldOffset(MemberName var0);

   static native Object staticFieldBase(MemberName var0);

   static native Object getMemberVMInfo(MemberName var0);

   static native int getConstant(int var0);

   static native void setCallSiteTargetNormal(CallSite var0, MethodHandle var1);

   static native void setCallSiteTargetVolatile(CallSite var0, MethodHandle var1);

   private static native void registerNatives();

   static boolean refKindIsValid(int var0) {
      return var0 > 0 && var0 < 10;
   }

   static boolean refKindIsField(byte var0) {
      assert refKindIsValid(var0);

      return var0 <= 4;
   }

   static boolean refKindIsGetter(byte var0) {
      assert refKindIsValid(var0);

      return var0 <= 2;
   }

   static boolean refKindIsSetter(byte var0) {
      return refKindIsField(var0) && !refKindIsGetter(var0);
   }

   static boolean refKindIsMethod(byte var0) {
      return !refKindIsField(var0) && var0 != 8;
   }

   static boolean refKindIsConstructor(byte var0) {
      return var0 == 8;
   }

   static boolean refKindHasReceiver(byte var0) {
      assert refKindIsValid(var0);

      return (var0 & 1) != 0;
   }

   static boolean refKindIsStatic(byte var0) {
      return !refKindHasReceiver(var0) && var0 != 8;
   }

   static boolean refKindDoesDispatch(byte var0) {
      assert refKindIsValid(var0);

      return var0 == 5 || var0 == 9;
   }

   static String refKindName(byte var0) {
      assert refKindIsValid(var0);

      switch(var0) {
      case 1:
         return "getField";
      case 2:
         return "getStatic";
      case 3:
         return "putField";
      case 4:
         return "putStatic";
      case 5:
         return "invokeVirtual";
      case 6:
         return "invokeStatic";
      case 7:
         return "invokeSpecial";
      case 8:
         return "newInvokeSpecial";
      case 9:
         return "invokeInterface";
      default:
         return "REF_???";
      }
   }

   private static native int getNamedCon(int var0, Object[] var1);

   static boolean verifyConstants() {
      Object[] var0 = new Object[]{null};
      int var1 = 0;

      while(true) {
         var0[0] = null;
         int var2 = getNamedCon(var1, var0);
         if (var0[0] == null) {
            return true;
         }

         String var3 = (String)var0[0];

         try {
            Field var4 = MethodHandleNatives.Constants.class.getDeclaredField(var3);
            int var8 = var4.getInt((Object)null);
            if (var8 != var2) {
               String var6 = var3 + ": JVM has " + var2 + " while Java has " + var8;
               if (!var3.equals("CONV_OP_LIMIT")) {
                  throw new InternalError(var6);
               }

               System.err.println("warning: " + var6);
            }
         } catch (IllegalAccessException | NoSuchFieldException var7) {
            String var5 = var3 + ": JVM has " + var2 + " which Java does not define";
         }

         ++var1;
      }
   }

   static MemberName linkCallSite(Object var0, Object var1, Object var2, Object var3, Object var4, Object[] var5) {
      MethodHandle var6 = (MethodHandle)var1;
      Class var7 = (Class)var0;
      String var8 = var2.toString().intern();
      MethodType var9 = (MethodType)var3;
      return !MethodHandleStatics.TRACE_METHOD_LINKAGE ? linkCallSiteImpl(var7, var6, var8, var9, var4, var5) : linkCallSiteTracing(var7, var6, var8, var9, var4, var5);
   }

   static MemberName linkCallSiteImpl(Class<?> var0, MethodHandle var1, String var2, MethodType var3, Object var4, Object[] var5) {
      CallSite var6 = CallSite.makeSite(var1, var2, var3, var4, var0);
      if (var6 instanceof ConstantCallSite) {
         var5[0] = var6.dynamicInvoker();
         return Invokers.linkToTargetMethod(var3);
      } else {
         var5[0] = var6;
         return Invokers.linkToCallSiteMethod(var3);
      }
   }

   static MemberName linkCallSiteTracing(Class<?> var0, MethodHandle var1, String var2, MethodType var3, Object var4, Object[] var5) {
      Object var6 = var1.internalMemberName();
      if (var6 == null) {
         var6 = var1;
      }

      Object var7 = var4 instanceof Object[] ? Arrays.asList((Object[])((Object[])var4)) : var4;
      System.out.println("linkCallSite " + var0.getName() + " " + var6 + " " + var2 + var3 + "/" + var7);

      try {
         MemberName var8 = linkCallSiteImpl(var0, var1, var2, var3, var4, var5);
         System.out.println("linkCallSite => " + var8 + " + " + var5[0]);
         return var8;
      } catch (Throwable var9) {
         System.out.println("linkCallSite => throw " + var9);
         throw var9;
      }
   }

   static MethodType findMethodHandleType(Class<?> var0, Class<?>[] var1) {
      return MethodType.makeImpl(var0, var1, true);
   }

   static MemberName linkMethod(Class<?> var0, int var1, Class<?> var2, String var3, Object var4, Object[] var5) {
      return !MethodHandleStatics.TRACE_METHOD_LINKAGE ? linkMethodImpl(var0, var1, var2, var3, var4, var5) : linkMethodTracing(var0, var1, var2, var3, var4, var5);
   }

   static MemberName linkMethodImpl(Class<?> var0, int var1, Class<?> var2, String var3, Object var4, Object[] var5) {
      try {
         if (var2 == MethodHandle.class && var1 == 5) {
            return Invokers.methodHandleInvokeLinkerMethod(var3, fixMethodType(var0, var4), var5);
         }
      } catch (Throwable var7) {
         if (var7 instanceof LinkageError) {
            throw (LinkageError)var7;
         }

         throw new LinkageError(var7.getMessage(), var7);
      }

      throw new LinkageError("no such method " + var2.getName() + "." + var3 + var4);
   }

   private static MethodType fixMethodType(Class<?> var0, Object var1) {
      return var1 instanceof MethodType ? (MethodType)var1 : MethodType.fromMethodDescriptorString((String)var1, var0.getClassLoader());
   }

   static MemberName linkMethodTracing(Class<?> var0, int var1, Class<?> var2, String var3, Object var4, Object[] var5) {
      System.out.println("linkMethod " + var2.getName() + "." + var3 + var4 + "/" + Integer.toHexString(var1));

      try {
         MemberName var6 = linkMethodImpl(var0, var1, var2, var3, var4, var5);
         System.out.println("linkMethod => " + var6 + " + " + var5[0]);
         return var6;
      } catch (Throwable var7) {
         System.out.println("linkMethod => throw " + var7);
         throw var7;
      }
   }

   static MethodHandle linkMethodHandleConstant(Class<?> var0, int var1, Class<?> var2, String var3, Object var4) {
      try {
         MethodHandles.Lookup var5 = MethodHandles.Lookup.IMPL_LOOKUP.in(var0);

         assert refKindIsValid(var1);

         return var5.linkMethodHandleConstant((byte)var1, var2, var3, var4);
      } catch (IllegalAccessException var8) {
         Throwable var14 = var8.getCause();
         if (var14 instanceof AbstractMethodError) {
            throw (AbstractMethodError)var14;
         } else {
            IllegalAccessError var7 = new IllegalAccessError(var8.getMessage());
            throw initCauseFrom(var7, var8);
         }
      } catch (NoSuchMethodException var9) {
         NoSuchMethodError var13 = new NoSuchMethodError(var9.getMessage());
         throw initCauseFrom(var13, var9);
      } catch (NoSuchFieldException var10) {
         NoSuchFieldError var12 = new NoSuchFieldError(var10.getMessage());
         throw initCauseFrom(var12, var10);
      } catch (ReflectiveOperationException var11) {
         IncompatibleClassChangeError var6 = new IncompatibleClassChangeError();
         throw initCauseFrom(var6, var11);
      }
   }

   private static Error initCauseFrom(Error var0, Exception var1) {
      Throwable var2 = var1.getCause();
      if (var0.getClass().isInstance(var2)) {
         return (Error)var2;
      } else {
         var0.initCause((Throwable)(var2 == null ? var1 : var2));
         return var0;
      }
   }

   static boolean isCallerSensitive(MemberName var0) {
      if (!var0.isInvocable()) {
         return false;
      } else {
         return var0.isCallerSensitive() || canBeCalledVirtual(var0);
      }
   }

   static boolean canBeCalledVirtual(MemberName var0) {
      assert var0.isInvocable();

      Class var1 = var0.getDeclaringClass();
      String var2 = var0.getName();
      byte var3 = -1;
      switch(var2.hashCode()) {
      case 622280134:
         if (var2.equals("checkMemberAccess")) {
            var3 = 0;
         }
         break;
      case 1178897522:
         if (var2.equals("getContextClassLoader")) {
            var3 = 1;
         }
      }

      switch(var3) {
      case 0:
         return canBeCalledVirtual(var0, SecurityManager.class);
      case 1:
         return canBeCalledVirtual(var0, Thread.class);
      default:
         return false;
      }
   }

   static boolean canBeCalledVirtual(MemberName var0, Class<?> var1) {
      Class var2 = var0.getDeclaringClass();
      if (var2 == var1) {
         return true;
      } else if (!var0.isStatic() && !var0.isPrivate()) {
         return var1.isAssignableFrom(var2) || var2.isInterface();
      } else {
         return false;
      }
   }

   static {
      registerNatives();
      COUNT_GWT = getConstant(4) != 0;
      MethodHandleImpl.initStatics();

      for(byte var1 = 1; var1 < 10; ++var1) {
         assert refKindHasReceiver(var1) == ((1 << var1 & 682) != 0) : var1;
      }

      assert verifyConstants();

   }

   static class Constants {
      static final int GC_COUNT_GWT = 4;
      static final int GC_LAMBDA_SUPPORT = 5;
      static final int MN_IS_METHOD = 65536;
      static final int MN_IS_CONSTRUCTOR = 131072;
      static final int MN_IS_FIELD = 262144;
      static final int MN_IS_TYPE = 524288;
      static final int MN_CALLER_SENSITIVE = 1048576;
      static final int MN_REFERENCE_KIND_SHIFT = 24;
      static final int MN_REFERENCE_KIND_MASK = 15;
      static final int MN_SEARCH_SUPERCLASSES = 1048576;
      static final int MN_SEARCH_INTERFACES = 2097152;
      static final int T_BOOLEAN = 4;
      static final int T_CHAR = 5;
      static final int T_FLOAT = 6;
      static final int T_DOUBLE = 7;
      static final int T_BYTE = 8;
      static final int T_SHORT = 9;
      static final int T_INT = 10;
      static final int T_LONG = 11;
      static final int T_OBJECT = 12;
      static final int T_VOID = 14;
      static final int T_ILLEGAL = 99;
      static final byte CONSTANT_Utf8 = 1;
      static final byte CONSTANT_Integer = 3;
      static final byte CONSTANT_Float = 4;
      static final byte CONSTANT_Long = 5;
      static final byte CONSTANT_Double = 6;
      static final byte CONSTANT_Class = 7;
      static final byte CONSTANT_String = 8;
      static final byte CONSTANT_Fieldref = 9;
      static final byte CONSTANT_Methodref = 10;
      static final byte CONSTANT_InterfaceMethodref = 11;
      static final byte CONSTANT_NameAndType = 12;
      static final byte CONSTANT_MethodHandle = 15;
      static final byte CONSTANT_MethodType = 16;
      static final byte CONSTANT_InvokeDynamic = 18;
      static final byte CONSTANT_LIMIT = 19;
      static final char ACC_PUBLIC = '\u0001';
      static final char ACC_PRIVATE = '\u0002';
      static final char ACC_PROTECTED = '\u0004';
      static final char ACC_STATIC = '\b';
      static final char ACC_FINAL = '\u0010';
      static final char ACC_SYNCHRONIZED = ' ';
      static final char ACC_VOLATILE = '@';
      static final char ACC_TRANSIENT = '\u0080';
      static final char ACC_NATIVE = 'Ā';
      static final char ACC_INTERFACE = 'Ȁ';
      static final char ACC_ABSTRACT = 'Ѐ';
      static final char ACC_STRICT = 'ࠀ';
      static final char ACC_SYNTHETIC = 'က';
      static final char ACC_ANNOTATION = ' ';
      static final char ACC_ENUM = '䀀';
      static final char ACC_SUPER = ' ';
      static final char ACC_BRIDGE = '@';
      static final char ACC_VARARGS = '\u0080';
      static final byte REF_NONE = 0;
      static final byte REF_getField = 1;
      static final byte REF_getStatic = 2;
      static final byte REF_putField = 3;
      static final byte REF_putStatic = 4;
      static final byte REF_invokeVirtual = 5;
      static final byte REF_invokeStatic = 6;
      static final byte REF_invokeSpecial = 7;
      static final byte REF_newInvokeSpecial = 8;
      static final byte REF_invokeInterface = 9;
      static final byte REF_LIMIT = 10;
   }
}
