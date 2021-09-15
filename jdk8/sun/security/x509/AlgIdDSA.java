package sun.security.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.ProviderException;
import java.security.interfaces.DSAParams;
import sun.security.util.Debug;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public final class AlgIdDSA extends AlgorithmId implements DSAParams {
   private static final long serialVersionUID = 3437177836797504046L;
   private BigInteger p;
   private BigInteger q;
   private BigInteger g;

   public BigInteger getP() {
      return this.p;
   }

   public BigInteger getQ() {
      return this.q;
   }

   public BigInteger getG() {
      return this.g;
   }

   /** @deprecated */
   @Deprecated
   public AlgIdDSA() {
   }

   AlgIdDSA(DerValue var1) throws IOException {
      super(var1.getOID());
   }

   public AlgIdDSA(byte[] var1) throws IOException {
      super((new DerValue(var1)).getOID());
   }

   public AlgIdDSA(byte[] var1, byte[] var2, byte[] var3) throws IOException {
      this(new BigInteger(1, var1), new BigInteger(1, var2), new BigInteger(1, var3));
   }

   public AlgIdDSA(BigInteger var1, BigInteger var2, BigInteger var3) {
      super(DSA_oid);
      if (var1 != null || var2 != null || var3 != null) {
         if (var1 == null || var2 == null || var3 == null) {
            throw new ProviderException("Invalid parameters for DSS/DSA Algorithm ID");
         }

         try {
            this.p = var1;
            this.q = var2;
            this.g = var3;
            this.initializeParams();
         } catch (IOException var5) {
            throw new ProviderException("Construct DSS/DSA Algorithm ID");
         }
      }

   }

   public String getName() {
      return "DSA";
   }

   private void initializeParams() throws IOException {
      DerOutputStream var1 = new DerOutputStream();
      var1.putInteger(this.p);
      var1.putInteger(this.q);
      var1.putInteger(this.g);
      this.params = new DerValue((byte)48, var1.toByteArray());
   }

   protected void decodeParams() throws IOException {
      if (this.params == null) {
         throw new IOException("DSA alg params are null");
      } else if (this.params.tag != 48) {
         throw new IOException("DSA alg parsing error");
      } else {
         this.params.data.reset();
         this.p = this.params.data.getBigInteger();
         this.q = this.params.data.getBigInteger();
         this.g = this.params.data.getBigInteger();
         if (this.params.data.available() != 0) {
            throw new IOException("AlgIdDSA params, extra=" + this.params.data.available());
         }
      }
   }

   public String toString() {
      return this.paramsToString();
   }

   protected String paramsToString() {
      return this.params == null ? " null\n" : "\n    p:\n" + Debug.toHexString(this.p) + "\n    q:\n" + Debug.toHexString(this.q) + "\n    g:\n" + Debug.toHexString(this.g) + "\n";
   }
}
