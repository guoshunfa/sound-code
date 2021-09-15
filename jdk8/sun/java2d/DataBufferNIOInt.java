package sun.java2d;

import java.awt.image.DataBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public final class DataBufferNIOInt extends DataBuffer {
   IntBuffer data;
   IntBuffer[] bankdata;

   public DataBufferNIOInt(int var1) {
      super(3, var1);
      this.data = this.getBufferOfSize(var1 * 4).asIntBuffer();
      this.bankdata = new IntBuffer[1];
      this.bankdata[0] = this.data;
   }

   public IntBuffer getBuffer() {
      return this.data;
   }

   public IntBuffer getBuffer(int var1) {
      return this.bankdata[var1];
   }

   public int[] getData() {
      return this.data.array();
   }

   public int[] getData(int var1) {
      return this.bankdata[var1].array();
   }

   public int[][] getBankData() {
      return (int[][])null;
   }

   public int getElem(int var1) {
      return this.data.get(var1 + this.offset);
   }

   public int getElem(int var1, int var2) {
      return this.bankdata[var1].get(var2 + this.offsets[var1]);
   }

   public void setElem(int var1, int var2) {
      this.data.put(var1 + this.offset, var2);
   }

   public void setElem(int var1, int var2, int var3) {
      this.bankdata[var1].put(var2 + this.offsets[var1], var3);
   }

   ByteBuffer getBufferOfSize(int var1) {
      ByteBuffer var2 = ByteBuffer.allocateDirect(var1);
      var2.order(ByteOrder.nativeOrder());
      return var2;
   }
}
