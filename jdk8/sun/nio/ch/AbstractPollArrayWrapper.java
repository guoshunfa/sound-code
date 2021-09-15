package sun.nio.ch;

public abstract class AbstractPollArrayWrapper {
   static final short SIZE_POLLFD = 8;
   static final short FD_OFFSET = 0;
   static final short EVENT_OFFSET = 4;
   static final short REVENT_OFFSET = 6;
   protected AllocatedNativeObject pollArray;
   protected int totalChannels = 0;
   protected long pollArrayAddress;

   int getEventOps(int var1) {
      int var2 = 8 * var1 + 4;
      return this.pollArray.getShort(var2);
   }

   int getReventOps(int var1) {
      int var2 = 8 * var1 + 6;
      return this.pollArray.getShort(var2);
   }

   int getDescriptor(int var1) {
      int var2 = 8 * var1 + 0;
      return this.pollArray.getInt(var2);
   }

   void putEventOps(int var1, int var2) {
      int var3 = 8 * var1 + 4;
      this.pollArray.putShort(var3, (short)var2);
   }

   void putReventOps(int var1, int var2) {
      int var3 = 8 * var1 + 6;
      this.pollArray.putShort(var3, (short)var2);
   }

   void putDescriptor(int var1, int var2) {
      int var3 = 8 * var1 + 0;
      this.pollArray.putInt(var3, var2);
   }
}
