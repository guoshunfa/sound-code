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

final class ScrollBarButtonPainter extends AbstractRegionPainter {
   static final int FOREGROUND_ENABLED = 1;
   static final int FOREGROUND_DISABLED = 2;
   static final int FOREGROUND_MOUSEOVER = 3;
   static final int FOREGROUND_PRESSED = 4;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = new Color(255, 200, 0, 255);
   private Color color2 = this.decodeColor("nimbusBlueGrey", -0.01111114F, -0.07763158F, -0.1490196F, 0);
   private Color color3 = this.decodeColor("nimbusBlueGrey", -0.111111104F, -0.10580933F, 0.086274505F, 0);
   private Color color4 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.102261856F, 0.20392156F, 0);
   private Color color5 = this.decodeColor("nimbusBlueGrey", -0.039682567F, -0.079276316F, 0.13333333F, 0);
   private Color color6 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.07382907F, 0.109803915F, 0);
   private Color color7 = this.decodeColor("nimbusBlueGrey", -0.039682567F, -0.08241387F, 0.23137254F, 0);
   private Color color8 = this.decodeColor("nimbusBlueGrey", -0.055555522F, -0.08443936F, -0.29411766F, -136);
   private Color color9 = this.decodeColor("nimbusBlueGrey", -0.055555522F, -0.09876161F, 0.25490195F, -178);
   private Color color10 = this.decodeColor("nimbusBlueGrey", 0.055555582F, -0.08878718F, -0.5647059F, 0);
   private Color color11 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.080223285F, -0.4862745F, 0);
   private Color color12 = this.decodeColor("nimbusBlueGrey", -0.111111104F, -0.09525914F, -0.23137254F, 0);
   private Color color13 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -165);
   private Color color14 = this.decodeColor("nimbusBlueGrey", -0.04444444F, -0.080223285F, -0.09803921F, 0);
   private Color color15 = this.decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, 0.10588235F, 0);
   private Color color16 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
   private Color color17 = this.decodeColor("nimbusBlueGrey", -0.039682567F, -0.081719734F, 0.20784312F, 0);
   private Color color18 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.07677104F, 0.18431371F, 0);
   private Color color19 = this.decodeColor("nimbusBlueGrey", -0.04444444F, -0.080223285F, -0.09803921F, -69);
   private Color color20 = this.decodeColor("nimbusBlueGrey", -0.055555522F, -0.09876161F, 0.25490195F, -39);
   private Color color21 = this.decodeColor("nimbusBlueGrey", 0.055555582F, -0.0951417F, -0.49019608F, 0);
   private Color color22 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.086996906F, -0.4117647F, 0);
   private Color color23 = this.decodeColor("nimbusBlueGrey", -0.111111104F, -0.09719298F, -0.15686274F, 0);
   private Color color24 = this.decodeColor("nimbusBlueGrey", -0.037037015F, -0.043859646F, -0.21568626F, 0);
   private Color color25 = this.decodeColor("nimbusBlueGrey", -0.06349206F, -0.07309316F, -0.011764705F, 0);
   private Color color26 = this.decodeColor("nimbusBlueGrey", -0.048611104F, -0.07296763F, 0.09019607F, 0);
   private Color color27 = this.decodeColor("nimbusBlueGrey", -0.03535354F, -0.05497076F, 0.031372547F, 0);
   private Color color28 = this.decodeColor("nimbusBlueGrey", -0.034188032F, -0.043168806F, 0.011764705F, 0);
   private Color color29 = this.decodeColor("nimbusBlueGrey", -0.03535354F, -0.0600676F, 0.109803915F, 0);
   private Color color30 = this.decodeColor("nimbusBlueGrey", -0.037037015F, -0.043859646F, -0.21568626F, -44);
   private Color color31 = this.decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.74509805F, 0);
   private Object[] componentColors;

   public ScrollBarButtonPainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 1:
         this.paintForegroundEnabled(var1);
         break;
      case 2:
         this.paintForegroundDisabled(var1);
         break;
      case 3:
         this.paintForegroundMouseOver(var1);
         break;
      case 4:
         this.paintForegroundPressed(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintForegroundEnabled(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color1);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient1(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient2(this.path));
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.decodeGradient3(this.path));
      var1.fill(this.path);
      this.path = this.decodePath5();
      var1.setPaint(this.color13);
      var1.fill(this.path);
   }

   private void paintForegroundDisabled(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color1);
      var1.fill(this.path);
   }

   private void paintForegroundMouseOver(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color1);
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
      this.path = this.decodePath5();
      var1.setPaint(this.color13);
      var1.fill(this.path);
   }

   private void paintForegroundPressed(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color1);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient7(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient8(this.path));
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.color31);
      var1.fill(this.path);
      this.path = this.decodePath5();
      var1.setPaint(this.color13);
      var1.fill(this.path);
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(3.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(3.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(1.6956522F), (double)this.decodeY(0.0F));
      this.path.curveTo((double)this.decodeAnchorX(1.6956522F, 0.0F), (double)this.decodeAnchorY(0.0F, 0.0F), (double)this.decodeAnchorX(1.6956522F, -0.7058824F), (double)this.decodeAnchorY(1.3076923F, -3.0294118F), (double)this.decodeX(1.6956522F), (double)this.decodeY(1.3076923F));
      this.path.curveTo((double)this.decodeAnchorX(1.6956522F, 0.7058824F), (double)this.decodeAnchorY(1.3076923F, 3.0294118F), (double)this.decodeAnchorX(1.826087F, -2.0F), (double)this.decodeAnchorY(1.7692308F, -1.9411764F), (double)this.decodeX(1.826087F), (double)this.decodeY(1.7692308F));
      this.path.curveTo((double)this.decodeAnchorX(1.826087F, 2.0F), (double)this.decodeAnchorY(1.7692308F, 1.9411764F), (double)this.decodeAnchorX(3.0F, 0.0F), (double)this.decodeAnchorY(2.0F, 0.0F), (double)this.decodeX(3.0F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath3() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(1.0022625F));
      this.path.lineTo((double)this.decodeX(0.9705882F), (double)this.decodeY(1.0384616F));
      this.path.lineTo((double)this.decodeX(1.0409207F), (double)this.decodeY(1.0791855F));
      this.path.lineTo((double)this.decodeX(1.0409207F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(1.0022625F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath4() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.4782609F), (double)this.decodeY(1.2307693F));
      this.path.lineTo((double)this.decodeX(1.4782609F), (double)this.decodeY(1.7692308F));
      this.path.lineTo((double)this.decodeX(1.1713555F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(1.4782609F), (double)this.decodeY(1.2307693F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath5() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.6713555F), (double)this.decodeY(1.0769231F));
      this.path.curveTo((double)this.decodeAnchorX(1.6713555F, 0.7352941F), (double)this.decodeAnchorY(1.0769231F, 0.0F), (double)this.decodeAnchorX(1.7186701F, -0.9117647F), (double)this.decodeAnchorY(1.4095023F, -2.2058823F), (double)this.decodeX(1.7186701F), (double)this.decodeY(1.4095023F));
      this.path.curveTo((double)this.decodeAnchorX(1.7186701F, 0.9117647F), (double)this.decodeAnchorY(1.4095023F, 2.2058823F), (double)this.decodeAnchorX(1.8439897F, -2.3529413F), (double)this.decodeAnchorY(1.7941177F, -1.8529412F), (double)this.decodeX(1.8439897F), (double)this.decodeY(1.7941177F));
      this.path.curveTo((double)this.decodeAnchorX(1.8439897F, 2.3529413F), (double)this.decodeAnchorY(1.7941177F, 1.8529412F), (double)this.decodeAnchorX(2.5F, 0.0F), (double)this.decodeAnchorY(2.2352943F, 0.0F), (double)this.decodeX(2.5F), (double)this.decodeY(2.2352943F));
      this.path.lineTo((double)this.decodeX(2.3529415F), (double)this.decodeY(2.8235292F));
      this.path.curveTo((double)this.decodeAnchorX(2.3529415F, 0.0F), (double)this.decodeAnchorY(2.8235292F, 0.0F), (double)this.decodeAnchorX(1.8184143F, 1.5588236F), (double)this.decodeAnchorY(1.8438914F, 1.382353F), (double)this.decodeX(1.8184143F), (double)this.decodeY(1.8438914F));
      this.path.curveTo((double)this.decodeAnchorX(1.8184143F, -1.5588236F), (double)this.decodeAnchorY(1.8438914F, -1.382353F), (double)this.decodeAnchorX(1.6943734F, 0.7941176F), (double)this.decodeAnchorY(1.4841628F, 2.0F), (double)this.decodeX(1.6943734F), (double)this.decodeY(1.4841628F));
      this.path.curveTo((double)this.decodeAnchorX(1.6943734F, -0.7941176F), (double)this.decodeAnchorY(1.4841628F, -2.0F), (double)this.decodeAnchorX(1.6713555F, -0.7352941F), (double)this.decodeAnchorY(1.0769231F, 0.0F), (double)this.decodeX(1.6713555F), (double)this.decodeY(1.0769231F));
      this.path.closePath();
      return this.path;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.032934133F, 0.065868266F, 0.089820355F, 0.11377245F, 0.23053892F, 0.3473054F, 0.494012F, 0.6407186F, 0.78443116F, 0.92814374F}, new Color[]{this.color2, this.decodeColor(this.color2, this.color3, 0.5F), this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4, this.decodeColor(this.color4, this.color5, 0.5F), this.color5, this.decodeColor(this.color5, this.color6, 0.5F), this.color6, this.decodeColor(this.color6, this.color7, 0.5F), this.color7});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.0F * var5 + var3, 0.5F * var6 + var4, 0.5735294F * var5 + var3, 0.5F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color8, this.decodeColor(this.color8, this.color9, 0.5F), this.color9});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.925F * var5 + var3, 0.9285714F * var6 + var4, 0.925F * var5 + var3, 0.004201681F * var6 + var4, new float[]{0.0F, 0.2964072F, 0.5928144F, 0.79341316F, 0.994012F}, new Color[]{this.color10, this.decodeColor(this.color10, this.color11, 0.5F), this.color11, this.decodeColor(this.color11, this.color12, 0.5F), this.color12});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.032934133F, 0.065868266F, 0.089820355F, 0.11377245F, 0.23053892F, 0.3473054F, 0.494012F, 0.6407186F, 0.78443116F, 0.92814374F}, new Color[]{this.color14, this.decodeColor(this.color14, this.color15, 0.5F), this.color15, this.decodeColor(this.color15, this.color16, 0.5F), this.color16, this.decodeColor(this.color16, this.color17, 0.5F), this.color17, this.decodeColor(this.color17, this.color18, 0.5F), this.color18, this.decodeColor(this.color18, this.color16, 0.5F), this.color16});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.0F * var5 + var3, 0.5F * var6 + var4, 0.5735294F * var5 + var3, 0.5F * var6 + var4, new float[]{0.19518717F, 0.5975936F, 1.0F}, new Color[]{this.color19, this.decodeColor(this.color19, this.color20, 0.5F), this.color20});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.925F * var5 + var3, 0.9285714F * var6 + var4, 0.925F * var5 + var3, 0.004201681F * var6 + var4, new float[]{0.0F, 0.2964072F, 0.5928144F, 0.79341316F, 0.994012F}, new Color[]{this.color21, this.decodeColor(this.color21, this.color22, 0.5F), this.color22, this.decodeColor(this.color22, this.color23, 0.5F), this.color23});
   }

   private Paint decodeGradient7(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.032934133F, 0.065868266F, 0.089820355F, 0.11377245F, 0.23053892F, 0.3473054F, 0.494012F, 0.6407186F, 0.78443116F, 0.92814374F}, new Color[]{this.color24, this.decodeColor(this.color24, this.color25, 0.5F), this.color25, this.decodeColor(this.color25, this.color26, 0.5F), this.color26, this.decodeColor(this.color26, this.color27, 0.5F), this.color27, this.decodeColor(this.color27, this.color28, 0.5F), this.color28, this.decodeColor(this.color28, this.color29, 0.5F), this.color29});
   }

   private Paint decodeGradient8(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.0F * var5 + var3, 0.5F * var6 + var4, 0.5735294F * var5 + var3, 0.5F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color30, this.decodeColor(this.color30, this.color9, 0.5F), this.color9});
   }
}
