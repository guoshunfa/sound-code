package java.lang.invoke;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import sun.invoke.util.Wrapper;

class LambdaFormEditor {
   final LambdaForm lambdaForm;
   private static final int MIN_CACHE_ARRAY_SIZE = 4;
   private static final int MAX_CACHE_ARRAY_SIZE = 16;

   private LambdaFormEditor(LambdaForm var1) {
      this.lambdaForm = var1;
   }

   static LambdaFormEditor lambdaFormEditor(LambdaForm var0) {
      return new LambdaFormEditor(var0.uncustomize());
   }

   private LambdaForm getInCache(LambdaFormEditor.Transform var1) {
      assert var1.get() == null;

      Object var2 = this.lambdaForm.transformCache;
      LambdaFormEditor.Transform var3 = null;
      if (var2 instanceof ConcurrentHashMap) {
         ConcurrentHashMap var4 = (ConcurrentHashMap)var2;
         var3 = (LambdaFormEditor.Transform)var4.get(var1);
      } else {
         if (var2 == null) {
            return null;
         }

         if (var2 instanceof LambdaFormEditor.Transform) {
            LambdaFormEditor.Transform var7 = (LambdaFormEditor.Transform)var2;
            if (var7.equals(var1)) {
               var3 = var7;
            }
         } else {
            LambdaFormEditor.Transform[] var8 = (LambdaFormEditor.Transform[])((LambdaFormEditor.Transform[])var2);

            for(int var5 = 0; var5 < var8.length; ++var5) {
               LambdaFormEditor.Transform var6 = var8[var5];
               if (var6 == null) {
                  break;
               }

               if (var6.equals(var1)) {
                  var3 = var6;
                  break;
               }
            }
         }
      }

      assert var3 == null || var1.equals(var3);

      return var3 != null ? (LambdaForm)var3.get() : null;
   }

   private LambdaForm putInCache(LambdaFormEditor.Transform var1, LambdaForm var2) {
      var1 = var1.withResult(var2);
      int var3 = 0;

      while(true) {
         Object var4 = this.lambdaForm.transformCache;
         if (var4 instanceof ConcurrentHashMap) {
            ConcurrentHashMap var5 = (ConcurrentHashMap)var4;
            LambdaFormEditor.Transform var18 = (LambdaFormEditor.Transform)var5.putIfAbsent(var1, var1);
            if (var18 == null) {
               return var2;
            }

            LambdaForm var19 = (LambdaForm)var18.get();
            if (var19 != null) {
               return var19;
            }

            if (var5.replace(var1, var18, var1)) {
               return var2;
            }
         } else {
            assert var3 == 0;

            synchronized(this.lambdaForm) {
               var4 = this.lambdaForm.transformCache;
               if (!(var4 instanceof ConcurrentHashMap)) {
                  label141: {
                     if (var4 == null) {
                        this.lambdaForm.transformCache = var1;
                        return var2;
                     }

                     LambdaFormEditor.Transform[] var6;
                     if (var4 instanceof LambdaFormEditor.Transform) {
                        LambdaFormEditor.Transform var7 = (LambdaFormEditor.Transform)var4;
                        if (var7.equals(var1)) {
                           LambdaForm var8 = (LambdaForm)var7.get();
                           if (var8 == null) {
                              this.lambdaForm.transformCache = var1;
                              return var2;
                           }

                           return var8;
                        }

                        if (var7.get() == null) {
                           this.lambdaForm.transformCache = var1;
                           return var2;
                        }

                        var6 = new LambdaFormEditor.Transform[4];
                        var6[0] = var7;
                        this.lambdaForm.transformCache = var6;
                     } else {
                        var6 = (LambdaFormEditor.Transform[])((LambdaFormEditor.Transform[])var4);
                     }

                     int var17 = var6.length;
                     int var20 = -1;

                     int var9;
                     for(var9 = 0; var9 < var17; ++var9) {
                        LambdaFormEditor.Transform var10 = var6[var9];
                        if (var10 == null) {
                           break;
                        }

                        if (var10.equals(var1)) {
                           LambdaForm var11 = (LambdaForm)var10.get();
                           if (var11 == null) {
                              var6[var9] = var1;
                              return var2;
                           }

                           return var11;
                        }

                        if (var20 < 0 && var10.get() == null) {
                           var20 = var9;
                        }
                     }

                     if (var9 >= var17 && var20 < 0) {
                        if (var17 >= 16) {
                           ConcurrentHashMap var22 = new ConcurrentHashMap(32);
                           LambdaFormEditor.Transform[] var23 = var6;
                           int var12 = var6.length;

                           for(int var13 = 0; var13 < var12; ++var13) {
                              LambdaFormEditor.Transform var14 = var23[var13];
                              var22.put(var14, var14);
                           }

                           this.lambdaForm.transformCache = var22;
                           break label141;
                        }

                        var17 = Math.min(var17 * 2, 16);
                        var6 = (LambdaFormEditor.Transform[])Arrays.copyOf((Object[])var6, var17);
                        this.lambdaForm.transformCache = var6;
                     }

                     int var21 = var20 >= 0 ? var20 : var9;
                     var6[var21] = var1;
                     return var2;
                  }
               }
            }
         }

         ++var3;
      }
   }

