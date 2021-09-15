package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferByte extends DataBuffer {
   byte[] data;
   byte[][] bankdata;

   public DataBufferByte(int var1) {
      super(StateTrackable.State.STABLE, 0, var1);
      this.data = new byte[var1];
      this.bankdata = new byte[1][];
      this.bankdata[0] = this.data;
   }

   public DataBufferByte(int var1, int var2) {
      super(StateTrackable.State.STABLE, 0, var1, var2);
      this.bankdata = new byte[var2][];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.bankdata[var3] = new byte[var1];
      }

      this.data = this.bankdata[0];
   }

   public DataBufferByte(byte[] var1, int var2) {
      super(StateTrackable.State.UNTRACKABLE, 0, var2);
      this.data = var1;
      this.bankdata = new byte[1][];
      this.bankdata[0] = this.data;
   }

   public DataBufferByte(byte[] var1, int var2, int var3) {
      super(StateTrackable.State.UNTRACKABLE, 0, var2, 1, var3);
      this.data = var1;
      this.bankdata = new byte[1][];
      this.bankdata[0] = this.data;
   }

   public DataBufferByte(byte[][] var1, int var2) {
      super(StateTrackable.State.UNTRACKABLE, 0, var2, var1.length);
      this.bankdata = (byte[][])((byte[][])var1.clone());
      this.data = this.bankdata[0];
   }

   public DataBufferByte(byte[][] var1, int var2, int[] var3) {
      super(StateTrackable.State.UNTRACKABLE, 0, var2, var1.length, var3);
      this.bankdata = (byte[][])((byte[][])var1.clone());
      this.data = this.bankdata[0];
   }

   public byte[] getData() {
      this.theTrackable.setUntrackable();
      return this.data;
   }

   public byte[] getData(int var1) {
      this.theTrackable.setUntrackable();
      return this.bankdata[var1];
   }

   public byte[][] getBankData() {
      this.theTrackable.setUntrackable();
      return (byte[][])((byte[][])this.bankdata.clone());
   }

   public int getElem(int var1) {
      return this.data[var1 + this.offset] & 255;
   }

   public int getElem(int var1, int var2) {
      return this.bankdata[var1][var2 + this.offsets[var1]] & 255;
   }

   public void setElem(int var1, int var2) {
      this.data[var1 + this.offset] = (byte)var2;
      this.theTrackable.markDirty();
   }

   public void setElem(int var1, int var2, int var3) {
      this.bankdata[var1][var2 + this.offsets[var1]] = (byte)var3;
      this.theTrackable.markDirty();
   }
}
