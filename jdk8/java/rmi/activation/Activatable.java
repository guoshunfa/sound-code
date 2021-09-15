package java.rmi.activation;

import java.rmi.MarshalledObject;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteServer;
import sun.rmi.server.ActivatableRef;
import sun.rmi.server.ActivatableServerRef;
import sun.rmi.transport.ObjectTable;

public abstract class Activatable extends RemoteServer {
   private ActivationID id;
   private static final long serialVersionUID = -3120617863591563455L;

   protected Activatable(String var1, MarshalledObject<?> var2, boolean var3, int var4) throws ActivationException, RemoteException {
      this.id = exportObject(this, var1, var2, var3, var4);
   }

   protected Activatable(String var1, MarshalledObject<?> var2, boolean var3, int var4, RMIClientSocketFactory var5, RMIServerSocketFactory var6) throws ActivationException, RemoteException {
      this.id = exportObject(this, var1, var2, var3, var4, var5, var6);
   }

   protected Activatable(ActivationID var1, int var2) throws RemoteException {
      this.id = var1;
      exportObject(this, var1, var2);
   }

   protected Activatable(ActivationID var1, int var2, RMIClientSocketFactory var3, RMIServerSocketFactory var4) throws RemoteException {
      this.id = var1;
      exportObject(this, var1, var2, var3, var4);
   }

   protected ActivationID getID() {
      return this.id;
   }

   public static Remote register(ActivationDesc var0) throws UnknownGroupException, ActivationException, RemoteException {
      ActivationID var1 = ActivationGroup.getSystem().registerObject(var0);
      return ActivatableRef.getStub(var0, var1);
   }

   public static boolean inactive(ActivationID var0) throws UnknownObjectException, ActivationException, RemoteException {
      return ActivationGroup.currentGroup().inactiveObject(var0);
   }

   public static void unregister(ActivationID var0) throws UnknownObjectException, ActivationException, RemoteException {
      ActivationGroup.getSystem().unregisterObject(var0);
   }

   public static ActivationID exportObject(Remote var0, String var1, MarshalledObject<?> var2, boolean var3, int var4) throws ActivationException, RemoteException {
      return exportObject(var0, var1, var2, var3, var4, (RMIClientSocketFactory)null, (RMIServerSocketFactory)null);
   }

   public static ActivationID exportObject(Remote var0, String var1, MarshalledObject<?> var2, boolean var3, int var4, RMIClientSocketFactory var5, RMIServerSocketFactory var6) throws ActivationException, RemoteException {
      ActivationDesc var7 = new ActivationDesc(var0.getClass().getName(), var1, var2, var3);
      ActivationSystem var8 = ActivationGroup.getSystem();
      ActivationID var9 = var8.registerObject(var7);

      try {
         exportObject(var0, var9, var4, var5, var6);
      } catch (RemoteException var13) {
         try {
            var8.unregisterObject(var9);
         } catch (Exception var12) {
         }

         throw var13;
      }

      ActivationGroup.currentGroup().activeObject(var9, var0);
      return var9;
   }

   public static Remote exportObject(Remote var0, ActivationID var1, int var2) throws RemoteException {
      return exportObject(var0, new ActivatableServerRef(var1, var2));
   }

   public static Remote exportObject(Remote var0, ActivationID var1, int var2, RMIClientSocketFactory var3, RMIServerSocketFactory var4) throws RemoteException {
      return exportObject(var0, new ActivatableServerRef(var1, var2, var3, var4));
   }

   public static boolean unexportObject(Remote var0, boolean var1) throws NoSuchObjectException {
      return ObjectTable.unexportObject(var0, var1);
   }

   private static Remote exportObject(Remote var0, ActivatableServerRef var1) throws RemoteException {
      if (var0 instanceof Activatable) {
         ((Activatable)var0).ref = var1;
      }

      return var1.exportObject(var0, (Object)null, false);
   }
}
