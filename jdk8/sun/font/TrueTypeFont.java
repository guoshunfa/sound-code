package sun.font;

import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import sun.awt.SunToolkit;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.security.action.GetPropertyAction;

public class TrueTypeFont extends FileFont {
   public static final int cmapTag = 1668112752;
   public static final int glyfTag = 1735162214;
   public static final int headTag = 1751474532;
   public static final int hheaTag = 1751672161;
   public static final int hmtxTag = 1752003704;
   public static final int locaTag = 1819239265;
   public static final int maxpTag = 1835104368;
   public static final int nameTag = 1851878757;
   public static final int postTag = 1886352244;
   public static final int os_2Tag = 1330851634;
   public static final int GDEFTag = 1195656518;
   public static final int GPOSTag = 1196445523;
   public static final int GSUBTag = 1196643650;
   public static final int mortTag = 1836020340;
   public static final int fdscTag = 1717859171;
   public static final int fvarTag = 1719034226;
   public static final int featTag = 1717920116;
   public static final int EBLCTag = 1161972803;
   public static final int gaspTag = 1734439792;
   public static final int ttcfTag = 1953784678;
   public static final int v1ttTag = 65536;
   public static final int trueTag = 1953658213;
   public static final int ottoTag = 1330926671;
   public static final int MS_PLATFORM_ID = 3;
   public static final short ENGLISH_LOCALE_ID = 1033;
   public static final int FAMILY_NAME_ID = 1;
   public static final int FULL_NAME_ID = 4;
   public static final int POSTSCRIPT_NAME_ID = 6;
   private static final short US_LCID = 1033;
   private static Map<String, Short> lcidMap;
   TrueTypeFont.TTDisposerRecord disposerRecord;
   int fontIndex;
   int directoryCount;
   int directoryOffset;
   int numTables;
   TrueTypeFont.DirectoryEntry[] tableDirectory;
   private boolean supportsJA;
   private boolean supportsCJK;
   private Locale nameLocale;
   private String localeFamilyName;
   private String localeFullName;
   private static final int TTCHEADERSIZE = 12;
   private static final int DIRECTORYHEADERSIZE = 12;
   private static final int DIRECTORYENTRYSIZE = 16;
   static final String[] encoding_mapping = new String[]{"cp1252", "cp1250", "cp1251", "cp1253", "cp1254", "cp1255", "cp1256", "cp1257", "", "", "", "", "", "", "", "", "ms874", "ms932", "gbk", "ms949", "ms950", "ms1361", "", "", "", "", "", "", "", "", "", ""};
   private static final String[][] languages = new String[][]{{"en", "ca", "da", "de", "es", "fi", "fr", "is", "it", "nl", "no", "pt", "sq", "sv"}, {"cs", "cz", "et", "hr", "hu", "nr", "pl", "ro", "sk", "sl", "sq", "sr"}, {"bg", "mk", "ru", "sh", "uk"}, {"el"}, {"tr"}, {"he"}, {"ar"}, {"et", "lt", "lv"}, {"th"}, {"ja"}, {"zh", "zh_CN"}, {"ko"}, {"zh_HK", "zh_TW"}, {"ko"}};
   private static final String[] codePages = new String[]{"cp1252", "cp1250", "cp1251", "cp1253", "cp1254", "cp1255", "cp1256", "cp1257", "ms874", "ms932", "gbk", "ms949", "ms950", "ms1361"};
   private static String defaultCodePage = null;
   public static final int reserved_bits1 = Integer.MIN_VALUE;
   public static final int reserved_bits2 = 65535;
   private int fontWidth;
   private int fontWeight;
   private static final int fsSelectionItalicBit = 1;
   private static final int fsSelectionBoldBit = 32;
   private static final int fsSelectionRegularBit = 64;
   private float stSize;
   private float stPos;
   private float ulSize;
   private float ulPos;
   private char[] gaspTable;

   public TrueTypeFont(String var1, Object var2, int var3, boolean var4) throws FontFormatException {
      this(var1, var2, var3, var4, true);
   }

   public TrueTypeFont(String var1, Object var2, int var3, boolean var4, boolean var5) throws FontFormatException {
      super(var1, var2);
      this.disposerRecord = new TrueTypeFont.TTDisposerRecord();
      this.fontIndex = 0;
      this.directoryCount = 1;
      this.fontWidth = 0;
      this.fontWeight = 0;
      this.useJavaRasterizer = var4;
      this.fontRank = 3;

      try {
         this.verify(var5);
         this.init(var3);
         if (!var5) {
            this.close();
         }
      } catch (Throwable var7) {
         this.close();
         if (var7 instanceof FontFormatException) {
            throw (FontFormatException)var7;
         }

         throw new FontFormatException("Unexpected runtime exception.");
      }

      Disposer.addObjectRecord(this, this.disposerRecord);
   }

