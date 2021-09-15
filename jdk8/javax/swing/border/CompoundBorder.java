package javax.swing.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;

public class CompoundBorder extends AbstractBorder {
   protected Border outsideBorder;
   protected Border insideBorder;

   public CompoundBorder() {
      this.outsideBorder = null;
      this.insideBorder = null;
   }

   @ConstructorProperties({"outsideBorder", "insideBorder"})
   public CompoundBorder(Border var1, Border var2) {
      this.outsideBorder = var1;
      this.insideBorder = var2;
   }

   public boolean isBorderOpaque() {
      return (this.outsideBorder == null || this.outsideBorder.isBorderOpaque()) && (this.insideBorder == null || this.insideBorder.isBorderOpaque());
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      int var8 = var3;
      int var9 = var4;
      int var10 = var5;
      int var11 = var6;
      if (this.outsideBorder != null) {
         this.outsideBorder.paintBorder(var1, var2, var3, var4, var5, var6);
         Insets var7 = this.outsideBorder.getBorderInsets(var1);
         var8 = var3 + var7.left;
         var9 = var4 + var7.top;
         var10 = var5 - var7.right - var7.left;
         var11 = var6 - var7.bottom - var7.top;
      }

      if (this.insideBorder != null) {
         this.insideBorder.paintBorder(var1, var2, var8, var9, var10, var11);
      }

   }

   public Insets getBorderInsets(Component var1, Insets var2) {
      var2.top = var2.left = var2.right = var2.bottom = 0;
      Insets var3;
      if (this.outsideBorder != null) {
         var3 = this.outsideBorder.getBorderInsets(var1);
         var2.top += var3.top;
         var2.left += var3.left;
         var2.right += var3.right;
         var2.bottom += var3.bottom;
      }

      if (this.insideBorder != null) {
         var3 = this.insideBorder.getBorderInsets(var1);
         var2.top += var3.top;
         var2.left += var3.left;
         var2.right += var3.right;
         var2.bottom += var3.bottom;
      }

      return var2;
   }

   public Border getOutsideBorder() {
      return this.outsideBorder;
   }

   public Border getInsideBorder() {
      return this.insideBorder;
   }
}
