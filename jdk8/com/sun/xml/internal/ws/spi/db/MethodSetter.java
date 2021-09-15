package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class MethodSetter extends PropertySetterBase {
   private Method method;

   public MethodSetter(Method m) {
      this.method = m;
      this.type = m.getParameterTypes()[0];
   }

   public Method getMethod() {
      return this.method;
   }

   public <A> A getAnnotation(Class<A> annotationType) {
      return this.method.getAnnotation(annotationType);
   }

   public void set(final Object instance, Object resource) {
      final Object[] args = new Object[]{resource};
      if (this.method.isAccessible()) {
         try {
            this.method.invoke(instance, args);
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      } else {
         try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
               public Object run() throws IllegalAccessException {
                  if (!MethodSetter.this.method.isAccessible()) {
                     MethodSetter.this.method.setAccessible(true);
                  }

                  try {
                     MethodSetter.this.method.invoke(instance, args);
                  } catch (Exception var2) {
                     var2.printStackTrace();
                  }

                  return null;
               }
            });
         } catch (PrivilegedActionException var5) {
            var5.printStackTrace();
         }
      }

   }
}
