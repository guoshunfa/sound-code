package sun.awt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import sun.font.CompositeFontDescriptor;
import sun.font.FontUtilities;
import sun.font.SunFontManager;
import sun.util.logging.PlatformLogger;

public abstract class FontConfiguration {
   protected static String osVersion;
   protected static String osName;
   protected static String encoding;
   protected static Locale startupLocale = null;
   protected static Hashtable localeMap = null;
   private static FontConfiguration fontConfig;
   private static PlatformLogger logger;
   protected static boolean isProperties = true;
   protected SunFontManager fontManager;
   protected boolean preferLocaleFonts;
   protected boolean preferPropFonts;
   private File fontConfigFile;
   private boolean foundOsSpecificFile;
   private boolean inited;
   private String javaLib;
   private static short stringIDNum;
   private static short[] stringIDs;
   private static StringBuilder stringTable;
   public static boolean verbose;
   private short initELC = -1;
   private Locale initLocale;
   private String initEncoding;
   private String alphabeticSuffix;
   private short[][][] compFontNameIDs = new short[5][4][];
   private int[][][] compExclusions = new int[5][][];
   private int[] compCoreNum = new int[5];
   private Set<Short> coreFontNameIDs = new HashSet();
   private Set<Short> fallbackFontNameIDs = new HashSet();
   protected static final int NUM_FONTS = 5;
   protected static final int NUM_STYLES = 4;
   protected static final String[] fontNames = new String[]{"serif", "sansserif", "monospaced", "dialog", "dialoginput"};
   protected static final String[] publicFontNames = new String[]{"Serif", "SansSerif", "Monospaced", "Dialog", "DialogInput"};
   protected static final String[] styleNames = new String[]{"plain", "bold", "italic", "bolditalic"};
   protected static String[] installedFallbackFontFiles = null;
   protected HashMap reorderMap = null;
   private Hashtable charsetRegistry = new Hashtable(5);
   private FontDescriptor[][][] fontDescriptors = new FontDescriptor[5][4][];
   HashMap<String, Boolean> existsMap;
   private int numCoreFonts = -1;
   private String[] componentFonts = null;
   HashMap<String, String> filenamesMap = new HashMap();
   HashSet<String> coreFontFileNames = new HashSet();
   private static final int HEAD_LENGTH = 20;
   private static final int INDEX_scriptIDs = 0;
   private static final int INDEX_scriptFonts = 1;
   private static final int INDEX_elcIDs = 2;
   private static final int INDEX_sequences = 3;
   private static final int INDEX_fontfileNameIDs = 4;
   private static final int INDEX_componentFontNameIDs = 5;
   private static final int INDEX_filenames = 6;
   private static final int INDEX_awtfontpaths = 7;
   private static final int INDEX_exclusions = 8;
   private static final int INDEX_proportionals = 9;
   private static final int INDEX_scriptFontsMotif = 10;
   private static final int INDEX_alphabeticSuffix = 11;
   private static final int INDEX_stringIDs = 12;
   private static final int INDEX_stringTable = 13;
   private static final int INDEX_TABLEEND = 14;
   private static final int INDEX_fallbackScripts = 15;
   private static final int INDEX_appendedfontpath = 16;
   private static final int INDEX_version = 17;
   private static short[] head;
   private static short[] table_scriptIDs;
   private static short[] table_scriptFonts;
   private static short[] table_elcIDs;
   private static short[] table_sequences;
   private static short[] table_fontfileNameIDs;
   private static short[] table_componentFontNameIDs;
   private static short[] table_filenames;
   protected static short[] table_awtfontpaths;
   private static short[] table_exclusions;
   private static short[] table_proportionals;
   private static short[] table_scriptFontsMotif;
   private static short[] table_alphabeticSuffix;
   private static short[] table_stringIDs;
   private static char[] table_stringTable;
   private HashMap<String, Short> reorderScripts;
   private static String[] stringCache;
   private static final int[] EMPTY_INT_ARRAY = new int[0];
   private static final String[] EMPTY_STRING_ARRAY = new String[0];
   private static final short[] EMPTY_SHORT_ARRAY = new short[0];
   private static final String UNDEFINED_COMPONENT_FONT = "unknown";

   public FontConfiguration(SunFontManager var1) {
      if (FontUtilities.debugFonts()) {
         FontUtilities.getLogger().info("Creating standard Font Configuration");
      }

      if (FontUtilities.debugFonts() && logger == null) {
         logger = PlatformLogger.getLogger("sun.awt.FontConfiguration");
      }

      this.fontManager = var1;
      this.setOsNameAndVersion();
      this.setEncoding();
      this.findFontConfigFile();
   }

   public synchronized boolean init() {
      if (!this.inited) {
         this.preferLocaleFonts = false;
         this.preferPropFonts = false;
         this.setFontConfiguration();
         this.readFontConfigFile(this.fontConfigFile);
         this.initFontConfig();
         this.inited = true;
      }

      return true;
   }

   public FontConfiguration(SunFontManager var1, boolean var2, boolean var3) {
      this.fontManager = var1;
      if (FontUtilities.debugFonts()) {
         FontUtilities.getLogger().info("Creating alternate Font Configuration");
      }

      this.preferLocaleFonts = var2;
      this.preferPropFonts = var3;
      this.initFontConfig();
   }

   protected void setOsNameAndVersion() {
      osName = System.getProperty("os.name");
      osVersion = System.getProperty("os.version");
   }

   private void setEncoding() {
      encoding = Charset.defaultCharset().name();
      startupLocale = SunToolkit.getStartupLocale();
   }

   public boolean foundOsSpecificFile() {
      return this.foundOsSpecificFile;
   }

