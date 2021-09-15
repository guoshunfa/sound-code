package sun.font;

import java.util.Locale;
import sun.awt.SunHints;
import sun.awt.SunToolkit;
import sun.util.logging.PlatformLogger;

public class FontConfigManager {
   static boolean fontConfigFailed = false;
   private static final FontConfigManager.FontConfigInfo fcInfo = new FontConfigManager.FontConfigInfo();
   private static String[] fontConfigNames = new String[]{"sans:regular:roman", "sans:bold:roman", "sans:regular:italic", "sans:bold:italic", "serif:regular:roman", "serif:bold:roman", "serif:regular:italic", "serif:bold:italic", "monospace:regular:roman", "monospace:bold:roman", "monospace:regular:italic", "monospace:bold:italic"};
   private FontConfigManager.FcCompFont[] fontConfigFonts;

   public static Object getFontConfigAAHint() {
      return getFontConfigAAHint("sans");
   }

   public static Object getFontConfigAAHint(String var0) {
      if (FontUtilities.isWindows) {
         return null;
      } else {
         int var1 = getFontConfigAASettings(getFCLocaleStr(), var0);
         return var1 < 0 ? null : SunHints.Value.get(2, var1);
      }
   }

   private static String getFCLocaleStr() {
      Locale var0 = SunToolkit.getStartupLocale();
      String var1 = var0.getLanguage();
      String var2 = var0.getCountry();
      if (!var2.equals("")) {
         var1 = var1 + "-" + var2;
      }

      return var1;
   }

   public static native int getFontConfigVersion();

   public synchronized void initFontConfigFonts(boolean var1) {
      if (this.fontConfigFonts == null || var1 && this.fontConfigFonts[0].allFonts == null) {
         if (!FontUtilities.isWindows && !fontConfigFailed) {
            long var2 = 0L;
            if (FontUtilities.isLogging()) {
               var2 = System.nanoTime();
            }

            FontConfigManager.FcCompFont[] var4 = new FontConfigManager.FcCompFont[fontConfigNames.length];

            int var6;
            for(int var5 = 0; var5 < var4.length; ++var5) {
               var4[var5] = new FontConfigManager.FcCompFont();
               var4[var5].fcName = fontConfigNames[var5];
               var6 = var4[var5].fcName.indexOf(58);
               var4[var5].fcFamily = var4[var5].fcName.substring(0, var6);
               var4[var5].jdkName = FontUtilities.mapFcName(var4[var5].fcFamily);
               var4[var5].style = var5 % 4;
            }

            getFontConfig(getFCLocaleStr(), fcInfo, var4, var1);
            FontConfigManager.FontConfigFont var13 = null;

            for(var6 = 0; var6 < var4.length; ++var6) {
               FontConfigManager.FcCompFont var7 = var4[var6];
               if (var7.firstFont == null) {
                  if (FontUtilities.isLogging()) {
                     PlatformLogger var8 = FontUtilities.getLogger();
                     var8.info("Fontconfig returned no font for " + var4[var6].fcName);
                  }

                  fontConfigFailed = true;
               } else if (var13 == null) {
                  var13 = var7.firstFont;
               }
            }

            PlatformLogger var15;
            if (var13 == null) {
               if (FontUtilities.isLogging()) {
                  var15 = FontUtilities.getLogger();
                  var15.info("Fontconfig returned no fonts at all.");
               }

               fontConfigFailed = true;
            } else {
               if (fontConfigFailed) {
                  for(var6 = 0; var6 < var4.length; ++var6) {
                     if (var4[var6].firstFont == null) {
                        var4[var6].firstFont = var13;
                     }
                  }
               }

               this.fontConfigFonts = var4;
               if (FontUtilities.isLogging()) {
                  var15 = FontUtilities.getLogger();
                  long var14 = System.nanoTime();
                  var15.info("Time spent accessing fontconfig=" + (var14 - var2) / 1000000L + "ms.");

                  for(int var9 = 0; var9 < this.fontConfigFonts.length; ++var9) {
                     FontConfigManager.FcCompFont var10 = this.fontConfigFonts[var9];
                     var15.info("FC font " + var10.fcName + " maps to family " + var10.firstFont.familyName + " in file " + var10.firstFont.fontFile);
                     if (var10.allFonts != null) {
                        for(int var11 = 0; var11 < var10.allFonts.length; ++var11) {
                           FontConfigManager.FontConfigFont var12 = var10.allFonts[var11];
                           var15.info("Family=" + var12.familyName + " Style=" + var12.styleStr + " Fullname=" + var12.fullName + " File=" + var12.fontFile);
                        }
                     }
                  }
               }

            }
         }
      }
   }

