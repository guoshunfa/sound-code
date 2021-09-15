package java.util;

public class DuplicateFormatFlagsException extends IllegalFormatException {
   private static final long serialVersionUID = 18890531L;
   private String flags;

   public DuplicateFormatFlagsException(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.flags = var1;
      }
   }

   public String getFlags() {
      return this.flags;
   }

   public String getMessage() {
      return String.format("Flags = '%s'", this.flags);
   }
}
