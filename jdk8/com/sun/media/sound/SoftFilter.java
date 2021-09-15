package com.sun.media.sound;

public final class SoftFilter {
   public static final int FILTERTYPE_LP6 = 0;
   public static final int FILTERTYPE_LP12 = 1;
   public static final int FILTERTYPE_HP12 = 17;
   public static final int FILTERTYPE_BP12 = 33;
   public static final int FILTERTYPE_NP12 = 49;
   public static final int FILTERTYPE_LP24 = 3;
   public static final int FILTERTYPE_HP24 = 19;
   private int filtertype = 0;
   private final float samplerate;
   private float x1;
   private float x2;
   private float y1;
   private float y2;
   private float xx1;
   private float xx2;
   private float yy1;
   private float yy2;
   private float a0;
   private float a1;
   private float a2;
   private float b1;
   private float b2;
   private float q;
   private float gain = 1.0F;
   private float wet = 0.0F;
   private float last_wet = 0.0F;
   private float last_a0;
   private float last_a1;
   private float last_a2;
   private float last_b1;
   private float last_b2;
   private float last_q;
   private float last_gain;
   private boolean last_set = false;
   private double cutoff = 44100.0D;
   private double resonancedB = 0.0D;
   private boolean dirty = true;

   public SoftFilter(float var1) {
      this.samplerate = var1;
      this.dirty = true;
   }

   public void setFrequency(double var1) {
      if (this.cutoff != var1) {
         this.cutoff = var1;
         this.dirty = true;
      }
   }

   public void setResonance(double var1) {
      if (this.resonancedB != var1) {
         this.resonancedB = var1;
         this.dirty = true;
      }
   }

   public void reset() {
      this.dirty = true;
      this.last_set = false;
      this.x1 = 0.0F;
      this.x2 = 0.0F;
      this.y1 = 0.0F;
      this.y2 = 0.0F;
      this.xx1 = 0.0F;
      this.xx2 = 0.0F;
      this.yy1 = 0.0F;
      this.yy2 = 0.0F;
      this.wet = 0.0F;
      this.gain = 1.0F;
      this.a0 = 0.0F;
      this.a1 = 0.0F;
      this.a2 = 0.0F;
      this.b1 = 0.0F;
      this.b2 = 0.0F;
   }

   public void setFilterType(int var1) {
      this.filtertype = var1;
   }

   public void processAudio(SoftAudioBuffer var1) {
      if (this.filtertype == 0) {
         this.filter1(var1);
      }

      if (this.filtertype == 1) {
         this.filter2(var1);
      }

      if (this.filtertype == 17) {
         this.filter2(var1);
      }

      if (this.filtertype == 33) {
         this.filter2(var1);
      }

      if (this.filtertype == 49) {
         this.filter2(var1);
      }

      if (this.filtertype == 3) {
         this.filter4(var1);
      }

      if (this.filtertype == 19) {
         this.filter4(var1);
      }

   }

