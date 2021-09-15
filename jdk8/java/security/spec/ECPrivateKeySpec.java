package java.security.spec;

import java.math.BigInteger;

public class ECPrivateKeySpec implements KeySpec {
   private BigInteger s;
   private ECParameterSpec params;

   public ECPrivateKeySpec(BigInteger var1, ECParameterSpec var2) {
      if (var1 == null) {
         throw new NullPointerException("s is null");
      } else if (var2 == null) {
         throw new NullPointerException("params is null");
      } else {
         this.s = var1;
         this.params = var2;
      }
   }

   public BigInteger getS() {
      return this.s;
   }

   public ECParameterSpec getParams() {
      return this.params;
   }
}
