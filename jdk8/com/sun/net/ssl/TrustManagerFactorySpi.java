package com.sun.net.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;

/** @deprecated */
@Deprecated
public abstract class TrustManagerFactorySpi {
   protected abstract void engineInit(KeyStore var1) throws KeyStoreException;

   protected abstract TrustManager[] engineGetTrustManagers();
}