   public void filter4(SoftAudioBuffer var1) {
      float[] var2 = var1.array();
      if (this.dirty) {
         this.filter2calc();
         this.dirty = false;
      }

      if (!this.last_set) {
         this.last_a0 = this.a0;
         this.last_a1 = this.a1;
         this.last_a2 = this.a2;
         this.last_b1 = this.b1;
         this.last_b2 = this.b2;
         this.last_gain = this.gain;
         this.last_wet = this.wet;
         this.last_set = true;
      }

      if (this.wet > 0.0F || this.last_wet > 0.0F) {
         int var3 = var2.length;
         float var4 = this.last_a0;
         float var5 = this.last_a1;
         float var6 = this.last_a2;
         float var7 = this.last_b1;
         float var8 = this.last_b2;
         float var9 = this.last_gain;
         float var10 = this.last_wet;
         float var11 = (this.a0 - this.last_a0) / (float)var3;
         float var12 = (this.a1 - this.last_a1) / (float)var3;
         float var13 = (this.a2 - this.last_a2) / (float)var3;
         float var14 = (this.b1 - this.last_b1) / (float)var3;
         float var15 = (this.b2 - this.last_b2) / (float)var3;
         float var16 = (this.gain - this.last_gain) / (float)var3;
         float var17 = (this.wet - this.last_wet) / (float)var3;
         float var18 = this.x1;
         float var19 = this.x2;
         float var20 = this.y1;
         float var21 = this.y2;
         float var22 = this.xx1;
         float var23 = this.xx2;
         float var24 = this.yy1;
         float var25 = this.yy2;
         int var26;
         float var27;
         float var28;
         float var29;
         float var30;
         if (var17 != 0.0F) {
            for(var26 = 0; var26 < var3; ++var26) {
               var4 += var11;
               var5 += var12;
               var6 += var13;
               var7 += var14;
               var8 += var15;
               var9 += var16;
               var10 += var17;
               var27 = var2[var26];
               var28 = var4 * var27 + var5 * var18 + var6 * var19 - var7 * var20 - var8 * var21;
               var29 = var28 * var9 * var10 + var27 * (1.0F - var10);
               var19 = var18;
               var18 = var27;
               var21 = var20;
               var20 = var28;
               var30 = var4 * var29 + var5 * var22 + var6 * var23 - var7 * var24 - var8 * var25;
               var2[var26] = var30 * var9 * var10 + var29 * (1.0F - var10);
               var23 = var22;
               var22 = var29;
               var25 = var24;
               var24 = var30;
            }
         } else if (var11 == 0.0F && var12 == 0.0F && var13 == 0.0F && var14 == 0.0F && var15 == 0.0F) {
            for(var26 = 0; var26 < var3; ++var26) {
               var27 = var2[var26];
               var28 = var4 * var27 + var5 * var18 + var6 * var19 - var7 * var20 - var8 * var21;
               var29 = var28 * var9 * var10 + var27 * (1.0F - var10);
               var19 = var18;
               var18 = var27;
               var21 = var20;
               var20 = var28;
               var30 = var4 * var29 + var5 * var22 + var6 * var23 - var7 * var24 - var8 * var25;
               var2[var26] = var30 * var9 * var10 + var29 * (1.0F - var10);
               var23 = var22;
               var22 = var29;
               var25 = var24;
               var24 = var30;
            }
         } else {
            for(var26 = 0; var26 < var3; ++var26) {
               var4 += var11;
               var5 += var12;
               var6 += var13;
               var7 += var14;
               var8 += var15;
               var9 += var16;
               var27 = var2[var26];
               var28 = var4 * var27 + var5 * var18 + var6 * var19 - var7 * var20 - var8 * var21;
               var29 = var28 * var9 * var10 + var27 * (1.0F - var10);
               var19 = var18;
               var18 = var27;
               var21 = var20;
               var20 = var28;
               var30 = var4 * var29 + var5 * var22 + var6 * var23 - var7 * var24 - var8 * var25;
               var2[var26] = var30 * var9 * var10 + var29 * (1.0F - var10);
               var23 = var22;
               var22 = var29;
               var25 = var24;
               var24 = var30;
            }
         }

         if ((double)Math.abs(var18) < 1.0E-8D) {
            var18 = 0.0F;
         }

         if ((double)Math.abs(var19) < 1.0E-8D) {
            var19 = 0.0F;
         }

         if ((double)Math.abs(var20) < 1.0E-8D) {
            var20 = 0.0F;
         }

         if ((double)Math.abs(var21) < 1.0E-8D) {
            var21 = 0.0F;
         }

         this.x1 = var18;
         this.x2 = var19;
         this.y1 = var20;
         this.y2 = var21;
         this.xx1 = var22;
         this.xx2 = var23;
         this.yy1 = var24;
         this.yy2 = var25;
      }

      this.last_a0 = this.a0;
      this.last_a1 = this.a1;
      this.last_a2 = this.a2;
      this.last_b1 = this.b1;
      this.last_b2 = this.b2;
      this.last_gain = this.gain;
      this.last_wet = this.wet;
   }

   private double sinh(double var1) {
      return (Math.exp(var1) - Math.exp(-var1)) * 0.5D;
   }

