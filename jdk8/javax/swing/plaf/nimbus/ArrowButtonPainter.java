package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

final class ArrowButtonPainter extends AbstractRegionPainter {
   static final int BACKGROUND_ENABLED = 1;
   static final int FOREGROUND_DISABLED = 2;
   static final int FOREGROUND_ENABLED = 3;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBase", 0.027408898F, -0.57391655F, 0.1490196F, 0);
   private Color color2 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.37254906F, 0);
   private Object[] componentColors;

   public ArrowButtonPainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 2:
         this.paintForegroundDisabled(var1);
         break;
      case 3:
         this.paintForegroundEnabled(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintForegroundDisabled(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color1);
      var1.fill(this.path);
   }

   private void paintForegroundEnabled(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color2);
      var1.fill(this.path);
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.8F), (double)this.decodeY(1.2F));
      this.path.lineTo((double)this.decodeX(1.2F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(1.8F), (double)this.decodeY(1.8F));
      this.path.lineTo((double)this.decodeX(1.8F), (double)this.decodeY(1.2F));
      this.path.closePath();
      return this.path;
   }
}
