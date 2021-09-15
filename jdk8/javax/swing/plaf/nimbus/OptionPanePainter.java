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

final class OptionPanePainter extends AbstractRegionPainter {
   static final int BACKGROUND_ENABLED = 1;
   static final int ERRORICON_ENABLED = 2;
   static final int INFORMATIONICON_ENABLED = 3;
   static final int QUESTIONICON_ENABLED = 4;
   static final int WARNINGICON_ENABLED = 5;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusRed", -0.014814814F, 0.18384242F, 0.015686274F, 0);
   private Color color2 = this.decodeColor("nimbusRed", -0.014814814F, -0.403261F, 0.21960783F, 0);
   private Color color3 = this.decodeColor("nimbusRed", -0.014814814F, -0.07154381F, 0.11372548F, 0);
   private Color color4 = this.decodeColor("nimbusRed", -0.014814814F, 0.110274374F, 0.07058823F, 0);
   private Color color5 = this.decodeColor("nimbusRed", -0.014814814F, -0.05413574F, 0.2588235F, 0);
   private Color color6 = new Color(250, 250, 250, 255);
   private Color color7 = this.decodeColor("nimbusRed", 0.0F, -0.79881656F, 0.33725488F, -187);
   private Color color8 = new Color(255, 200, 0, 255);
   private Color color9 = this.decodeColor("nimbusInfoBlue", 0.0F, 0.06231594F, -0.054901958F, 0);
   private Color color10 = this.decodeColor("nimbusInfoBlue", 3.1620264E-4F, 0.07790506F, -0.19215685F, 0);
   private Color color11 = this.decodeColor("nimbusInfoBlue", -8.2296133E-4F, -0.44631243F, 0.19215685F, 0);
   private Color color12 = this.decodeColor("nimbusInfoBlue", 0.0012729168F, -0.0739674F, 0.043137252F, 0);
   private Color color13 = this.decodeColor("nimbusInfoBlue", 8.354187E-4F, -0.14148629F, 0.19999999F, 0);
   private Color color14 = this.decodeColor("nimbusInfoBlue", -0.0014793873F, -0.41456455F, 0.16470587F, 0);
   private Color color15 = this.decodeColor("nimbusInfoBlue", 3.437996E-4F, -0.14726585F, 0.043137252F, 0);
   private Color color16 = this.decodeColor("nimbusInfoBlue", -4.271865E-4F, -0.0055555105F, 0.0F, 0);
   private Color color17 = this.decodeColor("nimbusInfoBlue", 0.0F, 0.0F, 0.0F, 0);
   private Color color18 = this.decodeColor("nimbusInfoBlue", -7.866621E-4F, -0.12728173F, 0.17254901F, 0);
   private Color color19 = new Color(115, 120, 126, 255);
   private Color color20 = new Color(26, 34, 43, 255);
   private Color color21 = new Color(168, 173, 178, 255);
   private Color color22 = new Color(101, 109, 118, 255);
   private Color color23 = new Color(159, 163, 168, 255);
   private Color color24 = new Color(116, 122, 130, 255);
   private Color color25 = new Color(96, 104, 112, 255);
   private Color color26 = new Color(118, 128, 138, 255);
   private Color color27 = new Color(255, 255, 255, 255);
   private Color color28 = this.decodeColor("nimbusAlertYellow", -4.9102306E-4F, 0.1372549F, -0.15294117F, 0);
   private Color color29 = this.decodeColor("nimbusAlertYellow", -0.0015973002F, 0.1372549F, -0.3490196F, 0);
   private Color color30 = this.decodeColor("nimbusAlertYellow", 6.530881E-4F, -0.40784314F, 0.0F, 0);
   private Color color31 = this.decodeColor("nimbusAlertYellow", -3.9456785E-4F, -0.109803915F, 0.0F, 0);
   private Color color32 = this.decodeColor("nimbusAlertYellow", 0.0F, 0.0F, 0.0F, 0);
   private Color color33 = this.decodeColor("nimbusAlertYellow", 0.008085668F, -0.04705882F, 0.0F, 0);
   private Color color34 = this.decodeColor("nimbusAlertYellow", 0.026515156F, -0.18431371F, 0.0F, 0);
   private Color color35 = new Color(69, 69, 69, 255);
   private Color color36 = new Color(0, 0, 0, 255);
   private Color color37 = new Color(16, 16, 16, 255);
   private Object[] componentColors;

