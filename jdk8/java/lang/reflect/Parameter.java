package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import sun.reflect.annotation.AnnotationSupport;

public final class Parameter implements AnnotatedElement {
   private final String name;
   private final int modifiers;
   private final Executable executable;
   private final int index;
   private transient volatile Type parameterTypeCache = null;
   private transient volatile Class<?> parameterClassCache = null;
   private transient Map<Class<? extends Annotation>, Annotation> declaredAnnotations;

   Parameter(String var1, int var2, Executable var3, int var4) {
      this.name = var1;
      this.modifiers = var2;
      this.executable = var3;
      this.index = var4;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Parameter)) {
         return false;
      } else {
         Parameter var2 = (Parameter)var1;
         return var2.executable.equals(this.executable) && var2.index == this.index;
      }
   }

   public int hashCode() {
      return this.executable.hashCode() ^ this.index;
   }

   public boolean isNamePresent() {
      return this.executable.hasRealParameterData() && this.name != null;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      Type var2 = this.getParameterizedType();
      String var3 = var2.getTypeName();
      var1.append(Modifier.toString(this.getModifiers()));
      if (0 != this.modifiers) {
         var1.append(' ');
      }

      if (this.isVarArgs()) {
         var1.append(var3.replaceFirst("\\[\\]$", "..."));
      } else {
         var1.append(var3);
      }

      var1.append(' ');
      var1.append(this.getName());
      return var1.toString();
   }

   public Executable getDeclaringExecutable() {
      return this.executable;
   }

   public int getModifiers() {
      return this.modifiers;
   }

   public String getName() {
      return this.name != null && !this.name.equals("") ? this.name : "arg" + this.index;
   }

   String getRealName() {
      return this.name;
   }

   public Type getParameterizedType() {
      Type var1 = this.parameterTypeCache;
      if (null == var1) {
         var1 = this.executable.getAllGenericParameterTypes()[this.index];
         this.parameterTypeCache = var1;
      }

      return var1;
   }

   public Class<?> getType() {
      Class var1 = this.parameterClassCache;
      if (null == var1) {
         var1 = this.executable.getParameterTypes()[this.index];
         this.parameterClassCache = var1;
      }

      return var1;
   }

   public AnnotatedType getAnnotatedType() {
      return this.executable.getAnnotatedParameterTypes()[this.index];
   }

   public boolean isImplicit() {
      return Modifier.isMandated(this.getModifiers());
   }

   public boolean isSynthetic() {
      return Modifier.isSynthetic(this.getModifiers());
   }

   public boolean isVarArgs() {
      return this.executable.isVarArgs() && this.index == this.executable.getParameterCount() - 1;
   }

   public <T extends Annotation> T getAnnotation(Class<T> var1) {
      Objects.requireNonNull(var1);
      return (Annotation)var1.cast(this.declaredAnnotations().get(var1));
   }

   public <T extends Annotation> T[] getAnnotationsByType(Class<T> var1) {
      Objects.requireNonNull(var1);
      return AnnotationSupport.getDirectlyAndIndirectlyPresent(this.declaredAnnotations(), var1);
   }

   public Annotation[] getDeclaredAnnotations() {
      return this.executable.getParameterAnnotations()[this.index];
   }

   public <T extends Annotation> T getDeclaredAnnotation(Class<T> var1) {
      return this.getAnnotation(var1);
   }

   public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> var1) {
      return this.getAnnotationsByType(var1);
   }

   public Annotation[] getAnnotations() {
      return this.getDeclaredAnnotations();
   }

   private synchronized Map<Class<? extends Annotation>, Annotation> declaredAnnotations() {
      if (null == this.declaredAnnotations) {
         this.declaredAnnotations = new HashMap();
         Annotation[] var1 = this.getDeclaredAnnotations();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            this.declaredAnnotations.put(var1[var2].annotationType(), var1[var2]);
         }
      }

      return this.declaredAnnotations;
   }
}
