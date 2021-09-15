package sun.awt.motif;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;
import sun.awt.FontConfiguration;
import sun.awt.X11FontManager;
import sun.font.FontUtilities;
import sun.font.SunFontManager;
import sun.util.logging.PlatformLogger;

public class MFontConfiguration extends FontConfiguration {
   private static FontConfiguration fontConfig = null;
   private static PlatformLogger logger;
   private static final String fontsDirPrefix = "$JRE_LIB_FONTS";
   private static HashMap encodingMap = new HashMap();

   public MFontConfiguration(SunFontManager var1) {
      super(var1);
      if (FontUtilities.debugFonts()) {
         logger = PlatformLogger.getLogger("sun.awt.FontConfiguration");
      }

      this.initTables();
   }

   public MFontConfiguration(SunFontManager var1, boolean var2, boolean var3) {
      super(var1, var2, var3);
      if (FontUtilities.debugFonts()) {
         logger = PlatformLogger.getLogger("sun.awt.FontConfiguration");
      }

      this.initTables();
   }

   protected void initReorderMap() {
      this.reorderMap = new HashMap();
      if (osName == null) {
         this.initReorderMapForSolaris();
      } else {
         this.initReorderMapForLinux();
      }

   }

   private void initReorderMapForSolaris() {
      this.reorderMap.put("UTF-8.hi", "devanagari");
      this.reorderMap.put("UTF-8.ja", this.split("japanese-x0201,japanese-x0208,japanese-x0212"));
      this.reorderMap.put("UTF-8.ko", "korean-johab");
      this.reorderMap.put("UTF-8.th", "thai");
      this.reorderMap.put("UTF-8.zh.TW", "chinese-big5");
      this.reorderMap.put("UTF-8.zh.HK", this.split("chinese-big5,chinese-hkscs"));
      if (FontUtilities.isSolaris8) {
         this.reorderMap.put("UTF-8.zh.CN", this.split("chinese-gb2312,chinese-big5"));
      } else {
         this.reorderMap.put("UTF-8.zh.CN", this.split("chinese-gb18030-0,chinese-gb18030-1"));
      }

      this.reorderMap.put("UTF-8.zh", this.split("chinese-big5,chinese-hkscs,chinese-gb18030-0,chinese-gb18030-1"));
      this.reorderMap.put("Big5", "chinese-big5");
      this.reorderMap.put("Big5-HKSCS", this.split("chinese-big5,chinese-hkscs"));
      if (!FontUtilities.isSolaris8 && !FontUtilities.isSolaris9) {
         this.reorderMap.put("GB2312", this.split("chinese-gbk,chinese-gb2312"));
      } else {
         this.reorderMap.put("GB2312", "chinese-gb2312");
      }

      this.reorderMap.put("x-EUC-TW", this.split("chinese-cns11643-1,chinese-cns11643-2,chinese-cns11643-3"));
      this.reorderMap.put("GBK", "chinese-gbk");
      this.reorderMap.put("GB18030", this.split("chinese-gb18030-0,chinese-gb18030-1"));
      this.reorderMap.put("TIS-620", "thai");
      this.reorderMap.put("x-PCK", this.split("japanese-x0201,japanese-x0208,japanese-x0212"));
      this.reorderMap.put("x-eucJP-Open", this.split("japanese-x0201,japanese-x0208,japanese-x0212"));
      this.reorderMap.put("EUC-KR", "korean");
      this.reorderMap.put("ISO-8859-2", "latin-2");
      this.reorderMap.put("ISO-8859-5", "cyrillic-iso8859-5");
      this.reorderMap.put("windows-1251", "cyrillic-cp1251");
      this.reorderMap.put("KOI8-R", "cyrillic-koi8-r");
      this.reorderMap.put("ISO-8859-6", "arabic");
      this.reorderMap.put("ISO-8859-7", "greek");
      this.reorderMap.put("ISO-8859-8", "hebrew");
      this.reorderMap.put("ISO-8859-9", "latin-5");
      this.reorderMap.put("ISO-8859-13", "latin-7");
      this.reorderMap.put("ISO-8859-15", "latin-9");
   }

   private void initReorderMapForLinux() {
      this.reorderMap.put("UTF-8.ja.JP", "japanese-iso10646");
      this.reorderMap.put("UTF-8.ko.KR", "korean-iso10646");
      this.reorderMap.put("UTF-8.zh.TW", "chinese-tw-iso10646");
      this.reorderMap.put("UTF-8.zh.HK", "chinese-tw-iso10646");
      this.reorderMap.put("UTF-8.zh.CN", "chinese-cn-iso10646");
      this.reorderMap.put("x-euc-jp-linux", this.split("japanese-x0201,japanese-x0208"));
      this.reorderMap.put("GB2312", "chinese-gb18030");
      this.reorderMap.put("Big5", "chinese-big5");
      this.reorderMap.put("EUC-KR", "korean");
      if (osName.equals("Sun")) {
         this.reorderMap.put("GB18030", "chinese-cn-iso10646");
      } else {
         this.reorderMap.put("GB18030", "chinese-gb18030");
      }

   }

