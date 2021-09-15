package java.rmi.activation;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.RemoteRef;
import java.rmi.server.UID;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;

public class ActivationID implements Serializable {
   private transient Activator activator;
   private transient UID uid = new UID();
   private static final long serialVersionUID = -4608673054848209235L;
   private static final AccessControlContext NOPERMS_ACC;

   public ActivationID(Activator var1) {
      this.activator = var1;
   }

   public Remote activate(boolean var1) throws ActivationException, UnknownObjectException, RemoteException {
      try {
         final MarshalledObject var2 = this.activator.activate(this, var1);
         return (Remote)AccessController.doPrivileged(new PrivilegedExceptionAction<Remote>() {
            public Remote run() throws IOException, ClassNotFoundException {
               return (Remote)var2.get();
            }
         }, NOPERMS_ACC);
      } catch (PrivilegedActionException var4) {
         Exception var3 = var4.getException();
         if (var3 instanceof RemoteException) {
            throw (RemoteException)var3;
         } else {
            throw new UnmarshalException("activation failed", var3);
         }
      }
   }

   public int hashCode() {
      return this.uid.hashCode();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ActivationID)) {
         return false;
      } else {
         ActivationID var2 = (ActivationID)var1;
         return this.uid.equals(var2.uid) && this.activator.equals(var2.activator);
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException, ClassNotFoundException {
      var1.writeObject(this.uid);
      RemoteRef var2;
      if (this.activator instanceof RemoteObject) {
         var2 = ((RemoteObject)this.activator).getRef();
      } else {
         if (!Proxy.isProxyClass(this.activator.getClass())) {
            throw new InvalidObjectException("unexpected activator type");
         }

         InvocationHandler var3 = Proxy.getInvocationHandler(this.activator);
         if (!(var3 instanceof RemoteObjectInvocationHandler)) {
            throw new InvalidObjectException("unexpected invocation handler");
         }

         var2 = ((RemoteObjectInvocationHandler)var3).getRef();
      }

      var1.writeUTF(var2.getRefClass(var1));
      var2.writeExternal(var1);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.uid = (UID)var1.readObject();

      try {
         Class var2 = Class.forName("sun.rmi.server." + var1.readUTF()).asSubclass(RemoteRef.class);
         RemoteRef var3 = (RemoteRef)var2.newInstance();
         var3.readExternal(var1);
         this.activator = (Activator)Proxy.newProxyInstance((ClassLoader)null, new Class[]{Activator.class}, new RemoteObjectInvocationHandler(var3));
      } catch (InstantiationException var4) {
         throw (IOException)(new InvalidObjectException("Unable to create remote reference")).initCause(var4);
      } catch (IllegalAccessException var5) {
         throw (IOException)(new InvalidObjectException("Unable to create remote reference")).initCause(var5);
      }
   }

   static {
      Permissions var0 = new Permissions();
      ProtectionDomain[] var1 = new ProtectionDomain[]{new ProtectionDomain((CodeSource)null, var0)};
      NOPERMS_ACC = new AccessControlContext(var1);
   }
}
