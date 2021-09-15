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

final class InternalFrameTitlePaneIconifyButtonPainter extends AbstractRegionPainter {
   static final int BACKGROUND_ENABLED = 1;
   static final int BACKGROUND_DISABLED = 2;
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
   private Color color1 = this.decodeColor("nimbusBlueGrey", 0.0055555105F, -0.0029994324F, -0.38039216F, -185);
   private Color color2 = this.decodeColor("nimbusOrange", -0.08377897F, 0.02094239F, -0.40392157F, 0);
   private Color color3 = this.decodeColor("nimbusOrange", 0.0F, 0.0F, 0.0F, 0);
   private Color color4 = this.decodeColor("nimbusOrange", -4.4563413E-4F, -0.48364475F, 0.10588235F, 0);
   private Color color5 = this.decodeColor("nimbusOrange", 0.0F, -0.0050992966F, 0.0039215684F, 0);
   private Color color6 = this.decodeColor("nimbusOrange", 0.0F, -0.12125945F, 0.10588235F, 0);
   private Color color7 = this.decodeColor("nimbusOrange", -0.08377897F, 0.02094239F, -0.40392157F, -106);
   private Color color8 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
   private Color color9 = this.decodeColor("nimbusOrange", 0.5203877F, -0.9376068F, 0.007843137F, 0);
   private Color color10 = this.decodeColor("nimbusOrange", 0.5273321F, -0.8903002F, -0.086274505F, 0);
   private Color color11 = this.decodeColor("nimbusOrange", 0.5273321F, -0.93313926F, 0.019607842F, 0);
   private Color color12 = this.decodeColor("nimbusOrange", 0.53526866F, -0.8995122F, -0.058823526F, 0);
   private Color color13 = this.decodeColor("nimbusOrange", 0.5233639F, -0.8971863F, -0.07843137F, 0);
   private Color color14 = this.decodeColor("nimbusBlueGrey", -0.0808081F, 0.015910469F, -0.40392157F, -216);
   private Color color15 = this.decodeColor("nimbusBlueGrey", -0.003968239F, -0.03760965F, 0.007843137F, 0);
   private Color color16 = new Color(255, 200, 0, 255);
   private Color color17 = this.decodeColor("nimbusOrange", -0.08377897F, 0.02094239F, -0.31764707F, 0);
   private Color color18 = this.decodeColor("nimbusOrange", -0.02758849F, 0.02094239F, -0.062745094F, 0);
   private Color color19 = this.decodeColor("nimbusOrange", -4.4563413E-4F, -0.5074419F, 0.1490196F, 0);
   private Color color20 = this.decodeColor("nimbusOrange", 9.745359E-6F, -0.11175901F, 0.07843137F, 0);
   private Color color21 = this.decodeColor("nimbusOrange", 0.0F, -0.09280169F, 0.07843137F, 0);
   private Color color22 = this.decodeColor("nimbusOrange", 0.0F, -0.19002807F, 0.18039215F, 0);
   private Color color23 = this.decodeColor("nimbusOrange", -0.025772434F, 0.02094239F, 0.05098039F, 0);
   private Color color24 = this.decodeColor("nimbusOrange", -0.08377897F, 0.02094239F, -0.4F, 0);
   private Color color25 = this.decodeColor("nimbusOrange", -0.053104125F, 0.02094239F, -0.109803915F, 0);
   private Color color26 = this.decodeColor("nimbusOrange", -0.017887495F, -0.33726656F, 0.039215684F, 0);
   private Color color27 = this.decodeColor("nimbusOrange", -0.018038228F, 0.02094239F, -0.043137252F, 0);
   private Color color28 = this.decodeColor("nimbusOrange", -0.015844189F, 0.02094239F, -0.027450979F, 0);
   private Color color29 = this.decodeColor("nimbusOrange", -0.010274701F, 0.02094239F, 0.015686274F, 0);
   private Color color30 = this.decodeColor("nimbusOrange", -0.08377897F, 0.02094239F, -0.14509803F, -91);
   private Color color31 = this.decodeColor("nimbusOrange", 0.5273321F, -0.87971985F, -0.15686274F, 0);
   private Color color32 = this.decodeColor("nimbusOrange", 0.5273321F, -0.842694F, -0.31764707F, 0);
   private Color color33 = this.decodeColor("nimbusOrange", 0.516221F, -0.9567362F, 0.12941176F, 0);
   private Color color34 = this.decodeColor("nimbusOrange", 0.5222816F, -0.9229352F, 0.019607842F, 0);
   private Color color35 = this.decodeColor("nimbusOrange", 0.5273321F, -0.91751915F, 0.015686274F, 0);
   private Color color36 = this.decodeColor("nimbusOrange", 0.5273321F, -0.9193561F, 0.039215684F, 0);
   private Color color37 = this.decodeColor("nimbusBlueGrey", -0.01111114F, -0.017933726F, -0.32156864F, 0);
   private Object[] componentColors;

