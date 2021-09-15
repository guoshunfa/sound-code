package java.util.prefs;

import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

class MacOSXPreferencesFile {
   private static HashMap<String, WeakReference<MacOSXPreferencesFile>> cachedFiles;
   private static HashSet<MacOSXPreferencesFile> changedFiles;
   private static Timer timer;
   private static MacOSXPreferencesFile.FlushTask flushTimerTask;
   private static long flushDelay;
   private static long syncInterval;
   private String appName;
   private long user;
   private long host;
   private static long cfCurrentUser;
   private static long cfAnyUser;
   private static long cfCurrentHost;
   private static long cfAnyHost;

   String name() {
      return this.appName;
   }

   long user() {
      return this.user;
   }

   long host() {
      return this.host;
   }

   private MacOSXPreferencesFile(String var1, long var2, long var4) {
      this.appName = var1;
      this.user = var2;
      this.host = var4;
   }

   static synchronized MacOSXPreferencesFile getFile(String var0, boolean var1) {
      MacOSXPreferencesFile var2 = null;
      if (cachedFiles == null) {
         cachedFiles = new HashMap();
      }

      String var3 = var0 + String.valueOf(var1);
      WeakReference var4 = (WeakReference)cachedFiles.get(var3);
      if (var4 != null) {
         var2 = (MacOSXPreferencesFile)var4.get();
      }

      if (var2 == null) {
         var2 = new MacOSXPreferencesFile(var0, var1 ? cfCurrentUser : cfAnyUser, var1 ? cfAnyHost : cfCurrentHost);
         cachedFiles.put(var3, new WeakReference(var2));
      }

      initSyncTimerIfNeeded();
      return var2;
   }

   static synchronized boolean syncWorld() {
      boolean var0 = true;
      if (cachedFiles != null && !cachedFiles.isEmpty()) {
         Iterator var1 = cachedFiles.values().iterator();

         while(var1.hasNext()) {
            WeakReference var2 = (WeakReference)var1.next();
            MacOSXPreferencesFile var3 = (MacOSXPreferencesFile)var2.get();
            if (var3 != null) {
               if (!var3.synchronize()) {
                  var0 = false;
               }
            } else {
               var1.remove();
            }
         }
      }

      if (flushTimerTask != null) {
         flushTimerTask.cancel();
         flushTimerTask = null;
      }

      if (changedFiles != null) {
         changedFiles.clear();
      }

      return var0;
   }

   static synchronized boolean syncUser() {
      boolean var0 = true;
      Iterator var1;
      if (cachedFiles != null && !cachedFiles.isEmpty()) {
         var1 = cachedFiles.values().iterator();

         label41:
         while(true) {
            while(true) {
               if (!var1.hasNext()) {
                  break label41;
               }

               WeakReference var2 = (WeakReference)var1.next();
               MacOSXPreferencesFile var3 = (MacOSXPreferencesFile)var2.get();
               if (var3 != null && var3.user == cfCurrentUser) {
                  if (!var3.synchronize()) {
                     var0 = false;
                  }
               } else {
                  var1.remove();
               }
            }
         }
      }

      if (changedFiles != null) {
         var1 = changedFiles.iterator();

         while(var1.hasNext()) {
            MacOSXPreferencesFile var4 = (MacOSXPreferencesFile)var1.next();
            if (var4 != null && var4.user == cfCurrentUser) {
               var1.remove();
            }
         }
      }

      return var0;
   }

   static synchronized boolean flushUser() {
      boolean var0 = true;
      if (changedFiles != null && !changedFiles.isEmpty()) {
         Iterator var1 = changedFiles.iterator();

         while(var1.hasNext()) {
            MacOSXPreferencesFile var2 = (MacOSXPreferencesFile)var1.next();
            if (var2.user == cfCurrentUser) {
               if (!var2.synchronize()) {
                  var0 = false;
               } else {
                  var1.remove();
               }
            }
         }
      }

      return var0;
   }

   static synchronized boolean flushWorld() {
      boolean var0 = true;
      if (changedFiles != null && !changedFiles.isEmpty()) {
         Iterator var1 = changedFiles.iterator();

         while(var1.hasNext()) {
            MacOSXPreferencesFile var2 = (MacOSXPreferencesFile)var1.next();
            if (!var2.synchronize()) {
               var0 = false;
            }
         }

         changedFiles.clear();
      }

      if (flushTimerTask != null) {
         flushTimerTask.cancel();
         flushTimerTask = null;
      }

      return var0;
   }

   private void markChanged() {
      if (changedFiles == null) {
         changedFiles = new HashSet();
      }

      changedFiles.add(this);
      if (flushTimerTask == null) {
         flushTimerTask = new MacOSXPreferencesFile.FlushTask();
         timer().schedule(flushTimerTask, flushDelay() * 1000L);
      }

   }

   private static synchronized long flushDelay() {
      if (flushDelay == -1L) {
         try {
            flushDelay = (long)Math.max(5, Integer.parseInt(System.getProperty("java.util.prefs.flushDelay", "60")));
         } catch (NumberFormatException var1) {
            flushDelay = 60L;
         }
      }

      return flushDelay;
   }

