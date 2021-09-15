package java.lang.invoke;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import sun.invoke.util.Wrapper;

class LambdaForm {
   final int arity;
   final int result;
   final boolean forceInline;
   final MethodHandle customized;
   @Stable
   final LambdaForm.Name[] names;
   final String debugName;
   MemberName vmentry;
   private boolean isCompiled;
   volatile Object transformCache;
   public static final int VOID_RESULT = -1;
   public static final int LAST_RESULT = -2;
   private static final boolean USE_PREDEFINED_INTERPRET_METHODS = true;
   private static final int COMPILE_THRESHOLD;
   private int invocationCounter;
   static final int INTERNED_ARGUMENT_LIMIT = 10;
   private static final LambdaForm.Name[][] INTERNED_ARGUMENTS;
   private static final MemberName.Factory IMPL_NAMES;
   private static final LambdaForm[] LF_identityForm;
   private static final LambdaForm[] LF_zeroForm;
   private static final LambdaForm.NamedFunction[] NF_identity;
   private static final LambdaForm.NamedFunction[] NF_zero;
   private static final HashMap<String, Integer> DEBUG_NAME_COUNTERS;
   private static final boolean TRACE_INTERPRETER;

   LambdaForm(String var1, int var2, LambdaForm.Name[] var3, int var4) {
      this(var1, var2, var3, var4, true, (MethodHandle)null);
   }

   LambdaForm(String var1, int var2, LambdaForm.Name[] var3, int var4, boolean var5, MethodHandle var6) {
      this.invocationCounter = 0;

      assert namesOK(var2, var3);

      this.arity = var2;
      this.result = fixResult(var4, var3);
      this.names = (LambdaForm.Name[])var3.clone();
      this.debugName = fixDebugName(var1);
      this.forceInline = var5;
      this.customized = var6;
      int var7 = this.normalize();
      if (var7 > 253) {
         assert var7 <= 255;

         this.compileToBytecode();
      }

   }

   LambdaForm(String var1, int var2, LambdaForm.Name[] var3) {
      this(var1, var2, var3, -2, true, (MethodHandle)null);
   }

   LambdaForm(String var1, int var2, LambdaForm.Name[] var3, boolean var4) {
      this(var1, var2, var3, -2, var4, (MethodHandle)null);
   }

   LambdaForm(String var1, LambdaForm.Name[] var2, LambdaForm.Name[] var3, LambdaForm.Name var4) {
      this(var1, var2.length, buildNames(var2, var3, var4), -2, true, (MethodHandle)null);
   }

   LambdaForm(String var1, LambdaForm.Name[] var2, LambdaForm.Name[] var3, LambdaForm.Name var4, boolean var5) {
      this(var1, var2.length, buildNames(var2, var3, var4), -2, var5, (MethodHandle)null);
   }

   private static LambdaForm.Name[] buildNames(LambdaForm.Name[] var0, LambdaForm.Name[] var1, LambdaForm.Name var2) {
      int var3 = var0.length;
      int var4 = var3 + var1.length + (var2 == null ? 0 : 1);
      LambdaForm.Name[] var5 = (LambdaForm.Name[])Arrays.copyOf((Object[])var0, var4);
      System.arraycopy(var1, 0, var5, var3, var1.length);
      if (var2 != null) {
         var5[var4 - 1] = var2;
      }

      return var5;
   }

   private LambdaForm(String var1) {
      this.invocationCounter = 0;

      assert isValidSignature(var1);

      this.arity = signatureArity(var1);
      this.result = signatureReturn(var1) == LambdaForm.BasicType.V_TYPE ? -1 : this.arity;
      this.names = buildEmptyNames(this.arity, var1);
      this.debugName = "LF.zero";
      this.forceInline = true;
      this.customized = null;

      assert this.nameRefsAreLegal();

      assert this.isEmpty();

      assert var1.equals(this.basicTypeSignature()) : var1 + " != " + this.basicTypeSignature();

   }

   private static LambdaForm.Name[] buildEmptyNames(int var0, String var1) {
      assert isValidSignature(var1);

      int var2 = var0 + 1;
      if (var0 >= 0 && var1.length() == var2 + 1) {
         int var3 = LambdaForm.BasicType.basicType(var1.charAt(var2)) == LambdaForm.BasicType.V_TYPE ? 0 : 1;
         LambdaForm.Name[] var4 = arguments(var3, var1.substring(0, var0));

         for(int var5 = 0; var5 < var3; ++var5) {
            LambdaForm.Name var6 = new LambdaForm.Name(constantZero(LambdaForm.BasicType.basicType(var1.charAt(var2 + var5))), new Object[0]);
            var4[var0 + var5] = var6.newIndex(var0 + var5);
         }

         return var4;
      } else {
         throw new IllegalArgumentException("bad arity for " + var1);
      }
   }

   private static int fixResult(int var0, LambdaForm.Name[] var1) {
      if (var0 == -2) {
         var0 = var1.length - 1;
      }

      if (var0 >= 0 && var1[var0].type == LambdaForm.BasicType.V_TYPE) {
         var0 = -1;
      }

      return var0;
   }

   private static String fixDebugName(String var0) {
      if (DEBUG_NAME_COUNTERS == null) {
         return var0;
      } else {
         int var1 = var0.indexOf(95);
         int var2 = var0.length();
         if (var1 < 0) {
            var1 = var2;
         }

         String var3 = var0.substring(0, var1);
         Integer var4;
         synchronized(DEBUG_NAME_COUNTERS) {
            var4 = (Integer)DEBUG_NAME_COUNTERS.get(var3);
            if (var4 == null) {
               var4 = 0;
            }

            DEBUG_NAME_COUNTERS.put(var3, var4 + 1);
         }

         StringBuilder var5 = new StringBuilder(var3);
         var5.append('_');
         int var6 = var5.length();
         var5.append(var4);

         for(int var7 = var5.length() - var6; var7 < 3; ++var7) {
            var5.insert(var6, '0');
         }

         if (var1 < var2) {
            ++var1;

            while(var1 < var2 && Character.isDigit(var0.charAt(var1))) {
               ++var1;
            }

            if (var1 < var2 && var0.charAt(var1) == '_') {
               ++var1;
            }

            if (var1 < var2) {
               var5.append('_').append((CharSequence)var0, var1, var2);
            }
         }

         return var5.toString();
      }
   }

   private static boolean namesOK(int var0, LambdaForm.Name[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         LambdaForm.Name var3 = var1[var2];

         assert var3 != null : "n is null";

         if (var2 < var0) {
            assert var3.isParam() : var3 + " is not param at " + var2;
         } else {
            assert !var3.isParam() : var3 + " is param at " + var2;
         }
      }

      return true;
   }

   LambdaForm customize(MethodHandle var1) {
      LambdaForm var2 = new LambdaForm(this.debugName, this.arity, this.names, this.result, this.forceInline, var1);
      if (COMPILE_THRESHOLD > 0 && this.isCompiled) {
         var2.compileToBytecode();
      }

      var2.transformCache = this;
      return var2;
   }

   LambdaForm uncustomize() {
      if (this.customized == null) {
         return this;
      } else {
         assert this.transformCache != null;

         LambdaForm var1 = (LambdaForm)this.transformCache;
         if (COMPILE_THRESHOLD > 0 && this.isCompiled) {
            var1.compileToBytecode();
         }

         return var1;
      }
   }

