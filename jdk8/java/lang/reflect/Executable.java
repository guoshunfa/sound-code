package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;
import sun.misc.SharedSecrets;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.TypeAnnotation;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.repository.ConstructorRepository;

public abstract class Executable extends AccessibleObject implements Member, GenericDeclaration {
   private transient volatile boolean hasRealParameterData;
   private transient volatile Parameter[] parameters;
   private transient Map<Class<? extends Annotation>, Annotation> declaredAnnotations;

   Executable() {
   }

   abstract byte[] getAnnotationBytes();

   abstract Executable getRoot();

   abstract boolean hasGenericInformation();

   abstract ConstructorRepository getGenericInfo();

   boolean equalParamTypes(Class<?>[] var1, Class<?>[] var2) {
      if (var1.length == var2.length) {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3] != var2[var3]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   Annotation[][] parseParameterAnnotations(byte[] var1) {
      return AnnotationParser.parseParameterAnnotations(var1, SharedSecrets.getJavaLangAccess().getConstantPool(this.getDeclaringClass()), this.getDeclaringClass());
   }

   void separateWithCommas(Class<?>[] var1, StringBuilder var2) {
      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2.append(var1[var3].getTypeName());
         if (var3 < var1.length - 1) {
            var2.append(",");
         }
      }

   }

   void printModifiersIfNonzero(StringBuilder var1, int var2, boolean var3) {
      int var4 = this.getModifiers() & var2;
      if (var4 != 0 && !var3) {
         var1.append(Modifier.toString(var4)).append(' ');
      } else {
         int var5 = var4 & 7;
         if (var5 != 0) {
            var1.append(Modifier.toString(var5)).append(' ');
         }

         if (var3) {
            var1.append("default ");
         }

         var4 &= -8;
         if (var4 != 0) {
            var1.append(Modifier.toString(var4)).append(' ');
         }
      }

   }

   String sharedToString(int var1, boolean var2, Class<?>[] var3, Class<?>[] var4) {
      try {
         StringBuilder var5 = new StringBuilder();
         this.printModifiersIfNonzero(var5, var1, var2);
         this.specificToStringHeader(var5);
         var5.append('(');
         this.separateWithCommas(var3, var5);
         var5.append(')');
         if (var4.length > 0) {
            var5.append(" throws ");
            this.separateWithCommas(var4, var5);
         }

         return var5.toString();
      } catch (Exception var6) {
         return "<" + var6 + ">";
      }
   }

   abstract void specificToStringHeader(StringBuilder var1);

