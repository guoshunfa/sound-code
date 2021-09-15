package com.sun.media.sound;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.sound.midi.VoiceStatus;

public final class SoftVoice extends VoiceStatus {
   public int exclusiveClass = 0;
   public boolean releaseTriggered = false;
   private int noteOn_noteNumber = 0;
   private int noteOn_velocity = 0;
   private int noteOff_velocity = 0;
   private int delay = 0;
   ModelChannelMixer channelmixer = null;
   double tunedKey = 0.0D;
   SoftTuning tuning = null;
   SoftChannel stealer_channel = null;
   ModelConnectionBlock[] stealer_extendedConnectionBlocks = null;
   SoftPerformer stealer_performer = null;
   ModelChannelMixer stealer_channelmixer = null;
   int stealer_voiceID = -1;
   int stealer_noteNumber = 0;
   int stealer_velocity = 0;
   boolean stealer_releaseTriggered = false;
   int voiceID = -1;
   boolean sustain = false;
   boolean sostenuto = false;
   boolean portamento = false;
   private final SoftFilter filter_left;
   private final SoftFilter filter_right;
   private final SoftProcess eg = new SoftEnvelopeGenerator();
   private final SoftProcess lfo = new SoftLowFrequencyOscillator();
   Map<String, SoftControl> objects = new HashMap();
   SoftSynthesizer synthesizer;
   SoftInstrument instrument;
   SoftPerformer performer;
   SoftChannel softchannel = null;
   boolean on = false;
   private boolean audiostarted = false;
   private boolean started = false;
   private boolean stopping = false;
   private float osc_attenuation = 0.0F;
   private ModelOscillatorStream osc_stream;
   private int osc_stream_nrofchannels;
   private float[][] osc_buff = new float[2][];
   private boolean osc_stream_off_transmitted = false;
   private boolean out_mixer_end = false;
   private float out_mixer_left = 0.0F;
   private float out_mixer_right = 0.0F;
   private float out_mixer_effect1 = 0.0F;
   private float out_mixer_effect2 = 0.0F;
   private float last_out_mixer_left = 0.0F;
   private float last_out_mixer_right = 0.0F;
   private float last_out_mixer_effect1 = 0.0F;
   private float last_out_mixer_effect2 = 0.0F;
   ModelConnectionBlock[] extendedConnectionBlocks = null;
   private ModelConnectionBlock[] connections;
   private double[] connections_last = new double[50];
   private double[][][] connections_src = new double[50][3][];
   private int[][] connections_src_kc = new int[50][3];
   private double[][] connections_dst = new double[50][];
   private boolean soundoff = false;
   private float lastMuteValue = 0.0F;
   private float lastSoloMuteValue = 0.0F;
   double[] co_noteon_keynumber = new double[1];
   double[] co_noteon_velocity = new double[1];
   double[] co_noteon_on = new double[1];
   private final SoftControl co_noteon = new SoftControl() {
      double[] keynumber;
      double[] velocity;
      double[] on;

      {
         this.keynumber = SoftVoice.this.co_noteon_keynumber;
         this.velocity = SoftVoice.this.co_noteon_velocity;
         this.on = SoftVoice.this.co_noteon_on;
      }

      public double[] get(int var1, String var2) {
         if (var2 == null) {
            return null;
         } else if (var2.equals("keynumber")) {
            return this.keynumber;
         } else if (var2.equals("velocity")) {
            return this.velocity;
         } else {
            return var2.equals("on") ? this.on : null;
         }
      }
   };
   private final double[] co_mixer_active = new double[1];
   private final double[] co_mixer_gain = new double[1];
   private final double[] co_mixer_pan = new double[1];
   private final double[] co_mixer_balance = new double[1];
   private final double[] co_mixer_reverb = new double[1];
   private final double[] co_mixer_chorus = new double[1];
   private final SoftControl co_mixer = new SoftControl() {
      double[] active;
      double[] gain;
      double[] pan;
      double[] balance;
      double[] reverb;
      double[] chorus;

      {
         this.active = SoftVoice.this.co_mixer_active;
         this.gain = SoftVoice.this.co_mixer_gain;
         this.pan = SoftVoice.this.co_mixer_pan;
         this.balance = SoftVoice.this.co_mixer_balance;
         this.reverb = SoftVoice.this.co_mixer_reverb;
         this.chorus = SoftVoice.this.co_mixer_chorus;
      }

      public double[] get(int var1, String var2) {
         if (var2 == null) {
            return null;
         } else if (var2.equals("active")) {
            return this.active;
         } else if (var2.equals("gain")) {
            return this.gain;
         } else if (var2.equals("pan")) {
            return this.pan;
         } else if (var2.equals("balance")) {
            return this.balance;
         } else if (var2.equals("reverb")) {
            return this.reverb;
         } else {
            return var2.equals("chorus") ? this.chorus : null;
         }
      }
   };
   private final double[] co_osc_pitch = new double[1];
   private final SoftControl co_osc = new SoftControl() {
      double[] pitch;

      {
         this.pitch = SoftVoice.this.co_osc_pitch;
      }

      public double[] get(int var1, String var2) {
         if (var2 == null) {
            return null;
         } else {
            return var2.equals("pitch") ? this.pitch : null;
         }
      }
   };
   private final double[] co_filter_freq = new double[1];
   private final double[] co_filter_type = new double[1];
   private final double[] co_filter_q = new double[1];
   private final SoftControl co_filter = new SoftControl() {
      double[] freq;
      double[] ftype;
      double[] q;

      {
         this.freq = SoftVoice.this.co_filter_freq;
         this.ftype = SoftVoice.this.co_filter_type;
         this.q = SoftVoice.this.co_filter_q;
      }

      public double[] get(int var1, String var2) {
         if (var2 == null) {
            return null;
         } else if (var2.equals("freq")) {
            return this.freq;
         } else if (var2.equals("type")) {
            return this.ftype;
         } else {
            return var2.equals("q") ? this.q : null;
         }
      }
   };
   SoftResamplerStreamer resampler;
   private final int nrofchannels;