   public void filter2calc() {
      double var1 = this.resonancedB;
      if (var1 < 0.0D) {
         var1 = 0.0D;
      }

      if (var1 > 30.0D) {
         var1 = 30.0D;
      }

      if (this.filtertype == 3 || this.filtertype == 19) {
         var1 *= 0.6D;
      }

      double var3;
      double var5;
      double var7;
      double var9;
      double var11;
      double var13;
      double var17;
      double var19;
      double var21;
      double var23;
      double var25;
      double var27;
      if (this.filtertype == 33) {
         this.wet = 1.0F;
         var3 = this.cutoff / (double)this.samplerate;
         if (var3 > 0.45D) {
            var3 = 0.45D;
         }

         var5 = 3.141592653589793D * Math.pow(10.0D, -(var1 / 20.0D));
         var7 = 6.283185307179586D * var3;
         var9 = Math.cos(var7);
         var11 = Math.sin(var7);
         var13 = var11 * this.sinh(Math.log(2.0D) * var5 * var7 / (var11 * 2.0D));
         var17 = 0.0D;
         var19 = -var13;
         var21 = 1.0D + var13;
         var23 = -2.0D * var9;
         var25 = 1.0D - var13;
         var27 = 1.0D / var21;
         this.b1 = (float)(var23 * var27);
         this.b2 = (float)(var25 * var27);
         this.a0 = (float)(var13 * var27);
         this.a1 = (float)(var17 * var27);
         this.a2 = (float)(var19 * var27);
      }

      double var15;
      if (this.filtertype == 49) {
         this.wet = 1.0F;
         var3 = this.cutoff / (double)this.samplerate;
         if (var3 > 0.45D) {
            var3 = 0.45D;
         }

         var5 = 3.141592653589793D * Math.pow(10.0D, -(var1 / 20.0D));
         var7 = 6.283185307179586D * var3;
         var9 = Math.cos(var7);
         var11 = Math.sin(var7);
         var13 = var11 * this.sinh(Math.log(2.0D) * var5 * var7 / (var11 * 2.0D));
         var15 = 1.0D;
         var17 = -2.0D * var9;
         var19 = 1.0D;
         var21 = 1.0D + var13;
         var23 = -2.0D * var9;
         var25 = 1.0D - var13;
         var27 = 1.0D / var21;
         this.b1 = (float)(var23 * var27);
         this.b2 = (float)(var25 * var27);
         this.a0 = (float)(var15 * var27);
         this.a1 = (float)(var17 * var27);
         this.a2 = (float)(var19 * var27);
      }

      if (this.filtertype == 1 || this.filtertype == 3) {
         var3 = this.cutoff / (double)this.samplerate;
         if (var3 > 0.45D) {
            if (this.wet == 0.0F) {
               if (var1 < 1.0E-5D) {
                  this.wet = 0.0F;
               } else {
                  this.wet = 1.0F;
               }
            }

            var3 = 0.45D;
         } else {
            this.wet = 1.0F;
         }

         var5 = 1.0D / Math.tan(3.141592653589793D * var3);
         var7 = var5 * var5;
         var9 = Math.pow(10.0D, -(var1 / 20.0D));
         var11 = Math.sqrt(2.0D) * var9;
         var13 = 1.0D / (1.0D + var11 * var5 + var7);
         var15 = 2.0D * var13;
         var19 = 2.0D * var13 * (1.0D - var7);
         var21 = var13 * (1.0D - var11 * var5 + var7);
         this.a0 = (float)var13;
         this.a1 = (float)var15;
         this.a2 = (float)var13;
         this.b1 = (float)var19;
         this.b2 = (float)var21;
      }

      if (this.filtertype == 17 || this.filtertype == 19) {
         var3 = this.cutoff / (double)this.samplerate;
         if (var3 > 0.45D) {
            var3 = 0.45D;
         }

         if (var3 < 1.0E-4D) {
            var3 = 1.0E-4D;
         }

         this.wet = 1.0F;
         var5 = Math.tan(3.141592653589793D * var3);
         var7 = var5 * var5;
         var9 = Math.pow(10.0D, -(var1 / 20.0D));
         var11 = Math.sqrt(2.0D) * var9;
         var13 = 1.0D / (1.0D + var11 * var5 + var7);
         var15 = -2.0D * var13;
         var19 = 2.0D * var13 * (var7 - 1.0D);
         var21 = var13 * (1.0D - var11 * var5 + var7);
         this.a0 = (float)var13;
         this.a1 = (float)var15;
         this.a2 = (float)var13;
         this.b1 = (float)var19;
         this.b2 = (float)var21;
      }

   }

