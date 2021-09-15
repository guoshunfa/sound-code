package javax.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Icon;

public class MatteBorder extends EmptyBorder {
   protected Color color;
   protected Icon tileIcon;

   public MatteBorder(int var1, int var2, int var3, int var4, Color var5) {
      super(var1, var2, var3, var4);
      this.color = var5;
   }

   public MatteBorder(Insets var1, Color var2) {
      super(var1);
      this.color = var2;
   }

   public MatteBorder(int var1, int var2, int var3, int var4, Icon var5) {
      super(var1, var2, var3, var4);
      this.tileIcon = var5;
   }

   public MatteBorder(Insets var1, Icon var2) {
      super(var1);
      this.tileIcon = var2;
   }

   public MatteBorder(Icon var1) {
      this(-1, -1, -1, -1, (Icon)var1);
   }

   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Insets var7 = this.getBorderInsets(var1);
      Color var8 = var2.getColor();
      var2.translate(var3, var4);
      if (this.tileIcon != null) {
         this.color = this.tileIcon.getIconWidth() == -1 ? Color.gray : null;
      }

      if (this.color != null) {
         var2.setColor(this.color);
         var2.fillRect(0, 0, var5 - var7.right, var7.top);
         var2.fillRect(0, var7.top, var7.left, var6 - var7.top);
         var2.fillRect(var7.left, var6 - var7.bottom, var5 - var7.left, var7.bottom);
         var2.fillRect(var5 - var7.right, 0, var7.right, var6 - var7.bottom);
      } else if (this.tileIcon != null) {
         int var9 = this.tileIcon.getIconWidth();
         int var10 = this.tileIcon.getIconHeight();
         this.paintEdge(var1, var2, 0, 0, var5 - var7.right, var7.top, var9, var10);
         this.paintEdge(var1, var2, 0, var7.top, var7.left, var6 - var7.top, var9, var10);
         this.paintEdge(var1, var2, var7.left, var6 - var7.bottom, var5 - var7.left, var7.bottom, var9, var10);
         this.paintEdge(var1, var2, var5 - var7.right, 0, var7.right, var6 - var7.bottom, var9, var10);
      }

      var2.translate(-var3, -var4);
      var2.setColor(var8);
   }

   private void paintEdge(Component var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      var2 = var2.create(var3, var4, var5, var6);
      int var9 = -(var4 % var8);

      for(var3 = -(var3 % var7); var3 < var5; var3 += var7) {
         for(var4 = var9; var4 < var6; var4 += var8) {
            this.tileIcon.paintIcon(var1, var2, var3, var4);
         }
      }

      var2.dispose();
   }

   public Insets getBorderInsets(Component var1, Insets var2) {
      return this.computeInsets(var2);
   }

   public Insets getBorderInsets() {
      return this.computeInsets(new Insets(0, 0, 0, 0));
   }

   private Insets computeInsets(Insets var1) {
      if (this.tileIcon != null && this.top == -1 && this.bottom == -1 && this.left == -1 && this.right == -1) {
         int var2 = this.tileIcon.getIconWidth();
         int var3 = this.tileIcon.getIconHeight();
         var1.top = var3;
         var1.right = var2;
         var1.bottom = var3;
         var1.left = var2;
      } else {
         var1.left = this.left;
         var1.top = this.top;
         var1.right = this.right;
         var1.bottom = this.bottom;
      }

      return var1;
   }

   public Color getMatteColor() {
      return this.color;
   }

   public Icon getTileIcon() {
      return this.tileIcon;
   }

   public boolean isBorderOpaque() {
      return this.color != null;
   }
}
