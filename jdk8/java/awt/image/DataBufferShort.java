package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferShort extends DataBuffer {
   short[] data;
   short[][] bankdata;

   public DataBufferShort(int var1) {
      super(StateTrackable.State.STABLE, 2, var1);
      this.data = new short[var1];
      this.bankdata = new short[1][];
      this.bankdata[0] = this.data;
   }

   public DataBufferShort(int var1, int var2) {
      super(StateTrackable.State.STABLE, 2, var1, var2);
      this.bankdata = new short[var2][];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.bankdata[var3] = new short[var1];
      }

      this.data = this.bankdata[0];
   }

   public DataBufferShort(short[] var1, int var2) {
      super(StateTrackable.State.UNTRACKABLE, 2, var2);
      this.data = var1;
      this.bankdata = new short[1][];
      this.bankdata[0] = this.data;
   }

   public DataBufferShort(short[] var1, int var2, int var3) {
      super(StateTrackable.State.UNTRACKABLE, 2, var2, 1, var3);
      this.data = var1;
      this.bankdata = new short[1][];
      this.bankdata[0] = this.data;
   }

   public DataBufferShort(short[][] var1, int var2) {
      super(StateTrackable.State.UNTRACKABLE, 2, var2, var1.length);
      this.bankdata = (short[][])((short[][])var1.clone());
      this.data = this.bankdata[0];
   }

   public DataBufferShort(short[][] var1, int var2, int[] var3) {
      super(StateTrackable.State.UNTRACKABLE, 2, var2, var1.length, var3);
      this.bankdata = (short[][])((short[][])var1.clone());
      this.data = this.bankdata[0];
   }

   public short[] getData() {
      this.theTrackable.setUntrackable();
      return this.data;
   }

   public short[] getData(int var1) {
      this.theTrackable.setUntrackable();
      return this.bankdata[var1];
   }

   public short[][] getBankData() {
      this.theTrackable.setUntrackable();
      return (short[][])((short[][])this.bankdata.clone());
   }

   public int getElem(int var1) {
      return this.data[var1 + this.offset];
   }

   public int getElem(int var1, int var2) {
      return this.bankdata[var1][var2 + this.offsets[var1]];
   }

   public void setElem(int var1, int var2) {
      this.data[var1 + this.offset] = (short)var2;
      this.theTrackable.markDirty();
   }

   public void setElem(int var1, int var2, int var3) {
      this.bankdata[var1][var2 + this.offsets[var1]] = (short)var3;
      this.theTrackable.markDirty();
   }
}
