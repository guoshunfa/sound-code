package java.lang.invoke;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import sun.invoke.util.ValueConversions;
import sun.invoke.util.VerifyAccess;
import sun.invoke.util.VerifyType;
import sun.invoke.util.Wrapper;
import sun.misc.Unsafe;

class DirectMethodHandle extends MethodHandle {
   final MemberName member;
   private static final MemberName.Factory IMPL_NAMES = MemberName.getFactory();
   private static byte AF_GETFIELD = 0;
   private static byte AF_PUTFIELD = 1;
   private static byte AF_GETSTATIC = 2;
   private static byte AF_PUTSTATIC = 3;
   private static byte AF_GETSTATIC_INIT = 4;
   private static byte AF_PUTSTATIC_INIT = 5;
   private static byte AF_LIMIT = 6;
   private static int FT_LAST_WRAPPER = Wrapper.values().length - 1;
   private static int FT_UNCHECKED_REF;
   private static int FT_CHECKED_REF;
   private static int FT_LIMIT;
   private static final LambdaForm[] ACCESSOR_FORMS;

   private DirectMethodHandle(MethodType var1, LambdaForm var2, MemberName var3) {
      super(var1, var2);
      if (!var3.isResolved()) {
         throw new InternalError();
      } else {
         if (var3.getDeclaringClass().isInterface() && var3.isMethod() && !var3.isAbstract()) {
            MemberName var4 = new MemberName(Object.class, var3.getName(), var3.getMethodType(), var3.getReferenceKind());
            var4 = MemberName.getFactory().resolveOrNull(var4.getReferenceKind(), var4, (Class)null);
            if (var4 != null && var4.isPublic()) {
               assert var3.getReferenceKind() == var4.getReferenceKind();

               var3 = var4;
            }
         }

         this.member = var3;
      }
   }

   static DirectMethodHandle make(byte var0, Class<?> var1, MemberName var2) {
      MethodType var3 = var2.getMethodOrFieldType();
      if (!var2.isStatic()) {
         if (!var2.getDeclaringClass().isAssignableFrom(var1) || var2.isConstructor()) {
            throw new InternalError(var2.toString());
         }

         var3 = var3.insertParameterTypes(0, (Class[])(var1));
      }

      LambdaForm var4;
      if (!var2.isField()) {
         switch(var0) {
         case 7:
            var2 = var2.asSpecial();
            var4 = preparedLambdaForm(var2);
            return new DirectMethodHandle.Special(var3, var4, var2);
         case 9:
            var4 = preparedLambdaForm(var2);
            return new DirectMethodHandle.Interface(var3, var4, var2, var1);
         default:
            var4 = preparedLambdaForm(var2);
            return new DirectMethodHandle(var3, var4, var2);
         }
      } else {
         var4 = preparedFieldLambdaForm(var2);
         long var5;
         if (var2.isStatic()) {
            var5 = MethodHandleNatives.staticFieldOffset(var2);
            Object var7 = MethodHandleNatives.staticFieldBase(var2);
            return new DirectMethodHandle.StaticAccessor(var3, var4, var2, var7, var5);
         } else {
            var5 = MethodHandleNatives.objectFieldOffset(var2);

            assert var5 == (long)((int)var5);

            return new DirectMethodHandle.Accessor(var3, var4, var2, (int)var5);
         }
      }
   }

   static DirectMethodHandle make(Class<?> var0, MemberName var1) {
      byte var2 = var1.getReferenceKind();
      if (var2 == 7) {
         var2 = 5;
      }

      return make(var2, var0, var1);
   }

   static DirectMethodHandle make(MemberName var0) {
      return var0.isConstructor() ? makeAllocator(var0) : make(var0.getDeclaringClass(), var0);
   }

   static DirectMethodHandle make(Method var0) {
      return make(var0.getDeclaringClass(), new MemberName(var0));
   }

   static DirectMethodHandle make(Field var0) {
      return make(var0.getDeclaringClass(), new MemberName(var0));
   }

