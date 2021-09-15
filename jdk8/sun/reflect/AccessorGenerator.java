package sun.reflect;

import java.lang.reflect.Modifier;
import sun.misc.Unsafe;

class AccessorGenerator implements ClassFileConstants {
   static final Unsafe unsafe = Unsafe.getUnsafe();
   protected static final short S0 = 0;
   protected static final short S1 = 1;
   protected static final short S2 = 2;
   protected static final short S3 = 3;
   protected static final short S4 = 4;
   protected static final short S5 = 5;
   protected static final short S6 = 6;
   protected ClassFileAssembler asm;
   protected int modifiers;
   protected short thisClass;
   protected short superClass;
   protected short targetClass;
   protected short throwableClass;
   protected short classCastClass;
   protected short nullPointerClass;
   protected short illegalArgumentClass;
   protected short invocationTargetClass;
   protected short initIdx;
   protected short initNameAndTypeIdx;
   protected short initStringNameAndTypeIdx;
   protected short nullPointerCtorIdx;
   protected short illegalArgumentCtorIdx;
   protected short illegalArgumentStringCtorIdx;
   protected short invocationTargetCtorIdx;
   protected short superCtorIdx;
   protected short objectClass;
   protected short toStringIdx;
   protected short codeIdx;
   protected short exceptionsIdx;
   protected short booleanIdx;
   protected short booleanCtorIdx;
   protected short booleanUnboxIdx;
   protected short byteIdx;
   protected short byteCtorIdx;
   protected short byteUnboxIdx;
   protected short characterIdx;
   protected short characterCtorIdx;
   protected short characterUnboxIdx;
   protected short doubleIdx;
   protected short doubleCtorIdx;
   protected short doubleUnboxIdx;
   protected short floatIdx;
   protected short floatCtorIdx;
   protected short floatUnboxIdx;
   protected short integerIdx;
   protected short integerCtorIdx;
   protected short integerUnboxIdx;
   protected short longIdx;
   protected short longCtorIdx;
   protected short longUnboxIdx;
   protected short shortIdx;
   protected short shortCtorIdx;
   protected short shortUnboxIdx;
   protected final short NUM_COMMON_CPOOL_ENTRIES = 30;
   protected final short NUM_BOXING_CPOOL_ENTRIES = 72;
   protected static final Class<?>[] primitiveTypes;
   private ClassFileAssembler illegalArgumentCodeBuffer;

   protected void emitCommonConstantPoolEntries() {
      this.asm.emitConstantPoolUTF8("java/lang/Throwable");
      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.throwableClass = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("java/lang/ClassCastException");
      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.classCastClass = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("java/lang/NullPointerException");
      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.nullPointerClass = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("java/lang/IllegalArgumentException");
      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.illegalArgumentClass = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("java/lang/reflect/InvocationTargetException");
      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.invocationTargetClass = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("<init>");
      this.initIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("()V");
      this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
      this.initNameAndTypeIdx = this.asm.cpi();
      this.asm.emitConstantPoolMethodref(this.nullPointerClass, this.initNameAndTypeIdx);
      this.nullPointerCtorIdx = this.asm.cpi();
      this.asm.emitConstantPoolMethodref(this.illegalArgumentClass, this.initNameAndTypeIdx);
      this.illegalArgumentCtorIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("(Ljava/lang/String;)V");
      this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
      this.initStringNameAndTypeIdx = this.asm.cpi();
      this.asm.emitConstantPoolMethodref(this.illegalArgumentClass, this.initStringNameAndTypeIdx);
      this.illegalArgumentStringCtorIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("(Ljava/lang/Throwable;)V");
      this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
      this.asm.emitConstantPoolMethodref(this.invocationTargetClass, this.asm.cpi());
      this.invocationTargetCtorIdx = this.asm.cpi();
      this.asm.emitConstantPoolMethodref(this.superClass, this.initNameAndTypeIdx);
      this.superCtorIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("java/lang/Object");
      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.objectClass = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("toString");
      this.asm.emitConstantPoolUTF8("()Ljava/lang/String;");
      this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
      this.asm.emitConstantPoolMethodref(this.objectClass, this.asm.cpi());
      this.toStringIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("Code");
      this.codeIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("Exceptions");
      this.exceptionsIdx = this.asm.cpi();
   }

