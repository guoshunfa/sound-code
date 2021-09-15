package com.sun.org.glassfish.gmbal.util;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericConstructor<T> {
   private final Object lock = new Object();
   private String typeName;
   private Class<T> resultType;
   private Class<?> type;
   private Class<?>[] signature;
   private Constructor constructor;

   public GenericConstructor(Class<T> type, String className, Class<?>... signature) {
      this.resultType = type;
      this.typeName = className;
      this.signature = (Class[])signature.clone();
   }

   private void getConstructor() {
      synchronized(this.lock) {
         if (this.type == null || this.constructor == null) {
            try {
               this.type = Class.forName(this.typeName);
               this.constructor = (Constructor)AccessController.doPrivileged(new PrivilegedExceptionAction<Constructor>() {
                  public Constructor run() throws Exception {
                     synchronized(GenericConstructor.this.lock) {
                        return GenericConstructor.this.type.getDeclaredConstructor(GenericConstructor.this.signature);
                     }
                  }
               });
            } catch (Exception var4) {
               Logger.getLogger("com.sun.org.glassfish.gmbal.util").log(Level.FINE, (String)"Failure in getConstructor", (Throwable)var4);
            }
         }

      }
   }

   public synchronized T create(Object... args) {
      synchronized(this.lock) {
         T result = null;
         int ctr = 0;

         while(ctr <= 1) {
            this.getConstructor();
            if (this.constructor == null) {
               break;
            }

            try {
               result = this.resultType.cast(this.constructor.newInstance(args));
               break;
            } catch (Exception var7) {
               this.constructor = null;
               Logger.getLogger("com.sun.org.glassfish.gmbal.util").log(Level.WARNING, (String)"Error invoking constructor", (Throwable)var7);
               ++ctr;
            }
         }

         return result;
      }
   }
}
