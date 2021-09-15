package com.sun.net.ssl;

import java.security.cert.X509Certificate;

/** @deprecated */
@Deprecated
public interface X509TrustManager extends TrustManager {
   boolean isClientTrusted(X509Certificate[] var1);

   boolean isServerTrusted(X509Certificate[] var1);

   X509Certificate[] getAcceptedIssuers();
}
