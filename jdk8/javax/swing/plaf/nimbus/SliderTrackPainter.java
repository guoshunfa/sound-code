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

final class SliderTrackPainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED = 1;
   static final int BACKGROUND_ENABLED = 2;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -245);
   private Color color2 = this.decodeColor("nimbusBlueGrey", 0.0055555105F, -0.061265234F, 0.05098039F, 0);
   private Color color3 = this.decodeColor("nimbusBlueGrey", 0.01010108F, -0.059835073F, 0.10588235F, 0);
   private Color color4 = this.decodeColor("nimbusBlueGrey", -0.01111114F, -0.061982628F, 0.062745094F, 0);
   private Color color5 = this.decodeColor("nimbusBlueGrey", -0.00505054F, -0.058639523F, 0.086274505F, 0);
   private Color color6 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -111);
   private Color color7 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.034093194F, -0.12941176F, 0);
   private Color color8 = this.decodeColor("nimbusBlueGrey", 0.01111114F, -0.023821115F, -0.06666666F, 0);
   private Color color9 = this.decodeColor("nimbusBlueGrey", -0.008547008F, -0.03314536F, -0.086274505F, 0);
   private Color color10 = this.decodeColor("nimbusBlueGrey", 0.004273474F, -0.040256046F, -0.019607842F, 0);
   private Color color11 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.03626889F, 0.04705882F, 0);
   private Object[] componentColors;

   public SliderTrackPainter(AbstractRegionPainter.PaintContext var1, int var2) {
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
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient1(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient2(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect4();
      var1.setPaint(this.color6);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient3(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect5();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
   }

   private RoundRectangle2D decodeRoundRect1() {
      this.roundRect.setRoundRect((double)this.decodeX(0.2F), (double)this.decodeY(1.6F), (double)(this.decodeX(2.8F) - this.decodeX(0.2F)), (double)(this.decodeY(2.8333333F) - this.decodeY(1.6F)), 8.70588207244873D, 8.70588207244873D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect2() {
      this.roundRect.setRoundRect((double)this.decodeX(0.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(3.0F) - this.decodeX(0.0F)), (double)(this.decodeY(2.0F) - this.decodeY(1.0F)), 4.941176414489746D, 4.941176414489746D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect3() {
      this.roundRect.setRoundRect((double)this.decodeX(0.29411763F), (double)this.decodeY(1.2F), (double)(this.decodeX(2.7058823F) - this.decodeX(0.29411763F)), (double)(this.decodeY(2.0F) - this.decodeY(1.2F)), 4.0D, 4.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect4() {
      this.roundRect.setRoundRect((double)this.decodeX(0.2F), (double)this.decodeY(1.6F), (double)(this.decodeX(2.8F) - this.decodeX(0.2F)), (double)(this.decodeY(2.1666667F) - this.decodeY(1.6F)), 8.70588207244873D, 8.70588207244873D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect5() {
      this.roundRect.setRoundRect((double)this.decodeX(0.28823528F), (double)this.decodeY(1.2F), (double)(this.decodeX(2.7F) - this.decodeX(0.28823528F)), (double)(this.decodeY(2.0F) - this.decodeY(1.2F)), 4.0D, 4.0D);
      return this.roundRect;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.07647059F * var6 + var4, 0.25F * var5 + var3, 0.9117647F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color2, this.decodeColor(this.color2, this.color3, 0.5F), this.color3});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.13770053F, 0.27540106F, 0.63770056F, 1.0F}, new Color[]{this.color4, this.decodeColor(this.color4, this.color5, 0.5F), this.color5, this.decodeColor(this.color5, this.color3, 0.5F), this.color3});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.07647059F * var6 + var4, 0.25F * var5 + var3, 0.9117647F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color7, this.decodeColor(this.color7, this.color8, 0.5F), this.color8});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.13770053F, 0.27540106F, 0.4906417F, 0.7058824F}, new Color[]{this.color9, this.decodeColor(this.color9, this.color10, 0.5F), this.color10, this.decodeColor(this.color10, this.color11, 0.5F), this.color11});
   }
}
