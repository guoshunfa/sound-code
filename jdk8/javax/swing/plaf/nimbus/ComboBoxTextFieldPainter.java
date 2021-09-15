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

final class ComboBoxTextFieldPainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED = 1;
   static final int BACKGROUND_ENABLED = 2;
   static final int BACKGROUND_SELECTED = 3;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.74509805F, -237);
   private Color color2 = this.decodeColor("nimbusBlueGrey", -0.006944418F, -0.07187897F, 0.06666666F, 0);
   private Color color3 = this.decodeColor("nimbusBlueGrey", 0.007936537F, -0.07703349F, 0.0745098F, 0);
   private Color color4 = this.decodeColor("nimbusBlueGrey", 0.007936537F, -0.07968931F, 0.14509803F, 0);
   private Color color5 = this.decodeColor("nimbusBlueGrey", 0.007936537F, -0.07856284F, 0.11372548F, 0);
   private Color color6 = this.decodeColor("nimbusBase", 0.040395975F, -0.60315615F, 0.29411763F, 0);
   private Color color7 = this.decodeColor("nimbusBase", 0.016586483F, -0.6051466F, 0.3490196F, 0);
   private Color color8 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.0965403F, -0.18431371F, 0);
   private Color color9 = this.decodeColor("nimbusBlueGrey", 0.055555582F, -0.1048766F, -0.05098039F, 0);
   private Color color10 = this.decodeColor("nimbusLightBackground", 0.6666667F, 0.004901961F, -0.19999999F, 0);
   private Color color11 = this.decodeColor("nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
   private Color color12 = this.decodeColor("nimbusBlueGrey", 0.055555582F, -0.105344966F, 0.011764705F, 0);
   private Object[] componentColors;

   public ComboBoxTextFieldPainter(AbstractRegionPainter.PaintContext var1, int var2) {
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
         this.paintBackgroundSelected(var1);
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
      this.rect = this.decodeRect4();
      var1.setPaint(this.color6);
      var1.fill(this.rect);
      this.rect = this.decodeRect5();
      var1.setPaint(this.color7);
      var1.fill(this.rect);
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient3(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.decodeGradient4(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color12);
      var1.fill(this.rect);
      this.rect = this.decodeRect5();
      var1.setPaint(this.color11);
      var1.fill(this.rect);
   }

   private void paintBackgroundSelected(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.decodeGradient3(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.decodeGradient4(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color12);
      var1.fill(this.rect);
      this.rect = this.decodeRect5();
      var1.setPaint(this.color11);
      var1.fill(this.rect);
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(0.6666667F), (double)this.decodeY(2.3333333F), (double)(this.decodeX(3.0F) - this.decodeX(0.6666667F)), (double)(this.decodeY(2.6666667F) - this.decodeY(2.3333333F)));
      return this.rect;
   }

   private Rectangle2D decodeRect2() {
      this.rect.setRect((double)this.decodeX(0.6666667F), (double)this.decodeY(0.4F), (double)(this.decodeX(3.0F) - this.decodeX(0.6666667F)), (double)(this.decodeY(1.0F) - this.decodeY(0.4F)));
      return this.rect;
   }

   private Rectangle2D decodeRect3() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(0.6F), (double)(this.decodeX(3.0F) - this.decodeX(1.0F)), (double)(this.decodeY(1.0F) - this.decodeY(0.6F)));
      return this.rect;
   }

   private Rectangle2D decodeRect4() {
      this.rect.setRect((double)this.decodeX(0.6666667F), (double)this.decodeY(1.0F), (double)(this.decodeX(3.0F) - this.decodeX(0.6666667F)), (double)(this.decodeY(2.3333333F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect5() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(3.0F) - this.decodeX(1.0F)), (double)(this.decodeY(2.0F) - this.decodeY(1.0F)));
      return this.rect;
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
      return this.decodeGradient(0.5F * var5 + var3, 1.0F * var6 + var4, 0.5F * var5 + var3, 0.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color4, this.decodeColor(this.color4, this.color5, 0.5F), this.color5});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.49573863F, 0.99147725F}, new Color[]{this.color8, this.decodeColor(this.color8, this.color9, 0.5F), this.color9});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.1F, 0.49999997F, 0.9F}, new Color[]{this.color10, this.decodeColor(this.color10, this.color11, 0.5F), this.color11});
   }
}
