package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.border.Border;

class LoweredBorder extends AbstractRegionPainter implements Border {
   private static final int IMG_SIZE = 30;
   private static final int RADIUS = 13;
   private static final Insets INSETS = new Insets(10, 10, 10, 10);
   private static final AbstractRegionPainter.PaintContext PAINT_CONTEXT;

   protected Object[] getExtendedCacheKeys(JComponent var1) {
      return var1 != null ? new Object[]{var1.getBackground()} : null;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      Color var6 = var2 == null ? Color.BLACK : var2.getBackground();
      BufferedImage var7 = new BufferedImage(30, 30, 2);
      BufferedImage var8 = new BufferedImage(30, 30, 2);
      Graphics2D var9 = (Graphics2D)var7.getGraphics();
      var9.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      var9.setColor(var6);
      var9.fillRoundRect(2, 0, 26, 26, 13, 13);
      var9.dispose();
      InnerShadowEffect var10 = new InnerShadowEffect();
      var10.setDistance(1);
      var10.setSize(3);
      var10.setColor(this.getLighter(var6, 2.1F));
      var10.setAngle(90);
      var10.applyEffect(var7, var8, 30, 30);
      var9 = (Graphics2D)var8.getGraphics();
      var9.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      var9.setClip(0, 28, 30, 1);
      var9.setColor(this.getLighter(var6, 0.9F));
      var9.drawRoundRect(2, 1, 25, 25, 13, 13);
      var9.dispose();
      if (var3 == 30 && var4 == 30) {
         var1.drawImage(var8, 0, 0, var2);
      } else {
         ImageScalingHelper.paint(var1, 0, 0, var3, var4, var8, INSETS, INSETS, ImageScalingHelper.PaintType.PAINT9_STRETCH, 512);
      }

      var7 = null;
      var8 = null;
   }

   protected AbstractRegionPainter.PaintContext getPaintContext() {
      return PAINT_CONTEXT;
   }

   public Insets getBorderInsets(Component var1) {
      return (Insets)INSETS.clone();
   }

   public boolean isBorderOpaque() {
      return false;
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      JComponent var7 = var1 instanceof JComponent ? (JComponent)var1 : null;
      if (var2 instanceof Graphics2D) {
         Graphics2D var8 = (Graphics2D)var2;
         var8.translate(var3, var4);
         this.paint(var8, var7, var5, var6);
         var8.translate(-var3, -var4);
      } else {
         BufferedImage var10 = new BufferedImage(30, 30, 2);
         Graphics2D var9 = (Graphics2D)var10.getGraphics();
         this.paint(var9, var7, var5, var6);
         var9.dispose();
         ImageScalingHelper.paint(var2, var3, var4, var5, var6, var10, INSETS, INSETS, ImageScalingHelper.PaintType.PAINT9_STRETCH, 512);
      }

   }

   private Color getLighter(Color var1, float var2) {
      return new Color(Math.min((int)((float)var1.getRed() / var2), 255), Math.min((int)((float)var1.getGreen() / var2), 255), Math.min((int)((float)var1.getBlue() / var2), 255));
   }

   static {
      PAINT_CONTEXT = new AbstractRegionPainter.PaintContext(INSETS, new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.147483647E9D, 2.147483647E9D);
   }
}