   public OptionPanePainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 2:
         this.painterrorIconEnabled(var1);
         break;
      case 3:
         this.paintinformationIconEnabled(var1);
         break;
      case 4:
         this.paintquestionIconEnabled(var1);
         break;
      case 5:
         this.paintwarningIconEnabled(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void painterrorIconEnabled(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color1);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient1(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.color6);
      var1.fill(this.path);
      this.ellipse = this.decodeEllipse1();
      var1.setPaint(this.color6);
      var1.fill(this.ellipse);
      this.path = this.decodePath4();
      var1.setPaint(this.color7);
      var1.fill(this.path);
   }

   private void paintinformationIconEnabled(Graphics2D var1) {
      this.ellipse = this.decodeEllipse2();
      var1.setPaint(this.color8);
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse2();
      var1.setPaint(this.color8);
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse2();
      var1.setPaint(this.color8);
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse3();
      var1.setPaint(this.decodeGradient2(this.ellipse));
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse4();
      var1.setPaint(this.decodeGradient3(this.ellipse));
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse5();
      var1.setPaint(this.decodeGradient4(this.ellipse));
      var1.fill(this.ellipse);
      this.path = this.decodePath5();
      var1.setPaint(this.color6);
      var1.fill(this.path);
      this.ellipse = this.decodeEllipse6();
      var1.setPaint(this.color6);
      var1.fill(this.ellipse);
   }

   private void paintquestionIconEnabled(Graphics2D var1) {
      this.ellipse = this.decodeEllipse3();
      var1.setPaint(this.decodeGradient5(this.ellipse));
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse4();
      var1.setPaint(this.decodeGradient6(this.ellipse));
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse5();
      var1.setPaint(this.decodeGradient7(this.ellipse));
      var1.fill(this.ellipse);
      this.path = this.decodePath6();
      var1.setPaint(this.color27);
      var1.fill(this.path);
      this.ellipse = this.decodeEllipse1();
      var1.setPaint(this.color27);
      var1.fill(this.ellipse);
   }

