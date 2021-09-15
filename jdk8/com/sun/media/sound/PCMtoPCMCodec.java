package com.sun.media.sound;

import java.io.IOException;
import java.util.Vector;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public final class PCMtoPCMCodec extends SunCodec {
   private static final AudioFormat.Encoding[] inputEncodings;
   private static final AudioFormat.Encoding[] outputEncodings;
   private static final int tempBufferSize = 64;
   private byte[] tempBuffer = null;

   public PCMtoPCMCodec() {
      super(inputEncodings, outputEncodings);
   }

   public AudioFormat.Encoding[] getTargetEncodings(AudioFormat var1) {
      if (!var1.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && !var1.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
         return new AudioFormat.Encoding[0];
      } else {
         AudioFormat.Encoding[] var2 = new AudioFormat.Encoding[]{AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED};
         return var2;
      }
   }

   public AudioFormat[] getTargetFormats(AudioFormat.Encoding var1, AudioFormat var2) {
      AudioFormat[] var3 = this.getOutputFormats(var2);
      Vector var4 = new Vector();

      for(int var5 = 0; var5 < var3.length; ++var5) {
         if (var3[var5].getEncoding().equals(var1)) {
            var4.addElement(var3[var5]);
         }
      }

      AudioFormat[] var7 = new AudioFormat[var4.size()];

      for(int var6 = 0; var6 < var7.length; ++var6) {
         var7[var6] = (AudioFormat)((AudioFormat)var4.elementAt(var6));
      }

      return var7;
   }

   public AudioInputStream getAudioInputStream(AudioFormat.Encoding var1, AudioInputStream var2) {
      if (this.isConversionSupported(var1, var2.getFormat())) {
         AudioFormat var3 = var2.getFormat();
         AudioFormat var4 = new AudioFormat(var1, var3.getSampleRate(), var3.getSampleSizeInBits(), var3.getChannels(), var3.getFrameSize(), var3.getFrameRate(), var3.isBigEndian());
         return this.getAudioInputStream(var4, var2);
      } else {
         throw new IllegalArgumentException("Unsupported conversion: " + var2.getFormat().toString() + " to " + var1.toString());
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
         var3 = new PCMtoPCMCodec.PCMtoPCMCodecStream(var2, var1);
         this.tempBuffer = new byte[64];
      }

      return (AudioInputStream)var3;
   }

   private AudioFormat[] getOutputFormats(AudioFormat var1) {
      Vector var2 = new Vector();
      int var4 = var1.getSampleSizeInBits();
      boolean var5 = var1.isBigEndian();
      AudioFormat var3;
      if (var4 == 8) {
         if (AudioFormat.Encoding.PCM_SIGNED.equals(var1.getEncoding())) {
            var3 = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, var1.getSampleRate(), var1.getSampleSizeInBits(), var1.getChannels(), var1.getFrameSize(), var1.getFrameRate(), false);
            var2.addElement(var3);
         }

         if (AudioFormat.Encoding.PCM_UNSIGNED.equals(var1.getEncoding())) {
            var3 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, var1.getSampleRate(), var1.getSampleSizeInBits(), var1.getChannels(), var1.getFrameSize(), var1.getFrameRate(), false);
            var2.addElement(var3);
         }
      } else if (var4 == 16) {
         if (AudioFormat.Encoding.PCM_SIGNED.equals(var1.getEncoding()) && var5) {
            var3 = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, var1.getSampleRate(), var1.getSampleSizeInBits(), var1.getChannels(), var1.getFrameSize(), var1.getFrameRate(), true);
            var2.addElement(var3);
            var3 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, var1.getSampleRate(), var1.getSampleSizeInBits(), var1.getChannels(), var1.getFrameSize(), var1.getFrameRate(), false);
            var2.addElement(var3);
            var3 = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, var1.getSampleRate(), var1.getSampleSizeInBits(), var1.getChannels(), var1.getFrameSize(), var1.getFrameRate(), false);
            var2.addElement(var3);
         }

         if (AudioFormat.Encoding.PCM_UNSIGNED.equals(var1.getEncoding()) && var5) {
            var3 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, var1.getSampleRate(), var1.getSampleSizeInBits(), var1.getChannels(), var1.getFrameSize(), var1.getFrameRate(), true);
            var2.addElement(var3);
            var3 = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, var1.getSampleRate(), var1.getSampleSizeInBits(), var1.getChannels(), var1.getFrameSize(), var1.getFrameRate(), false);
            var2.addElement(var3);
            var3 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, var1.getSampleRate(), var1.getSampleSizeInBits(), var1.getChannels(), var1.getFrameSize(), var1.getFrameRate(), false);
            var2.addElement(var3);
         }

         if (AudioFormat.Encoding.PCM_SIGNED.equals(var1.getEncoding()) && !var5) {
            var3 = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, var1.getSampleRate(), var1.getSampleSizeInBits(), var1.getChannels(), var1.getFrameSize(), var1.getFrameRate(), false);
            var2.addElement(var3);
            var3 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, var1.getSampleRate(), var1.getSampleSizeInBits(), var1.getChannels(), var1.getFrameSize(), var1.getFrameRate(), true);
            var2.addElement(var3);
            var3 = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, var1.getSampleRate(), var1.getSampleSizeInBits(), var1.getChannels(), var1.getFrameSize(), var1.getFrameRate(), true);
            var2.addElement(var3);
         }

         if (AudioFormat.Encoding.PCM_UNSIGNED.equals(var1.getEncoding()) && !var5) {
            var3 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, var1.getSampleRate(), var1.getSampleSizeInBits(), var1.getChannels(), var1.getFrameSize(), var1.getFrameRate(), false);
            var2.addElement(var3);
            var3 = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, var1.getSampleRate(), var1.getSampleSizeInBits(), var1.getChannels(), var1.getFrameSize(), var1.getFrameRate(), true);
            var2.addElement(var3);
            var3 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, var1.getSampleRate(), var1.getSampleSizeInBits(), var1.getChannels(), var1.getFrameSize(), var1.getFrameRate(), true);
            var2.addElement(var3);
         }
      }

      synchronized(var2) {
         AudioFormat[] var6 = new AudioFormat[var2.size()];

         for(int var8 = 0; var8 < var6.length; ++var8) {
            var6[var8] = (AudioFormat)((AudioFormat)var2.elementAt(var8));
         }

         return var6;
      }
   }

   static {
      inputEncodings = new AudioFormat.Encoding[]{AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED};
      outputEncodings = new AudioFormat.Encoding[]{AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED};
   }

   class PCMtoPCMCodecStream extends AudioInputStream {
      private final int PCM_SWITCH_SIGNED_8BIT = 1;
      private final int PCM_SWITCH_ENDIAN = 2;
      private final int PCM_SWITCH_SIGNED_LE = 3;
      private final int PCM_SWITCH_SIGNED_BE = 4;
      private final int PCM_UNSIGNED_LE2SIGNED_BE = 5;
      private final int PCM_SIGNED_LE2UNSIGNED_BE = 6;
      private final int PCM_UNSIGNED_BE2SIGNED_LE = 7;
      private final int PCM_SIGNED_BE2UNSIGNED_LE = 8;
      private final int sampleSizeInBytes;
      private int conversionType = 0;

      PCMtoPCMCodecStream(AudioInputStream var2, AudioFormat var3) {
         super(var2, var3, -1L);
         boolean var4 = false;
         AudioFormat.Encoding var5 = null;
         AudioFormat.Encoding var6 = null;
         AudioFormat var9 = var2.getFormat();
         if (!PCMtoPCMCodec.this.isConversionSupported(var9, var3)) {
            throw new IllegalArgumentException("Unsupported conversion: " + var9.toString() + " to " + var3.toString());
         } else {
            var5 = var9.getEncoding();
            var6 = var3.getEncoding();
            boolean var7 = var9.isBigEndian();
            boolean var8 = var3.isBigEndian();
            int var10 = var9.getSampleSizeInBits();
            this.sampleSizeInBytes = var10 / 8;
            if (var10 == 8) {
               if (AudioFormat.Encoding.PCM_UNSIGNED.equals(var5) && AudioFormat.Encoding.PCM_SIGNED.equals(var6)) {
                  this.conversionType = 1;
               } else if (AudioFormat.Encoding.PCM_SIGNED.equals(var5) && AudioFormat.Encoding.PCM_UNSIGNED.equals(var6)) {
                  this.conversionType = 1;
               }
            } else if (var5.equals(var6) && var7 != var8) {
               this.conversionType = 2;
            } else if (AudioFormat.Encoding.PCM_UNSIGNED.equals(var5) && !var7 && AudioFormat.Encoding.PCM_SIGNED.equals(var6) && var8) {
               this.conversionType = 5;
            } else if (AudioFormat.Encoding.PCM_SIGNED.equals(var5) && !var7 && AudioFormat.Encoding.PCM_UNSIGNED.equals(var6) && var8) {
               this.conversionType = 6;
            } else if (AudioFormat.Encoding.PCM_UNSIGNED.equals(var5) && var7 && AudioFormat.Encoding.PCM_SIGNED.equals(var6) && !var8) {
               this.conversionType = 7;
            } else if (AudioFormat.Encoding.PCM_SIGNED.equals(var5) && var7 && AudioFormat.Encoding.PCM_UNSIGNED.equals(var6) && !var8) {
               this.conversionType = 8;
            }

            this.frameSize = var9.getFrameSize();
            if (this.frameSize == -1) {
               this.frameSize = 1;
            }

            if (var2 instanceof AudioInputStream) {
               this.frameLength = var2.getFrameLength();
            } else {
               this.frameLength = -1L;
            }

            this.framePos = 0L;
         }
      }

      public int read() throws IOException {
         if (this.frameSize == 1) {
            if (this.conversionType == 1) {
               int var1 = super.read();
               if (var1 < 0) {
                  return var1;
               } else {
                  byte var2 = (byte)(var1 & 15);
                  var2 = var2 >= 0 ? (byte)(128 | var2) : (byte)(127 & var2);
                  var1 = var2 & 15;
                  return var1;
               }
            } else {
               throw new IOException("cannot read a single byte if frame size > 1");
            }
         } else {
            throw new IOException("cannot read a single byte if frame size > 1");
         }
      }

      public int read(byte[] var1) throws IOException {
         return this.read(var1, 0, var1.length);
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         if (var3 % this.frameSize != 0) {
            var3 -= var3 % this.frameSize;
         }

         if (this.frameLength != -1L && (long)(var3 / this.frameSize) > this.frameLength - this.framePos) {
            var3 = (int)(this.frameLength - this.framePos) * this.frameSize;
         }

         int var5 = super.read(var1, var2, var3);
         if (var5 < 0) {
            return var5;
         } else {
            switch(this.conversionType) {
            case 1:
               this.switchSigned8bit(var1, var2, var3, var5);
               break;
            case 2:
               this.switchEndian(var1, var2, var3, var5);
               break;
            case 3:
               this.switchSignedLE(var1, var2, var3, var5);
               break;
            case 4:
               this.switchSignedBE(var1, var2, var3, var5);
               break;
            case 5:
            case 6:
               this.switchSignedLE(var1, var2, var3, var5);
               this.switchEndian(var1, var2, var3, var5);
               break;
            case 7:
            case 8:
               this.switchSignedBE(var1, var2, var3, var5);
               this.switchEndian(var1, var2, var3, var5);
            }

            return var5;
         }
      }

      private void switchSigned8bit(byte[] var1, int var2, int var3, int var4) {
         for(int var5 = var2; var5 < var2 + var4; ++var5) {
            var1[var5] = var1[var5] >= 0 ? (byte)(128 | var1[var5]) : (byte)(127 & var1[var5]);
         }

      }

      private void switchSignedBE(byte[] var1, int var2, int var3, int var4) {
         for(int var5 = var2; var5 < var2 + var4; var5 += this.sampleSizeInBytes) {
            var1[var5] = var1[var5] >= 0 ? (byte)(128 | var1[var5]) : (byte)(127 & var1[var5]);
         }

      }

      private void switchSignedLE(byte[] var1, int var2, int var3, int var4) {
         for(int var5 = var2 + this.sampleSizeInBytes - 1; var5 < var2 + var4; var5 += this.sampleSizeInBytes) {
            var1[var5] = var1[var5] >= 0 ? (byte)(128 | var1[var5]) : (byte)(127 & var1[var5]);
         }

      }

      private void switchEndian(byte[] var1, int var2, int var3, int var4) {
         if (this.sampleSizeInBytes == 2) {
            for(int var5 = var2; var5 < var2 + var4; var5 += this.sampleSizeInBytes) {
               byte var6 = var1[var5];
               var1[var5] = var1[var5 + 1];
               var1[var5 + 1] = var6;
            }
         }

      }
   }
}
