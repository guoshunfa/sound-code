package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;

public final class SoftChannel implements MidiChannel, ModelDirectedPlayer {
   private static boolean[] dontResetControls = new boolean[128];
   private static final int RPN_NULL_VALUE = 16383;
   private int rpn_control = 16383;
   private int nrpn_control = 16383;
   double portamento_time = 1.0D;
   int[] portamento_lastnote = new int[128];
   int portamento_lastnote_ix = 0;
   private boolean portamento = false;
   private boolean mono = false;
   private boolean mute = false;
   private boolean solo = false;
   private boolean solomute = false;
   private final Object control_mutex;
   private int channel;
   private SoftVoice[] voices;
   private int bank;
   private int program;
   private SoftSynthesizer synthesizer;
   private SoftMainMixer mainmixer;
   private int[] polypressure = new int[128];
   private int channelpressure = 0;
   private int[] controller = new int[128];
   private int pitchbend;
   private double[] co_midi_pitch = new double[1];
   private double[] co_midi_channel_pressure = new double[1];
   SoftTuning tuning = new SoftTuning();
   int tuning_bank = 0;
   int tuning_program = 0;
   SoftInstrument current_instrument = null;
   ModelChannelMixer current_mixer = null;
   ModelDirector current_director = null;
   int cds_control_number = -1;
   ModelConnectionBlock[] cds_control_connections = null;
   ModelConnectionBlock[] cds_channelpressure_connections = null;
   ModelConnectionBlock[] cds_polypressure_connections = null;
   boolean sustain = false;
   boolean[][] keybasedcontroller_active = (boolean[][])null;
   double[][] keybasedcontroller_value = (double[][])null;
   private SoftControl[] co_midi = new SoftControl[128];
   private double[][] co_midi_cc_cc;
   private SoftControl co_midi_cc;
   Map<Integer, int[]> co_midi_rpn_rpn_i;
   Map<Integer, double[]> co_midi_rpn_rpn;
   private SoftControl co_midi_rpn;
   Map<Integer, int[]> co_midi_nrpn_nrpn_i;
   Map<Integer, double[]> co_midi_nrpn_nrpn;
   private SoftControl co_midi_nrpn;
   private int[] lastVelocity;
   private int prevVoiceID;
   private boolean firstVoice;
   private int voiceNo;
   private int play_noteNumber;
   private int play_velocity;
   private int play_delay;
   private boolean play_releasetriggered;

   private static int restrict7Bit(int var0) {
      if (var0 < 0) {
         return 0;
      } else {
         return var0 > 127 ? 127 : var0;
      }
   }

   private static int restrict14Bit(int var0) {
      if (var0 < 0) {
         return 0;
      } else {
         return var0 > 16256 ? 16256 : var0;
      }
   }

   public SoftChannel(SoftSynthesizer var1, int var2) {
      for(int var3 = 0; var3 < this.co_midi.length; ++var3) {
         this.co_midi[var3] = new SoftChannel.MidiControlObject();
      }

      this.co_midi_cc_cc = new double[128][1];
      this.co_midi_cc = new SoftControl() {
         double[][] cc;

         {
            this.cc = SoftChannel.this.co_midi_cc_cc;
         }

         public double[] get(int var1, String var2) {
            return var2 == null ? null : this.cc[Integer.parseInt(var2)];
         }
      };
      this.co_midi_rpn_rpn_i = new HashMap();
      this.co_midi_rpn_rpn = new HashMap();
      this.co_midi_rpn = new SoftControl() {
         Map<Integer, double[]> rpn;

         {
            this.rpn = SoftChannel.this.co_midi_rpn_rpn;
         }

         public double[] get(int var1, String var2) {
            if (var2 == null) {
               return null;
            } else {
               int var3 = Integer.parseInt(var2);
               double[] var4 = (double[])this.rpn.get(var3);
               if (var4 == null) {
                  var4 = new double[1];
                  this.rpn.put(var3, var4);
               }

               return var4;
            }
         }
      };
      this.co_midi_nrpn_nrpn_i = new HashMap();
      this.co_midi_nrpn_nrpn = new HashMap();
      this.co_midi_nrpn = new SoftControl() {
         Map<Integer, double[]> nrpn;

         {
            this.nrpn = SoftChannel.this.co_midi_nrpn_nrpn;
         }

         public double[] get(int var1, String var2) {
            if (var2 == null) {
               return null;
            } else {
               int var3 = Integer.parseInt(var2);
               double[] var4 = (double[])this.nrpn.get(var3);
               if (var4 == null) {
                  var4 = new double[1];
                  this.nrpn.put(var3, var4);
               }

               return var4;
            }
         }
      };
      this.lastVelocity = new int[128];
      this.firstVoice = true;
      this.voiceNo = 0;
      this.play_noteNumber = 0;
      this.play_velocity = 0;
      this.play_delay = 0;
      this.play_releasetriggered = false;
      this.channel = var2;
      this.voices = var1.getVoices();
      this.synthesizer = var1;
      this.mainmixer = var1.getMainMixer();
      this.control_mutex = var1.control_mutex;
      this.resetAllControllers(true);
   }

