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

final class ToolBarPainter extends AbstractRegionPainter {
   static final int BORDER_NORTH = 1;
   static final int BORDER_SOUTH = 2;
   static final int BORDER_EAST = 3;
   static final int BORDER_WEST = 4;
   static final int HANDLEICON_ENABLED = 5;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBorder", 0.0F, 0.0F, 0.0F, 0);
   private Color color2 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
   private Color color3 = this.decodeColor("nimbusBlueGrey", -0.006944418F, -0.07399663F, 0.11372548F, 0);
   private Color color4 = this.decodeColor("nimbusBorder", 0.0F, -0.029675633F, 0.109803915F, 0);
   private Color color5 = this.decodeColor("nimbusBlueGrey", -0.008547008F, -0.03494492F, -0.07058823F, 0);
   private Object[] componentColors;

   public ToolBarPainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 1:
         this.paintBorderNorth(var1);
         break;
      case 2:
         this.paintBorderSouth(var1);
         break;
      case 3:
         this.paintBorderEast(var1);
         break;
      case 4:
         this.paintBorderWest(var1);
         break;
      case 5:
         this.painthandleIconEnabled(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBorderNorth(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
   }

   private void paintBorderSouth(Graphics2D var1) {
      this.rect = this.decodeRect2();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
   }

   private void paintBorderEast(Graphics2D var1) {
      this.rect = this.decodeRect2();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
   }

   private void paintBorderWest(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
   }

   private void painthandleIconEnabled(Graphics2D var1) {
      this.rect = this.decodeRect3();
      var1.setPaint(this.decodeGradient1(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
      this.path = this.decodePath1();
      var1.setPaint(this.color5);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color5);
      var1.fill(this.path);
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(2.0F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(3.0F) - this.decodeY(2.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect2() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(0.0F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(1.0F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect3() {
      this.rect.setRect((double)this.decodeX(0.0F), (double)this.decodeY(0.0F), (double)(this.decodeX(2.8F) - this.decodeX(0.0F)), (double)(this.decodeY(3.0F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect4() {
      this.rect.setRect((double)this.decodeX(2.8F), (double)this.decodeY(0.0F), (double)(this.decodeX(3.0F) - this.decodeX(2.8F)), (double)(this.decodeY(3.0F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.closePath();
      return this.path;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.0F * var5 + var3, 0.5F * var6 + var4, 1.0F * var5 + var3, 0.5F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color2, this.decodeColor(this.color2, this.color3, 0.5F), this.color3});
   }
}
