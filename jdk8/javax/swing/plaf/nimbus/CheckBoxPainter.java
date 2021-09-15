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

final class CheckBoxPainter extends AbstractRegionPainter {
   static final int BACKGROUND_DISABLED = 1;
   static final int BACKGROUND_ENABLED = 2;
   static final int ICON_DISABLED = 3;
   static final int ICON_ENABLED = 4;
   static final int ICON_FOCUSED = 5;
   static final int ICON_MOUSEOVER = 6;
   static final int ICON_MOUSEOVER_FOCUSED = 7;
   static final int ICON_PRESSED = 8;
   static final int ICON_PRESSED_FOCUSED = 9;
   static final int ICON_SELECTED = 10;
   static final int ICON_SELECTED_FOCUSED = 11;
   static final int ICON_PRESSED_SELECTED = 12;
   static final int ICON_PRESSED_SELECTED_FOCUSED = 13;
   static final int ICON_MOUSEOVER_SELECTED = 14;
   static final int ICON_MOUSEOVER_SELECTED_FOCUSED = 15;
   static final int ICON_DISABLED_SELECTED = 16;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06766917F, 0.07843137F, 0);
   private Color color2 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.06484103F, 0.027450979F, 0);
   private Color color3 = this.decodeColor("nimbusBase", 0.032459438F, -0.60996324F, 0.36470586F, 0);
   private Color color4 = this.decodeColor("nimbusBase", 0.02551502F, -0.5996783F, 0.3215686F, 0);
   private Color color5 = this.decodeColor("nimbusBase", 0.032459438F, -0.59624064F, 0.34509802F, 0);
   private Color color6 = this.decodeColor("nimbusBlueGrey", 0.0F, 0.0F, 0.0F, -89);
   private Color color7 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.05356429F, -0.12549019F, 0);
   private Color color8 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.015789472F, -0.37254903F, 0);
   private Color color9 = this.decodeColor("nimbusBase", 0.08801502F, -0.63174605F, 0.43921566F, 0);
   private Color color10 = this.decodeColor("nimbusBase", 0.032459438F, -0.5953556F, 0.32549018F, 0);
   private Color color11 = this.decodeColor("nimbusBase", 0.032459438F, -0.59942394F, 0.4235294F, 0);
   private Color color12 = this.decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
   private Color color13 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.020974077F, -0.21960783F, 0);
   private Color color14 = this.decodeColor("nimbusBlueGrey", 0.01010108F, 0.08947369F, -0.5294118F, 0);
   private Color color15 = this.decodeColor("nimbusBase", 0.08801502F, -0.6317773F, 0.4470588F, 0);
   private Color color16 = this.decodeColor("nimbusBase", 0.032459438F, -0.5985242F, 0.39999998F, 0);
   private Color color17 = this.decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, 0);
   private Color color18 = this.decodeColor("nimbusBlueGrey", 0.055555582F, 0.8894737F, -0.7176471F, 0);
   private Color color19 = this.decodeColor("nimbusBlueGrey", 0.0F, 0.0016232133F, -0.3254902F, 0);
   private Color color20 = this.decodeColor("nimbusBase", 0.027408898F, -0.5847884F, 0.2980392F, 0);
   private Color color21 = this.decodeColor("nimbusBase", 0.029681683F, -0.52701867F, 0.17254901F, 0);
   private Color color22 = this.decodeColor("nimbusBase", 0.029681683F, -0.5376751F, 0.25098038F, 0);
   private Color color23 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.34585923F, -0.007843137F, 0);
   private Color color24 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.10238093F, -0.25490198F, 0);
   private Color color25 = this.decodeColor("nimbusBase", 0.004681647F, -0.6197143F, 0.43137252F, 0);
   private Color color26 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.44153953F, 0.2588235F, 0);
   private Color color27 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.4602757F, 0.34509802F, 0);
   private Color color28 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
   private Color color29 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
   private Color color30 = this.decodeColor("nimbusBase", -3.528595E-5F, 0.026785731F, -0.23529413F, 0);
   private Color color31 = this.decodeColor("nimbusBase", -4.2033195E-4F, -0.38050595F, 0.20392156F, 0);
   private Color color32 = this.decodeColor("nimbusBase", -0.0021489263F, -0.2891234F, 0.14117646F, 0);
   private Color color33 = this.decodeColor("nimbusBase", -0.006362498F, -0.016311288F, -0.02352941F, 0);
   private Color color34 = this.decodeColor("nimbusBase", 0.0F, -0.17930403F, 0.21568626F, 0);
   private Color color35 = this.decodeColor("nimbusBase", 0.0013483167F, -0.1769987F, -0.12156865F, 0);
   private Color color36 = this.decodeColor("nimbusBase", 0.05468172F, 0.3642857F, -0.43137258F, 0);
   private Color color37 = this.decodeColor("nimbusBase", 0.004681647F, -0.6198413F, 0.43921566F, 0);
   private Color color38 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.4555341F, 0.3215686F, 0);
   private Color color39 = this.decodeColor("nimbusBase", 5.1498413E-4F, -0.47377098F, 0.41960782F, 0);
   private Color color40 = this.decodeColor("nimbusBlueGrey", -0.01111114F, -0.03771078F, 0.062745094F, 0);
   private Color color41 = this.decodeColor("nimbusBlueGrey", -0.02222222F, -0.032806106F, 0.011764705F, 0);
   private Color color42 = this.decodeColor("nimbusBase", 0.021348298F, -0.59223604F, 0.35294116F, 0);
   private Color color43 = this.decodeColor("nimbusBase", 0.021348298F, -0.56722116F, 0.3098039F, 0);
   private Color color44 = this.decodeColor("nimbusBase", 0.021348298F, -0.56875F, 0.32941175F, 0);
   private Color color45 = this.decodeColor("nimbusBase", 0.027408898F, -0.5735674F, 0.14509803F, 0);
   private Object[] componentColors;

   public CheckBoxPainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 3:
         this.painticonDisabled(var1);
         break;
      case 4:
         this.painticonEnabled(var1);
         break;
      case 5:
         this.painticonFocused(var1);
         break;
      case 6:
         this.painticonMouseOver(var1);
         break;
      case 7:
         this.painticonMouseOverAndFocused(var1);
         break;
      case 8:
         this.painticonPressed(var1);
         break;
      case 9:
         this.painticonPressedAndFocused(var1);
         break;
      case 10:
         this.painticonSelected(var1);
         break;
      case 11:
         this.painticonSelectedAndFocused(var1);
         break;
      case 12:
         this.painticonPressedAndSelected(var1);
         break;
      case 13:
         this.painticonPressedAndSelectedAndFocused(var1);
         break;
      case 14:
         this.painticonMouseOverAndSelected(var1);
         break;
      case 15:
         this.painticonMouseOverAndSelectedAndFocused(var1);
         break;
      case 16:
         this.painticonDisabledAndSelected(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void painticonDisabled(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient1(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient2(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void painticonEnabled(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.color6);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient3(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void painticonFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect4();
      var1.setPaint(this.color12);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient3(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient4(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void painticonMouseOver(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.color6);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient5(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient6(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void painticonMouseOverAndFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect4();
      var1.setPaint(this.color12);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient5(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient6(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void painticonPressed(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.color6);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient7(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient8(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void painticonPressedAndFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect4();
      var1.setPaint(this.color12);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient7(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient8(this.roundRect));
      var1.fill(this.roundRect);
   }

   private void painticonSelected(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.color6);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient9(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient10(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.color28);
      var1.fill(this.path);
   }

   private void painticonSelectedAndFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect4();
      var1.setPaint(this.color12);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient9(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient10(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.color28);
      var1.fill(this.path);
   }

   private void painticonPressedAndSelected(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.color29);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient11(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient12(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.color28);
      var1.fill(this.path);
   }

   private void painticonPressedAndSelectedAndFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect4();
      var1.setPaint(this.color12);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient11(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient12(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.color28);
      var1.fill(this.path);
   }

   private void painticonMouseOverAndSelected(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect3();
      var1.setPaint(this.color6);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient13(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient14(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.color28);
      var1.fill(this.path);
   }

   private void painticonMouseOverAndSelectedAndFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect4();
      var1.setPaint(this.color12);
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient13(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient14(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.color28);
      var1.fill(this.path);
   }

   private void painticonDisabledAndSelected(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.decodeGradient15(this.roundRect));
      var1.fill(this.roundRect);
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.decodeGradient16(this.roundRect));
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.color45);
      var1.fill(this.path);
   }

   private RoundRectangle2D decodeRoundRect1() {
      this.roundRect.setRoundRect((double)this.decodeX(0.4F), (double)this.decodeY(0.4F), (double)(this.decodeX(2.6F) - this.decodeX(0.4F)), (double)(this.decodeY(2.6F) - this.decodeY(0.4F)), 3.7058823108673096D, 3.7058823108673096D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect2() {
      this.roundRect.setRoundRect((double)this.decodeX(0.6F), (double)this.decodeY(0.6F), (double)(this.decodeX(2.4F) - this.decodeX(0.6F)), (double)(this.decodeY(2.4F) - this.decodeY(0.6F)), 3.7647058963775635D, 3.7647058963775635D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect3() {
      this.roundRect.setRoundRect((double)this.decodeX(0.4F), (double)this.decodeY(1.75F), (double)(this.decodeX(2.6F) - this.decodeX(0.4F)), (double)(this.decodeY(2.8F) - this.decodeY(1.75F)), 5.176470756530762D, 5.176470756530762D);
      return this.roundRect;
   }

   private RoundRectangle2D decodeRoundRect4() {
      this.roundRect.setRoundRect((double)this.decodeX(0.120000005F), (double)this.decodeY(0.120000005F), (double)(this.decodeX(2.8799999F) - this.decodeX(0.120000005F)), (double)(this.decodeY(2.8799999F) - this.decodeY(0.120000005F)), 8.0D, 8.0D);
      return this.roundRect;
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0036764F), (double)this.decodeY(1.382353F));
      this.path.lineTo((double)this.decodeX(1.2536764F), (double)this.decodeY(1.382353F));
      this.path.lineTo((double)this.decodeX(1.430147F), (double)this.decodeY(1.757353F));
      this.path.lineTo((double)this.decodeX(1.8235294F), (double)this.decodeY(0.62352943F));
      this.path.lineTo((double)this.decodeX(2.2F), (double)this.decodeY(0.61764705F));
      this.path.lineTo((double)this.decodeX(1.492647F), (double)this.decodeY(2.0058823F));
      this.path.lineTo((double)this.decodeX(1.382353F), (double)this.decodeY(2.0058823F));
      this.path.lineTo((double)this.decodeX(1.0036764F), (double)this.decodeY(1.382353F));
      this.path.closePath();
      return this.path;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25210086F * var5 + var3, 0.9957983F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color1, this.decodeColor(this.color1, this.color2, 0.5F), this.color2});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25F * var5 + var3, 0.997549F * var6 + var4, new float[]{0.0F, 0.32228917F, 0.64457834F, 0.82228917F, 1.0F}, new Color[]{this.color3, this.decodeColor(this.color3, this.color4, 0.5F), this.color4, this.decodeColor(this.color4, this.color5, 0.5F), this.color5});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25210086F * var5 + var3, 0.9957983F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color7, this.decodeColor(this.color7, this.color8, 0.5F), this.color8});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25F * var5 + var3, 0.997549F * var6 + var4, new float[]{0.0F, 0.32228917F, 0.64457834F, 0.82228917F, 1.0F}, new Color[]{this.color9, this.decodeColor(this.color9, this.color10, 0.5F), this.color10, this.decodeColor(this.color10, this.color11, 0.5F), this.color11});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25210086F * var5 + var3, 0.9957983F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color13, this.decodeColor(this.color13, this.color14, 0.5F), this.color14});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25F * var5 + var3, 0.997549F * var6 + var4, new float[]{0.0F, 0.32228917F, 0.64457834F, 0.82228917F, 1.0F}, new Color[]{this.color15, this.decodeColor(this.color15, this.color16, 0.5F), this.color16, this.decodeColor(this.color16, this.color17, 0.5F), this.color17});
   }

   private Paint decodeGradient7(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25210086F * var5 + var3, 0.9957983F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color18, this.decodeColor(this.color18, this.color19, 0.5F), this.color19});
   }

   private Paint decodeGradient8(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25F * var5 + var3, 0.997549F * var6 + var4, new float[]{0.0F, 0.32228917F, 0.64457834F, 0.82228917F, 1.0F}, new Color[]{this.color20, this.decodeColor(this.color20, this.color21, 0.5F), this.color21, this.decodeColor(this.color21, this.color22, 0.5F), this.color22});
   }

   private Paint decodeGradient9(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25210086F * var5 + var3, 0.9957983F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color23, this.decodeColor(this.color23, this.color24, 0.5F), this.color24});
   }

   private Paint decodeGradient10(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25F * var5 + var3, 0.997549F * var6 + var4, new float[]{0.0F, 0.32228917F, 0.64457834F, 0.82228917F, 1.0F}, new Color[]{this.color25, this.decodeColor(this.color25, this.color26, 0.5F), this.color26, this.decodeColor(this.color26, this.color27, 0.5F), this.color27});
   }

   private Paint decodeGradient11(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25210086F * var5 + var3, 0.9957983F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color28, this.decodeColor(this.color28, this.color30, 0.5F), this.color30});
   }

   private Paint decodeGradient12(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25F * var5 + var3, 0.997549F * var6 + var4, new float[]{0.0F, 0.05775076F, 0.11550152F, 0.38003993F, 0.64457834F, 0.82228917F, 1.0F}, new Color[]{this.color31, this.decodeColor(this.color31, this.color32, 0.5F), this.color32, this.decodeColor(this.color32, this.color33, 0.5F), this.color33, this.decodeColor(this.color33, this.color34, 0.5F), this.color34});
   }

   private Paint decodeGradient13(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25210086F * var5 + var3, 0.9957983F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color35, this.decodeColor(this.color35, this.color36, 0.5F), this.color36});
   }

   private Paint decodeGradient14(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25F * var5 + var3, 0.997549F * var6 + var4, new float[]{0.0F, 0.32228917F, 0.64457834F, 0.82228917F, 1.0F}, new Color[]{this.color37, this.decodeColor(this.color37, this.color38, 0.5F), this.color38, this.decodeColor(this.color38, this.color39, 0.5F), this.color39});
   }

   private Paint decodeGradient15(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25210086F * var5 + var3, 0.9957983F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color40, this.decodeColor(this.color40, this.color41, 0.5F), this.color41});
   }

   private Paint decodeGradient16(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.25F * var5 + var3, 0.997549F * var6 + var4, new float[]{0.0F, 0.32228917F, 0.64457834F, 0.82228917F, 1.0F}, new Color[]{this.color42, this.decodeColor(this.color42, this.color43, 0.5F), this.color43, this.decodeColor(this.color43, this.color44, 0.5F), this.color44});
   }
}