   private LambdaFormBuffer buffer() {
      return new LambdaFormBuffer(this.lambdaForm);
   }

   private BoundMethodHandle.SpeciesData oldSpeciesData() {
      return BoundMethodHandle.speciesData(this.lambdaForm);
   }

   private BoundMethodHandle.SpeciesData newSpeciesData(LambdaForm.BasicType var1) {
      return this.oldSpeciesData().extendWith(var1);
   }

   BoundMethodHandle bindArgumentL(BoundMethodHandle var1, int var2, Object var3) {
      assert var1.speciesData() == this.oldSpeciesData();

      LambdaForm.BasicType var4 = LambdaForm.BasicType.L_TYPE;
      MethodType var5 = this.bindArgumentType(var1, var2, var4);
      LambdaForm var6 = this.bindArgumentForm(1 + var2);
      return var1.copyWithExtendL(var5, var6, var3);
   }

   BoundMethodHandle bindArgumentI(BoundMethodHandle var1, int var2, int var3) {
      assert var1.speciesData() == this.oldSpeciesData();

      LambdaForm.BasicType var4 = LambdaForm.BasicType.I_TYPE;
      MethodType var5 = this.bindArgumentType(var1, var2, var4);
      LambdaForm var6 = this.bindArgumentForm(1 + var2);
      return var1.copyWithExtendI(var5, var6, var3);
   }

   BoundMethodHandle bindArgumentJ(BoundMethodHandle var1, int var2, long var3) {
      assert var1.speciesData() == this.oldSpeciesData();

      LambdaForm.BasicType var5 = LambdaForm.BasicType.J_TYPE;
      MethodType var6 = this.bindArgumentType(var1, var2, var5);
      LambdaForm var7 = this.bindArgumentForm(1 + var2);
      return var1.copyWithExtendJ(var6, var7, var3);
   }

   BoundMethodHandle bindArgumentF(BoundMethodHandle var1, int var2, float var3) {
      assert var1.speciesData() == this.oldSpeciesData();

      LambdaForm.BasicType var4 = LambdaForm.BasicType.F_TYPE;
      MethodType var5 = this.bindArgumentType(var1, var2, var4);
      LambdaForm var6 = this.bindArgumentForm(1 + var2);
      return var1.copyWithExtendF(var5, var6, var3);
   }

   BoundMethodHandle bindArgumentD(BoundMethodHandle var1, int var2, double var3) {
      assert var1.speciesData() == this.oldSpeciesData();

      LambdaForm.BasicType var5 = LambdaForm.BasicType.D_TYPE;
      MethodType var6 = this.bindArgumentType(var1, var2, var5);
      LambdaForm var7 = this.bindArgumentForm(1 + var2);
      return var1.copyWithExtendD(var6, var7, var3);
   }

   private MethodType bindArgumentType(BoundMethodHandle var1, int var2, LambdaForm.BasicType var3) {
      assert var1.form.uncustomize() == this.lambdaForm;

      assert var1.form.names[1 + var2].type == var3;

      assert LambdaForm.BasicType.basicType(var1.type().parameterType(var2)) == var3;

      return var1.type().dropParameterTypes(var2, var2 + 1);
   }

