package java.time.zone;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ZoneRules implements Serializable {
   private static final long serialVersionUID = 3044319355680032515L;
   private static final int LAST_CACHED_YEAR = 2100;
   private final long[] standardTransitions;
   private final ZoneOffset[] standardOffsets;
   private final long[] savingsInstantTransitions;
   private final LocalDateTime[] savingsLocalTransitions;
   private final ZoneOffset[] wallOffsets;
   private final ZoneOffsetTransitionRule[] lastRules;
   private final transient ConcurrentMap<Integer, ZoneOffsetTransition[]> lastRulesCache = new ConcurrentHashMap();
   private static final long[] EMPTY_LONG_ARRAY = new long[0];
   private static final ZoneOffsetTransitionRule[] EMPTY_LASTRULES = new ZoneOffsetTransitionRule[0];
   private static final LocalDateTime[] EMPTY_LDT_ARRAY = new LocalDateTime[0];

   public static ZoneRules of(ZoneOffset var0, ZoneOffset var1, List<ZoneOffsetTransition> var2, List<ZoneOffsetTransition> var3, List<ZoneOffsetTransitionRule> var4) {
      Objects.requireNonNull(var0, (String)"baseStandardOffset");
      Objects.requireNonNull(var1, (String)"baseWallOffset");
      Objects.requireNonNull(var2, (String)"standardOffsetTransitionList");
      Objects.requireNonNull(var3, (String)"transitionList");
      Objects.requireNonNull(var4, (String)"lastRules");
      return new ZoneRules(var0, var1, var2, var3, var4);
   }

   public static ZoneRules of(ZoneOffset var0) {
      Objects.requireNonNull(var0, (String)"offset");
      return new ZoneRules(var0);
   }

   ZoneRules(ZoneOffset var1, ZoneOffset var2, List<ZoneOffsetTransition> var3, List<ZoneOffsetTransition> var4, List<ZoneOffsetTransitionRule> var5) {
      this.standardTransitions = new long[var3.size()];
      this.standardOffsets = new ZoneOffset[var3.size() + 1];
      this.standardOffsets[0] = var1;

      for(int var6 = 0; var6 < var3.size(); ++var6) {
         this.standardTransitions[var6] = ((ZoneOffsetTransition)var3.get(var6)).toEpochSecond();
         this.standardOffsets[var6 + 1] = ((ZoneOffsetTransition)var3.get(var6)).getOffsetAfter();
      }

      ArrayList var10 = new ArrayList();
      ArrayList var7 = new ArrayList();
      var7.add(var2);

      ZoneOffsetTransition var9;
      for(Iterator var8 = var4.iterator(); var8.hasNext(); var7.add(var9.getOffsetAfter())) {
         var9 = (ZoneOffsetTransition)var8.next();
         if (var9.isGap()) {
            var10.add(var9.getDateTimeBefore());
            var10.add(var9.getDateTimeAfter());
         } else {
            var10.add(var9.getDateTimeAfter());
            var10.add(var9.getDateTimeBefore());
         }
      }

      this.savingsLocalTransitions = (LocalDateTime[])var10.toArray(new LocalDateTime[var10.size()]);
      this.wallOffsets = (ZoneOffset[])var7.toArray(new ZoneOffset[var7.size()]);
      this.savingsInstantTransitions = new long[var4.size()];

      for(int var11 = 0; var11 < var4.size(); ++var11) {
         this.savingsInstantTransitions[var11] = ((ZoneOffsetTransition)var4.get(var11)).toEpochSecond();
      }

      if (var5.size() > 16) {
         throw new IllegalArgumentException("Too many transition rules");
      } else {
         this.lastRules = (ZoneOffsetTransitionRule[])var5.toArray(new ZoneOffsetTransitionRule[var5.size()]);
      }
   }

   private ZoneRules(long[] var1, ZoneOffset[] var2, long[] var3, ZoneOffset[] var4, ZoneOffsetTransitionRule[] var5) {
      this.standardTransitions = var1;
      this.standardOffsets = var2;
      this.savingsInstantTransitions = var3;
      this.wallOffsets = var4;
      this.lastRules = var5;
      if (var3.length == 0) {
         this.savingsLocalTransitions = EMPTY_LDT_ARRAY;
      } else {
         ArrayList var6 = new ArrayList();

         for(int var7 = 0; var7 < var3.length; ++var7) {
            ZoneOffset var8 = var4[var7];
            ZoneOffset var9 = var4[var7 + 1];
            ZoneOffsetTransition var10 = new ZoneOffsetTransition(var3[var7], var8, var9);
            if (var10.isGap()) {
               var6.add(var10.getDateTimeBefore());
               var6.add(var10.getDateTimeAfter());
            } else {
               var6.add(var10.getDateTimeAfter());
               var6.add(var10.getDateTimeBefore());
            }
         }

         this.savingsLocalTransitions = (LocalDateTime[])var6.toArray(new LocalDateTime[var6.size()]);
      }

   }

   private ZoneRules(ZoneOffset var1) {
      this.standardOffsets = new ZoneOffset[1];
      this.standardOffsets[0] = var1;
      this.standardTransitions = EMPTY_LONG_ARRAY;
      this.savingsInstantTransitions = EMPTY_LONG_ARRAY;
      this.savingsLocalTransitions = EMPTY_LDT_ARRAY;
      this.wallOffsets = this.standardOffsets;
      this.lastRules = EMPTY_LASTRULES;
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   private Object writeReplace() {
      return new Ser((byte)1, this);
   }

   void writeExternal(DataOutput var1) throws IOException {
      var1.writeInt(this.standardTransitions.length);
      long[] var2 = this.standardTransitions;
      int var3 = var2.length;

      int var4;
      long var5;
      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         Ser.writeEpochSec(var5, var1);
      }

      ZoneOffset[] var7 = this.standardOffsets;
      var3 = var7.length;

      ZoneOffset var9;
      for(var4 = 0; var4 < var3; ++var4) {
         var9 = var7[var4];
         Ser.writeOffset(var9, var1);
      }

      var1.writeInt(this.savingsInstantTransitions.length);
      var2 = this.savingsInstantTransitions;
      var3 = var2.length;

      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         Ser.writeEpochSec(var5, var1);
      }

      var7 = this.wallOffsets;
      var3 = var7.length;

      for(var4 = 0; var4 < var3; ++var4) {
         var9 = var7[var4];
         Ser.writeOffset(var9, var1);
      }

      var1.writeByte(this.lastRules.length);
      ZoneOffsetTransitionRule[] var8 = this.lastRules;
      var3 = var8.length;

      for(var4 = 0; var4 < var3; ++var4) {
         ZoneOffsetTransitionRule var10 = var8[var4];
         var10.writeExternal(var1);
      }

   }

   static ZoneRules readExternal(DataInput var0) throws IOException, ClassNotFoundException {
      int var1 = var0.readInt();
      long[] var2 = var1 == 0 ? EMPTY_LONG_ARRAY : new long[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3] = Ser.readEpochSec(var0);
      }

      ZoneOffset[] var10 = new ZoneOffset[var1 + 1];

      int var4;
      for(var4 = 0; var4 < var10.length; ++var4) {
         var10[var4] = Ser.readOffset(var0);
      }

      var4 = var0.readInt();
      long[] var5 = var4 == 0 ? EMPTY_LONG_ARRAY : new long[var4];

      for(int var6 = 0; var6 < var4; ++var6) {
         var5[var6] = Ser.readEpochSec(var0);
      }

      ZoneOffset[] var11 = new ZoneOffset[var4 + 1];

      for(int var7 = 0; var7 < var11.length; ++var7) {
         var11[var7] = Ser.readOffset(var0);
      }

      byte var12 = var0.readByte();
      ZoneOffsetTransitionRule[] var8 = var12 == 0 ? EMPTY_LASTRULES : new ZoneOffsetTransitionRule[var12];

      for(int var9 = 0; var9 < var12; ++var9) {
         var8[var9] = ZoneOffsetTransitionRule.readExternal(var0);
      }

      return new ZoneRules(var2, var10, var5, var11, var8);
   }

   public boolean isFixedOffset() {
      return this.savingsInstantTransitions.length == 0;
   }

   public ZoneOffset getOffset(Instant var1) {
      if (this.savingsInstantTransitions.length == 0) {
         return this.standardOffsets[0];
      } else {
         long var2 = var1.getEpochSecond();
         int var4;
         if (this.lastRules.length > 0 && var2 > this.savingsInstantTransitions[this.savingsInstantTransitions.length - 1]) {
            var4 = this.findYear(var2, this.wallOffsets[this.wallOffsets.length - 1]);
            ZoneOffsetTransition[] var5 = this.findTransitionArray(var4);
            ZoneOffsetTransition var6 = null;

            for(int var7 = 0; var7 < var5.length; ++var7) {
               var6 = var5[var7];
               if (var2 < var6.toEpochSecond()) {
                  return var6.getOffsetBefore();
               }
            }

            return var6.getOffsetAfter();
         } else {
            var4 = Arrays.binarySearch(this.savingsInstantTransitions, var2);
            if (var4 < 0) {
               var4 = -var4 - 2;
            }

            return this.wallOffsets[var4 + 1];
         }
      }
   }

   public ZoneOffset getOffset(LocalDateTime var1) {
      Object var2 = this.getOffsetInfo(var1);
      return var2 instanceof ZoneOffsetTransition ? ((ZoneOffsetTransition)var2).getOffsetBefore() : (ZoneOffset)var2;
   }

   public List<ZoneOffset> getValidOffsets(LocalDateTime var1) {
      Object var2 = this.getOffsetInfo(var1);
      return var2 instanceof ZoneOffsetTransition ? ((ZoneOffsetTransition)var2).getValidOffsets() : Collections.singletonList((ZoneOffset)var2);
   }

   public ZoneOffsetTransition getTransition(LocalDateTime var1) {
      Object var2 = this.getOffsetInfo(var1);
      return var2 instanceof ZoneOffsetTransition ? (ZoneOffsetTransition)var2 : null;
   }

   private Object getOffsetInfo(LocalDateTime var1) {
      if (this.savingsInstantTransitions.length == 0) {
         return this.standardOffsets[0];
      } else if (this.lastRules.length > 0 && var1.isAfter(this.savingsLocalTransitions[this.savingsLocalTransitions.length - 1])) {
         ZoneOffsetTransition[] var8 = this.findTransitionArray(var1.getYear());
         Object var9 = null;
         ZoneOffsetTransition[] var10 = var8;
         int var11 = var8.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            ZoneOffsetTransition var7 = var10[var12];
            var9 = this.findOffsetInfo(var1, var7);
            if (var9 instanceof ZoneOffsetTransition || var9.equals(var7.getOffsetBefore())) {
               return var9;
            }
         }

         return var9;
      } else {
         int var2 = Arrays.binarySearch(this.savingsLocalTransitions, var1);
         if (var2 == -1) {
            return this.wallOffsets[0];
         } else {
            if (var2 < 0) {
               var2 = -var2 - 2;
            } else if (var2 < this.savingsLocalTransitions.length - 1 && this.savingsLocalTransitions[var2].equals(this.savingsLocalTransitions[var2 + 1])) {
               ++var2;
            }

            if ((var2 & 1) == 0) {
               LocalDateTime var3 = this.savingsLocalTransitions[var2];
               LocalDateTime var4 = this.savingsLocalTransitions[var2 + 1];
               ZoneOffset var5 = this.wallOffsets[var2 / 2];
               ZoneOffset var6 = this.wallOffsets[var2 / 2 + 1];
               return var6.getTotalSeconds() > var5.getTotalSeconds() ? new ZoneOffsetTransition(var3, var5, var6) : new ZoneOffsetTransition(var4, var5, var6);
            } else {
               return this.wallOffsets[var2 / 2 + 1];
            }
         }
      }
   }

   private Object findOffsetInfo(LocalDateTime var1, ZoneOffsetTransition var2) {
      LocalDateTime var3 = var2.getDateTimeBefore();
      if (var2.isGap()) {
         if (var1.isBefore(var3)) {
            return var2.getOffsetBefore();
         } else {
            return var1.isBefore(var2.getDateTimeAfter()) ? var2 : var2.getOffsetAfter();
         }
      } else if (!var1.isBefore(var3)) {
         return var2.getOffsetAfter();
      } else {
         return var1.isBefore(var2.getDateTimeAfter()) ? var2.getOffsetBefore() : var2;
      }
   }

   private ZoneOffsetTransition[] findTransitionArray(int var1) {
      Integer var2 = var1;
      ZoneOffsetTransition[] var3 = (ZoneOffsetTransition[])this.lastRulesCache.get(var2);
      if (var3 != null) {
         return var3;
      } else {
         ZoneOffsetTransitionRule[] var4 = this.lastRules;
         var3 = new ZoneOffsetTransition[var4.length];

         for(int var5 = 0; var5 < var4.length; ++var5) {
            var3[var5] = var4[var5].createTransition(var1);
         }

         if (var1 < 2100) {
            this.lastRulesCache.putIfAbsent(var2, var3);
         }

         return var3;
      }
   }

   public ZoneOffset getStandardOffset(Instant var1) {
      if (this.savingsInstantTransitions.length == 0) {
         return this.standardOffsets[0];
      } else {
         long var2 = var1.getEpochSecond();
         int var4 = Arrays.binarySearch(this.standardTransitions, var2);
         if (var4 < 0) {
            var4 = -var4 - 2;
         }

         return this.standardOffsets[var4 + 1];
      }
   }

   public Duration getDaylightSavings(Instant var1) {
      if (this.savingsInstantTransitions.length == 0) {
         return Duration.ZERO;
      } else {
         ZoneOffset var2 = this.getStandardOffset(var1);
         ZoneOffset var3 = this.getOffset(var1);
         return Duration.ofSeconds((long)(var3.getTotalSeconds() - var2.getTotalSeconds()));
      }
   }

   public boolean isDaylightSavings(Instant var1) {
      return !this.getStandardOffset(var1).equals(this.getOffset(var1));
   }

   public boolean isValidOffset(LocalDateTime var1, ZoneOffset var2) {
      return this.getValidOffsets(var1).contains(var2);
   }

   public ZoneOffsetTransition nextTransition(Instant var1) {
      if (this.savingsInstantTransitions.length == 0) {
         return null;
      } else {
         long var2 = var1.getEpochSecond();
         int var4;
         if (var2 >= this.savingsInstantTransitions[this.savingsInstantTransitions.length - 1]) {
            if (this.lastRules.length == 0) {
               return null;
            } else {
               var4 = this.findYear(var2, this.wallOffsets[this.wallOffsets.length - 1]);
               ZoneOffsetTransition[] var5 = this.findTransitionArray(var4);
               ZoneOffsetTransition[] var6 = var5;
               int var7 = var5.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  ZoneOffsetTransition var9 = var6[var8];
                  if (var2 < var9.toEpochSecond()) {
                     return var9;
                  }
               }

               if (var4 < 999999999) {
                  var5 = this.findTransitionArray(var4 + 1);
                  return var5[0];
               } else {
                  return null;
               }
            }
         } else {
            var4 = Arrays.binarySearch(this.savingsInstantTransitions, var2);
            if (var4 < 0) {
               var4 = -var4 - 1;
            } else {
               ++var4;
            }

            return new ZoneOffsetTransition(this.savingsInstantTransitions[var4], this.wallOffsets[var4], this.wallOffsets[var4 + 1]);
         }
      }
   }

   public ZoneOffsetTransition previousTransition(Instant var1) {
      if (this.savingsInstantTransitions.length == 0) {
         return null;
      } else {
         long var2 = var1.getEpochSecond();
         if (var1.getNano() > 0 && var2 < Long.MAX_VALUE) {
            ++var2;
         }

         long var4 = this.savingsInstantTransitions[this.savingsInstantTransitions.length - 1];
         if (this.lastRules.length > 0 && var2 > var4) {
            ZoneOffset var6 = this.wallOffsets[this.wallOffsets.length - 1];
            int var7 = this.findYear(var2, var6);
            ZoneOffsetTransition[] var8 = this.findTransitionArray(var7);

            int var9;
            for(var9 = var8.length - 1; var9 >= 0; --var9) {
               if (var2 > var8[var9].toEpochSecond()) {
                  return var8[var9];
               }
            }

            var9 = this.findYear(var4, var6);
            --var7;
            if (var7 > var9) {
               var8 = this.findTransitionArray(var7);
               return var8[var8.length - 1];
            }
         }

         int var10 = Arrays.binarySearch(this.savingsInstantTransitions, var2);
         if (var10 < 0) {
            var10 = -var10 - 1;
         }

         return var10 <= 0 ? null : new ZoneOffsetTransition(this.savingsInstantTransitions[var10 - 1], this.wallOffsets[var10 - 1], this.wallOffsets[var10]);
      }
   }

   private int findYear(long var1, ZoneOffset var3) {
      long var4 = var1 + (long)var3.getTotalSeconds();
      long var6 = Math.floorDiv(var4, 86400L);
      return LocalDate.ofEpochDay(var6).getYear();
   }

   public List<ZoneOffsetTransition> getTransitions() {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < this.savingsInstantTransitions.length; ++var2) {
         var1.add(new ZoneOffsetTransition(this.savingsInstantTransitions[var2], this.wallOffsets[var2], this.wallOffsets[var2 + 1]));
      }

      return Collections.unmodifiableList(var1);
   }

   public List<ZoneOffsetTransitionRule> getTransitionRules() {
      return Collections.unmodifiableList(Arrays.asList(this.lastRules));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ZoneRules)) {
         return false;
      } else {
         ZoneRules var2 = (ZoneRules)var1;
         return Arrays.equals(this.standardTransitions, var2.standardTransitions) && Arrays.equals((Object[])this.standardOffsets, (Object[])var2.standardOffsets) && Arrays.equals(this.savingsInstantTransitions, var2.savingsInstantTransitions) && Arrays.equals((Object[])this.wallOffsets, (Object[])var2.wallOffsets) && Arrays.equals((Object[])this.lastRules, (Object[])var2.lastRules);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.standardTransitions) ^ Arrays.hashCode((Object[])this.standardOffsets) ^ Arrays.hashCode(this.savingsInstantTransitions) ^ Arrays.hashCode((Object[])this.wallOffsets) ^ Arrays.hashCode((Object[])this.lastRules);
   }

   public String toString() {
      return "ZoneRules[currentStandardOffset=" + this.standardOffsets[this.standardOffsets.length - 1] + "]";
   }
}
