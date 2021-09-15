package com.sun.media.sound;

import java.util.Arrays;
import javax.sound.sampled.AudioFormat;

public final class SoftAudioBuffer {
   private int size;
   private float[] buffer;
   private boolean empty = true;
   private AudioFormat format;
   private AudioFloatConverter converter;
   private byte[] converter_buffer;

   public SoftAudioBuffer(int var1, AudioFormat var2) {
      this.size = var1;
      this.format = var2;
      this.converter = AudioFloatConverter.getConverter(var2);
   }

   public void swap(SoftAudioBuffer var1) {
      int var2 = this.size;
      float[] var3 = this.buffer;
      boolean var4 = this.empty;
      AudioFormat var5 = this.format;
      AudioFloatConverter var6 = this.converter;
      byte[] var7 = this.converter_buffer;
      this.size = var1.size;
      this.buffer = var1.buffer;
      this.empty = var1.empty;
      this.format = var1.format;
      this.converter = var1.converter;
      this.converter_buffer = var1.converter_buffer;
      var1.size = var2;
      var1.buffer = var3;
      var1.empty = var4;
      var1.format = var5;
      var1.converter = var6;
      var1.converter_buffer = var7;
   }

   public AudioFormat getFormat() {
      return this.format;
   }

   public int getSize() {
      return this.size;
   }

   public void clear() {
      if (!this.empty) {
         Arrays.fill(this.buffer, 0.0F);
         this.empty = true;
      }

   }

   public boolean isSilent() {
      return this.empty;
   }

   public float[] array() {
      this.empty = false;
      if (this.buffer == null) {
         this.buffer = new float[this.size];
      }

      return this.buffer;
   }

   public void get(byte[] var1, int var2) {
      int var3 = this.format.getFrameSize() / this.format.getChannels();
      int var4 = this.size * var3;
      if (this.converter_buffer == null || this.converter_buffer.length < var4) {
         this.converter_buffer = new byte[var4];
      }

      if (this.format.getChannels() == 1) {
         this.converter.toByteArray(this.array(), this.size, var1);
      } else {
         this.converter.toByteArray(this.array(), this.size, this.converter_buffer);
         if (var2 >= this.format.getChannels()) {
            return;
         }

         int var5 = this.format.getChannels() * var3;
         int var6 = var3;

         for(int var7 = 0; var7 < var3; ++var7) {
            int var8 = var7;
            int var9 = var2 * var3 + var7;

            for(int var10 = 0; var10 < this.size; ++var10) {
               var1[var9] = this.converter_buffer[var8];
               var9 += var5;
               var8 += var6;
            }
         }
      }

   }
}
