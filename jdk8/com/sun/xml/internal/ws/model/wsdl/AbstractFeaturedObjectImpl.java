package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import java.util.Iterator;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceFeature;

abstract class AbstractFeaturedObjectImpl extends AbstractExtensibleImpl implements WSDLFeaturedObject {
   protected WebServiceFeatureList features;

   protected AbstractFeaturedObjectImpl(XMLStreamReader xsr) {
      super(xsr);
   }

   protected AbstractFeaturedObjectImpl(String systemId, int lineNumber) {
      super(systemId, lineNumber);
   }

   public final void addFeature(WebServiceFeature feature) {
      if (this.features == null) {
         this.features = new WebServiceFeatureList();
      }

      this.features.add(feature);
   }

   @NotNull
   public WebServiceFeatureList getFeatures() {
      return this.features == null ? new WebServiceFeatureList() : this.features;
   }

   public final WebServiceFeature getFeature(String id) {
      if (this.features != null) {
         Iterator var2 = this.features.iterator();

         while(var2.hasNext()) {
            WebServiceFeature f = (WebServiceFeature)var2.next();
            if (f.getID().equals(id)) {
               return f;
            }
         }
      }

      return null;
   }

   @Nullable
   public <F extends WebServiceFeature> F getFeature(@NotNull Class<F> featureType) {
      return this.features == null ? null : this.features.get(featureType);
   }
}
