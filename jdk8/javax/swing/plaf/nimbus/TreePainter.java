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

final class TreePainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED = 1;
   static final int BACKGROUND_ENABLED = 2;
   static final int BACKGROUND_ENABLED_SELECTED = 3;
   static final int LEAFICON_ENABLED = 4;
   static final int CLOSEDICON_ENABLED = 5;
   static final int OPENICON_ENABLED = 6;
   static final int COLLAPSEDICON_ENABLED = 7;
   static final int COLLAPSEDICON_ENABLED_SELECTED = 8;
   static final int EXPANDEDICON_ENABLED = 9;
   static final int EXPANDEDICON_ENABLED_SELECTED = 10;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBlueGrey", 0.007936537F, -0.065654516F, -0.13333333F, 0);
   private Color color2 = new Color(97, 98, 102, 255);
   private Color color3 = this.decodeColor("nimbusBlueGrey", -0.032679737F, -0.043332636F, 0.24705881F, 0);
   private Color color4 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
   private Color color5 = this.decodeColor("nimbusBase", 0.0077680945F, -0.51781034F, 0.3490196F, 0);
   private Color color6 = this.decodeColor("nimbusBase", 0.013940871F, -0.599277F, 0.41960782F, 0);
   private Color color7 = this.decodeColor("nimbusBase", 0.004681647F, -0.4198052F, 0.14117646F, 0);
   private Color color8 = this.decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, -127);
   private Color color9 = this.decodeColor("nimbusBlueGrey", 0.0F, 0.0F, -0.21F, -99);
   private Color color10 = this.decodeColor("nimbusBase", 2.9569864E-4F, -0.45978838F, 0.2980392F, 0);
   private Color color11 = this.decodeColor("nimbusBase", 0.0015952587F, -0.34848025F, 0.18823528F, 0);
   private Color color12 = this.decodeColor("nimbusBase", 0.0015952587F, -0.30844158F, 0.09803921F, 0);
   private Color color13 = this.decodeColor("nimbusBase", 0.0015952587F, -0.27329817F, 0.035294116F, 0);
   private Color color14 = this.decodeColor("nimbusBase", 0.004681647F, -0.6198413F, 0.43921566F, 0);
   private Color color15 = this.decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, -125);
   private Color color16 = this.decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, -50);
   private Color color17 = this.decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, -100);
   private Color color18 = this.decodeColor("nimbusBase", 0.0012094378F, -0.23571429F, -0.0784314F, 0);
   private Color color19 = this.decodeColor("nimbusBase", 2.9569864E-4F, -0.115166366F, -0.2627451F, 0);
   private Color color20 = this.decodeColor("nimbusBase", 0.0027436614F, -0.335015F, 0.011764705F, 0);
   private Color color21 = this.decodeColor("nimbusBase", 0.0024294257F, -0.3857143F, 0.031372547F, 0);
   private Color color22 = this.decodeColor("nimbusBase", 0.0018081069F, -0.3595238F, -0.13725492F, 0);
   private Color color23 = new Color(255, 200, 0, 255);
   private Color color24 = this.decodeColor("nimbusBase", 0.004681647F, -0.33496243F, -0.027450979F, 0);
   private Color color25 = this.decodeColor("nimbusBase", 0.0019934773F, -0.361378F, -0.10588238F, 0);
   private Color color26 = this.decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.34509805F, 0);
   private Object[] componentColors;

   public TreePainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 4:
         this.paintleafIconEnabled(var1);
         break;
      case 5:
         this.paintclosedIconEnabled(var1);
         break;
      case 6:
         this.paintopenIconEnabled(var1);
         break;
      case 7:
         this.paintcollapsedIconEnabled(var1);
         break;
      case 8:
         this.paintcollapsedIconEnabledAndSelected(var1);
         break;
      case 9:
         this.paintexpandedIconEnabled(var1);
         break;
      case 10:
         this.paintexpandedIconEnabledAndSelected(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintleafIconEnabled(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color1);
      var1.fill(this.path);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color2);
      var1.fill(this.rect);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient1(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient2(this.path));
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.color7);
      var1.fill(this.path);
      this.path = this.decodePath5();
      var1.setPaint(this.color8);
      var1.fill(this.path);
   }

   private void paintclosedIconEnabled(Graphics2D var1) {
      this.path = this.decodePath6();
      var1.setPaint(this.color9);
      var1.fill(this.path);
      this.path = this.decodePath7();
      var1.setPaint(this.decodeGradient3(this.path));
      var1.fill(this.path);
      this.path = this.decodePath8();
      var1.setPaint(this.decodeGradient4(this.path));
      var1.fill(this.path);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color15);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color17);
      var1.fill(this.rect);
      this.path = this.decodePath9();
      var1.setPaint(this.decodeGradient5(this.path));
      var1.fill(this.path);
      this.path = this.decodePath10();
      var1.setPaint(this.decodeGradient6(this.path));
      var1.fill(this.path);
      this.path = this.decodePath11();
      var1.setPaint(this.color23);
      var1.fill(this.path);
   }

   private void paintopenIconEnabled(Graphics2D var1) {
      this.path = this.decodePath6();
      var1.setPaint(this.color9);
      var1.fill(this.path);
      this.path = this.decodePath12();
      var1.setPaint(this.decodeGradient3(this.path));
      var1.fill(this.path);
      this.path = this.decodePath13();
      var1.setPaint(this.decodeGradient4(this.path));
      var1.fill(this.path);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color15);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color17);
      var1.fill(this.rect);
      this.path = this.decodePath14();
      var1.setPaint(this.decodeGradient5(this.path));
      var1.fill(this.path);
      this.path = this.decodePath15();
      var1.setPaint(this.decodeGradient7(this.path));
      var1.fill(this.path);
      this.path = this.decodePath11();
      var1.setPaint(this.color23);
      var1.fill(this.path);
   }

   private void paintcollapsedIconEnabled(Graphics2D var1) {
      this.path = this.decodePath16();
      var1.setPaint(this.color26);
      var1.fill(this.path);
   }

   private void paintcollapsedIconEnabledAndSelected(Graphics2D var1) {
      this.path = this.decodePath16();
      var1.setPaint(this.color4);
      var1.fill(this.path);
   }

   private void paintexpandedIconEnabled(Graphics2D var1) {
      this.path = this.decodePath17();
      var1.setPaint(this.color26);
      var1.fill(this.path);
   }

   private void paintexpandedIconEnabledAndSelected(Graphics2D var1) {
      this.path = this.decodePath17();
      var1.setPaint(this.color4);
      var1.fill(this.path);
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.2F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(1.9197531F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(0.9F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(0.88888896F));
      this.path.lineTo((double)this.decodeX(1.9537036F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(0.0F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(0.4F), (double)this.decodeY(2.8F), (double)(this.decodeX(2.6F) - this.decodeX(0.4F)), (double)(this.decodeY(3.0F) - this.decodeY(2.8F)));
      return this.rect;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.2F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath3() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.2F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath4() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(1.6234567F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(1.6296296F), (double)this.decodeY(1.2037038F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(1.2006173F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.2F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath5() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.4F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath6() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(2.4F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(2.4F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.4F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath7() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.6037037F), (double)this.decodeY(1.8425925F));
      this.path.lineTo((double)this.decodeX(0.8F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(2.6F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath8() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.2F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.40833336F), (double)this.decodeY(1.8645833F));
      this.path.lineTo((double)this.decodeX(0.79583335F), (double)this.decodeY(0.8F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(0.8F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(2.6F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect2() {
      this.rect.setRect((double)this.decodeX(0.2F), (double)this.decodeY(0.6F), (double)(this.decodeX(0.4F) - this.decodeX(0.2F)), (double)(this.decodeY(0.8F) - this.decodeY(0.6F)));
      return this.rect;
   }

   private Rectangle2D decodeRect3() {
      this.rect.setRect((double)this.decodeX(0.6F), (double)this.decodeY(0.2F), (double)(this.decodeX(1.3333334F) - this.decodeX(0.6F)), (double)(this.decodeY(0.4F) - this.decodeY(0.2F)));
      return this.rect;
   }

   private Rectangle2D decodeRect4() {
      this.rect.setRect((double)this.decodeX(1.5F), (double)this.decodeY(0.6F), (double)(this.decodeX(2.4F) - this.decodeX(1.5F)), (double)(this.decodeY(0.8F) - this.decodeY(0.6F)));
      return this.rect;
   }

   private Path2D decodePath9() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(3.0F), (double)this.decodeY(0.8F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.5888889F), (double)this.decodeY(0.20370372F));
      this.path.lineTo((double)this.decodeX(0.5962963F), (double)this.decodeY(0.34814817F));
      this.path.lineTo((double)this.decodeX(0.34814817F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(2.774074F), (double)this.decodeY(1.1604939F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.8925927F), (double)this.decodeY(1.1882716F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.65185183F));
      this.path.lineTo((double)this.decodeX(0.63703704F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(1.5925925F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(0.8F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(0.8F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath10() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(2.4F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(0.8F));
      this.path.lineTo((double)this.decodeX(0.74814814F), (double)this.decodeY(0.8F));
      this.path.lineTo((double)this.decodeX(0.4037037F), (double)this.decodeY(1.8425925F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.5925926F), (double)this.decodeY(2.225926F));
      this.path.lineTo((double)this.decodeX(0.916F), (double)this.decodeY(0.996F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(1.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath11() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(2.2F), (double)this.decodeY(2.2F));
      this.path.lineTo((double)this.decodeX(2.2F), (double)this.decodeY(2.2F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath12() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(2.2F));
      this.path.lineTo((double)this.decodeX(0.8F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(2.6F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath13() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.2F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(0.8F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(2.6F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath14() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(3.0F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.5888889F), (double)this.decodeY(0.20370372F));
      this.path.lineTo((double)this.decodeX(0.5962963F), (double)this.decodeY(0.34814817F));
      this.path.lineTo((double)this.decodeX(0.34814817F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(1.8333333F));
      this.path.lineTo((double)this.decodeX(2.916F), (double)this.decodeY(1.3533334F));
      this.path.lineTo((double)this.decodeX(2.98F), (double)this.decodeY(1.3766667F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(1.8333333F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.65185183F));
      this.path.lineTo((double)this.decodeX(0.63703704F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(1.5925925F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(1.1666666F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath15() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(2.4F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(0.74F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.5925926F), (double)this.decodeY(2.225926F));
      this.path.lineTo((double)this.decodeX(0.8F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(1.3333334F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath16() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(1.2397541F), (double)this.decodeY(0.70163935F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath17() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(1.25F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.70819676F), (double)this.decodeY(2.9901638F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.0F));
      this.path.closePath();
      return this.path;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.046296295F * var5 + var3, 0.9675926F * var6 + var4, 0.4861111F * var5 + var3, 0.5324074F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color5, this.decodeColor(this.color5, this.color6, 0.5F), this.color6});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.04191617F, 0.10329342F, 0.16467066F, 0.24550897F, 0.3263473F, 0.6631737F, 1.0F}, new Color[]{this.color10, this.decodeColor(this.color10, this.color11, 0.5F), this.color11, this.decodeColor(this.color11, this.color12, 0.5F), this.color12, this.decodeColor(this.color12, this.color13, 0.5F), this.color13});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color5, this.decodeColor(this.color5, this.color14, 0.5F), this.color14});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color18, this.decodeColor(this.color18, this.color19, 0.5F), this.color19});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.12724552F, 0.25449103F, 0.62724555F, 1.0F}, new Color[]{this.color20, this.decodeColor(this.color20, this.color21, 0.5F), this.color21, this.decodeColor(this.color21, this.color22, 0.5F), this.color22});
   }

   private Paint decodeGradient7(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color24, this.decodeColor(this.color24, this.color25, 0.5F), this.color25});
   }
}
