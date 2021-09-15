package javax.swing;

public class SizeSequence {
   private static int[] emptyArray = new int[0];
   private int[] a;

   public SizeSequence() {
      this.a = emptyArray;
   }

   public SizeSequence(int var1) {
      this(var1, 0);
   }

   public SizeSequence(int var1, int var2) {
      this();
      this.insertEntries(0, var1, var2);
   }

   public SizeSequence(int[] var1) {
      this();
      this.setSizes(var1);
   }

   void setSizes(int var1, int var2) {
      if (this.a.length != var1) {
         this.a = new int[var1];
      }

      this.setSizes(0, var1, var2);
   }

   private int setSizes(int var1, int var2, int var3) {
      if (var2 <= var1) {
         return 0;
      } else {
         int var4 = (var1 + var2) / 2;
         this.a[var4] = var3 + this.setSizes(var1, var4, var3);
         return this.a[var4] + this.setSizes(var4 + 1, var2, var3);
      }
   }

   public void setSizes(int[] var1) {
      if (this.a.length != var1.length) {
         this.a = new int[var1.length];
      }

      this.setSizes(0, this.a.length, var1);
   }

   private int setSizes(int var1, int var2, int[] var3) {
      if (var2 <= var1) {
         return 0;
      } else {
         int var4 = (var1 + var2) / 2;
         this.a[var4] = var3[var4] + this.setSizes(var1, var4, var3);
         return this.a[var4] + this.setSizes(var4 + 1, var2, var3);
      }
   }

   public int[] getSizes() {
      int var1 = this.a.length;
      int[] var2 = new int[var1];
      this.getSizes(0, var1, var2);
      return var2;
   }

   private int getSizes(int var1, int var2, int[] var3) {
      if (var2 <= var1) {
         return 0;
      } else {
         int var4 = (var1 + var2) / 2;
         var3[var4] = this.a[var4] - this.getSizes(var1, var4, var3);
         return this.a[var4] + this.getSizes(var4 + 1, var2, var3);
      }
   }

   public int getPosition(int var1) {
      return this.getPosition(0, this.a.length, var1);
   }

   private int getPosition(int var1, int var2, int var3) {
      if (var2 <= var1) {
         return 0;
      } else {
         int var4 = (var1 + var2) / 2;
         return var3 <= var4 ? this.getPosition(var1, var4, var3) : this.a[var4] + this.getPosition(var4 + 1, var2, var3);
      }
   }

   public int getIndex(int var1) {
      return this.getIndex(0, this.a.length, var1);
   }

   private int getIndex(int var1, int var2, int var3) {
      if (var2 <= var1) {
         return var1;
      } else {
         int var4 = (var1 + var2) / 2;
         int var5 = this.a[var4];
         return var3 < var5 ? this.getIndex(var1, var4, var3) : this.getIndex(var4 + 1, var2, var3 - var5);
      }
   }

   public int getSize(int var1) {
      return this.getPosition(var1 + 1) - this.getPosition(var1);
   }

   public void setSize(int var1, int var2) {
      this.changeSize(0, this.a.length, var1, var2 - this.getSize(var1));
   }

   private void changeSize(int var1, int var2, int var3, int var4) {
      if (var2 > var1) {
         int var5 = (var1 + var2) / 2;
         if (var3 <= var5) {
            int[] var10000 = this.a;
            var10000[var5] += var4;
            this.changeSize(var1, var5, var3, var4);
         } else {
            this.changeSize(var5 + 1, var2, var3, var4);
         }

      }
   }

   public void insertEntries(int var1, int var2, int var3) {
      int[] var4 = this.getSizes();
      int var5 = var1 + var2;
      int var6 = this.a.length + var2;
      this.a = new int[var6];

      int var7;
      for(var7 = 0; var7 < var1; ++var7) {
         this.a[var7] = var4[var7];
      }

      for(var7 = var1; var7 < var5; ++var7) {
         this.a[var7] = var3;
      }

      for(var7 = var5; var7 < var6; ++var7) {
         this.a[var7] = var4[var7 - var2];
      }

      this.setSizes(this.a);
   }

   public void removeEntries(int var1, int var2) {
      int[] var3 = this.getSizes();
      int var10000 = var1 + var2;
      int var5 = this.a.length - var2;
      this.a = new int[var5];

      int var6;
      for(var6 = 0; var6 < var1; ++var6) {
         this.a[var6] = var3[var6];
      }

      for(var6 = var1; var6 < var5; ++var6) {
         this.a[var6] = var3[var6 + var2];
      }

      this.setSizes(this.a);
   }
}
