package sun.java2d.jules;

public class JulesTile {
   byte[] imgBuffer;
   long pixmanImgPtr = 0L;
   int tilePos;

   public byte[] getImgBuffer() {
      if (this.imgBuffer == null) {
         this.imgBuffer = new byte[1024];
      }

      return this.imgBuffer;
   }

   public long getPixmanImgPtr() {
      return this.pixmanImgPtr;
   }

   public void setPixmanImgPtr(long var1) {
      this.pixmanImgPtr = var1;
   }

   public boolean hasBuffer() {
      return this.imgBuffer != null;
   }

   public int getTilePos() {
      return this.tilePos;
   }

   public void setTilePos(int var1) {
      this.tilePos = var1;
   }

   public void setImgBuffer(byte[] var1) {
      this.imgBuffer = var1;
   }
}
