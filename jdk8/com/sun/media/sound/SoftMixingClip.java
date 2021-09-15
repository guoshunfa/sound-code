package com.sun.media.sound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;

public final class SoftMixingClip extends SoftMixingDataLine implements Clip {
   private AudioFormat format;
   private int framesize;
   private byte[] data;
   private final InputStream datastream = new InputStream() {
      public int read() throws IOException {
         byte[] var1 = new byte[1];
         int var2 = this.read(var1);
         return var2 < 0 ? var2 : var1[0] & 255;
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         int var4;
         int var5;
         if (SoftMixingClip.this._loopcount != 0) {
            var4 = SoftMixingClip.this._loopend * SoftMixingClip.this.framesize;
            var5 = SoftMixingClip.this._loopstart * SoftMixingClip.this.framesize;
            int var6 = SoftMixingClip.this._frameposition * SoftMixingClip.this.framesize;
            if (var6 + var3 >= var4 && var6 < var4) {
               int var7 = var2 + var3;

               int var8;
               int var9;
               for(var8 = var2; var2 != var7; var2 += var3) {
                  if (var6 == var4) {
                     if (SoftMixingClip.this._loopcount == 0) {
                        break;
                     }

                     var6 = var5;
                     if (SoftMixingClip.this._loopcount != -1) {
                        SoftMixingClip.this._loopcount--;
                     }
                  }

                  var3 = var7 - var2;
                  var9 = var4 - var6;
                  if (var3 > var9) {
                     var3 = var9;
                  }

                  System.arraycopy(SoftMixingClip.this.data, var6, var1, var2, var3);
               }

               if (SoftMixingClip.this._loopcount == 0) {
                  var3 = var7 - var2;
                  var9 = var4 - var6;
                  if (var3 > var9) {
                     var3 = var9;
                  }

                  System.arraycopy(SoftMixingClip.this.data, var6, var1, var2, var3);
                  var2 += var3;
               }

               SoftMixingClip.this._frameposition = var6 / SoftMixingClip.this.framesize;
               return var8 - var2;
            }
         }

         var4 = SoftMixingClip.this._frameposition * SoftMixingClip.this.framesize;
         var5 = SoftMixingClip.this.bufferSize - var4;
         if (var5 == 0) {
            return -1;
         } else {
            if (var3 > var5) {
               var3 = var5;
            }

            System.arraycopy(SoftMixingClip.this.data, var4, var1, var2, var3);
            SoftMixingClip.this._frameposition = SoftMixingClip.this._frameposition + var3 / SoftMixingClip.this.framesize;
            return var3;
         }
      }
   };
   private int offset;
   private int bufferSize;
   private float[] readbuffer;
   private boolean open = false;
   private AudioFormat outputformat;
   private int out_nrofchannels;
   private int in_nrofchannels;
   private int frameposition = 0;
   private boolean frameposition_sg = false;
   private boolean active_sg = false;
   private int loopstart = 0;
   private int loopend = -1;
   private boolean active = false;
   private int loopcount = 0;
   private boolean _active = false;
   private int _frameposition = 0;
   private boolean loop_sg = false;
   private int _loopcount = 0;
   private int _loopstart = 0;
   private int _loopend = -1;
   private float _rightgain;
   private float _leftgain;
   private float _eff1gain;
   private float _eff2gain;
   private AudioFloatInputStream afis;

   SoftMixingClip(SoftMixingMixer var1, DataLine.Info var2) {
      super(var1, var2);
   }