   private int normalize() {
      LambdaForm.Name[] var1 = null;
      int var2 = 0;
      int var3 = 0;

      int var4;
      for(var4 = 0; var4 < this.names.length; ++var4) {
         LambdaForm.Name var5 = this.names[var4];
         if (!var5.initIndex(var4)) {
            if (var1 == null) {
               var1 = (LambdaForm.Name[])this.names.clone();
               var3 = var4;
            }

            this.names[var4] = var5.cloneWithIndex(var4);
         }

         if (var5.arguments != null && var2 < var5.arguments.length) {
            var2 = var5.arguments.length;
         }
      }

      if (var1 != null) {
         var4 = this.arity;
         if (var4 <= var3) {
            var4 = var3 + 1;
         }

         for(int var9 = var4; var9 < this.names.length; ++var9) {
            LambdaForm.Name var6 = this.names[var9].replaceNames(var1, this.names, var3, var9);
            this.names[var9] = var6.newIndex(var9);
         }
      }

      assert this.nameRefsAreLegal();

      var4 = Math.min(this.arity, 10);
      boolean var10 = false;

      int var11;
      for(var11 = 0; var11 < var4; ++var11) {
         LambdaForm.Name var7 = this.names[var11];
         LambdaForm.Name var8 = internArgument(var7);
         if (var7 != var8) {
            this.names[var11] = var8;
            var10 = true;
         }
      }

      if (var10) {
         for(var11 = this.arity; var11 < this.names.length; ++var11) {
            this.names[var11].internArguments();
         }
      }

      assert this.nameRefsAreLegal();

      return var2;
   }

   boolean nameRefsAreLegal() {
      assert this.arity >= 0 && this.arity <= this.names.length;

      assert this.result >= -1 && this.result < this.names.length;

      int var1;
      LambdaForm.Name var2;
      for(var1 = 0; var1 < this.arity; ++var1) {
         var2 = this.names[var1];

         assert var2.index() == var1 : Arrays.asList(var2.index(), var1);

         assert var2.isParam();
      }

      for(var1 = this.arity; var1 < this.names.length; ++var1) {
         var2 = this.names[var1];

         assert var2.index() == var1;

         Object[] var3 = var2.arguments;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Object var6 = var3[var5];
            if (var6 instanceof LambdaForm.Name) {
               LambdaForm.Name var7 = (LambdaForm.Name)var6;
               short var8 = var7.index;

               assert 0 <= var8 && var8 < this.names.length : var2.debugString() + ": 0 <= i2 && i2 < names.length: 0 <= " + var8 + " < " + this.names.length;

               assert this.names[var8] == var7 : Arrays.asList("-1-", var1, "-2-", var2.debugString(), "-3-", Integer.valueOf(var8), "-4-", var7.debugString(), "-5-", this.names[var8].debugString(), "-6-", this);

               assert var8 < var1;
            }
         }
      }

