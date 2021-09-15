package sun.rmi.transport;

import java.net.SocketPermission;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.dgc.DGC;
import java.rmi.dgc.Lease;
import java.rmi.dgc.VMID;
import java.rmi.server.LogStream;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UID;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import sun.misc.ObjectInputFilter;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.RuntimeUtil;
import sun.rmi.server.UnicastRef;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.server.Util;
import sun.security.action.GetLongAction;
import sun.security.action.GetPropertyAction;

final class DGCImpl implements DGC {
   static final Log dgcLog = Log.getLog("sun.rmi.dgc", "dgc", LogStream.parseLevel((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.rmi.dgc.logLevel")))));
   private static final long leaseValue = (Long)AccessController.doPrivileged((PrivilegedAction)(new GetLongAction("java.rmi.dgc.leaseValue", 600000L)));
   private static final long leaseCheckInterval;
   private static final ScheduledExecutorService scheduler;
   private static DGCImpl dgc;
   private Map<VMID, DGCImpl.LeaseInfo> leaseTable;
   private Future<?> checker;
   private static final String DGC_FILTER_PROPNAME = "sun.rmi.transport.dgcFilter";
   private static int DGC_MAX_DEPTH;
   private static int DGC_MAX_ARRAY_SIZE;
   private static final ObjectInputFilter dgcFilter;

   static DGCImpl getDGCImpl() {
      return dgc;
   }

   private static ObjectInputFilter initDgcFilter() {
      ObjectInputFilter var0 = null;
      String var1 = System.getProperty("sun.rmi.transport.dgcFilter");
      if (var1 == null) {
         var1 = Security.getProperty("sun.rmi.transport.dgcFilter");
      }

      if (var1 != null) {
         var0 = ObjectInputFilter.Config.createFilter(var1);
         if (dgcLog.isLoggable(Log.BRIEF)) {
            dgcLog.log(Log.BRIEF, "dgcFilter = " + var0);
         }
      }

      return var0;
   }

   private DGCImpl() {
      this.leaseTable = new HashMap();
      this.checker = null;
   }

   public Lease dirty(ObjID[] var1, long var2, Lease var4) {
      VMID var5 = var4.getVMID();
      long var6 = leaseValue;
      if (dgcLog.isLoggable(Log.VERBOSE)) {
         dgcLog.log(Log.VERBOSE, "vmid = " + var5);
      }

      if (var5 == null) {
         var5 = new VMID();
         if (dgcLog.isLoggable(Log.BRIEF)) {
            String var8;
            try {
               var8 = RemoteServer.getClientHost();
            } catch (ServerNotActiveException var13) {
               var8 = "<unknown host>";
            }

            dgcLog.log(Log.BRIEF, " assigning vmid " + var5 + " to client " + var8);
         }
      }

      var4 = new Lease(var5, var6);
      synchronized(this.leaseTable) {
         DGCImpl.LeaseInfo var9 = (DGCImpl.LeaseInfo)this.leaseTable.get(var5);
         if (var9 == null) {
            this.leaseTable.put(var5, new DGCImpl.LeaseInfo(var5, var6));
            if (this.checker == null) {
               this.checker = scheduler.scheduleWithFixedDelay(new Runnable() {
                  public void run() {
                     DGCImpl.this.checkLeases();
                  }
               }, leaseCheckInterval, leaseCheckInterval, TimeUnit.MILLISECONDS);
            }
         } else {
            var9.renew(var6);
         }
      }

      ObjID[] var14 = var1;
      int var15 = var1.length;

      for(int var10 = 0; var10 < var15; ++var10) {
         ObjID var11 = var14[var10];
         if (dgcLog.isLoggable(Log.VERBOSE)) {
            dgcLog.log(Log.VERBOSE, "id = " + var11 + ", vmid = " + var5 + ", duration = " + var6);
         }

         ObjectTable.referenced(var11, var2, var5);
      }

      return var4;
   }

   public void clean(ObjID[] var1, long var2, VMID var4, boolean var5) {
      ObjID[] var6 = var1;
      int var7 = var1.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         ObjID var9 = var6[var8];
         if (dgcLog.isLoggable(Log.VERBOSE)) {
            dgcLog.log(Log.VERBOSE, "id = " + var9 + ", vmid = " + var4 + ", strong = " + var5);
         }

         ObjectTable.unreferenced(var9, var2, var4, var5);
      }

   }

   void registerTarget(VMID var1, Target var2) {
      synchronized(this.leaseTable) {
         DGCImpl.LeaseInfo var4 = (DGCImpl.LeaseInfo)this.leaseTable.get(var1);
         if (var4 == null) {
            var2.vmidDead(var1);
         } else {
            var4.notifySet.add(var2);
         }

      }
   }

   void unregisterTarget(VMID var1, Target var2) {
      synchronized(this.leaseTable) {
         DGCImpl.LeaseInfo var4 = (DGCImpl.LeaseInfo)this.leaseTable.get(var1);
         if (var4 != null) {
            var4.notifySet.remove(var2);
         }

      }
   }

   private void checkLeases() {
      long var1 = System.currentTimeMillis();
      ArrayList var3 = new ArrayList();
      synchronized(this.leaseTable) {
         Iterator var5 = this.leaseTable.values().iterator();

         while(var5.hasNext()) {
            DGCImpl.LeaseInfo var6 = (DGCImpl.LeaseInfo)var5.next();
            if (var6.expired(var1)) {
               var3.add(var6);
               var5.remove();
            }
         }

         if (this.leaseTable.isEmpty()) {
            this.checker.cancel(false);
            this.checker = null;
         }
      }

      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         DGCImpl.LeaseInfo var9 = (DGCImpl.LeaseInfo)var4.next();
         Iterator var10 = var9.notifySet.iterator();

         while(var10.hasNext()) {
            Target var7 = (Target)var10.next();
            var7.vmidDead(var9.vmid);
         }
      }

   }

   private static ObjectInputFilter.Status checkInput(ObjectInputFilter.FilterInfo var0) {
      if (dgcFilter != null) {
         ObjectInputFilter.Status var1 = dgcFilter.checkInput(var0);
         if (var1 != ObjectInputFilter.Status.UNDECIDED) {
            return var1;
         }
      }

      if (var0.depth() > (long)DGC_MAX_DEPTH) {
         return ObjectInputFilter.Status.REJECTED;
      } else {
         Class var2 = var0.serialClass();
         if (var2 == null) {
            return ObjectInputFilter.Status.UNDECIDED;
         } else {
            while(var2.isArray()) {
               if (var0.arrayLength() >= 0L && var0.arrayLength() > (long)DGC_MAX_ARRAY_SIZE) {
                  return ObjectInputFilter.Status.REJECTED;
               }

               var2 = var2.getComponentType();
            }

            if (var2.isPrimitive()) {
               return ObjectInputFilter.Status.ALLOWED;
            } else {
               return var2 != ObjID.class && var2 != UID.class && var2 != VMID.class && var2 != Lease.class ? ObjectInputFilter.Status.REJECTED : ObjectInputFilter.Status.ALLOWED;
            }
         }
      }
   }

   // $FF: synthetic method
   DGCImpl(Object var1) {
      this();
   }

   static {
      leaseCheckInterval = (Long)AccessController.doPrivileged((PrivilegedAction)(new GetLongAction("sun.rmi.dgc.checkInterval", leaseValue / 2L)));
      scheduler = ((RuntimeUtil)AccessController.doPrivileged((PrivilegedAction)(new RuntimeUtil.GetInstanceAction()))).getScheduler();
      DGC_MAX_DEPTH = 5;
      DGC_MAX_ARRAY_SIZE = 10000;
      dgcFilter = (ObjectInputFilter)AccessController.doPrivileged(DGCImpl::initDgcFilter);
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            ClassLoader var1 = Thread.currentThread().getContextClassLoader();

            try {
               Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());

               try {
                  DGCImpl.dgc = new DGCImpl();
                  final ObjID var2 = new ObjID(2);
                  LiveRef var3 = new LiveRef(var2, 0);
                  final UnicastServerRef var4 = new UnicastServerRef(var3, (var0) -> {
                     return DGCImpl.checkInput(var0);
                  });
                  final Remote var5 = Util.createProxy(DGCImpl.class, new UnicastRef(var3), true);
                  var4.setSkeleton(DGCImpl.dgc);
                  Permissions var6 = new Permissions();
                  var6.add(new SocketPermission("*", "accept,resolve"));
                  ProtectionDomain[] var7 = new ProtectionDomain[]{new ProtectionDomain((CodeSource)null, var6)};
                  AccessControlContext var8 = new AccessControlContext(var7);
                  Target var9 = (Target)AccessController.doPrivileged(new PrivilegedAction<Target>() {
                     public Target run() {
                        return new Target(DGCImpl.dgc, var4, var5, var2, true);
                     }
                  }, var8);
                  ObjectTable.putTarget(var9);
               } catch (RemoteException var13) {
                  throw new Error("exception initializing server-side DGC", var13);
               }
            } finally {
               Thread.currentThread().setContextClassLoader(var1);
            }

            return null;
         }
      });
   }

   private static class LeaseInfo {
      VMID vmid;
      long expiration;
      Set<Target> notifySet = new HashSet();

      LeaseInfo(VMID var1, long var2) {
         this.vmid = var1;
         this.expiration = System.currentTimeMillis() + var2;
      }

      synchronized void renew(long var1) {
         long var3 = System.currentTimeMillis() + var1;
         if (var3 > this.expiration) {
            this.expiration = var3;
         }

      }

      boolean expired(long var1) {
         if (this.expiration < var1) {
            if (DGCImpl.dgcLog.isLoggable(Log.BRIEF)) {
               DGCImpl.dgcLog.log(Log.BRIEF, this.vmid.toString());
            }

            return true;
         } else {
            return false;
         }
      }
   }
}
