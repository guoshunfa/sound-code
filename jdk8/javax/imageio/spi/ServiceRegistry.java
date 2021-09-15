package javax.imageio.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

public class ServiceRegistry {
   private Map categoryMap = new HashMap();

   public ServiceRegistry(Iterator<Class<?>> var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("categories == null!");
      } else {
         while(var1.hasNext()) {
            Class var2 = (Class)var1.next();
            SubRegistry var3 = new SubRegistry(this, var2);
            this.categoryMap.put(var2, var3);
         }

      }
   }

   public static <T> Iterator<T> lookupProviders(Class<T> var0, ClassLoader var1) {
      if (var0 == null) {
         throw new IllegalArgumentException("providerClass == null!");
      } else {
         return ServiceLoader.load(var0, var1).iterator();
      }
   }

   public static <T> Iterator<T> lookupProviders(Class<T> var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("providerClass == null!");
      } else {
         return ServiceLoader.load(var0).iterator();
      }
   }

   public Iterator<Class<?>> getCategories() {
      Set var1 = this.categoryMap.keySet();
      return var1.iterator();
   }

   private Iterator getSubRegistries(Object var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = this.categoryMap.keySet().iterator();

      while(var3.hasNext()) {
         Class var4 = (Class)var3.next();
         if (var4.isAssignableFrom(var1.getClass())) {
            var2.add((SubRegistry)this.categoryMap.get(var4));
         }
      }

      return var2.iterator();
   }

   public <T> boolean registerServiceProvider(T var1, Class<T> var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("provider == null!");
      } else {
         SubRegistry var3 = (SubRegistry)this.categoryMap.get(var2);
         if (var3 == null) {
            throw new IllegalArgumentException("category unknown!");
         } else if (!var2.isAssignableFrom(var1.getClass())) {
            throw new ClassCastException();
         } else {
            return var3.registerServiceProvider(var1);
         }
      }
   }

   public void registerServiceProvider(Object var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("provider == null!");
      } else {
         Iterator var2 = this.getSubRegistries(var1);

         while(var2.hasNext()) {
            SubRegistry var3 = (SubRegistry)var2.next();
            var3.registerServiceProvider(var1);
         }

      }
   }

   public void registerServiceProviders(Iterator<?> var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("provider == null!");
      } else {
         while(var1.hasNext()) {
            this.registerServiceProvider(var1.next());
         }

      }
   }

   public <T> boolean deregisterServiceProvider(T var1, Class<T> var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("provider == null!");
      } else {
         SubRegistry var3 = (SubRegistry)this.categoryMap.get(var2);
         if (var3 == null) {
            throw new IllegalArgumentException("category unknown!");
         } else if (!var2.isAssignableFrom(var1.getClass())) {
            throw new ClassCastException();
         } else {
            return var3.deregisterServiceProvider(var1);
         }
      }
   }

   public void deregisterServiceProvider(Object var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("provider == null!");
      } else {
         Iterator var2 = this.getSubRegistries(var1);

         while(var2.hasNext()) {
            SubRegistry var3 = (SubRegistry)var2.next();
            var3.deregisterServiceProvider(var1);
         }

      }
   }

   public boolean contains(Object var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("provider == null!");
      } else {
         Iterator var2 = this.getSubRegistries(var1);

         SubRegistry var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = (SubRegistry)var2.next();
         } while(!var3.contains(var1));

         return true;
      }
   }

   public <T> Iterator<T> getServiceProviders(Class<T> var1, boolean var2) {
      SubRegistry var3 = (SubRegistry)this.categoryMap.get(var1);
      if (var3 == null) {
         throw new IllegalArgumentException("category unknown!");
      } else {
         return var3.getServiceProviders(var2);
      }
   }

   public <T> Iterator<T> getServiceProviders(Class<T> var1, ServiceRegistry.Filter var2, boolean var3) {
      SubRegistry var4 = (SubRegistry)this.categoryMap.get(var1);
      if (var4 == null) {
         throw new IllegalArgumentException("category unknown!");
      } else {
         Iterator var5 = this.getServiceProviders(var1, var3);
         return new FilterIterator(var5, var2);
      }
   }

   public <T> T getServiceProviderByClass(Class<T> var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("providerClass == null!");
      } else {
         Iterator var2 = this.categoryMap.keySet().iterator();

         while(var2.hasNext()) {
            Class var3 = (Class)var2.next();
            if (var3.isAssignableFrom(var1)) {
               SubRegistry var4 = (SubRegistry)this.categoryMap.get(var3);
               Object var5 = var4.getServiceProviderByClass(var1);
               if (var5 != null) {
                  return var5;
               }
            }
         }

         return null;
      }
   }

   public <T> boolean setOrdering(Class<T> var1, T var2, T var3) {
      if (var2 != null && var3 != null) {
         if (var2 == var3) {
            throw new IllegalArgumentException("providers are the same!");
         } else {
            SubRegistry var4 = (SubRegistry)this.categoryMap.get(var1);
            if (var4 == null) {
               throw new IllegalArgumentException("category unknown!");
            } else {
               return var4.contains(var2) && var4.contains(var3) ? var4.setOrdering(var2, var3) : false;
            }
         }
      } else {
         throw new IllegalArgumentException("provider is null!");
      }
   }

   public <T> boolean unsetOrdering(Class<T> var1, T var2, T var3) {
      if (var2 != null && var3 != null) {
         if (var2 == var3) {
            throw new IllegalArgumentException("providers are the same!");
         } else {
            SubRegistry var4 = (SubRegistry)this.categoryMap.get(var1);
            if (var4 == null) {
               throw new IllegalArgumentException("category unknown!");
            } else {
               return var4.contains(var2) && var4.contains(var3) ? var4.unsetOrdering(var2, var3) : false;
            }
         }
      } else {
         throw new IllegalArgumentException("provider is null!");
      }
   }

   public void deregisterAll(Class<?> var1) {
      SubRegistry var2 = (SubRegistry)this.categoryMap.get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("category unknown!");
      } else {
         var2.clear();
      }
   }

   public void deregisterAll() {
      Iterator var1 = this.categoryMap.values().iterator();

      while(var1.hasNext()) {
         SubRegistry var2 = (SubRegistry)var1.next();
         var2.clear();
      }

   }

   public void finalize() throws Throwable {
      this.deregisterAll();
      super.finalize();
   }

   public interface Filter {
      boolean filter(Object var1);
   }
}
