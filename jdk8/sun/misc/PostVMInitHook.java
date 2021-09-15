package sun.misc;

import sun.usagetracker.UsageTrackerClient;

public class PostVMInitHook {
   public static void run() {
      trackJavaUsage();
   }

   private static void trackJavaUsage() {
      UsageTrackerClient var0 = new UsageTrackerClient();
      var0.run("VM start", System.getProperty("sun.java.command"));
   }
}