   protected boolean checkUseNatives() {
      if (this.checkedNatives) {
         return this.useNatives;
      } else if (FontUtilities.isSolaris && !this.useJavaRasterizer && !FontUtilities.useT2K && this.nativeNames != null && this.getDirectoryEntry(1161972803) == null && !GraphicsEnvironment.isHeadless()) {
         if (this.nativeNames instanceof String) {
            String var1 = (String)this.nativeNames;
            if (var1.indexOf("8859") > 0) {
               this.checkedNatives = true;
               return false;
            }

            if (NativeFont.hasExternalBitmaps(var1)) {
               this.nativeFonts = new NativeFont[1];

               try {
                  this.nativeFonts[0] = new NativeFont(var1, true);
                  this.useNatives = true;
               } catch (FontFormatException var7) {
                  this.nativeFonts = null;
               }
            }
         } else if (this.nativeNames instanceof String[]) {
            String[] var8 = (String[])((String[])this.nativeNames);
            int var2 = var8.length;
            boolean var3 = false;

            int var4;
            for(var4 = 0; var4 < var2; ++var4) {
               if (var8[var4].indexOf("8859") > 0) {
                  this.checkedNatives = true;
                  return false;
               }

               if (NativeFont.hasExternalBitmaps(var8[var4])) {
                  var3 = true;
               }
            }

            if (!var3) {
               this.checkedNatives = true;
               return false;
            }

            this.useNatives = true;
            this.nativeFonts = new NativeFont[var2];

            for(var4 = 0; var4 < var2; ++var4) {
               try {
                  this.nativeFonts[var4] = new NativeFont(var8[var4], true);
               } catch (FontFormatException var6) {
                  this.useNatives = false;
                  this.nativeFonts = null;
               }
            }
         }

         if (this.useNatives) {
            this.glyphToCharMap = new char[this.getMapper().getNumGlyphs()];
         }

         this.checkedNatives = true;
         return this.useNatives;
      } else {
         this.checkedNatives = true;
         return false;
      }
   }

   private synchronized FileChannel open() throws FontFormatException {
      return this.open(true);
   }

