package sun.font;

import java.io.File;
import java.io.OutputStream;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import sun.awt.AppContext;
import sun.misc.ThreadGroupUtils;

public class CreatedFontTracker {
   public static final int MAX_FILE_SIZE = 33554432;
   public static final int MAX_TOTAL_BYTES = 335544320;
   static CreatedFontTracker tracker;
   int numBytes = 0;

   public static synchronized CreatedFontTracker getTracker() {
      if (tracker == null) {
         tracker = new CreatedFontTracker();
      }

      return tracker;
   }

   private CreatedFontTracker() {
   }

   public synchronized int getNumBytes() {
      return this.numBytes;
   }

   public synchronized void addBytes(int var1) {
      this.numBytes += var1;
   }

   public synchronized void subBytes(int var1) {
      this.numBytes -= var1;
   }

   private static synchronized Semaphore getCS() {
      AppContext var0 = AppContext.getAppContext();
      Semaphore var1 = (Semaphore)var0.get(CreatedFontTracker.class);
      if (var1 == null) {
         var1 = new Semaphore(5, true);
         var0.put(CreatedFontTracker.class, var1);
      }

      return var1;
   }

   public boolean acquirePermit() throws InterruptedException {
      return getCS().tryAcquire(120L, TimeUnit.SECONDS);
   }

   public void releasePermit() {
      getCS().release();
   }

   public void add(File var1) {
      CreatedFontTracker.TempFileDeletionHook.add(var1);
   }

   public void set(File var1, OutputStream var2) {
      CreatedFontTracker.TempFileDeletionHook.set(var1, var2);
   }

   public void remove(File var1) {
      CreatedFontTracker.TempFileDeletionHook.remove(var1);
   }

   private static class TempFileDeletionHook {
      private static HashMap<File, OutputStream> files = new HashMap();
      private static Thread t = null;

      static void init() {
         if (t == null) {
            AccessController.doPrivileged(() -> {
               ThreadGroup var0 = ThreadGroupUtils.getRootThreadGroup();
               t = new Thread(var0, CreatedFontTracker.TempFileDeletionHook::runHooks);
               t.setContextClassLoader((ClassLoader)null);
               Runtime.getRuntime().addShutdownHook(t);
               return null;
            });
         }

      }

      static synchronized void add(File var0) {
         init();
         files.put(var0, (Object)null);
      }

      static synchronized void set(File var0, OutputStream var1) {
         files.put(var0, var1);
      }

      static synchronized void remove(File var0) {
         files.remove(var0);
      }

      static synchronized void runHooks() {
         if (!files.isEmpty()) {
            Map.Entry var1;
            for(Iterator var0 = files.entrySet().iterator(); var0.hasNext(); ((File)var1.getKey()).delete()) {
               var1 = (Map.Entry)var0.next();

               try {
                  if (var1.getValue() != null) {
                     ((OutputStream)var1.getValue()).close();
                  }
               } catch (Exception var3) {
               }
            }

         }
      }
   }
}
