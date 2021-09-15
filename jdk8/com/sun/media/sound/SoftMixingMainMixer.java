package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioInputStream;

public final class SoftMixingMainMixer {
   public static final int CHANNEL_LEFT = 0;
   public static final int CHANNEL_RIGHT = 1;
   public static final int CHANNEL_EFFECT1 = 2;
   public static final int CHANNEL_EFFECT2 = 3;
   public static final int CHANNEL_EFFECT3 = 4;
   public static final int CHANNEL_EFFECT4 = 5;
   public static final int CHANNEL_LEFT_DRY = 10;
   public static final int CHANNEL_RIGHT_DRY = 11;
   public static final int CHANNEL_SCRATCH1 = 12;
   public static final int CHANNEL_SCRATCH2 = 13;
   public static final int CHANNEL_CHANNELMIXER_LEFT = 14;
   public static final int CHANNEL_CHANNELMIXER_RIGHT = 15;
   private final SoftMixingMixer mixer;
   private final AudioInputStream ais;
   private final SoftAudioBuffer[] buffers;
   private final SoftAudioProcessor reverb;
   private final SoftAudioProcessor chorus;
   private final SoftAudioProcessor agc;
   private final int nrofchannels;
   private final Object control_mutex;
   private final List<SoftMixingDataLine> openLinesList = new ArrayList();
   private SoftMixingDataLine[] openLines = new SoftMixingDataLine[0];

   public AudioInputStream getInputStream() {
      return this.ais;
   }

   void processAudioBuffers() {
      for(int var1 = 0; var1 < this.buffers.length; ++var1) {
         this.buffers[var1].clear();
      }

      SoftMixingDataLine[] var6;
      synchronized(this.control_mutex) {
         var6 = this.openLines;
         int var3 = 0;

         while(true) {
            if (var3 >= var6.length) {
               this.chorus.processControlLogic();
               this.reverb.processControlLogic();
               this.agc.processControlLogic();
               break;
            }

            var6[var3].processControlLogic();
            ++var3;
         }
      }

      for(int var2 = 0; var2 < var6.length; ++var2) {
         var6[var2].processAudioLogic(this.buffers);
      }

      this.chorus.processAudio();
      this.reverb.processAudio();
      this.agc.processAudio();
   }

   public SoftMixingMainMixer(SoftMixingMixer var1) {
      this.mixer = var1;
      this.nrofchannels = var1.getFormat().getChannels();
      int var2 = (int)(var1.getFormat().getSampleRate() / var1.getControlRate());
      this.control_mutex = var1.control_mutex;
      this.buffers = new SoftAudioBuffer[16];

      for(int var3 = 0; var3 < this.buffers.length; ++var3) {
         this.buffers[var3] = new SoftAudioBuffer(var2, var1.getFormat());
      }

      this.reverb = new SoftReverb();
      this.chorus = new SoftChorus();
      this.agc = new SoftLimiter();
      float var6 = var1.getFormat().getSampleRate();
      float var4 = var1.getControlRate();
      this.reverb.init(var6, var4);
      this.chorus.init(var6, var4);
      this.agc.init(var6, var4);
      this.reverb.setMixMode(true);
      this.chorus.setMixMode(true);
      this.agc.setMixMode(false);
      this.chorus.setInput(0, this.buffers[3]);
      this.chorus.setOutput(0, this.buffers[0]);
      if (this.nrofchannels != 1) {
         this.chorus.setOutput(1, this.buffers[1]);
      }

      this.chorus.setOutput(2, this.buffers[2]);
      this.reverb.setInput(0, this.buffers[2]);
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
            this.buffers = SoftMixingMainMixer.this.buffers;
            this.nrofchannels = SoftMixingMainMixer.this.mixer.getFormat().getChannels();
            this.buffersize = this.buffers[0].getSize();
            this.bbuffer = new byte[this.buffersize * (SoftMixingMainMixer.this.mixer.getFormat().getSampleSizeInBits() / 8) * this.nrofchannels];
            this.bbuffer_pos = 0;
            this.single = new byte[1];
         }

         public void fillBuffer() {
            SoftMixingMainMixer.this.processAudioBuffers();

            for(int var1 = 0; var1 < this.nrofchannels; ++var1) {
               this.buffers[var1].get(this.bbuffer, var1);
            }

            this.bbuffer_pos = 0;
         }

         public int read(byte[] var1, int var2, int var3) {
            int var4 = this.bbuffer.length;
            int var5 = var2 + var3;
            byte[] var6 = this.bbuffer;

            while(true) {
               while(var2 < var5) {
                  if (this.available() == 0) {
                     this.fillBuffer();
                  } else {
                     int var7;
                     for(var7 = this.bbuffer_pos; var2 < var5 && var7 < var4; var1[var2++] = var6[var7++]) {
                     }

                     this.bbuffer_pos = var7;
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
            SoftMixingMainMixer.this.mixer.close();
         }
      };
      this.ais = new AudioInputStream(var5, var1.getFormat(), -1L);
   }

   public void openLine(SoftMixingDataLine var1) {
      synchronized(this.control_mutex) {
         this.openLinesList.add(var1);
         this.openLines = (SoftMixingDataLine[])this.openLinesList.toArray(new SoftMixingDataLine[this.openLinesList.size()]);
      }
   }

   public void closeLine(SoftMixingDataLine var1) {
      synchronized(this.control_mutex) {
         this.openLinesList.remove(var1);
         this.openLines = (SoftMixingDataLine[])this.openLinesList.toArray(new SoftMixingDataLine[this.openLinesList.size()]);
         if (this.openLines.length == 0 && this.mixer.implicitOpen) {
            this.mixer.close();
         }

      }
   }

   public SoftMixingDataLine[] getOpenLines() {
      synchronized(this.control_mutex) {
         return this.openLines;
      }
   }

   public void close() {
      SoftMixingDataLine[] var1 = this.openLines;

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2].close();
      }

   }
}
