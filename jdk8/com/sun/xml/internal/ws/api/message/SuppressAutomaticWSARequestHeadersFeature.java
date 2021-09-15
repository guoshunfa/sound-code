package com.sun.xml.internal.ws.api.message;

import javax.xml.ws.WebServiceFeature;

public class SuppressAutomaticWSARequestHeadersFeature extends WebServiceFeature {
   public SuppressAutomaticWSARequestHeadersFeature() {
      this.enabled = true;
   }

   public String getID() {
      return SuppressAutomaticWSARequestHeadersFeature.class.toString();
   }
}