   LambdaForm bindArgumentForm(int var1) {
      LambdaFormEditor.Transform var2 = LambdaFormEditor.Transform.of(LambdaFormEditor.Transform.Kind.BIND_ARG, var1);
      LambdaForm var3 = this.getInCache(var2);
      if (var3 != null) {
         assert var3.parameterConstraint(0) == this.newSpeciesData(this.lambdaForm.parameterType(var1));

         return var3;
      } else {
         LambdaFormBuffer var4 = this.buffer();
         var4.startEdit();
         BoundMethodHandle.SpeciesData var5 = this.oldSpeciesData();
         BoundMethodHandle.SpeciesData var6 = this.newSpeciesData(this.lambdaForm.parameterType(var1));
         LambdaForm.Name var7 = this.lambdaForm.parameter(0);
         LambdaForm.NamedFunction var9 = var6.getterFunction(var5.fieldCount());
         LambdaForm.Name var8;
         if (var1 != 0) {
            var4.replaceFunctions(var5.getterFunctions(), var6.getterFunctions(), var7);
            var8 = var7.withConstraint(var6);
            var4.renameParameter(0, var8);
            var4.replaceParameterByNewExpression(var1, new LambdaForm.Name(var9, new Object[]{var8}));
         } else {
            assert var5 == BoundMethodHandle.SpeciesData.EMPTY;

            var8 = (new LambdaForm.Name(LambdaForm.BasicType.L_TYPE)).withConstraint(var6);
            var4.replaceParameterByNewExpression(0, new LambdaForm.Name(var9, new Object[]{var8}));
            var4.insertParameter(0, var8);
         }

         var3 = var4.endEdit();
         return this.putInCache(var2, var3);
      }
   }

   LambdaForm addArgumentForm(int var1, LambdaForm.BasicType var2) {
      LambdaFormEditor.Transform var3 = LambdaFormEditor.Transform.of(LambdaFormEditor.Transform.Kind.ADD_ARG, var1, var2.ordinal());
      LambdaForm var4 = this.getInCache(var3);
      if (var4 != null) {
         assert var4.arity == this.lambdaForm.arity + 1;

         assert var4.parameterType(var1) == var2;

         return var4;
      } else {
         LambdaFormBuffer var5 = this.buffer();
         var5.startEdit();
         var5.insertParameter(var1, new LambdaForm.Name(var2));
         var4 = var5.endEdit();
         return this.putInCache(var3, var4);
      }
   }

   LambdaForm dupArgumentForm(int var1, int var2) {
      LambdaFormEditor.Transform var3 = LambdaFormEditor.Transform.of(LambdaFormEditor.Transform.Kind.DUP_ARG, var1, var2);
      LambdaForm var4 = this.getInCache(var3);
      if (var4 != null) {
         assert var4.arity == this.lambdaForm.arity - 1;

         return var4;
      } else {
         LambdaFormBuffer var5 = this.buffer();
         var5.startEdit();

         assert this.lambdaForm.parameter(var1).constraint == null;

         assert this.lambdaForm.parameter(var2).constraint == null;

         var5.replaceParameterByCopy(var2, var1);
         var4 = var5.endEdit();
         return this.putInCache(var3, var4);
      }
   }

   LambdaForm spreadArgumentsForm(int var1, Class<?> var2, int var3) {
      Class var4 = var2.getComponentType();
      Class var5 = var2;
      if (!var4.isPrimitive()) {
         var5 = Object[].class;
      }

      LambdaForm.BasicType var6 = LambdaForm.BasicType.basicType(var4);
      int var7 = var6.ordinal();
      if (var6.basicTypeClass() != var4 && var4.isPrimitive()) {
         var7 = LambdaForm.BasicType.TYPE_LIMIT + Wrapper.forPrimitiveType(var4).ordinal();
      }

      LambdaFormEditor.Transform var8 = LambdaFormEditor.Transform.of(LambdaFormEditor.Transform.Kind.SPREAD_ARGS, var1, var7, var3);
      LambdaForm var9 = this.getInCache(var8);
      if (var9 != null) {
         assert var9.arity == this.lambdaForm.arity - var3 + 1;

         return var9;
      } else {
         LambdaFormBuffer var10 = this.buffer();
         var10.startEdit();

         assert var1 <= 255;

         assert var1 + var3 <= this.lambdaForm.arity;

         assert var1 > 0;

         LambdaForm.Name var11 = new LambdaForm.Name(LambdaForm.BasicType.L_TYPE);
         LambdaForm.Name var12 = new LambdaForm.Name(MethodHandleImpl.Lazy.NF_checkSpreadArgument, new Object[]{var11, var3});
         int var13 = this.lambdaForm.arity();
         var10.insertExpression(var13++, var12);
         MethodHandle var14 = MethodHandles.arrayElementGetter(var5);

         for(int var15 = 0; var15 < var3; ++var15) {
            LambdaForm.Name var16 = new LambdaForm.Name(var14, new Object[]{var11, var15});
            var10.insertExpression(var13 + var15, var16);
            var10.replaceParameterByCopy(var1 + var15, var13 + var15);
         }

         var10.insertParameter(var1, var11);
         var9 = var10.endEdit();
         return this.putInCache(var8, var9);
      }
   }

