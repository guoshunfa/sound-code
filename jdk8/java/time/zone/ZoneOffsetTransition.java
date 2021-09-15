package java.time.zone;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ZoneOffsetTransition implements Comparable<ZoneOffsetTransition>, Serializable {
   private static final long serialVersionUID = -6946044323557704546L;
   private final LocalDateTime transition;
   private final ZoneOffset offsetBefore;
   private final ZoneOffset offsetAfter;

   public static ZoneOffsetTransition of(LocalDateTime var0, ZoneOffset var1, ZoneOffset var2) {
      Objects.requireNonNull(var0, (String)"transition");
      Objects.requireNonNull(var1, (String)"offsetBefore");
      Objects.requireNonNull(var2, (String)"offsetAfter");
      if (var1.equals(var2)) {
         throw new IllegalArgumentException("Offsets must not be equal");
      } else if (var0.getNano() != 0) {
         throw new IllegalArgumentException("Nano-of-second must be zero");
      } else {
         return new ZoneOffsetTransition(var0, var1, var2);
      }
   }

   ZoneOffsetTransition(LocalDateTime var1, ZoneOffset var2, ZoneOffset var3) {
      this.transition = var1;
      this.offsetBefore = var2;
      this.offsetAfter = var3;
   }

   ZoneOffsetTransition(long var1, ZoneOffset var3, ZoneOffset var4) {
      this.transition = LocalDateTime.ofEpochSecond(var1, 0, var3);
      this.offsetBefore = var3;
      this.offsetAfter = var4;
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   private Object writeReplace() {
      return new Ser((byte)2, this);
   }

   void writeExternal(DataOutput var1) throws IOException {
      Ser.writeEpochSec(this.toEpochSecond(), var1);
      Ser.writeOffset(this.offsetBefore, var1);
      Ser.writeOffset(this.offsetAfter, var1);
   }

   static ZoneOffsetTransition readExternal(DataInput var0) throws IOException {
      long var1 = Ser.readEpochSec(var0);
      ZoneOffset var3 = Ser.readOffset(var0);
      ZoneOffset var4 = Ser.readOffset(var0);
      if (var3.equals(var4)) {
         throw new IllegalArgumentException("Offsets must not be equal");
      } else {
         return new ZoneOffsetTransition(var1, var3, var4);
      }
   }

   public Instant getInstant() {
      return this.transition.toInstant(this.offsetBefore);
   }

   public long toEpochSecond() {
      return this.transition.toEpochSecond(this.offsetBefore);
   }

   public LocalDateTime getDateTimeBefore() {
      return this.transition;
   }

   public LocalDateTime getDateTimeAfter() {
      return this.transition.plusSeconds((long)this.getDurationSeconds());
   }

   public ZoneOffset getOffsetBefore() {
      return this.offsetBefore;
   }

   public ZoneOffset getOffsetAfter() {
      return this.offsetAfter;
   }

   public Duration getDuration() {
      return Duration.ofSeconds((long)this.getDurationSeconds());
   }

   private int getDurationSeconds() {
      return this.getOffsetAfter().getTotalSeconds() - this.getOffsetBefore().getTotalSeconds();
   }

   public boolean isGap() {
      return this.getOffsetAfter().getTotalSeconds() > this.getOffsetBefore().getTotalSeconds();
   }

   public boolean isOverlap() {
      return this.getOffsetAfter().getTotalSeconds() < this.getOffsetBefore().getTotalSeconds();
   }

   public boolean isValidOffset(ZoneOffset var1) {
      return this.isGap() ? false : this.getOffsetBefore().equals(var1) || this.getOffsetAfter().equals(var1);
   }

   List<ZoneOffset> getValidOffsets() {
      return this.isGap() ? Collections.emptyList() : Arrays.asList(this.getOffsetBefore(), this.getOffsetAfter());
   }

   public int compareTo(ZoneOffsetTransition var1) {
      return this.getInstant().compareTo(var1.getInstant());
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ZoneOffsetTransition)) {
         return false;
      } else {
         ZoneOffsetTransition var2 = (ZoneOffsetTransition)var1;
         return this.transition.equals(var2.transition) && this.offsetBefore.equals(var2.offsetBefore) && this.offsetAfter.equals(var2.offsetAfter);
      }
   }

   public int hashCode() {
      return this.transition.hashCode() ^ this.offsetBefore.hashCode() ^ Integer.rotateLeft(this.offsetAfter.hashCode(), 16);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Transition[").append(this.isGap() ? "Gap" : "Overlap").append(" at ").append((Object)this.transition).append((Object)this.offsetBefore).append(" to ").append((Object)this.offsetAfter).append(']');
      return var1.toString();
   }
}
