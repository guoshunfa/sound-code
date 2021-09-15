package sun.rmi.transport;

import java.io.InvalidClassException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.net.SocketPermission;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.rmi.dgc.DGC;
import java.rmi.dgc.Lease;
import java.rmi.dgc.VMID;
import java.rmi.server.ObjID;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sun.misc.GC;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.NewThreadAction;
import sun.rmi.server.UnicastRef;
import sun.rmi.server.Util;
import sun.security.action.GetLongAction;

final class DGCClient {
   private static long nextSequenceNum = Long.MIN_VALUE;
   private static VMID vmid = new VMID();
   private static final long leaseValue = (Long)AccessController.doPrivileged((PrivilegedAction)(new GetLongAction("java.rmi.dgc.leaseValue", 600000L)));
   private static final long cleanInterval = (Long)AccessController.doPrivileged((PrivilegedAction)(new GetLongAction("sun.rmi.dgc.cleanInterval", 180000L)));
   private static final long gcInterval = (Long)AccessController.doPrivileged((PrivilegedAction)(new GetLongAction("sun.rmi.dgc.client.gcInterval", 3600000L)));
   private static final int dirtyFailureRetries = 5;
   private static final int cleanFailureRetries = 5;
   private static final ObjID[] emptyObjIDArray = new ObjID[0];
   private static final ObjID dgcID = new ObjID(2);
   private static final AccessControlContext SOCKET_ACC;

   private DGCClient() {
   }

   static void registerRefs(Endpoint var0, List<LiveRef> var1) {
      DGCClient.EndpointEntry var2;
      do {
         var2 = DGCClient.EndpointEntry.lookup(var0);
      } while(!var2.registerRefs(var1));

   }

   private static synchronized long getNextSequenceNum() {
      return (long)(nextSequenceNum++);
   }

   private static long computeRenewTime(long var0, long var2) {
      return var0 + var2 / 2L;
   }

   static {
      Permissions var0 = new Permissions();
      var0.add(new SocketPermission("*", "connect,resolve"));
      ProtectionDomain[] var1 = new ProtectionDomain[]{new ProtectionDomain((CodeSource)null, var0)};
      SOCKET_ACC = new AccessControlContext(var1);
   }

   private static class EndpointEntry {
      private Endpoint endpoint;
      private DGC dgc;
      private Map<LiveRef, DGCClient.EndpointEntry.RefEntry> refTable = new HashMap(5);
      private Set<DGCClient.EndpointEntry.RefEntry> invalidRefs = new HashSet(5);
      private boolean removed = false;
      private long renewTime = Long.MAX_VALUE;
      private long expirationTime = Long.MIN_VALUE;
      private int dirtyFailures = 0;
      private long dirtyFailureStartTime;
      private long dirtyFailureDuration;
      private Thread renewCleanThread;
      private boolean interruptible = false;
      private ReferenceQueue<LiveRef> refQueue = new ReferenceQueue();
      private Set<DGCClient.EndpointEntry.CleanRequest> pendingCleans = new HashSet(5);
      private static Map<Endpoint, DGCClient.EndpointEntry> endpointTable = new HashMap(5);
      private static GC.LatencyRequest gcLatencyRequest = null;

      public static DGCClient.EndpointEntry lookup(Endpoint var0) {
         synchronized(endpointTable) {
            DGCClient.EndpointEntry var2 = (DGCClient.EndpointEntry)endpointTable.get(var0);
            if (var2 == null) {
               var2 = new DGCClient.EndpointEntry(var0);
               endpointTable.put(var0, var2);
               if (gcLatencyRequest == null) {
                  gcLatencyRequest = GC.requestLatency(DGCClient.gcInterval);
               }
            }

            return var2;
         }
      }

