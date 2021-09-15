package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferInt extends DataBuffer {
   int[] data;
   int[][] bankdata;

   public DataBufferInt(int var1) {
      super(StateTrackable.State.STABLE, 3, var1);
      this.data = new int[var1];
      this.bankdata = new int[1][];
      this.bankdata[0] = this.data;
   }

   public DataBufferInt(int var1, int var2) {
      super(StateTrackable.State.STABLE, 3, var1, var2);
      this.bankdata = new int[var2][];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.bankdata[var3] = new int[var1];
      }

      this.data = this.bankdata[0];
   }

   public DataBufferInt(int[] var1, int var2) {
      super(StateTrackable.State.UNTRACKABLE, 3, var2);
      this.data = var1;
      this.bankdata = new int[1][];
      this.bankdata[0] = this.data;
   }

   public DataBufferInt(int[] var1, int var2, int var3) {
      super(StateTrackable.State.UNTRACKABLE, 3, var2, 1, var3);
      this.data = var1;
      this.bankdata = new int[1][];
      this.bankdata[0] = this.data;
   }

   public DataBufferInt(int[][] var1, int var2) {
      super(StateTrackable.State.UNTRACKABLE, 3, var2, var1.length);
      this.bankdata = (int[][])((int[][])var1.clone());
      this.data = this.bankdata[0];
   }

   public DataBufferInt(int[][] var1, int var2, int[] var3) {
      super(StateTrackable.State.UNTRACKABLE, 3, var2, var1.length, var3);
      this.bankdata = (int[][])((int[][])var1.clone());
      this.data = this.bankdata[0];
   }

   public int[] getData() {
      this.theTrackable.setUntrackable();
      return this.data;
   }

   public int[] getData(int var1) {
      this.theTrackable.setUntrackable();
      return this.bankdata[var1];
   }

   public int[][] getBankData() {
      this.theTrackable.setUntrackable();
      return (int[][])((int[][])this.bankdata.clone());
   }

   public int getElem(int var1) {
      return this.data[var1 + this.offset];
   }

   public int getElem(int var1, int var2) {
      return this.bankdata[var1][var2 + this.offsets[var1]];
   }

   public void setElem(int var1, int var2) {
      this.data[var1 + this.offset] = var2;
      this.theTrackable.markDirty();
   }

   public void setElem(int var1, int var2, int var3) {
      this.bankdata[var1][var2 + this.offsets[var1]] = var3;
      this.theTrackable.markDirty();
   }
}
