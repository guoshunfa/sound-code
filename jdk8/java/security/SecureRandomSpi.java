package java.security;

import java.io.Serializable;

public abstract class SecureRandomSpi implements Serializable {
   private static final long serialVersionUID = -2991854161009191830L;

   protected abstract void engineSetSeed(byte[] var1);

   protected abstract void engineNextBytes(byte[] var1);

   protected abstract byte[] engineGenerateSeed(int var1);
}
