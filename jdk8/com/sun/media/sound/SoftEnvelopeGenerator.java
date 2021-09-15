package com.sun.media.sound;

public final class SoftEnvelopeGenerator implements SoftProcess {
   public static final int EG_OFF = 0;
   public static final int EG_DELAY = 1;
   public static final int EG_ATTACK = 2;
   public static final int EG_HOLD = 3;
   public static final int EG_DECAY = 4;
   public static final int EG_SUSTAIN = 5;
   public static final int EG_RELEASE = 6;
   public static final int EG_SHUTDOWN = 7;
   public static final int EG_END = 8;
   int max_count = 10;
   int used_count = 0;
   private final int[] stage;
   private final int[] stage_ix;
   private final double[] stage_v;
   private final int[] stage_count;
   private final double[][] on;
   private final double[][] active;
   private final double[][] out;
   private final double[][] delay;
   private final double[][] attack;
   private final double[][] hold;
   private final double[][] decay;
   private final double[][] sustain;
   private final double[][] release;
   private final double[][] shutdown;
   private final double[][] release2;
   private final double[][] attack2;
   private final double[][] decay2;
   private double control_time;

   public SoftEnvelopeGenerator() {
      this.stage = new int[this.max_count];
      this.stage_ix = new int[this.max_count];
      this.stage_v = new double[this.max_count];
      this.stage_count = new int[this.max_count];
      this.on = new double[this.max_count][1];
      this.active = new double[this.max_count][1];
      this.out = new double[this.max_count][1];
      this.delay = new double[this.max_count][1];
      this.attack = new double[this.max_count][1];
      this.hold = new double[this.max_count][1];
      this.decay = new double[this.max_count][1];
      this.sustain = new double[this.max_count][1];
      this.release = new double[this.max_count][1];
      this.shutdown = new double[this.max_count][1];
      this.release2 = new double[this.max_count][1];
      this.attack2 = new double[this.max_count][1];
      this.decay2 = new double[this.max_count][1];
      this.control_time = 0.0D;
   }

   public void reset() {
      for(int var1 = 0; var1 < this.used_count; ++var1) {
         this.stage[var1] = 0;
         this.on[var1][0] = 0.0D;
         this.out[var1][0] = 0.0D;
         this.delay[var1][0] = 0.0D;
         this.attack[var1][0] = 0.0D;
         this.hold[var1][0] = 0.0D;
         this.decay[var1][0] = 0.0D;
         this.sustain[var1][0] = 0.0D;
         this.release[var1][0] = 0.0D;
         this.shutdown[var1][0] = 0.0D;
         this.attack2[var1][0] = 0.0D;
         this.decay2[var1][0] = 0.0D;
         this.release2[var1][0] = 0.0D;
      }

      this.used_count = 0;
   }

   public void init(SoftSynthesizer var1) {
      this.control_time = 1.0D / (double)var1.getControlRate();
      this.processControlLogic();
   }

   public double[] get(int var1, String var2) {
      if (var1 >= this.used_count) {
         this.used_count = var1 + 1;
      }

      if (var2 == null) {
         return this.out[var1];
      } else if (var2.equals("on")) {
         return this.on[var1];
      } else if (var2.equals("active")) {
         return this.active[var1];
      } else if (var2.equals("delay")) {
         return this.delay[var1];
      } else if (var2.equals("attack")) {
         return this.attack[var1];
      } else if (var2.equals("hold")) {
         return this.hold[var1];
      } else if (var2.equals("decay")) {
         return this.decay[var1];
      } else if (var2.equals("sustain")) {
         return this.sustain[var1];
      } else if (var2.equals("release")) {
         return this.release[var1];
      } else if (var2.equals("shutdown")) {
         return this.shutdown[var1];
      } else if (var2.equals("attack2")) {
         return this.attack2[var1];
      } else if (var2.equals("decay2")) {
         return this.decay2[var1];
      } else {
         return var2.equals("release2") ? this.release2[var1] : null;
      }
   }

