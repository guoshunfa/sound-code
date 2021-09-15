package javax.imageio.plugins.jpeg;

import javax.imageio.ImageReadParam;

public class JPEGImageReadParam extends ImageReadParam {
   private JPEGQTable[] qTables = null;
   private JPEGHuffmanTable[] DCHuffmanTables = null;
   private JPEGHuffmanTable[] ACHuffmanTables = null;

   public boolean areTablesSet() {
      return this.qTables != null;
   }

   public void setDecodeTables(JPEGQTable[] var1, JPEGHuffmanTable[] var2, JPEGHuffmanTable[] var3) {
      if (var1 != null && var2 != null && var3 != null && var1.length <= 4 && var2.length <= 4 && var3.length <= 4 && var2.length == var3.length) {
         this.qTables = (JPEGQTable[])((JPEGQTable[])var1.clone());
         this.DCHuffmanTables = (JPEGHuffmanTable[])((JPEGHuffmanTable[])var2.clone());
         this.ACHuffmanTables = (JPEGHuffmanTable[])((JPEGHuffmanTable[])var3.clone());
      } else {
         throw new IllegalArgumentException("Invalid JPEG table arrays");
      }
   }

   public void unsetDecodeTables() {
      this.qTables = null;
      this.DCHuffmanTables = null;
      this.ACHuffmanTables = null;
   }

   public JPEGQTable[] getQTables() {
      return this.qTables != null ? (JPEGQTable[])((JPEGQTable[])this.qTables.clone()) : null;
   }

   public JPEGHuffmanTable[] getDCHuffmanTables() {
      return this.DCHuffmanTables != null ? (JPEGHuffmanTable[])((JPEGHuffmanTable[])this.DCHuffmanTables.clone()) : null;
   }

   public JPEGHuffmanTable[] getACHuffmanTables() {
      return this.ACHuffmanTables != null ? (JPEGHuffmanTable[])((JPEGHuffmanTable[])this.ACHuffmanTables.clone()) : null;
   }
}
