package javax.net.ssl;

import java.security.Principal;

public abstract class X509ExtendedKeyManager implements X509KeyManager {
   protected X509ExtendedKeyManager() {
   }

   public String chooseEngineClientAlias(String[] var1, Principal[] var2, SSLEngine var3) {
      return null;
   }

   public String chooseEngineServerAlias(String var1, Principal[] var2, SSLEngine var3) {
      return null;
   }
}
