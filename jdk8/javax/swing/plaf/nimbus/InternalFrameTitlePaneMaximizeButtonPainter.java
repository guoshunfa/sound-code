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

final class InternalFrameTitlePaneMaximizeButtonPainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED_WINDOWMAXIMIZED = 1;
   static final int BACKGROUND_ENABLED_WINDOWMAXIMIZED = 2;
   static final int BACKGROUND_MOUSEOVER_WINDOWMAXIMIZED = 3;
   static final int BACKGROUND_PRESSED_WINDOWMAXIMIZED = 4;
   static final int BACKGROUND_ENABLED_WINDOWNOTFOCUSED_WINDOWMAXIMIZED = 5;
   static final int BACKGROUND_MOUSEOVER_WINDOWNOTFOCUSED_WINDOWMAXIMIZED = 6;
   static final int BACKGROUND_PRESSED_WINDOWNOTFOCUSED_WINDOWMAXIMIZED = 7;
   static final int BACKGROUND_DISABLED = 8;
   static final int BACKGROUND_ENABLED = 9;
   static final int BACKGROUND_MOUSEOVER = 10;
   static final int BACKGROUND_PRESSED = 11;
   static final int BACKGROUND_ENABLED_WINDOWNOTFOCUSED = 12;
   static final int BACKGROUND_MOUSEOVER_WINDOWNOTFOCUSED = 13;
   static final int BACKGROUND_PRESSED_WINDOWNOTFOCUSED = 14;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusGreen", 0.43362403F, -0.6792196F, 0.054901958F, 0);
   private Color color2 = this.decodeColor("nimbusGreen", 0.44056845F, -0.631913F, -0.039215684F, 0);
   private Color color3 = this.decodeColor("nimbusGreen", 0.44056845F, -0.67475206F, 0.06666666F, 0);
   private Color color4 = new Color(255, 200, 0, 255);
   private Color color5 = this.decodeColor("nimbusGreen", 0.4355179F, -0.6581704F, -0.011764705F, 0);
   private Color color6 = this.decodeColor("nimbusGreen", 0.44484192F, -0.644647F, -0.031372547F, 0);
   private Color color7 = this.decodeColor("nimbusGreen", 0.44484192F, -0.6480447F, 0.0F, 0);
   private Color color8 = this.decodeColor("nimbusGreen", 0.4366002F, -0.6368381F, -0.04705882F, 0);
   private Color color9 = this.decodeColor("nimbusGreen", 0.44484192F, -0.6423572F, -0.05098039F, 0);
   private Color color10 = this.decodeColor("nimbusBlueGrey", 0.0055555105F, -0.062449392F, 0.07058823F, 0);
   private Color color11 = this.decodeColor("nimbusBlueGrey", -0.008547008F, -0.04174325F, -0.0039215684F, -13);
   private Color color12 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.049920253F, 0.031372547F, 0);
   private Color color13 = this.decodeColor("nimbusBlueGrey", 0.0055555105F, -0.0029994324F, -0.38039216F, -185);
   private Color color14 = this.decodeColor("nimbusGreen", 0.1627907F, 0.2793296F, -0.6431373F, 0);
   private Color color15 = this.decodeColor("nimbusGreen", 0.025363803F, 0.2454313F, -0.2392157F, 0);
   private Color color16 = this.decodeColor("nimbusGreen", 0.02642706F, -0.3456704F, -0.011764705F, 0);
   private Color color17 = this.decodeColor("nimbusGreen", 0.025363803F, 0.2373128F, -0.23529413F, 0);
   private Color color18 = this.decodeColor("nimbusGreen", 0.025363803F, 0.0655365F, -0.13333333F, 0);
   private Color color19 = this.decodeColor("nimbusGreen", -0.0087068975F, -0.009330213F, -0.32156864F, 0);
   private Color color20 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -13);
   private Color color21 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -33);
   private Color color22 = this.decodeColor("nimbusGreen", 0.1627907F, 0.2793296F, -0.627451F, 0);
   private Color color23 = this.decodeColor("nimbusGreen", 0.04572721F, 0.2793296F, -0.37254903F, 0);
   private Color color24 = this.decodeColor("nimbusGreen", 0.009822637F, -0.34243205F, 0.054901958F, 0);
   private Color color25 = this.decodeColor("nimbusGreen", 0.010559708F, 0.13167858F, -0.11764705F, 0);
   private Color color26 = this.decodeColor("nimbusGreen", 0.010559708F, 0.12599629F, -0.11372548F, 0);
   private Color color27 = this.decodeColor("nimbusGreen", 0.010559708F, 9.2053413E-4F, -0.011764705F, 0);
   private Color color28 = this.decodeColor("nimbusGreen", 0.015249729F, 0.2793296F, -0.22352943F, -49);
   private Color color29 = this.decodeColor("nimbusGreen", 0.01279068F, 0.2793296F, -0.19215685F, 0);
   private Color color30 = this.decodeColor("nimbusGreen", 0.013319805F, 0.2793296F, -0.20784315F, 0);
   private Color color31 = this.decodeColor("nimbusGreen", 0.009604409F, 0.2793296F, -0.16862744F, 0);
   private Color color32 = this.decodeColor("nimbusGreen", 0.011600211F, 0.2793296F, -0.15294117F, 0);
   private Color color33 = this.decodeColor("nimbusGreen", 0.011939123F, 0.2793296F, -0.16470587F, 0);
   private Color color34 = this.decodeColor("nimbusGreen", 0.009506017F, 0.257901F, -0.15294117F, 0);
   private Color color35 = this.decodeColor("nimbusGreen", -0.17054264F, -0.7206704F, -0.7019608F, 0);
   private Color color36 = this.decodeColor("nimbusGreen", 0.07804492F, 0.2793296F, -0.47058827F, 0);
   private Color color37 = this.decodeColor("nimbusGreen", 0.03592503F, -0.23865601F, -0.15686274F, 0);
   private Color color38 = this.decodeColor("nimbusGreen", 0.035979107F, 0.23766291F, -0.3254902F, 0);
   private Color color39 = this.decodeColor("nimbusGreen", 0.03690417F, 0.2793296F, -0.33333334F, 0);
   private Color color40 = this.decodeColor("nimbusGreen", 0.09681849F, 0.2793296F, -0.5137255F, 0);
   private Color color41 = this.decodeColor("nimbusGreen", 0.06535478F, 0.2793296F, -0.44705883F, 0);
   private Color color42 = this.decodeColor("nimbusGreen", 0.0675526F, 0.2793296F, -0.454902F, 0);
   private Color color43 = this.decodeColor("nimbusGreen", 0.060800627F, 0.2793296F, -0.4392157F, 0);
   private Color color44 = this.decodeColor("nimbusGreen", 0.06419912F, 0.2793296F, -0.42352942F, 0);
   private Color color45 = this.decodeColor("nimbusGreen", 0.06375685F, 0.2793296F, -0.43137255F, 0);
   private Color color46 = this.decodeColor("nimbusGreen", 0.048207358F, 0.2793296F, -0.3882353F, 0);
   private Color color47 = this.decodeColor("nimbusGreen", 0.057156876F, 0.2793296F, -0.42352942F, 0);
   private Color color48 = this.decodeColor("nimbusGreen", 0.44056845F, -0.62133265F, -0.109803915F, 0);
   private Color color49 = this.decodeColor("nimbusGreen", 0.44056845F, -0.5843068F, -0.27058825F, 0);
   private Color color50 = this.decodeColor("nimbusGreen", 0.4294573F, -0.698349F, 0.17647058F, 0);
   private Color color51 = this.decodeColor("nimbusGreen", 0.45066953F, -0.665394F, 0.07843137F, 0);
   private Color color52 = this.decodeColor("nimbusGreen", 0.44056845F, -0.65913194F, 0.062745094F, 0);
   private Color color53 = this.decodeColor("nimbusGreen", 0.44056845F, -0.6609689F, 0.086274505F, 0);
   private Color color54 = this.decodeColor("nimbusGreen", 0.44056845F, -0.6578432F, 0.04705882F, 0);
   private Color color55 = this.decodeColor("nimbusGreen", 0.4355179F, -0.6633787F, 0.05098039F, 0);
   private Color color56 = this.decodeColor("nimbusGreen", 0.4355179F, -0.664548F, 0.06666666F, 0);
   private Color color57 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.029445238F, -0.30980393F, -13);
   private Color color58 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.027957506F, -0.31764707F, -33);
   private Color color59 = this.decodeColor("nimbusGreen", 0.43202144F, -0.64722407F, -0.007843137F, 0);
   private Color color60 = this.decodeColor("nimbusGreen", 0.44056845F, -0.6339652F, -0.02352941F, 0);
   private Color color61 = new Color(165, 169, 176, 255);
   private Color color62 = this.decodeColor("nimbusBlueGrey", -0.00505054F, -0.057128258F, 0.062745094F, 0);
   private Color color63 = this.decodeColor("nimbusBlueGrey", -0.003968239F, -0.035257496F, -0.015686274F, 0);
   private Color color64 = new Color(64, 88, 0, 255);
   private Color color65 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
   private Color color66 = this.decodeColor("nimbusBlueGrey", 0.004830897F, -0.00920473F, 0.14509803F, -101);
   private Color color67 = this.decodeColor("nimbusGreen", 0.009564877F, 0.100521624F, -0.109803915F, 0);
   private Color color68 = new Color(113, 125, 0, 255);
   private Color color69 = this.decodeColor("nimbusBlueGrey", 0.0025252104F, -0.0067527294F, 0.086274505F, -65);
   private Color color70 = this.decodeColor("nimbusGreen", 0.03129223F, 0.2793296F, -0.27450982F, 0);
   private Color color71 = new Color(19, 48, 0, 255);
   private Color color72 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.029445238F, -0.30980393F, 0);
   private Object[] componentColors;

   public InternalFrameTitlePaneMaximizeButtonPainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 1:
         this.paintBackgroundDisabledAndWindowMaximized(var1);
         break;
      case 2:
         this.paintBackgroundEnabledAndWindowMaximized(var1);
         break;
      case 3:
         this.paintBackgroundMouseOverAndWindowMaximized(var1);
         break;
      case 4:
         this.paintBackgroundPressedAndWindowMaximized(var1);
         break;
      case 5:
         this.paintBackgroundEnabledAndWindowNotFocusedAndWindowMaximized(var1);
         break;
      case 6:
         this.paintBackgroundMouseOverAndWindowNotFocusedAndWindowMaximized(var1);
         break;
      case 7:
         this.paintBackgroundPressedAndWindowNotFocusedAndWindowMaximized(var1);
         break;
      case 8:
         this.paintBackgroundDisabled(var1);
         break;
      case 9:
         this.paintBackgroundEnabled(var1);
         break;
      case 10:
         this.paintBackgroundMouseOver(var1);
         break;
      case 11:
         this.paintBackgroundPressed(var1);
         break;
      case 12:
         this.paintBackgroundEnabledAndWindowNotFocused(var1);
         break;
      case 13:
         this.paintBackgroundMouseOverAndWindowNotFocused(var1);
         break;
      case 14:
         this.paintBackgroundPressedAndWindowNotFocused(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundDisabledAndWindowMaximized(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient1(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient2(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color5);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color6);
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color6);
      var1.fill(this.rect);
      this.rect = this.decodeRect5();
      var1.setPaint(this.color7);
      var1.fill(this.rect);
      this.rect = this.decodeRect6();
      var1.setPaint(this.color8);
      var1.fill(this.rect);
      this.rect = this.decodeRect7();
      var1.setPaint(this.color9);
      var1.fill(this.rect);
      this.rect = this.decodeRect8();
      var1.setPaint(this.color7);
      var1.fill(this.rect);
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient3(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color12);
      var1.fill(this.path);
   }

   private void paintBackgroundEnabledAndWindowMaximized(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.color13);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient5(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color19);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color19);
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color19);
      var1.fill(this.rect);
      this.rect = this.decodeRect5();
      var1.setPaint(this.color19);
      var1.fill(this.rect);
      this.rect = this.decodeRect9();
      var1.setPaint(this.color19);
      var1.fill(this.rect);
      this.rect = this.decodeRect7();
      var1.setPaint(this.color19);
      var1.fill(this.rect);
      this.rect = this.decodeRect10();
      var1.setPaint(this.color19);
      var1.fill(this.rect);
      this.rect = this.decodeRect8();
      var1.setPaint(this.color19);
      var1.fill(this.rect);
      this.path = this.decodePath1();
      var1.setPaint(this.color20);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color21);
      var1.fill(this.path);
   }

   private void paintBackgroundMouseOverAndWindowMaximized(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.color13);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient6(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient7(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color28);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color29);
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color30);
      var1.fill(this.rect);
      this.rect = this.decodeRect5();
      var1.setPaint(this.color31);
      var1.fill(this.rect);
      this.rect = this.decodeRect9();
      var1.setPaint(this.color32);
      var1.fill(this.rect);
      this.rect = this.decodeRect7();
      var1.setPaint(this.color33);
      var1.fill(this.rect);
      this.rect = this.decodeRect10();
      var1.setPaint(this.color34);
      var1.fill(this.rect);
      this.rect = this.decodeRect8();
      var1.setPaint(this.color31);
      var1.fill(this.rect);
      this.path = this.decodePath1();
      var1.setPaint(this.color20);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color21);
      var1.fill(this.path);
   }

   private void paintBackgroundPressedAndWindowMaximized(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.color13);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient8(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient9(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color40);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color41);
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color42);
      var1.fill(this.rect);
      this.rect = this.decodeRect5();
      var1.setPaint(this.color43);
      var1.fill(this.rect);
      this.rect = this.decodeRect6();
      var1.setPaint(this.color44);
      var1.fill(this.rect);
      this.rect = this.decodeRect7();
      var1.setPaint(this.color45);
      var1.fill(this.rect);
      this.rect = this.decodeRect10();
      var1.setPaint(this.color46);
      var1.fill(this.rect);
      this.rect = this.decodeRect8();
      var1.setPaint(this.color47);
      var1.fill(this.rect);
      this.path = this.decodePath1();
      var1.setPaint(this.color20);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color21);
      var1.fill(this.path);
   }

   private void paintBackgroundEnabledAndWindowNotFocusedAndWindowMaximized(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient10(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient11(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color54);
      var1.fill(this.rect);
      this.rect = this.decodeRect5();
      var1.setPaint(this.color55);
      var1.fill(this.rect);
      this.rect = this.decodeRect8();
      var1.setPaint(this.color56);
      var1.fill(this.rect);
      this.path = this.decodePath1();
      var1.setPaint(this.color57);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color58);
      var1.fill(this.path);
   }

   private void paintBackgroundMouseOverAndWindowNotFocusedAndWindowMaximized(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.color13);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient6(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient7(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color28);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color29);
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color30);
      var1.fill(this.rect);
      this.rect = this.decodeRect5();
      var1.setPaint(this.color31);
      var1.fill(this.rect);
      this.rect = this.decodeRect9();
      var1.setPaint(this.color32);
      var1.fill(this.rect);
      this.rect = this.decodeRect7();
      var1.setPaint(this.color33);
      var1.fill(this.rect);
      this.rect = this.decodeRect10();
      var1.setPaint(this.color34);
      var1.fill(this.rect);
      this.rect = this.decodeRect8();
      var1.setPaint(this.color31);
      var1.fill(this.rect);
      this.path = this.decodePath1();
      var1.setPaint(this.color20);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color21);
      var1.fill(this.path);
   }

   private void paintBackgroundPressedAndWindowNotFocusedAndWindowMaximized(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.color13);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient8(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient9(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color40);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color41);
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color42);
      var1.fill(this.rect);
      this.rect = this.decodeRect5();
      var1.setPaint(this.color43);
      var1.fill(this.rect);
      this.rect = this.decodeRect6();
      var1.setPaint(this.color44);
      var1.fill(this.rect);
      this.rect = this.decodeRect7();
      var1.setPaint(this.color45);
      var1.fill(this.rect);
      this.rect = this.decodeRect10();
      var1.setPaint(this.color46);
      var1.fill(this.rect);
      this.rect = this.decodeRect8();
      var1.setPaint(this.color47);
      var1.fill(this.rect);
      this.path = this.decodePath1();
      var1.setPaint(this.color20);
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color21);
      var1.fill(this.path);
   }

   private void paintBackgroundDisabled(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient1(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient12(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
      this.path = this.decodePath3();
      var1.setPaint(this.color61);
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.decodeGradient13(this.path));
      var1.fill(this.path);
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.color13);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient5(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
      this.path = this.decodePath3();
      var1.setPaint(this.color64);
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.color65);
      var1.fill(this.path);
   }

   private void paintBackgroundMouseOver(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.color66);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient6(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient14(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
      this.path = this.decodePath3();
      var1.setPaint(this.color68);
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.color65);
      var1.fill(this.path);
   }

   private void paintBackgroundPressed(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.color69);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient8(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient15(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
      this.path = this.decodePath3();
      var1.setPaint(this.color71);
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.color65);
      var1.fill(this.path);
   }

   private void paintBackgroundEnabledAndWindowNotFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient10(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient16(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
      this.path = this.decodePath4();
      var1.setPaint(this.color72);
      var1.fill(this.path);
   }

   private void paintBackgroundMouseOverAndWindowNotFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.color66);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient6(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient14(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
      this.path = this.decodePath3();
      var1.setPaint(this.color68);
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.color65);
      var1.fill(this.path);
   }

   private void paintBackgroundPressedAndWindowNotFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.color69);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient8(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient15(this.roundRect));
      var1.fill(this.roundRect);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color4);
      var1.fill(this.rect);
      this.path = this.decodePath3();
      var1.setPaint(this.color71);
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.color65);
      var1.fill(this.path);
   }

   private RoundRectangle2D decodeRoundRect1() {
      this.roundRect.setRoundRect((double)this.decodeX(1.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(1.9444444F) - this.decodeY(1.0F)), 8.600000381469727D, 8.600000381469727D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect2() {
      this.roundRect.setRoundRect((double)this.decodeX(1.0526316F), (double)this.decodeY(1.0555556F), (double)(this.decodeX(1.9473684F) - this.decodeX(1.0526316F)), (double)(this.decodeY(1.8888888F) - this.decodeY(1.0555556F)), 6.75D, 6.75D);
      return this.roundRect;
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(1.0F) - this.decodeX(1.0F)), (double)(this.decodeY(1.0F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect2() {
      this.rect.setRect((double)this.decodeX(1.2165072F), (double)this.decodeY(1.2790405F), (double)(this.decodeX(1.6746411F) - this.decodeX(1.2165072F)), (double)(this.decodeY(1.3876263F) - this.decodeY(1.2790405F)));
      return this.rect;
   }

   private Rectangle2D decodeRect3() {
      this.rect.setRect((double)this.decodeX(1.2212919F), (double)this.decodeY(1.6047981F), (double)(this.decodeX(1.270335F) - this.decodeX(1.2212919F)), (double)(this.decodeY(1.3876263F) - this.decodeY(1.6047981F)));
      return this.rect;
   }

   private Rectangle2D decodeRect4() {
      this.rect.setRect((double)this.decodeX(1.2643541F), (double)this.decodeY(1.5542929F), (double)(this.decodeX(1.6315789F) - this.decodeX(1.2643541F)), (double)(this.decodeY(1.5997474F) - this.decodeY(1.5542929F)));
      return this.rect;
   }

   private Rectangle2D decodeRect5() {
      this.rect.setRect((double)this.decodeX(1.6267943F), (double)this.decodeY(1.3888888F), (double)(this.decodeX(1.673445F) - this.decodeX(1.6267943F)), (double)(this.decodeY(1.6085858F) - this.decodeY(1.3888888F)));
      return this.rect;
   }

   private Rectangle2D decodeRect6() {
      this.rect.setRect((double)this.decodeX(1.3684211F), (double)this.decodeY(1.6111112F), (double)(this.decodeX(1.4210527F) - this.decodeX(1.3684211F)), (double)(this.decodeY(1.7777778F) - this.decodeY(1.6111112F)));
      return this.rect;
   }

   private Rectangle2D decodeRect7() {
      this.rect.setRect((double)this.decodeX(1.4389952F), (double)this.decodeY(1.7209597F), (double)(this.decodeX(1.7882775F) - this.decodeX(1.4389952F)), (double)(this.decodeY(1.7765152F) - this.decodeY(1.7209597F)));
      return this.rect;
   }

   private Rectangle2D decodeRect8() {
      this.rect.setRect((double)this.decodeX(1.5645933F), (double)this.decodeY(1.4078283F), (double)(this.decodeX(1.7870812F) - this.decodeX(1.5645933F)), (double)(this.decodeY(1.5239899F) - this.decodeY(1.4078283F)));
      return this.rect;
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.2105263F), (double)this.decodeY(1.2222222F));
      this.path.lineTo((double)this.decodeX(1.6315789F), (double)this.decodeY(1.2222222F));
      this.path.lineTo((double)this.decodeX(1.6315789F), (double)this.decodeY(1.5555556F));
      this.path.lineTo((double)this.decodeX(1.2105263F), (double)this.decodeY(1.5555556F));
      this.path.lineTo((double)this.decodeX(1.2105263F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(1.2631578F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(1.2631578F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(1.5789473F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(1.5789473F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(1.2105263F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(1.2105263F), (double)this.decodeY(1.2222222F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.6842105F), (double)this.decodeY(1.3888888F));
      this.path.lineTo((double)this.decodeX(1.6842105F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(1.7368422F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(1.7368422F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(1.4210527F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(1.4210527F), (double)this.decodeY(1.6111112F));
      this.path.lineTo((double)this.decodeX(1.3684211F), (double)this.decodeY(1.6111112F));
      this.path.lineTo((double)this.decodeX(1.3684211F), (double)this.decodeY(1.7222222F));
      this.path.lineTo((double)this.decodeX(1.7894738F), (double)this.decodeY(1.7222222F));
      this.path.lineTo((double)this.decodeX(1.7894738F), (double)this.decodeY(1.3888888F));
      this.path.lineTo((double)this.decodeX(1.6842105F), (double)this.decodeY(1.3888888F));
      this.path.closePath();
      return this.path;
   }

   private RoundRectangle2D decodeRoundRect3() {
      this.roundRect.setRoundRect((double)this.decodeX(1.0F), (double)this.decodeY(1.6111112F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(2.0F) - this.decodeY(1.6111112F)), 6.0D, 6.0D);
      return this.roundRect;
   }

   private Rectangle2D decodeRect9() {
      this.rect.setRect((double)this.decodeX(1.3815789F), (double)this.decodeY(1.6111112F), (double)(this.decodeX(1.4366028F) - this.decodeX(1.3815789F)), (double)(this.decodeY(1.7739899F) - this.decodeY(1.6111112F)));
      return this.rect;
   }

   private Rectangle2D decodeRect10() {
      this.rect.setRect((double)this.decodeX(1.7918661F), (double)this.decodeY(1.7752526F), (double)(this.decodeX(1.8349283F) - this.decodeX(1.7918661F)), (double)(this.decodeY(1.4217172F) - this.decodeY(1.7752526F)));
      return this.rect;
   }

   private Path2D decodePath3() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.1913875F), (double)this.decodeY(1.2916666F));
      this.path.lineTo((double)this.decodeX(1.1925838F), (double)this.decodeY(1.7462121F));
      this.path.lineTo((double)this.decodeX(1.8157895F), (double)this.decodeY(1.7449496F));
      this.path.lineTo((double)this.decodeX(1.819378F), (double)this.decodeY(1.2916666F));
      this.path.lineTo((double)this.decodeX(1.722488F), (double)this.decodeY(1.2916666F));
      this.path.lineTo((double)this.decodeX(1.7320573F), (double)this.decodeY(1.669192F));
      this.path.lineTo((double)this.decodeX(1.2799044F), (double)this.decodeY(1.6565657F));
      this.path.lineTo((double)this.decodeX(1.284689F), (double)this.decodeY(1.3863636F));
      this.path.lineTo((double)this.decodeX(1.7260766F), (double)this.decodeY(1.385101F));
      this.path.lineTo((double)this.decodeX(1.722488F), (double)this.decodeY(1.2904041F));
      this.path.lineTo((double)this.decodeX(1.1913875F), (double)this.decodeY(1.2916666F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath4() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.2105263F), (double)this.decodeY(1.2222222F));
      this.path.lineTo((double)this.decodeX(1.2105263F), (double)this.decodeY(1.7222222F));
      this.path.lineTo((double)this.decodeX(1.7894738F), (double)this.decodeY(1.7222222F));
      this.path.lineTo((double)this.decodeX(1.7894738F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(1.7368422F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(1.7368422F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(1.2631578F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(1.2631578F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(1.7894738F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(1.7894738F), (double)this.decodeY(1.2222222F));
      this.path.lineTo((double)this.decodeX(1.2105263F), (double)this.decodeY(1.2222222F));
      this.path.closePath();
      return this.path;
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
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color3, this.decodeColor(this.color3, this.color2, 0.5F), this.color2});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color10, this.decodeColor(this.color10, this.color11, 0.5F), this.color11});
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
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F}, new Color[]{this.color16, this.decodeColor(this.color16, this.color15, 0.5F), this.color15, this.decodeColor(this.color15, this.color17, 0.5F), this.color17, this.decodeColor(this.color17, this.color18, 0.5F), this.color18});
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
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F}, new Color[]{this.color24, this.decodeColor(this.color24, this.color25, 0.5F), this.color25, this.decodeColor(this.color25, this.color26, 0.5F), this.color26, this.decodeColor(this.color26, this.color27, 0.5F), this.color27});
   }

   private Paint decodeGradient8(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.24868421F * var5 + var3, 0.0014705883F * var6 + var4, 0.24868421F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color35, this.decodeColor(this.color35, this.color36, 0.5F), this.color36});
   }

   private Paint decodeGradient9(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F}, new Color[]{this.color37, this.decodeColor(this.color37, this.color38, 0.5F), this.color38, this.decodeColor(this.color38, this.color39, 0.5F), this.color39, this.decodeColor(this.color39, this.color18, 0.5F), this.color18});
   }

   private Paint decodeGradient10(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.24868421F * var5 + var3, 0.0014705883F * var6 + var4, 0.24868421F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color48, this.decodeColor(this.color48, this.color49, 0.5F), this.color49});
   }

   private Paint decodeGradient11(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F}, new Color[]{this.color50, this.decodeColor(this.color50, this.color51, 0.5F), this.color51, this.decodeColor(this.color51, this.color52, 0.5F), this.color52, this.decodeColor(this.color52, this.color53, 0.5F), this.color53});
   }

   private Paint decodeGradient12(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.6082097F, 0.6766467F, 0.83832335F, 1.0F}, new Color[]{this.color3, this.decodeColor(this.color3, this.color59, 0.5F), this.color59, this.decodeColor(this.color59, this.color60, 0.5F), this.color60, this.decodeColor(this.color60, this.color2, 0.5F), this.color2});
   }

   private Paint decodeGradient13(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.26047903F, 0.6302395F, 1.0F}, new Color[]{this.color62, this.decodeColor(this.color62, this.color63, 0.5F), this.color63});
   }

   private Paint decodeGradient14(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F}, new Color[]{this.color24, this.decodeColor(this.color24, this.color67, 0.5F), this.color67, this.decodeColor(this.color67, this.color25, 0.5F), this.color25, this.decodeColor(this.color25, this.color27, 0.5F), this.color27});
   }

   private Paint decodeGradient15(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.66659296F, 0.79341316F, 0.8967066F, 1.0F}, new Color[]{this.color37, this.decodeColor(this.color37, this.color38, 0.5F), this.color38, this.decodeColor(this.color38, this.color39, 0.5F), this.color39, this.decodeColor(this.color39, this.color70, 0.5F), this.color70});
   }

   private Paint decodeGradient16(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25441176F * var5 + var3, 1.0016667F * var6 + var4, new float[]{0.0F, 0.26988637F, 0.53977275F, 0.6291678F, 0.7185629F, 0.8592814F, 1.0F}, new Color[]{this.color50, this.decodeColor(this.color50, this.color52, 0.5F), this.color52, this.decodeColor(this.color52, this.color52, 0.5F), this.color52, this.decodeColor(this.color52, this.color53, 0.5F), this.color53});
   }
}
