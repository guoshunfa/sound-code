package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class RuntimeInlineAnnotationReader extends AbstractInlineAnnotationReaderImpl<Type, Class, Field, Method> implements RuntimeAnnotationReader {
   private final Map<Class<? extends Annotation>, Map<Package, Annotation>> packageCache = new HashMap();

   public <A extends Annotation> A getFieldAnnotation(Class<A> annotation, Field field, Locatable srcPos) {
      return LocatableAnnotation.create(field.getAnnotation(annotation), srcPos);
   }

   public boolean hasFieldAnnotation(Class<? extends Annotation> annotationType, Field field) {
      return field.isAnnotationPresent(annotationType);
   }

   public boolean hasClassAnnotation(Class clazz, Class<? extends Annotation> annotationType) {
      return clazz.isAnnotationPresent(annotationType);
   }

   public Annotation[] getAllFieldAnnotations(Field field, Locatable srcPos) {
      Annotation[] r = field.getAnnotations();

      for(int i = 0; i < r.length; ++i) {
         r[i] = LocatableAnnotation.create(r[i], srcPos);
      }

      return r;
   }

   public <A extends Annotation> A getMethodAnnotation(Class<A> annotation, Method method, Locatable srcPos) {
      return LocatableAnnotation.create(method.getAnnotation(annotation), srcPos);
   }

   public boolean hasMethodAnnotation(Class<? extends Annotation> annotation, Method method) {
      return method.isAnnotationPresent(annotation);
   }

   public Annotation[] getAllMethodAnnotations(Method method, Locatable srcPos) {
      Annotation[] r = method.getAnnotations();

      for(int i = 0; i < r.length; ++i) {
         r[i] = LocatableAnnotation.create(r[i], srcPos);
      }

      return r;
   }

   public <A extends Annotation> A getMethodParameterAnnotation(Class<A> annotation, Method method, int paramIndex, Locatable srcPos) {
      Annotation[] pa = method.getParameterAnnotations()[paramIndex];
      Annotation[] var6 = pa;
      int var7 = pa.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Annotation a = var6[var8];
         if (a.annotationType() == annotation) {
            return LocatableAnnotation.create(a, srcPos);
         }
      }

      return null;
   }

   public <A extends Annotation> A getClassAnnotation(Class<A> a, Class clazz, Locatable srcPos) {
      return LocatableAnnotation.create(clazz.getAnnotation(a), srcPos);
   }

   public <A extends Annotation> A getPackageAnnotation(Class<A> a, Class clazz, Locatable srcPos) {
      Package p = clazz.getPackage();
      if (p == null) {
         return null;
      } else {
         Map<Package, Annotation> cache = (Map)this.packageCache.get(a);
         if (cache == null) {
            cache = new HashMap();
            this.packageCache.put(a, cache);
         }

         if (((Map)cache).containsKey(p)) {
            return (Annotation)((Map)cache).get(p);
         } else {
            A ann = LocatableAnnotation.create(p.getAnnotation(a), srcPos);
            ((Map)cache).put(p, ann);
            return ann;
         }
      }
   }

   public Class getClassValue(Annotation a, String name) {
      try {
         return (Class)a.annotationType().getMethod(name).invoke(a);
      } catch (IllegalAccessException var4) {
         throw new IllegalAccessError(var4.getMessage());
      } catch (InvocationTargetException var5) {
         throw new InternalError(Messages.CLASS_NOT_FOUND.format(a.annotationType(), var5.getMessage()));
      } catch (NoSuchMethodException var6) {
         throw new NoSuchMethodError(var6.getMessage());
      }
   }

   public Class[] getClassArrayValue(Annotation a, String name) {
      try {
         return (Class[])((Class[])a.annotationType().getMethod(name).invoke(a));
      } catch (IllegalAccessException var4) {
         throw new IllegalAccessError(var4.getMessage());
      } catch (InvocationTargetException var5) {
         throw new InternalError(var5.getMessage());
      } catch (NoSuchMethodException var6) {
         throw new NoSuchMethodError(var6.getMessage());
      }
   }

   protected String fullName(Method m) {
      return m.getDeclaringClass().getName() + '#' + m.getName();
   }
}
