package java.nio.charset;

public class IllegalCharsetNameException extends IllegalArgumentException {
   private static final long serialVersionUID = 1457525358470002989L;
   private String charsetName;

   public IllegalCharsetNameException(String var1) {
      super(String.valueOf((Object)var1));
      this.charsetName = var1;
   }

   public String getCharsetName() {
      return this.charsetName;
   }
}
