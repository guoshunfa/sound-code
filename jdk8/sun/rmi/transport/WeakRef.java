package sun.rmi.transport;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import sun.rmi.runtime.Log;

class WeakRef extends WeakReference<Object> {
   private int hashValue;
   private Object strongRef = null;

   public WeakRef(Object var1) {
      super(var1);
      this.setHashValue(var1);
   }

   public WeakRef(Object var1, ReferenceQueue<Object> var2) {
      super(var1, var2);
      this.setHashValue(var1);
   }

   public synchronized void pin() {
      if (this.strongRef == null) {
         this.strongRef = this.get();
         if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
            DGCImpl.dgcLog.log(Log.VERBOSE, "strongRef = " + this.strongRef);
         }
      }

   }

   public synchronized void unpin() {
      if (this.strongRef != null) {
         if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
            DGCImpl.dgcLog.log(Log.VERBOSE, "strongRef = " + this.strongRef);
         }

         this.strongRef = null;
      }

   }

   private void setHashValue(Object var1) {
      if (var1 != null) {
         this.hashValue = System.identityHashCode(var1);
      } else {
         this.hashValue = 0;
      }

   }

   public int hashCode() {
      return this.hashValue;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof WeakRef) {
         if (var1 == this) {
            return true;
         } else {
            Object var2 = this.get();
            return var2 != null && var2 == ((WeakRef)var1).get();
         }
      } else {
         return false;
      }
   }
}