      private EndpointEntry(Endpoint var1) {
         this.endpoint = var1;

         try {
            LiveRef var2 = new LiveRef(DGCClient.dgcID, var1, false);
            this.dgc = (DGC)Util.createProxy(DGCImpl.class, new UnicastRef(var2), true);
         } catch (RemoteException var3) {
            throw new Error("internal error creating DGC stub");
         }

         this.renewCleanThread = (Thread)AccessController.doPrivileged((PrivilegedAction)(new NewThreadAction(new DGCClient.EndpointEntry.RenewCleanThread(), "RenewClean-" + var1, true)));
         this.renewCleanThread.start();
      }

      public boolean registerRefs(List<LiveRef> var1) {
         assert !Thread.holdsLock(this);

         HashSet var2 = null;
         long var3;
         synchronized(this) {
            if (this.removed) {
               return false;
            }

            LiveRef var7;
            DGCClient.EndpointEntry.RefEntry var8;
            for(Iterator var6 = var1.iterator(); var6.hasNext(); var8.addInstanceToRefSet(var7)) {
               var7 = (LiveRef)var6.next();

               assert var7.getEndpoint().equals(this.endpoint);

               var8 = (DGCClient.EndpointEntry.RefEntry)this.refTable.get(var7);
               if (var8 == null) {
                  LiveRef var9 = (LiveRef)var7.clone();
                  var8 = new DGCClient.EndpointEntry.RefEntry(var9);
                  this.refTable.put(var9, var8);
                  if (var2 == null) {
                     var2 = new HashSet(5);
                  }

                  var2.add(var8);
               }
            }

            if (var2 == null) {
               return true;
            }

            var2.addAll(this.invalidRefs);
            this.invalidRefs.clear();
            var3 = DGCClient.getNextSequenceNum();
         }

         this.makeDirtyCall(var2, var3);
         return true;
      }

      private void removeRefEntry(DGCClient.EndpointEntry.RefEntry var1) {
         assert Thread.holdsLock(this);

         assert !this.removed;

         assert this.refTable.containsKey(var1.getRef());

         this.refTable.remove(var1.getRef());
         this.invalidRefs.remove(var1);
         if (this.refTable.isEmpty()) {
            synchronized(endpointTable) {
               endpointTable.remove(this.endpoint);
               Transport var3 = this.endpoint.getOutboundTransport();
               var3.free(this.endpoint);
               if (endpointTable.isEmpty()) {
                  assert gcLatencyRequest != null;

                  gcLatencyRequest.cancel();
                  gcLatencyRequest = null;
               }

               this.removed = true;
            }
         }

      }

      private void makeDirtyCall(Set<DGCClient.EndpointEntry.RefEntry> var1, long var2) {
         assert !Thread.holdsLock(this);

         ObjID[] var4;
         if (var1 != null) {
            var4 = createObjIDArray(var1);
         } else {
            var4 = DGCClient.emptyObjIDArray;
         }

         long var5 = System.currentTimeMillis();

         long var8;
         long var12;
         try {
            Lease var20 = this.dgc.dirty(var4, var2, new Lease(DGCClient.vmid, DGCClient.leaseValue));
            var8 = var20.getValue();
            long var10 = DGCClient.computeRenewTime(var5, var8);
            var12 = var5 + var8;
            synchronized(this) {
               this.dirtyFailures = 0;
               this.setRenewTime(var10);
               this.expirationTime = var12;
            }
         } catch (Exception var19) {
            Exception var7 = var19;
            var8 = System.currentTimeMillis();
            synchronized(this) {
               ++this.dirtyFailures;
               if (var7 instanceof UnmarshalException && var7.getCause() instanceof InvalidClassException) {
                  DGCImpl.dgcLog.log(Log.BRIEF, "InvalidClassException exception in DGC dirty call", var7);
                  return;
               }

               if (this.dirtyFailures == 1) {
                  this.dirtyFailureStartTime = var5;
                  this.dirtyFailureDuration = var8 - var5;
                  this.setRenewTime(var8);
               } else {
                  int var11 = this.dirtyFailures - 2;
                  if (var11 == 0) {
                     this.dirtyFailureDuration = Math.max(this.dirtyFailureDuration + (var8 - var5) >> 1, 1000L);
                  }

                  var12 = var8 + (this.dirtyFailureDuration << var11);
                  if (var12 >= this.expirationTime && this.dirtyFailures >= 5 && var12 >= this.dirtyFailureStartTime + DGCClient.leaseValue) {
                     this.setRenewTime(Long.MAX_VALUE);
                  } else {
                     this.setRenewTime(var12);
                  }
               }

               if (var1 != null) {
                  this.invalidRefs.addAll(var1);
                  Iterator var21 = var1.iterator();

                  while(var21.hasNext()) {
                     DGCClient.EndpointEntry.RefEntry var22 = (DGCClient.EndpointEntry.RefEntry)var21.next();
                     var22.markDirtyFailed();
                  }
               }

               if (this.renewTime >= this.expirationTime) {
                  this.invalidRefs.addAll(this.refTable.values());
               }
            }
         }

      }

