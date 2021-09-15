package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class MethodGetter extends PropertyGetterBase {
   private Method method;

   public MethodGetter(Method m) {
      this.method = m;
      this.type = m.getReturnType();
   }

   public Method getMethod() {
      return this.method;
   }

   public <A> A getAnnotation(Class<A> annotationType) {
      return this.method.getAnnotation(annotationType);
   }

   public Object get(Object instance) {
      Object[] args = new Object[0];

      try {
         if (this.method.isAccessible()) {
            return this.method.invoke(instance, args);
         } else {
            MethodGetter.PrivilegedGetter privilegedGetter = new MethodGetter.PrivilegedGetter(this.method, instance);

            try {
               AccessController.doPrivileged((PrivilegedExceptionAction)privilegedGetter);
            } catch (PrivilegedActionException var5) {
               var5.printStackTrace();
            }

            return privilegedGetter.value;
         }
      } catch (Exception var6) {
         var6.printStackTrace();
         return null;
      }
   }

   static class PrivilegedGetter implements PrivilegedExceptionAction {
      private Object value;
      private Method method;
      private Object instance;

      public PrivilegedGetter(Method m, Object instance) {
         this.method = m;
         this.instance = instance;
      }

      public Object run() throws IllegalAccessException {
         if (!this.method.isAccessible()) {
            this.method.setAccessible(true);
         }

         try {
            this.value = this.method.invoke(this.instance);
         } catch (Exception var2) {
            var2.printStackTrace();
         }

         return null;
      }
   }
}
