package sun.tracing;

import com.sun.tracing.Provider;
import com.sun.tracing.ProviderFactory;
import java.io.PrintStream;

public class PrintStreamProviderFactory extends ProviderFactory {
   private PrintStream stream;

   public PrintStreamProviderFactory(PrintStream var1) {
      this.stream = var1;
   }

   public <T extends Provider> T createProvider(Class<T> var1) {
      PrintStreamProvider var2 = new PrintStreamProvider(var1, this.stream);
      var2.init();
      return var2.newProxyInstance();
   }
}
