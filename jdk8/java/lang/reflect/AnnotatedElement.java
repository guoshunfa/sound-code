package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.AnnotationType;

public interface AnnotatedElement {
   default boolean isAnnotationPresent(Class<? extends Annotation> var1) {
      return this.getAnnotation(var1) != null;
   }

   <T extends Annotation> T getAnnotation(Class<T> var1);

   Annotation[] getAnnotations();

   default <T extends Annotation> T[] getAnnotationsByType(Class<T> var1) {
      Annotation[] var2 = this.getDeclaredAnnotationsByType(var1);
      if (var2.length == 0 && this instanceof Class && AnnotationType.getInstance(var1).isInherited()) {
         Class var3 = ((Class)this).getSuperclass();
         if (var3 != null) {
            var2 = var3.getAnnotationsByType(var1);
         }
      }

      return var2;
   }

   default <T extends Annotation> T getDeclaredAnnotation(Class<T> var1) {
      Objects.requireNonNull(var1);
      Annotation[] var2 = this.getDeclaredAnnotations();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Annotation var5 = var2[var4];
         if (var1.equals(var5.annotationType())) {
            return (Annotation)var1.cast(var5);
         }
      }

      return null;
   }

   default <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> var1) {
      Objects.requireNonNull(var1);
      return AnnotationSupport.getDirectlyAndIndirectlyPresent((Map)Arrays.stream((Object[])this.getDeclaredAnnotations()).collect(Collectors.toMap(Annotation::annotationType, Function.identity(), (var0, var1x) -> {
         return var0;
      }, LinkedHashMap::new)), var1);
   }

   Annotation[] getDeclaredAnnotations();
}
