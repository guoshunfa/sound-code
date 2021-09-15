package javax.swing;

import java.awt.AWTError;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.beans.ConstructorProperties;
import java.io.PrintStream;
import java.io.Serializable;

public class BoxLayout implements LayoutManager2, Serializable {
   public static final int X_AXIS = 0;
   public static final int Y_AXIS = 1;
   public static final int LINE_AXIS = 2;
   public static final int PAGE_AXIS = 3;
   private int axis;
   private Container target;
   private transient SizeRequirements[] xChildren;
   private transient SizeRequirements[] yChildren;
   private transient SizeRequirements xTotal;
   private transient SizeRequirements yTotal;
   private transient PrintStream dbg;

   @ConstructorProperties({"target", "axis"})
   public BoxLayout(Container var1, int var2) {
      if (var2 != 0 && var2 != 1 && var2 != 2 && var2 != 3) {
         throw new AWTError("Invalid axis");
      } else {
         this.axis = var2;
         this.target = var1;
      }
   }

   BoxLayout(Container var1, int var2, PrintStream var3) {
      this(var1, var2);
      this.dbg = var3;
   }

   public final Container getTarget() {
      return this.target;
   }

   public final int getAxis() {
      return this.axis;
   }

   public synchronized void invalidateLayout(Container var1) {
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
      Dimension var2;
      synchronized(this) {
         this.checkContainer(var1);
         this.checkRequests();
         var2 = new Dimension(this.xTotal.preferred, this.yTotal.preferred);
      }

      Insets var3 = var1.getInsets();
      var2.width = (int)Math.min((long)var2.width + (long)var3.left + (long)var3.right, 2147483647L);
      var2.height = (int)Math.min((long)var2.height + (long)var3.top + (long)var3.bottom, 2147483647L);
      return var2;
   }

   public Dimension minimumLayoutSize(Container var1) {
      Dimension var2;
      synchronized(this) {
         this.checkContainer(var1);
         this.checkRequests();
         var2 = new Dimension(this.xTotal.minimum, this.yTotal.minimum);
      }

      Insets var3 = var1.getInsets();
      var2.width = (int)Math.min((long)var2.width + (long)var3.left + (long)var3.right, 2147483647L);
      var2.height = (int)Math.min((long)var2.height + (long)var3.top + (long)var3.bottom, 2147483647L);
      return var2;
   }

   public Dimension maximumLayoutSize(Container var1) {
      Dimension var2;
      synchronized(this) {
         this.checkContainer(var1);
         this.checkRequests();
         var2 = new Dimension(this.xTotal.maximum, this.yTotal.maximum);
      }

      Insets var3 = var1.getInsets();
      var2.width = (int)Math.min((long)var2.width + (long)var3.left + (long)var3.right, 2147483647L);
      var2.height = (int)Math.min((long)var2.height + (long)var3.top + (long)var3.bottom, 2147483647L);
      return var2;
   }

   public synchronized float getLayoutAlignmentX(Container var1) {
      this.checkContainer(var1);
      this.checkRequests();
      return this.xTotal.alignment;
   }

   public synchronized float getLayoutAlignmentY(Container var1) {
      this.checkContainer(var1);
      this.checkRequests();
      return this.yTotal.alignment;
   }

   public void layoutContainer(Container var1) {
      this.checkContainer(var1);
      int var2 = var1.getComponentCount();
      int[] var3 = new int[var2];
      int[] var4 = new int[var2];
      int[] var5 = new int[var2];
      int[] var6 = new int[var2];
      Dimension var7 = var1.getSize();
      Insets var8 = var1.getInsets();
      var7.width -= var8.left + var8.right;
      var7.height -= var8.top + var8.bottom;
      ComponentOrientation var9 = var1.getComponentOrientation();
      int var10 = this.resolveAxis(this.axis, var9);
      boolean var11 = var10 != this.axis ? var9.isLeftToRight() : true;
      synchronized(this) {
         this.checkRequests();
         if (var10 == 0) {
            SizeRequirements.calculateTiledPositions(var7.width, this.xTotal, this.xChildren, var3, var4, var11);
            SizeRequirements.calculateAlignedPositions(var7.height, this.yTotal, this.yChildren, var5, var6);
         } else {
            SizeRequirements.calculateAlignedPositions(var7.width, this.xTotal, this.xChildren, var3, var4, var11);
            SizeRequirements.calculateTiledPositions(var7.height, this.yTotal, this.yChildren, var5, var6);
         }
      }

      int var12;
      Component var13;
      for(var12 = 0; var12 < var2; ++var12) {
         var13 = var1.getComponent(var12);
         var13.setBounds((int)Math.min((long)var8.left + (long)var3[var12], 2147483647L), (int)Math.min((long)var8.top + (long)var5[var12], 2147483647L), var4[var12], var6[var12]);
      }

      if (this.dbg != null) {
         for(var12 = 0; var12 < var2; ++var12) {
            var13 = var1.getComponent(var12);
            this.dbg.println(var13.toString());
            this.dbg.println("X: " + this.xChildren[var12]);
            this.dbg.println("Y: " + this.yChildren[var12]);
         }
      }

   }

   void checkContainer(Container var1) {
      if (this.target != var1) {
         throw new AWTError("BoxLayout can't be shared");
      }
   }

   void checkRequests() {
      if (this.xChildren == null || this.yChildren == null) {
         int var1 = this.target.getComponentCount();
         this.xChildren = new SizeRequirements[var1];
         this.yChildren = new SizeRequirements[var1];

         int var2;
         for(var2 = 0; var2 < var1; ++var2) {
            Component var3 = this.target.getComponent(var2);
            if (!var3.isVisible()) {
               this.xChildren[var2] = new SizeRequirements(0, 0, 0, var3.getAlignmentX());
               this.yChildren[var2] = new SizeRequirements(0, 0, 0, var3.getAlignmentY());
            } else {
               Dimension var4 = var3.getMinimumSize();
               Dimension var5 = var3.getPreferredSize();
               Dimension var6 = var3.getMaximumSize();
               this.xChildren[var2] = new SizeRequirements(var4.width, var5.width, var6.width, var3.getAlignmentX());
               this.yChildren[var2] = new SizeRequirements(var4.height, var5.height, var6.height, var3.getAlignmentY());
            }
         }

         var2 = this.resolveAxis(this.axis, this.target.getComponentOrientation());
         if (var2 == 0) {
            this.xTotal = SizeRequirements.getTiledSizeRequirements(this.xChildren);
            this.yTotal = SizeRequirements.getAlignedSizeRequirements(this.yChildren);
         } else {
            this.xTotal = SizeRequirements.getAlignedSizeRequirements(this.xChildren);
            this.yTotal = SizeRequirements.getTiledSizeRequirements(this.yChildren);
         }
      }

   }

   private int resolveAxis(int var1, ComponentOrientation var2) {
      int var3;
      if (var1 == 2) {
         var3 = var2.isHorizontal() ? 0 : 1;
      } else if (var1 == 3) {
         var3 = var2.isHorizontal() ? 1 : 0;
      } else {
         var3 = var1;
      }

      return var3;
   }
}
