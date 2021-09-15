package java.lang;

public class Error extends Throwable {
   static final long serialVersionUID = 4980196508277280342L;

   public Error() {
   }

   public Error(String var1) {
      super(var1);
   }

   public Error(String var1, Throwable var2) {
      super(var1, var2);
   }

   public Error(Throwable var1) {
      super(var1);
   }

   protected Error(String var1, Throwable var2, boolean var3, boolean var4) {
      super(var1, var2, var3, var4);
   }
}
