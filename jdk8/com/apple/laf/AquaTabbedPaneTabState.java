package com.apple.laf;

import java.awt.Dimension;
import java.awt.Rectangle;

class AquaTabbedPaneTabState {
   static final int FIXED_SCROLL_TAB_LENGTH = 27;
   protected final Rectangle leftScrollTabRect = new Rectangle();
   protected final Rectangle rightScrollTabRect = new Rectangle();
   protected int numberOfVisibleTabs = 0;
   protected int[] visibleTabList = new int[10];
   protected int lastLeftmostTab;
   protected int lastReturnAt;
   private boolean needsScrollers;
   private boolean hasMoreLeftTabs;
   private boolean hasMoreRightTabs;
   private final AquaTabbedPaneUI pane;

   protected AquaTabbedPaneTabState(AquaTabbedPaneUI var1) {
      this.pane = var1;
   }

   protected int getIndex(int var1) {
      return var1 >= this.visibleTabList.length ? Integer.MIN_VALUE : this.visibleTabList[var1];
   }

   protected void init(int var1) {
      if (var1 < 1) {
         this.needsScrollers = false;
      }

      if (var1 != this.visibleTabList.length) {
         int[] var2 = new int[var1];
         System.arraycopy(this.visibleTabList, 0, var2, 0, Math.min(this.visibleTabList.length, var1));
         this.visibleTabList = var2;
      }
   }

   int getTotal() {
      return this.numberOfVisibleTabs;
   }

   boolean needsScrollTabs() {
      return this.needsScrollers;
   }

   void setNeedsScrollers(boolean var1) {
      this.needsScrollers = var1;
   }

   boolean needsLeftScrollTab() {
      return this.hasMoreLeftTabs;
   }

   boolean needsRightScrollTab() {
      return this.hasMoreRightTabs;
   }

   Rectangle getLeftScrollTabRect() {
      return this.leftScrollTabRect;
   }

   Rectangle getRightScrollTabRect() {
      return this.rightScrollTabRect;
   }

   boolean isBefore(int var1) {
      if (this.numberOfVisibleTabs == 0) {
         return true;
      } else {
         return var1 < this.visibleTabList[0];
      }
   }

   boolean isAfter(int var1) {
      return var1 > this.visibleTabList[this.numberOfVisibleTabs - 1];
   }

   private void addToEnd(int var1, int var2) {
      this.visibleTabList[var2] = var1;
   }

   private void addToBeginning(int var1, int var2) {
      System.arraycopy(this.visibleTabList, 0, this.visibleTabList, 1, var2);
      this.visibleTabList[0] = var1;
   }

