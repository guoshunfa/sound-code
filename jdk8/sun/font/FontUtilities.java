package sun.font;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.plaf.FontUIResource;
import sun.util.logging.PlatformLogger;

public final class FontUtilities {
   public static boolean isSolaris;
   public static boolean isLinux;
   public static boolean isMacOSX;
   public static boolean isSolaris8;
   public static boolean isSolaris9;
   public static boolean isOpenSolaris;
   public static boolean useT2K;
   public static boolean isWindows;
   public static boolean isOpenJDK;
   static final String LUCIDA_FILE_NAME = "LucidaSansRegular.ttf";
   private static boolean debugFonts = false;
   private static PlatformLogger logger = null;
   private static boolean logging;
   public static final int MIN_LAYOUT_CHARCODE = 768;
   public static final int MAX_LAYOUT_CHARCODE = 8303;
   private static volatile SoftReference<ConcurrentHashMap<PhysicalFont, CompositeFont>> compMapRef;
   private static final String[][] nameMap;

   public static Font2D getFont2D(Font var0) {
      return FontAccess.getFontAccess().getFont2D(var0);
   }

   public static boolean isComplexText(char[] var0, int var1, int var2) {
      for(int var3 = var1; var3 < var2; ++var3) {
         if (var0[var3] >= 768 && isNonSimpleChar(var0[var3])) {
            return true;
         }
      }

      return false;
   }

   public static boolean isNonSimpleChar(char var0) {
      return isComplexCharCode(var0) || var0 >= '\ud800' && var0 <= '\udfff';
   }

   public static boolean isComplexCharCode(int var0) {
      if (var0 >= 768 && var0 <= 8303) {
         if (var0 <= 879) {
            return true;
         } else if (var0 < 1424) {
            return false;
         } else if (var0 <= 1791) {
            return true;
         } else if (var0 < 2304) {
            return false;
         } else if (var0 <= 3711) {
            return true;
         } else if (var0 < 3840) {
            return false;
         } else if (var0 <= 4095) {
            return true;
         } else if (var0 < 4352) {
            return false;
         } else if (var0 < 4607) {
            return true;
         } else if (var0 < 6016) {
            return false;
         } else if (var0 <= 6143) {
            return true;
         } else if (var0 < 8204) {
            return false;
         } else if (var0 <= 8205) {
            return true;
         } else if (var0 >= 8234 && var0 <= 8238) {
            return true;
         } else {
            return var0 >= 8298 && var0 <= 8303;
         }
      } else {
         return false;
      }
   }

   public static PlatformLogger getLogger() {
      return logger;
   }

   public static boolean isLogging() {
      return logging;
   }

   public static boolean debugFonts() {
      return debugFonts;
   }

   public static boolean fontSupportsDefaultEncoding(Font var0) {
      return getFont2D(var0) instanceof CompositeFont;
   }

   public static FontUIResource getCompositeFontUIResource(Font var0) {
      FontUIResource var1 = new FontUIResource(var0);
      Font2D var2 = getFont2D(var0);
      if (!(var2 instanceof PhysicalFont)) {
         return var1;
      } else {
         FontManager var3 = FontManagerFactory.getInstance();
         Font2D var4 = var3.findFont2D("dialog", var0.getStyle(), 0);
         if (var4 != null && var4 instanceof CompositeFont) {
            CompositeFont var5 = (CompositeFont)var4;
            PhysicalFont var6 = (PhysicalFont)var2;
            ConcurrentHashMap var7 = (ConcurrentHashMap)compMapRef.get();
            if (var7 == null) {
               var7 = new ConcurrentHashMap();
               compMapRef = new SoftReference(var7);
            }

            CompositeFont var8 = (CompositeFont)var7.get(var6);
            if (var8 == null) {
               var8 = new CompositeFont(var6, var5);
               var7.put(var6, var8);
            }

            FontAccess.getFontAccess().setFont2D(var1, var8.handle);
            FontAccess.getFontAccess().setCreatedFont(var1);
            return var1;
         } else {
            return var1;
         }
      }
   }

