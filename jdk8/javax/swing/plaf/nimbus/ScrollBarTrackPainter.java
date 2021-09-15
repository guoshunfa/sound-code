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

final class ScrollBarTrackPainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED = 1;
   static final int BACKGROUND_ENABLED = 2;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.10016362F, 0.011764705F, 0);
   private Color color2 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.100476064F, 0.035294116F, 0);
   private Color color3 = this.decodeColor("nimbusBlueGrey", 0.055555582F, -0.10606203F, 0.13333333F, 0);
   private Color color4 = this.decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, 0.24705881F, 0);
   private Color color5 = this.decodeColor("nimbusBlueGrey", 0.02222228F, -0.06465475F, -0.31764707F, 0);
   private Color color6 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06766917F, -0.19607842F, 0);
   private Color color7 = this.decodeColor("nimbusBlueGrey", -0.006944418F, -0.0655825F, -0.04705882F, 0);
   private Color color8 = this.decodeColor("nimbusBlueGrey", 0.0138888955F, -0.071117446F, 0.05098039F, 0);
   private Color color9 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.07016757F, 0.12941176F, 0);
   private Color color10 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.05967886F, -0.5137255F, 0);
   private Color color11 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.05967886F, -0.5137255F, -255);
   private Color color12 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.07826825F, -0.5019608F, -255);
   private Color color13 = this.decodeColor("nimbusBlueGrey", -0.015872955F, -0.06731644F, -0.109803915F, 0);
   private Color color14 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06924191F, 0.109803915F, 0);
   private Color color15 = this.decodeColor("nimbusBlueGrey", -0.015872955F, -0.06861015F, -0.09019607F, 0);
   private Color color16 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06766917F, 0.07843137F, 0);
   private Object[] componentColors;

   public ScrollBarTrackPainter(AbstractRegionPainter.PaintContext var1, int var2) {
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
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundDisabled(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.decodeGradient1(this.rect));
      var1.fill(this.rect);
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.decodeGradient2(this.rect));
      var1.fill(this.rect);
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient3(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient4(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient5(this.path));
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.decodeGradient6(this.path));
      var1.fill(this.path);
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(0.0F), (double)this.decodeY(0.0F), (double)(this.decodeX(3.0F) - this.decodeX(0.0F)), (double)(this.decodeY(3.0F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.7F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(1.2F));
      this.path.curveTo((double)this.decodeAnchorX(0.0F, 0.0F), (double)this.decodeAnchorY(1.2F, 0.0F), (double)this.decodeAnchorX(0.3F, -1.0F), (double)this.decodeAnchorY(2.2F, -1.0F), (double)this.decodeX(0.3F), (double)this.decodeY(2.2F));
      this.path.curveTo((double)this.decodeAnchorX(0.3F, 1.0F), (double)this.decodeAnchorY(2.2F, 1.0F), (double)this.decodeAnchorX(0.6785714F, 0.0F), (double)this.decodeAnchorY(2.8F, 0.0F), (double)this.decodeX(0.6785714F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.7F), (double)this.decodeY(0.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(3.0F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(2.2222223F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(2.2222223F), (double)this.decodeY(2.8F));
      this.path.curveTo((double)this.decodeAnchorX(2.2222223F, 0.0F), (double)this.decodeAnchorY(2.8F, 0.0F), (double)this.decodeAnchorX(2.6746032F, -1.0F), (double)this.decodeAnchorY(2.1857142F, 1.0F), (double)this.decodeX(2.6746032F), (double)this.decodeY(2.1857142F));
      this.path.curveTo((double)this.decodeAnchorX(2.6746032F, 1.0F), (double)this.decodeAnchorY(2.1857142F, -1.0F), (double)this.decodeAnchorX(3.0F, 0.0F), (double)this.decodeAnchorY(1.2F, 0.0F), (double)this.decodeX(3.0F), (double)this.decodeY(1.2F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(0.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath3() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.11428572F), (double)this.decodeY(1.3714286F));
      this.path.curveTo((double)this.decodeAnchorX(0.11428572F, 0.78571427F), (double)this.decodeAnchorY(1.3714286F, -0.5714286F), (double)this.decodeAnchorX(0.4642857F, -1.3571428F), (double)this.decodeAnchorY(2.0714285F, -1.5714285F), (double)this.decodeX(0.4642857F), (double)this.decodeY(2.0714285F));
      this.path.curveTo((double)this.decodeAnchorX(0.4642857F, 1.3571428F), (double)this.decodeAnchorY(2.0714285F, 1.5714285F), (double)this.decodeAnchorX(0.8714286F, 0.21428572F), (double)this.decodeAnchorY(2.7285714F, -1.0F), (double)this.decodeX(0.8714286F), (double)this.decodeY(2.7285714F));
      this.path.curveTo((double)this.decodeAnchorX(0.8714286F, -0.21428572F), (double)this.decodeAnchorY(2.7285714F, 1.0F), (double)this.decodeAnchorX(0.35714287F, 1.5F), (double)this.decodeAnchorY(2.3142858F, 1.6428572F), (double)this.decodeX(0.35714287F), (double)this.decodeY(2.3142858F));
      this.path.curveTo((double)this.decodeAnchorX(0.35714287F, -1.5F), (double)this.decodeAnchorY(2.3142858F, -1.6428572F), (double)this.decodeAnchorX(0.11428572F, -0.78571427F), (double)this.decodeAnchorY(1.3714286F, 0.5714286F), (double)this.decodeX(0.11428572F), (double)this.decodeY(1.3714286F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath4() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(2.1111112F), (double)this.decodeY(2.7F));
      this.path.curveTo((double)this.decodeAnchorX(2.1111112F, 0.42857143F), (double)this.decodeAnchorY(2.7F, 0.64285713F), (double)this.decodeAnchorX(2.6269841F, -1.5714285F), (double)this.decodeAnchorY(2.2F, 1.6428572F), (double)this.decodeX(2.6269841F), (double)this.decodeY(2.2F));
      this.path.curveTo((double)this.decodeAnchorX(2.6269841F, 1.5714285F), (double)this.decodeAnchorY(2.2F, -1.6428572F), (double)this.decodeAnchorX(2.84127F, 0.71428573F), (double)this.decodeAnchorY(1.3857143F, 0.64285713F), (double)this.decodeX(2.84127F), (double)this.decodeY(1.3857143F));
      this.path.curveTo((double)this.decodeAnchorX(2.84127F, -0.71428573F), (double)this.decodeAnchorY(1.3857143F, -0.64285713F), (double)this.decodeAnchorX(2.5238094F, 0.71428573F), (double)this.decodeAnchorY(2.0571427F, -0.85714287F), (double)this.decodeX(2.5238094F), (double)this.decodeY(2.0571427F));
      this.path.curveTo((double)this.decodeAnchorX(2.5238094F, -0.71428573F), (double)this.decodeAnchorY(2.0571427F, 0.85714287F), (double)this.decodeAnchorX(2.1111112F, -0.42857143F), (double)this.decodeAnchorY(2.7F, -0.64285713F), (double)this.decodeX(2.1111112F), (double)this.decodeY(2.7F));
      this.path.closePath();
      return this.path;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.016129032F, 0.038709678F, 0.061290324F, 0.16091082F, 0.26451612F, 0.4378071F, 0.88387096F}, new Color[]{this.color1, this.decodeColor(this.color1, this.color2, 0.5F), this.color2, this.decodeColor(this.color2, this.color3, 0.5F), this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.030645162F, 0.061290324F, 0.09677419F, 0.13225806F, 0.22096774F, 0.30967742F, 0.47434634F, 0.82258064F}, new Color[]{this.color5, this.decodeColor(this.color5, this.color6, 0.5F), this.color6, this.decodeColor(this.color6, this.color7, 0.5F), this.color7, this.decodeColor(this.color7, this.color8, 0.5F), this.color8, this.decodeColor(this.color8, this.color9, 0.5F), this.color9});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.0F * var5 + var3, 0.0F * var6 + var4, 0.9285714F * var5 + var3, 0.12244898F * var6 + var4, new float[]{0.0F, 0.1F, 1.0F}, new Color[]{this.color10, this.decodeColor(this.color10, this.color11, 0.5F), this.color11});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(-0.045918368F * var5 + var3, 0.18336426F * var6 + var4, 0.872449F * var5 + var3, 0.04050711F * var6 + var4, new float[]{0.0F, 0.87096775F, 1.0F}, new Color[]{this.color12, this.decodeColor(this.color12, this.color10, 0.5F), this.color10});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.12719299F * var5 + var3, 0.13157894F * var6 + var4, 0.90789473F * var5 + var3, 0.877193F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color13, this.decodeColor(this.color13, this.color14, 0.5F), this.color14});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.86458343F * var5 + var3, 0.20952381F * var6 + var4, 0.020833189F * var5 + var3, 0.95238096F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color15, this.decodeColor(this.color15, this.color16, 0.5F), this.color16});
   }
}