   String sharedToGenericString(int var1, boolean var2) {
      try {
         StringBuilder var3 = new StringBuilder();
         this.printModifiersIfNonzero(var3, var1, var2);
         TypeVariable[] var4 = this.getTypeParameters();
         int var7;
         if (var4.length > 0) {
            boolean var5 = true;
            var3.append('<');
            TypeVariable[] var6 = var4;
            var7 = var4.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               TypeVariable var9 = var6[var8];
               if (!var5) {
                  var3.append(',');
               }

               var3.append(var9.toString());
               var5 = false;
            }

            var3.append("> ");
         }

         this.specificToGenericStringHeader(var3);
         var3.append('(');
         Type[] var11 = this.getGenericParameterTypes();

         for(int var12 = 0; var12 < var11.length; ++var12) {
            String var14 = var11[var12].getTypeName();
            if (this.isVarArgs() && var12 == var11.length - 1) {
               var14 = var14.replaceFirst("\\[\\]$", "...");
            }

            var3.append(var14);
            if (var12 < var11.length - 1) {
               var3.append(',');
            }
         }

         var3.append(')');
         Type[] var13 = this.getGenericExceptionTypes();
         if (var13.length > 0) {
            var3.append(" throws ");

            for(var7 = 0; var7 < var13.length; ++var7) {
               var3.append(var13[var7] instanceof Class ? ((Class)var13[var7]).getName() : var13[var7].toString());
               if (var7 < var13.length - 1) {
                  var3.append(',');
               }
            }
         }

         return var3.toString();
      } catch (Exception var10) {
         return "<" + var10 + ">";
      }
   }

   abstract void specificToGenericStringHeader(StringBuilder var1);

   public abstract Class<?> getDeclaringClass();

   public abstract String getName();

   public abstract int getModifiers();

   public abstract TypeVariable<?>[] getTypeParameters();

   public abstract Class<?>[] getParameterTypes();

   public int getParameterCount() {
      throw new AbstractMethodError();
   }

   public Type[] getGenericParameterTypes() {
      return (Type[])(this.hasGenericInformation() ? this.getGenericInfo().getParameterTypes() : this.getParameterTypes());
   }

   Type[] getAllGenericParameterTypes() {
      boolean var1 = this.hasGenericInformation();
      if (!var1) {
         return this.getParameterTypes();
      } else {
         boolean var2 = this.hasRealParameterData();
         Type[] var3 = this.getGenericParameterTypes();
         Class[] var4 = this.getParameterTypes();
         Type[] var5 = new Type[var4.length];
         Parameter[] var6 = this.getParameters();
         int var7 = 0;
         if (!var2) {
            return (Type[])(var3.length == var4.length ? var3 : var4);
         } else {
            for(int var8 = 0; var8 < var5.length; ++var8) {
               Parameter var9 = var6[var8];
               if (!var9.isSynthetic() && !var9.isImplicit()) {
                  var5[var8] = var3[var7];
                  ++var7;
               } else {
                  var5[var8] = var4[var8];
               }
            }

            return var5;
         }
      }
   }

   public Parameter[] getParameters() {
      return (Parameter[])this.privateGetParameters().clone();
   }

   private Parameter[] synthesizeAllParams() {
      int var1 = this.getParameterCount();
      Parameter[] var2 = new Parameter[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3] = new Parameter("arg" + var3, 0, this, var3);
      }

      return var2;
   }

   private void verifyParameters(Parameter[] var1) {
      if (this.getParameterTypes().length != var1.length) {
         throw new MalformedParametersException("Wrong number of parameters in MethodParameters attribute");
      } else {
         Parameter[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Parameter var6 = var3[var5];
            String var7 = var6.getRealName();
            int var8 = var6.getModifiers();
            if (var7 != null && (var7.isEmpty() || var7.indexOf(46) != -1 || var7.indexOf(59) != -1 || var7.indexOf(91) != -1 || var7.indexOf(47) != -1)) {
               throw new MalformedParametersException("Invalid parameter name \"" + var7 + "\"");
            }

            if (var8 != (var8 & '逐')) {
               throw new MalformedParametersException("Invalid parameter modifiers");
            }
         }

      }
   }

   private Parameter[] privateGetParameters() {
      Parameter[] var1 = this.parameters;
      if (var1 == null) {
         try {
            var1 = this.getParameters0();
         } catch (IllegalArgumentException var3) {
            throw new MalformedParametersException("Invalid constant pool index");
         }

         if (var1 == null) {
            this.hasRealParameterData = false;
            var1 = this.synthesizeAllParams();
         } else {
            this.hasRealParameterData = true;
            this.verifyParameters(var1);
         }

         this.parameters = var1;
      }

      return var1;
   }

   boolean hasRealParameterData() {
      if (this.parameters == null) {
         this.privateGetParameters();
      }

      return this.hasRealParameterData;
   }

   private native Parameter[] getParameters0();

   native byte[] getTypeAnnotationBytes0();

   byte[] getTypeAnnotationBytes() {
      return this.getTypeAnnotationBytes0();
   }

   public abstract Class<?>[] getExceptionTypes();

   public Type[] getGenericExceptionTypes() {
      Type[] var1;
      return (Type[])(this.hasGenericInformation() && (var1 = this.getGenericInfo().getExceptionTypes()).length > 0 ? var1 : this.getExceptionTypes());
   }

   public abstract String toGenericString();

   public boolean isVarArgs() {
      return (this.getModifiers() & 128) != 0;
   }

   public boolean isSynthetic() {
      return Modifier.isSynthetic(this.getModifiers());
   }

   public abstract Annotation[][] getParameterAnnotations();

   Annotation[][] sharedGetParameterAnnotations(Class<?>[] var1, byte[] var2) {
      int var3 = var1.length;
      if (var2 == null) {
         return new Annotation[var3][0];
      } else {
         Annotation[][] var4 = this.parseParameterAnnotations(var2);
         if (var4.length != var3) {
            this.handleParameterNumberMismatch(var4.length, var3);
         }

         return var4;
      }
   }

   abstract void handleParameterNumberMismatch(int var1, int var2);

   public <T extends Annotation> T getAnnotation(Class<T> var1) {
      Objects.requireNonNull(var1);
      return (Annotation)var1.cast(this.declaredAnnotations().get(var1));
   }

   public <T extends Annotation> T[] getAnnotationsByType(Class<T> var1) {
      Objects.requireNonNull(var1);
      return AnnotationSupport.getDirectlyAndIndirectlyPresent(this.declaredAnnotations(), var1);
   }

   public Annotation[] getDeclaredAnnotations() {
      return AnnotationParser.toArray(this.declaredAnnotations());
   }

   private synchronized Map<Class<? extends Annotation>, Annotation> declaredAnnotations() {
      if (this.declaredAnnotations == null) {
         Executable var1 = this.getRoot();
         if (var1 != null) {
            this.declaredAnnotations = var1.declaredAnnotations();
         } else {
            this.declaredAnnotations = AnnotationParser.parseAnnotations(this.getAnnotationBytes(), SharedSecrets.getJavaLangAccess().getConstantPool(this.getDeclaringClass()), this.getDeclaringClass());
         }
      }

      return this.declaredAnnotations;
   }

   public abstract AnnotatedType getAnnotatedReturnType();

   AnnotatedType getAnnotatedReturnType0(Type var1) {
      return TypeAnnotationParser.buildAnnotatedType(this.getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(this.getDeclaringClass()), this, this.getDeclaringClass(), var1, TypeAnnotation.TypeAnnotationTarget.METHOD_RETURN);
   }

   public AnnotatedType getAnnotatedReceiverType() {
      return Modifier.isStatic(this.getModifiers()) ? null : TypeAnnotationParser.buildAnnotatedType(this.getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(this.getDeclaringClass()), this, this.getDeclaringClass(), this.getDeclaringClass(), TypeAnnotation.TypeAnnotationTarget.METHOD_RECEIVER);
   }

   public AnnotatedType[] getAnnotatedParameterTypes() {
      return TypeAnnotationParser.buildAnnotatedTypes(this.getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(this.getDeclaringClass()), this, this.getDeclaringClass(), this.getAllGenericParameterTypes(), TypeAnnotation.TypeAnnotationTarget.METHOD_FORMAL_PARAMETER);
   }

   public AnnotatedType[] getAnnotatedExceptionTypes() {
      return TypeAnnotationParser.buildAnnotatedTypes(this.getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(this.getDeclaringClass()), this, this.getDeclaringClass(), this.getGenericExceptionTypes(), TypeAnnotation.TypeAnnotationTarget.THROWS);
   }
}
