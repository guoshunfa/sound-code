package com.sun.media.sound;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.AudioFileWriter;

abstract class SunFileWriter extends AudioFileWriter {
   protected static final int bufferSize = 16384;
   protected static final int bisBufferSize = 4096;
   final AudioFileFormat.Type[] types;

   SunFileWriter(AudioFileFormat.Type[] var1) {
      this.types = var1;
   }

   public final AudioFileFormat.Type[] getAudioFileTypes() {
      AudioFileFormat.Type[] var1 = new AudioFileFormat.Type[this.types.length];
      System.arraycopy(this.types, 0, var1, 0, this.types.length);
      return var1;
   }

   public abstract AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream var1);

   public abstract int write(AudioInputStream var1, AudioFileFormat.Type var2, OutputStream var3) throws IOException;

   public abstract int write(AudioInputStream var1, AudioFileFormat.Type var2, File var3) throws IOException;

   final int rllong(DataInputStream var1) throws IOException {
      boolean var6 = false;
      int var7 = var1.readInt();
      int var2 = (var7 & 255) << 24;
      int var3 = (var7 & '\uff00') << 8;
      int var4 = (var7 & 16711680) >> 8;
      int var5 = (var7 & -16777216) >>> 24;
      var7 = var2 | var3 | var4 | var5;
      return var7;
   }

   final int big2little(int var1) {
      int var2 = (var1 & 255) << 24;
      int var3 = (var1 & '\uff00') << 8;
      int var4 = (var1 & 16711680) >> 8;
      int var5 = (var1 & -16777216) >>> 24;
      var1 = var2 | var3 | var4 | var5;
      return var1;
   }

   final short rlshort(DataInputStream var1) throws IOException {
      boolean var2 = false;
      short var5 = var1.readShort();
      short var3 = (short)((var5 & 255) << 8);
      short var4 = (short)((var5 & '\uff00') >>> 8);
      var5 = (short)(var3 | var4);
      return var5;
   }

   final short big2littleShort(short var1) {
      short var2 = (short)((var1 & 255) << 8);
      short var3 = (short)((var1 & '\uff00') >>> 8);
      var1 = (short)(var2 | var3);
      return var1;
   }

   final class NoCloseInputStream extends InputStream {
      private final InputStream in;

      NoCloseInputStream(InputStream var2) {
         this.in = var2;
      }

      public int read() throws IOException {
         return this.in.read();
      }

      public int read(byte[] var1) throws IOException {
         return this.in.read(var1);
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         return this.in.read(var1, var2, var3);
      }

      public long skip(long var1) throws IOException {
         return this.in.skip(var1);
      }

      public int available() throws IOException {
         return this.in.available();
      }

      public void close() throws IOException {
      }

      public void mark(int var1) {
         this.in.mark(var1);
      }

      public void reset() throws IOException {
         this.in.reset();
      }

      public boolean markSupported() {
         return this.in.markSupported();
      }
   }
}
