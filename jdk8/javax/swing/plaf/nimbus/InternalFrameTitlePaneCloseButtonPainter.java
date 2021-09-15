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

final class InternalFrameTitlePaneCloseButtonPainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED = 1;
   static final int BACKGROUND_ENABLED = 2;
   static final int BACKGROUND_MOUSEOVER = 3;
   static final int BACKGROUND_PRESSED = 4;
   static final int BACKGROUND_ENABLED_WINDOWNOTFOCUSED = 5;
   static final int BACKGROUND_MOUSEOVER_WINDOWNOTFOCUSED = 6;
   static final int BACKGROUND_PRESSED_WINDOWNOTFOCUSED = 7;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusRed", 0.5893519F, -0.75736576F, 0.09411764F, 0);
   private Color color2 = this.decodeColor("nimbusRed", 0.5962963F, -0.71005917F, 0.0F, 0);
   private Color color3 = this.decodeColor("nimbusRed", 0.6005698F, -0.7200287F, -0.015686274F, -122);
   private Color color4 = this.decodeColor("nimbusBlueGrey", 0.0055555105F, -0.062449392F, 0.07058823F, 0);
   private Color color5 = this.decodeColor("nimbusBlueGrey", 0.0055555105F, -0.0029994324F, -0.38039216F, -185);
   private Color color6 = this.decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.4431373F, 0);
   private Color color7 = this.decodeColor("nimbusRed", -2.7342606E-4F, 0.13829035F, -0.039215684F, 0);
   private Color color8 = this.decodeColor("nimbusRed", 6.890595E-4F, -0.36665577F, 0.11764705F, 0);
   private Color color9 = this.decodeColor("nimbusRed", -0.001021713F, 0.101804554F, -0.031372547F, 0);
   private Color color10 = this.decodeColor("nimbusRed", -2.7342606E-4F, 0.13243341F, -0.035294116F, 0);
   private Color color11 = this.decodeColor("nimbusRed", -2.7342606E-4F, 0.002258718F, 0.06666666F, 0);
   private Color color12 = this.decodeColor("nimbusRed", 0.0056530247F, 0.0040003657F, -0.38431373F, -122);
   private Color color13 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
   private Color color14 = this.decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.3882353F, 0);
   private Color color15 = this.decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.13333333F, 0);
   private Color color16 = this.decodeColor("nimbusRed", 6.890595E-4F, -0.38929275F, 0.1607843F, 0);
   private Color color17 = this.decodeColor("nimbusRed", 2.537202E-5F, 0.012294531F, 0.043137252F, 0);
   private Color color18 = this.decodeColor("nimbusRed", -2.7342606E-4F, 0.033585668F, 0.039215684F, 0);
   private Color color19 = this.decodeColor("nimbusRed", -2.7342606E-4F, -0.07198727F, 0.14117646F, 0);
   private Color color20 = this.decodeColor("nimbusRed", -0.014814814F, 0.20118344F, 0.0039215684F, -122);
   private Color color21 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -140);
   private Color color22 = this.decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.49411768F, 0);
   private Color color23 = this.decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.20392159F, 0);
   private Color color24 = this.decodeColor("nimbusRed", -0.014814814F, -0.21260965F, 0.019607842F, 0);
   private Color color25 = this.decodeColor("nimbusRed", -0.014814814F, 0.17340565F, -0.09803921F, 0);
   private Color color26 = this.decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.10588235F, 0);
   private Color color27 = this.decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.04705882F, 0);
   private Color color28 = this.decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.31764707F, -122);
   private Color color29 = this.decodeColor("nimbusRed", 0.5962963F, -0.6994788F, -0.07058823F, 0);
   private Color color30 = this.decodeColor("nimbusRed", 0.5962963F, -0.66245294F, -0.23137257F, 0);
   private Color color31 = this.decodeColor("nimbusRed", 0.58518517F, -0.77649516F, 0.21568626F, 0);
   private Color color32 = this.decodeColor("nimbusRed", 0.5962963F, -0.7372781F, 0.10196078F, 0);
   private Color color33 = this.decodeColor("nimbusRed", 0.5962963F, -0.73911506F, 0.12549019F, 0);
   private Color color34 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.027957506F, -0.31764707F, 0);
   private Object[] componentColors;

   public InternalFrameTitlePaneCloseButtonPainter(AbstractRegionPainter.PaintContext var1, int var2) {
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
         this.paintBackgroundMouseOver(var1);
         break;
      case 4:
         this.paintBackgroundPressed(var1);
         break;
      case 5:
         this.paintBackgroundEnabledAndWindowNotFocused(var1);
         break;
      case 6:
         this.paintBackgroundMouseOverAndWindowNotFocused(var1);
         break;
      case 7:
         this.paintBackgroundPressedAndWindowNotFocused(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundDisabled(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient1(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.color3);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color4);
      var1.fill(this.path);
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.color5);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient2(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient3(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.color12);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color13);
      var1.fill(this.path);
   }

   private void paintBackgroundMouseOver(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.color5);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect4();
      var1.setPaint(this.decodeGradient5(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.color20);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color13);
      var1.fill(this.path);
   }

   private void paintBackgroundPressed(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.color21);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient6(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient7(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.color28);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color13);
      var1.fill(this.path);
   }

   private void paintBackgroundEnabledAndWindowNotFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient8(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient9(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath2();
      var1.setPaint(this.color34);
      var1.fill(this.path);
   }

   private void paintBackgroundMouseOverAndWindowNotFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.color5);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect4();
      var1.setPaint(this.decodeGradient5(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.color20);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color13);
      var1.fill(this.path);
   }

   private void paintBackgroundPressedAndWindowNotFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.color21);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient6(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient7(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.color28);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color13);
      var1.fill(this.path);
   }

   private RoundRectangle2D decodeRoundRect1() {
      this.roundRect.setRoundRect((double)this.decodeX(1.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(1.9444444F) - this.decodeY(1.0F)), 8.600000381469727D, 8.600000381469727D);
      return this.roundRect;
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.25F), (double)this.decodeY(1.7373737F));
      this.path.lineTo((double)this.decodeX(1.3002392F), (double)this.decodeY(1.794192F));
      this.path.lineTo((double)this.decodeX(1.5047847F), (double)this.decodeY(1.5909091F));
      this.path.lineTo((double)this.decodeX(1.6842105F), (double)this.decodeY(1.7954545F));
      this.path.lineTo((double)this.decodeX(1.7595694F), (double)this.decodeY(1.719697F));
      this.path.lineTo((double)this.decodeX(1.5956938F), (double)this.decodeY(1.5239899F));
      this.path.lineTo((double)this.decodeX(1.7535884F), (double)this.decodeY(1.3409091F));
      this.path.lineTo((double)this.decodeX(1.6830144F), (double)this.decodeY(1.2537879F));
      this.path.lineTo((double)this.decodeX(1.5083733F), (double)this.decodeY(1.4406565F));
      this.path.lineTo((double)this.decodeX(1.3301436F), (double)this.decodeY(1.2563131F));
      this.path.lineTo((double)this.decodeX(1.257177F), (double)this.decodeY(1.3320707F));
      this.path.lineTo((double)this.decodeX(1.4270334F), (double)this.decodeY(1.5252526F));
      this.path.lineTo((double)this.decodeX(1.25F), (double)this.decodeY(1.7373737F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.257177F), (double)this.decodeY(1.2828283F));
      this.path.lineTo((double)this.decodeX(1.3217703F), (double)this.decodeY(1.2133838F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(1.4040405F));
      this.path.lineTo((double)this.decodeX(1.673445F), (double)this.decodeY(1.2108586F));
      this.path.lineTo((double)this.decodeX(1.7440192F), (double)this.decodeY(1.2853535F));
      this.path.lineTo((double)this.decodeX(1.5669856F), (double)this.decodeY(1.4709597F));
      this.path.lineTo((double)this.decodeX(1.7488039F), (double)this.decodeY(1.6527778F));
      this.path.lineTo((double)this.decodeX(1.673445F), (double)this.decodeY(1.7398989F));
      this.path.lineTo((double)this.decodeX(1.4988039F), (double)this.decodeY(1.5416667F));
      this.path.lineTo((double)this.decodeX(1.3313397F), (double)this.decodeY(1.7424242F));
      this.path.lineTo((double)this.decodeX(1.2523923F), (double)this.decodeY(1.6565657F));
      this.path.lineTo((double)this.decodeX(1.4366028F), (double)this.decodeY(1.4722222F));
      this.path.lineTo((double)this.decodeX(1.257177F), (double)this.decodeY(1.2828283F));
      this.path.closePath();
      return this.path;
   }

   private RoundRectangle2D decodeRoundRect2() {
      this.roundRect.setRoundRect((double)this.decodeX(1.0F), (double)this.decodeY(1.6111112F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(2.0F) - this.decodeY(1.6111112F)), 6.0D, 6.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect3() {
      this.roundRect.setRoundRect((double)this.decodeX(1.0526316F), (double)this.decodeY(1.0530303F), (double)(this.decodeX(1.9473684F) - this.decodeX(1.0526316F)), (double)(this.decodeY(1.8863636F) - this.decodeY(1.0530303F)), 6.75D, 6.75D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect4() {
      this.roundRect.setRoundRect((double)this.decodeX(1.0526316F), (double)this.decodeY(1.0517677F), (double)(this.decodeX(1.9473684F) - this.decodeX(1.0526316F)), (double)(this.decodeY(1.8851011F) - this.decodeY(1.0517677F)), 6.75D, 6.75D);
      return this.roundRect;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.24868421F * var5 + var3, 0.0014705883F * var6 + var4, 0.24868421F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color1, this.decodeColor(this.color1, this.color2, 0.5F), this.color2});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.24868421F * var5 + var3, 0.0014705883F * var6 + var4, 0.24868421F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color6, this.decodeColor(this.color6, this.color7, 0.5F), this.color7});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F}, new Color[]{this.color8, this.decodeColor(this.color8, this.color9, 0.5F), this.color9, this.decodeColor(this.color9, this.color10, 0.5F), this.color10, this.decodeColor(this.color10, this.color11, 0.5F), this.color11});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.24868421F * var5 + var3, 0.0014705883F * var6 + var4, 0.24868421F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color14, this.decodeColor(this.color14, this.color15, 0.5F), this.color15});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.81480503F, 0.97904193F}, new Color[]{this.color16, this.decodeColor(this.color16, this.color17, 0.5F), this.color17, this.decodeColor(this.color17, this.color18, 0.5F), this.color18, this.decodeColor(this.color18, this.color19, 0.5F), this.color19});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.24868421F * var5 + var3, 0.0014705883F * var6 + var4, 0.24868421F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color22, this.decodeColor(this.color22, this.color23, 0.5F), this.color23});
   }

   private Paint decodeGradient7(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.81630206F, 0.98203593F}, new Color[]{this.color24, this.decodeColor(this.color24, this.color25, 0.5F), this.color25, this.decodeColor(this.color25, this.color26, 0.5F), this.color26, this.decodeColor(this.color26, this.color27, 0.5F), this.color27});
   }

   private Paint decodeGradient8(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.24868421F * var5 + var3, 0.0014705883F * var6 + var4, 0.24868421F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color29, this.decodeColor(this.color29, this.color30, 0.5F), this.color30});
   }

   private Paint decodeGradient9(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.24101797F, 0.48203593F, 0.5838324F, 0.6856288F, 0.8428144F, 1.0F}, new Color[]{this.color31, this.decodeColor(this.color31, this.color32, 0.5F), this.color32, this.decodeColor(this.color32, this.color32, 0.5F), this.color32, this.decodeColor(this.color32, this.color33, 0.5F), this.color33});
   }
}