   protected void processControlLogic() {
      this._rightgain = this.rightgain;
      this._leftgain = this.leftgain;
      this._eff1gain = this.eff1gain;
      this._eff2gain = this.eff2gain;
      if (this.active_sg) {
         this._active = this.active;
         this.active_sg = false;
      } else {
         this.active = this._active;
      }

      if (this.frameposition_sg) {
         this._frameposition = this.frameposition;
         this.frameposition_sg = false;
         this.afis = null;
      } else {
         this.frameposition = this._frameposition;
      }

      if (this.loop_sg) {
         this._loopcount = this.loopcount;
         this._loopstart = this.loopstart;
         this._loopend = this.loopend;
      }

      if (this.afis == null) {
         this.afis = AudioFloatInputStream.getInputStream(new AudioInputStream(this.datastream, this.format, -1L));
         if ((double)Math.abs(this.format.getSampleRate() - this.outputformat.getSampleRate()) > 1.0E-6D) {
            this.afis = new SoftMixingDataLine.AudioFloatInputStreamResampler(this.afis, this.outputformat);
         }
      }

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
            if (var12 == -1) {
               this._active = false;
               return;
            }

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
         if ((double)this._eff1gain > 2.0E-4D) {
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

         if ((double)this._eff2gain > 2.0E-4D) {
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

   public int getFrameLength() {
      return this.bufferSize / this.format.getFrameSize();
   }

   public long getMicrosecondLength() {
      return (long)((double)this.getFrameLength() * (1000000.0D / (double)this.getFormat().getSampleRate()));
   }

   public void loop(int var1) {
      LineEvent var2 = null;
      synchronized(this.control_mutex) {
         if (this.isOpen()) {
            if (this.active) {
               return;
            }

            this.active = true;
            this.active_sg = true;
            this.loopcount = var1;
            var2 = new LineEvent(this, LineEvent.Type.START, this.getLongFramePosition());
         }
      }

      if (var2 != null) {
         this.sendEvent(var2);
      }

   }

   public void open(AudioInputStream var1) throws LineUnavailableException, IOException {
      if (this.isOpen()) {
         throw new IllegalStateException("Clip is already open with format " + this.getFormat() + " and frame lengh of " + this.getFrameLength());
      } else if (AudioFloatConverter.getConverter(var1.getFormat()) == null) {
         throw new IllegalArgumentException("Invalid format : " + var1.getFormat().toString());
      } else {
         int var4;
         if (var1.getFrameLength() != -1L) {
            byte[] var2 = new byte[(int)var1.getFrameLength() * var1.getFormat().getFrameSize()];
            int var3 = 512 * var1.getFormat().getFrameSize();

            int var5;
            for(var4 = 0; var4 != var2.length; var4 += var5) {
               if (var3 > var2.length - var4) {
                  var3 = var2.length - var4;
               }

               var5 = var1.read(var2, var4, var3);
               if (var5 == -1) {
                  break;
               }

               if (var5 == 0) {
                  Thread.yield();
               }
            }

            this.open(var1.getFormat(), var2, 0, var4);
         } else {
            ByteArrayOutputStream var6 = new ByteArrayOutputStream();
            byte[] var7 = new byte[512 * var1.getFormat().getFrameSize()];

            for(boolean var8 = false; (var4 = var1.read(var7)) != -1; var6.write(var7, 0, var4)) {
               if (var4 == 0) {
                  Thread.yield();
               }
            }

            this.open(var1.getFormat(), var6.toByteArray(), 0, var6.size());
         }

      }
   }

   public void open(AudioFormat var1, byte[] var2, int var3, int var4) throws LineUnavailableException {
      synchronized(this.control_mutex) {
         if (this.isOpen()) {
            throw new IllegalStateException("Clip is already open with format " + this.getFormat() + " and frame lengh of " + this.getFrameLength());
         } else if (AudioFloatConverter.getConverter(var1) == null) {
            throw new IllegalArgumentException("Invalid format : " + var1.toString());
         } else if (var4 % var1.getFrameSize() != 0) {
            throw new IllegalArgumentException("Buffer size does not represent an integral number of sample frames!");
         } else {
            if (var2 != null) {
               this.data = Arrays.copyOf(var2, var2.length);
            }

            this.offset = var3;
            this.bufferSize = var4;
            this.format = var1;
            this.framesize = var1.getFrameSize();
            this.loopstart = 0;
            this.loopend = -1;
            this.loop_sg = true;
            if (!this.mixer.isOpen()) {
               this.mixer.open();
               this.mixer.implicitOpen = true;
            }

            this.outputformat = this.mixer.getFormat();
            this.out_nrofchannels = this.outputformat.getChannels();
            this.in_nrofchannels = var1.getChannels();
            this.open = true;
            this.mixer.getMainMixer().openLine(this);
         }
      }
   }

   public void setFramePosition(int var1) {
      synchronized(this.control_mutex) {
         this.frameposition_sg = true;
         this.frameposition = var1;
      }
   }

   public void setLoopPoints(int var1, int var2) {
      synchronized(this.control_mutex) {
         if (var2 != -1) {
            if (var2 < var1) {
               throw new IllegalArgumentException("Invalid loop points : " + var1 + " - " + var2);
            }

            if (var2 * this.framesize > this.bufferSize) {
               throw new IllegalArgumentException("Invalid loop points : " + var1 + " - " + var2);
            }
         }

         if (var1 * this.framesize > this.bufferSize) {
            throw new IllegalArgumentException("Invalid loop points : " + var1 + " - " + var2);
         } else if (0 < var1) {
            throw new IllegalArgumentException("Invalid loop points : " + var1 + " - " + var2);
         } else {
            this.loopstart = var1;
            this.loopend = var2;
            this.loop_sg = true;
         }
      }
   }

   public void setMicrosecondPosition(long var1) {
      this.setFramePosition((int)((double)var1 * ((double)this.getFormat().getSampleRate() / 1000000.0D)));
   }

   public int available() {
      return 0;
   }

   public void drain() {
   }

   public void flush() {
   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   public AudioFormat getFormat() {
      return this.format;
   }

   public int getFramePosition() {
      synchronized(this.control_mutex) {
         return this.frameposition;
      }
   }

   public float getLevel() {
      return -1.0F;
   }

   public long getLongFramePosition() {
      return (long)this.getFramePosition();
   }

   public long getMicrosecondPosition() {
      return (long)((double)this.getFramePosition() * (1000000.0D / (double)this.getFormat().getSampleRate()));
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
            this.active_sg = true;
            this.loopcount = 0;
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
            this.active_sg = true;
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
      return this.open;
   }

   public void open() throws LineUnavailableException {
      if (this.data == null) {
         throw new IllegalArgumentException("Illegal call to open() in interface Clip");
      } else {
         this.open(this.format, this.data, this.offset, this.bufferSize);
      }
   }
}
