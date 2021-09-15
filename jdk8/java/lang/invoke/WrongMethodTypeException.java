package java.lang.invoke;

public class WrongMethodTypeException extends RuntimeException {
   private static final long serialVersionUID = 292L;

   public WrongMethodTypeException() {
   }

   public WrongMethodTypeException(String var1) {
      super(var1);
   }

   WrongMethodTypeException(String var1, Throwable var2) {
      super(var1, var2);
   }

   WrongMethodTypeException(Throwable var1) {
      super(var1);
   }
}
