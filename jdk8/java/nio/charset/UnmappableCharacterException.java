package java.nio.charset;

public class UnmappableCharacterException extends CharacterCodingException {
   private static final long serialVersionUID = -7026962371537706123L;
   private int inputLength;

   public UnmappableCharacterException(int var1) {
      this.inputLength = var1;
   }

   public int getInputLength() {
      return this.inputLength;
   }

   public String getMessage() {
      return "Input length = " + this.inputLength;
   }
}
