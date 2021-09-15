package javax.sound.sampled.spi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

public abstract class AudioFileReader {
   public abstract AudioFileFormat getAudioFileFormat(InputStream var1) throws UnsupportedAudioFileException, IOException;

   public abstract AudioFileFormat getAudioFileFormat(URL var1) throws UnsupportedAudioFileException, IOException;

   public abstract AudioFileFormat getAudioFileFormat(File var1) throws UnsupportedAudioFileException, IOException;

   public abstract AudioInputStream getAudioInputStream(InputStream var1) throws UnsupportedAudioFileException, IOException;

   public abstract AudioInputStream getAudioInputStream(URL var1) throws UnsupportedAudioFileException, IOException;

   public abstract AudioInputStream getAudioInputStream(File var1) throws UnsupportedAudioFileException, IOException;
}
