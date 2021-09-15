package java.lang.invoke;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import sun.invoke.util.VerifyAccess;
import sun.invoke.util.VerifyType;
import sun.invoke.util.Wrapper;
import sun.misc.Unsafe;
import sun.reflect.misc.ReflectUtil;

class InvokerBytecodeGenerator {
   private static final String MH = "java/lang/invoke/MethodHandle";
   private static final String MHI = "java/lang/invoke/MethodHandleImpl";
   private static final String LF = "java/lang/invoke/LambdaForm";
   private static final String LFN = "java/lang/invoke/LambdaForm$Name";
   private static final String CLS = "java/lang/Class";
   private static final String OBJ = "java/lang/Object";
   private static final String OBJARY = "[Ljava/lang/Object;";
   private static final String MH_SIG = "Ljava/lang/invoke/MethodHandle;";
   private static final String LF_SIG = "Ljava/lang/invoke/LambdaForm;";
   private static final String LFN_SIG = "Ljava/lang/invoke/LambdaForm$Name;";
   private static final String LL_SIG = "(Ljava/lang/Object;)Ljava/lang/Object;";
   private static final String LLV_SIG = "(Ljava/lang/Object;Ljava/lang/Object;)V";
   private static final String CLL_SIG = "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;";
   private static final String superName = "java/lang/Object";
   private final String className;
   private final String sourceFile;
   private final LambdaForm lambdaForm;
   private final String invokerName;
   private final MethodType invokerType;
   private final int[] localsMap;
   private final LambdaForm.BasicType[] localTypes;
   private final Class<?>[] localClasses;
   private ClassWriter cw;
   private MethodVisitor mv;
   private static final MemberName.Factory MEMBERNAME_FACTORY = MemberName.getFactory();
   private static final Class<?> HOST_CLASS = LambdaForm.class;
   private static final HashMap<String, Integer> DUMP_CLASS_FILES_COUNTERS;
   private static final File DUMP_CLASS_FILES_DIR;
   Map<Object, InvokerBytecodeGenerator.CpPatch> cpPatches;
   int cph;
   private static Class<?>[] STATICALLY_INVOCABLE_PACKAGES;

   private InvokerBytecodeGenerator(LambdaForm var1, int var2, String var3, String var4, MethodType var5) {
      this.cpPatches = new HashMap();
      this.cph = 0;
      if (var4.contains(".")) {
         int var6 = var4.indexOf(".");
         var3 = var4.substring(0, var6);
         var4 = var4.substring(var6 + 1);
      }

      if (MethodHandleStatics.DUMP_CLASS_FILES) {
         var3 = makeDumpableClassName(var3);
      }

      this.className = "java/lang/invoke/LambdaForm$" + var3;
      this.sourceFile = "LambdaForm$" + var3;
      this.lambdaForm = var1;
      this.invokerName = var4;
      this.invokerType = var5;
      this.localsMap = new int[var2 + 1];
      this.localTypes = new LambdaForm.BasicType[var2 + 1];
      this.localClasses = new Class[var2 + 1];
   }

   private InvokerBytecodeGenerator(String var1, String var2, MethodType var3) {
      this((LambdaForm)null, var3.parameterCount(), var1, var2, var3);
      this.localTypes[this.localTypes.length - 1] = LambdaForm.BasicType.V_TYPE;

      for(int var4 = 0; var4 < this.localsMap.length; ++var4) {
         this.localsMap[var4] = var3.parameterSlotCount() - var3.parameterSlotDepth(var4);
         if (var4 < var3.parameterCount()) {
            this.localTypes[var4] = LambdaForm.BasicType.basicType(var3.parameterType(var4));
         }
      }

   }

   private InvokerBytecodeGenerator(String var1, LambdaForm var2, MethodType var3) {
      this(var2, var2.names.length, var1, var2.debugName, var3);
      LambdaForm.Name[] var4 = var2.names;
      int var5 = 0;

      for(int var6 = 0; var5 < this.localsMap.length; ++var5) {
         this.localsMap[var5] = var6;
         if (var5 < var4.length) {
            LambdaForm.BasicType var7 = var4[var5].type();
            var6 += var7.basicTypeSlots();
            this.localTypes[var5] = var7;
         }
      }

   }

