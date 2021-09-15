package com.sun.xml.internal.ws.api.client;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class SelectOptimalEncodingFeature extends WebServiceFeature {
   public static final String ID = "http://java.sun.com/xml/ns/jaxws/client/selectOptimalEncoding";

   public SelectOptimalEncodingFeature() {
      this.enabled = true;
   }

   @FeatureConstructor({"enabled"})
   public SelectOptimalEncodingFeature(boolean enabled) {
      this.enabled = enabled;
   }

   @ManagedAttribute
   public String getID() {
      return "http://java.sun.com/xml/ns/jaxws/client/selectOptimalEncoding";
   }
}
