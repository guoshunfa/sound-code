package javax.xml.bind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

final class DatatypeConverterImpl implements DatatypeConverterInterface {
   public static final DatatypeConverterInterface theInstance = new DatatypeConverterImpl();
   private static final char[] hexCode = "0123456789ABCDEF".toCharArray();
   private static final byte[] decodeMap = initDecodeMap();
   private static final byte PADDING = 127;
   private static final char[] encodeMap = initEncodeMap();
   private static final DatatypeFactory datatypeFactory;

   protected DatatypeConverterImpl() {
   }

   public String parseString(String lexicalXSDString) {
      return lexicalXSDString;
   }

   public BigInteger parseInteger(String lexicalXSDInteger) {
      return _parseInteger(lexicalXSDInteger);
   }

   public static BigInteger _parseInteger(CharSequence s) {
      return new BigInteger(removeOptionalPlus(WhiteSpaceProcessor.trim(s)).toString());
   }

   public String printInteger(BigInteger val) {
      return _printInteger(val);
   }

   public static String _printInteger(BigInteger val) {
      return val.toString();
   }

   public int parseInt(String s) {
      return _parseInt(s);
   }

   public static int _parseInt(CharSequence s) {
      int len = s.length();
      int sign = 1;
      int r = 0;

      for(int i = 0; i < len; ++i) {
         char ch = s.charAt(i);
         if (!WhiteSpaceProcessor.isWhiteSpace(ch)) {
            if ('0' <= ch && ch <= '9') {
               r = r * 10 + (ch - 48);
            } else if (ch == '-') {
               sign = -1;
            } else if (ch != '+') {
               throw new NumberFormatException("Not a number: " + s);
            }
         }
      }

      return r * sign;
   }

   public long parseLong(String lexicalXSLong) {
      return _parseLong(lexicalXSLong);
   }

   public static long _parseLong(CharSequence s) {
      return Long.valueOf(removeOptionalPlus(WhiteSpaceProcessor.trim(s)).toString());
   }

   public short parseShort(String lexicalXSDShort) {
      return _parseShort(lexicalXSDShort);
   }

   public static short _parseShort(CharSequence s) {
      return (short)_parseInt(s);
   }

   public String printShort(short val) {
      return _printShort(val);
   }

   public static String _printShort(short val) {
      return String.valueOf((int)val);
   }

   public BigDecimal parseDecimal(String content) {
      return _parseDecimal(content);
   }

   public static BigDecimal _parseDecimal(CharSequence content) {
      content = WhiteSpaceProcessor.trim(content);
      return content.length() <= 0 ? null : new BigDecimal(content.toString());
   }

   public float parseFloat(String lexicalXSDFloat) {
      return _parseFloat(lexicalXSDFloat);
   }

   public static float _parseFloat(CharSequence _val) {
      String s = WhiteSpaceProcessor.trim(_val).toString();
      if (s.equals("NaN")) {
         return Float.NaN;
      } else if (s.equals("INF")) {
         return Float.POSITIVE_INFINITY;
      } else if (s.equals("-INF")) {
         return Float.NEGATIVE_INFINITY;
      } else if (s.length() != 0 && isDigitOrPeriodOrSign(s.charAt(0)) && isDigitOrPeriodOrSign(s.charAt(s.length() - 1))) {
         return Float.parseFloat(s);
      } else {
         throw new NumberFormatException();
      }
   }

   public String printFloat(float v) {
      return _printFloat(v);
   }

   public static String _printFloat(float v) {
      if (Float.isNaN(v)) {
         return "NaN";
      } else if (v == Float.POSITIVE_INFINITY) {
         return "INF";
      } else {
         return v == Float.NEGATIVE_INFINITY ? "-INF" : String.valueOf(v);
      }
   }

   public double parseDouble(String lexicalXSDDouble) {
      return _parseDouble(lexicalXSDDouble);
   }

   public static double _parseDouble(CharSequence _val) {
      String val = WhiteSpaceProcessor.trim(_val).toString();
      if (val.equals("NaN")) {
         return Double.NaN;
      } else if (val.equals("INF")) {
         return Double.POSITIVE_INFINITY;
      } else if (val.equals("-INF")) {
         return Double.NEGATIVE_INFINITY;
      } else if (val.length() != 0 && isDigitOrPeriodOrSign(val.charAt(0)) && isDigitOrPeriodOrSign(val.charAt(val.length() - 1))) {
         return Double.parseDouble(val);
      } else {
         throw new NumberFormatException(val);
      }
   }

