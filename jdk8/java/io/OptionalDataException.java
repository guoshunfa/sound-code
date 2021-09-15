package java.io;

public class OptionalDataException extends ObjectStreamException {
   private static final long serialVersionUID = -8011121865681257820L;
   public int length;
   public boolean eof;

   OptionalDataException(int var1) {
      this.eof = false;
      this.length = var1;
   }

   OptionalDataException(boolean var1) {
      this.length = 0;
      this.eof = var1;
   }
}