   private static DirectMethodHandle makeAllocator(MemberName var0) {
      assert var0.isConstructor() && var0.getName().equals("<init>");

      Class var1 = var0.getDeclaringClass();
      var0 = var0.asConstructor();

      assert var0.isConstructor() && var0.getReferenceKind() == 8 : var0;

      MethodType var2 = var0.getMethodType().changeReturnType(var1);
      LambdaForm var3 = preparedLambdaForm(var0);
      MemberName var4 = var0.asSpecial();

      assert var4.getMethodType().returnType() == Void.TYPE;

      return new DirectMethodHandle.Constructor(var2, var3, var0, var4, var1);
   }

   BoundMethodHandle rebind() {
      return BoundMethodHandle.makeReinvoker(this);
   }

   MethodHandle copyWith(MethodType var1, LambdaForm var2) {
      assert this.getClass() == DirectMethodHandle.class;

      return new DirectMethodHandle(var1, var2, this.member);
   }

   String internalProperties() {
      return "\n& DMH.MN=" + this.internalMemberName();
   }

   @ForceInline
   MemberName internalMemberName() {
      return this.member;
   }

   private static LambdaForm preparedLambdaForm(MemberName var0) {
      assert var0.isInvocable() : var0;

      MethodType var1 = var0.getInvocationType().basicType();

      assert !var0.isMethodHandleInvoke() : var0;

      byte var2;
      switch(var0.getReferenceKind()) {
      case 5:
         var2 = 0;
         break;
      case 6:
         var2 = 1;
         break;
      case 7:
         var2 = 2;
         break;
      case 8:
         var2 = 3;
         break;
      case 9:
         var2 = 4;
         break;
      default:
         throw new InternalError(var0.toString());
      }

      if (var2 == 1 && shouldBeInitialized(var0)) {
         preparedLambdaForm(var1, var2);
         var2 = 5;
      }

      LambdaForm var3 = preparedLambdaForm(var1, var2);
      maybeCompile(var3, var0);

      assert var3.methodType().dropParameterTypes(0, 1).equals((Object)var0.getInvocationType().basicType()) : Arrays.asList(var0, var0.getInvocationType().basicType(), var3, var3.methodType());

      return var3;
   }

   private static LambdaForm preparedLambdaForm(MethodType var0, int var1) {
      LambdaForm var2 = var0.form().cachedLambdaForm(var1);
      if (var2 != null) {
         return var2;
      } else {
         var2 = makePreparedLambdaForm(var0, var1);
         return var0.form().setCachedLambdaForm(var1, var2);
      }
   }

