package java.nio.charset;

public class MalformedInputException extends CharacterCodingException {
   private static final long serialVersionUID = -3438823399834806194L;
   private int inputLength;

   public MalformedInputException(int var1) {
      this.inputLength = var1;
   }

   public int getInputLength() {
      return this.inputLength;
   }

   public String getMessage() {
      return "Input length = " + this.inputLength;
   }
}
