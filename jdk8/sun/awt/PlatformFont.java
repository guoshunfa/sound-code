package sun.awt;

import java.awt.peer.FontPeer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Locale;
import java.util.Vector;
import sun.font.SunFontManager;
import sun.java2d.FontSupport;

public abstract class PlatformFont implements FontPeer {
   protected FontDescriptor[] componentFonts;
   protected char defaultChar;
   protected FontConfiguration fontConfig;
   protected FontDescriptor defaultFont;
   protected String familyName;
   private Object[] fontCache;
   protected static int FONTCACHESIZE;
   protected static int FONTCACHEMASK;
   protected static String osVersion;

   public PlatformFont(String var1, int var2) {
      SunFontManager var3 = SunFontManager.getInstance();
      if (var3 instanceof FontSupport) {
         this.fontConfig = var3.getFontConfiguration();
      }

      if (this.fontConfig != null) {
         this.familyName = var1.toLowerCase(Locale.ENGLISH);
         if (!FontConfiguration.isLogicalFontFamilyName(this.familyName)) {
            this.familyName = this.fontConfig.getFallbackFamilyName(this.familyName, "sansserif");
         }

         this.componentFonts = this.fontConfig.getFontDescriptors(this.familyName, var2);
         char var4 = this.getMissingGlyphCharacter();
         this.defaultChar = '?';
         if (this.componentFonts.length > 0) {
            this.defaultFont = this.componentFonts[0];
         }

         for(int var5 = 0; var5 < this.componentFonts.length; ++var5) {
            if (!this.componentFonts[var5].isExcluded(var4) && this.componentFonts[var5].encoder.canEncode(var4)) {
               this.defaultFont = this.componentFonts[var5];
               this.defaultChar = var4;
               break;
            }
         }

      }
   }

   protected abstract char getMissingGlyphCharacter();

   public CharsetString[] makeMultiCharsetString(String var1) {
      return this.makeMultiCharsetString(var1.toCharArray(), 0, var1.length(), true);
   }

   public CharsetString[] makeMultiCharsetString(String var1, boolean var2) {
      return this.makeMultiCharsetString(var1.toCharArray(), 0, var1.length(), var2);
   }

   public CharsetString[] makeMultiCharsetString(char[] var1, int var2, int var3) {
      return this.makeMultiCharsetString(var1, var2, var3, true);
   }

   public CharsetString[] makeMultiCharsetString(char[] var1, int var2, int var3, boolean var4) {
      if (var3 < 1) {
         return new CharsetString[0];
      } else {
         Vector var5 = null;
         char[] var6 = new char[var3];
         char var7 = this.defaultChar;
         boolean var8 = false;
         FontDescriptor var9 = this.defaultFont;

         int var10;
         for(var10 = 0; var10 < this.componentFonts.length; ++var10) {
            if (!this.componentFonts[var10].isExcluded(var1[var2]) && this.componentFonts[var10].encoder.canEncode(var1[var2])) {
               var9 = this.componentFonts[var10];
               var7 = var1[var2];
               var8 = true;
               break;
            }
         }

         if (!var4 && !var8) {
            return null;
         } else {
            var6[0] = var7;
            var10 = 0;

            for(int var11 = 1; var11 < var3; ++var11) {
               char var12 = var1[var2 + var11];
               FontDescriptor var13 = this.defaultFont;
               var7 = this.defaultChar;
               var8 = false;

               for(int var14 = 0; var14 < this.componentFonts.length; ++var14) {
                  if (!this.componentFonts[var14].isExcluded(var12) && this.componentFonts[var14].encoder.canEncode(var12)) {
                     var13 = this.componentFonts[var14];
                     var7 = var12;
                     var8 = true;
                     break;
                  }
               }

               if (!var4 && !var8) {
                  return null;
               }

               var6[var11] = var7;
               if (var9 != var13) {
                  if (var5 == null) {
                     var5 = new Vector(3);
                  }

                  var5.addElement(new CharsetString(var6, var10, var11 - var10, var9));
                  var9 = var13;
                  var13 = this.defaultFont;
                  var10 = var11;
               }
            }

            CharsetString var16 = new CharsetString(var6, var10, var3 - var10, var9);
            CharsetString[] var15;
            if (var5 == null) {
               var15 = new CharsetString[]{var16};
            } else {
               var5.addElement(var16);
               var15 = new CharsetString[var5.size()];

               for(int var17 = 0; var17 < var5.size(); ++var17) {
                  var15[var17] = (CharsetString)var5.elementAt(var17);
               }
            }

            return var15;
         }
      }
   }

   public boolean mightHaveMultiFontMetrics() {
      return this.fontConfig != null;
   }

   public Object[] makeConvertedMultiFontString(String var1) {
      return this.makeConvertedMultiFontChars(var1.toCharArray(), 0, var1.length());
   }

