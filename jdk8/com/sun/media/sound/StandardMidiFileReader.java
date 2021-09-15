package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.Sequence;
import javax.sound.midi.spi.MidiFileReader;

public final class StandardMidiFileReader extends MidiFileReader {
   private static final int MThd_MAGIC = 1297377380;
   private static final int bisBufferSize = 1024;

   public MidiFileFormat getMidiFileFormat(InputStream var1) throws InvalidMidiDataException, IOException {
      return this.getMidiFileFormatFromStream(var1, -1, (SMFParser)null);
   }

   private MidiFileFormat getMidiFileFormatFromStream(InputStream var1, int var2, SMFParser var3) throws InvalidMidiDataException, IOException {
      byte var4 = 16;
      byte var5 = -1;
      DataInputStream var6;
      if (var1 instanceof DataInputStream) {
         var6 = (DataInputStream)var1;
      } else {
         var6 = new DataInputStream(var1);
      }

      if (var3 == null) {
         var6.mark(var4);
      } else {
         var3.stream = var6;
      }

      short var7;
      float var9;
      int var10;
      try {
         int var11 = var6.readInt();
         if (var11 != 1297377380) {
            throw new InvalidMidiDataException("not a valid MIDI file");
         }

         int var12 = var6.readInt() - 6;
         var7 = var6.readShort();
         short var8 = var6.readShort();
         short var13 = var6.readShort();
         if (var13 > 0) {
            var9 = 0.0F;
            var10 = var13;
         } else {
            int var14 = -1 * (var13 >> 8);
            switch(var14) {
            case 24:
               var9 = 24.0F;
               break;
            case 25:
               var9 = 25.0F;
               break;
            case 26:
            case 27:
            case 28:
            default:
               throw new InvalidMidiDataException("Unknown frame code: " + var14);
            case 29:
               var9 = 29.97F;
               break;
            case 30:
               var9 = 30.0F;
            }

            var10 = var13 & 255;
         }

         if (var3 != null) {
            var6.skip((long)var12);
            var3.tracks = var8;
         }
      } finally {
         if (var3 == null) {
            var6.reset();
         }

      }

      MidiFileFormat var18 = new MidiFileFormat(var7, var9, var10, var2, (long)var5);
      return var18;
   }

   public MidiFileFormat getMidiFileFormat(URL var1) throws InvalidMidiDataException, IOException {
      InputStream var2 = var1.openStream();
      BufferedInputStream var3 = new BufferedInputStream(var2, 1024);
      MidiFileFormat var4 = null;

      try {
         var4 = this.getMidiFileFormat((InputStream)var3);
      } finally {
         var3.close();
      }

      return var4;
   }

   public MidiFileFormat getMidiFileFormat(File var1) throws InvalidMidiDataException, IOException {
      FileInputStream var2 = new FileInputStream(var1);
      BufferedInputStream var3 = new BufferedInputStream(var2, 1024);
      long var4 = var1.length();
      if (var4 > 2147483647L) {
         var4 = -1L;
      }

      MidiFileFormat var6 = null;

      try {
         var6 = this.getMidiFileFormatFromStream(var3, (int)var4, (SMFParser)null);
      } finally {
         var3.close();
      }

      return var6;
   }

   public Sequence getSequence(InputStream var1) throws InvalidMidiDataException, IOException {
      SMFParser var2 = new SMFParser();
      MidiFileFormat var3 = this.getMidiFileFormatFromStream(var1, -1, var2);
      if (var3.getType() != 0 && var3.getType() != 1) {
         throw new InvalidMidiDataException("Invalid or unsupported file type: " + var3.getType());
      } else {
         Sequence var4 = new Sequence(var3.getDivisionType(), var3.getResolution());

         for(int var5 = 0; var5 < var2.tracks && var2.nextTrack(); ++var5) {
            var2.readTrack(var4.createTrack());
         }

         return var4;
      }
   }

   public Sequence getSequence(URL var1) throws InvalidMidiDataException, IOException {
      InputStream var2 = var1.openStream();
      BufferedInputStream var7 = new BufferedInputStream(var2, 1024);
      Sequence var3 = null;

      try {
         var3 = this.getSequence((InputStream)var7);
      } finally {
         var7.close();
      }

      return var3;
   }

   public Sequence getSequence(File var1) throws InvalidMidiDataException, IOException {
      FileInputStream var2 = new FileInputStream(var1);
      BufferedInputStream var7 = new BufferedInputStream(var2, 1024);
      Sequence var3 = null;

      try {
         var3 = this.getSequence((InputStream)var7);
      } finally {
         var7.close();
      }

      return var3;
   }
}
