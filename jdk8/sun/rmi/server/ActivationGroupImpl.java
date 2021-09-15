package sun.rmi.server;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.rmi.MarshalledObject;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.ActivationID;
import java.rmi.activation.UnknownObjectException;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import sun.rmi.registry.RegistryImpl;

public class ActivationGroupImpl extends ActivationGroup {
   private static final long serialVersionUID = 5758693559430427303L;
   private final Hashtable<ActivationID, ActivationGroupImpl.ActiveEntry> active = new Hashtable();
   private boolean groupInactive = false;
   private final ActivationGroupID groupID;
   private final List<ActivationID> lockedIDs = new ArrayList();

   public ActivationGroupImpl(ActivationGroupID var1, MarshalledObject<?> var2) throws RemoteException {
      super(var1);
      this.groupID = var1;
      unexportObject(this, true);
      ActivationGroupImpl.ServerSocketFactoryImpl var3 = new ActivationGroupImpl.ServerSocketFactoryImpl();
      UnicastRemoteObject.exportObject(this, 0, (RMIClientSocketFactory)null, var3);
      if (System.getSecurityManager() == null) {
         try {
            System.setSecurityManager(new SecurityManager());
         } catch (Exception var5) {
            throw new RemoteException("unable to set security manager", var5);
         }
      }

   }

   private void acquireLock(ActivationID var1) {
      while(true) {
         ActivationID var2;
         synchronized(this.lockedIDs) {
            int var4 = this.lockedIDs.indexOf(var1);
            if (var4 < 0) {
               this.lockedIDs.add(var1);
               return;
            }

            var2 = (ActivationID)this.lockedIDs.get(var4);
         }

         synchronized(var2) {
            label75:
            synchronized(this.lockedIDs) {
               int var5 = this.lockedIDs.indexOf(var2);
               if (var5 >= 0) {
                  ActivationID var6 = (ActivationID)this.lockedIDs.get(var5);
                  if (var6 != var2) {
                     continue;
                  }
                  break label75;
               }
               continue;
            }

            try {
               var2.wait();
            } catch (InterruptedException var9) {
            }
         }
      }
   }

   private void releaseLock(ActivationID var1) {
      synchronized(this.lockedIDs) {
         var1 = (ActivationID)this.lockedIDs.remove(this.lockedIDs.indexOf(var1));
      }

      synchronized(var1) {
         var1.notifyAll();
      }
   }

