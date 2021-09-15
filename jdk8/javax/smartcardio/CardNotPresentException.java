package javax.smartcardio;

public class CardNotPresentException extends CardException {
   private static final long serialVersionUID = 1346879911706545215L;

   public CardNotPresentException(String var1) {
      super(var1);
   }

   public CardNotPresentException(Throwable var1) {
      super(var1);
   }

   public CardNotPresentException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
