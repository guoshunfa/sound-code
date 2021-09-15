package java.security.spec;

public abstract class EncodedKeySpec implements KeySpec {
   private byte[] encodedKey;

   public EncodedKeySpec(byte[] var1) {
      this.encodedKey = (byte[])var1.clone();
   }

   public byte[] getEncoded() {
      return (byte[])this.encodedKey.clone();
   }

   public abstract String getFormat();
}
