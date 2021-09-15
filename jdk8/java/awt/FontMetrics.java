package java.awt;

import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.CharacterIterator;

public abstract class FontMetrics implements Serializable {
   private static final FontRenderContext DEFAULT_FRC;
   protected Font font;
   private static final long serialVersionUID = 1681126225205050147L;

   protected FontMetrics(Font var1) {
      this.font = var1;
   }

   public Font getFont() {
      return this.font;
   }

   public FontRenderContext getFontRenderContext() {
      return DEFAULT_FRC;
   }

   public int getLeading() {
      return 0;
   }

   public int getAscent() {
      return this.font.getSize();
   }

   public int getDescent() {
      return 0;
   }

   public int getHeight() {
      return this.getLeading() + this.getAscent() + this.getDescent();
   }

   public int getMaxAscent() {
      return this.getAscent();
   }

   public int getMaxDescent() {
      return this.getDescent();
   }

   /** @deprecated */
   @Deprecated
   public int getMaxDecent() {
      return this.getMaxDescent();
   }

   public int getMaxAdvance() {
      return -1;
   }

   public int charWidth(int var1) {
      if (!Character.isValidCodePoint(var1)) {
         var1 = 65535;
      }

      if (var1 < 256) {
         return this.getWidths()[var1];
      } else {
         char[] var2 = new char[2];
         int var3 = Character.toChars(var1, var2, 0);
         return this.charsWidth(var2, 0, var3);
      }
   }

   public int charWidth(char var1) {
      if (var1 < 256) {
         return this.getWidths()[var1];
      } else {
         char[] var2 = new char[]{var1};
         return this.charsWidth(var2, 0, 1);
      }
   }

   public int stringWidth(String var1) {
      int var2 = var1.length();
      char[] var3 = new char[var2];
      var1.getChars(0, var2, var3, 0);
      return this.charsWidth(var3, 0, var2);
   }

   public int charsWidth(char[] var1, int var2, int var3) {
      return this.stringWidth(new String(var1, var2, var3));
   }

   public int bytesWidth(byte[] var1, int var2, int var3) {
      return this.stringWidth(new String(var1, 0, var2, var3));
   }

   public int[] getWidths() {
      int[] var1 = new int[256];

      for(char var2 = 0; var2 < 256; ++var2) {
         var1[var2] = this.charWidth(var2);
      }

      return var1;
   }

   public boolean hasUniformLineMetrics() {
      return this.font.hasUniformLineMetrics();
   }

   public LineMetrics getLineMetrics(String var1, Graphics var2) {
      return this.font.getLineMetrics(var1, this.myFRC(var2));
   }

   public LineMetrics getLineMetrics(String var1, int var2, int var3, Graphics var4) {
      return this.font.getLineMetrics(var1, var2, var3, this.myFRC(var4));
   }

   public LineMetrics getLineMetrics(char[] var1, int var2, int var3, Graphics var4) {
      return this.font.getLineMetrics(var1, var2, var3, this.myFRC(var4));
   }

   public LineMetrics getLineMetrics(CharacterIterator var1, int var2, int var3, Graphics var4) {
      return this.font.getLineMetrics(var1, var2, var3, this.myFRC(var4));
   }

   public Rectangle2D getStringBounds(String var1, Graphics var2) {
      return this.font.getStringBounds(var1, this.myFRC(var2));
   }

   public Rectangle2D getStringBounds(String var1, int var2, int var3, Graphics var4) {
      return this.font.getStringBounds(var1, var2, var3, this.myFRC(var4));
   }

   public Rectangle2D getStringBounds(char[] var1, int var2, int var3, Graphics var4) {
      return this.font.getStringBounds(var1, var2, var3, this.myFRC(var4));
   }

   public Rectangle2D getStringBounds(CharacterIterator var1, int var2, int var3, Graphics var4) {
      return this.font.getStringBounds(var1, var2, var3, this.myFRC(var4));
   }

   public Rectangle2D getMaxCharBounds(Graphics var1) {
      return this.font.getMaxCharBounds(this.myFRC(var1));
   }

   private FontRenderContext myFRC(Graphics var1) {
      return var1 instanceof Graphics2D ? ((Graphics2D)var1).getFontRenderContext() : DEFAULT_FRC;
   }

   public String toString() {
      return this.getClass().getName() + "[font=" + this.getFont() + "ascent=" + this.getAscent() + ", descent=" + this.getDescent() + ", height=" + this.getHeight() + "]";
   }

   private static native void initIDs();

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      DEFAULT_FRC = new FontRenderContext((AffineTransform)null, false, false);
   }
}
