package java.time.chrono;

import java.time.DateTimeException;

public enum IsoEra implements Era {
   BCE,
   CE;

   public static IsoEra of(int var0) {
      switch(var0) {
      case 0:
         return BCE;
      case 1:
         return CE;
      default:
         throw new DateTimeException("Invalid era: " + var0);
      }
   }

   public int getValue() {
      return this.ordinal();
   }
}
