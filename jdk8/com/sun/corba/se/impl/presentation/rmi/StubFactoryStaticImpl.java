package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import org.omg.CORBA.Object;

public class StubFactoryStaticImpl extends StubFactoryBase {
   private Class stubClass;

   public StubFactoryStaticImpl(Class var1) {
      super((PresentationManager.ClassData)null);
      this.stubClass = var1;
   }

   public Object makeStub() {
      Object var1 = null;

      try {
         var1 = (Object)this.stubClass.newInstance();
         return var1;
      } catch (InstantiationException var3) {
         throw new RuntimeException(var3);
      } catch (IllegalAccessException var4) {
         throw new RuntimeException(var4);
      }
   }
}
