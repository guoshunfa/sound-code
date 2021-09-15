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

final class InternalFrameTitlePaneMenuButtonPainter extends AbstractRegionPainter {
   static final int ICON_ENABLED = 1;
   static final int ICON_DISABLED = 2;
   static final int ICON_MOUSEOVER = 3;
   static final int ICON_PRESSED = 4;
   static final int ICON_ENABLED_WINDOWNOTFOCUSED = 5;
   static final int ICON_MOUSEOVER_WINDOWNOTFOCUSED = 6;
   static final int ICON_PRESSED_WINDOWNOTFOCUSED = 7;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBlueGrey", 0.0055555105F, -0.0029994324F, -0.38039216F, -185);
   private Color color2 = this.decodeColor("nimbusBase", 0.08801502F, 0.3642857F, -0.5019608F, 0);
   private Color color3 = this.decodeColor("nimbusBase", 0.030543745F, -0.3835404F, -0.09803924F, 0);
   private Color color4 = this.decodeColor("nimbusBase", 0.029191494F, -0.53801316F, 0.13333333F, 0);
   private Color color5 = this.decodeColor("nimbusBase", 0.030543745F, -0.3857143F, -0.09411767F, 0);
   private Color color6 = this.decodeColor("nimbusBase", 0.030543745F, -0.43148893F, 0.007843137F, 0);
   private Color color7 = this.decodeColor("nimbusBase", 0.029191494F, -0.24935067F, -0.20392159F, -132);
   private Color color8 = this.decodeColor("nimbusBase", 0.029191494F, -0.24935067F, -0.20392159F, 0);
   private Color color9 = this.decodeColor("nimbusBase", 0.029191494F, -0.24935067F, -0.20392159F, -123);
   private Color color10 = this.decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, 0);
   private Color color11 = this.decodeColor("nimbusBlueGrey", 0.0055555105F, -0.0029994324F, -0.38039216F, -208);
   private Color color12 = this.decodeColor("nimbusBase", 0.02551502F, -0.5942635F, 0.20784312F, 0);
   private Color color13 = this.decodeColor("nimbusBase", 0.032459438F, -0.5490091F, 0.12941176F, 0);
   private Color color14 = this.decodeColor("nimbusBase", 0.032459438F, -0.5469569F, 0.11372548F, 0);
   private Color color15 = this.decodeColor("nimbusBase", 0.032459438F, -0.5760128F, 0.23921567F, 0);
   private Color color16 = this.decodeColor("nimbusBase", 0.08801502F, 0.3642857F, -0.4901961F, 0);
   private Color color17 = this.decodeColor("nimbusBase", 0.032459438F, -0.1857143F, -0.23529413F, 0);
   private Color color18 = this.decodeColor("nimbusBase", 0.029191494F, -0.5438224F, 0.17647058F, 0);
   private Color color19 = this.decodeColor("nimbusBase", 0.030543745F, -0.41929638F, -0.02352941F, 0);
   private Color color20 = this.decodeColor("nimbusBase", 0.030543745F, -0.45559007F, 0.082352936F, 0);
   private Color color21 = this.decodeColor("nimbusBase", 0.03409344F, -0.329408F, -0.11372551F, -132);
   private Color color22 = this.decodeColor("nimbusBase", 0.03409344F, -0.329408F, -0.11372551F, 0);
   private Color color23 = this.decodeColor("nimbusBase", 0.03409344F, -0.329408F, -0.11372551F, -123);
   private Color color24 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
   private Color color25 = this.decodeColor("nimbusBase", 0.031104386F, 0.12354499F, -0.33725494F, 0);
   private Color color26 = this.decodeColor("nimbusBase", 0.032459438F, -0.4592437F, -0.015686274F, 0);
   private Color color27 = this.decodeColor("nimbusBase", 0.029191494F, -0.2579365F, -0.19607845F, 0);
   private Color color28 = this.decodeColor("nimbusBase", 0.03409344F, -0.3149596F, -0.13333336F, 0);
   private Color color29 = this.decodeColor("nimbusBase", 0.029681683F, 0.07857144F, -0.3294118F, -132);
   private Color color30 = this.decodeColor("nimbusBase", 0.029681683F, 0.07857144F, -0.3294118F, 0);
   private Color color31 = this.decodeColor("nimbusBase", 0.029681683F, 0.07857144F, -0.3294118F, -123);
   private Color color32 = this.decodeColor("nimbusBase", 0.032459438F, -0.53637654F, 0.043137252F, 0);
   private Color color33 = this.decodeColor("nimbusBase", 0.032459438F, -0.49935067F, -0.11764708F, 0);
   private Color color34 = this.decodeColor("nimbusBase", 0.021348298F, -0.6133929F, 0.32941175F, 0);
   private Color color35 = this.decodeColor("nimbusBase", 0.042560518F, -0.5804379F, 0.23137254F, 0);
   private Color color36 = this.decodeColor("nimbusBase", 0.032459438F, -0.57417583F, 0.21568626F, 0);
   private Color color37 = this.decodeColor("nimbusBase", 0.027408898F, -0.5784226F, 0.20392156F, -132);
   private Color color38 = this.decodeColor("nimbusBase", 0.042560518F, -0.5665319F, 0.0745098F, 0);
   private Color color39 = this.decodeColor("nimbusBase", 0.036732912F, -0.5642857F, 0.16470587F, -123);
   private Color color40 = this.decodeColor("nimbusBase", 0.021348298F, -0.54480517F, -0.11764708F, 0);
   private Object[] componentColors;

   public InternalFrameTitlePaneMenuButtonPainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 1:
         this.painticonEnabled(var1);
         break;
      case 2:
         this.painticonDisabled(var1);
         break;
      case 3:
         this.painticonMouseOver(var1);
         break;
      case 4:
         this.painticonPressed(var1);
         break;
      case 5:
         this.painticonEnabledAndWindowNotFocused(var1);
         break;
      case 6:
         this.painticonMouseOverAndWindowNotFocused(var1);
         break;
      case 7:
         this.painticonPressedAndWindowNotFocused(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void painticonEnabled(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient1(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient2(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient3(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color10);
      var1.fill(this.path);
   }

   private void painticonDisabled(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color11);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath2();
      var1.setPaint(this.color15);
      var1.fill(this.path);
   }

   private void painticonMouseOver(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient5(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient6(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient7(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color10);
      var1.fill(this.path);
   }

   private void painticonPressed(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient8(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient9(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient10(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color10);
      var1.fill(this.path);
   }

   private void painticonEnabledAndWindowNotFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient11(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient12(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient13(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color40);
      var1.fill(this.path);
   }

   private void painticonMouseOverAndWindowNotFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient5(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient6(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient7(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color10);
      var1.fill(this.path);
   }

   private void painticonPressedAndWindowNotFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient8(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient9(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient10(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color10);
      var1.fill(this.path);
   }

   private RoundRectangle2D decodeRoundRect1() {
      this.roundRect.setRoundRect((double)this.decodeX(1.0F), (double)this.decodeY(1.6111112F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(2.0F) - this.decodeY(1.6111112F)), 6.0D, 6.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect2() {
      this.roundRect.setRoundRect((double)this.decodeX(1.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(1.9444444F) - this.decodeY(1.0F)), 8.600000381469727D, 8.600000381469727D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect3() {
      this.roundRect.setRoundRect((double)this.decodeX(1.0526316F), (double)this.decodeY(1.0555556F), (double)(this.decodeX(1.9473684F) - this.decodeX(1.0526316F)), (double)(this.decodeY(1.8888888F) - this.decodeY(1.0555556F)), 6.75D, 6.75D);
      return this.roundRect;
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.3157895F), (double)this.decodeY(1.4444444F));
      this.path.lineTo((double)this.decodeX(1.6842105F), (double)this.decodeY(1.4444444F));
      this.path.lineTo((double)this.decodeX(1.5013158F), (double)this.decodeY(1.7208333F));
      this.path.lineTo((double)this.decodeX(1.3157895F), (double)this.decodeY(1.4444444F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.3157895F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(1.6842105F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(1.6083333F));
      this.path.lineTo((double)this.decodeX(1.3157895F), (double)this.decodeY(1.3333334F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath3() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.3157895F), (double)this.decodeY(1.3888888F));
      this.path.lineTo((double)this.decodeX(1.6842105F), (double)this.decodeY(1.3888888F));
      this.path.lineTo((double)this.decodeX(1.4952153F), (double)this.decodeY(1.655303F));
      this.path.lineTo((double)this.decodeX(1.3157895F), (double)this.decodeY(1.3888888F));
      this.path.closePath();
      return this.path;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.24868421F * var5 + var3, 0.0014705883F * var6 + var4, 0.24868421F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color2, this.decodeColor(this.color2, this.color3, 0.5F), this.color3});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F}, new Color[]{this.color4, this.decodeColor(this.color4, this.color5, 0.5F), this.color5, this.decodeColor(this.color5, this.color3, 0.5F), this.color3, this.decodeColor(this.color3, this.color6, 0.5F), this.color6});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.50714284F * var5 + var3, 0.095F * var6 + var4, 0.49285713F * var5 + var3, 0.91F * var6 + var4, new float[]{0.0F, 0.24289773F, 0.48579547F, 0.74289775F, 1.0F}, new Color[]{this.color7, this.decodeColor(this.color7, this.color8, 0.5F), this.color8, this.decodeColor(this.color8, this.color9, 0.5F), this.color9});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.24868421F * var5 + var3, 0.0014705883F * var6 + var4, 0.24868421F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.31107953F, 0.62215906F, 0.8110795F, 1.0F}, new Color[]{this.color12, this.decodeColor(this.color12, this.color13, 0.5F), this.color13, this.decodeColor(this.color13, this.color14, 0.5F), this.color14});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.24868421F * var5 + var3, 0.0014705883F * var6 + var4, 0.24868421F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color16, this.decodeColor(this.color16, this.color17, 0.5F), this.color17});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F}, new Color[]{this.color18, this.decodeColor(this.color18, this.color19, 0.5F), this.color19, this.decodeColor(this.color19, this.color19, 0.5F), this.color19, this.decodeColor(this.color19, this.color20, 0.5F), this.color20});
   }

   private Paint decodeGradient7(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.50714284F * var5 + var3, 0.095F * var6 + var4, 0.49285713F * var5 + var3, 0.91F * var6 + var4, new float[]{0.0F, 0.24289773F, 0.48579547F, 0.74289775F, 1.0F}, new Color[]{this.color21, this.decodeColor(this.color21, this.color22, 0.5F), this.color22, this.decodeColor(this.color22, this.color23, 0.5F), this.color23});
   }

   private Paint decodeGradient8(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.24868421F * var5 + var3, 0.0014705883F * var6 + var4, 0.24868421F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color24, this.decodeColor(this.color24, this.color25, 0.5F), this.color25});
   }

   private Paint decodeGradient9(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F}, new Color[]{this.color26, this.decodeColor(this.color26, this.color27, 0.5F), this.color27, this.decodeColor(this.color27, this.color27, 0.5F), this.color27, this.decodeColor(this.color27, this.color28, 0.5F), this.color28});
   }

   private Paint decodeGradient10(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.50714284F * var5 + var3, 0.095F * var6 + var4, 0.49285713F * var5 + var3, 0.91F * var6 + var4, new float[]{0.0F, 0.24289773F, 0.48579547F, 0.74289775F, 1.0F}, new Color[]{this.color29, this.decodeColor(this.color29, this.color30, 0.5F), this.color30, this.decodeColor(this.color30, this.color31, 0.5F), this.color31});
   }

   private Paint decodeGradient11(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.24868421F * var5 + var3, 0.0014705883F * var6 + var4, 0.24868421F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color32, this.decodeColor(this.color32, this.color33, 0.5F), this.color33});
   }

   private Paint decodeGradient12(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F}, new Color[]{this.color34, this.decodeColor(this.color34, this.color35, 0.5F), this.color35, this.decodeColor(this.color35, this.color36, 0.5F), this.color36, this.decodeColor(this.color36, this.color15, 0.5F), this.color15});
   }

   private Paint decodeGradient13(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.50714284F * var5 + var3, 0.095F * var6 + var4, 0.49285713F * var5 + var3, 0.91F * var6 + var4, new float[]{0.0F, 0.24289773F, 0.48579547F, 0.74289775F, 1.0F}, new Color[]{this.color37, this.decodeColor(this.color37, this.color38, 0.5F), this.color38, this.decodeColor(this.color38, this.color39, 0.5F), this.color39});
   }
}