   public void processControlLogic() {
      for(int var1 = 0; var1 < this.used_count; ++var1) {
         if (this.stage[var1] != 8) {
            int[] var10000;
            double var2;
            if (this.stage[var1] > 0 && this.stage[var1] < 6 && this.on[var1][0] < 0.5D) {
               if (this.on[var1][0] < -0.5D) {
                  this.stage_count[var1] = (int)(Math.pow(2.0D, this.shutdown[var1][0] / 1200.0D) / this.control_time);
                  if (this.stage_count[var1] < 0) {
                     this.stage_count[var1] = 0;
                  }

                  this.stage_v[var1] = this.out[var1][0];
                  this.stage_ix[var1] = 0;
                  this.stage[var1] = 7;
               } else {
                  if (this.release2[var1][0] < 1.0E-6D && this.release[var1][0] < 0.0D && Double.isInfinite(this.release[var1][0])) {
                     this.out[var1][0] = 0.0D;
                     this.active[var1][0] = 0.0D;
                     this.stage[var1] = 8;
                     continue;
                  }

                  this.stage_count[var1] = (int)(Math.pow(2.0D, this.release[var1][0] / 1200.0D) / this.control_time);
                  var10000 = this.stage_count;
                  var10000[var1] += (int)(this.release2[var1][0] / (this.control_time * 1000.0D));
                  if (this.stage_count[var1] < 0) {
                     this.stage_count[var1] = 0;
                  }

                  this.stage_ix[var1] = 0;
                  var2 = 1.0D - this.out[var1][0];
                  this.stage_ix[var1] = (int)((double)this.stage_count[var1] * var2);
                  this.stage[var1] = 6;
               }
            }

            int var10002;
            double var4;
            switch(this.stage[var1]) {
            case 0:
               this.active[var1][0] = 1.0D;
               if (this.on[var1][0] < 0.5D) {
                  break;
               }

               this.stage[var1] = 1;
               this.stage_ix[var1] = (int)(Math.pow(2.0D, this.delay[var1][0] / 1200.0D) / this.control_time);
               if (this.stage_ix[var1] < 0) {
                  this.stage_ix[var1] = 0;
               }
            case 1:
               if (this.stage_ix[var1] == 0) {
                  var2 = this.attack[var1][0];
                  var4 = this.attack2[var1][0];
                  if (var4 < 1.0E-6D && var2 < 0.0D && Double.isInfinite(var2)) {
                     this.out[var1][0] = 1.0D;
                     this.stage[var1] = 3;
                     this.stage_count[var1] = (int)(Math.pow(2.0D, this.hold[var1][0] / 1200.0D) / this.control_time);
                     this.stage_ix[var1] = 0;
                     break;
                  }

                  this.stage[var1] = 2;
                  this.stage_count[var1] = (int)(Math.pow(2.0D, var2 / 1200.0D) / this.control_time);
                  var10000 = this.stage_count;
                  var10000[var1] += (int)(var4 / (this.control_time * 1000.0D));
                  if (this.stage_count[var1] < 0) {
                     this.stage_count[var1] = 0;
                  }

                  this.stage_ix[var1] = 0;
                  break;
               }

               var10002 = this.stage_ix[var1]--;
               break;
            case 2:
               var10002 = this.stage_ix[var1]++;
               if (this.stage_ix[var1] >= this.stage_count[var1]) {
                  this.out[var1][0] = 1.0D;
                  this.stage[var1] = 3;
               } else {
                  var2 = (double)this.stage_ix[var1] / (double)this.stage_count[var1];
                  var2 = 1.0D + 0.4166666666666667D / Math.log(10.0D) * Math.log(var2);
                  if (var2 < 0.0D) {
                     var2 = 0.0D;
                  } else if (var2 > 1.0D) {
                     var2 = 1.0D;
                  }

                  this.out[var1][0] = var2;
               }
               break;
            case 3:
               var10002 = this.stage_ix[var1]++;
               if (this.stage_ix[var1] >= this.stage_count[var1]) {
                  this.stage[var1] = 4;
                  this.stage_count[var1] = (int)(Math.pow(2.0D, this.decay[var1][0] / 1200.0D) / this.control_time);
                  var10000 = this.stage_count;
                  var10000[var1] += (int)(this.decay2[var1][0] / (this.control_time * 1000.0D));
                  if (this.stage_count[var1] < 0) {
                     this.stage_count[var1] = 0;
                  }

                  this.stage_ix[var1] = 0;
               }
               break;
            case 4:
               var10002 = this.stage_ix[var1]++;
               var2 = this.sustain[var1][0] * 0.001D;
               if (this.stage_ix[var1] >= this.stage_count[var1]) {
                  this.out[var1][0] = var2;
                  this.stage[var1] = 5;
                  if (var2 < 0.001D) {
                     this.out[var1][0] = 0.0D;
                     this.active[var1][0] = 0.0D;
                     this.stage[var1] = 8;
                  }
               } else {
                  var4 = (double)this.stage_ix[var1] / (double)this.stage_count[var1];
                  this.out[var1][0] = 1.0D - var4 + var2 * var4;
               }
            case 5:
            default:
               break;
            case 6:
               var10002 = this.stage_ix[var1]++;
               if (this.stage_ix[var1] >= this.stage_count[var1]) {
                  this.out[var1][0] = 0.0D;
                  this.active[var1][0] = 0.0D;
                  this.stage[var1] = 8;
               } else {
                  var4 = (double)this.stage_ix[var1] / (double)this.stage_count[var1];
                  this.out[var1][0] = 1.0D - var4;
                  if (this.on[var1][0] < -0.5D) {
                     this.stage_count[var1] = (int)(Math.pow(2.0D, this.shutdown[var1][0] / 1200.0D) / this.control_time);
                     if (this.stage_count[var1] < 0) {
                        this.stage_count[var1] = 0;
                     }

                     this.stage_v[var1] = this.out[var1][0];
                     this.stage_ix[var1] = 0;
                     this.stage[var1] = 7;
                  }

                  if (this.on[var1][0] > 0.5D) {
                     var2 = this.sustain[var1][0] * 0.001D;
                     if (this.out[var1][0] > var2) {
                        this.stage[var1] = 4;
                        this.stage_count[var1] = (int)(Math.pow(2.0D, this.decay[var1][0] / 1200.0D) / this.control_time);
                        var10000 = this.stage_count;
                        var10000[var1] += (int)(this.decay2[var1][0] / (this.control_time * 1000.0D));
                        if (this.stage_count[var1] < 0) {
                           this.stage_count[var1] = 0;
                        }

                        var4 = (this.out[var1][0] - 1.0D) / (var2 - 1.0D);
                        this.stage_ix[var1] = (int)((double)this.stage_count[var1] * var4);
                     }
                  }
               }
               break;
            case 7:
               var10002 = this.stage_ix[var1]++;
               if (this.stage_ix[var1] >= this.stage_count[var1]) {
                  this.out[var1][0] = 0.0D;
                  this.active[var1][0] = 0.0D;
                  this.stage[var1] = 8;
               } else {
                  var4 = (double)this.stage_ix[var1] / (double)this.stage_count[var1];
                  this.out[var1][0] = (1.0D - var4) * this.stage_v[var1];
               }
            }
         }
      }

   }
}