   protected void emitBoxingContantPoolEntries() {
      this.asm.emitConstantPoolUTF8("java/lang/Boolean");
      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.booleanIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("(Z)V");
      this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
      this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
      this.booleanCtorIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("booleanValue");
      this.asm.emitConstantPoolUTF8("()Z");
      this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
      this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
      this.booleanUnboxIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("java/lang/Byte");
      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.byteIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("(B)V");
      this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
      this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
      this.byteCtorIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("byteValue");
      this.asm.emitConstantPoolUTF8("()B");
      this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
      this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
      this.byteUnboxIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("java/lang/Character");
      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.characterIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("(C)V");
      this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
      this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
      this.characterCtorIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("charValue");
      this.asm.emitConstantPoolUTF8("()C");
      this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
      this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
      this.characterUnboxIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("java/lang/Double");
      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.doubleIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("(D)V");
      this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
      this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
      this.doubleCtorIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("doubleValue");
      this.asm.emitConstantPoolUTF8("()D");
      this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
      this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
      this.doubleUnboxIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("java/lang/Float");
      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.floatIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("(F)V");
      this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
      this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
      this.floatCtorIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("floatValue");
      this.asm.emitConstantPoolUTF8("()F");
      this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
      this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
      this.floatUnboxIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("java/lang/Integer");
      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.integerIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("(I)V");
      this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
      this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
      this.integerCtorIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("intValue");
      this.asm.emitConstantPoolUTF8("()I");
      this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
      this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
      this.integerUnboxIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("java/lang/Long");
      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.longIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("(J)V");
      this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
      this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
      this.longCtorIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("longValue");
      this.asm.emitConstantPoolUTF8("()J");
      this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
      this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
      this.longUnboxIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("java/lang/Short");
      this.asm.emitConstantPoolClass(this.asm.cpi());
      this.shortIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("(S)V");
      this.asm.emitConstantPoolNameAndType(this.initIdx, this.asm.cpi());
      this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)2), this.asm.cpi());
      this.shortCtorIdx = this.asm.cpi();
      this.asm.emitConstantPoolUTF8("shortValue");
      this.asm.emitConstantPoolUTF8("()S");
      this.asm.emitConstantPoolNameAndType(sub(this.asm.cpi(), (short)1), this.asm.cpi());
      this.asm.emitConstantPoolMethodref(sub(this.asm.cpi(), (short)6), this.asm.cpi());
      this.shortUnboxIdx = this.asm.cpi();
   }

   protected static short add(short var0, short var1) {
      return (short)(var0 + var1);
   }

   protected static short sub(short var0, short var1) {
      return (short)(var0 - var1);
   }

   protected boolean isStatic() {
      return Modifier.isStatic(this.modifiers);
   }

   protected boolean isPrivate() {
      return Modifier.isPrivate(this.modifiers);
   }

   protected static String getClassName(Class<?> var0, boolean var1) {
      if (var0.isPrimitive()) {
         if (var0 == Boolean.TYPE) {
            return "Z";
         } else if (var0 == Byte.TYPE) {
            return "B";
         } else if (var0 == Character.TYPE) {
            return "C";
         } else if (var0 == Double.TYPE) {
            return "D";
         } else if (var0 == Float.TYPE) {
            return "F";
         } else if (var0 == Integer.TYPE) {
            return "I";
         } else if (var0 == Long.TYPE) {
            return "J";
         } else if (var0 == Short.TYPE) {
            return "S";
         } else if (var0 == Void.TYPE) {
            return "V";
         } else {
            throw new InternalError("Should have found primitive type");
         }
      } else if (var0.isArray()) {
         return "[" + getClassName(var0.getComponentType(), true);
      } else {
         return var1 ? internalize("L" + var0.getName() + ";") : internalize(var0.getName());
      }
   }

   private static String internalize(String var0) {
      return var0.replace('.', '/');
   }

   protected void emitConstructor() {
      ClassFileAssembler var1 = new ClassFileAssembler();
      var1.setMaxLocals(1);
      var1.opc_aload_0();
      var1.opc_invokespecial(this.superCtorIdx, 0, 0);
      var1.opc_return();
      this.emitMethod(this.initIdx, var1.getMaxLocals(), var1, (ClassFileAssembler)null, (short[])null);
   }

   protected void emitMethod(short var1, int var2, ClassFileAssembler var3, ClassFileAssembler var4, short[] var5) {
      short var6 = var3.getLength();
      short var7 = 0;
      if (var4 != null) {
         var7 = var4.getLength();
         if (var7 % 8 != 0) {
            throw new IllegalArgumentException("Illegal exception table");
         }
      }

      int var8 = 12 + var6 + var7;
      int var10 = var7 / 8;
      this.asm.emitShort((short)1);
      this.asm.emitShort(var1);
      this.asm.emitShort(add(var1, (short)1));
      if (var5 == null) {
         this.asm.emitShort((short)1);
      } else {
         this.asm.emitShort((short)2);
      }

      this.asm.emitShort(this.codeIdx);
      this.asm.emitInt(var8);
      this.asm.emitShort(var3.getMaxStack());
      this.asm.emitShort((short)Math.max(var2, var3.getMaxLocals()));
      this.asm.emitInt(var6);
      this.asm.append(var3);
      this.asm.emitShort((short)var10);
      if (var4 != null) {
         this.asm.append(var4);
      }

      this.asm.emitShort((short)0);
      if (var5 != null) {
         this.asm.emitShort(this.exceptionsIdx);
         this.asm.emitInt(2 + 2 * var5.length);
         this.asm.emitShort((short)var5.length);

         for(int var9 = 0; var9 < var5.length; ++var9) {
            this.asm.emitShort(var5[var9]);
         }
      }

   }

   protected short indexForPrimitiveType(Class<?> var1) {
      if (var1 == Boolean.TYPE) {
         return this.booleanIdx;
      } else if (var1 == Byte.TYPE) {
         return this.byteIdx;
      } else if (var1 == Character.TYPE) {
         return this.characterIdx;
      } else if (var1 == Double.TYPE) {
         return this.doubleIdx;
      } else if (var1 == Float.TYPE) {
         return this.floatIdx;
      } else if (var1 == Integer.TYPE) {
         return this.integerIdx;
      } else if (var1 == Long.TYPE) {
         return this.longIdx;
      } else if (var1 == Short.TYPE) {
         return this.shortIdx;
      } else {
         throw new InternalError("Should have found primitive type");
      }
   }

   protected short ctorIndexForPrimitiveType(Class<?> var1) {
      if (var1 == Boolean.TYPE) {
         return this.booleanCtorIdx;
      } else if (var1 == Byte.TYPE) {
         return this.byteCtorIdx;
      } else if (var1 == Character.TYPE) {
         return this.characterCtorIdx;
      } else if (var1 == Double.TYPE) {
         return this.doubleCtorIdx;
      } else if (var1 == Float.TYPE) {
         return this.floatCtorIdx;
      } else if (var1 == Integer.TYPE) {
         return this.integerCtorIdx;
      } else if (var1 == Long.TYPE) {
         return this.longCtorIdx;
      } else if (var1 == Short.TYPE) {
         return this.shortCtorIdx;
      } else {
         throw new InternalError("Should have found primitive type");
      }
   }

   protected static boolean canWidenTo(Class<?> var0, Class<?> var1) {
      if (!var0.isPrimitive()) {
         return false;
      } else {
         if (var0 == Boolean.TYPE) {
            if (var1 == Boolean.TYPE) {
               return true;
            }
         } else if (var0 == Byte.TYPE) {
            if (var1 == Byte.TYPE || var1 == Short.TYPE || var1 == Integer.TYPE || var1 == Long.TYPE || var1 == Float.TYPE || var1 == Double.TYPE) {
               return true;
            }
         } else if (var0 == Short.TYPE) {
            if (var1 == Short.TYPE || var1 == Integer.TYPE || var1 == Long.TYPE || var1 == Float.TYPE || var1 == Double.TYPE) {
               return true;
            }
         } else if (var0 == Character.TYPE) {
            if (var1 == Character.TYPE || var1 == Integer.TYPE || var1 == Long.TYPE || var1 == Float.TYPE || var1 == Double.TYPE) {
               return true;
            }
         } else if (var0 == Integer.TYPE) {
            if (var1 == Integer.TYPE || var1 == Long.TYPE || var1 == Float.TYPE || var1 == Double.TYPE) {
               return true;
            }
         } else if (var0 == Long.TYPE) {
            if (var1 == Long.TYPE || var1 == Float.TYPE || var1 == Double.TYPE) {
               return true;
            }
         } else if (var0 == Float.TYPE) {
            if (var1 == Float.TYPE || var1 == Double.TYPE) {
               return true;
            }
         } else if (var0 == Double.TYPE && var1 == Double.TYPE) {
            return true;
         }

         return false;
      }
   }

   protected static void emitWideningBytecodeForPrimitiveConversion(ClassFileAssembler var0, Class<?> var1, Class<?> var2) {
      if (var1 != Byte.TYPE && var1 != Short.TYPE && var1 != Character.TYPE && var1 != Integer.TYPE) {
         if (var1 == Long.TYPE) {
            if (var2 == Float.TYPE) {
               var0.opc_l2f();
            } else if (var2 == Double.TYPE) {
               var0.opc_l2d();
            }
         } else if (var1 == Float.TYPE && var2 == Double.TYPE) {
            var0.opc_f2d();
         }
      } else if (var2 == Long.TYPE) {
         var0.opc_i2l();
      } else if (var2 == Float.TYPE) {
         var0.opc_i2f();
      } else if (var2 == Double.TYPE) {
         var0.opc_i2d();
      }

   }

   protected short unboxingMethodForPrimitiveType(Class<?> var1) {
      if (var1 == Boolean.TYPE) {
         return this.booleanUnboxIdx;
      } else if (var1 == Byte.TYPE) {
         return this.byteUnboxIdx;
      } else if (var1 == Character.TYPE) {
         return this.characterUnboxIdx;
      } else if (var1 == Short.TYPE) {
         return this.shortUnboxIdx;
      } else if (var1 == Integer.TYPE) {
         return this.integerUnboxIdx;
      } else if (var1 == Long.TYPE) {
         return this.longUnboxIdx;
      } else if (var1 == Float.TYPE) {
         return this.floatUnboxIdx;
      } else if (var1 == Double.TYPE) {
         return this.doubleUnboxIdx;
      } else {
         throw new InternalError("Illegal primitive type " + var1.getName());
      }
   }

   protected static boolean isPrimitive(Class<?> var0) {
      return var0.isPrimitive() && var0 != Void.TYPE;
   }

   protected int typeSizeInStackSlots(Class<?> var1) {
      if (var1 == Void.TYPE) {
         return 0;
      } else {
         return var1 != Long.TYPE && var1 != Double.TYPE ? 1 : 2;
      }
   }

   protected ClassFileAssembler illegalArgumentCodeBuffer() {
      if (this.illegalArgumentCodeBuffer == null) {
         this.illegalArgumentCodeBuffer = new ClassFileAssembler();
         this.illegalArgumentCodeBuffer.opc_new(this.illegalArgumentClass);
         this.illegalArgumentCodeBuffer.opc_dup();
         this.illegalArgumentCodeBuffer.opc_invokespecial(this.illegalArgumentCtorIdx, 0, 0);
         this.illegalArgumentCodeBuffer.opc_athrow();
      }

      return this.illegalArgumentCodeBuffer;
   }

   static {
      primitiveTypes = new Class[]{Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE};
   }
}
