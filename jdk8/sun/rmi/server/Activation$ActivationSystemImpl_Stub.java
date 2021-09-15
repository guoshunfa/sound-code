package sun.rmi.server;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.ActivationID;
import java.rmi.activation.ActivationInstantiator;
import java.rmi.activation.ActivationMonitor;
import java.rmi.activation.ActivationSystem;
import java.rmi.activation.UnknownGroupException;
import java.rmi.activation.UnknownObjectException;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;

public final class Activation$ActivationSystemImpl_Stub extends RemoteStub implements ActivationSystem, Remote {
   private static final long serialVersionUID = 2L;
   private static Method $method_activeGroup_0;
   private static Method $method_getActivationDesc_1;
   private static Method $method_getActivationGroupDesc_2;
   private static Method $method_registerGroup_3;
   private static Method $method_registerObject_4;
   private static Method $method_setActivationDesc_5;
   private static Method $method_setActivationGroupDesc_6;
   private static Method $method_shutdown_7;
   private static Method $method_unregisterGroup_8;
   private static Method $method_unregisterObject_9;
   // $FF: synthetic field
   static Class class$java$rmi$activation$ActivationSystem;
   // $FF: synthetic field
   static Class class$java$rmi$activation$ActivationGroupID;
   // $FF: synthetic field
   static Class class$java$rmi$activation$ActivationInstantiator;
   // $FF: synthetic field
   static Class class$java$rmi$activation$ActivationID;
   // $FF: synthetic field
   static Class class$java$rmi$activation$ActivationGroupDesc;
   // $FF: synthetic field
   static Class class$java$rmi$activation$ActivationDesc;

