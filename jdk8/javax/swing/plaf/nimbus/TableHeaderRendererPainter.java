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

final class TableHeaderRendererPainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED = 1;
   static final int BACKGROUND_ENABLED = 2;
   static final int BACKGROUND_ENABLED_FOCUSED = 3;
   static final int BACKGROUND_MOUSEOVER = 4;
   static final int BACKGROUND_PRESSED = 5;
   static final int BACKGROUND_ENABLED_SORTED = 6;
   static final int BACKGROUND_ENABLED_FOCUSED_SORTED = 7;
   static final int BACKGROUND_DISABLED_SORTED = 8;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBorder", -0.013888836F, 5.823001E-4F, -0.12941176F, 0);
   private Color color2 = this.decodeColor("nimbusBlueGrey", -0.01111114F, -0.08625447F, 0.062745094F, 0);
   private Color color3 = this.decodeColor("nimbusBlueGrey", -0.013888836F, -0.028334536F, -0.17254901F, 0);
   private Color color4 = this.decodeColor("nimbusBlueGrey", -0.013888836F, -0.029445238F, -0.16470587F, 0);
   private Color color5 = this.decodeColor("nimbusBlueGrey", -0.02020204F, -0.053531498F, 0.011764705F, 0);
   private Color color6 = this.decodeColor("nimbusBlueGrey", 0.055555582F, -0.10655806F, 0.24313724F, 0);
   private Color color7 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.08455229F, 0.1607843F, 0);
   private Color color8 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.07016757F, 0.12941176F, 0);
   private Color color9 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.07466974F, 0.23921567F, 0);
   private Color color10 = this.decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
   private Color color11 = this.decodeColor("nimbusBlueGrey", 0.055555582F, -0.10658931F, 0.25098038F, 0);
   private Color color12 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.08613607F, 0.21960783F, 0);
   private Color color13 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.07333623F, 0.20392156F, 0);
   private Color color14 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
   private Color color15 = this.decodeColor("nimbusBlueGrey", -0.00505054F, -0.05960039F, 0.10196078F, 0);
   private Color color16 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.017742813F, 0.015686274F, 0);
   private Color color17 = this.decodeColor("nimbusBlueGrey", -0.0027777553F, -0.0018306673F, -0.02352941F, 0);
   private Color color18 = this.decodeColor("nimbusBlueGrey", 0.0055555105F, -0.020436227F, 0.12549019F, 0);
   private Color color19 = this.decodeColor("nimbusBase", -0.023096085F, -0.62376213F, 0.4352941F, 0);
   private Color color20 = this.decodeColor("nimbusBase", -0.0012707114F, -0.50901747F, 0.31764704F, 0);
   private Color color21 = this.decodeColor("nimbusBase", -0.002461195F, -0.47139505F, 0.2862745F, 0);
   private Color color22 = this.decodeColor("nimbusBase", -0.0051222444F, -0.49103343F, 0.372549F, 0);
   private Color color23 = this.decodeColor("nimbusBase", -8.738637E-4F, -0.49872798F, 0.3098039F, 0);
   private Color color24 = this.decodeColor("nimbusBase", -2.2029877E-4F, -0.4916465F, 0.37647057F, 0);
   private Object[] componentColors;

   public TableHeaderRendererPainter(AbstractRegionPainter.PaintContext var1, int var2) {
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
         this.paintBackgroundEnabledAndFocused(var1);
         break;
      case 4:
         this.paintBackgroundMouseOver(var1);
         break;
      case 5:
         this.paintBackgroundPressed(var1);
         break;
      case 6:
         this.paintBackgroundEnabledAndSorted(var1);
         break;
      case 7:
         this.paintBackgroundEnabledAndFocusedAndSorted(var1);
         break;
      case 8:
         this.paintBackgroundDisabledAndSorted(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundDisabled(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient1(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.decodeGradient2(this.rect));
      var1.fill(this.rect);
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient1(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.decodeGradient2(this.rect));
      var1.fill(this.rect);
   }

   private void paintBackgroundEnabledAndFocused(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient1(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.decodeGradient2(this.rect));
      var1.fill(this.rect);
      this.path = this.decodePath1();
      var1.setPaint(this.color10);
      var1.fill(this.path);
   }

   private void paintBackgroundMouseOver(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient1(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.decodeGradient3(this.rect));
      var1.fill(this.rect);
   }

   private void paintBackgroundPressed(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient1(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.decodeGradient4(this.rect));
      var1.fill(this.rect);
   }

   private void paintBackgroundEnabledAndSorted(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient1(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.decodeGradient5(this.rect));
      var1.fill(this.rect);
   }

   private void paintBackgroundEnabledAndFocusedAndSorted(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient1(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.decodeGradient6(this.rect));
      var1.fill(this.rect);
      this.path = this.decodePath1();
      var1.setPaint(this.color10);
      var1.fill(this.path);
   }

   private void paintBackgroundDisabledAndSorted(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient1(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.decodeGradient2(this.rect));
      var1.fill(this.rect);
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(0.0F), (double)this.decodeY(2.8F), (double)(this.decodeX(3.0F) - this.decodeX(0.0F)), (double)(this.decodeY(3.0F) - this.decodeY(2.8F)));
      return this.rect;
   }

   private Rectangle2D decodeRect2() {
      this.rect.setRect((double)this.decodeX(2.8F), (double)this.decodeY(0.0F), (double)(this.decodeX(3.0F) - this.decodeX(2.8F)), (double)(this.decodeY(2.8F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect3() {
      this.rect.setRect((double)this.decodeX(0.0F), (double)this.decodeY(0.0F), (double)(this.decodeX(2.8F) - this.decodeX(0.0F)), (double)(this.decodeY(2.8F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.24000001F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.24000001F), (double)this.decodeY(0.24000001F));
      this.path.lineTo((double)this.decodeX(2.7599998F), (double)this.decodeY(0.24000001F));
      this.path.lineTo((double)this.decodeX(2.7599998F), (double)this.decodeY(2.7599998F));
      this.path.lineTo((double)this.decodeX(0.24000001F), (double)this.decodeY(2.7599998F));
      this.path.lineTo((double)this.decodeX(0.24000001F), (double)this.decodeY(0.0F));
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
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.14441223F, 0.43703705F, 0.59444445F, 0.75185186F, 0.8759259F, 1.0F}, new Color[]{this.color2, this.decodeColor(this.color2, this.color3, 0.5F), this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4, this.decodeColor(this.color4, this.color5, 0.5F), this.color5});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.07147767F, 0.2888889F, 0.5490909F, 0.7037037F, 0.8518518F, 1.0F}, new Color[]{this.color6, this.decodeColor(this.color6, this.color7, 0.5F), this.color7, this.decodeColor(this.color7, this.color8, 0.5F), this.color8, this.decodeColor(this.color8, this.color9, 0.5F), this.color9});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.07147767F, 0.2888889F, 0.5490909F, 0.7037037F, 0.7919203F, 0.88013697F}, new Color[]{this.color11, this.decodeColor(this.color11, this.color12, 0.5F), this.color12, this.decodeColor(this.color12, this.color13, 0.5F), this.color13, this.decodeColor(this.color13, this.color14, 0.5F), this.color14});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.07147767F, 0.2888889F, 0.5490909F, 0.7037037F, 0.8518518F, 1.0F}, new Color[]{this.color15, this.decodeColor(this.color15, this.color16, 0.5F), this.color16, this.decodeColor(this.color16, this.color17, 0.5F), this.color17, this.decodeColor(this.color17, this.color18, 0.5F), this.color18});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.08049711F, 0.32534248F, 0.56267816F, 0.7037037F, 0.83986557F, 0.97602737F}, new Color[]{this.color19, this.decodeColor(this.color19, this.color20, 0.5F), this.color20, this.decodeColor(this.color20, this.color21, 0.5F), this.color21, this.decodeColor(this.color21, this.color22, 0.5F), this.color22});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.07147767F, 0.2888889F, 0.5490909F, 0.7037037F, 0.8518518F, 1.0F}, new Color[]{this.color19, this.decodeColor(this.color19, this.color23, 0.5F), this.color23, this.decodeColor(this.color23, this.color21, 0.5F), this.color21, this.decodeColor(this.color21, this.color24, 0.5F), this.color24});
   }
}
