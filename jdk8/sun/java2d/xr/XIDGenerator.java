package sun.java2d.xr;

public class XIDGenerator {
   private static final int XID_BUFFER_SIZE = 512;
   int[] xidBuffer = new int[512];
   int currentIndex = 512;

   public int getNextXID() {
      if (this.currentIndex >= 512) {
         bufferXIDs(this.xidBuffer, this.xidBuffer.length);
         this.currentIndex = 0;
      }

      return this.xidBuffer[this.currentIndex++];
   }

   private static native void bufferXIDs(int[] var0, int var1);
}
