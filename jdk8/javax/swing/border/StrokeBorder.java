package javax.swing.border;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.beans.ConstructorProperties;

public class StrokeBorder extends AbstractBorder {
   private final BasicStroke stroke;
   private final Paint paint;

   public StrokeBorder(BasicStroke var1) {
      this(var1, (Paint)null);
   }

   @ConstructorProperties({"stroke", "paint"})
   public StrokeBorder(BasicStroke var1, Paint var2) {
      if (var1 == null) {
         throw new NullPointerException("border's stroke");
      } else {
         this.stroke = var1;
         this.paint = var2;
      }
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      float var7 = this.stroke.getLineWidth();
      if (var7 > 0.0F) {
         var2 = var2.create();
         if (var2 instanceof Graphics2D) {
            Graphics2D var8 = (Graphics2D)var2;
            var8.setStroke(this.stroke);
            var8.setPaint((Paint)(this.paint != null ? this.paint : (var1 == null ? null : var1.getForeground())));
            var8.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            var8.draw(new Rectangle2D.Float((float)var3 + var7 / 2.0F, (float)var4 + var7 / 2.0F, (float)var5 - var7, (float)var6 - var7));
         }

         var2.dispose();
      }

   }

   public Insets getBorderInsets(Component var1, Insets var2) {
      int var3 = (int)Math.ceil((double)this.stroke.getLineWidth());
      var2.set(var3, var3, var3, var3);
      return var2;
   }

   public BasicStroke getStroke() {
      return this.stroke;
   }

   public Paint getPaint() {
      return this.paint;
   }
}
