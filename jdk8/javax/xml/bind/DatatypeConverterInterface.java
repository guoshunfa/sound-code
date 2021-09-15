package javax.xml.bind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

public interface DatatypeConverterInterface {
   String parseString(String var1);

   BigInteger parseInteger(String var1);

   int parseInt(String var1);

   long parseLong(String var1);

   short parseShort(String var1);

   BigDecimal parseDecimal(String var1);

   float parseFloat(String var1);

   double parseDouble(String var1);

   boolean parseBoolean(String var1);

   byte parseByte(String var1);

   QName parseQName(String var1, NamespaceContext var2);

   Calendar parseDateTime(String var1);

   byte[] parseBase64Binary(String var1);

   byte[] parseHexBinary(String var1);

   long parseUnsignedInt(String var1);

   int parseUnsignedShort(String var1);

   Calendar parseTime(String var1);

   Calendar parseDate(String var1);

   String parseAnySimpleType(String var1);

   String printString(String var1);

   String printInteger(BigInteger var1);

   String printInt(int var1);

   String printLong(long var1);

   String printShort(short var1);

   String printDecimal(BigDecimal var1);

   String printFloat(float var1);

   String printDouble(double var1);

   String printBoolean(boolean var1);

   String printByte(byte var1);

   String printQName(QName var1, NamespaceContext var2);

   String printDateTime(Calendar var1);

   String printBase64Binary(byte[] var1);

   String printHexBinary(byte[] var1);

   String printUnsignedInt(long var1);

   String printUnsignedShort(int var1);

   String printTime(Calendar var1);

   String printDate(Calendar var1);

   String printAnySimpleType(String var1);
}
