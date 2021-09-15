package com.sun.media.sound;

import java.io.IOException;
import java.util.Arrays;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.VoiceStatus;

public abstract class SoftAbstractResampler implements SoftResampler {
   public abstract int getPadding();

   public abstract void interpolate(float[] var1, float[] var2, float var3, float[] var4, float var5, float[] var6, int[] var7, int var8);

   public final SoftResamplerStreamer openStreamer() {
      return new SoftAbstractResampler.ModelAbstractResamplerStream();
   }

   private class ModelAbstractResamplerStream implements SoftResamplerStreamer {
      AudioFloatInputStream stream;
      boolean stream_eof = false;
      int loopmode;
      boolean loopdirection = true;
      float loopstart;
      float looplen;
      float target_pitch;
      float[] current_pitch = new float[1];
      boolean started;
      boolean eof;
      int sector_pos = 0;
      int sector_size = 400;
      int sector_loopstart = -1;
      boolean markset = false;
      int marklimit = 0;
      int streampos = 0;
      int nrofchannels = 2;
      boolean noteOff_flag = false;
      float[][] ibuffer;
      boolean ibuffer_order = true;
      float[] sbuffer;
      int pad = SoftAbstractResampler.this.getPadding();
      int pad2 = SoftAbstractResampler.this.getPadding() * 2;
      float[] ix = new float[1];
      int[] ox = new int[1];
      float samplerateconv = 1.0F;
      float pitchcorrection = 0.0F;

      ModelAbstractResamplerStream() {
         this.ibuffer = new float[2][this.sector_size + this.pad2];
         this.ibuffer_order = true;
      }

      public void noteOn(MidiChannel var1, VoiceStatus var2, int var3, int var4) {
      }

      public void noteOff(int var1) {
         this.noteOff_flag = true;
      }

      public void open(ModelWavetable var1, float var2) throws IOException {
         this.eof = false;
         this.nrofchannels = var1.getChannels();
         if (this.ibuffer.length < this.nrofchannels) {
            this.ibuffer = new float[this.nrofchannels][this.sector_size + this.pad2];
         }

         this.stream = var1.openStream();
         this.streampos = 0;
         this.stream_eof = false;
         this.pitchcorrection = var1.getPitchcorrection();
         this.samplerateconv = this.stream.getFormat().getSampleRate() / var2;
         this.looplen = var1.getLoopLength();
         this.loopstart = var1.getLoopStart();
         this.sector_loopstart = (int)(this.loopstart / (float)this.sector_size);
         --this.sector_loopstart;
         this.sector_pos = 0;
         if (this.sector_loopstart < 0) {
            this.sector_loopstart = 0;
         }

         this.started = false;
         this.loopmode = var1.getLoopType();
         if (this.loopmode != 0) {
            this.markset = false;
            this.marklimit = this.nrofchannels * (int)(this.looplen + (float)this.pad2 + 1.0F);
         } else {
            this.markset = true;
         }

         this.target_pitch = this.samplerateconv;
         this.current_pitch[0] = this.samplerateconv;
         this.ibuffer_order = true;
         this.loopdirection = true;
         this.noteOff_flag = false;

         for(int var3 = 0; var3 < this.nrofchannels; ++var3) {
            Arrays.fill(this.ibuffer[var3], this.sector_size, this.sector_size + this.pad2, 0.0F);
         }

         this.ix[0] = (float)this.pad;
         this.eof = false;
         this.ix[0] = (float)(this.sector_size + this.pad);
         this.sector_pos = -1;
         this.streampos = -this.sector_size;
         this.nextBuffer();
      }

      public void setPitch(float var1) {
         this.target_pitch = (float)Math.exp((double)(this.pitchcorrection + var1) * (Math.log(2.0D) / 1200.0D)) * this.samplerateconv;
         if (!this.started) {
            this.current_pitch[0] = this.target_pitch;
         }

      }

