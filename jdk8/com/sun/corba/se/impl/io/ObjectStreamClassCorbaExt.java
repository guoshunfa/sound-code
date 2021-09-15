package com.sun.corba.se.impl.io;

import java.io.IOException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PrivilegedAction;

class ObjectStreamClassCorbaExt {
   static final boolean isAbstractInterface(Class var0) {
      if (var0.isInterface() && !Remote.class.isAssignableFrom(var0)) {
         Method[] var1 = var0.getMethods();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            Class[] var3 = var1[var2].getExceptionTypes();
            boolean var4 = false;

            for(int var5 = 0; var5 < var3.length && !var4; ++var5) {
               if (RemoteException.class == var3[var5] || Throwable.class == var3[var5] || Exception.class == var3[var5] || IOException.class == var3[var5]) {
                  var4 = true;
               }
            }

            if (!var4) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   static final boolean isAny(String var0) {
      boolean var1 = false;
      if (var0 != null && (var0.equals("Ljava/lang/Object;") || var0.equals("Ljava/io/Serializable;") || var0.equals("Ljava/io/Externalizable;"))) {
         var1 = true;
      }

      return var1;
   }

   private static final Method[] getDeclaredMethods(final Class var0) {
      return (Method[])((Method[])AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return var0.getDeclaredMethods();
         }
      }));
   }
}
