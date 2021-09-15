package com.sun.xml.internal.ws.api.client;

import javax.xml.ws.WebServiceFeature;

public class ThrowableInPacketCompletionFeature extends WebServiceFeature {
   public ThrowableInPacketCompletionFeature() {
      this.enabled = true;
   }

   public String getID() {
      return ThrowableInPacketCompletionFeature.class.getName();
   }
}
