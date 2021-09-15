package com.sun.media.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public final class Toolkit {
   private Toolkit() {
   }

   static void getUnsigned8(byte[] var0, int var1, int var2) {
      for(int var3 = var1; var3 < var1 + var2; ++var3) {
         var0[var3] = (byte)(var0[var3] + 128);
      }

   }

   static void getByteSwapped(byte[] var0, int var1, int var2) {
      for(int var4 = var1; var4 < var1 + var2; var4 += 2) {
         byte var3 = var0[var4];
         var0[var4] = var0[var4 + 1];
         var0[var4 + 1] = var3;
      }

   }

   static float linearToDB(float var0) {
      float var1 = (float)(Math.log((double)var0 == 0.0D ? 1.0E-4D : (double)var0) / Math.log(10.0D) * 20.0D);
      return var1;
   }

   static float dBToLinear(float var0) {
      float var1 = (float)Math.pow(10.0D, (double)var0 / 20.0D);
      return var1;
   }

   static long align(long var0, int var2) {
      return var2 <= 1 ? var0 : var0 - var0 % (long)var2;
   }

   static int align(int var0, int var1) {
      return var1 <= 1 ? var0 : var0 - var0 % var1;
   }

   static long millis2bytes(AudioFormat var0, long var1) {
      long var3 = (long)((float)var1 * var0.getFrameRate() / 1000.0F * (float)var0.getFrameSize());
      return align(var3, var0.getFrameSize());
   }

   static long bytes2millis(AudioFormat var0, long var1) {
      return (long)((float)var1 / var0.getFrameRate() * 1000.0F / (float)var0.getFrameSize());
   }

   static long micros2bytes(AudioFormat var0, long var1) {
      long var3 = (long)((float)var1 * var0.getFrameRate() / 1000000.0F * (float)var0.getFrameSize());
      return align(var3, var0.getFrameSize());
   }

   static long bytes2micros(AudioFormat var0, long var1) {
      return (long)((float)var1 / var0.getFrameRate() * 1000000.0F / (float)var0.getFrameSize());
   }

   static long micros2frames(AudioFormat var0, long var1) {
      return (long)((float)var1 * var0.getFrameRate() / 1000000.0F);
   }

   static long frames2micros(AudioFormat var0, long var1) {
      return (long)((double)var1 / (double)var0.getFrameRate() * 1000000.0D);
   }

   static void isFullySpecifiedAudioFormat(AudioFormat var0) {
      if (var0.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) || var0.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED) || var0.getEncoding().equals(AudioFormat.Encoding.ULAW) || var0.getEncoding().equals(AudioFormat.Encoding.ALAW)) {
         if (var0.getFrameRate() <= 0.0F) {
            throw new IllegalArgumentException("invalid frame rate: " + (var0.getFrameRate() == -1.0F ? "NOT_SPECIFIED" : String.valueOf(var0.getFrameRate())));
         } else if (var0.getSampleRate() <= 0.0F) {
            throw new IllegalArgumentException("invalid sample rate: " + (var0.getSampleRate() == -1.0F ? "NOT_SPECIFIED" : String.valueOf(var0.getSampleRate())));
         } else if (var0.getSampleSizeInBits() <= 0) {
            throw new IllegalArgumentException("invalid sample size in bits: " + (var0.getSampleSizeInBits() == -1 ? "NOT_SPECIFIED" : String.valueOf(var0.getSampleSizeInBits())));
         } else if (var0.getFrameSize() <= 0) {
            throw new IllegalArgumentException("invalid frame size: " + (var0.getFrameSize() == -1 ? "NOT_SPECIFIED" : String.valueOf(var0.getFrameSize())));
         } else if (var0.getChannels() <= 0) {
            throw new IllegalArgumentException("invalid number of channels: " + (var0.getChannels() == -1 ? "NOT_SPECIFIED" : String.valueOf(var0.getChannels())));
         }
      }
   }

   static boolean isFullySpecifiedPCMFormat(AudioFormat var0) {
      if (!var0.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && !var0.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
         return false;
      } else {
         return var0.getFrameRate() > 0.0F && var0.getSampleRate() > 0.0F && var0.getSampleSizeInBits() > 0 && var0.getFrameSize() > 0 && var0.getChannels() > 0;
      }
   }

   public static AudioInputStream getPCMConvertedAudioInputStream(AudioInputStream var0) {
      AudioFormat var1 = var0.getFormat();
      if (!var1.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && !var1.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
         try {
            AudioFormat var2 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, var1.getSampleRate(), 16, var1.getChannels(), var1.getChannels() * 2, var1.getSampleRate(), Platform.isBigEndian());
            var0 = AudioSystem.getAudioInputStream(var2, var0);
         } catch (Exception var3) {
            var0 = null;
         }
      }

      return var0;
   }
}
