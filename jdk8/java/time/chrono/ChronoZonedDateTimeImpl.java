package java.time.chrono;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;
import java.util.List;
import java.util.Objects;

final class ChronoZonedDateTimeImpl<D extends ChronoLocalDate> implements ChronoZonedDateTime<D>, Serializable {
   private static final long serialVersionUID = -5261813987200935591L;
   private final transient ChronoLocalDateTimeImpl<D> dateTime;
   private final transient ZoneOffset offset;
   private final transient ZoneId zone;

   static <R extends ChronoLocalDate> ChronoZonedDateTime<R> ofBest(ChronoLocalDateTimeImpl<R> var0, ZoneId var1, ZoneOffset var2) {
      Objects.requireNonNull(var0, (String)"localDateTime");
      Objects.requireNonNull(var1, (String)"zone");
      if (var1 instanceof ZoneOffset) {
         return new ChronoZonedDateTimeImpl(var0, (ZoneOffset)var1, var1);
      } else {
         ZoneRules var3 = var1.getRules();
         LocalDateTime var4 = LocalDateTime.from(var0);
         List var5 = var3.getValidOffsets(var4);
         ZoneOffset var6;
         if (var5.size() == 1) {
            var6 = (ZoneOffset)var5.get(0);
         } else if (var5.size() == 0) {
            ZoneOffsetTransition var7 = var3.getTransition(var4);
            var0 = var0.plusSeconds(var7.getDuration().getSeconds());
            var6 = var7.getOffsetAfter();
         } else if (var2 != null && var5.contains(var2)) {
            var6 = var2;
         } else {
            var6 = (ZoneOffset)var5.get(0);
         }

         Objects.requireNonNull(var6, (String)"offset");
         return new ChronoZonedDateTimeImpl(var0, var6, var1);
      }
   }

   static ChronoZonedDateTimeImpl<?> ofInstant(Chronology var0, Instant var1, ZoneId var2) {
      ZoneRules var3 = var2.getRules();
      ZoneOffset var4 = var3.getOffset(var1);
      Objects.requireNonNull(var4, (String)"offset");
      LocalDateTime var5 = LocalDateTime.ofEpochSecond(var1.getEpochSecond(), var1.getNano(), var4);
      ChronoLocalDateTimeImpl var6 = (ChronoLocalDateTimeImpl)var0.localDateTime(var5);
      return new ChronoZonedDateTimeImpl(var6, var4, var2);
   }

   private ChronoZonedDateTimeImpl<D> create(Instant var1, ZoneId var2) {
      return ofInstant(this.getChronology(), var1, var2);
   }

   static <R extends ChronoLocalDate> ChronoZonedDateTimeImpl<R> ensureValid(Chronology var0, Temporal var1) {
      ChronoZonedDateTimeImpl var2 = (ChronoZonedDateTimeImpl)var1;
      if (!var0.equals(var2.getChronology())) {
         throw new ClassCastException("Chronology mismatch, required: " + var0.getId() + ", actual: " + var2.getChronology().getId());
      } else {
         return var2;
      }
   }

   private ChronoZonedDateTimeImpl(ChronoLocalDateTimeImpl<D> var1, ZoneOffset var2, ZoneId var3) {
      this.dateTime = (ChronoLocalDateTimeImpl)Objects.requireNonNull(var1, (String)"dateTime");
      this.offset = (ZoneOffset)Objects.requireNonNull(var2, (String)"offset");
      this.zone = (ZoneId)Objects.requireNonNull(var3, (String)"zone");
   }

   public ZoneOffset getOffset() {
      return this.offset;
   }

   public ChronoZonedDateTime<D> withEarlierOffsetAtOverlap() {
      ZoneOffsetTransition var1 = this.getZone().getRules().getTransition(LocalDateTime.from(this));
      if (var1 != null && var1.isOverlap()) {
         ZoneOffset var2 = var1.getOffsetBefore();
         if (!var2.equals(this.offset)) {
            return new ChronoZonedDateTimeImpl(this.dateTime, var2, this.zone);
         }
      }

      return this;
   }

