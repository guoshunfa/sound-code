package java.util;

public class UnknownFormatFlagsException extends IllegalFormatException {
   private static final long serialVersionUID = 19370506L;
   private String flags;

   public UnknownFormatFlagsException(String var1) {
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
      return "Flags = " + this.flags;
   }
}
