package java.lang.invoke;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import sun.invoke.empty.Empty;
import sun.invoke.util.ValueConversions;
import sun.invoke.util.VerifyType;
import sun.invoke.util.Wrapper;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

abstract class MethodHandleImpl {
   private static final int MAX_ARITY;
   private static final Function<MethodHandle, LambdaForm> PRODUCE_BLOCK_INLINING_FORM;
   private static final Function<MethodHandle, LambdaForm> PRODUCE_REINVOKER_FORM;
   static MethodHandle[] FAKE_METHOD_HANDLE_INVOKE;
   private static final Object[] NO_ARGS_ARRAY;
   private static final int FILL_ARRAYS_COUNT = 11;
   private static final int LEFT_ARGS = 10;
   private static final MethodHandle[] FILL_ARRAY_TO_RIGHT;
   private static final ClassValue<MethodHandle[]> TYPED_COLLECTORS;
   static final int MAX_JVM_ARITY = 255;

   static void initStatics() {
      MemberName.Factory.INSTANCE.getClass();
   }

   static MethodHandle makeArrayElementAccessor(Class<?> var0, boolean var1) {
      if (var0 == Object[].class) {
         return var1 ? MethodHandleImpl.ArrayAccessor.OBJECT_ARRAY_SETTER : MethodHandleImpl.ArrayAccessor.OBJECT_ARRAY_GETTER;
      } else if (!var0.isArray()) {
         throw MethodHandleStatics.newIllegalArgumentException("not an array: " + var0);
      } else {
         MethodHandle[] var2 = (MethodHandle[])MethodHandleImpl.ArrayAccessor.TYPED_ACCESSORS.get(var0);
         int var3 = var1 ? 1 : 0;
         MethodHandle var4 = var2[var3];
         if (var4 != null) {
            return var4;
         } else {
            var4 = MethodHandleImpl.ArrayAccessor.getAccessor(var0, var1);
            MethodType var5 = MethodHandleImpl.ArrayAccessor.correctType(var0, var1);
            if (var4.type() != var5) {
               assert var4.type().parameterType(0) == Object[].class;

               assert (var1 ? var4.type().parameterType(2) : var4.type().returnType()) == Object.class;

               assert var1 || var5.parameterType(0).getComponentType() == var5.returnType();

               var4 = var4.viewAsType(var5, false);
            }

            var4 = makeIntrinsic(var4, var1 ? MethodHandleImpl.Intrinsic.ARRAY_STORE : MethodHandleImpl.Intrinsic.ARRAY_LOAD);
            synchronized(var2) {
               if (var2[var3] == null) {
                  var2[var3] = var4;
               } else {
                  var4 = var2[var3];
               }

               return var4;
            }
         }
      }
   }

   static MethodHandle makePairwiseConvert(MethodHandle var0, MethodType var1, boolean var2, boolean var3) {
      MethodType var4 = var0.type();
      return var1 == var4 ? var0 : makePairwiseConvertByEditor(var0, var1, var2, var3);
   }

   private static int countNonNull(Object[] var0) {
      int var1 = 0;
      Object[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object var5 = var2[var4];
         if (var5 != null) {
            ++var1;
         }
      }

      return var1;
   }

   static MethodHandle makePairwiseConvertByEditor(MethodHandle var0, MethodType var1, boolean var2, boolean var3) {
      Object[] var4 = computeValueConversions(var1, var0.type(), var2, var3);
      int var5 = countNonNull(var4);
      if (var5 == 0) {
         return var0.viewAsType(var1, var2);
      } else {
         MethodType var6 = var1.basicType();
         MethodType var7 = var0.type().basicType();
         BoundMethodHandle var8 = var0.rebind();

         for(int var9 = 0; var9 < var4.length - 1; ++var9) {
            Object var10 = var4[var9];
            if (var10 != null) {
               MethodHandle var11;
               if (var10 instanceof Class) {
                  var11 = MethodHandleImpl.Lazy.MH_castReference.bindTo(var10);
               } else {
                  var11 = (MethodHandle)var10;
               }

               Class var12 = var6.parameterType(var9);
               --var5;
               if (var5 == 0) {
                  var7 = var1;
               } else {
                  var7 = var7.changeParameterType(var9, var12);
               }

               LambdaForm var13 = var8.editor().filterArgumentForm(1 + var9, LambdaForm.BasicType.basicType(var12));
               var8 = var8.copyWithExtendL(var7, var13, var11);
               var8 = var8.rebind();
            }
         }

         Object var14 = var4[var4.length - 1];
         if (var14 != null) {
            MethodHandle var15;
            if (var14 instanceof Class) {
               if (var14 == Void.TYPE) {
                  var15 = null;
               } else {
                  var15 = MethodHandleImpl.Lazy.MH_castReference.bindTo(var14);
               }
            } else {
               var15 = (MethodHandle)var14;
            }

            Class var16 = var6.returnType();
            if (!$assertionsDisabled) {
               --var5;
               if (var5 != 0) {
                  throw new AssertionError();
               }
            }

            LambdaForm var17;
            if (var15 != null) {
               var8 = var8.rebind();
               var17 = var8.editor().filterReturnForm(LambdaForm.BasicType.basicType(var16), false);
               var8 = var8.copyWithExtendL(var1, var17, var15);
            } else {
               var17 = var8.editor().filterReturnForm(LambdaForm.BasicType.basicType(var16), true);
               var8 = var8.copyWith(var1, var17);
            }
         }

         assert var5 == 0;

         assert var8.type().equals((Object)var1);

         return var8;
      }
   }

   static MethodHandle makePairwiseConvertIndirect(MethodHandle var0, MethodType var1, boolean var2, boolean var3) {
      assert var0.type().parameterCount() == var1.parameterCount();

      Object[] var4 = computeValueConversions(var1, var0.type(), var2, var3);
      int var5 = var1.parameterCount();
      int var6 = countNonNull(var4);
      boolean var7 = var4[var5] != null;
      boolean var8 = var1.returnType() == Void.TYPE;
      if (var7 && var8) {
         --var6;
         var7 = false;
      }

      int var11 = 1 + var5;
      int var12 = var11 + var6 + 1;
      int var13 = !var7 ? -1 : var12 - 1;
      int var14 = (!var7 ? var12 : var13) - 1;
      int var15 = var8 ? -1 : var12 - 1;
      MethodType var16 = var1.basicType().invokerType();
      LambdaForm.Name[] var17 = LambdaForm.arguments(var12 - var11, var16);
      Object[] var19 = new Object[0 + var5];
      int var20 = var11;

      for(int var21 = 0; var21 < var5; ++var21) {
         Object var22 = var4[var21];
         if (var22 == null) {
            var19[0 + var21] = var17[1 + var21];
         } else {
            LambdaForm.Name var23;
            if (var22 instanceof Class) {
               Class var24 = (Class)var22;
               var23 = new LambdaForm.Name(MethodHandleImpl.Lazy.MH_castReference, new Object[]{var24, var17[1 + var21]});
            } else {
               MethodHandle var29 = (MethodHandle)var22;
               var23 = new LambdaForm.Name(var29, new Object[]{var17[1 + var21]});
            }

            assert var17[var20] == null;

            var17[var20++] = var23;

            assert var19[0 + var21] == null;

            var19[0 + var21] = var23;
         }
      }

      assert var20 == var14;

      var17[var14] = new LambdaForm.Name(var0, var19);
      Object var25 = var4[var5];
      if (!var7) {
         assert var14 == var17.length - 1;
      } else {
         LambdaForm.Name var26;
         if (var25 == Void.TYPE) {
            var26 = new LambdaForm.Name(LambdaForm.constantZero(LambdaForm.BasicType.basicType(var1.returnType())), new Object[0]);
         } else if (var25 instanceof Class) {
            Class var28 = (Class)var25;
            var26 = new LambdaForm.Name(MethodHandleImpl.Lazy.MH_castReference, new Object[]{var28, var17[var14]});
         } else {
            MethodHandle var30 = (MethodHandle)var25;
            if (var30.type().parameterCount() == 0) {
               var26 = new LambdaForm.Name(var30, new Object[0]);
            } else {
               var26 = new LambdaForm.Name(var30, new Object[]{var17[var14]});
            }
         }

         assert var17[var13] == null;

         var17[var13] = var26;

         assert var13 == var17.length - 1;
      }

      LambdaForm var27 = new LambdaForm("convert", var16.parameterCount(), var17, var15);
      return SimpleMethodHandle.make(var1, var27);
   }

