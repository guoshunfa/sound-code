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

final class ProgressBarPainter extends AbstractRegionPainter {
   static final int BACKGROUND_ENABLED = 1;
   static final int BACKGROUND_DISABLED = 2;
   static final int FOREGROUND_ENABLED = 3;
   static final int FOREGROUND_ENABLED_FINISHED = 4;
   static final int FOREGROUND_ENABLED_INDETERMINATE = 5;
   static final int FOREGROUND_DISABLED = 6;
   static final int FOREGROUND_DISABLED_FINISHED = 7;
   static final int FOREGROUND_DISABLED_INDETERMINATE = 8;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.04845735F, -0.17647058F, 0);
   private Color color2 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.061345987F, -0.027450979F, 0);
   private Color color3 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
   private Color color4 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.097921275F, 0.18823528F, 0);
   private Color color5 = this.decodeColor("nimbusBlueGrey", 0.0138888955F, -0.0925083F, 0.12549019F, 0);
   private Color color6 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.08222443F, 0.086274505F, 0);
   private Color color7 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.08477524F, 0.16862744F, 0);
   private Color color8 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.086996906F, 0.25490195F, 0);
   private Color color9 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.061613273F, -0.02352941F, 0);
   private Color color10 = this.decodeColor("nimbusBlueGrey", -0.01111114F, -0.061265234F, 0.05098039F, 0);
   private Color color11 = this.decodeColor("nimbusBlueGrey", 0.0138888955F, -0.09378991F, 0.19215685F, 0);
   private Color color12 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.08455229F, 0.1607843F, 0);
   private Color color13 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.08362049F, 0.12941176F, 0);
   private Color color14 = this.decodeColor("nimbusBlueGrey", 0.007936537F, -0.07826825F, 0.10588235F, 0);
   private Color color15 = this.decodeColor("nimbusBlueGrey", 0.007936537F, -0.07982456F, 0.1490196F, 0);
   private Color color16 = this.decodeColor("nimbusBlueGrey", 0.007936537F, -0.08099045F, 0.18431371F, 0);
   private Color color17 = this.decodeColor("nimbusOrange", 0.0F, 0.0F, 0.0F, -156);
   private Color color18 = this.decodeColor("nimbusOrange", -0.015796512F, 0.02094239F, -0.15294117F, 0);
   private Color color19 = this.decodeColor("nimbusOrange", -0.004321605F, 0.02094239F, -0.0745098F, 0);
   private Color color20 = this.decodeColor("nimbusOrange", -0.008021399F, 0.02094239F, -0.10196078F, 0);
   private Color color21 = this.decodeColor("nimbusOrange", -0.011706904F, -0.1790576F, -0.02352941F, 0);
   private Color color22 = this.decodeColor("nimbusOrange", -0.048691254F, 0.02094239F, -0.3019608F, 0);
   private Color color23 = this.decodeColor("nimbusOrange", 0.003940329F, -0.7375322F, 0.17647058F, 0);
   private Color color24 = this.decodeColor("nimbusOrange", 0.005506739F, -0.46764207F, 0.109803915F, 0);
   private Color color25 = this.decodeColor("nimbusOrange", 0.0042127445F, -0.18595415F, 0.04705882F, 0);
   private Color color26 = this.decodeColor("nimbusOrange", 0.0047626942F, 0.02094239F, 0.0039215684F, 0);
   private Color color27 = this.decodeColor("nimbusOrange", 0.0047626942F, -0.15147138F, 0.1607843F, 0);
   private Color color28 = this.decodeColor("nimbusOrange", 0.010665476F, -0.27317524F, 0.25098038F, 0);
   private Color color29 = this.decodeColor("nimbusBlueGrey", -0.54444444F, -0.08748484F, 0.10588235F, 0);
   private Color color30 = this.decodeColor("nimbusOrange", 0.0047626942F, -0.21715283F, 0.23921567F, 0);
   private Color color31 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -173);
   private Color color32 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -170);
   private Color color33 = this.decodeColor("nimbusOrange", 0.024554357F, -0.8873145F, 0.10588235F, -156);
   private Color color34 = this.decodeColor("nimbusOrange", -0.023593787F, -0.7963165F, 0.02352941F, 0);
   private Color color35 = this.decodeColor("nimbusOrange", -0.010608241F, -0.7760873F, 0.043137252F, 0);
   private Color color36 = this.decodeColor("nimbusOrange", -0.015402906F, -0.7840576F, 0.035294116F, 0);
   private Color color37 = this.decodeColor("nimbusOrange", -0.017112307F, -0.8091547F, 0.058823526F, 0);
   private Color color38 = this.decodeColor("nimbusOrange", -0.07044564F, -0.844649F, -0.019607842F, 0);
   private Color color39 = this.decodeColor("nimbusOrange", -0.009704903F, -0.9381485F, 0.11372548F, 0);
   private Color color40 = this.decodeColor("nimbusOrange", -4.4563413E-4F, -0.86742973F, 0.09411764F, 0);
   private Color color41 = this.decodeColor("nimbusOrange", -4.4563413E-4F, -0.79896283F, 0.07843137F, 0);
   private Color color42 = this.decodeColor("nimbusOrange", 0.0013274103F, -0.7530961F, 0.06666666F, 0);
   private Color color43 = this.decodeColor("nimbusOrange", 0.0013274103F, -0.7644457F, 0.109803915F, 0);
   private Color color44 = this.decodeColor("nimbusOrange", 0.009244293F, -0.78794646F, 0.13333333F, 0);
   private Color color45 = this.decodeColor("nimbusBlueGrey", -0.015872955F, -0.0803539F, 0.16470587F, 0);
   private Color color46 = this.decodeColor("nimbusBlueGrey", 0.007936537F, -0.07968931F, 0.14509803F, 0);
   private Color color47 = this.decodeColor("nimbusBlueGrey", 0.02222228F, -0.08779904F, 0.11764705F, 0);
   private Color color48 = this.decodeColor("nimbusBlueGrey", 0.0138888955F, -0.075128086F, 0.14117646F, 0);
   private Color color49 = this.decodeColor("nimbusBlueGrey", 0.0138888955F, -0.07604356F, 0.16470587F, 0);
   private Color color50 = this.decodeColor("nimbusOrange", 0.0014062226F, -0.77816474F, 0.12941176F, 0);
   private Object[] componentColors;

   public ProgressBarPainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 1:
         this.paintBackgroundEnabled(var1);
         break;
      case 2:
         this.paintBackgroundDisabled(var1);
         break;
      case 3:
         this.paintForegroundEnabled(var1);
         break;
      case 4:
         this.paintForegroundEnabledAndFinished(var1);
         break;
      case 5:
         this.paintForegroundEnabledAndIndeterminate(var1);
         break;
      case 6:
         this.paintForegroundDisabled(var1);
         break;
      case 7:
         this.paintForegroundDisabledAndFinished(var1);
         break;
      case 8:
         this.paintForegroundDisabledAndIndeterminate(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.decodeGradient1(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient2(this.rect));
      var1.fill(this.rect);
   }

   private void paintBackgroundDisabled(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.decodeGradient3(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient4(this.rect));
      var1.fill(this.rect);
   }

   private void paintForegroundEnabled(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color17);
      var1.fill(this.path);
      this.rect = this.decodeRect3();
      var1.setPaint(this.decodeGradient5(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.decodeGradient6(this.rect));
      var1.fill(this.rect);
   }

   private void paintForegroundEnabledAndFinished(Graphics2D var1) {
      this.path = this.decodePath2();
      var1.setPaint(this.color17);
      var1.fill(this.path);
      this.rect = this.decodeRect5();
      var1.setPaint(this.decodeGradient5(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect6();
      var1.setPaint(this.decodeGradient6(this.rect));
      var1.fill(this.rect);
   }

   private void paintForegroundEnabledAndIndeterminate(Graphics2D var1) {
      this.rect = this.decodeRect7();
      var1.setPaint(this.decodeGradient7(this.rect));
      var1.fill(this.rect);
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient8(this.path));
      var1.fill(this.path);
      this.rect = this.decodeRect8();
      var1.setPaint(this.color31);
      var1.fill(this.rect);
      this.rect = this.decodeRect9();
      var1.setPaint(this.color32);
      var1.fill(this.rect);
   }

   private void paintForegroundDisabled(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color33);
      var1.fill(this.path);
      this.rect = this.decodeRect3();
      var1.setPaint(this.decodeGradient9(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.decodeGradient10(this.rect));
      var1.fill(this.rect);
   }

   private void paintForegroundDisabledAndFinished(Graphics2D var1) {
      this.path = this.decodePath4();
      var1.setPaint(this.color33);
      var1.fill(this.path);
      this.rect = this.decodeRect5();
      var1.setPaint(this.decodeGradient9(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect6();
      var1.setPaint(this.decodeGradient10(this.rect));
      var1.fill(this.rect);
   }

   private void paintForegroundDisabledAndIndeterminate(Graphics2D var1) {
      this.rect = this.decodeRect7();
      var1.setPaint(this.decodeGradient11(this.rect));
      var1.fill(this.rect);
      this.path = this.decodePath5();
      var1.setPaint(this.decodeGradient12(this.path));
      var1.fill(this.path);
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(0.4F), (double)this.decodeY(0.4F), (double)(this.decodeX(2.6F) - this.decodeX(0.4F)), (double)(this.decodeY(2.6F) - this.decodeY(0.4F)));
      return this.rect;
   }

   private Rectangle2D decodeRect2() {
      this.rect.setRect((double)this.decodeX(0.6F), (double)this.decodeY(0.6F), (double)(this.decodeX(2.4F) - this.decodeX(0.6F)), (double)(this.decodeY(2.4F) - this.decodeY(0.6F)));
      return this.rect;
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0F), (double)this.decodeY(0.21111111F));
      this.path.curveTo((double)this.decodeAnchorX(1.0F, -2.0F), (double)this.decodeAnchorY(0.21111111F, 0.0F), (double)this.decodeAnchorX(0.21111111F, 0.0F), (double)this.decodeAnchorY(1.0F, -2.0F), (double)this.decodeX(0.21111111F), (double)this.decodeY(1.0F));
      this.path.curveTo((double)this.decodeAnchorX(0.21111111F, 0.0F), (double)this.decodeAnchorY(1.0F, 2.0F), (double)this.decodeAnchorX(0.21111111F, 0.0F), (double)this.decodeAnchorY(2.0F, -2.0F), (double)this.decodeX(0.21111111F), (double)this.decodeY(2.0F));
      this.path.curveTo((double)this.decodeAnchorX(0.21111111F, 0.0F), (double)this.decodeAnchorY(2.0F, 2.0F), (double)this.decodeAnchorX(1.0F, -2.0F), (double)this.decodeAnchorY(2.8222225F, 0.0F), (double)this.decodeX(1.0F), (double)this.decodeY(2.8222225F));
      this.path.curveTo((double)this.decodeAnchorX(1.0F, 2.0F), (double)this.decodeAnchorY(2.8222225F, 0.0F), (double)this.decodeAnchorX(3.0F, 0.0F), (double)this.decodeAnchorY(2.8222225F, 0.0F), (double)this.decodeX(3.0F), (double)this.decodeY(2.8222225F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(2.3333333F));
      this.path.lineTo((double)this.decodeX(0.6666667F), (double)this.decodeY(2.3333333F));
      this.path.lineTo((double)this.decodeX(0.6666667F), (double)this.decodeY(0.6666667F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(0.6666667F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(0.2F));
      this.path.curveTo((double)this.decodeAnchorX(3.0F, 0.0F), (double)this.decodeAnchorY(0.2F, 0.0F), (double)this.decodeAnchorX(1.0F, 2.0F), (double)this.decodeAnchorY(0.21111111F, 0.0F), (double)this.decodeX(1.0F), (double)this.decodeY(0.21111111F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect3() {
      this.rect.setRect((double)this.decodeX(0.6666667F), (double)this.decodeY(0.6666667F), (double)(this.decodeX(3.0F) - this.decodeX(0.6666667F)), (double)(this.decodeY(2.3333333F) - this.decodeY(0.6666667F)));
      return this.rect;
   }

   private Rectangle2D decodeRect4() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(2.6666667F) - this.decodeX(1.0F)), (double)(this.decodeY(2.0F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.9111111F), (double)this.decodeY(0.21111111F));
      this.path.curveTo((double)this.decodeAnchorX(0.9111111F, -2.0F), (double)this.decodeAnchorY(0.21111111F, 0.0F), (double)this.decodeAnchorX(0.2F, 0.0F), (double)this.decodeAnchorY(1.0025641F, -2.0F), (double)this.decodeX(0.2F), (double)this.decodeY(1.0025641F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(2.0444443F));
      this.path.curveTo((double)this.decodeAnchorX(0.2F, 0.0F), (double)this.decodeAnchorY(2.0444443F, 2.0F), (double)this.decodeAnchorX(0.9666667F, -2.0F), (double)this.decodeAnchorY(2.8F, 0.0F), (double)this.decodeX(0.9666667F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(2.788889F));
      this.path.curveTo((double)this.decodeAnchorX(2.0F, 1.9709293F), (double)this.decodeAnchorY(2.788889F, 0.01985704F), (double)this.decodeAnchorX(2.777778F, -0.033333335F), (double)this.decodeAnchorY(2.0555553F, 1.9333333F), (double)this.decodeX(2.777778F), (double)this.decodeY(2.0555553F));
      this.path.lineTo((double)this.decodeX(2.788889F), (double)this.decodeY(1.8051281F));
      this.path.lineTo((double)this.decodeX(2.777778F), (double)this.decodeY(1.2794871F));
      this.path.lineTo((double)this.decodeX(2.777778F), (double)this.decodeY(1.0025641F));
      this.path.curveTo((double)this.decodeAnchorX(2.777778F, 0.0042173304F), (double)this.decodeAnchorY(1.0025641F, -1.9503378F), (double)this.decodeAnchorX(2.0999997F, 1.9659461F), (double)this.decodeAnchorY(0.22222222F, 0.017122267F), (double)this.decodeX(2.0999997F), (double)this.decodeY(0.22222222F));
      this.path.lineTo((double)this.decodeX(0.9111111F), (double)this.decodeY(0.21111111F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect5() {
      this.rect.setRect((double)this.decodeX(0.6666667F), (double)this.decodeY(0.6666667F), (double)(this.decodeX(2.3333333F) - this.decodeX(0.6666667F)), (double)(this.decodeY(2.3333333F) - this.decodeY(0.6666667F)));
      return this.rect;
   }

   private Rectangle2D decodeRect6() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(2.0F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect7() {
      this.rect.setRect((double)this.decodeX(0.0F), (double)this.decodeY(0.0F), (double)(this.decodeX(3.0F) - this.decodeX(0.0F)), (double)(this.decodeY(3.0F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Path2D decodePath3() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(1.4285715F));
      this.path.curveTo((double)this.decodeAnchorX(0.0F, 2.6785715F), (double)this.decodeAnchorY(1.4285715F, 8.881784E-16F), (double)this.decodeAnchorX(1.3898809F, -6.214286F), (double)this.decodeAnchorY(0.3452381F, -0.035714287F), (double)this.decodeX(1.3898809F), (double)this.decodeY(0.3452381F));
      this.path.lineTo((double)this.decodeX(1.5535715F), (double)this.decodeY(0.3452381F));
      this.path.curveTo((double)this.decodeAnchorX(1.5535715F, 8.32967F), (double)this.decodeAnchorY(0.3452381F, 0.0027472528F), (double)this.decodeAnchorX(2.3333333F, -5.285714F), (double)this.decodeAnchorY(1.4285715F, 0.035714287F), (double)this.decodeX(2.3333333F), (double)this.decodeY(1.4285715F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(1.4285715F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(1.5714285F));
      this.path.lineTo((double)this.decodeX(2.3333333F), (double)this.decodeY(1.5714285F));
      this.path.curveTo((double)this.decodeAnchorX(2.3333333F, -5.321429F), (double)this.decodeAnchorY(1.5714285F, 0.035714287F), (double)this.decodeAnchorX(1.5535715F, 8.983517F), (double)this.decodeAnchorY(2.6666667F, 0.03846154F), (double)this.decodeX(1.5535715F), (double)this.decodeY(2.6666667F));
      this.path.lineTo((double)this.decodeX(1.4077381F), (double)this.decodeY(2.6666667F));
      this.path.curveTo((double)this.decodeAnchorX(1.4077381F, -6.714286F), (double)this.decodeAnchorY(2.6666667F, 0.0F), (double)this.decodeAnchorX(0.0F, 2.607143F), (double)this.decodeAnchorY(1.5714285F, 0.035714287F), (double)this.decodeX(0.0F), (double)this.decodeY(1.5714285F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(1.4285715F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect8() {
      this.rect.setRect((double)this.decodeX(1.2916666F), (double)this.decodeY(0.0F), (double)(this.decodeX(1.3333334F) - this.decodeX(1.2916666F)), (double)(this.decodeY(3.0F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect9() {
      this.rect.setRect((double)this.decodeX(1.7083333F), (double)this.decodeY(0.0F), (double)(this.decodeX(1.75F) - this.decodeX(1.7083333F)), (double)(this.decodeY(3.0F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Path2D decodePath4() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.9888889F), (double)this.decodeY(0.2F));
      this.path.curveTo((double)this.decodeAnchorX(0.9888889F, -2.0F), (double)this.decodeAnchorY(0.2F, 0.0F), (double)this.decodeAnchorX(0.2F, 0.0F), (double)this.decodeAnchorY(0.9888889F, -2.0F), (double)this.decodeX(0.2F), (double)this.decodeY(0.9888889F));
      this.path.curveTo((double)this.decodeAnchorX(0.2F, 0.0F), (double)this.decodeAnchorY(0.9888889F, 2.0F), (double)this.decodeAnchorX(0.2F, 0.0F), (double)this.decodeAnchorY(1.9974358F, -2.0F), (double)this.decodeX(0.2F), (double)this.decodeY(1.9974358F));
      this.path.curveTo((double)this.decodeAnchorX(0.2F, 0.0F), (double)this.decodeAnchorY(1.9974358F, 2.0F), (double)this.decodeAnchorX(0.9888889F, -2.0F), (double)this.decodeAnchorY(2.8111107F, 0.0F), (double)this.decodeX(0.9888889F), (double)this.decodeY(2.8111107F));
      this.path.curveTo((double)this.decodeAnchorX(0.9888889F, 2.0F), (double)this.decodeAnchorY(2.8111107F, 0.0F), (double)this.decodeAnchorX(2.5F, 0.0F), (double)this.decodeAnchorY(2.8F, 0.0F), (double)this.decodeX(2.5F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.7444446F), (double)this.decodeY(2.488889F));
      this.path.lineTo((double)this.decodeX(2.7555554F), (double)this.decodeY(1.5794872F));
      this.path.lineTo((double)this.decodeX(2.7666664F), (double)this.decodeY(1.4358975F));
      this.path.lineTo((double)this.decodeX(2.7666664F), (double)this.decodeY(0.62222224F));
      this.path.lineTo((double)this.decodeX(2.5999997F), (double)this.decodeY(0.22222222F));
      this.path.curveTo((double)this.decodeAnchorX(2.5999997F, 0.0F), (double)this.decodeAnchorY(0.22222222F, 0.0F), (double)this.decodeAnchorX(0.9888889F, 2.0F), (double)this.decodeAnchorY(0.2F, 0.0F), (double)this.decodeX(0.9888889F), (double)this.decodeY(0.2F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath5() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(1.4285715F));
      this.path.curveTo((double)this.decodeAnchorX(0.0F, 2.6785715F), (double)this.decodeAnchorY(1.4285715F, 8.881784E-16F), (double)this.decodeAnchorX(1.3898809F, -6.357143F), (double)this.decodeAnchorY(0.3452381F, -0.035714287F), (double)this.decodeX(1.3898809F), (double)this.decodeY(0.3452381F));
      this.path.lineTo((double)this.decodeX(1.5535715F), (double)this.decodeY(0.3452381F));
      this.path.curveTo((double)this.decodeAnchorX(1.5535715F, 4.0F), (double)this.decodeAnchorY(0.3452381F, 0.0F), (double)this.decodeAnchorX(2.3333333F, -5.285714F), (double)this.decodeAnchorY(1.4285715F, 0.035714287F), (double)this.decodeX(2.3333333F), (double)this.decodeY(1.4285715F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(1.4285715F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(1.5714285F));
      this.path.lineTo((double)this.decodeX(2.3333333F), (double)this.decodeY(1.5714285F));
      this.path.curveTo((double)this.decodeAnchorX(2.3333333F, -5.321429F), (double)this.decodeAnchorY(1.5714285F, 0.035714287F), (double)this.decodeAnchorX(1.5535715F, 4.0F), (double)this.decodeAnchorY(2.6666667F, 0.0F), (double)this.decodeX(1.5535715F), (double)this.decodeY(2.6666667F));
      this.path.lineTo((double)this.decodeX(1.4077381F), (double)this.decodeY(2.6666667F));
      this.path.curveTo((double)this.decodeAnchorX(1.4077381F, -6.571429F), (double)this.decodeAnchorY(2.6666667F, -0.035714287F), (double)this.decodeAnchorX(0.0F, 2.607143F), (double)this.decodeAnchorY(1.5714285F, 0.035714287F), (double)this.decodeX(0.0F), (double)this.decodeY(1.5714285F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(1.4285715F));
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
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.038709678F, 0.05967742F, 0.08064516F, 0.23709677F, 0.3935484F, 0.41612905F, 0.43870968F, 0.67419356F, 0.90967745F, 0.91451615F, 0.91935486F}, new Color[]{this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4, this.decodeColor(this.color4, this.color5, 0.5F), this.color5, this.decodeColor(this.color5, this.color6, 0.5F), this.color6, this.decodeColor(this.color6, this.color7, 0.5F), this.color7, this.decodeColor(this.color7, this.color8, 0.5F), this.color8});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.05483871F, 0.5032258F, 0.9516129F}, new Color[]{this.color9, this.decodeColor(this.color9, this.color10, 0.5F), this.color10});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.038709678F, 0.05967742F, 0.08064516F, 0.23709677F, 0.3935484F, 0.41612905F, 0.43870968F, 0.67419356F, 0.90967745F, 0.91612905F, 0.92258066F}, new Color[]{this.color11, this.decodeColor(this.color11, this.color12, 0.5F), this.color12, this.decodeColor(this.color12, this.color13, 0.5F), this.color13, this.decodeColor(this.color13, this.color14, 0.5F), this.color14, this.decodeColor(this.color14, this.color15, 0.5F), this.color15, this.decodeColor(this.color15, this.color16, 0.5F), this.color16});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.038709678F, 0.05483871F, 0.07096774F, 0.28064516F, 0.4903226F, 0.6967742F, 0.9032258F, 0.9241935F, 0.9451613F}, new Color[]{this.color18, this.decodeColor(this.color18, this.color19, 0.5F), this.color19, this.decodeColor(this.color19, this.color20, 0.5F), this.color20, this.decodeColor(this.color20, this.color21, 0.5F), this.color21, this.decodeColor(this.color21, this.color22, 0.5F), this.color22});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.038709678F, 0.061290324F, 0.08387097F, 0.27258065F, 0.46129033F, 0.4903226F, 0.5193548F, 0.71774197F, 0.91612905F, 0.92419356F, 0.93225807F}, new Color[]{this.color23, this.decodeColor(this.color23, this.color24, 0.5F), this.color24, this.decodeColor(this.color24, this.color25, 0.5F), this.color25, this.decodeColor(this.color25, this.color26, 0.5F), this.color26, this.decodeColor(this.color26, this.color27, 0.5F), this.color27, this.decodeColor(this.color27, this.color28, 0.5F), this.color28});
   }

   private Paint decodeGradient7(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.051612902F, 0.06612903F, 0.08064516F, 0.2935484F, 0.5064516F, 0.6903226F, 0.87419355F, 0.88870966F, 0.9032258F}, new Color[]{this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4, this.decodeColor(this.color4, this.color29, 0.5F), this.color29, this.decodeColor(this.color29, this.color7, 0.5F), this.color7, this.decodeColor(this.color7, this.color8, 0.5F), this.color8});
   }

   private Paint decodeGradient8(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.20645161F, 0.41290322F, 0.44193548F, 0.47096774F, 0.7354839F, 1.0F}, new Color[]{this.color24, this.decodeColor(this.color24, this.color25, 0.5F), this.color25, this.decodeColor(this.color25, this.color26, 0.5F), this.color26, this.decodeColor(this.color26, this.color30, 0.5F), this.color30});
   }

   private Paint decodeGradient9(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.038709678F, 0.05483871F, 0.07096774F, 0.28064516F, 0.4903226F, 0.6967742F, 0.9032258F, 0.9241935F, 0.9451613F}, new Color[]{this.color34, this.decodeColor(this.color34, this.color35, 0.5F), this.color35, this.decodeColor(this.color35, this.color36, 0.5F), this.color36, this.decodeColor(this.color36, this.color37, 0.5F), this.color37, this.decodeColor(this.color37, this.color38, 0.5F), this.color38});
   }

   private Paint decodeGradient10(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.038709678F, 0.061290324F, 0.08387097F, 0.27258065F, 0.46129033F, 0.4903226F, 0.5193548F, 0.71774197F, 0.91612905F, 0.92419356F, 0.93225807F}, new Color[]{this.color39, this.decodeColor(this.color39, this.color40, 0.5F), this.color40, this.decodeColor(this.color40, this.color41, 0.5F), this.color41, this.decodeColor(this.color41, this.color42, 0.5F), this.color42, this.decodeColor(this.color42, this.color43, 0.5F), this.color43, this.decodeColor(this.color43, this.color44, 0.5F), this.color44});
   }

   private Paint decodeGradient11(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.051612902F, 0.06612903F, 0.08064516F, 0.2935484F, 0.5064516F, 0.6903226F, 0.87419355F, 0.88870966F, 0.9032258F}, new Color[]{this.color45, this.decodeColor(this.color45, this.color46, 0.5F), this.color46, this.decodeColor(this.color46, this.color47, 0.5F), this.color47, this.decodeColor(this.color47, this.color48, 0.5F), this.color48, this.decodeColor(this.color48, this.color49, 0.5F), this.color49});
   }

   private Paint decodeGradient12(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.20645161F, 0.41290322F, 0.44193548F, 0.47096774F, 0.7354839F, 1.0F}, new Color[]{this.color40, this.decodeColor(this.color40, this.color41, 0.5F), this.color41, this.decodeColor(this.color41, this.color42, 0.5F), this.color42, this.decodeColor(this.color42, this.color50, 0.5F), this.color50});
   }
}
