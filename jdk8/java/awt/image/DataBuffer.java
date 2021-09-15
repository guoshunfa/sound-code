package java.awt.image;

import sun.awt.image.SunWritableRaster;
import sun.java2d.StateTrackable;
import sun.java2d.StateTrackableDelegate;

public abstract class DataBuffer {
   public static final int TYPE_BYTE = 0;
   public static final int TYPE_USHORT = 1;
   public static final int TYPE_SHORT = 2;
   public static final int TYPE_INT = 3;
   public static final int TYPE_FLOAT = 4;
   public static final int TYPE_DOUBLE = 5;
   public static final int TYPE_UNDEFINED = 32;
   protected int dataType;
   protected int banks;
   protected int offset;
   protected int size;
   protected int[] offsets;
   StateTrackableDelegate theTrackable;
   private static final int[] dataTypeSize = new int[]{8, 16, 16, 32, 32, 64};

   public static int getDataTypeSize(int var0) {
      if (var0 >= 0 && var0 <= 5) {
         return dataTypeSize[var0];
      } else {
         throw new IllegalArgumentException("Unknown data type " + var0);
      }
   }

   protected DataBuffer(int var1, int var2) {
      this(StateTrackable.State.UNTRACKABLE, var1, var2);
   }

   DataBuffer(StateTrackable.State var1, int var2, int var3) {
      this.theTrackable = StateTrackableDelegate.createInstance(var1);
      this.dataType = var2;
      this.banks = 1;
      this.size = var3;
      this.offset = 0;
      this.offsets = new int[1];
   }

   protected DataBuffer(int var1, int var2, int var3) {
      this(StateTrackable.State.UNTRACKABLE, var1, var2, var3);
   }

   DataBuffer(StateTrackable.State var1, int var2, int var3, int var4) {
      this.theTrackable = StateTrackableDelegate.createInstance(var1);
      this.dataType = var2;
      this.banks = var4;
      this.size = var3;
      this.offset = 0;
      this.offsets = new int[this.banks];
   }

   protected DataBuffer(int var1, int var2, int var3, int var4) {
      this(StateTrackable.State.UNTRACKABLE, var1, var2, var3, var4);
   }

   DataBuffer(StateTrackable.State var1, int var2, int var3, int var4, int var5) {
      this.theTrackable = StateTrackableDelegate.createInstance(var1);
      this.dataType = var2;
      this.banks = var4;
      this.size = var3;
      this.offset = var5;
      this.offsets = new int[var4];

      for(int var6 = 0; var6 < var4; ++var6) {
         this.offsets[var6] = var5;
      }

   }

   protected DataBuffer(int var1, int var2, int var3, int[] var4) {
      this(StateTrackable.State.UNTRACKABLE, var1, var2, var3, var4);
   }

   DataBuffer(StateTrackable.State var1, int var2, int var3, int var4, int[] var5) {
      if (var4 != var5.length) {
         throw new ArrayIndexOutOfBoundsException("Number of banks does not match number of bank offsets");
      } else {
         this.theTrackable = StateTrackableDelegate.createInstance(var1);
         this.dataType = var2;
         this.banks = var4;
         this.size = var3;
         this.offset = var5[0];
         this.offsets = (int[])((int[])var5.clone());
      }
   }

   public int getDataType() {
      return this.dataType;
   }

   public int getSize() {
      return this.size;
   }

   public int getOffset() {
      return this.offset;
   }

   public int[] getOffsets() {
      return (int[])((int[])this.offsets.clone());
   }

   public int getNumBanks() {
      return this.banks;
   }

   public int getElem(int var1) {
      return this.getElem(0, var1);
   }

   public abstract int getElem(int var1, int var2);

   public void setElem(int var1, int var2) {
      this.setElem(0, var1, var2);
   }

   public abstract void setElem(int var1, int var2, int var3);

   public float getElemFloat(int var1) {
      return (float)this.getElem(var1);
   }

   public float getElemFloat(int var1, int var2) {
      return (float)this.getElem(var1, var2);
   }

   public void setElemFloat(int var1, float var2) {
      this.setElem(var1, (int)var2);
   }

   public void setElemFloat(int var1, int var2, float var3) {
      this.setElem(var1, var2, (int)var3);
   }

   public double getElemDouble(int var1) {
      return (double)this.getElem(var1);
   }

   public double getElemDouble(int var1, int var2) {
      return (double)this.getElem(var1, var2);
   }

   public void setElemDouble(int var1, double var2) {
      this.setElem(var1, (int)var2);
   }

   public void setElemDouble(int var1, int var2, double var3) {
      this.setElem(var1, var2, (int)var3);
   }

   static int[] toIntArray(Object var0) {
      if (var0 instanceof int[]) {
         return (int[])((int[])var0);
      } else if (var0 == null) {
         return null;
      } else {
         int[] var2;
         int var3;
         if (var0 instanceof short[]) {
            short[] var4 = (short[])((short[])var0);
            var2 = new int[var4.length];

            for(var3 = 0; var3 < var4.length; ++var3) {
               var2[var3] = var4[var3] & '\uffff';
            }

            return var2;
         } else if (!(var0 instanceof byte[])) {
            return null;
         } else {
            byte[] var1 = (byte[])((byte[])var0);
            var2 = new int[var1.length];

            for(var3 = 0; var3 < var1.length; ++var3) {
               var2[var3] = 255 & var1[var3];
            }

            return var2;
         }
      }
   }

   static {
      SunWritableRaster.setDataStealer(new SunWritableRaster.DataStealer() {
         public byte[] getData(DataBufferByte var1, int var2) {
            return var1.bankdata[var2];
         }

         public short[] getData(DataBufferUShort var1, int var2) {
            return var1.bankdata[var2];
         }

         public int[] getData(DataBufferInt var1, int var2) {
            return var1.bankdata[var2];
         }

         public StateTrackableDelegate getTrackable(DataBuffer var1) {
            return var1.theTrackable;
         }

         public void setTrackable(DataBuffer var1, StateTrackableDelegate var2) {
            var1.theTrackable = var2;
         }
      });
   }
}
