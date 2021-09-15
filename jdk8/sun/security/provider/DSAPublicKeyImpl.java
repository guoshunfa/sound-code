package sun.security.provider;

import java.io.ObjectStreamException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyRep;

public final class DSAPublicKeyImpl extends DSAPublicKey {
   private static final long serialVersionUID = 7819830118247182730L;

   public DSAPublicKeyImpl(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4) throws InvalidKeyException {
      super(var1, var2, var3, var4);
   }

   public DSAPublicKeyImpl(byte[] var1) throws InvalidKeyException {
      super(var1);
   }

   protected Object writeReplace() throws ObjectStreamException {
      return new KeyRep(KeyRep.Type.PUBLIC, this.getAlgorithm(), this.getFormat(), this.getEncoded());
   }
}
