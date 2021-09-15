package com.sun.xml.internal.ws.developer;

import com.sun.xml.internal.ws.api.FeatureConstructor;
import javax.xml.ws.WebServiceFeature;

public class SerializationFeature extends WebServiceFeature {
   public static final String ID = "http://jax-ws.java.net/features/serialization";
   private final String encoding;

   public SerializationFeature() {
      this("");
   }

   @FeatureConstructor({"encoding"})
   public SerializationFeature(String encoding) {
      this.encoding = encoding;
   }

   public String getID() {
      return "http://jax-ws.java.net/features/serialization";
   }

   public String getEncoding() {
      return this.encoding;
   }
}
