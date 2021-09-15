package sun.font;

import java.awt.FontFormatException;
import java.awt.Toolkit;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.plaf.FontUIResource;
import sun.awt.FontConfiguration;
import sun.awt.HeadlessToolkit;
import sun.lwawt.macosx.LWCToolkit;
import sun.misc.ThreadGroupUtils;

public final class CFontManager extends SunFontManager {
   private FontConfigManager fcManager = null;
   private static Hashtable<String, Font2D> genericFonts = new Hashtable();
   Object waitForFontsToBeLoaded = new Object();
   private boolean loadedAllFonts = false;

   protected FontConfiguration createFontConfiguration() {
      CFontConfiguration var1 = new CFontConfiguration(this);
      var1.init();
      return var1;
   }

   public FontConfiguration createFontConfiguration(boolean var1, boolean var2) {
      return new CFontConfiguration(this, var1, var2);
   }

   protected String[] getDefaultPlatformFont() {
      return new String[]{"Lucida Grande", "/System/Library/Fonts/LucidaGrande.ttc"};
   }

   public static Font2D[] getGenericFonts() {
      return (Font2D[])((Font2D[])genericFonts.values().toArray(new Font2D[0]));
   }

   public Font2D registerGenericFont(Font2D var1) {
      return this.registerGenericFont(var1, false);
   }

   public Font2D registerGenericFont(Font2D var1, boolean var2) {
      byte var3 = 4;
      String var4 = var1.fullName;
      String var5 = var1.familyName;
      if (var4 != null && !"".equals(var4)) {
         if (!var2 && genericFonts.containsKey(var4)) {
            return (Font2D)genericFonts.get(var4);
         } else {
            if (FontUtilities.debugFonts()) {
               FontUtilities.getLogger().info("Add to Family " + var5 + ", Font " + var4 + " rank=" + var3);
            }

            FontFamily var6 = FontFamily.getFamily(var5);
            if (var6 == null) {
               var6 = new FontFamily(var5, false, var3);
               var6.setFont(var1, var1.style);
            } else if (var6.getRank() >= var3) {
               var6.setFont(var1, var1.style);
            }

            if (!var2) {
               genericFonts.put(var4, var1);
               this.fullNameToFont.put(var4.toLowerCase(Locale.ENGLISH), var1);
            }

            return var1;
         }
      } else {
         return null;
      }
   }

   public Font2D[] getRegisteredFonts() {
      Font2D[] var1 = super.getRegisteredFonts();
      Font2D[] var2 = getGenericFonts();
      Font2D[] var3 = new Font2D[var1.length + var2.length];
      System.arraycopy(var1, 0, var3, 0, var1.length);
      System.arraycopy(var2, 0, var3, var1.length, var2.length);
      return var3;
   }

