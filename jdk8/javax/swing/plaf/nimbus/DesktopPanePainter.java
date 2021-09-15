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

final class DesktopPanePainter extends AbstractRegionPainter {
   static final int BACKGROUND_ENABLED = 1;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBase", -0.004577577F, -0.12867206F, 0.007843137F, 0);
   private Color color2 = this.decodeColor("nimbusBase", -0.0063245893F, -0.08363098F, -0.17254904F, 0);
   private Color color3 = this.decodeColor("nimbusBase", -3.6883354E-4F, -0.056766927F, -0.10196081F, 0);
   private Color color4 = this.decodeColor("nimbusBase", -0.008954704F, -0.12645501F, -0.12549022F, 0);
   private Color color5 = new Color(255, 200, 0, 6);
   private Color color6 = this.decodeColor("nimbusBase", -8.028746E-5F, -0.084533215F, -0.05098042F, 0);
   private Color color7 = this.decodeColor("nimbusBase", -0.0052053332F, -0.12267083F, -0.09803924F, 0);
   private Color color8 = this.decodeColor("nimbusBase", -0.012559712F, -0.13136649F, -0.09803924F, 0);
   private Color color9 = this.decodeColor("nimbusBase", -0.009207249F, -0.13984653F, -0.07450983F, 0);
   private Color color10 = this.decodeColor("nimbusBase", -0.010750473F, -0.13571429F, -0.12549022F, 0);
   private Color color11 = this.decodeColor("nimbusBase", -0.008476257F, -0.1267857F, -0.109803945F, 0);
   private Color color12 = this.decodeColor("nimbusBase", -0.0034883022F, -0.042691052F, -0.21176472F, 0);
   private Color color13 = this.decodeColor("nimbusBase", -0.012613952F, -0.11610645F, -0.14901963F, 0);
   private Color color14 = this.decodeColor("nimbusBase", -0.0038217902F, -0.05238098F, -0.21960786F, 0);
   private Object[] componentColors;

