package sun.util.locale.provider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.spi.LocaleServiceProvider;

public class HostLocaleProviderAdapter extends AuxLocaleProviderAdapter {
   public LocaleProviderAdapter.Type getAdapterType() {
      return LocaleProviderAdapter.Type.HOST;
   }

   protected <P extends LocaleServiceProvider> P findInstalledProvider(Class<P> var1) {
      try {
         Method var2 = HostLocaleProviderAdapterImpl.class.getMethod("get" + var1.getSimpleName(), (Class[])null);
         return (LocaleServiceProvider)var2.invoke((Object)null, (Object[])null);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException var3) {
         LocaleServiceProviderPool.config(HostLocaleProviderAdapter.class, var3.toString());
         return null;
      }
   }
}
