package javax.management;

/** @deprecated */
@Deprecated
public class DefaultLoaderRepository {
   public static Class<?> loadClass(String var0) throws ClassNotFoundException {
      return javax.management.loading.DefaultLoaderRepository.loadClass(var0);
   }

   public static Class<?> loadClassWithout(ClassLoader var0, String var1) throws ClassNotFoundException {
      return javax.management.loading.DefaultLoaderRepository.loadClassWithout(var0, var1);
   }
}