   private void paintwarningIconEnabled(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color8);
      var1.fill(this.rect);
      this.path = this.decodePath7();
      var1.setPaint(this.decodeGradient8(this.path));
      var1.fill(this.path);
      this.path = this.decodePath8();
      var1.setPaint(this.decodeGradient9(this.path));
      var1.fill(this.path);
      this.path = this.decodePath9();
      var1.setPaint(this.decodeGradient10(this.path));
      var1.fill(this.path);
      this.ellipse = this.decodeEllipse7();
      var1.setPaint(this.color37);
      var1.fill(this.ellipse);
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0F), (double)this.decodeY(1.2708334F));
      this.path.lineTo((double)this.decodeX(1.2708334F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.6875F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.9583333F), (double)this.decodeY(1.2708334F));
      this.path.lineTo((double)this.decodeX(1.9583333F), (double)this.decodeY(1.6875F));
      this.path.lineTo((double)this.decodeX(1.6875F), (double)this.decodeY(1.9583333F));
      this.path.lineTo((double)this.decodeX(1.2708334F), (double)this.decodeY(1.9583333F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(1.6875F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(1.2708334F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0208334F), (double)this.decodeY(1.2916666F));
      this.path.lineTo((double)this.decodeX(1.2916666F), (double)this.decodeY(1.0208334F));
      this.path.lineTo((double)this.decodeX(1.6666667F), (double)this.decodeY(1.0208334F));
      this.path.lineTo((double)this.decodeX(1.9375F), (double)this.decodeY(1.2916666F));
      this.path.lineTo((double)this.decodeX(1.9375F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(1.6666667F), (double)this.decodeY(1.9375F));
      this.path.lineTo((double)this.decodeX(1.2916666F), (double)this.decodeY(1.9375F));
      this.path.lineTo((double)this.decodeX(1.0208334F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(1.0208334F), (double)this.decodeY(1.2916666F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath3() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.4166666F), (double)this.decodeY(1.2291666F));
      this.path.curveTo((double)this.decodeAnchorX(1.4166666F, 0.0F), (double)this.decodeAnchorY(1.2291666F, -2.0F), (double)this.decodeAnchorX(1.4791666F, -2.0F), (double)this.decodeAnchorY(1.1666666F, 0.0F), (double)this.decodeX(1.4791666F), (double)this.decodeY(1.1666666F));
      this.path.curveTo((double)this.decodeAnchorX(1.4791666F, 2.0F), (double)this.decodeAnchorY(1.1666666F, 0.0F), (double)this.decodeAnchorX(1.5416667F, 0.0F), (double)this.decodeAnchorY(1.2291666F, -2.0F), (double)this.decodeX(1.5416667F), (double)this.decodeY(1.2291666F));
      this.path.curveTo((double)this.decodeAnchorX(1.5416667F, 0.0F), (double)this.decodeAnchorY(1.2291666F, 2.0F), (double)this.decodeAnchorX(1.5F, 0.0F), (double)this.decodeAnchorY(1.6041667F, 0.0F), (double)this.decodeX(1.5F), (double)this.decodeY(1.6041667F));
      this.path.lineTo((double)this.decodeX(1.4583334F), (double)this.decodeY(1.6041667F));
      this.path.curveTo((double)this.decodeAnchorX(1.4583334F, 0.0F), (double)this.decodeAnchorY(1.6041667F, 0.0F), (double)this.decodeAnchorX(1.4166666F, 0.0F), (double)this.decodeAnchorY(1.2291666F, 2.0F), (double)this.decodeX(1.4166666F), (double)this.decodeY(1.2291666F));
      this.path.closePath();
      return this.path;
   }

   private Ellipse2D decodeEllipse1() {
      this.ellipse.setFrame((double)this.decodeX(1.4166666F), (double)this.decodeY(1.6666667F), (double)(this.decodeX(1.5416667F) - this.decodeX(1.4166666F)), (double)(this.decodeY(1.7916667F) - this.decodeY(1.6666667F)));
      return this.ellipse;
   }

   private Path2D decodePath4() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0208334F), (double)this.decodeY(1.2851562F));
      this.path.lineTo((double)this.decodeX(1.2799479F), (double)this.decodeY(1.0208334F));
      this.path.lineTo((double)this.decodeX(1.6783855F), (double)this.decodeY(1.0208334F));
      this.path.lineTo((double)this.decodeX(1.9375F), (double)this.decodeY(1.28125F));
      this.path.lineTo((double)this.decodeX(1.9375F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(1.6666667F), (double)this.decodeY(1.9375F));
      this.path.lineTo((double)this.decodeX(1.2851562F), (double)this.decodeY(1.936198F));
      this.path.lineTo((double)this.decodeX(1.0221354F), (double)this.decodeY(1.673177F));
      this.path.lineTo((double)this.decodeX(1.0208334F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(1.0416666F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(1.0416666F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(1.2916666F), (double)this.decodeY(1.9166667F));
      this.path.lineTo((double)this.decodeX(1.6666667F), (double)this.decodeY(1.9166667F));
      this.path.lineTo((double)this.decodeX(1.9166667F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(1.9166667F), (double)this.decodeY(1.2916666F));
      this.path.lineTo((double)this.decodeX(1.6666667F), (double)this.decodeY(1.0416666F));
      this.path.lineTo((double)this.decodeX(1.2916666F), (double)this.decodeY(1.0416666F));
      this.path.lineTo((double)this.decodeX(1.0416666F), (double)this.decodeY(1.2916666F));
      this.path.lineTo((double)this.decodeX(1.0416666F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(1.0208334F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(1.0208334F), (double)this.decodeY(1.2851562F));
      this.path.closePath();
      return this.path;
   }

   private Ellipse2D decodeEllipse2() {
      this.ellipse.setFrame((double)this.decodeX(1.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(1.0F) - this.decodeX(1.0F)), (double)(this.decodeY(1.0F) - this.decodeY(1.0F)));
      return this.ellipse;
   }

   private Ellipse2D decodeEllipse3() {
      this.ellipse.setFrame((double)this.decodeX(1.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(1.9583333F) - this.decodeX(1.0F)), (double)(this.decodeY(1.9583333F) - this.decodeY(1.0F)));
      return this.ellipse;
   }

   private Ellipse2D decodeEllipse4() {
      this.ellipse.setFrame((double)this.decodeX(1.0208334F), (double)this.decodeY(1.0208334F), (double)(this.decodeX(1.9375F) - this.decodeX(1.0208334F)), (double)(this.decodeY(1.9375F) - this.decodeY(1.0208334F)));
      return this.ellipse;
   }

   private Ellipse2D decodeEllipse5() {
      this.ellipse.setFrame((double)this.decodeX(1.0416666F), (double)this.decodeY(1.0416666F), (double)(this.decodeX(1.9166667F) - this.decodeX(1.0416666F)), (double)(this.decodeY(1.9166667F) - this.decodeY(1.0416666F)));
      return this.ellipse;
   }

   private Path2D decodePath5() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.375F), (double)this.decodeY(1.375F));
      this.path.curveTo((double)this.decodeAnchorX(1.375F, 2.5F), (double)this.decodeAnchorY(1.375F, 0.0F), (double)this.decodeAnchorX(1.5F, -1.1875F), (double)this.decodeAnchorY(1.375F, 0.0F), (double)this.decodeX(1.5F), (double)this.decodeY(1.375F));
      this.path.curveTo((double)this.decodeAnchorX(1.5F, 1.1875F), (double)this.decodeAnchorY(1.375F, 0.0F), (double)this.decodeAnchorX(1.5416667F, 0.0F), (double)this.decodeAnchorY(1.4375F, -2.0F), (double)this.decodeX(1.5416667F), (double)this.decodeY(1.4375F));
      this.path.curveTo((double)this.decodeAnchorX(1.5416667F, 0.0F), (double)this.decodeAnchorY(1.4375F, 2.0F), (double)this.decodeAnchorX(1.5416667F, 0.0F), (double)this.decodeAnchorY(1.6875F, 0.0F), (double)this.decodeX(1.5416667F), (double)this.decodeY(1.6875F));
      this.path.curveTo((double)this.decodeAnchorX(1.5416667F, 0.0F), (double)this.decodeAnchorY(1.6875F, 0.0F), (double)this.decodeAnchorX(1.6028645F, -2.5625F), (double)this.decodeAnchorY(1.6875F, 0.0625F), (double)this.decodeX(1.6028645F), (double)this.decodeY(1.6875F));
      this.path.curveTo((double)this.decodeAnchorX(1.6028645F, 2.5625F), (double)this.decodeAnchorY(1.6875F, -0.0625F), (double)this.decodeAnchorX(1.6041667F, 2.5625F), (double)this.decodeAnchorY(1.7708333F, 0.0F), (double)this.decodeX(1.6041667F), (double)this.decodeY(1.7708333F));
      this.path.curveTo((double)this.decodeAnchorX(1.6041667F, -2.5625F), (double)this.decodeAnchorY(1.7708333F, 0.0F), (double)this.decodeAnchorX(1.3567709F, 2.5F), (double)this.decodeAnchorY(1.7708333F, 0.0625F), (double)this.decodeX(1.3567709F), (double)this.decodeY(1.7708333F));
      this.path.curveTo((double)this.decodeAnchorX(1.3567709F, -2.5F), (double)this.decodeAnchorY(1.7708333F, -0.0625F), (double)this.decodeAnchorX(1.3541666F, -2.4375F), (double)this.decodeAnchorY(1.6875F, 0.0F), (double)this.decodeX(1.3541666F), (double)this.decodeY(1.6875F));
      this.path.curveTo((double)this.decodeAnchorX(1.3541666F, 2.4375F), (double)this.decodeAnchorY(1.6875F, 0.0F), (double)this.decodeAnchorX(1.4166666F, 0.0F), (double)this.decodeAnchorY(1.6875F, 0.0F), (double)this.decodeX(1.4166666F), (double)this.decodeY(1.6875F));
      this.path.lineTo((double)this.decodeX(1.4166666F), (double)this.decodeY(1.4583334F));
      this.path.curveTo((double)this.decodeAnchorX(1.4166666F, 0.0F), (double)this.decodeAnchorY(1.4583334F, 0.0F), (double)this.decodeAnchorX(1.375F, 2.75F), (double)this.decodeAnchorY(1.4583334F, 0.0F), (double)this.decodeX(1.375F), (double)this.decodeY(1.4583334F));
      this.path.curveTo((double)this.decodeAnchorX(1.375F, -2.75F), (double)this.decodeAnchorY(1.4583334F, 0.0F), (double)this.decodeAnchorX(1.375F, -2.5F), (double)this.decodeAnchorY(1.375F, 0.0F), (double)this.decodeX(1.375F), (double)this.decodeY(1.375F));
      this.path.closePath();
      return this.path;
   }

   private Ellipse2D decodeEllipse6() {
      this.ellipse.setFrame((double)this.decodeX(1.4166666F), (double)this.decodeY(1.1666666F), (double)(this.decodeX(1.5416667F) - this.decodeX(1.4166666F)), (double)(this.decodeY(1.2916666F) - this.decodeY(1.1666666F)));
      return this.ellipse;
   }

   private Path2D decodePath6() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.3125F), (double)this.decodeY(1.3723959F));
      this.path.curveTo((double)this.decodeAnchorX(1.3125F, 1.5F), (double)this.decodeAnchorY(1.3723959F, 1.375F), (double)this.decodeAnchorX(1.3997396F, -0.75F), (double)this.decodeAnchorY(1.3580729F, 1.1875F), (double)this.decodeX(1.3997396F), (double)this.decodeY(1.3580729F));
      this.path.curveTo((double)this.decodeAnchorX(1.3997396F, 0.75F), (double)this.decodeAnchorY(1.3580729F, -1.1875F), (double)this.decodeAnchorX(1.46875F, -1.8125F), (double)this.decodeAnchorY(1.2903646F, 0.0F), (double)this.decodeX(1.46875F), (double)this.decodeY(1.2903646F));
      this.path.curveTo((double)this.decodeAnchorX(1.46875F, 1.8125F), (double)this.decodeAnchorY(1.2903646F, 0.0F), (double)this.decodeAnchorX(1.5351562F, 0.0F), (double)this.decodeAnchorY(1.3502604F, -1.5625F), (double)this.decodeX(1.5351562F), (double)this.decodeY(1.3502604F));
      this.path.curveTo((double)this.decodeAnchorX(1.5351562F, 0.0F), (double)this.decodeAnchorY(1.3502604F, 1.5625F), (double)this.decodeAnchorX(1.4700521F, 1.25F), (double)this.decodeAnchorY(1.4283854F, -1.1875F), (double)this.decodeX(1.4700521F), (double)this.decodeY(1.4283854F));
      this.path.curveTo((double)this.decodeAnchorX(1.4700521F, -1.25F), (double)this.decodeAnchorY(1.4283854F, 1.1875F), (double)this.decodeAnchorX(1.4179688F, -0.0625F), (double)this.decodeAnchorY(1.5442708F, -1.5F), (double)this.decodeX(1.4179688F), (double)this.decodeY(1.5442708F));
      this.path.curveTo((double)this.decodeAnchorX(1.4179688F, 0.0625F), (double)this.decodeAnchorY(1.5442708F, 1.5F), (double)this.decodeAnchorX(1.4765625F, -1.3125F), (double)this.decodeAnchorY(1.6028645F, 0.0F), (double)this.decodeX(1.4765625F), (double)this.decodeY(1.6028645F));
      this.path.curveTo((double)this.decodeAnchorX(1.4765625F, 1.3125F), (double)this.decodeAnchorY(1.6028645F, 0.0F), (double)this.decodeAnchorX(1.5403645F, 0.0F), (double)this.decodeAnchorY(1.546875F, 1.625F), (double)this.decodeX(1.5403645F), (double)this.decodeY(1.546875F));
      this.path.curveTo((double)this.decodeAnchorX(1.5403645F, 0.0F), (double)this.decodeAnchorY(1.546875F, -1.625F), (double)this.decodeAnchorX(1.6132812F, -1.1875F), (double)this.decodeAnchorY(1.4648438F, 1.25F), (double)this.decodeX(1.6132812F), (double)this.decodeY(1.4648438F));
      this.path.curveTo((double)this.decodeAnchorX(1.6132812F, 1.1875F), (double)this.decodeAnchorY(1.4648438F, -1.25F), (double)this.decodeAnchorX(1.6666667F, 0.0625F), (double)this.decodeAnchorY(1.3463541F, 3.3125F), (double)this.decodeX(1.6666667F), (double)this.decodeY(1.3463541F));
      this.path.curveTo((double)this.decodeAnchorX(1.6666667F, -0.0625F), (double)this.decodeAnchorY(1.3463541F, -3.3125F), (double)this.decodeAnchorX(1.4830729F, 6.125F), (double)this.decodeAnchorY(1.1679688F, -0.0625F), (double)this.decodeX(1.4830729F), (double)this.decodeY(1.1679688F));
      this.path.curveTo((double)this.decodeAnchorX(1.4830729F, -6.125F), (double)this.decodeAnchorY(1.1679688F, 0.0625F), (double)this.decodeAnchorX(1.3046875F, 0.4375F), (double)this.decodeAnchorY(1.2890625F, -1.25F), (double)this.decodeX(1.3046875F), (double)this.decodeY(1.2890625F));
      this.path.curveTo((double)this.decodeAnchorX(1.3046875F, -0.4375F), (double)this.decodeAnchorY(1.2890625F, 1.25F), (double)this.decodeAnchorX(1.3125F, -1.5F), (double)this.decodeAnchorY(1.3723959F, -1.375F), (double)this.decodeX(1.3125F), (double)this.decodeY(1.3723959F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(1.0F) - this.decodeX(1.0F)), (double)(this.decodeY(1.0F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Path2D decodePath7() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.5F), (double)this.decodeY(1.0208334F));
      this.path.curveTo((double)this.decodeAnchorX(1.5F, 2.0F), (double)this.decodeAnchorY(1.0208334F, 0.0F), (double)this.decodeAnchorX(1.5664062F, 0.0F), (double)this.decodeAnchorY(1.0820312F, 0.0F), (double)this.decodeX(1.5664062F), (double)this.decodeY(1.0820312F));
      this.path.lineTo((double)this.decodeX(1.9427083F), (double)this.decodeY(1.779948F));
      this.path.curveTo((double)this.decodeAnchorX(1.9427083F, 0.0F), (double)this.decodeAnchorY(1.779948F, 0.0F), (double)this.decodeAnchorX(1.9752605F, 0.0F), (double)this.decodeAnchorY(1.8802083F, -2.375F), (double)this.decodeX(1.9752605F), (double)this.decodeY(1.8802083F));
      this.path.curveTo((double)this.decodeAnchorX(1.9752605F, 0.0F), (double)this.decodeAnchorY(1.8802083F, 2.375F), (double)this.decodeAnchorX(1.9166667F, 0.0F), (double)this.decodeAnchorY(1.9375F, 0.0F), (double)this.decodeX(1.9166667F), (double)this.decodeY(1.9375F));
      this.path.lineTo((double)this.decodeX(1.0833334F), (double)this.decodeY(1.9375F));
      this.path.curveTo((double)this.decodeAnchorX(1.0833334F, 0.0F), (double)this.decodeAnchorY(1.9375F, 0.0F), (double)this.decodeAnchorX(1.0247396F, 0.125F), (double)this.decodeAnchorY(1.8815105F, 2.25F), (double)this.decodeX(1.0247396F), (double)this.decodeY(1.8815105F));
      this.path.curveTo((double)this.decodeAnchorX(1.0247396F, -0.125F), (double)this.decodeAnchorY(1.8815105F, -2.25F), (double)this.decodeAnchorX(1.0598959F, 0.0F), (double)this.decodeAnchorY(1.78125F, 0.0F), (double)this.decodeX(1.0598959F), (double)this.decodeY(1.78125F));
      this.path.lineTo((double)this.decodeX(1.4375F), (double)this.decodeY(1.0833334F));
      this.path.curveTo((double)this.decodeAnchorX(1.4375F, 0.0F), (double)this.decodeAnchorY(1.0833334F, 0.0F), (double)this.decodeAnchorX(1.5F, -2.0F), (double)this.decodeAnchorY(1.0208334F, 0.0F), (double)this.decodeX(1.5F), (double)this.decodeY(1.0208334F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath8() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.4986979F), (double)this.decodeY(1.0429688F));
      this.path.curveTo((double)this.decodeAnchorX(1.4986979F, 1.75F), (double)this.decodeAnchorY(1.0429688F, 0.0F), (double)this.decodeAnchorX(1.5546875F, 0.0F), (double)this.decodeAnchorY(1.0950521F, 0.0F), (double)this.decodeX(1.5546875F), (double)this.decodeY(1.0950521F));
      this.path.lineTo((double)this.decodeX(1.9322917F), (double)this.decodeY(1.8007812F));
      this.path.curveTo((double)this.decodeAnchorX(1.9322917F, 0.0F), (double)this.decodeAnchorY(1.8007812F, 0.0F), (double)this.decodeAnchorX(1.9570312F, 0.0F), (double)this.decodeAnchorY(1.875F, -1.4375F), (double)this.decodeX(1.9570312F), (double)this.decodeY(1.875F));
      this.path.curveTo((double)this.decodeAnchorX(1.9570312F, 0.0F), (double)this.decodeAnchorY(1.875F, 1.4375F), (double)this.decodeAnchorX(1.8841145F, 0.0F), (double)this.decodeAnchorY(1.9166667F, 0.0F), (double)this.decodeX(1.8841145F), (double)this.decodeY(1.9166667F));
      this.path.lineTo((double)this.decodeX(1.1002604F), (double)this.decodeY(1.9166667F));
      this.path.curveTo((double)this.decodeAnchorX(1.1002604F, 0.0F), (double)this.decodeAnchorY(1.9166667F, 0.0F), (double)this.decodeAnchorX(1.0455729F, 0.0625F), (double)this.decodeAnchorY(1.8723958F, 1.625F), (double)this.decodeX(1.0455729F), (double)this.decodeY(1.8723958F));
      this.path.curveTo((double)this.decodeAnchorX(1.0455729F, -0.0625F), (double)this.decodeAnchorY(1.8723958F, -1.625F), (double)this.decodeAnchorX(1.0755209F, 0.0F), (double)this.decodeAnchorY(1.7903645F, 0.0F), (double)this.decodeX(1.0755209F), (double)this.decodeY(1.7903645F));
      this.path.lineTo((double)this.decodeX(1.4414062F), (double)this.decodeY(1.1028646F));
      this.path.curveTo((double)this.decodeAnchorX(1.4414062F, 0.0F), (double)this.decodeAnchorY(1.1028646F, 0.0F), (double)this.decodeAnchorX(1.4986979F, -1.75F), (double)this.decodeAnchorY(1.0429688F, 0.0F), (double)this.decodeX(1.4986979F), (double)this.decodeY(1.0429688F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath9() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.5F), (double)this.decodeY(1.2291666F));
      this.path.curveTo((double)this.decodeAnchorX(1.5F, 2.0F), (double)this.decodeAnchorY(1.2291666F, 0.0F), (double)this.decodeAnchorX(1.5625F, 0.0F), (double)this.decodeAnchorY(1.3125F, -2.0F), (double)this.decodeX(1.5625F), (double)this.decodeY(1.3125F));
      this.path.curveTo((double)this.decodeAnchorX(1.5625F, 0.0F), (double)this.decodeAnchorY(1.3125F, 2.0F), (double)this.decodeAnchorX(1.5F, 1.3125F), (double)this.decodeAnchorY(1.6666667F, 0.0F), (double)this.decodeX(1.5F), (double)this.decodeY(1.6666667F));
      this.path.curveTo((double)this.decodeAnchorX(1.5F, -1.3125F), (double)this.decodeAnchorY(1.6666667F, 0.0F), (double)this.decodeAnchorX(1.4375F, 0.0F), (double)this.decodeAnchorY(1.3125F, 2.0F), (double)this.decodeX(1.4375F), (double)this.decodeY(1.3125F));
      this.path.curveTo((double)this.decodeAnchorX(1.4375F, 0.0F), (double)this.decodeAnchorY(1.3125F, -2.0F), (double)this.decodeAnchorX(1.5F, -2.0F), (double)this.decodeAnchorY(1.2291666F, 0.0F), (double)this.decodeX(1.5F), (double)this.decodeY(1.2291666F));
      this.path.closePath();
      return this.path;
   }

   private Ellipse2D decodeEllipse7() {
      this.ellipse.setFrame((double)this.decodeX(1.4375F), (double)this.decodeY(1.7291667F), (double)(this.decodeX(1.5625F) - this.decodeX(1.4375F)), (double)(this.decodeY(1.8541667F) - this.decodeY(1.7291667F)));
      return this.ellipse;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.17258064F, 0.3451613F, 0.5145161F, 0.683871F, 0.9F, 1.0F}, new Color[]{this.color2, this.decodeColor(this.color2, this.color3, 0.5F), this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4, this.decodeColor(this.color4, this.color5, 0.5F), this.color5});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color9, this.decodeColor(this.color9, this.color10, 0.5F), this.color10});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.24143836F, 0.48287672F, 0.7414384F, 1.0F}, new Color[]{this.color11, this.decodeColor(this.color11, this.color12, 0.5F), this.color12, this.decodeColor(this.color12, this.color13, 0.5F), this.color13});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.14212328F, 0.28424656F, 0.39212328F, 0.5F, 0.60958904F, 0.7191781F, 0.85958904F, 1.0F}, new Color[]{this.color14, this.decodeColor(this.color14, this.color15, 0.5F), this.color15, this.decodeColor(this.color15, this.color16, 0.5F), this.color16, this.decodeColor(this.color16, this.color17, 0.5F), this.color17, this.decodeColor(this.color17, this.color18, 0.5F), this.color18});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color19, this.decodeColor(this.color19, this.color20, 0.5F), this.color20});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color21, this.decodeColor(this.color21, this.color22, 0.5F), this.color22});
   }

   private Paint decodeGradient7(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.15239726F, 0.30479452F, 0.47945207F, 0.6541096F, 0.8270548F, 1.0F}, new Color[]{this.color23, this.decodeColor(this.color23, this.color24, 0.5F), this.color24, this.decodeColor(this.color24, this.color25, 0.5F), this.color25, this.decodeColor(this.color25, this.color26, 0.5F), this.color26});
   }

   private Paint decodeGradient8(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color28, this.decodeColor(this.color28, this.color29, 0.5F), this.color29});
   }

   private Paint decodeGradient9(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.1729452F, 0.3458904F, 0.49315068F, 0.64041096F, 0.7328767F, 0.8253425F, 0.9126712F, 1.0F}, new Color[]{this.color30, this.decodeColor(this.color30, this.color31, 0.5F), this.color31, this.decodeColor(this.color31, this.color32, 0.5F), this.color32, this.decodeColor(this.color32, this.color33, 0.5F), this.color33, this.decodeColor(this.color33, this.color34, 0.5F), this.color34});
   }

   private Paint decodeGradient10(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color35, this.decodeColor(this.color35, this.color36, 0.5F), this.color36});
   }
}
