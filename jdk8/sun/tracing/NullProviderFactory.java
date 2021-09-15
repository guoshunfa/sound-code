package sun.tracing;

import com.sun.tracing.Provider;
import com.sun.tracing.ProviderFactory;

public class NullProviderFactory extends ProviderFactory {
   public <T extends Provider> T createProvider(Class<T> var1) {
      NullProvider var2 = new NullProvider(var1);
      var2.init();
      return var2.newProxyInstance();
   }
}