   LambdaForm collectArgumentsForm(int var1, MethodType var2) {
      int var3 = var2.parameterCount();
      boolean var4 = var2.returnType() == Void.TYPE;
      if (var3 == 1 && !var4) {
         return this.filterArgumentForm(var1, LambdaForm.BasicType.basicType(var2.parameterType(0)));
      } else {
         LambdaForm.BasicType[] var5 = LambdaForm.BasicType.basicTypes(var2.parameterList());
         LambdaFormEditor.Transform.Kind var6 = var4 ? LambdaFormEditor.Transform.Kind.COLLECT_ARGS_TO_VOID : LambdaFormEditor.Transform.Kind.COLLECT_ARGS;
         if (var4 && var3 == 0) {
            var1 = 1;
         }

         LambdaFormEditor.Transform var7 = LambdaFormEditor.Transform.of(var6, var1, var3, LambdaForm.BasicType.basicTypesOrd(var5));
         LambdaForm var8 = this.getInCache(var7);
         if (var8 != null) {
            assert var8.arity == this.lambdaForm.arity - (var4 ? 0 : 1) + var3;

            return var8;
         } else {
            var8 = this.makeArgumentCombinationForm(var1, var2, false, var4);
            return this.putInCache(var7, var8);
         }
      }
   }

   LambdaForm collectArgumentArrayForm(int var1, MethodHandle var2) {
      MethodType var3 = var2.type();
      int var4 = var3.parameterCount();

      assert var2.intrinsicName() == MethodHandleImpl.Intrinsic.NEW_ARRAY;

      Class var5 = var3.returnType();
      Class var6 = var5.getComponentType();
      LambdaForm.BasicType var7 = LambdaForm.BasicType.basicType(var6);
      int var8 = var7.ordinal();
      if (var7.basicTypeClass() != var6) {
         if (!var6.isPrimitive()) {
            return null;
         }

         var8 = LambdaForm.BasicType.TYPE_LIMIT + Wrapper.forPrimitiveType(var6).ordinal();
      }

      assert var3.parameterList().equals(Collections.nCopies(var4, var6));

      LambdaFormEditor.Transform.Kind var9 = LambdaFormEditor.Transform.Kind.COLLECT_ARGS_TO_ARRAY;
      LambdaFormEditor.Transform var10 = LambdaFormEditor.Transform.of(var9, var1, var4, var8);
      LambdaForm var11 = this.getInCache(var10);
      if (var11 != null) {
         assert var11.arity == this.lambdaForm.arity - 1 + var4;

         return var11;
      } else {
         LambdaFormBuffer var12 = this.buffer();
         var12.startEdit();

         assert var1 + 1 <= this.lambdaForm.arity;

         assert var1 > 0;

         LambdaForm.Name[] var13 = new LambdaForm.Name[var4];

         for(int var14 = 0; var14 < var4; ++var14) {
            var13[var14] = new LambdaForm.Name(var1 + var14, var7);
         }

         LambdaForm.Name var21 = new LambdaForm.Name(var2, (Object[])var13);
         int var15 = this.lambdaForm.arity();
         var12.insertExpression(var15, var21);
         int var16 = var1 + 1;
         LambdaForm.Name[] var17 = var13;
         int var18 = var13.length;

         for(int var19 = 0; var19 < var18; ++var19) {
            LambdaForm.Name var20 = var17[var19];
            var12.insertParameter(var16++, var20);
         }

         assert var12.lastIndexOf(var21) == var15 + var13.length;

         var12.replaceParameterByCopy(var1, var15 + var13.length);
         var11 = var12.endEdit();
         return this.putInCache(var10, var11);
      }
   }

   LambdaForm filterArgumentForm(int var1, LambdaForm.BasicType var2) {
      LambdaFormEditor.Transform var3 = LambdaFormEditor.Transform.of(LambdaFormEditor.Transform.Kind.FILTER_ARG, var1, var2.ordinal());
      LambdaForm var4 = this.getInCache(var3);
      if (var4 != null) {
         assert var4.arity == this.lambdaForm.arity;

         assert var4.parameterType(var1) == var2;

         return var4;
      } else {
         LambdaForm.BasicType var5 = this.lambdaForm.parameterType(var1);
         MethodType var6 = MethodType.methodType(var5.basicTypeClass(), var2.basicTypeClass());
         var4 = this.makeArgumentCombinationForm(var1, var6, false, false);
         return this.putInCache(var3, var4);
      }
   }

