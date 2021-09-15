package com.sun.corba.se.spi.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public abstract class LogWrapperBase {
   protected Logger logger;
   protected String loggerName;

   protected LogWrapperBase(Logger var1) {
      this.logger = var1;
      this.loggerName = var1.getName();
   }

   protected void doLog(Level var1, String var2, Object[] var3, Class var4, Throwable var5) {
      LogRecord var6 = new LogRecord(var1, var2);
      if (var3 != null) {
         var6.setParameters(var3);
      }

      this.inferCaller(var4, var6);
      var6.setThrown(var5);
      var6.setLoggerName(this.loggerName);
      var6.setResourceBundle(this.logger.getResourceBundle());
      this.logger.log(var6);
   }

   private void inferCaller(Class var1, LogRecord var2) {
      StackTraceElement[] var3 = (new Throwable()).getStackTrace();
      StackTraceElement var4 = null;
      String var5 = var1.getName();
      String var6 = LogWrapperBase.class.getName();

      int var7;
      for(var7 = 0; var7 < var3.length; ++var7) {
         var4 = var3[var7];
         String var8 = var4.getClassName();
         if (!var8.equals(var5) && !var8.equals(var6)) {
            break;
         }
      }

      if (var7 < var3.length) {
         var2.setSourceClassName(var4.getClassName());
         var2.setSourceMethodName(var4.getMethodName());
      }

   }

   protected void doLog(Level var1, String var2, Class var3, Throwable var4) {
      this.doLog(var1, var2, (Object[])null, var3, var4);
   }
}
