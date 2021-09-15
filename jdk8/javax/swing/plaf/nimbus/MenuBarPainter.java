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

final class MenuBarPainter extends AbstractRegionPainter {
   static final int BACKGROUND_ENABLED = 1;
   static final int BORDER_ENABLED = 2;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.07016757F, 0.12941176F, 0);
   private Color color2 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.10255819F, 0.23921567F, 0);
   private Color color3 = this.decodeColor("nimbusBlueGrey", -0.111111104F, -0.10654225F, 0.23921567F, -29);
   private Color color4 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -255);
   private Color color5 = this.decodeColor("nimbusBorder", 0.0F, 0.0F, 0.0F, 0);
   private Object[] componentColors;

   public MenuBarPainter(AbstractRegionPainter.PaintContext var1, int var2) {
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
         this.paintBorderEnabled(var1);
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

   private void paintBorderEnabled(Graphics2D var1) {
      this.rect = this.decodeRect3();
      var1.setPaint(this.color5);
      var1.fill(this.rect);
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(0.0F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(1.9523809F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect2() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(0.0F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(2.0F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect3() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(2.0F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(3.0F) - this.decodeY(2.0F)));
      return this.rect;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(1.0F * var5 + var3, 0.0F * var6 + var4, 1.0F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.015F, 0.03F, 0.23354445F, 0.7569444F}, new Color[]{this.color2, this.decodeColor(this.color2, this.color3, 0.5F), this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4});
   }
}
