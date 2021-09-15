package com.sun.xml.internal.ws.binding;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.resources.ClientMessages;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;

public final class SOAPBindingImpl extends BindingImpl implements SOAPBinding {
   public static final String X_SOAP12HTTP_BINDING = "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/";
   private static final String ROLE_NONE = "http://www.w3.org/2003/05/soap-envelope/role/none";
   protected final SOAPVersion soapVersion;
   private Set<QName> portKnownHeaders;
   private Set<QName> bindingUnderstoodHeaders;

   SOAPBindingImpl(BindingID bindingId) {
      this(bindingId, EMPTY_FEATURES);
   }

   SOAPBindingImpl(BindingID bindingId, WebServiceFeature... features) {
      super(bindingId, features);
      this.portKnownHeaders = Collections.emptySet();
      this.bindingUnderstoodHeaders = new HashSet();
      this.soapVersion = bindingId.getSOAPVersion();
      this.setRoles(new HashSet());
      this.features.addAll(bindingId.createBuiltinFeatureList());
   }

   public void setPortKnownHeaders(@NotNull Set<QName> headers) {
      this.portKnownHeaders = headers;
   }

   public boolean understandsHeader(QName header) {
      return this.serviceMode == Service.Mode.MESSAGE || this.portKnownHeaders.contains(header) || this.bindingUnderstoodHeaders.contains(header);
   }

   public void setHandlerChain(List<Handler> chain) {
      this.setHandlerConfig(new HandlerConfiguration(this.getHandlerConfig().getRoles(), chain));
   }

   protected void addRequiredRoles(Set<String> roles) {
      roles.addAll(this.soapVersion.requiredRoles);
   }

   public Set<String> getRoles() {
      return this.getHandlerConfig().getRoles();
   }

   public void setRoles(Set<String> roles) {
      if (roles == null) {
         roles = new HashSet();
      }

      if (((Set)roles).contains("http://www.w3.org/2003/05/soap-envelope/role/none")) {
         throw new WebServiceException(ClientMessages.INVALID_SOAP_ROLE_NONE());
      } else {
         this.addRequiredRoles((Set)roles);
         this.setHandlerConfig(new HandlerConfiguration((Set)roles, this.getHandlerConfig()));
      }
   }

   public boolean isMTOMEnabled() {
      return this.isFeatureEnabled(MTOMFeature.class);
   }

   public void setMTOMEnabled(boolean b) {
      this.features.setMTOMEnabled(b);
   }

   public SOAPFactory getSOAPFactory() {
      return this.soapVersion.getSOAPFactory();
   }

   public MessageFactory getMessageFactory() {
      return this.soapVersion.getMessageFactory();
   }
}
