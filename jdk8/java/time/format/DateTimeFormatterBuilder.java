package java.time.format;

import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParsePosition;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;
import java.time.zone.ZoneRulesProvider;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;
import sun.util.locale.provider.TimeZoneNameUtility;

public final class DateTimeFormatterBuilder {
   private static final TemporalQuery<ZoneId> QUERY_REGION_ONLY = (var0) -> {
      ZoneId var1 = (ZoneId)var0.query(TemporalQueries.zoneId());
      return var1 != null && !(var1 instanceof ZoneOffset) ? var1 : null;
   };
   private DateTimeFormatterBuilder active = this;
   private final DateTimeFormatterBuilder parent;
   private final List<DateTimeFormatterBuilder.DateTimePrinterParser> printerParsers = new ArrayList();
   private final boolean optional;
   private int padNextWidth;
   private char padNextChar;
   private int valueParserIndex = -1;
   private static final Map<Character, TemporalField> FIELD_MAP = new HashMap();
   static final Comparator<String> LENGTH_SORT;

   public static String getLocalizedDateTimePattern(FormatStyle var0, FormatStyle var1, Chronology var2, Locale var3) {
      Objects.requireNonNull(var3, (String)"locale");
      Objects.requireNonNull(var2, (String)"chrono");
      if (var0 == null && var1 == null) {
         throw new IllegalArgumentException("Either dateStyle or timeStyle must be non-null");
      } else {
         LocaleResources var4 = LocaleProviderAdapter.getResourceBundleBased().getLocaleResources(var3);
         String var5 = var4.getJavaTimeDateTimePattern(convertStyle(var1), convertStyle(var0), var2.getCalendarType());
         return var5;
      }
   }

   private static int convertStyle(FormatStyle var0) {
      return var0 == null ? -1 : var0.ordinal();
   }

   public DateTimeFormatterBuilder() {
      this.parent = null;
      this.optional = false;
   }

   private DateTimeFormatterBuilder(DateTimeFormatterBuilder var1, boolean var2) {
      this.parent = var1;
      this.optional = var2;
   }

   public DateTimeFormatterBuilder parseCaseSensitive() {
      this.appendInternal(DateTimeFormatterBuilder.SettingsParser.SENSITIVE);
      return this;
   }

   public DateTimeFormatterBuilder parseCaseInsensitive() {
      this.appendInternal(DateTimeFormatterBuilder.SettingsParser.INSENSITIVE);
      return this;
   }

   public DateTimeFormatterBuilder parseStrict() {
      this.appendInternal(DateTimeFormatterBuilder.SettingsParser.STRICT);
      return this;
   }

   public DateTimeFormatterBuilder parseLenient() {
      this.appendInternal(DateTimeFormatterBuilder.SettingsParser.LENIENT);
      return this;
   }

   public DateTimeFormatterBuilder parseDefaulting(TemporalField var1, long var2) {
      Objects.requireNonNull(var1, (String)"field");
      this.appendInternal(new DateTimeFormatterBuilder.DefaultValueParser(var1, var2));
      return this;
   }

   public DateTimeFormatterBuilder appendValue(TemporalField var1) {
      Objects.requireNonNull(var1, (String)"field");
      this.appendValue(new DateTimeFormatterBuilder.NumberPrinterParser(var1, 1, 19, SignStyle.NORMAL));
      return this;
   }

   public DateTimeFormatterBuilder appendValue(TemporalField var1, int var2) {
      Objects.requireNonNull(var1, (String)"field");
      if (var2 >= 1 && var2 <= 19) {
         DateTimeFormatterBuilder.NumberPrinterParser var3 = new DateTimeFormatterBuilder.NumberPrinterParser(var1, var2, var2, SignStyle.NOT_NEGATIVE);
         this.appendValue(var3);
         return this;
      } else {
         throw new IllegalArgumentException("The width must be from 1 to 19 inclusive but was " + var2);
      }
   }

   public DateTimeFormatterBuilder appendValue(TemporalField var1, int var2, int var3, SignStyle var4) {
      if (var2 == var3 && var4 == SignStyle.NOT_NEGATIVE) {
         return this.appendValue(var1, var3);
      } else {
         Objects.requireNonNull(var1, (String)"field");
         Objects.requireNonNull(var4, (String)"signStyle");
         if (var2 >= 1 && var2 <= 19) {
            if (var3 >= 1 && var3 <= 19) {
               if (var3 < var2) {
                  throw new IllegalArgumentException("The maximum width must exceed or equal the minimum width but " + var3 + " < " + var2);
               } else {
                  DateTimeFormatterBuilder.NumberPrinterParser var5 = new DateTimeFormatterBuilder.NumberPrinterParser(var1, var2, var3, var4);
                  this.appendValue(var5);
                  return this;
               }
            } else {
               throw new IllegalArgumentException("The maximum width must be from 1 to 19 inclusive but was " + var3);
            }
         } else {
            throw new IllegalArgumentException("The minimum width must be from 1 to 19 inclusive but was " + var2);
         }
      }
   }

   public DateTimeFormatterBuilder appendValueReduced(TemporalField var1, int var2, int var3, int var4) {
      Objects.requireNonNull(var1, (String)"field");
      DateTimeFormatterBuilder.ReducedPrinterParser var5 = new DateTimeFormatterBuilder.ReducedPrinterParser(var1, var2, var3, var4, (ChronoLocalDate)null);
      this.appendValue((DateTimeFormatterBuilder.NumberPrinterParser)var5);
      return this;
   }

   public DateTimeFormatterBuilder appendValueReduced(TemporalField var1, int var2, int var3, ChronoLocalDate var4) {
      Objects.requireNonNull(var1, (String)"field");
      Objects.requireNonNull(var4, (String)"baseDate");
      DateTimeFormatterBuilder.ReducedPrinterParser var5 = new DateTimeFormatterBuilder.ReducedPrinterParser(var1, var2, var3, 0, var4);
      this.appendValue((DateTimeFormatterBuilder.NumberPrinterParser)var5);
      return this;
   }

   private DateTimeFormatterBuilder appendValue(DateTimeFormatterBuilder.NumberPrinterParser var1) {
      if (this.active.valueParserIndex >= 0) {
         int var2 = this.active.valueParserIndex;
         DateTimeFormatterBuilder.NumberPrinterParser var3 = (DateTimeFormatterBuilder.NumberPrinterParser)this.active.printerParsers.get(var2);
         if (var1.minWidth == var1.maxWidth && var1.signStyle == SignStyle.NOT_NEGATIVE) {
            var3 = var3.withSubsequentWidth(var1.maxWidth);
            this.appendInternal(var1.withFixedWidth());
            this.active.valueParserIndex = var2;
         } else {
            var3 = var3.withFixedWidth();
            this.active.valueParserIndex = this.appendInternal(var1);
         }

         this.active.printerParsers.set(var2, var3);
      } else {
         this.active.valueParserIndex = this.appendInternal(var1);
      }

      return this;
   }

   public DateTimeFormatterBuilder appendFraction(TemporalField var1, int var2, int var3, boolean var4) {
      this.appendInternal(new DateTimeFormatterBuilder.FractionPrinterParser(var1, var2, var3, var4));
      return this;
   }

   public DateTimeFormatterBuilder appendText(TemporalField var1) {
      return this.appendText(var1, TextStyle.FULL);
   }

   public DateTimeFormatterBuilder appendText(TemporalField var1, TextStyle var2) {
      Objects.requireNonNull(var1, (String)"field");
      Objects.requireNonNull(var2, (String)"textStyle");
      this.appendInternal(new DateTimeFormatterBuilder.TextPrinterParser(var1, var2, DateTimeTextProvider.getInstance()));
      return this;
   }

   public DateTimeFormatterBuilder appendText(TemporalField var1, Map<Long, String> var2) {
      Objects.requireNonNull(var1, (String)"field");
      Objects.requireNonNull(var2, (String)"textLookup");
      LinkedHashMap var3 = new LinkedHashMap(var2);
      Map var4 = Collections.singletonMap(TextStyle.FULL, var3);
      final DateTimeTextProvider.LocaleStore var5 = new DateTimeTextProvider.LocaleStore(var4);
      DateTimeTextProvider var6 = new DateTimeTextProvider() {
         public String getText(TemporalField var1, long var2, TextStyle var4, Locale var5x) {
            return var5.getText(var2, var4);
         }

         public Iterator<Map.Entry<String, Long>> getTextIterator(TemporalField var1, TextStyle var2, Locale var3) {
            return var5.getTextIterator(var2);
         }
      };
      this.appendInternal(new DateTimeFormatterBuilder.TextPrinterParser(var1, TextStyle.FULL, var6));
      return this;
   }

   public DateTimeFormatterBuilder appendInstant() {
      this.appendInternal(new DateTimeFormatterBuilder.InstantPrinterParser(-2));
      return this;
   }

   public DateTimeFormatterBuilder appendInstant(int var1) {
      if (var1 >= -1 && var1 <= 9) {
         this.appendInternal(new DateTimeFormatterBuilder.InstantPrinterParser(var1));
         return this;
      } else {
         throw new IllegalArgumentException("The fractional digits must be from -1 to 9 inclusive but was " + var1);
      }
   }

   public DateTimeFormatterBuilder appendOffsetId() {
      this.appendInternal(DateTimeFormatterBuilder.OffsetIdPrinterParser.INSTANCE_ID_Z);
      return this;
   }

   public DateTimeFormatterBuilder appendOffset(String var1, String var2) {
      this.appendInternal(new DateTimeFormatterBuilder.OffsetIdPrinterParser(var1, var2));
      return this;
   }

   public DateTimeFormatterBuilder appendLocalizedOffset(TextStyle var1) {
      Objects.requireNonNull(var1, (String)"style");
      if (var1 != TextStyle.FULL && var1 != TextStyle.SHORT) {
         throw new IllegalArgumentException("Style must be either full or short");
      } else {
         this.appendInternal(new DateTimeFormatterBuilder.LocalizedOffsetIdPrinterParser(var1));
         return this;
      }
   }

   public DateTimeFormatterBuilder appendZoneId() {
      this.appendInternal(new DateTimeFormatterBuilder.ZoneIdPrinterParser(TemporalQueries.zoneId(), "ZoneId()"));
      return this;
   }

   public DateTimeFormatterBuilder appendZoneRegionId() {
      this.appendInternal(new DateTimeFormatterBuilder.ZoneIdPrinterParser(QUERY_REGION_ONLY, "ZoneRegionId()"));
      return this;
   }

   public DateTimeFormatterBuilder appendZoneOrOffsetId() {
      this.appendInternal(new DateTimeFormatterBuilder.ZoneIdPrinterParser(TemporalQueries.zone(), "ZoneOrOffsetId()"));
      return this;
   }

   public DateTimeFormatterBuilder appendZoneText(TextStyle var1) {
      this.appendInternal(new DateTimeFormatterBuilder.ZoneTextPrinterParser(var1, (Set)null));
      return this;
   }

   public DateTimeFormatterBuilder appendZoneText(TextStyle var1, Set<ZoneId> var2) {
      Objects.requireNonNull(var2, (String)"preferredZones");
      this.appendInternal(new DateTimeFormatterBuilder.ZoneTextPrinterParser(var1, var2));
      return this;
   }

   public DateTimeFormatterBuilder appendChronologyId() {
      this.appendInternal(new DateTimeFormatterBuilder.ChronoPrinterParser((TextStyle)null));
      return this;
   }

   public DateTimeFormatterBuilder appendChronologyText(TextStyle var1) {
      Objects.requireNonNull(var1, (String)"textStyle");
      this.appendInternal(new DateTimeFormatterBuilder.ChronoPrinterParser(var1));
      return this;
   }

   public DateTimeFormatterBuilder appendLocalized(FormatStyle var1, FormatStyle var2) {
      if (var1 == null && var2 == null) {
         throw new IllegalArgumentException("Either the date or time style must be non-null");
      } else {
         this.appendInternal(new DateTimeFormatterBuilder.LocalizedPrinterParser(var1, var2));
         return this;
      }
   }

