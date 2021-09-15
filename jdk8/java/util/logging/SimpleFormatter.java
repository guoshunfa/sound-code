package java.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import sun.util.logging.LoggingSupport;

public class SimpleFormatter extends Formatter {
   private static final String format = LoggingSupport.getSimpleFormat();
   private final Date dat = new Date();

   public synchronized String format(LogRecord var1) {
      this.dat.setTime(var1.getMillis());
      String var2;
      if (var1.getSourceClassName() != null) {
         var2 = var1.getSourceClassName();
         if (var1.getSourceMethodName() != null) {
            var2 = var2 + " " + var1.getSourceMethodName();
         }
      } else {
         var2 = var1.getLoggerName();
      }

      String var3 = this.formatMessage(var1);
      String var4 = "";
      if (var1.getThrown() != null) {
         StringWriter var5 = new StringWriter();
         PrintWriter var6 = new PrintWriter(var5);
         var6.println();
         var1.getThrown().printStackTrace(var6);
         var6.close();
         var4 = var5.toString();
      }

      return String.format(format, this.dat, var2, var1.getLoggerName(), var1.getLevel().getLocalizedLevelName(), var3, var4);
   }
}
