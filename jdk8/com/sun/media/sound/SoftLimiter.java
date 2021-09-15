package com.sun.media.sound;

public final class SoftLimiter implements SoftAudioProcessor {
   float lastmax = 0.0F;
   float gain = 1.0F;
   float[] temp_bufferL;
   float[] temp_bufferR;
   boolean mix = false;
   SoftAudioBuffer bufferL;
   SoftAudioBuffer bufferR;
   SoftAudioBuffer bufferLout;
   SoftAudioBuffer bufferRout;
   float controlrate;
   double silentcounter = 0.0D;

   public void init(float var1, float var2) {
      this.controlrate = var2;
   }

   public void setInput(int var1, SoftAudioBuffer var2) {
      if (var1 == 0) {
         this.bufferL = var2;
      }

      if (var1 == 1) {
         this.bufferR = var2;
      }

   }

   public void setOutput(int var1, SoftAudioBuffer var2) {
      if (var1 == 0) {
         this.bufferLout = var2;
      }

      if (var1 == 1) {
         this.bufferRout = var2;
      }

   }

   public void setMixMode(boolean var1) {
      this.mix = var1;
   }

   public void globalParameterControlChange(int[] var1, long var2, long var4) {
   }

   public void processAudio() {
      if (this.bufferL.isSilent() && (this.bufferR == null || this.bufferR.isSilent())) {
         this.silentcounter += (double)(1.0F / this.controlrate);
         if (this.silentcounter > 60.0D) {
            if (!this.mix) {
               this.bufferLout.clear();
               if (this.bufferRout != null) {
                  this.bufferRout.clear();
               }
            }

            return;
         }
      } else {
         this.silentcounter = 0.0D;
      }

      float[] var1 = this.bufferL.array();
      float[] var2 = this.bufferR == null ? null : this.bufferR.array();
      float[] var3 = this.bufferLout.array();
      float[] var4 = this.bufferRout == null ? null : this.bufferRout.array();
      if (this.temp_bufferL == null || this.temp_bufferL.length < var1.length) {
         this.temp_bufferL = new float[var1.length];
      }

      if (var2 != null && (this.temp_bufferR == null || this.temp_bufferR.length < var2.length)) {
         this.temp_bufferR = new float[var2.length];
      }

      float var5 = 0.0F;
      int var6 = var1.length;
      int var7;
      if (var2 == null) {
         for(var7 = 0; var7 < var6; ++var7) {
            if (var1[var7] > var5) {
               var5 = var1[var7];
            }

            if (-var1[var7] > var5) {
               var5 = -var1[var7];
            }
         }
      } else {
         for(var7 = 0; var7 < var6; ++var7) {
            if (var1[var7] > var5) {
               var5 = var1[var7];
            }

            if (var2[var7] > var5) {
               var5 = var2[var7];
            }

            if (-var1[var7] > var5) {
               var5 = -var1[var7];
            }

            if (-var2[var7] > var5) {
               var5 = -var2[var7];
            }
         }
      }

      float var15 = this.lastmax;
      this.lastmax = var5;
      if (var15 > var5) {
         var5 = var15;
      }

      float var8 = 1.0F;
      if (var5 > 0.99F) {
         var8 = 0.99F / var5;
      } else {
         var8 = 1.0F;
      }

      if (var8 > this.gain) {
         var8 = (var8 + this.gain * 9.0F) / 10.0F;
      }

      float var9 = (var8 - this.gain) / (float)var6;
      int var10;
      float var11;
      float var12;
      float var13;
      float var14;
      if (this.mix) {
         if (var2 == null) {
            for(var10 = 0; var10 < var6; ++var10) {
               this.gain += var9;
               var11 = var1[var10];
               var12 = this.temp_bufferL[var10];
               this.temp_bufferL[var10] = var11;
               var3[var10] += var12 * this.gain;
            }
         } else {
            for(var10 = 0; var10 < var6; ++var10) {
               this.gain += var9;
               var11 = var1[var10];
               var12 = var2[var10];
               var13 = this.temp_bufferL[var10];
               var14 = this.temp_bufferR[var10];
               this.temp_bufferL[var10] = var11;
               this.temp_bufferR[var10] = var12;
               var3[var10] += var13 * this.gain;
               var4[var10] += var14 * this.gain;
            }
         }
      } else if (var2 == null) {
         for(var10 = 0; var10 < var6; ++var10) {
            this.gain += var9;
            var11 = var1[var10];
            var12 = this.temp_bufferL[var10];
            this.temp_bufferL[var10] = var11;
            var3[var10] = var12 * this.gain;
         }
      } else {
         for(var10 = 0; var10 < var6; ++var10) {
            this.gain += var9;
            var11 = var1[var10];
            var12 = var2[var10];
            var13 = this.temp_bufferL[var10];
            var14 = this.temp_bufferR[var10];
            this.temp_bufferL[var10] = var11;
            this.temp_bufferR[var10] = var12;
            var3[var10] = var13 * this.gain;
            var4[var10] = var14 * this.gain;
         }
      }

      this.gain = var8;
   }

   public void processControlLogic() {
   }
}
