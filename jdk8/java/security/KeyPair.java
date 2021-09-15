package java.security;

import java.io.Serializable;

public final class KeyPair implements Serializable {
   private static final long serialVersionUID = -7565189502268009837L;
   private PrivateKey privateKey;
   private PublicKey publicKey;

   public KeyPair(PublicKey var1, PrivateKey var2) {
      this.publicKey = var1;
      this.privateKey = var2;
   }

   public PublicKey getPublic() {
      return this.publicKey;
   }

   public PrivateKey getPrivate() {
      return this.privateKey;
   }
}
