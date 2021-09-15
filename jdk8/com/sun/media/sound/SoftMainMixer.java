package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Patch;
import javax.sound.midi.ShortMessage;
import javax.sound.sampled.AudioInputStream;

public final class SoftMainMixer {
   public static final int CHANNEL_LEFT = 0;
   public static final int CHANNEL_RIGHT = 1;
   public static final int CHANNEL_MONO = 2;
   public static final int CHANNEL_DELAY_LEFT = 3;
   public static final int CHANNEL_DELAY_RIGHT = 4;
   public static final int CHANNEL_DELAY_MONO = 5;
   public static final int CHANNEL_EFFECT1 = 6;
   public static final int CHANNEL_EFFECT2 = 7;
   public static final int CHANNEL_DELAY_EFFECT1 = 8;
   public static final int CHANNEL_DELAY_EFFECT2 = 9;
   public static final int CHANNEL_LEFT_DRY = 10;
   public static final int CHANNEL_RIGHT_DRY = 11;
   public static final int CHANNEL_SCRATCH1 = 12;
   public static final int CHANNEL_SCRATCH2 = 13;
   boolean active_sensing_on = false;
   private long msec_last_activity = -1L;
   private boolean pusher_silent = false;
   private int pusher_silent_count = 0;
   private long sample_pos = 0L;
   boolean readfully = true;
   private final Object control_mutex;
   private SoftSynthesizer synth;
   private float samplerate = 44100.0F;
   private int nrofchannels = 2;
   private SoftVoice[] voicestatus = null;
   private SoftAudioBuffer[] buffers;
   private SoftReverb reverb;
   private SoftAudioProcessor chorus;
   private SoftAudioProcessor agc;
   private long msec_buffer_len = 0L;
   private int buffer_len = 0;
   TreeMap<Long, Object> midimessages = new TreeMap();
   private int delay_midievent = 0;
   private int max_delay_midievent = 0;
   double last_volume_left = 1.0D;
   double last_volume_right = 1.0D;
   private double[] co_master_balance = new double[1];
   private double[] co_master_volume = new double[1];
   private double[] co_master_coarse_tuning = new double[1];
   private double[] co_master_fine_tuning = new double[1];
   private AudioInputStream ais;
   private Set<SoftMainMixer.SoftChannelMixerContainer> registeredMixers = null;
   private Set<ModelChannelMixer> stoppedMixers = null;
   private SoftMainMixer.SoftChannelMixerContainer[] cur_registeredMixers = null;
   SoftControl co_master = new SoftControl() {
      double[] balance;
      double[] volume;
      double[] coarse_tuning;
      double[] fine_tuning;

      {
         this.balance = SoftMainMixer.this.co_master_balance;
         this.volume = SoftMainMixer.this.co_master_volume;
         this.coarse_tuning = SoftMainMixer.this.co_master_coarse_tuning;
         this.fine_tuning = SoftMainMixer.this.co_master_fine_tuning;
      }

      public double[] get(int var1, String var2) {
         if (var2 == null) {
            return null;
         } else if (var2.equals("balance")) {
            return this.balance;
         } else if (var2.equals("volume")) {
            return this.volume;
         } else if (var2.equals("coarse_tuning")) {
            return this.coarse_tuning;
         } else {
            return var2.equals("fine_tuning") ? this.fine_tuning : null;
         }
      }
   };

