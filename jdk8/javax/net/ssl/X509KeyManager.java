package javax.net.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public interface X509KeyManager extends KeyManager {
   String[] getClientAliases(String var1, Principal[] var2);

   String chooseClientAlias(String[] var1, Principal[] var2, Socket var3);

   String[] getServerAliases(String var1, Principal[] var2);

   String chooseServerAlias(String var1, Principal[] var2, Socket var3);

   X509Certificate[] getCertificateChain(String var1);

   PrivateKey getPrivateKey(String var1);
}
