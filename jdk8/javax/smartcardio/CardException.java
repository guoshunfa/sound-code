package javax.smartcardio;

public class CardException extends Exception {
   private static final long serialVersionUID = 7787607144922050628L;

   public CardException(String var1) {
      super(var1);
   }

   public CardException(Throwable var1) {
      super(var1);
   }

   public CardException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
