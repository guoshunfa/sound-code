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

final class ComboBoxArrowButtonPainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED = 1;
   static final int BACKGROUND_ENABLED = 2;
   static final int BACKGROUND_ENABLED_MOUSEOVER = 3;
   static final int BACKGROUND_ENABLED_PRESSED = 4;
   static final int BACKGROUND_DISABLED_EDITABLE = 5;
   static final int BACKGROUND_ENABLED_EDITABLE = 6;
   static final int BACKGROUND_MOUSEOVER_EDITABLE = 7;
   static final int BACKGROUND_PRESSED_EDITABLE = 8;
   static final int BACKGROUND_SELECTED_EDITABLE = 9;
   static final int FOREGROUND_ENABLED = 10;
   static final int FOREGROUND_MOUSEOVER = 11;
   static final int FOREGROUND_DISABLED = 12;
   static final int FOREGROUND_PRESSED = 13;
   static final int FOREGROUND_SELECTED = 14;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.74509805F, -247);
   private Color color2 = this.decodeColor("nimbusBase", 0.021348298F, -0.56289876F, 0.2588235F, 0);
   private Color color3 = this.decodeColor("nimbusBase", 0.010237217F, -0.55799407F, 0.20784312F, 0);
   private Color color4 = new Color(255, 200, 0, 255);
   private Color color5 = this.decodeColor("nimbusBase", 0.021348298F, -0.59223604F, 0.35294116F, 0);
   private Color color6 = this.decodeColor("nimbusBase", 0.02391243F, -0.5774183F, 0.32549018F, 0);
   private Color color7 = this.decodeColor("nimbusBase", 0.021348298F, -0.56722116F, 0.3098039F, 0);
   private Color color8 = this.decodeColor("nimbusBase", 0.021348298F, -0.567841F, 0.31764704F, 0);
   private Color color9 = this.decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.74509805F, -191);
   private Color color10 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.34585923F, -0.007843137F, 0);
   private Color color11 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.095173776F, -0.25882354F, 0);
   private Color color12 = this.decodeColor("nimbusBase", 0.004681647F, -0.6197143F, 0.43137252F, 0);
   private Color color13 = this.decodeColor("nimbusBase", 0.0023007393F, -0.46825016F, 0.27058822F, 0);
   private Color color14 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.43866998F, 0.24705881F, 0);
   private Color color15 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.4625541F, 0.35686272F, 0);
   private Color color16 = this.decodeColor("nimbusBase", 0.0013483167F, -0.1769987F, -0.12156865F, 0);
   private Color color17 = this.decodeColor("nimbusBase", 0.059279382F, 0.3642857F, -0.43529415F, 0);
   private Color color18 = this.decodeColor("nimbusBase", 0.004681647F, -0.6198413F, 0.43921566F, 0);
   private Color color19 = this.decodeColor("nimbusBase", 0.0023007393F, -0.48084703F, 0.33725488F, 0);
   private Color color20 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.4555341F, 0.3215686F, 0);
   private Color color21 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.4757143F, 0.43137252F, 0);
   private Color color22 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
   private Color color23 = this.decodeColor("nimbusBase", -3.528595E-5F, 0.018606722F, -0.23137257F, 0);
   private Color color24 = this.decodeColor("nimbusBase", -4.2033195E-4F, -0.38050595F, 0.20392156F, 0);
   private Color color25 = this.decodeColor("nimbusBase", 7.13408E-4F, -0.064285696F, 0.027450979F, 0);
   private Color color26 = this.decodeColor("nimbusBase", 0.0F, -0.00895375F, 0.007843137F, 0);
   private Color color27 = this.decodeColor("nimbusBase", 8.9377165E-4F, -0.13853917F, 0.14509803F, 0);
   private Color color28 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.37254906F, 0);
   private Color color29 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.5254902F, 0);
   private Color color30 = this.decodeColor("nimbusBase", 0.027408898F, -0.57391655F, 0.1490196F, 0);
   private Color color31 = this.decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, 0);
   private Object[] componentColors;

   public ComboBoxArrowButtonPainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 5:
         this.paintBackgroundDisabledAndEditable(var1);
         break;
      case 6:
         this.paintBackgroundEnabledAndEditable(var1);
         break;
      case 7:
         this.paintBackgroundMouseOverAndEditable(var1);
         break;
      case 8:
         this.paintBackgroundPressedAndEditable(var1);
         break;
      case 9:
         this.paintBackgroundSelectedAndEditable(var1);
         break;
      case 10:
         this.paintForegroundEnabled(var1);
         break;
      case 11:
         this.paintForegroundMouseOver(var1);
         break;
      case 12:
         this.paintForegroundDisabled(var1);
         break;
      case 13:
         this.paintForegroundPressed(var1);
         break;
      case 14:
         this.paintForegroundSelected(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundDisabledAndEditable(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color1);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient1(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.color4);
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.decodeGradient2(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundEnabledAndEditable(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color9);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient3(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.color4);
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.decodeGradient4(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundMouseOverAndEditable(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color9);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient5(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.color4);
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.decodeGradient6(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundPressedAndEditable(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color9);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient7(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.color4);
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.decodeGradient8(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundSelectedAndEditable(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color9);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient7(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.color4);
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.decodeGradient8(this.path));
      var1.fill(this.path);
   }

   private void paintForegroundEnabled(Graphics2D var1) {
      this.path = this.decodePath5();
      var1.setPaint(this.decodeGradient9(this.path));
      var1.fill(this.path);
   }

   private void paintForegroundMouseOver(Graphics2D var1) {
      this.path = this.decodePath6();
      var1.setPaint(this.decodeGradient9(this.path));
      var1.fill(this.path);
   }

   private void paintForegroundDisabled(Graphics2D var1) {
      this.path = this.decodePath7();
      var1.setPaint(this.color30);
      var1.fill(this.path);
   }

   private void paintForegroundPressed(Graphics2D var1) {
      this.path = this.decodePath8();
      var1.setPaint(this.color31);
      var1.fill(this.path);
   }

   private void paintForegroundSelected(Graphics2D var1) {
      this.path = this.decodePath7();
      var1.setPaint(this.color31);
      var1.fill(this.path);
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(2.75F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(2.75F), (double)this.decodeY(2.25F));
      this.path.curveTo((double)this.decodeAnchorX(2.75F, 0.0F), (double)this.decodeAnchorY(2.25F, 4.0F), (double)this.decodeAnchorX(2.125F, 3.0F), (double)this.decodeAnchorY(2.875F, 0.0F), (double)this.decodeX(2.125F), (double)this.decodeY(2.875F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.875F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(0.25F));
      this.path.lineTo((double)this.decodeX(2.125F), (double)this.decodeY(0.25F));
      this.path.curveTo((double)this.decodeAnchorX(2.125F, 3.0F), (double)this.decodeAnchorY(0.25F, 0.0F), (double)this.decodeAnchorX(2.75F, 0.0F), (double)this.decodeAnchorY(0.875F, -3.0F), (double)this.decodeX(2.75F), (double)this.decodeY(0.875F));
      this.path.lineTo((double)this.decodeX(2.75F), (double)this.decodeY(2.125F));
      this.path.curveTo((double)this.decodeAnchorX(2.75F, 0.0F), (double)this.decodeAnchorY(2.125F, 3.0F), (double)this.decodeAnchorX(2.125F, 3.0F), (double)this.decodeAnchorY(2.75F, 0.0F), (double)this.decodeX(2.125F), (double)this.decodeY(2.75F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.75F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.25F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath3() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.85294116F), (double)this.decodeY(2.639706F));
      this.path.lineTo((double)this.decodeX(0.85294116F), (double)this.decodeY(2.639706F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath4() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0F), (double)this.decodeY(0.375F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(0.375F));
      this.path.curveTo((double)this.decodeAnchorX(2.0F, 4.0F), (double)this.decodeAnchorY(0.375F, 0.0F), (double)this.decodeAnchorX(2.625F, 0.0F), (double)this.decodeAnchorY(1.0F, -4.0F), (double)this.decodeX(2.625F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.625F), (double)this.decodeY(2.0F));
      this.path.curveTo((double)this.decodeAnchorX(2.625F, 0.0F), (double)this.decodeAnchorY(2.0F, 4.0F), (double)this.decodeAnchorX(2.0F, 4.0F), (double)this.decodeAnchorY(2.625F, 0.0F), (double)this.decodeX(2.0F), (double)this.decodeY(2.625F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(2.625F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(0.375F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath5() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.9995915F), (double)this.decodeY(1.3616071F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(0.8333333F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(1.8571429F));
      this.path.lineTo((double)this.decodeX(0.9995915F), (double)this.decodeY(1.3616071F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath6() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.00625F), (double)this.decodeY(1.3526785F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(0.8333333F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(1.8571429F));
      this.path.lineTo((double)this.decodeX(1.00625F), (double)this.decodeY(1.3526785F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath7() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0117648F), (double)this.decodeY(1.3616071F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(0.8333333F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(1.8571429F));
      this.path.lineTo((double)this.decodeX(1.0117648F), (double)this.decodeY(1.3616071F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath8() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0242647F), (double)this.decodeY(1.3526785F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(0.8333333F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(1.8571429F));
      this.path.lineTo((double)this.decodeX(1.0242647F), (double)this.decodeY(1.3526785F));
      this.path.closePath();
      return this.path;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color2, this.decodeColor(this.color2, this.color3, 0.5F), this.color3});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.171875F, 0.34375F, 0.4815341F, 0.6193182F, 0.8096591F, 1.0F}, new Color[]{this.color5, this.decodeColor(this.color5, this.color6, 0.5F), this.color6, this.decodeColor(this.color6, this.color7, 0.5F), this.color7, this.decodeColor(this.color7, this.color8, 0.5F), this.color8});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color10, this.decodeColor(this.color10, this.color11, 0.5F), this.color11});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.12299465F, 0.44652405F, 0.5441176F, 0.64171124F, 0.8208556F, 1.0F}, new Color[]{this.color12, this.decodeColor(this.color12, this.color13, 0.5F), this.color13, this.decodeColor(this.color13, this.color14, 0.5F), this.color14, this.decodeColor(this.color14, this.color15, 0.5F), this.color15});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color16, this.decodeColor(this.color16, this.color17, 0.5F), this.color17});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.12299465F, 0.44652405F, 0.5441176F, 0.64171124F, 0.81283426F, 0.98395723F}, new Color[]{this.color18, this.decodeColor(this.color18, this.color19, 0.5F), this.color19, this.decodeColor(this.color19, this.color20, 0.5F), this.color20, this.decodeColor(this.color20, this.color21, 0.5F), this.color21});
   }

   private Paint decodeGradient7(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color22, this.decodeColor(this.color22, this.color23, 0.5F), this.color23});
   }

   private Paint decodeGradient8(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.12299465F, 0.44652405F, 0.5441176F, 0.64171124F, 0.8208556F, 1.0F}, new Color[]{this.color24, this.decodeColor(this.color24, this.color25, 0.5F), this.color25, this.decodeColor(this.color25, this.color26, 0.5F), this.color26, this.decodeColor(this.color26, this.color27, 0.5F), this.color27});
   }

   private Paint decodeGradient9(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(1.0F * var5 + var3, 0.5F * var6 + var4, 0.0F * var5 + var3, 0.5F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color28, this.decodeColor(this.color28, this.color29, 0.5F), this.color29});
   }
}
