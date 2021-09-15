package java.security;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DomainLoadStoreParameter implements KeyStore.LoadStoreParameter {
   private final URI configuration;
   private final Map<String, KeyStore.ProtectionParameter> protectionParams;

   public DomainLoadStoreParameter(URI var1, Map<String, KeyStore.ProtectionParameter> var2) {
      if (var1 != null && var2 != null) {
         this.configuration = var1;
         this.protectionParams = Collections.unmodifiableMap(new HashMap(var2));
      } else {
         throw new NullPointerException("invalid null input");
      }
   }

   public URI getConfiguration() {
      return this.configuration;
   }

   public Map<String, KeyStore.ProtectionParameter> getProtectionParams() {
      return this.protectionParams;
   }

   public KeyStore.ProtectionParameter getProtectionParameter() {
      return null;
   }
}
