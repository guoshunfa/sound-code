package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import javax.xml.ws.WebServiceFeature;

public final class SEIPortInfo extends PortInfo {
   public final Class sei;
   public final SOAPSEIModel model;

   public SEIPortInfo(WSServiceDelegate owner, Class sei, SOAPSEIModel model, @NotNull WSDLPort portModel) {
      super(owner, portModel);
      this.sei = sei;
      this.model = model;

      assert sei != null && model != null;
   }

   public BindingImpl createBinding(WebServiceFeature[] webServiceFeatures, Class<?> portInterface) {
      BindingImpl binding = super.createBinding(webServiceFeatures, portInterface);
      return this.setKnownHeaders(binding);
   }

   public BindingImpl createBinding(WebServiceFeatureList webServiceFeatures, Class<?> portInterface) {
      BindingImpl binding = super.createBinding(webServiceFeatures, portInterface, (BindingImpl)null);
      return this.setKnownHeaders(binding);
   }

   private BindingImpl setKnownHeaders(BindingImpl binding) {
      if (binding instanceof SOAPBindingImpl) {
         ((SOAPBindingImpl)binding).setPortKnownHeaders(this.model.getKnownHeaders());
      }

      return binding;
   }
}
