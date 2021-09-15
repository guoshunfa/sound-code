package sun.font;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.GraphicAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

public final class GraphicComponent implements TextLineComponent, Decoration.Label {
   public static final float GRAPHIC_LEADING = 2.0F;
   private GraphicAttribute graphic;
   private int graphicCount;
   private int[] charsLtoV;
   private byte[] levels;
   private Rectangle2D visualBounds = null;
   private float graphicAdvance;
   private AffineTransform baseTx;
   private CoreMetrics cm;
   private Decoration decorator;

   public GraphicComponent(GraphicAttribute var1, Decoration var2, int[] var3, byte[] var4, int var5, int var6, AffineTransform var7) {
      if (var6 <= var5) {
         throw new IllegalArgumentException("0 or negative length in GraphicComponent");
      } else {
         this.graphic = var1;
         this.graphicAdvance = var1.getAdvance();
         this.decorator = var2;
         this.cm = createCoreMetrics(var1);
         this.baseTx = var7;
         this.initLocalOrdering(var3, var4, var5, var6);
      }
   }

   private GraphicComponent(GraphicComponent var1, int var2, int var3, int var4) {
      this.graphic = var1.graphic;
      this.graphicAdvance = var1.graphicAdvance;
      this.decorator = var1.decorator;
      this.cm = var1.cm;
      this.baseTx = var1.baseTx;
      int[] var5 = null;
      byte[] var6 = null;
      if (var4 == 2) {
         var5 = var1.charsLtoV;
         var6 = var1.levels;
      } else {
         if (var4 != 0 && var4 != 1) {
            throw new IllegalArgumentException("Invalid direction flag");
         }

         var3 -= var2;
         var2 = 0;
         if (var4 == 1) {
            var5 = new int[var3];
            var6 = new byte[var3];

            for(int var7 = 0; var7 < var3; ++var7) {
               var5[var7] = var3 - var7 - 1;
               var6[var7] = 1;
            }
         }
      }

      this.initLocalOrdering(var5, var6, var2, var3);
   }

   private void initLocalOrdering(int[] var1, byte[] var2, int var3, int var4) {
      this.graphicCount = var4 - var3;
      if (var1 != null && var1.length != this.graphicCount) {
         this.charsLtoV = BidiUtils.createNormalizedMap(var1, var2, var3, var4);
      } else {
         this.charsLtoV = var1;
      }

      if (var2 != null && var2.length != this.graphicCount) {
         this.levels = new byte[this.graphicCount];
         System.arraycopy(var2, var3, this.levels, 0, this.graphicCount);
      } else {
         this.levels = var2;
      }

   }

   public boolean isSimple() {
      return false;
   }

   public Rectangle getPixelBounds(FontRenderContext var1, float var2, float var3) {
      throw new InternalError("do not call if isSimple returns false");
   }

   public Rectangle2D handleGetVisualBounds() {
      Rectangle2D var1 = this.graphic.getBounds();
      float var2 = (float)var1.getWidth() + this.graphicAdvance * (float)(this.graphicCount - 1);
      return new Rectangle2D.Float((float)var1.getX(), (float)var1.getY(), var2, (float)var1.getHeight());
   }

   public CoreMetrics getCoreMetrics() {
      return this.cm;
   }

   public static CoreMetrics createCoreMetrics(GraphicAttribute var0) {
      return new CoreMetrics(var0.getAscent(), var0.getDescent(), 2.0F, var0.getAscent() + var0.getDescent() + 2.0F, var0.getAlignment(), new float[]{0.0F, -var0.getAscent() / 2.0F, -var0.getAscent()}, -var0.getAscent() / 2.0F, var0.getAscent() / 12.0F, var0.getDescent() / 3.0F, var0.getAscent() / 12.0F, 0.0F, 0.0F);
   }

   public float getItalicAngle() {
      return 0.0F;
   }

   public Rectangle2D getVisualBounds() {
      if (this.visualBounds == null) {
         this.visualBounds = this.decorator.getVisualBounds(this);
      }

      Rectangle2D.Float var1 = new Rectangle2D.Float();
      var1.setRect(this.visualBounds);
      return var1;
   }

