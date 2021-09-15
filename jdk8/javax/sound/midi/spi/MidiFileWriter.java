package javax.sound.midi.spi;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.midi.Sequence;

public abstract class MidiFileWriter {
   public abstract int[] getMidiFileTypes();

   public abstract int[] getMidiFileTypes(Sequence var1);

   public boolean isFileTypeSupported(int var1) {
      int[] var2 = this.getMidiFileTypes();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var1 == var2[var3]) {
            return true;
         }
      }

      return false;
   }

   public boolean isFileTypeSupported(int var1, Sequence var2) {
      int[] var3 = this.getMidiFileTypes(var2);

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var1 == var3[var4]) {
            return true;
         }
      }

      return false;
   }

   public abstract int write(Sequence var1, int var2, OutputStream var3) throws IOException;

   public abstract int write(Sequence var1, int var2, File var3) throws IOException;
}
