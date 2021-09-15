package com.sun.media.sound;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.spi.AudioFileWriter;

public final class WaveFloatFileWriter extends AudioFileWriter {
   public AudioFileFormat.Type[] getAudioFileTypes() {
      return new AudioFileFormat.Type[]{AudioFileFormat.Type.WAVE};
   }

   public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream var1) {
      return !var1.getFormat().getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT) ? new AudioFileFormat.Type[0] : new AudioFileFormat.Type[]{AudioFileFormat.Type.WAVE};
   }

   private void checkFormat(AudioFileFormat.Type var1, AudioInputStream var2) {
      if (!AudioFileFormat.Type.WAVE.equals(var1)) {
         throw new IllegalArgumentException("File type " + var1 + " not supported.");
      } else if (!var2.getFormat().getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT)) {
         throw new IllegalArgumentException("File format " + var2.getFormat() + " not supported.");
      }
   }

   public void write(AudioInputStream var1, RIFFWriter var2) throws IOException {
      RIFFWriter var3 = var2.writeChunk("fmt ");
      AudioFormat var4 = var1.getFormat();
      var3.writeUnsignedShort(3);
      var3.writeUnsignedShort(var4.getChannels());
      var3.writeUnsignedInt((long)((int)var4.getSampleRate()));
      var3.writeUnsignedInt((long)((int)var4.getFrameRate() * var4.getFrameSize()));
      var3.writeUnsignedShort(var4.getFrameSize());
      var3.writeUnsignedShort(var4.getSampleSizeInBits());
      var3.close();
      RIFFWriter var5 = var2.writeChunk("data");
      byte[] var6 = new byte[1024];

      int var7;
      while((var7 = var1.read(var6, 0, var6.length)) != -1) {
         var5.write(var6, 0, var7);
      }

      var5.close();
   }

   private AudioInputStream toLittleEndian(AudioInputStream var1) {
      AudioFormat var2 = var1.getFormat();
      AudioFormat var3 = new AudioFormat(var2.getEncoding(), var2.getSampleRate(), var2.getSampleSizeInBits(), var2.getChannels(), var2.getFrameSize(), var2.getFrameRate(), false);
      return AudioSystem.getAudioInputStream(var3, var1);
   }

   public int write(AudioInputStream var1, AudioFileFormat.Type var2, OutputStream var3) throws IOException {
      this.checkFormat(var2, var1);
      if (var1.getFormat().isBigEndian()) {
         var1 = this.toLittleEndian(var1);
      }

      RIFFWriter var4 = new RIFFWriter(new WaveFloatFileWriter.NoCloseOutputStream(var3), "WAVE");
      this.write(var1, var4);
      int var5 = (int)var4.getFilePointer();
      var4.close();
      return var5;
   }

   public int write(AudioInputStream var1, AudioFileFormat.Type var2, File var3) throws IOException {
      this.checkFormat(var2, var1);
      if (var1.getFormat().isBigEndian()) {
         var1 = this.toLittleEndian(var1);
      }

      RIFFWriter var4 = new RIFFWriter(var3, "WAVE");
      this.write(var1, var4);
      int var5 = (int)var4.getFilePointer();
      var4.close();
      return var5;
   }

   private static class NoCloseOutputStream extends OutputStream {
      final OutputStream out;

      NoCloseOutputStream(OutputStream var1) {
         this.out = var1;
      }

      public void write(int var1) throws IOException {
         this.out.write(var1);
      }

      public void flush() throws IOException {
         this.out.flush();
      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         this.out.write(var1, var2, var3);
      }

      public void write(byte[] var1) throws IOException {
         this.out.write(var1);
      }
   }
}
