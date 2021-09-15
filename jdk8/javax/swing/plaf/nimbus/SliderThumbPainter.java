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

final class SliderThumbPainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED = 1;
   static final int BACKGROUND_ENABLED = 2;
   static final int BACKGROUND_FOCUSED = 3;
   static final int BACKGROUND_FOCUSED_MOUSEOVER = 4;
   static final int BACKGROUND_FOCUSED_PRESSED = 5;
   static final int BACKGROUND_MOUSEOVER = 6;
   static final int BACKGROUND_PRESSED = 7;
   static final int BACKGROUND_ENABLED_ARROWSHAPE = 8;
   static final int BACKGROUND_DISABLED_ARROWSHAPE = 9;
   static final int BACKGROUND_MOUSEOVER_ARROWSHAPE = 10;
   static final int BACKGROUND_PRESSED_ARROWSHAPE = 11;
   static final int BACKGROUND_FOCUSED_ARROWSHAPE = 12;
   static final int BACKGROUND_FOCUSED_MOUSEOVER_ARROWSHAPE = 13;
   static final int BACKGROUND_FOCUSED_PRESSED_ARROWSHAPE = 14;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBase", 0.021348298F, -0.5625436F, 0.25490195F, 0);
   private Color color2 = this.decodeColor("nimbusBase", 0.015098333F, -0.55105823F, 0.19215685F, 0);
   private Color color3 = this.decodeColor("nimbusBase", 0.021348298F, -0.5924243F, 0.35686272F, 0);
   private Color color4 = this.decodeColor("nimbusBase", 0.021348298F, -0.56722116F, 0.3098039F, 0);
   private Color color5 = this.decodeColor("nimbusBase", 0.021348298F, -0.56844974F, 0.32549018F, 0);
   private Color color6 = this.decodeColor("nimbusBlueGrey", -0.003968239F, 0.0014736876F, -0.25490198F, -156);
   private Color color7 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.34585923F, -0.007843137F, 0);
   private Color color8 = this.decodeColor("nimbusBase", -0.0017285943F, -0.11571431F, -0.25490198F, 0);
   private Color color9 = this.decodeColor("nimbusBase", -0.023096085F, -0.6238095F, 0.43921566F, 0);
   private Color color10 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.43866998F, 0.24705881F, 0);
   private Color color11 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.45714286F, 0.32941175F, 0);
   private Color color12 = this.decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
   private Color color13 = this.decodeColor("nimbusBase", -0.0038217902F, -0.15532213F, -0.14901963F, 0);
   private Color color14 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54509807F, 0);
   private Color color15 = this.decodeColor("nimbusBase", 0.004681647F, -0.62780917F, 0.44313723F, 0);
   private Color color16 = this.decodeColor("nimbusBase", 2.9569864E-4F, -0.4653107F, 0.32549018F, 0);
   private Color color17 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.4563421F, 0.32549018F, 0);
   private Color color18 = this.decodeColor("nimbusBase", -0.0017285943F, -0.4732143F, 0.39215684F, 0);
   private Color color19 = this.decodeColor("nimbusBase", 0.0015952587F, -0.04875779F, -0.18823531F, 0);
   private Color color20 = this.decodeColor("nimbusBase", 2.9569864E-4F, -0.44943976F, 0.25098038F, 0);
   private Color color21 = this.decodeColor("nimbusBase", 0.0F, 0.0F, 0.0F, 0);
   private Color color22 = this.decodeColor("nimbusBase", 8.9377165E-4F, -0.121094406F, 0.12156862F, 0);
   private Color color23 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -121);
   private Color color24 = new Color(150, 156, 168, 146);
   private Color color25 = this.decodeColor("nimbusBase", -0.0033828616F, -0.40608466F, -0.019607842F, 0);
   private Color color26 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.17594418F, -0.20784315F, 0);
   private Color color27 = this.decodeColor("nimbusBase", 0.0023007393F, -0.11332625F, -0.28627452F, 0);
   private Color color28 = this.decodeColor("nimbusBase", -0.023096085F, -0.62376213F, 0.4352941F, 0);
   private Color color29 = this.decodeColor("nimbusBase", 0.004681647F, -0.594392F, 0.39999998F, 0);
   private Color color30 = this.decodeColor("nimbusBase", -0.0017285943F, -0.4454704F, 0.25490195F, 0);
   private Color color31 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.4625541F, 0.35686272F, 0);
   private Color color32 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.47442397F, 0.4235294F, 0);
   private Object[] componentColors;

   public SliderThumbPainter(AbstractRegionPainter.PaintContext var1, int var2) {
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
         this.paintBackgroundFocusedAndMouseOver(var1);
         break;
      case 5:
         this.paintBackgroundFocusedAndPressed(var1);
         break;
      case 6:
         this.paintBackgroundMouseOver(var1);
         break;
      case 7:
         this.paintBackgroundPressed(var1);
         break;
      case 8:
         this.paintBackgroundEnabledAndArrowShape(var1);
         break;
      case 9:
         this.paintBackgroundDisabledAndArrowShape(var1);
         break;
      case 10:
         this.paintBackgroundMouseOverAndArrowShape(var1);
         break;
      case 11:
         this.paintBackgroundPressedAndArrowShape(var1);
         break;
      case 12:
         this.paintBackgroundFocusedAndArrowShape(var1);
         break;
      case 13:
         this.paintBackgroundFocusedAndMouseOverAndArrowShape(var1);
         break;
      case 14:
         this.paintBackgroundFocusedAndPressedAndArrowShape(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundDisabled(Graphics2D var1) {
      this.ellipse = this.decodeEllipse1();
      var1.setPaint(this.decodeGradient1(this.ellipse));
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse2();
      var1.setPaint(this.decodeGradient2(this.ellipse));
      var1.fill(this.ellipse);
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.ellipse = this.decodeEllipse3();
      var1.setPaint(this.color6);
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse1();
      var1.setPaint(this.decodeGradient3(this.ellipse));
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse2();
      var1.setPaint(this.decodeGradient4(this.ellipse));
      var1.fill(this.ellipse);
   }

   private void paintBackgroundFocused(Graphics2D var1) {
      this.ellipse = this.decodeEllipse4();
      var1.setPaint(this.color12);
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse1();
      var1.setPaint(this.decodeGradient3(this.ellipse));
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse2();
      var1.setPaint(this.decodeGradient4(this.ellipse));
      var1.fill(this.ellipse);
   }

   private void paintBackgroundFocusedAndMouseOver(Graphics2D var1) {
      this.ellipse = this.decodeEllipse4();
      var1.setPaint(this.color12);
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse1();
      var1.setPaint(this.decodeGradient5(this.ellipse));
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse2();
      var1.setPaint(this.decodeGradient6(this.ellipse));
      var1.fill(this.ellipse);
   }

   private void paintBackgroundFocusedAndPressed(Graphics2D var1) {
      this.ellipse = this.decodeEllipse4();
      var1.setPaint(this.color12);
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse1();
      var1.setPaint(this.decodeGradient7(this.ellipse));
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse2();
      var1.setPaint(this.decodeGradient8(this.ellipse));
      var1.fill(this.ellipse);
   }

   private void paintBackgroundMouseOver(Graphics2D var1) {
      this.ellipse = this.decodeEllipse3();
      var1.setPaint(this.color6);
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse1();
      var1.setPaint(this.decodeGradient5(this.ellipse));
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse2();
      var1.setPaint(this.decodeGradient6(this.ellipse));
      var1.fill(this.ellipse);
   }

   private void paintBackgroundPressed(Graphics2D var1) {
      this.ellipse = this.decodeEllipse3();
      var1.setPaint(this.color23);
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse1();
      var1.setPaint(this.decodeGradient7(this.ellipse));
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse2();
      var1.setPaint(this.decodeGradient8(this.ellipse));
      var1.fill(this.ellipse);
   }

   private void paintBackgroundEnabledAndArrowShape(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color24);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient9(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient10(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundDisabledAndArrowShape(Graphics2D var1) {
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient11(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient12(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundMouseOverAndArrowShape(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color24);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient13(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient14(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundPressedAndArrowShape(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color24);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient15(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient16(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundFocusedAndArrowShape(Graphics2D var1) {
      this.path = this.decodePath4();
      var1.setPaint(this.color12);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient9(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient17(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundFocusedAndMouseOverAndArrowShape(Graphics2D var1) {
      this.path = this.decodePath4();
      var1.setPaint(this.color12);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient13(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient14(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundFocusedAndPressedAndArrowShape(Graphics2D var1) {
      this.path = this.decodePath4();
      var1.setPaint(this.color12);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient15(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient16(this.path));
      var1.fill(this.path);
   }

   private Ellipse2D decodeEllipse1() {
      this.ellipse.setFrame((double)this.decodeX(0.4F), (double)this.decodeY(0.4F), (double)(this.decodeX(2.6F) - this.decodeX(0.4F)), (double)(this.decodeY(2.6F) - this.decodeY(0.4F)));
      return this.ellipse;
   }

   private Ellipse2D decodeEllipse2() {
      this.ellipse.setFrame((double)this.decodeX(0.6F), (double)this.decodeY(0.6F), (double)(this.decodeX(2.4F) - this.decodeX(0.6F)), (double)(this.decodeY(2.4F) - this.decodeY(0.6F)));
      return this.ellipse;
   }

   private Ellipse2D decodeEllipse3() {
      this.ellipse.setFrame((double)this.decodeX(0.4F), (double)this.decodeY(0.6F), (double)(this.decodeX(2.6F) - this.decodeX(0.4F)), (double)(this.decodeY(2.8F) - this.decodeY(0.6F)));
      return this.ellipse;
   }

   private Ellipse2D decodeEllipse4() {
      this.ellipse.setFrame((double)this.decodeX(0.120000005F), (double)this.decodeY(0.120000005F), (double)(this.decodeX(2.8799999F) - this.decodeX(0.120000005F)), (double)(this.decodeY(2.8799999F) - this.decodeY(0.120000005F)));
      return this.ellipse;
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.8166667F), (double)this.decodeY(0.5007576F));
      this.path.curveTo((double)this.decodeAnchorX(0.8166667F, 1.5643269F), (double)this.decodeAnchorY(0.5007576F, -0.3097513F), (double)this.decodeAnchorX(2.7925456F, 0.058173586F), (double)this.decodeAnchorY(1.6116884F, -0.4647635F), (double)this.decodeX(2.7925456F), (double)this.decodeY(1.6116884F));
      this.path.curveTo((double)this.decodeAnchorX(2.7925456F, -0.34086856F), (double)this.decodeAnchorY(1.6116884F, 2.7232852F), (double)this.decodeAnchorX(0.7006364F, 4.568128F), (double)this.decodeAnchorY(2.7693636F, -0.006014915F), (double)this.decodeX(0.7006364F), (double)this.decodeY(2.7693636F));
      this.path.curveTo((double)this.decodeAnchorX(0.7006364F, -3.5233955F), (double)this.decodeAnchorY(2.7693636F, 0.004639302F), (double)this.decodeAnchorX(0.8166667F, -1.8635255F), (double)this.decodeAnchorY(0.5007576F, 0.36899543F), (double)this.decodeX(0.8166667F), (double)this.decodeY(0.5007576F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.6155303F), (double)this.decodeY(2.5954547F));
      this.path.curveTo((double)this.decodeAnchorX(0.6155303F, 0.90980893F), (double)this.decodeAnchorY(2.5954547F, 1.3154242F), (double)this.decodeAnchorX(2.6151516F, 0.014588808F), (double)this.decodeAnchorY(1.6112013F, 0.9295521F), (double)this.decodeX(2.6151516F), (double)this.decodeY(1.6112013F));
      this.path.curveTo((double)this.decodeAnchorX(2.6151516F, -0.01365518F), (double)this.decodeAnchorY(1.6112013F, -0.8700643F), (double)this.decodeAnchorX(0.60923916F, 0.9729935F), (double)this.decodeAnchorY(0.40716404F, -1.4248644F), (double)this.decodeX(0.60923916F), (double)this.decodeY(0.40716404F));
      this.path.curveTo((double)this.decodeAnchorX(0.60923916F, -0.7485209F), (double)this.decodeAnchorY(0.40716404F, 1.0961438F), (double)this.decodeAnchorX(0.6155303F, -0.74998796F), (double)this.decodeAnchorY(2.5954547F, -1.0843511F), (double)this.decodeX(0.6155303F), (double)this.decodeY(2.5954547F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath3() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.8055606F), (double)this.decodeY(0.6009697F));
      this.path.curveTo((double)this.decodeAnchorX(0.8055606F, 0.50820893F), (double)this.decodeAnchorY(0.6009697F, -0.8490881F), (double)this.decodeAnchorX(2.3692727F, 0.0031846066F), (double)this.decodeAnchorY(1.613117F, -0.60668826F), (double)this.decodeX(2.3692727F), (double)this.decodeY(1.613117F));
      this.path.curveTo((double)this.decodeAnchorX(2.3692727F, -0.003890196F), (double)this.decodeAnchorY(1.613117F, 0.74110764F), (double)this.decodeAnchorX(0.7945455F, 0.3870974F), (double)this.decodeAnchorY(2.3932729F, 1.240782F), (double)this.decodeX(0.7945455F), (double)this.decodeY(2.3932729F));
      this.path.curveTo((double)this.decodeAnchorX(0.7945455F, -0.38636583F), (double)this.decodeAnchorY(2.3932729F, -1.2384372F), (double)this.decodeAnchorX(0.8055606F, -0.995154F), (double)this.decodeAnchorY(0.6009697F, 1.6626496F), (double)this.decodeX(0.8055606F), (double)this.decodeY(0.6009697F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath4() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.60059524F), (double)this.decodeY(0.11727543F));
      this.path.curveTo((double)this.decodeAnchorX(0.60059524F, 1.5643269F), (double)this.decodeAnchorY(0.11727543F, -0.3097513F), (double)this.decodeAnchorX(2.7925456F, 0.004405844F), (double)this.decodeAnchorY(1.6116884F, -1.1881162F), (double)this.decodeX(2.7925456F), (double)this.decodeY(1.6116884F));
      this.path.curveTo((double)this.decodeAnchorX(2.7925456F, -0.007364541F), (double)this.decodeAnchorY(1.6116884F, 1.9859827F), (double)this.decodeAnchorX(0.7006364F, 2.7716863F), (double)this.decodeAnchorY(2.8693638F, -0.008974582F), (double)this.decodeX(0.7006364F), (double)this.decodeY(2.8693638F));
      this.path.curveTo((double)this.decodeAnchorX(0.7006364F, -3.754899F), (double)this.decodeAnchorY(2.8693638F, 0.012158176F), (double)this.decodeAnchorX(0.60059524F, -1.8635255F), (double)this.decodeAnchorY(0.11727543F, 0.36899543F), (double)this.decodeX(0.60059524F), (double)this.decodeY(0.11727543F));
      this.path.closePath();
      return this.path;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5106101F * var5 + var3, -4.553649E-18F * var6 + var4, 0.49933687F * var5 + var3, 1.0039787F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color1, this.decodeColor(this.color1, this.color2, 0.5F), this.color2});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5023511F * var5 + var3, 0.0015673981F * var6 + var4, 0.5023511F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.21256684F, 0.42513368F, 0.71256685F, 1.0F}, new Color[]{this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4, this.decodeColor(this.color4, this.color5, 0.5F), this.color5});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.51F * var5 + var3, -4.553649E-18F * var6 + var4, 0.51F * var5 + var3, 1.0039787F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color7, this.decodeColor(this.color7, this.color8, 0.5F), this.color8});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0015673981F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.21256684F, 0.42513368F, 0.56149733F, 0.69786096F, 0.8489305F, 1.0F}, new Color[]{this.color9, this.decodeColor(this.color9, this.color10, 0.5F), this.color10, this.decodeColor(this.color10, this.color10, 0.5F), this.color10, this.decodeColor(this.color10, this.color11, 0.5F), this.color11});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5106101F * var5 + var3, -4.553649E-18F * var6 + var4, 0.49933687F * var5 + var3, 1.0039787F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color13, this.decodeColor(this.color13, this.color14, 0.5F), this.color14});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5023511F * var5 + var3, 0.0015673981F * var6 + var4, 0.5023511F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.21256684F, 0.42513368F, 0.56149733F, 0.69786096F, 0.8489305F, 1.0F}, new Color[]{this.color15, this.decodeColor(this.color15, this.color16, 0.5F), this.color16, this.decodeColor(this.color16, this.color17, 0.5F), this.color17, this.decodeColor(this.color17, this.color18, 0.5F), this.color18});
   }

   private Paint decodeGradient7(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5106101F * var5 + var3, -4.553649E-18F * var6 + var4, 0.49933687F * var5 + var3, 1.0039787F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color14, this.decodeColor(this.color14, this.color19, 0.5F), this.color19});
   }

   private Paint decodeGradient8(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5023511F * var5 + var3, 0.0015673981F * var6 + var4, 0.5023511F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.23796791F, 0.47593582F, 0.5360962F, 0.5962567F, 0.79812837F, 1.0F}, new Color[]{this.color20, this.decodeColor(this.color20, this.color21, 0.5F), this.color21, this.decodeColor(this.color21, this.color21, 0.5F), this.color21, this.decodeColor(this.color21, this.color22, 0.5F), this.color22});
   }

   private Paint decodeGradient9(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.24032257F, 0.48064515F, 0.7403226F, 1.0F}, new Color[]{this.color25, this.decodeColor(this.color25, this.color26, 0.5F), this.color26, this.decodeColor(this.color26, this.color27, 0.5F), this.color27});
   }

   private Paint decodeGradient10(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.061290324F, 0.1016129F, 0.14193548F, 0.3016129F, 0.46129033F, 0.5983871F, 0.7354839F, 0.7935484F, 0.8516129F}, new Color[]{this.color28, this.decodeColor(this.color28, this.color29, 0.5F), this.color29, this.decodeColor(this.color29, this.color30, 0.5F), this.color30, this.decodeColor(this.color30, this.color31, 0.5F), this.color31, this.decodeColor(this.color31, this.color32, 0.5F), this.color32});
   }

   private Paint decodeGradient11(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color1, this.decodeColor(this.color1, this.color2, 0.5F), this.color2});
   }

   private Paint decodeGradient12(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.21256684F, 0.42513368F, 0.71256685F, 1.0F}, new Color[]{this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4, this.decodeColor(this.color4, this.color5, 0.5F), this.color5});
   }

   private Paint decodeGradient13(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color13, this.decodeColor(this.color13, this.color14, 0.5F), this.color14});
   }

   private Paint decodeGradient14(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.21256684F, 0.42513368F, 0.56149733F, 0.69786096F, 0.8489305F, 1.0F}, new Color[]{this.color15, this.decodeColor(this.color15, this.color16, 0.5F), this.color16, this.decodeColor(this.color16, this.color17, 0.5F), this.color17, this.decodeColor(this.color17, this.color18, 0.5F), this.color18});
   }

   private Paint decodeGradient15(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color14, this.decodeColor(this.color14, this.color19, 0.5F), this.color19});
   }

   private Paint decodeGradient16(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.23796791F, 0.47593582F, 0.5360962F, 0.5962567F, 0.79812837F, 1.0F}, new Color[]{this.color20, this.decodeColor(this.color20, this.color21, 0.5F), this.color21, this.decodeColor(this.color21, this.color21, 0.5F), this.color21, this.decodeColor(this.color21, this.color22, 0.5F), this.color22});
   }

   private Paint decodeGradient17(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.4925773F * var5 + var3, 0.082019866F * var6 + var4, 0.4925773F * var5 + var3, 0.91798013F * var6 + var4, new float[]{0.061290324F, 0.1016129F, 0.14193548F, 0.3016129F, 0.46129033F, 0.5983871F, 0.7354839F, 0.7935484F, 0.8516129F}, new Color[]{this.color28, this.decodeColor(this.color28, this.color29, 0.5F), this.color29, this.decodeColor(this.color29, this.color30, 0.5F), this.color30, this.decodeColor(this.color30, this.color31, 0.5F), this.color31, this.decodeColor(this.color31, this.color32, 0.5F), this.color32});
   }
}
