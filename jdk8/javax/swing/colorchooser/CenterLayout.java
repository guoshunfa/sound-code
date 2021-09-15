package javax.swing.colorchooser;

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
         var3.width += var4.left + var4.right;
         var3.height += var4.top + var4.bottom;
         return var3;
      } else {
         return new Dimension(0, 0);
      }
   }

   public Dimension minimumLayoutSize(Container var1) {
      return this.preferredLayoutSize(var1);
   }

   public void layoutContainer(Container var1) {
      try {
         Component var2 = var1.getComponent(0);
         var2.setSize(var2.getPreferredSize());
         Dimension var3 = var2.getSize();
         Dimension var4 = var1.getSize();
         Insets var5 = var1.getInsets();
         var4.width -= var5.left + var5.right;
         var4.height -= var5.top + var5.bottom;
         int var6 = var4.width / 2 - var3.width / 2;
         int var7 = var4.height / 2 - var3.height / 2;
         var6 += var5.left;
         var7 += var5.top;
         var2.setBounds(var6, var7, var3.width, var3.height);
      } catch (Exception var8) {
      }

   }
}
