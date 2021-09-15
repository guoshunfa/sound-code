package sun.java2d.cmm.kcms;

class pelArrayInfo {
   int nPels;
   int nSrc;
   int srcSize;
   int nDest;
   int destSize;

   pelArrayInfo(ICC_Transform var1, int var2, float[] var3, float[] var4) {
      this.nSrc = var1.getNumInComponents();
      this.nDest = var1.getNumOutComponents();
      this.nPels = var2;
      this.srcSize = this.nPels * this.nSrc;
      this.destSize = this.nPels * this.nDest;
      if (this.srcSize > var3.length) {
         throw new IllegalArgumentException("Inconsistent pel structure");
      } else {
         if (var4 != null) {
            this.checkDest(var4.length);
         }

      }
   }

   pelArrayInfo(ICC_Transform var1, short[] var2, short[] var3) {
      this.srcSize = var2.length;
      this.initInfo(var1);
      this.destSize = this.nPels * this.nDest;
      if (var3 != null) {
         this.checkDest(var3.length);
      }

   }

   pelArrayInfo(ICC_Transform var1, byte[] var2, byte[] var3) {
      this.srcSize = var2.length;
      this.initInfo(var1);
      this.destSize = this.nPels * this.nDest;
      if (var3 != null) {
         this.checkDest(var3.length);
      }

   }

   void initInfo(ICC_Transform var1) {
      this.nSrc = var1.getNumInComponents();
      this.nDest = var1.getNumOutComponents();
      this.nPels = this.srcSize / this.nSrc;
      if (this.nPels * this.nSrc != this.srcSize) {
         throw new IllegalArgumentException("Inconsistent pel structure");
      }
   }

   void checkDest(int var1) {
      if (this.destSize > var1) {
         throw new IllegalArgumentException("Inconsistent pel structure");
      }
   }
}
