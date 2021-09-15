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

final class SpinnerPreviousButtonPainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED = 1;
   static final int BACKGROUND_ENABLED = 2;
   static final int BACKGROUND_FOCUSED = 3;
   static final int BACKGROUND_MOUSEOVER_FOCUSED = 4;
   static final int BACKGROUND_PRESSED_FOCUSED = 5;
   static final int BACKGROUND_MOUSEOVER = 6;
   static final int BACKGROUND_PRESSED = 7;
   static final int FOREGROUND_DISABLED = 8;
   static final int FOREGROUND_ENABLED = 9;
   static final int FOREGROUND_FOCUSED = 10;
   static final int FOREGROUND_MOUSEOVER_FOCUSED = 11;
   static final int FOREGROUND_PRESSED_FOCUSED = 12;
   static final int FOREGROUND_MOUSEOVER = 13;
   static final int FOREGROUND_PRESSED = 14;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBase", 0.015098333F, -0.5557143F, 0.2352941F, 0);
   private Color color2 = this.decodeColor("nimbusBase", 0.010237217F, -0.55799407F, 0.20784312F, 0);
   private Color color3 = this.decodeColor("nimbusBase", 0.018570602F, -0.5821429F, 0.32941175F, 0);
   private Color color4 = this.decodeColor("nimbusBase", 0.021348298F, -0.56722116F, 0.3098039F, 0);
   private Color color5 = this.decodeColor("nimbusBase", 0.021348298F, -0.567841F, 0.31764704F, 0);
   private Color color6 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.0033834577F, -0.30588236F, -148);
   private Color color7 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.2583558F, -0.13333336F, 0);
   private Color color8 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.095173776F, -0.25882354F, 0);
   private Color color9 = this.decodeColor("nimbusBase", 0.004681647F, -0.5383692F, 0.33725488F, 0);
   private Color color10 = this.decodeColor("nimbusBase", -0.0017285943F, -0.44453782F, 0.25098038F, 0);
   private Color color11 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.43866998F, 0.24705881F, 0);
   private Color color12 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.4625541F, 0.35686272F, 0);
   private Color color13 = this.decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
   private Color color14 = this.decodeColor("nimbusBase", 0.0013483167F, 0.088923395F, -0.2784314F, 0);
   private Color color15 = this.decodeColor("nimbusBase", 0.059279382F, 0.3642857F, -0.43529415F, 0);
   private Color color16 = this.decodeColor("nimbusBase", 0.0010585189F, -0.541452F, 0.4078431F, 0);
   private Color color17 = this.decodeColor("nimbusBase", 0.00254488F, -0.4608264F, 0.32549018F, 0);
   private Color color18 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.4555341F, 0.3215686F, 0);
   private Color color19 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.4757143F, 0.43137252F, 0);
   private Color color20 = this.decodeColor("nimbusBase", 0.061133325F, 0.3642857F, -0.427451F, 0);
   private Color color21 = this.decodeColor("nimbusBase", -3.528595E-5F, 0.018606722F, -0.23137257F, 0);
   private Color color22 = this.decodeColor("nimbusBase", 8.354783E-4F, -0.2578073F, 0.12549019F, 0);
   private Color color23 = this.decodeColor("nimbusBase", 8.9377165E-4F, -0.01599598F, 0.007843137F, 0);
   private Color color24 = this.decodeColor("nimbusBase", 0.0F, -0.00895375F, 0.007843137F, 0);
   private Color color25 = this.decodeColor("nimbusBase", 8.9377165E-4F, -0.13853917F, 0.14509803F, 0);
   private Color color26 = this.decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.63529414F, -179);
   private Color color27 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -186);
   private Color color28 = this.decodeColor("nimbusBase", 0.018570602F, -0.56714284F, 0.1372549F, 0);
   private Color color29 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
   private Color color30 = this.decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, 0);
   private Object[] componentColors;

   public SpinnerPreviousButtonPainter(AbstractRegionPainter.PaintContext var1, int var2) {
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
         this.paintBackgroundFocused(var1);
         break;
      case 4:
         this.paintBackgroundMouseOverAndFocused(var1);
         break;
      case 5:
         this.paintBackgroundPressedAndFocused(var1);
         break;
      case 6:
         this.paintBackgroundMouseOver(var1);
         break;
      case 7:
         this.paintBackgroundPressed(var1);
         break;
      case 8:
         this.paintForegroundDisabled(var1);
         break;
      case 9:
         this.paintForegroundEnabled(var1);
         break;
      case 10:
         this.paintForegroundFocused(var1);
         break;
      case 11:
         this.paintForegroundMouseOverAndFocused(var1);
         break;
      case 12:
         this.paintForegroundPressedAndFocused(var1);
         break;
      case 13:
         this.paintForegroundMouseOver(var1);
         break;
      case 14:
         this.paintForegroundPressed(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundDisabled(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient1(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient2(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.path = this.decodePath3();
      var1.setPaint(this.color6);
      var1.fill(this.path);
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient3(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient4(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundFocused(Graphics2D var1) {
      this.path = this.decodePath4();
      var1.setPaint(this.color13);
      var1.fill(this.path);
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient3(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient4(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundMouseOverAndFocused(Graphics2D var1) {
      this.path = this.decodePath5();
      var1.setPaint(this.color13);
      var1.fill(this.path);
      this.path = this.decodePath6();
      var1.setPaint(this.decodeGradient5(this.path));
      var1.fill(this.path);
      this.path = this.decodePath7();
      var1.setPaint(this.decodeGradient6(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundPressedAndFocused(Graphics2D var1) {
      this.path = this.decodePath4();
      var1.setPaint(this.color13);
      var1.fill(this.path);
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient7(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient8(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundMouseOver(Graphics2D var1) {
      this.path = this.decodePath3();
      var1.setPaint(this.color26);
      var1.fill(this.path);
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient5(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient6(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundPressed(Graphics2D var1) {
      this.path = this.decodePath8();
      var1.setPaint(this.color27);
      var1.fill(this.path);
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient7(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient8(this.path));
      var1.fill(this.path);
   }

   private void paintForegroundDisabled(Graphics2D var1) {
      this.path = this.decodePath9();
      var1.setPaint(this.color28);
      var1.fill(this.path);
   }

   private void paintForegroundEnabled(Graphics2D var1) {
      this.path = this.decodePath9();
      var1.setPaint(this.color29);
      var1.fill(this.path);
   }

   private void paintForegroundFocused(Graphics2D var1) {
      this.path = this.decodePath9();
      var1.setPaint(this.color29);
      var1.fill(this.path);
   }

   private void paintForegroundMouseOverAndFocused(Graphics2D var1) {
      this.path = this.decodePath9();
      var1.setPaint(this.color29);
      var1.fill(this.path);
   }

   private void paintForegroundPressedAndFocused(Graphics2D var1) {
      this.path = this.decodePath9();
      var1.setPaint(this.color30);
      var1.fill(this.path);
   }

   private void paintForegroundMouseOver(Graphics2D var1) {
      this.path = this.decodePath9();
      var1.setPaint(this.color29);
      var1.fill(this.path);
   }

   private void paintForegroundPressed(Graphics2D var1) {
      this.path = this.decodePath9();
      var1.setPaint(this.color30);
      var1.fill(this.path);
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.6666667F));
      this.path.lineTo((double)this.decodeX(2.142857F), (double)this.decodeY(2.6666667F));
      this.path.curveTo((double)this.decodeAnchorX(2.142857F, 3.0F), (double)this.decodeAnchorY(2.6666667F, 0.0F), (double)this.decodeAnchorX(2.7142859F, 0.0F), (double)this.decodeAnchorY(2.0F, 2.0F), (double)this.decodeX(2.7142859F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(2.7142859F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(1.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(2.5F));
      this.path.lineTo((double)this.decodeX(2.142857F), (double)this.decodeY(2.5F));
      this.path.curveTo((double)this.decodeAnchorX(2.142857F, 2.0F), (double)this.decodeAnchorY(2.5F, 0.0F), (double)this.decodeAnchorX(2.5714285F, 0.0F), (double)this.decodeAnchorY(2.0F, 1.0F), (double)this.decodeX(2.5714285F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(2.5714285F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(1.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath3() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(2.6666667F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.8333333F));
      this.path.lineTo((double)this.decodeX(2.0324676F), (double)this.decodeY(2.8333333F));
      this.path.curveTo((double)this.decodeAnchorX(2.0324676F, 2.1136363F), (double)this.decodeAnchorY(2.8333333F, 0.0F), (double)this.decodeAnchorX(2.7142859F, 0.0F), (double)this.decodeAnchorY(2.0F, 3.0F), (double)this.decodeX(2.7142859F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.6666667F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath4() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.8999999F));
      this.path.lineTo((double)this.decodeX(2.2F), (double)this.decodeY(2.8999999F));
      this.path.curveTo((double)this.decodeAnchorX(2.2F, 3.0F), (double)this.decodeAnchorY(2.8999999F, 0.0F), (double)this.decodeAnchorX(2.9142857F, 0.0F), (double)this.decodeAnchorY(2.2333333F, 3.0F), (double)this.decodeX(2.9142857F), (double)this.decodeY(2.2333333F));
      this.path.lineTo((double)this.decodeX(2.9142857F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(1.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath5() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.8999999F));
      this.path.lineTo((double)this.decodeX(2.2F), (double)this.decodeY(2.8999999F));
      this.path.curveTo((double)this.decodeAnchorX(2.2F, 3.0F), (double)this.decodeAnchorY(2.8999999F, 0.0F), (double)this.decodeAnchorX(2.9142857F, 0.0F), (double)this.decodeAnchorY(2.2333333F, 3.0F), (double)this.decodeX(2.9142857F), (double)this.decodeY(2.2333333F));
      this.path.lineTo((double)this.decodeX(2.9142857F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath6() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.6666667F));
      this.path.lineTo((double)this.decodeX(2.142857F), (double)this.decodeY(2.6666667F));
      this.path.curveTo((double)this.decodeAnchorX(2.142857F, 3.0F), (double)this.decodeAnchorY(2.6666667F, 0.0F), (double)this.decodeAnchorX(2.7142859F, 0.0F), (double)this.decodeAnchorY(2.0F, 2.0F), (double)this.decodeX(2.7142859F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(2.7142859F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath7() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(2.5F));
      this.path.lineTo((double)this.decodeX(2.142857F), (double)this.decodeY(2.5F));
      this.path.curveTo((double)this.decodeAnchorX(2.142857F, 2.0F), (double)this.decodeAnchorY(2.5F, 0.0F), (double)this.decodeAnchorX(2.5714285F, 0.0F), (double)this.decodeAnchorY(2.0F, 1.0F), (double)this.decodeX(2.5714285F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(2.5714285F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(0.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath8() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(2.6666667F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.8333333F));
      this.path.curveTo((double)this.decodeAnchorX(0.0F, 0.0F), (double)this.decodeAnchorY(2.8333333F, 0.0F), (double)this.decodeAnchorX(2.0324676F, -2.1136363F), (double)this.decodeAnchorY(2.8333333F, 0.0F), (double)this.decodeX(2.0324676F), (double)this.decodeY(2.8333333F));
      this.path.curveTo((double)this.decodeAnchorX(2.0324676F, 2.1136363F), (double)this.decodeAnchorY(2.8333333F, 0.0F), (double)this.decodeAnchorX(2.7142859F, 0.0F), (double)this.decodeAnchorY(2.0F, 3.0F), (double)this.decodeX(2.7142859F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.6666667F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath9() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.5045455F), (double)this.decodeY(1.9943181F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(1.0F));
      this.path.closePath();
      return this.path;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color1, this.decodeColor(this.color1, this.color2, 0.5F), this.color2});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.05748663F, 0.11497326F, 0.55748665F, 1.0F}, new Color[]{this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4, this.decodeColor(this.color4, this.color5, 0.5F), this.color5});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color7, this.decodeColor(this.color7, this.color8, 0.5F), this.color8});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.05748663F, 0.11497326F, 0.2419786F, 0.36898395F, 0.684492F, 1.0F}, new Color[]{this.color9, this.decodeColor(this.color9, this.color10, 0.5F), this.color10, this.decodeColor(this.color10, this.color11, 0.5F), this.color11, this.decodeColor(this.color11, this.color12, 0.5F), this.color12});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color14, this.decodeColor(this.color14, this.color15, 0.5F), this.color15});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.05748663F, 0.11497326F, 0.2419786F, 0.36898395F, 0.684492F, 1.0F}, new Color[]{this.color16, this.decodeColor(this.color16, this.color17, 0.5F), this.color17, this.decodeColor(this.color17, this.color18, 0.5F), this.color18, this.decodeColor(this.color18, this.color19, 0.5F), this.color19});
   }

   private Paint decodeGradient7(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color20, this.decodeColor(this.color20, this.color21, 0.5F), this.color21});
   }

   private Paint decodeGradient8(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.05748663F, 0.11497326F, 0.2419786F, 0.36898395F, 0.684492F, 1.0F}, new Color[]{this.color22, this.decodeColor(this.color22, this.color23, 0.5F), this.color23, this.decodeColor(this.color23, this.color24, 0.5F), this.color24, this.decodeColor(this.color24, this.color25, 0.5F), this.color25});
   }
}