   private static synchronized void initSyncTimerIfNeeded() {
      if (syncInterval == -1L) {
         try {
            syncInterval = (long)Integer.parseInt(System.getProperty("java.util.prefs.syncInterval", "-2"));
            if (syncInterval >= 0L) {
               syncInterval = Math.max(5L, syncInterval);
            } else {
               syncInterval = -2L;
            }
         } catch (NumberFormatException var1) {
            syncInterval = -2L;
         }

         if (syncInterval > 0L) {
            timer().schedule(new TimerTask() {
               public void run() {
                  MacOSXPreferencesFile.syncWorld();
               }
            }, syncInterval * 1000L, syncInterval * 1000L);
         }
      }

   }

   private static synchronized Timer timer() {
      if (timer == null) {
         timer = new Timer(true);
         Thread var0 = new Thread() {
            public void run() {
               MacOSXPreferencesFile.flushWorld();
            }
         };
         var0.setContextClassLoader((ClassLoader)null);
         Runtime.getRuntime().addShutdownHook(var0);
      }

      return timer;
   }

   boolean addNode(String var1) {
      Class var2 = MacOSXPreferencesFile.class;
      synchronized(MacOSXPreferencesFile.class) {
         this.markChanged();
         return addNode(var1, this.appName, this.user, this.host);
      }
   }

   void removeNode(String var1) {
      Class var2 = MacOSXPreferencesFile.class;
      synchronized(MacOSXPreferencesFile.class) {
         this.markChanged();
         removeNode(var1, this.appName, this.user, this.host);
      }
   }

   boolean addChildToNode(String var1, String var2) {
      Class var3 = MacOSXPreferencesFile.class;
      synchronized(MacOSXPreferencesFile.class) {
         this.markChanged();
         return addChildToNode(var1, var2 + "/", this.appName, this.user, this.host);
      }
   }

   void removeChildFromNode(String var1, String var2) {
      Class var3 = MacOSXPreferencesFile.class;
      synchronized(MacOSXPreferencesFile.class) {
         this.markChanged();
         removeChildFromNode(var1, var2 + "/", this.appName, this.user, this.host);
      }
   }

   void addKeyToNode(String var1, String var2, String var3) {
      Class var4 = MacOSXPreferencesFile.class;
      synchronized(MacOSXPreferencesFile.class) {
         this.markChanged();
         addKeyToNode(var1, var2, var3, this.appName, this.user, this.host);
      }
   }

   void removeKeyFromNode(String var1, String var2) {
      Class var3 = MacOSXPreferencesFile.class;
      synchronized(MacOSXPreferencesFile.class) {
         this.markChanged();
         removeKeyFromNode(var1, var2, this.appName, this.user, this.host);
      }
   }

   String getKeyFromNode(String var1, String var2) {
      Class var3 = MacOSXPreferencesFile.class;
      synchronized(MacOSXPreferencesFile.class) {
         return getKeyFromNode(var1, var2, this.appName, this.user, this.host);
      }
   }

   String[] getChildrenForNode(String var1) {
      Class var2 = MacOSXPreferencesFile.class;
      synchronized(MacOSXPreferencesFile.class) {
         return getChildrenForNode(var1, this.appName, this.user, this.host);
      }
   }

   String[] getKeysForNode(String var1) {
      Class var2 = MacOSXPreferencesFile.class;
      synchronized(MacOSXPreferencesFile.class) {
         return getKeysForNode(var1, this.appName, this.user, this.host);
      }
   }

   boolean synchronize() {
      Class var1 = MacOSXPreferencesFile.class;
      synchronized(MacOSXPreferencesFile.class) {
         return synchronize(this.appName, this.user, this.host);
      }
   }

   private static final native boolean addNode(String var0, String var1, long var2, long var4);

   private static final native void removeNode(String var0, String var1, long var2, long var4);

   private static final native boolean addChildToNode(String var0, String var1, String var2, long var3, long var5);

   private static final native void removeChildFromNode(String var0, String var1, String var2, long var3, long var5);

   private static final native void addKeyToNode(String var0, String var1, String var2, String var3, long var4, long var6);

   private static final native void removeKeyFromNode(String var0, String var1, String var2, long var3, long var5);

   private static final native String getKeyFromNode(String var0, String var1, String var2, long var3, long var5);

   private static final native String[] getChildrenForNode(String var0, String var1, long var2, long var4);

   private static final native String[] getKeysForNode(String var0, String var1, long var2, long var4);

   private static final native boolean synchronize(String var0, long var1, long var3);

   private static final native long currentUser();

   private static final native long anyUser();

   private static final native long currentHost();

   private static final native long anyHost();

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("osx");
            return null;
         }
      });
      timer = null;
      flushTimerTask = null;
      flushDelay = -1L;
      syncInterval = -1L;
      cfCurrentUser = currentUser();
      cfAnyUser = anyUser();
      cfCurrentHost = currentHost();
      cfAnyHost = anyHost();
   }

   private class SyncTask extends TimerTask {
      public void run() {
         MacOSXPreferencesFile.syncWorld();
      }
   }

   private class FlushTask extends TimerTask {
      private FlushTask() {
      }

      public void run() {
         MacOSXPreferencesFile.flushWorld();
      }

      // $FF: synthetic method
      FlushTask(Object var2) {
         this();
      }
   }
}