   public SoftVoice(SoftSynthesizer var1) {
      this.synthesizer = var1;
      this.filter_left = new SoftFilter(var1.getFormat().getSampleRate());
      this.filter_right = new SoftFilter(var1.getFormat().getSampleRate());
      this.nrofchannels = var1.getFormat().getChannels();
   }

   private int getValueKC(ModelIdentifier var1) {
      if (var1.getObject().equals("midi_cc")) {
         int var2 = Integer.parseInt(var1.getVariable());
         if (var2 != 0 && var2 != 32 && var2 < 120) {
            return var2;
         }
      } else if (var1.getObject().equals("midi_rpn")) {
         if (var1.getVariable().equals("1")) {
            return 120;
         }

         if (var1.getVariable().equals("2")) {
            return 121;
         }
      }

      return -1;
   }

   private double[] getValue(ModelIdentifier var1) {
      SoftControl var2 = (SoftControl)this.objects.get(var1.getObject());
      return var2 == null ? null : var2.get(var1.getInstance(), var1.getVariable());
   }

   private double transformValue(double var1, ModelSource var3) {
      return var3.getTransform() != null ? var3.getTransform().transform(var1) : var1;
   }

   private double transformValue(double var1, ModelDestination var3) {
      return var3.getTransform() != null ? var3.getTransform().transform(var1) : var1;
   }

   private double processKeyBasedController(double var1, int var3) {
      if (var3 == -1) {
         return var1;
      } else {
         if (this.softchannel.keybasedcontroller_active != null && this.softchannel.keybasedcontroller_active[this.note] != null && this.softchannel.keybasedcontroller_active[this.note][var3]) {
            double var4 = this.softchannel.keybasedcontroller_value[this.note][var3];
            if (var3 == 10 || var3 == 91 || var3 == 93) {
               return var4;
            }

            var1 += var4 * 2.0D - 1.0D;
            if (var1 > 1.0D) {
               var1 = 1.0D;
            } else if (var1 < 0.0D) {
               var1 = 0.0D;
            }
         }

         return var1;
      }
   }

