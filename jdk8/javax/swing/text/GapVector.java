package javax.swing.text;

import java.io.Serializable;

abstract class GapVector implements Serializable {
   private Object array;
   private int g0;
   private int g1;

   public GapVector() {
      this(10);
   }

   public GapVector(int var1) {
      this.array = this.allocateArray(var1);
      this.g0 = 0;
      this.g1 = var1;
   }

   protected abstract Object allocateArray(int var1);

   protected abstract int getArrayLength();

   protected final Object getArray() {
      return this.array;
   }

   protected final int getGapStart() {
      return this.g0;
   }

   protected final int getGapEnd() {
      return this.g1;
   }

   protected void replace(int var1, int var2, Object var3, int var4) {
      byte var5 = 0;
      if (var4 == 0) {
         this.close(var1, var2);
      } else {
         if (var2 > var4) {
            this.close(var1 + var4, var2 - var4);
         } else {
            int var6 = var4 - var2;
            int var7 = this.open(var1 + var2, var6);
            System.arraycopy(var3, var2, this.array, var7, var6);
            var4 = var2;
         }

         System.arraycopy(var3, var5, this.array, var1, var4);
      }
   }

   void close(int var1, int var2) {
      if (var2 != 0) {
         int var3 = var1 + var2;
         int var4 = this.g1 - this.g0 + var2;
         if (var3 <= this.g0) {
            if (this.g0 != var3) {
               this.shiftGap(var3);
            }

            this.shiftGapStartDown(this.g0 - var2);
         } else if (var1 >= this.g0) {
            if (this.g0 != var1) {
               this.shiftGap(var1);
            }

            this.shiftGapEndUp(this.g0 + var4);
         } else {
            this.shiftGapStartDown(var1);
            this.shiftGapEndUp(this.g0 + var4);
         }

      }
   }

   int open(int var1, int var2) {
      int var3 = this.g1 - this.g0;
      if (var2 == 0) {
         if (var1 > this.g0) {
            var1 += var3;
         }

         return var1;
      } else {
         this.shiftGap(var1);
         if (var2 >= var3) {
            this.shiftEnd(this.getArrayLength() - var3 + var2);
            int var10000 = this.g1 - this.g0;
         }

         this.g0 += var2;
         return var1;
      }
   }

   void resize(int var1) {
      Object var2 = this.allocateArray(var1);
      System.arraycopy(this.array, 0, var2, 0, Math.min(var1, this.getArrayLength()));
      this.array = var2;
   }

   protected void shiftEnd(int var1) {
      int var2 = this.getArrayLength();
      int var3 = this.g1;
      int var4 = var2 - var3;
      int var5 = this.getNewArraySize(var1);
      int var6 = var5 - var4;
      this.resize(var5);
      this.g1 = var6;
      if (var4 != 0) {
         System.arraycopy(this.array, var3, this.array, var6, var4);
      }

   }

   int getNewArraySize(int var1) {
      return (var1 + 1) * 2;
   }

   protected void shiftGap(int var1) {
      if (var1 != this.g0) {
         int var2 = this.g0;
         int var3 = var1 - var2;
         int var4 = this.g1;
         int var5 = var4 + var3;
         int var10000 = var4 - var2;
         this.g0 = var1;
         this.g1 = var5;
         if (var3 > 0) {
            System.arraycopy(this.array, var4, this.array, var2, var3);
         } else if (var3 < 0) {
            System.arraycopy(this.array, var1, this.array, var5, -var3);
         }

      }
   }

   protected void shiftGapStartDown(int var1) {
      this.g0 = var1;
   }

   protected void shiftGapEndUp(int var1) {
      this.g1 = var1;
   }
}
