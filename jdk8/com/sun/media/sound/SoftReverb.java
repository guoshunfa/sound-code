package com.sun.media.sound;

import java.util.Arrays;

public final class SoftReverb implements SoftAudioProcessor {
   private float roomsize;
   private float damp;
   private float gain = 1.0F;
   private SoftReverb.Delay delay;
   private SoftReverb.Comb[] combL;
   private SoftReverb.Comb[] combR;
   private SoftReverb.AllPass[] allpassL;
   private SoftReverb.AllPass[] allpassR;
   private float[] input;
   private float[] out;
   private float[] pre1;
   private float[] pre2;
   private float[] pre3;
   private boolean denormal_flip = false;
   private boolean mix = true;
   private SoftAudioBuffer inputA;
   private SoftAudioBuffer left;
   private SoftAudioBuffer right;
   private boolean dirty = true;
   private float dirty_roomsize;
   private float dirty_damp;
   private float dirty_predelay;
   private float dirty_gain;
   private float samplerate;
   private boolean light = true;
   private boolean silent = true;

   public void init(float var1, float var2) {
      this.samplerate = var1;
      double var3 = (double)var1 / 44100.0D;
      byte var5 = 23;
      this.delay = new SoftReverb.Delay();
      this.combL = new SoftReverb.Comb[8];
      this.combR = new SoftReverb.Comb[8];
      this.combL[0] = new SoftReverb.Comb((int)(var3 * 1116.0D));
      this.combR[0] = new SoftReverb.Comb((int)(var3 * (double)(1116 + var5)));
      this.combL[1] = new SoftReverb.Comb((int)(var3 * 1188.0D));
      this.combR[1] = new SoftReverb.Comb((int)(var3 * (double)(1188 + var5)));
      this.combL[2] = new SoftReverb.Comb((int)(var3 * 1277.0D));
      this.combR[2] = new SoftReverb.Comb((int)(var3 * (double)(1277 + var5)));
      this.combL[3] = new SoftReverb.Comb((int)(var3 * 1356.0D));
      this.combR[3] = new SoftReverb.Comb((int)(var3 * (double)(1356 + var5)));
      this.combL[4] = new SoftReverb.Comb((int)(var3 * 1422.0D));
      this.combR[4] = new SoftReverb.Comb((int)(var3 * (double)(1422 + var5)));
      this.combL[5] = new SoftReverb.Comb((int)(var3 * 1491.0D));
      this.combR[5] = new SoftReverb.Comb((int)(var3 * (double)(1491 + var5)));
      this.combL[6] = new SoftReverb.Comb((int)(var3 * 1557.0D));
      this.combR[6] = new SoftReverb.Comb((int)(var3 * (double)(1557 + var5)));
      this.combL[7] = new SoftReverb.Comb((int)(var3 * 1617.0D));
      this.combR[7] = new SoftReverb.Comb((int)(var3 * (double)(1617 + var5)));
      this.allpassL = new SoftReverb.AllPass[4];
      this.allpassR = new SoftReverb.AllPass[4];
      this.allpassL[0] = new SoftReverb.AllPass((int)(var3 * 556.0D));
      this.allpassR[0] = new SoftReverb.AllPass((int)(var3 * (double)(556 + var5)));
      this.allpassL[1] = new SoftReverb.AllPass((int)(var3 * 441.0D));
      this.allpassR[1] = new SoftReverb.AllPass((int)(var3 * (double)(441 + var5)));
      this.allpassL[2] = new SoftReverb.AllPass((int)(var3 * 341.0D));
      this.allpassR[2] = new SoftReverb.AllPass((int)(var3 * (double)(341 + var5)));
      this.allpassL[3] = new SoftReverb.AllPass((int)(var3 * 225.0D));
      this.allpassR[3] = new SoftReverb.AllPass((int)(var3 * (double)(225 + var5)));

      for(int var6 = 0; var6 < this.allpassL.length; ++var6) {
         this.allpassL[var6].setFeedBack(0.5F);
         this.allpassR[var6].setFeedBack(0.5F);
      }

      this.globalParameterControlChange(new int[]{129}, 0L, 4L);
   }

