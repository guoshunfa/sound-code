package java.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.misc.LRUCache;

public final class Scanner implements Iterator<String>, Closeable {
   private CharBuffer buf;
   private static final int BUFFER_SIZE = 1024;
   private int position;
   private Matcher matcher;
   private Pattern delimPattern;
   private Pattern hasNextPattern;
   private int hasNextPosition;
   private String hasNextResult;
   private Readable source;
   private boolean sourceClosed;
   private boolean needInput;
   private boolean skipped;
   private int savedScannerPosition;
   private Object typeCache;
   private boolean matchValid;
   private boolean closed;
   private int radix;
   private int defaultRadix;
   private Locale locale;
   private LRUCache<String, Pattern> patternCache;
   private IOException lastException;
   private static Pattern WHITESPACE_PATTERN = Pattern.compile("\\p{javaWhitespace}+");
   private static Pattern FIND_ANY_PATTERN = Pattern.compile("(?s).*");
   private static Pattern NON_ASCII_DIGIT = Pattern.compile("[\\p{javaDigit}&&[^0-9]]");
   private String groupSeparator;
   private String decimalSeparator;
   private String nanString;
   private String infinityString;
   private String positivePrefix;
   private String negativePrefix;
   private String positiveSuffix;
   private String negativeSuffix;
   private static volatile Pattern boolPattern;
   private static final String BOOLEAN_PATTERN = "true|false";
   private Pattern integerPattern;
   private String digits;
   private String non0Digit;
   private int SIMPLE_GROUP_INDEX;
   private static volatile Pattern separatorPattern;
   private static volatile Pattern linePattern;
   private static final String LINE_SEPARATOR_PATTERN = "\r\n|[\n\r\u2028\u2029\u0085]";
   private static final String LINE_PATTERN = ".*(\r\n|[\n\r\u2028\u2029\u0085])|.+$";
   private Pattern floatPattern;
   private Pattern decimalPattern;

   private static Pattern boolPattern() {
      Pattern var0 = boolPattern;
      if (var0 == null) {
         boolPattern = var0 = Pattern.compile("true|false", 2);
      }

      return var0;
   }

   private String buildIntegerPatternString() {
      String var1 = this.digits.substring(0, this.radix);
      String var2 = "((?i)[" + var1 + "]|\\p{javaDigit})";
      String var3 = "(" + this.non0Digit + var2 + "?" + var2 + "?(" + this.groupSeparator + var2 + var2 + var2 + ")+)";
      String var4 = "((" + var2 + "++)|" + var3 + ")";
      String var5 = "([-+]?(" + var4 + "))";
      String var6 = this.negativePrefix + var4 + this.negativeSuffix;
      String var7 = this.positivePrefix + var4 + this.positiveSuffix;
      return "(" + var5 + ")|(" + var7 + ")|(" + var6 + ")";
   }

   private Pattern integerPattern() {
      if (this.integerPattern == null) {
         this.integerPattern = (Pattern)this.patternCache.forName(this.buildIntegerPatternString());
      }

      return this.integerPattern;
   }

   private static Pattern separatorPattern() {
      Pattern var0 = separatorPattern;
      if (var0 == null) {
         separatorPattern = var0 = Pattern.compile("\r\n|[\n\r\u2028\u2029\u0085]");
      }

      return var0;
   }

   private static Pattern linePattern() {
      Pattern var0 = linePattern;
      if (var0 == null) {
         linePattern = var0 = Pattern.compile(".*(\r\n|[\n\r\u2028\u2029\u0085])|.+$");
      }

      return var0;
   }

   private void buildFloatAndDecimalPattern() {
      String var1 = "([0-9]|(\\p{javaDigit}))";
      String var2 = "([eE][+-]?" + var1 + "+)?";
      String var3 = "(" + this.non0Digit + var1 + "?" + var1 + "?(" + this.groupSeparator + var1 + var1 + var1 + ")+)";
      String var4 = "((" + var1 + "++)|" + var3 + ")";
      String var5 = "(" + var4 + "|" + var4 + this.decimalSeparator + var1 + "*+|" + this.decimalSeparator + var1 + "++)";
      String var6 = "(NaN|" + this.nanString + "|Infinity|" + this.infinityString + ")";
      String var7 = "(" + this.positivePrefix + var5 + this.positiveSuffix + var2 + ")";
      String var8 = "(" + this.negativePrefix + var5 + this.negativeSuffix + var2 + ")";
      String var9 = "(([-+]?" + var5 + var2 + ")|" + var7 + "|" + var8 + ")";
      String var10 = "[-+]?0[xX][0-9a-fA-F]*\\.[0-9a-fA-F]+([pP][-+]?[0-9]+)?";
      String var11 = "(" + this.positivePrefix + var6 + this.positiveSuffix + ")";
      String var12 = "(" + this.negativePrefix + var6 + this.negativeSuffix + ")";
      String var13 = "(([-+]?" + var6 + ")|" + var11 + "|" + var12 + ")";
      this.floatPattern = Pattern.compile(var9 + "|" + var10 + "|" + var13);
      this.decimalPattern = Pattern.compile(var9);
   }

