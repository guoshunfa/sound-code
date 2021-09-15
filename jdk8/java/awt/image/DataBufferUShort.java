package java.awt.image;

import sun.java2d.StateTrackable;

public final class DataBufferUShort extends DataBuffer {
   short[] data;
   short[][] bankdata;

   public DataBufferUShort(int var1) {
      super(StateTrackable.State.STABLE, 1, var1);
      this.data = new short[var1];
      this.bankdata = new short[1][];
      this.bankdata[0] = this.data;
   }

   public DataBufferUShort(int var1, int var2) {
      super(StateTrackable.State.STABLE, 1, var1, var2);
      this.bankdata = new short[var2][];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.bankdata[var3] = new short[var1];
      }

      this.data = this.bankdata[0];
   }

   public DataBufferUShort(short[] var1, int var2) {
      super(StateTrackable.State.UNTRACKABLE, 1, var2);
      if (var1 == null) {
         throw new NullPointerException("dataArray is null");
      } else {
         this.data = var1;
         this.bankdata = new short[1][];
         this.bankdata[0] = this.data;
      }
   }

   public DataBufferUShort(short[] var1, int var2, int var3) {
      super(StateTrackable.State.UNTRACKABLE, 1, var2, 1, var3);
      if (var1 == null) {
         throw new NullPointerException("dataArray is null");
      } else if (var2 + var3 > var1.length) {
         throw new IllegalArgumentException("Length of dataArray is less  than size+offset.");
      } else {
         this.data = var1;
         this.bankdata = new short[1][];
         this.bankdata[0] = this.data;
      }
   }

   public DataBufferUShort(short[][] var1, int var2) {
      super(StateTrackable.State.UNTRACKABLE, 1, var2, var1.length);
      if (var1 == null) {
         throw new NullPointerException("dataArray is null");
      } else {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3] == null) {
               throw new NullPointerException("dataArray[" + var3 + "] is null");
            }
         }

         this.bankdata = (short[][])((short[][])var1.clone());
         this.data = this.bankdata[0];
      }
   }

   public DataBufferUShort(short[][] var1, int var2, int[] var3) {
      super(StateTrackable.State.UNTRACKABLE, 1, var2, var1.length, var3);
      if (var1 == null) {
         throw new NullPointerException("dataArray is null");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            if (var1[var4] == null) {
               throw new NullPointerException("dataArray[" + var4 + "] is null");
            }

            if (var2 + var3[var4] > var1[var4].length) {
               throw new IllegalArgumentException("Length of dataArray[" + var4 + "] is less than size+offsets[" + var4 + "].");
            }
         }

         this.bankdata = (short[][])((short[][])var1.clone());
         this.data = this.bankdata[0];
      }
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
      return this.data[var1 + this.offset] & '\uffff';
   }

   public int getElem(int var1, int var2) {
      return this.bankdata[var1][var2 + this.offsets[var1]] & '\uffff';
   }

   public void setElem(int var1, int var2) {
      this.data[var1 + this.offset] = (short)(var2 & '\uffff');
      this.theTrackable.markDirty();
   }

   public void setElem(int var1, int var2, int var3) {
      this.bankdata[var1][var2 + this.offsets[var1]] = (short)(var3 & '\uffff');
      this.theTrackable.markDirty();
   }
}