   @ForceInline
   static <T, U> T castReference(Class<? extends T> var0, U var1) {
      if (var1 != null && !var0.isInstance(var1)) {
         throw newClassCastException(var0, var1);
      } else {
         return var1;
      }
   }

   private static ClassCastException newClassCastException(Class<?> var0, Object var1) {
      return new ClassCastException("Cannot cast " + var1.getClass().getName() + " to " + var0.getName());
   }

   static Object[] computeValueConversions(MethodType var0, MethodType var1, boolean var2, boolean var3) {
      int var4 = var0.parameterCount();
      Object[] var5 = new Object[var4 + 1];

      for(int var6 = 0; var6 <= var4; ++var6) {
         boolean var7 = var6 == var4;
         Class var8 = var7 ? var1.returnType() : var0.parameterType(var6);
         Class var9 = var7 ? var0.returnType() : var1.parameterType(var6);
         if (!VerifyType.isNullConversion(var8, var9, var2)) {
            var5[var6] = valueConversion(var8, var9, var2, var3);
         }
      }

      return var5;
   }

   static MethodHandle makePairwiseConvert(MethodHandle var0, MethodType var1, boolean var2) {
      return makePairwiseConvert(var0, var1, var2, false);
   }

   static Object valueConversion(Class<?> var0, Class<?> var1, boolean var2, boolean var3) {
      assert !VerifyType.isNullConversion(var0, var1, var2);

      if (var1 == Void.TYPE) {
         return var1;
      } else {
         MethodHandle var4;
         Wrapper var5;
         if (var0.isPrimitive()) {
            if (var0 == Void.TYPE) {
               return Void.TYPE;
            }

            if (var1.isPrimitive()) {
               var4 = ValueConversions.convertPrimitive(var0, var1);
            } else {
               var5 = Wrapper.forPrimitiveType(var0);
               var4 = ValueConversions.boxExact(var5);

               assert var4.type().parameterType(0) == var5.primitiveType();

               assert var4.type().returnType() == var5.wrapperType();

               if (!VerifyType.isNullConversion(var5.wrapperType(), var1, var2)) {
                  MethodType var6 = MethodType.methodType(var1, var0);
                  if (var2) {
                     var4 = var4.asType(var6);
                  } else {
                     var4 = makePairwiseConvert(var4, var6, false);
                  }
               }
            }
         } else {
            if (!var1.isPrimitive()) {
               return var1;
            }

            var5 = Wrapper.forPrimitiveType(var1);
            if (!var3 && var0 != var5.wrapperType()) {
               var4 = var2 ? ValueConversions.unboxWiden(var5) : ValueConversions.unboxCast(var5);
            } else {
               var4 = ValueConversions.unboxExact(var5, var2);
            }
         }

         assert var4.type().parameterCount() <= 1 : "pc" + Arrays.asList(var0.getSimpleName(), var1.getSimpleName(), var4);

         return var4;
      }
   }

   static MethodHandle makeVarargsCollector(MethodHandle var0, Class<?> var1) {
      MethodType var2 = var0.type();
      int var3 = var2.parameterCount() - 1;
      if (var2.parameterType(var3) != var1) {
         var0 = var0.asType(var2.changeParameterType(var3, var1));
      }

      var0 = var0.asFixedArity();
      return new MethodHandleImpl.AsVarargsCollector(var0, var1);
   }

   static MethodHandle makeSpreadArguments(MethodHandle var0, Class<?> var1, int var2, int var3) {
      MethodType var4 = var0.type();

      for(int var5 = 0; var5 < var3; ++var5) {
         Class var6 = VerifyType.spreadArgElementType(var1, var5);
         if (var6 == null) {
            var6 = Object.class;
         }

         var4 = var4.changeParameterType(var2 + var5, var6);
      }

      var0 = var0.asType(var4);
      MethodType var16 = var4.replaceParameterTypes(var2, var2 + var3, var1);
      MethodType var17 = var16.invokerType();
      LambdaForm.Name[] var7 = LambdaForm.arguments(var3 + 2, var17);
      int var8 = var17.parameterCount();
      int[] var9 = new int[var4.parameterCount()];
      int var10 = 0;

      int var11;
      for(var11 = 1; var10 < var4.parameterCount() + 1; ++var11) {
         var17.parameterType(var10);
         if (var10 == var2) {
            MethodHandle var13 = MethodHandles.arrayElementGetter(var1);
            LambdaForm.Name var14 = var7[var11];
            var7[var8++] = new LambdaForm.Name(MethodHandleImpl.Lazy.NF_checkSpreadArgument, new Object[]{var14, var3});

            for(int var15 = 0; var15 < var3; ++var15) {
               var9[var10] = var8;
               var7[var8++] = new LambdaForm.Name(var13, new Object[]{var14, var15});
               ++var10;
            }
         } else if (var10 < var9.length) {
            var9[var10] = var11;
         }

         ++var10;
      }

      assert var8 == var7.length - 1;

      LambdaForm.Name[] var18 = new LambdaForm.Name[var4.parameterCount()];

      for(var11 = 0; var11 < var4.parameterCount(); ++var11) {
         int var12 = var9[var11];
         var18[var11] = var7[var12];
      }

      var7[var7.length - 1] = new LambdaForm.Name(var0, (Object[])var18);
      LambdaForm var19 = new LambdaForm("spread", var17.parameterCount(), var7);
      return SimpleMethodHandle.make(var16, var19);
   }

   static void checkSpreadArgument(Object var0, int var1) {
      if (var0 == null) {
         if (var1 == 0) {
            return;
         }
      } else {
         int var2;
         if (var0 instanceof Object[]) {
            var2 = ((Object[])((Object[])var0)).length;
            if (var2 == var1) {
               return;
            }
         } else {
            var2 = Array.getLength(var0);
            if (var2 == var1) {
               return;
            }
         }
      }

      throw MethodHandleStatics.newIllegalArgumentException("array is not of length " + var1);
   }

