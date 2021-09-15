package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

public final class WaveFloatFileReader extends AudioFileReader {
   public AudioFileFormat getAudioFileFormat(InputStream var1) throws UnsupportedAudioFileException, IOException {
      var1.mark(200);

      AudioFileFormat var2;
      try {
         var2 = this.internal_getAudioFileFormat(var1);
      } finally {
         var1.reset();
      }

      return var2;
   }

   private AudioFileFormat internal_getAudioFileFormat(InputStream var1) throws UnsupportedAudioFileException, IOException {
      RIFFReader var2 = new RIFFReader(var1);
      if (!var2.getFormat().equals("RIFF")) {
         throw new UnsupportedAudioFileException();
      } else if (!var2.getType().equals("WAVE")) {
         throw new UnsupportedAudioFileException();
      } else {
         boolean var3 = false;
         boolean var4 = false;
         int var5 = 1;
         long var6 = 1L;
         int var8 = 1;
         int var9 = 1;

         while(var2.hasNextChunk()) {
            RIFFReader var10 = var2.nextChunk();
            if (var10.getFormat().equals("fmt ")) {
               var3 = true;
               int var11 = var10.readUnsignedShort();
               if (var11 != 3) {
                  throw new UnsupportedAudioFileException();
               }

               var5 = var10.readUnsignedShort();
               var6 = var10.readUnsignedInt();
               var10.readUnsignedInt();
               var8 = var10.readUnsignedShort();
               var9 = var10.readUnsignedShort();
            }

            if (var10.getFormat().equals("data")) {
               var4 = true;
               break;
            }
         }

         if (!var3) {
            throw new UnsupportedAudioFileException();
         } else if (!var4) {
            throw new UnsupportedAudioFileException();
         } else {
            AudioFormat var12 = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, (float)var6, var9, var5, var8, (float)var6, false);
            AudioFileFormat var13 = new AudioFileFormat(AudioFileFormat.Type.WAVE, var12, -1);
            return var13;
         }
      }
   }

   public AudioInputStream getAudioInputStream(InputStream var1) throws UnsupportedAudioFileException, IOException {
      AudioFileFormat var2 = this.getAudioFileFormat(var1);
      RIFFReader var3 = new RIFFReader(var1);
      if (!var3.getFormat().equals("RIFF")) {
         throw new UnsupportedAudioFileException();
      } else if (!var3.getType().equals("WAVE")) {
         throw new UnsupportedAudioFileException();
      } else {
         RIFFReader var4;
         do {
            if (!var3.hasNextChunk()) {
               throw new UnsupportedAudioFileException();
            }

            var4 = var3.nextChunk();
         } while(!var4.getFormat().equals("data"));

         return new AudioInputStream(var4, var2.getFormat(), var4.getSize());
      }
   }

   public AudioFileFormat getAudioFileFormat(URL var1) throws UnsupportedAudioFileException, IOException {
      InputStream var2 = var1.openStream();

      AudioFileFormat var3;
      try {
         var3 = this.getAudioFileFormat((InputStream)(new BufferedInputStream(var2)));
      } finally {
         var2.close();
      }

      return var3;
   }

   public AudioFileFormat getAudioFileFormat(File var1) throws UnsupportedAudioFileException, IOException {
      FileInputStream var2 = new FileInputStream(var1);

      AudioFileFormat var3;
      try {
         var3 = this.getAudioFileFormat((InputStream)(new BufferedInputStream(var2)));
      } finally {
         var2.close();
      }

      return var3;
   }

   public AudioInputStream getAudioInputStream(URL var1) throws UnsupportedAudioFileException, IOException {
      return this.getAudioInputStream((InputStream)(new BufferedInputStream(var1.openStream())));
   }

   public AudioInputStream getAudioInputStream(File var1) throws UnsupportedAudioFileException, IOException {
      return this.getAudioInputStream((InputStream)(new BufferedInputStream(new FileInputStream(var1))));
   }
}
