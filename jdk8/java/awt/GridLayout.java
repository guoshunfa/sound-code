package java.awt;

import java.io.Serializable;

public class GridLayout implements LayoutManager, Serializable {
   private static final long serialVersionUID = -7411804673224730901L;
   int hgap;
   int vgap;
   int rows;
   int cols;

   public GridLayout() {
      this(1, 0, 0, 0);
   }

   public GridLayout(int var1, int var2) {
      this(var1, var2, 0, 0);
   }

   public GridLayout(int var1, int var2, int var3, int var4) {
      if (var1 == 0 && var2 == 0) {
         throw new IllegalArgumentException("rows and cols cannot both be zero");
      } else {
         this.rows = var1;
         this.cols = var2;
         this.hgap = var3;
         this.vgap = var4;
      }
   }

   public int getRows() {
      return this.rows;
   }

   public void setRows(int var1) {
      if (var1 == 0 && this.cols == 0) {
         throw new IllegalArgumentException("rows and cols cannot both be zero");
      } else {
         this.rows = var1;
      }
   }

   public int getColumns() {
      return this.cols;
   }

   public void setColumns(int var1) {
      if (var1 == 0 && this.rows == 0) {
         throw new IllegalArgumentException("rows and cols cannot both be zero");
      } else {
         this.cols = var1;
      }
   }

   public int getHgap() {
      return this.hgap;
   }

   public void setHgap(int var1) {
      this.hgap = var1;
   }

   public int getVgap() {
      return this.vgap;
   }

   public void setVgap(int var1) {
      this.vgap = var1;
   }

   public void addLayoutComponent(String var1, Component var2) {
   }

   public void removeLayoutComponent(Component var1) {
   }

   public Dimension preferredLayoutSize(Container var1) {
      synchronized(var1.getTreeLock()) {
         Insets var3 = var1.getInsets();
         int var4 = var1.getComponentCount();
         int var5 = this.rows;
         int var6 = this.cols;
         if (var5 > 0) {
            var6 = (var4 + var5 - 1) / var5;
         } else {
            var5 = (var4 + var6 - 1) / var6;
         }

         int var7 = 0;
         int var8 = 0;

         for(int var9 = 0; var9 < var4; ++var9) {
            Component var10 = var1.getComponent(var9);
            Dimension var11 = var10.getPreferredSize();
            if (var7 < var11.width) {
               var7 = var11.width;
            }

            if (var8 < var11.height) {
               var8 = var11.height;
            }
         }

         return new Dimension(var3.left + var3.right + var6 * var7 + (var6 - 1) * this.hgap, var3.top + var3.bottom + var5 * var8 + (var5 - 1) * this.vgap);
      }
   }

   public Dimension minimumLayoutSize(Container var1) {
      synchronized(var1.getTreeLock()) {
         Insets var3 = var1.getInsets();
         int var4 = var1.getComponentCount();
         int var5 = this.rows;
         int var6 = this.cols;
         if (var5 > 0) {
            var6 = (var4 + var5 - 1) / var5;
         } else {
            var5 = (var4 + var6 - 1) / var6;
         }

         int var7 = 0;
         int var8 = 0;

         for(int var9 = 0; var9 < var4; ++var9) {
            Component var10 = var1.getComponent(var9);
            Dimension var11 = var10.getMinimumSize();
            if (var7 < var11.width) {
               var7 = var11.width;
            }

            if (var8 < var11.height) {
               var8 = var11.height;
            }
         }

         return new Dimension(var3.left + var3.right + var6 * var7 + (var6 - 1) * this.hgap, var3.top + var3.bottom + var5 * var8 + (var5 - 1) * this.vgap);
      }
   }

   public void layoutContainer(Container var1) {
      synchronized(var1.getTreeLock()) {
         Insets var3 = var1.getInsets();
         int var4 = var1.getComponentCount();
         int var5 = this.rows;
         int var6 = this.cols;
         boolean var7 = var1.getComponentOrientation().isLeftToRight();
         if (var4 != 0) {
            if (var5 > 0) {
               var6 = (var4 + var5 - 1) / var5;
            } else {
               var5 = (var4 + var6 - 1) / var6;
            }

            int var8 = (var6 - 1) * this.hgap;
            int var9 = var1.width - (var3.left + var3.right);
            int var10 = (var9 - var8) / var6;
            int var11 = (var9 - (var10 * var6 + var8)) / 2;
            int var12 = (var5 - 1) * this.vgap;
            int var13 = var1.height - (var3.top + var3.bottom);
            int var14 = (var13 - var12) / var5;
            int var15 = (var13 - (var14 * var5 + var12)) / 2;
            int var16;
            int var17;
            int var18;
            int var19;
            int var20;
            if (var7) {
               var16 = 0;

               for(var17 = var3.left + var11; var16 < var6; var17 += var10 + this.hgap) {
                  var18 = 0;

                  for(var19 = var3.top + var15; var18 < var5; var19 += var14 + this.vgap) {
                     var20 = var18 * var6 + var16;
                     if (var20 < var4) {
                        var1.getComponent(var20).setBounds(var17, var19, var10, var14);
                     }

                     ++var18;
                  }

                  ++var16;
               }
            } else {
               var16 = 0;

               for(var17 = var1.width - var3.right - var10 - var11; var16 < var6; var17 -= var10 + this.hgap) {
                  var18 = 0;

                  for(var19 = var3.top + var15; var18 < var5; var19 += var14 + this.vgap) {
                     var20 = var18 * var6 + var16;
                     if (var20 < var4) {
                        var1.getComponent(var20).setBounds(var17, var19, var10, var14);
                     }

                     ++var18;
                  }

                  ++var16;
               }
            }

         }
      }
   }

   public String toString() {
      return this.getClass().getName() + "[hgap=" + this.hgap + ",vgap=" + this.vgap + ",rows=" + this.rows + ",cols=" + this.cols + "]";
   }
}
