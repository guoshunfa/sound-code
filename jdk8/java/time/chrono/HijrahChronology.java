package java.time.chrono;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import sun.util.calendar.BaseCalendar;
import sun.util.logging.PlatformLogger;

public final class HijrahChronology extends AbstractChronology implements Serializable {
   private final transient String typeId;
   private final transient String calendarType;
   private static final long serialVersionUID = 3127340209035924785L;
   public static final HijrahChronology INSTANCE;
   private transient volatile boolean initComplete;
   private transient int[] hijrahEpochMonthStartDays;
   private transient int minEpochDay;
   private transient int maxEpochDay;
   private transient int hijrahStartEpochMonth;
   private transient int minMonthLength;
   private transient int maxMonthLength;
   private transient int minYearLength;
   private transient int maxYearLength;
   private static final transient Properties calendarProperties;
   private static final String PROP_PREFIX = "calendar.hijrah.";
   private static final String PROP_TYPE_SUFFIX = ".type";
   private static final String KEY_ID = "id";
   private static final String KEY_TYPE = "type";
   private static final String KEY_VERSION = "version";
   private static final String KEY_ISO_START = "iso-start";

   private static void registerVariants() {
      Iterator var0 = calendarProperties.stringPropertyNames().iterator();

      while(var0.hasNext()) {
         String var1 = (String)var0.next();
         if (var1.startsWith("calendar.hijrah.")) {
            String var2 = var1.substring("calendar.hijrah.".length());
            if (var2.indexOf(46) < 0 && !var2.equals(INSTANCE.getId())) {
               try {
                  HijrahChronology var3 = new HijrahChronology(var2);
                  AbstractChronology.registerChrono(var3);
               } catch (DateTimeException var5) {
                  PlatformLogger var4 = PlatformLogger.getLogger("java.time.chrono");
                  var4.severe("Unable to initialize Hijrah calendar: " + var2, (Throwable)var5);
               }
            }
         }
      }

   }

