package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferFloat extends DataBuffer {
   float[][] bankdata;
   float[] data;

   public DataBufferFloat(int var1) {
      super(StateTrackable.State.STABLE, 4, var1);
      this.data = new float[var1];
      this.bankdata = new float[1][];
      this.bankdata[0] = this.data;
   }

   public DataBufferFloat(int var1, int var2) {
      super(StateTrackable.State.STABLE, 4, var1, var2);
      this.bankdata = new float[var2][];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.bankdata[var3] = new float[var1];
      }

      this.data = this.bankdata[0];
   }

   public DataBufferFloat(float[] var1, int var2) {
      super(StateTrackable.State.UNTRACKABLE, 4, var2);
      this.data = var1;
      this.bankdata = new float[1][];
      this.bankdata[0] = this.data;
   }

   public DataBufferFloat(float[] var1, int var2, int var3) {
      super(StateTrackable.State.UNTRACKABLE, 4, var2, 1, var3);
      this.data = var1;
      this.bankdata = new float[1][];
      this.bankdata[0] = this.data;
   }

   public DataBufferFloat(float[][] var1, int var2) {
      super(StateTrackable.State.UNTRACKABLE, 4, var2, var1.length);
      this.bankdata = (float[][])((float[][])var1.clone());
      this.data = this.bankdata[0];
   }

   public DataBufferFloat(float[][] var1, int var2, int[] var3) {
      super(StateTrackable.State.UNTRACKABLE, 4, var2, var1.length, var3);
      this.bankdata = (float[][])((float[][])var1.clone());
      this.data = this.bankdata[0];
   }

   public float[] getData() {
      this.theTrackable.setUntrackable();
      return this.data;
   }

   public float[] getData(int var1) {
      this.theTrackable.setUntrackable();
      return this.bankdata[var1];
   }

   public float[][] getBankData() {
      this.theTrackable.setUntrackable();
      return (float[][])((float[][])this.bankdata.clone());
   }

   public int getElem(int var1) {
      return (int)this.data[var1 + this.offset];
   }

   public int getElem(int var1, int var2) {
      return (int)this.bankdata[var1][var2 + this.offsets[var1]];
   }

   public void setElem(int var1, int var2) {
      this.data[var1 + this.offset] = (float)var2;
      this.theTrackable.markDirty();
   }

   public void setElem(int var1, int var2, int var3) {
      this.bankdata[var1][var2 + this.offsets[var1]] = (float)var3;
      this.theTrackable.markDirty();
   }

   public float getElemFloat(int var1) {
      return this.data[var1 + this.offset];
   }

   public float getElemFloat(int var1, int var2) {
      return this.bankdata[var1][var2 + this.offsets[var1]];
   }

   public void setElemFloat(int var1, float var2) {
      this.data[var1 + this.offset] = var2;
      this.theTrackable.markDirty();
   }

   public void setElemFloat(int var1, int var2, float var3) {
      this.bankdata[var1][var2 + this.offsets[var1]] = var3;
      this.theTrackable.markDirty();
   }

   public double getElemDouble(int var1) {
      return (double)this.data[var1 + this.offset];
   }

   public double getElemDouble(int var1, int var2) {
      return (double)this.bankdata[var1][var2 + this.offsets[var1]];
   }

   public void setElemDouble(int var1, double var2) {
      this.data[var1 + this.offset] = (float)var2;
      this.theTrackable.markDirty();
   }

   public void setElemDouble(int var1, int var2, double var3) {
      this.bankdata[var1][var2 + this.offsets[var1]] = (float)var3;
      this.theTrackable.markDirty();
   }
}
