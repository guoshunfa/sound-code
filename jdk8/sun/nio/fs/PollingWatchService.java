package sun.nio.fs;

import com.sun.nio.file.SensitivityWatchEventModifier;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

class PollingWatchService extends AbstractWatchService {
   private final Map<Object, PollingWatchService.PollingWatchKey> map = new HashMap();
   private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
      public Thread newThread(Runnable var1) {
         Thread var2 = new Thread(var1);
         var2.setDaemon(true);
         return var2;
      }
   });

   WatchKey register(final Path var1, WatchEvent.Kind<?>[] var2, WatchEvent.Modifier... var3) throws IOException {
      final HashSet var4 = new HashSet(var2.length);
      WatchEvent.Kind[] var5 = var2;
      int var6 = var2.length;

      int var7;
      for(var7 = 0; var7 < var6; ++var7) {
         WatchEvent.Kind var8 = var5[var7];
         if (var8 != StandardWatchEventKinds.ENTRY_CREATE && var8 != StandardWatchEventKinds.ENTRY_MODIFY && var8 != StandardWatchEventKinds.ENTRY_DELETE) {
            if (var8 != StandardWatchEventKinds.OVERFLOW) {
               if (var8 == null) {
                  throw new NullPointerException("An element in event set is 'null'");
               }

               throw new UnsupportedOperationException(var8.name());
            }
         } else {
            var4.add(var8);
         }
      }

      if (var4.isEmpty()) {
         throw new IllegalArgumentException("No events to register");
      } else {
         final SensitivityWatchEventModifier var11 = SensitivityWatchEventModifier.MEDIUM;
         if (var3.length > 0) {
            WatchEvent.Modifier[] var12 = var3;
            var7 = var3.length;

            for(int var14 = 0; var14 < var7; ++var14) {
               WatchEvent.Modifier var9 = var12[var14];
               if (var9 == null) {
                  throw new NullPointerException();
               }

               if (!(var9 instanceof SensitivityWatchEventModifier)) {
                  throw new UnsupportedOperationException("Modifier not supported");
               }

               var11 = (SensitivityWatchEventModifier)var9;
            }
         }

         if (!this.isOpen()) {
            throw new ClosedWatchServiceException();
         } else {
            try {
               return (WatchKey)AccessController.doPrivileged(new PrivilegedExceptionAction<PollingWatchService.PollingWatchKey>() {
                  public PollingWatchService.PollingWatchKey run() throws IOException {
                     return PollingWatchService.this.doPrivilegedRegister(var1, var4, var11);
                  }
               });
            } catch (PrivilegedActionException var10) {
               Throwable var13 = var10.getCause();
               if (var13 != null && var13 instanceof IOException) {
                  throw (IOException)var13;
               } else {
                  throw new AssertionError(var10);
               }
            }
         }
      }
   }

   private PollingWatchService.PollingWatchKey doPrivilegedRegister(Path var1, Set<? extends WatchEvent.Kind<?>> var2, SensitivityWatchEventModifier var3) throws IOException {
      BasicFileAttributes var4 = Files.readAttributes(var1, BasicFileAttributes.class);
      if (!var4.isDirectory()) {
         throw new NotDirectoryException(var1.toString());
      } else {
         Object var5 = var4.fileKey();
         if (var5 == null) {
            throw new AssertionError("File keys must be supported");
         } else {
            synchronized(this.closeLock()) {
               if (!this.isOpen()) {
                  throw new ClosedWatchServiceException();
               } else {
                  PollingWatchService.PollingWatchKey var7;
                  synchronized(this.map) {
                     var7 = (PollingWatchService.PollingWatchKey)this.map.get(var5);
                     if (var7 == null) {
                        var7 = new PollingWatchService.PollingWatchKey(var1, this, var5);
                        this.map.put(var5, var7);
                     } else {
                        var7.disable();
                     }
                  }

                  var7.enable(var2, (long)var3.sensitivityValueInSeconds());
                  return var7;
               }
            }
         }
      }
   }

   void implClose() throws IOException {
      synchronized(this.map) {
         Iterator var2 = this.map.entrySet().iterator();

         while(true) {
            if (!var2.hasNext()) {
               this.map.clear();
               break;
            }

            Map.Entry var3 = (Map.Entry)var2.next();
            PollingWatchService.PollingWatchKey var4 = (PollingWatchService.PollingWatchKey)var3.getValue();
            var4.disable();
            var4.invalidate();
         }
      }

      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            PollingWatchService.this.scheduledExecutor.shutdown();
            return null;
         }
      });
   }

   private class PollingWatchKey extends AbstractWatchKey {
      private final Object fileKey;
      private Set<? extends WatchEvent.Kind<?>> events;
      private ScheduledFuture<?> poller;
      private volatile boolean valid;
      private int tickCount;
      private Map<Path, PollingWatchService.CacheEntry> entries;

      PollingWatchKey(Path var2, PollingWatchService var3, Object var4) throws IOException {
         super(var2, var3);
         this.fileKey = var4;
         this.valid = true;
         this.tickCount = 0;
         this.entries = new HashMap();

         try {
            DirectoryStream var5 = Files.newDirectoryStream(var2);
            Throwable var6 = null;

            try {
               Iterator var7 = var5.iterator();

               while(var7.hasNext()) {
                  Path var8 = (Path)var7.next();
                  long var9 = Files.getLastModifiedTime(var8, LinkOption.NOFOLLOW_LINKS).toMillis();
                  this.entries.put(var8.getFileName(), new PollingWatchService.CacheEntry(var9, this.tickCount));
               }
            } catch (Throwable var19) {
               var6 = var19;
               throw var19;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var18) {
                        var6.addSuppressed(var18);
                     }
                  } else {
                     var5.close();
                  }
               }

            }

         } catch (DirectoryIteratorException var21) {
            throw var21.getCause();
         }
      }

      Object fileKey() {
         return this.fileKey;
      }

      public boolean isValid() {
         return this.valid;
      }

      void invalidate() {
         this.valid = false;
      }

      void enable(Set<? extends WatchEvent.Kind<?>> var1, long var2) {
         synchronized(this) {
            this.events = var1;
            Runnable var5 = new Runnable() {
               public void run() {
                  PollingWatchKey.this.poll();
               }
            };
            this.poller = PollingWatchService.this.scheduledExecutor.scheduleAtFixedRate(var5, var2, var2, TimeUnit.SECONDS);
         }
      }

      void disable() {
         synchronized(this) {
            if (this.poller != null) {
               this.poller.cancel(false);
            }

         }
      }

      public void cancel() {
         this.valid = false;
         synchronized(PollingWatchService.this.map) {
            PollingWatchService.this.map.remove(this.fileKey());
         }

         this.disable();
      }

      synchronized void poll() {
         if (this.valid) {
            ++this.tickCount;
            DirectoryStream var1 = null;

            try {
               var1 = Files.newDirectoryStream(this.watchable());
            } catch (IOException var17) {
               this.cancel();
               this.signal();
               return;
            }

            Iterator var2;
            try {
               var2 = var1.iterator();

               while(var2.hasNext()) {
                  Path var3 = (Path)var2.next();
                  long var4 = 0L;

                  try {
                     var4 = Files.getLastModifiedTime(var3, LinkOption.NOFOLLOW_LINKS).toMillis();
                  } catch (IOException var18) {
                     continue;
                  }

                  PollingWatchService.CacheEntry var6 = (PollingWatchService.CacheEntry)this.entries.get(var3.getFileName());
                  if (var6 == null) {
                     this.entries.put(var3.getFileName(), new PollingWatchService.CacheEntry(var4, this.tickCount));
                     if (this.events.contains(StandardWatchEventKinds.ENTRY_CREATE)) {
                        this.signalEvent(StandardWatchEventKinds.ENTRY_CREATE, var3.getFileName());
                     } else if (this.events.contains(StandardWatchEventKinds.ENTRY_MODIFY)) {
                        this.signalEvent(StandardWatchEventKinds.ENTRY_MODIFY, var3.getFileName());
                     }
                  } else {
                     if (var6.lastModified != var4 && this.events.contains(StandardWatchEventKinds.ENTRY_MODIFY)) {
                        this.signalEvent(StandardWatchEventKinds.ENTRY_MODIFY, var3.getFileName());
                     }

                     var6.update(var4, this.tickCount);
                  }
               }
            } catch (DirectoryIteratorException var19) {
            } finally {
               try {
                  var1.close();
               } catch (IOException var16) {
               }

            }

            var2 = this.entries.entrySet().iterator();

            while(var2.hasNext()) {
               Map.Entry var21 = (Map.Entry)var2.next();
               PollingWatchService.CacheEntry var22 = (PollingWatchService.CacheEntry)var21.getValue();
               if (var22.lastTickCount() != this.tickCount) {
                  Path var5 = (Path)var21.getKey();
                  var2.remove();
                  if (this.events.contains(StandardWatchEventKinds.ENTRY_DELETE)) {
                     this.signalEvent(StandardWatchEventKinds.ENTRY_DELETE, var5);
                  }
               }
            }

         }
      }
   }

   private static class CacheEntry {
      private long lastModified;
      private int lastTickCount;

      CacheEntry(long var1, int var3) {
         this.lastModified = var1;
         this.lastTickCount = var3;
      }

      int lastTickCount() {
         return this.lastTickCount;
      }

      long lastModified() {
         return this.lastModified;
      }

      void update(long var1, int var3) {
         this.lastModified = var1;
         this.lastTickCount = var3;
      }
   }
}
