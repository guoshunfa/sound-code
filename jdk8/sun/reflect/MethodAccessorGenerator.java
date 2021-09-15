package sun.reflect;

import java.security.AccessController;
import java.security.PrivilegedAction;

class MethodAccessorGenerator extends AccessorGenerator {
   private static final short NUM_BASE_CPOOL_ENTRIES = 12;
   private static final short NUM_METHODS = 2;
   private static final short NUM_SERIALIZATION_CPOOL_ENTRIES = 2;
   private static volatile int methodSymnum = 0;
   private static volatile int constructorSymnum = 0;
   private static volatile int serializationConstructorSymnum = 0;
   private Class<?> declaringClass;
   private Class<?>[] parameterTypes;
   private Class<?> returnType;
   private boolean isConstructor;
   private boolean forSerialization;
   private short targetMethodRef;
   private short invokeIdx;
   private short invokeDescriptorIdx;
   private short nonPrimitiveParametersBaseIdx;

   public MethodAccessor generateMethod(Class<?> var1, String var2, Class<?>[] var3, Class<?> var4, Class<?>[] var5, int var6) {
      return (MethodAccessor)this.generate(var1, var2, var3, var4, var5, var6, false, false, (Class)null);
   }

   public ConstructorAccessor generateConstructor(Class<?> var1, Class<?>[] var2, Class<?>[] var3, int var4) {
      return (ConstructorAccessor)this.generate(var1, "<init>", var2, Void.TYPE, var3, var4, true, false, (Class)null);
   }

   public SerializationConstructorAccessorImpl generateSerializationConstructor(Class<?> var1, Class<?>[] var2, Class<?>[] var3, int var4, Class<?> var5) {
      return (SerializationConstructorAccessorImpl)this.generate(var1, "<init>", var2, Void.TYPE, var3, var4, true, true, var5);
   }