   private static LambdaForm makePreparedLambdaForm(MethodType var0, int var1) {
      boolean var2 = var1 == 5;
      boolean var3 = var1 == 3;
      boolean var4 = var1 == 4;
      String var5;
      String var6;
      switch(var1) {
      case 0:
         var5 = "linkToVirtual";
         var6 = "DMH.invokeVirtual";
         break;
      case 1:
         var5 = "linkToStatic";
         var6 = "DMH.invokeStatic";
         break;
      case 2:
         var5 = "linkToSpecial";
         var6 = "DMH.invokeSpecial";
         break;
      case 3:
         var5 = "linkToSpecial";
         var6 = "DMH.newInvokeSpecial";
         break;
      case 4:
         var5 = "linkToInterface";
         var6 = "DMH.invokeInterface";
         break;
      case 5:
         var5 = "linkToStatic";
         var6 = "DMH.invokeStaticInit";
         break;
      default:
         throw new InternalError("which=" + var1);
      }

      MethodType var7 = var0.appendParameterTypes(MemberName.class);
      if (var3) {
         var7 = var7.insertParameterTypes(0, (Class[])(Object.class)).changeReturnType(Void.TYPE);
      }

      MemberName var8 = new MemberName(MethodHandle.class, var5, var7, (byte)6);

      try {
         var8 = IMPL_NAMES.resolveOrFail((byte)6, var8, (Class)null, NoSuchMethodException.class);
      } catch (ReflectiveOperationException var21) {
         throw MethodHandleStatics.newInternalError((Throwable)var21);
      }

      int var11 = 1 + var0.parameterCount();
      int var12 = var11;
      int var10000;
      if (var3) {
         var10000 = var11;
         var12 = var11 + 1;
      } else {
         var10000 = -1;
      }

      int var13 = var10000;
      int var14 = var12++;
      int var15 = var4 ? var12++ : -1;
      int var16 = var12++;
      LambdaForm.Name[] var17 = LambdaForm.arguments(var12 - var11, var0.invokerType());

      assert var17.length == var12;

      if (var3) {
         var17[var13] = new LambdaForm.Name(DirectMethodHandle.Lazy.NF_allocateInstance, new Object[]{var17[0]});
         var17[var14] = new LambdaForm.Name(DirectMethodHandle.Lazy.NF_constructorMethod, new Object[]{var17[0]});
      } else if (var2) {
         var17[var14] = new LambdaForm.Name(DirectMethodHandle.Lazy.NF_internalMemberNameEnsureInit, new Object[]{var17[0]});
      } else {
         var17[var14] = new LambdaForm.Name(DirectMethodHandle.Lazy.NF_internalMemberName, new Object[]{var17[0]});
      }

      assert findDirectMethodHandle(var17[var14]) == var17[0];

      Object[] var18 = Arrays.copyOfRange(var17, 1, var14 + 1, Object[].class);
      if (var4) {
         var17[var15] = new LambdaForm.Name(DirectMethodHandle.Lazy.NF_checkReceiver, new Object[]{var17[0], var17[1]});
         var18[0] = var17[var15];
      }

      assert var18[var18.length - 1] == var17[var14];

      int var19 = -2;
      if (var3) {
         assert var18[var18.length - 2] == var17[var13];

         System.arraycopy(var18, 0, var18, 1, var18.length - 2);
         var18[0] = var17[var13];
         var19 = var13;
      }

      var17[var16] = new LambdaForm.Name(var8, var18);
      var6 = var6 + "_" + LambdaForm.shortenSignature(LambdaForm.basicTypeSignature(var0));
      LambdaForm var20 = new LambdaForm(var6, var11, var17, var19);
      var20.compileToBytecode();
      return var20;
   }

   static Object findDirectMethodHandle(LambdaForm.Name var0) {
      if (var0.function != DirectMethodHandle.Lazy.NF_internalMemberName && var0.function != DirectMethodHandle.Lazy.NF_internalMemberNameEnsureInit && var0.function != DirectMethodHandle.Lazy.NF_constructorMethod) {
         return null;
      } else {
         assert var0.arguments.length == 1;

         return var0.arguments[0];
      }
   }

   private static void maybeCompile(LambdaForm var0, MemberName var1) {
      if (VerifyAccess.isSamePackage(var1.getDeclaringClass(), MethodHandle.class)) {
         var0.compileToBytecode();
      }

   }

   @ForceInline
   static Object internalMemberName(Object var0) {
      return ((DirectMethodHandle)var0).member;
   }

   static Object internalMemberNameEnsureInit(Object var0) {
      DirectMethodHandle var1 = (DirectMethodHandle)var0;
      var1.ensureInitialized();
      return var1.member;
   }

   static boolean shouldBeInitialized(MemberName var0) {
      switch(var0.getReferenceKind()) {
      case 2:
      case 4:
      case 6:
      case 8:
         Class var1 = var0.getDeclaringClass();
         if (var1 != ValueConversions.class && var1 != MethodHandleImpl.class && var1 != Invokers.class) {
            if (!VerifyAccess.isSamePackage(MethodHandle.class, var1) && !VerifyAccess.isSamePackage(ValueConversions.class, var1)) {
               return MethodHandleStatics.UNSAFE.shouldBeInitialized(var1);
            }

            if (MethodHandleStatics.UNSAFE.shouldBeInitialized(var1)) {
               MethodHandleStatics.UNSAFE.ensureClassInitialized(var1);
            }

            return false;
         }

         return false;
      case 3:
      case 5:
      case 7:
      default:
         return false;
      }
   }

   private void ensureInitialized() {
      if (checkInitialized(this.member)) {
         if (this.member.isField()) {
            this.updateForm(preparedFieldLambdaForm(this.member));
         } else {
            this.updateForm(preparedLambdaForm(this.member));
         }
      }

   }