   private HijrahChronology(String var1) throws DateTimeException {
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("calendar id is empty");
      } else {
         String var2 = "calendar.hijrah." + var1 + ".type";
         String var3 = calendarProperties.getProperty(var2);
         if (var3 != null && !var3.isEmpty()) {
            this.typeId = var1;
            this.calendarType = var3;
         } else {
            throw new DateTimeException("calendarType is missing or empty for: " + var2);
         }
      }
   }

   private void checkCalendarInit() {
      if (!this.initComplete) {
         this.loadCalendarData();
         this.initComplete = true;
      }

   }

   public String getId() {
      return this.typeId;
   }

   public String getCalendarType() {
      return this.calendarType;
   }

   public HijrahDate date(Era var1, int var2, int var3, int var4) {
      return this.date(this.prolepticYear(var1, var2), var3, var4);
   }

   public HijrahDate date(int var1, int var2, int var3) {
      return HijrahDate.of(this, var1, var2, var3);
   }

   public HijrahDate dateYearDay(Era var1, int var2, int var3) {
      return this.dateYearDay(this.prolepticYear(var1, var2), var3);
   }

   public HijrahDate dateYearDay(int var1, int var2) {
      HijrahDate var3 = HijrahDate.of(this, var1, 1, 1);
      if (var2 > var3.lengthOfYear()) {
         throw new DateTimeException("Invalid dayOfYear: " + var2);
      } else {
         return var3.plusDays((long)(var2 - 1));
      }
   }

   public HijrahDate dateEpochDay(long var1) {
      return HijrahDate.ofEpochDay(this, var1);
   }

   public HijrahDate dateNow() {
      return this.dateNow(Clock.systemDefaultZone());
   }

   public HijrahDate dateNow(ZoneId var1) {
      return this.dateNow(Clock.system(var1));
   }

   public HijrahDate dateNow(Clock var1) {
      return this.date(LocalDate.now(var1));
   }

   public HijrahDate date(TemporalAccessor var1) {
      return var1 instanceof HijrahDate ? (HijrahDate)var1 : HijrahDate.ofEpochDay(this, var1.getLong(ChronoField.EPOCH_DAY));
   }

   public ChronoLocalDateTime<HijrahDate> localDateTime(TemporalAccessor var1) {
      return super.localDateTime(var1);
   }

   public ChronoZonedDateTime<HijrahDate> zonedDateTime(TemporalAccessor var1) {
      return super.zonedDateTime(var1);
   }

   public ChronoZonedDateTime<HijrahDate> zonedDateTime(Instant var1, ZoneId var2) {
      return super.zonedDateTime(var1, var2);
   }

   public boolean isLeapYear(long var1) {
      this.checkCalendarInit();
      if (var1 >= (long)this.getMinimumYear() && var1 <= (long)this.getMaximumYear()) {
         int var3 = this.getYearLength((int)var1);
         return var3 > 354;
      } else {
         return false;
      }
   }

   public int prolepticYear(Era var1, int var2) {
      if (!(var1 instanceof HijrahEra)) {
         throw new ClassCastException("Era must be HijrahEra");
      } else {
         return var2;
      }
   }

   public HijrahEra eraOf(int var1) {
      switch(var1) {
      case 1:
         return HijrahEra.AH;
      default:
         throw new DateTimeException("invalid Hijrah era");
      }
   }

   public List<Era> eras() {
      return Arrays.asList(HijrahEra.values());
   }

   public ValueRange range(ChronoField var1) {
      this.checkCalendarInit();
      if (var1 instanceof ChronoField) {
         switch(var1) {
         case DAY_OF_MONTH:
            return ValueRange.of(1L, 1L, (long)this.getMinimumMonthLength(), (long)this.getMaximumMonthLength());
         case DAY_OF_YEAR:
            return ValueRange.of(1L, (long)this.getMaximumDayOfYear());
         case ALIGNED_WEEK_OF_MONTH:
            return ValueRange.of(1L, 5L);
         case YEAR:
         case YEAR_OF_ERA:
            return ValueRange.of((long)this.getMinimumYear(), (long)this.getMaximumYear());
         case ERA:
            return ValueRange.of(1L, 1L);
         default:
            return var1.range();
         }
      } else {
         return var1.range();
      }
   }

   public HijrahDate resolveDate(Map<TemporalField, Long> var1, ResolverStyle var2) {
      return (HijrahDate)super.resolveDate(var1, var2);
   }

   int checkValidYear(long var1) {
      if (var1 >= (long)this.getMinimumYear() && var1 <= (long)this.getMaximumYear()) {
         return (int)var1;
      } else {
         throw new DateTimeException("Invalid Hijrah year: " + var1);
      }
   }

   void checkValidDayOfYear(int var1) {
      if (var1 < 1 || var1 > this.getMaximumDayOfYear()) {
         throw new DateTimeException("Invalid Hijrah day of year: " + var1);
      }
   }

   void checkValidMonth(int var1) {
      if (var1 < 1 || var1 > 12) {
         throw new DateTimeException("Invalid Hijrah month: " + var1);
      }
   }

   int[] getHijrahDateInfo(int var1) {
      this.checkCalendarInit();
      if (var1 >= this.minEpochDay && var1 < this.maxEpochDay) {
         int var2 = this.epochDayToEpochMonth(var1);
         int var3 = this.epochMonthToYear(var2);
         int var4 = this.epochMonthToMonth(var2);
         int var5 = this.epochMonthToEpochDay(var2);
         int var6 = var1 - var5;
         int[] var7 = new int[]{var3, var4 + 1, var6 + 1};
         return var7;
      } else {
         throw new DateTimeException("Hijrah date out of range");
      }
   }

   long getEpochDay(int var1, int var2, int var3) {
      this.checkCalendarInit();
      this.checkValidMonth(var2);
      int var4 = this.yearToEpochMonth(var1) + (var2 - 1);
      if (var4 >= 0 && var4 < this.hijrahEpochMonthStartDays.length) {
         if (var3 >= 1 && var3 <= this.getMonthLength(var1, var2)) {
            return (long)(this.epochMonthToEpochDay(var4) + (var3 - 1));
         } else {
            throw new DateTimeException("Invalid Hijrah day of month: " + var3);
         }
      } else {
         throw new DateTimeException("Invalid Hijrah date, year: " + var1 + ", month: " + var2);
      }
   }

   int getDayOfYear(int var1, int var2) {
      return this.yearMonthToDayOfYear(var1, var2 - 1);
   }

   int getMonthLength(int var1, int var2) {
      int var3 = this.yearToEpochMonth(var1) + (var2 - 1);
      if (var3 >= 0 && var3 < this.hijrahEpochMonthStartDays.length) {
         return this.epochMonthLength(var3);
      } else {
         throw new DateTimeException("Invalid Hijrah date, year: " + var1 + ", month: " + var2);
      }
   }

   int getYearLength(int var1) {
      return this.yearMonthToDayOfYear(var1, 12);
   }

   int getMinimumYear() {
      return this.epochMonthToYear(0);
   }

   int getMaximumYear() {
      return this.epochMonthToYear(this.hijrahEpochMonthStartDays.length - 1) - 1;
   }

   int getMaximumMonthLength() {
      return this.maxMonthLength;
   }

   int getMinimumMonthLength() {
      return this.minMonthLength;
   }

   int getMaximumDayOfYear() {
      return this.maxYearLength;
   }

   int getSmallestMaximumDayOfYear() {
      return this.minYearLength;
   }

   private int epochDayToEpochMonth(int var1) {
      int var2 = Arrays.binarySearch(this.hijrahEpochMonthStartDays, var1);
      if (var2 < 0) {
         var2 = -var2 - 2;
      }

      return var2;
   }

   private int epochMonthToYear(int var1) {
      return (var1 + this.hijrahStartEpochMonth) / 12;
   }

   private int yearToEpochMonth(int var1) {
      return var1 * 12 - this.hijrahStartEpochMonth;
   }

   private int epochMonthToMonth(int var1) {
      return (var1 + this.hijrahStartEpochMonth) % 12;
   }

   private int epochMonthToEpochDay(int var1) {
      return this.hijrahEpochMonthStartDays[var1];
   }

   private int yearMonthToDayOfYear(int var1, int var2) {
      int var3 = this.yearToEpochMonth(var1);
      return this.epochMonthToEpochDay(var3 + var2) - this.epochMonthToEpochDay(var3);
   }

   private int epochMonthLength(int var1) {
      return this.hijrahEpochMonthStartDays[var1 + 1] - this.hijrahEpochMonthStartDays[var1];
   }

   private static Properties readConfigProperties(String var0) throws Exception {
      try {
         return (Properties)AccessController.doPrivileged(() -> {
            String var1 = System.getProperty("java.home") + File.separator + "lib";
            File var2 = new File(var1, var0);
            Properties var3 = new Properties();
            FileInputStream var4 = new FileInputStream(var2);
            Throwable var5 = null;

            try {
               var3.load((InputStream)var4);
            } catch (Throwable var14) {
               var5 = var14;
               throw var14;
            } finally {
               if (var4 != null) {
                  if (var5 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var13) {
                        var5.addSuppressed(var13);
                     }
                  } else {
                     var4.close();
                  }
               }

            }

            return var3;
         });
      } catch (PrivilegedActionException var2) {
         throw var2.getException();
      }
   }

   private void loadCalendarData() {
      try {
         String var1 = calendarProperties.getProperty("calendar.hijrah." + this.typeId);
         Objects.requireNonNull(var1, (String)("Resource missing for calendar: calendar.hijrah." + this.typeId));
         Properties var19 = readConfigProperties(var1);
         HashMap var3 = new HashMap();
         int var4 = Integer.MAX_VALUE;
         int var5 = Integer.MIN_VALUE;
         String var6 = null;
         String var7 = null;
         String var8 = null;
         int var9 = 0;
         Iterator var10 = var19.entrySet().iterator();

         while(var10.hasNext()) {
            Map.Entry var11 = (Map.Entry)var10.next();
            String var12 = (String)var11.getKey();
            byte var14 = -1;
            switch(var12.hashCode()) {
            case -1117701862:
               if (var12.equals("iso-start")) {
                  var14 = 3;
               }
               break;
            case 3355:
               if (var12.equals("id")) {
                  var14 = 0;
               }
               break;
            case 3575610:
               if (var12.equals("type")) {
                  var14 = 1;
               }
               break;
            case 351608024:
               if (var12.equals("version")) {
                  var14 = 2;
               }
            }

            switch(var14) {
            case 0:
               var6 = (String)var11.getValue();
               break;
            case 1:
               var7 = (String)var11.getValue();
               break;
            case 2:
               var8 = (String)var11.getValue();
               break;
            case 3:
               int[] var15 = this.parseYMD((String)var11.getValue());
               var9 = (int)LocalDate.of(var15[0], var15[1], var15[2]).toEpochDay();
               break;
            default:
               try {
                  int var22 = Integer.valueOf(var12);
                  int[] var16 = this.parseMonths((String)var11.getValue());
                  var3.put(var22, var16);
                  var5 = Math.max(var5, var22);
                  var4 = Math.min(var4, var22);
               } catch (NumberFormatException var17) {
                  throw new IllegalArgumentException("bad key: " + var12);
               }
            }
         }

         if (!this.getId().equals(var6)) {
            throw new IllegalArgumentException("Configuration is for a different calendar: " + var6);
         } else if (!this.getCalendarType().equals(var7)) {
            throw new IllegalArgumentException("Configuration is for a different calendar type: " + var7);
         } else if (var8 != null && !var8.isEmpty()) {
            if (var9 == 0) {
               throw new IllegalArgumentException("Configuration does not contain a ISO start date");
            } else {
               this.hijrahStartEpochMonth = var4 * 12;
               this.minEpochDay = var9;
               this.hijrahEpochMonthStartDays = this.createEpochMonths(this.minEpochDay, var4, var5, var3);
               this.maxEpochDay = this.hijrahEpochMonthStartDays[this.hijrahEpochMonthStartDays.length - 1];

               for(int var20 = var4; var20 < var5; ++var20) {
                  int var21 = this.getYearLength(var20);
                  this.minYearLength = Math.min(this.minYearLength, var21);
                  this.maxYearLength = Math.max(this.maxYearLength, var21);
               }

            }
         } else {
            throw new IllegalArgumentException("Configuration does not contain a version");
         }
      } catch (Exception var18) {
         PlatformLogger var2 = PlatformLogger.getLogger("java.time.chrono");
         var2.severe("Unable to initialize Hijrah calendar proxy: " + this.typeId, (Throwable)var18);
         throw new DateTimeException("Unable to initialize HijrahCalendar: " + this.typeId, var18);
      }
   }

   private int[] createEpochMonths(int var1, int var2, int var3, Map<Integer, int[]> var4) {
      int var5 = (var3 - var2 + 1) * 12 + 1;
      int var6 = 0;
      int[] var7 = new int[var5];
      this.minMonthLength = Integer.MAX_VALUE;
      this.maxMonthLength = Integer.MIN_VALUE;

      for(int var8 = var2; var8 <= var3; ++var8) {
         int[] var9 = (int[])var4.get(var8);

         for(int var10 = 0; var10 < 12; ++var10) {
            int var11 = var9[var10];
            var7[var6++] = var1;
            if (var11 < 29 || var11 > 32) {
               throw new IllegalArgumentException("Invalid month length in year: " + var2);
            }

            var1 += var11;
            this.minMonthLength = Math.min(this.minMonthLength, var11);
            this.maxMonthLength = Math.max(this.maxMonthLength, var11);
         }
      }

      var7[var6++] = var1;
      if (var6 != var7.length) {
         throw new IllegalStateException("Did not fill epochMonths exactly: ndx = " + var6 + " should be " + var7.length);
      } else {
         return var7;
      }
   }

   private int[] parseMonths(String var1) {
      int[] var2 = new int[12];
      String[] var3 = var1.split("\\s");
      if (var3.length != 12) {
         throw new IllegalArgumentException("wrong number of months on line: " + Arrays.toString((Object[])var3) + "; count: " + var3.length);
      } else {
         for(int var4 = 0; var4 < 12; ++var4) {
            try {
               var2[var4] = Integer.valueOf(var3[var4]);
            } catch (NumberFormatException var6) {
               throw new IllegalArgumentException("bad key: " + var3[var4]);
            }
         }

         return var2;
      }
   }

   private int[] parseYMD(String var1) {
      var1 = var1.trim();

      try {
         if (var1.charAt(4) == '-' && var1.charAt(7) == '-') {
            int[] var2 = new int[]{Integer.valueOf(var1.substring(0, 4)), Integer.valueOf(var1.substring(5, 7)), Integer.valueOf(var1.substring(8, 10))};
            return var2;
         } else {
            throw new IllegalArgumentException("date must be yyyy-MM-dd");
         }
      } catch (NumberFormatException var3) {
         throw new IllegalArgumentException("date must be yyyy-MM-dd", var3);
      }
   }

   Object writeReplace() {
      return super.writeReplace();
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   static {
      try {
         calendarProperties = BaseCalendar.getCalendarProperties();
      } catch (IOException var3) {
         throw new InternalError("Can't initialize lib/calendars.properties", var3);
      }

      try {
         INSTANCE = new HijrahChronology("Hijrah-umalqura");
         AbstractChronology.registerChrono(INSTANCE, "Hijrah");
         AbstractChronology.registerChrono(INSTANCE, "islamic");
      } catch (DateTimeException var2) {
         PlatformLogger var1 = PlatformLogger.getLogger("java.time.chrono");
         var1.severe("Unable to initialize Hijrah calendar: Hijrah-umalqura", (Throwable)var2);
         throw new RuntimeException("Unable to initialize Hijrah-umalqura calendar", var2.getCause());
      }

      registerVariants();
   }
}