   void relayoutForScrolling(Rectangle[] var1, int var2, int var3, int var4, int var5, boolean var6, int var7, boolean var8) {
      if (!this.needsScrollers) {
         this.hasMoreLeftTabs = false;
         this.hasMoreRightTabs = false;
      } else {
         if (var6) {
            this.rightScrollTabRect.height = 27;
            this.leftScrollTabRect.height = 27;
         } else {
            this.rightScrollTabRect.width = 27;
            this.leftScrollTabRect.width = 27;
         }

         boolean var9 = var4 != this.lastReturnAt;
         if (this.pane.popupSelectionChanged || var9) {
            this.pane.popupSelectionChanged = false;
            this.lastLeftmostTab = -1;
         }

         int var10 = var5;
         int var11 = var5 - 1;
         if (this.lastLeftmostTab >= 0) {
            var10 = this.lastLeftmostTab;
            var11 = -1;
         } else if (var5 < 0) {
            var10 = 0;
            var11 = -1;
         }

         int var12 = var4 - this.pane.tabAreaInsets.right - this.pane.tabAreaInsets.left - 54;
         int var13 = 0;
         Rectangle var14 = var1[var10];
         if ((var6 ? var14.height : var14.width) > var12) {
            this.addToEnd(var10, var13);
            if (var6) {
               var14.height = var12;
            } else {
               var14.width = var12;
            }

            ++var13;
         } else {
            boolean var15 = false;
            boolean var16 = false;

            label128:
            while(true) {
               while(true) {
                  if (var13 >= var7 || var15 && var16) {
                     break label128;
                  }

                  Rectangle var17;
                  if (!var15 && var10 >= 0 && var10 < var7) {
                     var17 = var1[var10];
                     if ((var6 ? var17.height : var17.width) <= var12) {
                        this.addToEnd(var10, var13);
                        ++var13;
                        var12 -= var6 ? var17.height : var17.width;
                        ++var10;
                        continue;
                     }

                     var15 = true;
                  } else {
                     var15 = true;
                  }

                  if (!var16 && var11 >= 0 && var11 < var7) {
                     var17 = var1[var11];
                     if ((var6 ? var17.height : var17.width) > var12) {
                        var16 = true;
                     } else {
                        this.addToBeginning(var11, var13);
                        ++var13;
                        var12 -= var6 ? var17.height : var17.width;
                        --var11;
                     }
                  } else {
                     var16 = true;
                  }
               }
            }
         }

         if (var13 > this.visibleTabList.length) {
            var13 = this.visibleTabList.length;
         }

         this.hasMoreLeftTabs = this.visibleTabList[0] > 0;
         this.hasMoreRightTabs = this.visibleTabList[var13 - 1] < this.visibleTabList.length - 1;
         this.numberOfVisibleTabs = var13;
         this.lastLeftmostTab = this.getIndex(0);
         this.lastReturnAt = var4;
         int var19 = this.getIndex(0);
         int var20 = this.getIndex(var13 - 1);

         for(int var21 = 0; var21 < var7; ++var21) {
            if (var21 < var19 || var21 > var20) {
               Rectangle var18 = var1[var21];
               var18.x = 32767;
               var18.y = 32767;
            }
         }

      }
   }

   protected void alignRectsRunFor(Rectangle[] var1, Dimension var2, int var3, boolean var4) {
      boolean var5 = var3 == 2 || var3 == 4;
      if (var5) {
         if (this.needsScrollers) {
            this.stretchScrollingVerticalRun(var1, var2);
         } else {
            this.centerVerticalRun(var1, var2);
         }
      } else if (this.needsScrollers) {
         this.stretchScrollingHorizontalRun(var1, var2, var4);
      } else {
         this.centerHorizontalRun(var1, var2, var4);
      }

   }

   private void centerHorizontalRun(Rectangle[] var1, Dimension var2, boolean var3) {
      int var4 = 0;
      Rectangle[] var5 = var1;
      int var6 = var1.length;

      int var7;
      for(var7 = 0; var7 < var6; ++var7) {
         Rectangle var8 = var5[var7];
         var4 += var8.width;
      }

      int var10 = var2.width / 2 - var4 / 2;
      if (var3) {
         Rectangle[] var11 = var1;
         var7 = var1.length;

         for(int var13 = 0; var13 < var7; ++var13) {
            Rectangle var9 = var11[var13];
            var9.x = var10;
            var10 += var9.width;
         }
      } else {
         for(var6 = var1.length - 1; var6 >= 0; --var6) {
            Rectangle var12 = var1[var6];
            var12.x = var10;
            var10 += var12.width;
         }
      }

   }

   private void centerVerticalRun(Rectangle[] var1, Dimension var2) {
      int var3 = 0;
      Rectangle[] var4 = var1;
      int var5 = var1.length;

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         Rectangle var7 = var4[var6];
         var3 += var7.height;
      }

      int var9 = var2.height / 2 - var3 / 2;
      Rectangle[] var10 = var1;
      var6 = var1.length;

