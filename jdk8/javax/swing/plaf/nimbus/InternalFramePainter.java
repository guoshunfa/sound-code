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

final class InternalFramePainter extends AbstractRegionPainter {
   static final int BACKGROUND_ENABLED = 1;
   static final int BACKGROUND_ENABLED_WINDOWFOCUSED = 2;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("nimbusBase", 0.032459438F, -0.53637654F, 0.043137252F, 0);
   private Color color2 = this.decodeColor("nimbusBlueGrey", 0.004273474F, -0.039488062F, -0.027450979F, 0);
   private Color color3 = this.decodeColor("nimbusBlueGrey", -0.00505054F, -0.056339122F, 0.05098039F, 0);
   private Color color4 = this.decodeColor("nimbusBlueGrey", -0.01111114F, -0.06357796F, 0.09019607F, 0);
   private Color color5 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.023821115F, -0.06666666F, 0);
   private Color color6 = this.decodeColor("control", 0.0F, 0.0F, 0.0F, 0);
   private Color color7 = this.decodeColor("nimbusBlueGrey", -0.006944418F, -0.07399663F, 0.11372548F, 0);
   private Color color8 = this.decodeColor("nimbusBase", 0.02551502F, -0.47885156F, -0.34901965F, 0);
   private Color color9 = new Color(255, 200, 0, 255);
   private Color color10 = this.decodeColor("nimbusBase", 0.004681647F, -0.6274498F, 0.39999998F, 0);
   private Color color11 = this.decodeColor("nimbusBase", 0.032459438F, -0.5934608F, 0.2862745F, 0);
   private Color color12 = new Color(204, 207, 213, 255);
   private Color color13 = this.decodeColor("nimbusBase", 0.032459438F, -0.55506915F, 0.18039215F, 0);
   private Color color14 = this.decodeColor("nimbusBase", 0.004681647F, -0.52792984F, 0.10588235F, 0);
   private Color color15 = this.decodeColor("nimbusBase", 0.03801495F, -0.4794643F, -0.04705882F, 0);
   private Color color16 = this.decodeColor("nimbusBase", 0.021348298F, -0.61416256F, 0.3607843F, 0);
   private Color color17 = this.decodeColor("nimbusBase", 0.032459438F, -0.5546332F, 0.17647058F, 0);
   private Color color18 = new Color(235, 236, 238, 255);
   private Object[] componentColors;

   public InternalFramePainter(AbstractRegionPainter.PaintContext var1, int var2) {
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
         this.paintBackgroundEnabledAndWindowFocused(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect1();
      var1.setPaint(this.color1);
      var1.fill(this.roundRect);
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient1(this.path));
      var1.fill(this.path);
      this.path = this.decodePath2();
      var1.setPaint(this.color3);
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.color4);
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.color5);
      var1.fill(this.path);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color6);
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color7);
      var1.fill(this.rect);
   }

   private void paintBackgroundEnabledAndWindowFocused(Graphics2D var1) {
      this.roundRect = this.decodeRoundRect2();
      var1.setPaint(this.color8);
      var1.fill(this.roundRect);
      this.path = this.decodePath5();
      var1.setPaint(this.color9);
      var1.fill(this.path);
      this.path = this.decodePath1();
      var1.setPaint(this.decodeGradient2(this.path));
      var1.fill(this.path);
      this.path = this.decodePath6();
      var1.setPaint(this.color12);
      var1.fill(this.path);
      this.path = this.decodePath7();
      var1.setPaint(this.color13);
      var1.fill(this.path);
      this.path = this.decodePath8();
      var1.setPaint(this.color14);
      var1.fill(this.path);
      this.path = this.decodePath9();
      var1.setPaint(this.color15);
      var1.fill(this.path);
      this.rect = this.decodeRect1();
      var1.setPaint(this.color6);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color9);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color9);
      var1.fill(this.rect);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color9);
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.decodeGradient3(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color18);
      var1.fill(this.rect);
   }

   private RoundRectangle2D decodeRoundRect1() {
      this.roundRect.setRoundRect((double)this.decodeX(0.0F), (double)this.decodeY(0.0F), (double)(this.decodeX(3.0F) - this.decodeX(0.0F)), (double)(this.decodeY(3.0F) - this.decodeY(0.0F)), 4.666666507720947D, 4.666666507720947D);
      return this.roundRect;
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.16666667F), (double)this.decodeY(0.12F));
      this.path.curveTo((double)this.decodeAnchorX(0.16666667F, 0.0F), (double)this.decodeAnchorY(0.12F, -1.0F), (double)this.decodeAnchorX(0.5F, -1.0F), (double)this.decodeAnchorY(0.04F, 0.0F), (double)this.decodeX(0.5F), (double)this.decodeY(0.04F));
      this.path.curveTo((double)this.decodeAnchorX(0.5F, 1.0F), (double)this.decodeAnchorY(0.04F, 0.0F), (double)this.decodeAnchorX(2.5F, -1.0F), (double)this.decodeAnchorY(0.04F, 0.0F), (double)this.decodeX(2.5F), (double)this.decodeY(0.04F));
      this.path.curveTo((double)this.decodeAnchorX(2.5F, 1.0F), (double)this.decodeAnchorY(0.04F, 0.0F), (double)this.decodeAnchorX(2.8333333F, 0.0F), (double)this.decodeAnchorY(0.12F, -1.0F), (double)this.decodeX(2.8333333F), (double)this.decodeY(0.12F));
      this.path.curveTo((double)this.decodeAnchorX(2.8333333F, 0.0F), (double)this.decodeAnchorY(0.12F, 1.0F), (double)this.decodeAnchorX(2.8333333F, 0.0F), (double)this.decodeAnchorY(0.96F, 0.0F), (double)this.decodeX(2.8333333F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(0.16666667F), (double)this.decodeY(0.96F));
      this.path.curveTo((double)this.decodeAnchorX(0.16666667F, 0.0F), (double)this.decodeAnchorY(0.96F, 0.0F), (double)this.decodeAnchorX(0.16666667F, 0.0F), (double)this.decodeAnchorY(0.12F, 1.0F), (double)this.decodeX(0.16666667F), (double)this.decodeY(0.12F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.6666667F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(0.16666667F), (double)this.decodeY(0.96F));
      this.path.curveTo((double)this.decodeAnchorX(0.16666667F, 0.0F), (double)this.decodeAnchorY(0.96F, 0.0F), (double)this.decodeAnchorX(0.16666667F, 0.0F), (double)this.decodeAnchorY(2.5F, -1.0F), (double)this.decodeX(0.16666667F), (double)this.decodeY(2.5F));
      this.path.curveTo((double)this.decodeAnchorX(0.16666667F, 0.0F), (double)this.decodeAnchorY(2.5F, 1.0F), (double)this.decodeAnchorX(0.5F, -1.0F), (double)this.decodeAnchorY(2.8333333F, 0.0F), (double)this.decodeX(0.5F), (double)this.decodeY(2.8333333F));
      this.path.curveTo((double)this.decodeAnchorX(0.5F, 1.0F), (double)this.decodeAnchorY(2.8333333F, 0.0F), (double)this.decodeAnchorX(2.5F, -1.0F), (double)this.decodeAnchorY(2.8333333F, 0.0F), (double)this.decodeX(2.5F), (double)this.decodeY(2.8333333F));
      this.path.curveTo((double)this.decodeAnchorX(2.5F, 1.0F), (double)this.decodeAnchorY(2.8333333F, 0.0F), (double)this.decodeAnchorX(2.8333333F, 0.0F), (double)this.decodeAnchorY(2.5F, 1.0F), (double)this.decodeX(2.8333333F), (double)this.decodeY(2.5F));
      this.path.curveTo((double)this.decodeAnchorX(2.8333333F, 0.0F), (double)this.decodeAnchorY(2.5F, -1.0F), (double)this.decodeAnchorX(2.8333333F, 0.0F), (double)this.decodeAnchorY(0.96F, 0.0F), (double)this.decodeX(2.8333333F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(2.3333333F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(2.3333333F), (double)this.decodeY(2.3333333F));
      this.path.lineTo((double)this.decodeX(0.6666667F), (double)this.decodeY(2.3333333F));
      this.path.lineTo((double)this.decodeX(0.6666667F), (double)this.decodeY(0.96F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath3() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.8333333F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(0.6666667F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(0.6666667F), (double)this.decodeY(2.3333333F));
      this.path.lineTo((double)this.decodeX(2.3333333F), (double)this.decodeY(2.3333333F));
      this.path.lineTo((double)this.decodeX(2.3333333F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(2.1666667F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(2.1666667F), (double)this.decodeY(2.1666667F));
      this.path.lineTo((double)this.decodeX(0.8333333F), (double)this.decodeY(2.1666667F));
      this.path.lineTo((double)this.decodeX(0.8333333F), (double)this.decodeY(0.96F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath4() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(2.1666667F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.1666667F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.1666667F), (double)this.decodeY(2.1666667F));
      this.path.lineTo((double)this.decodeX(0.8333333F), (double)this.decodeY(2.1666667F));
      this.path.lineTo((double)this.decodeX(0.8333333F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(2.1666667F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(2.1666667F), (double)this.decodeY(1.0F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(2.0F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect2() {
      this.rect.setRect((double)this.decodeX(0.33333334F), (double)this.decodeY(2.6666667F), (double)(this.decodeX(2.6666667F) - this.decodeX(0.33333334F)), (double)(this.decodeY(2.8333333F) - this.decodeY(2.6666667F)));
      return this.rect;
   }

   private RoundRectangle2D decodeRoundRect2() {
      this.roundRect.setRoundRect((double)this.decodeX(0.0F), (double)this.decodeY(0.0F), (double)(this.decodeX(3.0F) - this.decodeX(0.0F)), (double)(this.decodeY(3.0F) - this.decodeY(0.0F)), 4.833333492279053D, 4.833333492279053D);
      return this.roundRect;
   }

   private Path2D decodePath5() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.16666667F), (double)this.decodeY(0.08F));
      this.path.curveTo((double)this.decodeAnchorX(0.16666667F, 0.0F), (double)this.decodeAnchorY(0.08F, 1.0F), (double)this.decodeAnchorX(0.16666667F, 0.0F), (double)this.decodeAnchorY(0.08F, -1.0F), (double)this.decodeX(0.16666667F), (double)this.decodeY(0.08F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath6() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.5F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(0.16666667F), (double)this.decodeY(0.96F));
      this.path.curveTo((double)this.decodeAnchorX(0.16666667F, 0.0F), (double)this.decodeAnchorY(0.96F, 0.0F), (double)this.decodeAnchorX(0.16666667F, 0.0F), (double)this.decodeAnchorY(2.5F, -1.0F), (double)this.decodeX(0.16666667F), (double)this.decodeY(2.5F));
      this.path.curveTo((double)this.decodeAnchorX(0.16666667F, 0.0F), (double)this.decodeAnchorY(2.5F, 1.0F), (double)this.decodeAnchorX(0.5F, -1.0F), (double)this.decodeAnchorY(2.8333333F, 0.0F), (double)this.decodeX(0.5F), (double)this.decodeY(2.8333333F));
      this.path.curveTo((double)this.decodeAnchorX(0.5F, 1.0F), (double)this.decodeAnchorY(2.8333333F, 0.0F), (double)this.decodeAnchorX(2.5F, -1.0F), (double)this.decodeAnchorY(2.8333333F, 0.0F), (double)this.decodeX(2.5F), (double)this.decodeY(2.8333333F));
      this.path.curveTo((double)this.decodeAnchorX(2.5F, 1.0F), (double)this.decodeAnchorY(2.8333333F, 0.0F), (double)this.decodeAnchorX(2.8333333F, 0.0F), (double)this.decodeAnchorY(2.5F, 1.0F), (double)this.decodeX(2.8333333F), (double)this.decodeY(2.5F));
      this.path.curveTo((double)this.decodeAnchorX(2.8333333F, 0.0F), (double)this.decodeAnchorY(2.5F, -1.0F), (double)this.decodeAnchorX(2.8333333F, 0.0F), (double)this.decodeAnchorY(0.96F, 0.0F), (double)this.decodeX(2.8333333F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(2.5F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(2.5F), (double)this.decodeY(2.5F));
      this.path.lineTo((double)this.decodeX(0.5F), (double)this.decodeY(2.5F));
      this.path.lineTo((double)this.decodeX(0.5F), (double)this.decodeY(0.96F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath7() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.6666667F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(0.33333334F), (double)this.decodeY(0.96F));
      this.path.curveTo((double)this.decodeAnchorX(0.33333334F, 0.0F), (double)this.decodeAnchorY(0.96F, 0.0F), (double)this.decodeAnchorX(0.33333334F, 0.0F), (double)this.decodeAnchorY(2.3333333F, -1.0F), (double)this.decodeX(0.33333334F), (double)this.decodeY(2.3333333F));
      this.path.curveTo((double)this.decodeAnchorX(0.33333334F, 0.0F), (double)this.decodeAnchorY(2.3333333F, 1.0F), (double)this.decodeAnchorX(0.6666667F, -1.0F), (double)this.decodeAnchorY(2.6666667F, 0.0F), (double)this.decodeX(0.6666667F), (double)this.decodeY(2.6666667F));
      this.path.curveTo((double)this.decodeAnchorX(0.6666667F, 1.0F), (double)this.decodeAnchorY(2.6666667F, 0.0F), (double)this.decodeAnchorX(2.3333333F, -1.0F), (double)this.decodeAnchorY(2.6666667F, 0.0F), (double)this.decodeX(2.3333333F), (double)this.decodeY(2.6666667F));
      this.path.curveTo((double)this.decodeAnchorX(2.3333333F, 1.0F), (double)this.decodeAnchorY(2.6666667F, 0.0F), (double)this.decodeAnchorX(2.6666667F, 0.0F), (double)this.decodeAnchorY(2.3333333F, 1.0F), (double)this.decodeX(2.6666667F), (double)this.decodeY(2.3333333F));
      this.path.curveTo((double)this.decodeAnchorX(2.6666667F, 0.0F), (double)this.decodeAnchorY(2.3333333F, -1.0F), (double)this.decodeAnchorX(2.6666667F, 0.0F), (double)this.decodeAnchorY(0.96F, 0.0F), (double)this.decodeX(2.6666667F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(2.3333333F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(2.3333333F), (double)this.decodeY(2.3333333F));
      this.path.lineTo((double)this.decodeX(0.6666667F), (double)this.decodeY(2.3333333F));
      this.path.lineTo((double)this.decodeX(0.6666667F), (double)this.decodeY(0.96F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath8() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(2.3333333F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(2.1666667F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(2.1666667F), (double)this.decodeY(2.1666667F));
      this.path.lineTo((double)this.decodeX(0.8333333F), (double)this.decodeY(2.1666667F));
      this.path.lineTo((double)this.decodeX(0.8333333F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(0.6666667F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(0.6666667F), (double)this.decodeY(2.3333333F));
      this.path.lineTo((double)this.decodeX(2.3333333F), (double)this.decodeY(2.3333333F));
      this.path.lineTo((double)this.decodeX(2.3333333F), (double)this.decodeY(0.96F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath9() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.8333333F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(0.8333333F), (double)this.decodeY(2.1666667F));
      this.path.lineTo((double)this.decodeX(2.1666667F), (double)this.decodeY(2.1666667F));
      this.path.lineTo((double)this.decodeX(2.1666667F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(0.8333333F), (double)this.decodeY(0.96F));
      this.path.lineTo((double)this.decodeX(0.8333333F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(0.8333333F), (double)this.decodeY(1.0F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect3() {
      this.rect.setRect((double)this.decodeX(0.0F), (double)this.decodeY(0.0F), (double)(this.decodeX(0.0F) - this.decodeX(0.0F)), (double)(this.decodeY(0.0F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect4() {
      this.rect.setRect((double)this.decodeX(0.33333334F), (double)this.decodeY(0.08F), (double)(this.decodeX(2.6666667F) - this.decodeX(0.33333334F)), (double)(this.decodeY(0.96F) - this.decodeY(0.08F)));
      return this.rect;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.3203593F, 1.0F}, new Color[]{this.color2, this.decodeColor(this.color2, this.color3, 0.5F), this.color3});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color10, this.decodeColor(this.color10, this.color11, 0.5F), this.color11});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.24251497F, 1.0F}, new Color[]{this.color16, this.decodeColor(this.color16, this.color17, 0.5F), this.color17});
   }
}
