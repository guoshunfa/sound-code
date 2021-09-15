package java.time.chrono;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import sun.util.calendar.CalendarDate;

public final class JapaneseEra implements Era, Serializable {
   static final int ERA_OFFSET = 2;
   static final sun.util.calendar.Era[] ERA_CONFIG;
   public static final JapaneseEra MEIJI = new JapaneseEra(-1, LocalDate.of(1868, 1, 1));
   public static final JapaneseEra TAISHO = new JapaneseEra(0, LocalDate.of(1912, 7, 30));
   public static final JapaneseEra SHOWA = new JapaneseEra(1, LocalDate.of(1926, 12, 25));
   public static final JapaneseEra HEISEI = new JapaneseEra(2, LocalDate.of(1989, 1, 8));
   private static final int N_ERA_CONSTANTS;
   private static final long serialVersionUID = 1466499369062886794L;
   private static final JapaneseEra[] KNOWN_ERAS;
   private final transient int eraValue;
   private final transient LocalDate since;

   private JapaneseEra(int var1, LocalDate var2) {
      this.eraValue = var1;
      this.since = var2;
   }

   sun.util.calendar.Era getPrivateEra() {
      return ERA_CONFIG[ordinal(this.eraValue)];
   }

   public static JapaneseEra of(int var0) {
      if (var0 >= MEIJI.eraValue && var0 + 2 <= KNOWN_ERAS.length) {
         return KNOWN_ERAS[ordinal(var0)];
      } else {
         throw new DateTimeException("Invalid era: " + var0);
      }
   }

   public static JapaneseEra valueOf(String var0) {
      Objects.requireNonNull(var0, (String)"japaneseEra");
      JapaneseEra[] var1 = KNOWN_ERAS;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         JapaneseEra var4 = var1[var3];
         if (var4.getName().equals(var0)) {
            return var4;
         }
      }

      throw new IllegalArgumentException("japaneseEra is invalid");
   }

   public static JapaneseEra[] values() {
      return (JapaneseEra[])Arrays.copyOf((Object[])KNOWN_ERAS, KNOWN_ERAS.length);
   }

   public String getDisplayName(TextStyle var1, Locale var2) {
      if (this.getValue() > N_ERA_CONSTANTS - 2) {
         Objects.requireNonNull(var2, (String)"locale");
         return var1.asNormal() == TextStyle.NARROW ? this.getAbbreviation() : this.getName();
      } else {
         return Era.super.getDisplayName(var1, var2);
      }
   }

   static JapaneseEra from(LocalDate var0) {
      if (var0.isBefore(JapaneseDate.MEIJI_6_ISODATE)) {
         throw new DateTimeException("JapaneseDate before Meiji 6 are not supported");
      } else {
         for(int var1 = KNOWN_ERAS.length - 1; var1 > 0; --var1) {
            JapaneseEra var2 = KNOWN_ERAS[var1];
            if (var0.compareTo((ChronoLocalDate)var2.since) >= 0) {
               return var2;
            }
         }

         return null;
      }
   }

   static JapaneseEra toJapaneseEra(sun.util.calendar.Era var0) {
      for(int var1 = ERA_CONFIG.length - 1; var1 >= 0; --var1) {
         if (ERA_CONFIG[var1].equals(var0)) {
            return KNOWN_ERAS[var1];
         }
      }

      return null;
   }

   static sun.util.calendar.Era privateEraFrom(LocalDate var0) {
      for(int var1 = KNOWN_ERAS.length - 1; var1 > 0; --var1) {
         JapaneseEra var2 = KNOWN_ERAS[var1];
         if (var0.compareTo((ChronoLocalDate)var2.since) >= 0) {
            return ERA_CONFIG[var1];
         }
      }

      return null;
   }

   private static int ordinal(int var0) {
      return var0 + 2 - 1;
   }

   public int getValue() {
      return this.eraValue;
   }

   public ValueRange range(TemporalField var1) {
      return var1 == ChronoField.ERA ? JapaneseChronology.INSTANCE.range(ChronoField.ERA) : Era.super.range(var1);
   }

   String getAbbreviation() {
      return ERA_CONFIG[ordinal(this.getValue())].getAbbreviation();
   }

   String getName() {
      return ERA_CONFIG[ordinal(this.getValue())].getName();
   }

   public String toString() {
      return this.getName();
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   private Object writeReplace() {
      return new Ser((byte)5, this);
   }

   void writeExternal(DataOutput var1) throws IOException {
      var1.writeByte(this.getValue());
   }

   static JapaneseEra readExternal(DataInput var0) throws IOException {
      byte var1 = var0.readByte();
      return of(var1);
   }

   static {
      N_ERA_CONSTANTS = HEISEI.getValue() + 2;
      ERA_CONFIG = JapaneseChronology.JCAL.getEras();
      KNOWN_ERAS = new JapaneseEra[ERA_CONFIG.length];
      KNOWN_ERAS[0] = MEIJI;
      KNOWN_ERAS[1] = TAISHO;
      KNOWN_ERAS[2] = SHOWA;
      KNOWN_ERAS[3] = HEISEI;

      for(int var0 = N_ERA_CONSTANTS; var0 < ERA_CONFIG.length; ++var0) {
         CalendarDate var1 = ERA_CONFIG[var0].getSinceDate();
         LocalDate var2 = LocalDate.of(var1.getYear(), var1.getMonth(), var1.getDayOfMonth());
         KNOWN_ERAS[var0] = new JapaneseEra(var0 - 2 + 1, var2);
      }

   }
}
