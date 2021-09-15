package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

public class AnnotationType {
   private final Map<String, Class<?>> memberTypes;
   private final Map<String, Object> memberDefaults;
   private final Map<String, Method> members;
   private final RetentionPolicy retention;
   private final boolean inherited;

   public static AnnotationType getInstance(Class<? extends Annotation> var0) {
      JavaLangAccess var1 = SharedSecrets.getJavaLangAccess();
      AnnotationType var2 = var1.getAnnotationType(var0);
      if (var2 == null) {
         var2 = new AnnotationType(var0);
         if (!var1.casAnnotationType(var0, (AnnotationType)null, var2)) {
            var2 = var1.getAnnotationType(var0);

            assert var2 != null;
         }
      }

      return var2;
   }

   private AnnotationType(final Class<? extends Annotation> var1) {
      if (!var1.isAnnotation()) {
         throw new IllegalArgumentException("Not an annotation type");
      } else {
         Method[] var2 = (Method[])AccessController.doPrivileged(new PrivilegedAction<Method[]>() {
            public Method[] run() {
               return var1.getDeclaredMethods();
            }
         });
         this.memberTypes = new HashMap(var2.length + 1, 1.0F);
         this.memberDefaults = new HashMap(0);
         this.members = new HashMap(var2.length + 1, 1.0F);
         Method[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Method var6 = var3[var5];
            if (Modifier.isPublic(var6.getModifiers()) && Modifier.isAbstract(var6.getModifiers()) && !var6.isSynthetic()) {
               if (var6.getParameterTypes().length != 0) {
                  throw new IllegalArgumentException(var6 + " has params");
               }

               String var7 = var6.getName();
               Class var8 = var6.getReturnType();
               this.memberTypes.put(var7, invocationHandlerReturnType(var8));
               this.members.put(var7, var6);
               Object var9 = var6.getDefaultValue();
               if (var9 != null) {
                  this.memberDefaults.put(var7, var9);
               }
            }
         }

         if (var1 != Retention.class && var1 != Inherited.class) {
            JavaLangAccess var10 = SharedSecrets.getJavaLangAccess();
            Map var11 = AnnotationParser.parseSelectAnnotations(var10.getRawClassAnnotations(var1), var10.getConstantPool(var1), var1, Retention.class, Inherited.class);
            Retention var12 = (Retention)var11.get(Retention.class);
            this.retention = var12 == null ? RetentionPolicy.CLASS : var12.value();
            this.inherited = var11.containsKey(Inherited.class);
         } else {
            this.retention = RetentionPolicy.RUNTIME;
            this.inherited = false;
         }

      }
   }

   public static Class<?> invocationHandlerReturnType(Class<?> var0) {
      if (var0 == Byte.TYPE) {
         return Byte.class;
      } else if (var0 == Character.TYPE) {
         return Character.class;
      } else if (var0 == Double.TYPE) {
         return Double.class;
      } else if (var0 == Float.TYPE) {
         return Float.class;
      } else if (var0 == Integer.TYPE) {
         return Integer.class;
      } else if (var0 == Long.TYPE) {
         return Long.class;
      } else if (var0 == Short.TYPE) {
         return Short.class;
      } else {
         return var0 == Boolean.TYPE ? Boolean.class : var0;
      }
   }

   public Map<String, Class<?>> memberTypes() {
      return this.memberTypes;
   }

   public Map<String, Method> members() {
      return this.members;
   }

   public Map<String, Object> memberDefaults() {
      return this.memberDefaults;
   }

   public RetentionPolicy retention() {
      return this.retention;
   }

   public boolean isInherited() {
      return this.inherited;
   }

   public String toString() {
      return "Annotation Type:\n   Member types: " + this.memberTypes + "\n   Member defaults: " + this.memberDefaults + "\n   Retention policy: " + this.retention + "\n   Inherited: " + this.inherited;
   }
}
