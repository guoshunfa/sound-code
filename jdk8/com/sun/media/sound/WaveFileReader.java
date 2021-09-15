package com.sun.media.sound;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class WaveFileReader extends SunFileReader {
   private static final int MAX_READ_LENGTH = 12;

   public AudioFileFormat getAudioFileFormat(InputStream var1) throws UnsupportedAudioFileException, IOException {
      AudioFileFormat var2 = this.getFMT(var1, true);
      var1.reset();
      return var2;
   }

   public AudioFileFormat getAudioFileFormat(URL var1) throws UnsupportedAudioFileException, IOException {
      InputStream var2 = var1.openStream();
      AudioFileFormat var3 = null;

      try {
         var3 = this.getFMT(var2, false);
      } finally {
         var2.close();
      }

      return var3;
   }

   public AudioFileFormat getAudioFileFormat(File var1) throws UnsupportedAudioFileException, IOException {
      AudioFileFormat var2 = null;
      FileInputStream var3 = new FileInputStream(var1);

      try {
         var2 = this.getFMT(var3, false);
      } finally {
         var3.close();
      }

      return var2;
   }

   public AudioInputStream getAudioInputStream(InputStream var1) throws UnsupportedAudioFileException, IOException {
      AudioFileFormat var2 = this.getFMT(var1, true);
      return new AudioInputStream(var1, var2.getFormat(), (long)var2.getFrameLength());
   }

   public AudioInputStream getAudioInputStream(URL var1) throws UnsupportedAudioFileException, IOException {
      InputStream var2 = var1.openStream();
      AudioFileFormat var3 = null;

      try {
         var3 = this.getFMT(var2, false);
      } finally {
         if (var3 == null) {
            var2.close();
         }

      }

      return new AudioInputStream(var2, var3.getFormat(), (long)var3.getFrameLength());
   }

   public AudioInputStream getAudioInputStream(File var1) throws UnsupportedAudioFileException, IOException {
      FileInputStream var2 = new FileInputStream(var1);
      AudioFileFormat var3 = null;

      try {
         var3 = this.getFMT(var2, false);
      } finally {
         if (var3 == null) {
            var2.close();
         }

      }

      return new AudioInputStream(var2, var3.getFormat(), (long)var3.getFrameLength());
   }

   private AudioFileFormat getFMT(InputStream var1, boolean var2) throws UnsupportedAudioFileException, IOException {
      int var4 = 0;
      boolean var6 = false;
      boolean var7 = false;
      AudioFormat.Encoding var15 = null;
      DataInputStream var16 = new DataInputStream(var1);
      if (var2) {
         var16.mark(12);
      }

      int var17 = var16.readInt();
      int var18 = this.rllong(var16);
      int var19 = var16.readInt();
      int var20;
      if (var18 <= 0) {
         boolean var28 = true;
         var20 = -1;
      } else {
         var20 = var18 + 8;
      }

      if (var17 == 1380533830 && var19 == 1463899717) {
         int var26;
         while(true) {
            try {
               int var5 = var16.readInt();
               var4 += 4;
               if (var5 == 1718449184) {
                  break;
               }

               var26 = this.rllong(var16);
               var4 += 4;
               if (var26 % 2 > 0) {
                  ++var26;
               }

               var4 += var16.skipBytes(var26);
            } catch (EOFException var25) {
               throw new UnsupportedAudioFileException("Not a valid WAV file");
            }
         }

         var26 = this.rllong(var16);
         var4 += 4;
         int var21 = var4 + var26;
         short var27 = this.rlshort(var16);
         var4 += 2;
         if (var27 == 1) {
            var15 = AudioFormat.Encoding.PCM_SIGNED;
         } else if (var27 == 6) {
            var15 = AudioFormat.Encoding.ALAW;
         } else {
            if (var27 != 7) {
               throw new UnsupportedAudioFileException("Not a supported WAV file");
            }

            var15 = AudioFormat.Encoding.ULAW;
         }

         short var8 = this.rlshort(var16);
         var4 += 2;
         if (var8 <= 0) {
            throw new UnsupportedAudioFileException("Invalid number of channels");
         } else {
            long var9 = (long)this.rllong(var16);
            var4 += 4;
            long var11 = (long)this.rllong(var16);
            var4 += 4;
            this.rlshort(var16);
            var4 += 2;
            short var14 = this.rlshort(var16);
            var4 += 2;
            if (var14 <= 0) {
               throw new UnsupportedAudioFileException("Invalid bitsPerSample");
            } else {
               if (var14 == 8 && var15.equals(AudioFormat.Encoding.PCM_SIGNED)) {
                  var15 = AudioFormat.Encoding.PCM_UNSIGNED;
               }

               if (var26 % 2 != 0) {
                  ++var26;
               }

               if (var21 > var4) {
                  var4 += var16.skipBytes(var21 - var4);
               }

               var4 = 0;

               int var22;
               while(true) {
                  try {
                     var22 = var16.readInt();
                     var4 += 4;
                     if (var22 == 1684108385) {
                        break;
                     }

                     int var23 = this.rllong(var16);
                     var4 += 4;
                     if (var23 % 2 > 0) {
                        ++var23;
                     }

                     var4 += var16.skipBytes(var23);
                  } catch (EOFException var24) {
                     throw new UnsupportedAudioFileException("Not a valid WAV file");
                  }
               }

               var22 = this.rllong(var16);
               var4 += 4;
               AudioFormat var29 = new AudioFormat(var15, (float)var9, var14, var8, calculatePCMFrameSize(var14, var8), (float)var9, false);
               return new WaveFileFormat(AudioFileFormat.Type.WAVE, var20, var29, var22 / var29.getFrameSize());
            }
         }
      } else {
         if (var2) {
            var16.reset();
         }

         throw new UnsupportedAudioFileException("not a WAVE file");
      }
   }
}