      private void setRenewTime(long var1) {
         assert Thread.holdsLock(this);

         if (var1 < this.renewTime) {
            this.renewTime = var1;
            if (this.interruptible) {
               AccessController.doPrivileged(new PrivilegedAction<Void>() {
                  public Void run() {
                     EndpointEntry.this.renewCleanThread.interrupt();
                     return null;
                  }
               });
            }
         } else {
            this.renewTime = var1;
         }

      }

      private void processPhantomRefs(DGCClient.EndpointEntry.RefEntry.PhantomLiveRef var1) {
         assert Thread.holdsLock(this);

         HashSet var2 = null;
         HashSet var3 = null;

         do {
            DGCClient.EndpointEntry.RefEntry var4 = var1.getRefEntry();
            var4.removeInstanceFromRefSet(var1);
            if (var4.isRefSetEmpty()) {
               if (var4.hasDirtyFailed()) {
                  if (var2 == null) {
                     var2 = new HashSet(5);
                  }

                  var2.add(var4);
               } else {
                  if (var3 == null) {
                     var3 = new HashSet(5);
                  }

                  var3.add(var4);
               }

               this.removeRefEntry(var4);
            }
         } while((var1 = (DGCClient.EndpointEntry.RefEntry.PhantomLiveRef)this.refQueue.poll()) != null);

         if (var2 != null) {
            this.pendingCleans.add(new DGCClient.EndpointEntry.CleanRequest(createObjIDArray(var2), DGCClient.getNextSequenceNum(), true));
         }

         if (var3 != null) {
            this.pendingCleans.add(new DGCClient.EndpointEntry.CleanRequest(createObjIDArray(var3), DGCClient.getNextSequenceNum(), false));
         }

      }

      private void makeCleanCalls() {
         assert !Thread.holdsLock(this);

         Iterator var1 = this.pendingCleans.iterator();

         while(var1.hasNext()) {
            DGCClient.EndpointEntry.CleanRequest var2 = (DGCClient.EndpointEntry.CleanRequest)var1.next();

            try {
               this.dgc.clean(var2.objIDs, var2.sequenceNum, DGCClient.vmid, var2.strong);
               var1.remove();
            } catch (Exception var4) {
               if (++var2.failures >= 5) {
                  var1.remove();
               }
            }
         }

      }

      private static ObjID[] createObjIDArray(Set<DGCClient.EndpointEntry.RefEntry> var0) {
         ObjID[] var1 = new ObjID[var0.size()];
         Iterator var2 = var0.iterator();

         for(int var3 = 0; var3 < var1.length; ++var3) {
            var1[var3] = ((DGCClient.EndpointEntry.RefEntry)var2.next()).getRef().getObjID();
         }

         return var1;
      }

      private class RefEntry {
         private LiveRef ref;
         private Set<DGCClient.EndpointEntry.RefEntry.PhantomLiveRef> refSet = new HashSet(5);
         private boolean dirtyFailed = false;

         public RefEntry(LiveRef var2) {
            this.ref = var2;
         }