   public Object[] makeConvertedMultiFontChars(char[] var1, int var2, int var3) {
      Object[] var4 = new Object[2];
      byte[] var6 = null;
      int var7 = var2;
      int var8 = 0;
      int var9 = 0;
      FontDescriptor var11 = null;
      FontDescriptor var12 = null;
      int var15 = var2 + var3;
      if (var2 >= 0 && var15 <= var1.length) {
         if (var2 >= var15) {
            return null;
         } else {
            for(; var7 < var15; ++var7) {
               char var13 = var1[var7];
               int var10 = var13 & FONTCACHEMASK;
               PlatformFont.PlatformFontCache var14 = (PlatformFont.PlatformFontCache)this.getFontCache()[var10];
               int var17;
               if (var14 == null || var14.uniChar != var13) {
                  var11 = this.defaultFont;
                  var13 = this.defaultChar;
                  char var16 = var1[var7];
                  var17 = this.componentFonts.length;

                  for(int var18 = 0; var18 < var17; ++var18) {
                     FontDescriptor var19 = this.componentFonts[var18];
                     var19.encoder.reset();
                     if (!var19.isExcluded(var16) && var19.encoder.canEncode(var16)) {
                        var11 = var19;
                        var13 = var16;
                        break;
                     }
                  }

                  try {
                     char[] var23 = new char[]{var13};
                     var14 = new PlatformFont.PlatformFontCache();
                     if (var11.useUnicode()) {
                        if (FontDescriptor.isLE) {
                           var14.bb.put((byte)(var23[0] & 255));
                           var14.bb.put((byte)(var23[0] >> 8));
                        } else {
                           var14.bb.put((byte)(var23[0] >> 8));
                           var14.bb.put((byte)(var23[0] & 255));
                        }
                     } else {
                        var11.encoder.encode(CharBuffer.wrap(var23), var14.bb, true);
                     }

                     var14.fontDescriptor = var11;
                     var14.uniChar = var1[var7];
                     this.getFontCache()[var10] = var14;
                  } catch (Exception var20) {
                     System.err.println((Object)var20);
                     var20.printStackTrace();
                     return null;
                  }
               }

               if (var12 != var14.fontDescriptor) {
                  if (var12 != null) {
                     var4[var9++] = var12;
                     var4[var9++] = var6;
                     if (var6 != null) {
                        var8 -= 4;
                        var6[0] = (byte)(var8 >> 24);
                        var6[1] = (byte)(var8 >> 16);
                        var6[2] = (byte)(var8 >> 8);
                        var6[3] = (byte)var8;
                     }

                     if (var9 >= var4.length) {
                        Object[] var21 = new Object[var4.length * 2];
                        System.arraycopy(var4, 0, var21, 0, var4.length);
                        var4 = var21;
                     }
                  }

                  if (var14.fontDescriptor.useUnicode()) {
                     var6 = new byte[(var15 - var7 + 1) * (int)var14.fontDescriptor.unicodeEncoder.maxBytesPerChar() + 4];
                  } else {
                     var6 = new byte[(var15 - var7 + 1) * (int)var14.fontDescriptor.encoder.maxBytesPerChar() + 4];
                  }

                  var8 = 4;
                  var12 = var14.fontDescriptor;
               }

               byte[] var22 = var14.bb.array();
               var17 = var14.bb.position();
               if (var17 == 1) {
                  var6[var8++] = var22[0];
               } else if (var17 == 2) {
                  var6[var8++] = var22[0];
                  var6[var8++] = var22[1];
               } else if (var17 == 3) {
                  var6[var8++] = var22[0];
                  var6[var8++] = var22[1];
                  var6[var8++] = var22[2];
               } else if (var17 == 4) {
                  var6[var8++] = var22[0];
                  var6[var8++] = var22[1];
                  var6[var8++] = var22[2];
                  var6[var8++] = var22[3];
               }
            }

            var4[var9++] = var12;
            var4[var9] = var6;
            if (var6 != null) {
               var8 -= 4;
               var6[0] = (byte)(var8 >> 24);
               var6[1] = (byte)(var8 >> 16);
               var6[2] = (byte)(var8 >> 8);
               var6[3] = (byte)var8;
            }

            return var4;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   protected final Object[] getFontCache() {
      if (this.fontCache == null) {
         this.fontCache = new Object[FONTCACHESIZE];
      }

      return this.fontCache;
   }

   private static native void initIDs();

   static {
      NativeLibLoader.loadLibraries();
      initIDs();
      FONTCACHESIZE = 256;
      FONTCACHEMASK = FONTCACHESIZE - 1;
   }

   class PlatformFontCache {
      char uniChar;
      FontDescriptor fontDescriptor;
      ByteBuffer bb = ByteBuffer.allocate(4);
   }
}
