package com.sun.media.sound;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

final class WaveFileFormat extends AudioFileFormat {
   private final int waveType;
   private static final int STANDARD_HEADER_SIZE = 28;
   private static final int STANDARD_FMT_CHUNK_SIZE = 16;
   static final int RIFF_MAGIC = 1380533830;
   static final int WAVE_MAGIC = 1463899717;
   static final int FMT_MAGIC = 1718449184;
   static final int DATA_MAGIC = 1684108385;
   static final int WAVE_FORMAT_UNKNOWN = 0;
   static final int WAVE_FORMAT_PCM = 1;
   static final int WAVE_FORMAT_ADPCM = 2;
   static final int WAVE_FORMAT_ALAW = 6;
   static final int WAVE_FORMAT_MULAW = 7;
   static final int WAVE_FORMAT_OKI_ADPCM = 16;
   static final int WAVE_FORMAT_DIGISTD = 21;
   static final int WAVE_FORMAT_DIGIFIX = 22;
   static final int WAVE_IBM_FORMAT_MULAW = 257;
   static final int WAVE_IBM_FORMAT_ALAW = 258;
   static final int WAVE_IBM_FORMAT_ADPCM = 259;
   static final int WAVE_FORMAT_DVI_ADPCM = 17;
   static final int WAVE_FORMAT_SX7383 = 7175;

   WaveFileFormat(AudioFileFormat var1) {
      this(var1.getType(), var1.getByteLength(), var1.getFormat(), var1.getFrameLength());
   }

   WaveFileFormat(AudioFileFormat.Type var1, int var2, AudioFormat var3, int var4) {
      super(var1, var2, var3, var4);
      AudioFormat.Encoding var5 = var3.getEncoding();
      if (var5.equals(AudioFormat.Encoding.ALAW)) {
         this.waveType = 6;
      } else if (var5.equals(AudioFormat.Encoding.ULAW)) {
         this.waveType = 7;
      } else if (!var5.equals(AudioFormat.Encoding.PCM_SIGNED) && !var5.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
         this.waveType = 0;
      } else {
         this.waveType = 1;
      }

   }

   int getWaveType() {
      return this.waveType;
   }

   int getHeaderSize() {
      return getHeaderSize(this.getWaveType());
   }

   static int getHeaderSize(int var0) {
      return 28 + getFmtChunkSize(var0);
   }

   static int getFmtChunkSize(int var0) {
      int var1 = 16;
      if (var0 != 1) {
         var1 += 2;
      }

      return var1;
   }
}
