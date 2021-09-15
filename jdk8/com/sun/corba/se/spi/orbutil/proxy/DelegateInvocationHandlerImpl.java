package com.sun.corba.se.spi.orbutil.proxy;

import com.sun.corba.se.impl.presentation.rmi.DynamicAccessPermission;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class DelegateInvocationHandlerImpl {
   private DelegateInvocationHandlerImpl() {
   }

   public static InvocationHandler create(final Object var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new DynamicAccessPermission("access"));
      }

      return new InvocationHandler() {
         public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable {
            try {
               return var2.invoke(var0, var3);
            } catch (InvocationTargetException var5) {
               throw var5.getCause();
            }
         }
      };
   }
}
