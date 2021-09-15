package sun.nio.ch;

public class PollArrayWrapper extends AbstractPollArrayWrapper {
   int interruptFD;

   PollArrayWrapper(int var1) {
      var1 = (var1 + 1) * 8;
      this.pollArray = new AllocatedNativeObject(var1, false);
      this.pollArrayAddress = this.pollArray.address();
      this.totalChannels = 1;
   }

   void initInterrupt(int var1, int var2) {
      this.interruptFD = var2;
      this.putDescriptor(0, var1);
      this.putEventOps(0, Net.POLLIN);
      this.putReventOps(0, 0);
   }

   void release(int var1) {
   }

   void free() {
      this.pollArray.free();
   }

   void addEntry(SelChImpl var1) {
      this.putDescriptor(this.totalChannels, IOUtil.fdVal(var1.getFD()));
      this.putEventOps(this.totalChannels, 0);
      this.putReventOps(this.totalChannels, 0);
      ++this.totalChannels;
   }

   static void replaceEntry(PollArrayWrapper var0, int var1, PollArrayWrapper var2, int var3) {
      var2.putDescriptor(var3, var0.getDescriptor(var1));
      var2.putEventOps(var3, var0.getEventOps(var1));
      var2.putReventOps(var3, var0.getReventOps(var1));
   }

   void grow(int var1) {
      PollArrayWrapper var2 = new PollArrayWrapper(var1);

      for(int var3 = 0; var3 < this.totalChannels; ++var3) {
         replaceEntry(this, var3, var2, var3);
      }

      this.pollArray.free();
      this.pollArray = var2.pollArray;
      this.pollArrayAddress = this.pollArray.address();
   }

   int poll(int var1, int var2, long var3) {
      return this.poll0(this.pollArrayAddress + (long)(var2 * 8), var1, var3);
   }

   public void interrupt() {
      interrupt(this.interruptFD);
   }

   private native int poll0(long var1, int var3, long var4);

   private static native void interrupt(int var0);

   static {
      IOUtil.load();
   }
}
