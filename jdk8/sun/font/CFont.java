package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

public final class CFont extends PhysicalFont implements FontSubstitution {
   private boolean isFakeItalic;
   private String nativeFontName;
   private long nativeFontPtr;
   private int fontWidth;
   private int fontWeight;
   private CompositeFont compFont;
   private static FontRenderContext DEFAULT_FRC = new FontRenderContext((AffineTransform)null, false, false);

   StrikeMetrics getFontMetrics(long var1) {
      throw new InternalError("Not implemented");
   }

   float getGlyphAdvance(long var1, int var3) {
      throw new InternalError("Not implemented");
   }

   void getGlyphMetrics(long var1, int var3, Point2D.Float var4) {
      throw new InternalError("Not implemented");
   }

   long getGlyphImage(long var1, int var3) {
      throw new InternalError("Not implemented");
   }

   Rectangle2D.Float getGlyphOutlineBounds(long var1, int var3) {
      throw new InternalError("Not implemented");
   }

   GeneralPath getGlyphOutline(long var1, int var3, float var4, float var5) {
      throw new InternalError("Not implemented");
   }

   GeneralPath getGlyphVectorOutline(long var1, int[] var3, int var4, float var5, float var6) {
      throw new InternalError("Not implemented");
   }

   protected long getLayoutTableCache() {
      return this.getLayoutTableCacheNative(this.getNativeFontPtr());
   }

   protected byte[] getTableBytes(int var1) {
      return this.getTableBytesNative(this.getNativeFontPtr(), var1);
   }

   private synchronized native long getLayoutTableCacheNative(long var1);

   private native byte[] getTableBytesNative(long var1, int var3);

   private static native long createNativeFont(String var0, int var1);

   private static native void disposeNativeFont(long var0);

   private native float getWidthNative(long var1);

   private native float getWeightNative(long var1);

   public int getWidth() {
      if (this.fontWidth == -1) {
         float var1 = this.getWidthNative(this.getNativeFontPtr());
         if ((double)var1 == 0.0D) {
            this.fontWidth = 5;
            return this.fontWidth;
         }

         var1 = (float)((double)var1 + 1.0D);
         var1 = (float)((double)var1 * 100.0D);
         if ((double)var1 <= 50.0D) {
            this.fontWidth = 1;
         } else if ((double)var1 <= 62.5D) {
            this.fontWidth = 2;
         } else if ((double)var1 <= 75.0D) {
            this.fontWidth = 3;
         } else if ((double)var1 <= 87.5D) {
            this.fontWidth = 4;
         } else if ((double)var1 <= 100.0D) {
            this.fontWidth = 5;
         } else if ((double)var1 <= 112.5D) {
            this.fontWidth = 6;
         } else if ((double)var1 <= 125.0D) {
            this.fontWidth = 7;
         } else if ((double)var1 <= 150.0D) {
            this.fontWidth = 8;
         } else {
            this.fontWidth = 9;
         }
      }

      return this.fontWidth;
   }

   public int getWeight() {
      if (this.fontWeight == -1) {
         float var1 = this.getWeightNative(this.getNativeFontPtr());
         if (var1 == 0.0F) {
            return 400;
         }

         var1 = (float)((double)var1 + 1.0D);
         var1 *= 500.0F;
         this.fontWeight = (int)var1;
      }

      return this.fontWeight;
   }

   public CFont(String var1) {
      this(var1, var1);
   }

   public CFont(String var1, String var2) {
      this.fontWidth = -1;
      this.fontWeight = -1;
      this.handle = new Font2DHandle(this);
      this.fullName = var1;
      this.familyName = var2;
      this.nativeFontName = this.fullName;
      this.setStyle();
   }

   public CFont(CFont var1, String var2) {
      this.fontWidth = -1;
      this.fontWeight = -1;
      this.handle = new Font2DHandle(this);
      this.fullName = var2;
      this.familyName = var2;
      this.nativeFontName = var1.nativeFontName;
      this.style = var1.style;
      this.isFakeItalic = var1.isFakeItalic;
   }

   public CFont createItalicVariant() {
      CFont var1 = new CFont(this, this.familyName);
      var1.nativeFontName = this.fullName;
      var1.fullName = this.fullName + (this.style == 1 ? "" : "-") + "Italic-Derived";
      var1.style |= 2;
      var1.isFakeItalic = true;
      return var1;
   }

   protected synchronized long getNativeFontPtr() {
      if (this.nativeFontPtr == 0L) {
         this.nativeFontPtr = createNativeFont(this.nativeFontName, this.style);
      }

      return this.nativeFontPtr;
   }

   static native void getCascadeList(long var0, ArrayList<String> var2);

   private CompositeFont createCompositeFont() {
      ArrayList var1 = new ArrayList();
      getCascadeList(this.nativeFontPtr, var1);
      var1.add("Lucida Sans Regular");
      FontManager var2 = FontManagerFactory.getInstance();
      int var3 = 1 + var1.size();
      PhysicalFont[] var4 = new PhysicalFont[var3];
      var4[0] = this;
      int var5 = 1;
      Iterator var6 = var1.iterator();

      while(var6.hasNext()) {
         String var7 = (String)var6.next();
         if (var7.equals(".AppleSymbolsFB")) {
            var7 = "AppleSymbols";
         }

         Font2D var8 = var2.findFont2D(var7, 0, 0);
         if (var8 != null && var8 != this) {
            var4[var5++] = (PhysicalFont)var8;
         }
      }

      if (var5 < var4.length) {
         PhysicalFont[] var9 = var4;
         var4 = new PhysicalFont[var5];
         System.arraycopy(var9, 0, var4, 0, var5);
      }

      CompositeFont var10 = new CompositeFont(var4);
      var10.mapper = new CCompositeGlyphMapper(var10);
      return var10;
   }

   public CompositeFont getCompositeFont2D() {
      if (this.compFont == null) {
         this.compFont = this.createCompositeFont();
      }

      return this.compFont;
   }

   protected synchronized void finalize() {
      if (this.nativeFontPtr != 0L) {
         disposeNativeFont(this.nativeFontPtr);
      }

      this.nativeFontPtr = 0L;
   }

   protected CharToGlyphMapper getMapper() {
      if (this.mapper == null) {
         this.mapper = new CCharToGlyphMapper(this);
      }

      return this.mapper;
   }

   protected FontStrike createStrike(FontStrikeDesc var1) {
      if (this.isFakeItalic) {
         var1 = new FontStrikeDesc(var1);
         var1.glyphTx.concatenate(AffineTransform.getShearInstance(-0.2D, 0.0D));
      }

      return new CStrike(this, var1);
   }

   public FontStrike getStrike(Font var1) {
      return this.getStrike(var1, DEFAULT_FRC);
   }

   public String toString() {
      return "CFont { fullName: " + this.fullName + ",  familyName: " + this.familyName + ", style: " + this.style + " } aka: " + super.toString();
   }
}
