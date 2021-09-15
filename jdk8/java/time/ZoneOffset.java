package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.time.zone.ZoneRules;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ZoneOffset extends ZoneId implements TemporalAccessor, TemporalAdjuster, Comparable<ZoneOffset>, Serializable {
   private static final ConcurrentMap<Integer, ZoneOffset> SECONDS_CACHE = new ConcurrentHashMap(16, 0.75F, 4);
   private static final ConcurrentMap<String, ZoneOffset> ID_CACHE = new ConcurrentHashMap(16, 0.75F, 4);
   private static final int MAX_SECONDS = 64800;
   private static final long serialVersionUID = 2357656521762053153L;
   public static final ZoneOffset UTC = ofTotalSeconds(0);
   public static final ZoneOffset MIN = ofTotalSeconds(-64800);
   public static final ZoneOffset MAX = ofTotalSeconds(64800);
   private final int totalSeconds;
   private final transient String id;

   public static ZoneOffset of(String var0) {
      Objects.requireNonNull(var0, (String)"offsetId");
      ZoneOffset var1 = (ZoneOffset)ID_CACHE.get(var0);
      if (var1 != null) {
         return var1;
      } else {
         int var2;
         int var3;
         int var4;
         switch(var0.length()) {
         case 2:
            var0 = var0.charAt(0) + "0" + var0.charAt(1);
         case 3:
            var2 = parseNumber(var0, 1, false);
            var3 = 0;
            var4 = 0;
            break;
         case 4:
         case 8:
         default:
            throw new DateTimeException("Invalid ID for ZoneOffset, invalid format: " + var0);
         case 5:
            var2 = parseNumber(var0, 1, false);
            var3 = parseNumber(var0, 3, false);
            var4 = 0;
            break;
         case 6:
            var2 = parseNumber(var0, 1, false);
            var3 = parseNumber(var0, 4, true);
            var4 = 0;
            break;
         case 7:
            var2 = parseNumber(var0, 1, false);
            var3 = parseNumber(var0, 3, false);
            var4 = parseNumber(var0, 5, false);
            break;
         case 9:
            var2 = parseNumber(var0, 1, false);
            var3 = parseNumber(var0, 4, true);
            var4 = parseNumber(var0, 7, true);
         }

         char var5 = var0.charAt(0);
         if (var5 != '+' && var5 != '-') {
            throw new DateTimeException("Invalid ID for ZoneOffset, plus/minus not found when expected: " + var0);
         } else {
            return var5 == '-' ? ofHoursMinutesSeconds(-var2, -var3, -var4) : ofHoursMinutesSeconds(var2, var3, var4);
         }
      }
   }

   private static int parseNumber(CharSequence var0, int var1, boolean var2) {
      if (var2 && var0.charAt(var1 - 1) != ':') {
         throw new DateTimeException("Invalid ID for ZoneOffset, colon not found when expected: " + var0);
      } else {
         char var3 = var0.charAt(var1);
         char var4 = var0.charAt(var1 + 1);
         if (var3 >= '0' && var3 <= '9' && var4 >= '0' && var4 <= '9') {
            return (var3 - 48) * 10 + (var4 - 48);
         } else {
            throw new DateTimeException("Invalid ID for ZoneOffset, non numeric characters found: " + var0);
         }
      }
   }

   public static ZoneOffset ofHours(int var0) {
      return ofHoursMinutesSeconds(var0, 0, 0);
   }

   public static ZoneOffset ofHoursMinutes(int var0, int var1) {
      return ofHoursMinutesSeconds(var0, var1, 0);
   }

   public static ZoneOffset ofHoursMinutesSeconds(int var0, int var1, int var2) {
      validate(var0, var1, var2);
      int var3 = totalSeconds(var0, var1, var2);
      return ofTotalSeconds(var3);
   }

   public static ZoneOffset from(TemporalAccessor var0) {
      Objects.requireNonNull(var0, (String)"temporal");
      ZoneOffset var1 = (ZoneOffset)var0.query(TemporalQueries.offset());
      if (var1 == null) {
         throw new DateTimeException("Unable to obtain ZoneOffset from TemporalAccessor: " + var0 + " of type " + var0.getClass().getName());
      } else {
         return var1;
      }
   }

   private static void validate(int var0, int var1, int var2) {
      if (var0 >= -18 && var0 <= 18) {
         if (var0 > 0) {
            if (var1 < 0 || var2 < 0) {
               throw new DateTimeException("Zone offset minutes and seconds must be positive because hours is positive");
            }
         } else if (var0 < 0) {
            if (var1 > 0 || var2 > 0) {
               throw new DateTimeException("Zone offset minutes and seconds must be negative because hours is negative");
            }
         } else if (var1 > 0 && var2 < 0 || var1 < 0 && var2 > 0) {
            throw new DateTimeException("Zone offset minutes and seconds must have the same sign");
         }

         if (var1 >= -59 && var1 <= 59) {
            if (var2 >= -59 && var2 <= 59) {
               if (Math.abs(var0) == 18 && (var1 | var2) != 0) {
                  throw new DateTimeException("Zone offset not in valid range: -18:00 to +18:00");
               }
            } else {
               throw new DateTimeException("Zone offset seconds not in valid range: value " + var2 + " is not in the range -59 to 59");
            }
         } else {
            throw new DateTimeException("Zone offset minutes not in valid range: value " + var1 + " is not in the range -59 to 59");
         }
      } else {
         throw new DateTimeException("Zone offset hours not in valid range: value " + var0 + " is not in the range -18 to 18");
      }
   }

   private static int totalSeconds(int var0, int var1, int var2) {
      return var0 * 3600 + var1 * 60 + var2;
   }

   public static ZoneOffset ofTotalSeconds(int var0) {
      if (var0 >= -64800 && var0 <= 64800) {
         if (var0 % 900 == 0) {
            Integer var1 = var0;
            ZoneOffset var2 = (ZoneOffset)SECONDS_CACHE.get(var1);
            if (var2 == null) {
               var2 = new ZoneOffset(var0);
               SECONDS_CACHE.putIfAbsent(var1, var2);
               var2 = (ZoneOffset)SECONDS_CACHE.get(var1);
               ID_CACHE.putIfAbsent(var2.getId(), var2);
            }

            return var2;
         } else {
            return new ZoneOffset(var0);
         }
      } else {
         throw new DateTimeException("Zone offset not in valid range: -18:00 to +18:00");
      }
   }

   private ZoneOffset(int var1) {
      this.totalSeconds = var1;
      this.id = buildId(var1);
   }

   private static String buildId(int var0) {
      if (var0 == 0) {
         return "Z";
      } else {
         int var1 = Math.abs(var0);
         StringBuilder var2 = new StringBuilder();
         int var3 = var1 / 3600;
         int var4 = var1 / 60 % 60;
         var2.append(var0 < 0 ? "-" : "+").append(var3 < 10 ? "0" : "").append(var3).append(var4 < 10 ? ":0" : ":").append(var4);
         int var5 = var1 % 60;
         if (var5 != 0) {
            var2.append(var5 < 10 ? ":0" : ":").append(var5);
         }

         return var2.toString();
      }
   }

   public int getTotalSeconds() {
      return this.totalSeconds;
   }

   public String getId() {
      return this.id;
   }

   public ZoneRules getRules() {
      return ZoneRules.of(this);
   }

   public boolean isSupported(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         return var1 == ChronoField.OFFSET_SECONDS;
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   public ValueRange range(TemporalField var1) {
      return TemporalAccessor.super.range(var1);
   }

   public int get(TemporalField var1) {
      if (var1 == ChronoField.OFFSET_SECONDS) {
         return this.totalSeconds;
      } else if (var1 instanceof ChronoField) {
         throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
      } else {
         return this.range(var1).checkValidIntValue(this.getLong(var1), var1);
      }
   }

   public long getLong(TemporalField var1) {
      if (var1 == ChronoField.OFFSET_SECONDS) {
         return (long)this.totalSeconds;
      } else if (var1 instanceof ChronoField) {
         throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
      } else {
         return var1.getFrom(this);
      }
   }

   public <R> R query(TemporalQuery<R> var1) {
      return var1 != TemporalQueries.offset() && var1 != TemporalQueries.zone() ? TemporalAccessor.super.query(var1) : this;
   }

   public Temporal adjustInto(Temporal var1) {
      return var1.with(ChronoField.OFFSET_SECONDS, (long)this.totalSeconds);
   }

   public int compareTo(ZoneOffset var1) {
      return var1.totalSeconds - this.totalSeconds;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof ZoneOffset) {
         return this.totalSeconds == ((ZoneOffset)var1).totalSeconds;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.totalSeconds;
   }

   public String toString() {
      return this.id;
   }

   private Object writeReplace() {
      return new Ser((byte)8, this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void write(DataOutput var1) throws IOException {
      var1.writeByte(8);
      this.writeExternal(var1);
   }

   void writeExternal(DataOutput var1) throws IOException {
      int var2 = this.totalSeconds;
      int var3 = var2 % 900 == 0 ? var2 / 900 : 127;
      var1.writeByte(var3);
      if (var3 == 127) {
         var1.writeInt(var2);
      }

   }

   static ZoneOffset readExternal(DataInput var0) throws IOException {
      byte var1 = var0.readByte();
      return var1 == 127 ? ofTotalSeconds(var0.readInt()) : ofTotalSeconds(var1 * 900);
   }
}