   private Pattern floatPattern() {
      if (this.floatPattern == null) {
         this.buildFloatAndDecimalPattern();
      }

      return this.floatPattern;
   }

   private Pattern decimalPattern() {
      if (this.decimalPattern == null) {
         this.buildFloatAndDecimalPattern();
      }

      return this.decimalPattern;
   }

   private Scanner(Readable var1, Pattern var2) {
      this.sourceClosed = false;
      this.needInput = false;
      this.skipped = false;
      this.savedScannerPosition = -1;
      this.typeCache = null;
      this.matchValid = false;
      this.closed = false;
      this.radix = 10;
      this.defaultRadix = 10;
      this.locale = null;
      this.patternCache = new LRUCache<String, Pattern>(7) {
         protected Pattern create(String var1) {
            return Pattern.compile(var1);
         }

         protected boolean hasName(Pattern var1, String var2) {
            return var1.pattern().equals(var2);
         }
      };
      this.groupSeparator = "\\,";
      this.decimalSeparator = "\\.";
      this.nanString = "NaN";
      this.infinityString = "Infinity";
      this.positivePrefix = "";
      this.negativePrefix = "\\-";
      this.positiveSuffix = "";
      this.negativeSuffix = "";
      this.digits = "0123456789abcdefghijklmnopqrstuvwxyz";
      this.non0Digit = "[\\p{javaDigit}&&[^0]]";
      this.SIMPLE_GROUP_INDEX = 5;

      assert var1 != null : "source should not be null";

      assert var2 != null : "pattern should not be null";

      this.source = var1;
      this.delimPattern = var2;
      this.buf = CharBuffer.allocate(1024);
      this.buf.limit(0);
      this.matcher = this.delimPattern.matcher(this.buf);
      this.matcher.useTransparentBounds(true);
      this.matcher.useAnchoringBounds(false);
      this.useLocale(Locale.getDefault(Locale.Category.FORMAT));
   }

   public Scanner(Readable var1) {
      this((Readable)Objects.requireNonNull(var1, (String)"source"), WHITESPACE_PATTERN);
   }

   public Scanner(InputStream var1) {
      this((Readable)(new InputStreamReader(var1)), (Pattern)WHITESPACE_PATTERN);
   }

   public Scanner(InputStream var1, String var2) {
      this(makeReadable((InputStream)Objects.requireNonNull(var1, (String)"source"), toCharset(var2)), WHITESPACE_PATTERN);
   }

