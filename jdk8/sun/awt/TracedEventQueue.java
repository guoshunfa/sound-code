package sun.awt;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.util.StringTokenizer;

public class TracedEventQueue extends EventQueue {
   static boolean trace = false;
   static int[] suppressedIDs = null;

   public void postEvent(AWTEvent var1) {
      boolean var2 = true;
      int var3 = var1.getID();

      for(int var4 = 0; var4 < suppressedIDs.length; ++var4) {
         if (var3 == suppressedIDs[var4]) {
            var2 = false;
            break;
         }
      }

      if (var2) {
         System.out.println(Thread.currentThread().getName() + ": " + var1);
      }

      super.postEvent(var1);
   }

   static {
      String var0 = Toolkit.getProperty("AWT.IgnoreEventIDs", "");
      if (var0.length() > 0) {
         StringTokenizer var1 = new StringTokenizer(var0, ",");
         int var2 = var1.countTokens();
         suppressedIDs = new int[var2];

         for(int var3 = 0; var3 < var2; ++var3) {
            String var4 = var1.nextToken();

            try {
               suppressedIDs[var3] = Integer.parseInt(var4);
            } catch (NumberFormatException var6) {
               System.err.println("Bad ID listed in AWT.IgnoreEventIDs in awt.properties: \"" + var4 + "\" -- skipped");
               suppressedIDs[var3] = 0;
            }
         }
      } else {
         suppressedIDs = new int[0];
      }

   }
}