   private void processSystemExclusiveMessage(byte[] var1) {
      synchronized(this.synth.control_mutex) {
         this.activity();
         int var3;
         int var4;
         int var5;
         SoftTuning var6;
         int var7;
         SoftChannel[] var8;
         int var9;
         if ((var1[1] & 255) == 126) {
            var3 = var1[2] & 255;
            if (var3 == 127 || var3 == this.synth.getDeviceID()) {
               var4 = var1[3] & 255;
               label236:
               switch(var4) {
               case 8:
                  var5 = var1[4] & 255;
                  switch(var5) {
                  case 1:
                     var6 = this.synth.getTuning(new Patch(0, var1[5] & 255));
                     var6.load(var1);
                  case 2:
                  case 3:
                  default:
                     break label236;
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                     var6 = this.synth.getTuning(new Patch(var1[5] & 255, var1[6] & 255));
                     var6.load(var1);
                     break label236;
                  case 8:
                  case 9:
                     var6 = new SoftTuning(var1);
                     var7 = (var1[5] & 255) * 16384 + (var1[6] & 255) * 128 + (var1[7] & 255);
                     var8 = this.synth.channels;
                     var9 = 0;

                     while(true) {
                        if (var9 >= var8.length) {
                           break label236;
                        }

                        if ((var7 & 1 << var9) != 0) {
                           var8[var9].tuning = var6;
                        }

                        ++var9;
                     }
                  }
               case 9:
                  var5 = var1[4] & 255;
                  switch(var5) {
                  case 1:
                     this.synth.setGeneralMidiMode(1);
                     this.reset();
                     break label236;
                  case 2:
                     this.synth.setGeneralMidiMode(0);
                     this.reset();
                     break label236;
                  case 3:
                     this.synth.setGeneralMidiMode(2);
                     this.reset();
                  default:
                     break label236;
                  }
               case 10:
                  var5 = var1[4] & 255;
                  switch(var5) {
                  case 1:
                     if (this.synth.getGeneralMidiMode() == 0) {
                        this.synth.setGeneralMidiMode(1);
                     }

                     this.synth.voice_allocation_mode = 1;
                     this.reset();
                     break;
                  case 2:
                     this.synth.setGeneralMidiMode(0);
                     this.synth.voice_allocation_mode = 0;
                     this.reset();
                     break;
                  case 3:
                     this.synth.voice_allocation_mode = 0;
                     break;
                  case 4:
                     this.synth.voice_allocation_mode = 1;
                  }
               }
            }
         }

         if ((var1[1] & 255) == 127) {
            var3 = var1[2] & 255;
            if (var3 == 127 || var3 == this.synth.getDeviceID()) {
               var4 = var1[3] & 255;
               int var10;
               int var11;
               int var19;
               int var23;
               label219:
               switch(var4) {
               case 4:
                  var5 = var1[4] & 255;
                  switch(var5) {
                  case 1:
                  case 2:
                  case 3:
                  case 4:
                     var19 = (var1[5] & 127) + (var1[6] & 127) * 128;
                     if (var5 == 1) {
                        this.setVolume(var19);
                     } else if (var5 == 2) {
                        this.setBalance(var19);
                     } else if (var5 == 3) {
                        this.setFineTuning(var19);
                     } else if (var5 == 4) {
                        this.setCoarseTuning(var19);
                     }
                     break;
                  case 5:
                     byte var25 = 5;
                     var7 = var25 + 1;
                     var23 = var1[var25] & 255;
                     var9 = var1[var7++] & 255;
                     var10 = var1[var7++] & 255;
                     int[] var27 = new int[var23];

                     int var12;
                     for(var12 = 0; var12 < var23; ++var12) {
                        int var13 = var1[var7++] & 255;
                        int var14 = var1[var7++] & 255;
                        var27[var12] = var13 * 128 + var14;
                     }

                     var12 = (var1.length - 1 - var7) / (var9 + var10);
                     long[] var28 = new long[var12];
                     long[] var30 = new long[var12];

                     for(int var15 = 0; var15 < var12; ++var15) {
                        var30[var15] = 0L;

                        int var16;
                        for(var16 = 0; var16 < var9; ++var16) {
                           var28[var15] = var28[var15] * 128L + (long)(var1[var7++] & 255);
                        }

                        for(var16 = 0; var16 < var10; ++var16) {
                           var30[var15] = var30[var15] * 128L + (long)(var1[var7++] & 255);
                        }
                     }

                     this.globalParameterControlChange(var27, var28, var30);
                  }
               case 5:
               case 6:
               case 7:
               default:
                  break;
               case 8:
                  var5 = var1[4] & 255;
                  SoftVoice[] var24;
                  switch(var5) {
                  case 2:
                     var6 = this.synth.getTuning(new Patch(0, var1[5] & 255));
                     var6.load(var1);
                     var24 = this.synth.getVoices();

                     for(var23 = 0; var23 < var24.length; ++var23) {
                        if (var24[var23].active && var24[var23].tuning == var6) {
                           var24[var23].updateTuning(var6);
                        }
                     }
                     break label219;
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  default:
                     return;
                  case 7:
                     var6 = this.synth.getTuning(new Patch(var1[5] & 255, var1[6] & 255));
                     var6.load(var1);
                     var24 = this.synth.getVoices();

                     for(var23 = 0; var23 < var24.length; ++var23) {
                        if (var24[var23].active && var24[var23].tuning == var6) {
                           var24[var23].updateTuning(var6);
                        }
                     }

                     return;
                  case 8:
                  case 9:
                     var6 = new SoftTuning(var1);
                     var7 = (var1[5] & 255) * 16384 + (var1[6] & 255) * 128 + (var1[7] & 255);
                     var8 = this.synth.channels;

                     for(var9 = 0; var9 < var8.length; ++var9) {
                        if ((var7 & 1 << var9) != 0) {
                           var8[var9].tuning = var6;
                        }
                     }

                     SoftVoice[] var29 = this.synth.getVoices();

                     for(var10 = 0; var10 < var29.length; ++var10) {
                        if (var29[var10].active && (var7 & 1 << var29[var10].channel) != 0) {
                           var29[var10].updateTuning(var6);
                        }
                     }

                     return;
                  }
               case 9:
                  var5 = var1[4] & 255;
                  int[] var20;
                  int[] var21;
                  SoftChannel var26;
                  switch(var5) {
                  case 1:
                     var20 = new int[(var1.length - 7) / 2];
                     var21 = new int[(var1.length - 7) / 2];
                     var23 = 0;

                     for(var9 = 6; var9 < var1.length - 1; var9 += 2) {
                        var20[var23] = var1[var9] & 255;
                        var21[var23] = var1[var9 + 1] & 255;
                        ++var23;
                     }

                     var9 = var1[5] & 255;
                     var26 = this.synth.channels[var9];
                     var26.mapChannelPressureToDestination(var20, var21);
                     return;
                  case 2:
                     var20 = new int[(var1.length - 7) / 2];
                     var21 = new int[(var1.length - 7) / 2];
                     var23 = 0;

                     for(var9 = 6; var9 < var1.length - 1; var9 += 2) {
                        var20[var23] = var1[var9] & 255;
                        var21[var23] = var1[var9 + 1] & 255;
                        ++var23;
                     }

                     var9 = var1[5] & 255;
                     var26 = this.synth.channels[var9];
                     var26.mapPolyPressureToDestination(var20, var21);
                     return;
                  case 3:
                     var20 = new int[(var1.length - 7) / 2];
                     var21 = new int[(var1.length - 7) / 2];
                     var23 = 0;

                     for(var9 = 7; var9 < var1.length - 1; var9 += 2) {
                        var20[var23] = var1[var9] & 255;
                        var21[var23] = var1[var9 + 1] & 255;
                        ++var23;
                     }

                     var9 = var1[5] & 255;
                     var26 = this.synth.channels[var9];
                     var11 = var1[6] & 255;
                     var26.mapControlToDestination(var11, var20, var21);
                     return;
                  default:
                     return;
                  }
               case 10:
                  var5 = var1[4] & 255;
                  switch(var5) {
                  case 1:
                     var19 = var1[5] & 255;
                     var7 = var1[6] & 255;
                     SoftChannel var22 = this.synth.channels[var19];

                     for(var9 = 7; var9 < var1.length - 1; var9 += 2) {
                        var10 = var1[var9] & 255;
                        var11 = var1[var9 + 1] & 255;
                        var22.controlChangePerNote(var7, var10, var11);
                     }
                  }
               }
            }
         }

      }
   }

