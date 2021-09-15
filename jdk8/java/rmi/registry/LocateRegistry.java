package java.rmi.registry;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.ObjID;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteRef;
import sun.rmi.registry.RegistryImpl;
import sun.rmi.server.UnicastRef;
import sun.rmi.server.UnicastRef2;
import sun.rmi.server.Util;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;

public final class LocateRegistry {
   private LocateRegistry() {
   }

   public static Registry getRegistry() throws RemoteException {
      return getRegistry((String)null, 1099);
   }

   public static Registry getRegistry(int var0) throws RemoteException {
      return getRegistry((String)null, var0);
   }

   public static Registry getRegistry(String var0) throws RemoteException {
      return getRegistry(var0, 1099);
   }

   public static Registry getRegistry(String var0, int var1) throws RemoteException {
      return getRegistry(var0, var1, (RMIClientSocketFactory)null);
   }

   public static Registry getRegistry(String var0, int var1, RMIClientSocketFactory var2) throws RemoteException {
      Object var3 = null;
      if (var1 <= 0) {
         var1 = 1099;
      }

      if (var0 == null || var0.length() == 0) {
         try {
            var0 = InetAddress.getLocalHost().getHostAddress();
         } catch (Exception var6) {
            var0 = "";
         }
      }

      LiveRef var4 = new LiveRef(new ObjID(0), new TCPEndpoint(var0, var1, var2, (RMIServerSocketFactory)null), false);
      Object var5 = var2 == null ? new UnicastRef(var4) : new UnicastRef2(var4);
      return (Registry)Util.createProxy(RegistryImpl.class, (RemoteRef)var5, false);
   }

   public static Registry createRegistry(int var0) throws RemoteException {
      return new RegistryImpl(var0);
   }

   public static Registry createRegistry(int var0, RMIClientSocketFactory var1, RMIServerSocketFactory var2) throws RemoteException {
      return new RegistryImpl(var0, var1, var2);
   }
}
