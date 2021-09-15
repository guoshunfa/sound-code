package javax.sound.midi.spi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Soundbank;

public abstract class SoundbankReader {
   public abstract Soundbank getSoundbank(URL var1) throws InvalidMidiDataException, IOException;

   public abstract Soundbank getSoundbank(InputStream var1) throws InvalidMidiDataException, IOException;

   public abstract Soundbank getSoundbank(File var1) throws InvalidMidiDataException, IOException;
}
