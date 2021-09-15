package com.sun.media.sound;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

abstract class SunFileReader extends AudioFileReader {
   protected static final int bisBufferSize = 4096;

   public abstract AudioFileFormat getAudioFileFormat(InputStream var1) throws UnsupportedAudioFileException, IOException;

   public abstract AudioFileFormat getAudioFileFormat(URL var1) throws UnsupportedAudioFileException, IOException;

   public abstract AudioFileFormat getAudioFileFormat(File var1) throws UnsupportedAudioFileException, IOException;

   public abstract AudioInputStream getAudioInputStream(InputStream var1) throws UnsupportedAudioFileException, IOException;

   public abstract AudioInputStream getAudioInputStream(URL var1) throws UnsupportedAudioFileException, IOException;

   public abstract AudioInputStream getAudioInputStream(File var1) throws UnsupportedAudioFileException, IOException;

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

   static final int calculatePCMFrameSize(int var0, int var1) {
      return (var0 + 7) / 8 * var1;
   }
}