   private LambdaForm makeArgumentCombinationForm(int var1, MethodType var2, boolean var3, boolean var4) {
      LambdaFormBuffer var5 = this.buffer();
      var5.startEdit();
      int var6 = var2.parameterCount();
      int var7 = var4 ? 0 : 1;

      assert var1 <= 255;

      assert var1 + var7 + (var3 ? var6 : 0) <= this.lambdaForm.arity;

      assert var1 > 0;

      assert var2 == var2.basicType();

      assert var2.returnType() != Void.TYPE || var4;

      BoundMethodHandle.SpeciesData var8 = this.oldSpeciesData();
      BoundMethodHandle.SpeciesData var9 = this.newSpeciesData(LambdaForm.BasicType.L_TYPE);
      LambdaForm.Name var10 = this.lambdaForm.parameter(0);
      var5.replaceFunctions(var8.getterFunctions(), var9.getterFunctions(), var10);
      LambdaForm.Name var11 = var10.withConstraint(var9);
      var5.renameParameter(0, var11);
      LambdaForm.Name var12 = new LambdaForm.Name(var9.getterFunction(var8.fieldCount()), new Object[]{var11});
      Object[] var13 = new Object[1 + var6];
      var13[0] = var12;
      LambdaForm.Name[] var14;
      int var16;
      if (var3) {
         var14 = new LambdaForm.Name[0];
         System.arraycopy(this.lambdaForm.names, var1 + var7, var13, 1, var6);
      } else {
         var14 = new LambdaForm.Name[var6];
         LambdaForm.BasicType[] var15 = LambdaForm.BasicType.basicTypes(var2.parameterList());

         for(var16 = 0; var16 < var15.length; ++var16) {
            var14[var16] = new LambdaForm.Name(var1 + var16, var15[var16]);
         }

         System.arraycopy(var14, 0, var13, 1, var6);
      }

      LambdaForm.Name var22 = new LambdaForm.Name(var2, var13);
      var16 = this.lambdaForm.arity();
      var5.insertExpression(var16 + 0, var12);
      var5.insertExpression(var16 + 1, var22);
      int var17 = var1 + var7;
      LambdaForm.Name[] var18 = var14;
      int var19 = var14.length;

      for(int var20 = 0; var20 < var19; ++var20) {
         LambdaForm.Name var21 = var18[var20];
         var5.insertParameter(var17++, var21);
      }

      assert var5.lastIndexOf(var22) == var16 + 1 + var14.length;

      if (!var4) {
         var5.replaceParameterByCopy(var1, var16 + 1 + var14.length);
      }

      return var5.endEdit();
   }

   LambdaForm filterReturnForm(LambdaForm.BasicType var1, boolean var2) {
      LambdaFormEditor.Transform.Kind var3 = var2 ? LambdaFormEditor.Transform.Kind.FILTER_RETURN_TO_ZERO : LambdaFormEditor.Transform.Kind.FILTER_RETURN;
      LambdaFormEditor.Transform var4 = LambdaFormEditor.Transform.of(var3, var1.ordinal());
      LambdaForm var5 = this.getInCache(var4);
      if (var5 != null) {
         assert var5.arity == this.lambdaForm.arity;

         assert var5.returnType() == var1;

         return var5;
      } else {
         LambdaFormBuffer var6 = this.buffer();
         var6.startEdit();
         int var7 = this.lambdaForm.names.length;
         LambdaForm.Name var8;
         if (var2) {
            if (var1 == LambdaForm.BasicType.V_TYPE) {
               var8 = null;
            } else {
               var8 = new LambdaForm.Name(LambdaForm.constantZero(var1), new Object[0]);
            }
         } else {
            BoundMethodHandle.SpeciesData var9 = this.oldSpeciesData();
            BoundMethodHandle.SpeciesData var10 = this.newSpeciesData(LambdaForm.BasicType.L_TYPE);
            LambdaForm.Name var11 = this.lambdaForm.parameter(0);
            var6.replaceFunctions(var9.getterFunctions(), var10.getterFunctions(), var11);
            LambdaForm.Name var12 = var11.withConstraint(var10);
            var6.renameParameter(0, var12);
            LambdaForm.Name var13 = new LambdaForm.Name(var10.getterFunction(var9.fieldCount()), new Object[]{var12});
            var6.insertExpression(var7++, var13);
            LambdaForm.BasicType var14 = this.lambdaForm.returnType();
            MethodType var15;
            if (var14 == LambdaForm.BasicType.V_TYPE) {
               var15 = MethodType.methodType(var1.basicTypeClass());
               var8 = new LambdaForm.Name(var15, new Object[]{var13});
            } else {
               var15 = MethodType.methodType(var1.basicTypeClass(), var14.basicTypeClass());
               var8 = new LambdaForm.Name(var15, new Object[]{var13, this.lambdaForm.names[this.lambdaForm.result]});
            }
         }

         if (var8 != null) {
            var6.insertExpression(var7++, var8);
         }

         var6.setResult(var8);
         var5 = var6.endEdit();
         return this.putInCache(var4, var5);
      }
   }

