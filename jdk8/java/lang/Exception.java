package java.lang;

public class Exception extends Throwable {
   static final long serialVersionUID = -3387516993124229948L;

   public Exception() {
   }

   public Exception(String var1) {
      super(var1);
   }

   public Exception(String var1, Throwable var2) {
      super(var1, var2);
   }

   public Exception(Throwable var1) {
      super(var1);
   }

   protected Exception(String var1, Throwable var2, boolean var3, boolean var4) {
      super(var1, var2, var3, var4);
   }
}
