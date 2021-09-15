package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

final class TableEditorPainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED = 1;
   static final int BACKGROUND_ENABLED = 2;
   static final int BACKGROUND_ENABLED_FOCUSED = 3;
   static final int BACKGROUND_SELECTED = 4;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
   private Color color2 = this.decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
   private Object[] componentColors;

   public TableEditorPainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 2:
         this.paintBackgroundEnabled(var1);
         break;
      case 3:
         this.paintBackgroundEnabledAndFocused(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
   }

   private void paintBackgroundEnabledAndFocused(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color2);
      var1.fill(this.path);
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(0.0F), (double)this.decodeY(0.0F), (double)(this.decodeX(3.0F) - this.decodeX(0.0F)), (double)(this.decodeY(3.0F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.24000001F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.24000001F), (double)this.decodeY(0.24000001F));
      this.path.lineTo((double)this.decodeX(2.7600007F), (double)this.decodeY(0.24000001F));
      this.path.lineTo((double)this.decodeX(2.7600007F), (double)this.decodeY(2.7599998F));
      this.path.lineTo((double)this.decodeX(0.24000001F), (double)this.decodeY(2.7599998F));
      this.path.lineTo((double)this.decodeX(0.24000001F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.0F));
      this.path.closePath();
      return this.path;
   }
}