   private int findFreeVoice(int var1) {
      if (var1 == -1) {
         return -1;
      } else {
         int var2;
         for(var2 = var1; var2 < this.voices.length; ++var2) {
            if (!this.voices[var2].active) {
               return var2;
            }
         }

         var2 = this.synthesizer.getVoiceAllocationMode();
         int var3;
         if (var2 == 1) {
            var3 = this.channel;

            int var7;
            for(var7 = 0; var7 < this.voices.length; ++var7) {
               if (this.voices[var7].stealer_channel == null) {
                  if (var3 == 9) {
                     var3 = this.voices[var7].channel;
                  } else if (this.voices[var7].channel != 9 && this.voices[var7].channel > var3) {
                     var3 = this.voices[var7].channel;
                  }
               }
            }

            var7 = -1;
            SoftVoice var8 = null;

            int var6;
            for(var6 = 0; var6 < this.voices.length; ++var6) {
               if (this.voices[var6].channel == var3 && this.voices[var6].stealer_channel == null && !this.voices[var6].on) {
                  if (var8 == null) {
                     var8 = this.voices[var6];
                     var7 = var6;
                  }

                  if (this.voices[var6].voiceID < var8.voiceID) {
                     var8 = this.voices[var6];
                     var7 = var6;
                  }
               }
            }

            if (var7 == -1) {
               for(var6 = 0; var6 < this.voices.length; ++var6) {
                  if (this.voices[var6].channel == var3 && this.voices[var6].stealer_channel == null) {
                     if (var8 == null) {
                        var8 = this.voices[var6];
                        var7 = var6;
                     }

                     if (this.voices[var6].voiceID < var8.voiceID) {
                        var8 = this.voices[var6];
                        var7 = var6;
                     }
                  }
               }
            }

            return var7;
         } else {
            var3 = -1;
            SoftVoice var4 = null;

            int var5;
            for(var5 = 0; var5 < this.voices.length; ++var5) {
               if (this.voices[var5].stealer_channel == null && !this.voices[var5].on) {
                  if (var4 == null) {
                     var4 = this.voices[var5];
                     var3 = var5;
                  }

                  if (this.voices[var5].voiceID < var4.voiceID) {
                     var4 = this.voices[var5];
                     var3 = var5;
                  }
               }
            }

            if (var3 == -1) {
               for(var5 = 0; var5 < this.voices.length; ++var5) {
                  if (this.voices[var5].stealer_channel == null) {
                     if (var4 == null) {
                        var4 = this.voices[var5];
                        var3 = var5;
                     }

                     if (this.voices[var5].voiceID < var4.voiceID) {
                        var4 = this.voices[var5];
                        var3 = var5;
                     }
                  }
               }
            }

            return var3;
         }
      }
   }

   void initVoice(SoftVoice var1, SoftPerformer var2, int var3, int var4, int var5, int var6, ModelConnectionBlock[] var7, ModelChannelMixer var8, boolean var9) {
      if (var1.active) {
         var1.stealer_channel = this;
         var1.stealer_performer = var2;
         var1.stealer_voiceID = var3;
         var1.stealer_noteNumber = var4;
         var1.stealer_velocity = var5;
         var1.stealer_extendedConnectionBlocks = var7;
         var1.stealer_channelmixer = var8;
         var1.stealer_releaseTriggered = var9;

         for(int var10 = 0; var10 < this.voices.length; ++var10) {
            if (this.voices[var10].active && this.voices[var10].voiceID == var1.voiceID) {
               this.voices[var10].soundOff();
            }
         }

      } else {
         var1.extendedConnectionBlocks = var7;
         var1.channelmixer = var8;
         var1.releaseTriggered = var9;
         var1.voiceID = var3;
         var1.tuning = this.tuning;
         var1.exclusiveClass = var2.exclusiveClass;
         var1.softchannel = this;
         var1.channel = this.channel;
         var1.bank = this.bank;
         var1.program = this.program;
         var1.instrument = this.current_instrument;
         var1.performer = var2;
         var1.objects.clear();
         var1.objects.put("midi", this.co_midi[var4]);
         var1.objects.put("midi_cc", this.co_midi_cc);
         var1.objects.put("midi_rpn", this.co_midi_rpn);
         var1.objects.put("midi_nrpn", this.co_midi_nrpn);
         var1.noteOn(var4, var5, var6);
         var1.setMute(this.mute);
         var1.setSoloMute(this.solomute);
         if (!var9) {
            if (this.controller[84] != 0) {
               var1.co_noteon_keynumber[0] = this.tuning.getTuning(this.controller[84]) / 100.0D * 0.0078125D;
               var1.portamento = true;
               this.controlChange(84, 0);
            } else if (this.portamento) {
               if (this.mono) {
                  if (this.portamento_lastnote[0] != -1) {
                     var1.co_noteon_keynumber[0] = this.tuning.getTuning(this.portamento_lastnote[0]) / 100.0D * 0.0078125D;
                     var1.portamento = true;
                     this.controlChange(84, 0);
                  }

                  this.portamento_lastnote[0] = var4;
               } else if (this.portamento_lastnote_ix != 0) {
                  --this.portamento_lastnote_ix;
                  var1.co_noteon_keynumber[0] = this.tuning.getTuning(this.portamento_lastnote[this.portamento_lastnote_ix]) / 100.0D * 0.0078125D;
                  var1.portamento = true;
               }
            }

         }
      }
   }

   public void noteOn(int var1, int var2) {
      this.noteOn(var1, var2, 0);
   }

   void noteOn(int var1, int var2, int var3) {
      var1 = restrict7Bit(var1);
      var2 = restrict7Bit(var2);
      this.noteOn_internal(var1, var2, var3);
      if (this.current_mixer != null) {
         this.current_mixer.noteOn(var1, var2);
      }

   }

