package sun.font;

public class FontScalerException extends Exception {
   public FontScalerException() {
      super("Font scaler encountered runtime problem.");
   }

   public FontScalerException(String var1) {
      super(var1);
   }
}
