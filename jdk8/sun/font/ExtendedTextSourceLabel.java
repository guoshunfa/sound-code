package sun.font;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Map;

class ExtendedTextSourceLabel extends ExtendedTextLabel implements Decoration.Label {
   TextSource source;
   private Decoration decorator;
   private Font font;
   private AffineTransform baseTX;
   private CoreMetrics cm;
   Rectangle2D lb;
   Rectangle2D ab;
   Rectangle2D vb;
   Rectangle2D ib;
   StandardGlyphVector gv;
   float[] charinfo;
   private static final int posx = 0;
   private static final int posy = 1;
   private static final int advx = 2;
   private static final int advy = 3;
   private static final int visx = 4;
   private static final int visy = 5;
   private static final int visw = 6;
   private static final int vish = 7;
   private static final int numvals = 8;

   public ExtendedTextSourceLabel(TextSource var1, Decoration var2) {
      this.source = var1;
      this.decorator = var2;
      this.finishInit();
   }

   public ExtendedTextSourceLabel(TextSource var1, ExtendedTextSourceLabel var2, int var3) {
      this.source = var1;
      this.decorator = var2.decorator;
      this.finishInit();
   }

   private void finishInit() {
      this.font = this.source.getFont();
      Map var1 = this.font.getAttributes();
      this.baseTX = AttributeValues.getBaselineTransform(var1);
      if (this.baseTX == null) {
         this.cm = this.source.getCoreMetrics();
      } else {
         AffineTransform var2 = AttributeValues.getCharTransform(var1);
         if (var2 == null) {
            var2 = new AffineTransform();
         }

         this.font = this.font.deriveFont(var2);
         LineMetrics var3 = this.font.getLineMetrics(this.source.getChars(), this.source.getStart(), this.source.getStart() + this.source.getLength(), this.source.getFRC());
         this.cm = CoreMetrics.get(var3);
      }

   }

   public Rectangle2D getLogicalBounds() {
      return this.getLogicalBounds(0.0F, 0.0F);
   }

   public Rectangle2D getLogicalBounds(float var1, float var2) {
      if (this.lb == null) {
         this.lb = this.createLogicalBounds();
      }

      return new Rectangle2D.Float((float)(this.lb.getX() + (double)var1), (float)(this.lb.getY() + (double)var2), (float)this.lb.getWidth(), (float)this.lb.getHeight());
   }

   public float getAdvance() {
      if (this.lb == null) {
         this.lb = this.createLogicalBounds();
      }

      return (float)this.lb.getWidth();
   }

   public Rectangle2D getVisualBounds(float var1, float var2) {
      if (this.vb == null) {
         this.vb = this.decorator.getVisualBounds(this);
      }

      return new Rectangle2D.Float((float)(this.vb.getX() + (double)var1), (float)(this.vb.getY() + (double)var2), (float)this.vb.getWidth(), (float)this.vb.getHeight());
   }

   public Rectangle2D getAlignBounds(float var1, float var2) {
      if (this.ab == null) {
         this.ab = this.createAlignBounds();
      }

      return new Rectangle2D.Float((float)(this.ab.getX() + (double)var1), (float)(this.ab.getY() + (double)var2), (float)this.ab.getWidth(), (float)this.ab.getHeight());
   }

   public Rectangle2D getItalicBounds(float var1, float var2) {
      if (this.ib == null) {
         this.ib = this.createItalicBounds();
      }

      return new Rectangle2D.Float((float)(this.ib.getX() + (double)var1), (float)(this.ib.getY() + (double)var2), (float)this.ib.getWidth(), (float)this.ib.getHeight());
   }

   public Rectangle getPixelBounds(FontRenderContext var1, float var2, float var3) {
      return this.getGV().getPixelBounds(var1, var2, var3);
   }

   public boolean isSimple() {
      return this.decorator == Decoration.getPlainDecoration() && this.baseTX == null;
   }

   public AffineTransform getBaselineTransform() {
      return this.baseTX;
   }

   public Shape handleGetOutline(float var1, float var2) {
      return this.getGV().getOutline(var1, var2);
   }

   public Shape getOutline(float var1, float var2) {
      return this.decorator.getOutline(this, var1, var2);
   }

