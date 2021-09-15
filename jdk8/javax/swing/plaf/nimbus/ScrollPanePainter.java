package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

final class ScrollPanePainter extends AbstractRegionPainter {
   static final int BACKGROUND_ENABLED = 1;
   static final int BORDER_ENABLED_FOCUSED = 2;
   static final int BORDER_ENABLED = 3;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBorder", 0.0F, 0.0F, 0.0F, 0);
   private Color color2 = this.decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
   private Object[] componentColors;

   public ScrollPanePainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 2:
         this.paintBorderEnabledAndFocused(var1);
         break;
      case 3:
         this.paintBorderEnabled(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBorderEnabledAndFocused(Graphics2D var1) {
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
      this.path = this.decodePath1();
      var1.setPaint(this.color2);
      var1.fill(this.path);
   }

   private void paintBorderEnabled(Graphics2D var1) {
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
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(0.6F), (double)this.decodeY(0.4F), (double)(this.decodeX(2.4F) - this.decodeX(0.6F)), (double)(this.decodeY(0.6F) - this.decodeY(0.4F)));
      return this.rect;
   }

   private Rectangle2D decodeRect2() {
      this.rect.setRect((double)this.decodeX(0.4F), (double)this.decodeY(0.4F), (double)(this.decodeX(0.6F) - this.decodeX(0.4F)), (double)(this.decodeY(2.6F) - this.decodeY(0.4F)));
      return this.rect;
   }

   private Rectangle2D decodeRect3() {
      this.rect.setRect((double)this.decodeX(2.4F), (double)this.decodeY(0.4F), (double)(this.decodeX(2.6F) - this.decodeX(2.4F)), (double)(this.decodeY(2.6F) - this.decodeY(0.4F)));
      return this.rect;
   }

   private Rectangle2D decodeRect4() {
      this.rect.setRect((double)this.decodeX(0.6F), (double)this.decodeY(2.4F), (double)(this.decodeX(2.4F) - this.decodeX(0.6F)), (double)(this.decodeY(2.6F) - this.decodeY(2.4F)));
      return this.rect;
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.4F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(0.4F));
      this.path.curveTo((double)this.decodeAnchorX(2.6F, 0.0F), (double)this.decodeAnchorY(0.4F, 0.0F), (double)this.decodeAnchorX(2.8800004F, 0.1F), (double)this.decodeAnchorY(0.4F, 0.0F), (double)this.decodeX(2.8800004F), (double)this.decodeY(0.4F));
      this.path.curveTo((double)this.decodeAnchorX(2.8800004F, 0.1F), (double)this.decodeAnchorY(0.4F, 0.0F), (double)this.decodeAnchorX(2.8800004F, 0.0F), (double)this.decodeAnchorY(2.8799999F, 0.0F), (double)this.decodeX(2.8800004F), (double)this.decodeY(2.8799999F));
      this.path.lineTo((double)this.decodeX(0.120000005F), (double)this.decodeY(2.8799999F));
      this.path.lineTo((double)this.decodeX(0.120000005F), (double)this.decodeY(0.120000005F));
      this.path.lineTo((double)this.decodeX(2.8800004F), (double)this.decodeY(0.120000005F));
      this.path.lineTo((double)this.decodeX(2.8800004F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(0.4F));
      this.path.closePath();
      return this.path;
   }
}