   protected void addNativeFontFamilyNames(TreeMap<String, String> var1, Locale var2) {
      Font2D[] var3 = getGenericFonts();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (!(var3[var4] instanceof NativeFont)) {
            String var5 = var3[var4].getFamilyName(var2);
            var1.put(var5.toLowerCase(var2), var5);
         }
      }

   }

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
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
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
         FileFont.setFileToRemove(var6, var1, var4);
         Class var9 = FontManager.class;
         synchronized(FontManager.class) {
            if (this.tmpFontFiles == null) {
               this.tmpFontFiles = new Vector();
            }

            this.tmpFontFiles.add(var1);
            if (this.fileCloser == null) {
               Runnable var10 = new Runnable() {
                  public void run() {
                     AccessController.doPrivileged(new PrivilegedAction<Object>() {
                        public Object run() {
                           for(int var1 = 0; var1 < 20; ++var1) {
                              if (CFontManager.this.fontFileCache[var1] != null) {
                                 try {
                                    CFontManager.this.fontFileCache[var1].close();
                                 } catch (Exception var5) {
                                 }
                              }
                           }

                           if (CFontManager.this.tmpFontFiles != null) {
                              File[] var6 = new File[CFontManager.this.tmpFontFiles.size()];
                              var6 = (File[])CFontManager.this.tmpFontFiles.toArray(var6);

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

   protected void registerFontsInDir(String var1, boolean var2, int var3, boolean var4, boolean var5) {
      this.loadNativeDirFonts(var1);
      super.registerFontsInDir(var1, var2, var3, var4, var5);
   }

   private native void loadNativeDirFonts(String var1);

   private native void loadNativeFonts();

   void registerFont(String var1, String var2) {
      CFont var3 = new CFont(var1, var2);
      this.registerGenericFont(var3);
   }

   void registerItalicDerived() {
      FontFamily[] var1 = FontFamily.getAllFontFamilies();

      for(int var2 = 0; var2 < var1.length; ++var2) {
         FontFamily var3 = var1[var2];
         Font2D var4 = var3.getFont(0);
         if (var4 == null || var4 instanceof CFont) {
            Font2D var5 = var3.getFont(1);
            if (var5 == null || var5 instanceof CFont) {
               Font2D var6 = var3.getFont(2);
               if (var6 == null || var6 instanceof CFont) {
                  Font2D var7 = var3.getFont(3);
                  if (var7 == null || var7 instanceof CFont) {
                     CFont var8 = (CFont)var4;
                     CFont var9 = (CFont)var5;
                     CFont var10 = (CFont)var6;
                     CFont var11 = (CFont)var7;
                     if (var9 == null) {
                        var9 = var8;
                     }

                     if ((var8 != null || var9 != null) && (var10 == null || var11 == null)) {
                        if (var8 != null && var10 == null) {
                           this.registerGenericFont(var8.createItalicVariant(), true);
                        }

                        if (var9 != null && var11 == null) {
                           this.registerGenericFont(var9.createItalicVariant(), true);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public void loadFonts() {
      synchronized(this.waitForFontsToBeLoaded) {
         super.loadFonts();
         AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               if (!CFontManager.this.loadedAllFonts) {
                  CFontManager.this.loadNativeFonts();
                  CFontManager.this.registerItalicDerived();
                  CFontManager.this.loadedAllFonts = true;
               }

               return null;
            }
         });
         String var2 = "Lucida Grande";
         String var3 = "Lucida Sans";
         this.setupLogicalFonts("Dialog", var2, var3);
         this.setupLogicalFonts("Serif", "Times", "Lucida Bright");
         this.setupLogicalFonts("SansSerif", var2, var3);
         this.setupLogicalFonts("Monospaced", "Menlo", "Lucida Sans Typewriter");
         this.setupLogicalFonts("DialogInput", var2, var3);
      }
   }

   protected void setupLogicalFonts(String var1, String var2, String var3) {
      FontFamily var4 = this.getFontFamilyWithExtraTry(var1, var2, var3);
      this.cloneStyledFont(var4, var1, 0);
      this.cloneStyledFont(var4, var1, 1);
      this.cloneStyledFont(var4, var1, 2);
      this.cloneStyledFont(var4, var1, 3);
   }

   protected FontFamily getFontFamilyWithExtraTry(String var1, String var2, String var3) {
      FontFamily var4 = this.getFontFamily(var2, var3);
      if (var4 != null) {
         return var4;
      } else {
         super.loadFonts();
         var4 = this.getFontFamily(var2, var3);
         if (var4 != null) {
            return var4;
         } else {
            System.err.println("Warning: the fonts \"" + var2 + "\" and \"" + var3 + "\" are not available for the Java logical font \"" + var1 + "\", which may have unexpected appearance or behavior. Re-enable the \"" + var2 + "\" font to remove this warning.");
            return null;
         }
      }
   }

   protected FontFamily getFontFamily(String var1, String var2) {
      FontFamily var3 = FontFamily.getFamily(var1);
      if (var3 != null) {
         return var3;
      } else {
         var3 = FontFamily.getFamily(var2);
         if (var3 != null) {
            System.err.println("Warning: the font \"" + var1 + "\" is not available, so \"" + var2 + "\" has been substituted, but may have unexpected appearance or behavor. Re-enable the \"" + var1 + "\" font to remove this warning.");
            return var3;
         } else {
            return null;
         }
      }
   }

   protected boolean cloneStyledFont(FontFamily var1, String var2, int var3) {
      if (var1 == null) {
         return false;
      } else {
         Font2D var4 = var1.getFontWithExactStyleMatch(var3);
         if (var4 != null && var4 instanceof CFont) {
            CFont var5 = new CFont((CFont)var4, var2);
            this.registerGenericFont(var5, true);
            return true;
         } else {
            return false;
         }
      }
   }

   public String getFontPath(boolean var1) {
      Toolkit var2 = Toolkit.getDefaultToolkit();
      if (var2 instanceof HeadlessToolkit) {
         var2 = ((HeadlessToolkit)var2).getUnderlyingToolkit();
      }

      return var2 instanceof LWCToolkit ? "" : "/Library/Fonts";
   }

   protected FontUIResource getFontConfigFUIR(String var1, int var2, int var3) {
      String var4 = FontUtilities.mapFcName(var1);
      if (var4 == null) {
         var4 = "sansserif";
      }

      return new FontUIResource(var4, var2, var3);
   }

   protected void populateFontFileNameMap(HashMap<String, String> var1, HashMap<String, String> var2, HashMap<String, ArrayList<String>> var3, Locale var4) {
   }
}
