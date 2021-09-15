package java.time.format;

import java.io.IOException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.DateTimeException;
import java.time.Period;
import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class DateTimeFormatter {
   private final DateTimeFormatterBuilder.CompositePrinterParser printerParser;
   private final Locale locale;
   private final DecimalStyle decimalStyle;
   private final ResolverStyle resolverStyle;
   private final Set<TemporalField> resolverFields;
   private final Chronology chrono;
   private final ZoneId zone;
   public static final DateTimeFormatter ISO_LOCAL_DATE;
   public static final DateTimeFormatter ISO_OFFSET_DATE;
   public static final DateTimeFormatter ISO_DATE;
   public static final DateTimeFormatter ISO_LOCAL_TIME;
   public static final DateTimeFormatter ISO_OFFSET_TIME;
   public static final DateTimeFormatter ISO_TIME;
   public static final DateTimeFormatter ISO_LOCAL_DATE_TIME;
   public static final DateTimeFormatter ISO_OFFSET_DATE_TIME;
   public static final DateTimeFormatter ISO_ZONED_DATE_TIME;
   public static final DateTimeFormatter ISO_DATE_TIME;
   public static final DateTimeFormatter ISO_ORDINAL_DATE;
   public static final DateTimeFormatter ISO_WEEK_DATE;
   public static final DateTimeFormatter ISO_INSTANT;
   public static final DateTimeFormatter BASIC_ISO_DATE;
   public static final DateTimeFormatter RFC_1123_DATE_TIME;
   private static final TemporalQuery<Period> PARSED_EXCESS_DAYS;
   private static final TemporalQuery<Boolean> PARSED_LEAP_SECOND;

   public static DateTimeFormatter ofPattern(String var0) {
      return (new DateTimeFormatterBuilder()).appendPattern(var0).toFormatter();
   }

   public static DateTimeFormatter ofPattern(String var0, Locale var1) {
      return (new DateTimeFormatterBuilder()).appendPattern(var0).toFormatter(var1);
   }

   public static DateTimeFormatter ofLocalizedDate(FormatStyle var0) {
      Objects.requireNonNull(var0, (String)"dateStyle");
      return (new DateTimeFormatterBuilder()).appendLocalized(var0, (FormatStyle)null).toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
   }

   public static DateTimeFormatter ofLocalizedTime(FormatStyle var0) {
      Objects.requireNonNull(var0, (String)"timeStyle");
      return (new DateTimeFormatterBuilder()).appendLocalized((FormatStyle)null, var0).toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
   }

   public static DateTimeFormatter ofLocalizedDateTime(FormatStyle var0) {
      Objects.requireNonNull(var0, (String)"dateTimeStyle");
      return (new DateTimeFormatterBuilder()).appendLocalized(var0, var0).toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
   }

   public static DateTimeFormatter ofLocalizedDateTime(FormatStyle var0, FormatStyle var1) {
      Objects.requireNonNull(var0, (String)"dateStyle");
      Objects.requireNonNull(var1, (String)"timeStyle");
      return (new DateTimeFormatterBuilder()).appendLocalized(var0, var1).toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
   }

   public static final TemporalQuery<Period> parsedExcessDays() {
      return PARSED_EXCESS_DAYS;
   }

   public static final TemporalQuery<Boolean> parsedLeapSecond() {
      return PARSED_LEAP_SECOND;
   }

   DateTimeFormatter(DateTimeFormatterBuilder.CompositePrinterParser var1, Locale var2, DecimalStyle var3, ResolverStyle var4, Set<TemporalField> var5, Chronology var6, ZoneId var7) {
      this.printerParser = (DateTimeFormatterBuilder.CompositePrinterParser)Objects.requireNonNull(var1, (String)"printerParser");
      this.resolverFields = var5;
      this.locale = (Locale)Objects.requireNonNull(var2, (String)"locale");
      this.decimalStyle = (DecimalStyle)Objects.requireNonNull(var3, (String)"decimalStyle");
      this.resolverStyle = (ResolverStyle)Objects.requireNonNull(var4, (String)"resolverStyle");
      this.chrono = var6;
      this.zone = var7;
   }

   public Locale getLocale() {
      return this.locale;
   }

   public DateTimeFormatter withLocale(Locale var1) {
      return this.locale.equals(var1) ? this : new DateTimeFormatter(this.printerParser, var1, this.decimalStyle, this.resolverStyle, this.resolverFields, this.chrono, this.zone);
   }

   public DecimalStyle getDecimalStyle() {
      return this.decimalStyle;
   }

   public DateTimeFormatter withDecimalStyle(DecimalStyle var1) {
      return this.decimalStyle.equals(var1) ? this : new DateTimeFormatter(this.printerParser, this.locale, var1, this.resolverStyle, this.resolverFields, this.chrono, this.zone);
   }

   public Chronology getChronology() {
      return this.chrono;
   }

   public DateTimeFormatter withChronology(Chronology var1) {
      return Objects.equals(this.chrono, var1) ? this : new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, this.resolverStyle, this.resolverFields, var1, this.zone);
   }

   public ZoneId getZone() {
      return this.zone;
   }

   public DateTimeFormatter withZone(ZoneId var1) {
      return Objects.equals(this.zone, var1) ? this : new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, this.resolverStyle, this.resolverFields, this.chrono, var1);
   }

   public ResolverStyle getResolverStyle() {
      return this.resolverStyle;
   }

   public DateTimeFormatter withResolverStyle(ResolverStyle var1) {
      Objects.requireNonNull(var1, (String)"resolverStyle");
      return Objects.equals(this.resolverStyle, var1) ? this : new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, var1, this.resolverFields, this.chrono, this.zone);
   }

   public Set<TemporalField> getResolverFields() {
      return this.resolverFields;
   }

   public DateTimeFormatter withResolverFields(TemporalField... var1) {
      Set var2 = null;
      if (var1 != null) {
         var2 = Collections.unmodifiableSet(new HashSet(Arrays.asList(var1)));
      }

      return Objects.equals(this.resolverFields, var2) ? this : new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, this.resolverStyle, var2, this.chrono, this.zone);
   }

   public DateTimeFormatter withResolverFields(Set<TemporalField> var1) {
      if (Objects.equals(this.resolverFields, var1)) {
         return this;
      } else {
         if (var1 != null) {
            var1 = Collections.unmodifiableSet(new HashSet(var1));
         }

         return new DateTimeFormatter(this.printerParser, this.locale, this.decimalStyle, this.resolverStyle, var1, this.chrono, this.zone);
      }
   }

   public String format(TemporalAccessor var1) {
      StringBuilder var2 = new StringBuilder(32);
      this.formatTo(var1, var2);
      return var2.toString();
   }

   public void formatTo(TemporalAccessor var1, Appendable var2) {
      Objects.requireNonNull(var1, (String)"temporal");
      Objects.requireNonNull(var2, (String)"appendable");

      try {
         DateTimePrintContext var3 = new DateTimePrintContext(var1, this);
         if (var2 instanceof StringBuilder) {
            this.printerParser.format(var3, (StringBuilder)var2);
         } else {
            StringBuilder var4 = new StringBuilder(32);
            this.printerParser.format(var3, var4);
            var2.append(var4);
         }

      } catch (IOException var5) {
         throw new DateTimeException(var5.getMessage(), var5);
      }
   }

   public TemporalAccessor parse(CharSequence var1) {
      Objects.requireNonNull(var1, (String)"text");

      try {
         return this.parseResolved0(var1, (ParsePosition)null);
      } catch (DateTimeParseException var3) {
         throw var3;
      } catch (RuntimeException var4) {
         throw this.createError(var1, var4);
      }
   }

   public TemporalAccessor parse(CharSequence var1, ParsePosition var2) {
      Objects.requireNonNull(var1, (String)"text");
      Objects.requireNonNull(var2, (String)"position");

      try {
         return this.parseResolved0(var1, var2);
      } catch (IndexOutOfBoundsException | DateTimeParseException var4) {
         throw var4;
      } catch (RuntimeException var5) {
         throw this.createError(var1, var5);
      }
   }

   public <T> T parse(CharSequence var1, TemporalQuery<T> var2) {
      Objects.requireNonNull(var1, (String)"text");
      Objects.requireNonNull(var2, (String)"query");

      try {
         return this.parseResolved0(var1, (ParsePosition)null).query(var2);
      } catch (DateTimeParseException var4) {
         throw var4;
      } catch (RuntimeException var5) {
         throw this.createError(var1, var5);
      }
   }

   public TemporalAccessor parseBest(CharSequence var1, TemporalQuery<?>... var2) {
      Objects.requireNonNull(var1, (String)"text");
      Objects.requireNonNull(var2, (String)"queries");
      if (var2.length < 2) {
         throw new IllegalArgumentException("At least two queries must be specified");
      } else {
         try {
            TemporalAccessor var3 = this.parseResolved0(var1, (ParsePosition)null);
            TemporalQuery[] var4 = var2;
            int var5 = var2.length;
            int var6 = 0;

            while(var6 < var5) {
               TemporalQuery var7 = var4[var6];

               try {
                  return (TemporalAccessor)var3.query(var7);
               } catch (RuntimeException var9) {
                  ++var6;
               }
            }

            throw new DateTimeException("Unable to convert parsed text using any of the specified queries");
         } catch (DateTimeParseException var10) {
            throw var10;
         } catch (RuntimeException var11) {
            throw this.createError(var1, var11);
         }
      }
   }

   private DateTimeParseException createError(CharSequence var1, RuntimeException var2) {
      String var3;
      if (var1.length() > 64) {
         var3 = var1.subSequence(0, 64).toString() + "...";
      } else {
         var3 = var1.toString();
      }

      return new DateTimeParseException("Text '" + var3 + "' could not be parsed: " + var2.getMessage(), var1, 0, var2);
   }

   private TemporalAccessor parseResolved0(CharSequence var1, ParsePosition var2) {
      ParsePosition var3 = var2 != null ? var2 : new ParsePosition(0);
      DateTimeParseContext var4 = this.parseUnresolved0(var1, var3);
      if (var4 != null && var3.getErrorIndex() < 0 && (var2 != null || var3.getIndex() >= var1.length())) {
         return var4.toResolved(this.resolverStyle, this.resolverFields);
      } else {
         String var5;
         if (var1.length() > 64) {
            var5 = var1.subSequence(0, 64).toString() + "...";
         } else {
            var5 = var1.toString();
         }

         if (var3.getErrorIndex() >= 0) {
            throw new DateTimeParseException("Text '" + var5 + "' could not be parsed at index " + var3.getErrorIndex(), var1, var3.getErrorIndex());
         } else {
            throw new DateTimeParseException("Text '" + var5 + "' could not be parsed, unparsed text found at index " + var3.getIndex(), var1, var3.getIndex());
         }
      }
   }

   public TemporalAccessor parseUnresolved(CharSequence var1, ParsePosition var2) {
      DateTimeParseContext var3 = this.parseUnresolved0(var1, var2);
      return var3 == null ? null : var3.toUnresolved();
   }

   private DateTimeParseContext parseUnresolved0(CharSequence var1, ParsePosition var2) {
      Objects.requireNonNull(var1, (String)"text");
      Objects.requireNonNull(var2, (String)"position");
      DateTimeParseContext var3 = new DateTimeParseContext(this);
      int var4 = var2.getIndex();
      var4 = this.printerParser.parse(var3, var1, var4);
      if (var4 < 0) {
         var2.setErrorIndex(~var4);
         return null;
      } else {
         var2.setIndex(var4);
         return var3;
      }
   }

   DateTimeFormatterBuilder.CompositePrinterParser toPrinterParser(boolean var1) {
      return this.printerParser.withOptional(var1);
   }

   public Format toFormat() {
      return new DateTimeFormatter.ClassicFormat(this, (TemporalQuery)null);
   }

   public Format toFormat(TemporalQuery<?> var1) {
      Objects.requireNonNull(var1, (String)"parseQuery");
      return new DateTimeFormatter.ClassicFormat(this, var1);
   }

   public String toString() {
      String var1 = this.printerParser.toString();
      var1 = var1.startsWith("[") ? var1 : var1.substring(1, var1.length() - 1);
      return var1;
   }

   static {
      ISO_LOCAL_DATE = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
      ISO_OFFSET_DATE = (new DateTimeFormatterBuilder()).parseCaseInsensitive().append(ISO_LOCAL_DATE).appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
      ISO_DATE = (new DateTimeFormatterBuilder()).parseCaseInsensitive().append(ISO_LOCAL_DATE).optionalStart().appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
      ISO_LOCAL_TIME = (new DateTimeFormatterBuilder()).appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).optionalStart().appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter(ResolverStyle.STRICT, (Chronology)null);
      ISO_OFFSET_TIME = (new DateTimeFormatterBuilder()).parseCaseInsensitive().append(ISO_LOCAL_TIME).appendOffsetId().toFormatter(ResolverStyle.STRICT, (Chronology)null);
      ISO_TIME = (new DateTimeFormatterBuilder()).parseCaseInsensitive().append(ISO_LOCAL_TIME).optionalStart().appendOffsetId().toFormatter(ResolverStyle.STRICT, (Chronology)null);
      ISO_LOCAL_DATE_TIME = (new DateTimeFormatterBuilder()).parseCaseInsensitive().append(ISO_LOCAL_DATE).appendLiteral('T').append(ISO_LOCAL_TIME).toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
      ISO_OFFSET_DATE_TIME = (new DateTimeFormatterBuilder()).parseCaseInsensitive().append(ISO_LOCAL_DATE_TIME).appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
      ISO_ZONED_DATE_TIME = (new DateTimeFormatterBuilder()).append(ISO_OFFSET_DATE_TIME).optionalStart().appendLiteral('[').parseCaseSensitive().appendZoneRegionId().appendLiteral(']').toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
      ISO_DATE_TIME = (new DateTimeFormatterBuilder()).append(ISO_LOCAL_DATE_TIME).optionalStart().appendOffsetId().optionalStart().appendLiteral('[').parseCaseSensitive().appendZoneRegionId().appendLiteral(']').toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
      ISO_ORDINAL_DATE = (new DateTimeFormatterBuilder()).parseCaseInsensitive().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.DAY_OF_YEAR, 3).optionalStart().appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
      ISO_WEEK_DATE = (new DateTimeFormatterBuilder()).parseCaseInsensitive().appendValue(IsoFields.WEEK_BASED_YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral("-W").appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_WEEK, 1).optionalStart().appendOffsetId().toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
      ISO_INSTANT = (new DateTimeFormatterBuilder()).parseCaseInsensitive().appendInstant().toFormatter(ResolverStyle.STRICT, (Chronology)null);
      BASIC_ISO_DATE = (new DateTimeFormatterBuilder()).parseCaseInsensitive().appendValue(ChronoField.YEAR, 4).appendValue(ChronoField.MONTH_OF_YEAR, 2).appendValue(ChronoField.DAY_OF_MONTH, 2).optionalStart().appendOffset("+HHMMss", "Z").toFormatter(ResolverStyle.STRICT, IsoChronology.INSTANCE);
      HashMap var0 = new HashMap();
      var0.put(1L, "Mon");
      var0.put(2L, "Tue");
      var0.put(3L, "Wed");
      var0.put(4L, "Thu");
      var0.put(5L, "Fri");
      var0.put(6L, "Sat");
      var0.put(7L, "Sun");
      HashMap var1 = new HashMap();
      var1.put(1L, "Jan");
      var1.put(2L, "Feb");
      var1.put(3L, "Mar");
      var1.put(4L, "Apr");
      var1.put(5L, "May");
      var1.put(6L, "Jun");
      var1.put(7L, "Jul");
      var1.put(8L, "Aug");
      var1.put(9L, "Sep");
      var1.put(10L, "Oct");
      var1.put(11L, "Nov");
      var1.put(12L, "Dec");
      RFC_1123_DATE_TIME = (new DateTimeFormatterBuilder()).parseCaseInsensitive().parseLenient().optionalStart().appendText(ChronoField.DAY_OF_WEEK, (Map)var0).appendLiteral(", ").optionalEnd().appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE).appendLiteral(' ').appendText(ChronoField.MONTH_OF_YEAR, (Map)var1).appendLiteral(' ').appendValue(ChronoField.YEAR, 4).appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).optionalStart().appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).optionalEnd().appendLiteral(' ').appendOffset("+HHMM", "GMT").toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE);
      PARSED_EXCESS_DAYS = (var0x) -> {
         return var0x instanceof Parsed ? ((Parsed)var0x).excessDays : Period.ZERO;
      };
      PARSED_LEAP_SECOND = (var0x) -> {
         return var0x instanceof Parsed ? ((Parsed)var0x).leapSecond : Boolean.FALSE;
      };
   }

   static class ClassicFormat extends Format {
      private final DateTimeFormatter formatter;
      private final TemporalQuery<?> parseType;

      public ClassicFormat(DateTimeFormatter var1, TemporalQuery<?> var2) {
         this.formatter = var1;
         this.parseType = var2;
      }

      public StringBuffer format(Object var1, StringBuffer var2, FieldPosition var3) {
         Objects.requireNonNull(var1, "obj");
         Objects.requireNonNull(var2, (String)"toAppendTo");
         Objects.requireNonNull(var3, (String)"pos");
         if (!(var1 instanceof TemporalAccessor)) {
            throw new IllegalArgumentException("Format target must implement TemporalAccessor");
         } else {
            var3.setBeginIndex(0);
            var3.setEndIndex(0);

            try {
               this.formatter.formatTo((TemporalAccessor)var1, var2);
               return var2;
            } catch (RuntimeException var5) {
               throw new IllegalArgumentException(var5.getMessage(), var5);
            }
         }
      }

      public Object parseObject(String var1) throws ParseException {
         Objects.requireNonNull(var1, (String)"text");

         try {
            return this.parseType == null ? this.formatter.parseResolved0(var1, (ParsePosition)null) : this.formatter.parse(var1, (TemporalQuery)this.parseType);
         } catch (DateTimeParseException var3) {
            throw new ParseException(var3.getMessage(), var3.getErrorIndex());
         } catch (RuntimeException var4) {
            throw (ParseException)(new ParseException(var4.getMessage(), 0)).initCause(var4);
         }
      }

      public Object parseObject(String var1, ParsePosition var2) {
         Objects.requireNonNull(var1, (String)"text");

         DateTimeParseContext var3;
         try {
            var3 = this.formatter.parseUnresolved0(var1, var2);
         } catch (IndexOutOfBoundsException var6) {
            if (var2.getErrorIndex() < 0) {
               var2.setErrorIndex(0);
            }

            return null;
         }

         if (var3 == null) {
            if (var2.getErrorIndex() < 0) {
               var2.setErrorIndex(0);
            }

            return null;
         } else {
            try {
               TemporalAccessor var4 = var3.toResolved(this.formatter.resolverStyle, this.formatter.resolverFields);
               return this.parseType == null ? var4 : var4.query(this.parseType);
            } catch (RuntimeException var5) {
               var2.setErrorIndex(0);
               return null;
            }
         }
      }
   }
}
