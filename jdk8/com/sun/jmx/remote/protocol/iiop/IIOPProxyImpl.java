package com.sun.jmx.remote.protocol.iiop;

import com.sun.jmx.remote.internal.IIOPProxy;
import java.io.SerializablePermission;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Properties;
import javax.rmi.PortableRemoteObject;
import javax.rmi.CORBA.Stub;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.Delegate;

public class IIOPProxyImpl implements IIOPProxy {
   private static final AccessControlContext STUB_ACC;

   public boolean isStub(Object var1) {
      return var1 instanceof Stub;
   }

   public Object getDelegate(Object var1) {
      return ((Stub)var1)._get_delegate();
   }

   public void setDelegate(Object var1, Object var2) {
      ((Stub)var1)._set_delegate((Delegate)var2);
   }

   public Object getOrb(Object var1) {
      try {
         return ((Stub)var1)._orb();
      } catch (BAD_OPERATION var3) {
         throw new UnsupportedOperationException(var3);
      }
   }

   public void connect(Object var1, Object var2) throws RemoteException {
      ((Stub)var1).connect((ORB)var2);
   }

   public boolean isOrb(Object var1) {
      return var1 instanceof ORB;
   }

   public Object createOrb(String[] var1, Properties var2) {
      return ORB.init(var1, var2);
   }

   public Object stringToObject(Object var1, String var2) {
      return ((ORB)var1).string_to_object(var2);
   }

   public String objectToString(Object var1, Object var2) {
      return ((ORB)var1).object_to_string((org.omg.CORBA.Object)var2);
   }

   public <T> T narrow(Object var1, Class<T> var2) {
      return PortableRemoteObject.narrow(var1, var2);
   }

   public void exportObject(Remote var1) throws RemoteException {
      PortableRemoteObject.exportObject(var1);
   }

   public void unexportObject(Remote var1) throws NoSuchObjectException {
      PortableRemoteObject.unexportObject(var1);
   }

   public Remote toStub(final Remote var1) throws NoSuchObjectException {
      if (System.getSecurityManager() == null) {
         return PortableRemoteObject.toStub(var1);
      } else {
         try {
            return (Remote)AccessController.doPrivileged(new PrivilegedExceptionAction<Remote>() {
               public Remote run() throws Exception {
                  return PortableRemoteObject.toStub(var1);
               }
            }, STUB_ACC);
         } catch (PrivilegedActionException var3) {
            if (var3.getException() instanceof NoSuchObjectException) {
               throw (NoSuchObjectException)var3.getException();
            } else {
               throw new RuntimeException("Unexpected exception type", var3.getException());
            }
         }
      }
   }

   static {
      Permissions var0 = new Permissions();
      var0.add(new SerializablePermission("enableSubclassImplementation"));
      STUB_ACC = new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain((CodeSource)null, var0)});
   }
}
