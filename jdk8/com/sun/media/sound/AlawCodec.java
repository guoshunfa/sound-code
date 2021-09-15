package com.sun.media.sound;

import java.io.IOException;
import java.util.Vector;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public final class AlawCodec extends SunCodec {
   private static final byte[] ALAW_TABH = new byte[256];
   private static final byte[] ALAW_TABL = new byte[256];
   private static final AudioFormat.Encoding[] alawEncodings;
   private static final short[] seg_end;

   public AlawCodec() {
      super(alawEncodings, alawEncodings);
   }

   public AudioFormat.Encoding[] getTargetEncodings(AudioFormat var1) {
      AudioFormat.Encoding[] var2;
      if (var1.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
         if (var1.getSampleSizeInBits() == 16) {
            var2 = new AudioFormat.Encoding[]{AudioFormat.Encoding.ALAW};
            return var2;
         } else {
            return new AudioFormat.Encoding[0];
         }
      } else if (var1.getEncoding().equals(AudioFormat.Encoding.ALAW)) {
         if (var1.getSampleSizeInBits() == 8) {
            var2 = new AudioFormat.Encoding[]{AudioFormat.Encoding.PCM_SIGNED};
            return var2;
         } else {
            return new AudioFormat.Encoding[0];
         }
      } else {
         return new AudioFormat.Encoding[0];
      }
   }

   public AudioFormat[] getTargetFormats(AudioFormat.Encoding var1, AudioFormat var2) {
      return (!var1.equals(AudioFormat.Encoding.PCM_SIGNED) || !var2.getEncoding().equals(AudioFormat.Encoding.ALAW)) && (!var1.equals(AudioFormat.Encoding.ALAW) || !var2.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) ? new AudioFormat[0] : this.getOutputFormats(var2);
   }

   public AudioInputStream getAudioInputStream(AudioFormat.Encoding var1, AudioInputStream var2) {
      AudioFormat var3 = var2.getFormat();
      AudioFormat.Encoding var4 = var3.getEncoding();
      if (var4.equals(var1)) {
         return var2;
      } else {
         AudioFormat var5 = null;
         if (!this.isConversionSupported(var1, var2.getFormat())) {
            throw new IllegalArgumentException("Unsupported conversion: " + var2.getFormat().toString() + " to " + var1.toString());
         } else {
            if (var4.equals(AudioFormat.Encoding.ALAW) && var1.equals(AudioFormat.Encoding.PCM_SIGNED)) {
               var5 = new AudioFormat(var1, var3.getSampleRate(), 16, var3.getChannels(), 2 * var3.getChannels(), var3.getSampleRate(), var3.isBigEndian());
            } else {
               if (!var4.equals(AudioFormat.Encoding.PCM_SIGNED) || !var1.equals(AudioFormat.Encoding.ALAW)) {
                  throw new IllegalArgumentException("Unsupported conversion: " + var2.getFormat().toString() + " to " + var1.toString());
               }

               var5 = new AudioFormat(var1, var3.getSampleRate(), 8, var3.getChannels(), var3.getChannels(), var3.getSampleRate(), false);
            }

            return this.getAudioInputStream(var5, var2);
         }
      }
   }

   public AudioInputStream getAudioInputStream(AudioFormat var1, AudioInputStream var2) {
      return this.getConvertedStream(var1, var2);
   }

   private AudioInputStream getConvertedStream(AudioFormat var1, AudioInputStream var2) {
      Object var3 = null;
      AudioFormat var4 = var2.getFormat();
      if (var4.matches(var1)) {
         var3 = var2;
      } else {
         var3 = new AlawCodec.AlawCodecStream(var2, var1);
      }

      return (AudioInputStream)var3;
   }

   private AudioFormat[] getOutputFormats(AudioFormat var1) {
      Vector var2 = new Vector();
      AudioFormat var3;
      if (AudioFormat.Encoding.PCM_SIGNED.equals(var1.getEncoding())) {
         var3 = new AudioFormat(AudioFormat.Encoding.ALAW, var1.getSampleRate(), 8, var1.getChannels(), var1.getChannels(), var1.getSampleRate(), false);
         var2.addElement(var3);
      }

      if (AudioFormat.Encoding.ALAW.equals(var1.getEncoding())) {
         var3 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, var1.getSampleRate(), 16, var1.getChannels(), var1.getChannels() * 2, var1.getSampleRate(), false);
         var2.addElement(var3);
         var3 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, var1.getSampleRate(), 16, var1.getChannels(), var1.getChannels() * 2, var1.getSampleRate(), true);
         var2.addElement(var3);
      }

      AudioFormat[] var4 = new AudioFormat[var2.size()];

      for(int var5 = 0; var5 < var4.length; ++var5) {
         var4[var5] = (AudioFormat)((AudioFormat)var2.elementAt(var5));
      }

      return var4;
   }

   static {
      alawEncodings = new AudioFormat.Encoding[]{AudioFormat.Encoding.ALAW, AudioFormat.Encoding.PCM_SIGNED};
      seg_end = new short[]{255, 511, 1023, 2047, 4095, 8191, 16383, 32767};

      for(int var0 = 0; var0 < 256; ++var0) {
         int var1 = var0 ^ 85;
         int var2 = (var1 & 15) << 4;
         int var3 = (var1 & 112) >> 4;
         int var4 = var2 + 8;
         if (var3 >= 1) {
            var4 += 256;
         }

         if (var3 > 1) {
            var4 <<= var3 - 1;
         }

         if ((var1 & 128) == 0) {
            var4 = -var4;
         }

         ALAW_TABL[var0] = (byte)var4;
         ALAW_TABH[var0] = (byte)(var4 >> 8);
      }

   }

   final class AlawCodecStream extends AudioInputStream {
      private static final int tempBufferSize = 64;
      private byte[] tempBuffer = null;
      boolean encode = false;
      AudioFormat encodeFormat;
      AudioFormat decodeFormat;
      byte[] tabByte1 = null;
      byte[] tabByte2 = null;
      int highByte = 0;
      int lowByte = 1;

      AlawCodecStream(AudioInputStream var2, AudioFormat var3) {
         super(var2, var3, -1L);
         AudioFormat var4 = var2.getFormat();
         if (!AlawCodec.this.isConversionSupported(var3, var4)) {
            throw new IllegalArgumentException("Unsupported conversion: " + var4.toString() + " to " + var3.toString());
         } else {
            boolean var5;
            if (AudioFormat.Encoding.ALAW.equals(var4.getEncoding())) {
               this.encode = false;
               this.encodeFormat = var4;
               this.decodeFormat = var3;
               var5 = var3.isBigEndian();
            } else {
               this.encode = true;
               this.encodeFormat = var3;
               this.decodeFormat = var4;
               var5 = var4.isBigEndian();
               this.tempBuffer = new byte[64];
            }

            if (var5) {
               this.tabByte1 = AlawCodec.ALAW_TABH;
               this.tabByte2 = AlawCodec.ALAW_TABL;
               this.highByte = 0;
               this.lowByte = 1;
            } else {
               this.tabByte1 = AlawCodec.ALAW_TABL;
               this.tabByte2 = AlawCodec.ALAW_TABH;
               this.highByte = 1;
               this.lowByte = 0;
            }

            if (var2 instanceof AudioInputStream) {
               this.frameLength = var2.getFrameLength();
            }

            this.framePos = 0L;
            this.frameSize = var4.getFrameSize();
            if (this.frameSize == -1) {
               this.frameSize = 1;
            }

         }
      }

      private short search(short var1, short[] var2, short var3) {
         for(short var4 = 0; var4 < var3; ++var4) {
            if (var1 <= var2[var4]) {
               return var4;
            }
         }

         return var3;
      }

      public int read() throws IOException {
         byte[] var1 = new byte[1];
         return this.read(var1, 0, var1.length);
      }

      public int read(byte[] var1) throws IOException {
         return this.read(var1, 0, var1.length);
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         if (var3 % this.frameSize != 0) {
            var3 -= var3 % this.frameSize;
         }

         if (this.encode) {
            byte var16 = 15;
            byte var17 = 4;
            boolean var12 = false;
            int var13 = var2;
            int var14 = var3 * 2;

            int var20;
            for(int var15 = var14 > 64 ? 64 : var14; (var20 = super.read(this.tempBuffer, 0, var15)) > 0; var15 = var14 > 64 ? 64 : var14) {
               for(int var9 = 0; var9 < var20; var9 += 2) {
                  short var10 = (short)(this.tempBuffer[var9 + this.highByte] << 8 & '\uff00');
                  var10 |= (short)(this.tempBuffer[var9 + this.lowByte] & 255);
                  short var18;
                  if (var10 >= 0) {
                     var18 = 213;
                  } else {
                     var18 = 85;
                     var10 = (short)(-var10 - 8);
                  }

                  short var19 = this.search(var10, AlawCodec.seg_end, (short)8);
                  byte var11;
                  if (var19 >= 8) {
                     var11 = (byte)(127 ^ var18);
                  } else {
                     var11 = (byte)(var19 << var17);
                     if (var19 < 2) {
                        var11 |= (byte)(var10 >> 4 & var16);
                     } else {
                        var11 |= (byte)(var10 >> var19 + 3 & var16);
                     }

                     var11 = (byte)(var11 ^ var18);
                  }

                  var1[var13] = var11;
                  ++var13;
               }

               var14 -= var20;
            }

            if (var13 == var2 && var20 < 0) {
               return var20;
            } else {
               return var13 - var2;
            }
         } else {
            int var5 = var3 / 2;
            int var6 = var2 + var3 / 2;
            int var7 = super.read(var1, var6, var5);

            int var4;
            for(var4 = var2; var4 < var2 + var7 * 2; var4 += 2) {
               var1[var4] = this.tabByte1[var1[var6] & 255];
               var1[var4 + 1] = this.tabByte2[var1[var6] & 255];
               ++var6;
            }

            return var7 < 0 ? var7 : var4 - var2;
         }
      }
   }
}
