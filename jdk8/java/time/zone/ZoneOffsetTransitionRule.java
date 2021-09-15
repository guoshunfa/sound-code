package java.time.zone;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.chrono.IsoChronology;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

public final class ZoneOffsetTransitionRule implements Serializable {
   private static final long serialVersionUID = 6889046316657758795L;
   private final Month month;
   private final byte dom;
   private final DayOfWeek dow;
   private final LocalTime time;
   private final boolean timeEndOfDay;
   private final ZoneOffsetTransitionRule.TimeDefinition timeDefinition;
   private final ZoneOffset standardOffset;
   private final ZoneOffset offsetBefore;
   private final ZoneOffset offsetAfter;

   public static ZoneOffsetTransitionRule of(Month var0, int var1, DayOfWeek var2, LocalTime var3, boolean var4, ZoneOffsetTransitionRule.TimeDefinition var5, ZoneOffset var6, ZoneOffset var7, ZoneOffset var8) {
      Objects.requireNonNull(var0, (String)"month");
      Objects.requireNonNull(var3, (String)"time");
      Objects.requireNonNull(var5, (String)"timeDefnition");
      Objects.requireNonNull(var6, (String)"standardOffset");
      Objects.requireNonNull(var7, (String)"offsetBefore");
      Objects.requireNonNull(var8, (String)"offsetAfter");
      if (var1 >= -28 && var1 <= 31 && var1 != 0) {
         if (var4 && !var3.equals(LocalTime.MIDNIGHT)) {
            throw new IllegalArgumentException("Time must be midnight when end of day flag is true");
         } else {
            return new ZoneOffsetTransitionRule(var0, var1, var2, var3, var4, var5, var6, var7, var8);
         }
      } else {
         throw new IllegalArgumentException("Day of month indicator must be between -28 and 31 inclusive excluding zero");
      }
   }

