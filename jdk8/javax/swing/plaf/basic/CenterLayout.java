package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;

class CenterLayout implements LayoutManager, Serializable {
   public void addLayoutComponent(String var1, Component var2) {
   }

   public void removeLayoutComponent(Component var1) {
   }

   public Dimension preferredLayoutSize(Container var1) {
      Component var2 = var1.getComponent(0);
      if (var2 != null) {
         Dimension var3 = var2.getPreferredSize();
         Insets var4 = var1.getInsets();
         return new Dimension(var3.width + var4.left + var4.right, var3.height + var4.top + var4.bottom);
      } else {
         return new Dimension(0, 0);
      }
   }

   public Dimension minimumLayoutSize(Container var1) {
      return this.preferredLayoutSize(var1);
   }

   public void layoutContainer(Container var1) {
      if (var1.getComponentCount() > 0) {
         Component var2 = var1.getComponent(0);
         Dimension var3 = var2.getPreferredSize();
         int var4 = var1.getWidth();
         int var5 = var1.getHeight();
         Insets var6 = var1.getInsets();
         var4 -= var6.left + var6.right;
         var5 -= var6.top + var6.bottom;
         int var7 = (var4 - var3.width) / 2 + var6.left;
         int var8 = (var5 - var3.height) / 2 + var6.top;
         var2.setBounds(var7, var8, var3.width, var3.height);
      }

   }
}
