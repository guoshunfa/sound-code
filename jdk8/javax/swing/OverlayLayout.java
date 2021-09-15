package javax.swing;

import java.awt.AWTError;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.beans.ConstructorProperties;
import java.io.Serializable;

public class OverlayLayout implements LayoutManager2, Serializable {
   private Container target;
   private SizeRequirements[] xChildren;
   private SizeRequirements[] yChildren;
   private SizeRequirements xTotal;
   private SizeRequirements yTotal;

   @ConstructorProperties({"target"})
   public OverlayLayout(Container var1) {
      this.target = var1;
   }

   public final Container getTarget() {
      return this.target;
   }

   public void invalidateLayout(Container var1) {
      this.checkContainer(var1);
      this.xChildren = null;
      this.yChildren = null;
      this.xTotal = null;
      this.yTotal = null;
   }

   public void addLayoutComponent(String var1, Component var2) {
      this.invalidateLayout(var2.getParent());
   }

   public void removeLayoutComponent(Component var1) {
      this.invalidateLayout(var1.getParent());
   }

   public void addLayoutComponent(Component var1, Object var2) {
      this.invalidateLayout(var1.getParent());
   }

   public Dimension preferredLayoutSize(Container var1) {
      this.checkContainer(var1);
      this.checkRequests();
      Dimension var2 = new Dimension(this.xTotal.preferred, this.yTotal.preferred);
      Insets var3 = var1.getInsets();
      var2.width += var3.left + var3.right;
      var2.height += var3.top + var3.bottom;
      return var2;
   }

   public Dimension minimumLayoutSize(Container var1) {
      this.checkContainer(var1);
      this.checkRequests();
      Dimension var2 = new Dimension(this.xTotal.minimum, this.yTotal.minimum);
      Insets var3 = var1.getInsets();
      var2.width += var3.left + var3.right;
      var2.height += var3.top + var3.bottom;
      return var2;
   }

   public Dimension maximumLayoutSize(Container var1) {
      this.checkContainer(var1);
      this.checkRequests();
      Dimension var2 = new Dimension(this.xTotal.maximum, this.yTotal.maximum);
      Insets var3 = var1.getInsets();
      var2.width += var3.left + var3.right;
      var2.height += var3.top + var3.bottom;
      return var2;
   }

   public float getLayoutAlignmentX(Container var1) {
      this.checkContainer(var1);
      this.checkRequests();
      return this.xTotal.alignment;
   }

   public float getLayoutAlignmentY(Container var1) {
      this.checkContainer(var1);
      this.checkRequests();
      return this.yTotal.alignment;
   }

   public void layoutContainer(Container var1) {
      this.checkContainer(var1);
      this.checkRequests();
      int var2 = var1.getComponentCount();
      int[] var3 = new int[var2];
      int[] var4 = new int[var2];
      int[] var5 = new int[var2];
      int[] var6 = new int[var2];
      Dimension var7 = var1.getSize();
      Insets var8 = var1.getInsets();
      var7.width -= var8.left + var8.right;
      var7.height -= var8.top + var8.bottom;
      SizeRequirements.calculateAlignedPositions(var7.width, this.xTotal, this.xChildren, var3, var4);
      SizeRequirements.calculateAlignedPositions(var7.height, this.yTotal, this.yChildren, var5, var6);

      for(int var9 = 0; var9 < var2; ++var9) {
         Component var10 = var1.getComponent(var9);
         var10.setBounds(var8.left + var3[var9], var8.top + var5[var9], var4[var9], var6[var9]);
      }

   }

   void checkContainer(Container var1) {
      if (this.target != var1) {
         throw new AWTError("OverlayLayout can't be shared");
      }
   }

   void checkRequests() {
      if (this.xChildren == null || this.yChildren == null) {
         int var1 = this.target.getComponentCount();
         this.xChildren = new SizeRequirements[var1];
         this.yChildren = new SizeRequirements[var1];

         for(int var2 = 0; var2 < var1; ++var2) {
            Component var3 = this.target.getComponent(var2);
            Dimension var4 = var3.getMinimumSize();
            Dimension var5 = var3.getPreferredSize();
            Dimension var6 = var3.getMaximumSize();
            this.xChildren[var2] = new SizeRequirements(var4.width, var5.width, var6.width, var3.getAlignmentX());
            this.yChildren[var2] = new SizeRequirements(var4.height, var5.height, var6.height, var3.getAlignmentY());
         }

         this.xTotal = SizeRequirements.getAlignedSizeRequirements(this.xChildren);
         this.yTotal = SizeRequirements.getAlignedSizeRequirements(this.yChildren);
      }

   }
}
