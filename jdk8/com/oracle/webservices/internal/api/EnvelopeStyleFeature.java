package com.oracle.webservices.internal.api;

import javax.xml.ws.WebServiceFeature;

public class EnvelopeStyleFeature extends WebServiceFeature {
   private EnvelopeStyle.Style[] styles;

   public EnvelopeStyleFeature(EnvelopeStyle.Style... s) {
      this.styles = s;
   }

   public EnvelopeStyle.Style[] getStyles() {
      return this.styles;
   }

   public String getID() {
      return EnvelopeStyleFeature.class.getName();
   }
}
