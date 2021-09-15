package java.util;

public class IllegalFormatFlagsException extends IllegalFormatException {
   private static final long serialVersionUID = 790824L;
   private String flags;

   public IllegalFormatFlagsException(String var1) {
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
      return "Flags = '" + this.flags + "'";
   }
}
