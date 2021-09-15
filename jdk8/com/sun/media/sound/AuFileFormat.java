package com.sun.media.sound;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

final class AuFileFormat extends AudioFileFormat {
   static final int AU_SUN_MAGIC = 779316836;
   static final int AU_SUN_INV_MAGIC = 1684960046;
   static final int AU_DEC_MAGIC = 779314176;
   static final int AU_DEC_INV_MAGIC = 6583086;
   static final int AU_ULAW_8 = 1;
   static final int AU_LINEAR_8 = 2;
   static final int AU_LINEAR_16 = 3;
   static final int AU_LINEAR_24 = 4;
   static final int AU_LINEAR_32 = 5;
   static final int AU_FLOAT = 6;
   static final int AU_DOUBLE = 7;
   static final int AU_ADPCM_G721 = 23;
   static final int AU_ADPCM_G722 = 24;
   static final int AU_ADPCM_G723_3 = 25;
   static final int AU_ADPCM_G723_5 = 26;
   static final int AU_ALAW_8 = 27;
   static final int AU_HEADERSIZE = 24;
   private int auType;

   AuFileFormat(AudioFileFormat var1) {
      this(var1.getType(), var1.getByteLength(), var1.getFormat(), var1.getFrameLength());
   }

   AuFileFormat(AudioFileFormat.Type var1, int var2, AudioFormat var3, int var4) {
      super(var1, var2, var3, var4);
      AudioFormat.Encoding var5 = var3.getEncoding();
      this.auType = -1;
      if (AudioFormat.Encoding.ALAW.equals(var5)) {
         if (var3.getSampleSizeInBits() == 8) {
            this.auType = 27;
         }
      } else if (AudioFormat.Encoding.ULAW.equals(var5)) {
         if (var3.getSampleSizeInBits() == 8) {
            this.auType = 1;
         }
      } else if (AudioFormat.Encoding.PCM_SIGNED.equals(var5)) {
         if (var3.getSampleSizeInBits() == 8) {
            this.auType = 2;
         } else if (var3.getSampleSizeInBits() == 16) {
            this.auType = 3;
         } else if (var3.getSampleSizeInBits() == 24) {
            this.auType = 4;
         } else if (var3.getSampleSizeInBits() == 32) {
            this.auType = 5;
         }
      }

   }

   public int getAuType() {
      return this.auType;
   }
}
