package sun.text;

public final class SupplementaryCharacterData implements Cloneable {
   private static final byte IGNORE = -1;
   private int[] dataTable;

   public SupplementaryCharacterData(int[] var1) {
      this.dataTable = var1;
   }

   public int getValue(int var1) {
      assert var1 >= 65536 && var1 <= 1114111 : "Invalid code point:" + Integer.toHexString(var1);

      int var2 = 0;
      int var3 = this.dataTable.length - 1;

      while(true) {
         while(true) {
            int var4 = (var2 + var3) / 2;
            int var5 = this.dataTable[var4] >> 8;
            int var6 = this.dataTable[var4 + 1] >> 8;
            if (var1 < var5) {
               var3 = var4;
            } else {
               if (var1 <= var6 - 1) {
                  int var7 = this.dataTable[var4] & 255;
                  return var7 == 255 ? -1 : var7;
               }

               var2 = var4;
            }
         }
      }
   }

   public int[] getArray() {
      return this.dataTable;
   }
}
