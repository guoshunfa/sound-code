package sun.applet;

public class AppletIllegalArgumentException extends IllegalArgumentException {
   private String key = null;
   private static AppletMessageHandler amh = new AppletMessageHandler("appletillegalargumentexception");

   public AppletIllegalArgumentException(String var1) {
      super(var1);
      this.key = var1;
   }

   public String getLocalizedMessage() {
      return amh.getMessage(this.key);
   }
}
