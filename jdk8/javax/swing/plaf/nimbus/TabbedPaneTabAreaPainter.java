package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

final class TabbedPaneTabAreaPainter extends AbstractRegionPainter {
   static final int BACKGROUND_ENABLED = 1;
   static final int BACKGROUND_DISABLED = 2;
   static final int BACKGROUND_ENABLED_MOUSEOVER = 3;
   static final int BACKGROUND_ENABLED_PRESSED = 4;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = new Color(255, 200, 0, 255);
   private Color color2 = this.decodeColor("nimbusBase", 0.08801502F, 0.3642857F, -0.4784314F, 0);
   private Color color3 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.45471883F, 0.31764704F, 0);
   private Color color4 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.4633005F, 0.3607843F, 0);
   private Color color5 = this.decodeColor("nimbusBase", 0.05468172F, -0.58308274F, 0.19607842F, 0);
   private Color color6 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
   private Color color7 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.4690476F, 0.39215684F, 0);
   private Color color8 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.47635174F, 0.4352941F, 0);
   private Color color9 = this.decodeColor("nimbusBase", 0.0F, -0.05401492F, 0.05098039F, 0);
   private Color color10 = this.decodeColor("nimbusBase", 0.0F, -0.09303135F, 0.09411764F, 0);
   private Object[] componentColors;

   public TabbedPaneTabAreaPainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 1:
         this.paintBackgroundEnabled(var1);
         break;
      case 2:
         this.paintBackgroundDisabled(var1);
         break;
      case 3:
         this.paintBackgroundEnabledAndMouseOver(var1);
         break;
      case 4:
         this.paintBackgroundEnabledAndPressed(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient1(this.rect));
      var1.fill(this.rect);
   }

   private void paintBackgroundDisabled(Graphics2D var1) {
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient2(this.rect));
      var1.fill(this.rect);
   }

   private void paintBackgroundEnabledAndMouseOver(Graphics2D var1) {
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient3(this.rect));
      var1.fill(this.rect);
   }

   private void paintBackgroundEnabledAndPressed(Graphics2D var1) {
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient4(this.rect));
      var1.fill(this.rect);
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(0.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(0.0F) - this.decodeX(0.0F)), (double)(this.decodeY(1.0F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect2() {
      this.rect.setRect((double)this.decodeX(0.0F), (double)this.decodeY(2.1666667F), (double)(this.decodeX(3.0F) - this.decodeX(0.0F)), (double)(this.decodeY(3.0F) - this.decodeY(2.1666667F)));
      return this.rect;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.08387097F, 0.09677419F, 0.10967742F, 0.43709677F, 0.7645161F, 0.7758064F, 0.7870968F}, new Color[]{this.color2, this.decodeColor(this.color2, this.color3, 0.5F), this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4, this.decodeColor(this.color4, this.color2, 0.5F), this.color2});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.08387097F, 0.09677419F, 0.10967742F, 0.43709677F, 0.7645161F, 0.7758064F, 0.7870968F}, new Color[]{this.color5, this.decodeColor(this.color5, this.color3, 0.5F), this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4, this.decodeColor(this.color4, this.color5, 0.5F), this.color5});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.08387097F, 0.09677419F, 0.10967742F, 0.43709677F, 0.7645161F, 0.7758064F, 0.7870968F}, new Color[]{this.color6, this.decodeColor(this.color6, this.color7, 0.5F), this.color7, this.decodeColor(this.color7, this.color8, 0.5F), this.color8, this.decodeColor(this.color8, this.color2, 0.5F), this.color2});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.08387097F, 0.09677419F, 0.10967742F, 0.43709677F, 0.7645161F, 0.7758064F, 0.7870968F}, new Color[]{this.color2, this.decodeColor(this.color2, this.color9, 0.5F), this.color9, this.decodeColor(this.color9, this.color10, 0.5F), this.color10, this.decodeColor(this.color10, this.color2, 0.5F), this.color2});
   }
}
