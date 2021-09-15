package com.sun.media.sound;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.spi.SoundbankReader;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AudioFileSoundbankReader extends SoundbankReader {
   public Soundbank getSoundbank(URL var1) throws InvalidMidiDataException, IOException {
      try {
         AudioInputStream var2 = AudioSystem.getAudioInputStream(var1);
         Soundbank var3 = this.getSoundbank(var2);
         var2.close();
         return var3;
      } catch (UnsupportedAudioFileException var4) {
         return null;
      } catch (IOException var5) {
         return null;
      }
   }

   public Soundbank getSoundbank(InputStream var1) throws InvalidMidiDataException, IOException {
      var1.mark(512);

      try {
         AudioInputStream var2 = AudioSystem.getAudioInputStream(var1);
         Soundbank var3 = this.getSoundbank(var2);
         if (var3 != null) {
            return var3;
         }
      } catch (UnsupportedAudioFileException var4) {
      } catch (IOException var5) {
      }

      var1.reset();
      return null;
   }

   public Soundbank getSoundbank(AudioInputStream var1) throws InvalidMidiDataException, IOException {
      try {
         byte[] var2;
         if (var1.getFrameLength() == -1L) {
            ByteArrayOutputStream var3 = new ByteArrayOutputStream();
            byte[] var4 = new byte[1024 - 1024 % var1.getFormat().getFrameSize()];

            int var5;
            while((var5 = var1.read(var4)) != -1) {
               var3.write(var4, 0, var5);
            }

            var1.close();
            var2 = var3.toByteArray();
         } else {
            var2 = new byte[(int)(var1.getFrameLength() * (long)var1.getFormat().getFrameSize())];
            (new DataInputStream(var1)).readFully(var2);
         }

         ModelByteBufferWavetable var8 = new ModelByteBufferWavetable(new ModelByteBuffer(var2), var1.getFormat(), -4800.0F);
         ModelPerformer var9 = new ModelPerformer();
         var9.getOscillators().add(var8);
         SimpleSoundbank var10 = new SimpleSoundbank();
         SimpleInstrument var6 = new SimpleInstrument();
         var6.add(var9);
         var10.addInstrument(var6);
         return var10;
      } catch (Exception var7) {
         return null;
      }
   }

   public Soundbank getSoundbank(File var1) throws InvalidMidiDataException, IOException {
      try {
         AudioInputStream var2 = AudioSystem.getAudioInputStream(var1);
         var2.close();
         ModelByteBufferWavetable var3 = new ModelByteBufferWavetable(new ModelByteBuffer(var1, 0L, var1.length()), -4800.0F);
         ModelPerformer var4 = new ModelPerformer();
         var4.getOscillators().add(var3);
         SimpleSoundbank var5 = new SimpleSoundbank();
         SimpleInstrument var6 = new SimpleInstrument();
         var6.add(var4);
         var5.addInstrument(var6);
         return var5;
      } catch (UnsupportedAudioFileException var7) {
         return null;
      } catch (IOException var8) {
         return null;
      }
   }
}
