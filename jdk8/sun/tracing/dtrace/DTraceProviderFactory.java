package sun.tracing.dtrace;

import com.sun.tracing.Provider;
import com.sun.tracing.ProviderFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class DTraceProviderFactory extends ProviderFactory {
   public <T extends Provider> T createProvider(Class<T> var1) {
      DTraceProvider var2 = new DTraceProvider(var1);
      Provider var3 = var2.newProxyInstance();
      var2.setProxy(var3);
      var2.init();
      new Activation(var2.getModuleName(), new DTraceProvider[]{var2});
      return var3;
   }

   public Map<Class<? extends Provider>, Provider> createProviders(Set<Class<? extends Provider>> var1, String var2) {
      HashMap var3 = new HashMap();
      HashSet var4 = new HashSet();
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         Class var6 = (Class)var5.next();
         DTraceProvider var7 = new DTraceProvider(var6);
         var4.add(var7);
         var3.put(var6, var7.newProxyInstance());
      }

      new Activation(var2, (DTraceProvider[])var4.toArray(new DTraceProvider[0]));
      return var3;
   }

   public static boolean isSupported() {
      try {
         SecurityManager var0 = System.getSecurityManager();
         if (var0 != null) {
            RuntimePermission var1 = new RuntimePermission("com.sun.tracing.dtrace.createProvider");
            var0.checkPermission(var1);
         }

         return JVM.isSupported();
      } catch (SecurityException var2) {
         return false;
      }
   }
}
