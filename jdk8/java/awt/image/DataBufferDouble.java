package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferDouble extends DataBuffer {
   double[][] bankdata;
   double[] data;

   public DataBufferDouble(int var1) {
      super(StateTrackable.State.STABLE, 5, var1);
      this.data = new double[var1];
      this.bankdata = new double[1][];
      this.bankdata[0] = this.data;
   }

   public DataBufferDouble(int var1, int var2) {
      super(StateTrackable.State.STABLE, 5, var1, var2);
      this.bankdata = new double[var2][];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.bankdata[var3] = new double[var1];
      }

      this.data = this.bankdata[0];
   }

   public DataBufferDouble(double[] var1, int var2) {
      super(StateTrackable.State.UNTRACKABLE, 5, var2);
      this.data = var1;
      this.bankdata = new double[1][];
      this.bankdata[0] = this.data;
   }

   public DataBufferDouble(double[] var1, int var2, int var3) {
      super(StateTrackable.State.UNTRACKABLE, 5, var2, 1, var3);
      this.data = var1;
      this.bankdata = new double[1][];
      this.bankdata[0] = this.data;
   }

   public DataBufferDouble(double[][] var1, int var2) {
      super(StateTrackable.State.UNTRACKABLE, 5, var2, var1.length);
      this.bankdata = (double[][])((double[][])var1.clone());
      this.data = this.bankdata[0];
   }

   public DataBufferDouble(double[][] var1, int var2, int[] var3) {
      super(StateTrackable.State.UNTRACKABLE, 5, var2, var1.length, var3);
      this.bankdata = (double[][])((double[][])var1.clone());
      this.data = this.bankdata[0];
   }

   public double[] getData() {
      this.theTrackable.setUntrackable();
      return this.data;
   }

   public double[] getData(int var1) {
      this.theTrackable.setUntrackable();
      return this.bankdata[var1];
   }

   public double[][] getBankData() {
      this.theTrackable.setUntrackable();
      return (double[][])((double[][])this.bankdata.clone());
   }

   public int getElem(int var1) {
      return (int)this.data[var1 + this.offset];
   }

   public int getElem(int var1, int var2) {
      return (int)this.bankdata[var1][var2 + this.offsets[var1]];
   }

   public void setElem(int var1, int var2) {
      this.data[var1 + this.offset] = (double)var2;
      this.theTrackable.markDirty();
   }

   public void setElem(int var1, int var2, int var3) {
      this.bankdata[var1][var2 + this.offsets[var1]] = (double)var3;
      this.theTrackable.markDirty();
   }

   public float getElemFloat(int var1) {
      return (float)this.data[var1 + this.offset];
   }

   public float getElemFloat(int var1, int var2) {
      return (float)this.bankdata[var1][var2 + this.offsets[var1]];
   }

   public void setElemFloat(int var1, float var2) {
      this.data[var1 + this.offset] = (double)var2;
      this.theTrackable.markDirty();
   }

   public void setElemFloat(int var1, int var2, float var3) {
      this.bankdata[var1][var2 + this.offsets[var1]] = (double)var3;
      this.theTrackable.markDirty();
   }

   public double getElemDouble(int var1) {
      return this.data[var1 + this.offset];
   }

   public double getElemDouble(int var1, int var2) {
      return this.bankdata[var1][var2 + this.offsets[var1]];
   }

   public void setElemDouble(int var1, double var2) {
      this.data[var1 + this.offset] = var2;
      this.theTrackable.markDirty();
   }

   public void setElemDouble(int var1, int var2, double var3) {
      this.bankdata[var1][var2 + this.offsets[var1]] = var3;
      this.theTrackable.markDirty();
   }
}
