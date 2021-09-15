package com.sun.xml.internal.bind.v2;

import com.sun.xml.internal.bind.Util;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ClassFactory {
   private static final Class[] emptyClass = new Class[0];
   private static final Object[] emptyObject = new Object[0];
   private static final Logger logger = Util.getClassLogger();
   private static final ThreadLocal<Map<Class, WeakReference<Constructor>>> tls = new ThreadLocal<Map<Class, WeakReference<Constructor>>>() {
      public Map<Class, WeakReference<Constructor>> initialValue() {
         return new WeakHashMap();
      }
   };

   public static void cleanCache() {
      if (tls != null) {
         try {
            tls.remove();
         } catch (Exception var1) {
            logger.log(Level.WARNING, (String)"Unable to clean Thread Local cache of classes used in Unmarshaller: {0}", (Object)var1.getLocalizedMessage());
         }
      }

   }

   public static <T> T create0(Class<T> clazz) throws IllegalAccessException, InvocationTargetException, InstantiationException {
      Map<Class, WeakReference<Constructor>> m = (Map)tls.get();
      Constructor<T> cons = null;
      WeakReference<Constructor> consRef = (WeakReference)m.get(clazz);
      if (consRef != null) {
         cons = (Constructor)consRef.get();
      }

      if (cons == null) {
         try {
            cons = clazz.getDeclaredConstructor(emptyClass);
         } catch (NoSuchMethodException var7) {
            logger.log(Level.INFO, (String)("No default constructor found on " + clazz), (Throwable)var7);
            NoSuchMethodError exp;
            if (clazz.getDeclaringClass() != null && !Modifier.isStatic(clazz.getModifiers())) {
               exp = new NoSuchMethodError(Messages.NO_DEFAULT_CONSTRUCTOR_IN_INNER_CLASS.format(clazz.getName()));
            } else {
               exp = new NoSuchMethodError(var7.getMessage());
            }

            exp.initCause(var7);
            throw exp;
         }

         int classMod = clazz.getModifiers();
         if (!Modifier.isPublic(classMod) || !Modifier.isPublic(cons.getModifiers())) {
            try {
               cons.setAccessible(true);
            } catch (SecurityException var6) {
               logger.log(Level.FINE, (String)("Unable to make the constructor of " + clazz + " accessible"), (Throwable)var6);
               throw var6;
            }
         }

         m.put(clazz, new WeakReference(cons));
      }

      return cons.newInstance(emptyObject);
   }

   public static <T> T create(Class<T> clazz) {
      try {
         return create0(clazz);
      } catch (InstantiationException var3) {
         logger.log(Level.INFO, (String)("failed to create a new instance of " + clazz), (Throwable)var3);
         throw new InstantiationError(var3.toString());
      } catch (IllegalAccessException var4) {
         logger.log(Level.INFO, (String)("failed to create a new instance of " + clazz), (Throwable)var4);
         throw new IllegalAccessError(var4.toString());
      } catch (InvocationTargetException var5) {
         Throwable target = var5.getTargetException();
         if (target instanceof RuntimeException) {
            throw (RuntimeException)target;
         } else if (target instanceof Error) {
            throw (Error)target;
         } else {
            throw new IllegalStateException(target);
         }
      }
   }

   public static Object create(Method method) {
      Object errorMsg;
      try {
         return method.invoke((Object)null, emptyObject);
      } catch (InvocationTargetException var4) {
         Throwable target = var4.getTargetException();
         if (target instanceof RuntimeException) {
            throw (RuntimeException)target;
         }

         if (target instanceof Error) {
            throw (Error)target;
         }

         throw new IllegalStateException(target);
      } catch (IllegalAccessException var5) {
         logger.log(Level.INFO, (String)("failed to create a new instance of " + method.getReturnType().getName()), (Throwable)var5);
         throw new IllegalAccessError(var5.toString());
      } catch (IllegalArgumentException var6) {
         logger.log(Level.INFO, (String)("failed to create a new instance of " + method.getReturnType().getName()), (Throwable)var6);
         errorMsg = var6;
      } catch (NullPointerException var7) {
         logger.log(Level.INFO, (String)("failed to create a new instance of " + method.getReturnType().getName()), (Throwable)var7);
         errorMsg = var7;
      } catch (ExceptionInInitializerError var8) {
         logger.log(Level.INFO, (String)("failed to create a new instance of " + method.getReturnType().getName()), (Throwable)var8);
         errorMsg = var8;
      }

      NoSuchMethodError exp = new NoSuchMethodError(((Throwable)errorMsg).getMessage());
      exp.initCause((Throwable)errorMsg);
      throw exp;
   }

   public static <T> Class<? extends T> inferImplClass(Class<T> fieldType, Class[] knownImplClasses) {
      if (!fieldType.isInterface()) {
         return fieldType;
      } else {
         Class[] var2 = knownImplClasses;
         int var3 = knownImplClasses.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Class<?> impl = var2[var4];
            if (fieldType.isAssignableFrom(impl)) {
               return impl.asSubclass(fieldType);
            }
         }

         return null;
      }
   }
}
