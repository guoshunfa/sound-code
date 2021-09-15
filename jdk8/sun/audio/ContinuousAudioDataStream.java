package sun.audio;

public final class ContinuousAudioDataStream extends AudioDataStream {
   public ContinuousAudioDataStream(AudioData var1) {
      super(var1);
   }

   public int read() {
      int var1 = super.read();
      if (var1 == -1) {
         this.reset();
         var1 = super.read();
      }

      return var1;
   }

   public int read(byte[] var1, int var2, int var3) {
      int var4 = 0;

      while(var4 < var3) {
         int var5 = super.read(var1, var2 + var4, var3 - var4);
         if (var5 >= 0) {
            var4 += var5;
         } else {
            this.reset();
         }
      }

      return var4;
   }
}