   private void processConnection(int var1) {
      ModelConnectionBlock var2 = this.connections[var1];
      double[][] var3 = this.connections_src[var1];
      double[] var4 = this.connections_dst[var1];
      if (var4 != null && !Double.isInfinite(var4[0])) {
         double var5 = var2.getScale();
         ModelSource[] var7;
         if (this.softchannel.keybasedcontroller_active == null) {
            var7 = var2.getSources();

            for(int var8 = 0; var8 < var7.length; ++var8) {
               var5 *= this.transformValue(var3[var8][0], var7[var8]);
               if (var5 == 0.0D) {
                  break;
               }
            }
         } else {
            var7 = var2.getSources();
            int[] var10 = this.connections_src_kc[var1];

            for(int var9 = 0; var9 < var7.length; ++var9) {
               var5 *= this.transformValue(this.processKeyBasedController(var3[var9][0], var10[var9]), var7[var9]);
               if (var5 == 0.0D) {
                  break;
               }
            }
         }

         var5 = this.transformValue(var5, var2.getDestination());
         var4[0] = var4[0] - this.connections_last[var1] + var5;
         this.connections_last[var1] = var5;
      }
   }

   void updateTuning(SoftTuning var1) {
      this.tuning = var1;
      this.tunedKey = this.tuning.getTuning(this.note) / 100.0D;
      if (!this.portamento) {
         this.co_noteon_keynumber[0] = this.tunedKey * 0.0078125D;
         if (this.performer == null) {
            return;
         }

         int[] var2 = this.performer.midi_connections[4];
         if (var2 == null) {
            return;
         }

         for(int var3 = 0; var3 < var2.length; ++var3) {
            this.processConnection(var2[var3]);
         }
      }

   }

   void setNote(int var1) {
      this.note = var1;
      this.tunedKey = this.tuning.getTuning(var1) / 100.0D;
   }