   public DateTimeFormatterBuilder appendLiteral(char var1) {
      this.appendInternal(new DateTimeFormatterBuilder.CharLiteralPrinterParser(var1));
      return this;
   }

   public DateTimeFormatterBuilder appendLiteral(String var1) {
      Objects.requireNonNull(var1, (String)"literal");
      if (var1.length() > 0) {
         if (var1.length() == 1) {
            this.appendInternal(new DateTimeFormatterBuilder.CharLiteralPrinterParser(var1.charAt(0)));
         } else {
            this.appendInternal(new DateTimeFormatterBuilder.StringLiteralPrinterParser(var1));
         }
      }

      return this;
   }

   public DateTimeFormatterBuilder append(DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      this.appendInternal(var1.toPrinterParser(false));
      return this;
   }

   public DateTimeFormatterBuilder appendOptional(DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      this.appendInternal(var1.toPrinterParser(true));
      return this;
   }

   public DateTimeFormatterBuilder appendPattern(String var1) {
      Objects.requireNonNull(var1, (String)"pattern");
      this.parsePattern(var1);
      return this;
   }

   private void parsePattern(String var1) {
      for(int var2 = 0; var2 < var1.length(); ++var2) {
         char var3 = var1.charAt(var2);
         int var4;
         if (var3 >= 'A' && var3 <= 'Z' || var3 >= 'a' && var3 <= 'z') {
            for(var4 = var2++; var2 < var1.length() && var1.charAt(var2) == var3; ++var2) {
            }

            int var8 = var2 - var4;
            if (var3 == 'p') {
               int var6 = 0;
               if (var2 < var1.length()) {
                  var3 = var1.charAt(var2);
                  if (var3 >= 'A' && var3 <= 'Z' || var3 >= 'a' && var3 <= 'z') {
                     var6 = var8;

                     for(var4 = var2++; var2 < var1.length() && var1.charAt(var2) == var3; ++var2) {
                     }

                     var8 = var2 - var4;
                  }
               }

               if (var6 == 0) {
                  throw new IllegalArgumentException("Pad letter 'p' must be followed by valid pad pattern: " + var1);
               }

               this.padNext(var6);
            }

            TemporalField var9 = (TemporalField)FIELD_MAP.get(var3);
            if (var9 != null) {
               this.parseField(var3, var8, var9);
            } else if (var3 == 'z') {
               if (var8 > 4) {
                  throw new IllegalArgumentException("Too many pattern letters: " + var3);
               }

               if (var8 == 4) {
                  this.appendZoneText(TextStyle.FULL);
               } else {
                  this.appendZoneText(TextStyle.SHORT);
               }
            } else if (var3 == 'V') {
               if (var8 != 2) {
                  throw new IllegalArgumentException("Pattern letter count must be 2: " + var3);
               }

               this.appendZoneId();
            } else if (var3 == 'Z') {
               if (var8 < 4) {
                  this.appendOffset("+HHMM", "+0000");
               } else if (var8 == 4) {
                  this.appendLocalizedOffset(TextStyle.FULL);
               } else {
                  if (var8 != 5) {
                     throw new IllegalArgumentException("Too many pattern letters: " + var3);
                  }

                  this.appendOffset("+HH:MM:ss", "Z");
               }
            } else if (var3 == 'O') {
               if (var8 == 1) {
                  this.appendLocalizedOffset(TextStyle.SHORT);
               } else {
                  if (var8 != 4) {
                     throw new IllegalArgumentException("Pattern letter count must be 1 or 4: " + var3);
                  }

                  this.appendLocalizedOffset(TextStyle.FULL);
               }
            } else if (var3 == 'X') {
               if (var8 > 5) {
                  throw new IllegalArgumentException("Too many pattern letters: " + var3);
               }

               this.appendOffset(DateTimeFormatterBuilder.OffsetIdPrinterParser.PATTERNS[var8 + (var8 == 1 ? 0 : 1)], "Z");
            } else if (var3 == 'x') {
               if (var8 > 5) {
                  throw new IllegalArgumentException("Too many pattern letters: " + var3);
               }

               String var7 = var8 == 1 ? "+00" : (var8 % 2 == 0 ? "+0000" : "+00:00");
               this.appendOffset(DateTimeFormatterBuilder.OffsetIdPrinterParser.PATTERNS[var8 + (var8 == 1 ? 0 : 1)], var7);
            } else if (var3 == 'W') {
               if (var8 > 1) {
                  throw new IllegalArgumentException("Too many pattern letters: " + var3);
               }

               this.appendInternal(new DateTimeFormatterBuilder.WeekBasedFieldPrinterParser(var3, var8));
            } else if (var3 == 'w') {
               if (var8 > 2) {
                  throw new IllegalArgumentException("Too many pattern letters: " + var3);
               }

               this.appendInternal(new DateTimeFormatterBuilder.WeekBasedFieldPrinterParser(var3, var8));
            } else {
               if (var3 != 'Y') {
                  throw new IllegalArgumentException("Unknown pattern letter: " + var3);
               }

               this.appendInternal(new DateTimeFormatterBuilder.WeekBasedFieldPrinterParser(var3, var8));
            }

            --var2;
         } else if (var3 == '\'') {
            for(var4 = var2++; var2 < var1.length(); ++var2) {
               if (var1.charAt(var2) == '\'') {
                  if (var2 + 1 >= var1.length() || var1.charAt(var2 + 1) != '\'') {
                     break;
                  }

                  ++var2;
               }
            }

            if (var2 >= var1.length()) {
               throw new IllegalArgumentException("Pattern ends with an incomplete string literal: " + var1);
            }

            String var5 = var1.substring(var4 + 1, var2);
            if (var5.length() == 0) {
               this.appendLiteral('\'');
            } else {
               this.appendLiteral(var5.replace("''", "'"));
            }
         } else if (var3 == '[') {
            this.optionalStart();
         } else if (var3 == ']') {
            if (this.active.parent == null) {
               throw new IllegalArgumentException("Pattern invalid as it contains ] without previous [");
            }

            this.optionalEnd();
         } else {
            if (var3 == '{' || var3 == '}' || var3 == '#') {
               throw new IllegalArgumentException("Pattern includes reserved character: '" + var3 + "'");
            }

            this.appendLiteral(var3);
         }
      }

   }

   private void parseField(char var1, int var2, TemporalField var3) {
      boolean var4 = false;
      switch(var1) {
      case 'D':
         if (var2 == 1) {
            this.appendValue(var3);
         } else {
            if (var2 > 3) {
               throw new IllegalArgumentException("Too many pattern letters: " + var1);
            }

            this.appendValue(var3, var2);
         }
         break;
      case 'F':
         if (var2 != 1) {
            throw new IllegalArgumentException("Too many pattern letters: " + var1);
         }

         this.appendValue(var3);
         break;
      case 'G':
         switch(var2) {
         case 1:
         case 2:
         case 3:
            this.appendText(var3, TextStyle.SHORT);
            return;
         case 4:
            this.appendText(var3, TextStyle.FULL);
            return;
         case 5:
            this.appendText(var3, TextStyle.NARROW);
            return;
         default:
            throw new IllegalArgumentException("Too many pattern letters: " + var1);
         }
      case 'H':
      case 'K':
      case 'd':
      case 'h':
      case 'k':
      case 'm':
      case 's':
         if (var2 == 1) {
            this.appendValue(var3);
         } else {
            if (var2 != 2) {
               throw new IllegalArgumentException("Too many pattern letters: " + var1);
            }

            this.appendValue(var3, var2);
         }
         break;
      case 'I':
      case 'J':
      case 'N':
      case 'O':
      case 'P':
      case 'R':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      case 'Z':
      case '[':
      case '\\':
      case ']':
      case '^':
      case '_':
      case '`':
      case 'b':
      case 'f':
      case 'g':
      case 'i':
      case 'j':
      case 'l':
      case 'n':
      case 'o':
      case 'p':
      case 'r':
      case 't':
      case 'v':
      case 'w':
      case 'x':
      default:
         if (var2 == 1) {
            this.appendValue(var3);
         } else {
            this.appendValue(var3, var2);
         }
         break;
      case 'S':
         this.appendFraction(ChronoField.NANO_OF_SECOND, var2, var2, false);
         break;
      case 'a':
         if (var2 != 1) {
            throw new IllegalArgumentException("Too many pattern letters: " + var1);
         }

         this.appendText(var3, TextStyle.SHORT);
         break;
      case 'c':
         if (var2 == 2) {
            throw new IllegalArgumentException("Invalid pattern \"cc\"");
         }
      case 'L':
      case 'q':
         var4 = true;
      case 'E':
      case 'M':
      case 'Q':
      case 'e':
         switch(var2) {
         case 1:
         case 2:
            if (var1 != 'c' && var1 != 'e') {
               if (var1 == 'E') {
                  this.appendText(var3, TextStyle.SHORT);
                  return;
               } else {
                  if (var2 == 1) {
                     this.appendValue(var3);
                  } else {
                     this.appendValue(var3, 2);
                  }

                  return;
               }
            } else {
               this.appendInternal(new DateTimeFormatterBuilder.WeekBasedFieldPrinterParser(var1, var2));
               return;
            }
         case 3:
            this.appendText(var3, var4 ? TextStyle.SHORT_STANDALONE : TextStyle.SHORT);
            return;
         case 4:
            this.appendText(var3, var4 ? TextStyle.FULL_STANDALONE : TextStyle.FULL);
            return;
         case 5:
            this.appendText(var3, var4 ? TextStyle.NARROW_STANDALONE : TextStyle.NARROW);
            return;
         default:
            throw new IllegalArgumentException("Too many pattern letters: " + var1);
         }
      case 'u':
      case 'y':
         if (var2 == 2) {
            this.appendValueReduced(var3, 2, 2, DateTimeFormatterBuilder.ReducedPrinterParser.BASE_DATE);
         } else if (var2 < 4) {
            this.appendValue(var3, var2, 19, SignStyle.NORMAL);
         } else {
            this.appendValue(var3, var2, 19, SignStyle.EXCEEDS_PAD);
         }
      }

   }

   public DateTimeFormatterBuilder padNext(int var1) {
      return this.padNext(var1, ' ');
   }

   public DateTimeFormatterBuilder padNext(int var1, char var2) {
      if (var1 < 1) {
         throw new IllegalArgumentException("The pad width must be at least one but was " + var1);
      } else {
         this.active.padNextWidth = var1;
         this.active.padNextChar = var2;
         this.active.valueParserIndex = -1;
         return this;
      }
   }

   public DateTimeFormatterBuilder optionalStart() {
      this.active.valueParserIndex = -1;
      this.active = new DateTimeFormatterBuilder(this.active, true);
      return this;
   }

   public DateTimeFormatterBuilder optionalEnd() {
      if (this.active.parent == null) {
         throw new IllegalStateException("Cannot call optionalEnd() as there was no previous call to optionalStart()");
      } else {
         if (this.active.printerParsers.size() > 0) {
            DateTimeFormatterBuilder.CompositePrinterParser var1 = new DateTimeFormatterBuilder.CompositePrinterParser(this.active.printerParsers, this.active.optional);
            this.active = this.active.parent;
            this.appendInternal(var1);
         } else {
            this.active = this.active.parent;
         }

         return this;
      }
   }

   private int appendInternal(DateTimeFormatterBuilder.DateTimePrinterParser var1) {
      Objects.requireNonNull(var1, "pp");
      if (this.active.padNextWidth > 0) {
         if (var1 != null) {
            var1 = new DateTimeFormatterBuilder.PadPrinterParserDecorator((DateTimeFormatterBuilder.DateTimePrinterParser)var1, this.active.padNextWidth, this.active.padNextChar);
         }

         this.active.padNextWidth = 0;
         this.active.padNextChar = 0;
      }

      this.active.printerParsers.add(var1);
      this.active.valueParserIndex = -1;
      return this.active.printerParsers.size() - 1;
   }

