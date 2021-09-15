package java.rmi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.server.UnicastServerRef2;
import sun.rmi.transport.ObjectTable;

public class UnicastRemoteObject extends RemoteServer {
   private int port;
   private RMIClientSocketFactory csf;
   private RMIServerSocketFactory ssf;
   private static final long serialVersionUID = 4974527148936298033L;

   protected UnicastRemoteObject() throws RemoteException {
      this(0);
   }

   protected UnicastRemoteObject(int var1) throws RemoteException {
      this.port = 0;
      this.csf = null;
      this.ssf = null;
      this.port = var1;
      exportObject(this, var1);
   }

   protected UnicastRemoteObject(int var1, RMIClientSocketFactory var2, RMIServerSocketFactory var3) throws RemoteException {
      this.port = 0;
      this.csf = null;
      this.ssf = null;
      this.port = var1;
      this.csf = var2;
      this.ssf = var3;
      exportObject(this, var1, var2, var3);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.reexport();
   }

   public Object clone() throws CloneNotSupportedException {
      try {
         UnicastRemoteObject var1 = (UnicastRemoteObject)super.clone();
         var1.reexport();
         return var1;
      } catch (RemoteException var2) {
         throw new ServerCloneException("Clone failed", var2);
      }
   }

   private void reexport() throws RemoteException {
      if (this.csf == null && this.ssf == null) {
         exportObject(this, this.port);
      } else {
         exportObject(this, this.port, this.csf, this.ssf);
      }

   }

   /** @deprecated */
   @Deprecated
   public static RemoteStub exportObject(Remote var0) throws RemoteException {
      return (RemoteStub)exportObject(var0, new UnicastServerRef(true));
   }

   public static Remote exportObject(Remote var0, int var1) throws RemoteException {
      return exportObject(var0, new UnicastServerRef(var1));
   }

   public static Remote exportObject(Remote var0, int var1, RMIClientSocketFactory var2, RMIServerSocketFactory var3) throws RemoteException {
      return exportObject(var0, new UnicastServerRef2(var1, var2, var3));
   }

   public static boolean unexportObject(Remote var0, boolean var1) throws NoSuchObjectException {
      return ObjectTable.unexportObject(var0, var1);
   }

   private static Remote exportObject(Remote var0, UnicastServerRef var1) throws RemoteException {
      if (var0 instanceof UnicastRemoteObject) {
         ((UnicastRemoteObject)var0).ref = var1;
      }

      return var1.exportObject(var0, (Object)null, false);
   }
}
