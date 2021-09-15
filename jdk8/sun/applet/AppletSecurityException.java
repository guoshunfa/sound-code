package sun.applet;

public class AppletSecurityException extends SecurityException {
   private String key;
   private Object[] msgobj;
   private static AppletMessageHandler amh = new AppletMessageHandler("appletsecurityexception");

   public AppletSecurityException(String var1) {
      super(var1);
      this.key = null;
      this.msgobj = null;
      this.key = var1;
   }

   public AppletSecurityException(String var1, String var2) {
      this(var1);
      this.msgobj = new Object[1];
      this.msgobj[0] = var2;
   }

   public AppletSecurityException(String var1, String var2, String var3) {
      this(var1);
      this.msgobj = new Object[2];
      this.msgobj[0] = var2;
      this.msgobj[1] = var3;
   }

   public String getLocalizedMessage() {
      return this.msgobj != null ? amh.getMessage(this.key, this.msgobj) : amh.getMessage(this.key);
   }
}
