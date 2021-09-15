package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.FormatConversionProvider;

public final class AudioFloatFormatConverter extends FormatConversionProvider {
   private final AudioFormat.Encoding[] formats;

   public AudioFloatFormatConverter() {
      this.formats = new AudioFormat.Encoding[]{AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT};
   }

   public AudioInputStream getAudioInputStream(AudioFormat.Encoding var1, AudioInputStream var2) {
      if (var2.getFormat().getEncoding().equals(var1)) {
         return var2;
      } else {
         AudioFormat var3 = var2.getFormat();
         int var4 = var3.getChannels();
         float var6 = var3.getSampleRate();
         int var7 = var3.getSampleSizeInBits();
         boolean var8 = var3.isBigEndian();
         if (var1.equals(AudioFormat.Encoding.PCM_FLOAT)) {
            var7 = 32;
         }

         AudioFormat var9 = new AudioFormat(var1, var6, var7, var4, var4 * var7 / 8, var6, var8);
         return this.getAudioInputStream(var9, var2);
      }
   }

   public AudioInputStream getAudioInputStream(AudioFormat var1, AudioInputStream var2) {
      if (!this.isConversionSupported(var1, var2.getFormat())) {
         throw new IllegalArgumentException("Unsupported conversion: " + var2.getFormat().toString() + " to " + var1.toString());
      } else {
         return this.getAudioInputStream(var1, AudioFloatInputStream.getInputStream(var2));
      }
   }

   public AudioInputStream getAudioInputStream(AudioFormat var1, AudioFloatInputStream var2) {
      if (!this.isConversionSupported(var1, ((AudioFloatInputStream)var2).getFormat())) {
         throw new IllegalArgumentException("Unsupported conversion: " + ((AudioFloatInputStream)var2).getFormat().toString() + " to " + var1.toString());
      } else {
         if (var1.getChannels() != ((AudioFloatInputStream)var2).getFormat().getChannels()) {
            var2 = new AudioFloatFormatConverter.AudioFloatInputStreamChannelMixer((AudioFloatInputStream)var2, var1.getChannels());
         }

         if ((double)Math.abs(var1.getSampleRate() - ((AudioFloatInputStream)var2).getFormat().getSampleRate()) > 1.0E-6D) {
            var2 = new AudioFloatFormatConverter.AudioFloatInputStreamResampler((AudioFloatInputStream)var2, var1);
         }

         return new AudioInputStream(new AudioFloatFormatConverter.AudioFloatFormatConverterInputStream(var1, (AudioFloatInputStream)var2), var1, ((AudioFloatInputStream)var2).getFrameLength());
      }
   }

   public AudioFormat.Encoding[] getSourceEncodings() {
      return new AudioFormat.Encoding[]{AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT};
   }

   public AudioFormat.Encoding[] getTargetEncodings() {
      return new AudioFormat.Encoding[]{AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT};
   }

   public AudioFormat.Encoding[] getTargetEncodings(AudioFormat var1) {
      return AudioFloatConverter.getConverter(var1) == null ? new AudioFormat.Encoding[0] : new AudioFormat.Encoding[]{AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT};
   }

   public AudioFormat[] getTargetFormats(AudioFormat.Encoding var1, AudioFormat var2) {
      if (AudioFloatConverter.getConverter(var2) == null) {
         return new AudioFormat[0];
      } else {
         int var3 = var2.getChannels();
         ArrayList var4 = new ArrayList();
         if (var1.equals(AudioFormat.Encoding.PCM_SIGNED)) {
            var4.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, 8, var3, var3, -1.0F, false));
         }

