package java.lang.invoke;

import java.io.FilePermission;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedHashSet;
import java.util.PropertyPermission;
import java.util.concurrent.atomic.AtomicInteger;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import sun.invoke.util.BytecodeDescriptor;
import sun.misc.Unsafe;
import sun.security.action.GetPropertyAction;

final class InnerClassLambdaMetafactory extends AbstractValidatingLambdaMetafactory {
   private static final Unsafe UNSAFE = Unsafe.getUnsafe();
   private static final int CLASSFILE_VERSION = 52;
   private static final String METHOD_DESCRIPTOR_VOID;
   private static final String JAVA_LANG_OBJECT = "java/lang/Object";
   private static final String NAME_CTOR = "<init>";
   private static final String NAME_FACTORY = "get$Lambda";
   private static final String NAME_SERIALIZED_LAMBDA = "java/lang/invoke/SerializedLambda";
   private static final String NAME_NOT_SERIALIZABLE_EXCEPTION = "java/io/NotSerializableException";
   private static final String DESCR_METHOD_WRITE_REPLACE = "()Ljava/lang/Object;";
   private static final String DESCR_METHOD_WRITE_OBJECT = "(Ljava/io/ObjectOutputStream;)V";
   private static final String DESCR_METHOD_READ_OBJECT = "(Ljava/io/ObjectInputStream;)V";
   private static final String NAME_METHOD_WRITE_REPLACE = "writeReplace";
   private static final String NAME_METHOD_READ_OBJECT = "readObject";
   private static final String NAME_METHOD_WRITE_OBJECT = "writeObject";
   private static final String DESCR_CTOR_SERIALIZED_LAMBDA;
   private static final String DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION;
   private static final String[] SER_HOSTILE_EXCEPTIONS;
   private static final String[] EMPTY_STRING_ARRAY;
   private static final AtomicInteger counter;
   private static final ProxyClassesDumper dumper;
   private final String implMethodClassName;
   private final String implMethodName;
   private final String implMethodDesc;
   private final Class<?> implMethodReturnClass;
   private final MethodType constructorType;
   private final ClassWriter cw;
   private final String[] argNames;
   private final String[] argDescs;
   private final String lambdaClassName;

