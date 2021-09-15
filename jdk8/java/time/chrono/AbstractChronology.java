package java.time.chrono;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import sun.util.logging.PlatformLogger;

public abstract class AbstractChronology implements Chronology {
   static final Comparator<ChronoLocalDate> DATE_ORDER = (Comparator)((Serializable)((var0x, var1x) -> {
      return Long.compare(var0x.toEpochDay(), var1x.toEpochDay());
   }));
   static final Comparator<ChronoLocalDateTime<? extends ChronoLocalDate>> DATE_TIME_ORDER = (Comparator)((Serializable)((var0x, var1x) -> {
      int var2 = Long.compare(var0x.toLocalDate().toEpochDay(), var1x.toLocalDate().toEpochDay());
      if (var2 == 0) {
         var2 = Long.compare(var0x.toLocalTime().toNanoOfDay(), var1x.toLocalTime().toNanoOfDay());
      }

      return var2;
   }));
   static final Comparator<ChronoZonedDateTime<?>> INSTANT_ORDER = (Comparator)((Serializable)((var0x, var1x) -> {
      int var2 = Long.compare(var0x.toEpochSecond(), var1x.toEpochSecond());
      if (var2 == 0) {
         var2 = Long.compare((long)var0x.toLocalTime().getNano(), (long)var1x.toLocalTime().getNano());
      }

      return var2;
   }));
   private static final ConcurrentHashMap<String, Chronology> CHRONOS_BY_ID = new ConcurrentHashMap();
   private static final ConcurrentHashMap<String, Chronology> CHRONOS_BY_TYPE = new ConcurrentHashMap();

   static Chronology registerChrono(Chronology var0) {
      return registerChrono(var0, var0.getId());
   }

   static Chronology registerChrono(Chronology var0, String var1) {
      Chronology var2 = (Chronology)CHRONOS_BY_ID.putIfAbsent(var1, var0);
      if (var2 == null) {
         String var3 = var0.getCalendarType();
         if (var3 != null) {
            CHRONOS_BY_TYPE.putIfAbsent(var3, var0);
         }
      }

      return var2;
   }

   private static boolean initCache() {
      if (CHRONOS_BY_ID.get("ISO") != null) {
         return false;
      } else {
         registerChrono(HijrahChronology.INSTANCE);
         registerChrono(JapaneseChronology.INSTANCE);
         registerChrono(MinguoChronology.INSTANCE);
         registerChrono(ThaiBuddhistChronology.INSTANCE);
         ServiceLoader var0 = ServiceLoader.load(AbstractChronology.class, (ClassLoader)null);
         Iterator var1 = var0.iterator();

         while(true) {
            AbstractChronology var2;
            String var3;
            do {
               if (!var1.hasNext()) {
                  registerChrono(IsoChronology.INSTANCE);
                  return true;
               }

               var2 = (AbstractChronology)var1.next();
               var3 = var2.getId();
            } while(!var3.equals("ISO") && registerChrono(var2) == null);

            PlatformLogger var4 = PlatformLogger.getLogger("java.time.chrono");
            var4.warning("Ignoring duplicate Chronology, from ServiceLoader configuration " + var3);
         }
      }
   }

   static Chronology ofLocale(Locale var0) {
      Objects.requireNonNull(var0, (String)"locale");
      String var1 = var0.getUnicodeLocaleType("ca");
      if (var1 != null && !"iso".equals(var1) && !"iso8601".equals(var1)) {
         do {
            Chronology var2 = (Chronology)CHRONOS_BY_TYPE.get(var1);
            if (var2 != null) {
               return var2;
            }
         } while(initCache());

         ServiceLoader var5 = ServiceLoader.load(Chronology.class);
         Iterator var3 = var5.iterator();

         Chronology var4;
         do {
            if (!var3.hasNext()) {
               throw new DateTimeException("Unknown calendar system: " + var1);
            }

            var4 = (Chronology)var3.next();
         } while(!var1.equals(var4.getCalendarType()));

         return var4;
      } else {
         return IsoChronology.INSTANCE;
      }
   }

