package java.awt.font;

class TextJustifier {
   private GlyphJustificationInfo[] info;
   private int start;
   private int limit;
   static boolean DEBUG = false;
   public static final int MAX_PRIORITY = 3;

   TextJustifier(GlyphJustificationInfo[] var1, int var2, int var3) {
      this.info = var1;
      this.start = var2;
      this.limit = var3;
      if (DEBUG) {
         System.out.println("start: " + var2 + ", limit: " + var3);

         for(int var4 = var2; var4 < var3; ++var4) {
            GlyphJustificationInfo var5 = var1[var4];
            System.out.println("w: " + var5.weight + ", gp: " + var5.growPriority + ", gll: " + var5.growLeftLimit + ", grl: " + var5.growRightLimit);
         }
      }

   }

   public float[] justify(float var1) {
      float[] var2 = new float[this.info.length * 2];
      boolean var3 = var1 > 0.0F;
      if (DEBUG) {
         System.out.println("delta: " + var1);
      }

      int var4 = -1;

      for(int var5 = 0; var1 != 0.0F; ++var5) {
         boolean var6 = var5 > 3;
         if (var6) {
            var5 = var4;
         }

         float var7 = 0.0F;
         float var8 = 0.0F;
         float var9 = 0.0F;

         for(int var10 = this.start; var10 < this.limit; ++var10) {
            GlyphJustificationInfo var11 = this.info[var10];
            if ((var3 ? var11.growPriority : var11.shrinkPriority) == var5) {
               if (var4 == -1) {
                  var4 = var5;
               }

               if (var10 != this.start) {
                  var7 += var11.weight;
                  if (var3) {
                     var8 += var11.growLeftLimit;
                     if (var11.growAbsorb) {
                        var9 += var11.weight;
                     }
                  } else {
                     var8 += var11.shrinkLeftLimit;
                     if (var11.shrinkAbsorb) {
                        var9 += var11.weight;
                     }
                  }
               }

               if (var10 + 1 != this.limit) {
                  var7 += var11.weight;
                  if (var3) {
                     var8 += var11.growRightLimit;
                     if (var11.growAbsorb) {
                        var9 += var11.weight;
                     }
                  } else {
                     var8 += var11.shrinkRightLimit;
                     if (var11.shrinkAbsorb) {
                        var9 += var11.weight;
                     }
                  }
               }
            }
         }

         if (!var3) {
            var8 = -var8;
         }

         boolean var20 = var7 == 0.0F || !var6 && var1 < 0.0F == var1 < var8;
         boolean var21 = var20 && var9 > 0.0F;
         float var12 = var1 / var7;
         float var13 = 0.0F;
         if (var20 && var9 > 0.0F) {
            var13 = (var1 - var8) / var9;
         }

         if (DEBUG) {
            System.out.println("pass: " + var5 + ", d: " + var1 + ", l: " + var8 + ", w: " + var7 + ", aw: " + var9 + ", wd: " + var12 + ", wa: " + var13 + ", hit: " + (var20 ? "y" : "n"));
         }

         int var14 = this.start * 2;

         for(int var15 = this.start; var15 < this.limit; ++var15) {
            GlyphJustificationInfo var16 = this.info[var15];
            if ((var3 ? var16.growPriority : var16.shrinkPriority) == var5) {
               float var17;
               if (var15 != this.start) {
                  if (var20) {
                     var17 = var3 ? var16.growLeftLimit : -var16.shrinkLeftLimit;
                     if (var21) {
                        var17 += var16.weight * var13;
                     }
                  } else {
                     var17 = var16.weight * var12;
                  }

                  var2[var14] += var17;
               }

               ++var14;
               if (var15 + 1 != this.limit) {
                  if (var20) {
                     var17 = var3 ? var16.growRightLimit : -var16.shrinkRightLimit;
                     if (var21) {
                        var17 += var16.weight * var13;
                     }
                  } else {
                     var17 = var16.weight * var12;
                  }

                  var2[var14] += var17;
               }

               ++var14;
            } else {
               var14 += 2;
            }
         }

         if (!var6 && var20 && !var21) {
            var1 -= var8;
         } else {
            var1 = 0.0F;
         }
      }

      if (DEBUG) {
         float var19 = 0.0F;

         for(int var18 = 0; var18 < var2.length; ++var18) {
            var19 += var2[var18];
            System.out.print(var2[var18] + ", ");
            if (var18 % 20 == 9) {
               System.out.println();
            }
         }

         System.out.println("\ntotal: " + var19);
         System.out.println();
      }

      return var2;
   }
}
