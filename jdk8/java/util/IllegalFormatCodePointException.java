package java.util;

public class IllegalFormatCodePointException extends IllegalFormatException {
   private static final long serialVersionUID = 19080630L;
   private int c;

   public IllegalFormatCodePointException(int var1) {
      this.c = var1;
   }

   public int getCodePoint() {
      return this.c;
   }

   public String getMessage() {
      return String.format("Code point = %#x", this.c);
   }
}
