package com.sun.corba.se.spi.orbutil.proxy;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.presentation.rmi.DynamicAccessPermission;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class CompositeInvocationHandlerImpl implements CompositeInvocationHandler {
   private Map classToInvocationHandler = new LinkedHashMap();
   private InvocationHandler defaultHandler = null;
   private static final DynamicAccessPermission perm = new DynamicAccessPermission("access");
   private static final long serialVersionUID = 4571178305984833743L;

   public void addInvocationHandler(Class var1, InvocationHandler var2) {
      this.checkAccess();
      this.classToInvocationHandler.put(var1, var2);
   }

   public void setDefaultHandler(InvocationHandler var1) {
      this.checkAccess();
      this.defaultHandler = var1;
   }

   public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable {
      Class var4 = var2.getDeclaringClass();
      InvocationHandler var5 = (InvocationHandler)this.classToInvocationHandler.get(var4);
      if (var5 == null) {
         if (this.defaultHandler == null) {
            ORBUtilSystemException var6 = ORBUtilSystemException.get("util");
            throw var6.noInvocationHandler("\"" + var2.toString() + "\"");
         }

         var5 = this.defaultHandler;
      }

      return var5.invoke(var1, var2, var3);
   }

   private void checkAccess() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(perm);
      }

   }
}
