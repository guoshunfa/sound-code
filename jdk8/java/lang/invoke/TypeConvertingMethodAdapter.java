package java.lang.invoke;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import sun.invoke.util.BytecodeDescriptor;
import sun.invoke.util.Wrapper;

class TypeConvertingMethodAdapter extends MethodVisitor {
   private static final int NUM_WRAPPERS = Wrapper.values().length;
   private static final String NAME_OBJECT = "java/lang/Object";
   private static final String WRAPPER_PREFIX = "Ljava/lang/";
   private static final String NAME_BOX_METHOD = "valueOf";
   private static final int[][] wideningOpcodes;
   private static final Wrapper[] FROM_WRAPPER_NAME;
   private static final Wrapper[] FROM_TYPE_SORT;

   TypeConvertingMethodAdapter(MethodVisitor var1) {
      super(327680, var1);
   }

   private static void initWidening(Wrapper var0, int var1, Wrapper... var2) {
      Wrapper[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Wrapper var6 = var3[var5];
         wideningOpcodes[var6.ordinal()][var0.ordinal()] = var1;
      }

   }

   private static int hashWrapperName(String var0) {
      return var0.length() < 3 ? 0 : (3 * var0.charAt(1) + var0.charAt(2)) % 16;
   }

   private Wrapper wrapperOrNullFromDescriptor(String var1) {
      if (!var1.startsWith("Ljava/lang/")) {
         return null;
      } else {
         String var2 = var1.substring("Ljava/lang/".length(), var1.length() - 1);
         Wrapper var3 = FROM_WRAPPER_NAME[hashWrapperName(var2)];
         return var3 != null && !var3.wrapperSimpleName().equals(var2) ? null : var3;
      }
   }

   private static String wrapperName(Wrapper var0) {
      return "java/lang/" + var0.wrapperSimpleName();
   }

   private static String unboxMethod(Wrapper var0) {
      return var0.primitiveSimpleName() + "Value";
   }

   private static String boxingDescriptor(Wrapper var0) {
      return String.format("(%s)L%s;", var0.basicTypeChar(), wrapperName(var0));
   }

   private static String unboxingDescriptor(Wrapper var0) {
      return "()" + var0.basicTypeChar();
   }

   void boxIfTypePrimitive(Type var1) {
      Wrapper var2 = FROM_TYPE_SORT[var1.getSort()];
      if (var2 != null) {
         this.box(var2);
      }

   }

   void widen(Wrapper var1, Wrapper var2) {
      if (var1 != var2) {
         int var3 = wideningOpcodes[var1.ordinal()][var2.ordinal()];
         if (var3 != 0) {
            this.visitInsn(var3);
         }
      }

   }

   void box(Wrapper var1) {
      this.visitMethodInsn(184, wrapperName(var1), "valueOf", boxingDescriptor(var1), false);
   }

   void unbox(String var1, Wrapper var2) {
      this.visitMethodInsn(182, var1, unboxMethod(var2), unboxingDescriptor(var2), false);
   }

   private String descriptorToName(String var1) {
      int var2 = var1.length() - 1;
      return var1.charAt(0) == 'L' && var1.charAt(var2) == ';' ? var1.substring(1, var2) : var1;
   }

   void cast(String var1, String var2) {
      String var3 = this.descriptorToName(var1);
      String var4 = this.descriptorToName(var2);
      if (!var4.equals(var3) && !var4.equals("java/lang/Object")) {
         this.visitTypeInsn(192, var4);
      }

   }

   private boolean isPrimitive(Wrapper var1) {
      return var1 != Wrapper.OBJECT;
   }

   private Wrapper toWrapper(String var1) {
      char var2 = var1.charAt(0);
      if (var2 == '[' || var2 == '(') {
         var2 = 'L';
      }

      return Wrapper.forBasicType(var2);
   }

