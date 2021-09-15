package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class StubFactoryFactoryProxyImpl extends StubFactoryFactoryDynamicBase {
   public PresentationManager.StubFactory makeDynamicStubFactory(PresentationManager var1, final PresentationManager.ClassData var2, final ClassLoader var3) {
      return (PresentationManager.StubFactory)AccessController.doPrivileged(new PrivilegedAction<StubFactoryProxyImpl>() {
         public StubFactoryProxyImpl run() {
            return new StubFactoryProxyImpl(var2, var3);
         }
      });
   }
}
