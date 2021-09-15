package java.time.chrono;

import java.time.DateTimeException;

public enum MinguoEra implements Era {
   BEFORE_ROC,
   ROC;

   public static MinguoEra of(int var0) {
      switch(var0) {
      case 0:
         return BEFORE_ROC;
      case 1:
         return ROC;
      default:
         throw new DateTimeException("Invalid era: " + var0);
      }
   }

   public int getValue() {
      return this.ordinal();
   }
}
