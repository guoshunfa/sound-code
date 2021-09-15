package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import sun.misc.SharedSecrets;
import sun.reflect.CallerSensitive;
import sun.reflect.ConstructorAccessor;
import sun.reflect.Reflection;
import sun.reflect.annotation.TypeAnnotation;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.ConstructorRepository;
import sun.reflect.generics.scope.ConstructorScope;

public final class Constructor<T> extends Executable {
   private Class<T> clazz;
   private int slot;
   private Class<?>[] parameterTypes;
   private Class<?>[] exceptionTypes;
   private int modifiers;
   private transient String signature;
   private transient ConstructorRepository genericInfo;
   private byte[] annotations;
   private byte[] parameterAnnotations;
   private volatile ConstructorAccessor constructorAccessor;
   private Constructor<T> root;

   private GenericsFactory getFactory() {
      return CoreReflectionFactory.make(this, ConstructorScope.make(this));
   }

   ConstructorRepository getGenericInfo() {
      if (this.genericInfo == null) {
         this.genericInfo = ConstructorRepository.make(this.getSignature(), this.getFactory());
      }

      return this.genericInfo;
   }

   Executable getRoot() {
      return this.root;
   }

   Constructor(Class<T> var1, Class<?>[] var2, Class<?>[] var3, int var4, int var5, String var6, byte[] var7, byte[] var8) {
      this.clazz = var1;
      this.parameterTypes = var2;
      this.exceptionTypes = var3;
      this.modifiers = var4;
      this.slot = var5;
      this.signature = var6;
      this.annotations = var7;
      this.parameterAnnotations = var8;
   }

   Constructor<T> copy() {
      if (this.root != null) {
         throw new IllegalArgumentException("Can not copy a non-root Constructor");
      } else {
         Constructor var1 = new Constructor(this.clazz, this.parameterTypes, this.exceptionTypes, this.modifiers, this.slot, this.signature, this.annotations, this.parameterAnnotations);
         var1.root = this;
         var1.constructorAccessor = this.constructorAccessor;
         return var1;
      }
   }

   boolean hasGenericInformation() {
      return this.getSignature() != null;
   }

   byte[] getAnnotationBytes() {
      return this.annotations;
   }

   public Class<T> getDeclaringClass() {
      return this.clazz;
   }

   public String getName() {
      return this.getDeclaringClass().getName();
   }

   public int getModifiers() {
      return this.modifiers;
   }

   public TypeVariable<Constructor<T>>[] getTypeParameters() {
      return this.getSignature() != null ? (TypeVariable[])this.getGenericInfo().getTypeParameters() : (TypeVariable[])(new TypeVariable[0]);
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
      if (var1 != null && var1 instanceof Constructor) {
         Constructor var2 = (Constructor)var1;
         if (this.getDeclaringClass() == var2.getDeclaringClass()) {
            return this.equalParamTypes(this.parameterTypes, var2.parameterTypes);
         }
      }

      return false;
   }

   public int hashCode() {
      return this.getDeclaringClass().getName().hashCode();
   }

   public String toString() {
      return this.sharedToString(Modifier.constructorModifiers(), false, this.parameterTypes, this.exceptionTypes);
   }

   void specificToStringHeader(StringBuilder var1) {
      var1.append(this.getDeclaringClass().getTypeName());
   }

   public String toGenericString() {
      return this.sharedToGenericString(Modifier.constructorModifiers(), false);
   }

   void specificToGenericStringHeader(StringBuilder var1) {
      this.specificToStringHeader(var1);
   }

   @CallerSensitive
   public T newInstance(Object... var1) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
         Class var2 = Reflection.getCallerClass();
         this.checkAccess(var2, this.clazz, (Object)null, this.modifiers);
      }

      if ((this.clazz.getModifiers() & 16384) != 0) {
         throw new IllegalArgumentException("Cannot reflectively create enum objects");
      } else {
         ConstructorAccessor var4 = this.constructorAccessor;
         if (var4 == null) {
            var4 = this.acquireConstructorAccessor();
         }

         Object var3 = var4.newInstance(var1);
         return var3;
      }
   }

   public boolean isVarArgs() {
      return super.isVarArgs();
   }

   public boolean isSynthetic() {
      return super.isSynthetic();
   }

   private ConstructorAccessor acquireConstructorAccessor() {
      ConstructorAccessor var1 = null;
      if (this.root != null) {
         var1 = this.root.getConstructorAccessor();
      }

      if (var1 != null) {
         this.constructorAccessor = var1;
      } else {
         var1 = reflectionFactory.newConstructorAccessor(this);
         this.setConstructorAccessor(var1);
      }

      return var1;
   }

   ConstructorAccessor getConstructorAccessor() {
      return this.constructorAccessor;
   }

   void setConstructorAccessor(ConstructorAccessor var1) {
      this.constructorAccessor = var1;
      if (this.root != null) {
         this.root.setConstructorAccessor(var1);
      }

   }

   int getSlot() {
      return this.slot;
   }

   String getSignature() {
      return this.signature;
   }

   byte[] getRawAnnotations() {
      return this.annotations;
   }

   byte[] getRawParameterAnnotations() {
      return this.parameterAnnotations;
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

   void handleParameterNumberMismatch(int var1, int var2) {
      Class var3 = this.getDeclaringClass();
      if (!var3.isEnum() && !var3.isAnonymousClass() && !var3.isLocalClass()) {
         if (!var3.isMemberClass() || var3.isMemberClass() && (var3.getModifiers() & 8) == 0 && var1 + 1 != var2) {
            throw new AnnotationFormatError("Parameter annotations don't match number of parameters");
         }
      }
   }

   public AnnotatedType getAnnotatedReturnType() {
      return this.getAnnotatedReturnType0(this.getDeclaringClass());
   }

   public AnnotatedType getAnnotatedReceiverType() {
      return this.getDeclaringClass().getEnclosingClass() == null ? super.getAnnotatedReceiverType() : TypeAnnotationParser.buildAnnotatedType(this.getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(this.getDeclaringClass()), this, this.getDeclaringClass(), this.getDeclaringClass().getEnclosingClass(), TypeAnnotation.TypeAnnotationTarget.METHOD_RECEIVER);
   }
}
