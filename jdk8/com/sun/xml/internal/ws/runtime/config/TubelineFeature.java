package com.sun.xml.internal.ws.runtime.config;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import java.util.List;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class TubelineFeature extends WebServiceFeature {
   public static final String ID = "com.sun.xml.internal.ws.runtime.config.TubelineFeature";

   @FeatureConstructor({"enabled"})
   public TubelineFeature(boolean enabled) {
      super.enabled = enabled;
   }

   @ManagedAttribute
   public String getID() {
      return "com.sun.xml.internal.ws.runtime.config.TubelineFeature";
   }

   List<String> getTubeFactories() {
      return null;
   }
}