   public InternalFrameTitlePaneIconifyButtonPainter(AbstractRegionPainter.PaintContext var1, int var2) {
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
         this.paintBackgroundDisabled(var1);
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

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient1(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient2(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color7);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color8);
      var1.fill(this.rect);
   }

   private void paintBackgroundDisabled(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient3(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color14);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color15);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
   }

   private void paintBackgroundMouseOver(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient5(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient6(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color23);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color8);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
   }

   private void paintBackgroundPressed(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient7(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient8(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color30);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color8);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
   }

   private void paintBackgroundEnabledAndWindowNotFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient9(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient10(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color14);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color37);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
   }

   private void paintBackgroundMouseOverAndWindowNotFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient5(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient6(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color23);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color8);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
   }

   private void paintBackgroundPressedAndWindowNotFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient7(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient8(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color30);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color8);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
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

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(1.25F), (double)this.decodeY(1.6628788F), (double)(this.decodeX(1.75F) - this.decodeX(1.25F)), (double)(this.decodeY(1.7487373F) - this.decodeY(1.6628788F)));
      return this.rect;
   }

   private Rectangle2D decodeRect2() {
      this.rect.setRect((double)this.decodeX(1.2870814F), (double)this.decodeY(1.6123737F), (double)(this.decodeX(1.7165072F) - this.decodeX(1.2870814F)), (double)(this.decodeY(1.7222222F) - this.decodeY(1.6123737F)));
      return this.rect;
   }

   private Rectangle2D decodeRect3() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(1.0F) - this.decodeX(1.0F)), (double)(this.decodeY(1.0F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect4() {
      this.rect.setRect((double)this.decodeX(1.25F), (double)this.decodeY(1.6527778F), (double)(this.decodeX(1.7511961F) - this.decodeX(1.25F)), (double)(this.decodeY(1.7828283F) - this.decodeY(1.6527778F)));
      return this.rect;
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
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F}, new Color[]{this.color4, this.decodeColor(this.color4, this.color3, 0.5F), this.color3, this.decodeColor(this.color3, this.color5, 0.5F), this.color5, this.decodeColor(this.color5, this.color6, 0.5F), this.color6});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.24868421F * var5 + var3, 0.0014705883F * var6 + var4, 0.24868421F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color9, this.decodeColor(this.color9, this.color10, 0.5F), this.color10});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F}, new Color[]{this.color11, this.decodeColor(this.color11, this.color12, 0.5F), this.color12, this.decodeColor(this.color12, this.color13, 0.5F), this.color13, this.decodeColor(this.color13, this.color10, 0.5F), this.color10});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.24868421F * var5 + var3, 0.0014705883F * var6 + var4, 0.24868421F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color17, this.decodeColor(this.color17, this.color18, 0.5F), this.color18});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F}, new Color[]{this.color19, this.decodeColor(this.color19, this.color20, 0.5F), this.color20, this.decodeColor(this.color20, this.color21, 0.5F), this.color21, this.decodeColor(this.color21, this.color22, 0.5F), this.color22});
   }

   private Paint decodeGradient7(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.24868421F * var5 + var3, 0.0014705883F * var6 + var4, 0.24868421F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color24, this.decodeColor(this.color24, this.color25, 0.5F), this.color25});
   }

   private Paint decodeGradient8(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F}, new Color[]{this.color26, this.decodeColor(this.color26, this.color27, 0.5F), this.color27, this.decodeColor(this.color27, this.color28, 0.5F), this.color28, this.decodeColor(this.color28, this.color29, 0.5F), this.color29});
   }

   private Paint decodeGradient9(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.24868421F * var5 + var3, 0.0014705883F * var6 + var4, 0.24868421F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color31, this.decodeColor(this.color31, this.color32, 0.5F), this.color32});
   }

   private Paint decodeGradient10(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.78336793F, 0.9161677F}, new Color[]{this.color33, this.decodeColor(this.color33, this.color34, 0.5F), this.color34, this.decodeColor(this.color34, this.color35, 0.5F), this.color35, this.decodeColor(this.color35, this.color36, 0.5F), this.color36});
   }
}
