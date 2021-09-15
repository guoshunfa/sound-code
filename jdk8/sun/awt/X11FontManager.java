package sun.awt;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.plaf.FontUIResource;
import sun.awt.motif.MFontConfiguration;
import sun.font.CompositeFont;
import sun.font.FcFontConfiguration;
import sun.font.FontAccess;
import sun.font.FontUtilities;
import sun.font.NativeFont;
import sun.font.SunFontManager;
import sun.util.logging.PlatformLogger;

public final class X11FontManager extends FcFontManager {
   private static final int FOUNDRY_FIELD = 1;
   private static final int FAMILY_NAME_FIELD = 2;
   private static final int WEIGHT_NAME_FIELD = 3;
   private static final int SLANT_FIELD = 4;
   private static final int SETWIDTH_NAME_FIELD = 5;
   private static final int ADD_STYLE_NAME_FIELD = 6;
   private static final int PIXEL_SIZE_FIELD = 7;
   private static final int POINT_SIZE_FIELD = 8;
   private static final int RESOLUTION_X_FIELD = 9;
   private static final int RESOLUTION_Y_FIELD = 10;
   private static final int SPACING_FIELD = 11;
   private static final int AVERAGE_WIDTH_FIELD = 12;
   private static final int CHARSET_REGISTRY_FIELD = 13;
   private static final int CHARSET_ENCODING_FIELD = 14;
   private static Map fontNameMap = new HashMap();
   private static Map xlfdMap = new HashMap();
   private static Map xFontDirsMap;
   private static HashSet<String> fontConfigDirs = null;
   HashMap<String, String> oblmap = null;
   private static HashMap registeredDirs = new HashMap();
   private static String[] fontdirs = null;

   public static X11FontManager getInstance() {
      return (X11FontManager)SunFontManager.getInstance();
   }

   public String getFileNameFromPlatformName(String var1) {
      if (var1.startsWith("/")) {
         return var1;
      } else {
         String var2 = null;
         String var3 = this.specificFontIDForName(var1);
         var2 = super.getFileNameFromPlatformName(var1);
         if (var2 != null) {
            if (this.isHeadless() && var2.startsWith("-")) {
               return null;
            }

            if (var2.startsWith("/")) {
               Vector var4 = (Vector)xlfdMap.get(var2);
               if (var4 == null) {
                  if (this.getFontConfiguration().needToSearchForFile(var2)) {
                     var2 = null;
                  }

                  if (var2 != null) {
                     var4 = new Vector();
                     var4.add(var1);
                     xlfdMap.put(var2, var4);
                  }
               } else if (!var4.contains(var1)) {
                  var4.add(var1);
               }
            }

            if (var2 != null) {
               fontNameMap.put(var3, var2);
               return var2;
            }
         }

         if (var3 != null) {
            var2 = (String)fontNameMap.get(var3);
            if (var2 == null && FontUtilities.isLinux && !isOpenJDK()) {
               if (this.oblmap == null) {
                  this.initObliqueLucidaFontMap();
               }

               String var5 = this.getObliqueLucidaFontID(var3);
               if (var5 != null) {
                  var2 = (String)this.oblmap.get(var5);
               }
            }

            if (this.fontPath == null && (var2 == null || !var2.startsWith("/"))) {
               if (FontUtilities.debugFonts()) {
                  FontUtilities.getLogger().warning("** Registering all font paths because can't find file for " + var1);
               }

               this.fontPath = this.getPlatformFontPath(noType1Font);
               this.registerFontDirs(this.fontPath);
               if (FontUtilities.debugFonts()) {
                  FontUtilities.getLogger().warning("** Finished registering all font paths");
               }

               var2 = (String)fontNameMap.get(var3);
            }

            if (var2 == null && !this.isHeadless()) {
               var2 = getX11FontName(var1);
            }

            if (var2 == null) {
               var3 = this.switchFontIDForName(var1);
               var2 = (String)fontNameMap.get(var3);
            }

            if (var2 != null) {
               fontNameMap.put(var3, var2);
            }
         }

         return var2;
      }
   }

   protected String[] getNativeNames(String var1, String var2) {
      Vector var3;
      if ((var3 = (Vector)xlfdMap.get(var1)) == null) {
         if (var2 == null) {
            return null;
         } else {
            String[] var5 = new String[]{var2};
            return var5;
         }
      } else {
         int var4 = var3.size();
         return (String[])((String[])var3.toArray(new String[var4]));
      }
   }

