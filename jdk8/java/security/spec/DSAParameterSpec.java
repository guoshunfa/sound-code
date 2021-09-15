package java.security.spec;

import java.math.BigInteger;
import java.security.interfaces.DSAParams;

public class DSAParameterSpec implements AlgorithmParameterSpec, DSAParams {
   BigInteger p;
   BigInteger q;
   BigInteger g;

   public DSAParameterSpec(BigInteger var1, BigInteger var2, BigInteger var3) {
      this.p = var1;
      this.q = var2;
      this.g = var3;
   }

   public BigInteger getP() {
      return this.p;
   }

   public BigInteger getQ() {
      return this.q;
   }

   public BigInteger getG() {
      return this.g;
   }
}
