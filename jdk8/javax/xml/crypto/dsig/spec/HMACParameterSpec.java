package javax.xml.crypto.dsig.spec;

public final class HMACParameterSpec implements SignatureMethodParameterSpec {
   private int outputLength;

   public HMACParameterSpec(int var1) {
      this.outputLength = var1;
   }

   public int getOutputLength() {
      return this.outputLength;
   }
}
