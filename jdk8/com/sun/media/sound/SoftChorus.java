package com.sun.media.sound;

import java.util.Arrays;

public final class SoftChorus implements SoftAudioProcessor {
   private boolean mix = true;
   private SoftAudioBuffer inputA;
   private SoftAudioBuffer left;
   private SoftAudioBuffer right;
   private SoftAudioBuffer reverb;
   private SoftChorus.LFODelay vdelay1L;
   private SoftChorus.LFODelay vdelay1R;
   private float rgain = 0.0F;
   private boolean dirty = true;
   private double dirty_vdelay1L_rate;
   private double dirty_vdelay1R_rate;
   private double dirty_vdelay1L_depth;
   private double dirty_vdelay1R_depth;
   private float dirty_vdelay1L_feedback;
   private float dirty_vdelay1R_feedback;
   private float dirty_vdelay1L_reverbsendgain;
   private float dirty_vdelay1R_reverbsendgain;
   private float controlrate;
   double silentcounter = 1000.0D;

   public void init(float var1, float var2) {
      this.controlrate = var2;
      this.vdelay1L = new SoftChorus.LFODelay((double)var1, (double)var2);
      this.vdelay1R = new SoftChorus.LFODelay((double)var1, (double)var2);
      this.vdelay1L.setGain(1.0F);
      this.vdelay1R.setGain(1.0F);
      this.vdelay1L.setPhase(1.5707963267948966D);
      this.vdelay1R.setPhase(0.0D);
      this.globalParameterControlChange(new int[]{130}, 0L, 2L);
   }

   public void globalParameterControlChange(int[] var1, long var2, long var4) {
      if (var1.length == 1 && var1[0] == 130) {
         if (var2 == 0L) {
            switch((int)var4) {
            case 0:
               this.globalParameterControlChange(var1, 3L, 0L);
               this.globalParameterControlChange(var1, 1L, 3L);
               this.globalParameterControlChange(var1, 2L, 5L);
               this.globalParameterControlChange(var1, 4L, 0L);
               break;
            case 1:
               this.globalParameterControlChange(var1, 3L, 5L);
               this.globalParameterControlChange(var1, 1L, 9L);
               this.globalParameterControlChange(var1, 2L, 19L);
               this.globalParameterControlChange(var1, 4L, 0L);
               break;
            case 2:
               this.globalParameterControlChange(var1, 3L, 8L);
               this.globalParameterControlChange(var1, 1L, 3L);
               this.globalParameterControlChange(var1, 2L, 19L);
               this.globalParameterControlChange(var1, 4L, 0L);
               break;
            case 3:
               this.globalParameterControlChange(var1, 3L, 16L);
               this.globalParameterControlChange(var1, 1L, 9L);
               this.globalParameterControlChange(var1, 2L, 16L);
               this.globalParameterControlChange(var1, 4L, 0L);
               break;
            case 4:
               this.globalParameterControlChange(var1, 3L, 64L);
               this.globalParameterControlChange(var1, 1L, 2L);
               this.globalParameterControlChange(var1, 2L, 24L);
               this.globalParameterControlChange(var1, 4L, 0L);
               break;
            case 5:
               this.globalParameterControlChange(var1, 3L, 112L);
               this.globalParameterControlChange(var1, 1L, 1L);
               this.globalParameterControlChange(var1, 2L, 5L);
               this.globalParameterControlChange(var1, 4L, 0L);
            }
         } else if (var2 == 1L) {
            this.dirty_vdelay1L_rate = (double)var4 * 0.122D;
            this.dirty_vdelay1R_rate = (double)var4 * 0.122D;
            this.dirty = true;
         } else if (var2 == 2L) {
            this.dirty_vdelay1L_depth = (double)(var4 + 1L) / 3200.0D;
            this.dirty_vdelay1R_depth = (double)(var4 + 1L) / 3200.0D;
            this.dirty = true;
         } else if (var2 == 3L) {
            this.dirty_vdelay1L_feedback = (float)var4 * 0.00763F;
            this.dirty_vdelay1R_feedback = (float)var4 * 0.00763F;
            this.dirty = true;
         }

         if (var2 == 4L) {
            this.rgain = (float)var4 * 0.00787F;
            this.dirty_vdelay1L_reverbsendgain = (float)var4 * 0.00787F;
            this.dirty_vdelay1R_reverbsendgain = (float)var4 * 0.00787F;
            this.dirty = true;
         }
      }

   }

   public void processControlLogic() {
      if (this.dirty) {
         this.dirty = false;
         this.vdelay1L.setRate(this.dirty_vdelay1L_rate);
         this.vdelay1R.setRate(this.dirty_vdelay1R_rate);
         this.vdelay1L.setDepth(this.dirty_vdelay1L_depth);
         this.vdelay1R.setDepth(this.dirty_vdelay1R_depth);
         this.vdelay1L.setFeedBack(this.dirty_vdelay1L_feedback);
         this.vdelay1R.setFeedBack(this.dirty_vdelay1R_feedback);
         this.vdelay1L.setReverbSendGain(this.dirty_vdelay1L_reverbsendgain);
         this.vdelay1R.setReverbSendGain(this.dirty_vdelay1R_reverbsendgain);
      }

   }

