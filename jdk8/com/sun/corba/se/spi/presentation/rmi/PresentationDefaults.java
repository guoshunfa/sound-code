package com.sun.corba.se.spi.presentation.rmi;

import com.sun.corba.se.impl.presentation.rmi.StubFactoryFactoryProxyImpl;
import com.sun.corba.se.impl.presentation.rmi.StubFactoryFactoryStaticImpl;
import com.sun.corba.se.impl.presentation.rmi.StubFactoryStaticImpl;

public abstract class PresentationDefaults {
   private static StubFactoryFactoryStaticImpl staticImpl = null;

   private PresentationDefaults() {
   }

   public static synchronized PresentationManager.StubFactoryFactory getStaticStubFactoryFactory() {
      if (staticImpl == null) {
         staticImpl = new StubFactoryFactoryStaticImpl();
      }

      return staticImpl;
   }

   public static PresentationManager.StubFactoryFactory getProxyStubFactoryFactory() {
      return new StubFactoryFactoryProxyImpl();
   }

   public static PresentationManager.StubFactory makeStaticStubFactory(Class var0) {
      return new StubFactoryStaticImpl(var0);
   }
}