   public void setInput(int var1, SoftAudioBuffer var2) {
      if (var1 == 0) {
         this.inputA = var2;
      }

   }

   public void setOutput(int var1, SoftAudioBuffer var2) {
      if (var1 == 0) {
         this.left = var2;
      }

      if (var1 == 1) {
         this.right = var2;
      }

   }

   public void setMixMode(boolean var1) {
      this.mix = var1;
   }

   public void processAudio() {
      boolean var1 = this.inputA.isSilent();
      if (!var1) {
         this.silent = false;
      }

      if (this.silent) {
         if (!this.mix) {
            this.left.clear();
            this.right.clear();
         }

      } else {
         float[] var2 = this.inputA.array();
         float[] var3 = this.left.array();
         float[] var4 = this.right == null ? null : this.right.array();
         int var5 = var2.length;
         if (this.input == null || this.input.length < var5) {
            this.input = new float[var5];
         }

         float var6 = this.gain * 0.018F / 2.0F;
         this.denormal_flip = !this.denormal_flip;
         int var7;
         if (this.denormal_flip) {
            for(var7 = 0; var7 < var5; ++var7) {
               this.input[var7] = var2[var7] * var6 + 1.0E-20F;
            }
         } else {
            for(var7 = 0; var7 < var5; ++var7) {
               this.input[var7] = var2[var7] * var6 - 1.0E-20F;
            }
         }

         this.delay.processReplace(this.input);
         float var8;
         if (this.light && var4 != null) {
            if (this.pre1 == null || this.pre1.length < var5) {
               this.pre1 = new float[var5];
               this.pre2 = new float[var5];
               this.pre3 = new float[var5];
            }

            for(var7 = 0; var7 < this.allpassL.length; ++var7) {
               this.allpassL[var7].processReplace(this.input);
            }

            this.combL[0].processReplace(this.input, this.pre3);
            this.combL[1].processReplace(this.input, this.pre3);
            this.combL[2].processReplace(this.input, this.pre1);

            for(var7 = 4; var7 < this.combL.length - 2; var7 += 2) {
               this.combL[var7].processMix(this.input, this.pre1);
            }

            this.combL[3].processReplace(this.input, this.pre2);

            for(var7 = 5; var7 < this.combL.length - 2; var7 += 2) {
               this.combL[var7].processMix(this.input, this.pre2);
            }

            if (!this.mix) {
               Arrays.fill(var4, 0.0F);
               Arrays.fill(var3, 0.0F);
            }

            for(var7 = this.combR.length - 2; var7 < this.combR.length; ++var7) {
               this.combR[var7].processMix(this.input, var4);
            }

            for(var7 = this.combL.length - 2; var7 < this.combL.length; ++var7) {
               this.combL[var7].processMix(this.input, var3);
            }

            for(var7 = 0; var7 < var5; ++var7) {
               var8 = this.pre1[var7] - this.pre2[var7];
               float var9 = this.pre3[var7];
               var3[var7] += var9 + var8;
               var4[var7] += var9 - var8;
            }
         } else {
            if (this.out == null || this.out.length < var5) {
               this.out = new float[var5];
            }

            if (var4 != null) {
               if (!this.mix) {
                  Arrays.fill(var4, 0.0F);
               }

               this.allpassR[0].processReplace(this.input, this.out);

               for(var7 = 1; var7 < this.allpassR.length; ++var7) {
                  this.allpassR[var7].processReplace(this.out);
               }

               for(var7 = 0; var7 < this.combR.length; ++var7) {
                  this.combR[var7].processMix(this.out, var4);
               }
            }

            if (!this.mix) {
               Arrays.fill(var3, 0.0F);
            }

            this.allpassL[0].processReplace(this.input, this.out);

            for(var7 = 1; var7 < this.allpassL.length; ++var7) {
               this.allpassL[var7].processReplace(this.out);
            }

            for(var7 = 0; var7 < this.combL.length; ++var7) {
               this.combL[var7].processMix(this.out, var3);
            }
         }

         if (var1) {
            this.silent = true;

            for(var7 = 0; var7 < var5; ++var7) {
               var8 = var3[var7];
               if ((double)var8 > 1.0E-10D || (double)var8 < -1.0E-10D) {
                  this.silent = false;
                  break;
               }
            }
         }

      }
   }

