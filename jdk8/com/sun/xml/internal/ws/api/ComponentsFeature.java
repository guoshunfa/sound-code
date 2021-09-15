package com.sun.xml.internal.ws.api;

import java.util.List;
import javax.xml.ws.WebServiceFeature;

public class ComponentsFeature extends WebServiceFeature implements ServiceSharedFeatureMarker {
   private final List<ComponentFeature> componentFeatures;

   public ComponentsFeature(List<ComponentFeature> componentFeatures) {
      this.enabled = true;
      this.componentFeatures = componentFeatures;
   }

   public String getID() {
      return ComponentsFeature.class.getName();
   }

   public List<ComponentFeature> getComponentFeatures() {
      return this.componentFeatures;
   }
}
