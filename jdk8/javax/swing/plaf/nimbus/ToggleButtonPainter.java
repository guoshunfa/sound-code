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

final class ToggleButtonPainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED = 1;
   static final int BACKGROUND_ENABLED = 2;
   static final int BACKGROUND_FOCUSED = 3;
   static final int BACKGROUND_MOUSEOVER = 4;
   static final int BACKGROUND_MOUSEOVER_FOCUSED = 5;
   static final int BACKGROUND_PRESSED = 6;
   static final int BACKGROUND_PRESSED_FOCUSED = 7;
   static final int BACKGROUND_SELECTED = 8;
   static final int BACKGROUND_SELECTED_FOCUSED = 9;
   static final int BACKGROUND_PRESSED_SELECTED = 10;
   static final int BACKGROUND_PRESSED_SELECTED_FOCUSED = 11;
   static final int BACKGROUND_MOUSEOVER_SELECTED = 12;
   static final int BACKGROUND_MOUSEOVER_SELECTED_FOCUSED = 13;
   static final int BACKGROUND_DISABLED_SELECTED = 14;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.06885965F, -0.36862746F, -232);
   private Color color2 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06766917F, 0.07843137F, 0);
   private Color color3 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06484103F, 0.027450979F, 0);
   private Color color4 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.08477524F, 0.16862744F, 0);
   private Color color5 = this.decodeColor("nimbusBlueGrey", -0.015872955F, -0.080091536F, 0.15686274F, 0);
   private Color color6 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.07016757F, 0.12941176F, 0);
   private Color color7 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.07052632F, 0.1372549F, 0);
   private Color color8 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.070878744F, 0.14509803F, 0);
   private Color color9 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.06885965F, -0.36862746F, -190);
   private Color color10 = this.decodeColor("nimbusBlueGrey", -0.055555522F, -0.05356429F, -0.12549019F, 0);
   private Color color11 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.0147816315F, -0.3764706F, 0);
   private Color color12 = this.decodeColor("nimbusBlueGrey", 0.055555582F, -0.10655806F, 0.24313724F, 0);
   private Color color13 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.09823123F, 0.2117647F, 0);
   private Color color14 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.0749532F, 0.24705881F, 0);
   private Color color15 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
   private Color color16 = this.decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
   private Color color17 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.020974077F, -0.21960783F, 0);
   private Color color18 = this.decodeColor("nimbusBlueGrey", 0.0F, 0.11169591F, -0.53333336F, 0);
   private Color color19 = this.decodeColor("nimbusBlueGrey", 0.055555582F, -0.10658931F, 0.25098038F, 0);
   private Color color20 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.098526314F, 0.2352941F, 0);
   private Color color21 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.07333623F, 0.20392156F, 0);
   private Color color22 = new Color(245, 250, 255, 160);
   private Color color23 = this.decodeColor("nimbusBlueGrey", 0.055555582F, 0.8894737F, -0.7176471F, 0);
   private Color color24 = this.decodeColor("nimbusBlueGrey", 0.0F, 5.847961E-4F, -0.32156864F, 0);
   private Color color25 = this.decodeColor("nimbusBlueGrey", -0.00505054F, -0.05960039F, 0.10196078F, 0);
   private Color color26 = this.decodeColor("nimbusBlueGrey", -0.008547008F, -0.04772438F, 0.06666666F, 0);
   private Color color27 = this.decodeColor("nimbusBlueGrey", -0.0027777553F, -0.0018306673F, -0.02352941F, 0);
   private Color color28 = this.decodeColor("nimbusBlueGrey", -0.0027777553F, -0.0212406F, 0.13333333F, 0);
   private Color color29 = this.decodeColor("nimbusBlueGrey", 0.0055555105F, -0.030845039F, 0.23921567F, 0);
   private Color color30 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -86);
   private Color color31 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06472479F, -0.23137254F, 0);
   private Color color32 = this.decodeColor("nimbusBlueGrey", 0.007936537F, -0.06959064F, -0.0745098F, 0);
   private Color color33 = this.decodeColor("nimbusBlueGrey", 0.0138888955F, -0.06401469F, -0.07058823F, 0);
   private Color color34 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06530018F, 0.035294116F, 0);
   private Color color35 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06507177F, 0.031372547F, 0);
   private Color color36 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.05338346F, -0.47058824F, 0);
   private Color color37 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.049301825F, -0.36078432F, 0);
   private Color color38 = this.decodeColor("nimbusBlueGrey", -0.018518567F, -0.03909774F, -0.2509804F, 0);
   private Color color39 = this.decodeColor("nimbusBlueGrey", -0.00505054F, -0.040013492F, -0.13333333F, 0);
   private Color color40 = this.decodeColor("nimbusBlueGrey", 0.01010108F, -0.039558575F, -0.1372549F, 0);
   private Color color41 = this.decodeColor("nimbusBlueGrey", -0.01111114F, -0.060526315F, -0.3529412F, 0);
   private Color color42 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.064372465F, -0.2352941F, 0);
   private Color color43 = this.decodeColor("nimbusBlueGrey", -0.006944418F, -0.0595709F, -0.12941176F, 0);
   private Color color44 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.061075766F, -0.031372547F, 0);
   private Color color45 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06080256F, -0.035294116F, 0);
   private Color color46 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -220);
   private Color color47 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.066408664F, 0.054901958F, 0);
   private Color color48 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06807348F, 0.086274505F, 0);
   private Color color49 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06924191F, 0.109803915F, 0);
   private Object[] componentColors;

   public ToggleButtonPainter(AbstractRegionPainter.PaintContext var1, int var2) {
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
         this.paintBackgroundMouseOver(var1);
         break;
      case 5:
         this.paintBackgroundMouseOverAndFocused(var1);
         break;
      case 6:
         this.paintBackgroundPressed(var1);
         break;
      case 7:
         this.paintBackgroundPressedAndFocused(var1);
         break;
      case 8:
         this.paintBackgroundSelected(var1);
         break;
      case 9:
         this.paintBackgroundSelectedAndFocused(var1);
         break;
      case 10:
         this.paintBackgroundPressedAndSelected(var1);
         break;
      case 11:
         this.paintBackgroundPressedAndSelectedAndFocused(var1);
         break;
      case 12:
         this.paintBackgroundMouseOverAndSelected(var1);
         break;
      case 13:
         this.paintBackgroundMouseOverAndSelectedAndFocused(var1);
         break;
      case 14:
         this.paintBackgroundDisabledAndSelected(var1);
      }

   }

   protected Object[] getExtendedCacheKeys(JComponent var1) {
      Object[] var2 = null;
      switch(this.state) {
      case 2:
         var2 = new Object[]{this.getComponentColor(var1, "background", this.color12, -0.10655806F, 0.24313724F, 0), this.getComponentColor(var1, "background", this.color13, -0.09823123F, 0.2117647F, 0), this.getComponentColor(var1, "background", this.color6, -0.07016757F, 0.12941176F, 0), this.getComponentColor(var1, "background", this.color14, -0.0749532F, 0.24705881F, 0), this.getComponentColor(var1, "background", this.color15, -0.110526316F, 0.25490195F, 0)};
         break;
      case 3:
         var2 = new Object[]{this.getComponentColor(var1, "background", this.color12, -0.10655806F, 0.24313724F, 0), this.getComponentColor(var1, "background", this.color13, -0.09823123F, 0.2117647F, 0), this.getComponentColor(var1, "background", this.color6, -0.07016757F, 0.12941176F, 0), this.getComponentColor(var1, "background", this.color14, -0.0749532F, 0.24705881F, 0), this.getComponentColor(var1, "background", this.color15, -0.110526316F, 0.25490195F, 0)};
         break;
      case 4:
         var2 = new Object[]{this.getComponentColor(var1, "background", this.color19, -0.10658931F, 0.25098038F, 0), this.getComponentColor(var1, "background", this.color20, -0.098526314F, 0.2352941F, 0), this.getComponentColor(var1, "background", this.color21, -0.07333623F, 0.20392156F, 0), this.getComponentColor(var1, "background", this.color15, -0.110526316F, 0.25490195F, 0)};
         break;
      case 5:
         var2 = new Object[]{this.getComponentColor(var1, "background", this.color19, -0.10658931F, 0.25098038F, 0), this.getComponentColor(var1, "background", this.color20, -0.098526314F, 0.2352941F, 0), this.getComponentColor(var1, "background", this.color21, -0.07333623F, 0.20392156F, 0), this.getComponentColor(var1, "background", this.color15, -0.110526316F, 0.25490195F, 0)};
         break;
      case 6:
         var2 = new Object[]{this.getComponentColor(var1, "background", this.color25, -0.05960039F, 0.10196078F, 0), this.getComponentColor(var1, "background", this.color26, -0.04772438F, 0.06666666F, 0), this.getComponentColor(var1, "background", this.color27, -0.0018306673F, -0.02352941F, 0), this.getComponentColor(var1, "background", this.color28, -0.0212406F, 0.13333333F, 0), this.getComponentColor(var1, "background", this.color29, -0.030845039F, 0.23921567F, 0)};
         break;
      case 7:
         var2 = new Object[]{this.getComponentColor(var1, "background", this.color25, -0.05960039F, 0.10196078F, 0), this.getComponentColor(var1, "background", this.color26, -0.04772438F, 0.06666666F, 0), this.getComponentColor(var1, "background", this.color27, -0.0018306673F, -0.02352941F, 0), this.getComponentColor(var1, "background", this.color28, -0.0212406F, 0.13333333F, 0), this.getComponentColor(var1, "background", this.color29, -0.030845039F, 0.23921567F, 0)};
         break;
      case 8:
         var2 = new Object[]{this.getComponentColor(var1, "background", this.color33, -0.06401469F, -0.07058823F, 0), this.getComponentColor(var1, "background", this.color34, -0.06530018F, 0.035294116F, 0), this.getComponentColor(var1, "background", this.color35, -0.06507177F, 0.031372547F, 0)};
         break;
      case 9:
         var2 = new Object[]{this.getComponentColor(var1, "background", this.color33, -0.06401469F, -0.07058823F, 0), this.getComponentColor(var1, "background", this.color34, -0.06530018F, 0.035294116F, 0), this.getComponentColor(var1, "background", this.color35, -0.06507177F, 0.031372547F, 0)};
         break;
      case 10:
         var2 = new Object[]{this.getComponentColor(var1, "background", this.color38, -0.03909774F, -0.2509804F, 0), this.getComponentColor(var1, "background", this.color39, -0.040013492F, -0.13333333F, 0), this.getComponentColor(var1, "background", this.color40, -0.039558575F, -0.1372549F, 0)};
         break;
      case 11:
         var2 = new Object[]{this.getComponentColor(var1, "background", this.color38, -0.03909774F, -0.2509804F, 0), this.getComponentColor(var1, "background", this.color39, -0.040013492F, -0.13333333F, 0), this.getComponentColor(var1, "background", this.color40, -0.039558575F, -0.1372549F, 0)};
         break;
      case 12:
         var2 = new Object[]{this.getComponentColor(var1, "background", this.color43, -0.0595709F, -0.12941176F, 0), this.getComponentColor(var1, "background", this.color44, -0.061075766F, -0.031372547F, 0), this.getComponentColor(var1, "background", this.color45, -0.06080256F, -0.035294116F, 0)};
         break;
      case 13:
         var2 = new Object[]{this.getComponentColor(var1, "background", this.color43, -0.0595709F, -0.12941176F, 0), this.getComponentColor(var1, "background", this.color44, -0.061075766F, -0.031372547F, 0), this.getComponentColor(var1, "background", this.color45, -0.06080256F, -0.035294116F, 0)};
      }

      return var2;
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
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color9);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient3(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect4();
      var1.setPaint(this.color16);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient3(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient5(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundMouseOver(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color9);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient6(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient7(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundMouseOverAndFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect4();
      var1.setPaint(this.color16);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient6(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient7(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundPressed(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color22);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient8(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundPressedAndFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect4();
      var1.setPaint(this.color16);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient8(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundSelected(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect5();
      var1.setPaint(this.color30);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient9(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient10(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundSelectedAndFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect6();
      var1.setPaint(this.color16);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient9(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient10(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundPressedAndSelected(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect5();
      var1.setPaint(this.color30);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient11(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient10(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundPressedAndSelectedAndFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect6();
      var1.setPaint(this.color16);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient11(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient10(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundMouseOverAndSelected(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect5();
      var1.setPaint(this.color30);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient12(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient10(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundMouseOverAndSelectedAndFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect6();
      var1.setPaint(this.color16);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient12(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient10(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void paintBackgroundDisabledAndSelected(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect5();
      var1.setPaint(this.color46);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient13(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.decodeGradient14(this.roundRect));
      var1.fill(this.roundRect);
   }

   private RoundRectangle2D decodeRoundRect1() {
      this.roundRect.setRoundRect((double)this.decodeX(0.2857143F), (double)this.decodeY(0.42857143F), (double)(this.decodeX(2.7142859F) - this.decodeX(0.2857143F)), (double)(this.decodeY(2.857143F) - this.decodeY(0.42857143F)), 12.0D, 12.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect2() {
      this.roundRect.setRoundRect((double)this.decodeX(0.2857143F), (double)this.decodeY(0.2857143F), (double)(this.decodeX(2.7142859F) - this.decodeX(0.2857143F)), (double)(this.decodeY(2.7142859F) - this.decodeY(0.2857143F)), 9.0D, 9.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect3() {
      this.roundRect.setRoundRect((double)this.decodeX(0.42857143F), (double)this.decodeY(0.42857143F), (double)(this.decodeX(2.5714285F) - this.decodeX(0.42857143F)), (double)(this.decodeY(2.5714285F) - this.decodeY(0.42857143F)), 7.0D, 7.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect4() {
      this.roundRect.setRoundRect((double)this.decodeX(0.08571429F), (double)this.decodeY(0.08571429F), (double)(this.decodeX(2.914286F) - this.decodeX(0.08571429F)), (double)(this.decodeY(2.914286F) - this.decodeY(0.08571429F)), 11.0D, 11.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect5() {
      this.roundRect.setRoundRect((double)this.decodeX(0.2857143F), (double)this.decodeY(0.42857143F), (double)(this.decodeX(2.7142859F) - this.decodeX(0.2857143F)), (double)(this.decodeY(2.857143F) - this.decodeY(0.42857143F)), 9.0D, 9.0D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect6() {
      this.roundRect.setRoundRect((double)this.decodeX(0.08571429F), (double)this.decodeY(0.08571429F), (double)(this.decodeX(2.914286F) - this.decodeX(0.08571429F)), (double)(this.decodeY(2.9142857F) - this.decodeY(0.08571429F)), 11.0D, 11.0D);
      return this.roundRect;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.09F, 0.52F, 0.95F}, new Color[]{this.color2, this.decodeColor(this.color2, this.color3, 0.5F), this.color3});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.03F, 0.06F, 0.33F, 0.6F, 0.65F, 0.7F, 0.825F, 0.95F, 0.975F, 1.0F}, new Color[]{this.color4, this.decodeColor(this.color4, this.color5, 0.5F), this.color5, this.decodeColor(this.color5, this.color6, 0.5F), this.color6, this.decodeColor(this.color6, this.color6, 0.5F), this.color6, this.decodeColor(this.color6, this.color7, 0.5F), this.color7, this.decodeColor(this.color7, this.color8, 0.5F), this.color8});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.09F, 0.52F, 0.95F}, new Color[]{this.color10, this.decodeColor(this.color10, this.color11, 0.5F), this.color11});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.024F, 0.06F, 0.276F, 0.6F, 0.65F, 0.7F, 0.856F, 0.96F, 0.98399997F, 1.0F}, new Color[]{(Color)this.componentColors[0], this.decodeColor((Color)this.componentColors[0], (Color)this.componentColors[1], 0.5F), (Color)this.componentColors[1], this.decodeColor((Color)this.componentColors[1], (Color)this.componentColors[2], 0.5F), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[2], 0.5F), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[3], 0.5F), (Color)this.componentColors[3], this.decodeColor((Color)this.componentColors[3], (Color)this.componentColors[4], 0.5F), (Color)this.componentColors[4]});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.03F, 0.06F, 0.33F, 0.6F, 0.65F, 0.7F, 0.825F, 0.95F, 0.975F, 1.0F}, new Color[]{(Color)this.componentColors[0], this.decodeColor((Color)this.componentColors[0], (Color)this.componentColors[1], 0.5F), (Color)this.componentColors[1], this.decodeColor((Color)this.componentColors[1], (Color)this.componentColors[2], 0.5F), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[2], 0.5F), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[3], 0.5F), (Color)this.componentColors[3], this.decodeColor((Color)this.componentColors[3], (Color)this.componentColors[4], 0.5F), (Color)this.componentColors[4]});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.09F, 0.52F, 0.95F}, new Color[]{this.color17, this.decodeColor(this.color17, this.color18, 0.5F), this.color18});
   }

   private Paint decodeGradient7(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.024F, 0.06F, 0.276F, 0.6F, 0.65F, 0.7F, 0.856F, 0.96F, 0.98F, 1.0F}, new Color[]{(Color)this.componentColors[0], this.decodeColor((Color)this.componentColors[0], (Color)this.componentColors[1], 0.5F), (Color)this.componentColors[1], this.decodeColor((Color)this.componentColors[1], (Color)this.componentColors[2], 0.5F), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[2], 0.5F), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[3], 0.5F), (Color)this.componentColors[3], this.decodeColor((Color)this.componentColors[3], (Color)this.componentColors[3], 0.5F), (Color)this.componentColors[3]});
   }

   private Paint decodeGradient8(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.05F, 0.5F, 0.95F}, new Color[]{this.color23, this.decodeColor(this.color23, this.color24, 0.5F), this.color24});
   }

   private Paint decodeGradient9(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color31, this.decodeColor(this.color31, this.color32, 0.5F), this.color32});
   }

   private Paint decodeGradient10(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.06684492F, 0.13368984F, 0.56684494F, 1.0F}, new Color[]{(Color)this.componentColors[0], this.decodeColor((Color)this.componentColors[0], (Color)this.componentColors[1], 0.5F), (Color)this.componentColors[1], this.decodeColor((Color)this.componentColors[1], (Color)this.componentColors[2], 0.5F), (Color)this.componentColors[2]});
   }

   private Paint decodeGradient11(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color36, this.decodeColor(this.color36, this.color37, 0.5F), this.color37});
   }

   private Paint decodeGradient12(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color41, this.decodeColor(this.color41, this.color42, 0.5F), this.color42});
   }

   private Paint decodeGradient13(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color47, this.decodeColor(this.color47, this.color48, 0.5F), this.color48});
   }

   private Paint decodeGradient14(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.06684492F, 0.13368984F, 0.56684494F, 1.0F}, new Color[]{this.color48, this.decodeColor(this.color48, this.color49, 0.5F), this.color49, this.decodeColor(this.color49, this.color49, 0.5F), this.color49});
   }
}
