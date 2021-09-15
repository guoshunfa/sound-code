package java.lang.invoke;

import java.lang.reflect.Array;
import java.util.Arrays;

class Invokers {
   private final MethodType targetType;
   @Stable
   private final MethodHandle[] invokers = new MethodHandle[3];
   static final int INV_EXACT = 0;
   static final int INV_GENERIC = 1;
   static final int INV_BASIC = 2;
   static final int INV_LIMIT = 3;
   private static final int MH_LINKER_ARG_APPENDED = 1;
   private static final LambdaForm.NamedFunction NF_checkExactType;
   private static final LambdaForm.NamedFunction NF_checkGenericType;
   private static final LambdaForm.NamedFunction NF_getCallSiteTarget;
   private static final LambdaForm.NamedFunction NF_checkCustomized;

   Invokers(MethodType var1) {
      this.targetType = var1;
   }

   MethodHandle exactInvoker() {
      MethodHandle var1 = this.cachedInvoker(0);
      if (var1 != null) {
         return var1;
      } else {
         var1 = this.makeExactOrGeneralInvoker(true);
         return this.setCachedInvoker(0, var1);
      }
   }

   MethodHandle genericInvoker() {
      MethodHandle var1 = this.cachedInvoker(1);
      if (var1 != null) {
         return var1;
      } else {
         var1 = this.makeExactOrGeneralInvoker(false);
         return this.setCachedInvoker(1, var1);
      }
   }

   MethodHandle basicInvoker() {
      MethodHandle var1 = this.cachedInvoker(2);
      if (var1 != null) {
         return var1;
      } else {
         MethodType var2 = this.targetType.basicType();
         if (var2 != this.targetType) {
            return this.setCachedInvoker(2, var2.invokers().basicInvoker());
         } else {
            var1 = var2.form().cachedMethodHandle(0);
            if (var1 == null) {
               MemberName var3 = invokeBasicMethod(var2);
               DirectMethodHandle var4 = DirectMethodHandle.make(var3);

               assert this.checkInvoker(var4);

               var1 = var2.form().setCachedMethodHandle(0, var4);
            }

            return this.setCachedInvoker(2, var1);
         }
      }
   }

   private MethodHandle cachedInvoker(int var1) {
      return this.invokers[var1];
   }

   private synchronized MethodHandle setCachedInvoker(int var1, MethodHandle var2) {
      MethodHandle var3 = this.invokers[var1];
      return var3 != null ? var3 : (this.invokers[var1] = var2);
   }

   private MethodHandle makeExactOrGeneralInvoker(boolean var1) {
      MethodType var2 = this.targetType;
      MethodType var3 = var2.invokerType();
      int var4 = var1 ? 11 : 13;
      LambdaForm var5 = invokeHandleForm(var2, false, var4);
      BoundMethodHandle var6 = BoundMethodHandle.bindSingle(var3, var5, var2);
      String var7 = var1 ? "invokeExact" : "invoke";
      MethodHandle var8 = var6.withInternalMemberName(MemberName.makeMethodHandleInvoke(var7, var2), false);

      assert this.checkInvoker(var8);

      this.maybeCompileToBytecode(var8);
      return var8;
   }

   private void maybeCompileToBytecode(MethodHandle var1) {
      if (this.targetType == this.targetType.erase() && this.targetType.parameterCount() < 10) {
         var1.form.compileToBytecode();
      }

   }

   static MemberName invokeBasicMethod(MethodType var0) {
      assert var0 == var0.basicType();

      try {
         return MethodHandles.Lookup.IMPL_LOOKUP.resolveOrFail((byte)5, MethodHandle.class, "invokeBasic", (MethodType)var0);
      } catch (ReflectiveOperationException var2) {
         throw MethodHandleStatics.newInternalError("JVM cannot find invoker for " + var0, var2);
      }
   }

   private boolean checkInvoker(MethodHandle var1) {
      assert this.targetType.invokerType().equals((Object)var1.type()) : Arrays.asList(this.targetType, this.targetType.invokerType(), var1);

      assert var1.internalMemberName() == null || var1.internalMemberName().getMethodType().equals((Object)this.targetType);

      assert !var1.isVarargsCollector();

      return true;
   }

   MethodHandle spreadInvoker(int var1) {
      int var2 = this.targetType.parameterCount() - var1;
      MethodType var3 = this.targetType;
      Class var4 = impliedRestargType(var3, var1);
      if (var3.parameterSlotCount() <= 253) {
         return this.genericInvoker().asSpreader(var4, var2);
      } else {
         MethodType var5 = var3.replaceParameterTypes(var1, var3.parameterCount(), var4);
         MethodHandle var6 = MethodHandles.invoker(var5);
         MethodHandle var7 = MethodHandles.insertArguments(Invokers.Lazy.MH_asSpreader, 1, var4, var2);
         return MethodHandles.filterArgument(var6, 0, var7);
      }
   }

