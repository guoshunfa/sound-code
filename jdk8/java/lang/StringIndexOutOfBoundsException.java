package java.lang;

public class StringIndexOutOfBoundsException extends IndexOutOfBoundsException {
   private static final long serialVersionUID = -6762910422159637258L;

   public StringIndexOutOfBoundsException() {
   }

   public StringIndexOutOfBoundsException(String var1) {
      super(var1);
   }

   public StringIndexOutOfBoundsException(int var1) {
      super("String index out of range: " + var1);
   }
}
