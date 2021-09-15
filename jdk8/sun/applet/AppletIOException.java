package sun.applet;

import java.io.IOException;

public class AppletIOException extends IOException {
   private String key;
   private Object msgobj;
   private static AppletMessageHandler amh = new AppletMessageHandler("appletioexception");

   public AppletIOException(String var1) {
      super(var1);
      this.key = null;
      this.msgobj = null;
      this.key = var1;
   }

   public AppletIOException(String var1, Object var2) {
      this(var1);
      this.msgobj = var2;
   }

   public String getLocalizedMessage() {
      return this.msgobj != null ? amh.getMessage(this.key, this.msgobj) : amh.getMessage(this.key);
   }
}
