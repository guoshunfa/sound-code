package javax.swing.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;
import java.io.Serializable;

public class EmptyBorder extends AbstractBorder implements Serializable {
   protected int left;
   protected int right;
   protected int top;
   protected int bottom;

   public EmptyBorder(int var1, int var2, int var3, int var4) {
      this.top = var1;
      this.right = var4;
      this.bottom = var3;
      this.left = var2;
   }

   @ConstructorProperties({"borderInsets"})
   public EmptyBorder(Insets var1) {
      this.top = var1.top;
      this.right = var1.right;
      this.bottom = var1.bottom;
      this.left = var1.left;
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
   }

   public Insets getBorderInsets(Component var1, Insets var2) {
      var2.left = this.left;
      var2.top = this.top;
      var2.right = this.right;
      var2.bottom = this.bottom;
      return var2;
   }

   public Insets getBorderInsets() {
      return new Insets(this.top, this.left, this.bottom, this.right);
   }

   public boolean isBorderOpaque() {
      return false;
   }
}
