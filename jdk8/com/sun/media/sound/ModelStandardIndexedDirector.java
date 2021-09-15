package com.sun.media.sound;

import java.util.Arrays;

public final class ModelStandardIndexedDirector implements ModelDirector {
   private final ModelPerformer[] performers;
   private final ModelDirectedPlayer player;
   private boolean noteOnUsed = false;
   private boolean noteOffUsed = false;
   private byte[][] trantables;
   private int[] counters;
   private int[][] mat;

   public ModelStandardIndexedDirector(ModelPerformer[] var1, ModelDirectedPlayer var2) {
      this.performers = (ModelPerformer[])Arrays.copyOf((Object[])var1, var1.length);
      this.player = var2;
      ModelPerformer[] var3 = this.performers;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ModelPerformer var6 = var3[var5];
         if (var6.isReleaseTriggered()) {
            this.noteOffUsed = true;
         } else {
            this.noteOnUsed = true;
         }
      }

      this.buildindex();
   }

   private int[] lookupIndex(int var1, int var2) {
      if (var1 >= 0 && var1 < 128 && var2 >= 0 && var2 < 128) {
         byte var3 = this.trantables[0][var1];
         byte var4 = this.trantables[1][var2];
         if (var3 != -1 && var4 != -1) {
            return this.mat[var3 + var4 * this.counters[0]];
         }
      }

      return null;
   }

   private int restrict(int var1) {
      if (var1 < 0) {
         return 0;
      } else {
         return var1 > 127 ? 127 : var1;
      }
   }

   private void buildindex() {
      this.trantables = new byte[2][129];
      this.counters = new int[this.trantables.length];
      ModelPerformer[] var1 = this.performers;
      int var2 = var1.length;

      int var3;
      int var5;
      int var6;
      int var7;
      int var8;
      for(var3 = 0; var3 < var2; ++var3) {
         ModelPerformer var4 = var1[var3];
         var5 = var4.getKeyFrom();
         var6 = var4.getKeyTo();
         var7 = var4.getVelFrom();
         var8 = var4.getVelTo();
         if (var5 <= var6 && var7 <= var8) {
            var5 = this.restrict(var5);
            var6 = this.restrict(var6);
            var7 = this.restrict(var7);
            var8 = this.restrict(var8);
            this.trantables[0][var5] = 1;
            this.trantables[0][var6 + 1] = 1;
            this.trantables[1][var7] = 1;
            this.trantables[1][var8 + 1] = 1;
         }
      }

      int var20;
      int var23;
      for(var20 = 0; var20 < this.trantables.length; ++var20) {
         byte[] var21 = this.trantables[var20];
         var3 = var21.length;

         for(var23 = var3 - 1; var23 >= 0; --var23) {
            if (var21[var23] == 1) {
               var21[var23] = -1;
               break;
            }

            var21[var23] = -1;
         }

         var23 = -1;

         for(var5 = 0; var5 < var3; ++var5) {
            if (var21[var5] != 0) {
               ++var23;
               if (var21[var5] == -1) {
                  break;
               }
            }

            var21[var5] = (byte)var23;
         }

         this.counters[var20] = var23;
      }

      this.mat = new int[this.counters[0] * this.counters[1]][];
      var20 = 0;
      ModelPerformer[] var22 = this.performers;
      var3 = var22.length;

      for(var23 = 0; var23 < var3; ++var23) {
         ModelPerformer var24 = var22[var23];
         var6 = var24.getKeyFrom();
         var7 = var24.getKeyTo();
         var8 = var24.getVelFrom();
         int var9 = var24.getVelTo();
         if (var6 <= var7 && var8 <= var9) {
            var6 = this.restrict(var6);
            var7 = this.restrict(var7);
            var8 = this.restrict(var8);
            var9 = this.restrict(var9);
            byte var10 = this.trantables[0][var6];
            int var11 = this.trantables[0][var7 + 1];
            byte var12 = this.trantables[1][var8];
            int var13 = this.trantables[1][var9 + 1];
            if (var11 == -1) {
               var11 = this.counters[0];
            }

            if (var13 == -1) {
               var13 = this.counters[1];
            }

            for(int var14 = var12; var14 < var13; ++var14) {
               int var15 = var10 + var14 * this.counters[0];

               for(int var16 = var10; var16 < var11; ++var16) {
                  int[] var17 = this.mat[var15];
                  if (var17 == null) {
                     this.mat[var15] = new int[]{var20};
                  } else {
                     int[] var18 = new int[var17.length + 1];
                     var18[var18.length - 1] = var20;

                     for(int var19 = 0; var19 < var17.length; ++var19) {
                        var18[var19] = var17[var19];
                     }

                     this.mat[var15] = var18;
                  }

                  ++var15;
               }
            }

            ++var20;
         }
      }

   }

   public void close() {
   }

   public void noteOff(int var1, int var2) {
      if (this.noteOffUsed) {
         int[] var3 = this.lookupIndex(var1, var2);
         if (var3 != null) {
            int[] var4 = var3;
            int var5 = var3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               int var7 = var4[var6];
               ModelPerformer var8 = this.performers[var7];
               if (var8.isReleaseTriggered()) {
                  this.player.play(var7, (ModelConnectionBlock[])null);
               }
            }

         }
      }
   }

   public void noteOn(int var1, int var2) {
      if (this.noteOnUsed) {
         int[] var3 = this.lookupIndex(var1, var2);
         if (var3 != null) {
            int[] var4 = var3;
            int var5 = var3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               int var7 = var4[var6];
               ModelPerformer var8 = this.performers[var7];
               if (!var8.isReleaseTriggered()) {
                  this.player.play(var7, (ModelConnectionBlock[])null);
               }
            }

         }
      }
   }
}
