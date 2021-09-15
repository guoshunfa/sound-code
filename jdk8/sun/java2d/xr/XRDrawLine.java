package sun.java2d.xr;

public class XRDrawLine {
   static final int BIG_MAX = 536870911;
   static final int BIG_MIN = -536870912;
   static final int OUTCODE_TOP = 1;
   static final int OUTCODE_BOTTOM = 2;
   static final int OUTCODE_LEFT = 4;
   static final int OUTCODE_RIGHT = 8;
   int x1;
   int y1;
   int x2;
   int y2;
   int ucX1;
   int ucY1;
   int ucX2;
   int ucY2;
   DirtyRegion region = new DirtyRegion();

   protected void rasterizeLine(GrowableRectArray var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, boolean var10, boolean var11) {
      this.initCoordinates(var2, var3, var4, var5, var11);
      int var18 = this.x2 - this.x1;
      int var19 = this.y2 - this.y1;
      int var20 = Math.abs(var18);
      int var21 = Math.abs(var19);
      boolean var17 = var20 >= var21;
      float var12 = (float)var20 / (float)var21;
      if (!var10 || this.clipCoordinates(var6, var7, var8, var9, var17, var18, var19, var20, var21)) {
         this.region.setDirtyLineRegion(this.x1, this.y1, this.x2, this.y2);
         int var22 = this.region.x2 - this.region.x;
         int var23 = this.region.y2 - this.region.y;
         if (var22 != 0 && var23 != 0) {
            int var14;
            int var15;
            int var16;
            if (var17) {
               var16 = var21 * 2;
               var15 = var20 * 2;
               var20 = -var20;
               var14 = this.x2 - this.x1;
            } else {
               var16 = var20 * 2;
               var15 = var21 * 2;
               var21 = -var21;
               var14 = this.y2 - this.y1;
            }

            if ((var14 = Math.abs(var14) + 1) != 0) {
               int var13 = -(var15 / 2);
               int var24;
               if (this.y1 != this.ucY1) {
                  var24 = this.y1 - this.ucY1;
                  if (var24 < 0) {
                     var24 = -var24;
                  }

                  var13 += var24 * var20 * 2;
               }

               if (this.x1 != this.ucX1) {
                  var24 = this.x1 - this.ucX1;
                  if (var24 < 0) {
                     var24 = -var24;
                  }

                  var13 += var24 * var21 * 2;
               }

               var13 += var16;
               var15 -= var16;
               var24 = var18 > 0 ? 1 : -1;
               int var25 = var19 > 0 ? 1 : -1;
               int var26 = var17 ? var24 : 0;
               int var27 = !var17 ? var25 : 0;
               if ((double)var12 > 0.9D && (double)var12 < 1.1D) {
                  this.lineToPoints(var1, var14, var13, var16, var15, var24, var25, var26, var27);
               } else {
                  this.lineToRects(var1, var14, var13, var16, var15, var24, var25, var26, var27);
               }

            }
         } else {
            var1.pushRectValues(this.region.x, this.region.y, this.region.x2 - this.region.x + 1, this.region.y2 - this.region.y + 1);
         }
      }
   }

   private void lineToPoints(GrowableRectArray var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      int var10 = this.x1;
      int var11 = this.y1;

      do {
         var1.pushRectValues(var10, var11, 1, 1);
         if (var3 < 0) {
            var3 += var4;
            var10 += var8;
            var11 += var9;
         } else {
            var3 -= var5;
            var10 += var6;
            var11 += var7;
         }

         --var2;
      } while(var2 > 0);

   }

   private void lineToRects(GrowableRectArray var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      int var10 = this.x1;
      int var11 = this.y1;
      int var12 = Integer.MIN_VALUE;
      int var13 = 0;
      int var14 = 0;
      int var15 = 0;

      do {
         if (var11 == var13) {
            if (var10 == var12 + var14) {
               ++var14;
            } else if (var10 == var12 - 1) {
               --var12;
               ++var14;
            }
         } else if (var10 == var12) {
            if (var11 == var13 + var15) {
               ++var15;
            } else if (var11 == var13 - 1) {
               --var13;
               ++var15;
            }
         } else {
            if (var12 != Integer.MIN_VALUE) {
               var1.pushRectValues(var12, var13, var14, var15);
            }

            var12 = var10;
            var13 = var11;
            var15 = 1;
            var14 = 1;
         }

         if (var3 < 0) {
            var3 += var4;
            var10 += var8;
            var11 += var9;
         } else {
            var3 -= var5;
            var10 += var6;
            var11 += var7;
         }

         --var2;
      } while(var2 > 0);

      var1.pushRectValues(var12, var13, var14, var15);
   }

