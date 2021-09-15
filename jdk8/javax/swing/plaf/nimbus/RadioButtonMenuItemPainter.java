package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

final class RadioButtonMenuItemPainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED = 1;
   static final int BACKGROUND_ENABLED = 2;
   static final int BACKGROUND_MOUSEOVER = 3;
   static final int BACKGROUND_SELECTED_MOUSEOVER = 4;
   static final int CHECKICON_DISABLED_SELECTED = 5;
   static final int CHECKICON_ENABLED_SELECTED = 6;
   static final int CHECKICON_SELECTED_MOUSEOVER = 7;
   static final int ICON_DISABLED = 8;
   static final int ICON_ENABLED = 9;
   static final int ICON_MOUSEOVER = 10;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusSelection", 0.0F, 0.0F, 0.0F, 0);
   private Color color2 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.08983666F, -0.17647058F, 0);
   private Color color3 = this.decodeColor("nimbusBlueGrey", 0.055555582F, -0.09663743F, -0.4627451F, 0);
   private Color color4 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
   private Object[] componentColors;

   public RadioButtonMenuItemPainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 3:
         this.paintBackgroundMouseOver(var1);
         break;
      case 4:
         this.paintBackgroundSelectedAndMouseOver(var1);
         break;
      case 5:
         this.paintcheckIconDisabledAndSelected(var1);
         break;
      case 6:
         this.paintcheckIconEnabledAndSelected(var1);
         break;
      case 7:
         this.paintcheckIconSelectedAndMouseOver(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundMouseOver(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
   }

   private void paintBackgroundSelectedAndMouseOver(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
   }

   private void paintcheckIconDisabledAndSelected(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color2);
      var1.fill(this.path);
   }

   private void paintcheckIconEnabledAndSelected(Graphics2D var1) {
      this.path = this.decodePath2();
      var1.setPaint(this.color3);
      var1.fill(this.path);
   }

   private void paintcheckIconSelectedAndMouseOver(Graphics2D var1) {
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
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(2.097561F));
      this.path.lineTo((double)this.decodeX(0.90975606F), (double)this.decodeY(0.20243903F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(2.102439F));
      this.path.lineTo((double)this.decodeX(0.90731704F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.097561F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0024390244F), (double)this.decodeY(2.097561F));
      this.path.lineTo((double)this.decodeX(0.90975606F), (double)this.decodeY(0.20243903F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(2.102439F));
      this.path.lineTo((double)this.decodeX(0.90731704F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0024390244F), (double)this.decodeY(2.097561F));
      this.path.closePath();
      return this.path;
   }
}
