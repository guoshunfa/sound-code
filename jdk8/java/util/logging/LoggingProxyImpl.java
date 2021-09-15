package java.util.logging;

import java.util.List;
import sun.util.logging.LoggingProxy;

class LoggingProxyImpl implements LoggingProxy {
   static final LoggingProxy INSTANCE = new LoggingProxyImpl();

   private LoggingProxyImpl() {
   }

   public Object getLogger(String var1) {
      return Logger.getPlatformLogger(var1);
   }

   public Object getLevel(Object var1) {
      return ((Logger)var1).getLevel();
   }

   public void setLevel(Object var1, Object var2) {
      ((Logger)var1).setLevel((Level)var2);
   }

   public boolean isLoggable(Object var1, Object var2) {
      return ((Logger)var1).isLoggable((Level)var2);
   }

   public void log(Object var1, Object var2, String var3) {
      ((Logger)var1).log((Level)var2, var3);
   }

   public void log(Object var1, Object var2, String var3, Throwable var4) {
      ((Logger)var1).log((Level)var2, var3, var4);
   }

   public void log(Object var1, Object var2, String var3, Object... var4) {
      ((Logger)var1).log((Level)var2, var3, var4);
   }

   public List<String> getLoggerNames() {
      return LogManager.getLoggingMXBean().getLoggerNames();
   }

   public String getLoggerLevel(String var1) {
      return LogManager.getLoggingMXBean().getLoggerLevel(var1);
   }

   public void setLoggerLevel(String var1, String var2) {
      LogManager.getLoggingMXBean().setLoggerLevel(var1, var2);
   }

   public String getParentLoggerName(String var1) {
      return LogManager.getLoggingMXBean().getParentLoggerName(var1);
   }

   public Object parseLevel(String var1) {
      Level var2 = Level.findLevel(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("Unknown level \"" + var1 + "\"");
      } else {
         return var2;
      }
   }

   public String getLevelName(Object var1) {
      return ((Level)var1).getLevelName();
   }

   public int getLevelValue(Object var1) {
      return ((Level)var1).intValue();
   }

   public String getProperty(String var1) {
      return LogManager.getLogManager().getProperty(var1);
   }
}
