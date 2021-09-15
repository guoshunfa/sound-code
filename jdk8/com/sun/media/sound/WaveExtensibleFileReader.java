package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

public final class WaveExtensibleFileReader extends AudioFileReader {
   private static final String[] channelnames = new String[]{"FL", "FR", "FC", "LF", "BL", "BR", "FLC", "FLR", "BC", "SL", "SR", "TC", "TFL", "TFC", "TFR", "TBL", "TBC", "TBR"};
   private static final String[] allchannelnames = new String[]{"w1", "w2", "w3", "w4", "w5", "w6", "w7", "w8", "w9", "w10", "w11", "w12", "w13", "w14", "w15", "w16", "w17", "w18", "w19", "w20", "w21", "w22", "w23", "w24", "w25", "w26", "w27", "w28", "w29", "w30", "w31", "w32", "w33", "w34", "w35", "w36", "w37", "w38", "w39", "w40", "w41", "w42", "w43", "w44", "w45", "w46", "w47", "w48", "w49", "w50", "w51", "w52", "w53", "w54", "w55", "w56", "w57", "w58", "w59", "w60", "w61", "w62", "w63", "w64"};
   private static final WaveExtensibleFileReader.GUID SUBTYPE_PCM = new WaveExtensibleFileReader.GUID(1L, 0, 16, 128, 0, 0, 170, 0, 56, 155, 113);
   private static final WaveExtensibleFileReader.GUID SUBTYPE_IEEE_FLOAT = new WaveExtensibleFileReader.GUID(3L, 0, 16, 128, 0, 0, 170, 0, 56, 155, 113);

   private String decodeChannelMask(long var1) {
      StringBuffer var3 = new StringBuffer();
      long var4 = 1L;

      for(int var6 = 0; var6 < allchannelnames.length; ++var6) {
         if ((var1 & var4) != 0L) {
            if (var6 < channelnames.length) {
               var3.append(channelnames[var6] + " ");
            } else {
               var3.append(allchannelnames[var6] + " ");
            }
         }

         var4 *= 2L;
      }

      if (var3.length() == 0) {
         return null;
      } else {
         return var3.substring(0, var3.length() - 1);
      }
   }

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
         int var10 = 1;
         long var11 = 0L;
         WaveExtensibleFileReader.GUID var13 = null;

         while(var2.hasNextChunk()) {
            RIFFReader var14 = var2.nextChunk();
            if (var14.getFormat().equals("fmt ")) {
               var3 = true;
               int var15 = var14.readUnsignedShort();
               if (var15 != 65534) {
                  throw new UnsupportedAudioFileException();
               }

               var5 = var14.readUnsignedShort();
               var6 = var14.readUnsignedInt();
               var14.readUnsignedInt();
               var8 = var14.readUnsignedShort();
               var9 = var14.readUnsignedShort();
               int var16 = var14.readUnsignedShort();
               if (var16 != 22) {
                  throw new UnsupportedAudioFileException();
               }

               var10 = var14.readUnsignedShort();
               if (var10 > var9) {
                  throw new UnsupportedAudioFileException();
               }

               var11 = var14.readUnsignedInt();
               var13 = WaveExtensibleFileReader.GUID.read(var14);
            }

            if (var14.getFormat().equals("data")) {
               var4 = true;
               break;
            }
         }

         if (!var3) {
            throw new UnsupportedAudioFileException();
         } else if (!var4) {
            throw new UnsupportedAudioFileException();
         } else {
            HashMap var18 = new HashMap();
            String var19 = this.decodeChannelMask(var11);
            if (var19 != null) {
               var18.put("channelOrder", var19);
            }

            if (var11 != 0L) {
               var18.put("channelMask", var11);
            }

            var18.put("validBitsPerSample", var10);
            AudioFormat var20 = null;
            if (var13.equals(SUBTYPE_PCM)) {
               if (var9 == 8) {
                  var20 = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, (float)var6, var9, var5, var8, (float)var6, false, var18);
               } else {
                  var20 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float)var6, var9, var5, var8, (float)var6, false, var18);
               }
            } else {
               if (!var13.equals(SUBTYPE_IEEE_FLOAT)) {
                  throw new UnsupportedAudioFileException();
               }

               var20 = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, (float)var6, var9, var5, var8, (float)var6, false, var18);
            }

            AudioFileFormat var17 = new AudioFileFormat(AudioFileFormat.Type.WAVE, var20, -1);
            return var17;
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

   private static class GUID {
      long i1;
      int s1;
      int s2;
      int x1;
      int x2;
      int x3;
      int x4;
      int x5;
      int x6;
      int x7;
      int x8;

      private GUID() {
      }

      GUID(long var1, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
         this.i1 = var1;
         this.s1 = var3;
         this.s2 = var4;
         this.x1 = var5;
         this.x2 = var6;
         this.x3 = var7;
         this.x4 = var8;
         this.x5 = var9;
         this.x6 = var10;
         this.x7 = var11;
         this.x8 = var12;
      }

      public static WaveExtensibleFileReader.GUID read(RIFFReader var0) throws IOException {
         WaveExtensibleFileReader.GUID var1 = new WaveExtensibleFileReader.GUID();
         var1.i1 = var0.readUnsignedInt();
         var1.s1 = var0.readUnsignedShort();
         var1.s2 = var0.readUnsignedShort();
         var1.x1 = var0.readUnsignedByte();
         var1.x2 = var0.readUnsignedByte();
         var1.x3 = var0.readUnsignedByte();
         var1.x4 = var0.readUnsignedByte();
         var1.x5 = var0.readUnsignedByte();
         var1.x6 = var0.readUnsignedByte();
         var1.x7 = var0.readUnsignedByte();
         var1.x8 = var0.readUnsignedByte();
         return var1;
      }

      public int hashCode() {
         return (int)this.i1;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof WaveExtensibleFileReader.GUID)) {
            return false;
         } else {
            WaveExtensibleFileReader.GUID var2 = (WaveExtensibleFileReader.GUID)var1;
            if (this.i1 != var2.i1) {
               return false;
            } else if (this.s1 != var2.s1) {
               return false;
            } else if (this.s2 != var2.s2) {
               return false;
            } else if (this.x1 != var2.x1) {
               return false;
            } else if (this.x2 != var2.x2) {
               return false;
            } else if (this.x3 != var2.x3) {
               return false;
            } else if (this.x4 != var2.x4) {
               return false;
            } else if (this.x5 != var2.x5) {
               return false;
            } else if (this.x6 != var2.x6) {
               return false;
            } else if (this.x7 != var2.x7) {
               return false;
            } else {
               return this.x8 == var2.x8;
            }
         }
      }
   }
}
