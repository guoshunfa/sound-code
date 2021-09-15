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

final class SplitPaneDividerPainter extends AbstractRegionPainter {
   static final int BACKGROUND_ENABLED = 1;
   static final int BACKGROUND_FOCUSED = 2;
   static final int FOREGROUND_ENABLED = 3;
   static final int FOREGROUND_ENABLED_VERTICAL = 4;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.017358616F, -0.11372548F, 0);
   private Color color2 = this.decodeColor("nimbusBlueGrey", 0.055555582F, -0.102396235F, 0.21960783F, 0);
   private Color color3 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.07016757F, 0.12941176F, 0);
   private Color color4 = this.decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
   private Color color5 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
   private Color color6 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.048026316F, 0.007843137F, 0);
   private Color color7 = this.decodeColor("nimbusBlueGrey", 0.0055555105F, -0.06970999F, 0.21568626F, 0);
   private Color color8 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06704806F, 0.06666666F, 0);
   private Color color9 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.019617222F, -0.09803921F, 0);
   private Color color10 = this.decodeColor("nimbusBlueGrey", 0.004273474F, -0.03790062F, -0.043137252F, 0);
   private Color color11 = this.decodeColor("nimbusBlueGrey", -0.111111104F, -0.106573746F, 0.24705881F, 0);
   private Color color12 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.049301825F, 0.02352941F, 0);
   private Color color13 = this.decodeColor("nimbusBlueGrey", -0.006944418F, -0.07399663F, 0.11372548F, 0);
   private Color color14 = this.decodeColor("nimbusBlueGrey", -0.018518567F, -0.06998578F, 0.12549019F, 0);
   private Color color15 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.050526317F, 0.039215684F, 0);
   private Object[] componentColors;

   public SplitPaneDividerPainter(AbstractRegionPainter.PaintContext var1, int var2) {
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
         this.paintBackgroundFocused(var1);
         break;
      case 3:
         this.paintForegroundEnabled(var1);
         break;
      case 4:
         this.paintForegroundEnabledAndVertical(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.decodeGradient1(this.rect));
      var1.fill(this.rect);
   }

   private void paintBackgroundFocused(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.decodeGradient2(this.rect));
      var1.fill(this.rect);
   }

   private void paintForegroundEnabled(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient3(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintForegroundEnabledAndVertical(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient5(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient6(this.rect));
      var1.fill(this.rect);
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(0.0F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(3.0F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private RoundRectangle2D decodeRoundRect1() {
      this.roundRect.setRoundRect((double)this.decodeX(1.05F), (double)this.decodeY(1.3F), (double)(this.decodeX(1.95F) - this.decodeX(1.05F)), (double)(this.decodeY(1.8F) - this.decodeY(1.3F)), 3.6666667461395264D, 3.6666667461395264D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect2() {
      this.roundRect.setRoundRect((double)this.decodeX(1.1F), (double)this.decodeY(1.4F), (double)(this.decodeX(1.9F) - this.decodeX(1.1F)), (double)(this.decodeY(1.7F) - this.decodeY(1.4F)), 4.0D, 4.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect3() {
      this.roundRect.setRoundRect((double)this.decodeX(1.3F), (double)this.decodeY(1.1428572F), (double)(this.decodeX(1.7F) - this.decodeX(1.3F)), (double)(this.decodeY(1.8214285F) - this.decodeY(1.1428572F)), 4.0D, 4.0D);
      return this.roundRect;
   }

   private Rectangle2D decodeRect2() {
      this.rect.setRect((double)this.decodeX(1.4F), (double)this.decodeY(1.1785715F), (double)(this.decodeX(1.6F) - this.decodeX(1.4F)), (double)(this.decodeY(1.7678571F) - this.decodeY(1.1785715F)));
      return this.rect;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.058064517F, 0.08064516F, 0.103225805F, 0.116129026F, 0.12903225F, 0.43387097F, 0.7387097F, 0.77903223F, 0.81935483F, 0.85806453F, 0.8967742F}, new Color[]{this.color1, this.decodeColor(this.color1, this.color2, 0.5F), this.color2, this.decodeColor(this.color2, this.color3, 0.5F), this.color3, this.decodeColor(this.color3, this.color3, 0.5F), this.color3, this.decodeColor(this.color3, this.color2, 0.5F), this.color2, this.decodeColor(this.color2, this.color1, 0.5F), this.color1});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.058064517F, 0.08064516F, 0.103225805F, 0.1166129F, 0.13F, 0.43F, 0.73F, 0.7746774F, 0.81935483F, 0.85806453F, 0.8967742F}, new Color[]{this.color1, this.decodeColor(this.color1, this.color4, 0.5F), this.color4, this.decodeColor(this.color4, this.color3, 0.5F), this.color3, this.decodeColor(this.color3, this.color3, 0.5F), this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4, this.decodeColor(this.color4, this.color1, 0.5F), this.color1});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.20645161F, 0.5F, 0.7935484F}, new Color[]{this.color1, this.decodeColor(this.color1, this.color5, 0.5F), this.color5});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.090322584F, 0.2951613F, 0.5F, 0.5822581F, 0.66451615F}, new Color[]{this.color6, this.decodeColor(this.color6, this.color7, 0.5F), this.color7, this.decodeColor(this.color7, this.color8, 0.5F), this.color8});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.75F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.42096773F, 0.84193546F, 0.8951613F, 0.9483871F}, new Color[]{this.color9, this.decodeColor(this.color9, this.color10, 0.5F), this.color10, this.decodeColor(this.color10, this.color11, 0.5F), this.color11});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.08064516F, 0.16129032F, 0.5129032F, 0.86451614F, 0.88548386F, 0.90645164F}, new Color[]{this.color12, this.decodeColor(this.color12, this.color13, 0.5F), this.color13, this.decodeColor(this.color13, this.color14, 0.5F), this.color14, this.decodeColor(this.color14, this.color15, 0.5F), this.color15});
   }
}