   private boolean clipCoordinates(int var1, int var2, int var3, int var4, boolean var5, int var6, int var7, int var8, int var9) {
      int var10 = this.outcode(this.x1, this.y1, var1, var2, var3, var4);
      int var11 = this.outcode(this.x2, this.y2, var1, var2, var3, var4);

      while((var10 | var11) != 0) {
         boolean var12 = false;
         boolean var13 = false;
         if ((var10 & var11) != 0) {
            return false;
         }

         int var14;
         int var15;
         if (var10 != 0) {
            if ((var10 & 3) != 0) {
               if ((var10 & 1) != 0) {
                  this.y1 = var2;
               } else {
                  this.y1 = var4;
               }

               var15 = this.y1 - this.ucY1;
               if (var15 < 0) {
                  var15 = -var15;
               }

               var14 = 2 * var15 * var8 + var9;
               if (var5) {
                  var14 += var9 - var8 - 1;
               }

               var14 /= 2 * var9;
               if (var6 < 0) {
                  var14 = -var14;
               }

               this.x1 = this.ucX1 + var14;
            } else if ((var10 & 12) != 0) {
               if ((var10 & 4) != 0) {
                  this.x1 = var1;
               } else {
                  this.x1 = var3;
               }

               var14 = this.x1 - this.ucX1;
               if (var14 < 0) {
                  var14 = -var14;
               }

               var15 = 2 * var14 * var9 + var8;
               if (!var5) {
                  var15 += var8 - var9 - 1;
               }

               var15 /= 2 * var8;
               if (var7 < 0) {
                  var15 = -var15;
               }

               this.y1 = this.ucY1 + var15;
            }

            var10 = this.outcode(this.x1, this.y1, var1, var2, var3, var4);
         } else {
            if ((var11 & 3) != 0) {
               if ((var11 & 1) != 0) {
                  this.y2 = var2;
               } else {
                  this.y2 = var4;
               }

               var15 = this.y2 - this.ucY2;
               if (var15 < 0) {
                  var15 = -var15;
               }

               var14 = 2 * var15 * var8 + var9;
               if (var5) {
                  var14 += var9 - var8;
               } else {
                  --var14;
               }

               var14 /= 2 * var9;
               if (var6 > 0) {
                  var14 = -var14;
               }

               this.x2 = this.ucX2 + var14;
            } else if ((var11 & 12) != 0) {
               if ((var11 & 4) != 0) {
                  this.x2 = var1;
               } else {
                  this.x2 = var3;
               }

               var14 = this.x2 - this.ucX2;
               if (var14 < 0) {
                  var14 = -var14;
               }

               var15 = 2 * var14 * var9 + var8;
               if (var5) {
                  --var15;
               } else {
                  var15 += var8 - var9;
               }

               var15 /= 2 * var8;
               if (var7 > 0) {
                  var15 = -var15;
               }

               this.y2 = this.ucY2 + var15;
            }

            var11 = this.outcode(this.x2, this.y2, var1, var2, var3, var4);
         }
      }

      return true;
   }

   private void initCoordinates(int var1, int var2, int var3, int var4, boolean var5) {
      if (var5 && (this.OverflowsBig(var1) || this.OverflowsBig(var2) || this.OverflowsBig(var3) || this.OverflowsBig(var4))) {
         double var6 = (double)var1;
         double var8 = (double)var2;
         double var10 = (double)var3;
         double var12 = (double)var4;
         double var14 = var10 - var6;
         double var16 = var12 - var8;
         if (var1 < -536870912) {
            var8 = (double)var2 + (double)(-536870912 - var1) * var16 / var14;
            var6 = -5.36870912E8D;
         } else if (var1 > 536870911) {
            var8 = (double)var2 - (double)(var1 - 536870911) * var16 / var14;
            var6 = 5.36870911E8D;
         }

         if (var8 < -5.36870912E8D) {
            var6 = (double)var1 + (double)(-536870912 - var2) * var14 / var16;
            var8 = -5.36870912E8D;
         } else if (var8 > 5.36870911E8D) {
            var6 = (double)var1 - (double)(var2 - 536870911) * var14 / var16;
            var8 = 5.36870911E8D;
         }

         if (var3 < -536870912) {
            var12 = (double)var4 + (double)(-536870912 - var3) * var16 / var14;
            var10 = -5.36870912E8D;
         } else if (var3 > 536870911) {
            var12 = (double)var4 - (double)(var3 - 536870911) * var16 / var14;
            var10 = 5.36870911E8D;
         }

         if (var12 < -5.36870912E8D) {
            var10 = (double)var3 + (double)(-536870912 - var4) * var14 / var16;
            var12 = -5.36870912E8D;
         } else if (var12 > 5.36870911E8D) {
            var10 = (double)var3 - (double)(var4 - 536870911) * var14 / var16;
            var12 = 5.36870911E8D;
         }

         var1 = (int)var6;
         var2 = (int)var8;
         var3 = (int)var10;
         var4 = (int)var12;
      }

      this.x1 = this.ucX1 = var1;
      this.y1 = this.ucY1 = var2;
      this.x2 = this.ucX2 = var3;
      this.y2 = this.ucY2 = var4;
   }

   private boolean OverflowsBig(int var1) {
      return var1 != var1 << 2 >> 2;
   }

   private int out(int var1, int var2, int var3, int var4, int var5) {
      return var1 < var2 ? var4 : (var1 > var3 ? var5 : 0);
   }

   private int outcode(int var1, int var2, int var3, int var4, int var5, int var6) {
      return this.out(var2, var4, var6, 1, 2) | this.out(var1, var3, var5, 4, 8);
   }
}