   static Chronology of(String var0) {
      Objects.requireNonNull(var0, (String)"id");

      do {
         Chronology var1 = of0(var0);
         if (var1 != null) {
            return var1;
         }
      } while(initCache());

      ServiceLoader var4 = ServiceLoader.load(Chronology.class);
      Iterator var2 = var4.iterator();

      Chronology var3;
      do {
         if (!var2.hasNext()) {
            throw new DateTimeException("Unknown chronology: " + var0);
         }

         var3 = (Chronology)var2.next();
      } while(!var0.equals(var3.getId()) && !var0.equals(var3.getCalendarType()));

      return var3;
   }

   private static Chronology of0(String var0) {
      Chronology var1 = (Chronology)CHRONOS_BY_ID.get(var0);
      if (var1 == null) {
         var1 = (Chronology)CHRONOS_BY_TYPE.get(var0);
      }

      return var1;
   }

   static Set<Chronology> getAvailableChronologies() {
      initCache();
      HashSet var0 = new HashSet(CHRONOS_BY_ID.values());
      ServiceLoader var1 = ServiceLoader.load(Chronology.class);
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Chronology var3 = (Chronology)var2.next();
         var0.add(var3);
      }

      return var0;
   }

   protected AbstractChronology() {
   }

   public ChronoLocalDate resolveDate(Map<TemporalField, Long> var1, ResolverStyle var2) {
      if (var1.containsKey(ChronoField.EPOCH_DAY)) {
         return this.dateEpochDay((Long)var1.remove(ChronoField.EPOCH_DAY));
      } else {
         this.resolveProlepticMonth(var1, var2);
         ChronoLocalDate var3 = this.resolveYearOfEra(var1, var2);
         if (var3 != null) {
            return var3;
         } else {
            if (var1.containsKey(ChronoField.YEAR)) {
               if (var1.containsKey(ChronoField.MONTH_OF_YEAR)) {
                  if (var1.containsKey(ChronoField.DAY_OF_MONTH)) {
                     return this.resolveYMD(var1, var2);
                  }

                  if (var1.containsKey(ChronoField.ALIGNED_WEEK_OF_MONTH)) {
                     if (var1.containsKey(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH)) {
                        return this.resolveYMAA(var1, var2);
                     }

                     if (var1.containsKey(ChronoField.DAY_OF_WEEK)) {
                        return this.resolveYMAD(var1, var2);
                     }
                  }
               }

               if (var1.containsKey(ChronoField.DAY_OF_YEAR)) {
                  return this.resolveYD(var1, var2);
               }

               if (var1.containsKey(ChronoField.ALIGNED_WEEK_OF_YEAR)) {
                  if (var1.containsKey(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR)) {
                     return this.resolveYAA(var1, var2);
                  }

                  if (var1.containsKey(ChronoField.DAY_OF_WEEK)) {
                     return this.resolveYAD(var1, var2);
                  }
               }
            }

            return null;
         }
      }
   }

   void resolveProlepticMonth(Map<TemporalField, Long> var1, ResolverStyle var2) {
      Long var3 = (Long)var1.remove(ChronoField.PROLEPTIC_MONTH);
      if (var3 != null) {
         if (var2 != ResolverStyle.LENIENT) {
            ChronoField.PROLEPTIC_MONTH.checkValidValue(var3);
         }

         ChronoLocalDate var4 = this.dateNow().with(ChronoField.DAY_OF_MONTH, 1L).with(ChronoField.PROLEPTIC_MONTH, var3);
         this.addFieldValue(var1, ChronoField.MONTH_OF_YEAR, (long)var4.get(ChronoField.MONTH_OF_YEAR));
         this.addFieldValue(var1, ChronoField.YEAR, (long)var4.get(ChronoField.YEAR));
      }

   }

   ChronoLocalDate resolveYearOfEra(Map<TemporalField, Long> var1, ResolverStyle var2) {
      Long var3 = (Long)var1.remove(ChronoField.YEAR_OF_ERA);
      if (var3 != null) {
         Long var4 = (Long)var1.remove(ChronoField.ERA);
         int var5;
         if (var2 != ResolverStyle.LENIENT) {
            var5 = this.range(ChronoField.YEAR_OF_ERA).checkValidIntValue(var3, ChronoField.YEAR_OF_ERA);
         } else {
            var5 = Math.toIntExact(var3);
         }

         if (var4 != null) {
            Era var6 = this.eraOf(this.range(ChronoField.ERA).checkValidIntValue(var4, ChronoField.ERA));
            this.addFieldValue(var1, ChronoField.YEAR, (long)this.prolepticYear(var6, var5));
         } else if (var1.containsKey(ChronoField.YEAR)) {
            int var8 = this.range(ChronoField.YEAR).checkValidIntValue((Long)var1.get(ChronoField.YEAR), ChronoField.YEAR);
            ChronoLocalDate var7 = this.dateYearDay(var8, 1);
            this.addFieldValue(var1, ChronoField.YEAR, (long)this.prolepticYear(var7.getEra(), var5));
         } else if (var2 == ResolverStyle.STRICT) {
            var1.put(ChronoField.YEAR_OF_ERA, var3);
         } else {
            List var9 = this.eras();
            if (var9.isEmpty()) {
               this.addFieldValue(var1, ChronoField.YEAR, (long)var5);
            } else {
               Era var10 = (Era)var9.get(var9.size() - 1);
               this.addFieldValue(var1, ChronoField.YEAR, (long)this.prolepticYear(var10, var5));
            }
         }
      } else if (var1.containsKey(ChronoField.ERA)) {
         this.range(ChronoField.ERA).checkValidValue((Long)var1.get(ChronoField.ERA), ChronoField.ERA);
      }

      return null;
   }

   ChronoLocalDate resolveYMD(Map<TemporalField, Long> var1, ResolverStyle var2) {
      int var3 = this.range(ChronoField.YEAR).checkValidIntValue((Long)var1.remove(ChronoField.YEAR), ChronoField.YEAR);
      if (var2 == ResolverStyle.LENIENT) {
         long var9 = Math.subtractExact((Long)var1.remove(ChronoField.MONTH_OF_YEAR), 1L);
         long var10 = Math.subtractExact((Long)var1.remove(ChronoField.DAY_OF_MONTH), 1L);
         return this.date(var3, 1, 1).plus(var9, ChronoUnit.MONTHS).plus(var10, ChronoUnit.DAYS);
      } else {
         int var4 = this.range(ChronoField.MONTH_OF_YEAR).checkValidIntValue((Long)var1.remove(ChronoField.MONTH_OF_YEAR), ChronoField.MONTH_OF_YEAR);
         ValueRange var5 = this.range(ChronoField.DAY_OF_MONTH);
         int var6 = var5.checkValidIntValue((Long)var1.remove(ChronoField.DAY_OF_MONTH), ChronoField.DAY_OF_MONTH);
         if (var2 == ResolverStyle.SMART) {
            try {
               return this.date(var3, var4, var6);
            } catch (DateTimeException var8) {
               return this.date(var3, var4, 1).with(TemporalAdjusters.lastDayOfMonth());
            }
         } else {
            return this.date(var3, var4, var6);
         }
      }
   }

   ChronoLocalDate resolveYD(Map<TemporalField, Long> var1, ResolverStyle var2) {
      int var3 = this.range(ChronoField.YEAR).checkValidIntValue((Long)var1.remove(ChronoField.YEAR), ChronoField.YEAR);
      if (var2 == ResolverStyle.LENIENT) {
         long var6 = Math.subtractExact((Long)var1.remove(ChronoField.DAY_OF_YEAR), 1L);
         return this.dateYearDay(var3, 1).plus(var6, ChronoUnit.DAYS);
      } else {
         int var4 = this.range(ChronoField.DAY_OF_YEAR).checkValidIntValue((Long)var1.remove(ChronoField.DAY_OF_YEAR), ChronoField.DAY_OF_YEAR);
         return this.dateYearDay(var3, var4);
      }
   }

   ChronoLocalDate resolveYMAA(Map<TemporalField, Long> var1, ResolverStyle var2) {
      int var3 = this.range(ChronoField.YEAR).checkValidIntValue((Long)var1.remove(ChronoField.YEAR), ChronoField.YEAR);
      if (var2 == ResolverStyle.LENIENT) {
         long var10 = Math.subtractExact((Long)var1.remove(ChronoField.MONTH_OF_YEAR), 1L);
         long var11 = Math.subtractExact((Long)var1.remove(ChronoField.ALIGNED_WEEK_OF_MONTH), 1L);
         long var8 = Math.subtractExact((Long)var1.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH), 1L);
         return this.date(var3, 1, 1).plus(var10, ChronoUnit.MONTHS).plus(var11, ChronoUnit.WEEKS).plus(var8, ChronoUnit.DAYS);
      } else {
         int var4 = this.range(ChronoField.MONTH_OF_YEAR).checkValidIntValue((Long)var1.remove(ChronoField.MONTH_OF_YEAR), ChronoField.MONTH_OF_YEAR);
         int var5 = this.range(ChronoField.ALIGNED_WEEK_OF_MONTH).checkValidIntValue((Long)var1.remove(ChronoField.ALIGNED_WEEK_OF_MONTH), ChronoField.ALIGNED_WEEK_OF_MONTH);
         int var6 = this.range(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH).checkValidIntValue((Long)var1.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH), ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);
         ChronoLocalDate var7 = this.date(var3, var4, 1).plus((long)((var5 - 1) * 7 + (var6 - 1)), ChronoUnit.DAYS);
         if (var2 == ResolverStyle.STRICT && var7.get(ChronoField.MONTH_OF_YEAR) != var4) {
            throw new DateTimeException("Strict mode rejected resolved date as it is in a different month");
         } else {
            return var7;
         }
      }
   }

   ChronoLocalDate resolveYMAD(Map<TemporalField, Long> var1, ResolverStyle var2) {
      int var3 = this.range(ChronoField.YEAR).checkValidIntValue((Long)var1.remove(ChronoField.YEAR), ChronoField.YEAR);
      if (var2 == ResolverStyle.LENIENT) {
         long var10 = Math.subtractExact((Long)var1.remove(ChronoField.MONTH_OF_YEAR), 1L);
         long var11 = Math.subtractExact((Long)var1.remove(ChronoField.ALIGNED_WEEK_OF_MONTH), 1L);
         long var8 = Math.subtractExact((Long)var1.remove(ChronoField.DAY_OF_WEEK), 1L);
         return this.resolveAligned(this.date(var3, 1, 1), var10, var11, var8);
      } else {
         int var4 = this.range(ChronoField.MONTH_OF_YEAR).checkValidIntValue((Long)var1.remove(ChronoField.MONTH_OF_YEAR), ChronoField.MONTH_OF_YEAR);
         int var5 = this.range(ChronoField.ALIGNED_WEEK_OF_MONTH).checkValidIntValue((Long)var1.remove(ChronoField.ALIGNED_WEEK_OF_MONTH), ChronoField.ALIGNED_WEEK_OF_MONTH);
         int var6 = this.range(ChronoField.DAY_OF_WEEK).checkValidIntValue((Long)var1.remove(ChronoField.DAY_OF_WEEK), ChronoField.DAY_OF_WEEK);
         ChronoLocalDate var7 = this.date(var3, var4, 1).plus((long)((var5 - 1) * 7), ChronoUnit.DAYS).with(TemporalAdjusters.nextOrSame(DayOfWeek.of(var6)));
         if (var2 == ResolverStyle.STRICT && var7.get(ChronoField.MONTH_OF_YEAR) != var4) {
            throw new DateTimeException("Strict mode rejected resolved date as it is in a different month");
         } else {
            return var7;
         }
      }
   }

   ChronoLocalDate resolveYAA(Map<TemporalField, Long> var1, ResolverStyle var2) {
      int var3 = this.range(ChronoField.YEAR).checkValidIntValue((Long)var1.remove(ChronoField.YEAR), ChronoField.YEAR);
      if (var2 == ResolverStyle.LENIENT) {
         long var8 = Math.subtractExact((Long)var1.remove(ChronoField.ALIGNED_WEEK_OF_YEAR), 1L);
         long var9 = Math.subtractExact((Long)var1.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR), 1L);
         return this.dateYearDay(var3, 1).plus(var8, ChronoUnit.WEEKS).plus(var9, ChronoUnit.DAYS);
      } else {
         int var4 = this.range(ChronoField.ALIGNED_WEEK_OF_YEAR).checkValidIntValue((Long)var1.remove(ChronoField.ALIGNED_WEEK_OF_YEAR), ChronoField.ALIGNED_WEEK_OF_YEAR);
         int var5 = this.range(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR).checkValidIntValue((Long)var1.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR), ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR);
         ChronoLocalDate var6 = this.dateYearDay(var3, 1).plus((long)((var4 - 1) * 7 + (var5 - 1)), ChronoUnit.DAYS);
         if (var2 == ResolverStyle.STRICT && var6.get(ChronoField.YEAR) != var3) {
            throw new DateTimeException("Strict mode rejected resolved date as it is in a different year");
         } else {
            return var6;
         }
      }
   }

   ChronoLocalDate resolveYAD(Map<TemporalField, Long> var1, ResolverStyle var2) {
      int var3 = this.range(ChronoField.YEAR).checkValidIntValue((Long)var1.remove(ChronoField.YEAR), ChronoField.YEAR);
      if (var2 == ResolverStyle.LENIENT) {
         long var8 = Math.subtractExact((Long)var1.remove(ChronoField.ALIGNED_WEEK_OF_YEAR), 1L);
         long var9 = Math.subtractExact((Long)var1.remove(ChronoField.DAY_OF_WEEK), 1L);
         return this.resolveAligned(this.dateYearDay(var3, 1), 0L, var8, var9);
      } else {
         int var4 = this.range(ChronoField.ALIGNED_WEEK_OF_YEAR).checkValidIntValue((Long)var1.remove(ChronoField.ALIGNED_WEEK_OF_YEAR), ChronoField.ALIGNED_WEEK_OF_YEAR);
         int var5 = this.range(ChronoField.DAY_OF_WEEK).checkValidIntValue((Long)var1.remove(ChronoField.DAY_OF_WEEK), ChronoField.DAY_OF_WEEK);
         ChronoLocalDate var6 = this.dateYearDay(var3, 1).plus((long)((var4 - 1) * 7), ChronoUnit.DAYS).with(TemporalAdjusters.nextOrSame(DayOfWeek.of(var5)));
         if (var2 == ResolverStyle.STRICT && var6.get(ChronoField.YEAR) != var3) {
            throw new DateTimeException("Strict mode rejected resolved date as it is in a different year");
         } else {
            return var6;
         }
      }
   }

   ChronoLocalDate resolveAligned(ChronoLocalDate var1, long var2, long var4, long var6) {
      ChronoLocalDate var8 = var1.plus(var2, ChronoUnit.MONTHS).plus(var4, ChronoUnit.WEEKS);
      if (var6 > 7L) {
         var8 = var8.plus((var6 - 1L) / 7L, ChronoUnit.WEEKS);
         var6 = (var6 - 1L) % 7L + 1L;
      } else if (var6 < 1L) {
         var8 = var8.plus(Math.subtractExact(var6, 7L) / 7L, ChronoUnit.WEEKS);
         var6 = (var6 + 6L) % 7L + 1L;
      }

      return var8.with(TemporalAdjusters.nextOrSame(DayOfWeek.of((int)var6)));
   }

   void addFieldValue(Map<TemporalField, Long> var1, ChronoField var2, long var3) {
      Long var5 = (Long)var1.get(var2);
      if (var5 != null && var5 != var3) {
         throw new DateTimeException("Conflict found: " + var2 + " " + var5 + " differs from " + var2 + " " + var3);
      } else {
         var1.put(var2, var3);
      }
   }

   public int compareTo(Chronology var1) {
      return this.getId().compareTo(var1.getId());
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof AbstractChronology) {
         return this.compareTo((Chronology)((AbstractChronology)var1)) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.getClass().hashCode() ^ this.getId().hashCode();
   }

   public String toString() {
      return this.getId();
   }

   Object writeReplace() {
      return new Ser((byte)1, this);
   }

   private void readObject(ObjectInputStream var1) throws ObjectStreamException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void writeExternal(DataOutput var1) throws IOException {
      var1.writeUTF(this.getId());
   }

   static Chronology readExternal(DataInput var0) throws IOException {
      String var1 = var0.readUTF();
      return Chronology.of(var1);
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda var0) {
      String var1 = var0.getImplMethodName();
      byte var2 = -1;
      switch(var1.hashCode()) {
      case -1169580303:
         if (var1.equals("lambda$static$2241c452$1")) {
            var2 = 0;
         }
         break;
      case 113408742:
         if (var1.equals("lambda$static$7f2d2d5b$1")) {
            var2 = 1;
         }
         break;
      case 988104628:
         if (var1.equals("lambda$static$b5a61975$1")) {
            var2 = 2;
         }
      }

      switch(var2) {
      case 0:
         if (var0.getImplMethodKind() == 6 && var0.getFunctionalInterfaceClass().equals("java/util/Comparator") && var0.getFunctionalInterfaceMethodName().equals("compare") && var0.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)I") && var0.getImplClass().equals("java/time/chrono/AbstractChronology") && var0.getImplMethodSignature().equals("(Ljava/time/chrono/ChronoZonedDateTime;Ljava/time/chrono/ChronoZonedDateTime;)I")) {
            return (var0x, var1x) -> {
               int var2 = Long.compare(var0x.toEpochSecond(), var1x.toEpochSecond());
               if (var2 == 0) {
                  var2 = Long.compare((long)var0x.toLocalTime().getNano(), (long)var1x.toLocalTime().getNano());
               }

               return var2;
            };
         }
         break;
      case 1:
         if (var0.getImplMethodKind() == 6 && var0.getFunctionalInterfaceClass().equals("java/util/Comparator") && var0.getFunctionalInterfaceMethodName().equals("compare") && var0.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)I") && var0.getImplClass().equals("java/time/chrono/AbstractChronology") && var0.getImplMethodSignature().equals("(Ljava/time/chrono/ChronoLocalDate;Ljava/time/chrono/ChronoLocalDate;)I")) {
            return (var0x, var1x) -> {
               return Long.compare(var0x.toEpochDay(), var1x.toEpochDay());
            };
         }
         break;
      case 2:
         if (var0.getImplMethodKind() == 6 && var0.getFunctionalInterfaceClass().equals("java/util/Comparator") && var0.getFunctionalInterfaceMethodName().equals("compare") && var0.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)I") && var0.getImplClass().equals("java/time/chrono/AbstractChronology") && var0.getImplMethodSignature().equals("(Ljava/time/chrono/ChronoLocalDateTime;Ljava/time/chrono/ChronoLocalDateTime;)I")) {
            return (var0x, var1x) -> {
               int var2 = Long.compare(var0x.toLocalDate().toEpochDay(), var1x.toLocalDate().toEpochDay());
               if (var2 == 0) {
                  var2 = Long.compare(var0x.toLocalTime().toNanoOfDay(), var1x.toLocalTime().toNanoOfDay());
               }

               return var2;
            };
         }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }
}
