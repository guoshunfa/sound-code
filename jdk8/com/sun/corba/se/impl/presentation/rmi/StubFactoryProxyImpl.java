package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.lang.reflect.Proxy;
import org.omg.CORBA.Object;

public class StubFactoryProxyImpl extends StubFactoryDynamicBase {
   public StubFactoryProxyImpl(PresentationManager.ClassData var1, ClassLoader var2) {
      super(var1, var2);
   }

   public Object makeStub() {
      InvocationHandlerFactory var1 = this.classData.getInvocationHandlerFactory();
      LinkedInvocationHandler var2 = (LinkedInvocationHandler)var1.getInvocationHandler();
      Class[] var3 = var1.getProxyInterfaces();
      DynamicStub var4 = (DynamicStub)Proxy.newProxyInstance(this.loader, var3, var2);
      var2.setProxy((Proxy)var4);
      return var4;
   }
}
