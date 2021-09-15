package java.security.spec;

public final class DSAGenParameterSpec implements AlgorithmParameterSpec {
   private final int pLen;
   private final int qLen;
   private final int seedLen;

   public DSAGenParameterSpec(int var1, int var2) {
      this(var1, var2, var2);
   }

   public DSAGenParameterSpec(int var1, int var2, int var3) {
      switch(var1) {
      case 1024:
         if (var2 != 160) {
            throw new IllegalArgumentException("subprimeQLen must be 160 when primePLen=1024");
         }
         break;
      case 2048:
         if (var2 != 224 && var2 != 256) {
            throw new IllegalArgumentException("subprimeQLen must be 224 or 256 when primePLen=2048");
         }
         break;
      case 3072:
         if (var2 != 256) {
            throw new IllegalArgumentException("subprimeQLen must be 256 when primePLen=3072");
         }
         break;
      default:
         throw new IllegalArgumentException("primePLen must be 1024, 2048, or 3072");
      }

      if (var3 < var2) {
         throw new IllegalArgumentException("seedLen must be equal to or greater than subprimeQLen");
      } else {
         this.pLen = var1;
         this.qLen = var2;
         this.seedLen = var3;
      }
   }

   public int getPrimePLength() {
      return this.pLen;
   }

   public int getSubprimeQLength() {
      return this.qLen;
   }

   public int getSeedLength() {
      return this.seedLen;
   }
}
