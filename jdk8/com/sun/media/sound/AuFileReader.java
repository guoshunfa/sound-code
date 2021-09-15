package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AuFileReader extends SunFileReader {
   public AudioFileFormat getAudioFileFormat(InputStream var1) throws UnsupportedAudioFileException, IOException {
      AudioFormat var2 = null;
      AuFileFormat var3 = null;
      byte var4 = 28;
      boolean var5 = false;
      boolean var6 = true;
      boolean var7 = true;
      boolean var8 = true;
      boolean var9 = true;
      boolean var10 = true;
      boolean var11 = true;
      boolean var12 = true;
      boolean var13 = true;
      boolean var15 = false;
      byte var16 = 0;
      AudioFormat.Encoding var17 = null;
      DataInputStream var18 = new DataInputStream(var1);
      var18.mark(var4);
      int var19 = var18.readInt();
      if (var19 == 779316836 && var19 != 779314176 && var19 != 1684960046 && var19 != 6583086) {
         if (var19 == 779316836 || var19 == 779314176) {
            var5 = true;
         }

         int var20 = var5 ? var18.readInt() : this.rllong(var18);
         int var27 = var16 + 4;
         int var21 = var5 ? var18.readInt() : this.rllong(var18);
         var27 += 4;
         int var22 = var5 ? var18.readInt() : this.rllong(var18);
         var27 += 4;
         int var23 = var5 ? var18.readInt() : this.rllong(var18);
         var27 += 4;
         int var25 = var5 ? var18.readInt() : this.rllong(var18);
         var27 += 4;
         if (var25 <= 0) {
            var18.reset();
            throw new UnsupportedAudioFileException("Invalid number of channels");
         } else {
            byte var14;
            switch(var22) {
            case 1:
               var17 = AudioFormat.Encoding.ULAW;
               var14 = 8;
               break;
            case 2:
               var17 = AudioFormat.Encoding.PCM_SIGNED;
               var14 = 8;
               break;
            case 3:
               var17 = AudioFormat.Encoding.PCM_SIGNED;
               var14 = 16;
               break;
            case 4:
               var17 = AudioFormat.Encoding.PCM_SIGNED;
               var14 = 24;
               break;
            case 5:
               var17 = AudioFormat.Encoding.PCM_SIGNED;
               var14 = 32;
               break;
            case 27:
               var17 = AudioFormat.Encoding.ALAW;
               var14 = 8;
               break;
            default:
               var18.reset();
               throw new UnsupportedAudioFileException("not a valid AU file");
            }

            int var24 = calculatePCMFrameSize(var14, var25);
            int var26;
            if (var21 < 0) {
               var26 = -1;
            } else {
               var26 = var21 / var24;
            }

            var2 = new AudioFormat(var17, (float)var23, var14, var25, var24, (float)var23, var5);
            var3 = new AuFileFormat(AudioFileFormat.Type.AU, var21 + var20, var2, var26);
            var18.reset();
            return var3;
         }
      } else {
         var18.reset();
         throw new UnsupportedAudioFileException("not an AU file");
      }
   }

   public AudioFileFormat getAudioFileFormat(URL var1) throws UnsupportedAudioFileException, IOException {
      InputStream var2 = null;
      BufferedInputStream var3 = null;
      AudioFileFormat var4 = null;
      Object var5 = null;
      var2 = var1.openStream();

      try {
         var3 = new BufferedInputStream(var2, 4096);
         var4 = this.getAudioFileFormat((InputStream)var3);
      } finally {
         var2.close();
      }

      return var4;
   }

   public AudioFileFormat getAudioFileFormat(File var1) throws UnsupportedAudioFileException, IOException {
      FileInputStream var2 = null;
      BufferedInputStream var3 = null;
      AudioFileFormat var4 = null;
      Object var5 = null;
      var2 = new FileInputStream(var1);

      try {
         var3 = new BufferedInputStream(var2, 4096);
         var4 = this.getAudioFileFormat((InputStream)var3);
      } finally {
         var2.close();
      }

      return var4;
   }

   public AudioInputStream getAudioInputStream(InputStream var1) throws UnsupportedAudioFileException, IOException {
      DataInputStream var2 = null;
      AudioFileFormat var4 = null;
      AudioFormat var5 = null;
      var4 = this.getAudioFileFormat(var1);
      var5 = var4.getFormat();
      var2 = new DataInputStream(var1);
      var2.readInt();
      int var3 = var5.isBigEndian() ? var2.readInt() : this.rllong(var2);
      var2.skipBytes(var3 - 8);
      return new AudioInputStream(var2, var5, (long)var4.getFrameLength());
   }

   public AudioInputStream getAudioInputStream(URL var1) throws UnsupportedAudioFileException, IOException {
      InputStream var2 = null;
      BufferedInputStream var3 = null;
      Object var4 = null;
      var2 = var1.openStream();
      AudioInputStream var5 = null;

      try {
         var3 = new BufferedInputStream(var2, 4096);
         var5 = this.getAudioInputStream((InputStream)var3);
      } finally {
         if (var5 == null) {
            var2.close();
         }

      }

      return var5;
   }

   public AudioInputStream getAudioInputStream(File var1) throws UnsupportedAudioFileException, IOException {
      FileInputStream var2 = null;
      BufferedInputStream var3 = null;
      Object var4 = null;
      var2 = new FileInputStream(var1);
      AudioInputStream var5 = null;

      try {
         var3 = new BufferedInputStream(var2, 4096);
         var5 = this.getAudioInputStream((InputStream)var3);
      } finally {
         if (var5 == null) {
            var2.close();
         }

      }

      return var5;
   }
}
