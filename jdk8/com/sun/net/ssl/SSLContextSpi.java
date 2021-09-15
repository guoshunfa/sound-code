package com.sun.net.ssl;

import java.security.KeyManagementException;
import java.security.SecureRandom;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

/** @deprecated */
@Deprecated
public abstract class SSLContextSpi {
   protected abstract void engineInit(KeyManager[] var1, TrustManager[] var2, SecureRandom var3) throws KeyManagementException;

   protected abstract SSLSocketFactory engineGetSocketFactory();

   protected abstract SSLServerSocketFactory engineGetServerSocketFactory();
}
