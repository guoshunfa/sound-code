package com.sun.corba.se.spi.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.oa.poa.POAManagerImpl;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public abstract class StubAdapter {
   private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.presentation");

   private StubAdapter() {
   }

   public static boolean isStubClass(Class var0) {
      return ObjectImpl.class.isAssignableFrom(var0) || DynamicStub.class.isAssignableFrom(var0);
   }

   public static boolean isStub(Object var0) {
      return var0 instanceof DynamicStub || var0 instanceof ObjectImpl;
   }

   public static void setDelegate(Object var0, Delegate var1) {
      if (var0 instanceof DynamicStub) {
         ((DynamicStub)var0).setDelegate(var1);
      } else {
         if (!(var0 instanceof ObjectImpl)) {
            throw wrapper.setDelegateRequiresStub();
         }

         ((ObjectImpl)var0)._set_delegate(var1);
      }

   }

   public static org.omg.CORBA.Object activateServant(Servant var0) {
      POA var1 = var0._default_POA();
      org.omg.CORBA.Object var2 = null;

      try {
         var2 = var1.servant_to_reference(var0);
      } catch (ServantNotActive var5) {
         throw wrapper.getDelegateServantNotActive((Throwable)var5);
      } catch (WrongPolicy var6) {
         throw wrapper.getDelegateWrongPolicy((Throwable)var6);
      }

      POAManager var3 = var1.the_POAManager();
      if (var3 instanceof POAManagerImpl) {
         POAManagerImpl var4 = (POAManagerImpl)var3;
         var4.implicitActivation();
      }

      return var2;
   }

   public static org.omg.CORBA.Object activateTie(Tie var0) {
      if (var0 instanceof ObjectImpl) {
         return var0.thisObject();
      } else if (var0 instanceof Servant) {
         Servant var1 = (Servant)var0;
         return activateServant(var1);
      } else {
         throw wrapper.badActivateTieCall();
      }
   }

   public static Delegate getDelegate(Object var0) {
      if (var0 instanceof DynamicStub) {
         return ((DynamicStub)var0).getDelegate();
      } else if (var0 instanceof ObjectImpl) {
         return ((ObjectImpl)var0)._get_delegate();
      } else if (var0 instanceof Tie) {
         Tie var1 = (Tie)var0;
         org.omg.CORBA.Object var2 = activateTie(var1);
         return getDelegate(var2);
      } else {
         throw wrapper.getDelegateRequiresStub();
      }
   }

   public static ORB getORB(Object var0) {
      if (var0 instanceof DynamicStub) {
         return ((DynamicStub)var0).getORB();
      } else if (var0 instanceof ObjectImpl) {
         return ((ObjectImpl)var0)._orb();
      } else {
         throw wrapper.getOrbRequiresStub();
      }
   }

   public static String[] getTypeIds(Object var0) {
      if (var0 instanceof DynamicStub) {
         return ((DynamicStub)var0).getTypeIds();
      } else if (var0 instanceof ObjectImpl) {
         return ((ObjectImpl)var0)._ids();
      } else {
         throw wrapper.getTypeIdsRequiresStub();
      }
   }

   public static void connect(Object var0, ORB var1) throws RemoteException {
      if (var0 instanceof DynamicStub) {
         ((DynamicStub)var0).connect((com.sun.corba.se.spi.orb.ORB)var1);
      } else if (var0 instanceof Stub) {
         ((Stub)var0).connect(var1);
      } else {
         if (!(var0 instanceof ObjectImpl)) {
            throw wrapper.connectRequiresStub();
         }

         var1.connect((org.omg.CORBA.Object)var0);
      }

   }

   public static boolean isLocal(Object var0) {
      if (var0 instanceof DynamicStub) {
         return ((DynamicStub)var0).isLocal();
      } else if (var0 instanceof ObjectImpl) {
         return ((ObjectImpl)var0)._is_local();
      } else {
         throw wrapper.isLocalRequiresStub();
      }
   }

   public static OutputStream request(Object var0, String var1, boolean var2) {
      if (var0 instanceof DynamicStub) {
         return ((DynamicStub)var0).request(var1, var2);
      } else if (var0 instanceof ObjectImpl) {
         return ((ObjectImpl)var0)._request(var1, var2);
      } else {
         throw wrapper.requestRequiresStub();
      }
   }
}