   public MarshalledObject<? extends Remote> newInstance(final ActivationID var1, final ActivationDesc var2) throws ActivationException, RemoteException {
      RegistryImpl.checkAccess("ActivationInstantiator.newInstance");
      if (!this.groupID.equals(var2.getGroupID())) {
         throw new ActivationException("newInstance in wrong group");
      } else {
         MarshalledObject var4;
         try {
            this.acquireLock(var1);
            synchronized(this) {
               if (this.groupInactive) {
                  throw new InactiveGroupException("group is inactive");
               }
            }

            ActivationGroupImpl.ActiveEntry var3 = (ActivationGroupImpl.ActiveEntry)this.active.get(var1);
            if (var3 == null) {
               String var26 = var2.getClassName();
               final Class var5 = RMIClassLoader.loadClass(var2.getLocation(), var26).asSubclass(Remote.class);
               Remote var6 = null;
               final Thread var7 = Thread.currentThread();
               final ClassLoader var8 = var7.getContextClassLoader();
               ClassLoader var9 = var5.getClassLoader();
               final ClassLoader var10 = covers(var9, var8) ? var9 : var8;

               try {
                  var6 = (Remote)AccessController.doPrivileged(new PrivilegedExceptionAction<Remote>() {
                     public Remote run() throws InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
                        Constructor var1x = var5.getDeclaredConstructor(ActivationID.class, MarshalledObject.class);
                        var1x.setAccessible(true);

                        Remote var2x;
                        try {
                           var7.setContextClassLoader(var10);
                           var2x = (Remote)var1x.newInstance(var1, var2.getData());
                        } finally {
                           var7.setContextClassLoader(var8);
                        }

                        return var2x;
                     }
                  });
               } catch (PrivilegedActionException var20) {
                  Exception var12 = var20.getException();
                  if (var12 instanceof InstantiationException) {
                     throw (InstantiationException)var12;
                  }

                  if (var12 instanceof NoSuchMethodException) {
                     throw (NoSuchMethodException)var12;
                  }

                  if (var12 instanceof IllegalAccessException) {
                     throw (IllegalAccessException)var12;
                  }

                  if (var12 instanceof InvocationTargetException) {
                     throw (InvocationTargetException)var12;
                  }

                  if (var12 instanceof RuntimeException) {
                     throw (RuntimeException)var12;
                  }

                  if (var12 instanceof Error) {
                     throw (Error)var12;
                  }
               }

               var3 = new ActivationGroupImpl.ActiveEntry(var6);
               this.active.put(var1, var3);
               MarshalledObject var11 = var3.mobj;
               return var11;
            }

            var4 = var3.mobj;
         } catch (NoSuchMethodError | NoSuchMethodException var22) {
            throw new ActivationException("Activatable object must provide an activation constructor", var22);
         } catch (InvocationTargetException var23) {
            throw new ActivationException("exception in object constructor", var23.getTargetException());
         } catch (Exception var24) {
            throw new ActivationException("unable to activate object", var24);
         } finally {
            this.releaseLock(var1);
            this.checkInactiveGroup();
         }

         return var4;
      }
   }

   public boolean inactiveObject(ActivationID var1) throws ActivationException, UnknownObjectException, RemoteException {
      try {
         this.acquireLock(var1);
         synchronized(this) {
            if (this.groupInactive) {
               throw new ActivationException("group is inactive");
            }
         }

         ActivationGroupImpl.ActiveEntry var2 = (ActivationGroupImpl.ActiveEntry)this.active.get(var1);
         if (var2 == null) {
            throw new UnknownObjectException("object not active");
         }

         try {
            if (!Activatable.unexportObject(var2.impl, false)) {
               boolean var3 = false;
               return var3;
            }
         } catch (NoSuchObjectException var11) {
         }

         try {
            super.inactiveObject(var1);
         } catch (UnknownObjectException var9) {
         }

         this.active.remove(var1);
      } finally {
         this.releaseLock(var1);
         this.checkInactiveGroup();
      }

      return true;
   }

   private void checkInactiveGroup() {
      boolean var1 = false;
      synchronized(this) {
         if (this.active.size() == 0 && this.lockedIDs.size() == 0 && !this.groupInactive) {
            this.groupInactive = true;
            var1 = true;
         }
      }

      if (var1) {
         try {
            super.inactiveGroup();
         } catch (Exception var5) {
         }

         try {
            UnicastRemoteObject.unexportObject(this, true);
         } catch (NoSuchObjectException var4) {
         }
      }

   }

   public void activeObject(ActivationID var1, Remote var2) throws ActivationException, UnknownObjectException, RemoteException {
      try {
         this.acquireLock(var1);
         synchronized(this) {
            if (this.groupInactive) {
               throw new ActivationException("group is inactive");
            }
         }

         if (!this.active.contains(var1)) {
            ActivationGroupImpl.ActiveEntry var3 = new ActivationGroupImpl.ActiveEntry(var2);
            this.active.put(var1, var3);

            try {
               super.activeObject(var1, var3.mobj);
            } catch (RemoteException var9) {
            }
         }
      } finally {
         this.releaseLock(var1);
         this.checkInactiveGroup();
      }

   }

   private static boolean covers(ClassLoader var0, ClassLoader var1) {
      if (var1 == null) {
         return true;
      } else if (var0 == null) {
         return false;
      } else {
         while(var0 != var1) {
            var0 = var0.getParent();
            if (var0 == null) {
               return false;
            }
         }

         return true;
      }
   }

   private static class ActiveEntry {
      Remote impl;
      MarshalledObject<Remote> mobj;

      ActiveEntry(Remote var1) throws ActivationException {
         this.impl = var1;

         try {
            this.mobj = new MarshalledObject(var1);
         } catch (IOException var3) {
            throw new ActivationException("failed to marshal remote object", var3);
         }
      }
   }

   private static class ServerSocketFactoryImpl implements RMIServerSocketFactory {
      private ServerSocketFactoryImpl() {
      }

      public ServerSocket createServerSocket(int var1) throws IOException {
         RMISocketFactory var2 = RMISocketFactory.getSocketFactory();
         if (var2 == null) {
            var2 = RMISocketFactory.getDefaultSocketFactory();
         }

         return var2.createServerSocket(var1);
      }

      // $FF: synthetic method
      ServerSocketFactoryImpl(Object var1) {
         this();
      }
   }
}