   void convertType(Class<?> var1, Class<?> var2, Class<?> var3) {
      if (!var1.equals(var2) || !var1.equals(var3)) {
         if (var1 != Void.TYPE && var2 != Void.TYPE) {
            String var5;
            if (var1.isPrimitive()) {
               Wrapper var4 = Wrapper.forPrimitiveType(var1);
               if (var2.isPrimitive()) {
                  this.widen(var4, Wrapper.forPrimitiveType(var2));
               } else {
                  var5 = BytecodeDescriptor.unparse(var2);
                  Wrapper var6 = this.wrapperOrNullFromDescriptor(var5);
                  if (var6 != null) {
                     this.widen(var4, var6);
                     this.box(var6);
                  } else {
                     this.box(var4);
                     this.cast(wrapperName(var4), var5);
                  }
               }
            } else {
               String var10 = BytecodeDescriptor.unparse(var1);
               if (var3.isPrimitive()) {
                  var5 = var10;
               } else {
                  var5 = BytecodeDescriptor.unparse(var3);
                  this.cast(var10, var5);
               }

               String var11 = BytecodeDescriptor.unparse(var2);
               if (var2.isPrimitive()) {
                  Wrapper var7 = this.toWrapper(var11);
                  Wrapper var8 = this.wrapperOrNullFromDescriptor(var5);
                  if (var8 != null) {
                     if (!var8.isSigned() && !var8.isFloating()) {
                        this.unbox(wrapperName(var8), var8);
                        this.widen(var8, var7);
                     } else {
                        this.unbox(wrapperName(var8), var7);
                     }
                  } else {
                     String var9;
                     if (!var7.isSigned() && !var7.isFloating()) {
                        var9 = wrapperName(var7);
                     } else {
                        var9 = "java/lang/Number";
                     }

                     this.cast(var5, var9);
                     this.unbox(var9, var7);
                  }
               } else {
                  this.cast(var5, var11);
               }
            }

         }
      }
   }

   void iconst(int var1) {
      if (var1 >= -1 && var1 <= 5) {
         this.mv.visitInsn(3 + var1);
      } else if (var1 >= -128 && var1 <= 127) {
         this.mv.visitIntInsn(16, var1);
      } else if (var1 >= -32768 && var1 <= 32767) {
         this.mv.visitIntInsn(17, var1);
      } else {
         this.mv.visitLdcInsn(var1);
      }

   }

   static {
      wideningOpcodes = new int[NUM_WRAPPERS][NUM_WRAPPERS];
      FROM_WRAPPER_NAME = new Wrapper[16];
      FROM_TYPE_SORT = new Wrapper[16];
      Wrapper[] var0 = Wrapper.values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         Wrapper var3 = var0[var2];
         if (var3.basicTypeChar() != 'L') {
            int var4 = hashWrapperName(var3.wrapperSimpleName());

            assert FROM_WRAPPER_NAME[var4] == null;

            FROM_WRAPPER_NAME[var4] = var3;
         }
      }

      for(int var5 = 0; var5 < NUM_WRAPPERS; ++var5) {
         for(var1 = 0; var1 < NUM_WRAPPERS; ++var1) {
            wideningOpcodes[var5][var1] = 0;
         }
      }

      initWidening(Wrapper.LONG, 133, Wrapper.BYTE, Wrapper.SHORT, Wrapper.INT, Wrapper.CHAR);
      initWidening(Wrapper.LONG, 140, Wrapper.FLOAT);
      initWidening(Wrapper.FLOAT, 134, Wrapper.BYTE, Wrapper.SHORT, Wrapper.INT, Wrapper.CHAR);
      initWidening(Wrapper.FLOAT, 137, Wrapper.LONG);
      initWidening(Wrapper.DOUBLE, 135, Wrapper.BYTE, Wrapper.SHORT, Wrapper.INT, Wrapper.CHAR);
      initWidening(Wrapper.DOUBLE, 141, Wrapper.FLOAT);
      initWidening(Wrapper.DOUBLE, 138, Wrapper.LONG);
      FROM_TYPE_SORT[3] = Wrapper.BYTE;
      FROM_TYPE_SORT[4] = Wrapper.SHORT;
      FROM_TYPE_SORT[5] = Wrapper.INT;
      FROM_TYPE_SORT[7] = Wrapper.LONG;
      FROM_TYPE_SORT[2] = Wrapper.CHAR;
      FROM_TYPE_SORT[6] = Wrapper.FLOAT;
      FROM_TYPE_SORT[8] = Wrapper.DOUBLE;
      FROM_TYPE_SORT[1] = Wrapper.BOOLEAN;
   }
}