   static {
      try {
         $method_activeGroup_0 = (class$java$rmi$activation$ActivationSystem != null ? class$java$rmi$activation$ActivationSystem : (class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("activeGroup", class$java$rmi$activation$ActivationGroupID != null ? class$java$rmi$activation$ActivationGroupID : (class$java$rmi$activation$ActivationGroupID = class$("java.rmi.activation.ActivationGroupID")), class$java$rmi$activation$ActivationInstantiator != null ? class$java$rmi$activation$ActivationInstantiator : (class$java$rmi$activation$ActivationInstantiator = class$("java.rmi.activation.ActivationInstantiator")), Long.TYPE);
         $method_getActivationDesc_1 = (class$java$rmi$activation$ActivationSystem != null ? class$java$rmi$activation$ActivationSystem : (class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("getActivationDesc", class$java$rmi$activation$ActivationID != null ? class$java$rmi$activation$ActivationID : (class$java$rmi$activation$ActivationID = class$("java.rmi.activation.ActivationID")));
         $method_getActivationGroupDesc_2 = (class$java$rmi$activation$ActivationSystem != null ? class$java$rmi$activation$ActivationSystem : (class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("getActivationGroupDesc", class$java$rmi$activation$ActivationGroupID != null ? class$java$rmi$activation$ActivationGroupID : (class$java$rmi$activation$ActivationGroupID = class$("java.rmi.activation.ActivationGroupID")));
         $method_registerGroup_3 = (class$java$rmi$activation$ActivationSystem != null ? class$java$rmi$activation$ActivationSystem : (class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("registerGroup", class$java$rmi$activation$ActivationGroupDesc != null ? class$java$rmi$activation$ActivationGroupDesc : (class$java$rmi$activation$ActivationGroupDesc = class$("java.rmi.activation.ActivationGroupDesc")));
         $method_registerObject_4 = (class$java$rmi$activation$ActivationSystem != null ? class$java$rmi$activation$ActivationSystem : (class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("registerObject", class$java$rmi$activation$ActivationDesc != null ? class$java$rmi$activation$ActivationDesc : (class$java$rmi$activation$ActivationDesc = class$("java.rmi.activation.ActivationDesc")));
         $method_setActivationDesc_5 = (class$java$rmi$activation$ActivationSystem != null ? class$java$rmi$activation$ActivationSystem : (class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("setActivationDesc", class$java$rmi$activation$ActivationID != null ? class$java$rmi$activation$ActivationID : (class$java$rmi$activation$ActivationID = class$("java.rmi.activation.ActivationID")), class$java$rmi$activation$ActivationDesc != null ? class$java$rmi$activation$ActivationDesc : (class$java$rmi$activation$ActivationDesc = class$("java.rmi.activation.ActivationDesc")));
         $method_setActivationGroupDesc_6 = (class$java$rmi$activation$ActivationSystem != null ? class$java$rmi$activation$ActivationSystem : (class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("setActivationGroupDesc", class$java$rmi$activation$ActivationGroupID != null ? class$java$rmi$activation$ActivationGroupID : (class$java$rmi$activation$ActivationGroupID = class$("java.rmi.activation.ActivationGroupID")), class$java$rmi$activation$ActivationGroupDesc != null ? class$java$rmi$activation$ActivationGroupDesc : (class$java$rmi$activation$ActivationGroupDesc = class$("java.rmi.activation.ActivationGroupDesc")));
         $method_shutdown_7 = (class$java$rmi$activation$ActivationSystem != null ? class$java$rmi$activation$ActivationSystem : (class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("shutdown");
         $method_unregisterGroup_8 = (class$java$rmi$activation$ActivationSystem != null ? class$java$rmi$activation$ActivationSystem : (class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("unregisterGroup", class$java$rmi$activation$ActivationGroupID != null ? class$java$rmi$activation$ActivationGroupID : (class$java$rmi$activation$ActivationGroupID = class$("java.rmi.activation.ActivationGroupID")));
         $method_unregisterObject_9 = (class$java$rmi$activation$ActivationSystem != null ? class$java$rmi$activation$ActivationSystem : (class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("unregisterObject", class$java$rmi$activation$ActivationID != null ? class$java$rmi$activation$ActivationID : (class$java$rmi$activation$ActivationID = class$("java.rmi.activation.ActivationID")));
      } catch (NoSuchMethodException var0) {
         throw new NoSuchMethodError("stub class initialization failed");
      }
   }

   public Activation$ActivationSystemImpl_Stub(RemoteRef var1) {
      super(var1);
   }

   public ActivationMonitor activeGroup(ActivationGroupID var1, ActivationInstantiator var2, long var3) throws RemoteException, ActivationException, UnknownGroupException {
      try {
         Object var5 = super.ref.invoke(this, $method_activeGroup_0, new Object[]{var1, var2, new Long(var3)}, -4575843150759415294L);
         return (ActivationMonitor)var5;
      } catch (RuntimeException var6) {
         throw var6;
      } catch (RemoteException var7) {
         throw var7;
      } catch (ActivationException var8) {
         throw var8;
      } catch (Exception var9) {
         throw new UnexpectedException("undeclared checked exception", var9);
      }
   }

   // $FF: synthetic method
   static Class class$(String var0) {
      try {
         return Class.forName(var0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }

   public ActivationDesc getActivationDesc(ActivationID var1) throws RemoteException, ActivationException, UnknownObjectException {
      try {
         Object var2 = super.ref.invoke(this, $method_getActivationDesc_1, new Object[]{var1}, 4830055440982622087L);
         return (ActivationDesc)var2;
      } catch (RuntimeException var3) {
         throw var3;
      } catch (RemoteException var4) {
         throw var4;
      } catch (ActivationException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new UnexpectedException("undeclared checked exception", var6);
      }
   }

   public ActivationGroupDesc getActivationGroupDesc(ActivationGroupID var1) throws RemoteException, ActivationException, UnknownGroupException {
      try {
         Object var2 = super.ref.invoke(this, $method_getActivationGroupDesc_2, new Object[]{var1}, -8701843806548736528L);
         return (ActivationGroupDesc)var2;
      } catch (RuntimeException var3) {
         throw var3;
      } catch (RemoteException var4) {
         throw var4;
      } catch (ActivationException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new UnexpectedException("undeclared checked exception", var6);
      }
   }

   public ActivationGroupID registerGroup(ActivationGroupDesc var1) throws RemoteException, ActivationException {
      try {
         Object var2 = super.ref.invoke(this, $method_registerGroup_3, new Object[]{var1}, 6921515268192657754L);
         return (ActivationGroupID)var2;
      } catch (RuntimeException var3) {
         throw var3;
      } catch (RemoteException var4) {
         throw var4;
      } catch (ActivationException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new UnexpectedException("undeclared checked exception", var6);
      }
   }

   public ActivationID registerObject(ActivationDesc var1) throws RemoteException, ActivationException, UnknownGroupException {
      try {
         Object var2 = super.ref.invoke(this, $method_registerObject_4, new Object[]{var1}, -3006759798994351347L);
         return (ActivationID)var2;
      } catch (RuntimeException var3) {
         throw var3;
      } catch (RemoteException var4) {
         throw var4;
      } catch (ActivationException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new UnexpectedException("undeclared checked exception", var6);
      }
   }

   public ActivationDesc setActivationDesc(ActivationID var1, ActivationDesc var2) throws RemoteException, ActivationException, UnknownGroupException, UnknownObjectException {
      try {
         Object var3 = super.ref.invoke(this, $method_setActivationDesc_5, new Object[]{var1, var2}, 7128043237057180796L);
         return (ActivationDesc)var3;
      } catch (RuntimeException var4) {
         throw var4;
      } catch (RemoteException var5) {
         throw var5;
      } catch (ActivationException var6) {
         throw var6;
      } catch (Exception var7) {
         throw new UnexpectedException("undeclared checked exception", var7);
      }
   }

   public ActivationGroupDesc setActivationGroupDesc(ActivationGroupID var1, ActivationGroupDesc var2) throws RemoteException, ActivationException, UnknownGroupException {
      try {
         Object var3 = super.ref.invoke(this, $method_setActivationGroupDesc_6, new Object[]{var1, var2}, 1213918527826541191L);
         return (ActivationGroupDesc)var3;
      } catch (RuntimeException var4) {
         throw var4;
      } catch (RemoteException var5) {
         throw var5;
      } catch (ActivationException var6) {
         throw var6;
      } catch (Exception var7) {
         throw new UnexpectedException("undeclared checked exception", var7);
      }
   }

   public void shutdown() throws RemoteException {
      try {
         super.ref.invoke(this, $method_shutdown_7, (Object[])null, -7207851917985848402L);
      } catch (RuntimeException var2) {
         throw var2;
      } catch (RemoteException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new UnexpectedException("undeclared checked exception", var4);
      }
   }

   public void unregisterGroup(ActivationGroupID var1) throws RemoteException, ActivationException, UnknownGroupException {
      try {
         super.ref.invoke(this, $method_unregisterGroup_8, new Object[]{var1}, 3768097077835970701L);
      } catch (RuntimeException var3) {
         throw var3;
      } catch (RemoteException var4) {
         throw var4;
      } catch (ActivationException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new UnexpectedException("undeclared checked exception", var6);
      }
   }

   public void unregisterObject(ActivationID var1) throws RemoteException, ActivationException, UnknownObjectException {
      try {
         super.ref.invoke(this, $method_unregisterObject_9, new Object[]{var1}, -6843850585331411084L);
      } catch (RuntimeException var3) {
         throw var3;
      } catch (RemoteException var4) {
         throw var4;
      } catch (ActivationException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new UnexpectedException("undeclared checked exception", var6);
      }
   }
}
