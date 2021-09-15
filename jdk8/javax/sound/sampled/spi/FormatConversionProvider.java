package javax.sound.sampled.spi;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public abstract class FormatConversionProvider {
   public abstract AudioFormat.Encoding[] getSourceEncodings();

   public abstract AudioFormat.Encoding[] getTargetEncodings();

   public boolean isSourceEncodingSupported(AudioFormat.Encoding var1) {
      AudioFormat.Encoding[] var2 = this.getSourceEncodings();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var1.equals(var2[var3])) {
            return true;
         }
      }

      return false;
   }

   public boolean isTargetEncodingSupported(AudioFormat.Encoding var1) {
      AudioFormat.Encoding[] var2 = this.getTargetEncodings();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var1.equals(var2[var3])) {
            return true;
         }
      }

      return false;
   }

   public abstract AudioFormat.Encoding[] getTargetEncodings(AudioFormat var1);

   public boolean isConversionSupported(AudioFormat.Encoding var1, AudioFormat var2) {
      AudioFormat.Encoding[] var3 = this.getTargetEncodings(var2);

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var1.equals(var3[var4])) {
            return true;
         }
      }

      return false;
   }

   public abstract AudioFormat[] getTargetFormats(AudioFormat.Encoding var1, AudioFormat var2);

   public boolean isConversionSupported(AudioFormat var1, AudioFormat var2) {
      AudioFormat[] var3 = this.getTargetFormats(var1.getEncoding(), var2);

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var1.matches(var3[var4])) {
            return true;
         }
      }

      return false;
   }

   public abstract AudioInputStream getAudioInputStream(AudioFormat.Encoding var1, AudioInputStream var2);

   public abstract AudioInputStream getAudioInputStream(AudioFormat var1, AudioInputStream var2);
}
