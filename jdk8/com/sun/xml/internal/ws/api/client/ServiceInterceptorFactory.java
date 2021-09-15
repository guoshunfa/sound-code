package com.sun.xml.internal.ws.api.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class ServiceInterceptorFactory {
   private static ThreadLocal<Set<ServiceInterceptorFactory>> threadLocalFactories = new ThreadLocal<Set<ServiceInterceptorFactory>>() {
      protected Set<ServiceInterceptorFactory> initialValue() {
         return new HashSet();
      }
   };

   public abstract ServiceInterceptor create(@NotNull WSService var1);

   @NotNull
   public static ServiceInterceptor load(@NotNull WSService service, @Nullable ClassLoader cl) {
      List<ServiceInterceptor> l = new ArrayList();
      Iterator var3 = ServiceFinder.find(ServiceInterceptorFactory.class).iterator();

      ServiceInterceptorFactory f;
      while(var3.hasNext()) {
         f = (ServiceInterceptorFactory)var3.next();
         l.add(f.create(service));
      }

      var3 = ((Set)threadLocalFactories.get()).iterator();

      while(var3.hasNext()) {
         f = (ServiceInterceptorFactory)var3.next();
         l.add(f.create(service));
      }

      return ServiceInterceptor.aggregate((ServiceInterceptor[])l.toArray(new ServiceInterceptor[l.size()]));
   }

   public static boolean registerForThread(ServiceInterceptorFactory factory) {
      return ((Set)threadLocalFactories.get()).add(factory);
   }

   public static boolean unregisterForThread(ServiceInterceptorFactory factory) {
      return ((Set)threadLocalFactories.get()).remove(factory);
   }
}
