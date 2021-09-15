package java.util;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.misc.FormattedFloatingDecimal;

public final class Formatter implements Closeable, Flushable {
   private Appendable a;
   private final Locale l;
   private IOException lastException;
   private final char zero;
   private static double scaleUp;
   private static final int MAX_FD_CHARS = 30;
   private static final String formatSpecifier = "%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])";
   private static Pattern fsPattern = Pattern.compile("%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])");

   private static Charset toCharset(String var0) throws UnsupportedEncodingException {
      Objects.requireNonNull(var0, (String)"charsetName");

      try {
         return Charset.forName(var0);
      } catch (UnsupportedCharsetException | IllegalCharsetNameException var2) {
         throw new UnsupportedEncodingException(var0);
      }
   }

   private static final Appendable nonNullAppendable(Appendable var0) {
      return (Appendable)(var0 == null ? new StringBuilder() : var0);
   }

   private Formatter(Locale var1, Appendable var2) {
      this.a = var2;
      this.l = var1;
      this.zero = getZero(var1);
   }

   private Formatter(Charset var1, Locale var2, File var3) throws FileNotFoundException {
      this((Locale)var2, (Appendable)(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(var3), var1))));
   }

   public Formatter() {
      this((Locale)Locale.getDefault(Locale.Category.FORMAT), (Appendable)(new StringBuilder()));
   }

   public Formatter(Appendable var1) {
      this(Locale.getDefault(Locale.Category.FORMAT), nonNullAppendable(var1));
   }

   public Formatter(Locale var1) {
      this((Locale)var1, (Appendable)(new StringBuilder()));
   }

   public Formatter(Appendable var1, Locale var2) {
      this(var2, nonNullAppendable(var1));
   }

   public Formatter(String var1) throws FileNotFoundException {
      this((Locale)Locale.getDefault(Locale.Category.FORMAT), (Appendable)(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(var1)))));
   }

   public Formatter(String var1, String var2) throws FileNotFoundException, UnsupportedEncodingException {
      this(var1, var2, Locale.getDefault(Locale.Category.FORMAT));
   }

   public Formatter(String var1, String var2, Locale var3) throws FileNotFoundException, UnsupportedEncodingException {
      this(toCharset(var2), var3, new File(var1));
   }

   public Formatter(File var1) throws FileNotFoundException {
      this((Locale)Locale.getDefault(Locale.Category.FORMAT), (Appendable)(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(var1)))));
   }

   public Formatter(File var1, String var2) throws FileNotFoundException, UnsupportedEncodingException {
      this(var1, var2, Locale.getDefault(Locale.Category.FORMAT));
   }

   public Formatter(File var1, String var2, Locale var3) throws FileNotFoundException, UnsupportedEncodingException {
      this(toCharset(var2), var3, var1);
   }

   public Formatter(PrintStream var1) {
      this(Locale.getDefault(Locale.Category.FORMAT), (Appendable)Objects.requireNonNull(var1));
   }

   public Formatter(OutputStream var1) {
      this((Locale)Locale.getDefault(Locale.Category.FORMAT), (Appendable)(new BufferedWriter(new OutputStreamWriter(var1))));
   }

   public Formatter(OutputStream var1, String var2) throws UnsupportedEncodingException {
      this(var1, var2, Locale.getDefault(Locale.Category.FORMAT));
   }

   public Formatter(OutputStream var1, String var2, Locale var3) throws UnsupportedEncodingException {
      this((Locale)var3, (Appendable)(new BufferedWriter(new OutputStreamWriter(var1, var2))));
   }

   private static char getZero(Locale var0) {
      if (var0 != null && !var0.equals(Locale.US)) {
         DecimalFormatSymbols var1 = DecimalFormatSymbols.getInstance(var0);
         return var1.getZeroDigit();
      } else {
         return '0';
      }
   }

   public Locale locale() {
      this.ensureOpen();
      return this.l;
   }

   public Appendable out() {
      this.ensureOpen();
      return this.a;
   }

   public String toString() {
      this.ensureOpen();
      return this.a.toString();
   }

   public void flush() {
      this.ensureOpen();
      if (this.a instanceof Flushable) {
         try {
            ((Flushable)this.a).flush();
         } catch (IOException var2) {
            this.lastException = var2;
         }
      }

   }

   public void close() {
      if (this.a != null) {
         try {
            if (this.a instanceof Closeable) {
               ((Closeable)this.a).close();
            }
         } catch (IOException var5) {
            this.lastException = var5;
         } finally {
            this.a = null;
         }

      }
   }

   private void ensureOpen() {
      if (this.a == null) {
         throw new FormatterClosedException();
      }
   }

   public IOException ioException() {
      return this.lastException;
   }

   public Formatter format(String var1, Object... var2) {
      return this.format(this.l, var1, var2);
   }

   public Formatter format(Locale var1, String var2, Object... var3) {
      this.ensureOpen();
      int var4 = -1;
      int var5 = -1;
      Formatter.FormatString[] var6 = this.parse(var2);

      for(int var7 = 0; var7 < var6.length; ++var7) {
         Formatter.FormatString var8 = var6[var7];
         int var9 = var8.index();

         try {
            switch(var9) {
            case -2:
               var8.print((Object)null, var1);
               break;
            case -1:
               if (var4 < 0 || var3 != null && var4 > var3.length - 1) {
                  throw new MissingFormatArgumentException(var8.toString());
               }

               var8.print(var3 == null ? null : var3[var4], var1);
               break;
            case 0:
               ++var5;
               var4 = var5;
               if (var3 != null && var5 > var3.length - 1) {
                  throw new MissingFormatArgumentException(var8.toString());
               }

               var8.print(var3 == null ? null : var3[var5], var1);
               break;
            default:
               var4 = var9 - 1;
               if (var3 != null && var4 > var3.length - 1) {
                  throw new MissingFormatArgumentException(var8.toString());
               }

               var8.print(var3 == null ? null : var3[var4], var1);
            }
         } catch (IOException var11) {
            this.lastException = var11;
         }
      }

      return this;
   }

   private Formatter.FormatString[] parse(String var1) {
      ArrayList var2 = new ArrayList();
      Matcher var3 = fsPattern.matcher(var1);
      int var4 = 0;

      for(int var5 = var1.length(); var4 < var5; var4 = var3.end()) {
         if (!var3.find(var4)) {
            checkText(var1, var4, var5);
            var2.add(new Formatter.FixedString(var1.substring(var4)));
            break;
         }

         if (var3.start() != var4) {
            checkText(var1, var4, var3.start());
            var2.add(new Formatter.FixedString(var1.substring(var4, var3.start())));
         }

         var2.add(new Formatter.FormatSpecifier(var3));
      }

      return (Formatter.FormatString[])var2.toArray(new Formatter.FormatString[var2.size()]);
   }

   private static void checkText(String var0, int var1, int var2) {
      for(int var3 = var1; var3 < var2; ++var3) {
         if (var0.charAt(var3) == '%') {
            char var4 = var3 == var2 - 1 ? 37 : var0.charAt(var3 + 1);
            throw new UnknownFormatConversionException(String.valueOf(var4));
         }
      }

   }

   private static class DateTime {
      static final char HOUR_OF_DAY_0 = 'H';
      static final char HOUR_0 = 'I';
      static final char HOUR_OF_DAY = 'k';
      static final char HOUR = 'l';
      static final char MINUTE = 'M';
      static final char NANOSECOND = 'N';
      static final char MILLISECOND = 'L';
      static final char MILLISECOND_SINCE_EPOCH = 'Q';
      static final char AM_PM = 'p';
      static final char SECONDS_SINCE_EPOCH = 's';
      static final char SECOND = 'S';
      static final char TIME = 'T';
      static final char ZONE_NUMERIC = 'z';
      static final char ZONE = 'Z';
      static final char NAME_OF_DAY_ABBREV = 'a';
      static final char NAME_OF_DAY = 'A';
      static final char NAME_OF_MONTH_ABBREV = 'b';
      static final char NAME_OF_MONTH = 'B';
      static final char CENTURY = 'C';
      static final char DAY_OF_MONTH_0 = 'd';
      static final char DAY_OF_MONTH = 'e';
      static final char NAME_OF_MONTH_ABBREV_X = 'h';
      static final char DAY_OF_YEAR = 'j';
      static final char MONTH = 'm';
      static final char YEAR_2 = 'y';
      static final char YEAR_4 = 'Y';
      static final char TIME_12_HOUR = 'r';
      static final char TIME_24_HOUR = 'R';
      static final char DATE_TIME = 'c';
      static final char DATE = 'D';
      static final char ISO_STANDARD_DATE = 'F';

      static boolean isValid(char var0) {
         switch(var0) {
         case 'A':
         case 'B':
         case 'C':
         case 'D':
         case 'F':
         case 'H':
         case 'I':
         case 'L':
         case 'M':
         case 'N':
         case 'Q':
         case 'R':
         case 'S':
         case 'T':
         case 'Y':
         case 'Z':
         case 'a':
         case 'b':
         case 'c':
         case 'd':
         case 'e':
         case 'h':
         case 'j':
         case 'k':
         case 'l':
         case 'm':
         case 'p':
         case 'r':
         case 's':
         case 'y':
         case 'z':
            return true;
         case 'E':
         case 'G':
         case 'J':
         case 'K':
         case 'O':
         case 'P':
         case 'U':
         case 'V':
         case 'W':
         case 'X':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '_':
         case '`':
         case 'f':
         case 'g':
         case 'i':
         case 'n':
         case 'o':
         case 'q':
         case 't':
         case 'u':
         case 'v':
         case 'w':
         case 'x':
         default:
            return false;
         }
      }
   }

   private static class Conversion {
      static final char DECIMAL_INTEGER = 'd';
      static final char OCTAL_INTEGER = 'o';
      static final char HEXADECIMAL_INTEGER = 'x';
      static final char HEXADECIMAL_INTEGER_UPPER = 'X';
      static final char SCIENTIFIC = 'e';
      static final char SCIENTIFIC_UPPER = 'E';
      static final char GENERAL = 'g';
      static final char GENERAL_UPPER = 'G';
      static final char DECIMAL_FLOAT = 'f';
      static final char HEXADECIMAL_FLOAT = 'a';
      static final char HEXADECIMAL_FLOAT_UPPER = 'A';
      static final char CHARACTER = 'c';
      static final char CHARACTER_UPPER = 'C';
      static final char DATE_TIME = 't';
      static final char DATE_TIME_UPPER = 'T';
      static final char BOOLEAN = 'b';
      static final char BOOLEAN_UPPER = 'B';
      static final char STRING = 's';
      static final char STRING_UPPER = 'S';
      static final char HASHCODE = 'h';
      static final char HASHCODE_UPPER = 'H';
      static final char LINE_SEPARATOR = 'n';
      static final char PERCENT_SIGN = '%';

      static boolean isValid(char var0) {
         return isGeneral(var0) || isInteger(var0) || isFloat(var0) || isText(var0) || var0 == 't' || isCharacter(var0);
      }

      static boolean isGeneral(char var0) {
         switch(var0) {
         case 'B':
         case 'H':
         case 'S':
         case 'b':
         case 'h':
         case 's':
            return true;
         default:
            return false;
         }
      }

      static boolean isCharacter(char var0) {
         switch(var0) {
         case 'C':
         case 'c':
            return true;
         default:
            return false;
         }
      }

      static boolean isInteger(char var0) {
         switch(var0) {
         case 'X':
         case 'd':
         case 'o':
         case 'x':
            return true;
         default:
            return false;
         }
      }

      static boolean isFloat(char var0) {
         switch(var0) {
         case 'A':
         case 'E':
         case 'G':
         case 'a':
         case 'e':
         case 'f':
         case 'g':
            return true;
         default:
            return false;
         }
      }

      static boolean isText(char var0) {
         switch(var0) {
         case '%':
         case 'n':
            return true;
         default:
            return false;
         }
      }
   }

   private static class Flags {
      private int flags;
      static final Formatter.Flags NONE = new Formatter.Flags(0);
      static final Formatter.Flags LEFT_JUSTIFY = new Formatter.Flags(1);
      static final Formatter.Flags UPPERCASE = new Formatter.Flags(2);
      static final Formatter.Flags ALTERNATE = new Formatter.Flags(4);
      static final Formatter.Flags PLUS = new Formatter.Flags(8);
      static final Formatter.Flags LEADING_SPACE = new Formatter.Flags(16);
      static final Formatter.Flags ZERO_PAD = new Formatter.Flags(32);
      static final Formatter.Flags GROUP = new Formatter.Flags(64);
      static final Formatter.Flags PARENTHESES = new Formatter.Flags(128);
      static final Formatter.Flags PREVIOUS = new Formatter.Flags(256);

      private Flags(int var1) {
         this.flags = var1;
      }

      public int valueOf() {
         return this.flags;
      }

      public boolean contains(Formatter.Flags var1) {
         return (this.flags & var1.valueOf()) == var1.valueOf();
      }

      public Formatter.Flags dup() {
         return new Formatter.Flags(this.flags);
      }

      private Formatter.Flags add(Formatter.Flags var1) {
         this.flags |= var1.valueOf();
         return this;
      }

      public Formatter.Flags remove(Formatter.Flags var1) {
         this.flags &= ~var1.valueOf();
         return this;
      }

      public static Formatter.Flags parse(String var0) {
         char[] var1 = var0.toCharArray();
         Formatter.Flags var2 = new Formatter.Flags(0);

         for(int var3 = 0; var3 < var1.length; ++var3) {
            Formatter.Flags var4 = parse(var1[var3]);
            if (var2.contains(var4)) {
               throw new DuplicateFormatFlagsException(var4.toString());
            }

            var2.add(var4);
         }

         return var2;
      }

      private static Formatter.Flags parse(char var0) {
         switch(var0) {
         case ' ':
            return LEADING_SPACE;
         case '!':
         case '"':
         case '$':
         case '%':
         case '&':
         case '\'':
         case ')':
         case '*':
         case '.':
         case '/':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
         case ':':
         case ';':
         default:
            throw new UnknownFormatFlagsException(String.valueOf(var0));
         case '#':
            return ALTERNATE;
         case '(':
            return PARENTHESES;
         case '+':
            return PLUS;
         case ',':
            return GROUP;
         case '-':
            return LEFT_JUSTIFY;
         case '0':
            return ZERO_PAD;
         case '<':
            return PREVIOUS;
         }
      }

      public static String toString(Formatter.Flags var0) {
         return var0.toString();
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         if (this.contains(LEFT_JUSTIFY)) {
            var1.append('-');
         }

         if (this.contains(UPPERCASE)) {
            var1.append('^');
         }

         if (this.contains(ALTERNATE)) {
            var1.append('#');
         }

         if (this.contains(PLUS)) {
            var1.append('+');
         }

         if (this.contains(LEADING_SPACE)) {
            var1.append(' ');
         }

         if (this.contains(ZERO_PAD)) {
            var1.append('0');
         }

         if (this.contains(GROUP)) {
            var1.append(',');
         }

         if (this.contains(PARENTHESES)) {
            var1.append('(');
         }

         if (this.contains(PREVIOUS)) {
            var1.append('<');
         }

         return var1.toString();
      }
   }

   private class FormatSpecifier implements Formatter.FormatString {
      private int index = -1;
      private Formatter.Flags f;
      private int width;
      private int precision;
      private boolean dt;
      private char c;

      private int index(String var1) {
         if (var1 != null) {
            try {
               this.index = Integer.parseInt(var1.substring(0, var1.length() - 1));
            } catch (NumberFormatException var3) {
               assert false;
            }
         } else {
            this.index = 0;
         }

         return this.index;
      }

      public int index() {
         return this.index;
      }

      private Formatter.Flags flags(String var1) {
         this.f = Formatter.Flags.parse(var1);
         if (this.f.contains(Formatter.Flags.PREVIOUS)) {
            this.index = -1;
         }

         return this.f;
      }

      Formatter.Flags flags() {
         return this.f;
      }

      private int width(String var1) {
         this.width = -1;
         if (var1 != null) {
            try {
               this.width = Integer.parseInt(var1);
               if (this.width < 0) {
                  throw new IllegalFormatWidthException(this.width);
               }
            } catch (NumberFormatException var3) {
               assert false;
            }
         }

         return this.width;
      }

      int width() {
         return this.width;
      }

      private int precision(String var1) {
         this.precision = -1;
         if (var1 != null) {
            try {
               this.precision = Integer.parseInt(var1.substring(1));
               if (this.precision < 0) {
                  throw new IllegalFormatPrecisionException(this.precision);
               }
            } catch (NumberFormatException var3) {
               assert false;
            }
         }

         return this.precision;
      }

      int precision() {
         return this.precision;
      }

      private char conversion(String var1) {
         this.c = var1.charAt(0);
         if (!this.dt) {
            if (!Formatter.Conversion.isValid(this.c)) {
               throw new UnknownFormatConversionException(String.valueOf(this.c));
            }

            if (Character.isUpperCase(this.c)) {
               this.f.add(Formatter.Flags.UPPERCASE);
            }

            this.c = Character.toLowerCase(this.c);
            if (Formatter.Conversion.isText(this.c)) {
               this.index = -2;
            }
         }

         return this.c;
      }

      private char conversion() {
         return this.c;
      }

      FormatSpecifier(Matcher var2) {
         this.f = Formatter.Flags.NONE;
         this.dt = false;
         byte var3 = 1;
         int var5 = var3 + 1;
         this.index(var2.group(var3));
         this.flags(var2.group(var5++));
         this.width(var2.group(var5++));
         this.precision(var2.group(var5++));
         String var4 = var2.group(var5++);
         if (var4 != null) {
            this.dt = true;
            if (var4.equals("T")) {
               this.f.add(Formatter.Flags.UPPERCASE);
            }
         }

         this.conversion(var2.group(var5));
         if (this.dt) {
            this.checkDateTime();
         } else if (Formatter.Conversion.isGeneral(this.c)) {
            this.checkGeneral();
         } else if (Formatter.Conversion.isCharacter(this.c)) {
            this.checkCharacter();
         } else if (Formatter.Conversion.isInteger(this.c)) {
            this.checkInteger();
         } else if (Formatter.Conversion.isFloat(this.c)) {
            this.checkFloat();
         } else {
            if (!Formatter.Conversion.isText(this.c)) {
               throw new UnknownFormatConversionException(String.valueOf(this.c));
            }

            this.checkText();
         }

      }

      public void print(Object var1, Locale var2) throws IOException {
         if (this.dt) {
            this.printDateTime(var1, var2);
         } else {
            switch(this.c) {
            case '%':
               Formatter.this.a.append('%');
               break;
            case 'C':
            case 'c':
               this.printCharacter(var1);
               break;
            case 'a':
            case 'e':
            case 'f':
            case 'g':
               this.printFloat(var1, var2);
               break;
            case 'b':
               this.printBoolean(var1);
               break;
            case 'd':
            case 'o':
            case 'x':
               this.printInteger(var1, var2);
               break;
            case 'h':
               this.printHashCode(var1);
               break;
            case 'n':
               Formatter.this.a.append(System.lineSeparator());
               break;
            case 's':
               this.printString(var1, var2);
               break;
            default:
               assert false;
            }

         }
      }

      private void printInteger(Object var1, Locale var2) throws IOException {
         if (var1 == null) {
            this.print("null");
         } else if (var1 instanceof Byte) {
            this.print((Byte)var1, var2);
         } else if (var1 instanceof Short) {
            this.print((Short)var1, var2);
         } else if (var1 instanceof Integer) {
            this.print((Integer)var1, var2);
         } else if (var1 instanceof Long) {
            this.print((Long)var1, var2);
         } else if (var1 instanceof BigInteger) {
            this.print((BigInteger)var1, var2);
         } else {
            this.failConversion(this.c, var1);
         }

      }

      private void printFloat(Object var1, Locale var2) throws IOException {
         if (var1 == null) {
            this.print("null");
         } else if (var1 instanceof Float) {
            this.print((Float)var1, var2);
         } else if (var1 instanceof Double) {
            this.print((Double)var1, var2);
         } else if (var1 instanceof BigDecimal) {
            this.print((BigDecimal)var1, var2);
         } else {
            this.failConversion(this.c, var1);
         }

      }

      private void printDateTime(Object var1, Locale var2) throws IOException {
         if (var1 == null) {
            this.print("null");
         } else {
            Calendar var3 = null;
            if (var1 instanceof Long) {
               var3 = Calendar.getInstance(var2 == null ? Locale.US : var2);
               var3.setTimeInMillis((Long)var1);
            } else if (var1 instanceof Date) {
               var3 = Calendar.getInstance(var2 == null ? Locale.US : var2);
               var3.setTime((Date)var1);
            } else if (var1 instanceof Calendar) {
               var3 = (Calendar)((Calendar)var1).clone();
               var3.setLenient(true);
            } else {
               if (var1 instanceof TemporalAccessor) {
                  this.print((TemporalAccessor)var1, this.c, var2);
                  return;
               }

               this.failConversion(this.c, var1);
            }

            this.print(var3, this.c, var2);
         }
      }

      private void printCharacter(Object var1) throws IOException {
         if (var1 == null) {
            this.print("null");
         } else {
            String var2 = null;
            if (var1 instanceof Character) {
               var2 = ((Character)var1).toString();
            } else if (var1 instanceof Byte) {
               byte var3 = (Byte)var1;
               if (!Character.isValidCodePoint(var3)) {
                  throw new IllegalFormatCodePointException(var3);
               }

               var2 = new String(Character.toChars(var3));
            } else if (var1 instanceof Short) {
               short var4 = (Short)var1;
               if (!Character.isValidCodePoint(var4)) {
                  throw new IllegalFormatCodePointException(var4);
               }

               var2 = new String(Character.toChars(var4));
            } else if (var1 instanceof Integer) {
               int var5 = (Integer)var1;
               if (!Character.isValidCodePoint(var5)) {
                  throw new IllegalFormatCodePointException(var5);
               }

               var2 = new String(Character.toChars(var5));
            } else {
               this.failConversion(this.c, var1);
            }

            this.print(var2);
         }
      }

      private void printString(Object var1, Locale var2) throws IOException {
         if (var1 instanceof Formattable) {
            Formatter var3 = Formatter.this;
            if (var3.locale() != var2) {
               var3 = new Formatter(var3.out(), var2);
            }

            ((Formattable)var1).formatTo(var3, this.f.valueOf(), this.width, this.precision);
         } else {
            if (this.f.contains(Formatter.Flags.ALTERNATE)) {
               this.failMismatch(Formatter.Flags.ALTERNATE, 's');
            }

            if (var1 == null) {
               this.print("null");
            } else {
               this.print(var1.toString());
            }
         }

      }

      private void printBoolean(Object var1) throws IOException {
         String var2;
         if (var1 != null) {
            var2 = var1 instanceof Boolean ? ((Boolean)var1).toString() : Boolean.toString(true);
         } else {
            var2 = Boolean.toString(false);
         }

         this.print(var2);
      }

      private void printHashCode(Object var1) throws IOException {
         String var2 = var1 == null ? "null" : Integer.toHexString(var1.hashCode());
         this.print(var2);
      }

      private void print(String var1) throws IOException {
         if (this.precision != -1 && this.precision < var1.length()) {
            var1 = var1.substring(0, this.precision);
         }

         if (this.f.contains(Formatter.Flags.UPPERCASE)) {
            var1 = var1.toUpperCase();
         }

         Formatter.this.a.append(this.justify(var1));
      }

      private String justify(String var1) {
         if (this.width == -1) {
            return var1;
         } else {
            StringBuilder var2 = new StringBuilder();
            boolean var3 = this.f.contains(Formatter.Flags.LEFT_JUSTIFY);
            int var4 = this.width - var1.length();
            int var5;
            if (!var3) {
               for(var5 = 0; var5 < var4; ++var5) {
                  var2.append(' ');
               }
            }

            var2.append(var1);
            if (var3) {
               for(var5 = 0; var5 < var4; ++var5) {
                  var2.append(' ');
               }
            }

            return var2.toString();
         }
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder("%");
         Formatter.Flags var2 = this.f.dup().remove(Formatter.Flags.UPPERCASE);
         var1.append(var2.toString());
         if (this.index > 0) {
            var1.append(this.index).append('$');
         }

         if (this.width != -1) {
            var1.append(this.width);
         }

         if (this.precision != -1) {
            var1.append('.').append(this.precision);
         }

         if (this.dt) {
            var1.append((char)(this.f.contains(Formatter.Flags.UPPERCASE) ? 'T' : 't'));
         }

         var1.append(this.f.contains(Formatter.Flags.UPPERCASE) ? Character.toUpperCase(this.c) : this.c);
         return var1.toString();
      }

      private void checkGeneral() {
         if ((this.c == 'b' || this.c == 'h') && this.f.contains(Formatter.Flags.ALTERNATE)) {
            this.failMismatch(Formatter.Flags.ALTERNATE, this.c);
         }

         if (this.width == -1 && this.f.contains(Formatter.Flags.LEFT_JUSTIFY)) {
            throw new MissingFormatWidthException(this.toString());
         } else {
            this.checkBadFlags(Formatter.Flags.PLUS, Formatter.Flags.LEADING_SPACE, Formatter.Flags.ZERO_PAD, Formatter.Flags.GROUP, Formatter.Flags.PARENTHESES);
         }
      }

      private void checkDateTime() {
         if (this.precision != -1) {
            throw new IllegalFormatPrecisionException(this.precision);
         } else if (!Formatter.DateTime.isValid(this.c)) {
            throw new UnknownFormatConversionException("t" + this.c);
         } else {
            this.checkBadFlags(Formatter.Flags.ALTERNATE, Formatter.Flags.PLUS, Formatter.Flags.LEADING_SPACE, Formatter.Flags.ZERO_PAD, Formatter.Flags.GROUP, Formatter.Flags.PARENTHESES);
            if (this.width == -1 && this.f.contains(Formatter.Flags.LEFT_JUSTIFY)) {
               throw new MissingFormatWidthException(this.toString());
            }
         }
      }

      private void checkCharacter() {
         if (this.precision != -1) {
            throw new IllegalFormatPrecisionException(this.precision);
         } else {
            this.checkBadFlags(Formatter.Flags.ALTERNATE, Formatter.Flags.PLUS, Formatter.Flags.LEADING_SPACE, Formatter.Flags.ZERO_PAD, Formatter.Flags.GROUP, Formatter.Flags.PARENTHESES);
            if (this.width == -1 && this.f.contains(Formatter.Flags.LEFT_JUSTIFY)) {
               throw new MissingFormatWidthException(this.toString());
            }
         }
      }

      private void checkInteger() {
         this.checkNumeric();
         if (this.precision != -1) {
            throw new IllegalFormatPrecisionException(this.precision);
         } else {
            if (this.c == 'd') {
               this.checkBadFlags(Formatter.Flags.ALTERNATE);
            } else if (this.c == 'o') {
               this.checkBadFlags(Formatter.Flags.GROUP);
            } else {
               this.checkBadFlags(Formatter.Flags.GROUP);
            }

         }
      }

      private void checkBadFlags(Formatter.Flags... var1) {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (this.f.contains(var1[var2])) {
               this.failMismatch(var1[var2], this.c);
            }
         }

      }

      private void checkFloat() {
         this.checkNumeric();
         if (this.c != 'f') {
            if (this.c == 'a') {
               this.checkBadFlags(Formatter.Flags.PARENTHESES, Formatter.Flags.GROUP);
            } else if (this.c == 'e') {
               this.checkBadFlags(Formatter.Flags.GROUP);
            } else if (this.c == 'g') {
               this.checkBadFlags(Formatter.Flags.ALTERNATE);
            }
         }

      }

      private void checkNumeric() {
         if (this.width != -1 && this.width < 0) {
            throw new IllegalFormatWidthException(this.width);
         } else if (this.precision != -1 && this.precision < 0) {
            throw new IllegalFormatPrecisionException(this.precision);
         } else if (this.width == -1 && (this.f.contains(Formatter.Flags.LEFT_JUSTIFY) || this.f.contains(Formatter.Flags.ZERO_PAD))) {
            throw new MissingFormatWidthException(this.toString());
         } else if (this.f.contains(Formatter.Flags.PLUS) && this.f.contains(Formatter.Flags.LEADING_SPACE) || this.f.contains(Formatter.Flags.LEFT_JUSTIFY) && this.f.contains(Formatter.Flags.ZERO_PAD)) {
            throw new IllegalFormatFlagsException(this.f.toString());
         }
      }

      private void checkText() {
         if (this.precision != -1) {
            throw new IllegalFormatPrecisionException(this.precision);
         } else {
            switch(this.c) {
            case '%':
               if (this.f.valueOf() != Formatter.Flags.LEFT_JUSTIFY.valueOf() && this.f.valueOf() != Formatter.Flags.NONE.valueOf()) {
                  throw new IllegalFormatFlagsException(this.f.toString());
               }

               if (this.width == -1 && this.f.contains(Formatter.Flags.LEFT_JUSTIFY)) {
                  throw new MissingFormatWidthException(this.toString());
               }
               break;
            case 'n':
               if (this.width != -1) {
                  throw new IllegalFormatWidthException(this.width);
               }

               if (this.f.valueOf() != Formatter.Flags.NONE.valueOf()) {
                  throw new IllegalFormatFlagsException(this.f.toString());
               }
               break;
            default:
               assert false;
            }

         }
      }

      private void print(byte var1, Locale var2) throws IOException {
         long var3 = (long)var1;
         if (var1 < 0 && (this.c == 'o' || this.c == 'x')) {
            var3 += 256L;

            assert var3 >= 0L : var3;
         }

         this.print(var3, var2);
      }

      private void print(short var1, Locale var2) throws IOException {
         long var3 = (long)var1;
         if (var1 < 0 && (this.c == 'o' || this.c == 'x')) {
            var3 += 65536L;

            assert var3 >= 0L : var3;
         }

         this.print(var3, var2);
      }

      private void print(int var1, Locale var2) throws IOException {
         long var3 = (long)var1;
         if (var1 < 0 && (this.c == 'o' || this.c == 'x')) {
            var3 += 4294967296L;

            assert var3 >= 0L : var3;
         }

         this.print(var3, var2);
      }

      private void print(long var1, Locale var3) throws IOException {
         StringBuilder var4 = new StringBuilder();
         if (this.c == 'd') {
            boolean var5 = var1 < 0L;
            char[] var6;
            if (var1 < 0L) {
               var6 = Long.toString(var1, 10).substring(1).toCharArray();
            } else {
               var6 = Long.toString(var1, 10).toCharArray();
            }

            this.leadingSign(var4, var5);
            this.localizedMagnitude(var4, var6, this.f, this.adjustWidth(this.width, this.f, var5), var3);
            this.trailingSign(var4, var5);
         } else {
            int var7;
            String var8;
            int var9;
            if (this.c == 'o') {
               this.checkBadFlags(Formatter.Flags.PARENTHESES, Formatter.Flags.LEADING_SPACE, Formatter.Flags.PLUS);
               var8 = Long.toOctalString(var1);
               var9 = this.f.contains(Formatter.Flags.ALTERNATE) ? var8.length() + 1 : var8.length();
               if (this.f.contains(Formatter.Flags.ALTERNATE)) {
                  var4.append('0');
               }

               if (this.f.contains(Formatter.Flags.ZERO_PAD)) {
                  for(var7 = 0; var7 < this.width - var9; ++var7) {
                     var4.append('0');
                  }
               }

               var4.append(var8);
            } else if (this.c == 'x') {
               this.checkBadFlags(Formatter.Flags.PARENTHESES, Formatter.Flags.LEADING_SPACE, Formatter.Flags.PLUS);
               var8 = Long.toHexString(var1);
               var9 = this.f.contains(Formatter.Flags.ALTERNATE) ? var8.length() + 2 : var8.length();
               if (this.f.contains(Formatter.Flags.ALTERNATE)) {
                  var4.append(this.f.contains(Formatter.Flags.UPPERCASE) ? "0X" : "0x");
               }

               if (this.f.contains(Formatter.Flags.ZERO_PAD)) {
                  for(var7 = 0; var7 < this.width - var9; ++var7) {
                     var4.append('0');
                  }
               }

               if (this.f.contains(Formatter.Flags.UPPERCASE)) {
                  var8 = var8.toUpperCase();
               }

               var4.append(var8);
            }
         }

         Formatter.this.a.append(this.justify(var4.toString()));
      }

      private StringBuilder leadingSign(StringBuilder var1, boolean var2) {
         if (!var2) {
            if (this.f.contains(Formatter.Flags.PLUS)) {
               var1.append('+');
            } else if (this.f.contains(Formatter.Flags.LEADING_SPACE)) {
               var1.append(' ');
            }
         } else if (this.f.contains(Formatter.Flags.PARENTHESES)) {
            var1.append('(');
         } else {
            var1.append('-');
         }

         return var1;
      }

      private StringBuilder trailingSign(StringBuilder var1, boolean var2) {
         if (var2 && this.f.contains(Formatter.Flags.PARENTHESES)) {
            var1.append(')');
         }

         return var1;
      }

      private void print(BigInteger var1, Locale var2) throws IOException {
         StringBuilder var3 = new StringBuilder();
         boolean var4 = var1.signum() == -1;
         BigInteger var5 = var1.abs();
         this.leadingSign(var3, var4);
         if (this.c == 'd') {
            char[] var6 = var5.toString().toCharArray();
            this.localizedMagnitude(var3, var6, this.f, this.adjustWidth(this.width, this.f, var4), var2);
         } else {
            int var7;
            int var8;
            String var9;
            if (this.c == 'o') {
               var9 = var5.toString(8);
               var7 = var9.length() + var3.length();
               if (var4 && this.f.contains(Formatter.Flags.PARENTHESES)) {
                  ++var7;
               }

               if (this.f.contains(Formatter.Flags.ALTERNATE)) {
                  ++var7;
                  var3.append('0');
               }

               if (this.f.contains(Formatter.Flags.ZERO_PAD)) {
                  for(var8 = 0; var8 < this.width - var7; ++var8) {
                     var3.append('0');
                  }
               }

               var3.append(var9);
            } else if (this.c == 'x') {
               var9 = var5.toString(16);
               var7 = var9.length() + var3.length();
               if (var4 && this.f.contains(Formatter.Flags.PARENTHESES)) {
                  ++var7;
               }

               if (this.f.contains(Formatter.Flags.ALTERNATE)) {
                  var7 += 2;
                  var3.append(this.f.contains(Formatter.Flags.UPPERCASE) ? "0X" : "0x");
               }

               if (this.f.contains(Formatter.Flags.ZERO_PAD)) {
                  for(var8 = 0; var8 < this.width - var7; ++var8) {
                     var3.append('0');
                  }
               }

               if (this.f.contains(Formatter.Flags.UPPERCASE)) {
                  var9 = var9.toUpperCase();
               }

               var3.append(var9);
            }
         }

         this.trailingSign(var3, var1.signum() == -1);
         Formatter.this.a.append(this.justify(var3.toString()));
      }

      private void print(float var1, Locale var2) throws IOException {
         this.print((double)var1, var2);
      }

      private void print(double var1, Locale var3) throws IOException {
         StringBuilder var4 = new StringBuilder();
         boolean var5 = Double.compare(var1, 0.0D) == -1;
         if (!Double.isNaN(var1)) {
            double var6 = Math.abs(var1);
            this.leadingSign(var4, var5);
            if (!Double.isInfinite(var6)) {
               this.print(var4, var6, var3, this.f, this.c, this.precision, var5);
            } else {
               var4.append(this.f.contains(Formatter.Flags.UPPERCASE) ? "INFINITY" : "Infinity");
            }

            this.trailingSign(var4, var5);
         } else {
            var4.append(this.f.contains(Formatter.Flags.UPPERCASE) ? "NAN" : "NaN");
         }

         Formatter.this.a.append(this.justify(var4.toString()));
      }

      private void print(StringBuilder var1, double var2, Locale var4, Formatter.Flags var5, char var6, int var7, boolean var8) throws IOException {
         int var9;
         FormattedFloatingDecimal var10;
         char[] var11;
         int var13;
         Formatter.Flags var14;
         char var15;
         char[] var16;
         if (var6 == 'e') {
            var9 = var7 == -1 ? 6 : var7;
            var10 = FormattedFloatingDecimal.valueOf(var2, var9, FormattedFloatingDecimal.Form.SCIENTIFIC);
            var11 = this.addZeros(var10.getMantissa(), var9);
            if (var5.contains(Formatter.Flags.ALTERNATE) && var9 == 0) {
               var11 = this.addDot(var11);
            }

            char[] var12 = var2 == 0.0D ? new char[]{'+', '0', '0'} : var10.getExponent();
            var13 = this.width;
            if (this.width != -1) {
               var13 = this.adjustWidth(this.width - var12.length - 1, var5, var8);
            }

            this.localizedMagnitude(var1, var11, var5, var13, var4);
            var1.append((char)(var5.contains(Formatter.Flags.UPPERCASE) ? 'E' : 'e'));
            var14 = var5.dup().remove(Formatter.Flags.GROUP);
            var15 = var12[0];

            assert var15 == '+' || var15 == '-';

            var1.append(var15);
            var16 = new char[var12.length - 1];
            System.arraycopy(var12, 1, var16, 0, var12.length - 1);
            var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, var16, var14, -1, var4));
         } else {
            int var19;
            if (var6 == 'f') {
               var9 = var7 == -1 ? 6 : var7;
               var10 = FormattedFloatingDecimal.valueOf(var2, var9, FormattedFloatingDecimal.Form.DECIMAL_FLOAT);
               var11 = this.addZeros(var10.getMantissa(), var9);
               if (var5.contains(Formatter.Flags.ALTERNATE) && var9 == 0) {
                  var11 = this.addDot(var11);
               }

               var19 = this.width;
               if (this.width != -1) {
                  var19 = this.adjustWidth(this.width, var5, var8);
               }

               this.localizedMagnitude(var1, var11, var5, var19, var4);
            } else if (var6 == 'g') {
               var9 = var7;
               if (var7 == -1) {
                  var9 = 6;
               } else if (var7 == 0) {
                  var9 = 1;
               }

               char[] var17;
               if (var2 == 0.0D) {
                  var17 = null;
                  var11 = new char[]{'0'};
                  var19 = 0;
               } else {
                  FormattedFloatingDecimal var20 = FormattedFloatingDecimal.valueOf(var2, var9, FormattedFloatingDecimal.Form.GENERAL);
                  var17 = var20.getExponent();
                  var11 = var20.getMantissa();
                  var19 = var20.getExponentRounded();
               }

               if (var17 != null) {
                  --var9;
               } else {
                  var9 -= var19 + 1;
               }

               var11 = this.addZeros(var11, var9);
               if (var5.contains(Formatter.Flags.ALTERNATE) && var9 == 0) {
                  var11 = this.addDot(var11);
               }

               var13 = this.width;
               if (this.width != -1) {
                  if (var17 != null) {
                     var13 = this.adjustWidth(this.width - var17.length - 1, var5, var8);
                  } else {
                     var13 = this.adjustWidth(this.width, var5, var8);
                  }
               }

               this.localizedMagnitude(var1, var11, var5, var13, var4);
               if (var17 != null) {
                  var1.append((char)(var5.contains(Formatter.Flags.UPPERCASE) ? 'E' : 'e'));
                  var14 = var5.dup().remove(Formatter.Flags.GROUP);
                  var15 = var17[0];

                  assert var15 == '+' || var15 == '-';

                  var1.append(var15);
                  var16 = new char[var17.length - 1];
                  System.arraycopy(var17, 1, var16, 0, var17.length - 1);
                  var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, var16, var14, -1, var4));
               }
            } else if (var6 == 'a') {
               var9 = var7;
               if (var7 == -1) {
                  var9 = 0;
               } else if (var7 == 0) {
                  var9 = 1;
               }

               String var18 = this.hexDouble(var2, var9);
               boolean var21 = var5.contains(Formatter.Flags.UPPERCASE);
               var1.append(var21 ? "0X" : "0x");
               if (var5.contains(Formatter.Flags.ZERO_PAD)) {
                  for(var13 = 0; var13 < this.width - var18.length() - 2; ++var13) {
                     var1.append('0');
                  }
               }

               var13 = var18.indexOf(112);
               var11 = var18.substring(0, var13).toCharArray();
               if (var21) {
                  String var22 = new String(var11);
                  var22 = var22.toUpperCase(Locale.US);
                  var11 = var22.toCharArray();
               }

               var1.append(var9 != 0 ? this.addZeros(var11, var9) : var11);
               var1.append((char)(var21 ? 'P' : 'p'));
               var1.append(var18.substring(var13 + 1));
            }
         }

      }

      private char[] addZeros(char[] var1, int var2) {
         int var3;
         for(var3 = 0; var3 < var1.length && var1[var3] != '.'; ++var3) {
         }

         boolean var4 = false;
         if (var3 == var1.length) {
            var4 = true;
         }

         int var5 = var1.length - var3 - (var4 ? 0 : 1);

         assert var5 <= var2;

         if (var5 == var2) {
            return var1;
         } else {
            char[] var6 = new char[var1.length + var2 - var5 + (var4 ? 1 : 0)];
            System.arraycopy(var1, 0, var6, 0, var1.length);
            int var7 = var1.length;
            if (var4) {
               var6[var1.length] = '.';
               ++var7;
            }

            for(int var8 = var7; var8 < var6.length; ++var8) {
               var6[var8] = '0';
            }

            return var6;
         }
      }

      private String hexDouble(double var1, int var3) {
         if (Double.isFinite(var1) && var1 != 0.0D && var3 != 0 && var3 < 13) {
            assert var3 >= 1 && var3 <= 12;

            int var4 = Math.getExponent(var1);
            boolean var5 = var4 == -1023;
            if (var5) {
               Formatter.scaleUp = Math.scalb(1.0D, 54);
               var1 *= Formatter.scaleUp;
               var4 = Math.getExponent(var1);

               assert var4 >= -1022 && var4 <= 1023 : var4;
            }

            int var6 = 1 + var3 * 4;
            int var7 = 53 - var6;

            assert var7 >= 1 && var7 < 53;

            long var8 = Double.doubleToLongBits(var1);
            long var10 = (var8 & Long.MAX_VALUE) >> var7;
            long var12 = var8 & ~(-1L << var7);
            boolean var14 = (var10 & 1L) == 0L;
            boolean var15 = (1L << var7 - 1 & var12) != 0L;
            boolean var16 = var7 > 1 && (~(1L << var7 - 1) & var12) != 0L;
            if (var14 && var15 && var16 || !var14 && var15) {
               ++var10;
            }

            long var17 = var8 & Long.MIN_VALUE;
            var10 = var17 | var10 << var7;
            double var19 = Double.longBitsToDouble(var10);
            if (Double.isInfinite(var19)) {
               return "1.0p1024";
            } else {
               String var21 = Double.toHexString(var19).substring(2);
               if (!var5) {
                  return var21;
               } else {
                  int var22 = var21.indexOf(112);
                  if (var22 == -1) {
                     assert false;

                     return null;
                  } else {
                     String var23 = var21.substring(var22 + 1);
                     int var24 = Integer.parseInt(var23) - 54;
                     return var21.substring(0, var22) + "p" + Integer.toString(var24);
                  }
               }
            }
         } else {
            return Double.toHexString(var1).substring(2);
         }
      }

      private void print(BigDecimal var1, Locale var2) throws IOException {
         if (this.c == 'a') {
            this.failConversion(this.c, var1);
         }

         StringBuilder var3 = new StringBuilder();
         boolean var4 = var1.signum() == -1;
         BigDecimal var5 = var1.abs();
         this.leadingSign(var3, var4);
         this.print(var3, var5, var2, this.f, this.c, this.precision, var4);
         this.trailingSign(var3, var4);
         Formatter.this.a.append(this.justify(var3.toString()));
      }

      private void print(StringBuilder var1, BigDecimal var2, Locale var3, Formatter.Flags var4, char var5, int var6, boolean var7) throws IOException {
         int var8;
         int var9;
         int var10;
         int var11;
         int var12;
         if (var5 == 'e') {
            var8 = var6 == -1 ? 6 : var6;
            var9 = var2.scale();
            var10 = var2.precision();
            var11 = 0;
            if (var8 > var10 - 1) {
               var12 = var10;
               var11 = var8 - (var10 - 1);
            } else {
               var12 = var8 + 1;
            }

            MathContext var13 = new MathContext(var12);
            BigDecimal var14 = new BigDecimal(var2.unscaledValue(), var9, var13);
            Formatter.FormatSpecifier.BigDecimalLayout var15 = new Formatter.FormatSpecifier.BigDecimalLayout(var14.unscaledValue(), var14.scale(), Formatter.BigDecimalLayoutForm.SCIENTIFIC);
            char[] var16 = var15.mantissa();
            if ((var10 == 1 || !var15.hasDot()) && (var11 > 0 || var4.contains(Formatter.Flags.ALTERNATE))) {
               var16 = this.addDot(var16);
            }

            var16 = this.trailingZeros(var16, var11);
            char[] var17 = var15.exponent();
            int var18 = this.width;
            if (this.width != -1) {
               var18 = this.adjustWidth(this.width - var17.length - 1, var4, var7);
            }

            this.localizedMagnitude(var1, var16, var4, var18, var3);
            var1.append((char)(var4.contains(Formatter.Flags.UPPERCASE) ? 'E' : 'e'));
            Formatter.Flags var19 = var4.dup().remove(Formatter.Flags.GROUP);
            char var20 = var17[0];

            assert var20 == '+' || var20 == '-';

            var1.append(var17[0]);
            char[] var21 = new char[var17.length - 1];
            System.arraycopy(var17, 1, var21, 0, var17.length - 1);
            var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, var21, var19, -1, var3));
         } else if (var5 == 'f') {
            var8 = var6 == -1 ? 6 : var6;
            var9 = var2.scale();
            if (var9 > var8) {
               var10 = var2.precision();
               if (var10 <= var9) {
                  var2 = var2.setScale(var8, RoundingMode.HALF_UP);
               } else {
                  var10 -= var9 - var8;
                  var2 = new BigDecimal(var2.unscaledValue(), var9, new MathContext(var10));
               }
            }

            Formatter.FormatSpecifier.BigDecimalLayout var23 = new Formatter.FormatSpecifier.BigDecimalLayout(var2.unscaledValue(), var2.scale(), Formatter.BigDecimalLayoutForm.DECIMAL_FLOAT);
            char[] var25 = var23.mantissa();
            var12 = var23.scale() < var8 ? var8 - var23.scale() : 0;
            if (var23.scale() == 0 && (var4.contains(Formatter.Flags.ALTERNATE) || var12 > 0)) {
               var25 = this.addDot(var23.mantissa());
            }

            var25 = this.trailingZeros(var25, var12);
            this.localizedMagnitude(var1, var25, var4, this.adjustWidth(this.width, var4, var7), var3);
         } else if (var5 == 'g') {
            var8 = var6;
            if (var6 == -1) {
               var8 = 6;
            } else if (var6 == 0) {
               var8 = 1;
            }

            BigDecimal var22 = BigDecimal.valueOf(1L, 4);
            BigDecimal var24 = BigDecimal.valueOf(1L, -var8);
            if (var2.equals(BigDecimal.ZERO) || var2.compareTo(var22) != -1 && var2.compareTo(var24) == -1) {
               var11 = -var2.scale() + (var2.unscaledValue().toString().length() - 1);
               var8 = var8 - var11 - 1;
               this.print(var1, var2, var3, var4, 'f', var8, var7);
            } else {
               this.print(var1, var2, var3, var4, 'e', var8 - 1, var7);
            }
         } else {
            assert var5 != 'a';
         }

      }

      private int adjustWidth(int var1, Formatter.Flags var2, boolean var3) {
         int var4 = var1;
         if (var1 != -1 && var3 && var2.contains(Formatter.Flags.PARENTHESES)) {
            var4 = var1 - 1;
         }

         return var4;
      }

      private char[] addDot(char[] var1) {
         char[] var2 = new char[var1.length + 1];
         System.arraycopy(var1, 0, var2, 0, var1.length);
         var2[var2.length - 1] = '.';
         return var2;
      }

      private char[] trailingZeros(char[] var1, int var2) {
         char[] var3 = var1;
         if (var2 > 0) {
            var3 = new char[var1.length + var2];
            System.arraycopy(var1, 0, var3, 0, var1.length);

            for(int var4 = var1.length; var4 < var3.length; ++var4) {
               var3[var4] = '0';
            }
         }

         return var3;
      }

      private void print(Calendar var1, char var2, Locale var3) throws IOException {
         StringBuilder var4 = new StringBuilder();
         this.print(var4, var1, var2, var3);
         String var5 = this.justify(var4.toString());
         if (this.f.contains(Formatter.Flags.UPPERCASE)) {
            var5 = var5.toUpperCase();
         }

         Formatter.this.a.append(var5);
      }

      private Appendable print(StringBuilder var1, Calendar var2, char var3, Locale var4) throws IOException {
         if (var1 == null) {
            var1 = new StringBuilder();
         }

         int var5;
         long var10;
         char var11;
         Formatter.Flags var16;
         Formatter.Flags var17;
         DateFormatSymbols var18;
         Locale var21;
         switch(var3) {
         case 'A':
         case 'a':
            var5 = var2.get(7);
            var21 = var4 == null ? Locale.US : var4;
            var18 = DateFormatSymbols.getInstance(var21);
            if (var3 == 'A') {
               var1.append(var18.getWeekdays()[var5]);
            } else {
               var1.append(var18.getShortWeekdays()[var5]);
            }
            break;
         case 'B':
         case 'b':
         case 'h':
            var5 = var2.get(2);
            var21 = var4 == null ? Locale.US : var4;
            var18 = DateFormatSymbols.getInstance(var21);
            if (var3 == 'B') {
               var1.append(var18.getMonths()[var5]);
            } else {
               var1.append(var18.getShortMonths()[var5]);
            }
            break;
         case 'C':
         case 'Y':
         case 'y':
            var5 = var2.get(1);
            byte var20 = 2;
            switch(var3) {
            case 'C':
               var5 /= 100;
               break;
            case 'Y':
               var20 = 4;
               break;
            case 'y':
               var5 %= 100;
            }

            var17 = Formatter.Flags.ZERO_PAD;
            var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var17, var20, var4));
            break;
         case 'D':
            var11 = '/';
            this.print(var1, var2, 'm', var4).append(var11);
            this.print(var1, var2, 'd', var4).append(var11);
            this.print(var1, var2, 'y', var4);
            break;
         case 'E':
         case 'G':
         case 'J':
         case 'K':
         case 'O':
         case 'P':
         case 'U':
         case 'V':
         case 'W':
         case 'X':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '_':
         case '`':
         case 'f':
         case 'g':
         case 'i':
         case 'n':
         case 'o':
         case 'q':
         case 't':
         case 'u':
         case 'v':
         case 'w':
         case 'x':
         default:
            assert false;
            break;
         case 'F':
            var11 = '-';
            this.print(var1, var2, 'Y', var4).append(var11);
            this.print(var1, var2, 'm', var4).append(var11);
            this.print(var1, var2, 'd', var4);
            break;
         case 'H':
         case 'I':
         case 'k':
         case 'l':
            var5 = var2.get(11);
            if (var3 == 'I' || var3 == 'l') {
               var5 = var5 != 0 && var5 != 12 ? var5 % 12 : 12;
            }

            var16 = var3 != 'H' && var3 != 'I' ? Formatter.Flags.NONE : Formatter.Flags.ZERO_PAD;
            var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var16, 2, var4));
            break;
         case 'L':
            var5 = var2.get(14);
            var16 = Formatter.Flags.ZERO_PAD;
            var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var16, 3, var4));
            break;
         case 'M':
            var5 = var2.get(12);
            var16 = Formatter.Flags.ZERO_PAD;
            var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var16, 2, var4));
            break;
         case 'N':
            var5 = var2.get(14) * 1000000;
            var16 = Formatter.Flags.ZERO_PAD;
            var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var16, 9, var4));
            break;
         case 'Q':
            var10 = var2.getTimeInMillis();
            var17 = Formatter.Flags.NONE;
            var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, var10, var17, this.width, var4));
            break;
         case 'R':
         case 'T':
            var11 = ':';
            this.print(var1, var2, 'H', var4).append(var11);
            this.print(var1, var2, 'M', var4);
            if (var3 == 'T') {
               var1.append(var11);
               this.print(var1, var2, 'S', var4);
            }
            break;
         case 'S':
            var5 = var2.get(13);
            var16 = Formatter.Flags.ZERO_PAD;
            var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var16, 2, var4));
            break;
         case 'Z':
            TimeZone var19 = var2.getTimeZone();
            var1.append(var19.getDisplayName(var2.get(16) != 0, 0, var4 == null ? Locale.US : var4));
            break;
         case 'c':
            var11 = ' ';
            this.print(var1, var2, 'a', var4).append(var11);
            this.print(var1, var2, 'b', var4).append(var11);
            this.print(var1, var2, 'd', var4).append(var11);
            this.print(var1, var2, 'T', var4).append(var11);
            this.print(var1, var2, 'Z', var4).append(var11);
            this.print(var1, var2, 'Y', var4);
            break;
         case 'd':
         case 'e':
            var5 = var2.get(5);
            var16 = var3 == 'd' ? Formatter.Flags.ZERO_PAD : Formatter.Flags.NONE;
            var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var16, 2, var4));
            break;
         case 'j':
            var5 = var2.get(6);
            var16 = Formatter.Flags.ZERO_PAD;
            var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var16, 3, var4));
            break;
         case 'm':
            var5 = var2.get(2) + 1;
            var16 = Formatter.Flags.ZERO_PAD;
            var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var16, 2, var4));
            break;
         case 'p':
            String[] var13 = new String[]{"AM", "PM"};
            if (var4 != null && var4 != Locale.US) {
               DateFormatSymbols var14 = DateFormatSymbols.getInstance(var4);
               var13 = var14.getAmPmStrings();
            }

            String var15 = var13[var2.get(9)];
            var1.append(var15.toLowerCase(var4 != null ? var4 : Locale.US));
            break;
         case 'r':
            var11 = ':';
            this.print(var1, var2, 'I', var4).append(var11);
            this.print(var1, var2, 'M', var4).append(var11);
            this.print(var1, var2, 'S', var4).append(' ');
            StringBuilder var12 = new StringBuilder();
            this.print(var12, var2, 'p', var4);
            var1.append(var12.toString().toUpperCase(var4 != null ? var4 : Locale.US));
            break;
         case 's':
            var10 = var2.getTimeInMillis() / 1000L;
            var17 = Formatter.Flags.NONE;
            var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, var10, var17, this.width, var4));
            break;
         case 'z':
            var5 = var2.get(15) + var2.get(16);
            boolean var6 = var5 < 0;
            var1.append((char)(var6 ? '-' : '+'));
            if (var6) {
               var5 = -var5;
            }

            int var7 = var5 / '\uea60';
            int var8 = var7 / 60 * 100 + var7 % 60;
            Formatter.Flags var9 = Formatter.Flags.ZERO_PAD;
            var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var8, var9, 4, var4));
         }

         return var1;
      }

      private void print(TemporalAccessor var1, char var2, Locale var3) throws IOException {
         StringBuilder var4 = new StringBuilder();
         this.print(var4, var1, var2, var3);
         String var5 = this.justify(var4.toString());
         if (this.f.contains(Formatter.Flags.UPPERCASE)) {
            var5 = var5.toUpperCase();
         }

         Formatter.this.a.append(var5);
      }

      private Appendable print(StringBuilder var1, TemporalAccessor var2, char var3, Locale var4) throws IOException {
         if (var1 == null) {
            var1 = new StringBuilder();
         }

         try {
            int var5;
            long var11;
            char var12;
            Formatter.Flags var17;
            Formatter.Flags var18;
            DateFormatSymbols var19;
            Locale var23;
            switch(var3) {
            case 'A':
            case 'a':
               var5 = var2.get(ChronoField.DAY_OF_WEEK) % 7 + 1;
               var23 = var4 == null ? Locale.US : var4;
               var19 = DateFormatSymbols.getInstance(var23);
               if (var3 == 'A') {
                  var1.append(var19.getWeekdays()[var5]);
               } else {
                  var1.append(var19.getShortWeekdays()[var5]);
               }
               break;
            case 'B':
            case 'b':
            case 'h':
               var5 = var2.get(ChronoField.MONTH_OF_YEAR) - 1;
               var23 = var4 == null ? Locale.US : var4;
               var19 = DateFormatSymbols.getInstance(var23);
               if (var3 == 'B') {
                  var1.append(var19.getMonths()[var5]);
               } else {
                  var1.append(var19.getShortMonths()[var5]);
               }
               break;
            case 'C':
            case 'Y':
            case 'y':
               var5 = var2.get(ChronoField.YEAR_OF_ERA);
               byte var22 = 2;
               switch(var3) {
               case 'C':
                  var5 /= 100;
                  break;
               case 'Y':
                  var22 = 4;
                  break;
               case 'y':
                  var5 %= 100;
               }

               var18 = Formatter.Flags.ZERO_PAD;
               var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var18, var22, var4));
               break;
            case 'D':
               var12 = '/';
               this.print(var1, var2, 'm', var4).append(var12);
               this.print(var1, var2, 'd', var4).append(var12);
               this.print(var1, var2, 'y', var4);
               break;
            case 'E':
            case 'G':
            case 'J':
            case 'K':
            case 'O':
            case 'P':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'f':
            case 'g':
            case 'i':
            case 'n':
            case 'o':
            case 'q':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            default:
               assert false;
               break;
            case 'F':
               var12 = '-';
               this.print(var1, var2, 'Y', var4).append(var12);
               this.print(var1, var2, 'm', var4).append(var12);
               this.print(var1, var2, 'd', var4);
               break;
            case 'H':
               var5 = var2.get(ChronoField.HOUR_OF_DAY);
               var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, Formatter.Flags.ZERO_PAD, 2, var4));
               break;
            case 'I':
               var5 = var2.get(ChronoField.CLOCK_HOUR_OF_AMPM);
               var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, Formatter.Flags.ZERO_PAD, 2, var4));
               break;
            case 'L':
               var5 = var2.get(ChronoField.MILLI_OF_SECOND);
               var17 = Formatter.Flags.ZERO_PAD;
               var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var17, 3, var4));
               break;
            case 'M':
               var5 = var2.get(ChronoField.MINUTE_OF_HOUR);
               var17 = Formatter.Flags.ZERO_PAD;
               var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var17, 2, var4));
               break;
            case 'N':
               var5 = var2.get(ChronoField.MILLI_OF_SECOND) * 1000000;
               var17 = Formatter.Flags.ZERO_PAD;
               var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var17, 9, var4));
               break;
            case 'Q':
               var11 = var2.getLong(ChronoField.INSTANT_SECONDS) * 1000L + var2.getLong(ChronoField.MILLI_OF_SECOND);
               var18 = Formatter.Flags.NONE;
               var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, var11, var18, this.width, var4));
               break;
            case 'R':
            case 'T':
               var12 = ':';
               this.print(var1, var2, 'H', var4).append(var12);
               this.print(var1, var2, 'M', var4);
               if (var3 == 'T') {
                  var1.append(var12);
                  this.print(var1, var2, 'S', var4);
               }
               break;
            case 'S':
               var5 = var2.get(ChronoField.SECOND_OF_MINUTE);
               var17 = Formatter.Flags.ZERO_PAD;
               var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var17, 2, var4));
               break;
            case 'Z':
               ZoneId var21 = (ZoneId)var2.query(TemporalQueries.zone());
               if (var21 == null) {
                  throw new IllegalFormatConversionException(var3, var2.getClass());
               }

               if (!(var21 instanceof ZoneOffset) && var2.isSupported(ChronoField.INSTANT_SECONDS)) {
                  Instant var20 = Instant.from(var2);
                  var1.append(TimeZone.getTimeZone(var21.getId()).getDisplayName(var21.getRules().isDaylightSavings(var20), 0, var4 == null ? Locale.US : var4));
               } else {
                  var1.append(var21.getId());
               }
               break;
            case 'c':
               var12 = ' ';
               this.print(var1, var2, 'a', var4).append(var12);
               this.print(var1, var2, 'b', var4).append(var12);
               this.print(var1, var2, 'd', var4).append(var12);
               this.print(var1, var2, 'T', var4).append(var12);
               this.print(var1, var2, 'Z', var4).append(var12);
               this.print(var1, var2, 'Y', var4);
               break;
            case 'd':
            case 'e':
               var5 = var2.get(ChronoField.DAY_OF_MONTH);
               var17 = var3 == 'd' ? Formatter.Flags.ZERO_PAD : Formatter.Flags.NONE;
               var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var17, 2, var4));
               break;
            case 'j':
               var5 = var2.get(ChronoField.DAY_OF_YEAR);
               var17 = Formatter.Flags.ZERO_PAD;
               var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var17, 3, var4));
               break;
            case 'k':
               var5 = var2.get(ChronoField.HOUR_OF_DAY);
               var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, Formatter.Flags.NONE, 2, var4));
               break;
            case 'l':
               var5 = var2.get(ChronoField.CLOCK_HOUR_OF_AMPM);
               var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, Formatter.Flags.NONE, 2, var4));
               break;
            case 'm':
               var5 = var2.get(ChronoField.MONTH_OF_YEAR);
               var17 = Formatter.Flags.ZERO_PAD;
               var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var5, var17, 2, var4));
               break;
            case 'p':
               String[] var14 = new String[]{"AM", "PM"};
               if (var4 != null && var4 != Locale.US) {
                  DateFormatSymbols var15 = DateFormatSymbols.getInstance(var4);
                  var14 = var15.getAmPmStrings();
               }

               String var16 = var14[var2.get(ChronoField.AMPM_OF_DAY)];
               var1.append(var16.toLowerCase(var4 != null ? var4 : Locale.US));
               break;
            case 'r':
               var12 = ':';
               this.print(var1, var2, 'I', var4).append(var12);
               this.print(var1, var2, 'M', var4).append(var12);
               this.print(var1, var2, 'S', var4).append(' ');
               StringBuilder var13 = new StringBuilder();
               this.print(var13, var2, 'p', var4);
               var1.append(var13.toString().toUpperCase(var4 != null ? var4 : Locale.US));
               break;
            case 's':
               var11 = var2.getLong(ChronoField.INSTANT_SECONDS);
               var18 = Formatter.Flags.NONE;
               var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, var11, var18, this.width, var4));
               break;
            case 'z':
               var5 = var2.get(ChronoField.OFFSET_SECONDS);
               boolean var6 = var5 < 0;
               var1.append((char)(var6 ? '-' : '+'));
               if (var6) {
                  var5 = -var5;
               }

               int var7 = var5 / 60;
               int var8 = var7 / 60 * 100 + var7 % 60;
               Formatter.Flags var9 = Formatter.Flags.ZERO_PAD;
               var1.append((CharSequence)this.localizedMagnitude((StringBuilder)null, (long)var8, var9, 4, var4));
            }

            return var1;
         } catch (DateTimeException var10) {
            throw new IllegalFormatConversionException(var3, var2.getClass());
         }
      }

      private void failMismatch(Formatter.Flags var1, char var2) {
         String var3 = var1.toString();
         throw new FormatFlagsConversionMismatchException(var3, var2);
      }

      private void failConversion(char var1, Object var2) {
         throw new IllegalFormatConversionException(var1, var2.getClass());
      }

      private char getZero(Locale var1) {
         if (var1 != null && !var1.equals(Formatter.this.locale())) {
            DecimalFormatSymbols var2 = DecimalFormatSymbols.getInstance(var1);
            return var2.getZeroDigit();
         } else {
            return Formatter.this.zero;
         }
      }

      private StringBuilder localizedMagnitude(StringBuilder var1, long var2, Formatter.Flags var4, int var5, Locale var6) {
         char[] var7 = Long.toString(var2, 10).toCharArray();
         return this.localizedMagnitude(var1, var7, var4, var5, var6);
      }

      private StringBuilder localizedMagnitude(StringBuilder var1, char[] var2, Formatter.Flags var3, int var4, Locale var5) {
         if (var1 == null) {
            var1 = new StringBuilder();
         }

         int var6 = var1.length();
         char var7 = this.getZero(var5);
         char var8 = 0;
         int var9 = -1;
         char var10 = 0;
         int var11 = var2.length;
         int var12 = var11;

         int var13;
         for(var13 = 0; var13 < var11; ++var13) {
            if (var2[var13] == '.') {
               var12 = var13;
               break;
            }
         }

         DecimalFormatSymbols var15;
         if (var12 < var11) {
            if (var5 != null && !var5.equals(Locale.US)) {
               var15 = DecimalFormatSymbols.getInstance(var5);
               var10 = var15.getDecimalSeparator();
            } else {
               var10 = '.';
            }
         }

         if (var3.contains(Formatter.Flags.GROUP)) {
            if (var5 != null && !var5.equals(Locale.US)) {
               var15 = DecimalFormatSymbols.getInstance(var5);
               var8 = var15.getGroupingSeparator();
               DecimalFormat var14 = (DecimalFormat)NumberFormat.getIntegerInstance(var5);
               var9 = var14.getGroupingSize();
            } else {
               var8 = ',';
               var9 = 3;
            }
         }

         for(var13 = 0; var13 < var11; ++var13) {
            if (var13 == var12) {
               var1.append(var10);
               var8 = 0;
            } else {
               char var16 = var2[var13];
               var1.append((char)(var16 - 48 + var7));
               if (var8 != 0 && var13 != var12 - 1 && (var12 - var13) % var9 == 1) {
                  var1.append(var8);
               }
            }
         }

         var11 = var1.length();
         if (var4 != -1 && var3.contains(Formatter.Flags.ZERO_PAD)) {
            for(var13 = 0; var13 < var4 - var11; ++var13) {
               var1.insert(var6, var7);
            }
         }

         return var1;
      }

      private class BigDecimalLayout {
         private StringBuilder mant;
         private StringBuilder exp;
         private boolean dot = false;
         private int scale;

         public BigDecimalLayout(BigInteger var2, int var3, Formatter.BigDecimalLayoutForm var4) {
            this.layout(var2, var3, var4);
         }

         public boolean hasDot() {
            return this.dot;
         }

         public int scale() {
            return this.scale;
         }

         public char[] layoutChars() {
            StringBuilder var1 = new StringBuilder(this.mant);
            if (this.exp != null) {
               var1.append('E');
               var1.append((CharSequence)this.exp);
            }

            return this.toCharArray(var1);
         }

         public char[] mantissa() {
            return this.toCharArray(this.mant);
         }

         public char[] exponent() {
            return this.toCharArray(this.exp);
         }

         private char[] toCharArray(StringBuilder var1) {
            if (var1 == null) {
               return null;
            } else {
               char[] var2 = new char[var1.length()];
               var1.getChars(0, var2.length, var2, 0);
               return var2;
            }
         }

         private void layout(BigInteger var1, int var2, Formatter.BigDecimalLayoutForm var3) {
            char[] var4 = var1.toString().toCharArray();
            this.scale = var2;
            this.mant = new StringBuilder(var4.length + 14);
            if (var2 == 0) {
               int var9 = var4.length;
               if (var9 > 1) {
                  this.mant.append(var4[0]);
                  if (var3 == Formatter.BigDecimalLayoutForm.SCIENTIFIC) {
                     this.mant.append('.');
                     this.dot = true;
                     this.mant.append((char[])var4, 1, var9 - 1);
                     this.exp = new StringBuilder("+");
                     if (var9 < 10) {
                        this.exp.append("0").append(var9 - 1);
                     } else {
                        this.exp.append(var9 - 1);
                     }
                  } else {
                     this.mant.append((char[])var4, 1, var9 - 1);
                  }
               } else {
                  this.mant.append(var4);
                  if (var3 == Formatter.BigDecimalLayoutForm.SCIENTIFIC) {
                     this.exp = new StringBuilder("+00");
                  }
               }

            } else {
               long var5 = -((long)var2) + (long)(var4.length - 1);
               if (var3 == Formatter.BigDecimalLayoutForm.DECIMAL_FLOAT) {
                  int var7 = var2 - var4.length;
                  if (var7 >= 0) {
                     this.mant.append("0.");

                     for(this.dot = true; var7 > 0; --var7) {
                        this.mant.append('0');
                     }

                     this.mant.append(var4);
                  } else if (-var7 < var4.length) {
                     this.mant.append((char[])var4, 0, -var7);
                     this.mant.append('.');
                     this.dot = true;
                     this.mant.append(var4, -var7, var2);
                  } else {
                     this.mant.append((char[])var4, 0, var4.length);

                     for(int var8 = 0; var8 < -var2; ++var8) {
                        this.mant.append('0');
                     }

                     this.scale = 0;
                  }
               } else {
                  this.mant.append(var4[0]);
                  if (var4.length > 1) {
                     this.mant.append('.');
                     this.dot = true;
                     this.mant.append((char[])var4, 1, var4.length - 1);
                  }

                  this.exp = new StringBuilder();
                  if (var5 != 0L) {
                     long var10 = Math.abs(var5);
                     this.exp.append((char)(var5 < 0L ? '-' : '+'));
                     if (var10 < 10L) {
                        this.exp.append('0');
                     }

                     this.exp.append(var10);
                  } else {
                     this.exp.append("+00");
                  }
               }

            }
         }
      }
   }

   public static enum BigDecimalLayoutForm {
      SCIENTIFIC,
      DECIMAL_FLOAT;
   }

   private class FixedString implements Formatter.FormatString {
      private String s;

      FixedString(String var2) {
         this.s = var2;
      }

      public int index() {
         return -2;
      }

      public void print(Object var1, Locale var2) throws IOException {
         Formatter.this.a.append(this.s);
      }

      public String toString() {
         return this.s;
      }
   }

   private interface FormatString {
      int index();

      void print(Object var1, Locale var2) throws IOException;

      String toString();
   }
}
