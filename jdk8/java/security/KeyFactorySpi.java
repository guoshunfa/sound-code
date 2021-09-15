package java.security;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public abstract class KeyFactorySpi {
   protected abstract PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException;

   protected abstract PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException;

   protected abstract <T extends KeySpec> T engineGetKeySpec(Key var1, Class<T> var2) throws InvalidKeySpecException;

   protected abstract Key engineTranslateKey(Key var1) throws InvalidKeyException;
}
