package java.lang;

public class RuntimeException extends Exception {
   static final long serialVersionUID = -7034897190745766939L;

   public RuntimeException() {
   }

   public RuntimeException(String var1) {
      super(var1);
   }

   public RuntimeException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public RuntimeException(Throwable var1) {
      super(var1);
   }

   protected RuntimeException(String var1, Throwable var2, boolean var3, boolean var4) {
      super(var1, var2, var3, var4);
   }
}