   private static Class<?> impliedRestargType(MethodType var0, int var1) {
      if (var0.isGeneric()) {
         return Object[].class;
      } else {
         int var2 = var0.parameterCount();
         if (var1 >= var2) {
            return Object[].class;
         } else {
            Class var3 = var0.parameterType(var1);

            for(int var4 = var1 + 1; var4 < var2; ++var4) {
               if (var3 != var0.parameterType(var4)) {
                  throw MethodHandleStatics.newIllegalArgumentException("need homogeneous rest arguments", var0);
               }
            }

            if (var3 == Object.class) {
               return Object[].class;
            } else {
               return Array.newInstance(var3, 0).getClass();
            }
         }
      }
   }

   public String toString() {
      return "Invokers" + this.targetType;
   }

   static MemberName methodHandleInvokeLinkerMethod(String var0, MethodType var1, Object[] var2) {
      byte var5 = -1;
      switch(var0.hashCode()) {
      case -1183693704:
         if (var0.equals("invoke")) {
            var5 = 1;
         }
         break;
      case 941760871:
         if (var0.equals("invokeExact")) {
            var5 = 0;
         }
      }

      byte var3;
      switch(var5) {
      case 0:
         var3 = 10;
         break;
      case 1:
         var3 = 12;
         break;
      default:
         throw new InternalError("not invoker: " + var0);
      }

      LambdaForm var4;
      if (var1.parameterSlotCount() <= 253) {
         var4 = invokeHandleForm(var1, false, var3);
         var2[0] = var1;
      } else {
         var4 = invokeHandleForm(var1, true, var3);
      }

      return var4.vmentry;
   }

   private static LambdaForm invokeHandleForm(MethodType var0, boolean var1, int var2) {
      boolean var3;
      if (!var1) {
         var0 = var0.basicType();
         var3 = true;
      } else {
         var3 = false;
      }

      boolean var4;
      boolean var5;
      String var6;
      switch(var2) {
      case 10:
         var4 = true;
         var5 = false;
         var6 = "invokeExact_MT";
         break;
      case 11:
         var4 = false;
         var5 = false;
         var6 = "exactInvoker";
         break;
      case 12:
         var4 = true;
         var5 = true;
         var6 = "invoke_MT";
         break;
      case 13:
         var4 = false;
         var5 = true;
         var6 = "invoker";
         break;
      default:
         throw new InternalError();
      }

      LambdaForm var7;
      if (var3) {
         var7 = var0.form().cachedLambdaForm(var2);
         if (var7 != null) {
            return var7;
         }
      }

      int var9 = 0 + (var4 ? 0 : 1);
      int var10 = var9 + 1;
      int var11 = var10 + var0.parameterCount();
      int var12 = var11 + (var4 && !var1 ? 1 : 0);
      int var13 = var11;
      int var10000;
      if (var1) {
         var10000 = -1;
      } else {
         var10000 = var11;
         var13 = var11 + 1;
      }

      int var14 = var10000;
      int var15 = var13++;
      int var16 = MethodHandleStatics.CUSTOMIZE_THRESHOLD >= 0 ? var13++ : -1;
      int var17 = var13++;
      MethodType var18 = var0.invokerType();
      if (var4) {
         if (!var1) {
            var18 = var18.appendParameterTypes(MemberName.class);
         }
      } else {
         var18 = var18.invokerType();
      }

      LambdaForm.Name[] var19 = LambdaForm.arguments(var13 - var12, var18);

      assert var19.length == var13 : Arrays.asList(var0, var1, var2, var13, var19.length);

      if (var14 >= var12) {
         assert var19[var14] == null;

         BoundMethodHandle.SpeciesData var20 = BoundMethodHandle.speciesData_L();
         var19[0] = var19[0].withConstraint(var20);
         LambdaForm.NamedFunction var21 = var20.getterFunction(0);
         var19[var14] = new LambdaForm.Name(var21, new Object[]{var19[0]});
      }

      MethodType var23 = var0.basicType();
      Object[] var24 = Arrays.copyOfRange(var19, var9, var11, Object[].class);
      Object var22 = var1 ? var0 : var19[var14];
      if (!var5) {
         var19[var15] = new LambdaForm.Name(NF_checkExactType, new Object[]{var19[var9], var22});
      } else {
         var19[var15] = new LambdaForm.Name(NF_checkGenericType, new Object[]{var19[var9], var22});
         var24[0] = var19[var15];
      }

      if (var16 != -1) {
         var19[var16] = new LambdaForm.Name(NF_checkCustomized, new Object[]{var24[0]});
      }

      var19[var17] = new LambdaForm.Name(var23, var24);
      var7 = new LambdaForm(var6, var12, var19);
      if (var4) {
         var7.compileToBytecode();
      }

      if (var3) {
         var7 = var0.form().setCachedLambdaForm(var2, var7);
      }

      return var7;
   }