   public DateTimeFormatter toFormatter() {
      return this.toFormatter(Locale.getDefault(Locale.Category.FORMAT));
   }

   public DateTimeFormatter toFormatter(Locale var1) {
      return this.toFormatter(var1, ResolverStyle.SMART, (Chronology)null);
   }

   DateTimeFormatter toFormatter(ResolverStyle var1, Chronology var2) {
      return this.toFormatter(Locale.getDefault(Locale.Category.FORMAT), var1, var2);
   }

   private DateTimeFormatter toFormatter(Locale var1, ResolverStyle var2, Chronology var3) {
      Objects.requireNonNull(var1, (String)"locale");

      while(this.active.parent != null) {
         this.optionalEnd();
      }

      DateTimeFormatterBuilder.CompositePrinterParser var4 = new DateTimeFormatterBuilder.CompositePrinterParser(this.printerParsers, false);
      return new DateTimeFormatter(var4, var1, DecimalStyle.STANDARD, var2, (Set)null, var3, (ZoneId)null);
   }

   static {
      FIELD_MAP.put('G', ChronoField.ERA);
      FIELD_MAP.put('y', ChronoField.YEAR_OF_ERA);
      FIELD_MAP.put('u', ChronoField.YEAR);
      FIELD_MAP.put('Q', IsoFields.QUARTER_OF_YEAR);
      FIELD_MAP.put('q', IsoFields.QUARTER_OF_YEAR);
      FIELD_MAP.put('M', ChronoField.MONTH_OF_YEAR);
      FIELD_MAP.put('L', ChronoField.MONTH_OF_YEAR);
      FIELD_MAP.put('D', ChronoField.DAY_OF_YEAR);
      FIELD_MAP.put('d', ChronoField.DAY_OF_MONTH);
      FIELD_MAP.put('F', ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);
      FIELD_MAP.put('E', ChronoField.DAY_OF_WEEK);
      FIELD_MAP.put('c', ChronoField.DAY_OF_WEEK);
      FIELD_MAP.put('e', ChronoField.DAY_OF_WEEK);
      FIELD_MAP.put('a', ChronoField.AMPM_OF_DAY);
      FIELD_MAP.put('H', ChronoField.HOUR_OF_DAY);
      FIELD_MAP.put('k', ChronoField.CLOCK_HOUR_OF_DAY);
      FIELD_MAP.put('K', ChronoField.HOUR_OF_AMPM);
      FIELD_MAP.put('h', ChronoField.CLOCK_HOUR_OF_AMPM);
      FIELD_MAP.put('m', ChronoField.MINUTE_OF_HOUR);
      FIELD_MAP.put('s', ChronoField.SECOND_OF_MINUTE);
      FIELD_MAP.put('S', ChronoField.NANO_OF_SECOND);
      FIELD_MAP.put('A', ChronoField.MILLI_OF_DAY);
      FIELD_MAP.put('n', ChronoField.NANO_OF_SECOND);
      FIELD_MAP.put('N', ChronoField.NANO_OF_DAY);
      LENGTH_SORT = new Comparator<String>() {
         public int compare(String var1, String var2) {
            return var1.length() == var2.length() ? var1.compareTo(var2) : var1.length() - var2.length();
         }
      };
   }

   static final class WeekBasedFieldPrinterParser implements DateTimeFormatterBuilder.DateTimePrinterParser {
      private char chr;
      private int count;

      WeekBasedFieldPrinterParser(char var1, int var2) {
         this.chr = var1;
         this.count = var2;
      }

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         return this.printerParser(var1.getLocale()).format(var1, var2);
      }

      public int parse(DateTimeParseContext var1, CharSequence var2, int var3) {
         return this.printerParser(var1.getLocale()).parse(var1, var2, var3);
      }

