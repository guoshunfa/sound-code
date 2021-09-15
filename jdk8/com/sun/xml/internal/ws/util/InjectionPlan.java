package com.sun.xml.internal.ws.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.Resource;
import javax.xml.ws.WebServiceException;

public abstract class InjectionPlan<T, R> {
   public abstract void inject(T var1, R var2);

   public void inject(T instance, Callable<R> resource) {
      try {
         this.inject(instance, resource.call());
      } catch (Exception var4) {
         throw new WebServiceException(var4);
      }
   }

   private static void invokeMethod(final Method method, final Object instance, final Object... args) {
      if (method != null) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               try {
                  if (!method.isAccessible()) {
                     method.setAccessible(true);
                  }

                  method.invoke(instance, args);
                  return null;
               } catch (IllegalAccessException var2) {
                  throw new WebServiceException(var2);
               } catch (InvocationTargetException var3) {
                  throw new WebServiceException(var3);
               }
            }
         });
      }
   }

   public static <T, R> InjectionPlan<T, R> buildInjectionPlan(Class<? extends T> clazz, Class<R> resourceType, boolean isStatic) {
      List<InjectionPlan<T, R>> plan = new ArrayList();

      Class cl;
      int var6;
      int var7;
      Resource resource;
      for(cl = clazz; cl != Object.class; cl = cl.getSuperclass()) {
         Field[] var5 = cl.getDeclaredFields();
         var6 = var5.length;

         for(var7 = 0; var7 < var6; ++var7) {
            Field field = var5[var7];
            resource = (Resource)field.getAnnotation(Resource.class);
            if (resource != null && isInjectionPoint(resource, field.getType(), "Incorrect type for field" + field.getName(), resourceType)) {
               if (isStatic && !Modifier.isStatic(field.getModifiers())) {
                  throw new WebServiceException("Static resource " + resourceType + " cannot be injected to non-static " + field);
               }

               plan.add(new InjectionPlan.FieldInjectionPlan(field));
            }
         }
      }

      for(cl = clazz; cl != Object.class; cl = cl.getSuperclass()) {
         Method[] var11 = cl.getDeclaredMethods();
         var6 = var11.length;

         for(var7 = 0; var7 < var6; ++var7) {
            Method method = var11[var7];
            resource = (Resource)method.getAnnotation(Resource.class);
            if (resource != null) {
               Class[] paramTypes = method.getParameterTypes();
               if (paramTypes.length != 1) {
                  throw new WebServiceException("Incorrect no of arguments for method " + method);
               }

               if (isInjectionPoint(resource, paramTypes[0], "Incorrect argument types for method" + method.getName(), resourceType)) {
                  if (isStatic && !Modifier.isStatic(method.getModifiers())) {
                     throw new WebServiceException("Static resource " + resourceType + " cannot be injected to non-static " + method);
                  }

                  plan.add(new InjectionPlan.MethodInjectionPlan(method));
               }
            }
         }
      }

      return new InjectionPlan.Compositor(plan);
   }

   private static boolean isInjectionPoint(Resource resource, Class fieldType, String errorMessage, Class resourceType) {
      Class t = resource.type();
      if (t.equals(Object.class)) {
         return fieldType.equals(resourceType);
      } else if (t.equals(resourceType)) {
         if (fieldType.isAssignableFrom(resourceType)) {
            return true;
         } else {
            throw new WebServiceException(errorMessage);
         }
      } else {
         return false;
      }
   }

   private static class Compositor<T, R> extends InjectionPlan<T, R> {
      private final Collection<InjectionPlan<T, R>> children;

      public Compositor(Collection<InjectionPlan<T, R>> children) {
         this.children = children;
      }

      public void inject(T instance, R res) {
         Iterator var3 = this.children.iterator();

         while(var3.hasNext()) {
            InjectionPlan<T, R> plan = (InjectionPlan)var3.next();
            plan.inject(instance, res);
         }

      }

      public void inject(T instance, Callable<R> resource) {
         if (!this.children.isEmpty()) {
            super.inject(instance, resource);
         }

      }
   }

   public static class MethodInjectionPlan<T, R> extends InjectionPlan<T, R> {
      private final Method method;

      public MethodInjectionPlan(Method method) {
         this.method = method;
      }

      public void inject(T instance, R resource) {
         InjectionPlan.invokeMethod(this.method, instance, resource);
      }
   }

   public static class FieldInjectionPlan<T, R> extends InjectionPlan<T, R> {
      private final Field field;

      public FieldInjectionPlan(Field field) {
         this.field = field;
      }

      public void inject(final T instance, final R resource) {
         AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               try {
                  if (!FieldInjectionPlan.this.field.isAccessible()) {
                     FieldInjectionPlan.this.field.setAccessible(true);
                  }

                  FieldInjectionPlan.this.field.set(instance, resource);
                  return null;
               } catch (IllegalAccessException var2) {
                  throw new WebServiceException(var2);
               }
            }
         });
      }
   }
}
