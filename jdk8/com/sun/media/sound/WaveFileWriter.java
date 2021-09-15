package com.sun.media.sound;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.SequenceInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public final class WaveFileWriter extends SunFileWriter {
   static final int RIFF_MAGIC = 1380533830;
   static final int WAVE_MAGIC = 1463899717;
   static final int FMT_MAGIC = 1718449184;
   static final int DATA_MAGIC = 1684108385;
   static final int WAVE_FORMAT_UNKNOWN = 0;
   static final int WAVE_FORMAT_PCM = 1;
   static final int WAVE_FORMAT_ADPCM = 2;
   static final int WAVE_FORMAT_ALAW = 6;
   static final int WAVE_FORMAT_MULAW = 7;
   static final int WAVE_FORMAT_OKI_ADPCM = 16;
   static final int WAVE_FORMAT_DIGISTD = 21;
   static final int WAVE_FORMAT_DIGIFIX = 22;
   static final int WAVE_IBM_FORMAT_MULAW = 257;
   static final int WAVE_IBM_FORMAT_ALAW = 258;
   static final int WAVE_IBM_FORMAT_ADPCM = 259;
   static final int WAVE_FORMAT_DVI_ADPCM = 17;
   static final int WAVE_FORMAT_SX7383 = 7175;

   public WaveFileWriter() {
      super(new AudioFileFormat.Type[]{AudioFileFormat.Type.WAVE});
   }

   public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream var1) {
      AudioFileFormat.Type[] var2 = new AudioFileFormat.Type[this.types.length];
      System.arraycopy(this.types, 0, var2, 0, this.types.length);
      AudioFormat var3 = var1.getFormat();
      AudioFormat.Encoding var4 = var3.getEncoding();
      return !AudioFormat.Encoding.ALAW.equals(var4) && !AudioFormat.Encoding.ULAW.equals(var4) && !AudioFormat.Encoding.PCM_SIGNED.equals(var4) && !AudioFormat.Encoding.PCM_UNSIGNED.equals(var4) ? new AudioFileFormat.Type[0] : var2;
   }

   public int write(AudioInputStream var1, AudioFileFormat.Type var2, OutputStream var3) throws IOException {
      WaveFileFormat var4 = (WaveFileFormat)this.getAudioFileFormat(var2, var1);
      if (var1.getFrameLength() == -1L) {
         throw new IOException("stream length not specified");
      } else {
         int var5 = this.writeWaveFile(var1, var4, var3);
         return var5;
      }
   }

   public int write(AudioInputStream var1, AudioFileFormat.Type var2, File var3) throws IOException {
      WaveFileFormat var4 = (WaveFileFormat)this.getAudioFileFormat(var2, var1);
      FileOutputStream var5 = new FileOutputStream(var3);
      BufferedOutputStream var6 = new BufferedOutputStream(var5, 4096);
      int var7 = this.writeWaveFile(var1, var4, var6);
      var6.close();
      if (var4.getByteLength() == -1) {
         int var8 = var7 - var4.getHeaderSize();
         int var9 = var8 + var4.getHeaderSize() - 8;
         RandomAccessFile var10 = new RandomAccessFile(var3, "rw");
         var10.skipBytes(4);
         var10.writeInt(this.big2little(var9));
         var10.skipBytes(12 + WaveFileFormat.getFmtChunkSize(var4.getWaveType()) + 4);
         var10.writeInt(this.big2little(var8));
         var10.close();
      }

      return var7;
   }

   private AudioFileFormat getAudioFileFormat(AudioFileFormat.Type var1, AudioInputStream var2) {
      AudioFormat var3 = null;
      WaveFileFormat var4 = null;
      AudioFormat.Encoding var5 = AudioFormat.Encoding.PCM_SIGNED;
      AudioFormat var6 = var2.getFormat();
      AudioFormat.Encoding var7 = var6.getEncoding();
      if (!this.types[0].equals(var1)) {
         throw new IllegalArgumentException("File type " + var1 + " not supported.");
      } else {
         byte var14 = 1;
         int var9;
         if (!AudioFormat.Encoding.ALAW.equals(var7) && !AudioFormat.Encoding.ULAW.equals(var7)) {
            if (var6.getSampleSizeInBits() == 8) {
               var5 = AudioFormat.Encoding.PCM_UNSIGNED;
               var9 = 8;
            } else {
               var5 = AudioFormat.Encoding.PCM_SIGNED;
               var9 = var6.getSampleSizeInBits();
            }
         } else {
            var5 = var7;
            var9 = var6.getSampleSizeInBits();
            if (var7.equals(AudioFormat.Encoding.ALAW)) {
               var14 = 6;
            } else {
               var14 = 7;
            }
         }

         var3 = new AudioFormat(var5, var6.getSampleRate(), var9, var6.getChannels(), var6.getFrameSize(), var6.getFrameRate(), false);
         int var13;
         if (var2.getFrameLength() != -1L) {
            var13 = (int)var2.getFrameLength() * var6.getFrameSize() + WaveFileFormat.getHeaderSize(var14);
         } else {
            var13 = -1;
         }

         var4 = new WaveFileFormat(AudioFileFormat.Type.WAVE, var13, var3, (int)var2.getFrameLength());
         return var4;
      }
   }

   private int writeWaveFile(InputStream var1, WaveFileFormat var2, OutputStream var3) throws IOException {
      boolean var4 = false;
      int var5 = 0;
      InputStream var6 = this.getFileStream(var2, var1);
      byte[] var7 = new byte[4096];
      int var8 = var2.getByteLength();

      int var9;
      while((var9 = var6.read(var7)) >= 0) {
         if (var8 > 0) {
            if (var9 >= var8) {
               var3.write(var7, 0, var8);
               var5 += var8;
               boolean var10 = false;
               break;
            }

            var3.write(var7, 0, var9);
            var5 += var9;
            var8 -= var9;
         } else {
            var3.write(var7, 0, var9);
            var5 += var9;
         }
      }

      return var5;
   }

   private InputStream getFileStream(WaveFileFormat var1, InputStream var2) throws IOException {
      AudioFormat var3 = var1.getFormat();
      int var4 = var1.getHeaderSize();
      int var5 = 1380533830;
      int var6 = 1463899717;
      int var7 = 1718449184;
      int var8 = WaveFileFormat.getFmtChunkSize(var1.getWaveType());
      short var9 = (short)var1.getWaveType();
      short var10 = (short)var3.getChannels();
      short var11 = (short)var3.getSampleSizeInBits();
      int var12 = (int)var3.getSampleRate();
      int var13 = var3.getFrameSize();
      int var14 = (int)var3.getFrameRate();
      int var15 = var10 * var11 * var12 / 8;
      short var16 = (short)(var11 / 8 * var10);
      int var17 = 1684108385;
      int var18 = var1.getFrameLength() * var13;
      int var19 = var1.getByteLength();
      int var20 = var18 + var4 - 8;
      Object var21 = null;
      ByteArrayInputStream var22 = null;
      ByteArrayOutputStream var23 = null;
      DataOutputStream var24 = null;
      SequenceInputStream var25 = null;
      AudioFormat var26 = null;
      AudioFormat.Encoding var27 = null;
      Object var28 = var2;
      if (var2 instanceof AudioInputStream) {
         var26 = ((AudioInputStream)var2).getFormat();
         var27 = var26.getEncoding();
         if (AudioFormat.Encoding.PCM_SIGNED.equals(var27) && var11 == 8) {
            var9 = 1;
            var28 = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, var26.getSampleRate(), var26.getSampleSizeInBits(), var26.getChannels(), var26.getFrameSize(), var26.getFrameRate(), false), (AudioInputStream)var2);
         }

         if ((AudioFormat.Encoding.PCM_SIGNED.equals(var27) && var26.isBigEndian() || AudioFormat.Encoding.PCM_UNSIGNED.equals(var27) && !var26.isBigEndian() || AudioFormat.Encoding.PCM_UNSIGNED.equals(var27) && var26.isBigEndian()) && var11 != 8) {
            var9 = 1;
            var28 = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, var26.getSampleRate(), var26.getSampleSizeInBits(), var26.getChannels(), var26.getFrameSize(), var26.getFrameRate(), false), (AudioInputStream)var2);
         }
      }

      var23 = new ByteArrayOutputStream();
      var24 = new DataOutputStream(var23);
      var24.writeInt(var5);
      var24.writeInt(this.big2little(var20));
      var24.writeInt(var6);
      var24.writeInt(var7);
      var24.writeInt(this.big2little(var8));
      var24.writeShort(this.big2littleShort(var9));
      var24.writeShort(this.big2littleShort(var10));
      var24.writeInt(this.big2little(var12));
      var24.writeInt(this.big2little(var15));
      var24.writeShort(this.big2littleShort(var16));
      var24.writeShort(this.big2littleShort(var11));
      if (var9 != 1) {
         var24.writeShort(0);
      }

      var24.writeInt(var17);
      var24.writeInt(this.big2little(var18));
      var24.close();
      byte[] var29 = var23.toByteArray();
      var22 = new ByteArrayInputStream(var29);
      var25 = new SequenceInputStream(var22, new SunFileWriter.NoCloseInputStream((InputStream)var28));
      return var25;
   }
}
