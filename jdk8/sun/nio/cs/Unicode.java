package sun.nio.cs;

import java.nio.charset.Charset;

abstract class Unicode extends Charset implements HistoricallyNamedCharset {
   public Unicode(String var1, String[] var2) {
      super(var1, var2);
   }

   public boolean contains(Charset var1) {
      return var1 instanceof US_ASCII || var1 instanceof ISO_8859_1 || var1 instanceof ISO_8859_15 || var1 instanceof MS1252 || var1 instanceof UTF_8 || var1 instanceof UTF_16 || var1 instanceof UTF_16BE || var1 instanceof UTF_16LE || var1 instanceof UTF_16LE_BOM || var1.name().equals("GBK") || var1.name().equals("GB18030") || var1.name().equals("ISO-8859-2") || var1.name().equals("ISO-8859-3") || var1.name().equals("ISO-8859-4") || var1.name().equals("ISO-8859-5") || var1.name().equals("ISO-8859-6") || var1.name().equals("ISO-8859-7") || var1.name().equals("ISO-8859-8") || var1.name().equals("ISO-8859-9") || var1.name().equals("ISO-8859-13") || var1.name().equals("JIS_X0201") || var1.name().equals("x-JIS0208") || var1.name().equals("JIS_X0212-1990") || var1.name().equals("GB2312") || var1.name().equals("EUC-KR") || var1.name().equals("x-EUC-TW") || var1.name().equals("EUC-JP") || var1.name().equals("x-euc-jp-linux") || var1.name().equals("KOI8-R") || var1.name().equals("TIS-620") || var1.name().equals("x-ISCII91") || var1.name().equals("windows-1251") || var1.name().equals("windows-1253") || var1.name().equals("windows-1254") || var1.name().equals("windows-1255") || var1.name().equals("windows-1256") || var1.name().equals("windows-1257") || var1.name().equals("windows-1258") || var1.name().equals("windows-932") || var1.name().equals("x-mswin-936") || var1.name().equals("x-windows-949") || var1.name().equals("x-windows-950") || var1.name().equals("windows-31j") || var1.name().equals("Big5") || var1.name().equals("Big5-HKSCS") || var1.name().equals("x-MS950-HKSCS") || var1.name().equals("ISO-2022-JP") || var1.name().equals("ISO-2022-KR") || var1.name().equals("x-ISO-2022-CN-CNS") || var1.name().equals("x-ISO-2022-CN-GB") || var1.name().equals("Big5-HKSCS") || var1.name().equals("x-Johab") || var1.name().equals("Shift_JIS");
   }
}