      private DateTimeFormatterBuilder.DateTimePrinterParser printerParser(Locale var1) {
         WeekFields var2 = WeekFields.of(var1);
         TemporalField var3 = null;
         switch(this.chr) {
         case 'W':
            var3 = var2.weekOfMonth();
            break;
         case 'Y':
            var3 = var2.weekBasedYear();
            if (this.count == 2) {
               return new DateTimeFormatterBuilder.ReducedPrinterParser(var3, 2, 2, 0, DateTimeFormatterBuilder.ReducedPrinterParser.BASE_DATE, 0);
            }

            return new DateTimeFormatterBuilder.NumberPrinterParser(var3, this.count, 19, this.count < 4 ? SignStyle.NORMAL : SignStyle.EXCEEDS_PAD, -1);
         case 'c':
         case 'e':
            var3 = var2.dayOfWeek();
            break;
         case 'w':
            var3 = var2.weekOfWeekBasedYear();
            break;
         default:
            throw new IllegalStateException("unreachable");
         }

         return new DateTimeFormatterBuilder.NumberPrinterParser(var3, this.count == 2 ? 2 : 1, 2, SignStyle.NOT_NEGATIVE);
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder(30);
         var1.append("Localized(");
         if (this.chr == 'Y') {
            if (this.count == 1) {
               var1.append("WeekBasedYear");
            } else if (this.count == 2) {
               var1.append("ReducedValue(WeekBasedYear,2,2,2000-01-01)");
            } else {
               var1.append("WeekBasedYear,").append(this.count).append(",").append((int)19).append(",").append((Object)(this.count < 4 ? SignStyle.NORMAL : SignStyle.EXCEEDS_PAD));
            }
         } else {
            switch(this.chr) {
            case 'W':
               var1.append("WeekOfMonth");
               break;
            case 'c':
            case 'e':
               var1.append("DayOfWeek");
               break;
            case 'w':
               var1.append("WeekOfWeekBasedYear");
            }

            var1.append(",");
            var1.append(this.count);
         }

         var1.append(")");
         return var1.toString();
      }
   }

   static final class LocalizedPrinterParser implements DateTimeFormatterBuilder.DateTimePrinterParser {
      private static final ConcurrentMap<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap(16, 0.75F, 2);
      private final FormatStyle dateStyle;
      private final FormatStyle timeStyle;

      LocalizedPrinterParser(FormatStyle var1, FormatStyle var2) {
         this.dateStyle = var1;
         this.timeStyle = var2;
      }

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         Chronology var3 = Chronology.from(var1.getTemporal());
         return this.formatter(var1.getLocale(), var3).toPrinterParser(false).format(var1, var2);
      }

      public int parse(DateTimeParseContext var1, CharSequence var2, int var3) {
         Chronology var4 = var1.getEffectiveChronology();
         return this.formatter(var1.getLocale(), var4).toPrinterParser(false).parse(var1, var2, var3);
      }

      private DateTimeFormatter formatter(Locale var1, Chronology var2) {
         String var3 = var2.getId() + '|' + var1.toString() + '|' + this.dateStyle + this.timeStyle;
         DateTimeFormatter var4 = (DateTimeFormatter)FORMATTER_CACHE.get(var3);
         if (var4 == null) {
            String var5 = DateTimeFormatterBuilder.getLocalizedDateTimePattern(this.dateStyle, this.timeStyle, var2, var1);
            var4 = (new DateTimeFormatterBuilder()).appendPattern(var5).toFormatter(var1);
            DateTimeFormatter var6 = (DateTimeFormatter)FORMATTER_CACHE.putIfAbsent(var3, var4);
            if (var6 != null) {
               var4 = var6;
            }
         }

         return var4;
      }

      public String toString() {
         return "Localized(" + (this.dateStyle != null ? this.dateStyle : "") + "," + (this.timeStyle != null ? this.timeStyle : "") + ")";
      }
   }

   static final class ChronoPrinterParser implements DateTimeFormatterBuilder.DateTimePrinterParser {
      private final TextStyle textStyle;

      ChronoPrinterParser(TextStyle var1) {
         this.textStyle = var1;
      }

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         Chronology var3 = (Chronology)var1.getValue(TemporalQueries.chronology());
         if (var3 == null) {
            return false;
         } else {
            if (this.textStyle == null) {
               var2.append(var3.getId());
            } else {
               var2.append(this.getChronologyName(var3, var1.getLocale()));
            }

            return true;
         }
      }

      public int parse(DateTimeParseContext var1, CharSequence var2, int var3) {
         if (var3 >= 0 && var3 <= var2.length()) {
            Set var4 = Chronology.getAvailableChronologies();
            Chronology var5 = null;
            int var6 = -1;
            Iterator var7 = var4.iterator();

            while(var7.hasNext()) {
               Chronology var8 = (Chronology)var7.next();
               String var9;
               if (this.textStyle == null) {
                  var9 = var8.getId();
               } else {
                  var9 = this.getChronologyName(var8, var1.getLocale());
               }

               int var10 = var9.length();
               if (var10 > var6 && var1.subSequenceEquals(var2, var3, var9, 0, var10)) {
                  var5 = var8;
                  var6 = var10;
               }
            }

            if (var5 == null) {
               return ~var3;
            } else {
               var1.setParsed(var5);
               return var3 + var6;
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      private String getChronologyName(Chronology var1, Locale var2) {
         String var3 = "calendarname." + var1.getCalendarType();
         String var4 = (String)DateTimeTextProvider.getLocalizedResource(var3, var2);
         return var4 != null ? var4 : var1.getId();
      }
   }

   static class PrefixTree {
      protected String key;
      protected String value;
      protected char c0;
      protected DateTimeFormatterBuilder.PrefixTree child;
      protected DateTimeFormatterBuilder.PrefixTree sibling;

      private PrefixTree(String var1, String var2, DateTimeFormatterBuilder.PrefixTree var3) {
         this.key = var1;
         this.value = var2;
         this.child = var3;
         if (var1.length() == 0) {
            this.c0 = '\uffff';
         } else {
            this.c0 = this.key.charAt(0);
         }

      }

      public static DateTimeFormatterBuilder.PrefixTree newTree(DateTimeParseContext var0) {
         return (DateTimeFormatterBuilder.PrefixTree)(var0.isCaseSensitive() ? new DateTimeFormatterBuilder.PrefixTree("", (String)null, (DateTimeFormatterBuilder.PrefixTree)null) : new DateTimeFormatterBuilder.PrefixTree.CI("", (String)null, (DateTimeFormatterBuilder.PrefixTree)null));
      }

      public static DateTimeFormatterBuilder.PrefixTree newTree(Set<String> var0, DateTimeParseContext var1) {
         DateTimeFormatterBuilder.PrefixTree var2 = newTree(var1);
         Iterator var3 = var0.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            var2.add0(var4, var4);
         }

         return var2;
      }

      public DateTimeFormatterBuilder.PrefixTree copyTree() {
         DateTimeFormatterBuilder.PrefixTree var1 = new DateTimeFormatterBuilder.PrefixTree(this.key, this.value, (DateTimeFormatterBuilder.PrefixTree)null);
         if (this.child != null) {
            var1.child = this.child.copyTree();
         }

         if (this.sibling != null) {
            var1.sibling = this.sibling.copyTree();
         }

         return var1;
      }

      public boolean add(String var1, String var2) {
         return this.add0(var1, var2);
      }

      private boolean add0(String var1, String var2) {
         var1 = this.toKey(var1);
         int var3 = this.prefixLength(var1);
         DateTimeFormatterBuilder.PrefixTree var5;
         if (var3 == this.key.length()) {
            if (var3 < var1.length()) {
               String var6 = var1.substring(var3);

               for(var5 = this.child; var5 != null; var5 = var5.sibling) {
                  if (this.isEqual(var5.c0, var6.charAt(0))) {
                     return var5.add0(var6, var2);
                  }
               }

               var5 = this.newNode(var6, var2, (DateTimeFormatterBuilder.PrefixTree)null);
               var5.sibling = this.child;
               this.child = var5;
               return true;
            } else {
               this.value = var2;
               return true;
            }
         } else {
            DateTimeFormatterBuilder.PrefixTree var4 = this.newNode(this.key.substring(var3), this.value, this.child);
            this.key = var1.substring(0, var3);
            this.child = var4;
            if (var3 < var1.length()) {
               var5 = this.newNode(var1.substring(var3), var2, (DateTimeFormatterBuilder.PrefixTree)null);
               this.child.sibling = var5;
               this.value = null;
            } else {
               this.value = var2;
            }

            return true;
         }
      }

      public String match(CharSequence var1, int var2, int var3) {
         if (!this.prefixOf(var1, var2, var3)) {
            return null;
         } else {
            if (this.child != null && (var2 += this.key.length()) != var3) {
               DateTimeFormatterBuilder.PrefixTree var4 = this.child;

               do {
                  if (this.isEqual(var4.c0, var1.charAt(var2))) {
                     String var5 = var4.match(var1, var2, var3);
                     if (var5 != null) {
                        return var5;
                     }

                     return this.value;
                  }

                  var4 = var4.sibling;
               } while(var4 != null);
            }

            return this.value;
         }
      }

      public String match(CharSequence var1, ParsePosition var2) {
         int var3 = var2.getIndex();
         int var4 = var1.length();
         if (!this.prefixOf(var1, var3, var4)) {
            return null;
         } else {
            var3 += this.key.length();
            if (this.child != null && var3 != var4) {
               DateTimeFormatterBuilder.PrefixTree var5 = this.child;

               do {
                  if (this.isEqual(var5.c0, var1.charAt(var3))) {
                     var2.setIndex(var3);
                     String var6 = var5.match(var1, var2);
                     if (var6 != null) {
                        return var6;
                     }
                     break;
                  }

                  var5 = var5.sibling;
               } while(var5 != null);
            }

            var2.setIndex(var3);
            return this.value;
         }
      }

      protected String toKey(String var1) {
         return var1;
      }

      protected DateTimeFormatterBuilder.PrefixTree newNode(String var1, String var2, DateTimeFormatterBuilder.PrefixTree var3) {
         return new DateTimeFormatterBuilder.PrefixTree(var1, var2, var3);
      }

      protected boolean isEqual(char var1, char var2) {
         return var1 == var2;
      }

      protected boolean prefixOf(CharSequence var1, int var2, int var3) {
         if (var1 instanceof String) {
            return ((String)var1).startsWith(this.key, var2);
         } else {
            int var4 = this.key.length();
            if (var4 > var3 - var2) {
               return false;
            } else {
               int var5 = 0;

               do {
                  if (var4-- <= 0) {
                     return true;
                  }
               } while(this.isEqual(this.key.charAt(var5++), var1.charAt(var2++)));

               return false;
            }
         }
      }

      private int prefixLength(String var1) {
         int var2;
         for(var2 = 0; var2 < var1.length() && var2 < this.key.length(); ++var2) {
            if (!this.isEqual(var1.charAt(var2), this.key.charAt(var2))) {
               return var2;
            }
         }

         return var2;
      }

      // $FF: synthetic method
      PrefixTree(String var1, String var2, DateTimeFormatterBuilder.PrefixTree var3, Object var4) {
         this(var1, var2, var3);
      }

      private static class LENIENT extends DateTimeFormatterBuilder.PrefixTree.CI {
         private LENIENT(String var1, String var2, DateTimeFormatterBuilder.PrefixTree var3) {
            super(var1, var2, var3, null);
         }

         protected DateTimeFormatterBuilder.PrefixTree.CI newNode(String var1, String var2, DateTimeFormatterBuilder.PrefixTree var3) {
            return new DateTimeFormatterBuilder.PrefixTree.LENIENT(var1, var2, var3);
         }

         private boolean isLenientChar(char var1) {
            return var1 == ' ' || var1 == '_' || var1 == '/';
         }

         protected String toKey(String var1) {
            for(int var2 = 0; var2 < var1.length(); ++var2) {
               if (this.isLenientChar(var1.charAt(var2))) {
                  StringBuilder var3 = new StringBuilder(var1.length());
                  var3.append((CharSequence)var1, 0, var2);
                  ++var2;

                  for(; var2 < var1.length(); ++var2) {
                     if (!this.isLenientChar(var1.charAt(var2))) {
                        var3.append(var1.charAt(var2));
                     }
                  }

                  return var3.toString();
               }
            }

            return var1;
         }

         public String match(CharSequence var1, ParsePosition var2) {
            int var3 = var2.getIndex();
            int var4 = var1.length();
            int var5 = this.key.length();
            int var6 = 0;

            while(var6 < var5 && var3 < var4) {
               if (this.isLenientChar(var1.charAt(var3))) {
                  ++var3;
               } else if (!this.isEqual(this.key.charAt(var6++), var1.charAt(var3++))) {
                  return null;
               }
            }

            if (var6 != var5) {
               return null;
            } else {
               if (this.child != null && var3 != var4) {
                  int var7;
                  for(var7 = var3; var7 < var4 && this.isLenientChar(var1.charAt(var7)); ++var7) {
                  }

                  if (var7 < var4) {
                     DateTimeFormatterBuilder.PrefixTree var8 = this.child;

                     do {
                        if (this.isEqual(var8.c0, var1.charAt(var7))) {
                           var2.setIndex(var7);
                           String var9 = var8.match(var1, var2);
                           if (var9 != null) {
                              return var9;
                           }
                           break;
                        }

                        var8 = var8.sibling;
                     } while(var8 != null);
                  }
               }

               var2.setIndex(var3);
               return this.value;
            }
         }
      }

      private static class CI extends DateTimeFormatterBuilder.PrefixTree {
         private CI(String var1, String var2, DateTimeFormatterBuilder.PrefixTree var3) {
            super(var1, var2, var3, null);
         }

         protected DateTimeFormatterBuilder.PrefixTree.CI newNode(String var1, String var2, DateTimeFormatterBuilder.PrefixTree var3) {
            return new DateTimeFormatterBuilder.PrefixTree.CI(var1, var2, var3);
         }

         protected boolean isEqual(char var1, char var2) {
            return DateTimeParseContext.charEqualsIgnoreCase(var1, var2);
         }

         protected boolean prefixOf(CharSequence var1, int var2, int var3) {
            int var4 = this.key.length();
            if (var4 > var3 - var2) {
               return false;
            } else {
               int var5 = 0;

               do {
                  if (var4-- <= 0) {
                     return true;
                  }
               } while(this.isEqual(this.key.charAt(var5++), var1.charAt(var2++)));

               return false;
            }
         }

         // $FF: synthetic method
         CI(String var1, String var2, DateTimeFormatterBuilder.PrefixTree var3, Object var4) {
            this(var1, var2, var3);
         }
      }
   }

   static class ZoneIdPrinterParser implements DateTimeFormatterBuilder.DateTimePrinterParser {
      private final TemporalQuery<ZoneId> query;
      private final String description;
      private static volatile Map.Entry<Integer, DateTimeFormatterBuilder.PrefixTree> cachedPrefixTree;
      private static volatile Map.Entry<Integer, DateTimeFormatterBuilder.PrefixTree> cachedPrefixTreeCI;

      ZoneIdPrinterParser(TemporalQuery<ZoneId> var1, String var2) {
         this.query = var1;
         this.description = var2;
      }

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         ZoneId var3 = (ZoneId)var1.getValue(this.query);
         if (var3 == null) {
            return false;
         } else {
            var2.append(var3.getId());
            return true;
         }
      }

      protected DateTimeFormatterBuilder.PrefixTree getTree(DateTimeParseContext var1) {
         Set var2 = ZoneRulesProvider.getAvailableZoneIds();
         int var3 = var2.size();
         Object var4 = var1.isCaseSensitive() ? cachedPrefixTree : cachedPrefixTreeCI;
         if (var4 == null || (Integer)((Map.Entry)var4).getKey() != var3) {
            synchronized(this) {
               var4 = var1.isCaseSensitive() ? cachedPrefixTree : cachedPrefixTreeCI;
               if (var4 == null || (Integer)((Map.Entry)var4).getKey() != var3) {
                  var4 = new AbstractMap.SimpleImmutableEntry(var3, DateTimeFormatterBuilder.PrefixTree.newTree(var2, var1));
                  if (var1.isCaseSensitive()) {
                     cachedPrefixTree = (Map.Entry)var4;
                  } else {
                     cachedPrefixTreeCI = (Map.Entry)var4;
                  }
               }
            }
         }

         return (DateTimeFormatterBuilder.PrefixTree)((Map.Entry)var4).getValue();
      }

      public int parse(DateTimeParseContext var1, CharSequence var2, int var3) {
         int var4 = var2.length();
         if (var3 > var4) {
            throw new IndexOutOfBoundsException();
         } else if (var3 == var4) {
            return ~var3;
         } else {
            char var5 = var2.charAt(var3);
            if (var5 != '+' && var5 != '-') {
               if (var4 >= var3 + 2) {
                  char var6 = var2.charAt(var3 + 1);
                  if (var1.charEquals(var5, 'U') && var1.charEquals(var6, 'T')) {
                     if (var4 >= var3 + 3 && var1.charEquals(var2.charAt(var3 + 2), 'C')) {
                        return this.parseOffsetBased(var1, var2, var3, var3 + 3, DateTimeFormatterBuilder.OffsetIdPrinterParser.INSTANCE_ID_ZERO);
                     }

                     return this.parseOffsetBased(var1, var2, var3, var3 + 2, DateTimeFormatterBuilder.OffsetIdPrinterParser.INSTANCE_ID_ZERO);
                  }

                  if (var1.charEquals(var5, 'G') && var4 >= var3 + 3 && var1.charEquals(var6, 'M') && var1.charEquals(var2.charAt(var3 + 2), 'T')) {
                     return this.parseOffsetBased(var1, var2, var3, var3 + 3, DateTimeFormatterBuilder.OffsetIdPrinterParser.INSTANCE_ID_ZERO);
                  }
               }

               DateTimeFormatterBuilder.PrefixTree var9 = this.getTree(var1);
               ParsePosition var7 = new ParsePosition(var3);
               String var8 = var9.match(var2, var7);
               if (var8 == null) {
                  if (var1.charEquals(var5, 'Z')) {
                     var1.setParsed((ZoneId)ZoneOffset.UTC);
                     return var3 + 1;
                  } else {
                     return ~var3;
                  }
               } else {
                  var1.setParsed(ZoneId.of(var8));
                  return var7.getIndex();
               }
            } else {
               return this.parseOffsetBased(var1, var2, var3, var3, DateTimeFormatterBuilder.OffsetIdPrinterParser.INSTANCE_ID_Z);
            }
         }
      }

      private int parseOffsetBased(DateTimeParseContext var1, CharSequence var2, int var3, int var4, DateTimeFormatterBuilder.OffsetIdPrinterParser var5) {
         String var6 = var2.toString().substring(var3, var4).toUpperCase();
         if (var4 >= var2.length()) {
            var1.setParsed(ZoneId.of(var6));
            return var4;
         } else if (var2.charAt(var4) != '0' && !var1.charEquals(var2.charAt(var4), 'Z')) {
            DateTimeParseContext var7 = var1.copy();
            int var8 = var5.parse(var7, var2, var4);

            try {
               if (var8 < 0) {
                  if (var5 == DateTimeFormatterBuilder.OffsetIdPrinterParser.INSTANCE_ID_Z) {
                     return ~var3;
                  } else {
                     var1.setParsed(ZoneId.of(var6));
                     return var4;
                  }
               } else {
                  int var9 = (int)var7.getParsed(ChronoField.OFFSET_SECONDS);
                  ZoneOffset var10 = ZoneOffset.ofTotalSeconds(var9);
                  var1.setParsed(ZoneId.ofOffset(var6, var10));
                  return var8;
               }
            } catch (DateTimeException var11) {
               return ~var3;
            }
         } else {
            var1.setParsed(ZoneId.of(var6));
            return var4;
         }
      }

      public String toString() {
         return this.description;
      }
   }

   static final class ZoneTextPrinterParser extends DateTimeFormatterBuilder.ZoneIdPrinterParser {
      private final TextStyle textStyle;
      private Set<String> preferredZones;
      private static final int STD = 0;
      private static final int DST = 1;
      private static final int GENERIC = 2;
      private static final Map<String, SoftReference<Map<Locale, String[]>>> cache = new ConcurrentHashMap();
      private final Map<Locale, Map.Entry<Integer, SoftReference<DateTimeFormatterBuilder.PrefixTree>>> cachedTree = new HashMap();
      private final Map<Locale, Map.Entry<Integer, SoftReference<DateTimeFormatterBuilder.PrefixTree>>> cachedTreeCI = new HashMap();

      ZoneTextPrinterParser(TextStyle var1, Set<ZoneId> var2) {
         super(TemporalQueries.zone(), "ZoneText(" + var1 + ")");
         this.textStyle = (TextStyle)Objects.requireNonNull(var1, (String)"textStyle");
         if (var2 != null && var2.size() != 0) {
            this.preferredZones = new HashSet();
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
               ZoneId var4 = (ZoneId)var3.next();
               this.preferredZones.add(var4.getId());
            }
         }

      }

      private String getDisplayName(String var1, int var2, Locale var3) {
         if (this.textStyle == TextStyle.NARROW) {
            return null;
         } else {
            SoftReference var5 = (SoftReference)cache.get(var1);
            Object var6 = null;
            String[] var4;
            if (var5 == null || (var6 = (Map)var5.get()) == null || (var4 = (String[])((Map)var6).get(var3)) == null) {
               var4 = TimeZoneNameUtility.retrieveDisplayNames(var1, var3);
               if (var4 == null) {
                  return null;
               }

               var4 = (String[])Arrays.copyOfRange((Object[])var4, 0, 7);
               var4[5] = TimeZoneNameUtility.retrieveGenericDisplayName(var1, 1, var3);
               if (var4[5] == null) {
                  var4[5] = var4[0];
               }

               var4[6] = TimeZoneNameUtility.retrieveGenericDisplayName(var1, 0, var3);
               if (var4[6] == null) {
                  var4[6] = var4[0];
               }

               if (var6 == null) {
                  var6 = new ConcurrentHashMap();
               }

               ((Map)var6).put(var3, var4);
               cache.put(var1, new SoftReference(var6));
            }

            switch(var2) {
            case 0:
               return var4[this.textStyle.zoneNameStyleIndex() + 1];
            case 1:
               return var4[this.textStyle.zoneNameStyleIndex() + 3];
            default:
               return var4[this.textStyle.zoneNameStyleIndex() + 5];
            }
         }
      }

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         ZoneId var3 = (ZoneId)var1.getValue(TemporalQueries.zoneId());
         if (var3 == null) {
            return false;
         } else {
            String var4 = var3.getId();
            if (!(var3 instanceof ZoneOffset)) {
               TemporalAccessor var5 = var1.getTemporal();
               String var6 = this.getDisplayName(var4, var5.isSupported(ChronoField.INSTANT_SECONDS) ? (var3.getRules().isDaylightSavings(Instant.from(var5)) ? 1 : 0) : 2, var1.getLocale());
               if (var6 != null) {
                  var4 = var6;
               }
            }

            var2.append(var4);
            return true;
         }
      }

      protected DateTimeFormatterBuilder.PrefixTree getTree(DateTimeParseContext var1) {
         if (this.textStyle == TextStyle.NARROW) {
            return super.getTree(var1);
         } else {
            Locale var2 = var1.getLocale();
            boolean var3 = var1.isCaseSensitive();
            Set var4 = ZoneRulesProvider.getAvailableZoneIds();
            int var5 = var4.size();
            Map var6 = var3 ? this.cachedTree : this.cachedTreeCI;
            Map.Entry var7 = null;
            DateTimeFormatterBuilder.PrefixTree var8 = null;
            String[][] var9 = (String[][])null;
            if ((var7 = (Map.Entry)var6.get(var2)) == null || (Integer)var7.getKey() != var5 || (var8 = (DateTimeFormatterBuilder.PrefixTree)((SoftReference)var7.getValue()).get()) == null) {
               var8 = DateTimeFormatterBuilder.PrefixTree.newTree(var1);
               var9 = TimeZoneNameUtility.getZoneStrings(var2);
               String[][] var10 = var9;
               int var11 = var9.length;

               int var12;
               String[] var13;
               String var14;
               int var15;
               for(var12 = 0; var12 < var11; ++var12) {
                  var13 = var10[var12];
                  var14 = var13[0];
                  if (var4.contains(var14)) {
                     var8.add(var14, var14);
                     var14 = ZoneName.toZid(var14, var2);

                     for(var15 = this.textStyle == TextStyle.FULL ? 1 : 2; var15 < var13.length; var15 += 2) {
                        var8.add(var13[var15], var14);
                     }
                  }
               }

               if (this.preferredZones != null) {
                  var10 = var9;
                  var11 = var9.length;

                  for(var12 = 0; var12 < var11; ++var12) {
                     var13 = var10[var12];
                     var14 = var13[0];
                     if (this.preferredZones.contains(var14) && var4.contains(var14)) {
                        for(var15 = this.textStyle == TextStyle.FULL ? 1 : 2; var15 < var13.length; var15 += 2) {
                           var8.add(var13[var15], var14);
                        }
                     }
                  }
               }

               var6.put(var2, new AbstractMap.SimpleImmutableEntry(var5, new SoftReference(var8)));
            }

            return var8;
         }
      }
   }

   static final class LocalizedOffsetIdPrinterParser implements DateTimeFormatterBuilder.DateTimePrinterParser {
      private final TextStyle style;

      LocalizedOffsetIdPrinterParser(TextStyle var1) {
         this.style = var1;
      }

      private static StringBuilder appendHMS(StringBuilder var0, int var1) {
         return var0.append((char)(var1 / 10 + 48)).append((char)(var1 % 10 + 48));
      }

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         Long var3 = var1.getValue((TemporalField)ChronoField.OFFSET_SECONDS);
         if (var3 == null) {
            return false;
         } else {
            String var4 = "GMT";
            if (var4 != null) {
               var2.append(var4);
            }

            int var5 = Math.toIntExact(var3);
            if (var5 != 0) {
               int var6 = Math.abs(var5 / 3600 % 100);
               int var7 = Math.abs(var5 / 60 % 60);
               int var8 = Math.abs(var5 % 60);
               var2.append(var5 < 0 ? "-" : "+");
               if (this.style == TextStyle.FULL) {
                  appendHMS(var2, var6);
                  var2.append(':');
                  appendHMS(var2, var7);
                  if (var8 != 0) {
                     var2.append(':');
                     appendHMS(var2, var8);
                  }
               } else {
                  if (var6 >= 10) {
                     var2.append((char)(var6 / 10 + 48));
                  }

                  var2.append((char)(var6 % 10 + 48));
                  if (var7 != 0 || var8 != 0) {
                     var2.append(':');
                     appendHMS(var2, var7);
                     if (var8 != 0) {
                        var2.append(':');
                        appendHMS(var2, var8);
                     }
                  }
               }
            }

            return true;
         }
      }

      int getDigit(CharSequence var1, int var2) {
         char var3 = var1.charAt(var2);
         return var3 >= '0' && var3 <= '9' ? var3 - 48 : -1;
      }

      public int parse(DateTimeParseContext var1, CharSequence var2, int var3) {
         int var4 = var3;
         int var5 = var3 + var2.length();
         String var6 = "GMT";
         if (var6 != null) {
            if (!var1.subSequenceEquals(var2, var3, var6, 0, var6.length())) {
               return ~var3;
            }

            var4 = var3 + var6.length();
         }

         boolean var7 = false;
         if (var4 == var5) {
            return var1.setParsedField(ChronoField.OFFSET_SECONDS, 0L, var3, var4);
         } else {
            char var8 = var2.charAt(var4);
            byte var18;
            if (var8 == '+') {
               var18 = 1;
            } else {
               if (var8 != '-') {
                  return var1.setParsedField(ChronoField.OFFSET_SECONDS, 0L, var3, var4);
               }

               var18 = -1;
            }

            ++var4;
            boolean var9 = false;
            int var10 = 0;
            int var11 = 0;
            int var12;
            int var13;
            int var14;
            int var15;
            int var16;
            int var19;
            if (this.style == TextStyle.FULL) {
               var12 = this.getDigit(var2, var4++);
               var13 = this.getDigit(var2, var4++);
               if (var12 < 0 || var13 < 0 || var2.charAt(var4++) != ':') {
                  return ~var3;
               }

               var19 = var12 * 10 + var13;
               var14 = this.getDigit(var2, var4++);
               var15 = this.getDigit(var2, var4++);
               if (var14 < 0 || var15 < 0) {
                  return ~var3;
               }

               var10 = var14 * 10 + var15;
               if (var4 + 2 < var5 && var2.charAt(var4) == ':') {
                  var16 = this.getDigit(var2, var4 + 1);
                  int var17 = this.getDigit(var2, var4 + 2);
                  if (var16 >= 0 && var17 >= 0) {
                     var11 = var16 * 10 + var17;
                     var4 += 3;
                  }
               }
            } else {
               var19 = this.getDigit(var2, var4++);
               if (var19 < 0) {
                  return ~var3;
               }

               if (var4 < var5) {
                  var12 = this.getDigit(var2, var4);
                  if (var12 >= 0) {
                     var19 = var19 * 10 + var12;
                     ++var4;
                  }

                  if (var4 + 2 < var5 && var2.charAt(var4) == ':' && var4 + 2 < var5 && var2.charAt(var4) == ':') {
                     var13 = this.getDigit(var2, var4 + 1);
                     var14 = this.getDigit(var2, var4 + 2);
                     if (var13 >= 0 && var14 >= 0) {
                        var10 = var13 * 10 + var14;
                        var4 += 3;
                        if (var4 + 2 < var5 && var2.charAt(var4) == ':') {
                           var15 = this.getDigit(var2, var4 + 1);
                           var16 = this.getDigit(var2, var4 + 2);
                           if (var15 >= 0 && var16 >= 0) {
                              var11 = var15 * 10 + var16;
                              var4 += 3;
                           }
                        }
                     }
                  }
               }
            }

            long var20 = (long)var18 * ((long)var19 * 3600L + (long)var10 * 60L + (long)var11);
            return var1.setParsedField(ChronoField.OFFSET_SECONDS, var20, var3, var4);
         }
      }

      public String toString() {
         return "LocalizedOffset(" + this.style + ")";
      }
   }

   static final class OffsetIdPrinterParser implements DateTimeFormatterBuilder.DateTimePrinterParser {
      static final String[] PATTERNS = new String[]{"+HH", "+HHmm", "+HH:mm", "+HHMM", "+HH:MM", "+HHMMss", "+HH:MM:ss", "+HHMMSS", "+HH:MM:SS"};
      static final DateTimeFormatterBuilder.OffsetIdPrinterParser INSTANCE_ID_Z = new DateTimeFormatterBuilder.OffsetIdPrinterParser("+HH:MM:ss", "Z");
      static final DateTimeFormatterBuilder.OffsetIdPrinterParser INSTANCE_ID_ZERO = new DateTimeFormatterBuilder.OffsetIdPrinterParser("+HH:MM:ss", "0");
      private final String noOffsetText;
      private final int type;

      OffsetIdPrinterParser(String var1, String var2) {
         Objects.requireNonNull(var1, (String)"pattern");
         Objects.requireNonNull(var2, (String)"noOffsetText");
         this.type = this.checkPattern(var1);
         this.noOffsetText = var2;
      }

      private int checkPattern(String var1) {
         for(int var2 = 0; var2 < PATTERNS.length; ++var2) {
            if (PATTERNS[var2].equals(var1)) {
               return var2;
            }
         }

         throw new IllegalArgumentException("Invalid zone offset pattern: " + var1);
      }

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         Long var3 = var1.getValue((TemporalField)ChronoField.OFFSET_SECONDS);
         if (var3 == null) {
            return false;
         } else {
            int var4 = Math.toIntExact(var3);
            if (var4 == 0) {
               var2.append(this.noOffsetText);
            } else {
               int var5 = Math.abs(var4 / 3600 % 100);
               int var6 = Math.abs(var4 / 60 % 60);
               int var7 = Math.abs(var4 % 60);
               int var8 = var2.length();
               int var9 = var5;
               var2.append(var4 < 0 ? "-" : "+").append((char)(var5 / 10 + 48)).append((char)(var5 % 10 + 48));
               if (this.type >= 3 || this.type >= 1 && var6 > 0) {
                  var2.append(this.type % 2 == 0 ? ":" : "").append((char)(var6 / 10 + 48)).append((char)(var6 % 10 + 48));
                  var9 = var5 + var6;
                  if (this.type >= 7 || this.type >= 5 && var7 > 0) {
                     var2.append(this.type % 2 == 0 ? ":" : "").append((char)(var7 / 10 + 48)).append((char)(var7 % 10 + 48));
                     var9 += var7;
                  }
               }

               if (var9 == 0) {
                  var2.setLength(var8);
                  var2.append(this.noOffsetText);
               }
            }

            return true;
         }
      }

      public int parse(DateTimeParseContext var1, CharSequence var2, int var3) {
         int var4 = var2.length();
         int var5 = this.noOffsetText.length();
         if (var5 == 0) {
            if (var3 == var4) {
               return var1.setParsedField(ChronoField.OFFSET_SECONDS, 0L, var3, var3);
            }
         } else {
            if (var3 == var4) {
               return ~var3;
            }

            if (var1.subSequenceEquals(var2, var3, this.noOffsetText, 0, var5)) {
               return var1.setParsedField(ChronoField.OFFSET_SECONDS, 0L, var3, var3 + var5);
            }
         }

         char var6 = var2.charAt(var3);
         if (var6 == '+' || var6 == '-') {
            int var7 = var6 == '-' ? -1 : 1;
            int[] var8 = new int[4];
            var8[0] = var3 + 1;
            if (!this.parseNumber(var8, 1, var2, true) && !this.parseNumber(var8, 2, var2, this.type >= 3) && !this.parseNumber(var8, 3, var2, false)) {
               long var9 = (long)var7 * ((long)var8[1] * 3600L + (long)var8[2] * 60L + (long)var8[3]);
               return var1.setParsedField(ChronoField.OFFSET_SECONDS, var9, var3, var8[0]);
            }
         }

         return var5 == 0 ? var1.setParsedField(ChronoField.OFFSET_SECONDS, 0L, var3, var3 + var5) : ~var3;
      }

      private boolean parseNumber(int[] var1, int var2, CharSequence var3, boolean var4) {
         if ((this.type + 3) / 2 < var2) {
            return false;
         } else {
            int var5 = var1[0];
            if (this.type % 2 == 0 && var2 > 1) {
               if (var5 + 1 > var3.length() || var3.charAt(var5) != ':') {
                  return var4;
               }

               ++var5;
            }

            if (var5 + 2 > var3.length()) {
               return var4;
            } else {
               char var6 = var3.charAt(var5++);
               char var7 = var3.charAt(var5++);
               if (var6 >= '0' && var6 <= '9' && var7 >= '0' && var7 <= '9') {
                  int var8 = (var6 - 48) * 10 + (var7 - 48);
                  if (var8 >= 0 && var8 <= 59) {
                     var1[var2] = var8;
                     var1[0] = var5;
                     return false;
                  } else {
                     return var4;
                  }
               } else {
                  return var4;
               }
            }
         }
      }

      public String toString() {
         String var1 = this.noOffsetText.replace("'", "''");
         return "Offset(" + PATTERNS[this.type] + ",'" + var1 + "')";
      }
   }

   static final class InstantPrinterParser implements DateTimeFormatterBuilder.DateTimePrinterParser {
      private static final long SECONDS_PER_10000_YEARS = 315569520000L;
      private static final long SECONDS_0000_TO_1970 = 62167219200L;
      private final int fractionalDigits;

      InstantPrinterParser(int var1) {
         this.fractionalDigits = var1;
      }

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         Long var3 = var1.getValue((TemporalField)ChronoField.INSTANT_SECONDS);
         Long var4 = null;
         if (var1.getTemporal().isSupported(ChronoField.NANO_OF_SECOND)) {
            var4 = var1.getTemporal().getLong(ChronoField.NANO_OF_SECOND);
         }

         if (var3 == null) {
            return false;
         } else {
            long var5 = var3;
            int var7 = ChronoField.NANO_OF_SECOND.checkValidIntValue(var4 != null ? var4 : 0L);
            long var8;
            long var10;
            long var12;
            LocalDateTime var14;
            if (var5 >= -62167219200L) {
               var8 = var5 - 315569520000L + 62167219200L;
               var10 = Math.floorDiv(var8, 315569520000L) + 1L;
               var12 = Math.floorMod(var8, 315569520000L);
               var14 = LocalDateTime.ofEpochSecond(var12 - 62167219200L, 0, ZoneOffset.UTC);
               if (var10 > 0L) {
                  var2.append('+').append(var10);
               }

               var2.append((Object)var14);
               if (var14.getSecond() == 0) {
                  var2.append(":00");
               }
            } else {
               var8 = var5 + 62167219200L;
               var10 = var8 / 315569520000L;
               var12 = var8 % 315569520000L;
               var14 = LocalDateTime.ofEpochSecond(var12 - 62167219200L, 0, ZoneOffset.UTC);
               int var15 = var2.length();
               var2.append((Object)var14);
               if (var14.getSecond() == 0) {
                  var2.append(":00");
               }

               if (var10 < 0L) {
                  if (var14.getYear() == -10000) {
                     var2.replace(var15, var15 + 2, Long.toString(var10 - 1L));
                  } else if (var12 == 0L) {
                     var2.insert(var15, var10);
                  } else {
                     var2.insert(var15 + 1, Math.abs(var10));
                  }
               }
            }

            if (this.fractionalDigits < 0 && var7 > 0 || this.fractionalDigits > 0) {
               var2.append('.');
               int var16 = 100000000;

               for(int var9 = 0; this.fractionalDigits == -1 && var7 > 0 || this.fractionalDigits == -2 && (var7 > 0 || var9 % 3 != 0) || var9 < this.fractionalDigits; ++var9) {
                  int var17 = var7 / var16;
                  var2.append((char)(var17 + 48));
                  var7 -= var17 * var16;
                  var16 /= 10;
               }
            }

            var2.append('Z');
            return true;
         }
      }

      public int parse(DateTimeParseContext var1, CharSequence var2, int var3) {
         int var4 = this.fractionalDigits < 0 ? 0 : this.fractionalDigits;
         int var5 = this.fractionalDigits < 0 ? 9 : this.fractionalDigits;
         DateTimeFormatterBuilder.CompositePrinterParser var6 = (new DateTimeFormatterBuilder()).append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral('T').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).appendFraction(ChronoField.NANO_OF_SECOND, var4, var5, true).appendLiteral('Z').toFormatter().toPrinterParser(false);
         DateTimeParseContext var7 = var1.copy();
         int var8 = var6.parse(var7, var2, var3);
         if (var8 < 0) {
            return var8;
         } else {
            long var9 = var7.getParsed(ChronoField.YEAR);
            int var11 = var7.getParsed(ChronoField.MONTH_OF_YEAR).intValue();
            int var12 = var7.getParsed(ChronoField.DAY_OF_MONTH).intValue();
            int var13 = var7.getParsed(ChronoField.HOUR_OF_DAY).intValue();
            int var14 = var7.getParsed(ChronoField.MINUTE_OF_HOUR).intValue();
            Long var15 = var7.getParsed(ChronoField.SECOND_OF_MINUTE);
            Long var16 = var7.getParsed(ChronoField.NANO_OF_SECOND);
            int var17 = var15 != null ? var15.intValue() : 0;
            int var18 = var16 != null ? var16.intValue() : 0;
            byte var19 = 0;
            if (var13 == 24 && var14 == 0 && var17 == 0 && var18 == 0) {
               var13 = 0;
               var19 = 1;
            } else if (var13 == 23 && var14 == 59 && var17 == 60) {
               var1.setParsedLeapSecond();
               var17 = 59;
            }

            int var20 = (int)var9 % 10000;

            long var21;
            try {
               LocalDateTime var23 = LocalDateTime.of(var20, var11, var12, var13, var14, var17, 0).plusDays((long)var19);
               var21 = var23.toEpochSecond(ZoneOffset.UTC);
               var21 += Math.multiplyExact(var9 / 10000L, 315569520000L);
            } catch (RuntimeException var24) {
               return ~var3;
            }

            int var25 = var1.setParsedField(ChronoField.INSTANT_SECONDS, var21, var3, var8);
            return var1.setParsedField(ChronoField.NANO_OF_SECOND, (long)var18, var3, var25);
         }
      }

      public String toString() {
         return "Instant()";
      }
   }

   static final class TextPrinterParser implements DateTimeFormatterBuilder.DateTimePrinterParser {
      private final TemporalField field;
      private final TextStyle textStyle;
      private final DateTimeTextProvider provider;
      private volatile DateTimeFormatterBuilder.NumberPrinterParser numberPrinterParser;

      TextPrinterParser(TemporalField var1, TextStyle var2, DateTimeTextProvider var3) {
         this.field = var1;
         this.textStyle = var2;
         this.provider = var3;
      }

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         Long var3 = var1.getValue(this.field);
         if (var3 == null) {
            return false;
         } else {
            Chronology var5 = (Chronology)var1.getTemporal().query(TemporalQueries.chronology());
            String var4;
            if (var5 != null && var5 != IsoChronology.INSTANCE) {
               var4 = this.provider.getText(var5, this.field, var3, this.textStyle, var1.getLocale());
            } else {
               var4 = this.provider.getText(this.field, var3, this.textStyle, var1.getLocale());
            }

            if (var4 == null) {
               return this.numberPrinterParser().format(var1, var2);
            } else {
               var2.append(var4);
               return true;
            }
         }
      }

      public int parse(DateTimeParseContext var1, CharSequence var2, int var3) {
         int var4 = var2.length();
         if (var3 >= 0 && var3 <= var4) {
            TextStyle var5 = var1.isStrict() ? this.textStyle : null;
            Chronology var6 = var1.getEffectiveChronology();
            Iterator var7;
            if (var6 != null && var6 != IsoChronology.INSTANCE) {
               var7 = this.provider.getTextIterator(var6, this.field, var5, var1.getLocale());
            } else {
               var7 = this.provider.getTextIterator(this.field, var5, var1.getLocale());
            }

            if (var7 != null) {
               while(var7.hasNext()) {
                  Map.Entry var8 = (Map.Entry)var7.next();
                  String var9 = (String)var8.getKey();
                  if (var1.subSequenceEquals(var9, 0, var2, var3, var9.length())) {
                     return var1.setParsedField(this.field, (Long)var8.getValue(), var3, var3 + var9.length());
                  }
               }

               if (var1.isStrict()) {
                  return ~var3;
               }
            }

            return this.numberPrinterParser().parse(var1, var2, var3);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      private DateTimeFormatterBuilder.NumberPrinterParser numberPrinterParser() {
         if (this.numberPrinterParser == null) {
            this.numberPrinterParser = new DateTimeFormatterBuilder.NumberPrinterParser(this.field, 1, 19, SignStyle.NORMAL);
         }

         return this.numberPrinterParser;
      }

      public String toString() {
         return this.textStyle == TextStyle.FULL ? "Text(" + this.field + ")" : "Text(" + this.field + "," + this.textStyle + ")";
      }
   }

   static final class FractionPrinterParser implements DateTimeFormatterBuilder.DateTimePrinterParser {
      private final TemporalField field;
      private final int minWidth;
      private final int maxWidth;
      private final boolean decimalPoint;

      FractionPrinterParser(TemporalField var1, int var2, int var3, boolean var4) {
         Objects.requireNonNull(var1, (String)"field");
         if (!var1.range().isFixed()) {
            throw new IllegalArgumentException("Field must have a fixed set of values: " + var1);
         } else if (var2 >= 0 && var2 <= 9) {
            if (var3 >= 1 && var3 <= 9) {
               if (var3 < var2) {
                  throw new IllegalArgumentException("Maximum width must exceed or equal the minimum width but " + var3 + " < " + var2);
               } else {
                  this.field = var1;
                  this.minWidth = var2;
                  this.maxWidth = var3;
                  this.decimalPoint = var4;
               }
            } else {
               throw new IllegalArgumentException("Maximum width must be from 1 to 9 inclusive but was " + var3);
            }
         } else {
            throw new IllegalArgumentException("Minimum width must be from 0 to 9 inclusive but was " + var2);
         }
      }

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         Long var3 = var1.getValue(this.field);
         if (var3 == null) {
            return false;
         } else {
            DecimalStyle var4 = var1.getDecimalStyle();
            BigDecimal var5 = this.convertToFraction(var3);
            int var6;
            if (var5.scale() == 0) {
               if (this.minWidth > 0) {
                  if (this.decimalPoint) {
                     var2.append(var4.getDecimalSeparator());
                  }

                  for(var6 = 0; var6 < this.minWidth; ++var6) {
                     var2.append(var4.getZeroDigit());
                  }
               }
            } else {
               var6 = Math.min(Math.max(var5.scale(), this.minWidth), this.maxWidth);
               var5 = var5.setScale(var6, RoundingMode.FLOOR);
               String var7 = var5.toPlainString().substring(2);
               var7 = var4.convertNumberToI18N(var7);
               if (this.decimalPoint) {
                  var2.append(var4.getDecimalSeparator());
               }

               var2.append(var7);
            }

            return true;
         }
      }

      public int parse(DateTimeParseContext var1, CharSequence var2, int var3) {
         int var4 = var1.isStrict() ? this.minWidth : 0;
         int var5 = var1.isStrict() ? this.maxWidth : 9;
         int var6 = var2.length();
         if (var3 == var6) {
            return var4 > 0 ? ~var3 : var3;
         } else {
            if (this.decimalPoint) {
               if (var2.charAt(var3) != var1.getDecimalStyle().getDecimalSeparator()) {
                  return var4 > 0 ? ~var3 : var3;
               }

               ++var3;
            }

            int var7 = var3 + var4;
            if (var7 > var6) {
               return ~var3;
            } else {
               int var8 = Math.min(var3 + var5, var6);
               int var9 = 0;

               int var10;
               int var12;
               for(var10 = var3; var10 < var8; var9 = var9 * 10 + var12) {
                  char var11 = var2.charAt(var10++);
                  var12 = var1.getDecimalStyle().convertToDigit(var11);
                  if (var12 < 0) {
                     if (var10 < var7) {
                        return ~var3;
                     }

                     --var10;
                     break;
                  }
               }

               BigDecimal var14 = (new BigDecimal(var9)).movePointLeft(var10 - var3);
               long var15 = this.convertFromFraction(var14);
               return var1.setParsedField(this.field, var15, var3, var10);
            }
         }
      }

      private BigDecimal convertToFraction(long var1) {
         ValueRange var3 = this.field.range();
         var3.checkValidValue(var1, this.field);
         BigDecimal var4 = BigDecimal.valueOf(var3.getMinimum());
         BigDecimal var5 = BigDecimal.valueOf(var3.getMaximum()).subtract(var4).add(BigDecimal.ONE);
         BigDecimal var6 = BigDecimal.valueOf(var1).subtract(var4);
         BigDecimal var7 = var6.divide(var5, 9, RoundingMode.FLOOR);
         return var7.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : var7.stripTrailingZeros();
      }

      private long convertFromFraction(BigDecimal var1) {
         ValueRange var2 = this.field.range();
         BigDecimal var3 = BigDecimal.valueOf(var2.getMinimum());
         BigDecimal var4 = BigDecimal.valueOf(var2.getMaximum()).subtract(var3).add(BigDecimal.ONE);
         BigDecimal var5 = var1.multiply(var4).setScale(0, RoundingMode.FLOOR).add(var3);
         return var5.longValueExact();
      }

      public String toString() {
         String var1 = this.decimalPoint ? ",DecimalPoint" : "";
         return "Fraction(" + this.field + "," + this.minWidth + "," + this.maxWidth + var1 + ")";
      }
   }

   static final class ReducedPrinterParser extends DateTimeFormatterBuilder.NumberPrinterParser {
      static final LocalDate BASE_DATE = LocalDate.of(2000, 1, 1);
      private final int baseValue;
      private final ChronoLocalDate baseDate;

      ReducedPrinterParser(TemporalField var1, int var2, int var3, int var4, ChronoLocalDate var5) {
         this(var1, var2, var3, var4, var5, 0);
         if (var2 >= 1 && var2 <= 10) {
            if (var3 >= 1 && var3 <= 10) {
               if (var3 < var2) {
                  throw new IllegalArgumentException("Maximum width must exceed or equal the minimum width but " + var3 + " < " + var2);
               } else {
                  if (var5 == null) {
                     if (!var1.range().isValidValue((long)var4)) {
                        throw new IllegalArgumentException("The base value must be within the range of the field");
                     }

                     if ((long)var4 + EXCEED_POINTS[var3] > 2147483647L) {
                        throw new DateTimeException("Unable to add printer-parser as the range exceeds the capacity of an int");
                     }
                  }

               }
            } else {
               throw new IllegalArgumentException("The maxWidth must be from 1 to 10 inclusive but was " + var2);
            }
         } else {
            throw new IllegalArgumentException("The minWidth must be from 1 to 10 inclusive but was " + var2);
         }
      }

      private ReducedPrinterParser(TemporalField var1, int var2, int var3, int var4, ChronoLocalDate var5, int var6) {
         super(var1, var2, var3, SignStyle.NOT_NEGATIVE, var6);
         this.baseValue = var4;
         this.baseDate = var5;
      }

      long getValue(DateTimePrintContext var1, long var2) {
         long var4 = Math.abs(var2);
         int var6 = this.baseValue;
         if (this.baseDate != null) {
            Chronology var7 = Chronology.from(var1.getTemporal());
            var6 = var7.date(this.baseDate).get(this.field);
         }

         return var2 >= (long)var6 && var2 < (long)var6 + EXCEED_POINTS[this.minWidth] ? var4 % EXCEED_POINTS[this.minWidth] : var4 % EXCEED_POINTS[this.maxWidth];
      }

      int setValue(DateTimeParseContext var1, long var2, int var4, int var5) {
         int var6 = this.baseValue;
         if (this.baseDate != null) {
            Chronology var7 = var1.getEffectiveChronology();
            var6 = var7.date(this.baseDate).get(this.field);
            var1.addChronoChangedListener((var6x) -> {
               this.setValue(var1, var2, var4, var5);
            });
         }

         int var14 = var5 - var4;
         if (var14 == this.minWidth && var2 >= 0L) {
            long var8 = EXCEED_POINTS[this.minWidth];
            long var10 = (long)var6 % var8;
            long var12 = (long)var6 - var10;
            if (var6 > 0) {
               var2 += var12;
            } else {
               var2 = var12 - var2;
            }

            if (var2 < (long)var6) {
               var2 += var8;
            }
         }

         return var1.setParsedField(this.field, var2, var4, var5);
      }

      DateTimeFormatterBuilder.ReducedPrinterParser withFixedWidth() {
         return this.subsequentWidth == -1 ? this : new DateTimeFormatterBuilder.ReducedPrinterParser(this.field, this.minWidth, this.maxWidth, this.baseValue, this.baseDate, -1);
      }

      DateTimeFormatterBuilder.ReducedPrinterParser withSubsequentWidth(int var1) {
         return new DateTimeFormatterBuilder.ReducedPrinterParser(this.field, this.minWidth, this.maxWidth, this.baseValue, this.baseDate, this.subsequentWidth + var1);
      }

      boolean isFixedWidth(DateTimeParseContext var1) {
         return !var1.isStrict() ? false : super.isFixedWidth(var1);
      }

      public String toString() {
         return "ReducedValue(" + this.field + "," + this.minWidth + "," + this.maxWidth + "," + (this.baseDate != null ? this.baseDate : this.baseValue) + ")";
      }

      // $FF: synthetic method
      ReducedPrinterParser(TemporalField var1, int var2, int var3, int var4, ChronoLocalDate var5, int var6, Object var7) {
         this(var1, var2, var3, var4, var5, var6);
      }
   }

   static class NumberPrinterParser implements DateTimeFormatterBuilder.DateTimePrinterParser {
      static final long[] EXCEED_POINTS = new long[]{0L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L};
      final TemporalField field;
      final int minWidth;
      final int maxWidth;
      private final SignStyle signStyle;
      final int subsequentWidth;

      NumberPrinterParser(TemporalField var1, int var2, int var3, SignStyle var4) {
         this.field = var1;
         this.minWidth = var2;
         this.maxWidth = var3;
         this.signStyle = var4;
         this.subsequentWidth = 0;
      }

      protected NumberPrinterParser(TemporalField var1, int var2, int var3, SignStyle var4, int var5) {
         this.field = var1;
         this.minWidth = var2;
         this.maxWidth = var3;
         this.signStyle = var4;
         this.subsequentWidth = var5;
      }

      DateTimeFormatterBuilder.NumberPrinterParser withFixedWidth() {
         return this.subsequentWidth == -1 ? this : new DateTimeFormatterBuilder.NumberPrinterParser(this.field, this.minWidth, this.maxWidth, this.signStyle, -1);
      }

      DateTimeFormatterBuilder.NumberPrinterParser withSubsequentWidth(int var1) {
         return new DateTimeFormatterBuilder.NumberPrinterParser(this.field, this.minWidth, this.maxWidth, this.signStyle, this.subsequentWidth + var1);
      }

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         Long var3 = var1.getValue(this.field);
         if (var3 == null) {
            return false;
         } else {
            long var4 = this.getValue(var1, var3);
            DecimalStyle var6 = var1.getDecimalStyle();
            String var7 = var4 == Long.MIN_VALUE ? "9223372036854775808" : Long.toString(Math.abs(var4));
            if (var7.length() > this.maxWidth) {
               throw new DateTimeException("Field " + this.field + " cannot be printed as the value " + var4 + " exceeds the maximum print width of " + this.maxWidth);
            } else {
               var7 = var6.convertNumberToI18N(var7);
               if (var4 >= 0L) {
                  switch(this.signStyle) {
                  case EXCEEDS_PAD:
                     if (this.minWidth < 19 && var4 >= EXCEED_POINTS[this.minWidth]) {
                        var2.append(var6.getPositiveSign());
                     }
                     break;
                  case ALWAYS:
                     var2.append(var6.getPositiveSign());
                  }
               } else {
                  switch(this.signStyle) {
                  case EXCEEDS_PAD:
                  case ALWAYS:
                  case NORMAL:
                     var2.append(var6.getNegativeSign());
                     break;
                  case NOT_NEGATIVE:
                     throw new DateTimeException("Field " + this.field + " cannot be printed as the value " + var4 + " cannot be negative according to the SignStyle");
                  }
               }

               for(int var8 = 0; var8 < this.minWidth - var7.length(); ++var8) {
                  var2.append(var6.getZeroDigit());
               }

               var2.append(var7);
               return true;
            }
         }
      }

      long getValue(DateTimePrintContext var1, long var2) {
         return var2;
      }

      boolean isFixedWidth(DateTimeParseContext var1) {
         return this.subsequentWidth == -1 || this.subsequentWidth > 0 && this.minWidth == this.maxWidth && this.signStyle == SignStyle.NOT_NEGATIVE;
      }

      public int parse(DateTimeParseContext var1, CharSequence var2, int var3) {
         int var4 = var2.length();
         if (var3 == var4) {
            return ~var3;
         } else {
            char var5 = var2.charAt(var3);
            boolean var6 = false;
            boolean var7 = false;
            if (var5 == var1.getDecimalStyle().getPositiveSign()) {
               if (!this.signStyle.parse(true, var1.isStrict(), this.minWidth == this.maxWidth)) {
                  return ~var3;
               }

               var7 = true;
               ++var3;
            } else if (var5 == var1.getDecimalStyle().getNegativeSign()) {
               if (!this.signStyle.parse(false, var1.isStrict(), this.minWidth == this.maxWidth)) {
                  return ~var3;
               }

               var6 = true;
               ++var3;
            } else if (this.signStyle == SignStyle.ALWAYS && var1.isStrict()) {
               return ~var3;
            }

            int var8 = !var1.isStrict() && !this.isFixedWidth(var1) ? 1 : this.minWidth;
            int var9 = var3 + var8;
            if (var9 > var4) {
               return ~var3;
            } else {
               int var10 = (!var1.isStrict() && !this.isFixedWidth(var1) ? 9 : this.maxWidth) + Math.max(this.subsequentWidth, 0);
               long var11 = 0L;
               BigInteger var13 = null;
               int var14 = var3;

               int var15;
               for(var15 = 0; var15 < 2; ++var15) {
                  int var16 = Math.min(var14 + var10, var4);

                  while(var14 < var16) {
                     char var17 = var2.charAt(var14++);
                     int var18 = var1.getDecimalStyle().convertToDigit(var17);
                     if (var18 < 0) {
                        --var14;
                        if (var14 < var9) {
                           return ~var3;
                        }
                        break;
                     }

                     if (var14 - var3 > 18) {
                        if (var13 == null) {
                           var13 = BigInteger.valueOf(var11);
                        }

                        var13 = var13.multiply(BigInteger.TEN).add(BigInteger.valueOf((long)var18));
                     } else {
                        var11 = var11 * 10L + (long)var18;
                     }
                  }

                  if (this.subsequentWidth <= 0 || var15 != 0) {
                     break;
                  }

                  int var19 = var14 - var3;
                  var10 = Math.max(var8, var19 - this.subsequentWidth);
                  var14 = var3;
                  var11 = 0L;
                  var13 = null;
               }

               if (var6) {
                  if (var13 != null) {
                     if (var13.equals(BigInteger.ZERO) && var1.isStrict()) {
                        return ~(var3 - 1);
                     }

                     var13 = var13.negate();
                  } else {
                     if (var11 == 0L && var1.isStrict()) {
                        return ~(var3 - 1);
                     }

                     var11 = -var11;
                  }
               } else if (this.signStyle == SignStyle.EXCEEDS_PAD && var1.isStrict()) {
                  var15 = var14 - var3;
                  if (var7) {
                     if (var15 <= this.minWidth) {
                        return ~(var3 - 1);
                     }
                  } else if (var15 > this.minWidth) {
                     return ~var3;
                  }
               }

               if (var13 != null) {
                  if (var13.bitLength() > 63) {
                     var13 = var13.divide(BigInteger.TEN);
                     --var14;
                  }

                  return this.setValue(var1, var13.longValue(), var3, var14);
               } else {
                  return this.setValue(var1, var11, var3, var14);
               }
            }
         }
      }

      int setValue(DateTimeParseContext var1, long var2, int var4, int var5) {
         return var1.setParsedField(this.field, var2, var4, var5);
      }

      public String toString() {
         if (this.minWidth == 1 && this.maxWidth == 19 && this.signStyle == SignStyle.NORMAL) {
            return "Value(" + this.field + ")";
         } else {
            return this.minWidth == this.maxWidth && this.signStyle == SignStyle.NOT_NEGATIVE ? "Value(" + this.field + "," + this.minWidth + ")" : "Value(" + this.field + "," + this.minWidth + "," + this.maxWidth + "," + this.signStyle + ")";
         }
      }
   }

   static final class StringLiteralPrinterParser implements DateTimeFormatterBuilder.DateTimePrinterParser {
      private final String literal;

      StringLiteralPrinterParser(String var1) {
         this.literal = var1;
      }

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         var2.append(this.literal);
         return true;
      }

      public int parse(DateTimeParseContext var1, CharSequence var2, int var3) {
         int var4 = var2.length();
         if (var3 <= var4 && var3 >= 0) {
            return !var1.subSequenceEquals(var2, var3, this.literal, 0, this.literal.length()) ? ~var3 : var3 + this.literal.length();
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public String toString() {
         String var1 = this.literal.replace("'", "''");
         return "'" + var1 + "'";
      }
   }

   static final class CharLiteralPrinterParser implements DateTimeFormatterBuilder.DateTimePrinterParser {
      private final char literal;

      CharLiteralPrinterParser(char var1) {
         this.literal = var1;
      }

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         var2.append(this.literal);
         return true;
      }

      public int parse(DateTimeParseContext var1, CharSequence var2, int var3) {
         int var4 = var2.length();
         if (var3 == var4) {
            return ~var3;
         } else {
            char var5 = var2.charAt(var3);
            return var5 == this.literal || !var1.isCaseSensitive() && (Character.toUpperCase(var5) == Character.toUpperCase(this.literal) || Character.toLowerCase(var5) == Character.toLowerCase(this.literal)) ? var3 + 1 : ~var3;
         }
      }

      public String toString() {
         return this.literal == '\'' ? "''" : "'" + this.literal + "'";
      }
   }

   static class DefaultValueParser implements DateTimeFormatterBuilder.DateTimePrinterParser {
      private final TemporalField field;
      private final long value;

      DefaultValueParser(TemporalField var1, long var2) {
         this.field = var1;
         this.value = var2;
      }

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         return true;
      }

      public int parse(DateTimeParseContext var1, CharSequence var2, int var3) {
         if (var1.getParsed(this.field) == null) {
            var1.setParsedField(this.field, this.value, var3, var3);
         }

         return var3;
      }
   }

   static enum SettingsParser implements DateTimeFormatterBuilder.DateTimePrinterParser {
      SENSITIVE,
      INSENSITIVE,
      STRICT,
      LENIENT;

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         return true;
      }

      public int parse(DateTimeParseContext var1, CharSequence var2, int var3) {
         switch(this.ordinal()) {
         case 0:
            var1.setCaseSensitive(true);
            break;
         case 1:
            var1.setCaseSensitive(false);
            break;
         case 2:
            var1.setStrict(true);
            break;
         case 3:
            var1.setStrict(false);
         }

         return var3;
      }

      public String toString() {
         switch(this.ordinal()) {
         case 0:
            return "ParseCaseSensitive(true)";
         case 1:
            return "ParseCaseSensitive(false)";
         case 2:
            return "ParseStrict(true)";
         case 3:
            return "ParseStrict(false)";
         default:
            throw new IllegalStateException("Unreachable");
         }
      }
   }

   static final class PadPrinterParserDecorator implements DateTimeFormatterBuilder.DateTimePrinterParser {
      private final DateTimeFormatterBuilder.DateTimePrinterParser printerParser;
      private final int padWidth;
      private final char padChar;

      PadPrinterParserDecorator(DateTimeFormatterBuilder.DateTimePrinterParser var1, int var2, char var3) {
         this.printerParser = var1;
         this.padWidth = var2;
         this.padChar = var3;
      }

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         int var3 = var2.length();
         if (!this.printerParser.format(var1, var2)) {
            return false;
         } else {
            int var4 = var2.length() - var3;
            if (var4 > this.padWidth) {
               throw new DateTimeException("Cannot print as output of " + var4 + " characters exceeds pad width of " + this.padWidth);
            } else {
               for(int var5 = 0; var5 < this.padWidth - var4; ++var5) {
                  var2.insert(var3, this.padChar);
               }

               return true;
            }
         }
      }

      public int parse(DateTimeParseContext var1, CharSequence var2, int var3) {
         boolean var4 = var1.isStrict();
         if (var3 > var2.length()) {
            throw new IndexOutOfBoundsException();
         } else if (var3 == var2.length()) {
            return ~var3;
         } else {
            int var5 = var3 + this.padWidth;
            if (var5 > var2.length()) {
               if (var4) {
                  return ~var3;
               }

               var5 = var2.length();
            }

            int var6;
            for(var6 = var3; var6 < var5 && var1.charEquals(var2.charAt(var6), this.padChar); ++var6) {
            }

            var2 = var2.subSequence(0, var5);
            int var7 = this.printerParser.parse(var1, var2, var6);
            return var7 != var5 && var4 ? ~(var3 + var6) : var7;
         }
      }

      public String toString() {
         return "Pad(" + this.printerParser + "," + this.padWidth + (this.padChar == ' ' ? ")" : ",'" + this.padChar + "')");
      }
   }

   static final class CompositePrinterParser implements DateTimeFormatterBuilder.DateTimePrinterParser {
      private final DateTimeFormatterBuilder.DateTimePrinterParser[] printerParsers;
      private final boolean optional;

      CompositePrinterParser(List<DateTimeFormatterBuilder.DateTimePrinterParser> var1, boolean var2) {
         this((DateTimeFormatterBuilder.DateTimePrinterParser[])var1.toArray(new DateTimeFormatterBuilder.DateTimePrinterParser[var1.size()]), var2);
      }

      CompositePrinterParser(DateTimeFormatterBuilder.DateTimePrinterParser[] var1, boolean var2) {
         this.printerParsers = var1;
         this.optional = var2;
      }

      public DateTimeFormatterBuilder.CompositePrinterParser withOptional(boolean var1) {
         return var1 == this.optional ? this : new DateTimeFormatterBuilder.CompositePrinterParser(this.printerParsers, var1);
      }

      public boolean format(DateTimePrintContext var1, StringBuilder var2) {
         int var3 = var2.length();
         if (this.optional) {
            var1.startOptional();
         }

         try {
            DateTimeFormatterBuilder.DateTimePrinterParser[] var4 = this.printerParsers;
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               DateTimeFormatterBuilder.DateTimePrinterParser var7 = var4[var6];
               if (!var7.format(var1, var2)) {
                  var2.setLength(var3);
                  boolean var8 = true;
                  return var8;
               }
            }
         } finally {
            if (this.optional) {
               var1.endOptional();
            }

         }

         return true;
      }

      public int parse(DateTimeParseContext var1, CharSequence var2, int var3) {
         int var6;
         if (this.optional) {
            var1.startOptional();
            int var9 = var3;
            DateTimeFormatterBuilder.DateTimePrinterParser[] var10 = this.printerParsers;
            var6 = var10.length;

            for(int var11 = 0; var11 < var6; ++var11) {
               DateTimeFormatterBuilder.DateTimePrinterParser var8 = var10[var11];
               var9 = var8.parse(var1, var2, var9);
               if (var9 < 0) {
                  var1.endOptional(false);
                  return var3;
               }
            }

            var1.endOptional(true);
            return var9;
         } else {
            DateTimeFormatterBuilder.DateTimePrinterParser[] var4 = this.printerParsers;
            int var5 = var4.length;

            for(var6 = 0; var6 < var5; ++var6) {
               DateTimeFormatterBuilder.DateTimePrinterParser var7 = var4[var6];
               var3 = var7.parse(var1, var2, var3);
               if (var3 < 0) {
                  break;
               }
            }

            return var3;
         }
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         if (this.printerParsers != null) {
            var1.append(this.optional ? "[" : "(");
            DateTimeFormatterBuilder.DateTimePrinterParser[] var2 = this.printerParsers;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               DateTimeFormatterBuilder.DateTimePrinterParser var5 = var2[var4];
               var1.append((Object)var5);
            }

            var1.append(this.optional ? "]" : ")");
         }

         return var1.toString();
      }
   }

   interface DateTimePrinterParser {
      boolean format(DateTimePrintContext var1, StringBuilder var2);

      int parse(DateTimeParseContext var1, CharSequence var2, int var3);
   }
}