   public void globalParameterControlChange(int[] var1, long var2, long var4) {
      if (var1.length == 1 && var1[0] == 129) {
         if (var2 == 0L) {
            if (var4 == 0L) {
               this.dirty_roomsize = 1.1F;
               this.dirty_damp = 5000.0F;
               this.dirty_predelay = 0.0F;
               this.dirty_gain = 4.0F;
               this.dirty = true;
            }

            if (var4 == 1L) {
               this.dirty_roomsize = 1.3F;
               this.dirty_damp = 5000.0F;
               this.dirty_predelay = 0.0F;
               this.dirty_gain = 3.0F;
               this.dirty = true;
            }

            if (var4 == 2L) {
               this.dirty_roomsize = 1.5F;
               this.dirty_damp = 5000.0F;
               this.dirty_predelay = 0.0F;
               this.dirty_gain = 2.0F;
               this.dirty = true;
            }

            if (var4 == 3L) {
               this.dirty_roomsize = 1.8F;
               this.dirty_damp = 24000.0F;
               this.dirty_predelay = 0.02F;
               this.dirty_gain = 1.5F;
               this.dirty = true;
            }

            if (var4 == 4L) {
               this.dirty_roomsize = 1.8F;
               this.dirty_damp = 24000.0F;
               this.dirty_predelay = 0.03F;
               this.dirty_gain = 1.5F;
               this.dirty = true;
            }

            if (var4 == 8L) {
               this.dirty_roomsize = 1.3F;
               this.dirty_damp = 2500.0F;
               this.dirty_predelay = 0.0F;
               this.dirty_gain = 6.0F;
               this.dirty = true;
            }
         } else if (var2 == 1L) {
            this.dirty_roomsize = (float)Math.exp((double)(var4 - 40L) * 0.025D);
            this.dirty = true;
         }
      }

   }

   public void processControlLogic() {
      if (this.dirty) {
         this.dirty = false;
         this.setRoomSize(this.dirty_roomsize);
         this.setDamp(this.dirty_damp);
         this.setPreDelay(this.dirty_predelay);
         this.setGain(this.dirty_gain);
      }

   }

   public void setRoomSize(float var1) {
      this.roomsize = 1.0F - 0.17F / var1;

      for(int var2 = 0; var2 < this.combL.length; ++var2) {
         this.combL[var2].feedback = this.roomsize;
         this.combR[var2].feedback = this.roomsize;
      }

   }

   public void setPreDelay(float var1) {
      this.delay.setDelay((int)(var1 * this.samplerate));
   }

   public void setGain(float var1) {
      this.gain = var1;
   }

   public void setDamp(float var1) {
      double var2 = (double)(var1 / this.samplerate) * 6.283185307179586D;
      double var4 = 2.0D - Math.cos(var2);
      this.damp = (float)(var4 - Math.sqrt(var4 * var4 - 1.0D));
      if (this.damp > 1.0F) {
         this.damp = 1.0F;
      }

      if (this.damp < 0.0F) {
         this.damp = 0.0F;
      }

      for(int var6 = 0; var6 < this.combL.length; ++var6) {
         this.combL[var6].setDamp(this.damp);
         this.combR[var6].setDamp(this.damp);
      }

   }

   public void setLightMode(boolean var1) {
      this.light = var1;
   }

   private static final class Comb {
      private final float[] delaybuffer;
      private final int delaybuffersize;
      private int rovepos = 0;
      private float feedback;
      private float filtertemp = 0.0F;
      private float filtercoeff1 = 0.0F;
      private float filtercoeff2 = 1.0F;

      Comb(int var1) {
         this.delaybuffer = new float[var1];
         this.delaybuffersize = var1;
      }