   public void filter2(SoftAudioBuffer var1) {
      float[] var2 = var1.array();
      if (this.dirty) {
         this.filter2calc();
         this.dirty = false;
      }

      if (!this.last_set) {
         this.last_a0 = this.a0;
         this.last_a1 = this.a1;
         this.last_a2 = this.a2;
         this.last_b1 = this.b1;
         this.last_b2 = this.b2;
         this.last_q = this.q;
         this.last_gain = this.gain;
         this.last_wet = this.wet;
         this.last_set = true;
      }

      if (this.wet > 0.0F || this.last_wet > 0.0F) {
         int var3 = var2.length;
         float var4 = this.last_a0;
         float var5 = this.last_a1;
         float var6 = this.last_a2;
         float var7 = this.last_b1;
         float var8 = this.last_b2;
         float var9 = this.last_gain;
         float var10 = this.last_wet;
         float var11 = (this.a0 - this.last_a0) / (float)var3;
         float var12 = (this.a1 - this.last_a1) / (float)var3;
         float var13 = (this.a2 - this.last_a2) / (float)var3;
         float var14 = (this.b1 - this.last_b1) / (float)var3;
         float var15 = (this.b2 - this.last_b2) / (float)var3;
         float var16 = (this.gain - this.last_gain) / (float)var3;
         float var17 = (this.wet - this.last_wet) / (float)var3;
         float var18 = this.x1;
         float var19 = this.x2;
         float var20 = this.y1;
         float var21 = this.y2;
         int var22;
         float var23;
         float var24;
         if (var17 != 0.0F) {
            for(var22 = 0; var22 < var3; ++var22) {
               var4 += var11;
               var5 += var12;
               var6 += var13;
               var7 += var14;
               var8 += var15;
               var9 += var16;
               var10 += var17;
               var23 = var2[var22];
               var24 = var4 * var23 + var5 * var18 + var6 * var19 - var7 * var20 - var8 * var21;
               var2[var22] = var24 * var9 * var10 + var23 * (1.0F - var10);
               var19 = var18;
               var18 = var23;
               var21 = var20;
               var20 = var24;
            }
         } else if (var11 == 0.0F && var12 == 0.0F && var13 == 0.0F && var14 == 0.0F && var15 == 0.0F) {
            for(var22 = 0; var22 < var3; ++var22) {
               var23 = var2[var22];
               var24 = var4 * var23 + var5 * var18 + var6 * var19 - var7 * var20 - var8 * var21;
               var2[var22] = var24 * var9;
               var19 = var18;
               var18 = var23;
               var21 = var20;
               var20 = var24;
            }
         } else {
            for(var22 = 0; var22 < var3; ++var22) {
               var4 += var11;
               var5 += var12;
               var6 += var13;
               var7 += var14;
               var8 += var15;
               var9 += var16;
               var23 = var2[var22];
               var24 = var4 * var23 + var5 * var18 + var6 * var19 - var7 * var20 - var8 * var21;
               var2[var22] = var24 * var9;
               var19 = var18;
               var18 = var23;
               var21 = var20;
               var20 = var24;
            }
         }

         if ((double)Math.abs(var18) < 1.0E-8D) {
            var18 = 0.0F;
         }

         if ((double)Math.abs(var19) < 1.0E-8D) {
            var19 = 0.0F;
         }

         if ((double)Math.abs(var20) < 1.0E-8D) {
            var20 = 0.0F;
         }

         if ((double)Math.abs(var21) < 1.0E-8D) {
            var21 = 0.0F;
         }

         this.x1 = var18;
         this.x2 = var19;
         this.y1 = var20;
         this.y2 = var21;
      }

      this.last_a0 = this.a0;
      this.last_a1 = this.a1;
      this.last_a2 = this.a2;
      this.last_b1 = this.b1;
      this.last_b2 = this.b2;
      this.last_q = this.q;
      this.last_gain = this.gain;
      this.last_wet = this.wet;
   }

