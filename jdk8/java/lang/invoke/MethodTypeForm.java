package java.lang.invoke;

import java.lang.ref.SoftReference;
import sun.invoke.util.Wrapper;

final class MethodTypeForm {
   final int[] argToSlotTable;
   final int[] slotToArgTable;
   final long argCounts;
   final long primCounts;
   final MethodType erasedType;
   final MethodType basicType;
   @Stable
   final SoftReference<MethodHandle>[] methodHandles;
   static final int MH_BASIC_INV = 0;
   static final int MH_NF_INV = 1;
   static final int MH_UNINIT_CS = 2;
   static final int MH_LIMIT = 3;
   @Stable
   final SoftReference<LambdaForm>[] lambdaForms;
   static final int LF_INVVIRTUAL = 0;
   static final int LF_INVSTATIC = 1;
   static final int LF_INVSPECIAL = 2;
   static final int LF_NEWINVSPECIAL = 3;
   static final int LF_INVINTERFACE = 4;
   static final int LF_INVSTATIC_INIT = 5;
   static final int LF_INTERPRET = 6;
   static final int LF_REBIND = 7;
   static final int LF_DELEGATE = 8;
   static final int LF_DELEGATE_BLOCK_INLINING = 9;
   static final int LF_EX_LINKER = 10;
   static final int LF_EX_INVOKER = 11;
   static final int LF_GEN_LINKER = 12;
   static final int LF_GEN_INVOKER = 13;
   static final int LF_CS_LINKER = 14;
   static final int LF_MH_LINKER = 15;
   static final int LF_GWC = 16;
   static final int LF_GWT = 17;
   static final int LF_LIMIT = 18;
   public static final int NO_CHANGE = 0;
   public static final int ERASE = 1;
   public static final int WRAP = 2;
   public static final int UNWRAP = 3;
   public static final int INTS = 4;
   public static final int LONGS = 5;
   public static final int RAW_RETURN = 6;

   public MethodType erasedType() {
      return this.erasedType;
   }

   public MethodType basicType() {
      return this.basicType;
   }

   private boolean assertIsBasicType() {
      assert this.erasedType == this.basicType : "erasedType: " + this.erasedType + " != basicType: " + this.basicType;

      return true;
   }

   public MethodHandle cachedMethodHandle(int var1) {
      assert this.assertIsBasicType();

      SoftReference var2 = this.methodHandles[var1];
      return var2 != null ? (MethodHandle)var2.get() : null;
   }

   public synchronized MethodHandle setCachedMethodHandle(int var1, MethodHandle var2) {
      SoftReference var3 = this.methodHandles[var1];
      if (var3 != null) {
         MethodHandle var4 = (MethodHandle)var3.get();
         if (var4 != null) {
            return var4;
         }
      }

      this.methodHandles[var1] = new SoftReference(var2);
      return var2;
   }

   public LambdaForm cachedLambdaForm(int var1) {
      assert this.assertIsBasicType();

      SoftReference var2 = this.lambdaForms[var1];
      return var2 != null ? (LambdaForm)var2.get() : null;
   }

   public synchronized LambdaForm setCachedLambdaForm(int var1, LambdaForm var2) {
      SoftReference var3 = this.lambdaForms[var1];
      if (var3 != null) {
         LambdaForm var4 = (LambdaForm)var3.get();
         if (var4 != null) {
            return var4;
         }
      }

      this.lambdaForms[var1] = new SoftReference(var2);
      return var2;
   }

   protected MethodTypeForm(MethodType var1) {
      this.erasedType = var1;
      Class[] var2 = var1.ptypes();
      int var3 = var2.length;
      byte var5 = 1;
      int var6 = 1;
      Object var7 = null;
      Object var8 = null;
      int var9 = 0;
      int var10 = 0;
      int var11 = 0;
      int var12 = 0;
      Class[] var13 = var2;
      Class[] var14 = var2;

      Class var16;
      Wrapper var17;
      for(int var15 = 0; var15 < var13.length; ++var15) {
         var16 = var13[var15];
         if (var16 != Object.class) {
            ++var9;
            var17 = Wrapper.forPrimitiveType(var16);
            if (var17.isDoubleWord()) {
               ++var10;
            }

            if (var17.isSubwordOrInt() && var16 != Integer.TYPE) {
               if (var14 == var13) {
                  var14 = (Class[])var14.clone();
               }

               var14[var15] = Integer.TYPE;
            }
         }
      }

      int var4 = var3 + var10;
      Class var23 = var1.returnType();
      var16 = var23;
      if (var23 != Object.class) {
         ++var11;
         var17 = Wrapper.forPrimitiveType(var23);
         if (var17.isDoubleWord()) {
            ++var12;
         }

         if (var17.isSubwordOrInt() && var23 != Integer.TYPE) {
            var16 = Integer.TYPE;
         }

         if (var23 == Void.TYPE) {
            var6 = 0;
            var5 = 0;
         } else {
            var6 += var12;
         }
      }

      MethodTypeForm var24;
      if (var13 == var14 && var16 == var23) {
         this.basicType = var1;
         int var18;
         int[] var21;
         int[] var22;
         int var25;
         if (var10 != 0) {
            var25 = var3 + var10;
            var22 = new int[var25 + 1];
            var21 = new int[1 + var3];
            var21[0] = var25;

            for(var18 = 0; var18 < var13.length; ++var18) {
               Class var19 = var13[var18];
               Wrapper var20 = Wrapper.forBasicType(var19);
               if (var20.isDoubleWord()) {
                  --var25;
               }

               --var25;
               var22[var25] = var18 + 1;
               var21[1 + var18] = var25;
            }

            assert var25 == 0;
         } else if (var9 != 0) {
            assert var3 == var4;

            var24 = MethodType.genericMethodType(var3).form();

            assert this != var24;

            var22 = var24.slotToArgTable;
            var21 = var24.argToSlotTable;
         } else {
            var25 = var3;
            var22 = new int[var3 + 1];
            var21 = new int[1 + var3];
            var21[0] = var3;

            for(var18 = 0; var18 < var3; ++var18) {
               --var25;
               var22[var25] = var18 + 1;
               var21[1 + var18] = var25;
            }
         }

         this.primCounts = pack(var12, var11, var10, var9);
         this.argCounts = pack(var6, var5, var4, var3);
         this.argToSlotTable = var21;
         this.slotToArgTable = var22;
         if (var4 >= 256) {
            throw MethodHandleStatics.newIllegalArgumentException("too many arguments");
         } else {
            assert this.basicType == var1;

            this.lambdaForms = new SoftReference[18];
            this.methodHandles = new SoftReference[3];
         }
      } else {
         this.basicType = MethodType.makeImpl(var16, var14, true);
         var24 = this.basicType.form();

         assert this != var24;

         this.primCounts = var24.primCounts;
         this.argCounts = var24.argCounts;
         this.argToSlotTable = var24.argToSlotTable;
         this.slotToArgTable = var24.slotToArgTable;
         this.methodHandles = null;
         this.lambdaForms = null;
      }
   }

