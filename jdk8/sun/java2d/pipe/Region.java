package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.RectangularShape;

public class Region {
   static final int INIT_SIZE = 50;
   static final int GROW_SIZE = 50;
   public static final Region EMPTY_REGION = new Region.ImmutableRegion(0, 0, 0, 0);
   public static final Region WHOLE_REGION = new Region.ImmutableRegion(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
   int lox;
   int loy;
   int hix;
   int hiy;
   int endIndex;
   int[] bands;
   static final int INCLUDE_A = 1;
   static final int INCLUDE_B = 2;
   static final int INCLUDE_COMMON = 4;

   private static native void initIDs();

   public static int dimAdd(int var0, int var1) {
      if (var1 <= 0) {
         return var0;
      } else {
         return (var1 += var0) < var0 ? Integer.MAX_VALUE : var1;
      }
   }

   public static int clipAdd(int var0, int var1) {
      int var2 = var0 + var1;
      if (var2 > var0 != var1 > 0) {
         var2 = var1 < 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
      }

      return var2;
   }

   public static int clipScale(int var0, double var1) {
      if (var1 == 1.0D) {
         return var0;
      } else {
         double var3 = (double)var0 * var1;
         if (var3 < -2.147483648E9D) {
            return Integer.MIN_VALUE;
         } else {
            return var3 > 2.147483647E9D ? Integer.MAX_VALUE : (int)Math.round(var3);
         }
      }
   }

   protected Region(int var1, int var2, int var3, int var4) {
      this.lox = var1;
      this.loy = var2;
      this.hix = var3;
      this.hiy = var4;
   }

   private Region(int var1, int var2, int var3, int var4, int[] var5, int var6) {
      this.lox = var1;
      this.loy = var2;
      this.hix = var3;
      this.hiy = var4;
      this.bands = var5;
      this.endIndex = var6;
   }

   public static Region getInstance(Shape var0, AffineTransform var1) {
      return getInstance(WHOLE_REGION, false, var0, var1);
   }

   public static Region getInstance(Region var0, Shape var1, AffineTransform var2) {
      return getInstance(var0, false, var1, var2);
   }

   public static Region getInstance(Region var0, boolean var1, Shape var2, AffineTransform var3) {
      if (var2 instanceof RectangularShape && ((RectangularShape)var2).isEmpty()) {
         return EMPTY_REGION;
      } else {
         int[] var4 = new int[4];
         ShapeSpanIterator var5 = new ShapeSpanIterator(var1);

         Region var7;
         try {
            var5.setOutputArea(var0);
            var5.appendPath(var2.getPathIterator(var3));
            var5.getPathBox(var4);
            Region var6 = getInstance(var4);
            var6.appendSpans(var5);
            var7 = var6;
         } finally {
            var5.dispose();
         }

         return var7;
      }
   }

   static Region getInstance(int var0, int var1, int var2, int var3, int[] var4) {
      int var5 = var4[0];
      int var6 = var4[1];
      if (var3 > var1 && var2 > var0 && var6 > var5) {
         int[] var7 = new int[(var6 - var5) * 5];
         int var8 = 0;
         int var9 = 2;

         for(int var10 = var5; var10 < var6; ++var10) {
            int var11 = Math.max(clipAdd(var0, var4[var9++]), var0);
            int var12 = Math.min(clipAdd(var0, var4[var9++]), var2);
            if (var11 < var12) {
               int var13 = Math.max(clipAdd(var1, var10), var1);
               int var14 = Math.min(clipAdd(var13, 1), var3);
               if (var13 < var14) {
                  var7[var8++] = var13;
                  var7[var8++] = var14;
                  var7[var8++] = 1;
                  var7[var8++] = var11;
                  var7[var8++] = var12;
               }
            }
         }

         return var8 != 0 ? new Region(var0, var1, var2, var3, var7, var8) : EMPTY_REGION;
      } else {
         return EMPTY_REGION;
      }
   }

   public static Region getInstance(Rectangle var0) {
      return getInstanceXYWH(var0.x, var0.y, var0.width, var0.height);
   }

   public static Region getInstanceXYWH(int var0, int var1, int var2, int var3) {
      return getInstanceXYXY(var0, var1, dimAdd(var0, var2), dimAdd(var1, var3));
   }

   public static Region getInstance(int[] var0) {
      return new Region(var0[0], var0[1], var0[2], var0[3]);
   }

   public static Region getInstanceXYXY(int var0, int var1, int var2, int var3) {
      return new Region(var0, var1, var2, var3);
   }

   public void setOutputArea(Rectangle var1) {
      this.setOutputAreaXYWH(var1.x, var1.y, var1.width, var1.height);
   }

   public void setOutputAreaXYWH(int var1, int var2, int var3, int var4) {
      this.setOutputAreaXYXY(var1, var2, dimAdd(var1, var3), dimAdd(var2, var4));
   }

   public void setOutputArea(int[] var1) {
      this.lox = var1[0];
      this.loy = var1[1];
      this.hix = var1[2];
      this.hiy = var1[3];
   }

   public void setOutputAreaXYXY(int var1, int var2, int var3, int var4) {
      this.lox = var1;
      this.loy = var2;
      this.hix = var3;
      this.hiy = var4;
   }

   public void appendSpans(SpanIterator var1) {
      int[] var2 = new int[6];

      while(var1.nextSpan(var2)) {
         this.appendSpan(var2);
      }

      this.endRow(var2);
      this.calcBBox();
   }

   public Region getScaledRegion(double var1, double var3) {
      if (var1 != 0.0D && var3 != 0.0D && this != EMPTY_REGION) {
         if ((var1 != 1.0D || var3 != 1.0D) && this != WHOLE_REGION) {
            int var5 = clipScale(this.lox, var1);
            int var6 = clipScale(this.loy, var3);
            int var7 = clipScale(this.hix, var1);
            int var8 = clipScale(this.hiy, var3);
            Region var9 = new Region(var5, var6, var7, var8);
            int[] var10 = this.bands;
            if (var10 != null) {
               int var11 = this.endIndex;
               int[] var12 = new int[var11];
               int var13 = 0;
               int var14 = 0;

               while(var13 < var11) {
                  int var16;
                  var12[var14++] = var16 = clipScale(var10[var13++], var3);
                  int var17;
                  var12[var14++] = var17 = clipScale(var10[var13++], var3);
                  int var15;
                  var12[var14++] = var15 = var10[var13++];
                  int var18 = var14;
                  if (var16 < var17) {
                     while(true) {
                        --var15;
                        if (var15 < 0) {
                           break;
                        }

                        int var19 = clipScale(var10[var13++], var1);
                        int var20 = clipScale(var10[var13++], var1);
                        if (var19 < var20) {
                           var12[var14++] = var19;
                           var12[var14++] = var20;
                        }
                     }
                  } else {
                     var13 += var15 * 2;
                  }

                  if (var14 > var18) {
                     var12[var18 - 1] = (var14 - var18) / 2;
                  } else {
                     var14 = var18 - 3;
                  }
               }

               if (var14 <= 5) {
                  if (var14 < 5) {
                     var9.lox = var9.loy = var9.hix = var9.hiy = 0;
                  } else {
                     var9.loy = var12[0];
                     var9.hiy = var12[1];
                     var9.lox = var12[3];
                     var9.hix = var12[4];
                  }
               } else {
                  var9.endIndex = var14;
                  var9.bands = var12;
               }
            }

            return var9;
         } else {
            return this;
         }
      } else {
         return EMPTY_REGION;
      }
   }

   public Region getTranslatedRegion(int var1, int var2) {
      if ((var1 | var2) == 0) {
         return this;
      } else {
         int var3 = this.lox + var1;
         int var4 = this.loy + var2;
         int var5 = this.hix + var1;
         int var6 = this.hiy + var2;
         if (var3 > this.lox == var1 > 0 && var4 > this.loy == var2 > 0 && var5 > this.hix == var1 > 0 && var6 > this.hiy == var2 > 0) {
            Region var7 = new Region(var3, var4, var5, var6);
            int[] var8 = this.bands;
            if (var8 != null) {
               int var9 = this.endIndex;
               var7.endIndex = var9;
               int[] var10 = new int[var9];
               var7.bands = var10;
               int var11 = 0;

               while(var11 < var9) {
                  var10[var11] = var8[var11] + var2;
                  ++var11;
                  var10[var11] = var8[var11] + var2;
                  ++var11;
                  int var12;
                  var10[var11] = var12 = var8[var11];
                  ++var11;

                  while(true) {
                     --var12;
                     if (var12 < 0) {
                        break;
                     }

                     var10[var11] = var8[var11] + var1;
                     ++var11;
                     var10[var11] = var8[var11] + var1;
                     ++var11;
                  }
               }
            }

            return var7;
         } else {
            return this.getSafeTranslatedRegion(var1, var2);
         }
      }
   }

   private Region getSafeTranslatedRegion(int var1, int var2) {
      int var3 = clipAdd(this.lox, var1);
      int var4 = clipAdd(this.loy, var2);
      int var5 = clipAdd(this.hix, var1);
      int var6 = clipAdd(this.hiy, var2);
      Region var7 = new Region(var3, var4, var5, var6);
      int[] var8 = this.bands;
      if (var8 != null) {
         int var9 = this.endIndex;
         int[] var10 = new int[var9];
         int var11 = 0;
         int var12 = 0;

         while(var11 < var9) {
            int var14;
            var10[var12++] = var14 = clipAdd(var8[var11++], var2);
            int var15;
            var10[var12++] = var15 = clipAdd(var8[var11++], var2);
            int var13;
            var10[var12++] = var13 = var8[var11++];
            int var16 = var12;
            if (var14 < var15) {
               while(true) {
                  --var13;
                  if (var13 < 0) {
                     break;
                  }

                  int var17 = clipAdd(var8[var11++], var1);
                  int var18 = clipAdd(var8[var11++], var1);
                  if (var17 < var18) {
                     var10[var12++] = var17;
                     var10[var12++] = var18;
                  }
               }
            } else {
               var11 += var13 * 2;
            }

            if (var12 > var16) {
               var10[var16 - 1] = (var12 - var16) / 2;
            } else {
               var12 = var16 - 3;
            }
         }

         if (var12 <= 5) {
            if (var12 < 5) {
               var7.lox = var7.loy = var7.hix = var7.hiy = 0;
            } else {
               var7.loy = var10[0];
               var7.hiy = var10[1];
               var7.lox = var10[3];
               var7.hix = var10[4];
            }
         } else {
            var7.endIndex = var12;
            var7.bands = var10;
         }
      }

      return var7;
   }

   public Region getIntersection(Rectangle var1) {
      return this.getIntersectionXYWH(var1.x, var1.y, var1.width, var1.height);
   }

   public Region getIntersectionXYWH(int var1, int var2, int var3, int var4) {
      return this.getIntersectionXYXY(var1, var2, dimAdd(var1, var3), dimAdd(var2, var4));
   }

   public Region getIntersectionXYXY(int var1, int var2, int var3, int var4) {
      if (this.isInsideXYXY(var1, var2, var3, var4)) {
         return this;
      } else {
         Region var5 = new Region(var1 < this.lox ? this.lox : var1, var2 < this.loy ? this.loy : var2, var3 > this.hix ? this.hix : var3, var4 > this.hiy ? this.hiy : var4);
         if (this.bands != null) {
            var5.appendSpans(this.getSpanIterator());
         }

         return var5;
      }
   }

   public Region getIntersection(Region var1) {
      if (this.isInsideQuickCheck(var1)) {
         return this;
      } else if (var1.isInsideQuickCheck(this)) {
         return var1;
      } else {
         Region var2 = new Region(var1.lox < this.lox ? this.lox : var1.lox, var1.loy < this.loy ? this.loy : var1.loy, var1.hix > this.hix ? this.hix : var1.hix, var1.hiy > this.hiy ? this.hiy : var1.hiy);
         if (!var2.isEmpty()) {
            var2.filterSpans(this, var1, 4);
         }

         return var2;
      }
   }

   public Region getUnion(Region var1) {
      if (!var1.isEmpty() && !var1.isInsideQuickCheck(this)) {
         if (!this.isEmpty() && !this.isInsideQuickCheck(var1)) {
            Region var2 = new Region(var1.lox > this.lox ? this.lox : var1.lox, var1.loy > this.loy ? this.loy : var1.loy, var1.hix < this.hix ? this.hix : var1.hix, var1.hiy < this.hiy ? this.hiy : var1.hiy);
            var2.filterSpans(this, var1, 7);
            return var2;
         } else {
            return var1;
         }
      } else {
         return this;
      }
   }

   public Region getDifference(Region var1) {
      if (!var1.intersectsQuickCheck(this)) {
         return this;
      } else if (this.isInsideQuickCheck(var1)) {
         return EMPTY_REGION;
      } else {
         Region var2 = new Region(this.lox, this.loy, this.hix, this.hiy);
         var2.filterSpans(this, var1, 1);
         return var2;
      }
   }

   public Region getExclusiveOr(Region var1) {
      if (var1.isEmpty()) {
         return this;
      } else if (this.isEmpty()) {
         return var1;
      } else {
         Region var2 = new Region(var1.lox > this.lox ? this.lox : var1.lox, var1.loy > this.loy ? this.loy : var1.loy, var1.hix < this.hix ? this.hix : var1.hix, var1.hiy < this.hiy ? this.hiy : var1.hiy);
         var2.filterSpans(this, var1, 3);
         return var2;
      }
   }

   private void filterSpans(Region var1, Region var2, int var3) {
      int[] var4 = var1.bands;
      int[] var5 = var2.bands;
      if (var4 == null) {
         var4 = new int[]{var1.loy, var1.hiy, 1, var1.lox, var1.hix};
      }

      if (var5 == null) {
         var5 = new int[]{var2.loy, var2.hiy, 1, var2.lox, var2.hix};
      }

      int[] var6 = new int[6];
      byte var7 = 0;
      int var26 = var7 + 1;
      int var8 = var4[var7];
      int var9 = var4[var26++];
      int var10 = var4[var26++];
      var10 = var26 + 2 * var10;
      byte var11 = 0;
      int var27 = var11 + 1;
      int var12 = var5[var11];
      int var13 = var5[var27++];
      int var14 = var5[var27++];
      var14 = var27 + 2 * var14;
      int var15 = this.loy;

      while(var15 < this.hiy) {
         if (var15 >= var9) {
            if (var10 < var1.endIndex) {
               var26 = var10 + 1;
               var8 = var4[var10];
               var9 = var4[var26++];
               var10 = var4[var26++];
               var10 = var26 + 2 * var10;
            } else {
               if ((var3 & 2) == 0) {
                  break;
               }

               var8 = var9 = this.hiy;
            }
         } else if (var15 >= var13) {
            if (var14 < var2.endIndex) {
               var27 = var14 + 1;
               var12 = var5[var14];
               var13 = var5[var27++];
               var14 = var5[var27++];
               var14 = var27 + 2 * var14;
            } else {
               if ((var3 & 1) == 0) {
                  break;
               }

               var12 = var13 = this.hiy;
            }
         } else {
            int var16;
            int var17;
            if (var15 < var12) {
               if (var15 < var8) {
                  var15 = Math.min(var8, var12);
                  continue;
               }

               var16 = Math.min(var9, var12);
               if ((var3 & 1) != 0) {
                  var6[1] = var15;
                  var6[3] = var16;
                  var17 = var26;

                  while(var17 < var10) {
                     var6[0] = var4[var17++];
                     var6[2] = var4[var17++];
                     this.appendSpan(var6);
                  }
               }
            } else if (var15 < var8) {
               var16 = Math.min(var13, var8);
               if ((var3 & 2) != 0) {
                  var6[1] = var15;
                  var6[3] = var16;
                  var17 = var27;

                  while(var17 < var14) {
                     var6[0] = var5[var17++];
                     var6[2] = var5[var17++];
                     this.appendSpan(var6);
                  }
               }
            } else {
               var16 = Math.min(var9, var13);
               var6[1] = var15;
               var6[3] = var16;
               var17 = var26 + 1;
               int var19 = var4[var26];
               int var20 = var4[var17++];
               int var18 = var27 + 1;
               int var21 = var5[var27];
               int var22 = var5[var18++];
               int var23 = Math.min(var19, var21);
               if (var23 < this.lox) {
                  var23 = this.lox;
               }

               while(var23 < this.hix) {
                  if (var23 >= var20) {
                     if (var17 < var10) {
                        var19 = var4[var17++];
                        var20 = var4[var17++];
                     } else {
                        if ((var3 & 2) == 0) {
                           break;
                        }

                        var19 = var20 = this.hix;
                     }
                  } else if (var23 >= var22) {
                     if (var18 < var14) {
                        var21 = var5[var18++];
                        var22 = var5[var18++];
                     } else {
                        if ((var3 & 1) == 0) {
                           break;
                        }

                        var21 = var22 = this.hix;
                     }
                  } else {
                     int var24;
                     boolean var25;
                     if (var23 < var21) {
                        if (var23 < var19) {
                           var24 = Math.min(var19, var21);
                           var25 = false;
                        } else {
                           var24 = Math.min(var20, var21);
                           var25 = (var3 & 1) != 0;
                        }
                     } else if (var23 < var19) {
                        var24 = Math.min(var19, var22);
                        var25 = (var3 & 2) != 0;
                     } else {
                        var24 = Math.min(var20, var22);
                        var25 = (var3 & 4) != 0;
                     }

                     if (var25) {
                        var6[0] = var23;
                        var6[2] = var24;
                        this.appendSpan(var6);
                     }

                     var23 = var24;
                  }
               }
            }

            var15 = var16;
         }
      }

      this.endRow(var6);
      this.calcBBox();
   }

   public Region getBoundsIntersection(Rectangle var1) {
      return this.getBoundsIntersectionXYWH(var1.x, var1.y, var1.width, var1.height);
   }

   public Region getBoundsIntersectionXYWH(int var1, int var2, int var3, int var4) {
      return this.getBoundsIntersectionXYXY(var1, var2, dimAdd(var1, var3), dimAdd(var2, var4));
   }

   public Region getBoundsIntersectionXYXY(int var1, int var2, int var3, int var4) {
      return this.bands == null && this.lox >= var1 && this.loy >= var2 && this.hix <= var3 && this.hiy <= var4 ? this : new Region(var1 < this.lox ? this.lox : var1, var2 < this.loy ? this.loy : var2, var3 > this.hix ? this.hix : var3, var4 > this.hiy ? this.hiy : var4);
   }

   public Region getBoundsIntersection(Region var1) {
      if (this.encompasses(var1)) {
         return var1;
      } else {
         return var1.encompasses(this) ? this : new Region(var1.lox < this.lox ? this.lox : var1.lox, var1.loy < this.loy ? this.loy : var1.loy, var1.hix > this.hix ? this.hix : var1.hix, var1.hiy > this.hiy ? this.hiy : var1.hiy);
      }
   }

   private void appendSpan(int[] var1) {
      int var2;
      if ((var2 = var1[0]) < this.lox) {
         var2 = this.lox;
      }

      int var3;
      if ((var3 = var1[1]) < this.loy) {
         var3 = this.loy;
      }

      int var4;
      if ((var4 = var1[2]) > this.hix) {
         var4 = this.hix;
      }

      int var5;
      if ((var5 = var1[3]) > this.hiy) {
         var5 = this.hiy;
      }

      if (var4 > var2 && var5 > var3) {
         int var6 = var1[4];
         if (this.endIndex != 0 && var3 < this.bands[var6 + 1]) {
            if (var3 != this.bands[var6] || var5 != this.bands[var6 + 1] || var2 < this.bands[this.endIndex - 1]) {
               throw new InternalError("bad span");
            }

            if (var2 == this.bands[this.endIndex - 1]) {
               this.bands[this.endIndex - 1] = var4;
               return;
            }

            this.needSpace(2);
         } else {
            if (this.bands == null) {
               this.bands = new int[50];
            } else {
               this.needSpace(5);
               this.endRow(var1);
               var6 = var1[4];
            }

            this.bands[this.endIndex++] = var3;
            this.bands[this.endIndex++] = var5;
            this.bands[this.endIndex++] = 0;
         }

         this.bands[this.endIndex++] = var2;
         this.bands[this.endIndex++] = var4;
         int var10002 = this.bands[var6 + 2]++;
      }
   }

   private void needSpace(int var1) {
      if (this.endIndex + var1 >= this.bands.length) {
         int[] var2 = new int[this.bands.length + 50];
         System.arraycopy(this.bands, 0, var2, 0, this.endIndex);
         this.bands = var2;
      }

   }

   private void endRow(int[] var1) {
      int var2 = var1[4];
      int var3 = var1[5];
      if (var2 > var3) {
         int[] var4 = this.bands;
         if (var4[var3 + 1] == var4[var2] && var4[var3 + 2] == var4[var2 + 2]) {
            int var5 = var4[var2 + 2] * 2;
            var2 += 3;

            for(var3 += 3; var5 > 0 && var4[var2++] == var4[var3++]; --var5) {
            }

            if (var5 == 0) {
               var4[var1[5] + 1] = var4[var3 + 1];
               this.endIndex = var3;
               return;
            }
         }
      }

      var1[5] = var1[4];
      var1[4] = this.endIndex;
   }

   private void calcBBox() {
      int[] var1 = this.bands;
      if (this.endIndex <= 5) {
         if (this.endIndex == 0) {
            this.lox = this.loy = this.hix = this.hiy = 0;
         } else {
            this.loy = var1[0];
            this.hiy = var1[1];
            this.lox = var1[3];
            this.hix = var1[4];
            this.endIndex = 0;
         }

         this.bands = null;
      } else {
         int var2 = this.hix;
         int var3 = this.lox;
         int var4 = 0;
         int var5 = 0;

         while(var5 < this.endIndex) {
            var4 = var5;
            int var6 = var1[var5 + 2];
            var5 += 3;
            if (var2 > var1[var5]) {
               var2 = var1[var5];
            }

            var5 += var6 * 2;
            if (var3 < var1[var5 - 1]) {
               var3 = var1[var5 - 1];
            }
         }

         this.lox = var2;
         this.loy = var1[0];
         this.hix = var3;
         this.hiy = var1[var4 + 1];
      }
   }

   public final int getLoX() {
      return this.lox;
   }

   public final int getLoY() {
      return this.loy;
   }

   public final int getHiX() {
      return this.hix;
   }

   public final int getHiY() {
      return this.hiy;
   }

   public final int getWidth() {
      if (this.hix < this.lox) {
         return 0;
      } else {
         int var1;
         if ((var1 = this.hix - this.lox) < 0) {
            var1 = Integer.MAX_VALUE;
         }

         return var1;
      }
   }

   public final int getHeight() {
      if (this.hiy < this.loy) {
         return 0;
      } else {
         int var1;
         if ((var1 = this.hiy - this.loy) < 0) {
            var1 = Integer.MAX_VALUE;
         }

         return var1;
      }
   }

   public boolean isEmpty() {
      return this.hix <= this.lox || this.hiy <= this.loy;
   }

   public boolean isRectangular() {
      return this.bands == null;
   }

   public boolean contains(int var1, int var2) {
      if (var1 >= this.lox && var1 < this.hix && var2 >= this.loy && var2 < this.hiy) {
         if (this.bands == null) {
            return true;
         } else {
            int var4;
            for(int var3 = 0; var3 < this.endIndex; var3 += var4 * 2) {
               if (var2 < this.bands[var3++]) {
                  return false;
               }

               if (var2 < this.bands[var3++]) {
                  var4 = this.bands[var3++];
                  var4 = var3 + var4 * 2;

                  do {
                     if (var3 >= var4) {
                        return false;
                     }

                     if (var1 < this.bands[var3++]) {
                        return false;
                     }
                  } while(var1 >= this.bands[var3++]);

                  return true;
               }

               var4 = this.bands[var3++];
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isInsideXYWH(int var1, int var2, int var3, int var4) {
      return this.isInsideXYXY(var1, var2, dimAdd(var1, var3), dimAdd(var2, var4));
   }

   public boolean isInsideXYXY(int var1, int var2, int var3, int var4) {
      return this.lox >= var1 && this.loy >= var2 && this.hix <= var3 && this.hiy <= var4;
   }

   public boolean isInsideQuickCheck(Region var1) {
      return var1.bands == null && var1.lox <= this.lox && var1.loy <= this.loy && var1.hix >= this.hix && var1.hiy >= this.hiy;
   }

   public boolean intersectsQuickCheckXYXY(int var1, int var2, int var3, int var4) {
      return var3 > this.lox && var1 < this.hix && var4 > this.loy && var2 < this.hiy;
   }

   public boolean intersectsQuickCheck(Region var1) {
      return var1.hix > this.lox && var1.lox < this.hix && var1.hiy > this.loy && var1.loy < this.hiy;
   }

   public boolean encompasses(Region var1) {
      return this.bands == null && this.lox <= var1.lox && this.loy <= var1.loy && this.hix >= var1.hix && this.hiy >= var1.hiy;
   }

   public boolean encompassesXYWH(int var1, int var2, int var3, int var4) {
      return this.encompassesXYXY(var1, var2, dimAdd(var1, var3), dimAdd(var2, var4));
   }

   public boolean encompassesXYXY(int var1, int var2, int var3, int var4) {
      return this.bands == null && this.lox <= var1 && this.loy <= var2 && this.hix >= var3 && this.hiy >= var4;
   }

   public void getBounds(int[] var1) {
      var1[0] = this.lox;
      var1[1] = this.loy;
      var1[2] = this.hix;
      var1[3] = this.hiy;
   }

   public void clipBoxToBounds(int[] var1) {
      if (var1[0] < this.lox) {
         var1[0] = this.lox;
      }

      if (var1[1] < this.loy) {
         var1[1] = this.loy;
      }

      if (var1[2] > this.hix) {
         var1[2] = this.hix;
      }

      if (var1[3] > this.hiy) {
         var1[3] = this.hiy;
      }

   }

   public RegionIterator getIterator() {
      return new RegionIterator(this);
   }

   public SpanIterator getSpanIterator() {
      return new RegionSpanIterator(this);
   }

   public SpanIterator getSpanIterator(int[] var1) {
      SpanIterator var2 = this.getSpanIterator();
      var2.intersectClipBox(var1[0], var1[1], var1[2], var1[3]);
      return var2;
   }

   public SpanIterator filter(SpanIterator var1) {
      if (this.bands == null) {
         ((SpanIterator)var1).intersectClipBox(this.lox, this.loy, this.hix, this.hiy);
      } else {
         var1 = new RegionClipSpanIterator(this, (SpanIterator)var1);
      }

      return (SpanIterator)var1;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("Region[[");
      var1.append(this.lox);
      var1.append(", ");
      var1.append(this.loy);
      var1.append(" => ");
      var1.append(this.hix);
      var1.append(", ");
      var1.append(this.hiy);
      var1.append("]");
      if (this.bands != null) {
         int var2 = 0;

         while(var2 < this.endIndex) {
            var1.append("y{");
            var1.append(this.bands[var2++]);
            var1.append(",");
            var1.append(this.bands[var2++]);
            var1.append("}[");
            int var3 = this.bands[var2++];
            var3 = var2 + var3 * 2;

            while(var2 < var3) {
               var1.append("x(");
               var1.append(this.bands[var2++]);
               var1.append(", ");
               var1.append(this.bands[var2++]);
               var1.append(")");
            }

            var1.append("]");
         }
      }

      var1.append("]");
      return var1.toString();
   }

   public int hashCode() {
      return this.isEmpty() ? 0 : this.lox * 3 + this.loy * 5 + this.hix * 7 + this.hiy * 9;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Region)) {
         return false;
      } else {
         Region var2 = (Region)var1;
         if (this.isEmpty()) {
            return var2.isEmpty();
         } else if (var2.isEmpty()) {
            return false;
         } else if (var2.lox == this.lox && var2.loy == this.loy && var2.hix == this.hix && var2.hiy == this.hiy) {
            if (this.bands == null) {
               return var2.bands == null;
            } else if (var2.bands == null) {
               return false;
            } else if (this.endIndex != var2.endIndex) {
               return false;
            } else {
               int[] var3 = this.bands;
               int[] var4 = var2.bands;

               for(int var5 = 0; var5 < this.endIndex; ++var5) {
                  if (var3[var5] != var4[var5]) {
                     return false;
                  }
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   static {
      initIDs();
   }

   private static final class ImmutableRegion extends Region {
      protected ImmutableRegion(int var1, int var2, int var3, int var4) {
         super(var1, var2, var3, var4);
      }

      public void appendSpans(SpanIterator var1) {
      }

      public void setOutputArea(Rectangle var1) {
      }

      public void setOutputAreaXYWH(int var1, int var2, int var3, int var4) {
      }

      public void setOutputArea(int[] var1) {
      }

      public void setOutputAreaXYXY(int var1, int var2, int var3, int var4) {
      }
   }
}
