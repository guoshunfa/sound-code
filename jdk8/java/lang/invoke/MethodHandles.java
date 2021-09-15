package java.lang.invoke;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ReflectPermission;
import java.security.Permission;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import sun.invoke.util.ValueConversions;
import sun.invoke.util.VerifyAccess;
import sun.invoke.util.Wrapper;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public class MethodHandles {
   private static final MemberName.Factory IMPL_NAMES = MemberName.getFactory();
   private static final Permission ACCESS_PERMISSION;
   private static final MethodHandle[] IDENTITY_MHS;
   private static final MethodHandle[] ZERO_MHS;

   private MethodHandles() {
   }

   @CallerSensitive
   public static MethodHandles.Lookup lookup() {
      return new MethodHandles.Lookup(Reflection.getCallerClass());
   }

   public static MethodHandles.Lookup publicLookup() {
      return MethodHandles.Lookup.PUBLIC_LOOKUP;
   }

   public static <T extends Member> T reflectAs(Class<T> var0, MethodHandle var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(ACCESS_PERMISSION);
      }

      MethodHandles.Lookup var3 = MethodHandles.Lookup.IMPL_LOOKUP;
      return var3.revealDirect(var1).reflectAs(var0, var3);
   }

   public static MethodHandle arrayElementGetter(Class<?> var0) throws IllegalArgumentException {
      return MethodHandleImpl.makeArrayElementAccessor(var0, false);
   }

   public static MethodHandle arrayElementSetter(Class<?> var0) throws IllegalArgumentException {
      return MethodHandleImpl.makeArrayElementAccessor(var0, true);
   }

   public static MethodHandle spreadInvoker(MethodType var0, int var1) {
      if (var1 >= 0 && var1 <= var0.parameterCount()) {
         var0 = var0.asSpreaderType(Object[].class, var0.parameterCount() - var1);
         return var0.invokers().spreadInvoker(var1);
      } else {
         throw MethodHandleStatics.newIllegalArgumentException("bad argument count", var1);
      }
   }

   public static MethodHandle exactInvoker(MethodType var0) {
      return var0.invokers().exactInvoker();
   }

   public static MethodHandle invoker(MethodType var0) {
      return var0.invokers().genericInvoker();
   }

   static MethodHandle basicInvoker(MethodType var0) {
      return var0.invokers().basicInvoker();
   }

   public static MethodHandle explicitCastArguments(MethodHandle var0, MethodType var1) {
      explicitCastArgumentsChecks(var0, var1);
      MethodType var2 = var0.type();
      if (var2 == var1) {
         return var0;
      } else {
         return var2.explicitCastEquivalentToAsType(var1) ? var0.asFixedArity().asType(var1) : MethodHandleImpl.makePairwiseConvert(var0, var1, false);
      }
   }

   private static void explicitCastArgumentsChecks(MethodHandle var0, MethodType var1) {
      if (var0.type().parameterCount() != var1.parameterCount()) {
         throw new WrongMethodTypeException("cannot explicitly cast " + var0 + " to " + var1);
      }
   }

   public static MethodHandle permuteArguments(MethodHandle var0, MethodType var1, int... var2) {
      var2 = (int[])var2.clone();
      MethodType var3 = var0.type();
      permuteArgumentChecks(var2, var1, var3);
      BoundMethodHandle var5 = var0.rebind();
      LambdaForm var6 = var5.form;
      int var7 = var1.parameterCount();

      do {
         int var8;
         if ((var8 = findFirstDupOrDrop(var2, var7)) == 0) {
            assert var2.length == var7;

            var6 = var6.editor().permuteArgumentsForm(1, var2);
            if (var1 == var5.type() && var6 == var5.internalForm()) {
               return var5;
            }

            return var5.copyWith(var1, var6);
         }

         int var9;
         int var10;
         if (var8 > 0) {
            var9 = var8;
            var10 = var8;
            int var14 = var2[var8];
            boolean var15 = false;

            while(true) {
               --var10;
               int var13;
               if ((var13 = var2[var10]) == var14) {
                  if (!var15) {
                     var9 = var10;
                     var10 = var8;
                  }

                  var6 = var6.editor().dupArgumentForm(1 + var9, 1 + var10);

                  assert var2[var9] == var2[var10];

                  var3 = var3.dropParameterTypes(var10, var10 + 1);
                  var13 = var10 + 1;
                  System.arraycopy(var2, var13, var2, var10, var2.length - var13);
                  var2 = Arrays.copyOf(var2, var2.length - 1);
                  break;
               }

               if (var14 > var13) {
                  var15 = true;
               }
            }
         } else {
            var9 = ~var8;

            for(var10 = 0; var10 < var2.length && var2[var10] < var9; ++var10) {
            }

            Class var11 = var1.parameterType(var9);
            var6 = var6.editor().addArgumentForm(1 + var10, LambdaForm.BasicType.basicType(var11));
            var3 = var3.insertParameterTypes(var10, var11);
            int var12 = var10 + 1;
            var2 = Arrays.copyOf(var2, var2.length + 1);
            System.arraycopy(var2, var10, var2, var12, var2.length - var12);
            var2[var10] = var9;
         }
      } while($assertionsDisabled || permuteArgumentChecks(var2, var1, var3));

      throw new AssertionError();
   }

   private static int findFirstDupOrDrop(int[] var0, int var1) {
      int var5;
      if (var1 < 63) {
         long var9 = 0L;

         for(var5 = 0; var5 < var0.length; ++var5) {
            int var6 = var0[var5];
            if (var6 >= var1) {
               return var0.length;
            }

            long var7 = 1L << var6;
            if ((var9 & var7) != 0L) {
               return var5;
            }

            var9 |= var7;
         }

         if (var9 == (1L << var1) - 1L) {
            assert Long.numberOfTrailingZeros(Long.lowestOneBit(~var9)) == var1;

            return 0;
         } else {
            long var10 = Long.lowestOneBit(~var9);
            int var11 = Long.numberOfTrailingZeros(var10);

            assert var11 <= var1;

            if (var11 == var1) {
               return 0;
            } else {
               return ~var11;
            }
         }
      } else {
         BitSet var3 = new BitSet(var1);

         int var4;
         for(var4 = 0; var4 < var0.length; ++var4) {
            var5 = var0[var4];
            if (var5 >= var1) {
               return var0.length;
            }

            if (var3.get(var5)) {
               return var4;
            }

            var3.set(var5);
         }

         var4 = var3.nextClearBit(0);

         assert var4 <= var1;

         if (var4 == var1) {
            return 0;
         } else {
            return ~var4;
         }
      }
   }

   private static boolean permuteArgumentChecks(int[] var0, MethodType var1, MethodType var2) {
      if (var1.returnType() != var2.returnType()) {
         throw MethodHandleStatics.newIllegalArgumentException("return types do not match", var2, var1);
      } else {
         if (var0.length == var2.parameterCount()) {
            int var3 = var1.parameterCount();
            boolean var4 = false;

            for(int var5 = 0; var5 < var0.length; ++var5) {
               int var6 = var0[var5];
               if (var6 < 0 || var6 >= var3) {
                  var4 = true;
                  break;
               }

               Class var7 = var1.parameterType(var6);
               Class var8 = var2.parameterType(var5);
               if (var7 != var8) {
                  throw MethodHandleStatics.newIllegalArgumentException("parameter types do not match after reorder", var2, var1);
               }
            }

            if (!var4) {
               return true;
            }
         }

         throw MethodHandleStatics.newIllegalArgumentException("bad reorder array: " + Arrays.toString(var0));
      }
   }

   public static MethodHandle constant(Class<?> var0, Object var1) {
      if (var0.isPrimitive()) {
         if (var0 == Void.TYPE) {
            throw MethodHandleStatics.newIllegalArgumentException("void type");
         } else {
            Wrapper var2 = Wrapper.forPrimitiveType(var0);
            var1 = var2.convert(var1, var0);
            return var2.zero().equals(var1) ? zero(var2, var0) : insertArguments(identity(var0), 0, var1);
         }
      } else {
         return var1 == null ? zero(Wrapper.OBJECT, var0) : identity(var0).bindTo(var1);
      }
   }

   public static MethodHandle identity(Class<?> var0) {
      Wrapper var1 = var0.isPrimitive() ? Wrapper.forPrimitiveType(var0) : Wrapper.OBJECT;
      int var2 = var1.ordinal();
      MethodHandle var3 = IDENTITY_MHS[var2];
      if (var3 == null) {
         var3 = setCachedMethodHandle(IDENTITY_MHS, var2, makeIdentity(var1.primitiveType()));
      }

      if (var3.type().returnType() == var0) {
         return var3;
      } else {
         assert var1 == Wrapper.OBJECT;

         return makeIdentity(var0);
      }
   }

   private static MethodHandle makeIdentity(Class<?> var0) {
      MethodType var1 = MethodType.methodType(var0, var0);
      LambdaForm var2 = LambdaForm.identityForm(LambdaForm.BasicType.basicType(var0));
      return MethodHandleImpl.makeIntrinsic(var1, var2, MethodHandleImpl.Intrinsic.IDENTITY);
   }

   private static MethodHandle zero(Wrapper var0, Class<?> var1) {
      int var2 = var0.ordinal();
      MethodHandle var3 = ZERO_MHS[var2];
      if (var3 == null) {
         var3 = setCachedMethodHandle(ZERO_MHS, var2, makeZero(var0.primitiveType()));
      }

      if (var3.type().returnType() == var1) {
         return var3;
      } else {
         assert var0 == Wrapper.OBJECT;

         return makeZero(var1);
      }
   }

   private static MethodHandle makeZero(Class<?> var0) {
      MethodType var1 = MethodType.methodType(var0);
      LambdaForm var2 = LambdaForm.zeroForm(LambdaForm.BasicType.basicType(var0));
      return MethodHandleImpl.makeIntrinsic(var1, var2, MethodHandleImpl.Intrinsic.ZERO);
   }

   private static synchronized MethodHandle setCachedMethodHandle(MethodHandle[] var0, int var1, MethodHandle var2) {
      MethodHandle var3 = var0[var1];
      return var3 != null ? var3 : (var0[var1] = var2);
   }

   public static MethodHandle insertArguments(MethodHandle var0, int var1, Object... var2) {
      int var3 = var2.length;
      Class[] var4 = insertArgumentsChecks(var0, var3, var1);
      if (var3 == 0) {
         return var0;
      } else {
         BoundMethodHandle var5 = var0.rebind();

         for(int var6 = 0; var6 < var3; ++var6) {
            Object var7 = var2[var6];
            Class var8 = var4[var1 + var6];
            if (var8.isPrimitive()) {
               var5 = insertArgumentPrimitive(var5, var1, var8, var7);
            } else {
               var7 = var8.cast(var7);
               var5 = var5.bindArgumentL(var1, var7);
            }
         }

         return var5;
      }
   }

   private static BoundMethodHandle insertArgumentPrimitive(BoundMethodHandle var0, int var1, Class<?> var2, Object var3) {
      Wrapper var4 = Wrapper.forPrimitiveType(var2);
      var3 = var4.convert(var3, var2);
      switch(var4) {
      case INT:
         return var0.bindArgumentI(var1, (Integer)var3);
      case LONG:
         return var0.bindArgumentJ(var1, (Long)var3);
      case FLOAT:
         return var0.bindArgumentF(var1, (Float)var3);
      case DOUBLE:
         return var0.bindArgumentD(var1, (Double)var3);
      default:
         return var0.bindArgumentI(var1, ValueConversions.widenSubword(var3));
      }
   }

   private static Class<?>[] insertArgumentsChecks(MethodHandle var0, int var1, int var2) throws RuntimeException {
      MethodType var3 = var0.type();
      int var4 = var3.parameterCount();
      int var5 = var4 - var1;
      if (var5 < 0) {
         throw MethodHandleStatics.newIllegalArgumentException("too many values to insert");
      } else if (var2 >= 0 && var2 <= var5) {
         return var3.ptypes();
      } else {
         throw MethodHandleStatics.newIllegalArgumentException("no argument type to append");
      }
   }

   public static MethodHandle dropArguments(MethodHandle var0, int var1, List<Class<?>> var2) {
      var2 = copyTypes(var2);
      MethodType var3 = var0.type();
      int var4 = dropArgumentChecks(var3, var1, var2);
      MethodType var5 = var3.insertParameterTypes(var1, var2);
      if (var4 == 0) {
         return var0;
      } else {
         BoundMethodHandle var6 = var0.rebind();
         LambdaForm var7 = var6.form;
         int var8 = 1 + var1;

         Class var10;
         for(Iterator var9 = var2.iterator(); var9.hasNext(); var7 = var7.editor().addArgumentForm(var8++, LambdaForm.BasicType.basicType(var10))) {
            var10 = (Class)var9.next();
         }

         var6 = var6.copyWith(var5, var7);
         return var6;
      }
   }

   private static List<Class<?>> copyTypes(List<Class<?>> var0) {
      Object[] var1 = var0.toArray();
      return Arrays.asList(Arrays.copyOf(var1, var1.length, Class[].class));
   }

   private static int dropArgumentChecks(MethodType var0, int var1, List<Class<?>> var2) {
      int var3 = var2.size();
      MethodType.checkSlotCount(var3);
      int var4 = var0.parameterCount();
      int var5 = var4 + var3;
      if (var1 >= 0 && var1 <= var4) {
         return var3;
      } else {
         throw MethodHandleStatics.newIllegalArgumentException("no argument type to remove" + Arrays.asList(var0, var1, var2, var5, var4));
      }
   }

   public static MethodHandle dropArguments(MethodHandle var0, int var1, Class<?>... var2) {
      return dropArguments(var0, var1, Arrays.asList(var2));
   }

   public static MethodHandle filterArguments(MethodHandle var0, int var1, MethodHandle... var2) {
      filterArgumentsCheckArity(var0, var1, var2);
      MethodHandle var3 = var0;
      int var4 = var1 - 1;
      MethodHandle[] var5 = var2;
      int var6 = var2.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         MethodHandle var8 = var5[var7];
         ++var4;
         if (var8 != null) {
            var3 = filterArgument(var3, var4, var8);
         }
      }

      return var3;
   }

   static MethodHandle filterArgument(MethodHandle var0, int var1, MethodHandle var2) {
      filterArgumentChecks(var0, var1, var2);
      MethodType var3 = var0.type();
      MethodType var4 = var2.type();
      BoundMethodHandle var5 = var0.rebind();
      Class var6 = var4.parameterType(0);
      LambdaForm var7 = var5.editor().filterArgumentForm(1 + var1, LambdaForm.BasicType.basicType(var6));
      MethodType var8 = var3.changeParameterType(var1, var6);
      var5 = var5.copyWithExtendL(var8, var7, var2);
      return var5;
   }

   private static void filterArgumentsCheckArity(MethodHandle var0, int var1, MethodHandle[] var2) {
      MethodType var3 = var0.type();
      int var4 = var3.parameterCount();
      if (var1 + var2.length > var4) {
         throw MethodHandleStatics.newIllegalArgumentException("too many filters");
      }
   }

   private static void filterArgumentChecks(MethodHandle var0, int var1, MethodHandle var2) throws RuntimeException {
      MethodType var3 = var0.type();
      MethodType var4 = var2.type();
      if (var4.parameterCount() != 1 || var4.returnType() != var3.parameterType(var1)) {
         throw MethodHandleStatics.newIllegalArgumentException("target and filter types do not match", var3, var4);
      }
   }

   public static MethodHandle collectArguments(MethodHandle var0, int var1, MethodHandle var2) {
      MethodType var3 = collectArgumentsChecks(var0, var1, var2);
      MethodType var4 = var2.type();
      BoundMethodHandle var5 = var0.rebind();
      LambdaForm var6;
      if (var4.returnType().isArray() && var2.intrinsicName() == MethodHandleImpl.Intrinsic.NEW_ARRAY) {
         var6 = var5.editor().collectArgumentArrayForm(1 + var1, var2);
         if (var6 != null) {
            return var5.copyWith(var3, var6);
         }
      }

      var6 = var5.editor().collectArgumentsForm(1 + var1, var4.basicType());
      return var5.copyWithExtendL(var3, var6, var2);
   }

   private static MethodType collectArgumentsChecks(MethodHandle var0, int var1, MethodHandle var2) throws RuntimeException {
      MethodType var3 = var0.type();
      MethodType var4 = var2.type();
      Class var5 = var4.returnType();
      List var6 = var4.parameterList();
      if (var5 == Void.TYPE) {
         return var3.insertParameterTypes(var1, var6);
      } else if (var5 != var3.parameterType(var1)) {
         throw MethodHandleStatics.newIllegalArgumentException("target and filter types do not match", var3, var4);
      } else {
         return var3.dropParameterTypes(var1, var1 + 1).insertParameterTypes(var1, var6);
      }
   }

   public static MethodHandle filterReturnValue(MethodHandle var0, MethodHandle var1) {
      MethodType var2 = var0.type();
      MethodType var3 = var1.type();
      filterReturnValueChecks(var2, var3);
      BoundMethodHandle var4 = var0.rebind();
      LambdaForm.BasicType var5 = LambdaForm.BasicType.basicType(var3.returnType());
      LambdaForm var6 = var4.editor().filterReturnForm(var5, false);
      MethodType var7 = var2.changeReturnType(var3.returnType());
      var4 = var4.copyWithExtendL(var7, var6, var1);
      return var4;
   }

   private static void filterReturnValueChecks(MethodType var0, MethodType var1) throws RuntimeException {
      Class var2 = var0.returnType();
      int var3 = var1.parameterCount();
      if (var3 == 0) {
         if (var2 != Void.TYPE) {
            throw MethodHandleStatics.newIllegalArgumentException("target and filter types do not match", var0, var1);
         }
      } else if (var2 != var1.parameterType(0) || var3 != 1) {
         throw MethodHandleStatics.newIllegalArgumentException("target and filter types do not match", var0, var1);
      }

   }

   public static MethodHandle foldArguments(MethodHandle var0, MethodHandle var1) {
      byte var2 = 0;
      MethodType var3 = var0.type();
      MethodType var4 = var1.type();
      Class var5 = foldArgumentChecks(var2, var3, var4);
      BoundMethodHandle var6 = var0.rebind();
      boolean var7 = var5 == Void.TYPE;
      LambdaForm var8 = var6.editor().foldArgumentsForm(1 + var2, var7, var4.basicType());
      MethodType var9 = var3;
      if (!var7) {
         var9 = var3.dropParameterTypes(var2, var2 + 1);
      }

      var6 = var6.copyWithExtendL(var9, var8, var1);
      return var6;
   }

   private static Class<?> foldArgumentChecks(int var0, MethodType var1, MethodType var2) {
      int var3 = var2.parameterCount();
      Class var4 = var2.returnType();
      int var5 = var4 == Void.TYPE ? 0 : 1;
      int var6 = var0 + var5;
      boolean var7 = var1.parameterCount() >= var6 + var3;
      if (var7 && !var2.parameterList().equals(var1.parameterList().subList(var6, var6 + var3))) {
         var7 = false;
      }

      if (var7 && var5 != 0 && var2.returnType() != var1.parameterType(0)) {
         var7 = false;
      }

      if (!var7) {
         throw misMatchedTypes("target and combiner types", var1, var2);
      } else {
         return var4;
      }
   }

   public static MethodHandle guardWithTest(MethodHandle var0, MethodHandle var1, MethodHandle var2) {
      MethodType var3 = var0.type();
      MethodType var4 = var1.type();
      MethodType var5 = var2.type();
      if (!var4.equals((Object)var5)) {
         throw misMatchedTypes("target and fallback types", var4, var5);
      } else if (var3.returnType() != Boolean.TYPE) {
         throw MethodHandleStatics.newIllegalArgumentException("guard type is not a predicate " + var3);
      } else {
         List var6 = var4.parameterList();
         List var7 = var3.parameterList();
         if (!var6.equals(var7)) {
            int var8 = var7.size();
            int var9 = var6.size();
            if (var8 >= var9 || !var6.subList(0, var8).equals(var7)) {
               throw misMatchedTypes("target and test types", var4, var3);
            }

            var0 = dropArguments(var0, var8, var6.subList(var8, var9));
            var3 = var0.type();
         }

         return MethodHandleImpl.makeGuardWithTest(var0, var1, var2);
      }
   }

   static RuntimeException misMatchedTypes(String var0, MethodType var1, MethodType var2) {
      return MethodHandleStatics.newIllegalArgumentException(var0 + " must match: " + var1 + " != " + var2);
   }

   public static MethodHandle catchException(MethodHandle var0, Class<? extends Throwable> var1, MethodHandle var2) {
      MethodType var3 = var0.type();
      MethodType var4 = var2.type();
      if (var4.parameterCount() >= 1 && var4.parameterType(0).isAssignableFrom(var1)) {
         if (var4.returnType() != var3.returnType()) {
            throw misMatchedTypes("target and handler return types", var3, var4);
         } else {
            List var5 = var3.parameterList();
            List var6 = var4.parameterList();
            var6 = var6.subList(1, var6.size());
            if (!var5.equals(var6)) {
               int var7 = var6.size();
               int var8 = var5.size();
               if (var7 >= var8 || !var5.subList(0, var7).equals(var6)) {
                  throw misMatchedTypes("target and handler types", var3, var4);
               }

               var2 = dropArguments(var2, 1 + var7, var5.subList(var7, var8));
               var4 = var2.type();
            }

            return MethodHandleImpl.makeGuardWithCatch(var0, var1, var2);
         }
      } else {
         throw MethodHandleStatics.newIllegalArgumentException("handler does not accept exception type " + var1);
      }
   }

   public static MethodHandle throwException(Class<?> var0, Class<? extends Throwable> var1) {
      if (!Throwable.class.isAssignableFrom(var1)) {
         throw new ClassCastException(var1.getName());
      } else {
         return MethodHandleImpl.throwException(MethodType.methodType(var0, var1));
      }
   }

   static {
      MethodHandleImpl.initStatics();
      ACCESS_PERMISSION = new ReflectPermission("suppressAccessChecks");
      IDENTITY_MHS = new MethodHandle[Wrapper.values().length];
      ZERO_MHS = new MethodHandle[Wrapper.values().length];
   }

   public static final class Lookup {
      private final Class<?> lookupClass;
      private final int allowedModes;
      public static final int PUBLIC = 1;
      public static final int PRIVATE = 2;
      public static final int PROTECTED = 4;
      public static final int PACKAGE = 8;
      private static final int ALL_MODES = 15;
      private static final int TRUSTED = -1;
      static final MethodHandles.Lookup PUBLIC_LOOKUP;
      static final MethodHandles.Lookup IMPL_LOOKUP;
      private static final boolean ALLOW_NESTMATE_ACCESS = false;
      static ConcurrentHashMap<MemberName, DirectMethodHandle> LOOKASIDE_TABLE;

      private static int fixmods(int var0) {
         var0 &= 7;
         return var0 != 0 ? var0 : 8;
      }

      public Class<?> lookupClass() {
         return this.lookupClass;
      }

      private Class<?> lookupClassOrNull() {
         return this.allowedModes == -1 ? null : this.lookupClass;
      }

      public int lookupModes() {
         return this.allowedModes & 15;
      }

      Lookup(Class<?> var1) {
         this(var1, 15);
         checkUnprivilegedlookupClass(var1, 15);
      }

      private Lookup(Class<?> var1, int var2) {
         this.lookupClass = var1;
         this.allowedModes = var2;
      }

      public MethodHandles.Lookup in(Class<?> var1) {
         var1.getClass();
         if (this.allowedModes == -1) {
            return new MethodHandles.Lookup(var1, 15);
         } else if (var1 == this.lookupClass) {
            return this;
         } else {
            int var2 = this.allowedModes & 11;
            if ((var2 & 8) != 0 && !VerifyAccess.isSamePackage(this.lookupClass, var1)) {
               var2 &= -11;
            }

            if ((var2 & 2) != 0 && !VerifyAccess.isSamePackageMember(this.lookupClass, var1)) {
               var2 &= -3;
            }

            if ((var2 & 1) != 0 && !VerifyAccess.isClassAccessible(var1, this.lookupClass, this.allowedModes)) {
               var2 = 0;
            }

            checkUnprivilegedlookupClass(var1, var2);
            return new MethodHandles.Lookup(var1, var2);
         }
      }

      private static void checkUnprivilegedlookupClass(Class<?> var0, int var1) {
         String var2 = var0.getName();
         if (var2.startsWith("java.lang.invoke.")) {
            throw MethodHandleStatics.newIllegalArgumentException("illegal lookupClass: " + var0);
         } else if (var1 == 15 && var0.getClassLoader() == null && (var2.startsWith("java.") || var2.startsWith("sun.") && !var2.startsWith("sun.invoke.") && !var2.equals("sun.reflect.ReflectionFactory"))) {
            throw MethodHandleStatics.newIllegalArgumentException("illegal lookupClass: " + var0);
         }
      }

      public String toString() {
         String var1 = this.lookupClass.getName();
         switch(this.allowedModes) {
         case -1:
            return "/trusted";
         case 0:
            return var1 + "/noaccess";
         case 1:
            return var1 + "/public";
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 10:
         case 12:
         case 13:
         case 14:
         default:
            var1 = var1 + "/" + Integer.toHexString(this.allowedModes);

            assert false : var1;

            return var1;
         case 9:
            return var1 + "/package";
         case 11:
            return var1 + "/private";
         case 15:
            return var1;
         }
      }

      public MethodHandle findStatic(Class<?> var1, String var2, MethodType var3) throws NoSuchMethodException, IllegalAccessException {
         MemberName var4 = this.resolveOrFail((byte)6, var1, var2, (MethodType)var3);
         return this.getDirectMethod((byte)6, var1, var4, this.findBoundCallerClass(var4));
      }

      public MethodHandle findVirtual(Class<?> var1, String var2, MethodType var3) throws NoSuchMethodException, IllegalAccessException {
         if (var1 == MethodHandle.class) {
            MethodHandle var4 = this.findVirtualForMH(var2, var3);
            if (var4 != null) {
               return var4;
            }
         }

         int var6 = var1.isInterface() ? 9 : 5;
         MemberName var5 = this.resolveOrFail((byte)var6, var1, var2, (MethodType)var3);
         return this.getDirectMethod((byte)var6, var1, var5, this.findBoundCallerClass(var5));
      }

      private MethodHandle findVirtualForMH(String var1, MethodType var2) {
         if ("invoke".equals(var1)) {
            return MethodHandles.invoker(var2);
         } else if ("invokeExact".equals(var1)) {
            return MethodHandles.exactInvoker(var2);
         } else {
            assert !MemberName.isMethodHandleInvokeName(var1);

            return null;
         }
      }

      public MethodHandle findConstructor(Class<?> var1, MethodType var2) throws NoSuchMethodException, IllegalAccessException {
         if (var1.isArray()) {
            throw new NoSuchMethodException("no constructor for array class: " + var1.getName());
         } else {
            String var3 = "<init>";
            MemberName var4 = this.resolveOrFail((byte)8, var1, var3, (MethodType)var2);
            return this.getDirectConstructor(var1, var4);
         }
      }

      public MethodHandle findSpecial(Class<?> var1, String var2, MethodType var3, Class<?> var4) throws NoSuchMethodException, IllegalAccessException {
         this.checkSpecialCaller(var4);
         MethodHandles.Lookup var5 = this.in(var4);
         MemberName var6 = var5.resolveOrFail((byte)7, var1, var2, (MethodType)var3);
         return var5.getDirectMethod((byte)7, var1, var6, this.findBoundCallerClass(var6));
      }

      public MethodHandle findGetter(Class<?> var1, String var2, Class<?> var3) throws NoSuchFieldException, IllegalAccessException {
         MemberName var4 = this.resolveOrFail((byte)1, var1, var2, (Class)var3);
         return this.getDirectField((byte)1, var1, var4);
      }

      public MethodHandle findSetter(Class<?> var1, String var2, Class<?> var3) throws NoSuchFieldException, IllegalAccessException {
         MemberName var4 = this.resolveOrFail((byte)3, var1, var2, (Class)var3);
         return this.getDirectField((byte)3, var1, var4);
      }

      public MethodHandle findStaticGetter(Class<?> var1, String var2, Class<?> var3) throws NoSuchFieldException, IllegalAccessException {
         MemberName var4 = this.resolveOrFail((byte)2, var1, var2, (Class)var3);
         return this.getDirectField((byte)2, var1, var4);
      }

      public MethodHandle findStaticSetter(Class<?> var1, String var2, Class<?> var3) throws NoSuchFieldException, IllegalAccessException {
         MemberName var4 = this.resolveOrFail((byte)4, var1, var2, (Class)var3);
         return this.getDirectField((byte)4, var1, var4);
      }

      public MethodHandle bind(Object var1, String var2, MethodType var3) throws NoSuchMethodException, IllegalAccessException {
         Class var4 = var1.getClass();
         MemberName var5 = this.resolveOrFail((byte)7, var4, var2, (MethodType)var3);
         MethodHandle var6 = this.getDirectMethodNoRestrict((byte)7, var4, var5, this.findBoundCallerClass(var5));
         return var6.bindArgumentL(0, var1).setVarargs(var5);
      }

      public MethodHandle unreflect(Method var1) throws IllegalAccessException {
         if (var1.getDeclaringClass() == MethodHandle.class) {
            MethodHandle var2 = this.unreflectForMH(var1);
            if (var2 != null) {
               return var2;
            }
         }

         MemberName var5 = new MemberName(var1);
         byte var3 = var5.getReferenceKind();
         if (var3 == 7) {
            var3 = 5;
         }

         assert var5.isMethod();

         MethodHandles.Lookup var4 = var1.isAccessible() ? IMPL_LOOKUP : this;
         return var4.getDirectMethodNoSecurityManager(var3, var5.getDeclaringClass(), var5, this.findBoundCallerClass(var5));
      }

      private MethodHandle unreflectForMH(Method var1) {
         return MemberName.isMethodHandleInvokeName(var1.getName()) ? MethodHandleImpl.fakeMethodHandleInvoke(new MemberName(var1)) : null;
      }

      public MethodHandle unreflectSpecial(Method var1, Class<?> var2) throws IllegalAccessException {
         this.checkSpecialCaller(var2);
         MethodHandles.Lookup var3 = this.in(var2);
         MemberName var4 = new MemberName(var1, true);

         assert var4.isMethod();

         return var3.getDirectMethodNoSecurityManager((byte)7, var4.getDeclaringClass(), var4, this.findBoundCallerClass(var4));
      }

      public MethodHandle unreflectConstructor(Constructor<?> var1) throws IllegalAccessException {
         MemberName var2 = new MemberName(var1);

         assert var2.isConstructor();

         MethodHandles.Lookup var3 = var1.isAccessible() ? IMPL_LOOKUP : this;
         return var3.getDirectConstructorNoSecurityManager(var2.getDeclaringClass(), var2);
      }

      public MethodHandle unreflectGetter(Field var1) throws IllegalAccessException {
         return this.unreflectField(var1, false);
      }

      private MethodHandle unreflectField(Field var1, boolean var2) throws IllegalAccessException {
         MemberName var3 = new MemberName(var1, var2);
         if (!$assertionsDisabled) {
            if (var2) {
               if (!MethodHandleNatives.refKindIsSetter(var3.getReferenceKind())) {
                  throw new AssertionError();
               }
            } else if (!MethodHandleNatives.refKindIsGetter(var3.getReferenceKind())) {
               throw new AssertionError();
            }
         }

         MethodHandles.Lookup var4 = var1.isAccessible() ? IMPL_LOOKUP : this;
         return var4.getDirectFieldNoSecurityManager(var3.getReferenceKind(), var1.getDeclaringClass(), var3);
      }

      public MethodHandle unreflectSetter(Field var1) throws IllegalAccessException {
         return this.unreflectField(var1, true);
      }

      public MethodHandleInfo revealDirect(MethodHandle var1) {
         MemberName var2 = var1.internalMemberName();
         if (var2 != null && (var2.isResolved() || var2.isMethodHandleInvoke())) {
            Class var3 = var2.getDeclaringClass();
            byte var4 = var2.getReferenceKind();

            assert MethodHandleNatives.refKindIsValid(var4);

            if (var4 == 7 && !var1.isInvokeSpecial()) {
               var4 = 5;
            }

            if (var4 == 5 && var3.isInterface()) {
               var4 = 9;
            }

            try {
               this.checkAccess(var4, var3, var2);
               this.checkSecurityManager(var3, var2);
            } catch (IllegalAccessException var6) {
               throw new IllegalArgumentException(var6);
            }

            if (this.allowedModes != -1 && var2.isCallerSensitive()) {
               Class var5 = var1.internalCallerClass();
               if (!this.hasPrivateAccess() || var5 != this.lookupClass()) {
                  throw new IllegalArgumentException("method handle is caller sensitive: " + var5);
               }
            }

            return new InfoFromMemberName(this, var2, var4);
         } else {
            throw MethodHandleStatics.newIllegalArgumentException("not a direct method handle");
         }
      }

      MemberName resolveOrFail(byte var1, Class<?> var2, String var3, Class<?> var4) throws NoSuchFieldException, IllegalAccessException {
         this.checkSymbolicClass(var2);
         var3.getClass();
         var4.getClass();
         return MethodHandles.IMPL_NAMES.resolveOrFail(var1, new MemberName(var2, var3, var4, var1), this.lookupClassOrNull(), NoSuchFieldException.class);
      }

      MemberName resolveOrFail(byte var1, Class<?> var2, String var3, MethodType var4) throws NoSuchMethodException, IllegalAccessException {
         this.checkSymbolicClass(var2);
         var3.getClass();
         var4.getClass();
         this.checkMethodName(var1, var3);
         return MethodHandles.IMPL_NAMES.resolveOrFail(var1, new MemberName(var2, var3, var4, var1), this.lookupClassOrNull(), NoSuchMethodException.class);
      }

      MemberName resolveOrFail(byte var1, MemberName var2) throws ReflectiveOperationException {
         this.checkSymbolicClass(var2.getDeclaringClass());
         var2.getName().getClass();
         var2.getType().getClass();
         return MethodHandles.IMPL_NAMES.resolveOrFail(var1, var2, this.lookupClassOrNull(), ReflectiveOperationException.class);
      }

      void checkSymbolicClass(Class<?> var1) throws IllegalAccessException {
         var1.getClass();
         Class var2 = this.lookupClassOrNull();
         if (var2 != null && !VerifyAccess.isClassAccessible(var1, var2, this.allowedModes)) {
            throw (new MemberName(var1)).makeAccessException("symbolic reference class is not public", this);
         }
      }

      void checkMethodName(byte var1, String var2) throws NoSuchMethodException {
         if (var2.startsWith("<") && var1 != 8) {
            throw new NoSuchMethodException("illegal method name: " + var2);
         }
      }

      Class<?> findBoundCallerClass(MemberName var1) throws IllegalAccessException {
         Class var2 = null;
         if (MethodHandleNatives.isCallerSensitive(var1)) {
            if (!this.hasPrivateAccess()) {
               throw new IllegalAccessException("Attempt to lookup caller-sensitive method using restricted lookup object");
            }

            var2 = this.lookupClass;
         }

         return var2;
      }

      private boolean hasPrivateAccess() {
         return (this.allowedModes & 2) != 0;
      }

      void checkSecurityManager(Class<?> var1, MemberName var2) {
         SecurityManager var3 = System.getSecurityManager();
         if (var3 != null) {
            if (this.allowedModes != -1) {
               boolean var4 = this.hasPrivateAccess();
               if (!var4 || !VerifyAccess.classLoaderIsAncestor(this.lookupClass, var1)) {
                  ReflectUtil.checkPackageAccess(var1);
               }

               if (!var2.isPublic()) {
                  if (!var4) {
                     var3.checkPermission(SecurityConstants.CHECK_MEMBER_ACCESS_PERMISSION);
                  }

                  Class var5 = var2.getDeclaringClass();
                  if (!var4 && var5 != var1) {
                     ReflectUtil.checkPackageAccess(var5);
                  }

               }
            }
         }
      }

      void checkMethod(byte var1, Class<?> var2, MemberName var3) throws IllegalAccessException {
         boolean var4 = var1 == 6;
         String var5;
         if (var3.isConstructor()) {
            var5 = "expected a method, not a constructor";
         } else if (!var3.isMethod()) {
            var5 = "expected a method";
         } else {
            if (var4 == var3.isStatic()) {
               this.checkAccess(var1, var2, var3);
               return;
            }

            var5 = var4 ? "expected a static method" : "expected a non-static method";
         }

         throw var3.makeAccessException(var5, this);
      }

      void checkField(byte var1, Class<?> var2, MemberName var3) throws IllegalAccessException {
         boolean var4 = !MethodHandleNatives.refKindHasReceiver(var1);
         if (var4 != var3.isStatic()) {
            String var5 = var4 ? "expected a static field" : "expected a non-static field";
            throw var3.makeAccessException(var5, this);
         } else {
            this.checkAccess(var1, var2, var3);
         }
      }

      void checkAccess(byte var1, Class<?> var2, MemberName var3) throws IllegalAccessException {
         assert var3.referenceKindIsConsistentWith(var1) && MethodHandleNatives.refKindIsValid(var1) && MethodHandleNatives.refKindIsField(var1) == var3.isField();

         int var4 = this.allowedModes;
         if (var4 != -1) {
            int var5 = var3.getModifiers();
            if (Modifier.isProtected(var5) && var1 == 5 && var3.getDeclaringClass() == Object.class && var3.getName().equals("clone") && var2.isArray()) {
               var5 ^= 5;
            }

            if (Modifier.isProtected(var5) && var1 == 8) {
               var5 ^= 4;
            }

            if (Modifier.isFinal(var5) && MethodHandleNatives.refKindIsSetter(var1)) {
               throw var3.makeAccessException("unexpected set of a final field", this);
            } else if (!Modifier.isPublic(var5) || !Modifier.isPublic(var2.getModifiers()) || var4 == 0) {
               int var6 = fixmods(var5);
               if ((var6 & var4) != 0) {
                  if (VerifyAccess.isMemberAccessible(var2, var3.getDeclaringClass(), var5, this.lookupClass(), var4)) {
                     return;
                  }
               } else if ((var6 & 4) != 0 && (var4 & 8) != 0 && VerifyAccess.isSamePackage(var3.getDeclaringClass(), this.lookupClass())) {
                  return;
               }

               throw var3.makeAccessException(this.accessFailedMessage(var2, var3), this);
            }
         }
      }

      String accessFailedMessage(Class<?> var1, MemberName var2) {
         Class var3 = var2.getDeclaringClass();
         int var4 = var2.getModifiers();
         boolean var5 = Modifier.isPublic(var3.getModifiers()) && (var3 == var1 || Modifier.isPublic(var1.getModifiers()));
         if (!var5 && (this.allowedModes & 8) != 0) {
            var5 = VerifyAccess.isClassAccessible(var3, this.lookupClass(), 15) && (var3 == var1 || VerifyAccess.isClassAccessible(var1, this.lookupClass(), 15));
         }

         if (!var5) {
            return "class is not public";
         } else if (Modifier.isPublic(var4)) {
            return "access to public member failed";
         } else if (Modifier.isPrivate(var4)) {
            return "member is private";
         } else {
            return Modifier.isProtected(var4) ? "member is protected" : "member is private to package";
         }
      }

      private void checkSpecialCaller(Class<?> var1) throws IllegalAccessException {
         int var2 = this.allowedModes;
         if (var2 != -1) {
            if (!this.hasPrivateAccess() || var1 != this.lookupClass()) {
               throw (new MemberName(var1)).makeAccessException("no private access for invokespecial", this);
            }
         }
      }

      private boolean restrictProtectedReceiver(MemberName var1) {
         return var1.isProtected() && !var1.isStatic() && this.allowedModes != -1 && var1.getDeclaringClass() != this.lookupClass() && !VerifyAccess.isSamePackage(var1.getDeclaringClass(), this.lookupClass());
      }

      private MethodHandle restrictReceiver(MemberName var1, DirectMethodHandle var2, Class<?> var3) throws IllegalAccessException {
         assert !var1.isStatic();

         if (!var1.getDeclaringClass().isAssignableFrom(var3)) {
            throw var1.makeAccessException("caller class must be a subclass below the method", var3);
         } else {
            MethodType var4 = var2.type();
            if (var4.parameterType(0) == var3) {
               return var2;
            } else {
               MethodType var5 = var4.changeParameterType(0, var3);

               assert !var2.isVarargsCollector();

               assert var2.viewAsTypeChecks(var5, true);

               return var2.copyWith(var5, var2.form);
            }
         }
      }

      private MethodHandle getDirectMethod(byte var1, Class<?> var2, MemberName var3, Class<?> var4) throws IllegalAccessException {
         return this.getDirectMethodCommon(var1, var2, var3, true, true, var4);
      }

      private MethodHandle getDirectMethodNoRestrict(byte var1, Class<?> var2, MemberName var3, Class<?> var4) throws IllegalAccessException {
         return this.getDirectMethodCommon(var1, var2, var3, true, false, var4);
      }

      private MethodHandle getDirectMethodNoSecurityManager(byte var1, Class<?> var2, MemberName var3, Class<?> var4) throws IllegalAccessException {
         return this.getDirectMethodCommon(var1, var2, var3, false, true, var4);
      }

      private MethodHandle getDirectMethodCommon(byte var1, Class<?> var2, MemberName var3, boolean var4, boolean var5, Class<?> var6) throws IllegalAccessException {
         this.checkMethod(var1, var2, var3);
         if (var4) {
            this.checkSecurityManager(var2, var3);
         }

         assert !var3.isMethodHandleInvoke();

         if (var1 == 7 && var2 != this.lookupClass() && !var2.isInterface() && var2 != this.lookupClass().getSuperclass() && var2.isAssignableFrom(this.lookupClass())) {
            assert !var3.getName().equals("<init>");

            Class var7 = this.lookupClass();

            MemberName var8;
            do {
               var7 = var7.getSuperclass();
               var8 = new MemberName(var7, var3.getName(), var3.getMethodType(), (byte)7);
               var8 = MethodHandles.IMPL_NAMES.resolveOrNull(var1, var8, this.lookupClassOrNull());
            } while(var8 == null && var2 != var7);

            if (var8 == null) {
               throw new InternalError(var3.toString());
            }

            var3 = var8;
            var2 = var7;
            this.checkMethod(var1, var7, var8);
         }

         DirectMethodHandle var9 = DirectMethodHandle.make(var1, var2, var3);
         Object var10 = var9;
         if (var5 && (var1 == 7 || MethodHandleNatives.refKindHasReceiver(var1) && this.restrictProtectedReceiver(var3))) {
            var10 = this.restrictReceiver(var3, var9, this.lookupClass());
         }

         MethodHandle var11 = this.maybeBindCaller(var3, (MethodHandle)var10, var6);
         var11 = var11.setVarargs(var3);
         return var11;
      }

      private MethodHandle maybeBindCaller(MemberName var1, MethodHandle var2, Class<?> var3) throws IllegalAccessException {
         if (this.allowedModes != -1 && MethodHandleNatives.isCallerSensitive(var1)) {
            Class var4 = this.lookupClass;
            if (!this.hasPrivateAccess()) {
               var4 = var3;
            }

            MethodHandle var5 = MethodHandleImpl.bindCaller(var2, var4);
            return var5;
         } else {
            return var2;
         }
      }

      private MethodHandle getDirectField(byte var1, Class<?> var2, MemberName var3) throws IllegalAccessException {
         return this.getDirectFieldCommon(var1, var2, var3, true);
      }

      private MethodHandle getDirectFieldNoSecurityManager(byte var1, Class<?> var2, MemberName var3) throws IllegalAccessException {
         return this.getDirectFieldCommon(var1, var2, var3, false);
      }

      private MethodHandle getDirectFieldCommon(byte var1, Class<?> var2, MemberName var3, boolean var4) throws IllegalAccessException {
         this.checkField(var1, var2, var3);
         if (var4) {
            this.checkSecurityManager(var2, var3);
         }

         DirectMethodHandle var5 = DirectMethodHandle.make(var2, var3);
         boolean var6 = MethodHandleNatives.refKindHasReceiver(var1) && this.restrictProtectedReceiver(var3);
         return (MethodHandle)(var6 ? this.restrictReceiver(var3, var5, this.lookupClass()) : var5);
      }

      private MethodHandle getDirectConstructor(Class<?> var1, MemberName var2) throws IllegalAccessException {
         return this.getDirectConstructorCommon(var1, var2, true);
      }

      private MethodHandle getDirectConstructorNoSecurityManager(Class<?> var1, MemberName var2) throws IllegalAccessException {
         return this.getDirectConstructorCommon(var1, var2, false);
      }

      private MethodHandle getDirectConstructorCommon(Class<?> var1, MemberName var2, boolean var3) throws IllegalAccessException {
         assert var2.isConstructor();

         this.checkAccess((byte)8, var1, var2);
         if (var3) {
            this.checkSecurityManager(var1, var2);
         }

         assert !MethodHandleNatives.isCallerSensitive(var2);

         return DirectMethodHandle.make(var2).setVarargs(var2);
      }

      MethodHandle linkMethodHandleConstant(byte var1, Class<?> var2, String var3, Object var4) throws ReflectiveOperationException {
         if (!(var4 instanceof Class) && !(var4 instanceof MethodType)) {
            throw new InternalError("unresolved MemberName");
         } else {
            MemberName var5 = new MemberName(var1, var2, var3, var4);
            MethodHandle var6 = (MethodHandle)LOOKASIDE_TABLE.get(var5);
            if (var6 != null) {
               this.checkSymbolicClass(var2);
               return var6;
            } else {
               if (var2 == MethodHandle.class && var1 == 5) {
                  var6 = this.findVirtualForMH(var5.getName(), var5.getMethodType());
                  if (var6 != null) {
                     return var6;
                  }
               }

               MemberName var7 = this.resolveOrFail(var1, var5);
               var6 = this.getDirectMethodForConstant(var1, var2, var7);
               if (var6 instanceof DirectMethodHandle && this.canBeCached(var1, var2, var7)) {
                  MemberName var8 = var6.internalMemberName();
                  if (var8 != null) {
                     var8 = var8.asNormalOriginal();
                  }

                  if (var5.equals(var8)) {
                     LOOKASIDE_TABLE.put(var8, (DirectMethodHandle)var6);
                  }
               }

               return var6;
            }
         }
      }

      private boolean canBeCached(byte var1, Class<?> var2, MemberName var3) {
         if (var1 == 7) {
            return false;
         } else if (Modifier.isPublic(var2.getModifiers()) && Modifier.isPublic(var3.getDeclaringClass().getModifiers()) && var3.isPublic() && !var3.isCallerSensitive()) {
            ClassLoader var4 = var2.getClassLoader();
            if (!VM.isSystemDomainLoader(var4)) {
               ClassLoader var5 = ClassLoader.getSystemClassLoader();

               boolean var6;
               for(var6 = false; var5 != null; var5 = var5.getParent()) {
                  if (var4 == var5) {
                     var6 = true;
                     break;
                  }
               }

               if (!var6) {
                  return false;
               }
            }

            try {
               MemberName var8 = MethodHandles.publicLookup().resolveOrFail(var1, new MemberName(var1, var2, var3.getName(), var3.getType()));
               this.checkSecurityManager(var2, var8);
               return true;
            } catch (SecurityException | ReflectiveOperationException var7) {
               return false;
            }
         } else {
            return false;
         }
      }

      private MethodHandle getDirectMethodForConstant(byte var1, Class<?> var2, MemberName var3) throws ReflectiveOperationException {
         if (MethodHandleNatives.refKindIsField(var1)) {
            return this.getDirectFieldNoSecurityManager(var1, var2, var3);
         } else if (MethodHandleNatives.refKindIsMethod(var1)) {
            return this.getDirectMethodNoSecurityManager(var1, var2, var3, this.lookupClass);
         } else if (var1 == 8) {
            return this.getDirectConstructorNoSecurityManager(var2, var3);
         } else {
            throw MethodHandleStatics.newIllegalArgumentException("bad MethodHandle constant #" + var3);
         }
      }

      static {
         MethodHandles.IMPL_NAMES.getClass();
         PUBLIC_LOOKUP = new MethodHandles.Lookup(Object.class, 1);
         IMPL_LOOKUP = new MethodHandles.Lookup(Object.class, -1);
         LOOKASIDE_TABLE = new ConcurrentHashMap();
      }
   }
}