   public boolean parseBoolean(String lexicalXSDBoolean) {
      Boolean b = _parseBoolean(lexicalXSDBoolean);
      return b == null ? false : b;
   }

   public static Boolean _parseBoolean(CharSequence literal) {
      if (literal == null) {
         return null;
      } else {
         int i = 0;
         int len = literal.length();
         boolean value = false;
         if (literal.length() <= 0) {
            return null;
         } else {
            char ch;
            do {
               ch = literal.charAt(i++);
            } while(WhiteSpaceProcessor.isWhiteSpace(ch) && i < len);

            int strIndex = 0;
            switch(ch) {
            case '0':
               value = false;
               break;
            case '1':
               value = true;
               break;
            case 'f':
               String strFalse = "alse";

               do {
                  ch = literal.charAt(i++);
               } while(strFalse.charAt(strIndex++) == ch && i < len && strIndex < 4);

               if (strIndex != 4) {
                  return false;
               }

               value = false;
               break;
            case 't':
               String strTrue = "rue";

               do {
                  ch = literal.charAt(i++);
               } while(strTrue.charAt(strIndex++) == ch && i < len && strIndex < 3);

               if (strIndex != 3) {
                  return false;
               }

               value = true;
            }

            if (i < len) {
               do {
                  ch = literal.charAt(i++);
               } while(WhiteSpaceProcessor.isWhiteSpace(ch) && i < len);
            }

            return i == len ? value : null;
         }
      }
   }

   public String printBoolean(boolean val) {
      return val ? "true" : "false";
   }

   public static String _printBoolean(boolean val) {
      return val ? "true" : "false";
   }

   public byte parseByte(String lexicalXSDByte) {
      return _parseByte(lexicalXSDByte);
   }

   public static byte _parseByte(CharSequence literal) {
      return (byte)_parseInt(literal);
   }

   public String printByte(byte val) {
      return _printByte(val);
   }

   public static String _printByte(byte val) {
      return String.valueOf((int)val);
   }

   public QName parseQName(String lexicalXSDQName, NamespaceContext nsc) {
      return _parseQName(lexicalXSDQName, nsc);
   }

   public static QName _parseQName(CharSequence text, NamespaceContext nsc) {
      int length = text.length();

      int start;
      for(start = 0; start < length && WhiteSpaceProcessor.isWhiteSpace(text.charAt(start)); ++start) {
      }

      int end;
      for(end = length; end > start && WhiteSpaceProcessor.isWhiteSpace(text.charAt(end - 1)); --end) {
      }

      if (end == start) {
         throw new IllegalArgumentException("input is empty");
      } else {
         int idx;
         for(idx = start + 1; idx < end && text.charAt(idx) != ':'; ++idx) {
         }

         String uri;
         String localPart;
         String prefix;
         if (idx == end) {
            uri = nsc.getNamespaceURI("");
            localPart = text.subSequence(start, end).toString();
            prefix = "";
         } else {
            prefix = text.subSequence(start, idx).toString();
            localPart = text.subSequence(idx + 1, end).toString();
            uri = nsc.getNamespaceURI(prefix);
            if (uri == null || uri.length() == 0) {
               throw new IllegalArgumentException("prefix " + prefix + " is not bound to a namespace");
            }
         }

         return new QName(uri, localPart, prefix);
      }
   }

   public Calendar parseDateTime(String lexicalXSDDateTime) {
      return _parseDateTime(lexicalXSDDateTime);
   }

   public static GregorianCalendar _parseDateTime(CharSequence s) {
      String val = WhiteSpaceProcessor.trim(s).toString();
      return datatypeFactory.newXMLGregorianCalendar(val).toGregorianCalendar();
   }

   public String printDateTime(Calendar val) {
      return _printDateTime(val);
   }

   public static String _printDateTime(Calendar val) {
      return DatatypeConverterImpl.CalendarFormatter.doFormat("%Y-%M-%DT%h:%m:%s%z", val);
   }

   public byte[] parseBase64Binary(String lexicalXSDBase64Binary) {
      return _parseBase64Binary(lexicalXSDBase64Binary);
   }

