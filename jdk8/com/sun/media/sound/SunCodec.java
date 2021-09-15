package com.sun.media.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.FormatConversionProvider;

abstract class SunCodec extends FormatConversionProvider {
   private final AudioFormat.Encoding[] inputEncodings;
   private final AudioFormat.Encoding[] outputEncodings;

   SunCodec(AudioFormat.Encoding[] var1, AudioFormat.Encoding[] var2) {
      this.inputEncodings = var1;
      this.outputEncodings = var2;
   }

   public final AudioFormat.Encoding[] getSourceEncodings() {
      AudioFormat.Encoding[] var1 = new AudioFormat.Encoding[this.inputEncodings.length];
      System.arraycopy(this.inputEncodings, 0, var1, 0, this.inputEncodings.length);
      return var1;
   }

   public final AudioFormat.Encoding[] getTargetEncodings() {
      AudioFormat.Encoding[] var1 = new AudioFormat.Encoding[this.outputEncodings.length];
      System.arraycopy(this.outputEncodings, 0, var1, 0, this.outputEncodings.length);
      return var1;
   }

   public abstract AudioFormat.Encoding[] getTargetEncodings(AudioFormat var1);

   public abstract AudioFormat[] getTargetFormats(AudioFormat.Encoding var1, AudioFormat var2);

   public abstract AudioInputStream getAudioInputStream(AudioFormat.Encoding var1, AudioInputStream var2);

   public abstract AudioInputStream getAudioInputStream(AudioFormat var1, AudioInputStream var2);
}
