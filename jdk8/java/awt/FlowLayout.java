package java.awt;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class FlowLayout implements LayoutManager, Serializable {
   public static final int LEFT = 0;
   public static final int CENTER = 1;
   public static final int RIGHT = 2;
   public static final int LEADING = 3;
   public static final int TRAILING = 4;
   int align;
   int newAlign;
   int hgap;
   int vgap;
   private boolean alignOnBaseline;
   private static final long serialVersionUID = -7262534875583282631L;
   private static final int currentSerialVersion = 1;
   private int serialVersionOnStream;

   public FlowLayout() {
      this(1, 5, 5);
   }

   public FlowLayout(int var1) {
      this(var1, 5, 5);
   }

   public FlowLayout(int var1, int var2, int var3) {
      this.serialVersionOnStream = 1;
      this.hgap = var2;
      this.vgap = var3;
      this.setAlignment(var1);
   }

   public int getAlignment() {
      return this.newAlign;
   }

   public void setAlignment(int var1) {
      this.newAlign = var1;
      switch(var1) {
      case 3:
         this.align = 0;
         break;
      case 4:
         this.align = 2;
         break;
      default:
         this.align = var1;
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

   public void setAlignOnBaseline(boolean var1) {
      this.alignOnBaseline = var1;
   }

   public boolean getAlignOnBaseline() {
      return this.alignOnBaseline;
   }

   public void addLayoutComponent(String var1, Component var2) {
   }

   public void removeLayoutComponent(Component var1) {
   }

   public Dimension preferredLayoutSize(Container var1) {
      synchronized(var1.getTreeLock()) {
         Dimension var3 = new Dimension(0, 0);
         int var4 = var1.getComponentCount();
         boolean var5 = true;
         boolean var6 = this.getAlignOnBaseline();
         int var7 = 0;
         int var8 = 0;

         for(int var9 = 0; var9 < var4; ++var9) {
            Component var10 = var1.getComponent(var9);
            if (var10.isVisible()) {
               Dimension var11 = var10.getPreferredSize();
               var3.height = Math.max(var3.height, var11.height);
               if (var5) {
                  var5 = false;
               } else {
                  var3.width += this.hgap;
               }

               var3.width += var11.width;
               if (var6) {
                  int var12 = var10.getBaseline(var11.width, var11.height);
                  if (var12 >= 0) {
                     var7 = Math.max(var7, var12);
                     var8 = Math.max(var8, var11.height - var12);
                  }
               }
            }
         }

         if (var6) {
            var3.height = Math.max(var7 + var8, var3.height);
         }

         Insets var15 = var1.getInsets();
         var3.width += var15.left + var15.right + this.hgap * 2;
         var3.height += var15.top + var15.bottom + this.vgap * 2;
         return var3;
      }
   }

   public Dimension minimumLayoutSize(Container var1) {
      synchronized(var1.getTreeLock()) {
         boolean var3 = this.getAlignOnBaseline();
         Dimension var4 = new Dimension(0, 0);
         int var5 = var1.getComponentCount();
         int var6 = 0;
         int var7 = 0;
         boolean var8 = true;

         for(int var9 = 0; var9 < var5; ++var9) {
            Component var10 = var1.getComponent(var9);
            if (var10.visible) {
               Dimension var11 = var10.getMinimumSize();
               var4.height = Math.max(var4.height, var11.height);
               if (var8) {
                  var8 = false;
               } else {
                  var4.width += this.hgap;
               }

               var4.width += var11.width;
               if (var3) {
                  int var12 = var10.getBaseline(var11.width, var11.height);
                  if (var12 >= 0) {
                     var6 = Math.max(var6, var12);
                     var7 = Math.max(var7, var4.height - var12);
                  }
               }
            }
         }

         if (var3) {
            var4.height = Math.max(var6 + var7, var4.height);
         }

         Insets var15 = var1.getInsets();
         var4.width += var15.left + var15.right + this.hgap * 2;
         var4.height += var15.top + var15.bottom + this.vgap * 2;
         return var4;
      }
   }

   private int moveComponents(Container var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9, int[] var10, int[] var11) {
      switch(this.newAlign) {
      case 0:
         var2 += var8 ? 0 : var4;
         break;
      case 1:
         var2 += var4 / 2;
         break;
      case 2:
         var2 += var8 ? var4 : 0;
      case 3:
      default:
         break;
      case 4:
         var2 += var4;
      }

      int var12 = 0;
      int var13 = 0;
      int var14 = 0;
      int var15;
      if (var9) {
         var15 = 0;

         for(int var16 = var6; var16 < var7; ++var16) {
            Component var17 = var1.getComponent(var16);
            if (var17.visible) {
               if (var10[var16] >= 0) {
                  var12 = Math.max(var12, var10[var16]);
                  var15 = Math.max(var15, var11[var16]);
               } else {
                  var13 = Math.max(var17.getHeight(), var13);
               }
            }
         }

         var5 = Math.max(var12 + var15, var13);
         var14 = (var5 - var12 - var15) / 2;
      }

      for(var15 = var6; var15 < var7; ++var15) {
         Component var18 = var1.getComponent(var15);
         if (var18.isVisible()) {
            int var19;
            if (var9 && var10[var15] >= 0) {
               var19 = var3 + var14 + var12 - var10[var15];
            } else {
               var19 = var3 + (var5 - var18.height) / 2;
            }

            if (var8) {
               var18.setLocation(var2, var19);
            } else {
               var18.setLocation(var1.width - var2 - var18.width, var19);
            }

            var2 += var18.width + this.hgap;
         }
      }

      return var5;
   }

   public void layoutContainer(Container var1) {
      synchronized(var1.getTreeLock()) {
         Insets var3 = var1.getInsets();
         int var4 = var1.width - (var3.left + var3.right + this.hgap * 2);
         int var5 = var1.getComponentCount();
         int var6 = 0;
         int var7 = var3.top + this.vgap;
         int var8 = 0;
         int var9 = 0;
         boolean var10 = var1.getComponentOrientation().isLeftToRight();
         boolean var11 = this.getAlignOnBaseline();
         int[] var12 = null;
         int[] var13 = null;
         if (var11) {
            var12 = new int[var5];
            var13 = new int[var5];
         }

         for(int var14 = 0; var14 < var5; ++var14) {
            Component var15 = var1.getComponent(var14);
            if (var15.isVisible()) {
               Dimension var16 = var15.getPreferredSize();
               var15.setSize(var16.width, var16.height);
               if (var11) {
                  int var17 = var15.getBaseline(var16.width, var16.height);
                  if (var17 >= 0) {
                     var12[var14] = var17;
                     var13[var14] = var16.height - var17;
                  } else {
                     var12[var14] = -1;
                  }
               }

               if (var6 != 0 && var6 + var16.width > var4) {
                  var8 = this.moveComponents(var1, var3.left + this.hgap, var7, var4 - var6, var8, var9, var14, var10, var11, var12, var13);
                  var6 = var16.width;
                  var7 += this.vgap + var8;
                  var8 = var16.height;
                  var9 = var14;
               } else {
                  if (var6 > 0) {
                     var6 += this.hgap;
                  }

                  var6 += var16.width;
                  var8 = Math.max(var8, var16.height);
               }
            }
         }

         this.moveComponents(var1, var3.left + this.hgap, var7, var4 - var6, var8, var9, var5, var10, var11, var12, var13);
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.serialVersionOnStream < 1) {
         this.setAlignment(this.align);
      }

      this.serialVersionOnStream = 1;
   }

   public String toString() {
      String var1 = "";
      switch(this.align) {
      case 0:
         var1 = ",align=left";
         break;
      case 1:
         var1 = ",align=center";
         break;
      case 2:
         var1 = ",align=right";
         break;
      case 3:
         var1 = ",align=leading";
         break;
      case 4:
         var1 = ",align=trailing";
      }

      return this.getClass().getName() + "[hgap=" + this.hgap + ",vgap=" + this.vgap + var1 + "]";
   }
}
