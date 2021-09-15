package javax.swing.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.Serializable;

public abstract class AbstractBorder implements Border, Serializable {
   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
   }

   public Insets getBorderInsets(Component var1) {
      return this.getBorderInsets(var1, new Insets(0, 0, 0, 0));
   }

   public Insets getBorderInsets(Component var1, Insets var2) {
      var2.left = var2.top = var2.right = var2.bottom = 0;
      return var2;
   }

   public boolean isBorderOpaque() {
      return false;
   }

   public Rectangle getInteriorRectangle(Component var1, int var2, int var3, int var4, int var5) {
      return getInteriorRectangle(var1, this, var2, var3, var4, var5);
   }

   public static Rectangle getInteriorRectangle(Component var0, Border var1, int var2, int var3, int var4, int var5) {
      Insets var6;
      if (var1 != null) {
         var6 = var1.getBorderInsets(var0);
      } else {
         var6 = new Insets(0, 0, 0, 0);
      }

      return new Rectangle(var2 + var6.left, var3 + var6.top, var4 - var6.right - var6.left, var5 - var6.top - var6.bottom);
   }

   public int getBaseline(Component var1, int var2, int var3) {
      if (var2 >= 0 && var3 >= 0) {
         return -1;
      } else {
         throw new IllegalArgumentException("Width and height must be >= 0");
      }
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(Component var1) {
      if (var1 == null) {
         throw new NullPointerException("Component must be non-null");
      } else {
         return Component.BaselineResizeBehavior.OTHER;
      }
   }

   static boolean isLeftToRight(Component var0) {
      return var0.getComponentOrientation().isLeftToRight();
   }
}