   private MagicAccessorImpl generate(final Class<?> var1, String var2, Class<?>[] var3, Class<?> var4, Class<?>[] var5, int var6, boolean var7, boolean var8, Class<?> var9) {
      ByteVector var10 = ByteVectorFactory.create();
      this.asm = new ClassFileAssembler(var10);
      this.declaringClass = var1;
      this.parameterTypes = var3;
      this.returnType = var4;
      this.modifiers = var6;
      this.isConstructor = var7;
      this.forSerialization = var8;
      this.asm.emitMagicAndVersion();
      short var11 = 42;
      boolean var12 = this.usesPrimitiveTypes();
      if (var12) {
         var11 = (short)(var11 + 72);
      }

      if (var8) {
         var11 = (short)(var11 + 2);
      }

      var11 += (short)(2 * this.numNonPrimitiveParameterTypes());
      this.asm.emitShort(add(var11, (short)1));
      final String var13 = generateName(var7, var8);
      this.asm.emitConstantPoolUTF8(var13);
      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.thisClass = this.asm.cpi();
      if (var7) {
         if (var8) {
            this.asm.emitConstantPoolUTF8("sun/reflect/SerializationConstructorAccessorImpl");
         } else {
            this.asm.emitConstantPoolUTF8("sun/reflect/ConstructorAccessorImpl");
         }
      } else {
         this.asm.emitConstantPoolUTF8("sun/reflect/MethodAccessorImpl");
      }

      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.superClass = this.asm.cpi();
      this.asm.emitConstantPoolUTF8(getClassName(var1, false));
      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.targetClass = this.asm.cpi();
      short var14 = 0;
      if (var8) {
         this.asm.emitConstantPoolUTF8(getClassName(var9, false));
         this.asm.emitConstantPoolClass(this.asm.cpi());
         var14 = this.asm.cpi();
      }

      this.asm.emitConstantPoolUTF8(var2);
      this.asm.emitConstantPoolUTF8(this.buildInternalSignature());
      this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
      if (this.isInterface()) {
         this.asm.emitConstantPoolInterfaceMethodref(this.targetClass, this.asm.cpi());
      } else if (var8) {
         this.asm.emitConstantPoolMethodref(var14, this.asm.cpi());
      } else {
         this.asm.emitConstantPoolMethodref(this.targetClass, this.asm.cpi());
      }

      this.targetMethodRef = this.asm.cpi();
      if (var7) {
         this.asm.emitConstantPoolUTF8("newInstance");
      } else {
         this.asm.emitConstantPoolUTF8("invoke");
      }

      this.invokeIdx = this.asm.cpi();
      if (var7) {
         this.asm.emitConstantPoolUTF8("([Ljava/lang/Object;)Ljava/lang/Object;");
      } else {
         this.asm.emitConstantPoolUTF8("(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
      }

      this.invokeDescriptorIdx = this.asm.cpi();
      this.nonPrimitiveParametersBaseIdx = add(this.asm.cpi(), (short)2);

      for(int var15 = 0; var15 < var3.length; ++var15) {
         Class var16 = var3[var15];
         if (!isPrimitive(var16)) {
            this.asm.emitConstantPoolUTF8(getClassName(var16, false));
            this.asm.emitConstantPoolClass(this.asm.cpi());
         }
      }

      this.emitCommonConstantPoolEntries();
      if (var12) {
         this.emitBoxingContantPoolEntries();
      }

      if (this.asm.cpi() != var11) {
         throw new InternalError("Adjust this code (cpi = " + this.asm.cpi() + ", numCPEntries = " + var11 + ")");
      } else {
         this.asm.emitShort((short)1);
         this.asm.emitShort(this.thisClass);
         this.asm.emitShort(this.superClass);
         this.asm.emitShort((short)0);
         this.asm.emitShort((short)0);
         this.asm.emitShort((short)2);
         this.emitConstructor();
         this.emitInvoke();
         this.asm.emitShort((short)0);
         var10.trim();
         final byte[] var17 = var10.getData();
         return (MagicAccessorImpl)AccessController.doPrivileged(new PrivilegedAction<MagicAccessorImpl>() {
            public MagicAccessorImpl run() {
               try {
                  return (MagicAccessorImpl)ClassDefiner.defineClass(var13, var17, 0, var17.length, var1.getClassLoader()).newInstance();
               } catch (IllegalAccessException | InstantiationException var2) {
                  throw new InternalError(var2);
               }
            }
         });
      }
   }

   private void emitInvoke() {
      if (this.parameterTypes.length > 65535) {
         throw new InternalError("Can't handle more than 65535 parameters");
      } else {
         ClassFileAssembler var1 = new ClassFileAssembler();
         if (this.isConstructor) {
            var1.setMaxLocals(2);
         } else {
            var1.setMaxLocals(3);
         }

         short var2 = 0;
         Label var3;
         if (this.isConstructor) {
            var1.opc_new(this.targetClass);
            var1.opc_dup();
         } else {
            if (isPrimitive(this.returnType)) {
               var1.opc_new(this.indexForPrimitiveType(this.returnType));
               var1.opc_dup();
            }

            if (!this.isStatic()) {
               var1.opc_aload_1();
               var3 = new Label();
               var1.opc_ifnonnull(var3);
               var1.opc_new(this.nullPointerClass);
               var1.opc_dup();
               var1.opc_invokespecial(this.nullPointerCtorIdx, 0, 0);
               var1.opc_athrow();
               var3.bind();
               var2 = var1.getLength();
               var1.opc_aload_1();
               var1.opc_checkcast(this.targetClass);
            }
         }

         var3 = new Label();
         if (this.parameterTypes.length == 0) {
            if (this.isConstructor) {
               var1.opc_aload_1();
            } else {
               var1.opc_aload_2();
            }

            var1.opc_ifnull(var3);
         }

         if (this.isConstructor) {
            var1.opc_aload_1();
         } else {
            var1.opc_aload_2();
         }

         var1.opc_arraylength();
         var1.opc_sipush((short)this.parameterTypes.length);
         var1.opc_if_icmpeq(var3);
         var1.opc_new(this.illegalArgumentClass);
         var1.opc_dup();
         var1.opc_invokespecial(this.illegalArgumentCtorIdx, 0, 0);
         var1.opc_athrow();
         var3.bind();
         short var4 = this.nonPrimitiveParametersBaseIdx;
         Label var5 = null;
         byte var6 = 1;

         for(int var7 = 0; var7 < this.parameterTypes.length; ++var7) {
            Class var8 = this.parameterTypes[var7];
            var6 += (byte)this.typeSizeInStackSlots(var8);
            if (var5 != null) {
               var5.bind();
               var5 = null;
            }

            if (this.isConstructor) {
               var1.opc_aload_1();
            } else {
               var1.opc_aload_2();
            }

            var1.opc_sipush((short)var7);
            var1.opc_aaload();
            if (!isPrimitive(var8)) {
               var1.opc_checkcast(var4);
               var4 = add(var4, (short)2);
            } else {
               if (this.isConstructor) {
                  var1.opc_astore_2();
               } else {
                  var1.opc_astore_3();
               }

               Label var9 = null;
               var5 = new Label();

               for(int var10 = 0; var10 < primitiveTypes.length; ++var10) {
                  Class var11 = primitiveTypes[var10];
                  if (canWidenTo(var11, var8)) {
                     if (var9 != null) {
                        var9.bind();
                     }

                     if (this.isConstructor) {
                        var1.opc_aload_2();
                     } else {
                        var1.opc_aload_3();
                     }

                     var1.opc_instanceof(this.indexForPrimitiveType(var11));
                     var9 = new Label();
                     var1.opc_ifeq(var9);
                     if (this.isConstructor) {
                        var1.opc_aload_2();
                     } else {
                        var1.opc_aload_3();
                     }

                     var1.opc_checkcast(this.indexForPrimitiveType(var11));
                     var1.opc_invokevirtual(this.unboxingMethodForPrimitiveType(var11), 0, this.typeSizeInStackSlots(var11));
                     emitWideningBytecodeForPrimitiveConversion(var1, var11, var8);
                     var1.opc_goto(var5);
                  }
               }

               if (var9 == null) {
                  throw new InternalError("Must have found at least identity conversion");
               }

               var9.bind();
               var1.opc_new(this.illegalArgumentClass);
               var1.opc_dup();
               var1.opc_invokespecial(this.illegalArgumentCtorIdx, 0, 0);
               var1.opc_athrow();
            }
         }

         if (var5 != null) {
            var5.bind();
         }

         short var12 = var1.getLength();
         if (this.isConstructor) {
            var1.opc_invokespecial(this.targetMethodRef, var6, 0);
         } else if (this.isStatic()) {
            var1.opc_invokestatic(this.targetMethodRef, var6, this.typeSizeInStackSlots(this.returnType));
         } else if (this.isInterface()) {
            if (this.isPrivate()) {
               var1.opc_invokespecial(this.targetMethodRef, var6, 0);
            } else {
               var1.opc_invokeinterface(this.targetMethodRef, var6, var6, this.typeSizeInStackSlots(this.returnType));
            }
         } else {
            var1.opc_invokevirtual(this.targetMethodRef, var6, this.typeSizeInStackSlots(this.returnType));
         }

         short var13 = var1.getLength();
         if (!this.isConstructor) {
            if (isPrimitive(this.returnType)) {
               var1.opc_invokespecial(this.ctorIndexForPrimitiveType(this.returnType), this.typeSizeInStackSlots(this.returnType), 0);
            } else if (this.returnType == Void.TYPE) {
               var1.opc_aconst_null();
            }
         }

         var1.opc_areturn();
         short var14 = var1.getLength();
         var1.setStack(1);
         var1.opc_invokespecial(this.toStringIdx, 0, 1);
         var1.opc_new(this.illegalArgumentClass);
         var1.opc_dup_x1();
         var1.opc_swap();
         var1.opc_invokespecial(this.illegalArgumentStringCtorIdx, 1, 0);
         var1.opc_athrow();
         short var15 = var1.getLength();
         var1.setStack(1);
         var1.opc_new(this.invocationTargetClass);
         var1.opc_dup_x1();
         var1.opc_swap();
         var1.opc_invokespecial(this.invocationTargetCtorIdx, 1, 0);
         var1.opc_athrow();
         ClassFileAssembler var16 = new ClassFileAssembler();
         var16.emitShort(var2);
         var16.emitShort(var12);
         var16.emitShort(var14);
         var16.emitShort(this.classCastClass);
         var16.emitShort(var2);
         var16.emitShort(var12);
         var16.emitShort(var14);
         var16.emitShort(this.nullPointerClass);
         var16.emitShort(var12);
         var16.emitShort(var13);
         var16.emitShort(var15);
         var16.emitShort(this.throwableClass);
         this.emitMethod(this.invokeIdx, var1.getMaxLocals(), var1, var16, new short[]{this.invocationTargetClass});
      }
   }

   private boolean usesPrimitiveTypes() {
      if (this.returnType.isPrimitive()) {
         return true;
      } else {
         for(int var1 = 0; var1 < this.parameterTypes.length; ++var1) {
            if (this.parameterTypes[var1].isPrimitive()) {
               return true;
            }
         }

         return false;
      }
   }

   private int numNonPrimitiveParameterTypes() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.parameterTypes.length; ++var2) {
         if (!this.parameterTypes[var2].isPrimitive()) {
            ++var1;
         }
      }

      return var1;
   }

   private boolean isInterface() {
      return this.declaringClass.isInterface();
   }

   private String buildInternalSignature() {
      StringBuffer var1 = new StringBuffer();
      var1.append("(");

      for(int var2 = 0; var2 < this.parameterTypes.length; ++var2) {
         var1.append(getClassName(this.parameterTypes[var2], true));
      }

      var1.append(")");
      var1.append(getClassName(this.returnType, true));
      return var1.toString();
   }

   private static synchronized String generateName(boolean var0, boolean var1) {
      int var2;
      if (var0) {
         if (var1) {
            var2 = ++serializationConstructorSymnum;
            return "sun/reflect/GeneratedSerializationConstructorAccessor" + var2;
         } else {
            var2 = ++constructorSymnum;
            return "sun/reflect/GeneratedConstructorAccessor" + var2;
         }
      } else {
         var2 = ++methodSymnum;
         return "sun/reflect/GeneratedMethodAccessor" + var2;
      }
   }
}
