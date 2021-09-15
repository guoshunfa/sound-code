package sun.awt;

import java.awt.AWTEvent;
import java.security.AccessController;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import sun.misc.ThreadGroupUtils;
import sun.util.logging.PlatformLogger;

public final class AWTAutoShutdown implements Runnable {
   private static final AWTAutoShutdown theInstance = new AWTAutoShutdown();
   private final Object mainLock = new Object();
   private final Object activationLock = new Object();
   private final Set<Thread> busyThreadSet = new HashSet(7);
   private boolean toolkitThreadBusy = false;
   private final Map<Object, Object> peerMap = new IdentityHashMap();
   private Thread blockerThread = null;
   private boolean timeoutPassed = false;
   private static final int SAFETY_TIMEOUT = 1000;

   private AWTAutoShutdown() {
   }

   public static AWTAutoShutdown getInstance() {
      return theInstance;
   }

   public static void notifyToolkitThreadBusy() {
      getInstance().setToolkitBusy(true);
   }

   public static void notifyToolkitThreadFree() {
      getInstance().setToolkitBusy(false);
   }

   public void notifyThreadBusy(Thread var1) {
      if (var1 != null) {
         synchronized(this.activationLock) {
            synchronized(this.mainLock) {
               if (this.blockerThread == null) {
                  this.activateBlockerThread();
               } else if (this.isReadyToShutdown()) {
                  this.mainLock.notifyAll();
                  this.timeoutPassed = false;
               }

               this.busyThreadSet.add(var1);
            }

         }
      }
   }

   public void notifyThreadFree(Thread var1) {
      if (var1 != null) {
         synchronized(this.activationLock) {
            synchronized(this.mainLock) {
               this.busyThreadSet.remove(var1);
               if (this.isReadyToShutdown()) {
                  this.mainLock.notifyAll();
                  this.timeoutPassed = false;
               }
            }

         }
      }
   }

   void notifyPeerMapUpdated() {
      synchronized(this.activationLock) {
         synchronized(this.mainLock) {
            if (!this.isReadyToShutdown() && this.blockerThread == null) {
               AccessController.doPrivileged(() -> {
                  this.activateBlockerThread();
                  return null;
               });
            } else {
               this.mainLock.notifyAll();
               this.timeoutPassed = false;
            }
         }

      }
   }

   private boolean isReadyToShutdown() {
      return !this.toolkitThreadBusy && this.peerMap.isEmpty() && this.busyThreadSet.isEmpty();
   }

   private void setToolkitBusy(boolean var1) {
      if (var1 != this.toolkitThreadBusy) {
         synchronized(this.activationLock) {
            synchronized(this.mainLock) {
               if (var1 != this.toolkitThreadBusy) {
                  if (var1) {
                     if (this.blockerThread == null) {
                        this.activateBlockerThread();
                     } else if (this.isReadyToShutdown()) {
                        this.mainLock.notifyAll();
                        this.timeoutPassed = false;
                     }

                     this.toolkitThreadBusy = var1;
                  } else {
                     this.toolkitThreadBusy = var1;
                     if (this.isReadyToShutdown()) {
                        this.mainLock.notifyAll();
                        this.timeoutPassed = false;
                     }
                  }
               }
            }
         }
      }

   }

   public void run() {
      Thread var1 = Thread.currentThread();
      boolean var2 = false;
      synchronized(this.mainLock) {
         try {
            this.mainLock.notifyAll();

            label108:
            while(true) {
               while(true) {
                  if (this.blockerThread != var1) {
                     break label108;
                  }

                  this.mainLock.wait();
                  this.timeoutPassed = false;

                  while(this.isReadyToShutdown()) {
                     if (this.timeoutPassed) {
                        this.timeoutPassed = false;
                        this.blockerThread = null;
                        break;
                     }

                     this.timeoutPassed = true;
                     this.mainLock.wait(1000L);
                  }
               }
            }
         } catch (InterruptedException var10) {
            var2 = true;
         } finally {
            if (this.blockerThread == var1) {
               this.blockerThread = null;
            }

         }
      }

      if (!var2) {
         AppContext.stopEventDispatchThreads();
      }

   }

   static AWTEvent getShutdownEvent() {
      return new AWTEvent(getInstance(), 0) {
      };
   }

   private void activateBlockerThread() {
      Thread var1 = new Thread(ThreadGroupUtils.getRootThreadGroup(), this, "AWT-Shutdown");
      var1.setContextClassLoader((ClassLoader)null);
      var1.setDaemon(false);
      this.blockerThread = var1;
      var1.start();

      try {
         this.mainLock.wait();
      } catch (InterruptedException var3) {
         System.err.println("AWT blocker activation interrupted:");
         var3.printStackTrace();
      }

   }

   final void registerPeer(Object var1, Object var2) {
      synchronized(this.activationLock) {
         synchronized(this.mainLock) {
            this.peerMap.put(var1, var2);
            this.notifyPeerMapUpdated();
         }

      }
   }

   final void unregisterPeer(Object var1, Object var2) {
      synchronized(this.activationLock) {
         synchronized(this.mainLock) {
            if (this.peerMap.get(var1) == var2) {
               this.peerMap.remove(var1);
               this.notifyPeerMapUpdated();
            }
         }

      }
   }

   final Object getPeer(Object var1) {
      synchronized(this.activationLock) {
         Object var10000;
         synchronized(this.mainLock) {
            var10000 = this.peerMap.get(var1);
         }

         return var10000;
      }
   }

   final void dumpPeers(PlatformLogger var1) {
      if (var1.isLoggable(PlatformLogger.Level.FINE)) {
         synchronized(this.activationLock) {
            synchronized(this.mainLock) {
               var1.fine("Mapped peers:");
               Iterator var4 = this.peerMap.keySet().iterator();

               while(true) {
                  if (!var4.hasNext()) {
                     break;
                  }

                  Object var5 = var4.next();
                  var1.fine(var5 + "->" + this.peerMap.get(var5));
               }
            }
         }
      }

   }
}
