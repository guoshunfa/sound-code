package com.sun.media.sound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AiffFileReader extends SunFileReader {
   private static final int MAX_READ_LENGTH = 8;

   public AudioFileFormat getAudioFileFormat(InputStream var1) throws UnsupportedAudioFileException, IOException {
      AudioFileFormat var2 = this.getCOMM(var1, true);
      var1.reset();
      return var2;
   }

   public AudioFileFormat getAudioFileFormat(URL var1) throws UnsupportedAudioFileException, IOException {
      AudioFileFormat var2 = null;
      InputStream var3 = var1.openStream();

      try {
         var2 = this.getCOMM(var3, false);
      } finally {
         var3.close();
      }

      return var2;
   }

   public AudioFileFormat getAudioFileFormat(File var1) throws UnsupportedAudioFileException, IOException {
      AudioFileFormat var2 = null;
      FileInputStream var3 = new FileInputStream(var1);

      try {
         var2 = this.getCOMM(var3, false);
      } finally {
         var3.close();
      }

      return var2;
   }

   public AudioInputStream getAudioInputStream(InputStream var1) throws UnsupportedAudioFileException, IOException {
      AudioFileFormat var2 = this.getCOMM(var1, true);
      return new AudioInputStream(var1, var2.getFormat(), (long)var2.getFrameLength());
   }

   public AudioInputStream getAudioInputStream(URL var1) throws UnsupportedAudioFileException, IOException {
      InputStream var2 = var1.openStream();
      AudioFileFormat var3 = null;

      try {
         var3 = this.getCOMM(var2, false);
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
         var3 = this.getCOMM(var2, false);
      } finally {
         if (var3 == null) {
            var2.close();
         }

      }

      return new AudioInputStream(var2, var3.getFormat(), (long)var3.getFrameLength());
   }

   private AudioFileFormat getCOMM(InputStream var1, boolean var2) throws UnsupportedAudioFileException, IOException {
      DataInputStream var3 = new DataInputStream(var1);
      if (var2) {
         var3.mark(8);
      }

      byte var4 = 0;
      int var5 = 0;
      AudioFormat var6 = null;
      int var7 = var3.readInt();
      if (var7 != 1179603533) {
         if (var2) {
            var3.reset();
         }

         throw new UnsupportedAudioFileException("not an AIFF file");
      } else {
         int var8 = var3.readInt();
         int var9 = var3.readInt();
         int var23 = var4 + 12;
         int var10;
         if (var8 <= 0) {
            var8 = -1;
            var10 = -1;
         } else {
            var10 = var8 + 8;
         }

         boolean var11 = false;
         if (var9 == 1095321155) {
            var11 = true;
         }

         boolean var12 = false;

         while(!var12) {
            int var13 = var3.readInt();
            int var14 = var3.readInt();
            var23 += 8;
            int var15 = 0;
            int var16;
            switch(var13) {
            case 1129270605:
               if (!var11 && var14 < 18 || var11 && var14 < 22) {
                  throw new UnsupportedAudioFileException("Invalid AIFF/COMM chunksize");
               }

               var16 = var3.readUnsignedShort();
               if (var16 <= 0) {
                  throw new UnsupportedAudioFileException("Invalid number of channels");
               }

               var3.readInt();
               int var17 = var3.readUnsignedShort();
               if (var17 < 1 || var17 > 32) {
                  throw new UnsupportedAudioFileException("Invalid AIFF/COMM sampleSize");
               }

               float var18 = (float)this.read_ieee_extended(var3);
               var15 += 18;
               AudioFormat.Encoding var19 = AudioFormat.Encoding.PCM_SIGNED;
               int var20;
               if (var11) {
                  var20 = var3.readInt();
                  var15 += 4;
                  switch(var20) {
                  case 1313820229:
                     var19 = AudioFormat.Encoding.PCM_SIGNED;
                     break;
                  case 1970037111:
                     var19 = AudioFormat.Encoding.ULAW;
                     var17 = 8;
                     break;
                  default:
                     throw new UnsupportedAudioFileException("Invalid AIFF encoding");
                  }
               }

               var20 = calculatePCMFrameSize(var17, var16);
               var6 = new AudioFormat(var19, var18, var17, var16, var20, var18, true);
            case 1180058962:
            default:
               break;
            case 1397968452:
               int var21 = var3.readInt();
               int var22 = var3.readInt();
               var15 += 8;
               if (var14 < var8) {
                  var5 = var14 - var15;
               } else {
                  var5 = var8 - (var23 + var15);
               }

               var12 = true;
            }

            var23 += var15;
            if (!var12) {
               var16 = var14 - var15;
               if (var16 > 0) {
                  var23 += var3.skipBytes(var16);
               }
            }
         }

         if (var6 == null) {
            throw new UnsupportedAudioFileException("missing COMM chunk");
         } else {
            AudioFileFormat.Type var24 = var11 ? AudioFileFormat.Type.AIFC : AudioFileFormat.Type.AIFF;
            return new AiffFileFormat(var24, var10, var6, var5 / var6.getFrameSize());
         }
      }
   }

   private void write_ieee_extended(DataOutputStream var1, double var2) throws IOException {
      int var4 = 16398;

      double var5;
      for(var5 = var2; var5 < 44000.0D; --var4) {
         var5 *= 2.0D;
      }

      var1.writeShort(var4);
      var1.writeInt((int)var5 << 16);
      var1.writeInt(0);
   }

   private double read_ieee_extended(DataInputStream var1) throws IOException {
      double var2 = 0.0D;
      boolean var4 = false;
      long var5 = 0L;
      long var7 = 0L;
      double var13 = 3.4028234663852886E38D;
      int var15 = var1.readUnsignedShort();
      long var9 = (long)var1.readUnsignedShort();
      long var11 = (long)var1.readUnsignedShort();
      var5 = var9 << 16 | var11;
      var9 = (long)var1.readUnsignedShort();
      var11 = (long)var1.readUnsignedShort();
      var7 = var9 << 16 | var11;
      if (var15 == 0 && var5 == 0L && var7 == 0L) {
         var2 = 0.0D;
      } else if (var15 == 32767) {
         var2 = var13;
      } else {
         var15 -= 16383;
         var15 -= 31;
         var2 = (double)var5 * Math.pow(2.0D, (double)var15);
         var15 -= 32;
         var2 += (double)var7 * Math.pow(2.0D, (double)var15);
      }

      return var2;
   }
}
