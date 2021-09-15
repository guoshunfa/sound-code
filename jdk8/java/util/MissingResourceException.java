package java.util;

public class MissingResourceException extends RuntimeException {
   private static final long serialVersionUID = -4876345176062000401L;
   private String className;
   private String key;

   public MissingResourceException(String var1, String var2, String var3) {
      super(var1);
      this.className = var2;
      this.key = var3;
   }

   MissingResourceException(String var1, String var2, String var3, Throwable var4) {
      super(var1, var4);
      this.className = var2;
      this.key = var3;
   }

   public String getClassName() {
      return this.className;
   }

   public String getKey() {
      return this.key;
   }
}