      for(int var11 = 0; var11 < var6; ++var11) {
         Rectangle var8 = var10[var11];
         var8.y = var9;
         var9 += var8.height;
      }

   }

   private void stretchScrollingHorizontalRun(Rectangle[] var1, Dimension var2, boolean var3) {
      int var4 = this.getTotal();
      int var5 = this.getIndex(0);
      int var6 = this.getIndex(var4 - 1);
      int var7 = 0;

      int var8;
      for(var8 = var5; var8 <= var6; ++var8) {
         var7 += var1[var8].width;
      }

      var8 = var2.width - var7 - this.pane.tabAreaInsets.left - this.pane.tabAreaInsets.right;
      if (this.needsLeftScrollTab()) {
         var8 -= 27;
      }

      if (this.needsRightScrollTab()) {
         var8 -= 27;
      }

      int var9 = (int)((float)var8 / (float)var4);
      int var10 = var8 - var9 * var4;
      int var11 = 0;
      int var12 = this.pane.tabAreaInsets.left + (this.needsLeftScrollTab() ? 27 : 0);
      int var13;
      Rectangle var14;
      int var15;
      if (var3) {
         for(var13 = var5; var13 <= var6; ++var13) {
            var14 = var1[var13];
            var15 = var9;
            if (var10 > 0) {
               var15 = var9 + 1;
               --var10;
            }

            var14.x = var11 + var12;
            var14.width += var15;
            var11 += var14.width;
         }
      } else {
         for(var13 = var6; var13 >= var5; --var13) {
            var14 = var1[var13];
            var15 = var9;
            if (var10 > 0) {
               var15 = var9 + 1;
               --var10;
            }

            var14.x = var11 + var12;
            var14.width += var15;
            var11 += var14.width;
         }
      }

      if (var3) {
         this.leftScrollTabRect.x = this.pane.tabAreaInsets.left;
         this.leftScrollTabRect.y = var1[var5].y;
         this.leftScrollTabRect.height = var1[var5].height;
         this.rightScrollTabRect.x = var2.width - this.pane.tabAreaInsets.right - this.rightScrollTabRect.width;
         this.rightScrollTabRect.y = var1[var6].y;
         this.rightScrollTabRect.height = var1[var6].height;
      } else {
         this.rightScrollTabRect.x = this.pane.tabAreaInsets.left;
         this.rightScrollTabRect.y = var1[var5].y;
         this.rightScrollTabRect.height = var1[var5].height;
         this.leftScrollTabRect.x = var2.width - this.pane.tabAreaInsets.right - this.rightScrollTabRect.width;
         this.leftScrollTabRect.y = var1[var6].y;
         this.leftScrollTabRect.height = var1[var6].height;
         if (this.needsLeftScrollTab()) {
            for(var13 = var6; var13 >= var5; --var13) {
               var14 = var1[var13];
               var14.x -= 27;
            }
         }

         if (this.needsRightScrollTab()) {
            for(var13 = var6; var13 >= var5; --var13) {
               var14 = var1[var13];
               var14.x += 27;
            }
         }
      }

   }

   private void stretchScrollingVerticalRun(Rectangle[] var1, Dimension var2) {
      int var3 = this.getTotal();
      int var4 = this.getIndex(0);
      int var5 = this.getIndex(var3 - 1);
      int var6 = 0;

      int var7;
      for(var7 = var4; var7 <= var5; ++var7) {
         var6 += var1[var7].height;
      }

      var7 = var2.height - var6 - this.pane.tabAreaInsets.top - this.pane.tabAreaInsets.bottom;
      if (this.needsLeftScrollTab()) {
         var7 -= 27;
      }

      if (this.needsRightScrollTab()) {
         var7 -= 27;
      }

      int var8 = (int)((float)var7 / (float)var3);
      int var9 = var7 - var8 * var3;
      int var10 = 0;
      int var11 = this.pane.tabAreaInsets.top + (this.needsLeftScrollTab() ? 27 : 0);

      for(int var12 = var4; var12 <= var5; ++var12) {
         Rectangle var13 = var1[var12];
         int var14 = var8;
         if (var9 > 0) {
            var14 = var8 + 1;
            --var9;
         }

         var13.y = var10 + var11;
         var13.height += var14;
         var10 += var13.height;
      }

      this.leftScrollTabRect.x = var1[var4].x;
      this.leftScrollTabRect.y = this.pane.tabAreaInsets.top;
      this.leftScrollTabRect.width = var1[var4].width;
      this.rightScrollTabRect.x = var1[var5].x;
      this.rightScrollTabRect.y = var2.height - this.pane.tabAreaInsets.bottom - this.rightScrollTabRect.height;
      this.rightScrollTabRect.width = var1[var5].width;
   }
}