   private static Charset toCharset(String var0) {
      Objects.requireNonNull(var0, (String)"charsetName");

      try {
         return Charset.forName(var0);
      } catch (UnsupportedCharsetException | IllegalCharsetNameException var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   private static Readable makeReadable(InputStream var0, Charset var1) {
      return new InputStreamReader(var0, var1);
   }

   public Scanner(File var1) throws FileNotFoundException {
      this((ReadableByteChannel)(new FileInputStream(var1)).getChannel());
   }

   public Scanner(File var1, String var2) throws FileNotFoundException {
      this((File)Objects.requireNonNull(var1), toDecoder(var2));
   }

   private Scanner(File var1, CharsetDecoder var2) throws FileNotFoundException {
      this(makeReadable((ReadableByteChannel)(new FileInputStream(var1)).getChannel(), (CharsetDecoder)var2));
   }

   private static CharsetDecoder toDecoder(String var0) {
      Objects.requireNonNull(var0, (String)"charsetName");

      try {
         return Charset.forName(var0).newDecoder();
      } catch (UnsupportedCharsetException | IllegalCharsetNameException var2) {
         throw new IllegalArgumentException(var0);
      }
   }

   private static Readable makeReadable(ReadableByteChannel var0, CharsetDecoder var1) {
      return Channels.newReader(var0, var1, -1);
   }

   public Scanner(Path var1) throws IOException {
      this(Files.newInputStream(var1));
   }

   public Scanner(Path var1, String var2) throws IOException {
      this((Path)Objects.requireNonNull(var1), toCharset(var2));
   }

   private Scanner(Path var1, Charset var2) throws IOException {
      this(makeReadable(Files.newInputStream(var1), var2));
   }

   public Scanner(String var1) {
      this((Readable)(new StringReader(var1)), (Pattern)WHITESPACE_PATTERN);
   }

   public Scanner(ReadableByteChannel var1) {
      this(makeReadable((ReadableByteChannel)Objects.requireNonNull(var1, (String)"source")), WHITESPACE_PATTERN);
   }

   private static Readable makeReadable(ReadableByteChannel var0) {
      return makeReadable(var0, Charset.defaultCharset().newDecoder());
   }

   public Scanner(ReadableByteChannel var1, String var2) {
      this(makeReadable((ReadableByteChannel)Objects.requireNonNull(var1, (String)"source"), toDecoder(var2)), WHITESPACE_PATTERN);
   }

   private void saveState() {
      this.savedScannerPosition = this.position;
   }

   private void revertState() {
      this.position = this.savedScannerPosition;
      this.savedScannerPosition = -1;
      this.skipped = false;
   }

   private boolean revertState(boolean var1) {
      this.position = this.savedScannerPosition;
      this.savedScannerPosition = -1;
      this.skipped = false;
      return var1;
   }

   private void cacheResult() {
      this.hasNextResult = this.matcher.group();
      this.hasNextPosition = this.matcher.end();
      this.hasNextPattern = this.matcher.pattern();
   }

   private void cacheResult(String var1) {
      this.hasNextResult = var1;
      this.hasNextPosition = this.matcher.end();
      this.hasNextPattern = this.matcher.pattern();
   }

   private void clearCaches() {
      this.hasNextPattern = null;
      this.typeCache = null;
   }

   private String getCachedResult() {
      this.position = this.hasNextPosition;
      this.hasNextPattern = null;
      this.typeCache = null;
      return this.hasNextResult;
   }

   private void useTypeCache() {
      if (this.closed) {
         throw new IllegalStateException("Scanner closed");
      } else {
         this.position = this.hasNextPosition;
         this.hasNextPattern = null;
         this.typeCache = null;
      }
   }

   private void readInput() {
      if (this.buf.limit() == this.buf.capacity()) {
         this.makeSpace();
      }

      int var1 = this.buf.position();
      this.buf.position(this.buf.limit());
      this.buf.limit(this.buf.capacity());
      boolean var2 = false;

      int var5;
      try {
         var5 = this.source.read(this.buf);
      } catch (IOException var4) {
         this.lastException = var4;
         var5 = -1;
      }

      if (var5 == -1) {
         this.sourceClosed = true;
         this.needInput = false;
      }

      if (var5 > 0) {
         this.needInput = false;
      }

      this.buf.limit(this.buf.position());
      this.buf.position(var1);
   }

   private boolean makeSpace() {
      this.clearCaches();
      int var1 = this.savedScannerPosition == -1 ? this.position : this.savedScannerPosition;
      this.buf.position(var1);
      if (var1 > 0) {
         this.buf.compact();
         this.translateSavedIndexes(var1);
         this.position -= var1;
         this.buf.flip();
         return true;
      } else {
         int var2 = this.buf.capacity() * 2;
         CharBuffer var3 = CharBuffer.allocate(var2);
         var3.put(this.buf);
         var3.flip();
         this.translateSavedIndexes(var1);
         this.position -= var1;
         this.buf = var3;
         this.matcher.reset(this.buf);
         return true;
      }
   }

   private void translateSavedIndexes(int var1) {
      if (this.savedScannerPosition != -1) {
         this.savedScannerPosition -= var1;
      }

   }

   private void throwFor() {
      this.skipped = false;
      if (this.sourceClosed && this.position == this.buf.limit()) {
         throw new NoSuchElementException();
      } else {
         throw new InputMismatchException();
      }
   }

   private boolean hasTokenInBuffer() {
      this.matchValid = false;
      this.matcher.usePattern(this.delimPattern);
      this.matcher.region(this.position, this.buf.limit());
      if (this.matcher.lookingAt()) {
         this.position = this.matcher.end();
      }

      return this.position != this.buf.limit();
   }

   private String getCompleteTokenInBuffer(Pattern var1) {
      this.matchValid = false;
      this.matcher.usePattern(this.delimPattern);
      if (!this.skipped) {
         this.matcher.region(this.position, this.buf.limit());
         if (this.matcher.lookingAt()) {
            if (this.matcher.hitEnd() && !this.sourceClosed) {
               this.needInput = true;
               return null;
            }

            this.skipped = true;
            this.position = this.matcher.end();
         }
      }

      if (this.position == this.buf.limit()) {
         if (this.sourceClosed) {
            return null;
         } else {
            this.needInput = true;
            return null;
         }
      } else {
         this.matcher.region(this.position, this.buf.limit());
         boolean var2 = this.matcher.find();
         if (var2 && this.matcher.end() == this.position) {
            var2 = this.matcher.find();
         }

         if (var2) {
            if (this.matcher.requireEnd() && !this.sourceClosed) {
               this.needInput = true;
               return null;
            } else {
               int var5 = this.matcher.start();
               if (var1 == null) {
                  var1 = FIND_ANY_PATTERN;
               }

               this.matcher.usePattern(var1);
               this.matcher.region(this.position, var5);
               if (this.matcher.matches()) {
                  String var4 = this.matcher.group();
                  this.position = this.matcher.end();
                  return var4;
               } else {
                  return null;
               }
            }
         } else if (this.sourceClosed) {
            if (var1 == null) {
               var1 = FIND_ANY_PATTERN;
            }

            this.matcher.usePattern(var1);
            this.matcher.region(this.position, this.buf.limit());
            if (this.matcher.matches()) {
               String var3 = this.matcher.group();
               this.position = this.matcher.end();
               return var3;
            } else {
               return null;
            }
         } else {
            this.needInput = true;
            return null;
         }
      }
   }

   private String findPatternInBuffer(Pattern var1, int var2) {
      this.matchValid = false;
      this.matcher.usePattern(var1);
      int var3 = this.buf.limit();
      int var4 = -1;
      int var5 = var3;
      if (var2 > 0) {
         var4 = this.position + var2;
         if (var4 < var3) {
            var5 = var4;
         }
      }

      this.matcher.region(this.position, var5);
      if (this.matcher.find()) {
         if (this.matcher.hitEnd() && !this.sourceClosed) {
            if (var5 != var4) {
               this.needInput = true;
               return null;
            }

            if (var5 == var4 && this.matcher.requireEnd()) {
               this.needInput = true;
               return null;
            }
         }

         this.position = this.matcher.end();
         return this.matcher.group();
      } else if (this.sourceClosed) {
         return null;
      } else {
         if (var2 == 0 || var5 != var4) {
            this.needInput = true;
         }

         return null;
      }
   }

   private String matchPatternInBuffer(Pattern var1) {
      this.matchValid = false;
      this.matcher.usePattern(var1);
      this.matcher.region(this.position, this.buf.limit());
      if (this.matcher.lookingAt()) {
         if (this.matcher.hitEnd() && !this.sourceClosed) {
            this.needInput = true;
            return null;
         } else {
            this.position = this.matcher.end();
            return this.matcher.group();
         }
      } else if (this.sourceClosed) {
         return null;
      } else {
         this.needInput = true;
         return null;
      }
   }

   private void ensureOpen() {
      if (this.closed) {
         throw new IllegalStateException("Scanner closed");
      }
   }

   public void close() {
      if (!this.closed) {
         if (this.source instanceof Closeable) {
            try {
               ((Closeable)this.source).close();
            } catch (IOException var2) {
               this.lastException = var2;
            }
         }

         this.sourceClosed = true;
         this.source = null;
         this.closed = true;
      }
   }

   public IOException ioException() {
      return this.lastException;
   }

   public Pattern delimiter() {
      return this.delimPattern;
   }

   public Scanner useDelimiter(Pattern var1) {
      this.delimPattern = var1;
      return this;
   }

   public Scanner useDelimiter(String var1) {
      this.delimPattern = (Pattern)this.patternCache.forName(var1);
      return this;
   }

   public Locale locale() {
      return this.locale;
   }

   public Scanner useLocale(Locale var1) {
      if (var1.equals(this.locale)) {
         return this;
      } else {
         this.locale = var1;
         DecimalFormat var2 = (DecimalFormat)NumberFormat.getNumberInstance(var1);
         DecimalFormatSymbols var3 = DecimalFormatSymbols.getInstance(var1);
         this.groupSeparator = "\\" + var3.getGroupingSeparator();
         this.decimalSeparator = "\\" + var3.getDecimalSeparator();
         this.nanString = "\\Q" + var3.getNaN() + "\\E";
         this.infinityString = "\\Q" + var3.getInfinity() + "\\E";
         this.positivePrefix = var2.getPositivePrefix();
         if (this.positivePrefix.length() > 0) {
            this.positivePrefix = "\\Q" + this.positivePrefix + "\\E";
         }

         this.negativePrefix = var2.getNegativePrefix();
         if (this.negativePrefix.length() > 0) {
            this.negativePrefix = "\\Q" + this.negativePrefix + "\\E";
         }

         this.positiveSuffix = var2.getPositiveSuffix();
         if (this.positiveSuffix.length() > 0) {
            this.positiveSuffix = "\\Q" + this.positiveSuffix + "\\E";
         }

         this.negativeSuffix = var2.getNegativeSuffix();
         if (this.negativeSuffix.length() > 0) {
            this.negativeSuffix = "\\Q" + this.negativeSuffix + "\\E";
         }

         this.integerPattern = null;
         this.floatPattern = null;
         return this;
      }
   }

   public int radix() {
      return this.defaultRadix;
   }

   public Scanner useRadix(int var1) {
      if (var1 >= 2 && var1 <= 36) {
         if (this.defaultRadix == var1) {
            return this;
         } else {
            this.defaultRadix = var1;
            this.integerPattern = null;
            return this;
         }
      } else {
         throw new IllegalArgumentException("radix:" + var1);
      }
   }

   private void setRadix(int var1) {
      if (this.radix != var1) {
         this.integerPattern = null;
         this.radix = var1;
      }

   }

   public MatchResult match() {
      if (!this.matchValid) {
         throw new IllegalStateException("No match result available");
      } else {
         return this.matcher.toMatchResult();
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("java.util.Scanner");
      var1.append("[delimiters=" + this.delimPattern + "]");
      var1.append("[position=" + this.position + "]");
      var1.append("[match valid=" + this.matchValid + "]");
      var1.append("[need input=" + this.needInput + "]");
      var1.append("[source closed=" + this.sourceClosed + "]");
      var1.append("[skipped=" + this.skipped + "]");
      var1.append("[group separator=" + this.groupSeparator + "]");
      var1.append("[decimal separator=" + this.decimalSeparator + "]");
      var1.append("[positive prefix=" + this.positivePrefix + "]");
      var1.append("[negative prefix=" + this.negativePrefix + "]");
      var1.append("[positive suffix=" + this.positiveSuffix + "]");
      var1.append("[negative suffix=" + this.negativeSuffix + "]");
      var1.append("[NaN string=" + this.nanString + "]");
      var1.append("[infinity string=" + this.infinityString + "]");
      return var1.toString();
   }

   public boolean hasNext() {
      this.ensureOpen();
      this.saveState();

      while(!this.sourceClosed) {
         if (this.hasTokenInBuffer()) {
            return this.revertState(true);
         }

         this.readInput();
      }

      boolean var1 = this.hasTokenInBuffer();
      return this.revertState(var1);
   }

   public String next() {
      this.ensureOpen();
      this.clearCaches();

      while(true) {
         String var1 = this.getCompleteTokenInBuffer((Pattern)null);
         if (var1 != null) {
            this.matchValid = true;
            this.skipped = false;
            return var1;
         }

         if (this.needInput) {
            this.readInput();
         } else {
            this.throwFor();
         }
      }
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }

   public boolean hasNext(String var1) {
      return this.hasNext((Pattern)this.patternCache.forName(var1));
   }

   public String next(String var1) {
      return this.next((Pattern)this.patternCache.forName(var1));
   }

   public boolean hasNext(Pattern var1) {
      this.ensureOpen();
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.hasNextPattern = null;
         this.saveState();

         while(this.getCompleteTokenInBuffer(var1) == null) {
            if (!this.needInput) {
               return this.revertState(false);
            }

            this.readInput();
         }

         this.matchValid = true;
         this.cacheResult();
         return this.revertState(true);
      }
   }

   public String next(Pattern var1) {
      this.ensureOpen();
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.hasNextPattern == var1) {
         return this.getCachedResult();
      } else {
         this.clearCaches();

         while(true) {
            String var2 = this.getCompleteTokenInBuffer(var1);
            if (var2 != null) {
               this.matchValid = true;
               this.skipped = false;
               return var2;
            }

            if (this.needInput) {
               this.readInput();
            } else {
               this.throwFor();
            }
         }
      }
   }

   public boolean hasNextLine() {
      this.saveState();
      String var1 = this.findWithinHorizon((Pattern)linePattern(), 0);
      if (var1 != null) {
         MatchResult var2 = this.match();
         String var3 = var2.group(1);
         if (var3 != null) {
            var1 = var1.substring(0, var1.length() - var3.length());
            this.cacheResult(var1);
         } else {
            this.cacheResult();
         }
      }

      this.revertState();
      return var1 != null;
   }

   public String nextLine() {
      if (this.hasNextPattern == linePattern()) {
         return this.getCachedResult();
      } else {
         this.clearCaches();
         String var1 = this.findWithinHorizon((Pattern)linePattern, 0);
         if (var1 == null) {
            throw new NoSuchElementException("No line found");
         } else {
            MatchResult var2 = this.match();
            String var3 = var2.group(1);
            if (var3 != null) {
               var1 = var1.substring(0, var1.length() - var3.length());
            }

            if (var1 == null) {
               throw new NoSuchElementException();
            } else {
               return var1;
            }
         }
      }
   }

   public String findInLine(String var1) {
      return this.findInLine((Pattern)this.patternCache.forName(var1));
   }

   public String findInLine(Pattern var1) {
      this.ensureOpen();
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.clearCaches();
         boolean var2 = false;
         this.saveState();

         int var4;
         while(true) {
            String var3 = this.findPatternInBuffer(separatorPattern(), 0);
            if (var3 != null) {
               var4 = this.matcher.start();
               break;
            }

            if (!this.needInput) {
               var4 = this.buf.limit();
               break;
            }

            this.readInput();
         }

         this.revertState();
         int var5 = var4 - this.position;
         return var5 == 0 ? null : this.findWithinHorizon(var1, var5);
      }
   }

