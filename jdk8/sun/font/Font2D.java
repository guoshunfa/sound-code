package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Font2D {
   public static final int FONT_CONFIG_RANK = 2;
   public static final int JRE_RANK = 2;
   public static final int TTF_RANK = 3;
   public static final int TYPE1_RANK = 4;
   public static final int NATIVE_RANK = 5;
   public static final int UNKNOWN_RANK = 6;
   public static final int DEFAULT_RANK = 4;
   private static final String[] boldNames = new String[]{"bold", "demibold", "demi-bold", "demi bold", "negreta", "demi"};
   private static final String[] italicNames = new String[]{"italic", "cursiva", "oblique", "inclined"};
   private static final String[] boldItalicNames = new String[]{"bolditalic", "bold-italic", "bold italic", "boldoblique", "bold-oblique", "bold oblique", "demibold italic", "negreta cursiva", "demi oblique"};
   private static final FontRenderContext DEFAULT_FRC = new FontRenderContext((AffineTransform)null, false, false);
   public Font2DHandle handle;
   protected String familyName;
   protected String fullName;
   protected int style = 0;
   protected FontFamily family;
   protected int fontRank = 4;
   protected CharToGlyphMapper mapper;
   protected ConcurrentHashMap<FontStrikeDesc, Reference> strikeCache = new ConcurrentHashMap();
   protected Reference lastFontStrike = new SoftReference((Object)null);
   public static final int FWIDTH_NORMAL = 5;
   public static final int FWEIGHT_NORMAL = 400;
   public static final int FWEIGHT_BOLD = 700;

   public int getStyle() {
      return this.style;
   }

   protected void setStyle() {
      String var1 = this.fullName.toLowerCase();

      int var2;
      for(var2 = 0; var2 < boldItalicNames.length; ++var2) {
         if (var1.indexOf(boldItalicNames[var2]) != -1) {
            this.style = 3;
            return;
         }
      }

      for(var2 = 0; var2 < italicNames.length; ++var2) {
         if (var1.indexOf(italicNames[var2]) != -1) {
            this.style = 2;
            return;
         }
      }

      for(var2 = 0; var2 < boldNames.length; ++var2) {
         if (var1.indexOf(boldNames[var2]) != -1) {
            this.style = 1;
            return;
         }
      }

   }

   public int getWidth() {
      return 5;
   }

   public int getWeight() {
      return (this.style & 1) != 0 ? 700 : 400;
   }

   int getRank() {
      return this.fontRank;
   }

   void setRank(int var1) {
      this.fontRank = var1;
   }

   abstract CharToGlyphMapper getMapper();

   protected int getValidatedGlyphCode(int var1) {
      if (var1 < 0 || var1 >= this.getMapper().getNumGlyphs()) {
         var1 = this.getMapper().getMissingGlyphCode();
      }

      return var1;
   }

   abstract FontStrike createStrike(FontStrikeDesc var1);

   public FontStrike getStrike(Font var1) {
      FontStrike var2 = (FontStrike)this.lastFontStrike.get();
      return var2 != null ? var2 : this.getStrike(var1, DEFAULT_FRC);
   }

   public FontStrike getStrike(Font var1, AffineTransform var2, int var3, int var4) {
      double var5 = (double)var1.getSize2D();
      AffineTransform var7 = (AffineTransform)var2.clone();
      var7.scale(var5, var5);
      if (var1.isTransformed()) {
         var7.concatenate(var1.getTransform());
      }

      if (var7.getTranslateX() != 0.0D || var7.getTranslateY() != 0.0D) {
         var7.setTransform(var7.getScaleX(), var7.getShearY(), var7.getShearX(), var7.getScaleY(), 0.0D, 0.0D);
      }

      FontStrikeDesc var8 = new FontStrikeDesc(var2, var7, var1.getStyle(), var3, var4);
      return this.getStrike(var8, false);
   }

   public FontStrike getStrike(Font var1, AffineTransform var2, AffineTransform var3, int var4, int var5) {
      FontStrikeDesc var6 = new FontStrikeDesc(var2, var3, var1.getStyle(), var4, var5);
      return this.getStrike(var6, false);
   }

   public FontStrike getStrike(Font var1, FontRenderContext var2) {
      AffineTransform var3 = var2.getTransform();
      double var4 = (double)var1.getSize2D();
      var3.scale(var4, var4);
      if (var1.isTransformed()) {
         var3.concatenate(var1.getTransform());
         if (var3.getTranslateX() != 0.0D || var3.getTranslateY() != 0.0D) {
            var3.setTransform(var3.getScaleX(), var3.getShearY(), var3.getShearX(), var3.getScaleY(), 0.0D, 0.0D);
         }
      }

      int var6 = FontStrikeDesc.getAAHintIntVal(this, var1, var2);
      int var7 = FontStrikeDesc.getFMHintIntVal(var2.getFractionalMetricsHint());
      FontStrikeDesc var8 = new FontStrikeDesc(var2.getTransform(), var3, var1.getStyle(), var6, var7);
      return this.getStrike(var8, false);
   }

   FontStrike getStrike(FontStrikeDesc var1) {
      return this.getStrike(var1, true);
   }

   private FontStrike getStrike(FontStrikeDesc var1, boolean var2) {
      FontStrike var3 = (FontStrike)this.lastFontStrike.get();
      if (var3 != null && var1.equals(var3.desc)) {
         return var3;
      } else {
         Reference var4 = (Reference)this.strikeCache.get(var1);
         if (var4 != null) {
            var3 = (FontStrike)var4.get();
            if (var3 != null) {
               this.lastFontStrike = new SoftReference(var3);
               StrikeCache.refStrike(var3);
               return var3;
            }
         }

         if (var2) {
            var1 = new FontStrikeDesc(var1);
         }

         var3 = this.createStrike(var1);
         int var5 = var1.glyphTx.getType();
         if (var5 != 32 && ((var5 & 16) == 0 || this.strikeCache.size() <= 10)) {
            var4 = StrikeCache.getStrikeRef(var3);
         } else {
            var4 = StrikeCache.getStrikeRef(var3, true);
         }

         this.strikeCache.put(var1, var4);
         this.lastFontStrike = new SoftReference(var3);
         StrikeCache.refStrike(var3);
         return var3;
      }
   }

   void removeFromCache(FontStrikeDesc var1) {
      Reference var2 = (Reference)this.strikeCache.get(var1);
      if (var2 != null) {
         Object var3 = var2.get();
         if (var3 == null) {
            this.strikeCache.remove(var1);
         }
      }

   }

   public void getFontMetrics(Font var1, AffineTransform var2, Object var3, Object var4, float[] var5) {
      int var6 = FontStrikeDesc.getAAHintIntVal(var3, this, var1.getSize());
      int var7 = FontStrikeDesc.getFMHintIntVal(var4);
      FontStrike var8 = this.getStrike(var1, var2, var6, var7);
      StrikeMetrics var9 = var8.getFontMetrics();
      var5[0] = var9.getAscent();
      var5[1] = var9.getDescent();
      var5[2] = var9.getLeading();
      var5[3] = var9.getMaxAdvance();
      this.getStyleMetrics(var1.getSize2D(), var5, 4);
   }

   public void getStyleMetrics(float var1, float[] var2, int var3) {
      var2[var3] = -var2[0] / 2.5F;
      var2[var3 + 1] = var1 / 12.0F;
      var2[var3 + 2] = var2[var3 + 1] / 1.5F;
      var2[var3 + 3] = var2[var3 + 1];
   }

   public void getFontMetrics(Font var1, FontRenderContext var2, float[] var3) {
      StrikeMetrics var4 = this.getStrike(var1, var2).getFontMetrics();
      var3[0] = var4.getAscent();
      var3[1] = var4.getDescent();
      var3[2] = var4.getLeading();
      var3[3] = var4.getMaxAdvance();
   }

   protected byte[] getTableBytes(int var1) {
      return null;
   }

   protected long getLayoutTableCache() {
      return 0L;
   }

   protected long getUnitsPerEm() {
      return 2048L;
   }

   boolean supportsEncoding(String var1) {
      return false;
   }

   public boolean canDoStyle(int var1) {
      return var1 == this.style;
   }

   public boolean useAAForPtSize(int var1) {
      return true;
   }

   public boolean hasSupplementaryChars() {
      return false;
   }

   public String getPostscriptName() {
      return this.fullName;
   }

   public String getFontName(Locale var1) {
      return this.fullName;
   }

   public String getFamilyName(Locale var1) {
      return this.familyName;
   }

   public int getNumGlyphs() {
      return this.getMapper().getNumGlyphs();
   }

   public int charToGlyph(int var1) {
      return this.getMapper().charToGlyph(var1);
   }

   public int getMissingGlyphCode() {
      return this.getMapper().getMissingGlyphCode();
   }

   public boolean canDisplay(char var1) {
      return this.getMapper().canDisplay(var1);
   }

   public boolean canDisplay(int var1) {
      return this.getMapper().canDisplay(var1);
   }

   public byte getBaselineFor(char var1) {
      return 0;
   }

   public float getItalicAngle(Font var1, AffineTransform var2, Object var3, Object var4) {
      int var5 = FontStrikeDesc.getAAHintIntVal(var3, this, 12);
      int var6 = FontStrikeDesc.getFMHintIntVal(var4);
      FontStrike var7 = this.getStrike(var1, var2, var5, var6);
      StrikeMetrics var8 = var7.getFontMetrics();
      return var8.ascentY != 0.0F && var8.ascentX != 0.0F ? var8.ascentX / -var8.ascentY : 0.0F;
   }
}