   public void handleDraw(Graphics2D var1, float var2, float var3) {
      var1.drawGlyphVector(this.getGV(), var2, var3);
   }

   public void draw(Graphics2D var1, float var2, float var3) {
      this.decorator.drawTextAndDecorations(this, var1, var2, var3);
   }

   protected Rectangle2D createLogicalBounds() {
      return this.getGV().getLogicalBounds();
   }

   public Rectangle2D handleGetVisualBounds() {
      return this.getGV().getVisualBounds();
   }

   protected Rectangle2D createAlignBounds() {
      float[] var1 = this.getCharinfo();
      float var2 = 0.0F;
      float var3 = -this.cm.ascent;
      float var4 = 0.0F;
      float var5 = this.cm.ascent + this.cm.descent;
      if (this.charinfo != null && this.charinfo.length != 0) {
         boolean var6 = (this.source.getLayoutFlags() & 8) == 0;
         int var7 = var1.length - 8;
         if (var6) {
            while(var7 > 0 && var1[var7 + 6] == 0.0F) {
               var7 -= 8;
            }
         }

         if (var7 >= 0) {
            int var8;
            for(var8 = 0; var8 < var7 && (var1[var8 + 2] == 0.0F || !var6 && var1[var8 + 6] == 0.0F); var8 += 8) {
            }

            var2 = Math.max(0.0F, var1[var8 + 0]);
            var4 = var1[var7 + 0] + var1[var7 + 2] - var2;
         }

         return new Rectangle2D.Float(var2, var3, var4, var5);
      } else {
         return new Rectangle2D.Float(var2, var3, var4, var5);
      }
   }

   public Rectangle2D createItalicBounds() {
      float var1 = this.cm.italicAngle;
      Rectangle2D var2 = this.getLogicalBounds();
      float var3 = (float)var2.getMinX();
      float var4 = -this.cm.ascent;
      float var5 = (float)var2.getMaxX();
      float var6 = this.cm.descent;
      if (var1 != 0.0F) {
         if (var1 > 0.0F) {
            var3 -= var1 * (var6 - this.cm.ssOffset);
            var5 -= var1 * (var4 - this.cm.ssOffset);
         } else {
            var3 -= var1 * (var4 - this.cm.ssOffset);
            var5 -= var1 * (var6 - this.cm.ssOffset);
         }
      }

      return new Rectangle2D.Float(var3, var4, var5 - var3, var6 - var4);
   }

   private final StandardGlyphVector getGV() {
      if (this.gv == null) {
         this.gv = this.createGV();
      }

      return this.gv;
   }

   protected StandardGlyphVector createGV() {
      FontRenderContext var1 = this.source.getFRC();
      int var2 = this.source.getLayoutFlags();
      char[] var3 = this.source.getChars();
      int var4 = this.source.getStart();
      int var5 = this.source.getLength();
      GlyphLayout var6 = GlyphLayout.get((GlyphLayout.LayoutEngineFactory)null);
      this.gv = var6.layout(this.font, var1, var3, var4, var5, var2, (StandardGlyphVector)null);
      GlyphLayout.done(var6);
      return this.gv;
   }

   public int getNumCharacters() {
      return this.source.getLength();
   }

   public CoreMetrics getCoreMetrics() {
      return this.cm;
   }

   public float getCharX(int var1) {
      this.validate(var1);
      float[] var2 = this.getCharinfo();
      int var3 = this.l2v(var1) * 8 + 0;
      return var2 != null && var3 < var2.length ? var2[var3] : 0.0F;
   }

   public float getCharY(int var1) {
      this.validate(var1);
      float[] var2 = this.getCharinfo();
      int var3 = this.l2v(var1) * 8 + 1;
      return var2 != null && var3 < var2.length ? var2[var3] : 0.0F;
   }

   public float getCharAdvance(int var1) {
      this.validate(var1);
      float[] var2 = this.getCharinfo();
      int var3 = this.l2v(var1) * 8 + 2;
      return var2 != null && var3 < var2.length ? var2[var3] : 0.0F;
   }

   public Rectangle2D handleGetCharVisualBounds(int var1) {
      this.validate(var1);
      float[] var2 = this.getCharinfo();
      var1 = this.l2v(var1) * 8;
      return var2 != null && var1 + 7 < var2.length ? new Rectangle2D.Float(var2[var1 + 4], var2[var1 + 5], var2[var1 + 6], var2[var1 + 7]) : new Rectangle2D.Float();
   }

