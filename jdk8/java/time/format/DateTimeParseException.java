package java.time.format;

import java.time.DateTimeException;

public class DateTimeParseException extends DateTimeException {
   private static final long serialVersionUID = 4304633501674722597L;
   private final String parsedString;
   private final int errorIndex;

   public DateTimeParseException(String var1, CharSequence var2, int var3) {
      super(var1);
      this.parsedString = var2.toString();
      this.errorIndex = var3;
   }

   public DateTimeParseException(String var1, CharSequence var2, int var3, Throwable var4) {
      super(var1, var4);
      this.parsedString = var2.toString();
      this.errorIndex = var3;
   }

   public String getParsedString() {
      return this.parsedString;
   }

   public int getErrorIndex() {
      return this.errorIndex;
   }
}
