package sun.applet;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

class AppletMessageHandler {
   private static ResourceBundle rb;
   private String baseKey = null;

   AppletMessageHandler(String var1) {
      this.baseKey = var1;
   }

   String getMessage(String var1) {
      return rb.getString(this.getQualifiedKey(var1));
   }

   String getMessage(String var1, Object var2) {
      String var3 = rb.getString(this.getQualifiedKey(var1));
      MessageFormat var4 = new MessageFormat(var3);
      Object[] var5 = new Object[1];
      if (var2 == null) {
         var2 = "null";
      }

      var5[0] = var2;
      return var4.format(var5);
   }

   String getMessage(String var1, Object var2, Object var3) {
      String var4 = rb.getString(this.getQualifiedKey(var1));
      MessageFormat var5 = new MessageFormat(var4);
      Object[] var6 = new Object[2];
      if (var2 == null) {
         var2 = "null";
      }

      if (var3 == null) {
         var3 = "null";
      }

      var6[0] = var2;
      var6[1] = var3;
      return var5.format(var6);
   }

   String getMessage(String var1, Object var2, Object var3, Object var4) {
      String var5 = rb.getString(this.getQualifiedKey(var1));
      MessageFormat var6 = new MessageFormat(var5);
      Object[] var7 = new Object[3];
      if (var2 == null) {
         var2 = "null";
      }

      if (var3 == null) {
         var3 = "null";
      }

      if (var4 == null) {
         var4 = "null";
      }

      var7[0] = var2;
      var7[1] = var3;
      var7[2] = var4;
      return var6.format(var7);
   }

   String getMessage(String var1, Object[] var2) {
      String var3 = rb.getString(this.getQualifiedKey(var1));
      MessageFormat var4 = new MessageFormat(var3);
      return var4.format(var2);
   }

   String getQualifiedKey(String var1) {
      return this.baseKey + "." + var1;
   }

   static {
      try {
         rb = ResourceBundle.getBundle("sun.applet.resources.MsgAppletViewer");
      } catch (MissingResourceException var1) {
         System.out.println(var1.getMessage());
         System.exit(1);
      }

   }
}