   private static boolean checkInitialized(MemberName var0) {
      Class var1 = var0.getDeclaringClass();
      WeakReference var2 = (WeakReference)DirectMethodHandle.EnsureInitialized.INSTANCE.get(var1);
      if (var2 == null) {
         return true;
      } else {
         Thread var3 = (Thread)var2.get();
         if (var3 == Thread.currentThread()) {
            if (MethodHandleStatics.UNSAFE.shouldBeInitialized(var1)) {
               return false;
            }
         } else {
            MethodHandleStatics.UNSAFE.ensureClassInitialized(var1);
         }

         assert !MethodHandleStatics.UNSAFE.shouldBeInitialized(var1);

         DirectMethodHandle.EnsureInitialized.INSTANCE.remove(var1);
         return true;
      }
   }

   static void ensureInitialized(Object var0) {
      ((DirectMethodHandle)var0).ensureInitialized();
   }

   static Object constructorMethod(Object var0) {
      DirectMethodHandle.Constructor var1 = (DirectMethodHandle.Constructor)var0;
      return var1.initMethod;
   }

   static Object allocateInstance(Object var0) throws InstantiationException {
      DirectMethodHandle.Constructor var1 = (DirectMethodHandle.Constructor)var0;
      return MethodHandleStatics.UNSAFE.allocateInstance(var1.instanceClass);
   }

   @ForceInline
   static long fieldOffset(Object var0) {
      return (long)((DirectMethodHandle.Accessor)var0).fieldOffset;
   }

   @ForceInline
   static Object checkBase(Object var0) {
      var0.getClass();
      return var0;
   }

   @ForceInline
   static Object nullCheck(Object var0) {
      var0.getClass();
      return var0;
   }

   @ForceInline
   static Object staticBase(Object var0) {
      return ((DirectMethodHandle.StaticAccessor)var0).staticBase;
   }

   @ForceInline
   static long staticOffset(Object var0) {
      return ((DirectMethodHandle.StaticAccessor)var0).staticOffset;
   }

   @ForceInline
   static Object checkCast(Object var0, Object var1) {
      return ((DirectMethodHandle)var0).checkCast(var1);
   }

   Object checkCast(Object var1) {
      return this.member.getReturnType().cast(var1);
   }

   private static int afIndex(byte var0, boolean var1, int var2) {
      return var0 * FT_LIMIT * 2 + (var1 ? FT_LIMIT : 0) + var2;
   }

   private static int ftypeKind(Class<?> var0) {
      if (var0.isPrimitive()) {
         return Wrapper.forPrimitiveType(var0).ordinal();
      } else {
         return VerifyType.isNullReferenceConversion(Object.class, var0) ? FT_UNCHECKED_REF : FT_CHECKED_REF;
      }
   }

   private static LambdaForm preparedFieldLambdaForm(MemberName var0) {
      Class var1 = var0.getFieldType();
      boolean var2 = var0.isVolatile();
      byte var3;
      switch(var0.getReferenceKind()) {
      case 1:
         var3 = AF_GETFIELD;
         break;
      case 2:
         var3 = AF_GETSTATIC;
         break;
      case 3:
         var3 = AF_PUTFIELD;
         break;
      case 4:
         var3 = AF_PUTSTATIC;
         break;
      default:
         throw new InternalError(var0.toString());
      }

      if (shouldBeInitialized(var0)) {
         preparedFieldLambdaForm(var3, var2, var1);

         assert AF_GETSTATIC_INIT - AF_GETSTATIC == AF_PUTSTATIC_INIT - AF_PUTSTATIC;

         var3 = (byte)(var3 + (AF_GETSTATIC_INIT - AF_GETSTATIC));
      }

      LambdaForm var4 = preparedFieldLambdaForm(var3, var2, var1);
      maybeCompile(var4, var0);

      assert var4.methodType().dropParameterTypes(0, 1).equals((Object)var0.getInvocationType().basicType()) : Arrays.asList(var0, var0.getInvocationType().basicType(), var4, var4.methodType());

      return var4;
   }