      public void nextBuffer() throws IOException {
         float[] var10000;
         if (this.ix[0] < (float)this.pad && this.markset) {
            this.stream.reset();
            var10000 = this.ix;
            var10000[0] += (float)(this.streampos - this.sector_loopstart * this.sector_size);
            this.sector_pos = this.sector_loopstart;
            this.streampos = this.sector_pos * this.sector_size;
            var10000 = this.ix;
            var10000[0] += (float)this.sector_size;
            --this.sector_pos;
            this.streampos -= this.sector_size;
            this.stream_eof = false;
         }

         if (this.ix[0] >= (float)(this.sector_size + this.pad) && this.stream_eof) {
            this.eof = true;
         } else {
            int var1;
            if (this.ix[0] >= (float)(this.sector_size * 4 + this.pad)) {
               var1 = (int)((this.ix[0] - (float)(this.sector_size * 4) + (float)this.pad) / (float)this.sector_size);
               var10000 = this.ix;
               var10000[0] -= (float)(this.sector_size * var1);
               this.sector_pos += var1;
               this.streampos += this.sector_size * var1;
               this.stream.skip((long)(this.sector_size * var1));
            }

            for(; this.ix[0] >= (float)(this.sector_size + this.pad); this.ibuffer_order = true) {
               if (!this.markset && this.sector_pos + 1 == this.sector_loopstart) {
                  this.stream.mark(this.marklimit);
                  this.markset = true;
               }

               var10000 = this.ix;
               var10000[0] -= (float)this.sector_size;
               ++this.sector_pos;
               this.streampos += this.sector_size;

               int var3;
               for(var1 = 0; var1 < this.nrofchannels; ++var1) {
                  float[] var2 = this.ibuffer[var1];

                  for(var3 = 0; var3 < this.pad2; ++var3) {
                     var2[var3] = var2[var3 + this.sector_size];
                  }
               }

               int var10;
               if (this.nrofchannels == 1) {
                  var1 = this.stream.read(this.ibuffer[0], this.pad2, this.sector_size);
               } else {
                  var10 = this.sector_size * this.nrofchannels;
                  if (this.sbuffer == null || this.sbuffer.length < var10) {
                     this.sbuffer = new float[var10];
                  }

                  var3 = this.stream.read(this.sbuffer, 0, var10);
                  if (var3 == -1) {
                     var1 = -1;
                  } else {
                     var1 = var3 / this.nrofchannels;

                     for(int var4 = 0; var4 < this.nrofchannels; ++var4) {
                        float[] var5 = this.ibuffer[var4];
                        int var6 = var4;
                        int var7 = this.nrofchannels;
                        int var8 = this.pad2;

                        for(int var9 = 0; var9 < var1; ++var8) {
                           var5[var8] = this.sbuffer[var6];
                           ++var9;
                           var6 += var7;
                        }
                     }
                  }
               }

               if (var1 == -1) {
                  boolean var11 = false;
                  this.stream_eof = true;

                  for(var10 = 0; var10 < this.nrofchannels; ++var10) {
                     Arrays.fill(this.ibuffer[var10], this.pad2, this.pad2 + this.sector_size, 0.0F);
                  }

                  return;
               }

               if (var1 != this.sector_size) {
                  for(var10 = 0; var10 < this.nrofchannels; ++var10) {
                     Arrays.fill(this.ibuffer[var10], this.pad2 + var1, this.pad2 + this.sector_size, 0.0F);
                  }
               }
            }

         }
      }

      public void reverseBuffers() {
         this.ibuffer_order = !this.ibuffer_order;

         for(int var1 = 0; var1 < this.nrofchannels; ++var1) {
            float[] var2 = this.ibuffer[var1];
            int var3 = var2.length - 1;
            int var4 = var2.length / 2;

            for(int var5 = 0; var5 < var4; ++var5) {
               float var6 = var2[var5];
               var2[var5] = var2[var3 - var5];
               var2[var3 - var5] = var6;
            }
         }

      }

