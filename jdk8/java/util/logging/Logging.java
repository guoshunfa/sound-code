package java.util.logging;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

class Logging implements LoggingMXBean {
   private static LogManager logManager = LogManager.getLogManager();
   private static String EMPTY_STRING = "";

   public List<String> getLoggerNames() {
      Enumeration var1 = logManager.getLoggerNames();
      ArrayList var2 = new ArrayList();

      while(var1.hasMoreElements()) {
         var2.add(var1.nextElement());
      }

      return var2;
   }

   public String getLoggerLevel(String var1) {
      Logger var2 = logManager.getLogger(var1);
      if (var2 == null) {
         return null;
      } else {
         Level var3 = var2.getLevel();
         return var3 == null ? EMPTY_STRING : var3.getLevelName();
      }
   }

   public void setLoggerLevel(String var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException("loggerName is null");
      } else {
         Logger var3 = logManager.getLogger(var1);
         if (var3 == null) {
            throw new IllegalArgumentException("Logger " + var1 + "does not exist");
         } else {
            Level var4 = null;
            if (var2 != null) {
               var4 = Level.findLevel(var2);
               if (var4 == null) {
                  throw new IllegalArgumentException("Unknown level \"" + var2 + "\"");
               }
            }

            var3.setLevel(var4);
         }
      }
   }

   public String getParentLoggerName(String var1) {
      Logger var2 = logManager.getLogger(var1);
      if (var2 == null) {
         return null;
      } else {
         Logger var3 = var2.getParent();
         return var3 == null ? EMPTY_STRING : var3.getName();
      }
   }
}
