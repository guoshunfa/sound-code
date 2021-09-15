package com.sun.media.sound;

import java.io.IOException;
import java.util.Vector;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public final class UlawCodec extends SunCodec {
   private static final byte[] ULAW_TABH = new byte[256];
   private static final byte[] ULAW_TABL = new byte[256];
   private static final AudioFormat.Encoding[] ulawEncodings;
   private static final short[] seg_end;

   public UlawCodec() {
      super(ulawEncodings, ulawEncodings);
   }

   public AudioFormat.Encoding[] getTargetEncodings(AudioFormat var1) {
      AudioFormat.Encoding[] var2;
      if (AudioFormat.Encoding.PCM_SIGNED.equals(var1.getEncoding())) {
         if (var1.getSampleSizeInBits() == 16) {
            var2 = new AudioFormat.Encoding[]{AudioFormat.Encoding.ULAW};
            return var2;
         } else {
            return new AudioFormat.Encoding[0];
         }
      } else if (AudioFormat.Encoding.ULAW.equals(var1.getEncoding())) {
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
      return (!AudioFormat.Encoding.PCM_SIGNED.equals(var1) || !AudioFormat.Encoding.ULAW.equals(var2.getEncoding())) && (!AudioFormat.Encoding.ULAW.equals(var1) || !AudioFormat.Encoding.PCM_SIGNED.equals(var2.getEncoding())) ? new AudioFormat[0] : this.getOutputFormats(var2);
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
            if (AudioFormat.Encoding.ULAW.equals(var4) && AudioFormat.Encoding.PCM_SIGNED.equals(var1)) {
               var5 = new AudioFormat(var1, var3.getSampleRate(), 16, var3.getChannels(), 2 * var3.getChannels(), var3.getSampleRate(), var3.isBigEndian());
            } else {
               if (!AudioFormat.Encoding.PCM_SIGNED.equals(var4) || !AudioFormat.Encoding.ULAW.equals(var1)) {
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
         var3 = new UlawCodec.UlawCodecStream(var2, var1);
      }

      return (AudioInputStream)var3;
   }

   private AudioFormat[] getOutputFormats(AudioFormat var1) {
      Vector var2 = new Vector();
      AudioFormat var3;
      if (var1.getSampleSizeInBits() == 16 && AudioFormat.Encoding.PCM_SIGNED.equals(var1.getEncoding())) {
         var3 = new AudioFormat(AudioFormat.Encoding.ULAW, var1.getSampleRate(), 8, var1.getChannels(), var1.getChannels(), var1.getSampleRate(), false);
         var2.addElement(var3);
      }

      if (AudioFormat.Encoding.ULAW.equals(var1.getEncoding())) {
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
      ulawEncodings = new AudioFormat.Encoding[]{AudioFormat.Encoding.ULAW, AudioFormat.Encoding.PCM_SIGNED};
      seg_end = new short[]{255, 511, 1023, 2047, 4095, 8191, 16383, 32767};

      for(int var0 = 0; var0 < 256; ++var0) {
         int var1 = ~var0;
         var1 &= 255;
         int var2 = ((var1 & 15) << 3) + 132;
         var2 <<= (var1 & 112) >> 4;
         var2 = (var1 & 128) != 0 ? 132 - var2 : var2 - 132;
         ULAW_TABL[var0] = (byte)(var2 & 255);
         ULAW_TABH[var0] = (byte)(var2 >> 8 & 255);
      }

   }

   class UlawCodecStream extends AudioInputStream {
      private static final int tempBufferSize = 64;
      private byte[] tempBuffer = null;
      boolean encode = false;
      AudioFormat encodeFormat;
      AudioFormat decodeFormat;
      byte[] tabByte1 = null;
      byte[] tabByte2 = null;
      int highByte = 0;
      int lowByte = 1;

      UlawCodecStream(AudioInputStream var2, AudioFormat var3) {
         super(var2, var3, -1L);
         AudioFormat var4 = var2.getFormat();
         if (!UlawCodec.this.isConversionSupported(var3, var4)) {
            throw new IllegalArgumentException("Unsupported conversion: " + var4.toString() + " to " + var3.toString());
         } else {
            boolean var5;
            if (AudioFormat.Encoding.ULAW.equals(var4.getEncoding())) {
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
               this.tabByte1 = UlawCodec.ULAW_TABH;
               this.tabByte2 = UlawCodec.ULAW_TABL;
               this.highByte = 0;
               this.lowByte = 1;
            } else {
               this.tabByte1 = UlawCodec.ULAW_TABL;
               this.tabByte2 = UlawCodec.ULAW_TABH;
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
         return this.read(var1, 0, var1.length) == 1 ? var1[1] & 255 : -1;
      }

      public int read(byte[] var1) throws IOException {
         return this.read(var1, 0, var1.length);
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         if (var3 % this.frameSize != 0) {
            var3 -= var3 % this.frameSize;
         }

         int var7;
         if (this.encode) {
            short var14 = 132;
            boolean var10 = false;
            int var11 = var2;
            int var12 = var3 * 2;

            int var17;
            for(int var13 = var12 > 64 ? 64 : var12; (var17 = super.read(this.tempBuffer, 0, var13)) > 0; var13 = var12 > 64 ? 64 : var12) {
               for(var7 = 0; var7 < var17; var7 += 2) {
                  short var8 = (short)(this.tempBuffer[var7 + this.highByte] << 8 & '\uff00');
                  var8 |= (short)((short)this.tempBuffer[var7 + this.lowByte] & 255);
                  short var15;
                  if (var8 < 0) {
                     var8 = (short)(var14 - var8);
                     var15 = 127;
                  } else {
                     var8 += var14;
                     var15 = 255;
                  }

                  short var16 = this.search(var8, UlawCodec.seg_end, (short)8);
                  byte var9;
                  if (var16 >= 8) {
                     var9 = (byte)(127 ^ var15);
                  } else {
                     var9 = (byte)(var16 << 4 | var8 >> var16 + 3 & 15);
                     var9 = (byte)(var9 ^ var15);
                  }

                  var1[var11] = var9;
                  ++var11;
               }

               var12 -= var17;
            }

            if (var11 == var2 && var17 < 0) {
               return var17;
            } else {
               return var11 - var2;
            }
         } else {
            int var5 = var3 / 2;
            int var6 = var2 + var3 / 2;
            var7 = super.read(var1, var6, var5);
            if (var7 < 0) {
               return var7;
            } else {
               int var4;
               for(var4 = var2; var4 < var2 + var7 * 2; var4 += 2) {
                  var1[var4] = this.tabByte1[var1[var6] & 255];
                  var1[var4 + 1] = this.tabByte2[var1[var6] & 255];
                  ++var6;
               }

               return var4 - var2;
            }
         }
      }
   }
}