   static void maybeDump(final String var0, final byte[] var1) {
      if (MethodHandleStatics.DUMP_CLASS_FILES) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               try {
                  String var1x = var0;
                  File var2 = new File(InvokerBytecodeGenerator.DUMP_CLASS_FILES_DIR, var1x + ".class");
                  System.out.println("dump: " + var2);
                  var2.getParentFile().mkdirs();
                  FileOutputStream var3 = new FileOutputStream(var2);
                  var3.write(var1);
                  var3.close();
                  return null;
               } catch (IOException var4) {
                  throw MethodHandleStatics.newInternalError((Throwable)var4);
               }
            }
         });
      }

   }

   private static String makeDumpableClassName(String var0) {
      Integer var1;
      synchronized(DUMP_CLASS_FILES_COUNTERS) {
         var1 = (Integer)DUMP_CLASS_FILES_COUNTERS.get(var0);
         if (var1 == null) {
            var1 = 0;
         }

         DUMP_CLASS_FILES_COUNTERS.put(var0, var1 + 1);
      }

      String var2;
      for(var2 = var1.toString(); var2.length() < 3; var2 = "0" + var2) {
      }

      var0 = var0 + var2;
      return var0;
   }

   String constantPlaceholder(Object var1) {
      String var2 = "CONSTANT_PLACEHOLDER_" + this.cph++;
      if (MethodHandleStatics.DUMP_CLASS_FILES) {
         var2 = var2 + " <<" + debugString(var1) + ">>";
      }

      if (this.cpPatches.containsKey(var2)) {
         throw new InternalError("observed CP placeholder twice: " + var2);
      } else {
         int var3 = this.cw.newConst(var2);
         this.cpPatches.put(var2, new InvokerBytecodeGenerator.CpPatch(var3, var2, var1));
         return var2;
      }
   }

   Object[] cpPatches(byte[] var1) {
      int var2 = getConstantPoolSize(var1);
      Object[] var3 = new Object[var2];

      InvokerBytecodeGenerator.CpPatch var5;
      for(Iterator var4 = this.cpPatches.values().iterator(); var4.hasNext(); var3[var5.index] = var5.value) {
         var5 = (InvokerBytecodeGenerator.CpPatch)var4.next();
         if (var5.index >= var2) {
            throw new InternalError("in cpool[" + var2 + "]: " + var5 + "\n" + Arrays.toString(Arrays.copyOf((byte[])var1, 20)));
         }
      }

      return var3;
   }

   private static String debugString(Object var0) {
      if (var0 instanceof MethodHandle) {
         MethodHandle var1 = (MethodHandle)var0;
         MemberName var2 = var1.internalMemberName();
         return var2 != null ? var2.toString() : var1.debugString();
      } else {
         return var0.toString();
      }
   }

   private static int getConstantPoolSize(byte[] var0) {
      return (var0[8] & 255) << 8 | var0[9] & 255;
   }

   private MemberName loadMethod(byte[] var1) {
      Class var2 = loadAndInitializeInvokerClass(var1, this.cpPatches(var1));
      return resolveInvokerMember(var2, this.invokerName, this.invokerType);
   }

   private static Class<?> loadAndInitializeInvokerClass(byte[] var0, Object[] var1) {
      Class var2 = MethodHandleStatics.UNSAFE.defineAnonymousClass(HOST_CLASS, var0, var1);
      MethodHandleStatics.UNSAFE.ensureClassInitialized(var2);
      return var2;
   }

   private static MemberName resolveInvokerMember(Class<?> var0, String var1, MethodType var2) {
      MemberName var3 = new MemberName(var0, var1, var2, (byte)6);

      try {
         var3 = MEMBERNAME_FACTORY.resolveOrFail((byte)6, var3, HOST_CLASS, ReflectiveOperationException.class);
         return var3;
      } catch (ReflectiveOperationException var5) {
         throw MethodHandleStatics.newInternalError((Throwable)var5);
      }
   }

   private void classFilePrologue() {
      this.cw = new ClassWriter(3);
      this.cw.visit(52, 48, this.className, (String)null, "java/lang/Object", (String[])null);
      this.cw.visitSource(this.sourceFile, (String)null);
      String var2 = this.invokerType.toMethodDescriptorString();
      this.mv = this.cw.visitMethod(8, this.invokerName, var2, (String)null, (String[])null);
   }

   private void classFileEpilogue() {
      this.mv.visitMaxs(0, 0);
      this.mv.visitEnd();
   }

   private void emitConst(Object var1) {
      if (var1 == null) {
         this.mv.visitInsn(1);
      } else if (var1 instanceof Integer) {
         this.emitIconstInsn((Integer)var1);
      } else {
         if (var1 instanceof Long) {
            long var2 = (Long)var1;
            if (var2 == (long)((short)((int)var2))) {
               this.emitIconstInsn((int)var2);
               this.mv.visitInsn(133);
               return;
            }
         }

         if (var1 instanceof Float) {
            float var4 = (Float)var1;
            if (var4 == (float)((short)((int)var4))) {
               this.emitIconstInsn((int)var4);
               this.mv.visitInsn(134);
               return;
            }
         }

         if (var1 instanceof Double) {
            double var5 = (Double)var1;
            if (var5 == (double)((short)((int)var5))) {
               this.emitIconstInsn((int)var5);
               this.mv.visitInsn(135);
               return;
            }
         }

         if (var1 instanceof Boolean) {
            this.emitIconstInsn((Boolean)var1 ? 1 : 0);
         } else {
            this.mv.visitLdcInsn(var1);
         }
      }
   }

   private void emitIconstInsn(int var1) {
      byte var2;
      switch(var1) {
      case 0:
         var2 = 3;
         break;
      case 1:
         var2 = 4;
         break;
      case 2:
         var2 = 5;
         break;
      case 3:
         var2 = 6;
         break;
      case 4:
         var2 = 7;
         break;
      case 5:
         var2 = 8;
         break;
      default:
         if (var1 == (byte)var1) {
            this.mv.visitIntInsn(16, var1 & 255);
         } else if (var1 == (short)var1) {
            this.mv.visitIntInsn(17, (char)var1);
         } else {
            this.mv.visitLdcInsn(var1);
         }

         return;
      }

      this.mv.visitInsn(var2);
   }

   private void emitLoadInsn(LambdaForm.BasicType var1, int var2) {
      int var3 = this.loadInsnOpcode(var1);
      this.mv.visitVarInsn(var3, this.localsMap[var2]);
   }

   private int loadInsnOpcode(LambdaForm.BasicType var1) throws InternalError {
      switch(var1) {
      case I_TYPE:
         return 21;
      case J_TYPE:
         return 22;
      case F_TYPE:
         return 23;
      case D_TYPE:
         return 24;
      case L_TYPE:
         return 25;
      default:
         throw new InternalError("unknown type: " + var1);
      }
   }

   private void emitAloadInsn(int var1) {
      this.emitLoadInsn(LambdaForm.BasicType.L_TYPE, var1);
   }

   private void emitStoreInsn(LambdaForm.BasicType var1, int var2) {
      int var3 = this.storeInsnOpcode(var1);
      this.mv.visitVarInsn(var3, this.localsMap[var2]);
   }

   private int storeInsnOpcode(LambdaForm.BasicType var1) throws InternalError {
      switch(var1) {
      case I_TYPE:
         return 54;
      case J_TYPE:
         return 55;
      case F_TYPE:
         return 56;
      case D_TYPE:
         return 57;
      case L_TYPE:
         return 58;
      default:
         throw new InternalError("unknown type: " + var1);
      }
   }

   private void emitAstoreInsn(int var1) {
      this.emitStoreInsn(LambdaForm.BasicType.L_TYPE, var1);
   }

   private byte arrayTypeCode(Wrapper var1) {
      switch(var1) {
      case BOOLEAN:
         return 4;
      case BYTE:
         return 8;
      case CHAR:
         return 5;
      case SHORT:
         return 9;
      case INT:
         return 10;
      case LONG:
         return 11;
      case FLOAT:
         return 6;
      case DOUBLE:
         return 7;
      case OBJECT:
         return 0;
      default:
         throw new InternalError();
      }
   }

   private int arrayInsnOpcode(byte var1, int var2) throws InternalError {
      assert var2 == 83 || var2 == 50;

      byte var3;
      switch(var1) {
      case 0:
         var3 = 83;
         break;
      case 1:
      case 2:
      case 3:
      default:
         throw new InternalError();
      case 4:
         var3 = 84;
         break;
      case 5:
         var3 = 85;
         break;
      case 6:
         var3 = 81;
         break;
      case 7:
         var3 = 82;
         break;
      case 8:
         var3 = 84;
         break;
      case 9:
         var3 = 86;
         break;
      case 10:
         var3 = 79;
         break;
      case 11:
         var3 = 80;
      }

      return var3 - 83 + var2;
   }

   private void freeFrameLocal(int var1) {
      int var2 = this.indexForFrameLocal(var1);
      if (var2 >= 0) {
         LambdaForm.BasicType var3 = this.localTypes[var2];
         int var4 = this.makeLocalTemp(var3);
         this.mv.visitVarInsn(this.loadInsnOpcode(var3), var1);
         this.mv.visitVarInsn(this.storeInsnOpcode(var3), var4);

         assert this.localsMap[var2] == var1;

         this.localsMap[var2] = var4;

         assert this.indexForFrameLocal(var1) < 0;

      }
   }

   private int indexForFrameLocal(int var1) {
      for(int var2 = 0; var2 < this.localsMap.length; ++var2) {
         if (this.localsMap[var2] == var1 && this.localTypes[var2] != LambdaForm.BasicType.V_TYPE) {
            return var2;
         }
      }

      return -1;
   }

   private int makeLocalTemp(LambdaForm.BasicType var1) {
      int var2 = this.localsMap[this.localsMap.length - 1];
      this.localsMap[this.localsMap.length - 1] = var2 + var1.basicTypeSlots();
      return var2;
   }

   private void emitBoxing(Wrapper var1) {
      String var2 = "java/lang/" + var1.wrapperType().getSimpleName();
      String var3 = "valueOf";
      String var4 = "(" + var1.basicTypeChar() + ")L" + var2 + ";";
      this.mv.visitMethodInsn(184, var2, var3, var4, false);
   }

   private void emitUnboxing(Wrapper var1) {
      String var2 = "java/lang/" + var1.wrapperType().getSimpleName();
      String var3 = var1.primitiveSimpleName() + "Value";
      String var4 = "()" + var1.basicTypeChar();
      this.emitReferenceCast(var1.wrapperType(), (Object)null);
      this.mv.visitMethodInsn(182, var2, var3, var4, false);
   }

   private void emitImplicitConversion(LambdaForm.BasicType var1, Class<?> var2, Object var3) {
      assert LambdaForm.BasicType.basicType(var2) == var1;

      if (var2 != var1.basicTypeClass() || var1 == LambdaForm.BasicType.L_TYPE) {
         switch(var1) {
         case I_TYPE:
            if (!VerifyType.isNullConversion(Integer.TYPE, var2, false)) {
               this.emitPrimCast(var1.basicTypeWrapper(), Wrapper.forPrimitiveType(var2));
            }

            return;
         case L_TYPE:
            if (VerifyType.isNullConversion(Object.class, var2, false)) {
               if (MethodHandleStatics.PROFILE_LEVEL > 0) {
                  this.emitReferenceCast(Object.class, var3);
               }

               return;
            }

            this.emitReferenceCast(var2, var3);
            return;
         default:
            throw MethodHandleStatics.newInternalError("bad implicit conversion: tc=" + var1 + ": " + var2);
         }
      }
   }

   private boolean assertStaticType(Class<?> var1, LambdaForm.Name var2) {
      int var3 = var2.index();
      Class var4 = this.localClasses[var3];
      if (var4 != null && (var4 == var1 || var1.isAssignableFrom(var4))) {
         return true;
      } else {
         if (var4 == null || var4.isAssignableFrom(var1)) {
            this.localClasses[var3] = var1;
         }

         return false;
      }
   }

   private void emitReferenceCast(Class<?> var1, Object var2) {
      LambdaForm.Name var3 = null;
      if (var2 instanceof LambdaForm.Name) {
         LambdaForm.Name var4 = (LambdaForm.Name)var2;
         if (this.assertStaticType(var1, var4)) {
            return;
         }

         if (this.lambdaForm.useCount(var4) > 1) {
            var3 = var4;
         }
      }

      if (isStaticallyNameable(var1)) {
         String var5 = getInternalName(var1);
         this.mv.visitTypeInsn(192, var5);
      } else {
         this.mv.visitLdcInsn(this.constantPlaceholder(var1));
         this.mv.visitTypeInsn(192, "java/lang/Class");
         this.mv.visitInsn(95);
         this.mv.visitMethodInsn(184, "java/lang/invoke/MethodHandleImpl", "castReference", "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;", false);
         if (Object[].class.isAssignableFrom(var1)) {
            this.mv.visitTypeInsn(192, "[Ljava/lang/Object;");
         } else if (MethodHandleStatics.PROFILE_LEVEL > 0) {
            this.mv.visitTypeInsn(192, "java/lang/Object");
         }
      }

      if (var3 != null) {
         this.mv.visitInsn(89);
         this.emitAstoreInsn(var3.index());
      }

   }

   private void emitReturnInsn(LambdaForm.BasicType var1) {
      short var2;
      switch(var1) {
      case I_TYPE:
         var2 = 172;
         break;
      case J_TYPE:
         var2 = 173;
         break;
      case F_TYPE:
         var2 = 174;
         break;
      case D_TYPE:
         var2 = 175;
         break;
      case L_TYPE:
         var2 = 176;
         break;
      case V_TYPE:
         var2 = 177;
         break;
      default:
         throw new InternalError("unknown return type: " + var1);
      }

      this.mv.visitInsn(var2);
   }

   private static String getInternalName(Class<?> var0) {
      if (var0 == Object.class) {
         return "java/lang/Object";
      } else if (var0 == Object[].class) {
         return "[Ljava/lang/Object;";
      } else if (var0 == Class.class) {
         return "java/lang/Class";
      } else if (var0 == MethodHandle.class) {
         return "java/lang/invoke/MethodHandle";
      } else {
         assert VerifyAccess.isTypeVisible(var0, Object.class) : var0.getName();

         return var0.getName().replace('.', '/');
      }
   }

   static MemberName generateCustomizedCode(LambdaForm var0, MethodType var1) {
      InvokerBytecodeGenerator var2 = new InvokerBytecodeGenerator("MH", var0, var1);
      return var2.loadMethod(var2.generateCustomizedCodeBytes());
   }

   private boolean checkActualReceiver() {
      this.mv.visitInsn(89);
      this.mv.visitVarInsn(25, this.localsMap[0]);
      this.mv.visitMethodInsn(184, "java/lang/invoke/MethodHandleImpl", "assertSame", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
      return true;
   }

   private byte[] generateCustomizedCodeBytes() {
      this.classFilePrologue();
      this.mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
      this.mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Compiled;", true);
      if (this.lambdaForm.forceInline) {
         this.mv.visitAnnotation("Ljava/lang/invoke/ForceInline;", true);
      } else {
         this.mv.visitAnnotation("Ljava/lang/invoke/DontInline;", true);
      }

      if (this.lambdaForm.customized != null) {
         this.mv.visitLdcInsn(this.constantPlaceholder(this.lambdaForm.customized));
         this.mv.visitTypeInsn(192, "java/lang/invoke/MethodHandle");

         assert this.checkActualReceiver();

         this.mv.visitVarInsn(58, this.localsMap[0]);
      }

      LambdaForm.Name var1 = null;

      for(int var2 = this.lambdaForm.arity; var2 < this.lambdaForm.names.length; ++var2) {
         LambdaForm.Name var3 = this.lambdaForm.names[var2];
         this.emitStoreResult(var1);
         var1 = var3;
         MethodHandleImpl.Intrinsic var4 = var3.function.intrinsicName();
         switch(var4) {
         case SELECT_ALTERNATIVE:
            assert this.isSelectAlternative(var2);

            if (MethodHandleStatics.PROFILE_GWT) {
               assert var3.arguments[0] instanceof LambdaForm.Name && this.nameRefersTo((LambdaForm.Name)var3.arguments[0], MethodHandleImpl.class, "profileBoolean");

               this.mv.visitAnnotation("Ljava/lang/invoke/InjectedProfile;", true);
            }

            var1 = this.emitSelectAlternative(var3, this.lambdaForm.names[var2 + 1]);
            ++var2;
            break;
         case GUARD_WITH_CATCH:
            assert this.isGuardWithCatch(var2);

            var1 = this.emitGuardWithCatch(var2);
            var2 += 2;
            break;
         case NEW_ARRAY:
            Class var5 = var3.function.methodType().returnType();
            if (isStaticallyNameable(var5)) {
               this.emitNewArray(var3);
               break;
            }
         case NONE:
            MemberName var7 = var3.function.member();
            if (isStaticallyInvocable(var7)) {
               this.emitStaticInvoke(var7, var3);
            } else {
               this.emitInvoke(var3);
            }
            break;
         case ARRAY_LOAD:
            this.emitArrayLoad(var3);
            break;
         case ARRAY_STORE:
            this.emitArrayStore(var3);
            break;
         case IDENTITY:
            assert var3.arguments.length == 1;

            this.emitPushArguments(var3);
            break;
         case ZERO:
            assert var3.arguments.length == 0;

            this.emitConst(var3.type.basicTypeWrapper().zero());
            break;
         default:
            throw MethodHandleStatics.newInternalError("Unknown intrinsic: " + var4);
         }
      }

      this.emitReturn(var1);
      this.classFileEpilogue();
      this.bogusMethod(this.lambdaForm);
      byte[] var6 = this.cw.toByteArray();
      maybeDump(this.className, var6);
      return var6;
   }

   void emitArrayLoad(LambdaForm.Name var1) {
      this.emitArrayOp(var1, 50);
   }

   void emitArrayStore(LambdaForm.Name var1) {
      this.emitArrayOp(var1, 83);
   }

   void emitArrayOp(LambdaForm.Name var1, int var2) {
      assert var2 == 50 || var2 == 83;

      Class var3 = var1.function.methodType().parameterType(0).getComponentType();

      assert var3 != null;

      this.emitPushArguments(var1);
      if (var3.isPrimitive()) {
         Wrapper var4 = Wrapper.forPrimitiveType(var3);
         var2 = this.arrayInsnOpcode(this.arrayTypeCode(var4), var2);
      }

      this.mv.visitInsn(var2);
   }

   void emitInvoke(LambdaForm.Name var1) {
      assert !this.isLinkerMethodInvoke(var1);

      MethodHandle var2 = var1.function.resolvedHandle;

      assert var2 != null : var1.exprString();

      this.mv.visitLdcInsn(this.constantPlaceholder(var2));
      this.emitReferenceCast(MethodHandle.class, var2);
      this.emitPushArguments(var1);
      MethodType var3 = var1.function.methodType();
      this.mv.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", var3.basicType().toMethodDescriptorString(), false);
   }

   static boolean isStaticallyInvocable(LambdaForm.Name var0) {
      return isStaticallyInvocable(var0.function.member());
   }

   static boolean isStaticallyInvocable(MemberName var0) {
      if (var0 == null) {
         return false;
      } else if (var0.isConstructor()) {
         return false;
      } else {
         Class var1 = var0.getDeclaringClass();
         if (!var1.isArray() && !var1.isPrimitive()) {
            if (!var1.isAnonymousClass() && !var1.isLocalClass()) {
               if (var1.getClassLoader() != MethodHandle.class.getClassLoader()) {
                  return false;
               } else if (ReflectUtil.isVMAnonymousClass(var1)) {
                  return false;
               } else {
                  MethodType var2 = var0.getMethodOrFieldType();
                  if (!isStaticallyNameable(var2.returnType())) {
                     return false;
                  } else {
                     Class[] var3 = var2.parameterArray();
                     int var4 = var3.length;

                     for(int var5 = 0; var5 < var4; ++var5) {
                        Class var6 = var3[var5];
                        if (!isStaticallyNameable(var6)) {
                           return false;
                        }
                     }

                     if (!var0.isPrivate() && VerifyAccess.isSamePackage(MethodHandle.class, var1)) {
                        return true;
                     } else if (var0.isPublic() && isStaticallyNameable(var1)) {
                        return true;
                     } else {
                        return false;
                     }
                  }
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   static boolean isStaticallyNameable(Class<?> var0) {
      if (var0 == Object.class) {
         return true;
      } else {
         while(var0.isArray()) {
            var0 = var0.getComponentType();
         }

         if (var0.isPrimitive()) {
            return true;
         } else if (ReflectUtil.isVMAnonymousClass(var0)) {
            return false;
         } else if (var0.getClassLoader() != Object.class.getClassLoader()) {
            return false;
         } else if (VerifyAccess.isSamePackage(MethodHandle.class, var0)) {
            return true;
         } else if (!Modifier.isPublic(var0.getModifiers())) {
            return false;
         } else {
            Class[] var1 = STATICALLY_INVOCABLE_PACKAGES;
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               Class var4 = var1[var3];
               if (VerifyAccess.isSamePackage(var4, var0)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   void emitStaticInvoke(LambdaForm.Name var1) {
      this.emitStaticInvoke(var1.function.member(), var1);
   }

   void emitStaticInvoke(MemberName var1, LambdaForm.Name var2) {
      assert var1.equals(var2.function.member());

      Class var3 = var1.getDeclaringClass();
      String var4 = getInternalName(var3);
      String var5 = var1.getName();
      byte var7 = var1.getReferenceKind();
      if (var7 == 7) {
         assert var1.canBeStaticallyBound() : var1;

         var7 = 5;
      }

      if (var1.getDeclaringClass().isInterface() && var7 == 5) {
         var7 = 9;
      }

      this.emitPushArguments(var2);
      String var6;
      if (var1.isMethod()) {
         var6 = var1.getMethodType().toMethodDescriptorString();
         this.mv.visitMethodInsn(this.refKindOpcode(var7), var4, var5, var6, var1.getDeclaringClass().isInterface());
      } else {
         var6 = MethodType.toFieldDescriptorString(var1.getFieldType());
         this.mv.visitFieldInsn(this.refKindOpcode(var7), var4, var5, var6);
      }

      if (var2.type == LambdaForm.BasicType.L_TYPE) {
         Class var8 = var1.getInvocationType().returnType();

         assert !var8.isPrimitive();

         if (var8 != Object.class && !var8.isInterface()) {
            this.assertStaticType(var8, var2);
         }
      }

   }

   void emitNewArray(LambdaForm.Name var1) throws InternalError {
      Class var2 = var1.function.methodType().returnType();
      if (var1.arguments.length == 0) {
         Object var7;
         try {
            var7 = var1.function.resolvedHandle.invoke();
         } catch (Throwable var6) {
            throw MethodHandleStatics.newInternalError(var6);
         }

         assert Array.getLength(var7) == 0;

         assert var7.getClass() == var2;

         this.mv.visitLdcInsn(this.constantPlaceholder(var7));
         this.emitReferenceCast(var2, var7);
      } else {
         Class var3 = var2.getComponentType();

         assert var3 != null;

         this.emitIconstInsn(var1.arguments.length);
         int var4 = 83;
         if (!var3.isPrimitive()) {
            this.mv.visitTypeInsn(189, getInternalName(var3));
         } else {
            byte var5 = this.arrayTypeCode(Wrapper.forPrimitiveType(var3));
            var4 = this.arrayInsnOpcode(var5, var4);
            this.mv.visitIntInsn(188, var5);
         }

         for(int var8 = 0; var8 < var1.arguments.length; ++var8) {
            this.mv.visitInsn(89);
            this.emitIconstInsn(var8);
            this.emitPushArgument(var1, var8);
            this.mv.visitInsn(var4);
         }

         this.assertStaticType(var2, var1);
      }
   }

   int refKindOpcode(byte var1) {
      switch(var1) {
      case 1:
         return 180;
      case 2:
         return 178;
      case 3:
         return 181;
      case 4:
         return 179;
      case 5:
         return 182;
      case 6:
         return 184;
      case 7:
         return 183;
      case 8:
      default:
         throw new InternalError("refKind=" + var1);
      case 9:
         return 185;
      }
   }

   private boolean memberRefersTo(MemberName var1, Class<?> var2, String var3) {
      return var1 != null && var1.getDeclaringClass() == var2 && var1.getName().equals(var3);
   }

   private boolean nameRefersTo(LambdaForm.Name var1, Class<?> var2, String var3) {
      return var1.function != null && this.memberRefersTo(var1.function.member(), var2, var3);
   }

   private boolean isInvokeBasic(LambdaForm.Name var1) {
      if (var1.function == null) {
         return false;
      } else if (var1.arguments.length < 1) {
         return false;
      } else {
         MemberName var2 = var1.function.member();
         return this.memberRefersTo(var2, MethodHandle.class, "invokeBasic") && !var2.isPublic() && !var2.isStatic();
      }
   }

   private boolean isLinkerMethodInvoke(LambdaForm.Name var1) {
      if (var1.function == null) {
         return false;
      } else if (var1.arguments.length < 1) {
         return false;
      } else {
         MemberName var2 = var1.function.member();
         return var2 != null && var2.getDeclaringClass() == MethodHandle.class && !var2.isPublic() && var2.isStatic() && var2.getName().startsWith("linkTo");
      }
   }

   private boolean isSelectAlternative(int var1) {
      if (var1 + 1 >= this.lambdaForm.names.length) {
         return false;
      } else {
         LambdaForm.Name var2 = this.lambdaForm.names[var1];
         LambdaForm.Name var3 = this.lambdaForm.names[var1 + 1];
         return this.nameRefersTo(var2, MethodHandleImpl.class, "selectAlternative") && this.isInvokeBasic(var3) && var3.lastUseIndex(var2) == 0 && this.lambdaForm.lastUseIndex(var2) == var1 + 1;
      }
   }

   private boolean isGuardWithCatch(int var1) {
      if (var1 + 2 >= this.lambdaForm.names.length) {
         return false;
      } else {
         LambdaForm.Name var2 = this.lambdaForm.names[var1];
         LambdaForm.Name var3 = this.lambdaForm.names[var1 + 1];
         LambdaForm.Name var4 = this.lambdaForm.names[var1 + 2];
         return this.nameRefersTo(var3, MethodHandleImpl.class, "guardWithCatch") && this.isInvokeBasic(var2) && this.isInvokeBasic(var4) && var3.lastUseIndex(var2) == 3 && this.lambdaForm.lastUseIndex(var2) == var1 + 1 && var4.lastUseIndex(var3) == 1 && this.lambdaForm.lastUseIndex(var3) == var1 + 2;
      }
   }

   private LambdaForm.Name emitSelectAlternative(LambdaForm.Name var1, LambdaForm.Name var2) {
      assert isStaticallyInvocable(var2);

      LambdaForm.Name var3 = (LambdaForm.Name)var2.arguments[0];
      Label var4 = new Label();
      Label var5 = new Label();
      this.emitPushArgument(var1, 0);
      this.mv.visitJumpInsn(153, var4);
      Class[] var6 = (Class[])this.localClasses.clone();
      this.emitPushArgument(var1, 1);
      this.emitAstoreInsn(var3.index());
      this.emitStaticInvoke(var2);
      this.mv.visitJumpInsn(167, var5);
      this.mv.visitLabel(var4);
      System.arraycopy(var6, 0, this.localClasses, 0, var6.length);
      this.emitPushArgument(var1, 2);
      this.emitAstoreInsn(var3.index());
      this.emitStaticInvoke(var2);
      this.mv.visitLabel(var5);
      System.arraycopy(var6, 0, this.localClasses, 0, var6.length);
      return var2;
   }

   private LambdaForm.Name emitGuardWithCatch(int var1) {
      LambdaForm.Name var2 = this.lambdaForm.names[var1];
      LambdaForm.Name var3 = this.lambdaForm.names[var1 + 1];
      LambdaForm.Name var4 = this.lambdaForm.names[var1 + 2];
      Label var5 = new Label();
      Label var6 = new Label();
      Label var7 = new Label();
      Label var8 = new Label();
      Class var9 = var4.function.resolvedHandle.type().returnType();
      MethodType var10 = var2.function.resolvedHandle.type().dropParameterTypes(0, 1).changeReturnType(var9);
      this.mv.visitTryCatchBlock(var5, var6, var7, "java/lang/Throwable");
      this.mv.visitLabel(var5);
      this.emitPushArgument(var3, 0);
      this.emitPushArguments(var2, 1);
      this.mv.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", var10.basicType().toMethodDescriptorString(), false);
      this.mv.visitLabel(var6);
      this.mv.visitJumpInsn(167, var8);
      this.mv.visitLabel(var7);
      this.mv.visitInsn(89);
      this.emitPushArgument(var3, 1);
      this.mv.visitInsn(95);
      this.mv.visitMethodInsn(182, "java/lang/Class", "isInstance", "(Ljava/lang/Object;)Z", false);
      Label var11 = new Label();
      this.mv.visitJumpInsn(153, var11);
      this.emitPushArgument(var3, 2);
      this.mv.visitInsn(95);
      this.emitPushArguments(var2, 1);
      MethodType var12 = var10.insertParameterTypes(0, (Class[])(Throwable.class));
      this.mv.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", var12.basicType().toMethodDescriptorString(), false);
      this.mv.visitJumpInsn(167, var8);
      this.mv.visitLabel(var11);
      this.mv.visitInsn(191);
      this.mv.visitLabel(var8);
      return var4;
   }

   private void emitPushArguments(LambdaForm.Name var1) {
      this.emitPushArguments(var1, 0);
   }

   private void emitPushArguments(LambdaForm.Name var1, int var2) {
      for(int var3 = var2; var3 < var1.arguments.length; ++var3) {
         this.emitPushArgument(var1, var3);
      }

   }

   private void emitPushArgument(LambdaForm.Name var1, int var2) {
      Object var3 = var1.arguments[var2];
      Class var4 = var1.function.methodType().parameterType(var2);
      this.emitPushArgument(var4, var3);
   }

   private void emitPushArgument(Class<?> var1, Object var2) {
      LambdaForm.BasicType var3 = LambdaForm.BasicType.basicType(var1);
      if (var2 instanceof LambdaForm.Name) {
         LambdaForm.Name var4 = (LambdaForm.Name)var2;
         this.emitLoadInsn(var4.type, var4.index());
         this.emitImplicitConversion(var4.type, var1, var4);
      } else if ((var2 == null || var2 instanceof String) && var3 == LambdaForm.BasicType.L_TYPE) {
         this.emitConst(var2);
      } else if (Wrapper.isWrapperType(var2.getClass()) && var3 != LambdaForm.BasicType.L_TYPE) {
         this.emitConst(var2);
      } else {
         this.mv.visitLdcInsn(this.constantPlaceholder(var2));
         this.emitImplicitConversion(LambdaForm.BasicType.L_TYPE, var1, var2);
      }

   }

   private void emitStoreResult(LambdaForm.Name var1) {
      if (var1 != null && var1.type != LambdaForm.BasicType.V_TYPE) {
         this.emitStoreInsn(var1.type, var1.index());
      }

   }

   private void emitReturn(LambdaForm.Name var1) {
      Class var2 = this.invokerType.returnType();
      LambdaForm.BasicType var3 = this.lambdaForm.returnType();

      assert var3 == LambdaForm.BasicType.basicType(var2);

      if (var3 == LambdaForm.BasicType.V_TYPE) {
         this.mv.visitInsn(177);
      } else {
         LambdaForm.Name var4 = this.lambdaForm.names[this.lambdaForm.result];
         if (var4 != var1) {
            this.emitLoadInsn(var3, this.lambdaForm.result);
         }

         this.emitImplicitConversion(var3, var2, var4);
         this.emitReturnInsn(var3);
      }

   }

   private void emitPrimCast(Wrapper var1, Wrapper var2) {
      if (var1 != var2) {
         if (var1.isSubwordOrInt()) {
            this.emitI2X(var2);
         } else if (var2.isSubwordOrInt()) {
            this.emitX2I(var1);
            if (var2.bitWidth() < 32) {
               this.emitI2X(var2);
            }
         } else {
            boolean var3;
            var3 = false;
            label32:
            switch(var1) {
            case LONG:
               switch(var2) {
               case FLOAT:
                  this.mv.visitInsn(137);
                  break label32;
               case DOUBLE:
                  this.mv.visitInsn(138);
                  break label32;
               default:
                  var3 = true;
                  break label32;
               }
            case FLOAT:
               switch(var2) {
               case LONG:
                  this.mv.visitInsn(140);
                  break label32;
               case DOUBLE:
                  this.mv.visitInsn(141);
                  break label32;
               default:
                  var3 = true;
                  break label32;
               }
            case DOUBLE:
               switch(var2) {
               case LONG:
                  this.mv.visitInsn(143);
                  break label32;
               case FLOAT:
                  this.mv.visitInsn(144);
                  break label32;
               default:
                  var3 = true;
                  break label32;
               }
            default:
               var3 = true;
            }

            if (var3) {
               throw new IllegalStateException("unhandled prim cast: " + var1 + "2" + var2);
            }
         }

      }
   }

   private void emitI2X(Wrapper var1) {
      switch(var1) {
      case BOOLEAN:
         this.mv.visitInsn(4);
         this.mv.visitInsn(126);
         break;
      case BYTE:
         this.mv.visitInsn(145);
         break;
      case CHAR:
         this.mv.visitInsn(146);
         break;
      case SHORT:
         this.mv.visitInsn(147);
      case INT:
         break;
      case LONG:
         this.mv.visitInsn(133);
         break;
      case FLOAT:
         this.mv.visitInsn(134);
         break;
      case DOUBLE:
         this.mv.visitInsn(135);
         break;
      default:
         throw new InternalError("unknown type: " + var1);
      }

   }

   private void emitX2I(Wrapper var1) {
      switch(var1) {
      case LONG:
         this.mv.visitInsn(136);
         break;
      case FLOAT:
         this.mv.visitInsn(139);
         break;
      case DOUBLE:
         this.mv.visitInsn(142);
         break;
      default:
         throw new InternalError("unknown type: " + var1);
      }

   }

   static MemberName generateLambdaFormInterpreterEntryPoint(String var0) {
      assert LambdaForm.isValidSignature(var0);

      String var1 = "interpret_" + LambdaForm.signatureReturn(var0).basicTypeChar();
      MethodType var2 = LambdaForm.signatureType(var0);
      var2 = var2.changeParameterType(0, MethodHandle.class);
      InvokerBytecodeGenerator var3 = new InvokerBytecodeGenerator("LFI", var1, var2);
      return var3.loadMethod(var3.generateLambdaFormInterpreterEntryPointBytes());
   }

   private byte[] generateLambdaFormInterpreterEntryPointBytes() {
      this.classFilePrologue();
      this.mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
      this.mv.visitAnnotation("Ljava/lang/invoke/DontInline;", true);
      this.emitIconstInsn(this.invokerType.parameterCount());
      this.mv.visitTypeInsn(189, "java/lang/Object");

      for(int var1 = 0; var1 < this.invokerType.parameterCount(); ++var1) {
         Class var2 = this.invokerType.parameterType(var1);
         this.mv.visitInsn(89);
         this.emitIconstInsn(var1);
         this.emitLoadInsn(LambdaForm.BasicType.basicType(var2), var1);
         if (var2.isPrimitive()) {
            this.emitBoxing(Wrapper.forPrimitiveType(var2));
         }

         this.mv.visitInsn(83);
      }

      this.emitAloadInsn(0);
      this.mv.visitFieldInsn(180, "java/lang/invoke/MethodHandle", "form", "Ljava/lang/invoke/LambdaForm;");
      this.mv.visitInsn(95);
      this.mv.visitMethodInsn(182, "java/lang/invoke/LambdaForm", "interpretWithArguments", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
      Class var3 = this.invokerType.returnType();
      if (var3.isPrimitive() && var3 != Void.TYPE) {
         this.emitUnboxing(Wrapper.forPrimitiveType(var3));
      }

      this.emitReturnInsn(LambdaForm.BasicType.basicType(var3));
      this.classFileEpilogue();
      this.bogusMethod(this.invokerType);
      byte[] var4 = this.cw.toByteArray();
      maybeDump(this.className, var4);
      return var4;
   }

   static MemberName generateNamedFunctionInvoker(MethodTypeForm var0) {
      MethodType var1 = LambdaForm.NamedFunction.INVOKER_METHOD_TYPE;
      String var2 = "invoke_" + LambdaForm.shortenSignature(LambdaForm.basicTypeSignature(var0.erasedType()));
      InvokerBytecodeGenerator var3 = new InvokerBytecodeGenerator("NFI", var2, var1);
      return var3.loadMethod(var3.generateNamedFunctionInvokerImpl(var0));
   }

   private byte[] generateNamedFunctionInvokerImpl(MethodTypeForm var1) {
      MethodType var2 = var1.erasedType();
      this.classFilePrologue();
      this.mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
      this.mv.visitAnnotation("Ljava/lang/invoke/ForceInline;", true);
      this.emitAloadInsn(0);

      Class var4;
      Wrapper var6;
      for(int var3 = 0; var3 < var2.parameterCount(); ++var3) {
         this.emitAloadInsn(1);
         this.emitIconstInsn(var3);
         this.mv.visitInsn(50);
         var4 = var2.parameterType(var3);
         if (var4.isPrimitive()) {
            Class var5 = var2.basicType().wrap().parameterType(var3);
            var6 = Wrapper.forBasicType(var4);
            Wrapper var7 = var6.isSubwordOrInt() ? Wrapper.INT : var6;
            this.emitUnboxing(var7);
            this.emitPrimCast(var7, var6);
         }
      }

      String var8 = var2.basicType().toMethodDescriptorString();
      this.mv.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", var8, false);
      var4 = var2.returnType();
      if (var4 != Void.TYPE && var4.isPrimitive()) {
         Wrapper var9 = Wrapper.forBasicType(var4);
         var6 = var9.isSubwordOrInt() ? Wrapper.INT : var9;
         this.emitPrimCast(var9, var6);
         this.emitBoxing(var6);
      }

      if (var4 == Void.TYPE) {
         this.mv.visitInsn(1);
      }

      this.emitReturnInsn(LambdaForm.BasicType.L_TYPE);
      this.classFileEpilogue();
      this.bogusMethod(var2);
      byte[] var10 = this.cw.toByteArray();
      maybeDump(this.className, var10);
      return var10;
   }

   private void bogusMethod(Object... var1) {
      if (MethodHandleStatics.DUMP_CLASS_FILES) {
         this.mv = this.cw.visitMethod(8, "dummy", "()V", (String)null, (String[])null);
         Object[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Object var5 = var2[var4];
            this.mv.visitLdcInsn(var5.toString());
            this.mv.visitInsn(87);
         }

         this.mv.visitInsn(177);
         this.mv.visitMaxs(0, 0);
         this.mv.visitEnd();
      }

   }

   static {
      if (MethodHandleStatics.DUMP_CLASS_FILES) {
         DUMP_CLASS_FILES_COUNTERS = new HashMap();

         try {
            File var0 = new File("DUMP_CLASS_FILES");
            if (!var0.exists()) {
               var0.mkdirs();
            }

            DUMP_CLASS_FILES_DIR = var0;
            System.out.println("Dumping class files to " + DUMP_CLASS_FILES_DIR + "/...");
         } catch (Exception var1) {
            throw MethodHandleStatics.newInternalError((Throwable)var1);
         }
      } else {
         DUMP_CLASS_FILES_COUNTERS = null;
         DUMP_CLASS_FILES_DIR = null;
      }

      STATICALLY_INVOCABLE_PACKAGES = new Class[]{Object.class, Arrays.class, Unsafe.class};
   }

   class CpPatch {
      final int index;
      final String placeholder;
      final Object value;

      CpPatch(int var2, String var3, Object var4) {
         this.index = var2;
         this.placeholder = var3;
         this.value = var4;
      }

      public String toString() {
         return "CpPatch/index=" + this.index + ",placeholder=" + this.placeholder + ",value=" + this.value;
      }
   }
}
