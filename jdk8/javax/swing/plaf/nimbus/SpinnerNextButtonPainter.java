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

final class SpinnerNextButtonPainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED = 1;
   static final int BACKGROUND_ENABLED = 2;
   static final int BACKGROUND_FOCUSED = 3;
   static final int BACKGROUND_MOUSEOVER_FOCUSED = 4;
   static final int BACKGROUND_PRESSED_FOCUSED = 5;
   static final int BACKGROUND_MOUSEOVER = 6;
   static final int BACKGROUND_PRESSED = 7;
   static final int FOREGROUND_DISABLED = 8;
   static final int FOREGROUND_ENABLED = 9;
   static final int FOREGROUND_FOCUSED = 10;
   static final int FOREGROUND_MOUSEOVER_FOCUSED = 11;
   static final int FOREGROUND_PRESSED_FOCUSED = 12;
   static final int FOREGROUND_MOUSEOVER = 13;
   static final int FOREGROUND_PRESSED = 14;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBase", 0.021348298F, -0.56289876F, 0.2588235F, 0);
   private Color color2 = this.decodeColor("nimbusBase", 0.010237217F, -0.5607143F, 0.2352941F, 0);
   private Color color3 = this.decodeColor("nimbusBase", 0.021348298F, -0.59223604F, 0.35294116F, 0);
   private Color color4 = this.decodeColor("nimbusBase", 0.016586483F, -0.5723659F, 0.31764704F, 0);
   private Color color5 = this.decodeColor("nimbusBase", 0.021348298F, -0.56182265F, 0.24705881F, 0);
   private Color color6 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.34585923F, -0.007843137F, 0);
   private Color color7 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.27207792F, -0.11764708F, 0);
   private Color color8 = this.decodeColor("nimbusBase", 0.004681647F, -0.6197143F, 0.43137252F, 0);
   private Color color9 = this.decodeColor("nimbusBase", -0.0012707114F, -0.5078604F, 0.3098039F, 0);
   private Color color10 = this.decodeColor("nimbusBase", -0.0028941035F, -0.4800539F, 0.28235292F, 0);
   private Color color11 = this.decodeColor("nimbusBase", 0.0023007393F, -0.3622768F, -0.04705882F, 0);
   private Color color12 = this.decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
   private Color color13 = this.decodeColor("nimbusBase", 0.0013483167F, -0.1769987F, -0.12156865F, 0);
   private Color color14 = this.decodeColor("nimbusBase", 0.0013483167F, 0.039961398F, -0.25882354F, 0);
   private Color color15 = this.decodeColor("nimbusBase", 0.004681647F, -0.6198413F, 0.43921566F, 0);
   private Color color16 = this.decodeColor("nimbusBase", -0.0012707114F, -0.51502466F, 0.3607843F, 0);
   private Color color17 = this.decodeColor("nimbusBase", 0.0021564364F, -0.49097747F, 0.34509802F, 0);
   private Color color18 = this.decodeColor("nimbusBase", 5.2034855E-5F, -0.38743842F, 0.019607842F, 0);
   private Color color19 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
   private Color color20 = this.decodeColor("nimbusBase", 0.08801502F, 0.3642857F, -0.454902F, 0);
   private Color color21 = this.decodeColor("nimbusBase", -4.2033195E-4F, -0.38050595F, 0.20392156F, 0);
   private Color color22 = this.decodeColor("nimbusBase", 2.9569864E-4F, -0.15470162F, 0.07058823F, 0);
   private Color color23 = this.decodeColor("nimbusBase", -4.6235323E-4F, -0.09571427F, 0.039215684F, 0);
   private Color color24 = this.decodeColor("nimbusBase", 0.018363237F, 0.18135887F, -0.227451F, 0);
   private Color color25 = new Color(255, 200, 0, 255);
   private Color color26 = this.decodeColor("nimbusBase", 0.021348298F, -0.58106947F, 0.16862744F, 0);
   private Color color27 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.043137252F, 0);
   private Color color28 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.24313727F, 0);
   private Color color29 = this.decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, 0);
   private Object[] componentColors;

   public SpinnerNextButtonPainter(AbstractRegionPainter.PaintContext var1, int var2) {
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
         this.paintBackgroundFocused(var1);
         break;
      case 4:
         this.paintBackgroundMouseOverAndFocused(var1);
         break;
      case 5:
         this.paintBackgroundPressedAndFocused(var1);
         break;
      case 6:
         this.paintBackgroundMouseOver(var1);
         break;
      case 7:
         this.paintBackgroundPressed(var1);
         break;
      case 8:
         this.paintForegroundDisabled(var1);
         break;
      case 9:
         this.paintForegroundEnabled(var1);
         break;
      case 10:
         this.paintForegroundFocused(var1);
         break;
      case 11:
         this.paintForegroundMouseOverAndFocused(var1);
         break;
      case 12:
         this.paintForegroundPressedAndFocused(var1);
         break;
      case 13:
         this.paintForegroundMouseOver(var1);
         break;
      case 14:
         this.paintForegroundPressed(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundDisabled(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient1(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient2(this.path));
      var1.fill(this.path);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color5);
      var1.fill(this.rect);
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient3(this.path));
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.decodeGradient4(this.path));
      var1.fill(this.path);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color11);
      var1.fill(this.rect);
   }

   private void paintBackgroundFocused(Graphics2D var1) {
      this.path = this.decodePath5();
      var1.setPaint(this.color12);
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient3(this.path));
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.decodeGradient5(this.path));
      var1.fill(this.path);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color11);
      var1.fill(this.rect);
   }

   private void paintBackgroundMouseOverAndFocused(Graphics2D var1) {
      this.path = this.decodePath5();
      var1.setPaint(this.color12);
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient6(this.path));
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.decodeGradient7(this.path));
      var1.fill(this.path);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color18);
      var1.fill(this.rect);
   }

   private void paintBackgroundPressedAndFocused(Graphics2D var1) {
      this.path = this.decodePath5();
      var1.setPaint(this.color12);
      var1.fill(this.path);
      this.path = this.decodePath6();
      var1.setPaint(this.decodeGradient8(this.path));
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.decodeGradient9(this.path));
      var1.fill(this.path);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color24);
      var1.fill(this.rect);
   }

   private void paintBackgroundMouseOver(Graphics2D var1) {
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient6(this.path));
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.decodeGradient10(this.path));
      var1.fill(this.path);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color18);
      var1.fill(this.rect);
   }

   private void paintBackgroundPressed(Graphics2D var1) {
      this.path = this.decodePath6();
      var1.setPaint(this.decodeGradient8(this.path));
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.decodeGradient11(this.path));
      var1.fill(this.path);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color24);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color25);
      var1.fill(this.rect);
   }

   private void paintForegroundDisabled(Graphics2D var1) {
      this.path = this.decodePath7();
      var1.setPaint(this.color26);
      var1.fill(this.path);
   }

   private void paintForegroundEnabled(Graphics2D var1) {
      this.path = this.decodePath7();
      var1.setPaint(this.decodeGradient12(this.path));
      var1.fill(this.path);
   }

   private void paintForegroundFocused(Graphics2D var1) {
      this.path = this.decodePath8();
      var1.setPaint(this.decodeGradient12(this.path));
      var1.fill(this.path);
   }

   private void paintForegroundMouseOverAndFocused(Graphics2D var1) {
      this.path = this.decodePath8();
      var1.setPaint(this.decodeGradient12(this.path));
      var1.fill(this.path);
   }

   private void paintForegroundPressedAndFocused(Graphics2D var1) {
      this.path = this.decodePath9();
      var1.setPaint(this.color29);
      var1.fill(this.path);
   }

   private void paintForegroundMouseOver(Graphics2D var1) {
      this.path = this.decodePath7();
      var1.setPaint(this.decodeGradient12(this.path));
      var1.fill(this.path);
   }

   private void paintForegroundPressed(Graphics2D var1) {
      this.path = this.decodePath9();
      var1.setPaint(this.color29);
      var1.fill(this.path);
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.2857143F));
      this.path.curveTo((double)this.decodeAnchorX(0.0F, 0.0F), (double)this.decodeAnchorY(0.2857143F, 0.0F), (double)this.decodeAnchorX(2.0F, -3.6363637F), (double)this.decodeAnchorY(0.2857143F, 0.0F), (double)this.decodeX(2.0F), (double)this.decodeY(0.2857143F));
      this.path.curveTo((double)this.decodeAnchorX(2.0F, 3.6363637F), (double)this.decodeAnchorY(0.2857143F, 0.0F), (double)this.decodeAnchorX(2.7142859F, -0.022727273F), (double)this.decodeAnchorY(1.0F, -3.75F), (double)this.decodeX(2.7142859F), (double)this.decodeY(1.0F));
      this.path.curveTo((double)this.decodeAnchorX(2.7142859F, 0.022727273F), (double)this.decodeAnchorY(1.0F, 3.75F), (double)this.decodeAnchorX(2.7142859F, 0.0F), (double)this.decodeAnchorY(3.0F, 0.0F), (double)this.decodeX(2.7142859F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(0.42857143F));
      this.path.curveTo((double)this.decodeAnchorX(1.0F, 0.0F), (double)this.decodeAnchorY(0.42857143F, 0.0F), (double)this.decodeAnchorX(2.0F, -3.0F), (double)this.decodeAnchorY(0.42857143F, 0.0F), (double)this.decodeX(2.0F), (double)this.decodeY(0.42857143F));
      this.path.curveTo((double)this.decodeAnchorX(2.0F, 3.0F), (double)this.decodeAnchorY(0.42857143F, 0.0F), (double)this.decodeAnchorX(2.5714285F, 0.0F), (double)this.decodeAnchorY(1.0F, -2.0F), (double)this.decodeX(2.5714285F), (double)this.decodeY(1.0F));
      this.path.curveTo((double)this.decodeAnchorX(2.5714285F, 0.0F), (double)this.decodeAnchorY(1.0F, 2.0F), (double)this.decodeAnchorX(2.5714285F, 0.0F), (double)this.decodeAnchorY(2.0F, 0.0F), (double)this.decodeX(2.5714285F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(2.0F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(2.0F), (double)(this.decodeX(2.5714285F) - this.decodeX(1.0F)), (double)(this.decodeY(3.0F) - this.decodeY(2.0F)));
      return this.rect;
   }

   private Path2D decodePath3() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.2857143F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(0.2857143F));
      this.path.curveTo((double)this.decodeAnchorX(2.0F, 3.6363637F), (double)this.decodeAnchorY(0.2857143F, 0.0F), (double)this.decodeAnchorX(2.7142859F, -0.022727273F), (double)this.decodeAnchorY(1.0F, -3.75F), (double)this.decodeX(2.7142859F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.7142859F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath4() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(0.42857143F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(0.42857143F));
      this.path.curveTo((double)this.decodeAnchorX(2.0F, 3.0F), (double)this.decodeAnchorY(0.42857143F, 0.0F), (double)this.decodeAnchorX(2.5714285F, 0.0F), (double)this.decodeAnchorY(1.0F, -2.0F), (double)this.decodeX(2.5714285F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.5714285F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(2.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath5() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.08571429F));
      this.path.lineTo((double)this.decodeX(2.142857F), (double)this.decodeY(0.08571429F));
      this.path.curveTo((double)this.decodeAnchorX(2.142857F, 3.4F), (double)this.decodeAnchorY(0.08571429F, 0.0F), (double)this.decodeAnchorX(2.9142857F, 0.0F), (double)this.decodeAnchorY(1.0F, -3.4F), (double)this.decodeX(2.9142857F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.9142857F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath6() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.2857143F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(0.2857143F));
      this.path.curveTo((double)this.decodeAnchorX(2.0F, 3.4545455F), (double)this.decodeAnchorY(0.2857143F, 0.0F), (double)this.decodeAnchorX(2.7142859F, -0.022727273F), (double)this.decodeAnchorY(1.0F, -3.4772727F), (double)this.decodeX(2.7142859F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.7142859F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect2() {
      this.rect.setRect((double)this.decodeX(0.0F), (double)this.decodeY(0.0F), (double)(this.decodeX(0.0F) - this.decodeX(0.0F)), (double)(this.decodeY(0.0F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Path2D decodePath7() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(1.490909F), (double)this.decodeY(1.0284091F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(2.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath8() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(1.490909F), (double)this.decodeY(1.3522727F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(2.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath9() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(1.5045455F), (double)this.decodeY(1.0795455F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(2.0F));
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
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color6, this.decodeColor(this.color6, this.color7, 0.5F), this.color7});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.36497328F, 0.72994655F, 0.8649733F, 1.0F}, new Color[]{this.color8, this.decodeColor(this.color8, this.color9, 0.5F), this.color9, this.decodeColor(this.color9, this.color10, 0.5F), this.color10});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.37566844F, 0.7513369F, 0.8756684F, 1.0F}, new Color[]{this.color8, this.decodeColor(this.color8, this.color9, 0.5F), this.color9, this.decodeColor(this.color9, this.color10, 0.5F), this.color10});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color13, this.decodeColor(this.color13, this.color14, 0.5F), this.color14});
   }

   private Paint decodeGradient7(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.37967914F, 0.7593583F, 0.87967914F, 1.0F}, new Color[]{this.color15, this.decodeColor(this.color15, this.color16, 0.5F), this.color16, this.decodeColor(this.color16, this.color17, 0.5F), this.color17});
   }

   private Paint decodeGradient8(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color19, this.decodeColor(this.color19, this.color20, 0.5F), this.color20});
   }

   private Paint decodeGradient9(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.37165776F, 0.7433155F, 0.8716577F, 1.0F}, new Color[]{this.color21, this.decodeColor(this.color21, this.color22, 0.5F), this.color22, this.decodeColor(this.color22, this.color23, 0.5F), this.color23});
   }

   private Paint decodeGradient10(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.3970588F, 0.7941176F, 0.89705884F, 1.0F}, new Color[]{this.color15, this.decodeColor(this.color15, this.color16, 0.5F), this.color16, this.decodeColor(this.color16, this.color17, 0.5F), this.color17});
   }

   private Paint decodeGradient11(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.4318182F, 0.8636364F, 0.9318182F, 1.0F}, new Color[]{this.color21, this.decodeColor(this.color21, this.color22, 0.5F), this.color22, this.decodeColor(this.color22, this.color23, 0.5F), this.color23});
   }

   private Paint decodeGradient12(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.48636365F * var5 + var3, 0.0116959065F * var6 + var4, 0.4909091F * var5 + var3, 0.8888889F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color27, this.decodeColor(this.color27, this.color28, 0.5F), this.color28});
   }
}
