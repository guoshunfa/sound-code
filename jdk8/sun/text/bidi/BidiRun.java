package sun.text.bidi;

public class BidiRun {
   int start;
   int limit;
   int insertRemove;
   byte level;

   BidiRun() {
      this(0, 0, (byte)0);
   }

   BidiRun(int var1, int var2, byte var3) {
      this.start = var1;
      this.limit = var2;
      this.level = var3;
   }

   void copyFrom(BidiRun var1) {
      this.start = var1.start;
      this.limit = var1.limit;
      this.level = var1.level;
      this.insertRemove = var1.insertRemove;
   }

   public byte getEmbeddingLevel() {
      return this.level;
   }

   boolean isEvenRun() {
      return (this.level & 1) == 0;
   }
}