   static MethodHandle makeCollectArguments(MethodHandle var0, MethodHandle var1, int var2, boolean var3) {
      MethodType var4 = var0.type();
      MethodType var5 = var1.type();
      int var6 = var5.parameterCount();
      Class var7 = var5.returnType();
      int var8 = var7 == Void.TYPE ? 0 : 1;
      MethodType var9 = var4.dropParameterTypes(var2, var2 + var8);
      if (!var3) {
         var9 = var9.insertParameterTypes(var2, var5.parameterList());
      }

      MethodType var10 = var9.invokerType();
      LambdaForm.Name[] var11 = LambdaForm.arguments(2, (MethodType)var10);
      int var12 = var11.length - 2;
      int var13 = var11.length - 1;
      LambdaForm.Name[] var14 = (LambdaForm.Name[])Arrays.copyOfRange((Object[])var11, 1 + var2, 1 + var2 + var6);
      var11[var12] = new LambdaForm.Name(var1, (Object[])var14);
      LambdaForm.Name[] var15 = new LambdaForm.Name[var4.parameterCount()];
      byte var16 = 1;
      byte var17 = 0;
      System.arraycopy(var11, var16, var15, var17, var2);
      int var20 = var16 + var2;
      int var21 = var17 + var2;
      if (var7 != Void.TYPE) {
         var15[var21++] = var11[var12];
      }

      if (var3) {
         System.arraycopy(var11, var20, var15, var21, var6);
         var21 += var6;
      }

      var20 += var6;
      int var18 = var15.length - var21;
      System.arraycopy(var11, var20, var15, var21, var18);

      assert var20 + var18 == var12;

      var11[var13] = new LambdaForm.Name(var0, (Object[])var15);
      LambdaForm var19 = new LambdaForm("collect", var10.parameterCount(), var11);
      return SimpleMethodHandle.make(var9, var19);
   }

   @LambdaForm.Hidden
   static MethodHandle selectAlternative(boolean var0, MethodHandle var1, MethodHandle var2) {
      return var0 ? var1 : var2;
   }

   @LambdaForm.Hidden
   static boolean profileBoolean(boolean var0, int[] var1) {
      int var2 = var0 ? 1 : 0;

      try {
         var1[var2] = Math.addExact(var1[var2], 1);
      } catch (ArithmeticException var4) {
         var1[var2] /= 2;
      }

      return var0;
   }

   static MethodHandle makeGuardWithTest(MethodHandle var0, MethodHandle var1, MethodHandle var2) {
      MethodType var3 = var1.type();

      assert var0.type().equals((Object)var3.changeReturnType(Boolean.TYPE)) && var2.type().equals((Object)var3);

      MethodType var4 = var3.basicType();
      LambdaForm var5 = makeGuardWithTestForm(var4);

      BoundMethodHandle var6;
      try {
         if (MethodHandleStatics.PROFILE_GWT) {
            int[] var7 = new int[2];
            var6 = BoundMethodHandle.speciesData_LLLL().constructor().invokeBasic(var3, var5, var0, profile(var1), profile(var2), var7);
         } else {
            var6 = BoundMethodHandle.speciesData_LLL().constructor().invokeBasic(var3, var5, var0, profile(var1), profile(var2));
         }
      } catch (Throwable var8) {
         throw MethodHandleStatics.uncaughtException(var8);
      }

      assert var6.type() == var3;

      return var6;
   }

   static MethodHandle profile(MethodHandle var0) {
      return MethodHandleStatics.DONT_INLINE_THRESHOLD >= 0 ? makeBlockInlningWrapper(var0) : var0;
   }

   static MethodHandle makeBlockInlningWrapper(MethodHandle var0) {
      LambdaForm var1 = (LambdaForm)PRODUCE_BLOCK_INLINING_FORM.apply(var0);
      return new MethodHandleImpl.CountingWrapper(var0, var1, PRODUCE_BLOCK_INLINING_FORM, PRODUCE_REINVOKER_FORM, MethodHandleStatics.DONT_INLINE_THRESHOLD);
   }

   static LambdaForm makeGuardWithTestForm(MethodType var0) {
      LambdaForm var1 = var0.form().cachedLambdaForm(17);
      if (var1 != null) {
         return var1;
      } else {
         int var4 = 1 + var0.parameterCount();
         int var5 = var4 + 1;
         int var7 = var5++;
         int var8 = var5++;
         int var9 = MethodHandleStatics.PROFILE_GWT ? var5++ : -1;
         int var10 = var5++;
         int var11 = var9 != -1 ? var5++ : -1;
         int var12 = var5 - 1;
         int var13 = var5++;
         int var14 = var5++;

         assert var14 == var13 + 1;

         MethodType var15 = var0.invokerType();
         LambdaForm.Name[] var16 = LambdaForm.arguments(var5 - var4, var15);
         BoundMethodHandle.SpeciesData var17 = var9 != -1 ? BoundMethodHandle.speciesData_LLLL() : BoundMethodHandle.speciesData_LLL();
         var16[0] = var16[0].withConstraint(var17);
         var16[var4] = new LambdaForm.Name(var17.getterFunction(0), new Object[]{var16[0]});
         var16[var7] = new LambdaForm.Name(var17.getterFunction(1), new Object[]{var16[0]});
         var16[var8] = new LambdaForm.Name(var17.getterFunction(2), new Object[]{var16[0]});
         if (var9 != -1) {
            var16[var9] = new LambdaForm.Name(var17.getterFunction(3), new Object[]{var16[0]});
         }

         Object[] var18 = Arrays.copyOfRange(var16, 0, var4, Object[].class);
         MethodType var19 = var0.changeReturnType(Boolean.TYPE).basicType();
         var18[0] = var16[var4];
         var16[var10] = new LambdaForm.Name(var19, var18);
         if (var11 != -1) {
            var16[var11] = new LambdaForm.Name(MethodHandleImpl.Lazy.NF_profileBoolean, new Object[]{var16[var10], var16[var9]});
         }

         var16[var13] = new LambdaForm.Name(MethodHandleImpl.Lazy.MH_selectAlternative, new Object[]{var16[var12], var16[var7], var16[var8]});
         var18[0] = var16[var13];
         var16[var14] = new LambdaForm.Name(var0, var18);
         var1 = new LambdaForm("guard", var15.parameterCount(), var16, true);
         return var0.form().setCachedLambdaForm(17, var1);
      }
   }

   private static LambdaForm makeGuardWithCatchForm(MethodType var0) {
      MethodType var1 = var0.invokerType();
      LambdaForm var2 = var0.form().cachedLambdaForm(16);
      if (var2 != null) {
         return var2;
      } else {
         int var5 = 1 + var0.parameterCount();
         int var6 = var5 + 1;
         int var8 = var6++;
         int var9 = var6++;
         int var10 = var6++;
         int var11 = var6++;
         int var12 = var6++;
         int var13 = var6++;
         int var14 = var6++;
         LambdaForm.Name[] var15 = LambdaForm.arguments(var6 - var5, var1);
         BoundMethodHandle.SpeciesData var16 = BoundMethodHandle.speciesData_LLLLL();
         var15[0] = var15[0].withConstraint(var16);
         var15[var5] = new LambdaForm.Name(var16.getterFunction(0), new Object[]{var15[0]});
         var15[var8] = new LambdaForm.Name(var16.getterFunction(1), new Object[]{var15[0]});
         var15[var9] = new LambdaForm.Name(var16.getterFunction(2), new Object[]{var15[0]});
         var15[var10] = new LambdaForm.Name(var16.getterFunction(3), new Object[]{var15[0]});
         var15[var11] = new LambdaForm.Name(var16.getterFunction(4), new Object[]{var15[0]});
         MethodType var17 = var0.changeReturnType(Object.class);
         MethodHandle var18 = MethodHandles.basicInvoker(var17);
         Object[] var19 = new Object[var18.type().parameterCount()];
         var19[0] = var15[var10];
         System.arraycopy(var15, 1, var19, 1, var5 - 1);
         var15[var12] = new LambdaForm.Name(makeIntrinsic(var18, MethodHandleImpl.Intrinsic.GUARD_WITH_CATCH), var19);
         Object[] var20 = new Object[]{var15[var5], var15[var8], var15[var9], var15[var12]};
         var15[var13] = new LambdaForm.Name(MethodHandleImpl.Lazy.NF_guardWithCatch, var20);
         MethodHandle var21 = MethodHandles.basicInvoker(MethodType.methodType(var0.rtype(), Object.class));
         Object[] var22 = new Object[]{var15[var11], var15[var13]};
         var15[var14] = new LambdaForm.Name(var21, var22);
         var2 = new LambdaForm("guardWithCatch", var1.parameterCount(), var15);
         return var0.form().setCachedLambdaForm(16, var2);
      }
   }

