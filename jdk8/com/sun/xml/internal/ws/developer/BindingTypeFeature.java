package com.sun.xml.internal.ws.developer;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public final class BindingTypeFeature extends WebServiceFeature {
   public static final String ID = "http://jax-ws.dev.java.net/features/binding";
   private final String bindingId;

   public BindingTypeFeature(String bindingId) {
      this.bindingId = bindingId;
   }

   @ManagedAttribute
   public String getID() {
      return "http://jax-ws.dev.java.net/features/binding";
   }

   @ManagedAttribute
   public String getBindingId() {
      return this.bindingId;
   }
}