   LambdaForm foldArgumentsForm(int var1, boolean var2, MethodType var3) {
      int var4 = var3.parameterCount();
      LambdaFormEditor.Transform.Kind var5 = var2 ? LambdaFormEditor.Transform.Kind.FOLD_ARGS_TO_VOID : LambdaFormEditor.Transform.Kind.FOLD_ARGS;
      LambdaFormEditor.Transform var6 = LambdaFormEditor.Transform.of(var5, var1, var4);
      LambdaForm var7 = this.getInCache(var6);
      if (var7 != null) {
         assert var7.arity == this.lambdaForm.arity - (var5 == LambdaFormEditor.Transform.Kind.FOLD_ARGS ? 1 : 0);

         return var7;
      } else {
         var7 = this.makeArgumentCombinationForm(var1, var3, true, var2);
         return this.putInCache(var6, var7);
      }
   }

   LambdaForm permuteArgumentsForm(int var1, int[] var2) {
      assert var1 == 1;

      int var3 = this.lambdaForm.names.length;
      int var4 = var2.length;
      int var5 = 0;
      boolean var6 = true;

      for(int var7 = 0; var7 < var2.length; ++var7) {
         int var8 = var2[var7];
         if (var8 != var7) {
            var6 = false;
         }

         var5 = Math.max(var5, var8 + 1);
      }

      assert var1 + var2.length == this.lambdaForm.arity;

      if (var6) {
         return this.lambdaForm;
      } else {
         LambdaFormEditor.Transform var20 = LambdaFormEditor.Transform.of(LambdaFormEditor.Transform.Kind.PERMUTE_ARGS, var2);
         LambdaForm var21 = this.getInCache(var20);
         if (var21 != null) {
            assert var21.arity == var1 + var5 : var21;

            return var21;
         } else {
            LambdaForm.BasicType[] var9 = new LambdaForm.BasicType[var5];

            int var10;
            for(var10 = 0; var10 < var4; ++var10) {
               int var11 = var2[var10];
               var9[var11] = this.lambdaForm.names[var1 + var10].type;
            }

            assert var1 + var4 == this.lambdaForm.arity;

            assert permutedTypesMatch(var2, var9, this.lambdaForm.names, var1);

            for(var10 = 0; var10 < var4 && var2[var10] == var10; ++var10) {
            }

            LambdaForm.Name[] var22 = new LambdaForm.Name[var3 - var4 + var5];
            System.arraycopy(this.lambdaForm.names, 0, var22, 0, var1 + var10);
            int var12 = var3 - this.lambdaForm.arity;
            System.arraycopy(this.lambdaForm.names, var1 + var4, var22, var1 + var5, var12);
            int var13 = var22.length - var12;
            int var14 = this.lambdaForm.result;
            if (var14 >= var1) {
               if (var14 < var1 + var4) {
                  var14 = var2[var14 - var1] + var1;
               } else {
                  var14 = var14 - var4 + var5;
               }
            }

            int var15;
            LambdaForm.Name var18;
            int var19;
            for(var15 = var10; var15 < var4; ++var15) {
               LambdaForm.Name var16 = this.lambdaForm.names[var1 + var15];
               int var17 = var2[var15];
               var18 = var22[var1 + var17];
               if (var18 == null) {
                  var22[var1 + var17] = var18 = new LambdaForm.Name(var9[var17]);
               } else {
                  assert var18.type == var9[var17];
               }

               for(var19 = var13; var19 < var22.length; ++var19) {
                  var22[var19] = var22[var19].replaceName(var16, var18);
               }
            }

            for(var15 = var1 + var10; var15 < var13; ++var15) {
               if (var22[var15] == null) {
                  var22[var15] = LambdaForm.argument(var15, var9[var15 - var1]);
               }
            }

            for(var15 = this.lambdaForm.arity; var15 < this.lambdaForm.names.length; ++var15) {
               int var23 = var15 - this.lambdaForm.arity + var13;
               LambdaForm.Name var24 = this.lambdaForm.names[var15];
               var18 = var22[var23];
               if (var24 != var18) {
                  for(var19 = var23 + 1; var19 < var22.length; ++var19) {
                     var22[var19] = var22[var19].replaceName(var24, var18);
                  }
               }
            }

            var21 = new LambdaForm(this.lambdaForm.debugName, var13, var22, var14);
            return this.putInCache(var20, var21);
         }
      }
   }

