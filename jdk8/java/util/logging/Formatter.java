package java.util.logging;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class Formatter {
   protected Formatter() {
   }

   public abstract String format(LogRecord var1);

   public String getHead(Handler var1) {
      return "";
   }

   public String getTail(Handler var1) {
      return "";
   }

   public synchronized String formatMessage(LogRecord var1) {
      String var2 = var1.getMessage();
      ResourceBundle var3 = var1.getResourceBundle();
      if (var3 != null) {
         try {
            var2 = var3.getString(var1.getMessage());
         } catch (MissingResourceException var6) {
            var2 = var1.getMessage();
         }
      }

      try {
         Object[] var4 = var1.getParameters();
         if (var4 != null && var4.length != 0) {
            return var2.indexOf("{0") < 0 && var2.indexOf("{1") < 0 && var2.indexOf("{2") < 0 && var2.indexOf("{3") < 0 ? var2 : MessageFormat.format(var2, var4);
         } else {
            return var2;
         }
      } catch (Exception var5) {
         return var2;
      }
   }
}
