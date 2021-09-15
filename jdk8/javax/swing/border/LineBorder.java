package javax.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.ConstructorProperties;

public class LineBorder extends AbstractBorder {
   private static Border blackLine;
   private static Border grayLine;
   protected int thickness;
   protected Color lineColor;
   protected boolean roundedCorners;

   public static Border createBlackLineBorder() {
      if (blackLine == null) {
         blackLine = new LineBorder(Color.black, 1);
      }

      return blackLine;
   }

   public static Border createGrayLineBorder() {
      if (grayLine == null) {
         grayLine = new LineBorder(Color.gray, 1);
      }

      return grayLine;
   }

   public LineBorder(Color var1) {
      this(var1, 1, false);
   }

   public LineBorder(Color var1, int var2) {
      this(var1, var2, false);
   }

   @ConstructorProperties({"lineColor", "thickness", "roundedCorners"})
   public LineBorder(Color var1, int var2, boolean var3) {
      this.lineColor = var1;
      this.thickness = var2;
      this.roundedCorners = var3;
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      if (this.thickness > 0 && var2 instanceof Graphics2D) {
         Graphics2D var7 = (Graphics2D)var2;
         Color var8 = var7.getColor();
         var7.setColor(this.lineColor);
         int var11 = this.thickness;
         int var12 = var11 + var11;
         Object var9;
         Object var10;
         if (this.roundedCorners) {
            float var13 = 0.2F * (float)var11;
            var9 = new RoundRectangle2D.Float((float)var3, (float)var4, (float)var5, (float)var6, (float)var11, (float)var11);
            var10 = new RoundRectangle2D.Float((float)(var3 + var11), (float)(var4 + var11), (float)(var5 - var12), (float)(var6 - var12), var13, var13);
         } else {
            var9 = new Rectangle2D.Float((float)var3, (float)var4, (float)var5, (float)var6);
            var10 = new Rectangle2D.Float((float)(var3 + var11), (float)(var4 + var11), (float)(var5 - var12), (float)(var6 - var12));
         }

         Path2D.Float var14 = new Path2D.Float(0);
         var14.append((Shape)var9, false);
         var14.append((Shape)var10, false);
         var7.fill(var14);
         var7.setColor(var8);
      }

   }

   public Insets getBorderInsets(Component var1, Insets var2) {
      var2.set(this.thickness, this.thickness, this.thickness, this.thickness);
      return var2;
   }

   public Color getLineColor() {
      return this.lineColor;
   }

   public int getThickness() {
      return this.thickness;
   }

   public boolean getRoundedCorners() {
      return this.roundedCorners;
   }

   public boolean isBorderOpaque() {
      return !this.roundedCorners;
   }
}
