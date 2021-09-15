package sun.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Vector;
import sun.security.action.GetPropertyAction;

public class PerformanceLogger {
   private static final int START_INDEX = 0;
   private static final int LAST_RESERVED = 0;
   private static boolean perfLoggingOn = false;
   private static boolean useNanoTime = false;
   private static Vector<PerformanceLogger.TimeData> times;
   private static String logFileName = null;
   private static Writer logWriter = null;
   private static long baseTime;

   public static boolean loggingEnabled() {
      return perfLoggingOn;
   }

   private static long getCurrentTime() {
      return useNanoTime ? System.nanoTime() : System.currentTimeMillis();
   }

   public static void setStartTime(String var0) {
      if (loggingEnabled()) {
         long var1 = getCurrentTime();
         setStartTime(var0, var1);
      }

   }

   public static void setBaseTime(long var0) {
      if (loggingEnabled()) {
         baseTime = var0;
      }

   }

   public static void setStartTime(String var0, long var1) {
      if (loggingEnabled()) {
         times.set(0, new PerformanceLogger.TimeData(var0, var1));
      }

   }

   public static long getStartTime() {
      return loggingEnabled() ? ((PerformanceLogger.TimeData)times.get(0)).getTime() : 0L;
   }

   public static int setTime(String var0) {
      if (loggingEnabled()) {
         long var1 = getCurrentTime();
         return setTime(var0, var1);
      } else {
         return 0;
      }
   }

   public static int setTime(String var0, long var1) {
      if (loggingEnabled()) {
         synchronized(times) {
            times.add(new PerformanceLogger.TimeData(var0, var1));
            return times.size() - 1;
         }
      } else {
         return 0;
      }
   }

   public static long getTimeAtIndex(int var0) {
      return loggingEnabled() ? ((PerformanceLogger.TimeData)times.get(var0)).getTime() : 0L;
   }

   public static String getMessageAtIndex(int var0) {
      return loggingEnabled() ? ((PerformanceLogger.TimeData)times.get(var0)).getMessage() : null;
   }

   public static void outputLog(Writer var0) {
      if (loggingEnabled()) {
         try {
            synchronized(times) {
               int var2 = 0;

               while(true) {
                  if (var2 >= times.size()) {
                     break;
                  }

                  PerformanceLogger.TimeData var3 = (PerformanceLogger.TimeData)times.get(var2);
                  if (var3 != null) {
                     var0.write(var2 + " " + var3.getMessage() + ": " + (var3.getTime() - baseTime) + "\n");
                  }

                  ++var2;
               }
            }

            var0.flush();
         } catch (Exception var6) {
            System.out.println(var6 + ": Writing performance log to " + var0);
         }
      }

   }

   public static void outputLog() {
      outputLog(logWriter);
   }

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.perflog")));
      if (var0 != null) {
         perfLoggingOn = true;
         String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.perflog.nano")));
         if (var1 != null) {
            useNanoTime = true;
         }

         if (var0.regionMatches(true, 0, "file:", 0, 5)) {
            logFileName = var0.substring(5);
         }

         if (logFileName != null && logWriter == null) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  try {
                     File var1 = new File(PerformanceLogger.logFileName);
                     var1.createNewFile();
                     PerformanceLogger.logWriter = new FileWriter(var1);
                  } catch (Exception var2) {
                     System.out.println(var2 + ": Creating logfile " + PerformanceLogger.logFileName + ".  Log to console");
                  }

                  return null;
               }
            });
         }

         if (logWriter == null) {
            logWriter = new OutputStreamWriter(System.out);
         }
      }

      times = new Vector(10);

      for(int var2 = 0; var2 <= 0; ++var2) {
         times.add(new PerformanceLogger.TimeData("Time " + var2 + " not set", 0L));
      }

   }

   static class TimeData {
      String message;
      long time;

      TimeData(String var1, long var2) {
         this.message = var1;
         this.time = var2;
      }

      String getMessage() {
         return this.message;
      }

      long getTime() {
         return this.time;
      }
   }
}
