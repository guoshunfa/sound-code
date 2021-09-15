package java.time.temporal;

import java.time.DateTimeException;

public class UnsupportedTemporalTypeException extends DateTimeException {
   private static final long serialVersionUID = -6158898438688206006L;

   public UnsupportedTemporalTypeException(String var1) {
      super(var1);
   }

   public UnsupportedTemporalTypeException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
