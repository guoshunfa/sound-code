package java.awt.image;

public class ShortLookupTable extends LookupTable {
   short[][] data;

   public ShortLookupTable(int var1, short[][] var2) {
      super(var1, var2.length);
      this.numComponents = var2.length;
      this.numEntries = var2[0].length;
      this.data = new short[this.numComponents][];

      for(int var3 = 0; var3 < this.numComponents; ++var3) {
         this.data[var3] = var2[var3];
      }

   }

   public ShortLookupTable(int var1, short[] var2) {
      super(var1, var2.length);
      this.numComponents = 1;
      this.numEntries = var2.length;
      this.data = new short[1][];
      this.data[0] = var2;
   }

   public final short[][] getTable() {
      return this.data;
   }

   public int[] lookupPixel(int[] var1, int[] var2) {
      if (var2 == null) {
         var2 = new int[var1.length];
      }

      int var3;
      int var4;
      if (this.numComponents == 1) {
         for(var3 = 0; var3 < var1.length; ++var3) {
            var4 = (var1[var3] & '\uffff') - this.offset;
            if (var4 < 0) {
               throw new ArrayIndexOutOfBoundsException("src[" + var3 + "]-offset is less than zero");
            }

            var2[var3] = this.data[0][var4];
         }
      } else {
         for(var3 = 0; var3 < var1.length; ++var3) {
            var4 = (var1[var3] & '\uffff') - this.offset;
            if (var4 < 0) {
               throw new ArrayIndexOutOfBoundsException("src[" + var3 + "]-offset is less than zero");
            }

            var2[var3] = this.data[var3][var4];
         }
      }

      return var2;
   }

   public short[] lookupPixel(short[] var1, short[] var2) {
      if (var2 == null) {
         var2 = new short[var1.length];
      }

      int var3;
      int var4;
      if (this.numComponents == 1) {
         for(var3 = 0; var3 < var1.length; ++var3) {
            var4 = (var1[var3] & '\uffff') - this.offset;
            if (var4 < 0) {
               throw new ArrayIndexOutOfBoundsException("src[" + var3 + "]-offset is less than zero");
            }

            var2[var3] = this.data[0][var4];
         }
      } else {
         for(var3 = 0; var3 < var1.length; ++var3) {
            var4 = (var1[var3] & '\uffff') - this.offset;
            if (var4 < 0) {
               throw new ArrayIndexOutOfBoundsException("src[" + var3 + "]-offset is less than zero");
            }

            var2[var3] = this.data[var3][var4];
         }
      }

      return var2;
   }
}