   static WrongMethodTypeException newWrongMethodTypeException(MethodType var0, MethodType var1) {
      return new WrongMethodTypeException("expected " + var1 + " but found " + var0);
   }

   @ForceInline
   static void checkExactType(Object var0, Object var1) {
      MethodHandle var2 = (MethodHandle)var0;
      MethodType var3 = (MethodType)var1;
      MethodType var4 = var2.type();
      if (var4 != var3) {
         throw newWrongMethodTypeException(var3, var4);
      }
   }

   @ForceInline
   static Object checkGenericType(Object var0, Object var1) {
      MethodHandle var2 = (MethodHandle)var0;
      MethodType var3 = (MethodType)var1;
      return var2.asType(var3);
   }

   static MemberName linkToCallSiteMethod(MethodType var0) {
      LambdaForm var1 = callSiteForm(var0, false);
      return var1.vmentry;
   }

   static MemberName linkToTargetMethod(MethodType var0) {
      LambdaForm var1 = callSiteForm(var0, true);
      return var1.vmentry;
   }

   private static LambdaForm callSiteForm(MethodType var0, boolean var1) {
      var0 = var0.basicType();
      int var2 = var1 ? 15 : 14;
      LambdaForm var3 = var0.form().cachedLambdaForm(var2);
      if (var3 != null) {
         return var3;
      } else {
         int var5 = 0 + var0.parameterCount();
         int var6 = var5 + 1;
         int var7 = var5 + 1;
         int var9 = var1 ? -1 : var5;
         int var10 = var1 ? var5 : var7++;
         int var11 = var7++;
         MethodType var12 = var0.appendParameterTypes(var1 ? MethodHandle.class : CallSite.class);
         LambdaForm.Name[] var13 = LambdaForm.arguments(var7 - var6, var12);

         assert var13.length == var7;

         assert var13[var5] != null;

         if (!var1) {
            var13[var10] = new LambdaForm.Name(NF_getCallSiteTarget, new Object[]{var13[var9]});
         }

         Object[] var16 = Arrays.copyOfRange(var13, 0, var5 + 1, Object[].class);
         System.arraycopy(var16, 0, var16, 1, var16.length - 1);
         var16[0] = var13[var10];
         var13[var11] = new LambdaForm.Name(var0, var16);
         var3 = new LambdaForm(var1 ? "linkToTargetMethod" : "linkToCallSite", var6, var13);
         var3.compileToBytecode();
         var3 = var0.form().setCachedLambdaForm(var2, var3);
         return var3;
      }
   }

   @ForceInline
   static Object getCallSiteTarget(Object var0) {
      return ((CallSite)var0).getTarget();
   }

   @ForceInline
   static void checkCustomized(Object var0) {
      MethodHandle var1 = (MethodHandle)var0;
      if (var1.form.customized == null) {
         maybeCustomize(var1);
      }

   }

   @DontInline
   static void maybeCustomize(MethodHandle var0) {
      byte var1 = var0.customizationCount;
      if (var1 >= MethodHandleStatics.CUSTOMIZE_THRESHOLD) {
         var0.customize();
      } else {
         var0.customizationCount = (byte)(var1 + 1);
      }

   }

   static {
      try {
         LambdaForm.NamedFunction[] var0 = new LambdaForm.NamedFunction[]{NF_checkExactType = new LambdaForm.NamedFunction(Invokers.class.getDeclaredMethod("checkExactType", Object.class, Object.class)), NF_checkGenericType = new LambdaForm.NamedFunction(Invokers.class.getDeclaredMethod("checkGenericType", Object.class, Object.class)), NF_getCallSiteTarget = new LambdaForm.NamedFunction(Invokers.class.getDeclaredMethod("getCallSiteTarget", Object.class)), NF_checkCustomized = new LambdaForm.NamedFunction(Invokers.class.getDeclaredMethod("checkCustomized", Object.class))};
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

   private static class Lazy {
      private static final MethodHandle MH_asSpreader;

      static {
         try {
            MH_asSpreader = MethodHandles.Lookup.IMPL_LOOKUP.findVirtual(MethodHandle.class, "asSpreader", MethodType.methodType(MethodHandle.class, Class.class, Integer.TYPE));
         } catch (ReflectiveOperationException var1) {
            throw MethodHandleStatics.newInternalError((Throwable)var1);
         }
      }
   }
}
