package sun.awt.image;

import java.awt.image.BufferStrategy;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

public abstract class VSyncedBSManager {
   private static VSyncedBSManager theInstance;
   private static final boolean vSyncLimit = Boolean.valueOf((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.java2d.vsynclimit", "true"))));

   private static VSyncedBSManager getInstance(boolean var0) {
      if (theInstance == null && var0) {
         theInstance = (VSyncedBSManager)(vSyncLimit ? new VSyncedBSManager.SingleVSyncedBSMgr() : new VSyncedBSManager.NoLimitVSyncBSMgr());
      }

      return theInstance;
   }

   abstract boolean checkAllowed(BufferStrategy var1);

   abstract void relinquishVsync(BufferStrategy var1);

   public static boolean vsyncAllowed(BufferStrategy var0) {
      VSyncedBSManager var1 = getInstance(true);
      return var1.checkAllowed(var0);
   }

   public static synchronized void releaseVsync(BufferStrategy var0) {
      VSyncedBSManager var1 = getInstance(false);
      if (var1 != null) {
         var1.relinquishVsync(var0);
      }

   }

   private static final class SingleVSyncedBSMgr extends VSyncedBSManager {
      private WeakReference<BufferStrategy> strategy;

      private SingleVSyncedBSMgr() {
      }

      public synchronized boolean checkAllowed(BufferStrategy var1) {
         if (this.strategy != null) {
            BufferStrategy var2 = (BufferStrategy)this.strategy.get();
            if (var2 != null) {
               return var2 == var1;
            }
         }

         this.strategy = new WeakReference(var1);
         return true;
      }

      public synchronized void relinquishVsync(BufferStrategy var1) {
         if (this.strategy != null) {
            BufferStrategy var2 = (BufferStrategy)this.strategy.get();
            if (var2 == var1) {
               this.strategy.clear();
               this.strategy = null;
            }
         }

      }

      // $FF: synthetic method
      SingleVSyncedBSMgr(Object var1) {
         this();
      }
   }

   private static final class NoLimitVSyncBSMgr extends VSyncedBSManager {
      private NoLimitVSyncBSMgr() {
      }

      boolean checkAllowed(BufferStrategy var1) {
         return true;
      }

      void relinquishVsync(BufferStrategy var1) {
      }

      // $FF: synthetic method
      NoLimitVSyncBSMgr(Object var1) {
         this();
      }
   }
}