   private static LambdaForm preparedFieldLambdaForm(byte var0, boolean var1, Class<?> var2) {
      int var3 = afIndex(var0, var1, ftypeKind(var2));
      LambdaForm var4 = ACCESSOR_FORMS[var3];
      if (var4 != null) {
         return var4;
      } else {
         var4 = makePreparedFieldLambdaForm(var0, var1, ftypeKind(var2));
         ACCESSOR_FORMS[var3] = var4;
         return var4;
      }
   }

   private static LambdaForm makePreparedFieldLambdaForm(byte var0, boolean var1, int var2) {
      boolean var3 = (var0 & 1) == (AF_GETFIELD & 1);
      boolean var4 = var0 >= AF_GETSTATIC;
      boolean var5 = var0 >= AF_GETSTATIC_INIT;
      boolean var6 = var2 == FT_CHECKED_REF;
      Wrapper var7 = var6 ? Wrapper.OBJECT : Wrapper.values()[var2];
      Class var8 = var7.primitiveType();

      assert ftypeKind(var6 ? String.class : var8) == var2;

      String var9 = var7.primitiveSimpleName();
      String var10 = Character.toUpperCase(var9.charAt(0)) + var9.substring(1);
      if (var1) {
         var10 = var10 + "Volatile";
      }

      String var11 = var3 ? "get" : "put";
      String var12 = var11 + var10;
      MethodType var13;
      if (var3) {
         var13 = MethodType.methodType(var8, Object.class, Long.TYPE);
      } else {
         var13 = MethodType.methodType(Void.TYPE, Object.class, Long.TYPE, var8);
      }

      MemberName var14 = new MemberName(Unsafe.class, var12, var13, (byte)5);

      try {
         var14 = IMPL_NAMES.resolveOrFail((byte)5, var14, (Class)null, NoSuchMethodException.class);
      } catch (ReflectiveOperationException var36) {
         throw MethodHandleStatics.newInternalError((Throwable)var36);
      }

      MethodType var15;
      if (var3) {
         var15 = MethodType.methodType(var8);
      } else {
         var15 = MethodType.methodType(Void.TYPE, var8);
      }

      var15 = var15.basicType();
      if (!var4) {
         var15 = var15.insertParameterTypes(0, (Class[])(Object.class));
      }

      int var18 = 1 + var15.parameterCount();
      int var19 = var4 ? -1 : 1;
      int var20 = var3 ? -1 : var18 - 1;
      int var21 = var18;
      int var10000;
      if (var4) {
         var10000 = var18;
         var21 = var18 + 1;
      } else {
         var10000 = -1;
      }

      int var22 = var10000;
      int var23 = var21++;
      int var24 = var19 >= 0 ? var21++ : -1;
      int var25 = var5 ? var21++ : -1;
      int var26 = var6 && !var3 ? var21++ : -1;
      int var27 = var21++;
      int var28 = var6 && var3 ? var21++ : -1;
      int var29 = var21 - 1;
      LambdaForm.Name[] var30 = LambdaForm.arguments(var21 - var18, var15.invokerType());
      if (var5) {
         var30[var25] = new LambdaForm.Name(DirectMethodHandle.Lazy.NF_ensureInitialized, new Object[]{var30[0]});
      }

      if (var6 && !var3) {
         var30[var26] = new LambdaForm.Name(DirectMethodHandle.Lazy.NF_checkCast, new Object[]{var30[0], var30[var20]});
      }

      Object[] var31 = new Object[1 + var13.parameterCount()];

      assert var31.length == (var3 ? 3 : 4);

      var31[0] = MethodHandleStatics.UNSAFE;
      if (var4) {
         var31[1] = var30[var22] = new LambdaForm.Name(DirectMethodHandle.Lazy.NF_staticBase, new Object[]{var30[0]});
         var31[2] = var30[var23] = new LambdaForm.Name(DirectMethodHandle.Lazy.NF_staticOffset, new Object[]{var30[0]});
      } else {
         var31[1] = var30[var24] = new LambdaForm.Name(DirectMethodHandle.Lazy.NF_checkBase, new Object[]{var30[var19]});
         var31[2] = var30[var23] = new LambdaForm.Name(DirectMethodHandle.Lazy.NF_fieldOffset, new Object[]{var30[0]});
      }

      if (!var3) {
         var31[3] = var6 ? var30[var26] : var30[var20];
      }

      Object[] var32 = var31;
      int var33 = var31.length;

      int var34;
      for(var34 = 0; var34 < var33; ++var34) {
         Object var35 = var32[var34];

         assert var35 != null;
      }

      var30[var27] = new LambdaForm.Name(var14, var31);
      if (var6 && var3) {
         var30[var28] = new LambdaForm.Name(DirectMethodHandle.Lazy.NF_checkCast, new Object[]{var30[0], var30[var27]});
      }

      LambdaForm.Name[] var37 = var30;
      var33 = var30.length;

      for(var34 = 0; var34 < var33; ++var34) {
         LambdaForm.Name var40 = var37[var34];

         assert var40 != null;
      }

      String var38 = var4 ? "Static" : "Field";
      String var39 = var12 + var38;
      if (var6) {
         var39 = var39 + "Cast";
      }

      if (var5) {
         var39 = var39 + "Init";
      }

      return new LambdaForm(var39, var18, var30, var29);
   }

