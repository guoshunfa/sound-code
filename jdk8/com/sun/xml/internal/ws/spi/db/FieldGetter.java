package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class FieldGetter extends PropertyGetterBase {
   protected Field field;

   public FieldGetter(Field f) {
      this.field = f;
      this.type = f.getType();
   }

   public Field getField() {
      return this.field;
   }

   public Object get(Object instance) {
      if (this.field.isAccessible()) {
         try {
            return this.field.get(instance);
         } catch (Exception var4) {
            var4.printStackTrace();
            return null;
         }
      } else {
         FieldGetter.PrivilegedGetter privilegedGetter = new FieldGetter.PrivilegedGetter(this.field, instance);

         try {
            AccessController.doPrivileged((PrivilegedExceptionAction)privilegedGetter);
         } catch (PrivilegedActionException var5) {
            var5.printStackTrace();
         }

         return privilegedGetter.value;
      }
   }

   public <A> A getAnnotation(Class<A> annotationType) {
      return this.field.getAnnotation(annotationType);
   }

   static class PrivilegedGetter implements PrivilegedExceptionAction {
      private Object value;
      private Field field;
      private Object instance;

      public PrivilegedGetter(Field field, Object instance) {
         this.field = field;
         this.instance = instance;
      }

      public Object run() throws IllegalAccessException {
         if (!this.field.isAccessible()) {
            this.field.setAccessible(true);
         }

         this.value = this.field.get(this.instance);
         return null;
      }
   }
}
