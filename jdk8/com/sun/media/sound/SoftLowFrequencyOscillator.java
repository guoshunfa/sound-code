package com.sun.media.sound;

public final class SoftLowFrequencyOscillator implements SoftProcess {
   private final int max_count = 10;
   private int used_count = 0;
   private final double[][] out = new double[10][1];
   private final double[][] delay = new double[10][1];
   private final double[][] delay2 = new double[10][1];
   private final double[][] freq = new double[10][1];
   private final int[] delay_counter = new int[10];
   private final double[] sin_phase = new double[10];
   private final double[] sin_stepfreq = new double[10];
   private final double[] sin_step = new double[10];
   private double control_time = 0.0D;
   private double sin_factor = 0.0D;
   private static final double PI2 = 6.283185307179586D;

   public SoftLowFrequencyOscillator() {
      for(int var1 = 0; var1 < this.sin_stepfreq.length; ++var1) {
         this.sin_stepfreq[var1] = Double.NEGATIVE_INFINITY;
      }

   }

   public void reset() {
      for(int var1 = 0; var1 < this.used_count; ++var1) {
         this.out[var1][0] = 0.0D;
         this.delay[var1][0] = 0.0D;
         this.delay2[var1][0] = 0.0D;
         this.freq[var1][0] = 0.0D;
         this.delay_counter[var1] = 0;
         this.sin_phase[var1] = 0.0D;
         this.sin_stepfreq[var1] = Double.NEGATIVE_INFINITY;
         this.sin_step[var1] = 0.0D;
      }

      this.used_count = 0;
   }

   public void init(SoftSynthesizer var1) {
      this.control_time = 1.0D / (double)var1.getControlRate();
      this.sin_factor = this.control_time * 2.0D * 3.141592653589793D;

      for(int var2 = 0; var2 < this.used_count; ++var2) {
         this.delay_counter[var2] = (int)(Math.pow(2.0D, this.delay[var2][0] / 1200.0D) / this.control_time);
         int[] var10000 = this.delay_counter;
         var10000[var2] += (int)(this.delay2[var2][0] / (this.control_time * 1000.0D));
      }

      this.processControlLogic();
   }

   public void processControlLogic() {
      for(int var1 = 0; var1 < this.used_count; ++var1) {
         if (this.delay_counter[var1] > 0) {
            int var10002 = this.delay_counter[var1]--;
            this.out[var1][0] = 0.5D;
         } else {
            double var2 = this.freq[var1][0];
            double var4;
            if (this.sin_stepfreq[var1] != var2) {
               this.sin_stepfreq[var1] = var2;
               var4 = 440.0D * Math.exp((var2 - 6900.0D) * (Math.log(2.0D) / 1200.0D));
               this.sin_step[var1] = var4 * this.sin_factor;
            }

            var4 = this.sin_phase[var1];

            for(var4 += this.sin_step[var1]; var4 > 6.283185307179586D; var4 -= 6.283185307179586D) {
            }

            this.out[var1][0] = 0.5D + Math.sin(var4) * 0.5D;
            this.sin_phase[var1] = var4;
         }
      }

   }

   public double[] get(int var1, String var2) {
      if (var1 >= this.used_count) {
         this.used_count = var1 + 1;
      }

      if (var2 == null) {
         return this.out[var1];
      } else if (var2.equals("delay")) {
         return this.delay[var1];
      } else if (var2.equals("delay2")) {
         return this.delay2[var1];
      } else {
         return var2.equals("freq") ? this.freq[var1] : null;
      }
   }
}