   public DesktopPanePainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 1:
         this.paintBackgroundEnabled(var1);
      default:
      }
   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient1(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient2(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.color5);
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.decodeGradient3(this.path));
      var1.fill(this.path);
      this.path = this.decodePath5();
      var1.setPaint(this.decodeGradient4(this.path));
      var1.fill(this.path);
      this.path = this.decodePath6();
      var1.setPaint(this.decodeGradient5(this.path));
      var1.fill(this.path);
      this.path = this.decodePath7();
      var1.setPaint(this.decodeGradient6(this.path));
      var1.fill(this.path);
      this.path = this.decodePath8();
      var1.setPaint(this.decodeGradient7(this.path));
      var1.fill(this.path);
      this.path = this.decodePath9();
      var1.setPaint(this.decodeGradient8(this.path));
      var1.fill(this.path);
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.2716666F), (double)this.decodeY(2.0F));
      this.path.curveTo((double)this.decodeAnchorX(1.2716666F, 0.0F), (double)this.decodeAnchorY(2.0F, 0.5F), (double)this.decodeAnchorX(1.1283333F, 0.0F), (double)this.decodeAnchorY(1.0F, 0.0F), (double)this.decodeX(1.1283333F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.3516667F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.5866666F), (double)this.decodeY(1.5754311F));
      this.path.lineTo((double)this.decodeX(1.5416667F), (double)this.decodeY(2.0F));
      this.path.curveTo((double)this.decodeAnchorX(1.5416667F, 0.0F), (double)this.decodeAnchorY(2.0F, 0.0F), (double)this.decodeAnchorX(1.2716666F, 0.0F), (double)this.decodeAnchorY(2.0F, -0.5F), (double)this.decodeX(1.2716666F), (double)this.decodeY(2.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.7883334F), (double)this.decodeY(2.0F));
      this.path.curveTo((double)this.decodeAnchorX(1.7883334F, 0.0F), (double)this.decodeAnchorY(2.0F, 0.5F), (double)this.decodeAnchorX(1.6533333F, 0.0F), (double)this.decodeAnchorY(1.7737069F, 0.0F), (double)this.decodeX(1.6533333F), (double)this.decodeY(1.7737069F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(1.1465517F));
      this.path.curveTo((double)this.decodeAnchorX(2.0F, 0.0F), (double)this.decodeAnchorY(1.1465517F, 0.0F), (double)this.decodeAnchorX(2.0F, 0.0F), (double)this.decodeAnchorY(2.0F, -0.5F), (double)this.decodeX(2.0F), (double)this.decodeY(2.0F));
      this.path.curveTo((double)this.decodeAnchorX(2.0F, 0.5F), (double)this.decodeAnchorY(2.0F, 0.5F), (double)this.decodeAnchorX(1.7883334F, 0.0F), (double)this.decodeAnchorY(2.0F, -0.5F), (double)this.decodeX(1.7883334F), (double)this.decodeY(2.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath3() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.5666666F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.5666666F), (double)this.decodeY(1.5689654F));
      this.path.lineTo((double)this.decodeX(1.675F), (double)this.decodeY(1.7715517F));
      this.path.curveTo((double)this.decodeAnchorX(1.675F, 0.0F), (double)this.decodeAnchorY(1.7715517F, 0.0F), (double)this.decodeAnchorX(1.8116667F, -23.5F), (double)this.decodeAnchorY(1.4978448F, 33.5F), (double)this.decodeX(1.8116667F), (double)this.decodeY(1.4978448F));
      this.path.curveTo((double)this.decodeAnchorX(1.8116667F, 23.5F), (double)this.decodeAnchorY(1.4978448F, -33.5F), (double)this.decodeAnchorX(2.0F, 0.0F), (double)this.decodeAnchorY(1.200431F, 0.0F), (double)this.decodeX(2.0F), (double)this.decodeY(1.200431F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.5666666F), (double)this.decodeY(1.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath4() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.3383334F), (double)this.decodeY(1.0F));
      this.path.curveTo((double)this.decodeAnchorX(1.3383334F, 0.0F), (double)this.decodeAnchorY(1.0F, 0.0F), (double)this.decodeAnchorX(1.4416666F, -21.0F), (double)this.decodeAnchorY(1.3103448F, -37.5F), (double)this.decodeX(1.4416666F), (double)this.decodeY(1.3103448F));
      this.path.curveTo((double)this.decodeAnchorX(1.4416666F, 21.0F), (double)this.decodeAnchorY(1.3103448F, 37.5F), (double)this.decodeAnchorX(1.5733333F, 0.0F), (double)this.decodeAnchorY(1.5840517F, 0.0F), (double)this.decodeX(1.5733333F), (double)this.decodeY(1.5840517F));
      this.path.curveTo((double)this.decodeAnchorX(1.5733333F, 0.0F), (double)this.decodeAnchorY(1.5840517F, 0.0F), (double)this.decodeAnchorX(1.6066667F, 1.5F), (double)this.decodeAnchorY(1.2413793F, 29.5F), (double)this.decodeX(1.6066667F), (double)this.decodeY(1.2413793F));
      this.path.curveTo((double)this.decodeAnchorX(1.6066667F, -1.5F), (double)this.decodeAnchorY(1.2413793F, -29.5F), (double)this.decodeAnchorX(1.605F, 0.0F), (double)this.decodeAnchorY(1.0F, 0.0F), (double)this.decodeX(1.605F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.3383334F), (double)this.decodeY(1.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath5() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.5683334F), (double)this.decodeY(1.5797414F));
      this.path.curveTo((double)this.decodeAnchorX(1.5683334F, 0.0F), (double)this.decodeAnchorY(1.5797414F, 0.0F), (double)this.decodeAnchorX(1.575F, 0.0F), (double)this.decodeAnchorY(1.2392242F, 33.0F), (double)this.decodeX(1.575F), (double)this.decodeY(1.2392242F));
      this.path.curveTo((double)this.decodeAnchorX(1.575F, 0.0F), (double)this.decodeAnchorY(1.2392242F, -33.0F), (double)this.decodeAnchorX(1.5616667F, 0.0F), (double)this.decodeAnchorY(1.0F, 0.0F), (double)this.decodeX(1.5616667F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(1.1982758F));
      this.path.curveTo((double)this.decodeAnchorX(2.0F, 0.0F), (double)this.decodeAnchorY(1.1982758F, 0.0F), (double)this.decodeAnchorX(1.8066666F, 27.5F), (double)this.decodeAnchorY(1.5043104F, -38.5F), (double)this.decodeX(1.8066666F), (double)this.decodeY(1.5043104F));
      this.path.curveTo((double)this.decodeAnchorX(1.8066666F, -27.5F), (double)this.decodeAnchorY(1.5043104F, 38.5F), (double)this.decodeAnchorX(1.6766667F, 0.0F), (double)this.decodeAnchorY(1.7780173F, 0.0F), (double)this.decodeX(1.6766667F), (double)this.decodeY(1.7780173F));
      this.path.lineTo((double)this.decodeX(1.5683334F), (double)this.decodeY(1.5797414F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath6() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.5216666F), (double)this.decodeY(2.0F));
      this.path.curveTo((double)this.decodeAnchorX(1.5216666F, 0.0F), (double)this.decodeAnchorY(2.0F, 0.0F), (double)this.decodeAnchorX(1.5550001F, -2.0F), (double)this.decodeAnchorY(1.7780173F, 22.5F), (double)this.decodeX(1.5550001F), (double)this.decodeY(1.7780173F));
      this.path.curveTo((double)this.decodeAnchorX(1.5550001F, 2.0F), (double)this.decodeAnchorY(1.7780173F, -22.5F), (double)this.decodeAnchorX(1.5683334F, 0.0F), (double)this.decodeAnchorY(1.5765086F, 0.0F), (double)this.decodeX(1.5683334F), (double)this.decodeY(1.5765086F));
      this.path.lineTo((double)this.decodeX(1.6775F), (double)this.decodeY(1.7747846F));
      this.path.curveTo((double)this.decodeAnchorX(1.6775F, 0.0F), (double)this.decodeAnchorY(1.7747846F, 0.0F), (double)this.decodeAnchorX(1.6508334F, 6.0F), (double)this.decodeAnchorY(1.8922414F, -14.0F), (double)this.decodeX(1.6508334F), (double)this.decodeY(1.8922414F));
      this.path.curveTo((double)this.decodeAnchorX(1.6508334F, -6.0F), (double)this.decodeAnchorY(1.8922414F, 14.0F), (double)this.decodeAnchorX(1.6083333F, 0.0F), (double)this.decodeAnchorY(2.0F, 0.0F), (double)this.decodeX(1.6083333F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(1.5216666F), (double)this.decodeY(2.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath7() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.6066667F), (double)this.decodeY(2.0F));
      this.path.curveTo((double)this.decodeAnchorX(1.6066667F, 0.0F), (double)this.decodeAnchorY(2.0F, 0.0F), (double)this.decodeAnchorX(1.64F, -7.0F), (double)this.decodeAnchorY(1.8814654F, 17.0F), (double)this.decodeX(1.64F), (double)this.decodeY(1.8814654F));
      this.path.curveTo((double)this.decodeAnchorX(1.64F, 7.0F), (double)this.decodeAnchorY(1.8814654F, -17.0F), (double)this.decodeAnchorX(1.6775F, 0.0F), (double)this.decodeAnchorY(1.7747846F, 0.0F), (double)this.decodeX(1.6775F), (double)this.decodeY(1.7747846F));
      this.path.curveTo((double)this.decodeAnchorX(1.6775F, 0.0F), (double)this.decodeAnchorY(1.7747846F, 0.0F), (double)this.decodeAnchorX(1.7416667F, -11.0F), (double)this.decodeAnchorY(1.8836207F, -15.0F), (double)this.decodeX(1.7416667F), (double)this.decodeY(1.8836207F));
      this.path.curveTo((double)this.decodeAnchorX(1.7416667F, 11.0F), (double)this.decodeAnchorY(1.8836207F, 15.0F), (double)this.decodeAnchorX(1.8133333F, 0.0F), (double)this.decodeAnchorY(2.0F, -0.5F), (double)this.decodeX(1.8133333F), (double)this.decodeY(2.0F));
      this.path.curveTo((double)this.decodeAnchorX(1.8133333F, 0.0F), (double)this.decodeAnchorY(2.0F, 0.5F), (double)this.decodeAnchorX(1.6066667F, 0.0F), (double)this.decodeAnchorY(2.0F, 0.0F), (double)this.decodeX(1.6066667F), (double)this.decodeY(2.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath8() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.2733333F), (double)this.decodeY(2.0F));
      this.path.curveTo((double)this.decodeAnchorX(1.2733333F, 0.0F), (double)this.decodeAnchorY(2.0F, 0.0F), (double)this.decodeAnchorX(1.2633333F, 5.0F), (double)this.decodeAnchorY(1.6594827F, 37.0F), (double)this.decodeX(1.2633333F), (double)this.decodeY(1.6594827F));
      this.path.curveTo((double)this.decodeAnchorX(1.2633333F, -5.0F), (double)this.decodeAnchorY(1.6594827F, -37.0F), (double)this.decodeAnchorX(1.1933334F, 9.0F), (double)this.decodeAnchorY(1.2241379F, 33.5F), (double)this.decodeX(1.1933334F), (double)this.decodeY(1.2241379F));
      this.path.curveTo((double)this.decodeAnchorX(1.1933334F, -9.0F), (double)this.decodeAnchorY(1.2241379F, -33.5F), (double)this.decodeAnchorX(1.1333333F, 0.0F), (double)this.decodeAnchorY(1.0F, 0.0F), (double)this.decodeX(1.1333333F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(1.6120689F));
      this.path.curveTo((double)this.decodeAnchorX(1.0F, 0.0F), (double)this.decodeAnchorY(1.6120689F, 0.0F), (double)this.decodeAnchorX(1.15F, 0.0F), (double)this.decodeAnchorY(2.0F, -0.5F), (double)this.decodeX(1.15F), (double)this.decodeY(2.0F));
      this.path.curveTo((double)this.decodeAnchorX(1.15F, 0.0F), (double)this.decodeAnchorY(2.0F, 0.5F), (double)this.decodeAnchorX(1.2733333F, 0.0F), (double)this.decodeAnchorY(2.0F, 0.0F), (double)this.decodeX(1.2733333F), (double)this.decodeY(2.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath9() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(1.5969827F));
      this.path.curveTo((double)this.decodeAnchorX(1.0F, 0.0F), (double)this.decodeAnchorY(1.5969827F, 0.0F), (double)this.decodeAnchorX(1.0733334F, -10.0F), (double)this.decodeAnchorY(1.7974138F, -19.5F), (double)this.decodeX(1.0733334F), (double)this.decodeY(1.7974138F));
      this.path.curveTo((double)this.decodeAnchorX(1.0733334F, 10.0F), (double)this.decodeAnchorY(1.7974138F, 19.5F), (double)this.decodeAnchorX(1.1666666F, 0.0F), (double)this.decodeAnchorY(2.0F, -0.5F), (double)this.decodeX(1.1666666F), (double)this.decodeY(2.0F));
      this.path.curveTo((double)this.decodeAnchorX(1.1666666F, 0.0F), (double)this.decodeAnchorY(2.0F, 0.5F), (double)this.decodeAnchorX(1.0F, 0.0F), (double)this.decodeAnchorY(2.0F, 0.0F), (double)this.decodeX(1.0F), (double)this.decodeY(2.0F));
      this.path.closePath();
      return this.path;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.75F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color1, this.decodeColor(this.color1, this.color2, 0.5F), this.color2});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.9567308F * var5 + var3, 0.06835443F * var6 + var4, 0.75F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.83536583F * var5 + var3, 0.9522059F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color6, this.decodeColor(this.color6, this.color7, 0.5F), this.color7});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.8659696F * var5 + var3, 0.011049724F * var6 + var4, 0.24809887F * var5 + var3, 0.95027626F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color6, this.decodeColor(this.color6, this.color8, 0.5F), this.color8});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.3511236F * var5 + var3, 0.09326425F * var6 + var4, 0.33426967F * var5 + var3, 0.9846154F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color9, this.decodeColor(this.color9, this.color10, 0.5F), this.color10});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.3548387F * var5 + var3, 0.114285715F * var6 + var4, 0.48387095F * var5 + var3, 0.9809524F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color11, this.decodeColor(this.color11, this.color4, 0.5F), this.color4});
   }

   private Paint decodeGradient7(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.75F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color1, this.decodeColor(this.color1, this.color12, 0.5F), this.color12});
   }

   private Paint decodeGradient8(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.75F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color13, this.decodeColor(this.color13, this.color14, 0.5F), this.color14});
   }
}
