package com.sun.net.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

/** @deprecated */
@Deprecated
public abstract class KeyManagerFactorySpi {
   protected abstract void engineInit(KeyStore var1, char[] var2) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException;

   protected abstract KeyManager[] engineGetKeyManagers();
}