   public String findWithinHorizon(String var1, int var2) {
      return this.findWithinHorizon((Pattern)this.patternCache.forName(var1), var2);
   }

   public String findWithinHorizon(Pattern var1, int var2) {
      this.ensureOpen();
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 < 0) {
         throw new IllegalArgumentException("horizon < 0");
      } else {
         this.clearCaches();

         while(true) {
            String var3 = this.findPatternInBuffer(var1, var2);
            if (var3 != null) {
               this.matchValid = true;
               return var3;
            }

            if (!this.needInput) {
               return null;
            }

            this.readInput();
         }
      }
   }

   public Scanner skip(Pattern var1) {
      this.ensureOpen();
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.clearCaches();

         while(true) {
            String var2 = this.matchPatternInBuffer(var1);
            if (var2 != null) {
               this.matchValid = true;
               this.position = this.matcher.end();
               return this;
            }

            if (!this.needInput) {
               throw new NoSuchElementException();
            }

            this.readInput();
         }
      }
   }

   public Scanner skip(String var1) {
      return this.skip((Pattern)this.patternCache.forName(var1));
   }

   public boolean hasNextBoolean() {
      return this.hasNext(boolPattern());
   }

   public boolean nextBoolean() {
      this.clearCaches();
      return Boolean.parseBoolean(this.next(boolPattern()));
   }

   public boolean hasNextByte() {
      return this.hasNextByte(this.defaultRadix);
   }

   public boolean hasNextByte(int var1) {
      this.setRadix(var1);
      boolean var2 = this.hasNext(this.integerPattern());
      if (var2) {
         try {
            String var3 = this.matcher.group(this.SIMPLE_GROUP_INDEX) == null ? this.processIntegerToken(this.hasNextResult) : this.hasNextResult;
            this.typeCache = Byte.parseByte(var3, var1);
         } catch (NumberFormatException var4) {
            var2 = false;
         }
      }

      return var2;
   }

   public byte nextByte() {
      return this.nextByte(this.defaultRadix);
   }

   public byte nextByte(int var1) {
      if (this.typeCache != null && this.typeCache instanceof Byte && this.radix == var1) {
         byte var4 = (Byte)this.typeCache;
         this.useTypeCache();
         return var4;
      } else {
         this.setRadix(var1);
         this.clearCaches();

         try {
            String var2 = this.next(this.integerPattern());
            if (this.matcher.group(this.SIMPLE_GROUP_INDEX) == null) {
               var2 = this.processIntegerToken(var2);
            }

            return Byte.parseByte(var2, var1);
         } catch (NumberFormatException var3) {
            this.position = this.matcher.start();
            throw new InputMismatchException(var3.getMessage());
         }
      }
   }

   public boolean hasNextShort() {
      return this.hasNextShort(this.defaultRadix);
   }

   public boolean hasNextShort(int var1) {
      this.setRadix(var1);
      boolean var2 = this.hasNext(this.integerPattern());
      if (var2) {
         try {
            String var3 = this.matcher.group(this.SIMPLE_GROUP_INDEX) == null ? this.processIntegerToken(this.hasNextResult) : this.hasNextResult;
            this.typeCache = Short.parseShort(var3, var1);
         } catch (NumberFormatException var4) {
            var2 = false;
         }
      }

      return var2;
   }

   public short nextShort() {
      return this.nextShort(this.defaultRadix);
   }

   public short nextShort(int var1) {
      if (this.typeCache != null && this.typeCache instanceof Short && this.radix == var1) {
         short var4 = (Short)this.typeCache;
         this.useTypeCache();
         return var4;
      } else {
         this.setRadix(var1);
         this.clearCaches();

         try {
            String var2 = this.next(this.integerPattern());
            if (this.matcher.group(this.SIMPLE_GROUP_INDEX) == null) {
               var2 = this.processIntegerToken(var2);
            }

            return Short.parseShort(var2, var1);
         } catch (NumberFormatException var3) {
            this.position = this.matcher.start();
            throw new InputMismatchException(var3.getMessage());
         }
      }
   }

   public boolean hasNextInt() {
      return this.hasNextInt(this.defaultRadix);
   }

   public boolean hasNextInt(int var1) {
      this.setRadix(var1);
      boolean var2 = this.hasNext(this.integerPattern());
      if (var2) {
         try {
            String var3 = this.matcher.group(this.SIMPLE_GROUP_INDEX) == null ? this.processIntegerToken(this.hasNextResult) : this.hasNextResult;
            this.typeCache = Integer.parseInt(var3, var1);
         } catch (NumberFormatException var4) {
            var2 = false;
         }
      }

      return var2;
   }

   private String processIntegerToken(String var1) {
      String var2 = var1.replaceAll("" + this.groupSeparator, "");
      boolean var3 = false;
      int var4 = this.negativePrefix.length();
      if (var4 > 0 && var2.startsWith(this.negativePrefix)) {
         var3 = true;
         var2 = var2.substring(var4);
      }

      int var5 = this.negativeSuffix.length();
      if (var5 > 0 && var2.endsWith(this.negativeSuffix)) {
         var3 = true;
         var2 = var2.substring(var2.length() - var5, var2.length());
      }

      if (var3) {
         var2 = "-" + var2;
      }

      return var2;
   }

   public int nextInt() {
      return this.nextInt(this.defaultRadix);
   }

   public int nextInt(int var1) {
      if (this.typeCache != null && this.typeCache instanceof Integer && this.radix == var1) {
         int var4 = (Integer)this.typeCache;
         this.useTypeCache();
         return var4;
      } else {
         this.setRadix(var1);
         this.clearCaches();

         try {
            String var2 = this.next(this.integerPattern());
            if (this.matcher.group(this.SIMPLE_GROUP_INDEX) == null) {
               var2 = this.processIntegerToken(var2);
            }

            return Integer.parseInt(var2, var1);
         } catch (NumberFormatException var3) {
            this.position = this.matcher.start();
            throw new InputMismatchException(var3.getMessage());
         }
      }
   }

   public boolean hasNextLong() {
      return this.hasNextLong(this.defaultRadix);
   }

   public boolean hasNextLong(int var1) {
      this.setRadix(var1);
      boolean var2 = this.hasNext(this.integerPattern());
      if (var2) {
         try {
            String var3 = this.matcher.group(this.SIMPLE_GROUP_INDEX) == null ? this.processIntegerToken(this.hasNextResult) : this.hasNextResult;
            this.typeCache = Long.parseLong(var3, var1);
         } catch (NumberFormatException var4) {
            var2 = false;
         }
      }

      return var2;
   }

   public long nextLong() {
      return this.nextLong(this.defaultRadix);
   }

   public long nextLong(int var1) {
      if (this.typeCache != null && this.typeCache instanceof Long && this.radix == var1) {
         long var5 = (Long)this.typeCache;
         this.useTypeCache();
         return var5;
      } else {
         this.setRadix(var1);
         this.clearCaches();

         try {
            String var2 = this.next(this.integerPattern());
            if (this.matcher.group(this.SIMPLE_GROUP_INDEX) == null) {
               var2 = this.processIntegerToken(var2);
            }

            return Long.parseLong(var2, var1);
         } catch (NumberFormatException var4) {
            this.position = this.matcher.start();
            throw new InputMismatchException(var4.getMessage());
         }
      }
   }

   private String processFloatToken(String var1) {
      String var2 = var1.replaceAll(this.groupSeparator, "");
      if (!this.decimalSeparator.equals("\\.")) {
         var2 = var2.replaceAll(this.decimalSeparator, ".");
      }

      boolean var3 = false;
      int var4 = this.negativePrefix.length();
      if (var4 > 0 && var2.startsWith(this.negativePrefix)) {
         var3 = true;
         var2 = var2.substring(var4);
      }

      int var5 = this.negativeSuffix.length();
      if (var5 > 0 && var2.endsWith(this.negativeSuffix)) {
         var3 = true;
         var2 = var2.substring(var2.length() - var5, var2.length());
      }

      if (var2.equals(this.nanString)) {
         var2 = "NaN";
      }

      if (var2.equals(this.infinityString)) {
         var2 = "Infinity";
      }

      if (var3) {
         var2 = "-" + var2;
      }

      Matcher var6 = NON_ASCII_DIGIT.matcher(var2);
      if (var6.find()) {
         StringBuilder var7 = new StringBuilder();

         for(int var8 = 0; var8 < var2.length(); ++var8) {
            char var9 = var2.charAt(var8);
            if (Character.isDigit(var9)) {
               int var10 = Character.digit((char)var9, 10);
               if (var10 != -1) {
                  var7.append(var10);
               } else {
                  var7.append(var9);
               }
            } else {
               var7.append(var9);
            }
         }

         var2 = var7.toString();
      }

      return var2;
   }

   public boolean hasNextFloat() {
      this.setRadix(10);
      boolean var1 = this.hasNext(this.floatPattern());
      if (var1) {
         try {
            String var2 = this.processFloatToken(this.hasNextResult);
            this.typeCache = Float.parseFloat(var2);
         } catch (NumberFormatException var3) {
            var1 = false;
         }
      }

      return var1;
   }

   public float nextFloat() {
      if (this.typeCache != null && this.typeCache instanceof Float) {
         float var1 = (Float)this.typeCache;
         this.useTypeCache();
         return var1;
      } else {
         this.setRadix(10);
         this.clearCaches();

         try {
            return Float.parseFloat(this.processFloatToken(this.next(this.floatPattern())));
         } catch (NumberFormatException var2) {
            this.position = this.matcher.start();
            throw new InputMismatchException(var2.getMessage());
         }
      }
   }

   public boolean hasNextDouble() {
      this.setRadix(10);
      boolean var1 = this.hasNext(this.floatPattern());
      if (var1) {
         try {
            String var2 = this.processFloatToken(this.hasNextResult);
            this.typeCache = Double.parseDouble(var2);
         } catch (NumberFormatException var3) {
            var1 = false;
         }
      }

      return var1;
   }

   public double nextDouble() {
      if (this.typeCache != null && this.typeCache instanceof Double) {
         double var1 = (Double)this.typeCache;
         this.useTypeCache();
         return var1;
      } else {
         this.setRadix(10);
         this.clearCaches();

         try {
            return Double.parseDouble(this.processFloatToken(this.next(this.floatPattern())));
         } catch (NumberFormatException var3) {
            this.position = this.matcher.start();
            throw new InputMismatchException(var3.getMessage());
         }
      }
   }

   public boolean hasNextBigInteger() {
      return this.hasNextBigInteger(this.defaultRadix);
   }

   public boolean hasNextBigInteger(int var1) {
      this.setRadix(var1);
      boolean var2 = this.hasNext(this.integerPattern());
      if (var2) {
         try {
            String var3 = this.matcher.group(this.SIMPLE_GROUP_INDEX) == null ? this.processIntegerToken(this.hasNextResult) : this.hasNextResult;
            this.typeCache = new BigInteger(var3, var1);
         } catch (NumberFormatException var4) {
            var2 = false;
         }
      }

      return var2;
   }

   public BigInteger nextBigInteger() {
      return this.nextBigInteger(this.defaultRadix);
   }

   public BigInteger nextBigInteger(int var1) {
      if (this.typeCache != null && this.typeCache instanceof BigInteger && this.radix == var1) {
         BigInteger var4 = (BigInteger)this.typeCache;
         this.useTypeCache();
         return var4;
      } else {
         this.setRadix(var1);
         this.clearCaches();

         try {
            String var2 = this.next(this.integerPattern());
            if (this.matcher.group(this.SIMPLE_GROUP_INDEX) == null) {
               var2 = this.processIntegerToken(var2);
            }

            return new BigInteger(var2, var1);
         } catch (NumberFormatException var3) {
            this.position = this.matcher.start();
            throw new InputMismatchException(var3.getMessage());
         }
      }
   }

   public boolean hasNextBigDecimal() {
      this.setRadix(10);
      boolean var1 = this.hasNext(this.decimalPattern());
      if (var1) {
         try {
            String var2 = this.processFloatToken(this.hasNextResult);
            this.typeCache = new BigDecimal(var2);
         } catch (NumberFormatException var3) {
            var1 = false;
         }
      }

      return var1;
   }

   public BigDecimal nextBigDecimal() {
      if (this.typeCache != null && this.typeCache instanceof BigDecimal) {
         BigDecimal var3 = (BigDecimal)this.typeCache;
         this.useTypeCache();
         return var3;
      } else {
         this.setRadix(10);
         this.clearCaches();

         try {
            String var1 = this.processFloatToken(this.next(this.decimalPattern()));
            return new BigDecimal(var1);
         } catch (NumberFormatException var2) {
            this.position = this.matcher.start();
            throw new InputMismatchException(var2.getMessage());
         }
      }
   }

   public Scanner reset() {
      this.delimPattern = WHITESPACE_PATTERN;
      this.useLocale(Locale.getDefault(Locale.Category.FORMAT));
      this.useRadix(10);
      this.clearCaches();
      return this;
   }
}
