package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Duration implements TemporalAmount, Comparable<Duration>, Serializable {
   public static final Duration ZERO = new Duration(0L, 0);
   private static final long serialVersionUID = 3078945930695997490L;
   private static final BigInteger BI_NANOS_PER_SECOND = BigInteger.valueOf(1000000000L);
   private static final Pattern PATTERN = Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)D)?(T(?:([-+]?[0-9]+)H)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)(?:[.,]([0-9]{0,9}))?S)?)?", 2);
   private final long seconds;
   private final int nanos;

   public static Duration ofDays(long var0) {
      return create(Math.multiplyExact(var0, 86400L), 0);
   }

   public static Duration ofHours(long var0) {
      return create(Math.multiplyExact(var0, 3600L), 0);
   }

   public static Duration ofMinutes(long var0) {
      return create(Math.multiplyExact(var0, 60L), 0);
   }

   public static Duration ofSeconds(long var0) {
      return create(var0, 0);
   }

   public static Duration ofSeconds(long var0, long var2) {
      long var4 = Math.addExact(var0, Math.floorDiv(var2, 1000000000L));
      int var6 = (int)Math.floorMod(var2, 1000000000L);
      return create(var4, var6);
   }

   public static Duration ofMillis(long var0) {
      long var2 = var0 / 1000L;
      int var4 = (int)(var0 % 1000L);
      if (var4 < 0) {
         var4 += 1000;
         --var2;
      }

      return create(var2, var4 * 1000000);
   }

   public static Duration ofNanos(long var0) {
      long var2 = var0 / 1000000000L;
      int var4 = (int)(var0 % 1000000000L);
      if (var4 < 0) {
         var4 = (int)((long)var4 + 1000000000L);
         --var2;
      }

      return create(var2, var4);
   }

   public static Duration of(long var0, TemporalUnit var2) {
      return ZERO.plus(var0, var2);
   }

   public static Duration from(TemporalAmount var0) {
      Objects.requireNonNull(var0, (String)"amount");
      Duration var1 = ZERO;

      TemporalUnit var3;
      for(Iterator var2 = var0.getUnits().iterator(); var2.hasNext(); var1 = var1.plus(var0.get(var3), var3)) {
         var3 = (TemporalUnit)var2.next();
      }

      return var1;
   }

   public static Duration parse(CharSequence var0) {
      Objects.requireNonNull(var0, (String)"text");
      Matcher var1 = PATTERN.matcher(var0);
      if (var1.matches() && !"T".equals(var1.group(3))) {
         boolean var2 = "-".equals(var1.group(1));
         String var3 = var1.group(2);
         String var4 = var1.group(4);
         String var5 = var1.group(5);
         String var6 = var1.group(6);
         String var7 = var1.group(7);
         if (var3 != null || var4 != null || var5 != null || var6 != null) {
            long var8 = parseNumber(var0, var3, 86400, "days");
            long var10 = parseNumber(var0, var4, 3600, "hours");
            long var12 = parseNumber(var0, var5, 60, "minutes");
            long var14 = parseNumber(var0, var6, 1, "seconds");
            int var16 = parseFraction(var0, var7, var14 < 0L ? -1 : 1);

            try {
               return create(var2, var8, var10, var12, var14, var16);
            } catch (ArithmeticException var18) {
               throw (DateTimeParseException)(new DateTimeParseException("Text cannot be parsed to a Duration: overflow", var0, 0)).initCause(var18);
            }
         }
      }

      throw new DateTimeParseException("Text cannot be parsed to a Duration", var0, 0);
   }

   private static long parseNumber(CharSequence var0, String var1, int var2, String var3) {
      if (var1 == null) {
         return 0L;
      } else {
         try {
            long var4 = Long.parseLong(var1);
            return Math.multiplyExact(var4, (long)var2);
         } catch (ArithmeticException | NumberFormatException var6) {
            throw (DateTimeParseException)(new DateTimeParseException("Text cannot be parsed to a Duration: " + var3, var0, 0)).initCause(var6);
         }
      }
   }

   private static int parseFraction(CharSequence var0, String var1, int var2) {
      if (var1 != null && var1.length() != 0) {
         try {
            var1 = (var1 + "000000000").substring(0, 9);
            return Integer.parseInt(var1) * var2;
         } catch (ArithmeticException | NumberFormatException var4) {
            throw (DateTimeParseException)(new DateTimeParseException("Text cannot be parsed to a Duration: fraction", var0, 0)).initCause(var4);
         }
      } else {
         return 0;
      }
   }

   private static Duration create(boolean var0, long var1, long var3, long var5, long var7, int var9) {
      long var10 = Math.addExact(var1, Math.addExact(var3, Math.addExact(var5, var7)));
      return var0 ? ofSeconds(var10, (long)var9).negated() : ofSeconds(var10, (long)var9);
   }

   public static Duration between(Temporal var0, Temporal var1) {
      try {
         return ofNanos(var0.until(var1, ChronoUnit.NANOS));
      } catch (ArithmeticException | DateTimeException var9) {
         long var3 = var0.until(var1, ChronoUnit.SECONDS);

         long var5;
         try {
            var5 = var1.getLong(ChronoField.NANO_OF_SECOND) - var0.getLong(ChronoField.NANO_OF_SECOND);
            if (var3 > 0L && var5 < 0L) {
               ++var3;
            } else if (var3 < 0L && var5 > 0L) {
               --var3;
            }
         } catch (DateTimeException var8) {
            var5 = 0L;
         }

         return ofSeconds(var3, var5);
      }
   }

   private static Duration create(long var0, int var2) {
      return (var0 | (long)var2) == 0L ? ZERO : new Duration(var0, var2);
   }

   private Duration(long var1, int var3) {
      this.seconds = var1;
      this.nanos = var3;
   }

   public long get(TemporalUnit var1) {
      if (var1 == ChronoUnit.SECONDS) {
         return this.seconds;
      } else if (var1 == ChronoUnit.NANOS) {
         return (long)this.nanos;
      } else {
         throw new UnsupportedTemporalTypeException("Unsupported unit: " + var1);
      }
   }

   public List<TemporalUnit> getUnits() {
      return Duration.DurationUnits.UNITS;
   }

   public boolean isZero() {
      return (this.seconds | (long)this.nanos) == 0L;
   }

   public boolean isNegative() {
      return this.seconds < 0L;
   }

   public long getSeconds() {
      return this.seconds;
   }

   public int getNano() {
      return this.nanos;
   }

   public Duration withSeconds(long var1) {
      return create(var1, this.nanos);
   }

   public Duration withNanos(int var1) {
      ChronoField.NANO_OF_SECOND.checkValidIntValue((long)var1);
      return create(this.seconds, var1);
   }

   public Duration plus(Duration var1) {
      return this.plus(var1.getSeconds(), (long)var1.getNano());
   }

   public Duration plus(long var1, TemporalUnit var3) {
      Objects.requireNonNull(var3, (String)"unit");
      if (var3 == ChronoUnit.DAYS) {
         return this.plus(Math.multiplyExact(var1, 86400L), 0L);
      } else if (var3.isDurationEstimated()) {
         throw new UnsupportedTemporalTypeException("Unit must not have an estimated duration");
      } else if (var1 == 0L) {
         return this;
      } else if (var3 instanceof ChronoUnit) {
         switch((ChronoUnit)var3) {
         case NANOS:
            return this.plusNanos(var1);
         case MICROS:
            return this.plusSeconds(var1 / 1000000000L * 1000L).plusNanos(var1 % 1000000000L * 1000L);
         case MILLIS:
            return this.plusMillis(var1);
         case SECONDS:
            return this.plusSeconds(var1);
         default:
            return this.plusSeconds(Math.multiplyExact(var3.getDuration().seconds, var1));
         }
      } else {
         Duration var4 = var3.getDuration().multipliedBy(var1);
         return this.plusSeconds(var4.getSeconds()).plusNanos((long)var4.getNano());
      }
   }

   public Duration plusDays(long var1) {
      return this.plus(Math.multiplyExact(var1, 86400L), 0L);
   }

   public Duration plusHours(long var1) {
      return this.plus(Math.multiplyExact(var1, 3600L), 0L);
   }

   public Duration plusMinutes(long var1) {
      return this.plus(Math.multiplyExact(var1, 60L), 0L);
   }

   public Duration plusSeconds(long var1) {
      return this.plus(var1, 0L);
   }

   public Duration plusMillis(long var1) {
      return this.plus(var1 / 1000L, var1 % 1000L * 1000000L);
   }

   public Duration plusNanos(long var1) {
      return this.plus(0L, var1);
   }

   private Duration plus(long var1, long var3) {
      if ((var1 | var3) == 0L) {
         return this;
      } else {
         long var5 = Math.addExact(this.seconds, var1);
         var5 = Math.addExact(var5, var3 / 1000000000L);
         var3 %= 1000000000L;
         long var7 = (long)this.nanos + var3;
         return ofSeconds(var5, var7);
      }
   }

   public Duration minus(Duration var1) {
      long var2 = var1.getSeconds();
      int var4 = var1.getNano();
      return var2 == Long.MIN_VALUE ? this.plus(Long.MAX_VALUE, (long)(-var4)).plus(1L, 0L) : this.plus(-var2, (long)(-var4));
   }

   public Duration minus(long var1, TemporalUnit var3) {
      return var1 == Long.MIN_VALUE ? this.plus(Long.MAX_VALUE, var3).plus(1L, var3) : this.plus(-var1, var3);
   }

   public Duration minusDays(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusDays(Long.MAX_VALUE).plusDays(1L) : this.plusDays(-var1);
   }

   public Duration minusHours(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusHours(Long.MAX_VALUE).plusHours(1L) : this.plusHours(-var1);
   }

   public Duration minusMinutes(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusMinutes(Long.MAX_VALUE).plusMinutes(1L) : this.plusMinutes(-var1);
   }

   public Duration minusSeconds(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusSeconds(Long.MAX_VALUE).plusSeconds(1L) : this.plusSeconds(-var1);
   }

   public Duration minusMillis(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusMillis(Long.MAX_VALUE).plusMillis(1L) : this.plusMillis(-var1);
   }

   public Duration minusNanos(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusNanos(Long.MAX_VALUE).plusNanos(1L) : this.plusNanos(-var1);
   }

   public Duration multipliedBy(long var1) {
      if (var1 == 0L) {
         return ZERO;
      } else {
         return var1 == 1L ? this : create(this.toSeconds().multiply(BigDecimal.valueOf(var1)));
      }
   }

   public Duration dividedBy(long var1) {
      if (var1 == 0L) {
         throw new ArithmeticException("Cannot divide by zero");
      } else {
         return var1 == 1L ? this : create(this.toSeconds().divide(BigDecimal.valueOf(var1), RoundingMode.DOWN));
      }
   }

   private BigDecimal toSeconds() {
      return BigDecimal.valueOf(this.seconds).add(BigDecimal.valueOf((long)this.nanos, 9));
   }

   private static Duration create(BigDecimal var0) {
      BigInteger var1 = var0.movePointRight(9).toBigIntegerExact();
      BigInteger[] var2 = var1.divideAndRemainder(BI_NANOS_PER_SECOND);
      if (var2[0].bitLength() > 63) {
         throw new ArithmeticException("Exceeds capacity of Duration: " + var1);
      } else {
         return ofSeconds(var2[0].longValue(), (long)var2[1].intValue());
      }
   }

   public Duration negated() {
      return this.multipliedBy(-1L);
   }

   public Duration abs() {
      return this.isNegative() ? this.negated() : this;
   }

   public Temporal addTo(Temporal var1) {
      if (this.seconds != 0L) {
         var1 = var1.plus(this.seconds, ChronoUnit.SECONDS);
      }

      if (this.nanos != 0) {
         var1 = var1.plus((long)this.nanos, ChronoUnit.NANOS);
      }

      return var1;
   }

   public Temporal subtractFrom(Temporal var1) {
      if (this.seconds != 0L) {
         var1 = var1.minus(this.seconds, ChronoUnit.SECONDS);
      }

      if (this.nanos != 0) {
         var1 = var1.minus((long)this.nanos, ChronoUnit.NANOS);
      }

      return var1;
   }

   public long toDays() {
      return this.seconds / 86400L;
   }

   public long toHours() {
      return this.seconds / 3600L;
   }

   public long toMinutes() {
      return this.seconds / 60L;
   }

   public long toMillis() {
      long var1 = Math.multiplyExact(this.seconds, 1000L);
      var1 = Math.addExact(var1, (long)(this.nanos / 1000000));
      return var1;
   }

   public long toNanos() {
      long var1 = Math.multiplyExact(this.seconds, 1000000000L);
      var1 = Math.addExact(var1, (long)this.nanos);
      return var1;
   }

   public int compareTo(Duration var1) {
      int var2 = Long.compare(this.seconds, var1.seconds);
      return var2 != 0 ? var2 : this.nanos - var1.nanos;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Duration)) {
         return false;
      } else {
         Duration var2 = (Duration)var1;
         return this.seconds == var2.seconds && this.nanos == var2.nanos;
      }
   }

   public int hashCode() {
      return (int)(this.seconds ^ this.seconds >>> 32) + 51 * this.nanos;
   }

   public String toString() {
      if (this == ZERO) {
         return "PT0S";
      } else {
         long var1 = this.seconds / 3600L;
         int var3 = (int)(this.seconds % 3600L / 60L);
         int var4 = (int)(this.seconds % 60L);
         StringBuilder var5 = new StringBuilder(24);
         var5.append("PT");
         if (var1 != 0L) {
            var5.append(var1).append('H');
         }

         if (var3 != 0) {
            var5.append(var3).append('M');
         }

         if (var4 == 0 && this.nanos == 0 && var5.length() > 2) {
            return var5.toString();
         } else {
            if (var4 < 0 && this.nanos > 0) {
               if (var4 == -1) {
                  var5.append("-0");
               } else {
                  var5.append(var4 + 1);
               }
            } else {
               var5.append(var4);
            }

            if (this.nanos > 0) {
               int var6 = var5.length();
               if (var4 < 0) {
                  var5.append(2000000000L - (long)this.nanos);
               } else {
                  var5.append((long)this.nanos + 1000000000L);
               }

               while(var5.charAt(var5.length() - 1) == '0') {
                  var5.setLength(var5.length() - 1);
               }

               var5.setCharAt(var6, '.');
            }

            var5.append('S');
            return var5.toString();
         }
      }
   }

   private Object writeReplace() {
      return new Ser((byte)1, this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void writeExternal(DataOutput var1) throws IOException {
      var1.writeLong(this.seconds);
      var1.writeInt(this.nanos);
   }

   static Duration readExternal(DataInput var0) throws IOException {
      long var1 = var0.readLong();
      int var3 = var0.readInt();
      return ofSeconds(var1, (long)var3);
   }

   private static class DurationUnits {
      static final List<TemporalUnit> UNITS;

      static {
         UNITS = Collections.unmodifiableList(Arrays.asList(ChronoUnit.SECONDS, ChronoUnit.NANOS));
      }
   }
}
