package sun.security.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.AlgorithmParametersSpi;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import sun.security.util.Debug;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class DSAParameters extends AlgorithmParametersSpi {
   protected BigInteger p;
   protected BigInteger q;
   protected BigInteger g;

   protected void engineInit(AlgorithmParameterSpec var1) throws InvalidParameterSpecException {
      if (!(var1 instanceof DSAParameterSpec)) {
         throw new InvalidParameterSpecException("Inappropriate parameter specification");
      } else {
         this.p = ((DSAParameterSpec)var1).getP();
         this.q = ((DSAParameterSpec)var1).getQ();
         this.g = ((DSAParameterSpec)var1).getG();
      }
   }

   protected void engineInit(byte[] var1) throws IOException {
      DerValue var2 = new DerValue(var1);
      if (var2.tag != 48) {
         throw new IOException("DSA params parsing error");
      } else {
         var2.data.reset();
         this.p = var2.data.getBigInteger();
         this.q = var2.data.getBigInteger();
         this.g = var2.data.getBigInteger();
         if (var2.data.available() != 0) {
            throw new IOException("encoded params have " + var2.data.available() + " extra bytes");
         }
      }
   }

   protected void engineInit(byte[] var1, String var2) throws IOException {
      this.engineInit(var1);
   }

   protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(Class<T> var1) throws InvalidParameterSpecException {
      try {
         Class var2 = Class.forName("java.security.spec.DSAParameterSpec");
         if (var2.isAssignableFrom(var1)) {
            return (AlgorithmParameterSpec)var1.cast(new DSAParameterSpec(this.p, this.q, this.g));
         } else {
            throw new InvalidParameterSpecException("Inappropriate parameter Specification");
         }
      } catch (ClassNotFoundException var3) {
         throw new InvalidParameterSpecException("Unsupported parameter specification: " + var3.getMessage());
      }
   }

   protected byte[] engineGetEncoded() throws IOException {
      DerOutputStream var1 = new DerOutputStream();
      DerOutputStream var2 = new DerOutputStream();
      var2.putInteger(this.p);
      var2.putInteger(this.q);
      var2.putInteger(this.g);
      var1.write((byte)48, (DerOutputStream)var2);
      return var1.toByteArray();
   }

   protected byte[] engineGetEncoded(String var1) throws IOException {
      return this.engineGetEncoded();
   }

   protected String engineToString() {
      return "\n\tp: " + Debug.toHexString(this.p) + "\n\tq: " + Debug.toHexString(this.q) + "\n\tg: " + Debug.toHexString(this.g) + "\n";
   }
}