         public LiveRef getRef() {
            return this.ref;
         }

         public void addInstanceToRefSet(LiveRef var1) {
            assert Thread.holdsLock(EndpointEntry.this);

            assert var1.equals(this.ref);

            this.refSet.add(new DGCClient.EndpointEntry.RefEntry.PhantomLiveRef(var1));
         }

         public void removeInstanceFromRefSet(DGCClient.EndpointEntry.RefEntry.PhantomLiveRef var1) {
            assert Thread.holdsLock(EndpointEntry.this);

            assert this.refSet.contains(var1);

            this.refSet.remove(var1);
         }

         public boolean isRefSetEmpty() {
            assert Thread.holdsLock(EndpointEntry.this);

            return this.refSet.size() == 0;
         }

         public void markDirtyFailed() {
            assert Thread.holdsLock(EndpointEntry.this);

            this.dirtyFailed = true;
         }

         public boolean hasDirtyFailed() {
            assert Thread.holdsLock(EndpointEntry.this);

            return this.dirtyFailed;
         }

         private class PhantomLiveRef extends PhantomReference<LiveRef> {
            public PhantomLiveRef(LiveRef var2) {
               super(var2, EndpointEntry.this.refQueue);
            }

            public DGCClient.EndpointEntry.RefEntry getRefEntry() {
               return RefEntry.this;
            }
         }
      }

      private static class CleanRequest {
         final ObjID[] objIDs;
         final long sequenceNum;
         final boolean strong;
         int failures = 0;

         CleanRequest(ObjID[] var1, long var2, boolean var4) {
            this.objIDs = var1;
            this.sequenceNum = var2;
            this.strong = var4;
         }
      }

      private class RenewCleanThread implements Runnable {
         private RenewCleanThread() {
         }

         public void run() {
            do {
               DGCClient.EndpointEntry.RefEntry.PhantomLiveRef var3 = null;
               final boolean var4 = false;
               final Set var5 = null;
               final long var6 = Long.MIN_VALUE;
               long var1;
               long var9;
               synchronized(EndpointEntry.this) {
                  var9 = EndpointEntry.this.renewTime - System.currentTimeMillis();
                  var1 = Math.max(var9, 1L);
                  if (!EndpointEntry.this.pendingCleans.isEmpty()) {
                     var1 = Math.min(var1, DGCClient.cleanInterval);
                  }

                  EndpointEntry.this.interruptible = true;
               }

               try {
                  var3 = (DGCClient.EndpointEntry.RefEntry.PhantomLiveRef)EndpointEntry.this.refQueue.remove(var1);
               } catch (InterruptedException var13) {
               }

               synchronized(EndpointEntry.this) {
                  EndpointEntry.this.interruptible = false;
                  Thread.interrupted();
                  if (var3 != null) {
                     EndpointEntry.this.processPhantomRefs(var3);
                  }

                  var9 = System.currentTimeMillis();
                  if (var9 > EndpointEntry.this.renewTime) {
                     var4 = true;
                     if (!EndpointEntry.this.invalidRefs.isEmpty()) {
                        var5 = EndpointEntry.this.invalidRefs;
                        EndpointEntry.this.invalidRefs = new HashSet(5);
                     }

                     var6 = DGCClient.getNextSequenceNum();
                  }
               }

               AccessController.doPrivileged(new PrivilegedAction<Void>() {
                  public Void run() {
                     if (var4) {
                        EndpointEntry.this.makeDirtyCall(var5, var6);
                     }

                     if (!EndpointEntry.this.pendingCleans.isEmpty()) {
                        EndpointEntry.this.makeCleanCalls();
                     }

                     return null;
                  }
               }, DGCClient.SOCKET_ACC);
            } while(!EndpointEntry.this.removed || !EndpointEntry.this.pendingCleans.isEmpty());

         }

         // $FF: synthetic method
         RenewCleanThread(Object var2) {
            this();
         }
      }
   }
}
