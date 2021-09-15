package java.nio.file.attribute;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class FileTime implements Comparable<FileTime> {
   private final TimeUnit unit;
   private final long value;
   private Instant instant;
   private String valueAsString;
   private static final long HOURS_PER_DAY = 24L;
   private static final long MINUTES_PER_HOUR = 60L;
   private static final long SECONDS_PER_MINUTE = 60L;
   private static final long SECONDS_PER_HOUR = 3600L;
   private static final long SECONDS_PER_DAY = 86400L;
   private static final long MILLIS_PER_SECOND = 1000L;
   private static final long MICROS_PER_SECOND = 1000000L;
   private static final long NANOS_PER_SECOND = 1000000000L;
   private static final int NANOS_PER_MILLI = 1000000;
   private static final int NANOS_PER_MICRO = 1000;
   private static final long MIN_SECOND = -31557014167219200L;
   private static final long MAX_SECOND = 31556889864403199L;
   private static final long DAYS_PER_10000_YEARS = 3652425L;
   private static final long SECONDS_PER_10000_YEARS = 315569520000L;
   private static final long SECONDS_0000_TO_1970 = 62167219200L;

   private FileTime(long var1, TimeUnit var3, Instant var4) {
      this.value = var1;
      this.unit = var3;
      this.instant = var4;
   }

   public static FileTime from(long var0, TimeUnit var2) {
      Objects.requireNonNull(var2, (String)"unit");
      return new FileTime(var0, var2, (Instant)null);
   }

   public static FileTime fromMillis(long var0) {
      return new FileTime(var0, TimeUnit.MILLISECONDS, (Instant)null);
   }

   public static FileTime from(Instant var0) {
      Objects.requireNonNull(var0, (String)"instant");
      return new FileTime(0L, (TimeUnit)null, var0);
   }

   public long to(TimeUnit var1) {
      Objects.requireNonNull(var1, (String)"unit");
      if (this.unit != null) {
         return var1.convert(this.value, this.unit);
      } else {
         long var2 = var1.convert(this.instant.getEpochSecond(), TimeUnit.SECONDS);
         if (var2 != Long.MIN_VALUE && var2 != Long.MAX_VALUE) {
            long var4 = var1.convert((long)this.instant.getNano(), TimeUnit.NANOSECONDS);
            long var6 = var2 + var4;
            if (((var2 ^ var6) & (var4 ^ var6)) < 0L) {
               return var2 < 0L ? Long.MIN_VALUE : Long.MAX_VALUE;
            } else {
               return var6;
            }
         } else {
            return var2;
         }
      }
   }

   public long toMillis() {
      if (this.unit != null) {
         return this.unit.toMillis(this.value);
      } else {
         long var1 = this.instant.getEpochSecond();
         int var3 = this.instant.getNano();
         long var4 = var1 * 1000L;
         long var6 = Math.abs(var1);
         if ((var6 | 1000L) >>> 31 != 0L && var4 / 1000L != var1) {
            return var1 < 0L ? Long.MIN_VALUE : Long.MAX_VALUE;
         } else {
            return var4 + (long)(var3 / 1000000);
         }
      }
   }

   private static long scale(long var0, long var2, long var4) {
      if (var0 > var4) {
         return Long.MAX_VALUE;
      } else {
         return var0 < -var4 ? Long.MIN_VALUE : var0 * var2;
      }
   }

   public Instant toInstant() {
      if (this.instant == null) {
         long var1 = 0L;
         int var3 = 0;
         switch(this.unit) {
         case DAYS:
            var1 = scale(this.value, 86400L, 106751991167300L);
            break;
         case HOURS:
            var1 = scale(this.value, 3600L, 2562047788015215L);
            break;
         case MINUTES:
            var1 = scale(this.value, 60L, 153722867280912930L);
            break;
         case SECONDS:
            var1 = this.value;
            break;
         case MILLISECONDS:
            var1 = Math.floorDiv(this.value, 1000L);
            var3 = (int)Math.floorMod(this.value, 1000L) * 1000000;
            break;
         case MICROSECONDS:
            var1 = Math.floorDiv(this.value, 1000000L);
            var3 = (int)Math.floorMod(this.value, 1000000L) * 1000;
            break;
         case NANOSECONDS:
            var1 = Math.floorDiv(this.value, 1000000000L);
            var3 = (int)Math.floorMod(this.value, 1000000000L);
            break;
         default:
            throw new AssertionError("Unit not handled");
         }

         if (var1 <= -31557014167219200L) {
            this.instant = Instant.MIN;
         } else if (var1 >= 31556889864403199L) {
            this.instant = Instant.MAX;
         } else {
            this.instant = Instant.ofEpochSecond(var1, (long)var3);
         }
      }

      return this.instant;
   }

   public boolean equals(Object var1) {
      return var1 instanceof FileTime ? this.compareTo((FileTime)var1) == 0 : false;
   }

   public int hashCode() {
      return this.toInstant().hashCode();
   }

   private long toDays() {
      return this.unit != null ? this.unit.toDays(this.value) : TimeUnit.SECONDS.toDays(this.toInstant().getEpochSecond());
   }

   private long toExcessNanos(long var1) {
      return this.unit != null ? this.unit.toNanos(this.value - this.unit.convert(var1, TimeUnit.DAYS)) : TimeUnit.SECONDS.toNanos(this.toInstant().getEpochSecond() - TimeUnit.DAYS.toSeconds(var1));
   }

   public int compareTo(FileTime var1) {
      if (this.unit != null && this.unit == var1.unit) {
         return Long.compare(this.value, var1.value);
      } else {
         long var2 = this.toInstant().getEpochSecond();
         long var4 = var1.toInstant().getEpochSecond();
         int var6 = Long.compare(var2, var4);
         if (var6 != 0) {
            return var6;
         } else {
            var6 = Long.compare((long)this.toInstant().getNano(), (long)var1.toInstant().getNano());
            if (var6 != 0) {
               return var6;
            } else if (var2 != 31556889864403199L && var2 != -31557014167219200L) {
               return 0;
            } else {
               long var7 = this.toDays();
               long var9 = var1.toDays();
               return var7 == var9 ? Long.compare(this.toExcessNanos(var7), var1.toExcessNanos(var9)) : Long.compare(var7, var9);
            }
         }
      }
   }

   private StringBuilder append(StringBuilder var1, int var2, int var3) {
      while(var2 > 0) {
         var1.append((char)(var3 / var2 + 48));
         var3 %= var2;
         var2 /= 10;
      }

      return var1;
   }

   public String toString() {
      if (this.valueAsString == null) {
         long var1 = 0L;
         int var3 = 0;
         if (this.instant == null && this.unit.compareTo(TimeUnit.SECONDS) >= 0) {
            var1 = this.unit.toSeconds(this.value);
         } else {
            var1 = this.toInstant().getEpochSecond();
            var3 = this.toInstant().getNano();
         }

         boolean var5 = false;
         LocalDateTime var4;
         long var6;
         long var8;
         long var10;
         int var12;
         if (var1 >= -62167219200L) {
            var6 = var1 - 315569520000L + 62167219200L;
            var8 = Math.floorDiv(var6, 315569520000L) + 1L;
            var10 = Math.floorMod(var6, 315569520000L);
            var4 = LocalDateTime.ofEpochSecond(var10 - 62167219200L, var3, ZoneOffset.UTC);
            var12 = var4.getYear() + (int)var8 * 10000;
         } else {
            var6 = var1 + 62167219200L;
            var8 = var6 / 315569520000L;
            var10 = var6 % 315569520000L;
            var4 = LocalDateTime.ofEpochSecond(var10 - 62167219200L, var3, ZoneOffset.UTC);
            var12 = var4.getYear() + (int)var8 * 10000;
         }

         if (var12 <= 0) {
            --var12;
         }

         int var13 = var4.getNano();
         StringBuilder var7 = new StringBuilder(64);
         var7.append(var12 < 0 ? "-" : "");
         var12 = Math.abs(var12);
         if (var12 < 10000) {
            this.append(var7, 1000, Math.abs(var12));
         } else {
            var7.append(String.valueOf(var12));
         }

         var7.append('-');
         this.append(var7, 10, var4.getMonthValue());
         var7.append('-');
         this.append(var7, 10, var4.getDayOfMonth());
         var7.append('T');
         this.append(var7, 10, var4.getHour());
         var7.append(':');
         this.append(var7, 10, var4.getMinute());
         var7.append(':');
         this.append(var7, 10, var4.getSecond());
         if (var13 != 0) {
            var7.append('.');

            int var14;
            for(var14 = 100000000; var13 % 10 == 0; var14 /= 10) {
               var13 /= 10;
            }

            this.append(var7, var14, var13);
         }

         var7.append('Z');
         this.valueAsString = var7.toString();
      }

      return this.valueAsString;
   }
}