   private void noteOn_internal(int var1, int var2, int var3) {
      if (var2 == 0) {
         this.noteOff_internal(var1, 64);
      } else {
         synchronized(this.control_mutex) {
            int var5;
            if (this.sustain) {
               this.sustain = false;

               for(var5 = 0; var5 < this.voices.length; ++var5) {
                  if ((this.voices[var5].sustain || this.voices[var5].on) && this.voices[var5].channel == this.channel && this.voices[var5].active && this.voices[var5].note == var1) {
                     this.voices[var5].sustain = false;
                     this.voices[var5].on = true;
                     this.voices[var5].noteOff(0);
                  }
               }

               this.sustain = true;
            }

            this.mainmixer.activity();
            if (this.mono) {
               int var6;
               boolean var9;
               if (this.portamento) {
                  var9 = false;

                  for(var6 = 0; var6 < this.voices.length; ++var6) {
                     if (this.voices[var6].on && this.voices[var6].channel == this.channel && this.voices[var6].active && !this.voices[var6].releaseTriggered) {
                        this.voices[var6].portamento = true;
                        this.voices[var6].setNote(var1);
                        var9 = true;
                     }
                  }

                  if (var9) {
                     this.portamento_lastnote[0] = var1;
                     return;
                  }
               }

               if (this.controller[84] != 0) {
                  var9 = false;

                  for(var6 = 0; var6 < this.voices.length; ++var6) {
                     if (this.voices[var6].on && this.voices[var6].channel == this.channel && this.voices[var6].active && this.voices[var6].note == this.controller[84] && !this.voices[var6].releaseTriggered) {
                        this.voices[var6].portamento = true;
                        this.voices[var6].setNote(var1);
                        var9 = true;
                     }
                  }

                  this.controlChange(84, 0);
                  if (var9) {
                     return;
                  }
               }
            }

            if (this.mono) {
               this.allNotesOff();
            }

            if (this.current_instrument == null) {
               this.current_instrument = this.synthesizer.findInstrument(this.program, this.bank, this.channel);
               if (this.current_instrument == null) {
                  return;
               }

               if (this.current_mixer != null) {
                  this.mainmixer.stopMixer(this.current_mixer);
               }

               this.current_mixer = this.current_instrument.getSourceInstrument().getChannelMixer(this, this.synthesizer.getFormat());
               if (this.current_mixer != null) {
                  this.mainmixer.registerMixer(this.current_mixer);
               }

               this.current_director = this.current_instrument.getDirector(this, this);
               this.applyInstrumentCustomization();
            }

            this.prevVoiceID = this.synthesizer.voiceIDCounter++;
            this.firstVoice = true;
            this.voiceNo = 0;
            var5 = (int)Math.round(this.tuning.getTuning(var1) / 100.0D);
            this.play_noteNumber = var1;
            this.play_velocity = var2;
            this.play_delay = var3;
            this.play_releasetriggered = false;
            this.lastVelocity[var1] = var2;
            this.current_director.noteOn(var5, var2);
         }
      }
   }

   public void noteOff(int var1, int var2) {
      var1 = restrict7Bit(var1);
      var2 = restrict7Bit(var2);
      this.noteOff_internal(var1, var2);
      if (this.current_mixer != null) {
         this.current_mixer.noteOff(var1, var2);
      }

   }

   private void noteOff_internal(int var1, int var2) {
      synchronized(this.control_mutex) {
         if (!this.mono && this.portamento && this.portamento_lastnote_ix != 127) {
            this.portamento_lastnote[this.portamento_lastnote_ix] = var1;
            ++this.portamento_lastnote_ix;
         }

         this.mainmixer.activity();

         int var4;
         for(var4 = 0; var4 < this.voices.length; ++var4) {
            if (this.voices[var4].on && this.voices[var4].channel == this.channel && this.voices[var4].note == var1 && !this.voices[var4].releaseTriggered) {
               this.voices[var4].noteOff(var2);
            }

            if (this.voices[var4].stealer_channel == this && this.voices[var4].stealer_noteNumber == var1) {
               SoftVoice var5 = this.voices[var4];
               var5.stealer_releaseTriggered = false;
               var5.stealer_channel = null;
               var5.stealer_performer = null;
               var5.stealer_voiceID = -1;
               var5.stealer_noteNumber = 0;
               var5.stealer_velocity = 0;
               var5.stealer_extendedConnectionBlocks = null;
               var5.stealer_channelmixer = null;
            }
         }

         if (this.current_instrument == null) {
            this.current_instrument = this.synthesizer.findInstrument(this.program, this.bank, this.channel);
            if (this.current_instrument == null) {
               return;
            }

            if (this.current_mixer != null) {
               this.mainmixer.stopMixer(this.current_mixer);
            }

            this.current_mixer = this.current_instrument.getSourceInstrument().getChannelMixer(this, this.synthesizer.getFormat());
            if (this.current_mixer != null) {
               this.mainmixer.registerMixer(this.current_mixer);
            }

            this.current_director = this.current_instrument.getDirector(this, this);
            this.applyInstrumentCustomization();
         }

         this.prevVoiceID = this.synthesizer.voiceIDCounter++;
         this.firstVoice = true;
         this.voiceNo = 0;
         var4 = (int)Math.round(this.tuning.getTuning(var1) / 100.0D);
         this.play_noteNumber = var1;
         this.play_velocity = this.lastVelocity[var1];
         this.play_releasetriggered = true;
         this.play_delay = 0;
         this.current_director.noteOff(var4, var2);
      }
   }

   public void play(int var1, ModelConnectionBlock[] var2) {
      int var3 = this.play_noteNumber;
      int var4 = this.play_velocity;
      int var5 = this.play_delay;
      boolean var6 = this.play_releasetriggered;
      SoftPerformer var7 = this.current_instrument.getPerformer(var1);
      if (this.firstVoice) {
         this.firstVoice = false;
         if (var7.exclusiveClass != 0) {
            int var8 = var7.exclusiveClass;

            for(int var9 = 0; var9 < this.voices.length; ++var9) {
               if (this.voices[var9].active && this.voices[var9].channel == this.channel && this.voices[var9].exclusiveClass == var8 && (!var7.selfNonExclusive || this.voices[var9].note != var3)) {
                  this.voices[var9].shutdown();
               }
            }
         }
      }

      this.voiceNo = this.findFreeVoice(this.voiceNo);
      if (this.voiceNo != -1) {
         this.initVoice(this.voices[this.voiceNo], var7, this.prevVoiceID, var3, var4, var5, var2, this.current_mixer, var6);
      }
   }

   public void noteOff(int var1) {
      if (var1 >= 0 && var1 <= 127) {
         this.noteOff_internal(var1, 64);
      }
   }

