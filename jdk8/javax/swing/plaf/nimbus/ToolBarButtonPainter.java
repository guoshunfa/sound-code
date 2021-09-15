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

final class ToolBarButtonPainter extends AbstractRegionPainter {
   static final int BACKGROUND_ENABLED = 1;
   static final int BACKGROUND_FOCUSED = 2;
   static final int BACKGROUND_MOUSEOVER = 3;
   static final int BACKGROUND_MOUSEOVER_FOCUSED = 4;
   static final int BACKGROUND_PRESSED = 5;
   static final int BACKGROUND_PRESSED_FOCUSED = 6;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
   private Color color2 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.06885965F, -0.36862746F, -153);
   private Color color3 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.020974077F, -0.21960783F, 0);
   private Color color4 = this.decodeColor("nimbusBlueGrey", 0.0F, 0.11169591F, -0.53333336F, 0);
   private Color color5 = this.decodeColor("nimbusBlueGrey", 0.055555582F, -0.10658931F, 0.25098038F, 0);
   private Color color6 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.098526314F, 0.2352941F, 0);
   private Color color7 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.07333623F, 0.20392156F, 0);
   private Color color8 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
   private Color color9 = this.decodeColor("nimbusBlueGrey", -0.00505054F, -0.05960039F, 0.10196078F, 0);
   private Color color10 = this.decodeColor("nimbusBlueGrey", -0.008547008F, -0.04772438F, 0.06666666F, 0);
   private Color color11 = this.decodeColor("nimbusBlueGrey", -0.0027777553F, -0.0018306673F, -0.02352941F, 0);
   private Color color12 = this.decodeColor("nimbusBlueGrey", -0.0027777553F, -0.0212406F, 0.13333333F, 0);
   private Color color13 = this.decodeColor("nimbusBlueGrey", 0.0055555105F, -0.030845039F, 0.23921567F, 0);
   private Object[] componentColors;

   public ToolBarButtonPainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 2:
         this.paintBackgroundFocused(var1);
         break;
      case 3:
         this.paintBackgroundMouseOver(var1);
         break;
      case 4:
         this.paintBackgroundMouseOverAndFocused(var1);
         break;
      case 5:
         this.paintBackgroundPressed(var1);
         break;
      case 6:
         this.paintBackgroundPressedAndFocused(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundFocused(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color1);
      var1.fill(this.path);
   }

   private void paintBackgroundMouseOver(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color2);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient1(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient2(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundMouseOverAndFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect4();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient1(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient2(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundPressed(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color2);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient1(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient3(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundPressedAndFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect4();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient1(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient3(this.roundRect));
      var1.fill(this.roundRect);
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.4133738F), (double)this.decodeY(0.120000005F));
      this.path.lineTo((double)this.decodeX(1.9893618F), (double)this.decodeY(0.120000005F));
      this.path.curveTo((double)this.decodeAnchorX(1.9893618F, 3.0F), (double)this.decodeAnchorY(0.120000005F, 0.0F), (double)this.decodeAnchorX(2.8857148F, 0.0F), (double)this.decodeAnchorY(1.0434783F, -3.0F), (double)this.decodeX(2.8857148F), (double)this.decodeY(1.0434783F));
      this.path.lineTo((double)this.decodeX(2.9F), (double)this.decodeY(1.9565217F));
      this.path.curveTo((double)this.decodeAnchorX(2.9F, 0.0F), (double)this.decodeAnchorY(1.9565217F, 3.0F), (double)this.decodeAnchorX(1.9893618F, 3.0F), (double)this.decodeAnchorY(2.8714287F, 0.0F), (double)this.decodeX(1.9893618F), (double)this.decodeY(2.8714287F));
      this.path.lineTo((double)this.decodeX(1.0106384F), (double)this.decodeY(2.8714287F));
      this.path.curveTo((double)this.decodeAnchorX(1.0106384F, -3.0F), (double)this.decodeAnchorY(2.8714287F, 0.0F), (double)this.decodeAnchorX(0.120000005F, 0.0F), (double)this.decodeAnchorY(1.9565217F, 3.0F), (double)this.decodeX(0.120000005F), (double)this.decodeY(1.9565217F));
      this.path.lineTo((double)this.decodeX(0.120000005F), (double)this.decodeY(1.0465839F));
      this.path.curveTo((double)this.decodeAnchorX(0.120000005F, 0.0F), (double)this.decodeAnchorY(1.0465839F, -3.0F), (double)this.decodeAnchorX(1.0106384F, -3.0F), (double)this.decodeAnchorY(0.120000005F, 0.0F), (double)this.decodeX(1.0106384F), (double)this.decodeY(0.120000005F));
      this.path.lineTo((double)this.decodeX(1.4148936F), (double)this.decodeY(0.120000005F));
      this.path.lineTo((double)this.decodeX(1.4148936F), (double)this.decodeY(0.4857143F));
      this.path.lineTo((double)this.decodeX(1.0106384F), (double)this.decodeY(0.4857143F));
      this.path.curveTo((double)this.decodeAnchorX(1.0106384F, -1.9285715F), (double)this.decodeAnchorY(0.4857143F, 0.0F), (double)this.decodeAnchorX(0.47142857F, -0.044279482F), (double)this.decodeAnchorY(1.0403726F, -2.429218F), (double)this.decodeX(0.47142857F), (double)this.decodeY(1.0403726F));
      this.path.lineTo((double)this.decodeX(0.47142857F), (double)this.decodeY(1.9565217F));
      this.path.curveTo((double)this.decodeAnchorX(0.47142857F, 0.0F), (double)this.decodeAnchorY(1.9565217F, 2.2142856F), (double)this.decodeAnchorX(1.0106384F, -1.7857143F), (double)this.decodeAnchorY(2.5142856F, 0.0F), (double)this.decodeX(1.0106384F), (double)this.decodeY(2.5142856F));
      this.path.lineTo((double)this.decodeX(1.9893618F), (double)this.decodeY(2.5142856F));
      this.path.curveTo((double)this.decodeAnchorX(1.9893618F, 2.0714285F), (double)this.decodeAnchorY(2.5142856F, 0.0F), (double)this.decodeAnchorX(2.5F, 0.0F), (double)this.decodeAnchorY(1.9565217F, 2.2142856F), (double)this.decodeX(2.5F), (double)this.decodeY(1.9565217F));
      this.path.lineTo((double)this.decodeX(2.5142853F), (double)this.decodeY(1.0434783F));
      this.path.curveTo((double)this.decodeAnchorX(2.5142853F, 0.0F), (double)this.decodeAnchorY(1.0434783F, -2.142857F), (double)this.decodeAnchorX(1.9901216F, 2.142857F), (double)this.decodeAnchorY(0.47142857F, 0.0F), (double)this.decodeX(1.9901216F), (double)this.decodeY(0.47142857F));
      this.path.lineTo((double)this.decodeX(1.4148936F), (double)this.decodeY(0.4857143F));
      this.path.lineTo((double)this.decodeX(1.4133738F), (double)this.decodeY(0.120000005F));
      this.path.closePath();
      return this.path;
   }

   private RoundRectangle2D decodeRoundRect1() {
      this.roundRect.setRoundRect((double)this.decodeX(0.4F), (double)this.decodeY(0.6F), (double)(this.decodeX(2.6F) - this.decodeX(0.4F)), (double)(this.decodeY(2.8F) - this.decodeY(0.6F)), 12.0D, 12.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect2() {
      this.roundRect.setRoundRect((double)this.decodeX(0.4F), (double)this.decodeY(0.4F), (double)(this.decodeX(2.6F) - this.decodeX(0.4F)), (double)(this.decodeY(2.6F) - this.decodeY(0.4F)), 12.0D, 12.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect3() {
      this.roundRect.setRoundRect((double)this.decodeX(0.6F), (double)this.decodeY(0.6F), (double)(this.decodeX(2.4F) - this.decodeX(0.6F)), (double)(this.decodeY(2.4F) - this.decodeY(0.6F)), 9.0D, 9.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect4() {
      this.roundRect.setRoundRect((double)this.decodeX(0.120000005F), (double)this.decodeY(0.120000005F), (double)(this.decodeX(2.8800004F) - this.decodeX(0.120000005F)), (double)(this.decodeY(2.8800004F) - this.decodeY(0.120000005F)), 13.0D, 13.0D);
      return this.roundRect;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.09F, 0.52F, 0.95F}, new Color[]{this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.03F, 0.06F, 0.33F, 0.6F, 0.65F, 0.7F, 0.825F, 0.95F, 0.975F, 1.0F}, new Color[]{this.color5, this.decodeColor(this.color5, this.color6, 0.5F), this.color6, this.decodeColor(this.color6, this.color7, 0.5F), this.color7, this.decodeColor(this.color7, this.color7, 0.5F), this.color7, this.decodeColor(this.color7, this.color8, 0.5F), this.color8, this.decodeColor(this.color8, this.color8, 0.5F), this.color8});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.03F, 0.06F, 0.33F, 0.6F, 0.65F, 0.7F, 0.825F, 0.95F, 0.975F, 1.0F}, new Color[]{this.color9, this.decodeColor(this.color9, this.color10, 0.5F), this.color10, this.decodeColor(this.color10, this.color11, 0.5F), this.color11, this.decodeColor(this.color11, this.color11, 0.5F), this.color11, this.decodeColor(this.color11, this.color12, 0.5F), this.color12, this.decodeColor(this.color12, this.color13, 0.5F), this.color13});
   }
}