         if (var1.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
            var4.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, 8, var3, var3, -1.0F, false));
         }

         for(int var5 = 16; var5 < 32; var5 += 8) {
            if (var1.equals(AudioFormat.Encoding.PCM_SIGNED)) {
               var4.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, var5, var3, var3 * var5 / 8, -1.0F, false));
               var4.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, var5, var3, var3 * var5 / 8, -1.0F, true));
            }

            if (var1.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
               var4.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, var5, var3, var3 * var5 / 8, -1.0F, true));
               var4.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, var5, var3, var3 * var5 / 8, -1.0F, false));
            }
         }

         if (var1.equals(AudioFormat.Encoding.PCM_FLOAT)) {
            var4.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 32, var3, var3 * 4, -1.0F, false));
            var4.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 32, var3, var3 * 4, -1.0F, true));
            var4.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 64, var3, var3 * 8, -1.0F, false));
            var4.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 64, var3, var3 * 8, -1.0F, true));
         }

         return (AudioFormat[])var4.toArray(new AudioFormat[var4.size()]);
      }
   }

   public boolean isConversionSupported(AudioFormat var1, AudioFormat var2) {
      if (AudioFloatConverter.getConverter(var2) == null) {
         return false;
      } else if (AudioFloatConverter.getConverter(var1) == null) {
         return false;
      } else if (var2.getChannels() <= 0) {
         return false;
      } else {
         return var1.getChannels() > 0;
      }
   }

   public boolean isConversionSupported(AudioFormat.Encoding var1, AudioFormat var2) {
      if (AudioFloatConverter.getConverter(var2) == null) {
         return false;
      } else {
         for(int var3 = 0; var3 < this.formats.length; ++var3) {
            if (var1.equals(this.formats[var3])) {
               return true;
            }
         }

         return false;
      }
   }

   private static class AudioFloatInputStreamResampler extends AudioFloatInputStream {
      private final AudioFloatInputStream ais;
      private final AudioFormat targetFormat;
      private float[] skipbuffer;
      private SoftAbstractResampler resampler;
      private final float[] pitch = new float[1];
      private final float[] ibuffer2;
      private final float[][] ibuffer;
      private float ibuffer_index = 0.0F;
      private int ibuffer_len = 0;
      private final int nrofchannels;
      private float[][] cbuffer;
      private final int buffer_len = 512;
      private final int pad;
      private final int pad2;
      private final float[] ix = new float[1];
      private final int[] ox = new int[1];
      private float[][] mark_ibuffer = (float[][])null;
      private float mark_ibuffer_index = 0.0F;
      private int mark_ibuffer_len = 0;

      AudioFloatInputStreamResampler(AudioFloatInputStream var1, AudioFormat var2) {
         this.ais = var1;
         AudioFormat var3 = var1.getFormat();
         this.targetFormat = new AudioFormat(var3.getEncoding(), var2.getSampleRate(), var3.getSampleSizeInBits(), var3.getChannels(), var3.getFrameSize(), var2.getSampleRate(), var3.isBigEndian());
         this.nrofchannels = this.targetFormat.getChannels();
         Object var4 = var2.getProperty("interpolation");
         if (var4 != null && var4 instanceof String) {
            String var5 = (String)var4;
            if (var5.equalsIgnoreCase("point")) {
               this.resampler = new SoftPointResampler();
            }

            if (var5.equalsIgnoreCase("linear")) {
               this.resampler = new SoftLinearResampler2();
            }

            if (var5.equalsIgnoreCase("linear1")) {
               this.resampler = new SoftLinearResampler();
            }

            if (var5.equalsIgnoreCase("linear2")) {
               this.resampler = new SoftLinearResampler2();
            }

            if (var5.equalsIgnoreCase("cubic")) {
               this.resampler = new SoftCubicResampler();
            }

            if (var5.equalsIgnoreCase("lanczos")) {
               this.resampler = new SoftLanczosResampler();
            }

            if (var5.equalsIgnoreCase("sinc")) {
               this.resampler = new SoftSincResampler();
            }
         }

         if (this.resampler == null) {
            this.resampler = new SoftLinearResampler2();
         }

         this.pitch[0] = var3.getSampleRate() / var2.getSampleRate();
         this.pad = this.resampler.getPadding();
         this.pad2 = this.pad * 2;
         this.ibuffer = new float[this.nrofchannels][512 + this.pad2];
         this.ibuffer2 = new float[this.nrofchannels * 512];
         this.ibuffer_index = (float)(512 + this.pad);
         this.ibuffer_len = 512;
      }

      public int available() throws IOException {
         return 0;
      }

      public void close() throws IOException {
         this.ais.close();
      }

      public AudioFormat getFormat() {
         return this.targetFormat;
      }

      public long getFrameLength() {
         return -1L;
      }

      public void mark(int var1) {
         this.ais.mark((int)((float)var1 * this.pitch[0]));
         this.mark_ibuffer_index = this.ibuffer_index;
         this.mark_ibuffer_len = this.ibuffer_len;
         if (this.mark_ibuffer == null) {
            this.mark_ibuffer = new float[this.ibuffer.length][this.ibuffer[0].length];
         }

         for(int var2 = 0; var2 < this.ibuffer.length; ++var2) {
            float[] var3 = this.ibuffer[var2];
            float[] var4 = this.mark_ibuffer[var2];

            for(int var5 = 0; var5 < var4.length; ++var5) {
               var4[var5] = var3[var5];
            }
         }

      }

      public boolean markSupported() {
         return this.ais.markSupported();
      }

      private void readNextBuffer() throws IOException {
         if (this.ibuffer_len != -1) {
            int var1;
            int var4;
            int var5;
            for(var1 = 0; var1 < this.nrofchannels; ++var1) {
               float[] var2 = this.ibuffer[var1];
               int var3 = this.ibuffer_len + this.pad2;
               var4 = this.ibuffer_len;

               for(var5 = 0; var4 < var3; ++var5) {
                  var2[var5] = var2[var4];
                  ++var4;
               }
            }

            this.ibuffer_index -= (float)this.ibuffer_len;
            this.ibuffer_len = this.ais.read(this.ibuffer2);
            if (this.ibuffer_len < 0) {
               Arrays.fill(this.ibuffer2, 0, this.ibuffer2.length, 0.0F);
            } else {
               while(this.ibuffer_len < this.ibuffer2.length) {
                  var1 = this.ais.read(this.ibuffer2, this.ibuffer_len, this.ibuffer2.length - this.ibuffer_len);
                  if (var1 == -1) {
                     break;
                  }

                  this.ibuffer_len += var1;
               }

               Arrays.fill(this.ibuffer2, this.ibuffer_len, this.ibuffer2.length, 0.0F);
               this.ibuffer_len /= this.nrofchannels;
            }

            var1 = this.ibuffer2.length;

            for(int var6 = 0; var6 < this.nrofchannels; ++var6) {
               float[] var7 = this.ibuffer[var6];
               var4 = var6;

               for(var5 = this.pad2; var4 < var1; ++var5) {
                  var7[var5] = this.ibuffer2[var4];
                  var4 += this.nrofchannels;
               }
            }

         }
      }

      public int read(float[] var1, int var2, int var3) throws IOException {
         if (this.cbuffer == null || this.cbuffer[0].length < var3 / this.nrofchannels) {
            this.cbuffer = new float[this.nrofchannels][var3 / this.nrofchannels];
         }

         if (this.ibuffer_len == -1) {
            return -1;
         } else if (var3 < 0) {
            return 0;
         } else {
            int var4 = var2 + var3;
            int var5 = var3 / this.nrofchannels;
            int var6 = 0;

            int var8;
            int var9;
            float[] var10;
            for(int var7 = this.ibuffer_len; var5 > 0; var5 -= var6 - var8) {
               if (this.ibuffer_len >= 0) {
                  if (this.ibuffer_index >= (float)(this.ibuffer_len + this.pad)) {
                     this.readNextBuffer();
                  }

                  var7 = this.ibuffer_len + this.pad;
               }

               if (this.ibuffer_len < 0) {
                  var7 = this.pad2;
                  if (this.ibuffer_index >= (float)var7) {
                     break;
                  }
               }

               if (this.ibuffer_index < 0.0F) {
                  break;
               }

               var8 = var6;

               for(var9 = 0; var9 < this.nrofchannels; ++var9) {
                  this.ix[0] = this.ibuffer_index;
                  this.ox[0] = var6;
                  var10 = this.ibuffer[var9];
                  this.resampler.interpolate(var10, this.ix, (float)var7, this.pitch, 0.0F, this.cbuffer[var9], this.ox, var3 / this.nrofchannels);
               }

               this.ibuffer_index = this.ix[0];
               var6 = this.ox[0];
            }

            for(var8 = 0; var8 < this.nrofchannels; ++var8) {
               var9 = 0;
               var10 = this.cbuffer[var8];

               for(int var11 = var8 + var2; var11 < var4; var11 += this.nrofchannels) {
                  var1[var11] = var10[var9++];
               }
            }

            return var3 - var5 * this.nrofchannels;
         }
      }

      public void reset() throws IOException {
         this.ais.reset();
         if (this.mark_ibuffer != null) {
            this.ibuffer_index = this.mark_ibuffer_index;
            this.ibuffer_len = this.mark_ibuffer_len;

            for(int var1 = 0; var1 < this.ibuffer.length; ++var1) {
               float[] var2 = this.mark_ibuffer[var1];
               float[] var3 = this.ibuffer[var1];

               for(int var4 = 0; var4 < var3.length; ++var4) {
                  var3[var4] = var2[var4];
               }
            }

         }
      }

      public long skip(long var1) throws IOException {
         if (var1 < 0L) {
            return 0L;
         } else {
            if (this.skipbuffer == null) {
               this.skipbuffer = new float[1024 * this.targetFormat.getFrameSize()];
            }

            float[] var3 = this.skipbuffer;

            long var4;
            int var6;
            for(var4 = var1; var4 > 0L; var4 -= (long)var6) {
               var6 = this.read(var3, 0, (int)Math.min(var4, (long)this.skipbuffer.length));
               if (var6 < 0) {
                  if (var4 == var1) {
                     return (long)var6;
                  }
                  break;
               }
            }

            return var1 - var4;
         }
      }
   }

   private static class AudioFloatInputStreamChannelMixer extends AudioFloatInputStream {
      private final int targetChannels;
      private final int sourceChannels;
      private final AudioFloatInputStream ais;
      private final AudioFormat targetFormat;
      private float[] conversion_buffer;

      AudioFloatInputStreamChannelMixer(AudioFloatInputStream var1, int var2) {
         this.sourceChannels = var1.getFormat().getChannels();
         this.targetChannels = var2;
         this.ais = var1;
         AudioFormat var3 = var1.getFormat();
         this.targetFormat = new AudioFormat(var3.getEncoding(), var3.getSampleRate(), var3.getSampleSizeInBits(), var2, var3.getFrameSize() / this.sourceChannels * var2, var3.getFrameRate(), var3.isBigEndian());
      }

      public int available() throws IOException {
         return this.ais.available() / this.sourceChannels * this.targetChannels;
      }

      public void close() throws IOException {
         this.ais.close();
      }

      public AudioFormat getFormat() {
         return this.targetFormat;
      }

      public long getFrameLength() {
         return this.ais.getFrameLength();
      }

      public void mark(int var1) {
         this.ais.mark(var1 / this.targetChannels * this.sourceChannels);
      }

      public boolean markSupported() {
         return this.ais.markSupported();
      }

      public int read(float[] var1, int var2, int var3) throws IOException {
         int var4 = var3 / this.targetChannels * this.sourceChannels;
         if (this.conversion_buffer == null || this.conversion_buffer.length < var4) {
            this.conversion_buffer = new float[var4];
         }

         int var5 = this.ais.read(this.conversion_buffer, 0, var4);
         if (var5 < 0) {
            return var5;
         } else {
            int var6;
            int var7;
            int var8;
            int var9;
            if (this.sourceChannels == 1) {
               var6 = this.targetChannels;

               for(var7 = 0; var7 < this.targetChannels; ++var7) {
                  var8 = 0;

                  for(var9 = var2 + var7; var8 < var4; var9 += var6) {
                     var1[var9] = this.conversion_buffer[var8];
                     ++var8;
                  }
               }
            } else if (this.targetChannels == 1) {
               var6 = this.sourceChannels;
               var7 = 0;

               for(var8 = var2; var7 < var4; ++var8) {
                  var1[var8] = this.conversion_buffer[var7];
                  var7 += var6;
               }

               for(var7 = 1; var7 < this.sourceChannels; ++var7) {
                  var8 = var7;

                  for(var9 = var2; var8 < var4; ++var9) {
                     var1[var9] += this.conversion_buffer[var8];
                     var8 += var6;
                  }
               }

               float var13 = 1.0F / (float)this.sourceChannels;
               var8 = 0;

               for(var9 = var2; var8 < var4; ++var9) {
                  var1[var9] *= var13;
                  var8 += var6;
               }
            } else {
               var6 = Math.min(this.sourceChannels, this.targetChannels);
               var7 = var2 + var3;
               var8 = this.targetChannels;
               var9 = this.sourceChannels;

               int var10;
               int var11;
               for(var10 = 0; var10 < var6; ++var10) {
                  var11 = var2 + var10;

                  for(int var12 = var10; var11 < var7; var12 += var9) {
                     var1[var11] = this.conversion_buffer[var12];
                     var11 += var8;
                  }
               }

               for(var10 = var6; var10 < this.targetChannels; ++var10) {
                  for(var11 = var2 + var10; var11 < var7; var11 += var8) {
                     var1[var11] = 0.0F;
                  }
               }
            }

            return var5 / this.sourceChannels * this.targetChannels;
         }
      }

      public void reset() throws IOException {
         this.ais.reset();
      }

      public long skip(long var1) throws IOException {
         long var3 = this.ais.skip(var1 / (long)this.targetChannels * (long)this.sourceChannels);
         return var3 < 0L ? var3 : var3 / (long)this.sourceChannels * (long)this.targetChannels;
      }
   }

   private static class AudioFloatFormatConverterInputStream extends InputStream {
      private final AudioFloatConverter converter;
      private final AudioFloatInputStream stream;
      private float[] readfloatbuffer;
      private final int fsize;

      AudioFloatFormatConverterInputStream(AudioFormat var1, AudioFloatInputStream var2) {
         this.stream = var2;
         this.converter = AudioFloatConverter.getConverter(var1);
         this.fsize = (var1.getSampleSizeInBits() + 7) / 8;
      }

      public int read() throws IOException {
         byte[] var1 = new byte[1];
         int var2 = this.read(var1);
         return var2 < 0 ? var2 : var1[0] & 255;
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         int var4 = var3 / this.fsize;
         if (this.readfloatbuffer == null || this.readfloatbuffer.length < var4) {
            this.readfloatbuffer = new float[var4];
         }

         int var5 = this.stream.read(this.readfloatbuffer, 0, var4);
         if (var5 < 0) {
            return var5;
         } else {
            this.converter.toByteArray(this.readfloatbuffer, 0, var5, var1, var2);
            return var5 * this.fsize;
         }
      }

      public int available() throws IOException {
         int var1 = this.stream.available();
         return var1 < 0 ? var1 : var1 * this.fsize;
      }

      public void close() throws IOException {
         this.stream.close();
      }

      public synchronized void mark(int var1) {
         this.stream.mark(var1 * this.fsize);
      }

      public boolean markSupported() {
         return this.stream.markSupported();
      }

      public synchronized void reset() throws IOException {
         this.stream.reset();
      }

      public long skip(long var1) throws IOException {
         long var3 = this.stream.skip(var1 / (long)this.fsize);
         return var3 < 0L ? var3 : var3 * (long)this.fsize;
      }
   }
}
