package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public final class SoftMixingSourceDataLine extends SoftMixingDataLine implements SourceDataLine {
   private boolean open = false;
   private AudioFormat format = new AudioFormat(44100.0F, 16, 2, true, false);
   private int framesize;
   private int bufferSize = -1;
   private float[] readbuffer;
   private boolean active = false;
   private byte[] cycling_buffer;
   private int cycling_read_pos = 0;
   private int cycling_write_pos = 0;
   private int cycling_avail = 0;
   private long cycling_framepos = 0L;
   private AudioFloatInputStream afis;
   private boolean _active = false;
   private AudioFormat outputformat;
   private int out_nrofchannels;
   private int in_nrofchannels;
   private float _rightgain;
   private float _leftgain;
   private float _eff1gain;
   private float _eff2gain;

   SoftMixingSourceDataLine(SoftMixingMixer var1, DataLine.Info var2) {
      super(var1, var2);
   }

   public int write(byte[] var1, int var2, int var3) {
      if (!this.isOpen()) {
         return 0;
      } else if (var3 % this.framesize != 0) {
         throw new IllegalArgumentException("Number of bytes does not represent an integral number of sample frames.");
      } else if (var2 < 0) {
         throw new ArrayIndexOutOfBoundsException(var2);
      } else if ((long)var2 + (long)var3 > (long)var1.length) {
         throw new ArrayIndexOutOfBoundsException(var1.length);
      } else {
         byte[] var4 = this.cycling_buffer;
         int var5 = this.cycling_buffer.length;
         int var6 = 0;

         while(var6 != var3) {
            int var7;
            synchronized(this.cycling_buffer) {
               int var9 = this.cycling_write_pos;
               var7 = this.cycling_avail;

               while(var6 != var3 && var7 != var5) {
                  var4[var9++] = var1[var2++];
                  ++var6;
                  ++var7;
                  if (var9 == var5) {
                     var9 = 0;
                  }
               }

               this.cycling_avail = var7;
               this.cycling_write_pos = var9;
               if (var6 == var3) {
                  return var6;
               }
            }

            if (var7 == var5) {
               try {
                  Thread.sleep(1L);
               } catch (InterruptedException var11) {
                  return var6;
               }

               if (!this.isRunning()) {
                  return var6;
               }
            }
         }

         return var6;
      }
   }

   protected void processControlLogic() {
      this._active = this.active;
      this._rightgain = this.rightgain;
      this._leftgain = this.leftgain;
      this._eff1gain = this.eff1gain;
      this._eff2gain = this.eff2gain;
   }

   protected void processAudioLogic(SoftAudioBuffer[] var1) {
      if (this._active) {
         float[] var2 = var1[0].array();
         float[] var3 = var1[1].array();
         int var4 = var1[0].getSize();
         int var5 = var4 * this.in_nrofchannels;
         if (this.readbuffer == null || this.readbuffer.length < var5) {
            this.readbuffer = new float[var5];
         }

         boolean var6 = false;

         try {
            int var12 = this.afis.read(this.readbuffer);
            if (var12 != this.in_nrofchannels) {
               Arrays.fill(this.readbuffer, var12, var5, 0.0F);
            }
         } catch (IOException var11) {
         }

         int var7 = this.in_nrofchannels;
         int var8 = 0;

         int var9;
         for(var9 = 0; var8 < var4; var9 += var7) {
            var2[var8] += this.readbuffer[var9] * this._leftgain;
            ++var8;
         }

         if (this.out_nrofchannels != 1) {
            if (this.in_nrofchannels == 1) {
               var8 = 0;

               for(var9 = 0; var8 < var4; var9 += var7) {
                  var3[var8] += this.readbuffer[var9] * this._rightgain;
                  ++var8;
               }
            } else {
               var8 = 0;

               for(var9 = 1; var8 < var4; var9 += var7) {
                  var3[var8] += this.readbuffer[var9] * this._rightgain;
                  ++var8;
               }
            }
         }

         int var10;
         float[] var13;
         if ((double)this._eff1gain > 1.0E-4D) {
            var13 = var1[2].array();
            var9 = 0;

            for(var10 = 0; var9 < var4; var10 += var7) {
               var13[var9] += this.readbuffer[var10] * this._eff1gain;
               ++var9;
            }

            if (this.in_nrofchannels == 2) {
               var9 = 0;

               for(var10 = 1; var9 < var4; var10 += var7) {
                  var13[var9] += this.readbuffer[var10] * this._eff1gain;
                  ++var9;
               }
            }
         }

         if ((double)this._eff2gain > 1.0E-4D) {
            var13 = var1[3].array();
            var9 = 0;

            for(var10 = 0; var9 < var4; var10 += var7) {
               var13[var9] += this.readbuffer[var10] * this._eff2gain;
               ++var9;
            }

            if (this.in_nrofchannels == 2) {
               var9 = 0;

               for(var10 = 1; var9 < var4; var10 += var7) {
                  var13[var9] += this.readbuffer[var10] * this._eff2gain;
                  ++var9;
               }
            }
         }
      }

   }

   public void open() throws LineUnavailableException {
      this.open(this.format);
   }

   public void open(AudioFormat var1) throws LineUnavailableException {
      if (this.bufferSize == -1) {
         this.bufferSize = (int)(var1.getFrameRate() / 2.0F) * var1.getFrameSize();
      }

      this.open(var1, this.bufferSize);
   }

   public void open(AudioFormat var1, int var2) throws LineUnavailableException {
      LineEvent var3 = null;
      if (var2 < var1.getFrameSize() * 32) {
         var2 = var1.getFrameSize() * 32;
      }

      synchronized(this.control_mutex) {
         if (!this.isOpen()) {
            if (!this.mixer.isOpen()) {
               this.mixer.open();
               this.mixer.implicitOpen = true;
            }

            var3 = new LineEvent(this, LineEvent.Type.OPEN, 0L);
            this.bufferSize = var2 - var2 % var1.getFrameSize();
            this.format = var1;
            this.framesize = var1.getFrameSize();
            this.outputformat = this.mixer.getFormat();
            this.out_nrofchannels = this.outputformat.getChannels();
            this.in_nrofchannels = var1.getChannels();
            this.open = true;
            this.mixer.getMainMixer().openLine(this);
            this.cycling_buffer = new byte[this.framesize * var2];
            this.cycling_read_pos = 0;
            this.cycling_write_pos = 0;
            this.cycling_avail = 0;
            this.cycling_framepos = 0L;
            InputStream var5 = new InputStream() {
               public int read() throws IOException {
                  byte[] var1 = new byte[1];
                  int var2 = this.read(var1);
                  return var2 < 0 ? var2 : var1[0] & 255;
               }

               public int available() throws IOException {
                  synchronized(SoftMixingSourceDataLine.this.cycling_buffer) {
                     return SoftMixingSourceDataLine.this.cycling_avail;
                  }
               }

               public int read(byte[] var1, int var2, int var3) throws IOException {
                  synchronized(SoftMixingSourceDataLine.this.cycling_buffer) {
                     if (var3 > SoftMixingSourceDataLine.this.cycling_avail) {
                        var3 = SoftMixingSourceDataLine.this.cycling_avail;
                     }

                     int var5 = SoftMixingSourceDataLine.this.cycling_read_pos;
                     byte[] var6 = SoftMixingSourceDataLine.this.cycling_buffer;
                     int var7 = var6.length;

                     for(int var8 = 0; var8 < var3; ++var8) {
                        var1[var2++] = var6[var5];
                        ++var5;
                        if (var5 == var7) {
                           var5 = 0;
                        }
                     }

                     SoftMixingSourceDataLine.this.cycling_read_pos = var5;
                     SoftMixingSourceDataLine.this.cycling_avail = SoftMixingSourceDataLine.this.cycling_avail - var3;
                     SoftMixingSourceDataLine.this.cycling_framepos = SoftMixingSourceDataLine.this.cycling_framepos + (long)(var3 / SoftMixingSourceDataLine.this.framesize);
                     return var3;
                  }
               }
            };
            this.afis = AudioFloatInputStream.getInputStream(new AudioInputStream(var5, var1, -1L));
            this.afis = new SoftMixingSourceDataLine.NonBlockingFloatInputStream(this.afis);
            if ((double)Math.abs(var1.getSampleRate() - this.outputformat.getSampleRate()) > 1.0E-6D) {
               this.afis = new SoftMixingDataLine.AudioFloatInputStreamResampler(this.afis, this.outputformat);
            }
         } else if (!var1.matches(this.getFormat())) {
            throw new IllegalStateException("Line is already open with format " + this.getFormat() + " and bufferSize " + this.getBufferSize());
         }
      }

      if (var3 != null) {
         this.sendEvent(var3);
      }

   }

   public int available() {
      synchronized(this.cycling_buffer) {
         return this.cycling_buffer.length - this.cycling_avail;
      }
   }

   public void drain() {
      while(true) {
         int var1;
         synchronized(this.cycling_buffer) {
            var1 = this.cycling_avail;
         }

         if (var1 != 0) {
            return;
         }

         try {
            Thread.sleep(1L);
         } catch (InterruptedException var4) {
            return;
         }
      }
   }

   public void flush() {
      synchronized(this.cycling_buffer) {
         this.cycling_read_pos = 0;
         this.cycling_write_pos = 0;
         this.cycling_avail = 0;
      }
   }

   public int getBufferSize() {
      synchronized(this.control_mutex) {
         return this.bufferSize;
      }
   }

   public AudioFormat getFormat() {
      synchronized(this.control_mutex) {
         return this.format;
      }
   }

   public int getFramePosition() {
      return (int)this.getLongFramePosition();
   }

   public float getLevel() {
      return -1.0F;
   }

   public long getLongFramePosition() {
      synchronized(this.cycling_buffer) {
         return this.cycling_framepos;
      }
   }

   public long getMicrosecondPosition() {
      return (long)((double)this.getLongFramePosition() * (1000000.0D / (double)this.getFormat().getSampleRate()));
   }

   public boolean isActive() {
      synchronized(this.control_mutex) {
         return this.active;
      }
   }

   public boolean isRunning() {
      synchronized(this.control_mutex) {
         return this.active;
      }
   }

   public void start() {
      LineEvent var1 = null;
      synchronized(this.control_mutex) {
         if (this.isOpen()) {
            if (this.active) {
               return;
            }

            this.active = true;
            var1 = new LineEvent(this, LineEvent.Type.START, this.getLongFramePosition());
         }
      }

      if (var1 != null) {
         this.sendEvent(var1);
      }

   }

   public void stop() {
      LineEvent var1 = null;
      synchronized(this.control_mutex) {
         if (this.isOpen()) {
            if (!this.active) {
               return;
            }

            this.active = false;
            var1 = new LineEvent(this, LineEvent.Type.STOP, this.getLongFramePosition());
         }
      }

      if (var1 != null) {
         this.sendEvent(var1);
      }

   }

   public void close() {
      LineEvent var1 = null;
      synchronized(this.control_mutex) {
         if (!this.isOpen()) {
            return;
         }

         this.stop();
         var1 = new LineEvent(this, LineEvent.Type.CLOSE, this.getLongFramePosition());
         this.open = false;
         this.mixer.getMainMixer().closeLine(this);
      }

      if (var1 != null) {
         this.sendEvent(var1);
      }

   }

   public boolean isOpen() {
      synchronized(this.control_mutex) {
         return this.open;
      }
   }

   private static class NonBlockingFloatInputStream extends AudioFloatInputStream {
      AudioFloatInputStream ais;

      NonBlockingFloatInputStream(AudioFloatInputStream var1) {
         this.ais = var1;
      }

      public int available() throws IOException {
         return this.ais.available();
      }

      public void close() throws IOException {
         this.ais.close();
      }

      public AudioFormat getFormat() {
         return this.ais.getFormat();
      }

      public long getFrameLength() {
         return this.ais.getFrameLength();
      }

      public void mark(int var1) {
         this.ais.mark(var1);
      }

      public boolean markSupported() {
         return this.ais.markSupported();
      }

      public int read(float[] var1, int var2, int var3) throws IOException {
         int var4 = this.available();
         if (var3 > var4) {
            int var5 = this.ais.read(var1, var2, var4);
            Arrays.fill(var1, var2 + var5, var2 + var3, 0.0F);
            return var3;
         } else {
            return this.ais.read(var1, var2, var3);
         }
      }

      public void reset() throws IOException {
         this.ais.reset();
      }

      public long skip(long var1) throws IOException {
         return this.ais.skip(var1);
      }
   }
}