   public Shape handleGetOutline(float var1, float var2) {
      double[] var3 = new double[]{1.0D, 0.0D, 0.0D, 1.0D, (double)var1, (double)var2};
      if (this.graphicCount == 1) {
         AffineTransform var7 = new AffineTransform(var3);
         return this.graphic.getOutline(var7);
      } else {
         GeneralPath var4 = new GeneralPath();

         for(int var5 = 0; var5 < this.graphicCount; ++var5) {
            AffineTransform var6 = new AffineTransform(var3);
            var4.append(this.graphic.getOutline(var6), false);
            var3[4] += (double)this.graphicAdvance;
         }

         return var4;
      }
   }

   public AffineTransform getBaselineTransform() {
      return this.baseTx;
   }

   public Shape getOutline(float var1, float var2) {
      return this.decorator.getOutline(this, var1, var2);
   }

   public void handleDraw(Graphics2D var1, float var2, float var3) {
      for(int var4 = 0; var4 < this.graphicCount; ++var4) {
         this.graphic.draw(var1, var2, var3);
         var2 += this.graphicAdvance;
      }

   }

   public void draw(Graphics2D var1, float var2, float var3) {
      this.decorator.drawTextAndDecorations(this, var1, var2, var3);
   }

   public Rectangle2D getCharVisualBounds(int var1) {
      return this.decorator.getCharVisualBounds(this, var1);
   }

   public int getNumCharacters() {
      return this.graphicCount;
   }

   public float getCharX(int var1) {
      int var2 = this.charsLtoV == null ? var1 : this.charsLtoV[var1];
      return this.graphicAdvance * (float)var2;
   }

   public float getCharY(int var1) {
      return 0.0F;
   }

   public float getCharAdvance(int var1) {
      return this.graphicAdvance;
   }

   public boolean caretAtOffsetIsValid(int var1) {
      return true;
   }

   public Rectangle2D handleGetCharVisualBounds(int var1) {
      Rectangle2D var2 = this.graphic.getBounds();
      Rectangle2D.Float var3 = new Rectangle2D.Float();
      var3.setRect(var2);
      var3.x += this.graphicAdvance * (float)var1;
      return var3;
   }

   public int getLineBreakIndex(int var1, float var2) {
      int var3 = (int)(var2 / this.graphicAdvance);
      if (var3 > this.graphicCount - var1) {
         var3 = this.graphicCount - var1;
      }

      return var3;
   }

   public float getAdvanceBetween(int var1, int var2) {
      return this.graphicAdvance * (float)(var2 - var1);
   }

   public Rectangle2D getLogicalBounds() {
      float var1 = 0.0F;
      float var2 = -this.cm.ascent;
      float var3 = this.graphicAdvance * (float)this.graphicCount;
      float var4 = this.cm.descent - var2;
      return new Rectangle2D.Float(var1, var2, var3, var4);
   }

   public float getAdvance() {
      return this.graphicAdvance * (float)this.graphicCount;
   }

   public Rectangle2D getItalicBounds() {
      return this.getLogicalBounds();
   }

   public TextLineComponent getSubset(int var1, int var2, int var3) {
      if (var1 >= 0 && var2 <= this.graphicCount && var1 < var2) {
         return var1 == 0 && var2 == this.graphicCount && var3 == 2 ? this : new GraphicComponent(this, var1, var2, var3);
      } else {
         throw new IllegalArgumentException("Invalid range.  start=" + var1 + "; limit=" + var2);
      }
   }

   public String toString() {
      return "[graphic=" + this.graphic + ":count=" + this.getNumCharacters() + "]";
   }

   public int getNumJustificationInfos() {
      return 0;
   }

   public void getJustificationInfos(GlyphJustificationInfo[] var1, int var2, int var3, int var4) {
   }

   public TextLineComponent applyJustificationDeltas(float[] var1, int var2, boolean[] var3) {
      return this;
   }
}