   protected void registerFontDir(String var1) {
      if (FontUtilities.debugFonts()) {
         FontUtilities.getLogger().info("ParseFontDir " + var1);
      }

      File var2 = new File(var1 + File.separator + "fonts.dir");
      FileReader var3 = null;

      try {
         if (var2.canRead()) {
            var3 = new FileReader(var2);
            BufferedReader var4 = new BufferedReader(var3, 8192);
            StreamTokenizer var5 = new StreamTokenizer(var4);
            var5.eolIsSignificant(true);
            int var6 = var5.nextToken();
            if (var6 == -2) {
               int var7 = (int)var5.nval;
               var6 = var5.nextToken();
               if (var6 == 10) {
                  var5.resetSyntax();
                  var5.wordChars(32, 127);
                  var5.wordChars(160, 255);
                  var5.whitespaceChars(0, 31);

                  for(int var8 = 0; var8 < var7; ++var8) {
                     var6 = var5.nextToken();
                     if (var6 == -1 || var6 != -3) {
                        break;
                     }

                     int var9 = var5.sval.indexOf(32);
                     if (var9 <= 0) {
                        ++var7;
                        var6 = var5.nextToken();
                        if (var6 != 10) {
                           break;
                        }
                     } else if (var5.sval.charAt(0) == '!') {
                        ++var7;
                        var6 = var5.nextToken();
                        if (var6 != 10) {
                           break;
                        }
                     } else {
                        String var10 = var5.sval.substring(0, var9);
                        int var11 = var10.lastIndexOf(58);
                        if (var11 > 0) {
                           if (var11 + 1 >= var10.length()) {
                              continue;
                           }

                           var10 = var10.substring(var11 + 1);
                        }

                        String var12 = var5.sval.substring(var9 + 1);
                        String var13 = this.specificFontIDForName(var12);
                        String var14 = (String)fontNameMap.get(var13);
                        PlatformLogger var15;
                        if (FontUtilities.debugFonts()) {
                           var15 = FontUtilities.getLogger();
                           var15.info("file=" + var10 + " xlfd=" + var12);
                           var15.info("fontID=" + var13 + " sVal=" + var14);
                        }

                        var15 = null;

                        String var29;
                        try {
                           File var16 = new File(var1, var10);
                           if (xFontDirsMap == null) {
                              xFontDirsMap = new HashMap();
                           }

                           xFontDirsMap.put(var13, var1);
                           var29 = var16.getCanonicalPath();
                        } catch (IOException var26) {
                           var29 = var1 + File.separator + var10;
                        }

                        Vector var30 = (Vector)xlfdMap.get(var29);
                        if (FontUtilities.debugFonts()) {
                           FontUtilities.getLogger().info("fullPath=" + var29 + " xVal=" + var30);
                        }

                        if ((var30 == null || !var30.contains(var12)) && var14 == null || !var14.startsWith("/")) {
                           if (FontUtilities.debugFonts()) {
                              FontUtilities.getLogger().info("Map fontID:" + var13 + "to file:" + var29);
                           }

                           fontNameMap.put(var13, var29);
                           if (var30 == null) {
                              var30 = new Vector();
                              xlfdMap.put(var29, var30);
                           }

                           var30.add(var12);
                        }

                        var6 = var5.nextToken();
                        if (var6 != 10) {
                           break;
                        }
                     }
                  }
               }
            }

            var3.close();
         }
      } catch (IOException var27) {
      } finally {
         if (var3 != null) {
            try {
               var3.close();
            } catch (IOException var25) {
            }
         }

      }

   }

   public void loadFonts() {
      super.loadFonts();
      xFontDirsMap = null;
      xlfdMap = new HashMap(1);
      fontNameMap = new HashMap(1);
   }

   private String getObliqueLucidaFontID(String var1) {
      return !var1.startsWith("-lucidasans-medium-i-normal") && !var1.startsWith("-lucidasans-bold-i-normal") && !var1.startsWith("-lucidatypewriter-medium-i-normal") && !var1.startsWith("-lucidatypewriter-bold-i-normal") ? null : var1.substring(0, var1.indexOf("-i-"));
   }

   private static String getX11FontName(String var0) {
      String var1 = var0.replaceAll("%d", "*");
      return NativeFont.fontExists(var1) ? var1 : null;
   }

   private void initObliqueLucidaFontMap() {
      this.oblmap = new HashMap();
      this.oblmap.put("-lucidasans-medium", jreLibDirName + "/fonts/LucidaSansRegular.ttf");
      this.oblmap.put("-lucidasans-bold", jreLibDirName + "/fonts/LucidaSansDemiBold.ttf");
      this.oblmap.put("-lucidatypewriter-medium", jreLibDirName + "/fonts/LucidaTypewriterRegular.ttf");
      this.oblmap.put("-lucidatypewriter-bold", jreLibDirName + "/fonts/LucidaTypewriterBold.ttf");
   }

   private boolean isHeadless() {
      GraphicsEnvironment var1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      return GraphicsEnvironment.isHeadless();
   }

   private String specificFontIDForName(String var1) {
      int[] var2 = new int[14];
      int var3 = 1;
      int var4 = 1;

      while(var4 != -1 && var3 < 14) {
         var4 = var1.indexOf(45, var4);
         if (var4 != -1) {
            var2[var3++] = var4++;
         }
      }

      if (var3 != 14) {
         if (FontUtilities.debugFonts()) {
            FontUtilities.getLogger().severe("Font Configuration Font ID is malformed:" + var1);
         }

         return var1;
      } else {
         StringBuffer var5 = new StringBuffer(var1.substring(var2[1], var2[5]));
         var5.append(var1.substring(var2[12]));
         String var6 = var5.toString().toLowerCase(Locale.ENGLISH);
         return var6;
      }
   }

