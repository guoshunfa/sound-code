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

public final class AuFileWriter extends SunFileWriter {
   public static final int UNKNOWN_SIZE = -1;

   public AuFileWriter() {
      super(new AudioFileFormat.Type[]{AudioFileFormat.Type.AU});
   }

   public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream var1) {
      AudioFileFormat.Type[] var2 = new AudioFileFormat.Type[this.types.length];
      System.arraycopy(this.types, 0, var2, 0, this.types.length);
      AudioFormat var3 = var1.getFormat();
      AudioFormat.Encoding var4 = var3.getEncoding();
      return !AudioFormat.Encoding.ALAW.equals(var4) && !AudioFormat.Encoding.ULAW.equals(var4) && !AudioFormat.Encoding.PCM_SIGNED.equals(var4) && !AudioFormat.Encoding.PCM_UNSIGNED.equals(var4) ? new AudioFileFormat.Type[0] : var2;
   }

   public int write(AudioInputStream var1, AudioFileFormat.Type var2, OutputStream var3) throws IOException {
      AuFileFormat var4 = (AuFileFormat)this.getAudioFileFormat(var2, var1);
      int var5 = this.writeAuFile(var1, var4, var3);
      return var5;
   }

   public int write(AudioInputStream var1, AudioFileFormat.Type var2, File var3) throws IOException {
      AuFileFormat var4 = (AuFileFormat)this.getAudioFileFormat(var2, var1);
      FileOutputStream var5 = new FileOutputStream(var3);
      BufferedOutputStream var6 = new BufferedOutputStream(var5, 4096);
      int var7 = this.writeAuFile(var1, var4, var6);
      var6.close();
      if (var4.getByteLength() == -1) {
         RandomAccessFile var8 = new RandomAccessFile(var3, "rw");
         if (var8.length() <= 2147483647L) {
            var8.skipBytes(8);
            var8.writeInt(var7 - 24);
         }

         var8.close();
      }

      return var7;
   }

   private AudioFileFormat getAudioFileFormat(AudioFileFormat.Type var1, AudioInputStream var2) {
      AudioFormat var3 = null;
      AuFileFormat var4 = null;
      AudioFormat.Encoding var5 = AudioFormat.Encoding.PCM_SIGNED;
      AudioFormat var6 = var2.getFormat();
      AudioFormat.Encoding var7 = var6.getEncoding();
      if (!this.types[0].equals(var1)) {
         throw new IllegalArgumentException("File type " + var1 + " not supported.");
      } else {
         int var9;
         if (!AudioFormat.Encoding.ALAW.equals(var7) && !AudioFormat.Encoding.ULAW.equals(var7)) {
            if (var6.getSampleSizeInBits() == 8) {
               var5 = AudioFormat.Encoding.PCM_SIGNED;
               var9 = 8;
            } else {
               var5 = AudioFormat.Encoding.PCM_SIGNED;
               var9 = var6.getSampleSizeInBits();
            }
         } else {
            var5 = var7;
            var9 = var6.getSampleSizeInBits();
         }

         var3 = new AudioFormat(var5, var6.getSampleRate(), var9, var6.getChannels(), var6.getFrameSize(), var6.getFrameRate(), true);
         int var13;
         if (var2.getFrameLength() != -1L) {
            var13 = (int)var2.getFrameLength() * var6.getFrameSize() + 24;
         } else {
            var13 = -1;
         }

         var4 = new AuFileFormat(AudioFileFormat.Type.AU, var13, var3, (int)var2.getFrameLength());
         return var4;
      }
   }

   private InputStream getFileStream(AuFileFormat var1, InputStream var2) throws IOException {
      AudioFormat var3 = var1.getFormat();
      int var4 = 779316836;
      byte var5 = 24;
      long var6 = (long)var1.getFrameLength();
      long var8 = var6 == -1L ? -1L : var6 * (long)var3.getFrameSize();
      if (var8 > 2147483647L) {
         var8 = -1L;
      }

      int var10 = var1.getAuType();
      int var11 = (int)var3.getSampleRate();
      int var12 = var3.getChannels();
      boolean var13 = true;
      Object var14 = null;
      ByteArrayInputStream var15 = null;
      ByteArrayOutputStream var16 = null;
      DataOutputStream var17 = null;
      SequenceInputStream var18 = null;
      AudioFormat var19 = null;
      AudioFormat.Encoding var20 = null;
      Object var21 = var2;
      if (var2 instanceof AudioInputStream) {
         var19 = ((AudioInputStream)var2).getFormat();
         var20 = var19.getEncoding();
         if (AudioFormat.Encoding.PCM_UNSIGNED.equals(var20) || AudioFormat.Encoding.PCM_SIGNED.equals(var20) && var13 != var19.isBigEndian()) {
            var21 = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, var19.getSampleRate(), var19.getSampleSizeInBits(), var19.getChannels(), var19.getFrameSize(), var19.getFrameRate(), var13), (AudioInputStream)var2);
         }
      }

      var16 = new ByteArrayOutputStream();
      var17 = new DataOutputStream(var16);
      if (var13) {
         var17.writeInt(779316836);
         var17.writeInt(var5);
         var17.writeInt((int)var8);
         var17.writeInt(var10);
         var17.writeInt(var11);
         var17.writeInt(var12);
      } else {
         var17.writeInt(1684960046);
         var17.writeInt(this.big2little(var5));
         var17.writeInt(this.big2little((int)var8));
         var17.writeInt(this.big2little(var10));
         var17.writeInt(this.big2little(var11));
         var17.writeInt(this.big2little(var12));
      }

      var17.close();
      byte[] var22 = var16.toByteArray();
      var15 = new ByteArrayInputStream(var22);
      var18 = new SequenceInputStream(var15, new SunFileWriter.NoCloseInputStream((InputStream)var21));
      return var18;
   }

   private int writeAuFile(InputStream var1, AuFileFormat var2, OutputStream var3) throws IOException {
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
}
