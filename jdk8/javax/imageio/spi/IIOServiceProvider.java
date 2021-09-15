package javax.imageio.spi;

import java.util.Locale;

public abstract class IIOServiceProvider implements RegisterableService {
   protected String vendorName;
   protected String version;

   public IIOServiceProvider(String var1, String var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("vendorName == null!");
      } else if (var2 == null) {
         throw new IllegalArgumentException("version == null!");
      } else {
         this.vendorName = var1;
         this.version = var2;
      }
   }

   public IIOServiceProvider() {
   }

   public void onRegistration(ServiceRegistry var1, Class<?> var2) {
   }

   public void onDeregistration(ServiceRegistry var1, Class<?> var2) {
   }

   public String getVendorName() {
      return this.vendorName;
   }

   public String getVersion() {
      return this.version;
   }

   public abstract String getDescription(Locale var1);
}
