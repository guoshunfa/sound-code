package sun.font;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FontFamily {
   private static ConcurrentHashMap<String, FontFamily> familyNameMap = new ConcurrentHashMap();
   private static HashMap<String, FontFamily> allLocaleNames;
   protected String familyName;
   protected Font2D plain;
   protected Font2D bold;
   protected Font2D italic;
   protected Font2D bolditalic;
   protected boolean logicalFont = false;
   protected int familyRank;
   private int familyWidth = 0;

   public static FontFamily getFamily(String var0) {
      return (FontFamily)familyNameMap.get(var0.toLowerCase(Locale.ENGLISH));
   }

   public static String[] getAllFamilyNames() {
      return null;
   }

   static void remove(Font2D var0) {
      String var1 = var0.getFamilyName(Locale.ENGLISH);
      FontFamily var2 = getFamily(var1);
      if (var2 != null) {
         if (var2.plain == var0) {
            var2.plain = null;
         }

         if (var2.bold == var0) {
            var2.bold = null;
         }

         if (var2.italic == var0) {
            var2.italic = null;
         }

         if (var2.bolditalic == var0) {
            var2.bolditalic = null;
         }

         if (var2.plain == null && var2.bold == null && var2.plain == null && var2.bold == null) {
            familyNameMap.remove(var1);
         }

      }
   }

   public FontFamily(String var1, boolean var2, int var3) {
      this.logicalFont = var2;
      this.familyName = var1;
      this.familyRank = var3;
      familyNameMap.put(var1.toLowerCase(Locale.ENGLISH), this);
   }

   FontFamily(String var1) {
      this.logicalFont = false;
      this.familyName = var1;
      this.familyRank = 4;
   }

   public String getFamilyName() {
      return this.familyName;
   }

   public int getRank() {
      return this.familyRank;
   }

   private boolean isFromSameSource(Font2D var1) {
      if (!(var1 instanceof FileFont)) {
         return false;
      } else {
         FileFont var2 = null;
         if (this.plain instanceof FileFont) {
            var2 = (FileFont)this.plain;
         } else if (this.bold instanceof FileFont) {
            var2 = (FileFont)this.bold;
         } else if (this.italic instanceof FileFont) {
            var2 = (FileFont)this.italic;
         } else if (this.bolditalic instanceof FileFont) {
            var2 = (FileFont)this.bolditalic;
         }

         if (var2 == null) {
            return false;
         } else {
            File var3 = (new File(var2.platName)).getParentFile();
            FileFont var4 = (FileFont)var1;
            File var5 = (new File(var4.platName)).getParentFile();
            if (var3 != null) {
               try {
                  var3 = var3.getCanonicalFile();
               } catch (IOException var8) {
               }
            }

            if (var5 != null) {
               try {
                  var5 = var5.getCanonicalFile();
               } catch (IOException var7) {
               }
            }

            return Objects.equals(var5, var3);
         }
      }
   }

   private boolean preferredWidth(Font2D var1) {
      int var2 = var1.getWidth();
      if (this.familyWidth == 0) {
         this.familyWidth = var2;
         return true;
      } else if (var2 == this.familyWidth) {
         return true;
      } else if (Math.abs(5 - var2) < Math.abs(5 - this.familyWidth)) {
         if (FontUtilities.debugFonts()) {
            FontUtilities.getLogger().info("Found more preferred width. New width = " + var2 + " Old width = " + this.familyWidth + " in font " + var1 + " nulling out fonts plain: " + this.plain + " bold: " + this.bold + " italic: " + this.italic + " bolditalic: " + this.bolditalic);
         }

         this.familyWidth = var2;
         this.plain = this.bold = this.italic = this.bolditalic = null;
         return true;
      } else {
         if (FontUtilities.debugFonts()) {
            FontUtilities.getLogger().info("Family rejecting font " + var1 + " of less preferred width " + var2);
         }

         return false;
      }
   }

   private boolean closerWeight(Font2D var1, Font2D var2, int var3) {
      if (this.familyWidth != var2.getWidth()) {
         return false;
      } else if (var1 == null) {
         return true;
      } else {
         if (FontUtilities.debugFonts()) {
            FontUtilities.getLogger().info("New weight for style " + var3 + ". Curr.font=" + var1 + " New font=" + var2 + " Curr.weight=" + var1.getWeight() + " New weight=" + var2.getWeight());
         }

         int var4 = var2.getWeight();
         switch(var3) {
         case 0:
         case 2:
            return var4 <= 400 && var4 > var1.getWeight();
         case 1:
         case 3:
            return Math.abs(var4 - 700) < Math.abs(var1.getWeight() - 700);
         default:
            return false;
         }
      }
   }

   public void setFont(Font2D var1, int var2) {
      if (FontUtilities.isLogging()) {
         String var3;
         if (var1 instanceof CompositeFont) {
            var3 = "Request to add " + var1.getFamilyName((Locale)null) + " with style " + var2 + " to family " + this.familyName;
         } else {
            var3 = "Request to add " + var1 + " with style " + var2 + " to family " + this;
         }

         FontUtilities.getLogger().info(var3);
      }

      if (var1.getRank() > this.familyRank && !this.isFromSameSource(var1)) {
         if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().warning("Rejecting adding " + var1 + " of lower rank " + var1.getRank() + " to family " + this + " of rank " + this.familyRank);
         }

      } else {
         switch(var2) {
         case 0:
            if (this.preferredWidth(var1) && this.closerWeight(this.plain, var1, var2)) {
               this.plain = var1;
            }
            break;
         case 1:
            if (this.preferredWidth(var1) && this.closerWeight(this.bold, var1, var2)) {
               this.bold = var1;
            }
            break;
         case 2:
            if (this.preferredWidth(var1) && this.closerWeight(this.italic, var1, var2)) {
               this.italic = var1;
            }
            break;
         case 3:
            if (this.preferredWidth(var1) && this.closerWeight(this.bolditalic, var1, var2)) {
               this.bolditalic = var1;
            }
         }

      }
   }

   public Font2D getFontWithExactStyleMatch(int var1) {
      switch(var1) {
      case 0:
         return this.plain;
      case 1:
         return this.bold;
      case 2:
         return this.italic;
      case 3:
         return this.bolditalic;
      default:
         return null;
      }
   }

   public Font2D getFont(int var1) {
      switch(var1) {
      case 0:
         return this.plain;
      case 1:
         if (this.bold != null) {
            return this.bold;
         } else {
            if (this.plain != null && this.plain.canDoStyle(var1)) {
               return this.plain;
            }

            return null;
         }
      case 2:
         if (this.italic != null) {
            return this.italic;
         } else {
            if (this.plain != null && this.plain.canDoStyle(var1)) {
               return this.plain;
            }

            return null;
         }
      case 3:
         if (this.bolditalic != null) {
            return this.bolditalic;
         } else if (this.bold != null && this.bold.canDoStyle(var1)) {
            return this.bold;
         } else if (this.italic != null && this.italic.canDoStyle(var1)) {
            return this.italic;
         } else {
            if (this.plain != null && this.plain.canDoStyle(var1)) {
               return this.plain;
            }

            return null;
         }
      default:
         return null;
      }
   }

   Font2D getClosestStyle(int var1) {
      switch(var1) {
      case 0:
         if (this.bold != null) {
            return this.bold;
         } else {
            if (this.italic != null) {
               return this.italic;
            }

            return this.bolditalic;
         }
      case 1:
         if (this.plain != null) {
            return this.plain;
         } else {
            if (this.bolditalic != null) {
               return this.bolditalic;
            }

            return this.italic;
         }
      case 2:
         if (this.bolditalic != null) {
            return this.bolditalic;
         } else {
            if (this.plain != null) {
               return this.plain;
            }

            return this.bold;
         }
      case 3:
         if (this.italic != null) {
            return this.italic;
         } else {
            if (this.bold != null) {
               return this.bold;
            }

            return this.plain;
         }
      default:
         return null;
      }
   }

   static synchronized void addLocaleNames(FontFamily var0, String[] var1) {
      if (allLocaleNames == null) {
         allLocaleNames = new HashMap();
      }

      for(int var2 = 0; var2 < var1.length; ++var2) {
         allLocaleNames.put(var1[var2].toLowerCase(), var0);
      }

   }

   public static synchronized FontFamily getLocaleFamily(String var0) {
      return allLocaleNames == null ? null : (FontFamily)allLocaleNames.get(var0.toLowerCase());
   }

   public static FontFamily[] getAllFontFamilies() {
      Collection var0 = familyNameMap.values();
      return (FontFamily[])var0.toArray(new FontFamily[0]);
   }

   public String toString() {
      return "Font family: " + this.familyName + " plain=" + this.plain + " bold=" + this.bold + " italic=" + this.italic + " bolditalic=" + this.bolditalic;
   }
}