   public void setPolyPressure(int var1, int var2) {
      var1 = restrict7Bit(var1);
      var2 = restrict7Bit(var2);
      if (this.current_mixer != null) {
         this.current_mixer.setPolyPressure(var1, var2);
      }

      synchronized(this.control_mutex) {
         this.mainmixer.activity();
         this.co_midi[var1].get(0, "poly_pressure")[0] = (double)var2 * 0.0078125D;
         this.polypressure[var1] = var2;

         for(int var4 = 0; var4 < this.voices.length; ++var4) {
            if (this.voices[var4].active && this.voices[var4].note == var1) {
               this.voices[var4].setPolyPressure(var2);
            }
         }

      }
   }

   public int getPolyPressure(int var1) {
      synchronized(this.control_mutex) {
         return this.polypressure[var1];
      }
   }

   public void setChannelPressure(int var1) {
      var1 = restrict7Bit(var1);
      if (this.current_mixer != null) {
         this.current_mixer.setChannelPressure(var1);
      }

      synchronized(this.control_mutex) {
         this.mainmixer.activity();
         this.co_midi_channel_pressure[0] = (double)var1 * 0.0078125D;
         this.channelpressure = var1;

         for(int var3 = 0; var3 < this.voices.length; ++var3) {
            if (this.voices[var3].active) {
               this.voices[var3].setChannelPressure(var1);
            }
         }

      }
   }

   public int getChannelPressure() {
      synchronized(this.control_mutex) {
         return this.channelpressure;
      }
   }

   void applyInstrumentCustomization() {
      if (this.cds_control_connections != null || this.cds_channelpressure_connections != null || this.cds_polypressure_connections != null) {
         ModelInstrument var1 = this.current_instrument.getSourceInstrument();
         ModelPerformer[] var2 = var1.getPerformers();
         ModelPerformer[] var3 = new ModelPerformer[var2.length];

         for(int var4 = 0; var4 < var3.length; ++var4) {
            ModelPerformer var5 = var2[var4];
            ModelPerformer var6 = new ModelPerformer();
            var6.setName(var5.getName());
            var6.setExclusiveClass(var5.getExclusiveClass());
            var6.setKeyFrom(var5.getKeyFrom());
            var6.setKeyTo(var5.getKeyTo());
            var6.setVelFrom(var5.getVelFrom());
            var6.setVelTo(var5.getVelTo());
            var6.getOscillators().addAll(var5.getOscillators());
            var6.getConnectionBlocks().addAll(var5.getConnectionBlocks());
            var3[var4] = var6;
            List var7 = var6.getConnectionBlocks();
            if (this.cds_control_connections != null) {
               String var8 = Integer.toString(this.cds_control_number);
               Iterator var9 = var7.iterator();

               label95:
               while(true) {
                  if (!var9.hasNext()) {
                     int var18 = 0;

                     while(true) {
                        if (var18 >= this.cds_control_connections.length) {
                           break label95;
                        }

                        var7.add(this.cds_control_connections[var18]);
                        ++var18;
                     }
                  }

                  ModelConnectionBlock var10 = (ModelConnectionBlock)var9.next();
                  ModelSource[] var11 = var10.getSources();
                  boolean var12 = false;
                  if (var11 != null) {
                     for(int var13 = 0; var13 < var11.length; ++var13) {
                        ModelSource var14 = var11[var13];
                        if ("midi_cc".equals(var14.getIdentifier().getObject()) && var8.equals(var14.getIdentifier().getVariable())) {
                           var12 = true;
                        }
                     }
                  }

                  if (var12) {
                     var9.remove();
                  }
               }
            }

            Iterator var15;
            ModelConnectionBlock var16;
            int var17;
            ModelSource[] var19;
            boolean var20;
            int var21;
            if (this.cds_polypressure_connections != null) {
               var15 = var7.iterator();

               label115:
               while(true) {
                  if (!var15.hasNext()) {
                     var17 = 0;

                     while(true) {
                        if (var17 >= this.cds_polypressure_connections.length) {
                           break label115;
                        }

                        var7.add(this.cds_polypressure_connections[var17]);
                        ++var17;
                     }
                  }

                  var16 = (ModelConnectionBlock)var15.next();
                  var19 = var16.getSources();
                  var20 = false;
                  if (var19 != null) {
                     for(var21 = 0; var21 < var19.length; ++var21) {
                        ModelSource var22 = var19[var21];
                        if ("midi".equals(var22.getIdentifier().getObject()) && "poly_pressure".equals(var22.getIdentifier().getVariable())) {
                           var20 = true;
                        }
                     }
                  }

                  if (var20) {
                     var15.remove();
                  }
               }
            }

            if (this.cds_channelpressure_connections != null) {
               var15 = var7.iterator();

               while(var15.hasNext()) {
                  var16 = (ModelConnectionBlock)var15.next();
                  var19 = var16.getSources();
                  var20 = false;
                  if (var19 != null) {
                     for(var21 = 0; var21 < var19.length; ++var21) {
                        ModelIdentifier var23 = var19[var21].getIdentifier();
                        if ("midi".equals(var23.getObject()) && "channel_pressure".equals(var23.getVariable())) {
                           var20 = true;
                        }
                     }
                  }

                  if (var20) {
                     var15.remove();
                  }
               }

               for(var17 = 0; var17 < this.cds_channelpressure_connections.length; ++var17) {
                  var7.add(this.cds_channelpressure_connections[var17]);
               }
            }
         }

         this.current_instrument = new SoftInstrument(var1, var3);
      }
   }

