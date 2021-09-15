package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSFeatureList;
import javax.xml.ws.WebServiceFeature;

public interface WSDLFeaturedObject extends WSDLObject {
   @Nullable
   <F extends WebServiceFeature> F getFeature(@NotNull Class<F> var1);

   @NotNull
   WSFeatureList getFeatures();

   void addFeature(@NotNull WebServiceFeature var1);
}
