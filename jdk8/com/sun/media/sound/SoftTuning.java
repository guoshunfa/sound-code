package com.sun.media.sound;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import javax.sound.midi.Patch;

public final class SoftTuning {
   private String name = null;
   private final double[] tuning = new double[128];
   private Patch patch = null;

   public SoftTuning() {
      this.name = "12-TET";

      for(int var1 = 0; var1 < this.tuning.length; ++var1) {
         this.tuning[var1] = (double)(var1 * 100);
      }

   }

   public SoftTuning(byte[] var1) {
      for(int var2 = 0; var2 < this.tuning.length; ++var2) {
         this.tuning[var2] = (double)(var2 * 100);
      }

      this.load(var1);
   }

   public SoftTuning(Patch var1) {
      this.patch = var1;
      this.name = "12-TET";

      for(int var2 = 0; var2 < this.tuning.length; ++var2) {
         this.tuning[var2] = (double)(var2 * 100);
      }

   }

   public SoftTuning(Patch var1, byte[] var2) {
      this.patch = var1;

      for(int var3 = 0; var3 < this.tuning.length; ++var3) {
         this.tuning[var3] = (double)(var3 * 100);
      }

      this.load(var2);
   }

   private boolean checksumOK(byte[] var1) {
      int var2 = var1[1] & 255;

      for(int var3 = 2; var3 < var1.length - 2; ++var3) {
         var2 ^= var1[var3] & 255;
      }

      return (var1[var1.length - 2] & 255) == (var2 & 127);
   }

   public void load(byte[] var1) {
      if ((var1[1] & 255) == 126 || (var1[1] & 255) == 127) {
         int var2 = var1[3] & 255;
         switch(var2) {
         case 8:
            int var3 = var1[4] & 255;
            int var4;
            int var5;
            int var7;
            int var8;
            int var9;
            int var10;
            int var18;
            switch(var3) {
            case 1:
               try {
                  this.name = new String(var1, 6, 16, "ascii");
               } catch (UnsupportedEncodingException var14) {
                  this.name = null;
               }

               var4 = 22;

               for(var5 = 0; var5 < 128; ++var5) {
                  var18 = var1[var4++] & 255;
                  var7 = var1[var4++] & 255;
                  var8 = var1[var4++] & 255;
                  if (var18 != 127 || var7 != 127 || var8 != 127) {
                     this.tuning[var5] = 100.0D * ((double)(var18 * 16384 + var7 * 128 + var8) / 16384.0D);
                  }
               }

               return;
            case 2:
               var4 = var1[6] & 255;
               var5 = 7;

               for(var18 = 0; var18 < var4; ++var18) {
                  var7 = var1[var5++] & 255;
                  var8 = var1[var5++] & 255;
                  var9 = var1[var5++] & 255;
                  var10 = var1[var5++] & 255;
                  if (var8 != 127 || var9 != 127 || var10 != 127) {
                     this.tuning[var7] = 100.0D * ((double)(var8 * 16384 + var9 * 128 + var10) / 16384.0D);
                  }
               }
            case 3:
            default:
               break;
            case 4:
               if (this.checksumOK(var1)) {
                  try {
                     this.name = new String(var1, 7, 16, "ascii");
                  } catch (UnsupportedEncodingException var13) {
                     this.name = null;
                  }

                  var4 = 23;

                  for(var5 = 0; var5 < 128; ++var5) {
                     var18 = var1[var4++] & 255;
                     var7 = var1[var4++] & 255;
                     var8 = var1[var4++] & 255;
                     if (var18 != 127 || var7 != 127 || var8 != 127) {
                        this.tuning[var5] = 100.0D * ((double)(var18 * 16384 + var7 * 128 + var8) / 16384.0D);
                     }
                  }
               }
               break;
            case 5:
               if (this.checksumOK(var1)) {
                  try {
                     this.name = new String(var1, 7, 16, "ascii");
                  } catch (UnsupportedEncodingException var12) {
                     this.name = null;
                  }

                  int[] var16 = new int[12];

                  for(var5 = 0; var5 < 12; ++var5) {
                     var16[var5] = (var1[var5 + 23] & 255) - 64;
                  }

                  for(var5 = 0; var5 < this.tuning.length; ++var5) {
                     this.tuning[var5] = (double)(var5 * 100 + var16[var5 % 12]);
                  }
               }
               break;
            case 6:
               if (this.checksumOK(var1)) {
                  try {
                     this.name = new String(var1, 7, 16, "ascii");
                  } catch (UnsupportedEncodingException var11) {
                     this.name = null;
                  }

                  double[] var15 = new double[12];

                  for(var5 = 0; var5 < 12; ++var5) {
                     var18 = (var1[var5 * 2 + 23] & 255) * 128 + (var1[var5 * 2 + 24] & 255);
                     var15[var5] = ((double)var18 / 8192.0D - 1.0D) * 100.0D;
                  }

                  for(var5 = 0; var5 < this.tuning.length; ++var5) {
                     this.tuning[var5] = (double)(var5 * 100) + var15[var5 % 12];
                  }
               }
               break;
            case 7:
               var4 = var1[7] & 255;
               var5 = 8;

               for(var18 = 0; var18 < var4; ++var18) {
                  var7 = var1[var5++] & 255;
                  var8 = var1[var5++] & 255;
                  var9 = var1[var5++] & 255;
                  var10 = var1[var5++] & 255;
                  if (var8 != 127 || var9 != 127 || var10 != 127) {
                     this.tuning[var7] = 100.0D * ((double)(var8 * 16384 + var9 * 128 + var10) / 16384.0D);
                  }
               }

               return;
            case 8:
               int[] var17 = new int[12];

               for(var7 = 0; var7 < 12; ++var7) {
                  var17[var7] = (var1[var7 + 8] & 255) - 64;
               }

               for(var7 = 0; var7 < this.tuning.length; ++var7) {
                  this.tuning[var7] = (double)(var7 * 100 + var17[var7 % 12]);
               }

               return;
            case 9:
               double[] var6 = new double[12];

               for(var7 = 0; var7 < 12; ++var7) {
                  var8 = (var1[var7 * 2 + 8] & 255) * 128 + (var1[var7 * 2 + 9] & 255);
                  var6[var7] = ((double)var8 / 8192.0D - 1.0D) * 100.0D;
               }

               for(var7 = 0; var7 < this.tuning.length; ++var7) {
                  this.tuning[var7] = (double)(var7 * 100) + var6[var7 % 12];
               }
            }
         }
      }

   }

   public double[] getTuning() {
      return Arrays.copyOf(this.tuning, this.tuning.length);
   }

   public double getTuning(int var1) {
      return this.tuning[var1];
   }

   public Patch getPatch() {
      return this.patch;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }
}
