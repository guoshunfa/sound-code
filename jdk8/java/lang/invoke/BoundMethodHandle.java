package java.lang.invoke;

import java.lang.reflect.Field;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import sun.invoke.util.ValueConversions;
import sun.invoke.util.Wrapper;

abstract class BoundMethodHandle extends MethodHandle {
   private static final int FIELD_COUNT_THRESHOLD = 12;
   private static final int FORM_EXPRESSION_THRESHOLD = 24;
   private static final MethodHandles.Lookup LOOKUP;
   static final BoundMethodHandle.SpeciesData SPECIES_DATA;
   private static final BoundMethodHandle.SpeciesData[] SPECIES_DATA_CACHE;

   BoundMethodHandle(MethodType var1, LambdaForm var2) {
      super(var1, var2);

      assert this.speciesData() == speciesData(var2);

   }

   static BoundMethodHandle bindSingle(MethodType var0, LambdaForm var1, LambdaForm.BasicType var2, Object var3) {
      try {
         switch(var2) {
         case L_TYPE:
            return bindSingle(var0, var1, var3);
         case I_TYPE:
            return BoundMethodHandle.SpeciesData.EMPTY.extendWith(LambdaForm.BasicType.I_TYPE).constructor().invokeBasic(var0, var1, ValueConversions.widenSubword(var3));
         case J_TYPE:
            return BoundMethodHandle.SpeciesData.EMPTY.extendWith(LambdaForm.BasicType.J_TYPE).constructor().invokeBasic(var0, var1, (Long)var3);
         case F_TYPE:
            return BoundMethodHandle.SpeciesData.EMPTY.extendWith(LambdaForm.BasicType.F_TYPE).constructor().invokeBasic(var0, var1, (Float)var3);
         case D_TYPE:
            return BoundMethodHandle.SpeciesData.EMPTY.extendWith(LambdaForm.BasicType.D_TYPE).constructor().invokeBasic(var0, var1, (Double)var3);
         default:
            throw MethodHandleStatics.newInternalError("unexpected xtype: " + var2);
         }
      } catch (Throwable var5) {
         throw MethodHandleStatics.newInternalError(var5);
      }
   }

   LambdaFormEditor editor() {
      return this.form.editor();
   }

   static BoundMethodHandle bindSingle(MethodType var0, LambdaForm var1, Object var2) {
      return BoundMethodHandle.Species_L.make(var0, var1, var2);
   }

   BoundMethodHandle bindArgumentL(int var1, Object var2) {
      return this.editor().bindArgumentL(this, var1, var2);
   }

   BoundMethodHandle bindArgumentI(int var1, int var2) {
      return this.editor().bindArgumentI(this, var1, var2);
   }

   BoundMethodHandle bindArgumentJ(int var1, long var2) {
      return this.editor().bindArgumentJ(this, var1, var2);
   }

   BoundMethodHandle bindArgumentF(int var1, float var2) {
      return this.editor().bindArgumentF(this, var1, var2);
   }

   BoundMethodHandle bindArgumentD(int var1, double var2) {
      return this.editor().bindArgumentD(this, var1, var2);
   }

   BoundMethodHandle rebind() {
      return !this.tooComplex() ? this : makeReinvoker(this);
   }

   private boolean tooComplex() {
      return this.fieldCount() > 12 || this.form.expressionCount() > 24;
   }

   static BoundMethodHandle makeReinvoker(MethodHandle var0) {
      LambdaForm var1 = DelegatingMethodHandle.makeReinvokerForm(var0, 7, BoundMethodHandle.Species_L.SPECIES_DATA, BoundMethodHandle.Species_L.SPECIES_DATA.getterFunction(0));
      return BoundMethodHandle.Species_L.make(var0.type(), var1, var0);
   }

   abstract BoundMethodHandle.SpeciesData speciesData();

   static BoundMethodHandle.SpeciesData speciesData(LambdaForm var0) {
      Object var1 = var0.names[0].constraint;
      return var1 instanceof BoundMethodHandle.SpeciesData ? (BoundMethodHandle.SpeciesData)var1 : BoundMethodHandle.SpeciesData.EMPTY;
   }

