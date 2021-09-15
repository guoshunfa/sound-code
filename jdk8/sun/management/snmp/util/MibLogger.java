package sun.management.snmp.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MibLogger {
   final Logger logger;
   final String className;

   static String getClassName(Class<?> var0) {
      if (var0 == null) {
         return null;
      } else if (var0.isArray()) {
         return getClassName(var0.getComponentType()) + "[]";
      } else {
         String var1 = var0.getName();
         int var2 = var1.lastIndexOf(46);
         int var3 = var1.length();
         return var2 >= 0 && var2 < var3 ? var1.substring(var2 + 1, var3) : var1;
      }
   }

   static String getLoggerName(Class<?> var0) {
      if (var0 == null) {
         return "sun.management.snmp.jvminstr";
      } else {
         Package var1 = var0.getPackage();
         if (var1 == null) {
            return "sun.management.snmp.jvminstr";
         } else {
            String var2 = var1.getName();
            return var2 == null ? "sun.management.snmp.jvminstr" : var2;
         }
      }
   }

   public MibLogger(Class<?> var1) {
      this(getLoggerName(var1), getClassName(var1));
   }

   public MibLogger(Class<?> var1, String var2) {
      this(getLoggerName(var1) + (var2 == null ? "" : "." + var2), getClassName(var1));
   }

   public MibLogger(String var1) {
      this("sun.management.snmp.jvminstr", var1);
   }

   public MibLogger(String var1, String var2) {
      Logger var3 = null;

      try {
         var3 = Logger.getLogger(var1);
      } catch (Exception var5) {
      }

      this.logger = var3;
      this.className = var2;
   }

   protected Logger getLogger() {
      return this.logger;
   }

   public boolean isTraceOn() {
      Logger var1 = this.getLogger();
      return var1 == null ? false : var1.isLoggable(Level.FINE);
   }

   public boolean isDebugOn() {
      Logger var1 = this.getLogger();
      return var1 == null ? false : var1.isLoggable(Level.FINEST);
   }

   public boolean isInfoOn() {
      Logger var1 = this.getLogger();
      return var1 == null ? false : var1.isLoggable(Level.INFO);
   }

   public boolean isConfigOn() {
      Logger var1 = this.getLogger();
      return var1 == null ? false : var1.isLoggable(Level.CONFIG);
   }

   public void config(String var1, String var2) {
      Logger var3 = this.getLogger();
      if (var3 != null) {
         var3.logp(Level.CONFIG, this.className, var1, var2);
      }

   }

   public void config(String var1, Throwable var2) {
      Logger var3 = this.getLogger();
      if (var3 != null) {
         var3.logp(Level.CONFIG, this.className, var1, var2.toString(), var2);
      }

   }

   public void config(String var1, String var2, Throwable var3) {
      Logger var4 = this.getLogger();
      if (var4 != null) {
         var4.logp(Level.CONFIG, this.className, var1, var2, var3);
      }

   }

   public void error(String var1, String var2) {
      Logger var3 = this.getLogger();
      if (var3 != null) {
         var3.logp(Level.SEVERE, this.className, var1, var2);
      }

   }

   public void info(String var1, String var2) {
      Logger var3 = this.getLogger();
      if (var3 != null) {
         var3.logp(Level.INFO, this.className, var1, var2);
      }

   }

   public void info(String var1, Throwable var2) {
      Logger var3 = this.getLogger();
      if (var3 != null) {
         var3.logp(Level.INFO, this.className, var1, var2.toString(), var2);
      }

   }

   public void info(String var1, String var2, Throwable var3) {
      Logger var4 = this.getLogger();
      if (var4 != null) {
         var4.logp(Level.INFO, this.className, var1, var2, var3);
      }

   }

   public void warning(String var1, String var2) {
      Logger var3 = this.getLogger();
      if (var3 != null) {
         var3.logp(Level.WARNING, this.className, var1, var2);
      }

   }

   public void warning(String var1, Throwable var2) {
      Logger var3 = this.getLogger();
      if (var3 != null) {
         var3.logp(Level.WARNING, this.className, var1, var2.toString(), var2);
      }

   }

   public void warning(String var1, String var2, Throwable var3) {
      Logger var4 = this.getLogger();
      if (var4 != null) {
         var4.logp(Level.WARNING, this.className, var1, var2, var3);
      }

   }

   public void trace(String var1, String var2) {
      Logger var3 = this.getLogger();
      if (var3 != null) {
         var3.logp(Level.FINE, this.className, var1, var2);
      }

   }

   public void trace(String var1, Throwable var2) {
      Logger var3 = this.getLogger();
      if (var3 != null) {
         var3.logp(Level.FINE, this.className, var1, var2.toString(), var2);
      }

   }

   public void trace(String var1, String var2, Throwable var3) {
      Logger var4 = this.getLogger();
      if (var4 != null) {
         var4.logp(Level.FINE, this.className, var1, var2, var3);
      }

   }

   public void debug(String var1, String var2) {
      Logger var3 = this.getLogger();
      if (var3 != null) {
         var3.logp(Level.FINEST, this.className, var1, var2);
      }

   }

   public void debug(String var1, Throwable var2) {
      Logger var3 = this.getLogger();
      if (var3 != null) {
         var3.logp(Level.FINEST, this.className, var1, var2.toString(), var2);
      }

   }

   public void debug(String var1, String var2, Throwable var3) {
      Logger var4 = this.getLogger();
      if (var4 != null) {
         var4.logp(Level.FINEST, this.className, var1, var2, var3);
      }

   }
}
