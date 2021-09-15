package java.awt;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public abstract class Graphics2D extends Graphics {
   protected Graphics2D() {
   }

   public void draw3DRect(int var1, int var2, int var3, int var4, boolean var5) {
      Paint var6 = this.getPaint();
      Color var7 = this.getColor();
      Color var8 = var7.brighter();
      Color var9 = var7.darker();
      this.setColor(var5 ? var8 : var9);
      this.fillRect(var1, var2, 1, var4 + 1);
      this.fillRect(var1 + 1, var2, var3 - 1, 1);
      this.setColor(var5 ? var9 : var8);
      this.fillRect(var1 + 1, var2 + var4, var3, 1);
      this.fillRect(var1 + var3, var2, 1, var4);
      this.setPaint(var6);
   }

   public void fill3DRect(int var1, int var2, int var3, int var4, boolean var5) {
      Paint var6 = this.getPaint();
      Color var7 = this.getColor();
      Color var8 = var7.brighter();
      Color var9 = var7.darker();
      if (!var5) {
         this.setColor(var9);
      } else if (var6 != var7) {
         this.setColor(var7);
      }

      this.fillRect(var1 + 1, var2 + 1, var3 - 2, var4 - 2);
      this.setColor(var5 ? var8 : var9);
      this.fillRect(var1, var2, 1, var4);
      this.fillRect(var1 + 1, var2, var3 - 2, 1);
      this.setColor(var5 ? var9 : var8);
      this.fillRect(var1 + 1, var2 + var4 - 1, var3 - 1, 1);
      this.fillRect(var1 + var3 - 1, var2, 1, var4 - 1);
      this.setPaint(var6);
   }

   public abstract void draw(Shape var1);

   public abstract boolean drawImage(Image var1, AffineTransform var2, ImageObserver var3);

   public abstract void drawImage(BufferedImage var1, BufferedImageOp var2, int var3, int var4);

   public abstract void drawRenderedImage(RenderedImage var1, AffineTransform var2);

   public abstract void drawRenderableImage(RenderableImage var1, AffineTransform var2);

   public abstract void drawString(String var1, int var2, int var3);

   public abstract void drawString(String var1, float var2, float var3);

   public abstract void drawString(AttributedCharacterIterator var1, int var2, int var3);

   public abstract void drawString(AttributedCharacterIterator var1, float var2, float var3);

   public abstract void drawGlyphVector(GlyphVector var1, float var2, float var3);

   public abstract void fill(Shape var1);

   public abstract boolean hit(Rectangle var1, Shape var2, boolean var3);

   public abstract GraphicsConfiguration getDeviceConfiguration();

   public abstract void setComposite(Composite var1);

   public abstract void setPaint(Paint var1);

   public abstract void setStroke(Stroke var1);

   public abstract void setRenderingHint(RenderingHints.Key var1, Object var2);

   public abstract Object getRenderingHint(RenderingHints.Key var1);

   public abstract void setRenderingHints(Map<?, ?> var1);

   public abstract void addRenderingHints(Map<?, ?> var1);

   public abstract RenderingHints getRenderingHints();

   public abstract void translate(int var1, int var2);

   public abstract void translate(double var1, double var3);

   public abstract void rotate(double var1);

   public abstract void rotate(double var1, double var3, double var5);

   public abstract void scale(double var1, double var3);

   public abstract void shear(double var1, double var3);

   public abstract void transform(AffineTransform var1);

   public abstract void setTransform(AffineTransform var1);

   public abstract AffineTransform getTransform();

   public abstract Paint getPaint();

   public abstract Composite getComposite();

   public abstract void setBackground(Color var1);

   public abstract Color getBackground();

   public abstract Stroke getStroke();

   public abstract void clip(Shape var1);

   public abstract FontRenderContext getFontRenderContext();
}