      public int read(float[][] var1, int var2, int var3) throws IOException {
         if (this.eof) {
            return -1;
         } else {
            if (this.noteOff_flag && (this.loopmode & 2) != 0 && this.loopdirection) {
               this.loopmode = 0;
            }

            float var4 = (this.target_pitch - this.current_pitch[0]) / (float)var3;
            float[] var5 = this.current_pitch;
            this.started = true;
            int[] var6 = this.ox;
            var6[0] = var2;
            int var7 = var3 + var2;
            float var8 = (float)(this.sector_size + this.pad);
            if (!this.loopdirection) {
               var8 = (float)this.pad;
            }

            while(true) {
               while(true) {
                  while(var6[0] != var7) {
                     this.nextBuffer();
                     float var9;
                     int var10;
                     float var11;
                     int var12;
                     float[] var10000;
                     if (!this.loopdirection) {
                        if ((float)this.streampos < this.loopstart + (float)this.pad) {
                           var8 = this.loopstart - (float)this.streampos + (float)this.pad2;
                           if (this.ix[0] <= var8) {
                              if ((this.loopmode & 4) != 0) {
                                 this.loopdirection = true;
                                 var8 = (float)(this.sector_size + this.pad);
                              } else {
                                 var10000 = this.ix;
                                 var10000[0] += this.looplen;
                                 var8 = (float)this.pad;
                              }
                              continue;
                           }
                        }

                        if (this.ibuffer_order != this.loopdirection) {
                           this.reverseBuffers();
                        }

                        this.ix[0] = (float)(this.sector_size + this.pad2) - this.ix[0];
                        var8 = (float)(this.sector_size + this.pad2) - var8;
                        ++var8;
                        var9 = this.ix[0];
                        var10 = var6[0];
                        var11 = var5[0];

                        for(var12 = 0; var12 < this.nrofchannels; ++var12) {
                           if (var1[var12] != null) {
                              this.ix[0] = var9;
                              var6[0] = var10;
                              var5[0] = var11;
                              SoftAbstractResampler.this.interpolate(this.ibuffer[var12], this.ix, var8, var5, var4, var1[var12], var6, var7);
                           }
                        }

                        this.ix[0] = (float)(this.sector_size + this.pad2) - this.ix[0];
                        --var8;
                        var8 = (float)(this.sector_size + this.pad2) - var8;
                        if (this.eof) {
                           var5[0] = this.target_pitch;
                           return var6[0] - var2;
                        }
                     } else {
                        if (this.loopmode != 0 && (float)(this.streampos + this.sector_size) > this.looplen + this.loopstart + (float)this.pad) {
                           var8 = this.loopstart + this.looplen - (float)this.streampos + (float)this.pad2;
                           if (this.ix[0] >= var8) {
                              if ((this.loopmode & 4) == 0 && (this.loopmode & 8) == 0) {
                                 var8 = (float)(this.sector_size + this.pad);
                                 var10000 = this.ix;
                                 var10000[0] -= this.looplen;
                                 continue;
                              }

                              this.loopdirection = false;
                              var8 = (float)this.pad;
                              continue;
                           }
                        }

                        if (this.ibuffer_order != this.loopdirection) {
                           this.reverseBuffers();
                        }

                        var9 = this.ix[0];
                        var10 = var6[0];
                        var11 = var5[0];

                        for(var12 = 0; var12 < this.nrofchannels; ++var12) {
                           if (var1[var12] != null) {
                              this.ix[0] = var9;
                              var6[0] = var10;
                              var5[0] = var11;
                              SoftAbstractResampler.this.interpolate(this.ibuffer[var12], this.ix, var8, var5, var4, var1[var12], var6, var7);
                           }
                        }

                        if (this.eof) {
                           var5[0] = this.target_pitch;
                           return var6[0] - var2;
                        }
                     }
                  }

                  var5[0] = this.target_pitch;
                  return var3;
               }
            }
         }
      }

      public void close() throws IOException {
         this.stream.close();
      }
   }
}
