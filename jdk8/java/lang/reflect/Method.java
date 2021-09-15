package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.nio.ByteBuffer;
import sun.misc.SharedSecrets;
import sun.reflect.CallerSensitive;
import sun.reflect.MethodAccessor;
import sun.reflect.Reflection;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationType;
import sun.reflect.annotation.ExceptionProxy;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.MethodRepository;
import sun.reflect.generics.scope.MethodScope;

public final class Method extends Executable {
   private Class<?> clazz;
   private int slot;
   private String name;
   private Class<?> returnType;
   private Class<?>[] parameterTypes;
   private Class<?>[] exceptionTypes;
   private int modifiers;
   private transient String signature;
   private transient MethodRepository genericInfo;
   private byte[] annotations;
   private byte[] parameterAnnotations;
   private byte[] annotationDefault;
   private volatile MethodAccessor methodAccessor;
   private Method root;

   private String getGenericSignature() {
      return this.signature;
   }

   private GenericsFactory getFactory() {
      return CoreReflectionFactory.make(this, MethodScope.make(this));
   }

   MethodRepository getGenericInfo() {
      if (this.genericInfo == null) {
         this.genericInfo = MethodRepository.make(this.getGenericSignature(), this.getFactory());
      }

      return this.genericInfo;
   }

   Method(Class<?> var1, String var2, Class<?>[] var3, Class<?> var4, Class<?>[] var5, int var6, int var7, String var8, byte[] var9, byte[] var10, byte[] var11) {
      this.clazz = var1;
      this.name = var2;
      this.parameterTypes = var3;
      this.returnType = var4;
      this.exceptionTypes = var5;
      this.modifiers = var6;
      this.slot = var7;
      this.signature = var8;
      this.annotations = var9;
      this.parameterAnnotations = var10;
      this.annotationDefault = var11;
   }

   Method copy() {
      if (this.root != null) {
         throw new IllegalArgumentException("Can not copy a non-root Method");
      } else {
         Method var1 = new Method(this.clazz, this.name, this.parameterTypes, this.returnType, this.exceptionTypes, this.modifiers, this.slot, this.signature, this.annotations, this.parameterAnnotations, this.annotationDefault);
         var1.root = this;
         var1.methodAccessor = this.methodAccessor;
         return var1;
      }
   }

   Executable getRoot() {
      return this.root;
   }

   boolean hasGenericInformation() {
      return this.getGenericSignature() != null;
   }

   byte[] getAnnotationBytes() {
      return this.annotations;
   }

   public Class<?> getDeclaringClass() {
      return this.clazz;
   }

   public String getName() {
      return this.name;
   }

   public int getModifiers() {
      return this.modifiers;
   }

   public TypeVariable<Method>[] getTypeParameters() {
      return this.getGenericSignature() != null ? (TypeVariable[])this.getGenericInfo().getTypeParameters() : (TypeVariable[])(new TypeVariable[0]);
   }

   public Class<?> getReturnType() {
      return this.returnType;
   }

   public Type getGenericReturnType() {
      return (Type)(this.getGenericSignature() != null ? this.getGenericInfo().getReturnType() : this.getReturnType());
   }

   public Class<?>[] getParameterTypes() {
      return (Class[])this.parameterTypes.clone();
   }

   public int getParameterCount() {
      return this.parameterTypes.length;
   }

   public Type[] getGenericParameterTypes() {
      return super.getGenericParameterTypes();
   }

   public Class<?>[] getExceptionTypes() {
      return (Class[])this.exceptionTypes.clone();
   }

   public Type[] getGenericExceptionTypes() {
      return super.getGenericExceptionTypes();
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof Method) {
         Method var2 = (Method)var1;
         if (this.getDeclaringClass() == var2.getDeclaringClass() && this.getName() == var2.getName()) {
            if (!this.returnType.equals(var2.getReturnType())) {
               return false;
            }

            return this.equalParamTypes(this.parameterTypes, var2.parameterTypes);
         }
      }

      return false;
   }

   public int hashCode() {
      return this.getDeclaringClass().getName().hashCode() ^ this.getName().hashCode();
   }

   public String toString() {
      return this.sharedToString(Modifier.methodModifiers(), this.isDefault(), this.parameterTypes, this.exceptionTypes);
   }

   void specificToStringHeader(StringBuilder var1) {
      var1.append(this.getReturnType().getTypeName()).append(' ');
      var1.append(this.getDeclaringClass().getTypeName()).append('.');
      var1.append(this.getName());
   }

   public String toGenericString() {
      return this.sharedToGenericString(Modifier.methodModifiers(), this.isDefault());
   }

   void specificToGenericStringHeader(StringBuilder var1) {
      Type var2 = this.getGenericReturnType();
      var1.append(var2.getTypeName()).append(' ');
      var1.append(this.getDeclaringClass().getTypeName()).append('.');
      var1.append(this.getName());
   }

   @CallerSensitive
   public Object invoke(Object var1, Object... var2) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var3 = Reflection.getCallerClass();
         this.checkAccess(var3, this.clazz, var1, this.modifiers);
      }

      MethodAccessor var4 = this.methodAccessor;
      if (var4 == null) {
         var4 = this.acquireMethodAccessor();
      }

      return var4.invoke(var1, var2);
   }

   public boolean isBridge() {
      return (this.getModifiers() & 64) != 0;
   }

   public boolean isVarArgs() {
      return super.isVarArgs();
   }

   public boolean isSynthetic() {
      return super.isSynthetic();
   }

   public boolean isDefault() {
      return (this.getModifiers() & 1033) == 1 && this.getDeclaringClass().isInterface();
   }

   private MethodAccessor acquireMethodAccessor() {
      MethodAccessor var1 = null;
      if (this.root != null) {
         var1 = this.root.getMethodAccessor();
      }

      if (var1 != null) {
         this.methodAccessor = var1;
      } else {
         var1 = reflectionFactory.newMethodAccessor(this);
         this.setMethodAccessor(var1);
      }

      return var1;
   }

   MethodAccessor getMethodAccessor() {
      return this.methodAccessor;
   }

   void setMethodAccessor(MethodAccessor var1) {
      this.methodAccessor = var1;
      if (this.root != null) {
         this.root.setMethodAccessor(var1);
      }

   }

   public Object getDefaultValue() {
      if (this.annotationDefault == null) {
         return null;
      } else {
         Class var1 = AnnotationType.invocationHandlerReturnType(this.getReturnType());
         Object var2 = AnnotationParser.parseMemberValue(var1, ByteBuffer.wrap(this.annotationDefault), SharedSecrets.getJavaLangAccess().getConstantPool(this.getDeclaringClass()), this.getDeclaringClass());
         if (var2 instanceof ExceptionProxy) {
            throw new AnnotationFormatError("Invalid default: " + this);
         } else {
            return var2;
         }
      }
   }

   public <T extends Annotation> T getAnnotation(Class<T> var1) {
      return super.getAnnotation(var1);
   }

   public Annotation[] getDeclaredAnnotations() {
      return super.getDeclaredAnnotations();
   }

   public Annotation[][] getParameterAnnotations() {
      return this.sharedGetParameterAnnotations(this.parameterTypes, this.parameterAnnotations);
   }

   public AnnotatedType getAnnotatedReturnType() {
      return this.getAnnotatedReturnType0(this.getGenericReturnType());
   }

   void handleParameterNumberMismatch(int var1, int var2) {
      throw new AnnotationFormatError("Parameter annotations don't match number of parameters");
   }
}