   // $FF: synthetic method
   DirectMethodHandle(MethodType var1, LambdaForm var2, MemberName var3, Object var4) {
      this(var1, var2, var3);
   }

   static {
      FT_UNCHECKED_REF = Wrapper.OBJECT.ordinal();
      FT_CHECKED_REF = FT_LAST_WRAPPER + 1;
      FT_LIMIT = FT_LAST_WRAPPER + 2;
      ACCESSOR_FORMS = new LambdaForm[afIndex(AF_LIMIT, false, 0)];
   }

   private static class Lazy {
      static final LambdaForm.NamedFunction NF_internalMemberName;
      static final LambdaForm.NamedFunction NF_internalMemberNameEnsureInit;
      static final LambdaForm.NamedFunction NF_ensureInitialized;
      static final LambdaForm.NamedFunction NF_fieldOffset;
      static final LambdaForm.NamedFunction NF_checkBase;
      static final LambdaForm.NamedFunction NF_staticBase;
      static final LambdaForm.NamedFunction NF_staticOffset;
      static final LambdaForm.NamedFunction NF_checkCast;
      static final LambdaForm.NamedFunction NF_allocateInstance;
      static final LambdaForm.NamedFunction NF_constructorMethod;
      static final LambdaForm.NamedFunction NF_checkReceiver;

