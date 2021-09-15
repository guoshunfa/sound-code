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

final class DesktopIconPainter extends AbstractRegionPainter {
   static final int BACKGROUND_ENABLED = 1;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBase", 0.02551502F, -0.47885156F, -0.34901965F, 0);
   private Color color2 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.102261856F, 0.20392156F, 0);
   private Color color3 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.0682728F, 0.09019607F, 0);
   private Color color4 = this.decodeColor("nimbusBlueGrey", -0.01111114F, -0.088974595F, 0.16470587F, 0);
   private Color color5 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.029445238F, -0.019607842F, 0);
   private Object[] componentColors;

   public DesktopIconPainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 1:
         this.paintBackgroundEnabled(var1);
      default:
      }
   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient1(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.decodeGradient2(this.rect));
      var1.fill(this.rect);
   }

   private RoundRectangle2D decodeRoundRect1() {
      this.roundRect.setRoundRect((double)this.decodeX(0.4F), (double)this.decodeY(0.0F), (double)(this.decodeX(2.8F) - this.decodeX(0.4F)), (double)(this.decodeY(2.6F) - this.decodeY(0.0F)), 4.833333492279053D, 4.833333492279053D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect2() {
      this.roundRect.setRoundRect((double)this.decodeX(0.6F), (double)this.decodeY(0.2F), (double)(this.decodeX(2.8F) - this.decodeX(0.6F)), (double)(this.decodeY(2.4F) - this.decodeY(0.2F)), 3.0999999046325684D, 3.0999999046325684D);
      return this.roundRect;
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(0.8F), (double)this.decodeY(0.4F), (double)(this.decodeX(2.4F) - this.decodeX(0.8F)), (double)(this.decodeY(2.2F) - this.decodeY(0.4F)));
      return this.rect;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color2, this.decodeColor(this.color2, this.color3, 0.5F), this.color3});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.24F, 1.0F}, new Color[]{this.color4, this.decodeColor(this.color4, this.color5, 0.5F), this.color5});
   }
}