      return true;
   }

   LambdaForm.BasicType returnType() {
      if (this.result < 0) {
         return LambdaForm.BasicType.V_TYPE;
      } else {
         LambdaForm.Name var1 = this.names[this.result];
         return var1.type;
      }
   }

   LambdaForm.BasicType parameterType(int var1) {
      return this.parameter(var1).type;
   }

   LambdaForm.Name parameter(int var1) {
      assert var1 < this.arity;

      LambdaForm.Name var2 = this.names[var1];

      assert var2.isParam();

      return var2;
   }

   Object parameterConstraint(int var1) {
      return this.parameter(var1).constraint;
   }

   int arity() {
      return this.arity;
   }

   int expressionCount() {
      return this.names.length - this.arity;
   }

   MethodType methodType() {
      return signatureType(this.basicTypeSignature());
   }

   final String basicTypeSignature() {
      StringBuilder var1 = new StringBuilder(this.arity() + 3);
      int var2 = 0;

      for(int var3 = this.arity(); var2 < var3; ++var2) {
         var1.append(this.parameterType(var2).basicTypeChar());
      }

      return var1.append('_').append(this.returnType().basicTypeChar()).toString();
   }

   static int signatureArity(String var0) {
      assert isValidSignature(var0);

      return var0.indexOf(95);
   }

   static LambdaForm.BasicType signatureReturn(String var0) {
      return LambdaForm.BasicType.basicType(var0.charAt(signatureArity(var0) + 1));
   }

   static boolean isValidSignature(String var0) {
      int var1 = var0.indexOf(95);
      if (var1 < 0) {
         return false;
      } else {
         int var2 = var0.length();
         if (var2 != var1 + 2) {
            return false;
         } else {
            for(int var3 = 0; var3 < var2; ++var3) {
               if (var3 != var1) {
                  char var4 = var0.charAt(var3);
                  if (var4 == 'V') {
                     return var3 == var2 - 1 && var1 == var2 - 2;
                  }

                  if (!LambdaForm.BasicType.isArgBasicTypeChar(var4)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   static MethodType signatureType(String var0) {
      Class[] var1 = new Class[signatureArity(var0)];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = LambdaForm.BasicType.basicType(var0.charAt(var2)).btClass;
      }

      Class var3 = signatureReturn(var0).btClass;
      return MethodType.methodType(var3, var1);
   }

   public void prepare() {
      if (COMPILE_THRESHOLD == 0 && !this.isCompiled) {
         this.compileToBytecode();
      }

      if (this.vmentry == null) {
         LambdaForm var1 = getPreparedForm(this.basicTypeSignature());
         this.vmentry = var1.vmentry;
      }
   }

   MemberName compileToBytecode() {
      if (this.vmentry != null && this.isCompiled) {
         return this.vmentry;
      } else {
         MethodType var1 = this.methodType();

         assert this.vmentry == null || this.vmentry.getMethodType().basicType().equals((Object)var1);

         try {
            this.vmentry = InvokerBytecodeGenerator.generateCustomizedCode(this, var1);
            if (TRACE_INTERPRETER) {
               traceInterpreter("compileToBytecode", this);
            }

            this.isCompiled = true;
            return this.vmentry;
         } catch (Exception | Error var3) {
            throw MethodHandleStatics.newInternalError(this.toString(), var3);
         }
      }
   }

   private static void computeInitialPreparedForms() {
      Iterator var0 = MemberName.getFactory().getMethods(LambdaForm.class, false, (String)null, (MethodType)null, (Class)null).iterator();

      while(var0.hasNext()) {
         MemberName var1 = (MemberName)var0.next();
         if (var1.isStatic() && var1.isPackage()) {
            MethodType var2 = var1.getMethodType();
            if (var2.parameterCount() > 0 && var2.parameterType(0) == MethodHandle.class && var1.getName().startsWith("interpret_")) {
               String var3 = basicTypeSignature(var2);

               assert var1.getName().equals("interpret" + var3.substring(var3.indexOf(95)));

               LambdaForm var4 = new LambdaForm(var3);
               var4.vmentry = var1;
               var4 = var2.form().setCachedLambdaForm(6, var4);
            }
         }
      }

   }

   static Object interpret_L(MethodHandle var0) throws Throwable {
      Object[] var1 = new Object[]{var0};
      String var2 = null;
      if (!$assertionsDisabled) {
         var2 = "L_L";
         if (!argumentTypesMatch("L_L", var1)) {
            throw new AssertionError();
         }
      }

      Object var3 = var0.form.interpretWithArguments(var1);

      assert returnTypesMatch(var2, var1, var3);

      return var3;
   }

   static Object interpret_L(MethodHandle var0, Object var1) throws Throwable {
      Object[] var2 = new Object[]{var0, var1};
      String var3 = null;
      if (!$assertionsDisabled) {
         var3 = "LL_L";
         if (!argumentTypesMatch("LL_L", var2)) {
            throw new AssertionError();
         }
      }

      Object var4 = var0.form.interpretWithArguments(var2);

      assert returnTypesMatch(var3, var2, var4);

      return var4;
   }

   static Object interpret_L(MethodHandle var0, Object var1, Object var2) throws Throwable {
      Object[] var3 = new Object[]{var0, var1, var2};
      String var4 = null;
      if (!$assertionsDisabled) {
         var4 = "LLL_L";
         if (!argumentTypesMatch("LLL_L", var3)) {
            throw new AssertionError();
         }
      }

      Object var5 = var0.form.interpretWithArguments(var3);

      assert returnTypesMatch(var4, var3, var5);

      return var5;
   }

   private static LambdaForm getPreparedForm(String var0) {
      MethodType var1 = signatureType(var0);
      LambdaForm var2 = var1.form().cachedLambdaForm(6);
      if (var2 != null) {
         return var2;
      } else {
         assert isValidSignature(var0);

         var2 = new LambdaForm(var0);
         var2.vmentry = InvokerBytecodeGenerator.generateLambdaFormInterpreterEntryPoint(var0);
         return var1.form().setCachedLambdaForm(6, var2);
      }
   }

   private static boolean argumentTypesMatch(String var0, Object[] var1) {
      int var2 = signatureArity(var0);

      assert var1.length == var2 : "av.length == arity: av.length=" + var1.length + ", arity=" + var2;

      assert var1[0] instanceof MethodHandle : "av[0] not instace of MethodHandle: " + var1[0];

      MethodHandle var3 = (MethodHandle)var1[0];
      MethodType var4 = var3.type();

      assert var4.parameterCount() == var2 - 1;

      for(int var5 = 0; var5 < var1.length; ++var5) {
         Class var6 = var5 == 0 ? MethodHandle.class : var4.parameterType(var5 - 1);

         assert valueMatches(LambdaForm.BasicType.basicType(var0.charAt(var5)), var6, var1[var5]);
      }

      return true;
   }

   private static boolean valueMatches(LambdaForm.BasicType var0, Class<?> var1, Object var2) {
      if (var1 == Void.TYPE) {
         var0 = LambdaForm.BasicType.V_TYPE;
      }

      assert var0 == LambdaForm.BasicType.basicType(var1) : var0 + " == basicType(" + var1 + ")=" + LambdaForm.BasicType.basicType(var1);

      switch(var0) {
      case I_TYPE:
         assert checkInt(var1, var2) : "checkInt(" + var1 + "," + var2 + ")";
         break;
      case J_TYPE:
         assert var2 instanceof Long : "instanceof Long: " + var2;
         break;
      case F_TYPE:
         assert var2 instanceof Float : "instanceof Float: " + var2;
         break;
      case D_TYPE:
         assert var2 instanceof Double : "instanceof Double: " + var2;
         break;
      case L_TYPE:
         assert checkRef(var1, var2) : "checkRef(" + var1 + "," + var2 + ")";
      case V_TYPE:
         break;
      default:
         assert false;
      }

      return true;
   }

   private static boolean returnTypesMatch(String var0, Object[] var1, Object var2) {
      MethodHandle var3 = (MethodHandle)var1[0];
      return valueMatches(signatureReturn(var0), var3.type().returnType(), var2);
   }

   private static boolean checkInt(Class<?> var0, Object var1) {
      assert var1 instanceof Integer;

      if (var0 == Integer.TYPE) {
         return true;
      } else {
         Wrapper var2 = Wrapper.forBasicType(var0);

         assert var2.isSubwordOrInt();

         Object var3 = Wrapper.INT.wrap(var2.wrap(var1));
         return var1.equals(var3);
      }
   }

   private static boolean checkRef(Class<?> var0, Object var1) {
      assert !var0.isPrimitive();

      if (var1 == null) {
         return true;
      } else {
         return var0.isInterface() ? true : var0.isInstance(var1);
      }
   }

   @LambdaForm.Hidden
   @DontInline
   Object interpretWithArguments(Object... var1) throws Throwable {
      if (TRACE_INTERPRETER) {
         return this.interpretWithArgumentsTracing(var1);
      } else {
         this.checkInvocationCounter();

         assert this.arityCheck(var1);

         Object[] var2 = Arrays.copyOf(var1, this.names.length);

         for(int var3 = var1.length; var3 < var2.length; ++var3) {
            var2[var3] = this.interpretName(this.names[var3], var2);
         }

         Object var4 = this.result < 0 ? null : var2[this.result];

         assert this.resultCheck(var1, var4);

         return var4;
      }
   }

   @LambdaForm.Hidden
   @DontInline
   Object interpretName(LambdaForm.Name var1, Object[] var2) throws Throwable {
      if (TRACE_INTERPRETER) {
         traceInterpreter("| interpretName", var1.debugString(), (Object[])null);
      }

      Object[] var3 = Arrays.copyOf(var1.arguments, var1.arguments.length, Object[].class);

      for(int var4 = 0; var4 < var3.length; ++var4) {
         Object var5 = var3[var4];
         if (var5 instanceof LambdaForm.Name) {
            int var6 = ((LambdaForm.Name)var5).index();

            assert this.names[var6] == var5;

            var5 = var2[var6];
            var3[var4] = var5;
         }
      }

      return var1.function.invokeWithArguments(var3);
   }

   private void checkInvocationCounter() {
      if (COMPILE_THRESHOLD != 0 && this.invocationCounter < COMPILE_THRESHOLD) {
         ++this.invocationCounter;
         if (this.invocationCounter >= COMPILE_THRESHOLD) {
            this.compileToBytecode();
         }
      }

   }

   Object interpretWithArgumentsTracing(Object... var1) throws Throwable {
      traceInterpreter("[ interpretWithArguments", this, var1);
      if (this.invocationCounter < COMPILE_THRESHOLD) {
         int var2 = this.invocationCounter++;
         traceInterpreter("| invocationCounter", var2);
         if (this.invocationCounter >= COMPILE_THRESHOLD) {
            this.compileToBytecode();
         }
      }

      Object var6;
      try {
         assert this.arityCheck(var1);

         Object[] var3 = Arrays.copyOf(var1, this.names.length);

         for(int var4 = var1.length; var4 < var3.length; ++var4) {
            var3[var4] = this.interpretName(this.names[var4], var3);
         }

         var6 = this.result < 0 ? null : var3[this.result];
      } catch (Throwable var5) {
         traceInterpreter("] throw =>", var5);
         throw var5;
      }

      traceInterpreter("] return =>", var6);
      return var6;
   }

   static void traceInterpreter(String var0, Object var1, Object... var2) {
      if (TRACE_INTERPRETER) {
         System.out.println("LFI: " + var0 + " " + (var1 != null ? var1 : "") + (var2 != null && var2.length != 0 ? Arrays.asList(var2) : ""));
      }

   }

   static void traceInterpreter(String var0, Object var1) {
      traceInterpreter(var0, var1, (Object[])null);
   }

   private boolean arityCheck(Object[] var1) {
      assert var1.length == this.arity : this.arity + "!=" + Arrays.asList(var1) + ".length";

      assert var1[0] instanceof MethodHandle : "not MH: " + var1[0];

      MethodHandle var2 = (MethodHandle)var1[0];

      assert var2.internalForm() == this;

      argumentTypesMatch(this.basicTypeSignature(), var1);
      return true;
   }

   private boolean resultCheck(Object[] var1, Object var2) {
      MethodHandle var3 = (MethodHandle)var1[0];
      MethodType var4 = var3.type();

      assert valueMatches(this.returnType(), var4.returnType(), var2);

      return true;
   }

   private boolean isEmpty() {
      if (this.result < 0) {
         return this.names.length == this.arity;
      } else {
         return this.result == this.arity && this.names.length == this.arity + 1 ? this.names[this.arity].isConstantZero() : false;
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(this.debugName + "=Lambda(");

      for(int var2 = 0; var2 < this.names.length; ++var2) {
         if (var2 == this.arity) {
            var1.append(")=>{");
         }

         LambdaForm.Name var3 = this.names[var2];
         if (var2 >= this.arity) {
            var1.append("\n    ");
         }

         var1.append(var3.paramString());
         if (var2 < this.arity) {
            if (var2 + 1 < this.arity) {
               var1.append(",");
            }
         } else {
            var1.append("=").append(var3.exprString());
            var1.append(";");
         }
      }

      if (this.arity == this.names.length) {
         var1.append(")=>{");
      }

      var1.append(this.result < 0 ? "void" : this.names[this.result]).append("}");
      if (TRACE_INTERPRETER) {
         var1.append(":").append(this.basicTypeSignature());
         var1.append("/").append((Object)this.vmentry);
      }

      return var1.toString();
   }

   public boolean equals(Object var1) {
      return var1 instanceof LambdaForm && this.equals((LambdaForm)var1);
   }

   public boolean equals(LambdaForm var1) {
      return this.result != var1.result ? false : Arrays.equals((Object[])this.names, (Object[])var1.names);
   }

   public int hashCode() {
      return this.result + 31 * Arrays.hashCode((Object[])this.names);
   }

   LambdaFormEditor editor() {
      return LambdaFormEditor.lambdaFormEditor(this);
   }

   boolean contains(LambdaForm.Name var1) {
      int var2 = var1.index();
      if (var2 < 0) {
         for(int var3 = this.arity; var3 < this.names.length; ++var3) {
            if (var1.equals(this.names[var3])) {
               return true;
            }
         }

         return false;
      } else {
         return var2 < this.names.length && var1.equals(this.names[var2]);
      }
   }

   LambdaForm addArguments(int var1, LambdaForm.BasicType... var2) {
      int var3 = var1 + 1;

      assert var3 <= this.arity;

      int var4 = this.names.length;
      int var5 = var2.length;
      LambdaForm.Name[] var6 = (LambdaForm.Name[])Arrays.copyOf((Object[])this.names, var4 + var5);
      int var7 = this.arity + var5;
      int var8 = this.result;
      if (var8 >= var3) {
         var8 += var5;
      }

      System.arraycopy(this.names, var3, var6, var3 + var5, var4 - var3);

      for(int var9 = 0; var9 < var5; ++var9) {
         var6[var3 + var9] = new LambdaForm.Name(var2[var9]);
      }

      return new LambdaForm(this.debugName, var7, var6, var8);
   }

   LambdaForm addArguments(int var1, List<Class<?>> var2) {
      return this.addArguments(var1, LambdaForm.BasicType.basicTypes(var2));
   }

   LambdaForm permuteArguments(int var1, int[] var2, LambdaForm.BasicType[] var3) {
      int var4 = this.names.length;
      int var5 = var3.length;
      int var6 = var2.length;

      assert var1 + var6 == this.arity;

      assert permutedTypesMatch(var2, var3, this.names, var1);

      int var7;
      for(var7 = 0; var7 < var6 && var2[var7] == var7; ++var7) {
      }

      LambdaForm.Name[] var8 = new LambdaForm.Name[var4 - var6 + var5];
      System.arraycopy(this.names, 0, var8, 0, var1 + var7);
      int var9 = var4 - this.arity;
      System.arraycopy(this.names, var1 + var6, var8, var1 + var5, var9);
      int var10 = var8.length - var9;
      int var11 = this.result;
      if (var11 >= 0) {
         if (var11 < var1 + var6) {
            var11 = var2[var11 - var1];
         } else {
            var11 = var11 - var6 + var5;
         }
      }

      int var12;
      LambdaForm.Name var15;
      int var16;
      for(var12 = var7; var12 < var6; ++var12) {
         LambdaForm.Name var13 = this.names[var1 + var12];
         int var14 = var2[var12];
         var15 = var8[var1 + var14];
         if (var15 == null) {
            var8[var1 + var14] = var15 = new LambdaForm.Name(var3[var14]);
         } else {
            assert var15.type == var3[var14];
         }

         for(var16 = var10; var16 < var8.length; ++var16) {
            var8[var16] = var8[var16].replaceName(var13, var15);
         }
      }

      for(var12 = var1 + var7; var12 < var10; ++var12) {
         if (var8[var12] == null) {
            var8[var12] = argument(var12, var3[var12 - var1]);
         }
      }

      for(var12 = this.arity; var12 < this.names.length; ++var12) {
         int var17 = var12 - this.arity + var10;
         LambdaForm.Name var18 = this.names[var12];
         var15 = var8[var17];
         if (var18 != var15) {
            for(var16 = var17 + 1; var16 < var8.length; ++var16) {
               var8[var16] = var8[var16].replaceName(var18, var15);
            }
         }
      }

      return new LambdaForm(this.debugName, var10, var8, var11);
   }

   static boolean permutedTypesMatch(int[] var0, LambdaForm.BasicType[] var1, LambdaForm.Name[] var2, int var3) {
      int var4 = var1.length;
      int var5 = var0.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         assert var2[var3 + var6].isParam();

         assert var2[var3 + var6].type == var1[var0[var6]];
      }

      return true;
   }

   public static String basicTypeSignature(MethodType var0) {
      char[] var1 = new char[var0.parameterCount() + 2];
      int var2 = 0;

      Class var4;
      for(Iterator var3 = var0.parameterList().iterator(); var3.hasNext(); var1[var2++] = LambdaForm.BasicType.basicTypeChar(var4)) {
         var4 = (Class)var3.next();
      }

      var1[var2++] = '_';
      var1[var2++] = LambdaForm.BasicType.basicTypeChar(var0.returnType());

      assert var2 == var1.length;

      return String.valueOf(var1);
   }

   public static String shortenSignature(String var0) {
      int var4 = -1;
      int var5 = 0;
      StringBuilder var6 = null;
      int var7 = var0.length();
      if (var7 < 3) {
         return var0;
      } else {
         for(int var8 = 0; var8 <= var7; ++var8) {
            int var3 = var4;
            var4 = var8 == var7 ? -1 : var0.charAt(var8);
            if (var4 == var3) {
               ++var5;
            } else {
               int var9 = var5;
               var5 = 1;
               if (var9 < 3) {
                  if (var6 != null) {
                     while(true) {
                        --var9;
                        if (var9 < 0) {
                           break;
                        }

                        var6.append((char)var3);
                     }
                  }
               } else {
                  if (var6 == null) {
                     var6 = (new StringBuilder()).append((CharSequence)var0, 0, var8 - var9);
                  }

                  var6.append((char)var3).append(var9);
               }
            }
         }

         return var6 == null ? var0 : var6.toString();
      }
   }

   int lastUseIndex(LambdaForm.Name var1) {
      short var2 = var1.index;
      int var3 = this.names.length;

      assert this.names[var2] == var1;

      if (this.result == var2) {
         return var3;
      } else {
         int var4 = var3;

         do {
            --var4;
            if (var4 <= var2) {
               return -1;
            }
         } while(this.names[var4].lastUseIndex(var1) < 0);

         return var4;
      }
   }

   int useCount(LambdaForm.Name var1) {
      short var2 = var1.index;
      int var3 = this.names.length;
      int var4 = this.lastUseIndex(var1);
      if (var4 < 0) {
         return 0;
      } else {
         int var5 = 0;
         if (var4 == var3) {
            ++var5;
            --var4;
         }

         int var6 = var1.index() + 1;
         if (var6 < this.arity) {
            var6 = this.arity;
         }

         for(int var7 = var6; var7 <= var4; ++var7) {
            var5 += this.names[var7].useCount(var1);
         }

         return var5;
      }
   }

   static LambdaForm.Name argument(int var0, char var1) {
      return argument(var0, LambdaForm.BasicType.basicType(var1));
   }

   static LambdaForm.Name argument(int var0, LambdaForm.BasicType var1) {
      return var0 >= 10 ? new LambdaForm.Name(var0, var1) : INTERNED_ARGUMENTS[var1.ordinal()][var0];
   }

   static LambdaForm.Name internArgument(LambdaForm.Name var0) {
      assert var0.isParam() : "not param: " + var0;

      assert var0.index < 10;

      return var0.constraint != null ? var0 : argument(var0.index, var0.type);
   }

   static LambdaForm.Name[] arguments(int var0, String var1) {
      int var2 = var1.length();
      LambdaForm.Name[] var3 = new LambdaForm.Name[var2 + var0];

      for(int var4 = 0; var4 < var2; ++var4) {
         var3[var4] = argument(var4, var1.charAt(var4));
      }

      return var3;
   }

   static LambdaForm.Name[] arguments(int var0, char... var1) {
      int var2 = var1.length;
      LambdaForm.Name[] var3 = new LambdaForm.Name[var2 + var0];

      for(int var4 = 0; var4 < var2; ++var4) {
         var3[var4] = argument(var4, var1[var4]);
      }

      return var3;
   }

   static LambdaForm.Name[] arguments(int var0, List<Class<?>> var1) {
      int var2 = var1.size();
      LambdaForm.Name[] var3 = new LambdaForm.Name[var2 + var0];

      for(int var4 = 0; var4 < var2; ++var4) {
         var3[var4] = argument(var4, LambdaForm.BasicType.basicType((Class)var1.get(var4)));
      }

      return var3;
   }

   static LambdaForm.Name[] arguments(int var0, Class<?>... var1) {
      int var2 = var1.length;
      LambdaForm.Name[] var3 = new LambdaForm.Name[var2 + var0];

      for(int var4 = 0; var4 < var2; ++var4) {
         var3[var4] = argument(var4, LambdaForm.BasicType.basicType(var1[var4]));
      }

      return var3;
   }

   static LambdaForm.Name[] arguments(int var0, MethodType var1) {
      int var2 = var1.parameterCount();
      LambdaForm.Name[] var3 = new LambdaForm.Name[var2 + var0];

      for(int var4 = 0; var4 < var2; ++var4) {
         var3[var4] = argument(var4, LambdaForm.BasicType.basicType(var1.parameterType(var4)));
      }

      return var3;
   }

   static LambdaForm identityForm(LambdaForm.BasicType var0) {
      return LF_identityForm[var0.ordinal()];
   }

   static LambdaForm zeroForm(LambdaForm.BasicType var0) {
      return LF_zeroForm[var0.ordinal()];
   }

   static LambdaForm.NamedFunction identity(LambdaForm.BasicType var0) {
      return NF_identity[var0.ordinal()];
   }

   static LambdaForm.NamedFunction constantZero(LambdaForm.BasicType var0) {
      return NF_zero[var0.ordinal()];
   }

   private static void createIdentityForms() {
      LambdaForm.BasicType[] var0 = LambdaForm.BasicType.ALL_TYPES;
      int var1 = var0.length;

      int var2;
      LambdaForm.BasicType var3;
      int var4;
      MemberName var10;
      for(var2 = 0; var2 < var1; ++var2) {
         var3 = var0[var2];
         var4 = var3.ordinal();
         char var5 = var3.basicTypeChar();
         boolean var6 = var3 == LambdaForm.BasicType.V_TYPE;
         Class var7 = var3.btClass;
         MethodType var8 = MethodType.methodType(var7);
         MethodType var9 = var6 ? var8 : var8.appendParameterTypes(var7);
         var10 = new MemberName(LambdaForm.class, "identity_" + var5, var9, (byte)6);
         MemberName var11 = new MemberName(LambdaForm.class, "zero_" + var5, var8, (byte)6);

         try {
            var11 = IMPL_NAMES.resolveOrFail((byte)6, var11, (Class)null, NoSuchMethodException.class);
            var10 = IMPL_NAMES.resolveOrFail((byte)6, var10, (Class)null, NoSuchMethodException.class);
         } catch (NoSuchMethodException | IllegalAccessException var18) {
            throw MethodHandleStatics.newInternalError((Throwable)var18);
         }

         LambdaForm.NamedFunction var12 = new LambdaForm.NamedFunction(var10);
         LambdaForm var13;
         LambdaForm.Name[] var14;
         if (var6) {
            var14 = new LambdaForm.Name[]{argument(0, LambdaForm.BasicType.L_TYPE)};
            var13 = new LambdaForm(var10.getName(), 1, var14, -1);
         } else {
            var14 = new LambdaForm.Name[]{argument(0, LambdaForm.BasicType.L_TYPE), argument(1, var3)};
            var13 = new LambdaForm(var10.getName(), 2, var14, 1);
         }

         LF_identityForm[var4] = var13;
         NF_identity[var4] = var12;
         LambdaForm.NamedFunction var24 = new LambdaForm.NamedFunction(var11);
         LambdaForm var15;
         if (var6) {
            var15 = var13;
         } else {
            Object var16 = Wrapper.forBasicType(var5).zero();
            LambdaForm.Name[] var17 = new LambdaForm.Name[]{argument(0, LambdaForm.BasicType.L_TYPE), new LambdaForm.Name(var12, new Object[]{var16})};
            var15 = new LambdaForm(var11.getName(), 1, var17, 1);
         }

         LF_zeroForm[var4] = var15;
         NF_zero[var4] = var24;

         assert var12.isIdentity();

         assert var24.isConstantZero();

         assert (new LambdaForm.Name(var24, new Object[0])).isConstantZero();
      }

      var0 = LambdaForm.BasicType.ALL_TYPES;
      var1 = var0.length;

      for(var2 = 0; var2 < var1; ++var2) {
         var3 = var0[var2];
         var4 = var3.ordinal();
         LambdaForm.NamedFunction var19 = NF_identity[var4];
         LambdaForm var20 = LF_identityForm[var4];
         MemberName var21 = var19.member;
         var19.resolvedHandle = SimpleMethodHandle.make(var21.getInvocationType(), var20);
         LambdaForm.NamedFunction var22 = NF_zero[var4];
         LambdaForm var23 = LF_zeroForm[var4];
         var10 = var22.member;
         var22.resolvedHandle = SimpleMethodHandle.make(var10.getInvocationType(), var23);

         assert var19.isIdentity();

         assert var22.isConstantZero();

         assert (new LambdaForm.Name(var22, new Object[0])).isConstantZero();
      }

   }

   private static int identity_I(int var0) {
      return var0;
   }

   private static long identity_J(long var0) {
      return var0;
   }

   private static float identity_F(float var0) {
      return var0;
   }

   private static double identity_D(double var0) {
      return var0;
   }

   private static Object identity_L(Object var0) {
      return var0;
   }

   private static void identity_V() {
   }

   private static int zero_I() {
      return 0;
   }

   private static long zero_J() {
      return 0L;
   }

   private static float zero_F() {
      return 0.0F;
   }

   private static double zero_D() {
      return 0.0D;
   }

   private static Object zero_L() {
      return null;
   }

   private static void zero_V() {
   }

   static {
      COMPILE_THRESHOLD = Math.max(-1, MethodHandleStatics.COMPILE_THRESHOLD);
      INTERNED_ARGUMENTS = new LambdaForm.Name[LambdaForm.BasicType.ARG_TYPE_LIMIT][10];
      LambdaForm.BasicType[] var0 = LambdaForm.BasicType.ARG_TYPES;
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         LambdaForm.BasicType var3 = var0[var2];
         int var4 = var3.ordinal();

         for(int var5 = 0; var5 < INTERNED_ARGUMENTS[var4].length; ++var5) {
            INTERNED_ARGUMENTS[var4][var5] = new LambdaForm.Name(var5, var3);
         }
      }

      IMPL_NAMES = MemberName.getFactory();
      LF_identityForm = new LambdaForm[LambdaForm.BasicType.TYPE_LIMIT];
      LF_zeroForm = new LambdaForm[LambdaForm.BasicType.TYPE_LIMIT];
      NF_identity = new LambdaForm.NamedFunction[LambdaForm.BasicType.TYPE_LIMIT];
      NF_zero = new LambdaForm.NamedFunction[LambdaForm.BasicType.TYPE_LIMIT];
      if (MethodHandleStatics.debugEnabled()) {
         DEBUG_NAME_COUNTERS = new HashMap();
      } else {
         DEBUG_NAME_COUNTERS = null;
      }

      createIdentityForms();
      computeInitialPreparedForms();
      LambdaForm.NamedFunction.initializeInvokers();
      TRACE_INTERPRETER = MethodHandleStatics.TRACE_INTERPRETER;
   }

   @Target({ElementType.METHOD})
   @Retention(RetentionPolicy.RUNTIME)
   @interface Hidden {
   }

   @Target({ElementType.METHOD})
   @Retention(RetentionPolicy.RUNTIME)
   @interface Compiled {
   }

   static final class Name {
      final LambdaForm.BasicType type;
      private short index;
      final LambdaForm.NamedFunction function;
      final Object constraint;
      @Stable
      final Object[] arguments;

      private Name(int var1, LambdaForm.BasicType var2, LambdaForm.NamedFunction var3, Object[] var4) {
         this.index = (short)var1;
         this.type = var2;
         this.function = var3;
         this.arguments = var4;
         this.constraint = null;

         assert this.index == var1;

      }

      private Name(LambdaForm.Name var1, Object var2) {
         this.index = var1.index;
         this.type = var1.type;
         this.function = var1.function;
         this.arguments = var1.arguments;
         this.constraint = var2;

         assert var2 == null || this.isParam();

         assert var2 == null || var2 instanceof BoundMethodHandle.SpeciesData || var2 instanceof Class;

      }

      Name(MethodHandle var1, Object... var2) {
         this(new LambdaForm.NamedFunction(var1), var2);
      }

      Name(MethodType var1, Object... var2) {
         this(new LambdaForm.NamedFunction(var1), var2);

         assert var2[0] instanceof LambdaForm.Name && ((LambdaForm.Name)var2[0]).type == LambdaForm.BasicType.L_TYPE;
      }

      Name(MemberName var1, Object... var2) {
         this(new LambdaForm.NamedFunction(var1), var2);
      }

      Name(LambdaForm.NamedFunction var1, Object... var2) {
         this(-1, var1.returnType(), var1, var2 = Arrays.copyOf(var2, var2.length, Object[].class));

         assert var2.length == var1.arity() : "arity mismatch: arguments.length=" + var2.length + " == function.arity()=" + var1.arity() + " in " + this.debugString();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            assert typesMatch(var1.parameterType(var3), var2[var3]) : "types don't match: function.parameterType(" + var3 + ")=" + var1.parameterType(var3) + ", arguments[" + var3 + "]=" + var2[var3] + " in " + this.debugString();
         }

      }

      Name(int var1, LambdaForm.BasicType var2) {
         this(var1, var2, (LambdaForm.NamedFunction)null, (Object[])null);
      }

      Name(LambdaForm.BasicType var1) {
         this(-1, var1);
      }

      LambdaForm.BasicType type() {
         return this.type;
      }

      int index() {
         return this.index;
      }

      boolean initIndex(int var1) {
         if (this.index != var1) {
            if (this.index != -1) {
               return false;
            }

            this.index = (short)var1;
         }

         return true;
      }

      char typeChar() {
         return this.type.btChar;
      }

      void resolve() {
         if (this.function != null) {
            this.function.resolve();
         }

      }

      LambdaForm.Name newIndex(int var1) {
         return this.initIndex(var1) ? this : this.cloneWithIndex(var1);
      }

      LambdaForm.Name cloneWithIndex(int var1) {
         Object[] var2 = this.arguments == null ? null : (Object[])this.arguments.clone();
         return (new LambdaForm.Name(var1, this.type, this.function, var2)).withConstraint(this.constraint);
      }

      LambdaForm.Name withConstraint(Object var1) {
         return var1 == this.constraint ? this : new LambdaForm.Name(this, var1);
      }

      LambdaForm.Name replaceName(LambdaForm.Name var1, LambdaForm.Name var2) {
         if (var1 == var2) {
            return this;
         } else {
            Object[] var3 = this.arguments;
            if (var3 == null) {
               return this;
            } else {
               boolean var4 = false;

               for(int var5 = 0; var5 < var3.length; ++var5) {
                  if (var3[var5] == var1) {
                     if (!var4) {
                        var4 = true;
                        var3 = (Object[])var3.clone();
                     }

                     var3[var5] = var2;
                  }
               }

               if (!var4) {
                  return this;
               } else {
                  return new LambdaForm.Name(this.function, var3);
               }
            }
         }
      }

      LambdaForm.Name replaceNames(LambdaForm.Name[] var1, LambdaForm.Name[] var2, int var3, int var4) {
         if (var3 >= var4) {
            return this;
         } else {
            Object[] var5 = this.arguments;
            boolean var6 = false;

            for(int var7 = 0; var7 < var5.length; ++var7) {
               if (var5[var7] instanceof LambdaForm.Name) {
                  LambdaForm.Name var8 = (LambdaForm.Name)var5[var7];
                  short var9 = var8.index;
                  if (var9 < 0 || var9 >= var2.length || var8 != var2[var9]) {
                     for(int var10 = var3; var10 < var4; ++var10) {
                        if (var8 == var1[var10]) {
                           if (var8 != var2[var10]) {
                              if (!var6) {
                                 var6 = true;
                                 var5 = (Object[])var5.clone();
                              }

                              var5[var7] = var2[var10];
                           }
                           break;
                        }
                     }
                  }
               }
            }

            if (!var6) {
               return this;
            } else {
               return new LambdaForm.Name(this.function, var5);
            }
         }
      }

      void internArguments() {
         Object[] var1 = this.arguments;

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2] instanceof LambdaForm.Name) {
               LambdaForm.Name var3 = (LambdaForm.Name)var1[var2];
               if (var3.isParam() && var3.index < 10) {
                  var1[var2] = LambdaForm.internArgument(var3);
               }
            }
         }

      }

      boolean isParam() {
         return this.function == null;
      }

      boolean isConstantZero() {
         return !this.isParam() && this.arguments.length == 0 && this.function.isConstantZero();
      }

      public String toString() {
         return (this.isParam() ? "a" : "t") + (this.index >= 0 ? this.index : System.identityHashCode(this)) + ":" + this.typeChar();
      }

      public String debugString() {
         String var1 = this.paramString();
         return this.function == null ? var1 : var1 + "=" + this.exprString();
      }

      public String paramString() {
         String var1 = this.toString();
         Object var2 = this.constraint;
         if (var2 == null) {
            return var1;
         } else {
            if (var2 instanceof Class) {
               var2 = ((Class)var2).getSimpleName();
            }

            return var1 + "/" + var2;
         }
      }

      public String exprString() {
         if (this.function == null) {
            return this.toString();
         } else {
            StringBuilder var1 = new StringBuilder(this.function.toString());
            var1.append("(");
            String var2 = "";
            Object[] var3 = this.arguments;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Object var6 = var3[var5];
               var1.append(var2);
               var2 = ",";
               if (!(var6 instanceof LambdaForm.Name) && !(var6 instanceof Integer)) {
                  var1.append("(").append(var6).append(")");
               } else {
                  var1.append(var6);
               }
            }

            var1.append(")");
            return var1.toString();
         }
      }

      static boolean typesMatch(LambdaForm.BasicType var0, Object var1) {
         if (var1 instanceof LambdaForm.Name) {
            return ((LambdaForm.Name)var1).type == var0;
         } else {
            switch(var0) {
            case I_TYPE:
               return var1 instanceof Integer;
            case J_TYPE:
               return var1 instanceof Long;
            case F_TYPE:
               return var1 instanceof Float;
            case D_TYPE:
               return var1 instanceof Double;
            default:
               assert var0 == LambdaForm.BasicType.L_TYPE;

               return true;
            }
         }
      }

      int lastUseIndex(LambdaForm.Name var1) {
         if (this.arguments == null) {
            return -1;
         } else {
            int var2 = this.arguments.length;

            do {
               --var2;
               if (var2 < 0) {
                  return -1;
               }
            } while(this.arguments[var2] != var1);

            return var2;
         }
      }

      int useCount(LambdaForm.Name var1) {
         if (this.arguments == null) {
            return 0;
         } else {
            int var2 = 0;
            int var3 = this.arguments.length;

            while(true) {
               --var3;
               if (var3 < 0) {
                  return var2;
               }

               if (this.arguments[var3] == var1) {
                  ++var2;
               }
            }
         }
      }

      boolean contains(LambdaForm.Name var1) {
         return this == var1 || this.lastUseIndex(var1) >= 0;
      }

      public boolean equals(LambdaForm.Name var1) {
         if (this == var1) {
            return true;
         } else if (this.isParam()) {
            return false;
         } else {
            return this.type == var1.type && this.function.equals(var1.function) && Arrays.equals(this.arguments, var1.arguments);
         }
      }

      public boolean equals(Object var1) {
         return var1 instanceof LambdaForm.Name && this.equals((LambdaForm.Name)var1);
      }

      public int hashCode() {
         return this.isParam() ? this.index | this.type.ordinal() << 8 : this.function.hashCode() ^ Arrays.hashCode(this.arguments);
      }
   }

   static class NamedFunction {
      final MemberName member;
      @Stable
      MethodHandle resolvedHandle;
      @Stable
      MethodHandle invoker;
      static final MethodType INVOKER_METHOD_TYPE = MethodType.methodType(Object.class, MethodHandle.class, Object[].class);

      NamedFunction(MethodHandle var1) {
         this(var1.internalMemberName(), var1);
      }

      NamedFunction(MemberName var1, MethodHandle var2) {
         this.member = var1;
         this.resolvedHandle = var2;
      }

      NamedFunction(MethodType var1) {
         assert var1 == var1.basicType() : var1;

         if (var1.parameterSlotCount() < 253) {
            this.resolvedHandle = var1.invokers().basicInvoker();
            this.member = this.resolvedHandle.internalMemberName();
         } else {
            this.member = Invokers.invokeBasicMethod(var1);
         }

         assert isInvokeBasic(this.member);

      }

      private static boolean isInvokeBasic(MemberName var0) {
         return var0 != null && var0.getDeclaringClass() == MethodHandle.class && "invokeBasic".equals(var0.getName());
      }

      NamedFunction(Method var1) {
         this(new MemberName(var1));
      }

      NamedFunction(Field var1) {
         this(new MemberName(var1));
      }

      NamedFunction(MemberName var1) {
         this.member = var1;
         this.resolvedHandle = null;
      }

      MethodHandle resolvedHandle() {
         if (this.resolvedHandle == null) {
            this.resolve();
         }

         return this.resolvedHandle;
      }

      void resolve() {
         this.resolvedHandle = DirectMethodHandle.make(this.member);
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 == null) {
            return false;
         } else if (!(var1 instanceof LambdaForm.NamedFunction)) {
            return false;
         } else {
            LambdaForm.NamedFunction var2 = (LambdaForm.NamedFunction)var1;
            return this.member != null && this.member.equals(var2.member);
         }
      }

      public int hashCode() {
         return this.member != null ? this.member.hashCode() : super.hashCode();
      }

      static void initializeInvokers() {
         Iterator var0 = MemberName.getFactory().getMethods(LambdaForm.NamedFunction.class, false, (String)null, (MethodType)null, (Class)null).iterator();

         while(var0.hasNext()) {
            MemberName var1 = (MemberName)var0.next();
            if (var1.isStatic() && var1.isPackage()) {
               MethodType var2 = var1.getMethodType();
               if (var2.equals((Object)INVOKER_METHOD_TYPE) && var1.getName().startsWith("invoke_")) {
                  String var3 = var1.getName().substring("invoke_".length());
                  int var4 = LambdaForm.signatureArity(var3);
                  MethodType var5 = MethodType.genericMethodType(var4);
                  if (LambdaForm.signatureReturn(var3) == LambdaForm.BasicType.V_TYPE) {
                     var5 = var5.changeReturnType(Void.TYPE);
                  }

                  MethodTypeForm var6 = var5.form();
                  var6.setCachedMethodHandle(1, DirectMethodHandle.make(var1));
               }
            }
         }

      }

      @LambdaForm.Hidden
      static Object invoke__V(MethodHandle var0, Object[] var1) throws Throwable {
         assert arityCheck(0, Void.TYPE, var0, var1);

         var0.invokeBasic();
         return null;
      }

      @LambdaForm.Hidden
      static Object invoke_L_V(MethodHandle var0, Object[] var1) throws Throwable {
         assert arityCheck(1, Void.TYPE, var0, var1);

         var0.invokeBasic(var1[0]);
         return null;
      }

      @LambdaForm.Hidden
      static Object invoke_LL_V(MethodHandle var0, Object[] var1) throws Throwable {
         assert arityCheck(2, Void.TYPE, var0, var1);

         var0.invokeBasic(var1[0], var1[1]);
         return null;
      }

      @LambdaForm.Hidden
      static Object invoke_LLL_V(MethodHandle var0, Object[] var1) throws Throwable {
         assert arityCheck(3, Void.TYPE, var0, var1);

         var0.invokeBasic(var1[0], var1[1], var1[2]);
         return null;
      }

      @LambdaForm.Hidden
      static Object invoke_LLLL_V(MethodHandle var0, Object[] var1) throws Throwable {
         assert arityCheck(4, Void.TYPE, var0, var1);

         var0.invokeBasic(var1[0], var1[1], var1[2], var1[3]);
         return null;
      }

      @LambdaForm.Hidden
      static Object invoke_LLLLL_V(MethodHandle var0, Object[] var1) throws Throwable {
         assert arityCheck(5, Void.TYPE, var0, var1);

         var0.invokeBasic(var1[0], var1[1], var1[2], var1[3], var1[4]);
         return null;
      }

      @LambdaForm.Hidden
      static Object invoke__L(MethodHandle var0, Object[] var1) throws Throwable {
         assert arityCheck(0, var0, var1);

         return var0.invokeBasic();
      }

      @LambdaForm.Hidden
      static Object invoke_L_L(MethodHandle var0, Object[] var1) throws Throwable {
         assert arityCheck(1, var0, var1);

         return var0.invokeBasic(var1[0]);
      }

      @LambdaForm.Hidden
      static Object invoke_LL_L(MethodHandle var0, Object[] var1) throws Throwable {
         assert arityCheck(2, var0, var1);

         return var0.invokeBasic(var1[0], var1[1]);
      }

      @LambdaForm.Hidden
      static Object invoke_LLL_L(MethodHandle var0, Object[] var1) throws Throwable {
         assert arityCheck(3, var0, var1);

         return var0.invokeBasic(var1[0], var1[1], var1[2]);
      }

      @LambdaForm.Hidden
      static Object invoke_LLLL_L(MethodHandle var0, Object[] var1) throws Throwable {
         assert arityCheck(4, var0, var1);

         return var0.invokeBasic(var1[0], var1[1], var1[2], var1[3]);
      }

      @LambdaForm.Hidden
      static Object invoke_LLLLL_L(MethodHandle var0, Object[] var1) throws Throwable {
         assert arityCheck(5, var0, var1);

         return var0.invokeBasic(var1[0], var1[1], var1[2], var1[3], var1[4]);
      }

      private static boolean arityCheck(int var0, MethodHandle var1, Object[] var2) {
         return arityCheck(var0, Object.class, var1, var2);
      }

      private static boolean arityCheck(int var0, Class<?> var1, MethodHandle var2, Object[] var3) {
         assert var3.length == var0 : Arrays.asList(var3.length, var0);

         assert var2.type().basicType() == MethodType.genericMethodType(var0).changeReturnType(var1) : Arrays.asList(var2, var1, var0);

         MemberName var4 = var2.internalMemberName();
         if (isInvokeBasic(var4)) {
            assert var0 > 0;

            assert var3[0] instanceof MethodHandle;

            MethodHandle var5 = (MethodHandle)var3[0];

            assert var5.type().basicType() == MethodType.genericMethodType(var0 - 1).changeReturnType(var1) : Arrays.asList(var4, var5, var1, var0);
         }

         return true;
      }

      private static MethodHandle computeInvoker(MethodTypeForm var0) {
         var0 = var0.basicType().form();
         MethodHandle var1 = var0.cachedMethodHandle(1);
         if (var1 != null) {
            return var1;
         } else {
            MemberName var2 = InvokerBytecodeGenerator.generateNamedFunctionInvoker(var0);
            DirectMethodHandle var4 = DirectMethodHandle.make(var2);
            MethodHandle var3 = var0.cachedMethodHandle(1);
            if (var3 != null) {
               return var3;
            } else if (!var4.type().equals((Object)INVOKER_METHOD_TYPE)) {
               throw MethodHandleStatics.newInternalError(var4.debugString());
            } else {
               return var0.setCachedMethodHandle(1, var4);
            }
         }
      }

      @LambdaForm.Hidden
      Object invokeWithArguments(Object... var1) throws Throwable {
         if (LambdaForm.TRACE_INTERPRETER) {
            return this.invokeWithArgumentsTracing(var1);
         } else {
            assert checkArgumentTypes(var1, this.methodType());

            return this.invoker().invokeBasic(this.resolvedHandle(), var1);
         }
      }

      @LambdaForm.Hidden
      Object invokeWithArgumentsTracing(Object[] var1) throws Throwable {
         Object var2;
         try {
            LambdaForm.traceInterpreter("[ call", this, var1);
            if (this.invoker == null) {
               LambdaForm.traceInterpreter("| getInvoker", this);
               this.invoker();
            }

            if (this.resolvedHandle == null) {
               LambdaForm.traceInterpreter("| resolve", this);
               this.resolvedHandle();
            }

            assert checkArgumentTypes(var1, this.methodType());

            var2 = this.invoker().invokeBasic(this.resolvedHandle(), var1);
         } catch (Throwable var4) {
            LambdaForm.traceInterpreter("] throw =>", var4);
            throw var4;
         }

         LambdaForm.traceInterpreter("] return =>", var2);
         return var2;
      }

      private MethodHandle invoker() {
         return this.invoker != null ? this.invoker : (this.invoker = computeInvoker(this.methodType().form()));
      }

      private static boolean checkArgumentTypes(Object[] var0, MethodType var1) {
         return true;
      }

      MethodType methodType() {
         return this.resolvedHandle != null ? this.resolvedHandle.type() : this.member.getInvocationType();
      }

      MemberName member() {
         assert this.assertMemberIsConsistent();

         return this.member;
      }

      private boolean assertMemberIsConsistent() {
         if (this.resolvedHandle instanceof DirectMethodHandle) {
            MemberName var1 = this.resolvedHandle.internalMemberName();

            assert var1.equals(this.member);
         }

         return true;
      }

      Class<?> memberDeclaringClassOrNull() {
         return this.member == null ? null : this.member.getDeclaringClass();
      }

      LambdaForm.BasicType returnType() {
         return LambdaForm.BasicType.basicType(this.methodType().returnType());
      }

      LambdaForm.BasicType parameterType(int var1) {
         return LambdaForm.BasicType.basicType(this.methodType().parameterType(var1));
      }

      int arity() {
         return this.methodType().parameterCount();
      }

      public String toString() {
         return this.member == null ? String.valueOf((Object)this.resolvedHandle) : this.member.getDeclaringClass().getSimpleName() + "." + this.member.getName();
      }

      public boolean isIdentity() {
         return this.equals(LambdaForm.identity(this.returnType()));
      }

      public boolean isConstantZero() {
         return this.equals(LambdaForm.constantZero(this.returnType()));
      }

      public MethodHandleImpl.Intrinsic intrinsicName() {
         return this.resolvedHandle == null ? MethodHandleImpl.Intrinsic.NONE : this.resolvedHandle.intrinsicName();
      }
   }

   static enum BasicType {
      L_TYPE('L', Object.class, Wrapper.OBJECT),
      I_TYPE('I', Integer.TYPE, Wrapper.INT),
      J_TYPE('J', Long.TYPE, Wrapper.LONG),
      F_TYPE('F', Float.TYPE, Wrapper.FLOAT),
      D_TYPE('D', Double.TYPE, Wrapper.DOUBLE),
      V_TYPE('V', Void.TYPE, Wrapper.VOID);

      static final LambdaForm.BasicType[] ALL_TYPES = values();
      static final LambdaForm.BasicType[] ARG_TYPES = (LambdaForm.BasicType[])Arrays.copyOf((Object[])ALL_TYPES, ALL_TYPES.length - 1);
      static final int ARG_TYPE_LIMIT = ARG_TYPES.length;
      static final int TYPE_LIMIT = ALL_TYPES.length;
      private final char btChar;
      private final Class<?> btClass;
      private final Wrapper btWrapper;

      private BasicType(char var3, Class<?> var4, Wrapper var5) {
         this.btChar = var3;
         this.btClass = var4;
         this.btWrapper = var5;
      }

      char basicTypeChar() {
         return this.btChar;
      }

      Class<?> basicTypeClass() {
         return this.btClass;
      }

      Wrapper basicTypeWrapper() {
         return this.btWrapper;
      }

      int basicTypeSlots() {
         return this.btWrapper.stackSlots();
      }

      static LambdaForm.BasicType basicType(byte var0) {
         return ALL_TYPES[var0];
      }

      static LambdaForm.BasicType basicType(char var0) {
         switch(var0) {
         case 'B':
         case 'C':
         case 'S':
         case 'Z':
            return I_TYPE;
         case 'D':
            return D_TYPE;
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'T':
         case 'U':
         case 'W':
         case 'X':
         case 'Y':
         default:
            throw MethodHandleStatics.newInternalError("Unknown type char: '" + var0 + "'");
         case 'F':
            return F_TYPE;
         case 'I':
            return I_TYPE;
         case 'J':
            return J_TYPE;
         case 'L':
            return L_TYPE;
         case 'V':
            return V_TYPE;
         }
      }

      static LambdaForm.BasicType basicType(Wrapper var0) {
         char var1 = var0.basicTypeChar();
         return basicType(var1);
      }

      static LambdaForm.BasicType basicType(Class<?> var0) {
         return !var0.isPrimitive() ? L_TYPE : basicType(Wrapper.forPrimitiveType(var0));
      }

      static char basicTypeChar(Class<?> var0) {
         return basicType(var0).btChar;
      }

      static LambdaForm.BasicType[] basicTypes(List<Class<?>> var0) {
         LambdaForm.BasicType[] var1 = new LambdaForm.BasicType[var0.size()];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            var1[var2] = basicType((Class)var0.get(var2));
         }

         return var1;
      }

      static LambdaForm.BasicType[] basicTypes(String var0) {
         LambdaForm.BasicType[] var1 = new LambdaForm.BasicType[var0.length()];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            var1[var2] = basicType(var0.charAt(var2));
         }

         return var1;
      }

      static byte[] basicTypesOrd(LambdaForm.BasicType[] var0) {
         byte[] var1 = new byte[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = (byte)var0[var2].ordinal();
         }

         return var1;
      }

      static boolean isBasicTypeChar(char var0) {
         return "LIJFDV".indexOf(var0) >= 0;
      }

      static boolean isArgBasicTypeChar(char var0) {
         return "LIJFD".indexOf(var0) >= 0;
      }

      private static boolean checkBasicType() {
         int var0;
         for(var0 = 0; var0 < ARG_TYPE_LIMIT; ++var0) {
            assert ARG_TYPES[var0].ordinal() == var0;

            assert ARG_TYPES[var0] == ALL_TYPES[var0];
         }

         for(var0 = 0; var0 < TYPE_LIMIT; ++var0) {
            assert ALL_TYPES[var0].ordinal() == var0;
         }

         assert ALL_TYPES[TYPE_LIMIT - 1] == V_TYPE;

         assert !Arrays.asList(ARG_TYPES).contains(V_TYPE);

         return true;
      }

      static {
         assert checkBasicType();

      }
   }
}