   protected void setOsNameAndVersion() {
      super.setOsNameAndVersion();
      if (osName.equals("SunOS")) {
         osName = null;
      } else if (osName.equals("Linux")) {
         try {
            File var1;
            if ((var1 = new File("/etc/fedora-release")).canRead()) {
               osName = "Fedora";
               osVersion = this.getVersionString(var1);
            } else if ((var1 = new File("/etc/redhat-release")).canRead()) {
               osName = "RedHat";
               osVersion = this.getVersionString(var1);
            } else if ((var1 = new File("/etc/turbolinux-release")).canRead()) {
               osName = "Turbo";
               osVersion = this.getVersionString(var1);
            } else if ((var1 = new File("/etc/SuSE-release")).canRead()) {
               osName = "SuSE";
               osVersion = this.getVersionString(var1);
            } else if ((var1 = new File("/etc/lsb-release")).canRead()) {
               Properties var2 = new Properties();
               var2.load((InputStream)(new FileInputStream(var1)));
               osName = var2.getProperty("DISTRIB_ID");
               osVersion = var2.getProperty("DISTRIB_RELEASE");
            }
         } catch (Exception var3) {
         }
      }

   }

   private String getVersionString(File var1) {
      try {
         Scanner var2 = new Scanner(var1);
         return var2.findInLine("(\\d)+((\\.)(\\d)+)*");
      } catch (Exception var3) {
         return null;
      }
   }

   protected String mapFileName(String var1) {
      return var1 != null && var1.startsWith("$JRE_LIB_FONTS") ? SunFontManager.jreFontDirName + var1.substring("$JRE_LIB_FONTS".length()) : var1;
   }

   public String getFallbackFamilyName(String var1, String var2) {
      String var3 = this.getCompatibilityFamilyName(var1);
      return var3 != null ? var3 : var2;
   }

   protected String getEncoding(String var1, String var2) {
      int var3 = 0;

      for(int var4 = 13; var4-- > 0 && var3 >= 0; var3 = var1.indexOf("-", var3) + 1) {
      }

      if (var3 == -1) {
         return "default";
      } else {
         String var5 = var1.substring(var3);
         if (var5.indexOf("fontspecific") > 0) {
            if (var1.indexOf("dingbats") > 0) {
               return "sun.awt.motif.X11Dingbats";
            }

            if (var1.indexOf("symbol") > 0) {
               return "sun.awt.Symbol";
            }
         }

         String var6 = (String)encodingMap.get(var5);
         if (var6 == null) {
            var6 = "default";
         }

         return var6;
      }
   }

   protected Charset getDefaultFontCharset(String var1) {
      return Charset.forName("ISO8859_1");
   }

   protected String getFaceNameFromComponentFontName(String var1) {
      return null;
   }

   protected String getFileNameFromComponentFontName(String var1) {
      String var2 = this.getFileNameFromPlatformName(var1);
      return var2 != null && var2.charAt(0) == '/' && !this.needToSearchForFile(var2) ? var2 : ((X11FontManager)this.fontManager).getFileNameFromXLFD(var1);
   }

   public HashSet<String> getAWTFontPathSet() {
      HashSet var1 = new HashSet();
      short[] var2 = this.getCoreScripts(0);

      for(int var3 = 0; var3 < var2.length; ++var3) {
         String var4 = getString(table_awtfontpaths[var2[var3]]);
         if (var4 != null) {
            int var5 = 0;

            for(int var6 = var4.indexOf(58); var6 >= 0; var6 = var4.indexOf(58, var5)) {
               var1.add(var4.substring(var5, var6));
               var5 = var6 + 1;
            }

            var1.add(var5 == 0 ? var4 : var4.substring(var5));
         }
      }

      return var1;
   }

   private void initTables() {
      encodingMap.put("iso8859-1", "ISO-8859-1");
      encodingMap.put("iso8859-2", "ISO-8859-2");
      encodingMap.put("iso8859-4", "ISO-8859-4");
      encodingMap.put("iso8859-5", "ISO-8859-5");
      encodingMap.put("iso8859-6", "ISO-8859-6");
      encodingMap.put("iso8859-7", "ISO-8859-7");
      encodingMap.put("iso8859-8", "ISO-8859-8");
      encodingMap.put("iso8859-9", "ISO-8859-9");
      encodingMap.put("iso8859-13", "ISO-8859-13");
      encodingMap.put("iso8859-15", "ISO-8859-15");
      encodingMap.put("gb2312.1980-0", "sun.awt.motif.X11GB2312");
      if (osName == null) {
         encodingMap.put("gbk-0", "GBK");
      } else {
         encodingMap.put("gbk-0", "sun.awt.motif.X11GBK");
      }

      encodingMap.put("gb18030.2000-0", "sun.awt.motif.X11GB18030_0");
      encodingMap.put("gb18030.2000-1", "sun.awt.motif.X11GB18030_1");
      encodingMap.put("cns11643-1", "sun.awt.motif.X11CNS11643P1");
      encodingMap.put("cns11643-2", "sun.awt.motif.X11CNS11643P2");
      encodingMap.put("cns11643-3", "sun.awt.motif.X11CNS11643P3");
      encodingMap.put("big5-1", "Big5");
      encodingMap.put("big5-0", "Big5");
      encodingMap.put("hkscs-1", "Big5-HKSCS");
      encodingMap.put("ansi-1251", "windows-1251");
      encodingMap.put("koi8-r", "KOI8-R");
      encodingMap.put("jisx0201.1976-0", "sun.awt.motif.X11JIS0201");
      encodingMap.put("jisx0208.1983-0", "sun.awt.motif.X11JIS0208");
      encodingMap.put("jisx0212.1990-0", "sun.awt.motif.X11JIS0212");
      encodingMap.put("ksc5601.1987-0", "sun.awt.motif.X11KSC5601");
      encodingMap.put("ksc5601.1992-3", "sun.awt.motif.X11Johab");
      encodingMap.put("tis620.2533-0", "TIS-620");
      encodingMap.put("iso10646-1", "UTF-16BE");
   }
}