   public byte[] parseHexBinary(String s) {
      int len = s.length();
      if (len % 2 != 0) {
         throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
      } else {
         byte[] out = new byte[len / 2];

         for(int i = 0; i < len; i += 2) {
            int h = hexToBin(s.charAt(i));
            int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1) {
               throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);
            }

            out[i / 2] = (byte)(h * 16 + l);
         }

         return out;
      }
   }

   private static int hexToBin(char ch) {
      if ('0' <= ch && ch <= '9') {
         return ch - 48;
      } else if ('A' <= ch && ch <= 'F') {
         return ch - 65 + 10;
      } else {
         return 'a' <= ch && ch <= 'f' ? ch - 97 + 10 : -1;
      }
   }

   public String printHexBinary(byte[] data) {
      StringBuilder r = new StringBuilder(data.length * 2);
      byte[] var3 = data;
      int var4 = data.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         byte b = var3[var5];
         r.append(hexCode[b >> 4 & 15]);
         r.append(hexCode[b & 15]);
      }

      return r.toString();
   }

   public long parseUnsignedInt(String lexicalXSDUnsignedInt) {
      return _parseLong(lexicalXSDUnsignedInt);
   }

   public String printUnsignedInt(long val) {
      return _printLong(val);
   }

   public int parseUnsignedShort(String lexicalXSDUnsignedShort) {
      return _parseInt(lexicalXSDUnsignedShort);
   }

   public Calendar parseTime(String lexicalXSDTime) {
      return datatypeFactory.newXMLGregorianCalendar(lexicalXSDTime).toGregorianCalendar();
   }

   public String printTime(Calendar val) {
      return DatatypeConverterImpl.CalendarFormatter.doFormat("%h:%m:%s%z", val);
   }

   public Calendar parseDate(String lexicalXSDDate) {
      return datatypeFactory.newXMLGregorianCalendar(lexicalXSDDate).toGregorianCalendar();
   }

   public String printDate(Calendar val) {
      return _printDate(val);
   }

   public static String _printDate(Calendar val) {
      return DatatypeConverterImpl.CalendarFormatter.doFormat("%Y-%M-%D" + "%z", val);
   }

   public String parseAnySimpleType(String lexicalXSDAnySimpleType) {
      return lexicalXSDAnySimpleType;
   }

   public String printString(String val) {
      return val;
   }

   public String printInt(int val) {
      return _printInt(val);
   }

   public static String _printInt(int val) {
      return String.valueOf(val);
   }

   public String printLong(long val) {
      return _printLong(val);
   }

   public static String _printLong(long val) {
      return String.valueOf(val);
   }

   public String printDecimal(BigDecimal val) {
      return _printDecimal(val);
   }

   public static String _printDecimal(BigDecimal val) {
      return val.toPlainString();
   }

   public String printDouble(double v) {
      return _printDouble(v);
   }

   public static String _printDouble(double v) {
      if (Double.isNaN(v)) {
         return "NaN";
      } else if (v == Double.POSITIVE_INFINITY) {
         return "INF";
      } else {
         return v == Double.NEGATIVE_INFINITY ? "-INF" : String.valueOf(v);
      }
   }

   public String printQName(QName val, NamespaceContext nsc) {
      return _printQName(val, nsc);
   }

   public static String _printQName(QName val, NamespaceContext nsc) {
      String prefix = nsc.getPrefix(val.getNamespaceURI());
      String localPart = val.getLocalPart();
      String qname;
      if (prefix != null && prefix.length() != 0) {
         qname = prefix + ':' + localPart;
      } else {
         qname = localPart;
      }

      return qname;
   }

   public String printBase64Binary(byte[] val) {
      return _printBase64Binary(val);
   }

   public String printUnsignedShort(int val) {
      return String.valueOf(val);
   }

   public String printAnySimpleType(String val) {
      return val;
   }

   public static String installHook(String s) {
      DatatypeConverter.setDatatypeConverter(theInstance);
      return s;
   }

   private static byte[] initDecodeMap() {
      byte[] map = new byte[128];

      int i;
      for(i = 0; i < 128; ++i) {
         map[i] = -1;
      }

      for(i = 65; i <= 90; ++i) {
         map[i] = (byte)(i - 65);
      }

      for(i = 97; i <= 122; ++i) {
         map[i] = (byte)(i - 97 + 26);
      }

      for(i = 48; i <= 57; ++i) {
         map[i] = (byte)(i - 48 + 52);
      }

      map[43] = 62;
      map[47] = 63;
      map[61] = 127;
      return map;
   }

   private static int guessLength(String text) {
      int len = text.length();

      int j;
      for(j = len - 1; j >= 0; --j) {
         byte code = decodeMap[text.charAt(j)];
         if (code != 127) {
            if (code == -1) {
               return text.length() / 4 * 3;
            }
            break;
         }
      }

      ++j;
      int padSize = len - j;
      return padSize > 2 ? text.length() / 4 * 3 : text.length() / 4 * 3 - padSize;
   }

   public static byte[] _parseBase64Binary(String text) {
      int buflen = guessLength(text);
      byte[] out = new byte[buflen];
      int o = 0;
      int len = text.length();
      byte[] quadruplet = new byte[4];
      int q = 0;

      for(int i = 0; i < len; ++i) {
         char ch = text.charAt(i);
         byte v = decodeMap[ch];
         if (v != -1) {
            quadruplet[q++] = v;
         }

         if (q == 4) {
            out[o++] = (byte)(quadruplet[0] << 2 | quadruplet[1] >> 4);
            if (quadruplet[2] != 127) {
               out[o++] = (byte)(quadruplet[1] << 4 | quadruplet[2] >> 2);
            }

            if (quadruplet[3] != 127) {
               out[o++] = (byte)(quadruplet[2] << 6 | quadruplet[3]);
            }

            q = 0;
         }
      }

      if (buflen == o) {
         return out;
      } else {
         byte[] nb = new byte[o];
         System.arraycopy(out, 0, nb, 0, o);
         return nb;
      }
   }

   private static char[] initEncodeMap() {
      char[] map = new char[64];

      int i;
      for(i = 0; i < 26; ++i) {
         map[i] = (char)(65 + i);
      }

      for(i = 26; i < 52; ++i) {
         map[i] = (char)(97 + (i - 26));
      }

      for(i = 52; i < 62; ++i) {
         map[i] = (char)(48 + (i - 52));
      }

      map[62] = '+';
      map[63] = '/';
      return map;
   }

   public static char encode(int i) {
      return encodeMap[i & 63];
   }

   public static byte encodeByte(int i) {
      return (byte)encodeMap[i & 63];
   }

   public static String _printBase64Binary(byte[] input) {
      return _printBase64Binary(input, 0, input.length);
   }

   public static String _printBase64Binary(byte[] input, int offset, int len) {
      char[] buf = new char[(len + 2) / 3 * 4];
      int ptr = _printBase64Binary(input, offset, len, (char[])buf, 0);

      assert ptr == buf.length;

      return new String(buf);
   }

   public static int _printBase64Binary(byte[] input, int offset, int len, char[] buf, int ptr) {
      int remaining = len;

      int i;
      for(i = offset; remaining >= 3; i += 3) {
         buf[ptr++] = encode(input[i] >> 2);
         buf[ptr++] = encode((input[i] & 3) << 4 | input[i + 1] >> 4 & 15);
         buf[ptr++] = encode((input[i + 1] & 15) << 2 | input[i + 2] >> 6 & 3);
         buf[ptr++] = encode(input[i + 2] & 63);
         remaining -= 3;
      }

      if (remaining == 1) {
         buf[ptr++] = encode(input[i] >> 2);
         buf[ptr++] = encode((input[i] & 3) << 4);
         buf[ptr++] = '=';
         buf[ptr++] = '=';
      }

      if (remaining == 2) {
         buf[ptr++] = encode(input[i] >> 2);
         buf[ptr++] = encode((input[i] & 3) << 4 | input[i + 1] >> 4 & 15);
         buf[ptr++] = encode((input[i + 1] & 15) << 2);
         buf[ptr++] = '=';
      }

      return ptr;
   }

   public static int _printBase64Binary(byte[] input, int offset, int len, byte[] out, int ptr) {
      byte[] buf = out;
      int remaining = len;

      int i;
      for(i = offset; remaining >= 3; i += 3) {
         buf[ptr++] = encodeByte(input[i] >> 2);
         buf[ptr++] = encodeByte((input[i] & 3) << 4 | input[i + 1] >> 4 & 15);
         buf[ptr++] = encodeByte((input[i + 1] & 15) << 2 | input[i + 2] >> 6 & 3);
         buf[ptr++] = encodeByte(input[i + 2] & 63);
         remaining -= 3;
      }

      if (remaining == 1) {
         buf[ptr++] = encodeByte(input[i] >> 2);
         buf[ptr++] = encodeByte((input[i] & 3) << 4);
         buf[ptr++] = 61;
         buf[ptr++] = 61;
      }

      if (remaining == 2) {
         buf[ptr++] = encodeByte(input[i] >> 2);
         buf[ptr++] = encodeByte((input[i] & 3) << 4 | input[i + 1] >> 4 & 15);
         buf[ptr++] = encodeByte((input[i + 1] & 15) << 2);
         buf[ptr++] = 61;
      }

      return ptr;
   }

   private static CharSequence removeOptionalPlus(CharSequence s) {
      int len = s.length();
      if (len > 1 && s.charAt(0) == '+') {
         s = s.subSequence(1, len);
         char ch = s.charAt(0);
         if ('0' <= ch && ch <= '9') {
            return s;
         } else if ('.' == ch) {
            return s;
         } else {
            throw new NumberFormatException();
         }
      } else {
         return s;
      }
   }

   private static boolean isDigitOrPeriodOrSign(char ch) {
      if ('0' <= ch && ch <= '9') {
         return true;
      } else {
         return ch == '+' || ch == '-' || ch == '.';
      }
   }

   static {
      try {
         datatypeFactory = DatatypeFactory.newInstance();
      } catch (DatatypeConfigurationException var1) {
         throw new Error(var1);
      }
   }

   private static final class CalendarFormatter {
      public static String doFormat(String format, Calendar cal) throws IllegalArgumentException {
         int fidx = 0;
         int flen = format.length();
         StringBuilder buf = new StringBuilder();

         while(fidx < flen) {
            char fch = format.charAt(fidx++);
            if (fch != '%') {
               buf.append(fch);
            } else {
               switch(format.charAt(fidx++)) {
               case 'D':
                  formatDays(cal, buf);
                  break;
               case 'M':
                  formatMonth(cal, buf);
                  break;
               case 'Y':
                  formatYear(cal, buf);
                  break;
               case 'h':
                  formatHours(cal, buf);
                  break;
               case 'm':
                  formatMinutes(cal, buf);
                  break;
               case 's':
                  formatSeconds(cal, buf);
                  break;
               case 'z':
                  formatTimeZone(cal, buf);
                  break;
               default:
                  throw new InternalError();
               }
            }
         }

         return buf.toString();
      }

      private static void formatYear(Calendar cal, StringBuilder buf) {
         int year = cal.get(1);
         String s;
         if (year <= 0) {
            s = Integer.toString(1 - year);
         } else {
            s = Integer.toString(year);
         }

         while(s.length() < 4) {
            s = '0' + s;
         }

         if (year <= 0) {
            s = '-' + s;
         }

         buf.append(s);
      }

      private static void formatMonth(Calendar cal, StringBuilder buf) {
         formatTwoDigits(cal.get(2) + 1, buf);
      }

      private static void formatDays(Calendar cal, StringBuilder buf) {
         formatTwoDigits(cal.get(5), buf);
      }

      private static void formatHours(Calendar cal, StringBuilder buf) {
         formatTwoDigits(cal.get(11), buf);
      }

      private static void formatMinutes(Calendar cal, StringBuilder buf) {
         formatTwoDigits(cal.get(12), buf);
      }

      private static void formatSeconds(Calendar cal, StringBuilder buf) {
         formatTwoDigits(cal.get(13), buf);
         if (cal.isSet(14)) {
            int n = cal.get(14);
            if (n != 0) {
               String ms;
               for(ms = Integer.toString(n); ms.length() < 3; ms = '0' + ms) {
               }

               buf.append('.');
               buf.append(ms);
            }
         }

      }

      private static void formatTimeZone(Calendar cal, StringBuilder buf) {
         TimeZone tz = cal.getTimeZone();
         if (tz != null) {
            int offset = tz.getOffset(cal.getTime().getTime());
            if (offset == 0) {
               buf.append('Z');
            } else {
               if (offset >= 0) {
                  buf.append('+');
               } else {
                  buf.append('-');
                  offset *= -1;
               }

               offset /= 60000;
               formatTwoDigits(offset / 60, buf);
               buf.append(':');
               formatTwoDigits(offset % 60, buf);
            }
         }
      }

      private static void formatTwoDigits(int n, StringBuilder buf) {
         if (n < 10) {
            buf.append('0');
         }

         buf.append(n);
      }
   }
}
