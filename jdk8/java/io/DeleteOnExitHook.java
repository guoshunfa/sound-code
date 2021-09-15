package java.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import sun.misc.SharedSecrets;

class DeleteOnExitHook {
   private static LinkedHashSet<String> files = new LinkedHashSet();

   private DeleteOnExitHook() {
   }

   static synchronized void add(String var0) {
      if (files == null) {
         throw new IllegalStateException("Shutdown in progress");
      } else {
         files.add(var0);
      }
   }

   static void runHooks() {
      Class var1 = DeleteOnExitHook.class;
      LinkedHashSet var0;
      synchronized(DeleteOnExitHook.class) {
         var0 = files;
         files = null;
      }

      ArrayList var5 = new ArrayList(var0);
      Collections.reverse(var5);
      Iterator var2 = var5.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         (new File(var3)).delete();
      }

   }

   static {
      SharedSecrets.getJavaLangAccess().registerShutdownHook(2, true, new Runnable() {
         public void run() {
            DeleteOnExitHook.runHooks();
         }
      });
   }
}
