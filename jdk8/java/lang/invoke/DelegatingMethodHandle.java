package java.lang.invoke;

import java.util.Arrays;

abstract class DelegatingMethodHandle extends MethodHandle {
   static final LambdaForm.NamedFunction NF_getTarget;

   protected DelegatingMethodHandle(MethodHandle var1) {
      this(var1.type(), var1);
   }

   protected DelegatingMethodHandle(MethodType var1, MethodHandle var2) {
      super(var1, chooseDelegatingForm(var2));
   }

   protected DelegatingMethodHandle(MethodType var1, LambdaForm var2) {
      super(var1, var2);
   }

   protected abstract MethodHandle getTarget();

   abstract MethodHandle asTypeUncached(MethodType var1);

   MemberName internalMemberName() {
      return this.getTarget().internalMemberName();
   }

   boolean isInvokeSpecial() {
      return this.getTarget().isInvokeSpecial();
   }

   Class<?> internalCallerClass() {
      return this.getTarget().internalCallerClass();
   }

   MethodHandle copyWith(MethodType var1, LambdaForm var2) {
      throw MethodHandleStatics.newIllegalArgumentException("do not use this");
   }

   String internalProperties() {
      return "\n& Class=" + this.getClass().getSimpleName() + "\n& Target=" + this.getTarget().debugString();
   }

   BoundMethodHandle rebind() {
      return this.getTarget().rebind();
   }

   private static LambdaForm chooseDelegatingForm(MethodHandle var0) {
      return var0 instanceof SimpleMethodHandle ? var0.internalForm() : makeReinvokerForm(var0, 8, DelegatingMethodHandle.class, NF_getTarget);
   }

   static LambdaForm makeReinvokerForm(MethodHandle var0, int var1, Object var2, LambdaForm.NamedFunction var3) {
      String var4;
      switch(var1) {
      case 7:
         var4 = "BMH.reinvoke";
         break;
      case 8:
         var4 = "MH.delegate";
         break;
      default:
         var4 = "MH.reinvoke";
      }

      return makeReinvokerForm(var0, var1, var2, var4, true, var3, (LambdaForm.NamedFunction)null);
   }

   static LambdaForm makeReinvokerForm(MethodHandle var0, int var1, Object var2, String var3, boolean var4, LambdaForm.NamedFunction var5, LambdaForm.NamedFunction var6) {
      MethodType var7 = var0.type().basicType();
      boolean var8 = var1 < 0 || var7.parameterSlotCount() > 253;
      boolean var9 = var6 != null;
      LambdaForm var10;
      if (!var8) {
         var10 = var7.form().cachedLambdaForm(var1);
         if (var10 != null) {
            return var10;
         }
      }

      int var13 = 1 + var7.parameterCount();
      int var14 = var13;
      int var10000;
      if (var9) {
         var10000 = var13;
         var14 = var13 + 1;
      } else {
         var10000 = -1;
      }

      int var15 = var10000;
      int var16 = var8 ? -1 : var14++;
      int var17 = var14++;
      LambdaForm.Name[] var18 = LambdaForm.arguments(var14 - var13, var7.invokerType());

      assert var18.length == var14;

      var18[0] = var18[0].withConstraint(var2);
      if (var9) {
         var18[var15] = new LambdaForm.Name(var6, new Object[]{var18[0]});
      }

      Object[] var19;
      if (var8) {
         var19 = Arrays.copyOfRange(var18, 1, var13, Object[].class);
         var18[var17] = new LambdaForm.Name(var0, var19);
      } else {
         var18[var16] = new LambdaForm.Name(var5, new Object[]{var18[0]});
         var19 = Arrays.copyOfRange(var18, 0, var13, Object[].class);
         var19[0] = var18[var16];
         var18[var17] = new LambdaForm.Name(var7, var19);
      }

      var10 = new LambdaForm(var3, var13, var18, var4);
      if (!var8) {
         var10 = var7.form().setCachedLambdaForm(var1, var10);
      }

      return var10;
   }

   static {
      try {
         NF_getTarget = new LambdaForm.NamedFunction(DelegatingMethodHandle.class.getDeclaredMethod("getTarget"));
      } catch (ReflectiveOperationException var1) {
         throw MethodHandleStatics.newInternalError((Throwable)var1);
      }
   }
}
