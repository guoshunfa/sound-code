package sun.rmi.transport;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.dgc.VMID;
import java.rmi.server.ObjID;
import java.rmi.server.Unreferenced;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.NewThreadAction;
import sun.rmi.server.Dispatcher;

public final class Target {
   private final ObjID id;
   private final boolean permanent;
   private final WeakRef weakImpl;
   private volatile Dispatcher disp;
   private final Remote stub;
   private final Vector<VMID> refSet = new Vector();
   private final Hashtable<VMID, SequenceEntry> sequenceTable = new Hashtable(5);
   private final AccessControlContext acc;
   private final ClassLoader ccl;
   private int callCount = 0;
   private boolean removed = false;
   private volatile Transport exportedTransport = null;
   private static int nextThreadNum = 0;

   public Target(Remote var1, Dispatcher var2, Remote var3, ObjID var4, boolean var5) {
      this.weakImpl = new WeakRef(var1, ObjectTable.reapQueue);
      this.disp = var2;
      this.stub = var3;
      this.id = var4;
      this.acc = AccessController.getContext();
      ClassLoader var6 = Thread.currentThread().getContextClassLoader();
      ClassLoader var7 = var1.getClass().getClassLoader();
      if (checkLoaderAncestry(var6, var7)) {
         this.ccl = var6;
      } else {
         this.ccl = var7;
      }

      this.permanent = var5;
      if (var5) {
         this.pinImpl();
      }

   }

   private static boolean checkLoaderAncestry(ClassLoader var0, ClassLoader var1) {
      if (var1 == null) {
         return true;
      } else if (var0 == null) {
         return false;
      } else {
         for(ClassLoader var2 = var0; var2 != null; var2 = var2.getParent()) {
            if (var2 == var1) {
               return true;
            }
         }

         return false;
      }
   }

   public Remote getStub() {
      return this.stub;
   }

   ObjectEndpoint getObjectEndpoint() {
      return new ObjectEndpoint(this.id, this.exportedTransport);
   }

   WeakRef getWeakImpl() {
      return this.weakImpl;
   }

   Dispatcher getDispatcher() {
      return this.disp;
   }

   AccessControlContext getAccessControlContext() {
      return this.acc;
   }

   ClassLoader getContextClassLoader() {
      return this.ccl;
   }

   Remote getImpl() {
      return (Remote)this.weakImpl.get();
   }

   boolean isPermanent() {
      return this.permanent;
   }

   synchronized void pinImpl() {
      this.weakImpl.pin();
   }

   synchronized void unpinImpl() {
      if (!this.permanent && this.refSet.isEmpty()) {
         this.weakImpl.unpin();
      }

   }

   void setExportedTransport(Transport var1) {
      if (this.exportedTransport == null) {
         this.exportedTransport = var1;
      }

   }

   synchronized void referenced(long var1, VMID var3) {
      SequenceEntry var4 = (SequenceEntry)this.sequenceTable.get(var3);
      if (var4 == null) {
         this.sequenceTable.put(var3, new SequenceEntry(var1));
      } else {
         if (var4.sequenceNum >= var1) {
            return;
         }

         var4.update(var1);
      }

      if (!this.refSet.contains(var3)) {
         this.pinImpl();
         if (this.getImpl() == null) {
            return;
         }

         if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
            DGCImpl.dgcLog.log(Log.VERBOSE, "add to dirty set: " + var3);
         }

         this.refSet.addElement(var3);
         DGCImpl.getDGCImpl().registerTarget(var3, this);
      }

   }

   synchronized void unreferenced(long var1, VMID var3, boolean var4) {
      SequenceEntry var5 = (SequenceEntry)this.sequenceTable.get(var3);
      if (var5 != null && var5.sequenceNum <= var1) {
         if (var4) {
            var5.retain(var1);
         } else if (!var5.keep) {
            this.sequenceTable.remove(var3);
         }

         if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
            DGCImpl.dgcLog.log(Log.VERBOSE, "remove from dirty set: " + var3);
         }

         this.refSetRemove(var3);
      }
   }

   private synchronized void refSetRemove(VMID var1) {
      DGCImpl.getDGCImpl().unregisterTarget(var1, this);
      if (this.refSet.removeElement(var1) && this.refSet.isEmpty()) {
         if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
            DGCImpl.dgcLog.log(Log.VERBOSE, "reference set is empty: target = " + this);
         }

         Remote var2 = this.getImpl();
         if (var2 instanceof Unreferenced) {
            Unreferenced var3 = (Unreferenced)var2;
            ((Thread)AccessController.doPrivileged((PrivilegedAction)(new NewThreadAction(() -> {
               Thread.currentThread().setContextClassLoader(this.ccl);
               AccessController.doPrivileged(() -> {
                  var3.unreferenced();
                  return null;
               }, this.acc);
            }, "Unreferenced-" + nextThreadNum++, false, true)))).start();
         }

         this.unpinImpl();
      }

   }

   synchronized boolean unexport(boolean var1) {
      if (!var1 && this.callCount != 0 && this.disp != null) {
         return false;
      } else {
         this.disp = null;
         this.unpinImpl();
         DGCImpl var2 = DGCImpl.getDGCImpl();
         Enumeration var3 = this.refSet.elements();

         while(var3.hasMoreElements()) {
            VMID var4 = (VMID)var3.nextElement();
            var2.unregisterTarget(var4, this);
         }

         return true;
      }
   }

   synchronized void markRemoved() {
      if (this.removed) {
         throw new AssertionError();
      } else {
         this.removed = true;
         if (!this.permanent && this.callCount == 0) {
            ObjectTable.decrementKeepAliveCount();
         }

         if (this.exportedTransport != null) {
            this.exportedTransport.targetUnexported();
         }

      }
   }

   synchronized void incrementCallCount() throws NoSuchObjectException {
      if (this.disp != null) {
         ++this.callCount;
      } else {
         throw new NoSuchObjectException("object not accepting new calls");
      }
   }

   synchronized void decrementCallCount() {
      if (--this.callCount < 0) {
         throw new Error("internal error: call count less than zero");
      } else {
         if (!this.permanent && this.removed && this.callCount == 0) {
            ObjectTable.decrementKeepAliveCount();
         }

      }
   }

   boolean isEmpty() {
      return this.refSet.isEmpty();
   }

   public synchronized void vmidDead(VMID var1) {
      if (DGCImpl.dgcLog.isLoggable(Log.BRIEF)) {
         DGCImpl.dgcLog.log(Log.BRIEF, "removing endpoint " + var1 + " from reference set");
      }

      this.sequenceTable.remove(var1);
      this.refSetRemove(var1);
   }
}
