package javax.sound.sampled;

import java.io.IOException;
import java.io.InputStream;

public class AudioInputStream extends InputStream {
   private InputStream stream;
   protected AudioFormat format;
   protected long frameLength;
   protected int frameSize;
   protected long framePos;
   private long markpos;
   private byte[] pushBackBuffer = null;
   private int pushBackLen = 0;
   private byte[] markPushBackBuffer = null;
   private int markPushBackLen = 0;

   public AudioInputStream(InputStream var1, AudioFormat var2, long var3) {
      this.format = var2;
      this.frameLength = var3;
      this.frameSize = var2.getFrameSize();
      if (this.frameSize == -1 || this.frameSize <= 0) {
         this.frameSize = 1;
      }

      this.stream = var1;
      this.framePos = 0L;
      this.markpos = 0L;
   }

   public AudioInputStream(TargetDataLine var1) {
      AudioInputStream.TargetDataLineInputStream var2 = new AudioInputStream.TargetDataLineInputStream(var1);
      this.format = var1.getFormat();
      this.frameLength = -1L;
      this.frameSize = this.format.getFrameSize();
      if (this.frameSize == -1 || this.frameSize <= 0) {
         this.frameSize = 1;
      }

      this.stream = var2;
      this.framePos = 0L;
      this.markpos = 0L;
   }

   public AudioFormat getFormat() {
      return this.format;
   }

   public long getFrameLength() {
      return this.frameLength;
   }

   public int read() throws IOException {
      if (this.frameSize != 1) {
         throw new IOException("cannot read a single byte if frame size > 1");
      } else {
         byte[] var1 = new byte[1];
         int var2 = this.read(var1);
         return var2 <= 0 ? -1 : var1[0] & 255;
      }
   }

   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var3 % this.frameSize != 0) {
         var3 -= var3 % this.frameSize;
         if (var3 == 0) {
            return 0;
         }
      }

      if (this.frameLength != -1L) {
         if (this.framePos >= this.frameLength) {
            return -1;
         }

         if ((long)(var3 / this.frameSize) > this.frameLength - this.framePos) {
            var3 = (int)(this.frameLength - this.framePos) * this.frameSize;
         }
      }

      int var4 = 0;
      int var5 = var2;
      if (this.pushBackLen > 0 && var3 >= this.pushBackLen) {
         System.arraycopy(this.pushBackBuffer, 0, var1, var2, this.pushBackLen);
         var5 = var2 + this.pushBackLen;
         var3 -= this.pushBackLen;
         var4 += this.pushBackLen;
         this.pushBackLen = 0;
      }

      int var6 = this.stream.read(var1, var5, var3);
      if (var6 == -1) {
         return -1;
      } else {
         if (var6 > 0) {
            var4 += var6;
         }

         if (var4 > 0) {
            this.pushBackLen = var4 % this.frameSize;
            if (this.pushBackLen > 0) {
               if (this.pushBackBuffer == null) {
                  this.pushBackBuffer = new byte[this.frameSize];
               }

               System.arraycopy(var1, var2 + var4 - this.pushBackLen, this.pushBackBuffer, 0, this.pushBackLen);
               var4 -= this.pushBackLen;
            }

            this.framePos += (long)(var4 / this.frameSize);
         }

         return var4;
      }
   }

   public long skip(long var1) throws IOException {
      if (var1 % (long)this.frameSize != 0L) {
         var1 -= var1 % (long)this.frameSize;
      }

      if (this.frameLength != -1L && var1 / (long)this.frameSize > this.frameLength - this.framePos) {
         var1 = (this.frameLength - this.framePos) * (long)this.frameSize;
      }

      long var3 = this.stream.skip(var1);
      if (var3 % (long)this.frameSize != 0L) {
         throw new IOException("Could not skip an integer number of frames.");
      } else {
         if (var3 >= 0L) {
            this.framePos += var3 / (long)this.frameSize;
         }

         return var3;
      }
   }

   public int available() throws IOException {
      int var1 = this.stream.available();
      return this.frameLength != -1L && (long)(var1 / this.frameSize) > this.frameLength - this.framePos ? (int)(this.frameLength - this.framePos) * this.frameSize : var1;
   }

   public void close() throws IOException {
      this.stream.close();
   }

   public void mark(int var1) {
      this.stream.mark(var1);
      if (this.markSupported()) {
         this.markpos = this.framePos;
         this.markPushBackLen = this.pushBackLen;
         if (this.markPushBackLen > 0) {
            if (this.markPushBackBuffer == null) {
               this.markPushBackBuffer = new byte[this.frameSize];
            }

            System.arraycopy(this.pushBackBuffer, 0, this.markPushBackBuffer, 0, this.markPushBackLen);
         }
      }

   }

   public void reset() throws IOException {
      this.stream.reset();
      this.framePos = this.markpos;
      this.pushBackLen = this.markPushBackLen;
      if (this.pushBackLen > 0) {
         if (this.pushBackBuffer == null) {
            this.pushBackBuffer = new byte[this.frameSize - 1];
         }

         System.arraycopy(this.markPushBackBuffer, 0, this.pushBackBuffer, 0, this.pushBackLen);
      }

   }

   public boolean markSupported() {
      return this.stream.markSupported();
   }

   private class TargetDataLineInputStream extends InputStream {
      TargetDataLine line;

      TargetDataLineInputStream(TargetDataLine var2) {
         this.line = var2;
      }

      public int available() throws IOException {
         return this.line.available();
      }

      public void close() throws IOException {
         if (this.line.isActive()) {
            this.line.flush();
            this.line.stop();
         }

         this.line.close();
      }

      public int read() throws IOException {
         byte[] var1 = new byte[1];
         int var2 = this.read(var1, 0, 1);
         if (var2 == -1) {
            return -1;
         } else {
            var2 = var1[0];
            if (this.line.getFormat().getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
               var2 += 128;
            }

            return var2;
         }
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         try {
            return this.line.read(var1, var2, var3);
         } catch (IllegalArgumentException var5) {
            throw new IOException(var5.getMessage());
         }
      }
   }
}
