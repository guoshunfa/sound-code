package sun.font;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;
import sun.awt.FcFontManager;
import sun.awt.FontConfiguration;
import sun.awt.FontDescriptor;
import sun.awt.SunToolkit;
import sun.util.logging.PlatformLogger;

public class FcFontConfiguration extends FontConfiguration {
   private static final String fileVersion = "1";
   private String fcInfoFileName = null;
   private FontConfigManager.FcCompFont[] fcCompFonts = null;

   public FcFontConfiguration(SunFontManager var1) {
      super(var1);
      this.init();
   }

   public FcFontConfiguration(SunFontManager var1, boolean var2, boolean var3) {
      super(var1, var2, var3);
      this.init();
   }

   public synchronized boolean init() {
      if (this.fcCompFonts != null) {
         return true;
      } else {
         this.setFontConfiguration();
         this.readFcInfo();
         FcFontManager var1 = (FcFontManager)this.fontManager;
         FontConfigManager var2 = var1.getFontConfigManager();
         if (this.fcCompFonts == null) {
            this.fcCompFonts = var2.loadFontConfig();
            if (this.fcCompFonts != null) {
               try {
                  this.writeFcInfo();
               } catch (Exception var5) {
                  if (FontUtilities.debugFonts()) {
                     warning("Exception writing fcInfo " + var5);
                  }
               }
            } else if (FontUtilities.debugFonts()) {
               warning("Failed to get info from libfontconfig");
            }
         } else {
            var2.populateFontConfig(this.fcCompFonts);
         }

         if (this.fcCompFonts == null) {
            return false;
         } else {
            String var3 = System.getProperty("java.home");
            if (var3 == null) {
               throw new Error("java.home property not set");
            } else {
               String var4 = var3 + File.separator + "lib";
               this.getInstalledFallbackFonts(var4);
               return true;
            }
         }
      }
   }

   public String getFallbackFamilyName(String var1, String var2) {
      String var3 = this.getCompatibilityFamilyName(var1);
      return var3 != null ? var3 : var2;
   }

   protected String getFaceNameFromComponentFontName(String var1) {
      return null;
   }

   protected String getFileNameFromComponentFontName(String var1) {
      return null;
   }

   public String getFileNameFromPlatformName(String var1) {
      return null;
   }

   protected Charset getDefaultFontCharset(String var1) {
      return Charset.forName("ISO8859_1");
   }

   protected String getEncoding(String var1, String var2) {
      return "default";
   }

   protected void initReorderMap() {
      this.reorderMap = new HashMap();
   }

   protected FontDescriptor[] buildFontDescriptors(int var1, int var2) {
      CompositeFontDescriptor[] var3 = this.get2DCompositeFontInfo();
      int var4 = var1 * 4 + var2;
      String[] var5 = var3[var4].getComponentFaceNames();
      FontDescriptor[] var6 = new FontDescriptor[var5.length];

      for(int var7 = 0; var7 < var5.length; ++var7) {
         var6[var7] = new FontDescriptor(var5[var7], StandardCharsets.ISO_8859_1.newEncoder(), new int[0]);
      }

      return var6;
   }

   public int getNumberCoreFonts() {
      return 1;
   }

   public String[] getPlatformFontNames() {
      HashSet var1 = new HashSet();
      FcFontManager var2 = (FcFontManager)this.fontManager;
      FontConfigManager var3 = var2.getFontConfigManager();
      FontConfigManager.FcCompFont[] var4 = var3.loadFontConfig();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         for(int var6 = 0; var6 < var4[var5].allFonts.length; ++var6) {
            var1.add(var4[var5].allFonts[var6].fontFile);
         }
      }

