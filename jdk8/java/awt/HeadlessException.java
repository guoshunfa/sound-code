package java.awt;

public class HeadlessException extends UnsupportedOperationException {
   private static final long serialVersionUID = 167183644944358563L;

   public HeadlessException() {
   }

   public HeadlessException(String var1) {
      super(var1);
   }

   public String getMessage() {
      String var1 = super.getMessage();
      String var2 = GraphicsEnvironment.getHeadlessMessage();
      if (var1 == null) {
         return var2;
      } else {
         return var2 == null ? var1 : var1 + var2;
      }
   }
}
