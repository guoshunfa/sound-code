package java.time.chrono;

import java.time.DateTimeException;

public enum ThaiBuddhistEra implements Era {
   BEFORE_BE,
   BE;

   public static ThaiBuddhistEra of(int var0) {
      switch(var0) {
      case 0:
         return BEFORE_BE;
      case 1:
         return BE;
      default:
         throw new DateTimeException("Invalid era: " + var0);
      }
   }

   public int getValue() {
      return this.ordinal();
   }
}
