package com.sun.net.ssl;

import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/** @deprecated */
@Deprecated
public interface X509KeyManager extends KeyManager {
   String[] getClientAliases(String var1, Principal[] var2);

   String chooseClientAlias(String var1, Principal[] var2);

   String[] getServerAliases(String var1, Principal[] var2);

   String chooseServerAlias(String var1, Principal[] var2);

   X509Certificate[] getCertificateChain(String var1);

   PrivateKey getPrivateKey(String var1);
}
