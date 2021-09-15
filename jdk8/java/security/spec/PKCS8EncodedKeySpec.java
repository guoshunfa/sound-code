package java.security.spec;

public class PKCS8EncodedKeySpec extends EncodedKeySpec {
   public PKCS8EncodedKeySpec(byte[] var1) {
      super(var1);
   }

   public byte[] getEncoded() {
      return super.getEncoded();
   }

   public final String getFormat() {
      return "PKCS#8";
   }
}
