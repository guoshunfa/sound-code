package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

final class MenuPainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED = 1;
   static final int BACKGROUND_ENABLED = 2;
   static final int BACKGROUND_ENABLED_SELECTED = 3;
   static final int ARROWICON_DISABLED = 4;
   static final int ARROWICON_ENABLED = 5;
   static final int ARROWICON_ENABLED_SELECTED = 6;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusSelection", 0.0F, 0.0F, 0.0F, 0);
   private Color color2 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.08983666F, -0.17647058F, 0);
   private Color color3 = this.decodeColor("nimbusBlueGrey", 0.055555582F, -0.09663743F, -0.4627451F, 0);
   private Color color4 = new Color(255, 255, 255, 255);
   private Object[] componentColors;

   public MenuPainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 3:
         this.paintBackgroundEnabledAndSelected(var1);
         break;
      case 4:
         this.paintarrowIconDisabled(var1);
         break;
      case 5:
         this.paintarrowIconEnabled(var1);
         break;
      case 6:
         this.paintarrowIconEnabledAndSelected(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundEnabledAndSelected(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
   }

   private void paintarrowIconDisabled(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color2);
      var1.fill(this.path);
   }

   private void paintarrowIconEnabled(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color3);
      var1.fill(this.path);
   }

   private void paintarrowIconEnabledAndSelected(Graphics2D var1) {
      this.path = this.decodePath2();
      var1.setPaint(this.color4);
      var1.fill(this.path);
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(2.0F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(2.7512195F), (double)this.decodeY(2.102439F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.2F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.9529617F), (double)this.decodeY(1.5625F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(1.0F));
      this.path.closePath();
      return this.path;
   }
}
