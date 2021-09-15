package java.lang.invoke;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

public abstract class MethodHandle {
   private final MethodType type;
   final LambdaForm form;
   MethodHandle asTypeCache;
   byte customizationCount;
   private static final long FORM_OFFSET;

   public MethodType type() {
      return this.type;
   }

   MethodHandle(MethodType var1, LambdaForm var2) {
      var1.getClass();
      var2.getClass();
      this.type = var1;
      this.form = var2.uncustomize();
      this.form.prepare();
   }

   @MethodHandle.PolymorphicSignature
   public final native Object invokeExact(Object... var1) throws Throwable;

   @MethodHandle.PolymorphicSignature
   public final native Object invoke(Object... var1) throws Throwable;

   @MethodHandle.PolymorphicSignature
   final native Object invokeBasic(Object... var1) throws Throwable;

   @MethodHandle.PolymorphicSignature
   static native Object linkToVirtual(Object... var0) throws Throwable;

   @MethodHandle.PolymorphicSignature
   static native Object linkToStatic(Object... var0) throws Throwable;

   @MethodHandle.PolymorphicSignature
   static native Object linkToSpecial(Object... var0) throws Throwable;

   @MethodHandle.PolymorphicSignature
   static native Object linkToInterface(Object... var0) throws Throwable;

   public Object invokeWithArguments(Object... var1) throws Throwable {
      MethodType var2 = MethodType.genericMethodType(var1 == null ? 0 : var1.length);
      return var2.invokers().spreadInvoker(0).invokeExact(this.asType(var2), var1);
   }

   public Object invokeWithArguments(List<?> var1) throws Throwable {
      return this.invokeWithArguments(var1.toArray());
   }

   public MethodHandle asType(MethodType var1) {
      if (var1 == this.type) {
         return this;
      } else {
         MethodHandle var2 = this.asTypeCached(var1);
         return var2 != null ? var2 : this.asTypeUncached(var1);
      }
   }

   private MethodHandle asTypeCached(MethodType var1) {
      MethodHandle var2 = this.asTypeCache;
      return var2 != null && var1 == var2.type ? var2 : null;
   }

   MethodHandle asTypeUncached(MethodType var1) {
      if (!this.type.isConvertibleTo(var1)) {
         throw new WrongMethodTypeException("cannot convert " + this + " to " + var1);
      } else {
         return this.asTypeCache = MethodHandleImpl.makePairwiseConvert(this, var1, true);
      }
   }

   public MethodHandle asSpreader(Class<?> var1, int var2) {
      MethodType var3 = this.asSpreaderChecks(var1, var2);
      int var4 = this.type().parameterCount();
      int var5 = var4 - var2;
      MethodHandle var6 = this.asType(var3);
      BoundMethodHandle var7 = var6.rebind();
      LambdaForm var8 = var7.editor().spreadArgumentsForm(1 + var5, var1, var2);
      MethodType var9 = var3.replaceParameterTypes(var5, var4, var1);
      return var7.copyWith(var9, var8);
   }

   private MethodType asSpreaderChecks(Class<?> var1, int var2) {
      this.spreadArrayChecks(var1, var2);
      int var3 = this.type().parameterCount();
      if (var3 >= var2 && var2 >= 0) {
         Class var4 = var1.getComponentType();
         MethodType var5 = this.type();
         boolean var6 = true;
         boolean var7 = false;

         for(int var8 = var3 - var2; var8 < var3; ++var8) {
            Class var9 = var5.parameterType(var8);
            if (var9 != var4) {
               var6 = false;
               if (!MethodType.canConvert(var4, var9)) {
                  var7 = true;
                  break;
               }
            }
         }

         if (var6) {
            return var5;
         } else {
            MethodType var10 = var5.asSpreaderType(var1, var2);
            if (!var7) {
               return var10;
            } else {
               this.asType(var10);
               throw MethodHandleStatics.newInternalError("should not return", (Throwable)null);
            }
         }
      } else {
         throw MethodHandleStatics.newIllegalArgumentException("bad spread array length");
      }
   }

   private void spreadArrayChecks(Class<?> var1, int var2) {
      Class var3 = var1.getComponentType();
      if (var3 == null) {
         throw MethodHandleStatics.newIllegalArgumentException("not an array type", var1);
      } else {
         if ((var2 & 127) != var2) {
            if ((var2 & 255) != var2) {
               throw MethodHandleStatics.newIllegalArgumentException("array length is not legal", var2);
            }

            assert var2 >= 128;

            if (var3 == Long.TYPE || var3 == Double.TYPE) {
               throw MethodHandleStatics.newIllegalArgumentException("array length is not legal for long[] or double[]", var2);
            }
         }

      }
   }

