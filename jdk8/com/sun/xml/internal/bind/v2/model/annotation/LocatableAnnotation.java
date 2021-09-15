package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.runtime.Location;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class LocatableAnnotation implements InvocationHandler, Locatable, Location {
   private final Annotation core;
   private final Locatable upstream;
   private static final Map<Class, Quick> quicks = new HashMap();

   public static <A extends Annotation> A create(A annotation, Locatable parentSourcePos) {
      if (annotation == null) {
         return null;
      } else {
         Class<? extends Annotation> type = annotation.annotationType();
         if (quicks.containsKey(type)) {
            return ((Quick)quicks.get(type)).newInstance(parentSourcePos, annotation);
         } else {
            ClassLoader cl = SecureLoader.getClassClassLoader(LocatableAnnotation.class);

            try {
               Class loadableT = Class.forName(type.getName(), false, cl);
               return loadableT != type ? annotation : (Annotation)Proxy.newProxyInstance(cl, new Class[]{type, Locatable.class}, new LocatableAnnotation(annotation, parentSourcePos));
            } catch (ClassNotFoundException var5) {
               return annotation;
            } catch (IllegalArgumentException var6) {
               return annotation;
            }
         }
      }
   }

   LocatableAnnotation(Annotation core, Locatable upstream) {
      this.core = core;
      this.upstream = upstream;
   }

   public Locatable getUpstream() {
      return this.upstream;
   }

   public Location getLocation() {
      return this;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      try {
         if (method.getDeclaringClass() == Locatable.class) {
            return method.invoke(this, args);
         } else if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException();
         } else {
            return method.invoke(this.core, args);
         }
      } catch (InvocationTargetException var5) {
         if (var5.getTargetException() != null) {
            throw var5.getTargetException();
         } else {
            throw var5;
         }
      }
   }

   public String toString() {
      return this.core.toString();
   }

   static {
      Quick[] var0 = Init.getAll();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         Quick q = var0[var2];
         quicks.put(q.annotationType(), q);
      }

   }
}
