package javax.swing.colorchooser;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;

class SmartGridLayout implements LayoutManager, Serializable {
   int rows = 2;
   int columns = 2;
   int xGap = 2;
   int yGap = 2;
   int componentCount = 0;
   Component[][] layoutGrid;

   public SmartGridLayout(int var1, int var2) {
      this.rows = var2;
      this.columns = var1;
      this.layoutGrid = new Component[var1][var2];
   }

   public void layoutContainer(Container var1) {
      this.buildLayoutGrid(var1);
      int[] var2 = new int[this.rows];
      int[] var3 = new int[this.columns];

      int var4;
      for(var4 = 0; var4 < this.rows; ++var4) {
         var2[var4] = this.computeRowHeight(var4);
      }

      for(var4 = 0; var4 < this.columns; ++var4) {
         var3[var4] = this.computeColumnWidth(var4);
      }

      Insets var10 = var1.getInsets();
      int var5;
      int var6;
      int var7;
      int var8;
      Component var9;
      if (var1.getComponentOrientation().isLeftToRight()) {
         var5 = var10.left;

         for(var6 = 0; var6 < this.columns; ++var6) {
            var7 = var10.top;

            for(var8 = 0; var8 < this.rows; ++var8) {
               var9 = this.layoutGrid[var6][var8];
               var9.setBounds(var5, var7, var3[var6], var2[var8]);
               var7 += var2[var8] + this.yGap;
            }

            var5 += var3[var6] + this.xGap;
         }
      } else {
         var5 = var1.getWidth() - var10.right;

         for(var6 = 0; var6 < this.columns; ++var6) {
            var7 = var10.top;
            var5 -= var3[var6];

            for(var8 = 0; var8 < this.rows; ++var8) {
               var9 = this.layoutGrid[var6][var8];
               var9.setBounds(var5, var7, var3[var6], var2[var8]);
               var7 += var2[var8] + this.yGap;
            }

            var5 -= this.xGap;
         }
      }

   }

   public Dimension minimumLayoutSize(Container var1) {
      this.buildLayoutGrid(var1);
      Insets var2 = var1.getInsets();
      int var3 = 0;
      int var4 = 0;

      int var5;
      for(var5 = 0; var5 < this.rows; ++var5) {
         var3 += this.computeRowHeight(var5);
      }

      for(var5 = 0; var5 < this.columns; ++var5) {
         var4 += this.computeColumnWidth(var5);
      }

      var3 += this.yGap * (this.rows - 1) + var2.top + var2.bottom;
      var4 += this.xGap * (this.columns - 1) + var2.right + var2.left;
      return new Dimension(var4, var3);
   }

   public Dimension preferredLayoutSize(Container var1) {
      return this.minimumLayoutSize(var1);
   }

   public void addLayoutComponent(String var1, Component var2) {
   }

   public void removeLayoutComponent(Component var1) {
   }

   private void buildLayoutGrid(Container var1) {
      Component[] var2 = var1.getComponents();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         int var4 = 0;
         int var5 = 0;
         if (var3 != 0) {
            var5 = var3 % this.columns;
            var4 = (var3 - var5) / this.columns;
         }

         this.layoutGrid[var5][var4] = var2[var3];
      }

   }

   private int computeColumnWidth(int var1) {
      int var2 = 1;

      for(int var3 = 0; var3 < this.rows; ++var3) {
         int var4 = this.layoutGrid[var1][var3].getPreferredSize().width;
         if (var4 > var2) {
            var2 = var4;
         }
      }

      return var2;
   }

   private int computeRowHeight(int var1) {
      int var2 = 1;

      for(int var3 = 0; var3 < this.columns; ++var3) {
         int var4 = this.layoutGrid[var3][var1].getPreferredSize().height;
         if (var4 > var2) {
            var2 = var4;
         }
      }

      return var2;
   }
}