   public boolean fontFilesArePresent() {
      this.init();
      short var1 = this.compFontNameIDs[0][0][0];
      short var2 = getComponentFileID(var1);
      final String var3 = this.mapFileName(getComponentFileName(var2));
      Boolean var4 = (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            try {
               File var1 = new File(var3);
               return var1.exists();
            } catch (Exception var2) {
               return false;
            }
         }
      });
      return var4;
   }

   private void findFontConfigFile() {
      this.foundOsSpecificFile = true;
      String var1 = System.getProperty("java.home");
      if (var1 == null) {
         throw new Error("java.home property not set");
      } else {
         this.javaLib = var1 + File.separator + "lib";
         String var2 = System.getProperty("sun.awt.fontconfig");
         if (var2 != null) {
            this.fontConfigFile = new File(var2);
         } else {
            this.fontConfigFile = this.findFontConfigFile(this.javaLib);
         }

      }
   }

   private void readFontConfigFile(File var1) {
      this.getInstalledFallbackFonts(this.javaLib);
      if (var1 != null) {
         try {
            FileInputStream var2 = new FileInputStream(var1.getPath());
            if (isProperties) {
               loadProperties(var2);
            } else {
               loadBinary(var2);
            }

            var2.close();
            if (FontUtilities.debugFonts()) {
               logger.config("Read logical font configuration from " + var1);
            }
         } catch (IOException var3) {
            if (FontUtilities.debugFonts()) {
               logger.config("Failed to read logical font configuration from " + var1);
            }
         }
      }

      String var4 = this.getVersion();
      if (!"1".equals(var4) && FontUtilities.debugFonts()) {
         logger.config("Unsupported fontconfig version: " + var4);
      }

   }

   protected void getInstalledFallbackFonts(String var1) {
      String var2 = var1 + File.separator + "fonts" + File.separator + "fallback";
      File var3 = new File(var2);
      if (var3.exists() && var3.isDirectory()) {
         String[] var4 = var3.list(this.fontManager.getTrueTypeFilter());
         String[] var5 = var3.list(this.fontManager.getType1Filter());
         int var6 = var4 == null ? 0 : var4.length;
         int var7 = var5 == null ? 0 : var5.length;
         int var8 = var6 + var7;
         if (var6 + var7 == 0) {
            return;
         }

         installedFallbackFontFiles = new String[var8];

         int var9;
         for(var9 = 0; var9 < var6; ++var9) {
            installedFallbackFontFiles[var9] = var3 + File.separator + var4[var9];
         }

         for(var9 = 0; var9 < var7; ++var9) {
            installedFallbackFontFiles[var9 + var6] = var3 + File.separator + var5[var9];
         }

         this.fontManager.registerFontsInDir(var2);
      }

   }

   private File findImpl(String var1) {
      File var2 = new File(var1 + ".properties");
      if (var2.canRead()) {
         isProperties = true;
         return var2;
      } else {
         var2 = new File(var1 + ".bfc");
         if (var2.canRead()) {
            isProperties = false;
            return var2;
         } else {
            return null;
         }
      }
   }

   private File findFontConfigFile(String var1) {
      String var2 = var1 + File.separator + "fontconfig";
      String var4 = null;
      File var3;
      if (osVersion != null && osName != null) {
         var3 = this.findImpl(var2 + "." + osName + "." + osVersion);
         if (var3 != null) {
            return var3;
         }

         int var5 = osVersion.indexOf(".");
         if (var5 != -1) {
            var4 = osVersion.substring(0, osVersion.indexOf("."));
            var3 = this.findImpl(var2 + "." + osName + "." + var4);
            if (var3 != null) {
               return var3;
            }
         }
      }

      if (osName != null) {
         var3 = this.findImpl(var2 + "." + osName);
         if (var3 != null) {
            return var3;
         }
      }

      if (osVersion != null) {
         var3 = this.findImpl(var2 + "." + osVersion);
         if (var3 != null) {
            return var3;
         }

         if (var4 != null) {
            var3 = this.findImpl(var2 + "." + var4);
            if (var3 != null) {
               return var3;
            }
         }
      }

      this.foundOsSpecificFile = false;
      var3 = this.findImpl(var2);
      return var3 != null ? var3 : null;
   }

   public static void loadBinary(InputStream var0) throws IOException {
      DataInputStream var1 = new DataInputStream(var0);
      head = readShortTable(var1, 20);
      int[] var2 = new int[14];

      int var3;
      for(var3 = 0; var3 < 14; ++var3) {
         var2[var3] = head[var3 + 1] - head[var3];
      }

      table_scriptIDs = readShortTable(var1, var2[0]);
      table_scriptFonts = readShortTable(var1, var2[1]);
      table_elcIDs = readShortTable(var1, var2[2]);
      table_sequences = readShortTable(var1, var2[3]);
      table_fontfileNameIDs = readShortTable(var1, var2[4]);
      table_componentFontNameIDs = readShortTable(var1, var2[5]);
      table_filenames = readShortTable(var1, var2[6]);
      table_awtfontpaths = readShortTable(var1, var2[7]);
      table_exclusions = readShortTable(var1, var2[8]);
      table_proportionals = readShortTable(var1, var2[9]);
      table_scriptFontsMotif = readShortTable(var1, var2[10]);
      table_alphabeticSuffix = readShortTable(var1, var2[11]);
      table_stringIDs = readShortTable(var1, var2[12]);
      stringCache = new String[table_stringIDs.length + 1];
      var3 = var2[13];
      byte[] var4 = new byte[var3 * 2];
      table_stringTable = new char[var3];
      var1.read(var4);
      int var5 = 0;

      for(int var6 = 0; var5 < var3; table_stringTable[var5++] = (char)(var4[var6++] << 8 | var4[var6++] & 255)) {
      }

      if (verbose) {
         dump();
      }

   }

   public static void saveBinary(OutputStream var0) throws IOException {
      sanityCheck();
      DataOutputStream var1 = new DataOutputStream(var0);
      writeShortTable(var1, head);
      writeShortTable(var1, table_scriptIDs);
      writeShortTable(var1, table_scriptFonts);
      writeShortTable(var1, table_elcIDs);
      writeShortTable(var1, table_sequences);
      writeShortTable(var1, table_fontfileNameIDs);
      writeShortTable(var1, table_componentFontNameIDs);
      writeShortTable(var1, table_filenames);
      writeShortTable(var1, table_awtfontpaths);
      writeShortTable(var1, table_exclusions);
      writeShortTable(var1, table_proportionals);
      writeShortTable(var1, table_scriptFontsMotif);
      writeShortTable(var1, table_alphabeticSuffix);
      writeShortTable(var1, table_stringIDs);
      var1.writeChars(new String(table_stringTable));
      var0.close();
      if (verbose) {
         dump();
      }

   }

   public static void loadProperties(InputStream var0) throws IOException {
      stringIDNum = 1;
      stringIDs = new short[1000];
      stringTable = new StringBuilder(4096);
      if (verbose && logger == null) {
         logger = PlatformLogger.getLogger("sun.awt.FontConfiguration");
      }

      (new FontConfiguration.PropertiesHandler()).load(var0);
      stringIDs = null;
      stringTable = null;
   }

   private void initFontConfig() {
      this.initLocale = startupLocale;
      this.initEncoding = encoding;
      if (this.preferLocaleFonts && !willReorderForStartupLocale()) {
         this.preferLocaleFonts = false;
      }

      this.initELC = this.getInitELC();
      this.initAllComponentFonts();
   }

   private short getInitELC() {
      if (this.initELC != -1) {
         return this.initELC;
      } else {
         HashMap var1 = new HashMap();

         for(int var2 = 0; var2 < table_elcIDs.length; ++var2) {
            var1.put(getString(table_elcIDs[var2]), var2);
         }

         String var6 = this.initLocale.getLanguage();
         String var3 = this.initLocale.getCountry();
         String var4;
         if (!var1.containsKey(var4 = this.initEncoding + "." + var6 + "." + var3) && !var1.containsKey(var4 = this.initEncoding + "." + var6) && !var1.containsKey(var4 = this.initEncoding)) {
            this.initELC = ((Integer)var1.get("NULL.NULL.NULL")).shortValue();
         } else {
            this.initELC = ((Integer)var1.get(var4)).shortValue();
         }

         for(int var5 = 0; var5 < table_alphabeticSuffix.length; var5 += 2) {
            if (this.initELC == table_alphabeticSuffix[var5]) {
               this.alphabeticSuffix = getString(table_alphabeticSuffix[var5 + 1]);
               return this.initELC;
            }
         }

         return this.initELC;
      }
   }

   private void initAllComponentFonts() {
      short[] var1 = getFallbackScripts();

      for(int var2 = 0; var2 < 5; ++var2) {
         short[] var3 = this.getCoreScripts(var2);
         this.compCoreNum[var2] = var3.length;
         int[][] var4 = new int[var3.length][];

         int var5;
         for(var5 = 0; var5 < var3.length; ++var5) {
            var4[var5] = getExclusionRanges(var3[var5]);
         }

         this.compExclusions[var2] = var4;

         for(var5 = 0; var5 < 4; ++var5) {
            short[] var7 = new short[var3.length + var1.length];

            int var6;
            for(var6 = 0; var6 < var3.length; ++var6) {
               var7[var6] = getComponentFontID(var3[var6], var2, var5);
               if (this.preferLocaleFonts && localeMap != null && this.fontManager.usingAlternateFontforJALocales()) {
                  var7[var6] = this.remapLocaleMap(var2, var5, var3[var6], var7[var6]);
               }

               if (this.preferPropFonts) {
                  var7[var6] = this.remapProportional(var2, var7[var6]);
               }

               this.coreFontNameIDs.add(var7[var6]);
            }

            for(int var8 = 0; var8 < var1.length; ++var8) {
               short var9 = getComponentFontID(var1[var8], var2, var5);
               if (this.preferLocaleFonts && localeMap != null && this.fontManager.usingAlternateFontforJALocales()) {
                  var9 = this.remapLocaleMap(var2, var5, var1[var8], var9);
               }

               if (this.preferPropFonts) {
                  var9 = this.remapProportional(var2, var9);
               }

               if (!contains(var7, var9, var6)) {
                  this.fallbackFontNameIDs.add(var9);
                  var7[var6++] = var9;
               }
            }

            if (var6 < var7.length) {
               short[] var10 = new short[var6];
               System.arraycopy(var7, 0, var10, 0, var6);
               var7 = var10;
            }

            this.compFontNameIDs[var2][var5] = var7;
         }
      }

   }

   private short remapLocaleMap(int var1, int var2, short var3, short var4) {
      String var5 = getString(table_scriptIDs[var3]);
      String var6 = (String)localeMap.get(var5);
      String var8;
      if (var6 == null) {
         String var7 = fontNames[var1];
         var8 = styleNames[var2];
         var6 = (String)localeMap.get(var7 + "." + var8 + "." + var5);
      }

      if (var6 == null) {
         return var4;
      } else {
         for(int var9 = 0; var9 < table_componentFontNameIDs.length; ++var9) {
            var8 = getString(table_componentFontNameIDs[var9]);
            if (var6.equalsIgnoreCase(var8)) {
               var4 = (short)var9;
               break;
            }
         }

         return var4;
      }
   }

   public static boolean hasMonoToPropMap() {
      return table_proportionals != null && table_proportionals.length != 0;
   }

   private short remapProportional(int var1, short var2) {
      if (this.preferPropFonts && table_proportionals.length != 0 && var1 != 2 && var1 != 4) {
         for(int var3 = 0; var3 < table_proportionals.length; var3 += 2) {
            if (table_proportionals[var3] == var2) {
               return table_proportionals[var3 + 1];
            }
         }
      }

      return var2;
   }

   public static boolean isLogicalFontFamilyName(String var0) {
      return isLogicalFontFamilyNameLC(var0.toLowerCase(Locale.ENGLISH));
   }

   public static boolean isLogicalFontFamilyNameLC(String var0) {
      for(int var1 = 0; var1 < fontNames.length; ++var1) {
         if (var0.equals(fontNames[var1])) {
            return true;
         }
      }

      return false;
   }

   private static boolean isLogicalFontStyleName(String var0) {
      for(int var1 = 0; var1 < styleNames.length; ++var1) {
         if (var0.equals(styleNames[var1])) {
            return true;
         }
      }

      return false;
   }

   public static boolean isLogicalFontFaceName(String var0) {
      return isLogicalFontFaceNameLC(var0.toLowerCase(Locale.ENGLISH));
   }

   public static boolean isLogicalFontFaceNameLC(String var0) {
      int var1 = var0.indexOf(46);
      if (var1 < 0) {
         return isLogicalFontFamilyName(var0);
      } else {
         String var2 = var0.substring(0, var1);
         String var3 = var0.substring(var1 + 1);
         return isLogicalFontFamilyName(var2) && isLogicalFontStyleName(var3);
      }
   }

   protected static int getFontIndex(String var0) {
      return getArrayIndex(fontNames, var0);
   }

   protected static int getStyleIndex(String var0) {
      return getArrayIndex(styleNames, var0);
   }

   private static int getArrayIndex(String[] var0, String var1) {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         if (var1.equals(var0[var2])) {
            return var2;
         }
      }

      assert false;

      return 0;
   }

   protected static int getStyleIndex(int var0) {
      switch(var0) {
      case 0:
         return 0;
      case 1:
         return 1;
      case 2:
         return 2;
      case 3:
         return 3;
      default:
         return 0;
      }
   }

   protected static String getFontName(int var0) {
      return fontNames[var0];
   }

   protected static String getStyleName(int var0) {
      return styleNames[var0];
   }

   public static String getLogicalFontFaceName(String var0, int var1) {
      assert isLogicalFontFamilyName(var0);

      return var0.toLowerCase(Locale.ENGLISH) + "." + getStyleString(var1);
   }

   public static String getStyleString(int var0) {
      return getStyleName(getStyleIndex(var0));
   }

   public abstract String getFallbackFamilyName(String var1, String var2);

   protected String getCompatibilityFamilyName(String var1) {
      var1 = var1.toLowerCase(Locale.ENGLISH);
      if (var1.equals("timesroman")) {
         return "serif";
      } else if (var1.equals("helvetica")) {
         return "sansserif";
      } else {
         return var1.equals("courier") ? "monospaced" : null;
      }
   }

   protected String mapFileName(String var1) {
      return var1;
   }

   protected abstract void initReorderMap();

   private void shuffle(String[] var1, int var2, int var3) {
      if (var3 < var2) {
         String var4 = var1[var2];

         for(int var5 = var2; var5 > var3; --var5) {
            var1[var5] = var1[var5 - 1];
         }

         var1[var3] = var4;
      }
   }

   public static boolean willReorderForStartupLocale() {
      return getReorderSequence() != null;
   }

   private static Object getReorderSequence() {
      if (fontConfig.reorderMap == null) {
         fontConfig.initReorderMap();
      }

      HashMap var0 = fontConfig.reorderMap;
      String var1 = startupLocale.getLanguage();
      String var2 = startupLocale.getCountry();
      Object var3 = var0.get(encoding + "." + var1 + "." + var2);
      if (var3 == null) {
         var3 = var0.get(encoding + "." + var1);
      }

      if (var3 == null) {
         var3 = var0.get(encoding);
      }

      return var3;
   }

   private void reorderSequenceForLocale(String[] var1) {
      Object var2 = getReorderSequence();
      if (var2 instanceof String) {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3].equals(var2)) {
               this.shuffle(var1, var3, 0);
               return;
            }
         }
      } else if (var2 instanceof String[]) {
         String[] var6 = (String[])((String[])var2);

         for(int var4 = 0; var4 < var6.length; ++var4) {
            for(int var5 = 0; var5 < var1.length; ++var5) {
               if (var1[var5].equals(var6[var4])) {
                  this.shuffle(var1, var5, var4);
               }
            }
         }
      }

   }

   private static Vector splitSequence(String var0) {
      Vector var1 = new Vector();

      int var2;
      int var3;
      for(var2 = 0; (var3 = var0.indexOf(44, var2)) >= 0; var2 = var3 + 1) {
         var1.add(var0.substring(var2, var3));
      }

      if (var0.length() > var2) {
         var1.add(var0.substring(var2, var0.length()));
      }

      return var1;
   }

   protected String[] split(String var1) {
      Vector var2 = splitSequence(var1);
      return (String[])((String[])var2.toArray(new String[0]));
   }

   public FontDescriptor[] getFontDescriptors(String var1, int var2) {
      assert isLogicalFontFamilyName(var1);

      var1 = var1.toLowerCase(Locale.ENGLISH);
      int var3 = getFontIndex(var1);
      int var4 = getStyleIndex(var2);
      return this.getFontDescriptors(var3, var4);
   }

   private FontDescriptor[] getFontDescriptors(int var1, int var2) {
      FontDescriptor[] var3 = this.fontDescriptors[var1][var2];
      if (var3 == null) {
         var3 = this.buildFontDescriptors(var1, var2);
         this.fontDescriptors[var1][var2] = var3;
      }

      return var3;
   }

   protected FontDescriptor[] buildFontDescriptors(int var1, int var2) {
      String var10000 = fontNames[var1];
      var10000 = styleNames[var2];
      short[] var5 = this.getCoreScripts(var1);
      short[] var6 = this.compFontNameIDs[var1][var2];
      String[] var7 = new String[var5.length];
      String[] var8 = new String[var5.length];

      for(int var9 = 0; var9 < var7.length; ++var9) {
         var8[var9] = getComponentFontName(var6[var9]);
         var7[var9] = getScriptName(var5[var9]);
         if (this.alphabeticSuffix != null && "alphabetic".equals(var7[var9])) {
            var7[var9] = var7[var9] + "/" + this.alphabeticSuffix;
         }
      }

      int[][] var16 = this.compExclusions[var1];
      FontDescriptor[] var10 = new FontDescriptor[var8.length];

      for(int var11 = 0; var11 < var8.length; ++var11) {
         String var12 = this.makeAWTFontName(var8[var11], var7[var11]);
         String var13 = this.getEncoding(var8[var11], var7[var11]);
         if (var13 == null) {
            var13 = "default";
         }

         CharsetEncoder var14 = this.getFontCharsetEncoder(var13.trim(), var12);
         int[] var15 = var16[var11];
         var10[var11] = new FontDescriptor(var12, var14, var15);
      }

      return var10;
   }

   protected String makeAWTFontName(String var1, String var2) {
      return var1;
   }

   protected abstract String getEncoding(String var1, String var2);

   private CharsetEncoder getFontCharsetEncoder(final String var1, String var2) {
      Charset var3 = null;
      if (var1.equals("default")) {
         var3 = (Charset)this.charsetRegistry.get(var2);
      } else {
         var3 = (Charset)this.charsetRegistry.get(var1);
      }

      if (var3 != null) {
         return var3.newEncoder();
      } else {
         if (!var1.startsWith("sun.awt.") && !var1.equals("default")) {
            var3 = Charset.forName(var1);
         } else {
            Class var4 = (Class)AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  try {
                     return Class.forName(var1, true, ClassLoader.getSystemClassLoader());
                  } catch (ClassNotFoundException var2) {
                     return null;
                  }
               }
            });
            if (var4 != null) {
               try {
                  var3 = (Charset)var4.newInstance();
               } catch (Exception var6) {
               }
            }
         }

         if (var3 == null) {
            var3 = this.getDefaultFontCharset(var2);
         }

         if (var1.equals("default")) {
            this.charsetRegistry.put(var2, var3);
         } else {
            this.charsetRegistry.put(var1, var3);
         }

         return var3.newEncoder();
      }
   }

   protected abstract Charset getDefaultFontCharset(String var1);

   public HashSet<String> getAWTFontPathSet() {
      return null;
   }

   public CompositeFontDescriptor[] get2DCompositeFontInfo() {
      CompositeFontDescriptor[] var1 = new CompositeFontDescriptor[20];
      String var2 = this.fontManager.getDefaultFontFile();
      String var3 = this.fontManager.getDefaultFontFaceName();

      for(int var4 = 0; var4 < 5; ++var4) {
         String var5 = publicFontNames[var4];
         int[][] var6 = this.compExclusions[var4];
         int var7 = 0;

         for(int var8 = 0; var8 < var6.length; ++var8) {
            var7 += var6[var8].length;
         }

         int[] var22 = new int[var7];
         int[] var9 = new int[var6.length];
         int var10 = 0;
         boolean var11 = false;

         int var12;
         for(var12 = 0; var12 < var6.length; ++var12) {
            int[] var13 = var6[var12];

            for(int var14 = 0; var14 < var13.length; var22[var10++] = var13[var14++]) {
               int var10000 = var13[var14];
               var22[var10++] = var13[var14++];
            }

            var9[var12] = var10;
         }

         for(var12 = 0; var12 < 4; ++var12) {
            int var23 = this.compFontNameIDs[var4][var12].length;
            boolean var24 = false;
            if (installedFallbackFontFiles != null) {
               var23 += installedFallbackFontFiles.length;
            }

            String var15 = var5 + "." + styleNames[var12];
            String[] var16 = new String[var23];
            String[] var17 = new String[var23];

            int var18;
            for(var18 = 0; var18 < this.compFontNameIDs[var4][var12].length; ++var18) {
               short var19 = this.compFontNameIDs[var4][var12][var18];
               short var20 = getComponentFileID(var19);
               var16[var18] = this.getFaceNameFromComponentFontName(getComponentFontName(var19));
               var17[var18] = this.mapFileName(getComponentFileName(var20));
               if (var17[var18] == null || this.needToSearchForFile(var17[var18])) {
                  var17[var18] = this.getFileNameFromComponentFontName(getComponentFontName(var19));
               }

               if (!var24 && var2.equals(var17[var18])) {
                  var24 = true;
               }
            }

            int var25;
            String[] var26;
            if (!var24) {
               var25 = 0;
               if (installedFallbackFontFiles != null) {
                  var25 = installedFallbackFontFiles.length;
               }

               if (var18 + var25 == var23) {
                  var26 = new String[var23 + 1];
                  System.arraycopy(var16, 0, var26, 0, var18);
                  var16 = var26;
                  String[] var21 = new String[var23 + 1];
                  System.arraycopy(var17, 0, var21, 0, var18);
                  var17 = var21;
               }

               var16[var18] = var3;
               var17[var18] = var2;
               ++var18;
            }

            if (installedFallbackFontFiles != null) {
               for(var25 = 0; var25 < installedFallbackFontFiles.length; ++var25) {
                  var16[var18] = null;
                  var17[var18] = installedFallbackFontFiles[var25];
                  ++var18;
               }
            }

            if (var18 < var23) {
               String[] var27 = new String[var18];
               System.arraycopy(var16, 0, var27, 0, var18);
               var16 = var27;
               var26 = new String[var18];
               System.arraycopy(var17, 0, var26, 0, var18);
               var17 = var26;
            }

            int[] var29 = var9;
            if (var18 != var9.length) {
               int var28 = var9.length;
               var29 = new int[var18];
               System.arraycopy(var9, 0, var29, 0, var28);

               for(int var30 = var28; var30 < var18; ++var30) {
                  var29[var30] = var22.length;
               }
            }

            var1[var4 * 4 + var12] = new CompositeFontDescriptor(var15, this.compCoreNum[var4], var16, var17, var22, var29);
         }
      }

      return var1;
   }

   protected abstract String getFaceNameFromComponentFontName(String var1);

   protected abstract String getFileNameFromComponentFontName(String var1);

   public boolean needToSearchForFile(String var1) {
      if (!FontUtilities.isLinux) {
         return false;
      } else {
         if (this.existsMap == null) {
            this.existsMap = new HashMap();
         }

         Boolean var2 = (Boolean)this.existsMap.get(var1);
         if (var2 == null) {
            this.getNumberCoreFonts();
            if (!this.coreFontFileNames.contains(var1)) {
               var2 = Boolean.TRUE;
            } else {
               var2 = (new File(var1)).exists();
               this.existsMap.put(var1, var2);
               if (FontUtilities.debugFonts() && var2 == Boolean.FALSE) {
                  logger.warning("Couldn't locate font file " + var1);
               }
            }
         }

         return var2 == Boolean.FALSE;
      }
   }

   public int getNumberCoreFonts() {
      if (this.numCoreFonts == -1) {
         this.numCoreFonts = this.coreFontNameIDs.size();
         Short[] var1 = new Short[0];
         Short[] var2 = (Short[])this.coreFontNameIDs.toArray(var1);
         Short[] var3 = (Short[])this.fallbackFontNameIDs.toArray(var1);
         int var4 = 0;

         int var5;
         for(var5 = 0; var5 < var3.length; ++var5) {
            if (this.coreFontNameIDs.contains(var3[var5])) {
               var3[var5] = null;
            } else {
               ++var4;
            }
         }

         this.componentFonts = new String[this.numCoreFonts + var4];
         Object var6 = null;

         short var8;
         for(var5 = 0; var5 < var2.length; ++var5) {
            short var7 = var2[var5];
            var8 = getComponentFileID(var7);
            this.componentFonts[var5] = getComponentFontName(var7);
            String var9 = getComponentFileName(var8);
            if (var9 != null) {
               this.coreFontFileNames.add(var9);
            }

            this.filenamesMap.put(this.componentFonts[var5], this.mapFileName(var9));
         }

         for(int var10 = 0; var10 < var3.length; ++var10) {
            if (var3[var10] != null) {
               var8 = var3[var10];
               short var11 = getComponentFileID(var8);
               this.componentFonts[var5] = getComponentFontName(var8);
               this.filenamesMap.put(this.componentFonts[var5], this.mapFileName(getComponentFileName(var11)));
               ++var5;
            }
         }
      }

      return this.numCoreFonts;
   }

   public String[] getPlatformFontNames() {
      if (this.numCoreFonts == -1) {
         this.getNumberCoreFonts();
      }

      return this.componentFonts;
   }

   public String getFileNameFromPlatformName(String var1) {
      return (String)this.filenamesMap.get(var1);
   }

   public String getExtraFontPath() {
      return getString(head[16]);
   }

   public String getVersion() {
      return getString(head[17]);
   }

   protected static FontConfiguration getFontConfiguration() {
      return fontConfig;
   }

   protected void setFontConfiguration() {
      fontConfig = this;
   }

   private static void sanityCheck() {
      int var0 = 0;
      String var1 = (String)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return System.getProperty("os.name");
         }
      });

      int var2;
      for(var2 = 1; var2 < table_filenames.length; ++var2) {
         if (table_filenames[var2] == -1) {
            if (var1.contains("Windows")) {
               System.err.println("\n Error: <filename." + getString(table_componentFontNameIDs[var2]) + "> entry is missing!!!");
               ++var0;
            } else if (verbose && !isEmpty(table_filenames)) {
               System.err.println("\n Note: 'filename' entry is undefined for \"" + getString(table_componentFontNameIDs[var2]) + "\"");
            }
         }
      }

      for(var2 = 0; var2 < table_scriptIDs.length; ++var2) {
         short var3 = table_scriptFonts[var2];
         if (var3 == 0) {
            System.out.println("\n Error: <allfonts." + getString(table_scriptIDs[var2]) + "> entry is missing!!!");
            ++var0;
         } else if (var3 < 0) {
            var3 = (short)(-var3);

            for(int var4 = 0; var4 < 5; ++var4) {
               for(int var5 = 0; var5 < 4; ++var5) {
                  int var6 = var4 * 4 + var5;
                  short var7 = table_scriptFonts[var3 + var6];
                  if (var7 == 0) {
                     System.err.println("\n Error: <" + getFontName(var4) + "." + getStyleName(var5) + "." + getString(table_scriptIDs[var2]) + "> entry is missing!!!");
                     ++var0;
                  }
               }
            }
         }
      }

      if ("SunOS".equals(var1)) {
         for(var2 = 0; var2 < table_awtfontpaths.length; ++var2) {
            if (table_awtfontpaths[var2] == 0) {
               String var8 = getString(table_scriptIDs[var2]);
               if (!var8.contains("lucida") && !var8.contains("dingbats") && !var8.contains("symbol")) {
                  System.err.println("\nError: <awtfontpath." + var8 + "> entry is missing!!!");
                  ++var0;
               }
            }
         }
      }

      if (var0 != 0) {
         System.err.println("!!THERE ARE " + var0 + " ERROR(S) IN THE FONTCONFIG FILE, PLEASE CHECK ITS CONTENT!!\n");
         System.exit(1);
      }

   }

   private static boolean isEmpty(short[] var0) {
      short[] var1 = var0;
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         short var4 = var1[var3];
         if (var4 != -1) {
            return false;
         }
      }

      return true;
   }

   private static void dump() {
      System.out.println("\n----Head Table------------");

      int var0;
      for(var0 = 0; var0 < 20; ++var0) {
         System.out.println("  " + var0 + " : " + head[var0]);
      }

      System.out.println("\n----scriptIDs-------------");
      printTable(table_scriptIDs, 0);
      System.out.println("\n----scriptFonts----------------");

      short var1;
      for(var0 = 0; var0 < table_scriptIDs.length; ++var0) {
         var1 = table_scriptFonts[var0];
         if (var1 >= 0) {
            System.out.println("  allfonts." + getString(table_scriptIDs[var0]) + "=" + getString(table_componentFontNameIDs[var1]));
         }
      }

      int var2;
      for(var0 = 0; var0 < table_scriptIDs.length; ++var0) {
         var1 = table_scriptFonts[var0];
         if (var1 < 0) {
            var1 = (short)(-var1);

            for(var2 = 0; var2 < 5; ++var2) {
               for(int var3 = 0; var3 < 4; ++var3) {
                  int var4 = var2 * 4 + var3;
                  short var5 = table_scriptFonts[var1 + var4];
                  System.out.println("  " + getFontName(var2) + "." + getStyleName(var3) + "." + getString(table_scriptIDs[var0]) + "=" + getString(table_componentFontNameIDs[var5]));
               }
            }
         }
      }

      System.out.println("\n----elcIDs----------------");
      printTable(table_elcIDs, 0);
      System.out.println("\n----sequences-------------");

      short[] var6;
      for(var0 = 0; var0 < table_elcIDs.length; ++var0) {
         System.out.println("  " + var0 + "/" + getString(table_elcIDs[var0]));
         var6 = getShortArray(table_sequences[var0 * 5 + 0]);

         for(var2 = 0; var2 < var6.length; ++var2) {
            System.out.println("     " + getString(table_scriptIDs[var6[var2]]));
         }
      }

      System.out.println("\n----fontfileNameIDs-------");
      printTable(table_fontfileNameIDs, 0);
      System.out.println("\n----componentFontNameIDs--");
      printTable(table_componentFontNameIDs, 1);
      System.out.println("\n----filenames-------------");

      for(var0 = 0; var0 < table_filenames.length; ++var0) {
         if (table_filenames[var0] == -1) {
            System.out.println("  " + var0 + " : null");
         } else {
            System.out.println("  " + var0 + " : " + getString(table_fontfileNameIDs[table_filenames[var0]]));
         }
      }

      System.out.println("\n----awtfontpaths---------");

      for(var0 = 0; var0 < table_awtfontpaths.length; ++var0) {
         System.out.println("  " + getString(table_scriptIDs[var0]) + " : " + getString(table_awtfontpaths[var0]));
      }

      System.out.println("\n----proportionals--------");

      for(var0 = 0; var0 < table_proportionals.length; ++var0) {
         System.out.println("  " + getString(table_componentFontNameIDs[table_proportionals[var0++]]) + " -> " + getString(table_componentFontNameIDs[table_proportionals[var0]]));
      }

      var0 = 0;
      System.out.println("\n----alphabeticSuffix----");

      while(var0 < table_alphabeticSuffix.length) {
         System.out.println("    " + getString(table_elcIDs[table_alphabeticSuffix[var0++]]) + " -> " + getString(table_alphabeticSuffix[var0++]));
      }

      System.out.println("\n----String Table---------");
      System.out.println("    stringID:    Num =" + table_stringIDs.length);
      System.out.println("    stringTable: Size=" + table_stringTable.length * 2);
      System.out.println("\n----fallbackScriptIDs---");
      var6 = getShortArray(head[15]);

      for(var2 = 0; var2 < var6.length; ++var2) {
         System.out.println("  " + getString(table_scriptIDs[var6[var2]]));
      }

      System.out.println("\n----appendedfontpath-----");
      System.out.println("  " + getString(head[16]));
      System.out.println("\n----Version--------------");
      System.out.println("  " + getString(head[17]));
   }

   protected static short getComponentFontID(short var0, int var1, int var2) {
      short var3 = table_scriptFonts[var0];
      return var3 >= 0 ? var3 : table_scriptFonts[-var3 + var1 * 4 + var2];
   }

   protected static short getComponentFontIDMotif(short var0, int var1, int var2) {
      if (table_scriptFontsMotif.length == 0) {
         return 0;
      } else {
         short var3 = table_scriptFontsMotif[var0];
         return var3 >= 0 ? var3 : table_scriptFontsMotif[-var3 + var1 * 4 + var2];
      }
   }

   private static int[] getExclusionRanges(short var0) {
      short var1 = table_exclusions[var0];
      if (var1 == 0) {
         return EMPTY_INT_ARRAY;
      } else {
         char[] var2 = getString(var1).toCharArray();
         int[] var3 = new int[var2.length / 2];
         int var4 = 0;

         for(int var5 = 0; var5 < var3.length; ++var5) {
            var3[var5] = (var2[var4++] << 16) + (var2[var4++] & '\uffff');
         }

         return var3;
      }
   }

   private static boolean contains(short[] var0, short var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         if (var0[var3] == var1) {
            return true;
         }
      }

      return false;
   }

   protected static String getComponentFontName(short var0) {
      return var0 < 0 ? null : getString(table_componentFontNameIDs[var0]);
   }

   private static String getComponentFileName(short var0) {
      return var0 < 0 ? null : getString(table_fontfileNameIDs[var0]);
   }

   private static short getComponentFileID(short var0) {
      return table_filenames[var0];
   }

   private static String getScriptName(short var0) {
      return getString(table_scriptIDs[var0]);
   }

   protected short[] getCoreScripts(int var1) {
      short var2 = this.getInitELC();
      short[] var3 = getShortArray(table_sequences[var2 * 5 + var1]);
      if (this.preferLocaleFonts) {
         if (this.reorderScripts == null) {
            this.reorderScripts = new HashMap();
         }

         String[] var4 = new String[var3.length];

         int var5;
         for(var5 = 0; var5 < var4.length; ++var5) {
            var4[var5] = getScriptName(var3[var5]);
            this.reorderScripts.put(var4[var5], var3[var5]);
         }

         this.reorderSequenceForLocale(var4);

         for(var5 = 0; var5 < var4.length; ++var5) {
            var3[var5] = (Short)this.reorderScripts.get(var4[var5]);
         }
      }

      return var3;
   }

   private static short[] getFallbackScripts() {
      return getShortArray(head[15]);
   }

   private static void printTable(short[] var0, int var1) {
      for(int var2 = var1; var2 < var0.length; ++var2) {
         System.out.println("  " + var2 + " : " + getString(var0[var2]));
      }

   }

   private static short[] readShortTable(DataInputStream var0, int var1) throws IOException {
      if (var1 == 0) {
         return EMPTY_SHORT_ARRAY;
      } else {
         short[] var2 = new short[var1];
         byte[] var3 = new byte[var1 * 2];
         var0.read(var3);
         int var4 = 0;

         for(int var5 = 0; var4 < var1; var2[var4++] = (short)(var3[var5++] << 8 | var3[var5++] & 255)) {
         }

         return var2;
      }
   }

   private static void writeShortTable(DataOutputStream var0, short[] var1) throws IOException {
      short[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         short var5 = var2[var4];
         var0.writeShort(var5);
      }

   }

   private static short[] toList(HashMap<String, Short> var0) {
      short[] var1 = new short[var0.size()];
      Arrays.fill((short[])var1, (short)-1);

      Map.Entry var3;
      for(Iterator var2 = var0.entrySet().iterator(); var2.hasNext(); var1[(Short)var3.getValue()] = getStringID((String)var3.getKey())) {
         var3 = (Map.Entry)var2.next();
      }

      return var1;
   }

   protected static String getString(short var0) {
      if (var0 == 0) {
         return null;
      } else {
         if (stringCache[var0] == null) {
            stringCache[var0] = new String(table_stringTable, table_stringIDs[var0], table_stringIDs[var0 + 1] - table_stringIDs[var0]);
         }

         return stringCache[var0];
      }
   }

   private static short[] getShortArray(short var0) {
      String var1 = getString(var0);
      char[] var2 = var1.toCharArray();
      short[] var3 = new short[var2.length];

      for(int var4 = 0; var4 < var2.length; ++var4) {
         var3[var4] = (short)(var2[var4] & '\uffff');
      }

      return var3;
   }

   private static short getStringID(String var0) {
      if (var0 == null) {
         return 0;
      } else {
         short var1 = (short)stringTable.length();
         stringTable.append(var0);
         short var2 = (short)stringTable.length();
         stringIDs[stringIDNum] = var1;
         stringIDs[stringIDNum + 1] = var2;
         ++stringIDNum;
         if (stringIDNum + 1 >= stringIDs.length) {
            short[] var3 = new short[stringIDNum + 1000];
            System.arraycopy(stringIDs, 0, var3, 0, stringIDNum);
            stringIDs = var3;
         }

         return (short)(stringIDNum - 1);
      }
   }

   private static short getShortArrayID(short[] var0) {
      char[] var1 = new char[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = (char)var0[var2];
      }

      String var3 = new String(var1);
      return getStringID(var3);
   }

   static class PropertiesHandler {
      private HashMap<String, Short> scriptIDs;
      private HashMap<String, Short> elcIDs;
      private HashMap<String, Short> componentFontNameIDs;
      private HashMap<String, Short> fontfileNameIDs;
      private HashMap<String, Integer> logicalFontIDs;
      private HashMap<String, Integer> fontStyleIDs;
      private HashMap<Short, Short> filenames;
      private HashMap<Short, short[]> sequences;
      private HashMap<Short, Short[]> scriptFonts;
      private HashMap<Short, Short> scriptAllfonts;
      private HashMap<Short, int[]> exclusions;
      private HashMap<Short, Short> awtfontpaths;
      private HashMap<Short, Short> proportionals;
      private HashMap<Short, Short> scriptAllfontsMotif;
      private HashMap<Short, Short[]> scriptFontsMotif;
      private HashMap<Short, Short> alphabeticSuffix;
      private short[] fallbackScriptIDs;
      private String version;
      private String appendedfontpath;

      public void load(InputStream var1) throws IOException {
         this.initLogicalNameStyle();
         this.initHashMaps();
         FontConfiguration.PropertiesHandler.FontProperties var2 = new FontConfiguration.PropertiesHandler.FontProperties();
         var2.load(var1);
         this.initBinaryTable();
      }

      private void initBinaryTable() {
         FontConfiguration.head = new short[20];
         FontConfiguration.head[0] = 20;
         FontConfiguration.table_scriptIDs = FontConfiguration.toList(this.scriptIDs);
         FontConfiguration.head[1] = (short)(FontConfiguration.head[0] + FontConfiguration.table_scriptIDs.length);
         int var1 = FontConfiguration.table_scriptIDs.length + this.scriptFonts.size() * 20;
         FontConfiguration.table_scriptFonts = new short[var1];

         Map.Entry var3;
         for(Iterator var2 = this.scriptAllfonts.entrySet().iterator(); var2.hasNext(); FontConfiguration.table_scriptFonts[((Short)var3.getKey()).intValue()] = (Short)var3.getValue()) {
            var3 = (Map.Entry)var2.next();
         }

         int var9 = FontConfiguration.table_scriptIDs.length;
         Iterator var10 = this.scriptFonts.entrySet().iterator();

         Map.Entry var4;
         while(var10.hasNext()) {
            var4 = (Map.Entry)var10.next();
            FontConfiguration.table_scriptFonts[((Short)var4.getKey()).intValue()] = (short)(-var9);
            Short[] var5 = (Short[])var4.getValue();

            for(int var6 = 0; var6 < 20; ++var6) {
               if (var5[var6] != null) {
                  FontConfiguration.table_scriptFonts[var9++] = var5[var6];
               } else {
                  FontConfiguration.table_scriptFonts[var9++] = 0;
               }
            }
         }

         FontConfiguration.head[2] = (short)(FontConfiguration.head[1] + FontConfiguration.table_scriptFonts.length);
         FontConfiguration.table_elcIDs = FontConfiguration.toList(this.elcIDs);
         FontConfiguration.head[3] = (short)(FontConfiguration.head[2] + FontConfiguration.table_elcIDs.length);
         FontConfiguration.table_sequences = new short[this.elcIDs.size() * 5];
         var10 = this.sequences.entrySet().iterator();

         while(true) {
            int var7;
            while(var10.hasNext()) {
               var4 = (Map.Entry)var10.next();
               int var12 = ((Short)var4.getKey()).intValue();
               short[] var16 = (short[])var4.getValue();
               if (var16.length == 1) {
                  for(var7 = 0; var7 < 5; ++var7) {
                     FontConfiguration.table_sequences[var12 * 5 + var7] = var16[0];
                  }
               } else {
                  for(var7 = 0; var7 < 5; ++var7) {
                     FontConfiguration.table_sequences[var12 * 5 + var7] = var16[var7];
                  }
               }
            }

            FontConfiguration.head[4] = (short)(FontConfiguration.head[3] + FontConfiguration.table_sequences.length);
            FontConfiguration.table_fontfileNameIDs = FontConfiguration.toList(this.fontfileNameIDs);
            FontConfiguration.head[5] = (short)(FontConfiguration.head[4] + FontConfiguration.table_fontfileNameIDs.length);
            FontConfiguration.table_componentFontNameIDs = FontConfiguration.toList(this.componentFontNameIDs);
            FontConfiguration.head[6] = (short)(FontConfiguration.head[5] + FontConfiguration.table_componentFontNameIDs.length);
            FontConfiguration.table_filenames = new short[FontConfiguration.table_componentFontNameIDs.length];
            Arrays.fill((short[])FontConfiguration.table_filenames, (short)-1);

            for(var10 = this.filenames.entrySet().iterator(); var10.hasNext(); FontConfiguration.table_filenames[(Short)var4.getKey()] = (Short)var4.getValue()) {
               var4 = (Map.Entry)var10.next();
            }

            FontConfiguration.head[7] = (short)(FontConfiguration.head[6] + FontConfiguration.table_filenames.length);
            FontConfiguration.table_awtfontpaths = new short[FontConfiguration.table_scriptIDs.length];

            for(var10 = this.awtfontpaths.entrySet().iterator(); var10.hasNext(); FontConfiguration.table_awtfontpaths[(Short)var4.getKey()] = (Short)var4.getValue()) {
               var4 = (Map.Entry)var10.next();
            }

            FontConfiguration.head[8] = (short)(FontConfiguration.head[7] + FontConfiguration.table_awtfontpaths.length);
            FontConfiguration.table_exclusions = new short[this.scriptIDs.size()];

            char[] var17;
            for(var10 = this.exclusions.entrySet().iterator(); var10.hasNext(); FontConfiguration.table_exclusions[(Short)var4.getKey()] = FontConfiguration.getStringID(new String(var17))) {
               var4 = (Map.Entry)var10.next();
               int[] var14 = (int[])var4.getValue();
               var17 = new char[var14.length * 2];
               var7 = 0;

               for(int var8 = 0; var8 < var14.length; ++var8) {
                  var17[var7++] = (char)(var14[var8] >> 16);
                  var17[var7++] = (char)(var14[var8] & '\uffff');
               }
            }

            FontConfiguration.head[9] = (short)(FontConfiguration.head[8] + FontConfiguration.table_exclusions.length);
            FontConfiguration.table_proportionals = new short[this.proportionals.size() * 2];
            int var11 = 0;

            Iterator var13;
            Map.Entry var15;
            for(var13 = this.proportionals.entrySet().iterator(); var13.hasNext(); FontConfiguration.table_proportionals[var11++] = (Short)var15.getValue()) {
               var15 = (Map.Entry)var13.next();
               FontConfiguration.table_proportionals[var11++] = (Short)var15.getKey();
            }

            FontConfiguration.head[10] = (short)(FontConfiguration.head[9] + FontConfiguration.table_proportionals.length);
            if (this.scriptAllfontsMotif.size() == 0 && this.scriptFontsMotif.size() == 0) {
               FontConfiguration.table_scriptFontsMotif = FontConfiguration.EMPTY_SHORT_ARRAY;
            } else {
               var1 = FontConfiguration.table_scriptIDs.length + this.scriptFontsMotif.size() * 20;
               FontConfiguration.table_scriptFontsMotif = new short[var1];

               for(var13 = this.scriptAllfontsMotif.entrySet().iterator(); var13.hasNext(); FontConfiguration.table_scriptFontsMotif[((Short)var15.getKey()).intValue()] = (Short)var15.getValue()) {
                  var15 = (Map.Entry)var13.next();
               }

               var9 = FontConfiguration.table_scriptIDs.length;
               var13 = this.scriptFontsMotif.entrySet().iterator();

               while(var13.hasNext()) {
                  var15 = (Map.Entry)var13.next();
                  FontConfiguration.table_scriptFontsMotif[((Short)var15.getKey()).intValue()] = (short)(-var9);
                  Short[] var18 = (Short[])var15.getValue();

                  for(var7 = 0; var7 < 20; ++var7) {
                     if (var18[var7] != null) {
                        FontConfiguration.table_scriptFontsMotif[var9++] = var18[var7];
                     } else {
                        FontConfiguration.table_scriptFontsMotif[var9++] = 0;
                     }
                  }
               }
            }

            FontConfiguration.head[11] = (short)(FontConfiguration.head[10] + FontConfiguration.table_scriptFontsMotif.length);
            FontConfiguration.table_alphabeticSuffix = new short[this.alphabeticSuffix.size() * 2];
            var11 = 0;

            for(var13 = this.alphabeticSuffix.entrySet().iterator(); var13.hasNext(); FontConfiguration.table_alphabeticSuffix[var11++] = (Short)var15.getValue()) {
               var15 = (Map.Entry)var13.next();
               FontConfiguration.table_alphabeticSuffix[var11++] = (Short)var15.getKey();
            }

            FontConfiguration.head[15] = FontConfiguration.getShortArrayID(this.fallbackScriptIDs);
            FontConfiguration.head[16] = FontConfiguration.getStringID(this.appendedfontpath);
            FontConfiguration.head[17] = FontConfiguration.getStringID(this.version);
            FontConfiguration.head[12] = (short)(FontConfiguration.head[11] + FontConfiguration.table_alphabeticSuffix.length);
            FontConfiguration.table_stringIDs = new short[FontConfiguration.stringIDNum + 1];
            System.arraycopy(FontConfiguration.stringIDs, 0, FontConfiguration.table_stringIDs, 0, FontConfiguration.stringIDNum + 1);
            FontConfiguration.head[13] = (short)(FontConfiguration.head[12] + FontConfiguration.stringIDNum + 1);
            FontConfiguration.table_stringTable = FontConfiguration.stringTable.toString().toCharArray();
            FontConfiguration.head[14] = (short)(FontConfiguration.head[13] + FontConfiguration.stringTable.length());
            FontConfiguration.stringCache = new String[FontConfiguration.table_stringIDs.length];
            return;
         }
      }

      private void initLogicalNameStyle() {
         this.logicalFontIDs = new HashMap();
         this.fontStyleIDs = new HashMap();
         this.logicalFontIDs.put("serif", 0);
         this.logicalFontIDs.put("sansserif", 1);
         this.logicalFontIDs.put("monospaced", 2);
         this.logicalFontIDs.put("dialog", 3);
         this.logicalFontIDs.put("dialoginput", 4);
         this.fontStyleIDs.put("plain", 0);
         this.fontStyleIDs.put("bold", 1);
         this.fontStyleIDs.put("italic", 2);
         this.fontStyleIDs.put("bolditalic", 3);
      }

      private void initHashMaps() {
         this.scriptIDs = new HashMap();
         this.elcIDs = new HashMap();
         this.componentFontNameIDs = new HashMap();
         this.componentFontNameIDs.put("", Short.valueOf((short)0));
         this.fontfileNameIDs = new HashMap();
         this.filenames = new HashMap();
         this.sequences = new HashMap();
         this.scriptFonts = new HashMap();
         this.scriptAllfonts = new HashMap();
         this.exclusions = new HashMap();
         this.awtfontpaths = new HashMap();
         this.proportionals = new HashMap();
         this.scriptFontsMotif = new HashMap();
         this.scriptAllfontsMotif = new HashMap();
         this.alphabeticSuffix = new HashMap();
         this.fallbackScriptIDs = FontConfiguration.EMPTY_SHORT_ARRAY;
      }

      private int[] parseExclusions(String var1, String var2) {
         if (var2 == null) {
            return FontConfiguration.EMPTY_INT_ARRAY;
         } else {
            int var3 = 1;

            int var4;
            for(var4 = 0; (var4 = var2.indexOf(44, var4)) != -1; ++var4) {
               ++var3;
            }

            int[] var5 = new int[var3 * 2];
            var4 = 0;
            boolean var6 = false;

            int var17;
            for(int var7 = 0; var7 < var3 * 2; var5[var7++] = var17) {
               boolean var10 = false;
               boolean var11 = false;

               int var16;
               try {
                  int var15 = var2.indexOf(45, var4);
                  String var8 = var2.substring(var4, var15);
                  var4 = var15 + 1;
                  var15 = var2.indexOf(44, var4);
                  if (var15 == -1) {
                     var15 = var2.length();
                  }

                  String var9 = var2.substring(var4, var15);
                  var4 = var15 + 1;
                  int var12 = var8.length();
                  int var13 = var9.length();
                  if (var12 != 4 && var12 != 6 || var13 != 4 && var13 != 6) {
                     throw new Exception();
                  }

                  var16 = Integer.parseInt(var8, 16);
                  var17 = Integer.parseInt(var9, 16);
                  if (var16 > var17) {
                     throw new Exception();
                  }
               } catch (Exception var14) {
                  if (FontUtilities.debugFonts() && FontConfiguration.logger != null) {
                     FontConfiguration.logger.config("Failed parsing " + var1 + " property of font configuration.");
                  }

                  return FontConfiguration.EMPTY_INT_ARRAY;
               }

               var5[var7++] = var16;
            }

            return var5;
         }
      }

      private Short getID(HashMap<String, Short> var1, String var2) {
         Short var3 = (Short)var1.get(var2);
         if (var3 == null) {
            var1.put(var2, (short)var1.size());
            return (Short)var1.get(var2);
         } else {
            return var3;
         }
      }

      private void parseProperty(String var1, String var2) {
         if (var1.startsWith("filename.")) {
            var1 = var1.substring(9);
            if (!"MingLiU_HKSCS".equals(var1)) {
               var1 = var1.replace('_', ' ');
            }

            Short var13 = this.getID(this.componentFontNameIDs, var1);
            Short var15 = this.getID(this.fontfileNameIDs, var2);
            this.filenames.put(var13, var15);
         } else if (var1.startsWith("exclusion.")) {
            var1 = var1.substring(10);
            this.exclusions.put(this.getID(this.scriptIDs, var1), this.parseExclusions(var1, var2));
         } else {
            Short var8;
            if (var1.startsWith("sequence.")) {
               var1 = var1.substring(9);
               boolean var12 = false;
               boolean var14 = false;
               String[] var16 = (String[])((String[])FontConfiguration.splitSequence(var2).toArray(FontConfiguration.EMPTY_STRING_ARRAY));
               short[] var17 = new short[var16.length];

               for(int var18 = 0; var18 < var16.length; ++var18) {
                  if ("alphabetic/default".equals(var16[var18])) {
                     var16[var18] = "alphabetic";
                     var12 = true;
                  } else if ("alphabetic/1252".equals(var16[var18])) {
                     var16[var18] = "alphabetic";
                     var14 = true;
                  }

                  var17[var18] = this.getID(this.scriptIDs, var16[var18]);
               }

               short var19 = FontConfiguration.getShortArrayID(var17);
               var8 = null;
               int var21 = var1.indexOf(46);
               if (var21 == -1) {
                  if ("fallback".equals(var1)) {
                     this.fallbackScriptIDs = var17;
                     return;
                  }

                  if (!"allfonts".equals(var1)) {
                     if (FontConfiguration.logger != null) {
                        FontConfiguration.logger.config("Error sequence def: <sequence." + var1 + ">");
                     }

                     return;
                  }

                  var8 = this.getID(this.elcIDs, "NULL.NULL.NULL");
               } else {
                  var8 = this.getID(this.elcIDs, var1.substring(var21 + 1));
                  var1 = var1.substring(0, var21);
               }

               Object var10 = null;
               short[] var20;
               if ("allfonts".equals(var1)) {
                  var20 = new short[]{var19};
               } else {
                  var20 = (short[])this.sequences.get(var8);
                  if (var20 == null) {
                     var20 = new short[5];
                  }

                  Integer var11 = (Integer)this.logicalFontIDs.get(var1);
                  if (var11 == null) {
                     if (FontConfiguration.logger != null) {
                        FontConfiguration.logger.config("Unrecognizable logicfont name " + var1);
                     }

                     return;
                  }

                  var20[var11] = var19;
               }

               this.sequences.put(var8, var20);
               if (var12) {
                  this.alphabeticSuffix.put(var8, FontConfiguration.getStringID("default"));
               } else if (var14) {
                  this.alphabeticSuffix.put(var8, FontConfiguration.getStringID("1252"));
               }
            } else if (var1.startsWith("allfonts.")) {
               var1 = var1.substring(9);
               if (var1.endsWith(".motif")) {
                  var1 = var1.substring(0, var1.length() - 6);
                  this.scriptAllfontsMotif.put(this.getID(this.scriptIDs, var1), this.getID(this.componentFontNameIDs, var2));
               } else {
                  this.scriptAllfonts.put(this.getID(this.scriptIDs, var1), this.getID(this.componentFontNameIDs, var2));
               }
            } else if (var1.startsWith("awtfontpath.")) {
               var1 = var1.substring(12);
               this.awtfontpaths.put(this.getID(this.scriptIDs, var1), FontConfiguration.getStringID(var2));
            } else if ("version".equals(var1)) {
               this.version = var2;
            } else if ("appendedfontpath".equals(var1)) {
               this.appendedfontpath = var2;
            } else if (var1.startsWith("proportional.")) {
               var1 = var1.substring(13).replace('_', ' ');
               this.proportionals.put(this.getID(this.componentFontNameIDs, var1), this.getID(this.componentFontNameIDs, var2));
            } else {
               boolean var5 = false;
               int var3 = var1.indexOf(46);
               if (var3 == -1) {
                  if (FontConfiguration.logger != null) {
                     FontConfiguration.logger.config("Failed parsing " + var1 + " property of font configuration.");
                  }

                  return;
               }

               int var4 = var1.indexOf(46, var3 + 1);
               if (var4 == -1) {
                  if (FontConfiguration.logger != null) {
                     FontConfiguration.logger.config("Failed parsing " + var1 + " property of font configuration.");
                  }

                  return;
               }

               if (var1.endsWith(".motif")) {
                  var1 = var1.substring(0, var1.length() - 6);
                  var5 = true;
               }

               Integer var6 = (Integer)this.logicalFontIDs.get(var1.substring(0, var3));
               Integer var7 = (Integer)this.fontStyleIDs.get(var1.substring(var3 + 1, var4));
               var8 = this.getID(this.scriptIDs, var1.substring(var4 + 1));
               if (var6 == null || var7 == null) {
                  if (FontConfiguration.logger != null) {
                     FontConfiguration.logger.config("unrecognizable logicfont name/style at " + var1);
                  }

                  return;
               }

               Short[] var9;
               if (var5) {
                  var9 = (Short[])this.scriptFontsMotif.get(var8);
               } else {
                  var9 = (Short[])this.scriptFonts.get(var8);
               }

               if (var9 == null) {
                  var9 = new Short[20];
               }

               var9[var6 * 4 + var7] = this.getID(this.componentFontNameIDs, var2);
               if (var5) {
                  this.scriptFontsMotif.put(var8, var9);
               } else {
                  this.scriptFonts.put(var8, var9);
               }
            }
         }

      }

      class FontProperties extends Properties {
         public synchronized Object put(Object var1, Object var2) {
            PropertiesHandler.this.parseProperty((String)var1, (String)var2);
            return null;
         }
      }
   }
}
