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

public final class AiffFileWriter extends SunFileWriter {
   private static final int DOUBLE_MANTISSA_LENGTH = 52;
   private static final int DOUBLE_EXPONENT_LENGTH = 11;
   private static final long DOUBLE_SIGN_MASK = Long.MIN_VALUE;
   private static final long DOUBLE_EXPONENT_MASK = 9218868437227405312L;
   private static final long DOUBLE_MANTISSA_MASK = 4503599627370495L;
   private static final int DOUBLE_EXPONENT_OFFSET = 1023;
   private static final int EXTENDED_EXPONENT_OFFSET = 16383;
   private static final int EXTENDED_MANTISSA_LENGTH = 63;
   private static final int EXTENDED_EXPONENT_LENGTH = 15;
   private static final long EXTENDED_INTEGER_MASK = Long.MIN_VALUE;

   public AiffFileWriter() {
      super(new AudioFileFormat.Type[]{AudioFileFormat.Type.AIFF});
   }

   public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream var1) {
      AudioFileFormat.Type[] var2 = new AudioFileFormat.Type[this.types.length];
      System.arraycopy(this.types, 0, var2, 0, this.types.length);
      AudioFormat var3 = var1.getFormat();
      AudioFormat.Encoding var4 = var3.getEncoding();
      return !AudioFormat.Encoding.ALAW.equals(var4) && !AudioFormat.Encoding.ULAW.equals(var4) && !AudioFormat.Encoding.PCM_SIGNED.equals(var4) && !AudioFormat.Encoding.PCM_UNSIGNED.equals(var4) ? new AudioFileFormat.Type[0] : var2;
   }

   public int write(AudioInputStream var1, AudioFileFormat.Type var2, OutputStream var3) throws IOException {
      AiffFileFormat var4 = (AiffFileFormat)this.getAudioFileFormat(var2, var1);
      if (var1.getFrameLength() == -1L) {
         throw new IOException("stream length not specified");
      } else {
         int var5 = this.writeAiffFile(var1, var4, var3);
         return var5;
      }
   }

   public int write(AudioInputStream var1, AudioFileFormat.Type var2, File var3) throws IOException {
      AiffFileFormat var4 = (AiffFileFormat)this.getAudioFileFormat(var2, var1);
      FileOutputStream var5 = new FileOutputStream(var3);
      BufferedOutputStream var6 = new BufferedOutputStream(var5, 4096);
      int var7 = this.writeAiffFile(var1, var4, var6);
      var6.close();
      if (var4.getByteLength() == -1) {
         int var8 = var4.getFormat().getChannels() * var4.getFormat().getSampleSizeInBits();
         int var10 = var7 - var4.getHeaderSize() + 16;
         long var11 = (long)(var10 - 16);
         int var13 = (int)(var11 * 8L / (long)var8);
         RandomAccessFile var14 = new RandomAccessFile(var3, "rw");
         var14.skipBytes(4);
         var14.writeInt(var7 - 8);
         var14.skipBytes(4 + var4.getFverChunkSize() + 4 + 4 + 2);
         var14.writeInt(var13);
         var14.skipBytes(16);
         var14.writeInt(var10 - 8);
         var14.close();
      }

      return var7;
   }

   private AudioFileFormat getAudioFileFormat(AudioFileFormat.Type var1, AudioInputStream var2) {
      AudioFormat var3 = null;
      AiffFileFormat var4 = null;
      AudioFormat.Encoding var5 = AudioFormat.Encoding.PCM_SIGNED;
      AudioFormat var6 = var2.getFormat();
      AudioFormat.Encoding var7 = var6.getEncoding();
      boolean var14 = false;
      if (!this.types[0].equals(var1)) {
         throw new IllegalArgumentException("File type " + var1 + " not supported.");
      } else {
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
            if (var6.getSampleSizeInBits() != 8) {
               throw new IllegalArgumentException("Encoding " + var7 + " supported only for 8-bit data.");
            }

            var5 = AudioFormat.Encoding.PCM_SIGNED;
            var9 = 16;
            var14 = true;
         }

         var3 = new AudioFormat(var5, var6.getSampleRate(), var9, var6.getChannels(), var6.getFrameSize(), var6.getFrameRate(), true);
         int var13;
         if (var2.getFrameLength() != -1L) {
            if (var14) {
               var13 = (int)var2.getFrameLength() * var6.getFrameSize() * 2 + 54;
            } else {
               var13 = (int)var2.getFrameLength() * var6.getFrameSize() + 54;
            }
         } else {
            var13 = -1;
         }

         var4 = new AiffFileFormat(AudioFileFormat.Type.AIFF, var13, var3, (int)var2.getFrameLength());
         return var4;
      }
   }

   private int writeAiffFile(InputStream var1, AiffFileFormat var2, OutputStream var3) throws IOException {
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

   private InputStream getFileStream(AiffFileFormat var1, InputStream var2) throws IOException {
      AudioFormat var3 = var1.getFormat();
      AudioFormat var4 = null;
      AudioFormat.Encoding var5 = null;
      int var6 = var1.getHeaderSize();
      int var7 = var1.getFverChunkSize();
      int var8 = var1.getCommChunkSize();
      int var9 = -1;
      int var10 = -1;
      int var11 = var1.getSsndChunkOffset();
      short var12 = (short)var3.getChannels();
      short var13 = (short)var3.getSampleSizeInBits();
      int var14 = var12 * var13;
      int var15 = var1.getFrameLength();
      long var16 = -1L;
      if (var15 != -1) {
         var16 = (long)var15 * (long)var14 / 8L;
         var10 = (int)var16 + 16;
         var9 = (int)var16 + var6;
      }

      float var18 = var3.getSampleRate();
      int var19 = 1313820229;
      Object var20 = null;
      ByteArrayInputStream var21 = null;
      ByteArrayOutputStream var22 = null;
      DataOutputStream var23 = null;
      SequenceInputStream var24 = null;
      Object var25 = var2;
      if (var2 instanceof AudioInputStream) {
         var4 = ((AudioInputStream)var2).getFormat();
         var5 = var4.getEncoding();
         if (!AudioFormat.Encoding.PCM_UNSIGNED.equals(var5) && (!AudioFormat.Encoding.PCM_SIGNED.equals(var5) || var4.isBigEndian())) {
            if (AudioFormat.Encoding.ULAW.equals(var5) || AudioFormat.Encoding.ALAW.equals(var5)) {
               if (var4.getSampleSizeInBits() != 8) {
                  throw new IllegalArgumentException("unsupported encoding");
               }

               var25 = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, var4.getSampleRate(), var4.getSampleSizeInBits() * 2, var4.getChannels(), var4.getFrameSize() * 2, var4.getFrameRate(), true), (AudioInputStream)var2);
            }
         } else {
            var25 = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, var4.getSampleRate(), var4.getSampleSizeInBits(), var4.getChannels(), var4.getFrameSize(), var4.getFrameRate(), true), (AudioInputStream)var2);
         }
      }

      var22 = new ByteArrayOutputStream();
      var23 = new DataOutputStream(var22);
      var23.writeInt(1179603533);
      var23.writeInt(var9 - 8);
      var23.writeInt(1095321158);
      var23.writeInt(1129270605);
      var23.writeInt(var8 - 8);
      var23.writeShort(var12);
      var23.writeInt(var15);
      var23.writeShort(var13);
      this.write_ieee_extended(var23, var18);
      var23.writeInt(1397968452);
      var23.writeInt(var10 - 8);
      var23.writeInt(0);
      var23.writeInt(0);
      var23.close();
      byte[] var26 = var22.toByteArray();
      var21 = new ByteArrayInputStream(var26);
      var24 = new SequenceInputStream(var21, new SunFileWriter.NoCloseInputStream((InputStream)var25));
      return var24;
   }

   private void write_ieee_extended(DataOutputStream var1, float var2) throws IOException {
      long var3 = Double.doubleToLongBits((double)var2);
      long var5 = (var3 & Long.MIN_VALUE) >> 63;
      long var7 = (var3 & 9218868437227405312L) >> 52;
      long var9 = var3 & 4503599627370495L;
      long var11 = var7 - 1023L + 16383L;
      long var13 = var9 << 11;
      long var15 = var5 << 15;
      short var17 = (short)((int)(var15 | var11));
      long var18 = Long.MIN_VALUE | var13;
      var1.writeShort(var17);
      var1.writeLong(var18);
   }
}
