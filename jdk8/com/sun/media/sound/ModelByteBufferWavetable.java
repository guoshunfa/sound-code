package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public final class ModelByteBufferWavetable implements ModelWavetable {
   private float loopStart = -1.0F;
   private float loopLength = -1.0F;
   private final ModelByteBuffer buffer;
   private ModelByteBuffer buffer8 = null;
   private AudioFormat format = null;
   private float pitchcorrection = 0.0F;
   private float attenuation = 0.0F;
   private int loopType = 0;

   public ModelByteBufferWavetable(ModelByteBuffer var1) {
      this.buffer = var1;
   }

   public ModelByteBufferWavetable(ModelByteBuffer var1, float var2) {
      this.buffer = var1;
      this.pitchcorrection = var2;
   }

   public ModelByteBufferWavetable(ModelByteBuffer var1, AudioFormat var2) {
      this.format = var2;
      this.buffer = var1;
   }

   public ModelByteBufferWavetable(ModelByteBuffer var1, AudioFormat var2, float var3) {
      this.format = var2;
      this.buffer = var1;
      this.pitchcorrection = var3;
   }

   public void set8BitExtensionBuffer(ModelByteBuffer var1) {
      this.buffer8 = var1;
   }

   public ModelByteBuffer get8BitExtensionBuffer() {
      return this.buffer8;
   }

   public ModelByteBuffer getBuffer() {
      return this.buffer;
   }

   public AudioFormat getFormat() {
      if (this.format == null) {
         if (this.buffer == null) {
            return null;
         } else {
            InputStream var1 = this.buffer.getInputStream();
            AudioFormat var2 = null;

            try {
               var2 = AudioSystem.getAudioFileFormat(var1).getFormat();
            } catch (Exception var5) {
            }

            try {
               var1.close();
            } catch (IOException var4) {
            }

            return var2;
         }
      } else {
         return this.format;
      }
   }

   public AudioFloatInputStream openStream() {
      if (this.buffer == null) {
         return null;
      } else {
         AudioFormat var2;
         if (this.format == null) {
            InputStream var5 = this.buffer.getInputStream();
            var2 = null;

            AudioInputStream var6;
            try {
               var6 = AudioSystem.getAudioInputStream(var5);
            } catch (Exception var4) {
               return null;
            }

            return AudioFloatInputStream.getInputStream(var6);
         } else if (this.buffer.array() == null) {
            return AudioFloatInputStream.getInputStream(new AudioInputStream(this.buffer.getInputStream(), this.format, this.buffer.capacity() / (long)this.format.getFrameSize()));
         } else if (this.buffer8 == null || !this.format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && !this.format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
            return AudioFloatInputStream.getInputStream(this.format, this.buffer.array(), (int)this.buffer.arrayOffset(), (int)this.buffer.capacity());
         } else {
            ModelByteBufferWavetable.Buffer8PlusInputStream var1 = new ModelByteBufferWavetable.Buffer8PlusInputStream();
            var2 = new AudioFormat(this.format.getEncoding(), this.format.getSampleRate(), this.format.getSampleSizeInBits() + 8, this.format.getChannels(), this.format.getFrameSize() + 1 * this.format.getChannels(), this.format.getFrameRate(), this.format.isBigEndian());
            AudioInputStream var3 = new AudioInputStream(var1, var2, this.buffer.capacity() / (long)this.format.getFrameSize());
            return AudioFloatInputStream.getInputStream(var3);
         }
      }
   }

   public int getChannels() {
      return this.getFormat().getChannels();
   }

   public ModelOscillatorStream open(float var1) {
      return null;
   }

   public float getAttenuation() {
      return this.attenuation;
   }

   public void setAttenuation(float var1) {
      this.attenuation = var1;
   }

   public float getLoopLength() {
      return this.loopLength;
   }

   public void setLoopLength(float var1) {
      this.loopLength = var1;
   }

   public float getLoopStart() {
      return this.loopStart;
   }

   public void setLoopStart(float var1) {
      this.loopStart = var1;
   }

   public void setLoopType(int var1) {
      this.loopType = var1;
   }

   public int getLoopType() {
      return this.loopType;
   }

   public float getPitchcorrection() {
      return this.pitchcorrection;
   }

   public void setPitchcorrection(float var1) {
      this.pitchcorrection = var1;
   }

   private class Buffer8PlusInputStream extends InputStream {
      private final boolean bigendian;
      private final int framesize_pc;
      int pos = 0;
      int pos2 = 0;
      int markpos = 0;
      int markpos2 = 0;

      Buffer8PlusInputStream() {
         this.framesize_pc = ModelByteBufferWavetable.this.format.getFrameSize() / ModelByteBufferWavetable.this.format.getChannels();
         this.bigendian = ModelByteBufferWavetable.this.format.isBigEndian();
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         int var4 = this.available();
         if (var4 <= 0) {
            return -1;
         } else {
            if (var3 > var4) {
               var3 = var4;
            }

            byte[] var5 = ModelByteBufferWavetable.this.buffer.array();
            byte[] var6 = ModelByteBufferWavetable.this.buffer8.array();
            this.pos = (int)((long)this.pos + ModelByteBufferWavetable.this.buffer.arrayOffset());
            this.pos2 = (int)((long)this.pos2 + ModelByteBufferWavetable.this.buffer8.arrayOffset());
            int var7;
            if (this.bigendian) {
               for(var7 = 0; var7 < var3; var7 += this.framesize_pc + 1) {
                  System.arraycopy(var5, this.pos, var1, var7, this.framesize_pc);
                  System.arraycopy(var6, this.pos2, var1, var7 + this.framesize_pc, 1);
                  this.pos += this.framesize_pc;
                  ++this.pos2;
               }
            } else {
               for(var7 = 0; var7 < var3; var7 += this.framesize_pc + 1) {
                  System.arraycopy(var6, this.pos2, var1, var7, 1);
                  System.arraycopy(var5, this.pos, var1, var7 + 1, this.framesize_pc);
                  this.pos += this.framesize_pc;
                  ++this.pos2;
               }
            }

            this.pos = (int)((long)this.pos - ModelByteBufferWavetable.this.buffer.arrayOffset());
            this.pos2 = (int)((long)this.pos2 - ModelByteBufferWavetable.this.buffer8.arrayOffset());
            return var3;
         }
      }

      public long skip(long var1) throws IOException {
         int var3 = this.available();
         if (var3 <= 0) {
            return -1L;
         } else {
            if (var1 > (long)var3) {
               var1 = (long)var3;
            }

            this.pos = (int)((long)this.pos + var1 / (long)(this.framesize_pc + 1) * (long)this.framesize_pc);
            this.pos2 = (int)((long)this.pos2 + var1 / (long)(this.framesize_pc + 1));
            return super.skip(var1);
         }
      }

      public int read(byte[] var1) throws IOException {
         return this.read(var1, 0, var1.length);
      }

      public int read() throws IOException {
         byte[] var1 = new byte[1];
         int var2 = this.read(var1, 0, 1);
         return var2 == -1 ? -1 : 0;
      }

      public boolean markSupported() {
         return true;
      }

      public int available() throws IOException {
         return (int)ModelByteBufferWavetable.this.buffer.capacity() + (int)ModelByteBufferWavetable.this.buffer8.capacity() - this.pos - this.pos2;
      }

      public synchronized void mark(int var1) {
         this.markpos = this.pos;
         this.markpos2 = this.pos2;
      }

      public synchronized void reset() throws IOException {
         this.pos = this.markpos;
         this.pos2 = this.markpos2;
      }
   }
}
