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

final class PasswordFieldPainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED = 1;
   static final int BACKGROUND_ENABLED = 2;
   static final int BACKGROUND_SELECTED = 3;
   static final int BORDER_DISABLED = 4;
   static final int BORDER_FOCUSED = 5;
   static final int BORDER_ENABLED = 6;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBlueGrey", -0.015872955F, -0.07995863F, 0.15294117F, 0);
   private Color color2 = this.decodeColor("nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
   private Color color3 = this.decodeColor("nimbusBlueGrey", -0.006944418F, -0.07187897F, 0.06666666F, 0);
   private Color color4 = this.decodeColor("nimbusBlueGrey", 0.007936537F, -0.07826825F, 0.10588235F, 0);
   private Color color5 = this.decodeColor("nimbusBlueGrey", 0.007936537F, -0.07856284F, 0.11372548F, 0);
   private Color color6 = this.decodeColor("nimbusBlueGrey", 0.007936537F, -0.07796818F, 0.09803921F, 0);
   private Color color7 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.0965403F, -0.18431371F, 0);
   private Color color8 = this.decodeColor("nimbusBlueGrey", 0.055555582F, -0.1048766F, -0.05098039F, 0);
   private Color color9 = this.decodeColor("nimbusLightBackground", 0.6666667F, 0.004901961F, -0.19999999F, 0);
   private Color color10 = this.decodeColor("nimbusBlueGrey", 0.055555582F, -0.10512091F, -0.019607842F, 0);
   private Color color11 = this.decodeColor("nimbusBlueGrey", 0.055555582F, -0.105344966F, 0.011764705F, 0);
   private Color color12 = this.decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
   private Object[] componentColors;

   public PasswordFieldPainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 1:
         this.paintBackgroundDisabled(var1);
         break;
      case 2:
         this.paintBackgroundEnabled(var1);
         break;
      case 3:
         this.paintBackgroundSelected(var1);
         break;
      case 4:
         this.paintBorderDisabled(var1);
         break;
      case 5:
         this.paintBorderFocused(var1);
         break;
      case 6:
         this.paintBorderEnabled(var1);
      }

   }

   protected Object[] getExtendedCacheKeys(JComponent var1) {
      Object[] var2 = null;
      switch(this.state) {
      case 2:
         var2 = new Object[]{this.getComponentColor(var1, "background", this.color2, 0.0F, 0.0F, 0)};
      case 3:
      case 4:
      default:
         break;
      case 5:
         var2 = new Object[]{this.getComponentColor(var1, "background", this.color9, 0.004901961F, -0.19999999F, 0), this.getComponentColor(var1, "background", this.color2, 0.0F, 0.0F, 0)};
         break;
      case 6:
         var2 = new Object[]{this.getComponentColor(var1, "background", this.color9, 0.004901961F, -0.19999999F, 0), this.getComponentColor(var1, "background", this.color2, 0.0F, 0.0F, 0)};
      }

      return var2;
   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundDisabled(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint((Color)this.componentColors[0]);
      var1.fill(this.rect);
   }

   private void paintBackgroundSelected(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color2);
      var1.fill(this.rect);
   }

   private void paintBorderDisabled(Graphics2D var1) {
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient1(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.decodeGradient2(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color6);
      var1.fill(this.rect);
      this.rect = this.decodeRect5();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
      this.rect = this.decodeRect6();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
   }

   private void paintBorderFocused(Graphics2D var1) {
      this.rect = this.decodeRect7();
      var1.setPaint(this.decodeGradient3(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect8();
      var1.setPaint(this.decodeGradient4(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect9();
      var1.setPaint(this.color10);
      var1.fill(this.rect);
      this.rect = this.decodeRect10();
      var1.setPaint(this.color10);
      var1.fill(this.rect);
      this.rect = this.decodeRect11();
      var1.setPaint(this.color11);
      var1.fill(this.rect);
      this.path = this.decodePath1();
      var1.setPaint(this.color12);
      var1.fill(this.path);
   }

   private void paintBorderEnabled(Graphics2D var1) {
      this.rect = this.decodeRect7();
      var1.setPaint(this.decodeGradient5(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect8();
      var1.setPaint(this.decodeGradient4(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect9();
      var1.setPaint(this.color10);
      var1.fill(this.rect);
      this.rect = this.decodeRect10();
      var1.setPaint(this.color10);
      var1.fill(this.rect);
      this.rect = this.decodeRect11();
      var1.setPaint(this.color11);
      var1.fill(this.rect);
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(0.4F), (double)this.decodeY(0.4F), (double)(this.decodeX(2.6F) - this.decodeX(0.4F)), (double)(this.decodeY(2.6F) - this.decodeY(0.4F)));
      return this.rect;
   }

   private Rectangle2D decodeRect2() {
      this.rect.setRect((double)this.decodeX(0.6666667F), (double)this.decodeY(0.4F), (double)(this.decodeX(2.3333333F) - this.decodeX(0.6666667F)), (double)(this.decodeY(1.0F) - this.decodeY(0.4F)));
      return this.rect;
   }

   private Rectangle2D decodeRect3() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(0.6F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(1.0F) - this.decodeY(0.6F)));
      return this.rect;
   }

   private Rectangle2D decodeRect4() {
      this.rect.setRect((double)this.decodeX(0.6666667F), (double)this.decodeY(1.0F), (double)(this.decodeX(1.0F) - this.decodeX(0.6666667F)), (double)(this.decodeY(2.0F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect5() {
      this.rect.setRect((double)this.decodeX(0.6666667F), (double)this.decodeY(2.3333333F), (double)(this.decodeX(2.3333333F) - this.decodeX(0.6666667F)), (double)(this.decodeY(2.0F) - this.decodeY(2.3333333F)));
      return this.rect;
   }

   private Rectangle2D decodeRect6() {
      this.rect.setRect((double)this.decodeX(2.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(2.3333333F) - this.decodeX(2.0F)), (double)(this.decodeY(2.0F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect7() {
      this.rect.setRect((double)this.decodeX(0.4F), (double)this.decodeY(0.4F), (double)(this.decodeX(2.6F) - this.decodeX(0.4F)), (double)(this.decodeY(1.0F) - this.decodeY(0.4F)));
      return this.rect;
   }

   private Rectangle2D decodeRect8() {
      this.rect.setRect((double)this.decodeX(0.6F), (double)this.decodeY(0.6F), (double)(this.decodeX(2.4F) - this.decodeX(0.6F)), (double)(this.decodeY(1.0F) - this.decodeY(0.6F)));
      return this.rect;
   }

   private Rectangle2D decodeRect9() {
      this.rect.setRect((double)this.decodeX(0.4F), (double)this.decodeY(1.0F), (double)(this.decodeX(0.6F) - this.decodeX(0.4F)), (double)(this.decodeY(2.6F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect10() {
      this.rect.setRect((double)this.decodeX(2.4F), (double)this.decodeY(1.0F), (double)(this.decodeX(2.6F) - this.decodeX(2.4F)), (double)(this.decodeY(2.6F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect11() {
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

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color5, this.decodeColor(this.color5, this.color1, 0.5F), this.color1});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25F * var5 + var3, 0.1625F * var6 + var4, new float[]{0.1F, 0.49999997F, 0.9F}, new Color[]{this.color7, this.decodeColor(this.color7, this.color8, 0.5F), this.color8});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.1F, 0.49999997F, 0.9F}, new Color[]{(Color)this.componentColors[0], this.decodeColor((Color)this.componentColors[0], (Color)this.componentColors[1], 0.5F), (Color)this.componentColors[1]});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.1F, 0.49999997F, 0.9F}, new Color[]{this.color7, this.decodeColor(this.color7, this.color8, 0.5F), this.color8});
   }
}
