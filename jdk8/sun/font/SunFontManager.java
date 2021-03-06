package sun.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.plaf.FontUIResource;
import sun.applet.AppletSecurity;
import sun.awt.AppContext;
import sun.awt.FontConfiguration;
import sun.awt.SunToolkit;
import sun.java2d.FontSupport;
import sun.misc.ThreadGroupUtils;
import sun.util.logging.PlatformLogger;

public abstract class SunFontManager implements FontSupport, FontManagerForSGE {
   public static final int FONTFORMAT_NONE = -1;
   public static final int FONTFORMAT_TRUETYPE = 0;
   public static final int FONTFORMAT_TYPE1 = 1;
   public static final int FONTFORMAT_T2K = 2;
   public static final int FONTFORMAT_TTC = 3;
   public static final int FONTFORMAT_COMPOSITE = 4;
   public static final int FONTFORMAT_NATIVE = 5;
   protected static final int CHANNELPOOLSIZE = 20;
   protected FileFont[] fontFileCache = new FileFont[20];
   private int lastPoolIndex = 0;
   private int maxCompFont = 0;
   private CompositeFont[] compFonts = new CompositeFont[20];
   private ConcurrentHashMap<String, CompositeFont> compositeFonts = new ConcurrentHashMap();
   private ConcurrentHashMap<String, PhysicalFont> physicalFonts = new ConcurrentHashMap();
   private ConcurrentHashMap<String, PhysicalFont> registeredFonts = new ConcurrentHashMap();
   protected ConcurrentHashMap<String, Font2D> fullNameToFont = new ConcurrentHashMap();
   private HashMap<String, TrueTypeFont> localeFullNamesToFont;
   private PhysicalFont defaultPhysicalFont;
   static boolean longAddresses;
   private boolean loaded1dot0Fonts = false;
   boolean loadedAllFonts = false;
   boolean loadedAllFontFiles = false;
   HashMap<String, String> jreFontMap;
   HashSet<String> jreLucidaFontFiles;
   String[] jreOtherFontFiles;
   boolean noOtherJREFontFiles = false;
   public static final String lucidaFontName = "Lucida Sans Regular";
   public static String jreLibDirName;
   public static String jreFontDirName;
   private static HashSet<String> missingFontFiles = null;
   private String defaultFontName;
   private String defaultFontFileName;
   protected HashSet registeredFontFiles = new HashSet();
   private ArrayList badFonts;
   protected String fontPath;
   private FontConfiguration fontConfig;
   private boolean discoveredAllFonts = false;
   private static final FilenameFilter ttFilter = new SunFontManager.TTFilter();
   private static final FilenameFilter t1Filter = new SunFontManager.T1Filter();
   private Font[] allFonts;
   private String[] allFamilies;
   private Locale lastDefaultLocale;
   public static boolean noType1Font;
   private static String[] STR_ARRAY = new String[0];
   private boolean usePlatformFontMetrics = false;
   private final ConcurrentHashMap<String, SunFontManager.FontRegistrationInfo> deferredFontFiles = new ConcurrentHashMap();
   private final ConcurrentHashMap<String, Font2DHandle> initialisedFonts = new ConcurrentHashMap();
   private HashMap<String, String> fontToFileMap = null;
   private HashMap<String, String> fontToFamilyNameMap = null;
   private HashMap<String, ArrayList<String>> familyToFontListMap = null;
   private String[] pathDirs = null;
   private boolean haveCheckedUnreferencedFontFiles;
   static HashMap<String, SunFontManager.FamilyDescription> platformFontMap;
   private ConcurrentHashMap<String, Font2D> fontNameCache = new ConcurrentHashMap();
   protected Thread fileCloser = null;
   Vector<File> tmpFontFiles = null;
   private static final Object altJAFontKey;
   private static final Object localeFontKey;
   private static final Object proportionalFontKey;
   private boolean _usingPerAppContextComposites = false;
   private boolean _usingAlternateComposites = false;
   private static boolean gAltJAFont;
   private boolean gLocalePref = false;
   private boolean gPropPref = false;
   private static HashSet<String> installedNames;
   private static final Object regFamilyKey;
   private static final Object regFullNameKey;
   private Hashtable<String, FontFamily> createdByFamilyName;
   private Hashtable<String, Font2D> createdByFullName;
   private boolean fontsAreRegistered = false;
   private boolean fontsAreRegisteredPerAppContext = false;
   private static Locale systemLocale;

   public static SunFontManager getInstance() {
      FontManager var0 = FontManagerFactory.getInstance();
      return (SunFontManager)var0;
   }

   public FilenameFilter getTrueTypeFilter() {
      return ttFilter;
   }

   public FilenameFilter getType1Filter() {
      return t1Filter;
   }

   public boolean usingPerAppContextComposites() {
      return this._usingPerAppContextComposites;
   }

   private void initJREFontMap() {
      this.jreFontMap = new HashMap();
      this.jreLucidaFontFiles = new HashSet();
      if (!isOpenJDK()) {
         this.jreFontMap.put("lucida sans0", "LucidaSansRegular.ttf");
         this.jreFontMap.put("lucida sans1", "LucidaSansDemiBold.ttf");
         this.jreFontMap.put("lucida sans regular0", "LucidaSansRegular.ttf");
         this.jreFontMap.put("lucida sans regular1", "LucidaSansDemiBold.ttf");
         this.jreFontMap.put("lucida sans bold1", "LucidaSansDemiBold.ttf");
         this.jreFontMap.put("lucida sans demibold1", "LucidaSansDemiBold.ttf");
         this.jreFontMap.put("lucida sans typewriter0", "LucidaTypewriterRegular.ttf");
         this.jreFontMap.put("lucida sans typewriter1", "LucidaTypewriterBold.ttf");
         this.jreFontMap.put("lucida sans typewriter regular0", "LucidaTypewriter.ttf");
         this.jreFontMap.put("lucida sans typewriter regular1", "LucidaTypewriterBold.ttf");
         this.jreFontMap.put("lucida sans typewriter bold1", "LucidaTypewriterBold.ttf");
         this.jreFontMap.put("lucida sans typewriter demibold1", "LucidaTypewriterBold.ttf");
         this.jreFontMap.put("lucida bright0", "LucidaBrightRegular.ttf");
         this.jreFontMap.put("lucida bright1", "LucidaBrightDemiBold.ttf");
         this.jreFontMap.put("lucida bright2", "LucidaBrightItalic.ttf");
         this.jreFontMap.put("lucida bright3", "LucidaBrightDemiItalic.ttf");
         this.jreFontMap.put("lucida bright regular0", "LucidaBrightRegular.ttf");
         this.jreFontMap.put("lucida bright regular1", "LucidaBrightDemiBold.ttf");
         this.jreFontMap.put("lucida bright regular2", "LucidaBrightItalic.ttf");
         this.jreFontMap.put("lucida bright regular3", "LucidaBrightDemiItalic.ttf");
         this.jreFontMap.put("lucida bright bold1", "LucidaBrightDemiBold.ttf");
         this.jreFontMap.put("lucida bright bold3", "LucidaBrightDemiItalic.ttf");
         this.jreFontMap.put("lucida bright demibold1", "LucidaBrightDemiBold.ttf");
         this.jreFontMap.put("lucida bright demibold3", "LucidaBrightDemiItalic.ttf");
         this.jreFontMap.put("lucida bright italic2", "LucidaBrightItalic.ttf");
         this.jreFontMap.put("lucida bright italic3", "LucidaBrightDemiItalic.ttf");
         this.jreFontMap.put("lucida bright bold italic3", "LucidaBrightDemiItalic.ttf");
         this.jreFontMap.put("lucida bright demibold italic3", "LucidaBrightDemiItalic.ttf");
         Iterator var1 = this.jreFontMap.values().iterator();

         while(var1.hasNext()) {
            String var2 = (String)var1.next();
            this.jreLucidaFontFiles.add(var2);
         }

      }
   }

   public TrueTypeFont getEUDCFont() {
      return null;
   }

   private static native void initIDs();

