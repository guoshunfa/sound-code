package com.sun.xml.internal.ws.api.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.developer.WSBindingProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.ws.WebServiceFeature;

public abstract class ServiceInterceptor {
   public List<WebServiceFeature> preCreateBinding(@NotNull WSPortInfo port, @Nullable Class<?> serviceEndpointInterface, @NotNull WSFeatureList defaultFeatures) {
      return Collections.emptyList();
   }

   public void postCreateProxy(@NotNull WSBindingProvider bp, @NotNull Class<?> serviceEndpointInterface) {
   }

   public void postCreateDispatch(@NotNull WSBindingProvider bp) {
   }

   public static ServiceInterceptor aggregate(final ServiceInterceptor... interceptors) {
      return interceptors.length == 1 ? interceptors[0] : new ServiceInterceptor() {
         public List<WebServiceFeature> preCreateBinding(@NotNull WSPortInfo port, @Nullable Class<?> portInterface, @NotNull WSFeatureList defaultFeatures) {
            List<WebServiceFeature> r = new ArrayList();
            ServiceInterceptor[] var5 = interceptors;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               ServiceInterceptor si = var5[var7];
               r.addAll(si.preCreateBinding(port, portInterface, defaultFeatures));
            }

            return r;
         }

         public void postCreateProxy(@NotNull WSBindingProvider bp, @NotNull Class<?> serviceEndpointInterface) {
            ServiceInterceptor[] var3 = interceptors;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               ServiceInterceptor si = var3[var5];
               si.postCreateProxy(bp, serviceEndpointInterface);
            }

         }

         public void postCreateDispatch(@NotNull WSBindingProvider bp) {
            ServiceInterceptor[] var2 = interceptors;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               ServiceInterceptor si = var2[var4];
               si.postCreateDispatch(bp);
            }

         }
      };
   }
}
