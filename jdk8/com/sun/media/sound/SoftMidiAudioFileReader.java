package com.sun.media.sound;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

public final class SoftMidiAudioFileReader extends AudioFileReader {
   public static final AudioFileFormat.Type MIDI = new AudioFileFormat.Type("MIDI", "mid");
   private static AudioFormat format = new AudioFormat(44100.0F, 16, 2, true, false);

   public AudioFileFormat getAudioFileFormat(Sequence var1) throws UnsupportedAudioFileException, IOException {
      long var2 = var1.getMicrosecondLength() / 1000000L;
      long var4 = (long)(format.getFrameRate() * (float)(var2 + 4L));
      return new AudioFileFormat(MIDI, format, (int)var4);
   }

   public AudioInputStream getAudioInputStream(Sequence var1) throws UnsupportedAudioFileException, IOException {
      SoftSynthesizer var2 = new SoftSynthesizer();

      AudioInputStream var3;
      Receiver var4;
      try {
         var3 = var2.openStream(format, (Map)null);
         var4 = var2.getReceiver();
      } catch (MidiUnavailableException var20) {
         throw new IOException(var20.toString());
      }

      float var5 = var1.getDivisionType();
      Track[] var6 = var1.getTracks();
      int[] var7 = new int[var6.length];
      int var8 = 500000;
      int var9 = var1.getResolution();
      long var10 = 0L;
      long var12 = 0L;

      while(true) {
         MidiEvent var14 = null;
         int var15 = -1;

         for(int var16 = 0; var16 < var6.length; ++var16) {
            int var17 = var7[var16];
            Track var18 = var6[var16];
            if (var17 < var18.size()) {
               MidiEvent var19 = var18.get(var17);
               if (var14 == null || var19.getTick() < var14.getTick()) {
                  var14 = var19;
                  var15 = var16;
               }
            }
         }

         long var22;
         if (var15 == -1) {
            long var21 = var12 / 1000000L;
            var22 = (long)(var3.getFormat().getFrameRate() * (float)(var21 + 4L));
            var3 = new AudioInputStream(var3, var3.getFormat(), var22);
            return var3;
         }

         int var10002 = var7[var15]++;
         var22 = var14.getTick();
         if (var5 == 0.0F) {
            var12 += (var22 - var10) * (long)var8 / (long)var9;
         } else {
            var12 = (long)((double)var22 * 1000000.0D * (double)var5 / (double)var9);
         }

         var10 = var22;
         MidiMessage var23 = var14.getMessage();
         if (var23 instanceof MetaMessage) {
            if (var5 == 0.0F && ((MetaMessage)var23).getType() == 81) {
               byte[] var24 = ((MetaMessage)var23).getData();
               if (var24.length < 3) {
                  throw new UnsupportedAudioFileException();
               }

               var8 = (var24[0] & 255) << 16 | (var24[1] & 255) << 8 | var24[2] & 255;
            }
         } else {
            var4.send(var23, var12);
         }
      }
   }

   public AudioInputStream getAudioInputStream(InputStream var1) throws UnsupportedAudioFileException, IOException {
      var1.mark(200);

      Sequence var2;
      try {
         var2 = MidiSystem.getSequence(var1);
      } catch (InvalidMidiDataException var4) {
         var1.reset();
         throw new UnsupportedAudioFileException();
      } catch (IOException var5) {
         var1.reset();
         throw new UnsupportedAudioFileException();
      }

      return this.getAudioInputStream(var2);
   }

   public AudioFileFormat getAudioFileFormat(URL var1) throws UnsupportedAudioFileException, IOException {
      Sequence var2;
      try {
         var2 = MidiSystem.getSequence(var1);
      } catch (InvalidMidiDataException var4) {
         throw new UnsupportedAudioFileException();
      } catch (IOException var5) {
         throw new UnsupportedAudioFileException();
      }

      return this.getAudioFileFormat(var2);
   }

   public AudioFileFormat getAudioFileFormat(File var1) throws UnsupportedAudioFileException, IOException {
      Sequence var2;
      try {
         var2 = MidiSystem.getSequence(var1);
      } catch (InvalidMidiDataException var4) {
         throw new UnsupportedAudioFileException();
      } catch (IOException var5) {
         throw new UnsupportedAudioFileException();
      }

      return this.getAudioFileFormat(var2);
   }

   public AudioInputStream getAudioInputStream(URL var1) throws UnsupportedAudioFileException, IOException {
      Sequence var2;
      try {
         var2 = MidiSystem.getSequence(var1);
      } catch (InvalidMidiDataException var4) {
         throw new UnsupportedAudioFileException();
      } catch (IOException var5) {
         throw new UnsupportedAudioFileException();
      }

      return this.getAudioInputStream(var2);
   }

   public AudioInputStream getAudioInputStream(File var1) throws UnsupportedAudioFileException, IOException {
      if (!var1.getName().toLowerCase().endsWith(".mid")) {
         throw new UnsupportedAudioFileException();
      } else {
         Sequence var2;
         try {
            var2 = MidiSystem.getSequence(var1);
         } catch (InvalidMidiDataException var4) {
            throw new UnsupportedAudioFileException();
         } catch (IOException var5) {
            throw new UnsupportedAudioFileException();
         }

         return this.getAudioInputStream(var2);
      }
   }

   public AudioFileFormat getAudioFileFormat(InputStream var1) throws UnsupportedAudioFileException, IOException {
      var1.mark(200);

      Sequence var2;
      try {
         var2 = MidiSystem.getSequence(var1);
      } catch (InvalidMidiDataException var4) {
         var1.reset();
         throw new UnsupportedAudioFileException();
      } catch (IOException var5) {
         var1.reset();
         throw new UnsupportedAudioFileException();
      }

      return this.getAudioFileFormat(var2);
   }
}
