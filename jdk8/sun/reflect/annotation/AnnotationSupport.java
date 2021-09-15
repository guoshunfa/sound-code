package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

public final class AnnotationSupport {
   private static final JavaLangAccess LANG_ACCESS = SharedSecrets.getJavaLangAccess();

   public static <A extends Annotation> A[] getDirectlyAndIndirectlyPresent(Map<Class<? extends Annotation>, Annotation> var0, Class<A> var1) {
      ArrayList var2 = new ArrayList();
      Annotation var3 = (Annotation)var0.get(var1);
      if (var3 != null) {
         var2.add(var3);
      }

      Annotation[] var4 = getIndirectlyPresent(var0, var1);
      if (var4 != null && var4.length != 0) {
         boolean var5 = var3 == null || containerBeforeContainee(var0, var1);
         var2.addAll(var5 ? 0 : 1, Arrays.asList(var4));
      }

      Annotation[] var6 = (Annotation[])((Annotation[])Array.newInstance(var1, var2.size()));
      return (Annotation[])var2.toArray(var6);
   }

   private static <A extends Annotation> A[] getIndirectlyPresent(Map<Class<? extends Annotation>, Annotation> var0, Class<A> var1) {
      Repeatable var2 = (Repeatable)var1.getDeclaredAnnotation(Repeatable.class);
      if (var2 == null) {
         return null;
      } else {
         Class var3 = var2.value();
         Annotation var4 = (Annotation)var0.get(var3);
         if (var4 == null) {
            return null;
         } else {
            Annotation[] var5 = getValueArray(var4);
            checkTypes(var5, var4, var1);
            return var5;
         }
      }
   }

   private static <A extends Annotation> boolean containerBeforeContainee(Map<Class<? extends Annotation>, Annotation> var0, Class<A> var1) {
      Class var2 = ((Repeatable)var1.getDeclaredAnnotation(Repeatable.class)).value();
      Iterator var3 = var0.keySet().iterator();

      Class var4;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         var4 = (Class)var3.next();
         if (var4 == var2) {
            return true;
         }
      } while(var4 != var1);

      return false;
   }

   public static <A extends Annotation> A[] getAssociatedAnnotations(Map<Class<? extends Annotation>, Annotation> var0, Class<?> var1, Class<A> var2) {
      Objects.requireNonNull(var1);
      Annotation[] var3 = getDirectlyAndIndirectlyPresent(var0, var2);
      if (AnnotationType.getInstance(var2).isInherited()) {
         for(Class var4 = var1.getSuperclass(); var3.length == 0 && var4 != null; var4 = var4.getSuperclass()) {
            var3 = getDirectlyAndIndirectlyPresent(LANG_ACCESS.getDeclaredAnnotationMap(var4), var2);
         }
      }

      return var3;
   }

   private static <A extends Annotation> A[] getValueArray(Annotation var0) {
      try {
         Class var1 = var0.annotationType();
         AnnotationType var2 = AnnotationType.getInstance(var1);
         if (var2 == null) {
            throw invalidContainerException(var0, (Throwable)null);
         } else {
            Method var3 = (Method)var2.members().get("value");
            if (var3 == null) {
               throw invalidContainerException(var0, (Throwable)null);
            } else {
               var3.setAccessible(true);
               Annotation[] var4 = (Annotation[])((Annotation[])var3.invoke(var0));
               return var4;
            }
         }
      } catch (IllegalArgumentException | InvocationTargetException | ClassCastException | IllegalAccessException var5) {
         throw invalidContainerException(var0, var5);
      }
   }

   private static AnnotationFormatError invalidContainerException(Annotation var0, Throwable var1) {
      return new AnnotationFormatError(var0 + " is an invalid container for repeating annotations", var1);
   }

   private static <A extends Annotation> void checkTypes(A[] var0, Annotation var1, Class<A> var2) {
      Annotation[] var3 = var0;
      int var4 = var0.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Annotation var6 = var3[var5];
         if (!var2.isInstance(var6)) {
            throw new AnnotationFormatError(String.format("%s is an invalid container for repeating annotations of type: %s", var1, var2));
         }
      }

   }
}