   void noteOn(int var1, int var2, int var3) {
      this.sustain = false;
      this.sostenuto = false;
      this.portamento = false;
      this.soundoff = false;
      this.on = true;
      this.active = true;
      this.started = true;
      this.noteOn_noteNumber = var1;
      this.noteOn_velocity = var2;
      this.delay = var3;
      this.lastMuteValue = 0.0F;
      this.lastSoloMuteValue = 0.0F;
      this.setNote(var1);
      if (this.performer.forcedKeynumber) {
         this.co_noteon_keynumber[0] = 0.0D;
      } else {
         this.co_noteon_keynumber[0] = this.tunedKey * 0.0078125D;
      }

      if (this.performer.forcedVelocity) {
         this.co_noteon_velocity[0] = 0.0D;
      } else {
         this.co_noteon_velocity[0] = (double)((float)var2 * 0.0078125F);
      }

      this.co_mixer_active[0] = 0.0D;
      this.co_mixer_gain[0] = 0.0D;
      this.co_mixer_pan[0] = 0.0D;
      this.co_mixer_balance[0] = 0.0D;
      this.co_mixer_reverb[0] = 0.0D;
      this.co_mixer_chorus[0] = 0.0D;
      this.co_osc_pitch[0] = 0.0D;
      this.co_filter_freq[0] = 0.0D;
      this.co_filter_q[0] = 0.0D;
      this.co_filter_type[0] = 0.0D;
      this.co_noteon_on[0] = 1.0D;
      this.eg.reset();
      this.lfo.reset();
      this.filter_left.reset();
      this.filter_right.reset();
      this.objects.put("master", this.synthesizer.getMainMixer().co_master);
      this.objects.put("eg", this.eg);
      this.objects.put("lfo", this.lfo);
      this.objects.put("noteon", this.co_noteon);
      this.objects.put("osc", this.co_osc);
      this.objects.put("mixer", this.co_mixer);
      this.objects.put("filter", this.co_filter);
      this.connections = this.performer.connections;
      if (this.connections_last == null || this.connections_last.length < this.connections.length) {
         this.connections_last = new double[this.connections.length];
      }

      if (this.connections_src == null || this.connections_src.length < this.connections.length) {
         this.connections_src = new double[this.connections.length][][];
         this.connections_src_kc = new int[this.connections.length][];
      }

      if (this.connections_dst == null || this.connections_dst.length < this.connections.length) {
         this.connections_dst = new double[this.connections.length][];
      }

      int var4;
      for(var4 = 0; var4 < this.connections.length; ++var4) {
         ModelConnectionBlock var5 = this.connections[var4];
         this.connections_last[var4] = 0.0D;
         if (var5.getSources() != null) {
            ModelSource[] var6 = var5.getSources();
            if (this.connections_src[var4] == null || this.connections_src[var4].length < var6.length) {
               this.connections_src[var4] = new double[var6.length][];
               this.connections_src_kc[var4] = new int[var6.length];
            }

            double[][] var7 = this.connections_src[var4];
            int[] var8 = this.connections_src_kc[var4];
            this.connections_src[var4] = var7;

            for(int var9 = 0; var9 < var6.length; ++var9) {
               var8[var9] = this.getValueKC(var6[var9].getIdentifier());
               var7[var9] = this.getValue(var6[var9].getIdentifier());
            }
         }

         if (var5.getDestination() != null) {
            this.connections_dst[var4] = this.getValue(var5.getDestination().getIdentifier());
         } else {
            this.connections_dst[var4] = null;
         }
      }

      for(var4 = 0; var4 < this.connections.length; ++var4) {
         this.processConnection(var4);
      }

      if (this.extendedConnectionBlocks != null) {
         ModelConnectionBlock[] var17 = this.extendedConnectionBlocks;
         int var18 = var17.length;

         for(int var19 = 0; var19 < var18; ++var19) {
            ModelConnectionBlock var20 = var17[var19];
            double var21 = 0.0D;
            ModelSource[] var10;
            int var11;
            int var12;
            ModelSource var13;
            double var14;
            ModelTransform var16;
            if (this.softchannel.keybasedcontroller_active == null) {
               var10 = var20.getSources();
               var11 = var10.length;

               for(var12 = 0; var12 < var11; ++var12) {
                  var13 = var10[var12];
                  var14 = this.getValue(var13.getIdentifier())[0];
                  var16 = var13.getTransform();
                  if (var16 == null) {
                     var21 += var14;
                  } else {
                     var21 += var16.transform(var14);
                  }
               }
            } else {
               var10 = var20.getSources();
               var11 = var10.length;

               for(var12 = 0; var12 < var11; ++var12) {
                  var13 = var10[var12];
                  var14 = this.getValue(var13.getIdentifier())[0];
                  var14 = this.processKeyBasedController(var14, this.getValueKC(var13.getIdentifier()));
                  var16 = var13.getTransform();
                  if (var16 == null) {
                     var21 += var14;
                  } else {
                     var21 += var16.transform(var14);
                  }
               }
            }

            ModelDestination var22 = var20.getDestination();
            ModelTransform var23 = var22.getTransform();
            if (var23 != null) {
               var21 = var23.transform(var21);
            }

            double[] var10000 = this.getValue(var22.getIdentifier());
            var10000[0] += var21;
         }
      }

      this.eg.init(this.synthesizer);
      this.lfo.init(this.synthesizer);
   }

