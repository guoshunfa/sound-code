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

final class ToolBarToggleButtonPainter extends AbstractRegionPainter {
   static final int BACKGROUND_ENABLED = 1;
   static final int BACKGROUND_FOCUSED = 2;
   static final int BACKGROUND_MOUSEOVER = 3;
   static final int BACKGROUND_MOUSEOVER_FOCUSED = 4;
   static final int BACKGROUND_PRESSED = 5;
   static final int BACKGROUND_PRESSED_FOCUSED = 6;
   static final int BACKGROUND_SELECTED = 7;
   static final int BACKGROUND_SELECTED_FOCUSED = 8;
   static final int BACKGROUND_PRESSED_SELECTED = 9;
   static final int BACKGROUND_PRESSED_SELECTED_FOCUSED = 10;
   static final int BACKGROUND_MOUSEOVER_SELECTED = 11;
   static final int BACKGROUND_MOUSEOVER_SELECTED_FOCUSED = 12;
   static final int BACKGROUND_DISABLED_SELECTED = 13;
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
   private Color color9 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -86);
   private Color color10 = this.decodeColor("nimbusBlueGrey", -0.01111114F, -0.060526315F, -0.3529412F, 0);
   private Color color11 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.064372465F, -0.2352941F, 0);
   private Color color12 = this.decodeColor("nimbusBlueGrey", -0.006944418F, -0.0595709F, -0.12941176F, 0);
   private Color color13 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.061075766F, -0.031372547F, 0);
   private Color color14 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06080256F, -0.035294116F, 0);
   private Color color15 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06472479F, -0.23137254F, 0);
   private Color color16 = this.decodeColor("nimbusBlueGrey", 0.007936537F, -0.06959064F, -0.0745098F, 0);
   private Color color17 = this.decodeColor("nimbusBlueGrey", 0.0138888955F, -0.06401469F, -0.07058823F, 0);
   private Color color18 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06530018F, 0.035294116F, 0);
   private Color color19 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06507177F, 0.031372547F, 0);
   private Color color20 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.05338346F, -0.47058824F, 0);
   private Color color21 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.049301825F, -0.36078432F, 0);
   private Color color22 = this.decodeColor("nimbusBlueGrey", -0.018518567F, -0.03909774F, -0.2509804F, 0);
   private Color color23 = this.decodeColor("nimbusBlueGrey", -0.00505054F, -0.040013492F, -0.13333333F, 0);
   private Color color24 = this.decodeColor("nimbusBlueGrey", 0.01010108F, -0.039558575F, -0.1372549F, 0);
   private Color color25 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -220);
   private Color color26 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.066408664F, 0.054901958F, 0);
   private Color color27 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06807348F, 0.086274505F, 0);
   private Color color28 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06924191F, 0.109803915F, 0);
   private Object[] componentColors;

   public ToolBarToggleButtonPainter(AbstractRegionPainter.PaintContext var1, int var2) {
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
         break;
      case 7:
         this.paintBackgroundSelected(var1);
         break;
      case 8:
         this.paintBackgroundSelectedAndFocused(var1);
         break;
      case 9:
         this.paintBackgroundPressedAndSelected(var1);
         break;
      case 10:
         this.paintBackgroundPressedAndSelectedAndFocused(var1);
         break;
      case 11:
         this.paintBackgroundMouseOverAndSelected(var1);
         break;
      case 12:
         this.paintBackgroundMouseOverAndSelectedAndFocused(var1);
         break;
      case 13:
         this.paintBackgroundDisabledAndSelected(var1);
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
      this.roundRect = this.decodeRoundRect5();
      var1.setPaint(this.color9);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect6();
      var1.setPaint(this.decodeGradient3(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect7();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundPressedAndFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect8();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect6();
      var1.setPaint(this.decodeGradient3(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect7();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundSelected(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect5();
      var1.setPaint(this.color9);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect6();
      var1.setPaint(this.decodeGradient5(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect7();
      var1.setPaint(this.decodeGradient6(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundSelectedAndFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect8();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect6();
      var1.setPaint(this.decodeGradient5(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect7();
      var1.setPaint(this.decodeGradient6(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundPressedAndSelected(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect5();
      var1.setPaint(this.color9);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect6();
      var1.setPaint(this.decodeGradient7(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect7();
      var1.setPaint(this.decodeGradient8(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundPressedAndSelectedAndFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect8();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect6();
      var1.setPaint(this.decodeGradient7(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect7();
      var1.setPaint(this.decodeGradient8(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundMouseOverAndSelected(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect5();
      var1.setPaint(this.color9);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect6();
      var1.setPaint(this.decodeGradient3(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect7();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundMouseOverAndSelectedAndFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect8();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect6();
      var1.setPaint(this.decodeGradient3(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect7();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundDisabledAndSelected(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect5();
      var1.setPaint(this.color25);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect6();
      var1.setPaint(this.decodeGradient9(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect7();
      var1.setPaint(this.decodeGradient10(this.roundRect));
      var1.fill(this.roundRect);
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.4133738F), (double)this.decodeY(0.120000005F));
      this.path.lineTo((double)this.decodeX(1.9893618F), (double)this.decodeY(0.120000005F));
      this.path.curveTo((double)this.decodeAnchorX(1.9893618F, 3.0F), (double)this.decodeAnchorY(0.120000005F, 0.0F), (double)this.decodeAnchorX(2.8857148F, 0.0F), (double)this.decodeAnchorY(1.0416666F, -3.0F), (double)this.decodeX(2.8857148F), (double)this.decodeY(1.0416666F));
      this.path.lineTo((double)this.decodeX(2.9F), (double)this.decodeY(1.9166667F));
      this.path.curveTo((double)this.decodeAnchorX(2.9F, 0.0F), (double)this.decodeAnchorY(1.9166667F, 3.0F), (double)this.decodeAnchorX(1.9893618F, 3.0F), (double)this.decodeAnchorY(2.6714287F, 0.0F), (double)this.decodeX(1.9893618F), (double)this.decodeY(2.6714287F));
      this.path.lineTo((double)this.decodeX(1.0106384F), (double)this.decodeY(2.6714287F));
      this.path.curveTo((double)this.decodeAnchorX(1.0106384F, -3.0F), (double)this.decodeAnchorY(2.6714287F, 0.0F), (double)this.decodeAnchorX(0.120000005F, 0.0F), (double)this.decodeAnchorY(1.9166667F, 3.0F), (double)this.decodeX(0.120000005F), (double)this.decodeY(1.9166667F));
      this.path.lineTo((double)this.decodeX(0.120000005F), (double)this.decodeY(1.0446429F));
      this.path.curveTo((double)this.decodeAnchorX(0.120000005F, 0.0F), (double)this.decodeAnchorY(1.0446429F, -3.0F), (double)this.decodeAnchorX(1.0106384F, -3.0F), (double)this.decodeAnchorY(0.120000005F, 0.0F), (double)this.decodeX(1.0106384F), (double)this.decodeY(0.120000005F));
      this.path.lineTo((double)this.decodeX(1.4148936F), (double)this.decodeY(0.120000005F));
      this.path.lineTo((double)this.decodeX(1.4148936F), (double)this.decodeY(0.4857143F));
      this.path.lineTo((double)this.decodeX(1.0106384F), (double)this.decodeY(0.4857143F));
      this.path.curveTo((double)this.decodeAnchorX(1.0106384F, -1.9285715F), (double)this.decodeAnchorY(0.4857143F, 0.0F), (double)this.decodeAnchorX(0.47142857F, -0.044279482F), (double)this.decodeAnchorY(1.0386904F, -2.429218F), (double)this.decodeX(0.47142857F), (double)this.decodeY(1.0386904F));
      this.path.lineTo((double)this.decodeX(0.47142857F), (double)this.decodeY(1.9166667F));
      this.path.curveTo((double)this.decodeAnchorX(0.47142857F, 0.0F), (double)this.decodeAnchorY(1.9166667F, 2.2142856F), (double)this.decodeAnchorX(1.0106384F, -1.7857143F), (double)this.decodeAnchorY(2.3142858F, 0.0F), (double)this.decodeX(1.0106384F), (double)this.decodeY(2.3142858F));
      this.path.lineTo((double)this.decodeX(1.9893618F), (double)this.decodeY(2.3142858F));
      this.path.curveTo((double)this.decodeAnchorX(1.9893618F, 2.0714285F), (double)this.decodeAnchorY(2.3142858F, 0.0F), (double)this.decodeAnchorX(2.5F, 0.0F), (double)this.decodeAnchorY(1.9166667F, 2.2142856F), (double)this.decodeX(2.5F), (double)this.decodeY(1.9166667F));
      this.path.lineTo((double)this.decodeX(2.5142853F), (double)this.decodeY(1.0416666F));
      this.path.curveTo((double)this.decodeAnchorX(2.5142853F, 0.0F), (double)this.decodeAnchorY(1.0416666F, -2.142857F), (double)this.decodeAnchorX(1.9901216F, 2.142857F), (double)this.decodeAnchorY(0.47142857F, 0.0F), (double)this.decodeX(1.9901216F), (double)this.decodeY(0.47142857F));
      this.path.lineTo((double)this.decodeX(1.4148936F), (double)this.decodeY(0.4857143F));
      this.path.lineTo((double)this.decodeX(1.4133738F), (double)this.decodeY(0.120000005F));
      this.path.closePath();
      return this.path;
   }

   private RoundRectangle2D decodeRoundRect1() {
      this.roundRect.setRoundRect((double)this.decodeX(0.4F), (double)this.decodeY(0.6F), (double)(this.decodeX(2.6F) - this.decodeX(0.4F)), (double)(this.decodeY(2.6F) - this.decodeY(0.6F)), 12.0D, 12.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect2() {
      this.roundRect.setRoundRect((double)this.decodeX(0.4F), (double)this.decodeY(0.4F), (double)(this.decodeX(2.6F) - this.decodeX(0.4F)), (double)(this.decodeY(2.4F) - this.decodeY(0.4F)), 12.0D, 12.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect3() {
      this.roundRect.setRoundRect((double)this.decodeX(0.6F), (double)this.decodeY(0.6F), (double)(this.decodeX(2.4F) - this.decodeX(0.6F)), (double)(this.decodeY(2.2F) - this.decodeY(0.6F)), 9.0D, 9.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect4() {
      this.roundRect.setRoundRect((double)this.decodeX(0.120000005F), (double)this.decodeY(0.120000005F), (double)(this.decodeX(2.8800004F) - this.decodeX(0.120000005F)), (double)(this.decodeY(2.6800003F) - this.decodeY(0.120000005F)), 13.0D, 13.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect5() {
      this.roundRect.setRoundRect((double)this.decodeX(0.4F), (double)this.decodeY(0.6F), (double)(this.decodeX(2.6F) - this.decodeX(0.4F)), (double)(this.decodeY(2.6F) - this.decodeY(0.6F)), 10.0D, 10.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect6() {
      this.roundRect.setRoundRect((double)this.decodeX(0.4F), (double)this.decodeY(0.4F), (double)(this.decodeX(2.6F) - this.decodeX(0.4F)), (double)(this.decodeY(2.4F) - this.decodeY(0.4F)), 10.0D, 10.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect7() {
      this.roundRect.setRoundRect((double)this.decodeX(0.6F), (double)this.decodeY(0.6F), (double)(this.decodeX(2.4F) - this.decodeX(0.6F)), (double)(this.decodeY(2.2F) - this.decodeY(0.6F)), 8.0D, 8.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect8() {
      this.roundRect.setRoundRect((double)this.decodeX(0.120000005F), (double)this.decodeY(0.120000005F), (double)(this.decodeX(2.8800004F) - this.decodeX(0.120000005F)), (double)(this.decodeY(2.6799998F) - this.decodeY(0.120000005F)), 13.0D, 13.0D);
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
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25F * var5 + var3, 1.0041667F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color10, this.decodeColor(this.color10, this.color11, 0.5F), this.color11});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25126263F * var5 + var3, 1.0092592F * var6 + var4, new float[]{0.0F, 0.06684492F, 0.13368984F, 0.56684494F, 1.0F}, new Color[]{this.color12, this.decodeColor(this.color12, this.color13, 0.5F), this.color13, this.decodeColor(this.color13, this.color14, 0.5F), this.color14});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25F * var5 + var3, 1.0041667F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color15, this.decodeColor(this.color15, this.color16, 0.5F), this.color16});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25126263F * var5 + var3, 1.0092592F * var6 + var4, new float[]{0.0F, 0.06684492F, 0.13368984F, 0.56684494F, 1.0F}, new Color[]{this.color17, this.decodeColor(this.color17, this.color18, 0.5F), this.color18, this.decodeColor(this.color18, this.color19, 0.5F), this.color19});
   }

   private Paint decodeGradient7(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25F * var5 + var3, 1.0041667F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color20, this.decodeColor(this.color20, this.color21, 0.5F), this.color21});
   }

   private Paint decodeGradient8(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25126263F * var5 + var3, 1.0092592F * var6 + var4, new float[]{0.0F, 0.06684492F, 0.13368984F, 0.56684494F, 1.0F}, new Color[]{this.color22, this.decodeColor(this.color22, this.color23, 0.5F), this.color23, this.decodeColor(this.color23, this.color24, 0.5F), this.color24});
   }

   private Paint decodeGradient9(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25F * var5 + var3, 1.0041667F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color26, this.decodeColor(this.color26, this.color27, 0.5F), this.color27});
   }

   private Paint decodeGradient10(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25126263F * var5 + var3, 1.0092592F * var6 + var4, new float[]{0.0F, 0.06684492F, 0.13368984F, 0.56684494F, 1.0F}, new Color[]{this.color27, this.decodeColor(this.color27, this.color28, 0.5F), this.color28, this.decodeColor(this.color28, this.color28, 0.5F), this.color28});
   }
}