   private synchronized FileChannel open(boolean var1) throws FontFormatException {
      if (this.disposerRecord.channel == null) {
         if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().info("open TTF: " + this.platName);
         }

         try {
            RandomAccessFile var2 = (RandomAccessFile)AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  try {
                     return new RandomAccessFile(TrueTypeFont.this.platName, "r");
                  } catch (FileNotFoundException var2) {
                     return null;
                  }
               }
            });
            this.disposerRecord.channel = var2.getChannel();
            this.fileSize = (int)this.disposerRecord.channel.size();
            if (var1) {
               FontManager var3 = FontManagerFactory.getInstance();
               if (var3 instanceof SunFontManager) {
                  ((SunFontManager)var3).addToPool(this);
               }
            }
         } catch (NullPointerException var4) {
            this.close();
            throw new FontFormatException(var4.toString());
         } catch (ClosedChannelException var5) {
            Thread.interrupted();
            this.close();
            this.open();
         } catch (IOException var6) {
            this.close();
            throw new FontFormatException(var6.toString());
         }
      }

      return this.disposerRecord.channel;
   }

   protected synchronized void close() {
      this.disposerRecord.dispose();
   }

   int readBlock(ByteBuffer var1, int var2, int var3) {
      int var4 = 0;

      try {
         synchronized(this) {
            if (this.disposerRecord.channel == null) {
               this.open();
            }

            if (var2 + var3 > this.fileSize) {
               if (var2 >= this.fileSize) {
                  if (FontUtilities.isLogging()) {
                     String var15 = "Read offset is " + var2 + " file size is " + this.fileSize + " file is " + this.platName;
                     FontUtilities.getLogger().severe(var15);
                  }

                  return -1;
               }

               var3 = this.fileSize - var2;
            }

            var1.clear();
            this.disposerRecord.channel.position((long)var2);

            while(var4 < var3) {
               int var6 = this.disposerRecord.channel.read(var1);
               if (var6 == -1) {
                  String var7 = "Unexpected EOF " + this;
                  int var8 = (int)this.disposerRecord.channel.size();
                  if (var8 != this.fileSize) {
                     var7 = var7 + " File size was " + this.fileSize + " and now is " + var8;
                  }

                  if (FontUtilities.isLogging()) {
                     FontUtilities.getLogger().severe(var7);
                  }

                  if (var4 <= var3 / 2 && var4 <= 16384) {
                     boolean var14 = true;
                  } else {
                     var1.flip();
                     if (FontUtilities.isLogging()) {
                        var7 = "Returning " + var4 + " bytes instead of " + var3;
                        FontUtilities.getLogger().severe(var7);
                     }
                  }

                  throw new IOException(var7);
               }

               var4 += var6;
            }

            var1.flip();
            if (var4 > var3) {
               var4 = var3;
            }
         }
      } catch (FontFormatException var11) {
         if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().severe("While reading " + this.platName, (Throwable)var11);
         }

         var4 = -1;
         this.deregisterFontAndClearStrikeCache();
      } catch (ClosedChannelException var12) {
         Thread.interrupted();
         this.close();
         return this.readBlock(var1, var2, var3);
      } catch (IOException var13) {
         if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().severe("While reading " + this.platName, (Throwable)var13);
         }

         if (var4 == 0) {
            var4 = -1;
            this.deregisterFontAndClearStrikeCache();
         }
      }

      return var4;
   }

   ByteBuffer readBlock(int var1, int var2) {
      ByteBuffer var3 = ByteBuffer.allocate(var2);

      try {
         synchronized(this) {
            if (this.disposerRecord.channel == null) {
               this.open();
            }

            if (var1 + var2 > this.fileSize) {
               if (var1 > this.fileSize) {
                  return null;
               }

               var3 = ByteBuffer.allocate(this.fileSize - var1);
            }

            this.disposerRecord.channel.position((long)var1);
            this.disposerRecord.channel.read(var3);
            var3.flip();
         }
      } catch (FontFormatException var7) {
         return null;
      } catch (ClosedChannelException var8) {
         Thread.interrupted();
         this.close();
         this.readBlock(var3, var1, var2);
      } catch (IOException var9) {
         return null;
      }

      return var3;
   }

   byte[] readBytes(int var1, int var2) {
      ByteBuffer var3 = this.readBlock(var1, var2);
      if (var3.hasArray()) {
         return var3.array();
      } else {
         byte[] var4 = new byte[var3.limit()];
         var3.get(var4);
         return var4;
      }
   }

   private void verify(boolean var1) throws FontFormatException {
      this.open(var1);
   }

   protected void init(int var1) throws FontFormatException {
      int var2 = 0;
      ByteBuffer var3 = this.readBlock(0, 12);

      ByteBuffer var4;
      try {
         switch(var3.getInt()) {
         case 1953784678:
            var3.getInt();
            this.directoryCount = var3.getInt();
            if (var1 >= this.directoryCount) {
               throw new FontFormatException("Bad collection index");
            }

            this.fontIndex = var1;
            var3 = this.readBlock(12 + 4 * var1, 4);
            var2 = var3.getInt();
         case 65536:
         case 1330926671:
         case 1953658213:
            var3 = this.readBlock(var2 + 4, 2);
            this.numTables = var3.getShort();
            this.directoryOffset = var2 + 12;
            var4 = this.readBlock(this.directoryOffset, this.numTables * 16);
            IntBuffer var5 = var4.asIntBuffer();
            this.tableDirectory = new TrueTypeFont.DirectoryEntry[this.numTables];

            for(int var7 = 0; var7 < this.numTables; ++var7) {
               TrueTypeFont.DirectoryEntry var6;
               this.tableDirectory[var7] = var6 = new TrueTypeFont.DirectoryEntry();
               var6.tag = var5.get();
               var5.get();
               var6.offset = var5.get();
               var6.length = var5.get();
               if (var6.offset + var6.length > this.fileSize) {
                  throw new FontFormatException("bad table, tag=" + var6.tag);
               }
            }

            if (this.getDirectoryEntry(1751474532) == null) {
               throw new FontFormatException("missing head table");
            }

            if (this.getDirectoryEntry(1835104368) == null) {
               throw new FontFormatException("missing maxp table");
            }

            if (this.getDirectoryEntry(1752003704) != null && this.getDirectoryEntry(1751672161) == null) {
               throw new FontFormatException("missing hhea table");
            }

            this.initNames();
            break;
         default:
            throw new FontFormatException("Unsupported sfnt " + this.getPublicFileName());
         }
      } catch (Exception var8) {
         if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().severe(var8.toString());
         }

         if (var8 instanceof FontFormatException) {
            throw (FontFormatException)var8;
         }

         throw new FontFormatException(var8.toString());
      }

      if (this.familyName != null && this.fullName != null) {
         var4 = this.getTableBuffer(1330851634);
         this.setStyle(var4);
         this.setCJKSupport(var4);
      } else {
         throw new FontFormatException("Font name not found");
      }
   }

   static String getCodePage() {
      if (defaultCodePage != null) {
         return defaultCodePage;
      } else {
         if (FontUtilities.isWindows) {
            defaultCodePage = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("file.encoding")));
         } else {
            if (languages.length != codePages.length) {
               throw new InternalError("wrong code pages array length");
            }

            Locale var0 = SunToolkit.getStartupLocale();
            String var1 = var0.getLanguage();
            if (var1 != null) {
               if (var1.equals("zh")) {
                  String var2 = var0.getCountry();
                  if (var2 != null) {
                     var1 = var1 + "_" + var2;
                  }
               }

               for(int var4 = 0; var4 < languages.length; ++var4) {
                  for(int var3 = 0; var3 < languages[var4].length; ++var3) {
                     if (var1.equals(languages[var4][var3])) {
                        defaultCodePage = codePages[var4];
                        return defaultCodePage;
                     }
                  }
               }
            }
         }

         if (defaultCodePage == null) {
            defaultCodePage = "";
         }

         return defaultCodePage;
      }
   }

   boolean supportsEncoding(String var1) {
      if (var1 == null) {
         var1 = getCodePage();
      }

      if ("".equals(var1)) {
         return false;
      } else {
         var1 = var1.toLowerCase();
         if (var1.equals("gb18030")) {
            var1 = "gbk";
         } else if (var1.equals("ms950_hkscs")) {
            var1 = "ms950";
         }

         ByteBuffer var2 = this.getTableBuffer(1330851634);
         if (var2 != null && var2.capacity() >= 86) {
            int var3 = var2.getInt(78);
            int var4 = var2.getInt(82);

            for(int var5 = 0; var5 < encoding_mapping.length; ++var5) {
               if (encoding_mapping[var5].equals(var1) && (1 << var5 & var3) != 0) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }
   }

   private void setCJKSupport(ByteBuffer var1) {
      if (var1 != null && var1.capacity() >= 50) {
         int var2 = var1.getInt(46);
         this.supportsCJK = (var2 & 700383232) != 0;
         this.supportsJA = (var2 & 393216) != 0;
      }
   }

   boolean supportsJA() {
      return this.supportsJA;
   }

   ByteBuffer getTableBuffer(int var1) {
      TrueTypeFont.DirectoryEntry var2 = null;

      int var3;
      for(var3 = 0; var3 < this.numTables; ++var3) {
         if (this.tableDirectory[var3].tag == var1) {
            var2 = this.tableDirectory[var3];
            break;
         }
      }

      if (var2 != null && var2.length != 0 && var2.offset + var2.length <= this.fileSize) {
         boolean var12 = false;
         ByteBuffer var4 = ByteBuffer.allocate(var2.length);
         synchronized(this) {
            try {
               if (this.disposerRecord.channel == null) {
                  this.open();
               }

               this.disposerRecord.channel.position((long)var2.offset);
               var3 = this.disposerRecord.channel.read(var4);
               var4.flip();
            } catch (ClosedChannelException var8) {
               Thread.interrupted();
               this.close();
               return this.getTableBuffer(var1);
            } catch (IOException var9) {
               return null;
            } catch (FontFormatException var10) {
               return null;
            }

            return var3 < var2.length ? null : var4;
         }
      } else {
         return null;
      }
   }

   protected long getLayoutTableCache() {
      try {
         return this.getScaler().getLayoutTableCache();
      } catch (FontScalerException var2) {
         return 0L;
      }
   }

   protected byte[] getTableBytes(int var1) {
      ByteBuffer var2 = this.getTableBuffer(var1);
      if (var2 == null) {
         return null;
      } else {
         if (var2.hasArray()) {
            try {
               return var2.array();
            } catch (Exception var4) {
            }
         }

         byte[] var3 = new byte[this.getTableSize(var1)];
         var2.get(var3);
         return var3;
      }
   }

   int getTableSize(int var1) {
      for(int var2 = 0; var2 < this.numTables; ++var2) {
         if (this.tableDirectory[var2].tag == var1) {
            return this.tableDirectory[var2].length;
         }
      }

      return 0;
   }

   int getTableOffset(int var1) {
      for(int var2 = 0; var2 < this.numTables; ++var2) {
         if (this.tableDirectory[var2].tag == var1) {
            return this.tableDirectory[var2].offset;
         }
      }

      return 0;
   }

   TrueTypeFont.DirectoryEntry getDirectoryEntry(int var1) {
      for(int var2 = 0; var2 < this.numTables; ++var2) {
         if (this.tableDirectory[var2].tag == var1) {
            return this.tableDirectory[var2];
         }
      }

      return null;
   }

   boolean useEmbeddedBitmapsForSize(int var1) {
      if (!this.supportsCJK) {
         return false;
      } else if (this.getDirectoryEntry(1161972803) == null) {
         return false;
      } else {
         ByteBuffer var2 = this.getTableBuffer(1161972803);
         int var3 = var2.getInt(4);

         for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = var2.get(8 + var4 * 48 + 45) & 255;
            if (var5 == var1) {
               return true;
            }
         }

         return false;
      }
   }

   public String getFullName() {
      return this.fullName;
   }

   protected void setStyle() {
      this.setStyle(this.getTableBuffer(1330851634));
   }

   public int getWidth() {
      return this.fontWidth > 0 ? this.fontWidth : super.getWidth();
   }

   public int getWeight() {
      return this.fontWeight > 0 ? this.fontWeight : super.getWeight();
   }

   private void setStyle(ByteBuffer var1) {
      if (var1 != null) {
         if (var1.capacity() >= 8) {
            this.fontWeight = var1.getChar(4) & '\uffff';
            this.fontWidth = var1.getChar(6) & '\uffff';
         }

         if (var1.capacity() < 64) {
            super.setStyle();
         } else {
            int var2 = var1.getChar(62) & '\uffff';
            int var3 = var2 & 1;
            int var4 = var2 & 32;
            int var5 = var2 & 64;
            if (var5 != 0 && (var3 | var4) != 0) {
               super.setStyle();
            } else if ((var5 | var3 | var4) == 0) {
               super.setStyle();
            } else {
               switch(var4 | var3) {
               case 1:
                  this.style = 2;
                  break;
               case 32:
                  if (FontUtilities.isSolaris && this.platName.endsWith("HG-GothicB.ttf")) {
                     this.style = 0;
                  } else {
                     this.style = 1;
                  }
                  break;
               case 33:
                  this.style = 3;
               }

            }
         }
      }
   }

   private void setStrikethroughMetrics(ByteBuffer var1, int var2) {
      if (var1 != null && var1.capacity() >= 30 && var2 >= 0) {
         ShortBuffer var3 = var1.asShortBuffer();
         this.stSize = (float)var3.get(13) / (float)var2;
         this.stPos = (float)(-var3.get(14)) / (float)var2;
      } else {
         this.stSize = 0.05F;
         this.stPos = -0.4F;
      }
   }

   private void setUnderlineMetrics(ByteBuffer var1, int var2) {
      if (var1 != null && var1.capacity() >= 12 && var2 >= 0) {
         ShortBuffer var3 = var1.asShortBuffer();
         this.ulSize = (float)var3.get(5) / (float)var2;
         this.ulPos = (float)(-var3.get(4)) / (float)var2;
      } else {
         this.ulSize = 0.05F;
         this.ulPos = 0.1F;
      }
   }

   public void getStyleMetrics(float var1, float[] var2, int var3) {
      if (this.ulSize == 0.0F && this.ulPos == 0.0F) {
         ByteBuffer var4 = this.getTableBuffer(1751474532);
         int var5 = -1;
         if (var4 != null && var4.capacity() >= 18) {
            ShortBuffer var6 = var4.asShortBuffer();
            var5 = var6.get(9) & '\uffff';
            if (var5 < 16 || var5 > 16384) {
               var5 = 2048;
            }
         }

         ByteBuffer var8 = this.getTableBuffer(1330851634);
         this.setStrikethroughMetrics(var8, var5);
         ByteBuffer var7 = this.getTableBuffer(1886352244);
         this.setUnderlineMetrics(var7, var5);
      }

      var2[var3] = this.stPos * var1;
      var2[var3 + 1] = this.stSize * var1;
      var2[var3 + 2] = this.ulPos * var1;
      var2[var3 + 3] = this.ulSize * var1;
   }

   private String makeString(byte[] var1, int var2, short var3) {
      if (var3 >= 2 && var3 <= 6) {
         byte[] var4 = var1;
         int var5 = var2;
         var1 = new byte[var2];
         var2 = 0;

         for(int var6 = 0; var6 < var5; ++var6) {
            if (var4[var6] != 0) {
               var1[var2++] = var4[var6];
            }
         }
      }

      String var9;
      switch(var3) {
      case 0:
         var9 = "UTF-16";
         break;
      case 1:
         var9 = "UTF-16";
         break;
      case 2:
         var9 = "SJIS";
         break;
      case 3:
         var9 = "GBK";
         break;
      case 4:
         var9 = "MS950";
         break;
      case 5:
         var9 = "EUC_KR";
         break;
      case 6:
         var9 = "Johab";
         break;
      default:
         var9 = "UTF-16";
      }

      try {
         return new String(var1, 0, var2, var9);
      } catch (UnsupportedEncodingException var7) {
         if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().warning(var7 + " EncodingID=" + var3);
         }

         return new String(var1, 0, var2);
      } catch (Throwable var8) {
         return null;
      }
   }

   protected void initNames() {
      byte[] var1 = new byte[256];
      ByteBuffer var2 = this.getTableBuffer(1851878757);
      if (var2 != null) {
         ShortBuffer var3 = var2.asShortBuffer();
         var3.get();
         short var4 = var3.get();
         int var5 = var3.get() & '\uffff';
         this.nameLocale = SunToolkit.getStartupLocale();
         short var6 = getLCIDFromLocale(this.nameLocale);

         for(int var7 = 0; var7 < var4; ++var7) {
            short var8 = var3.get();
            if (var8 != 3) {
               var3.position(var3.position() + 5);
            } else {
               short var9 = var3.get();
               short var10 = var3.get();
               short var11 = var3.get();
               int var12 = var3.get() & '\uffff';
               int var13 = (var3.get() & '\uffff') + var5;
               String var14 = null;
               switch(var11) {
               case 1:
                  if (this.familyName != null && var10 != 1033 && var10 != var6) {
                     break;
                  }

                  var2.position(var13);
                  var2.get(var1, 0, var12);
                  var14 = this.makeString(var1, var12, var9);
                  if (this.familyName == null || var10 == 1033) {
                     this.familyName = var14;
                  }

                  if (var10 == var6) {
                     this.localeFamilyName = var14;
                  }
                  break;
               case 4:
                  if (this.fullName == null || var10 == 1033 || var10 == var6) {
                     var2.position(var13);
                     var2.get(var1, 0, var12);
                     var14 = this.makeString(var1, var12, var9);
                     if (this.fullName == null || var10 == 1033) {
                        this.fullName = var14;
                     }

                     if (var10 == var6) {
                        this.localeFullName = var14;
                     }
                  }
               }
            }
         }

         if (this.localeFamilyName == null) {
            this.localeFamilyName = this.familyName;
         }

         if (this.localeFullName == null) {
            this.localeFullName = this.fullName;
         }
      }

   }

   protected String lookupName(short var1, int var2) {
      String var3 = null;
      byte[] var4 = new byte[1024];
      ByteBuffer var5 = this.getTableBuffer(1851878757);
      if (var5 != null) {
         ShortBuffer var6 = var5.asShortBuffer();
         var6.get();
         short var7 = var6.get();
         int var8 = var6.get() & '\uffff';

         for(int var9 = 0; var9 < var7; ++var9) {
            short var10 = var6.get();
            if (var10 != 3) {
               var6.position(var6.position() + 5);
            } else {
               short var11 = var6.get();
               short var12 = var6.get();
               short var13 = var6.get();
               int var14 = var6.get() & '\uffff';
               int var15 = (var6.get() & '\uffff') + var8;
               if (var13 == var2 && (var3 == null && var12 == 1033 || var12 == var1)) {
                  var5.position(var15);
                  var5.get(var4, 0, var14);
                  var3 = this.makeString(var4, var14, var11);
                  if (var12 == var1) {
                     return var3;
                  }
               }
            }
         }
      }

      return var3;
   }

   public int getFontCount() {
      return this.directoryCount;
   }

   protected synchronized FontScaler getScaler() {
      if (this.scaler == null) {
         this.scaler = FontScaler.getScaler(this, this.fontIndex, this.supportsCJK, this.fileSize);
      }

      return this.scaler;
   }

   public String getPostscriptName() {
      String var1 = this.lookupName((short)1033, 6);
      return var1 == null ? this.fullName : var1;
   }

   public String getFontName(Locale var1) {
      if (var1 == null) {
         return this.fullName;
      } else if (var1.equals(this.nameLocale) && this.localeFullName != null) {
         return this.localeFullName;
      } else {
         short var2 = getLCIDFromLocale(var1);
         String var3 = this.lookupName(var2, 4);
         return var3 == null ? this.fullName : var3;
      }
   }

   private static void addLCIDMapEntry(Map<String, Short> var0, String var1, short var2) {
      var0.put(var1, var2);
   }

   private static synchronized void createLCIDMap() {
      if (lcidMap == null) {
         HashMap var0 = new HashMap(200);
         addLCIDMapEntry(var0, "ar", (short)1025);
         addLCIDMapEntry(var0, "bg", (short)1026);
         addLCIDMapEntry(var0, "ca", (short)1027);
         addLCIDMapEntry(var0, "zh", (short)1028);
         addLCIDMapEntry(var0, "cs", (short)1029);
         addLCIDMapEntry(var0, "da", (short)1030);
         addLCIDMapEntry(var0, "de", (short)1031);
         addLCIDMapEntry(var0, "el", (short)1032);
         addLCIDMapEntry(var0, "es", (short)1034);
         addLCIDMapEntry(var0, "fi", (short)1035);
         addLCIDMapEntry(var0, "fr", (short)1036);
         addLCIDMapEntry(var0, "iw", (short)1037);
         addLCIDMapEntry(var0, "hu", (short)1038);
         addLCIDMapEntry(var0, "is", (short)1039);
         addLCIDMapEntry(var0, "it", (short)1040);
         addLCIDMapEntry(var0, "ja", (short)1041);
         addLCIDMapEntry(var0, "ko", (short)1042);
         addLCIDMapEntry(var0, "nl", (short)1043);
         addLCIDMapEntry(var0, "no", (short)1044);
         addLCIDMapEntry(var0, "pl", (short)1045);
         addLCIDMapEntry(var0, "pt", (short)1046);
         addLCIDMapEntry(var0, "rm", (short)1047);
         addLCIDMapEntry(var0, "ro", (short)1048);
         addLCIDMapEntry(var0, "ru", (short)1049);
         addLCIDMapEntry(var0, "hr", (short)1050);
         addLCIDMapEntry(var0, "sk", (short)1051);
         addLCIDMapEntry(var0, "sq", (short)1052);
         addLCIDMapEntry(var0, "sv", (short)1053);
         addLCIDMapEntry(var0, "th", (short)1054);
         addLCIDMapEntry(var0, "tr", (short)1055);
         addLCIDMapEntry(var0, "ur", (short)1056);
         addLCIDMapEntry(var0, "in", (short)1057);
         addLCIDMapEntry(var0, "uk", (short)1058);
         addLCIDMapEntry(var0, "be", (short)1059);
         addLCIDMapEntry(var0, "sl", (short)1060);
         addLCIDMapEntry(var0, "et", (short)1061);
         addLCIDMapEntry(var0, "lv", (short)1062);
         addLCIDMapEntry(var0, "lt", (short)1063);
         addLCIDMapEntry(var0, "fa", (short)1065);
         addLCIDMapEntry(var0, "vi", (short)1066);
         addLCIDMapEntry(var0, "hy", (short)1067);
         addLCIDMapEntry(var0, "eu", (short)1069);
         addLCIDMapEntry(var0, "mk", (short)1071);
         addLCIDMapEntry(var0, "tn", (short)1074);
         addLCIDMapEntry(var0, "xh", (short)1076);
         addLCIDMapEntry(var0, "zu", (short)1077);
         addLCIDMapEntry(var0, "af", (short)1078);
         addLCIDMapEntry(var0, "ka", (short)1079);
         addLCIDMapEntry(var0, "fo", (short)1080);
         addLCIDMapEntry(var0, "hi", (short)1081);
         addLCIDMapEntry(var0, "mt", (short)1082);
         addLCIDMapEntry(var0, "se", (short)1083);
         addLCIDMapEntry(var0, "gd", (short)1084);
         addLCIDMapEntry(var0, "ms", (short)1086);
         addLCIDMapEntry(var0, "kk", (short)1087);
         addLCIDMapEntry(var0, "ky", (short)1088);
         addLCIDMapEntry(var0, "sw", (short)1089);
         addLCIDMapEntry(var0, "tt", (short)1092);
         addLCIDMapEntry(var0, "bn", (short)1093);
         addLCIDMapEntry(var0, "pa", (short)1094);
         addLCIDMapEntry(var0, "gu", (short)1095);
         addLCIDMapEntry(var0, "ta", (short)1097);
         addLCIDMapEntry(var0, "te", (short)1098);
         addLCIDMapEntry(var0, "kn", (short)1099);
         addLCIDMapEntry(var0, "ml", (short)1100);
         addLCIDMapEntry(var0, "mr", (short)1102);
         addLCIDMapEntry(var0, "sa", (short)1103);
         addLCIDMapEntry(var0, "mn", (short)1104);
         addLCIDMapEntry(var0, "cy", (short)1106);
         addLCIDMapEntry(var0, "gl", (short)1110);
         addLCIDMapEntry(var0, "dv", (short)1125);
         addLCIDMapEntry(var0, "qu", (short)1131);
         addLCIDMapEntry(var0, "mi", (short)1153);
         addLCIDMapEntry(var0, "ar_IQ", (short)2049);
         addLCIDMapEntry(var0, "zh_CN", (short)2052);
         addLCIDMapEntry(var0, "de_CH", (short)2055);
         addLCIDMapEntry(var0, "en_GB", (short)2057);
         addLCIDMapEntry(var0, "es_MX", (short)2058);
         addLCIDMapEntry(var0, "fr_BE", (short)2060);
         addLCIDMapEntry(var0, "it_CH", (short)2064);
         addLCIDMapEntry(var0, "nl_BE", (short)2067);
         addLCIDMapEntry(var0, "no_NO_NY", (short)2068);
         addLCIDMapEntry(var0, "pt_PT", (short)2070);
         addLCIDMapEntry(var0, "ro_MD", (short)2072);
         addLCIDMapEntry(var0, "ru_MD", (short)2073);
         addLCIDMapEntry(var0, "sr_CS", (short)2074);
         addLCIDMapEntry(var0, "sv_FI", (short)2077);
         addLCIDMapEntry(var0, "az_AZ", (short)2092);
         addLCIDMapEntry(var0, "se_SE", (short)2107);
         addLCIDMapEntry(var0, "ga_IE", (short)2108);
         addLCIDMapEntry(var0, "ms_BN", (short)2110);
         addLCIDMapEntry(var0, "uz_UZ", (short)2115);
         addLCIDMapEntry(var0, "qu_EC", (short)2155);
         addLCIDMapEntry(var0, "ar_EG", (short)3073);
         addLCIDMapEntry(var0, "zh_HK", (short)3076);
         addLCIDMapEntry(var0, "de_AT", (short)3079);
         addLCIDMapEntry(var0, "en_AU", (short)3081);
         addLCIDMapEntry(var0, "fr_CA", (short)3084);
         addLCIDMapEntry(var0, "sr_CS", (short)3098);
         addLCIDMapEntry(var0, "se_FI", (short)3131);
         addLCIDMapEntry(var0, "qu_PE", (short)3179);
         addLCIDMapEntry(var0, "ar_LY", (short)4097);
         addLCIDMapEntry(var0, "zh_SG", (short)4100);
         addLCIDMapEntry(var0, "de_LU", (short)4103);
         addLCIDMapEntry(var0, "en_CA", (short)4105);
         addLCIDMapEntry(var0, "es_GT", (short)4106);
         addLCIDMapEntry(var0, "fr_CH", (short)4108);
         addLCIDMapEntry(var0, "hr_BA", (short)4122);
         addLCIDMapEntry(var0, "ar_DZ", (short)5121);
         addLCIDMapEntry(var0, "zh_MO", (short)5124);
         addLCIDMapEntry(var0, "de_LI", (short)5127);
         addLCIDMapEntry(var0, "en_NZ", (short)5129);
         addLCIDMapEntry(var0, "es_CR", (short)5130);
         addLCIDMapEntry(var0, "fr_LU", (short)5132);
         addLCIDMapEntry(var0, "bs_BA", (short)5146);
         addLCIDMapEntry(var0, "ar_MA", (short)6145);
         addLCIDMapEntry(var0, "en_IE", (short)6153);
         addLCIDMapEntry(var0, "es_PA", (short)6154);
         addLCIDMapEntry(var0, "fr_MC", (short)6156);
         addLCIDMapEntry(var0, "sr_BA", (short)6170);
         addLCIDMapEntry(var0, "ar_TN", (short)7169);
         addLCIDMapEntry(var0, "en_ZA", (short)7177);
         addLCIDMapEntry(var0, "es_DO", (short)7178);
         addLCIDMapEntry(var0, "sr_BA", (short)7194);
         addLCIDMapEntry(var0, "ar_OM", (short)8193);
         addLCIDMapEntry(var0, "en_JM", (short)8201);
         addLCIDMapEntry(var0, "es_VE", (short)8202);
         addLCIDMapEntry(var0, "ar_YE", (short)9217);
         addLCIDMapEntry(var0, "es_CO", (short)9226);
         addLCIDMapEntry(var0, "ar_SY", (short)10241);
         addLCIDMapEntry(var0, "en_BZ", (short)10249);
         addLCIDMapEntry(var0, "es_PE", (short)10250);
         addLCIDMapEntry(var0, "ar_JO", (short)11265);
         addLCIDMapEntry(var0, "en_TT", (short)11273);
         addLCIDMapEntry(var0, "es_AR", (short)11274);
         addLCIDMapEntry(var0, "ar_LB", (short)12289);
         addLCIDMapEntry(var0, "en_ZW", (short)12297);
         addLCIDMapEntry(var0, "es_EC", (short)12298);
         addLCIDMapEntry(var0, "ar_KW", (short)13313);
         addLCIDMapEntry(var0, "en_PH", (short)13321);
         addLCIDMapEntry(var0, "es_CL", (short)13322);
         addLCIDMapEntry(var0, "ar_AE", (short)14337);
         addLCIDMapEntry(var0, "es_UY", (short)14346);
         addLCIDMapEntry(var0, "ar_BH", (short)15361);
         addLCIDMapEntry(var0, "es_PY", (short)15370);
         addLCIDMapEntry(var0, "ar_QA", (short)16385);
         addLCIDMapEntry(var0, "es_BO", (short)16394);
         addLCIDMapEntry(var0, "es_SV", (short)17418);
         addLCIDMapEntry(var0, "es_HN", (short)18442);
         addLCIDMapEntry(var0, "es_NI", (short)19466);
         addLCIDMapEntry(var0, "es_PR", (short)20490);
         lcidMap = var0;
      }
   }

   private static short getLCIDFromLocale(Locale var0) {
      if (var0.equals(Locale.US)) {
         return 1033;
      } else {
         if (lcidMap == null) {
            createLCIDMap();
         }

         int var3;
         for(String var1 = var0.toString(); !"".equals(var1); var1 = var1.substring(0, var3)) {
            Short var2 = (Short)lcidMap.get(var1);
            if (var2 != null) {
               return var2;
            }

            var3 = var1.lastIndexOf(95);
            if (var3 < 1) {
               return 1033;
            }
         }

         return 1033;
      }
   }

   public String getFamilyName(Locale var1) {
      if (var1 == null) {
         return this.familyName;
      } else if (var1.equals(this.nameLocale) && this.localeFamilyName != null) {
         return this.localeFamilyName;
      } else {
         short var2 = getLCIDFromLocale(var1);
         String var3 = this.lookupName(var2, 1);
         return var3 == null ? this.familyName : var3;
      }
   }

   public CharToGlyphMapper getMapper() {
      if (this.mapper == null) {
         this.mapper = new TrueTypeGlyphMapper(this);
      }

      return this.mapper;
   }

   protected void initAllNames(int var1, HashSet var2) {
      byte[] var3 = new byte[256];
      ByteBuffer var4 = this.getTableBuffer(1851878757);
      if (var4 != null) {
         ShortBuffer var5 = var4.asShortBuffer();
         var5.get();
         short var6 = var5.get();
         int var7 = var5.get() & '\uffff';

         for(int var8 = 0; var8 < var6; ++var8) {
            short var9 = var5.get();
            if (var9 != 3) {
               var5.position(var5.position() + 5);
            } else {
               short var10 = var5.get();
               short var11 = var5.get();
               short var12 = var5.get();
               int var13 = var5.get() & '\uffff';
               int var14 = (var5.get() & '\uffff') + var7;
               if (var12 == var1) {
                  var4.position(var14);
                  var4.get(var3, 0, var13);
                  var2.add(this.makeString(var3, var13, var10));
               }
            }
         }
      }

   }

   String[] getAllFamilyNames() {
      HashSet var1 = new HashSet();

      try {
         this.initAllNames(1, var1);
      } catch (Exception var3) {
      }

      return (String[])((String[])var1.toArray(new String[0]));
   }

   String[] getAllFullNames() {
      HashSet var1 = new HashSet();

      try {
         this.initAllNames(4, var1);
      } catch (Exception var3) {
      }

      return (String[])((String[])var1.toArray(new String[0]));
   }

   Point2D.Float getGlyphPoint(long var1, int var3, int var4) {
      try {
         return this.getScaler().getGlyphPoint(var1, var3, var4);
      } catch (FontScalerException var6) {
         return null;
      }
   }

   private char[] getGaspTable() {
      if (this.gaspTable != null) {
         return this.gaspTable;
      } else {
         ByteBuffer var1 = this.getTableBuffer(1734439792);
         if (var1 == null) {
            return this.gaspTable = new char[0];
         } else {
            CharBuffer var2 = var1.asCharBuffer();
            char var3 = var2.get();
            if (var3 > 1) {
               return this.gaspTable = new char[0];
            } else {
               char var4 = var2.get();
               if (4 + var4 * 4 > this.getTableSize(1734439792)) {
                  return this.gaspTable = new char[0];
               } else {
                  this.gaspTable = new char[2 * var4];
                  var2.get(this.gaspTable);
                  return this.gaspTable;
               }
            }
         }
      }
   }

   public boolean useAAForPtSize(int var1) {
      char[] var2 = this.getGaspTable();
      if (var2.length > 0) {
         for(int var3 = 0; var3 < var2.length; var3 += 2) {
            if (var1 <= var2[var3]) {
               return (var2[var3 + 1] & 2) != 0;
            }
         }

         return true;
      } else if (this.style == 1) {
         return true;
      } else {
         return var1 <= 8 || var1 >= 18;
      }
   }

   public boolean hasSupplementaryChars() {
      return ((TrueTypeGlyphMapper)this.getMapper()).hasSupplementaryChars();
   }

   public String toString() {
      return "** TrueType Font: Family=" + this.familyName + " Name=" + this.fullName + " style=" + this.style + " fileName=" + this.getPublicFileName();
   }

   private static class TTDisposerRecord implements DisposerRecord {
      FileChannel channel;

      private TTDisposerRecord() {
         this.channel = null;
      }

      public synchronized void dispose() {
         try {
            if (this.channel != null) {
               this.channel.close();
            }
         } catch (IOException var5) {
         } finally {
            this.channel = null;
         }

      }

      // $FF: synthetic method
      TTDisposerRecord(Object var1) {
         this();
      }
   }

   static class DirectoryEntry {
      int tag;
      int offset;
      int length;
   }
}