   ZoneOffsetTransitionRule(Month var1, int var2, DayOfWeek var3, LocalTime var4, boolean var5, ZoneOffsetTransitionRule.TimeDefinition var6, ZoneOffset var7, ZoneOffset var8, ZoneOffset var9) {
      this.month = var1;
      this.dom = (byte)var2;
      this.dow = var3;
      this.time = var4;
      this.timeEndOfDay = var5;
      this.timeDefinition = var6;
      this.standardOffset = var7;
      this.offsetBefore = var8;
      this.offsetAfter = var9;
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   private Object writeReplace() {
      return new Ser((byte)3, this);
   }

   void writeExternal(DataOutput var1) throws IOException {
      int var2 = this.timeEndOfDay ? 86400 : this.time.toSecondOfDay();
      int var3 = this.standardOffset.getTotalSeconds();
      int var4 = this.offsetBefore.getTotalSeconds() - var3;
      int var5 = this.offsetAfter.getTotalSeconds() - var3;
      int var6 = var2 % 3600 == 0 ? (this.timeEndOfDay ? 24 : this.time.getHour()) : 31;
      int var7 = var3 % 900 == 0 ? var3 / 900 + 128 : 255;
      int var8 = var4 != 0 && var4 != 1800 && var4 != 3600 ? 3 : var4 / 1800;
      int var9 = var5 != 0 && var5 != 1800 && var5 != 3600 ? 3 : var5 / 1800;
      int var10 = this.dow == null ? 0 : this.dow.getValue();
      int var11 = (this.month.getValue() << 28) + (this.dom + 32 << 22) + (var10 << 19) + (var6 << 14) + (this.timeDefinition.ordinal() << 12) + (var7 << 4) + (var8 << 2) + var9;
      var1.writeInt(var11);
      if (var6 == 31) {
         var1.writeInt(var2);
      }

      if (var7 == 255) {
         var1.writeInt(var3);
      }

      if (var8 == 3) {
         var1.writeInt(this.offsetBefore.getTotalSeconds());
      }

      if (var9 == 3) {
         var1.writeInt(this.offsetAfter.getTotalSeconds());
      }

   }

   static ZoneOffsetTransitionRule readExternal(DataInput var0) throws IOException {
      int var1 = var0.readInt();
      Month var2 = Month.of(var1 >>> 28);
      int var3 = ((var1 & 264241152) >>> 22) - 32;
      int var4 = (var1 & 3670016) >>> 19;
      DayOfWeek var5 = var4 == 0 ? null : DayOfWeek.of(var4);
      int var6 = (var1 & 507904) >>> 14;
      ZoneOffsetTransitionRule.TimeDefinition var7 = ZoneOffsetTransitionRule.TimeDefinition.values()[(var1 & 12288) >>> 12];
      int var8 = (var1 & 4080) >>> 4;
      int var9 = (var1 & 12) >>> 2;
      int var10 = var1 & 3;
      LocalTime var11 = var6 == 31 ? LocalTime.ofSecondOfDay((long)var0.readInt()) : LocalTime.of(var6 % 24, 0);
      ZoneOffset var12 = var8 == 255 ? ZoneOffset.ofTotalSeconds(var0.readInt()) : ZoneOffset.ofTotalSeconds((var8 - 128) * 900);
      ZoneOffset var13 = var9 == 3 ? ZoneOffset.ofTotalSeconds(var0.readInt()) : ZoneOffset.ofTotalSeconds(var12.getTotalSeconds() + var9 * 1800);
      ZoneOffset var14 = var10 == 3 ? ZoneOffset.ofTotalSeconds(var0.readInt()) : ZoneOffset.ofTotalSeconds(var12.getTotalSeconds() + var10 * 1800);
      return of(var2, var3, var5, var11, var6 == 24, var7, var12, var13, var14);
   }

   public Month getMonth() {
      return this.month;
   }

   public int getDayOfMonthIndicator() {
      return this.dom;
   }

   public DayOfWeek getDayOfWeek() {
      return this.dow;
   }

   public LocalTime getLocalTime() {
      return this.time;
   }

   public boolean isMidnightEndOfDay() {
      return this.timeEndOfDay;
   }

   public ZoneOffsetTransitionRule.TimeDefinition getTimeDefinition() {
      return this.timeDefinition;
   }

   public ZoneOffset getStandardOffset() {
      return this.standardOffset;
   }

   public ZoneOffset getOffsetBefore() {
      return this.offsetBefore;
   }

   public ZoneOffset getOffsetAfter() {
      return this.offsetAfter;
   }

   public ZoneOffsetTransition createTransition(int var1) {
      LocalDate var2;
      if (this.dom < 0) {
         var2 = LocalDate.of(var1, this.month, this.month.length(IsoChronology.INSTANCE.isLeapYear((long)var1)) + 1 + this.dom);
         if (this.dow != null) {
            var2 = var2.with(TemporalAdjusters.previousOrSame(this.dow));
         }
      } else {
         var2 = LocalDate.of(var1, this.month, this.dom);
         if (this.dow != null) {
            var2 = var2.with(TemporalAdjusters.nextOrSame(this.dow));
         }
      }

      if (this.timeEndOfDay) {
         var2 = var2.plusDays(1L);
      }

      LocalDateTime var3 = LocalDateTime.of(var2, this.time);
      LocalDateTime var4 = this.timeDefinition.createDateTime(var3, this.standardOffset, this.offsetBefore);
      return new ZoneOffsetTransition(var4, this.offsetBefore, this.offsetAfter);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ZoneOffsetTransitionRule)) {
         return false;
      } else {
         ZoneOffsetTransitionRule var2 = (ZoneOffsetTransitionRule)var1;
         return this.month == var2.month && this.dom == var2.dom && this.dow == var2.dow && this.timeDefinition == var2.timeDefinition && this.time.equals(var2.time) && this.timeEndOfDay == var2.timeEndOfDay && this.standardOffset.equals(var2.standardOffset) && this.offsetBefore.equals(var2.offsetBefore) && this.offsetAfter.equals(var2.offsetAfter);
      }
   }

   public int hashCode() {
      int var1 = (this.time.toSecondOfDay() + (this.timeEndOfDay ? 1 : 0) << 15) + (this.month.ordinal() << 11) + (this.dom + 32 << 5) + ((this.dow == null ? 7 : this.dow.ordinal()) << 2) + this.timeDefinition.ordinal();
      return var1 ^ this.standardOffset.hashCode() ^ this.offsetBefore.hashCode() ^ this.offsetAfter.hashCode();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("TransitionRule[").append(this.offsetBefore.compareTo(this.offsetAfter) > 0 ? "Gap " : "Overlap ").append((Object)this.offsetBefore).append(" to ").append((Object)this.offsetAfter).append(", ");
      if (this.dow != null) {
         if (this.dom == -1) {
            var1.append(this.dow.name()).append(" on or before last day of ").append(this.month.name());
         } else if (this.dom < 0) {
            var1.append(this.dow.name()).append(" on or before last day minus ").append(-this.dom - 1).append(" of ").append(this.month.name());
         } else {
            var1.append(this.dow.name()).append(" on or after ").append(this.month.name()).append(' ').append((int)this.dom);
         }
      } else {
         var1.append(this.month.name()).append(' ').append((int)this.dom);
      }

      var1.append(" at ").append(this.timeEndOfDay ? "24:00" : this.time.toString()).append(" ").append((Object)this.timeDefinition).append(", standard offset ").append((Object)this.standardOffset).append(']');
      return var1.toString();
   }

   public static enum TimeDefinition {
      UTC,
      WALL,
      STANDARD;

      public LocalDateTime createDateTime(LocalDateTime var1, ZoneOffset var2, ZoneOffset var3) {
         int var4;
         switch(this) {
         case UTC:
            var4 = var3.getTotalSeconds() - ZoneOffset.UTC.getTotalSeconds();
            return var1.plusSeconds((long)var4);
         case STANDARD:
            var4 = var3.getTotalSeconds() - var2.getTotalSeconds();
            return var1.plusSeconds((long)var4);
         default:
            return var1;
         }
      }
   }
}