   public static String mapFcName(String var0) {
      for(int var1 = 0; var1 < nameMap.length; ++var1) {
         if (var0.equals(nameMap[var1][0])) {
            return nameMap[var1][1];
         }
      }

      return null;
   }

   public static FontUIResource getFontConfigFUIR(String var0, int var1, int var2) {
      String var3 = mapFcName(var0);
      if (var3 == null) {
         var3 = "sansserif";
      }

      FontManager var5 = FontManagerFactory.getInstance();
      FontUIResource var4;
      if (var5 instanceof SunFontManager) {
         SunFontManager var6 = (SunFontManager)var5;
         var4 = var6.getFontConfigFUIR(var3, var1, var2);
      } else {
         var4 = new FontUIResource(var3, var1, var2);
      }

      return var4;
   }

   public static boolean textLayoutIsCompatible(Font var0) {
      Font2D var1 = getFont2D(var0);
      if (!(var1 instanceof TrueTypeFont)) {
         return false;
      } else {
         TrueTypeFont var2 = (TrueTypeFont)var1;
         return var2.getDirectoryEntry(1196643650) == null || var2.getDirectoryEntry(1196445523) != null;
      }
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            String var1 = System.getProperty("os.name", "unknownOS");
            FontUtilities.isSolaris = var1.startsWith("SunOS");
            FontUtilities.isLinux = var1.startsWith("Linux");
            FontUtilities.isMacOSX = var1.contains("OS X");
            String var2 = System.getProperty("sun.java2d.font.scaler");
            if (var2 != null) {
               FontUtilities.useT2K = "t2k".equals(var2);
            } else {
               FontUtilities.useT2K = false;
            }

            String var3;
            File var5;
            String var6;
            if (FontUtilities.isSolaris) {
               var3 = System.getProperty("os.version", "0.0");
               FontUtilities.isSolaris8 = var3.startsWith("5.8");
               FontUtilities.isSolaris9 = var3.startsWith("5.9");
               float var4 = Float.parseFloat(var3);
               if (var4 > 5.1F) {
                  var5 = new File("/etc/release");
                  var6 = null;

                  try {
                     FileInputStream var7 = new FileInputStream(var5);
                     InputStreamReader var8 = new InputStreamReader(var7, "ISO-8859-1");
                     BufferedReader var9 = new BufferedReader(var8);
                     var6 = var9.readLine();
                     var7.close();
                  } catch (Exception var10) {
                  }

                  if (var6 != null && var6.indexOf("OpenSolaris") >= 0) {
                     FontUtilities.isOpenSolaris = true;
                  } else {
                     FontUtilities.isOpenSolaris = false;
                  }
               } else {
                  FontUtilities.isOpenSolaris = false;
               }
            } else {
               FontUtilities.isSolaris8 = false;
               FontUtilities.isSolaris9 = false;
               FontUtilities.isOpenSolaris = false;
            }

            FontUtilities.isWindows = var1.startsWith("Windows");
            var3 = System.getProperty("java.home", "") + File.separator + "lib";
            String var11 = var3 + File.separator + "fonts";
            var5 = new File(var11 + File.separator + "LucidaSansRegular.ttf");
            FontUtilities.isOpenJDK = !var5.exists();
            var6 = System.getProperty("sun.java2d.debugfonts");
            if (var6 != null && !var6.equals("false")) {
               FontUtilities.debugFonts = true;
               FontUtilities.logger = PlatformLogger.getLogger("sun.java2d");
               if (var6.equals("warning")) {
                  FontUtilities.logger.setLevel(PlatformLogger.Level.WARNING);
               } else if (var6.equals("severe")) {
                  FontUtilities.logger.setLevel(PlatformLogger.Level.SEVERE);
               }
            }

            if (FontUtilities.debugFonts) {
               FontUtilities.logger = PlatformLogger.getLogger("sun.java2d");
               FontUtilities.logging = FontUtilities.logger.isEnabled();
            }

            return null;
         }
      });
      compMapRef = new SoftReference((Object)null);
      nameMap = new String[][]{{"sans", "sansserif"}, {"sans-serif", "sansserif"}, {"serif", "serif"}, {"monospace", "monospaced"}};
   }
}