   public PhysicalFont registerFromFcInfo(FontConfigManager.FcCompFont var1) {
      SunFontManager var2 = SunFontManager.getInstance();
      String var3 = var1.firstFont.fontFile;
      int var4 = var3.length() - 4;
      if (var4 <= 0) {
         return null;
      } else {
         String var5 = var3.substring(var4).toLowerCase();
         boolean var6 = var5.equals(".ttc");
         PhysicalFont var7 = var2.getRegisteredFontFile(var3);
         Font2D var10;
         if (var7 != null) {
            if (var6) {
               var10 = var2.findFont2D(var1.firstFont.familyName, var1.style, 0);
               return var10 instanceof PhysicalFont ? (PhysicalFont)var10 : null;
            } else {
               return var7;
            }
         } else {
            var7 = var2.findJREDeferredFont(var1.firstFont.familyName, var1.style);
            if (var7 == null && var2.isDeferredFont(var3)) {
               var7 = var2.initialiseDeferredFont(var1.firstFont.fontFile);
               if (var7 != null) {
                  if (var6) {
                     var10 = var2.findFont2D(var1.firstFont.familyName, var1.style, 0);
                     if (var10 instanceof PhysicalFont) {
                        return (PhysicalFont)var10;
                     }

                     return null;
                  }

                  return var7;
               }
            }

            if (var7 == null) {
               byte var8 = -1;
               byte var9 = 6;
               if (!var5.equals(".ttf") && !var6) {
                  if (var5.equals(".pfa") || var5.equals(".pfb")) {
                     var8 = 1;
                     var9 = 4;
                  }
               } else {
                  var8 = 0;
                  var9 = 3;
               }

               var7 = var2.registerFontFile(var1.firstFont.fontFile, (String[])null, var8, true, var9);
            }

            return var7;
         }
      }
   }

   public CompositeFont getFontConfigFont(String var1, int var2) {
      var1 = var1.toLowerCase();
      this.initFontConfigFonts(false);
      if (this.fontConfigFonts == null) {
         return null;
      } else {
         FontConfigManager.FcCompFont var3 = null;

         for(int var4 = 0; var4 < this.fontConfigFonts.length; ++var4) {
            if (var1.equals(this.fontConfigFonts[var4].fcFamily) && var2 == this.fontConfigFonts[var4].style) {
               var3 = this.fontConfigFonts[var4];
               break;
            }
         }

         if (var3 == null) {
            var3 = this.fontConfigFonts[0];
         }

         if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().info("FC name=" + var1 + " style=" + var2 + " uses " + var3.firstFont.familyName + " in file: " + var3.firstFont.fontFile);
         }

         if (var3.compFont != null) {
            return var3.compFont;
         } else {
            FontManager var10 = FontManagerFactory.getInstance();
            CompositeFont var5 = (CompositeFont)var10.findFont2D(var3.jdkName, var2, 2);
            if (var3.firstFont.familyName != null && var3.firstFont.fontFile != null) {
               FontFamily var6 = FontFamily.getFamily(var3.firstFont.familyName);
               PhysicalFont var7 = null;
               if (var6 != null) {
                  Font2D var8 = var6.getFontWithExactStyleMatch(var3.style);
                  if (var8 instanceof PhysicalFont) {
                     var7 = (PhysicalFont)var8;
                  }
               }

               if (var7 == null || !var3.firstFont.fontFile.equals(var7.platName)) {
                  var7 = this.registerFromFcInfo(var3);
                  if (var7 == null) {
                     return var3.compFont = var5;
                  }

                  var6 = FontFamily.getFamily(var7.getFamilyName((Locale)null));
               }

               for(int var11 = 0; var11 < this.fontConfigFonts.length; ++var11) {
                  FontConfigManager.FcCompFont var9 = this.fontConfigFonts[var11];
                  if (var9 != var3 && var7.getFamilyName((Locale)null).equals(var9.firstFont.familyName) && !var9.firstFont.fontFile.equals(var7.platName) && var6.getFontWithExactStyleMatch(var9.style) == null) {
                     this.registerFromFcInfo(this.fontConfigFonts[var11]);
                  }
               }

               return var3.compFont = new CompositeFont(var7, var5);
            } else {
               return var3.compFont = var5;
            }
         }
      }
   }

   public FontConfigManager.FcCompFont[] getFontConfigFonts() {
      return this.fontConfigFonts;
   }

   private static native void getFontConfig(String var0, FontConfigManager.FontConfigInfo var1, FontConfigManager.FcCompFont[] var2, boolean var3);

   void populateFontConfig(FontConfigManager.FcCompFont[] var1) {
      this.fontConfigFonts = var1;
   }

   FontConfigManager.FcCompFont[] loadFontConfig() {
      this.initFontConfigFonts(true);
      return this.fontConfigFonts;
   }

   FontConfigManager.FontConfigInfo getFontConfigInfo() {
      this.initFontConfigFonts(true);
      return fcInfo;
   }

   private static native int getFontConfigAASettings(String var0, String var1);

   public static class FontConfigInfo {
      public int fcVersion;
      public String[] cacheDirs = new String[4];
   }

   public static class FcCompFont {
      public String fcName;
      public String fcFamily;
      public String jdkName;
      public int style;
      public FontConfigManager.FontConfigFont firstFont;
      public FontConfigManager.FontConfigFont[] allFonts;
      public CompositeFont compFont;
   }

   public static class FontConfigFont {
      public String familyName;
      public String styleStr;
      public String fullName;
      public String fontFile;
   }
}
