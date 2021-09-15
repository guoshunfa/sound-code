package java.nio.charset;

public class UnsupportedCharsetException extends IllegalArgumentException {
   private static final long serialVersionUID = 1490765524727386367L;
   private String charsetName;

   public UnsupportedCharsetException(String var1) {
      super(String.valueOf((Object)var1));
      this.charsetName = var1;
   }

   public String getCharsetName() {
      return this.charsetName;
   }
}