   public void filter1calc() {
      if (this.cutoff < 120.0D) {
         this.cutoff = 120.0D;
      }

      double var1 = 7.3303828583761845D * this.cutoff / (double)this.samplerate;
      if (var1 > 1.0D) {
         var1 = 1.0D;
      }

      this.a0 = (float)(Math.sqrt(1.0D - Math.cos(var1)) * Math.sqrt(1.5707963267948966D));
      if (this.resonancedB < 0.0D) {
         this.resonancedB = 0.0D;
      }

      if (this.resonancedB > 20.0D) {
         this.resonancedB = 20.0D;
      }

      this.q = (float)(Math.sqrt(0.5D) * Math.pow(10.0D, -(this.resonancedB / 20.0D)));
      this.gain = (float)Math.pow(10.0D, -this.resonancedB / 40.0D);
      if (this.wet == 0.0F && (this.resonancedB > 1.0E-5D || var1 < 0.9999999D)) {
         this.wet = 1.0F;
      }

   }

   public void filter1(SoftAudioBuffer var1) {
      if (this.dirty) {
         this.filter1calc();
         this.dirty = false;
      }

      if (!this.last_set) {
         this.last_a0 = this.a0;
         this.last_q = this.q;
         this.last_gain = this.gain;
         this.last_wet = this.wet;
         this.last_set = true;
      }

      if (this.wet > 0.0F || this.last_wet > 0.0F) {
         float[] var2 = var1.array();
         int var3 = var2.length;
         float var4 = this.last_a0;
         float var5 = this.last_q;
         float var6 = this.last_gain;
         float var7 = this.last_wet;
         float var8 = (this.a0 - this.last_a0) / (float)var3;
         float var9 = (this.q - this.last_q) / (float)var3;
         float var10 = (this.gain - this.last_gain) / (float)var3;
         float var11 = (this.wet - this.last_wet) / (float)var3;
         float var12 = this.y2;
         float var13 = this.y1;
         int var14;
         float var15;
         if (var11 != 0.0F) {
            for(var14 = 0; var14 < var3; ++var14) {
               var4 += var8;
               var5 += var9;
               var6 += var10;
               var7 += var11;
               var15 = 1.0F - var5 * var4;
               var13 = var15 * var13 + var4 * (var2[var14] - var12);
               var12 = var15 * var12 + var4 * var13;
               var2[var14] = var12 * var6 * var7 + var2[var14] * (1.0F - var7);
            }
         } else if (var8 == 0.0F && var9 == 0.0F) {
            float var16 = 1.0F - var5 * var4;

            for(int var17 = 0; var17 < var3; ++var17) {
               var13 = var16 * var13 + var4 * (var2[var17] - var12);
               var12 = var16 * var12 + var4 * var13;
               var2[var17] = var12 * var6;
            }
         } else {
            for(var14 = 0; var14 < var3; ++var14) {
               var4 += var8;
               var5 += var9;
               var6 += var10;
               var15 = 1.0F - var5 * var4;
               var13 = var15 * var13 + var4 * (var2[var14] - var12);
               var12 = var15 * var12 + var4 * var13;
               var2[var14] = var12 * var6;
            }
         }

         if ((double)Math.abs(var12) < 1.0E-8D) {
            var12 = 0.0F;
         }

         if ((double)Math.abs(var13) < 1.0E-8D) {
            var13 = 0.0F;
         }

         this.y2 = var12;
         this.y1 = var13;
      }

      this.last_a0 = this.a0;
      this.last_q = this.q;
      this.last_gain = this.gain;
      this.last_wet = this.wet;
   }
}
