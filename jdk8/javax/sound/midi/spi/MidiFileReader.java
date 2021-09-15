package javax.sound.midi.spi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.Sequence;

public abstract class MidiFileReader {
   public abstract MidiFileFormat getMidiFileFormat(InputStream var1) throws InvalidMidiDataException, IOException;

   public abstract MidiFileFormat getMidiFileFormat(URL var1) throws InvalidMidiDataException, IOException;

   public abstract MidiFileFormat getMidiFileFormat(File var1) throws InvalidMidiDataException, IOException;

   public abstract Sequence getSequence(InputStream var1) throws InvalidMidiDataException, IOException;

   public abstract Sequence getSequence(URL var1) throws InvalidMidiDataException, IOException;

   public abstract Sequence getSequence(File var1) throws InvalidMidiDataException, IOException;
}
