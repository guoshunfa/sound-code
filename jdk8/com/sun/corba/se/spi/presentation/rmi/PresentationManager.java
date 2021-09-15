package com.sun.corba.se.spi.presentation.rmi;

import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import java.lang.reflect.Method;
import java.util.Map;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.Object;

public interface PresentationManager {
   PresentationManager.ClassData getClassData(Class var1);

   DynamicMethodMarshaller getDynamicMethodMarshaller(Method var1);

   PresentationManager.StubFactoryFactory getStubFactoryFactory(boolean var1);

   void setStubFactoryFactory(boolean var1, PresentationManager.StubFactoryFactory var2);

   Tie getTie();

   boolean useDynamicStubs();

   public interface ClassData {
      Class getMyClass();

      IDLNameTranslator getIDLNameTranslator();

      String[] getTypeIds();

      InvocationHandlerFactory getInvocationHandlerFactory();

      Map getDictionary();
   }

   public interface StubFactory {
      Object makeStub();

      String[] getTypeIds();
   }

   public interface StubFactoryFactory {
      String getStubName(String var1);

      PresentationManager.StubFactory createStubFactory(String var1, boolean var2, String var3, Class var4, ClassLoader var5);

      Tie getTie(Class var1);

      boolean createsDynamicStubs();
   }
}
