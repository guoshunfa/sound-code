package com.sun.media.sound;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public abstract class AudioFloatInputStream {
   public static AudioFloatInputStream getInputStream(URL var0) throws UnsupportedAudioFileException, IOException {
      return new AudioFloatInputStream.DirectAudioFloatInputStream(AudioSystem.getAudioInputStream(var0));
   }

   public static AudioFloatInputStream getInputStream(File var0) throws UnsupportedAudioFileException, IOException {
      return new AudioFloatInputStream.DirectAudioFloatInputStream(AudioSystem.getAudioInputStream(var0));
   }

   public static AudioFloatInputStream getInputStream(InputStream var0) throws UnsupportedAudioFileException, IOException {
      return new AudioFloatInputStream.DirectAudioFloatInputStream(AudioSystem.getAudioInputStream(var0));
   }

   public static AudioFloatInputStream getInputStream(AudioInputStream var0) {
      return new AudioFloatInputStream.DirectAudioFloatInputStream(var0);
   }

   public static AudioFloatInputStream getInputStream(AudioFormat var0, byte[] var1, int var2, int var3) {
      AudioFloatConverter var4 = AudioFloatConverter.getConverter(var0);
      if (var4 != null) {
         return new AudioFloatInputStream.BytaArrayAudioFloatInputStream(var4, var1, var2, var3);
      } else {
         ByteArrayInputStream var5 = new ByteArrayInputStream(var1, var2, var3);
         long var6 = var0.getFrameSize() == -1 ? -1L : (long)(var3 / var0.getFrameSize());
         AudioInputStream var8 = new AudioInputStream(var5, var0, var6);
         return getInputStream(var8);
      }
   }

   public abstract AudioFormat getFormat();

   public abstract long getFrameLength();

   public abstract int read(float[] var1, int var2, int var3) throws IOException;

   public final int read(float[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public final float read() throws IOException {
      float[] var1 = new float[1];
      int var2 = this.read(var1, 0, 1);
      return var2 != -1 && var2 != 0 ? var1[0] : 0.0F;
   }

   public abstract long skip(long var1) throws IOException;

   public abstract int available() throws IOException;

   public abstract void close() throws IOException;

   public abstract void mark(int var1);

   public abstract boolean markSupported();

   public abstract void reset() throws IOException;

   private static class DirectAudioFloatInputStream extends AudioFloatInputStream {
      private final AudioInputStream stream;
      private AudioFloatConverter converter;
      private final int framesize_pc;
      private byte[] buffer;

      DirectAudioFloatInputStream(AudioInputStream var1) {
         this.converter = AudioFloatConverter.getConverter(var1.getFormat());
         if (this.converter == null) {
            AudioFormat var2 = var1.getFormat();
            AudioFormat[] var4 = AudioSystem.getTargetFormats(AudioFormat.Encoding.PCM_SIGNED, var2);
            AudioFormat var3;
            if (var4.length != 0) {
               var3 = var4[0];
            } else {
               float var5 = var2.getSampleRate();
               int var6 = var2.getSampleSizeInBits();
               int var7 = var2.getFrameSize();
               float var8 = var2.getFrameRate();
               byte var9 = 16;
               var7 = var2.getChannels() * (var9 / 8);
               var3 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, var5, var9, var2.getChannels(), var7, var5, false);
            }

            var1 = AudioSystem.getAudioInputStream(var3, var1);
            this.converter = AudioFloatConverter.getConverter(var1.getFormat());
         }

         this.framesize_pc = var1.getFormat().getFrameSize() / var1.getFormat().getChannels();
         this.stream = var1;
      }

      public AudioFormat getFormat() {
         return this.stream.getFormat();
      }

      public long getFrameLength() {
         return this.stream.getFrameLength();
      }

      public int read(float[] var1, int var2, int var3) throws IOException {
         int var4 = var3 * this.framesize_pc;
         if (this.buffer == null || this.buffer.length < var4) {
            this.buffer = new byte[var4];
         }

         int var5 = this.stream.read(this.buffer, 0, var4);
         if (var5 == -1) {
            return -1;
         } else {
            this.converter.toFloatArray(this.buffer, var1, var2, var5 / this.framesize_pc);
            return var5 / this.framesize_pc;
         }
      }

      public long skip(long var1) throws IOException {
         long var3 = var1 * (long)this.framesize_pc;
         long var5 = this.stream.skip(var3);
         return var5 == -1L ? -1L : var5 / (long)this.framesize_pc;
      }

      public int available() throws IOException {
         return this.stream.available() / this.framesize_pc;
      }

      public void close() throws IOException {
         this.stream.close();
      }

      public void mark(int var1) {
         this.stream.mark(var1 * this.framesize_pc);
      }

      public boolean markSupported() {
         return this.stream.markSupported();
      }

      public void reset() throws IOException {
         this.stream.reset();
      }
   }

   private static class BytaArrayAudioFloatInputStream extends AudioFloatInputStream {
      private int pos = 0;
      private int markpos = 0;
      private final AudioFloatConverter converter;
      private final AudioFormat format;
      private final byte[] buffer;
      private final int buffer_offset;
      private final int buffer_len;
      private final int framesize_pc;

      BytaArrayAudioFloatInputStream(AudioFloatConverter var1, byte[] var2, int var3, int var4) {
         this.converter = var1;
         this.format = var1.getFormat();
         this.buffer = var2;
         this.buffer_offset = var3;
         this.framesize_pc = this.format.getFrameSize() / this.format.getChannels();
         this.buffer_len = var4 / this.framesize_pc;
      }

      public AudioFormat getFormat() {
         return this.format;
      }

      public long getFrameLength() {
         return (long)this.buffer_len;
      }

      public int read(float[] var1, int var2, int var3) throws IOException {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (var2 >= 0 && var3 >= 0 && var3 <= var1.length - var2) {
            if (this.pos >= this.buffer_len) {
               return -1;
            } else if (var3 == 0) {
               return 0;
            } else {
               if (this.pos + var3 > this.buffer_len) {
                  var3 = this.buffer_len - this.pos;
               }

               this.converter.toFloatArray(this.buffer, this.buffer_offset + this.pos * this.framesize_pc, var1, var2, var3);
               this.pos += var3;
               return var3;
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public long skip(long var1) throws IOException {
         if (this.pos >= this.buffer_len) {
            return -1L;
         } else if (var1 <= 0L) {
            return 0L;
         } else {
            if ((long)this.pos + var1 > (long)this.buffer_len) {
               var1 = (long)(this.buffer_len - this.pos);
            }

            this.pos = (int)((long)this.pos + var1);
            return var1;
         }
      }

      public int available() throws IOException {
         return this.buffer_len - this.pos;
      }

      public void close() throws IOException {
      }

      public void mark(int var1) {
         this.markpos = this.pos;
      }

      public boolean markSupported() {
         return true;
      }

      public void reset() throws IOException {
         this.pos = this.markpos;
      }
   }
}
