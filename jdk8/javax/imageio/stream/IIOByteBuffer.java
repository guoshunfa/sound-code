package javax.imageio.stream;

public class IIOByteBuffer {
   private byte[] data;
   private int offset;
   private int length;

   public IIOByteBuffer(byte[] var1, int var2, int var3) {
      this.data = var1;
      this.offset = var2;
      this.length = var3;
   }

   public byte[] getData() {
      return this.data;
   }

   public void setData(byte[] var1) {
      this.data = var1;
   }

   public int getOffset() {
      return this.offset;
   }

   public void setOffset(int var1) {
      this.offset = var1;
   }

   public int getLength() {
      return this.length;
   }

   public void setLength(int var1) {
      this.length = var1;
   }
}