   private void processMessages(long var1) {
      Iterator var3 = this.midimessages.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)var3.next();
         if ((Long)var4.getKey() >= var1 + this.msec_buffer_len) {
            return;
         }

         long var5 = (Long)var4.getKey() - var1;
         this.delay_midievent = (int)((double)var5 * ((double)this.samplerate / 1000000.0D) + 0.5D);
         if (this.delay_midievent > this.max_delay_midievent) {
            this.delay_midievent = this.max_delay_midievent;
         }

         if (this.delay_midievent < 0) {
            this.delay_midievent = 0;
         }

         this.processMessage(var4.getValue());
         var3.remove();
      }

      this.delay_midievent = 0;
   }

   void processAudioBuffers() {
      if (this.synth.weakstream != null && this.synth.weakstream.silent_samples != 0L) {
         this.sample_pos += this.synth.weakstream.silent_samples;
         this.synth.weakstream.silent_samples = 0L;
      }

      for(int var1 = 0; var1 < this.buffers.length; ++var1) {
         if (var1 != 3 && var1 != 4 && var1 != 5 && var1 != 8 && var1 != 9) {
            this.buffers[var1].clear();
         }
      }

      if (!this.buffers[3].isSilent()) {
         this.buffers[0].swap(this.buffers[3]);
      }

      if (!this.buffers[4].isSilent()) {
         this.buffers[1].swap(this.buffers[4]);
      }

      if (!this.buffers[5].isSilent()) {
         this.buffers[2].swap(this.buffers[5]);
      }

      if (!this.buffers[8].isSilent()) {
         this.buffers[6].swap(this.buffers[8]);
      }

      if (!this.buffers[9].isSilent()) {
         this.buffers[7].swap(this.buffers[9]);
      }

      double var3;
      SoftMainMixer.SoftChannelMixerContainer[] var5;
      int var10;
      int var11;
      double var34;
      int var40;
      synchronized(this.control_mutex) {
         long var7 = (long)((double)this.sample_pos * (1000000.0D / (double)this.samplerate));
         this.processMessages(var7);
         if (this.active_sensing_on && var7 - this.msec_last_activity > 1000000L) {
            this.active_sensing_on = false;
            SoftChannel[] var9 = this.synth.channels;
            var10 = var9.length;

            for(var11 = 0; var11 < var10; ++var11) {
               SoftChannel var12 = var9[var11];
               var12.allSoundOff();
            }
         }

         var40 = 0;

         while(true) {
            if (var40 >= this.voicestatus.length) {
               this.sample_pos += (long)this.buffer_len;
               double var41 = this.co_master_volume[0];
               var34 = var41;
               var3 = var41;
               double var46 = this.co_master_balance[0];
               if (var46 > 0.5D) {
                  var34 = var41 * (1.0D - var46) * 2.0D;
               } else {
                  var3 = var41 * var46 * 2.0D;
               }

               this.chorus.processControlLogic();
               this.reverb.processControlLogic();
               this.agc.processControlLogic();
               if (this.cur_registeredMixers == null && this.registeredMixers != null) {
                  this.cur_registeredMixers = new SoftMainMixer.SoftChannelMixerContainer[this.registeredMixers.size()];
                  this.registeredMixers.toArray(this.cur_registeredMixers);
               }

               var5 = this.cur_registeredMixers;
               if (var5 != null && var5.length == 0) {
                  var5 = null;
               }
               break;
            }

            if (this.voicestatus[var40].active) {
               this.voicestatus[var40].processControlLogic();
            }

            ++var40;
         }
      }

      if (var5 != null) {
         SoftAudioBuffer var6 = this.buffers[0];
         SoftAudioBuffer var37 = this.buffers[1];
         SoftAudioBuffer var8 = this.buffers[2];
         SoftAudioBuffer var42 = this.buffers[3];
         SoftAudioBuffer var43 = this.buffers[4];
         SoftAudioBuffer var48 = this.buffers[5];
         int var49 = this.buffers[0].getSize();
         float[][] var13 = new float[this.nrofchannels][];
         float[][] var14 = new float[this.nrofchannels][];
         var14[0] = var6.array();
         if (this.nrofchannels != 1) {
            var14[1] = var37.array();
         }

         SoftMainMixer.SoftChannelMixerContainer[] var15 = var5;
         int var16 = var5.length;

         for(int var17 = 0; var17 < var16; ++var17) {
            SoftMainMixer.SoftChannelMixerContainer var18 = var15[var17];
            this.buffers[0] = var18.buffers[0];
            this.buffers[1] = var18.buffers[1];
            this.buffers[2] = var18.buffers[2];
            this.buffers[3] = var18.buffers[3];
            this.buffers[4] = var18.buffers[4];
            this.buffers[5] = var18.buffers[5];
            this.buffers[0].clear();
            this.buffers[1].clear();
            this.buffers[2].clear();
            if (!this.buffers[3].isSilent()) {
               this.buffers[0].swap(this.buffers[3]);
            }

            if (!this.buffers[4].isSilent()) {
               this.buffers[1].swap(this.buffers[4]);
            }

            if (!this.buffers[5].isSilent()) {
               this.buffers[2].swap(this.buffers[5]);
            }

            var13[0] = this.buffers[0].array();
            if (this.nrofchannels != 1) {
               var13[1] = this.buffers[1].array();
            }

            boolean var19 = false;

            int var20;
            for(var20 = 0; var20 < this.voicestatus.length; ++var20) {
               if (this.voicestatus[var20].active && this.voicestatus[var20].channelmixer == var18.mixer) {
                  this.voicestatus[var20].processAudioLogic(this.buffers);
                  var19 = true;
               }
            }

            float[] var21;
            int var23;
            float[] var52;
            if (!this.buffers[2].isSilent()) {
               float[] var51 = this.buffers[2].array();
               var21 = this.buffers[0].array();
               if (this.nrofchannels != 1) {
                  var52 = this.buffers[1].array();

                  for(var23 = 0; var23 < var49; ++var23) {
                     float var24 = var51[var23];
                     var21[var23] += var24;
                     var52[var23] += var24;
                  }
               } else {
                  for(int var22 = 0; var22 < var49; ++var22) {
                     var21[var22] += var51[var22];
                  }
               }
            }

            if (!var18.mixer.process(var13, 0, var49)) {
               synchronized(this.control_mutex) {
                  this.registeredMixers.remove(var18);
                  this.cur_registeredMixers = null;
               }
            }

            for(var20 = 0; var20 < var13.length; ++var20) {
               var21 = var13[var20];
               var52 = var14[var20];

               for(var23 = 0; var23 < var49; ++var23) {
                  var52[var23] += var21[var23];
               }
            }

            if (!var19) {
               synchronized(this.control_mutex) {
                  if (this.stoppedMixers != null && this.stoppedMixers.contains(var18)) {
                     this.stoppedMixers.remove(var18);
                     var18.mixer.stop();
                  }
               }
            }
         }

         this.buffers[0] = var6;
         this.buffers[1] = var37;
         this.buffers[2] = var8;
         this.buffers[3] = var42;
         this.buffers[4] = var43;
         this.buffers[5] = var48;
      }

      int var35;
      for(var35 = 0; var35 < this.voicestatus.length; ++var35) {
         if (this.voicestatus[var35].active && this.voicestatus[var35].channelmixer == null) {
            this.voicestatus[var35].processAudioLogic(this.buffers);
         }
      }

      float[] var36;
      float[] var38;
      int var39;
      if (!this.buffers[2].isSilent()) {
         var36 = this.buffers[2].array();
         var38 = this.buffers[0].array();
         var39 = this.buffers[0].getSize();
         if (this.nrofchannels != 1) {
            float[] var44 = this.buffers[1].array();

            for(var10 = 0; var10 < var39; ++var10) {
               float var50 = var36[var10];
               var38[var10] += var50;
               var44[var10] += var50;
            }
         } else {
            for(var40 = 0; var40 < var39; ++var40) {
               var38[var40] += var36[var40];
            }
         }
      }

      if (this.synth.chorus_on) {
         this.chorus.processAudio();
      }

      if (this.synth.reverb_on) {
         this.reverb.processAudio();
      }

      if (this.nrofchannels == 1) {
         var34 = (var34 + var3) / 2.0D;
      }

      float var47;
      if (this.last_volume_left == var34 && this.last_volume_right == var3) {
         if (var34 != 1.0D || var3 != 1.0D) {
            var36 = this.buffers[0].array();
            var38 = this.buffers[1].array();
            var39 = this.buffers[0].getSize();
            var47 = (float)(var34 * var34);

            for(var10 = 0; var10 < var39; ++var10) {
               var36[var10] *= var47;
            }

            if (this.nrofchannels != 1) {
               var47 = (float)(var3 * var3);

               for(var10 = 0; var10 < var39; ++var10) {
                  var38[var10] *= var47;
               }
            }
         }
      } else {
         var36 = this.buffers[0].array();
         var38 = this.buffers[1].array();
         var39 = this.buffers[0].getSize();
         var47 = (float)(this.last_volume_left * this.last_volume_left);
         float var45 = (float)((var34 * var34 - (double)var47) / (double)var39);

         for(var11 = 0; var11 < var39; ++var11) {
            var47 += var45;
            var36[var11] *= var47;
         }

         if (this.nrofchannels != 1) {
            var47 = (float)(this.last_volume_right * this.last_volume_right);
            var45 = (float)((var3 * var3 - (double)var47) / (double)var39);

            for(var11 = 0; var11 < var39; ++var11) {
               var47 += var45;
               var38[var11] = (float)((double)var38[var11] * var3);
            }
         }

         this.last_volume_left = var34;
         this.last_volume_right = var3;
      }

      if (this.buffers[0].isSilent() && this.buffers[1].isSilent()) {
         synchronized(this.control_mutex) {
            var35 = this.midimessages.size();
         }

         if (var35 == 0) {
            ++this.pusher_silent_count;
            if (this.pusher_silent_count > 5) {
               this.pusher_silent_count = 0;
               synchronized(this.control_mutex) {
                  this.pusher_silent = true;
                  if (this.synth.weakstream != null) {
                     this.synth.weakstream.setInputStream((AudioInputStream)null);
                  }
               }
            }
         }
      } else {
         this.pusher_silent_count = 0;
      }

      if (this.synth.agc_on) {
         this.agc.processAudio();
      }

   }

   public void activity() {
      long var1 = 0L;
      if (this.pusher_silent) {
         this.pusher_silent = false;
         if (this.synth.weakstream != null) {
            this.synth.weakstream.setInputStream(this.ais);
            var1 = this.synth.weakstream.silent_samples;
         }
      }

      this.msec_last_activity = (long)((double)(this.sample_pos + var1) * (1000000.0D / (double)this.samplerate));
   }

   public void stopMixer(ModelChannelMixer var1) {
      if (this.stoppedMixers == null) {
         this.stoppedMixers = new HashSet();
      }

      this.stoppedMixers.add(var1);
   }

   public void registerMixer(ModelChannelMixer var1) {
      if (this.registeredMixers == null) {
         this.registeredMixers = new HashSet();
      }

      SoftMainMixer.SoftChannelMixerContainer var2 = new SoftMainMixer.SoftChannelMixerContainer();
      var2.buffers = new SoftAudioBuffer[6];

      for(int var3 = 0; var3 < var2.buffers.length; ++var3) {
         var2.buffers[var3] = new SoftAudioBuffer(this.buffer_len, this.synth.getFormat());
      }

      var2.mixer = var1;
      this.registeredMixers.add(var2);
      this.cur_registeredMixers = null;
   }

   public SoftMainMixer(SoftSynthesizer var1) {
      this.synth = var1;
      this.sample_pos = 0L;
      this.co_master_balance[0] = 0.5D;
      this.co_master_volume[0] = 1.0D;
      this.co_master_coarse_tuning[0] = 0.5D;
      this.co_master_fine_tuning[0] = 0.5D;
      this.msec_buffer_len = (long)(1000000.0D / (double)var1.getControlRate());
      this.samplerate = var1.getFormat().getSampleRate();
      this.nrofchannels = var1.getFormat().getChannels();
      int var2 = (int)(var1.getFormat().getSampleRate() / var1.getControlRate());
      this.buffer_len = var2;
      this.max_delay_midievent = var2;
      this.control_mutex = var1.control_mutex;
      this.buffers = new SoftAudioBuffer[14];

      for(int var3 = 0; var3 < this.buffers.length; ++var3) {
         this.buffers[var3] = new SoftAudioBuffer(var2, var1.getFormat());
      }

      this.voicestatus = var1.getVoices();
      this.reverb = new SoftReverb();
      this.chorus = new SoftChorus();
      this.agc = new SoftLimiter();
      float var6 = var1.getFormat().getSampleRate();
      float var4 = var1.getControlRate();
      this.reverb.init(var6, var4);
      this.chorus.init(var6, var4);
      this.agc.init(var6, var4);
      this.reverb.setLightMode(var1.reverb_light);
      this.reverb.setMixMode(true);
      this.chorus.setMixMode(true);
      this.agc.setMixMode(false);
      this.chorus.setInput(0, this.buffers[7]);
      this.chorus.setOutput(0, this.buffers[0]);
      if (this.nrofchannels != 1) {
         this.chorus.setOutput(1, this.buffers[1]);
      }

      this.chorus.setOutput(2, this.buffers[6]);
      this.reverb.setInput(0, this.buffers[6]);
      this.reverb.setOutput(0, this.buffers[0]);
      if (this.nrofchannels != 1) {
         this.reverb.setOutput(1, this.buffers[1]);
      }

      this.agc.setInput(0, this.buffers[0]);
      if (this.nrofchannels != 1) {
         this.agc.setInput(1, this.buffers[1]);
      }

      this.agc.setOutput(0, this.buffers[0]);
      if (this.nrofchannels != 1) {
         this.agc.setOutput(1, this.buffers[1]);
      }

      InputStream var5 = new InputStream() {
         private final SoftAudioBuffer[] buffers;
         private final int nrofchannels;
         private final int buffersize;
         private final byte[] bbuffer;
         private int bbuffer_pos;
         private final byte[] single;

         {
            this.buffers = SoftMainMixer.this.buffers;
            this.nrofchannels = SoftMainMixer.this.synth.getFormat().getChannels();
            this.buffersize = this.buffers[0].getSize();
            this.bbuffer = new byte[this.buffersize * (SoftMainMixer.this.synth.getFormat().getSampleSizeInBits() / 8) * this.nrofchannels];
            this.bbuffer_pos = 0;
            this.single = new byte[1];
         }

         public void fillBuffer() {
            SoftMainMixer.this.processAudioBuffers();

            for(int var1 = 0; var1 < this.nrofchannels; ++var1) {
               this.buffers[var1].get(this.bbuffer, var1);
            }

            this.bbuffer_pos = 0;
         }

         public int read(byte[] var1, int var2, int var3) {
            int var4 = this.bbuffer.length;
            int var5 = var2 + var3;
            int var6 = var2;
            byte[] var7 = this.bbuffer;

            while(true) {
               while(var2 < var5) {
                  if (this.available() == 0) {
                     this.fillBuffer();
                  } else {
                     int var8;
                     for(var8 = this.bbuffer_pos; var2 < var5 && var8 < var4; var1[var2++] = var7[var8++]) {
                     }

                     this.bbuffer_pos = var8;
                     if (!SoftMainMixer.this.readfully) {
                        return var2 - var6;
                     }
                  }
               }

               return var3;
            }
         }

         public int read() throws IOException {
            int var1 = this.read(this.single);
            return var1 == -1 ? -1 : this.single[0] & 255;
         }

         public int available() {
            return this.bbuffer.length - this.bbuffer_pos;
         }

         public void close() {
            SoftMainMixer.this.synth.close();
         }
      };
      this.ais = new AudioInputStream(var5, var1.getFormat(), -1L);
   }

   public AudioInputStream getInputStream() {
      return this.ais;
   }

   public void reset() {
      SoftChannel[] var1 = this.synth.channels;

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2].allSoundOff();
         var1[var2].resetAllControllers(true);
         if (this.synth.getGeneralMidiMode() == 2) {
            if (var2 == 9) {
               var1[var2].programChange(0, 15360);
            } else {
               var1[var2].programChange(0, 15488);
            }
         } else {
            var1[var2].programChange(0, 0);
         }
      }

      this.setVolume(16383);
      this.setBalance(8192);
      this.setCoarseTuning(8192);
      this.setFineTuning(8192);
      this.globalParameterControlChange(new int[]{129}, new long[]{0L}, new long[]{4L});
      this.globalParameterControlChange(new int[]{130}, new long[]{0L}, new long[]{2L});
   }

   public void setVolume(int var1) {
      synchronized(this.control_mutex) {
         this.co_master_volume[0] = (double)var1 / 16384.0D;
      }
   }

   public void setBalance(int var1) {
      synchronized(this.control_mutex) {
         this.co_master_balance[0] = (double)var1 / 16384.0D;
      }
   }

   public void setFineTuning(int var1) {
      synchronized(this.control_mutex) {
         this.co_master_fine_tuning[0] = (double)var1 / 16384.0D;
      }
   }

   public void setCoarseTuning(int var1) {
      synchronized(this.control_mutex) {
         this.co_master_coarse_tuning[0] = (double)var1 / 16384.0D;
      }
   }

   public int getVolume() {
      synchronized(this.control_mutex) {
         return (int)(this.co_master_volume[0] * 16384.0D);
      }
   }

   public int getBalance() {
      synchronized(this.control_mutex) {
         return (int)(this.co_master_balance[0] * 16384.0D);
      }
   }

   public int getFineTuning() {
      synchronized(this.control_mutex) {
         return (int)(this.co_master_fine_tuning[0] * 16384.0D);
      }
   }

   public int getCoarseTuning() {
      synchronized(this.control_mutex) {
         return (int)(this.co_master_coarse_tuning[0] * 16384.0D);
      }
   }

   public void globalParameterControlChange(int[] var1, long[] var2, long[] var3) {
      if (var1.length != 0) {
         synchronized(this.control_mutex) {
            int var5;
            if (var1[0] == 129) {
               for(var5 = 0; var5 < var3.length; ++var5) {
                  this.reverb.globalParameterControlChange(var1, var2[var5], var3[var5]);
               }
            }

            if (var1[0] == 130) {
               for(var5 = 0; var5 < var3.length; ++var5) {
                  this.chorus.globalParameterControlChange(var1, var2[var5], var3[var5]);
               }
            }

         }
      }
   }

   public void processMessage(Object var1) {
      if (var1 instanceof byte[]) {
         this.processMessage((byte[])((byte[])var1));
      }

      if (var1 instanceof MidiMessage) {
         this.processMessage((MidiMessage)var1);
      }

   }

   public void processMessage(MidiMessage var1) {
      if (var1 instanceof ShortMessage) {
         ShortMessage var2 = (ShortMessage)var1;
         this.processMessage(var2.getChannel(), var2.getCommand(), var2.getData1(), var2.getData2());
      } else {
         this.processMessage(var1.getMessage());
      }
   }

   public void processMessage(byte[] var1) {
      int var2 = 0;
      if (var1.length > 0) {
         var2 = var1[0] & 255;
      }

      if (var2 == 240) {
         this.processSystemExclusiveMessage(var1);
      } else {
         int var3 = var2 & 240;
         int var4 = var2 & 15;
         int var5;
         if (var1.length > 1) {
            var5 = var1[1] & 255;
         } else {
            var5 = 0;
         }

         int var6;
         if (var1.length > 2) {
            var6 = var1[2] & 255;
         } else {
            var6 = 0;
         }

         this.processMessage(var4, var3, var5, var6);
      }
   }

   public void processMessage(int var1, int var2, int var3, int var4) {
      synchronized(this.synth.control_mutex) {
         this.activity();
      }

      if (var2 == 240) {
         int var10 = var2 | var1;
         switch(var10) {
         case 254:
            synchronized(this.synth.control_mutex) {
               this.active_sensing_on = true;
            }
         default:
         }
      } else {
         SoftChannel[] var5 = this.synth.channels;
         if (var1 < var5.length) {
            SoftChannel var6 = var5[var1];
            switch(var2) {
            case 128:
               var6.noteOff(var3, var4);
               break;
            case 144:
               if (this.delay_midievent != 0) {
                  var6.noteOn(var3, var4, this.delay_midievent);
               } else {
                  var6.noteOn(var3, var4);
               }
               break;
            case 160:
               var6.setPolyPressure(var3, var4);
               break;
            case 176:
               var6.controlChange(var3, var4);
               break;
            case 192:
               var6.programChange(var3);
               break;
            case 208:
               var6.setChannelPressure(var3);
               break;
            case 224:
               var6.setPitchBend(var3 + var4 * 128);
            }

         }
      }
   }

   public long getMicrosecondPosition() {
      return this.pusher_silent && this.synth.weakstream != null ? (long)((double)(this.sample_pos + this.synth.weakstream.silent_samples) * (1000000.0D / (double)this.samplerate)) : (long)((double)this.sample_pos * (1000000.0D / (double)this.samplerate));
   }

   public void close() {
   }

   private class SoftChannelMixerContainer {
      ModelChannelMixer mixer;
      SoftAudioBuffer[] buffers;

      private SoftChannelMixerContainer() {
      }

      // $FF: synthetic method
      SoftChannelMixerContainer(Object var2) {
         this();
      }
   }
}