   private String switchFontIDForName(String var1) {
      int[] var2 = new int[14];
      int var3 = 1;
      int var4 = 1;

      while(var4 != -1 && var3 < 14) {
         var4 = var1.indexOf(45, var4);
         if (var4 != -1) {
            var2[var3++] = var4++;
         }
      }

      if (var3 != 14) {
         if (FontUtilities.debugFonts()) {
            FontUtilities.getLogger().severe("Font Configuration Font ID is malformed:" + var1);
         }

         return var1;
      } else {
         String var5 = var1.substring(var2[3] + 1, var2[4]);
         String var6 = var1.substring(var2[1] + 1, var2[2]);
         String var7 = var1.substring(var2[12] + 1, var2[13]);
         String var8 = var1.substring(var2[13] + 1);
         if (var5.equals("i")) {
            var5 = "o";
         } else if (var5.equals("o")) {
            var5 = "i";
         }

         if (var6.equals("itc zapfdingbats") && var7.equals("sun") && var8.equals("fontspecific")) {
            var7 = "adobe";
         }

         StringBuffer var9 = new StringBuffer(var1.substring(var2[1], var2[3] + 1));
         var9.append(var5);
         var9.append(var1.substring(var2[4], var2[5] + 1));
         var9.append(var7);
         var9.append(var1.substring(var2[13]));
         String var10 = var9.toString().toLowerCase(Locale.ENGLISH);
         return var10;
      }
   }

   public String getFileNameFromXLFD(String var1) {
      String var2 = null;
      String var3 = this.specificFontIDForName(var1);
      if (var3 != null) {
         var2 = (String)fontNameMap.get(var3);
         if (var2 == null) {
            var3 = this.switchFontIDForName(var1);
            var2 = (String)fontNameMap.get(var3);
         }

         if (var2 == null) {
            var2 = this.getDefaultFontFile();
         }
      }

      return var2;
   }

   protected void registerFontDirs(String var1) {
      StringTokenizer var2 = new StringTokenizer(var1, File.pathSeparator);

      try {
         while(var2.hasMoreTokens()) {
            String var3 = var2.nextToken();
            if (var3 != null && !registeredDirs.containsKey(var3)) {
               registeredDirs.put(var3, (Object)null);
               this.registerFontDir(var3);
            }
         }
      } catch (NoSuchElementException var4) {
      }

   }

   protected void addFontToPlatformFontPath(String var1) {
      this.getPlatformFontPathFromFontConfig();
      if (xFontDirsMap != null) {
         String var2 = this.specificFontIDForName(var1);
         String var3 = (String)xFontDirsMap.get(var2);
         if (var3 != null) {
            fontConfigDirs.add(var3);
         }
      }

   }

   private void getPlatformFontPathFromFontConfig() {
      if (fontConfigDirs == null) {
         fontConfigDirs = this.getFontConfiguration().getAWTFontPathSet();
         if (FontUtilities.debugFonts() && fontConfigDirs != null) {
            String[] var1 = (String[])fontConfigDirs.toArray(new String[0]);

            for(int var2 = 0; var2 < var1.length; ++var2) {
               FontUtilities.getLogger().info("awtfontpath : " + var1[var2]);
            }
         }
      }

   }

   protected void registerPlatformFontsUsedByFontConfiguration() {
      this.getPlatformFontPathFromFontConfig();
      if (fontConfigDirs != null) {
         if (FontUtilities.isLinux) {
            fontConfigDirs.add(jreLibDirName + File.separator + "oblique-fonts");
         }

         fontdirs = (String[])((String[])fontConfigDirs.toArray(new String[0]));
      }
   }

   protected FontConfiguration createFontConfiguration() {
      MFontConfiguration var1 = new MFontConfiguration(this);
      if (FontUtilities.isOpenSolaris || FontUtilities.isLinux && (!var1.foundOsSpecificFile() || !var1.fontFilesArePresent()) || FontUtilities.isSolaris && !var1.fontFilesArePresent()) {
         FcFontConfiguration var2 = new FcFontConfiguration(this);
         if (var2.init()) {
            return var2;
         }
      }

      var1.init();
      return var1;
   }

   public FontConfiguration createFontConfiguration(boolean var1, boolean var2) {
      return new MFontConfiguration(this, var1, var2);
   }

   protected synchronized String getFontPath(boolean var1) {
      this.isHeadless();
      return this.getFontPathNative(var1, true);
   }

   protected FontUIResource getFontConfigFUIR(String var1, int var2, int var3) {
      CompositeFont var4 = this.getFontConfigManager().getFontConfigFont(var1, var2);
      if (var4 == null) {
         return new FontUIResource(var1, var2, var3);
      } else {
         FontUIResource var5 = new FontUIResource(var4.getFamilyName((Locale)null), var2, var3);
         FontAccess.getFontAccess().setFont2D(var5, var4.handle);
         FontAccess.getFontAccess().setCreatedFont(var5);
         return var5;
      }
   }
}