   public MethodHandle asCollector(Class<?> var1, int var2) {
      this.asCollectorChecks(var1, var2);
      int var3 = this.type().parameterCount() - 1;
      BoundMethodHandle var4 = this.rebind();
      MethodType var5 = this.type().asCollectorType(var1, var2);
      MethodHandle var6 = MethodHandleImpl.varargsArray(var1, var2);
      LambdaForm var7 = var4.editor().collectArgumentArrayForm(1 + var3, var6);
      if (var7 != null) {
         return var4.copyWith(var5, var7);
      } else {
         var7 = var4.editor().collectArgumentsForm(1 + var3, var6.type().basicType());
         return var4.copyWithExtendL(var5, var7, var6);
      }
   }

   boolean asCollectorChecks(Class<?> var1, int var2) {
      this.spreadArrayChecks(var1, var2);
      int var3 = this.type().parameterCount();
      if (var3 != 0) {
         Class var4 = this.type().parameterType(var3 - 1);
         if (var4 == var1) {
            return true;
         }

         if (var4.isAssignableFrom(var1)) {
            return false;
         }
      }

      throw MethodHandleStatics.newIllegalArgumentException("array type not assignable to trailing argument", this, var1);
   }

   public MethodHandle asVarargsCollector(Class<?> var1) {
      var1.getClass();
      boolean var2 = this.asCollectorChecks(var1, 0);
      return this.isVarargsCollector() && var2 ? this : MethodHandleImpl.makeVarargsCollector(this, var1);
   }

   public boolean isVarargsCollector() {
      return false;
   }

   public MethodHandle asFixedArity() {
      assert !this.isVarargsCollector();

      return this;
   }

   public MethodHandle bindTo(Object var1) {
      var1 = this.type.leadingReferenceParameter().cast(var1);
      return this.bindArgumentL(0, var1);
   }

   public String toString() {
      return MethodHandleStatics.DEBUG_METHOD_HANDLE_NAMES ? "MethodHandle" + this.debugString() : this.standardString();
   }

   String standardString() {
      return "MethodHandle" + this.type;
   }

   String debugString() {
      return this.type + " : " + this.internalForm() + this.internalProperties();
   }

   BoundMethodHandle bindArgumentL(int var1, Object var2) {
      return this.rebind().bindArgumentL(var1, var2);
   }

   MethodHandle setVarargs(MemberName var1) throws IllegalAccessException {
      if (!var1.isVarargs()) {
         return this;
      } else {
         Class var2 = this.type().lastParameterType();
         if (var2.isArray()) {
            return MethodHandleImpl.makeVarargsCollector(this, var2);
         } else {
            throw var1.makeAccessException("cannot make variable arity", (Object)null);
         }
      }
   }

   MethodHandle viewAsType(MethodType var1, boolean var2) {
      assert this.viewAsTypeChecks(var1, var2);

      BoundMethodHandle var3 = this.rebind();

      assert !(var3 instanceof DirectMethodHandle);

      return var3.copyWith(var1, var3.form);
   }

   boolean viewAsTypeChecks(MethodType var1, boolean var2) {
      if (var2) {
         assert this.type().isViewableAs(var1, true) : Arrays.asList(this, var1);
      } else {
         assert this.type().basicType().isViewableAs(var1.basicType(), true) : Arrays.asList(this, var1);
      }

      return true;
   }

   LambdaForm internalForm() {
      return this.form;
   }

   MemberName internalMemberName() {
      return null;
   }

   Class<?> internalCallerClass() {
      return null;
   }

   MethodHandleImpl.Intrinsic intrinsicName() {
      return MethodHandleImpl.Intrinsic.NONE;
   }

   MethodHandle withInternalMemberName(MemberName var1, boolean var2) {
      if (var1 != null) {
         return MethodHandleImpl.makeWrappedMember(this, var1, var2);
      } else if (this.internalMemberName() == null) {
         return this;
      } else {
         BoundMethodHandle var3 = this.rebind();

         assert var3.internalMemberName() == null;

         return var3;
      }
   }

   boolean isInvokeSpecial() {
      return false;
   }

   Object internalValues() {
      return null;
   }

   Object internalProperties() {
      return "";
   }

   abstract MethodHandle copyWith(MethodType var1, LambdaForm var2);

   abstract BoundMethodHandle rebind();

   void updateForm(LambdaForm var1) {
      assert var1.customized == null || var1.customized == this;

      if (this.form != var1) {
         var1.prepare();
         MethodHandleStatics.UNSAFE.putObject(this, FORM_OFFSET, var1);
         MethodHandleStatics.UNSAFE.fullFence();
      }
   }

   void customize() {
      if (this.form.customized == null) {
         LambdaForm var1 = this.form.customize(this);
         this.updateForm(var1);
      } else {
         assert this.form.customized == this;
      }

   }

   static {
      MethodHandleImpl.initStatics();

      try {
         FORM_OFFSET = MethodHandleStatics.UNSAFE.objectFieldOffset(MethodHandle.class.getDeclaredField("form"));
      } catch (ReflectiveOperationException var1) {
         throw MethodHandleStatics.newInternalError((Throwable)var1);
      }
   }

   @Target({ElementType.METHOD})
   @Retention(RetentionPolicy.RUNTIME)
   @interface PolymorphicSignature {
   }
}
