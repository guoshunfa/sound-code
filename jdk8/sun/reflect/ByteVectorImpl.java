package sun.reflect;

class ByteVectorImpl implements ByteVector {
   private byte[] data;
   private int pos;

   public ByteVectorImpl() {
      this(100);
   }

   public ByteVectorImpl(int var1) {
      this.data = new byte[var1];
      this.pos = -1;
   }

   public int getLength() {
      return this.pos + 1;
   }

   public byte get(int var1) {
      if (var1 >= this.data.length) {
         this.resize(var1);
         this.pos = var1;
      }

      return this.data[var1];
   }

   public void put(int var1, byte var2) {
      if (var1 >= this.data.length) {
         this.resize(var1);
         this.pos = var1;
      }

      this.data[var1] = var2;
   }

   public void add(byte var1) {
      if (++this.pos >= this.data.length) {
         this.resize(this.pos);
      }

      this.data[this.pos] = var1;
   }

   public void trim() {
      if (this.pos != this.data.length - 1) {
         byte[] var1 = new byte[this.pos + 1];
         System.arraycopy(this.data, 0, var1, 0, this.pos + 1);
         this.data = var1;
      }

   }

   public byte[] getData() {
      return this.data;
   }

   private void resize(int var1) {
      if (var1 <= 2 * this.data.length) {
         var1 = 2 * this.data.length;
      }

      byte[] var2 = new byte[var1];
      System.arraycopy(this.data, 0, var2, 0, this.data.length);
      this.data = var2;
   }
}
