package java.awt.image;

public abstract class LookupTable {
   int numComponents;
   int offset;
   int numEntries;

   protected LookupTable(int var1, int var2) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Offset must be greater than 0");
      } else if (var2 < 1) {
         throw new IllegalArgumentException("Number of components must  be at least 1");
      } else {
         this.numComponents = var2;
         this.offset = var1;
      }
   }

   public int getNumComponents() {
      return this.numComponents;
   }

   public int getOffset() {
      return this.offset;
   }

   public abstract int[] lookupPixel(int[] var1, int[] var2);
}