   static boolean permutedTypesMatch(int[] var0, LambdaForm.BasicType[] var1, LambdaForm.Name[] var2, int var3) {
      for(int var4 = 0; var4 < var0.length; ++var4) {
         assert var2[var3 + var4].isParam();

         assert var2[var3 + var4].type == var1[var0[var4]];
      }

      return true;
   }

   private static final class Transform extends SoftReference<LambdaForm> {
      final long packedBytes;
      final byte[] fullBytes;
      private static final boolean STRESS_TEST = false;
      private static final int PACKED_BYTE_SIZE = 4;
      private static final int PACKED_BYTE_MASK = 15;
      private static final int PACKED_BYTE_MAX_LENGTH = 16;
      private static final byte[] NO_BYTES = new byte[0];

      private static long packedBytes(byte[] var0) {
         if (var0.length > 16) {
            return 0L;
         } else {
            long var1 = 0L;
            int var3 = 0;

            for(int var4 = 0; var4 < var0.length; ++var4) {
               int var5 = var0[var4] & 255;
               var3 |= var5;
               var1 |= (long)var5 << var4 * 4;
            }

            return !inRange(var3) ? 0L : var1;
         }
      }

      private static long packedBytes(int var0, int var1) {
         assert inRange(var0 | var1);

         return (long)(var0 << 0 | var1 << 4);
      }

      private static long packedBytes(int var0, int var1, int var2) {
         assert inRange(var0 | var1 | var2);

         return (long)(var0 << 0 | var1 << 4 | var2 << 8);
      }

      private static long packedBytes(int var0, int var1, int var2, int var3) {
         assert inRange(var0 | var1 | var2 | var3);

         return (long)(var0 << 0 | var1 << 4 | var2 << 8 | var3 << 12);
      }

      private static boolean inRange(int var0) {
         assert (var0 & 255) == var0;

         return (var0 & -16) == 0;
      }

      private static byte[] fullBytes(int... var0) {
         byte[] var1 = new byte[var0.length];
         int var2 = 0;
         int[] var3 = var0;
         int var4 = var0.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = var3[var5];
            var1[var2++] = bval(var6);
         }

         assert packedBytes(var1) == 0L;

         return var1;
      }

      private byte byteAt(int var1) {
         long var2 = this.packedBytes;
         if (var2 == 0L) {
            return var1 >= this.fullBytes.length ? 0 : this.fullBytes[var1];
         } else {
            assert this.fullBytes == null;

            if (var1 > 16) {
               return 0;
            } else {
               int var4 = var1 * 4;
               return (byte)((int)(var2 >>> var4 & 15L));
            }
         }
      }

      LambdaFormEditor.Transform.Kind kind() {
         return LambdaFormEditor.Transform.Kind.values()[this.byteAt(0)];
      }

      private Transform(long var1, byte[] var3, LambdaForm var4) {
         super(var4);
         this.packedBytes = var1;
         this.fullBytes = var3;
      }

      private Transform(long var1) {
         this(var1, (byte[])null, (LambdaForm)null);

         assert var1 != 0L;

      }

      private Transform(byte[] var1) {
         this(0L, var1, (LambdaForm)null);
      }

      private static byte bval(int var0) {
         assert (var0 & 255) == var0;

         return (byte)var0;
      }

      private static byte bval(LambdaFormEditor.Transform.Kind var0) {
         return bval(var0.ordinal());
      }