   void setPolyPressure(int var1) {
      if (this.performer != null) {
         int[] var2 = this.performer.midi_connections[2];
         if (var2 != null) {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               this.processConnection(var2[var3]);
            }

         }
      }
   }

   void setChannelPressure(int var1) {
      if (this.performer != null) {
         int[] var2 = this.performer.midi_connections[1];
         if (var2 != null) {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               this.processConnection(var2[var3]);
            }

         }
      }
   }

   void controlChange(int var1, int var2) {
      if (this.performer != null) {
         int[] var3 = this.performer.midi_ctrl_connections[var1];
         if (var3 != null) {
            for(int var4 = 0; var4 < var3.length; ++var4) {
               this.processConnection(var3[var4]);
            }

         }
      }
   }

   void nrpnChange(int var1, int var2) {
      if (this.performer != null) {
         int[] var3 = (int[])this.performer.midi_nrpn_connections.get(var1);
         if (var3 != null) {
            for(int var4 = 0; var4 < var3.length; ++var4) {
               this.processConnection(var3[var4]);
            }

         }
      }
   }

   void rpnChange(int var1, int var2) {
      if (this.performer != null) {
         int[] var3 = (int[])this.performer.midi_rpn_connections.get(var1);
         if (var3 != null) {
            for(int var4 = 0; var4 < var3.length; ++var4) {
               this.processConnection(var3[var4]);
            }

         }
      }
   }

   void setPitchBend(int var1) {
      if (this.performer != null) {
         int[] var2 = this.performer.midi_connections[0];
         if (var2 != null) {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               this.processConnection(var2[var3]);
            }

         }
      }
   }

   void setMute(boolean var1) {
      double[] var10000 = this.co_mixer_gain;
      var10000[0] -= (double)this.lastMuteValue;
      this.lastMuteValue = var1 ? -960.0F : 0.0F;
      var10000 = this.co_mixer_gain;
      var10000[0] += (double)this.lastMuteValue;
   }

   void setSoloMute(boolean var1) {
      double[] var10000 = this.co_mixer_gain;
      var10000[0] -= (double)this.lastSoloMuteValue;
      this.lastSoloMuteValue = var1 ? -960.0F : 0.0F;
      var10000 = this.co_mixer_gain;
      var10000[0] += (double)this.lastSoloMuteValue;
   }

   void shutdown() {
      if (this.co_noteon_on[0] >= -0.5D) {
         this.on = false;
         this.co_noteon_on[0] = -1.0D;
         if (this.performer != null) {
            int[] var1 = this.performer.midi_connections[3];
            if (var1 != null) {
               for(int var2 = 0; var2 < var1.length; ++var2) {
                  this.processConnection(var1[var2]);
               }

            }
         }
      }
   }

   void soundOff() {
      this.on = false;
      this.soundoff = true;
   }

   void noteOff(int var1) {
      if (this.on) {
         this.on = false;
         this.noteOff_velocity = var1;
         if (this.softchannel.sustain) {
            this.sustain = true;
         } else if (!this.sostenuto) {
            this.co_noteon_on[0] = 0.0D;
            if (this.performer != null) {
               int[] var2 = this.performer.midi_connections[3];
               if (var2 != null) {
                  for(int var3 = 0; var3 < var2.length; ++var3) {
                     this.processConnection(var2[var3]);
                  }

               }
            }
         }
      }
   }

   void redamp() {
      if (this.co_noteon_on[0] <= 0.5D) {
         if (this.co_noteon_on[0] >= -0.5D) {
            this.sustain = true;
            this.co_noteon_on[0] = 1.0D;
            if (this.performer != null) {
               int[] var1 = this.performer.midi_connections[3];
               if (var1 != null) {
                  for(int var2 = 0; var2 < var1.length; ++var2) {
                     this.processConnection(var1[var2]);
                  }

               }
            }
         }
      }
   }

   void processControlLogic() {
      if (this.stopping) {
         this.active = false;
         this.stopping = false;
         this.audiostarted = false;
         this.instrument = null;
         this.performer = null;
         this.connections = null;
         this.extendedConnectionBlocks = null;
         this.channelmixer = null;
         if (this.osc_stream != null) {
            try {
               this.osc_stream.close();
            } catch (IOException var12) {
            }
         }

         if (this.stealer_channel != null) {
            this.stealer_channel.initVoice(this, this.stealer_performer, this.stealer_voiceID, this.stealer_noteNumber, this.stealer_velocity, 0, this.stealer_extendedConnectionBlocks, this.stealer_channelmixer, this.stealer_releaseTriggered);
            this.stealer_releaseTriggered = false;
            this.stealer_channel = null;
            this.stealer_performer = null;
            this.stealer_voiceID = -1;
            this.stealer_noteNumber = 0;
            this.stealer_velocity = 0;
            this.stealer_extendedConnectionBlocks = null;
            this.stealer_channelmixer = null;
         }
      }

      if (this.started) {
         this.audiostarted = true;
         ModelOscillator var1 = this.performer.oscillators[0];
         this.osc_stream_off_transmitted = false;
         if (var1 instanceof ModelWavetable) {
            try {
               this.resampler.open((ModelWavetable)var1, this.synthesizer.getFormat().getSampleRate());
               this.osc_stream = this.resampler;
            } catch (IOException var11) {
            }
         } else {
            this.osc_stream = var1.open(this.synthesizer.getFormat().getSampleRate());
         }

         this.osc_attenuation = var1.getAttenuation();
         this.osc_stream_nrofchannels = var1.getChannels();
         if (this.osc_buff == null || this.osc_buff.length < this.osc_stream_nrofchannels) {
            this.osc_buff = new float[this.osc_stream_nrofchannels][];
         }

         if (this.osc_stream != null) {
            this.osc_stream.noteOn(this.softchannel, this, this.noteOn_noteNumber, this.noteOn_velocity);
         }
      }

      if (this.audiostarted) {
         if (this.portamento) {
            double var13 = this.tunedKey - this.co_noteon_keynumber[0] * 128.0D;
            double var3 = Math.abs(var13);
            if (var3 < 1.0E-10D) {
               this.co_noteon_keynumber[0] = this.tunedKey * 0.0078125D;
               this.portamento = false;
            } else {
               if (var3 > this.softchannel.portamento_time) {
                  var13 = Math.signum(var13) * this.softchannel.portamento_time;
               }

               double[] var10000 = this.co_noteon_keynumber;
               var10000[0] += var13 * 0.0078125D;
            }

            int[] var5 = this.performer.midi_connections[4];
            if (var5 == null) {
               return;
            }

            for(int var6 = 0; var6 < var5.length; ++var6) {
               this.processConnection(var5[var6]);
            }
         }

         this.eg.processControlLogic();
         this.lfo.processControlLogic();

         int var14;
         for(var14 = 0; var14 < this.performer.ctrl_connections.length; ++var14) {
            this.processConnection(this.performer.ctrl_connections[var14]);
         }

         this.osc_stream.setPitch((float)this.co_osc_pitch[0]);
         var14 = (int)this.co_filter_type[0];
         double var2;
         if (this.co_filter_freq[0] == 13500.0D) {
            var2 = 19912.126958213175D;
         } else {
            var2 = 440.0D * Math.exp((this.co_filter_freq[0] - 6900.0D) * (Math.log(2.0D) / 1200.0D));
         }

         double var4 = this.co_filter_q[0] / 10.0D;
         this.filter_left.setFilterType(var14);
         this.filter_left.setFrequency(var2);
         this.filter_left.setResonance(var4);
         this.filter_right.setFilterType(var14);
         this.filter_right.setFrequency(var2);
         this.filter_right.setResonance(var4);
         float var15 = (float)Math.exp(((double)(-this.osc_attenuation) + this.co_mixer_gain[0]) * (Math.log(10.0D) / 200.0D));
         if (this.co_mixer_gain[0] <= -960.0D) {
            var15 = 0.0F;
         }

         if (this.soundoff) {
            this.stopping = true;
            var15 = 0.0F;
         }

         this.volume = (int)(Math.sqrt((double)var15) * 128.0D);
         double var7 = this.co_mixer_pan[0] * 0.001D;
         if (var7 < 0.0D) {
            var7 = 0.0D;
         } else if (var7 > 1.0D) {
            var7 = 1.0D;
         }

         if (var7 == 0.5D) {
            this.out_mixer_left = var15 * 0.70710677F;
            this.out_mixer_right = this.out_mixer_left;
         } else {
            this.out_mixer_left = var15 * (float)Math.cos(var7 * 3.141592653589793D * 0.5D);
            this.out_mixer_right = var15 * (float)Math.sin(var7 * 3.141592653589793D * 0.5D);
         }

         double var9 = this.co_mixer_balance[0] * 0.001D;
         if (var9 != 0.5D) {
            if (var9 > 0.5D) {
               this.out_mixer_left = (float)((double)this.out_mixer_left * (1.0D - var9) * 2.0D);
            } else {
               this.out_mixer_right = (float)((double)this.out_mixer_right * var9 * 2.0D);
            }
         }

         if (this.synthesizer.reverb_on) {
            this.out_mixer_effect1 = (float)(this.co_mixer_reverb[0] * 0.001D);
            this.out_mixer_effect1 *= var15;
         } else {
            this.out_mixer_effect1 = 0.0F;
         }

         if (this.synthesizer.chorus_on) {
            this.out_mixer_effect2 = (float)(this.co_mixer_chorus[0] * 0.001D);
            this.out_mixer_effect2 *= var15;
         } else {
            this.out_mixer_effect2 = 0.0F;
         }

         this.out_mixer_end = this.co_mixer_active[0] < 0.5D;
         if (!this.on && !this.osc_stream_off_transmitted) {
            this.osc_stream_off_transmitted = true;
            if (this.osc_stream != null) {
               this.osc_stream.noteOff(this.noteOff_velocity);
            }
         }
      }

      if (this.started) {
         this.last_out_mixer_left = this.out_mixer_left;
         this.last_out_mixer_right = this.out_mixer_right;
         this.last_out_mixer_effect1 = this.out_mixer_effect1;
         this.last_out_mixer_effect2 = this.out_mixer_effect2;
         this.started = false;
      }

   }

   void mixAudioStream(SoftAudioBuffer var1, SoftAudioBuffer var2, SoftAudioBuffer var3, float var4, float var5) {
      int var6 = var1.getSize();
      if ((double)var4 >= 1.0E-9D || (double)var5 >= 1.0E-9D) {
         float[] var7;
         float[] var8;
         int var9;
         float[] var10;
         int var11;
         float var13;
         float var14;
         float[] var15;
         if (var3 != null && this.delay != 0) {
            if (var4 == var5) {
               var7 = var2.array();
               var8 = var1.array();
               var9 = 0;

               int var16;
               for(var16 = this.delay; var16 < var6; ++var16) {
                  var7[var16] += var8[var9++] * var5;
               }

               var7 = var3.array();

               for(var16 = 0; var16 < this.delay; ++var16) {
                  var7[var16] += var8[var9++] * var5;
               }
            } else {
               var13 = var4;
               var14 = (var5 - var4) / (float)var6;
               var15 = var2.array();
               var10 = var1.array();
               var11 = 0;

               int var12;
               for(var12 = this.delay; var12 < var6; ++var12) {
                  var13 += var14;
                  var15[var12] += var10[var11++] * var13;
               }

               var15 = var3.array();

               for(var12 = 0; var12 < this.delay; ++var12) {
                  var13 += var14;
                  var15[var12] += var10[var11++] * var13;
               }
            }
         } else if (var4 == var5) {
            var7 = var2.array();
            var8 = var1.array();

            for(var9 = 0; var9 < var6; ++var9) {
               var7[var9] += var8[var9] * var5;
            }
         } else {
            var13 = var4;
            var14 = (var5 - var4) / (float)var6;
            var15 = var2.array();
            var10 = var1.array();

            for(var11 = 0; var11 < var6; ++var11) {
               var13 += var14;
               var15[var11] += var10[var11] * var13;
            }
         }

      }
   }

   void processAudioLogic(SoftAudioBuffer[] var1) {
      if (this.audiostarted) {
         int var2 = var1[0].getSize();

         try {
            this.osc_buff[0] = var1[10].array();
            if (this.nrofchannels != 1) {
               this.osc_buff[1] = var1[11].array();
            }

            int var3 = this.osc_stream.read(this.osc_buff, 0, var2);
            if (var3 == -1) {
               this.stopping = true;
               return;
            }

            if (var3 != var2) {
               Arrays.fill(this.osc_buff[0], var3, var2, 0.0F);
               if (this.nrofchannels != 1) {
                  Arrays.fill(this.osc_buff[1], var3, var2, 0.0F);
               }
            }
         } catch (IOException var15) {
         }

         SoftAudioBuffer var16 = var1[0];
         SoftAudioBuffer var4 = var1[1];
         SoftAudioBuffer var5 = var1[2];
         SoftAudioBuffer var6 = var1[6];
         SoftAudioBuffer var7 = var1[7];
         SoftAudioBuffer var8 = var1[3];
         SoftAudioBuffer var9 = var1[4];
         SoftAudioBuffer var10 = var1[5];
         SoftAudioBuffer var11 = var1[8];
         SoftAudioBuffer var12 = var1[9];
         SoftAudioBuffer var13 = var1[10];
         SoftAudioBuffer var14 = var1[11];
         if (this.osc_stream_nrofchannels == 1) {
            var14 = null;
         }

         if (!Double.isInfinite(this.co_filter_freq[0])) {
            this.filter_left.processAudio(var13);
            if (var14 != null) {
               this.filter_right.processAudio(var14);
            }
         }

         if (this.nrofchannels == 1) {
            this.out_mixer_left = (this.out_mixer_left + this.out_mixer_right) / 2.0F;
            this.mixAudioStream(var13, var16, var8, this.last_out_mixer_left, this.out_mixer_left);
            if (var14 != null) {
               this.mixAudioStream(var14, var16, var8, this.last_out_mixer_left, this.out_mixer_left);
            }
         } else if (var14 == null && this.last_out_mixer_left == this.last_out_mixer_right && this.out_mixer_left == this.out_mixer_right) {
            this.mixAudioStream(var13, var5, var10, this.last_out_mixer_left, this.out_mixer_left);
         } else {
            this.mixAudioStream(var13, var16, var8, this.last_out_mixer_left, this.out_mixer_left);
            if (var14 != null) {
               this.mixAudioStream(var14, var4, var9, this.last_out_mixer_right, this.out_mixer_right);
            } else {
               this.mixAudioStream(var13, var4, var9, this.last_out_mixer_right, this.out_mixer_right);
            }
         }

         if (var14 == null) {
            this.mixAudioStream(var13, var6, var11, this.last_out_mixer_effect1, this.out_mixer_effect1);
            this.mixAudioStream(var13, var7, var12, this.last_out_mixer_effect2, this.out_mixer_effect2);
         } else {
            this.mixAudioStream(var13, var6, var11, this.last_out_mixer_effect1 * 0.5F, this.out_mixer_effect1 * 0.5F);
            this.mixAudioStream(var13, var7, var12, this.last_out_mixer_effect2 * 0.5F, this.out_mixer_effect2 * 0.5F);
            this.mixAudioStream(var14, var6, var11, this.last_out_mixer_effect1 * 0.5F, this.out_mixer_effect1 * 0.5F);
            this.mixAudioStream(var14, var7, var12, this.last_out_mixer_effect2 * 0.5F, this.out_mixer_effect2 * 0.5F);
         }

         this.last_out_mixer_left = this.out_mixer_left;
         this.last_out_mixer_right = this.out_mixer_right;
         this.last_out_mixer_effect1 = this.out_mixer_effect1;
         this.last_out_mixer_effect2 = this.out_mixer_effect2;
         if (this.out_mixer_end) {
            this.stopping = true;
         }

      }
   }
}
