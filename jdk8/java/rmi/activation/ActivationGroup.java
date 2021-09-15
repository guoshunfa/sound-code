package java.rmi.activation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.rmi.MarshalledObject;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.UnicastRemoteObject;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.rmi.server.ActivationGroupImpl;
import sun.security.action.GetIntegerAction;

public abstract class ActivationGroup extends UnicastRemoteObject implements ActivationInstantiator {
   private ActivationGroupID groupID;
   private ActivationMonitor monitor;
   private long incarnation;
   private static ActivationGroup currGroup;
   private static ActivationGroupID currGroupID;
   private static ActivationSystem currSystem;
   private static boolean canCreate = true;
   private static final long serialVersionUID = -7696947875314805420L;

   protected ActivationGroup(ActivationGroupID var1) throws RemoteException {
      this.groupID = var1;
   }

   public boolean inactiveObject(ActivationID var1) throws ActivationException, UnknownObjectException, RemoteException {
      this.getMonitor().inactiveObject(var1);
      return true;
   }

   public abstract void activeObject(ActivationID var1, Remote var2) throws ActivationException, UnknownObjectException, RemoteException;

   public static synchronized ActivationGroup createGroup(ActivationGroupID var0, ActivationGroupDesc var1, long var2) throws ActivationException {
      SecurityManager var4 = System.getSecurityManager();
      if (var4 != null) {
         var4.checkSetFactory();
      }

      if (currGroup != null) {
         throw new ActivationException("group already exists");
      } else if (!canCreate) {
         throw new ActivationException("group deactivated and cannot be recreated");
      } else {
         try {
            String var5 = var1.getClassName();
            Class var7 = ActivationGroupImpl.class;
            Class var6;
            if (var5 != null && !var5.equals(var7.getName())) {
               Class var8;
               try {
                  var8 = RMIClassLoader.loadClass(var1.getLocation(), var5);
               } catch (Exception var10) {
                  throw new ActivationException("Could not load group implementation class", var10);
               }

               if (!ActivationGroup.class.isAssignableFrom(var8)) {
                  throw new ActivationException("group not correct class: " + var8.getName());
               }

               var6 = var8.asSubclass(ActivationGroup.class);
            } else {
               var6 = var7;
            }

            Constructor var14 = var6.getConstructor(ActivationGroupID.class, MarshalledObject.class);
            ActivationGroup var9 = (ActivationGroup)var14.newInstance(var0, var1.getData());
            currSystem = var0.getSystem();
            var9.incarnation = var2;
            var9.monitor = currSystem.activeGroup(var0, var9, var2);
            currGroup = var9;
            currGroupID = var0;
            canCreate = false;
         } catch (InvocationTargetException var11) {
            var11.getTargetException().printStackTrace();
            throw new ActivationException("exception in group constructor", var11.getTargetException());
         } catch (ActivationException var12) {
            throw var12;
         } catch (Exception var13) {
            throw new ActivationException("exception creating group", var13);
         }

         return currGroup;
      }
   }

   public static synchronized ActivationGroupID currentGroupID() {
      return currGroupID;
   }

   static synchronized ActivationGroupID internalCurrentGroupID() throws ActivationException {
      if (currGroupID == null) {
         throw new ActivationException("nonexistent group");
      } else {
         return currGroupID;
      }
   }

   public static synchronized void setSystem(ActivationSystem var0) throws ActivationException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkSetFactory();
      }

      if (currSystem != null) {
         throw new ActivationException("activation system already set");
      } else {
         currSystem = var0;
      }
   }

   public static synchronized ActivationSystem getSystem() throws ActivationException {
      if (currSystem == null) {
         try {
            int var0 = (Integer)AccessController.doPrivileged((PrivilegedAction)(new GetIntegerAction("java.rmi.activation.port", 1098)));
            currSystem = (ActivationSystem)Naming.lookup("//:" + var0 + "/java.rmi.activation.ActivationSystem");
         } catch (Exception var1) {
            throw new ActivationException("unable to obtain ActivationSystem", var1);
         }
      }

      return currSystem;
   }

   protected void activeObject(ActivationID var1, MarshalledObject<? extends Remote> var2) throws ActivationException, UnknownObjectException, RemoteException {
      this.getMonitor().activeObject(var1, var2);
   }

   protected void inactiveGroup() throws UnknownGroupException, RemoteException {
      try {
         this.getMonitor().inactiveGroup(this.groupID, this.incarnation);
      } finally {
         destroyGroup();
      }

   }

   private ActivationMonitor getMonitor() throws RemoteException {
      Class var1 = ActivationGroup.class;
      synchronized(ActivationGroup.class) {
         if (this.monitor != null) {
            return this.monitor;
         }
      }

      throw new RemoteException("monitor not received");
   }

   private static synchronized void destroyGroup() {
      currGroup = null;
      currGroupID = null;
   }

   static synchronized ActivationGroup currentGroup() throws ActivationException {
      if (currGroup == null) {
         throw new ActivationException("group is not active");
      } else {
         return currGroup;
      }
   }
}
