package com.sun.xml.internal.ws.developer;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import com.sun.xml.internal.ws.server.DraconianValidationErrorHandler;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class SchemaValidationFeature extends WebServiceFeature {
   public static final String ID = "http://jax-ws.dev.java.net/features/schema-validation";
   private final Class<? extends ValidationErrorHandler> clazz;
   private final boolean inbound;
   private final boolean outbound;

   public SchemaValidationFeature() {
      this(true, true, DraconianValidationErrorHandler.class);
   }

   public SchemaValidationFeature(Class<? extends ValidationErrorHandler> clazz) {
      this(true, true, clazz);
   }

   public SchemaValidationFeature(boolean inbound, boolean outbound) {
      this(inbound, outbound, DraconianValidationErrorHandler.class);
   }

   @FeatureConstructor({"inbound", "outbound", "handler"})
   public SchemaValidationFeature(boolean inbound, boolean outbound, Class<? extends ValidationErrorHandler> clazz) {
      this.enabled = true;
      this.inbound = inbound;
      this.outbound = outbound;
      this.clazz = clazz;
   }

   @ManagedAttribute
   public String getID() {
      return "http://jax-ws.dev.java.net/features/schema-validation";
   }

   @ManagedAttribute
   public Class<? extends ValidationErrorHandler> getErrorHandler() {
      return this.clazz;
   }

   public boolean isInbound() {
      return this.inbound;
   }

   public boolean isOutbound() {
      return this.outbound;
   }
}
