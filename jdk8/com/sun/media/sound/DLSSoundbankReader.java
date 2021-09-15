package com.sun.media.sound;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.spi.SoundbankReader;

public final class DLSSoundbankReader extends SoundbankReader {
   public Soundbank getSoundbank(URL var1) throws InvalidMidiDataException, IOException {
      try {
         return new DLSSoundbank(var1);
      } catch (RIFFInvalidFormatException var3) {
         return null;
      } catch (IOException var4) {
         return null;
      }
   }

   public Soundbank getSoundbank(InputStream var1) throws InvalidMidiDataException, IOException {
      try {
         var1.mark(512);
         return new DLSSoundbank(var1);
      } catch (RIFFInvalidFormatException var3) {
         var1.reset();
         return null;
      }
   }

   public Soundbank getSoundbank(File var1) throws InvalidMidiDataException, IOException {
      try {
         return new DLSSoundbank(var1);
      } catch (RIFFInvalidFormatException var3) {
         return null;
      }
   }
}