   protected SunFontManager() {
      this.initJREFontMap();
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            File var1 = new File(SunFontManager.jreFontDirName + File.separator + "badfonts.txt");
            String var5;
            if (var1.exists()) {
               FileInputStream var2 = null;

               try {
                  SunFontManager.this.badFonts = new ArrayList();
                  var2 = new FileInputStream(var1);
                  InputStreamReader var3 = new InputStreamReader(var2);
                  BufferedReader var4 = new BufferedReader(var3);

                  while(true) {
                     var5 = var4.readLine();
                     if (var5 == null) {
                        break;
                     }

                     if (FontUtilities.debugFonts()) {
                        FontUtilities.getLogger().warning("read bad font: " + var5);
                     }

                     SunFontManager.this.badFonts.add(var5);
                  }
               } catch (IOException var8) {
                  try {
                     if (var2 != null) {
                        var2.close();
                     }
                  } catch (IOException var7) {
                  }
               }
            }

            if (FontUtilities.isLinux) {
               SunFontManager.this.registerFontDir(SunFontManager.jreFontDirName);
            }

            SunFontManager.this.registerFontsInDir(SunFontManager.jreFontDirName, true, 2, true, false);
            SunFontManager.this.fontConfig = SunFontManager.this.createFontConfiguration();
            if (SunFontManager.isOpenJDK()) {
               String[] var9 = SunFontManager.this.getDefaultPlatformFont();
               SunFontManager.this.defaultFontName = var9[0];
               SunFontManager.this.defaultFontFileName = var9[1];
            }

            String var10 = SunFontManager.this.fontConfig.getExtraFontPath();
            boolean var11 = false;
            boolean var12 = false;
            var5 = System.getProperty("sun.java2d.fontpath");
            if (var5 != null) {
               if (var5.startsWith("prepend:")) {
                  var11 = true;
                  var5 = var5.substring("prepend:".length());
               } else if (var5.startsWith("append:")) {
                  var12 = true;
                  var5 = var5.substring("append:".length());
               }
            }

            if (FontUtilities.debugFonts()) {
               PlatformLogger var6 = FontUtilities.getLogger();
               var6.info("JRE font directory: " + SunFontManager.jreFontDirName);
               var6.info("Extra font path: " + var10);
               var6.info("Debug font path: " + var5);
            }

            if (var5 != null) {
               SunFontManager.this.fontPath = SunFontManager.this.getPlatformFontPath(SunFontManager.noType1Font);
               if (var10 != null) {
                  SunFontManager.this.fontPath = var10 + File.pathSeparator + SunFontManager.this.fontPath;
               }

               if (var12) {
                  SunFontManager.this.fontPath = SunFontManager.this.fontPath + File.pathSeparator + var5;
               } else if (var11) {
                  SunFontManager.this.fontPath = var5 + File.pathSeparator + SunFontManager.this.fontPath;
               } else {
                  SunFontManager.this.fontPath = var5;
               }

               SunFontManager.this.registerFontDirs(SunFontManager.this.fontPath);
            } else if (var10 != null) {
               SunFontManager.this.registerFontDirs(var10);
            }

            if (FontUtilities.isSolaris && Locale.JAPAN.equals(Locale.getDefault())) {
               SunFontManager.this.registerFontDir("/usr/openwin/lib/locale/ja/X11/fonts/TT");
            }

            SunFontManager.this.initCompositeFonts(SunFontManager.this.fontConfig, (ConcurrentHashMap)null);
            return null;
         }
      });
      boolean var1 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            String var1 = System.getProperty("java2d.font.usePlatformFont");
            String var2 = System.getenv("JAVA2D_USEPLATFORMFONT");
            return "true".equals(var1) || var2 != null;
         }
      });
      if (var1) {
         this.usePlatformFontMetrics = true;
         System.out.println("Enabling platform font metrics for win32. This is an unsupported option.");
         System.out.println("This yields incorrect composite font metrics as reported by 1.1.x releases.");
         System.out.println("It is appropriate only for use by applications which do not use any Java 2");
         System.out.println("functionality. This property will be removed in a later release.");
      }

   }

   public Font2DHandle getNewComposite(String var1, int var2, Font2DHandle var3) {
      if (!(var3.font2D instanceof CompositeFont)) {
         return var3;
      } else {
         CompositeFont var4 = (CompositeFont)var3.font2D;
         PhysicalFont var5 = var4.getSlotFont(0);
         if (var1 == null) {
            var1 = var5.getFamilyName((Locale)null);
         }

         if (var2 == -1) {
            var2 = var4.getStyle();
         }

         Object var6 = this.findFont2D(var1, var2, 0);
         if (!(var6 instanceof PhysicalFont)) {
            var6 = var5;
         }

         PhysicalFont var7 = (PhysicalFont)var6;
         CompositeFont var8 = (CompositeFont)this.findFont2D("dialog", var2, 0);
         if (var8 == null) {
            return var3;
         } else {
            CompositeFont var9 = new CompositeFont(var7, var8);
            Font2DHandle var10 = new Font2DHandle(var9);
            return var10;
         }
      }
   }

   protected void registerCompositeFont(String var1, String[] var2, String[] var3, int var4, int[] var5, int[] var6, boolean var7) {
      CompositeFont var8 = new CompositeFont(var1, var2, var3, var4, var5, var6, var7, this);
      this.addCompositeToFontList(var8, 2);
      synchronized(this.compFonts) {
         this.compFonts[this.maxCompFont++] = var8;
      }
   }

   protected static void registerCompositeFont(String var0, String[] var1, String[] var2, int var3, int[] var4, int[] var5, boolean var6, ConcurrentHashMap<String, Font2D> var7) {
      CompositeFont var8 = new CompositeFont(var0, var1, var2, var3, var4, var5, var6, getInstance());
      Font2D var9 = (Font2D)var7.get(var0.toLowerCase(Locale.ENGLISH));
      if (var9 instanceof CompositeFont) {
         var9.handle.font2D = var8;
      }

      var7.put(var0.toLowerCase(Locale.ENGLISH), var8);
   }

   private void addCompositeToFontList(CompositeFont var1, int var2) {
      if (FontUtilities.isLogging()) {
         FontUtilities.getLogger().info("Add to Family " + var1.familyName + ", Font " + var1.fullName + " rank=" + var2);
      }

      var1.setRank(var2);
      this.compositeFonts.put(var1.fullName, var1);
      this.fullNameToFont.put(var1.fullName.toLowerCase(Locale.ENGLISH), var1);
      FontFamily var3 = FontFamily.getFamily(var1.familyName);
      if (var3 == null) {
         var3 = new FontFamily(var1.familyName, true, var2);
      }

      var3.setFont(var1, var1.style);
   }

   protected PhysicalFont addToFontList(PhysicalFont var1, int var2) {
      String var3 = var1.fullName;
      String var4 = var1.familyName;
      if (var3 != null && !"".equals(var3)) {
         if (this.compositeFonts.containsKey(var3)) {
            return null;
         } else {
            var1.setRank(var2);
            if (!this.physicalFonts.containsKey(var3)) {
               if (FontUtilities.isLogging()) {
                  FontUtilities.getLogger().info("Add to Family " + var4 + ", Font " + var3 + " rank=" + var2);
               }

               this.physicalFonts.put(var3, var1);
               FontFamily var5 = FontFamily.getFamily(var4);
               if (var5 == null) {
                  var5 = new FontFamily(var4, false, var2);
                  var5.setFont(var1, var1.style);
               } else {
                  var5.setFont(var1, var1.style);
               }

               this.fullNameToFont.put(var3.toLowerCase(Locale.ENGLISH), var1);
               return var1;
            } else {
               PhysicalFont var6 = (PhysicalFont)this.physicalFonts.get(var3);
               if (var6 == null) {
                  return null;
               } else if (var6.getRank() >= var2) {
                  if (var6.mapper != null && var2 > 2) {
                     return var6;
                  } else {
                     if (var6.getRank() == var2) {
                        if (!(var6 instanceof TrueTypeFont) || !(var1 instanceof TrueTypeFont)) {
                           return var6;
                        }

                        TrueTypeFont var7 = (TrueTypeFont)var6;
                        TrueTypeFont var8 = (TrueTypeFont)var1;
                        if (var7.fileSize >= var8.fileSize) {
                           return var6;
                        }
                     }

                     if (var6.platName.startsWith(jreFontDirName)) {
                        if (FontUtilities.isLogging()) {
                           FontUtilities.getLogger().warning("Unexpected attempt to replace a JRE  font " + var3 + " from " + var6.platName + " with " + var1.platName);
                        }

                        return var6;
                     } else {
                        if (FontUtilities.isLogging()) {
                           FontUtilities.getLogger().info("Replace in Family " + var4 + ",Font " + var3 + " new rank=" + var2 + " from " + var6.platName + " with " + var1.platName);
                        }

                        this.replaceFont(var6, var1);
                        this.physicalFonts.put(var3, var1);
                        this.fullNameToFont.put(var3.toLowerCase(Locale.ENGLISH), var1);
                        FontFamily var9 = FontFamily.getFamily(var4);
                        if (var9 == null) {
                           var9 = new FontFamily(var4, false, var2);
                           var9.setFont(var1, var1.style);
                        } else {
                           var9.setFont(var1, var1.style);
                        }

                        return var1;
                     }
                  }
               } else {
                  return var6;
               }
            }
         }
      } else {
         return null;
      }
   }

   public Font2D[] getRegisteredFonts() {
      PhysicalFont[] var1 = this.getPhysicalFonts();
      int var2 = this.maxCompFont;
      Font2D[] var3 = new Font2D[var1.length + var2];
      System.arraycopy(this.compFonts, 0, var3, 0, var2);
      System.arraycopy(var1, 0, var3, var2, var1.length);
      return var3;
   }

   protected PhysicalFont[] getPhysicalFonts() {
      return (PhysicalFont[])this.physicalFonts.values().toArray(new PhysicalFont[0]);
   }

   protected synchronized void initialiseDeferredFonts() {
      Iterator var1 = this.deferredFontFiles.keySet().iterator();

      while(var1.hasNext()) {
         String var2 = (String)var1.next();
         this.initialiseDeferredFont(var2);
      }

   }

   protected synchronized void registerDeferredJREFonts(String var1) {
      Iterator var2 = this.deferredFontFiles.values().iterator();

      while(var2.hasNext()) {
         SunFontManager.FontRegistrationInfo var3 = (SunFontManager.FontRegistrationInfo)var2.next();
         if (var3.fontFilePath != null && var3.fontFilePath.startsWith(var1)) {
            this.initialiseDeferredFont(var3.fontFilePath);
         }
      }

   }

   public boolean isDeferredFont(String var1) {
      return this.deferredFontFiles.containsKey(var1);
   }

   public PhysicalFont findJREDeferredFont(String var1, int var2) {
      String var4 = var1.toLowerCase(Locale.ENGLISH) + var2;
      String var5 = (String)this.jreFontMap.get(var4);
      PhysicalFont var3;
      if (var5 != null) {
         var5 = jreFontDirName + File.separator + var5;
         if (this.deferredFontFiles.get(var5) != null) {
            var3 = this.initialiseDeferredFont(var5);
            if (var3 != null && (var3.getFontName((Locale)null).equalsIgnoreCase(var1) || var3.getFamilyName((Locale)null).equalsIgnoreCase(var1)) && var3.style == var2) {
               return var3;
            }
         }
      }

      if (this.noOtherJREFontFiles) {
         return null;
      } else {
         synchronized(this.jreLucidaFontFiles) {
            if (this.jreOtherFontFiles == null) {
               HashSet var7 = new HashSet();
               Iterator var8 = this.deferredFontFiles.keySet().iterator();

               while(var8.hasNext()) {
                  String var9 = (String)var8.next();
                  File var10 = new File(var9);
                  String var11 = var10.getParent();
                  String var12 = var10.getName();
                  if (var11 != null && var11.equals(jreFontDirName) && !this.jreLucidaFontFiles.contains(var12)) {
                     var7.add(var9);
                  }
               }

               this.jreOtherFontFiles = (String[])var7.toArray(STR_ARRAY);
               if (this.jreOtherFontFiles.length == 0) {
                  this.noOtherJREFontFiles = true;
               }
            }

            for(int var15 = 0; var15 < this.jreOtherFontFiles.length; ++var15) {
               var5 = this.jreOtherFontFiles[var15];
               if (var5 != null) {
                  this.jreOtherFontFiles[var15] = null;
                  var3 = this.initialiseDeferredFont(var5);
                  if (var3 != null && (var3.getFontName((Locale)null).equalsIgnoreCase(var1) || var3.getFamilyName((Locale)null).equalsIgnoreCase(var1)) && var3.style == var2) {
                     return var3;
                  }
               }
            }

            return null;
         }
      }
   }

   private PhysicalFont findOtherDeferredFont(String var1, int var2) {
      Iterator var3 = this.deferredFontFiles.keySet().iterator();

      PhysicalFont var8;
      do {
         do {
            do {
               String var4;
               String var6;
               String var7;
               do {
                  if (!var3.hasNext()) {
                     return null;
                  }

                  var4 = (String)var3.next();
                  File var5 = new File(var4);
                  var6 = var5.getParent();
                  var7 = var5.getName();
               } while(var6 != null && var6.equals(jreFontDirName) && this.jreLucidaFontFiles.contains(var7));

               var8 = this.initialiseDeferredFont(var4);
            } while(var8 == null);
         } while(!var8.getFontName((Locale)null).equalsIgnoreCase(var1) && !var8.getFamilyName((Locale)null).equalsIgnoreCase(var1));
      } while(var8.style != var2);

      return var8;
   }

   private PhysicalFont findDeferredFont(String var1, int var2) {
      PhysicalFont var3 = this.findJREDeferredFont(var1, var2);
      return var3 != null ? var3 : this.findOtherDeferredFont(var1, var2);
   }

   public void registerDeferredFont(String var1, String var2, String[] var3, int var4, boolean var5, int var6) {
      SunFontManager.FontRegistrationInfo var7 = new SunFontManager.FontRegistrationInfo(var2, var3, var4, var5, var6);
      this.deferredFontFiles.put(var1, var7);
   }

   public synchronized PhysicalFont initialiseDeferredFont(String var1) {
      if (var1 == null) {
         return null;
      } else {
         if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().info("Opening deferred font file " + var1);
         }

         SunFontManager.FontRegistrationInfo var3 = (SunFontManager.FontRegistrationInfo)this.deferredFontFiles.get(var1);
         PhysicalFont var2;
         if (var3 != null) {
            this.deferredFontFiles.remove(var1);
            var2 = this.registerFontFile(var3.fontFilePath, var3.nativeNames, var3.fontFormat, var3.javaRasterizer, var3.fontRank);
            if (var2 != null) {
               this.initialisedFonts.put(var1, var2.handle);
            } else {
               this.initialisedFonts.put(var1, this.getDefaultPhysicalFont().handle);
            }
         } else {
            Font2DHandle var4 = (Font2DHandle)this.initialisedFonts.get(var1);
            if (var4 == null) {
               var2 = this.getDefaultPhysicalFont();
            } else {
               var2 = (PhysicalFont)((PhysicalFont)var4.font2D);
            }
         }

         return var2;
      }
   }

   public boolean isRegisteredFontFile(String var1) {
      return this.registeredFonts.containsKey(var1);
   }

   public PhysicalFont getRegisteredFontFile(String var1) {
      return (PhysicalFont)this.registeredFonts.get(var1);
   }

   public PhysicalFont registerFontFile(String var1, String[] var2, int var3, boolean var4, int var5) {
      PhysicalFont var6 = (PhysicalFont)this.registeredFonts.get(var1);
      if (var6 != null) {
         return var6;
      } else {
         PhysicalFont var7 = null;

         try {
            label39:
            switch(var3) {
            case 0:
               int var9 = 0;

               while(true) {
                  TrueTypeFont var10 = new TrueTypeFont(var1, var2, var9++, var4);
                  PhysicalFont var14 = this.addToFontList(var10, var5);
                  if (var7 == null) {
                     var7 = var14;
                  }

                  if (var9 >= var10.getFontCount()) {
                     break label39;
                  }
               }
            case 1:
               Type1Font var11 = new Type1Font(var1, var2);
               var7 = this.addToFontList(var11, var5);
               break;
            case 5:
               NativeFont var12 = new NativeFont(var1, false);
               var7 = this.addToFontList(var12, var5);
            }

            if (FontUtilities.isLogging()) {
               FontUtilities.getLogger().info("Registered file " + var1 + " as font " + var7 + " rank=" + var5);
            }
         } catch (FontFormatException var13) {
            if (FontUtilities.isLogging()) {
               FontUtilities.getLogger().warning("Unusable font: " + var1 + " " + var13.toString());
            }
         }

         if (var7 != null && var3 != 5) {
            this.registeredFonts.put(var1, var7);
         }

         return var7;
      }
   }

   public void registerFonts(String[] var1, String[][] var2, int var3, int var4, boolean var5, int var6, boolean var7) {
      for(int var8 = 0; var8 < var3; ++var8) {
         if (var7) {
            this.registerDeferredFont(var1[var8], var1[var8], var2[var8], var4, var5, var6);
         } else {
            this.registerFontFile(var1[var8], var2[var8], var4, var5, var6);
         }
      }

   }

   public PhysicalFont getDefaultPhysicalFont() {
      if (this.defaultPhysicalFont == null) {
         this.defaultPhysicalFont = (PhysicalFont)this.findFont2D("Lucida Sans Regular", 0, 0);
         if (this.defaultPhysicalFont == null) {
            this.defaultPhysicalFont = (PhysicalFont)this.findFont2D("Arial", 0, 0);
         }

         if (this.defaultPhysicalFont == null) {
            Iterator var1 = this.physicalFonts.values().iterator();
            if (!var1.hasNext()) {
               throw new Error("Probable fatal error:No fonts found.");
            }

            this.defaultPhysicalFont = (PhysicalFont)var1.next();
         }
      }

      return this.defaultPhysicalFont;
   }

   public Font2D getDefaultLogicalFont(int var1) {
      return this.findFont2D("dialog", var1, 0);
   }

   private static String dotStyleStr(int var0) {
      switch(var0) {
      case 1:
         return ".bold";
      case 2:
         return ".italic";
      case 3:
         return ".bolditalic";
      default:
         return ".plain";
      }
   }

   protected void populateFontFileNameMap(HashMap<String, String> var1, HashMap<String, String> var2, HashMap<String, ArrayList<String>> var3, Locale var4) {
   }

   private String[] getFontFilesFromPath(boolean var1) {
      final Object var2;
      if (var1) {
         var2 = ttFilter;
      } else {
         var2 = new SunFontManager.TTorT1Filter();
      }

      return (String[])((String[])AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            if (SunFontManager.this.pathDirs.length == 1) {
               File var6 = new File(SunFontManager.this.pathDirs[0]);
               String[] var7 = var6.list((FilenameFilter)var2);
               if (var7 == null) {
                  return new String[0];
               } else {
                  for(int var8 = 0; var8 < var7.length; ++var8) {
                     var7[var8] = var7[var8].toLowerCase();
                  }

                  return var7;
               }
            } else {
               ArrayList var1 = new ArrayList();

               for(int var2x = 0; var2x < SunFontManager.this.pathDirs.length; ++var2x) {
                  File var3 = new File(SunFontManager.this.pathDirs[var2x]);
                  String[] var4 = var3.list((FilenameFilter)var2);
                  if (var4 != null) {
                     for(int var5 = 0; var5 < var4.length; ++var5) {
                        var1.add(var4[var5].toLowerCase());
                     }
                  }
               }

               return var1.toArray(SunFontManager.STR_ARRAY);
            }
         }
      }));
   }

   private void resolveWindowsFonts() {
      ArrayList var1 = null;
      Iterator var2 = this.fontToFamilyNameMap.keySet().iterator();

      String var5;
      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         String var4 = (String)this.fontToFileMap.get(var3);
         if (var4 == null) {
            if (var3.indexOf("  ") > 0) {
               var5 = var3.replaceFirst("  ", " ");
               var4 = (String)this.fontToFileMap.get(var5);
               if (var4 != null && !this.fontToFamilyNameMap.containsKey(var5)) {
                  this.fontToFileMap.remove(var5);
                  this.fontToFileMap.put(var3, var4);
               }
            } else if (var3.equals("marlett")) {
               this.fontToFileMap.put(var3, "marlett.ttf");
            } else if (var3.equals("david")) {
               var4 = (String)this.fontToFileMap.get("david regular");
               if (var4 != null) {
                  this.fontToFileMap.remove("david regular");
                  this.fontToFileMap.put("david", var4);
               }
            } else {
               if (var1 == null) {
                  var1 = new ArrayList();
               }

               var1.add(var3);
            }
         }
      }

      if (var1 != null) {
         HashSet var9 = new HashSet();
         HashMap var10 = (HashMap)((HashMap)this.fontToFileMap.clone());
         Iterator var11 = this.fontToFamilyNameMap.keySet().iterator();

         while(var11.hasNext()) {
            var5 = (String)var11.next();
            var10.remove(var5);
         }

         var11 = var10.keySet().iterator();

         while(var11.hasNext()) {
            var5 = (String)var11.next();
            var9.add(var10.get(var5));
            this.fontToFileMap.remove(var5);
         }

         this.resolveFontFiles(var9, var1);
         String var6;
         if (var1.size() > 0) {
            ArrayList var12 = new ArrayList();
            Iterator var13 = this.fontToFileMap.values().iterator();

            while(var13.hasNext()) {
               var6 = (String)var13.next();
               var12.add(var6.toLowerCase());
            }

            String[] var15 = this.getFontFilesFromPath(true);
            int var16 = var15.length;

            for(int var7 = 0; var7 < var16; ++var7) {
               String var8 = var15[var7];
               if (!var12.contains(var8)) {
                  var9.add(var8);
               }
            }

            this.resolveFontFiles(var9, var1);
         }

         if (var1.size() > 0) {
            int var14 = var1.size();

            for(int var17 = 0; var17 < var14; ++var17) {
               var6 = (String)var1.get(var17);
               String var18 = (String)this.fontToFamilyNameMap.get(var6);
               if (var18 != null) {
                  ArrayList var19 = (ArrayList)this.familyToFontListMap.get(var18);
                  if (var19 != null && var19.size() <= 1) {
                     this.familyToFontListMap.remove(var18);
                  }
               }

               this.fontToFamilyNameMap.remove(var6);
               if (FontUtilities.isLogging()) {
                  FontUtilities.getLogger().info("No file for font:" + var6);
               }
            }
         }
      }

   }

   private synchronized void checkForUnreferencedFontFiles() {
      if (!this.haveCheckedUnreferencedFontFiles) {
         this.haveCheckedUnreferencedFontFiles = true;
         if (FontUtilities.isWindows) {
            ArrayList var1 = new ArrayList();
            Iterator var2 = this.fontToFileMap.values().iterator();

            while(var2.hasNext()) {
               String var3 = (String)var2.next();
               var1.add(var3.toLowerCase());
            }

            HashMap var14 = null;
            HashMap var15 = null;
            HashMap var4 = null;
            String[] var5 = this.getFontFilesFromPath(false);
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               String var8 = var5[var7];
               if (!var1.contains(var8)) {
                  if (FontUtilities.isLogging()) {
                     FontUtilities.getLogger().info("Found non-registry file : " + var8);
                  }

                  PhysicalFont var9 = this.registerFontFile(this.getPathName(var8));
                  if (var9 != null) {
                     if (var14 == null) {
                        var14 = new HashMap(this.fontToFileMap);
                        var15 = new HashMap(this.fontToFamilyNameMap);
                        var4 = new HashMap(this.familyToFontListMap);
                     }

                     String var10 = var9.getFontName((Locale)null);
                     String var11 = var9.getFamilyName((Locale)null);
                     String var12 = var11.toLowerCase();
                     var15.put(var10, var11);
                     var14.put(var10, var8);
                     ArrayList var13 = (ArrayList)var4.get(var12);
                     if (var13 == null) {
                        var13 = new ArrayList();
                     } else {
                        var13 = new ArrayList(var13);
                     }

                     var13.add(var10);
                     var4.put(var12, var13);
                  }
               }
            }

            if (var14 != null) {
               this.fontToFileMap = var14;
               this.familyToFontListMap = var4;
               this.fontToFamilyNameMap = var15;
            }

         }
      }
   }

   private void resolveFontFiles(HashSet<String> var1, ArrayList<String> var2) {
      Locale var3 = SunToolkit.getStartupLocale();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();

         try {
            int var6 = 0;
            String var8 = this.getPathName(var5);
            if (FontUtilities.isLogging()) {
               FontUtilities.getLogger().info("Trying to resolve file " + var8);
            }

            while(true) {
               TrueTypeFont var7 = new TrueTypeFont(var8, (Object)null, var6++, false);
               String var9 = var7.getFontName(var3).toLowerCase();
               if (var2.contains(var9)) {
                  this.fontToFileMap.put(var9, var5);
                  var2.remove(var9);
                  if (FontUtilities.isLogging()) {
                     FontUtilities.getLogger().info("Resolved absent registry entry for " + var9 + " located in " + var8);
                  }
               }

               if (var6 >= var7.getFontCount()) {
                  break;
               }
            }
         } catch (Exception var10) {
         }
      }

   }

   public HashMap<String, SunFontManager.FamilyDescription> populateHardcodedFileNameMap() {
      return new HashMap(0);
   }

   Font2D findFontFromPlatformMap(String var1, int var2) {
      if (platformFontMap == null) {
         platformFontMap = this.populateHardcodedFileNameMap();
      }

      if (platformFontMap != null && platformFontMap.size() != 0) {
         int var3 = var1.indexOf(32);
         String var4 = var1;
         if (var3 > 0) {
            var4 = var1.substring(0, var3);
         }

         SunFontManager.FamilyDescription var5 = (SunFontManager.FamilyDescription)platformFontMap.get(var4);
         if (var5 == null) {
            return null;
         } else {
            byte var6 = -1;
            if (var1.equalsIgnoreCase(var5.plainFullName)) {
               var6 = 0;
            } else if (var1.equalsIgnoreCase(var5.boldFullName)) {
               var6 = 1;
            } else if (var1.equalsIgnoreCase(var5.italicFullName)) {
               var6 = 2;
            } else if (var1.equalsIgnoreCase(var5.boldItalicFullName)) {
               var6 = 3;
            }

            if (var6 == -1 && !var1.equalsIgnoreCase(var5.familyName)) {
               return null;
            } else {
               String var7 = null;
               String var8 = null;
               String var9 = null;
               String var10 = null;
               boolean var11 = false;
               this.getPlatformFontDirs(noType1Font);
               if (var5.plainFileName != null) {
                  var7 = this.getPathName(var5.plainFileName);
                  if (var7 == null) {
                     var11 = true;
                  }
               }

               if (var5.boldFileName != null) {
                  var8 = this.getPathName(var5.boldFileName);
                  if (var8 == null) {
                     var11 = true;
                  }
               }

               if (var5.italicFileName != null) {
                  var9 = this.getPathName(var5.italicFileName);
                  if (var9 == null) {
                     var11 = true;
                  }
               }

               if (var5.boldItalicFileName != null) {
                  var10 = this.getPathName(var5.boldItalicFileName);
                  if (var10 == null) {
                     var11 = true;
                  }
               }

               if (var11) {
                  if (FontUtilities.isLogging()) {
                     FontUtilities.getLogger().info("Hardcoded file missing looking for " + var1);
                  }

                  platformFontMap.remove(var4);
                  return null;
               } else {
                  final String[] var12 = new String[]{var7, var8, var9, var10};
                  var11 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                     public Boolean run() {
                        for(int var1 = 0; var1 < var12.length; ++var1) {
                           if (var12[var1] != null) {
                              File var2 = new File(var12[var1]);
                              if (!var2.exists()) {
                                 return Boolean.TRUE;
                              }
                           }
                        }

                        return Boolean.FALSE;
                     }
                  });
                  if (var11) {
                     if (FontUtilities.isLogging()) {
                        FontUtilities.getLogger().info("Hardcoded file missing looking for " + var1);
                     }

                     platformFontMap.remove(var4);
                     return null;
                  } else {
                     Object var13 = null;

                     for(int var14 = 0; var14 < var12.length; ++var14) {
                        if (var12[var14] != null) {
                           PhysicalFont var15 = this.registerFontFile(var12[var14], (String[])null, 0, false, 3);
                           if (var14 == var6) {
                              var13 = var15;
                           }
                        }
                     }

                     FontFamily var16 = FontFamily.getFamily(var5.familyName);
                     if (var16 != null) {
                        if (var13 == null) {
                           var13 = var16.getFont(var2);
                           if (var13 == null) {
                              var13 = var16.getClosestStyle(var2);
                           }
                        } else if (var2 > 0 && var2 != ((Font2D)var13).style) {
                           var2 |= ((Font2D)var13).style;
                           var13 = var16.getFont(var2);
                           if (var13 == null) {
                              var13 = var16.getClosestStyle(var2);
                           }
                        }
                     }

                     return (Font2D)var13;
                  }
               }
            }
         }
      } else {
         return null;
      }
   }

   private synchronized HashMap<String, String> getFullNameToFileMap() {
      if (this.fontToFileMap == null) {
         this.pathDirs = this.getPlatformFontDirs(noType1Font);
         this.fontToFileMap = new HashMap(100);
         this.fontToFamilyNameMap = new HashMap(100);
         this.familyToFontListMap = new HashMap(50);
         this.populateFontFileNameMap(this.fontToFileMap, this.fontToFamilyNameMap, this.familyToFontListMap, Locale.ENGLISH);
         if (FontUtilities.isWindows) {
            this.resolveWindowsFonts();
         }

         if (FontUtilities.isLogging()) {
            this.logPlatformFontInfo();
         }
      }

      return this.fontToFileMap;
   }

   private void logPlatformFontInfo() {
      PlatformLogger var1 = FontUtilities.getLogger();

      for(int var2 = 0; var2 < this.pathDirs.length; ++var2) {
         var1.info("fontdir=" + this.pathDirs[var2]);
      }

      Iterator var4 = this.fontToFileMap.keySet().iterator();

      String var3;
      while(var4.hasNext()) {
         var3 = (String)var4.next();
         var1.info("font=" + var3 + " file=" + (String)this.fontToFileMap.get(var3));
      }

      var4 = this.fontToFamilyNameMap.keySet().iterator();

      while(var4.hasNext()) {
         var3 = (String)var4.next();
         var1.info("font=" + var3 + " family=" + (String)this.fontToFamilyNameMap.get(var3));
      }

      var4 = this.familyToFontListMap.keySet().iterator();

      while(var4.hasNext()) {
         var3 = (String)var4.next();
         var1.info("family=" + var3 + " fonts=" + this.familyToFontListMap.get(var3));
      }

   }

   protected String[] getFontNamesFromPlatform() {
      if (this.getFullNameToFileMap().size() == 0) {
         return null;
      } else {
         this.checkForUnreferencedFontFiles();
         ArrayList var1 = new ArrayList();
         Iterator var2 = this.familyToFontListMap.values().iterator();

         while(var2.hasNext()) {
            ArrayList var3 = (ArrayList)var2.next();
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               String var5 = (String)var4.next();
               var1.add(var5);
            }
         }

         return (String[])var1.toArray(STR_ARRAY);
      }
   }

   public boolean gotFontsFromPlatform() {
      return this.getFullNameToFileMap().size() != 0;
   }

   public String getFileNameForFontName(String var1) {
      String var2 = var1.toLowerCase(Locale.ENGLISH);
      return (String)this.fontToFileMap.get(var2);
   }

   private PhysicalFont registerFontFile(String var1) {
      if ((new File(var1)).isAbsolute() && !this.registeredFonts.contains(var1)) {
         byte var2 = -1;
         byte var3 = 6;
         if (ttFilter.accept((File)null, var1)) {
            var2 = 0;
            var3 = 3;
         } else if (t1Filter.accept((File)null, var1)) {
            var2 = 1;
            var3 = 4;
         }

         return var2 == -1 ? null : this.registerFontFile(var1, (String[])null, var2, false, var3);
      } else {
         return null;
      }
   }

   protected void registerOtherFontFiles(HashSet var1) {
      if (this.getFullNameToFileMap().size() != 0) {
         Iterator var2 = this.fontToFileMap.values().iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            this.registerFontFile(var3);
         }

      }
   }

   public boolean getFamilyNamesFromPlatform(TreeMap<String, String> var1, Locale var2) {
      if (this.getFullNameToFileMap().size() == 0) {
         return false;
      } else {
         this.checkForUnreferencedFontFiles();
         Iterator var3 = this.fontToFamilyNameMap.values().iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            var1.put(var4.toLowerCase(var2), var4);
         }

         return true;
      }
   }

   private String getPathName(final String var1) {
      File var2 = new File(var1);
      if (var2.isAbsolute()) {
         return var1;
      } else if (this.pathDirs.length == 1) {
         return this.pathDirs[0] + File.separator + var1;
      } else {
         String var3 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               for(int var1x = 0; var1x < SunFontManager.this.pathDirs.length; ++var1x) {
                  File var2 = new File(SunFontManager.this.pathDirs[var1x] + File.separator + var1);
                  if (var2.exists()) {
                     return var2.getAbsolutePath();
                  }
               }

               return null;
            }
         });
         return var3 != null ? var3 : var1;
      }
   }

   private Font2D findFontFromPlatform(String var1, int var2) {
      if (this.getFullNameToFileMap().size() == 0) {
         return null;
      } else {
         ArrayList var3 = null;
         String var4 = null;
         String var5 = (String)this.fontToFamilyNameMap.get(var1);
         if (var5 != null) {
            var4 = (String)this.fontToFileMap.get(var1);
            var3 = (ArrayList)this.familyToFontListMap.get(var5.toLowerCase(Locale.ENGLISH));
         } else {
            var3 = (ArrayList)this.familyToFontListMap.get(var1);
            if (var3 != null && var3.size() > 0) {
               String var6 = ((String)var3.get(0)).toLowerCase(Locale.ENGLISH);
               if (var6 != null) {
                  var5 = (String)this.fontToFamilyNameMap.get(var6);
               }
            }
         }

         if (var3 != null && var5 != null) {
            String[] var11 = (String[])((String[])var3.toArray(STR_ARRAY));
            if (var11.length == 0) {
               return null;
            } else {
               String var9;
               for(int var7 = 0; var7 < var11.length; ++var7) {
                  String var8 = var11[var7].toLowerCase(Locale.ENGLISH);
                  var9 = (String)this.fontToFileMap.get(var8);
                  if (var9 == null) {
                     if (FontUtilities.isLogging()) {
                        FontUtilities.getLogger().info("Platform lookup : No file for font " + var11[var7] + " in family " + var5);
                     }

                     return null;
                  }
               }

               PhysicalFont var12 = null;
               if (var4 != null) {
                  var12 = this.registerFontFile(this.getPathName(var4), (String[])null, 0, false, 3);
               }

               for(int var13 = 0; var13 < var11.length; ++var13) {
                  var9 = var11[var13].toLowerCase(Locale.ENGLISH);
                  String var10 = (String)this.fontToFileMap.get(var9);
                  if (var4 == null || !var4.equals(var10)) {
                     this.registerFontFile(this.getPathName(var10), (String[])null, 0, false, 3);
                  }
               }

               Font2D var14 = null;
               FontFamily var15 = FontFamily.getFamily(var5);
               if (var12 != null) {
                  var2 |= var12.style;
               }

               if (var15 != null) {
                  var14 = var15.getFont(var2);
                  if (var14 == null) {
                     var14 = var15.getClosestStyle(var2);
                  }
               }

               return var14;
            }
         } else {
            return null;
         }
      }
   }

   public Font2D findFont2D(String var1, int var2, int var3) {
      String var4 = var1.toLowerCase(Locale.ENGLISH);
      String var5 = var4 + dotStyleStr(var2);
      Font2D var6;
      if (this._usingPerAppContextComposites) {
         ConcurrentHashMap var7 = (ConcurrentHashMap)AppContext.getAppContext().get(CompositeFont.class);
         if (var7 != null) {
            var6 = (Font2D)var7.get(var5);
         } else {
            var6 = null;
         }
      } else {
         var6 = (Font2D)this.fontNameCache.get(var5);
      }

      if (var6 != null) {
         return var6;
      } else {
         if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().info("Search for font: " + var1);
         }

         if (FontUtilities.isWindows) {
            if (var4.equals("ms sans serif")) {
               var1 = "sansserif";
            } else if (var4.equals("ms serif")) {
               var1 = "serif";
            }
         }

         if (var4.equals("default")) {
            var1 = "dialog";
         }

         FontFamily var11 = FontFamily.getFamily(var1);
         if (var11 != null) {
            Object var12 = var11.getFontWithExactStyleMatch(var2);
            if (var12 == null) {
               var12 = this.findDeferredFont(var1, var2);
            }

            if (var12 == null) {
               var12 = var11.getFont(var2);
            }

            if (var12 == null) {
               var12 = var11.getClosestStyle(var2);
            }

            if (var12 != null) {
               this.fontNameCache.put(var5, var12);
               return (Font2D)var12;
            }
         }

         var6 = (Font2D)this.fullNameToFont.get(var4);
         Font2D var8;
         if (var6 != null) {
            if (var6.style == var2 || var2 == 0) {
               this.fontNameCache.put(var5, var6);
               return var6;
            }

            var11 = FontFamily.getFamily(var6.getFamilyName((Locale)null));
            if (var11 != null) {
               var8 = var11.getFont(var2 | var6.style);
               if (var8 != null) {
                  this.fontNameCache.put(var5, var8);
                  return var8;
               }

               var8 = var11.getClosestStyle(var2 | var6.style);
               if (var8 != null && var8.canDoStyle(var2 | var6.style)) {
                  this.fontNameCache.put(var5, var8);
                  return var8;
               }
            }
         }

         PhysicalFont var15;
         if (FontUtilities.isWindows) {
            var6 = this.findFontFromPlatformMap(var4, var2);
            if (FontUtilities.isLogging()) {
               FontUtilities.getLogger().info("findFontFromPlatformMap returned " + var6);
            }

            if (var6 != null) {
               this.fontNameCache.put(var5, var6);
               return var6;
            }

            if (this.deferredFontFiles.size() > 0) {
               var15 = this.findJREDeferredFont(var4, var2);
               if (var15 != null) {
                  this.fontNameCache.put(var5, var15);
                  return var15;
               }
            }

            var6 = this.findFontFromPlatform(var4, var2);
            if (var6 != null) {
               if (FontUtilities.isLogging()) {
                  FontUtilities.getLogger().info("Found font via platform API for request:\"" + var1 + "\":, style=" + var2 + " found font: " + var6);
               }

               this.fontNameCache.put(var5, var6);
               return var6;
            }
         }

         if (this.deferredFontFiles.size() > 0) {
            var15 = this.findDeferredFont(var1, var2);
            if (var15 != null) {
               this.fontNameCache.put(var5, var15);
               return var15;
            }
         }

         if (FontUtilities.isSolaris && !this.loaded1dot0Fonts) {
            if (var4.equals("timesroman")) {
               var6 = this.findFont2D("serif", var2, var3);
               this.fontNameCache.put(var5, var6);
            }

            this.register1dot0Fonts();
            this.loaded1dot0Fonts = true;
            var8 = this.findFont2D(var1, var2, var3);
            return var8;
         } else {
            if (this.fontsAreRegistered || this.fontsAreRegisteredPerAppContext) {
               var8 = null;
               Hashtable var9;
               Hashtable var13;
               if (this.fontsAreRegistered) {
                  var13 = this.createdByFamilyName;
                  var9 = this.createdByFullName;
               } else {
                  AppContext var10 = AppContext.getAppContext();
                  var13 = (Hashtable)var10.get(regFamilyKey);
                  var9 = (Hashtable)var10.get(regFullNameKey);
               }

               var11 = (FontFamily)var13.get(var4);
               if (var11 != null) {
                  var6 = var11.getFontWithExactStyleMatch(var2);
                  if (var6 == null) {
                     var6 = var11.getFont(var2);
                  }

                  if (var6 == null) {
                     var6 = var11.getClosestStyle(var2);
                  }

                  if (var6 != null) {
                     if (this.fontsAreRegistered) {
                        this.fontNameCache.put(var5, var6);
                     }

                     return var6;
                  }
               }

               var6 = (Font2D)var9.get(var4);
               if (var6 != null) {
                  if (this.fontsAreRegistered) {
                     this.fontNameCache.put(var5, var6);
                  }

                  return var6;
               }
            }

            if (!this.loadedAllFonts) {
               if (FontUtilities.isLogging()) {
                  FontUtilities.getLogger().info("Load fonts looking for:" + var1);
               }

               this.loadFonts();
               this.loadedAllFonts = true;
               return this.findFont2D(var1, var2, var3);
            } else if (!this.loadedAllFontFiles) {
               if (FontUtilities.isLogging()) {
                  FontUtilities.getLogger().info("Load font files looking for:" + var1);
               }

               this.loadFontFiles();
               this.loadedAllFontFiles = true;
               return this.findFont2D(var1, var2, var3);
            } else if ((var6 = this.findFont2DAllLocales(var1, var2)) != null) {
               this.fontNameCache.put(var5, var6);
               return var6;
            } else {
               if (FontUtilities.isWindows) {
                  String var14 = this.getFontConfiguration().getFallbackFamilyName(var1, (String)null);
                  if (var14 != null) {
                     var6 = this.findFont2D(var14, var2, var3);
                     this.fontNameCache.put(var5, var6);
                     return var6;
                  }
               } else {
                  if (var4.equals("timesroman")) {
                     var6 = this.findFont2D("serif", var2, var3);
                     this.fontNameCache.put(var5, var6);
                     return var6;
                  }

                  if (var4.equals("helvetica")) {
                     var6 = this.findFont2D("sansserif", var2, var3);
                     this.fontNameCache.put(var5, var6);
                     return var6;
                  }

                  if (var4.equals("courier")) {
                     var6 = this.findFont2D("monospaced", var2, var3);
                     this.fontNameCache.put(var5, var6);
                     return var6;
                  }
               }

               if (FontUtilities.isLogging()) {
                  FontUtilities.getLogger().info("No font found for:" + var1);
               }

               switch(var3) {
               case 1:
                  return this.getDefaultPhysicalFont();
               case 2:
                  return this.getDefaultLogicalFont(var2);
               default:
                  return null;
               }
            }
         }
      }
   }

   public boolean usePlatformFontMetrics() {
      return this.usePlatformFontMetrics;
   }

   public int getNumFonts() {
      return this.physicalFonts.size() + this.maxCompFont;
   }

   private static boolean fontSupportsEncoding(Font var0, String var1) {
      return FontUtilities.getFont2D(var0).supportsEncoding(var1);
   }

   protected abstract String getFontPath(boolean var1);

   public Font2D createFont2D(final File var1, int var2, boolean var3, final CreatedFontTracker var4) throws FontFormatException {
      String var5 = var1.getPath();
      Object var6 = null;

      try {
         switch(var2) {
         case 0:
            var6 = new TrueTypeFont(var5, (Object)null, 0, true);
            break;
         case 1:
            var6 = new Type1Font(var5, (Object)null, var3);
            break;
         default:
            throw new FontFormatException("Unrecognised Font Format");
         }
      } catch (FontFormatException var13) {
         if (var3) {
            AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  if (var4 != null) {
                     var4.subBytes((int)var1.length());
                  }

                  var1.delete();
                  return null;
               }
            });
         }

         throw var13;
      }

      if (var3) {
         ((FileFont)var6).setFileToRemove(var1, var4);
         Class var9 = FontManager.class;
         synchronized(FontManager.class) {
            if (this.tmpFontFiles == null) {
               this.tmpFontFiles = new Vector();
            }

            this.tmpFontFiles.add(var1);
            if (this.fileCloser == null) {
               Runnable var10 = new Runnable() {
                  public void run() {
                     AccessController.doPrivileged(new PrivilegedAction() {
                        public Object run() {
                           for(int var1 = 0; var1 < 20; ++var1) {
                              if (SunFontManager.this.fontFileCache[var1] != null) {
                                 try {
                                    SunFontManager.this.fontFileCache[var1].close();
                                 } catch (Exception var5) {
                                 }
                              }
                           }

                           if (SunFontManager.this.tmpFontFiles != null) {
                              File[] var6 = new File[SunFontManager.this.tmpFontFiles.size()];
                              var6 = (File[])SunFontManager.this.tmpFontFiles.toArray(var6);

                              for(int var2 = 0; var2 < var6.length; ++var2) {
                                 try {
                                    var6[var2].delete();
                                 } catch (Exception var4) {
                                 }
                              }
                           }

                           return null;
                        }
                     });
                  }
               };
               AccessController.doPrivileged(() -> {
                  ThreadGroup var2 = ThreadGroupUtils.getRootThreadGroup();
                  this.fileCloser = new Thread(var2, var10);
                  this.fileCloser.setContextClassLoader((ClassLoader)null);
                  Runtime.getRuntime().addShutdownHook(this.fileCloser);
                  return null;
               });
            }
         }
      }

      return (Font2D)var6;
   }

   public synchronized String getFullNameByFileName(String var1) {
      PhysicalFont[] var2 = this.getPhysicalFonts();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3].platName.equals(var1)) {
            return var2[var3].getFontName((Locale)null);
         }
      }

      return null;
   }

   public synchronized void deRegisterBadFont(Font2D var1) {
      if (var1 instanceof PhysicalFont) {
         if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().severe("Deregister bad font: " + var1);
         }

         this.replaceFont((PhysicalFont)var1, this.getDefaultPhysicalFont());
      }
   }

   public synchronized void replaceFont(PhysicalFont var1, PhysicalFont var2) {
      if (var1.handle.font2D == var1) {
         int var4;
         if (var1 == var2) {
            if (FontUtilities.isLogging()) {
               FontUtilities.getLogger().severe("Can't replace bad font with itself " + var1);
            }

            PhysicalFont[] var3 = this.getPhysicalFonts();

            for(var4 = 0; var4 < var3.length; ++var4) {
               if (var3[var4] != var2) {
                  var2 = var3[var4];
                  break;
               }
            }

            if (var1 == var2) {
               if (FontUtilities.isLogging()) {
                  FontUtilities.getLogger().severe("This is bad. No good physicalFonts found.");
               }

               return;
            }
         }

         var1.handle.font2D = var2;
         this.physicalFonts.remove(var1.fullName);
         this.fullNameToFont.remove(var1.fullName.toLowerCase(Locale.ENGLISH));
         FontFamily.remove(var1);
         if (this.localeFullNamesToFont != null) {
            Map.Entry[] var7 = (Map.Entry[])((Map.Entry[])this.localeFullNamesToFont.entrySet().toArray(new Map.Entry[0]));

            for(var4 = 0; var4 < var7.length; ++var4) {
               if (var7[var4].getValue() == var1) {
                  try {
                     var7[var4].setValue(var2);
                  } catch (Exception var6) {
                     this.localeFullNamesToFont.remove(var7[var4].getKey());
                  }
               }
            }
         }

         for(int var8 = 0; var8 < this.maxCompFont; ++var8) {
            if (var2.getRank() > 2) {
               this.compFonts[var8].replaceComponentFont(var1, var2);
            }
         }

      }
   }

   private synchronized void loadLocaleNames() {
      if (this.localeFullNamesToFont == null) {
         this.localeFullNamesToFont = new HashMap();
         Font2D[] var1 = this.getRegisteredFonts();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2] instanceof TrueTypeFont) {
               TrueTypeFont var3 = (TrueTypeFont)var1[var2];
               String[] var4 = var3.getAllFullNames();

               for(int var5 = 0; var5 < var4.length; ++var5) {
                  this.localeFullNamesToFont.put(var4[var5], var3);
               }

               FontFamily var6 = FontFamily.getFamily(var3.familyName);
               if (var6 != null) {
                  FontFamily.addLocaleNames(var6, var3.getAllFamilyNames());
               }
            }
         }

      }
   }

   private Font2D findFont2DAllLocales(String var1, int var2) {
      if (FontUtilities.isLogging()) {
         FontUtilities.getLogger().info("Searching localised font names for:" + var1);
      }

      if (this.localeFullNamesToFont == null) {
         this.loadLocaleNames();
      }

      String var3 = var1.toLowerCase();
      Font2D var4 = null;
      FontFamily var5 = FontFamily.getLocaleFamily(var3);
      if (var5 != null) {
         var4 = var5.getFont(var2);
         if (var4 == null) {
            var4 = var5.getClosestStyle(var2);
         }

         if (var4 != null) {
            return var4;
         }
      }

      synchronized(this) {
         var4 = (Font2D)this.localeFullNamesToFont.get(var1);
      }

      if (var4 != null) {
         if (var4.style == var2 || var2 == 0) {
            return var4;
         }

         var5 = FontFamily.getFamily(var4.getFamilyName((Locale)null));
         if (var5 != null) {
            Font2D var6 = var5.getFont(var2);
            if (var6 != null) {
               return var6;
            }

            var6 = var5.getClosestStyle(var2);
            if (var6 != null) {
               if (!var6.canDoStyle(var2)) {
                  var6 = null;
               }

               return var6;
            }
         }
      }

      return var4;
   }

   public boolean maybeUsingAlternateCompositeFonts() {
      return this._usingAlternateComposites || this._usingPerAppContextComposites;
   }

   public boolean usingAlternateCompositeFonts() {
      return this._usingAlternateComposites || this._usingPerAppContextComposites && AppContext.getAppContext().get(CompositeFont.class) != null;
   }

   private static boolean maybeMultiAppContext() {
      Boolean var0 = (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            SecurityManager var1 = System.getSecurityManager();
            return new Boolean(var1 instanceof AppletSecurity);
         }
      });
      return var0;
   }

   public synchronized void useAlternateFontforJALocales() {
      if (FontUtilities.isLogging()) {
         FontUtilities.getLogger().info("Entered useAlternateFontforJALocales().");
      }

      if (FontUtilities.isWindows) {
         if (!maybeMultiAppContext()) {
            gAltJAFont = true;
         } else {
            AppContext var1 = AppContext.getAppContext();
            var1.put(altJAFontKey, altJAFontKey);
         }

      }
   }

   public boolean usingAlternateFontforJALocales() {
      if (!maybeMultiAppContext()) {
         return gAltJAFont;
      } else {
         AppContext var1 = AppContext.getAppContext();
         return var1.get(altJAFontKey) == altJAFontKey;
      }
   }

   public synchronized void preferLocaleFonts() {
      if (FontUtilities.isLogging()) {
         FontUtilities.getLogger().info("Entered preferLocaleFonts().");
      }

      if (FontConfiguration.willReorderForStartupLocale()) {
         if (!maybeMultiAppContext()) {
            if (this.gLocalePref) {
               return;
            }

            this.gLocalePref = true;
            this.createCompositeFonts(this.fontNameCache, this.gLocalePref, this.gPropPref);
            this._usingAlternateComposites = true;
         } else {
            AppContext var1 = AppContext.getAppContext();
            if (var1.get(localeFontKey) == localeFontKey) {
               return;
            }

            var1.put(localeFontKey, localeFontKey);
            boolean var2 = var1.get(proportionalFontKey) == proportionalFontKey;
            ConcurrentHashMap var3 = new ConcurrentHashMap();
            var1.put(CompositeFont.class, var3);
            this._usingPerAppContextComposites = true;
            this.createCompositeFonts(var3, true, var2);
         }

      }
   }

   public synchronized void preferProportionalFonts() {
      if (FontUtilities.isLogging()) {
         FontUtilities.getLogger().info("Entered preferProportionalFonts().");
      }

      if (FontConfiguration.hasMonoToPropMap()) {
         if (!maybeMultiAppContext()) {
            if (this.gPropPref) {
               return;
            }

            this.gPropPref = true;
            this.createCompositeFonts(this.fontNameCache, this.gLocalePref, this.gPropPref);
            this._usingAlternateComposites = true;
         } else {
            AppContext var1 = AppContext.getAppContext();
            if (var1.get(proportionalFontKey) == proportionalFontKey) {
               return;
            }

            var1.put(proportionalFontKey, proportionalFontKey);
            boolean var2 = var1.get(localeFontKey) == localeFontKey;
            ConcurrentHashMap var3 = new ConcurrentHashMap();
            var1.put(CompositeFont.class, var3);
            this._usingPerAppContextComposites = true;
            this.createCompositeFonts(var3, var2, true);
         }

      }
   }

   private static HashSet<String> getInstalledNames() {
      if (installedNames == null) {
         Locale var0 = getSystemStartupLocale();
         SunFontManager var1 = getInstance();
         String[] var2 = var1.getInstalledFontFamilyNames(var0);
         Font[] var3 = var1.getAllInstalledFonts();
         HashSet var4 = new HashSet();

         int var5;
         for(var5 = 0; var5 < var2.length; ++var5) {
            var4.add(var2[var5].toLowerCase(var0));
         }

         for(var5 = 0; var5 < var3.length; ++var5) {
            var4.add(var3[var5].getFontName(var0).toLowerCase(var0));
         }

         installedNames = var4;
      }

      return installedNames;
   }

   public boolean registerFont(Font var1) {
      if (var1 == null) {
         return false;
      } else {
         synchronized(regFamilyKey) {
            if (this.createdByFamilyName == null) {
               this.createdByFamilyName = new Hashtable();
               this.createdByFullName = new Hashtable();
            }
         }

         if (!FontAccess.getFontAccess().isCreatedFont(var1)) {
            return false;
         } else {
            HashSet var2 = getInstalledNames();
            Locale var3 = getSystemStartupLocale();
            String var4 = var1.getFamily(var3).toLowerCase();
            String var5 = var1.getFontName(var3).toLowerCase();
            if (!var2.contains(var4) && !var2.contains(var5)) {
               Hashtable var6;
               Hashtable var7;
               if (!maybeMultiAppContext()) {
                  var6 = this.createdByFamilyName;
                  var7 = this.createdByFullName;
                  this.fontsAreRegistered = true;
               } else {
                  AppContext var8 = AppContext.getAppContext();
                  var6 = (Hashtable)var8.get(regFamilyKey);
                  var7 = (Hashtable)var8.get(regFullNameKey);
                  if (var6 == null) {
                     var6 = new Hashtable();
                     var7 = new Hashtable();
                     var8.put(regFamilyKey, var6);
                     var8.put(regFullNameKey, var7);
                  }

                  this.fontsAreRegisteredPerAppContext = true;
               }

               Font2D var12 = FontUtilities.getFont2D(var1);
               int var9 = var12.getStyle();
               FontFamily var10 = (FontFamily)var6.get(var4);
               if (var10 == null) {
                  var10 = new FontFamily(var1.getFamily(var3));
                  var6.put(var4, var10);
               }

               if (this.fontsAreRegistered) {
                  this.removeFromCache(var10.getFont(0));
                  this.removeFromCache(var10.getFont(1));
                  this.removeFromCache(var10.getFont(2));
                  this.removeFromCache(var10.getFont(3));
                  this.removeFromCache((Font2D)var7.get(var5));
               }

               var10.setFont(var12, var9);
               var7.put(var5, var12);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private void removeFromCache(Font2D var1) {
      if (var1 != null) {
         String[] var2 = (String[])((String[])this.fontNameCache.keySet().toArray(STR_ARRAY));

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (this.fontNameCache.get(var2[var3]) == var1) {
               this.fontNameCache.remove(var2[var3]);
            }
         }

      }
   }

   public TreeMap<String, String> getCreatedFontFamilyNames() {
      Hashtable var1;
      if (this.fontsAreRegistered) {
         var1 = this.createdByFamilyName;
      } else {
         if (!this.fontsAreRegisteredPerAppContext) {
            return null;
         }

         AppContext var2 = AppContext.getAppContext();
         var1 = (Hashtable)var2.get(regFamilyKey);
      }

      Locale var11 = getSystemStartupLocale();
      synchronized(var1) {
         TreeMap var4 = new TreeMap();
         Iterator var5 = var1.values().iterator();

         while(var5.hasNext()) {
            FontFamily var6 = (FontFamily)var5.next();
            Font2D var7 = var6.getFont(0);
            if (var7 == null) {
               var7 = var6.getClosestStyle(0);
            }

            String var8 = var7.getFamilyName(var11);
            var4.put(var8.toLowerCase(var11), var8);
         }

         return var4;
      }
   }

   public Font[] getCreatedFonts() {
      Hashtable var1;
      if (this.fontsAreRegistered) {
         var1 = this.createdByFullName;
      } else {
         if (!this.fontsAreRegisteredPerAppContext) {
            return null;
         }

         AppContext var2 = AppContext.getAppContext();
         var1 = (Hashtable)var2.get(regFullNameKey);
      }

      Locale var10 = getSystemStartupLocale();
      synchronized(var1) {
         Font[] var4 = new Font[var1.size()];
         int var5 = 0;

         Font2D var7;
         for(Iterator var6 = var1.values().iterator(); var6.hasNext(); var4[var5++] = new Font(var7.getFontName(var10), 0, 1)) {
            var7 = (Font2D)var6.next();
         }

         return var4;
      }
   }

   protected String[] getPlatformFontDirs(boolean var1) {
      if (this.pathDirs != null) {
         return this.pathDirs;
      } else {
         String var2 = this.getPlatformFontPath(var1);
         StringTokenizer var3 = new StringTokenizer(var2, File.pathSeparator);
         ArrayList var4 = new ArrayList();

         try {
            while(var3.hasMoreTokens()) {
               var4.add(var3.nextToken());
            }
         } catch (NoSuchElementException var6) {
         }

         this.pathDirs = (String[])var4.toArray(new String[0]);
         return this.pathDirs;
      }
   }

   protected abstract String[] getDefaultPlatformFont();

   private void addDirFonts(String var1, File var2, FilenameFilter var3, int var4, boolean var5, int var6, boolean var7, boolean var8) {
      String[] var9 = var2.list(var3);
      if (var9 != null && var9.length != 0) {
         String[] var10 = new String[var9.length];
         String[][] var11 = new String[var9.length][];
         int var12 = 0;

         for(int var13 = 0; var13 < var9.length; ++var13) {
            File var14 = new File(var2, var9[var13]);
            String var15 = null;
            if (var8) {
               try {
                  var15 = var14.getCanonicalPath();
               } catch (IOException var19) {
               }
            }

            if (var15 == null) {
               var15 = var1 + File.separator + var9[var13];
            }

            if (!this.registeredFontFiles.contains(var15)) {
               if (this.badFonts != null && this.badFonts.contains(var15)) {
                  if (FontUtilities.debugFonts()) {
                     FontUtilities.getLogger().warning("skip bad font " + var15);
                  }
               } else {
                  this.registeredFontFiles.add(var15);
                  if (FontUtilities.debugFonts() && FontUtilities.getLogger().isLoggable(PlatformLogger.Level.INFO)) {
                     String var16 = "Registering font " + var15;
                     String[] var17 = this.getNativeNames(var15, (String)null);
                     if (var17 == null) {
                        var16 = var16 + " with no native name";
                     } else {
                        var16 = var16 + " with native name(s) " + var17[0];

                        for(int var18 = 1; var18 < var17.length; ++var18) {
                           var16 = var16 + ", " + var17[var18];
                        }
                     }

                     FontUtilities.getLogger().info(var16);
                  }

                  var10[var12] = var15;
                  var11[var12++] = this.getNativeNames(var15, (String)null);
               }
            }
         }

         this.registerFonts(var10, var11, var12, var4, var5, var6, var7);
      }
   }

   protected String[] getNativeNames(String var1, String var2) {
      return null;
   }

   protected String getFileNameFromPlatformName(String var1) {
      return this.fontConfig.getFileNameFromPlatformName(var1);
   }

   public FontConfiguration getFontConfiguration() {
      return this.fontConfig;
   }

   public String getPlatformFontPath(boolean var1) {
      if (this.fontPath == null) {
         this.fontPath = this.getFontPath(var1);
      }

      return this.fontPath;
   }

   public static boolean isOpenJDK() {
      return FontUtilities.isOpenJDK;
   }

   protected void loadFonts() {
      if (!this.discoveredAllFonts) {
         synchronized(this) {
            if (FontUtilities.debugFonts()) {
               Thread.dumpStack();
               FontUtilities.getLogger().info("SunGraphicsEnvironment.loadFonts() called");
            }

            this.initialiseDeferredFonts();
            AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  if (SunFontManager.this.fontPath == null) {
                     SunFontManager.this.fontPath = SunFontManager.this.getPlatformFontPath(SunFontManager.noType1Font);
                     SunFontManager.this.registerFontDirs(SunFontManager.this.fontPath);
                  }

                  if (SunFontManager.this.fontPath != null && !SunFontManager.this.gotFontsFromPlatform()) {
                     SunFontManager.this.registerFontsOnPath(SunFontManager.this.fontPath, false, 6, false, true);
                     SunFontManager.this.loadedAllFontFiles = true;
                  }

                  SunFontManager.this.registerOtherFontFiles(SunFontManager.this.registeredFontFiles);
                  SunFontManager.this.discoveredAllFonts = true;
                  return null;
               }
            });
         }
      }
   }

   protected void registerFontDirs(String var1) {
   }

   private void registerFontsOnPath(String var1, boolean var2, int var3, boolean var4, boolean var5) {
      StringTokenizer var6 = new StringTokenizer(var1, File.pathSeparator);

      try {
         while(var6.hasMoreTokens()) {
            this.registerFontsInDir(var6.nextToken(), var2, var3, var4, var5);
         }
      } catch (NoSuchElementException var8) {
      }

   }

   public void registerFontsInDir(String var1) {
      this.registerFontsInDir(var1, true, 2, true, false);
   }

   protected void registerFontsInDir(String var1, boolean var2, int var3, boolean var4, boolean var5) {
      File var6 = new File(var1);
      this.addDirFonts(var1, var6, ttFilter, 0, var2, var3 == 6 ? 3 : var3, var4, var5);
      this.addDirFonts(var1, var6, t1Filter, 1, var2, var3 == 6 ? 4 : var3, var4, var5);
   }

   protected void registerFontDir(String var1) {
   }

   public synchronized String getDefaultFontFile() {
      if (this.defaultFontFileName == null) {
         this.initDefaultFonts();
      }

      return this.defaultFontFileName;
   }

   private void initDefaultFonts() {
      if (!isOpenJDK()) {
         this.defaultFontName = "Lucida Sans Regular";
         if (this.useAbsoluteFontFileNames()) {
            this.defaultFontFileName = jreFontDirName + File.separator + "LucidaSansRegular.ttf";
         } else {
            this.defaultFontFileName = "LucidaSansRegular.ttf";
         }
      }

   }

   protected boolean useAbsoluteFontFileNames() {
      return true;
   }

   protected abstract FontConfiguration createFontConfiguration();

   public abstract FontConfiguration createFontConfiguration(boolean var1, boolean var2);

   public synchronized String getDefaultFontFaceName() {
      if (this.defaultFontName == null) {
         this.initDefaultFonts();
      }

      return this.defaultFontName;
   }

   public void loadFontFiles() {
      this.loadFonts();
      if (!this.loadedAllFontFiles) {
         synchronized(this) {
            if (FontUtilities.debugFonts()) {
               Thread.dumpStack();
               FontUtilities.getLogger().info("loadAllFontFiles() called");
            }

            AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  if (SunFontManager.this.fontPath == null) {
                     SunFontManager.this.fontPath = SunFontManager.this.getPlatformFontPath(SunFontManager.noType1Font);
                  }

                  if (SunFontManager.this.fontPath != null) {
                     SunFontManager.this.registerFontsOnPath(SunFontManager.this.fontPath, false, 6, false, true);
                  }

                  SunFontManager.this.loadedAllFontFiles = true;
                  return null;
               }
            });
         }
      }
   }

   private void initCompositeFonts(FontConfiguration var1, ConcurrentHashMap<String, Font2D> var2) {
      if (FontUtilities.isLogging()) {
         FontUtilities.getLogger().info("Initialising composite fonts");
      }

      int var3 = var1.getNumberCoreFonts();
      String[] var4 = var1.getPlatformFontNames();

      String[] var8;
      for(int var5 = 0; var5 < var4.length; ++var5) {
         String var6 = var4[var5];
         String var7 = this.getFileNameFromPlatformName(var6);
         var8 = null;
         if (var7 != null && !var7.equals(var6)) {
            if (var5 < var3) {
               this.addFontToPlatformFontPath(var6);
            }

            var8 = this.getNativeNames(var7, var6);
         } else {
            var7 = var6;
         }

         this.registerFontFile(var7, var8, 2, true);
      }

      this.registerPlatformFontsUsedByFontConfiguration();
      CompositeFontDescriptor[] var11 = var1.get2DCompositeFontInfo();

      for(int var12 = 0; var12 < var11.length; ++var12) {
         CompositeFontDescriptor var13 = var11[var12];
         var8 = var13.getComponentFileNames();
         String[] var9 = var13.getComponentFaceNames();
         if (missingFontFiles != null) {
            for(int var10 = 0; var10 < var8.length; ++var10) {
               if (missingFontFiles.contains(var8[var10])) {
                  var8[var10] = this.getDefaultFontFile();
                  var9[var10] = this.getDefaultFontFaceName();
               }
            }
         }

         if (var2 != null) {
            registerCompositeFont(var13.getFaceName(), var8, var9, var13.getCoreComponentCount(), var13.getExclusionRanges(), var13.getExclusionRangeLimits(), true, var2);
         } else {
            this.registerCompositeFont(var13.getFaceName(), var8, var9, var13.getCoreComponentCount(), var13.getExclusionRanges(), var13.getExclusionRangeLimits(), true);
         }

         if (FontUtilities.debugFonts()) {
            FontUtilities.getLogger().info("registered " + var13.getFaceName());
         }
      }

   }

   protected void addFontToPlatformFontPath(String var1) {
   }

   protected void registerFontFile(String var1, String[] var2, int var3, boolean var4) {
      if (!this.registeredFontFiles.contains(var1)) {
         byte var5;
         if (ttFilter.accept((File)null, var1)) {
            var5 = 0;
         } else if (t1Filter.accept((File)null, var1)) {
            var5 = 1;
         } else {
            var5 = 5;
         }

         this.registeredFontFiles.add(var1);
         if (var4) {
            this.registerDeferredFont(var1, var1, var2, var5, false, var3);
         } else {
            this.registerFontFile(var1, var2, var5, false, var3);
         }

      }
   }

   protected void registerPlatformFontsUsedByFontConfiguration() {
   }

   protected void addToMissingFontFileList(String var1) {
      if (missingFontFiles == null) {
         missingFontFiles = new HashSet();
      }

      missingFontFiles.add(var1);
   }

   private boolean isNameForRegisteredFile(String var1) {
      String var2 = this.getFileNameForFontName(var1);
      return var2 == null ? false : this.registeredFontFiles.contains(var2);
   }

   public void createCompositeFonts(ConcurrentHashMap<String, Font2D> var1, boolean var2, boolean var3) {
      FontConfiguration var4 = this.createFontConfiguration(var2, var3);
      this.initCompositeFonts(var4, var1);
   }

   public Font[] getAllInstalledFonts() {
      if (this.allFonts == null) {
         this.loadFonts();
         TreeMap var1 = new TreeMap();
         Font2D[] var2 = this.getRegisteredFonts();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (!(var2[var3] instanceof NativeFont)) {
               var1.put(var2[var3].getFontName((Locale)null), var2[var3]);
            }
         }

         String[] var9 = this.getFontNamesFromPlatform();
         if (var9 != null) {
            for(int var4 = 0; var4 < var9.length; ++var4) {
               if (!this.isNameForRegisteredFile(var9[var4])) {
                  var1.put(var9[var4], (Object)null);
               }
            }
         }

         String[] var10 = null;
         int var6;
         if (var1.size() > 0) {
            var10 = new String[var1.size()];
            Object[] var5 = var1.keySet().toArray();

            for(var6 = 0; var6 < var5.length; ++var6) {
               var10[var6] = (String)var5[var6];
            }
         }

         Font[] var11 = new Font[var10.length];

         for(var6 = 0; var6 < var10.length; ++var6) {
            var11[var6] = new Font(var10[var6], 0, 1);
            Font2D var7 = (Font2D)var1.get(var10[var6]);
            if (var7 != null) {
               FontAccess.getFontAccess().setFont2D(var11[var6], var7.handle);
            }
         }

         this.allFonts = var11;
      }

      Font[] var8 = new Font[this.allFonts.length];
      System.arraycopy(this.allFonts, 0, var8, 0, this.allFonts.length);
      return var8;
   }

   public String[] getInstalledFontFamilyNames(Locale var1) {
      if (var1 == null) {
         var1 = Locale.getDefault();
      }

      if (this.allFamilies != null && this.lastDefaultLocale != null && var1.equals(this.lastDefaultLocale)) {
         String[] var7 = new String[this.allFamilies.length];
         System.arraycopy(this.allFamilies, 0, var7, 0, this.allFamilies.length);
         return var7;
      } else {
         TreeMap var2 = new TreeMap();
         String var3 = "Serif";
         var2.put(var3.toLowerCase(), var3);
         var3 = "SansSerif";
         var2.put(var3.toLowerCase(), var3);
         var3 = "Monospaced";
         var2.put(var3.toLowerCase(), var3);
         var3 = "Dialog";
         var2.put(var3.toLowerCase(), var3);
         var3 = "DialogInput";
         var2.put(var3.toLowerCase(), var3);
         if (var1.equals(getSystemStartupLocale()) && this.getFamilyNamesFromPlatform(var2, var1)) {
            this.getJREFontFamilyNames(var2, var1);
         } else {
            this.loadFontFiles();
            PhysicalFont[] var4 = this.getPhysicalFonts();

            for(int var5 = 0; var5 < var4.length; ++var5) {
               if (!(var4[var5] instanceof NativeFont)) {
                  String var6 = var4[var5].getFamilyName(var1);
                  var2.put(var6.toLowerCase(var1), var6);
               }
            }
         }

         this.addNativeFontFamilyNames(var2, var1);
         String[] var8 = new String[var2.size()];
         Object[] var9 = var2.keySet().toArray();

         for(int var10 = 0; var10 < var9.length; ++var10) {
            var8[var10] = (String)var2.get(var9[var10]);
         }

         if (var1.equals(Locale.getDefault())) {
            this.lastDefaultLocale = var1;
            this.allFamilies = new String[var8.length];
            System.arraycopy(var8, 0, this.allFamilies, 0, this.allFamilies.length);
         }

         return var8;
      }
   }

   protected void addNativeFontFamilyNames(TreeMap<String, String> var1, Locale var2) {
   }

   public void register1dot0Fonts() {
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            String var1 = "/usr/openwin/lib/X11/fonts/Type1";
            SunFontManager.this.registerFontsInDir(var1, true, 4, false, false);
            return null;
         }
      });
   }

   protected void getJREFontFamilyNames(TreeMap<String, String> var1, Locale var2) {
      this.registerDeferredJREFonts(jreFontDirName);
      PhysicalFont[] var3 = this.getPhysicalFonts();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (!(var3[var4] instanceof NativeFont)) {
            String var5 = var3[var4].getFamilyName(var2);
            var1.put(var5.toLowerCase(var2), var5);
         }
      }

   }

   private static Locale getSystemStartupLocale() {
      if (systemLocale == null) {
         systemLocale = (Locale)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               String var1 = System.getProperty("file.encoding", "");
               String var2 = System.getProperty("sun.jnu.encoding");
               if (var2 != null && !var2.equals(var1)) {
                  return Locale.ROOT;
               } else {
                  String var3 = System.getProperty("user.language", "en");
                  String var4 = System.getProperty("user.country", "");
                  String var5 = System.getProperty("user.variant", "");
                  return new Locale(var3, var4, var5);
               }
            }
         });
      }

      return systemLocale;
   }

   void addToPool(FileFont var1) {
      FileFont var2 = null;
      int var3 = -1;
      synchronized(this.fontFileCache) {
         for(int var5 = 0; var5 < 20; ++var5) {
            if (this.fontFileCache[var5] == var1) {
               return;
            }

            if (this.fontFileCache[var5] == null && var3 < 0) {
               var3 = var5;
            }
         }

         if (var3 >= 0) {
            this.fontFileCache[var3] = var1;
            return;
         }

         var2 = this.fontFileCache[this.lastPoolIndex];
         this.fontFileCache[this.lastPoolIndex] = var1;
         this.lastPoolIndex = (this.lastPoolIndex + 1) % 20;
      }

      if (var2 != null) {
         var2.close();
      }

   }

   protected FontUIResource getFontConfigFUIR(String var1, int var2, int var3) {
      return new FontUIResource(var1, var2, var3);
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            FontManagerNativeLibrary.load();
            SunFontManager.initIDs();
            switch(StrikeCache.nativeAddressSize) {
            case 4:
               SunFontManager.longAddresses = false;
               break;
            case 8:
               SunFontManager.longAddresses = true;
               break;
            default:
               throw new RuntimeException("Unexpected address size");
            }

            SunFontManager.noType1Font = "true".equals(System.getProperty("sun.java2d.noType1Font"));
            SunFontManager.jreLibDirName = System.getProperty("java.home", "") + File.separator + "lib";
            SunFontManager.jreFontDirName = SunFontManager.jreLibDirName + File.separator + "fonts";
            new File(SunFontManager.jreFontDirName + File.separator + "LucidaSansRegular.ttf");
            return null;
         }
      });
      altJAFontKey = new Object();
      localeFontKey = new Object();
      proportionalFontKey = new Object();
      gAltJAFont = false;
      installedNames = null;
      regFamilyKey = new Object();
      regFullNameKey = new Object();
      systemLocale = null;
   }

   public static class FamilyDescription {
      public String familyName;
      public String plainFullName;
      public String boldFullName;
      public String italicFullName;
      public String boldItalicFullName;
      public String plainFileName;
      public String boldFileName;
      public String italicFileName;
      public String boldItalicFileName;
   }

   private static final class FontRegistrationInfo {
      String fontFilePath;
      String[] nativeNames;
      int fontFormat;
      boolean javaRasterizer;
      int fontRank;

      FontRegistrationInfo(String var1, String[] var2, int var3, boolean var4, int var5) {
         this.fontFilePath = var1;
         this.nativeNames = var2;
         this.fontFormat = var3;
         this.javaRasterizer = var4;
         this.fontRank = var5;
      }
   }

   private static class TTorT1Filter implements FilenameFilter {
      private TTorT1Filter() {
      }

      public boolean accept(File var1, String var2) {
         int var3 = var2.length() - 4;
         if (var3 <= 0) {
            return false;
         } else {
            boolean var4 = var2.startsWith(".ttf", var3) || var2.startsWith(".TTF", var3) || var2.startsWith(".ttc", var3) || var2.startsWith(".TTC", var3) || var2.startsWith(".otf", var3) || var2.startsWith(".OTF", var3);
            if (var4) {
               return true;
            } else if (SunFontManager.noType1Font) {
               return false;
            } else {
               return var2.startsWith(".pfa", var3) || var2.startsWith(".pfb", var3) || var2.startsWith(".PFA", var3) || var2.startsWith(".PFB", var3);
            }
         }
      }

      // $FF: synthetic method
      TTorT1Filter(Object var1) {
         this();
      }
   }

   private static class T1Filter implements FilenameFilter {
      private T1Filter() {
      }

      public boolean accept(File var1, String var2) {
         if (SunFontManager.noType1Font) {
            return false;
         } else {
            int var3 = var2.length() - 4;
            if (var3 <= 0) {
               return false;
            } else {
               return var2.startsWith(".pfa", var3) || var2.startsWith(".pfb", var3) || var2.startsWith(".PFA", var3) || var2.startsWith(".PFB", var3);
            }
         }
      }

      // $FF: synthetic method
      T1Filter(Object var1) {
         this();
      }
   }

   private static class TTFilter implements FilenameFilter {
      private TTFilter() {
      }

      public boolean accept(File var1, String var2) {
         int var3 = var2.length() - 4;
         if (var3 <= 0) {
            return false;
         } else {
            return var2.startsWith(".ttf", var3) || var2.startsWith(".TTF", var3) || var2.startsWith(".ttc", var3) || var2.startsWith(".TTC", var3) || var2.startsWith(".otf", var3) || var2.startsWith(".OTF", var3);
         }
      }

      // $FF: synthetic method
      TTFilter(Object var1) {
         this();
      }
   }
}