   public Rectangle2D getCharVisualBounds(int var1, float var2, float var3) {
      Rectangle2D var4 = this.decorator.getCharVisualBounds(this, var1);
      if (var2 != 0.0F || var3 != 0.0F) {
         var4.setRect(var4.getX() + (double)var2, var4.getY() + (double)var3, var4.getWidth(), var4.getHeight());
      }

      return var4;
   }

   private void validate(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("index " + var1 + " < 0");
      } else if (var1 >= this.source.getLength()) {
         throw new IllegalArgumentException("index " + var1 + " < " + this.source.getLength());
      }
   }

   public int logicalToVisual(int var1) {
      this.validate(var1);
      return this.l2v(var1);
   }

   public int visualToLogical(int var1) {
      this.validate(var1);
      return this.v2l(var1);
   }

   public int getLineBreakIndex(int var1, float var2) {
      float[] var3 = this.getCharinfo();
      int var4 = this.source.getLength();
      --var1;

      while(var2 >= 0.0F) {
         ++var1;
         if (var1 >= var4) {
            break;
         }

         int var5 = this.l2v(var1) * 8 + 2;
         if (var5 >= var3.length) {
            break;
         }

         float var6 = var3[var5];
         var2 -= var6;
      }

      return var1;
   }

   public float getAdvanceBetween(int var1, int var2) {
      float var3 = 0.0F;
      float[] var4 = this.getCharinfo();
      --var1;

      while(true) {
         ++var1;
         if (var1 >= var2) {
            break;
         }

         int var5 = this.l2v(var1) * 8 + 2;
         if (var5 >= var4.length) {
            break;
         }

         var3 += var4[var5];
      }

      return var3;
   }

   public boolean caretAtOffsetIsValid(int var1) {
      if (var1 != 0 && var1 != this.source.getLength()) {
         char var2 = this.source.getChars()[this.source.getStart() + var1];
         if (var2 != '\t' && var2 != '\n' && var2 != '\r') {
            int var3 = this.l2v(var1);
            int var4 = var3 * 8 + 2;
            float[] var5 = this.getCharinfo();
            if (var5 != null && var4 < var5.length) {
               return var5[var4] != 0.0F;
            } else {
               return false;
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   private final float[] getCharinfo() {
      if (this.charinfo == null) {
         this.charinfo = this.createCharinfo();
      }

      return this.charinfo;
   }

   protected float[] createCharinfo() {
      StandardGlyphVector var1 = this.getGV();
      float[] var2 = null;

      try {
         var2 = var1.getGlyphInfo();
      } catch (Exception var31) {
         System.out.println((Object)this.source);
      }

      int var3 = var1.getNumGlyphs();
      if (var3 == 0) {
         return var2;
      } else {
         int[] var4 = var1.getGlyphCharIndices(0, var3, (int[])null);
         boolean var5 = false;
         int var6;
         if (var5) {
            System.err.println("number of glyphs: " + var3);

            for(var6 = 0; var6 < var3; ++var6) {
               System.err.println("g: " + var6 + ", x: " + var2[var6 * 8 + 0] + ", a: " + var2[var6 * 8 + 2] + ", n: " + var4[var6]);
            }
         }

         var6 = var4[0];
         int var8 = 0;
         int var9 = 0;
         int var10 = 0;
         int var11 = 0;
         int var12 = 0;
         int var13 = var3;
         byte var14 = 8;
         byte var15 = 1;
         boolean var16 = (this.source.getLayoutFlags() & 1) == 0;
         if (!var16) {
            var6 = var4[var3 - 1];
            var8 = 0;
            var9 = var2.length - 8;
            var10 = 0;
            var11 = var2.length - 8;
            var12 = var3 - 1;
            var13 = -1;
            var14 = -8;
            var15 = -1;
         }

         float var17 = 0.0F;
         float var18 = 0.0F;
         float var19 = 0.0F;
         float var20 = 0.0F;
         float var21 = 0.0F;
         float var22 = 0.0F;
         float var23 = 0.0F;

         boolean var24;
         int var26;
         int var27;
         int var34;
         for(var24 = false; var12 != var13; var10 += var15) {
            boolean var25 = false;
            var26 = 0;
            var6 = var4[var12];
            int var7 = var6;
            var12 += var15;

            for(var11 += var14; var12 != var13 && (var2[var11 + 2] == 0.0F || var6 != var8 || var4[var12] <= var7 || var7 - var6 > var26); var11 += var14) {
               if (!var25) {
                  var27 = var11 - var14;
                  var17 = var2[var27 + 0];
                  var18 = var17 + var2[var27 + 2];
                  var19 = var2[var27 + 4];
                  var20 = var2[var27 + 5];
                  var21 = var19 + var2[var27 + 6];
                  var22 = var20 + var2[var27 + 7];
                  var25 = true;
               }

               ++var26;
               float var33 = var2[var11 + 2];
               float var28;
               if (var33 != 0.0F) {
                  var28 = var2[var11 + 0];
                  var17 = Math.min(var17, var28);
                  var18 = Math.max(var18, var28 + var33);
               }

               var28 = var2[var11 + 6];
               if (var28 != 0.0F) {
                  float var29 = var2[var11 + 4];
                  float var30 = var2[var11 + 5];
                  var19 = Math.min(var19, var29);
                  var20 = Math.min(var20, var30);
                  var21 = Math.max(var21, var29 + var28);
                  var22 = Math.max(var22, var30 + var2[var11 + 7]);
               }

               var6 = Math.min(var6, var4[var12]);
               var7 = Math.max(var7, var4[var12]);
               var12 += var15;
            }

            if (var5) {
               System.out.println("minIndex = " + var6 + ", maxIndex = " + var7);
            }

            var8 = var7 + 1;
            var2[var9 + 1] = var23;
            var2[var9 + 3] = 0.0F;
            if (var25) {
               var2[var9 + 0] = var17;
               var2[var9 + 2] = var18 - var17;
               var2[var9 + 4] = var19;
               var2[var9 + 5] = var20;
               var2[var9 + 6] = var21 - var19;
               var2[var9 + 7] = var22 - var20;
               if (var7 - var6 < var26) {
                  var24 = true;
               }

               if (var6 < var7) {
                  if (!var16) {
                     var18 = var17;
                  }

                  var21 -= var19;
                  var22 -= var20;
                  var27 = var6;

                  for(var34 = var9 / 8; var6 < var7; var2[var9 + 7] = var22) {
                     ++var6;
                     var10 += var15;
                     var9 += var14;
                     if ((var9 < 0 || var9 >= var2.length) && var5) {
                        System.out.println("minIndex = " + var27 + ", maxIndex = " + var7 + ", cp = " + var34);
                     }

                     var2[var9 + 0] = var18;
                     var2[var9 + 1] = var23;
                     var2[var9 + 2] = 0.0F;
                     var2[var9 + 3] = 0.0F;
                     var2[var9 + 4] = var19;
                     var2[var9 + 5] = var20;
                     var2[var9 + 6] = var21;
                  }
               }

               var25 = false;
            } else if (var24) {
               var27 = var11 - var14;
               var2[var9 + 0] = var2[var27 + 0];
               var2[var9 + 2] = var2[var27 + 2];
               var2[var9 + 4] = var2[var27 + 4];
               var2[var9 + 5] = var2[var27 + 5];
               var2[var9 + 6] = var2[var27 + 6];
               var2[var9 + 7] = var2[var27 + 7];
            }

            var9 += var14;
         }

         if (var24 && !var16) {
            var9 -= var14;
            System.arraycopy(var2, var9, var2, 0, var2.length - var9);
         }

         if (var5) {
            char[] var32 = this.source.getChars();
            var26 = this.source.getStart();
            var27 = this.source.getLength();
            System.out.println("char info for " + var27 + " characters");
            var34 = 0;

            while(var34 < var27 * 8) {
               System.out.println(" ch: " + Integer.toHexString(var32[var26 + this.v2l(var34 / 8)]) + " x: " + var2[var34++] + " y: " + var2[var34++] + " xa: " + var2[var34++] + " ya: " + var2[var34++] + " l: " + var2[var34++] + " t: " + var2[var34++] + " w: " + var2[var34++] + " h: " + var2[var34++]);
            }
         }

         return var2;
      }
   }

   protected int l2v(int var1) {
      return (this.source.getLayoutFlags() & 1) == 0 ? var1 : this.source.getLength() - 1 - var1;
   }

   protected int v2l(int var1) {
      return (this.source.getLayoutFlags() & 1) == 0 ? var1 : this.source.getLength() - 1 - var1;
   }

   public TextLineComponent getSubset(int var1, int var2, int var3) {
      return new ExtendedTextSourceLabel(this.source.getSubSource(var1, var2 - var1, var3), this.decorator);
   }

   public String toString() {
      TextSource var10001 = this.source;
      return this.source.toString(false);
   }

   public int getNumJustificationInfos() {
      return this.getGV().getNumGlyphs();
   }

   public void getJustificationInfos(GlyphJustificationInfo[] var1, int var2, int var3, int var4) {
      StandardGlyphVector var5 = this.getGV();
      float[] var6 = this.getCharinfo();
      float var7 = var5.getFont().getSize2D();
      GlyphJustificationInfo var8 = new GlyphJustificationInfo(0.0F, false, 3, 0.0F, 0.0F, false, 3, 0.0F, 0.0F);
      GlyphJustificationInfo var9 = new GlyphJustificationInfo(var7, true, 1, 0.0F, var7, true, 1, 0.0F, var7 / 4.0F);
      GlyphJustificationInfo var10 = new GlyphJustificationInfo(var7, true, 2, var7, var7, false, 3, 0.0F, 0.0F);
      char[] var11 = this.source.getChars();
      int var12 = this.source.getStart();
      int var13 = var5.getNumGlyphs();
      int var14 = 0;
      int var15 = var13;
      boolean var16 = (this.source.getLayoutFlags() & 1) == 0;
      if (var3 != 0 || var4 != this.source.getLength()) {
         if (var16) {
            var14 = var3;
            var15 = var4;
         } else {
            var14 = var13 - var4;
            var15 = var13 - var3;
         }
      }

      for(int var17 = 0; var17 < var13; ++var17) {
         GlyphJustificationInfo var18 = null;
         if (var17 >= var14 && var17 < var15) {
            if (var6[var17 * 8 + 2] == 0.0F) {
               var18 = var8;
            } else {
               int var19 = this.v2l(var17);
               char var20 = var11[var12 + var19];
               if (Character.isWhitespace(var20)) {
                  var18 = var9;
               } else if ((var20 < 19968 || var20 >= 'ꀀ') && (var20 < '가' || var20 >= 'ힰ') && (var20 < '豈' || var20 >= 'ﬀ')) {
                  var18 = var8;
               } else {
                  var18 = var10;
               }
            }
         }

         var1[var2 + var17] = var18;
      }

   }

   public TextLineComponent applyJustificationDeltas(float[] var1, int var2, boolean[] var3) {
      float[] var4 = (float[])((float[])this.getCharinfo().clone());
      var3[0] = false;
      StandardGlyphVector var5 = (StandardGlyphVector)this.getGV().clone();
      float[] var6 = var5.getGlyphPositions((float[])null);
      int var7 = var5.getNumGlyphs();
      char[] var8 = this.source.getChars();
      int var9 = this.source.getStart();
      float var10 = 0.0F;

      for(int var11 = 0; var11 < var7; ++var11) {
         if (Character.isWhitespace(var8[var9 + this.v2l(var11)])) {
            var6[var11 * 2] += var10;
            float var12 = var1[var2 + var11 * 2] + var1[var2 + var11 * 2 + 1];
            var4[var11 * 8 + 0] += var10;
            var4[var11 * 8 + 4] += var10;
            var4[var11 * 8 + 2] += var12;
            var10 += var12;
         } else {
            var10 += var1[var2 + var11 * 2];
            var6[var11 * 2] += var10;
            var4[var11 * 8 + 0] += var10;
            var4[var11 * 8 + 4] += var10;
            var10 += var1[var2 + var11 * 2 + 1];
         }
      }

      var6[var7 * 2] += var10;
      var5.setGlyphPositions(var6);
      ExtendedTextSourceLabel var13 = new ExtendedTextSourceLabel(this.source, this.decorator);
      var13.gv = var5;
      var13.charinfo = var4;
      return var13;
   }
}
