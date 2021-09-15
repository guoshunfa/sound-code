package javax.sound.sampled.spi;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;

public abstract class AudioFileWriter {
   public abstract AudioFileFormat.Type[] getAudioFileTypes();

   public boolean isFileTypeSupported(AudioFileFormat.Type var1) {
      AudioFileFormat.Type[] var2 = this.getAudioFileTypes();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var1.equals(var2[var3])) {
            return true;
         }
      }

      return false;
   }

   public abstract AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream var1);

   public boolean isFileTypeSupported(AudioFileFormat.Type var1, AudioInputStream var2) {
      AudioFileFormat.Type[] var3 = this.getAudioFileTypes(var2);

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var1.equals(var3[var4])) {
            return true;
         }
      }

      return false;
   }

   public abstract int write(AudioInputStream var1, AudioFileFormat.Type var2, OutputStream var3) throws IOException;

   public abstract int write(AudioInputStream var1, AudioFileFormat.Type var2, File var3) throws IOException;
}