   static MethodHandle makeGuardWithCatch(MethodHandle var0, Class<? extends Throwable> var1, MethodHandle var2) {
      MethodType var3 = var0.type();
      LambdaForm var4 = makeGuardWithCatchForm(var3.basicType());
      MethodType var5 = var3.changeReturnType(Object[].class);
      MethodHandle var6 = varargsArray(var3.parameterCount()).asType(var5);
      Class var8 = var3.returnType();
      MethodHandle var7;
      if (var8.isPrimitive()) {
         if (var8 == Void.TYPE) {
            var7 = ValueConversions.ignore();
         } else {
            Wrapper var9 = Wrapper.forPrimitiveType(var3.returnType());
            var7 = ValueConversions.unboxExact(var9);
         }
      } else {
         var7 = MethodHandles.identity(Object.class);
      }

      BoundMethodHandle.SpeciesData var13 = BoundMethodHandle.speciesData_LLLLL();

      BoundMethodHandle var10;
      try {
         var10 = var13.constructor().invokeBasic(var3, var4, var0, var1, var2, var6, var7);
      } catch (Throwable var12) {
         throw MethodHandleStatics.uncaughtException(var12);
      }

      assert var10.type() == var3;

      return var10;
   }

   @LambdaForm.Hidden
   static Object guardWithCatch(MethodHandle var0, Class<? extends Throwable> var1, MethodHandle var2, Object... var3) throws Throwable {
      try {
         return var0.asFixedArity().invokeWithArguments(var3);
      } catch (Throwable var5) {
         if (!var1.isInstance(var5)) {
            throw var5;
         } else {
            return var2.asFixedArity().invokeWithArguments(prepend(var5, var3));
         }
      }
   }

   @LambdaForm.Hidden
   private static Object[] prepend(Object var0, Object[] var1) {
      Object[] var2 = new Object[var1.length + 1];
      var2[0] = var0;
      System.arraycopy(var1, 0, var2, 1, var1.length);
      return var2;
   }

   static MethodHandle throwException(MethodType var0) {
      assert Throwable.class.isAssignableFrom(var0.parameterType(0));

      int var1 = var0.parameterCount();
      if (var1 > 1) {
         MethodHandle var2 = throwException(var0.dropParameterTypes(1, var1));
         var2 = MethodHandles.dropArguments(var2, 1, (List)var0.parameterList().subList(1, var1));
         return var2;
      } else {
         return makePairwiseConvert(MethodHandleImpl.Lazy.NF_throwException.resolvedHandle(), var0, false, true);
      }
   }

   static <T extends Throwable> Empty throwException(T var0) throws T {
      throw var0;
   }

   static MethodHandle fakeMethodHandleInvoke(MemberName var0) {
      assert var0.isMethodHandleInvoke();

      String var2 = var0.getName();
      byte var3 = -1;
      switch(var2.hashCode()) {
      case -1183693704:
         if (var2.equals("invoke")) {
            var3 = 0;
         }
         break;
      case 941760871:
         if (var2.equals("invokeExact")) {
            var3 = 1;
         }
      }

      byte var1;
      switch(var3) {
      case 0:
         var1 = 0;
         break;
      case 1:
         var1 = 1;
         break;
      default:
         throw new InternalError(var0.getName());
      }

      MethodHandle var4 = FAKE_METHOD_HANDLE_INVOKE[var1];
      if (var4 != null) {
         return var4;
      } else {
         MethodType var5 = MethodType.methodType(Object.class, UnsupportedOperationException.class, MethodHandle.class, Object[].class);
         var4 = throwException(var5);
         var4 = var4.bindTo(new UnsupportedOperationException("cannot reflectively invoke MethodHandle"));
         if (!var0.getInvocationType().equals((Object)var4.type())) {
            throw new InternalError(var0.toString());
         } else {
            var4 = var4.withInternalMemberName(var0, false);
            var4 = var4.asVarargsCollector(Object[].class);

            assert var0.isVarargs();

            FAKE_METHOD_HANDLE_INVOKE[var1] = var4;
            return var4;
         }
      }
   }

   static MethodHandle bindCaller(MethodHandle var0, Class<?> var1) {
      return MethodHandleImpl.BindCaller.bindCaller(var0, var1);
   }

   static MethodHandle makeWrappedMember(MethodHandle var0, MemberName var1, boolean var2) {
      return (MethodHandle)(var1.equals(var0.internalMemberName()) && var2 == var0.isInvokeSpecial() ? var0 : new MethodHandleImpl.WrappedMember(var0, var0.type(), var1, var2, (Class)null));
   }

   static MethodHandle makeIntrinsic(MethodHandle var0, MethodHandleImpl.Intrinsic var1) {
      return (MethodHandle)(var1 == var0.intrinsicName() ? var0 : new MethodHandleImpl.IntrinsicMethodHandle(var0, var1));
   }

   static MethodHandle makeIntrinsic(MethodType var0, LambdaForm var1, MethodHandleImpl.Intrinsic var2) {
      return new MethodHandleImpl.IntrinsicMethodHandle(SimpleMethodHandle.make(var0, var1), var2);
   }

