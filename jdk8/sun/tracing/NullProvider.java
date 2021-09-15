package sun.tracing;

import com.sun.tracing.Provider;
import java.lang.reflect.Method;

class NullProvider extends ProviderSkeleton {
   NullProvider(Class<? extends Provider> var1) {
      super(var1);
   }

   protected ProbeSkeleton createProbe(Method var1) {
      return new NullProbe(var1.getParameterTypes());
   }
}