   private static long pack(int var0, int var1, int var2, int var3) {
      assert ((var0 | var1 | var2 | var3) & -65536) == 0;

      long var4 = (long)(var0 << 16 | var1);
      long var6 = (long)(var2 << 16 | var3);
      return var4 << 32 | var6;
   }

   private static char unpack(long var0, int var2) {
      assert var2 <= 3;

      return (char)((int)(var0 >> (3 - var2) * 16));
   }

   public int parameterCount() {
      return unpack(this.argCounts, 3);
   }

   public int parameterSlotCount() {
      return unpack(this.argCounts, 2);
   }

   public int returnCount() {
      return unpack(this.argCounts, 1);
   }

   public int returnSlotCount() {
      return unpack(this.argCounts, 0);
   }

   public int primitiveParameterCount() {
      return unpack(this.primCounts, 3);
   }

   public int longPrimitiveParameterCount() {
      return unpack(this.primCounts, 2);
   }

   public int primitiveReturnCount() {
      return unpack(this.primCounts, 1);
   }

   public int longPrimitiveReturnCount() {
      return unpack(this.primCounts, 0);
   }

   public boolean hasPrimitives() {
      return this.primCounts != 0L;
   }

   public boolean hasNonVoidPrimitives() {
      if (this.primCounts == 0L) {
         return false;
      } else if (this.primitiveParameterCount() != 0) {
         return true;
      } else {
         return this.primitiveReturnCount() != 0 && this.returnCount() != 0;
      }
   }

   public boolean hasLongPrimitives() {
      return (this.longPrimitiveParameterCount() | this.longPrimitiveReturnCount()) != 0;
   }

   public int parameterToArgSlot(int var1) {
      return this.argToSlotTable[1 + var1];
   }

   public int argSlotToParameter(int var1) {
      return this.slotToArgTable[var1] - 1;
   }

   static MethodTypeForm findForm(MethodType var0) {
      MethodType var1 = canonicalize(var0, 1, 1);
      return var1 == null ? new MethodTypeForm(var0) : var1.form();
   }

   public static MethodType canonicalize(MethodType var0, int var1, int var2) {
      Class[] var3 = var0.ptypes();
      Class[] var4 = canonicalizeAll(var3, var2);
      Class var5 = var0.returnType();
      Class var6 = canonicalize(var5, var1);
      if (var4 == null && var6 == null) {
         return null;
      } else {
         if (var6 == null) {
            var6 = var5;
         }

         if (var4 == null) {
            var4 = var3;
         }

         return MethodType.makeImpl(var6, var4, true);
      }
   }

   static Class<?> canonicalize(Class<?> var0, int var1) {
      if (var0 != Object.class) {
         if (!var0.isPrimitive()) {
            switch(var1) {
            case 1:
            case 6:
               return Object.class;
            case 3:
               Class var2 = Wrapper.asPrimitiveType(var0);
               if (var2 != var0) {
                  return var2;
               }
            }
         } else if (var0 == Void.TYPE) {
            switch(var1) {
            case 2:
               return Void.class;
            case 6:
               return Integer.TYPE;
            }
         } else {
            switch(var1) {
            case 2:
               return Wrapper.asWrapperType(var0);
            case 3:
            default:
               break;
            case 4:
               if (var0 != Integer.TYPE && var0 != Long.TYPE) {
                  if (var0 == Double.TYPE) {
                     return Long.TYPE;
                  }

                  return Integer.TYPE;
               }

               return null;
            case 5:
               if (var0 == Long.TYPE) {
                  return null;
               }

               return Long.TYPE;
            case 6:
               if (var0 != Integer.TYPE && var0 != Long.TYPE && var0 != Float.TYPE && var0 != Double.TYPE) {
                  return Integer.TYPE;
               }

               return null;
            }
         }
      }

      return null;
   }

   static Class<?>[] canonicalizeAll(Class<?>[] var0, int var1) {
      Class[] var2 = null;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Class var5 = canonicalize(var0[var4], var1);
         if (var5 == Void.TYPE) {
            var5 = null;
         }

         if (var5 != null) {
            if (var2 == null) {
               var2 = (Class[])var0.clone();
            }

            var2[var4] = var5;
         }
      }

      return var2;
   }

   public String toString() {
      return "Form" + this.erasedType;
   }
}