   private static MethodHandle findCollector(String var0, int var1, Class<?> var2, Class<?>... var3) {
      MethodType var4 = MethodType.genericMethodType(var1).changeReturnType(var2).insertParameterTypes(0, (Class[])var3);

      try {
         return MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MethodHandleImpl.class, var0, var4);
      } catch (ReflectiveOperationException var6) {
         return null;
      }
   }

   private static Object[] makeArray(Object... var0) {
      return var0;
   }

   private static Object[] array() {
      return NO_ARGS_ARRAY;
   }

   private static Object[] array(Object var0) {
      return makeArray(var0);
   }

   private static Object[] array(Object var0, Object var1) {
      return makeArray(var0, var1);
   }

   private static Object[] array(Object var0, Object var1, Object var2) {
      return makeArray(var0, var1, var2);
   }

   private static Object[] array(Object var0, Object var1, Object var2, Object var3) {
      return makeArray(var0, var1, var2, var3);
   }

   private static Object[] array(Object var0, Object var1, Object var2, Object var3, Object var4) {
      return makeArray(var0, var1, var2, var3, var4);
   }

   private static Object[] array(Object var0, Object var1, Object var2, Object var3, Object var4, Object var5) {
      return makeArray(var0, var1, var2, var3, var4, var5);
   }

   private static Object[] array(Object var0, Object var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      return makeArray(var0, var1, var2, var3, var4, var5, var6);
   }

   private static Object[] array(Object var0, Object var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      return makeArray(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   private static Object[] array(Object var0, Object var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      return makeArray(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   private static Object[] array(Object var0, Object var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return makeArray(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   private static MethodHandle[] makeArrays() {
      ArrayList var0 = new ArrayList();

      while(true) {
         MethodHandle var1 = findCollector("array", var0.size(), Object[].class);
         if (var1 == null) {
            assert var0.size() == 11;

            return (MethodHandle[])var0.toArray(new MethodHandle[MAX_ARITY + 1]);
         }

         var1 = makeIntrinsic(var1, MethodHandleImpl.Intrinsic.NEW_ARRAY);
         var0.add(var1);
      }
   }

   private static Object[] fillNewArray(Integer var0, Object[] var1) {
      Object[] var2 = new Object[var0];
      fillWithArguments(var2, 0, var1);
      return var2;
   }

   private static Object[] fillNewTypedArray(Object[] var0, Integer var1, Object[] var2) {
      Object[] var3 = Arrays.copyOf(var0, var1);

      assert var3.getClass() != Object[].class;

      fillWithArguments(var3, 0, var2);
      return var3;
   }

   private static void fillWithArguments(Object[] var0, int var1, Object... var2) {
      System.arraycopy(var2, 0, var0, var1, var2.length);
   }

   private static Object[] fillArray(Integer var0, Object[] var1, Object var2) {
      fillWithArguments(var1, var0, var2);
      return var1;
   }

   private static Object[] fillArray(Integer var0, Object[] var1, Object var2, Object var3) {
      fillWithArguments(var1, var0, var2, var3);
      return var1;
   }

   private static Object[] fillArray(Integer var0, Object[] var1, Object var2, Object var3, Object var4) {
      fillWithArguments(var1, var0, var2, var3, var4);
      return var1;
   }

   private static Object[] fillArray(Integer var0, Object[] var1, Object var2, Object var3, Object var4, Object var5) {
      fillWithArguments(var1, var0, var2, var3, var4, var5);
      return var1;
   }

   private static Object[] fillArray(Integer var0, Object[] var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      fillWithArguments(var1, var0, var2, var3, var4, var5, var6);
      return var1;
   }

   private static Object[] fillArray(Integer var0, Object[] var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      fillWithArguments(var1, var0, var2, var3, var4, var5, var6, var7);
      return var1;
   }

   private static Object[] fillArray(Integer var0, Object[] var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      fillWithArguments(var1, var0, var2, var3, var4, var5, var6, var7, var8);
      return var1;
   }

   private static Object[] fillArray(Integer var0, Object[] var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      fillWithArguments(var1, var0, var2, var3, var4, var5, var6, var7, var8, var9);
      return var1;
   }

   private static Object[] fillArray(Integer var0, Object[] var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      fillWithArguments(var1, var0, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      return var1;
   }

   private static Object[] fillArray(Integer var0, Object[] var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      fillWithArguments(var1, var0, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      return var1;
   }

   private static MethodHandle[] makeFillArrays() {
      ArrayList var0 = new ArrayList();
      var0.add((Object)null);

      while(true) {
         MethodHandle var1 = findCollector("fillArray", var0.size(), Object[].class, Integer.class, Object[].class);
         if (var1 == null) {
            assert var0.size() == 11;

            return (MethodHandle[])var0.toArray(new MethodHandle[0]);
         }

         var0.add(var1);
      }
   }

   private static Object copyAsPrimitiveArray(Wrapper var0, Object... var1) {
      Object var2 = var0.makeArray(var1.length);
      var0.copyArrayUnboxing(var1, 0, var2, 0, var1.length);
      return var2;
   }

   static MethodHandle varargsArray(int var0) {
      MethodHandle var1 = MethodHandleImpl.Lazy.ARRAYS[var0];
      if (var1 != null) {
         return var1;
      } else {
         var1 = findCollector("array", var0, Object[].class);
         if (var1 != null) {
            var1 = makeIntrinsic(var1, MethodHandleImpl.Intrinsic.NEW_ARRAY);
         }

         if (var1 != null) {
            return MethodHandleImpl.Lazy.ARRAYS[var0] = var1;
         } else {
            var1 = buildVarargsArray(MethodHandleImpl.Lazy.MH_fillNewArray, MethodHandleImpl.Lazy.MH_arrayIdentity, var0);

            assert assertCorrectArity(var1, var0);

            var1 = makeIntrinsic(var1, MethodHandleImpl.Intrinsic.NEW_ARRAY);
            return MethodHandleImpl.Lazy.ARRAYS[var0] = var1;
         }
      }
   }

   private static boolean assertCorrectArity(MethodHandle var0, int var1) {
      assert var0.type().parameterCount() == var1 : "arity != " + var1 + ": " + var0;

      return true;
   }

   static <T> T[] identity(T[] var0) {
      return var0;
   }

   private static MethodHandle buildVarargsArray(MethodHandle var0, MethodHandle var1, int var2) {
      int var3 = Math.min(var2, 10);
      int var4 = var2 - var3;
      MethodHandle var5 = var0.bindTo(var2);
      var5 = var5.asCollector(Object[].class, var3);
      MethodHandle var6 = var1;
      if (var4 > 0) {
         MethodHandle var7 = fillToRight(10 + var4);
         if (var1 == MethodHandleImpl.Lazy.MH_arrayIdentity) {
            var6 = var7;
         } else {
            var6 = MethodHandles.collectArguments(var1, 0, var7);
         }
      }

      if (var6 == MethodHandleImpl.Lazy.MH_arrayIdentity) {
         var6 = var5;
      } else {
         var6 = MethodHandles.collectArguments(var6, 0, var5);
      }

      return var6;
   }

   private static MethodHandle fillToRight(int var0) {
      MethodHandle var1 = FILL_ARRAY_TO_RIGHT[var0];
      if (var1 != null) {
         return var1;
      } else {
         var1 = buildFiller(var0);

         assert assertCorrectArity(var1, var0 - 10 + 1);

         return FILL_ARRAY_TO_RIGHT[var0] = var1;
      }
   }

   private static MethodHandle buildFiller(int var0) {
      if (var0 <= 10) {
         return MethodHandleImpl.Lazy.MH_arrayIdentity;
      } else {
         int var2 = var0 % 10;
         int var3 = var0 - var2;
         if (var2 == 0) {
            var2 = 10;
            var3 = var0 - 10;
            if (FILL_ARRAY_TO_RIGHT[var3] == null) {
               for(int var4 = 0; var4 < var3; var4 += 10) {
                  if (var4 > 10) {
                     fillToRight(var4);
                  }
               }
            }
         }

         if (var3 < 10) {
            var3 = 10;
            var2 = var0 - 10;
         }

         assert var2 > 0;

         MethodHandle var6 = fillToRight(var3);
         MethodHandle var5 = MethodHandleImpl.Lazy.FILL_ARRAYS[var2].bindTo(var3);

         assert var6.type().parameterCount() == 1 + var3 - 10;

         assert var5.type().parameterCount() == 1 + var2;

         return var3 == 10 ? var5 : MethodHandles.collectArguments(var5, 0, var6);
      }
   }

   static MethodHandle varargsArray(Class<?> var0, int var1) {
      Class var2 = var0.getComponentType();
      if (var2 == null) {
         throw new IllegalArgumentException("not an array: " + var0);
      } else {
         if (var1 >= 126) {
            int var3 = var1;
            if (var1 <= 254 && var2.isPrimitive()) {
               var3 = var1 * Wrapper.forPrimitiveType(var2).stackSlots();
            }

            if (var3 > 254) {
               throw new IllegalArgumentException("too many arguments: " + var0.getSimpleName() + ", length " + var1);
            }
         }

         if (var2 == Object.class) {
            return varargsArray(var1);
         } else {
            MethodHandle[] var9 = (MethodHandle[])TYPED_COLLECTORS.get(var2);
            MethodHandle var4 = var1 < var9.length ? var9[var1] : null;
            if (var4 != null) {
               return var4;
            } else {
               if (var1 == 0) {
                  Object var5 = Array.newInstance(var0.getComponentType(), 0);
                  var4 = MethodHandles.constant(var0, var5);
               } else if (var2.isPrimitive()) {
                  MethodHandle var10 = MethodHandleImpl.Lazy.MH_fillNewArray;
                  MethodHandle var6 = buildArrayProducer(var0);
                  var4 = buildVarargsArray(var10, var6, var1);
               } else {
                  Class var11 = var0.asSubclass(Object[].class);
                  Object[] var12 = Arrays.copyOf(NO_ARGS_ARRAY, 0, var11);
                  MethodHandle var7 = MethodHandleImpl.Lazy.MH_fillNewTypedArray.bindTo(var12);
                  MethodHandle var8 = MethodHandleImpl.Lazy.MH_arrayIdentity;
                  var4 = buildVarargsArray(var7, var8, var1);
               }

               var4 = var4.asType(MethodType.methodType(var0, Collections.nCopies(var1, var2)));
               var4 = makeIntrinsic(var4, MethodHandleImpl.Intrinsic.NEW_ARRAY);

               assert assertCorrectArity(var4, var1);

               if (var1 < var9.length) {
                  var9[var1] = var4;
               }

               return var4;
            }
         }
      }
   }

   private static MethodHandle buildArrayProducer(Class<?> var0) {
      Class var1 = var0.getComponentType();

      assert var1.isPrimitive();

      return MethodHandleImpl.Lazy.MH_copyAsPrimitiveArray.bindTo(Wrapper.forPrimitiveType(var1));
   }

   static void assertSame(Object var0, Object var1) {
      if (var0 != var1) {
         String var2 = String.format("mh1 != mh2: mh1 = %s (form: %s); mh2 = %s (form: %s)", var0, ((MethodHandle)var0).form, var1, ((MethodHandle)var1).form);
         throw MethodHandleStatics.newInternalError(var2);
      }
   }

   static {
      final Object[] var0 = new Object[]{255};
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            var0[0] = Integer.getInteger(MethodHandleImpl.class.getName() + ".MAX_ARITY", 255);
            return null;
         }
      });
      MAX_ARITY = (Integer)var0[0];
      PRODUCE_BLOCK_INLINING_FORM = new Function<MethodHandle, LambdaForm>() {
         public LambdaForm apply(MethodHandle var1) {
            return DelegatingMethodHandle.makeReinvokerForm(var1, 9, MethodHandleImpl.CountingWrapper.class, "reinvoker.dontInline", false, DelegatingMethodHandle.NF_getTarget, MethodHandleImpl.CountingWrapper.NF_maybeStopCounting);
         }
      };
      PRODUCE_REINVOKER_FORM = new Function<MethodHandle, LambdaForm>() {
         public LambdaForm apply(MethodHandle var1) {
            return DelegatingMethodHandle.makeReinvokerForm(var1, 8, DelegatingMethodHandle.class, DelegatingMethodHandle.NF_getTarget);
         }
      };
      FAKE_METHOD_HANDLE_INVOKE = new MethodHandle[2];
      NO_ARGS_ARRAY = new Object[0];
      FILL_ARRAY_TO_RIGHT = new MethodHandle[MAX_ARITY + 1];
      TYPED_COLLECTORS = new ClassValue<MethodHandle[]>() {
         protected MethodHandle[] computeValue(Class<?> var1) {
            return new MethodHandle[256];
         }
      };
   }

   private static final class IntrinsicMethodHandle extends DelegatingMethodHandle {
      private final MethodHandle target;
      private final MethodHandleImpl.Intrinsic intrinsicName;

      IntrinsicMethodHandle(MethodHandle var1, MethodHandleImpl.Intrinsic var2) {
         super(var1.type(), var1);
         this.target = var1;
         this.intrinsicName = var2;
      }

      protected MethodHandle getTarget() {
         return this.target;
      }

      MethodHandleImpl.Intrinsic intrinsicName() {
         return this.intrinsicName;
      }

      public MethodHandle asTypeUncached(MethodType var1) {
         return this.asTypeCache = this.target.asType(var1);
      }

      String internalProperties() {
         return super.internalProperties() + "\n& Intrinsic=" + this.intrinsicName;
      }

      public MethodHandle asCollector(Class<?> var1, int var2) {
         if (this.intrinsicName == MethodHandleImpl.Intrinsic.IDENTITY) {
            MethodType var3 = this.type().asCollectorType(var1, var2);
            MethodHandle var4 = MethodHandleImpl.varargsArray(var1, var2);
            return var4.asType(var3);
         } else {
            return super.asCollector(var1, var2);
         }
      }
   }

   static enum Intrinsic {
      SELECT_ALTERNATIVE,
      GUARD_WITH_CATCH,
      NEW_ARRAY,
      ARRAY_LOAD,
      ARRAY_STORE,
      IDENTITY,
      ZERO,
      NONE;
   }

   private static final class WrappedMember extends DelegatingMethodHandle {
      private final MethodHandle target;
      private final MemberName member;
      private final Class<?> callerClass;
      private final boolean isInvokeSpecial;

      private WrappedMember(MethodHandle var1, MethodType var2, MemberName var3, boolean var4, Class<?> var5) {
         super(var2, var1);
         this.target = var1;
         this.member = var3;
         this.callerClass = var5;
         this.isInvokeSpecial = var4;
      }

      MemberName internalMemberName() {
         return this.member;
      }

      Class<?> internalCallerClass() {
         return this.callerClass;
      }

      boolean isInvokeSpecial() {
         return this.isInvokeSpecial;
      }

      protected MethodHandle getTarget() {
         return this.target;
      }

      public MethodHandle asTypeUncached(MethodType var1) {
         return this.asTypeCache = this.target.asType(var1);
      }

      // $FF: synthetic method
      WrappedMember(MethodHandle var1, MethodType var2, MemberName var3, boolean var4, Class var5, Object var6) {
         this(var1, var2, var3, var4, var5);
      }
   }

   private static class BindCaller {
      private static ClassValue<MethodHandle> CV_makeInjectedInvoker = new ClassValue<MethodHandle>() {
         protected MethodHandle computeValue(Class<?> var1) {
            return MethodHandleImpl.BindCaller.makeInjectedInvoker(var1);
         }
      };
      private static final MethodHandle MH_checkCallerClass;
      private static final byte[] T_BYTES;

      static MethodHandle bindCaller(MethodHandle var0, Class<?> var1) {
         if (var1 != null && !var1.isArray() && !var1.isPrimitive() && !var1.getName().startsWith("java.") && !var1.getName().startsWith("sun.")) {
            MethodHandle var2 = prepareForInvoker(var0);
            MethodHandle var3 = (MethodHandle)CV_makeInjectedInvoker.get(var1);
            return restoreToType(var3.bindTo(var2), var0, var1);
         } else {
            throw new InternalError();
         }
      }

      private static MethodHandle makeInjectedInvoker(Class<?> var0) {
         Class var1 = MethodHandleStatics.UNSAFE.defineAnonymousClass(var0, T_BYTES, (Object[])null);
         if (var0.getClassLoader() != var1.getClassLoader()) {
            throw new InternalError(var0.getName() + " (CL)");
         } else {
            try {
               if (var0.getProtectionDomain() != var1.getProtectionDomain()) {
                  throw new InternalError(var0.getName() + " (PD)");
               }
            } catch (SecurityException var8) {
            }

            MethodHandle var2;
            try {
               var2 = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(var1, "init", MethodType.methodType(Void.TYPE));
               var2.invokeExact();
            } catch (Throwable var7) {
               throw MethodHandleStatics.uncaughtException(var7);
            }

            try {
               MethodType var3 = MethodType.methodType(Object.class, MethodHandle.class, Object[].class);
               var2 = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(var1, "invoke_V", var3);
            } catch (ReflectiveOperationException var6) {
               throw MethodHandleStatics.uncaughtException(var6);
            }

            try {
               MethodHandle var9 = prepareForInvoker(MH_checkCallerClass);
               var2.invokeExact(var9, new Object[]{var0, var1});
               return var2;
            } catch (Throwable var5) {
               throw new InternalError(var5);
            }
         }
      }

      private static MethodHandle prepareForInvoker(MethodHandle var0) {
         var0 = var0.asFixedArity();
         MethodType var1 = var0.type();
         int var2 = var1.parameterCount();
         MethodHandle var3 = var0.asType(var1.generic());
         var3.internalForm().compileToBytecode();
         var3 = var3.asSpreader(Object[].class, var2);
         var3.internalForm().compileToBytecode();
         return var3;
      }

      private static MethodHandle restoreToType(MethodHandle var0, MethodHandle var1, Class<?> var2) {
         MethodType var3 = var1.type();
         MethodHandle var4 = var0.asCollector(Object[].class, var3.parameterCount());
         MemberName var5 = var1.internalMemberName();
         var4 = var4.asType(var3);
         MethodHandleImpl.WrappedMember var6 = new MethodHandleImpl.WrappedMember(var4, var3, var5, var1.isInvokeSpecial(), var2);
         return var6;
      }

      @CallerSensitive
      private static boolean checkCallerClass(Class<?> var0, Class<?> var1) {
         Class var2 = Reflection.getCallerClass();
         if (var2 != var0 && var2 != var1) {
            throw new InternalError("found " + var2.getName() + ", expected " + var0.getName() + (var0 == var1 ? "" : ", or else " + var1.getName()));
         } else {
            return true;
         }
      }

      static {
         Class var0 = MethodHandleImpl.BindCaller.class;

         assert checkCallerClass(var0, var0);

         try {
            MH_checkCallerClass = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(var0, "checkCallerClass", MethodType.methodType(Boolean.TYPE, Class.class, Class.class));

            assert MH_checkCallerClass.invokeExact(var0, var0);
         } catch (Throwable var2) {
            throw new InternalError(var2);
         }

         final Object[] var3 = new Object[]{null};
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               try {
                  Class var1 = MethodHandleImpl.BindCaller.T.class;
                  String var2 = var1.getName();
                  String var3x = var2.substring(var2.lastIndexOf(46) + 1) + ".class";
                  URLConnection var4 = var1.getResource(var3x).openConnection();
                  int var5 = var4.getContentLength();
                  byte[] var6 = new byte[var5];
                  InputStream var7 = var4.getInputStream();
                  Throwable var8 = null;

                  try {
                     int var9 = var7.read(var6);
                     if (var9 != var5) {
                        throw new IOException(var3x);
                     }
                  } catch (Throwable var18) {
                     var8 = var18;
                     throw var18;
                  } finally {
                     if (var7 != null) {
                        if (var8 != null) {
                           try {
                              var7.close();
                           } catch (Throwable var17) {
                              var8.addSuppressed(var17);
                           }
                        } else {
                           var7.close();
                        }
                     }

                  }

                  var3[0] = var6;
                  return null;
               } catch (IOException var20) {
                  throw new InternalError(var20);
               }
            }
         });
         T_BYTES = (byte[])((byte[])var3[0]);
      }

      private static class T {
         static void init() {
         }

         static Object invoke_V(MethodHandle var0, Object[] var1) throws Throwable {
            return var0.invokeExact(var1);
         }
      }
   }

   static class CountingWrapper extends DelegatingMethodHandle {
      private final MethodHandle target;
      private int count;
      private Function<MethodHandle, LambdaForm> countingFormProducer;
      private Function<MethodHandle, LambdaForm> nonCountingFormProducer;
      private volatile boolean isCounting;
      static final LambdaForm.NamedFunction NF_maybeStopCounting;

      private CountingWrapper(MethodHandle var1, LambdaForm var2, Function<MethodHandle, LambdaForm> var3, Function<MethodHandle, LambdaForm> var4, int var5) {
         super(var1.type(), var2);
         this.target = var1;
         this.count = var5;
         this.countingFormProducer = var3;
         this.nonCountingFormProducer = var4;
         this.isCounting = var5 > 0;
      }

      @LambdaForm.Hidden
      protected MethodHandle getTarget() {
         return this.target;
      }

      public MethodHandle asTypeUncached(MethodType var1) {
         MethodHandle var2 = this.target.asType(var1);
         Object var3;
         if (this.isCounting) {
            LambdaForm var4 = (LambdaForm)this.countingFormProducer.apply(var2);
            var3 = new MethodHandleImpl.CountingWrapper(var2, var4, this.countingFormProducer, this.nonCountingFormProducer, MethodHandleStatics.DONT_INLINE_THRESHOLD);
         } else {
            var3 = var2;
         }

         return this.asTypeCache = (MethodHandle)var3;
      }

      boolean countDown() {
         if (this.count <= 0) {
            if (this.isCounting) {
               this.isCounting = false;
               return true;
            } else {
               return false;
            }
         } else {
            --this.count;
            return false;
         }
      }

      @LambdaForm.Hidden
      static void maybeStopCounting(Object var0) {
         MethodHandleImpl.CountingWrapper var1 = (MethodHandleImpl.CountingWrapper)var0;
         if (var1.countDown()) {
            LambdaForm var2 = (LambdaForm)var1.nonCountingFormProducer.apply(var1.target);
            var2.compileToBytecode();
            var1.updateForm(var2);
         }

      }

      // $FF: synthetic method
      CountingWrapper(MethodHandle var1, LambdaForm var2, Function var3, Function var4, int var5, Object var6) {
         this(var1, var2, var3, var4, var5);
      }

      static {
         Class var0 = MethodHandleImpl.CountingWrapper.class;

         try {
            NF_maybeStopCounting = new LambdaForm.NamedFunction(var0.getDeclaredMethod("maybeStopCounting", Object.class));
         } catch (ReflectiveOperationException var2) {
            throw MethodHandleStatics.newInternalError((Throwable)var2);
         }
      }
   }

   static class Lazy {
      private static final Class<?> MHI = MethodHandleImpl.class;
      private static final MethodHandle[] ARRAYS = MethodHandleImpl.makeArrays();
      private static final MethodHandle[] FILL_ARRAYS = MethodHandleImpl.makeFillArrays();
      static final LambdaForm.NamedFunction NF_checkSpreadArgument;
      static final LambdaForm.NamedFunction NF_guardWithCatch;
      static final LambdaForm.NamedFunction NF_throwException;
      static final LambdaForm.NamedFunction NF_profileBoolean;
      static final MethodHandle MH_castReference;
      static final MethodHandle MH_selectAlternative;
      static final MethodHandle MH_copyAsPrimitiveArray;
      static final MethodHandle MH_fillNewTypedArray;
      static final MethodHandle MH_fillNewArray;
      static final MethodHandle MH_arrayIdentity;

      static {
         try {
            NF_checkSpreadArgument = new LambdaForm.NamedFunction(MHI.getDeclaredMethod("checkSpreadArgument", Object.class, Integer.TYPE));
            NF_guardWithCatch = new LambdaForm.NamedFunction(MHI.getDeclaredMethod("guardWithCatch", MethodHandle.class, Class.class, MethodHandle.class, Object[].class));
            NF_throwException = new LambdaForm.NamedFunction(MHI.getDeclaredMethod("throwException", Throwable.class));
            NF_profileBoolean = new LambdaForm.NamedFunction(MHI.getDeclaredMethod("profileBoolean", Boolean.TYPE, int[].class));
            NF_checkSpreadArgument.resolve();
            NF_guardWithCatch.resolve();
            NF_throwException.resolve();
            NF_profileBoolean.resolve();
            MH_castReference = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "castReference", MethodType.methodType(Object.class, Class.class, Object.class));
            MH_copyAsPrimitiveArray = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "copyAsPrimitiveArray", MethodType.methodType(Object.class, Wrapper.class, Object[].class));
            MH_arrayIdentity = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "identity", MethodType.methodType(Object[].class, Object[].class));
            MH_fillNewArray = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "fillNewArray", MethodType.methodType(Object[].class, Integer.class, Object[].class));
            MH_fillNewTypedArray = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "fillNewTypedArray", MethodType.methodType(Object[].class, Object[].class, Integer.class, Object[].class));
            MH_selectAlternative = MethodHandleImpl.makeIntrinsic(MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "selectAlternative", MethodType.methodType(MethodHandle.class, Boolean.TYPE, MethodHandle.class, MethodHandle.class)), MethodHandleImpl.Intrinsic.SELECT_ALTERNATIVE);
         } catch (ReflectiveOperationException var1) {
            throw MethodHandleStatics.newInternalError((Throwable)var1);
         }
      }
   }

   private static final class AsVarargsCollector extends DelegatingMethodHandle {
      private final MethodHandle target;
      private final Class<?> arrayType;
      @Stable
      private MethodHandle asCollectorCache;

      AsVarargsCollector(MethodHandle var1, Class<?> var2) {
         this(var1.type(), var1, var2);
      }

      AsVarargsCollector(MethodType var1, MethodHandle var2, Class<?> var3) {
         super(var1, var2);
         this.target = var2;
         this.arrayType = var3;
         this.asCollectorCache = var2.asCollector(var3, 0);
      }

      public boolean isVarargsCollector() {
         return true;
      }

      protected MethodHandle getTarget() {
         return this.target;
      }

      public MethodHandle asFixedArity() {
         return this.target;
      }

      MethodHandle setVarargs(MemberName var1) {
         return (MethodHandle)(var1.isVarargs() ? this : this.asFixedArity());
      }

      public MethodHandle asTypeUncached(MethodType var1) {
         MethodType var2 = this.type();
         int var3 = var2.parameterCount() - 1;
         int var4 = var1.parameterCount();
         if (var4 == var3 + 1 && var2.parameterType(var3).isAssignableFrom(var1.parameterType(var3))) {
            return this.asTypeCache = this.asFixedArity().asType(var1);
         } else {
            MethodHandle var5 = this.asCollectorCache;
            if (var5 != null && var5.type().parameterCount() == var4) {
               return this.asTypeCache = var5.asType(var1);
            } else {
               int var6 = var4 - var3;

               MethodHandle var7;
               try {
                  var7 = this.asFixedArity().asCollector(this.arrayType, var6);

                  assert var7.type().parameterCount() == var4 : "newArity=" + var4 + " but collector=" + var7;
               } catch (IllegalArgumentException var9) {
                  throw new WrongMethodTypeException("cannot build collector", var9);
               }

               this.asCollectorCache = var7;
               return this.asTypeCache = var7.asType(var1);
            }
         }
      }

      boolean viewAsTypeChecks(MethodType var1, boolean var2) {
         super.viewAsTypeChecks(var1, true);
         if (var2) {
            return true;
         } else {
            assert this.type().lastParameterType().getComponentType().isAssignableFrom(var1.lastParameterType().getComponentType()) : Arrays.asList(this, var1);

            return true;
         }
      }
   }

   static final class ArrayAccessor {
      static final int GETTER_INDEX = 0;
      static final int SETTER_INDEX = 1;
      static final int INDEX_LIMIT = 2;
      static final ClassValue<MethodHandle[]> TYPED_ACCESSORS = new ClassValue<MethodHandle[]>() {
         protected MethodHandle[] computeValue(Class<?> var1) {
            return new MethodHandle[2];
         }
      };
      static final MethodHandle OBJECT_ARRAY_GETTER;
      static final MethodHandle OBJECT_ARRAY_SETTER;

      static int getElementI(int[] var0, int var1) {
         return var0[var1];
      }

      static long getElementJ(long[] var0, int var1) {
         return var0[var1];
      }

      static float getElementF(float[] var0, int var1) {
         return var0[var1];
      }

      static double getElementD(double[] var0, int var1) {
         return var0[var1];
      }

      static boolean getElementZ(boolean[] var0, int var1) {
         return var0[var1];
      }

      static byte getElementB(byte[] var0, int var1) {
         return var0[var1];
      }

      static short getElementS(short[] var0, int var1) {
         return var0[var1];
      }

      static char getElementC(char[] var0, int var1) {
         return var0[var1];
      }

      static Object getElementL(Object[] var0, int var1) {
         return var0[var1];
      }

      static void setElementI(int[] var0, int var1, int var2) {
         var0[var1] = var2;
      }

      static void setElementJ(long[] var0, int var1, long var2) {
         var0[var1] = var2;
      }

      static void setElementF(float[] var0, int var1, float var2) {
         var0[var1] = var2;
      }

      static void setElementD(double[] var0, int var1, double var2) {
         var0[var1] = var2;
      }

      static void setElementZ(boolean[] var0, int var1, boolean var2) {
         var0[var1] = var2;
      }

      static void setElementB(byte[] var0, int var1, byte var2) {
         var0[var1] = var2;
      }

      static void setElementS(short[] var0, int var1, short var2) {
         var0[var1] = var2;
      }

      static void setElementC(char[] var0, int var1, char var2) {
         var0[var1] = var2;
      }

      static void setElementL(Object[] var0, int var1, Object var2) {
         var0[var1] = var2;
      }

      static String name(Class<?> var0, boolean var1) {
         Class var2 = var0.getComponentType();
         if (var2 == null) {
            throw MethodHandleStatics.newIllegalArgumentException("not an array", var0);
         } else {
            return (!var1 ? "getElement" : "setElement") + Wrapper.basicTypeChar(var2);
         }
      }

      static MethodType type(Class<?> var0, boolean var1) {
         Class var2 = var0.getComponentType();
         Class var3 = var0;
         if (!var2.isPrimitive()) {
            var3 = Object[].class;
            var2 = Object.class;
         }

         return !var1 ? MethodType.methodType(var2, var3, Integer.TYPE) : MethodType.methodType(Void.TYPE, var3, Integer.TYPE, var2);
      }

      static MethodType correctType(Class<?> var0, boolean var1) {
         Class var2 = var0.getComponentType();
         return !var1 ? MethodType.methodType(var2, var0, Integer.TYPE) : MethodType.methodType(Void.TYPE, var0, Integer.TYPE, var2);
      }

      static MethodHandle getAccessor(Class<?> var0, boolean var1) {
         String var2 = name(var0, var1);
         MethodType var3 = type(var0, var1);

         try {
            return MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MethodHandleImpl.ArrayAccessor.class, var2, var3);
         } catch (ReflectiveOperationException var5) {
            throw MethodHandleStatics.uncaughtException(var5);
         }
      }

      static {
         MethodHandle[] var0 = (MethodHandle[])TYPED_ACCESSORS.get(Object[].class);
         var0[0] = OBJECT_ARRAY_GETTER = MethodHandleImpl.makeIntrinsic(getAccessor(Object[].class, false), MethodHandleImpl.Intrinsic.ARRAY_LOAD);
         var0[1] = OBJECT_ARRAY_SETTER = MethodHandleImpl.makeIntrinsic(getAccessor(Object[].class, true), MethodHandleImpl.Intrinsic.ARRAY_STORE);

         assert InvokerBytecodeGenerator.isStaticallyInvocable(OBJECT_ARRAY_GETTER.internalMemberName());

         assert InvokerBytecodeGenerator.isStaticallyInvocable(OBJECT_ARRAY_SETTER.internalMemberName());

      }
   }
}