   private ModelConnectionBlock[] createModelConnections(ModelIdentifier var1, int[] var2, int[] var3) {
      ArrayList var4 = new ArrayList();

      for(int var5 = 0; var5 < var2.length; ++var5) {
         int var6 = var2[var5];
         int var7 = var3[var5];
         final double var8;
         ModelConnectionBlock var10;
         if (var6 == 0) {
            var8 = (double)((var7 - 64) * 100);
            var10 = new ModelConnectionBlock(new ModelSource(var1, false, false, 0), var8, new ModelDestination(new ModelIdentifier("osc", "pitch")));
            var4.add(var10);
         }

         if (var6 == 1) {
            var8 = ((double)var7 / 64.0D - 1.0D) * 9600.0D;
            if (var8 > 0.0D) {
               var10 = new ModelConnectionBlock(new ModelSource(var1, true, false, 0), -var8, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
            } else {
               var10 = new ModelConnectionBlock(new ModelSource(var1, false, false, 0), var8, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
            }

            var4.add(var10);
         }

         ModelConnectionBlock var11;
         ModelTransform var12;
         if (var6 == 2) {
            var8 = (double)var7 / 64.0D;
            var12 = new ModelTransform() {
               double s = var8;

               public double transform(double var1) {
                  if (this.s < 1.0D) {
                     var1 = this.s + var1 * (1.0D - this.s);
                  } else {
                     if (this.s <= 1.0D) {
                        return 0.0D;
                     }

                     var1 = 1.0D + var1 * (this.s - 1.0D);
                  }

                  return -(0.4166666666666667D / Math.log(10.0D)) * Math.log(var1);
               }
            };
            var11 = new ModelConnectionBlock(new ModelSource(var1, var12), -960.0D, new ModelDestination(ModelDestination.DESTINATION_GAIN));
            var4.add(var11);
         }

         if (var6 == 3) {
            var8 = ((double)var7 / 64.0D - 1.0D) * 9600.0D;
            var10 = new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true, 0), new ModelSource(var1, false, false, 0), var8, new ModelDestination(ModelDestination.DESTINATION_PITCH));
            var4.add(var10);
         }

         if (var6 == 4) {
            var8 = (double)var7 / 128.0D * 2400.0D;
            var10 = new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true, 0), new ModelSource(var1, false, false, 0), var8, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
            var4.add(var10);
         }

         if (var6 == 5) {
            var8 = (double)var7 / 127.0D;
            var12 = new ModelTransform() {
               double s = var8;

               public double transform(double var1) {
                  return -(0.4166666666666667D / Math.log(10.0D)) * Math.log(1.0D - var1 * this.s);
               }
            };
            var11 = new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, false, 0), new ModelSource(var1, var12), -960.0D, new ModelDestination(ModelDestination.DESTINATION_GAIN));
            var4.add(var11);
         }
      }

      return (ModelConnectionBlock[])var4.toArray(new ModelConnectionBlock[var4.size()]);
   }

   public void mapPolyPressureToDestination(int[] var1, int[] var2) {
      this.current_instrument = null;
      if (var1.length == 0) {
         this.cds_polypressure_connections = null;
      } else {
         this.cds_polypressure_connections = this.createModelConnections(new ModelIdentifier("midi", "poly_pressure"), var1, var2);
      }
   }

   public void mapChannelPressureToDestination(int[] var1, int[] var2) {
      this.current_instrument = null;
      if (var1.length == 0) {
         this.cds_channelpressure_connections = null;
      } else {
         this.cds_channelpressure_connections = this.createModelConnections(new ModelIdentifier("midi", "channel_pressure"), var1, var2);
      }
   }

   public void mapControlToDestination(int var1, int[] var2, int[] var3) {
      if (var1 >= 1 && var1 <= 31 || var1 >= 64 && var1 <= 95) {
         this.current_instrument = null;
         this.cds_control_number = var1;
         if (var2.length == 0) {
            this.cds_control_connections = null;
         } else {
            this.cds_control_connections = this.createModelConnections(new ModelIdentifier("midi_cc", Integer.toString(var1)), var2, var3);
         }
      } else {
         this.cds_control_connections = null;
      }
   }

   public void controlChangePerNote(int var1, int var2, int var3) {
      if (this.keybasedcontroller_active == null) {
         this.keybasedcontroller_active = new boolean[128][];
         this.keybasedcontroller_value = new double[128][];
      }

      if (this.keybasedcontroller_active[var1] == null) {
         this.keybasedcontroller_active[var1] = new boolean[128];
         Arrays.fill(this.keybasedcontroller_active[var1], false);
         this.keybasedcontroller_value[var1] = new double[128];
         Arrays.fill(this.keybasedcontroller_value[var1], 0.0D);
      }

      if (var3 == -1) {
         this.keybasedcontroller_active[var1][var2] = false;
      } else {
         this.keybasedcontroller_active[var1][var2] = true;
         this.keybasedcontroller_value[var1][var2] = (double)var3 / 128.0D;
      }

      int var4;
      if (var2 < 120) {
         for(var4 = 0; var4 < this.voices.length; ++var4) {
            if (this.voices[var4].active) {
               this.voices[var4].controlChange(var2, -1);
            }
         }
      } else if (var2 == 120) {
         for(var4 = 0; var4 < this.voices.length; ++var4) {
            if (this.voices[var4].active) {
               this.voices[var4].rpnChange(1, -1);
            }
         }
      } else if (var2 == 121) {
         for(var4 = 0; var4 < this.voices.length; ++var4) {
            if (this.voices[var4].active) {
               this.voices[var4].rpnChange(2, -1);
            }
         }
      }

   }

   public int getControlPerNote(int var1, int var2) {
      if (this.keybasedcontroller_active == null) {
         return -1;
      } else if (this.keybasedcontroller_active[var1] == null) {
         return -1;
      } else {
         return !this.keybasedcontroller_active[var1][var2] ? -1 : (int)(this.keybasedcontroller_value[var1][var2] * 128.0D);
      }
   }

   public void controlChange(int var1, int var2) {
      var1 = restrict7Bit(var1);
      var2 = restrict7Bit(var2);
      if (this.current_mixer != null) {
         this.current_mixer.controlChange(var1, var2);
      }

      synchronized(this.control_mutex) {
         boolean var7;
         int var8;
         switch(var1) {
         case 5:
            double var4 = -Math.asin((double)var2 / 128.0D * 2.0D - 1.0D) / 3.141592653589793D + 0.5D;
            var4 = Math.pow(100000.0D, var4) / 100.0D;
            var4 /= 100.0D;
            var4 *= 1000.0D;
            var4 /= (double)this.synthesizer.getControlRate();
            this.portamento_time = var4;
            break;
         case 6:
         case 38:
         case 96:
         case 97:
            int var6 = 0;
            int[] var12;
            if (this.nrpn_control != 16383) {
               var12 = (int[])this.co_midi_nrpn_nrpn_i.get(this.nrpn_control);
               if (var12 != null) {
                  var6 = var12[0];
               }
            }

            if (this.rpn_control != 16383) {
               var12 = (int[])this.co_midi_rpn_rpn_i.get(this.rpn_control);
               if (var12 != null) {
                  var6 = var12[0];
               }
            }

            if (var1 == 6) {
               var6 = (var6 & 127) + (var2 << 7);
            } else if (var1 == 38) {
               var6 = (var6 & 16256) + var2;
            } else if (var1 == 96 || var1 == 97) {
               short var13 = 1;
               if (this.rpn_control == 2 || this.rpn_control == 3 || this.rpn_control == 4) {
                  var13 = 128;
               }

               if (var1 == 96) {
                  var6 += var13;
               }

               if (var1 == 97) {
                  var6 -= var13;
               }
            }

            if (this.nrpn_control != 16383) {
               this.nrpnChange(this.nrpn_control, var6);
            }

            if (this.rpn_control != 16383) {
               this.rpnChange(this.rpn_control, var6);
            }
            break;
         case 64:
            var7 = var2 >= 64;
            if (this.sustain != var7) {
               this.sustain = var7;
               if (!var7) {
                  for(var8 = 0; var8 < this.voices.length; ++var8) {
                     if (this.voices[var8].active && this.voices[var8].sustain && this.voices[var8].channel == this.channel) {
                        this.voices[var8].sustain = false;
                        if (!this.voices[var8].on) {
                           this.voices[var8].on = true;
                           this.voices[var8].noteOff(0);
                        }
                     }
                  }
               } else {
                  for(var8 = 0; var8 < this.voices.length; ++var8) {
                     if (this.voices[var8].active && this.voices[var8].channel == this.channel) {
                        this.voices[var8].redamp();
                     }
                  }
               }
            }
            break;
         case 65:
            this.portamento = var2 >= 64;
            this.portamento_lastnote[0] = -1;
            this.portamento_lastnote_ix = 0;
            break;
         case 66:
            var7 = var2 >= 64;
            if (var7) {
               for(var8 = 0; var8 < this.voices.length; ++var8) {
                  if (this.voices[var8].active && this.voices[var8].on && this.voices[var8].channel == this.channel) {
                     this.voices[var8].sostenuto = true;
                  }
               }
            }

            if (!var7) {
               for(var8 = 0; var8 < this.voices.length; ++var8) {
                  if (this.voices[var8].active && this.voices[var8].sostenuto && this.voices[var8].channel == this.channel) {
                     this.voices[var8].sostenuto = false;
                     if (!this.voices[var8].on) {
                        this.voices[var8].on = true;
                        this.voices[var8].noteOff(0);
                     }
                  }
               }
            }
            break;
         case 98:
            this.nrpn_control = (this.nrpn_control & 16256) + var2;
            this.rpn_control = 16383;
            break;
         case 99:
            this.nrpn_control = (this.nrpn_control & 127) + (var2 << 7);
            this.rpn_control = 16383;
            break;
         case 100:
            this.rpn_control = (this.rpn_control & 16256) + var2;
            this.nrpn_control = 16383;
            break;
         case 101:
            this.rpn_control = (this.rpn_control & 127) + (var2 << 7);
            this.nrpn_control = 16383;
            break;
         case 120:
            this.allSoundOff();
            break;
         case 121:
            this.resetAllControllers(var2 == 127);
            break;
         case 122:
            this.localControl(var2 >= 64);
            break;
         case 123:
            this.allNotesOff();
            break;
         case 124:
            this.setOmni(false);
            break;
         case 125:
            this.setOmni(true);
            break;
         case 126:
            if (var2 == 1) {
               this.setMono(true);
            }
            break;
         case 127:
            this.setMono(false);
         }

         this.co_midi_cc_cc[var1][0] = (double)var2 * 0.0078125D;
         if (var1 == 0) {
            this.bank = var2 << 7;
         } else if (var1 == 32) {
            this.bank = (this.bank & 16256) + var2;
         } else {
            this.controller[var1] = var2;
            if (var1 < 32) {
               this.controller[var1 + 32] = 0;
            }

            for(int var11 = 0; var11 < this.voices.length; ++var11) {
               if (this.voices[var11].active) {
                  this.voices[var11].controlChange(var1, var2);
               }
            }

         }
      }
   }

   public int getController(int var1) {
      synchronized(this.control_mutex) {
         return this.controller[var1] & 127;
      }
   }

   public void tuningChange(int var1) {
      this.tuningChange(0, var1);
   }

   public void tuningChange(int var1, int var2) {
      synchronized(this.control_mutex) {
         this.tuning = this.synthesizer.getTuning(new Patch(var1, var2));
      }
   }

   public void programChange(int var1) {
      this.programChange(this.bank, var1);
   }

   public void programChange(int var1, int var2) {
      var1 = restrict14Bit(var1);
      var2 = restrict7Bit(var2);
      synchronized(this.control_mutex) {
         this.mainmixer.activity();
         if (this.bank != var1 || this.program != var2) {
            this.bank = var1;
            this.program = var2;
            this.current_instrument = null;
         }

      }
   }

   public int getProgram() {
      synchronized(this.control_mutex) {
         return this.program;
      }
   }

   public void setPitchBend(int var1) {
      var1 = restrict14Bit(var1);
      if (this.current_mixer != null) {
         this.current_mixer.setPitchBend(var1);
      }

      synchronized(this.control_mutex) {
         this.mainmixer.activity();
         this.co_midi_pitch[0] = (double)var1 * 6.103515625E-5D;
         this.pitchbend = var1;

         for(int var3 = 0; var3 < this.voices.length; ++var3) {
            if (this.voices[var3].active) {
               this.voices[var3].setPitchBend(var1);
            }
         }

      }
   }

   public int getPitchBend() {
      synchronized(this.control_mutex) {
         return this.pitchbend;
      }
   }

   public void nrpnChange(int var1, int var2) {
      if (this.synthesizer.getGeneralMidiMode() == 0) {
         if (var1 == 136) {
            this.controlChange(76, var2 >> 7);
         }

         if (var1 == 137) {
            this.controlChange(77, var2 >> 7);
         }

         if (var1 == 138) {
            this.controlChange(78, var2 >> 7);
         }

         if (var1 == 160) {
            this.controlChange(74, var2 >> 7);
         }

         if (var1 == 161) {
            this.controlChange(71, var2 >> 7);
         }

         if (var1 == 227) {
            this.controlChange(73, var2 >> 7);
         }

         if (var1 == 228) {
            this.controlChange(75, var2 >> 7);
         }

         if (var1 == 230) {
            this.controlChange(72, var2 >> 7);
         }

         if (var1 >> 7 == 24) {
            this.controlChangePerNote(var1 % 128, 120, var2 >> 7);
         }

         if (var1 >> 7 == 26) {
            this.controlChangePerNote(var1 % 128, 7, var2 >> 7);
         }

         if (var1 >> 7 == 28) {
            this.controlChangePerNote(var1 % 128, 10, var2 >> 7);
         }

         if (var1 >> 7 == 29) {
            this.controlChangePerNote(var1 % 128, 91, var2 >> 7);
         }

         if (var1 >> 7 == 30) {
            this.controlChangePerNote(var1 % 128, 93, var2 >> 7);
         }
      }

      int[] var3 = (int[])this.co_midi_nrpn_nrpn_i.get(var1);
      double[] var4 = (double[])this.co_midi_nrpn_nrpn.get(var1);
      if (var3 == null) {
         var3 = new int[1];
         this.co_midi_nrpn_nrpn_i.put(var1, var3);
      }

      if (var4 == null) {
         var4 = new double[1];
         this.co_midi_nrpn_nrpn.put(var1, var4);
      }

      var3[0] = var2;
      var4[0] = (double)var3[0] * 6.103515625E-5D;

      for(int var5 = 0; var5 < this.voices.length; ++var5) {
         if (this.voices[var5].active) {
            this.voices[var5].nrpnChange(var1, var3[0]);
         }
      }

   }

   public void rpnChange(int var1, int var2) {
      if (var1 == 3) {
         this.tuning_program = var2 >> 7 & 127;
         this.tuningChange(this.tuning_bank, this.tuning_program);
      }

      if (var1 == 4) {
         this.tuning_bank = var2 >> 7 & 127;
      }

      int[] var3 = (int[])this.co_midi_rpn_rpn_i.get(var1);
      double[] var4 = (double[])this.co_midi_rpn_rpn.get(var1);
      if (var3 == null) {
         var3 = new int[1];
         this.co_midi_rpn_rpn_i.put(var1, var3);
      }

      if (var4 == null) {
         var4 = new double[1];
         this.co_midi_rpn_rpn.put(var1, var4);
      }

      var3[0] = var2;
      var4[0] = (double)var3[0] * 6.103515625E-5D;

      for(int var5 = 0; var5 < this.voices.length; ++var5) {
         if (this.voices[var5].active) {
            this.voices[var5].rpnChange(var1, var3[0]);
         }
      }

   }

   public void resetAllControllers() {
      this.resetAllControllers(false);
   }

   public void resetAllControllers(boolean var1) {
      synchronized(this.control_mutex) {
         this.mainmixer.activity();

         int var3;
         for(var3 = 0; var3 < 128; ++var3) {
            this.setPolyPressure(var3, 0);
         }

         this.setChannelPressure(0);
         this.setPitchBend(8192);

         for(var3 = 0; var3 < 128; ++var3) {
            if (!dontResetControls[var3]) {
               this.controlChange(var3, 0);
            }
         }

         this.controlChange(71, 64);
         this.controlChange(72, 64);
         this.controlChange(73, 64);
         this.controlChange(74, 64);
         this.controlChange(75, 64);
         this.controlChange(76, 64);
         this.controlChange(77, 64);
         this.controlChange(78, 64);
         this.controlChange(8, 64);
         this.controlChange(11, 127);
         this.controlChange(98, 127);
         this.controlChange(99, 127);
         this.controlChange(100, 127);
         this.controlChange(101, 127);
         if (var1) {
            this.keybasedcontroller_active = (boolean[][])null;
            this.keybasedcontroller_value = (double[][])null;
            this.controlChange(7, 100);
            this.controlChange(10, 64);
            this.controlChange(91, 40);
            Iterator var7 = this.co_midi_rpn_rpn.keySet().iterator();

            int var4;
            while(var7.hasNext()) {
               var4 = (Integer)var7.next();
               if (var4 != 3 && var4 != 4) {
                  this.rpnChange(var4, 0);
               }
            }

            var7 = this.co_midi_nrpn_nrpn.keySet().iterator();

            while(var7.hasNext()) {
               var4 = (Integer)var7.next();
               this.nrpnChange(var4, 0);
            }

            this.rpnChange(0, 256);
            this.rpnChange(1, 8192);
            this.rpnChange(2, 8192);
            this.rpnChange(5, 64);
            this.tuning_bank = 0;
            this.tuning_program = 0;
            this.tuning = new SoftTuning();
         }

      }
   }

   public void allNotesOff() {
      if (this.current_mixer != null) {
         this.current_mixer.allNotesOff();
      }

      synchronized(this.control_mutex) {
         for(int var2 = 0; var2 < this.voices.length; ++var2) {
            if (this.voices[var2].on && this.voices[var2].channel == this.channel && !this.voices[var2].releaseTriggered) {
               this.voices[var2].noteOff(0);
            }
         }

      }
   }

   public void allSoundOff() {
      if (this.current_mixer != null) {
         this.current_mixer.allSoundOff();
      }

      synchronized(this.control_mutex) {
         for(int var2 = 0; var2 < this.voices.length; ++var2) {
            if (this.voices[var2].on && this.voices[var2].channel == this.channel) {
               this.voices[var2].soundOff();
            }
         }

      }
   }

   public boolean localControl(boolean var1) {
      return false;
   }

   public void setMono(boolean var1) {
      if (this.current_mixer != null) {
         this.current_mixer.setMono(var1);
      }

      synchronized(this.control_mutex) {
         this.allNotesOff();
         this.mono = var1;
      }
   }

   public boolean getMono() {
      synchronized(this.control_mutex) {
         return this.mono;
      }
   }

   public void setOmni(boolean var1) {
      if (this.current_mixer != null) {
         this.current_mixer.setOmni(var1);
      }

      this.allNotesOff();
   }

   public boolean getOmni() {
      return false;
   }

   public void setMute(boolean var1) {
      if (this.current_mixer != null) {
         this.current_mixer.setMute(var1);
      }

      synchronized(this.control_mutex) {
         this.mute = var1;

         for(int var3 = 0; var3 < this.voices.length; ++var3) {
            if (this.voices[var3].active && this.voices[var3].channel == this.channel) {
               this.voices[var3].setMute(var1);
            }
         }

      }
   }

   public boolean getMute() {
      synchronized(this.control_mutex) {
         return this.mute;
      }
   }

   public void setSolo(boolean var1) {
      if (this.current_mixer != null) {
         this.current_mixer.setSolo(var1);
      }

      synchronized(this.control_mutex) {
         this.solo = var1;
         boolean var3 = false;
         SoftChannel[] var4 = this.synthesizer.channels;
         int var5 = var4.length;

         int var6;
         SoftChannel var7;
         for(var6 = 0; var6 < var5; ++var6) {
            var7 = var4[var6];
            if (var7.solo) {
               var3 = true;
               break;
            }
         }

         if (var3) {
            var4 = this.synthesizer.channels;
            var5 = var4.length;

            for(var6 = 0; var6 < var5; ++var6) {
               var7 = var4[var6];
               var7.setSoloMute(!var7.solo);
            }

         } else {
            var4 = this.synthesizer.channels;
            var5 = var4.length;

            for(var6 = 0; var6 < var5; ++var6) {
               var7 = var4[var6];
               var7.setSoloMute(false);
            }

         }
      }
   }

   private void setSoloMute(boolean var1) {
      synchronized(this.control_mutex) {
         if (this.solomute != var1) {
            this.solomute = var1;

            for(int var3 = 0; var3 < this.voices.length; ++var3) {
               if (this.voices[var3].active && this.voices[var3].channel == this.channel) {
                  this.voices[var3].setSoloMute(this.solomute);
               }
            }

         }
      }
   }

   public boolean getSolo() {
      synchronized(this.control_mutex) {
         return this.solo;
      }
   }

   static {
      for(int var0 = 0; var0 < dontResetControls.length; ++var0) {
         dontResetControls[var0] = false;
      }

      dontResetControls[0] = true;
      dontResetControls[32] = true;
      dontResetControls[7] = true;
      dontResetControls[8] = true;
      dontResetControls[10] = true;
      dontResetControls[11] = true;
      dontResetControls[91] = true;
      dontResetControls[92] = true;
      dontResetControls[93] = true;
      dontResetControls[94] = true;
      dontResetControls[95] = true;
      dontResetControls[70] = true;
      dontResetControls[71] = true;
      dontResetControls[72] = true;
      dontResetControls[73] = true;
      dontResetControls[74] = true;
      dontResetControls[75] = true;
      dontResetControls[76] = true;
      dontResetControls[77] = true;
      dontResetControls[78] = true;
      dontResetControls[79] = true;
      dontResetControls[120] = true;
      dontResetControls[121] = true;
      dontResetControls[122] = true;
      dontResetControls[123] = true;
      dontResetControls[124] = true;
      dontResetControls[125] = true;
      dontResetControls[126] = true;
      dontResetControls[127] = true;
      dontResetControls[6] = true;
      dontResetControls[38] = true;
      dontResetControls[96] = true;
      dontResetControls[97] = true;
      dontResetControls[98] = true;
      dontResetControls[99] = true;
      dontResetControls[100] = true;
      dontResetControls[101] = true;
   }

   private class MidiControlObject implements SoftControl {
      double[] pitch;
      double[] channel_pressure;
      double[] poly_pressure;

      private MidiControlObject() {
         this.pitch = SoftChannel.this.co_midi_pitch;
         this.channel_pressure = SoftChannel.this.co_midi_channel_pressure;
         this.poly_pressure = new double[1];
      }

      public double[] get(int var1, String var2) {
         if (var2 == null) {
            return null;
         } else if (var2.equals("pitch")) {
            return this.pitch;
         } else if (var2.equals("channel_pressure")) {
            return this.channel_pressure;
         } else {
            return var2.equals("poly_pressure") ? this.poly_pressure : null;
         }
      }

      // $FF: synthetic method
      MidiControlObject(Object var2) {
         this();
      }
   }
}