   public void processAudio() {
      if (this.inputA.isSilent()) {
         this.silentcounter += (double)(1.0F / this.controlrate);
         if (this.silentcounter > 1.0D) {
            if (!this.mix) {
               this.left.clear();
               this.right.clear();
            }

            return;
         }
      } else {
         this.silentcounter = 0.0D;
      }

      float[] var1 = this.inputA.array();
      float[] var2 = this.left.array();
      float[] var3 = this.right == null ? null : this.right.array();
      float[] var4 = this.rgain != 0.0F ? this.reverb.array() : null;
      if (this.mix) {
         this.vdelay1L.processMix(var1, var2, var4);
         if (var3 != null) {
            this.vdelay1R.processMix(var1, var3, var4);
         }
      } else {
         this.vdelay1L.processReplace(var1, var2, var4);
         if (var3 != null) {
            this.vdelay1R.processReplace(var1, var3, var4);
         }
      }

   }

   public void setInput(int var1, SoftAudioBuffer var2) {
      if (var1 == 0) {
         this.inputA = var2;
      }

   }

   public void setMixMode(boolean var1) {
      this.mix = var1;
   }

   public void setOutput(int var1, SoftAudioBuffer var2) {
      if (var1 == 0) {
         this.left = var2;
      }

      if (var1 == 1) {
         this.right = var2;
      }

      if (var1 == 2) {
         this.reverb = var2;
      }

   }

   private static class LFODelay {
      private double phase = 1.0D;
      private double phase_step = 0.0D;
      private double depth = 0.0D;
      private SoftChorus.VariableDelay vdelay;
      private final double samplerate;
      private final double controlrate;

      LFODelay(double var1, double var3) {
         this.samplerate = var1;
         this.controlrate = var3;
         this.vdelay = new SoftChorus.VariableDelay((int)((this.depth + 10.0D) * 2.0D));
      }

      public void setDepth(double var1) {
         this.depth = var1 * this.samplerate;
         this.vdelay = new SoftChorus.VariableDelay((int)((this.depth + 10.0D) * 2.0D));
      }

      public void setRate(double var1) {
         double var3 = 6.283185307179586D * (var1 / this.controlrate);
         this.phase_step = var3;
      }

      public void setPhase(double var1) {
         this.phase = var1;
      }

      public void setFeedBack(float var1) {
         this.vdelay.setFeedBack(var1);
      }

      public void setGain(float var1) {
         this.vdelay.setGain(var1);
      }

      public void setReverbSendGain(float var1) {
         this.vdelay.setReverbSendGain(var1);
      }

      public void processMix(float[] var1, float[] var2, float[] var3) {
         for(this.phase += this.phase_step; this.phase > 6.283185307179586D; this.phase -= 6.283185307179586D) {
         }

         this.vdelay.setDelay((float)(this.depth * 0.5D * (Math.cos(this.phase) + 2.0D)));
         this.vdelay.processMix(var1, var2, var3);
      }

      public void processReplace(float[] var1, float[] var2, float[] var3) {
         for(this.phase += this.phase_step; this.phase > 6.283185307179586D; this.phase -= 6.283185307179586D) {
         }

         this.vdelay.setDelay((float)(this.depth * 0.5D * (Math.cos(this.phase) + 2.0D)));
         this.vdelay.processReplace(var1, var2, var3);
      }
   }

   private static class VariableDelay {
      private final float[] delaybuffer;
      private int rovepos = 0;
      private float gain = 1.0F;
      private float rgain = 0.0F;
      private float delay = 0.0F;
      private float lastdelay = 0.0F;
      private float feedback = 0.0F;

      VariableDelay(int var1) {
         this.delaybuffer = new float[var1];
      }

      public void setDelay(float var1) {
         this.delay = var1;
      }

      public void setFeedBack(float var1) {
         this.feedback = var1;
      }

      public void setGain(float var1) {
         this.gain = var1;
      }

      public void setReverbSendGain(float var1) {
         this.rgain = var1;
      }

      public void processMix(float[] var1, float[] var2, float[] var3) {
         float var4 = this.gain;
         float var5 = this.delay;
         float var6 = this.feedback;
         float[] var7 = this.delaybuffer;
         int var8 = var1.length;
         float var9 = (var5 - this.lastdelay) / (float)var8;
         int var10 = var7.length;
         int var11 = this.rovepos;
         int var12;
         float var13;
         int var14;
         float var15;
         float var16;
         float var17;
         float var18;
         if (var3 == null) {
            for(var12 = 0; var12 < var8; ++var12) {
               var13 = (float)var11 - (this.lastdelay + 2.0F) + (float)var10;
               var14 = (int)var13;
               var15 = var13 - (float)var14;
               var16 = var7[var14 % var10];
               var17 = var7[(var14 + 1) % var10];
               var18 = var16 * (1.0F - var15) + var17 * var15;
               var2[var12] += var18 * var4;
               var7[var11] = var1[var12] + var18 * var6;
               var11 = (var11 + 1) % var10;
               this.lastdelay += var9;
            }
         } else {
            for(var12 = 0; var12 < var8; ++var12) {
               var13 = (float)var11 - (this.lastdelay + 2.0F) + (float)var10;
               var14 = (int)var13;
               var15 = var13 - (float)var14;
               var16 = var7[var14 % var10];
               var17 = var7[(var14 + 1) % var10];
               var18 = var16 * (1.0F - var15) + var17 * var15;
               var2[var12] += var18 * var4;
               var3[var12] += var18 * this.rgain;
               var7[var11] = var1[var12] + var18 * var6;
               var11 = (var11 + 1) % var10;
               this.lastdelay += var9;
            }
         }

         this.rovepos = var11;
         this.lastdelay = var5;
      }

      public void processReplace(float[] var1, float[] var2, float[] var3) {
         Arrays.fill(var2, 0.0F);
         Arrays.fill(var3, 0.0F);
         this.processMix(var1, var2, var3);
      }
   }
}