   public ChronoZonedDateTime<D> withLaterOffsetAtOverlap() {
      ZoneOffsetTransition var1 = this.getZone().getRules().getTransition(LocalDateTime.from(this));
      if (var1 != null) {
         ZoneOffset var2 = var1.getOffsetAfter();
         if (!var2.equals(this.getOffset())) {
            return new ChronoZonedDateTimeImpl(this.dateTime, var2, this.zone);
         }
      }

      return this;
   }

   public ChronoLocalDateTime<D> toLocalDateTime() {
      return this.dateTime;
   }

   public ZoneId getZone() {
      return this.zone;
   }

   public ChronoZonedDateTime<D> withZoneSameLocal(ZoneId var1) {
      return ofBest(this.dateTime, var1, this.offset);
   }

   public ChronoZonedDateTime<D> withZoneSameInstant(ZoneId var1) {
      Objects.requireNonNull(var1, (String)"zone");
      return this.zone.equals(var1) ? this : this.create(this.dateTime.toInstant(this.offset), var1);
   }

   public boolean isSupported(TemporalField var1) {
      return var1 instanceof ChronoField || var1 != null && var1.isSupportedBy(this);
   }

   public ChronoZonedDateTime<D> with(TemporalField var1, long var2) {
      if (var1 instanceof ChronoField) {
         ChronoField var4 = (ChronoField)var1;
         switch(var4) {
         case INSTANT_SECONDS:
            return this.plus(var2 - this.toEpochSecond(), ChronoUnit.SECONDS);
         case OFFSET_SECONDS:
            ZoneOffset var5 = ZoneOffset.ofTotalSeconds(var4.checkValidIntValue(var2));
            return this.create(this.dateTime.toInstant(var5), this.zone);
         default:
            return ofBest(this.dateTime.with(var1, var2), this.zone, this.offset);
         }
      } else {
         return ensureValid(this.getChronology(), var1.adjustInto(this, var2));
      }
   }

   public ChronoZonedDateTime<D> plus(long var1, TemporalUnit var3) {
      return (ChronoZonedDateTime)(var3 instanceof ChronoUnit ? this.with(this.dateTime.plus(var1, var3)) : ensureValid(this.getChronology(), var3.addTo(this, var1)));
   }

   public long until(Temporal var1, TemporalUnit var2) {
      Objects.requireNonNull(var1, (String)"endExclusive");
      ChronoZonedDateTime var3 = this.getChronology().zonedDateTime(var1);
      if (var2 instanceof ChronoUnit) {
         var3 = var3.withZoneSameInstant(this.offset);
         return this.dateTime.until(var3.toLocalDateTime(), var2);
      } else {
         Objects.requireNonNull(var2, (String)"unit");
         return var2.between(this, var3);
      }
   }

   private Object writeReplace() {
      return new Ser((byte)3, this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeObject(this.dateTime);
      var1.writeObject(this.offset);
      var1.writeObject(this.zone);
   }

   static ChronoZonedDateTime<?> readExternal(ObjectInput var0) throws IOException, ClassNotFoundException {
      ChronoLocalDateTime var1 = (ChronoLocalDateTime)var0.readObject();
      ZoneOffset var2 = (ZoneOffset)var0.readObject();
      ZoneId var3 = (ZoneId)var0.readObject();
      return var1.atZone(var2).withZoneSameLocal(var3);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof ChronoZonedDateTime) {
         return this.compareTo((ChronoZonedDateTime)var1) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.toLocalDateTime().hashCode() ^ this.getOffset().hashCode() ^ Integer.rotateLeft(this.getZone().hashCode(), 3);
   }

   public String toString() {
      String var1 = this.toLocalDateTime().toString() + this.getOffset().toString();
      if (this.getOffset() != this.getZone()) {
         var1 = var1 + '[' + this.getZone().toString() + ']';
      }

      return var1;
   }
}