      static LambdaFormEditor.Transform of(LambdaFormEditor.Transform.Kind var0, int var1) {
         byte var2 = bval(var0);
         return inRange(var2 | var1) ? new LambdaFormEditor.Transform(packedBytes(var2, var1)) : new LambdaFormEditor.Transform(fullBytes(var2, var1));
      }

      static LambdaFormEditor.Transform of(LambdaFormEditor.Transform.Kind var0, int var1, int var2) {
         byte var3 = (byte)var0.ordinal();
         return inRange(var3 | var1 | var2) ? new LambdaFormEditor.Transform(packedBytes(var3, var1, var2)) : new LambdaFormEditor.Transform(fullBytes(var3, var1, var2));
      }

      static LambdaFormEditor.Transform of(LambdaFormEditor.Transform.Kind var0, int var1, int var2, int var3) {
         byte var4 = (byte)var0.ordinal();
         return inRange(var4 | var1 | var2 | var3) ? new LambdaFormEditor.Transform(packedBytes(var4, var1, var2, var3)) : new LambdaFormEditor.Transform(fullBytes(var4, var1, var2, var3));
      }

      static LambdaFormEditor.Transform of(LambdaFormEditor.Transform.Kind var0, int... var1) {
         return ofBothArrays(var0, var1, NO_BYTES);
      }

      static LambdaFormEditor.Transform of(LambdaFormEditor.Transform.Kind var0, int var1, byte[] var2) {
         return ofBothArrays(var0, new int[]{var1}, var2);
      }

      static LambdaFormEditor.Transform of(LambdaFormEditor.Transform.Kind var0, int var1, int var2, byte[] var3) {
         return ofBothArrays(var0, new int[]{var1, var2}, var3);
      }

      private static LambdaFormEditor.Transform ofBothArrays(LambdaFormEditor.Transform.Kind var0, int[] var1, byte[] var2) {
         byte[] var3 = new byte[1 + var1.length + var2.length];
         byte var4 = 0;
         int var9 = var4 + 1;
         var3[var4] = bval(var0);
         int[] var5 = var1;
         int var6 = var1.length;

         int var7;
         for(var7 = 0; var7 < var6; ++var7) {
            int var8 = var5[var7];
            var3[var9++] = bval(var8);
         }

         byte[] var10 = var2;
         var6 = var2.length;

         for(var7 = 0; var7 < var6; ++var7) {
            byte var12 = var10[var7];
            var3[var9++] = var12;
         }

         long var11 = packedBytes(var3);
         return var11 != 0L ? new LambdaFormEditor.Transform(var11) : new LambdaFormEditor.Transform(var3);
      }

      LambdaFormEditor.Transform withResult(LambdaForm var1) {
         return new LambdaFormEditor.Transform(this.packedBytes, this.fullBytes, var1);
      }

      public boolean equals(Object var1) {
         return var1 instanceof LambdaFormEditor.Transform && this.equals((LambdaFormEditor.Transform)var1);
      }

      public boolean equals(LambdaFormEditor.Transform var1) {
         return this.packedBytes == var1.packedBytes && Arrays.equals(this.fullBytes, var1.fullBytes);
      }

      public int hashCode() {
         if (this.packedBytes != 0L) {
            assert this.fullBytes == null;

            return Long.hashCode(this.packedBytes);
         } else {
            return Arrays.hashCode(this.fullBytes);
         }
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         long var2 = this.packedBytes;
         if (var2 != 0L) {
            var1.append("(");

            while(var2 != 0L) {
               var1.append(var2 & 15L);
               var2 >>>= 4;
               if (var2 != 0L) {
                  var1.append(",");
               }
            }

            var1.append(")");
         }

         if (this.fullBytes != null) {
            var1.append("unpacked");
            var1.append(Arrays.toString(this.fullBytes));
         }

         LambdaForm var4 = (LambdaForm)this.get();
         if (var4 != null) {
            var1.append(" result=");
            var1.append((Object)var4);
         }

         return var1.toString();
      }

      private static enum Kind {
         NO_KIND,
         BIND_ARG,
         ADD_ARG,
         DUP_ARG,
         SPREAD_ARGS,
         FILTER_ARG,
         FILTER_RETURN,
         FILTER_RETURN_TO_ZERO,
         COLLECT_ARGS,
         COLLECT_ARGS_TO_VOID,
         COLLECT_ARGS_TO_ARRAY,
         FOLD_ARGS,
         FOLD_ARGS_TO_VOID,
         PERMUTE_ARGS;
      }
   }
}
