package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.rmi.Remote;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.IDLEntity;

public abstract class StubFactoryFactoryDynamicBase extends StubFactoryFactoryBase {
   protected final ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.presentation");

   public PresentationManager.StubFactory createStubFactory(String var1, boolean var2, String var3, Class var4, ClassLoader var5) {
      Class var6 = null;

      try {
         var6 = Util.loadClass(var1, var3, var5);
      } catch (ClassNotFoundException var10) {
         throw this.wrapper.classNotFound3(CompletionStatus.COMPLETED_MAYBE, var10, var1);
      }

      PresentationManager var7 = ORB.getPresentationManager();
      if (IDLEntity.class.isAssignableFrom(var6) && !Remote.class.isAssignableFrom(var6)) {
         PresentationManager.StubFactoryFactory var11 = var7.getStubFactoryFactory(false);
         PresentationManager.StubFactory var9 = var11.createStubFactory(var1, true, var3, var4, var5);
         return var9;
      } else {
         PresentationManager.ClassData var8 = var7.getClassData(var6);
         return this.makeDynamicStubFactory(var7, var8, var5);
      }
   }

   public abstract PresentationManager.StubFactory makeDynamicStubFactory(PresentationManager var1, PresentationManager.ClassData var2, ClassLoader var3);

   public Tie getTie(Class var1) {
      PresentationManager var2 = ORB.getPresentationManager();
      return new ReflectiveTie(var2, this.wrapper);
   }

   public boolean createsDynamicStubs() {
      return true;
   }
}
