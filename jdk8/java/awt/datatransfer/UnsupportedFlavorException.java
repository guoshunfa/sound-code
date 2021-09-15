package java.awt.datatransfer;

public class UnsupportedFlavorException extends Exception {
   private static final long serialVersionUID = 5383814944251665601L;

   public UnsupportedFlavorException(DataFlavor var1) {
      super(var1 != null ? var1.getHumanPresentableName() : null);
   }
}
