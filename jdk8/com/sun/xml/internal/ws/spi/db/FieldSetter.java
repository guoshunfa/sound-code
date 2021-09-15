package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class FieldSetter extends PropertySetterBase {
   protected Field field;

   public FieldSetter(Field f) {
      this.field = f;
      this.type = f.getType();
   }

   public Field getField() {
      return this.field;
   }

   public void set(final Object instance, final Object resource) {
      if (this.field.isAccessible()) {
         try {
            this.field.set(instance, resource);
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      } else {
         try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
               public Object run() throws IllegalAccessException {
                  if (!FieldSetter.this.field.isAccessible()) {
                     FieldSetter.this.field.setAccessible(true);
                  }

                  FieldSetter.this.field.set(instance, resource);
                  return null;
               }
            });
         } catch (PrivilegedActionException var4) {
            var4.printStackTrace();
         }
      }

   }

   public <A> A getAnnotation(Class<A> annotationType) {
      return this.field.getAnnotation(annotationType);
   }
}
