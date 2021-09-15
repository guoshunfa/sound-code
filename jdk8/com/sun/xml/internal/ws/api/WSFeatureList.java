package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.ws.WebServiceFeature;

public interface WSFeatureList extends Iterable<WebServiceFeature> {
   boolean isEnabled(@NotNull Class<? extends WebServiceFeature> var1);

   @Nullable
   <F extends WebServiceFeature> F get(@NotNull Class<F> var1);

   @NotNull
   WebServiceFeature[] toArray();

   void mergeFeatures(@NotNull WebServiceFeature[] var1, boolean var2);

   void mergeFeatures(@NotNull Iterable<WebServiceFeature> var1, boolean var2);
}
