package javax.sound.sampled;

import java.util.Arrays;

public interface DataLine extends Line {
   void drain();

   void flush();

   void start();

   void stop();

   boolean isRunning();

   boolean isActive();

   AudioFormat getFormat();

   int getBufferSize();

   int available();

   int getFramePosition();

   long getLongFramePosition();

   long getMicrosecondPosition();

   float getLevel();

   public static class Info extends Line.Info {
      private final AudioFormat[] formats;
      private final int minBufferSize;
      private final int maxBufferSize;

      public Info(Class<?> var1, AudioFormat[] var2, int var3, int var4) {
         super(var1);
         if (var2 == null) {
            this.formats = new AudioFormat[0];
         } else {
            this.formats = (AudioFormat[])Arrays.copyOf((Object[])var2, var2.length);
         }

         this.minBufferSize = var3;
         this.maxBufferSize = var4;
      }

      public Info(Class<?> var1, AudioFormat var2, int var3) {
         super(var1);
         if (var2 == null) {
            this.formats = new AudioFormat[0];
         } else {
            this.formats = new AudioFormat[]{var2};
         }

         this.minBufferSize = var3;
         this.maxBufferSize = var3;
      }

      public Info(Class<?> var1, AudioFormat var2) {
         this(var1, var2, -1);
      }

      public AudioFormat[] getFormats() {
         return (AudioFormat[])Arrays.copyOf((Object[])this.formats, this.formats.length);
      }

      public boolean isFormatSupported(AudioFormat var1) {
         for(int var2 = 0; var2 < this.formats.length; ++var2) {
            if (var1.matches(this.formats[var2])) {
               return true;
            }
         }

         return false;
      }

      public int getMinBufferSize() {
         return this.minBufferSize;
      }

      public int getMaxBufferSize() {
         return this.maxBufferSize;
      }

      public boolean matches(Line.Info var1) {
         if (!super.matches(var1)) {
            return false;
         } else {
            DataLine.Info var2 = (DataLine.Info)var1;
            if (this.getMaxBufferSize() >= 0 && var2.getMaxBufferSize() >= 0 && this.getMaxBufferSize() > var2.getMaxBufferSize()) {
               return false;
            } else if (this.getMinBufferSize() >= 0 && var2.getMinBufferSize() >= 0 && this.getMinBufferSize() < var2.getMinBufferSize()) {
               return false;
            } else {
               AudioFormat[] var3 = this.getFormats();
               if (var3 != null) {
                  for(int var4 = 0; var4 < var3.length; ++var4) {
                     if (var3[var4] != null && !var2.isFormatSupported(var3[var4])) {
                        return false;
                     }
                  }
               }

               return true;
            }
         }
      }

      public String toString() {
         StringBuffer var1 = new StringBuffer();
         if (this.formats.length == 1 && this.formats[0] != null) {
            var1.append(" supporting format " + this.formats[0]);
         } else if (this.getFormats().length > 1) {
            var1.append(" supporting " + this.getFormats().length + " audio formats");
         }

         if (this.minBufferSize != -1 && this.maxBufferSize != -1) {
            var1.append(", and buffers of " + this.minBufferSize + " to " + this.maxBufferSize + " bytes");
         } else if (this.minBufferSize != -1 && this.minBufferSize > 0) {
            var1.append(", and buffers of at least " + this.minBufferSize + " bytes");
         } else if (this.maxBufferSize != -1) {
            var1.append(", and buffers of up to " + this.minBufferSize + " bytes");
         }

         return new String(super.toString() + var1);
      }
   }
}
