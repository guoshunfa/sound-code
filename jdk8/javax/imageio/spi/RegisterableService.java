package javax.imageio.spi;

public interface RegisterableService {
   void onRegistration(ServiceRegistry var1, Class<?> var2);

   void onDeregistration(ServiceRegistry var1, Class<?> var2);
}
