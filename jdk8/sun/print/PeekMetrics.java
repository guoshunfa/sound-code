package sun.print;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.font.TextLayout;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;

public class PeekMetrics {
   private boolean mHasNonSolidColors;
   private boolean mHasCompositing;
   private boolean mHasText;
   private boolean mHasImages;

   public boolean hasNonSolidColors() {
      return this.mHasNonSolidColors;
   }

   public boolean hasCompositing() {
      return this.mHasCompositing;
   }

   public boolean hasText() {
      return this.mHasText;
   }

   public boolean hasImages() {
      return this.mHasImages;
   }

   public void fill(Graphics2D var1) {
      this.checkDrawingMode(var1);
   }

   public void draw(Graphics2D var1) {
      this.checkDrawingMode(var1);
   }

   public void clear(Graphics2D var1) {
      this.checkPaint(var1.getBackground());
   }

   public void drawText(Graphics2D var1) {
      this.mHasText = true;
      this.checkDrawingMode(var1);
   }

   public void drawText(Graphics2D var1, TextLayout var2) {
      this.mHasText = true;
      this.checkDrawingMode(var1);
   }

   public void drawImage(Graphics2D var1, Image var2) {
      this.mHasImages = true;
   }

   public void drawImage(Graphics2D var1, RenderedImage var2) {
      this.mHasImages = true;
   }

   public void drawImage(Graphics2D var1, RenderableImage var2) {
      this.mHasImages = true;
   }

   private void checkDrawingMode(Graphics2D var1) {
      this.checkPaint(var1.getPaint());
      this.checkAlpha(var1.getComposite());
   }

   private void checkPaint(Paint var1) {
      if (var1 instanceof Color) {
         if (((Color)var1).getAlpha() < 255) {
            this.mHasNonSolidColors = true;
         }
      } else {
         this.mHasNonSolidColors = true;
      }

   }

   private void checkAlpha(Composite var1) {
      if (var1 instanceof AlphaComposite) {
         AlphaComposite var2 = (AlphaComposite)var1;
         float var3 = var2.getAlpha();
         int var4 = var2.getRule();
         if ((double)var3 != 1.0D || var4 != 2 && var4 != 3) {
            this.mHasCompositing = true;
         }
      } else {
         this.mHasCompositing = true;
      }

   }
}