      public void setFeedBack(float var1) {
         this.feedback = var1;
         this.filtercoeff2 = (1.0F - this.filtercoeff1) * var1;
      }

      public void processMix(float[] var1, float[] var2) {
         int var3 = var1.length;
         int var4 = this.delaybuffersize;
         int var5 = this.rovepos;
         float var6 = this.filtertemp;
         float var7 = this.filtercoeff1;
         float var8 = this.filtercoeff2;

         for(int var9 = 0; var9 < var3; ++var9) {
            float var10 = this.delaybuffer[var5];
            var6 = var10 * var8 + var6 * var7;
            var2[var9] += var10;
            this.delaybuffer[var5] = var1[var9] + var6;
            ++var5;
            if (var5 == var4) {
               var5 = 0;
            }
         }

         this.filtertemp = var6;
         this.rovepos = var5;
      }

      public void processReplace(float[] var1, float[] var2) {
         int var3 = var1.length;
         int var4 = this.delaybuffersize;
         int var5 = this.rovepos;
         float var6 = this.filtertemp;
         float var7 = this.filtercoeff1;
         float var8 = this.filtercoeff2;

         for(int var9 = 0; var9 < var3; ++var9) {
            float var10 = this.delaybuffer[var5];
            var6 = var10 * var8 + var6 * var7;
            var2[var9] = var10;
            this.delaybuffer[var5] = var1[var9] + var6;
            ++var5;
            if (var5 == var4) {
               var5 = 0;
            }
         }

         this.filtertemp = var6;
         this.rovepos = var5;
      }

      public void setDamp(float var1) {
         this.filtercoeff1 = var1;
         this.filtercoeff2 = (1.0F - this.filtercoeff1) * this.feedback;
      }
   }

   private static final class AllPass {
      private final float[] delaybuffer;
      private final int delaybuffersize;
      private int rovepos = 0;
      private float feedback;

      AllPass(int var1) {
         this.delaybuffer = new float[var1];
         this.delaybuffersize = var1;
      }

      public void setFeedBack(float var1) {
         this.feedback = var1;
      }

      public void processReplace(float[] var1) {
         int var2 = var1.length;
         int var3 = this.delaybuffersize;
         int var4 = this.rovepos;

         for(int var5 = 0; var5 < var2; ++var5) {
            float var6 = this.delaybuffer[var4];
            float var7 = var1[var5];
            var1[var5] = var6 - var7;
            this.delaybuffer[var4] = var7 + var6 * this.feedback;
            ++var4;
            if (var4 == var3) {
               var4 = 0;
            }
         }

         this.rovepos = var4;
      }

      public void processReplace(float[] var1, float[] var2) {
         int var3 = var1.length;
         int var4 = this.delaybuffersize;
         int var5 = this.rovepos;

         for(int var6 = 0; var6 < var3; ++var6) {
            float var7 = this.delaybuffer[var5];
            float var8 = var1[var6];
            var2[var6] = var7 - var8;
            this.delaybuffer[var5] = var8 + var7 * this.feedback;
            ++var5;
            if (var5 == var4) {
               var5 = 0;
            }
         }

         this.rovepos = var5;
      }
   }

   private static final class Delay {
      private float[] delaybuffer = null;
      private int rovepos = 0;

      Delay() {
      }

      public void setDelay(int var1) {
         if (var1 == 0) {
            this.delaybuffer = null;
         } else {
            this.delaybuffer = new float[var1];
         }

         this.rovepos = 0;
      }

      public void processReplace(float[] var1) {
         if (this.delaybuffer != null) {
            int var2 = var1.length;
            int var3 = this.delaybuffer.length;
            int var4 = this.rovepos;

            for(int var5 = 0; var5 < var2; ++var5) {
               float var6 = var1[var5];
               var1[var5] = this.delaybuffer[var4];
               this.delaybuffer[var4] = var6;
               ++var4;
               if (var4 == var3) {
                  var4 = 0;
               }
            }

            this.rovepos = var4;
         }
      }
   }
}