      static {
         try {
            LambdaForm.NamedFunction[] var0 = new LambdaForm.NamedFunction[]{NF_internalMemberName = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("internalMemberName", Object.class)), NF_internalMemberNameEnsureInit = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("internalMemberNameEnsureInit", Object.class)), NF_ensureInitialized = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("ensureInitialized", Object.class)), NF_fieldOffset = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("fieldOffset", Object.class)), NF_checkBase = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("checkBase", Object.class)), NF_staticBase = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("staticBase", Object.class)), NF_staticOffset = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("staticOffset", Object.class)), NF_checkCast = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("checkCast", Object.class, Object.class)), NF_allocateInstance = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("allocateInstance", Object.class)), NF_constructorMethod = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("constructorMethod", Object.class)), NF_checkReceiver = new LambdaForm.NamedFunction(new MemberName(DirectMethodHandle.Interface.class.getDeclaredMethod("checkReceiver", Object.class)))};
            LambdaForm.NamedFunction[] var1 = var0;
            int var2 = var0.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               LambdaForm.NamedFunction var4 = var1[var3];

               assert InvokerBytecodeGenerator.isStaticallyInvocable(var4.member) : var4;

               var4.resolve();
            }

         } catch (ReflectiveOperationException var5) {
            throw MethodHandleStatics.newInternalError((Throwable)var5);
         }
      }
   }

   static class StaticAccessor extends DirectMethodHandle {
      private final Class<?> fieldType;
      private final Object staticBase;
      private final long staticOffset;

      private StaticAccessor(MethodType var1, LambdaForm var2, MemberName var3, Object var4, long var5) {
         super(var1, var2, var3, null);
         this.fieldType = var3.getFieldType();
         this.staticBase = var4;
         this.staticOffset = var5;
      }

      Object checkCast(Object var1) {
         return this.fieldType.cast(var1);
      }

      MethodHandle copyWith(MethodType var1, LambdaForm var2) {
         return new DirectMethodHandle.StaticAccessor(var1, var2, this.member, this.staticBase, this.staticOffset);
      }

      // $FF: synthetic method
      StaticAccessor(MethodType var1, LambdaForm var2, MemberName var3, Object var4, long var5, Object var7) {
         this(var1, var2, var3, var4, var5);
      }
   }

   static class Accessor extends DirectMethodHandle {
      final Class<?> fieldType;
      final int fieldOffset;

      private Accessor(MethodType var1, LambdaForm var2, MemberName var3, int var4) {
         super(var1, var2, var3, null);
         this.fieldType = var3.getFieldType();
         this.fieldOffset = var4;
      }

      Object checkCast(Object var1) {
         return this.fieldType.cast(var1);
      }

      MethodHandle copyWith(MethodType var1, LambdaForm var2) {
         return new DirectMethodHandle.Accessor(var1, var2, this.member, this.fieldOffset);
      }

      // $FF: synthetic method
      Accessor(MethodType var1, LambdaForm var2, MemberName var3, int var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }

   static class Constructor extends DirectMethodHandle {
      final MemberName initMethod;
      final Class<?> instanceClass;

      private Constructor(MethodType var1, LambdaForm var2, MemberName var3, MemberName var4, Class<?> var5) {
         super(var1, var2, var3, null);
         this.initMethod = var4;
         this.instanceClass = var5;

         assert var4.isResolved();

      }

      MethodHandle copyWith(MethodType var1, LambdaForm var2) {
         return new DirectMethodHandle.Constructor(var1, var2, this.member, this.initMethod, this.instanceClass);
      }

      // $FF: synthetic method
      Constructor(MethodType var1, LambdaForm var2, MemberName var3, MemberName var4, Class var5, Object var6) {
         this(var1, var2, var3, var4, var5);
      }
   }

   static class Interface extends DirectMethodHandle {
      private final Class<?> refc;

      private Interface(MethodType var1, LambdaForm var2, MemberName var3, Class<?> var4) {
         super(var1, var2, var3, null);

         assert var4.isInterface() : var4;

         this.refc = var4;
      }

      MethodHandle copyWith(MethodType var1, LambdaForm var2) {
         return new DirectMethodHandle.Interface(var1, var2, this.member, this.refc);
      }

      Object checkReceiver(Object var1) {
         if (!this.refc.isInstance(var1)) {
            String var2 = String.format("Class %s does not implement the requested interface %s", var1.getClass().getName(), this.refc.getName());
            throw new IncompatibleClassChangeError(var2);
         } else {
            return var1;
         }
      }

      // $FF: synthetic method
      Interface(MethodType var1, LambdaForm var2, MemberName var3, Class var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }

   static class Special extends DirectMethodHandle {
      private Special(MethodType var1, LambdaForm var2, MemberName var3) {
         super(var1, var2, var3, null);
      }

      boolean isInvokeSpecial() {
         return true;
      }

      MethodHandle copyWith(MethodType var1, LambdaForm var2) {
         return new DirectMethodHandle.Special(var1, var2, this.member);
      }

      // $FF: synthetic method
      Special(MethodType var1, LambdaForm var2, MemberName var3, Object var4) {
         this(var1, var2, var3);
      }
   }

   private static class EnsureInitialized extends ClassValue<WeakReference<Thread>> {
      static final DirectMethodHandle.EnsureInitialized INSTANCE = new DirectMethodHandle.EnsureInitialized();

      protected WeakReference<Thread> computeValue(Class<?> var1) {
         MethodHandleStatics.UNSAFE.ensureClassInitialized(var1);
         return MethodHandleStatics.UNSAFE.shouldBeInitialized(var1) ? new WeakReference(Thread.currentThread()) : null;
      }
   }
}
