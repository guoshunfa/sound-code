package javax.imageio.spi;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class SubRegistry {
   ServiceRegistry registry;
   Class category;
   final PartiallyOrderedSet poset = new PartiallyOrderedSet();
   final Map<Class<?>, Object> map = new HashMap();
   final Map<Class<?>, AccessControlContext> accMap = new HashMap();

   public SubRegistry(ServiceRegistry var1, Class var2) {
      this.registry = var1;
      this.category = var2;
   }

   public boolean registerServiceProvider(Object var1) {
      Object var2 = this.map.get(var1.getClass());
      boolean var3 = var2 != null;
      if (var3) {
         this.deregisterServiceProvider(var2);
      }

      this.map.put(var1.getClass(), var1);
      this.accMap.put(var1.getClass(), AccessController.getContext());
      this.poset.add(var1);
      if (var1 instanceof RegisterableService) {
         RegisterableService var4 = (RegisterableService)var1;
         var4.onRegistration(this.registry, this.category);
      }

      return !var3;
   }

   public boolean deregisterServiceProvider(Object var1) {
      Object var2 = this.map.get(var1.getClass());
      if (var1 == var2) {
         this.map.remove(var1.getClass());
         this.accMap.remove(var1.getClass());
         this.poset.remove(var1);
         if (var1 instanceof RegisterableService) {
            RegisterableService var3 = (RegisterableService)var1;
            var3.onDeregistration(this.registry, this.category);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean contains(Object var1) {
      Object var2 = this.map.get(var1.getClass());
      return var2 == var1;
   }

   public boolean setOrdering(Object var1, Object var2) {
      return this.poset.setOrdering(var1, var2);
   }

   public boolean unsetOrdering(Object var1, Object var2) {
      return this.poset.unsetOrdering(var1, var2);
   }

   public Iterator getServiceProviders(boolean var1) {
      return var1 ? this.poset.iterator() : this.map.values().iterator();
   }

   public <T> T getServiceProviderByClass(Class<T> var1) {
      return this.map.get(var1);
   }

   public void clear() {
      Iterator var1 = this.map.values().iterator();

      while(true) {
         RegisterableService var3;
         AccessControlContext var4;
         do {
            Object var2;
            do {
               if (!var1.hasNext()) {
                  this.poset.clear();
                  this.accMap.clear();
                  return;
               }

               var2 = var1.next();
               var1.remove();
            } while(!(var2 instanceof RegisterableService));

            var3 = (RegisterableService)var2;
            var4 = (AccessControlContext)this.accMap.get(var2.getClass());
         } while(var4 == null && System.getSecurityManager() != null);

         AccessController.doPrivileged(() -> {
            var3.onDeregistration(this.registry, this.category);
            return null;
         }, var4);
      }
   }

   public void finalize() {
      this.clear();
   }
}
