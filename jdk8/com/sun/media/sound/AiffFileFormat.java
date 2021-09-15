package com.sun.media.sound;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

final class AiffFileFormat extends AudioFileFormat {
   static final int AIFF_MAGIC = 1179603533;
   static final int AIFC_MAGIC = 1095321155;
   static final int AIFF_MAGIC2 = 1095321158;
   static final int FVER_MAGIC = 1180058962;
   static final int FVER_TIMESTAMP = -1568648896;
   static final int COMM_MAGIC = 1129270605;
   static final int SSND_MAGIC = 1397968452;
   static final int AIFC_PCM = 1313820229;
   static final int AIFC_ACE2 = 1094927666;
   static final int AIFC_ACE8 = 1094927672;
   static final int AIFC_MAC3 = 1296122675;
   static final int AIFC_MAC6 = 1296122678;
   static final int AIFC_ULAW = 1970037111;
   static final int AIFC_IMA4 = 1768775988;
   static final int AIFF_HEADERSIZE = 54;
   private final int headerSize;
   private final int commChunkSize;
   private final int fverChunkSize;

   AiffFileFormat(AudioFileFormat var1) {
      this(var1.getType(), var1.getByteLength(), var1.getFormat(), var1.getFrameLength());
   }

   AiffFileFormat(AudioFileFormat.Type var1, int var2, AudioFormat var3, int var4) {
      super(var1, var2, var3, var4);
      this.headerSize = 54;
      this.commChunkSize = 26;
      this.fverChunkSize = 0;
   }

   int getHeaderSize() {
      return 54;
   }

   int getCommChunkSize() {
      return 26;
   }

   int getFverChunkSize() {
      return 0;
   }

   int getSsndChunkOffset() {
      return this.getHeaderSize() - 16;
   }
}