   public InnerClassLambdaMetafactory(MethodHandles.Lookup var1, MethodType var2, String var3, MethodType var4, MethodHandle var5, MethodType var6, boolean var7, Class<?>[] var8, MethodType[] var9) throws LambdaConversionException {
      super(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      this.implMethodClassName = this.implDefiningClass.getName().replace('.', '/');
      this.implMethodName = this.implInfo.getName();
      this.implMethodDesc = this.implMethodType.toMethodDescriptorString();
      this.implMethodReturnClass = this.implKind == 8 ? this.implDefiningClass : this.implMethodType.returnType();
      this.constructorType = var2.changeReturnType(Void.TYPE);
      this.lambdaClassName = this.targetClass.getName().replace('.', '/') + "$$Lambda$" + counter.incrementAndGet();
      this.cw = new ClassWriter(1);
      int var10 = var2.parameterCount();
      if (var10 > 0) {
         this.argNames = new String[var10];
         this.argDescs = new String[var10];

         for(int var11 = 0; var11 < var10; ++var11) {
            this.argNames[var11] = "arg$" + (var11 + 1);
            this.argDescs[var11] = BytecodeDescriptor.unparse(var2.parameterType(var11));
         }
      } else {
         this.argNames = this.argDescs = EMPTY_STRING_ARRAY;
      }

   }

   CallSite buildCallSite() throws LambdaConversionException {
      final Class var1 = this.spinInnerClass();
      if (this.invokedType.parameterCount() == 0) {
         Constructor[] var2 = (Constructor[])AccessController.doPrivileged(new PrivilegedAction<Constructor<?>[]>() {
            public Constructor<?>[] run() {
               Constructor[] var1x = var1.getDeclaredConstructors();
               if (var1x.length == 1) {
                  var1x[0].setAccessible(true);
               }

               return var1x;
            }
         });
         if (var2.length != 1) {
            throw new LambdaConversionException("Expected one lambda constructor for " + var1.getCanonicalName() + ", got " + var2.length);
         } else {
            try {
               Object var3 = var2[0].newInstance();
               return new ConstantCallSite(MethodHandles.constant(this.samBase, var3));
            } catch (ReflectiveOperationException var4) {
               throw new LambdaConversionException("Exception instantiating lambda object", var4);
            }
         }
      } else {
         try {
            UNSAFE.ensureClassInitialized(var1);
            return new ConstantCallSite(MethodHandles.Lookup.IMPL_LOOKUP.findStatic(var1, "get$Lambda", this.invokedType));
         } catch (ReflectiveOperationException var5) {
            throw new LambdaConversionException("Exception finding constructor", var5);
         }
      }
   }

   private Class<?> spinInnerClass() throws LambdaConversionException {
      String var2 = this.samBase.getName().replace('.', '/');
      boolean var3 = !this.isSerializable && Serializable.class.isAssignableFrom(this.samBase);
      String[] var1;
      int var6;
      int var7;
      if (this.markerInterfaces.length == 0) {
         var1 = new String[]{var2};
      } else {
         LinkedHashSet var4 = new LinkedHashSet(this.markerInterfaces.length + 1);
         var4.add(var2);
         Class[] var5 = this.markerInterfaces;
         var6 = var5.length;

         for(var7 = 0; var7 < var6; ++var7) {
            Class var8 = var5[var7];
            var4.add(var8.getName().replace('.', '/'));
            var3 |= !this.isSerializable && Serializable.class.isAssignableFrom(var8);
         }

         var1 = (String[])var4.toArray(new String[var4.size()]);
      }

      this.cw.visit(52, 4144, this.lambdaClassName, (String)null, "java/lang/Object", var1);

      for(int var9 = 0; var9 < this.argDescs.length; ++var9) {
         FieldVisitor var11 = this.cw.visitField(18, this.argNames[var9], this.argDescs[var9], (String)null, (Object)null);
         var11.visitEnd();
      }

      this.generateConstructor();
      if (this.invokedType.parameterCount() != 0) {
         this.generateFactory();
      }

      MethodVisitor var10 = this.cw.visitMethod(1, this.samMethodName, this.samMethodType.toMethodDescriptorString(), (String)null, (String[])null);
      var10.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
      (new InnerClassLambdaMetafactory.ForwardingMethodGenerator(var10)).generate(this.samMethodType);
      if (this.additionalBridges != null) {
         MethodType[] var12 = this.additionalBridges;
         var6 = var12.length;

         for(var7 = 0; var7 < var6; ++var7) {
            MethodType var14 = var12[var7];
            var10 = this.cw.visitMethod(65, this.samMethodName, var14.toMethodDescriptorString(), (String)null, (String[])null);
            var10.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
            (new InnerClassLambdaMetafactory.ForwardingMethodGenerator(var10)).generate(var14);
         }
      }

      if (this.isSerializable) {
         this.generateSerializationFriendlyMethods();
      } else if (var3) {
         this.generateSerializationHostileMethods();
      }

      this.cw.visitEnd();
      final byte[] var13 = this.cw.toByteArray();
      if (dumper != null) {
         AccessController.doPrivileged((PrivilegedAction)(new PrivilegedAction<Void>() {
            public Void run() {
               InnerClassLambdaMetafactory.dumper.dumpClass(InnerClassLambdaMetafactory.this.lambdaClassName, var13);
               return null;
            }
         }), (AccessControlContext)null, new FilePermission("<<ALL FILES>>", "read, write"), new PropertyPermission("user.dir", "read"));
      }

      return UNSAFE.defineAnonymousClass(this.targetClass, var13, (Object[])null);
   }

   private void generateFactory() {
      MethodVisitor var1 = this.cw.visitMethod(10, "get$Lambda", this.invokedType.toMethodDescriptorString(), (String)null, (String[])null);
      var1.visitCode();
      var1.visitTypeInsn(187, this.lambdaClassName);
      var1.visitInsn(89);
      int var2 = this.invokedType.parameterCount();
      int var3 = 0;

      for(int var4 = 0; var3 < var2; ++var3) {
         Class var5 = this.invokedType.parameterType(var3);
         var1.visitVarInsn(getLoadOpcode(var5), var4);
         var4 += getParameterSize(var5);
      }

      var1.visitMethodInsn(183, this.lambdaClassName, "<init>", this.constructorType.toMethodDescriptorString(), false);
      var1.visitInsn(176);
      var1.visitMaxs(-1, -1);
      var1.visitEnd();
   }

   private void generateConstructor() {
      MethodVisitor var1 = this.cw.visitMethod(2, "<init>", this.constructorType.toMethodDescriptorString(), (String)null, (String[])null);
      var1.visitCode();
      var1.visitVarInsn(25, 0);
      var1.visitMethodInsn(183, "java/lang/Object", "<init>", METHOD_DESCRIPTOR_VOID, false);
      int var2 = this.invokedType.parameterCount();
      int var3 = 0;

      for(int var4 = 0; var3 < var2; ++var3) {
         var1.visitVarInsn(25, 0);
         Class var5 = this.invokedType.parameterType(var3);
         var1.visitVarInsn(getLoadOpcode(var5), var4 + 1);
         var4 += getParameterSize(var5);
         var1.visitFieldInsn(181, this.lambdaClassName, this.argNames[var3], this.argDescs[var3]);
      }

      var1.visitInsn(177);
      var1.visitMaxs(-1, -1);
      var1.visitEnd();
   }

   private void generateSerializationFriendlyMethods() {
      TypeConvertingMethodAdapter var1 = new TypeConvertingMethodAdapter(this.cw.visitMethod(18, "writeReplace", "()Ljava/lang/Object;", (String)null, (String[])null));
      var1.visitCode();
      var1.visitTypeInsn(187, "java/lang/invoke/SerializedLambda");
      var1.visitInsn(89);
      var1.visitLdcInsn(Type.getType(this.targetClass));
      var1.visitLdcInsn(this.invokedType.returnType().getName().replace('.', '/'));
      var1.visitLdcInsn(this.samMethodName);
      var1.visitLdcInsn(this.samMethodType.toMethodDescriptorString());
      var1.visitLdcInsn(this.implInfo.getReferenceKind());
      var1.visitLdcInsn(this.implInfo.getDeclaringClass().getName().replace('.', '/'));
      var1.visitLdcInsn(this.implInfo.getName());
      var1.visitLdcInsn(this.implInfo.getMethodType().toMethodDescriptorString());
      var1.visitLdcInsn(this.instantiatedMethodType.toMethodDescriptorString());
      var1.iconst(this.argDescs.length);
      var1.visitTypeInsn(189, "java/lang/Object");

      for(int var2 = 0; var2 < this.argDescs.length; ++var2) {
         var1.visitInsn(89);
         var1.iconst(var2);
         var1.visitVarInsn(25, 0);
         var1.visitFieldInsn(180, this.lambdaClassName, this.argNames[var2], this.argDescs[var2]);
         var1.boxIfTypePrimitive(Type.getType(this.argDescs[var2]));
         var1.visitInsn(83);
      }

      var1.visitMethodInsn(183, "java/lang/invoke/SerializedLambda", "<init>", DESCR_CTOR_SERIALIZED_LAMBDA, false);
      var1.visitInsn(176);
      var1.visitMaxs(-1, -1);
      var1.visitEnd();
   }

   private void generateSerializationHostileMethods() {
      MethodVisitor var1 = this.cw.visitMethod(18, "writeObject", "(Ljava/io/ObjectOutputStream;)V", (String)null, SER_HOSTILE_EXCEPTIONS);
      var1.visitCode();
      var1.visitTypeInsn(187, "java/io/NotSerializableException");
      var1.visitInsn(89);
      var1.visitLdcInsn("Non-serializable lambda");
      var1.visitMethodInsn(183, "java/io/NotSerializableException", "<init>", DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION, false);
      var1.visitInsn(191);
      var1.visitMaxs(-1, -1);
      var1.visitEnd();
      var1 = this.cw.visitMethod(18, "readObject", "(Ljava/io/ObjectInputStream;)V", (String)null, SER_HOSTILE_EXCEPTIONS);
      var1.visitCode();
      var1.visitTypeInsn(187, "java/io/NotSerializableException");
      var1.visitInsn(89);
      var1.visitLdcInsn("Non-serializable lambda");
      var1.visitMethodInsn(183, "java/io/NotSerializableException", "<init>", DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION, false);
      var1.visitInsn(191);
      var1.visitMaxs(-1, -1);
      var1.visitEnd();
   }

   static int getParameterSize(Class<?> var0) {
      if (var0 == Void.TYPE) {
         return 0;
      } else {
         return var0 != Long.TYPE && var0 != Double.TYPE ? 1 : 2;
      }
   }

   static int getLoadOpcode(Class<?> var0) {
      if (var0 == Void.TYPE) {
         throw new InternalError("Unexpected void type of load opcode");
      } else {
         return 21 + getOpcodeOffset(var0);
      }
   }

   static int getReturnOpcode(Class<?> var0) {
      return var0 == Void.TYPE ? 177 : 172 + getOpcodeOffset(var0);
   }

   private static int getOpcodeOffset(Class<?> var0) {
      if (var0.isPrimitive()) {
         if (var0 == Long.TYPE) {
            return 1;
         } else if (var0 == Float.TYPE) {
            return 2;
         } else {
            return var0 == Double.TYPE ? 3 : 0;
         }
      } else {
         return 4;
      }
   }

   static {
      METHOD_DESCRIPTOR_VOID = Type.getMethodDescriptor(Type.VOID_TYPE);
      DESCR_CTOR_SERIALIZED_LAMBDA = MethodType.methodType(Void.TYPE, Class.class, String.class, String.class, String.class, Integer.TYPE, String.class, String.class, String.class, String.class, Object[].class).toMethodDescriptorString();
      DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION = MethodType.methodType(Void.TYPE, String.class).toMethodDescriptorString();
      SER_HOSTILE_EXCEPTIONS = new String[]{"java/io/NotSerializableException"};
      EMPTY_STRING_ARRAY = new String[0];
      counter = new AtomicInteger(0);
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("jdk.internal.lambda.dumpProxyClasses")), (AccessControlContext)null, new PropertyPermission("jdk.internal.lambda.dumpProxyClasses", "read"));
      dumper = null == var1 ? null : ProxyClassesDumper.getInstance(var1);
   }

   private class ForwardingMethodGenerator extends TypeConvertingMethodAdapter {
      ForwardingMethodGenerator(MethodVisitor var2) {
         super(var2);
      }

      void generate(MethodType var1) {
         this.visitCode();
         if (InnerClassLambdaMetafactory.this.implKind == 8) {
            this.visitTypeInsn(187, InnerClassLambdaMetafactory.this.implMethodClassName);
            this.visitInsn(89);
         }

         for(int var2 = 0; var2 < InnerClassLambdaMetafactory.this.argNames.length; ++var2) {
            this.visitVarInsn(25, 0);
            this.visitFieldInsn(180, InnerClassLambdaMetafactory.this.lambdaClassName, InnerClassLambdaMetafactory.this.argNames[var2], InnerClassLambdaMetafactory.this.argDescs[var2]);
         }

         this.convertArgumentTypes(var1);
         this.visitMethodInsn(this.invocationOpcode(), InnerClassLambdaMetafactory.this.implMethodClassName, InnerClassLambdaMetafactory.this.implMethodName, InnerClassLambdaMetafactory.this.implMethodDesc, InnerClassLambdaMetafactory.this.implDefiningClass.isInterface());
         Class var3 = var1.returnType();
         this.convertType(InnerClassLambdaMetafactory.this.implMethodReturnClass, var3, var3);
         this.visitInsn(InnerClassLambdaMetafactory.getReturnOpcode(var3));
         this.visitMaxs(-1, -1);
         this.visitEnd();
      }

      private void convertArgumentTypes(MethodType var1) {
         int var2 = 0;
         boolean var3 = InnerClassLambdaMetafactory.this.implIsInstanceMethod && InnerClassLambdaMetafactory.this.invokedType.parameterCount() == 0;
         int var4 = var3 ? 1 : 0;
         if (var3) {
            Class var5 = var1.parameterType(0);
            this.visitVarInsn(InnerClassLambdaMetafactory.getLoadOpcode(var5), var2 + 1);
            var2 += InnerClassLambdaMetafactory.getParameterSize(var5);
            this.convertType(var5, InnerClassLambdaMetafactory.this.implDefiningClass, InnerClassLambdaMetafactory.this.instantiatedMethodType.parameterType(0));
         }

         int var9 = var1.parameterCount();
         int var6 = InnerClassLambdaMetafactory.this.implMethodType.parameterCount() - var9;

         for(int var7 = var4; var7 < var9; ++var7) {
            Class var8 = var1.parameterType(var7);
            this.visitVarInsn(InnerClassLambdaMetafactory.getLoadOpcode(var8), var2 + 1);
            var2 += InnerClassLambdaMetafactory.getParameterSize(var8);
            this.convertType(var8, InnerClassLambdaMetafactory.this.implMethodType.parameterType(var6 + var7), InnerClassLambdaMetafactory.this.instantiatedMethodType.parameterType(var7));
         }

      }

      private int invocationOpcode() throws InternalError {
         switch(InnerClassLambdaMetafactory.this.implKind) {
         case 5:
            return 182;
         case 6:
            return 184;
         case 7:
            return 183;
         case 8:
            return 183;
         case 9:
            return 185;
         default:
            throw new InternalError("Unexpected invocation kind: " + InnerClassLambdaMetafactory.this.implKind);
         }
      }
   }
}
