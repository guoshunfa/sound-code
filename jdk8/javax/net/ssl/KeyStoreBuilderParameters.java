package javax.net.ssl;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class KeyStoreBuilderParameters implements ManagerFactoryParameters {
   private final List<KeyStore.Builder> parameters;

   public KeyStoreBuilderParameters(KeyStore.Builder var1) {
      this.parameters = Collections.singletonList(Objects.requireNonNull(var1));
   }

   public KeyStoreBuilderParameters(List<KeyStore.Builder> var1) {
      if (var1.isEmpty()) {
         throw new IllegalArgumentException();
      } else {
         this.parameters = Collections.unmodifiableList(new ArrayList(var1));
      }
   }

   public List<KeyStore.Builder> getParameters() {
      return this.parameters;
   }
}
