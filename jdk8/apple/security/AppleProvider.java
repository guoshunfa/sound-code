package apple.security;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;

public final class AppleProvider extends Provider {
   private static final String info = "Apple Provider";

   public AppleProvider() {
      super("Apple", 1.8D, "Apple Provider");
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            AppleProvider.this.put("KeyStore.KeychainStore", "apple.security.KeychainStore");
            return null;
         }
      });
   }
}