   abstract int fieldCount();

   Object internalProperties() {
      return "\n& BMH=" + this.internalValues();
   }

   final Object internalValues() {
      Object[] var1 = new Object[this.speciesData().fieldCount()];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = this.arg(var2);
      }

      return Arrays.asList(var1);
   }

   final Object arg(int var1) {
      try {
         switch(this.speciesData().fieldType(var1)) {
         case L_TYPE:
            return this.speciesData().getters[var1].invokeBasic(this);
         case I_TYPE:
            return this.speciesData().getters[var1].invokeBasic(this);
         case J_TYPE:
            return this.speciesData().getters[var1].invokeBasic(this);
         case F_TYPE:
            return this.speciesData().getters[var1].invokeBasic(this);
         case D_TYPE:
            return this.speciesData().getters[var1].invokeBasic(this);
         }
      } catch (Throwable var3) {
         throw MethodHandleStatics.newInternalError(var3);
      }

      throw new InternalError("unexpected type: " + this.speciesData().typeChars + "." + var1);
   }

   abstract BoundMethodHandle copyWith(MethodType var1, LambdaForm var2);

   abstract BoundMethodHandle copyWithExtendL(MethodType var1, LambdaForm var2, Object var3);

   abstract BoundMethodHandle copyWithExtendI(MethodType var1, LambdaForm var2, int var3);

   abstract BoundMethodHandle copyWithExtendJ(MethodType var1, LambdaForm var2, long var3);

   abstract BoundMethodHandle copyWithExtendF(MethodType var1, LambdaForm var2, float var3);

   abstract BoundMethodHandle copyWithExtendD(MethodType var1, LambdaForm var2, double var3);

   static BoundMethodHandle.SpeciesData getSpeciesData(String var0) {
      return BoundMethodHandle.SpeciesData.get(var0);
   }

   private static BoundMethodHandle.SpeciesData checkCache(int var0, String var1) {
      int var2 = var0 - 1;
      BoundMethodHandle.SpeciesData var3 = SPECIES_DATA_CACHE[var2];
      if (var3 != null) {
         return var3;
      } else {
         SPECIES_DATA_CACHE[var2] = var3 = getSpeciesData(var1);
         return var3;
      }
   }

   static BoundMethodHandle.SpeciesData speciesData_L() {
      return checkCache(1, "L");
   }

   static BoundMethodHandle.SpeciesData speciesData_LL() {
      return checkCache(2, "LL");
   }

   static BoundMethodHandle.SpeciesData speciesData_LLL() {
      return checkCache(3, "LLL");
   }

   static BoundMethodHandle.SpeciesData speciesData_LLLL() {
      return checkCache(4, "LLLL");
   }

   static BoundMethodHandle.SpeciesData speciesData_LLLLL() {
      return checkCache(5, "LLLLL");
   }

   static {
      LOOKUP = MethodHandles.Lookup.IMPL_LOOKUP;
      SPECIES_DATA = BoundMethodHandle.SpeciesData.EMPTY;
      SPECIES_DATA_CACHE = new BoundMethodHandle.SpeciesData[5];
   }

   static class Factory {
      static final String JLO_SIG = "Ljava/lang/Object;";
      static final String JLS_SIG = "Ljava/lang/String;";
      static final String JLC_SIG = "Ljava/lang/Class;";
      static final String MH = "java/lang/invoke/MethodHandle";
      static final String MH_SIG = "Ljava/lang/invoke/MethodHandle;";
      static final String BMH = "java/lang/invoke/BoundMethodHandle";
      static final String BMH_SIG = "Ljava/lang/invoke/BoundMethodHandle;";
      static final String SPECIES_DATA = "java/lang/invoke/BoundMethodHandle$SpeciesData";
      static final String SPECIES_DATA_SIG = "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;";
      static final String STABLE_SIG = "Ljava/lang/invoke/Stable;";
      static final String SPECIES_PREFIX_NAME = "Species_";
      static final String SPECIES_PREFIX_PATH = "java/lang/invoke/BoundMethodHandle$Species_";
      static final String BMHSPECIES_DATA_EWI_SIG = "(B)Ljava/lang/invoke/BoundMethodHandle$SpeciesData;";
      static final String BMHSPECIES_DATA_GFC_SIG = "(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/BoundMethodHandle$SpeciesData;";
      static final String MYSPECIES_DATA_SIG = "()Ljava/lang/invoke/BoundMethodHandle$SpeciesData;";
      static final String VOID_SIG = "()V";
      static final String INT_SIG = "()I";
      static final String SIG_INCIPIT = "(Ljava/lang/invoke/MethodType;Ljava/lang/invoke/LambdaForm;";
      static final String[] E_THROWABLE = new String[]{"java/lang/Throwable"};
      static final ConcurrentMap<String, Class<? extends BoundMethodHandle>> CLASS_CACHE = new ConcurrentHashMap();

      static Class<? extends BoundMethodHandle> getConcreteBMHClass(String var0) {
         return (Class)CLASS_CACHE.computeIfAbsent(var0, new Function<String, Class<? extends BoundMethodHandle>>() {
            public Class<? extends BoundMethodHandle> apply(String var1) {
               return BoundMethodHandle.Factory.generateConcreteBMHClass(var1);
            }
         });
      }

      static Class<? extends BoundMethodHandle> generateConcreteBMHClass(String var0) {
         ClassWriter var1 = new ClassWriter(3);
         String var2 = LambdaForm.shortenSignature(var0);
         String var3 = "java/lang/invoke/BoundMethodHandle$Species_" + var2;
         String var4 = "Species_" + var2;
         var1.visit(50, 48, var3, (String)null, "java/lang/invoke/BoundMethodHandle", (String[])null);
         var1.visitSource(var4, (String)null);
         FieldVisitor var6 = var1.visitField(8, "SPECIES_DATA", "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;", (String)null, (Object)null);
         var6.visitAnnotation("Ljava/lang/invoke/Stable;", true);
         var6.visitEnd();

         for(int var7 = 0; var7 < var0.length(); ++var7) {
            char var8 = var0.charAt(var7);
            String var9 = makeFieldName(var0, var7);
            String var10 = var8 == 'L' ? "Ljava/lang/Object;" : String.valueOf(var8);
            var1.visitField(16, var9, var10, (String)null, (Object)null).visitEnd();
         }

         MethodVisitor var16 = var1.visitMethod(2, "<init>", makeSignature(var0, true), (String)null, (String[])null);
         var16.visitCode();
         var16.visitVarInsn(25, 0);
         var16.visitVarInsn(25, 1);
         var16.visitVarInsn(25, 2);
         var16.visitMethodInsn(183, "java/lang/invoke/BoundMethodHandle", "<init>", makeSignature("", true), false);
         int var17 = 0;

         int var18;
         for(var18 = 0; var17 < var0.length(); ++var18) {
            char var19 = var0.charAt(var17);
            var16.visitVarInsn(25, 0);
            var16.visitVarInsn(typeLoadOp(var19), var18 + 3);
            var16.visitFieldInsn(181, var3, makeFieldName(var0, var17), typeSig(var19));
            if (var19 == 'J' || var19 == 'D') {
               ++var18;
            }

            ++var17;
         }

         var16.visitInsn(177);
         var16.visitMaxs(0, 0);
         var16.visitEnd();
         var16 = var1.visitMethod(16, "speciesData", "()Ljava/lang/invoke/BoundMethodHandle$SpeciesData;", (String)null, (String[])null);
         var16.visitCode();
         var16.visitFieldInsn(178, var3, "SPECIES_DATA", "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;");
         var16.visitInsn(176);
         var16.visitMaxs(0, 0);
         var16.visitEnd();
         var16 = var1.visitMethod(16, "fieldCount", "()I", (String)null, (String[])null);
         var16.visitCode();
         var17 = var0.length();
         if (var17 <= 5) {
            var16.visitInsn(3 + var17);
         } else {
            var16.visitIntInsn(17, var17);
         }

         var16.visitInsn(172);
         var16.visitMaxs(0, 0);
         var16.visitEnd();
         var16 = var1.visitMethod(8, "make", makeSignature(var0, false), (String)null, (String[])null);
         var16.visitCode();
         var16.visitTypeInsn(187, var3);
         var16.visitInsn(89);
         var16.visitVarInsn(25, 0);
         var16.visitVarInsn(25, 1);
         var18 = 0;

         int var20;
         for(var20 = 0; var18 < var0.length(); ++var20) {
            char var11 = var0.charAt(var18);
            var16.visitVarInsn(typeLoadOp(var11), var20 + 2);
            if (var11 == 'J' || var11 == 'D') {
               ++var20;
            }

            ++var18;
         }

         var16.visitMethodInsn(183, var3, "<init>", makeSignature(var0, true), false);
         var16.visitInsn(176);
         var16.visitMaxs(0, 0);
         var16.visitEnd();
         var16 = var1.visitMethod(16, "copyWith", makeSignature("", false), (String)null, (String[])null);
         var16.visitCode();
         var16.visitTypeInsn(187, var3);
         var16.visitInsn(89);
         var16.visitVarInsn(25, 1);
         var16.visitVarInsn(25, 2);
         emitPushFields(var0, var3, var16);
         var16.visitMethodInsn(183, var3, "<init>", makeSignature(var0, true), false);
         var16.visitInsn(176);
         var16.visitMaxs(0, 0);
         var16.visitEnd();
         LambdaForm.BasicType[] var21 = LambdaForm.BasicType.ARG_TYPES;
         var20 = var21.length;

         for(int var22 = 0; var22 < var20; ++var22) {
            LambdaForm.BasicType var12 = var21[var22];
            int var13 = var12.ordinal();
            char var14 = var12.basicTypeChar();
            var16 = var1.visitMethod(16, "copyWithExtend" + var14, makeSignature(String.valueOf(var14), false), (String)null, E_THROWABLE);
            var16.visitCode();
            var16.visitFieldInsn(178, var3, "SPECIES_DATA", "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;");
            int var15 = 3 + var13;

            assert var15 <= 8;

            var16.visitInsn(var15);
            var16.visitMethodInsn(182, "java/lang/invoke/BoundMethodHandle$SpeciesData", "extendWith", "(B)Ljava/lang/invoke/BoundMethodHandle$SpeciesData;", false);
            var16.visitMethodInsn(182, "java/lang/invoke/BoundMethodHandle$SpeciesData", "constructor", "()Ljava/lang/invoke/MethodHandle;", false);
            var16.visitVarInsn(25, 1);
            var16.visitVarInsn(25, 2);
            emitPushFields(var0, var3, var16);
            var16.visitVarInsn(typeLoadOp(var14), 3);
            var16.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", makeSignature(var0 + var14, false), false);
            var16.visitInsn(176);
            var16.visitMaxs(0, 0);
            var16.visitEnd();
         }

         var1.visitEnd();
         byte[] var23 = var1.toByteArray();
         InvokerBytecodeGenerator.maybeDump(var3, var23);
         Class var24 = MethodHandleStatics.UNSAFE.defineClass(var3, var23, 0, var23.length, BoundMethodHandle.class.getClassLoader(), (ProtectionDomain)null).asSubclass(BoundMethodHandle.class);
         return var24;
      }

      private static int typeLoadOp(char var0) {
         switch(var0) {
         case 'D':
            return 24;
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         default:
            throw MethodHandleStatics.newInternalError("unrecognized type " + var0);
         case 'F':
            return 23;
         case 'I':
            return 21;
         case 'J':
            return 22;
         case 'L':
            return 25;
         }
      }

      private static void emitPushFields(String var0, String var1, MethodVisitor var2) {
         for(int var3 = 0; var3 < var0.length(); ++var3) {
            char var4 = var0.charAt(var3);
            var2.visitVarInsn(25, 0);
            var2.visitFieldInsn(180, var1, makeFieldName(var0, var3), typeSig(var4));
         }

      }

      static String typeSig(char var0) {
         return var0 == 'L' ? "Ljava/lang/Object;" : String.valueOf(var0);
      }

      private static MethodHandle makeGetter(Class<?> var0, String var1, int var2) {
         String var3 = makeFieldName(var1, var2);
         Class var4 = Wrapper.forBasicType(var1.charAt(var2)).primitiveType();

         try {
            return BoundMethodHandle.LOOKUP.findGetter(var0, var3, var4);
         } catch (IllegalAccessException | NoSuchFieldException var6) {
            throw MethodHandleStatics.newInternalError((Throwable)var6);
         }
      }

      static MethodHandle[] makeGetters(Class<?> var0, String var1, MethodHandle[] var2) {
         if (var2 == null) {
            var2 = new MethodHandle[var1.length()];
         }

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3] = makeGetter(var0, var1, var3);

            assert var2[var3].internalMemberName().getDeclaringClass() == var0;
         }

         return var2;
      }

      static MethodHandle[] makeCtors(Class<? extends BoundMethodHandle> var0, String var1, MethodHandle[] var2) {
         if (var2 == null) {
            var2 = new MethodHandle[1];
         }

         if (var1.equals("")) {
            return var2;
         } else {
            var2[0] = makeCbmhCtor(var0, var1);
            return var2;
         }
      }

      static LambdaForm.NamedFunction[] makeNominalGetters(String var0, LambdaForm.NamedFunction[] var1, MethodHandle[] var2) {
         if (var1 == null) {
            var1 = new LambdaForm.NamedFunction[var0.length()];
         }

         for(int var3 = 0; var3 < var1.length; ++var3) {
            var1[var3] = new LambdaForm.NamedFunction(var2[var3]);
         }

         return var1;
      }

      static BoundMethodHandle.SpeciesData getSpeciesDataFromConcreteBMHClass(Class<? extends BoundMethodHandle> var0) {
         try {
            Field var1 = var0.getDeclaredField("SPECIES_DATA");
            return (BoundMethodHandle.SpeciesData)var1.get((Object)null);
         } catch (ReflectiveOperationException var2) {
            throw MethodHandleStatics.newInternalError((Throwable)var2);
         }
      }

      static void setSpeciesDataToConcreteBMHClass(Class<? extends BoundMethodHandle> var0, BoundMethodHandle.SpeciesData var1) {
         try {
            Field var2 = var0.getDeclaredField("SPECIES_DATA");

            assert var2.getDeclaredAnnotation(Stable.class) != null;

            var2.set((Object)null, var1);
         } catch (ReflectiveOperationException var3) {
            throw MethodHandleStatics.newInternalError((Throwable)var3);
         }
      }

      private static String makeFieldName(String var0, int var1) {
         assert var1 >= 0 && var1 < var0.length();

         return "arg" + var0.charAt(var1) + var1;
      }

      private static String makeSignature(String var0, boolean var1) {
         StringBuilder var2 = new StringBuilder("(Ljava/lang/invoke/MethodType;Ljava/lang/invoke/LambdaForm;");
         char[] var3 = var0.toCharArray();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            char var6 = var3[var5];
            var2.append(typeSig(var6));
         }

         return var2.append(')').append(var1 ? "V" : "Ljava/lang/invoke/BoundMethodHandle;").toString();
      }

      static MethodHandle makeCbmhCtor(Class<? extends BoundMethodHandle> var0, String var1) {
         try {
            return BoundMethodHandle.LOOKUP.findStatic(var0, "make", MethodType.fromMethodDescriptorString(makeSignature(var1, false), (ClassLoader)null));
         } catch (IllegalAccessException | IllegalArgumentException | TypeNotPresentException | NoSuchMethodException var3) {
            throw MethodHandleStatics.newInternalError((Throwable)var3);
         }
      }
   }

   static class SpeciesData {
      private final String typeChars;
      private final LambdaForm.BasicType[] typeCodes;
      private final Class<? extends BoundMethodHandle> clazz;
      @Stable
      private final MethodHandle[] constructor;
      @Stable
      private final MethodHandle[] getters;
      @Stable
      private final LambdaForm.NamedFunction[] nominalGetters;
      @Stable
      private final BoundMethodHandle.SpeciesData[] extensions;
      static final BoundMethodHandle.SpeciesData EMPTY = new BoundMethodHandle.SpeciesData("", BoundMethodHandle.class);
      private static final ConcurrentMap<String, BoundMethodHandle.SpeciesData> CACHE = new ConcurrentHashMap();
      private static final boolean INIT_DONE;

      int fieldCount() {
         return this.typeCodes.length;
      }

      LambdaForm.BasicType fieldType(int var1) {
         return this.typeCodes[var1];
      }

      char fieldTypeChar(int var1) {
         return this.typeChars.charAt(var1);
      }

      Object fieldSignature() {
         return this.typeChars;
      }

      public Class<? extends BoundMethodHandle> fieldHolder() {
         return this.clazz;
      }

      public String toString() {
         return "SpeciesData<" + this.fieldSignature() + ">";
      }

      LambdaForm.NamedFunction getterFunction(int var1) {
         LambdaForm.NamedFunction var2 = this.nominalGetters[var1];

         assert var2.memberDeclaringClassOrNull() == this.fieldHolder();

         assert var2.returnType() == this.fieldType(var1);

         return var2;
      }

      LambdaForm.NamedFunction[] getterFunctions() {
         return this.nominalGetters;
      }

      MethodHandle[] getterHandles() {
         return this.getters;
      }

      MethodHandle constructor() {
         return this.constructor[0];
      }

      SpeciesData(String var1, Class<? extends BoundMethodHandle> var2) {
         this.typeChars = var1;
         this.typeCodes = LambdaForm.BasicType.basicTypes(var1);
         this.clazz = var2;
         if (!INIT_DONE) {
            this.constructor = new MethodHandle[1];
            this.getters = new MethodHandle[var1.length()];
            this.nominalGetters = new LambdaForm.NamedFunction[var1.length()];
         } else {
            this.constructor = BoundMethodHandle.Factory.makeCtors(var2, var1, (MethodHandle[])null);
            this.getters = BoundMethodHandle.Factory.makeGetters(var2, var1, (MethodHandle[])null);
            this.nominalGetters = BoundMethodHandle.Factory.makeNominalGetters(var1, (LambdaForm.NamedFunction[])null, this.getters);
         }

         this.extensions = new BoundMethodHandle.SpeciesData[LambdaForm.BasicType.ARG_TYPE_LIMIT];
      }

      private void initForBootstrap() {
         assert !INIT_DONE;

         if (this.constructor() == null) {
            String var1 = this.typeChars;
            CACHE.put(var1, this);
            BoundMethodHandle.Factory.makeCtors(this.clazz, var1, this.constructor);
            BoundMethodHandle.Factory.makeGetters(this.clazz, var1, this.getters);
            BoundMethodHandle.Factory.makeNominalGetters(var1, this.nominalGetters, this.getters);
         }

      }

      BoundMethodHandle.SpeciesData extendWith(byte var1) {
         return this.extendWith(LambdaForm.BasicType.basicType(var1));
      }

      BoundMethodHandle.SpeciesData extendWith(LambdaForm.BasicType var1) {
         int var2 = var1.ordinal();
         BoundMethodHandle.SpeciesData var3 = this.extensions[var2];
         if (var3 != null) {
            return var3;
         } else {
            this.extensions[var2] = var3 = get(this.typeChars + var1.basicTypeChar());
            return var3;
         }
      }

      private static BoundMethodHandle.SpeciesData get(String var0) {
         return (BoundMethodHandle.SpeciesData)CACHE.computeIfAbsent(var0, new Function<String, BoundMethodHandle.SpeciesData>() {
            public BoundMethodHandle.SpeciesData apply(String var1) {
               Class var2 = BoundMethodHandle.Factory.getConcreteBMHClass(var1);
               BoundMethodHandle.SpeciesData var3 = new BoundMethodHandle.SpeciesData(var1, var2);
               BoundMethodHandle.Factory.setSpeciesDataToConcreteBMHClass(var2, var3);
               return var3;
            }
         });
      }

      static boolean speciesDataCachePopulated() {
         Class var0 = BoundMethodHandle.class;

         try {
            Class[] var1 = var0.getDeclaredClasses();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               Class var4 = var1[var3];
               if (var0.isAssignableFrom(var4)) {
                  Class var5 = var4.asSubclass(BoundMethodHandle.class);
                  BoundMethodHandle.SpeciesData var6 = BoundMethodHandle.Factory.getSpeciesDataFromConcreteBMHClass(var5);

                  assert var6 != null : var5.getName();

                  assert var6.clazz == var5;

                  assert CACHE.get(var6.typeChars) == var6;
               }
            }

            return true;
         } catch (Throwable var7) {
            throw MethodHandleStatics.newInternalError(var7);
         }
      }

      static {
         EMPTY.initForBootstrap();
         BoundMethodHandle.Species_L.SPECIES_DATA.initForBootstrap();

         assert speciesDataCachePopulated();

         INIT_DONE = Boolean.TRUE;
      }
   }

   private static final class Species_L extends BoundMethodHandle {
      final Object argL0;
      static final BoundMethodHandle.SpeciesData SPECIES_DATA = new BoundMethodHandle.SpeciesData("L", BoundMethodHandle.Species_L.class);

      private Species_L(MethodType var1, LambdaForm var2, Object var3) {
         super(var1, var2);
         this.argL0 = var3;
      }

      BoundMethodHandle.SpeciesData speciesData() {
         return SPECIES_DATA;
      }

      int fieldCount() {
         return 1;
      }

      static BoundMethodHandle make(MethodType var0, LambdaForm var1, Object var2) {
         return new BoundMethodHandle.Species_L(var0, var1, var2);
      }

      final BoundMethodHandle copyWith(MethodType var1, LambdaForm var2) {
         return new BoundMethodHandle.Species_L(var1, var2, this.argL0);
      }

      final BoundMethodHandle copyWithExtendL(MethodType var1, LambdaForm var2, Object var3) {
         try {
            return SPECIES_DATA.extendWith(LambdaForm.BasicType.L_TYPE).constructor().invokeBasic(var1, var2, this.argL0, var3);
         } catch (Throwable var5) {
            throw MethodHandleStatics.uncaughtException(var5);
         }
      }

      final BoundMethodHandle copyWithExtendI(MethodType var1, LambdaForm var2, int var3) {
         try {
            return SPECIES_DATA.extendWith(LambdaForm.BasicType.I_TYPE).constructor().invokeBasic(var1, var2, this.argL0, var3);
         } catch (Throwable var5) {
            throw MethodHandleStatics.uncaughtException(var5);
         }
      }

      final BoundMethodHandle copyWithExtendJ(MethodType var1, LambdaForm var2, long var3) {
         try {
            return SPECIES_DATA.extendWith(LambdaForm.BasicType.J_TYPE).constructor().invokeBasic(var1, var2, this.argL0, var3);
         } catch (Throwable var6) {
            throw MethodHandleStatics.uncaughtException(var6);
         }
      }

      final BoundMethodHandle copyWithExtendF(MethodType var1, LambdaForm var2, float var3) {
         try {
            return SPECIES_DATA.extendWith(LambdaForm.BasicType.F_TYPE).constructor().invokeBasic(var1, var2, this.argL0, var3);
         } catch (Throwable var5) {
            throw MethodHandleStatics.uncaughtException(var5);
         }
      }

      final BoundMethodHandle copyWithExtendD(MethodType var1, LambdaForm var2, double var3) {
         try {
            return SPECIES_DATA.extendWith(LambdaForm.BasicType.D_TYPE).constructor().invokeBasic(var1, var2, this.argL0, var3);
         } catch (Throwable var6) {
            throw MethodHandleStatics.uncaughtException(var6);
         }
      }
   }
}
