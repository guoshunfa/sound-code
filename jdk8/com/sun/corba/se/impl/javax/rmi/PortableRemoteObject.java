package com.sun.corba.se.impl.javax.rmi;

import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.io.Externalizable;
import java.io.Serializable;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteStub;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.CORBA.PortableRemoteObjectDelegate;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;

public class PortableRemoteObject implements PortableRemoteObjectDelegate {
   public void exportObject(Remote var1) throws RemoteException {
      if (var1 == null) {
         throw new NullPointerException("invalid argument");
      } else if (Util.getTie(var1) != null) {
         throw new ExportException(var1.getClass().getName() + " already exported");
      } else {
         Tie var2 = Utility.loadTie(var1);
         if (var2 != null) {
            Util.registerTarget(var2, var1);
         } else {
            UnicastRemoteObject.exportObject(var1);
         }

      }
   }

   public Remote toStub(Remote var1) throws NoSuchObjectException {
      Remote var2 = null;
      if (var1 == null) {
         throw new NullPointerException("invalid argument");
      } else if (StubAdapter.isStub(var1)) {
         return var1;
      } else if (var1 instanceof RemoteStub) {
         return var1;
      } else {
         Tie var3 = Util.getTie(var1);
         if (var3 != null) {
            var2 = Utility.loadStub(var3, (PresentationManager.StubFactory)null, (String)null, true);
         } else if (Utility.loadTie(var1) == null) {
            var2 = RemoteObject.toStub(var1);
         }

         if (var2 == null) {
            throw new NoSuchObjectException("object not exported");
         } else {
            return var2;
         }
      }
   }

   public void unexportObject(Remote var1) throws NoSuchObjectException {
      if (var1 == null) {
         throw new NullPointerException("invalid argument");
      } else if (!StubAdapter.isStub(var1) && !(var1 instanceof RemoteStub)) {
         Tie var2 = Util.getTie(var1);
         if (var2 != null) {
            Util.unexportObject(var1);
         } else {
            if (Utility.loadTie(var1) != null) {
               throw new NoSuchObjectException("Object not exported.");
            }

            UnicastRemoteObject.unexportObject(var1, true);
         }

      } else {
         throw new NoSuchObjectException("Can only unexport a server object.");
      }
   }

   public Object narrow(Object var1, Class var2) throws ClassCastException {
      Object var3 = null;
      if (var1 == null) {
         return null;
      } else if (var2 == null) {
         throw new NullPointerException("invalid argument");
      } else {
         try {
            if (var2.isAssignableFrom(var1.getClass())) {
               return var1;
            } else if (var2.isInterface() && var2 != Serializable.class && var2 != Externalizable.class) {
               org.omg.CORBA.Object var4 = (org.omg.CORBA.Object)var1;
               String var7 = RepositoryId.createForAnyType(var2);
               if (var4._is_a(var7)) {
                  return Utility.loadStub(var4, var2);
               } else {
                  throw new ClassCastException("Object is not of remote type " + var2.getName());
               }
            } else {
               throw new ClassCastException("Class " + var2.getName() + " is not a valid remote interface");
            }
         } catch (Exception var6) {
            ClassCastException var5 = new ClassCastException();
            var5.initCause(var6);
            throw var5;
         }
      }
   }

   public void connect(Remote var1, Remote var2) throws RemoteException {
      if (var1 != null && var2 != null) {
         ORB var3 = null;

         try {
            if (StubAdapter.isStub(var2)) {
               var3 = StubAdapter.getORB(var2);
            } else {
               Tie var4 = Util.getTie(var2);
               if (var4 != null) {
                  var3 = var4.orb();
               }
            }
         } catch (SystemException var7) {
            throw new RemoteException("'source' object not connected", var7);
         }

         boolean var10 = false;
         Tie var5 = null;
         if (StubAdapter.isStub(var1)) {
            var10 = true;
         } else {
            var5 = Util.getTie(var1);
            if (var5 != null) {
               var10 = true;
            }
         }

         if (!var10) {
            if (var3 != null) {
               throw new RemoteException("'source' object exported to IIOP, 'target' is JRMP");
            }
         } else {
            if (var3 == null) {
               throw new RemoteException("'source' object is JRMP, 'target' is IIOP");
            }

            try {
               if (var5 != null) {
                  try {
                     ORB var6 = var5.orb();
                     if (var6 == var3) {
                        return;
                     }

                     throw new RemoteException("'target' object was already connected");
                  } catch (SystemException var8) {
                     var5.orb(var3);
                  }
               } else {
                  StubAdapter.connect(var1, var3);
               }
            } catch (SystemException var9) {
               throw new RemoteException("'target' object was already connected", var9);
            }
         }

      } else {
         throw new NullPointerException("invalid argument");
      }
   }
}