      return (String[])var1.toArray(new String[0]);
   }

   public String getExtraFontPath() {
      return null;
   }

   public boolean needToSearchForFile(String var1) {
      return false;
   }

   private FontConfigManager.FontConfigFont[] getFcFontList(FontConfigManager.FcCompFont[] var1, String var2, int var3) {
      if (var2.equals("dialog")) {
         var2 = "sansserif";
      } else if (var2.equals("dialoginput")) {
         var2 = "monospaced";
      }

      for(int var4 = 0; var4 < var1.length; ++var4) {
         if (var2.equals(var1[var4].jdkName) && var3 == var1[var4].style) {
            return var1[var4].allFonts;
         }
      }

      return var1[0].allFonts;
   }

   public CompositeFontDescriptor[] get2DCompositeFontInfo() {
      FcFontManager var1 = (FcFontManager)this.fontManager;
      FontConfigManager var2 = var1.getFontConfigManager();
      FontConfigManager.FcCompFont[] var3 = var2.loadFontConfig();
      CompositeFontDescriptor[] var4 = new CompositeFontDescriptor[20];

      for(int var5 = 0; var5 < 5; ++var5) {
         String var6 = publicFontNames[var5];

         for(int var7 = 0; var7 < 4; ++var7) {
            String var8 = var6 + "." + styleNames[var7];
            FontConfigManager.FontConfigFont[] var9 = this.getFcFontList(var3, fontNames[var5], var7);
            int var10 = var9.length;
            if (installedFallbackFontFiles != null) {
               var10 += installedFallbackFontFiles.length;
            }

            String[] var11 = new String[var10];
            String[] var12 = new String[var10];

            for(int var13 = 0; var13 < var9.length; ++var13) {
               var11[var13] = var9[var13].fontFile;
               var12[var13] = var9[var13].familyName;
            }

            if (installedFallbackFontFiles != null) {
               System.arraycopy(installedFallbackFontFiles, 0, var11, var9.length, installedFallbackFontFiles.length);
            }

            var4[var5 * 4 + var7] = new CompositeFontDescriptor(var8, 1, var12, var11, (int[])null, (int[])null);
         }
      }

      return var4;
   }

   private String getVersionString(File var1) {
      try {
         Scanner var2 = new Scanner(var1);
         return var2.findInLine("(\\d)+((\\.)(\\d)+)*");
      } catch (Exception var3) {
         return null;
      }
   }

   protected void setOsNameAndVersion() {
      super.setOsNameAndVersion();
      if (osName.equals("Linux")) {
         try {
            File var1;
            if ((var1 = new File("/etc/lsb-release")).canRead()) {
               Properties var2 = new Properties();
               var2.load((InputStream)(new FileInputStream(var1)));
               osName = var2.getProperty("DISTRIB_ID");
               osVersion = var2.getProperty("DISTRIB_RELEASE");
            } else if ((var1 = new File("/etc/redhat-release")).canRead()) {
               osName = "RedHat";
               osVersion = this.getVersionString(var1);
            } else if ((var1 = new File("/etc/SuSE-release")).canRead()) {
               osName = "SuSE";
               osVersion = this.getVersionString(var1);
            } else if ((var1 = new File("/etc/turbolinux-release")).canRead()) {
               osName = "Turbo";
               osVersion = this.getVersionString(var1);
            } else if ((var1 = new File("/etc/fedora-release")).canRead()) {
               osName = "Fedora";
               osVersion = this.getVersionString(var1);
            }
         } catch (Exception var3) {
            if (FontUtilities.debugFonts()) {
               warning("Exception identifying Linux distro.");
            }
         }

      }
   }

   private File getFcInfoFile() {
      if (this.fcInfoFileName == null) {
         String var1;
         try {
            var1 = InetAddress.getLocalHost().getHostName();
         } catch (UnknownHostException var8) {
            var1 = "localhost";
         }

         String var2 = System.getProperty("user.home");
         String var3 = System.getProperty("java.version");
         String var4 = File.separator;
         String var5 = var2 + var4 + ".java" + var4 + "fonts" + var4 + var3;
         String var6 = SunToolkit.getStartupLocale().getLanguage();
         String var7 = "fcinfo-1-" + var1 + "-" + osName + "-" + osVersion + "-" + var6 + ".properties";
         this.fcInfoFileName = var5 + var4 + var7;
      }

      return new File(this.fcInfoFileName);
   }

   private void writeFcInfo() {
      Properties var1 = new Properties();
      var1.setProperty("version", "1");
      FcFontManager var2 = (FcFontManager)this.fontManager;
      FontConfigManager var3 = var2.getFontConfigManager();
      FontConfigManager.FontConfigInfo var4 = var3.getFontConfigInfo();
      var1.setProperty("fcversion", Integer.toString(var4.fcVersion));
      int var5;
      if (var4.cacheDirs != null) {
         for(var5 = 0; var5 < var4.cacheDirs.length; ++var5) {
            if (var4.cacheDirs[var5] != null) {
               var1.setProperty("cachedir." + var5, var4.cacheDirs[var5]);
            }
         }
      }

      for(var5 = 0; var5 < this.fcCompFonts.length; ++var5) {
         FontConfigManager.FcCompFont var6 = this.fcCompFonts[var5];
         String var7 = var6.jdkName + "." + var6.style;
         var1.setProperty(var7 + ".length", Integer.toString(var6.allFonts.length));

         for(int var8 = 0; var8 < var6.allFonts.length; ++var8) {
            var1.setProperty(var7 + "." + var8 + ".family", var6.allFonts[var8].familyName);
            var1.setProperty(var7 + "." + var8 + ".file", var6.allFonts[var8].fontFile);
         }
      }

      try {
         File var11 = this.getFcInfoFile();
         File var12 = var11.getParentFile();
         var12.mkdirs();
         File var13 = Files.createTempFile(var12.toPath(), "fcinfo", (String)null).toFile();
         FileOutputStream var14 = new FileOutputStream(var13);
         var1.store((OutputStream)var14, "JDK Font Configuration Generated File: *Do Not Edit*");
         var14.close();
         boolean var9 = var13.renameTo(var11);
         if (!var9 && FontUtilities.debugFonts()) {
            System.out.println("rename failed");
            warning("Failed renaming file to " + this.getFcInfoFile());
         }
      } catch (Exception var10) {
         if (FontUtilities.debugFonts()) {
            warning("IOException writing to " + this.getFcInfoFile());
         }
      }

   }

   private void readFcInfo() {
      File var1 = this.getFcInfoFile();
      if (var1.exists()) {
         Properties var2 = new Properties();
         FcFontManager var3 = (FcFontManager)this.fontManager;
         FontConfigManager var4 = var3.getFontConfigManager();

         try {
            FileInputStream var5 = new FileInputStream(var1);
            var2.load((InputStream)var5);
            var5.close();
         } catch (IOException var26) {
            if (FontUtilities.debugFonts()) {
               warning("IOException reading from " + var1.toString());
            }

            return;
         }

         String var28 = (String)var2.get("version");
         if (var28 != null && var28.equals("1")) {
            String var6 = (String)var2.get("fcversion");
            if (var6 != null) {
               try {
                  int var7 = Integer.parseInt(var6);
                  if (var7 != 0 && var7 != FontConfigManager.getFontConfigVersion()) {
                     return;
                  }
               } catch (Exception var25) {
                  if (FontUtilities.debugFonts()) {
                     warning("Exception parsing version " + var6);
                  }

                  return;
               }
            }

            long var29 = var1.lastModified();

            for(int var9 = 0; var9 < 4; ++var9) {
               String var10 = (String)var2.get("cachedir." + var9);
               if (var10 == null) {
                  break;
               }

               File var11 = new File(var10);
               if (var11.exists() && var11.lastModified() > var29) {
                  return;
               }
            }

            String[] var30 = new String[]{"sansserif", "serif", "monospaced"};
            String[] var31 = new String[]{"sans", "serif", "monospace"};
            int var12 = var30.length;
            byte var13 = 4;
            FontConfigManager.FcCompFont[] var14 = new FontConfigManager.FcCompFont[var12 * var13];

            try {
               for(int var15 = 0; var15 < var12; ++var15) {
                  for(int var16 = 0; var16 < var13; ++var16) {
                     int var17 = var15 * var13 + var16;
                     var14[var17] = new FontConfigManager.FcCompFont();
                     String var18 = var30[var15] + "." + var16;
                     var14[var17].jdkName = var30[var15];
                     var14[var17].fcFamily = var31[var15];
                     var14[var17].style = var16;
                     String var19 = (String)var2.get(var18 + ".length");
                     int var20 = Integer.parseInt(var19);
                     if (var20 <= 0) {
                        return;
                     }

                     var14[var17].allFonts = new FontConfigManager.FontConfigFont[var20];

                     for(int var21 = 0; var21 < var20; ++var21) {
                        var14[var17].allFonts[var21] = new FontConfigManager.FontConfigFont();
                        String var22 = var18 + "." + var21 + ".family";
                        String var23 = (String)var2.get(var22);
                        var14[var17].allFonts[var21].familyName = var23;
                        var22 = var18 + "." + var21 + ".file";
                        String var24 = (String)var2.get(var22);
                        if (var24 == null) {
                           return;
                        }

                        var14[var17].allFonts[var21].fontFile = var24;
                     }

                     var14[var17].firstFont = var14[var17].allFonts[0];
                  }
               }

               this.fcCompFonts = var14;
            } catch (Throwable var27) {
               if (FontUtilities.debugFonts()) {
                  warning(var27.toString());
               }
            }

         }
      }
   }

   private static void warning(String var0) {
      PlatformLogger var1 = PlatformLogger.getLogger("sun.awt.FontConfiguration");
      var1.warning(var0);
   }
}
