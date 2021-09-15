package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

final class ToolTipPainter extends AbstractRegionPainter {
   static final int BACKGROUND_ENABLED = 1;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBorder", 0.0F, 0.0F, 0.0F, 0);
   private Color color2 = this.decodeColor("info", 0.0F, 0.0F, 0.0F, 0);
   private Object[] componentColors;

   public ToolTipPainter(AbstractRegionPainter.PaintContext var1, int var2) {
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
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
      this.rect = this.decodeRect5();
      var1.setPaint(this.color2);
      var1.fill(this.rect);
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(2.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(3.0F) - this.decodeX(2.0F)), (double)(this.decodeY(2.0F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect2() {
      this.rect.setRect((double)this.decodeX(0.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(1.0F) - this.decodeX(0.0F)), (double)(this.decodeY(2.0F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect3() {
      this.rect.setRect((double)this.decodeX(0.0F), (double)this.decodeY(2.0F), (double)(this.decodeX(3.0F) - this.decodeX(0.0F)), (double)(this.decodeY(3.0F) - this.decodeY(2.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect4() {
      this.rect.setRect((double)this.decodeX(0.0F), (double)this.decodeY(0.0F), (double)(this.decodeX(3.0F) - this.decodeX(0.0F)), (double)(this.decodeY(1.0F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect5() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(2.0F) - this.decodeY(1.0F)));
      return this.rect;
   }
}
